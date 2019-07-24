package main;

import java.util.ArrayList;

import model.causalRelationship.CausalRelationship;
import model.conceptualComponent.ConceptualComponent;
import model.exporters.jucm.JUCMExporter;
import model.responsibility.Responsibility;
import model.ucm.UseCaseMap;


public class PruebaPublica {

	public static void main(String[] args) {
		
		// Model
		
		Responsibility r1 = new Responsibility("add", "course");
		r1.setId("Resp 1");
		Responsibility r2 = new Responsibility("drop", "course");
		r2.setId("Resp 2");
		Responsibility r3 = new Responsibility("drop", "course");
		r2.setId("Resp 3");
		
		
		ConceptualComponent c1 = new ConceptualComponent();
		c1.setName("Component 1");
		c1.addResponsibility(r1);
		ConceptualComponent c2 = new ConceptualComponent();
		c2.setName("Component 2");
		c2.addResponsibility(r2);
		c2.addResponsibility(r3);
		
		r1.setComponent(c1);
		r1.addNextResponsibility(r2);
		r2.setComponent(c2);
		r2.addPrevResponsibility(r1);
		r2.addNextResponsibility(r3);
		r3.setComponent(c2);
		r3.addPrevResponsibility(r2);
		
		CausalRelationship cr1 = new CausalRelationship(r1, r2);
		CausalRelationship cr2 = new CausalRelationship(r2, r3);
		
		// Responsibilities
		ArrayList<Responsibility> responsibilities = new ArrayList<Responsibility>();
		responsibilities.add(r1);
		responsibilities.add(r2);
		responsibilities.add(r3);
		ArrayList<ConceptualComponent> components = new ArrayList<ConceptualComponent>();
		components.add(c1);
		components.add(c2);
		ArrayList<CausalRelationship> relations = new ArrayList<CausalRelationship>();
		relations.add(cr1);
		relations.add(cr2);
		
		UseCaseMap ucm = new UseCaseMap();
		ucm.setResponsibilities(responsibilities);
		ucm.setComponents(components);
		ucm.setRelations(relations);
		
		// JUCM
		
		JUCMExporter exporter = new JUCMExporter();
		
		// Output
		
		exporter.export(ucm, ".\\salida.jucm");
		
		// We would have to read the jUCM (URN)
				
		// Get the image and display it (PNG)
		
	}
}
