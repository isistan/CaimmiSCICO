package model.exporters.jucm;

public abstract class JUCMNode extends JUCMElement{
	
	// Variables
	
	// Constructors
	
	public JUCMNode(JUCMExporter exporter) {
		super(exporter);
	}
	
	// Getters and Setters
	
	// Methods

	/**
	 * This method should not be called directly, they are used when creating a connection
	 * 
	 * @param succ
	 */
	public abstract void addSucc(JUCMConnection succ);
	
	/**
	 * This method should not be called directly, they are used when creating a connection
	 * 
	 * @param prev
	 */
	public abstract void addPrev(JUCMConnection prev);
}
