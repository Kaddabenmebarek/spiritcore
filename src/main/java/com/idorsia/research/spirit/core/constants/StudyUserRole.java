package com.idorsia.research.spirit.core.constants;

public enum StudyUserRole {
	ADMIN("Admin User"),
    EXPERT("Expert User"),
    BLIND_NAMES("Blind User (Names)"),
    BLIND_ALL("Blind User (All)"),
    ;

    private final String description;
    StudyUserRole(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
