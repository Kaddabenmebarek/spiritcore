package com.idorsia.research.spirit;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.util.CollectionUtils;

import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.model.PropertyLink;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PropertyLinkTest  extends AbstractSpiritTest{

	private static int newId=0;

	@Test
	public void atestAddPropertyLink() {
		PropertyLink a = new PropertyLink();
		a.setStudyId(50);
		a.setPropertyId(1);
		a.setValue("TEST");
		a.setId(getPropertyLinkService().getSequence(Constants.STUDY_PROPERTY_LINK_SEQUENCE_NAME));
		a.setCreDate(new Date());
		a.setCreUser("TEST");
		a.setUpdDate(new Date());
		a.setUpdUser("TEST");
		getPropertyLinkService().addPropertyLink(a);
		newId=a.getId();
		assertTrue(newId!=0);
	}
	
	@Test
	public void btestGet() {
		PropertyLink s = getPropertyLinkService().get(newId);
		assertNotNull(s);
		assertTrue(s.getId() == newId);
	}
	
	@Test
	public void btestGetPropertyLinkById() {
		PropertyLink s = getPropertyLinkService().getPropertyLinkById(newId);
		assertNotNull(s);
		assertTrue(s.getId() == newId);
	}
	
	@Test
	public void ctestGetPropertyLinksByStudy() {
		List<PropertyLink> s = getPropertyLinkService().getPropertyLinksByStudy(50);
		assertTrue(s.size()>0);
	}
	
	@Test
	public void ctestGetPropertyLinksByProperty() {
		List<PropertyLink> s = getPropertyLinkService().getPropertyLinksByProperty(1);
		assertTrue(s.size()>0);
	}
	
	@Test
	public void ctestGetPropertyLinksByStudyAndProperty() {
		PropertyLink s = getPropertyLinkService().getPropertyLinkByStudyAndProperty(50,1);
		assertTrue(s.getId().equals(newId));
	}

	@Test
	public void dtestList() {
		List<PropertyLink> propertyLinks = getPropertyLinkService().list();
		assertNotNull(propertyLinks);
		assertTrue(!CollectionUtils.isEmpty(propertyLinks));
	}

	@Test
	public void etestGetCount() {
		int nbPropertyLinks = getPropertyLinkService().getCount();
		assertTrue(nbPropertyLinks> 0);
	}
	
	@Test
	public void gtestSaveOrUpdate() {
		PropertyLink a = getPropertyLinkService().getPropertyLinkById(newId);
		a.setValue("Lorem ipsum dolor sit amet, consectetur adipiscing elit.");
		getPropertyLinkService().saveOrUpdate(a);
		assertTrue("Lorem ipsum dolor sit amet, consectetur adipiscing elit.".equals(getPropertyLinkService().getPropertyLinkById(newId).getValue()));
	}
	
	@Test
	public void htestDelete() {
		PropertyLink s = getPropertyLinkService().getPropertyLinkById(newId);
		getPropertyLinkService().delete(getPropertyLinkService().map(s));
		assertNull(getPropertyLinkService().getPropertyLinkById(newId));
	}
}
