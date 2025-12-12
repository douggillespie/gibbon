package gibbon.offline;

import PamController.PamControlledUnit;
import PamguardMVC.PamDataBlock;
import dataMap.OfflineDataMapPoint;
import gibbon.GibbonCallProcess;
import gibbon.GibbonControl;
import gibbon.GibbonDataBlock;
import gibbon.GibbonDataUnit;
import gibbon.GibbonResult;
import gibbon.GibbonResultDataBlock;
import offlineProcessing.OfflineTask;

public class CallOfflineTask extends OfflineTask<GibbonResult>{

	private GibbonControl gibbonControl;
	private GibbonDataBlock gibbonDataBlock;
	private GibbonResultDataBlock resultDataBlock;
	private GibbonCallProcess callDetector;

	public CallOfflineTask(GibbonControl gibbonControl, GibbonResultDataBlock resultDataBlock, GibbonDataBlock gibbonDataBlock) {
		super(gibbonControl, resultDataBlock);
		this.gibbonControl = gibbonControl;
		this.resultDataBlock = resultDataBlock;
		this.gibbonDataBlock = gibbonDataBlock;
		addAffectedDataBlock(gibbonDataBlock);
		callDetector = gibbonControl.getGibbonCallProcess();
	}

	@Override
	public String getName() {
		return "Gibbon Call Detection";
	}

	@Override
	public boolean processDataUnit(GibbonResult dataUnit) {
		
		callDetector.newData(resultDataBlock, dataUnit);
		
		return true;
	}

	@Override
	public void newDataLoad(long startTime, long endTime, OfflineDataMapPoint mapPoint) {
		
	}

	@Override
	public void loadedDataComplete() {
		
	}

	@Override
	public boolean canRun() {
		return true;
	}

	@Override
	public void prepareTask() {
		callDetector.prepareProcessOK();
	}



}
