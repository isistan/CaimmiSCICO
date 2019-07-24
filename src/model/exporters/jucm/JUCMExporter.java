package model.exporters.jucm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import model.causalRelationship.CausalRelationship;
import model.conceptualComponent.ConceptualComponent;
import model.exporters.XMLExporter;
import model.responsibility.Responsibility;
import model.ucm.UseCaseMap;

public class JUCMExporter extends XMLExporter{

	// Variables

	public static final String xmiNS = "http://www.omg.org/XMI";
	public static final String xsiNS = "http://www.w3.org/2001/XMLSchema-instance";
	public static final String ucmMapNS = "http:///ucm/map.ecore";
	public static final String urnNS = "http:///urn.ecore";
	public static final Integer COMPONENT_SECTOR_X = 100;
	public static final Integer COMPONENT_SECTOR_Y = 100;
	public static final Integer COMPONENT_SECTOR_WIDTH = 700;
	public static final Integer COMPONENT_SECTOR_HEIGHT = 1000;
	public static final Integer START_SECTOR_X = 0;
	public static final Integer START_SECTOR_Y = 0;
	public static final Integer START_SECTOR_WIDTH = 50;
	public static final Integer START_SECTOR_HEIGHT = 1000;
	public static final Integer END_SECTOR_X = 800;
	public static final Integer END_SECTOR_Y = 0;
	public static final Integer END_SECTOR_WIDTH = 50;
	public static final Integer END_SECTOR_HEIGHT = 1000;

	private Document document;
	private List<JUCMConnection> connections = new LinkedList<JUCMConnection>();
	private List<JUCMResponsibility> responsibilityNodes = new LinkedList<JUCMResponsibility>();
	private List<JUCMComponent> componentNodes = new LinkedList<JUCMComponent>();
	private List<JUCMNode> nodes = new LinkedList<JUCMNode>();
	private Integer maxID;

	// Constructors

	public JUCMExporter() {
		this.setMaxID(100);
	}

	// Getters and Setters

	public Document getDocument(){
		return document;
	}

	public List<JUCMConnection> getConnections(){
		return connections;
	}

	public List<JUCMNode> getNodes(){
		return nodes;
	}


	public Integer getMaxID(){
		return maxID;
	}

	public void setMaxID(Integer maxID){
		this.maxID = maxID;
	}

	public Collection<JUCMComponent> getComponents() {
		return this.componentNodes;
	}

	// Methods

	public JUCMResponsibility getJUCMResponsibility(Responsibility element){
		for(JUCMResponsibility jucmResponsibility:responsibilityNodes){
			if(jucmResponsibility.getUCMElement().equals(element)){
				return jucmResponsibility;
			}
		}

		return null;
	}

	public JUCMComponent getJUCMComponent(ConceptualComponent component){
		for(JUCMComponent jucmComponent:componentNodes){
			if(jucmComponent.getUCMComponent().getName().equals(component.getName())){
				return jucmComponent;
			}
		}

		return null;
	}

	@Override
	public Document getDocument(UseCaseMap useCaseMap) {
		Document result = null;
		try{
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setValidating(false);
			dbf.setIgnoringComments(true);
			dbf.setIgnoringElementContentWhitespace(true);
			dbf.setNamespaceAware(true);

			DocumentBuilder documentBuilder = dbf.newDocumentBuilder();
			result = documentBuilder.newDocument();
			configureHeaders(result);
			configureUCMSpec(result);
			configureGrlSpec(result);
			this.document = result;
			generate(useCaseMap);
		} catch (DOMException e){
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}

		return result;
	}

