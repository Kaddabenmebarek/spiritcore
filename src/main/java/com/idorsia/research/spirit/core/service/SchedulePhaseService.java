package com.idorsia.research.spirit.core.service;

import static com.idorsia.research.spirit.core.util.MiscUtils.nullSupportEqual;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.dao.SchedulePhaseDao;
import com.idorsia.research.spirit.core.dto.AssignmentDto;
import com.idorsia.research.spirit.core.dto.ExecutionDetailDto;
import com.idorsia.research.spirit.core.dto.PhaseDto;
import com.idorsia.research.spirit.core.dto.ScheduleDto;
import com.idorsia.research.spirit.core.dto.SchedulePhaseDto;
import com.idorsia.research.spirit.core.dto.view.StudyAction;
import com.idorsia.research.spirit.core.model.ExecutionDetail;
import com.idorsia.research.spirit.core.model.SchedulePhase;
import com.idorsia.research.spirit.core.util.UserUtil;

@Service
public class SchedulePhaseService extends AbstractService implements Serializable {

	private static final long serialVersionUID = 548192629837025395L;
	@Autowired
	private SchedulePhaseDao schedulePhaseDao;
	@Autowired
	private ExecutionDetailService executionDetailService;
	@Autowired
	private PhaseService phaseService;
	@Autowired
	private ScheduleService scheduleService;
	
	@SuppressWarnings("unchecked")
	private static Map<Integer, SchedulePhaseDto> idToSchedulePhase = (Map<Integer, SchedulePhaseDto>) getCacheMap(SchedulePhaseDto.class);

	public SchedulePhase get(Integer id) {
		return schedulePhaseDao.get(id);
	}

	public List<SchedulePhase> getByPhase(int phaseId) {
		return schedulePhaseDao.getByPhase(phaseId);
	}

	public List<SchedulePhase> getBySchedule(int scheduleId) {
		return schedulePhaseDao.getBySchedule(scheduleId);
	}

	public List<SchedulePhase> getBySchedules(Set<Integer> scheduleIds) {
		return schedulePhaseDao.getBySchedules(scheduleIds);
	}
	
	public void deleteByPhase(Integer phaseId) {
		for(SchedulePhaseDto sp : map(schedulePhaseDao.getByPhase(phaseId))) {
			delete(sp);
		}
	}
	
	public Set<Integer> getPhaseIds(Set<Integer> scheduleIds) {
		List<SchedulePhase> schedules = getBySchedules(scheduleIds);
		Set<Integer> phaseIds = new HashSet<Integer>();
		for(SchedulePhase sc : schedules) {
			phaseIds.add(sc.getPhaseId());
		}
		return phaseIds;
	}

	public List<SchedulePhase> list() {
		return schedulePhaseDao.list();
	}

	public int getCount() {
		return schedulePhaseDao.getCount();
	}

	public Integer saveOrUpdate(SchedulePhase schedulePhase) {
		return schedulePhaseDao.saveOrUpdate(schedulePhase);
	}

	public int addSchedulePhase(SchedulePhase schedulePhase) {
		return schedulePhaseDao.addSchedulePhase(schedulePhase);
	}

	public SchedulePhaseDao getSchedulePhaseDao() {
		return schedulePhaseDao;
	}

	public void setSchedulePhaseDao(SchedulePhaseDao schedulePhaseDao) {
		this.schedulePhaseDao = schedulePhaseDao;
	}

	public StudyAction getAction(SchedulePhaseDto schedulePhase) {
        return schedulePhase.getSchedule().getActionPattern().getAction();
    }

	public ExecutionDetailDto getExecutionDetails(SchedulePhaseDto schedulePhase, AssignmentDto assignment) {
		ExecutionDetailDto executionDetails = null;
        for (ExecutionDetailDto executionDetail : schedulePhase.getExecutionDetails()) {
            if (assignment.equals(executionDetail.getAssignment())) {
                executionDetails = executionDetail;
                break;
            }
        }
        if (executionDetails == null) {
        	executionDetails = new ExecutionDetailDto();
        	executionDetailService.setAssignment(executionDetails, assignment);
        	executionDetailService.setSchedulePhase(executionDetails, schedulePhase);
        }
        return executionDetails;
	}

