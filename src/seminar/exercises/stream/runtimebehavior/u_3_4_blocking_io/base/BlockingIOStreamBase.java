package seminar.exercises.stream.runtimebehavior.u_3_4_blocking_io.base;

import java.util.Scanner;
import java.util.stream.IntStream;

/*
 * Ausgangsbasis zur Übung PS 3.4 Blocking IO
 * 
 * 
 * Schreiben Sie einen Parallel-Stream, der lange blockierende Aufrufe tätigt, z.B. von der Shell lesen. 
 * Zeigen Sie, dass der Stream dadurch in einen Deadlock kommt: Lauffähige Teile des Streams werden nicht abgearbeitet,
 * obwohl keiner der Pool-Threads die CPU benutzt.
 * 
 * Was passiert, wenn Sie einen zweiten Stream starten?
 * 
 */
public class BlockingIOStreamBase{
	
	
	public static void main(String[] args) {
		
		System.out.println("ArraySumUp TestMain()");
		
		BlockingIOStreamBase instance = new BlockingIOStreamBase();
		
		instance.run();
	}
	
	void run() {
		
		final long timeStart = System.currentTimeMillis(); 
		
		// Einen Stream erzeugen
		IntStream intStream = IntStream.range(0, 100);
		
		// TODO
		// Stream Elemente mit blockierendem IO parallel abarbeiten: Deadlock!!!
		
			
		final long timeEnd = System.currentTimeMillis(); 
		
		System.out.println("Processing time: " + (timeEnd - timeStart));
	}
	
	
	

	
	// Blockierende Calls erzeugen DEADLOCK im Stream!!! Durch Shell Eingabe wir die Blockirung aufgehoben.
	private void blockingIOCall(int e) {
		System.out.println("Processing: " + e + " " + Thread.currentThread() + " HIT ANY KEY TO UNBLOCK CALL" );
		// Problem: eine blockierende IO Operation aufrufen:
		Scanner scanner = new Scanner(System.in);
		String input = scanner.nextLine();	
		System.out.println(Thread.currentThread() + " UNBLOCKED" );
	}
	
	
}