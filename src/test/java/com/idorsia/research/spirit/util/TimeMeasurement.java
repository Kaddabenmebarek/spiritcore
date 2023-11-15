package com.idorsia.research.spirit.util;

public class TimeMeasurement {
    TimeReporter.Section section;
    String optionalText;
    Long timeInMs;

    public TimeMeasurement(TimeReporter.Section section,
                           String optionalText,
                           Long timeInMs) {
        this.section = section;
        this.optionalText = optionalText;
        this.timeInMs = timeInMs;
    }

    public String getSectionTimeString(boolean padSectionWithSpace) {
        String start;
        if (padSectionWithSpace) {
            start = String.format("%20s", section) + " "
                    + String.format("%6s", timeInMs) + " ms in ";
        } else {
            start = section + " " + timeInMs + " ms in ";
        }
        return start;
    }

    public String getOptionalText() {
        return optionalText;
    }

    public TimeReporter.Section getSection() {
        return section;
    }
}
