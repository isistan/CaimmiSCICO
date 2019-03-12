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
	 * Este metodo no debe ser llamado directamente, son usados al crear una conexion
	 * 
	 * @param succ
	 */
	public abstract void addSucc(JUCMConnection succ);
	
	/**
	 * Este metodo no debe ser llamado directamente, son usados al crear una conexion
	 * 
	 * @param prev
	 */
	public abstract void addPrev(JUCMConnection prev);
}
