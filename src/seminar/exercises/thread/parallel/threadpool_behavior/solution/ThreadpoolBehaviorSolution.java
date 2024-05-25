package seminar.exercises.thread.parallel.threadpool_behavior.solution;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.*;

/*
 * Demonstrates behavior of Threadpool in case of many pool-jobs blocking IO.
 * 
 * Illustrates two situations:
 * 
 * 1. In case of growing threadpool, the memory  increases infinitely and the program might crash 
 * 		threadPool = Executors.newCachedThreadPool();
 * 
 * 2  In case of limited Threadpool but unbounded Queue the memory consumption grows due to an infinitely growing queue.
 * 		threadPool = Executors.newFixedThreadPool(MAX_POOL_THREADS);
 * 
 * 3  In case of a limited Queue and limited Threadpool the program crashes, due to an RejectedException upon trying t schedule a new Job
 * 		BlockingQueue<Runnable> boundedQueue = new ArrayBlockingQueue<>(10);
		threadPool = new ThreadPoolExecutor(MAX_POOL_THREADS, MAX_POOL_THREADS,
                0L, TimeUnit.MILLISECONDS,
                boundedQueue);
 * 
 * 4. The fix is a combination of the above measures + a RejectedExecutionHandler that runs rejected Tasks in the Thread of the caller,
 *     such that the caller blocks when calling submit()
 * 		a) Fixed size Threadpool: ThreadPoolExecutor(MAX_POOL_THREADS, MAX_POOL_THREADS, ...
 *  	b) Bounded Queue: new ArrayBlockingQueue<>(10) passed to ThreadPoolExecutor constructor
 *  	c) RejectedExecutionHandler of Type: ThreadPoolExecutor.CallerRunsPolicy passed to ThreadPool constructor
 *   The program limits the number of executed IO calls and blocks untitl these calls return.
 *   This is the desired behavior. 
 *   
 *   Open Questions / Possible Answers
 *   - In case 1: Memory consumptions is less than I did expect: It seems like the threads are created but not started?
 *   - In case 2 I have 100% CPU load, I would have expected 25% as I have one infinite loop. MAybe the submitt() uses Threads
 *   	to organize its internal pool management.
 * 
 */
public class ThreadpoolBehaviorSolution {

	static final int MAX_POOL_THREADS = 10;
	
	ExecutorService threadPool; 
	
	
	BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
	
	
	public static void main(String[] args) {
		
		ThreadpoolBehaviorSolution instance = new ThreadpoolBehaviorSolution();
		instance.submittJobs();
	}
	
	ThreadpoolBehaviorSolution(){
		setUpThreadPool();
	}
	
	void submittJobs(){
		
		while(true) {
			//Future <String> future = 
			threadPool.submit(() -> blockingIOCall());
			
			// Print out the pool characteristics
			//System.out.println("pool: " + threadPool);
		}
	}
	
	void setUpThreadPool() {
		
		// Version 1: unbounded Pool: Risk Memory consumption due to many threads in the pool
		threadPool = Executors.newCachedThreadPool();
		
		
		// Version 2 FixedSize Pool, but unlimited Queue: 
		// Problems: Memory consumption due to Queue  growing infinitely and high CPU load due to continuous insertion into queue
		//threadPool = Executors.newFixedThreadPool(MAX_POOL_THREADS);
		
		// Version 3: fixed size blocking Queue: Problem: Rejected Jobs
		/*
		BlockingQueue<Runnable> boundedQueue = new ArrayBlockingQueue<>(10);
		threadPool = new ThreadPoolExecutor(MAX_POOL_THREADS, MAX_POOL_THREADS,
                0L, TimeUnit.MILLISECONDS,
                boundedQueue);
        */
		
		// Version 4 FIX: Bounded Queue, Bounded Pool, and rejected Task is run in the caller thread.
		/*
		BlockingQueue<Runnable> boundedQueue = new ArrayBlockingQueue<>(10);
		threadPool = new ThreadPoolExecutor(MAX_POOL_THREADS, MAX_POOL_THREADS,
                0L, TimeUnit.MILLISECONDS,
                boundedQueue, new ThreadPoolExecutor.CallerRunsPolicy());
         */
	}
	
	
	String blockingIOCall() {
		System.out.println(" HIT ANY KEY TO UNBLOCK Thread: " + Thread.currentThread() );
		//String input = scanner.nextLine();	
		String input;
		
		try {
			input = reader.readLine();
		} catch (IOException e) {
			throw new Error(e);
		}
		System.out.println(Thread.currentThread() + " UNBLOCKED: " + input );
		return input;
	}
	
}
