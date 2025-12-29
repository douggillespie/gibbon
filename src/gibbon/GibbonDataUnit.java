package gibbon;

import PamDetection.PamDetection;
import PamguardMVC.DataUnitBaseData;
import PamguardMVC.PamDataUnit;

public class GibbonDataUnit extends PamDataUnit implements PamDetection {

	/*
	 * Peak score from AI model detector. 
	 */
	private float bestScore; 
	
	/**
	 * Name of detection model, OR "Manual Detection" for human edited. 
	 * This will be set when the unit is created and cannot be edited. 
	 */
	private String model;
	
	/**
	 * Some bookkeeping of edits made after initial creation. Useful
	 * if limits of auto detections get changed. 
	 */
	private String manualEdit;
	
	/**
	 * Call type, can be set for manual or automatic detections from 
	 * a fixed list rather than a lookup 
	 */
	private String callType;
	
	/**
	 * confidence score for manual detections, can also be added to automatic ones. 
	 */
	private int confidence; 
	
	/**
	 * Free form comment. 
	 */
	private String comment; 
	
	public GibbonDataUnit(long timeMilliseconds) {
		super(timeMilliseconds);
		// TODO Auto-generated constructor stub
	}

	public GibbonDataUnit(long timeMilliseconds, int channelBitmap, long startSample, long durationSamples) {
		super(timeMilliseconds, channelBitmap, startSample, durationSamples);
		// TODO Auto-generated constructor stub
	}

	public GibbonDataUnit(DataUnitBaseData basicData) {
		super(basicData);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getSummaryString() {
		String str = super.getSummaryString();
		if (str.endsWith("<br>"));
		str = str.substring(0, str.length()-4);
		if (callType != null) {
			str += "<br>Call Type: " + callType;
		}
		if (model != null) {
			str += "<br>Model: " + model; 
		}
		if (comment != null) {
			str += "<br>Comment: " + comment;
		}
		if (manualEdit != null) {
			str += "<br>Edits: " + manualEdit;
		}
		
		return str;
	}

	/**
	 * Put in a potential best score value. The maximum will be kept
	 * @param bestScore
	 */
	public void addBestScore(float bestScore) {
		bestScore = Math.max(this.bestScore, bestScore);
	}
	
	/**
	 * @return the bestScore
	 */
	public float getBestScore() {
		return bestScore;
	}

	/**
	 * @param bestScore the bestScore to set
	 */
	public void setBestScore(float bestScore) {
		this.bestScore = bestScore;
	}

	/**
	 * @return the model
	 */
	public String getModel() {
		return model;
	}

	/**
	 * @param model the model to set
	 */
	public void setModel(String model) {
		this.model = model;
	}

	/**
	 * @return the manualEdit
	 */
	public String getManualEdit() {
		return manualEdit;
	}

	/**
	 * @param manualEdit the manualEdit to set
	 */
	public void setManualEdit(String manualEdit) {
		this.manualEdit = manualEdit;
	}

	/**
	 * @return the callType
	 */
	public String getCallType() {
		return callType;
	}

	/**
	 * @param callType the callType to set
	 */
	public void setCallType(String callType) {
		this.callType = callType;
	}

	/**
	 * @return the confidence
	 */
	public int getConfidence() {
		return confidence;
	}

	/**
	 * @param confidence the confidence to set
	 */
	public void setConfidence(int confidence) {
		this.confidence = confidence;
	}

	/**
	 * @return the comment
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * @param comment the comment to set
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}

}
