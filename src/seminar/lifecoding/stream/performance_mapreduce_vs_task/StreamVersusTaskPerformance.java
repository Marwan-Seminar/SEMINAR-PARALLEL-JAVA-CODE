package seminar.lifecoding.stream.performance_mapreduce_vs_task;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;


/* MEASURMENTS

ACHTUING: Reiehnfolge der Aufrufe beiinflusst die Laufzeit!

MapAndMax Beispiel: Hier kann man zeigen, dass der Threshold von Parallel-Streams wirkt! 

MapAndMax mit Threshold im Task-Fall
StreamVersusTaskPerformance: Init Complete  ARRAY_SIZE: 100000000 time(ms): 6679
StreamVersusTaskPerformance: Sequential Processing complete. SequentialResult  2147483601  time(ms): 4352
StreamVersusTaskPerformance: Stream Processing complete, Stream-Result: 2147483601 time(ms): 1247
StreamVersusTaskPerformance: Processing Task complete  Task-Result: 2147483601 time(ms): 965

Ohne Threshold im Task-Fall: Task ist langsamer als sequentiell
StreamVersusTaskPerformance: Init Complete  ARRAY_SIZE: 100000000 time(ms): 6767
StreamVersusTaskPerformance: Sequential Processing complete. SequentialResult  2147483646  time(ms): 4101
StreamVersusTaskPerformance: Stream Processing complete, Stream-Result: 2147483646 time(ms): 1347
StreamVersusTaskPerformance: Processing Task complete  Task-Result: 2147483646 time(ms): 4382
 
*/

/**
 * Dieses Beispiel zeigt, dass Parallel-Stream Parallelisierungen schneller sind, als naive Task-Parallelisierungen.
 * 
 * Das zentrale Lernergebnis der Messungen ist:
 *  1. Stream Parallelit‰t ist schneller als naiv ohne Threashold implementierte Task-Parallelit‰t, da Streams implizit einen Threshold realisieren.
 * 
 * Nebenerkenntnisse sind:
 * 2.Parallelisierung kann auch bei trivalen Algorithmen die Performance verbessern (hier ca. um den Faktor 3-4 im Verglich zum sequentiellen Fall) 
 * 3. Eine geschickt realiserte Task-Parallelisierung kann genauso acuh schneller sein als eine Stream-Parallelisierung 
 * 4. Eine Task-Parallelisierung ohne Threshold ist langsamer als der sequentielle Algorithmus.

 * Dieses File ist folgendermaﬂen organisiert:
 * 
 *  - StreamVersusTaskPerformance: hat die main-Methode
 * 
 * Die folgenden Klassen implementieren einen identischen (trivialen) Algorithmus auf unterschiedliche Weise
 * Der Algorithmus: Bildet alle Strings im Array auf Integer-Werte ab und findet das Maximum
 * 
 *  - SequentialExecution realiseirt den sequentiellen Algorithmus
 *  - ParallelStreamExecution realisiert den Algorithmus unter Verwendung von Parallel-Streams 
 *  - TaskParallelExecution und MapAndMaxTask realiseren den Algorithmus auf Basis von ForkJoinTasks
 * 
 **/

/*
 * This class demonstrates, that a parallel stream is more efficenth than a naiive 
 * parallelization with tasks, as the theshold is automanted in the parallel stream case.
 */
public class StreamVersusTaskPerformance {
	
	//static final int ARRAY_SIZE = 10000000;
	static final int ARRAY_SIZE = 100000000;	// Default
	//static final int ARRAY_SIZE = 200000000; 	// Heap-Size for String Array: -Xmx25G 
	//static final int ARRAY_SIZE = 1000000000;	// Not possible for String Array

		
	
	// Array to operate on
	String[] stringNumbersArray = new String[ARRAY_SIZE];

	// Strings Array, with integer representations as strings
	public static void main(String[] args) {
			
		System.out.println("StreamVersusTaskPerformance: LIFE_CODING Start Processing:  ARRAY_SIZE: " + ARRAY_SIZE );	
		
		StreamVersusTaskPerformance instance = new StreamVersusTaskPerformance();
		
		instance.runTests();		
		
	}


