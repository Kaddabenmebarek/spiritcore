package com.idorsia.research.spirit.core.dao;

import java.util.List;

import com.idorsia.research.spirit.core.model.SubGroupPattern;

public interface SubGroupPatternDao {

	public SubGroupPattern get(Integer id);
	
	public SubGroupPattern getByAcionPattern(int actionPatternId);
	
	public List<SubGroupPattern> list();
	
	public List<SubGroupPattern> getBySubgroup(int subgroupId);

	public int getCount();
	
	public Integer saveOrUpdate(SubGroupPattern subgroupPattern);
	
	public int addSubGroupPattern(SubGroupPattern subgroupPattern);

	public void delete(int groupPatternId);
	
	public void deleteByActionPattern(Integer actionPatternId);

	public void deleteBySubGroup(Integer subGroupId);

	public SubGroupPattern getByActionPatternAndSubGroup(int actionPatternId, int subgroupId);

	public List<Integer> getBySubGoups(List<Integer> subGoupIds);
}
