// Copyright Marwan Abu-Khalil 2012

package seminar.exercises.thread.monitor.producer_consumer_monitor.solution_alternatives;

import java.util.LinkedList;
import java.util.Queue;

/* Musterlösung für Uebung 2.6 b.  

	Diese Lösung zeigt, dass man bei Verwendung mehrerer Consumer und Producer
	notifyAll()- Aufrufe optimieren kann, indem man sie nur beim Zustandsübergang aufruft.
 */
class ProducerConsumerSolution_1_Many {
	Queue<Integer> queue = new LinkedList<Integer>();

	final int MAX_ELEMENTS = 100;

	// Consumes elements from the queue. Blocks If queue empty
	synchronized int consume() throws InterruptedException {
		// Monitor usage idiom
		while (queue.isEmpty()) {
			System.out.println("Consumer Waiting");
			this.wait();
		}
		
		// Wake waiting consumer, if this the queue is full the last element
		if(queue.size() >= 100){
			// notifyAll is important! notify() can lead to deadlock in this case.
			this.notifyAll();
		}
	
		// Now queue contains elements
		int nextElement = queue.poll();
		return nextElement;
	}

	synchronized void produce(int producedItem) throws InterruptedException {
		while (queue.size() >= MAX_ELEMENTS) {
			System.out.println("Producer Waiting");
			// Queue is full
			this.wait();
		}
		// Wake waiting consumer, if queue empty
		if(queue.size() == 0){
			this.notifyAll();
		}
		
		// Now, queue has space
		queue.add(producedItem);

		
	}
	
	public static void  main(String[] args) throws Exception {
		ProducerConsumerTest.doTest();
	}

}

class ProducerConsumerTest {
	final ProducerConsumerSolution_1_Many proConQueue = new ProducerConsumerSolution_1_Many();

	public static void doTest() throws Exception {

		ProducerConsumerTest testInstance = new ProducerConsumerTest();
		final int NR_OF_PROD_AND_CON = 4;
		for(int proConCount = 0; proConCount < NR_OF_PROD_AND_CON; ++ proConCount){
			testInstance.startProduction();
			testInstance.startConsumption();
		}

	}
	
	void startProduction()throws InterruptedException {
		(new Thread(new Runnable() {
			public void run() {
				int producedItem = 0;
				while (true) {
					try {
						proConQueue.produce(producedItem++);
						System.out.println("Producing item: " +  producedItem + " Thread " + Thread.currentThread().getId());
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
						System.out.println("Consuming item: " +  consumedItem + " Thread " + Thread.currentThread().getId());
					} catch (InterruptedException e) {
						throw new Error(e);
					}
				}
			}
		})).start();
	}
}
