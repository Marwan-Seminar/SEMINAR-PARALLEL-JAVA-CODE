// Copyright Marwan Abu-Khalil 2012

package seminar.examples.thread.synchronisation;

// Demonstrates options for usage of synchronized
public class MySynchronizedClass {

	synchronized void mySynchronizedMethod(){
		// Method can be executed on this object by 
		// only one thread at a time
	}
	
	void mySycnhronizedBlock(){
		
		synchronized(this){
			// Block can be executed on this
			// object only by one thread each moment in time
		}
	}
	
	static synchronized void myStaticSynchMethod(){
		// Code can not be executed in parallel 
		// even if called on different objects
	}
}
