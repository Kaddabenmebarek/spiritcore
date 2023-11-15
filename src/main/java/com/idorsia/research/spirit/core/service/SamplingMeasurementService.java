package com.idorsia.research.spirit.core.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.dao.SamplingMeasurementDao;
import com.idorsia.research.spirit.core.dto.SamplingMeasurementAttributeDto;
import com.idorsia.research.spirit.core.dto.SamplingMeasurementDto;
import com.idorsia.research.spirit.core.dto.view.Measurement;
import com.idorsia.research.spirit.core.model.SamplingMeasurement;
import com.idorsia.research.spirit.core.model.SamplingMeasurementAttribute;
import com.idorsia.research.spirit.core.util.UserUtil;

@Service
public class SamplingMeasurementService extends AbstractService implements Serializable {

	private static final long serialVersionUID = 7934943304221461172L;
	@Autowired
	private SamplingMeasurementDao samplingMeasurementDao;
	@Autowired
	private SamplingMeasurementAttributeService samplingMeasurementAttributeService;
	@Autowired
	private SamplingService samplingService;
	@Autowired
	private AssayService assayService;
	@SuppressWarnings("unchecked")
	private static Map<Integer, SamplingMeasurementDto> idToSamplingMeasurement = (Map<Integer, SamplingMeasurementDto>) getCacheMap(SamplingMeasurementDto.class);

	public SamplingMeasurement get(Integer id) {
		return samplingMeasurementDao.get(id);
	}

	public List<SamplingMeasurement> getSamplingMeasurementByTest(int testId) {
		return samplingMeasurementDao.getSamplingMeasurementByTest(testId);
	}

	public List<SamplingMeasurement> getSamplingMeasurementBySampling(int samplingId) {
		return samplingMeasurementDao.getSamplingMeasurementBySampling(samplingId);
	}

	public SamplingMeasurement getSamplingMeasurementById(int id) {
		return samplingMeasurementDao.getSamplingMeasurementById(id);
	}

	public List<SamplingMeasurement> getSamplingMeasurementsByAssay(Integer assayId) {
		return samplingMeasurementDao.getSamplingMeasurementsByAssay(assayId);
	}

	public List<SamplingMeasurement> getSamplingMeasurementsBySampling(Integer samplingId) {
		return samplingMeasurementDao.getSamplingMeasurementsBySampling(samplingId);
	}

	public List<SamplingMeasurement> getSamplingMeasurementBySamplingAndAssay(Integer samplingId, Integer assayId) {
		return samplingMeasurementDao.getSamplingMeasurementBySamplingAndAssay(samplingId, assayId);
	}

	public List<SamplingMeasurement> list() {
		return samplingMeasurementDao.list();
	}

	public int getCount() {
		return samplingMeasurementDao.getCount();
	}
	
	public List<SamplingMeasurementDto> map(List<SamplingMeasurement> samplingMeasurements) {
		List<SamplingMeasurementDto> res = new ArrayList<>();
		for(SamplingMeasurement samplingMeasurement : samplingMeasurements) {
			res.add(map(samplingMeasurement));
		}
		return res;
	}
	
	public SamplingMeasurementDto map(SamplingMeasurement samplingMeasurement) {
		SamplingMeasurementDto samplingMeasurementDto = idToSamplingMeasurement.get(samplingMeasurement.getId());
		if(samplingMeasurementDto==null) {
			samplingMeasurementDto = dozerMapper.map(samplingMeasurement,SamplingMeasurementDto.class,"samplingMeasurementCustomMapping");
			if(idToSamplingMeasurement.get(samplingMeasurement.getId())==null)
				idToSamplingMeasurement.put(samplingMeasurement.getId(), samplingMeasurementDto);
			else
				samplingMeasurementDto=idToSamplingMeasurement.get(samplingMeasurement.getId());
		}
		return samplingMeasurementDto;
	}

	public Integer saveOrUpdate(SamplingMeasurement samplingMeasurement) {
		return samplingMeasurementDao.saveOrUpdate(samplingMeasurement);
	}
	
	public int addSamplingMeasurement(SamplingMeasurement samplingMeasurement) {
		return samplingMeasurementDao.addSamplingMeasurement(samplingMeasurement);
	}
	
	@Transactional
	public void save(SamplingMeasurementDto samplingMeasurement) throws Exception {
		save(samplingMeasurement, false);
	}

