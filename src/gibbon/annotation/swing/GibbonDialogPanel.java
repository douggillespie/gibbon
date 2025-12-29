package gibbon.annotation.swing;

import javax.swing.JComponent;
import javax.swing.JPanel;

import PamguardMVC.PamDataUnit;
import annotation.AnnotationDialogPanel;

public class GibbonDialogPanel implements AnnotationDialogPanel {

	private JPanel mainPanel; 
	
	public GibbonDialogPanel() {
		mainPanel = new JPanel();
	}

	@Override
	public JComponent getDialogComponent() {
		return mainPanel;
	}

	@Override
	public void setParams(PamDataUnit pamDataUnit) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean getParams(PamDataUnit pamDataUnit) {
		// TODO Auto-generated method stub
		return false;
	}

}
