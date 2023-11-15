package com.idorsia.research.spirit;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.dto.GroupPatternDto;
import com.idorsia.research.spirit.core.model.GroupPattern;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class GroupPatternTest extends AbstractSpiritTest{

	private static final int GROUPPATTENID = 460;
	
	@Test
	public void aTestGet() {
		assertNotNull(getGroupPatternService().get(GROUPPATTENID));
	}

	@Test
	public void bTestDozerConversion() {
		GroupPatternDto gDto = getDozerMapper().map(getGroupPatternService().get(GROUPPATTENID), GroupPatternDto.class,"groupPatternCustomMapping");
		assertNotNull(gDto.getActionpattern());
		assertNotNull(gDto.getGroup());
		GroupPattern g = getDozerMapper().map(gDto, GroupPattern.class,"groupPatternCustomMapping");
		assertTrue(g.getActionpatternId().equals(gDto.getActionpattern().getId()));
		assertTrue(g.getGroupId().equals(gDto.getGroup().getId()));
	}
	
	@Test
	public void cTestList() {
		assertTrue(!getGroupPatternService().list().isEmpty());
	}

	@Test
	public void dTestGetCount() {
		assertTrue(getGroupPatternService().getCount()>0);
	}
	
	@Test
	public void eTestAddGroupPattern() {
		GroupPattern g = new GroupPattern();
		g.setId(getGroupPatternService().getSequence(Constants.GROUP_PATTERN_SEQUENCE_NAME));
		g.setGroupId(30900);
		g.setActionpatternId(271610);
		assertNotNull(getGroupPatternService().addGroupPattern(g));
	}

	@Test
	public void fTestSaveOrUpdate() {
		GroupPattern g = getGroupPatternService().getByActionPatternAndGroup(271610, 30900);
		g.setCreUser("benmeka1");
		getGroupPatternService().saveOrUpdate(g);
		assertTrue(getGroupPatternService().getByActionPatternAndGroup(271610, 30900).getCreUser().equals("benmeka1"));
	}

	@Test
	public void gTestDelete() {
		int before = getGroupPatternService().getCount();
		GroupPattern g = getGroupPatternService().getByActionPatternAndGroup(271610, 30900);
		getGroupPatternService().delete(getGroupPatternService().map(g));
		assertTrue(before - getGroupPatternService().getCount() == 1);
	}
	


}
