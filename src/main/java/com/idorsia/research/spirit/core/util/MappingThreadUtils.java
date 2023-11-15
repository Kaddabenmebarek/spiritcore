package com.idorsia.research.spirit.core.util;

import java.util.ArrayList;
import java.util.List;

import com.idorsia.research.spirit.core.constants.Constants;

public class MappingThreadUtils {
	
	private static List<Thread> activeThreads=new ArrayList<>();

	public synchronized static void addThread(Thread thread) {
		activeThreads.add(thread);
	}
	
	public synchronized static void removeThread(Thread thread) {
		activeThreads.remove(thread);
	}
	
	public static boolean threadIsOver() {
		return activeThreads.size()==0;
	}

	public static void waitProcess() {
		while(!MappingThreadUtils.threadIsOver()) {
			try {
				Thread.sleep(300);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
	}

	//Limit number of thread (more or less, should not be synchronized !)
	public static void start(Thread t) {
		while(activeThreads.size()>Constants.MAX_THREAD) {
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		addThread(t);
		t.start();
	}

}
