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

public class ThirdCaseResponsiiblitiesExtractor extends ResponsibilitiesExtractor {

	// Variables

	// Constructors

	public ThirdCaseResponsiiblitiesExtractor(Annotation document) {
		super(document, "{tag:/VB.*|NN.*/}=FIRST_VERB ?>/.*subj/ {tag:/NN.*|PRP/}=NSUBJ >xcomp ({tag:/VB|VBP|VBZ|NN.*/}=SECOND_VERB ?>/nmod:(to|on)/ {tag:/NN.*|PRP/}=NMODIFIED_NOUN ?>dobj {tag:/NN.*|PRP/}=DOBJ)");
	}

	// Getters and Setters

	// Methods

	@Override
	public ArrayList<Responsibility> extractResponsibilitiesFromRequirements(Requirement requirement, CoreMap sentence,SemanticGraph semanticGraph, HashMap<Integer, Integer> wordsCount) {

		ArrayList<Responsibility> out = new ArrayList<Responsibility>();

		SemgrexMatcher matcher = pattern.matcher(semanticGraph);

		// list
		List<CoreMap> list = sentence.get(edu.stanford.nlp.ling.CoreAnnotations.NumerizedTokensAnnotation.class);

		while (matcher.find()){

			// Search for nodes
			IndexedWord nsubjNode = matcher.getNode("NSUBJ");
			IndexedWord firstVerbNode = matcher.getNode("FIRST_VERB");
			IndexedWord secondVerbNode = matcher.getNode("SECOND_VERB");
			IndexedWord dobjNode = matcher.getNode("DOBJ");
			IndexedWord nmodifiedNode = matcher.getNode("NMODIFIED_NOUN");
			IndexedWord representativeCoreferenceNode = null;

			// Get the information from the nodes			
			String long_dobj = "";
			String dobj = "";

			if (dobjNode == null && nmodifiedNode == null)
				continue;
			
			StringWrapper dobjW = new StringWrapper(dobj);
			StringWrapper long_dobjW = new StringWrapper(long_dobj);
			IndexWordWrapper nodeW = new IndexWordWrapper(representativeCoreferenceNode);

			if (dobjNode != null) {
				dobj = dobjNode.originalText();
				long_dobj = constructStringFromNode(semanticGraph, dobjNode, requirement.getText(), false);
				// If the direct object is a Personal pronoun (PRP) then you have to go find what it refers to
				searchBackReference(dobjNode, dobjW, long_dobjW, nodeW, requirement, semanticGraph, dobjNode.sentIndex());
			} else if (nmodifiedNode != null) {
				dobj = nmodifiedNode.originalText();
				long_dobj = constructStringFromNode(semanticGraph, nmodifiedNode, requirement.getText(), false);
				searchBackReference(nmodifiedNode, dobjW, long_dobjW, nodeW, requirement, semanticGraph, nmodifiedNode.sentIndex());
			}

			dobj = dobjW.getString();
			long_dobj = long_dobjW.getString();
			representativeCoreferenceNode = nodeW.get();
			
			String verb = secondVerbNode.originalText();
			Boolean negationDetected = detectNegation(semanticGraph, firstVerbNode);

			// We create the responsibility
			Responsibility responsibility = new Responsibility(verb, dobj);

			// The responsibility is denied? (If it is denied it does not have to be shown in the final path)
			responsibility.setNegated(negationDetected);			

			// Acknowledgments
			ArrayList<Pair<String, POS>> recognitions = new ArrayList<Pair<String, POS>>();
			recognitions.add(new Pair<String, POS> (verb, POS.VERB));
			if (representativeCoreferenceNode == null && dobjNode != null)
				recognitions.addAll(constructRecognitionsFromNode(semanticGraph, dobjNode, requirement.getText()));
			else
				if (representativeCoreferenceNode == null && nmodifiedNode != null)
					recognitions.addAll(constructRecognitionsFromNode(semanticGraph, nmodifiedNode, requirement.getText()));
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
				responsibility.setFirstWordPosition(wordsCount.get(3) + secondVerbNode.index());
				responsibility.setLastWordPositionShort(wordsCount.get(3) + dobjNode.index());
				responsibility.setLastWordPositionLong(wordsCount.get(3) + lastPosition);	
			}else
				if (nmodifiedNode != null) {
					List<IndexedWord> nodes = semanticGraph.getChildList(nmodifiedNode);
					nodes.add(nmodifiedNode);
					lastPosition = calculateLastPosition(semanticGraph, nmodifiedNode);	
					responsibility.setFirstWordPosition(wordsCount.get(3) + secondVerbNode.index());
					responsibility.setLastWordPositionShort(wordsCount.get(3) + nmodifiedNode.index());
					responsibility.setLastWordPositionLong(wordsCount.get(3) + lastPosition);	
				}

			// We add the responsibility to the output manifold
			out.add(responsibility);
		}

		// Update the wordsCount
		wordsCount.put(3, wordsCount.get(3) + list.size());	

		return out;


	}

}
