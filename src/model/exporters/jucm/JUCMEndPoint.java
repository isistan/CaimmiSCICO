package model.exporters.jucm;

import org.w3c.dom.Element;

public class JUCMEndPoint extends JUCMNode {

	// Variables

	public static Integer Y = 100;

	private JUCMConnection prev;
	private Element nodeElement;

	// Constructors

	public JUCMEndPoint(JUCMExporter exporter){
		super(exporter);		
		this.exporter.getNodes().add(this);
	}

	// Getters and Setters

	@Override
	public Element getNodeElement() {
		return nodeElement;
	}

	// Methods

	@Override
	public void addSucc(JUCMConnection succ) {

	}

	@Override
	public void addPrev(JUCMConnection prev) {
		this.prev = prev;
	}	

	@Override
	public void generate(Element documentElement) {
		nodeElement = exporter.getDocument().createElement("nodes");
		nodeElement.setAttributeNS(JUCMExporter.xsiNS, "xsi:type", "ucm.map:EndPoint");
		nodeElement.setAttribute("name", "");
		nodeElement.setAttribute("x", new Integer(JUCMExporter.END_SECTOR_X).toString());
		nodeElement.setAttribute("y", new Integer(JUCMExporter.END_SECTOR_Y + Y).toString());
		nodeElement.setAttribute("id", exporter.getMaxID().toString());
		nodeElement.appendChild(exporter.getDocument().createElement("label"));

		// Actualizo Y
		Y = Y + 50;

		exporter.setMaxID(exporter.getMaxID()+1);

		nodeElement.setAttribute("pred", "//@urndef/@specDiagrams.0/@connections."+prev.getConnectionNumber());

		Element postConditions = exporter.getDocument().createElement("postcondition");
		postConditions.setAttribute("deltaX", "-40");
		postConditions.setAttribute("deltaY", "-20");
		postConditions.setAttribute("label", "");
		postConditions.setAttribute("expression", "true");
		nodeElement.appendChild(postConditions);

		documentElement.appendChild(nodeElement);
	}

}
