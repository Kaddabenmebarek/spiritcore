package com.idorsia.research.spirit;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.model.BiosampleEnclosure;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class BiosampleEnclosureTest extends AbstractSpiritTest {

	private static final int BIOSAMPLENCLOSUREID = 38630;
	private static final int BIOSAMPLEID = 1181805;
	private static final int ENCLOSUREID = 17860;
	private static final int PHASEINID = 193490;
	
	@Test
	public final void aTestGet() {
		assertNotNull(getBiosampleEnclosureService().get(BIOSAMPLENCLOSUREID));
	}

	@Test
	public final void bTestGetByBiosample() {
		assertNotNull(getBiosampleEnclosureService().getByBiosample(BIOSAMPLEID));
	}

	@Test
	public final void cTestList() {
		assertTrue(!getBiosampleEnclosureService().list().isEmpty());
	}

	@Test
	public final void eTestGetByEnclosure() {
		assertTrue(!getBiosampleEnclosureService().getByEnclosure(ENCLOSUREID).isEmpty());
	}

	@Test
	public final void fTestGetByPhaseIn() {
		assertTrue(!getBiosampleEnclosureService().getByPhaseIn(PHASEINID).isEmpty());
	}

	@Test
	public final void gTestGetCount() {
		assertTrue(getBiosampleEnclosureService().getCount()>0);
	}
	
	@Test
	public final void hTestAddBiosampleEnclosure() {
		BiosampleEnclosure b = new BiosampleEnclosure(
				getBiosampleEnclosureService().getSequence(Constants.BIOSAMPLE_ENCLOSURE_LINK_SEQ_NAME), BIOSAMPLEID,
				ENCLOSUREID, PHASEINID);
		assertNotNull(getBiosampleEnclosureService().addBiosampleEnclosure(b));
	}

	@Test
	public final void iTestSaveOrUpdate() {
		BiosampleEnclosure b = getLastInserted();
		b.setCreUser("benmeka1");
		getBiosampleEnclosureService().saveOrUpdate(b);
		assertTrue(getBiosampleEnclosureService().get(b.getId()).getCreUser().equals("benmeka1"));
	}

	@Test
	public final void jTestDelete() {
		int before = getBiosampleEnclosureService().getCount();
		getBiosampleEnclosureService().delete(getBiosampleEnclosureService().map(getLastInserted()));
		assertTrue(before - getBiosampleEnclosureService().getCount() == 1);
	}

	private BiosampleEnclosure getLastInserted() {
		List<BiosampleEnclosure> list = getBiosampleEnclosureService().list();
		Collections.sort(list, new Comparator<BiosampleEnclosure>() {
			public int compare(BiosampleEnclosure b1, BiosampleEnclosure b2) {
				return Integer.compare(b2.getId(), b1.getId());
			}
		});
		BiosampleEnclosure a = list.get(0); // last inserted
		return a;
	}
	
}
