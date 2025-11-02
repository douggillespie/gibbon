package gibbon;

import java.io.Serializable;

public class GibbonParameters implements Serializable, Cloneable {

	public static final long serialVersionUID = 1L;

	public String rawDataSource;
	
	public int channelMap = 7;
	
	public String modelLocation;
	
	public int fftLength = 512;
	
	public int ffthop = 256;
	
	public double fLow = 1000;
	
	public double fHigh = 2000;
	
	public int nMels = 32;

}
