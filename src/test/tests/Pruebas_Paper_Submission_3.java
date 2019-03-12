package test.tests;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import model.Project;
import model.causalRelationship.CausalRelationship;
import model.requirement.Requirement;
import model.responsibility.Responsibility;
import pipeline.PipelineParalelo;
import test.tests.loadResults.ExpectedResult;
import test.tests.loadResults.ExpectedResultSeq;
import test.tests.loadResults.LoadResults;
import utils.io.ProjectIO;

public class Pruebas_Paper_Submission_3 {

	public static void main(String[] args) {

		// Proyecto
		Integer proyecto = 2;
		Boolean testSecuencializacion = true;

		ProjectIO reader = new ProjectIO();
		Project project = null;

		switch (proyecto){
		case 0:{
			project = reader.readFromFile("." + File.separator + "projects" + File.separator + "exampleUCM.txt");
			break;
		}
		case 1:{
			project = reader.readFromFile("." + File.separator + "projects" + File.separator + "proyecto_1_[spaceFractions].txt");
			if (testSecuencializacion){
				getRespProject1(project, "proyecto_1_1_seq.txt");
			}
			break;
		}
		case 2:{
			project = reader.readFromFile("." + File.separator + "projects" + File.separator + "proyecto_2_[splitMerge].txt");
			if (testSecuencializacion){
				getRespProject1(project, "proyecto_1_2_seq.txt");
			}
			break;
		}
		case 3:{
			project = reader.readFromFile("." + File.separator + "projects" + File.separator + "proyecto_3_[aloha].txt");
			if (testSecuencializacion){
				getRespProject1(project, "proyecto_1_3_seq.txt");
			}
			break;
		}
		}

		// Se ejecutra el pipeline
		// Objeto encargado de realizar las diferentes secuencias de procesamiento sobre un proyecto.
		PipelineParalelo pipeline = new PipelineParalelo(project, null);
		pipeline.setNlpIP("http://10.1.4.166");
		//pipeline.setNlpIP("http://corenlp.run");
		//pipeline.setNlpPort(80);
		pipeline.firstStep();
		if (testSecuencializacion)
			pipeline.setType(PipelineParalelo.TOPROCESS.STAGE2);
		pipeline.run();		

		// Chequear los resultados
		switch (proyecto) {
		case 1: {
			if (!testSecuencializacion)
				checkResultsProject(project, "proyecto_1_1.txt");
			else {
				checkSeqResults(project, "proyecto_1_1_seq.txt");
			}
			break;
		}
		case 2: {
			if (!testSecuencializacion)
				checkResultsProject(project, "proyecto_1_2.txt");
			else {
				checkSeqResults(project, "proyecto_1_2_seq.txt");
			}
			break;
		}
		case 3: {
			if (!testSecuencializacion)
				checkResultsProject(project, "proyecto_1_3.txt");
			else {
				checkSeqResults(project, "proyecto_1_3_seq.txt");
			}
			break;
		}
		}

	}

