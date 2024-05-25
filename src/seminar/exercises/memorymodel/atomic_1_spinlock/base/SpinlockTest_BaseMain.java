// Copyright Marwan Abu-Khalil 2019

package seminar.exercises.memorymodel.atomic_1_spinlock.base;

import java.util.concurrent.atomic.AtomicLong;

/*  
 *  Übung Spinlock:  Eine einfache Implementierung eines Spinlocks 
 *  Schreiben Sie ein Spinlock auf Basis von compareAndSet(), 
 *  diese Methode finden Sie z.B: in der Klasse java.util.concurrent.atomic.AtomicLong
 *  
 *  Synchronisieren Sie damit eine Datenstruktur, um nachzuweisen, dass das Spinlock funktioniert.
 *  
 *  Lenziel: HW-Support für Mutex verstehen.
*/

public class SpinlockTest_BaseMain {
	
	public static void main(String[] args) throws InterruptedException{
		Counter counter = new Counter();
		counter.startIncrementingThreads();
		System.out.println("Counter should be 100.000: acually it is: " + counter.counter);
	}
}

// This "StupidSpinlock" calss demonstrates, that Spinlock does not work without CAS
class StupidSpinlock{
	
	volatile long lockword = 0;
	
	void acquire() {
		long thradID = Thread.currentThread().getId();
		while (lockword != 0) {
			// spin
		}
		// lock
		lockword = thradID;
	}

	void release() {
		lockword = 0;
	}
}

// Testcode zum Spinlock : 10 Threads inkrementieren den counter simultan.
// Ohne Spinlock: Counter should be 100.000: acually it is: 85383
 class Counter {
	int counter;
	StupidSpinlock spinlock = new StupidSpinlock();
	
	
	void increment() {
		spinlock.acquire();
		counter++;
		spinlock.release();
	}

	void startIncrementingThreads() throws InterruptedException {
		int NR_OF_THREADS = 10;
		Thread[] threads = new Thread[NR_OF_THREADS];
		for (int threadCout = 0; threadCout < NR_OF_THREADS; threadCout++) {
			threads[threadCout] = new IncrementingThread(this);
			threads[threadCout].start();
		}
		for(int i = 0; i < threads.length; ++i){
			threads[i].join();
		}
	}
}

class IncrementingThread extends Thread{
	Counter counter;
	IncrementingThread(Counter counter){
		this.counter = counter;
	}
	public void run(){
		for(int i = 0; i < 10000; ++i){
			counter.increment();
		}
	}
}



