package com.idorsia.research.spirit.core.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.dao.AbstractDao;
import com.idorsia.research.spirit.core.dao.SamplingMeasurementDao;
import com.idorsia.research.spirit.core.model.SamplingMeasurement;
import com.idorsia.research.spirit.core.util.DataUtils;
import com.idorsia.research.spirit.core.util.UserUtil;

@Repository
public class SamplingMeasurementDaoImpl extends AbstractDao<SamplingMeasurement> implements SamplingMeasurementDao {

	private static final String TABLE_NAME = "SAMPLING_MEASUREMENT";
	
	@Override
	public SamplingMeasurement get(Integer id) {
		return super.get(TABLE_NAME, SamplingMeasurement.class, id);
	}

	@Override
	public List<SamplingMeasurement> getSamplingMeasurementByTest(int testId) {
		String sql = String.format("SELECT * FROM " + TABLE_NAME + " WHERE TEST_ID = '%s'",
				testId);
		return (List<SamplingMeasurement>) super.getObjectList(TABLE_NAME, SamplingMeasurement.class, sql);
	}

	@Override
	public List<SamplingMeasurement> getSamplingMeasurementBySampling(int samplingId) {
		String sql = String.format("SELECT * FROM " + TABLE_NAME + " WHERE SAMPLING_ID = '%s'",
				samplingId);
		return (List<SamplingMeasurement>) super.getObjectList(TABLE_NAME, SamplingMeasurement.class, sql);
	}

	@Override
	public List<SamplingMeasurement> list() {
		return super.getlist(TABLE_NAME, SamplingMeasurement.class);
	}

	@Override
	public int getCount() {
		return super.getCount(TABLE_NAME);
	}

	@Override
	public Integer saveOrUpdate(SamplingMeasurement samplingMeasurement) {
		samplingMeasurement.setUpdDate(new Date());
		samplingMeasurement.setUpdUser(UserUtil.getUsername());
		if (samplingMeasurement.getId() != null && samplingMeasurement.getId() > 0) {
			if(!samplingMeasurement.equals(get(samplingMeasurement.getId()))) {
				String sql = "UPDATE " + TABLE_NAME + " SET " 
						+ "SAMPLING_ID=:samplingId, " 
						+ "ASSAY_ID=:assayId, "
						+ "CREDATE=:creDate, "
						+ "CREUSER=:creUser, "
						+ "UPDDATE=:updDate, "
						+ "UPDUSER=:updUser "					
						+ "WHERE ID=:id";
				SqlParameterSource namedParameters = new MapSqlParameterSource()
						.addValue("id", samplingMeasurement.getId())
						.addValue("samplingId", samplingMeasurement.getSamplingId())
						.addValue("assayId", samplingMeasurement.getAssayId())
			    		.addValue("creDate", samplingMeasurement.getCreDate())
			    		.addValue("creUser", samplingMeasurement.getCreUser())
			    		.addValue("updDate", samplingMeasurement.getUpdDate())
			    		.addValue("updUser", samplingMeasurement.getUpdUser());
				super.saveOrUpdate(sql, namedParameters);
			}
		} else {
			samplingMeasurement.setCreDate(new Date());
			samplingMeasurement.setCreUser(UserUtil.getUsername());
			samplingMeasurement.setId(getSequence(Constants.SAMPLING_MEASUREMENT_SEQUENCE_NAME));
			addSamplingMeasurement(samplingMeasurement);
			addTransient(samplingMeasurement);
		}
		addIdToObject(SamplingMeasurement.class, samplingMeasurement.getId(), samplingMeasurement);
		return samplingMeasurement.getId();
	}

	@Override
	public SamplingMeasurement getSamplingMeasurementById(int id) {
		String sql = String.format("SELECT * FROM SAMPLING_MEASUREMENT WHERE ID = %s", id);
		return super.getObject(TABLE_NAME, SamplingMeasurement.class, sql);
	}

	@Override
	public List<SamplingMeasurement> getSamplingMeasurementsByAssay(Integer assayId) {
		String sql = String.format("SELECT * FROM SAMPLING_MEASUREMENT WHERE TEST_ID = %s", assayId);
		return (List<SamplingMeasurement>) super.getObjectList(TABLE_NAME, SamplingMeasurement.class, sql);
	}

	@Override
	public List<SamplingMeasurement> getSamplingMeasurementsBySampling(Integer samplingId) {
		String sql = String.format("SELECT * FROM SAMPLING_MEASUREMENT WHERE SAMPLING_ID = %s", samplingId);
		return (List<SamplingMeasurement>) super.getObjectList(TABLE_NAME, SamplingMeasurement.class, sql);
	}

	@Override
	public List<SamplingMeasurement> getSamplingMeasurementBySamplingAndAssay(Integer samplingId, Integer assayId) {
		String sql = String.format("SELECT * FROM SAMPLING_MEASUREMENT WHERE SAMPLING_ID = %s AND TEST_ID=%s", samplingId, assayId);
		return (List<SamplingMeasurement>) super.getObjectList(TABLE_NAME, SamplingMeasurement.class, sql);
	}

	@Override
	public int addSamplingMeasurement(SamplingMeasurement samplingMeasurement) {
		return super.add(TABLE_NAME, samplingMeasurement);
	}

	@Override
	public void delete(int samplingMeasurementId) {
		super.delete(TABLE_NAME, samplingMeasurementId);
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
