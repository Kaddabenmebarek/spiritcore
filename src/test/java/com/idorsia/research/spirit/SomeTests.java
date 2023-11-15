package com.idorsia.research.spirit;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ FoodWaterTest.class, NamedSamplingTest.class, NamedTreatmentTest.class, PhaseTest.class,
		SamplingMeasurementTest.class, SamplingParameterTest.class, GroupTest.class, SubGroupTest.class })
public class SomeTests {

	private static long start;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		start = System.currentTimeMillis();
	}
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		System.out.println("Duration: " + (System.currentTimeMillis() - start) / 1000 + "s");
	}
	
}