	private void configureHeaders(Document document){

		Element urnSpecificationElement = document.createElementNS(urnNS, "urn:URNspec");
		document.appendChild(urnSpecificationElement);

		urnSpecificationElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:xmi", xmiNS);
		urnSpecificationElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:xsi", xsiNS);
		urnSpecificationElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:ucm.map", ucmMapNS);
		urnSpecificationElement.setAttributeNS(xmiNS, "xmi:version", "2.0");

		urnSpecificationElement.setAttribute("name", "URNspec");
		urnSpecificationElement.setAttribute("author", "bcaimmi");
		urnSpecificationElement.setAttribute("specVersion", "9");
		urnSpecificationElement.setAttribute("urnVersion", "1.27");
		urnSpecificationElement.setAttribute("nextGlobalID", "117");

		Element metadataElement = document.createElement("metadata");
		urnSpecificationElement.appendChild(metadataElement);
		metadataElement.setAttribute("name", "_Use0to100EvaluationRange");
		metadataElement.setAttribute("value", "false");
	}

	private void configureUCMSpec(Document document){
		Element urnSpecificationElement = document.getDocumentElement();
		Element ucmSpecElement = document.createElement("ucmspec");
		urnSpecificationElement.appendChild(ucmSpecElement);

		Element scenarioGroupsElement = document.createElement("scenarioGroups");
		scenarioGroupsElement.setAttribute("name", "ScenarioGroup5");
		scenarioGroupsElement.setAttribute("id", "5");
		ucmSpecElement.appendChild(scenarioGroupsElement);

		Element scenariosElement = document.createElement("scenarios");
		scenariosElement.setAttribute("name", "ScenarioDef6");
		scenariosElement.setAttribute("id", "6");
		scenarioGroupsElement.appendChild(scenariosElement);
	}

	private void configureGrlSpec(Document document){
		Element urnSpecificationElement = document.getDocumentElement();
		Element grlSpecElement = document.createElement("grlspec");
		urnSpecificationElement.appendChild(grlSpecElement);

		Element auxElement = document.createElement("groups");
		auxElement.setAttribute("id", "3");
		auxElement.setAttribute("name", "StrategiesGroup3");
		auxElement.setAttribute("strategies", "4");
		grlSpecElement.appendChild(auxElement);

		auxElement = document.createElement("strategies");
		auxElement.setAttribute("id", "4");
		auxElement.setAttribute("name", "EvaluationStrategy4");
		auxElement.setAttribute("author", "bcaimmi");
		auxElement.setAttribute("group", "3");
		grlSpecElement.appendChild(auxElement);

		auxElement = document.createElement("impactModel");
		grlSpecElement.appendChild(auxElement);

		auxElement = document.createElement("indicatorGroup");
		auxElement.setAttribute("id", "7");
		auxElement.setAttribute("name", "Time");
		auxElement.setAttribute("isRedesignCategory", "true");
		grlSpecElement.appendChild(auxElement);

		auxElement = document.createElement("indicatorGroup");
		auxElement.setAttribute("id", "8");
		auxElement.setAttribute("name", "Cost");
		auxElement.setAttribute("isRedesignCategory", "true");
		grlSpecElement.appendChild(auxElement);

		auxElement = document.createElement("indicatorGroup");
		auxElement.setAttribute("id", "9");
		auxElement.setAttribute("name", "Quality");
		auxElement.setAttribute("isRedesignCategory", "true");
		grlSpecElement.appendChild(auxElement);

		auxElement = document.createElement("indicatorGroup");
		auxElement.setAttribute("id", "10");
		auxElement.setAttribute("name", "Flexibility");
		auxElement.setAttribute("isRedesignCategory", "true");
		grlSpecElement.appendChild(auxElement);

		auxElement = document.createElement("featureModel");
		grlSpecElement.appendChild(auxElement);
	}



