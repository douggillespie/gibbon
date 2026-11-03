package gibbon;

import java.io.File;

import PamDetection.RawDataUnit;
import PamguardMVC.PamDataUnit;
import PamguardMVC.PamObservable;
import PamguardMVC.PamProcess;
import PamguardMVC.PamRawDataBlock;
import rawDeepLearningClassifier.DLStatus;
import rawDeepLearningClassifier.dlClassification.animalSpot.StandardModelParams;
import rawDeepLearningClassifier.dlClassification.archiveModel.ArchiveModelWorker;
import rawDeepLearningClassifier.segmenter.GroupedRawData;

/**
 * Abstract class for processing with different model types. There will be two 
 * sub classes, one for jpam models and one for the pythorch models CS are developing. 
 */
public class GibbonJPAMProcess extends PamProcess{


	private GibbonControl gibbonControl;
	private GibbonDLProcess gibbonDlProcess;
	private GibbonResultDataBlock resultDataBlock;
	private ModelInfoDataBlock modelInfoDataBlock;
	private ArchiveModelWorker worker;

	public GibbonJPAMProcess(GibbonControl gibbonControl, GibbonDLProcess gibbonDlProcess) {
		super(gibbonControl, null);
		this.gibbonControl = gibbonControl;
		this.gibbonDlProcess = gibbonDlProcess;
		
		this.resultDataBlock = gibbonDlProcess.getResultDataBlock();
		this.modelInfoDataBlock = gibbonDlProcess.getModelInfoDataBlock();
	}

	@Override
	public void pamStart() {
		// TODO Auto-generated method stub
		if (gibbonControl.getModelType() != ModelType.JPAM) {
			return;
		}
		
	}

	@Override
	public void pamStop() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean prepareProcessOK() {
		if (gibbonControl.getModelType() != ModelType.JPAM) {
			setParentDataBlock(null);
			return true;
		}
		boolean ok = super.prepareProcessOK();
		String modelPath = gibbonControl.getGibbonParameters().modelLocation;
		if (modelPath == null) {
			return false;
		}
		File modelFile = new File(modelPath);
		if (modelFile.exists() == false) {
			return false;
		}
		

		worker = new ArchiveModelWorker();

		StandardModelParams params = new StandardModelParams();
		params.modelPath = modelPath;

		System.out.println("Loading model " + modelPath);
		DLStatus status = worker.prepModel(params, null);
		if (status != DLStatus.MODEL_LOAD_SUCCESS) {
			System.out.println("Model load failed " + status);
			return false;
		}
		System.out.println("Model load success");
//		model will take 7s segments, and returns three classes. 

				
		
		
		return ok;
	}

	@Override
	public void newData(PamObservable o, PamDataUnit arg) {
		if (gibbonControl.getModelType() != ModelType.JPAM) {
			return;
		}
		RawDataUnit rawUnit = (RawDataUnit) arg;
		GroupedRawData grd = new GroupedRawData(getChainPosition(), getNumOutputDataBlocks(), getNumMuiltiplexDataBlocks(), getLastSourceNotificationType(), getChainPosition())
	}

	@Override
	public String getProcessName() {
		return "JPAM Process";
	}
	
	
	
	
}
