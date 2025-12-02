package gibbon;

import PamguardMVC.PamDataBlock;
import PamguardMVC.PamProcess;

/**
 * Data block for gibbon results. These are the model outputs, NOT acutal 
 * detections. Each unit will hold an array of results from one call to 
 * the RCNN model, typically around 20 (for 20s of data)
 * @author dg50
 *
 */
public class GibbonResultDataBlock extends PamDataBlock<GibbonResult> {

	private GibbonDLProcess gibbonDLProcess;
	
	public GibbonResultDataBlock(GibbonDLProcess gibbonDLProcess) {
		super(GibbonResult.class, "Gibbon Results", gibbonDLProcess, 1);
		this.gibbonDLProcess = gibbonDLProcess;
	}


}
