package nlp;

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
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.semgrex.SemgrexMatcher;
import edu.stanford.nlp.semgraph.semgrex.SemgrexPattern;
import edu.stanford.nlp.trees.GrammaticalRelation;
import edu.stanford.nlp.util.CoreMap;
import model.requirement.Requirement;
import model.responsibility.Responsibility;
import nlp.stanfordExtractors.FifthCaseResponsiiblitiesExtractor;
import nlp.stanfordExtractors.FirstCaseResponsiiblitiesExtractor;
import nlp.stanfordExtractors.ForthCaseResponsiiblitiesExtractor;
import nlp.stanfordExtractors.ResponsibilitiesExtractor;
import nlp.stanfordExtractors.SecondCaseResponsiiblitiesExtractor;
import nlp.stanfordExtractors.ThirdCaseResponsiiblitiesExtractor;
import utils.misc.Pair;

public class StanfordExtractor {

	// Variables

	private NLPConnector nlp;

	// Constructors

	public StanfordExtractor(NLPConnector nlp){
		super();
		this.nlp = nlp;
	}

	// Getters and Setters

	// Methods

	public ArrayList<String> getSentences(String text) {
		ArrayList<String> out = new ArrayList<String>();

		List<CoreMap> sentences = nlp.annotate(text);			

		for (CoreMap sentence : sentences){
			out.add(sentence.get(TextAnnotation.class));
		}

		return out;
	}
	
	public ArrayList<CoreMap> getCoreMap(String text){
		return (ArrayList<CoreMap>) nlp.annotate(text);			
	}

	public ArrayList<Responsibility> extractResponsibilitiesFromRequirement(Requirement requirement){

		ArrayList<Responsibility> out = new ArrayList<Responsibility>();

		// We write down the text of the requirement
		List<CoreMap> sentences = nlp.annotate(requirement.getText());	
		HashMap<Integer, Integer> wordsCounts = new HashMap<Integer, Integer>();
		wordsCounts.put(1, 0);
		wordsCounts.put(2, 0);
		wordsCounts.put(3, 0);
		wordsCounts.put(4, 0);
		wordsCounts.put(5, 0);
		System.out.println("\tSentences : [" + sentences.size() + "]");

		ResponsibilitiesExtractor ext1 = new FirstCaseResponsiiblitiesExtractor(nlp.getDocument());
		ResponsibilitiesExtractor ext2 = new SecondCaseResponsiiblitiesExtractor(nlp.getDocument());
		//ResponsibilitiesExtractor ext3 = new ThirdCaseResponsiiblitiesExtractor(nlp.getDocument());
		ResponsibilitiesExtractor ext4 = new ForthCaseResponsiiblitiesExtractor(nlp.getDocument());
		//ResponsibilitiesExtractor ext5 = new FifthCaseResponsiiblitiesExtractor(nlp.getDocument());

		// Sentence by sentence
		Integer sentenceNumber = 1;
		for (CoreMap sentence : sentences){

			SemanticGraph semanticGraph = sentence.get(edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.EnhancedPlusPlusDependenciesAnnotation.class);

			// We detect the responsibilities
			ArrayList<Responsibility> outAux = new ArrayList<Responsibility>();

			// Case 1
			ArrayList<Responsibility> out1 = ext1.extractResponsibilitiesFromRequirements(requirement, sentence, semanticGraph, wordsCounts);
			outAux.addAll(out1);
			System.out.println("\t\tCaso 1 [" + out1.size() + "]: " + out1);

			// Case 2
			ArrayList<Responsibility> out2 = ext2.extractResponsibilitiesFromRequirements(requirement, sentence, semanticGraph, wordsCounts);
			ArrayList<Responsibility> outAux2 = new ArrayList<Responsibility>();
			for (Responsibility resp2 : out2) {
				Boolean meter = true;
				for (Responsibility respAux : outAux) {
					Boolean aux1 = resp2.getVerb().trim().equals(respAux.getVerb().trim());
					Boolean aux2 = resp2.getFirstWordPosition() == respAux.getFirstWordPosition();
					if (aux1 && aux2) {
						meter = false;
					}
				}
				if (meter) {
					outAux2.add(resp2);
				}
			}
			outAux.addAll(outAux2);
			System.out.println("\t\tCaso 2 [" + out2.size() + "]: " + out2);

			// Case 4
			//ArrayList<Responsibility> out4 = extractForthCaseResponsibilitiesFromRequirements(requirement, sentence, wordsCounts);
			ArrayList<Responsibility> out4 = ext4.extractResponsibilitiesFromRequirements(requirement, sentence, semanticGraph, wordsCounts);
			ArrayList<Responsibility> outAux4 = new ArrayList<Responsibility>();
			for (Responsibility resp4 : out4) { 
				Boolean meterSiNoEstaRepetido = true;
				Boolean meterSiExisteSimilar = false;
				for (Responsibility respAux : outAux) {
					Boolean aux1 = resp4.getVerb().trim().equals(respAux.getVerb().trim());
					Boolean aux2 = resp4.getFirstWordPosition() == respAux.getFirstWordPosition();
					if (aux1 && aux2) {
						meterSiNoEstaRepetido = false;
					}

					// Consider if it previously existed
					Boolean aux3 = resp4.getDobj().toLowerCase().trim().equals(respAux.getDobj().toLowerCase().trim());
					if (aux1 && aux3) {
						meterSiExisteSimilar = true;
					}
				}
				if (meterSiNoEstaRepetido /*&& meterSiExisteSimilar*/) {
					outAux4.add(resp4);
				}
			}
			outAux.addAll(outAux4);
			System.out.println("\t\tCaso 4 [" + out4.size() + "]: " + out4);
			
			// Verify that there are no repeated responsibilities !!
			ArrayList<Responsibility> outSinRepes = new ArrayList<Responsibility>();
			for (Responsibility resp : outAux) {
				
				Boolean esta = false;
				for (Responsibility resp2 : outSinRepes) {
					if (resp.getSimpleTextForm().trim().equals(resp2.getSimpleTextForm().trim())) {
						esta = true;
					}
				}
				
				if (!esta) {
					outSinRepes.add(resp);
				}
			}
			
			// To the responsibilities detected we assign their sentence id
			for (Responsibility resp : outSinRepes) {
				resp.setSentenceID(sentenceNumber);
			}
			sentenceNumber++;
			
			// We add at the end
			out.addAll(outSinRepes);
		}

		return out;
	}

}

// Comparador Auxiliar

class Comparador implements Comparator<IndexedWord>{

	@Override
	public int compare(IndexedWord o1, IndexedWord o2) {

		return ((Integer) o1.index()).compareTo((Integer)o2.index()) ;
	}

}
