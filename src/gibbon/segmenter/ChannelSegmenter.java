package gibbon.segmenter;

import PamController.PamControlledUnit;
import PamDetection.RawDataUnit;
import PamUtils.PamUtils;
import PamguardMVC.PamDataBlock;
import PamguardMVC.PamProcess;

/**
 * Have to recreate a lot of functionality of SegmenterProcess since Jamies one relied on references
 * Do this as a much simpler class that gets raw data and calls back to a user when there is 
 * enough data to do something with. Use callback since we don't know if raw blocks are bigger or
 * smaller than the segmented blocks at this stage. 
 * back to DLControl. 
 */
public class ChannelSegmenter {

	private int channel;
	private int segmentSamples;
	private double[] segData;
	private int currentPos = 0;
	private SegmentObserver segmentObserver;
	private float sampleRate;
	private int segmentHop;

	public ChannelSegmenter(int channel, int segmentSamples, int segmentHop, float sampleRate, SegmentObserver segmentObserver) {
		this.channel = channel;
		this.segmentSamples = segmentSamples;
		this.segmentHop = segmentHop;
		this.sampleRate = sampleRate;
		this.segmentObserver = segmentObserver;
		segData = new double[segmentSamples];
	}

	public void newRawData(RawDataUnit rawDataUnit) {
		int chan = PamUtils.getSingleChannel(rawDataUnit.getChannelBitmap());
		if (chan != channel) {
			return;
		}
		double[] newData = rawDataUnit.getRawData(); 
		int len = newData.length;
		int newDataPos = 0;
		/**
		 * Handle a many to many situation, where it may take multiple raw units to 
		 * fill a segment, or one raw unit may fill multiple segments. 
		 */
		while (newDataPos < len) {
			// how many samples to fill the block. 
			int needed = segmentSamples - currentPos;
			// max number of samples we can safely copy
			int toCopy = Math.min(len-newDataPos, needed);
			// copy those samples. 
			System.arraycopy(newData, newDataPos, segData, currentPos, toCopy);
			// shift up indexes
			currentPos += toCopy;
			newDataPos += toCopy;
			// if we've a complete segment, send it for processing. 
			if (currentPos == segmentSamples) {
				// segment may have started before this data block, 
				long segStartMillis = rawDataUnit.getTimeMilliseconds() - (long) (newDataPos * 1000. / sampleRate);
				long startSample = rawDataUnit.getStartSample() - newDataPos;
				double[] readyData = segData;
				segData = new double[segmentSamples];
				segmentObserver.newSegment(chan, segStartMillis, startSample, sampleRate, readyData);
				// now copy the overlap.
				if (segmentHop < segmentSamples) {
					int overlap = segmentSamples - segmentHop;
					System.arraycopy(readyData, segmentHop, segData, 0, overlap);
					currentPos = overlap;
				}
				else {				
					currentPos = 0;
				}
			}
		}
		// segData will probably hold a bit of remaining data at this point, which will get used on the next call. 
	}
}
