package gibbon;

import java.io.Serializable;

public class GibbonParameters implements Serializable, Cloneable {

	public static final long serialVersionUID = 1L;

	public String rawDataSource;
	
	public int channelMap = 7;
	
	public String modelLocation;
	
	public int fftLength = 512;
	
	public int ffthop = 256;
	
	public float modelSampleRate = 9600;
	
	public double fLow = 1000;
	
	public double fHigh = 2000;
	
	public int nMels = 32;
	
	public int melPower = 1;
	
	/**
	 * Length of frames going into the model. 
	 */
	public int nSliceX = 720;
	
	/**
	 * Advance in time in FFT slices. 
	 */
	public int nHopX = nSliceX / 2;

}
