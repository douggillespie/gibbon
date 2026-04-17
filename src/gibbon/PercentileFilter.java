package gibbon;

import java.util.Arrays;

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
		if (nLines != this.nLines) {
			infiniteSorts = Arrays.copyOf(infiniteSorts, nLines);
			for (int i = this.nLines; i < nLines; i++) {
				infiniteSorts[i] = new InfiniteSort(sortLength);
			}
		}

		this.nLines = nLines;
	}
	
	/**
	 * Get a percentile value for a given line
	 * @param line line number
	 * @param percentile percentile (0 - 100)
	 * @return closest percentile value
	 */
	public double getPercentile(int line, double percentile) {
		int ind = (int) Math.round(percentile*sortLength/100);
		ind = Math.max(0, Math.min(ind, sortLength-1));
		InfiniteSort is = infiniteSorts[line];
		return is.getData()[is.getSortInd()[ind]];
	}
	
	/**
	 * Get a percentile value for all lines. 
	 * @param percentile percentile (0 - 100)
	 * @return array of closest percentile values. 
	 */
	public double[] getPercentiles(double percentile) {
		int ind = (int) Math.round(percentile*sortLength/100);
		ind = Math.max(0, Math.min(ind, sortLength-1));
		double[] out = new double[nLines];
		for (int i = 0; i < nLines; i++) {
			InfiniteSort is = infiniteSorts[i];
			out[i] = is.getData()[is.getSortInd()[ind]];
		}
		return out;
	}
	
	public double[] getMedians() {
		double[] out = new double[nLines];
		for (int i = 0; i < nLines; i++) {
			InfiniteSort is = infiniteSorts[i];
			out[i] = is.getMedian();
		}
		return out;
	}

	/**
	 * @return the sortLength
	 */
	public int getSortLength() {
		return sortLength;
	}

	/**
	 * Changing this will cause all sorters to reset to zero
	 * @param sortLength the sortLength to set
	 */
	public void setSortLength(int sortLength) {
		if (this.sortLength == sortLength) {
			return;
		}
		this.sortLength = sortLength;
		createSorters();
	}
	
	
}