	private static void getRespProject1(Project project, String file) {

		LoadResults loadResults = new LoadResults();
		loadResults.loadSeq("." + File.separator + "projects" + File.separator + "results" + File.separator + file );
		HashMap<String, ExpectedResultSeq> expectedResults = loadResults.getExpectedResultsSeq();

		// Cargamos las responsabilidades bien

		for (int reqIDs = 0; reqIDs < project.getRequirements().size(); reqIDs++) {

			Requirement requirement = project.getRequirements().get(reqIDs);
			ExpectedResultSeq expResults = expectedResults.get(requirement.getId());

			if (expResults != null) {

				ArrayList<String> expectedResponsibilities = expResults.getExpectedResponsibilities();

				// Las convertimos bien
				ArrayList<Responsibility> respsToAdd = new ArrayList<Responsibility>();
				int i = 1;
				for (String txtResp : expectedResponsibilities) {

					String[] split = txtResp.split(":");
					if (split != null) {
						String[] primero = split[0].split("_");
						String verb = primero[0].trim();
						String dobj = primero[2].trim();
						String indexFirst = primero[1].trim();
						String indexSecond = primero[3].trim();

						String sentenceNumber = "1";
						if (split.length > 1)
								sentenceNumber = split[1].trim();

						String verbToken = verb + "-" + indexFirst;
						String dobjToken = dobj + "-" + indexSecond;
						if (split.length > 2) {
							String[] tercero = split[2].split("_");
							verbToken = tercero[0].trim();
							dobjToken = tercero[1].trim();
						}
						Responsibility toAdd = new Responsibility(verb, dobj, Integer.parseInt(indexFirst), Integer.parseInt(indexSecond));
						toAdd.setId("" + (reqIDs+1) + "-" + i++);
						toAdd.setSentenceID(Integer.parseInt(sentenceNumber));
						toAdd.setVerbBaseForm(verb, "VB");
						toAdd.setVerbToken(verbToken);
						toAdd.setDobjToken(dobjToken);

						respsToAdd.add(toAdd);

					}else {
						System.out.println("CASO MAL CARGADO = " + txtResp);
					}

				}

				// Cargamos las responsabilidades
				requirement.addAllResponsabilities(respsToAdd);
			}
		}

	}

	private static Integer calculateTN(Integer VP, ArrayList<String> expectedResponsibilities, ArrayList<Responsibility> detectedResponsibilities) {
		// TN = VP - deseadas (TP + FN) - (no deseadas que no se repiten en el verbo)

		Integer out = VP - expectedResponsibilities.size();

		for (Responsibility resp : detectedResponsibilities) {
			Boolean paso = true;
			for (String respExp : expectedResponsibilities) {
				String auxResp = resp.getVerb().toLowerCase().trim();
				String auxRespExp = respExp.split("_")[0].toLowerCase().trim();

				if (auxResp.equals(auxRespExp)) {
					paso = false;
				}
			}	
			if (paso) {
				out--;
			}
		}

		if (out < 0)
			out = 0;

		return out;
	}

