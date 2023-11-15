package com.idorsia.research.spirit.core.service;

import static com.idorsia.research.spirit.core.util.MiscUtils.nullSupportEqual;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.dao.PhaseDao;
import com.idorsia.research.spirit.core.dto.BiosampleDto;
import com.idorsia.research.spirit.core.dto.BiosampleEnclosureDto;
import com.idorsia.research.spirit.core.dto.EnclosureDto;
import com.idorsia.research.spirit.core.dto.PhaseDto;
import com.idorsia.research.spirit.core.dto.SchedulePhaseDto;
import com.idorsia.research.spirit.core.dto.StageDto;
import com.idorsia.research.spirit.core.model.Phase;
import com.idorsia.research.spirit.core.model.SchedulePhase;
import com.idorsia.research.spirit.core.model.Stage;

@Service
public class PhaseService extends AbstractService implements Serializable {
	
	private static final long serialVersionUID = -8632553614353496447L;
	@Autowired
	private PhaseDao phaseDao;
	@Autowired
	private StageService stageService;
	@Autowired
	private SchedulePhaseService schedulePhaseService;
	@Autowired
	private BiosampleService biosampleService;
	
	static int i=0;
	
	@SuppressWarnings("unchecked")
	private static Map<Integer, PhaseDto> idToPhase = (Map<Integer, PhaseDto>) getCacheMap(PhaseDto.class);
	
	public Phase get(Integer id) {
		return phaseDao.get(id);
	}
	
	public List<Phase> list() {
		return phaseDao.list();
	}
	
	public int getCount() {
		return phaseDao.getCount();
	}

	public Integer saveOrUpdate(Phase phase) {
		return phaseDao.saveOrUpdate(phase);
	}

	public int addPhase(Phase phase) {
		return phaseDao.addPhase(phase);
	}

	public List<Phase> getPhasesByStage(Integer stageId) {
		return phaseDao.getPhasesByStage(stageId);
	}
	
	public List<Phase> getPhasesByStages(List<Stage> stages) {
		return phaseDao.getPhasesByStages(stages);
	}
	
	public List<Phase> getByStudy(Integer studyId) {
		return phaseDao.getPhasesByStudyId(studyId);
	}
	
	public List<Phase> getPhases(int studyId) {
		return phaseDao.getPhasesByStudy(studyId);
	}
	
	public Set<Integer> getBySchedule(Set<Integer> matchingSchedules) {
		return phaseDao.getBySchedule(matchingSchedules);
	}
	
	public Phase getByStageAndDuration(double phaseDuration, Integer stageId) {
		return phaseDao.getByStageAndDuration(phaseDuration, stageId);
	}
	
	public PhaseDao getPhaseDao() {
		return phaseDao;
	}

	public void setPhaseDao(PhaseDao phaseDao) {
		this.phaseDao = phaseDao;
	}

	public Date add(Date date, Duration duration) {
		if (duration==null) return date;
		if (date==null) return null;
		return new Date(date.getTime()+duration.toMillis());
	}

	public Duration getDisplayPhase(PhaseDto phase) {
		return phase != null && phase.getPhase() != null ? phase.getPhase().minus(stageService.getOffsetOfD0(phase.getStage())) : null;
	}

	public List<PhaseDto> map(List<Phase> phases) {
		List<PhaseDto> res = new ArrayList<PhaseDto>();
		for(Phase phase : phases) {
			res.add(map(phase));
		}
		return res;
	}

	public PhaseDto map(Phase phase) {
		PhaseDto phaseDto = idToPhase.get(phase.getId());
		if(phaseDto==null) {
			phaseDto = dozerMapper.map(phase, PhaseDto.class,"phaseCustomMapping");
			if(idToPhase.get(phase.getId())==null)
				idToPhase.put(phase.getId(), phaseDto);
			else 
				phaseDto = idToPhase.get(phase.getId());
		}		
		return phaseDto;
	}
	
	@Transactional
	public void save(PhaseDto phase) throws Exception {
		save(phase, false);
	}
	
