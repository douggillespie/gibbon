package gibbon.swing;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.ListIterator;

import Layout.DisplayPanel;
import Layout.DisplayPanelContainer;
import Layout.DisplayPanelProvider;
import Layout.DisplayProviderList;
import Layout.PamAxis;
import PamController.PamController;
import PamUtils.PamUtils;
import PamView.PamColors;
import PamView.PamColors.PamColor;
import PamguardMVC.PamConstants;
import PamguardMVC.PamDataBlock;
import PamguardMVC.PamDataUnit;
import PamguardMVC.PamObservable;
import PamguardMVC.PamObserver;
import gibbon.GibbonDLProcess;
import gibbon.GibbonResult;
import gibbon.GibbonResultDataBlock;

/**
 * Spec panel provider for Gibbon Results. 
 * @author dg50
 *
 */
public class GibbonSpecPlugin implements DisplayPanelProvider {

	private GibbonDLProcess gibbonDLProcess;
	private GibbonResultDataBlock resultDataBlock;

	public GibbonSpecPlugin(GibbonDLProcess gibbonDLProcess, GibbonResultDataBlock gibbonResultDataBlock) {
		this.gibbonDLProcess = gibbonDLProcess;
		this.resultDataBlock = gibbonResultDataBlock;
		DisplayProviderList.addDisplayPanelProvider(this);
	}

	@Override
	public DisplayPanel createDisplayPanel(DisplayPanelContainer displayPanelContainer) {
		return new GibbonSpecPanel(this, displayPanelContainer);
	}

	@Override
	public String getDisplayPanelName() {
		return "Gibbon Result";
	}
	
	private class GibbonSpecPanel extends DisplayPanel implements PamObserver {
		
		private PamAxis westAxis;

		public GibbonSpecPanel(DisplayPanelProvider displayPanelProvider, DisplayPanelContainer displayPanelContainer) {
			super(displayPanelProvider, displayPanelContainer);
			resultDataBlock.addObserver(this, true);
			westAxis = new PamAxis(0, 1, 0, 1, -10, 10, PamAxis.ABOVE_LEFT, "DL Result", PamAxis.LABEL_NEAR_CENTRE, "%3.1f");
		}

		@Override
		public void destroyPanel() {
			resultDataBlock.deleteObserver(this);
		}
		
		

		@Override
		public void containerNotification(DisplayPanelContainer displayContainer, int noteType) {
//			displayContainer.
		}

		@Override
		public void repaint() {
//			updateImage();
			super.repaint();
		}

		@Override
		public PamAxis getWestAxis() {
			return westAxis;
		}

		@Override
		public long getRequiredDataHistory(PamObservable observable, Object arg) {
			double xDuration = getDisplayPanelContainer().getXDuration();
			return (long) (xDuration * 3);
		}

		@Override
		public void addData(PamObservable observable, PamDataUnit pamDataUnit) {
			repaint();
		}

		private void updateImage() {

			BufferedImage image = getDisplayImage();
			if (image == null) return;
			
			Graphics g = image.getGraphics();

			double currentXPix = getDisplayPanelContainer().getCurrentXPixel();
			long currentXTime = getDisplayPanelContainer().getCurrentXTime();
			double xDuration = getDisplayPanelContainer().getXDuration();
			boolean isWrap = getDisplayPanelContainer().wrapDisplay();
			
			int h = image.getHeight();
			int w = image.getWidth();

			if (PamController.getInstance().getRunMode() == PamController.RUN_PAMVIEW) {
				currentXPix = w;
			}
			
			double xScale = (double) w / xDuration;
//			g.clearRect(0,0,w,h);
			Color fill = Color.white;// PamColors.getInstance().getBorderColour(PamColor.PlOTWINDOW);
			g.setColor(fill);
			g.fillRect(0, 0, w, h);
//			g.setColor(Color.RED);
//			g.drawLine(0, 0, (int) currentXPix, h);
			ArrayList<GibbonResult> copy = resultDataBlock.getDataCopy((long) (currentXTime - 2*xDuration), 
					currentXTime, true);
			if (copy.size() == 0) {
				return;
			}
			ListIterator<GibbonResult> it = copy.listIterator(copy.size()-1);
			int[] lastH = new int[PamConstants.MAX_CHANNELS];
			int[] lastX = new int[PamConstants.MAX_CHANNELS];
			boolean[] notFirst = new boolean[PamConstants.MAX_CHANNELS];
			long minTime = (long) (currentXTime - xDuration);
			while (it.hasPrevious()) {
				GibbonResult resultDU = it.previous();
				long[] times = resultDU.calcResultTimes();
				float[] result = resultDU.getResult();
				int chan = PamUtils.getSingleChannel(resultDU.getChannelBitmap());
				Color col = PamColors.getInstance().getChannelColor(chan);
				g.setColor(col);
				for (int i = result.length - 1; i >= 0; i--) {
					if (times[i] < minTime) {
						break;
					}
					int hPos = (int) westAxis.getPosition(result[i]);
					int xPos = (int) (currentXPix + xScale * (times[i] - currentXTime));
					if (xPos < 0 && isWrap) {
						xPos += w;
//						notFirst[chan] = false;
					}
					if (notFirst[chan] && lastX[chan] >= xPos) {
						g.drawLine(xPos, hPos, lastX[chan], lastH[chan]);
					}
					lastH[chan] = hPos;
					lastX[chan] = xPos;
					notFirst[chan] = true;
				}
			}
		}

		@Override
		public void updateData(PamObservable observable, PamDataUnit pamDataUnit) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void removeObservable(PamObservable observable) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void setSampleRate(float sampleRate, boolean notify) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void noteNewSettings() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public String getObserverName() {
			return "Gibbon Spectrogram Plugin";
		}

		@Override
		public void masterClockUpdate(long milliSeconds, long sampleNumber) {
			repaint();
		}

		@Override
		public PamObserver getObserverObject() {
			return this;
		}

		@Override
		public void receiveSourceNotification(int type, Object object) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public PamDataBlock getViewerDataBlock() {
			return resultDataBlock;
		}

		@Override
		public void prepareImage() {
			updateImage();
		}

	}

}
