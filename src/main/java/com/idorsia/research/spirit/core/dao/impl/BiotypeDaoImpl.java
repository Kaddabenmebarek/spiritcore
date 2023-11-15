package com.idorsia.research.spirit.core.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.dao.AbstractDao;
import com.idorsia.research.spirit.core.dao.BiotypeDao;
import com.idorsia.research.spirit.core.dto.BiotypeDto;
import com.idorsia.research.spirit.core.dto.BiotypeMetadataDto;
import com.idorsia.research.spirit.core.dto.StudyDto;
import com.idorsia.research.spirit.core.model.Biotype;

@Repository
public class BiotypeDaoImpl extends AbstractDao<Biotype> implements BiotypeDao {

	private static final String TABLE_NAME = "Biotype";
	
	@Override
	public Biotype get(Integer id) {
		return super.get(TABLE_NAME, Biotype.class, id);
	}
	
	public Biotype getByName(String name) {
		String sql = String.format("SELECT * FROM " + TABLE_NAME + " WHERE NAME = '%s'", name);
		return super.getObject(TABLE_NAME, Biotype.class, sql);
	}
	
	public List<Biotype> getByParentId(Integer parentId){
		String sql = String.format("SELECT * FROM " + TABLE_NAME + " WHERE PARENT_ID = %s", parentId);
		return (List<Biotype>) super.getObjectList(TABLE_NAME, Biotype.class, sql);
	}


	@Override
	public List<Biotype> list() {
		return super.getlist(TABLE_NAME, Biotype.class);
	}

	@Override
	public int getCount() {
		return super.getCount(TABLE_NAME);
	}

	@Override
	public Integer saveOrUpdate(Biotype biotype) {
		if (biotype.getId() != null && biotype.getId() > 0) {
			if(!biotype.equals(get(biotype.getId()))) {
				String sql = "UPDATE BIOTYPE SET "
						+ "creDate=:creDate, " 
						+ "updDate=:updDate, "
						+ "creUser=:creUser, "
						+ "updUser=:updUser, "
						+ "parent_Id=:parentId, "
						+ "category=:parentId, "
						+ "description=:description, "
						+ "name=:name, "
						+ "prefix=:prefix, "
						+ "amountUnit=:amountUnit, "
						+ "containerType=:containerType, "
						+ "nameLabel=:nameLabel, "
						+ "isHidden=:isHidden, "
						+ "isAbstract=:isAbstract, "
						+ "nameAutoComplete=:nameAutoComplete, "
						+ "nameRequired=:nameRequired, "
						+ "hideSampleId=:hideSampleId, "
						+ "hideContainer=:hideContainer, "
						+ "nameUnique=:nameUnique "
						+ "WHERE id=:id";
			    SqlParameterSource namedParameters = new MapSqlParameterSource()
			    		.addValue("id", biotype.getId())
			    		.addValue("parentId", biotype.getParentId())
			    		.addValue("creDate", biotype.getCreDate())
			    		.addValue("updDate", biotype.getUpdDate())
			    		.addValue("creUser", biotype.getCreUser())
			    		.addValue("updUser", biotype.getUpdUser())
			    		.addValue("category", biotype.getCategory())
			    		.addValue("description", biotype.getDescription())
			    		.addValue("name", biotype.getName())
			    		.addValue("prefix", biotype.getPrefix())
			    		.addValue("amountUnit", biotype.getAmountUnit())
			    		.addValue("containerType", biotype.getContainerType())
			    		.addValue("nameLabel", biotype.getNameLabel())
			    		.addValue("isHidden", biotype.getIsHidden())
			    		.addValue("isAbstract", biotype.getIsAbstract())
			    		.addValue("nameAutoComplete", biotype.getNameAutoComplete())
			    		.addValue("nameRequired", biotype.getNameRequired())
			    		.addValue("hideSampleId", biotype.getHideSampleId())
			    		.addValue("hideContainer", biotype.getHideContainer())
			    		.addValue("nameUnique", biotype.getNameUnique());
			    super.saveOrUpdate(sql, namedParameters);
			}
		} else {
			biotype.setId(getSequence(Constants.BIOTYPE_SEQUENCE_NAME));
			addBiotype(biotype);
			addTransient(biotype);
		}
		addIdToObject(Biotype.class, biotype.getId(), biotype);
		return biotype.getId();
	}

