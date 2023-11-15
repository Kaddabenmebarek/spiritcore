package com.idorsia.research.spirit.core.dao.impl;

import java.util.Date;
import java.util.List;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.dao.AbstractDao;
import com.idorsia.research.spirit.core.dao.LinkedDocumentDao;
import com.idorsia.research.spirit.core.model.LinkedDocument;
import com.idorsia.research.spirit.core.util.UserUtil;

@Repository
public class LinkedDocumentDaoImpl extends AbstractDao<LinkedDocument> implements LinkedDocumentDao {

	private static String TABLE_NAME = "BIOSAMPLE_DOCUMENT";

	@Override
	public LinkedDocument get(Integer id) {
		String sql = String.format("SELECT * FROM %s WHERE ID = %s", TABLE_NAME, id);
		return super.getObject(TABLE_NAME, LinkedDocument.class, sql);
	}

	@Override
	public int addLinkedDocument(LinkedDocument linkedDocument) {
		try {
			return super.add(TABLE_NAME, linkedDocument);
		} catch (Exception e) {
			// if already exists
			return 0;
		}
	}

	@Override
	public void delete(Integer linkedDocument) {
		String sql = String.format("DELETE FROM %s WHERE ID = %s", TABLE_NAME, linkedDocument);
		getJdbcTemplate().execute(sql);
	}

	@Override
	public List<LinkedDocument> getLinkedDocumentsByBiosample(Integer biosampleId) {
		String sql = String.format("SELECT * FROM " + TABLE_NAME + " WHERE BIOSAMPLE_ID=%s", biosampleId);
		return (List<LinkedDocument>) super.getObjectList(TABLE_NAME, LinkedDocument.class, sql);
	}

	@Override
	public List<LinkedDocument> getLinkedDocumentsByLinkedDocument(Integer linkedBiosampleId) {
		String sql = String.format("SELECT * FROM " + TABLE_NAME + " WHERE LINKEDDOCUMENT_ID=%s", linkedBiosampleId);
		return (List<LinkedDocument>) super.getObjectList(TABLE_NAME, LinkedDocument.class, sql);
	}

	@Override
	public List<LinkedDocument> getLinkedDocumentsByMetadata(Integer metadataId) {
		String sql = String.format("SELECT * FROM " + TABLE_NAME + " WHERE BIOTYPEMETADATA_ID=%s", metadataId);
		return (List<LinkedDocument>) super.getObjectList(TABLE_NAME, LinkedDocument.class, sql);
	}

	@Override
	public Integer saveOrUpdate(LinkedDocument linkedDocument) {
		linkedDocument.setUpdDate(new Date());
		linkedDocument.setUpdUser(UserUtil.getUsername());
		if (linkedDocument.getId() != null && linkedDocument.getId() > 0) {
			if(!linkedDocument.equals(get(linkedDocument.getId()))) {
				String sql = "UPDATE "+TABLE_NAME+" SET "
						+ "BIOSAMPLE_ID=:biosampleId, "
						+ "BIOTYPEMETADATA_ID=:biotypeMetadatId, "
						+ "CREDATE=:creDate, "
						+ "CREUSER=:creUser, "
						+ "UPDDATE=:updDate, "
						+ "UPDUSER=:updUser, "
						+ "LINKEDDOCUMENT_ID=linkedDocumentId"
						+ "WHERE id=:id";
			    SqlParameterSource namedParameters = new MapSqlParameterSource()
			    		.addValue("id", linkedDocument.getId())
			    		.addValue("biosampleId", linkedDocument.getBiosampleId())
			    		.addValue("biotypeMetadatId", linkedDocument.getBiotypemetadataId())
						.addValue("updDate", linkedDocument.getUpdDate())
						.addValue("updUser", linkedDocument.getUpdUser())
						.addValue("creDate", linkedDocument.getCreDate())
						.addValue("creUser", linkedDocument.getCreUser())
			    		.addValue("linkedDocumentId", linkedDocument.getLinkeddocumentId());
			    super.saveOrUpdate(sql, namedParameters);
			}
		} else {
			linkedDocument.setCreDate(new Date());
			linkedDocument.setCreUser(UserUtil.getUsername());
			linkedDocument.setId(getSequence(Constants.SUBGROUP_PATTERN_SEQUENCE_NAME));
			addLinkedDocument(linkedDocument);
			addTransient(linkedDocument);
		}
		addIdToObject(LinkedDocument.class, linkedDocument.getId(), linkedDocument);
		return linkedDocument.getId();
	}
}
