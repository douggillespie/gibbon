package gibbon.annotation.swing;

/**
 * Quick comments on gibbons annotations. Probably only going to 
 * be a string, but hold out option for more parameters by wrapping in a class. 
 * @author dg50
 *
 */
public class QuickCommentData {

	private String comment;
	private int nUnits;
	
	public QuickCommentData(String comment, int nUnits) {
		this.comment = comment;
		this.setnUnits(nUnits);
	}

	/**
	 * @return the comment
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * @param comment the comment to set
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}

	/**
	 * @return the nUnits
	 */
	public int getnUnits() {
		return nUnits;
	}

	/**
	 * @param nUnits the nUnits to set
	 */
	public void setnUnits(int nUnits) {
		this.nUnits = nUnits;
	}

}
