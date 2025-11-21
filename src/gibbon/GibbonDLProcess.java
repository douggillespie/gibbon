package gibbon;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;

import org.jamdev.jdl4pam.utils.DLUtils;

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
import gibbon.swing.GibbonOverlayDraw;

public class GibbonDLProcess extends PamProcess {

	private GibbonDataBlock gibbonDataBlock;
	private GibbonControl gibbonControl;
	private int highestChannel;
	
//	String mFile = "C:\\Users\\dg50\\source\\repos\\Arcus-CRNN\\runNewData\\model\\crnn_model_finetuned_3.pt";
	String mFile = "C:\\Users\\dg50\\source\\repos\\gibbon\\src\\models\\crnn_model0_for_java.pt";
	private Model dlModel;
	private DLTranslator dlTranslator;
	private Predictor<float[][][], float[]> dlPredictor;
	

	int d1 = 1;
	int d2 = 32;
	int d3 = 751;
	

	Random r = new Random();
//	priva

	public static void main(String args[]) {
		
	}
	
	public GibbonDLProcess(GibbonControl gibbonControl) {
		super(gibbonControl, null);
		this.gibbonControl = gibbonControl;
		
		gibbonDataBlock = new GibbonDataBlock(this, 1);
		addOutputDataBlock(gibbonDataBlock);
		gibbonDataBlock.SetLogging(new GibbonDatabase(gibbonControl, gibbonDataBlock));
		gibbonDataBlock.setOverlayDraw(new GibbonOverlayDraw(gibbonControl, gibbonDataBlock));

		setParentDataBlock(gibbonControl.getGibbonPreProcess().getModelInputDataBlock());
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
		runModel(modelInputDataUnit);
	}

	private void runModel(ModelInputDataUnit modelInputDataUnit) {
		/*
		 * Call the model
		 * group /  merge positive seconds. 
		 * Collate results over all channels, or whatever options for collation
		 * has been selected. 
		 * will also want to output the model results for display and possible 
		 * storage in a separate data stream. 
		 */
		float[][][] input = {modelInputDataUnit.getModelData()};
		float[] result = null;
		
		
				
		input = new float[d1][d2][d3];
		for (int i = 0; i < d2; i++) {
			for (int j = 0; j < d3; j++) {
				input[0][i][j] = r.nextFloat()/2f+0.1f;
			}
		}
		
		
		try {
			result = dlPredictor.predict(input);
		} catch (TranslateException e) {
			e.printStackTrace();
		}
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
	
	private class DLTranslator implements Translator<float[][][], float[]> {

		@Override
		public NDList processInput(TranslatorContext ctx, float[][][] input) throws Exception {
			float[] flatData = DLUtils.flattenDoubleArrayF(input);
			Shape shape = new Shape(1,d1,d2,d3);
			NDArray ndArray = ctx.getNDManager().create(flatData, shape);
			NDList ndList = new NDList(ndArray);
			return ndList;
		}

		@Override
		public float[] processOutput(TranslatorContext ctx, NDList list) throws Exception {
			// TODO Auto-generated method stub
			return new float[3];
		}
		
	}

	@Override
	public String getProcessName() {
		return "DL Processing";
	}

}
