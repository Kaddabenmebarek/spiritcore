package com.idorsia.research.spirit.core.dto.view;

import java.awt.Color;
import java.io.Serializable;

import com.idorsia.research.spirit.core.constants.StudyActionType;
import com.idorsia.research.spirit.core.dto.AssignmentDto;


public class StudyRemoval implements StudyAction, Serializable {

	private static final long serialVersionUID = -8040272959842345490L;
	AssignmentDto assignment;
	
	public StudyRemoval(AssignmentDto assignment) {
		this.assignment=assignment;
	}
	
	@Override
	public String getName() {
		return "Study Removal";
	}

	@Override
	public int getDuration() {
		return 0;
	}

	@Override
	public void setDuration(int duration) {
	}

	@Override
	public Integer getId() {
		return 0;
	}

	@Override
	public StudyActionType getType() {
		return StudyActionType.REMOVE;
	}

	public AssignmentDto getAssignment() {
		return assignment;
	}
	
	public Color getColor() {
		return assignment.getSubgroup().getGroup().getColor();
	}
}
