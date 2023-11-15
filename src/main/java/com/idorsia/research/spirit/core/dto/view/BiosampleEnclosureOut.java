package com.idorsia.research.spirit.core.dto.view;

import java.awt.Color;
import java.io.Serializable;

import com.idorsia.research.spirit.core.constants.StudyActionType;


public class BiosampleEnclosureOut implements StudyAction, Serializable {

	private static final long serialVersionUID = 3040905936938140761L;
	private Integer id;
	
    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public String getName() {
        return "Move in";
    }

    @Override
    public int getDuration() {
        return 0;
    }

    @Override
    public void setDuration(int duration) {
    }

    @Override
    public StudyActionType getType() {
        return null;
    }

    @Override
    public Color getColor() {
        return null;
    }
}
