package model.requirement;

import java.util.ArrayList;

import model.causalRelationship.CausalRelationship;
import model.responsibility.Responsibility;
import model.responsibility.SimpleResponsibility;

public class Requirement {

	// Variables
	
	private String id;
	private String text;
	private ArrayList<Responsibility> responsabilities;
	private ArrayList<CausalRelationship> causalRelationships;
	
	// Constructors
	
	public Requirement(){
		this("","");
	}
	
	public Requirement(String text){
		this("",text);
	}
	
	public Requirement(String id, String text) {
		super();
		this.text = text;
		this.id = id;
		this.responsabilities = new ArrayList<Responsibility>();
		this.causalRelationships = new ArrayList<CausalRelationship>();
	}

	// Getters and Setters
	
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public ArrayList<CausalRelationship> getCausalRelationships() {
		return causalRelationships;
	}

	public void setCausalRelationships(ArrayList<CausalRelationship> causalRelationships) {
		this.causalRelationships = causalRelationships;
	}

	public ArrayList<Responsibility> getResponsibilities(){
		return this.responsabilities;
	}
	
	public ArrayList<Responsibility> getResponsibilities(Integer sentenceNumber){
		ArrayList<Responsibility> out = new ArrayList<Responsibility>();
		for (Responsibility resp : this.responsabilities) {
			if (resp.getSentenceID() == sentenceNumber) {
				out.add(resp);
			}
		}
		return out;
	}
	
	public void setResponsabilities(ArrayList<Responsibility> responsabilities){
		this.responsabilities = responsabilities;
	}
	
	public void addAllResponsabilities(ArrayList<Responsibility> responsabilities){
		this.responsabilities.addAll(responsabilities);
	}	
	
	// Methods
	
	
	
}
