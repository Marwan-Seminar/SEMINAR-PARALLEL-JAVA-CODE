package seminar.exercises.thread.parallel.hello_world_threads.base;


/*
 * Uebung 1 Triviale Parallelisierung mit Threads: „Hello World“ 
 * 
 * Schreiben Sie ein Programm mit zwei Threads. Ein Thread schreibt das Wort „Hello“ auf
 * die Konsole, der andere das Wort „World“.
 * 
 * a ) Die Threads schreiben in beliebiger Abfolge
 * 
 * b) Versuchen Sie, die Lösung von a) so verfeinern, dass die Worte immer
 * genau abwechselnd ausgeschrieben werden (ohne wait() und notify() zu
 * verwenden)
 * 
 * c) Verwenden Sie die Monitor API (wait() und notitfy()), um die Loesung b) so zu verbessern,
 * dass kein Busy Waiting mehr stattfindet.
 * 
 * Lernziel: Thread API kennenlernen, beobachten, dass das OS die Threads
 * abwechselnd bzw. gleichzeitig schedult. Unterschied zwischen Busy-Waiting und Monitor erkennen.
 */
public class HelloWorldThreadsBase {

	public static void main(String[] args) throws InterruptedException {
		
		HelloWorldThreadsBase instance = new HelloWorldThreadsBase();
		// a
		instance.startTwoSimpleThrads();
	
	}
	
	void startTwoSimpleThrads(){
	
		// TODO introduce Threads here, to make the two print loops run in parallel
		printLoop("Hello");
		
		printLoop("World");
	}

	void printLoop(String text){
		while(true){
			System.out.println(text);

		}
	}
	
}



/* HINTS FOR THE IMPLEMENTATION */
/*
How to start new Threads?

1. Declare a class that extends Thread

class MyThread extends Thread{
	
	public void run(){
		// This code is executed concurrently
	}

	
}


2. Create an instance of this class and call its start() method
new MyThread().start();

OR: 3. short form

 */


