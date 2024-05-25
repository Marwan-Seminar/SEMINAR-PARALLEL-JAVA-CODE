// Copyright Marwan Abu-Khalil 2019

package seminar.exercises.memorymodel.atomic_1_spinlock.solution;

import java.util.concurrent.atomic.AtomicLong;

/*  
 *  Musterlösung zur Übung Spinlock:  Eine einfache Implementierung eines Spinlocks 
 *  Schreiben Sie ein Spinlock auf Basis von compareAndSet(), 
 *  diese Methode finden Sie z.B: in der Klasse java.util.concurrent.atomic.AtomicLong
 *  
 *  Synchronisieren Sie damit eine Datenstruktur, um nachzuweisen, dass das Spinlock funktioniert.
 *  
 *  Lenziel: HW-Support für Mutex verstehen.
*/

/*
 * Testcode für Spinlock
 */
public class SpinlockTest_Solution{
	public static void main(String[] args) throws InterruptedException{
		MyCounterClass counter = new MyCounterClass();
		counter.startIncrementingThreads();
		System.out.println("Counter should be 100.000: acually it is: " + counter.counter);
	}
}

/*
 * Implements a real Spinlock based on compareAndSet()
 */

class Spinlock {

	// is either 0, then the lock is available or it contains the
	// id of the owning thread
	AtomicLong lockword = new AtomicLong();
		
	void acquire() {
		long thradID = Thread.currentThread().getId();
		while (!lockword.compareAndSet(0, thradID)) {
			// spin
		}
	}

	void release() {
		lockword.set(0);
	}
	
	
}

// Testcode dazu: 10 Threads inkrementieren den counter simultan.
// Mit Spinlock folgender output: Counter should be 100.000: acually it is: 100000
// Ohne Spinlock, bzw. mit StupidSpilock> Counter should be 100.000: acually it is: 85383
class MyCounterClass {
	int counter;
	Spinlock spinlock = new Spinlock();
	//StupidSpinlock spinlock = new StupidSpinlock();
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
	MyCounterClass counter;
	IncrementingThread(MyCounterClass counter){
		this.counter = counter;
	}
	public void run(){
		for(int i = 0; i < 10000; ++i){
			counter.increment();
		}
	}
}


//Demonstrates, that Spinlock does not work without CAS
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

