package com.idorsia.research.spirit.core.dao.impl;

import java.util.Date;
import java.util.List;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.dao.AbstractDao;
import com.idorsia.research.spirit.core.dao.LinkedBiosampleDao;
import com.idorsia.research.spirit.core.model.LinkedBiosample;
import com.idorsia.research.spirit.core.util.UserUtil;

@Repository
public class LinkedBiosampleDaoImpl extends AbstractDao<LinkedBiosample> implements LinkedBiosampleDao {

	private static String TABLE_NAME = "BIOSAMPLE_BIOSAMPLE";

	@Override
	public LinkedBiosample get(Integer id) {
		String sql = String.format("SELECT * FROM %s WHERE ID = %s", TABLE_NAME, id);
		return super.getObject(TABLE_NAME, LinkedBiosample.class, sql);
	}
	
	@Override
	public int addLinkedBiosample(LinkedBiosample linkedBiosample) {
		try {
			return super.add(TABLE_NAME, linkedBiosample);
		} catch (Exception e) {
			// if already exists
			return 0;
		}
	}

	@Override
	public void delete(Integer linkedBiosample) {
		String sql = String.format("DELETE FROM %s WHERE ID = %s",TABLE_NAME, linkedBiosample);
		getJdbcTemplate().execute(sql);
	}

	@Override
	public List<LinkedBiosample> getLinkedBiosamplesByBiosample(Integer biosampleId) {
		String sql = String.format("SELECT * FROM " + TABLE_NAME + " WHERE BIOSAMPLE_ID=%s", biosampleId);
		return (List<LinkedBiosample>) super.getObjectList(TABLE_NAME, LinkedBiosample.class, sql);
	}

	@Override
	public List<LinkedBiosample> getLinkedBiosamplesByLinkedBiosample(Integer linkedBiosampleId) {
		String sql = String.format("SELECT * FROM " + TABLE_NAME + " WHERE LINKED_BIOSAMPLE_ID=%s", linkedBiosampleId);
		return (List<LinkedBiosample>) super.getObjectList(TABLE_NAME, LinkedBiosample.class, sql);
	}

	@Override
	public List<LinkedBiosample> getLinkedBiosamplesByMetadata(Integer metadataId) {
		String sql = String.format("SELECT * FROM " + TABLE_NAME + " WHERE BIOTYPEMETADATA_ID=%s", metadataId);
		return (List<LinkedBiosample>) super.getObjectList(TABLE_NAME, LinkedBiosample.class, sql);
	}
	
	@Override
	public Integer saveOrUpdate(LinkedBiosample linkedBiosample) {
		linkedBiosample.setUpdDate(new Date());
		linkedBiosample.setUpdUser(UserUtil.getUsername());
		if (linkedBiosample.getId() != null && linkedBiosample.getId() > 0) {
			if(!linkedBiosample.equals(get(linkedBiosample.getId()))) {
				String sql = "UPDATE "+TABLE_NAME+" SET "
						+ "LINKED_BIOSAMPLE_ID=:linkedBiosampleId, "
						+ "BIOTYPEMETADATA_ID=:biotypeMetadataId, "
						+ "BIOSAMPLE_ID=:biosampleId, "
						+ "CREDATE=:creDate, "
						+ "CREUSER=:creUser, "
						+ "UPDDATE=:updDate, "
						+ "UPDUSER=:updUser "
						+ "WHERE id=:id";
			    SqlParameterSource namedParameters = new MapSqlParameterSource()
			    		.addValue("id", linkedBiosample.getId())
			    		.addValue("biosampleId", linkedBiosample.getBiosampleId())
			    		.addValue("biotypeMetadataId", linkedBiosample.getBiotypemetadataId())
			    		.addValue("linkedBiosampleId", linkedBiosample.getLinkedBiosampleId())
			    		.addValue("creDate", linkedBiosample.getCreDate())
			    		.addValue("creUser", linkedBiosample.getCreUser())
			    		.addValue("updDate", linkedBiosample.getUpdDate())
			    		.addValue("updUser", linkedBiosample.getUpdUser());;
			    super.saveOrUpdate(sql, namedParameters);
			}
		} else {
			linkedBiosample.setCreDate(new Date());
			linkedBiosample.setCreUser(UserUtil.getUsername());
			linkedBiosample.setId(getSequence(Constants.GROUP_PATTERN_SEQUENCE_NAME));
			addLinkedBiosample(linkedBiosample);
			addTransient(linkedBiosample);
		}
		addIdToObject(LinkedBiosample.class, linkedBiosample.getId(), linkedBiosample);
		return linkedBiosample.getId();
	}

}
