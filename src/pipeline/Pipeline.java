package pipeline;

import java.util.ArrayList;
import java.util.Properties;

import clustering.ResponsibilitiesClustererOLD;
import model.Project;
import model.causalRelationship.CausalRelationship;
import model.conceptualComponent.ConceptualComponent;
import model.requirement.Requirement;
import model.responsibility.Responsibility;
import model.responsibility.SimpleResponsibility;
import nlp.ConditionalStructureAnalyzer;
import nlp.NLPConnector;
import nlp.StanfordExtractor;
import clustering.wordnet.SynonymsOverResponsibilities;

public class Pipeline {

	// Variables
	private String nlpIP;
	private int nlpPort;
	private int nlpThreads;


	// Constructors

	public Pipeline(){

		// Valores por defecto para usarlo con el servidor del ISISTAN
		this.nlpIP = "7.7.115.232";
		this.nlpPort = 9000;
		this.nlpThreads = 4;

	}

	// Getters and Setters

	public String getNlpIP() {
		return nlpIP;
	}

	public void setNlpIP(String nlpIP) {
		this.nlpIP = nlpIP;
	}

	public int getNlpPort() {
		return nlpPort;
	}

	public void setNlpPort(int nlpPort) {
		this.nlpPort = nlpPort;
	}

	public int getNlpThreads() {
		return nlpThreads;
	}

	public void setNlpThreads(int nlpThreads) {
		this.nlpThreads = nlpThreads;
	}

	// Methods

	private void preProcess(ArrayList<Requirement> requirements){

		System.out.println("** PreProcess  **");

		for (Requirement requirement : requirements){

			// Separación de coma y puntos

			String inputText = requirement.getText();
			inputText = inputText.replaceAll("\\,", " , ");
			inputText = inputText.replaceAll("\\.", " . ");
			inputText = inputText.replaceAll("(\\s)+", " ");

			requirement.setText(inputText);

		}
	}


	private void responsibilityExtraction(ArrayList<Requirement> requirements){

		System.out.println("** Identification of Responsibilities **");

		// Properties to access annotator
		Properties extractorProperties = new Properties();
		//extractorProperties.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse, depparse, dcoref, natlog, openie");
		extractorProperties.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse, depparse, dcoref");
		NLPConnector nlpConnector = new NLPConnector(extractorProperties, this.nlpIP, this.nlpPort, this.nlpThreads);
		StanfordExtractor extractor = new StanfordExtractor(nlpConnector);		

		int id = 1;
		for (Requirement requirement : requirements){

			System.out.println("\nReq - [" + id++ + "/" + requirements.size() + "]");
			System.out.println("\tRequirement [" + requirement.getId() + "] = [" + requirement.getText() + "]");

			// Identificación de Responsabilidades
			ArrayList<Responsibility> responsibilities = extractor.extractResponsibilitiesFromRequirement(requirement);

			System.out.println("\tResponsibilities: [" + responsibilities.size() + "]");
			for (Responsibility responsibility : responsibilities){
				System.out.println("\t\t" + responsibility);
			}

			requirement.addAllResponsabilities(responsibilities);

		}		

	}

