package gibbon.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;

import PamguardMVC.PamDataUnit;
import binaryFileStorage.BinaryDataSource;
import binaryFileStorage.BinaryHeader;
import binaryFileStorage.BinaryObjectData;
import binaryFileStorage.ModuleFooter;
import binaryFileStorage.ModuleHeader;
import gibbon.GibbonResult;
import gibbon.GibbonResultDataBlock;

/**
 * Binary data for gibbon results. 
 * @author dg50
 *
 */
public class GibbonResultBinary extends BinaryDataSource {

	public GibbonResultBinary(GibbonResultDataBlock sisterDataBlock) {
		super(sisterDataBlock);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getStreamName() {
		return "Gibbon Results";
	}

	@Override
	public int getStreamVersion() {
		return 0;
	}

	@Override
	public int getModuleVersion() {
		return 0;
	}

	@Override
	public byte[] getModuleHeaderData() {		
		return null;
	}

	@Override
	public PamDataUnit sinkData(BinaryObjectData binaryObjectData, BinaryHeader bh, int moduleVersion) {
		DataInputStream dis = new DataInputStream(new ByteArrayInputStream(binaryObjectData.getData()));
		float[] result = null;
		try {
			int nR = dis.readShort();
			result = new float[nR];
			for (int i = 0; i < nR; i++) {
				result[i] = dis.readFloat();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		GibbonResult gibbonResult = new GibbonResult(binaryObjectData.getDataUnitBaseData(), result);
		return gibbonResult;
	}

	@Override
	public ModuleHeader sinkModuleHeader(BinaryObjectData binaryObjectData, BinaryHeader bh) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ModuleFooter sinkModuleFooter(BinaryObjectData binaryObjectData, BinaryHeader bh,
			ModuleHeader moduleHeader) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BinaryObjectData getPackedData(PamDataUnit pamDataUnit) {
		GibbonResult gibbonResult = (GibbonResult) pamDataUnit;
		float[] result = gibbonResult.getResult();
		if (result == null) {
			return null;
		}
		ByteArrayOutputStream bos;
		DataOutputStream dos = new DataOutputStream(bos = new ByteArrayOutputStream(Float.BYTES*result.length + Short.BYTES));
		try {
			dos.writeShort((short) result.length);
			for (int i = 0; i < result.length; i++) {
				dos.writeFloat(result[i]);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		BinaryObjectData bod = new BinaryObjectData(0, bos.toByteArray());
		return bod;
	}

	@Override
	public void newFileOpened(File outputFile) {
		// TODO Auto-generated method stub
		
	}

}
