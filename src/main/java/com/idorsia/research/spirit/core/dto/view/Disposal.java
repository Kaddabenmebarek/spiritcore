package com.idorsia.research.spirit.core.dto.view;

import java.awt.Color;
import java.io.Serializable;
import java.util.Objects;

import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.constants.StudyActionType;
import com.idorsia.research.spirit.core.dto.NamedSamplingDto;

public class Disposal implements StudyAction, Serializable {
	private static final long serialVersionUID = -5042465950699169124L;
	private NamedSamplingDto sampling;
    private String comment;

    // This is an action changing the study design, please DO NOT create on outside of a validated method (setEndPhase)
    public Disposal() {
    }

    public Disposal(NamedSamplingDto sampling) {
        setSampling(sampling);
    }

    public Disposal(Disposal disposal) {
    	this.comment=disposal.getComment();
	}

	@Override
    public String getName() {
        return "Necropsy";
    }

    @Override
    public int getDuration() {
        return getSampling() == null ? 1200 : 60;
    }

    @Override
    public void setDuration(int duration) {

    }

    @Override
    public Integer getId() {
        return getSampling() == null ? Constants.NEWTRANSIENTID : getSampling().getId();
    }

    @Override
    public StudyActionType getType() {
        return StudyActionType.DISPOSAL;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSampling(), getComment());
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Disposal)) return false;
        return
                Objects.equals(this.getSampling(), getSampling())
                && Objects.equals(this.getComment(), getComment());
    }

    public void setSampling(NamedSamplingDto terminationSampling) {
        sampling = terminationSampling;
    }

    public NamedSamplingDto getSampling() {
        return sampling;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Color getColor(){return Color.RED;}

    @Override
    public String toString() {
        return "Disposal" +
                (sampling == null ? "" : " with " + sampling) +
                (comment == null ? "" : ":" + comment);
    }
}
