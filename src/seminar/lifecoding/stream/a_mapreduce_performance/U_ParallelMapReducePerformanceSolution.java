package seminar.lifecoding.stream.a_mapreduce_performance;


import java.util.Arrays;
import java.util.OptionalInt;
import java.util.concurrent.ThreadLocalRandom;


/*
 * Dieses Beispiel zeigt, dass Stream-Processing durch .parallel() wesentlich beschleunigt werden kann. 
 * 
 * Zum "Umschalten" zwischen den beiden Varianten, im Stream das .parallel() in 
 * ParallelStreamExecution.findMaxReduce(int[] intArray)
 * auskommententieren, ist mit TODO gekennzeichnet. 
 * Beispiel-Messungen
 * 
 * SEQUENTIELLER STREAM
 * U_ParallelMapReducePerformance:  Start Processing:  ARRAY_SIZE: 2000000000
 * ParallelMapReducePerformance: Init Complete  ARRAY_SIZE: 2000000000 time(ms): 5332
 * findMaxReduce(int[] intArray)
 * ParallelMapReducePerformance: Parallel Processing complete, Result: 98 time(ms): 9881
 * 
 * 
 * PARALLELLER STREAM
 * U_ParallelMapReducePerformance:  Start Processing:  ARRAY_SIZE: 2000000000
 * ParallelMapReducePerformance: Init Complete  ARRAY_SIZE: 2000000000 time(ms): 5098
 * findMaxReduce(int[] intArray)
 * ParallelMapReducePerformance: Parallel Processing complete, Result: 98 time(ms): 1848
 */
public class U_ParallelMapReducePerformanceSolution {

	//static final int ARRAY_SIZE = 10;
	// static final int ARRAY_SIZE = 10000000;
	//static final int ARRAY_SIZE = 100000000; // Default
	//static final int ARRAY_SIZE = 200000000; // -Xmx25G good for String Array  
	static final int ARRAY_SIZE = 2000000000; // -Xmx25G  good for  int[] example, not possible for String Array


	public static void main(String[] args) {

		System.out.println("U_ParallelMapReducePerformanceSolution:  Start Processing:  ARRAY_SIZE: " + ARRAY_SIZE);

		U_ParallelMapReducePerformanceSolution instance = new U_ParallelMapReducePerformanceSolution();

		instance.runTests();

	}

	/*
	 * Fill array with random numbers and start the Stream-Processing
	 */
	void runTests() {

		long startInit = System.currentTimeMillis();

		// Arrays to operate on: Contain random numbers in [0,100]
		int[] intArray = initIntArray();
		// Strings Array, with integer representations as strings
		//String[] stringNumbersArray = initStringNumbersArray();
		
		
		System.out.println("ParallelMapReducePerformance: Init Complete  ARRAY_SIZE: " + ARRAY_SIZE + " time(ms): "
				+ (System.currentTimeMillis() - startInit));

		//print(intArray);
		//print(stringNumbersArray);
	
	
		// Parallel Stream measurement
		long startParallel = System.currentTimeMillis();
		
		// ARRAY_SIZE = 2000000000 recommended
		long resultParallel = ParallelStreamExecution.findMaxReduce(intArray);
		
		System.out.println("ParallelMapReducePerformance: Processing complete, Result: " + resultParallel
				+ " Stream-Processing time(ms): " + (System.currentTimeMillis() - startParallel));

	}

	

	// Initializes array of strings that represent integers  range [0, 100]
	String[] initStringNumbersArray() {
		
		String[] stringNumbersArray = new String[ARRAY_SIZE];
		
		Arrays.parallelSetAll(stringNumbersArray,  s -> String.valueOf(ThreadLocalRandom.current().nextInt(100)));
		
		return stringNumbersArray;
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


class ParallelStreamExecution {

	/*
	 * Algorithmus
	 * - alle ungeraden Zahlen herausfiltern
	 * - von den verbleibenden das Maximum finden
	 */
	static int findMaxReduce(int[] intArray) {
		System.out.println(" findMaxReduce(int[] intArray)");
		// Dies erzeugt einen IntStream, das Resutat ist daher ein OptionalInt und nicht
		// ein Optional<Integer>
		OptionalInt result =
		    Arrays.stream(intArray).
		    // TODO comment .parallel() to see the difference
			parallel().
			filter(i -> i % 2 == 0).
			reduce((a, b) -> a > b ? a : b);

		return result.getAsInt();
	}
}
