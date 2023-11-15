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
import com.idorsia.research.spirit.core.dto.SamplingDto;
import com.idorsia.research.spirit.core.model.Sampling;
import com.idorsia.research.spirit.core.util.DataUtils;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SamplingTest extends AbstractSpiritTest {

	private static final Integer SAMPLINGID = 12615;

	@Test
	public void aTestGet() {
		assertNotNull(getSamplingService().get(SAMPLINGID));
	}

	@Test
	public void bTestDozerConversion() {
		SamplingDto sDto = getDozerMapper().map(getSamplingService().get(4030), SamplingDto.class, "samplingCustomMapping");
		assertNotNull(sDto.getBiotype());
		assertNotNull(sDto.getParentSampling());
		assertNotNull(sDto.getNamedSampling());
		Sampling s = getDozerMapper().map(sDto, Sampling.class, "samplingCustomMapping");
		assertTrue(s.getBiotypeId().equals(sDto.getBiotype().getId()));
		assertTrue(s.getParentSamplingId().equals(sDto.getParentSampling().getId()));
		assertTrue(s.getNamedsamplingId().equals(sDto.getNamedSampling().getId()));
	}

	@Test
	public void cTestList() {
		assertTrue(!CollectionUtils.isEmpty(getSamplingService().list()));
	}

	@Test
	public void dTestGetCount() {
		assertTrue(getSamplingService().getCount() > 0);
	}

	@Test
	public void eTestAddSampling() {
		Sampling s = new Sampling(getSamplingService().getSequence(Constants.SAMPLING_SEQUENCE_NAME), 50,
				DataUtils.booleanToInt(Boolean.FALSE), DataUtils.booleanToInt(Boolean.FALSE),
				DataUtils.booleanToInt(Boolean.FALSE));
		assertNotNull(getSamplingService().addSampling(s));
	}

	@Test
	public void fTestSaveOrUpdate() {
		Sampling s = getLastInserted();
		s.setContainerType("BOTTLE");
		getSamplingService().saveOrUpdate(s);
		assertTrue(getSamplingService().get(s.getId()).getContainerType().equals("BOTTLE"));
	}

	@Test
	public void gTestDelete() {
		int before = getSamplingService().getCount();
		try {
			getSamplingService().delete(getSamplingService().map(getLastInserted()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertTrue(before - getSamplingService().getCount() == 1);
	}
	
	private Sampling getLastInserted() {
		List<Sampling> list = getSamplingService().list();
		Collections.sort(list, new Comparator<Sampling>() {
			public int compare(Sampling s1, Sampling s2) {
				return Integer.compare(s2.getId(), s1.getId());
			}
		});
		Sampling a = list.get(0);
		return a;
	}

}
