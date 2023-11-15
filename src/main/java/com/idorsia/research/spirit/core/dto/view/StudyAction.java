package com.idorsia.research.spirit.core.dto.view;

import java.awt.Color;

import com.idorsia.research.spirit.core.constants.StudyActionType;

public interface StudyAction {
	String getName();
    int getDuration();
    void setDuration(int duration);
    Integer getId();
    StudyActionType getType();
	Color getColor();
}
