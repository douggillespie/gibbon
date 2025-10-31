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
import PamController.PamSettings;

public class GibbonControl extends PamControlledUnit implements PamSettings{

	public static final String unitType = "Gibbon Detector";
	
	private GibbonParameters gibbonParameters = new GibbonParameters();
	
	private GibbonProcess gibbonProcess;
	
	public GibbonControl(PamConfiguration pamConfiguration, String unitName) {
		super(pamConfiguration, unitType, unitName);
		gibbonProcess = new GibbonProcess(this);
		addPamProcess(gibbonProcess);
		
		
//		PamSettingManager.getInstance().registerSettings(this);
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
			gibbonProcess.setupProcess();
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
		// TODO Auto-generated method stub
		
	}



}
