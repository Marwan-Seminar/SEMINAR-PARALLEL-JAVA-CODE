// Copyright Marwan Abu-Khalil 2012

package seminar.exercises.thread.monitor.producer_consumer_condition_variable.solution;

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
public class ProducerConsumerConditionVariableSolution {
	public static void main(String[] args) throws Exception {

		ProConThreadTest testInstance = new ProConThreadTest();
		testInstance.startProduction();
		testInstance.startConsumption();

	}
}

class ProConQueue {
	
	Queue<Integer> queue = new LinkedList<Integer>();

	ReentrantLock lock = new ReentrantLock();
	 
	Condition consumerCond = lock.newCondition(); 
	Condition producerCond = lock.newCondition();
	  
	boolean queueEmpty;
	boolean queueFull;

	
	final int MAX_ELEMENTS = 100;

	// Consumes elements from the queue. Blocks If queue empty
	int consume() throws InterruptedException {
		// Explicit Lock allocation
		lock.lock();
		
		// Monitor usage idiom
		while (queue.isEmpty()) {
			//System.out.println("Consumer waiting");
			
			// use specific condition varaible to wait for Signal by Producer
			consumerCond.await();
			
			//System.out.println("Consumer woke up");
		}
		
		// Now queue contains elements
		int nextElement = queue.poll();
		System.out.println("Consumer consumed: " + nextElement + " " + Thread.currentThread());
			
		// notify potentially waiting producers by use of threir specific Condition-Variable 
		producerCond.signalAll();
		
		// Explicit unlock
		lock.unlock();
		
		return nextElement;
	}

	void produce(int producedItem) throws InterruptedException {
	
		// Explicit Lock allocation
		lock.lock();
		
		while (queue.size() >= MAX_ELEMENTS) {
			//System.out.println("Producer waiting");
			// Queue is full
			producerCond.await();
			//System.out.println("Producer woke up");
		}
		// Now, queue has space
		queue.add(producedItem);
		System.out.println("Producer produced: " + producedItem + " " + Thread.currentThread());
		
		// Signal potentially waiting Consumers on their specific Condition-Varaible
		consumerCond.signalAll();
	
		// Explicit unlock
		lock.unlock();
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

