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
import com.idorsia.research.spirit.core.dto.NamedSamplingDto;
import com.idorsia.research.spirit.core.model.NamedSampling;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class NamedSamplingTest extends AbstractSpiritTest {

	private static final Integer NAMEDSAMPLINGID = 4140;
	
	@Test
	public void aTestGet() {
		assertNotNull(getNamedSamplingService().get(NAMEDSAMPLINGID));
	}

	@Test
	public void bTestDozerConversion() {
		NamedSamplingDto nsDto = getDozerMapper().map(getNamedSamplingService().get(NAMEDSAMPLINGID), NamedSamplingDto.class, "namedSamplingCustomMapping");
		assertNotNull(nsDto.getStudy());
		NamedSampling ns = getDozerMapper().map(nsDto, NamedSampling.class, "namedSamplingCustomMapping");
		assertTrue(ns.getStudyId().equals(nsDto.getStudy().getId()));
	}	
	
	@Test
	public void cTestList() {
		assertTrue(!CollectionUtils.isEmpty(getNamedSamplingService().list()));
	}

	@Test
	public void dTestGetCount() {
		assertTrue(getNamedSamplingService().getCount()>0);
	}
	
	@Test
	public void eTestAddNamedSampling() {
		NamedSampling ns = new NamedSampling(getNamedSamplingService().getSequence(Constants.STUDY_SAMPLING_SEQUENCE_NAME), "PlasmaTest");
		assertNotNull(getNamedSamplingService().addNamedSampling(ns));
	}

	@Test
	public void fTestSaveOrUpdate() {
		NamedSampling ns = getLastInserted();
		ns.setColor(12345);
		getNamedSamplingService().saveOrUpdate(ns);
		assertTrue(getNamedSamplingService().get(ns.getId()).getColor().equals(12345));
	}

	@Test
	public void gTestDelete() {
		int before = getNamedSamplingService().getCount();
		try {
			getNamedSamplingService().delete(getNamedSamplingService().map(getLastInserted()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertTrue(before - getNamedSamplingService().getCount() == 1);
	}
	
	private NamedSampling getLastInserted() {
		List<NamedSampling> list = getNamedSamplingService().list();
		Collections.sort(list, new Comparator<NamedSampling>() {
			public int compare(NamedSampling s1, NamedSampling s2) {
				return Integer.compare(s2.getId(), s1.getId());
			}
		});
		NamedSampling a = list.get(0);
		return a;
	}

}
