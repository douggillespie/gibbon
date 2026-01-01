package gibbon.swing;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import PamUtils.FrequencyFormat;
import PamUtils.PamCalendar;
import PamView.dialog.PamDialog;
import PamView.dialog.PamDialogPanel;
import PamView.dialog.PamGridBagContraints;
import PamView.panel.PamAlignmentPanel;
import PamguardMVC.PamDataUnit;
import annotation.AnnotationDialogPanel;
import gibbon.GibbonControl;
import gibbon.GibbonDataUnit;
import gibbon.annotation.GibbonCallTypes;

public class GibbonCallDialogPanel implements AnnotationDialogPanel {

	private GibbonControl gibbonControl;
	
	private GibbonDataUnit gibbonDataUnit;
	
	private JTextField confidence;
	
	private JTextArea comment;
	
	private JComboBox<String> callType;
	
	private JLabel model;
	
	private JLabel time;
	
	private JLabel frequency;

	private JPanel mainPanel;

	public GibbonCallDialogPanel(GibbonControl gibbonControl) {
		this.gibbonControl = gibbonControl;

		mainPanel = new JPanel(new BorderLayout());
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
		
	}

	@Override
	public JComponent getDialogComponent() {
		// TODO Auto-generated method stub
		return mainPanel;
	}

	@Override
	public void setParams(PamDataUnit dataUnit) {
		gibbonDataUnit = (GibbonDataUnit) dataUnit;
		if (gibbonDataUnit == null) {
			return;
		}
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
	public boolean getParams(PamDataUnit dataUnit) {
		gibbonDataUnit = (GibbonDataUnit) dataUnit;
		if (gibbonDataUnit == null) {
			return false;
		}
		gibbonDataUnit.setCallType((String) callType.getSelectedItem());
		String conf = confidence.getText();
		try {
			int c = Integer.valueOf(conf);
			gibbonDataUnit.setConfidence(c);			
		}
		catch (NumberFormatException e) {
			return PamDialog.showWarning(null, "Gibbon call parameters", "Invalid confidence value. Must be an integer (whole number)");
		}
		gibbonDataUnit.setComment(comment.getText());
		return true;
	}

	/**
	 * @return the gibbonDataUnit
	 */
	public GibbonDataUnit getGibbonDataUnit() {
		return gibbonDataUnit;
	}

	/**
	 * @param gibbonDataUnit the gibbonDataUnit to set
	 */
	public void setGibbonDataUnit(GibbonDataUnit gibbonDataUnit) {
		this.gibbonDataUnit = gibbonDataUnit;
	}

}
