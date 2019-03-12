package model.exporters.jucm;

import org.w3c.dom.Element;

import model.responsibility.Responsibility;

public class JUCMResponsibility extends JUCMNode {

	// Variables

	private JUCMConnection prev;
	private JUCMConnection succ;
	private Responsibility responsibility;
	private Element nodeElement;

	// Constructors

	public JUCMResponsibility(JUCMExporter exporter, Responsibility responsibility) {
		super(exporter);
		this.prev = null;
		this.succ = null;
		this.responsibility = responsibility;		
		this.exporter.getNodes().add(this);
	}

	// Getters and Setters

	@Override
	public Element getNodeElement() {
		return nodeElement;
	}

	public Responsibility getUCMElement(){
		return this.responsibility;
	}

	// Mehtods

	public void addPrev(JUCMConnection prev) {
		this.prev = prev;
	}

	public void addSucc(JUCMConnection succ) {
		this.succ = succ;
	}

	@Override
	public void generate(Element documentElement) {

		Element responsibilityElement = exporter.getDocument().createElement("responsibilities");
		documentElement.getParentNode().appendChild(responsibilityElement);

		responsibilityElement.setAttribute("respRefs", new Integer(exporter.getMaxID()+1).toString());
		responsibilityElement.setAttribute("name", responsibility.getPrettyShortForm());
		responsibilityElement.setAttribute("id", exporter.getMaxID().toString());

		nodeElement = exporter.getDocument().createElement("nodes");
		documentElement.appendChild(nodeElement);

		exporter.setMaxID(exporter.getMaxID()+1);

		nodeElement.setAttributeNS(JUCMExporter.xsiNS, "xsi:type", "ucm.map:RespRef");
		nodeElement.setAttribute("name", responsibility.getPrettyShortForm());
		nodeElement.setAttribute("description", responsibility.getLongForm());
		nodeElement.setAttribute("id", exporter.getMaxID().toString());
		nodeElement.setAttribute("respDef", responsibilityElement.getAttribute("id"));

		if (responsibility.getComponent() != null && exporter.getJUCMComponent(responsibility.getComponent()) != null){
			
			JUCMComponent component = exporter.getJUCMComponent(responsibility.getComponent());
			if ( !component.getNodeElement().hasAttribute("nodes")) {
				component.getNodeElement().setAttribute("nodes", nodeElement.getAttribute("id"));
			} else {
				component.getNodeElement().setAttribute("nodes", 	component.getNodeElement().getAttribute("nodes") +	" " + nodeElement.getAttribute("id"));
			}
			
			int resps = component.getUCMComponent().getResponsibilities().size();
			
			Integer compWidth = JUCMComponent.COMPONENT_WIDTH * resps;
			
			Integer resultX = (compWidth / (resps + 1)) * (component.cantPuestas + 1);
			Integer resultY = component.changeY();
			resultX += Integer.parseInt(component.getNodeElement().getAttribute("x"));
			resultY += Integer.parseInt(component.getNodeElement().getAttribute("y"));
			
			nodeElement.setAttribute("contRef", component.getNodeElement().getAttribute("id"));
			nodeElement.setAttribute("x", resultX.toString());
			nodeElement.setAttribute("y", resultY.toString());
		
			System.out.println("Resp ["+ resultX + ", " + resultY + "]");
			
			component.cantPuestas++; 
			
		}else{
			
			nodeElement.setAttribute("x", new Integer(new Double(JUCMExporter.COMPONENT_SECTOR_X.doubleValue() + Math.random()*JUCMExporter.COMPONENT_SECTOR_WIDTH.doubleValue()).intValue()).toString());
			nodeElement.setAttribute("y", new Integer(new Double(JUCMExporter.COMPONENT_SECTOR_Y.doubleValue() + Math.random()*JUCMExporter.COMPONENT_SECTOR_HEIGHT.doubleValue()).intValue()).toString());
		
		}
		
		nodeElement.setAttribute("pred", "//@urndef/@specDiagrams.0/@connections." + prev.getConnectionNumber());
		nodeElement.setAttribute("succ", "//@urndef/@specDiagrams.0/@connections." + succ.getConnectionNumber());
		nodeElement.appendChild(exporter.getDocument().createElement("label"));

		exporter.setMaxID(exporter.getMaxID()+1);
	}

}
