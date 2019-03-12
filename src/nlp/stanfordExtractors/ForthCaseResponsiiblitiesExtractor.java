package nlp.stanfordExtractors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import edu.mit.jwi.item.POS;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.semgrex.SemgrexMatcher;
import edu.stanford.nlp.semgraph.semgrex.SemgrexPattern;
import edu.stanford.nlp.util.CoreMap;
import model.requirement.Requirement;
import model.responsibility.Responsibility;
import utils.misc.IndexWordWrapper;
import utils.misc.Pair;
import utils.misc.StringWrapper;

public class ForthCaseResponsiiblitiesExtractor extends ResponsibilitiesExtractor {

	// Variables

	// Constructors

	public ForthCaseResponsiiblitiesExtractor(Annotation document) {
		super(document, "{tag:/VBN|VB/}=VERB >nsubjpass {tag:/NN.*|PRP/}=NSUBJPASS !>>neg {}");
	}

	// Getters and Setters

	// Methods

	@Override
	public ArrayList<Responsibility> extractResponsibilitiesFromRequirements(Requirement requirement, CoreMap sentence,SemanticGraph semanticGraph, HashMap<Integer, Integer> wordsCount) {

		ArrayList<Responsibility> out = new ArrayList<Responsibility>();

		SemgrexMatcher matcher = pattern.matcher(semanticGraph);

		// lista
		List<CoreMap> list = sentence.get(edu.stanford.nlp.ling.CoreAnnotations.NumerizedTokensAnnotation.class);

		while (matcher.find()){

			// Buscar los nodos
			IndexedWord nsubjpassNode = matcher.getNode("NSUBJPASS");
			IndexedWord verbNode = matcher.getNode("VERB");
			IndexedWord representativeCoreferenceNode = null;

			// Obtener la informacion a partir de los nod				
			String long_dobj = "";
			String dobj = "";

			if (nsubjpassNode != null) {
				dobj = nsubjpassNode.originalText();
				long_dobj = constructStringFromNode(semanticGraph, nsubjpassNode, requirement.getText(), false);
				// Si el objeto directo es una Personal pronoun (PRP) entonces hay que ir a buscar a que hace referencia 
				
				StringWrapper dobjW = new StringWrapper(dobj);
				StringWrapper long_dobjW = new StringWrapper(long_dobj);
				IndexWordWrapper nodeW = new IndexWordWrapper(representativeCoreferenceNode);
				
				boolean resultado = searchBackReference(nsubjpassNode, dobjW, long_dobjW, nodeW, requirement, semanticGraph, nsubjpassNode.sentIndex());
				if (!resultado)
					continue;
				dobj = dobjW.getString();
				long_dobj = long_dobjW.getString();
				representativeCoreferenceNode = nodeW.get();
			} 

			String verb = verbNode.originalText();
			Boolean negationDetected = detectNegation(semanticGraph, verbNode);

			// Creamos la responsabilidad
			Responsibility responsibility = new Responsibility(verb, dobj);

			// La responsabilidad esta negada? (Si esta negada no se tiene que mostrar en el path final)
			responsibility.setNegated(negationDetected);			

			// Reconocimientos	
			ArrayList<Pair<String, POS>> recognitions = new ArrayList<Pair<String, POS>>();
			recognitions.add(new Pair<String, POS> (verb, POS.VERB));
			if (representativeCoreferenceNode == null)
				recognitions.addAll(constructRecognitionsFromNode(semanticGraph, nsubjpassNode, requirement.getText()));
			else
				recognitions.addAll(constructRecognitionsFromNode(semanticGraph, representativeCoreferenceNode, requirement.getText()));
			responsibility.setRecognitions(recognitions);

			responsibility.setVerbBaseForm(verb, verbNode.tag());
			
			// Calculas las posiciones de los elementos
			if (nsubjpassNode != null) {
				List<IndexedWord> nodes = semanticGraph.getChildList(nsubjpassNode);
				nodes.add(nsubjpassNode);
				Integer lastPosition = calculateLastPosition(semanticGraph, nsubjpassNode);			
				responsibility.setFirstWordPosition(wordsCount.get(4) + nsubjpassNode.index());
				responsibility.setLastWordPositionShort(wordsCount.get(4) + verbNode.index());
				responsibility.setLastWordPositionLong(wordsCount.get(4) + lastPosition);
				responsibility.setDobjToken(nsubjpassNode.backingLabel().toString());
				responsibility.setVerbToken(verbNode.backingLabel().toString());
			}

			// Agregamos la responsabilidad al colector de salida
			out.add(responsibility);
		}

		// Actualizo el wordsCount
		wordsCount.put(4, wordsCount.get(4) + list.size());	

		return out;

	}

}
