package com.idorsia.research.spirit;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.util.CollectionUtils;

import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.dto.FoodWaterDto;
import com.idorsia.research.spirit.core.model.FoodWater;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class FoodWaterTest extends AbstractSpiritTest{

	private static final Integer FOODWATER_ID = 850;
	
	@Test
	public void aTestGet() {
		assertNotNull(getFoodWaterService().get(FOODWATER_ID));
		assertTrue(FOODWATER_ID.equals(getFoodWaterService().get(FOODWATER_ID).getId()));
	}

	@Test
	public void bTestDozerConversion() {
		FoodWaterDto fDto = getDozerMapper().map(getFoodWaterService().get(FOODWATER_ID), FoodWaterDto.class,"foodWaterCustomMapping");
		assertNotNull(fDto.getEnclosure());
		FoodWater f = getDozerMapper().map(fDto, FoodWater.class,"foodWaterCustomMapping");
		assertTrue(f.getEnclosureId().equals(fDto.getEnclosure().getId()));
	}
	
	@Test
	public void cTestList() {
		assertTrue(!CollectionUtils.isEmpty(getFoodWaterService().list()));
	}

	@Test
	public void dTestGetCount() {
		assertTrue(getFoodWaterService().getCount()>0);
	}
	
	@Test
	public void eTestAddFoodWater() {
		FoodWater fw = new FoodWater();
		fw.setId(getFoodWaterService().getSequence(Constants.FOOD_WATER_SEQUENCE_NAME));
		fw.setCreDate(new Date());
		fw.setCreUser("benmeka1");
		fw.setUpdDate(new Date());
		fw.setUpdUser("benmeka1");
		fw.setFoodTare(Double.valueOf(500));
		assertNotNull(getFoodWaterService().addFoodWater(fw));
	}

	@Test
	public void fTestSaveOrUpdate() {
		FoodWater fw = getLastInserted();
		fw.setFoodWeight(Double.valueOf(500));
		getFoodWaterService().saveOrUpdate(fw);
		assertTrue(getFoodWaterService().get(fw.getId()).getFoodWeight().equals(Double.valueOf(500)));
	}

	@Test
	public void gTestDelete() {
		int before = getFoodWaterService().getCount();
		getFoodWaterService().delete(getFoodWaterService().map(getLastInserted()));
		assertTrue(before - getFoodWaterService().getCount() == 1);
	}
	
	private FoodWater getLastInserted() {
		List<FoodWater> list = getFoodWaterService().list();
		Collections.sort(list, new Comparator<FoodWater>() {
			public int compare(FoodWater s1, FoodWater s2) {
				return Integer.compare(s2.getId(), s1.getId());
			}
		});
		FoodWater a = list.get(0);
		return a;
	}

}
