package seminar.exercises.stream.runtimebehavior.u_3_4_blocking_io.solution;

import java.util.Scanner;
import java.util.stream.IntStream;

/*
 * Diese Klasse zeigt das Stream Verhalten bei Blocking IO
 * 
 * traverseBlockingIO() ruft von Zeit zu Zeit einen blockierenden Systemcall auf. NAch dem 7. Aufruf entsteht ein Deadlock. 
 * Das Deadlock wird aufgehoben, indem man eine Beliebige Eingabe auf der Shell macht. Das muss man mehrmals machen damit
 * das PRogramm terminiert.
 * 
 */
public class BlockingIOStreamSolution{
	
	
	public static void main(String[] args) {
		
		System.out.println("ArraySumUp TestMain()");
		
		BlockingIOStreamSolution instance = new BlockingIOStreamSolution();
		
		instance.run();
	}
	
	void run() {
		
		final long timeStart = System.currentTimeMillis(); 
		
		// Einen Stream erzeugen
		IntStream intStream = IntStream.range(0, 100);
		
		// Stream Elemente mit blockierendem IO parallel abarbeiten: Deadlock!!!
		traverseBlockingIO(intStream);
		
			
		final long timeEnd = System.currentTimeMillis(); 
		
		System.out.println("Processing time: " + (timeEnd - timeStart));
	}
	
	
	

	// Blockierende Calls erzeugen DEADLOCK!!! Durch Shell Eingaben aufheben!
	private void traverseBlockingIO(IntStream intStream) {
		
		System.out.println("traverseBlockingIO() started");
		
		//  Für jedes Element eine blocking IO Methode aufrufen
		intStream.parallel().forEach(e -> {
			System.out.println("Processing: " + e + " " + Thread.currentThread() + " HIT ANY KEY TO UNBLOCK CALL" );
			// Problem: eine blockierende IO Operation aufrufen. Beim 7. Mal sollte ein Deadlock entstehen! 
			Scanner scanner = new Scanner(System.in);
			String input = scanner.nextLine();	
			System.out.println(Thread.currentThread() + " UNBLOCKED" );
			
		});
		
		System.out.println("traverseBlockingIO() returns");
	}
	
	
	//////////////////// CODE FÜR CPU-INTENSIVE LANGLÄUFER: FUHRT ZU STARVATION /////////////////////////////
	
	// Für jedes Element im Stream eine CPUintensive Methode aufrufen und den Thread-Namen ausdrucken. 
		private void traverseParallel(IntStream intStream) {
			System.out.println("traverseParallel() started");
			
			intStream.parallel().forEach(e -> {
				System.out.println(e);
				System.out.println(Thread.currentThread());
				
				cpuIntensiveFunction();
			});
			
			System.out.println("traverseParallel() returnes");
		}

	long cpuIntensiveFunction() {
		
		System.out.println("ArraySumUp cpuIntensiveFunction()");
		
		long result = 1;
		for(long i = 0; i < 500000000L; ++i) {
			result = result ++;
			result = result * result;
			if(result >= Integer.MAX_VALUE) {
				result = 1;
			}
		}
		
		return result;
	}
}