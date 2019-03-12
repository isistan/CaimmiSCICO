package model.responsibility;

public class SimpleResponsibility {

	// Variables
	
	private String actor;
	private String verb;
	private String dobj;
	private String verbInLemma;
	private String verbInInfinitive;
	private String dobjInLemma;
	private String dobjInInfinitive;
	
	
	// Constructors
	
	public SimpleResponsibility(String actor, String verb, String dobj) {
		this(actor, verb, dobj, null, null);
	}
	
	public SimpleResponsibility(String actor, String verb, String dobj, String verbInLemma, String dobjInLemma) {
		this(actor, verb, dobj, verbInLemma, dobjInLemma, null, null);
	}
	
	public SimpleResponsibility(String actor, String verb, String dobj, String verbInLemma, String dobjInLemma, String verbInInfinitive, String dobjInInfinitive) {
		super();
		this.actor = actor;
		this.verb = verb;
		this.dobj = dobj;
		this.verbInLemma = verbInLemma;
		this.dobjInLemma = dobjInLemma;
		this.verbInInfinitive = verbInInfinitive;
		this.dobjInInfinitive = dobjInInfinitive;
	}
	
	// Getters and Setters
	
	public String getActor() {
		return actor;
	}
	
	public void setActor(String actor) {
		this.actor = actor;
	}
	
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
	
	public String getVerbInLemma() {
		return verbInLemma;
	}

	public void setVerbInLemma(String verbInLemma) {
		this.verbInLemma = verbInLemma;
	}

	public String getVerbInInfinitive() {
		return verbInInfinitive;
	}

	public void setVerbInInfinitive(String verbInInfinitive) {
		this.verbInInfinitive = verbInInfinitive;
	}

	public String getDobjInLemma() {
		return dobjInLemma;
	}

	public void setDobjInLemma(String dobjInLemma) {
		this.dobjInLemma = dobjInLemma;
	}

	public String getDobjInInfinitive() {
		return dobjInInfinitive;
	}

	public void setDobjInInfinitive(String dobjInInfinitive) {
		this.dobjInInfinitive = dobjInInfinitive;
	}	
	
	// Methods
	
	@Override
	public String toString() {
		return "[ ACTOR = " + this.getActor() + " | VERB = " + this.getVerb() + " | DOBJ = " + this.getDobj() + "]";
	}
	
	
	public String getPhraseVerbDobj(){
		return this.getVerb().trim() + " " + this.getDobj().trim();
	}
	
}
