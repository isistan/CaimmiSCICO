package model.exporters.jucm;

import java.util.Collection;
import java.util.LinkedList;

import org.w3c.dom.Element;

public class JUCMOrJoin extends JUCMNode {
	
	// Variables
	
	private Collection<JUCMConnection> prev;
	private JUCMConnection succ;
	private Element orJoinNodeElement;
	
	// Constructors
	
	public JUCMOrJoin(JUCMExporter exporter){
		super(exporter);
		this.prev = new LinkedList<JUCMConnection>();
		this.succ = null;
		this.exporter.getNodes().add(this);
	}
	
	// Getters and Setters
	
	@Override
	public Element getNodeElement() {
		return orJoinNodeElement;
	}
	
	// Methods

	@Override
	public void addSucc(JUCMConnection succ) {
		this.succ = succ;
	}

	@Override
	public void addPrev(JUCMConnection prev) {
		this.prev.add(prev);
	}
	
	@Override
	public void generate(Element documentElement) {
		orJoinNodeElement = exporter.getDocument().createElement("nodes");
		documentElement.appendChild(orJoinNodeElement);
		
		orJoinNodeElement.setAttributeNS(JUCMExporter.xsiNS, "xsi:type", "ucm.map:OrJoin");
		orJoinNodeElement.setAttribute("name", "OrJoin");
		orJoinNodeElement.setAttribute("x", new Integer(new Double(
				JUCMExporter.COMPONENT_SECTOR_X.doubleValue() + Math.random()*JUCMExporter.COMPONENT_SECTOR_WIDTH.doubleValue())
			.intValue()).toString());
		orJoinNodeElement.setAttribute("y", new Integer(new Double(
				JUCMExporter.COMPONENT_SECTOR_Y.doubleValue() + Math.random()*JUCMExporter.COMPONENT_SECTOR_HEIGHT.doubleValue())
			.intValue()).toString());
		orJoinNodeElement.setAttribute("id", exporter.getMaxID().toString());
		
		orJoinNodeElement.setAttribute("pred", "");
		orJoinNodeElement.setAttribute("succ", "//@urndef/@specDiagrams.0/@connections."+this.succ.getConnectionNumber());
		
		for(JUCMConnection nextPrev: this.prev){
			orJoinNodeElement.setAttribute("pred", orJoinNodeElement.getAttribute("pred") + " //@urndef/@specDiagrams.0/@connections."+nextPrev.getConnectionNumber());
		}
		
		exporter.setMaxID(exporter.getMaxID()+1);
	}

}
