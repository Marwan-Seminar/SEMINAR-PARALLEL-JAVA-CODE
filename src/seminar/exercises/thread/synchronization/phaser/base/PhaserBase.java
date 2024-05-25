package seminar.exercises.thread.synchronization.phaser.base;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Phaser;

/*
 * Basis zur Übung Synch-4: Phaser
 * 
 *  
 *  Programmieren Sie folgendes Szenario, das einen Wettlauf simuliert:
 *  Mehrere „Läufer“-Instanzen  werden erzeugt
 *  Die Läufer begeben sich zum Start
 *  Die Läufer brauchen unterschiedlich lange, bis sie am Start ankommen
 *  In dem Moment, in dem alle Läufer den Start erreicht haben laufen sie alle gleichzeitig los
 *  
 *  Im hier vorliegenden Code laufen die Runner alle zu unterschiedlichen Zeiten los.
 *  Reparieren Sie das!
 *  
 *  The Algorithm consists of the steps, marked in the Code with STEP 1... STEP 4
 */
public class PhaserBase {
	
	
	public static void main(String[] args) throws InterruptedException {
		PhaserBase instance = new PhaserBase();
		
		instance.startRunners();
				
	}
	
	void startRunners() throws InterruptedException {
		
		// TODO STEP 1: Register arbitrator at the phaser 
		
		Runner.startTime = System.currentTimeMillis();
		
		// Create and start 5 Runners.
		// They are waiting between 1 and 5 Seconds before they reach the Phaser
		for(int i = 1; i < 5; ++i) {
			
			// TODO STEP 2
			// Register the new Runner at the Phaser!
			
			
			Runner runner = new Runner(i);
			runner.start();
		}
		
		// TODO STEP 3: Deregister arbitrator at the Phaser
		
		System.out.println("Arbitrator Deregistered");
		 
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

	// TODO: Declare a Phaser Instance, that all know
	
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
		
		//TODO STEP 4
		// await all other Runners arriving here
		
		System.out.println("\nRunner starting!" + (System.currentTimeMillis() - startTime));
		
	}
	
	
	@Override
	public String toString() {
		
		return super.toString() + " secondsToWait " +  secondsToWait;
		
	}
	
}

//////////////////// Displays dots to indicate progres /////////////

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
