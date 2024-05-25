// Copyright Marwan Abu-Khalil 2012

package seminar.examples.thread.synchronisation;

import java.util.concurrent.locks.ReentrantLock;
// Demonstrates Deadlock due to Exception in Critical-Section
public class DeadlockException implements Runnable{

	ReentrantLock explicitLock = new ReentrantLock();
	boolean dead = true;
	
	void deadlockingMethod(){
		explicitLock.lock();
		// Critical Section
		System.out.println("Thread entered critical section");
		if(dead){
			throw new Error();
		}
		explicitLock.unlock();
	}
	
	public void run(){
		deadlockingMethod();
	}
	public static void main(String[] args) throws InterruptedException{
		DeadlockException deadlockingObject = new DeadlockException();
		new  Thread(deadlockingObject).start();
		new  Thread(deadlockingObject).start();
		
	}
}
