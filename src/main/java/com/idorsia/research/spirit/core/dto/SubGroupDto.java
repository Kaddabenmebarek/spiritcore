package com.idorsia.research.spirit.core.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.idorsia.research.spirit.core.conf.ContextShare;
import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.dto.view.SubjectSet;
import com.idorsia.research.spirit.core.service.SubGroupService;

@Component
public class SubGroupDto implements IObject, Comparable<SubGroupDto>, SubjectSet, Serializable{

	private static final long serialVersionUID = -1455381492349590145L;
	@Autowired
	private SubGroupService subGroupService;
	private Integer id = Constants.NEWTRANSIENTID;
	private GroupDto group;
	private GroupDto randofromgroup;
	private SubGroupDto randofromsubgroup;
	private Date creDate;
	private String creUser;
	private Date updDate;
	private String updUser;
	private Integer no;
	private String name;
	private Set<AssignmentDto> assignments;
	private Set<SubGroupPatternDto> actionPatterns;
	private Set<SubGroupBiotypeMetadataValueDto> metadatas;

	public SubGroupDto() {
	}

	public SubGroupDto(SubGroupDto subGroup) {
		this.no=subGroup.getNo();
		this.name=subGroup.getFullName();
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public GroupDto getGroup() {
		return group;
	}

	/**Do not call this method directly but call the service instead 
	 *SubGroupService.setGroup(this, group);
	 */
	@Deprecated
	public void setGroup(GroupDto group) {
		this.group = group;
	}

	public GroupDto getRandofromgroup() {
		return randofromgroup;
	}

	public void setRandofromgroup(GroupDto randofromgroup) {
		this.randofromgroup = randofromgroup;
	}

	public SubGroupDto getRandofromsubgroup() {
		return randofromsubgroup;
	}

	public void setRandofromsubgroup(SubGroupDto randofromsubgroup) {
		this.randofromsubgroup = randofromsubgroup;
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

	public String getCreUser() {
		return creUser;
	}

	public void setCreUser(String creUser) {
		this.creUser = creUser;
	}

	public String getUpdUser() {
		return updUser;
	}

	public void setUpdUser(String updUser) {
		this.updUser = updUser;
	}

	public Integer getNo() {
		return no;
	}

	public void setNo(Integer no) {
		this.no = no;
	}
	
	public String getName() {
		return name;
	}

	public String getFullName() {
		if (name == null) {
			if (group.getSubgroups().size() == 1)
				return "";
			else
				return "Subgroup " + Integer.toString(getNo() + 1);
		} else {
	        return name;
	    }
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<SubGroupPatternDto> getActionDefinition() {
		if(actionPatterns == null) {
			getSubGroupService().mapActionDefinition(this);
		}
		return actionPatterns;
	}
	
	@Deprecated
	public Set<SubGroupPatternDto> getActionDefinitionNoMapping() {
		return actionPatterns;
	}
	
	public Set<ActionPatternsDto> getActionDefinitionPattern() {
		Set<ActionPatternsDto> patterns = new HashSet<>();
		for(SubGroupPatternDto pattern : getActionDefinition()) {
			patterns.add(pattern.getActionpattern());
		}
		return patterns;
	}
	
	public void setActionDefinition(Set<SubGroupPatternDto> actionPatterns) {
		this.actionPatterns=actionPatterns;
	}

	public Set<SubGroupBiotypeMetadataValueDto> getSubGroupMetadatas() {
		if(metadatas == null) {
			getSubGroupService().mapMetadatas(this);
		}
		return metadatas;
	}

	@Deprecated
	public Set<SubGroupBiotypeMetadataValueDto> getSubGroupMetadatasNoMapping() {
		return metadatas;
	}
	
	public void setSubGroupMetadatas(Set<SubGroupBiotypeMetadataValueDto> metadatas) {
		this.metadatas = metadatas;
	}
	
	public Set<AssignmentDto> getAssignments() {
		if(assignments == null) {
			getSubGroupService().mapAssignments(this);
		}
		return assignments;
	}

	@Deprecated
	public Set<AssignmentDto> getAssignmentsNoMapping() {
		return assignments;
	}
	
	public void setAssignments(Set<AssignmentDto> assignments) {
		this.assignments = assignments;
	}
	
	public Set<BiotypeMetadataValueDto> getMetadatas() {
		return getSubGroupService().getMetadatas(this);
	}
	
	public void setMetadatas(Set<BiotypeMetadataValueDto> metadatas) {
		getSubGroupService().setMetadatas(this, metadatas);
	}
	
	@Override
	public String toString() {
		return getFullName();
	}

	@Override
    public int compareTo(SubGroupDto o) {
        if(o==null) return -1;
        if(o==this) return 0;

        int c = getGroup()==null? (o.getGroup()==null?0:1): getGroup().compareTo(o.getGroup());
        if(c!=0) return c;

        c = getNo() - o.getNo();
        return c;
    }

	public SubGroupService getSubGroupService() {
		if(subGroupService == null) {
			subGroupService = (SubGroupService) ContextShare.getContext().getBean("subGroupService");
		}
		return subGroupService;
	}
	
	@Override
	public void addMetadata(BiotypeMetadataValueDto metaDefinition) {
		getSubGroupService().addMetadata(this, metaDefinition);
	}

	@Override
	public void removeMetadata(BiotypeMetadataValueDto metaDefinition) {
		getSubGroupService().removeMetadata(this, metaDefinition);
	}

	@Override
	public boolean addActionDefinition(ActionPatternsDto actionDefinition) {
		return getSubGroupService().addActionDefinition(this, actionDefinition);
	}

	@Override
	public boolean removeActionDefinition(ActionPatternsDto actionDefinition) {
		return getSubGroupService().removeActionDefinition(this, actionDefinition);
	}

	@Override
	public Set<BiosampleDto> getSubjects() {
		return getSubGroupService().getSubjects(this);
	}
	
	
}
