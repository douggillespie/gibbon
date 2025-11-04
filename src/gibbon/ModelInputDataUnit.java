package gibbon;

import PamguardMVC.PamDataUnit;

/**
 * Data unit of a final data matrix to go into the DL model. This is saves as a dataunit
 * part because that's the easiest way to keep required metadata with the actual data matrix
 * but also because this allows us to transfer the data to a separate process thread and 
 * hopefully speed the whole of PAMGuard up by spreading across cores a bit more. 
 * @author dg50
 *
 */
public class ModelInputDataUnit extends PamDataUnit {
	
	protected float[][] modelData;

	public ModelInputDataUnit(long timeMilliseconds, int channelBitmap, long startSample, long durationSamples, float[][] modelData) {
		super(timeMilliseconds, channelBitmap, startSample, durationSamples);
		this.modelData = modelData;
	}


}