	public List<SchedulePhaseDto> map(List<SchedulePhase> schedulePhases) {
		List<SchedulePhaseDto> res = new ArrayList<SchedulePhaseDto>();
		for(SchedulePhase schedulePhase : schedulePhases) {			
			res.add(map(schedulePhase));
		}
		return res;
	}
	
	public SchedulePhaseDto map(SchedulePhase schedulePhase) {
		SchedulePhaseDto schedulePhaseDto = idToSchedulePhase.get(schedulePhase.getId());
		if(schedulePhaseDto==null) {
			schedulePhaseDto = dozerMapper.map(schedulePhase, SchedulePhaseDto.class,"schedulePhaseCustomMapping");
			if(idToSchedulePhase.get(schedulePhase.getId())==null)
				idToSchedulePhase.put(schedulePhase.getId(), schedulePhaseDto);
			else
				schedulePhaseDto = idToSchedulePhase.get(schedulePhase.getId());
		}
		return schedulePhaseDto;
	}
	
	@Transactional
	public void save(SchedulePhaseDto schedulePhase) throws Exception {
		save(schedulePhase, false);
	}
	
	@SuppressWarnings("deprecation")
	protected void save(SchedulePhaseDto schedulePhase, Boolean cross) throws Exception {
		try {
			if(!savedItems.contains(schedulePhase)) {
				savedItems.add(schedulePhase);
				if(schedulePhase.getId()!=Constants.NEWTRANSIENTID) {
					deleteChildren(schedulePhase);
				}
				if(schedulePhase.getPhase().getId()==Constants.NEWTRANSIENTID)
					phaseService.save(schedulePhase.getPhase(), true);
				if(schedulePhase.getSchedule().getId()==Constants.NEWTRANSIENTID)
					scheduleService.save(schedulePhase.getSchedule(), true);
				schedulePhase.setUpdDate(new Date());
				schedulePhase.setUpdUser(UserUtil.getUsername());
				if(schedulePhase.getId().equals(Constants.NEWTRANSIENTID)) {
					schedulePhase.setCreDate(new Date());
					schedulePhase.setCreUser(UserUtil.getUsername());
				}
				schedulePhase.setId(saveOrUpdate(dozerMapper.map(schedulePhase, SchedulePhase.class, "schedulePhaseCustomMapping")));
				idToSchedulePhase.put(schedulePhase.getId(), schedulePhase);
				if(schedulePhase.getExecutionDetailsNoMapping()!=null)
					for(ExecutionDetailDto executionDetail : schedulePhase.getExecutionDetails()) {
						executionDetailService.save(executionDetail, true);
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
	private void deleteChildren(SchedulePhaseDto schedulePhase) {
		if(schedulePhase.getExecutionDetailsNoMapping()!=null) {
			for(ExecutionDetail ed : executionDetailService.getBySchedulePhase(schedulePhase.getId())) {
				Boolean found = false;
				for(ExecutionDetailDto child : schedulePhase.getExecutionDetails()){
					if(ed.getId().equals(child.getId())) {
						found=true;
						break;
					}
				}
				if(!found) {
					executionDetailService.delete(executionDetailService.map(ed), true);
				}
			}
		}
	}

	@Transactional
	public void delete(SchedulePhaseDto schedulePhase) {
		delete(schedulePhase, false);
	}
	
	protected void delete(SchedulePhaseDto schedulePhase, Boolean cross) {
		schedulePhaseDao.delete(schedulePhase.getId());
	}

	public void setSchedule(SchedulePhaseDto schedulePhaseDto, ScheduleDto schedule) {
		setSchedule(schedulePhaseDto, schedule, false);
	}

	@SuppressWarnings("deprecation")
	public void setSchedule(SchedulePhaseDto schedulePhase, ScheduleDto schedule, boolean cross) {
		ScheduleDto previousSchedule = schedulePhase.getSchedule();
		//prevent endless loop
        if (nullSupportEqual(previousSchedule, schedule))
            return;
        //remove from the old owner
        if (schedulePhase.getSchedule() !=null && !(cross && schedule == null))
            scheduleService.removeSchedulePhase(schedulePhase.getSchedule(), schedulePhase, true);
        //set new owner
        schedulePhase.setSchedule(schedule);
        //set myself to new owner
        if (schedule !=null && !cross)
        	scheduleService.addSchedulePhase(schedule, schedulePhase, true);
        if (schedule == null && !cross) 
        	setPhase(schedulePhase, null, false);
		
	}

	public void setPhase(SchedulePhaseDto schedulePhaseDto, PhaseDto phase) {
		 setPhase(schedulePhaseDto, phase, false);
	}

	@SuppressWarnings("deprecation")
	public void setPhase(SchedulePhaseDto schedulePhaseDto, PhaseDto phase, boolean cross) {
		PhaseDto previousPhase = schedulePhaseDto.getPhase();
		//prevent endless loop
        if (nullSupportEqual(previousPhase, phase))
            return;
        //remove from the old owner
        if (previousPhase !=null && !(cross && phase == null))
        	phaseService.removeSchedulePhase(previousPhase, schedulePhaseDto, true);
        //set new owner
        schedulePhaseDto.setPhase(phase);
        //set myself to new owner
        if (phase !=null && !cross)
            phaseService.addSchedulePhase(phase, schedulePhaseDto, true);
        if (phase == null && !cross) 
        	setSchedule(schedulePhaseDto, null, false);
		
	}

	public void remove(SchedulePhaseDto schedulePhase) {
		setPhase(schedulePhase, null);
		schedulePhase.getExecutionDetails().stream().forEach(e->executionDetailService.setAssignment(e, null));
	}

	 public void addExecutionDetails(SchedulePhaseDto schedulePhase, ExecutionDetailDto executionDetails) {
	        addExecutionDetails(schedulePhase, executionDetails, false);
	    }

	    public void addExecutionDetails(SchedulePhaseDto schedulePhase, ExecutionDetailDto executionDetails, boolean cross) {
	        //prevent endless loop
	        if (schedulePhase.getExecutionDetails().contains(executionDetails))
	            return ;
	        //add new member
	        schedulePhase.getExecutionDetails().add(executionDetails);
	        //update child if request is not from it
	        if (!cross) {
	            executionDetailService.setSchedulePhase(executionDetails, schedulePhase, true);
	        }
	    }

	    public void removeExecutionDetails(SchedulePhaseDto schedulePhase, ExecutionDetailDto  executionDetails) {
	        removeExecutionDetails(schedulePhase, executionDetails, false);
	    }

	    public void removeExecutionDetails(SchedulePhaseDto schedulePhase, ExecutionDetailDto  executionDetails, boolean cross) {
	        //prevent endless loop
	        if (!schedulePhase.getExecutionDetails().contains(executionDetails))
	            return ;
	        //remove old member
	        schedulePhase.getExecutionDetails().remove(executionDetails);
	        //remove child's owner
	        if (!cross) {
	            executionDetailService.setSchedulePhase(executionDetails,null, true);
	        }
	    }

		@SuppressWarnings("deprecation")
		public void mapExecutionDetails(SchedulePhaseDto schedulePhase) {
			List<ExecutionDetailDto> executionDetails = executionDetailService
					.map(executionDetailService.getBySchedulePhase(schedulePhase.getId()));
			if(executionDetails != null) {				
				schedulePhase.setExecxutionDetails(new HashSet<ExecutionDetailDto>(executionDetails));
			}
		}

		public SchedulePhaseDto getScheduleTypeDto(Integer id) {
			return map(get(id));
		}
}
