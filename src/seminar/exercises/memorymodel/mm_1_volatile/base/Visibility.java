// Copyright Marwan Abu-Khalil 2019

package seminar.exercises.memorymodel.mm_1_volatile.base;

/*	
	Übung Visibility: Variablen-Veränderung unsichtbar im anderen Thread
	
	Schreiben Sie ein Programm, in dem sich zwei Threads eine Variable teilen, 
	jedoch der eine nicht sieht, wenn diese vom anderen Thread verändert worden ist. 
	
	Reparieren Sie dieses Programm durch die Verwendung des Schlüsselwortes volatile. 
	
	Lernziel: Erkennen, dass Threads unterschiedliche Sichten auf gemeinsamen Speicher haben können.

*/
public class Visibility {
		
	void startGlobalDataThreads() throws InterruptedException{
		
		
		// New Thread that reads global state. 
		new Thread(){
			public void run(){
				// TODO infinite loop here, to read some global state, that might have changed. 
				// Upon state change exit loop, print to the shell and return from this thread.
				
				System.out.println("Reader Thread saw global state change: Returning, global data: ");
			}
			
		}.start();
		
		// Wait a moment here in main thread before changing the global state
		Thread.sleep(1000);
		
		// Thread main sets globalData to 1:
		System.out.println("Global state changed in Writer Thread " );
		
	}
	
	public static void main(String[] args) throws InterruptedException{
		new Visibility().startGlobalDataThreads();
	}

}
