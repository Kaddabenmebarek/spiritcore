package com.idorsia.research.spirit.core.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.actelion.research.util.CompareUtils;
import com.idorsia.research.spirit.core.conf.ContextShare;
import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.constants.StudyActionType;
import com.idorsia.research.spirit.core.dto.view.Measurement;
import com.idorsia.research.spirit.core.dto.view.StudyAction;
import com.idorsia.research.spirit.core.service.ActionPatternsService;
import com.idorsia.research.spirit.core.service.ScheduleService;
import com.idorsia.research.spirit.core.util.MiscUtils;

@Component
public class ActionPatternsDto implements IObject, Comparable<ActionPatternsDto>, Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3341842749819424071L;
	@Autowired
	private ActionPatternsService actionPatternsService;
	private ScheduleService scheduleService = (ScheduleService) ContextShare.getContext().getBean("scheduleService");
	private Integer id = Constants.NEWTRANSIENTID;
	private StudyAction action;
	private StudyActionType actionType;
	private String actionParameters;
	private Integer actionId;
	private ScheduleDto schedule;
	private Integer no=0;
	private StageDto stage;
	private Date creDate;
	private String creUser;
	private Date updDate;
	private String updUser;
	private ActionPatternsDto parent;
	private Set<ActionPatternsDto> children;

	public ActionPatternsDto() {
	}

	public ActionPatternsDto(ActionPatternsDto actionPattern) {
		this.actionType=actionPattern.getActionType();
		this.actionParameters=actionPattern.getActionParameters();
		this.no=actionPattern.getNo();
	}

	@Override
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public StudyAction getAction() {
		return action;
	}

	/**Do not call this method directly but call the service instead
	 * ActionPatternService.setAction(this, action);
	 */
	@Deprecated
	public void setAction(StudyAction action) {
		this.action = action;
		setActionId(action.getId());
		setActionType(action.getType());
		if(action instanceof Measurement)
			setActionParameters(MiscUtils.unsplit(((Measurement)action).getParameters(), ";"));
	}

	public StudyActionType getActionType() {
		return actionType;
	}

	public void setActionType(StudyActionType actionType) {
		this.actionType = actionType;
	}

	public String getActionParameters() {
		return actionParameters;
	}

	public void setActionParameters(String actionParameters) {
		this.actionParameters = actionParameters;
	}

	public ScheduleDto getSchedule() {
		return schedule;
	}

	/**Do not call this method directly but call the service instead
	 * ActionPatternService.setSchedule(this, schedule, false);
	 */
	@Deprecated
	public void setSchedule(ScheduleDto schedule) {
		this.schedule = schedule;
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
	 * ActionPatternService.setStage(this, stage);
	 */
	@Deprecated
	public void setStage(StageDto stage) {
		this.stage = stage;
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

	public Set<ActionPatternsDto> getChildren() {
		if(children == null) {
			getActionPatternsService().mapChildren(this);
		}
		return this.children;
	}
	
	@Deprecated
	public Set<ActionPatternsDto> getChildrenNoMapping() {
		return this.children;
	}
	
	public void setChildren(Set<ActionPatternsDto> children) {
		this.children=children;
	}

	public ActionPatternsDto getParent() {
		return parent;
	}
	
	/**Do not call this method directly but call the service instead 
	 *ActionPatternService.setParent(this, parent, false);
	 */
	@Deprecated
	public void setParent(ActionPatternsDto parent) {
		this.parent=parent;
	}

	public Integer getActionId() {
		return actionId;
	}

	public void setActionId(Integer actionId) {
		this.actionId = actionId;
	}

	@Override
	public int compareTo(ActionPatternsDto o) {
		int c = 0;
        c = CompareUtils.compare(getNo(), o.getNo());
        if(c!=0) return c;
        c = CompareUtils.compare(getSchedule(), o.getSchedule());
        return c;
	}

	@Override
    public String toString() {
        String ret = getAction() != null ? getAction().getName() + " " : "";
        ret += scheduleService.getDescription(getSchedule());
        return ret;
    }
	
	public ActionPatternsService getActionPatternsService() {
		if(actionPatternsService == null) {
			actionPatternsService = (ActionPatternsService) ContextShare.getContext().getBean("actionPatternsService");
		}
		return actionPatternsService;
	}
}
