// Copyright Marwan Abu-Khalil 2012

package seminar.exercises.thread.monitor.producer_consumer_monitor.solution_alternatives;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/*
 
 Musterlösung für Uebung 2.6c. Die anderen Teile von 2.6 sind in der Klasse ist in der Klasse ProducerConsumer realisiert
 Man sieht hier, dass die Verwendung der BlockingQueue<Integer> sehr viel Code überflüssig macht, der in der
 Klasse ProducerConsumer von Hand geschrieben werden musste.
 */
public class ProducerConsumerSolution_3_LibQueue {

	BlockingQueue<Integer> blockingQueue = new LinkedBlockingQueue<Integer>(100);
	
	public static void  main(String[] args) throws Exception {
		ProConLibQueueThreadTest.doTest();
	}
}


// Testcode für ProducerConsumerLibQueue
class ProConLibQueueThreadTest {
	
	final ProducerConsumerSolution_3_LibQueue proConQueue = new ProducerConsumerSolution_3_LibQueue();

	static void doTest() throws InterruptedException{
		ProConLibQueueThreadTest testInstance = new ProConLibQueueThreadTest();
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
						proConQueue.blockingQueue.put(producedItem++);
						
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
						int consumedItem = proConQueue.blockingQueue.take();
						System.out.println("Consuming item: " +  consumedItem + " Thread " + Thread.currentThread().getId());
					} catch (InterruptedException e) {
						throw new Error(e);
					}
				}
			}
		})).start();
	}
}

