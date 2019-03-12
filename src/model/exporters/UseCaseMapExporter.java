package model.exporters;

import java.io.File;
import model.ucm.UseCaseMap;

public interface UseCaseMapExporter {
	
	// Methods
	
	public File export(UseCaseMap useCaseMap, String fileName);
	
}
