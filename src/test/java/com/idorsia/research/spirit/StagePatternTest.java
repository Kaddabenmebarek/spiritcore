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
import com.idorsia.research.spirit.core.dto.StagePatternDto;
import com.idorsia.research.spirit.core.model.StagePattern;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class StagePatternTest extends AbstractSpiritTest {

	private static final int STAGEPATTERNID = 50;
	private static final int ACTIONPATTERNID = 243800;
	private static final int STAGEID = 1890;
	
	@Test
	public final void aTestGet() {
		assertNotNull(getStagePatternService().get(STAGEPATTERNID));
	}

	@Test
	public final void bTestDozerMapping() {
		StagePatternDto sDto = getDozerMapper().map(getStagePatternService().get(STAGEPATTERNID),
				StagePatternDto.class, "stagePatternCustomMapping");
		assertNotNull(sDto.getActionpattern());
		assertNotNull(sDto.getStage());
		StagePattern s = getDozerMapper().map(sDto, StagePattern.class,
				"stagePatternCustomMapping");
		assertTrue(s.getActionpatternId().equals(sDto.getActionpattern().getId()));
		assertTrue(s.getStageId().equals(sDto.getStage().getId()));
	}
	
	@Test
	public final void cTestGetByActionPattern() {
		assertNotNull(getStagePatternService().getByActionPattern(ACTIONPATTERNID));
	}

	@Test
	public final void dTestGetByStage() {
		assertTrue(!CollectionUtils.isEmpty(getStagePatternService().getByStage(STAGEID)));
	}

	@Test
	public final void eTestList() {
		assertTrue(!CollectionUtils.isEmpty(getStagePatternService().list()));
	}

	@Test
	public final void fTestGetCount() {
		assertTrue(getStagePatternService().getCount()>0);;
	}
	
	@Test
	public final void gTestAddStagePattern() {
		StagePattern s = new StagePattern(getStagePatternService().getSequence(Constants.STAGE_PATTERN_SEQUENCE_NAME),
				ACTIONPATTERNID, STAGEPATTERNID);
		assertNotNull(getStagePatternService().addStagePattern(s));
	}

	@Test
	public final void hTestSaveOrUpdate() {
		StagePattern s = getLastInserted();
		s.setCreUser("benmeka1");
		getStagePatternService().saveOrUpdate(s);
		assertTrue(getStagePatternService().get(s.getId()).getCreUser().equals("benmeka1"));
	}

	@Test
	public final void iTestDelete() {
		int before = getStagePatternService().getCount();
		getStagePatternService().delete(getStagePatternService().map(getLastInserted()));
		assertTrue(before - getStagePatternService().getCount() == 1);
	}

	private StagePattern getLastInserted() {
		List<StagePattern> list = getStagePatternService().list();
		Collections.sort(list, new Comparator<StagePattern>() {
			public int compare(StagePattern s1, StagePattern s2) {
				return Integer.compare(s2.getId(), s1.getId());
			}
		});
		StagePattern a = list.get(0);
		return a;
	}
	
}
