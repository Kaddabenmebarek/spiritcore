package com.idorsia.research.spirit.core.dao;

import java.util.List;

import com.idorsia.research.spirit.core.model.SubGroup;

public interface SubGroupDao {

	public SubGroup get(Integer id);
	
	public List<SubGroup> list();

	public List<SubGroup> getByRandoFromGroup(Integer groupId);
	
	public List<SubGroup> getByRandoFromSubGroup(Integer subGroupId);

	public int getCount();
	
	public Integer saveOrUpdate(SubGroup subGroup);

	public int addSubGroup(SubGroup subGroup);
	
	public void delete(int subGroupId);

	public List<SubGroup> getByGroup(int groupId);
	
	public List<SubGroup> getByStudy(Integer studyId);
}
