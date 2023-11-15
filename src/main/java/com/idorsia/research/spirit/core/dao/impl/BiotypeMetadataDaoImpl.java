package com.idorsia.research.spirit.core.dao.impl;

import java.util.List;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.dao.AbstractDao;
import com.idorsia.research.spirit.core.dao.BiotypeMetadataDao;
import com.idorsia.research.spirit.core.model.BiotypeMetadata;

@Repository
public class BiotypeMetadataDaoImpl extends AbstractDao<BiotypeMetadata> implements BiotypeMetadataDao {

	private static final String TABLE_NAME = "BIOTYPE_METADATA";

	@Override
	public BiotypeMetadata get(Integer id) {
		return super.get(TABLE_NAME, BiotypeMetadata.class, id);
	}

	@Override
	public List<BiotypeMetadata> getByBiotype(int biotypeId) {
		String sql = String.format("SELECT * FROM " + TABLE_NAME + " WHERE BIOTYPE_ID = '%s'", biotypeId);
		return (List<BiotypeMetadata>) super.getObjectList(TABLE_NAME, BiotypeMetadata.class, sql);
	}
	
	@Override
	public List<BiotypeMetadata> list() {
		return super.getlist(TABLE_NAME, BiotypeMetadata.class);
	}

	@Override
	public int getCount() {
		return super.getCount(TABLE_NAME);
	}

	@Override
	public Integer saveOrUpdate(BiotypeMetadata biotypeMetadata) {
		if (biotypeMetadata.getId() != null && biotypeMetadata.getId() > 0) {
			if(!biotypeMetadata.equals(get(biotypeMetadata.getId()))) {
				String sql = "UPDATE " + TABLE_NAME + " SET " 
					+ "IDX=:idx, " 
					+ "NAME=:name, " 
					+ "PARAMETERS=:parameters, "
					+ "REQUIRED=:required, " 
					+ "BIOTYPE_ID=:biotypeId, " 
					+ "DESCRIPTION=:description, "
					+ "DATATYPE=:dataType, " 
					+ "HIDEFROMDISPLAY=:hideFromDisplay, "
					+ "UPDDATE=:upddate, "
					+ "UPDUSER=:upduser, "
					+ "CREDATE=:credate, " 
					+ "CREUSER=:creuser " 
					+ "WHERE ID=:id";
				SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("id", biotypeMetadata.getId())
						.addValue("idx", biotypeMetadata.getIdx())
						.addValue("name", biotypeMetadata.getName())
						.addValue("parameters", biotypeMetadata.getParameters())
						.addValue("required", biotypeMetadata.getRequired())
						.addValue("biotypeId", biotypeMetadata.getBiotypeId())
						.addValue("description", biotypeMetadata.getDescription())
						.addValue("dataType", biotypeMetadata.getDatatype())
						.addValue("hideFromDisplay", biotypeMetadata.getHidefromdisplay())
						.addValue("upddate", biotypeMetadata.getUpdDate())
						.addValue("upduser", biotypeMetadata.getUpdUser())
						.addValue("credate", biotypeMetadata.getCreDate())
						.addValue("creuser", biotypeMetadata.getCreUser());
				super.saveOrUpdate(sql, namedParameters);
			}
		} else {
			biotypeMetadata.setId(getSequence(Constants.BIOTYPE_METADATA_SEQUENCE_NAME));
			addBiotypeMetadata(biotypeMetadata);
			addTransient(biotypeMetadata);
		}
		addIdToObject(BiotypeMetadata.class, biotypeMetadata.getId(), biotypeMetadata);
		return biotypeMetadata.getId();
	}

	@Override
	public int addBiotypeMetadata(BiotypeMetadata biotypeMetadata) {
		return super.add(TABLE_NAME, biotypeMetadata);
	}

	@Override
	public void delete(int biotypeMetadataId) {
		super.delete(TABLE_NAME, biotypeMetadataId);
	}

	@Override
	public int countRelations(int id) {
		StringBuilder query = new StringBuilder("select count(biosample_id) from Biotype_Metadata_Biosample");
		query.append(" where metadata_id = ").append(id);
		return getJdbcTemplate().queryForObject(query.toString(), Integer.class);
	}

}
