package com.idorsia.research.spirit.core.service;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.actelion.research.util.FormatterUtils;
import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.dao.FoodWaterDao;
import com.idorsia.research.spirit.core.dto.BiosampleDto;
import com.idorsia.research.spirit.core.dto.EnclosureDto;
import com.idorsia.research.spirit.core.dto.FoodWaterDto;
import com.idorsia.research.spirit.core.dto.PhaseDto;
import com.idorsia.research.spirit.core.dto.StudyDto;
import com.idorsia.research.spirit.core.dto.view.Consumption;
import com.idorsia.research.spirit.core.model.Enclosure;
import com.idorsia.research.spirit.core.model.FoodWater;
import com.idorsia.research.spirit.core.util.MiscUtils;
import com.idorsia.research.spirit.core.util.UserUtil;

@Service
public class FoodWaterService extends AbstractService implements Serializable {

	private static final long serialVersionUID = 735158294531744962L;
	@Autowired
	private FoodWaterDao foodWaterDao;
	@Autowired
	private EnclosureService enclosureService;
	@Autowired
	private BiosampleService biosampleService;
	
	private static Map<Integer, FoodWaterDto> idToFoodWater = new HashMap<>();

	public FoodWater get(Integer id) {
		return foodWaterDao.get(id);
	}

	public List<FoodWater> list() {
		return foodWaterDao.list();
	}

	public int getCount() {
		return foodWaterDao.getCount();
	}

	public Integer saveOrUpdate(FoodWater foodWater) {
		return foodWaterDao.saveOrUpdate(foodWater);
	}

	public int addFoodWater(FoodWater foodWater) {
		return foodWaterDao.addFoodWater(foodWater);
	}

	public Map<Integer, List<FoodWater>> getByEnclosures(List<Enclosure> enclosures) {
		List<Integer> enclosureIds = new ArrayList<Integer>();
		for(Enclosure enclosure : enclosures) {
			enclosureIds.add(enclosure.getId());
		}
		return foodWaterDao.getByEnclosures(enclosureIds);
	}

	public List<FoodWater> getByEnclosure(Integer enclosureId) {
		return foodWaterDao.getByEnclosure(enclosureId);
	}
	

	public FoodWater getByEnclosureAndDate(Integer enclosureId, String timepointDate) {
		return foodWaterDao.getByEnclosureAndDate(enclosureId, timepointDate);
	}

	public List<FoodWater> getByStudy(Integer studyId) {
		return foodWaterDao.getByStudy(studyId);
	}

	public List<FoodWater> getByStudyAndDate(Integer studyId, String timepointDate) {
		return foodWaterDao.getByStudyAndDate(studyId, timepointDate);
	}

	public FoodWaterDao getFoodWaterDao() {
		return foodWaterDao;
	}

	public void setFoodWaterDao(FoodWaterDao foodWaterDao) {
		this.foodWaterDao = foodWaterDao;
	}

	@Transactional
	public void save(FoodWaterDto foodWater) throws Exception {
		save(foodWater, false);
	}

	@Transactional
	public void save(Set<FoodWaterDto> fws) {
		for(FoodWaterDto fw : fws) {
			try {
				save(fw);
			} catch(Exception e) {
				e.printStackTrace();
				AbstractService.clearTransient(false);
			}
		}
		AbstractService.clearSavedItem();
		AbstractService.clearTransient(true);
	}
	
