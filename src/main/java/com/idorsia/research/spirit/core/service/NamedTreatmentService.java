package com.idorsia.research.spirit.core.service;

import static com.idorsia.research.spirit.core.util.MiscUtils.nullSupportEqual;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.measure.Quantity;
import javax.measure.Unit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.actelion.research.business.ppg.formulation.PpgFormulationCompound;
import com.actelion.research.business.ppg.formulation.PpgTreatment;
import com.actelion.research.business.spi.formulation.SPIFormuCompound;
import com.actelion.research.business.spi.formulation.SpiFormuSubstance;
import com.actelion.research.business.spi.formulation.SpiFormulation;
import com.actelion.research.hts.datacenter.restapi.ppg.DAOPpgTreatment;
import com.actelion.research.hts.datacenter.restapi.spi.DAOSpiFormulation;
import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.dao.NamedTreatmentDao;
import com.idorsia.research.spirit.core.dto.ActionPatternsDto;
import com.idorsia.research.spirit.core.dto.NamedTreatmentDto;
import com.idorsia.research.spirit.core.dto.StudyDto;
import com.idorsia.research.spirit.core.model.NamedTreatment;
import com.idorsia.research.spirit.core.util.UserUtil;
import com.idorsia.research.uom.DBUnit;
import com.idorsia.research.uom.IdorsiaUnits;
import com.idorsia.research.uom.UnitSymbols;

import tech.units.indriya.ComparableQuantity;
import tech.units.indriya.quantity.Quantities;

@Service
public class NamedTreatmentService extends AbstractService implements Serializable {

	private static final long serialVersionUID = -1907898489747356459L;
	@Autowired
	private NamedTreatmentDao namedTreatmentDao;
	@Autowired
	private StudyService studyService;
	@Autowired
	private ActionPatternsService actionPatternsService;
	
	@SuppressWarnings("unchecked")
	private static Map<Integer, NamedTreatmentDto> idToNamedTreatment = (Map<Integer, NamedTreatmentDto>) getCacheMap(
			NamedTreatmentDto.class);
	@SuppressWarnings("unchecked")
	private static Map<Integer, PpgTreatment> idToPpgTreatment = (Map<Integer, PpgTreatment>) getCacheMap(
			PpgTreatment.class);
	@SuppressWarnings("unchecked")
	private static Map<Integer, SpiFormulation> idToSpiFormulation = (Map<Integer, SpiFormulation>) getCacheMap(
			SpiFormulation.class);
	
	public NamedTreatment get(Integer id) {
		return namedTreatmentDao.get(id);
	}
	
	public List<NamedTreatment> getNamedTreatmentsByStudy(Integer studyId) {
		return namedTreatmentDao.getNamedTreatmentsByStudy(studyId);
	}
	
	public List<NamedTreatment> list() {
		return namedTreatmentDao.list();
	}
	
	public int getCount() {
		return namedTreatmentDao.getCount();
	}

	public Integer saveOrUpdate(NamedTreatment namedTreatment) {
		return namedTreatmentDao.saveOrUpdate(namedTreatment);
	}

	public int addNamedTreatment(NamedTreatment namedTreatment) {
		return namedTreatmentDao.addNamedTreatment(namedTreatment);
	}

	public NamedTreatmentDao getNamedTreatmentDao() {
		return namedTreatmentDao;
	}

	public void setNamedTreatmentDao(NamedTreatmentDao namedTreatmentDao) {
		this.namedTreatmentDao = namedTreatmentDao;
	}

	public NamedTreatmentDto getNamedTreatmentDto(Integer id) {
		return map(get(id));
	}

	public Set<NamedTreatmentDto> map(List<NamedTreatment> namedTreatments) {
		Set<NamedTreatmentDto> res = new HashSet<NamedTreatmentDto>();
		for (NamedTreatment namedTreatment : namedTreatments) {
			res.add(map(namedTreatment));
		}
		return res;
	}

	public NamedTreatmentDto map(NamedTreatment namedTreatment) {
		NamedTreatmentDto namedTreatmentDto = idToNamedTreatment.get(namedTreatment.getId());
		if (namedTreatmentDto == null) {
			namedTreatmentDto = dozerMapper.map(namedTreatment, NamedTreatmentDto.class, "namedTreatmentCustomMapping");
			if (idToNamedTreatment.get(namedTreatment.getId()) == null)
				idToNamedTreatment.put(namedTreatment.getId(), namedTreatmentDto);
			else
				namedTreatmentDto = idToNamedTreatment.get(namedTreatment.getId());
			namedTreatmentDto.setPpgTreatment(idToPpgTreatment.get(namedTreatmentDto.getPpgtreatmentInstanceId()));
			namedTreatmentDto.setSpiTreatment(idToSpiFormulation.get(namedTreatmentDto.getSpiTreatmentId()));

		}
		return namedTreatmentDto;
	}

