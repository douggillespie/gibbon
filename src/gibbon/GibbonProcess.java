package gibbon;

import PamDetection.RawDataUnit;
import PamguardMVC.PamDataBlock;
import PamguardMVC.PamDataUnit;
import PamguardMVC.PamObservable;
import PamguardMVC.PamObserver;
import PamguardMVC.PamProcess;
import fftManager.FFTDataBlock;
import fftManager.FFTDataUnit;
import fftManager.PamFFTWorker;
import gibbon.io.GibbonDatabase;
import gibbon.swing.GibbonOverlayDraw;

public class GibbonProcess extends PamProcess {

	private GibbonControl gibbonControl;
	
	private GibbonDataBlock gibbonDataBlock;
	
	private PamFFTWorker fftWorker;
	
	private FFTDataBlock fftDataBlock;

	public GibbonProcess(GibbonControl gibbonControl) {
		super(gibbonControl, null);
		this.gibbonControl = gibbonControl;
		fftDataBlock = new FFTDataBlock(gibbonControl.getUnitName() + " FFT", this,
				1, 512, 256);
		addOutputDataBlock(fftDataBlock);
		gibbonDataBlock = new GibbonDataBlock(this, 1);
		addOutputDataBlock(gibbonDataBlock);
		gibbonDataBlock.SetLogging(new GibbonDatabase(gibbonControl, gibbonDataBlock));
		gibbonDataBlock.setOverlayDraw(new GibbonOverlayDraw(gibbonControl, gibbonDataBlock));
		fftWorker = new PamFFTWorker(fftDataBlock);
		fftDataBlock.addObserver(this);
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
	public void newData(PamObservable o, PamDataUnit arg) {
		if (o == getParentDataBlock()) {
			RawDataUnit rawDataUnit = (RawDataUnit) arg;
			fftWorker.newRawData(rawDataUnit);
		}
		else if (o == fftDataBlock) {
			FFTDataUnit fftDataUnit = (FFTDataUnit) arg;
//			newFFTData(fftDataUnit);
		}
	}

	private void newFFTData(FFTDataUnit fftDataUnit) {
		
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

		fftDataBlock.setChannelMap(input.getChannelMap() & params.channelMap);
		fftDataBlock.setFftLength(params.fftLength);
		fftDataBlock.setFftHop(params.ffthop);
		gibbonDataBlock.setChannelMap(input.getChannelMap() & params.channelMap);
		fftWorker.prepare(input.getSampleRate(), params.channelMap, params.fftLength, params.ffthop);
		
		return true;
	}



}
