// Copyright Marwan Abu-Khalil 2012

package seminar.examples.thread.synchronisation;

import java.util.Stack;
import java.util.concurrent.locks.ReentrantReadWriteLock;

// Connection Pool with Reader-Writer-Lock
public class ReaderWriterPool {

	ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
	
	Stack<Object> pool = new Stack<Object>();
	
	// Exclusive access
	void createNewConnection(){
		// Acquire lock for writing (exclusive)
		rwLock.writeLock().lock();
		try{
			// Insert new DB-Connection into the pool
			pool.push(new Object());
			try{Thread.sleep(5000);}catch(Exception e){throw new Error();};
		}finally{
			rwLock.writeLock().unlock();
		}
	}
	
	// Shared access
	Object getConnectionFromPool(){
		// Get read lock
		rwLock.readLock().lock();
		try{
			// if pool empty, call createNewConnection() with unlocked read lock!
			if(pool.isEmpty()){
				// release read-lock
				rwLock.readLock().unlock();
				createNewConnection();
				// re-acquire read-lock
				rwLock.readLock().lock();
			}
			// now fetch from pool... read-lock protected
			return pool.pop();
		}finally{
			rwLock.readLock().unlock();
		}
	}

	public static void main(String[] args){
		final long startTime = System.currentTimeMillis();
		// Test Read-Write behavior
		final ReaderWriterPool rwConnectionPool = new ReaderWriterPool();
		// Start two writers
		for(int i = 1; i <= 2; ++i){
			new Thread(){public void run(){
				System.out.println("Writer start " + (System.currentTimeMillis()-startTime));
				rwConnectionPool.createNewConnection();
				System.out.println("Writer return " +(System.currentTimeMillis()-startTime));
			}}.start();
		}
		
		// Start two readers
		for(int i = 1; i <= 2; ++i){
			new Thread(){public void run(){
				System.out.println("Reader start "+ (System.currentTimeMillis()-startTime));
				rwConnectionPool.getConnectionFromPool();
				System.out.println("Reader return " + (System.currentTimeMillis()-startTime));
			}}.start();	
		}
	}
}

