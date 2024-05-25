// Copyright Marwan Abu-Khalil 2019

package seminar.exercises.memorymodel.mm_1_volatile.solution;

/*	
	Übung Visibility: Variablen-Veränderung unsichtbar im anderen Thread
	
	Schreiben Sie ein Programm, in dem sich zwei Threads eine Variable teilen, 
	jedoch der eine nicht sieht, wenn diese vom anderen Thread verändert worden ist. 
	Reparieren Sie dieses Programm durch die Verwendung des Schlüsselwortes volatile. 
	
	Lernziel: Erkennen, dass Threads unterschiedliche Sichten auf gemeinsamen Speicher haben können.
	
*/
public class Visibility {

	// volatile
	private  Object globalData;
		
	void startGlobalDataThreads() throws InterruptedException{
		
		
		
		new Thread(){
			public void run(){
				// this line is fundamentally important to create the behavior of "invisiblity"
				globalData = null;
				while(globalData == null){
					// following line must be commented, as it destroys the desired effect.
					//System.out.println("Global data in Reader Thread " + Thread.currentThread()  + globalData  );
				}
				System.out.println("Reader Returned, global data: "   + globalData);
			}
		}.start();
		
		Thread.sleep(1000);
		
		// Thread main sets globalData to 1:
		
		globalData = new Object();
		System.out.println("Global data in Writer Thread " + globalData );
		
	}
	
	public static void main(String[] args) throws InterruptedException{
		new Visibility().startGlobalDataThreads();
	}

}
