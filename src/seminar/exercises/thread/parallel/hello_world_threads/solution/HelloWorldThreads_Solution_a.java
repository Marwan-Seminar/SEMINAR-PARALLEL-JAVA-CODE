// Copyright Marwan Abu-Khalil 2012

package  seminar.exercises.thread.parallel.hello_world_threads.solution;

/*
 * Lösung a) Die Threads schreiben in beliebiger Abfolge
 * 
 * Uebung:
 * Triviale Parallelisierung mit Threads: „Hello World“ 
 * 
 */
public class HelloWorldThreads_Solution_a {

	public static void main(String[] args) throws InterruptedException {
		
		HelloWorldThreads_Solution_a instance = new HelloWorldThreads_Solution_a();
		// a
		instance.startTwoSimpleThrads();
	
		// b siehe  class HelloWorldTakingTurns

	}
	
	void startTwoSimpleThrads(){
		new SimplePrintThread("Hello").start();
		new SimplePrintThread("World").start();
		
	}

}

class SimplePrintThread extends Thread{
	String text;
	
	SimplePrintThread(String text){
		this.text = text;		
	}
	
	public void run(){
		while(true){
			System.out.println(text);
		}
	}
}




