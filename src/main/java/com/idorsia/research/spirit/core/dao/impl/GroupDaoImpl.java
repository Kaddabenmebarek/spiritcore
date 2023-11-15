package com.idorsia.research.spirit.core.dao.impl;

import java.util.List;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.dao.AbstractDao;
import com.idorsia.research.spirit.core.dao.GroupDao;
import com.idorsia.research.spirit.core.model.Group;

@Repository
public class GroupDaoImpl extends AbstractDao<Group> implements GroupDao {

	private static final String TABLE_NAME = "Study_Group";
	
	@Override
	public Group get(Integer id) {
		return super.get(TABLE_NAME, Group.class, id);
	}

	@Override
	public List<Group> list() {
		return super.getlist(TABLE_NAME, Group.class);
	}

	@Override
	public int getCount() {
		return super.getCount(TABLE_NAME);
	}

	@Override
	public Integer saveOrUpdate(Group group) {
		if (group.getId() != null && group.getId() > 0) {
			if(!group.equals(get(group.getId()))) {
				String sql = "UPDATE "+TABLE_NAME+" SET "
						+ "IDX=:idx, "
						+ "NAME=:name, "
						+ "COLOR=:color, "
						+ "SEVERITY=:severity, "
						+ "CREDATE=:credate, "
						+ "CREUSER=:creuser, "
						+ "UPDDATE=:upddate, "
						+ "UPDUSER=:upduser, "
						+ "STAGE_ID=:stageId "
						+ "WHERE id=:id";
			    SqlParameterSource namedParameters = new MapSqlParameterSource()
			    		.addValue("id", group.getId())
			    		.addValue("idx", group.getIdx())
			    		.addValue("name", group.getName())
			    		.addValue("color", group.getColor())
			    		.addValue("severity", group.getSeverity())
						.addValue("upddate", group.getUpdDate())
						.addValue("upduser", group.getUpdUser())
						.addValue("credate", group.getCreDate())
						.addValue("creuser", group.getCreUser())
			    		.addValue("stageId", group.getStageId());
			    super.saveOrUpdate(sql, namedParameters);
			}
		} else {
			group.setId(getSequence(Constants.GROUP_SEQUENCE_NAME));
			addGroup(group);
			addTransient(group);
		}
		addIdToObject(Group.class, group.getId(), group);
		return group.getId();
	}

	@Override
	public int addGroup(Group group) {
		return super.add(TABLE_NAME, group);
	}

	@Override
	public void delete(int groupId) {
		super.delete(TABLE_NAME, groupId);
	}

	@Override
	public List<Group> getByStage(int stageId) {
		String sql = String.format("SELECT * FROM " + TABLE_NAME + " WHERE STAGE_ID = '%s'",
				stageId);
		return (List<Group>) super.getObjectList(TABLE_NAME, Group.class, sql);
	}

	@Override
	public List<Group> getByStudy(Integer studyId){
		String sql = String.format("SELECT g.* FROM " + TABLE_NAME + " g, stage s WHERE "
				+"g.stage_id=s.id and s.study_id = %s",studyId);
		List<Group> results = (List<Group>) super.getObjectList(TABLE_NAME, Group.class, sql);
		for(Group g : results) {
			addObjectInCache(g, Group.class, g.getId());
		}
		return results;
	}
}
