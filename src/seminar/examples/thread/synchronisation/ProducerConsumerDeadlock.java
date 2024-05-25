// Copyright Marwan Abu-Khalil 2012

package seminar.examples.thread.synchronisation;

import java.util.LinkedList;
import java.util.Queue;

// ILL FORMED PROGRAM!!! DEADLOCK, DONT DO THIS

// Failed attempt to implement consumer-producer.
// Consumer deadlocks producer, because it does not release
// the mutex while sleeping
// For failed attempts to fix this, see ProducerConsumerSleepInterrupted
public class ProducerConsumerDeadlock {

	Queue<Integer> queue = new LinkedList<Integer>();

	Thread consumerThreadWaiting;

	void produce(){
		int item = 0;
		while(true){
			System.out.println("Producer waiting to enter critical section");
			synchronized(queue){
				System.out.println("Producer successfully entered critical section");
				queue.offer(new Integer(item++));
				if (consumerThreadWaiting != null){
					consumerThreadWaiting.interrupt();
				}
			}
		}
	}

	void consume(){
		while(true){
			synchronized(queue){
				if(queue.isEmpty()){
					try{
						consumerThreadWaiting = Thread.currentThread();
						Thread.sleep(Integer.MAX_VALUE);
					}catch(InterruptedException e){
						consumerThreadWaiting = null;
						System.out.println("Consumer woke up");
					}
				}
				Integer nextItem = queue.poll();
				System.out.println("Consumer got next item:  " + nextItem);
			}
		}
	}

	public static void main(String[] args){
		final ProducerConsumerDeadlock instance = new ProducerConsumerDeadlock();
		// producer thread
		new Thread(){public void run(){instance.produce();}}.start();
		// consumer thread
		new Thread(){public void run(){instance.consume();}}.start();

	}

}
