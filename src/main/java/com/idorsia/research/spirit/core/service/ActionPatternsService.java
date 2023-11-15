package com.idorsia.research.spirit.core.service;

import static com.idorsia.research.spirit.core.util.MiscUtils.nullSupportEqual;

import java.awt.Component;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.constants.StudyActionType;
import com.idorsia.research.spirit.core.dao.ActionPatternsDao;
import com.idorsia.research.spirit.core.dto.ActionPatternsDto;
import com.idorsia.research.spirit.core.dto.AssignmentDto;
import com.idorsia.research.spirit.core.dto.NamedSamplingDto;
import com.idorsia.research.spirit.core.dto.NamedTreatmentDto;
import com.idorsia.research.spirit.core.dto.ScheduleDto;
import com.idorsia.research.spirit.core.dto.StageDto;
import com.idorsia.research.spirit.core.dto.view.Measurement;
import com.idorsia.research.spirit.core.dto.view.StudyAction;
import com.idorsia.research.spirit.core.model.ActionPatterns;
import com.idorsia.research.spirit.core.model.Schedule;
import com.idorsia.research.spirit.core.model.Stage;
import com.idorsia.research.spirit.core.util.UserUtil;

@Service
public class ActionPatternsService extends AbstractService implements Serializable {

	private static final long serialVersionUID = 6422841957851038358L;
	@Autowired
	private ActionPatternsDao actionPatternsDao;
	@Autowired
	private AssignmentService assignmentService;
	@Autowired
	private StudyActionService studyActionService;
	@Autowired
	private StageService stageService;
	@Autowired
	private ScheduleService scheduleService;
	
	@SuppressWarnings("unchecked")
	private static Map<Integer, ActionPatternsDto> idToActionPattern = (Map<Integer, ActionPatternsDto>) getCacheMap(ActionPatternsDto.class);

	public ActionPatterns get(Integer id) {
		return actionPatternsDao.get(id);
	}

	public List<ActionPatterns> getByIds(List<Integer> actionPatternsIds) {
		return actionPatternsDao.getByIds(actionPatternsIds);
	}

	public List<ActionPatterns> list() {
		return actionPatternsDao.list();
	}

	public int getCount() {
		return actionPatternsDao.getCount();
	}

	public Integer saveOrUpdate(ActionPatterns actionPatterns) {
		return actionPatternsDao.saveOrUpdate(actionPatterns);
	}

	public int addActionPatterns(ActionPatterns actionPatterns) {
		return actionPatternsDao.addActionPattern(actionPatterns);
	}

	public List<ActionPatterns> getByAssignment(Integer assignmentId) {
		if(assignmentId.equals(Constants.NEWTRANSIENTID))
			return new ArrayList<>();
		return actionPatternsDao.getByAssignment(assignmentId);
	}
	
	public List<ActionPatterns> getByAction(Integer actionId, String type) {
		if(actionId.equals(Constants.NEWTRANSIENTID))
			return new ArrayList<>();
		return actionPatternsDao.getByAction(actionId, type);
	}

	public List<ActionPatterns> getBySubGroup(Integer subGroupId) {
		if(subGroupId.equals(Constants.NEWTRANSIENTID))
			return new ArrayList<>();
		return actionPatternsDao.getBySubGroup(subGroupId);
	}

	public List<ActionPatterns> getByGroup(Integer groupId) {
		if(groupId.equals(Constants.NEWTRANSIENTID))
			return new ArrayList<>();
		return actionPatternsDao.getByGroup(groupId);
	}

	private List<ActionPatterns> getActionPatternsByParent(Integer parentId) {
		if(parentId.equals(Constants.NEWTRANSIENTID))
			return new ArrayList<>();
		return actionPatternsDao.getActionPatternsByParent(parentId);
	}

	public String getActionTypeByActionId(Integer id) {
		return actionPatternsDao.getActionTypeByActionId(id);
	}

	public ActionPatterns getBySchedule(Integer id) {
		return actionPatternsDao.getActionTypeBySchdule(id);
	}

	public List<ActionPatterns> getActionPatternsByStage(Integer stageId) {
		return actionPatternsDao.getActionTypeByStage(stageId);
	}

