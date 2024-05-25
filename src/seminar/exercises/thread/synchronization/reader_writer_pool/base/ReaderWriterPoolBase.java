// Copyright Marwan Abu-Khalil 2012

package seminar.exercises.thread.synchronization.reader_writer_pool.base;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/* Connection Pool with Reader-Writer-Lock
 * 
 * Übung: Connection-Pool mit Reader-Writer Lock schreiben
 * 
 * Connection Aufbau im write-Teil
 * 
 * Bauen Sie auf Basis des ReaderWriterLocks einen Connection-Pool für Datenbank-Connections.
 * 
 * Der Connection-Pool enthält eine Map, die Thread zu Connection zuordnet.
 * 
 * Wenn ein Thread mit getConnection() anfragt, dann wird zunächst in der Map geschaut,
 * ob es eine Connection für diesen Thread gibt, wenn nicht, dann wird eine erzeugt.
 * 
 * getConnection() darf von mehreren Threads gleichzeitig ausgefürht werden,
 * weil das Lesen der HashMap parallel ohne Synchronisation möglich ist.
 * 
 * Das Erzeugen findet in der Funktion createConnection() statt. Diese darf jedoch
 * nur von einem Einzigen Thread gleichzeitig ausgeführt werden. 
 * 
 * Bemerkung: Hier werden zwei Critical-Sections vermischt,
 * die eine ist das Einfügen in die HashMap, die andere der Connection-Aufbau zur Datenbank. 
 * Das dient der Vereinfachung.
*/
public class ReaderWriterPoolBase {



	public static void main(String[] args){
		final long startTime = System.currentTimeMillis();
		// Test Read-Write behavior
		final RederWriterPool rwConnectionPool = new RederWriterPool();
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

class RederWriterPool{
	
	ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
	
	Map<Thread, Connection> connectionMap= new HashMap<>();
	
	/*
	 * Exclusive access
	*/ 
	Connection createNewConnection(){
		
		Connection connection;
	
		
		// TODO create connection to DB 
		connection = new Connection();
		
		// artificial delay:
		try{Thread.sleep(5000);}catch(Exception e){throw new Error();};

		
		//  Insert new DB-Connection into the pool
		Connection oldConn = 
				connectionMap.put(Thread.currentThread(), connection);
		
		if(oldConn != null) {
			throw new Error("Illegal: Connection for Thread already Existed: " + Thread.currentThread());
		}
		
		
		
		return connection;
	}
	
	// Shared access
	Object getConnectionFromPool(){
		
		Connection threadConnection;
		
			
		// lookup if there is already a connection available for the calling thread
		if(connectionMap.containsKey(Thread.currentThread())){
					
			threadConnection = 
					connectionMap.get(Thread.currentThread());	
		}
		else {	
			
			// if pool empty, call createNewConnection() with unlocked read lock!
			threadConnection = createNewConnection();
			
		}
		
		return threadConnection;
		
	}
}

// Represents a DB Connection for a specific Thread
class Connection{
	
}
