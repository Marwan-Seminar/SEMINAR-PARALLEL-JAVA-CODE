package seminar.exercises.memorymodel.mm_2_peterson.solution;

import java.util.concurrent.atomic.AtomicInteger;


public class PetersonsLock_Increment_Solution {

	
	int nonAtomicInt = 0;
	
	PetersonsLock petersonsLock = new PetersonsLock();
	
	public static void main(String[] args) {
		PetersonsLock_Increment_Solution instance = new PetersonsLock_Increment_Solution();
		
		
		instance.testNonAtomicInc();
	}
	
	void incrementNonAtomic(){
		
		nonAtomicInt = nonAtomicInt +1;
	}
		
	void testNonAtomicInc() {
		

		System.out.println("testNonAtomicInc()");
		
		Thread firstThread = new Thread(()->{
			for(int i = 0; i < 50000000; ++i) {
				petersonsLock.acquireLock(0);
				
				incrementNonAtomic();
				
				petersonsLock.releaseLock(0);
			}
		});
		Thread secondThread = new Thread(()->{
			for(int i = 0; i < 50000000; ++i) {
				petersonsLock.acquireLock(1);
				incrementNonAtomic();
				petersonsLock.releaseLock(1);
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



/*
 * Petersons solution is a classic algorithm that implements mutex in software. 
 * But on modern multi-processors, the memory model introduces reorderings that 
 * interfere with this algorithm.
 * 
 * Therefore it is required to introduce so called memory barriers. 
 * 
 * Java does not provide explict memory barriers. Instead the memory barriers are 
 * introduced implicitely by the use of volatile variables of synchronized blocks. 
 * 
 * This implementation of Petersons Lock does not work if volatile and synchronized are missing.
 * 
 * By introduction of one of these it can me fixed.
 */
class PetersonsLock {


	boolean[] interested = new boolean[2];
	
	// volatile turn is one  option to fix this mutex implementation. 
	// This shows, the strong semantics of volatile in Java, that includes acquire release semantics
	// and introduces the required memory barriers.
	
	// Without volatile inconsistency or deadlock can occur.
	//volatile 
	int turn;
	

	PetersonsLock(){
		
		interested[0] = false;
		interested[1] = false;
		turn = 0;
		
	}
	
	
	// Acquires lock for one of the two processes
	void acquireLock(int  callingProcess){
		
		// Id of this callingProcess and otherProcess
		int otherProccess = 1 - callingProcess;
		
		interested[callingProcess] = true;
		turn = otherProccess;
			
		// TODO  memory barrier here is required to avoid simultaneous entry to critical section
		
		
		int loopCount = 0;
		while ((interested[otherProccess] == true) && (turn == otherProccess) ){
			
			
			// memory barrier here reuqired to avoiding DEADLOCK / LIFELCOK

			
			// busy wait indicator
			if(++loopCount% 100000000 == 0){
				//System.out.println("BusyWait: " + Thread.currentThread().getName());
			}
		}

	}
	
	void releaseLock(int  callingProccess){
		interested[callingProccess] = false;
		
	}
	
};