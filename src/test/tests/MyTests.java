package test.tests;

import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Test;

import model.Project;
import model.requirement.Requirement;
import model.responsibility.Responsibility;
import pipeline.PipelineParalelo;

public class MyTests {

	// Variables
	private static final String barra = " ==========================================================================================";
	
	// Constructors
	
	// Getters and Setters
	
	// Methods
	
	@Test
	public void testCoreference() {
		
		System.out.println("\n\nTEST CORREFERENCIA" + barra);
		
		// Proyecto de ejemplo
		Project project = new Project("Test_Project");
		
		// Requerimientos
		Requirement req1 = new Requirement("If the user clicks an image, the system decompresses it.");		
		project.addRequirement(req1);
		
		//PipelineParalelo
		PipelineParalelo pipe = new PipelineParalelo(project, null);
		// Pre-procesamiento de los requerimientos
		pipe.firstStep();
		
		// Extraccion de responsabilidades
		pipe.testResponsabilityExtraction();
		
		ArrayList<Responsibility> responsibilities = req1.getResponsibilities();
		
		Assert.assertTrue(responsibilities.size() == 2);		
		
	}
	
	@Test
	public void testVerbAsNoun() {
		
		System.out.println("\n\nTEST VERBO COMO SUSTANTIVO" + barra);
		
		// Proyecto de ejemplo
		Project project = new Project("Test_Project");
		
		// Requerimientos
		Requirement req1 = new Requirement("If the user updates the image.");		
		project.addRequirement(req1);
		
		//PipelineParalelo
		PipelineParalelo pipe = new PipelineParalelo(project, null);
		
		// Pre-procesamiento de los requerimientos
		pipe.firstStep();
		
		// Extraccion de responsabilidades
		pipe.testResponsabilityExtraction();
		
		ArrayList<Responsibility> responsibilities = req1.getResponsibilities();
		
		Assert.assertTrue(responsibilities.size() == 1);		
		
	}
	
	@Test
	public void testNegations() {
		
		System.out.println("\n\nTEST DE NEGACION" + barra);
		
		// Proyecto de ejemplo
		Project project = new Project("Test_Project");
		
		// Requerimientos
		Requirement req1 = new Requirement("If the user does not click the image, the system clicks it.");		
		project.addRequirement(req1);
		
		//PipelineParalelo
		PipelineParalelo pipe = new PipelineParalelo(project, null);
		
		// Pre-procesamiento de los requerimientos
		pipe.firstStep();
		
		// Extraccion de responsabilidades
		pipe.testResponsabilityExtraction();
		
		ArrayList<Responsibility> responsibilities = req1.getResponsibilities();
		
		Assert.assertTrue(responsibilities.size() == 2);
		Assert.assertTrue(responsibilities.get(1).getNegated());
		
	}
	
	@Test
	public void testSubjectAbsence() {
		
		System.out.println("\n\nTEST AUSENCIA DE SUJETO" + barra);
		
		// Proyecto de ejemplo
		Project project = new Project("Test_Project");
		
		// Requerimientos
		Requirement req1 = new Requirement("The user has to select the Split option from the plugins tree (or press the “S” key) to display the split panel.");		
		project.addRequirement(req1);
		
		//PipelineParalelo
		PipelineParalelo pipe = new PipelineParalelo(project, null);
		
		// Pre-procesamiento de los requerimientos
		pipe.firstStep();
		
		// Extraccion de responsabilidades
		pipe.testResponsabilityExtraction();
		
		ArrayList<Responsibility> responsibilities = req1.getResponsibilities();
		
		Assert.assertTrue(responsibilities.size() == 3);		
		
	}
	
	@Test
	public void testXCOMP() {
		
		System.out.println("\n\nTEST XCOMP" + barra);
		
		// Proyecto de ejemplo
		Project project = new Project("Test_Project");
		
		// Requerimientos
		// The user selects to return to the main menu.
		// The user choose to open the window.
		// The user has to open the window.
		// The user gives the toys to the children.
		Requirement req1 = new Requirement("The user selects to return to the main menu.");		
		Requirement req2 = new Requirement("The user choose to open the window.");		
		Requirement req3 = new Requirement("The user has to open the window.");		
		Requirement req4 = new Requirement("The user gives the toys to the children.");
		Requirement req5 = new Requirement("The user must not click the button to proceed.");	
		project.addRequirement(req1);
		project.addRequirement(req2);
		project.addRequirement(req3);
		project.addRequirement(req4);
		
		//PipelineParalelo
		PipelineParalelo pipe = new PipelineParalelo(project, null);
		
		// Pre-procesamiento de los requerimientos
		pipe.firstStep();
		
		// Extraccion de responsabilidades
		pipe.testResponsabilityExtraction();
		
		Assert.assertTrue(req1.getResponsibilities().size() == 1);
		Assert.assertTrue(req2.getResponsibilities().size() == 1);
		Assert.assertTrue(req3.getResponsibilities().size() == 1);
		Assert.assertTrue(req4.getResponsibilities().size() == 1);
		
	}
		
}
