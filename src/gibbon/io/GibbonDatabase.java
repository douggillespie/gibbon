package gibbon.io;

import PamDetection.AcousticSQLLogging;
import PamguardMVC.PamDataUnit;
import generalDatabase.SQLTypes;
import gibbon.GibbonControl;
import gibbon.GibbonDataBlock;

public class GibbonDatabase extends AcousticSQLLogging {

	private GibbonControl gibbonControl;
	private Object gibbonDatablock;

	public GibbonDatabase(GibbonControl gibbonControl, GibbonDataBlock gibbonDataBlock) {
		super(gibbonDataBlock, gibbonControl.getUnitName());
		this.gibbonControl = gibbonControl;
		this.gibbonDatablock = gibbonDatablock;
	}

	@Override
	public void setTableData(SQLTypes sqlTypes, PamDataUnit pamDataUnit) {
		super.setTableData(sqlTypes, pamDataUnit);
	}

	@Override
	protected PamDataUnit createDataUnit(SQLTypes sqlTypes, long timeMilliseconds, int chanMap, long duration,
			double[] f) {
		return null;
	}




}
