package gibbon.offline;

import PamUtils.PamUtils;
import PamguardMVC.PamDataUnit;
import dataGram.DatagramProvider;
import dataGram.DatagramScaleInformation;
import gibbon.GibbonControl;
import gibbon.GibbonResult;
import gibbon.GibbonResultDataBlock;

public class GibbonResultDatagram implements DatagramProvider {

	private GibbonControl gibbonControl;
	private GibbonResultDataBlock resultDataBlock;
	private DatagramScaleInformation scaleInfo;

	public GibbonResultDatagram(GibbonControl gibbonControl, GibbonResultDataBlock resultDataBlock) {
		this.gibbonControl = gibbonControl;
		this.resultDataBlock = resultDataBlock;
		scaleInfo = new DatagramScaleInformation(-10, 10, "Score", false, DatagramScaleInformation.PLOT_2D);
	}

	@Override
	public int getNumDataGramPoints() {
//		int nChan = PamUtils.getNumChannels(resultDataBlock.getChannelMap());
//		return Math.max(nChan, 1);
		return 1;
	}

	@Override
	public int addDatagramData(PamDataUnit dataUnit, float[] dataGramLine) {
		GibbonResult result = (GibbonResult) dataUnit;
		float[] data = result.getResult();
		if (data == null || data.length == 0) {
			return 0;
		}
		float max = data[0];
		for (int i = 1; i < data.length; i++) {
			max = Math.max(max, data[i]);
		}
		dataGramLine[0] = Math.max(max, dataGramLine[0]);
		return 1;
	}

	@Override
	public DatagramScaleInformation getScaleInformation() {
		return scaleInfo;
	}

}
