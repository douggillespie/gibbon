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
	
	private static GibbonCallDialog singleInstance;
	
	private GibbonDataUnit gibbonDataUnit;

	private GibbonControl gibbonControl;
	
	private JTextField confidence;
	
	private JTextArea comment;
	
	private JComboBox<String> callType;
	
	private JLabel model;
	
	private JLabel time;
	
	private JLabel frequency;

	private GibbonCallDialog(Window parentFrame, GibbonControl gibbonControl) {
		super(parentFrame, "Gibbon Detection", false);
		this.gibbonControl = gibbonControl;
		JPanel mainPanel = new JPanel(new BorderLayout());
		confidence = new JTextField(3);
		comment = new JTextArea(5,30);
		callType = new JComboBox<>();
		model = new JLabel(" ");
		time = new JLabel(" ");
		frequency = new JLabel(" ");
		
		JPanel nPanel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new PamGridBagContraints();
		nPanel.setBorder(new TitledBorder("Call information"));
		mainPanel.add(BorderLayout.NORTH,new PamAlignmentPanel(nPanel, BorderLayout.WEST, true));
		nPanel.add(new JLabel("Time ", JLabel.RIGHT), c);
		c.gridx++;
		c.gridwidth = 2;
		nPanel.add(time, c);
		c.gridx = 0;
		c.gridwidth = 1;
		c.gridy++;
		nPanel.add(new JLabel("Frequency ", JLabel.RIGHT), c);
		c.gridx++;
		c.gridwidth = 2;
		nPanel.add(frequency, c);
		c.gridx = 0;
		c.gridwidth = 1;
		c.gridy++;
		nPanel.add(new JLabel("Model ", JLabel.RIGHT), c);
		c.gridx++;
		c.gridwidth = 2;
		nPanel.add(model, c);
		c.gridx = 0;
		c.gridwidth = 1;
		c.gridy++;
		nPanel.add(new JLabel("Call Type ", JLabel.RIGHT), c);
		c.gridx++;
		c.gridwidth = 1;
		nPanel.add(callType, c);
		c.gridx = 0;
		c.gridwidth = 1;
		c.gridy++;
		nPanel.add(new JLabel("Confidence ", JLabel.RIGHT), c);
		c.gridx++;
		c.gridwidth = 1;
		nPanel.add(confidence, c);
				
		String[] types = GibbonCallTypes.types;
		for (int i = 0; i < types.length; i++) {
			callType.addItem(types[i]);
		}
		
		JPanel commentPanel = new JPanel(new BorderLayout());
		commentPanel.setBorder(new TitledBorder("Comments"));
		commentPanel.add(BorderLayout.CENTER, comment);
		mainPanel.add(BorderLayout.CENTER, commentPanel);
		
		setResizable(true);
		setDialogComponent(mainPanel);
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
		time.setText(String.format("%s : %3.1fs", 
				PamCalendar.formatDBDateTime(gibbonDataUnit.getTimeMilliseconds(), true), 
				gibbonDataUnit.getDurationInMilliseconds()/1000.));
		frequency.setText(FrequencyFormat.formatFrequencyRange(gibbonDataUnit.getFrequency(), true));
		model.setText(gibbonDataUnit.getModel());
		
		callType.setSelectedItem(gibbonDataUnit.getCallType());
		confidence.setText(String.format("%d", gibbonDataUnit.getConfidence()));
		
		comment.setText(gibbonDataUnit.getComment());
		
		
	}

	@Override
	public boolean getParams() {
		
		gibbonDataUnit.setCallType((String) callType.getSelectedItem());
		String conf = confidence.getText();
		try {
			int c = Integer.valueOf(confidence.getText());
		}
		catch (NumberFormatException e) {
			return showWarning("Invalid confidence value. Must be an integer (whole number)");
		}
		gibbonDataUnit.setComment(comment.getText());
		
		return true;
	}

	@Override
	public void cancelButtonPressed() {
		gibbonDataUnit = null;
	}

	@Override
	public void restoreDefaultSettings() {		
	}

}
