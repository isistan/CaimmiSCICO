package test.tests;

import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Test;

import model.Project;
import model.causalRelationship.CausalRelationship;
import model.requirement.Requirement;
import model.responsibility.Responsibility;
import pipeline.PipelineParalelo;

public class SeqTests {

	// Variables
	private static final String barra = " ==========================================================================================";

	// Constructors

	// Getters and Setters

	// Methods

	@Test
	public void testIF_THEN() {

		System.out.println("\n\nTEST IF <...> THEN <...>" + barra);

		// Example of a project
		Project project = new Project("Test_Project");

		// Requirements
		Requirement req1 = new Requirement("If the user clicks an image, the system decompresses it.");		
		project.addRequirement(req1);
		Requirement req2 = new Requirement("If the user clicks an image then the system decompresses it.");		
		project.addRequirement(req2);
		Requirement req3 = new Requirement("If the user clicks an image, then the system decompresses it.");		
		project.addRequirement(req3);
		Requirement req4 = new Requirement("If the user clicks an image the system decompresses it.");		
		project.addRequirement(req4);
		Requirement req5 = new Requirement("Later that night, if the user clicks an image, the system decompresses it.");		
		project.addRequirement(req5);
		Requirement req6 = new Requirement("if the user clicks an image, the system decompresses it if everyting is ok.");		
		project.addRequirement(req6);
		Requirement req7 = new Requirement("The user enters to the page and if the user clicks an image, the system decompresses it.");		
		project.addRequirement(req7);

		//Parallel Pipeline
		PipelineParalelo pipe = new PipelineParalelo(project, null);

		//Pre-processing of the requirements
		pipe.firstStep();

		// Extraction of responsibilities
		pipe.testResponsabilityExtraction();

		// We check that we have two responsibilities
		//Assert.assertTrue(responsibilities.size() == 2);	

		// Sequentialization of responsibilities
		ArrayList<CausalRelationship> causalRelations = pipe.testResponsibilitiesSequencing();
		//Assert.assertTrue(causalRelations.size() == 1);

		Assert.assertTrue(req1.getCausalRelationships().size() == 1);
		Assert.assertTrue(req2.getCausalRelationships().size() == 1);
		Assert.assertTrue(req3.getCausalRelationships().size() == 1);
		Assert.assertTrue(req4.getCausalRelationships().size() == 0); // This has to be 0!
		Assert.assertTrue(req5.getCausalRelationships().size() == 1);
		Assert.assertTrue(req6.getCausalRelationships().size() == 1 && req6.getCausalRelationships().get(0).getResp1().getVerb().equals("clicks"));
		Assert.assertTrue(req7.getCausalRelationships().size() == 1 && req7.getCausalRelationships().get(0).getResp1().getVerb().equals("clicks"));

	}

	@Test
	public void testTEMPORAL() {

		System.out.println("\n\nTEST TEMPORARY" + barra);

		// Example of a project
		Project project = new Project("Test_Project");

		// Requirements
		Requirement req1 = new Requirement("After the user clicks an image, the system decompresses it.");		
		project.addRequirement(req1);
		Requirement req2 = new Requirement("Before the user clicks an image, the system decompresses it.");		
		project.addRequirement(req2);
		Requirement req3 = new Requirement("Once the user clicks an image, the system decompresses it.");		
		project.addRequirement(req3);
		Requirement req4 = new Requirement("When the user clicks an image, the system decompresses it.");		
		project.addRequirement(req4);
		Requirement req5 = new Requirement("When the user clicks an image the system decompresses it.");		
		project.addRequirement(req5);
		Requirement req6 = new Requirement("As soon as the user clicks an image, the system decompresses it if everyting is ok.");		
		project.addRequirement(req6);
		Requirement req7 = new Requirement("The user reloads the page until the system presents a splash screen.");		
		project.addRequirement(req7);
		Requirement req8 = new Requirement("The system decompresses the image after the user clicks that image.");		
		project.addRequirement(req8);

		//Parallel Pipeline
		PipelineParalelo pipe = new PipelineParalelo(project, null);

		//Pre-processing of the requirements
		pipe.firstStep();

		// Extraction of responsibilities
		pipe.testResponsabilityExtraction();

		// Sequentialization of responsibilities
		ArrayList<CausalRelationship> causalRelations = pipe.testResponsibilitiesSequencing();
		//Assert.assertTrue(causalRelations.size() == 1);


		Assert.assertTrue(req1.getCausalRelationships().size() == 1);

		Assert.assertTrue(req2.getCausalRelationships().size() == 1 && req2.getCausalRelationships().get(0).getResp1().getVerb().equals("decompresses"));

		Assert.assertTrue(req3.getCausalRelationships().size() == 1);

		Assert.assertTrue(req4.getCausalRelationships().size() == 1);

		Assert.assertTrue(req5.getCausalRelationships().size() == 0); // This has to be 0!

		Assert.assertTrue(req6.getCausalRelationships().size() == 1 
				&& req6.getCausalRelationships().get(0).getResp1().getVerb().equals("clicks"));

		Assert.assertTrue(req7.getCausalRelationships().size() == 1 
				&& req7.getCausalRelationships().get(0).getResp1().getVerb().equals("reloads"));

		Assert.assertTrue(req8.getCausalRelationships().size() == 1 
				&& req8.getCausalRelationships().get(0).getResp1().getVerb().equals("clicks") 
				&& req8.getCausalRelationships().get(0).getResp2().getVerb().equals("decompresses"));

	}

