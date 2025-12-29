package gibbon;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import PamController.PamConfiguration;
import PamController.PamControlledUnit;
import PamController.PamControlledUnitSettings;
import PamController.PamControllerInterface;
import PamController.PamSettingManager;
import PamController.PamSettings;
import PamView.paneloverlay.overlaymark.OverlayMarkObservers;
import gibbon.annotation.GibbonAnnotationHandler;
import gibbon.annotation.GibbonAnnotationType;
import gibbon.annotation.GibbonMarkObserver;
import gibbon.offline.CallOfflineTask;
import gibbon.swing.GibbonDialog;
import offlineProcessing.OLProcessDialog;
import offlineProcessing.OfflineTaskGroup;

public class GibbonControl extends PamControlledUnit implements PamSettings{

	public static final String unitType = "Gibbon Detector";
	
	private GibbonParameters gibbonParameters = new GibbonParameters();
	
	private GibbonPreProcess gibbonPreProcess;
	
	private GibbonDLProcess gibbonDLProcess;
	
	private GibbonCallProcess gibbonCallProcess;

	private CallOfflineTask callOfflineTask;

	private OfflineTaskGroup offlineTaskGroup;

//	private GibbonAnnotationHandler annotationHandler;
	
	public GibbonControl(PamConfiguration pamConfiguration, String unitName) {
		super(pamConfiguration, unitType, unitName);
		gibbonPreProcess = new GibbonPreProcess(this);
		addPamProcess(gibbonPreProcess);
		gibbonDLProcess = new GibbonDLProcess(this);
		addPamProcess(gibbonDLProcess);		
		gibbonCallProcess = new GibbonCallProcess(this);
		addPamProcess(gibbonCallProcess);
		
		PamSettingManager.getInstance().registerSettings(this);
		
		OverlayMarkObservers.singleInstance().addObserver(new GibbonMarkObserver(this));
		
		/**
		 * don't use the handler, just build everything in. Perhaps will use handler for
		 * other annotations such as SNR later on. 
		 */
//		annotationHandler = new GibbonAnnotationHandler(this, gibbonCallProcess.getGibbonDataBlock());
//		annotationHandler.addAnnotationType(new GibbonAnnotationType(this));
		
		gibbonParameters.nSliceX = 751;
		gibbonParameters.nHopX = 751;
		
//		if (isViewer()) {
			createOfflineTasks();
//		}
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
		if (isViewer() == false) {
			return menuItem;
		}
		JMenu menu = new JMenu(getUnitName());
		menu.add(menuItem);
		
		menuItem = new JMenuItem("Detect Calls");
		menuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				runDetectCallsTask(parentFrame);
			}
		});
		menu.add(menuItem);
		
		return menu;
	}

	private void createOfflineTasks() {
		callOfflineTask = new CallOfflineTask(this, gibbonDLProcess.getResultDataBlock(), gibbonCallProcess.getGibbonDataBlock());
		offlineTaskGroup = new OfflineTaskGroup(this, getUnitName());
		offlineTaskGroup.addTask(callOfflineTask);
		addOfflineTaskGroup(offlineTaskGroup);
	}

	/**
	 * Offline task to reprocess all gibbon calls from the model result data. 
	 * @param parentFrame
	 */
	protected void runDetectCallsTask(Frame parentFrame) {
		OLProcessDialog olp = new OLProcessDialog(getGuiFrame(), offlineTaskGroup, getUnitName());
		olp.setVisible(true);
	}

	/**
	 * Show the detection dialog
	 * @param parentFrame
	 */
	public boolean showDetectionDialog(Frame parentFrame) {
		GibbonParameters newParams = GibbonDialog.showDialog(this);
		if (newParams != null) {
			gibbonParameters = newParams;
			gibbonPreProcess.setupProcess();
			return true;
		}
		else {
			return false;
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

	/**
	 * @return the gibbonCallProcess
	 */
	public GibbonCallProcess getGibbonCallProcess() {
		return gibbonCallProcess;
	}



}
