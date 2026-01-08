package gibbon;


import PamDetection.RawDataUnit;
import PamUtils.PamUtils;
import PamguardMVC.PamConstants;
import PamguardMVC.PamDataBlock;
import PamguardMVC.PamDataUnit;
import PamguardMVC.PamObservable;
import PamguardMVC.PamProcess;
import decimator.DecimatorParams;
import decimator.DecimatorWorker;
import fftManager.FFTDataBlock;
import fftManager.FFTDataUnit;
import fftManager.PamFFTWorker;
import mel.MelConverter;

/**
 * Pre processing of data for the Gibbon DL detector. This comprises several signal 
 * processing stages, followed by conversion of the data into a simple rectantular array
 * which wil eventually get sent to the DL model. <p>The DL model itself is called in a separate
 * process a) for speed and b) to simplify this class a bit.
 * <p>The signal processing is a bit fluid at the moment, but it may contain any or all of the 
 * following:
 * <br>Decimation
 * <br>FFT
 * <br>Mel Spectrogram conversion
 * <br>Any background subtraction methods we can think of 
 * @author dg50
 *
 */
public class GibbonPreProcess extends PamProcess {

	private GibbonControl gibbonControl;

	private ModelInputDataBlock modelInputDataBlock;

	private PamFFTWorker fftWorker;

	private FFTDataBlock fftDataBlock;

	private Object blockSynch = new Object();

	private SpectrogramBlock[] spectrogramBlocks;

	private int highestChan;

	private MelConverter melConverter;

	private DecimatorWorker decimatorWorker;

	private double[][] channelBackgrounds = new double[PamConstants.MAX_CHANNELS][];;

	public GibbonPreProcess(GibbonControl gibbonControl) {
		super(gibbonControl, null);
		this.gibbonControl = gibbonControl;
		fftDataBlock = new IntlFFTBlock(gibbonControl.getUnitName() + " FFT", this,
				1, 512, 256);
		addOutputDataBlock(fftDataBlock);
		modelInputDataBlock = new ModelInputDataBlock(gibbonControl, this, 1);
		// don't add this, nothing really wants to find it. Why not - makes the model view clearer
		addOutputDataBlock(modelInputDataBlock);

		fftWorker = new PamFFTWorker(fftDataBlock);
	}

	@Override
	public void pamStart() {

	}

	@Override
	public void pamStop() {

	}

	@Override
	public void setupProcess() {
		prepareProcessOK();
	}


	@Override
	public boolean prepareProcessOK() {
		super.prepareProcessOK();

		super.setupProcess();
		// then find the correct input datablock.
		GibbonParameters params = gibbonControl.getGibbonParameters();
		PamDataBlock input = gibbonControl.getPamConfiguration().getDataBlockByLongName(params.rawDataSource);
		if (input == null) {
			return false;
		}
		setParentDataBlock(input);

		highestChan = PamUtils.getHighestChannel(params.channelMap);

		/**
		 * Sort out the decimator if necessary
		 */
		if (params.modelSampleRate != getSampleRate()) {
			DecimatorParams decimatorParams = new DecimatorParams(params.modelSampleRate, 4);
			decimatorWorker = new DecimatorWorker(decimatorParams, params.channelMap, getSampleRate(), params.modelSampleRate);
		}
		else {
			decimatorWorker = null;
		}

		fftDataBlock.setChannelMap(input.getChannelMap() & params.channelMap);
		fftDataBlock.setFftLength(params.fftLength);
		fftDataBlock.setFftHop(params.ffthop);
		setSampleRate(input.getSampleRate(), true);
		modelInputDataBlock.setChannelMap(input.getChannelMap() & params.channelMap);
		fftWorker.prepare(params.modelSampleRate, params.channelMap, params.fftLength, params.ffthop);

		/*
		 * Prepare the mel scectrogram conversion. 
		 */
		melConverter = new MelConverter(params.modelSampleRate, params.fLow, params.fHigh, params.fftLength, params.nMels, params.melPower);


		for (int i = 0; i <= highestChan; i++) {
			if (channelBackgrounds[i] == null || channelBackgrounds[i].length != params.nMels) {
				channelBackgrounds[i] = new double[params.nMels];
			}
		}

		/*
		 * Prepare the Spectrogram Blocks which will hold data until it's got enough 
		 * to make an image to go off to the DL model. 
		 */
		synchronized (blockSynch) {
			spectrogramBlocks  = new SpectrogramBlock[highestChan+1];
			for (int i = 0; i <= highestChan; i++) {
				if ((params.channelMap & 1<<i) == 0) {
					continue;
				}
				spectrogramBlocks[i] = new SpectrogramBlock(i, params.nSliceX);
			}
		}

		return true;
	}

