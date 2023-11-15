package com.idorsia.research.spirit.core.dto;

import java.awt.Color;
import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.idorsia.research.spirit.core.conf.ContextShare;
import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.constants.StudyActionType;
import com.idorsia.research.spirit.core.dto.view.StudyAction;
import com.idorsia.research.spirit.core.dto.view.SubjectSet;
import com.idorsia.research.spirit.core.service.StageService;

@Component
public class StageDto implements IObject, StudyAction, Comparable<StageDto>, SubjectSet, Serializable{

	private static final long serialVersionUID = -5957279279658911162L;
	@Autowired
	private StageService stageService;
	private Integer id = Constants.NEWTRANSIENTID;
	private StageDto previousStage;
	private StudyDto studyDto;
	private Boolean dynamic;
	private BiotypeDto biotype;
	private String name;
	private StageDto nextStage;
	private ZonedDateTime startDate;
	private Duration offsetOfD0=Duration.ZERO;
	private Duration offsetFromPreviousStage = Duration.ofDays(1);
	private DayOfWeek startingDay;
	private Date creDate;
	private String creUser;
	private Date updDate;
	private String updUser;
	private List<AssignmentDto> assignments;
	private List<GroupDto> groups;
	private List<PhaseDto> phases;
	private Set<StagePatternDto> definitionActionPatterns;
	private Set<ActionPatternsDto> actionPatterns;
	private Integer duration=60;
	private Set<StageBiotypeMetadataValueDto> metadatas;
	private Set<BiosampleDto> plannedSamples;

	public StageDto() {
	}
	
	public StageDto(String name) {
		this.name=name;
	}

