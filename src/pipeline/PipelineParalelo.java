package pipeline;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.swing.JTextPane;

import app.appMainWindow;
import clustering.DummyResponsibilitiesClusterer;
import clustering.ResponsibilitiesClusterer;
import clustering.ResponsibilitiesClustererOLD;
import model.Project;
import model.causalRelationship.CausalRelationship;
import model.conceptualComponent.ConceptualComponent;
import model.exporters.jucm.JUCMExporter;
import model.requirement.Requirement;
import model.responsibility.Responsibility;
import model.responsibility.SimpleResponsibility;
import model.ucm.UseCaseMap;
import nlp.ConditionalStructureAnalyzer;
import nlp.NLPConnector;
import nlp.StanfordExtractor;
import nlp.sequencializers.SeqAnalyzer;
import nlp.sequencializers.SeqAnalyzerBetweenSentences;
import nlp.sequencializers.SeqAnalyzer_IFTHEN;
import nlp.sequencializers.SeqAnalyzer_SIMPLE;
import nlp.sequencializers.SeqAnalyzer_TEMPORAL;
import nlp.sequencializers.SeqAnalyzer_TEMPORALINVERTED;
import utils.misc.Pair;
import clustering.wordnet.SynonymsOverResponsibilities;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.util.CoreMap;

public class PipelineParalelo implements Runnable{

	// Variables

	private String nlpIP;
	private int nlpPort;
	private int nlpThreads;
	private Project project;
	private ArrayList<Requirement> requirementsToProcess;
	private appMainWindow appMainWindow;
	public enum TOPROCESS {STAGE1, STAGE2, STAGE3, STAGE4}
	private TOPROCESS type = TOPROCESS.STAGE1;
	private ArrayList<ConceptualComponent> resultsClustering;
	private ArrayList<CausalRelationship> resultsSequencing;
	private UseCaseMap resultUCM;

	// Constructors

