package model.exporters.jucm;

import org.w3c.dom.Element;

import model.conceptualComponent.ConceptualComponent;

public class JUCMComponent extends JUCMElement {

	// Variables

	public static final Integer COMPONENT_WIDTH = 100;
	public static final Integer COMPONENT_HEIGHT = 200;
	public static Integer compWidth = 0;
	public static Integer compHeight = 0;
	public static Integer lastX = 50;
	public static final Integer firstY = 50;
	public static final Integer lastY = 150;
	public Integer cantPuestas = 0;
	
	private Boolean actualY = false;
	private ConceptualComponent component;
	private Element nodeElement;

	// Constructors

	public JUCMComponent(JUCMExporter exporter, ConceptualComponent component) {
		super(exporter);
		this.component = component;
		this.nodeElement = null;
	}

	// Getters and Setters

	@Override
	public Element getNodeElement() {
		return nodeElement;
	}

	public ConceptualComponent getUCMComponent(){
		return this.component;
	}

	// Methods

	public Integer changeY(){
		actualY = !actualY;
		if (actualY){
			return firstY;
		}else{
			return lastY;
		}
	}
	
	@Override
	public void generate(Element documentElement) {
		Element componentElement = exporter.getDocument().createElement("components");
		documentElement.getParentNode().appendChild(componentElement);

		componentElement.setAttribute("contRefs", new Integer(exporter.getMaxID()+1).toString());
		componentElement.setAttribute("name", component.getName());
		componentElement.setAttribute("kind", "Other");
		componentElement.setAttribute("id", exporter.getMaxID().toString());

		nodeElement = exporter.getDocument().createElement("contRefs");
		documentElement.appendChild(nodeElement);

		exporter.setMaxID(exporter.getMaxID()+1);

		nodeElement.setAttributeNS(JUCMExporter.xsiNS, "xsi:type", "ucm.map:ComponentRef");
		nodeElement.setAttribute("name", component.getName());

		String[] coords = getCoords();
		nodeElement.setAttribute("x", coords[0]);
		nodeElement.setAttribute("y", coords[1]);

		compWidth = COMPONENT_WIDTH * this.getUCMComponent().getResponsibilities().size();
		compHeight = COMPONENT_HEIGHT;
		if (this.getUCMComponent().getResponsibilities().size() == 1)
			compHeight /= 2; 
		
		nodeElement.setAttribute("id", exporter.getMaxID().toString());
		nodeElement.setAttribute("width", compWidth.toString());
		nodeElement.setAttribute("height", compHeight.toString());
		nodeElement.setAttribute("contDef", componentElement.getAttribute("id"));

		nodeElement.appendChild(exporter.getDocument().createElement("label"));

		exporter.setMaxID(exporter.getMaxID()+1);
	}

	private String[] getCoords(){
		Integer resultX = null;
		Integer resultY = null;
		Boolean colides = true;
		Integer counter = 0;

		while(colides || counter > 50){

			colides = false;


			//resultX = new Integer(new Double(JUCMExporter.COMPONENT_SECTOR_X.doubleValue() + Math.random() * JUCMExporter.COMPONENT_SECTOR_WIDTH.doubleValue()).intValue());
			//resultY = new Integer(new Double(JUCMExporter.COMPONENT_SECTOR_Y.doubleValue() + Math.random() * JUCMExporter.COMPONENT_SECTOR_HEIGHT.doubleValue()).intValue());

			// De X tiene que estar bien en el medio
			// De y tiene que ir moviendose hasta no colisionar
			resultX = new Integer(JUCMExporter.COMPONENT_SECTOR_X);
			resultY = new Integer((Integer) (counter * (this.COMPONENT_HEIGHT + 50)));
 
			for(JUCMComponent component : this.exporter.getComponents()){

				if ( component.getNodeElement() != null && !component.equals(this) ){

					Integer xMin = new Integer(component.getNodeElement().getAttribute("x")) - JUCMComponent.COMPONENT_WIDTH;
					Integer xMax = new Integer(component.getNodeElement().getAttribute("x")) + JUCMComponent.COMPONENT_WIDTH;
					Integer yMin = new Integer(component.getNodeElement().getAttribute("y")) - JUCMComponent.COMPONENT_HEIGHT;
					Integer yMax = new Integer(component.getNodeElement().getAttribute("y")) + JUCMComponent.COMPONENT_HEIGHT;

					if (resultX > xMin && resultX < xMax && resultY > yMin && resultY < yMax){
						colides = true;
					}
				}
			}

			counter++;
		}

		System.out.println("Comp [" + resultX + ", " + resultY+ "]");
		
		String[] result = new String[2];
		result[0] = resultX.toString();
		result[1] = resultY.toString();

		//System.out.println("XCoords: " + result[0]);
		//System.out.println("YCoords: " + result[1]);

		return result;
	}
}
