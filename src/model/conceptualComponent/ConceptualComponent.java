package model.conceptualComponent;

import java.util.ArrayList;

import model.responsibility.Responsibility;

public class ConceptualComponent {

	// Variables
	private String name;
	private ArrayList<Responsibility> responsibilities;
	
	// Constructors
	
	public ConceptualComponent(){
		this("", new ArrayList<Responsibility>());
	}
	
	public ConceptualComponent(ArrayList<Responsibility> responsibilities){
		this("", responsibilities);
	}

	public ConceptualComponent(String name, ArrayList<Responsibility> responsibilities){
		this.name = name;
		this.responsibilities= responsibilities;
	}
	
	// Getters and Setters
	
	public ArrayList<Responsibility> getResponsibilities() {
		return responsibilities;
	}

	public void setResponsibilities(ArrayList<Responsibility> responsibilities) {
		this.responsibilities = responsibilities;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	// Methods
	
	public void addResponsibility(Responsibility responsibility){
		this.responsibilities.add(responsibility);
	}
	
	@Override
	public String toString() {
		return "[" + getName() + ", " + getResponsibilities() + "]";
	}
	
}
