package com.teamd.taxi;

import static org.junit.Assert.*;

import org.junit.Test;

import com.teamd.taxi.service.DistanceCalculator;

public class DistanceTest {

	@Test
	public void testCalculateDistanceForStrings() {
		//fail("Not yet implemented");
		assertEquals("Distance has to be: ",6266,DistanceCalculator.calculateDistance("Khreschatyk Street 1, Kyiv, Kyiv city",
						"Peremohy Avenue 22, Kyiv, Kyiv city"));
	}
	@Test
	public void testCalculateDistanceForNumbers() {
		//fail("Not yet implemented");
		assertEquals("Distance has to be: ",115384,DistanceCalculator.calculateDistance("50.45,31", "50,30"));
	}

}
