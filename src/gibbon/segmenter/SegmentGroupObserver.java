package gibbon.segmenter;

import java.util.ArrayList;

import PamDetection.RawDataUnit;

/**
 * Receive a sorted group of segments. 
 * The sort order is set in the 
 */
public interface SegmentGroupObserver {

	void newDataList(ArrayList<RawDataUnit> newDataUnits);

}
