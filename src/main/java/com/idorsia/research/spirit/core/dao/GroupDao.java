package com.idorsia.research.spirit.core.dao;

import java.util.List;

import com.idorsia.research.spirit.core.model.Group;

public interface GroupDao {

	public Group get(Integer id);
	
	public List<Group> list();

	public int getCount();
	
	public Integer saveOrUpdate(Group group);

	public int addGroup(Group group);
	
	public void delete(int groupId);

	public List<Group> getByStage(int stageId);

	public List<Group> getByStudy(Integer studyId);
	
}
