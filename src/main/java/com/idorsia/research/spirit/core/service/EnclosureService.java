package com.idorsia.research.spirit.core.service;

import static com.idorsia.research.spirit.core.util.MiscUtils.nullSupportEqual;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.constants.ContainmentType;
import com.idorsia.research.spirit.core.dao.EnclosureDao;
import com.idorsia.research.spirit.core.dto.BiosampleDto;
import com.idorsia.research.spirit.core.dto.BiosampleEnclosureDto;
import com.idorsia.research.spirit.core.dto.EnclosureDto;
import com.idorsia.research.spirit.core.dto.FoodWaterDto;
import com.idorsia.research.spirit.core.dto.StudyDto;
import com.idorsia.research.spirit.core.dto.view.Participant;
import com.idorsia.research.spirit.core.model.BiosampleEnclosure;
import com.idorsia.research.spirit.core.model.Enclosure;
import com.idorsia.research.spirit.core.model.FoodWater;
import com.idorsia.research.spirit.core.util.UserUtil;

@Service
public class EnclosureService extends AbstractService implements Serializable {

	private static final long serialVersionUID = 7768081298292570816L;
	@Autowired
	private EnclosureDao enclosureDao;
	@Autowired
	private BiosampleEnclosureService biosampleEnclosureService;
	@Autowired
	private StudyService studyService;
	@Autowired
	private FoodWaterService foodWaterService;
	@Autowired
	private BiosampleService biosampleService;

	@SuppressWarnings("unchecked")
	private Map<Integer, EnclosureDto> idToEnclosure = (Map<Integer, EnclosureDto>) getCacheMap(EnclosureDto.class);

	public Enclosure get(Integer id) {
		return enclosureDao.get(id);
	}

	public List<Enclosure> list() {
		return enclosureDao.list();
	}

	public int getCount() {
		return enclosureDao.getCount();
	}

	public Integer saveOrUpdate(Enclosure enclosure) {
		return enclosureDao.saveOrUpdate(enclosure);
	}

	public int addEnclosure(Enclosure enclosure) {
		return enclosureDao.addEnclosure(enclosure);
	}

	public Set<Enclosure> getBySamples(Set<Integer> samplesIds, int studyId) {
		return enclosureDao.getBySamples(samplesIds, studyId);
	}

	public Set<Enclosure> getByStudy(Integer studyId) {
		return enclosureDao.getByStudy(studyId);
	}
	
	public List<EnclosureDto> getEnclosureDtosByStudy(Integer studyId) {
		return map(getByStudy(studyId));
	}

	public Map<String, List<Integer>> getCageBiosampleMap(List<Enclosure> cages) {
		List<Integer> cageIds = new ArrayList<Integer>();
		for (Enclosure cage : cages) {
			cageIds.add(cage.getId());
		}
		return enclosureDao.getCageBiosampleMap(cageIds);
	}

	public EnclosureDao getEnclosureDao() {
		return enclosureDao;
	}

	public void setEnclosureDao(EnclosureDao enclosureDao) {
		this.enclosureDao = enclosureDao;
	}

	public List<EnclosureDto> map(Set<Enclosure> enclosures) {
		List<EnclosureDto> res = new ArrayList<EnclosureDto>();
		for(Enclosure enclosure : enclosures) {
			res.add(map(enclosure));
		}
		return res;
	}
	
	public EnclosureDto map(Enclosure enclosure) {
		EnclosureDto enclosureDto = idToEnclosure.get(enclosure.getId());
		if (enclosureDto == null) {
			enclosureDto = dozerMapper.map(enclosure, EnclosureDto.class, "enclosureCustomMapping");
			if (idToEnclosure.get(enclosure.getId()) == null) {
				idToEnclosure.put(enclosure.getId(), enclosureDto);
			} else
				enclosureDto = idToEnclosure.get(enclosure.getId());
		}
		return enclosureDto;
	}

	@Transactional
	public void save(EnclosureDto enclosure) throws Exception {
		save(enclosure, false);
	}

