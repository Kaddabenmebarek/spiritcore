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
import com.idorsia.research.spirit.core.model.NamedTreatment;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class NamedTreatmentTest extends AbstractSpiritTest {

	private static final Integer NAMEDTREATMENTID = 7600;
	
	@Test
	public void aTestGet() {
		assertNotNull(getNamedTreatmentService().get(NAMEDTREATMENTID));
	}

	@Test
	public void bTestDozerConversion() {
		//TODO
	}
	
	@Test
	public void cTestList() {
		assertTrue(!CollectionUtils.isEmpty(getNamedTreatmentService().list()));
	}

	@Test
	public void dTestGetCount() {
		assertTrue(getNamedTreatmentService().getCount()>0);
	}
	
	@Test
	public void eTestAddNamedTreatment() {
		NamedTreatment t = new NamedTreatment();
		t.setId(getNamedSamplingService().getSequence(Constants.STUDY_NAMEDTREATMENT_SEQ_NAME));
		t.setColor(123456);
		assertNotNull(getNamedTreatmentService().addNamedTreatment(t));
	}

	@Test
	public void fTestSaveOrUpdate() {
		NamedTreatment t = getLastInserted();
		t.setComments("LoremIpsum");
		getNamedTreatmentService().saveOrUpdate(t);
		assertTrue(getNamedTreatmentService().get(t.getId()).getComments().equals("LoremIpsum"));
	}

	@Test
	public void gTestDelete() {
		int before = getNamedTreatmentService().getCount();
		getNamedTreatmentService().delete(getNamedTreatmentService().map(getLastInserted()));
		assertTrue(before - getNamedTreatmentService().getCount() == 1);	
	}

	private NamedTreatment getLastInserted() {
		List<NamedTreatment> list = getNamedTreatmentService().list();
		Collections.sort(list, new Comparator<NamedTreatment>() {
			public int compare(NamedTreatment s1, NamedTreatment s2) {
				return Integer.compare(s2.getId(), s1.getId());
			}
		});
		NamedTreatment a = list.get(0);
		return a;
	}
	
}
