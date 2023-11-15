package com.idorsia.research.spirit.util;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import static java.lang.StackWalker.Option.RETAIN_CLASS_REFERENCE;

public class StackHelper {

    static public List<StackWalker.StackFrame> getStackFrames() {
        return StackWalker.getInstance(RETAIN_CLASS_REFERENCE).walk(StackHelper::listStackFrames);
    }

    static public List<StackWalker.StackFrame> listStackFrames(Stream<StackWalker.StackFrame> stackFrameStream) {
        return stackFrameStream
                .filter(frame -> frame.getClassName().contains("com.actelion.research") &&
                        !frame.getClassName().contains("StackHelper"))
                .collect(Collectors.toList());
    }

    static public void logCallers(int levels) {
        if (levels > 0) {
            List<StackWalker.StackFrame> stackFrames = getStackFrames();
            String IDENT = "  ";
            int currentLevel = 0;
            for (StackWalker.StackFrame stackFrame : stackFrames) {
                String prefix = "";
                if (currentLevel > 0) {
                    prefix = IDENT.repeat(currentLevel)
                            + "L> ";
                }
                System.out.println(prefix
                        + stackFrame.getClassName() + "." + stackFrame.getMethodName()
                        + " (l: " + stackFrame.getLineNumber() + ") ");
                currentLevel++;
                if (currentLevel == levels){
                    break;
                }
            }
        }
    }
}
