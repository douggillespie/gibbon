package gibbon;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;

import org.jamdev.jdl4pam.utils.DLUtils;

import PamUtils.PamCalendar;
import PamUtils.PamUtils;
import PamguardMVC.PamDataUnit;
import PamguardMVC.PamObservable;
import PamguardMVC.PamProcess;
import ai.djl.MalformedModelException;
import ai.djl.Model;
import ai.djl.inference.Predictor;
import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDList;
import ai.djl.ndarray.types.Shape;
import ai.djl.translate.TranslateException;
import ai.djl.translate.Translator;
import ai.djl.translate.TranslatorContext;
import gibbon.io.GibbonDatabase;
import gibbon.io.GibbonResultBinary;
import gibbon.swing.GibbonOverlayDraw;
import gibbon.swing.GibbonSpecPlugin;

public class GibbonDLProcess extends PamProcess {

	private GibbonResultDataBlock resultDataBlock;
	private GibbonDataBlock gibbonDataBlock;
	private GibbonControl gibbonControl;
	private int highestChannel;
		
//	String mFile = "C:\\Users\\dg50\\source\\repos\\Arcus-CRNN\\runNewData\\model\\crnn_model_finetuned_3.pt";
	static String mFile = "C:\\Users\\dg50\\source\\repos\\gibbon\\src\\models\\crnn_model0_for_java.pt";
//	static String mFile = "/Users/jdjm/git/gibbon/src/models/crnn_model0_for_java.pt";
	private Model dlModel;
	private DLTranslator dlTranslator;
	private Predictor<float[][][], float[]> dlPredictor;
	

	int d1 = 1;
	int d2 = 32;
	int d3 = 751;
	/**
	 * 751 FFT slices with 9600Hz data and FFT hop=256 is 751/9600*256 = 20.02s.
	 * Getting 10 results from that ? 
	 */
	

	Random r = new Random();
//	priva

//	public static void main(String args[]) {
//		
//		File modelFile = new File(mFile);
//		String modelFolder = modelFile.getParent();
//	
//		String modelName = modelFile.getName();
//		Model dlModel = Model.newInstance(mFile, "PyTorch");
//		try {
//			// model needs to be saved with Pytorch.jit.save
//			//https://docs.pytorch.org/docs/stable/generated/torch.jit.save.html 
//			dlModel.load(Paths.get(modelFolder), modelName);
//		} catch (Error e4) {
//			e4.printStackTrace();
//		} catch (MalformedModelException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		System.out.println(dlModel.describeInput());
//		
//		DLTranslator dlTranslator = new DLTranslator();
//		Predictor<float[][][], float[]>  dlPredictor = dlModel.newPredictor(dlTranslator);
//		
//		Random r = new Random();
//
//		int d1 = 1;
//		int d2 = 32;
//		int d3 = 751;
//		
//		float[][][] input = new float[d1][d2][d3];
//		for (int i = 0; i < d2; i++) {
//			for (int j = 0; j < d3; j++) {
//				input[0][i][j] = r.nextFloat()/2f+0.1f;
//			}
//		}
//		
//		try {
//			
//			long time0 = System.currentTimeMillis();
//			System.out.println("Running prediction");
//			float[] result = dlPredictor.predict(input);
//			long timerun = System.currentTimeMillis() - time0;
//			System.out.println("Prediction complete in "+timerun+" ms" + " Results length: " + result.length);
//			
//		} catch (TranslateException e) {
//			e.printStackTrace();
//		}
//		
//	}
//	
	public GibbonDLProcess(GibbonControl gibbonControl) {
		super(gibbonControl, null);
		this.gibbonControl = gibbonControl;
		
		resultDataBlock = new GibbonResultDataBlock(this);
		addOutputDataBlock(resultDataBlock);
		resultDataBlock.setBinaryDataSource(new GibbonResultBinary(resultDataBlock));
		
		gibbonDataBlock = new GibbonDataBlock(this, 1);
		addOutputDataBlock(gibbonDataBlock);
		gibbonDataBlock.SetLogging(new GibbonDatabase(gibbonControl, gibbonDataBlock));
		gibbonDataBlock.setOverlayDraw(new GibbonOverlayDraw(gibbonControl, gibbonDataBlock));

		setParentDataBlock(gibbonControl.getGibbonPreProcess().getModelInputDataBlock());
		
		new GibbonSpecPlugin(this, resultDataBlock);
	}

