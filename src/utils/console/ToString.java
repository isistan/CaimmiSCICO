package utils.console;

import java.util.Collection;
import java.util.Map;

import edu.stanford.nlp.dcoref.CorefChain;
import edu.stanford.nlp.dcoref.CorefCoreAnnotations.CorefChainAnnotation;
import edu.stanford.nlp.ie.util.RelationTriple;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.naturalli.NaturalLogicAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;
import edu.stanford.nlp.util.CoreMap;

public class ToString {

	// Variables

	private static ToString instance = null;	

	// Constructors

	private ToString(){

	}

	// Getters and Setters

	public static ToString getInstance(){
		if (instance == null){
			instance = new ToString();
		}
		return instance;
	}

	// Methods

	public String toStringSentenceTokens(CoreMap sentence){
		String out = "";		
		// traversing the words in the current sentence
		// a CoreLabel is a CoreMap with additional token-specific methods
		for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
			// this is the text of the token
			String word = token.get(TextAnnotation.class);
			// this is the POS tag of the token
			String pos = token.get(PartOfSpeechAnnotation.class);
			// this is the NER label of the token
			String ne = token.get(NamedEntityTagAnnotation.class);

			out += "[ Token = " + word + " | POS = " + pos + " | Named-Entity = " + ne + "]\n";
		}

		return out;
	}

	public String toStringSentenceTree(CoreMap sentence){
		// this is the parse tree of the current sentence
		Tree tree = sentence.get(TreeAnnotation.class);
		return tree.toString();
	}

	public String toStringSentenceDependencies(CoreMap sentence){
		// this is the Stanford dependency graph of the current sentence
		SemanticGraph dependencies = sentence.get(CollapsedCCProcessedDependenciesAnnotation.class);
		return dependencies.toString();
	}

	public String toStringSentenceOpenIE(CoreMap sentence){
		String out = "";

		// Get the OpenIE triples for the sentence
		Collection<RelationTriple> triples = sentence.get(NaturalLogicAnnotations.RelationTriplesAnnotation.class);
		// Print the triples
		for (RelationTriple triple : triples) {
			out += "[ Subject = " + triple.subjectLemmaGloss() + " | Relation = " + triple.relationGloss() + " | Object = " + triple.objectLemmaGloss() + " | Confidence = " + triple.confidence + " ]";
		}

		return out;
	}

	public String toStringCoreference(Annotation document){
		String out = "";
		
		// This is the coreference link graph
		// Each chain stores a set of mentions that link to each other,
		// along with a method for getting the most representative mention
		// Both sentence and token offsets start at 1!
		Map<Integer, CorefChain> graph = document.get(CorefChainAnnotation.class);

		if (graph != null){
			for (int i = 1; i <= graph.size(); i++){
				out += "[ Node = i => " + graph.get(i).toString() + "]";
			}
		}
		
		return out;
	}

}
