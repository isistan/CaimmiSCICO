package model.exporters.jucm;

import org.w3c.dom.Element;

public class JUCMConnection {
	
	// Variables
	
	public final static String TAG_NAME = "connections";
	
	private Element connectionElem;
	private JUCMNode from;
	private JUCMNode to;
	private JUCMExporter exporter;
	
	// Constructors
	
	public JUCMConnection(JUCMExporter exporter, JUCMNode from, JUCMNode to) {
		super();
		this.exporter = exporter;
		this.setFrom(from);
		this.setTo(to);
		this.exporter.getConnections().add(this);
		
	}
	
	// Getters and Setters
	
	public Integer getConnectionNumber(){
		return exporter.getConnections().indexOf(this);
	}

	public JUCMNode getFrom() {
		return from;
	}

	public void setFrom(JUCMNode from) {
		this.from = from;
		this.from.addSucc(this);
	}

	public JUCMNode getTo() {
		return to;
	}

	public void setTo(JUCMNode to) {
		this.to = to;
		this.to.addPrev(this);
	}
	
	// Methods
	
	public void generate(Element documentElement){
		connectionElem = exporter.getDocument().createElement(TAG_NAME);
		connectionElem.setAttributeNS(JUCMExporter.xsiNS,"xsi:type", "ucm.map:NodeConnection");
		connectionElem.setAttribute("source", getFrom().getNodeElement().getAttribute("id").toString());
		connectionElem.setAttribute("target", getTo().getNodeElement().getAttribute("id").toString());
		documentElement.appendChild(connectionElem);
	}
}
