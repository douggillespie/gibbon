package gibbon.annotation;

import gibbon.GibbonDataUnit;

/**
 * information on an existing, clicked on, data unit 
 * found at the start of a mark event. 
 * @author dg50
 *
 */
public class MatchedGibbon {

	public static final int TOPBORDER = 0x1;
	public static final int BOTTOMBORDER = 0x2;
	public static final int LEFTBORDER = 0x4;
	public static final int RIGHTBORDER = 0x8;
	
	private GibbonDataUnit gibbonDataUnit;
	private int edges;
	/**
	 * @param gibbonDataUnit
	 * @param edges
	 */
	public MatchedGibbon(GibbonDataUnit gibbonDataUnit, int edges) {
		super();
		this.gibbonDataUnit = gibbonDataUnit;
		this.edges = edges;
	}
	/**
	 * @return the gibbonDataUnit
	 */
	public GibbonDataUnit getGibbonDataUnit() {
		return gibbonDataUnit;
	}
	/**
	 * @return the edges
	 */
	public int getEdges() {
		return edges;
	}
	
}
