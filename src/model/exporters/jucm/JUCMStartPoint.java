package model.exporters.jucm;

import org.w3c.dom.Element;

public class JUCMStartPoint extends JUCMNode {
	
	// Variables
	
	public static Integer Y = 100;
	
	private JUCMConnection succ;
	private Element nodeElement;
	
	// Constructors
	
	public JUCMStartPoint(JUCMExporter exporter){
		super(exporter);		
		this.exporter.getNodes().add(this);
	}

	// Getters and Setters
	
	@Override
	public Element getNodeElement() {
		return nodeElement;
	}

	@Override
	public void addSucc(JUCMConnection succ) {
		this.succ = succ;
	}

	@Override
	public void addPrev(JUCMConnection prev) {
		
	}
	
	// Methods
	
	@Override
	public void generate(Element documentElement) {
		
		nodeElement = exporter.getDocument().createElement("nodes");
		nodeElement.setAttributeNS(JUCMExporter.xsiNS, "xsi:type", "ucm.map:StartPoint");
		nodeElement.setAttribute("name", "");
		nodeElement.setAttribute("x", new Integer(JUCMExporter.START_SECTOR_X + (JUCMExporter.START_SECTOR_WIDTH  / 2)).toString());
		nodeElement.setAttribute("y", new Integer(JUCMExporter.START_SECTOR_Y + Y).toString());
		nodeElement.setAttribute("id", exporter.getMaxID().toString());
		nodeElement.appendChild(exporter.getDocument().createElement("label"));
		
		// Actualizo Y
		Y = Y + 50;
		
		exporter.setMaxID(exporter.getMaxID()+1);
		
		nodeElement.setAttribute("succ", "//@urndef/@specDiagrams.0/@connections."+succ.getConnectionNumber());

		Element preCondition = exporter.getDocument().createElement("precondition");
		preCondition.setAttribute("deltaX", "-40");
		preCondition.setAttribute("deltaY", "-20");
		preCondition.setAttribute("label", "");
		preCondition.setAttribute("expression", "true");
		nodeElement.appendChild(preCondition);
		
		documentElement.appendChild(nodeElement);
	}

}
