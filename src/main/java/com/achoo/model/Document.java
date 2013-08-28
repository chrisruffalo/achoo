package com.achoo.model;

import com.lmax.disruptor.EventFactory;

public class Document extends Item {

	private static final int DEFAULT_DOCUMENT_SIZE = 1024;
	
	private byte[] contents;
	
	private boolean lastPart;
	
	public Document() {
		this.contents = new byte[Document.DEFAULT_DOCUMENT_SIZE];
		this.lastPart = false;
	}
	
	public byte[] getContents() {
		return contents;
	}

	public void setContents(byte[] contents) {
		this.contents = contents;
	}
	
	public boolean isLastPart() {
		return lastPart;
	}

	public void setLastPart(boolean lastPart) {
		this.lastPart = lastPart;
	}

	public static EventFactory<Document> getContentsFactory() {
		return new EventFactory<Document>() {
			@Override
			public Document newInstance() {
				return new Document();
			}			
		};
	}	
}