	private static void checkResultsProject(Project project, String file) {

		System.out.println("\n\n=======  RESULTADOS  ===============================================\n");

		LoadResults loadResults = new LoadResults();
		loadResults.load("." + File.separator + "projects" + File.separator + "results" + File.separator + file );
		HashMap<String, ExpectedResult> expectedResults = loadResults.getExpectedResults();

		// Variables auxiliares
		Integer totalTP = 0;
		Integer totalFP = 0;
		Integer totalFN = 0;
		Integer totalVP = 0;
		Integer totalTN = 0;
		Integer totalRespDetectadas = 0;
		Integer totalRespDeseadas = 0;
		Double Accuracy = 0.0;
		Double Precision = 0.0;
		Double Recall = 0.0;

		for (int reqIDs = 0; reqIDs < project.getRequirements().size(); reqIDs++) {

			System.out.println("Req - [" + (reqIDs + 1) + "/" + project.getRequirements().size() + "]");

			Requirement requirement = project.getRequirements().get(reqIDs);
			ArrayList<Responsibility> responsibilities = requirement.getResponsibilities();

			ExpectedResult expResults = expectedResults.get(requirement.getId());

			if (expResults != null) {

				System.out.println("\tResponsabilidades deseadas [" + expResults.getExpectedResponsibilities().size() + "]: " + expResults.getExpectedResponsibilities());
				System.out.println("\tResponsabilidades detectadas [" + responsibilities.size() + "]");

				ArrayList<Responsibility> TPList = new ArrayList<Responsibility>();
				ArrayList<Responsibility> FPList = (ArrayList<Responsibility>) responsibilities.clone();

				for (Responsibility responsibility : responsibilities) {
					System.out.println("\t\tResponsabilidad detectada: " + responsibility.getSimpleTextForm());

					if (expResults.getExpectedResponsibilities().contains(responsibility.getSimpleTextForm())) {
						// Incrementamos el TP aca porque la encontre bien
						TPList.add(responsibility);
						FPList.remove(responsibility);
					}					
				}

				Integer TP = TPList.size();
				Integer FP = FPList.size();
				Integer FN = expResults.getExpectedResponsibilities().size() - TP;
				//Integer TN = expResults.getVerbPhrases() - TP - FN;
				Integer TN = calculateTN(expResults.getVerbPhrases(), expResults.getExpectedResponsibilities(), responsibilities);
				System.out.println("\tTP = " + TP);
				System.out.println("\tFP = " + FP);
				System.out.println("\tFN = " + FN);
				System.out.println("\tTN = " + TN);
				System.out.println();

				// Actualizar
				totalTP += TP;
				totalFN += FN;
				totalFP += FP;
				totalTN += TN;
				totalVP += expResults.getVerbPhrases();
				totalRespDeseadas += expResults.getExpectedResponsibilities().size();
				totalRespDetectadas += responsibilities.size();

			}
		}

		// Se calculan los resultados
		Accuracy = (((double) (totalTP + totalTN)) / (totalTP + totalTN + totalFP + totalFN));
		Precision = (((double) totalTP) / (totalTP + totalFP));
		Recall = (((double) totalTP) / (totalTP + totalFN));

		// Se muestran los resultados finales para el proyecto

		System.out.println("Total Requerimientos = " + project.getRequirements().size());
		System.out.println("Total VP (Verb Phrases) = " + totalVP);
		System.out.println("Total Responsabilidades Deseadas = " + totalRespDeseadas);
		System.out.println("Total Responsabilidades Detectadas = " + totalRespDetectadas);
		System.out.println("TP = " + totalTP);
		System.out.println("FP = " + totalFP);
		System.out.println("FN = " + totalFN);
		System.out.println("TN = " + totalTN);		
		System.out.println("Accuracy = " + Accuracy);
		System.out.println("Precision = " + Precision);
		System.out.println("Recall = " + Recall);
	}

