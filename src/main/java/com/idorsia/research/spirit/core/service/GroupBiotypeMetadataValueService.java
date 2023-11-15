package com.idorsia.research.spirit.core.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.dao.GroupBiotypeMetadataValueDao;
import com.idorsia.research.spirit.core.dto.GroupBiotypeMetadataValueDto;
import com.idorsia.research.spirit.core.model.GroupBiotypeMetadataValue;

@Service
public class GroupBiotypeMetadataValueService extends AbstractService implements Serializable {

	private static final long serialVersionUID = 9166939028354584154L;
	@Autowired
	private GroupBiotypeMetadataValueDao groupBiotypeMetadataValueDao;
	@Autowired
	private BiotypeMetadataValueService biotypeMetadataValueService;
	@Autowired
	private GroupService groupService;
	
	public GroupBiotypeMetadataValue get(Integer id) {
		return groupBiotypeMetadataValueDao.get(id);
	}
	
	public List<GroupBiotypeMetadataValue> getByBiotypeMetadataValue(int biotypeMetadataValueId) {
		return groupBiotypeMetadataValueDao.getByBiotypeMetadataValue(biotypeMetadataValueId);
	}

	public List<GroupBiotypeMetadataValue> getByGroup(int groupId) {
		return groupBiotypeMetadataValueDao.getByGroup(groupId);
	}

	public List<GroupBiotypeMetadataValue> list() {
		return groupBiotypeMetadataValueDao.list();
	}

	public int getCount() {
		return groupBiotypeMetadataValueDao.getCount();
	}

	public void addGroupBiotypeMetadataValue(GroupBiotypeMetadataValue groupBiotypeMetadataValue) {
		groupBiotypeMetadataValueDao.addGroupBiotypeMetadataValue(groupBiotypeMetadataValue);
	}

	public void delete(Integer groupBiotypeMetadataValueId) {
		groupBiotypeMetadataValueDao.delete(groupBiotypeMetadataValueId);
	}
	
	protected void deleteByBiotypeMetadataValue(int biotypeMetadataValueId) {
		groupBiotypeMetadataValueDao.deleteByBiotypeMetadataValue(biotypeMetadataValueId);
	}

	protected void deleteByGroup(int groupId) {
		groupBiotypeMetadataValueDao.deleteByGroup(groupId);
	}
	
	public GroupBiotypeMetadataValueDao getGroupBiotypeMetadataValueDao() {
		return groupBiotypeMetadataValueDao;
	}

	public void setGroupBiotypeMetadataValueDao(GroupBiotypeMetadataValueDao groupBiotypeMetadataValueDao) {
		this.groupBiotypeMetadataValueDao = groupBiotypeMetadataValueDao;
	}
	
	public List<GroupBiotypeMetadataValueDto> map(List<GroupBiotypeMetadataValue> metadatas){
		List<GroupBiotypeMetadataValueDto> results = new ArrayList<>();
		for(GroupBiotypeMetadataValue metadata : metadatas) {
			results.add(map(metadata));
		}
		return results;
	}
	
	public GroupBiotypeMetadataValueDto map(GroupBiotypeMetadataValue groupBiotypeMetadataValue) {
		return dozerMapper.map(groupBiotypeMetadataValue, GroupBiotypeMetadataValueDto.class,"groupBiotypeMetadataValueCustomMapping");
	}

	@Transactional
	public void save(GroupBiotypeMetadataValueDto groupBiotypeMetadataValue) throws Exception {
		save(groupBiotypeMetadataValue, false);
	}
	
	protected void save(GroupBiotypeMetadataValueDto groupBiotypeMetadataValue, Boolean cross) throws Exception {
		try{
			if(!savedItems.contains(groupBiotypeMetadataValue)) {
				savedItems.add(groupBiotypeMetadataValue);
				biotypeMetadataValueService.save(groupBiotypeMetadataValue.getBiotypeMetadataValue(), true);
				if(groupBiotypeMetadataValue.getGroup().getId().equals(Constants.NEWTRANSIENTID))
					groupService.save(groupBiotypeMetadataValue.getGroup(), true);
				groupBiotypeMetadataValueDao.saveOrUpdate(dozerMapper.map(groupBiotypeMetadataValue, GroupBiotypeMetadataValue.class, "groupBiotypeMetadataValueCustomMapping"));
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
	public void delete(GroupBiotypeMetadataValueDto groupBiotypeMetadataValue) {
		delete(groupBiotypeMetadataValue, false);
	}
	
	protected void delete(GroupBiotypeMetadataValueDto groupBiotypeMetadataValue, Boolean cross) {
		groupBiotypeMetadataValueDao.delete(groupBiotypeMetadataValue.getId());
	}

}
