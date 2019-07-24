package nlp.sequencializers;

import java.util.ArrayList;
import java.util.List;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.util.CoreMap;
import model.causalRelationship.CausalRelationship;
import model.responsibility.Responsibility;
import utils.misc.Pair;

public class SeqAnalyzer_TEMPORALINVERTED extends SeqAnalyzer {

	// Variables

	// Constructors

	public SeqAnalyzer_TEMPORALINVERTED() {
		super();
		ArrayList<SimplePattern> patterns = new ArrayList<SimplePattern>();
		patterns.add(new SimplePattern("<R_A> [AFTER] <R_B>")); 			// R_B -> R_A
		patterns.add(new SimplePattern("<R_A> [BEFORE] <R_B>")); 			// R_A -> R_B
		patterns.add(new SimplePattern("<R_A> [ONCE] <R_B>")); 			// R_B -> R_A
		patterns.add(new SimplePattern("<R_A> [WHEN] <R_B>")); 			// R_B -> R_A
		patterns.add(new SimplePattern("<R_A> [UPON] <R_B>")); 			// R_B -> R_A
		patterns.add(new SimplePattern("<R_A> [AS] [SOON] [AS] <R_B>"));	// R_B -> R_A
		patterns.add(new SimplePattern("<R_A> [UNTIL] <R_B>")); 			// R_A -> R_B
		super.setPatterns(patterns);
	}

	// Getters and Setters

	// Methods

	@Override
	public ArrayList<CausalRelationship> detectSeq(Integer id, String sentence, CoreMap coreMap, List<CoreLabel> tokens, ArrayList<Responsibility> responsibilitiesInSentence) {
		ArrayList<CausalRelationship> out = new ArrayList<CausalRelationship>();

		// First interpret the patterns
		for (SimplePattern pattern : this.patterns) {

			Boolean resultMatched = pattern.match(tokens);

			if (resultMatched) {
				ArrayList<String> matched = pattern.getMatch();
				System.out.println("\t\t+ Hubo Match [" + pattern.toString() + "] = " + matched);
				
				// Return the boundaries and then play with the responsibilities where they fall
				ArrayList<Pair<String, ArrayList<Responsibility>>> result = calculateWhereResponsibilitiesFall(matched, pattern, responsibilitiesInSentence);

				// We traverse the result generating causal relationships
				Integer indexDelim_1 = null;
				Integer index = 0;
				for (Pair<String, ArrayList<Responsibility>> r : result) {

					if (r.getPair1() != null && r.getPair1().equals(pattern.getStructuredDelimiters().get(0).toUpperCase())) {
						indexDelim_1 = index;
					}

					index++;
				}
				ArrayList<Responsibility> respsInTemporalClause = null;
				ArrayList<Responsibility> respsInIndependentClause = null;
				if (pattern.getStructuredDelimiters().size() == 1) {
					respsInTemporalClause = result.get(indexDelim_1 + 1).getPair2();
					respsInIndependentClause = result.get(indexDelim_1 - 1).getPair2();
				}else {
					// AS SOON AS
					respsInTemporalClause = result.get(indexDelim_1 + 1).getPair2();
					respsInIndependentClause = result.get(indexDelim_1 - 3).getPair2();
				}

				// All combinations are made
				if (pattern.getStructuredDelimiters().get(0).equals("[BEFORE]") || pattern.getStructuredDelimiters().get(0).equals("[UNTIL]")) {
					for (Responsibility resp1 : respsInTemporalClause){
						for (Responsibility resp2 : respsInIndependentClause){
							CausalRelationship toAdd = new CausalRelationship(resp2, resp1);
							toAdd.setMatchedPattern(pattern.patternTxt);
							out.add(toAdd);
						}
					}
				} else {
					for (Responsibility resp1 : respsInTemporalClause){
						for (Responsibility resp2 : respsInIndependentClause){
							CausalRelationship toAdd = new CausalRelationship(resp1, resp2);
							toAdd.setMatchedPattern(pattern.patternTxt);
							out.add(toAdd);
						}
					}
				}

				// If there are repeated you have to delete them
				ArrayList<CausalRelationship> outAux = eliminateRepetitions(out);

				// I reconfigured the out				
				out = outAux;

			}
		}

		return out;
	}

}
