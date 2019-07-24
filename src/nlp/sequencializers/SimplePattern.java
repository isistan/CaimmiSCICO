package nlp.sequencializers;

import java.util.ArrayList;
import java.util.List;

import edu.stanford.nlp.ling.CoreLabel;

public class SimplePattern {

	// Variables

	protected String patternTxt;
	protected ArrayList<String> separatedPattern;
	protected ArrayList<String> delimiters;
	protected ArrayList<String> structuredDelimiters;
	protected ArrayList<String> match;

	// Constructors

	public SimplePattern(String patternTxt) {
		this.patternTxt = patternTxt;
		this.match = new ArrayList<String>();
		if (this.patternTxt != null) {
			separatePattern();
		}
	}

	// Getters and Setters

	public ArrayList<String> getMatch() {
		return this.match;		
	}

	public ArrayList<String> getDelimiters(){
		return this.delimiters;
	}

	public ArrayList<String> getStructuredDelimiters(){
		return this.structuredDelimiters;
	}

	public ArrayList<String> getSeparatedPattern(){
		return this.separatedPattern;
	}

	// Methods

	private void separatePattern() {
		this.separatedPattern = new ArrayList<String>();
		this.delimiters = new ArrayList<String>();
		this.structuredDelimiters = new ArrayList<String>();
		
		String[] split = this.patternTxt.split(" ");
		for (String s : split) {
			this.separatedPattern.add(s.trim().toLowerCase());

			if (s.startsWith("[") && s.endsWith("]")) {
				String delimiter = s.substring(1, s.length() - 1).toLowerCase().trim();
				delimiters.add(delimiter);
				structuredDelimiters.add(s.trim().toUpperCase());
			}
		}
	}

	public Boolean match(List<CoreLabel> tokens) {

		Boolean out = false;
		
		// We look for delimiters
		
		match = new ArrayList<String>();
		Integer delimiterIndex = 0;

		for (int idToken = 0; idToken < tokens.size(); idToken++) {

			CoreLabel token = tokens.get(idToken);

			if (delimiterIndex == delimiters.size()) {
				match.add(token.toString().toLowerCase().trim());
			}else
				// If I found a delimiter
				if (token.originalText().toLowerCase().trim().equals(delimiters.get(delimiterIndex))) {
					match.add("[" + delimiters.get(delimiterIndex).toUpperCase() + "]");
					delimiterIndex++;
				}else {
					match.add(token.toString().toLowerCase().trim());
				}
		}

		if (delimiterIndex >= delimiters.size()) {
			out = true;
		}

		return out;
	}

	// FIXME THIS TRY TO DO IT WITH THE RETRY
	public Boolean match(List<CoreLabel> tokens, Integer retry) {

		Boolean out = false;
		
		//We look for delimiters
		
		match = new ArrayList<String>();
		Integer delimiterIndex = 0;

		for (int idToken = 0; idToken < tokens.size(); idToken++) {

			CoreLabel token = tokens.get(idToken);

			if (delimiterIndex == delimiters.size()) {
				match.add(token.toString().toLowerCase().trim());
			}else
				// If I found a delimiter
				if (token.originalText().toLowerCase().trim().equals(delimiters.get(delimiterIndex)) && retry-- == 1) {
					match.add("[" + delimiters.get(delimiterIndex).toUpperCase() + "]");
					delimiterIndex++;
				}else {
					match.add(token.toString().toLowerCase().trim());
				}
		}

		if (delimiterIndex >= delimiters.size()) {
			out = true;
		}

		return out;
	}

	
	@Override
	public String toString() {
		return this.patternTxt;
	}

}
