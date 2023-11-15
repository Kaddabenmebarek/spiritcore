package com.idorsia.research.spirit.util;

import org.apache.commons.lang3.time.StopWatch;

public class TimeMonitor {
    private final StopWatch stopWatch = new StopWatch();
    private final TimeReporter timeReporter;
    private final TimeReporter.Section section;

    TimeMonitor(TimeReporter timeReporter, TimeReporter.Section section) {
        this.timeReporter = timeReporter;
        this.section = section;
        stopWatch.start();
    }

    public void timePoint() {
        timePoint("");
    }

    public void timePoint(String optionalDescription) {
        stopWatch.split();
        saveTime(optionalDescription);
    }

    public void timePointAndReset() {
        timePoint("");
        resetTimer();
    }

    public void timePointAndReset(String optionalDescription) {
        timePoint(optionalDescription);
        resetTimer();
    }

    public void resetTimer() {
        stopWatch.reset();
        stopWatch.start();
    }

    private void saveTime(String optionalDescription) {
        timeReporter.reportTime(new TimeMeasurement(section, optionalDescription, stopWatch.getTime()));
    }
}
