package seminar.exercises.thread.parallel.threadpool_behavior.base;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.concurrent.*;

/*
 * Demonstrates behavior of Threadpool in case of many pool-jobs with blocking IO.
 * 
 * Illustrates two situations:
 * 
 * 1. In case of growing threadpool, the memory  increases infinitely and the program micht crash 
 * 		
 * 2  In case of limited Threadpool but unbounded Queue the memory consumption grows due to an infinitely growing queue.
 * 
 * 3  In case of a limited Queue and limited Threadpool the program crashes, due to an RecectedException upon trying t schedule a new Job
 * 		
 * 4. The fix is a combination of the above measures + a RejectedExecutionHandler that runs rejected Tasks in the Thread of the caller,
 *    
 * 
 */
public class ThreadpoolBehaviorBase {

	static final int MAX_POOL_THREADS = 10;
	
	ExecutorService threadPool; 
	
	
	BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
	
	
	public static void main(String[] args) {
		
		ThreadpoolBehaviorBase instance = new ThreadpoolBehaviorBase();
		instance.submittJobs();
	}
	
	ThreadpoolBehaviorBase(){
		setUpThreadPool();
	}
	
	void submittJobs(){
		
		while(true) {
			// TODO put jobs into ThreadPool
		}
	}
	
	void setUpThreadPool() {
		
		// Version 1: unbounded Pool: Risk Memory consumption due to many threads in the pool
		threadPool = null; // TODO 
		
		// Version 2 FixedSize Pool, but unlimited Queue: 
		// Problems: Memory consumption due to Queue  growing infinitely and high CPU load due to continuous insertion into queue
		//threadPool = null; // TODO
		
		// Version 3: fixed size blocking Queue: Problem: Rejected Jobs
		//threadPool = null; // TODO
		
		// Version 4 FIX: Bounded Queue, Bounded Pool, and rejected Task is run in the caller thread.
		//threadPool = null; // TODO
		
         
	}
	
	
	String blockingIOCall() {
		System.out.println(" HIT ANY KEY TO UNBLOCK Thread: " + Thread.currentThread() );
	
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
