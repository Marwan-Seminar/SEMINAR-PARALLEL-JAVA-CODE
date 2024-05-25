// Copyright Marwan Abu-Khalil 2012
package  seminar.exercises.thread.parallel.hello_world_threads.solution;


/*
 *  Lösung b) zu Übung "Hello World Threads im Wechsel"
 *  
 *  Die Threads wecheln sich beim Schreiben ab, ohne synchronized oder Monitor zu verwenden. 
 */
public class HelloWorldThreads_Solution_b_TakingTurns {
	public static void main(String[] args){
		new TakingTurnsThread().startThreads();
	}
}

// Taking turns to print Hello or World. 
// No Monitor is used, but the price is: Busy Waiting! 
class TakingTurnsThread{
	
	// Used as Thread-Identifier. The Threads identify themselves as true or false. volatile is practically not required
	// if volatile is no used, a lifelock can occur
	volatile boolean lastOne = false;
	
	static final String HELLO = "Hello";
	static final String WORLD = "World";
	
	
	
	// The Hello Threads identifies himself as "true"
	void helloLoop() {
		
		while (true) {		
		
			while(lastOne == true) {
				// busy wait, if I was the last one to run
			}	
			// now hopefully, lastOne == false
			if (lastOne == true) {
				throw new Error("Insane");
			}
			
			System.out.println(HELLO);
			
			// toggle the flag
			lastOne = true;
		}
	}

	// The Hello Threads identifies himself as "false"
	void worldLoop() {

		while (true) {
		
			while(lastOne == false) {
				// busy wait, until other thread toggled the flag
			}
			// now hopefully, lastOne == true
			if (lastOne == false) {
				throw new Error("Insane");
			}
			
			System.out.println(WORLD);
			
			// toggle the flag
			lastOne = false;
		}
	}

	
	//
	void startThreads(){
		
		Thread helloThread = new Thread(){
			public void run(){
				helloLoop();
			}
		};
		Thread worldThread = new Thread(){
			public void run(){
				worldLoop();
			}
		};
		
		helloThread.start();
		worldThread.start();
	}

}