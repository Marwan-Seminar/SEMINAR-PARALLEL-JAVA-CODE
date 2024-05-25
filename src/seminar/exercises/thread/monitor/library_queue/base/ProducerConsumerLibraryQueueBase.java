// Copyright Marwan Abu-Khalil 2019

package seminar.exercises.thread.monitor.library_queue.base;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;


/*
	Producer-Consumer mit blockierender Queue aus der Library
	
	Lernziel: Moitor-Benutzung, Producer-Consumer-Implementierung und die Funktionsweise blockierender Queues aus den Bibliotheken verstehen.
*/


// Demonstrates, that BlockingQueue integrates all Monitor-Logic: Easy to use
public class ProducerConsumerLibraryQueueBase {
	public static void main(String[] args) throws Exception {

		ProConLibQueueTest testInstance = new ProConLibQueueTest();
		
		testInstance.startProducingAndConsuming();

	}
}


class ProConLibQueueTest {
	
	void startProducingAndConsuming(){
		
		Thread producer = new Thread( () ->{
			int newProduct = 0;
			while(true) {
				// TODO Put new product into Queue
				
				System.out.println("Producer produced: " +newProduct  + " Thread " + Thread.currentThread());
				
			}		
		});
		
		Thread consumer = new Thread( () ->{
			
			while(true) {
				// TODO: take next product out of queue
				int consumedItem = 0; // 
				
				System.out.println("Consumer consumed: " +consumedItem + " Thread " + Thread.currentThread());
			
			}
		});
		
		producer.start();
		consumer.start();
	}
	
	
	
	
}

