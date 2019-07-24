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

			// Search for nodes
			IndexedWord nsubjpassNode = matcher.getNode("NSUBJPASS");
			IndexedWord verbNode = matcher.getNode("VERB");
			IndexedWord representativeCoreferenceNode = null;

			// Get the information from the nodes			
			String long_dobj = "";
			String dobj = "";

			if (nsubjpassNode != null) {
				dobj = nsubjpassNode.originalText();
				long_dobj = constructStringFromNode(semanticGraph, nsubjpassNode, requirement.getText(), false);
				// If the direct object is a Personal pronoun (PRP) then you have to go find what it refers to
				
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

			// We create the responsibility
			Responsibility responsibility = new Responsibility(verb, dobj);

			// The responsibility is denied? (If it is denied it does not have to be shown in the final path)
			responsibility.setNegated(negationDetected);			

			// Acknowledgments
			ArrayList<Pair<String, POS>> recognitions = new ArrayList<Pair<String, POS>>();
			recognitions.add(new Pair<String, POS> (verb, POS.VERB));
			if (representativeCoreferenceNode == null)
				recognitions.addAll(constructRecognitionsFromNode(semanticGraph, nsubjpassNode, requirement.getText()));
			else
				recognitions.addAll(constructRecognitionsFromNode(semanticGraph, representativeCoreferenceNode, requirement.getText()));
			responsibility.setRecognitions(recognitions);

			responsibility.setVerbBaseForm(verb, verbNode.tag());
			
			// You calculate the positions of the elements
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

			// We add the responsibility to the output manifold
			out.add(responsibility);
		}

		// I update the wordsCount
		wordsCount.put(4, wordsCount.get(4) + list.size());	

		return out;

	}

}
