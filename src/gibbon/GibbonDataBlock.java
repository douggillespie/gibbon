package gibbon;

import PamguardMVC.PamDataBlock;
import PamguardMVC.PamProcess;

public class GibbonDataBlock extends PamDataBlock<GibbonDataUnit> {

	public GibbonDataBlock(GibbonProcess gibbonProcess, int channelMap) {
		super(GibbonDataUnit.class, "Gibbons", gibbonProcess, channelMap);
	}



}
