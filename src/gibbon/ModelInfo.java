package gibbon;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * information about a running model. Will get stored whenever the Gibbon module starts processing. 
 * @author dg50
 *
 */
public class ModelInfo {
	
	private String modelFile;
	
	private long creationDate;
	
	private long modifiedData;
	
	private int size;
	
	private String description;

	public ModelInfo(File modelFile, String description) {
		this.modelFile = modelFile.getName();
		this.modifiedData = modelFile.lastModified();
		try {
			BasicFileAttributes attr = Files.readAttributes(modelFile.toPath(), BasicFileAttributes.class);
			this.creationDate = attr.creationTime().toMillis();
			this.size = (int) attr.size();
		} catch (Exception e) {
			System.out.println("Unable to get model file creation date: " + e.getMessage());
		}
		this.description = description;

	}

	/**
	 * @param modelFile
	 * @param creationDate
	 * @param modifiedData
	 * @param size
	 * @param description
	 */
	public ModelInfo(String modelFile, long creationDate, long modifiedData, int size, String description) {
		super();
		this.modelFile = modelFile;
		this.creationDate = creationDate;
		this.modifiedData = modifiedData;
		this.size = size;
		this.description = description;
	}

	/**
	 * @return the modelFile
	 */
	public String getModelFile() {
		return modelFile;
	}

	/**
	 * @param modelFile the modelFile to set
	 */
	public void setModelFile(String modelFile) {
		this.modelFile = modelFile;
	}

	/**
	 * @return the creationDate
	 */
	public long getCreationDate() {
		return creationDate;
	}

	/**
	 * @param creationDate the creationDate to set
	 */
	public void setCreationDate(long creationDate) {
		this.creationDate = creationDate;
	}

	/**
	 * @return the modifiedData
	 */
	public long getModifiedData() {
		return modifiedData;
	}

	/**
	 * @param modifiedData the modifiedData to set
	 */
	public void setModifiedData(long modifiedData) {
		this.modifiedData = modifiedData;
	}

	/**
	 * @return the size
	 */
	public int getSize() {
		return size;
	}

	/**
	 * @param size the size to set
	 */
	public void setSize(int size) {
		this.size = size;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

}
