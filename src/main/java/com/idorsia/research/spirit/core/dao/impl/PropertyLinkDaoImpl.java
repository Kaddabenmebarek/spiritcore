package com.idorsia.research.spirit.core.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.dao.AbstractDao;
import com.idorsia.research.spirit.core.dao.PropertyLinkDao;
import com.idorsia.research.spirit.core.model.PropertyLink;
import com.idorsia.research.spirit.core.util.UserUtil;

@Repository
public class PropertyLinkDaoImpl extends AbstractDao<PropertyLink> implements PropertyLinkDao {
	
	private static final String TABLE_NAME = "PROPERTY_LINK";

	@Override
	public PropertyLink get(Integer id) {
		return super.get(TABLE_NAME, PropertyLink.class, id);
	}

	@Override
	public PropertyLink getPropertyLinkById(int id) {
		String sql = String.format("SELECT * FROM PROPERTY_LINK WHERE ID = %s", id);
		return super.getObject(TABLE_NAME, PropertyLink.class, sql);
	}
	
	public List<String> getValuesByPopertyId(Integer id){
		List<String> results = new ArrayList<String>();
		String sql = String.format("SELECT * FROM PROPERTY_LINK WHERE PROPERTY_ID = %s", id);
		List<PropertyLink> pLList = (List<PropertyLink>) super.getObjectList(TABLE_NAME, PropertyLink.class, sql);
		for(PropertyLink pLL : pLList) {
			String value = pLL.getValue();
			if(!results.contains(value))
				results.add(value);
		}
		return results;
	}
	
	@Override
	public List<PropertyLink> getPropertyLinksByStudy(Integer studyId) {
		String sql = String.format("SELECT * FROM PROPERTY_LINK WHERE STUDY_ID = %s", studyId);
		return (List<PropertyLink>) super.getObjectList(TABLE_NAME, PropertyLink.class, sql);
	}
	
	@Override
	public List<PropertyLink> getPropertyLinksByProperty(Integer propertyId) {
		String sql = String.format("SELECT * FROM PROPERTY_LINK WHERE PROPERTY_ID = %s", propertyId);
		return (List<PropertyLink>) super.getObjectList(TABLE_NAME, PropertyLink.class, sql);
	}
	
	@Override
	public PropertyLink getPropertyLinkByStudyAndProperty(Integer studyId, Integer propertyId) {
		String sql = String.format("SELECT * FROM PROPERTY_LINK WHERE STUDY_ID = %s AND PROPERTY_ID  = %s", studyId, propertyId);
		return super.getObject(TABLE_NAME, PropertyLink.class, sql);
	}
	
	@Override
	public List<PropertyLink> list() {
		return super.getlist(TABLE_NAME, PropertyLink.class);
	}

	@Override
	public int getCount() {
		return super.getCount(TABLE_NAME);
	}

	@Override
	public int addPropertyLink(PropertyLink propertyLink) {
		return super.add(TABLE_NAME, propertyLink);
	}
	
	@Override
	public void delete(int propertyLinkId) {
		super.delete(TABLE_NAME, propertyLinkId);
	}

	@Override
	public Integer saveOrUpdate(PropertyLink propertyLink) {
		propertyLink.setUpdDate(new Date());
		propertyLink.setUpdUser(UserUtil.getUsername());
		if (propertyLink.getId() != null && propertyLink.getId() > 0) {
			if(!propertyLink.equals(get(propertyLink.getId()))) {
				String sql = "UPDATE PROPERTY_LINK SET property_id=:property_id, " 
						+ "study_id=:study_id, " 
						+ "value=:value, " 
						+ "upd_date=:upd_date, " 
						+ "upd_user=:upd_user, "
						+ "cre_date=:cre_date, "
						+ "cre_user=:cre_user "
						+ "WHERE id=:id";
			    SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("id", propertyLink.getId())
			    		.addValue("property_id", propertyLink.getPropertyId())
			    		.addValue("study_id", propertyLink.getStudyId())
			    		.addValue("value", propertyLink.getValue())
			    		.addValue("upd_date", propertyLink.getUpdDate())
			    		.addValue("upd_user", propertyLink.getUpdUser())
			    		.addValue("cre_date", propertyLink.getCreDate())
			    		.addValue("cre_user", propertyLink.getCreUser());
			    super.saveOrUpdate(sql, namedParameters);
			}
		} else {
			propertyLink.setCreDate(new Date());
			propertyLink.setCreUser(UserUtil.getUsername());
			propertyLink.setId(getSequence(Constants.STUDY_PROPERTY_LINK_SEQUENCE_NAME));
			addPropertyLink(propertyLink);
			addTransient(propertyLink);
		}
		addIdToObject(PropertyLink.class, propertyLink.getId(), propertyLink);
		return propertyLink.getId();
	}
	
	public PropertyLink rowMap(ResultSet rs) {
		PropertyLink propertyLink = null;
    	try {
    		propertyLink = new PropertyLink();
			propertyLink.setId(rs.getInt("id"));
			propertyLink.setPropertyId(rs.getInt("property_id"));
			propertyLink.setStudyId(rs.getInt("study_id"));
			propertyLink.setValue(rs.getString("value"));
			propertyLink.setUpdDate(rs.getDate("upd_date"));
			propertyLink.setUpdUser(rs.getString("upd_user"));
			propertyLink.setCreDate(rs.getDate("cre_date"));
			propertyLink.setCreUser(rs.getString("cre_user"));
		} catch (SQLException e) {
			e.printStackTrace();
		}
    	return propertyLink;
	}
}
