package com.idorsia.research.spirit.core.dao.impl;

import java.util.List;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.dao.AbstractDao;
import com.idorsia.research.spirit.core.dao.SamplingDao;
import com.idorsia.research.spirit.core.model.Sampling;

@Repository
public class SamplingDaoImpl extends AbstractDao<Sampling> implements SamplingDao {

	private static final String TABLE_NAME = "Sampling";
	
	@Override
	public Sampling get(Integer id) {
		return super.get(TABLE_NAME, Sampling.class, id);
	}

	@Override
	public List<Sampling> getByNamedSampling(Integer namedSamplingId){
		String sql = String.format("SELECT * FROM " + TABLE_NAME + " WHERE NAMEDSAMPLING_ID = '%s'",
				namedSamplingId);
		return (List<Sampling>) super.getObjectList(TABLE_NAME, Sampling.class, sql);
	}

	@Override
	public List<Sampling> getByParent(Integer parentId){
		String sql = String.format("SELECT * FROM " + TABLE_NAME + " WHERE PARENT_SAMPLING_ID = '%s'",
				parentId);
		return (List<Sampling>) super.getObjectList(TABLE_NAME, Sampling.class, sql);
	}
	
	@Override
	public List<Sampling> list() {
		return super.getlist(TABLE_NAME, Sampling.class);
	}

	@Override
	public int getCount() {
		return super.getCount(TABLE_NAME);
	}

	@Override
	public Integer saveOrUpdate(Sampling sampling) {
		if (sampling.getId() != null && sampling.getId() > 0) {
			if(!sampling.equals(get(sampling.getId()))) {
				String sql = "UPDATE SAMPLING SET "
						+ "BIOTYPE_ID=:biotypeId, "
						+ "NAMEDSAMPLING_ID=:namedSamplingId, "
						+ "WEIGHINGREQUIRED=:weightRequired, "
						+ "PARENT_SAMPLING_ID=:parentSamplingId, "
						+ "LOCINDEX=:locIndex, "					
						+ "COMMENTSREQUIRED=:commenstRequired, "
						+ "CONTAINERTYPE=:containerType, "
						+ "LENGTHREQUIRED=:lengthRequired, "
						+ "COMMENTS=:comments, "
						+ "AMOUNT=:amount, "
						+ "CREDATE=:credate, "
						+ "CREUSER=:creuser, "
						+ "UPDDATE=:upddate, "
						+ "UPDUSER=:upduser, "
						+ "SAMPLENAME=:sampleName, "
						+ "ROW_NUMBER=:rowNumber "
						+ "WHERE id=:id";
			    SqlParameterSource namedParameters = new MapSqlParameterSource()
			    		.addValue("id", sampling.getId())
			    		.addValue("biotypeId", sampling.getBiotypeId())
			    		.addValue("namedSamplingId", sampling.getNamedsamplingId())
			    		.addValue("weightRequired", sampling.getWeighingRequired())
			    		.addValue("parentSamplingId", sampling.getParentSamplingId())
			    		.addValue("locIndex", sampling.getLocIndex())		    		
			    		.addValue("commenstRequired", sampling.getCommentsRequired())
			    		.addValue("containerType", sampling.getContainerType())
			    		.addValue("lengthRequired", sampling.getLengthRequired())
			    		.addValue("comments", sampling.getComments())
			    		.addValue("amount", sampling.getAmount())
			    		.addValue("sampleName", sampling.getSampleName())
						.addValue("upddate", sampling.getUpdDate())
						.addValue("upduser", sampling.getUpdUser())
						.addValue("credate", sampling.getCreDate())
						.addValue("creuser", sampling.getCreUser())
			    		.addValue("rowNumber", sampling.getRowNumber());
			    super.saveOrUpdate(sql, namedParameters);
			}
		} else {
			sampling.setId(getSequence(Constants.SAMPLING_SEQUENCE_NAME));
			addSampling(sampling);
			addTransient(sampling);
		}
		addIdToObject(Sampling.class, sampling.getId(), sampling);
		return sampling.getId();
	}

	@Override
	public int addSampling(Sampling sampling) {
		return super.add(TABLE_NAME, sampling);
	}

	@Override
	public void delete(int samplingId) {
		super.delete(TABLE_NAME, samplingId);
	}

}
