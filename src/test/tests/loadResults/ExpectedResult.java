package test.tests.loadResults;

import java.util.ArrayList;

public class ExpectedResult {

	// Variables
	
	private String reqID;
	private Integer verbPhrases;
	private ArrayList<String> expectedResponsibilities;
	
	// Constructors
	
	public ExpectedResult(String reqID, Integer verbPhrases, String resps) {
		this.setReqID(reqID);
		this.setVerbPhrases(verbPhrases);
		this.setExpectedResponsibilities(this.createResps(resps));
	}

	// Getters and Setters
	
	public String getReqID() {
		return reqID;
	}

	public void setReqID(String reqID) {
		this.reqID = reqID;
	}

	public Integer getVerbPhrases() {
		return verbPhrases;
	}

	public void setVerbPhrases(Integer verbPhrases) {
		this.verbPhrases = verbPhrases;
	}

	public ArrayList<String> getExpectedResponsibilities() {
		return expectedResponsibilities;
	}

	public void setExpectedResponsibilities(ArrayList<String> expectedResponsibilities) {
		this.expectedResponsibilities = expectedResponsibilities;
	}	
	
	// Methods
	
	private ArrayList<String> createResps(String resps){
		ArrayList<String> out = new ArrayList<String>();
		String[] splits = resps.split(";");
		for (String split : splits) {
			out.add(split);
		}
		return out;
	}
	
}