	public PipelineParalelo(Project project, appMainWindow appMainWindow){

		// Default values for use with the ISISTAN server
		this.nlpIP = "http://10.1.4.166";
		this.nlpPort = 9000;
		this.nlpThreads = 4;

		this.project = project;
		this.appMainWindow = appMainWindow;

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

	public TOPROCESS getType() {
		return type;
	}

	public void setType(TOPROCESS type) {
		this.type = type;
	}

	// Methods

	private void preProcess(ArrayList<Requirement> requirements){

		System.out.println("** PreProcess  **");

		for (Requirement requirement : requirements){

			// Comma and point separation

			String inputText = requirement.getText();
			inputText = inputText.replaceAll("\\,", " , ");
			inputText = inputText.replaceAll("\\.", " . ");
			inputText = inputText.replaceAll("(\\s)+", " ");

			requirement.setText(inputText);

		}
	}

	public void firstStep(){

		System.out.println("Project Name: [" + project.getProjectName() + "]\n");

		// PreProcess
		preProcess(project.getRequirements());

		requirementsToProcess = project.getRequirements();
		System.out.println("  + # Requirements [" + requirementsToProcess.size() + "]\n");

	}

	public String testResponsabilityExtraction() {
		return this.responsibilityExtraction(project.getRequirements());
	}

	public ArrayList<CausalRelationship> testResponsibilitiesSequencing() {
		//return this.responsibilitySequencing(project.getRequirements());
		return this.respSequencing2(project.getRequirements());
	}

	public ArrayList<CausalRelationship> respSequencing2(ArrayList<Requirement> requirements){

		System.out.println("\n\n** Sequencing of Responsibilities  **");

		ArrayList<CausalRelationship> out = new ArrayList<CausalRelationship>();

		// Properties to access annotator
		Properties extractorProperties = new Properties();
		extractorProperties.setProperty("annotators", "tokenize, ssplit");
		NLPConnector nlpConnector = new NLPConnector(extractorProperties, this.nlpIP, this.nlpPort, this.nlpThreads);

		// Analizers
		SeqAnalyzer seq1 = new SeqAnalyzer_IFTHEN();
		SeqAnalyzer seq2 = new SeqAnalyzer_TEMPORAL();
		SeqAnalyzer seq3 = new SeqAnalyzer_TEMPORALINVERTED();
		SeqAnalyzer seq4 = new SeqAnalyzer_SIMPLE();
		SeqAnalyzerBetweenSentences seqBS = new SeqAnalyzerBetweenSentences();

		int id = 1;
		for (Requirement requirement : requirements){

			System.out.println("\nReq -  [" + id++ + "/" + requirements.size() + "]");
			System.out.println("\tRequirement [" + requirement.getId() + "] = [" + requirement.getText() + "]");

			// We verify that there are responsibilities associated with the requirement
			if ( ! requirement.getResponsibilities().isEmpty() ){
				System.out.println("\tTIENE RESPONSABILIDADES");

				// We obtain the coreMap
				List<CoreMap> coreMaps = nlpConnector.annotate(requirement.getText());

				//Arraylists for later
				ArrayList<List<CoreLabel>> tokensInSentences = new ArrayList<List<CoreLabel>>();
				ArrayList<ArrayList<Responsibility>> responsibilitiesInSentences = new ArrayList<ArrayList<Responsibility>>();
				ArrayList<ArrayList<CausalRelationship>> relationsInSentences = new ArrayList<ArrayList<CausalRelationship>>();

				// We go sentence by sentence analyzing
				int sentenceNumber = 1;
				for (CoreMap coreMap : coreMaps) {

					// Sentence
					String sentence = coreMap.get(TextAnnotation.class);

					// Token Notation
					List<CoreLabel> tokens = coreMap.get(edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation.class);
					tokensInSentences.add(tokens);

					System.out.println("\t\t+ Sentence [" + sentenceNumber + "] = [" + sentence + "]");
					System.out.println("\t\t+ Tokens    [" + sentenceNumber + "] = [" + tokens + "]");

					ArrayList<CausalRelationship> out_aux_requirement = new ArrayList<CausalRelationship>();
					ArrayList<Responsibility> responsibilitiesInSentence = requirement.getResponsibilities(sentenceNumber);
					responsibilitiesInSentences.add(responsibilitiesInSentence);

					ArrayList<CausalRelationship> out_seq1 = seq1.detectSeq(sentenceNumber, sentence, coreMap, tokens, responsibilitiesInSentence);
					out_aux_requirement.addAll(out_seq1);
					ArrayList<CausalRelationship> out_seq2 = seq2.detectSeq(sentenceNumber, sentence, coreMap, tokens, responsibilitiesInSentence);
					out_aux_requirement.addAll(out_seq2);
					ArrayList<CausalRelationship> out_seq3 = seq3.detectSeq(sentenceNumber, sentence, coreMap, tokens, responsibilitiesInSentence);
					out_aux_requirement.addAll(out_seq3);
					ArrayList<CausalRelationship> out_seq4 = seq4.detectSeq(sentenceNumber, sentence, coreMap, tokens, responsibilitiesInSentence);
					out_aux_requirement.addAll(out_seq4);

					// Modifications are made (eliminate repeated, and unnecessary relationships)
					out_aux_requirement = SeqAnalyzer.eliminateRepetitions(out_aux_requirement);
					out_aux_requirement = SeqAnalyzer.eliminateJumperRelations(out_aux_requirement);
					requirement.getCausalRelationships().addAll(out_aux_requirement);
					//requirement.setCausalRelationships(out_aux_requirement);

					relationsInSentences.add(out_aux_requirement);

					// The necessary values are updated
					sentenceNumber++;
				}

				// CausalRelationshipsBetweenSentences!!
				ArrayList<CausalRelationship> out_seqBS = seqBS.detectSeq(tokensInSentences, responsibilitiesInSentences, relationsInSentences);
				requirement.getCausalRelationships().addAll(out_seqBS);

			}

			Integer i = 1;
			for (CausalRelationship cr : requirement.getCausalRelationships()) {
				System.out.println("\t\t+ Causal Relationship [" + i++ + "/" + requirement.getCausalRelationships().size() + "] = [" + cr.toString() + "]");
				if (! cr.getResp1().getNegated() && ! cr.getResp2().getNegated() )
					out.add(cr);
			}

			if (appMainWindow != null)
				appMainWindow.appendRelationsToView(requirement, requirement.getCausalRelationships());

		}

		//Here we have to generate the PATHS

		out = generateExecutionPaths(project.getRequirements(), out);

		return out;
	}


	private ArrayList<CausalRelationship> generateExecutionPaths(ArrayList<Requirement> requirements, ArrayList<CausalRelationship> outAux) {

		ArrayList<CausalRelationship> out = new ArrayList<CausalRelationship>();

		ArrayList<Responsibility> resps = new ArrayList<Responsibility>();
		for (Requirement requirement : requirements){
			if (!requirement.getResponsibilities().isEmpty()){
				resps.addAll(requirement.getResponsibilities());
			}
		}

		ArrayList<Pair<Responsibility, Responsibility>> similars = new ArrayList<Pair<Responsibility, Responsibility>>();

		for (int i = 0; i < resps.size() - 1 ; i++){
			for (int j = i + 1; j < resps.size(); j++){
				if (resps.get(i).similarTo(resps.get(j))){
					System.out.println("Equals : " + resps.get(i).getId() + " " + resps.get(j).getId());
					// The same ones I keep the IDs
					similars.add(new Pair<Responsibility, Responsibility>(resps.get(i), resps.get(j)));
				}
			}
		}

		// Then look for connections Resp 1 -> Res 2 && Resp 2 -> Resp 3
		// If that is the case and Resp 2 are two different responsibilities considered similar
		// add the missing connection

		ArrayList<CausalRelationship> toAdd = new ArrayList<CausalRelationship>();
		ArrayList<CausalRelationship> toDelete = new ArrayList<CausalRelationship>();
		for (Pair<Responsibility, Responsibility> similar : similars){
			for (int i = 0; i < outAux.size() - 1; i++){
				for (int j = i + 1; j < outAux.size(); j++){
					CausalRelationship cr1 = outAux.get(i);
					CausalRelationship cr2 = outAux.get(j);

					Boolean aux1 = cr1.getResp2().getId().equals(similar.getPair1().getId());
					Boolean aux2 = cr2.getResp1().getId().equals(similar.getPair2().getId());

					if (aux1 && aux2){
						CausalRelationship newCR = new CausalRelationship(cr1.getResp2(), cr2.getResp2());
						toAdd.add(newCR);

						// We must take the bad case
						toDelete.add(cr2);
						resps.remove(cr2.getResp1());
					}
				}
			}
		}

		// I add the connections found
		out.addAll(toAdd);

		// I stop taking into account the relationships that are over
		for (CausalRelationship cr : outAux) {
			Boolean aux = false;
			for (CausalRelationship crToDelete : toDelete) {
				if (cr.equals(crToDelete))
					aux = true;
			}

			if (!aux) {
				out.add(cr);
			}

		}

		// Causal relationships can be made between the responsibilities left alone
		// I have to remove all existing responsibilities from resps
		ArrayList<CausalRelationship> halfRelations = new ArrayList<CausalRelationship>();
		for (Responsibility resp : resps) {
			Boolean esta = false;
			for (CausalRelationship cr : outAux) {
				if (cr.getResp1().equals(resp) || cr.getResp2().equals(resp)) {
					esta = true;
				}
			}
			if (!esta) {
				halfRelations.add(new CausalRelationship(resp, null));
			}
		}

		out.addAll(halfRelations);

		return out;
	}

	private String responsibilityExtraction(ArrayList<Requirement> requirements){

		System.out.println("** Identification of Responsibilities **");
		String out = "** Identification of Responsibilities **\n";

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
			out+= "\nReq - [" + id + "/" + requirements.size() + "]\n";
			out+= "\tRequirement [" + requirement.getId() + "] = [" + requirement.getText() + "]\n";

			// Identification of Responsibilities
			ArrayList<Responsibility> responsibilities = extractor.extractResponsibilitiesFromRequirement(requirement);

			System.out.println("\n\tResponsibilities: [" + responsibilities.size() + "]");
			out+= "\tResponsibilities: [" + responsibilities.size() + "]\n";

			int i = 1;
			for (Responsibility responsibility : responsibilities){

				System.out.println("\t\t" + responsibility);
				out+= "\t\t" + responsibility+"\n";

				responsibility.setId("" + (id-1) + "-" + i++);
			}

			requirement.addAllResponsabilities(responsibilities);

			if (appMainWindow != null)
				appMainWindow.appendResponsibilityToView(requirement);
		}	

		return out;
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

			//TODO see what startPOS was not going well
			Integer startPOS = 1;

			if (!requirement.getResponsibilities().isEmpty()){

				// Separate by sentences, and there apply the splitting by spaces
				ArrayList<String> sentences = extractor.getSentences(requirement.getText());

				for (String sentence : sentences){
					String[] split = sentence.split(" ");
					ArrayList<String> words = new ArrayList<String>();
					for (String s : split){
						words.add(s);
					}

					ConditionalStructureAnalyzer sequencializer = new ConditionalStructureAnalyzer();

					ArrayList<CausalRelationship> detecciones = new ArrayList<CausalRelationship>();
					detecciones.addAll(sequencializer.detectIFTHEN(words , requirement.getResponsibilities(), startPOS));
					//detecciones.addAll(sequencializer.detectTEMPORAL(words, requirement.getResponsibilities(), startPOS));

					cantidad = sequencializer.getCantidadAFTERBEFORE() + sequencializer.getCantidadIFTHEN();
					total += cantidad;
					startPOS = words.size() + 1;

					//System.out.println("\t[" + sentence + "]");
					System.out.println("\t\t Number of Conditional Sentences: [" + cantidad + "]\n");

					if (!detecciones.isEmpty()){
						System.out.println("\tCausal Relationships\n\t\t" + detecciones);
						out.addAll(detecciones);

						requirement.setCausalRelationships(detecciones);
					}

					if (appMainWindow != null)
						appMainWindow.appendRelationsToView(requirement, detecciones);
				}				
			}
		}

