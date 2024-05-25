// Copyright Marwan Abu-Khalil 2012

package seminar.examples.thread.procon;

import java.util.LinkedList;
import java.util.Queue;

/*
 *  This implementation has several shortcomings
 *  1. Unnecessary notify() calls are issued. (inefficient)
 *  2. If this was fixed by checking the condition before calling notify()
 *  	the implementation could not handle many producers or consumers. 
 *  	notifyAll() must be used to fix this.
 */

public class ProducerConsumer {

	public static void main(String[] args) throws Exception {

		ProConThreadTest testInstance = new ProConThreadTest();
		testInstance.startProduction();
		testInstance.startConsumption();

	}
}

class ProConQueue {
	Queue<Integer> queue = new LinkedList<Integer>();

	final int MAX_ELEMENTS = 100;

	// Consumes elements from the queue. Blocks If queue empty
	synchronized int consume() throws InterruptedException {
		// Monitor usage idiom
		while (queue.isEmpty()) {
			System.out.println("Consumer Waiting");
			this.wait();
		}
		// Now queue contains elements
		int nextElement = queue.poll();

		// Wake waiting consumer
		this.notify();

		return nextElement;
	}

	synchronized void produce(int producedItem) throws InterruptedException {
		while (queue.size() >= MAX_ELEMENTS) {
			System.out.println("Producer Waiting");
			// Queue is full
			this.wait();
		}
		// Now, queue has space
		queue.add(producedItem);

		// Wake waiting consumer
		this.notify();
	}
}

class ProConThreadTest {
	final ProConQueue proConQueue = new ProConQueue();

	void startProduction()throws InterruptedException {
		(new Thread(new Runnable() {
			public void run() {
				int producedItem = 0;
				while (true) {
					try {
						proConQueue.produce(producedItem++);
					} catch (InterruptedException e) {
						throw new Error(e);
					}
				}
			}
		})).start();
	}

	void startConsumption() {
		(new Thread(new Runnable() {
			public void run() {
				while (true) {
					try {
						int consumedItem = proConQueue.consume();
						// System.out.println("Consuming item: " +
						// consumedItem);
					} catch (InterruptedException e) {
						throw new Error(e);
					}
				}
			}
		})).start();
	}
}
