package com.idorsia.research.spirit.core.constants;

import com.idorsia.research.spirit.core.dto.NamedSamplingDto;
import com.idorsia.research.spirit.core.dto.NamedTreatmentDto;
import com.idorsia.research.spirit.core.dto.StageDto;
import com.idorsia.research.spirit.core.dto.view.Disposal;
import com.idorsia.research.spirit.core.dto.view.Measurement;
import com.idorsia.research.spirit.core.dto.view.StudyRemoval;

public enum StudyActionType {
	DISEASE("Disease", NamedTreatmentDto.class, false),
    THERAPY("Therapy", NamedTreatmentDto.class, false),
    SAMPLING("Sampling", NamedSamplingDto.class, false),
    MEASUREMENT("Measurement", Measurement.class, false),
    GROUPASSIGN("Group Assignment", StageDto.class, true),
    DISPOSAL("Disposal", Disposal.class, true),
    REMOVE("Remove", StudyRemoval.class, true),
    ;

    private final boolean singleAction;
    private final Class<?> actionType;
    private final String description;
    StudyActionType(String description, Class<?> actionType, boolean singleAction) {
        this.actionType = actionType;
        this.description = description;
        this.singleAction = singleAction;
    }

    public Class<?> getActionType() {
        return actionType;
    }

    public boolean isSingleAction() {
        return singleAction;
    }

    public String getDescription() {
        return description;
    }
}
