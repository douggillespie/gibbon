package gibbon;

import java.io.File;
import java.util.ArrayList;

import PamDetection.RawDataUnit;
import PamUtils.PamCalendar;
import PamUtils.PamUtils;
import PamguardMVC.PamDataBlock;
import PamguardMVC.PamDataUnit;
import PamguardMVC.PamObservable;
import PamguardMVC.PamProcess;
import gibbon.segmenter.ChannelGroupSegmenter;
import gibbon.segmenter.SegmentGroupObserver;
import rawDeepLearningClassifier.DLStatus;
import rawDeepLearningClassifier.dlClassification.animalSpot.StandardModelParams;
import rawDeepLearningClassifier.dlClassification.archiveModel.ArchiveModelWorker;
import rawDeepLearningClassifier.dlClassification.genericModel.StandardPrediction;
import rawDeepLearningClassifier.segmenter.GroupedRawData;

/**
 * Abstract class for processing with different model types. There will be two 
 * sub classes, one for jpam models and one for the pythorch models CS are developing. 
 */
public class GibbonJPAMProcess extends PamProcess implements SegmentGroupObserver {


	private GibbonControl gibbonControl;
	private GibbonDLProcess gibbonDlProcess;
	private GibbonResultDataBlock resultDataBlock;
	private ModelInfoDataBlock modelInfoDataBlock;
	private ArchiveModelWorker worker;
	ChannelGroupSegmenter channelGroupSegmenter;
	private StandardModelParams modelParams;

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
		
		// sort out the raw data block. 
		GibbonParameters params = gibbonControl.getGibbonParameters();
		params.fLow = 800;
		params.fHigh = 2200;
		PamDataBlock input = gibbonControl.getPamConfiguration().getDataBlockByLongName(params.rawDataSource);
		setParentDataBlock(input);
		if (input == null) {
			return false;
		}
		
		String modelPath = gibbonControl.getGibbonParameters().modelLocation;
		if (modelPath == null) {
			return false;
		}
		File modelFile = new File(modelPath);
		if (modelFile.exists() == false) {
			return false;
		}
		
		worker = new ArchiveModelWorker();

		modelParams = new StandardModelParams();
		modelParams.modelPath = modelPath;

		System.out.println("Loading model " + modelPath);
		DLStatus status = worker.prepModel(modelParams, null);
		if (status != DLStatus.MODEL_LOAD_SUCCESS) {
			System.out.println("Model load failed " + status);
			return false;
		}
		System.out.println("Model load success");
//		model will take 7s segments, and returns three classes. 
		double segmentLength = modelParams.defaultSegmentLen;
		int segSamples = (int) Math.round((segmentLength / 1000.) * input.getSampleRate());
		
		int segmentMap = params.channelMap & input.getChannelMap();
		int seghop = (int) (2 * getSampleRate());
		channelGroupSegmenter = new ChannelGroupSegmenter(segmentMap, (int) segSamples, seghop, input.getSampleRate(), this);
		
		return ok;
	}

	@Override
	public void newData(PamObservable o, PamDataUnit arg) {
		if (gibbonControl.getModelType() != ModelType.JPAM) {
			return;
		}
		RawDataUnit rawUnit = (RawDataUnit) arg;
		channelGroupSegmenter.newRawData(rawUnit);
	}

	@Override
	public String getProcessName() {
		return "JPAM Process";
	}

	@Override
	public void newDataList(ArrayList<RawDataUnit> newDataUnits) {
		/**
		 * We arrive here when the segmenter has organised the data into chunks of the right size. 
		 */
		// put it into the correct format for model input
		ArrayList<GroupedRawData> groupedData = new ArrayList<GroupedRawData>();
		for (int i = 0; i < newDataUnits.size(); i++) {
			RawDataUnit rdu = newDataUnits.get(i);
			double[] data = rdu.getRawData();
			GroupedRawData grd = new GroupedRawData(rdu.getTimeMilliseconds(), rdu.getChannelBitmap(), rdu.getStartSample(), data.length, data.length);
			grd.setParentDataUnit(rdu);
			double[][] data2 = {data};
			grd.setRawData(data2);
			groupedData.add(grd);
			ArrayList<StandardPrediction> modelOut = worker.runModel(groupedData, getSampleRate(), 0);
			float[] prediction = modelOut.get(0).getPrediction();
//			System.out.printf("Model out channel %d", PamUtils.getSingleChannel(rdu.getChannelBitmap()));
//			for (int p = 0; p < prediction.length; p++) {
//				System.out.printf(", %5.3f", prediction[p]);
//			}
//			System.out.printf(" at %s\n", PamCalendar.formatDBDateTime(rdu.getTimeMilliseconds()));
			groupedData.clear();
			
			/*
			 *  format this into a gibbon result data unit and put into the model results datablock. This will
			 *  getpicked up by the third stage process that generates calls output. 
			 * Prediction[1] is for female. not sure what the other two are. Probably nothing and male ?
			 * Note that GibbonResult object was designed for a time series of results from a single classification
			 * which is quite different from what we have here which is one set of results for a single time period with
			 * multiple classifiction
			 */
			float[] femaleResult = {prediction[1]};
			double segLen = modelParams.defaultSegmentLen;
			GibbonResult gibbonResult = new GibbonResult(rdu.getTimeMilliseconds(),
					(long) segLen, rdu.getChannelBitmap(), femaleResult);
			resultDataBlock.addPamData(gibbonResult);
		}


//		System.out.printf("Segment data arrived: %d units with segment length %d, start %s\n", 
//				newDataUnits.size(), newDataUnits.get(0).getSampleDuration(), PamCalendar.formatDBDateTime(newDataUnits.get(0).getTimeMilliseconds()));
	}
	
	
	
	
}