	@Test
	public void testSIMPLES() {

		System.out.println("\n\nTEST SIMPLE" + barra);

		// Example of a project
		Project project = new Project("Test_Project");

		// Requirements 
		Requirement req1 = new Requirement("The user clicks an image to decompress it.");		
		project.addRequirement(req1);
		Requirement req2 = new Requirement("The user clicks an image for decompressesing it.");		
		project.addRequirement(req2);
		Requirement req3 = new Requirement("The user clicks an image in order to decompresses it.");		
		project.addRequirement(req3);
		Requirement req4 = new Requirement("The user decompresses an image by clicking it.");		
		project.addRequirement(req4);
		Requirement req5 = new Requirement("The user clicks an image and then the system decompresses it.");		
		project.addRequirement(req5);
		Requirement req6 = new Requirement("The user clicks an image and next the system decompresses it.");		
		project.addRequirement(req6);
		Requirement req7 = new Requirement("If the user reloads the page, the system presents a splash screen and then the system shows an icon.");		
		project.addRequirement(req7);

		//Parallel pipeline
		PipelineParalelo pipe = new PipelineParalelo(project, null);

		// Pre-processing of the requirements
		pipe.firstStep();

		// Extraction of responsibilities
		pipe.testResponsabilityExtraction();

		// Sequentialization of responsibilities
		ArrayList<CausalRelationship> causalRelations = pipe.testResponsibilitiesSequencing();


		Assert.assertTrue(req1.getCausalRelationships().size() == 1);

		Assert.assertTrue(req2.getCausalRelationships().size() == 1);

		Assert.assertTrue(req3.getCausalRelationships().size() == 1);

		Assert.assertTrue(req4.getCausalRelationships().size() == 1 
				&& req4.getCausalRelationships().get(0).getResp1().getVerb().equals("clicking"));

		Assert.assertTrue(req5.getCausalRelationships().size() == 1);

		Assert.assertTrue(req6.getCausalRelationships().size() == 1);

		// This last requirement would be somewhat conflicting.
		Assert.assertTrue(req7.getCausalRelationships().size() == 2);

	}


	@Test
	public void testBETWEENSENTENCES() {

		System.out.println("\n\nTEST BETWEENSENTENCES" + barra);

		// Example of a project
		Project project = new Project("Test_Project");

		// Requirements
		Requirement req1 = new Requirement("If the user opens a window, the system will play the movie and also the system will show a warning. Otherwise, the system will stop the movie and then the system will presents another window.");		
		project.addRequirement(req1);

		// Requirements
		Requirement req2 = new Requirement("The user to click a submission button. Then, the system will show a warning.");		
		project.addRequirement(req2);

		// This is in the specification
		Requirement req3 = new Requirement("If a mouse click is received, this component will terminate the movie and forward the user to the main menu component. Otherwise, the movie will continue to its completion and the user will be moved to the main menu.");		
		//project.addRequirement(req2);
		
		Requirement req4 = new Requirement("If the user selects the correct answer, a message will be displayed and the component will move to the next question. If the incorrect answer is selected, this component will inform the user of this and give them another chance to answer the question.");		
		project.addRequirement(req4);	
		// 
		//Parallel Pipeline
		PipelineParalelo pipe = new PipelineParalelo(project, null);

		// Pre-procesamiento de los requerimientos
		pipe.firstStep();

		// Extraccion de responsabilidades
		pipe.testResponsabilityExtraction();

		Assert.assertTrue(req1.getResponsibilities().size() == 5);

		// Sequentialization of responsibilities
		ArrayList<CausalRelationship> causalRelations = pipe.testResponsibilitiesSequencing();

		Assert.assertTrue(req1.getCausalRelationships().size() == 3);
		
		Assert.assertTrue(req2.getCausalRelationships().size() == 1);

		Assert.assertTrue(req4.getCausalRelationships().size() == 4);

	}
}
