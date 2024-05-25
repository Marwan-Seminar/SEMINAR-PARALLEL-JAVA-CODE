// Copyright Marwan Abu-Khalil 2012

package seminar.examples.thread.api;

import java.util.concurrent.locks.ReentrantLock;

public class ApiSamples {

	public static void main(String[] args) throws Exception {
		ApiSamples instance = new ApiSamples();
		
		//instance.threadCreatingMethod();
		
		//(new JoiningThreads()).joinThread();
		
		DataInOutThread.testDataInOut();
	}

	void threadCreatingMethod() {
		// Parallelization
		new Thread() {
			public void run() {
				// work in separate thread
			}
		}.start();
	}

	// Synchronization
	synchronized void mySynchronizedMethod() {
		// mutex protected work
	}
	
	boolean condition;
	// Monitor Pattern
	synchronized void MonitorUsage() throws InterruptedException{
		while(condition){
			wait();
		}
		notify();
	}
	
	// Global data
	Data data = new Data();
	synchronized void anyMethod(){
		// Threadsafe call
		data.change();
	}
	
}

class Data{
	void change(){
		
	}
}
// Extending Thread class
class MyThread extends Thread{
	// run() is called by the VM inside a new OS thread 
	public void run(){
		// do work in extra thread
	}
	
	static void startMyThread(){
		Thread myThread = new MyThread();
		myThread.start();
	}
}

// Implementing Runnable
class MyRunnable implements Runnable{
	public void run(){
		// do work in extra thread
	}
	
	void startMyRunnable(){
		Thread aThread = new Thread( new MyRunnable());
		aThread.start();
	}
}


// Joining Example
class JoiningThreads{
	
	void joinThread() throws InterruptedException{
		Thread thread = new Thread(){
			public void run(){
				System.out.println("New Thread running");
			}
		};
		// Starting the Thread
		thread.start();
		
		// Waiting for Thread to return
		//thread.join();
		System.out.println("New Thread finished");

	}
}

class DataInOutThread extends Thread{
	int data;
	
	DataInOutThread(int dataArg){
		this.data = dataArg;
	}
	
	public void run(){
		for(int i = 1; i< 100000; ++i){
			data++;
		}
	}
	
	static void testDataInOut() throws InterruptedException{
		DataInOutThread thread = new DataInOutThread(7);
		thread.start();
		System.out.println("Data after start:" + thread.data);
		thread.join();
		System.out.println("Data after join:" + thread.data);
		
	}
}

class ExplicitLock {

	ReentrantLock explicitLock = new ReentrantLock();

	void myExplcitlyLockedBlockUnsafe() {

		explicitLock.lock();

		// Critical Section

		explicitLock.unlock();

	}

	void myExplcitlyLockedBlockSafe() {

		explicitLock.lock();
		try {
			// Critical Section
		} finally {
			explicitLock.unlock();
		}
	}

	
}