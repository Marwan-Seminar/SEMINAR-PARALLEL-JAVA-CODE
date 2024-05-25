package seminar.exercises.thread.synchronization.linked_list_inconsistent.base;

import java.util.LinkedList;

/*
 * Ausgangsbasis  zur Übung  Synch-1: Linked-List inskonsistent
 * a) Zeigen Sie, dass eine Linked-List aus dem JDK inkonsistent wird, wenn mehrere Threads simultan Daten einfügen.
 * b) Repareieren Sie das Verhalten
 * 
 * Hinweis: 
 * Um die Inkonsistenz nachzuweisen, können Sie z.B. die Linked-List mit toString() auf die Shell schreiben.
 * 
 * Lernziel: 
 * Erkennen, dass Inkonsistenzen leicht entstehen und fatale folgen haben können
 */
public class LinkedListInconsistentBase {

	// The linked List to be "destroyed" by unsynchronized access.
	LinkedList<Integer> linkedList = new LinkedList<Integer>();
	
	public static void main(String[] args) throws InterruptedException {
		System.out.println("LinkedListInconsistentBase");
		
		LinkedListInconsistentBase instance= new LinkedListInconsistentBase();
				
		instance.runTest();

	}

	
	/*
	 * Runs threads and after a while checks the consitency of the LinkedList
	 * by calling toString() on it.
	 */
	void runTest() throws InterruptedException {
		
		startTwoWriterThreads();
		
		// TODO: After the threads did return, 
		// write the linkedList to System.out to check if it is still consistent
	}


	/*
	 * Creates and starts two threads that write simultaneously into the LinkeList
	 */
	void startTwoWriterThreads() throws InterruptedException {
		Thread writer_one = new Thread(
			() -> {
				// TODO write into linked list 
			}
		);
		Thread writer_two = new Thread(
			() -> {
				// TODO write into linked list 
			}
		);
		
		// TODO: Start the threads
	}
}
