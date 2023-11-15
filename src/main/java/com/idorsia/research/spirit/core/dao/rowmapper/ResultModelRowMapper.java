package com.idorsia.research.spirit.core.dao.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.idorsia.research.spirit.core.model.ResultsModel;

public class ResultModelRowMapper implements RowMapper<ResultsModel> {

	@Override
	public ResultsModel mapRow(ResultSet rs, int rowNum) throws SQLException {
		ResultsModel resultModel = new ResultsModel();
		resultModel.setStudyId(rs.getInt("STUDY_ID"));
		resultModel.setAssayResultId(rs.getInt("ASSAY_RESULT_ID"));
		resultModel.setBioSampleId(rs.getInt("BIOSAMPLE_ID"));
		//resultModel.setBioSampleName(rs.getString("BIOSAMPLE_NAME"));
		//resultModel.setBiosampleAssignmentName(rs.getString("ASSIGNMENT_NAME"));
		resultModel.setPhaseId(rs.getInt("PHASE_ID"));
		resultModel.setResultTextValue(rs.getString("RESULT_TEXT_VALUE"));
		resultModel.setResultNumericValue(rs.getLong("RESULT_NUMERIC_VALUE"));
		resultModel.setAssayAttributName(rs.getString("ATTRIBUTE_NAME"));
		resultModel.setAssayCategory(rs.getString("ASSAY_CATEGORY"));
		resultModel.setAssayName(rs.getString("ASSAY_NAME"));
		resultModel.setAttributeDatatype(rs.getString("DATATYPE"));

        return resultModel;
	}

}
