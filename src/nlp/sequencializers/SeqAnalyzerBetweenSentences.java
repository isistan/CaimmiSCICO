package nlp.sequencializers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.stanford.nlp.ling.CoreLabel;
import model.causalRelationship.CausalRelationship;
import model.responsibility.Responsibility;

public class SeqAnalyzerBetweenSentences {

	// Variables

	// Constructors

	public SeqAnalyzerBetweenSentences () {

	}

	// Getters and Setters

	// Methods

	private Boolean case1Matchs (List<CoreLabel> tokens) {
		Boolean out = false;

		// Casos = [OTHERWISE, IN OTHER CASE]
		if (tokens != null && tokens.size() > 1) {
			out = tokens.get(0).originalText().trim().toLowerCase().equals("otherwise") 
					|| (tokens.get(0).originalText().trim().toLowerCase().equals("in") 
							&& tokens.get(1).originalText().trim().toLowerCase().equals("other") 
							&& tokens.get(0).originalText().trim().toLowerCase().equals("case"));
		}

		return out;
	}

	private Boolean case2Matchs (List<CoreLabel> tokens) {
		Boolean out = false;

		// Casos = [THEN, NEXT, AFTER THAT, AFTERWARD, AFTERWARDS]
		if (tokens != null && tokens.size() > 1) {
			out = tokens.get(0).originalText().trim().toLowerCase().equals("then") 
					|| tokens.get(0).originalText().trim().toLowerCase().equals("next") 
					|| tokens.get(0).originalText().trim().toLowerCase().equals("afterward") 
					|| tokens.get(0).originalText().trim().toLowerCase().equals("afterwards") 
					|| (tokens.get(0).originalText().trim().toLowerCase().equals("after") 
							&& tokens.get(1).originalText().trim().toLowerCase().equals("that"));
		}

		return out;
	}

	private Boolean case3Matchs (ArrayList<CausalRelationship> before, ArrayList<CausalRelationship> here) {

		// Casos = Doble IF con condiciones negadas
		CausalRelationship first = null;
		CausalRelationship second = null;

		for (CausalRelationship cr : before) {
			if (cr.getResp1IsCondition()) {
				first = cr;
			}
		}

		for (CausalRelationship cr : before) {
			if (cr.getResp1IsCondition()) {
				second = cr;
			}
		}

		return first != null && second != null;
	}


	public ArrayList<CausalRelationship> detectSeq (ArrayList<List<CoreLabel>> tokensInSentences, ArrayList<ArrayList<Responsibility>> responsibilitiesInSentences, ArrayList<ArrayList<CausalRelationship>> relationsInSentences){
		ArrayList<CausalRelationship> out = new ArrayList<CausalRelationship>();

		Integer size = tokensInSentences.size();

		for (int index = 0; index < size; index++) {

			// Obtengo los datos de cada sentencia
			List<CoreLabel> tokens = tokensInSentences.get(index);
			ArrayList<Responsibility> responsibilities = responsibilitiesInSentences.get(index);
			ArrayList<CausalRelationship> relations = relationsInSentences.get(index);

			// Caso 1 = OTHERWISE / IN OTHER CASE
			if (case1Matchs(tokens)) {
				// Buscamos una condicion para atras
				for (int j = index - 1; j >= 0; j--) {
					for (CausalRelationship cr : relationsInSentences.get(j)) {
						if (cr.getResp1IsCondition()) {
							// Tenemos que anexar el camino de la negacion
							if (relations.size() > 0) {
								// Seteamos la segunda rama del if
								cr.setResp3(relations.get(0).getResp1());
								cr.setMatchedPattern(cr.getMatchedPattern() + "+ OTHERWISE");
							}
						}					
					}
				}				
			}

			// Caso 2 = THEN / NEXT

			if (case2Matchs(tokens)) {

				Integer encontre = 0;
				Responsibility primeraResp = null;
				Responsibility ultimaResp = null;


				// Buscamos para atras
				for (int j = index - 1; j >= 0 && encontre == 0; j--) {
					ArrayList<Responsibility> respsInOtherSentene = new ArrayList<Responsibility>();

					for (Responsibility r : responsibilitiesInSentences.get(j)) {
						respsInOtherSentene.add(r);	
					}

					// Las ordenamos y nos quedamos con la ultima
					if (respsInOtherSentene.size() > 0) {
						Collections.sort(respsInOtherSentene);
						ultimaResp = respsInOtherSentene.get(respsInOtherSentene.size() - 1);
						encontre++;
					}
				}

				//Buscamos para adelante
				for (int j = index; j < size && encontre == 1; j++) {
					ArrayList<Responsibility> respsInOtherSentene = new ArrayList<Responsibility>();
					for (Responsibility r : responsibilitiesInSentences.get(j)) {
						respsInOtherSentene.add(r);	
					}

					// Las ordenamos y nos quedamos con la ultima
					if (respsInOtherSentene.size() > 0) {
						Collections.sort(respsInOtherSentene);
						primeraResp = respsInOtherSentene.get(0);
						encontre++;
					}
				}

				if (encontre == 2) {
					CausalRelationship toAdd = new CausalRelationship(ultimaResp, primeraResp);
					toAdd.setMatchedPattern("THEN / NEXT");
					out.add(toAdd);
				}

			}

		}

		// Caso 3
		for (int index = 1; index < size; index++) {

			// Obtengo los datos de cada sentencia
			ArrayList<CausalRelationship> relationsBefore = relationsInSentences.get(index - 1);
			ArrayList<CausalRelationship> relationsHere = relationsInSentences.get(index);

			if (case3Matchs(relationsBefore, relationsHere)) {

				// Busco las relaciones que estan en la condicion de cada sentencia
				CausalRelationship first = null;
				CausalRelationship second = null;

				for (CausalRelationship cr : relationsBefore) {
					if (cr.getResp1IsCondition() && cr.getMatchedPattern().equals("IF")) {
						first = cr;
						break;
					}
				}

				for (CausalRelationship cr : relationsHere) {
					if (cr.getResp1IsCondition() && cr.getMatchedPattern().equals("IF")) {
						second = cr;
						break;
					}
				}

				if (first != null && second != null) {

					Responsibility  r1 = first.getResp1();
					Responsibility  r2 = second.getResp1();

					Boolean aux1 = r1.getVerbBaseForm().toLowerCase().trim().equals(r2.getVerbBaseForm().toLowerCase().trim());
					Boolean aux2 = r1.getDobj().toLowerCase().trim().equals(r2.getDobj().toLowerCase().trim());

					if (aux1 && aux2) {
						first.setResp3(second.getResp2());
					}

				}

			}		


		}


		return out;
	}

}