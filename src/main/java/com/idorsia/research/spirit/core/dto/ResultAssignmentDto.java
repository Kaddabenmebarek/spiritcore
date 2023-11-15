package com.idorsia.research.spirit.core.dto;

import java.io.Serializable;
import java.util.Date;

import com.idorsia.research.spirit.core.constants.Constants;

public class ResultAssignmentDto implements IObject, Serializable{
	
	private static final long serialVersionUID = 6625063644325761953L;
	private Integer id = Constants.NEWTRANSIENTID;
	private AssignmentDto assignment;
	private AssayResultDto assayResult;
	private Date creDate;
	private Date updDate;
	private String updUser;
	private String creUser;
	
	public ResultAssignmentDto() {
	}
	
	public ResultAssignmentDto(AssayResultDto result) {
		this.assayResult = result;
	}

	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public AssignmentDto getAssignment() {
		return assignment;
	}
	/**Do not call this method directly but call the service instead 
	 *ResultAssignmentService.setAssignment(this, assignment);
	 */
	@Deprecated
	public void setAssignment(AssignmentDto assignment) {
		this.assignment = assignment;
	}
	public AssayResultDto getAssayResult() {
		return assayResult;
	}
	public void setAssayResult(AssayResultDto assayResult) {
		this.assayResult = assayResult;
	}
	public Date getCreDate() {
		return creDate;
	}
	public void setCreDate(Date creDate) {
		this.creDate = creDate;
	}
	public Date getUpdDate() {
		return updDate;
	}
	public void setUpdDate(Date updDate) {
		this.updDate = updDate;
	}
	public String getUpdUser() {
		return updUser;
	}
	public void setUpdUser(String updUser) {
		this.updUser = updUser;
	}
	public String getCreUser() {
		return creUser;
	}
	public void setCreUser(String creUser) {
		this.creUser = creUser;
	}
}
