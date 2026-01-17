package gibbon.annotation;

import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import PamView.PamGui;
import PamguardMVC.PamDataBlock;
import PamguardMVC.PamDataUnit;
import PamguardMVC.datamenus.DataMenuParent;
import annotation.handler.AnnotationHandler;
import gibbon.GibbonControl;
import gibbon.GibbonDataBlock;
import gibbon.GibbonDataUnit;
import gibbon.annotation.swing.GibbonQuickCommentDialog;
import gibbon.annotation.swing.QuickCommentData;

public class GibbonAnnotationHandler extends AnnotationHandler {

	private GibbonControl gibbonControl;

	public GibbonAnnotationHandler(GibbonControl gibbonControl, PamDataBlock pamDataBlock) {
		super(pamDataBlock);
		this.gibbonControl = gibbonControl;
	}

	@Override
	public List<JMenuItem> getAnnotationMenuItems(DataMenuParent menuParent, Point mousePosition,
			PamDataUnit... dataUnits) {
		// TODO Auto-generated method stub
		return super.getAnnotationMenuItems(menuParent, mousePosition, dataUnits);
	}

	@Override
	public List<JMenuItem> getSingleUnitMenuItems(DataMenuParent menuParent, Point mousePosition,
			PamDataUnit pamDataUnit) {
		JMenuItem menuItem = super.createAnnotationEditMenu(pamDataUnit);
		if (menuItem == null) {
			return null;
		}
		ArrayList<JMenuItem> items = new ArrayList<>();
		items.add(menuItem);
		// I like the multiple items too, so add them in here. 
		PamDataUnit[] units = {pamDataUnit};
		List<JMenuItem> moreItems = getMultiUnitMenuItems(menuParent, mousePosition, units);
		items.addAll(moreItems);
		return items;
	}

	@Override
	public List<JMenuItem> getMultiUnitMenuItems(DataMenuParent menuParent, Point mousePosition,
			PamDataUnit[] dataUnits) {
//		return super.getMultiUnitMenuItems(menuParent, mousePosition, dataUnits);
		// make a menu that can set the species for all selected units ...
		JMenuItem manyItem = new JMenu("Set species  ");
		manyItem.setToolTipText("Set species for multiple clips");
		String[] spList = GibbonCallTypes.types;
		for (int i = 0; i < spList.length; i++) {
			JMenuItem spM = new JMenuItem(spList[i]);
			spM.addActionListener(new SetManySpecies(dataUnits, spList[i]));
			manyItem.add(spM);
		}
		// add the confidence items. 
		JMenu cItems = new JMenu("Confidence  ");
		cItems.setToolTipText("Set confidence level for multiple clips");
		for (int i = 0; i <= 5; i++) {
			JMenuItem cI = new JMenuItem(String.format("%d", i));
			cI.addActionListener(new SetManyConfidence(dataUnits, i));
			cItems.add(cI);
		}
		JMenuItem comment = new JMenuItem(String.format("Comment on %d gibbon calls ...", dataUnits.length));
		comment.setToolTipText("Apply same comment to multiple gibbon calls");
		comment.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				commentMany(e, dataUnits);
			}
		});
		
		ArrayList<JMenuItem> items = new ArrayList<>();
		items.add(manyItem);
		items.add(cItems);
		items.add(comment);
		return items;
	}

	/**
	 * Quick comment on multiple calls. 
	 * @param e
	 * @param dataUnits
	 */
	protected void commentMany(ActionEvent e, PamDataUnit[] dataUnits) {
		QuickCommentData cDat = new QuickCommentData(null, dataUnits.length);
		for (int i = 0; i < dataUnits.length; i++) {
			if (dataUnits[i] instanceof GibbonDataUnit) {
				String c = ((GibbonDataUnit) dataUnits[i]).getComment();
				if (c != null && c.length() > 0) {
					cDat.setComment(c);
					break;
				}
			}
		}
		Window parent = null;
		Point point = null;
		if (e.getSource() instanceof Window) {
			parent = (Window) e.getSource();
		}
		if (e.getSource() instanceof JMenuItem) {
			JMenuItem mi = (JMenuItem) e.getSource();
			parent = PamGui.findComponentWindow(mi);
		}
		if (parent == null) {
			parent = gibbonControl.getGuiFrame();
		}
		cDat = GibbonQuickCommentDialog.showDialog(parent, point, cDat);
		if (cDat != null) {
			for (int i = 0; i < dataUnits.length; i++) {
				if (dataUnits[i] instanceof GibbonDataUnit) {
					GibbonDataUnit gd = (GibbonDataUnit) dataUnits[i];
					gd.setComment(cDat.getComment());
					gd.getParentDataBlock().updatePamData(gd, System.currentTimeMillis());
				}
			}
		}
		
	}

	private class SetManySpecies implements ActionListener {
		private PamDataUnit[] dataUnits;
		private String species;

		public SetManySpecies(PamDataUnit[] dataUnits, String species) {
			this.dataUnits = dataUnits;
			this.species = species;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			for (int i = 0; i < dataUnits.length; i++) {
				if (dataUnits[i] instanceof GibbonDataUnit) {
					GibbonDataUnit gibbon = (GibbonDataUnit) dataUnits[i];
					gibbon.setCallType(species);
					getPamDataBlock().updatePamData(gibbon, System.currentTimeMillis());
				}
			}
			
		}
	}
	
	private class SetManyConfidence implements ActionListener {

		private PamDataUnit[] dataUnits;
		private int confidence;

		/**
		 * @param dataUnits
		 * @param confidence
		 */
		public SetManyConfidence(PamDataUnit[] dataUnits, int confidence) {
			super();
			this.dataUnits = dataUnits;
			this.confidence = confidence;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			for (int i = 0; i < dataUnits.length; i++) {
				if (dataUnits[i] instanceof GibbonDataUnit) {
					GibbonDataUnit gibbon = (GibbonDataUnit) dataUnits[i];
					gibbon.setConfidence(confidence);
					getPamDataBlock().updatePamData(gibbon, System.currentTimeMillis());
				}
			}
		}
	}
	
	
//	public GibbonAnnotationHandler(GibbonControl gibbonControl, GibbonDataBlock gibbonDataBlock) {
//		// TODO Auto-generated constructor stub
//	}

}
