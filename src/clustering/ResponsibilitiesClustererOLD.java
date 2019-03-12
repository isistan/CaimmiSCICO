package clustering;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import model.conceptualComponent.ConceptualComponent;
import model.responsibility.Responsibility;
import model.responsibility.SimpleResponsibility;
import weka.clusterers.AbstractClusterer;
import weka.clusterers.ClusterEvaluation;
import weka.clusterers.Clusterer;
import weka.clusterers.FilteredClusterer;
import weka.clusterers.HierarchicalClusterer;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.unsupervised.attribute.StringToWordVector;

public class ResponsibilitiesClustererOLD {

	// Variables

	private Boolean debugMode;
	private FilteredClusterer wekaClusterer;

	// Constructors

	public ResponsibilitiesClustererOLD(String arguments, String filterArguments, Boolean debugMode){
		super();
		this.debugMode = debugMode;

		if (arguments == null && filterArguments == null){
			//ERROR
		}else{
			if (arguments != null && filterArguments == null){
				// Solo clusterizador
				this.wekaClusterer = loadClusterer(arguments);				
			}else{
				if (arguments != null && filterArguments != null){
					// Clasificador con filtro
					this.wekaClusterer = loadClusterer(arguments, filterArguments);
				}else{
					//ERROR
				}
			}
		}
	}

	// Getters and Setters

	// Methods

	private FilteredClusterer loadClusterer(String arguments){
		FilteredClusterer out = null;
		String classifierName = arguments.split(" ")[0].trim();

		try {
			out = (FilteredClusterer) Class.forName(classifierName).newInstance();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			e.printStackTrace();
		}

		return out;
	}	

	private FilteredClusterer loadClusterer(String arguments, String filterArguments){
		FilteredClusterer out = new FilteredClusterer();
		AbstractClusterer clusterer = null;
		StringToWordVector filter = null;

		// Load filters

		String filterName = filterArguments.split(" ")[0].trim();
		filterArguments = filterArguments.substring(filterArguments.split(" ")[0].length()).trim();

		try {
			filter = (StringToWordVector) Class.forName(filterName).newInstance();
			String[] options = weka.core.Utils.splitOptions(filterArguments);
			filter.setOptions(options);	
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Load the clusterer

		String clustererName = arguments.split(" ")[0].trim();
		arguments = arguments.substring(arguments.split(" ")[0].length()).trim();

		try {
			clusterer = (AbstractClusterer) Class.forName(clustererName).newInstance();
			String[] options = weka.core.Utils.splitOptions(arguments);
			if (options.length > 1)
				clusterer.setOptions(options);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Se adjunta lo cargado
		out.setFilter(filter);
		out.setClusterer(clusterer);

		return out;
	}

	private Instances createData(ArrayList<Responsibility> responsibilities){
		Instances out;
		ArrayList<Attribute> attributes = new ArrayList<Attribute>();

		Attribute textAttribute = new Attribute("responsibility", (ArrayList<String>) null);

		// 1 - Se definen los atributos
		attributes.add(textAttribute);

		// 2 - Se crea la instancia
		out = new Instances("responsibilities", attributes, 0);

		// 3 - Se insertan los datos

		for (Responsibility responsibility : responsibilities){
			Instance instance = new DenseInstance(1);
			instance.setValue(textAttribute, responsibility.getLongForm());
			out.add(instance);
		}

		System.out.println(out);

		return out;
	}

	public ArrayList<ConceptualComponent> createDataAndClusterResponsibilities(ArrayList<Responsibility> responsibilities) {
		
		ArrayList<ConceptualComponent> out = new ArrayList<ConceptualComponent>();
		
		HashMap<Integer, ConceptualComponent> components = new HashMap<Integer, ConceptualComponent>();
		
		Instances data = createData(responsibilities);

		if (responsibilities.size() > 1){		
			try {
				wekaClusterer.buildClusterer(data);
				
				System.out.println(wekaClusterer.numberOfClusters());
				            
	            for(int i = 0; i < data.numInstances(); i++){
	            	
	            	Integer clusterNumber = wekaClusterer.clusterInstance(data.instance(i));
		            System.out.printf("Instance %d -> Cluster %d \n", i, clusterNumber);
		            
		            if (!components.containsKey(clusterNumber)){            	
			            ConceptualComponent conceptualComponent = new ConceptualComponent();
			            conceptualComponent.addResponsibility(responsibilities.get(i));
		            	components.put(clusterNumber, conceptualComponent);
		            }else{
		            	components.get(clusterNumber).addResponsibility(responsibilities.get(i));
		            }
		        }
	            
	            for (Integer key : components.keySet()){
	            	out.add(components.get(key));
	            }
			 
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return out; 
	}
}