	@SuppressWarnings("deprecation")
	protected void save(PhaseDto phase, Boolean cross) throws Exception {
		try {
			if(phase!=null && !savedItems.contains(phase)) {
				savedItems.add(phase);
				if(phase.getId()!=Constants.NEWTRANSIENTID)
					deleteChildren(phase);
				if(phase.getStage().getId()==Constants.NEWTRANSIENTID)stageService.save(phase.getStage(), true);
				phase.setId(saveOrUpdate(dozerMapper.map(phase, Phase.class, "phaseCustomMapping")));
				idToPhase.put(phase.getId(), phase);
				if(phase.getSchedulePhasesNoMapping()!=null)
					for(SchedulePhaseDto schedulePhase : phase.getSchedulePhases()) {
						schedulePhaseService.save(schedulePhase, true);
				}
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
	
	@SuppressWarnings("deprecation")
	private void deleteChildren(PhaseDto phase) {
		if(phase.getSchedulePhasesNoMapping()!=null) {
			for(SchedulePhase sp : schedulePhaseService.getByPhase(phase.getId())) {
				Boolean found = false;
				for(SchedulePhaseDto child : phase.getSchedulePhases()){
					if(sp.getId().equals(child.getId())) {
						found=true;
						break;
					}
				}
				if(!found) {
					schedulePhaseService.delete(schedulePhaseService.map(sp), true);
				}
			}
		}
	}

	@Transactional
	public void delete(PhaseDto phase) throws Exception {
		delete(phase, false);
	}
	
	protected void delete(PhaseDto phase, Boolean cross) throws Exception {
		for(BiosampleDto b : biosampleService.map(biosampleService.getBiosampleByEndPhase(phase.getId()))) {
			b.setEndPhase(null);
		}
		phaseDao.delete(phase.getId());
	}
	
	public PhaseDto getPhaseDto(Integer id) {
		return map(get(id));
	}

	public Integer getDays(Duration duration) {
		if (duration == null) return -1;
		return (int) duration.toDays();
	}

	public int getHours(Duration duration) {
		if (duration == null) return -1;
		return (int)(duration.toMinutes() % 1440) / 60;
	}
	
	public int getMinutes(Duration duration) {
		if (duration == null) return -1;
		return (int)duration.toMinutes() % 1440 % 60;
	}

	public void setStage(PhaseDto phase, StageDto stage) {
		setStage(phase, stage, false);
	}

	@SuppressWarnings("deprecation")
	public void setStage(PhaseDto phase, StageDto stage, boolean cross) {
		StageDto previousStage = phase.getStage();
		//prevent endless loop
		if (nullSupportEqual(previousStage, stage))
			return;
		//remove from the old owner
		if (previousStage !=null && !(cross && stage == null))
			stageService.removePhase(previousStage, phase, true);
		//set new owner
		phase.setStage(stage);
		//set myself to new owner
		if (stage !=null && !cross)
			stageService.addPhase(stage, phase, true);
	}

	public void removeSchedulePhase(PhaseDto phase, SchedulePhaseDto schedulePhase, boolean cross) {
		//prevent endless loop
		if (!phase.getSchedulePhases().contains(schedulePhase))
			return ;
		//remove old member
		phase.getSchedulePhases().remove(schedulePhase);
		//remove child's owner
		if (!cross) {
			schedulePhaseService.setPhase(schedulePhase, null, true);
		}
		remove(phase);
	}

	public void addSchedulePhase(PhaseDto phase, SchedulePhaseDto schedulePhase, boolean cross) {
		//prevent endless loop
		if (phase.getSchedulePhases().contains(schedulePhase))
			return ;
		//add new member
		phase.getSchedulePhases().add(schedulePhase);
		//update child if request is not from it
		if (!cross) {
			schedulePhaseService.setPhase(schedulePhase, phase, true);
		}
	}
	
	public void remove(PhaseDto phase) {
		StageDto stage = phase.getStage();
		//Stage already null
		if(stage==null || stage.getPhases()==null) return;
		//Has actions
		if (phase.getSchedulePhases().size() > 0) return;
		//Subject movement
		for (EnclosureDto enclosure : phase.getStage().getStudy().getEnclosures()) {
			for (BiosampleEnclosureDto biosampleEnclosure : enclosure.getBiosampleEnclosures()) {
				if (phase.equals(biosampleEnclosure.getPhaseIn()) || phase.equals(biosampleEnclosure.getPhaseOut())) return;
			}
		}
		//Result attached
		setStage(phase, null);
	}

	public Duration getDurationFromString(String value) {
		try {
			Duration parse = Duration.parse(value);
			if (parse != null) return parse;
		} catch (Exception e) {
		}
		if (value.matches("(d-?[0-9]+)?[^0-9]?([0-9]{0,2}h)?([0-9]{0,2})?")) {
			try {
				Pattern dayPattern = Pattern.compile("(d-?[0-9]+)?[^0-9]?([0-9]{0,2}h)?([0-9]{0,2})?");
				Matcher matcher = dayPattern.matcher(value);
				if (matcher.matches()) {
					if (matcher.group(0) == null || matcher.group(0).length()<1) throw new Exception("Empty");
					String dayString = matcher.group(1);
					int day = dayString == null || dayString.length()<1 ? 0 : Integer.parseInt(dayString.substring(1));
					String hourString = matcher.group(2);
					int hour = hourString == null || hourString.length()<1 ? 0 : Integer.parseInt(hourString.substring(0, hourString.length()-1));
					int minute = matcher.group(3) == null || matcher.group(3).length()<1 ? 0 : Integer.parseInt(matcher.group(3));
					int minutes = day * 1440 + hour * 60 + minute;
					return Duration.ofMinutes(minutes);
				}
			} catch (Exception e) {
			}
		}
		try {
			String[] split = value.split("d");
			int day = Integer.parseInt(split[0]);
			String[] split1 = split[1].split(":");
			int hour = Integer.parseInt(split1[0]);
			int minute = Integer.parseInt(split1[1]);
			int minutes = day * 1440 + hour * 60 + minute;
			return Duration.ofMinutes(minutes);
		} catch (Exception e) {
		}
		return null;
	}

	public String getDurationStringDays(Duration duration) {
		if (duration == null) return "";
		long minutes = duration.toMinutes();
		long absMinutes = Math.abs(minutes);
		if (minutes < 0) {
			String negative;
			if (absMinutes % 1440 == 0) {
				negative = String.format(
						"%03dd00:00",
						absMinutes / 1440);
			} else {
				if (absMinutes % 1440 % 60 == 0) {
					negative = String.format(
							"%03dd%02d:00",
							absMinutes / 1440 + 1,
							24 - (absMinutes % 1440) / 60);
				} else {
					negative = String.format(
							"%03dd%02d:%02d",
							absMinutes / 1440 + 1,
							23 - (absMinutes % 1440) / 60,
							60 - absMinutes % 1440 % 60);
				}
			}
			return "-" + negative;
		} else {
			return String.format(
				"%03dd%02d:%02d",
				absMinutes / 1440,
				(absMinutes % 1440) / 60,
				absMinutes % 1440 % 60);
		}
	}
	
	public String getDurationStringHours(Duration duration) {
		if (duration == null) return "";
		long minutes = duration.toMinutes();
		long absMinutes = Math.abs(minutes);
		String positive = String.format(
				"%02d:%02d",
				absMinutes  / 60,
				absMinutes % 60);
		return minutes < 0 ? "-" + positive : positive;
	}

	public long getTime(PhaseDto phase, boolean display) {
		return display && getDisplayPhase(phase) != null ?
				getDisplayPhase(phase).getSeconds() - getDisplayPhase(phase).toDays()*24*60*60
				: phase.getPhase().getSeconds() - phase.getPhase().toDays()*24*60*60;
	}
	
	public int getDays(PhaseDto phase, boolean display) {
		return getDays(display && getDisplayPhase(phase) != null ? getDisplayPhase(phase) : phase.getPhase());
	}
	
	public int getHours(PhaseDto phase, boolean display) { 
		return getHours(display && phase != null && getDisplayPhase(phase) != null ? getDisplayPhase(phase) : phase.getPhase()); 
	}
	
	public int getMinutes(PhaseDto phase, boolean display) {
		return getMinutes(display && phase != null && getDisplayPhase(phase) != null ? getDisplayPhase(phase) : phase.getPhase());
	}

	public static int getDays(PhaseDto phase, Duration duration) {
		if (duration == null) return -1;
		return (int) duration.toDays();
	}
	public static int getHours(PhaseDto phase, Duration duration) {
		if (duration == null) return -1;
		return (int)(duration.toMinutes() % 1440) / 60;
	}
	public static int getMinutes(PhaseDto phase, Duration duration) {
		if (duration == null) return -1;
		return (int)duration.toMinutes() % 1440 % 60;
	}

	public Duration getPhase(PhaseDto phase, boolean reference) {
		return reference ? getDisplayPhase(phase) : phase.getPhase();
	}

	public boolean isSameDay(Date date1, Date date2) {
		LocalDate localDate1 = date1.toInstant()
				.atZone(ZoneId.systemDefault())
				.toLocalDate();
		LocalDate localDate2 = date2.toInstant()
				.atZone(ZoneId.systemDefault())
				.toLocalDate();
		return localDate1.isEqual(localDate2);
	}

	@SuppressWarnings("deprecation")
	public void mapSchedulePhases(PhaseDto phase) {
		phase.setSchedulePhases(schedulePhaseService.map(schedulePhaseService.getByPhase(phase.getId())));
	}

	public String getShortName(PhaseDto phase) {
		try {
			return getDurationStringDays(getDisplayPhase(phase));
		} catch(NullPointerException e) {
			return "removed";
		}
	}

}
