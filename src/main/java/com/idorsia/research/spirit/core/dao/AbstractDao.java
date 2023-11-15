package com.idorsia.research.spirit.core.dao;

import java.io.Serializable;
import java.util.Collection;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.service.AbstractService;

@Repository
public class AbstractDao<IObject> extends GenericJDB<IObject> {
	
	@SuppressWarnings("rawtypes")
	public Collection<IObject> getObjectList(String tableName, Class classType, String sql) {
		return super.getObjectList(classType,sql);
	}

	@SuppressWarnings("rawtypes")
	public IObject getObject(String tableName, Class classType, String sql) {
		try{
			return super.getObject(classType,sql);
		}catch(EmptyResultDataAccessException e) {
			return null;
		}catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public int getCount(String tableName) {
		return super.getCount(tableName);
	}

	public int add(String tableName, IObject object) {
		return super.add(tableName, object);
	}

	public void saveOrUpdate(final String sql, final SqlParameterSource namedParameters) {
		super.saveOrUpdate(sql, namedParameters);
	}

	public void delete(String tableName, Integer objectId) {
		if(objectId==null || objectId==Constants.NEWTRANSIENTID)
			return;
		super.delete(tableName, objectId);
	}

	public Integer getSequence(String sequenceName) {
		return super.getSequence(sequenceName);
	}
	
	public static void clearCache() {
		classIdToObject.clear();
	}
	
	protected void addTransient(com.idorsia.research.spirit.core.dto.IObject obj) {
		AbstractService.addTransient(obj);
	}
}
