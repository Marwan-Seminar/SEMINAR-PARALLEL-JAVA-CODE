package seminar.exercises.stream.runtimebehavior.u_3_3_quicksort_parallel_stream.base;

import java.util.Random;
import java.util.Spliterator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/*
 * This class demonstrates how to implement quicksort within the Stream-Framework based on a Spliterator 
 * 
 * 
 */
public class QuicksortSpliteratorPerformanceBase {

	public static void main(String[] args) {
		System.out.println("QuicksortSpliteratorTest");

		// OLD: max arraysize: 250.000.000, adjust heap: -Xmx1500m: approx 30 Sec. sequential
		
		//int ARRAYSIZE = 100;
		int ARRAYSIZE = 10000; 
		//int ARRAYSIZE = 10000000;
		//int ARRAYSIZE	= 100000000; // DEFAULT Performance: approx. 3 Seconds. That seams to be faster than my thread version (4 sec)
		//int ARRAYSIZE	= 250000000;
		//int ARRAYSIZE = 1000000000; 
		
		
		
		
		int[] testData = new int[ARRAYSIZE];

		System.out.println("Arraysize: " + ARRAYSIZE);

		Random rand = new Random();
		for (int i = 0; i < ARRAYSIZE; ++i) {
			// ACHTUNG: Belegung mit Pattern kann zu Abstutrz oder sehr langsamem Run führen!!!
			int nextInt = rand.nextInt();
			//int nextInt = ARRAYSIZE -i % 1000;
			testData[i] = nextInt;
		}

		System.out.println("start sorting");

		long beginTime = System.currentTimeMillis();

		QuicksortStreamAlgorithm quicksortAlgorithm = new QuicksortStreamAlgorithm(testData);

		QuicksortSpliterator quicksortSpiterator = new QuicksortSpliterator(quicksortAlgorithm);
		
		// The second boolean parameter determines if stream is processed in parallel (true)
		Stream<QuicksortStreamAlgorithm> quicksortStream = StreamSupport.stream(quicksortSpiterator, true);
		
		// This is a hack, I did not find another way to start the stream processing
		quicksortStream.forEach(a -> {});



		System.out.println("Time used for sorting: " + (System.currentTimeMillis() - beginTime)  + " Milliseconds ");
	
		print(testData);

	}
	
	public static void print(int[] data) {
		for (int i = 0; i < data.length; ++i) {
			System.out.println(data[i]);
		}
	}
	
}


/*
 * This class has two responsibilities:
 * 1. It represents a subarray of the array to be sorted. Therefore it  keeps track of the limits of this subarray.
 * 2. It organizes the splitting process.
 * 
 * The subarray representation is a closed interval [lower, upper]
 * 
 * trySplit() performs the sorting of the array into two parts, "small" and "big" elements
 * tryAdvance() sorts the complete subarray recursively sequentially.
 */
class QuicksortSpliterator implements Spliterator<QuicksortStreamAlgorithm>{

	
	
	QuicksortStreamAlgorithm quicksortStreamAlgorithm;
	int lower;
	int upper;
	
	QuicksortSpliterator(QuicksortStreamAlgorithm quicksortStreamAlgorithm){
		
		this.quicksortStreamAlgorithm = quicksortStreamAlgorithm;
		this.lower = 0;
		this.upper = quicksortStreamAlgorithm.data.length - 1;
	}
	
	QuicksortSpliterator(QuicksortStreamAlgorithm quicksortStreamAlgorithm, int lower, int upper){
		
		this.quicksortStreamAlgorithm = quicksortStreamAlgorithm;
		this.lower = lower;
		this.upper = upper;
	}
	
	/*
	 * Sorts the subarray completely.
	 * action is ignored.
	 */
	@Override
	public boolean tryAdvance(Consumer action) {
		
		// TODO: 
		// Die rekursive Sortierung des verbliebenen Teilarrays aufrufen
		// Dabei wird nicht weiter parallelisiert, sonderen Quicksort wird sequeuntiell rekursiv ausgeführt.
		
		return false;
	}

