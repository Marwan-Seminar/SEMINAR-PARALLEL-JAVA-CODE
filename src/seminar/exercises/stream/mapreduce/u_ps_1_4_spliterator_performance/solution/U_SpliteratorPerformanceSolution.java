package seminar.exercises.stream.mapreduce.u_ps_1_4_spliterator_performance.solution;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

/*
 * Dieses Beispiel zeigt, dass der Parallelisierungsgewinn, bei einem Stream der auf einer LinkedList basiert
 * wesentlich schlechter ist, als bei einem Stream der auf einer ArrayList basiert.
 * 
 * Es kann sogar passieren, dass die parallele Version langsame ist als die sequentielle.
 * 
 * Beispiel 1: Arraysize 200000000, 2*(10^8): LinkedList wird durch Parallelität  ca. Faktor 1,5 schneller,  die ArrayList wird um Faktor 4 schneller
 * Beispiel 2: Arraysize 100000000, 10^8: LinkedList wird durch Parallelität  ca. Faktor 4 langsamer,  die ArrayList wird um Faktor 4 schneller
 * 
 *  Der Grund ist, dass der Spliterator bei einer LinkedList wesentlich langsamer arbeitet als bei einer ArrayList.
 *  Denn das Auffinden der Mitte einer LinkedList ist nicht durch eine einfache indexierung möglich, sondern man muss 
 *  sich von Element zu Element hangeln.
 *  
 *  Messergebnisse:
 * 
 * Bsp 1: 
 * 
 * U_ParallelMapReducePerformanceSolution:  Start Processing:  ARRAY_SIZE: 200000000
 * U_SpliteratorPerformanceSolution: Init Complete  ARRAY_SIZE: 200000000 time(ms): 33457
 * findMaxReduce(List<Integer> intList) seqeuntial
 * Sequential Array-List Result: 98 Processing time(ms): 1495
 * findMaxReduce(List<Integer> intList) parallel
 * Parallel Array-List Result: 98 Processing time(ms): 348
 * findMaxReduce(List<Integer> intList) seqeuntial
 * Sequential Linked-List Result: 98 Processing time(ms): 2159
 * findMaxReduce(List<Integer> intList) parallel
 * Parallel Linked-List Result: 98 Processing time(ms): 1659
 * 
 * Bsp. 2
 * U_ParallelMapReducePerformanceSolution:  Start Processing:  ARRAY_SIZE: 100000000
 * U_SpliteratorPerformanceSolution: Init Complete  ARRAY_SIZE: 100000000 time(ms): 13892
 * findMaxReduce(List<Integer> intList) seqeuntial
 * Sequential Array-List Result: 98 Processing time(ms): 757
 * findMaxReduce(List<Integer> intList) parallel
 * Parallel Array-List Result: 98 Processing time(ms): 192
 * findMaxReduce(List<Integer> intList) seqeuntial
 * Sequential Linked-List Result: 98 Processing time(ms): 957
 * findMaxReduce(List<Integer> intList) parallel
 * Parallel Linked-List Result: 98 Processing time(ms): 3478
 *  
 */
public class U_SpliteratorPerformanceSolution {

	//static final int ARRAY_SIZE = 10;
	//static final int ARRAY_SIZE = 1000000;
	//static final int ARRAY_SIZE = 10000000;	// LinkedList wird Faktor 4 langsamer, ArrayList wird Faktor 2 schneller
	//static final int ARRAY_SIZE = 100000000; 	// Bsp. 2: LinkedList wird Faktor 4 langsamer, ArrayList wird Faktor 4 schneller
	static final int ARRAY_SIZE = 200000000; 	// Bsp. 1: LinkedList wird FAktor 1,5 schneller, ArrayList wird Faktor 4 schneller
	//static final int ARRAY_SIZE = 2000000000; // -Xmx25G  good for  int[] example, not possible for String Array


