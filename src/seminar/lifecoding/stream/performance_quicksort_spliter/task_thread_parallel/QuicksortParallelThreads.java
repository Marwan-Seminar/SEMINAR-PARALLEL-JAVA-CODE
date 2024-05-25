package seminar.lifecoding.stream.performance_quicksort_spliter.task_thread_parallel;

import java.util.concurrent.atomic.AtomicInteger;

class QuicksortParallelThreads implements Runnable {
	// Array of integers to be sorted
	private int[] data;

	int lower;
	int upper;

	// Gets calculated in the topmost recursion by the public ctor
	// and afterwards is propagated in each recursion step via private ctor
	static  int threshold;
	
	static AtomicInteger threadCount = new AtomicInteger(0);
	
	public QuicksortParallelThreads(int[] data){
		this.data = data;
		this.lower = 0;
		this.upper = data.length - 1;
		// Define threshold	according to naive heuristics
		threshold = data.length / Runtime.getRuntime().availableProcessors();
		
	}
	
	private QuicksortParallelThreads(int[] data, int lower, int upper, int threshold) {
		this.data = data;
		this.lower = lower;
		this.upper = upper;
	}
	
	public void sort() {
		System.out.println("threshold: " + threshold);
		System.out.println("data.length " + data.length);
		
		
		Thread thread = new Thread(this);
		thread.start();
		try {
			thread.join();
		} catch (InterruptedException e) {
			throw new Error(e);
		}
		
		System.out.println("Number of used Threads: " + threadCount.get());
	}
	
	public void run(){
		threadCount.incrementAndGet();
		this.sortRecursively();
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
			QuicksortParallelThreads leftSequential = new QuicksortParallelThreads(this.data, l, j-1, threshold);
			QuicksortParallelThreads rightSequential =new QuicksortParallelThreads(this.data, j + 1, u, threshold);
			// Call that does not start new threads!
			leftSequential.sortRecursively();
			rightSequential.sortRecursively();
			
		} else{
			// parallel
			Thread leftThread = new Thread(new QuicksortParallelThreads(this.data, l, j-1, threshold));
			Thread rightThread =new Thread(new QuicksortParallelThreads(this.data, j + 1, u, threshold));
			
			// Fork new threads
			leftThread.start();
			rightThread.start();
			
			try {
				leftThread.join();
				rightThread.join();
			} catch (InterruptedException e) {
				throw new Error(e);
			}
		}
		
		
		
					
	}

	private void Swap(int i, int j) {

		int tmp = data[i];
		data[i] = data[j];
		data[j] = tmp;
	}
}