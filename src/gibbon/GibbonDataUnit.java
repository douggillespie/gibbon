package gibbon;

import java.util.Arrays;

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
	 * Time it was last viewed. Will be populated whenever the
	 * data unit is drawn since I can't see another way of working out that
	 * something has displayed it. 
	 */
	private Long viewedAt;
	
	/**
	 * Flag for auto detection. not really needed since this information 
	 * is recorded elsewhere, but helpful nonetheless.  
	 */
	private boolean autoDetection;
	
	/**
	 * Times and frequencies of automatic detection information. will 
	 * be null for manual detections. 
	 */
	private Long autoTimeMillis;
	
	private Double autoDuration;
	
	private double[] autoFrequency;
	
	private Double snr;
	
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
		if (snr != null) {
			str += String.format("<br>SNR: %3.1fdB", snr); 
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
		str += String.format("<br>Best score: %3.2f", bestScore);
		
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

	/**
	 * @return the viewedAt
	 */
	public Long getViewedAt() {
		return viewedAt;
	}

	/**
	 * @param viewedAt the viewedAt to set
	 */
	public void setViewedAt(Long viewedAt) {
		this.viewedAt = viewedAt;
	}

	/**
	 * @return the autoDetection
	 */
	public boolean isAutoDetection() {
		return autoDetection;
	}

	/**
	 * @param autoDetection the autoDetection to set
	 */
	public void setAutoDetection(boolean autoDetection) {
		this.autoDetection = autoDetection;
	}

	/**
	 * @return the autoTimeMillis
	 */
	public Long getAutoTimeMillis() {
		return autoTimeMillis;
	}

	/**
	 * @param autoTimeMillis the autoTimeMillis to set
	 */
	public void setAutoTimeMillis(Long autoTimeMillis) {
		this.autoTimeMillis = autoTimeMillis;
	}

	/**
	 * @return the autoDuration
	 */
	public Double getAutoDuration() {
		return autoDuration;
	}

	/**
	 * @param autoDuration the autoDuration to set
	 */
	public void setAutoDuration(Double autoDuration) {
		this.autoDuration = autoDuration;
	}

	/**
	 * @return the autoFrequency
	 */
	public double[] getAutoFrequency() {
		return autoFrequency;
	}

	/**
	 * @param autoFrequency the autoFrequency to set
	 */
	public void setAutoFrequency(double[] autoFrequency) {
		this.autoFrequency = autoFrequency;
	}

	/**
	 * Called when an auto detection is created and copies the basic
	 * time and frequency information so that it gets saved in addtional 
	 * database columns. 
	 */
	public void copyAutoInfo() {
		this.autoDetection = true;
		this.autoTimeMillis = getTimeMilliseconds();
		this.autoDuration = getDurationInMilliseconds();
		this.autoFrequency = Arrays.copyOf(getFrequency(), 2); // make sure it's copied so manual editing doesn't overwrite it. 
	}

	/**
	 * @return the SNR
	 */
	public Double getSNR() {
		return snr;
	}

	/**
	 * @param snr the SNR to set
	 */
	public void setSNR(Double snr) {
		this.snr = snr;
	}

}
