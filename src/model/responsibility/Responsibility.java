package model.responsibility;

import java.util.ArrayList;
import java.util.List;

import edu.mit.jwi.item.POS;
import edu.stanford.nlp.ling.WordTag;
import edu.stanford.nlp.process.Morphology;
import model.conceptualComponent.ConceptualComponent;
import model.exporters.jucm.JUCMConnection;
import utils.misc.Pair;


public class Responsibility implements Comparable{

	// Variables
	private String id;
	private String verb;
	private String dobj;	
	private String verbBaseForm;
	private ArrayList<Pair<String, POS>> recognitions;
	private Integer firstWordPosition;
	private Integer lastWordPositionShort;
	private Integer lastWordPositionLong;
	private String verbToken;
	private String dobjToken;	
	private ConceptualComponent component;
	private ArrayList<Responsibility> prevResponsibilities = new ArrayList<Responsibility>();
	private ArrayList<Responsibility> nextResponsibilities = new ArrayList<Responsibility>();
	private Boolean negated = false;
	private Integer sentenceID = null;

	// Constructors
	public Responsibility(String verb, String dobj){
		setVerb(verb);
		setDobj(dobj);
		this.recognitions = new ArrayList<Pair<String, POS>>();
	}

	public Responsibility(ArrayList<Pair<String, POS>> recognitions){
		setVerb("");
		setDobj(dobj);
		this.recognitions = recognitions;
	}

	public Responsibility(String verb, String dobj, Integer firstWord, Integer lastWordShort){
		setVerb(verb);
		setDobj(dobj);
		setFirstWordPosition(firstWord);
		setLastWordPositionShort(lastWordShort);
		this.recognitions = new ArrayList<Pair<String, POS>>();
	}

	// Getters and Setters

	public String getVerb() {
		return verb;
	}

	public void setVerb(String verb) {
		this.verb = verb;
	}

	public String getDobj() {
		return dobj;
	}

	public void setDobj(String dobj) {
		this.dobj = dobj;
	}

	public ArrayList<Pair<String, POS>> getRecognitions() {
		return recognitions;
	}

	public void setRecognitions(ArrayList<Pair<String, POS>> recognitions) {
		this.recognitions = recognitions;
	}

	public Integer getFirstWordPosition() {
		return firstWordPosition;
	}

	public void setFirstWordPosition(Integer firstWordPosition) {
		this.firstWordPosition = firstWordPosition;
	}

	public Integer getLastWordPositionShort() {
		return lastWordPositionShort;
	}

	public void setLastWordPositionShort(Integer lastWordPositionShort) {
		this.lastWordPositionShort = lastWordPositionShort;
	}

	public Integer getLastWordPositionLong() {
		return lastWordPositionLong;
	}

	public void setLastWordPositionLong(Integer lastWordPositionLong) {
		this.lastWordPositionLong = lastWordPositionLong;
	}

	public void setId(String id){
		this.id = id;
	}

	public String getId(){
		return this.id;
	}

	public ConceptualComponent getComponent() {
		return component;
	}

	public void setComponent(ConceptualComponent component) {
		this.component = component;
	}

	public void addPrevResponsibility(Responsibility r){
		this.prevResponsibilities.add(r);
	}

	public void addNextResponsibility(Responsibility r){
		this.nextResponsibilities.add(r);
	}

	public ArrayList<Responsibility> getPrevResponsibilities() {

		return this.prevResponsibilities;
	}

	public ArrayList<Responsibility> getNextResponsibilities() {

		return this.nextResponsibilities;
	}

	public Boolean getNegated() {
		return negated;
	}

	public void setNegated(Boolean negated) {
		this.negated = negated;
	}
	
	public Integer getSentenceID() {
		return sentenceID;
	}

	public void setSentenceID(Integer sentenceID) {
		this.sentenceID = sentenceID;
	}

	public String getVerbToken() {
		return verbToken;
	}

	public void setVerbToken(String verbToken) {
		this.verbToken = verbToken;
	}

	public String getDobjToken() {
		return dobjToken;
	}

	public void setDobjToken(String dobjToken) {
		this.dobjToken = dobjToken;
	}
	
	public String getVerbBaseForm() {
		return verbBaseForm;
	}

	public void setVerbBaseForm(String verbBaseForm, String POS) {
		this.verbBaseForm = getBaseForm(verbBaseForm, POS);;
	}
	
	// Methods

	public Integer getNextResponsibilitiesSize() {
		Integer out = 0;
		
		if (this.getNextResponsibilities().size() > 0) {
			for (Responsibility resp : this.getNextResponsibilities()) {
				out += resp.getNextResponsibilitiesSize() + 1;
			}
		}
		
		return out;
	}
	