	/*
	 * This test compares sequential, stream-cased and task-based performance.
	 * 
	 * BUT: the sequence in which these tests are performed affect their actual runtime. For accurate numbers the tests should be executed in separate programs runs.
	 */
	void runTests() {
		
		
		long startInit = System.currentTimeMillis();
		
		//initIntegerArray();
		
		initStringNumbersArray();
	
		System.out.println("StreamVersusTaskPerformance: Init Complete  ARRAY_SIZE: " + ARRAY_SIZE + " time(ms): " + (System.currentTimeMillis() - startInit));	
		
		
		//print(instance.numbers);
			
		// Sequential measurements
		long startSequential = System.currentTimeMillis();
		long resultSequential = SequentialExecution.mapAndMaxLoop(stringNumbersArray);
		System.out.println("StreamVersusTaskPerformance: Sequential Processing complete. SequentialResult  " + resultSequential + "  time(ms): " + (System.currentTimeMillis() - startSequential));	
				
		
		// Stream measurement
		long startStream = System.currentTimeMillis();
		long resultStream = ParallelStreamExecution.mapAndMaxStream(stringNumbersArray);
		System.out.println("StreamVersusTaskPerformance: Stream Processing complete, Stream-Result: " + resultStream +  " time(ms): " + (System.currentTimeMillis() - startStream));	
	
		
		// Task measurement
		long startTask = System.currentTimeMillis();
		long resultTask = TaskParallelExecution.mapAndMaxTask(stringNumbersArray);
		System.out.println("StreamVersusTaskPerformance: Processing Task complete  Task-Result: " + resultTask +  " time(ms): " + (System.currentTimeMillis() - startTask));

			
				
	}
	
	
	// Initializes array of strings that represent integers
	void initStringNumbersArray(){
		System.out.println("initStringNumbersArray()");
		Random rand = new Random();
		for (int i = 0; i < stringNumbersArray.length; ++i) {
			int number = rand.nextInt();
			number = number < 0 ? -number : number;
			number = number == 0 ? 2 : number ;
			
			stringNumbersArray[i] = Integer.toString(number);
			
		}
	}
	
	// for debugging
	public static void print(int[] data) {
		long sum = 0;
		for (int i = 0; i < data.length; ++i) {
			System.out.println(data[i]);
			sum += data[i];
		}
		
		System.out.println("Sum of array elements:" + sum);
	}
}

class SequentialExecution{
	
	
	static long mapAndMaxLoop(String[] numStrings) {

		int max = 0;
		int current=0;
		for(int i = 0; i < numStrings.length; ++i) {
			current = Integer.valueOf(numStrings[i]);
			max = current > max ? current : max;
		}
		return max;

	}

}

class ParallelStreamExecution {
 
	static int mapAndMaxStream(String[] numStrings) {

		Optional<Integer> result = 
		Arrays.stream(numStrings).
		parallel().
		map(Integer::parseInt).
		reduce((a, b) -> a>b? a:b);

		return result.get();
	}
}


/*
 * Identical Algorithm as ParallelStreamExecution, but realised with Tasks instead of Streams.
 * 
 * Array is split into subarrays as long as there are maximum two elements left.
 * Each split is handled by a dedicated Task object (inefficient, as no completions used)
 * Then each maximum is calculated recursively throughout the tree of tasks (inefficient, as work for each task instance is too small).
 */
class TaskParallelExecution{
	
	static long mapAndMaxTask(String[] stringNumbers) {
		
		MapAndMaxTask rootTask = new MapAndMaxTask(stringNumbers, 0, stringNumbers.length);
		
		long max = ForkJoinPool.commonPool().invoke(rootTask);
		
		return max;
	}
	
}


/*
 * Maps String to long and finds max
 */
class MapAndMaxTask extends RecursiveTask<Integer>{

	String[] numbers;
	int lower, upper; // half open interval, [lower, upper). This  MaxTask is responisble for numbers[lower] ...numbers[upper-1]
	
	MapAndMaxTask(String[] numbers, int lower, int upper){
		this.numbers = numbers;
		this.lower = lower;
		this.upper = upper;
		//System.out.println("MapAndMaxTask(double[] numbers:, int lower, "  + lower  + "int upper: " + upper +")");
		if (lower >= upper) {
			throw new Error("MapAndMaxTask illegal array boundaries: lower " + lower + " upper " + upper);
		}
	}
	
	// REcursively creates new Tasks until sub array contains one element only
	protected Integer compute() {
		
		// end of recursion, only one element remaining
		if(lower == upper -1) {
			return Integer.parseInt(numbers[lower]);
			
		}
		// THRESHOLD ABSOLUTELY REQUIRED FOR GOOD PERFORMANCE to avoid that work splits become too small 
		if(upper-lower < numbers.length / 8) {
			return sequentialCompute();
		}
		
		// recurse: slow if no threshold aktive 
		// split array 
		int halfSpace = (upper - lower) / 2; 	// halfSpace can be 0! it describes the approximately the half of the the elements of this subarray.
		int mid = lower + halfSpace; 		// mid marks the mid of the array to be handled by this task
		
		MapAndMaxTask leftTask 	= new MapAndMaxTask(numbers, lower, mid);
		MapAndMaxTask rightTask 	= new MapAndMaxTask(numbers, mid, upper);		
		
		leftTask.fork();
		rightTask.fork();
		
		int leftResult = 	leftTask.join();
		int rightResult = 	rightTask.join();
		
		// Return the bigger of the both values
		int max = leftResult > rightResult ? leftResult : rightResult ;
		
		//System.out.println("MapAndMaxTask retruning " + max + " , int lower, "  + lower  + "int upper: " + upper +")");
		return max;
		
	}

	private int sequentialCompute() {
		int max = 0;
		for(int i = lower; i< upper; ++i) {
			int current = Integer.parseInt(numbers[i]);
			max = max > current ? max : current; 
		}
		
		return max;
	}
}
