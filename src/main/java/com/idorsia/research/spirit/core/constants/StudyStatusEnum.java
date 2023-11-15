package com.idorsia.research.spirit.core.constants;

public enum StudyStatusEnum {
	EXAMPLE("Example"),
    TEST("Test"),
    ONGOING("Ongoing"),
    IRBR("Irbr"),
    PUBLISHED("Published"),
    STOP("Stop"),
    ;

    private final String description;
    StudyStatusEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
