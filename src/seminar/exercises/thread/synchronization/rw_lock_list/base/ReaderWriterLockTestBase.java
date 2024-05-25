package seminar.exercises.thread.synchronization.rw_lock_list.base;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/*/
 * Demonstrates usage of ReentrantReadWriteLock
 * 
 * Schreiben Sie eine Klasse, die den Zugriff auf eine Datenstruktur (z.B. eine LinkedList oder HashMap) so schützt,
 * dass zwar viele Threads gleichzeitig aus ihr lesen können, aber nur jeweils ein Thread zu jeder
 * Zeit in diese Struktur schreiben kann. 
 * 
 * Nutzen Sie für die Synchronisation die Klasse util.concurrent. ReentrantReadWriteLock
 * 
 * Durch diese Art des Lockings lässt sich ein Performance-Vorteil erzielen, gegenüber einem Synchronisationsmodell,
 * bei dem zu jedem Zeitpunkt nur ein Thread die Datenstruktur benutzen darf. Weisen Sie diesen Performance-Vorteil nach,
 * indem Sie die Lese- und Schreib-Operationen künstlich verlangsamen (z.B. indem Sie ein Sleep oder eine 
 * CPU-intensive Schleife innerhalb der Critical Section  einbauen). 
 * 
 * 
 */
public class ReaderWriterLockTestBase {

	public static void main(String[] args) throws InterruptedException {
		
		ReaderWriterList rwList = new ReaderWriterList();
		
		// Two Writer Threads are required, to produce inconsistencies in unsynchronized case
		// Writer Thread 1
		Thread threadWriter_1 = new Thread(() ->  {
			for(int i = 0; i < 1000; ++i) {
				rwList.write();;
			}
		});
		
		// Writer Thread 2
		Thread threadWriter_2 = new Thread(() ->  {
			for(int i = 0; i < 1000; ++i) {
				rwList.write();;
			}
		});
		
		// Two reader Threads are required to prove perfomance advantage in Reader-Writer Lock case
		// Reader Thread
		Thread threadReader_1 = new Thread(() ->  {
			for(int i = 0; i < 1000; ++i) {
				rwList.read();
			}
		});
		
		// Reader Thread
		Thread threadReader_2 = new Thread(() ->  {
			for(int i = 0; i < 1000; ++i) {
				rwList.read();
			}
		});
		
		long startTime = System.currentTimeMillis();
		
		threadWriter_1.start();
		threadWriter_2.start();
		threadReader_1.start();
		threadReader_2.start();
		
		threadWriter_1.join();
		threadWriter_2.join();
		threadReader_1.join();
		threadReader_2.join();
		
		System.out.println("Time for Reader-Writer Threads: " +(System.currentTimeMillis() - startTime));
		System.out.println(rwList.data);
	}

}

class ReaderWriterList{
	
	List<Integer> data = new LinkedList<Integer>();

	ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();

	/*
	 * Reads and returns data at a random position of the list.
	 */
	 Integer read() {

		// artificial delay, to proove performance increase through Reader-Writer Lock in comparison to synchronized
		try {Thread.sleep(3);} catch (InterruptedException e) {throw new Error(e);}
		
		Integer returnValue;
		
		if(data.isEmpty()) {
			returnValue = null;
		}else {
			returnValue=
				data.get(Math.abs(ThreadLocalRandom.current().nextInt(data.size() )));
		}
		
		return returnValue;

	}
	 
	/* 
	 * Writes a random value at the end of the list
	 */
	 void write() {
		 
		 data.add(ThreadLocalRandom.current().nextInt());
		 
	 }	
}
