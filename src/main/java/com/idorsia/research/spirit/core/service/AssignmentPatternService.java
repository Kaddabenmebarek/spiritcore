package com.idorsia.research.spirit.core.service;

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
import com.idorsia.research.spirit.core.dao.AssignmentPatternDao;
import com.idorsia.research.spirit.core.dto.AssignmentDto;
import com.idorsia.research.spirit.core.dto.AssignmentPatternDto;
import com.idorsia.research.spirit.core.model.AssignmentPattern;
import com.idorsia.research.spirit.core.util.UserUtil;

@Service
public class AssignmentPatternService extends AbstractService implements Serializable {

	private static final long serialVersionUID = 1418530440819217332L;
	@SuppressWarnings("unchecked")
	private static Map<Integer, AssignmentPatternDto> idToAssignmentPattern = (Map<Integer, AssignmentPatternDto>) getCacheMap(AssignmentPatternDto.class);
	
	@Autowired
	private AssignmentPatternDao assignmentPatternDao;
	@Autowired
	private ActionPatternsService actionPatternsService;
	@Autowired
	private AssignmentService assignmentService;

	public AssignmentPattern get(Integer id) {
		return assignmentPatternDao.get(id);
	}

	public List<AssignmentPattern> getByAssignment(Integer assignmentId) {
		if(assignmentId.equals(Constants.NEWTRANSIENTID))
			return new ArrayList<>();
		return assignmentPatternDao.getByAssignment(assignmentId);
	}

	public List<AssignmentPattern> list() {
		return assignmentPatternDao.list();
	}

	public int getCount() {
		return assignmentPatternDao.getCount();
	}

	public Integer saveOrUpdate(AssignmentPattern assignmentPattern) {
		return assignmentPatternDao.saveOrUpdate(assignmentPattern);
	}

	public int addAssignmentPattern(AssignmentPattern assignmentPattern) {
		return assignmentPatternDao.addAssignmentPattern(assignmentPattern);
	}

	@Transactional
	public void deleteByAssignment(int assignmentId) {
		assignmentPatternDao.deleteByAssignment(assignmentId);
	}
	
	public void deleteByActionPattern(int assignmentPatternId) {
		assignmentPatternDao.deleteByActionPattern(assignmentPatternId);
	}
	
	public List<Integer> getByAssignments(List<AssignmentDto> assignments) {
		List<Integer> assignmentIds = new ArrayList<Integer>();
		for(AssignmentDto st : assignments) {
			assignmentIds.add(st.getId());
		}
		return assignmentPatternDao.getByAssignments(assignmentIds);
	}

	public AssignmentPatternDao getAssignmentPatternDao() {
		return assignmentPatternDao;
	}

	public void setAssignmentPatternDao(AssignmentPatternDao assignmentPatternDao) {
		this.assignmentPatternDao = assignmentPatternDao;
	}

	public Set<AssignmentPatternDto> map(List<AssignmentPattern> assignmentPatterns) {
		Set<AssignmentPatternDto> res = new HashSet<AssignmentPatternDto>();
		for(AssignmentPattern assignmentPattern : assignmentPatterns) {
			res.add(map(assignmentPattern));
		}
		return res;
	}
	
	public AssignmentPatternDto map(AssignmentPattern assignmentPattern) {
		AssignmentPatternDto assignmentPatternDto = idToAssignmentPattern.get(assignmentPattern.getId());
		if(assignmentPatternDto==null) {
			assignmentPatternDto = dozerMapper.map(assignmentPattern, AssignmentPatternDto.class,"assignmentPatternCustomMapping");
			if(idToAssignmentPattern.get(assignmentPattern.getId())==null)
				idToAssignmentPattern.put(assignmentPattern.getId(), assignmentPatternDto);
			else
				assignmentPatternDto = idToAssignmentPattern.get(assignmentPattern.getId());
		}
		return assignmentPatternDto;
	}
	
	@Transactional
	public void save(AssignmentPatternDto assignmentPattern) throws Exception {
		save(assignmentPattern, false);
	}
	
	protected void save(AssignmentPatternDto assignmentPattern, Boolean cross) throws Exception {
		try {
			if(!savedItems.contains(assignmentPattern)) {
				savedItems.add(assignmentPattern);
				assignmentPattern.setUpdDate(new Date());
				assignmentPattern.setUpdUser(UserUtil.getUsername());
				if(assignmentPattern.getId().equals(Constants.NEWTRANSIENTID)) {
					assignmentPattern.setCreDate(new Date());
					assignmentPattern.setCreUser(UserUtil.getUsername());
				}
				if(assignmentPattern.getActionpattern().getId().equals(Constants.NEWTRANSIENTID)) {
					actionPatternsService.save(assignmentPattern.getActionpattern(), true);
				}
				if(assignmentPattern.getAssignment().getId().equals(Constants.NEWTRANSIENTID)) {
					assignmentService.save(assignmentPattern.getAssignment(), true);
				}
				assignmentPattern.setId(saveOrUpdate(dozerMapper.map(assignmentPattern, AssignmentPattern.class, "assignmentPatternCustomMapping")));
				idToAssignmentPattern.put(assignmentPattern.getId(), assignmentPattern);
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
	public void delete(AssignmentPatternDto assignmentPattern) {
		delete(assignmentPattern, false);
	}
	
	protected void delete(AssignmentPatternDto assignmentPattern, Boolean cross) {
		assignmentPatternDao.delete(assignmentPattern.getId());
	}

}