	@Transactional
	public void save(Collection<SamplingMeasurementDto> samplingMeasurements) {
		for(SamplingMeasurementDto samplingMeasurement : samplingMeasurements) {
			try {				
				save(samplingMeasurement, true);
			} catch(Exception e) {
				e.printStackTrace();
				AbstractService.clearTransient(false);
			}
		}
		AbstractService.clearSavedItem();
		AbstractService.clearTransient(true);
	}

	protected void save(SamplingMeasurementDto samplingMeasurement, Boolean cross) throws Exception {
		try {
			if (samplingMeasurement != null && !savedItems.contains(samplingMeasurement)) {
				savedItems.add(samplingMeasurement);
				if(samplingMeasurement.getId()!=Constants.NEWTRANSIENTID) {
					deleteChildren(samplingMeasurement);
				}
				if (samplingMeasurement.getSampling() != null
						&& samplingMeasurement.getSampling().getId() == Constants.NEWTRANSIENTID)
					samplingService.save(samplingMeasurement.getSampling(), true);
				if (samplingMeasurement.getAssay() != null && samplingMeasurement.getAssay().getId() == Constants.NEWTRANSIENTID)
					assayService.save(samplingMeasurement.getAssay(), true);
				SamplingMeasurement samplingMeasurementPojo = dozerMapper.map(samplingMeasurement, SamplingMeasurement.class, "samplingMeasurementCustomMapping");
				samplingMeasurementPojo.setUpdDate(new Date());
				samplingMeasurementPojo.setUpdUser(UserUtil.getUsername());
				if(samplingMeasurementPojo.getId().equals(Constants.NEWTRANSIENTID)) {
					samplingMeasurementPojo.setCreDate(new Date());
					samplingMeasurementPojo.setCreUser(UserUtil.getUsername());
				}
				samplingMeasurement.setId(saveOrUpdate(samplingMeasurementPojo));
				idToSamplingMeasurement.put(samplingMeasurement.getId(), samplingMeasurement);
				if(samplingMeasurement.getAttributesNoMapping()!=null) {
					for (SamplingMeasurementAttributeDto attribute : samplingMeasurement.getAttributes())
						samplingMeasurementAttributeService.save(attribute, true);
				}
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
	
	private void deleteChildren(SamplingMeasurementDto samplingMeasurement) {
		if(samplingMeasurement.getAttributesNoMapping()!=null) {
			for(SamplingMeasurementAttribute attribut : samplingMeasurementAttributeService.getBySamplingMeasurement(samplingMeasurement.getId())) {
				Boolean found = false;
				for(SamplingMeasurementAttributeDto child : samplingMeasurement.getAttributes()) {
					if(attribut.getId().equals(child.getId())) {
						found=true;
						break;
					}
				}
				if(!found) {
					samplingMeasurementAttributeService.delete(samplingMeasurementAttributeService.map(attribut), true);
				}
			}
		}
	}

	
	@Transactional
	public void delete(Collection<SamplingMeasurementDto> samplingMeasurements) {
		samplingMeasurementDao.delete(getIds(samplingMeasurements));
	}

	@Transactional
	public void delete(SamplingMeasurementDto samplingMeasurement) {
		delete(samplingMeasurement, false);
	}
	
	protected void delete(SamplingMeasurementDto samplingMeasurement, Boolean cross) {
		samplingMeasurementDao.delete(samplingMeasurement.getId());
	}

	public SamplingMeasurementDao getSamplingMeasurementDao() {
		return samplingMeasurementDao;
	}

	public void setSamplingMeasurementDao(SamplingMeasurementDao samplingMeasurementDao) {
		this.samplingMeasurementDao = samplingMeasurementDao;
	}

	public void mapAttributes(SamplingMeasurementDto samplingMeasurement) {
		samplingMeasurement.setAttributes(samplingMeasurementAttributeService.map(samplingMeasurementAttributeService.getBySamplingMeasurement(samplingMeasurement.getId())));
		Collections.sort(samplingMeasurement.getAttributes());
	}

	@SuppressWarnings("deprecation")
	public void getMeasurement(SamplingMeasurementDto samplingMeasurement) {
		Measurement measurement = new Measurement(samplingMeasurement.getAssay());
		measurement.setParameters(samplingMeasurementAttributeService.getValues(samplingMeasurement.getAttributes()));
		samplingMeasurement.setMeasurement(measurement);
	}

	public SamplingMeasurementDto getDto(Integer id) {
		return map(get(id));
	}
}
