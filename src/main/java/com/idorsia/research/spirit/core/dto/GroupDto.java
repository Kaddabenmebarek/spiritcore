package com.idorsia.research.spirit.core.dto;

import java.awt.Color;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.idorsia.research.spirit.core.conf.ContextShare;
import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.dto.view.SubjectSet;
import com.idorsia.research.spirit.core.service.GroupService;

@Component
public class GroupDto implements IObject, Comparable<GroupDto>, SubjectSet, Serializable{

	private static final long serialVersionUID = -6746748485933629301L;
	@Autowired
	private GroupService groupService;
	private Integer id = Constants.NEWTRANSIENTID;
	private Integer idx;
	private String name;
	private Color color;
	private Integer severity;
	private StageDto stageDto;
	private Date creDate;
	private String creUser;
	private Date updDate;
	private String updUser;
	private List<SubGroupDto> subgroups;
	private Set<GroupPatternDto> definitionActionPatterns;
	private Set<GroupBiotypeMetadataValueDto> metadatas;
	
	public GroupDto() {
	}

	public GroupDto(GroupDto group) {
		this.idx=group.getIdx();
		this.name=group.getName();
		this.color=group.getColor();
		this.severity=group.getSeverity();
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getIdx() {
		return idx;
	}

	public void setIdx(Integer idx) {
		this.idx = idx;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public Integer getSeverity() {
		return severity;
	}

	public void setSeverity(Integer severity) {
		this.severity = severity;
	}

	public StageDto getStage() {
		return stageDto;
	}

	/**Do not call this method directly but call the service instead 
	 *GroupService.setStage(this, stage);
	 */
	@Deprecated
	public void setStage(StageDto stageDto) {
		this.stageDto = stageDto;
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

	public Set<GroupPatternDto> getActionDefinition() {
		if(definitionActionPatterns == null) {
			getGroupService().mapActionDefinition(this);
		}
		return definitionActionPatterns;
	}
	
	@Deprecated
	public Set<GroupPatternDto> getActionDefinitionNoMapping() {
		return definitionActionPatterns;
	}
	
	public Set<ActionPatternsDto> getActionDefinitionPattern() {
		Set<ActionPatternsDto> patterns = new HashSet<>();
		for(GroupPatternDto pattern : getActionDefinition()) {
			patterns.add(pattern.getActionpattern());
		}
		return patterns;
	}
	
	public void setActionDefinition(Set<GroupPatternDto> definitionActionPatterns) {
		this.definitionActionPatterns = definitionActionPatterns;
	}

	public List<SubGroupDto> getSubgroups() {
		if(subgroups == null) {
			getGroupService().mapSubgroups(this);
		}
		return subgroups;
	}

	@Deprecated
	public List<SubGroupDto> getSubgroupsNoMapping() {
		return subgroups;
	}
	
	/**Do not call this method directly but call the service instead 
	 *GroupService.addSubgroup(this, subgroup)/removeSubgroup(this, subgroup);
	 */
	@Deprecated
	public void setSubgroups(List<SubGroupDto> subgroups) {
		this.subgroups = subgroups;
	}

	public Set<GroupBiotypeMetadataValueDto> getGroupMetadatas() {
		if(metadatas == null) {
			getGroupService().mapMetadatas(this);
		}
		return metadatas;
	}

	@Deprecated
	public Set<GroupBiotypeMetadataValueDto> getGroupMetadatasNoMapping() {
		return metadatas;
	}
	
	/**Do not call this method directly but call the service instead 
	 *GroupService.addSubgroup(this, subgroup)/removeSubgroup(this, subgroup);
	 */
	@Deprecated
	public void setGroupMetadatas(Set<GroupBiotypeMetadataValueDto> metadatas) {
		this.metadatas = metadatas;
	}
	
	public Set<BiotypeMetadataValueDto> getMetadatas() {
		return getGroupService().getMetadatas(this);
	}
	
	/**Do not call this method directly but call the service instead 
	 *GroupService.addMetadata(this, metadata)/removeMetadata(this, metadata);
	 */
	@Deprecated
	public void setMetadatas(Set<BiotypeMetadataValueDto> metadatas) {
		getGroupService().setMetadatas(this, metadatas);
	}
	
	@Override
	public int compareTo(GroupDto o) {
		if (o == null) return -1;
		if (o == this) return 0;

		int c = getStage() == null ? (o.getStage() == null ? 0 : 1) : getStage().compareTo(o.getStage());
		if (c != 0) return c;

		c = getName().compareTo(o.getName());
		return c;
	}
	
	@Override
	public String toString() {
		return getStage().toString() + " - " + getName();
	}

	public GroupService getGroupService() {
		if(groupService == null) {
			groupService = (GroupService) ContextShare.getContext().getBean("groupService");
		}
		return groupService;
	}

	@Override
	public void addMetadata(BiotypeMetadataValueDto metaDefinition) {
		getGroupService().addMetadata(this, metaDefinition);
	}

	@Override
	public void removeMetadata(BiotypeMetadataValueDto metaDefinition) {
		getGroupService().removeMetadata(this, metaDefinition);
	}

	@Override
	public boolean addActionDefinition(ActionPatternsDto actionDefinition) {
		return getGroupService().addActionDefinition(this, actionDefinition);
	}

	@Override
	public boolean removeActionDefinition(ActionPatternsDto actionDefinition) {
		return getGroupService().removeActionDefinition(this, actionDefinition);
	}

	@Override
	public Set<BiosampleDto> getSubjects() {
		return getGroupService().getSubjects(this);
	}
}
