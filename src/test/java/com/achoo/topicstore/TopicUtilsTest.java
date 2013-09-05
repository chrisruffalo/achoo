package com.achoo.topicstore;

import org.junit.Assert;
import org.junit.Test;

import com.achoo.topicstore.InvalidTopicException;
import com.achoo.topicstore.TopicUtils;

/**
 * Testing topic utilities
 * 
 * @author Chris Ruffalo
 *
 */
public class TopicUtilsTest {

	@Test
	public void testNullAndEmptyNormalize() {
		Assert.assertEquals("", TopicUtils.normalize(null, false));
		Assert.assertEquals("", TopicUtils.normalize("", false));
	}
	
	@Test
	public void testNoNormalize() {
		Assert.assertEquals("airspace", TopicUtils.normalize("airspace", false));
	}
	
	@Test
	public void testNormalizeSlash() {
		Assert.assertEquals("airspace.usa", TopicUtils.normalize("airspace/usa", false));
		Assert.assertEquals("airspace.usa.heavy", TopicUtils.normalize("airspace/usa/heavy", false));
		Assert.assertEquals("airspace.usa.commercial", TopicUtils.normalize("airspace/usa/commercial", false));
		Assert.assertEquals("airspace.commercial", TopicUtils.normalize("airspace/./commercial", false));
		Assert.assertEquals("airspace.#.commercial", TopicUtils.normalize("airspace/#/commercial", false));
		Assert.assertEquals("airspace.uk", TopicUtils.normalize("...airspace.uk.....", false));
	}
	
	@Test
	public void testValidWildcardTopics() {
		TopicUtils.normalize("airspace/#/usa", false);
		TopicUtils.normalize("airspace/*/usa", false);
		TopicUtils.normalize("airspace/1234/usa", false);
		TopicUtils.normalize("airspace//", false);
		TopicUtils.normalize("..airspace", false);
	}
	
	@Test(expected=InvalidTopicException.class)
	public void testInvalidSubscribeWildcardTopic01() {
		TopicUtils.normalize("airspace/!/usa", false);
	}
	
	@Test(expected=InvalidTopicException.class)
	public void testInvalidSubscribeWildcardTopic02() {
		TopicUtils.normalize("airspace/1233/usa-", false);
	}

	@Test(expected=InvalidTopicException.class)
	public void testInvalidPublishWildcardTopic01() {
		TopicUtils.normalize("airspace/!/usa", true);
	}
	
	@Test(expected=InvalidTopicException.class)
	public void testInvalidPublishWildcardTopic02() {
		TopicUtils.normalize("airspace/u#/", true);
	}

}
