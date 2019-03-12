package nlp.sequencializers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.util.CoreMap;
import model.causalRelationship.CausalRelationship;
import model.responsibility.Responsibility;
import nlp.NLPConnector;
import utils.misc.Pair;

public abstract class SeqAnalyzer {

	// Variables

	protected ArrayList<SimplePattern> patterns;

	// Constructors

	public SeqAnalyzer() {

	}

	// Getters and Setters

	public ArrayList<SimplePattern> getPatterns() {
		return patterns;
	}

	public void setPatterns(ArrayList<SimplePattern> patterns) {
		this.patterns = patterns;
	}	

	// Methods

	public abstract ArrayList<CausalRelationship> detectSeq(Integer id, String sentence, CoreMap coreMap, List<CoreLabel> tokens, ArrayList<Responsibility> responsibilitiesInSentence);

	protected ArrayList<Pair<String, ArrayList<Responsibility>>> calculateWhereResponsibilitiesFall(ArrayList<String> matched, SimplePattern pattern, ArrayList<Responsibility> responsibilitiesInSentence) {
		ArrayList<Pair<String, ArrayList<Responsibility>>> out = new ArrayList<Pair<String, ArrayList<Responsibility>>>();

		// Ordenar responsabilidades
		Collections.sort(responsibilitiesInSentence);

		// Ver en donde caen
		ArrayList<Integer> delimiterIndex = new ArrayList<Integer>();
		ArrayList<String> delimiters = new ArrayList<String>();

		// Donde hayan <...> solamente pueden haber responsabilidades
		ArrayList<String> sepPattern = pattern.getSeparatedPattern();

		// Tenemos que sacar las posiciones en donde se ubican los delimitadores
		Integer index = 1;
		for (String token : matched) {
			// Si es un delimitador
			if (token.startsWith("[")) {
				delimiterIndex.add(index);
				delimiters.add(token);
			}
			index++;
		}

		// Recorremos el pattern, y a medida que vamos avanznado vamos conformando el resultado out

		Integer index_actualDelim = 0;
		Integer index_lastDelim = 0;
		Boolean viUnDelimitador = false;

		for (String term : sepPattern) {

			String termType = term.substring(0, 1);

			switch (termType) {
			// Delimitador
			case "[" :{

				// Agregamos al resultado la indicacion de un delimitador
				index_lastDelim = index_actualDelim;
				out.add(new Pair<String, ArrayList<Responsibility>>(delimiters.get(index_actualDelim++), null));	
				viUnDelimitador = true;

				break;
			}
			// Zona de responsabilidades
			case "<" :{

				// Tenemos que agarrar todas las responsabilidades considerando el ultimo delimitador y el proximo

				ArrayList<Responsibility> resp2 = new ArrayList<Responsibility>();

				// Verificamos que responsabilidades caen dentro de esta zona
				if (viUnDelimitador) {
					for (Responsibility resp : responsibilitiesInSentence) {

						Integer indexVerb = resp.getIndex(resp.getVerbToken());
						Integer indexDobj = resp.getIndex(resp.getDobjToken());

						Boolean aux1 = delimiterIndex.get(index_lastDelim) < indexVerb;
						Boolean aux2 = delimiterIndex.get(index_lastDelim) < indexDobj;
						Boolean aux3 = false;
						Boolean aux4 = false;
						if (index_actualDelim < delimiterIndex.size()) {
							aux3 = indexVerb < delimiterIndex.get(index_actualDelim);
							aux4 = indexDobj < delimiterIndex.get(index_actualDelim);
						}else {
							aux3 = true;
							aux4 = true;
						}

						if (aux1 && aux2 && aux3 && aux4) {
							resp2.add(resp);					
						}
					}
				}else {
					for (Responsibility resp : responsibilitiesInSentence) {

						Integer indexVerb = resp.getIndex(resp.getVerbToken());
						Integer indexDobj = resp.getIndex(resp.getDobjToken());

						Boolean aux1 = indexVerb < delimiterIndex.get(index_lastDelim);
						Boolean aux2 = indexDobj < delimiterIndex.get(index_lastDelim);

						if (aux1 && aux2) {
							resp2.add(resp);					
						}
					}

				}

				out.add(new Pair<String, ArrayList<Responsibility>>(null, resp2));

				break;
			}
			// Zona de cualquier cosa que no interesa
			case "(" :{

				// No consideramos a las responsabilides que caen dentro de este recinto
				out.add(new Pair<String, ArrayList<Responsibility>>(null, null));

				break;
			}
			default:{

			}
			}

		}

		return out;
	}

	public static ArrayList<CausalRelationship> eliminateRepetitions(ArrayList<CausalRelationship> out) {
		// Meter en out si no es que ya existen
		ArrayList<CausalRelationship> outAux = new ArrayList<CausalRelationship>();
		for (CausalRelationship cr1 : out) {
			Boolean esta = false;
			for (CausalRelationship cr2 : outAux) {
				if (cr1.getResp1().getSimpleTextForm().equals(cr2.getResp1().getSimpleTextForm()) && cr1.getResp2().getSimpleTextForm().equals(cr2.getResp2().getSimpleTextForm())) {
					esta = true;
				}

			}
			if (!esta) {
				outAux.add(cr1);
			}
		}
		return outAux;
	}
	
	public static ArrayList<CausalRelationship> eliminateJumperRelations(ArrayList<CausalRelationship> relations) {
		
		// Meter en out si no es que ya existen
		ArrayList<CausalRelationship> out = new ArrayList<CausalRelationship>();
		HashMap<String, ArrayList<String>> outAux = new HashMap<String, ArrayList<String>>();
		
		for (CausalRelationship cr : relations) {
			
			if (outAux.containsKey(cr.getResp1().getShortForm())) {
				ArrayList<String> destinations = outAux.get(cr.getResp1().getShortForm());
				destinations.add(cr.getResp2().getShortForm());
			}else {
				ArrayList<String> toAdd = new ArrayList<String>();
				toAdd.add(cr.getResp2().getShortForm());
				outAux.put(cr.getResp1().getShortForm(), toAdd);
			}
			
		}
		
		// En este punto outAux tiene la referencia a cada Responsabilidad y sus siguientes.
		ArrayList<String> crsToCancel = new ArrayList<String>();
		
		for (String base : outAux.keySet()) {
			ArrayList<String> destinationsA = outAux.get(base);
			for (String destinyA : destinationsA) {
				ArrayList<String> destinationsB = outAux.get(destinyA);
				if (destinationsB != null) {
					for (String destinyB : destinationsB) {
						if (destinationsA.contains(destinyB)) {
							// Este caso hay que cancelarlo
							crsToCancel.add(base + " -> " + destinyB);
						}
					}
				}
			}
		}
		
		for (CausalRelationship cr : relations) {
			
			if (!crsToCancel.contains(cr.toString())){
				out.add(cr);
			}
		}			
			
		return out;
	}



}
