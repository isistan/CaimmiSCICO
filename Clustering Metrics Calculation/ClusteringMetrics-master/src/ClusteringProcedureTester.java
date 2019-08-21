import java.util.ArrayList;
import java.util.HashMap;

import edu.mit.jwi.item.POS;
import input.InputReader;
import model.responsibility.Responsibility;
import utils.Pair;
import weka.core.stemmers.Stemmer;
import wordnet.WordNetInterface;

public class ClusteringProcedureTester {


	public static void main(String[] args) {

		// Input 
		// Responsibilities

		InputReader input = new InputReader();
		ArrayList<Responsibility> responsibilities = input.readResponisibilitiesFromARFF("D:\\Isistan\\SVN\\papers\\Paper Lomagno\\casos de estudio\\4 Experimento Agrupación de Responsabilidades\\Proyectos", "experimento_4_proyecto_2.arff");

		System.out.println("Responsibilities");

		for (int i = 0; i < responsibilities.size(); i++){
			Responsibility responsibility = responsibilities.get(i);
			System.out.println("  [" + (i+1) + "] " + responsibility.getCompleteResponsibility());
		}

		System.out.println("-------------------------------------------------------------------------------------------------\n");

		

		// (1)

		// I separate the existing words
		ArrayList<String> nouns = new ArrayList<String>();
		ArrayList<String> verbs = new ArrayList<String>();
		ArrayList<String> adjectives = new ArrayList<String>();
		ArrayList<String> adverbs = new ArrayList<String>();

		// Iterate about the responsibilities of entry (those of a specific project)
		for (Responsibility responsibility : responsibilities){

			// I separate each group of words according to their POS
			ArrayList<Pair<String, POS>> recognitions = responsibility.getRecognitions();
			for (int i = 0; i < recognitions.size(); i++){
				String word = recognitions.get(i).getPair1();
				POS pos = recognitions.get(i).getPair2();

				if (pos != null){
					if (pos.equals(POS.NOUN)){
						if (!nouns.contains(word))
							nouns.add(word);
					}else
						if (pos.equals(POS.VERB)){
							if (!verbs.contains(word))
								verbs.add(word);
						}else
							if (pos.equals(POS.ADJECTIVE)){
								if (!adjectives.contains(word))
									adjectives.add(word);
							}else{
								if (pos.equals(POS.ADVERB)){
									if (!adverbs.contains(word))
										adverbs.add(word);
								}
							}
				}
			}
		}

		// (2)

		// For each group of words, I do one against all, I look for if there are synonyms
		// If there are, I have to determine PARES
		// Finally, determine which is the optimal synonym and go to that same.
		// Pal_1 is synonymous with Pal_2; Pal_3 is synonymous with Pal_2, but Pal_1 is not synonymous with Pal_3 (Then turn everything to word 3)

		System.out.println("1) Groups of words");
		System.out.println("  Nouns: \t"+ nouns);
		System.out.println("  Verbs: \t"+ verbs);
		System.out.println("  Adjectives: \t"+ adjectives);
		System.out.println("  Adverbs: \t"+ adverbs);
		System.out.println("-------------------------------------------------------------------------------------------------\n");

		ArrayList<ArrayList<String>> resultsWordnetNouns = searchWordnet(nouns, POS.NOUN);
		ArrayList<ArrayList<String>> resultsWordnetVerbs = searchWordnet(verbs, POS.VERB);
		ArrayList<ArrayList<String>> resultsWordnetAdjectives = searchWordnet(adjectives, POS.ADJECTIVE);
		ArrayList<ArrayList<String>> resultsWordnetAdverbs = searchWordnet(adverbs, POS.ADVERB);

		System.out.println("2) Wordnet output");
		System.out.println("  Nouns: \t" + resultsWordnetNouns);
		System.out.println("  Verbs: \t" + resultsWordnetVerbs);
		System.out.println("  Adjectives: \t"+ resultsWordnetAdjectives);
		System.out.println("  Adverb: \t"+ resultsWordnetAdverbs);
		System.out.println("-------------------------------------------------------------------------------------------------\n");

		// (3)

		// By group of synonyms, determine which are the priority synonyms

		ArrayList<Pair<String, String>> synonymsForNouns = searchSynonyms(nouns, resultsWordnetNouns);
		ArrayList<Pair<String, String>> synonymsForVerbs = searchSynonyms(verbs, resultsWordnetVerbs);
		ArrayList<Pair<String, String>> synonymsForAdjectives = searchSynonyms(adjectives, resultsWordnetAdjectives);
		ArrayList<Pair<String, String>> synonymsForAdverbs = searchSynonyms(adverbs, resultsWordnetAdverbs);

		System.out.println("3) Search results to exchange words");
		System.out.println("  Nouns: \t" + synonymsForNouns);
		System.out.println("  Verbs: \t" + synonymsForVerbs);
		System.out.println("  Adjectives: \t"+ synonymsForAdjectives);
		System.out.println("  Adverbs: \t"+ synonymsForAdverbs);
		System.out.println("-------------------------------------------------------------------------------------------------\n");

		// (4)

		// We determine the priority words

		System.out.println("4) Word prioritization");

		HashMap<String, Integer> prioritizedNouns = prioritizeGroups(synonymsForNouns, nouns , resultsWordnetNouns);
		HashMap<String, Integer> prioritizedVerbs = prioritizeGroups(synonymsForVerbs, verbs , resultsWordnetVerbs);
		HashMap<String, Integer> prioritizedAdjectives = prioritizeGroups(synonymsForAdjectives, adjectives, resultsWordnetAdjectives);
		HashMap<String, Integer> prioritizedAdverbs = prioritizeGroups(synonymsForAdverbs, adverbs, resultsWordnetAdverbs);

		System.out.println("-------------------------------------------------------------------------------------------------\n");

		// (5)

		// We eliminate all conversions that are not necessary (priority)

		System.out.println("5) Conversion Definitions");

		HashMap<String, String> changesNouns = new HashMap<String, String>();
		HashMap<String, String> changesVerbs = new HashMap<String, String>();
		HashMap<String, String> changesAdjectives = new HashMap<String, String>();
		HashMap<String, String> changesAdverbs = new HashMap<String, String>();

		defineConvertions(synonymsForNouns, prioritizedNouns, changesNouns);
		defineConvertions(synonymsForVerbs, prioritizedVerbs, changesVerbs);
		defineConvertions(synonymsForAdjectives, prioritizedAdjectives, changesAdjectives);
		defineConvertions(synonymsForAdverbs, prioritizedAdverbs, changesAdverbs);

		System.out.println("-------------------------------------------------------------------------------------------------\n");

		// (6)
		// We make the change in all responsibilities
        // In a pair, par2 is replaced with par1

		ArrayList<Responsibility> resultsResponsibilities = new ArrayList<Responsibility>();

		for (Responsibility responsibility : responsibilities){

			Responsibility newResponsibility = performTheConvertion(changesNouns, responsibility, POS.NOUN);
			newResponsibility = performTheConvertion(changesVerbs, newResponsibility, POS.VERB);
			newResponsibility = performTheConvertion(changesAdjectives, newResponsibility, POS.ADJECTIVE);
			newResponsibility = performTheConvertion(changesAdverbs, newResponsibility, POS.ADVERB);

			resultsResponsibilities.add(newResponsibility);
		}

		System.out.println("6) Obtained responsibilities");
		for (int i = 0; i < resultsResponsibilities.size(); i++){
			System.out.println("  + ("+ (i+1) + ") " + resultsResponsibilities.get(i).getCompleteResponsibility());
		}

	}

