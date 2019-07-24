package nlp.sequencializers;

import java.util.ArrayList;
import java.util.List;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.util.CoreMap;
import model.causalRelationship.CausalRelationship;
import model.responsibility.Responsibility;
import utils.misc.Pair;

public class SeqAnalyzer_TEMPORAL extends SeqAnalyzer {

	// Variables

	// Constructors

	public SeqAnalyzer_TEMPORAL() {
		super();
		ArrayList<SimplePattern> patterns = new ArrayList<SimplePattern>();
		patterns.add(new SimplePattern("[AFTER] <...> [,] <...>"));
		patterns.add(new SimplePattern("[BEFORE] <...> [,] <...>")); 
		patterns.add(new SimplePattern("[ONCE] <...> [,] <...>"));
		patterns.add(new SimplePattern("[WHEN] <...> [,] <...>"));
		patterns.add(new SimplePattern("[UPON] <...> [,] <...>"));
		patterns.add(new SimplePattern("[AS] [SOON] [AS] <...> [,] <...>"));
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
				Integer indexDelim_2 = null;
				Integer index = 0;
				for (Pair<String, ArrayList<Responsibility>> r : result) {

					if (r.getPair1() != null && r.getPair1().equals(pattern.getStructuredDelimiters().get(0).toUpperCase())) {
						indexDelim_1 = index;
					}

					if (pattern.getDelimiters().size() == 2 && r.getPair1() != null && r.getPair1().equals(pattern.getStructuredDelimiters().get(1).toUpperCase())) {
						indexDelim_2 = index;
					}

					// AS SOON AS
					if (pattern.getDelimiters().size() > 2 && r.getPair1() != null && r.getPair1().equals(pattern.getStructuredDelimiters().get(3).toUpperCase())) {
						indexDelim_2 = index;
					}

					index++;
				}
				ArrayList<Responsibility> respsInTemporalClause = null;
				ArrayList<Responsibility> respsInIndependentClause = null;
				respsInTemporalClause = result.get(indexDelim_1 + 1).getPair2();
				respsInIndependentClause = result.get(indexDelim_2 + 1).getPair2();


				// All combinations are made
				if (pattern.getStructuredDelimiters().get(0).equals("[BEFORE]")) {
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
