package gibbon.swing;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Window;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import PamUtils.FrequencyFormat;
import PamUtils.PamCalendar;
import PamView.dialog.PamDialog;
import PamView.dialog.PamGridBagContraints;
import PamView.panel.PamAlignmentPanel;
import gibbon.GibbonControl;
import gibbon.GibbonDataUnit;
import gibbon.annotation.GibbonCallTypes;

public class GibbonCallDialog extends PamDialog {

	private static final long serialVersionUID = 1L;
	
	private GibbonControl gibbonControl;
	
	private GibbonCallDialogPanel dialogPanel;

	private GibbonDataUnit gibbonDataUnit;
	
	private static GibbonCallDialog singleInstance;

	private GibbonCallDialog(Window parentFrame, GibbonControl gibbonControl) {
		super(parentFrame, "Gibbon Detection", false);
		this.gibbonControl = gibbonControl;
		dialogPanel = new GibbonCallDialogPanel(gibbonControl);
		setResizable(true);
		setDialogComponent(dialogPanel.getDialogComponent());
	}
	
	public static GibbonDataUnit showDialog(Window parentFrame, GibbonControl gibbonControl, GibbonDataUnit gibbonDataUnit) {
//		if (singleInstance == null || singleInstance.getParent() != parentFrame || singleInstance.gibbonControl != gibbonControl) {
			singleInstance = new GibbonCallDialog(parentFrame, gibbonControl);
//		}
		singleInstance.setDetection(gibbonDataUnit);
		singleInstance.setVisible(true);
		return singleInstance.gibbonDataUnit;
	}

	private void setDetection(GibbonDataUnit gibbonDataUnit) {
		this.gibbonDataUnit = gibbonDataUnit;
		dialogPanel.setParams(gibbonDataUnit);
	}

	@Override
	public boolean getParams() {
		
		return dialogPanel.getParams(gibbonDataUnit);
	}

	@Override
	public void cancelButtonPressed() {
		gibbonDataUnit = null;
	}

	@Override
	public void restoreDefaultSettings() {		
	}

}
