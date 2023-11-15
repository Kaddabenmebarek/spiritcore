package com.idorsia.research.spirit.core.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.dao.ExecutionDetailDao;
import com.idorsia.research.spirit.core.dto.AssignmentDto;
import com.idorsia.research.spirit.core.dto.ExecutionDetailDto;
import com.idorsia.research.spirit.core.dto.PhaseDto;
import com.idorsia.research.spirit.core.dto.SchedulePhaseDto;
import com.idorsia.research.spirit.core.dto.view.StudyAction;
import com.idorsia.research.spirit.core.model.ExecutionDetail;
import com.idorsia.research.spirit.core.util.UserUtil;

@Service
public class ExecutionDetailService extends AbstractService implements Serializable {

	private static final long serialVersionUID = 8162665960937515351L;
	@Autowired
	private ExecutionDetailDao executionDetailDao;
	@Autowired
	private AssignmentService assignmentService;
	@Autowired
	private BiosampleService biosampleService;
	@Autowired
	private SchedulePhaseService schedulePhaseService;
	
	@SuppressWarnings("unchecked")
	private static Map<Integer, ExecutionDetailDto> idToExecutionDetail = (Map<Integer, ExecutionDetailDto>) getCacheMap(ExecutionDetailDto.class);

	public ExecutionDetail get(Integer id) {
		return executionDetailDao.get(id);
	}

	public ExecutionDetail getByPhaseAndAssignment(int phaseLinkId, int assignmentId) {
		return executionDetailDao.getByPhaseAndAssignment(phaseLinkId, assignmentId);
	}

	public List<ExecutionDetail> list() {
		return executionDetailDao.list();
	}

	public List<ExecutionDetail> getBySchedulePhase(Integer schedulePhaseId) {
		return executionDetailDao.getBySchedulePhase(schedulePhaseId);
	}

	public List<ExecutionDetail> getByAssignment(Integer assignmentId) {
		return executionDetailDao.getByAssignment(assignmentId);
	}

	public List<ExecutionDetail> getByStudy(Integer studyId) {
		return executionDetailDao.getByStudyId(studyId);
	}

	public int getCount() {
		return executionDetailDao.getCount();
	}

	public int addExecutionDetail(ExecutionDetail executionDetail) {
		return executionDetailDao.addExecutionDetail(executionDetail);
	}

	public Integer saveOrUpdate(ExecutionDetail executionDetail) {
		return executionDetailDao.saveOrUpdate(executionDetail);
	}

	public ExecutionDetailDao getExecutionDetailDao() {
		return executionDetailDao;
	}

	public void setExecutionDetailDao(ExecutionDetailDao executionDetailDao) {
		this.executionDetailDao = executionDetailDao;
	}

	public Date getExecutionDate(ExecutionDetailDto executionDetail) {
    	return getExecutionDate(executionDetail, false);
    }
    
    public Date getExecutionDate(ExecutionDetailDto executionDetail, boolean dateAfterDeath) {
    	Date date = getPlannedDate(executionDetail);
    	if(date==null)
    		return null;
    	Date executionDate = Date.from(date.toInstant().plus(executionDetail.getDeviation()));
    	if(!dateAfterDeath && executionDetail.getAssignment().getBiosample() != null && biosampleService.isDeadAt(executionDetail.getAssignment().getBiosample(), executionDate))
    		return null;
    	return executionDate;
    }
    
    public Date getPlannedDate(ExecutionDetailDto executionDetail) {
        return assignmentService.getDate(executionDetail.getAssignment(), getPhase(executionDetail).getPhase(), true);
    }
    
    public PhaseDto getPhase(ExecutionDetailDto executionDetail) {
        return executionDetail.getPhaseLink() != null ? executionDetail.getPhaseLink().getPhase() : null;
    }

	public StudyAction getAction(ExecutionDetailDto executionDetails) {
		return executionDetails.getPhaseLink().getSchedule().getActionPattern().getAction();
	}

	public List<ExecutionDetailDto> map(List<ExecutionDetail> executionDetails) {
		List<ExecutionDetailDto> res = new ArrayList<ExecutionDetailDto>();
		for(ExecutionDetail executionDetail : executionDetails) {
			res.add(map(executionDetail));
		}
		return res;
	}
	
