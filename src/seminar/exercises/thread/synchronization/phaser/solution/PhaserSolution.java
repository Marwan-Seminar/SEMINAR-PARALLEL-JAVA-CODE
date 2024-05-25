package seminar.exercises.thread.synchronization.phaser.solution;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Phaser;

/*
 * Demonstrates usage of Phaser. 
 * 
 * The Algorithm consists of the following steps, marked in the Code with STEP 1... STEP 4
 */
public class PhaserSolution {
	
	
	public static void main(String[] args) throws InterruptedException {
		PhaserSolution instance = new PhaserSolution();
		
		instance.startRunners();
				
	}
	
	void startRunners() throws InterruptedException {
		
		// STEP 1: Register self at the phaser (kann man auch weglassen, da die Runner selbst warten. Garantiert aber, dass keiner losläuft bevor sich alle registreiert haben)
		Runner.phaser.register();
		Runner.startTime = System.currentTimeMillis();
		
		// Create and start 5 Runners.
		// They are waiting between 1 and 5 Seconds before they reach the Phaser
		for(int i = 1; i < 5; ++i) {
			
			// STEP 2
			// Register a new Runner!!! (Obviously registering just increases a counter, it does not register a specific new thread at the Phaser)
			Runner.phaser.register();
			
			
			Runner runner = new Runner(i);
			runner.start();
		}
		
		// STEP 3
		Runner.phaser.arriveAndDeregister();
		System.out.println("Deregister Call returned");
		 
		ProgresThread progresTrehad = new ProgresThread();
		progresTrehad.setDaemon(true);
		progresTrehad.start();
	}
	
}


/*
 * This Runner simulates a Runner taht starts when a 
 * Signal is given, such that all Runners start simultaneously.
 */

class Runner extends Thread{
	
	static long startTime;

	final static Phaser phaser = new Phaser(0); // "0" no one is registered in the beginning
	
	int secondsToWait = 0;
	
	Runner(int secondsToWait) throws InterruptedException{
		
		this.secondsToWait = secondsToWait;
	}
	
	public void run() {
		
		System.out.println("Runner created, wating for:   " + secondsToWait + " seconds" );
		try {
			Thread.sleep(1000 * secondsToWait);
		} catch (InterruptedException e) {
			throw new Error(e);
		}  
		
		// STEP 4
		// await all other Runners arriving here
		phaser.arriveAndAwaitAdvance(); 
		
		System.out.println("\nRunner starting!" + (System.currentTimeMillis() - startTime));
		
	}
	
	
	@Override
	public String toString() {
		
		return super.toString() + " secondsToWait " +  secondsToWait;
		
	}
	
}

//////////////////// Displays dots to indicate progress /////////////

class ProgresThread extends Thread{
	
	public void run() {
		
		System.out.println("waiting for all to run: ");
		int i = 1;
		while(true) {
			System.out.print(i++ + " ");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
