package gibbon.annotation;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import PamUtils.FrequencyFormat;
import PamUtils.PamCalendar;
import PamUtils.PamCoordinate;
import PamUtils.PamUtils;
import PamView.GeneralProjector;
import PamView.GeneralProjector.ParameterType;
import PamView.HoverData;
import PamView.dialog.warn.WarnOnce;
import PamView.paneloverlay.overlaymark.ExtMouseAdapter;
import PamView.paneloverlay.overlaymark.MarkDataSelector;
import PamView.paneloverlay.overlaymark.OverlayMark;
import PamView.paneloverlay.overlaymark.OverlayMarkObserver;
import PamView.paneloverlay.overlaymark.OverlayMarker;
import Spectrogram.DirectDrawProjector;
import detectiongrouplocaliser.DetectionGroupSummary;
import gibbon.GibbonCallProcess;
import gibbon.GibbonControl;
import gibbon.GibbonDataBlock;
import gibbon.GibbonDataUnit;
import gibbon.swing.GibbonCallDialog;

/**
 * Get spectrogram mouse movements for the editing of existing units and creation of new ones. 
 * Most of this is based on the DisplayObserver in SpectrogramAnnotationModule
 * @author dg50
 *
 */
public class GibbonMarkObserver implements OverlayMarkObserver {

	private static final String MANUALNAME = "Manual selection";

	private GibbonControl gibbonControl;

	private final ParameterType[] parameterTypes = {ParameterType.TIME, ParameterType.FREQUENCY};

	private GibbonCallProcess gibbonCallProcess;

	private GibbonDataBlock callDataBlock;

	private MatchedGibbon existingGibbon;

	private double dragStartFreq;

	private long dragStartTime;

	public GibbonMarkObserver(GibbonControl gibbonControl) {
		this.gibbonControl = gibbonControl;
		this.gibbonCallProcess = gibbonControl.getGibbonCallProcess();
		this.callDataBlock = gibbonCallProcess.getGibbonDataBlock();
	}

	@Override
	public boolean markUpdate(int markStatus, javafx.scene.input.MouseEvent mouseEvent, OverlayMarker overlayMarker,
			OverlayMark overlayMark) {
//		overlayMark.setHidden(false);
		boolean consumed = false;
		int markChannels = overlayMark.getMarkChannels();
		int markChannel = PamUtils.getLowestChannel(markChannels);
		long t0 = (long) overlayMark.getCoordinate(0).getCoordinate(0);
		double f0 =  overlayMark.getCoordinate(0).getCoordinate(1);
		long t2 = (long) overlayMark.getLastCoordinate().getCoordinate(0);
		double f2 = overlayMark.getLastCoordinate().getCoordinate(1);
		MouseEvent swingMouse = ExtMouseAdapter.swingMouse(mouseEvent);
		Component swingDisplay = swingMouse.getComponent();
		if (markStatus == MARK_START) {
			existingGibbon = findExistingUnit(overlayMarker, markChannel, t0, f0);
			dragStartFreq = f0;
			dragStartTime = t0;
//			System.out.printf("Mark start : %s\n", PamCalendar.formatDBDateTime(t0, true));
			if (swingDisplay != null && existingGibbon != null && existingGibbon.getEdges() != 0) {
//				swingDisplay.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
				swingDisplay.setCursor(selectMovementCursor(existingGibbon.getEdges()));
				gibbonCallProcess.getGibbonOverlayDraw().setMarkedDataUnit(existingGibbon, dragStartTime, dragStartFreq);
				if (swingDisplay != null) {
					swingDisplay.repaint();
				}
			}
		}
		long tEnd = t2;
		double fEnd = f2;
		if (t2 == dragStartTime) {
			tEnd = t0;
		}
		if (f2 == dragStartFreq) {
			fEnd = f0;
		}
		if (markStatus == MARK_UPDATE) {
			if (existingGibbon != null) {
				gibbonCallProcess.getGibbonOverlayDraw().setMarkedDataUnit(existingGibbon, tEnd, fEnd);
			}
		}
		if (markStatus == MARK_CANCELLED) {
			existingGibbon = null;				
			if (swingDisplay != null) {
				swingDisplay.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}
		}
		if (markStatus == MARK_END) {
			/**
			 * Very annoyingly, the marker sorts the t and f, so the t2-t0 is always positive
			 * so we need to find the ones that are different !!!
			 */
			long dt = t2-t0;
			if (t2==dragStartTime) {
				dt = t0-t2;
			}
			double df = f2-f0;
			if (f2 == dragStartFreq) {
				df = f0-f2;
			}
//			System.out.printf("Mark end : %s-%s: %d\n", PamCalendar.formatDBDateTime(t0, true), 
//					PamCalendar.formatDBDateTime(t2, true), t2-t0);
			if (existingGibbon != null) {
				updateGibbon(existingGibbon, tEnd, fEnd);
			}
			else if (t0!=t2 && f0!=f2){
				createGibbon(markChannels, t0, t2, f0, f2);
			}
			if (swingMouse != null) {
				if (swingMouse.isPopupTrigger()) {
					JPopupMenu menu = getPopupMenuItems(null);
					if (menu != null) {
						consumed = true;
						menu.show(swingDisplay, swingMouse.getX(), swingMouse.getY());
					}
				}
			}
			existingGibbon = null;				
			if (swingDisplay != null) {
				swingDisplay.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}
			gibbonCallProcess.getGibbonOverlayDraw().setMarkedDataUnit(null, tEnd, fEnd);
		}
		
		overlayMark.setHidden(existingGibbon != null);
		return consumed;
	}
	
