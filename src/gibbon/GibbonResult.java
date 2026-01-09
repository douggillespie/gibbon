package gibbon;

import PamguardMVC.DataUnitBaseData;
import PamguardMVC.PamDataUnit;
import binaryFileStorage.BinaryHeader;

/**
 * Data unit for Gibbon model results. Note that these are not Detections, 
 * but all the output from calls to the model. Each unit will hold an array of results from one call to 
 * the RCNN model, typically around 20 (for 20s of data)
 * @author dg50
 *
 */
public class GibbonResult extends PamDataUnit<PamDataUnit, PamDataUnit> {

	private float[] result;
	
	private long[] resultTimes;
	
	/*
	 *  the signal levels in the data for each result. These are the rms summed
	 *  signal level for each segment and can be used to estimate SNR of calls
	 *  during the detection stage  
	 */
	private float[] levels;

	public GibbonResult(long timeMilliseconds, long durationMillis, int channelMap, float[] result) {
		super(timeMilliseconds);
		setChannelBitmap(channelMap);
		this.result = result;
		this.setDurationInMilliseconds(durationMillis);
	}


	/**
	 * @param basicData
	 */
	public GibbonResult(DataUnitBaseData basicData, float[] result) {
		super(basicData);
		this.result = result;
	}

	/**
	 * @return the result
	 */
	public float[] getResult() {
		return result;
	}
	
	/**
	 * Calculate the times of the individual results. 
	 * @return time of each individual result. 
	 */
	public long[] calcResultTimes() {
		if (result == null) {
			return null;
		}
		long[] t = new long[result.length];
		t[0] = getTimeMilliseconds();
		double step = getDurationInMilliseconds() / result.length;
		for (int i = 1; i < result.length; i++) {
			long tt = (long) (i*step);
			t[i] = getTimeMilliseconds() + tt;
		}
			
		return t;
	}

	/**
	 * @return the resultTimes
	 */
	public long[] getResultTimes() {
		if (resultTimes == null) {
			resultTimes = calcResultTimes();
		}
		return resultTimes;
	}


	/**
	 * @return the levels
	 */
	public float[] getLevels() {
		return levels;
	}


	/**
	 * @param levels the levels to set
	 */
	public void setLevels(float[] levels) {
		this.levels = levels;
	}

	
}
