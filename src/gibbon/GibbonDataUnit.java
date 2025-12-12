package gibbon;

import PamDetection.PamDetection;
import PamguardMVC.DataUnitBaseData;
import PamguardMVC.PamDataUnit;

public class GibbonDataUnit extends PamDataUnit implements PamDetection {

	public float bestScore;
	
	public GibbonDataUnit(long timeMilliseconds) {
		super(timeMilliseconds);
		// TODO Auto-generated constructor stub
	}

	public GibbonDataUnit(long timeMilliseconds, int channelBitmap, long startSample, long durationSamples) {
		super(timeMilliseconds, channelBitmap, startSample, durationSamples);
		// TODO Auto-generated constructor stub
	}

	public GibbonDataUnit(DataUnitBaseData basicData) {
		super(basicData);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Put in a potential best score value. The maximum will be kept
	 * @param bestScore
	 */
	public void addBestScore(float bestScore) {
		bestScore = Math.max(this.bestScore, bestScore);
	}
	
	/**
	 * @return the bestScore
	 */
	public float getBestScore() {
		return bestScore;
	}

	/**
	 * @param bestScore the bestScore to set
	 */
	public void setBestScore(float bestScore) {
		this.bestScore = bestScore;
	}

}
