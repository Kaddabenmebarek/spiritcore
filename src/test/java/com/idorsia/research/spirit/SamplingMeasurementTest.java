package com.idorsia.research.spirit;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.util.CollectionUtils;

import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.model.SamplingMeasurement;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SamplingMeasurementTest extends AbstractSpiritTest{
	
	private static int newId=0;

	@Test
	public void atestAddSamplingMeasurement() {
		SamplingMeasurement a = new SamplingMeasurement();
		a.setSamplingId(1079);
		a.setAssayId(13027);
		a.setId(getSamplingMeasurementService().getSequence(Constants.SAMPLING_MEASUREMENT_SEQUENCE_NAME));
		getSamplingMeasurementService().addSamplingMeasurement(a);
		newId=a.getId();
		assertTrue(newId!=0);
	}
	
	@Test
	public void btestGet() {
		SamplingMeasurement s = getSamplingMeasurementService().get(newId);
		assertNotNull(s);
		assertTrue(s.getId() == newId);
	}
	
	@Test
	public void btestGetSamplingMeasurementById() {
		SamplingMeasurement s = getSamplingMeasurementService().getSamplingMeasurementById(newId);
		assertNotNull(s);
		assertTrue(s.getId() == newId);
	}
	
	@Test
	public void ctestGetSamplingMeasurementsBySampling() {
		List<SamplingMeasurement> s = getSamplingMeasurementService().getSamplingMeasurementsBySampling(1079);
		assertTrue(s.size()>0);
	}
	
	@Test
	public void ctestGetSamplingMeasurementsByAssay() {
		List<SamplingMeasurement> s = getSamplingMeasurementService().getSamplingMeasurementsByAssay(13027);
		assertTrue(s.size()>0);
	}
	
	@Test
	public void ctestGetSamplingMeasurementsBySamplingAndAssay() {
		List<SamplingMeasurement> s = getSamplingMeasurementService().getSamplingMeasurementBySamplingAndAssay(1079,13027);
		assertTrue(s.size()>0);
	}
	
	@Test
	public void dtestList() {
		List<SamplingMeasurement> samplingMeasurements = getSamplingMeasurementService().list();
		assertNotNull(samplingMeasurements);
		assertTrue(!CollectionUtils.isEmpty(samplingMeasurements));
	}
	
	@Test
	public void etestGetCount() {
		int nbSamplingMeasurements = getSamplingMeasurementService().getCount();
		assertTrue(nbSamplingMeasurements> 0);
	}
	
	@Test
	public void gtestSaveOrUpdate() {
		SamplingMeasurement a = getSamplingMeasurementService().getSamplingMeasurementById(newId);
		a.setSamplingId(150);
		getSamplingMeasurementService().saveOrUpdate(a);
		assertTrue(150==getSamplingMeasurementService().getSamplingMeasurementById(newId).getSamplingId());
	}
	
	/*@Test
	public void htestDelete() {
		SamplingMeasurement s = getSamplingMeasurementService().getSamplingMeasurementById(newId);
		getSamplingMeasurementService().delete(s.getId());
		assertNull(getSamplingMeasurementService().getSamplingMeasurementById(newId));
	}*/
}
