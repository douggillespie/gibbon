package gibbon;

import PamguardMVC.PamDataBlock;
import PamguardMVC.PamProcess;

public class ModelInputDataBlock extends PamDataBlock<ModelInputDataUnit> {

	private GibbonControl gibbonControl;

	public ModelInputDataBlock(GibbonControl gibbonControl, GibbonPreProcess gibbonPreProcess, int channelMap) {
		super(ModelInputDataUnit.class, "Model Input", gibbonPreProcess, channelMap);
		this.gibbonControl = gibbonControl;
	}

	@Override
	public float getSampleRate() {
		return gibbonControl.getGibbonParameters().modelSampleRate;
	}

	@Override
	public void setSampleRate(float sampleRate, boolean notify) {
		super.setSampleRate(getSampleRate(), notify);
	}


}
