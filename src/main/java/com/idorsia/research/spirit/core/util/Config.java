package com.idorsia.research.spirit.core.util;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.prefs.Preferences;

public class Config {
	
	private static final Map<String, Config> configs = new HashMap<>();
	public static final String SERVER = "ares";
	public static final String DATEFORMAT = "%1$td.%1$tm.%1$tY %1$tH:%1$tM";
	
	private Preferences preferences;
	
	private Config() {}
	
	/**
	 * Constructor. 
	 * Loads the properties from ${user.home}/.${project}.properties
	 * @param project
	 */
	private Config(String project) {
		this.preferences = Preferences.userRoot().node("com.actelion.research."+project);
	}
	
	
	/**
	 * Gets or Creates the Config instance for the given project.<br>
	 * This method is synchronized
	 * @param project
	 * @return
	 */
	public static Config getInstance(String project) {
		Config config = configs.get(project);
		if(config==null) {
			synchronized(configs) {
				config = configs.get(project);
				if(config==null) {			 
					config = new Config(project);
					configs.put(project, config);
				}
			}
		} 
		return config;
	}

	public String getProperty(String key, String def) {
		return preferences.get(key, def);
	}
	  
	public int getProperty(String key, int def) {
		return preferences.getInt(key, def);
	}
	public long getProperty(String key, long def) {
		return preferences.getLong(key, def);
	}
	public boolean getProperty(String key, boolean def) {
		return preferences.getBoolean(key, def);
	}
	public File getProperty(String key, File def) {
		String v = getProperty(key, ""+def);		
		return new File(v); 
	}

	public List<String> getProperty(String key, List<String> def) {
		List<String> res = new ArrayList<String>();
		for(int i=0; ;i++) {
			String v = preferences.get(key + "." + i, null);
			if(v==null) break; 
			res.add(v);
		}
		if(res.size()==0) res = def;
		
		return res;
	}
	  
	public void setProperty(String key, boolean value) {
		preferences.putBoolean(key, value);
	}
	public void setProperty(String key, File file) {
		setProperty(key, file==null?"": file.getAbsolutePath());
	}
	public void setProperty(String key, String value) {
		if ("".equals(value)) {
			preferences.remove(key);
		} else {
			preferences.put(key, value);
		}
	}
	public void setProperty(String key, int value) {
		preferences.putInt(key, value);
	}
	public void setProperty(String key, long value) {
		preferences.putLong(key, value);
	}
	public void setProperty(String key, List<String> value) {
		for (int i = 0; i < value.size(); i++) {
			String s = value.get(i);
			preferences.put(key+"."+i, ""+s);			
		}
		for(int i=value.size(); preferences.get(key+"."+i, null)!=null; i++) {
			preferences.remove(key+"."+i);			
		}
	}

}
