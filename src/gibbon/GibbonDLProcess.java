package gibbon;

import PamUtils.PamUtils;
import PamguardMVC.PamDataUnit;
import PamguardMVC.PamObservable;
import PamguardMVC.PamProcess;
import gibbon.io.GibbonDatabase;
import gibbon.swing.GibbonOverlayDraw;

public class GibbonDLProcess extends PamProcess {

	private GibbonDataBlock gibbonDataBlock;
	private GibbonControl gibbonControl;
	private int highestChannel;

	public GibbonDLProcess(GibbonControl gibbonControl) {
		super(gibbonControl, null);
		this.gibbonControl = gibbonControl;
		
		gibbonDataBlock = new GibbonDataBlock(this, 1);
		addOutputDataBlock(gibbonDataBlock);
		gibbonDataBlock.SetLogging(new GibbonDatabase(gibbonControl, gibbonDataBlock));
		gibbonDataBlock.setOverlayDraw(new GibbonOverlayDraw(gibbonControl, gibbonDataBlock));

		setParentDataBlock(gibbonControl.getGibbonPreProcess().getModelInputDataBlock());
	}

	@Override
	public void pamStart() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pamStop() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void newData(PamObservable o, PamDataUnit arg) {
		ModelInputDataUnit modelInputDataUnit = (ModelInputDataUnit) arg;
		// now call the model I guess ? 
		runModel(modelInputDataUnit);
	}

	private void runModel(ModelInputDataUnit modelInputDataUnit) {
		/*
		 * Call the model
		 * group /  merge positive seconds. 
		 * Collate results over all channels, or whatever options for collation
		 * has been selected. 
		 */
		
	}

	@Override
	public void setupProcess() {
		super.setupProcess();
		prepareProcessOK();
	}

	@Override
	public boolean prepareProcessOK() {
		GibbonParameters params = gibbonControl.getGibbonParameters();
		// load the model file. Return false if it can't be set up. 

		gibbonDataBlock.setChannelMap(params.channelMap);
		highestChannel = PamUtils.getHighestChannel(params.channelMap);
		return true;
	}

	@Override
	public String getProcessName() {
		return "DL Processing";
	}

}
