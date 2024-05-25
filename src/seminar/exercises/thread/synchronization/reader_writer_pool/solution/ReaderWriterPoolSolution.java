// Copyright Marwan Abu-Khalil 2012

package seminar.exercises.thread.synchronization.reader_writer_pool.solution;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.locks.ReentrantReadWriteLock;

// Connection Pool with Reader-Writer-Lock
// TODO improve Test scenario
// TODO: introduce real connection to database
public class ReaderWriterPoolSolution {



	public static void main(String[] args){
		final long startTime = System.currentTimeMillis();
		// Test Read-Write behavior
		final RederWriterPool rwConnectionPool = new RederWriterPool();
		
		// Start some Threads that accquire connections several times. They start as readers and become to writers
		for(int i = 1; i <= 2; ++i){
			new Thread(){public void run(){
				System.out.println("First call of getConnectionFromPool() start " + (System.currentTimeMillis()-startTime) +  " " + Thread.currentThread());
				rwConnectionPool.getConnectionFromPool();
				System.out.println("Second call of getConnectionFromPool() start " + (System.currentTimeMillis()-startTime) + " " + Thread.currentThread());
				rwConnectionPool.getConnectionFromPool();
				System.out.println("Thread returns " +(System.currentTimeMillis()-startTime)+  Thread.currentThread());
			}}.start();
		
		}
	}
}

class RederWriterPool{
	
	ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
	
	Map<Thread, Connection> connectionMap= new HashMap<>();
	
	
	/*
	 * This method acuires a reader-lco and  gets a connection from the pool.
	 * This connection is specific for the calling Thread
	 * If such a connection does not exist yet in the pool, it will be created. 
	 * For the creation of the new connection, the internal method createNewConnection() is called, which 
	 * acquires the writer-lock.
	 */
	// Shared access
	public Connection getConnectionFromPool(){
		
		// Get read lock
		rwLock.readLock().lock();
		
		Connection threadConnection;
		
		try{
			
			// lookup if there is already a connection available for the calling thread
			if(connectionMap.containsKey(Thread.currentThread())){
			
				
				
				threadConnection = 
						connectionMap.get(Thread.currentThread());
				
				
			}
			else {	
				// Release the read lock!! Important, as upgrade from read-lock to write lock is not possible
				rwLock.readLock().unlock();
				
				// if pool empty, call createNewConnection() with unlocked read lock!
				threadConnection = createNewConnection();
				
				// reacquire the read lock
				rwLock.readLock().lock();
			}
			
			return threadConnection;
			
		}finally{
			rwLock.readLock().unlock();
		}
	}
	
	/*
	 * Exclusive access
	 * 
	 * It is IMPORTANT that the read lock is released before the write lock is acquired!
	 * From the Javadoc:
	 *  * write lock. However, upgrading from a read lock to the write lock is <b>not</b> possible.
	*/ 
	private Connection createNewConnection(){
		
		// Acquire lock for writing (exclusive)
		rwLock.writeLock().lock();
		
		Connection connection;
		
		try{
			
			// TODO create connection to DB 
			connection = new Connection();
			
			// artificial delay:
			try{Thread.sleep(5000);}catch(Exception e){throw new Error();};

			
			// Insert new DB-Connection into the pool
			Connection oldConn = 
					connectionMap.put(Thread.currentThread(), connection);
			
			if(oldConn != null) {
				throw new Error("Illegal: Connection for Thread already Existed: " + Thread.currentThread());
			}
		
		}finally{
			rwLock.writeLock().unlock();
		}
		
		return connection;
	}
}

// Represents a DB Connection for a specific Thread
class Connection{
	
}

/////////////// SHORT VERSION ////////////////////////////////////////////////////////////////////////////////////
//Connection Pool with Reader-Writer-Lock 
class ReaderWriterPoolShort {

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
	
	
	public static void runTest (){
		final long startTime = System.currentTimeMillis();
		
		// Test Read-Write behavior
		final ReaderWriterPoolShort rwConnectionPool = new ReaderWriterPoolShort();
		
		
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


