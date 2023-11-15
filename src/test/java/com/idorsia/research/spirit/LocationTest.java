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
import com.idorsia.research.spirit.core.dto.LocationDto;
import com.idorsia.research.spirit.core.model.Location;
import com.idorsia.research.spirit.core.util.DataUtils;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class LocationTest extends AbstractSpiritTest {

	private static final Integer LOCATIONID = 7140;
	
	@Test
	public void aTestGet() {
		assertNotNull(getLocationService().get(LOCATIONID));
	}

	@Test
	public void bTestDozerConversion() {
		LocationDto lDto = getDozerMapper().map(getLocationService().get(LOCATIONID), LocationDto.class, "locationCustomMapping");
		assertNotNull(lDto.getParent());
		Location l = getDozerMapper().map(lDto, Location.class, "locationCustomMapping");
		assertTrue(l.getParentId().equals(lDto.getParent().getId()));
	}
	
	@Test
	public void cTestList() {
		assertTrue(!CollectionUtils.isEmpty(getLocationService().list()));
	}

	@Test
	public void dTestGetCount() {
		assertTrue(getLocationService().getCount()>0);
	}
	
	@Test
	public void eTestAddLocation() {
		Location l = new Location(getLocationService().getSequence(Constants.LOCATION_SEQUENCE_NAME), 12, "NameTest",
				DataUtils.booleanToInt(Boolean.FALSE), 8, "BOX");
		l.setParentId(11020);
		assertNotNull(getLocationService().addLocation(l));
	}

	@Test
	public void fTestSaveOrUpdate() {
		Location l = getLastInserted();
		l.setLocationtype("LAB");
		getLocationService().saveOrUpdate(l);
		assertTrue(getLocationService().get(l.getId()).getLocationtype().equals("LAB"));
	}

	@Test
	public void gTestDelete() {
		int before = getLocationService().getCount();
		try {
			getLocationService().delete(getLocationService().map(getLastInserted()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertTrue(before - getLocationService().getCount() == 1);
	}
	
	private Location getLastInserted() {
		List<Location> list = getLocationService().list();
		Collections.sort(list, new Comparator<Location>() {
			public int compare(Location s1, Location s2) {
				return Integer.compare(s2.getId(), s1.getId());
			}
		});
		Location a = list.get(0);
		return a;
	}

}
