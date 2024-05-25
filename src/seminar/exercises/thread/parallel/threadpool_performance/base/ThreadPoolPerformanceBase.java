package seminar.exercises.thread.parallel.threadpool_performance.base;


/*
 * Demonstrates performance gain with Thread-Pool and Future
 */
public class ThreadPoolPerformanceBase {
	
	static long startTime;
	
	public static void main(String[] args) {
		
		startTime = System.currentTimeMillis();
	
		new ThreadPoolPerformanceBase().sequentialLoop();
		
		
	}
	
	void sequentialLoop() {
		
		long counter = 0;
		for(long i = 0; i < 40000000000L; ++i) {
			counter++;
			
		}
		
		System.out.println("Time: " + (System.currentTimeMillis() - startTime) + " Result: " + counter);
		
	}
	
}