	public String getBaseForm(String wordStr, String POSTag) {
		WordTag result = Morphology.stemStatic(wordStr, POSTag);
		if (result != null)
			return result.value();
		else
			return wordStr;
	}
	
	public String getShortForm(){
		return this.getVerb().trim() + " " + this.getDobj().trim();
	}

	public String getLongForm() {
		String completeResponsibility = "";

		for (Pair<String, POS> pair : recognitions){
			completeResponsibility += pair.getPair1() + " ";
		}

		return completeResponsibility;
	}

	@Override
	public String toString() {
		String aux = "[";
		for (Pair<String,POS> recognition : getRecognitions()){
			if (recognition.getPair2() == null){
				aux += recognition.getPair1() + "/NULL";
			}else{
				aux += recognition.getPair1() + "/" +recognition.getPair2().toString();
			}
			aux += ", ";
		}
		if (aux.length() > 1)
			aux = aux.substring(0, aux.length() - 2) + "]";

		String aux2 = "['";
		for (Pair<String,POS> recognition : getRecognitions()){
			if (recognition.getPair2() != null){
				String aux3 = "";
				switch (recognition.getPair2().toString().toLowerCase()){
				case "verb": {
					aux3 = "VB";
					break;
				}
				case "noun": {
					aux3 = "NN";
					break;
				}
				case "adverb": {
					aux3 = "ADV";
					break;
				}
				case "adjective": {
					aux3 = "ADJ";
					break;
				}
				}

				aux2 += recognition.getPair1() + "|" + aux3;
				aux2 += " ";
			}
		}
		if (aux2.length() > 1)
			aux2 = aux2.substring(0, aux2.length() - 1) + "']";

		String negatedTxt = "OK";
		if (this.negated)
			negatedTxt = "NEGATED";
		return "[" + getSimpleTextForm() + ", "+ getShortForm() + ", " + getLongForm() + ", " + getFirstWordPosition() + ", " + getLastWordPositionShort() + ", " + getLastWordPositionLong() + ", " + aux + ", " + aux2 + ", " + negatedTxt + "]";
	}

	public boolean similarTo(Responsibility responsibility) {

		String myVerb = this.getVerbBaseForm();
		String hisVerb = responsibility.getVerbBaseForm();
		String myDobj = this.getDobj();
		String hisDobj = responsibility.getDobj();

		if ( (myVerb.startsWith(hisVerb) || hisVerb.startsWith(myVerb)) && (myDobj.equals(hisDobj)) )
			return true;
		else
			return false;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Responsibility) {
			Responsibility otherResp = (Responsibility) obj;
			Boolean aux1 = this.getVerb().trim().equals(otherResp.getVerb().trim());
			Boolean aux2 = this.getDobj().trim().equals(otherResp.getDobj().trim());
			Boolean aux3 = this.getShortForm().trim().equals(otherResp.getShortForm().trim());
			Boolean aux4 = this.getFirstWordPosition() == otherResp.getFirstWordPosition();
			Boolean aux5 = this.getLastWordPositionShort() == otherResp.getLastWordPositionShort();
			Boolean aux6 = this.getNegated() == otherResp.getNegated();
			return aux1 && aux2 && aux3 && aux4 & aux5 && aux6;
		}
		return false;
	}
	
	public boolean similarCaseOneAndSecond(Responsibility otherResp) {
		
		Boolean aux1 =  this.getVerb().trim().equals(otherResp.getVerb().trim());
		Boolean aux4 = this.getFirstWordPosition() == otherResp.getFirstWordPosition();
		
		return aux1 && aux4;
	}
	
	public String getSimpleTextForm() {
		return this.getVerb() + "_" + this.getFirstWordPosition() + "_"+ this.getDobj() + "_" + this.lastWordPositionShort;
	}

	public Integer getIndex(String token) {
		String[] split = token.split("-");
		return Integer.parseInt(split[split.length - 1]);
	}
	
	@Override
	public int compareTo(Object o) {
		Responsibility anotherResp = (Responsibility) o;
		
		Integer indexVerb = getIndex(this.getVerbToken());
		Integer indexDobj = getIndex(this.getDobjToken());
		
		Integer indexVerbAnother = getIndex(anotherResp.getVerbToken());
		Integer indexDobjAnother = getIndex(anotherResp.getDobjToken());
		
		if (indexVerb.compareTo(indexVerbAnother) != 0) {
			return indexVerb.compareTo(indexVerbAnother);
		}else {
			return indexDobj.compareTo(indexDobjAnother);
		}
	
	}
	
	public String getCompleteResponsibility() {
		String completeResponsibility = "";
		
		for (Pair<String, POS> pair : recognitions){
			completeResponsibility += pair.getPair1() + " ";
		}
			
		return completeResponsibility;
	}
	
	public String getPrettyShortForm() {
		return this.getVerbBaseForm().trim() + " " + this.getDobj().trim();
	}

}
