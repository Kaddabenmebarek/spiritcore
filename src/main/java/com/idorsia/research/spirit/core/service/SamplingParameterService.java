package com.idorsia.research.spirit.core.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.dao.SamplingParameterDao;
import com.idorsia.research.spirit.core.dto.SamplingParameterDto;
import com.idorsia.research.spirit.core.model.SamplingParameter;
import com.idorsia.research.spirit.core.util.UserUtil;

@Service
public class SamplingParameterService extends AbstractService implements Serializable {

	private static final long serialVersionUID = 7279249259249981575L;
	@Autowired
	private SamplingParameterDao samplingParameterDao;
	@Autowired
	private SamplingService samplingService;
	@Autowired
	private BiotypeMetadataService biotypeMetadataService;
	@SuppressWarnings("unchecked")
	private static Map<Integer, SamplingParameterDto> idToSamplingParameter = (Map<Integer, SamplingParameterDto>) getCacheMap(SamplingParameterDto.class);


	public SamplingParameter get(Integer id) {
		return samplingParameterDao.get(id);
	}

	public List<SamplingParameter> getSamplingParameterByBiotypeMetadata(int biotypeMetadataId) {
		return samplingParameterDao.getSamplingParameterByBiotypeMetadata(biotypeMetadataId);
	}

	public List<SamplingParameter> getSamplingParameterBySampling(int samplingId) {
		return samplingParameterDao.getSamplingParameterBySampling(samplingId);
	}

	public List<SamplingParameter> list() {
		return samplingParameterDao.list();
	}

	public int getCount() {
		return samplingParameterDao.getCount();
	}

	public List<SamplingParameterDto> map(List<SamplingParameter> samplingParameters) {
		List<SamplingParameterDto> res = new ArrayList<>();
		for(SamplingParameter samplingParameter : samplingParameters) {
			res.add(map(samplingParameter));
		}
		return res;
	}
	
	public SamplingParameterDto map(SamplingParameter samplingParameter) {
		SamplingParameterDto samplingParameterDto = idToSamplingParameter.get(samplingParameter.getId());
		if(samplingParameterDto==null) {
			samplingParameterDto = dozerMapper.map(samplingParameter,SamplingParameterDto.class,"samplingParameterCustomMapping");
			if(idToSamplingParameter.get(samplingParameter.getId())==null)
				idToSamplingParameter.put(samplingParameter.getId(), samplingParameterDto);
			else
				samplingParameterDto=idToSamplingParameter.get(samplingParameter.getId());
		}
		return samplingParameterDto;
	}

	public Integer saveOrUpdate(SamplingParameter samplingParameter) {
		return samplingParameterDao.saveOrUpdate(samplingParameter);
	}
	
	public int addSamplingMeasurement(SamplingParameter samplingParameter) {
		return samplingParameterDao.addSamplingParameter(samplingParameter);
	}
	
	@Transactional
	public void save(SamplingParameterDto samplingParameter) throws Exception {
		save(samplingParameter, false);
	}

	@Transactional
	public void save(Collection<SamplingParameterDto> samplingParameters) {
		for(SamplingParameterDto samplingParameter : samplingParameters) {
			try {				
				save(samplingParameter, true);
			} catch(Exception e) {
				e.printStackTrace();
				AbstractService.clearTransient(false);
			}
		}
		AbstractService.clearSavedItem();
		AbstractService.clearTransient(true);
	}

	protected void save(SamplingParameterDto samplingParameter, Boolean cross) throws Exception {
		try {
			if (samplingParameter != null && !savedItems.contains(samplingParameter)) {
				savedItems.add(samplingParameter);
				if (samplingParameter.getSampling() != null
						&& samplingParameter.getSampling().getId() == Constants.NEWTRANSIENTID)
					samplingService.save(samplingParameter.getSampling(), true);
				if (samplingParameter.getBiotypemetadata() != null && samplingParameter.getBiotypemetadata().getId().equals(Constants.NEWTRANSIENTID))
					biotypeMetadataService.save(samplingParameter.getBiotypemetadata(), true);
				SamplingParameter samplingParameterPojo = dozerMapper.map(samplingParameter, SamplingParameter.class, "samplingParameterCustomMapping");
				samplingParameterPojo.setUpdDate(new Date());
				samplingParameterPojo.setUpdUser(UserUtil.getUsername());
				if(samplingParameterPojo.getId().equals(Constants.NEWTRANSIENTID)) {
					samplingParameterPojo.setCreDate(new Date());
					samplingParameterPojo.setCreUser(UserUtil.getUsername());
				}
				samplingParameter.setId(saveOrUpdate(samplingParameterPojo));
				idToSamplingParameter.put(samplingParameter.getId(), samplingParameter);
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
	public void delete(Collection<SamplingParameterDto> samplingParameters) {
		samplingParameterDao.delete(getIds(samplingParameters));
	}

	@Transactional
	public void delete(SamplingParameterDto samplingParameter) {
		delete(samplingParameter, false);
	}
	
	protected void delete(SamplingParameterDto samplingParameter, Boolean cross) {
		samplingParameterDao.delete(samplingParameter.getId());
	}

	public SamplingParameterDao getSamplingParameterDao() {
		return samplingParameterDao;
	}

	public void setSamplingParameterDao(SamplingParameterDao samplingParameterDao) {
		this.samplingParameterDao = samplingParameterDao;
	}

}