	@Override
	public void newData(PamObservable o, PamDataUnit arg) {
//		if (gibbonControl.isViewer()) {
//			return;
//		}
		if (o == getParentDataBlock()) {
			RawDataUnit rawDataUnit = (RawDataUnit) arg;
			if (decimatorWorker != null) { 
				rawDataUnit = decimatorWorker.process(rawDataUnit);
			}
			if (rawDataUnit != null) {
				// check not null, since Decimator may take time to start returning data. 
				// this will call back into newFFTData
				fftWorker.newRawData(rawDataUnit);
			}
		}
	}

	/**
	 * New FFT data have arrived. Collate into blocks, then process those blocks. 
	 * @param fftDataUnit
	 */
	private void newFFTData(FFTDataUnit fftDataUnit) {
		int singleChan = PamUtils.getSingleChannel(fftDataUnit.getChannelBitmap());
		GibbonParameters params = gibbonControl.getGibbonParameters();
		if ((fftDataUnit.getChannelBitmap() & params.channelMap) == 0) {
			return;
		}
		spectrogramBlocks[singleChan].addData(fftDataUnit);
		/*
		 * See if all blocks should be full and send them off for 
		 * the next stage of processing. 
		 */
		if (singleChan == highestChan && spectrogramBlocks[highestChan].dataPos == params.nSliceX) {
			// and shuffle the data
			for (int i = 0; i <= highestChan; i++) {
				if ((1<<i & params.channelMap) !=0 & spectrogramBlocks[i] != null) {
					prepModelData(spectrogramBlocks[i]);
					spectrogramBlocks[i].shuffle(params.nHopX);
				}
			}
		}
	}

	/**
	 * Called when every channel we're working with has a complete block
	 * of FFT data. There may still be a lot to do here, for instance 
	 * converting to mels, background subtraction, etc. Will do all that preprocessing
	 * here, then make up a data unit and send if off to another process for the actual 
	 * model calling, so that it runs in a separate thread. This will split processing between
	 * this process - that handles all the data prep, and a separate process thread that will
	 * call the DL. Should be faster overall than doing in a single process. 
	 * @param spectrogramBlocks2 
	 */
	private void prepModelData(SpectrogramBlock spectrogramBlock) {
		int chan = spectrogramBlock.channelIndex;
		GibbonParameters params = gibbonControl.getGibbonParameters();
		float[][] data = new float[params.nMels][params.nSliceX]; // is this the wrong way around ? 
		float minVal = Float.MAX_VALUE;
		float maxVal = 0;
		double[] chBgnd = channelBackgrounds[chan];
		for (int i = 0; i < params.nSliceX; i++) {
			FFTDataUnit fft = spectrogramBlock.dataList[i];
			double[] mels = melConverter.melFromComplex(fft.getFftData());
			for (int j = 0; j < params.nMels; j++) {
				data[j][i] = (float) mels[j];
				minVal = Math.min(minVal, data[j][i]);
				maxVal = Math.max(maxVal, data[j][i]);
			}
		}
		/**
		 * If doing amplitudetodb, note that the default has a minimum of 1e-5 on the input and and 80dB on the output
		 */
		for (int i = 0; i < params.nSliceX; i++) {
			for (int j = 0; j < params.nMels; j++) {
				data[j][i] = (float) Math.min(10*Math.log10(Math.max(1e-5, data[j][i])), 80);
			}
		}
		
		/** 
		 * Get the background mean
		 * 
		 */
		for (int i = 0; i < params.nSliceX; i++) {
			for (int j = 0; j < params.nMels; j++) {
				chBgnd[j] += (data[j][i]-chBgnd[j]) / 1000;				
			}
		}

	/*
	 * If we're to normalise the data, run this code block ...
	 */
	//		for (int i = 0; i < params.nSliceX; i++) {
	//			for (int j = 0; j < params.nMels; j++) {
	//				data[j][i] = (data[j][i] - minVal) /(maxVal-minVal);
	//			}
	//		}
	/**
	 * To subtract off the mean, run this (which isn't the same as the Python code since that
	 * was doing a mean for an entire file, whereas we're really reliant on doing an evolving mean here. 
	 * need to loop through the i times and j nMels
	 */
	for (int i = 0; i < params.nSliceX; i++) {
		for (int j = 0; j < params.nMels; j++) {
			data[j][i] -= chBgnd[j];
			data[j][i] = Math.max(data[j][i], 0);
		}
	}




	FFTDataUnit firstFFT = spectrogramBlock.dataList[0];
	long t = firstFFT.getTimeMilliseconds();
	long s1 = firstFFT.getStartSample();
	long s2 = spectrogramBlock.dataList[params.nSliceX-1].getStartSample() + params.fftLength;
	ModelInputDataUnit midu = new ModelInputDataUnit(t, firstFFT.getChannelBitmap(), s1, s2-s1, data);
	modelInputDataBlock.addPamData(midu);
}

/**
 * @return the modelInputDataBlock
 */
public ModelInputDataBlock getModelInputDataBlock() {
	return modelInputDataBlock;
}

/**
 * Simple extend of FFT Datablock so we can get a call whenever
 * the FFT worker generates new data without having to make a whole new
 * observer of the data. 
 * @author dg50
 *
 */
private class IntlFFTBlock extends FFTDataBlock {

