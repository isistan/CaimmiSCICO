package wordnet;

import java.util.ArrayList;

import edu.mit.jwi.item.POS;
import weka.core.stemmers.Stemmer;

public class testClass {


	public static void main(String[] args) {

		Stemmer stemmer = new weka.core.stemmers.SnowballStemmer();
		WordNetInterface wni = new WordNetInterface("C:");
		POS pos = POS.NOUN;

		ArrayList<String> words = new ArrayList<String>();
		words.add("course");
		words.add("classes");

		for (String word : words){

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
						
						System.err.println("Neither word is valid");
						
					}
				}
			}

			System.out.println("Word: \""  + word + "\"");
			System.out.println("Stem: \""  + stemmedWord + "\"");
			System.out.println("Working Word : " + workingWord);

			System.out.println("\n=== Test Word ===\n");
			wni.testWord(workingWord);

			
			System.out.println("\n=== getHyperonyms ===\n");
			wni.getHyperonyms(workingWord, POS.NOUN);

		}

		wni.closeConnection();
	}

}
