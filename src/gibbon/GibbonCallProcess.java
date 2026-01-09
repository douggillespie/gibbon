package gibbon;

import java.util.Arrays;

import PamUtils.PamUtils;
import PamguardMVC.PamDataUnit;
import PamguardMVC.PamObservable;
import PamguardMVC.PamProcess;
import gibbon.io.GibbonDatabase;
import gibbon.swing.GibbonOverlayDraw;

/**
 * Process to take the model results from the GibbonDLProcess and turn them into calls with a start and an end. 
 * @author dg50
 *
 */
public class GibbonCallProcess extends PamProcess {

	private GibbonControl gibbonControl;
	private GibbonDataBlock gibbonDataBlock;
	private GibbonResultDataBlock resultDataBlock;
	private int highestChannel, lowestChannel;
	/**
	 * Highest result across all channels. 
	 */
	private float[] bestResult;
	private long[] resultTimes;
	private float[] meanLevels;
	private int resultChannels;
	private int resultState;
	private int minLegth = 2;
	private int downCount;
	private int upCount;
	private GibbonDataUnit currentCall;
	private GibbonOverlayDraw gibbonOverlayDraw;
	private GibbonDLProcess gibbonDLProcess;
	private float latestLevel;
	private float signalLevel;
	private int nLevel;

	public GibbonCallProcess(GibbonControl gibbonControl) {
		super(gibbonControl, null, "Call Detection");
		this.gibbonControl = gibbonControl;

		gibbonDLProcess = gibbonControl.getGibbonDLProcess();
		gibbonDataBlock = new GibbonDataBlock(this, 1);
		addOutputDataBlock(gibbonDataBlock);
		gibbonDataBlock.SetLogging(new GibbonDatabase(gibbonControl, gibbonDataBlock));
		gibbonDataBlock.setOverlayDraw(gibbonOverlayDraw = new GibbonOverlayDraw(gibbonControl, gibbonDataBlock));
	}

	@Override
	public void pamStart() {
		resultState = 0;
		downCount = 0;
		upCount = 0;
		currentCall = null;
	}

	@Override
	public void pamStop() {
		
	}
	
	@Override
	public boolean prepareProcessOK() {
		GibbonParameters params = gibbonControl.getGibbonParameters();
		// load the model file. Return false if it can't be set up. 

		resultDataBlock = gibbonControl.getGibbonDLProcess().getResultDataBlock();
		setParentDataBlock(resultDataBlock);
		gibbonDataBlock.setChannelMap(params.channelMap);
		lowestChannel = PamUtils.getLowestChannel(params.channelMap);
		highestChannel = PamUtils.getHighestChannel(params.channelMap);
		currentCall = null;
		
		return true;
	}

	@Override
	public void newData(PamObservable o, PamDataUnit arg) {
		super.newData(o, arg);
		GibbonResult resultData = (GibbonResult) arg;
		int chan = PamUtils.getSingleChannel(resultData.getChannelBitmap());
		float[] results = resultData.getResult();
		long[] times = resultData.getResultTimes();
		float[] levels = resultData.getLevels();
		if (chan == lowestChannel) {
			bestResult = Arrays.copyOf(results, results.length);
			resultTimes = Arrays.copyOf(times, times.length);
			resultChannels = resultData.getChannelBitmap();
			meanLevels = Arrays.copyOf(levels, levels.length);
		}
		else {
			for (int i = 0; i < results.length; i++) {
				bestResult[i] = Math.max(bestResult[i], results[i]);
			}
			if (levels != null) {
				for (int i = 0; i < levels.length; i++) {
					meanLevels[i] += levels[i];
				}
			}
			resultChannels |= resultData.getChannelBitmap();
		}
		if (chan == highestChannel) {
			if (meanLevels != null) {
				int nChan = Integer.bitCount(resultChannels);
				for (int i = 0; i < levels.length; i++) {
					meanLevels[i] /= nChan;
				}
			}
			
			findCalls(resultTimes, bestResult, resultChannels, meanLevels);
		}
		
	}

	private void findCalls(long[] resultTimes, float[] bestResult, int channelMap, float[] meanLevels) {
		GibbonParameters params = gibbonControl.getGibbonParameters();
		if (meanLevels != null && meanLevels.length != bestResult.length) {
			meanLevels = null; // should never happen !
		}
		if (meanLevels == null) {
			meanLevels = new float[resultTimes.length];
		}
		for (int i = 0; i < resultTimes.length; i++) {
			if (currentCall == null) {
				/*
				 * See if we should start a call. 
				 */
				if (bestResult[i] >= params.threshold ) {
					// start a call
					currentCall = new GibbonDataUnit(resultTimes[i], channelMap, 0, 0);
					currentCall.setDurationInMilliseconds(1000);
					currentCall.setBestScore(bestResult[i]);
					upCount = 1;
					downCount = 0;
					signalLevel = meanLevels[i];
					nLevel = 1;
				}
				else {
					// simple decaying running mean of background. 
					latestLevel += ((meanLevels[i]-latestLevel) / 5);
				}
			}
			else {
				/* 
				 * A call is developing, so see if we should end it. 
				 */
				if (bestResult[i] >= params.threshold) {
					upCount++;
					downCount = 0;
					currentCall.setDurationInMilliseconds(resultTimes[i]-currentCall.getTimeMilliseconds() + 1000);
					currentCall.addBestScore(bestResult[i]);
					signalLevel += meanLevels[i];
					nLevel++;
				}
				else {
					downCount++;
				}
				if (downCount > params.maxGap) {
					// end the call
					double[] f = {params.fLow, params.fHigh};
					currentCall.setFrequency(f);
					currentCall.copyAutoInfo();
					currentCall.setCallType("Female");
					currentCall.setModel(gibbonDLProcess.getModelName());
					if (latestLevel != 0) {
						// latestLevel may be 0 in some very old data, or if the input was somehow not working, but
						// generally this should always be true. 
						double snr = 10*Math.log10(signalLevel/nLevel/latestLevel);
						currentCall.setSNR(snr);
					}
					gibbonDataBlock.addPamData(currentCall);
					currentCall = null;
					upCount = downCount = 0;
				}
			}
		}
	}

	/**
	 * @return the gibbonDataBlock
	 */
	public GibbonDataBlock getGibbonDataBlock() {
		return gibbonDataBlock;
	}

	/**
	 * @return the gibbonOverlayDraw
	 */
	public GibbonOverlayDraw getGibbonOverlayDraw() {
		return gibbonOverlayDraw;
	}

	@Override
	public void setupProcess() {
		super.setupProcess();
		prepareProcessOK();
	}

}
