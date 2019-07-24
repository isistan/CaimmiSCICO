package clustering.wordnet;

import java.util.ArrayList;

import edu.mit.jwi.item.POS;
import weka.core.stemmers.Stemmer;

public class testClass {


	public static void main(String[] args) {

		Stemmer stemmer = new weka.core.stemmers.SnowballStemmer();
		WordNetInterface wni = new WordNetInterface(".\\wordnet");
		POS pos = POS.NOUN;

		ArrayList<String> words = new ArrayList<String>();
		words.add("course");
		words.add("classes");

		for (String word : words){

			String stemmedWord = stemmer.stem(word);
			String workingWord = null;

			if (word.equals(stemmedWord)){
				// They are equal then they are treated the same
				workingWord = word;
			}else{
				// They are not the same, you try first with the original version
				if (wni.isOk(word, pos)){
					// we keep on working with word
					workingWord = word;
				}else{
					if (wni.isOk(stemmedWord, pos)){
						// we continue with stemmedWord
						workingWord = stemmedWord;
					}else{
						//Both failed ..
						System.err.println("Neither word is valid");
						// There would be no disambiguation for that word
					}
				}
			}

			System.out.println("Word: \""  + word + "\"");
			System.out.println("Stem: \""  + stemmedWord + "\"");
			System.out.println("Working Word : " + workingWord);

			System.out.println("\n=== Test Word ===\n");
			wni.testWord(workingWord);

			//System.out.println("\n=== getSynonyms ===\n");

			/*	ArrayList<ArrayList<String>> synonyms = wni.getSynonyms(workingWord, POS.NOUN);

			for (int i = 0; i < synonyms.size(); i++){
				System.out.println("Meaning " + i);
				System.out.println("\t" + synonyms.get(i).toString() + "\n");
			}
			 */
			System.out.println("\n=== getHyperonyms ===\n");
			wni.getHyperonyms(workingWord, POS.NOUN);

		}

		wni.closeConnection();
	}

}
