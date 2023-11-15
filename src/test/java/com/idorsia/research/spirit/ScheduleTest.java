package com.idorsia.research.spirit;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.util.CollectionUtils;

import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.model.Schedule;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ScheduleTest extends AbstractSpiritTest {

	private static final Integer SCHEDULE_ID = 3580;

	@Test
	public void aTestGet() {
		Schedule s = getScheduleService().get(SCHEDULE_ID);
		assertNotNull(s);
		assertTrue(s.getId().equals(SCHEDULE_ID));
	}

	@Test
	public void bTestList() {
		List<Schedule> schedules = getScheduleService().list();
		assertNotNull(schedules);
		assertTrue(!CollectionUtils.isEmpty(schedules));
	}

	@Test
	public void cTestGetCount() {
		assertTrue(getScheduleService().getCount()>0);
	}

	@Test
	public void dTestAddSchedule() {
		Schedule s = new Schedule();
		s.setId(getScheduleService().getSequence(Constants.STUDY_SCHEDULE_SEQUENCE_NAME));
		s.setStartDate(10);
		s.setrRule("FREQ=DAILY;COUNT=1;INTERVAL=1");
		s.setTimePoints("00:00:00");
		assertNotNull(getScheduleService().addSchedule(s));
	}
	
	@Test
	public void eTestSaveOrUpdate() {
		Schedule s = getLastInserted();
		s.setStartDate(15);
		getScheduleService().saveOrUpdate(s);
		assertTrue(getScheduleService().get(s.getId()).getStartDate().equals(15));
	}

	@Test
	public void fTestDelete() {
		int before = getScheduleService().getCount();
		getScheduleService().delete(getScheduleService().map(getLastInserted()));
		assertTrue(before - getScheduleService().getCount() == 1);
	}

	private Schedule getLastInserted() {
		List<Schedule> list = getScheduleService().list();
		Collections.sort(list, new Comparator<Schedule>() {
			public int compare(Schedule s1, Schedule s2) {
				return Integer.compare(s2.getId(), s1.getId());
			}
		});
		Schedule a = list.get(0);
		return a;
	}
	
}