	private ArrayList<CausalRelationship> responsibilitySequencing(ArrayList<Requirement> requirements){

		System.out.println("\n\n** Sequencing of Responsibilities  **");

		ArrayList<CausalRelationship> out = new ArrayList<CausalRelationship>();
		Integer cantidad = 0;
		Integer total = 0;

		// Properties to access annotator
		Properties extractorProperties = new Properties();
		extractorProperties.setProperty("annotators", "tokenize, ssplit");
		NLPConnector nlpConnector = new NLPConnector(extractorProperties, this.nlpIP, this.nlpPort, this.nlpThreads);
		StanfordExtractor extractor = new StanfordExtractor(nlpConnector);		

		int id = 1;
		for (Requirement requirement : requirements){

			System.out.println("\nReq -  [" + id++ + "/" + requirements.size() + "]");
			System.out.println("\tRequirement [" + requirement.getId() + "] = [" + requirement.getText() + "]");
			Integer startPOS = 1;
			
			if (!requirement.getResponsibilities().isEmpty()){

				// Separar por sentencias, y ahi aplicar el splitting por espacios
				ArrayList<String> sentences = extractor.getSentences(requirement.getText());

				for (String sentence : sentences){
					String[] split = sentence.split(" ");
					ArrayList<String> words = new ArrayList<String>();
					for (String s : split){
						words.add(s);
					}

					ConditionalStructureAnalyzer sequencializer = new ConditionalStructureAnalyzer();
					if (id == 15)
							System.out.println();
					
					ArrayList<CausalRelationship> detecciones = new ArrayList<CausalRelationship>();
					detecciones.addAll(sequencializer.detectIFTHEN(words , requirement.getResponsibilities(), startPOS));
					detecciones.addAll(sequencializer.detectTEMPORAL(words, requirement.getResponsibilities(), startPOS));

					cantidad = sequencializer.getCantidadAFTERBEFORE() + sequencializer.getCantidadIFTHEN();
					total += cantidad;
					startPOS = words.size() + 1;
					
					System.out.println("\t [" + sentence + "]");
					System.out.println("\t\t Cantidad de Sentencias Condicionales: [" + cantidad + "]\n");

					if (!detecciones.isEmpty()){
						System.out.println("\tCausal Relationships\n\t\t" + detecciones);
						out.addAll(detecciones);
					}
				}
			}
		}

		System.out.println("\n\nCantidad de sentencias condicionales en total: [" + total +"]");
		System.out.println("\n\nCantidad de relaciones causales detectadas en total: [" + out.size() +"]");

		return out;
	}

	private ArrayList<ConceptualComponent> responsibilityClustering(ArrayList<Requirement> requirements){

		System.out.println("\n\n** Allocation of Responsibilities into Conceptual Components **");


		ArrayList<ConceptualComponent> out = new ArrayList<ConceptualComponent>();
		ArrayList<Responsibility> responsibilities = new ArrayList<Responsibility>();

		// Junto todas las responsabilidades
		for (Requirement requirement : requirements){
			responsibilities.addAll(requirement.getResponsibilities());
		}

		// Se tratan las responsabilidades antes de clusterizar

		String filterArguments = "weka.filters.unsupervised.attribute.StringToWordVector -R first-last -W 100000 -prune-rate -1.0 -C -T -I -N 1 -L -stemmer weka.core.stemmers.NullStemmer -stopwords-handler weka.core.stopwords.Null -M 1 -tokenizer \"weka.core.tokenizers.WordTokenizer -delimiters \\\" \\\\r\\\\n\\\\t.,;:\\\\\\\'\\\\\\\"()?!\\\"\"";
		ResponsibilitiesClustererOLD clusterer1 = new ResponsibilitiesClustererOLD("weka.clusterers.XMeans -I 1 -M 1000 -J 1000 -L 2 -H 4 -B 1.0 -C 0.5 -D \"weka.core.EuclideanDistance -R first-last\" -S 10",filterArguments,true);

		// Hacer preprocesamiento de las responsabilidades

		SynonymsOverResponsibilities searcherSynonyms = new SynonymsOverResponsibilities() ;
		ArrayList<Responsibility> auxiliaryResponsibilities = new ArrayList<Responsibility>();
		auxiliaryResponsibilities = searcherSynonyms.performWSDOverResponsibilities(responsibilities);

		//System.out.println("Número de cambios hechos en WSD: ["+ searcherSynonyms.getNumberOfChanges() +"]");

		// Clusterizar
		out = clusterer1.createDataAndClusterResponsibilities(auxiliaryResponsibilities);

		return out;
	}


	// Método principal de la clase Pipeline
	public void run(Project project){

		System.out.println("Project Name: [" + project.getProjectName() + "]\n");

		// PreProcess
		preProcess(project.getRequirements());

		ArrayList<Requirement> requirementsToProcess = project.getRequirements();
		System.out.println("  + # Requirements [" + requirementsToProcess.size() + "]\n");

		if (! requirementsToProcess.isEmpty()){			

			// Responsability Extraction
			responsibilityExtraction(requirementsToProcess);

			// Responsibility Sequencing
			responsibilitySequencing(requirementsToProcess);

			// Clustering
			//responsibilityClustering(requirementsToProcess);

		}


	}


}