	/*
	 * Sorts the sub-array into two parts, and returns a new Spliterator for the left half. 
	 */
	@Override
	public Spliterator trySplit() {
		
		
		// TODO: Den richtigen Punkt finden, um das Array zu teilen
		
		// TODO: Einen neuen Spliterator für den linken Teil des Arrays erzeugen
		
		// TODO: Die eigenen Array-Grenzen anpassen
		
		// TODO: Den neuen QuicksortSpliterator für die linke Hälfte zurückgeben
		return null;
	}

	@Override
	public long estimateSize() {
		return (upper - lower) + 1;
	}

	@Override
	public int characteristics() {
		// estimateSize() provides exact result:
		return SIZED;
	}
	
}

/*
 * This class implements the Quicksort algorithm in a way that is suitable for streams and Spliterators.
 * 
 * It is used by the QuicksortSpliterator to perform the sorting.
 * 
 * The two main methods are:
 * 
 * 1. sortRecursively(int lower, int upper): This sorts a subarray completely, applying the recursive Quicksort algorithm.
 *    This method is used by the  QuicksortSpliterator when no further Parallelism is desired.
 *    Practically this means, it should be called in
 *    QuicksortSpliteratort.tryAdvance(Consumer action) 
 *    
 * 2. int splitNonRecursively(int l, int u): This methods sorts the array intwo parts. Small elements are in the left part, 
 *    big elements are in the right part, and the index of the element in the middle is returned.
 *    This method should be used by QuicksortSpliteratort.trySplit()
 *    
 */
class QuicksortStreamAlgorithm{
	
	
	
	// Array of integers to be sorted
	int[] data;

	public QuicksortStreamAlgorithm(int[] data) {
		this.data = data;
	}
	

	void sortRecursively(int lower, int upper) {
		if (lower >= upper) {
			return;
	    } else {
		
	    	// sort this subarray into two parts
	    	int splitPoint = splitNonRecursively(lower,  upper);
		
			// recursive calls
			sortRecursively(lower, splitPoint - 1);
			sortRecursively(splitPoint + 1, upper);
		}

	    
	    
	}
	
	/*
	 * Spits sub-array [l, u] into two parts, without recursing!!!
	 * The result is that  the right part contains elements bigger or equal to the pivot element and the 
	 * left part contains elements smaller or equal to the pivot element. 
	 * The pivot Element is arbitrarily chosen (here, the first one in the subarray)
	 * @return the position where the array is "split", or -1 if lower >= upper
	 */
	int splitNonRecursively(int l, int u) {
		
		if (l >= u) {
			throw new Error("sortNonRecursively lower must be < upper, but: lower: " + l + " upper " + u);
	    }
	
	    // Choose a pivot element, arbitrarily.
	    int pivotIdx = l;
	
	    // running indices:
	    int i = l;
	    int j = u + 1;
	
	    // Sorts the subrange [l,u] of the data array into two sub-arrays such
	    // that
	    // all members of the left sub-array are smaller than all members of the
	    // right sub-array.
	    // After this loop j is the index of the dividing element.
	    while (true) {
	      // i points to the left end of the range under consideration.
	      // Shift i to the right as long as elements are <= pivot.
	      // After this loop, i points to an element which is bigger than
	      // pivot, of i == j.
	      do {
	        i++;
	      } while (i <= u && data[i] <= data[pivotIdx]);
	
	      // Do the analogous action for the right end pointer j
	      do {
	        j--;
	      } while (data[j] > data[pivotIdx]);
	
	      if (i > j)
	        break;
	
	      // Swap
	      Swap(i, j);
	    }
	
	    Swap(pivotIdx, j);
		    
	    return j;
	   
	  }
	
	  private void Swap(int i, int j) {
	
	    int tmp = data[i];
	    data[i] = data[j];
	    data[j] = tmp;
	  }

}

