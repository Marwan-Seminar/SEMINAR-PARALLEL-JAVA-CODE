package seminar.examples.memorymodel;

/**
 * 
 *  *DOES NOT SHOW EXPECTED BEHAVIOR ON MY MACHINE:
 *  	Memory-Barriers introduced by volatile variables
 *  	do not make an observable difference. 
 * 		I have to declare turn as volatile, and than Petersons Lock seems to work correctly.
 * 
 * This class demonstrates, that Petersons solution works to realize Mutex in software.
 * It shows also, that Memory Barriers are required on a Multicore to make it work, due to the Memorymodel
 * 
 * Two solutions are possible, as no explicit memory barriers are available in Java.
 * 1. declaring turn as volatile.
 * 2. introducing a volatile dummy variable and accessing it to introduce a memory barrier. 
 *    (This 2. approach does not seem to work on all platforms, although it should conform to the JMM)
 *
 */
class PetersonsLock {

	boolean[] interested = {false, false}; // = new boolean[2];
	
	volatile
	int turn;	
	
	//volatile
	//int memory_barrier;
	
	void acquireLock(int threadID){
		
		// The id of the other thread
		int otherThreadID = 1 - threadID;
		
		// Declaring, that I am interested to enter the critical section
		interested[threadID] = true;
		// Give other thread priority
		turn = otherThreadID;
		
		// This Memory Barrier is required to guarantee mutual exclusion
		//memory_barrier = otherThreadID;
		
		while(interested[otherThreadID] && turn == otherThreadID){
			// Busy wait
			
			// This  Memory Barrier is required for avoiding a deadlock / lifelock
			//memory_barrier = otherThreadID;
		}
		// Enter critical section
		
	}
	
	void releaseLock(int threadID){
		// I am no more interested in entering the critical section
		interested[threadID] = false;
	}
		
}


/*
 * This class creates inconsistent states and Errors if not synchronized.
 * To make the program correct, uncomment 3 (three!) synchronized statements marked with "TODO" 
 */
class StackUnsynchronized{

	static final int STACK_SIZE = 100;

	Integer[] stackData = new Integer[STACK_SIZE];

	// Points to the topmost occupied slot, i.e is element of[-1, STACK_SIZE-1]
	int stackPointer = -1;

	boolean push(Integer arg) {

		if (stackPointer >= STACK_SIZE - 1) {
			// stack is full
			return false;
		}

		// push element
		stackData[stackPointer + 1] = arg;

		// increment stackpointer
		stackPointer++;

		return true;
	}

	/*
	 * Pops an item from the stack and returns it. If the stack is empty, null
	 * is returned
	 */
	synchronized
	Integer pop() {

		if (stackPointer <= -1) {
			// stack is empty
			return null;
		}

		Integer elementToPop = stackData[stackPointer];
		stackData[stackPointer] = null;

		// decrement stackpointer
		stackPointer--;

		return elementToPop;
	}

	/*
	 * Checks the invariant of the Stack, and throws an exception if
	 * it is violated. 
	 * The invariant is, that the stackpointer points to the
	 * topmost occupied slot of the stack.
	 */
	synchronized
	void checkStackInvariant() {
		if (stackPointer < -1 || stackPointer >= STACK_SIZE) {
			throw new Error("Invariant violated: range: " + stackPointer);
		}

		if (stackPointer > -1 && stackData[stackPointer] == null) {
			throw new Error("Invariant violated: null at: " + stackPointer);
		}
	}

	
}


/**
 * Uses Peterson's Lock to synchronize a stack.
 */
public class PetersonsLockTest {
	
	public static void main(String[] args) {
		
		final StackUnsynchronized stack = new StackUnsynchronized();

		final PetersonsLock petersonsLock = new PetersonsLock();
		
		// Error in unsynchronized case
		PetersonsLockTest testInstance = new PetersonsLockTest();
		testInstance.pudhAndPopMT(stack, petersonsLock);
		

	}

	// Simultaneously pushes and pops.
	void pudhAndPopMT(final	StackUnsynchronized stack, final PetersonsLock petersonsLock ) {
		
		
		
		System.out.println("PetersonsLockTest pudhAndPopMT() ");
		
		new Thread() {
			
			final int threadID = 0;
			
			public void run() {
				// push new items onto the stack infinitely
				int item = 0;
				while (true) {
					boolean success = false;
					item++;
					// repeat the call push (item), until the push is successful,
					// which means, there is space on the stack to push
					while (!success) {
						petersonsLock.acquireLock(threadID);
					
						success = stack.push(item);
						stack.checkStackInvariant();
					
						petersonsLock.releaseLock(threadID);
					}
					// Print from time to time what has been pushed
					if (item % 1000000 == 0) {
						System.out.println("Pushed item " + item);
					}

				}
			}
		}.start();

		new Thread() {
			
			final int threadID = 1;
			
			public void run() {
				// Pop infinitely
				while (true) {
					Integer poppedItem = null;
					while(poppedItem == null){
						petersonsLock.acquireLock(threadID);
						
						poppedItem = stack.pop();
						stack.checkStackInvariant();
						
						petersonsLock.releaseLock(threadID);
					}
					// Print from time to time what has been pushed
					if (poppedItem % 1000000 == 0) {
						System.out.println("Popped item " + poppedItem);
					}
				}
			}
		}.start();
	}
}