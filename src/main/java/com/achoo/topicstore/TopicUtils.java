package com.achoo.topicstore;

import java.util.regex.Pattern;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;


public class TopicUtils {

	private static final Splitter SPLITTER = Splitter.on(".").limit(1);
	private static final Pattern VALID_SUBSCRIBE_PATTERN = Pattern.compile("[^a-zA-Z0-9\\#\\.\\*]");
	private static final Pattern VALID_PUBLISH_PATTERN = Pattern.compile("[^a-zA-Z0-9]");
	
	public static Iterable<String> split(String topic) {
		return TopicUtils.SPLITTER.split(topic);
	}
	
	public static String normalize(String topic, boolean publication) {
		if(Strings.isNullOrEmpty(topic)) {
			return "";
		}
		
		// in a topic "/" is functionally equivalent to "."
		// and we only intend to operate with ONE of them
		// so . it is.  (this is like qpid/mrg-m)
		if(topic.contains("/")) {
			topic = topic.replaceAll("/", ".");
		}
		
		// recursively remove instances where two dots
		// are adjacent
		while(topic.contains("..")) {
			topic = topic.replaceAll("\\.\\.", ".");
		}
		
		// remove trailing "root points"
		while(topic.startsWith(".")) {
			topic = topic.substring(1);
		}
		
		// remove trailing "slashes"
		while(topic.endsWith(".")) {
			topic = topic.substring(0, topic.length() - 1);
		}
		
		// choose pattern based on if normalization is for publish or subscription
		Pattern pattern = (publication) ? VALID_PUBLISH_PATTERN : VALID_SUBSCRIBE_PATTERN;		
		// acceptable patterns
		if(pattern.matcher(topic).find()) {
			throw new InvalidTopicException("An invalid topic '" + topic + "' for '" + ((publication) ? "publication" : "subscription") + "' was provided");
		}
		
		// trim and lower
		topic = topic.trim();
		topic = topic.toLowerCase();
		
		return topic;
	}
	
}
