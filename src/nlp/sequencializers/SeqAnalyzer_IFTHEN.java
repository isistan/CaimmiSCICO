package nlp.sequencializers;

import java.util.ArrayList;
import java.util.List;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.util.CoreMap;
import model.causalRelationship.CausalRelationship;
import model.responsibility.Responsibility;
import utils.misc.Pair;

public class SeqAnalyzer_IFTHEN extends SeqAnalyzer {

	// Variables

	// Constructors

	public SeqAnalyzer_IFTHEN() {
		super();
		ArrayList<SimplePattern> patterns = new ArrayList<SimplePattern>();
		patterns.add(new SimplePattern("[IF] <...> [THEN] <...>"));
		patterns.add(new SimplePattern("[IF] <...> [,] <...>"));
		super.setPatterns(patterns);
	}

	// Getters and Setters

	// Methods

	@Override
	public ArrayList<CausalRelationship> detectSeq(Integer id, String sentence, CoreMap coreMap, List<CoreLabel> tokens, ArrayList<Responsibility> responsibilitiesInSentence) {
		ArrayList<CausalRelationship> out = new ArrayList<CausalRelationship>();

		// Primero interpretar los patrones
		for (SimplePattern pattern : this.patterns) {

			Boolean resultMatched = pattern.match(tokens);
								   
			if (resultMatched) {
				ArrayList<String> matched = pattern.getMatch();
				System.out.println("\t\t+ Hubo Match [" + pattern.toString() + "] = " + matched);
				
				// Devolver las delimitaciones y ahi jugar con las responsabilidades en donde caen
				ArrayList<Pair<String, ArrayList<Responsibility>>> result = calculateWhereResponsibilitiesFall(matched, pattern, responsibilitiesInSentence);
				
				// Recorremos el resultado generando las relaciones causales
				Integer indexIF = null;
				Integer indexTHEN = null;
				Integer index = 0;
				for (Pair<String, ArrayList<Responsibility>> r : result) {
					
					if (r.getPair1() != null && r.getPair1().equals("[IF]")) {
						indexIF = index;
					}
					
					if (r.getPair1() != null && (r.getPair1().equals("[THEN]") || r.getPair1().equals("[,]"))) {
						indexTHEN = index;
					}
					
					index++;
				}
				ArrayList<Responsibility> respsInConditionalClause = result.get(indexIF + 1).getPair2();
				ArrayList<Responsibility> respsInConsequentClause = result.get(indexTHEN + 1).getPair2();
				
				// Se hacen todas las combinaciones
				for (Responsibility resp1 : respsInConditionalClause){
					for (Responsibility resp2 : respsInConsequentClause){
						CausalRelationship crToAdd = new CausalRelationship(resp1, resp2);
						crToAdd.setResp1IsCondition(true);
						crToAdd.setMatchedPattern(pattern.patternTxt);
						out.add(crToAdd);
					}
				}
				
				// Si hay repetidas hay que borrarlas
				ArrayList<CausalRelationship> outAux = eliminateRepetitions(out);
				
				// Reconfiguro el out				
				out = outAux;
			
			}
		}

		return out;
	}

	

}