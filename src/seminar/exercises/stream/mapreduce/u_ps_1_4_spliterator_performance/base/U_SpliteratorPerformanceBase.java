package seminar.exercises.stream.mapreduce.u_ps_1_4_spliterator_performance.base;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

/*
 * Zeigen Sie, dass der Parallelisierungsgewinn, bei einem Stream der auf einer LinkedList basiert
 * wesentlich schlechter ist, als bei einem Stream der auf einer ArrayList basiert.
 * 
 * Es kann sogar passieren, dass die parallele Version langsame ist als die sequentielle.
 * 
 * Typische Array-Sizes können sein:
 * Beispiel 1: Arraysize 200000000 
 * Beispiel 2: Arraysize 100000000
 * 
  */
public class U_SpliteratorPerformanceBase {

	//static final int ARRAY_SIZE = 10;
	//static final int ARRAY_SIZE = 1000000;
	//static final int ARRAY_SIZE = 10000000;	// LinkedList wird Faktor 4 langsamer, ArrayList wird Faktor 2 schneller
	static final int ARRAY_SIZE = 100000000; 	// Bsp. 2: LinkedList wird Faktor 4 langsamer, ArrayList wird Faktor 4 schneller
	//static final int ARRAY_SIZE = 200000000; 	// Bsp. 1: LinkedList wird FAktor 1,5 schneller, ArrayList wird Faktor 4 schneller
	//static final int ARRAY_SIZE = 2000000000; // -Xmx25G  good for  int[] example, not possible for String Array


	public static void main(String[] args) {

		System.out.println("U_SpliteratorPerformanceBase:  Start Processing:  ARRAY_SIZE: " + ARRAY_SIZE);

		U_SpliteratorPerformanceBase instance = new U_SpliteratorPerformanceBase();

		instance.runTests();

	}

	/*
	 * Fill array with random numbers and start the Stream-Processing
	 */
	void runTests() {

		long startInit = System.currentTimeMillis();

		
		ArrayList<Integer> integerArrayList 	= initIntegerArrayList();
		
		// TODO 1: Linked List zum Vergleich erzeugen
		
		System.out.println("U_SpliteratorPerformanceBase: Init Complete  ARRAY_SIZE: " + ARRAY_SIZE + " time(ms): "
				+ (System.currentTimeMillis() - startInit));

		long startStream = 0;
		long result = 0;
		
		// 1. ArrayList Sequential Version
		startStream = System.currentTimeMillis();
		result = StreamExecution.findMaxReduceSequential(integerArrayList);
		System.out.println("Sequential Array-List Result: " + result+ " Processing time(ms): " + (System.currentTimeMillis() - startStream));

		// TODO 2: Weitere Varianten der Verarbeitung 
		
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
	
}

/*
 * Stream Processing:
 */
class StreamExecution {

	/*
	 * Algorithmus
	 * - alle ungeraden Zahlen herausfiltern
	 * - von den verbleibenden das Maximum finden
	 */
	static int findMaxReduceSequential(List<Integer> intList) {
		System.out.println(" findMaxReduce(List<Integer> intList) parallel");
		
		Optional<Integer> result =
			intList.stream().
			filter(i -> i % 2 == 0).
			reduce((a, b) -> a > b ? a : b);

		return result.get();
	}

}
