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
	private PamTableItem model, manualEdit, callType, confidence, comment;

	public GibbonDatabase(GibbonControl gibbonControl, GibbonDataBlock gibbonDataBlock) {
		super(gibbonDataBlock, gibbonControl.getUnitName());
		this.gibbonControl = gibbonControl;
		this.gibbonDataBlock = gibbonDataBlock;
		
		scoreItem = new PamTableItem("AIScore", Types.REAL);
		model = new PamTableItem("Model", Types.CHAR, 30);
		manualEdit = new PamTableItem("Manual Edit", Types.VARCHAR);
		callType = new PamTableItem("Call Type", Types.VARCHAR);
		confidence = new PamTableItem("Confidence", Types.INTEGER);
		comment = new PamTableItem("Comment", Types.VARCHAR);
		
		getTableDefinition().addTableItem(scoreItem);
		getTableDefinition().addTableItem(model);
		getTableDefinition().addTableItem(manualEdit);
		getTableDefinition().addTableItem(callType);
		getTableDefinition().addTableItem(confidence);
		getTableDefinition().addTableItem(comment);
	}

	@Override
	public void setTableData(SQLTypes sqlTypes, PamDataUnit pamDataUnit) {
		super.setTableData(sqlTypes, pamDataUnit);
		GibbonDataUnit gibbonDataUnit = (GibbonDataUnit) pamDataUnit;
		scoreItem.setValue(gibbonDataUnit.getBestScore());
		model.setValue(gibbonDataUnit.getModel());
		manualEdit.setValue(gibbonDataUnit.getManualEdit());
		callType.setValue(gibbonDataUnit.getCallType());
		confidence.setValue(gibbonDataUnit.getConfidence());
		comment.setValue(gibbonDataUnit.getComment());
	}

	@Override
	protected PamDataUnit createDataUnit(SQLTypes sqlTypes, long timeMilliseconds, int chanMap, long duration,
			double[] f) {
		GibbonDataUnit dataUnit = new GibbonDataUnit(timeMilliseconds, chanMap, 0, 0);
		dataUnit.setDurationInMilliseconds(duration);
		dataUnit.setFrequency(f);
		dataUnit.setBestScore(scoreItem.getFloatValue());
		dataUnit.setModel(model.getDeblankedStringValue());
		dataUnit.setManualEdit(manualEdit.getDeblankedStringValue());
		dataUnit.setCallType(callType.getDeblankedStringValue());
		dataUnit.setConfidence(confidence.getIntegerValue());
		dataUnit.setComment(comment.getDeblankedStringValue());
		
		return dataUnit;
	}




}
