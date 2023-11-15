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
import com.idorsia.research.spirit.core.dto.SubGroupPatternDto;
import com.idorsia.research.spirit.core.model.SubGroupPattern;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SubGroupPatternTest extends AbstractSpiritTest {

	private static final int SUBGROUPPATTENID = 119260;
	private static final int ACTIONPATTERNID = 4880;
	private static final int SUBGROUPID = 16580;

	@Test
	public final void aTestGet() {
		assertNotNull(getSubGroupPatternService().get(SUBGROUPPATTENID));
	}

	@Test
	public final void bTestDozerMapping() {
		SubGroupPatternDto sDto = getDozerMapper().map(getSubGroupPatternService().get(SUBGROUPPATTENID),
				SubGroupPatternDto.class, "subgroupPatternCustomMapping");
		assertNotNull(sDto.getActionpattern());
		assertNotNull(sDto.getSubgroup());
		SubGroupPattern s = getDozerMapper().map(sDto, SubGroupPattern.class, "subgroupPatternCustomMapping");
		assertTrue(s.getActionpatternId().equals(sDto.getActionpattern().getId()));
		assertTrue(s.getSubgroupId().equals(sDto.getSubgroup().getId()));
	}

	@Test
	public final void cTestGetByAcionPattern() {
		assertNotNull(getSubGroupPatternService().getByAcionPattern(ACTIONPATTERNID));
	}

	@Test
	public final void dTestList() {
		assertTrue(!CollectionUtils.isEmpty(getSubGroupPatternService().list()));
	}

	@Test
	public final void eTestGetBySubgroup() {
		assertTrue(!CollectionUtils.isEmpty(getSubGroupPatternService().getBySubgroup(SUBGROUPID)));
	}

	@Test
	public final void fTestGetCount() {
		assertTrue(getSubGroupPatternService().getCount() > 0);
	}

	@Test
	public final void gTestAddSubGroupPattern() {
		SubGroupPattern s = new SubGroupPattern(
				getSubGroupPatternService().getSequence(Constants.SUBGROUP_PATTERN_SEQUENCE_NAME), ACTIONPATTERNID,
				SUBGROUPID);
		assertNotNull(getSubGroupPatternService().addSubGroupPattern(s));
	}

	@Test
	public final void hTestSaveOrUpdate() {
		SubGroupPattern s = getLastInserted();
		s.setCreUser("benmeka1");
		getSubGroupPatternService().saveOrUpdate(s);
		assertTrue(getSubGroupPatternService().get(s.getId()).getCreUser().equals("benmeka1"));
	}

	@Test
	public final void iTestGetByActionPatternAndSubGroup() {
		assertNotNull(getSubGroupPatternService().getByActionPatternAndSubGroup(ACTIONPATTERNID, SUBGROUPID));
	}

	@Test
	public final void jTestDelete() {
		int before = getSubGroupPatternService().getCount();
		getSubGroupPatternService().delete(getSubGroupPatternService().map(getLastInserted()));
		assertTrue(before - getSubGroupPatternService().getCount() == 1);
	}

	private SubGroupPattern getLastInserted() {
		List<SubGroupPattern> list = getSubGroupPatternService().list();
		Collections.sort(list, new Comparator<SubGroupPattern>() {
			public int compare(SubGroupPattern s1, SubGroupPattern s2) {
				return Integer.compare(s2.getId(), s1.getId());
			}
		});
		SubGroupPattern a = list.get(0);
		return a;
	}

}
