package com.idorsia.research.spirit.core.dto;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.actelion.research.util.CompareUtils;
import com.idorsia.research.spirit.core.conf.ContextShare;
import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.constants.Quality;
import com.idorsia.research.spirit.core.dto.view.Execution;
import com.idorsia.research.spirit.core.dto.view.StudyActionResult;
import com.idorsia.research.spirit.core.service.AssayResultService;
import com.idorsia.research.spirit.core.util.UserUtil;

@Component
public class AssayResultDto implements IObject, Comparable<AssayResultDto>, StudyActionResult, Serializable {

	private static final long serialVersionUID = 5751360415301594692L;
	@Autowired
	private AssayResultService assayResultService;
	private Integer id=Constants.NEWTRANSIENTID;
	private String comments;
	private Date creDate = new Date();
	private String creUser = UserUtil.getUsername();
	private String elb;
	private Quality quality;
	private Date updDate = new Date();
	private String updUser = UserUtil.getUsername();
	private BiosampleDto biosample;
	private PhaseDto phase;
	private AssayDto assay;
	private StudyDto study;
	private Execution execution;
	private ResultAssignmentDto resultAssignment;
	private List<AssayResultValueDto> values;

	public AssayResultDto() {
		super();
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public Date getCreDate() {
		return creDate;
	}

	public void setCreDate(Date creDate) {
		this.creDate = creDate;
	}

	public String getCreUser() {
		return creUser;
	}

	public void setCreUser(String creUser) {
		this.creUser = creUser;
	}

	public String getElb() {
		return elb;
	}

	public void setElb(String elb) {
		this.elb = elb;
	}

	public Quality getQuality() {
		return quality;
	}

	public void setQuality(Quality quality) {
		this.quality = quality;
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

	public BiosampleDto getBiosample() {
		return biosample;
	}

	/**Do not call this method directly but call the service instead 
	 *AssayResultService.setBiosample(this, biosample);
	 */
	@Deprecated
	public void setBiosample(BiosampleDto biosample) {
		this.biosample = biosample;
	}

	public PhaseDto getPhase() {
		return phase;
	}

	/**Do not call this method directly but call the service instead 
	 *AssayResultService.setPhase(this, phase);
	 */
	@Deprecated
	public void setPhase(PhaseDto phase) {
		this.phase = phase;
	}

	public AssayDto getAssay() {
		return assay;
	}

	/**Do not call this method directly but call the service instead 
	 *AssayResultService.setAssay(this, assay);
	 */
	@Deprecated
	public void setAssay(AssayDto assay) {
		this.assay = assay;
	}

	public StudyDto getStudy() {
		return study;
	}

	public void setStudy(StudyDto study) {
		this.study = study;
	}

	public Execution getExecution() {
		return execution;
	}

	public void setExecution(Execution executionDate) {
		this.execution = executionDate;
	}

	public List<AssayResultValueDto> getValues() {
		if(values == null) {
			getAssayResultService().mapValues(this);
		}
		return values;
	}

	@Deprecated
	public List<AssayResultValueDto> getValuesNoMapping() {
		return values;
	}
	
	public void setValues(List<AssayResultValueDto> values) {
		Collections.sort(values);
		this.values = values;
	}

	public ResultAssignmentDto getResultAssignment() {
		return resultAssignment;
	}

	public void setResultAssignment(ResultAssignmentDto resultAssignment) {
		this.resultAssignment = resultAssignment;
	}

	@Override
	public int compareTo(AssayResultDto r) {
		int c;
		c = CompareUtils.compare(getAssay(), r.getAssay());
		if(c!=0) return c;

		c = CompareUtils.compare(getBiosample(), r.getBiosample());
		if(c!=0) return c;

		c = CompareUtils.compare(getPhase(), r.getPhase());
		if(c!=0) return c;

		c = CompareUtils.compare(getElb(), r.getElb());
		if(c!=0) return c;

		c = CompareUtils.compare(getUpdDate(), r.getUpdDate());

		return c;
	}

	public AssayResultService getAssayResultService() {
		if(assayResultService == null) {
			assayResultService = (AssayResultService) ContextShare.getContext().getBean("assayResultService");
		}
		return assayResultService;
	}
	
	@Override
	public String toString() {
		 return "[Result:"
				+ " biosample=" + biosample
				+ " test=" + assay
				+ (getPhase()!=null? " phase=" + getPhase(): "")
				+ (getAssayResultService().getInputResultValuesAsString(this).length()>0? " input=" + getAssayResultService().getInputResultValuesAsString(this): "")
				+ " output=" + getAssayResultService().getOutputResultValuesAsString(this)+"]";
	}
}
