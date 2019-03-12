package utils.io;

import model.Project;
import model.requirement.Requirement;

public class ProjectIO {

	// Variables
	
	// Constructors
	
	public ProjectIO(){
		
	}
	
	// Getters and Setters
	
	// Methods
	
	public Project readFromFile(String filepath){
		Project out = new Project();
		
		IO io = new IO();
		
		String fileContent = io.readFromFile(filepath);
		
		String[] lines = fileContent.split("\n");
		
		boolean readingRequirement = false;
		boolean readingProjectName = false;
		int requirementNumber = 1;
		
		for (int index = 0 ; index < lines.length ; index++){
			
			String line = lines[index];
			
			// Do not consider '#' lines since they are commentaries and blank lines
			if (line.startsWith("#") || line.isEmpty()){
				continue;
			}
			
			// Start reading
			if (line.toLowerCase().startsWith("@projectname")){
				readingProjectName = true;
				continue;
			}
			
			if (line.toLowerCase().startsWith("@requirement")){
				readingRequirement = true;
				continue;
			}	
			
			if (line.startsWith("@")){
				readingRequirement = false;
				continue;
			}
			
			// Read Project Name
			if (readingProjectName && ! line.startsWith("@")){
				out.setProjectName(line);
				readingProjectName = false;
			}
			
			// Read Requirement
			if (readingRequirement && ! line.startsWith("@")){
				out.addRequirement(new Requirement("REQ-"+requirementNumber, line));
				requirementNumber++;
			}
			
		}
		
		return out;
	}
	
}
