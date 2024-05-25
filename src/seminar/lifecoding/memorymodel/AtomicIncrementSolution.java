package seminar.lifecoding.memorymodel;

import java.util.concurrent.atomic.AtomicInteger;

/*
 * Musterlöunng zur Übung Atomic-Increment
 * 
 * Shows that increment operations are not atomic
 * and demonstrates use of atomic increment methods
 * (bytcode does not provide this, as instruction iinc is not atomic!)
 * 
 * Although AtomicInteger has a method for atomic increment: getAndIncrement() 
 * this example does intentionally not use this method, but instead shows how
 * such an atomic increment could be implemented by use of CAS (Compare-And-Set)
*/

public class AtomicIncrementSolution {

	
	AtomicInteger atomicInt = new AtomicInteger(0);
	
	int nonAtomicInt = 0;
	
	public static void main(String[] args) {
		AtomicIncrementSolution instance = new AtomicIncrementSolution();
		instance.testAtomicInc();
		instance.testNonAtomicInc();
	}
	
	void incrementNonAtomic(){
		
		nonAtomicInt = nonAtomicInt +1;
	}
	
	void atomicIncrementCAS() {
		
		int oldValue = atomicInt.get();
		int newValue = oldValue + 1;
		
		//System.out.println("old out loop " + oldValue + " new " + newValue);
		
		while(!atomicInt.compareAndSet(oldValue, newValue)) {
			oldValue = atomicInt.get();
			newValue = oldValue + 1;

			// System.out.println("old in loop " + oldValue + " new " + newValue);
			
		}
	}
	
	
	void testAtomicInc() {
		

		System.out.println("testAtomicInc()");
		
		Thread firstThread = new Thread(()->{
			for(int i = 0; i < 5000; ++i) {
				atomicIncrementCAS();
			}
		});
		Thread secondThread = new Thread(()->{
			for(int i = 0; i < 5000; ++i) {
				atomicIncrementCAS();
			}
		});
		
		firstThread.start();
		secondThread.start();
		

		try {
			firstThread.join();
			secondThread.join();
		} catch (InterruptedException e) {
			throw new Error(e);
		}
		
		System.out.println(atomicInt.get());
		
	}
	
	void testNonAtomicInc() {
		

		System.out.println("testNonAtomicInc()");
		
		Thread firstThread = new Thread(()->{
			for(int i = 0; i < 5000; ++i) {
				incrementNonAtomic();
			}
		});
		Thread secondThread = new Thread(()->{
			for(int i = 0; i < 5000; ++i) {
				incrementNonAtomic();
			}
		});
		
		firstThread.start();
		secondThread.start();
		

		try {
			firstThread.join();
			secondThread.join();
		} catch (InterruptedException e) {
			throw new Error(e);
		}
		
		System.out.println(nonAtomicInt);
		
	}
	
}
