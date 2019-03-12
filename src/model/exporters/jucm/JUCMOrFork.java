package model.exporters.jucm;

import java.util.Collection;
import java.util.LinkedList;

import org.w3c.dom.Element;

public class JUCMOrFork extends JUCMNode {
	
	// Variables
	
	private JUCMConnection prev;
	private Collection<JUCMConnection> succ;
	private Element orForkNodeElement;
	
	public JUCMOrFork(JUCMExporter exporter){
		super(exporter);
		this.prev = null;
		this.succ = new LinkedList<JUCMConnection>();		
		this.exporter.getNodes().add(this);
	}

	@Override
	public Element getNodeElement() {
		return orForkNodeElement;
	}

	@Override
	public void addSucc(JUCMConnection succ) {
		this.succ.add(succ);
	}

	@Override
	public void addPrev(JUCMConnection prev) {
		this.prev = prev;
	}
	
	@Override
	public void generate(Element documentElement) {
		orForkNodeElement = exporter.getDocument().createElement("nodes");
		documentElement.appendChild(orForkNodeElement);
		
		orForkNodeElement.setAttributeNS(JUCMExporter.xsiNS, "xsi:type", "ucm.map:OrFork");
		orForkNodeElement.setAttribute("name", "OrFork");
		orForkNodeElement.setAttribute("x", new Integer(new Double(
				JUCMExporter.COMPONENT_SECTOR_X.doubleValue() + Math.random()*JUCMExporter.COMPONENT_SECTOR_WIDTH.doubleValue())
			.intValue()).toString());
		orForkNodeElement.setAttribute("y", new Integer(new Double(
				JUCMExporter.COMPONENT_SECTOR_Y.doubleValue() + Math.random()*JUCMExporter.COMPONENT_SECTOR_HEIGHT.doubleValue())
			.intValue()).toString());
		orForkNodeElement.setAttribute("id", exporter.getMaxID().toString());
		
		orForkNodeElement.setAttribute("pred", "//@urndef/@specDiagrams.0/@connections."+this.prev.getConnectionNumber());
		orForkNodeElement.setAttribute("succ", "");
		
		for(JUCMConnection nextSucc: this.succ){
			orForkNodeElement.setAttribute("succ", orForkNodeElement.getAttribute("succ") + " //@urndef/@specDiagrams.0/@connections."+nextSucc.getConnectionNumber());
		}
		
		exporter.setMaxID(exporter.getMaxID()+1);
	}

}