	public ExecutionDetailDto map(ExecutionDetail executionDetail) {
		ExecutionDetailDto executionDetailDto = idToExecutionDetail.get(executionDetail.getId());
		if(executionDetailDto==null) {
			executionDetailDto = dozerMapper.map(executionDetail, ExecutionDetailDto.class,"executionDetailCustomMapping");
			if(idToExecutionDetail.get(executionDetail.getId())==null)
				idToExecutionDetail.put(executionDetail.getId(), executionDetailDto);
			else
				executionDetailDto = idToExecutionDetail.get(executionDetail.getId());
		}
		return executionDetailDto;
	}
	
	@Transactional
	public void save(ExecutionDetailDto executionDetail) throws Exception {
		save(executionDetail, false);
	}
	
	protected void save(ExecutionDetailDto executionDetail, Boolean cross) throws Exception {
		try {
			if(!savedItems.contains(executionDetail)) {
				savedItems.add(executionDetail);
				if(executionDetail.getAssignment().getId()==Constants.NEWTRANSIENTID)assignmentService.save(executionDetail.getAssignment(), true);
				if(executionDetail.getPhaseLink().getId()==Constants.NEWTRANSIENTID)schedulePhaseService.save(executionDetail.getPhaseLink(), true);
				executionDetail.setUpdDate(new Date());
				executionDetail.setUpdUser(UserUtil.getUsername());
				if(executionDetail.getId().equals(Constants.NEWTRANSIENTID)) {
					executionDetail.setCreDate(new Date());
					executionDetail.setCreUser(UserUtil.getUsername());
				}
				executionDetail.setId(saveOrUpdate(dozerMapper.map(executionDetail, ExecutionDetail.class, "executionDetailCustomMapping")));
				idToExecutionDetail.put(executionDetail.getId(), executionDetail);
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

	@Transactional
	public void delete(ExecutionDetailDto executionDetail) {
		delete(executionDetail, false);
	}
	
	protected void delete(ExecutionDetailDto executionDetail, Boolean cross) {
		executionDetailDao.delete(executionDetail.getId());
	}

	 public void setAssignment(ExecutionDetailDto executionDetail, AssignmentDto assignment) {
        setAssignment(executionDetail, assignment, false);
    }

    @SuppressWarnings("deprecation")
	public void setAssignment(ExecutionDetailDto executionDetail, AssignmentDto assignment, boolean cross) {
        //prevent endless loop
        if (Objects.equals(executionDetail.getAssignment(), assignment))
            return;
        //remove from the old owner
        if (executionDetail.getAssignment() !=null && !(cross && assignment == null))
        	assignmentService.removeExecutionDetails(executionDetail.getAssignment(), executionDetail, true);
        //set new owner
        executionDetail.setAssignment(assignment);
        //set myself to new owner
        if (assignment !=null && !cross)
        	assignmentService.addExecutionDetails(assignment, executionDetail, true);
    }

    public void setSchedulePhase(ExecutionDetailDto executionDetail, SchedulePhaseDto schedulePhase) {
        setSchedulePhase(executionDetail, schedulePhase, false);
    }

    @SuppressWarnings("deprecation")
	public void setSchedulePhase(ExecutionDetailDto executionDetail, SchedulePhaseDto schedulePhase, boolean cross) {
        //prevent endless loop
        if (Objects.equals(executionDetail.getPhaseLink(), schedulePhase))
            return;
        //remove from the old owner
        if (executionDetail.getPhaseLink() !=null && !(cross && schedulePhase== null))
        	schedulePhaseService.removeExecutionDetails(executionDetail.getPhaseLink(), executionDetail, true);
        //set new owner
        executionDetail.setPhaseLink(schedulePhase);
        //set myself to new owner
        if (schedulePhase !=null && !cross)
        	schedulePhaseService.addExecutionDetails(schedulePhase, executionDetail, true);
    }

	public ExecutionDetailDto newExecutionDetails(AssignmentDto assignment, SchedulePhaseDto schedulePhase) {
		ExecutionDetailDto executionDetail = new ExecutionDetailDto();
		setAssignment(executionDetail, assignment);
		setSchedulePhase(executionDetail, schedulePhase);
		return executionDetail;
	}

}
