package gibbon;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;

import javax.swing.JMenuItem;

import PamController.PamConfiguration;
import PamController.PamControlledUnit;
import PamController.PamControlledUnitSettings;
import PamController.PamControllerInterface;
import PamController.PamSettingManager;
import PamController.PamSettings;
import gibbon.swing.GibbonDialog;

public class GibbonControl extends PamControlledUnit implements PamSettings{

	public static final String unitType = "Gibbon Detector";
	
	private GibbonParameters gibbonParameters = new GibbonParameters();
	
	private GibbonPreProcess gibbonPreProcess;
	
	private GibbonDLProcess gibbonDLProcess;
	
	public GibbonControl(PamConfiguration pamConfiguration, String unitName) {
		super(pamConfiguration, unitType, unitName);
		gibbonPreProcess = new GibbonPreProcess(this);
		addPamProcess(gibbonPreProcess);
		gibbonDLProcess = new GibbonDLProcess(this);
		addPamProcess(gibbonDLProcess);		
		
		PamSettingManager.getInstance().registerSettings(this);
		
		gibbonParameters.nSliceX = 640;
		gibbonParameters.nHopX = 3;
	}

	@Override
	public Serializable getSettingsReference() {
		return gibbonParameters;
	}

	@Override
	public long getSettingsVersion() {
		return GibbonParameters.serialVersionUID;
	}

	@Override
	public boolean restoreSettings(PamControlledUnitSettings pamControlledUnitSettings) {
		gibbonParameters = (GibbonParameters) pamControlledUnitSettings.getSettings();
		return true;
	}

	@Override
	public void notifyModelChanged(int changeType) {
		super.notifyModelChanged(changeType);
		if (changeType == PamControllerInterface.INITIALIZATION_COMPLETE) {
			gibbonPreProcess.setupProcess();
		}
	}

	@Override
	public JMenuItem createDetectionMenu(Frame parentFrame) {
		JMenuItem menuItem = new JMenuItem(getUnitName() + " settings ...");
		menuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showDetectionDialog(parentFrame);
			}
		});
		return menuItem;
	}

	/**
	 * Show the detection dialog
	 * @param parentFrame
	 */
	protected void showDetectionDialog(Frame parentFrame) {
		GibbonParameters newParams = GibbonDialog.showDialog(this);
		if (newParams != null) {
			gibbonParameters = newParams;
			gibbonPreProcess.setupProcess();
		}
	}

	/**
	 * @return the gibbonParameters
	 */
	public GibbonParameters getGibbonParameters() {
		return gibbonParameters;
	}

	/**
	 * @return the gibbonPreProcess
	 */
	public GibbonPreProcess getGibbonPreProcess() {
		return gibbonPreProcess;
	}

	/**
	 * @return the gibbonDLProcess
	 */
	public GibbonDLProcess getGibbonDLProcess() {
		return gibbonDLProcess;
	}



}
