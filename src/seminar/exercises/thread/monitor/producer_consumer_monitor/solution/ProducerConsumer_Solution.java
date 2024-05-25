// Copyright Marwan Abu-Khalil 2012

package seminar.exercises.thread.monitor.producer_consumer_monitor.solution;

import java.util.LinkedList;
import java.util.Queue;

/*
	Uebung Producer-Consumer

Uebung Producer-Consumer

	Konzipieren Sie ein Producer-Consumer Programm, das folgendes leistet:
		-	Ein Thread (Producer) fuellt Items, Objekte oder Zahlen, in eine Queue. Wenn die Queue 
		 	voll ist (100 Elemente enthaelt), so blockiert dieser Thread. Der Producer wacht wieder auf, 
		 	sobald Platz in der Queue ist (d.h. sobald die Queue weniger als 100 Elemente enthaelt). 
		-	Ein anderer Thread (Consumer), entnimmt die Items aus der Queue. Wenn die Queue leer ist,
		 	blockiert dieser Thread. Der Consumer wacht wieder auf, sobald es neue Items in der Queue gibt.
	
	
	Lernziel: Monitor-Benutzung, Producer-Consumer-Implementierung 
*/
public class ProducerConsumer_Solution {
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
 * It contains the Monitor Logic to protect the Queue.
 */
class ProConQueue {
	Queue<Integer> queue = new LinkedList<Integer>();

	final int MAX_ELEMENTS = 100;

	// Consumes elements from the queue. Blocks If queue empty
	synchronized int consume() throws InterruptedException {
		// Monitor usage idiom
		while (queue.isEmpty()) {
			System.out.println("Consumer waiting");
			this.wait();
			System.out.println("Consumer woke up");
		}

		// Now queue contains elements
		int nextElement = queue.poll();

		
		this.notify();
		//this.notifyAll();
		
		return nextElement;
	}

	synchronized void produce(int producedItem) throws InterruptedException {
		while (queue.size() >= MAX_ELEMENTS) {
			System.out.println("Producer waiting");
			// Queue is full
			this.wait();
			System.out.println("Producer woke up");
		}
		
		// Now, queue has space
		queue.add(producedItem);

		// Wake waiting consumers, if the element produced is the only element in the queue
		this.notify();
		//this.notifyAll();
		
	}
}



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
						"Producing item: " + producedItem +
						" Queue Size "+ proConQueue.queue.size() +
						" ThreadID: " + Thread.currentThread().getId());
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
				
				System.out.println(
						"Consuming item: " + consumedItem +
						" Queue Size "+ proConQueue.queue.size() +
						" ThreadID: " + Thread.currentThread().getId());
			} catch (InterruptedException e) {
				throw new Error(e);
			}	
		}
	}
}