	@SuppressWarnings("deprecation")
	protected void save(EnclosureDto enclosure, Boolean cross) throws Exception {
		try {
			if (!savedItems.contains(enclosure)) {
				savedItems.add(enclosure);
				if(enclosure.getId()!=Constants.NEWTRANSIENTID) {
					deleteChildren(enclosure);
				}
				if (enclosure.getStudy().getId() == Constants.NEWTRANSIENTID)
					studyService.save(enclosure.getStudy(), true);
				enclosure.setUpdDate(new Date());
				enclosure.setUpdUser(UserUtil.getUsername());
				if(enclosure.getId().equals(Constants.NEWTRANSIENTID)) {
					enclosure.setCreDate(new Date());
					enclosure.setCreUser(UserUtil.getUsername());
				}
				enclosure.setId(saveOrUpdate(dozerMapper.map(enclosure, Enclosure.class, "enclosureCustomMapping")));
				idToEnclosure.put(enclosure.getId(), enclosure);
				if(enclosure.getBiosampleEnclosuresNoMapping()!=null)
					for (BiosampleEnclosureDto biosampleEnclosure : enclosure.getBiosampleEnclosures()) {
						biosampleEnclosureService.save(biosampleEnclosure, true);
					}
				if(enclosure.getFoodWatersNoMapping()!=null)
					for (FoodWaterDto foodWater : enclosure.getFoodWaters()) {
						foodWaterService.save(foodWater, true);
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

	@SuppressWarnings("deprecation")
	private void deleteChildren(EnclosureDto enclosure) {
		if(enclosure.getBiosampleEnclosuresNoMapping()!=null) {
			for(BiosampleEnclosure be : biosampleEnclosureService.getByEnclosure(enclosure.getId())) {
				Boolean found = false;
				for(BiosampleEnclosureDto child : enclosure.getBiosampleEnclosures()){
					if(be.getId().equals(child.getId())) {
						found=true;
						break;
					}
				}
				if(!found) {
					biosampleEnclosureService.delete(biosampleEnclosureService.map(be), true);
				}
			}
		}
		if(enclosure.getFoodWatersNoMapping()!=null) {
			for(FoodWater fw : foodWaterService.getByEnclosure(enclosure.getId())) {
				Boolean found = false;
				for(BiosampleEnclosureDto child : enclosure.getBiosampleEnclosures()){
					if(fw.getId().equals(child.getId())) {
						found=true;
						break;
					}
				}
				if(!found) {
					foodWaterService.delete(foodWaterService.map(fw), true);
				}
			}
		}
	}

	@Transactional
	public void delete(EnclosureDto enclosure) {
		delete(enclosure, false);
	}
	
	protected void delete(EnclosureDto enclosure, Boolean cross) {
		enclosureDao.delete(enclosure.getId());
	}

	public EnclosureDto getEnclosureDto(Integer id) {
		return map(get(id));
	}

	public void addBiosampleEnclosure(EnclosureDto enclosure, BiosampleEnclosureDto biosampleEnclosureLink) {
		addBiosampleEnclosure(enclosure, biosampleEnclosureLink, false);
	}

	public void addBiosampleEnclosure(EnclosureDto enclosure, BiosampleEnclosureDto biosampleEnclosureLink,
			boolean cross) {
		// prevent endless loop
		if (enclosure.getBiosampleEnclosures().contains(biosampleEnclosureLink))
			return;
		// add new member
		enclosure.getBiosampleEnclosures().add(biosampleEnclosureLink);
		// update child if request is not from it
		if (!cross) {
			biosampleEnclosureService.setEnclosure(biosampleEnclosureLink, enclosure, true);
		}
	}

	public void removeBiosampleEnclosureLink(EnclosureDto enclosure, BiosampleEnclosureDto biosampleEnclosure) {
		removeBiosampleEnclosure(enclosure, biosampleEnclosure, false);
	}

	public void removeBiosampleEnclosure(EnclosureDto enclosure, BiosampleEnclosureDto biosampleEnclosure,
			boolean cross) {
		// prevent endless loop
		if (!enclosure.getBiosampleEnclosures().contains(biosampleEnclosure))
			return;
		// remove old member
		enclosure.getBiosampleEnclosures().remove(biosampleEnclosure);
		// remove child's owner
		if (!cross) {
			biosampleEnclosureService.setEnclosure(biosampleEnclosure, null, true);
		}
		if(enclosure.getBiosampleEnclosures().size()==0)
			studyService.removeEnclosure(enclosure.getStudy(), enclosure);
	}

	public void setStudy(EnclosureDto enclosure, StudyDto study) {
		setStudy(enclosure, study, false);
	}

	@SuppressWarnings("deprecation")
	public void setStudy(EnclosureDto enclosure, StudyDto study, boolean cross) {
		// prevent endless loop
		if (nullSupportEqual(enclosure.getStudy(), study))
			return;
		// remove from the old owner
		if (enclosure.getStudy() != null && !(cross && study == null))
			studyService.removeEnclosure(enclosure.getStudy(), enclosure, true);
		// set new owner
		enclosure.setStudy(study);
		// set myself to new owner
		if (study != null && !cross)
			studyService.addEnclosure(study, enclosure, true);
	}

	public void remove(EnclosureDto enclosure) {
		HashSet<BiosampleEnclosureDto> biosampleEnclosureLinkHashSet = new HashSet<>(
				enclosure.getBiosampleEnclosures());
		for (BiosampleEnclosureDto biosampleEnclosure : biosampleEnclosureLinkHashSet) {
			biosampleEnclosureService.remove(biosampleEnclosure);
		}
		setStudy(enclosure, null);
	}

	public List<BiosampleDto> getBiosamples(EnclosureDto enclosure, Date date) {
		return getBiosamples(enclosure, date, null);
	}

	public List<BiosampleDto> getBiosamples(EnclosureDto selectedEnclosure, Date date, Set<Participant> participants) {
		List<BiosampleDto> biosamples = new ArrayList<>();
		for (BiosampleEnclosureDto biosampleEnclosure : selectedEnclosure.getBiosampleEnclosures()) {
			if (biosampleEnclosureService.containsDate(biosampleEnclosure, date, ContainmentType.FW, participants))
				biosamples.add(biosampleEnclosure.getBiosample());
		}
		return biosamples;
	}

	public FoodWaterDto getFoodWater(EnclosureDto enclosure, Date date) {
		for (FoodWaterDto fw : enclosure.getFoodWaters()) {
			if (fw.getFwDate().equals(date))
				return fw;
		}
		return null;
	}

	public FoodWaterDto getFoodWater(EnclosureDto enclosure, Date date, boolean water) {
		List<FoodWaterDto> fws = enclosure.getFoodWaters();
		Collections.sort(fws);
		FoodWaterDto result = null;
		int i = 0;
		while (i < fws.size() && fws.get(i).getFwDate().compareTo(date) <= 0) {
			FoodWaterDto fw = fws.get(i);
			if (fw.getFwDate().equals(date)) {
				if (water) {
					if (fw.getWaterTare() != null)
						result = fw;
				} else {
					if (fw.getFoodTare() != null)
						result = fw;
				}
			}
			i = i + 1;
		}
		return result;
	}

	public List<FoodWaterDto> getFoodWaters(EnclosureDto enclosure, boolean water) {
		List<FoodWaterDto> values = new ArrayList<>();
		List<FoodWaterDto> fws = enclosure.getFoodWaters();
		Collections.sort(fws);
		for (FoodWaterDto fw : fws) {
			if (water) {
				if (fw.getWaterTare() != null)
					values.add(fw);
			} else {
				if (fw.getFoodTare() != null)
					values.add(fw);
			}
		}
		return values;
	}

	public Set<BiosampleDto> getOccupiedAnimals(EnclosureDto enclosure, Date date) {
		Set<BiosampleDto> biosamples = new HashSet<>();
		for (BiosampleEnclosureDto biosampleEnclosure : enclosure.getBiosampleEnclosures()) {
			if (biosampleEnclosureService.containsDate(biosampleEnclosure, date, ContainmentType.INSERT))
				biosamples.add(biosampleEnclosure.getBiosample());
		}
		return biosamples;
	}

	public int getNanimalsForPeriod(EnclosureDto enclosure, Date from, Date to) {
		int result = 0;
		StudyDto study = enclosure.getStudy();
		Set<Participant> participants = studyService.getParticipants(study);
		double diff = ChronoUnit.DAYS.between(from.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
				to.toInstant().atZone(ZoneId.systemDefault()));
		Date lastDate = studyService.getLastDate(study, participants);
		for (BiosampleEnclosureDto biosampleEnclosure : enclosure.getBiosampleEnclosures()) {
			Date dateOut = biosampleEnclosureService.getOutDate(biosampleEnclosure);
			if (dateOut == null) {
				BiosampleDto biosample = biosampleEnclosure.getBiosample();
				if (biosampleService.isDeadAt(biosample, to))
					dateOut = biosampleService.getTerminationExecutionDate(biosample);
				else if (biosampleService.isRemove(biosample, study, to))
					dateOut = biosampleService.getAssignment(biosample, study, to).getRemoveDate();
				else
					dateOut = lastDate;
			}
			Date inDate = biosampleEnclosureService.getInDate(biosampleEnclosure);
			if (inDate == null || inDate.compareTo(to) >= 0 || dateOut == null || dateOut.compareTo(from) <= 0)
				continue;
			BigDecimal res = BigDecimal
					.valueOf((dateOut.compareTo(to) > 0 ? to.getTime() : dateOut.getTime())
							- (inDate.compareTo(from) < 0 ? from.getTime() : inDate.getTime()))
					.divide(BigDecimal.valueOf(1000 * 60 * 60 * 24), 2, RoundingMode.HALF_UP);
			if (res.doubleValue() >= diff) {
				result = result + 1;
			}

		}
		return result;
	}

	public BigDecimal getOccupancyForPeriod(EnclosureDto enclosure, Date from, Date to) {
		BigDecimal result = new BigDecimal("0");
		StudyDto study = enclosure.getStudy();
		Set<Participant> participants = studyService.getParticipants(study);
		BigDecimal diff = BigDecimal.valueOf(to.toInstant().toEpochMilli() - from.toInstant().toEpochMilli())
				.divide(BigDecimal.valueOf(1000 * 60 * 60 * 24), 2, RoundingMode.HALF_UP);
		for (BiosampleEnclosureDto biosampleEnclosure : enclosure.getBiosampleEnclosures()) {
			Date dateOut = biosampleEnclosureService.getOutDate(biosampleEnclosure);
			if (dateOut == null) {
				BiosampleDto biosample = biosampleEnclosure.getBiosample();
				if (biosampleService.isDeadAt(biosample, to))
					dateOut = biosampleService.getTerminationExecutionDate(biosample);
				else if (biosampleService.isRemove(biosample, study, to))
					dateOut = biosampleService.getAssignment(biosample, study, to).getRemoveDate();
				else
					dateOut = studyService.getLastDate(biosampleEnclosure.getStudy(), participants);
			}
			Date dateIn = biosampleEnclosureService.getInDate(biosampleEnclosure);
			if (dateIn == null)
				dateIn = biosampleService.getDate(biosampleEnclosure.getBiosample(), biosampleEnclosure.getPhaseIn());
			if (dateIn == null || dateIn.compareTo(to) >= 0 || dateOut == null || dateOut.compareTo(from) <= 0)
				continue;
			BigDecimal res = BigDecimal
					.valueOf((dateOut.compareTo(to) > 0 ? to.getTime() : dateOut.getTime())
							- (dateIn.compareTo(from) < 0 ? from.getTime() : dateIn.getTime()))
					.divide(BigDecimal.valueOf(1000 * 60 * 60 * 24), 2, RoundingMode.HALF_UP);
			if (res.doubleValue() >= Math.min(diff.doubleValue(), 1)) {
				if (res.doubleValue() > 1)
					res = res.setScale(0, RoundingMode.DOWN);
				result = result.add(res);
			}

		}
		return result;
	}

	@SuppressWarnings("deprecation")
	public String generateNewName(EnclosureDto enclosure, StudyDto study, String cagename) {
		if(cagename == null) {			
			String res = study.getStudyId();
			if (study.getStudyId().startsWith("S-")) {
				try {
					res = "S" + Integer.parseInt(study.getStudyId().substring(2));
				} catch (Exception e) {
					res = study.getStudyId();
				}
			}
			res += "-";
			boolean free;
			int number = 1;
			do {
				free = true;
				cagename = String.format(res + "%02d", number++);
				enclosureloop: for (EnclosureDto e : study.getEnclosures()) {
					if (cagename.equals(e.getName())) {
						free = false;
						break enclosureloop;
					}
				}
			} while (!free);
		}
		enclosure.setName(cagename);
		return cagename;
	}

	@SuppressWarnings("deprecation")
	public void mapBiosampleEnclosures(EnclosureDto enclosure) {
		enclosure.setBiosampleEnclosures(
				biosampleEnclosureService.map(biosampleEnclosureService.getByEnclosure(enclosure.getId())));
		Collections.sort(enclosure.getBiosampleEnclosures());
	}

	public void mapFoodWaters(EnclosureDto enclosure) {
		enclosure.setFoodWater(foodWaterService.map(foodWaterService.getByEnclosure(enclosure.getId())));
	}

	public Date getInDate(EnclosureDto enclosure, BiosampleDto b, Date date) {
		for (BiosampleEnclosureDto biosampleEnclosure : enclosure.getBiosampleEnclosures()) {
            if (b.getSampleId() == biosampleEnclosure.getBiosample().getSampleId() && biosampleEnclosureService.containsDate(biosampleEnclosure, date, ContainmentType.FW)) 
            	return biosampleEnclosureService.getDateIn(biosampleEnclosure);
        }
        return null;
	}

	public Object getOutPhase(EnclosureDto enclosure, BiosampleDto biosample, Date date) {
		for (BiosampleEnclosureDto biosampleEnclosure : enclosure.getBiosampleEnclosures()) {
            if (biosample.getSampleId() == biosampleEnclosure.getBiosample().getSampleId() && biosampleEnclosureService.containsDate(biosampleEnclosure, date, ContainmentType.FW)) 
            	return biosampleEnclosureService.getDateOut(biosampleEnclosure);
        }
        return null;
	}
	
	public List<Enclosure> getEnclosure(Integer studyId) {
        return enclosureDao.getEnclosuresByStudy(studyId);
	}
	
	public EnclosureDto getEnclosure(StudyDto study, String name) {
        if (study == null) 
        	return null;
        for(EnclosureDto enclosure : study.getEnclosures()) {
        	if(enclosure.getName().equals(name)) {
        		return enclosure;
        	}
        }
       	return null;
	}

	public EnclosureDto newEnclosure(StudyDto study, String name) {
		EnclosureDto enclosure = new EnclosureDto();
		setStudy(enclosure, study);
		generateNewName(enclosure, study, name);
		return enclosure;
	}
}
