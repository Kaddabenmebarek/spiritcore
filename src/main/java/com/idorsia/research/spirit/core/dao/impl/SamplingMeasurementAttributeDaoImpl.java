package com.idorsia.research.spirit.core.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.dao.AbstractDao;
import com.idorsia.research.spirit.core.dao.SamplingMeasurementAttributeDao;
import com.idorsia.research.spirit.core.model.SamplingMeasurementAttribute;
import com.idorsia.research.spirit.core.util.DataUtils;

@Repository
public class SamplingMeasurementAttributeDaoImpl  extends AbstractDao<SamplingMeasurementAttribute> implements SamplingMeasurementAttributeDao {

private static final String TABLE_NAME = "SAMPLING_MEASUREMENT_ATTRIBUTE";
	
	@Override
	public SamplingMeasurementAttribute get(Integer id) {
		return super.get(TABLE_NAME, SamplingMeasurementAttribute.class, id);
	}

	@Override
	public List<SamplingMeasurementAttribute> getByAssayAttribute(Integer assayAttributeId) {
		String sql = String.format("SELECT * FROM " + TABLE_NAME + " WHERE ASSAY_ATTRIBUTE_ID = %s",
				assayAttributeId);
		return (List<SamplingMeasurementAttribute>) super.getObjectList(TABLE_NAME, SamplingMeasurementAttribute.class, sql);		
	}

	@Override
	public List<SamplingMeasurementAttribute> getBySamplingMeasurement(Integer samplingMeasurementId) {
		String sql = String.format("SELECT * FROM " + TABLE_NAME + " WHERE SAMPLING_MEASUREMENT_ID = %s",
				samplingMeasurementId);
		return (List<SamplingMeasurementAttribute>) super.getObjectList(TABLE_NAME, SamplingMeasurementAttribute.class, sql);	
	}

	@Override
	public Integer saveOrUpdate(SamplingMeasurementAttribute samplingMeasurementAttribute) {
		if (samplingMeasurementAttribute.getId() != null && samplingMeasurementAttribute.getId() > 0) {
			if(!samplingMeasurementAttribute.equals(get(samplingMeasurementAttribute.getId()))) {
				String sql = "UPDATE " + TABLE_NAME + " SET " 
						+ "SAMPLING_MEASUREMENT_ID=:samplingMeasurementId, " 
						+ "ASSAY_ATTRIBUTE_ID=:assayAttributeId, "
						+ "VALUE=:value, "
						+ "CREDATE=:creDate, "
						+ "CREUSER=:creUser, "
						+ "UPDDATE=:updDate, "
						+ "UPDUSER=:updUser "					
						+ "WHERE ID=:id";
				SqlParameterSource namedParameters = new MapSqlParameterSource()
						.addValue("id", samplingMeasurementAttribute.getId())
						.addValue("samplingMeasurementId", samplingMeasurementAttribute.getSamplingMeasurementId())
						.addValue("assayAttributeId", samplingMeasurementAttribute.getAssayAttributeId())
			    		.addValue("value", samplingMeasurementAttribute.getValue())
			    		.addValue("creDate", samplingMeasurementAttribute.getCreDate())
			    		.addValue("creUser", samplingMeasurementAttribute.getCreUser())
			    		.addValue("updDate", samplingMeasurementAttribute.getUpdDate())
			    		.addValue("updUser", samplingMeasurementAttribute.getUpdUser());
				super.saveOrUpdate(sql, namedParameters);
			}
		} else {
			samplingMeasurementAttribute.setId(getSequence(Constants.SAMPLING_MEASUREMENT_ATTRIBUTE_SEQUENCE_NAME));
			addSamplingMeasurementAttribute(samplingMeasurementAttribute);
			addTransient(samplingMeasurementAttribute);
		}
		addIdToObject(SamplingMeasurementAttribute.class, samplingMeasurementAttribute.getId(), samplingMeasurementAttribute);
		return samplingMeasurementAttribute.getId();
	}

	@Override
	public int addSamplingMeasurementAttribute(SamplingMeasurementAttribute samplingMeasurementAttribute) {
		return super.add(TABLE_NAME, samplingMeasurementAttribute);
	}

	@Override
	public void delete(Integer samplingMeasurementAttributeId) {
		super.delete(TABLE_NAME, samplingMeasurementAttributeId);
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
