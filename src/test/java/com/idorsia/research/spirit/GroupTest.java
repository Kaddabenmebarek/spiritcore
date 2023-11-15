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
import com.idorsia.research.spirit.core.dto.GroupDto;
import com.idorsia.research.spirit.core.model.Group;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class GroupTest extends AbstractSpiritTest {

	private static final Integer GROUPID =  12607;
	
	@Test
	public void aTestGet() {
		assertNotNull(getGroupService().get(GROUPID));
	}

	@Test
	public void bTestDozerConversion() {
		GroupDto gDto = getDozerMapper().map(getGroupService().get(GROUPID), GroupDto.class, "groupCustomMapping");
		assertNotNull(gDto.getStage());
		Group g = getDozerMapper().map(gDto, Group.class, "groupCustomMapping");
		assertTrue(g.getStageId().equals(gDto.getStage().getId()));
	}
	
	@Test
	public void cTestList() {
		assertTrue(!CollectionUtils.isEmpty(getGroupService().list()));
	}

	@Test
	public void dTestGetCount() {
		assertTrue(getGroupService().getCount()>0);
	}
	
	@Test
	public void eTestAddGroup() {
		Group g = new Group(getGroupService().getSequence(Constants.GROUP_SEQUENCE_NAME), "TestGroup");
		assertNotNull(getGroupService().addGroup(g));
	}

	@Test
	public void fTestSaveOrUpdate() {
		Group g = getLastInserted();
		g.setSeverity(2);
		getGroupService().saveOrUpdate(g);
		assertTrue(getGroupService().get(g.getId()).getSeverity().equals(2));
	}

	@Test
	public void gTestDelete() {
		int before = getGroupService().getCount();
		try {
			getGroupService().delete(getGroupService().map(getLastInserted()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertTrue(before - getGroupService().getCount() == 1);
	}
	
	private Group getLastInserted() {
		List<Group> list = getGroupService().list();
		Collections.sort(list, new Comparator<Group>() {
			public int compare(Group s1, Group s2) {
				return Integer.compare(s2.getId(), s1.getId());
			}
		});
		Group a = list.get(0);
		return a;
	}
}