		System.out.println("\n\nTotal number of conditional sentences: [" + total +"]");
		System.out.println("\n\nNumber of causal relationships detected in total: [" + out.size() +"]");

		// Here we would have to do the analysis of similar responsibilities to sequentialize and add them to the output list

		ArrayList<Responsibility> resps = new ArrayList<Responsibility>();
		for (Requirement requirement : requirements){
			if (!requirement.getResponsibilities().isEmpty()){
				resps.addAll(requirement.getResponsibilities());
			}
		}

		ArrayList<Pair<Responsibility, Responsibility>> similars = new ArrayList<Pair<Responsibility, Responsibility>>();

		for (int i = 0; i < resps.size() - 1 ; i++){
			for (int j = i + 1; j < resps.size(); j++){
				if (resps.get(i).similarTo(resps.get(j))){
					System.out.println("Iguales : " + resps.get(i).getId() + " " + resps.get(j).getId());
					// The same ones I keep the IDs
					similars.add(new Pair<Responsibility, Responsibility>(resps.get(i), resps.get(j)));
				}
			}
		}

		// Then look for connections Resp 1 -> Res 2 && Resp 2 -> Resp 3
		// If that is the case and Resp 2 are two different responsibilities considered similar
		// add the missing connection

		ArrayList<CausalRelationship> toAdd = new ArrayList<CausalRelationship>();
		for (Pair<Responsibility, Responsibility> similar : similars){
			for (int i = 0; i < out.size() - 1; i++){
				for (int j = i + 1; j < out.size(); j++){
					CausalRelationship cr1 = out.get(i);
					CausalRelationship cr2 = out.get(j);

					if (cr1.getResp2().getId().equals(similar.getPair1().getId()) && cr2.getResp1().getId().equals(similar.getPair2().getId())){
						CausalRelationship newCR = new CausalRelationship(cr1.getResp2(), cr2.getResp1());
						toAdd.add(newCR);
					}
				}
			}
		}

