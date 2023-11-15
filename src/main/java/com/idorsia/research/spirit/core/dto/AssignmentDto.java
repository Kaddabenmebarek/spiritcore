package com.idorsia.research.spirit.core.dto;

import java.io.Serializable;
import java.time.Duration;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.idorsia.research.spirit.core.conf.ContextShare;
import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.dto.view.StudyActionResult;
import com.idorsia.research.spirit.core.service.AssignmentService;
import com.idorsia.research.spirit.core.util.MiscUtils;

@Component
public class AssignmentDto implements IObject, Comparable<AssignmentDto>, StudyActionResult, Serializable {

	private static final long serialVersionUID = 847173460782640523L;
	@Autowired
	private AssignmentService assignmentService;
	private Integer id = Constants.NEWTRANSIENTID;
	private Integer no;
	private StageDto stage;
	private BiosampleDto biosample;
	private SubGroupDto subgroup;
	private String name;
	private String elb;
	private String datalist;
	private Duration stratification;
	private Date creDate;
	private String creUser;
	private Date updDate;
	private String updUser;
	private Date removeDate;
	private List<ExecutionDetailDto> executionDetails;
	private Set<AssignmentPatternDto> definitionActionPatterns;
	private List<ResultAssignmentDto> resultAssignments;
	private List<Double> dataListList = null;
	private Boolean skipRando = false;
	private Double tmpWeight;
	
	public AssignmentDto() {
	}

	public AssignmentDto(AssignmentDto assignment) {
		this.no=assignment.getNo();
		this.name=assignment.getName();
		this.elb=assignment.getElb();
		this.datalist=assignment.getDatalist();
		this.stratification=assignment.getStratification();
	}

	public AssignmentDto(String stageNextName, int no, Duration offset) {
		this.no = no;
		this.stratification = offset;
		if (stage!=null) {
			this.name = stageNextName;
		}
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getNo() {
		return no;
	}

	public void setNo(Integer no) {
		this.no = no;
	}

	public StageDto getStage() {
		return stage;
	}

	/**Do not call this method directly but call the service instead 
	 *AssignmentService.setStage(this, stage);
	 */
	@Deprecated
	public void setStage(StageDto stage) {
		this.stage = stage;
	}

	public BiosampleDto getBiosample() {
		return biosample;
	}

	/**Do not call this method directly but call the service instead 
	 *AssignmentService.setBiosample(this, biosample);
	 */
	@Deprecated
	public void setBiosample(BiosampleDto biosample) {
		this.biosample = biosample;
	}

	public SubGroupDto getSubgroup() {
		return subgroup;
	}

	/**Do not call this method directly but call the service instead 
	 *AssignmentService.setSubgroup(this, subgroup);
	 */
	@Deprecated
	public void setSubgroup(SubGroupDto subgroup) {
		this.subgroup = subgroup;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getElb() {
		return elb;
	}

	public void setElb(String elb) {
		this.elb = elb;
	}

	public String getDatalist() {
		return datalist;
	}
	
	public List<Double> getDataListList() {
		if (dataListList == null) {
			dataListList = MiscUtils.deserializeDataList(datalist);
		}
		return dataListList;
	}
	
	public void setDataListList(List<Double> dataListList) {
		this.dataListList = dataListList;
		this.datalist = MiscUtils.serializeDataList(dataListList);
	}

	public void setDatalist(String datalist) {
		this.datalist = datalist;
		this.dataListList=null;
	}

	public Duration getStratification() {
		return stratification;
	}

	/**Do not call this method directly but call the service instead 
	 *AssignmentService.setStratification(this, stratification);
	 */
	@Deprecated
	public void setStratification(Duration stratification) {
		this.stratification = stratification;
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

	public Date getRemoveDate() {
		return removeDate;
	}

	/**Do not call this method directly but call the service instead 
	 *AssignmentService.setRemoveDate(this, Date);
	 */
	@Deprecated
	public void setRemoveDate(Date removeDate) {
		this.removeDate = removeDate;
	}

	@Override
	public int compareTo(AssignmentDto o) {
		return this.getNo() - o.getNo();
	}

	public List<ExecutionDetailDto> getExecutionDetails() {
		if(executionDetails == null) {
			getAssignmentService().mapExecutionDetails(this);
		}
		return this.executionDetails;
	}
	
	@Deprecated
	public List<ExecutionDetailDto> getExecutionDetailsNoMapping() {
		return this.executionDetails;
	}
	
	/**Do not call this method directly but call the service instead 
	 *AssignmentService.addExecutionDetail(this, executionDetail)/removeExecutionDetail(this, executionDetail);
	 */
	@Deprecated
	public void setExecutionDetails(List<ExecutionDetailDto> executionDetails) {
		this.executionDetails=executionDetails;
	}

	public Set<AssignmentPatternDto> getActionDefinition() {
		if(definitionActionPatterns == null) {
			getAssignmentService().mapActionDefinition(this);
		}
		return this.definitionActionPatterns;
	}
	
	@Deprecated
	public Set<AssignmentPatternDto> getActionDefinitionNoMapping() {
		return this.definitionActionPatterns;
	}
	
	public Set<ActionPatternsDto> getActionDefinitionPattern() {
		Set<ActionPatternsDto> patterns = new HashSet<>();
		for(AssignmentPatternDto pattern : getActionDefinition()) {
			patterns.add(pattern.getActionpattern());
		}
		return patterns;
	}

	public void setActionDefinition(Set<AssignmentPatternDto> definitionActionPatterns) {
		this.definitionActionPatterns = definitionActionPatterns;
	}

	public List<ResultAssignmentDto> getResultAssignments() {
		if(resultAssignments == null) {
			getAssignmentService().mapResultAssignments(this);
		}
		return resultAssignments;
	}
	
	@Deprecated
	public List<ResultAssignmentDto> getResultAssignmentsNoMapping() {
		return resultAssignments;
	}

	/**Do not call this method directly but call the service instead 
	 *AssignmentService.addResultAssignment(this, resultAssignments)/removeResultAssignment(this, resultAssignments);
	 */
	@Deprecated
	public void setResultAssignments(List<ResultAssignmentDto> resultAssignments) {
		this.resultAssignments = resultAssignments;
	}
	
	@Override
	public String toString() {
		return getStage()+" - "+getBiosample();
	}

	public AssignmentService getAssignmentService() {
		if(assignmentService == null) {
			assignmentService = (AssignmentService) ContextShare.getContext().getBean("assignmentService");
		}
		return assignmentService;
	}

	public boolean getSkipRando() {
		return skipRando==Boolean.TRUE;
	}
	public void setSkipRando(Boolean rando) {
		this.skipRando = rando;
	}
	
	public Double getTmpWeight() {
		return tmpWeight;
	}
	
	public void setTmpWeight(Double tmpWeight) {
		this.tmpWeight=tmpWeight;
	}
}
