package gibbon.io;

import java.sql.Types;

import PamDetection.AcousticSQLLogging;
import PamguardMVC.PamDataUnit;
import generalDatabase.PamTableItem;
import generalDatabase.SQLTypes;
import gibbon.GibbonControl;
import gibbon.GibbonDataBlock;
import gibbon.GibbonDataUnit;

public class GibbonDatabase extends AcousticSQLLogging {

	private GibbonControl gibbonControl;
	private GibbonDataBlock gibbonDataBlock;
	
	private PamTableItem scoreItem;

	public GibbonDatabase(GibbonControl gibbonControl, GibbonDataBlock gibbonDataBlock) {
		super(gibbonDataBlock, gibbonControl.getUnitName());
		this.gibbonControl = gibbonControl;
		this.gibbonDataBlock = gibbonDataBlock;
		
		scoreItem = new PamTableItem("AIScore", Types.REAL);
		getTableDefinition().addTableItem(scoreItem);
	}

	@Override
	public void setTableData(SQLTypes sqlTypes, PamDataUnit pamDataUnit) {
		super.setTableData(sqlTypes, pamDataUnit);
		GibbonDataUnit gibbonDataUnit = (GibbonDataUnit) pamDataUnit;
		scoreItem.setValue(gibbonDataUnit.getBestScore());
	}

	@Override
	protected PamDataUnit createDataUnit(SQLTypes sqlTypes, long timeMilliseconds, int chanMap, long duration,
			double[] f) {
		GibbonDataUnit dataUnit = new GibbonDataUnit(timeMilliseconds, chanMap, chanMap, 0);
		dataUnit.setDurationInMilliseconds(duration);
		dataUnit.setFrequency(f);
		dataUnit.setBestScore(scoreItem.getFloatValue());
		
		return dataUnit;
	}




}
