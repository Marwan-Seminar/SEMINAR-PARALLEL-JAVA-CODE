package seminar.exercises.stream.mapreduce.u_ps_1_2_map_red_performance.base;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;


/*
 * Basis für Übungsaufgabe Parallel Map-Reduce Performance 
 *  
 * Zeigen Sie, dass eine einfache Map-Reduce Berechnung durch Parallelität wesentlich beschleunigt werden kann.   
 * 
 * Lösungshinweis: 
 * - Erzeugen Sie ein großes Array von int-Werten mit Zufallszahlen (ca. 10^8 Einträge )
 * - Erzeugen Sie aus dem Array einen Stream
 * - filtern Sie alle ungeraden Zahlen aus dem Stream (Stream.filter())
 * - Rufen Sie reduce((a,b) -> max(a,b)) auf dem Stream auf, um das Maximum zu finden.
 * 
 * Sie sollten durch parallel() einen Performace-Vorteil in einem Faktor erreichen,  in etwa der Anzahl der
 * physikalischen CPU-Cores entspricht.
 * 
 */
public class U_ParallelMapReducePerformanceBase {

	//static final int ARRAY_SIZE = 10;
	// static final int ARRAY_SIZE = 10000000;
	//static final int ARRAY_SIZE = 100000000; // Default
	//static final int ARRAY_SIZE = 200000000; // -Xmx25G good for String Array  
	static final int ARRAY_SIZE = 2000000000; // -Xmx25G  good for  int[] example, not possible for String Array


	public static void main(String[] args) {

		System.out.println("U_ParallelMapReducePerformanceBase:  Start Processing:  ARRAY_SIZE: " + ARRAY_SIZE);

		U_ParallelMapReducePerformanceBase instance = new U_ParallelMapReducePerformanceBase();

		instance.runTests();

	}

	/*
	 * Fill array with random numbers and start the Stream-Processing
	 */
	void runTests() {

		long startInit = System.currentTimeMillis();

		// Array to operate on: Contain random numbers in [0,100]
		int[] intArray = initIntArray();
		
	
		System.out.println("ParallelMapReducePerformance: Init Complete  ARRAY_SIZE: " + ARRAY_SIZE + " time(ms): "
				+ (System.currentTimeMillis() - startInit));

		//print(intArray);
		
	
		// Parallel Stream measurement
		long startParallel = System.currentTimeMillis();
		
		// ARRAY_SIZE = 2000000000 recommended
		long resultParallel = ParallelStreamExecution.findMaxReduce(intArray);
		
		System.out.println("ParallelMapReducePerformance: Processing complete, Result: " + resultParallel
				+ " Stream-Processing time(ms): " + (System.currentTimeMillis() - startParallel));

	}


	// Initializes array of integers range [0, 100]
	int[] initIntArray() {
		int[] intArray = new int[ARRAY_SIZE];
		Arrays.parallelSetAll(intArray, i -> ThreadLocalRandom.current().nextInt(100));
		return intArray;
	}
	
	
	

	// for debugging
	<T> void print(T[] dataArray) {
		for (int i = 0; i < dataArray.length; ++i) {
			System.out.println(dataArray[i]);			
		}
	}
}


// TODO: Hier die Stream-Verarbeitung einfügen
class ParallelStreamExecution {

	/*
	 * Algorithmus
	 * - alle ungeraden Zahlen herausfiltern
	 * - von den verbleibenden das Maximum finden
	 */
	static int findMaxReduce(int[] intArray) {
		System.out.println(" findMaxReduce(int[] intArray)");
		
		// TODO 1.: Stream erzeugen
		
		// TODO 2: Alle ungeraden Zahlen herausfiltern
		
		// TODO 2. reduce Operation auf dem Stream aufrufen, z.B. um das Maximum zu berechnen
		
		// TODO 3.: Ergebnis abholen und zurückgeben
		
		// TODO 4: Stream parallelisieren	

		return 0;
	}
}
