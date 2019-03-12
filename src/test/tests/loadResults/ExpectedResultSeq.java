package test.tests.loadResults;

import java.util.ArrayList;

public class ExpectedResultSeq {

	// Variables
	
	private String reqID;
	private ArrayList<String> expectedResponsibilities;
	private ArrayList<String> expectedCausalRelationships;
	
	// Constructors
	
	public ExpectedResultSeq(String reqID, String resps, String causalRelations) {
		this.setReqID(reqID);
		this.setExpectedResponsibilities(this.createResps(resps));
		this.setExpectedCausalRelationships(this.createCausalRelations(causalRelations));
	}

	// Getters and Setters
	
	public String getReqID() {
		return reqID;
	}

	public void setReqID(String reqID) {
		this.reqID = reqID;
	}

	public ArrayList<String> getExpectedResponsibilities() {
		return expectedResponsibilities;
	}

	public void setExpectedResponsibilities(ArrayList<String> expectedResponsibilities) {
		this.expectedResponsibilities = expectedResponsibilities;
	}
	
	public ArrayList<String> getExpectedCausalRelationships() {
		return expectedCausalRelationships;
	}

	public void setExpectedCausalRelationships(ArrayList<String> expectedCausalRelationships) {
		this.expectedCausalRelationships = expectedCausalRelationships;
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
	

	private ArrayList<String> createCausalRelations(String causalRelations) {
		ArrayList<String> out = new ArrayList<String>();
		
		String[] splits = causalRelations.split(";");
		for (String split : splits) {
			out.add(split);
		}
		
		return out;
	}
}
