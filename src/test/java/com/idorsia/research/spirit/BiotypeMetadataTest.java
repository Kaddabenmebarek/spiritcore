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
import com.idorsia.research.spirit.core.dto.BiotypeMetadataDto;
import com.idorsia.research.spirit.core.model.BiotypeMetadata;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class BiotypeMetadataTest extends AbstractSpiritTest {

	private static final int BIOTYPEMETADATAID = 1760;
	private static final int BIOTYPEID = 50;

	@Test
	public final void aTestGet() {
		assertNotNull(getBiotypeMetadataService().get(BIOTYPEMETADATAID));
	}
	
	@Test
	public void aaTestDozerConversion() {
		BiotypeMetadataDto bDto = getDozerMapper().map(getBiotypeMetadataService().get(BIOTYPEMETADATAID),
				BiotypeMetadataDto.class, "biotyMetataDataCustomMapping");
		assertNotNull(bDto.getBiotype());
		BiotypeMetadata e = getDozerMapper().map(bDto, BiotypeMetadata.class, "biotyMetataDataCustomMapping");
		assertTrue(e.getBiotypeId().equals(bDto.getBiotype().getId()));
	}	

	@Test
	public final void bTestGetByBiotype() {
		assertTrue(!CollectionUtils.isEmpty(getBiotypeMetadataService().getByBiotype(BIOTYPEID)));
	}

	@Test
	public final void cTestList() {
		assertTrue(!getBiotypeMetadataService().list().isEmpty());
	}

	@Test
	public final void dTestGetCount() {
		assertTrue(getBiotypeMetadataService().getCount() > 0);
	}
	
	@Test
	public final void eTestAddBiotypeMetadata() {
		BiotypeMetadata b = new BiotypeMetadata(
				getBiotypeMetadataService().getSequence(Constants.BIOTYPE_METADATA_SEQUENCE_NAME), 1550, "TestName",
				0, BIOTYPEID);
		assertNotNull(getBiotypeMetadataService().addBiotypeMetadata(b));
	}

	@Test
	public final void fTestSaveOrUpdate() {
		BiotypeMetadata b = getLastInserted();
		b.setDescription("LoremIpsum");
		getBiotypeMetadataService().saveOrUpdate(b);
		assertTrue(getBiotypeMetadataService().get(b.getId()).getDescription().equals("LoremIpsum"));
	}

	@Test
	public final void gTestDelete() {
		int before = getBiotypeMetadataService().getCount();
		getBiotypeMetadataService().delete(getBiotypeMetadataService().map(getLastInserted()));
		assertTrue(before - getBiotypeMetadataService().getCount() == 1);
	}

	
	private BiotypeMetadata getLastInserted() {
		List<BiotypeMetadata> list = getBiotypeMetadataService().list();
		Collections.sort(list, new Comparator<BiotypeMetadata>() {
			public int compare(BiotypeMetadata b1, BiotypeMetadata b2) {
				return Integer.compare(b2.getId(), b1.getId());
			}
		});
		BiotypeMetadata b = list.get(0); // last inserted
		return b;
	}
}
