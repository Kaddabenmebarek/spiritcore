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
import com.idorsia.research.spirit.core.dto.SubGroupDto;
import com.idorsia.research.spirit.core.model.SubGroup;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SubGroupTest extends AbstractSpiritTest {

	private static final Integer SUBGROUPID = 11430;
	
	@Test
	public void aTestGet() {
		assertNotNull(getSubGroupService().get(SUBGROUPID));
	}

	@Test
	public void bTestDozerConversion() {
		SubGroupDto sgDto = getDozerMapper().map(getSubGroupService().get(14000), SubGroupDto.class, "subGroupCustomMapping");
		assertNotNull(sgDto.getGroup());
		assertNotNull(sgDto.getRandofromgroup());
		assertNotNull(sgDto.getRandofromsubgroup());
		SubGroup sg = getDozerMapper().map(sgDto, SubGroup.class, "subGroupCustomMapping");
		assertTrue(sg.getGroupId().equals(sgDto.getGroup().getId()));
		assertTrue(sg.getRandofromgroupId().equals(sgDto.getRandofromgroup().getId()));
		assertTrue(sg.getRandofromsubgroupId().equals(sgDto.getRandofromsubgroup().getId()));
	}
	
	@Test
	public void cTestList() {
		assertTrue(!CollectionUtils.isEmpty(getSubGroupService().list()));
	}

	@Test
	public void dTestGetCount() {
		assertTrue(getSubGroupService().getCount()>0);
	}
	
	@Test
	public void eTestAddSubGroup() {
		SubGroup sg = new SubGroup(getSubGroupService().getSequence(Constants.STUDY_SUBGROUP_SEQUENCE_NAME), 27010);
		assertNotNull(getSubGroupService().addSubGroup(sg));
	}

	@Test
	public void fTestSaveOrUpdate() {
		SubGroup sg = getLastInserted();
		sg.setNo(8);
		getSubGroupService().saveOrUpdate(sg);
		assertTrue(getSubGroupService().get(sg.getId()).getNo().equals(8));
	}

	@Test
	public void gTestDelete() {
		int before = getSubGroupService().getCount();
		try {
			getSubGroupService().delete(getSubGroupService().map(getLastInserted()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertTrue(before - getSubGroupService().getCount() == 1);
	}
	
	private SubGroup getLastInserted() {
		List<SubGroup> list = getSubGroupService().list();
		Collections.sort(list, new Comparator<SubGroup>() {
			public int compare(SubGroup s1, SubGroup s2) {
				return Integer.compare(s2.getId(), s1.getId());
			}
		});
		SubGroup a = list.get(0);
		return a;
	}

}