	private static void checkSeqResults(Project project, String file) {

		System.out.println("\n\n=======  RESULTADOS  ===============================================\n");

		LoadResults loadResults = new LoadResults();
		loadResults.loadSeq("." + File.separator + "projects" + File.separator + "results" + File.separator + file );
		HashMap<String, ExpectedResultSeq> expectedResultsSeq = loadResults.getExpectedResultsSeq();

		// Variables auxiliares
		Integer totalTP = 0;
		Integer totalFP = 0;
		Integer totalFN = 0;
		Integer totalSeqDetectadas = 0;
		Integer totalSeqDeseadas = 0;
		Double Precision = 0.0;
		Double Recall = 0.0;

		for (int reqIDs = 0; reqIDs < project.getRequirements().size(); reqIDs++) {

			System.out.println("Req - [" + (reqIDs + 1) + "/" + project.getRequirements().size() + "]");

			Requirement requirement = project.getRequirements().get(reqIDs);
			ArrayList<CausalRelationship> relations = requirement.getCausalRelationships();

			ExpectedResultSeq expResults = expectedResultsSeq.get(requirement.getId());
			
			if (expResults != null) {

				System.out.println("\tRelaciones deseadas [" + expResults.getExpectedCausalRelationships().size() + "]: " + expResults.getExpectedCausalRelationships());
				System.out.println("\tRelaciones detectadas [" + relations.size() + "]");

				ArrayList<CausalRelationship> TPList = new ArrayList<CausalRelationship>();
				ArrayList<CausalRelationship> FPList = (ArrayList<CausalRelationship>) relations.clone();

				for (CausalRelationship cr : relations) {
					System.out.println("\t\tRelaciones detectada: " + cr.toString() + "  " + cr.getMatchedPattern());

					// Lo convertimos en algo manejable
					String crAux = cr.getResp1().getSimpleTextForm() + " -> " + cr.getResp2().getSimpleTextForm();

					if (expResults.getExpectedCausalRelationships().contains(crAux)) {
						// Incrementamos el TP aca porque la encontre bien
						TPList.add(cr);
						FPList.remove(cr);
					}					
				}

				Integer TP = TPList.size();
				Integer FP = FPList.size();
				Integer FN = expResults.getExpectedCausalRelationships().size() - TP;
				if (expResults.getExpectedCausalRelationships().size() > 0 && expResults.getExpectedCausalRelationships().get(0).equals(""))
					FN = 0;
				System.out.println("\tTP = " + TP);
				System.out.println("\tFP = " + FP);
				System.out.println("\tFN = " + FN);
				System.out.println();

				// Actualizar
				totalTP += TP;
				totalFN += FN;
				totalFP += FP;
				totalSeqDeseadas += expResults.getExpectedCausalRelationships().size();
				totalSeqDetectadas += relations.size();

			}else {
				System.out.println("\tRelaciones deseadas [0] ");
				System.out.println("\tRelaciones detectadas [" + relations.size() + "]");
				System.out.println();

			}
		}

		// Se calculan los resultados
		Precision = (((double) totalTP) / (totalTP + totalFP));
		Recall = (((double) totalTP) / (totalTP + totalFN));

		// Se muestran los resultados finales para el proyecto

		System.out.println("Total Requerimientos = " + project.getRequirements().size());
		System.out.println("Total Relaciones Deseadas = " + totalSeqDeseadas);
		System.out.println("Total Relaciones Detectadas = " + totalSeqDetectadas);
		System.out.println("TP = " + totalTP);
		System.out.println("FP = " + totalFP);
		System.out.println("FN = " + totalFN);	
		System.out.println("Precision = " + Precision);
		System.out.println("Recall = " + Recall);


	}

	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	// Solo la que les faltan
	private static ArrayList<ArrayList<Responsibility>> getResponsibilitiesProject1(){
		ArrayList<ArrayList<Responsibility>> out = new ArrayList<ArrayList<Responsibility>>();

		//Req-1
		ArrayList<Responsibility> req1 = new ArrayList<Responsibility>();
		req1.add(new Responsibility("proceed", "menu", 19 , 23));
		out.add(req1);

		//Req-2
		ArrayList<Responsibility> req2 = new ArrayList<Responsibility>();
		req2.add(new Responsibility("forward", "user", 27 , 29));
		out.add(req2);

		//Req-3
		ArrayList<Responsibility> req3 = new ArrayList<Responsibility>();
		req3.add(new Responsibility("click", "button", 4 , 8));
		out.add(req3);

		//Req-4
		ArrayList<Responsibility> req4 = new ArrayList<Responsibility>();
		out.add(req4);

		//Req-5
		ArrayList<Responsibility> req5 = new ArrayList<Responsibility>();
		out.add(req5);

		//Req-6
		ArrayList<Responsibility> req6 = new ArrayList<Responsibility>();
		out.add(req6);

		//Req-7
		ArrayList<Responsibility> req7 = new ArrayList<Responsibility>();
		req7.add(new Responsibility("move", "question", 20 , 24));
		out.add(req7);

		//Req-8
		ArrayList<Responsibility> req8 = new ArrayList<Responsibility>();
		out.add(req8);

		//Req-9
		ArrayList<Responsibility> req9 = new ArrayList<Responsibility>();
		out.add(req9);

		//Req-10
		ArrayList<Responsibility> req10 = new ArrayList<Responsibility>();
		out.add(req10);

		//Req-11
		ArrayList<Responsibility> req11 = new ArrayList<Responsibility>();
		req11.add(new Responsibility("return", "menu", 11 , 14));
		out.add(req11);

		//Req-12
		ArrayList<Responsibility> req12 = new ArrayList<Responsibility>();
		req12.add(new Responsibility("return", "menu", 11 , 14));
		req12.add(new Responsibility("exit", "game", 17 , 19));
		out.add(req12);

		//Req-13
		ArrayList<Responsibility> req13 = new ArrayList<Responsibility>();
		req13.add(new Responsibility("input", "data", 4 , 6));
		out.add(req13);

		//Req-14
		ArrayList<Responsibility> req14 = new ArrayList<Responsibility>();
		req14.add(new Responsibility("check", "data", 27 , 31));
		out.add(req14);

		//Req-15
		ArrayList<Responsibility> req15 = new ArrayList<Responsibility>();
		out.add(req15);

		return out;
	}

