package model.exporters;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

import model.ucm.UseCaseMap;

public abstract class XMLExporter implements UseCaseMapExporter {

	// Variables

	// Constructors

	// Getters and Setters

	// Methods

	protected abstract Document getDocument(UseCaseMap useCaseMap);

	@Override
	public File export(UseCaseMap useCaseMap, String fileName){

		try {

			TransformerFactory tFactory = TransformerFactory.newInstance();
			Transformer transformer = tFactory.newTransformer();
			DOMSource source = new DOMSource(getDocument(useCaseMap));
			File outputFile = new File(fileName);
			FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
			StreamResult result = new StreamResult(fileOutputStream);
			transformer.transform(source, result);

			return outputFile;

		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}



}
