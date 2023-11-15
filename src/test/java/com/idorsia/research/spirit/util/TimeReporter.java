package com.idorsia.research.spirit.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.stream.Collectors;

public class TimeReporter {
    private static final TimeReporter instance = new TimeReporter();
    private static final String REPORT_PREFIX = "performanceReportLog";
    private String environment;
    private String reportFileDir;
    private String reportFileName;
    private final HashSet<Section> enabledSections = new HashSet<>();
    private final Semaphore reportFileSemaphore = new Semaphore(1);
    private double minTime = 0.0;

    /**
     * Sections are used to enable or disable different group of time measurements logs.
     */
    public enum Section {
        TEST("Test"),
        DAO("DataAccessObjects");

        String displayName;

        Section(String displayName) {
            this.displayName = displayName;
        }

        @Override
        public String toString() {
            return displayName;
        }
    }

    private TimeReporter() {
        String arguments = System.getProperty("timeReport");
        if (null != arguments) {
            if (arguments.equalsIgnoreCase("true") || arguments.equalsIgnoreCase("all")) {
                enabledSections.clear();
                showAllSections();
            } else if (arguments.isEmpty()) {
                enabledSections.clear();
            } else {
                String[] flags = arguments.split(",");
                hideAllSections();
                for (String flag : flags) {
                    try {
                        Section section = Section.valueOf(flag.toUpperCase());
                        showSectionInLog(section);
                    } catch (IllegalArgumentException e) {
                    	System.out.println("Invalid timeReport argument: \"" + flag + "\"");
                    } catch (NullPointerException e) {
                    	System.out.println(e.getMessage());
                    }
                }
            }
        }

        String timeReportMinArg = System.getProperty("timeReportMin");
        if (null != timeReportMinArg) {
            double minRequestedTime = Double.parseDouble(timeReportMinArg);
            if ( minRequestedTime > 0) {
                minTime = minRequestedTime;
            }
        }
        checkLogDir();
        rotateLogFiles();
    }

    private String getLogDir(){
        if(reportFileDir == null || reportFileDir.isEmpty()){
            reportFileDir = REPORT_PREFIX + File.separator + getEnvironment();
        }
        return reportFileDir;
    }

    private void checkLogDir(){
        File file = new File(getLogDir());
        if(!file.exists()){
            file.mkdir();
        }
    }

    private void rotateLogFiles() {
        String DATE_FORMAT = "yyyy-MM-dd_HH-mm-ss";
        reportFileName = REPORT_PREFIX + "-" + getEnvironment() + "-" + new SimpleDateFormat(DATE_FORMAT).format(new Date()) + ".log";
    }

    public void showAllSections() {
        enabledSections.addAll(Arrays.asList(Section.values()));
    }

    public void hideAllSections() {
        enabledSections.clear();
    }

    public void showSectionInLog(Section section) {
        enabledSections.add(section);
    }

    public void hideSectionFromLog(Section section) {
        enabledSections.remove(section);
    }

    public TimeMonitor newTimeMonitor(Section section) {
        return new TimeMonitor(this, section);
    }

    @SuppressWarnings("unused")
	public synchronized void reportTime(TimeMeasurement timeMeasurement) {
        if (!enabledSections.isEmpty()) {
            if (enabledSections.contains(timeMeasurement.getSection())) {
                if (timeMeasurement.timeInMs >= minTime) {
                    // Get the stack trace from this thread and only if the section is enabled
                    String stackFrameString = getStackFrameString();
                    Executors.newSingleThreadExecutor().submit((Callable<Void>) () -> {
                        try {
                            reportFileSemaphore.acquire();
                            System.out.println(buildLine(timeMeasurement, stackFrameString, true));
                            File file = new File(reportFileDir + File.separator + reportFileName);
                            boolean newFile = !file.exists();
                            FileWriter fw;
                            try {
                                fw = new FileWriter(file, true);
                            } catch (FileNotFoundException fnfe){
                                file = new File(reportFileName);
                                fw = new FileWriter(file, true);
                            }
                            if(fw == null) return null;
                            BufferedWriter reportWriter = new BufferedWriter(fw);
                            if(newFile){
                                reportWriter.write("Environment: " + getEnvironment());
                                reportWriter.newLine();
                            }
                            reportWriter.write(buildLine(timeMeasurement, stackFrameString, true));
                            reportWriter.newLine();
                            reportWriter.close();
                            reportFileSemaphore.release();
                        } catch (Exception e) {
                        	System.out.println("Could not write timing report to " + reportFileName + ", got: " + e.getMessage());
                        }
                        return null;
                    });
                }
            }
        }
    }

    private String getEnvironment(){
        if(environment == null || environment.isEmpty()) {
            if ("true".equalsIgnoreCase(System.getProperty("production"))) {
                environment = "prod";
            } else if ("true".equalsIgnoreCase(System.getProperty("development"))) {
                environment = "dev";
            } else {
                environment = "test";
            }
        }
        return environment;
    }

    private String buildLine(TimeMeasurement timeMeasurement, String caller, boolean padSectionWithSpaces) {
        return timeMeasurement.getSectionTimeString(padSectionWithSpaces)
                + caller
                + timeMeasurement.getOptionalText();
    }

    private String getStackFrameString() {
        List<StackWalker.StackFrame> stackFrames = getLastStackFrame();
        String caller = "???.??? ";
        if (!stackFrames.isEmpty()) {
            StackWalker.StackFrame stackFrame = stackFrames.get(0);
            caller = stackFrame.getClassName() + "." + stackFrame.getMethodName()
                    + " (l:" + stackFrame.getLineNumber() + ") ";
        }
        return caller;
    }

    private List<StackWalker.StackFrame> getLastStackFrame() {
        return StackHelper.getStackFrames()
                .stream()
                .filter(frame -> !frame.getClassName().contains(TimeMonitor.class.getSimpleName()) &&
                        !frame.getClassName().contains(TimeReporter.class.getSimpleName()))
                .collect(Collectors.toList());
    }

    public static TimeReporter getInstance() {
        return instance;
    }
}
