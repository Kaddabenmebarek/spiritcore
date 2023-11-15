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
import com.idorsia.research.spirit.core.dto.BiosampleDto;
import com.idorsia.research.spirit.core.model.Biosample;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class BiosampleTest extends AbstractSpiritTest {

	private static final Integer BIOSAMPLEID = 2050;

	@Test
	public void aTestGet() {
		Biosample b = getBiosampleService().get(BIOSAMPLEID);
		assertNotNull(b);
		assertTrue(BIOSAMPLEID.equals(b.getId()));
	}
	
	@Test
	public void bTestDozerConversion() {
		BiosampleDto bDto = getDozerMapper().map(getBiosampleService().get(514529), BiosampleDto.class,"biosampleCustomMapping");
		assertNotNull(bDto.getStudy());
		assertNotNull(bDto.getParent());
		assertNotNull(bDto.getInheritedPhase());
		Biosample b = getDozerMapper().map(bDto, Biosample.class,"biosampleCustomMapping");
		assertTrue(b.getStudyId().equals(bDto.getStudy().getId()));
		assertTrue(b.getParentId().equals(bDto.getParent().getId()));
		assertTrue(b.getInheritedphaseId().equals(bDto.getInheritedPhase().getId()));
	}
	
	@Test
	public void cTestGetStrain() {
		Integer b = getBiosampleService().getStrainId(getDozerMapper().map(getBiosampleService().get(514529), BiosampleDto.class,"biosampleCustomMapping"));
		assertNotNull(b);
	}

	/*@Test
	public void cTestList() {
		List<Biosample> biosamples = getBiosampleService().list();
		assertNotNull(biosamples);
		assertTrue(!CollectionUtils.isEmpty(biosamples));
	}

	@Test
	public void dTestGetCount() {
		assertTrue(getBiosampleService().getCount()>0);
	}*/

	@Test
	public void eTestAddBiosample() {
		Biosample b = new Biosample();
		b.setBiotypeId(58);
		b.setSampleId("SampleIdtest");
		b.setId(getBiosampleService().getSequence(Constants.BIOSAMPLE_SEQUENCE_NAME));
		assertNotNull(getBiosampleService().addBiosample(b));
	}
	
	@Test
	public void fTestGetBiosampleBySampleId() {
		assertNotNull(getBiosampleService().getBiosampleBySampleId("SampleIdtest"));
	}
	
	@Test
	public void fTestSaveOrUpdate() {
		Biosample b = getLastInserted();
		b.setComments("Lorem ipsum.");
		getBiosampleService().saveOrUpdate(b);
		assertTrue(getBiosampleService().get(b.getId()).getComments().equals("Lorem ipsum."));
	}

	@Test
	public void gTestDelete() {
		int before = getBiosampleService().getCount();
		try {
			getBiosampleService().delete(getBiosampleService().map(getLastInserted()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertTrue(before - getBiosampleService().getCount() == 1);
	}
	
	private Biosample getLastInserted() {
		List<Biosample> list = getBiosampleService().list();
		Collections.sort(list, new Comparator<Biosample>() {
			public int compare(Biosample s1, Biosample s2) {
				return Integer.compare(s2.getId(), s1.getId());
			}
		});
		Biosample a = list.get(0);
		return a;
	}

}
