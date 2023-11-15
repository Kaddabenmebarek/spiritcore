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

import com.idorsia.research.spirit.core.dto.SamplingParameterDto;
import com.idorsia.research.spirit.core.model.SamplingParameter;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SamplingParameterTest extends AbstractSpiritTest {

	private static final int SAMPLINGPARAMETERID = 30;
	private static final int BIOTYPEMETADATAID = 51;
	private static final int SAMPLINGID = 40980;
	
	@Test
	public final void aTestGet() {
		assertNotNull(getSamplingParameterService().get(SAMPLINGPARAMETERID));
	}

	@Test
	public final void bTestDozerMapping() {
		SamplingParameterDto sDto = getDozerMapper().map(getSamplingParameterService().get(SAMPLINGPARAMETERID),
				SamplingParameterDto.class, "samplingParameterCustomMapping");
		assertNotNull(sDto.getBiotypemetadata());
		assertNotNull(sDto.getSampling());
		SamplingParameter s = getDozerMapper().map(sDto, SamplingParameter.class,
				"samplingParameterCustomMapping");
		assertTrue(s.getBiotypemetadataId().equals(sDto.getBiotypemetadata().getId()));
		assertTrue(s.getSamplingId().equals(sDto.getSampling().getId()));
	}
	
	@Test
	public final void cTtestGetSamplingParameterByBiotypeMetadata() {
		assertTrue(!CollectionUtils.isEmpty(getSamplingParameterService().getSamplingParameterByBiotypeMetadata(BIOTYPEMETADATAID)));
	}

	@Test
	public final void dTestGetSamplingParameterBySampling() {
		assertNotNull(!CollectionUtils.isEmpty(getSamplingParameterService().getSamplingParameterBySampling(SAMPLINGID)));
	}

	@Test
	public final void eTestList() {
		assertTrue(!CollectionUtils.isEmpty(getSamplingParameterService().list()));
	}

	@Test
	public final void fTestGetCount() {
		assertTrue(getSamplingParameterService().getCount()>0);;
	}
	

	@Test
	public final void hTestSaveOrUpdate() {
		SamplingParameter s = getLastInserted();
		s.setValue("LoremIpsum");
		getSamplingParameterService().saveOrUpdate(s);
		assertTrue(getSamplingParameterService().get(s.getId()).getValue().equals("LoremIpsum"));
	}

	
	private SamplingParameter getLastInserted() {
		List<SamplingParameter> list = getSamplingParameterService().list();
		Collections.sort(list, new Comparator<SamplingParameter>() {
			public int compare(SamplingParameter s1, SamplingParameter s2) {
				return Integer.compare(s2.getId(), s1.getId());
			}
		});
		SamplingParameter a = list.get(0);
		return a;
	}

}
