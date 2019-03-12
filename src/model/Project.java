package model;
import java.util.ArrayList;

import model.requirement.Requirement;

public class Project {

	// Variables
	
	private String projectName;
	private ArrayList<Requirement> requirements;
		
	// Constructors
	
	public Project(){
		this("");		
	}
	
	public Project(String projectName){
		this.projectName = projectName;
		this.requirements = new ArrayList<Requirement>();
	}
	
	// Getters and Setters
	
	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	
	public void addRequirement(Requirement requirement){
		if (requirement.getId().isEmpty()){
			requirement.setId("" + this.requirements.size());
		}
		this.requirements.add(requirement);
	}
	
	public ArrayList<Requirement> getRequirements(){
		return this.requirements;
	}
	
	// Methods	
	
}
