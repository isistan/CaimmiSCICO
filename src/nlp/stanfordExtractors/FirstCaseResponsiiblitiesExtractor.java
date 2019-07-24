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

public class FirstCaseResponsiiblitiesExtractor extends ResponsibilitiesExtractor {

	// Variables

	// Constructors

	public FirstCaseResponsiiblitiesExtractor(Annotation document) {
		super(document, "{tag:/VB|VBP|VBZ|VBG|NN.*/}=VERB >dobj {tag:/NN.*|PRP/}=DOBJ ?>/.*subj/ {tag:/NN.*|PRP/}=NSUBJ");
	}

	// Getters and Setters

	// Methods

	@Override
	public ArrayList<Responsibility> extractResponsibilitiesFromRequirements(Requirement requirement, CoreMap sentence, SemanticGraph semanticGraph, HashMap<Integer, Integer> wordsCount) {

		ArrayList<Responsibility> out = new ArrayList<Responsibility>();

		// List of words annotated with numbers
		List<CoreMap> list = sentence.get(edu.stanford.nlp.ling.CoreAnnotations.NumerizedTokensAnnotation.class);
		System.out.println("\t" + list);

		// Semantic graph and matcher
		//SemanticGraph semanticGraph = sentence.get(edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.EnhancedPlusPlusDependenciesAnnotation.class);
		SemgrexMatcher matcher = pattern.matcher(semanticGraph);

		while (matcher.find()){

			// Search for nodes
			IndexedWord nsubjNode = matcher.getNode("NSUBJ");
			IndexedWord verbNode = matcher.getNode("VERB");
			IndexedWord dobjNode = matcher.getNode("DOBJ");
			IndexedWord representativeCoreferenceNode = null;

			// Get the information from the nodes				
			String verb = verbNode.originalText();
			String dobj = dobjNode.originalText();
			String long_dobj = constructStringFromNode(semanticGraph, dobjNode, requirement.getText(), false);
			Boolean negationDetected = detectNegation(semanticGraph, verbNode);

			// If the direct object is a Personal pronoun (PRP) then you have to go find what it refers to 
			StringWrapper dobjW = new StringWrapper(dobj);
			StringWrapper long_dobjW = new StringWrapper(long_dobj);
			IndexWordWrapper nodeW = new IndexWordWrapper(representativeCoreferenceNode);
			
			boolean resultado = searchBackReference(dobjNode, dobjW, long_dobjW, nodeW, requirement, semanticGraph, dobjNode.sentIndex());
			if (!resultado)
				continue;
			
			dobj = dobjW.getString();
			long_dobj = long_dobjW.getString();
			representativeCoreferenceNode = nodeW.get();
			
			// We create the responsibility
			Responsibility responsibility = new Responsibility(verb, dobj);

			// The responsibility is denied? (If it is denied it does not have to be shown in the final path)
			responsibility.setNegated(negationDetected);			

			// Acknowledgments
			ArrayList<Pair<String, POS>> recognitions = new ArrayList<Pair<String, POS>>();
			recognitions.add(new Pair<String, POS> (verb, POS.VERB));
			if (representativeCoreferenceNode == null)
				recognitions.addAll(constructRecognitionsFromNode(semanticGraph, dobjNode, requirement.getText()));
			else
				recognitions.addAll(constructRecognitionsFromNode(semanticGraph, representativeCoreferenceNode, requirement.getText()));
			responsibility.setRecognitions(recognitions);

			// You calculate the positions of the elements
			Integer lastPosition = calculateLastPosition(semanticGraph, dobjNode);			
			responsibility.setFirstWordPosition(wordsCount.get(1) + verbNode.index());
			responsibility.setLastWordPositionShort(wordsCount.get(1) + dobjNode.index());
			responsibility.setLastWordPositionLong(wordsCount.get(1) + lastPosition);
			responsibility.setVerbToken(verbNode.backingLabel().toString());
			responsibility.setDobjToken(dobjNode.backingLabel().toString());
			responsibility.setVerbBaseForm(verb, verbNode.tag());
			
			// We add the responsibility to the output manifold
			out.add(responsibility);
		}

		// Update the wordsCount
		wordsCount.put(1, wordsCount.get(1) + list.size());	

		return out;
	}

}
