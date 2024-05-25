package seminar.lifecoding.stream.performance_quicksort_spliter;

import java.util.Random;
import java.util.Spliterator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/*
 * This class demonstrates how to implement quicksort within the Stream-Framework based on a Spliterator 
 * 
 * Performance is pretty good!
 * 
 * My hand optimized threashold is most of the time solower, than the Framework generated Threshold, ALLTHOUG the framework 
 * creates much more splits than processors are available! e.G. 53 Slits in Arraysize: 100000000 case.
 * 
 * TODO; I do not understand, why I have e.g. 53 Splits and also 53 calls to tryAdvance! I think, tryAdvace is called to the leafs of the Splits-Tree
 * TODO with allmost any arraysize around 50 Splits!
 */
public class QuicksortSpliteratorPerformanceTest {

	public static void main(String[] args) {
		System.out.println("QuicksortSpliteratorTest");

		// OLD: max arraysize: 250.000.000, adjust heap: -Xmx1500m: approx 30 Sec. sequential
		
		//int ARRAYSIZE = 100;
		//int ARRAYSIZE = 10000; 
		//int ARRAYSIZE = 10000000;
		int ARRAYSIZE	= 100000000; // DEFAULT Performance: approx. 3 Seconds. That semms to be faster than my thread version (4 sec)
		//int ARRAYSIZE	= 250000000;
		//int ARRAYSIZE = 1000000000; // 31812 Milliseconds
		
		
		
		
		int[] testData = new int[ARRAYSIZE];

		System.out.println("Arraysize: " + ARRAYSIZE);

		Random rand = new Random();
		for (int i = 0; i < ARRAYSIZE; ++i) {
			// ACHTUNG: Belegung mit Pattern kann zu Abstutrz oder sehr langsameem Run führen!!!
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

		// This is a sequential call:
		//quicksortAlgorithm.sort();

		//System.out.println("Time used for sorting: " + (System.currentTimeMillis() - beginTime) / 1000);
		System.out.println("Time used for sorting: " + (System.currentTimeMillis() - beginTime)  + " Milliseconds ");
		System.out.println("Number of Splits: " + quicksortSpiterator.splitsCounter.get() );
		System.out.println("Number of TryAdvance calls: " + quicksortSpiterator.tryAdvanceCounter.get() );
		System.out.println("Number of trySplit() calls: " + quicksortSpiterator.trySplitCounter.get() );
		//print(testData);

	}
	
	public static void print(int[] data) {
		for (int i = 0; i < data.length; ++i) {
			System.out.println(data[i]);
		}
	}
	
}


/*
 * This class has two responsibilities:
 * 1. It represents a subarray of the array to be sorted.  Therefore it  keeps track of the limits of this subarray.
 * 2. It organizes the splitting process.
 * 
 * The subarray representation is a closed interval [lower, upper]
 * 
 * trySplit() performs the sorting of the array into two parts, "small" and "big" elements
 * tryAdvance() sorts the complete subarray recursively sequentially.
 */
class QuicksortSpliterator implements Spliterator<QuicksortStreamAlgorithm>{

	// Statistics
	static AtomicInteger splitsCounter = new AtomicInteger(0);
	static AtomicInteger trySplitCounter = new AtomicInteger(0);
	static AtomicInteger tryAdvanceCounter = new AtomicInteger(0);
	
	QuicksortStreamAlgorithm quicksortStreamAlgorithm;
	int lower;
	int upper;
	
	QuicksortSpliterator(QuicksortStreamAlgorithm quicksortStreamAlgorithm){
		splitsCounter.incrementAndGet();
		this.quicksortStreamAlgorithm = quicksortStreamAlgorithm;
		this.lower = 0;
		this.upper = quicksortStreamAlgorithm.data.length - 1;
	}
	
	QuicksortSpliterator(QuicksortStreamAlgorithm quicksortStreamAlgorithm, int lower, int upper){
		splitsCounter.incrementAndGet();
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
		//System.out.println("tryAdvance() called for: lower " + lower + " upper " + upper + " in Thread:  " + Thread.currentThread());
		tryAdvanceCounter.incrementAndGet();
		quicksortStreamAlgorithm.sortRecursively(lower, upper); 
		return false;
	}

	/*
	 * Sorts the sub-array into two parts, and returns a new spliterator for the left half. 
	 * Relies on framework to stop splitting. Does never return null! (TODO: change this, return null if upper == lower)
	 */
	@Override
	public Spliterator trySplit() {
		//System.out.println("trySplit() called for: lower " + lower + " upper " + upper + " in Thread:  " + Thread.currentThread());
		trySplitCounter.incrementAndGet();
		// Number of splits is increased:
		//   1. here by one as limits of this Spliterator are adapted
		//   2. in the Constructor 
		splitsCounter.incrementAndGet();
		// Try to be smart: use threshold, but in my case, I can not accelerate the performance
		/*if(upper - lower < (quicksortStreamSupport.data.length / Runtime.getRuntime().availableProcessors()) / 20 ) {
			return null;
		}*/
		int splitPoint = quicksortStreamAlgorithm.splitNonRecursively(lower, upper);
		
		// create new split 
		QuicksortSpliterator leftSideSlpiterator = 
				new QuicksortSpliterator(quicksortStreamAlgorithm, lower, splitPoint -1);
		
		// adjust own limits
		this.lower = splitPoint + 1;
		// upper remains unchanged
		
		return leftSideSlpiterator;
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
 * Implements the quicksort algorithm in a way suitable for streams and spliterators
 */
class QuicksortStreamAlgorithm{
	
	
	
	// Array of integers to be sorted
	int[] data;

	public QuicksortStreamAlgorithm(int[] data) {
		this.data = data;
	}
	
	/*
	
	public void sort() {
		sortRecursively(0, data.length - 1);
	}
	*/
	

	void sortRecursively(int lower, int upper) {
		if (lower >= upper) {
			return;
	    } else {
		
	    	// sort this subarray into two halves
	    	int splitPoint = splitNonRecursively(lower,  upper);
		
			// recursive calls
			sortRecursively(lower, splitPoint - 1);
			sortRecursively(splitPoint + 1, upper);
		}

	    
	    
	}
	
	/*
	 * Spits sub-array [l, u] into two halves, without recursing!!!
	 * The result is that  the right half contains elements bigger or equal to the pivot element and the 
	 * left half contains elements smaller or equal to the pivot element. 
	 * The pivot Element is arbitrarily chosen (here, the first one in the subarray)
	 * @return the position where the array is "split", or -1 if lower >= upper
	 */
	int splitNonRecursively(int l, int u) {
		
		if (l >= u) {
			throw new Error("sortNonRecursively lower must be < upper, but: lower: " + l + " upper " + u);
			//return -1; 
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

