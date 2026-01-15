package gibbon;

import PamguardMVC.PamDataUnit;

public class ModelInfoDataUnit extends PamDataUnit {

	private ModelInfo modelInfo;
	
	public ModelInfoDataUnit(long timeMilliseconds, ModelInfo modelInfo) {
		super(timeMilliseconds);
		this.modelInfo = modelInfo;
	}

	/**
	 * @return the modelInfo
	 */
	public ModelInfo getModelInfo() {
		return modelInfo;
	}


}
