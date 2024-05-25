package seminar.exercises.thread.synchronization.linked_list_inconsistent.solution;

import java.util.LinkedList;

/*
 * Lösung zur Übung  Synch-1: Linked-List inskonsistent
 * a). Zeigen Sie, dass eine Linked-List aus dem JDK inkonsistent wird, wenn mehrere Threads simultan Daten einfügen.
 * b). Reparieren Sie das Verhalten
 * 
 * Hinweis: 
 * zu a) Um die Inkonsistenz nachzuweisen, können Sie z.B. die Linked-List mit toString() auf die Shell schreiben.
 * zu b) Den Zugriff auf die List mit synchronized schützen
 * 
 * Lernziel: Erkennen, dass Inkonsistenzen leicht entstehen und fatale Folgen haben können
 */
public class LinkedListInconsistentSolution {

	
	LinkedList<Integer> linkedList = new LinkedList<Integer>();
	
	public static void main(String[] args) throws InterruptedException {
		System.out.println("LinkedListInconsistentSolution");
		
		LinkedListInconsistentSolution instance= new LinkedListInconsistentSolution();
				
		instance.runTest();

	}

	
	/*
	 * Runs threads and after a while checks the consitency of the LinkedList
	 * by calling toString() on it.
	 */
	void runTest() throws InterruptedException {
		
		startTwoWriterThreads();
		
		// TODO einkommentieren, List auf die Shell schreiben
		System.out.println(linkedList);
	}


	/*
	 * Creates and starts two threads that write simultaneously into the LinkeList
	 */
	void startTwoWriterThreads() throws InterruptedException {
		Thread writer_one = new Thread(
			() -> { for(int i = 0; i < 1000; ++i)
				{
					// TODO b) synchronisieren
					//synchronized(this) {
						this.linkedList.add(i);
					//}
				}
			}
		);
		Thread writer_two = new Thread(
			() -> { for(int i = 0; i < 1000; ++i)
				{
					// TODO b) synchronisieren
					//synchronized(this) {
						this.linkedList.add(i);
				}	//}
			}
		);
		
		writer_one.start();
		writer_two.start();
		writer_one.join();
		writer_two.join();
	}
}
