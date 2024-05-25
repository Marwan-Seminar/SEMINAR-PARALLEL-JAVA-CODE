// Copyright Marwan Abu-Khalil 2012

package seminar.examples.thread.synchronisation;

import java.util.concurrent.atomic.AtomicInteger;


public class NonAtomic {

	int counter;
	
	void increment(){
		counter ++;
	}
	
	void incrementReal(){
		int tmp = counter + 1;
		counter = tmp;
	}
	
}

class Atomic{
	
	AtomicInteger atomicCounter = new AtomicInteger();
	
	void increment(){
		atomicCounter.incrementAndGet();
	}
}