	/**
	 * Get an appropriate shaped cursor for edge dragging. 
	 * @param edges
	 * @return movement /  drag cursor. 
	 */
	private Cursor selectMovementCursor(int edges) {
		if (edges == MatchedGibbon.TOPBORDER || edges == MatchedGibbon.BOTTOMBORDER) {
			return Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR);
		}
		if (edges == MatchedGibbon.LEFTBORDER || edges == MatchedGibbon.RIGHTBORDER) {
			return Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR);
		}
		if (edges == (MatchedGibbon.TOPBORDER | MatchedGibbon.RIGHTBORDER)){
			return Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR);
		}
		if (edges == (MatchedGibbon.BOTTOMBORDER | MatchedGibbon.LEFTBORDER)){
			return Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR);
		}
		if (edges == (MatchedGibbon.TOPBORDER | MatchedGibbon.LEFTBORDER)){
			return Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR);
		}
		if (edges == (MatchedGibbon.BOTTOMBORDER | MatchedGibbon.RIGHTBORDER)){
			return Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR);
		}
		return Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR);
	}

	/**
	 * Create a gibbon detection
	 * @param t0
	 * @param t2
	 * @param f0
	 * @param f2
	 */
	private void createGibbon(int channels, long t0, long t2, double f0, double f2) {
		GibbonDataUnit gdu = new GibbonDataUnit(t0, channels, 0, 0);
		gdu.setDurationInMilliseconds(t2-t0);
		double[] f = {f0, f2};
		gdu.setFrequency(f);
		gdu.setModel(MANUALNAME);
		
		gdu = GibbonCallDialog.showDialog(gibbonControl.getGuiFrame(), gibbonControl, gdu);
		if (gdu != null) {
			callDataBlock.addPamData(gdu);
			callDataBlock.sortData();
		}
		
	}

	/**
	 * Update a gibbon data unit and update in database.
	 * @param match
	 * @param tEnd last absolute time of drag 
	 * @param fEnd last absolute frequency of drag 
	 */
	private void updateGibbon(MatchedGibbon match, long tEnd, double fEnd) {
		int edges = match.getEdges();
		GibbonDataUnit gibbon = match.getGibbonDataUnit();
		double[] f = gibbon.getFrequency();
		if ((edges & MatchedGibbon.LEFTBORDER) != 0) {
			double duration = gibbon.getDurationInMilliseconds();
			gibbon.setTimeMilliseconds(tEnd);
			gibbon.setDurationInMilliseconds(duration);
		}
		else if ((edges & MatchedGibbon.RIGHTBORDER) != 0) {
			gibbon.setDurationInMilliseconds(Math.max(.1, tEnd-gibbon.getTimeMilliseconds()));
		}
		if ((edges & MatchedGibbon.TOPBORDER) != 0) {
//			System.out.printf("Set top frequency to %s\n", FrequencyFormat.formatFrequency(fEnd, true));
			f[1] = fEnd;
		}
		else if ((edges & MatchedGibbon.BOTTOMBORDER) != 0) {
//			System.out.printf("Set bottom frequency to %s\n", FrequencyFormat.formatFrequency(fEnd, true));
			f[0] = fEnd;
		}
		gibbon.setFrequency(f);
		callDataBlock.updatePamData(gibbon, System.currentTimeMillis());
	}

	/**
	 * Find an existing gibbon call and also which edges the mouse is close to, if any. 
	 * @param overlayMarker 
	 * @param markChannel
	 * @param tMillis
	 * @param fHz
	 * @return
	 */
	private MatchedGibbon findExistingUnit(OverlayMarker overlayMarker, int markChannel, long tMillis, double fHz) {
		// get all data units within a minute - none are that long
		GeneralProjector<PamCoordinate> projector = overlayMarker.getProjector();
		List<HoverData> hovData = projector.getHoverDataList();
		
//		ArrayList<GibbonDataUnit> data = callDataBlock.getDataCopy(tMillis-60000, tMillis+60000, false);
//		for (GibbonDataUnit gibbon : data) {
		/*
		 * Get the list from the projector, since that will be whats been plotted, not
		 * what exists in the datablock. 
		 */
		for (HoverData aHover : hovData) {
			if (aHover.getDataUnit() instanceof GibbonDataUnit == false) {
				continue;
			}
//			int subPlot = aHover.getSubPlotNumber();
//			if (projector instanceof DirectDrawProjector) {
//				subPlot = ((DirectDrawProjector) projector.getp)
//			}
//			projector.getHoveredDataUnit()
			GibbonDataUnit gibbon = (GibbonDataUnit) aHover.getDataUnit();
			if ((gibbon.getChannelBitmap() & 1<<markChannel) == 0) {
				continue;
			}
			Double duration = gibbon.getDurationInMilliseconds();
			double tSpace = 100;
			if (duration != null) {
				tSpace = duration / 10;
			}
			double[] f = gibbon.getFrequency();
			double fSpace = (f[1]-f[0])/10;
			int edges = 0;
			if (tMillis < gibbon.getTimeMilliseconds()-tSpace) {
				continue;
			}
			else {
				edges += (Math.abs(tMillis-gibbon.getTimeMilliseconds())<tSpace) ? MatchedGibbon.LEFTBORDER : 0; 
			}
			if (tMillis > gibbon.getEndTimeInMilliseconds()+tSpace) {
				continue;
			}
			else {
				edges += (Math.abs(tMillis-gibbon.getEndTimeInMilliseconds())<tSpace) ? MatchedGibbon.RIGHTBORDER : 0; 
			}
			if (fHz < f[0]-fSpace) {
				continue;
			}
			else {
				edges += (Math.abs(fHz-f[0]) < fSpace) ? MatchedGibbon.BOTTOMBORDER : 0;
			}
			if (fHz > f[1]+fSpace) {
				continue;
			}
			else {
				edges += (Math.abs(fHz-f[1]) < fSpace) ? MatchedGibbon.TOPBORDER : 0;
			}
			// seems either within or close to the shape, possible close to one or more edges. 
			return new MatchedGibbon(gibbon, edges);
		}
		return null;
	}

	@Override
	public JPopupMenu getPopupMenuItems(DetectionGroupSummary markSummaryData) {
		if (existingGibbon == null) {
			return null;
		}
		
		GibbonDataUnit gibbon = existingGibbon.getGibbonDataUnit();
		boolean isManual = MANUALNAME.equals(gibbon.getModel());
		JPopupMenu pMenu = new JPopupMenu();
		if (isManual) {
			JMenuItem del = new JMenuItem("Delete");
			del.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					deleteGibbon(gibbon);
				}
			});
			pMenu.add(del);
		}
		JMenuItem menuItem = new JMenuItem("Edit ...");
		menuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				editGibbon(gibbon);
			}
		});
		pMenu.add(menuItem);
		return pMenu;
	}

	protected void editGibbon(GibbonDataUnit gibbon) {
		GibbonDataUnit gdu = GibbonCallDialog.showDialog(gibbonControl.getGuiFrame(), gibbonControl, gibbon);
		if (gdu != null) {
			callDataBlock.updatePamData(gdu, System.currentTimeMillis());
		}		
	}

	protected void deleteGibbon(GibbonDataUnit gibbon) {
		String tit = "Delete gibbon call";
		String msg = "Are you sure you want to permanently delete this call from the database ? ";
		int ans = WarnOnce.showWarning(gibbonControl.getGuiFrame(), tit, msg, WarnOnce.OK_CANCEL_OPTION);
		if (ans == WarnOnce.CANCEL_OPTION) {
			return;
		}
		callDataBlock.remove(gibbon, true);
	}

	@Override
	public ParameterType[] getRequiredParameterTypes() {
		return parameterTypes;
	}

	@Override
	public String getObserverName() {
		return gibbonControl.getUnitName();
	}

	@Override
	public MarkDataSelector getMarkDataSelector(OverlayMarker overlayMarker) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getMarkName() {
		return gibbonControl.getUnitName();
	}

	
}
