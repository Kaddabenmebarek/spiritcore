package com.idorsia.research.spirit.core.dto.view;

import java.io.Serializable;
import java.util.ArrayList;

import com.idorsia.research.spirit.core.dto.AssignmentDto;
import com.idorsia.research.spirit.core.dto.BiosampleDto;

public class Participant implements Comparable<Participant>, Serializable{

	private static final long serialVersionUID = 2260877159006151398L;
	private BiosampleDto biosample;
    private ArrayList<AssignmentDto> assignmentSeries = new ArrayList<>();

    @Override
    public int compareTo(Participant o) {
        if (equals(o)) return 0;
        int c = this.getAssignmentSeries().get(0).getStage().compareTo(o.getAssignmentSeries().get(0).getStage());
        if(c!=0) return c;
        c = this.getAssignmentSeries().get(0).getNo() - o.getAssignmentSeries().get(0).getNo();
        return c;

    }

    public BiosampleDto getBiosample() {
        return biosample;
    }

    public void setBiosample(BiosampleDto biosample) {
        this.biosample = biosample;
    }

    public ArrayList<AssignmentDto> getAssignmentSeries() {
        return assignmentSeries;
    }

    public void setAssignmentSeries(ArrayList<AssignmentDto> assignmentSeries) {
        this.assignmentSeries = assignmentSeries;
    }
}
