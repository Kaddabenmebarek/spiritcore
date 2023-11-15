package com.idorsia.research.spirit.core.service;

import static com.idorsia.research.spirit.core.util.MiscUtils.nullSupportEqual;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.constants.ContainmentType;
import com.idorsia.research.spirit.core.dao.BiosampleEnclosureDao;
import com.idorsia.research.spirit.core.dto.AssayDto;
import com.idorsia.research.spirit.core.dto.AssayResultDto;
import com.idorsia.research.spirit.core.dto.AssignmentDto;
import com.idorsia.research.spirit.core.dto.BiosampleDto;
import com.idorsia.research.spirit.core.dto.BiosampleEnclosureDto;
import com.idorsia.research.spirit.core.dto.EnclosureDto;
import com.idorsia.research.spirit.core.dto.FoodWaterDto;
import com.idorsia.research.spirit.core.dto.PhaseDto;
import com.idorsia.research.spirit.core.dto.StudyDto;
import com.idorsia.research.spirit.core.dto.view.BiosampleEnclosureOut;
import com.idorsia.research.spirit.core.dto.view.Consumption;
import com.idorsia.research.spirit.core.dto.view.Participant;
import com.idorsia.research.spirit.core.model.BiosampleEnclosure;
import com.idorsia.research.spirit.core.util.UserUtil;

@Service
public class BiosampleEnclosureService extends AbstractService implements Serializable {

	private static final long serialVersionUID = 37216022856158743L;
	@Autowired
	private BiosampleEnclosureDao biosampleEnclosureDao;
	@Autowired
	private BiosampleService biosampleService;
	@Autowired
	private EnclosureService enclosureService;
	@Autowired
	private PhaseService phaseService;
	@Autowired
	private StudyService studyService;
	@Autowired
	private StageService stageService;
	@Autowired
	private FoodWaterService foodWaterService;
	@Autowired
	private AssayResultService assayResultService;
	@Autowired
	private AssayService assayService;
	
	@SuppressWarnings("unchecked")
	private Map<Integer, BiosampleEnclosureDto> idToBiosampleEnclosure = (Map<Integer, BiosampleEnclosureDto>) getCacheMap(
			BiosampleEnclosureDto.class);

	public BiosampleEnclosure get(Integer id) {
		return biosampleEnclosureDao.get(id);
	}

	public List<BiosampleEnclosure> getByBiosample(Integer biosampleId) {
		if(biosampleId.equals(Constants.NEWTRANSIENTID))
			return new ArrayList<>();
		return biosampleEnclosureDao.getByBiosample(biosampleId);
	}

	public List<BiosampleEnclosure> list() {
		return biosampleEnclosureDao.list();
	}

	public List<BiosampleEnclosure> getByEnclosure(Integer enclosureId) {
		if(enclosureId.equals(Constants.NEWTRANSIENTID))
			return new ArrayList<>();
		return biosampleEnclosureDao.getByEnclosure(enclosureId);
	}

	public List<BiosampleEnclosure> getByPhaseIn(Integer phaseInId) {
		if(phaseInId.equals(Constants.NEWTRANSIENTID))
			return new ArrayList<>();
		return biosampleEnclosureDao.getByPhaseIn(phaseInId);
	}

	public int getCount() {
		return biosampleEnclosureDao.getCount();
	}

	public Integer saveOrUpdate(BiosampleEnclosure biosampleEnclosure) {
		return biosampleEnclosureDao.saveOrUpdate(biosampleEnclosure);
	}

	public int addBiosampleEnclosure(BiosampleEnclosure biosampleEnclosure) {
		return biosampleEnclosureDao.addBiosampleEnclosure(biosampleEnclosure);
	}

	public BiosampleEnclosureDao getBiosampleEnclosureDao() {
		return biosampleEnclosureDao;
	}

	public void setBiosampleEnclosureDao(BiosampleEnclosureDao biosampleEnclosureDao) {
		this.biosampleEnclosureDao = biosampleEnclosureDao;
	}

	public List<BiosampleEnclosureDto> map(List<BiosampleEnclosure> biosampleEnclosures) {
		List<BiosampleEnclosureDto> res = new ArrayList<BiosampleEnclosureDto>();
		for(BiosampleEnclosure biosampleEnclosure : biosampleEnclosures) {
			res.add(map(biosampleEnclosure));
		}
		return res;
	}
	
