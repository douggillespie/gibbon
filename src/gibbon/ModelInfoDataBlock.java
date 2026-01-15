package gibbon;

import PamguardMVC.PamDataBlock;
import PamguardMVC.PamProcess;

public class ModelInfoDataBlock extends PamDataBlock<ModelInfoDataUnit> {

	public ModelInfoDataBlock(GibbonControl gibbonControl, GibbonDLProcess parentProcess) {
		super(ModelInfoDataUnit.class, gibbonControl.getUnitName()+" model", parentProcess, 0);
	}


}
