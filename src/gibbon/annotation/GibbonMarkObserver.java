package gibbon.annotation;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JPopupMenu;

import PamUtils.PamCalendar;
import PamUtils.PamUtils;
import PamView.GeneralProjector.ParameterType;
import PamView.paneloverlay.overlaymark.ExtMouseAdapter;
import PamView.paneloverlay.overlaymark.MarkDataSelector;
import PamView.paneloverlay.overlaymark.OverlayMark;
import PamView.paneloverlay.overlaymark.OverlayMarkObserver;
import PamView.paneloverlay.overlaymark.OverlayMarker;
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
		int markChannels = overlayMark.getMarkChannels();
		int markChannel = PamUtils.getLowestChannel(markChannels);
		long t0 = (long) overlayMark.getCoordinate(0).getCoordinate(0);
		double f0 =  overlayMark.getCoordinate(0).getCoordinate(1);
		long t2 = (long) overlayMark.getLastCoordinate().getCoordinate(0);
		double f2 = overlayMark.getLastCoordinate().getCoordinate(1);
		MouseEvent swingMouse = ExtMouseAdapter.swingMouse(mouseEvent);
		Component swingDisplay = swingMouse.getComponent();
		if (markStatus == MARK_START) {
			existingGibbon = findExistingUnit(markChannel, t0, f0);
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
				updateGibbon(existingGibbon, dt, df);
			}
			else if (t0!=t2 && f0!=f2){
				createGibbon(markChannels, t0, t2, f0, f2);
			}
			existingGibbon = null;				
			if (swingDisplay != null) {
				swingDisplay.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}
			gibbonCallProcess.getGibbonOverlayDraw().setMarkedDataUnit(null, tEnd, fEnd);
		}
		overlayMark.setHidden(existingGibbon != null);
		return true;
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
		gdu.setModel("Manual selection");
		
		gdu = GibbonCallDialog.showDialog(gibbonControl.getGuiFrame(), gibbonControl, gdu);
		if (gdu != null) {
			callDataBlock.addPamData(gdu);
		}
		
	}

	/**
	 * Update a gibbon data unit and update in database.
	 * @param match
	 * @param deltaT
	 * @param deltaF
	 */
	private void updateGibbon(MatchedGibbon match, long deltaT, double deltaF) {
		int edges = match.getEdges();
		GibbonDataUnit gibbon = match.getGibbonDataUnit();
		double[] f = gibbon.getFrequency();
		if ((edges & MatchedGibbon.LEFTBORDER) != 0) {
			gibbon.setTimeMilliseconds(gibbon.getTimeMilliseconds() + deltaT);
			gibbon.setDurationInMilliseconds(gibbon.getDurationInMilliseconds() - deltaT);
		}
		if ((edges & MatchedGibbon.RIGHTBORDER) != 0) {
			gibbon.setDurationInMilliseconds(gibbon.getDurationInMilliseconds() + deltaT);
		}
		if ((edges & MatchedGibbon.TOPBORDER) != 0) {
			f[1] += deltaF;
		}
		if ((edges & MatchedGibbon.BOTTOMBORDER) != 0) {
			f[0] += deltaF;
		}
		gibbon.setFrequency(f);
		callDataBlock.updatePamData(gibbon, System.currentTimeMillis());
	}

	/**
	 * Find an existing gibbon call and also which edges the mouse is close to, if any. 
	 * @param markChannel
	 * @param tMillis
	 * @param fHz
	 * @return
	 */
	private MatchedGibbon findExistingUnit(int markChannel, long tMillis, double fHz) {
		// get all data units within a minute - none are that long
		ArrayList<GibbonDataUnit> data = callDataBlock.getDataCopy(tMillis-60000, tMillis+60000, false);
		for (GibbonDataUnit gibbon : data) {
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
		// TODO Auto-generated method stub
		return null;
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
