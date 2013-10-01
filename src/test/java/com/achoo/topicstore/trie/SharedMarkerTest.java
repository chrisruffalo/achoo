package com.achoo.topicstore.trie;

import org.junit.Assert;
import org.junit.Test;

public class SharedMarkerTest {

	@Test
	public void basicSet() {
		long low = 12l;
		SharedMarker marker = new SharedMarker();
		marker.set(low);
		Assert.assertTrue(marker.get(low));
		Assert.assertFalse(marker.get(low+5));
		Assert.assertFalse(marker.get(low-5));
		Assert.assertEquals(1, marker.size(low));
		
		long high = ((long)Integer.MAX_VALUE) * 3;
		Assert.assertTrue(high > Integer.MAX_VALUE);
		marker.set(high);
		Assert.assertTrue(marker.get(high));
		Assert.assertFalse(marker.get(high+5));
		Assert.assertFalse(marker.get(high-5));
		Assert.assertEquals(1, marker.size(high));
		
		long[] positions = marker.positions(high - 5);
		Assert.assertEquals(1, positions.length);
		Assert.assertEquals(high, positions[0]);
	}
	
}
