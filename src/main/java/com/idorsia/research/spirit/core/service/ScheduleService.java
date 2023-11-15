package com.idorsia.research.spirit.core.service;

import static com.idorsia.research.spirit.core.util.MiscUtils.nullSupportEqual;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.dao.ScheduleDao;
import com.idorsia.research.spirit.core.dto.ActionPatternsDto;
import com.idorsia.research.spirit.core.dto.AssignmentDto;
import com.idorsia.research.spirit.core.dto.NamedSamplingDto;
import com.idorsia.research.spirit.core.dto.PhaseDto;
import com.idorsia.research.spirit.core.dto.ScheduleDto;
import com.idorsia.research.spirit.core.dto.SchedulePhaseDto;
import com.idorsia.research.spirit.core.dto.StageDto;
import com.idorsia.research.spirit.core.dto.StudyDto;
import com.idorsia.research.spirit.core.dto.view.Disposal;
import com.idorsia.research.spirit.core.dto.view.StudyActionResultQuery;
import com.idorsia.research.spirit.core.model.ActionPatterns;
import com.idorsia.research.spirit.core.model.Phase;
import com.idorsia.research.spirit.core.model.Schedule;
import com.idorsia.research.spirit.core.model.SchedulePhase;
import com.idorsia.research.spirit.core.util.UserUtil;

import biweekly.ICalVersion;
import biweekly.io.ParseContext;
import biweekly.io.TimezoneInfo;
import biweekly.io.WriteContext;
import biweekly.parameter.ICalParameters;
import biweekly.property.RecurrenceRule;
import biweekly.util.ByDay;
import biweekly.util.Frequency;
import biweekly.util.Recurrence;
import biweekly.util.com.google.ical.compat.javautil.DateIterator;

@Service
public class ScheduleService extends AbstractService implements Serializable {

	private static final long serialVersionUID = 5387622847850767987L;
	@Autowired
	private ScheduleDao scheduleDao;
	@Autowired
	private SchedulePhaseService schedulePhaseService;
	@Autowired
	private ActionPatternsService actionPatternsService;
	@Autowired
	private PhaseService phaseService;
	@Autowired
	private StageService stageService;
	@Autowired
	private AssignmentService assignmentService;
	@Autowired
	private StudyActionResultService studyActionResultService;
	
	@SuppressWarnings("unchecked")
	private static Map<Integer, ScheduleDto> idToSchedule = (Map<Integer, ScheduleDto>) getCacheMap(ScheduleDto.class);

	public Schedule get(Integer id) {
		return scheduleDao.get(id);
	}

	public List<Schedule> list() {
		return scheduleDao.list();
	}

	public int getCount() {
		return scheduleDao.getCount();
	}

	public Integer saveOrUpdate(Schedule schedule) {
		return scheduleDao.saveOrUpdate(schedule);
	}

	public int addSchedule(Schedule schedule) {
		return scheduleDao.addSchedule(schedule);
	}

	public List<Schedule> getSchedulesByPhases(List<Phase> phases) {
		return scheduleDao.getSchedulesByPhases(phases);
	}

	public List<Schedule> getByStudy(Integer studyId) {
		return scheduleDao.getByStudy(studyId);
	}

	public ScheduleDao getScheduleDao() {
		return scheduleDao;
	}

	public void setScheduleDao(ScheduleDao scheduleDao) {
		this.scheduleDao = scheduleDao;
	}

	public List<Schedule> getSchedulesByStage(List<StageDto> stages) {
		List<Integer> stageIds = new ArrayList<Integer>();
		for(StageDto st : stages) {
			stageIds.add(st.getId());
		}
		return scheduleDao.getSchedulesByStages(stageIds);
	}

	public ArrayList<PhaseDto> getPhases(ScheduleDto schedule) {
		ArrayList<PhaseDto> phaseList = new ArrayList<>();
		schedule.getSchedulePhases().forEach(sp -> {
			phaseList.add(sp.getPhase());
		});
		Collections.sort(phaseList);
		return phaseList;
	}