	@Transactional
	public void save(NamedTreatmentDto namedTreatment) throws Exception {
		save(namedTreatment, false);
	}

	protected void save(NamedTreatmentDto namedTreatment, Boolean cross) throws Exception {
		try {
			if (!savedItems.contains(namedTreatment)) {
				savedItems.add(namedTreatment);
				if (namedTreatment.getStudy().getId() == Constants.NEWTRANSIENTID)
					studyService.save(namedTreatment.getStudy(), true);
				try {
					if (namedTreatment.getPpgTreatment() != null) {
						namedTreatment.setPpgTreatment(addOrUpdateTreatment(namedTreatment.getPpgTreatment()));
					}
					if (namedTreatment.getSpiTreatment() != null) {
						persistFormulation(namedTreatment, namedTreatment.getSpiTreatment());
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				namedTreatment.setUpdDate(new Date());
				namedTreatment.setUpdUser(UserUtil.getUsername());
				if(namedTreatment.getId().equals(Constants.NEWTRANSIENTID)) {
					namedTreatment.setCreDate(new Date());
					namedTreatment.setCreUser(UserUtil.getUsername());
				}
				namedTreatment.setId(saveOrUpdate(dozerMapper.map(namedTreatment, NamedTreatment.class, "namedTreatmentCustomMapping")));
				idToNamedTreatment.put(namedTreatment.getId(), namedTreatment);
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

	@SuppressWarnings("deprecation")
	public void persistFormulation(NamedTreatmentDto namedTreatment, SpiFormulation spiFormulation) throws Exception {
		int newId=0;
		if (spiFormulation!=null) {
			newId = DAOSpiFormulation.createFormulation(spiFormulation); // Stay with the deprecated for the moment as the new one isn't working
			if (newId > 0) {
				spiFormulation.setId(newId);
				namedTreatment.setSpiTreatmentId(spiFormulation.getId());
			} else {
				spiFormulation = null;
				namedTreatment.setSpiTreatmentId(null);
				throw new Exception("Could not save SpiFormulation");
			}
		}
	}
	
	@Transactional
	public void delete(NamedTreatmentDto namedTreatment) {
		delete(namedTreatment, false);
	}
	
	protected void delete(NamedTreatmentDto namedTreatment, Boolean cross) {
		for(ActionPatternsDto ap : actionPatternsService.map(actionPatternsService.getByAction(namedTreatment.getId(), namedTreatment.getType().name()))) {
			actionPatternsService.delete(ap, true);
		}
		namedTreatmentDao.delete(namedTreatment.getId());
	}

	public void setStudy(NamedTreatmentDto namedTreatment, StudyDto study) {
		setStudy(namedTreatment, study, false);
	}

	@SuppressWarnings("deprecation")
	public void setStudy(NamedTreatmentDto namedTreatment, StudyDto study, boolean cross) {
		// prevent endless loop
		if (nullSupportEqual(namedTreatment.getStudy(), study))
			return;
		// remove from the old owner
		if (namedTreatment.getStudy() != null && !(cross && study == null))
			studyService.removeNamedTreatment(namedTreatment.getStudy(), namedTreatment, true);
		// set new owner
		namedTreatment.setStudy(study);
		// set myself to new owner
		if (study != null && !cross)
			studyService.addNamedTreatment(study, namedTreatment, true);
	}

	public List<PpgTreatment> getTreatments(PpgTreatment ppgTreatment, boolean full) throws Exception {
		boolean enoughCriteria = true;
		List<String> criterias = new ArrayList<>();
		if (!full) {
			if (ppgTreatment.isVehicleAvailable() && ppgTreatment.getVehicle() == null) {
				enoughCriteria = false;
			}
			if (!ppgTreatment.isVehicle()) {
//	                enoughCriteria = false;
				for (PpgFormulationCompound compound : ppgTreatment.getCompounds()) {
					if (compound.getActNumber() != null) {
						criterias.add("act_no=" + compound.getActNumber());
						enoughCriteria = true;
						break;
					}
					if (ppgTreatment.isDoseAvailable(true)) {
					}
					if (ppgTreatment.isVolumeAvailable(true)) {
					}
					if (ppgTreatment.isConcentrationAvailable(true)) {
					}

				}
			}
		}
		if (full || enoughCriteria) {
			List<PpgTreatment> res = new ArrayList<>();
			try {
				List<PpgTreatment> treatments = DAOPpgTreatment.getTreatments(ppgTreatment);
				HashSet<PpgTreatment> treatmentsStripped = new HashSet<>();
				for (PpgTreatment treatment : treatments) {
					treatment.setTreatmentInstanceId(-1);
					if (!treatment.isVehicle()) {
						for (PpgFormulationCompound compound : treatment.getCompounds()) {
							compound.setContextId(-1);
						}
					}
					treatmentsStripped.add(treatment);
				}
				res.addAll(new ArrayList<>(treatmentsStripped));
			} catch (Exception e) {
				e.printStackTrace();
			}
			return res;
		}
		return null;
	}

	public Set<Integer> getTreatmentInstanceIds(PpgTreatment ppgTreatment) throws Exception {
		Set<Integer> treatmentInstanceIds = new HashSet<>();
		boolean enoughCriteria = true;
		List<String> criterias = new ArrayList<>();

		for (PpgFormulationCompound compound : ppgTreatment.getCompounds()) {
			if (compound.getActNumber() != null) {
				criterias.add("act_no=" + compound.getActNumber());
				enoughCriteria = true;
				break;
			}
		}

		if (enoughCriteria) {
			try {
				List<PpgTreatment> treatments = DAOPpgTreatment.getTreatments(ppgTreatment);
				for (PpgTreatment treatment : treatments) {
					treatmentInstanceIds.add(treatment.getTreatmentInstanceId());
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		return treatmentInstanceIds;
	}

	public PpgTreatment getTreatment(int id) {
		PpgTreatment ppgTreatment = null;
		try {
			ppgTreatment = DAOPpgTreatment.getTreatment(id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ppgTreatment;
	}
	
	public SpiFormulation getSpiFormulation(int id) {
		SpiFormulation spiFormulation = null;
		try {
			spiFormulation = DAOSpiFormulation.getFormulation(id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return spiFormulation;
	}

	public Quantity<?> getCalculatedAmount(int treatmentInstanceId, Double weight) {
		IdorsiaUnits idorsiaUnits = IdorsiaUnits.getInstance();
		ComparableQuantity<?> quantity = weight == null ? null
				: Quantities.getQuantity(weight, (Unit<?>) idorsiaUnits.getUnit(UnitSymbols.G));
		Quantity<?> result = null;
		try {
			result = DAOPpgTreatment.getPosologyForTreatment(quantity, treatmentInstanceId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public Quantity<?> getCalculatedAmount(NamedTreatmentDto nt, Double weight) {
		if (nt.getPpgTreatment() == null || nt.getPpgTreatment().getTreatmentId() == 0) {
			if(nt.getSpiTreatment()==null || weight == null)
				return null;
			return DAOSpiFormulation.getPosologyForSpiFormulation(nt.getSpiTreatmentId(), Quantities.getQuantity(weight, (Unit<?>) DBUnit.getUnitBySymbol(UnitSymbols.G)));
		}
		return getCalculatedAmount(nt.getPpgTreatment().getTreatmentInstanceId(), weight);
	}

	public PpgTreatment addOrUpdateTreatment(PpgTreatment treatment) throws Exception {
		Integer i = Constants.NEWTRANSIENTID;
		// Spirit should only save a prototype for now, DO NOT REMOVE
		treatment.setPrototype(true);
		try {
			ResponseEntity<Integer> respone = DAOPpgTreatment.addOrUpdateTreatment(treatment, UserUtil.getUsername(),
					"spirit-core");
			i = respone.getBody();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (i.intValue() <= Constants.NEWTRANSIENTID)
			throw new Exception("The treatment " + treatment + " couldn't be saved");
		treatment.setTreatmentInstanceId(i);
		return treatment;
	}

	public SpiFormulation addOrUpdateSpiTreatment(SpiFormulation spiFormulation) throws Exception {
		Integer i = Constants.NEWTRANSIENTID;
		try {
			i = DAOSpiFormulation.addOrUpdateFormulation(spiFormulation);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (i.intValue() <= Constants.NEWTRANSIENTID)
			throw new Exception("The formulation " + spiFormulation + " couldn't be saved");
		spiFormulation.setId(i);
		return spiFormulation;
	}	
	
	public void storeTreatments(List<PpgTreatment> treatments) {
		for (PpgTreatment t : treatments) {
			idToPpgTreatment.put(t.getTreatmentInstanceId(), t);
		}
	}

	public void storeFormulations(List<SpiFormulation> formulations) {
		for (SpiFormulation t : formulations) {
			idToSpiFormulation.put(t.getId(), t);
		}
	}

	public String getSpiSubstanceDescription(SpiFormuSubstance p) {
		return p.getActive_cmpd().getName() + ", " + p.getDose_amount() + " " + p.getDose_unit();
	}

	public String getSpiCompoundDescription(SPIFormuCompound p) {
		return p.getActive_cmpd().getName() + ", " + p.getDose_amount() + " " + p.getDose_unit();
	}

	public String getPpgCompoundDescription(PpgFormulationCompound p, PpgTreatment treatment, StudyDto study) {
		String cmpdDescription = p.getActNumber() + ",";
		if (treatment.isDoseAvailable(true)) {
			cmpdDescription += " " + p.getDose() + " " + p.getDoseUnit();
		}
		if (treatment.isConcentrationAvailable(true)) {
			cmpdDescription += " " + p.getConcentration() + " " + p.getConcentrationUnit();
		}
		return cmpdDescription;
	}

	public NamedTreatmentDto newNamedTreatmentDto(StudyDto study, boolean isDisease) {
		try {
			NamedTreatmentDto namedTreatmentDto = new NamedTreatmentDto();
			setStudy(namedTreatmentDto, study);
			namedTreatmentDto.setDisease(isDisease);
			return namedTreatmentDto;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public NamedTreatmentDto clone(NamedTreatmentDto namedTreatment) {
		return clone(namedTreatment, namedTreatment.getStudy());
	}

	public NamedTreatmentDto clone(NamedTreatmentDto namedTreatment, StudyDto study) {
		NamedTreatmentDto res = newNamedTreatmentDto(study, namedTreatment.getDisease());
		res.setName(namedTreatment.getName() + (namedTreatment.getStudy().equals(study) ? " Copy" : ""));
		res.setColor(namedTreatment.getColor());
		if (namedTreatment.getPpgTreatment() != null) {
			res.setPpgTreatment(clonePpgTreatment(namedTreatment.getPpgTreatment()));
		}
		if (namedTreatment.getSpiTreatmentId() != null && namedTreatment.getSpiTreatmentId() > 0) {
			res.setSpiTreatment(cloneSpiFormulation(namedTreatment.getSpiTreatment()));
			res.setSpiTreatmentId(res.getSpiTreatment().getId());
		}
		// Treatment is obsolete will not be copied
		res.setSurgery(namedTreatment.getSurgery());
		res.setDuration(namedTreatment.getDuration());
		return res;
	}

	private SpiFormulation cloneSpiFormulation(SpiFormulation formulation) {
		SpiFormulation clone = new SpiFormulation(formulation);
		return clone;
	}

	private PpgTreatment clonePpgTreatment(PpgTreatment ppgTreatment) {
		PpgTreatment clone = ppgTreatment.clone();
		if (!ppgTreatment.isVehicle()) {
			ArrayList<PpgFormulationCompound> cmpds = new ArrayList<PpgFormulationCompound>();
			for (PpgFormulationCompound cmpd : ppgTreatment.getCompounds()) {
				PpgFormulationCompound c = clonePpgFormulationCompound(cmpd);
				cmpds.add(c);
			}
			clone.setCompounds(cmpds);
		}
		clone.setTreatmentInstanceId(Constants.NEWTRANSIENTID);
		return clone;
	}

	private PpgFormulationCompound clonePpgFormulationCompound(PpgFormulationCompound original) {
		PpgFormulationCompound clone = new PpgFormulationCompound();
		clone.setCmpdId(0);
		clone.setContextId(clone.getContextId());
		clone.setVolumeUnitOld(original.getVolumeUnitOld());
		clone.setVolumeOld(original.getVolumeOld());
		clone.setRadioactive(original.isRadioactive());
		clone.setRadioactivity(original.getRadioactivity());
		clone.setMolWeight(original.getMolWeight());
		clone.setDose(original.getDose());
		clone.setDoseUnit(original.getDoseUnit());
		clone.setIsNarcotic(original.isNarcotic());
		clone.setAliquotId(original.getAliquotId());
		clone.setSaltFactor(original.getSaltFactor());
		clone.setCorrectionFactor(original.getCorrectionFactor());
		clone.setConcentrationUnit(original.getConcentrationUnit());
		clone.setConcentration(original.getConcentration());
		clone.setHotBatch(original.getHotBatch());
		clone.setBatch(original.getBatch());
		clone.setHotActNumber(original.getHotActNumber());
		clone.setActNumber(original.getActNumber());
		clone.setCmpdNames(original.getCmpdNames());
		return clone;
	}

	public boolean isWeightDependant(NamedTreatmentDto namedTreatment) {
		if (namedTreatment.getPpgTreatment() != null) {
			if (namedTreatment.getPpgTreatment().getVolumeUnit() != null
					&& namedTreatment.getPpgTreatment().getVolumeUnit().getSymbol().endsWith("/kg"))
				return true;
		}
		return false;
	}

}
