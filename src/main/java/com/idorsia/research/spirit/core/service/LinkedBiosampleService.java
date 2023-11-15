package com.idorsia.research.spirit.core.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.dao.LinkedBiosampleDao;
import com.idorsia.research.spirit.core.dto.LinkedBiosampleDto;
import com.idorsia.research.spirit.core.model.LinkedBiosample;

@Service
public class LinkedBiosampleService extends AbstractService implements Serializable{

	private static final long serialVersionUID = 7793566086216410868L;
	@Autowired
	private LinkedBiosampleDao linkedBiosampleDao;
	@Autowired
	private BiotypeMetadataService biotypeMetadataService;
	@Autowired
	private BiosampleService biosampleService;
	
	@SuppressWarnings("unchecked")
	private static Map<Integer, LinkedBiosampleDto> idToLinkedBiosample = (Map<Integer, LinkedBiosampleDto>) getCacheMap(LinkedBiosampleDto.class);
	
	public LinkedBiosample get(Integer id) {
		return linkedBiosampleDao.get(id);
	}
	
	public int addLinkedBiosample(LinkedBiosample linkedBiosample) {
		return linkedBiosampleDao.addLinkedBiosample(linkedBiosample);
	}

	public List<LinkedBiosample> getLinkedBiosamplesByBiosample(Integer biosampleId) {
		return linkedBiosampleDao.getLinkedBiosamplesByBiosample(biosampleId);
	}

	public List<LinkedBiosample> getLinkedBiosamplesByLinkedBiosample(Integer linkedBiosampleId) {
		return linkedBiosampleDao.getLinkedBiosamplesByLinkedBiosample(linkedBiosampleId);
	}

	public List<LinkedBiosample> getLinkedBiosamplesByMetadata(Integer metadataId) {
		return linkedBiosampleDao.getLinkedBiosamplesByMetadata(metadataId);
	}
	
	public LinkedBiosampleDao getLinkedBiosampleDao() {
		return linkedBiosampleDao;
	}

	public void setLinkedBiosampleDao(LinkedBiosampleDao linkedBiosampleDao) {
		this.linkedBiosampleDao = linkedBiosampleDao;
	}
	
	public LinkedBiosampleDto map(LinkedBiosample linkedBiosample) {
		LinkedBiosampleDto linkedBiosampleDto = idToLinkedBiosample.get(linkedBiosample.getId());
		if(linkedBiosampleDto==null) {
			linkedBiosampleDto = dozerMapper.map(linkedBiosample,LinkedBiosampleDto.class,"linkedBiosampleCustomMapping");
			if(idToLinkedBiosample.get(linkedBiosample.getId())==null)
				idToLinkedBiosample.put(linkedBiosample.getId(), linkedBiosampleDto);
			else
				linkedBiosampleDto = idToLinkedBiosample.get(linkedBiosample.getId());
		}
		return linkedBiosampleDto;
	}
	
	public List<LinkedBiosampleDto> map(List<LinkedBiosample> linkedBiosamples) {
		List<LinkedBiosampleDto> result = new ArrayList<>();
		for(LinkedBiosample linkedBiosample : linkedBiosamples) {
			result.add(map(linkedBiosample));
		}
		return result;
	}

	@Transactional
	public void save(LinkedBiosampleDto linkedBiosample) throws Exception {
		save(linkedBiosample, false);
	}
	
	protected void save(LinkedBiosampleDto linkedBiosample, Boolean cross) throws Exception {
		try {
			if(!savedItems.contains(linkedBiosample)) {
				savedItems.add(linkedBiosample);
				if(linkedBiosample.getBiosample().getId().equals(Constants.NEWTRANSIENTID))
					biosampleService.save(linkedBiosample.getBiosample());
				if(linkedBiosample.getLinkedBiosample().getId().equals(Constants.NEWTRANSIENTID))
					biosampleService.save(linkedBiosample.getLinkedBiosample());
				if(linkedBiosample.getMetadata().getId().equals(Constants.NEWTRANSIENTID))
					biotypeMetadataService.save(linkedBiosample.getMetadata());
				linkedBiosampleDao.saveOrUpdate(dozerMapper.map(linkedBiosample, LinkedBiosample.class, "linkedBiosampleCustomMapping"));
				idToLinkedBiosample.put(linkedBiosample.getId(), linkedBiosample);
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
	public void delete(LinkedBiosampleDto linkedBiosample) {
		delete(linkedBiosample, false);
	}
	
	protected void delete(LinkedBiosampleDto linkedBiosample, Boolean cross) {
		linkedBiosampleDao.delete(linkedBiosample.getId());
	}
}
