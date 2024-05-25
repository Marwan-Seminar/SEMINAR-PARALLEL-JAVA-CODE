// Copyright Marwan Abu-Khalil 2012

package seminar.lifecoding.thread;

/*

	Ü 2.4 Ein unkorrekt synchronisiertes Programm schreiben (Stack).
	
	a)	Schreiben sie ein Programm, bei dem eine Datenstruktur von zwei nebenläufigen Threads korrumpiert wird. Z.B. ein Stack, der einen Pointer auf das oberste Element hat (oder einen Zähler, der definiert, welches das oberste Element ist). Zeigen sie, dass das Programm fehlerhaften Output liefert.
	b)	Synchronisieren sie das Programm korrekt und zeigen Sie, dass es nun einen korrekten Output liefert.
	
	Lernziel: Risiken der unkorrekten Synchronisation verstehen, Gegenmaßnahmen finden.

  
    Hinweise zur Lösung
    
    Die Klasee Stack implementiert einen Stack, der Fehlerzustände liefert, wenn er unsynchronisiert benutzt wird, 
    die Klasse TestStack enthält den Testcode dazu.
    Stack.push(): Befüllt den Stack
    Stack.pop(): Entnimmt aus dem Stack
    Stack.checkStackInvariant(): Prüft die Konsistenz der internen Datenstruktur und wirft eine Exception im Falle von Inkonsistenz 
 
   	Lösungen können  basieren auf:
    
    1. synchronized in der Klasse Stack
    oder 
    2. synchronized(geeignetes Objekt) { Block mit Code, der geschützt werden soll} in TestStack
 
 */

/*
 * This class creates inconsistent states and Errors if not synchronized.
 * To make the program correct, uncomment 3 (three!) synchronized statements marked with "TODO" 
 */
public class StackUnsynchronized_Solution {

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
	 * Invariant: Checks the invariant of the Stack, and throws an exception if
	 * it is violated. The invariant is, that the stackpointer points to the
	 * topmost occupied slot of the stack.
	 */
	void checkStackInvariant() {
		if (stackPointer < -1 || stackPointer >= STACK_SIZE) {
			throw new Error("Invariant violated: range: " + stackPointer);
		}

		if (stackPointer > -1 && stackData[stackPointer] == null) {
			throw new Error("Invariant violated: null at: " + stackPointer);
		}
	}

	public static void main(String[] args) {
		TestStack testInstance = new TestStack();

		// Error in unsynchronized case
		testInstance.pudhAndPopMT();

		// Error in unsynchronized case
		//testInstance.fillAndCheckMT();

		// testInstance.fillAndCheckST();
		// testInstance.emptyAndCheckST();

	}
}

class TestStack {

	// Simultaneously pushes and pops.
	void pudhAndPopMT() {
		
		System.out.println("StacUnsynchronzed_Solution pudhAndPopMT() ");
		
		final StackUnsynchronized_Solution stack = new StackUnsynchronized_Solution();

		new Thread() {
			public void run() {
				// push new items onto the stack infinitely
				int item = 0;
				while (true) {
					boolean success = false;
					item++;
					// repeat the call push (item), until the push is successful,
					// which means, there is space on the stack to push
					while (!success) {
						success = stack.push(item);
						stack.checkStackInvariant();
					}
					// Print from time to time what has been pushed
					if (item % 1000000 == 0) {
						System.out.println("Pushed item " + item);
					}

				}
			}
		}.start();

		new Thread() {
			public void run() {
				// Pop infinitely
				while (true) {
					Integer poppedItem = null;
					while(poppedItem == null){
						poppedItem = stack.pop();
						stack.checkStackInvariant();
					}
					// Print from time to time what has been pushed
					if (poppedItem % 1000000 == 0) {
						System.out.println("Popped item " + poppedItem);
					}
				}
			}
		}.start();
	}

	void fillAndCheckST() {

		StackUnsynchronized_Solution stack = new StackUnsynchronized_Solution();

		for (int i = 0; i < 300; ++i) {
			stack.push(i);
			stack.checkStackInvariant();
		}
	}

	void emptyAndCheckST() {

		StackUnsynchronized_Solution stack = new StackUnsynchronized_Solution();

		for (int i = 0; i < 300; ++i) {
			stack.push(i);
			stack.checkStackInvariant();
		}

		for (int i = 0; i < 400; ++i) {
			Integer popped = stack.pop();
			stack.checkStackInvariant();
			System.out.println("Popped:" + popped);
		}
	}

	// Pushes simultaneously in 2 Threads
	void fillAndCheckMT() {

		final StackUnsynchronized_Solution stack = new StackUnsynchronized_Solution();
		final int NR_OF_PUSH_THREADS = 2;
		for (int threadCout = 0; threadCout < NR_OF_PUSH_THREADS; threadCout++) {
			new Thread() {
				public void run() {
					int i = 0;
					while (true) {
						i++;
						stack.checkStackInvariant();
						stack.push(i);
						stack.checkStackInvariant();

					}
				}
			}.start();
		}
	}
}
