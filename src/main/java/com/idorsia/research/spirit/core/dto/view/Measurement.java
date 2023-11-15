package com.idorsia.research.spirit.core.dto.view;

import java.awt.Color;
import java.io.Serializable;

import com.actelion.research.util.CompareUtils;
import com.actelion.research.util.ui.UIUtils;
import com.idorsia.research.spirit.core.conf.ContextShare;
import com.idorsia.research.spirit.core.constants.StudyActionType;
import com.idorsia.research.spirit.core.dto.AssayDto;
import com.idorsia.research.spirit.core.service.AssayService;
import com.idorsia.research.spirit.core.util.MiscUtils;


public class Measurement implements StudyAction, Comparable<Measurement>, Serializable {

	private static final long serialVersionUID = -8704377724910930008L;
	private int testId;
	private String[] parameters;
	private transient AssayDto assay;
	private int duration = 60;

	@Override
	public int getDuration() {
		return duration;
	}

	@Override
	public void setDuration(int duration) {
		this.duration = duration;
	}

	@Override
	public Integer getId() {
		return assay.getId();
	}

	@Override
	public StudyActionType getType() {
		return StudyActionType.MEASUREMENT;
	}


	public Measurement(int testId, String[] parameters) {
		this.testId = testId;
		this.parameters = parameters;
	}

	public Measurement(AssayDto test) {
		this(test, new String[0]);
	}

	public Measurement(AssayDto test, String[] parameters) {
		assert test!=null;
		this.assay = test;
		this.testId = test.getId();
		setParameters(parameters);
	}

	public Measurement(Measurement measurement) {
		setParameters(measurement.getParameters());
		this.testId=measurement.getTestId();
		this.assay=measurement.getAssay();
	}

	@Override
	public String getName() {
		return assay.getName();
	}

	public int getTestId() {
		return testId;
	}

	public String[] getParameters() {
		return parameters;
	}

	public void setParameters(String[] parameters) {
		this.parameters = parameters;
	}

	public AssayDto getAssay() {
		if(assay == null) {
			assay = ((AssayService)ContextShare.getContext().getBean("assayService")).getAssayDto(testId);
		}
		return assay;
	}
	public void setAssay(AssayDto test) {
		this.assay = test;
	}

	@Override
	public int hashCode() {
		return testId;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof Measurement)) return false;
		Measurement p = (Measurement) obj;
		if(testId>0 && testId!=p.testId) return false;
		if(testId<=0 && assay!=null && !assay.equals(p.assay)) return false;
		return CompareUtils.compare(parameters, p.parameters)==0;
	}

	@Override
	public int compareTo(Measurement o) {
		if(o==null) return -1;
		int c = CompareUtils.compare(testId, o.testId);
		if(c!=0) return c;
		return CompareUtils.compare(parameters, o.parameters);
	}

	@Override
	public String toString() {
		return ("EM"+getTestId()) + (getParameters()!=null && getParameters().length>0? ": "+MiscUtils.unsplit(getParameters(), ", "): "");
	}
	
	public Color getColor() {
		return UIUtils.getColor(0, 60, 220);
	}
}
