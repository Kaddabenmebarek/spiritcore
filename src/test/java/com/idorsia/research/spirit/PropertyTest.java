package com.idorsia.research.spirit;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.springframework.util.CollectionUtils;

import com.idorsia.research.spirit.core.model.Property;

public class PropertyTest extends AbstractSpiritTest {
	
	private static final int PROPERTYID = 1;

	@Test
	public void testGet() {
		Property property = getPropertyService().get(PROPERTYID);
		assertNotNull(property);
		assertTrue(property.getId() == PROPERTYID);
	}
	
	@Test
	public void testList() {
		List<Property> propertys = getPropertyService().list();
		assertNotNull(propertys);
		assertTrue(!CollectionUtils.isEmpty(propertys));
	}

	@Test
	public void testGetCount() {
		int nbPropertys = getPropertyService().getCount();
		assertTrue(nbPropertys > 0);
	}
	
	@Test
	public void testGetByName() {
		Property p = getPropertyService().getPropertyByName("TYPE");
		assertTrue(p.getId()==PROPERTYID);
	}
	

}
