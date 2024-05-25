package seminar.exercises.thread.synchronization.list_writer.solution;

import java.util.LinkedList;
import java.util.ListIterator;
import java.util.concurrent.ThreadLocalRandom;

/*
 * Demonstrates locking errors due to the scope of the lock, 
 * for example confusing static and instance locking.
 * 
 * Problem description:
 * 
 * The list is static
 * 1. The write() function is a synchronized instance method
 * 2. The printList() method is not synchronized
 * 
 * The two threads operate originally on two different instances, therefore they call the 
 * write method simultaneously
 * 
 * Possible fixes
 *  Fix 1: use a single instance of LsitWriter in both Threads
 *  Fix 2: Change the write() method to be static
 * 
 *  additionally: synchronize printList()
 */
public class ListWriterTest {
	
	public static void main(String[] args) throws InterruptedException {
		ListWriterTest instance = new ListWriterTest();
		instance.startThreads();
	}

	void startThreads() throws InterruptedException{
		
		System.out.println("List Writer Test Starting");
		
		//Fix 1: use a single instance of LsitWriter in both Threads
		ListWriter listWriter = new ListWriter();
		
		Thread writer_1 = new Thread(() ->  {
			// Fix 1 : comment the following line
			//ListWriter listWriter = new ListWriter();
			listWriter.write();
			listWriter.printList();
		});
		
		Thread writer_2 = new Thread(() ->  {
			// Fix 1 comment the following line
			//ListWriter listWriter = new ListWriter();
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
	
	synchronized void printList() {
		//System.out.println("Printing List: of size:" + list.size());
		ListIterator<String> iter = list.listIterator();
		while(iter.hasNext()) {
			System.out.println(iter.next());
		}
	
	}
}