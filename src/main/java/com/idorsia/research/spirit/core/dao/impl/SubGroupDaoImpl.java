package com.idorsia.research.spirit.core.dao.impl;

import java.util.List;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.dao.AbstractDao;
import com.idorsia.research.spirit.core.dao.SubGroupDao;
import com.idorsia.research.spirit.core.model.SubGroup;

@Repository
public class SubGroupDaoImpl extends AbstractDao<SubGroup> implements SubGroupDao {

	private static final String TABLE_NAME = "Subgroup";
	
	@Override
	public SubGroup get(Integer id) {
		return super.get(TABLE_NAME, SubGroup.class, id);
	}

	@Override
	public List<SubGroup> list() {
		return super.getlist(TABLE_NAME, SubGroup.class);
	}
	
	@Override
	public List<SubGroup> getByRandoFromGroup(Integer groupId){
		String sql = String.format("SELECT * FROM " + TABLE_NAME + " WHERE RANDOFROMGROUP_ID = %s", groupId);
		return (List<SubGroup>) super.getObjectList(TABLE_NAME, SubGroup.class, sql);
	}
	
	@Override
	public List<SubGroup> getByRandoFromSubGroup(Integer subGroupId){
		String sql = String.format("SELECT * FROM " + TABLE_NAME + " WHERE RANDOFROMSUBGROUP_ID = %s", subGroupId);
		return (List<SubGroup>) super.getObjectList(TABLE_NAME, SubGroup.class, sql);
	}

	@Override
	public int getCount() {
		return super.getCount(TABLE_NAME);
	}

	@Override
	public Integer saveOrUpdate(SubGroup subGroup) {
		if (subGroup.getId() != null && subGroup.getId() > 0) {
			if(!subGroup.equals(get(subGroup.getId()))) {
				String sql = "UPDATE "+TABLE_NAME+" SET "
						+ "GROUP_ID=:groupId, "
						+ "NAME=:name, "
						+ "NO=:no, "
						+ "RANDOFROMSUBGROUP_ID=:randoFromSubGroupId, "
						+ "RANDOFROMGROUP_ID=:randoFromGroupId, "
						+ "CREDATE=:creDate, "
						+ "CREUSER=:creUser, "
						+ "UPDDATE=:updDate, "
						+ "UPDUSER=:updUser "
						+ "WHERE id=:id";
			    SqlParameterSource namedParameters = new MapSqlParameterSource()
			    		.addValue("id", subGroup.getId())
			    		.addValue("groupId", subGroup.getGroupId())
			    		.addValue("name", subGroup.getName())
			    		.addValue("no", subGroup.getNo())
			    		.addValue("randoFromSubGroupId", subGroup.getRandofromsubgroupId())
			    		.addValue("randoFromGroupId", subGroup.getRandofromgroupId())
			    		.addValue("creDate", subGroup.getCreDate())
			    		.addValue("creUser", subGroup.getCreUser())
			    		.addValue("updDate", subGroup.getUpdDate())
			    		.addValue("updUser", subGroup.getUpdUser());
			    super.saveOrUpdate(sql, namedParameters);
			}
		} else {
			subGroup.setId(getSequence(Constants.STUDY_SUBGROUP_SEQUENCE_NAME));
			addSubGroup(subGroup);
			addTransient(subGroup);
		}
		addIdToObject(SubGroup.class, subGroup.getId(), subGroup);
		return subGroup.getId();
	}

	@Override
	public int addSubGroup(SubGroup subGroup) {
		return super.add(TABLE_NAME, subGroup);
	}

	@Override
	public void delete(int subGroupId) {
		super.delete(TABLE_NAME, subGroupId);
	}

	@Override
	public List<SubGroup> getByGroup(int groupId) {
		String sql = String.format("SELECT * FROM " + TABLE_NAME + " WHERE GROUP_ID = '%s'",
				groupId);
		return (List<SubGroup>) super.getObjectList(TABLE_NAME, SubGroup.class, sql);
	}
	
	@Override
	public List<SubGroup> getByStudy(Integer studyId){
		String sql = String.format("SELECT sg.* FROM " + TABLE_NAME + " sg, study_group g, stage s WHERE sg.group_id=g.id and"
				+" g.stage_id=s.id and s.study_id = %s",studyId);
		List<SubGroup> results = (List<SubGroup>) super.getObjectList(TABLE_NAME, SubGroup.class, sql);
		for(SubGroup sg : results) {
			addObjectInCache(sg, SubGroup.class, sg.getId());
		}
		return results;
	}

}
