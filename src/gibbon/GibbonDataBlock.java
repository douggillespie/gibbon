package gibbon;

import PamguardMVC.PamDataBlock;
import PamguardMVC.PamProcess;

public class GibbonDataBlock extends PamDataBlock<GibbonDataUnit> {

	public GibbonDataBlock(GibbonDLProcess gibbonDLProcess, int channelMap) {
		super(GibbonDataUnit.class, "Gibbons", gibbonDLProcess, channelMap);
	}



}
