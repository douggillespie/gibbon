package gibbon;

import gpl.whiten.InfiniteSort;

public class PercentileFilter {

	private int nLines = 1;
	
	private int sortLength = 100;
	
	private InfiniteSort[] infiniteSorts;

	/**
	 * Create a percentile filter bank using the InfiniteSort class. 
	 * @param nLines number of sorts
	 * @param sortLength length of each sort.
	 */
	public PercentileFilter(int nLines, int sortLength) {
		super();
		this.nLines = nLines;
		this.sortLength = sortLength;
		createSorters();
	}
	
	private void reset() {
		createSorters();
	}
	
	private void createSorters() {
		infiniteSorts = new InfiniteSort[nLines];
		for (int i = 0; i < nLines; i++) {
			infiniteSorts[i] = new InfiniteSort(sortLength);
		}
	}
	
	/**
	 * Add data for a single line
	 * @param lineNo between = and nLines -1
	 * @param data data to add.
	 */
	public void addData(int lineNo, double data) {
		infiniteSorts[lineNo].addData(data);
	}
	
	/**
	 * Add an array of data, with one point for each line. 
	 * @param data array length must be equal to nLines;
	 */
	public void addData(double[] data) {
		for (int i = 0; i < nLines; i++) {
			infiniteSorts[i].addData(data[i]);
		}
	}

	public int getnLines() {
		return nLines;
	}

	public void setnLines(int nLines) {
		this.nLines = nLines;
		if (nLines != infiniteSorts.length) {
			infiniteSorts = Arrays.CopyOf(infiniteSorts, nLines);
		}
	}
	
	
}
