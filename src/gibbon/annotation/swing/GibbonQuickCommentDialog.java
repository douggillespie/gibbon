package gibbon.annotation.swing;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.Window;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;

import PamView.dialog.PamDialog;

/**
 * Quick comment for gibbon annotation, generally applied to multiple calls. 
 * @author dg50
 *
 */
public class GibbonQuickCommentDialog extends PamDialog {
	
	private static GibbonQuickCommentDialog singleInstance;
	
	private QuickCommentData commentData;
	
	private JLabel nItemsLabel;
	
	private JTextArea mainText;

	private GibbonQuickCommentDialog(Window parentFrame) {
		super(parentFrame, "Comment data", false);
		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.setBorder(new TitledBorder("Gibbon free txt info"));
		nItemsLabel = new JLabel("jgjgjf;jg;ajfd", JLabel.LEFT);
		mainText = new JTextArea(5, 60);
		mainPanel.add(BorderLayout.NORTH, nItemsLabel);
		mainPanel.add(BorderLayout.CENTER, mainText);
		setResizable(true);
		setDialogComponent(mainPanel);
	}
	
	public static QuickCommentData showDialog(Window parentFrame, Point point,  QuickCommentData commentData) {
		if (singleInstance == null || singleInstance.getParent() != parentFrame) {
			singleInstance = new GibbonQuickCommentDialog(parentFrame);
		}
		if (point != null) {
			singleInstance.setCloseLocation(point);
		}
		singleInstance.commentData = commentData;
		singleInstance.setParams();
		singleInstance.setVisible(true);
		return singleInstance.commentData;
	}

	private void setParams() {
		if (commentData.getnUnits() == 0) {
			nItemsLabel.setText("Set freeform comment on gibbon call(s))");
		}
		else if (commentData.getnUnits() == 1) {
			nItemsLabel.setText("Set freeform comment on gibbon call)");
		}
		else {
			nItemsLabel.setText(String.format("Set freeform comment on %d gibbon calls", commentData.getnUnits()));
		}
		mainText.setText(commentData.getComment());
		
	}

	@Override
	public boolean getParams() {
		commentData.setComment(mainText.getText());
		return true;
	}

	@Override
	public void cancelButtonPressed() {
		commentData = null;
	}

	@Override
	public void restoreDefaultSettings() {
		// TODO Auto-generated method stub
		
	}

}
