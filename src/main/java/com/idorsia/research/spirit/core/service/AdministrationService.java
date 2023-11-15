package com.idorsia.research.spirit.core.service;

import static com.idorsia.research.spirit.core.util.MiscUtils.nullSupportEqual;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.measure.Quantity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.dao.AdministrationDao;
import com.idorsia.research.spirit.core.dto.AdministrationDto;
import com.idorsia.research.spirit.core.dto.BiosampleDto;
import com.idorsia.research.spirit.core.dto.NamedTreatmentDto;
import com.idorsia.research.spirit.core.dto.PhaseDto;
import com.idorsia.research.spirit.core.dto.view.Execution;
import com.idorsia.research.spirit.core.model.Administration;
import com.idorsia.research.spirit.core.util.UserUtil;

@Service
public class AdministrationService extends AbstractService implements Serializable {

	private static final long serialVersionUID = 1194505042421647745L;

	@SuppressWarnings({ "unchecked" })
	private Map<Integer, AdministrationDto> idToAdmninistration = (Map<Integer, AdministrationDto>) getCacheMap(
			AdministrationDto.class);

	@Autowired
	private AdministrationDao administrationDao;
	@Autowired
	private BiosampleService biosampleService;
	@Autowired
	private NamedTreatmentService namedTreatmentService;
	@Autowired
	ExecutionService executionService;
	@Autowired
	private PhaseService phaseService;

	public Administration get(Integer id) {
		return administrationDao.get(id);
	}

	public List<Administration> list() {
		return administrationDao.list();
	}

	public List<Administration> getByNamedTreatment(Integer namedTreatmentId) {
		if(namedTreatmentId.equals(Constants.NEWTRANSIENTID))
			return new ArrayList<>();
		return administrationDao.getByNamedTreatment(namedTreatmentId);
	}

	public List<Administration> getByBiosample(Integer biosampleId) {
		if(biosampleId.equals(Constants.NEWTRANSIENTID))
			return new ArrayList<>();
		return administrationDao.getByBiosample(biosampleId);
	}

	public List<Administration> getByPhase(Integer phaseId) {
		if(phaseId.equals(Constants.NEWTRANSIENTID))
			return new ArrayList<>();
		return administrationDao.getByPhase(phaseId);
	}

	public List<Administration> getByStudy(Integer studyId) {
		if(studyId.equals(Constants.NEWTRANSIENTID))
			return new ArrayList<>();
		return administrationDao.getByStudyId(studyId);
	}

	public List<Administration> getByBiosamplesAndPhase(List<Integer> biosamples, Integer phaseId) {
		return administrationDao.getByBiosamplesAndPhase(biosamples, phaseId);
	}

	public AdministrationDto map(Administration administration) {
		if (administration == null)
			return null;
		AdministrationDto administrationDto = idToAdmninistration.get(administration.getId());
		if (administrationDto == null) {
			administrationDto = dozerMapper.map(administration, AdministrationDto.class, "administrationCustomMapping");
			if (idToAdmninistration.get(administration.getId()) == null)
				idToAdmninistration.put(administration.getId(), administrationDto);
			else
				administrationDto = idToAdmninistration.get(administration.getId());
			Execution execution = new Execution();
			execution.setExecutionDate(administration.getExecutiondate());
			administrationDto.setExecution(execution);
		}
		return administrationDto;
	}


	@Transactional
	public void save(AdministrationDto administration) throws Exception {
		save(administration, false);
	}
	
	@Transactional
	public void save(List<AdministrationDto> administrations) {
		for(AdministrationDto administration : administrations) {
			try {				
				save(administration, true);
			} catch(Exception e) {
				e.printStackTrace();
				AbstractService.clearTransient(false);
			}
		}
		AbstractService.clearSavedItem();
		AbstractService.clearTransient(true);
	}

