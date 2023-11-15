package com.idorsia.research.spirit.core.constants;

public enum ClinicalStatusEnum {
	PRECLINICAL("Preclinical"),
    CLINICAL("Clinical"),
    ;

    private final String description;
    ClinicalStatusEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
