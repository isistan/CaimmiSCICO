package main;

import edu.stanford.nlp.ling.WordTag;
import edu.stanford.nlp.process.Morphology;

public class PruebaVerbBaseForm {

	
	public static String getBaseForm(String wordStr, String POSTag) {
		WordTag result = Morphology.stemStatic(wordStr, POSTag);
		return result.value();
	}
	
	public static void main(String[] args) {
				
		System.out.println(getBaseForm("walking", "VB"));	
		
	}
}
