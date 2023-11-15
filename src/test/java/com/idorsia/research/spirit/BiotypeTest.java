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
import com.idorsia.research.spirit.core.model.Biotype;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class BiotypeTest extends AbstractSpiritTest {

	
	private static final Integer BIOTYPEID = 507;
	
	@Test
	public void aTestGet() {
		assertNotNull(getBiotypeService().get(BIOTYPEID));
	}

	@Test
	public void bTestList() {
		assertTrue(!CollectionUtils.isEmpty(getBiotypeService().list()));
	}

	@Test
	public void cTestGetCount() {
		assertTrue(getBiotypeService().getCount() > 0);
	}
	
	@Test
	public void dTestAddBiotype() {
		Biotype b = new Biotype(getBiotypeService().getSequence(Constants.BIOTYPE_SEQUENCE_NAME), 0, "PURIFIED");
		assertNotNull(getBiotypeService().addBiotype(b));
	}

	@Test
	public void eTestSaveOrUpdate() {
		Biotype b = getLastInserted();
		b.setDescription("Purified Protein");
		getBiotypeService().saveOrUpdate(b);
		assertTrue(getBiotypeService().get(b.getId()).getDescription().equals("Purified Protein"));
	}

	private Biotype getLastInserted() {
		List<Biotype> list = getBiotypeService().list();
		Collections.sort(list, new Comparator<Biotype>() {
			public int compare(Biotype s1, Biotype s2) {
				return Integer.compare(s2.getId(), s1.getId());
			}
		});
		Biotype a = list.get(0);
		return a;
	}
	
}
