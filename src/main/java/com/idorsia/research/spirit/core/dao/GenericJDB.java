package com.idorsia.research.spirit.core.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.dto.IObject;


@Repository
public class GenericJDB<T> {
	
	@SuppressWarnings("rawtypes")
	protected static Map<Class, Map<Integer, Object>> classIdToObject = new ConcurrentHashMap<>();


	@Autowired
	@Qualifier("spiritJdbcTemplate")
	private JdbcTemplate jdbcTemplate;
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public T get(String tableName, Class classType, Integer id) {
		if(id==null || id.equals(Constants.NEWTRANSIENTID))
			return null;
		Map<Integer, Object> idToObject = classIdToObject.get(classType); 
		if(idToObject == null) {
			idToObject = new HashMap<Integer, Object>();
			classIdToObject.put(classType, idToObject);
		}
		T object = (T) idToObject.get(id);
		if(object==null) {
			try {	
				String sql = String.format("SELECT * FROM " + tableName + " WHERE ID = %s", id);
				object = (T) jdbcTemplate.queryForObject(sql, BeanPropertyRowMapper.newInstance(classType));
			}catch(EmptyResultDataAccessException e) {
				System.out.println("Entity "+classType+" from "+tableName+" with id "+id+" not found...");
				return null;
			}
			idToObject.put(id, object);
		}
		return object;
	}

	public void addIdToObject(Class<?> classType, Integer id, Object object) {
		if(id==null || id.equals(Constants.NEWTRANSIENTID))
			return;
		Map<Integer, Object> idToObject = classIdToObject.get(classType); 
		if(idToObject == null) {
			idToObject = new HashMap<Integer, Object>();
			classIdToObject.put(classType, idToObject);
		}
		idToObject.put(id, object);
	}
	
	@SuppressWarnings({ "rawtypes"})
	public List<T> getlist(String tableName, Class classType) {
		String sql = "SELECT * FROM " + tableName;
		return (List<T>) getObjectList(tableName, classType, sql);
	}

	public int getCount(String tableName) {
		return jdbcTemplate.queryForList("SELECT * FROM " + tableName).size();
	}

	public int add(String tableName,  T object) {
		SimpleJdbcInsert insertObject = new SimpleJdbcInsert(jdbcTemplate.getDataSource())
				.withTableName(tableName);
		SqlParameterSource parameters = new BeanPropertySqlParameterSource(object);
		return insertObject.execute(parameters);
	}

	public int add(String tableName,  String schemaName, T object) {
		SimpleJdbcInsert insertObject = new SimpleJdbcInsert(jdbcTemplate.getDataSource())
				.withTableName(tableName).withSchemaName(schemaName);
		SqlParameterSource parameters = new BeanPropertySqlParameterSource(object);
		return insertObject.execute(parameters);
	}
	
	public void saveOrUpdate(String sql, SqlParameterSource namedParameters) {
		NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(
				jdbcTemplate.getDataSource());
		namedParameterJdbcTemplate.update(sql, namedParameters);
	}

	public void delete(String tableName, int objectId) {
		String sql = "DELETE FROM " + tableName + " WHERE ID=?";
		this.getJdbcTemplate().update(sql, objectId);
	}

	public void deleteAll(String tableName, Set<Integer> objectIds) {
		StringBuilder sql = new StringBuilder("SELECT * FROM " + tableName + " WHERE ID IN (");
		boolean isFirst=true;
		for (Integer id : objectIds) {
			if(isFirst) {
				isFirst=false;
			}else {
				sql.append(",");
			}
			sql.append(id);
		}
		sql.append(")");

		this.getJdbcTemplate().update(sql.toString());
	}
	
	public Integer getSequence(String sequenceName) {
		int currentValue = jdbcTemplate.queryForObject(String.format("SELECT %s.NEXTVAL from dual", sequenceName),
				Integer.class);
		int sequence = currentValue - 1;
		return sequence;
	}
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public T getObject(String tableName, Class classtype, String sql) {
		return jdbcTemplate.query(sql, new ResultSetExtractor<T>() {
			public T extractData(ResultSet rs) throws SQLException, DataAccessException {
				if (rs.next()) {
					return (T) BeanPropertyRowMapper.newInstance(classtype);
				}
				return null;
			}
		});
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Collection<T> getObjectList(String tableName, Class classtype, String sql) {
		return jdbcTemplate.query(sql, new ResultSetExtractor<List<T>>() {
			public List<T> extractData(ResultSet rs) throws SQLException, DataAccessException {
				List<T> s = new ArrayList<T>();
				while (rs.next()) {
					s.add((T) BeanPropertyRowMapper.newInstance(classtype));
				}
				return s;
			}
		});
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public T getObject(Class classtype, String sql) {
		return (T) jdbcTemplate.queryForObject(sql, BeanPropertyRowMapper.newInstance(classtype));
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Collection<T> getObjectList(Class classtype, String sql) {
		Collection<T> results =  jdbcTemplate.query(sql, BeanPropertyRowMapper.newInstance(classtype));
		for(T result : results) {
			if(result instanceof IObject) {
				IObject r = (IObject)result;
				addObjectInCache(r, r.getClass(), r.getId());
			}
		}
		return results;
	}

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}
	
	protected void addObjectInCache(IObject object, Class<?> classType, int id){
		Map<Integer, Object> idToObject = classIdToObject.get(classType); 
		if(idToObject == null) {
			idToObject = new HashMap<Integer, Object>();
			classIdToObject.put(classType, idToObject);
		}
		if(idToObject.get(id)==null)
			idToObject.put(id, object);
	}
	
	protected Object getObjectInCache(Class<?> classType, int id){
		Map<Integer, Object> idToObject = classIdToObject.get(classType); 
		if(idToObject != null) {
			return idToObject.get(id);
		}
		return null;
	}
}
