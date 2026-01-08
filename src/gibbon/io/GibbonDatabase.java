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
	private PamTableItem autoDetection, viewedAt, autoStart, autoDuration, autoF1, autoF2;

	public GibbonDatabase(GibbonControl gibbonControl, GibbonDataBlock gibbonDataBlock) {
		super(gibbonDataBlock, gibbonControl.getUnitName());
		this.gibbonControl = gibbonControl;
		this.gibbonDataBlock = gibbonDataBlock;
		
		autoDetection = new PamTableItem("Auto Detection", Types.BOOLEAN);
		scoreItem = new PamTableItem("AIScore", Types.REAL);
		model = new PamTableItem("Model", Types.CHAR, 30);
		autoStart = new PamTableItem("AutoUTC", Types.TIMESTAMP);
		autoDuration = new PamTableItem("AutoDuration", Types.DOUBLE);
		autoF1 = new PamTableItem("AutoF1", Types.DOUBLE);
		autoF2 = new PamTableItem("AutoF2", Types.DOUBLE);
		callType = new PamTableItem("Call Type", Types.VARCHAR);
		confidence = new PamTableItem("Confidence", Types.INTEGER);
		viewedAt = new PamTableItem("ViewedAt", Types.TIMESTAMP);
		comment = new PamTableItem("Comment", Types.VARCHAR);
		manualEdit = new PamTableItem("Manual Edit", Types.VARCHAR);

		getTableDefinition().addTableItem(autoDetection);
		getTableDefinition().addTableItem(model);
		getTableDefinition().addTableItem(scoreItem);
		getTableDefinition().addTableItem(autoStart);
		getTableDefinition().addTableItem(autoDuration);
		getTableDefinition().addTableItem(autoF1);
		getTableDefinition().addTableItem(autoF2);
		getTableDefinition().addTableItem(viewedAt);
		getTableDefinition().addTableItem(manualEdit);
		getTableDefinition().addTableItem(callType);
		getTableDefinition().addTableItem(confidence);
		getTableDefinition().addTableItem(comment);
	}

	@Override
	public void setTableData(SQLTypes sqlTypes, PamDataUnit pamDataUnit) {
		super.setTableData(sqlTypes, pamDataUnit);
		GibbonDataUnit gibbonDataUnit = (GibbonDataUnit) pamDataUnit;
		autoDetection.setValue(gibbonDataUnit.isAutoDetection());
		model.setValue(gibbonDataUnit.getModel());
		scoreItem.setValue(gibbonDataUnit.getBestScore());
		autoStart.setValue(sqlTypes.getTimeStamp(gibbonDataUnit.getAutoTimeMillis()));
		Double autoD = gibbonDataUnit.getAutoDuration();
		if (autoD == null) {
			autoDuration.setValue(null);
		}
		else {
			autoDuration.setValue(autoD / 1000.);
		}
		double[] autoF = gibbonDataUnit.getAutoFrequency();
		if (autoF == null) {
			autoF1.setValue(null);
			autoF2.setValue(null);
		}
		else {
			autoF1.setValue(autoF[0]);
			autoF2.setValue(autoF[1]);
		}
		viewedAt.setValue(sqlTypes.getTimeStamp(gibbonDataUnit.getViewedAt()));
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
		try {
			Double aF1 = (Double) autoF1.getValue();
			Double aF2 = (Double) autoF1.getValue();
			if (aF1 != null && aF2 != null) {
				double[] aF = new double[2];
				aF[0] = aF1;
				aF[1] = aF2;
				dataUnit.setAutoFrequency(aF);
			}
		}
		catch (Exception e){
		}
		dataUnit.setAutoTimeMillis(sqlTypes.millisFromTimeStamp(autoStart.getValue()));
		Double aD = (Double) autoDuration.getValue();
		if (aD != null) {
			dataUnit.setAutoDuration(aD);
		}
		dataUnit.setAutoDetection(autoDetection.getBooleanValue());
		dataUnit.setViewedAt(sqlTypes.millisFromTimeStamp(viewedAt.getValue()));
		
		return dataUnit;
	}




}