	private static ArrayList<ArrayList<String>> searchWordnet(ArrayList<String> grupo, POS pos) {

		ArrayList<ArrayList<String>> synonymsList = new ArrayList<ArrayList<String>>();

		Stemmer stemmer = new weka.core.stemmers.SnowballStemmer();
		WordNetInterface wni = new WordNetInterface("C:");

		for (String word : grupo){

			String stemmedWord = stemmer.stem(word);
			String workingWord = null;

			if (word.equals(stemmedWord)){
				
				workingWord = word;
			}else{
				
				if (wni.isOk(word, pos)){
				
					workingWord = word;
				}else{
					if (wni.isOk(stemmedWord, pos)){
						
						workingWord = stemmedWord;
					}else{
						
					}
				}
			}

			

			ArrayList<ArrayList<String>> synonyms = null;

			try{
				synonyms = wni.getSynonyms(workingWord, pos);
			}catch (Exception e){
				
			}

			
			if (synonyms != null)
				synonymsList.add(synonyms.get(0));
			else
				synonymsList.add(null);
		}

		wni.closeConnection();

		return synonymsList;
	}
	

	private static ArrayList<Pair<String, String>> searchSynonyms(ArrayList<String> words, ArrayList<ArrayList<String>> synonymsInGroup) {
		ArrayList<Pair<String, String>> out = new ArrayList<Pair<String, String>>();

		for (int wid = 0; wid < words.size(); wid++){

			String actualWorkingWord = words.get(wid);
			ArrayList<String> allWordsExceptWID = (ArrayList<String>) words.clone();
			allWordsExceptWID.remove(wid);

			ArrayList<String> widSynonyms = synonymsInGroup.get(wid);
			if (widSynonyms !=null){
				for (String otherWord : allWordsExceptWID){

					if (widSynonyms.contains(otherWord)){
						Pair<String, String> synonymFound = new Pair<String, String>(actualWorkingWord, otherWord);
						out.add(synonymFound);
						
					}
				}
			}
		}

		return out;
	}
	
