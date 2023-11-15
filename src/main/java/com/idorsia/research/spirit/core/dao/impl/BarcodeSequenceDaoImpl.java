package com.idorsia.research.spirit.core.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.dao.AbstractDao;
import com.idorsia.research.spirit.core.dao.BarcodeSequenceDao;
import com.idorsia.research.spirit.core.model.BarcodeSequence;

@Repository
public class BarcodeSequenceDaoImpl extends AbstractDao<BarcodeSequence> implements BarcodeSequenceDao {

	@Override
	public String getLastBiosampleBarcode(String pattern) {
		StringBuilder query = new StringBuilder("SELECT MAX(sampleid) FROM biosample WHERE sampleid LIKE '");
				query.append(pattern.replaceAll("\\#+", "%")).append("'");
				query.append(" AND (sampleid NOT LIKE '").append(pattern.replaceAll("\\#+", "%-%")).append("')");
				query.append(" AND (sampleid NOT LIKE '").append(pattern.replaceAll("\\#+", "%/%")).append("')");
				query.append(" AND (sampleid NOT LIKE '").append(pattern.replaceAll("\\#+", "%.%")).append("')");
				query.append(" AND length(sampleid) >= ").append(pattern.length());
		
		return getJdbcTemplate().query(query.toString(), new ResultSetExtractor<String>() {
			public String extractData(ResultSet rs) throws SQLException, DataAccessException {
				String s = null;
				while (rs.next()) {
					s = rs.getString(1);
				}
				return s;
			}
		});
	}

	@Override
	public String getLastLocationBarcode(String pattern) {
		StringBuilder query = new StringBuilder("select max(l.name) from biolocation l where l.name like '");
				query.append(pattern.replaceAll("\\#+", "%"));
				query.append("' and length(l.name) = (select max(length(l.name)) from biolocation l where l.name like '");
				query.append(pattern.replaceAll("\\#+", "%"));
				query.append("')");
		
		return getJdbcTemplate().query(query.toString(), new ResultSetExtractor<String>() {
			public String extractData(ResultSet rs) throws SQLException, DataAccessException {
				String s = null;
				while (rs.next()) {
					s = rs.getString(1);
				}
				return s;
			}
		});
	}

	@Override
	public String getLastContainerBarcode(String pattern) {
		StringBuilder query = new StringBuilder("select max(b.containerId) from Biosample b where b.containerId like '");
		query.append(pattern.replaceAll("\\#+", "%").replaceAll("-N", "")).append("'");
		query.append(" and length(b.containerId)>=").append(pattern.length());
		
		return getJdbcTemplate().query(query.toString(), new ResultSetExtractor<String>() {
			public String extractData(ResultSet rs) throws SQLException, DataAccessException {
				String s = null;
				while (rs.next()) {
					s = rs.getString(1);
				}
				return s;
			}
		});
	}

	@Override
	public List<BarcodeSequence> getBarcodeSequencesByTypeAndCategory(String type, Integer category) {
		String sql = String.format("select * from Barcode where type = '%s' and category = %s", type, category);
		return getJdbcTemplate().query(sql, BeanPropertyRowMapper.newInstance(BarcodeSequence.class));
	}

	@Override
	public Integer saveOrUpdate(BarcodeSequence barcodeSequence) {
		if (barcodeSequence.getId() != null && barcodeSequence.getId() > 0) {
			String sql = "UPDATE BARCODE SET " 
					+ "category=:category, " 
					+ "type=:type, " 
					+ "lastbarcode=:lastbarcode, " 
					+ "WHERE id=:id";
		    SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("id", barcodeSequence.getId())
		    		.addValue("category", barcodeSequence.getCategory())
		    		.addValue("type", barcodeSequence.getType())
		    		.addValue("lastbarcode", barcodeSequence.getLastBarcode());
		    super.saveOrUpdate(sql, namedParameters);
		} else {
			barcodeSequence.setId(getSequence(Constants.BARCODE_SEQUENCE_NAME));
			addBarcodeSequence(barcodeSequence);
		}
		return barcodeSequence.getId();
	}

	@Override
	public int addBarcodeSequence(BarcodeSequence barcodeSequence) {
		return super.add("BARCODE", barcodeSequence);
	}
	
	@Override
	public void delete(int barcodeId) {
		super.delete("Barcode", barcodeId);
	}

}
