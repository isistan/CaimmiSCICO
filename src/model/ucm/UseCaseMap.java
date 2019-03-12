package model.ucm;

import java.util.ArrayList;

import model.causalRelationship.CausalRelationship;
import model.conceptualComponent.ConceptualComponent;
import model.responsibility.Responsibility;

public class UseCaseMap {

	// Variables
	
	private ArrayList<Responsibility> responsibilities;
	private ArrayList<ConceptualComponent> components;
	private ArrayList<CausalRelationship> relations;
	
	
	// Constructors
	
	public UseCaseMap() {
	
	}
	
	// Getters and Setters
	
	public ArrayList<Responsibility> getResponsibilities() {
		return responsibilities;
	}


	public void setResponsibilities(ArrayList<Responsibility> responsibilities) {
		this.responsibilities = responsibilities;
	}


	public ArrayList<ConceptualComponent> getComponents() {
		return components;
	}


	public void setComponents(ArrayList<ConceptualComponent> components) {
		this.components = components;
	}


	public ArrayList<CausalRelationship> getRelations() {
		return relations;
	}


	public void setRelations(ArrayList<CausalRelationship> relations) {
		this.relations = relations;
	}
		
	// Methods
	
}
