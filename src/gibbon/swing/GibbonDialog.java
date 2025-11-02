package gibbon.swing;

import java.awt.BorderLayout;
import java.awt.Window;

import javax.swing.JPanel;

import PamDetection.RawDataUnit;
import PamView.dialog.PamDialog;
import PamView.dialog.SourcePanel;
import PamguardMVC.PamDataBlock;
import gibbon.GibbonControl;
import gibbon.GibbonParameters;

public class GibbonDialog extends PamDialog {

	private GibbonControl gibbonControl;
	
	private static GibbonDialog singleInstance;
	
	private GibbonParameters params;
	
	private SourcePanel sourcePanel;
	
	private GibbonDialog(GibbonControl gibbonControl) {
		super(gibbonControl.getGuiFrame(), gibbonControl.getUnitName(), true);
		this.gibbonControl = gibbonControl;
		sourcePanel = new SourcePanel(this, "Audio data source", RawDataUnit.class, true, true);
		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(BorderLayout.NORTH, sourcePanel.getPanel());
		
		setDialogComponent(mainPanel);
	}
	
	public static GibbonParameters showDialog(GibbonControl gibbonControl) {
		if (singleInstance == null || singleInstance.gibbonControl != gibbonControl) {
			singleInstance = new GibbonDialog(gibbonControl);
		}
		singleInstance.setParams(gibbonControl.getGibbonParameters());
		singleInstance.setVisible(true);
		return singleInstance.params;
	}

	private void setParams(GibbonParameters gibbonParameters) {
		this.params = gibbonParameters;
		sourcePanel.setSource(params.rawDataSource);
		sourcePanel.setChannelList(params.channelMap);
		
	}

	@Override
	public boolean getParams() {
		PamDataBlock block = sourcePanel.getSource();
		if (block == null) {
			return showWarning("you must select an input data source");
		}
		params.rawDataSource = block.getLongDataName();
		int chans = sourcePanel.getChannelList();
		if (chans == 0) {
			return showWarning("You must select at least one channel to process");
		}
		params.channelMap = chans;
		
		
		return true;
	}

	@Override
	public void cancelButtonPressed() {
		params = null;
	}

	@Override
	public void restoreDefaultSettings() {
		// TODO Auto-generated method stub
		
	}



}
