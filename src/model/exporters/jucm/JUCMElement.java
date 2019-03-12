package model.exporters.jucm;

import org.w3c.dom.Element;

public abstract class JUCMElement {
	
	// Variables
	
	protected JUCMExporter exporter;
	
	// Constructors
	
	public JUCMElement(JUCMExporter exporter) {
		setExporter(exporter);
	}
	
	// Getters and Setters
	
	public void setExporter(JUCMExporter exporter) {
		this.exporter = exporter;
	}
	
	public JUCMExporter getExporter() {
		return exporter;
	}
		
	// Methods
	
	public abstract Element getNodeElement();
	public abstract void generate(Element documentElement);
	
	
}