	private static HashMap<String, Integer> prioritizeGroups(ArrayList<Pair<String, String>> synonymsForNouns, ArrayList<String> nouns, ArrayList<ArrayList<String>> resultsWordnetNouns) {
		HashMap<String, Integer> out = new HashMap<String, Integer>();

		for (Pair<String, String> pair : synonymsForNouns){

			if (out.containsKey(pair.getPair1())){
				out.put(pair.getPair1(), out.get(pair.getPair1()) + 1);
			}else{
				out.put(pair.getPair1(), 1);
			}
		}

		// We proceed with those in the second pair
		for (Pair<String, String> pair : synonymsForNouns){

			if (!out.containsKey(pair.getPair2())){
				out.put(pair.getPair2(), 0);
			}
		}	


		for (String key : out.keySet()){
			System.out.println("  Key: [" + key + "] Cantidad: [" + out.get(key) + "]");
		}

		return out;
	}
	
	private static void defineConvertions(ArrayList<Pair<String, String>> synonyms,	HashMap<String, Integer> prioritized, HashMap<String, String> convertions) {
		ArrayList<Pair<String, String>> denials = new ArrayList<Pair<String, String>>();
		for (Pair<String, String> synonym : synonyms){

			Integer value1 = prioritized.get(synonym.getPair1());
			Integer value2 = prioritized.get(synonym.getPair2());

			

			if (value1 > value2){
				convertions.put(synonym.getPair2(), synonym.getPair1());
				System.out.println("  " + synonym.getPair2() + " -> " + synonym.getPair1());
			}else{
				if (value1 == value2){

					boolean continuar = true;
					for (Pair<String, String> pair : denials){
						if (pair.getPair1().equals(synonym.getPair1()) && pair.getPair2().equals(synonym.getPair2())){
							continuar = false;
						}
					}

					if (continuar){
						convertions.put(synonym.getPair2(), synonym.getPair1());
						System.out.println("  " + synonym.getPair2() + " -> " + synonym.getPair1());

						denials.add(new Pair<String, String>(synonym.getPair2(), synonym.getPair1()));
					}
				}
			}
		}			


	}

	private static Responsibility performTheConvertion(HashMap<String, String> change, Responsibility responsibility, POS pos) {

		ArrayList<Pair<String, POS>> newRecognitions = new ArrayList<Pair<String, POS>>();

		for (Pair<String, POS> recognition : responsibility.getRecognitions()){

			if (change.get(recognition.getPair1()) != null){

			
				Pair<String, POS> newRecognition = new Pair<String, POS>();
				newRecognition.setPair1(change.get(recognition.getPair1()));
				newRecognition.setPair2(recognition.getPair2());
				newRecognitions.add(newRecognition);

			}else{
				newRecognitions.add(recognition);
			}

		}

		Responsibility newResponsibility = new Responsibility(newRecognitions);
		return newResponsibility;
	}
	
}
