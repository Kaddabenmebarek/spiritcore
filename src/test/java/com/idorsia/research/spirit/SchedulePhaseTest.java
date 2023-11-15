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
import com.idorsia.research.spirit.core.dto.SchedulePhaseDto;
import com.idorsia.research.spirit.core.model.SchedulePhase;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SchedulePhaseTest extends AbstractSpiritTest {

	private static final int SCHEDULEPHASEID = 990;
	private static final int PHASEID = 5710;
	private static final int SCHEDULEID = 271768;

	@Test
	public final void aTestGet() {
		assertNotNull(getSchedulePhaseService().get(SCHEDULEPHASEID));
	}

	@Test
	public final void bTestDozerMapping() {
		SchedulePhaseDto sDto = getDozerMapper().map(getSchedulePhaseService().get(SCHEDULEPHASEID),
				SchedulePhaseDto.class, "schedulePhaseCustomMapping");
		assertNotNull(sDto.getSchedule());
		assertNotNull(sDto.getPhase());
		SchedulePhase s = getDozerMapper().map(sDto, SchedulePhase.class, "schedulePhaseCustomMapping");
		assertTrue(s.getScheduleId().equals(sDto.getSchedule().getId()));
		assertTrue(s.getPhaseId().equals(sDto.getPhase().getId()));
	}

	@Test
	public final void cTestGetByPhase() {
		assertTrue(!CollectionUtils.isEmpty(getSchedulePhaseService().getByPhase(PHASEID)));
	}

	@Test
	public final void dTestGetBySchedule() {
		assertNotNull(getSchedulePhaseService().getBySchedule(SCHEDULEID));
	}

	@Test
	public final void eTestList() {
		assertTrue(!CollectionUtils.isEmpty(getSchedulePhaseService().list()));
	}

	@Test
	public final void fTestGetCount() {
		assertTrue(getSchedulePhaseService().getCount() > 0);
	}

	@Test
	public final void gTestAddSchedulePhase() {
		SchedulePhase s = new SchedulePhase(
				getSchedulePhaseService().getSequence(Constants.STUDY_SCHEDULE_PHASE_SEQUENCE_NAME), SCHEDULEID,
				PHASEID);
		assertNotNull(getSchedulePhaseService().addSchedulePhase(s));
	}

	@Test
	public final void hTestSaveOrUpdate() {
		SchedulePhase s = getLastInserted();
		s.setphaseId(193630);
		getSchedulePhaseService().saveOrUpdate(s);
		assertTrue(getSchedulePhaseService().get(s.getId()).getPhaseId() == 193630);
	}

	@Test
	public final void iTestDelete() {
		int before = getSchedulePhaseService().getCount();
		getSchedulePhaseService().delete(getSchedulePhaseService().map(getLastInserted()));
		assertTrue(before - getSchedulePhaseService().getCount() == 1);
	}

	private SchedulePhase getLastInserted() {
		List<SchedulePhase> list = getSchedulePhaseService().list();
		Collections.sort(list, new Comparator<SchedulePhase>() {
			public int compare(SchedulePhase s1, SchedulePhase s2) {
				return Integer.compare(s2.getId(), s1.getId());
			}
		});
		SchedulePhase a = list.get(0);
		return a;
	}

}
