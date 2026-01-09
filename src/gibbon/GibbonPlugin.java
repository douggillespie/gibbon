package gibbon;

import Acquisition.AcquisitionControl;
import PamDetection.RawDataUnit;
import PamModel.PamDependency;
import PamModel.PamPluginInterface;

public class GibbonPlugin implements PamPluginInterface {


	private String jarFile;
	
	@Override
	public String getDefaultName() {
		return GibbonControl.unitType;
	}

	@Override
	public String getHelpSetName() {
		return "help/Gibbon.hs";
	}

	@Override
	public void setJarFile(String jarFile) {
		this.jarFile = jarFile;
	}

	@Override
	public String getJarFile() {
		return jarFile;
	}

	@Override
	public String getDeveloperName() {
		return "Douglas Gillespie";
	}

	@Override
	public String getContactEmail() {
		return "dg50@st-andrews.ac.uk";
	}

	@Override
	public String getVersion() {
		// TODO Auto-generated method stub
		return "0.0";
	}

	@Override
	public String getPamVerDevelopedOn() {
		return "2.02.18";
	}

	@Override
	public String getPamVerTestedOn() {
		return "2.02.17";
	}

	@Override
	public String getAboutText() {
		return "AI Detector gor Gibbon calls";
	}

	@Override
	public String getClassName() {
		return GibbonControl.class.getName();
	}

	@Override
	public String getDescription() {
		return getDefaultName();
	}

	@Override
	public String getMenuGroup() {
		// TODO Auto-generated method stub
		return "Detectors";
	}

	@Override
	public String getToolTip() {
		return getAboutText();
	}

	@Override
	public PamDependency getDependency() {
		return new PamDependency(RawDataUnit.class, AcquisitionControl.class.getName());
	}

	@Override
	public int getMinNumber() {
		return 0;
	}

	@Override
	public int getMaxNumber() {
		return 0;
	}

	@Override
	public int getNInstances() {
		return 0;
	}

	@Override
	public boolean isItHidden() {
		return false;
	}

	@Override
	public int allowedModes() {
		return ALLMODES;
	}

}
