package com.idorsia.research.spirit.core.constants;

public enum FrequencyType {
    DAILY("DAILY","Daily"),
    WEEKLY("WEEKLY","Weekly"),
    MONTHLY("MONTHLY","Monthly"),
    YEARLY("YEARLY","Yearly"),
    ;

    private final String name;
    private final String description;
    
    FrequencyType(String name, String description) {
    	this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
