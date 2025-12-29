package gibbon.swing;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Point2D;

import PamUtils.Coordinate3d;
import PamView.GeneralProjector;
import PamView.PamDetectionOverlayGraphics;
import PamView.PamSymbol;
import PamView.PamSymbolType;
import PamView.GeneralProjector.ParameterType;
import PamguardMVC.PamDataBlock;
import gibbon.GibbonControl;
import gibbon.GibbonDataBlock;
import gibbon.GibbonDataUnit;
import gibbon.annotation.MatchedGibbon;

public class GibbonOverlayDraw extends PamDetectionOverlayGraphics {

	private static PamSymbol defaultSymbol = new PamSymbol(PamSymbolType.SYMBOL_SQUARE, 10, 10, false, Color.WHITE, Color.RED);
	private GibbonControl gibbonControl;
	private GibbonDataBlock gibbonDataBlock;
	private MatchedGibbon matchedGibbon;
	private long tDrag;
	private double fDrag;
	
	public GibbonOverlayDraw(GibbonControl gibbonControl, GibbonDataBlock gibbonDataBlock) {
		super(gibbonDataBlock, defaultSymbol);
		this.gibbonControl = gibbonControl;
		this.gibbonDataBlock = gibbonDataBlock;
	}

	@Override
	public boolean preDrawAnything(Graphics g, PamDataBlock pamDataBlock, GeneralProjector projector) {
		if (projector.getParmeterType(0) == ParameterType.TIME
				&& projector.getParmeterType(1) == ParameterType.FREQUENCY) {
			if (matchedGibbon != null) {
				preDrawSpectrogram(g, projector);
			}
		}
		return true;
	}
	
	private void preDrawSpectrogram(Graphics g, GeneralProjector projector) {
		GibbonDataUnit gibbon = matchedGibbon.getGibbonDataUnit();
		long t1 = gibbon.getTimeMilliseconds();
		long t2 = gibbon.getEndTimeInMilliseconds();
		double[] f = gibbon.getFrequency();
		int edges = matchedGibbon.getEdges();
		if ((edges & MatchedGibbon.LEFTBORDER) != 0) {
			t1 = tDrag;
		}
		if ((edges & MatchedGibbon.RIGHTBORDER) != 0) {
			t2 = tDrag;
		}
		if ((edges & MatchedGibbon.TOPBORDER) != 0) {
			f[1] = fDrag;
		}
		if ((edges & MatchedGibbon.BOTTOMBORDER) != 0) {
			f[0] = fDrag;
		}
		Coordinate3d topLeft = projector.getCoord3d(t1, f[1], 0);
		Coordinate3d botRight = projector.getCoord3d(t2, f[0], 0);
		Point2D start = topLeft.getXYPoint();
		int x = (int) Math.round(start.getX());
		int y = (int) Math.round(start.getY());
		int w = (int) (botRight.x - x);
		int h = (int) (botRight.y - y);
		
		Graphics2D g2d = (Graphics2D) g; 
		g.setColor(Color.RED);
		float[] dash = {3, 5};
		PamSymbol square = new PamSymbol(PamSymbolType.SYMBOL_SQUARE, 7, 7, false, Color.BLACK, Color.RED);
		 BasicStroke stroke = new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1, dash, 0);
		 g2d.setStroke(stroke);
		g.drawRect(x, y,  w,  h);
		square.draw(g2d, new Point(x, y+h/2));
		square.draw(g2d, new Point(x+w, y+h/2));
		square.draw(g2d, new Point(x+w/2, y));
		square.draw(g2d, new Point(x+w/2, y+h));
	}

	public void setMarkedDataUnit(MatchedGibbon matchedGibbon, long tDrag, double fDrag) {
		this.matchedGibbon = matchedGibbon;
		this.tDrag = tDrag;
		this.fDrag = fDrag;
	}

}
