package test.tests.loadResults;

import java.util.HashMap;

import utils.io.IO;

public class LoadResults {

	// Variables

	private HashMap<String, ExpectedResult> expectedResults;
	private HashMap<String, ExpectedResultSeq> expectedResultsSeq;

	// Constructors

	public LoadResults() {
		this.expectedResults = new HashMap<String, ExpectedResult>();
		this.expectedResultsSeq = new HashMap<String, ExpectedResultSeq>();
	}

	// Getters and Setters

	public HashMap<String, ExpectedResult> getExpectedResults() {
		return expectedResults;
	}

	public void setExpectedResults(HashMap<String, ExpectedResult> expectedResults) {
		this.expectedResults = expectedResults;
	}

	public HashMap<String, ExpectedResultSeq> getExpectedResultsSeq() {
		return expectedResultsSeq;
	}

	public void setExpectedResultsSeq(HashMap<String, ExpectedResultSeq> expectedResultsSeq) {
		this.expectedResultsSeq = expectedResultsSeq;
	}

	// Methods

	public void load(String filePath) {

		IO io = new IO();
		String txt = io.readFromFile(filePath);
		String[] lines = txt.split("\n");

		String line = "";

		Integer id = null;
		Integer vp = null;
		String resps = null;

		for (int lineID = 0; lineID < lines.length; lineID++) {

			line = lines[lineID];

			if (!line.trim().isEmpty() && line.toLowerCase().trim().startsWith("req") && line.split("=").length > 1) {
				String rightPart = line.split("=")[1];
				if (!rightPart.trim().isEmpty()) {
					id = Integer.parseInt(rightPart);
				}
			}

			if (!line.trim().isEmpty() && line.toLowerCase().trim().startsWith("vp")  && line.split("=").length > 1) {
				String rightPart = line.split("=")[1];
				if (!rightPart.trim().isEmpty()) {
					vp = Integer.parseInt(rightPart);
				}
			}

			if (!line.trim().isEmpty() && line.toLowerCase().trim().startsWith("resp")  && line.split("=").length > 1) {
				String rightPart = line.split("=")[1];
				if (!rightPart.trim().isEmpty()) {
					if (line.split("=").length > 1)
						resps = rightPart;
				}
			}

			if (id != null && vp != null && resps != null) {
				ExpectedResult expResults = new ExpectedResult("REQ-"+id, vp, resps);
				this.expectedResults.put(expResults.getReqID(), expResults);
				id = null;
				vp = null;
				resps = null;
			}

		}	

	}

	public void loadSeq(String filePath) {
		IO io = new IO();
		String txt = io.readFromFile(filePath);
		String[] lines = txt.split("\n");

		String line = "";

		Integer id = null;
		String resps = null;
		String relations = null;

		for (int lineID = 0; lineID < lines.length; lineID++) {

			line = lines[lineID];

			if (!line.trim().isEmpty() && line.toLowerCase().trim().startsWith("req") && line.split("=").length > 1) {
				String rightPart = line.split("=")[1];
				if (!rightPart.trim().isEmpty()) {
					id = Integer.parseInt(rightPart);
				}
			}


			if (!line.trim().isEmpty() && line.toLowerCase().trim().startsWith("resp")  && line.split("=").length > 1) {
				String rightPart = line.split("=")[1];
				if (!rightPart.trim().isEmpty()) {
					if (line.split("=").length > 1)
						resps = rightPart;
				}
			}

			if (!line.trim().isEmpty() && line.toLowerCase().trim().startsWith("seq")  && line.split("=").length >= 1) {
				if (line.split("=").length == 1)
					relations = "";
				else {
					String rightPart = line.split("=")[1];
					relations = rightPart;
				}

			}

			if (id != null && resps != null && relations != null) {
				ExpectedResultSeq expResults = new ExpectedResultSeq("REQ-"+id, resps, relations);
				this.expectedResultsSeq.put(expResults.getReqID(), expResults);
				id = null;
				resps = null;
				relations = null;
			}

		}	



	}

}
