package com.achoo.store;

import java.io.InputStream;

/**
 * Storage interface for storing items to some sort of persistent, permanent, or transient storage
 * 
 * @author Chris Ruffalo <cruffalo@redhat.com>
 *
 */
public interface IStorage {

	/**
	 * Store a binary content array for the given byte array
	 * 
	 * @param tokenUuid
	 * @param content
	 */
	public void store(String tokenUuid, byte[] content);
	
	/**
	 * Mark a document as written
	 * 
	 * @param tokenUuid
	 */
	public void complete(String tokenUuid);
	
	/**
	 * Get the full byte array for a document as related to a particular token
	 * 
	 * @param tokenUuid
	 * @return
	 */
	byte[] getByteArray(String tokenUuid);
	
	/**
	 * Get the input stream for a document related to the given token
	 * 
	 * @param tokenUuid
	 * @return
	 */
	InputStream getInputStream(String tokenUuid);
	
}
