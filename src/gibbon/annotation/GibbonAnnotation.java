package gibbon.annotation;

import annotation.DataAnnotation;
import annotation.DataAnnotationType;

public class GibbonAnnotation extends DataAnnotation<GibbonAnnotationType> {

	private String callType;
	
	private int confidence;
	
	private String comment;
	
	public GibbonAnnotation(GibbonAnnotationType dataAnnotationType) {
		super(dataAnnotationType);
	}

}
