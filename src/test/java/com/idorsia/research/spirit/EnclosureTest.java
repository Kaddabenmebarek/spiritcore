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
import com.idorsia.research.spirit.core.dto.EnclosureDto;
import com.idorsia.research.spirit.core.model.Enclosure;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class EnclosureTest extends AbstractSpiritTest {

	private static final Integer ENCLOSURE_ID = 1410;

	@Test
	public void aTestGet() {
		Enclosure e = getEnclosureService().get(ENCLOSURE_ID);
		assertNotNull(e);
	}
	
	@Test
	public void bTestDozerConversion() {
		EnclosureDto eDto = getDozerMapper().map(getEnclosureService().get(ENCLOSURE_ID), EnclosureDto.class,"enclosureCustomMapping");
		assertNotNull(eDto.getStudy());
		Enclosure e = getDozerMapper().map(eDto, Enclosure.class,"enclosureCustomMapping");
		assertTrue(e.getStudyId().equals(eDto.getStudy().getId()));
	}

	@Test
	public void cTestList() {
		assertTrue(!CollectionUtils.isEmpty(getEnclosureService().list()));
	}

	@Test
	public void dTestGetCount() {
		assertTrue(getEnclosureService().getCount()>0);
	}

	@Test
	public void eTestSaveOrUpdate() {
		Enclosure e = new Enclosure(getEnclosureService().getSequence(Constants.STUDY_ENCLOSURE_SEQUENCE_NAME), 9124 ,"S-123-45");
		assertNotNull(getEnclosureService().addEnclosure(e));
	}

	@Test
	public void fTestAddEnclosure() {
		Enclosure e = getLastInserted();
		e.setName("S012-34");
		getEnclosureService().saveOrUpdate(e);
		assertTrue(getEnclosureService().get(e.getId()).getName().equals("S012-34"));
	}

	@Test
	public void gTestDelete() {
		int before = getEnclosureService().getCount();
		getEnclosureService().delete(getEnclosureService().map(getLastInserted()));
		assertTrue(before - getEnclosureService().getCount() == 1);
	}
	
	private Enclosure getLastInserted() {
		List<Enclosure> list = getEnclosureService().list();
		Collections.sort(list, new Comparator<Enclosure>() {
			public int compare(Enclosure s1, Enclosure s2) {
				return Integer.compare(s2.getId(), s1.getId());
			}
		});
		Enclosure a = list.get(0);
		return a;
	}

}
