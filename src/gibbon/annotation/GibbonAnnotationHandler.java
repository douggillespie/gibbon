package gibbon.annotation;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import PamguardMVC.PamDataBlock;
import PamguardMVC.PamDataUnit;
import PamguardMVC.datamenus.DataMenuParent;
import annotation.handler.AnnotationHandler;
import gibbon.GibbonControl;
import gibbon.GibbonDataBlock;
import gibbon.GibbonDataUnit;

public class GibbonAnnotationHandler extends AnnotationHandler {

	public GibbonAnnotationHandler(GibbonControl gibbonControl, PamDataBlock pamDataBlock) {
		super(pamDataBlock);
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
		return items;
	}

	@Override
	public List<JMenuItem> getMultiUnitMenuItems(DataMenuParent menuParent, Point mousePosition,
			PamDataUnit[] dataUnits) {
//		return super.getMultiUnitMenuItems(menuParent, mousePosition, dataUnits);
		// make a menu that can set the species for all selected units ...
		JMenuItem manyItem = new JMenu("Set species");
		manyItem.setToolTipText("Set species for multiple clips");
		String[] spList = GibbonCallTypes.types;
		for (int i = 0; i < spList.length; i++) {
			JMenuItem spM = new JMenuItem(spList[i]);
			spM.addActionListener(new SetManySpecies(dataUnits, spList[i]));
			manyItem.add(spM);
		}
		ArrayList<JMenuItem> items = new ArrayList<>();
		items.add(manyItem);
		return items;
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
	
	
//	public GibbonAnnotationHandler(GibbonControl gibbonControl, GibbonDataBlock gibbonDataBlock) {
//		// TODO Auto-generated constructor stub
//	}

}
