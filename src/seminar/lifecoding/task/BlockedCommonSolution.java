package seminar.lifecoding.task;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

/*
 * Demonstrates, that  a blocked common-Pool 
 * blocks off all Tasks in the System.
 * 
 * Two problems can cause this
 * a) blocking tasks
 * b) long running tasks
 * 
 * Usage and algorithm
 * - Start the main method.
 * - See that as many blocng tasks run as the Common-Pool has threads
 * - See that the so called "good Algorithm" does not run, unless some bad tasks are unblocked by hitting any key (does not work smoothly).
 * 
 * The Common-Pool is overwhelmed by either a) IO-blocking tasks or by b) CPU-intensive tasks.
 * After these "bad" tasks are submitted to the pool, we wait a second, and then start  a "good" task. 
 * This good task is never executed, unless bad tasks are unblocked.
 */
public class BlockedCommonSolution {

	
	public static void main(String[] args) throws InterruptedException {
		
		BlockedCommonSolution instance = new BlockedCommonSolution();
		
		new Thread() {
			public void run() {
				System.out.println("Starting bad Algorithm");
				//Version a) Blocking IO
				instance.startBadAlgorithmBlocking();
				// Version b: CPU load
				//instance.startBadAlgorithmCPUIntensive();
			}
		}.start();
		
		// HACK: make sure, that first all the bad tasks are started, and only than the friendly task is started
		Thread.sleep(1000);
	
		new Thread() {
			public void run() {
				System.out.println("Starting good Algorithm: friendly Task should run...");
				instance.startFriendlyAlgorithm();
			}
		}.start();
		
		// Dieser Aufruf führt offenbar (manchmal) dazu, dass ein pending Task im Current-Thread ausgeführt wird
		ForkJoinPool.commonPool().awaitQuiescence(100000, TimeUnit.SECONDS);
		// HACK again, keep the program running, avoiding the side-effect by awaitQuiescence described above
		//Thread.sleep(60000);
	}
	
	void startBadAlgorithmBlocking() {
		// THIS IS THE CRUCIAL LINE: Starting more Tasks than Threads in the pool: getCommonPoolParallelism() +1
		for(int i = 0; i < ForkJoinPool.getCommonPoolParallelism() +1; ++i) {
			ForkJoinPool.commonPool().submit(()->{
				System.out.println("Reading next Line, Thread: " + Thread.currentThread().getName());
				
				// Blocking IO call
				new Scanner(System.in).nextLine();
				
				/*
				try {
					new BufferedReader(new InputStreamReader(System.in)).readLine();
				} catch (IOException e) {
					throw new Error(e);
				}
				*/
				
				System.out.println("Got next Line");
			});
		}		
	}

	void startBadAlgorithmCPUIntensive() {
		
		// THIS IS THE CRUCIAL LINE: Starting more Tasks than Threads in the pool: getCommonPoolParallelism() +1
		for(int i = 0; i < ForkJoinPool.getCommonPoolParallelism() + 1; ++i) {
			ForkJoinPool.commonPool().submit(()->{
				System.out.println("CPU Intesive Loop");
				
				for(long l = 0; l < 100000000000L; ++l) {
					
				}
				System.out.println("LOOP returns");
			});
		}		
	}
	
	/*
	 * This Method gets called, after the Common-Pool is already blocked by IO-blocing tasks or bei CPU-Intensive Tasks.
	 * The effect to be shown is, that the submitted taks does not run, because all pool threads are blocked.
	 * Only after Pool-Threads are unblocked, the task submitted here does acutally run.
	 */
	void startFriendlyAlgorithm() {
		
		ForkJoinPool.commonPool().submit(()->{
			System.out.println("Friendly Task is running now !!");
		});
	}
	
}
