package gibbon;

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
public class GibbonSegmenter {

	private int channelMap;
	private int segmentSamples;

	public GibbonSegmenter(int channelMap, int segmentSamples) {
		this.channelMap = channelMap;
		this.segmentSamples = segmentSamples;
	}

	public void newRawData(RawDataUnit rawDataUnit) {
		if ((rawDataUnit.getChannelBitmap() & channelMap) == 0) {
			return;
		}
		int chan = PamUtils.getSingleChannel(rawDataUnit.getChannelBitmap());
		newRawData(chan, rawDataUnit.getRawData());
	}
	
	public void newRawData(int singleChannel, double[] rawData) {
		
	}
}