	public StageDto(StageDto stage) {
		this.dynamic=stage.getDynamic();
		this.biotype=stage.getBiotype();
		this.name=stage.getName();
		this.startDate=stage.getStartDate();
		this.offsetOfD0=stage.getOffsetOfD0();
		this.offsetFromPreviousStage=stage.getOffsetFromPreviousStage();
		this.startingDay=stage.getStartingDay();
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public StudyDto getStudy() {
		return studyDto;
	}

	/**Do not call this method directly but call the service instead 
	 *StageService.setStudy(this, study);
	 */
	@Deprecated
	public void setStudy(StudyDto studyDto) {
		this.studyDto = studyDto;
	}

	public Boolean getDynamic() {
		return dynamic!=Boolean.FALSE;
	}
	
	public Boolean isDynamic() {
		return dynamic!=Boolean.FALSE;
	}

	public void setDynamic(Boolean dynamic) {
		this.dynamic = dynamic;
	}

	public BiotypeDto getBiotype() {
		return biotype;
	}

	public void setBiotype(BiotypeDto biotype) {
		this.biotype = biotype;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public StageDto getPreviousStage() {
		return this.previousStage;
	}
	
	/**Do not call this method directly but call the service instead 
	 *StageService.setPreviousStage(this, previousStage);
	 */
	@Deprecated
	public void setPreviousStage(StageDto previousStage) {
		this.previousStage=previousStage;
	}

	public StageDto getNextStage() {
		return nextStage;
	}

	/**Do not call this method directly but call the service instead 
	 *StageService.setNextStage(this, nextStage);
	 */
	@Deprecated
	public void setNextStage(StageDto nextStage) {
		this.nextStage = nextStage;
	}

	/**Do not call this method directly but call the service instead 
	 *StageService.getStartDate(stage);
	 */
	@Deprecated
	public ZonedDateTime getStartDate() {
		return startDate;
	}


	/**Do not call this method directly but call the service instead 
	 *StageService.setStartDate(this, startDate);
	 */
	@Deprecated
	public void setStartDate(ZonedDateTime startDate) {
		this.startDate = startDate;
	}
	
	/**
	 * Do not call this method directly but call the service instead
	 * StageService.getOffsetOfD0(this);
	 */
	@Deprecated
	public Duration getOffsetOfD0() {
		return offsetOfD0;
	}

	public void setOffsetOfD0(Duration offsetOfD0) {
		this.offsetOfD0 = offsetOfD0;
	}
	
	public Duration getOffsetFromPreviousStage() {
		if (offsetFromPreviousStage == null)
			setOffsetFromPreviousStage(Duration.ofDays(1));
		return offsetFromPreviousStage;
	}

	public void setOffsetFromPreviousStage(Duration offSetFromPreviousStage) {
		this.offsetFromPreviousStage = offSetFromPreviousStage;
	}

	@Override
	public int getDuration() {
		return duration;
	}
	
	@Override
	public void setDuration(int duration) {
		this.duration = duration;
	}

	public void setDuration(Integer duration) {
		this.duration = duration;
	}

	public DayOfWeek getStartingDay() {
		return startingDay;
	}

	public void setStartingDay(DayOfWeek startingDay) {
		this.startingDay = startingDay;
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

	public List<AssignmentDto> getAssignments() {
		if(assignments == null) {
			getStageService().mapAssignments(this);
		}
		return assignments;
	}
	
	@Deprecated
	public List<AssignmentDto> getAssignmentsNoMapping() {
		return assignments;
	}

	/**Do not call this method directly but call the service instead 
	 *StageService.addAssignment(this, assignment)/removeAssignment(this, assignment);
	 */
	@Deprecated
	public void setAssignments(List<AssignmentDto> assignments) {
		this.assignments = assignments;
	}
	
	public List<PhaseDto> getPhases() {
		if(phases == null) {
			getStageService().mapPhases(this);
		}
		return phases;
	}
	
	@Deprecated
	public List<PhaseDto> getPhasesNoMapping() {
		return phases;
	}

	/**Do not call this method directly but call the service instead 
	 *StageService.addPhase(this, phase)/removePhase(this, phase);
	 */
	@Deprecated
	public void setPhases(List<PhaseDto> phases) {
		this.phases = phases;
	}
	
	@Deprecated
	public Set<StageBiotypeMetadataValueDto> getStageMetadataValuesNoMapping() {
		return this.metadatas;
	}
	
	public Set<StageBiotypeMetadataValueDto> getStageMetadataValues() {
		if(this.metadatas==null)
			getStageService().mapMetadatas(this);
		return this.metadatas;
	}
	
	public void setStageMetadataValues(Set<StageBiotypeMetadataValueDto> metadatas) {
		this.metadatas=metadatas;
	}
	
	@Override
	public int compareTo(StageDto o) {
		if(o==null) return -1;
		if (o.equals(this)) return 0;
		if (!studyDto.equals(o.getStudy())) {
			return studyDto.compareTo(o.getStudy());
		}
		StageDto prevStage = o.getPreviousStage();
		if (prevStage == null) return 1;
		if (this.getPreviousStage() == null) return -1;
		if (prevStage.equals(this)) return -1;
		return compareTo(o.getPreviousStage());
	}
	
	public Set<StagePatternDto> getActionDefinition() {
		if(definitionActionPatterns == null) {
			getStageService().mapActionDefinition(this);
		}
		return definitionActionPatterns;
	}
	
	@Deprecated
	public Set<StagePatternDto> getActionDefinitionNoMapping() {
		return definitionActionPatterns;
	}
	
	public Set<ActionPatternsDto> getActionDefinitionPattern() {
		Set<ActionPatternsDto> patterns = new HashSet<>();
		for(StagePatternDto pattern : getActionDefinition()) {
			patterns.add(pattern.getActionpattern());
		}
		return patterns;
	}

	/**Do not call this method directly but call the service instead 
	 *StageService.addActionDefinition(this, definitionActionPattern)/removeActionDefinition(this, definitionActionPattern);
	 */
	@Deprecated
	public void setActionDefinition(Set<StagePatternDto> definitionActionPatterns) {
		this.definitionActionPatterns=definitionActionPatterns;
	}
	
	public Set<ActionPatternsDto> getActionPatterns() {
		if(actionPatterns == null) {
			getStageService().mapActionpatterns(this);
		}
		return actionPatterns;
	}
	
	@Deprecated
	public Set<ActionPatternsDto> getActionPatternsNoMapping() {
		return actionPatterns;
	}

	/**Do not call this method directly but call the service instead 
	 *StageService.addActionPattern(this, actionPattern)/removeActionPattern(this, actionPattern);
	 */
	@Deprecated
	public void setActionPatterns(Set<ActionPatternsDto> actionPatterns) {
		this.actionPatterns=actionPatterns;
	}

	public List<GroupDto> getGroups() {
		if(groups == null) {
			getStageService().mapGroups(this);
		}
		return groups;
	}

	@Deprecated
	public List<GroupDto> getGroupsNoMapping() {
		return groups;
	}
	
	/**Do not call this method directly but call the service instead 
	 *StageService.addGroup(this, group)/removeGroup(this, group);
	 */
	@Deprecated
	public void setGroups(List<GroupDto> groups) {
		this.groups = groups;
	}

	@Override
	public StudyActionType getType() {
		return StudyActionType.GROUPASSIGN;
	}
	
	@Override
	public Color getColor() {
		return Color.BLACK;
	}
	
	@Override
	public Set<BiotypeMetadataValueDto> getMetadatas() {
		return getStageService().getMetadatas(this);
	}
	
	public void setMetadatas(Set<BiotypeMetadataValueDto> metadatas) {
		getStageService().setMetadatas(this, metadatas);
	}
	
	public Set<BiosampleDto> getPlannedSamples() {
		return plannedSamples;
	}

	public void setPlannedSamples(Set<BiosampleDto> plannedSamples) {
		this.plannedSamples = plannedSamples;
	}

	@Override
	public String toString() {
		return getName();
	}

	public StageService getStageService() {
		if(stageService == null) {
			stageService = (StageService) ContextShare.getContext().getBean("stageService");
		}
		return stageService;
	}
	
	@Override
	public void addMetadata(BiotypeMetadataValueDto metaDefinition) {
		getStageService().addMetadata(this, metaDefinition);
	}

	@Override
	public void removeMetadata(BiotypeMetadataValueDto metaDefinition) {
		getStageService().removeMetadata(this, metaDefinition);
	}

	@Override
	public boolean addActionDefinition(ActionPatternsDto actionDefinition) {
		return getStageService().addAction(this, actionDefinition);
	}

	@Override
	public boolean removeActionDefinition(ActionPatternsDto actionDefinition) {
		return getStageService().removeActionDefinition(this, actionDefinition);
	}

	@Override
	public Set<BiosampleDto> getSubjects() {
		return getStageService().getSubjects(this);
	}
}
