package com.idorsia.research.spirit;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.dto.BiotypeMetadataValueDto;
import com.idorsia.research.spirit.core.model.BiotypeMetadataValue;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class BiotypeMetadataValueTest extends AbstractSpiritTest {

	private static final int BIOTYPEMETADATAVALUEID = 2602;
	private static int newId=0;

	@Test
	public void atestGet() {
		BiotypeMetadataValue s = getBiotypeMetadataValueService().get(BIOTYPEMETADATAVALUEID);
		assertNotNull(s);
		assertTrue(s.getId() == BIOTYPEMETADATAVALUEID);
	}
	
	@Test
	public void atestDozerConversion() {
		BiotypeMetadataValueDto aDto = getDozerMapper().map(getBiotypeMetadataValueService().get(BIOTYPEMETADATAVALUEID), BiotypeMetadataValueDto.class,"biotypeMetadataValueCustomMapping");
		assertNotNull(aDto.getBiotypeMetadata());
		BiotypeMetadataValue a = getDozerMapper().map(aDto, BiotypeMetadataValue.class,"biotypeMetadataValueCustomMapping");
		assertTrue(a.getBiotypemetadataId().equals(aDto.getBiotypeMetadata().getId()));
	}
	
	@Test
	public void btestAddBiotypeMetadataValue() {
		BiotypeMetadataValue a = new BiotypeMetadataValue();
		a.setBiotypemetadataId(52);
		a.setValue("TESSSSST");
		a.setId(getBiotypeMetadataValueService().getSequence(Constants.BIOTYPE_METADATA_VALUE_SEQUENCE_NAME));
		a.setCredate(new Date());
		a.setCreuser("test");
		a.setUpddate(new Date());
		a.setUpduser("test");
		getBiotypeMetadataValueService().addBiotypeMetadataValue(a);
		newId=a.getId();
		assertTrue(newId!=0);
	}
	
	@Test
	public void ctestGetByBiotypeMetadata() {
		List<BiotypeMetadataValue> s = getBiotypeMetadataValueService().getByBiotypeMetadata(52);
		assertTrue(s.size()!=0);
	}
	
	@Test
	public void ctestGetByStage() {
		List<BiotypeMetadataValue> s = getBiotypeMetadataValueService().getByStage(1893);
		assertTrue(s.size()!=0);
	}
	
	@Test
	public void ctestGetByGroup() {
		List<BiotypeMetadataValue> s = getBiotypeMetadataValueService().getByGroup(26726);
		assertTrue(s.size()!=0);
	}
	
	@Test
	public void ctestGetBySubGroup() {
		List<BiotypeMetadataValue> s = getBiotypeMetadataValueService().getBySubGroup(12769);
		assertTrue(s.size()!=0);
	}
	
	@Test
	public void gtestSaveOrUpdate() {
		BiotypeMetadataValue a = getBiotypeMetadataValueService().get(newId);
		assertNotNull(a);
		a.setValue("Test");
		getBiotypeMetadataValueService().saveOrUpdate(a);
		assertTrue("Test".equals(getBiotypeMetadataValueService().get(newId).getValue()));
	}
	
	@Test
	public void htestDelete() {
		BiotypeMetadataValue s = getBiotypeMetadataValueService().get(newId);
		getBiotypeMetadataValueService().delete(getBiotypeMetadataValueService().map(s));
		assertNull(getBiotypeMetadataValueService().getById(newId));
	}
}
