package gibbon.annotation;

import annotation.AnnotationDialogPanel;
import annotation.AnnotationSettingsPanel;
import annotation.DataAnnotationType;
import generalDatabase.SQLLoggingAddon;
import gibbon.GibbonControl;
import gibbon.GibbonDataUnit;

public class GibbonAnnotationType extends DataAnnotationType<GibbonAnnotation> {

	private GibbonControl gibbonControl;

	public GibbonAnnotationType(GibbonControl gibbonControl) {
		this.gibbonControl = gibbonControl;
	}

	@Override
	public String getAnnotationName() {
		return "Gibbon Info";
	}

	@Override
	public Class getAnnotationClass() {
		return GibbonAnnotationType.class;
	}

	@Override
	public boolean canAnnotate(Class dataUnitType) {
		return (GibbonDataUnit.class.isAssignableFrom(dataUnitType));
	}

	@Override
	public boolean canAutoAnnotate() {
		return false;
	}

	@Override
	public SQLLoggingAddon getSQLLoggingAddon() {
		return null;
	}

	@Override
	public AnnotationDialogPanel getDialogPanel() {
		// TODO Auto-generated method stub
		return super.getDialogPanel();
	}

	@Override
	public AnnotationSettingsPanel getSettingsPanel() {
		return null;
	}

	@Override
	public boolean hasSettingsPanel() {
		return false;
	}

}