	protected void save(FoodWaterDto foodWater, Boolean cross) throws Exception {
		try {
			if(!savedItems.contains(foodWater)) {
				savedItems.add(foodWater);
				if(foodWater.getEnclosure().getId()==Constants.NEWTRANSIENTID)enclosureService.save(foodWater.getEnclosure(), true);
				foodWater.setUpdDate(new Date());
				foodWater.setUpdUser(UserUtil.getUsername());
				if(foodWater.getId().equals(Constants.NEWTRANSIENTID)) {
					foodWater.setCreDate(new Date());
					foodWater.setCreUser(UserUtil.getUsername());
				}
				foodWater.setId(saveOrUpdate(dozerMapper.map(foodWater, FoodWater.class, "foodWaterCustomMapping")));
				idToFoodWater.put(foodWater.getId(), foodWater);
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
	public void delete(FoodWaterDto foodWater) {
		delete(foodWater, false);
	}
	
	protected void delete(FoodWaterDto foodWater, Boolean cross) {
		foodWaterDao.delete(foodWater.getId());
	}

	public FoodWaterDto map(FoodWater foodWater) {
		FoodWaterDto foodWaterDto = idToFoodWater.get(foodWater.getId());
		if(foodWaterDto==null) {
			foodWaterDto=dozerMapper.map(foodWater, FoodWaterDto.class,"foodWaterCustomMapping");
			if(idToFoodWater.get(foodWater.getId())==null) { 
				idToFoodWater.put(foodWater.getId(), foodWaterDto);
			}
			else
				foodWaterDto=idToFoodWater.get(foodWater.getId());
		}
		return foodWaterDto;
	}
	
	public List<FoodWaterDto> map(List<FoodWater> foodWaters) {
		List<FoodWaterDto> results = new ArrayList<>();
		for(FoodWater foodWater : foodWaters) {
			results.add(map(foodWater));
		}
		return results;
	}

	public Integer getNAnimals(FoodWaterDto fw, Date date) {
		return enclosureService.getNanimalsForPeriod(fw.getEnclosure(), date, fw.getFwDate());
	}
	
	
	public FoodWaterDto getPreviousFromList(FoodWaterDto foodWater, List<FoodWaterDto> fws, boolean water) {
		FoodWaterDto sel = null;
		if(fws!=null) {
			for (FoodWaterDto fw : fws) {
				if(!fw.getEnclosure().equals(foodWater.getEnclosure())) continue;
				if(fw.getFwDate().compareTo(foodWater.getFwDate())>=0) continue;
				
				if(!water && (fw.getFoodTare()==null)) continue;
				if(water && (fw.getWaterTare()==null)) continue;

				if(sel==null || sel.getFwDate().compareTo(fw.getFwDate()) < 0) {
					sel = fw;
				}
			}
		}
		return sel;
	}
	
	public FoodWaterDto getNextFromList(FoodWaterDto foodWater, List<FoodWaterDto> fws, boolean water) {
		FoodWaterDto sel = null;
		if(fws!=null) {
			for (FoodWaterDto fw : fws) {
				if(!fw.getEnclosure().equals(foodWater.getEnclosure())) continue;
				if(fw.getFwDate().compareTo(foodWater.getFwDate())<=0) continue;
				
				if(!water && (fw.getFoodWeight()==null)) continue;
				if(water && (fw.getWaterWeight()==null)) continue;
				
				if(sel==null || sel.getFwDate().compareTo(fw.getFwDate()) > 0) {
					sel = fw;
				}
			}
		}
		return sel;
	}
	
	public Consumption calculatePrevConsumptionFromList(FoodWaterDto actual, List<FoodWaterDto> fws, boolean water) {
		FoodWaterDto prev = getPreviousFromList(actual, fws, water);
		if(prev==null) return null;
		return calculateConsumption(prev, actual, water);
	}

	public Consumption calculateNextConsumptionFromList(FoodWaterDto actual, List<FoodWaterDto> fws, boolean water) {
		FoodWaterDto next = getNextFromList(actual, fws, water);
		if(next==null) return null;
		FoodWaterDto prev = getPreviousFromList(next, fws, water); //previous==actual except if there is no measurement
		return calculateConsumption(prev, next, water);
	}


	/**
	 * Util function to calculate the food/water consumption from the previous measurement
	 * The no of rats used is the number of rats from the current FoodWater's location
	 * the time is calculated from the phases
	 */
	public Consumption calculateConsumption(FoodWaterDto prev, FoodWaterDto fw, boolean water) {
		if(prev==null || fw==null || !fw.getEnclosure().equals(prev.getEnclosure())) {
			return null;
		} else {
			Consumption res = new Consumption(water);
			res.fromDate = prev.getFwDate();
			res.toDate = fw.getFwDate();
			BigDecimal occupancy = enclosureService.getOccupancyForPeriod(fw.getEnclosure(), prev.getFwDate(), fw.getFwDate());

			if(occupancy==null) return res;
			
			if(water) {
				if(prev.getWaterTare()!=null && fw.getWaterWeight()!=null) {				
					if(occupancy.doubleValue()>0 ) {
						res.value = BigDecimal.valueOf(prev.getWaterTare()).subtract(BigDecimal.valueOf(fw.getWaterWeight())).divide(occupancy, 2,RoundingMode.HALF_UP);
					}
					res.formula = "("+FormatterUtils.formatMax2(prev.getWaterTare()) + " - "+ FormatterUtils.formatMax2(fw.getWaterWeight())+") / " + FormatterUtils.formatMax2(occupancy.doubleValue())+" animal days)";
				}
			} else {
				if(prev.getFoodTare()!=null && fw.getFoodWeight()!=null) {
					if(occupancy.doubleValue()>0) {
						res.value = BigDecimal.valueOf(prev.getFoodTare()).subtract(BigDecimal.valueOf(fw.getFoodWeight())).divide(occupancy, 2,RoundingMode.HALF_UP);
					}
					res.formula = "("+FormatterUtils.formatMax2(prev.getFoodTare()) + " - "+ FormatterUtils.formatMax2(fw.getFoodWeight())+") / " + FormatterUtils.formatMax2(occupancy.doubleValue())+" animal days)";
				}
			}

			return res;
		}
	}

	public Map<PhaseDto, Map<Date, Set<BiosampleDto>>> getPhases(List<FoodWaterDto> fws) {
		Map<PhaseDto, Map<Date, Set<BiosampleDto>>> res = new HashMap<>();
		for (FoodWaterDto fw : fws) {
			EnclosureDto enclosure = fw.getEnclosure();
			for (BiosampleDto biosample : enclosureService.getBiosamples(enclosure, fw.getFwDate())) {
				PhaseDto phase = biosampleService.getPhase(biosample, enclosure.getStudy(), fw.getFwDate(), null);
				Map<Date, Set<BiosampleDto>> dateSetMap = res.get(phase);
				if (dateSetMap == null) {
					dateSetMap = new HashMap<>();
				}
				MiscUtils.put(dateSetMap, fw.getFwDate(), biosample);
				res.put(phase, dateSetMap);
			}
		}
		return res;
	}

	public Map<Date, Set<BiosampleDto>> getDatesMap(List<FoodWaterDto> fws) {
		Map<Date, Set<BiosampleDto>> res = new HashMap<>();
		for (FoodWaterDto fw : fws) {
			Date date = fw.getFwDate();
			EnclosureDto enclosure = fw.getEnclosure();
			for (BiosampleDto biosample : enclosureService.getBiosamples(enclosure, date)) {
				Set<BiosampleDto> set = res.get(date);
				if (set == null) {
					set = new HashSet<>();
					res.put(date, set);
				}
				set.add(biosample);
			}
		}
		return res;
	}

	public FoodWaterDto extract(List<FoodWaterDto> fws, Integer enclosureId, Date date) {
		assert date != null;
		for (FoodWaterDto fw : fws) {
			if (date.equals(fw.getFwDate()) && enclosureId.equals(fw.getEnclosure().getId()))
				return fw;
		}
		return null;
	}
	
	public static List<FoodWaterDto> extract(Collection<FoodWaterDto> fws, Date date) {
		assert date!=null;
		List<FoodWaterDto> res = new ArrayList<>();
		for (FoodWaterDto fw : fws) {
			if(date.equals(fw.getFwDate())) res.add(fw);
		}
		return res;
	}

	public List<FoodWaterDto> getFoodWater(StudyDto study, Date date) {
		if(study==null)
			throw new IllegalArgumentException("You must give a study");
		List<FoodWater> fws = foodWaterDao.getFoodWater(study, date);
		return map(fws);
	}
}
