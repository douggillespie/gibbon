package gibbon;

import PamguardMVC.PamDataBlock;
import PamguardMVC.PamProcess;
import gibbon.swing.GibbonSymbolManager;

/**
* Detected gibbon calls. 
 * @author dg50
 *
 */
public class GibbonDataBlock extends PamDataBlock<GibbonDataUnit> {

	public GibbonDataBlock(GibbonCallProcess gibbonCallProcess, int channelMap) {
		super(GibbonDataUnit.class, "Gibbons", gibbonCallProcess, channelMap);
		setPamSymbolManager(new GibbonSymbolManager(this));
	}



}
