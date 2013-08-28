package com.achoo.store;

import java.util.concurrent.Executor;

import com.achoo.Hub;
import com.achoo.model.Document;
import com.lmax.disruptor.EventHandler;

public class DocumentStorer extends Hub<Document> { 

	private class StorageHandler implements EventHandler<Document> {

		private IStorage storage;
		
		public StorageHandler(IStorage storage) {
			this.storage = storage;
		}
		
		@Override
		public void onEvent(Document event, long sequence, boolean endOfBatch) throws Exception {
			this.storage.store(event.getUuid(), event.getContents());
			
			if(event.isLastPart()) {
				this.storage.complete(event.getUuid());
			}
		}
		
	}
	
	public DocumentStorer(Executor executor, int ringSize, IStorage storage) {
		super(executor, ringSize, Document.getContentsFactory());
		
		StorageHandler handler = new StorageHandler(storage);
		
		StorageHandler[] handlers = new StorageHandler[] {
			handler
		};
		
		this.getDisruptor().handleEventsWith(handlers);
		this.getDisruptor().start();
	}
	
}
