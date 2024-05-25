// Copyright Marwan Abu-Khalil 2019

package seminar.exercises.thread.monitor.library_queue.solution;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;


/*
 	Producer-Consumer mit blockierender Queue aus der Library
	
	Lernziel: Moitor-Benutzung, Producer-Consumer-Implementierung und die Funktionsweise blockierender Queues aus den Bibliotheken verstehen.
*/

// Demonstrates, that BlockingQueue integrates all Monitor-Logic: Easy to use
public class ProducerConsumerLibraryQueueSolution {
	public static void main(String[] args) throws Exception {

		ProConLibQueueTest testInstance = new ProConLibQueueTest();
		
		testInstance.startPoducingAndConsuming();

	}
}


class ProConLibQueueTest {
	
	
	void startPoducingAndConsuming(){
		
		BlockingQueue<Integer> proConQueue = new ArrayBlockingQueue<>(100);
		
		Thread producer = new Thread( () ->{
			int newProduct = 0;
			while(true) {
				try {
					proConQueue.put(newProduct++);

					System.out.println("Producer produced: " +newProduct + " Queue Size: " + proConQueue.size() + " Thread " + Thread.currentThread());
				} catch (InterruptedException e) {
					throw new Error(e);
				}
			}
			
		});
		
		Thread consumer = new Thread( () ->{
			
			while(true) {
				try {
					int consumedItem = proConQueue.take();
					System.out.println("Consumer consumed: " +consumedItem + " Queue Size: " + proConQueue.size() + " Thread " + Thread.currentThread());
				} catch (InterruptedException e) {
					throw new Error(e);
				}
			}
		});
		
		producer.start();
		consumer.start();
	}
	
	
	
	
}

