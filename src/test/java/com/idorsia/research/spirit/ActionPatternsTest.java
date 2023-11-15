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
import com.idorsia.research.spirit.core.dto.ActionPatternsDto;
import com.idorsia.research.spirit.core.model.ActionPatterns;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ActionPatternsTest extends AbstractSpiritTest {

	private static final Integer ACTIONPATTERNS_ID = 91090;

	@Test
	public void aTestGet() {
		ActionPatterns ap = getActionPatternsService().get(ACTIONPATTERNS_ID);
		assertNotNull(ap);
		assertTrue(ap.getId().equals(ACTIONPATTERNS_ID));
	}

	@Test
	public void bTestDozerConversion() {
		ActionPatternsDto aDto = getDozerMapper().map(getActionPatternsService().get(ACTIONPATTERNS_ID), ActionPatternsDto.class,"actionPatternsCustomMapping");
		assertNotNull(aDto.getStage());
		assertNotNull(aDto.getSchedule());
		ActionPatterns a = getDozerMapper().map(aDto, ActionPatterns.class,"actionPatternsCustomMapping");
		assertTrue(a.getStageId().equals(aDto.getStage().getId()));
		assertTrue(a.getScheduleId().equals(aDto.getSchedule().getId()));
	}
	
	@Test
	public void cTestList() {
		assertNotNull(getActionPatternsService().list());
		assertTrue(!CollectionUtils.isEmpty(getActionPatternsService().list()));
	}

	@Test
	public void dTestGetCount() {
		assertTrue(getActionPatternsService().getCount() > 0);
	}

	@Test
	public void eTestAddPhase() {
		ActionPatterns ap = new ActionPatterns(
				getActionPatternsService().getSequence(Constants.STUDY_ACTIONPATTERNS_SEQUENCE_NAME), 13670, "MEASUREMENT", 90870);
		assertNotNull(getActionPatternsService().addActionPatterns(ap));
	}

	@Test
	public void fTestSaveOrUpdate() {
		ActionPatterns ap = getLastInserted();
		ap.setActionParameters("CPP");
		getActionPatternsService().saveOrUpdate(ap);		
		assertTrue(getActionPatternsService().get(ap.getId()).getActionParameters().equals("CPP"));
	}

	@Test
	public void gTestDelete() {
		int before = getActionPatternsService().getCount();
		getActionPatternsService().delete(getActionPatternsService().map(getLastInserted()));
		assertTrue(before - getActionPatternsService().getCount() == 1);
	}
	
	private ActionPatterns getLastInserted() {
		List<ActionPatterns> list = getActionPatternsService().list();
		Collections.sort(list, new Comparator<ActionPatterns>() {
			public int compare(ActionPatterns s1, ActionPatterns s2) {
				return Integer.compare(s2.getId(), s1.getId());
			}
		});
		ActionPatterns a = list.get(0);
		return a;
	}

}
