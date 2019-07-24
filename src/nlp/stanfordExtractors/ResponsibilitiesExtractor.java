package nlp.stanfordExtractors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.mit.jwi.item.POS;
import edu.stanford.nlp.coref.CorefCoreAnnotations;
import edu.stanford.nlp.coref.data.CorefChain.CorefMention;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.semgrex.SemgrexPattern;
import edu.stanford.nlp.trees.GrammaticalRelation;
import edu.stanford.nlp.util.CoreMap;
import model.requirement.Requirement;
import model.responsibility.Responsibility;
import nlp.stanfordExtractors.ResponsibilitiesExtractor.Comparador;
import utils.misc.IndexWordWrapper;
import utils.misc.Pair;
import utils.misc.StringWrapper;

public abstract class ResponsibilitiesExtractor {

	// Variables

	protected String semgrex;
	protected SemgrexPattern pattern;
	protected Annotation document;

	// Constructors

	public ResponsibilitiesExtractor(Annotation document, String semgrex) {
		this.semgrex = semgrex;
		this.pattern = SemgrexPattern.compile(this.semgrex);
		this.document = document;
	}

	// Getters and Setters

	// Methods

	protected String constructStringFromNode(SemanticGraph semanticGraph, IndexedWord nounNode, String text, Boolean useDeterminer){

		int minBegin = Integer.MAX_VALUE;
		int maxEnd = Integer.MIN_VALUE;
		IndexedWord firstNode = null;

		List<IndexedWord> nodes = semanticGraph.getChildList(nounNode);
		nodes.add(nounNode);

		for ( IndexedWord indexedWord : nodes){
			if (indexedWord.beginPosition() < minBegin){
				minBegin = indexedWord.beginPosition();
				firstNode = indexedWord;
			}
			if (indexedWord.endPosition() > maxEnd){
				maxEnd = indexedWord.endPosition();
			}
		}

		if (!useDeterminer && semanticGraph.getChildWithReln(nounNode, GrammaticalRelation.valueOf("det")) == firstNode){
			minBegin += firstNode.originalText().length();			
		}

		return text.substring(minBegin, maxEnd).trim();
	}

	protected HashSet<IndexedWord> getAllNodes(SemanticGraph semanticGraph, IndexedWord rootNode, HashSet<IndexedWord> out){

		for (IndexedWord node : semanticGraph.getChildList(rootNode)){
			if (!out.contains(node)){
				out.add(node);
				getAllNodes(semanticGraph, node, out);
			}
		}
		if (!out.contains(rootNode))
			out.add(rootNode);

		return out;
	}

	// Auxiliar comparator

	protected class Comparador implements Comparator<IndexedWord>{

		@Override
		public int compare(IndexedWord o1, IndexedWord o2) {

			return ((Integer) o1.index()).compareTo((Integer)o2.index()) ;
		}

	}

	protected ArrayList<Pair<String, POS>> constructRecognitionsFromNode(SemanticGraph semanticGraph, IndexedWord dobjNode, String text) {

		ArrayList<Pair<String, POS>> out = new ArrayList<Pair<String, POS>>();
		int minBegin = Integer.MAX_VALUE;
		int maxEnd = Integer.MIN_VALUE;
		IndexedWord firstNode = null;

		// All internal nodes are recovered
		HashSet<IndexedWord> allNodes = getAllNodes(semanticGraph, dobjNode, new HashSet<IndexedWord>());

		// Sorting
		ArrayList<IndexedWord> nodes = new ArrayList<IndexedWord>();
		for (IndexedWord node : allNodes){
			if (!node.originalText().equals(",")){
				nodes.add(node);
			}else {
				break;
			}
		}

		Collections.sort(nodes, new Comparador());

		for (IndexedWord indexedWord : nodes){
			if (indexedWord.tag().startsWith("NN")){
				out.add(new Pair<String, POS>(indexedWord.originalText(), POS.NOUN));
			}else
				if (indexedWord.tag().startsWith("VB")){
					out.add(new Pair<String, POS>(indexedWord.originalText(), POS.VERB));
				}else
					if (indexedWord.tag().startsWith("JJ")){
						out.add(new Pair<String, POS>(indexedWord.originalText(), POS.ADJECTIVE));
					}else
						if (indexedWord.tag().startsWith("RB")){
							out.add(new Pair<String, POS>(indexedWord.originalText(), POS.ADVERB));
						}else
							out.add(new Pair<String, POS>(indexedWord.originalText(), null));
		}

		return out;
	}

	protected Boolean detectNegation(SemanticGraph semanticGraph, IndexedWord verbNode) {
		Set<IndexedWord> negs = semanticGraph.getChildrenWithReln(verbNode, GrammaticalRelation.valueOf("neg"));	
		return (negs != null) && (!negs.isEmpty());
	}

	protected boolean hacerCambioReferencia(String viejoString, String nuevoString) {
		return !viejoString.equals(nuevoString);
	}


	protected boolean searchBackReference(IndexedWord dobjNode, StringWrapper dobj, StringWrapper long_dobj, IndexWordWrapper nodeW, Requirement requirement, SemanticGraph semanticGraph, Integer sentNum) {

		Boolean out = true;
		
		if (dobjNode != null && dobjNode.backingLabel().tag().equals("PRP")) {
			
			out = false;
			for (edu.stanford.nlp.coref.data.CorefChain cc : document.get(CorefCoreAnnotations.CorefChainAnnotation.class).values()) {
				if (cc.getMentionMap().size() > 1) {
					for (CorefMention mention : cc.getMentionsInTextualOrder()) {	
						if (mention.headIndex == dobjNode.index() && mention.sentNum == (sentNum+1)) {
							Boolean excp = false;
							try {
								nodeW.set(semanticGraph.getNodeByIndex(cc.getRepresentativeMention().headIndex));
							}catch (Exception e) {
								excp = true;
							}
							// Update values
							if (hacerCambioReferencia(dobj.getString(), nodeW.get().originalText())) {
								if (!excp) {
									long_dobj.setString(constructStringFromNode(semanticGraph, nodeW.get(), requirement.getText(), false));
									dobj.setString(nodeW.get().originalText());

								}else {
									
									long_dobj.setString(cc.getRepresentativeMention().mentionSpan);
									dobj.setString(nodeW.get().originalText());
								}
								return true;
							}else
								return false;

						}
					}
				}
			}
		}
		
		return out;
	}

	protected Integer calculateLastPosition(SemanticGraph semanticGraph, IndexedWord dobjNode) {
		List<IndexedWord> nodes = semanticGraph.getChildList(dobjNode);
		nodes.add(dobjNode);
		Integer lastPosition = null;
		HashSet<IndexedWord> allNodes = getAllNodes(semanticGraph, dobjNode, new HashSet<IndexedWord>());
		ArrayList<IndexedWord> nodesAux = new ArrayList<IndexedWord>();
		for (IndexedWord node : allNodes){
			if (!node.originalText().equals(","))
				nodesAux.add(node);
			else {
				break;
			}
		}
		Collections.sort(nodesAux, new Comparador());				
		lastPosition = nodesAux.get(nodesAux.size()-1).index();
		return lastPosition;
	}

	public abstract ArrayList<Responsibility> extractResponsibilitiesFromRequirements(Requirement requirement, CoreMap sentence, SemanticGraph semanticGraph, HashMap<Integer, Integer> wordsCount);

}
