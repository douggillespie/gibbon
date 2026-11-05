package gibbon.segmenter;

/**
 * Interface to receive data from a ChannelSegmenter. 
 */
public interface SegmentObserver {

	/**
	 * Receive segment data. 
	 * @param channel channel number (number, not bitmap!)
	 * @param dataStart data start time in milliseconds 
	 * @param sampleRate sample rate
	 * @param data complete data array. 
	 */
	public void newSegment(int channel, long dataStartMillis, long dataStartSample, float sampleRate, double[] data);
	
}