	public BiosampleEnclosureDto map(BiosampleEnclosure biosampleEnclosure) {
		BiosampleEnclosureDto biosampleEnclosureDto = idToBiosampleEnclosure.get(biosampleEnclosure.getId());
		if (biosampleEnclosureDto == null) {
			biosampleEnclosureDto = dozerMapper.map(biosampleEnclosure, BiosampleEnclosureDto.class,"biosampleEnclosureCustomMapping");
			if (idToBiosampleEnclosure.get(biosampleEnclosure.getId()) == null) {
				idToBiosampleEnclosure.put(biosampleEnclosure.getId(), biosampleEnclosureDto);
			} else
				biosampleEnclosureDto = idToBiosampleEnclosure.get(biosampleEnclosure.getId());
		}
		return biosampleEnclosureDto;
	}

	@Transactional
	public void save(BiosampleEnclosureDto biosampleEnclosure) throws Exception {
		save(biosampleEnclosure, false);
	}
	
	protected void save(BiosampleEnclosureDto biosampleEnclosure, Boolean cross) throws Exception {
		try {
			if (!savedItems.contains(biosampleEnclosure)) {
				savedItems.add(biosampleEnclosure);
				if(biosampleEnclosure.getBiosample().getId()==Constants.NEWTRANSIENTID)biosampleService.save(biosampleEnclosure.getBiosample(), true);
				if(biosampleEnclosure.getEnclosure().getId()==Constants.NEWTRANSIENTID)enclosureService.save(biosampleEnclosure.getEnclosure(), true);
				if(biosampleEnclosure.getPhaseIn().getId()==Constants.NEWTRANSIENTID)phaseService.save(biosampleEnclosure.getPhaseIn(), true);
				if(biosampleEnclosure.getPhaseOut()!=null && biosampleEnclosure.getPhaseOut().getId()==Constants.NEWTRANSIENTID)phaseService.save(biosampleEnclosure.getPhaseOut(), true);
				if(biosampleEnclosure.getStudy().getId()==Constants.NEWTRANSIENTID)studyService.save(biosampleEnclosure.getStudy(), true);
				biosampleEnclosure.setUpdDate(new Date());
				biosampleEnclosure.setUpdUser(UserUtil.getUsername());
				if(biosampleEnclosure.getId().equals(Constants.NEWTRANSIENTID)) {
					biosampleEnclosure.setCreDate(new Date());
					biosampleEnclosure.setCreUser(UserUtil.getUsername());
				}
				biosampleEnclosure.setId(saveOrUpdate(
						dozerMapper.map(biosampleEnclosure, BiosampleEnclosure.class, "biosampleEnclosureCustomMapping")));
				idToBiosampleEnclosure.put(biosampleEnclosure.getId(), biosampleEnclosure);
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
	public void delete(BiosampleEnclosureDto biosampleEnclosure) {
		delete(biosampleEnclosure, false);
	}
	
	protected void delete(BiosampleEnclosureDto biosampleEnclosure, Boolean cross) {
		biosampleEnclosureDao.delete(biosampleEnclosure.getId());
	}

	public void setEnclosure(BiosampleEnclosureDto biosampleEnclosure, EnclosureDto enclosure) {
		setEnclosure(biosampleEnclosure, enclosure, false);
	}

	@SuppressWarnings("deprecation")
	public void setEnclosure(BiosampleEnclosureDto biosampleEnclosure, EnclosureDto enclosure, boolean cross) {
		// prevent endless loop
		if (nullSupportEqual(biosampleEnclosure.getEnclosure(), enclosure))
			return;
		// remove from the old owner
		if (biosampleEnclosure.getEnclosure() != null && !(cross && enclosure == null))
			enclosureService.removeBiosampleEnclosure(biosampleEnclosure.getEnclosure(), biosampleEnclosure, true);
		// set myself to new owner
		if (enclosure != null && !cross)
			enclosureService.addBiosampleEnclosure(enclosure, biosampleEnclosure, true);
		//recalculate FW
		setPhaseIn(biosampleEnclosure, biosampleEnclosure.getPhaseIn());
		setPhaseOut(biosampleEnclosure, biosampleEnclosure.getPhaseOut());
		// set new owner
		biosampleEnclosure.setEnclosure(enclosure);
		biosampleEnclosure.setStudy(enclosure==null?null:enclosure.getStudy());
	}

	public void setBiosample(BiosampleEnclosureDto biosampleEnclosure, BiosampleDto biosample) {
		setBiosample(biosampleEnclosure, biosample, false);
	}

	@SuppressWarnings("deprecation")
	public void setBiosample(BiosampleEnclosureDto biosampleEnclosure, BiosampleDto biosample, boolean cross) {
		// prevent endless loop
		if (nullSupportEqual(biosampleEnclosure.getBiosample(), biosample))
			return;
		// remove from the old owner
		if (biosampleEnclosure.getBiosample() != null && !(cross && biosample == null))
			biosampleService.removeBiosampleEnclosure(biosampleEnclosure.getBiosample(), biosampleEnclosure, true);
		// set new owner
		biosampleEnclosure.setBiosample(biosample);
		// set myself to new owner
		if (biosample != null && !cross)
			biosampleService.addBiosampleEnclosure(biosample, biosampleEnclosure, true);
	}

	@SuppressWarnings("deprecation") 
	public void setPhaseIn(BiosampleEnclosureDto biosampleEnclosure, PhaseDto phaseIn) {
		PhaseDto remove = biosampleEnclosure.getPhaseIn();
		if (nullSupportEqual(remove, phaseIn))
			return;
		EnclosureDto enclosure = biosampleEnclosure.getEnclosure();
		BiosampleDto biosample = biosampleEnclosure.getBiosample();
		biosampleEnclosure.setPhaseIn(phaseIn);
		if(enclosure!=null && biosample!=null) {
			StudyDto study = enclosure.getStudy();
			AssayDto assay = assayService.map(assayService.getAssayByName(Constants.FOODWATER_TESTNAME));
			List<FoodWaterDto> fws = enclosure.getFoodWaters();
			List<AssayResultDto> results = new ArrayList<>();
			List<AssayResultDto> resultsToDelete = new ArrayList<>();
			for(FoodWaterDto fw : fws) {
				List<BiosampleDto> biosamples = enclosureService.getBiosamples(enclosure, fw.getFwDate());
				
				if(!biosamples.contains(biosample)) {
					PhaseDto phase = biosampleService.getPhase(biosample, study, fw.getFwDate());
					AssayResultDto result = biosampleService.getFirstResultByPhaseAndAssayName(biosample, phase, Constants.FOODWATER_TESTNAME);
					if(result!=null)
						resultsToDelete.add(result);
				}
				
				//calculate new food
				Consumption newFoodCons = foodWaterService.calculatePrevConsumptionFromList(fw, fws, false);
				
				if(newFoodCons!=null) {
					for (BiosampleDto animal : biosamples) {
						PhaseDto phase = biosampleService.getPhase(animal, enclosure.getStudy(), newFoodCons.toDate);
						AssayResultDto r = biosampleService.getFirstResultByPhaseAndAssayName(animal, phase, Constants.FOODWATER_TESTNAME);
						if(r==null) {
							r = new AssayResultDto();
							assayResultService.setAssay(r, assay);
							assayResultService.setBiosample(r, animal);
							assayResultService.setPhase(r, phase);
							r.setStudy(study);
						}
						assayResultService.getOutputResultValues(r).get(0).setTextValue(newFoodCons.value==null? null: ""+newFoodCons.value);
						if(!results.contains(r)) 
							results.add(r);
					}
				}
				
				//calculate new water
				Consumption newWaterCons = foodWaterService.calculatePrevConsumptionFromList(fw, fws, true);
				if(newWaterCons!=null) {
					for (BiosampleDto animal : biosamples) {
						PhaseDto phase = biosampleService.getPhase(animal, enclosure.getStudy(), newWaterCons.toDate);
						AssayResultDto r = biosampleService.getFirstResultByPhaseAndAssayName(animal, phase, Constants.FOODWATER_TESTNAME);
						if(r==null) {
							r = new AssayResultDto();
							assayResultService.setAssay(r, assay);
							assayResultService.setBiosample(r, animal);
							assayResultService.setPhase(r, phase);
							r.setStudy(study);
						}
						assayResultService.getOutputResultValues(r).get(1).setTextValue(newWaterCons.value==null? null: ""+newWaterCons.value);
						if(!results.contains(r)) 
							results.add(r);
					}
				}
			}
			assayResultService.delete(resultsToDelete);
			assayResultService.save(results);
		}
		if (remove != null)
			phaseService.remove(remove);
	}

	@SuppressWarnings("deprecation")
	public void setPhaseOut(BiosampleEnclosureDto biosampleEnclosure, PhaseDto phaseOut) {
		if (biosampleEnclosure==null || (phaseOut != null && !biosampleEnclosure.getStudy().equals(phaseOut.getStage().getStudy())))
			return;
		PhaseDto remove = biosampleEnclosure.getPhaseOut();
		if (nullSupportEqual(remove, phaseOut))
			return;
		biosampleEnclosure.setPhaseOut(phaseOut);
		EnclosureDto enclosure = biosampleEnclosure.getEnclosure();
		BiosampleDto biosample = biosampleEnclosure.getBiosample();
		if(enclosure!=null && biosample!=null) {
			StudyDto study = enclosure.getStudy();
			AssayDto assay = assayService.map(assayService.getAssayByName(Constants.FOODWATER_TESTNAME));
			List<FoodWaterDto> fws = enclosure.getFoodWaters();
			List<AssayResultDto> results = new ArrayList<>();
			List<AssayResultDto> resultsToDelete = new ArrayList<>();
			for(FoodWaterDto fw : fws) {
				List<BiosampleDto> biosamples = enclosureService.getBiosamples(enclosure, fw.getFwDate());
				if(!biosamples.contains(biosample)) {
					PhaseDto phase = biosampleService.getPhase(biosample, study, fw.getFwDate());
					AssayResultDto result = biosampleService.getFirstResultByPhaseAndAssayName(biosample, phase, Constants.FOODWATER_TESTNAME);
					if(result!=null)
						resultsToDelete.add(result);
				}
				
				//calculate new food
				Consumption newFoodCons = foodWaterService.calculatePrevConsumptionFromList(fw, fws, false);
				
				if(newFoodCons!=null) {
					for (BiosampleDto animal : biosamples) {
						PhaseDto phase = biosampleService.getPhase(animal, enclosure.getStudy(), newFoodCons.toDate);
						AssayResultDto r = biosampleService.getFirstResultByPhaseAndAssayName(animal, phase, Constants.FOODWATER_TESTNAME);
						if(r==null) {
							r = new AssayResultDto();
							assayResultService.setAssay(r, assay);
							assayResultService.setBiosample(r, animal);
							assayResultService.setPhase(r, phase);
							r.setStudy(study);
						}
						assayResultService.getOutputResultValues(r).get(0).setTextValue(newFoodCons.value==null? null: ""+newFoodCons.value);
						if(!results.contains(r)) results.add(r);
					}
				}
				
				//calculate new water
				Consumption newWaterCons = foodWaterService.calculatePrevConsumptionFromList(fw, fws, true);
				if(newWaterCons!=null) {
					for (BiosampleDto animal : biosamples) {
						PhaseDto phase = biosampleService.getPhase(animal, enclosure.getStudy(), newWaterCons.toDate);
						AssayResultDto r = biosampleService.getFirstResultByPhaseAndAssayName(animal, phase, Constants.FOODWATER_TESTNAME);
						if(r==null) {
							r = new AssayResultDto();
							assayResultService.setAssay(r, assay);
							assayResultService.setBiosample(r, animal);
							assayResultService.setPhase(r,phase);
							r.setStudy(study);
						}
						assayResultService.getOutputResultValues(r).get(1).setTextValue(newWaterCons.value==null? null: ""+newWaterCons.value);
						if(!results.contains(r)) 
							results.add(r);
					}
				}
			}
			assayResultService.delete(resultsToDelete);
			assayResultService.save(results);
		}
		if (remove != null) {
			phaseService.remove(remove);
		}
	}

	public void remove(BiosampleEnclosureDto biosampleEnclosure) {
		setBiosample(biosampleEnclosure, null);
		setEnclosure(biosampleEnclosure, null);
		biosampleEnclosure.setStudy(null);
		setPhaseIn(biosampleEnclosure, null);
		setPhaseOut(biosampleEnclosure, null);
	}

	public boolean containsPhase(BiosampleEnclosureDto biosampleEnclosure, PhaseDto phase, ContainmentType containmentType) {
		if (phase == null) return false;
        PhaseDto pOut = biosampleEnclosure.getPhaseOut();
        if (pOut==null) {
            AssignmentDto last = biosampleService.getLastAssignment(biosampleEnclosure.getBiosample(), biosampleEnclosure.getStudy());
            if(last==null) {
            	System.out.println("be : "+biosampleEnclosure.getId());
            	return false;
            }
            pOut = last.getStage().getNextStage()!=null?stageService.getLastPhase(studyService.getLastStage(biosampleEnclosure.getStudy())):stageService.getLastPhase(last.getStage());
        }
        switch (containmentType) {
            case FW:
                return (phase.compareTo(biosampleEnclosure.getPhaseIn()) >= 0 && phase.compareTo(pOut) <= 0);
            case INSERT:
                return (phase.compareTo(biosampleEnclosure.getPhaseIn()) >= 0 && (phase.compareTo(pOut) < 0
                        // we have only one phase in the stage, and that is the last phase of the study...
                        // pOut should only be equal to this.getPhaseIn in that case!
                        || (biosampleEnclosure.getPhaseIn().equals(pOut) && phase.equals(pOut) && biosampleEnclosure.getPhaseOut()==null ) ));
            case CONSUMPTION:
                return (phase.compareTo(biosampleEnclosure.getPhaseIn()) > 0 && phase.compareTo(pOut) <= 0);
        }
        return false;
	}

	public boolean containsDate(BiosampleEnclosureDto biosampleEnclosure, Date date, ContainmentType containmentType) {
		return containsDate(biosampleEnclosure, date, containmentType, null);
	}
	
	public boolean containsDate(BiosampleEnclosureDto biosampleEnclosure, Date date, ContainmentType containmentType, Set<Participant>participants) {
    	BiosampleDto biosample = biosampleEnclosure.getBiosample();
    	StudyDto study = biosampleEnclosure.getStudy();
		if(biosampleService.isDeadAt(biosample, date) || biosampleService.isRemove(biosample, study, date))
            return false;
    	Date inTime = biosampleService.getDate(biosample, biosampleEnclosure.getPhaseIn(), biosampleEnclosure);
        if(participants==null) {
        	participants=studyService.getParticipants(study);
        }
        if (inTime == null) return false;
        Date outTime = biosampleService.getDate(biosample, biosampleEnclosure.getPhaseOut(), new BiosampleEnclosureDto());
        switch (containmentType) {
            case FW:
                return (date.compareTo(inTime) >= 0 && (date.compareTo(outTime == null ? studyService.getLastDate(study, participants) : outTime) <= 0));
            case INSERT:
            	if(outTime==null)
            		return date.compareTo(inTime) >= 0 && date.compareTo(studyService.getLastDate(study, participants)) <= 0;
                return date.compareTo(inTime) >= 0 && date.compareTo(outTime) < 0;
            case CONSUMPTION:
                return (date.compareTo(inTime) > 0 && (date.compareTo(outTime == null ? studyService.getLastDate(study, participants) : outTime) <= 0));
        }
        return false;
    }

	public Date getOutDate(BiosampleEnclosureDto biosampleEnclosure) {
		if(biosampleEnclosure!=null)
			return biosampleService.getDate(biosampleEnclosure.getBiosample(), biosampleEnclosure.getPhaseOut());
		return null;
	}
	
	public Date getInDate(BiosampleEnclosureDto biosampleEnclosure) {
		if(biosampleEnclosure!=null)
			return biosampleService.getDate(biosampleEnclosure.getBiosample(), biosampleEnclosure.getPhaseIn());
		return null;
	}

	public Date getDateIn(BiosampleEnclosureDto biosampleEnclosure) {
        return biosampleService.getDate(biosampleEnclosure.getBiosample(), biosampleEnclosure.getPhaseIn(), biosampleEnclosure);
    }

	public Date getDateOut(BiosampleEnclosureDto biosampleEnclosure) {
		 return biosampleService.getDate(biosampleEnclosure.getBiosample(), biosampleEnclosure.getPhaseOut(), new BiosampleEnclosureOut());
	}

	public BiosampleEnclosureDto newBiosampleEnclosure(BiosampleDto biosample, EnclosureDto enclosure,
			PhaseDto phaseIn) {
		BiosampleEnclosureDto biosampleEnclosure = new BiosampleEnclosureDto();
		setBiosample(biosampleEnclosure, biosample);
		setEnclosure(biosampleEnclosure, enclosure);
		setPhaseIn(biosampleEnclosure, phaseIn);
		return biosampleEnclosure;
	}

}
