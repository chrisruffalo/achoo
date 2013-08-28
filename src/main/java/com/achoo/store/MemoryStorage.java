package com.achoo.store;

import java.io.InputStream;

/**
 * Memory-only document storage
 * 
 * @author Chris Ruffalo <cruffalo@redhat.com>
 *
 */
public class MemoryStorage implements IStorage {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void store(String tokenUuid, byte[] content) {
		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void complete(String tokenUuid) {

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public byte[] getByteArray(String tokenUuid) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public InputStream getInputStream(String tokenUuid) {
		return null;
	}

}
