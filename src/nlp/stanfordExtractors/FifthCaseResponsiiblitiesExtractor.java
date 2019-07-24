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

public class FifthCaseResponsiiblitiesExtractor extends ResponsibilitiesExtractor {

	// Variables

	// Constructors

	public FifthCaseResponsiiblitiesExtractor(Annotation document) {
		super(document, "{tag:/VB|VBP|VBZ/}=VERB ?>nsubj {tag:/NN.*|PRP/}=NSUBJ >/advcl:(by|for)/ ({tag:VBG}=VERBG ?>dobj {tag:/NN.*|PRP/}=DOBJ ?>/nmod:(to|on)/ {tag:/NN.*|PRP/}=NMOD)");
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

			// Search for nodes
			IndexedWord verbNode = matcher.getNode("VERB");
			IndexedWord verbGNode = matcher.getNode("VERBG");
			IndexedWord dobjNode = matcher.getNode("DOBJ");
			IndexedWord nmodNode = matcher.getNode("NMOD");
			IndexedWord nsubjNode = matcher.getNode("NSUBJ");
			IndexedWord representativeCoreferenceNode = null;

			if (dobjNode == null && nmodNode == null && nsubjNode == null) {
				continue;
			}
			
			// Get the information from the nodes			
			String long_dobj = "";
			String dobj = "";
			String verb = verbGNode.originalText();
			StringWrapper dobjW = new StringWrapper(dobj);
			StringWrapper long_dobjW = new StringWrapper(long_dobj);
			IndexWordWrapper nodeW = new IndexWordWrapper(representativeCoreferenceNode);
			
			if (dobjNode != null) {
				dobj = dobjNode.originalText();
				long_dobj = constructStringFromNode(semanticGraph, dobjNode, requirement.getText(), false);
				// If the direct object is a Personal pronoun (PRP) then you have to go find what it refers to 
				searchBackReference(dobjNode, dobjW, long_dobjW, nodeW, requirement, semanticGraph, dobjNode.sentIndex());
			} else if (nmodNode != null) {
				dobj = nmodNode.originalText();
				long_dobj = constructStringFromNode(semanticGraph, nmodNode, requirement.getText(), false);
				searchBackReference(nmodNode, dobjW, long_dobjW, nodeW, requirement, semanticGraph, nmodNode.sentIndex());
			}
			dobj = dobjW.getString();
			long_dobj = long_dobjW.getString();
			representativeCoreferenceNode = nodeW.get();
			
			// We create the responsibility
			Responsibility responsibility = new Responsibility(verb, dobj);

			// The responsibility is denied? (If it is denied it does not have to be shown in the final path)
			Boolean negationDetected = detectNegation(semanticGraph, verbNode);
			responsibility.setNegated(negationDetected);			

			// Acknowledgments	
			ArrayList<Pair<String, POS>> recognitions = new ArrayList<Pair<String, POS>>();
			recognitions.add(new Pair<String, POS> (verb, POS.VERB));
			
			if (representativeCoreferenceNode == null && dobjNode != null)
				recognitions.addAll(constructRecognitionsFromNode(semanticGraph, dobjNode, requirement.getText()));
			else
				if (representativeCoreferenceNode == null && nmodNode != null)
					recognitions.addAll(constructRecognitionsFromNode(semanticGraph, nmodNode, requirement.getText()));
				else
					if (representativeCoreferenceNode != null)
						recognitions.addAll(constructRecognitionsFromNode(semanticGraph, representativeCoreferenceNode, requirement.getText()));
			responsibility.setRecognitions(recognitions);

			// You calculate the positions of the elements
			Integer lastPosition = null;
			if (dobjNode != null) {
				List<IndexedWord> nodes = semanticGraph.getChildList(dobjNode);
				nodes.add(dobjNode);
				lastPosition = calculateLastPosition(semanticGraph, dobjNode);	
				responsibility.setFirstWordPosition(wordsCount.get(5) + verbGNode.index());
				responsibility.setLastWordPositionShort(wordsCount.get(5) + dobjNode.index());
				responsibility.setLastWordPositionLong(wordsCount.get(5) + lastPosition);
			}else
				if (nmodNode != null) {
					List<IndexedWord> nodes = semanticGraph.getChildList(nmodNode);
					nodes.add(nmodNode);
					lastPosition = calculateLastPosition(semanticGraph, nmodNode);	
					responsibility.setFirstWordPosition(wordsCount.get(5) + verbGNode.index());
					responsibility.setLastWordPositionShort(wordsCount.get(5) + nmodNode.index());
					responsibility.setLastWordPositionLong(wordsCount.get(5) + lastPosition);;
				}	

			// We add the responsibility to the output manifold
			out.add(responsibility);
		}

		// I update the wordsCount
		wordsCount.put(5, wordsCount.get(5) + list.size());	
	
		return out;

	}

}