	// Solo la que les faltan
	private static ArrayList<ArrayList<Responsibility>> getResponsibilitiesProject2(){
		ArrayList<ArrayList<Responsibility>> out = new ArrayList<ArrayList<Responsibility>>();

		//Req-1
		ArrayList<Responsibility> req1 = new ArrayList<Responsibility>();
		out.add(req1);

		//Req-2
		ArrayList<Responsibility> req2 = new ArrayList<Responsibility>();
		out.add(req2);

		//Req-3
		ArrayList<Responsibility> req3 = new ArrayList<Responsibility>();
		req3.add(new Responsibility("reload", "pdf", 58 , 60));
		out.add(req3);

		//Req-4
		ArrayList<Responsibility> req4 = new ArrayList<Responsibility>();
		out.add(req4);

		//Req-5
		ArrayList<Responsibility> req5 = new ArrayList<Responsibility>();
		out.add(req5);

		//Req-6
		ArrayList<Responsibility> req6 = new ArrayList<Responsibility>();
		out.add(req6);

		//Req-7
		ArrayList<Responsibility> req7 = new ArrayList<Responsibility>();
		req7.add(new Responsibility("sets", "parameters", 7 , 10));
		req7.add(new Responsibility("presses", "button", 13 , 16));
		out.add(req7);

		//Req-8
		ArrayList<Responsibility> req8 = new ArrayList<Responsibility>();
		out.add(req8);

		//Req-9
		ArrayList<Responsibility> req9 = new ArrayList<Responsibility>();
		out.add(req9);

		//Req-10
		ArrayList<Responsibility> req10 = new ArrayList<Responsibility>();
		out.add(req10);

		//Req-11
		ArrayList<Responsibility> req11 = new ArrayList<Responsibility>();
		req11.add(new Responsibility("write", "all", 12 , 15));
		req11.add(new Responsibility("write", "page", 12 , 28));
		req11.add(new Responsibility("write", "page", 12 , 41));
		req11.add(new Responsibility("write", "number", 12 , 83));
		req11.add(new Responsibility("write", "commas", 12 , 96));
		req11.add(new Responsibility("merge", "document", 19 , 22));
		out.add(req11);

		//Req-12
		ArrayList<Responsibility> req12 = new ArrayList<Responsibility>();
		req12.add(new Responsibility("set", "password", 8 , 10));
		out.add(req12);

		//Req-13
		ArrayList<Responsibility> req13 = new ArrayList<Responsibility>();
		out.add(req13);

		//Req-14
		ArrayList<Responsibility> req14 = new ArrayList<Responsibility>();
		req14.add(new Responsibility("add", "that", 11 , 12));
		out.add(req14);

		//Req-15
		ArrayList<Responsibility> req15 = new ArrayList<Responsibility>();
		out.add(req15);

		//Req-16
		ArrayList<Responsibility> req16 = new ArrayList<Responsibility>();
		out.add(req16);

		//Req-17
		ArrayList<Responsibility> req17 = new ArrayList<Responsibility>();
		out.add(req17);

		//Req-18
		ArrayList<Responsibility> req18 = new ArrayList<Responsibility>();
		out.add(req18);

		//Req-19
		ArrayList<Responsibility> req19 = new ArrayList<Responsibility>();
		out.add(req19);

		//Req-20
		ArrayList<Responsibility> req20 = new ArrayList<Responsibility>();
		req20.add(new Responsibility("set", "parameters",  7, 9));
		req20.add(new Responsibility("presses", "button",  12, 15));
		out.add(req20);

		//Req-21
		ArrayList<Responsibility> req21 = new ArrayList<Responsibility>();
		out.add(req21);

		//Req-22
		ArrayList<Responsibility> req22 = new ArrayList<Responsibility>();
		out.add(req22);

		//Req-23
		ArrayList<Responsibility> req23 = new ArrayList<Responsibility>();
		out.add(req23);

		//Req-24
		ArrayList<Responsibility> req24 = new ArrayList<Responsibility>();
		out.add(req24);

		//Req-25
		ArrayList<Responsibility> req25 = new ArrayList<Responsibility>();
		out.add(req25);

		//Req-26
		ArrayList<Responsibility> req26 = new ArrayList<Responsibility>();
		req26.add(new Responsibility("set", "parameters",  7, 9));
		req26.add(new Responsibility("presses", "button",  12, 15));
		out.add(req26);

		//Req-27
		ArrayList<Responsibility> req27 = new ArrayList<Responsibility>();
		out.add(req27);

		//Req-28
		ArrayList<Responsibility> req28 = new ArrayList<Responsibility>();
		out.add(req28);

		//Req-29
		ArrayList<Responsibility> req29 = new ArrayList<Responsibility>();
		out.add(req29);

		//Req-30
		ArrayList<Responsibility> req30 = new ArrayList<Responsibility>();
		out.add(req30);

		//Req-31
		ArrayList<Responsibility> req31 = new ArrayList<Responsibility>();
		req31.add(new Responsibility("set", "parameters",  7, 9));
		req31.add(new Responsibility("presses", "button",  12, 15));
		out.add(req31);

		//Req-32
		ArrayList<Responsibility> req32 = new ArrayList<Responsibility>();
		out.add(req32);

		//Req-33
		ArrayList<Responsibility> req33 = new ArrayList<Responsibility>();
		out.add(req33);

		//Req-34
		ArrayList<Responsibility> req34 = new ArrayList<Responsibility>();
		out.add(req34);

		//Req-35
		ArrayList<Responsibility> req35 = new ArrayList<Responsibility>();
		out.add(req35);

		//Req-36
		ArrayList<Responsibility> req36 = new ArrayList<Responsibility>();
		out.add(req36);

		//Req-37
		ArrayList<Responsibility> req37 = new ArrayList<Responsibility>();
		out.add(req37);

		//Req-38
		ArrayList<Responsibility> req38 = new ArrayList<Responsibility>();
		out.add(req38);

		//Req-39
		ArrayList<Responsibility> req39 = new ArrayList<Responsibility>();
		out.add(req39);

		//Req-40
		ArrayList<Responsibility> req40 = new ArrayList<Responsibility>();
		out.add(req40);

		//Req-41
		ArrayList<Responsibility> req41 = new ArrayList<Responsibility>();
		out.add(req41);

		//Req-42
		ArrayList<Responsibility> req42 = new ArrayList<Responsibility>();
		out.add(req42);

		//Req-43
		ArrayList<Responsibility> req43 = new ArrayList<Responsibility>();
		out.add(req43);

		//Req-44
		ArrayList<Responsibility> req44 = new ArrayList<Responsibility>();
		out.add(req44);


		return out;
	}