	public Set<ActionPatterns> getBySubGoupsAndSchedules(Set<Integer> subGroupIds, List<Schedule> matchingSchedules) {
		Set<Integer> scheduleIds = new HashSet<Integer>();
		for(Schedule sc : matchingSchedules) {
			scheduleIds.add(sc.getId());
		}
		return actionPatternsDao.getBySubGoupsAndSchedules(subGroupIds, scheduleIds);
	}

	public Map<Integer, List<Integer>> getActionsPhasesMap(List<Stage> stages) {
		List<Integer> stageIds = new ArrayList<Integer>();
		for(Stage st : stages) {
			stageIds.add(st.getId());
		}
		return actionPatternsDao.getActionsPhasesMap(stageIds);
	}
	
	public Map<Integer, Integer> getActionsPhasesMap(Integer actionpatternId) {
		return actionPatternsDao.getActionsPhasesMap(actionpatternId);
	}

	public ActionPatternsDao getActionPatternsDao() {
		return actionPatternsDao;
	}

	public void setActionPatternsDao(ActionPatternsDao actionPatternsDao) {
		this.actionPatternsDao = actionPatternsDao;
	}

	public List<ActionPatternsDto> getByStudy(Integer studyId) {
		List<ActionPatternsDto> result = new ArrayList<>();
		if(studyId.equals(Constants.NEWTRANSIENTID))
			return result;
		for (AssignmentDto assignment : assignmentService.map(assignmentService.getAssignmentsByStudy(studyId))) {
			result.addAll(assignmentService.getFullActionDefinition(assignment));
		}
		return result;
	}

	public List<ActionPatternsDto> getStudyActionTreatment(List<ActionPatternsDto> actionPatterns) {
		List<ActionPatternsDto> result = new ArrayList<>();
		for(ActionPatternsDto ap : actionPatterns) {
			if (StudyActionType.THERAPY.equals(ap.getActionType())) result.add(ap);
		}
		return result;
	}

	public Set<ActionPatternsDto> map(List<ActionPatterns> actionPatternsList) {
		Set<ActionPatternsDto> res = new HashSet<ActionPatternsDto>();
		for(ActionPatterns actionPattern : actionPatternsList) {
			res.add(map(actionPattern));
		}
		return res;
	}
	
	@SuppressWarnings("deprecation")
	public ActionPatternsDto map(ActionPatterns actionPattern) {
		ActionPatternsDto actionPatternDto = idToActionPattern.get(actionPattern.getId());
		if(actionPatternDto == null) {
			actionPatternDto = dozerMapper.map(actionPattern, ActionPatternsDto.class, "actionPatternsCustomMapping");
			actionPatternDto.setAction(studyActionService.map(actionPattern.getActionId(), actionPattern.getActionType(), actionPattern.getActionParameters(), actionPattern.getStageId()));
			if(idToActionPattern.get(actionPattern.getId())==null)
				idToActionPattern.put(actionPattern.getId(), actionPatternDto);
			else
				actionPatternDto = idToActionPattern.get(actionPattern.getId());
		}
		return actionPatternDto;
	}
	
	public ActionPatternsDto getActionPatternsDto(Integer id) {
		return map(get(id));
	}

	@Transactional
	public void save(ActionPatternsDto actionPattern) throws Exception {
		save(actionPattern, false);
	}
	
