package seminar.examples.task.quicksort;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.atomic.AtomicInteger;


// Demonstrates Parallelization of Quicksort with Java FJ Tasks
// For test code see: seminar.examples.thread.quicksort.QuicksortTest
// Perfomance seems to be similar to QuicksortThreads.
public class QuicksortTasks extends RecursiveAction {

	static ForkJoinPool fjPool = new ForkJoinPool();
	
	// Array of integers to be sorted
	private int[] data;

	int lower;
	int upper;

	// Gets calculated in the topmost recursion by the public ctor
	// and afterwards is propagated in each recursion step via private ctor
	static  int threshold;
	
	static AtomicInteger taskCount = new AtomicInteger(0);
	
	public QuicksortTasks(int[] data){
		this.data = data;
		this.lower = 0;
		this.upper = data.length - 1;
		// Define threshold	according to naive heuristics
		threshold = (data.length / Runtime.getRuntime().availableProcessors())/4;
		
	}
	
	private QuicksortTasks(int[] data, int lower, int upper, int threshold) {
		this.data = data;
		this.lower = lower;
		this.upper = upper;
		
	}
	
	@Override
	protected void compute() {
		taskCount.incrementAndGet();
		this.sortRecursively();	
	}
	
	// The public main entry point to parallelized sorting
	public void sort() {
		
		System.out.println("QuicksortTasks. Data Size: " + data.length);
		System.out.println("threshold " + threshold);
		System.out.println("data.length " + data.length);
		
		
		// Blocking call
		fjPool.invoke(this);
		
		System.out.println("Number of used Tasks: " + taskCount.get() + " Steal-Count: " + fjPool.getStealCount());
		
	}
	
	

	private void sortRecursively() {

		//System.out.println("sortRecursively() [" + lower + "," + upper + "] in Thread " + Thread.currentThread().getId());
		int l = lower;
		int u = upper;
		
		if (l >= u) {
			return;
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

		// Recursive Calls
		// Check threshold to decide for sequential or parallel processing
		if( u-l < threshold){
			// sequential
			QuicksortTasks leftSequential = new QuicksortTasks(this.data, l, j-1, threshold);
			QuicksortTasks rightSequential =new QuicksortTasks(this.data, j + 1, u, threshold);
			// Call that does not start new tasks!
			leftSequential.sortRecursively();
			rightSequential.sortRecursively();
			
		} else{
			// parallel
			RecursiveAction leftTask = new QuicksortTasks(this.data, l, j-1, threshold);
			RecursiveAction rightTask = new QuicksortTasks(this.data, j + 1, u, threshold);
			
			// Fork new tasks
			invokeAll(leftTask, rightTask);
			leftTask.join();
			rightTask.join();
			
		}
		
		
		
					
	}

	private void Swap(int i, int j) {

		int tmp = data[i];
		data[i] = data[j];
		data[j] = tmp;
	}
	
}


