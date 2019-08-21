import java.io.File;
import java.util.ArrayList;

public class Main_Paper_UCM {

	// Main Method

	public static void main(String[] args) throws Exception {
		// Here the clustering files are read, which comes to be the responsibilities along with their class (component that should be
		// assigned in the cluster

		// All desired clustering algorithms are tested
		String filterArguments = "weka.filters.unsupervised.attribute.StringToWordVector -R first-last -W 100000 -prune-rate -1.0 -C -T -I -N 1 -L -stemmer weka.core.stemmers.NullStemmer -stopwords-handler weka.core.stopwords.Null -M 1 -tokenizer \"weka.core.tokenizers.WordTokenizer -delimiters \\\" \\\\r\\\\n\\\\t.,;:\\\\\\\'\\\\\\\"()?!\\\"\"";
		ArrayList<ResponsibilitiesClusterer> clusterers = new ArrayList<ResponsibilitiesClusterer>();

		// HierarchicalClusterer
		ResponsibilitiesClusterer clusterer0 = new ResponsibilitiesClusterer("weka.clusterers.HierarchicalClusterer -N 3 -L SINGLE -P -A \"weka.core.EuclideanDistance -R first-last\"",filterArguments,true);
		clusterers.add(clusterer0);

		// Xmeans
		ResponsibilitiesClusterer clusterer1 = new ResponsibilitiesClusterer("weka.clusterers.XMeans -I 1 -M 1000 -J 1000 -L 2 -H 4 -B 1.0 -C 0.5 -D \"weka.core.EuclideanDistance -R first-last\" -S 10",filterArguments,true);
		clusterers.add(clusterer1);

		// EM
		ResponsibilitiesClusterer clusterer2 = new ResponsibilitiesClusterer("weka.clusterers.EM -I 100 -N 2 -X 10 -max -1 -ll-cv 1.0E-6 -ll-iter 1.0E-6 -M 1.0E-6 -K 10 -num-slots 1 -S 100",filterArguments,true);
		clusterers.add(clusterer2);

		// DBScan
		ResponsibilitiesClusterer clusterer3 = new ResponsibilitiesClusterer("weka.clusterers.DBSCAN -E 0.9 -M 6 -A \"weka.core.EuclideanDistance -R first-last\"",filterArguments,true);
		clusterers.add(clusterer3);

		// PAM
		ResponsibilitiesClusterer clusterer4 = new DummyResponsibilitiesClusterer("", filterArguments, true, "PAM");
		clusterers.add(clusterer4);

		// KMeans
		ResponsibilitiesClusterer clusterer5 = new DummyResponsibilitiesClusterer("", filterArguments, true, "KMeans");
		clusterers.add(clusterer5);

		// The project type files are generated, which are the responsibilities, next to their class, and the assigned component
		// Project files are used to calculate metrics

		String arffPath = "D:\\EvalSico"; // PATH WHERE THE PROJECTS ARE
		
		int[] proyectos = {3}; // 3.arff it would be the project, in case of more each number is used as an indicator of the project

		for (ResponsibilitiesClusterer clusterer : clusterers){
			for (int i = 0; i < proyectos.length; i++){
				
				clusterer.performClustering(arffPath, proyectos[i], false,"_completo");
			
			}
		}		

		
		System.out.println("\n\n\n\n\n\n\n\n ################################################################################################### \n\n");

		ArrayList<String> toSearches = new ArrayList<String>();
		toSearches.add("");
	
		
		
		for (ResponsibilitiesClusterer clusterer : clusterers){
			for (int i = 0; i < proyectos.length; i++){
				for (String sss : toSearches){
								
				String toSearch = arffPath + File.separator + proyectos[i] + "_" + clusterer.getName() + sss +".arff";

				System.out.println(toSearch);
				
				ClusterEvaluation evaluation = new ClusterEvaluation(toSearch);

				System.out.println("\n\nAlgorithm : " + clusterer.getName());
				System.out.println("Project : " + proyectos[i]);
				System.out.println("Metrics---------------------------------------------------");
				System.out.println();

				System.out.println("Entropy =\t\t[" + evaluation.getEntropy() + "]"); 						
				System.out.println("Purity =\t\t[" + evaluation.getPurity() + "]"); 						
				System.out.println("F-Measure =\t\t[" + evaluation.getFmeasure() + "]");					
				System.out.println("Rand Index =\t\t[" + evaluation.getRandIndex() + "]");					
				System.out.println("Adjusted Rand Index =\t[" + evaluation.getAdjustedRandIndex() + "]");	

				System.out.println();
				
				System.out.println(evaluation.getEntropy() + "\t" + evaluation.getPurity() + "\t" +evaluation.getFmeasure() + "\t" +evaluation.getAdjustedRandIndex());
				
				System.out.println("\n#########################################################################\n");
				}
			}			
		}



	}

}