	@SuppressWarnings("deprecation")
	protected void save(ActionPatternsDto actionPattern, Boolean cross) throws Exception {
		try{
			if(actionPattern!=null && !savedItems.contains(actionPattern)) {
				savedItems.add(actionPattern);
				if(actionPattern.getId()!=Constants.NEWTRANSIENTID)
					deleteChildren(actionPattern);	
				if(actionPattern.getParent()!=null && actionPattern.getParent().getId().equals(Constants.NEWTRANSIENTID))
					save(actionPattern.getParent(), true);	
				if(actionPattern.getStage().getId().equals(Constants.NEWTRANSIENTID))
					stageService.save(actionPattern.getStage(), true);
				if(actionPattern.getSchedule().getId().equals(Constants.NEWTRANSIENTID))
					scheduleService.save(actionPattern.getSchedule(), true);
				if(actionPattern.getAction().getId().equals(Constants.NEWTRANSIENTID))
					studyActionService.save(actionPattern.getAction(), true);
				setAction(actionPattern, actionPattern.getAction());
				actionPattern.setUpdDate(new Date());
				actionPattern.setUpdUser(UserUtil.getUsername());
				if(actionPattern.getId().equals(Constants.NEWTRANSIENTID)) {
					actionPattern.setCreDate(new Date());
					actionPattern.setCreUser(UserUtil.getUsername());
				}
				actionPattern.setId(saveOrUpdate(dozerMapper.map(actionPattern, ActionPatterns.class, "actionPatternsCustomMapping")));
				idToActionPattern.put(actionPattern.getId(), actionPattern);
				if(actionPattern.getChildrenNoMapping()!=null)
					for(ActionPatternsDto ap : actionPattern.getChildren()) {
						save(ap, true);
					}
			}
		}catch(Exception e) {
			if (!cross) {
				e.printStackTrace();
				AbstractService.clearTransient(false);
			}
			throw e;
		}finally{
			if(!cross) {
				AbstractService.clearSavedItem();
				AbstractService.clearTransient(true);
			}
		}
	}

	@SuppressWarnings("deprecation")
	private void deleteChildren(ActionPatternsDto actionPattern) {
		if(actionPattern.getChildrenNoMapping()!=null) {
			for(ActionPatterns ap : getActionPatternsByParent(actionPattern.getId())) {
				Boolean found=false;
				for(ActionPatternsDto child : actionPattern.getChildren()) {
					if(child.getId().equals(ap.getId())) {
						found=true;
						break;
					}
				}
				if(!found)
					delete(map(ap), true);
			}
		}
	}

	@Transactional
	public void delete(ActionPatternsDto actionPattern) {
		delete(actionPattern, false);
	}
	
	protected void delete(ActionPatternsDto actionPattern, Boolean cross) {
		actionPatternsDao.delete(actionPattern.getId());
		scheduleService.delete(actionPattern.getSchedule(), true);
	}

