// Copyright Marwan Abu-Khalil 2012

package seminar.exercises.thread.monitor.producer_consumer_condition_variable.base;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;


/*
 	Übung:  Producer-Consumer mit expliziten Codition Varaiblen

*/

// Producer-Consumer for any number of Threads.
// Demonstrates the need for notifyAll()
// If the two notifyAll() calls are substituded by noitfy(): DEADLOCK!!!
public class ProducerConsumerConditionVaraibleBase {
	public static void main(String[] args) throws Exception {

		ProConThreadTest testInstance = new ProConThreadTest();
		testInstance.startProduction();
		testInstance.startConsumption();

	}
}

class ProConQueue {
	
	Queue<Integer> queue = new LinkedList<Integer>();

	// TODO: Set up explicit lock and two explicit condition variables
	
	final int MAX_ELEMENTS = 100;

	// Consumes elements from the queue. Blocks If queue empty
	int consume() throws InterruptedException {
		
		// TODO  Explicit Lock allocation
		
		// Monitor usage idiom
		while (queue.isEmpty()) {
			
			
			// TODO  use specific condition variable to wait for Signal by Producer to wake up when queue is no more empty
			
			
		}
		
		// Now queue contains elements
		int nextElement = queue.poll();
		System.out.println("Consumer consumed: " + nextElement + " " + Thread.currentThread());
			
		// TODO notify potentially waiting producers by use of their specific Condition-Variable 
	
		
		// TODO Explicit unlock

		
		return nextElement;
	}

	void produce(int producedItem) throws InterruptedException {
	
		// TODO Explicit Lock allocation
		
		while (queue.size() >= MAX_ELEMENTS) {
			
			// Queue is full
			// TODO wait while queue full
		}
		
		// Now, queue has space
		queue.add(producedItem);
		System.out.println("Producer produced: " + producedItem + " " + Thread.currentThread());
		
		// TODO Signal potentially waiting Consumers on their specific Condition-Varaible
		
	
		// TODO Explicit unlock
		
	}
	
	
}

class ProConThreadTest {
	final ProConQueue proConQueue = new ProConQueue();
	final int NR_OF_PRODUCRES = 1;
	final int NR_OF_CONSUMERS = 1;
	
	
	void startProduction()throws InterruptedException {
		for(int producerCount = 0; producerCount < NR_OF_PRODUCRES; producerCount++){
			(new Thread(new Runnable() {
				public void run() {
					int producedItem = 0;
					while (true) {
						try {
							proConQueue.produce(producedItem++);
							//System.out.println("Producing item: " + producedItem + " ThreadID: " + Thread.currentThread().getId());
						} catch (InterruptedException e) {
							throw new Error(e);
						}
						
						// Yield
						Thread.yield();
					}
				}
			})).start();
		}
	}

	void startConsumption() {
		for(int consumerCount = 0; consumerCount < NR_OF_CONSUMERS; consumerCount++){
			(new Thread(new Runnable() {
				public void run() {
					while (true) {
						try {
							int consumedItem = proConQueue.consume();
								//System.out.println("Consuming item: " + consumedItem + " ThreadID: " + Thread.currentThread().getId());
						} catch (InterruptedException e) {
							throw new Error(e);
						}
						
						// Yield
						Thread.yield();
					}
					
				}
			})).start();
		}
	}
	
	
}

