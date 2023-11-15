package com.idorsia.research.spirit.core.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("rawtypes")
public class Cache {

	public static final int FAST = 15;
	public static final int MEDIUM = 60;
	public static final int LONG = 300;

	private static class CachedObject {
		public CachedObject(Object obj, long expire) {
			this.object = obj;
			this.expire = expire;
		}
		public long expire;
		public Object object;
	}

	private final static Cache instance = new Cache();
	private final Map<String, CachedObject> cache = new ConcurrentHashMap<>();

	static {
		Thread cleaningThread = new Thread() {
			@Override
			public void run() {
				while(!interrupted()) {
					try {Thread.sleep(60000);} catch (Exception e) {e.printStackTrace();}
					instance.clean();

				}
			}
		};
		cleaningThread.setDaemon(true);
		cleaningThread.start();
	}

	public static Cache getInstance() {
		return instance;
	}

	public void clear() {
		cache.clear();
	}

	public void clean() {
		synchronized (instance) {
			List<String> remove = new ArrayList<>();
			long time = System.currentTimeMillis();
			for(String name : cache.keySet()) {
				long expiry = cache.get(name).expire;
				if(expiry>0 && expiry<time) remove.add(name);
			}
			for(String s: remove) cache.remove(s);
		}
	}

	private Cache() {}

	public void add(String name, Object obj) {
		add(name, obj, LONG);
	}

	public void add(String name, Object obj, int timeSec) {
		if(obj instanceof ArrayList) obj = ((ArrayList)obj).clone();
		CachedObject o = new CachedObject(obj, timeSec>0? System.currentTimeMillis()+timeSec*1000: 0);
		cache.put(name, o);
	}

	public Object get(String name) {
		CachedObject o = cache.get(name);
		if(o==null || o.object==null) return null;
		if(o.object instanceof ArrayList) return ((ArrayList)o.object).clone(); //keep the order
		return o.object;
	}

	public void remove(String name) {
		cache.remove(name);
	}

	public void removeAllWithPrefix(String prefix) {
		for(String key: new ArrayList<>(cache.keySet())) {
			if(key.startsWith(prefix)) cache.remove(key);
		}
	}

	public static void removeAll() {
		instance.clear();
	}
}
