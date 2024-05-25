// Copyright Marwan Abu-Khalil 2012

package seminar.examples.thread.synchronisation;

public class Stack {

	// Array used for stack-data
	Object[] stack = new Object[100];
	
	// Points to the top element in the stack array
	int stackPointer = -1;
	
	synchronized void push(Object newData){
		// Insert new data
		stack[stackPointer + 1] = newData;
	
		// Increment stack pointer
		stackPointer++;
	}
}
