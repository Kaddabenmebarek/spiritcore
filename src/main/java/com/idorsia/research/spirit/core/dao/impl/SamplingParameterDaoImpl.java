package com.idorsia.research.spirit.core.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.dao.AbstractDao;
import com.idorsia.research.spirit.core.dao.SamplingParameterDao;
import com.idorsia.research.spirit.core.model.SamplingParameter;
import com.idorsia.research.spirit.core.util.DataUtils;
import com.idorsia.research.spirit.core.util.UserUtil;

@Repository
public class SamplingParameterDaoImpl extends AbstractDao<SamplingParameter> implements SamplingParameterDao {

	private static final String TABLE_NAME = "SAMPLING_PARAMETER";
	
	@Override
	public SamplingParameter get(Integer id) {
		return super.get(TABLE_NAME, SamplingParameter.class, id);
	}

	@Override
	public List<SamplingParameter> getSamplingParameterByBiotypeMetadata(int biotypeMetadataId) {
		String sql = String.format("SELECT * FROM " + TABLE_NAME + " WHERE BIOTYPEMETADATA_ID = '%s'",
				biotypeMetadataId);
		return (List<SamplingParameter>) super.getObjectList(TABLE_NAME, SamplingParameter.class, sql);
	}

	@Override
	public List<SamplingParameter> getSamplingParameterBySampling(int samplingId) {
		String sql = String.format("SELECT * FROM " + TABLE_NAME + " WHERE SAMPLING_ID = '%s'",
				samplingId);
		return (List<SamplingParameter>) super.getObjectList(TABLE_NAME, SamplingParameter.class, sql);
	}

	@Override
	public List<SamplingParameter> list() {
		return super.getlist(TABLE_NAME, SamplingParameter.class);
	}

	@Override
	public int getCount() {
		return super.getCount(TABLE_NAME);
	}

	@Override
	public Integer saveOrUpdate(SamplingParameter samplingParameter) {
		samplingParameter.setUpdDate(new Date());
		samplingParameter.setUpdUser(UserUtil.getUsername());
		if (samplingParameter.getId() != null && samplingParameter.getId() > 0) {
			if(!samplingParameter.equals(get(samplingParameter.getId()))) {
				String sql = "UPDATE "+TABLE_NAME+" SET "
						+ "BIOTYPEMETADATA_ID=:biotypeMetadataId, "
						+ "SAMPLING_ID=:samplingId, "
						+ "VALUE=:value, "
						+ "CREDATE=:creDate, "
						+ "CREUSER=:creUser, "
						+ "UPDDATE=:updDate, "
						+ "UPDUSER=:updUser "
						+ "WHERE id=:id";
			    SqlParameterSource namedParameters = new MapSqlParameterSource()
			    		.addValue("id", samplingParameter.getId())
			    		.addValue("biotypeMetadataId", samplingParameter.getBiotypemetadataId())
			    		.addValue("samplingId", samplingParameter.getSamplingId())
			    		.addValue("value", samplingParameter.getValue())
			    		.addValue("creDate", samplingParameter.getCreDate())
			    		.addValue("creUser", samplingParameter.getCreUser())
			    		.addValue("updDate", samplingParameter.getUpdDate())
			    		.addValue("updUser", samplingParameter.getUpdUser());
			    super.saveOrUpdate(sql, namedParameters);
			}
		} else {
			samplingParameter.setCreDate(new Date());
			samplingParameter.setCreUser(UserUtil.getUsername());
			samplingParameter.setId(getSequence(Constants.SAMPLING_PARAMETER_SEQUENCE_NAME));
			addSamplingParameter(samplingParameter);
			addTransient(samplingParameter);
		}
		addIdToObject(SamplingParameter.class, samplingParameter.getId(), samplingParameter);
		return samplingParameter.getId();
	}

	@Override
	public int addSamplingParameter(SamplingParameter samplingParameter) {
		return super.add(TABLE_NAME, samplingParameter);
	}

	@Override
	public void delete(int samplingParameterId) {
		super.delete(TABLE_NAME, samplingParameterId);
	}
	
	@Override
	public void delete(List<Integer> ids) {
		List<Integer> idsTemp = new ArrayList<>();
		int i=0;
		int j=0;
		while(ids.size()>i) {
			while(j%1000<999 && ids.size()>i) {
				Integer id = ids.get(i);
				if(id!=Constants.NEWTRANSIENTID) {
					idsTemp.add(id);
					j++;
				}
				i++;
			}
			j=0;
			if(idsTemp.size()>0) {
				super.getJdbcTemplate().update("DELETE from " + TABLE_NAME + " b WHERE ID"+DataUtils.fetchInInt(new ArrayList<Integer>(idsTemp)));
			}
			idsTemp.clear();
		}
	}

}