	@Override
	public void pamStart() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pamStop() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void newData(PamObservable o, PamDataUnit arg) {
		ModelInputDataUnit modelInputDataUnit = (ModelInputDataUnit) arg;
		// now call the model I guess ? 
		float[] result = runModel(modelInputDataUnit);
		if (result != null) {
			GibbonResult gibbonResult = new GibbonResult(modelInputDataUnit.getTimeMilliseconds(),
				 modelInputDataUnit.getEndTimeInMilliseconds() - modelInputDataUnit.getTimeMilliseconds(), 
				 modelInputDataUnit.getChannelBitmap(), result);
			resultDataBlock.addPamData(gibbonResult);
		}
	}

	private float[] runModel(ModelInputDataUnit modelInputDataUnit) {
		/*
		 * Call the model
		 * group /  merge positive seconds. 
		 * Collate results over all channels, or whatever options for collation
		 * has been selected. 
		 * will also want to output the model results for display and possible 
		 * storage in a separate data stream. 
		 */
		float[][][] data = {modelInputDataUnit.getModelData()};
		float[] result = null;
		int a1 = data.length;
		int a2 = data[0].length;
		int a3 = data[0][0].length;
		a1 = Math.min(a1, d1);
		a2 = Math.min(a2, d2);
		a3 = Math.min(a3, d3);
		
		float[][][] input = new float[d1][d2][d3];
		for (int i = 0; i < a2; i++) {
			for (int j = 0; j < a3; j++) {
				input[0][i][j] = data[0][i][j];
			}
		}
		
		long t1 = System.currentTimeMillis();
		try {
			result = dlPredictor.predict(input);
		} catch (TranslateException e) {
//			e.printStackTrace();
			System.out.println("Prediction failure: " + e.getMessage());
			return null;
		}
		long t2 = System.currentTimeMillis();
//		if (result != null) {
//			System.out.printf("Channel %d returned %d results in %d millis\n", 
//					modelInputDataUnit.getChannelBitmap(), result.length, t2-t1);
//		}
//		String tit = String.format("Channel %d %s", modelInputDataUnit.getChannelBitmap(), 
//				PamCalendar.formatDBDateTime(modelInputDataUnit.getTimeMilliseconds()));
//		printFloatArray(tit, "%5.2f", result);
		return result;
	}
	
	private void printFloatArray(String tit, String fmt, float[] array) {
		if (tit != null) {
			System.out.printf(tit);
		}
		if (array == null) {
			System.out.printf("\n");
			return;
		}
		for (int i = 0; i < array.length; i++) {
			System.out.printf(", " + fmt, array[i]);
		}
		System.out.printf("\n");
	}

	@Override
	public void setupProcess() {
		super.setupProcess();
		prepareProcessOK();
	}

	@Override
	public boolean prepareProcessOK() {
		GibbonParameters params = gibbonControl.getGibbonParameters();
		// load the model file. Return false if it can't be set up. 

		resultDataBlock.setChannelMap(params.channelMap);
		gibbonDataBlock.setChannelMap(params.channelMap);
		highestChannel = PamUtils.getHighestChannel(params.channelMap);
		
		File modelFile = new File(mFile);
		String modelFolder = modelFile.getParent();
	
		String modelName = modelFile.getName();
		dlModel = Model.newInstance(mFile, "PyTorch");
		try {
			// model needs to be saved with Pytorch.jit.save
			//https://docs.pytorch.org/docs/stable/generated/torch.jit.save.html 
			dlModel.load(Paths.get(modelFolder), modelName);
		} catch (Error e4) {
			e4.printStackTrace();
		} catch (MalformedModelException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(dlModel.describeInput());
		
		dlTranslator = new DLTranslator();
		dlPredictor = dlModel.newPredictor(dlTranslator);
		
		return true;
	}
	
	
	private static class DLTranslator implements Translator<float[][][], float[]> {
		

		@Override
		public NDList processInput(TranslatorContext ctx, float[][][] input) throws Exception {

			int d1 = 1;
			int d2 = 32;
			int d3 = 751;
			
			
			float[] flatData = DLUtils.flattenDoubleArrayF(input);
			Shape shape = new Shape(d1,d2,d3);
			NDArray ndArray = ctx.getNDManager().create(flatData, shape);
						
			ndArray = ndArray.squeeze(); // Removes dimensions of size 1
			ndArray = ndArray.reshape(shape);
			
			NDList ndList = new NDList(ndArray);
			return ndList;
		}

		@Override
		public float[] processOutput(TranslatorContext ctx, NDList list) throws Exception {
//			System.out.println("Ouput list: " + list.size());

			NDArray temp_arr = list.get(0);

			Number[] number = temp_arr.toArray(); 

			float[] results = new float[number.length]; 
			for (int i=0; i<number.length; i++) {
				results[i] = number[i].floatValue(); 
			}
			
			return results;
		}
		
	}

	@Override
	public String getProcessName() {
		return "DL Processing";
	}
	
}
