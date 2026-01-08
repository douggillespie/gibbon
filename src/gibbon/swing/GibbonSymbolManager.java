package gibbon.swing;

import java.awt.Color;

import PamView.PamSymbolType;
import PamView.symbol.StandardSymbolManager;
import PamView.symbol.SymbolData;
import PamguardMVC.PamDataBlock;

public class GibbonSymbolManager extends StandardSymbolManager {
	
	private static SymbolData defaultSymbol = new SymbolData(PamSymbolType.SYMBOL_DIAMOND, 10, 10, false, Color.BLUE, Color.RED);
	
	public GibbonSymbolManager(PamDataBlock pamDataBlock) {
		super(pamDataBlock, defaultSymbol);
	}

}
