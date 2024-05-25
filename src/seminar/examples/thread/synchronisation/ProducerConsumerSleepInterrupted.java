// Copyright Marwan Abu-Khalil 2012

package seminar.examples.thread.synchronisation;

import java.util.LinkedList;
import java.util.Queue;

// Failed attempt to implement Producer-Consumer without Monitor.
// Based on the use of Thread.interrupt() and Thread.sleep().
// IS NOT CORRECT, crashes depending on speed of producer vs. Consumer with
// Exception in thread "Thread-1" java.lang.Error: Unexpected Queue State, EMPTY! 
// or
// java.lang.OutOfMemoryError
public class ProducerConsumerSleepInterrupted {

	volatile Thread waitingThread = null;
	Queue<Integer> queue = new LinkedList<Integer>();

	void produce() {

		int i = 1;
		while (true) {
			synchronized (queue) {
				Integer nextItemProduced = new Integer(i++);
				queue.offer(nextItemProduced);
				//System.out.println("Producer produced item " + nextItemProduced);
				if (waitingThread != null) {
					waitingThread.interrupt();
					System.out.println("Producer called interrupt");
				}
			}
			try{ Thread.sleep(1);} catch(Exception e){ throw new Error();}
			
			
		}
	}
	
	void consume(){
		while(true){
			
			synchronized(queue){
				
				
				if(queue.isEmpty()){
					waitingThread = Thread.currentThread();
				}
			}
			
			if(waitingThread == Thread.currentThread()){
				// Queue empty, wait:
				try{
					System.out.println("Consumer going to sleep");
					Thread.sleep(Integer.MAX_VALUE);
					System.out.println("Consumer woke up from sleep");
				}catch(InterruptedException e){
					System.out.println("Woke up via InterruptedException. Queue-Size: " + queue.size());
				}
				waitingThread = null;
			}
			
			synchronized(this){
				Integer nextItem = queue.poll();
				if(nextItem==null){
					throw new Error("Unexpected Queue State, EMPTY!");
				}
				System.out.println("Consuming item : " + nextItem);
			}
		}
	}
	
	public static void main(String[] args){
		
		final ProducerConsumerSleepInterrupted instance = new ProducerConsumerSleepInterrupted();
		
		// start producer
		new Thread(){
			public void run(){
				instance.produce();
			}
		}.start();
		
		// start consumer
		new Thread(){
			public void run(){
				instance.consume();
			}
		}.start();
	}
}