	@Override
	public int addBiotype(Biotype biotype) {
		return super.add(TABLE_NAME, biotype);
	}

	@Override
	public void delete(int biotypeId) {
		super.delete(TABLE_NAME, biotypeId);
	}


	@Override
	public List<String> getAutoCompletionFieldsForName(BiotypeDto biotype, StudyDto study) {
		StringBuilder query = new StringBuilder(String.format("SELECT distinct localid FROM BIOSAMPLE b WHERE b.BIOTYPE_ID = %s AND length(b.localId)>0 ", biotype.getId()));
		if(study != null) {
			query.append(" AND b.STUDY_ID = ").append(study.getId());
		}
		return getJdbcTemplate().query(query.toString(), new ResultSetExtractor<List<String>>() {
			public List<String> extractData(ResultSet rs) throws SQLException, DataAccessException {
				List<String> s = new ArrayList<String>();
				while (rs.next()) {
					s.add(rs.getString(1));
				}
				return s;
			}
		});
	}

	@Override
	public List<String> getAutoCompletionFields(BiotypeMetadataDto metadataType, StudyDto study) {
		StringBuilder sql = new StringBuilder("SELECT distinct (bm.value) FROM biotype_metadata_biosample bm, biosample b where bm.biosample_id = b.id and bm.metadata_id = ").append(metadataType.getId());
		if(study!=null) {
			sql.append(" and b.study_id = ").append(study.getId());
		}
		return getJdbcTemplate().query(sql.toString(), new ResultSetExtractor<List<String>>() {
			public List<String> extractData(ResultSet rs) throws SQLException, DataAccessException {
				List<String> s = new ArrayList<String>();
				while (rs.next()) {
					s.add(rs.getString(1));
				}
				return s;
			}
		});
	}

	@Override
	public List<String> getAutoCompletionFieldsForSampleId(BiotypeDto biotype, BiotypeMetadataDto fromAgregated,
			StudyDto study) {
		String query = "select distinct(b.sampleid) from biosample b, biosample_biosample bb where b.id = bb.linked_biosample_id and b.biotype_id = "+biotype.getId()+" and bb.biotypemetadata_id = "+fromAgregated.getId();
		if(study != null) {
			query += " and b.study_id = " + study.getId();
		}
		return getJdbcTemplate().query(query.toString(), new ResultSetExtractor<List<String>>() {
			public List<String> extractData(ResultSet rs) throws SQLException, DataAccessException {
				List<String> s = new ArrayList<String>();
				while (rs.next()) {
					s.add(rs.getString(1));
				}
				return s;
			}
		});
	}

	@Override
	public int countRelation(int id) {
		String query = String.format("select count(*) from Biosample where biotype_id = %s", id);
		return getJdbcTemplate().queryForObject(query, Integer.class);
	}

	@Override
	public List<Biotype> getBiotypes(String studyId, Set<Integer> assayIds) {
		StringBuilder query = new StringBuilder("select bt.* from Biotype bt, Biosample b, Study s, Assay_Result r ");
		query.append("where b.biotype_id = bt.id ");
		query.append("and r.biosample_id = b.id ");
		query.append("and r.study_id = s.id").append(" and s.studyid = '").append(studyId).append("' ");
		if(assayIds!=null && assayIds.size()>0) {
			query.append(" and (");
			boolean first = true;
			for (int testId : assayIds) {
				if(first) 
					first = false; 
				else 
					query.append(" or ");
				query.append(" r.assay_id = "+testId);
			}
			query.append(")");
		}
		return (List<Biotype>) super.getObjectList(TABLE_NAME, Biotype.class, query.toString());
	}
	
	
}