	public static void main(String[] args) {

		System.out.println("U_ParallelMapReducePerformanceSolution:  Start Processing:  ARRAY_SIZE: " + ARRAY_SIZE);

		U_SpliteratorPerformanceSolution instance = new U_SpliteratorPerformanceSolution();

		instance.runTests();

	}

	/*
	 * Fill array with random numbers and start the Stream-Processing
	 */
	void runTests() {

		long startInit = System.currentTimeMillis();

		
		ArrayList<Integer> integerArrayList 	= initIntegerArrayList();
		LinkedList<Integer> integerLinkedList	= initIntegerLinkedList();
		
		
		System.out.println("U_SpliteratorPerformanceSolution: Init Complete  ARRAY_SIZE: " + ARRAY_SIZE + " time(ms): "
				+ (System.currentTimeMillis() - startInit));

		long startStream = 0;
		long result = 0;
		
		// 1. ArrayList Sequential Version
		startStream = System.currentTimeMillis();
		result = StreamExecution.findMaxReduceSeqeuntial(integerArrayList);
		System.out.println("Sequential Array-List Result: " + result+ " Processing time(ms): " + (System.currentTimeMillis() - startStream));

		// 2. ArrayList Parallel Version
		startStream = System.currentTimeMillis();
		result = StreamExecution.findMaxReduceParallel(integerArrayList);
		System.out.println("Parallel Array-List Result: " + result+ " Processing time(ms): " + (System.currentTimeMillis() - startStream));
		
		// 3. LinkedList Sequential Version
		startStream = System.currentTimeMillis();
		result = StreamExecution.findMaxReduceSeqeuntial(integerLinkedList);
		System.out.println("Sequential Linked-List Result: " + result+ " Processing time(ms): " + (System.currentTimeMillis() - startStream));

		// 4. LinkedList Parallel Version
		startStream = System.currentTimeMillis();
		result = StreamExecution.findMaxReduceParallel(integerLinkedList);
		System.out.println("Parallel Linked-List Result: " + result+ " Processing time(ms): " + (System.currentTimeMillis() - startStream));
	}

	

	// Initialize the Lists
	
	/*
	 * Initilaize the ArrayList
	 */
	ArrayList<Integer> initIntegerArrayList(){
		ArrayList<Integer> integerArrayList = new ArrayList<Integer>(ARRAY_SIZE);

		for(int i = 0; i < ARRAY_SIZE; ++i) {
			integerArrayList.add(ThreadLocalRandom.current().nextInt(100));
		}
		return integerArrayList;
	}
	
	/*
	 * Initialize the LinkedList
	 */
	LinkedList<Integer> initIntegerLinkedList(){
		LinkedList<Integer> integerArrayList = new LinkedList<Integer>();
		
		for(int i = 0; i < ARRAY_SIZE; ++i) {
			integerArrayList.add(ThreadLocalRandom.current().nextInt(100));
		}
		return integerArrayList;
	}
	
}

/*
 * Two identical Stram Prossessings:
 *  a) parallel findMaxReduceParallel()
 *  b) sequential findMaxReduceSeqeuntial()
 */
class StreamExecution {

	/*
	 * Algorithmus
	 * - alle ungeraden Zahlen herausfiltern
	 * - von den verbleibenden das Maximum finden
	 */
	static int findMaxReduceParallel(List<Integer> intList) {
		System.out.println(" findMaxReduce(List<Integer> intList) parallel");
		
		Optional<Integer> result =
			intList.stream().
			parallel().
			filter(i -> i % 2 == 0).
			reduce((a, b) -> a > b ? a : b);

		return result.get();
	}
	/*
	 * Same as above, just without parallel()
	 */
	static int findMaxReduceSeqeuntial(List<Integer> intList) {
		System.out.println(" findMaxReduce(List<Integer> intList) seqeuntial");
		
		Optional<Integer> result =
			intList.stream().
			filter(i -> i % 2 == 0).
			reduce((a, b) -> a > b ? a : b);

		return result.get();
	}
}