		out.addAll(toAdd);
		return out;
	}

	private ArrayList<ConceptualComponent> responsibilityClustering(ArrayList<Requirement> requirements){

		System.out.println("\n\n** Allocation of Responsibilities into Conceptual Components **");

		ArrayList<ConceptualComponent> out = new ArrayList<ConceptualComponent>();
		ArrayList<ConceptualComponent> outAux = new ArrayList<ConceptualComponent>();
		ArrayList<Responsibility> responsibilities = new ArrayList<Responsibility>();

		// Get together all the responsibilities
		for (Requirement requirement : requirements){
			responsibilities.addAll(requirement.getResponsibilities());
		}

		// Responsibilities are discussed before clustering

		String filterArguments = "weka.filters.unsupervised.attribute.StringToWordVector -R first-last -W 100000 -prune-rate -1.0 -C -T -I -N 1 -L -stemmer weka.core.stemmers.NullStemmer -stopwords-handler weka.core.stopwords.Null -M 1 -tokenizer \"weka.core.tokenizers.WordTokenizer -delimiters \\\" \\\\r\\\\n\\\\t.,;:\\\\\\\'\\\\\\\"()?!\\\"\"";
		//ResponsibilitiesClusterer clusterer1 = new ResponsibilitiesClusterer("weka.clusterers.XMeans -I 1 -M 1000 -J 1000 -L 2 -H 4 -B 1.0 -C 0.5 -D \"weka.core.EuclideanDistance -R first-last\" -S 10",filterArguments,true);

		ResponsibilitiesClustererOLD clusterer1 = new ResponsibilitiesClustererOLD("weka.clusterers.HierarchicalClusterer -N 4 -L SINGLE -P -A \"weka.core.EuclideanDistance -R first-last\"", filterArguments,true);
		//ResponsibilitiesClusterer clusterer1 = new DummyResponsibilitiesClusterer("", filterArguments, true, "KMeans");

		// Preprocessing responsibilities

		SynonymsOverResponsibilities searcherSynonyms = new SynonymsOverResponsibilities() ;
		ArrayList<Responsibility> auxiliaryResponsibilities = new ArrayList<Responsibility>();
		auxiliaryResponsibilities = searcherSynonyms.performWSDOverResponsibilities(responsibilities);

		//System.out.println("Number of changes made in WSD: ["+ searcherSynonyms.getNumberOfChanges() +"]");

		// Clustering
		outAux = clusterer1.createDataAndClusterResponsibilities(auxiliaryResponsibilities);

		Integer i = 1;
		for (ConceptualComponent comp  : outAux){
			ConceptualComponent newComp = new ConceptualComponent();
			newComp.setName("Component " + i++);

			for (Responsibility resp : comp.getResponsibilities()){
				for (Responsibility resp2 : responsibilities){
					if (resp.getId().equals(resp2.getId())){
						newComp.addResponsibility(resp2);
					}					
				}
			}

			out.add(newComp);
		}

		return out;
	}

	private UseCaseMap UCMGeneration(ArrayList<Requirement> requirements) {

		System.out.println("\n\n** UCM generation **");

		UseCaseMap out = new UseCaseMap();

		ArrayList<Responsibility> responsibilities = new ArrayList<Responsibility>();

		// Get together all the responsibilities
		for (Requirement requirement : requirements){
			responsibilities.addAll(requirement.getResponsibilities());
		}

		// Get together all the conceptual components
		ArrayList<ConceptualComponent> components = resultsClustering;
		for (ConceptualComponent comp : components){
			for (Responsibility resp : comp.getResponsibilities()){
				resp.setComponent(comp);
			}
		}

		// I generate the execution traces
		ArrayList<CausalRelationship> relations = resultsSequencing;

		// Here we would have to do the intelligent analysis of responsibilities that are similar

		for (CausalRelationship rel : relations){
			if (rel.getResp2() != null) {
				rel.getResp1().addNextResponsibility(rel.getResp2());
				rel.getResp2().addPrevResponsibility(rel.getResp1());
			}
		}

		// Generation of the UCM
		out.setResponsibilities(responsibilities);
		out.setComponents(components);
		out.setRelations(relations);


		return out;
	}

	@Override
	public void run() {



		if (! requirementsToProcess.isEmpty()){			

			switch (type){
			case STAGE1 :{
				// Responsability Extraction
				responsibilityExtraction(requirementsToProcess);
				break;
			}
			case STAGE2 :{
				// Responsibility Sequencing
				//resultsSequencing = responsibilitySequencing(requirementsToProcess);
				resultsSequencing = respSequencing2(requirementsToProcess);
				break;
			}
			case STAGE3 :{
				// Clustering
				resultsClustering = responsibilityClustering(requirementsToProcess);
				appMainWindow.appendConceptualComponentsToView(resultsClustering);
				break;
			}
			case STAGE4: {
				// Generation of the UCM
				resultUCM = UCMGeneration(requirementsToProcess);
				JUCMExporter exporter = new JUCMExporter();
				Date date = new Date();
				String projectNameWithoutSpaces = project.getProjectName().replace(" ", "_");
				String filePathName = "." + File.separator + "UCMs" + File.separator + "UCM_" + projectNameWithoutSpaces + "_" + date.getTime() +".jucm";
				exporter.export(resultUCM, filePathName);
				appMainWindow.appendUCMToView(filePathName);
				break;
			}
			}	

		}

	}


}
