package com.idorsia.research.spirit.core.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.dao.AbstractDao;
import com.idorsia.research.spirit.core.dto.IObject;

@Service
public class AbstractService implements Serializable {
	
	private static final long serialVersionUID = -4996179987560399259L;
	private static volatile Map<Class<?>,Map<Integer, IObject>> idToObject = new ConcurrentHashMap<>();
	private static List<IObject> transients = new ArrayList<IObject>();
	static Set<Object> savedItems = new HashSet<>();


	@Autowired
	@SuppressWarnings("rawtypes")
	private AbstractDao abstractDao;
	
	@Autowired
	@Qualifier("org.dozer.Mapper")
	protected transient DozerBeanMapper dozerMapper;

	public Integer getSequence(String sequenceName) {
		return abstractDao.getSequence(sequenceName);
	}
	
	private static void clearCachMap() {
		for(Class<?> clazz : idToObject.keySet()) {
			idToObject.get(clazz).clear();
		}
	}
	
	protected static void clearSavedItem() {
		savedItems.clear();
	}
	
	public static Map<Integer, ?> getCacheMap(Class<?> clazz){
		Map<Integer, IObject> result = idToObject.get(clazz);
		if(result==null) {
			result = new HashMap<Integer, IObject>();
			idToObject.put(clazz, result);
		}
		return result;
	}
	
	public static void clear() {
		clearSavedItem();
		clearTransient(true);
		clearCachMap();
		AbstractDao.clearCache();
	}

	public <T extends IObject> List<Integer> getIds(Collection<T> object) {
		List<Integer> res = new ArrayList<>();
		for (T o : object) {
			if(o!=null) res.add(o.getId());
		}
		return res;
	}
	
	public static <T extends IObject> Map<Integer, T> mapIds(Collection<T> object) {
		Map<Integer, T> res = new HashMap<>();
		for (T o : object) {
			if(o!=null && o.getId()>0) res.put(o.getId(), o);
		}
		return res;
	}

	public static void clearTransient(boolean isOk) {
		if(!isOk) {
			for(IObject obj : transients) {
				obj.setId(Constants.NEWTRANSIENTID);
			}
		}
		transients.clear();
	}

	public static void addTransient(IObject obj) {
		transients.add(obj);
	}
}