	public ScheduleDto getScheduleDto(Integer id) {
		return map(get(id));
	}

	@SuppressWarnings("deprecation")
	public synchronized ScheduleDto map(Schedule schedule) {
		ScheduleDto scheduleDto = idToSchedule.get(schedule.getId());
		if(scheduleDto==null) {
			scheduleDto = dozerMapper.map(schedule, ScheduleDto.class,"scheduleCustomMapping");
			if(idToSchedule.get(schedule.getId())==null)
				idToSchedule.put(schedule.getId(), scheduleDto);
			else
				scheduleDto=idToSchedule.get(schedule.getId());
			ActionPatterns ap = actionPatternsService.getBySchedule(schedule.getId());
			if(ap!=null)
				scheduleDto.setActionPattern(actionPatternsService.map(ap));
			createRecurrence(scheduleDto);
		}
		return scheduleDto;
	}
	
	@Transactional
	public void save(ScheduleDto schedule) throws Exception {
		save(schedule, false);
	}
	
	@SuppressWarnings("deprecation")
	protected void save(ScheduleDto schedule, Boolean cross) throws Exception {
		try {
			if(!savedItems.contains(schedule)) {
				savedItems.add(schedule);
				if(schedule.getId()!=Constants.NEWTRANSIENTID)
					deleteChildren(schedule);
				schedule.setUpdDate(new Date());
				schedule.setUpdUser(UserUtil.getUsername());
				if(schedule.getId().equals(Constants.NEWTRANSIENTID)) {
					schedule.setCreDate(new Date());
					schedule.setCreUser(UserUtil.getUsername());
				}
				schedule.setId(saveOrUpdate(dozerMapper.map(schedule, Schedule.class, "scheduleCustomMapping")));
				idToSchedule.put(schedule.getId(), schedule);
				if(schedule.getSchedulePhasesNoMapping()!=null)
					for(SchedulePhaseDto schedulePhase : schedule.getSchedulePhases())
						schedulePhaseService.save(schedulePhase, true);
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
	private void deleteChildren(ScheduleDto schedule) {
		if(schedule.getSchedulePhasesNoMapping()!=null) {
			for(SchedulePhase sp : schedulePhaseService.getBySchedule(schedule.getId())) {
				Boolean found = false;
				for(SchedulePhaseDto child : schedule.getSchedulePhases()){
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
	public void delete(ScheduleDto schedule) {
		delete(schedule, false);
	}
	
	protected void delete(ScheduleDto schedule, Boolean cross) {
		scheduleDao.delete(schedule.getId());
	}
	
	@SuppressWarnings("deprecation")
	public void createRecurrence(ScheduleDto schedule) {
		String rrule = schedule.getrRule();
		Recurrence recurrence = schedule.getRecurrence();
		if (rrule == null || "".equals(rrule)) {
			recurrence = null;
		} else {
			ParseContext context = new ParseContext();
			context.setVersion(ICalVersion.V2_0);
			recurrence = schedule.getScribe().parseText(rrule, null, new ICalParameters(), context).getValue();
		}
		ArrayList<LocalTime> timepointSet = new ArrayList<>();
		if (schedule.getTimePoints() != null) {
			String[] split = schedule.getTimePoints().split(",");
			for (String s : split) {
				try {
					timepointSet.add(LocalTime.parse(s, DateTimeFormatter.ISO_LOCAL_TIME));
				} catch (DateTimeParseException e) {
				}
			}
		}
		schedule.setTimepointSet(timepointSet);
		schedule.setRecurrence(recurrence);
	}
	
	@SuppressWarnings("deprecation")
	public void setRecurrence(ScheduleDto schedule, Recurrence recurrence) {
		Recurrence oldRecurrence = schedule.getRecurrence();
		String oldRrule = schedule.getrRule();
		schedule.setRecurrence(recurrence);
		if (recurrence == null) {
			schedule.setrRule(null);
		} else {
			WriteContext context = new WriteContext(ICalVersion.V2_0, new TimezoneInfo(), null);
			schedule.setrRule(schedule.getScribe().writeText(new RecurrenceRule(recurrence), context));
		}
		if(recurrence!=null && recurrence.getFrequency().equals(Frequency.WEEKLY) && schedule.getActionPattern().getChildren().size()>0) {
			for(AssignmentDto a : schedule.getActionPattern().getStage().getAssignments()) {
				ActionPatternsDto ap = assignmentService.getActionPatternByParent(a, schedule.getActionPattern());
				if(ap!=null) {
					setRecurrence(ap.getSchedule(), null);
					ScheduleDto cloneSchedule = new ScheduleDto();
					setRrule(cloneSchedule, schedule.getrRule());
					try {
						cloneSchedule.setLastPhase(schedule.getLastPhase());
						cloneSchedule.setStartDate(schedule.getStartDate());
						removeTimepoint(cloneSchedule, LocalTime.MIDNIGHT);
						schedule.getTimePointsSorted().forEach(e-> addTimePoint(cloneSchedule, e));
						actionPatternsService.setSchedule(ap, cloneSchedule);
						setPhases(cloneSchedule, a.getStratification());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		try {
			setPhases(schedule);
		} catch (Exception e) {
			schedule.setRecurrence(oldRecurrence);
			schedule.setrRule(oldRrule);
		}
	}

	public void setPhases(ScheduleDto schedule) throws Exception {
		setPhases(schedule, Duration.ZERO);
	}
	
	private void saveTimepoint(ScheduleDto schedule) {
		String timepoints = "";
		for (LocalTime localTime : schedule.getTimepointSet()) {
			timepoints += localTime.format(DateTimeFormatter.ISO_LOCAL_TIME)+",";
		}
		schedule.setTimePoints(timepoints);
	}

	public void setPhases(ScheduleDto schedule, Duration stratification) throws Exception {
		ActionPatternsDto pattern = schedule.getActionPattern();
		Duration lastPhase = schedule.getLastPhase();
		if (pattern == null) return;
		//Pattern deleted
		if (pattern.getStage() == null) {
			removePhases(schedule, new HashSet<>(schedule.getSchedulePhases()));
			return;
		}
		Set<SchedulePhaseDto> verifiedElements = new HashSet<>();
		StageDto stage = pattern.getStage();
		for (Duration duration : getPossiblePhases(schedule, stratification)) {
			if (lastPhase != null && duration.compareTo(lastPhase) > 0) continue;
			PhaseDto phase = stageService.getPhase(stage, duration);
			if (phase == null) {
				phase = new PhaseDto(duration);
				phaseService.setStage(phase, stage);
			}
			SchedulePhaseDto schedulePhase = getSchedulePhase(schedule, phase);
			if (schedulePhase == null) {
				schedulePhase = new SchedulePhaseDto();
				schedulePhaseService.setSchedule(schedulePhase, schedule);
				schedulePhaseService.setPhase(schedulePhase, phase);
			}
			verifiedElements.add(schedulePhase);
		}
		Set<SchedulePhaseDto> toRemove = new HashSet<>();
		for (SchedulePhaseDto schedulePhaseLink : schedule.getSchedulePhases()) {
			if (!verifiedElements.contains(schedulePhaseLink)) toRemove.add(schedulePhaseLink);
		}
		removePhases(schedule, toRemove);
	}
	
	public boolean setPhase(ScheduleDto schedule, Duration duration) {
		int oldStartDate = schedule.getStartDate();
		ArrayList<LocalTime> oldTimepoints = new ArrayList<>(schedule.getTimepointSet());
		setStartDate(schedule, phaseService.getDays(duration));
		LocalTime localTime = LocalTime.of(phaseService.getHours(duration), phaseService.getMinutes(duration));
		schedule.getTimepointSet().clear();
		schedule.getTimepointSet().add(localTime);
		try {
			setPhases(schedule);
		} catch (Exception e) {
			setStartDate(schedule, oldStartDate);
			schedule.getTimepointSet().clear();
			schedule.getTimepointSet().addAll(oldTimepoints);
			return false;
		}
		saveTimepoint(schedule);
		return true;
		
	}

	private SchedulePhaseDto getSchedulePhase(ScheduleDto schedule, PhaseDto phase) {
		for (SchedulePhaseDto schedulePhaseLink : schedule.getSchedulePhases()) {
			if (schedulePhaseLink.getPhase().equals(phase)) return schedulePhaseLink;
		}
		return null;
	}

	public Collection<Duration> getPossiblePhases(ScheduleDto schedule) {
		return getPossiblePhases(schedule, Duration.ZERO);
	}

	public Collection<Duration> getPossiblePhases(ScheduleDto schedule, Duration stratification) {
		HashSet<Duration> durations = new HashSet<>();
		StageDto stage = schedule.getActionPattern().getStage();
		Date stageFirstDate=stageService.getFirstDate(stage);
		Date firstDate=stageFirstDate==null ? new Date()
				:new Date(stageFirstDate.toInstant().toEpochMilli()+stratification.toMillis());
		ZonedDateTime zonedDateTime =null;
		Recurrence recurrence = schedule.getRecurrence();
		if(firstDate!=null)
			zonedDateTime = ZonedDateTime.ofInstant(firstDate.toInstant(),ZoneId.systemDefault());
		if (zonedDateTime == null) {
			zonedDateTime = ZonedDateTime.now(ZoneId.systemDefault());
			if (stage.getStartingDay() != null)
				zonedDateTime = zonedDateTime.with(TemporalAdjusters.next(stage.getStartingDay()));
		}
		if (recurrence != null) {
			DateIterator it = recurrence.getDateIterator(Date.from(zonedDateTime.toInstant()), TimeZone.getTimeZone(zonedDateTime.getZone()));
			List<Date>usedDate=new ArrayList<>();
			List<Integer> validDays = new ArrayList<>();
			for(ByDay day : recurrence.getByDay()) {
				validDays.add(day.getDay().getCalendarConstant());
			}
			boolean needAdjustment=(schedule.getStartDate()>0||!Duration.ZERO.equals(stratification))&&recurrence.getFrequency().equals(Frequency.WEEKLY)&&recurrence.getByDay().size()>0;
			int moreDays = 0;
			while (it.hasNext()) {
				Date date = it.next();
				LocalDate localDate = Instant.ofEpochMilli(date.getTime()+Duration.ofDays(schedule.getStartDate()).toMillis()+Duration.ofDays(moreDays).toMillis())
						.atZone(zonedDateTime.getZone())
						.toLocalDate();
				if(needAdjustment) {
					boolean isGoodDate=false;
					int diffSeconds = (int) ((date.toInstant().toEpochMilli()-date.toInstant().toEpochMilli())%(1000*60*60*24))/1000;
					while(!isGoodDate) {
						Calendar c = Calendar.getInstance();
						if (firstDate != null)
							c.setTime(firstDate);
						c.add(Calendar.DATE, schedule.getStartDate()+moreDays);
						Date newDate=c.getTime();
						if(validDays.contains(c.get(Calendar.DAY_OF_WEEK))&&!usedDate.contains(newDate)) {
							isGoodDate=true;
							usedDate.add(newDate);
						}else {
							moreDays=moreDays+1;
						}
					}
					localDate = Instant.ofEpochMilli((firstDate!=null?firstDate.getTime():date.getTime())+Duration.ofDays(schedule.getStartDate()).toMillis()+Duration.ofDays(moreDays).toMillis()+Duration.ofSeconds(diffSeconds).toMillis())
								.atZone(zonedDateTime.getZone())
								.toLocalDate();
				}
				
				for (LocalTime timepoint : schedule.getTimepointSet()) {
					LocalDateTime next = LocalDateTime.of(localDate, timepoint);
					durations.add(Duration.between(zonedDateTime.toLocalDateTime().truncatedTo(ChronoUnit.DAYS), next));
				}
			}
		} else {
			for (LocalTime timepoint : schedule.getTimepointSet()) {
				durations.add(Duration.ofDays(schedule.getStartDate()).plus(Duration.ofSeconds(timepoint.toSecondOfDay())));
			}
		}
		TreeSet<Duration> treeSet = new TreeSet<Duration>(durations);
		return treeSet;
	}

	public void removePhases(ScheduleDto schedule, Set<SchedulePhaseDto> toRemove) throws Exception {
		Set<PhaseDto> phases = new HashSet<>();
		toRemove.stream().forEach(a -> phases.add(a.getPhase()));
		StudyDto study;
		if(schedule.getActionPattern().getAction() instanceof NamedSamplingDto)
			study=((NamedSamplingDto)schedule.getActionPattern().getAction()).getStudy();
		else if(schedule.getActionPattern().getAction() instanceof Disposal && ((Disposal)schedule.getActionPattern().getAction()).getSampling()!=null) {
			study = ((Disposal)schedule.getActionPattern().getAction()).getSampling().getStudy();
		}else
			study=schedule.getActionPattern().getStage()==null?null:schedule.getActionPattern().getStage().getStudy();
		StudyActionResultQuery studyActionResultQuery = new StudyActionResultQuery();
		studyActionResultQuery.setStage(schedule.getActionPattern().getStage());
		studyActionResultQuery.setAction(schedule.getActionPattern().getAction());
		studyActionResultQuery.setPhases(phases);
		studyActionResultQuery.setStudy(study);
		studyActionResultQuery.setSchedule(schedule);
		studyActionResultService.checkBeforeDeleteStudyActionResult(Constants.SCHEDULE, studyActionResultQuery);

		for (SchedulePhaseDto schedulePhase : toRemove) {
			schedulePhaseService.remove(schedulePhase);
		}
		
	}
	
	public void removeSchedulePhase(ScheduleDto schedule, SchedulePhaseDto schedulePhase, boolean cross) {
		//prevent endless loop
		if (!schedule.getSchedulePhases().contains(schedulePhase))
			return ;
		//remove old member
		schedule.getSchedulePhases().remove(schedulePhase);
		if(schedulePhase.getPhase()!=null)
			schedulePhase.getPhase().getSchedulePhases().remove(schedulePhase);
		//remove child's owner
		if (!cross) {
			schedulePhaseService.setSchedule(schedulePhase, null, true);
		}
	}

	public void addSchedulePhase(ScheduleDto schedule, SchedulePhaseDto schedulePhase, boolean cross) {
		//prevent endless loop
		if (schedule.getSchedulePhases().contains(schedulePhase))
			return ;
		//add new member
		schedule.getSchedulePhases().add(schedulePhase);
		//update child if request is not from it
		if (!cross) {
			schedulePhaseService.setSchedule(schedulePhase, schedule, true);
		}
	}

	@SuppressWarnings("deprecation")
	public void setRrule(ScheduleDto schedule, String rrule) {
		if ("".equals(rrule)) rrule = null;
		schedule.setrRule(rrule);
		createRecurrence(schedule);
		try {
			setPhases(schedule);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean removeTimepoint(ScheduleDto schedule, LocalTime localTime) {
		if (!schedule.getTimepointSet().contains(localTime) || localTime == null) 
			return false;
		schedule.getTimepointSet().remove(localTime);
		try {
			setPhases(schedule);
		} catch (Exception e) {
			schedule.getTimepointSet().add(localTime);
			return false;
		}
		saveTimepoint(schedule);
		return true;
	}

	public boolean addTimePoint(ScheduleDto schedule, LocalTime localTime) {
		if (schedule.getTimepointSet().contains(localTime) || localTime == null) return false;
		schedule.getTimepointSet().add(localTime);
		try {
			setPhases(schedule);
		} catch (Exception e) {
			return false;
		}
		saveTimepoint(schedule);
		return true;
	}

	@SuppressWarnings("deprecation")
	public void setActionPattern(ScheduleDto schedule, ActionPatternsDto actionPattern, boolean cross) throws Exception {
		//prevent endless loop
		if (nullSupportEqual(schedule.getActionPattern(), actionPattern))
			return;
		//remove from the old owner
		if (schedule.getActionPattern() !=null && !(cross && actionPattern == null))
			actionPatternsService.setSchedule(schedule.getActionPattern(), null, true);
		//set new owner
		schedule.setActionPattern(actionPattern);
		setPhases(schedule);
		//set myself to new owner
		if (actionPattern !=null && !cross)
			actionPatternsService.setSchedule(actionPattern, schedule, true);
	}

	public PhaseDto getFirstPhase(ScheduleDto schedule) {
		ArrayList<PhaseDto> phaseList = getPhases(schedule);
		if (phaseList.size() == 0) return null;
		return phaseList.get(0);
	}

	@SuppressWarnings("deprecation")
	public boolean setStartDate(ScheduleDto schedule, int startDate) {
		int oldStartDate = schedule.getStartDate();
        schedule.setStartDate(startDate);
		try {
			setPhases(schedule);
			return true;
		} catch (Exception e) {
			 schedule.setStartDate(oldStartDate);
			return false;
		}
		
	}

	public ScheduleDto newScheduleDto(String rrule) {
		return newScheduleDto(0, rrule, LocalTime.MIDNIGHT);
	}
	
	public ScheduleDto newScheduleDto(Duration duration) {
		return newScheduleDto(phaseService.getDays(duration), "FREQ=DAILY;INTERVAL=1;COUNT=1", LocalTime.of(phaseService.getHours(duration), phaseService.getMinutes(duration)));
	}

	public ScheduleDto newScheduleDto(int startDate, String rrule, LocalTime localTime) {
		try {
			ScheduleDto scheduleDto = new ScheduleDto();
			addTimePoint(scheduleDto, localTime);
			setStartDate(scheduleDto, startDate);
			setRrule(scheduleDto, rrule);
			return scheduleDto;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public String getDescription(ScheduleDto schedule) {
		String result = "";
		if (schedule.getRecurrence() == null) result = "On day " + schedule.getStartDate() + " ";
		else {
			result = "From day " + schedule.getStartDate() + " ";
			switch (schedule.getRecurrence().getFrequency()) {
				case DAILY:
					result += "every " + (schedule.getRecurrence().getInterval().intValue()>1 ? schedule.getRecurrence().getInterval() + ". " : "") + "day ";
					break;
				case WEEKLY:
					if (schedule.getRecurrence ().getByDay().size() > 0) {
						result += "on ";
						for (ByDay byDay : schedule.getRecurrence().getByDay()) {
							result += byDay.getDay().getAbbr() + " ";
						}
					}
					break;
				default:
					return "unknown schedule ";
			}
			if (schedule.getRecurrence().getCount() != null) result += "/ " + schedule.getRecurrence().getCount() + " occurrences ";
			if (schedule.getLastPhase() != null) result += "until " + phaseService.getDurationStringDays(schedule.getLastPhase()) + " ";
		}
		if (schedule.getTimepointSet().size() > 1 ) {
			result += "RT +" + schedule.getTimepointSet().stream().map(t -> t.getHour() + "h " + t.getMinute() + "m").collect(Collectors.joining(", "));
		} else {
			result += "RT +" + schedule.getTimepointSet().stream().map(t -> t.getHour()
					+ (t.getHour() == 1 ? " hour " : " hours ")
					+ t.getMinute() + (t.getMinute() == 1 ? " minute" :" minutes")).collect(Collectors.joining(", "));
		}
		return result;
	}

	@SuppressWarnings("deprecation")
	public void mapSchedulePhases(ScheduleDto schedule) {
		List<SchedulePhaseDto> schedulePhases = schedulePhaseService.map(schedulePhaseService.getBySchedule(schedule.getId()));
		if(schedulePhases != null) {			
			schedule.setSchedulePhases(new HashSet<SchedulePhaseDto>(schedulePhases));
		}
	}
}
