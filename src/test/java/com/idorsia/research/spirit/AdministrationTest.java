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
import com.idorsia.research.spirit.core.dto.AdministrationDto;
import com.idorsia.research.spirit.core.model.Administration;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AdministrationTest extends AbstractSpiritTest {

	private final static int ADMINISTRATIONID = 650;
	private final static int NAMEDTREATMENTID = 7575;
	private final static int BIOSAMPLEID = 38021;
	private final static int PHASEID = 4552;

	@Test
	public final void aTestGet() {
		assertNotNull(getAdministrationService().get(ADMINISTRATIONID));
	}

	@Test
	public void bTestDozerConversion() {
		AdministrationDto aDto = getDozerMapper().map(getAdministrationService().get(ADMINISTRATIONID), AdministrationDto.class,"administrationCustomMapping");
		assertNotNull(aDto.getNamedTreatment());
		assertNotNull(aDto.getBiosample());
		assertNotNull(aDto.getPhase());
		Administration a = getDozerMapper().map(aDto, Administration.class,"administrationCustomMapping");
		assertTrue(a.getNamedTreatmentId().equals(aDto.getNamedTreatment().getId()));
		assertTrue(a.getBiosampleId().equals(aDto.getBiosample().getId()));
		assertTrue(a.getPhaseId().equals(aDto.getPhase().getId()));
	}
	
	@Test
	public final void cTestList() {
		assertTrue(!CollectionUtils.isEmpty(getAdministrationService().list()));
	}

	@Test
	public final void dTestGetByNamedTreatment() {
		assertTrue(!CollectionUtils.isEmpty(getAdministrationService().getByNamedTreatment(NAMEDTREATMENTID)));
	}

	@Test
	public final void eTestGetByBiosample() {
		assertTrue(!CollectionUtils.isEmpty(getAdministrationService().getByBiosample(BIOSAMPLEID)));
	}

	@Test
	public final void fTestGetByPhase() {
		assertTrue(!CollectionUtils.isEmpty(getAdministrationService().getByPhase(PHASEID)));
	}

	@Test
	public final void gTestGetCount() {
		assertTrue(getAdministrationService().getCount() > 0);
	}

	@Test
	public final void hTestAddAdministration() {
		Administration a = new Administration();
		a.setId(getAdministrationService().getSequence(Constants.STUDY_ADMINISTRATION_SEQ_NAME));
		a.setNamedTreatmentId(NAMEDTREATMENTID);
		a.setBiosampleId(BIOSAMPLEID);
		a.setPhaseId(PHASEID);
		assertNotNull(getAdministrationService().addAdministration(a));
	}

	@Test
	public final void iTestSaveOrUpdate() {
		Administration a = getLastInserted();
		a.setCreUser("benmeka1");
		getAdministrationService().saveOrUpdate(a);
		assertTrue(getAdministrationService().get(a.getId()).getCreUser().equals("benmeka1"));
	}

	@Test
	public final void jTestDelete() {
		int before = getAdministrationService().getCount();
		Administration a = getLastInserted();
		getAdministrationService().delete(getAdministrationService().map(a));
		assertTrue(before - getAdministrationService().getCount() == 1);

	}

	private Administration getLastInserted() {
		List<Administration> list = getAdministrationService().list();
		Collections.sort(list, new Comparator<Administration>() {
			public int compare(Administration a1, Administration a2) {
				return Integer.compare(a2.getId(), a1.getId());
			}
		});
		Administration a = list.get(0); // last inserted
		return a;
	}

}