	// Solo la que les faltan
	private static ArrayList<ArrayList<Responsibility>> getResponsibilitiesProject3(){
		ArrayList<ArrayList<Responsibility>> out = new ArrayList<ArrayList<Responsibility>>();

		//Req-1
		ArrayList<Responsibility> req1 = new ArrayList<Responsibility>();
		req1.add(new Responsibility("agree", "terms", 64 , 67));
		out.add(req1);

		//Req-2
		ArrayList<Responsibility> req2 = new ArrayList<Responsibility>();
		out.add(req2);

		//Req-3
		ArrayList<Responsibility> req3 = new ArrayList<Responsibility>();
		out.add(req3);

		//Req-4
		ArrayList<Responsibility> req4 = new ArrayList<Responsibility>();
		out.add(req4);

		//Req-5
		ArrayList<Responsibility> req5 = new ArrayList<Responsibility>();
		out.add(req5);

		//Req-6
		ArrayList<Responsibility> req6 = new ArrayList<Responsibility>();
		out.add(req6);

		//Req-7
		ArrayList<Responsibility> req7 = new ArrayList<Responsibility>();
		out.add(req7);

		//Req-8
		ArrayList<Responsibility> req8 = new ArrayList<Responsibility>();
		out.add(req8);

		//Req-9
		ArrayList<Responsibility> req9 = new ArrayList<Responsibility>();
		out.add(req9);

		//Req-10
		ArrayList<Responsibility> req10 = new ArrayList<Responsibility>();
		out.add(req10);

		//Req-11
		ArrayList<Responsibility> req11 = new ArrayList<Responsibility>();
		out.add(req11);

		//Req-12
		ArrayList<Responsibility> req12 = new ArrayList<Responsibility>();
		out.add(req12);

		//Req-13
		ArrayList<Responsibility> req13 = new ArrayList<Responsibility>();
		out.add(req13);

		//Req-14
		ArrayList<Responsibility> req14 = new ArrayList<Responsibility>();
		req14.add(new Responsibility("ignore", "request", 57 , 59));
		out.add(req14);

		//Req-15
		ArrayList<Responsibility> req15 = new ArrayList<Responsibility>();
		out.add(req15);

		//Req-16
		ArrayList<Responsibility> req16 = new ArrayList<Responsibility>();
		out.add(req16);

		//Req-17
		ArrayList<Responsibility> req17 = new ArrayList<Responsibility>();
		out.add(req17);

		//Req-18
		ArrayList<Responsibility> req18 = new ArrayList<Responsibility>();
		out.add(req18);

		//Req-19
		ArrayList<Responsibility> req19 = new ArrayList<Responsibility>();
		out.add(req19);

		//Req-20
		ArrayList<Responsibility> req20 = new ArrayList<Responsibility>();
		req20.add(new Responsibility("set", "parameters",  7, 9));
		req20.add(new Responsibility("presses", "button",  12, 15));
		out.add(req20);

		//Req-21
		ArrayList<Responsibility> req21 = new ArrayList<Responsibility>();
		out.add(req21);

		//Req-22
		ArrayList<Responsibility> req22 = new ArrayList<Responsibility>();
		out.add(req22);

		//Req-23
		ArrayList<Responsibility> req23 = new ArrayList<Responsibility>();
		out.add(req23);

		//Req-24
		ArrayList<Responsibility> req24 = new ArrayList<Responsibility>();
		out.add(req24);

		//Req-25
		ArrayList<Responsibility> req25 = new ArrayList<Responsibility>();
		out.add(req25);

		//Req-26
		ArrayList<Responsibility> req26 = new ArrayList<Responsibility>();
		req26.add(new Responsibility("set", "parameters",  7, 9));
		req26.add(new Responsibility("presses", "button",  12, 15));
		out.add(req26);

		//Req-27
		ArrayList<Responsibility> req27 = new ArrayList<Responsibility>();
		out.add(req27);

		//Req-28
		ArrayList<Responsibility> req28 = new ArrayList<Responsibility>();
		out.add(req28);

		//Req-29
		ArrayList<Responsibility> req29 = new ArrayList<Responsibility>();
		out.add(req29);

		//Req-30
		ArrayList<Responsibility> req30 = new ArrayList<Responsibility>();
		out.add(req30);

		//Req-31
		ArrayList<Responsibility> req31 = new ArrayList<Responsibility>();
		req31.add(new Responsibility("set", "parameters",  7, 9));
		req31.add(new Responsibility("presses", "button",  12, 15));
		out.add(req31);

		return out;
	}



}
