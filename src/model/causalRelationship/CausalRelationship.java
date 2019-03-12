package model.causalRelationship;

import model.responsibility.Responsibility;

public class CausalRelationship {

	// Variables
	private Responsibility resp1;
	private Responsibility resp2;
	private Boolean resp1IsCondition;
	private Responsibility resp3;
	private String matchedPattern;

	// Constructors

	public CausalRelationship(Responsibility resp1, Responsibility resp2){
		this.resp1 = resp1;
		this.resp2 = resp2;
		this.resp1IsCondition= false;
		this.resp3 = null;
		this.matchedPattern = null;
	}

	// Getters and Setters

	public Responsibility getResp1() {
		return resp1;
	}

	public void setResp1(Responsibility resp1) {
		this.resp1 = resp1;
	}

	public Responsibility getResp2() {
		return resp2;
	}

	public void setResp2(Responsibility resp2) {
		this.resp2 = resp2;
	}

	public Boolean getResp1IsCondition() {
		return resp1IsCondition;
	}

	public void setResp1IsCondition(Boolean resp1IsCondition) {
		this.resp1IsCondition = resp1IsCondition;
	}

	public Responsibility getResp3() {
		return resp3;
	}

	public void setResp3(Responsibility resp3) {
		this.resp3 = resp3;
	}

	public String getMatchedPattern() {
		return matchedPattern;
	}

	public void setMatchedPattern(String matchedPattern) {
		this.matchedPattern = matchedPattern;
	}

	// Methods

	@Override
	public String toString() {
		if (this.resp2 == null)
			return getResp1().getShortForm() + " -> NULL";
		else
			if (this.resp3 == null)
				return getResp1().getShortForm() + " -> " + getResp2().getShortForm();
			else
				return getResp1().getShortForm() + " -> " + getResp2().getShortForm() + " !-> " + getResp3().getShortForm();
	}
}