	private void generate(UseCaseMap useCaseMap){
		connections.clear();
		responsibilityNodes.clear();
		nodes.clear();

		Element urnSpecificationElement = document.getDocumentElement();
		Element ucmSpecElement = document.createElement("urndef");
		urnSpecificationElement.appendChild(ucmSpecElement);

		Element specDiagrams = document.createElement("specDiagrams");
		specDiagrams.setAttributeNS(xsiNS, "xsi:type", "ucm.map:UCMmap");
		specDiagrams.setAttribute("name", "UCMmap2");
		specDiagrams.setAttribute("id", "2");

		ucmSpecElement.appendChild(specDiagrams);

		/* Creation of components */
		for (ConceptualComponent component : useCaseMap.getComponents()){
			if (component.getResponsibilities().size() > 0) {
				JUCMComponent node = new JUCMComponent(this, component);
				this.componentNodes.add(node);
			}
		}

		ArrayList<Responsibility> resps = new ArrayList<Responsibility>();
		for (CausalRelationship causalRelationship : useCaseMap.getRelations()) {

			if (!resps.contains(causalRelationship.getResp1())) {
				resps.add(causalRelationship.getResp1());
			}

			if (causalRelationship.getResp2() != null && !resps.contains(causalRelationship.getResp2())) {
				resps.add(causalRelationship.getResp2());
			}

		}

		// Sort them according to NextResponsibilitiesSize from highest to lowest
		Collections.sort(resps, new Comparator<Responsibility>() {

			@Override
			public int compare(Responsibility o1, Responsibility o2) {
				return o1.getNextResponsibilitiesSize().compareTo(o2.getNextResponsibilitiesSize());
			}
			
		});
		Collections.reverse(resps);
		
		for (Responsibility element : resps){
			JUCMResponsibility node = new JUCMResponsibility(this, element);
			this.responsibilityNodes.add(node);
		}

		for (JUCMResponsibility node : responsibilityNodes){

			// Check the following elements to the current one, in case of being 0 an End Point is created, in case of being greater than 1 an OrFork
			if (node.getUCMElement().getNextResponsibilities().size() == 0){
				// EndPoint is created
				JUCMEndPoint endPointElement = new JUCMEndPoint(this);
				new JUCMConnection(this, node, endPointElement);
			} else 
				if (node.getUCMElement().getNextResponsibilities().size() > 1){
					// OrFork is created
					JUCMOrFork orForkNode = new JUCMOrFork(this);
					new JUCMConnection(this, node, orForkNode);

					for(Responsibility nextElement : node.getUCMElement().getNextResponsibilities()){
						JUCMResponsibility nextNode = getJUCMResponsibility(nextElement);
						new JUCMConnection(this, orForkNode, nextNode);
					}
				} else {
					// Elements are linked directly
					JUCMResponsibility nextElement = getJUCMResponsibility(node.getUCMElement().getNextResponsibilities().iterator().next());

					// I contemplate the case in which a responsibility has more than one predecessor
					if(nextElement.getUCMElement().getPrevResponsibilities().size()==1){
						new JUCMConnection(this, node, nextElement);
					}
				}

			// Check the elements prior to the current one, in case of 0 a Start Point is created, in case of being greater than 1 an OrJoin 
			if(node.getUCMElement().getPrevResponsibilities().size()==0){
				// Start Point is created
				JUCMStartPoint startPointElement = new JUCMStartPoint(this);
				new JUCMConnection(this, startPointElement, node);
			} else 
				if(node.getUCMElement().getPrevResponsibilities().size()>1){
					// OrJoin is created
					JUCMOrJoin orJoinNode = new JUCMOrJoin(this);
					new JUCMConnection(this, orJoinNode, node);

					for(Responsibility prevElement:node.getUCMElement().getPrevResponsibilities()){
						JUCMResponsibility prevNode = getJUCMResponsibility(prevElement);
						new JUCMConnection(this, prevNode, orJoinNode);
					}
				}
		}

		/* I generate the XML of the components */
		for(JUCMComponent component:componentNodes){
			component.generate(specDiagrams);
		}

		/* I generate the XML of responsibilities, orJoins, orForks, startPoints and endPoints */
		for(JUCMNode node : nodes){
			node.generate(specDiagrams);
		}

		/* I generate the connections XML */
		for(JUCMConnection connection:connections){
			connection.generate(specDiagrams);
		}
	}





}
