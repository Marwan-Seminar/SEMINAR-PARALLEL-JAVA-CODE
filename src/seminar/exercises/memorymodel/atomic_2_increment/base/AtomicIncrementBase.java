package seminar.exercises.memorymodel.atomic_2_increment.base;

import java.util.concurrent.atomic.AtomicInteger;

/*
 * Übung: Atomares Inkrement
 * 
 * Implement an atomic Increment based on AtomicInteger.compareAndSet()
 *  
 * The code example belco shows that increment operations are not atomic
 * (bytcode also does not provide this, as instruction iinc is not atomic!)
 * 
 * Two threads increment a common variable. Each thread increments it 5000 times.
 * But the result is less than 10.000. This is inconsistent. Some incremnts got lost.
 * 
 * Although AtomicInteger has a method for atomic increment: getAndIncrement() 
 * do not use this method, but instead show how such an atomic increment could
 * be implemented by use of CAS (Compare-And-Set)
*/

public class AtomicIncrementBase {

	
	int nonAtomicInt = 0;
	
	public static void main(String[] args) {
		AtomicIncrementBase instance = new AtomicIncrementBase();
		
		instance.testNonAtomicInc();
	}
	
	void incrementNonAtomic(){
		
		nonAtomicInt = nonAtomicInt +1;
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
