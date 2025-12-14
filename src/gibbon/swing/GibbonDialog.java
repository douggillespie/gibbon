package gibbon.swing;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;


import PamController.PamFolders;
import PamDetection.RawDataUnit;
import PamUtils.PamFileChooser;
import PamUtils.PamFileFilter;
import PamUtils.SelectFolder;
import PamView.dialog.PamDialog;
import PamView.dialog.SourcePanel;
import PamView.panel.PamAlignmentPanel;
import PamguardMVC.PamDataBlock;
import gibbon.GibbonControl;
import gibbon.GibbonParameters;

public class GibbonDialog extends PamDialog {

	private GibbonControl gibbonControl;
	
	private static GibbonDialog singleInstance;
	
	private GibbonParameters params;
	
	private SourcePanel sourcePanel;
	
	private PamFileChooser modelChooser;
	
	private JTextField modelFile;
	
	private JButton browseModels;
	
	private GibbonDialog(GibbonControl gibbonControl) {
		super(gibbonControl.getGuiFrame(), gibbonControl.getUnitName(), true);
		this.gibbonControl = gibbonControl;
		sourcePanel = new SourcePanel(this, "Audio data source", RawDataUnit.class, true, true);
		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(BorderLayout.NORTH, sourcePanel.getPanel());
		
		JPanel modelPanel = new JPanel(new BorderLayout());
		modelPanel.setBorder(new TitledBorder("Pythorch Model"));
		modelFile = new JTextField(50);
		modelChooser = new PamFileChooser(PamFolders.getDefaultProjectFolder());
		modelChooser.setFileFilter(new PamFileFilter("Pytorch Models", ".pt"));
		browseModels = new JButton("Browse ...");
		modelPanel.add(BorderLayout.NORTH, modelFile);
		modelPanel.add(BorderLayout.SOUTH, new PamAlignmentPanel(browseModels, BorderLayout.EAST));
		mainPanel.add(BorderLayout.SOUTH, modelPanel);
		
		setDialogComponent(mainPanel);
		
		browseModels.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				choseModel();
			}
		});
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
		
		modelFile.setText(params.modelLocation);
		
	}

	protected void choseModel() {
		int ans = modelChooser.showOpenDialog(this);
		if (ans == JFileChooser.APPROVE_OPTION) {
			File f = modelChooser.getSelectedFile();
			if (f != null && f.exists()) {
				params.modelLocation = f.getAbsolutePath();
				modelFile.setText(f.getAbsolutePath());
			}
		}
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
		
		try {
			File f = new File(modelFile.getText());
			if (f == null || f.exists() == false) {
				return showWarning("no model file selected");
			}
			params.modelLocation = f.getAbsolutePath();
		}
		catch (Exception e){
			return showWarning("no model file selected");
		}
		
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
