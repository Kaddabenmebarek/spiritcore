package com.idorsia.research.spirit.core.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.dao.AbstractDao;
import com.idorsia.research.spirit.core.dao.EnclosureDao;
import com.idorsia.research.spirit.core.model.Enclosure;
import com.idorsia.research.spirit.core.util.DataUtils;

@Repository
public class EnclosureDaoImpl extends AbstractDao<Enclosure> implements EnclosureDao {

	private static final String TABLE_NAME = "Enclosure";

	@Override
	public Enclosure get(Integer id) {
		return super.get(TABLE_NAME, Enclosure.class, id);
	}

	@Override
	public List<Enclosure> list() {
		return super.getlist(TABLE_NAME, Enclosure.class);
	}

	@Override
	public Set<Enclosure> getByStudy(Integer studyId) {
		String	sql = String.format("SELECT * FROM " + TABLE_NAME + " WHERE STUDY_ID = '%s'", studyId);
		List<Enclosure> result = (List<Enclosure>) super.getObjectList(TABLE_NAME, Enclosure.class, sql);
		return result != null ? new HashSet<Enclosure>(result) : new HashSet<Enclosure>();
	}

	@Override
	public Set<Enclosure> getBySamples(Set<Integer> samplesIds, int studyId) {
		StringBuilder sql = new StringBuilder(
				"select e.* from enclosure e, biosample_enclosure be where e.id = be.enclosure_id and e.study_id = ")
						.append(studyId).append(" and be.biosample_id ");
		sql.append(DataUtils.fetchInInt(new ArrayList<Integer>(samplesIds)));
		List<Enclosure> cages = (List<Enclosure>) super.getObjectList(TABLE_NAME, Enclosure.class, sql.toString());
		return new HashSet<Enclosure>(cages);
	}
	
	@Override
	public int getCount() {
		return super.getCount(TABLE_NAME);
	}

	@Override
	public Integer saveOrUpdate(Enclosure enclosure) {
		if (enclosure.getId() != null && enclosure.getId() > 0) {
			if(!enclosure.equals(get(enclosure.getId()))) {
				String sql = "UPDATE ENCLOSURE SET " 
						+ "STUDY_ID=:studyId, " 
						+ "CREDATE=:credate, "
						+ "CREUSER=:creuser, "
						+ "UPDDATE=:upddate, "
						+ "UPDUSER=:upduser, "
						+ "NAME=:name " 
						+ "WHERE id=:id";
				SqlParameterSource namedParameters = new MapSqlParameterSource()
						.addValue("id", enclosure.getId())
						.addValue("studyId", enclosure.getStudyId())
						.addValue("upddate", enclosure.getUpdDate())
						.addValue("upduser", enclosure.getUpdUser())
						.addValue("credate", enclosure.getCreDate())
						.addValue("creuser", enclosure.getCreUser())
						.addValue("name", enclosure.getName());
				super.saveOrUpdate(sql, namedParameters);
			}
		} else {
			enclosure.setId(getSequence(Constants.STUDY_ENCLOSURE_SEQUENCE_NAME));
			addEnclosure(enclosure);
			addTransient(enclosure);
		}
		addIdToObject(Enclosure.class, enclosure.getId(), enclosure);
		return enclosure.getId();
	}

	@Override
	public int addEnclosure(Enclosure enclosure) {
		return super.add(TABLE_NAME, enclosure);
	}

	@Override
	public void delete(int enclosureId) {
		super.delete(TABLE_NAME, enclosureId);
	}

	@Override
	public Map<String, List<Integer>> getCageBiosampleMap(List<Integer> cageIds) {
		Map<String, List<Integer>> cageBiosampleMap = new HashMap<String, List<Integer>>();
		Map<Integer,String> queryResults = new HashMap<Integer,String>();
		StringBuilder sql = new StringBuilder("select be.biosample_id, e.name from biosample_enclosure be, enclosure e where e.id = be.enclosure_id and e.id ");
		sql.append(DataUtils.fetchInInt(cageIds));
		getJdbcTemplate().query(sql.toString(), 
			      (rs, rowNum) -> queryResults.put(rs.getInt("biosample_id"), rs.getString("name")));
		for(Entry<Integer,String> entry : queryResults.entrySet()) {
			if(cageBiosampleMap.get(entry.getValue())!=null) {
				cageBiosampleMap.get(entry.getValue()).add(entry.getKey());
			}else {
				List<Integer> sampleIds = new ArrayList<Integer>();
				sampleIds.add(entry.getKey());
				cageBiosampleMap.put(entry.getValue(), sampleIds);
			}
		}
		return cageBiosampleMap;
	}

	@Override
	public List<Enclosure> getEnclosuresByStudy(Integer studyId) {
		String sql = String.format("select e.* from enclosure e where e.study_id = %s", studyId);
		return (List<Enclosure>) super.getObjectList(TABLE_NAME, Enclosure.class, sql);
	}

}
