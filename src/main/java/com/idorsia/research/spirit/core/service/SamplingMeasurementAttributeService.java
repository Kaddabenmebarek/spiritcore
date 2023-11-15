package com.idorsia.research.spirit.core.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.dao.SamplingMeasurementAttributeDao;
import com.idorsia.research.spirit.core.dto.SamplingMeasurementAttributeDto;
import com.idorsia.research.spirit.core.model.SamplingMeasurementAttribute;
import com.idorsia.research.spirit.core.util.UserUtil;

@Service
public class SamplingMeasurementAttributeService extends AbstractService {
	
	private static final long serialVersionUID = -9219613087610413105L;
	
	@SuppressWarnings("unchecked")
	private static Map<Integer, SamplingMeasurementAttributeDto> idToSamplingMeasurementAttribute = (Map<Integer, SamplingMeasurementAttributeDto>) getCacheMap(SamplingMeasurementAttributeDto.class);
	@Autowired
	private SamplingMeasurementService samplingMeasurementService;
	@Autowired
	private AssayAttributeService assayAttributeService;
	@Autowired
	private SamplingMeasurementAttributeDao samplingMeasurementAttributeDao;
	
	
	public Integer saveOrUpdate(SamplingMeasurementAttribute samplingMeasurementAttribute) {
		return samplingMeasurementAttributeDao.saveOrUpdate(samplingMeasurementAttribute);
	}
	
	public int addSamplingMeasurement(SamplingMeasurementAttribute samplingMeasurementAttribute) {
		return samplingMeasurementAttributeDao.addSamplingMeasurementAttribute(samplingMeasurementAttribute);
	}
	
	public List<SamplingMeasurementAttribute> getBySamplingMeasurement(Integer samplingMeasurementId) {
		return samplingMeasurementAttributeDao.getBySamplingMeasurement(samplingMeasurementId);
	}
	
	public List<SamplingMeasurementAttribute> getByAssayAttribute(Integer assayAttributeId) {
		return samplingMeasurementAttributeDao.getByAssayAttribute(assayAttributeId);
	}
	
	public List<SamplingMeasurementAttributeDto> map(List<SamplingMeasurementAttribute> samplings) {
		List<SamplingMeasurementAttributeDto> res = new ArrayList<>();
		for(SamplingMeasurementAttribute sampling : samplings) {
			res.add(map(sampling));
		}
		return res;
	}
	
	public SamplingMeasurementAttributeDto map(SamplingMeasurementAttribute samplingMeasurementAttribute) {
		SamplingMeasurementAttributeDto samplingMeasurementAttributeDto = idToSamplingMeasurementAttribute.get(samplingMeasurementAttribute.getId());
		if(samplingMeasurementAttributeDto==null) {
			samplingMeasurementAttributeDto = dozerMapper.map(samplingMeasurementAttribute,SamplingMeasurementAttributeDto.class,"samplingMeasurementAttributeCustomMapping");
			if(idToSamplingMeasurementAttribute.get(samplingMeasurementAttribute.getId())==null)
				idToSamplingMeasurementAttribute.put(samplingMeasurementAttribute.getId(), samplingMeasurementAttributeDto);
			else
				samplingMeasurementAttributeDto=idToSamplingMeasurementAttribute.get(samplingMeasurementAttribute.getId());
		}
		return samplingMeasurementAttributeDto;
	}
	
	@Transactional
	public void save(SamplingMeasurementAttributeDto samplingMeasurementAttribute) throws Exception {
		save(samplingMeasurementAttribute, false);
	}

	@Transactional
	public void save(Collection<SamplingMeasurementAttributeDto> samplingMeasurementAttributes) {
		for(SamplingMeasurementAttributeDto samplingMeasurementAttribute : samplingMeasurementAttributes) {
			try {				
				save(samplingMeasurementAttribute, true);
			} catch(Exception e) {
				e.printStackTrace();
				AbstractService.clearTransient(false);
			}
		}
		AbstractService.clearSavedItem();
		AbstractService.clearTransient(true);
	}

	protected void save(SamplingMeasurementAttributeDto samplingMeasurementAttribute, Boolean cross) throws Exception {
		try {
			if (samplingMeasurementAttribute != null && !savedItems.contains(samplingMeasurementAttribute)) {
				savedItems.add(samplingMeasurementAttribute);
				if (samplingMeasurementAttribute.getSamplingMeasurement() != null
						&& samplingMeasurementAttribute.getSamplingMeasurement().getId() == Constants.NEWTRANSIENTID)
					samplingMeasurementService.save(samplingMeasurementAttribute.getSamplingMeasurement(), true);
				if (samplingMeasurementAttribute.getAssayAttribute() != null && samplingMeasurementAttribute.getAssayAttribute().getId() == Constants.NEWTRANSIENTID)
					assayAttributeService.save(samplingMeasurementAttribute.getAssayAttribute(), true);
				SamplingMeasurementAttribute samplingMeasurementAttributePojo = dozerMapper.map(samplingMeasurementAttribute, SamplingMeasurementAttribute.class, "samplingMeasurementAttributeCustomMapping");
				samplingMeasurementAttributePojo.setUpdDate(new Date());
				samplingMeasurementAttributePojo.setUpdUser(UserUtil.getUsername());
				if(samplingMeasurementAttributePojo.getId().equals(Constants.NEWTRANSIENTID)) {
					samplingMeasurementAttributePojo.setCreDate(new Date());
					samplingMeasurementAttributePojo.setCreUser(UserUtil.getUsername());
				}
				samplingMeasurementAttribute.setId(saveOrUpdate(samplingMeasurementAttributePojo));
				idToSamplingMeasurementAttribute.put(samplingMeasurementAttribute.getId(), samplingMeasurementAttribute);
			}
		}catch(Exception e) {
			if (!cross) {
				e.printStackTrace();
				AbstractService.clearTransient(false);
			}
			throw e;
		}finally{
			if (!cross) {
				AbstractService.clearSavedItem();
				AbstractService.clearTransient(true);
			}
		}
	}
	
	@Transactional
	public void delete(Collection<SamplingMeasurementAttributeDto> samplingMeasurementAttributes) {
		samplingMeasurementAttributeDao.delete(getIds(samplingMeasurementAttributes));
	}

	@Transactional
	public void delete(SamplingMeasurementAttributeDto samplingMeasurementAttribute) {
		delete(samplingMeasurementAttribute, false);
	}
	
	protected void delete(SamplingMeasurementAttributeDto samplingMeasurementAttribute, Boolean cross) {
		samplingMeasurementAttributeDao.delete(samplingMeasurementAttribute.getId());
	}

	public String[] getValues(List<SamplingMeasurementAttributeDto> attributes) {
		List<String> result = new ArrayList<>();
		for(SamplingMeasurementAttributeDto attribute : attributes) {
			result.add(attribute.getValue());
		}
		return result.toArray(new String[0]);
	}
}
