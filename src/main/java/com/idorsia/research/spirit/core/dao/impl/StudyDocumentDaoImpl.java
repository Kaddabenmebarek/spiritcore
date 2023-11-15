package com.idorsia.research.spirit.core.dao.impl;

import java.util.Date;
import java.util.List;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.dao.AbstractDao;
import com.idorsia.research.spirit.core.dao.StudyDocumentDao;
import com.idorsia.research.spirit.core.dto.StudyDocumentDto;
import com.idorsia.research.spirit.core.model.StudyDocument;
import com.idorsia.research.spirit.core.util.UserUtil;

@Repository
public class StudyDocumentDaoImpl extends AbstractDao<StudyDocument> implements StudyDocumentDao {

	private static String TABLE_NAME = "STUDY_DOCUMENT";

	@Override
	public StudyDocument get(Integer id) {
		String sql = String.format("SELECT * FROM %s WHERE ID = %s", TABLE_NAME, id);
		return super.getObject(TABLE_NAME, StudyDocument.class, sql);
	}

	@Override
	public int addStudyDocument(StudyDocument studyDocument) {
		try {
			return super.add(TABLE_NAME, studyDocument);
		} catch (Exception e) {
			// if already exists
			return 0;
		}
	}

	@Override
	public void delete(StudyDocumentDto studyDocument) {
		String sql = String.format("DELETE FROM %s WHERE ID = %s", TABLE_NAME, studyDocument.getId());
		getJdbcTemplate().execute(sql);
	}

	@Override
	public StudyDocument getByDocumentId(Integer documentId) {
		String sql = String.format("SELECT * FROM " + TABLE_NAME + " WHERE DOCUMENT_ID=%s", documentId);
		return super.getObject(TABLE_NAME, StudyDocument.class, sql);
	}

	@Override
	public List<StudyDocument> getByStudyId(Integer studyId) {
		String sql = String.format("SELECT * FROM " + TABLE_NAME + " WHERE STUDY_ID=%s", studyId);
		return (List<StudyDocument>) super.getObjectList(TABLE_NAME, StudyDocument.class, sql);
	}

	@Override
	public Integer saveOrUpdate(StudyDocument studyDocument) {
		studyDocument.setUpdDate(new Date());
		studyDocument.setUpdUser(UserUtil.getUsername());
		if (studyDocument.getId() != null && studyDocument.getId() > 0) {
			if(!studyDocument.equals(get(studyDocument.getId()))) {
				String sql = "UPDATE "+TABLE_NAME+" SET "
						+ "DOCUMENTS_ID=:documentsId, "
						+ "CREDATE=:creDate, "
						+ "CREUSER=:creUser, "
						+ "UPDDATE=:updDate, "
						+ "UPDUSER=:updUser, "
						+ "STUDY_ID=:studyId "
						+ "WHERE id=:id";
			    SqlParameterSource namedParameters = new MapSqlParameterSource()
			    		.addValue("id", studyDocument.getId())
			    		.addValue("documentsId", studyDocument.getDocumentsId())
						.addValue("upddate", studyDocument.getUpdDate())
						.addValue("upduser", studyDocument.getUpdUser())
						.addValue("credate", studyDocument.getCreDate())
						.addValue("creuser", studyDocument.getCreUser())
			    		.addValue("studyId", studyDocument.getStudyId());
			    super.saveOrUpdate(sql, namedParameters);
			}
		} else {
			studyDocument.setCreDate(new Date());
			studyDocument.setCreUser(UserUtil.getUsername());
			studyDocument.setId(getSequence(Constants.SUBGROUP_PATTERN_SEQUENCE_NAME));
			addStudyDocument(studyDocument);
			addTransient(studyDocument);
		}
		addIdToObject(StudyDocument.class, studyDocument.getId(), studyDocument);
		return studyDocument.getId();
	}
	
}
