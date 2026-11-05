package gibbon.segmenter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import PamDetection.RawDataUnit;
import PamUtils.PamUtils;

/**
 * Sun segmentation over multiple channel groups. 
 */
public class ChannelGroupSegmenter implements SegmentObserver {
	
	private int channelMap;
	private int segmentSamples;
	private float sampleRate;
	private ChannelSegmenter[] channelSegmenters;
	private ArrayList<RawDataUnit> newDataUnits = new ArrayList<RawDataUnit>();
	private int maxChan;
	private SegmentGroupObserver segmentGroupObserver;

	public ChannelGroupSegmenter(int channelMap, int segmentSamples, int segmentHop, float sampleRate, SegmentGroupObserver segmentGroupObserver) {
		this.channelMap = channelMap;
		this.segmentSamples = segmentSamples;
		this.sampleRate = sampleRate;
		this.segmentGroupObserver = segmentGroupObserver;
		// create channel segmenters
		maxChan = PamUtils.getHighestChannel(channelMap);
		channelSegmenters = new ChannelSegmenter[maxChan+1];
		for (int i = 0; i <= maxChan; i++) {
			if ((1<<i & channelMap) != 0) {
				channelSegmenters[i] = new ChannelSegmenter(i, segmentSamples, segmentHop, sampleRate, this);
			}
		}
	}

	public void newRawData(RawDataUnit rawDataUnit) {
		if ((rawDataUnit.getChannelBitmap() & channelMap) == 0) {
			return;
		}
		int chan = PamUtils.getSingleChannel(rawDataUnit.getChannelBitmap());
		channelSegmenters[chan].newRawData(rawDataUnit);
		if (chan == maxChan && newDataUnits.size() > 0) {
			/*
			 *  we've a complete set of data across all channels. however the data may no longer be
			 *  interleaved the way we want it if there were multiple outputs per input block (i.e. if
			 *  the desired segment length was shorter than the input data lengths.  
			 */
			newDataUnits.sort(new InterleavedSort());
			segmentGroupObserver.newDataList(newDataUnits);
			newDataUnits.clear();
		}
	}

	@Override
	public void newSegment(int channel, long dataStartMillis, long dataStartSample, float sampleRate, double[] data) {
		RawDataUnit rdu = new RawDataUnit(dataStartMillis, 1<<channel, dataStartSample, data.length);
		// copy the array since it's about to get overwritten. 
		rdu.setRawData(data, true);
		newDataUnits.add(rdu);
	}
	
	/**
	 * Comparator that will sort an array of raw data units by time, then by 
	 * channel. Can be used to correctly interleave data 
	 */
	private class InterleavedSort implements Comparator<RawDataUnit> {

		@Override
		public int compare(RawDataUnit o1, RawDataUnit o2) {
			/*
			 *  first thing to sort on is time. Very small risk of samme millisecond time for 
			 *  very short segments of very high freuqency data, so deal with it. 
			 */
			if (o2.getTimeMilliseconds() != o1.getTimeMilliseconds()) {
				return (int) (o2.getTimeMilliseconds() - o1.getTimeMilliseconds());
			}
			if (o2.getStartSample() != o1.getStartSample()) {
				return (int) (o2.getStartSample() - o1.getStartSample());
			}
			// then sort on channel. 
			return o2.getChannelBitmap() - o1.getChannelBitmap();
		}
		
	}

}
