package com.idorsia.research.spirit.core.service;

import static com.idorsia.research.spirit.core.util.MiscUtils.nullSupportEqual;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.dao.ResultAssignmentDao;
import com.idorsia.research.spirit.core.dto.AssayResultDto;
import com.idorsia.research.spirit.core.dto.AssignmentDto;
import com.idorsia.research.spirit.core.dto.ResultAssignmentDto;
import com.idorsia.research.spirit.core.model.ResultAssignment;
import com.idorsia.research.spirit.core.util.UserUtil;

@Service
public class ResultAssignmentService extends AbstractService implements Serializable {

	private static final long serialVersionUID = 6948688761297213692L;

	@SuppressWarnings("unchecked")
	private static Map<Integer, ResultAssignmentDto> idToResultAssignment = (Map<Integer, ResultAssignmentDto>) getCacheMap(ResultAssignmentDto.class);
	
	@Autowired
	private ResultAssignmentDao resultAssignmentDao;
	@Autowired
	private AssignmentService assignmentService;
	@Autowired
	private AssayResultService assayResultService;
	
	public int addAssayResult(ResultAssignment resultAssignment) {
		return resultAssignmentDao.addResultAssignment(resultAssignment);
	}

	public Integer saveOrUpdate(ResultAssignment resultAssignment) {
		return resultAssignmentDao.saveOrUpdate(resultAssignment);
	}

	public ResultAssignment get(Integer id) {
		return resultAssignmentDao.get(id);
	}

	public List<ResultAssignment> getResultAssignmentByAssignment(Integer assignmentId){
		return resultAssignmentDao.getResultAssignmentByAssignment(assignmentId);
	}
	
	public List<ResultAssignment> getResultAssignmentByResult(Integer resultId) {
		return resultAssignmentDao.getResultAssignmentByResult(resultId);
	}

	public ResultAssignment getResultAssignmentByAssignmentAndAssay(Integer assignmentId, Integer assayId){
		return resultAssignmentDao.getResultAssignmentbyAssignmentAndAssay(assignmentId, assayId);
	}

	public ResultAssignment getResultAssignmentById(Integer resultAssignmentId){
		return resultAssignmentDao.getResultAssignmentById(resultAssignmentId);
	}

	public List<ResultAssignmentDto> map(List<ResultAssignment> resultAssignments) {
		List<ResultAssignmentDto> res = new ArrayList<ResultAssignmentDto>();
		for(ResultAssignment resultAssignment : resultAssignments) {
			res.add(map(resultAssignment));
		}
		return res;
	}
	
	public ResultAssignmentDto map(ResultAssignment resultAssignment) {
		ResultAssignmentDto resultAssignmentDto = idToResultAssignment.get(resultAssignment.getId());
		if(resultAssignmentDto==null) {
			resultAssignmentDto = dozerMapper.map(resultAssignment, ResultAssignmentDto.class,"resultAssignmentCustomMapping");
			if(idToResultAssignment.get(resultAssignment.getId())==null)
				idToResultAssignment.put(resultAssignment.getId(), resultAssignmentDto);
			else
				resultAssignmentDto = idToResultAssignment.get(resultAssignment.getId());
		}
		return resultAssignmentDto;
	}

	@Transactional
	public void save(ResultAssignmentDto resultAssignment) throws Exception {
		save(resultAssignment, false);
	}
	
	protected void save(ResultAssignmentDto resultAssignment, Boolean cross) throws Exception {
		try {
			if(resultAssignment!=null && !savedItems.contains(resultAssignment)) {
				savedItems.add(resultAssignment);
				if(resultAssignment.getAssayResult().getId()==Constants.NEWTRANSIENTID)assayResultService.save(resultAssignment.getAssayResult(), true);
				if(resultAssignment.getAssignment().getId()==Constants.NEWTRANSIENTID)assignmentService.save(resultAssignment.getAssignment(), true);
				resultAssignment.setUpdDate(new Date());
				resultAssignment.setUpdUser(UserUtil.getUsername());
				if(resultAssignment.getId().equals(Constants.NEWTRANSIENTID)) {
					resultAssignment.setCreDate(new Date());
					resultAssignment.setCreUser(UserUtil.getUsername());
				}
				resultAssignment.setId(saveOrUpdate(dozerMapper.map(resultAssignment, ResultAssignment.class, "resultAssignmentCustomMapping")));
				idToResultAssignment.put(resultAssignment.getId(), resultAssignment);
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
	public void delete(ResultAssignmentDto resultAssignment) {
		delete(resultAssignment, false);
	}
	
	protected void delete(ResultAssignmentDto resultAssignment, Boolean cross) {
		resultAssignmentDao.delete(resultAssignment.getId());
	}
	
	public ResultAssignmentDto getResultAssignmentDto(Integer id) {
		return map(get(id));
	}

	 public void setAssignment(ResultAssignmentDto resultAssignment, AssignmentDto assignment) {
		 setAssignment(resultAssignment, assignment, false);
    }

    @SuppressWarnings("deprecation")
	public void setAssignment(ResultAssignmentDto resultAssignment, AssignmentDto assignment, boolean cross) {
        //prevent endless loop
        if (nullSupportEqual(resultAssignment.getAssignment(), assignment))
            return;
        //remove from the old owner
        if (resultAssignment.getAssignment() !=null && !(cross && assignment == null))
        	assignmentService.removeResultAssignment(resultAssignment.getAssignment(), resultAssignment, true);
        //set new owner
        resultAssignment.setAssignment(assignment);
        //set myself to new owner
        if (assignment !=null && !cross)
            assignmentService.addResultAssignment(assignment, resultAssignment, true);
    }

    public ResultAssignmentDto newResultAssignmentDto(AssignmentDto assignment, AssayResultDto result) {
		ResultAssignmentDto resultAssignment = new ResultAssignmentDto(result);
		setAssignment(resultAssignment, assignment);
		return resultAssignment;
	}
}
