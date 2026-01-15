package gibbon.io;

import java.sql.Types;

import PamguardMVC.PamDataUnit;
import generalDatabase.PamTableDefinition;
import generalDatabase.PamTableItem;
import generalDatabase.SQLLogging;
import generalDatabase.SQLTypes;
import gibbon.GibbonControl;
import gibbon.ModelInfo;
import gibbon.ModelInfoDataBlock;
import gibbon.ModelInfoDataUnit;

public class GibbonModelLogging extends SQLLogging {
	
	private PamTableItem modelFile, fileSize, dateCreated, dateModified, description;

	public GibbonModelLogging(GibbonControl gibbonControl, ModelInfoDataBlock pamDataBlock) {
		super(pamDataBlock);
		PamTableDefinition tableDef = new PamTableDefinition(gibbonControl.getUnitName()+"_model");
		modelFile = new PamTableItem("Model file", Types.CHAR, 50);
		fileSize = new PamTableItem("Model size", Types.INTEGER);
		dateCreated = new PamTableItem("Created", Types.TIMESTAMP);
		dateModified = new PamTableItem("Created", Types.TIMESTAMP);
		description = new PamTableItem("Description", Types.VARCHAR);
		
		tableDef.addTableItem(modelFile);
		tableDef.addTableItem(fileSize);
		tableDef.addTableItem(dateCreated);
		tableDef.addTableItem(dateModified);
		tableDef.addTableItem(description);
		
		setTableDefinition(tableDef);
	}

	@Override
	public void setTableData(SQLTypes sqlTypes, PamDataUnit pamDataUnit) {
		ModelInfoDataUnit midu = (ModelInfoDataUnit) pamDataUnit;
		ModelInfo modelInfo = midu.getModelInfo();
		modelFile.setValue(modelInfo.getModelFile());
		fileSize.setValue(modelInfo.getSize());
		dateCreated.setValue(sqlTypes.getTimeStamp(modelInfo.getCreationDate()));
		dateModified.setValue(sqlTypes.getTimeStamp(modelInfo.getModifiedData()));
		description.setValue(modelInfo.getDescription());
		
	}

}
