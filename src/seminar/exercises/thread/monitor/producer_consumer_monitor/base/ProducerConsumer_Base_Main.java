// Copyright Marwan Abu-Khalil 2012

package seminar.exercises.thread.monitor.producer_consumer_monitor.base;

import java.util.LinkedList;
import java.util.Queue;


/*
 	Uebung Producer-Consumer

	Konzipieren Sie ein Producer-Consumer Programm, das folgendes leistet:
		-	Ein Thread (Producer) fuellt Items, Objekte oder Zahlen, in eine Queue. Wenn die Queue 
		 	voll ist (100 Elemente enthaelt), so blockiert dieser Thread. Der Producer wacht wieder auf, 
		 	sobald Platz in der Queue ist (d.h. sobald die Queue weniger als 100 Elemente enthaelt). 
		-	Ein anderer Thread (Consumer), entnimmt die Items aus der Queue. Wenn die Queue leer ist,
		 	blockiert dieser Thread. Der Consumer wacht wieder auf, sobald es neue Items in der Queue gibt.
	
	
	Lernziel: Monitor-Benutzung, Producer-Consumer-Implementierung 

*/
public class ProducerConsumer_Base_Main {
	public static void main(String[] args) throws Exception {

		// Create Queue
		ProConQueue proConQueue = new ProConQueue();
		
		// Start Consumer Thread
		ConsumerThread consumerThread = new ConsumerThread(proConQueue);
		consumerThread.start();	
			
		// Start Producer Thread
		ProducerThread producerThread = new ProducerThread(proConQueue);
		producerThread.start();

	}
}

/*
 * This class realizes the Queue
 * It should contain the Monitor Logic to protect the Queue.
 */
class ProConQueue {
	Queue<Integer> queue = new LinkedList<Integer>();

	final int MAX_ELEMENTS = 100;

	// Consumes elements from the queue. Blocks If queue empty
	int consume() throws InterruptedException {
		
		int nextElement = -1;
		
		// TODO Make sure, that queue contains an element, if not wait. Use Monitor wait()
		
		// TODO Now queue should contains elements, take one and remove it: (if poll() is called on an empty queue: Exception)
		// nextElement = queue.poll();

		// TODO: Wake eventually waiting producer(s). HINT: Use Monitor notify()
		
		return nextElement;
	}

	void produce(int producedItem) throws InterruptedException {
		
		// TODO: make sure, there is space in the Queue, this means queue.size() < MAX_ELEMENTS.
		// If this is not the case, wait. Use Monitor wait()
	
		// Now, queue has space
		queue.add(producedItem);

		// TODO Wake up eventually waiting consumer

		
	}
	
}

/* HINT: Usage of Monitor wait() / notify()

	synchronized(this){
		
		while("Queue Full"){
			
			this.wait();
		
		}
		
		this.notify();
	}

 
*/

class ProducerThread extends Thread{
	ProConQueue proConQueue;
	
	ProducerThread(ProConQueue queue){
		this.proConQueue = queue;
	}
	
	public void run(){
		int producedItem = 0;
		
		while (true) {
			try {
				
				// Send Item to Queue
				proConQueue.produce(producedItem++);
			
				System.out.println(
						"Producing item: " + producedItem + " ThreadID: " + Thread.currentThread().getId());
			} catch (InterruptedException e) {
				throw new Error(e);
			}
		}
	}
}

class ConsumerThread extends Thread{
	ProConQueue proConQueue;
	
	ConsumerThread(ProConQueue queue){
		this.proConQueue = queue;
	}
	
	public void run() {
		while (true) {
			try{	

				// Consume Item from Queue
				int consumedItem = proConQueue.consume();
				
				System.out.println("Consuming item: " + consumedItem + " ThreadID: " + Thread.currentThread().getId());
			} catch (InterruptedException e) {
				throw new Error(e);
			}	
		}
	}
}
