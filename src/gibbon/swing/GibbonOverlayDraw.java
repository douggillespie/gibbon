package gibbon.swing;

import java.awt.Color;

import PamView.PamDetectionOverlayGraphics;
import PamView.PamSymbol;
import PamView.PamSymbolType;
import PamguardMVC.PamDataBlock;
import gibbon.GibbonControl;
import gibbon.GibbonDataBlock;

public class GibbonOverlayDraw extends PamDetectionOverlayGraphics {

	private static PamSymbol defaultSymbol = new PamSymbol(PamSymbolType.SYMBOL_SQUARE, 10, 10, false, Color.WHITE, Color.RED);
	private GibbonControl gibbonControl;
	private GibbonDataBlock gibbonDataBlock;
	
	public GibbonOverlayDraw(GibbonControl gibbonControl, GibbonDataBlock gibbonDataBlock) {
		super(gibbonDataBlock, defaultSymbol);
		this.gibbonControl = gibbonControl;
		this.gibbonDataBlock = gibbonDataBlock;
	}

}