	@SuppressWarnings("deprecation")
	public void setStage(ActionPatternsDto actionPattern, StageDto stage, boolean cross) {
        StageDto actualStage = actionPattern.getStage();
        ScheduleDto schedule = actionPattern.getSchedule();
		//prevent endless loop
        if (nullSupportEqual(actualStage, stage))
            return;
        //remove from the old owner
        if (actualStage !=null && !(cross && stage == null))
        	stageService.removeActionPattern(actualStage, actionPattern, true);
        //set new owner
        	actionPattern.setStage(stage);
        if (schedule != null) {
            try {
            	scheduleService.setPhases(schedule);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //set myself to new owner
        if (stage !=null && !cross)
            stageService.addActionPattern(stage, actionPattern, true);
	}

	public void setParent(ActionPatternsDto actionPattern, ActionPatternsDto parent) {
		setParent(actionPattern, parent, false);
	}
	
	@SuppressWarnings("deprecation")
	public void setParent(ActionPatternsDto actionPattern, ActionPatternsDto parent, boolean cross) {
        //prevent endless loop
        if (nullSupportEqual(actionPattern, parent))
            return;
        if(actionPattern.getParent()!=null)
        	actionPattern.getParent().getChildren().remove(actionPattern);
        //set new owner
        actionPattern.setParent(parent);
        //set myself to new owner
        if (parent !=null && !cross)
            parent.getChildren().add(actionPattern);
	}

	@SuppressWarnings("deprecation")
	public void removeChildren(ActionPatternsDto actionPattern, ActionPatternsDto parent, boolean cross) {
		if(actionPattern !=null) {
			actionPattern.setParent(null);
			parent.getChildren().remove(actionPattern);
		}
	}

	public void setStage(ActionPatternsDto actionPattern, StageDto stage) {
		setStage(actionPattern, stage, false);
	}

	@SuppressWarnings("deprecation")
	public void setAction(ActionPatternsDto actionPattern, StudyAction action) {
		actionPattern.setAction(action);
	     for(ActionPatternsDto ap : actionPattern.getChildren()) {
	     	setAction(ap, action);
	     }
	}

	public void setSchedule(ActionPatternsDto actionPattern, ScheduleDto schedule) throws Exception {
		setSchedule(actionPattern, schedule, false);
	}
	
	 @SuppressWarnings("deprecation")
	public void setSchedule(ActionPatternsDto actionPatterns, ScheduleDto schedule, boolean cross) throws Exception {
	        //prevent endless loop
	        if (nullSupportEqual(actionPatterns.getSchedule(), schedule))
	            return;
	        //remove from the old owner
	        if (actionPatterns.getSchedule() !=null) {
	        	scheduleService.removePhases(actionPatterns.getSchedule(), actionPatterns.getSchedule().getSchedulePhases());
	        	if(!(cross && schedule == null))
	        		scheduleService.setActionPattern(actionPatterns.getSchedule(), null, true);
	        }
	        //set new owner
	        actionPatterns.setSchedule(schedule);
	        //set myself to new owner
	        if (schedule !=null && !cross)
	        	scheduleService.setActionPattern(schedule, actionPatterns, true);
	    }

	public boolean isVisible(ActionPatternsDto a) {
		return a.getParent()==null;
	}

	public ActionPatternsDto newActionPatternsDto(StageDto stage, ScheduleDto studySchedule,
			StudyAction studyAction) {
		if(studySchedule == null) 
			throw new IllegalArgumentException("SubGroup and Phase+Schedule are required");
        ActionPatternsDto ap = new ActionPatternsDto();
		try {
            setStage(ap, stage);
            setAction(ap, studyAction);
            setSchedule(ap, studySchedule);
            return ap;
        } catch (Exception e) {
            e.printStackTrace();
    		return null;
        }
	}

	public DefaultListCellRenderer getListCellRenderer(ActionPatternsDto ap) {
		switch (ap.getActionType()) {
        case DISEASE:
        case THERAPY:
            return new DefaultListCellRenderer() {
				private static final long serialVersionUID = 568278805139820077L;

				@Override
                public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    String txt = "<html>";
                    NamedTreatmentDto ns = ((NamedTreatmentDto) ap.getAction());
                    String compoundUnit = studyActionService.getDescription(ns);
                    if (ns.getName() != null && !ns.getName().equals(""))
                        txt += "<li><b><u>" + ns.getName() + "</u></b>" + (compoundUnit.length() > 0 ? compoundUnit : "") + "</li>";
                    setText(txt);
                    return this;
                }
            };
        case MEASUREMENT:
            return new DefaultListCellRenderer() {
				private static final long serialVersionUID = 1085195631565930571L;

				@Override
                public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    String txt = "<html>";
                    Measurement m = ((Measurement) ap.getAction());
                    if (m.getAssay()!= null && !m.getAssay().getName().equals(""))
                        txt += "<li><b><u>" + m.getAssay() + "</u></b><br>" +
                        		studyActionService.getParametersString(m)+"</li>";
                    txt += "</html>";
                    setText(txt);

                    return this;
                }
            };
        case SAMPLING:
            return new DefaultListCellRenderer() {
				private static final long serialVersionUID = 8516592145922790098L;

				@Override
                public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    String txt = "<html>";
                    NamedSamplingDto ns = ((NamedSamplingDto) ap.getAction());
                    if (ns.getName()!= null && !ns.getName().equals(""))
                        txt += "<li><b><u>" + ns.getName() + "</u></b> " + (ns.getStudy() == null ? "" : "(" + ns.getStudy().getStudyId() + ")") + "<br>" +
                                studyActionService.getDescription(ns) +"</li>";
                    txt += "</html>";
                    setText(txt);

                    return this;
                }
            };
		default:
			break;
    }
    return null;
	}

	public void mapChildren(ActionPatternsDto actionPatternDto) {
		Set<ActionPatternsDto> actionPatterns = new HashSet<>();
		for(ActionPatterns ap : getActionPatternsByParent(actionPatternDto.getId()))
			actionPatterns.add(map(ap));
		actionPatternDto.setChildren(actionPatterns);	
	}
}
