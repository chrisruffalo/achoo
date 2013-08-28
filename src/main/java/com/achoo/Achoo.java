package com.achoo;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.achoo.broker.Dispatcher;
import com.achoo.input.http.HttpInput;
import com.achoo.store.DocumentStorer;
import com.achoo.store.MemoryStorage;


public class Achoo {

	public void start() {
		// create executor
		Executor sharedExecutor = Executors.newFixedThreadPool(4);
		
		// create central dispatch control
		Dispatcher dispatcher = new Dispatcher(sharedExecutor, 4096);
		
		// add handlers
		MemoryStorage storage = new MemoryStorage();
		DocumentStorer storer = new DocumentStorer(sharedExecutor, 4096, storage);
		
		HttpInput input = new HttpInput(dispatcher, storer);
		input.create(8080);
	}
	
	public static void main(String[] args) {
		
		Achoo achoo = new Achoo();
		achoo.start();
		
	}
	
}
