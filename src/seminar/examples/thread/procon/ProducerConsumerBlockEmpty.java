// Copyright Marwan Abu-Khalil 2012

package seminar.examples.thread.procon;

import java.util.Deque;
import java.util.LinkedList;

// A simple Producer-Consumer, blocks only if queue empty
public class ProducerConsumerBlockEmpty {
	Deque<Integer> queue = new LinkedList<Integer>();

	void produce(){
		new Thread(){ public void run(){
			int i = 1;
			while(true){
				synchronized(queue){
					queue.offer(i++);
					if(queue.size() == 1){
						queue.notify();
					}
				}
			}
		}}.start();	
	}
	void consume(){
		new Thread(){ public void run(){
			while(true){
				synchronized(queue){
					while(queue.isEmpty()){
						try {
							queue.wait();
						} catch (InterruptedException e) {
							throw new Error(e);
						}
					}
					Integer nextItem = queue.poll();
					System.out.println("Consuming Item: " + nextItem);
				}
			}
		}}.start();
	}
	
	public static void main(String[] args){
		ProducerConsumerBlockEmpty instance = new ProducerConsumerBlockEmpty();
		
		instance.produce();
		instance.consume();
	}
}
