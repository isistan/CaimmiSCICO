package nlp;

import java.util.ArrayList;

import model.causalRelationship.CausalRelationship;
import model.responsibility.Responsibility;

public class ConditionalStructureAnalyzer {

	// Variables
	private Integer cantidadIFTHEN;
	private Integer cantidadAFTERBEFORE;
		
	// Constructors
	public ConditionalStructureAnalyzer(){

		this.cantidadIFTHEN = 0;
		this.cantidadAFTERBEFORE = 0;
		
	}

	// Getters and Setters

	public Integer getCantidadIFTHEN() {
		return cantidadIFTHEN;
	}

	public void setCantidadIFTHEN(Integer cantidadIFTHEN) {
		this.cantidadIFTHEN = cantidadIFTHEN;
	}

	public Integer getCantidadAFTERBEFORE() {
		return cantidadAFTERBEFORE;
	}

	public void setCantidadAFTERBEFORE(Integer cantidadAFTER) {
		this.cantidadAFTERBEFORE = cantidadAFTER;
	}
	
	// Methods

	public ArrayList<CausalRelationship> detectIFTHEN(ArrayList<String> words, ArrayList<Responsibility> responsibilities, Integer startPOS){
		ArrayList<CausalRelationship> out = new ArrayList<CausalRelationship>();

		int ifPOS = -1;
		int thenPOS = -1;

		for (int i = 0; i < words.size(); i++){

			if (words.get(i).toLowerCase().equals("if")){
				ifPOS = i + 1;
			}

			if (words.get(i).toLowerCase().equals("then") && ifPOS > -1 && i > ifPOS){
				thenPOS = i +  1;
			}

			if (words.get(i).toLowerCase().equals(",") && thenPOS == -1 && ifPOS > -1 && i > ifPOS){
				thenPOS = i + 1;
			}

		}

		if (ifPOS > -1 && thenPOS > -1){

			// Se muestra la estructura
			System.out.println("\tHay estructura IF THEN");
			String auxS = "\t";
			for (int i = 0; i < words.size(); i++){
				if (i == ifPOS - 1){
					auxS += "[IF] ";
				}else
					if (i == thenPOS - 1){
						auxS += "[THEN] ";
					}else 
						auxS += words.get(i) + " ";
			}

			System.out.println(auxS);
			
			cantidadIFTHEN++;
			
			// Se intentan detectar las relaciones causales

			ArrayList<Responsibility> enClausulaCondicional = new ArrayList<Responsibility>();
			ArrayList<Responsibility> enClausulaConsecuencia = new ArrayList<Responsibility>();

			// Verificamos que hayan responsabilidades en la clausula de condicion y en las de consecuencia
			for (Responsibility resp : responsibilities){

				if (resp.getFirstWordPosition() > ifPOS && resp.getLastWordPositionShort() < thenPOS /*&& resp.getFirstWordPosition() < startPOS*/){
					enClausulaCondicional.add(resp);
				}
				if (resp.getFirstWordPosition() > thenPOS && resp.getFirstWordPosition() < words.size()){
					enClausulaConsecuencia.add(resp);
				}
			}

			// Se hacen todas las combinaciones
			for (Responsibility resp1 : enClausulaCondicional){
				for (Responsibility resp2 : enClausulaConsecuencia){
					out.add(new CausalRelationship(resp1, resp2));
				}
			}
		}

		return out;
	}



	public ArrayList<CausalRelationship> detectTEMPORAL(ArrayList<String> words, ArrayList<Responsibility> responsibilities, Integer startPOS){
		ArrayList<CausalRelationship> out = new ArrayList<CausalRelationship>();

		String estructura = "null";
		int afterPOS = -1;
		int beforePOS = -1;
		int commaPOS = -1;

		for (int i = 0; i < words.size(); i++){

			if (words.get(i).toLowerCase().equals("after")){
				afterPOS = i + 1;
				estructura = "after";
			}

			if (words.get(i).toLowerCase().equals("before")){
				beforePOS = i + 1;
				estructura = "before";
			}
			
			if ( (words.get(i).toLowerCase().equals(",")) && ( ( (afterPOS > -1) && ( i > afterPOS ) )  ||  ( (beforePOS > -1) && (i > beforePOS) ) ) ){
				commaPOS = i + 1;
			}

		}

		if ((afterPOS > -1 && commaPOS > -1) || (beforePOS > -1 && commaPOS > -1)){

			// Se muestra la estructura
			System.out.println("\tHay estructura AFTER/BEFORE");
			String auxS = "\t";
			for (int i = 0; i < words.size(); i++){
				if (i == afterPOS - 1){
					auxS += "[AFTER] ";
				}else
					if (i == beforePOS - 1){
						auxS += "[BEFORE] ";
					}else
						if (i == commaPOS - 1){
							auxS += "[,] ";
						}else 
							auxS += words.get(i) + " ";
			}

			System.out.println(auxS);
			
			cantidadAFTERBEFORE++;
			
			// Se intentan detectar las relaciones causales

			ArrayList<Responsibility> enClausulaTemporal = new ArrayList<Responsibility>();
			ArrayList<Responsibility> enClausulaConsecuencia = new ArrayList<Responsibility>();

			// Verificamos que hayan responsabilidades en la clausula de condicion y en las de consecuencia
			for (Responsibility resp : responsibilities){

				if (afterPOS > -1 && resp.getFirstWordPosition() > afterPOS && resp.getLastWordPositionShort() < commaPOS /*&& resp.getFirstWordPosition() < startPOS*/){
					enClausulaTemporal.add(resp);
				}

				if (beforePOS > -1 && resp.getFirstWordPosition() > beforePOS && resp.getLastWordPositionShort() < commaPOS /* && resp.getFirstWordPosition() < startPOS*/){
					enClausulaTemporal.add(resp);
				}

				if (resp.getFirstWordPosition() > commaPOS && resp.getFirstWordPosition() < words.size()){
					enClausulaConsecuencia.add(resp);
				}

			}

			// Se hacen todas las combinaciones
			for (Responsibility resp1 : enClausulaTemporal){
				for (Responsibility resp2 : enClausulaConsecuencia){
					if (estructura.equals("after"))
						out.add(new CausalRelationship(resp1, resp2));
					else
						out.add(new CausalRelationship(resp2, resp1));
				}
			}

		}

		return out;
	}

}
