package seminar.exercises.thread.parallel.threadpool_performance.solution;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/*
 * Demonstrates performance gain with Thread-Pool and Future
 */
public class ThreadPoolPerformanceSolution {
	
	static long startTime;
	
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		
		startTime = System.currentTimeMillis();
	
		//new ThreadPoolPerformanceSolution().sequentialLoop();
		
		new ThreadPoolPerformanceSolution().poolLoop();
		
	}
	
	void sequentialLoop() {
		
		long counter = 0;
		for(long i = 0; i < 40000000000L; ++i) {
			counter++;
			
		}
		
		System.out.println("Time: " + (System.currentTimeMillis() - startTime) + " Result: " + counter);
		
	}
	
	void poolLoop() throws InterruptedException, ExecutionException {
		
		ExecutorService pool =  Executors.newCachedThreadPool();
		
		long globalCounter = 0;
	
		List<Future<Long>> futures = new ArrayList<>();
		
		for(int i = 0; i< 4; i++) {
			Future<Long> futureReslt = pool.submit(() ->{
				long localCounter = 0;
				// The loop count is factor 4 smaller that in the sequential case
				for(long innerIndex = 0; innerIndex < 10000000000L; ++innerIndex) {
					localCounter++;
				}
				return localCounter;
			});
			
			futures.add(futureReslt);
		}
		
		for(Future<Long> future : futures) {
			globalCounter += future.get();
		}
		
		
		
		System.out.println("Time: " + (System.currentTimeMillis() - startTime) + " Result: " + globalCounter);
	
	}

}
