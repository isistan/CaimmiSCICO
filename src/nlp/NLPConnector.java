package nlp;

import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.AnnotationPipeline;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.pipeline.StanfordCoreNLPClient;
import edu.stanford.nlp.util.CoreMap;

public class NLPConnector {

	// Variables

	private AnnotationPipeline pipeline;
	private Annotation document;
	
	// Constructors

	public NLPConnector(Properties properties, String url, Integer port, Integer nThreads){
		pipeline = new StanfordCoreNLPClient(properties, url, port, nThreads);
		this.document = null;
	}

	public NLPConnector(Properties properties){
		pipeline = new StanfordCoreNLP(properties);
		this.document = null;
	}

	// Getters and Setters

	public Annotation getDocument(){
		return this.document;
	}
	
	// Methods

	public List<CoreMap> annotate(String text){
		Annotation document = new Annotation(text);
		pipeline.annotate(document);

		this.document = document;
		return document.get(SentencesAnnotation.class);
	}

	public List<CoreMap> annotateQuotes(String text){
		Annotation annotation = new Annotation(text);
		pipeline.annotate(annotation);
		
		this.document = annotation;
		return annotation.get(CoreAnnotations.QuotationsAnnotation.class);
	}

}
