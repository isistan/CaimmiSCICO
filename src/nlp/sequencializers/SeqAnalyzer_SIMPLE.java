package nlp.sequencializers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.util.CoreMap;
import model.causalRelationship.CausalRelationship;
import model.responsibility.Responsibility;
import utils.misc.Pair;

public class SeqAnalyzer_SIMPLE extends SeqAnalyzer {

	// Variables

	// Constructors

	public SeqAnalyzer_SIMPLE() {
		super();
		ArrayList<SimplePattern> patterns = new ArrayList<SimplePattern>();
		patterns.add(new SimplePattern("<R_A> [TO] <R_B>"));					// R_A -> R_B
		patterns.add(new SimplePattern("<R_A> [IN] [ORDER] [TO] <R_B>"));		// R_A -> R_B
		patterns.add(new SimplePattern("<R_A> [FOR] <R_B>"));					// R_A -> R_B
		patterns.add(new SimplePattern("<R_A> [BY] <R_B>")); 					// R_B -> R_A
		patterns.add(new SimplePattern("<R_A> [THEN] <R_B>"));				// R_A -> R_B
		patterns.add(new SimplePattern("<R_A> [NEXT] <R_B>"));				// R_A -> R_B
		patterns.add(new SimplePattern("<R_A> [AND] <R_B>"));				// R_A -> R_B
		super.setPatterns(patterns);
	}
	// Getters and Setters

	// Methods

	@Override
	public ArrayList<CausalRelationship> detectSeq(Integer id, String sentence, CoreMap coreMap, List<CoreLabel> tokens, ArrayList<Responsibility> responsibilitiesInSentence) {
		ArrayList<CausalRelationship> out = new ArrayList<CausalRelationship>();

		Integer retry = 1;

		// Primero interpretar los patrones
		for (SimplePattern pattern : this.patterns) {

			// Aca esta enganchando una sola vez.. tendria que seguir devolviendo matchs !
			Boolean resultMatched = true;
			while (resultMatched) {
				resultMatched = pattern.match(tokens, retry++);

				if (resultMatched) {
					ArrayList<String> matched = pattern.getMatch();
					System.out.println("\t\t+ Hubo Match [" + pattern.toString() + "] = " + matched);

					// Devolver las delimitaciones y ahi jugar con las responsabilidades en donde caen
					ArrayList<Pair<String, ArrayList<Responsibility>>> result = calculateWhereResponsibilitiesFall(matched, pattern, responsibilitiesInSentence);

					// Recorremos el resultado generando las relaciones causales
					Integer indexDelim_1 = null;
					Integer index = 0;
					for (Pair<String, ArrayList<Responsibility>> r : result) {

						if (r.getPair1() != null && r.getPair1().equals(pattern.getStructuredDelimiters().get(0).toUpperCase())) {
							indexDelim_1 = index;
						}

						index++;
					}
					ArrayList<Responsibility> respsInRight = null;
					ArrayList<Responsibility> respsInLeft = null;

					if (pattern.getStructuredDelimiters().size() == 1) {
						respsInRight = result.get(indexDelim_1 + 1).getPair2();
						respsInLeft = result.get(indexDelim_1 - 1).getPair2();
					}else {
						// IN ORDER TO 
						respsInRight = result.get(indexDelim_1 + 3).getPair2();
						respsInLeft = result.get(indexDelim_1 - 1).getPair2();
					}

					// Se hacen todas las combinaciones
					if (pattern.getStructuredDelimiters().get(0).equals("[BY]")) {
						for (Responsibility resp1 : respsInRight){
							for (Responsibility resp2 : respsInLeft){
								CausalRelationship toAdd = new CausalRelationship(resp1, resp2);
								toAdd.setMatchedPattern(pattern.patternTxt);
								out.add(toAdd);
							}
						}
					} else {
						if (pattern.getStructuredDelimiters().get(0).equals("[AND]")){

							if (respsInLeft.size() > 0 && respsInRight.size() > 0) {
								Collections.sort(respsInLeft);
								Collections.sort(respsInRight);

								Responsibility respL = respsInLeft.get(respsInLeft.size() - 1);
								Responsibility respR = respsInRight.get(respsInRight.size() - 1);

								CausalRelationship toAdd = new CausalRelationship(respL, respR);
								toAdd.setMatchedPattern(pattern.patternTxt);
								out.add(toAdd);
							}

						}else {
							for (Responsibility resp1 : respsInRight){
								for (Responsibility resp2 : respsInLeft){
									CausalRelationship toAdd = new CausalRelationship(resp2, resp1);
									toAdd.setMatchedPattern(pattern.patternTxt);
									out.add(toAdd);
								}
							}
						}


						// Si hay repetidas hay que borrarlas
						ArrayList<CausalRelationship> outAux = eliminateRepetitions(out);

						// Reconfiguro el out				
						out = outAux;

					}
				}else {
					retry = 1;
				}
			}

		}
		return out;
	}

}
