package nlp.stanfordExtractors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.mit.jwi.item.POS;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.semgrex.SemgrexMatcher;
import edu.stanford.nlp.util.CoreMap;
import model.requirement.Requirement;
import model.responsibility.Responsibility;
import utils.misc.IndexWordWrapper;
import utils.misc.Pair;
import utils.misc.StringWrapper;

public class SecondCaseResponsiiblitiesExtractor extends ResponsibilitiesExtractor {

	// Variables

	// Constructors

	public SecondCaseResponsiiblitiesExtractor(Annotation document) {
		super(document, "{tag:/VB|VBP|VBZ|NN.*/}=VERB ?>/.*subj/ {tag:/NN.*|PRP/}=NSUBJ >/nmod:(on|to|between)/ {tag:/NN.*|PRP/}=NMOD !>dobj {tag:/NN.*|PRP/}");
	}

	// Getters and Setters

	// Methods

	@Override
	public ArrayList<Responsibility> extractResponsibilitiesFromRequirements(Requirement requirement, CoreMap sentence,SemanticGraph semanticGraph, HashMap<Integer, Integer> wordsCount) {

		ArrayList<Responsibility> out = new ArrayList<Responsibility>();

		//SemanticGraph semanticGraph = sentence.get(edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.EnhancedPlusPlusDependenciesAnnotation.class);
		SemgrexMatcher matcher = pattern.matcher(semanticGraph);

		// Lista
		List<CoreMap> list = sentence.get(edu.stanford.nlp.ling.CoreAnnotations.NumerizedTokensAnnotation.class);

		while (matcher.find()){

			// Buscar los nodos
			//IndexedWord nsubjNode = matcher.getNode("NSUBJ");
			IndexedWord verbNode = matcher.getNode("VERB");
			IndexedWord dobjNode = matcher.getNode("NMOD");
			IndexedWord representativeCoreferenceNode = null;

			// Obtener la informacion a partir de los nod				
			String long_dobj = constructStringFromNode(semanticGraph, dobjNode, requirement.getText(), false);
			String verb = verbNode.originalText();
			String dobj = dobjNode.originalText();
			Boolean negationDetected = detectNegation(semanticGraph, verbNode);

			// Si el objeto directo es una Personal pronoun (PRP) entonces hay que ir a buscar a que hace referencia 
			StringWrapper dobjW = new StringWrapper(dobj);
			StringWrapper long_dobjW = new StringWrapper(long_dobj);
			IndexWordWrapper nodeW = new IndexWordWrapper(representativeCoreferenceNode);
			boolean resultado = searchBackReference(dobjNode, dobjW, long_dobjW, nodeW, requirement, semanticGraph, dobjNode.sentIndex());
			if (!resultado)
				continue;
			dobj = dobjW.getString();
			long_dobj = long_dobjW.getString();
			representativeCoreferenceNode = nodeW.get();
			
			// Creamos la responsabilidad
			Responsibility responsibility = new Responsibility(verb, dobj);

			// La responsabilidad esta negada? (Si esta negada no se tiene que mostrar en el path final)
			responsibility.setNegated(negationDetected);			

			// Reconocimientos	
			ArrayList<Pair<String, POS>> recognitions = new ArrayList<Pair<String, POS>>();
			recognitions.add(new Pair<String, POS> (verb, POS.VERB));
			if (representativeCoreferenceNode == null)
				recognitions.addAll(constructRecognitionsFromNode(semanticGraph, dobjNode, requirement.getText()));
			else
				recognitions.addAll(constructRecognitionsFromNode(semanticGraph, representativeCoreferenceNode, requirement.getText()));
			responsibility.setRecognitions(recognitions);

			// Calculas las posiciones de los elementos	
			Integer lastPosition = calculateLastPosition(semanticGraph, dobjNode);			
			responsibility.setFirstWordPosition(wordsCount.get(2) + verbNode.index());
			responsibility.setLastWordPositionShort(wordsCount.get(2) + dobjNode.index());
			responsibility.setLastWordPositionLong(wordsCount.get(2) + lastPosition);
			responsibility.setDobjToken(dobjNode.backingLabel().toString());
			responsibility.setVerbToken(verbNode.backingLabel().toString());
			responsibility.setVerbBaseForm(verb, verbNode.tag());
			
			// Agregamos la responsabilidad al colector de salida
			out.add(responsibility);
		}

		// Actualizo el wordsCount
		wordsCount.put(2, wordsCount.get(2) + list.size());	

		return out;

	}

}
