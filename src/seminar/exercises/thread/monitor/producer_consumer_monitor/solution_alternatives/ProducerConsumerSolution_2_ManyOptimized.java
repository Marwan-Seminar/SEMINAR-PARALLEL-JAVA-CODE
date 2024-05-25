// Copyright Marwan Abu-Khalil 2012

package seminar.exercises.thread.monitor.producer_consumer_monitor.solution_alternatives;

import java.util.LinkedList;
import java.util.Queue;


/*
 	Ü 2.6 Producer-Consumer

	a)	Konzipieren Sie auf dem Papier ein „Producer-Consumer Programm“, das folgendes leistet:
		-	Ein Thread (Producer) füllt „Items“, Objekte oder Zahlen, in eine Queue. Wenn die Queue 
		 	voll ist (100 Elemente enthält), so blockiert dieser Thread. Der Producer wacht wieder auf, 
		 	sobald Platz in der Queue ist (d.h. sobald die Queue weniger als 100 Elemente enthält). 
		-	Ein anderer Thread (Consumer), entnimmt die Items aus der Queue. Wenn die Queue leer ist,
		 	blockiert dieser Thread. Der Consumer wacht wieder auf, sobald es neue Items in der Queue gibt.
	
	b)	Programmieren Sie das Design aus a) (ohne Verwendung blockierender Queues aus den
	 	Libraries), so dass folgende Bedingungen erfüllt sind:
		-	Das Programm kann mit beliebig vielen Producern und Consumern umgehen
		-	Es werden keine unnötigen Weck-Aufrufe getätigt
		-	Kein Busy-Waiting
		-	Keine Datenstrukturen aus java.util.concurrent 
	
	c)	Vereinfachen Sie das Programm aus b) durch Verwendung einer blockierenden Queue aus einer Bibliothek.

	Lernziel: Moitor-Benutzung, Producer-Consumer-Implementierung und die Funktionsweise blockierender Queues aus den Bibliotheken verstehen.

*/

// Producer-Consumer for any number of Threads.
// Demonstrates the need for notifyAll()
// If the two notifyAll() calls are substituded by noitfy(): DEADLOCK!!!
public class ProducerConsumerSolution_2_ManyOptimized {
	public static void main(String[] args) throws Exception {

		ProConThreadTest testInstance = new ProConThreadTest();
		testInstance.startProduction();
		testInstance.startConsumption();

	}
}

class ProConQueueMany {
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

		// Wake eventually waiting producer(s)
		if(queue.size() == MAX_ELEMENTS -1){
			// why ist notfy() not enough? DEADLOCK!!! There could be several waiting producers.
			//this.notify();
			this.notifyAll();
		}
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
		if(queue.size()== 1){
			// see comment above in consume() regarding notify() vs. notifyAll()
			//this.notify();
			this.notifyAll();
		}
	}
}

class ProConThreadTest {
	final ProConQueueMany proConQueue = new ProConQueueMany();
	final int NR_OF_PRODUCRES = 4;
	final int NR_OF_CONSUMERS = 4;
	
	
	void startProduction()throws InterruptedException {
		for(int producerCount = 0; producerCount < NR_OF_PRODUCRES; producerCount++){
			(new Thread(new Runnable() {
				public void run() {
					int producedItem = 0;
					while (true) {
						try {
							proConQueue.produce(producedItem++);
							System.out.println("Producing item: " + producedItem + " ThreadID: " + Thread.currentThread().getId());
						} catch (InterruptedException e) {
							throw new Error(e);
						}
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
								System.out.println("Consuming item: " + consumedItem + " ThreadID: " + Thread.currentThread().getId());
						} catch (InterruptedException e) {
							throw new Error(e);
						}
					}
				}
			})).start();
		}
	}
	
	
}