	protected void save(AdministrationDto administration, Boolean cross) throws Exception {
		try{
			if (!savedItems.contains(administration)) {
				savedItems.add(administration);
				if (administration.getBiosample().getId() == Constants.NEWTRANSIENTID)
					biosampleService.save(administration.getBiosample(), true);
				if (administration.getPhase().getId() == Constants.NEWTRANSIENTID)
					phaseService.save(administration.getPhase(), true);
				if (administration.getNamedTreatment().getId() == Constants.NEWTRANSIENTID)
					namedTreatmentService.save(administration.getNamedTreatment(), true);
				administration.setUpdDate(new Date());
				administration.setUpdUser(UserUtil.getUsername());
				if(administration.getId().equals(Constants.NEWTRANSIENTID)) {
					administration.setCreDate(new Date());
					administration.setCreUser(UserUtil.getUsername());
				}
				administration.setId(
						saveOrUpdate(dozerMapper.map(administration, Administration.class, "administrationCustomMapping")));
				idToAdmninistration.put(administration.getId(), administration);
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
	public void delete(AdministrationDto administration) {
		delete(administration, false);
	}
	
	protected void delete(AdministrationDto administration, boolean cross) {
		administrationDao.delete(administration.getId());
	}

	public List<AdministrationDto> map(List<Administration> administrations) {
		List<AdministrationDto> result = new ArrayList<>();
		for (Administration administration : administrations) {
			result.add(map(administration));
		}
		return result;
	}

	public int getCount() {
		return administrationDao.getCount();
	}

	public int addAdministration(Administration administration) {
		return administrationDao.addAdministration(administration);
	}

	public Integer saveOrUpdate(Administration administration) {
		return administrationDao.saveOrUpdate(administration);
	}

	public AdministrationDao getAdministrationDao() {
		return administrationDao;
	}

	public void setAdministrationDao(AdministrationDao administrationDao) {
		this.administrationDao = administrationDao;
	}

	public List<AdministrationDto> getAdministrations(List<BiosampleDto> biosamples, PhaseDto phase) {
		List<Integer> ids = new ArrayList<>();
		List<AdministrationDto> results = new ArrayList<>();
		for (BiosampleDto b : biosamples) {
			ids.add(b.getId());
		}
		if (ids.size() > 0) {
			List<AdministrationDto> adms = map(
					administrationDao.getByBiosamplesAndPhase(ids, phase == null ? null : phase.getId()));
			results.addAll(adms);
		}
		return results;
	}

	public boolean setBiosample(AdministrationDto administration, BiosampleDto biosample) {
		setBiosample(administration, biosample, false);
		return true;
	}

	@SuppressWarnings("deprecation")
	public void setBiosample(AdministrationDto administration, BiosampleDto biosample, boolean cross) {
		// prevent endless loop
		if (nullSupportEqual(administration.getBiosample(), biosample))
			return;
		// remove from the old owner
		if (administration.getBiosample() != null && !(cross && biosample == null))
			biosampleService.removeAdministration(administration.getBiosample(), administration, true);
		// set new owner
		administration.setBiosample(biosample);
		// set myself to new owner
		if (biosample != null && !cross)
			biosampleService.addAdministration(biosample, administration, true);
	}

	public Date getExecutionDate(AdministrationDto administration) {
		return administration.getExecution() == null
				? executionService.getExecutionDateCalculated(administration.getBiosample(), administration.getPhase())
				: administration.getExecution().getExecutionDate();
	}

	public AdministrationDto newAdministration(NamedTreatmentDto namedTreatment, BiosampleDto biosample, PhaseDto phase,
			Date executionDate, String batch, Quantity<?> effectiveAmount, String comment) {
		AdministrationDto administration = new AdministrationDto();
		administration.setNamedTreatment(namedTreatment);
		setBiosample(administration, biosample);
		administration.setPhase(phase);
		setExecutionDate(administration, executionDate);
		administration.setBatchId(batch);
		administration.setEffectiveAmountQuantity(effectiveAmount);
		administration.setAdmComment(comment);
		return administration;
	}

	private boolean setExecutionDate(AdministrationDto administration, Date value) {
		if (administration.getExecution() == null) {
			if (value == null)
				return false;
			administration.setExecution(new Execution());
		}
		administration.getExecution().setExecutionDate(value);
		return true;

	}

	public void setExecutiondate(AdministrationDto administration, Date date) {
		Execution e = administration.getExecution();
		if(e==null)
			e = new Execution();
		e.setExecutionDate(date);
	}
}
