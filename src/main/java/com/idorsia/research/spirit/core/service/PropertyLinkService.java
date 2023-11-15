package com.idorsia.research.spirit.core.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.idorsia.research.spirit.core.dao.PropertyLinkDao;
import com.idorsia.research.spirit.core.dto.PropertyLinkDto;
import com.idorsia.research.spirit.core.dto.StudyDto;
import com.idorsia.research.spirit.core.model.PropertyLink;

@Service
public class PropertyLinkService extends AbstractService implements Serializable {

	private static final long serialVersionUID = 1817280324275976599L;

	@Autowired
	private PropertyLinkDao propertyLinkDao;
	@Autowired
	private StudyService studyService;
	
	@SuppressWarnings("unchecked")
	private static Map<Integer, PropertyLinkDto> idToPropertyLink = (Map<Integer, PropertyLinkDto>) getCacheMap(PropertyLinkDto.class);

	public PropertyLink get(Integer id) {
		return propertyLinkDao.get(id);
	}

	public PropertyLink getPropertyLinkById(int id) {
		return propertyLinkDao.getPropertyLinkById(id);
	}

	public List<String> getValuesByPopertyId(Integer id) {
		return propertyLinkDao.getValuesByPopertyId(id);
	}

	public List<PropertyLink> list() {
		return propertyLinkDao.list();
	}

	public List<PropertyLink> getPropertyLinksByStudy(Integer studyId) {
		return propertyLinkDao.getPropertyLinksByStudy(studyId);
	}

	public List<PropertyLink> getPropertyLinksByProperty(Integer propertyId) {
		return propertyLinkDao.getPropertyLinksByProperty(propertyId);
	}

	public PropertyLink getPropertyLinkByStudyAndProperty(Integer studyId, Integer propertyId) {
		return propertyLinkDao.getPropertyLinkByStudyAndProperty(studyId, propertyId);
	}

	public int getCount() {
		return propertyLinkDao.getCount();
	}

	public Integer saveOrUpdate(PropertyLink propertyLink) {
		return propertyLinkDao.saveOrUpdate(propertyLink);
	}

	public int addPropertyLink(PropertyLink propertyLink) {
		return propertyLinkDao.addPropertyLink(propertyLink);
	}

	public PropertyLinkDao getPropertyLinkDao() {
		return propertyLinkDao;
	}

	public void setPropertyLinkDao(PropertyLinkDao propertyLinkDao) {
		this.propertyLinkDao = propertyLinkDao;
	}

	public List<PropertyLinkDto> map(List<PropertyLink> propertyLinks) {
		List<PropertyLinkDto> res = new ArrayList<PropertyLinkDto>();
		for(PropertyLink propertyLink : propertyLinks) {
			res.add(map(propertyLink));
		}
		return res;
	}
	
	public PropertyLinkDto map(PropertyLink propertyLink) {
		PropertyLinkDto propertyLinkDto = idToPropertyLink.get(propertyLink.getId());
		if(propertyLinkDto==null) {
			propertyLinkDto = dozerMapper.map(propertyLink, PropertyLinkDto.class,"propertyLinkCustomMapping");
			if(idToPropertyLink.get(propertyLink.getId())==null)
				idToPropertyLink.put(propertyLink.getId(), propertyLinkDto);
			else
				propertyLinkDto = idToPropertyLink.get(propertyLink.getId());
		}
		return propertyLinkDto;
	}
	
	@Transactional
	public void save(PropertyLinkDto property) {
		save(property, false);
	}
	
	protected void save(PropertyLinkDto property, Boolean cross) {
		try {
			if(!savedItems.contains(property)) {
				savedItems.add(property);
				property.setId(saveOrUpdate(dozerMapper.map(property, PropertyLink.class, "propertyLinkCustomMapping")));
				idToPropertyLink.put(property.getId(), property);
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
	public void delete(PropertyLinkDto property) {
		delete(property, false);
	}
	
	protected void delete(PropertyLinkDto property, Boolean cross) {
		propertyLinkDao.delete(property.getId());
	}

	@SuppressWarnings("deprecation")
	public void setStudy(PropertyLinkDto pl, StudyDto study) {
		pl.setStudy(study);
		studyService.addPorperty(study, pl.getProperty().getName(), pl.getValue());
	}
}
