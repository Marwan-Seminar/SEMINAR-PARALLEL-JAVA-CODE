package seminar.exercises.task.loop_parallel.solution;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveTask;

/*
 * Demonstrates performance gain Tasks 
 * Parallelization of a trivial Loop
 * 
 * Contains 3 Versions:
 * 
 * 1. Sequential version sequentialLoop(): 13 Seconds
 * 
 * 2. Correctly parallelized version taskLoop(): 4 Seconds
 * 
 * 3. Oversimplified Paralleleized (4 subloops with identical ranges) taskLoopOversiplified(): 4 Seconds
 * 
 * On my machine: 
 * Taskcount 4: approximately 60% CPU Load, speedup factor 3,5  (close to perfect which would be factor 4)
 * Taskcount 8: 100% CPU, but slower!! 
 * 
 * Interesting: Hyperthreading does not help here, I have 4 CPUs with Hyperthreading. 
 * CPU Load is shown according to my 8 logical processors (50 % with my four tasks).
 * But if I increase the task count to 8, although the displayed CPU load increases to 100% I do not achieve better speedup.
 *  
 * */
public class LoopParallelizationTasksSolution {
	
	
	final long LOOPSIZE = 40000000000L;
	final int TASKCOUNT = 8;
	
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		
		
	
		// Sequential version
		new LoopParallelizationTasksSolution().sequentialLoop();
		
		// Correctly parallelized version
		new LoopParallelizationTasksSolution().taskLoop();
		
		// Oversimplified version
		//new LoopParallelizationTasksSolution().taskLoopOversimplified();
	}
	
	void sequentialLoop() {
		
		System.out.println("Sequential Loop started");
		
		long startTime = System.currentTimeMillis();
		
		long counter = 0;
		for(long i = 0; i < LOOPSIZE; ++i) {
			counter++;
		}
		
		System.out.println("Sequential Time: " + (System.currentTimeMillis() - startTime) + " Result: " + counter);
		
	}
	
	/*
	 * Uses several ForkJoinTask instanaces to execute subranges of the original loop
	 * The results fo the separate tasks are finalle put together. 
	 */
	void taskLoop() throws InterruptedException, ExecutionException {
		
		System.out.println("\n Parallel Loop startet");
		
		long startTime = System.currentTimeMillis();
		
		ForkJoinPool pool =  ForkJoinPool.commonPool();
		
		long globalCounter = 0;
	
		List<Future<Long>> futures = new ArrayList<>();
		
		for(int i = 0; i< TASKCOUNT; i++) {
			long from = i * (LOOPSIZE / TASKCOUNT) ;
			long to = (i+1) * (LOOPSIZE / TASKCOUNT) ;
			
			Future<Long> futureResult = pool.submit(new LoopTask(from, to));
			
			futures.add(futureResult);
		}
		
		for(Future<Long> future : futures) {
			globalCounter += future.get();
		}
		
		System.out.println("Parallel Time: " + (System.currentTimeMillis() - startTime) + " Result: " + globalCounter);
	
	}
	
	/*
	 *  Oversiplified. Executes for loops of equal range instead of partitioning the original range.
	 */
	void taskLoopOversimplified() throws InterruptedException, ExecutionException {
		
		System.out.println("\n taskLoopOversimplified()  startet");
		
		long startTime = System.currentTimeMillis();
		
		ForkJoinPool pool =  ForkJoinPool.commonPool();
		
		long globalCounter = 0;
	
		List<Future<Long>> futures = new ArrayList<>();
		
		for(int i = 0; i< TASKCOUNT; i++) {
			
			Future<Long> localResult = 
					pool.submit(() ->{
						//System.out.println("Task ");
						long localCounter = 0;
						for(long idx = 0; idx < LOOPSIZE / TASKCOUNT; ++idx) {
							localCounter++;
						}
						return localCounter;
					});
			
			futures.add(localResult);
		}
		
		for(Future<Long> future : futures) {
			globalCounter += future.get();
		}
		
		System.out.println("Oversimplified Parallel Time: " + (System.currentTimeMillis() - startTime) + " Result: " + globalCounter);
	
	}

}

class LoopTask extends RecursiveTask<Long>{
	long from;
	long to;
	
	public LoopTask(long from, long to) {
		this.from = from;
		this.to = to;
	}
	
	@Override
	public Long compute(){
		System.out.println("Task: from " + from + " to " + to);
		long localCounter = 0;
		long loopRange = to - from;
		for(long index = 0; index < loopRange; ++index) {
			localCounter ++;
		}
		return localCounter;
	}
}