	public IntlFFTBlock(String dataName, PamProcess parentProcess, int channelMap, int fftHop, int fftLength) {
		super(dataName, parentProcess, channelMap, fftHop, fftLength);
	}

	@Override
	public void addPamData(FFTDataUnit fftDataUnit) {
		super.addPamData(fftDataUnit);
		newFFTData(fftDataUnit);
	}

	@Override
	public float getSampleRate() {
		return gibbonControl.getGibbonParameters().modelSampleRate;
	}

}

/**
 * Make blocks of raw spectrogram data. Do this before converting to Mels to 
 * make it easier to NOT covert to Mels if we don't want to. These will have to handle
 * shuffling data along to manage overlaps between segments going into the DL model just
 * as the FFT module shuffles along the raw audio data. 
 * @author dg50
 *
 */
private class SpectrogramBlock implements Cloneable {
	private int channelIndex;
	/*
	 * fixed length, so no need for an array list, etc. 
	 */
	private FFTDataUnit[] dataList;
	private int blockLength;
	private int dataPos;

	public SpectrogramBlock(int channelIndex, int blockLength) {
		super();
		this.channelIndex = channelIndex;
		this.blockLength = blockLength;
		dataList = new FFTDataUnit[blockLength];
	}

	public void addData(FFTDataUnit fftDataUnit) {
		dataList[dataPos++] = fftDataUnit;
	}

	public void clear() {
		dataPos = 0;
	}

	/**
	 * Shuffle left by step FFT's. 
	 * @param step
	 */
	public void shuffle(int step) {
		System.arraycopy(dataList, 0, dataList, blockLength-step, step);
		dataPos -= step;
		dataPos = Math.max(dataPos, 0);
	}

	@Override
	protected SpectrogramBlock clone() {
		try {
			return (SpectrogramBlock) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}
}

@Override
public String getProcessName() {
	return "Pre processing";
}

@Override
public void setSampleRate(float sampleRate, boolean notify) {
	/**
	 * Had to override this since some of the output data of this may 
	 * have a different sample rate. 
	 */
	this.sampleRate = sampleRate;
	GibbonParameters params = gibbonControl.getGibbonParameters();
	fftDataBlock.setSampleRate(params.modelSampleRate, notify);
	modelInputDataBlock.setSampleRate(params.modelSampleRate, notify);
}


}
