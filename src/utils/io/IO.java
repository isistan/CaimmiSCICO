package utils.io;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class IO {
	
	// Variables
	private String charset;
	
	// Constructors
	
	public IO(){
		this("UTF-8");
	}
	
	public IO(String charset){
		this.charset = charset;
	}
	
	// Getters and Setters
	
	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}
	
	// Methods
	
	public void writeToFile(String path, String toWrite){
		PrintWriter writer;
		try {
			writer = new PrintWriter(path, this.charset);
			writer.println(toWrite);
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	public String readFromFile(String path){
		String out = "";
		Path pathToRead = Paths.get(path);
		BufferedReader reader = null;

		try {
			reader = Files.newBufferedReader(pathToRead, Charset.forName(this.charset));
			String line = null;
			while ((line = reader.readLine()) != null) {
				out += line + "\n";
			}
		} catch (IOException e){
			e.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return out;
	}
}
