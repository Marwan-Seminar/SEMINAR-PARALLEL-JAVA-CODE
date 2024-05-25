package seminar.exercises.thread.synchronization.list_writer.base;

import java.util.LinkedList;
import java.util.ListIterator;
import java.util.concurrent.ThreadLocalRandom;

/*
 * Exercise List-Writer
 * 
 * This Program Throws an Exception.
 * 
 * Fix the program, without changing the "intended" behavior
 */
public class ListWriterTest {
	
	public static void main(String[] args) throws InterruptedException {
		ListWriterTest instance = new ListWriterTest();
		instance.startThreads();
	}

	void startThreads() throws InterruptedException{
		
		System.out.println("List Writer Test Starting");
		
		Thread writer_1 = new Thread(() ->  {
			ListWriter listWriter = new ListWriter();
			listWriter.write();
			listWriter.printList();
		});
		
		Thread writer_2 = new Thread(() ->  {
			ListWriter listWriter = new ListWriter();
			listWriter.write();
			listWriter.printList();
		});
		
		writer_1.start();
		writer_2.start();
		
		writer_1.join();
		writer_2.join();
		
		
		System.out.println("ListWriter Test finished");
		
	}
	
	
	
}


class ListWriter{
	
	static LinkedList<String> list = new LinkedList<String>();
	
	synchronized void  write(){
		for(int i = 0; i < 100; ++i) {
			list.add(String.valueOf(ThreadLocalRandom.current().nextInt()));
		}
		
	}
	
	void printList() {
		ListIterator<String> iter = list.listIterator();
		while(iter.hasNext()) {
			System.out.println(iter.next());
		}
	
	}
}