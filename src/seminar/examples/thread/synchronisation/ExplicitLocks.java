// Copyright Marwan Abu-Khalil 2012

package seminar.examples.thread.synchronisation;

import java.util.concurrent.locks.ReentrantLock;

public class ExplicitLocks {

	ReentrantLock explicitLock = new ReentrantLock();
	
	void myExplcitlyLockedBlock(){
		explicitLock.lock();
		try{
			// Critical Section
		}finally{
			explicitLock.unlock();
		}
	}
}
