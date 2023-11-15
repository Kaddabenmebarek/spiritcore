package com.idorsia.research.spirit.core.dao.impl;

import java.io.ByteArrayInputStream;
import java.sql.Types;
import java.util.List;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.support.SqlLobValue;
import org.springframework.jdbc.support.lob.DefaultLobHandler;
import org.springframework.stereotype.Repository;

import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.dao.AbstractDao;
import com.idorsia.research.spirit.core.dao.DocumentDao;
import com.idorsia.research.spirit.core.model.Document;

@Repository
public class DocumentDaoImpl extends AbstractDao<Document> implements DocumentDao {

	private static final String TABLE_NAME = "Document";
	
	@Override
	public Document get(Integer id) {
		return super.get(TABLE_NAME, Document.class, id);
	}

	@Override
	public List<Document> list() {
		return super.getlist(TABLE_NAME, Document.class);
	}

	@Override
	public int getCount() {
		return super.getCount(TABLE_NAME);
	}

	@Override
	public Integer saveOrUpdate(Document document) {
		if (document.getId() != null && document.getId() > 0) {
			if(!document.equals(get(document.getId()))) {
				String sql = "UPDATE "+TABLE_NAME+" SET "
						+ "TYPE=:type, " 
						+ "BYTES=:bytes, " 
						+ "FILENAME=:fileName, "
						+ "UPDDATE=:upddate, "
						+ "UPDUSER=:upduser, "
						+ "CREDATE=:credate, "
						+ "CREUSER=:creuser "
						+ "WHERE ID=:id";
			    SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("id", document.getId())
			    		.addValue("type", document.getType())
			    		.addValue("bytes", document.getBytes())
			    		.addValue("fileName", document.getFilename())
			    		.addValue("upddate", document.getUpdDate())
			    		.addValue("upduser", document.getUpdUser())
			    		.addValue("credate", document.getCreDate())
			    		.addValue("creuser", document.getCreUser());
			    super.saveOrUpdate(sql, namedParameters);
			}
		} else {
			document.setId(getSequence(Constants.DOCUMENT_SEQUENCE_NAME));
			addDocument(document);
			addTransient(document);
		}
		addIdToObject(Document.class, document.getId(), document);
		return document.getId();
	}
	
	public void addDocument(Document document) {

		MapSqlParameterSource in = new MapSqlParameterSource();
		in.addValue("id", document.getId());
		in.addValue("type", document.getType());
		in.addValue("filename", document.getFilename());
		in.addValue("updUser", document.getUpdUser());
		in.addValue("updDate", document.getUpdDate());
		in.addValue("creUser", document.getCreUser());
		in.addValue("creDate", document.getCreDate());
		in.addValue("bytes", new SqlLobValue(new ByteArrayInputStream(document.getBytes()), document.getBytes().length,
				new DefaultLobHandler()), Types.BLOB);

		String SQL = "Insert into DOCUMENT (ID,CREDATE,CREUSER,FILENAME,TYPE,UPDUSER,UPDDATE, BYTES) values(:id, :creDate, :creUser, :filename, :type , null, null, :bytes)";

		NamedParameterJdbcTemplate jdbcTemplateObject = new NamedParameterJdbcTemplate(
				getJdbcTemplate().getDataSource());

		jdbcTemplateObject.update(SQL, in);
	}

	@Override
	public void delete(int documentId) {
		super.delete(TABLE_NAME, documentId);
	}

}
