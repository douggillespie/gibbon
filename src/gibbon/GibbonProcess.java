package gibbon;

import PamguardMVC.PamProcess;

public class GibbonProcess extends PamProcess {

	private GibbonControl gibbonControl;
	
	private GibbonDataBlock gibbonDataBlock;

	public GibbonProcess(GibbonControl gibbonControl) {
		super(gibbonControl, null);
		this.gibbonControl = gibbonControl;
		gibbonDataBlock = new GibbonDataBlock(this, 1);
		addOutputDataBlock(gibbonDataBlock);
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
	public void setupProcess() {
		super.setupProcess();
		// then find the correct input datablock.
	}


}
