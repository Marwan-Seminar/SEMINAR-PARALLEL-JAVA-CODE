package seminar.exercises.task.steal_count.solution;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/*
 * Demonstrates Stealing Behavior
 * 
 * 3 Cases:
 * 1. Unrelated Tasks forked bei a Thread create stealing
 * 2. Trees work perfect
 * 3. Linear Recursive Tasks can also create stealing. In this example they also create low CPU load, underutilized Threads
 * 	  DO NOT USE, UNCLEAR BEHAVIOR	
 */
public class ForkJoinPoolStealCountSolution {

	
	public static void main(String[] args) throws InterruptedException {
		ForkJoinPoolStealCountSolution instance = new ForkJoinPoolStealCountSolution();
		
		// Steal-Count demonstration
		
		// Case 1: Unrelated Tasks forked by a Thread create stealing
		instance.creatingManyNonRecursiveTasks();
		
		// Case 2. Trees work perfect
		//instance.createTreeManyRecursiveTasks();
				
		//  Case 2. Linear Recursive Tasks DO NOT USE, UNCLEAR BEHAVIOR
		//instance.createLinearManyRecursiveTasks();
		
		
	}
	
	ForkJoinPool createPrivatePool(){
		
		ForkJoinPool pool = new ForkJoinPool();
			
		return pool;
	}
	

	/*
	 * Crates unrealted tasks from within a thread that is not running in the pool.
	 * This creates a hgh steal count.
	 */
	void creatingManyNonRecursiveTasks() throws InterruptedException{
		
		System.out.println("creatingManyNonRecursiveTasks()");
		ForkJoinPool pool = createPrivatePool();
		
		for(int taskCount = 0; taskCount < 10000; ++taskCount) {
			pool.submit(new NonRecursiveAction(pool));
		}
		// HACK!!
		Thread.sleep(100000);
		pool.awaitQuiescence(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
	}
	
	/*
	 * Builds a tree of tasks. 
	 */
	void createTreeManyRecursiveTasks() throws InterruptedException{
		
		ForkJoinPool pool = createPrivatePool();
		
		ForkingRecursiveTreeTask rootTask = new ForkingRecursiveTreeTask(pool, 0);
		
		pool.submit(rootTask);
		
		// HACK!!
		Thread.sleep(100000);
		
		// Does not wait until all tasks are done.
		//pool.awaitQuiescence(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
	
	}
	
	/*
	 * Tasks fork other tasks recursively, but not in a tree. Each task has only one single child task
	 */
	void createLinearManyRecursiveTasks() throws InterruptedException{
		
		ForkJoinPool pool = createPrivatePool();
		
		ForkingRecursiveLinearTask rootTask = new ForkingRecursiveLinearTask(pool, 0);
		
		pool.submit(rootTask);
		
		// HACK!!
		Thread.sleep(100000);
		
		// Does not wait until all tasks are done.
		//pool.awaitQuiescence(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
	
	}
}


/*
 * Task class to show high steal count, instances are unrelated to each 
 * other and are forked from a thread that runs outside the pool.
 * 
 * No subtasks are created.
 * 
 * Each tasks executes a CPU-intensive method.
 * 
 * The longer the CPU-intensive method runs, the smaller the steal count gets. Why?
 */
class NonRecursiveAction extends RecursiveAction{
	
	ForkJoinPool pool;
	
	static final AtomicInteger instanceCounter = new AtomicInteger();
	int taskSeqeunceNumber; 
	
	NonRecursiveAction(ForkJoinPool pool){
		this.pool = pool;
		taskSeqeunceNumber = instanceCounter.incrementAndGet();
	}
	
	@Override
	protected void compute() {
	
			
		System.out.println("Task: " + taskSeqeunceNumber +" " + Thread.currentThread());
		
		//System.out.println(pool);
		System.out.println("Steal Count: " + pool.getStealCount());
		System.out.println("Active Threads : " + pool.getActiveThreadCount());
		cpuIntensiveLoop();
		//System.out.println("Task returns"+ Thread.currentThread());
		
	}

 	void cpuIntensiveLoop(){
	
 		// je größer die Zahl, desto geringer der Steal Count
 		for(long l = 0; l < 10000000L; ++l) {
		
 		}
 	}
}


/*
 * Task class to realize Tree
 */
class ForkingRecursiveTreeTask extends RecursiveAction {
	
	int depth;
	
	static final int maxDepth = 1000;
	ForkJoinPool pool;

	static final AtomicInteger instanceCounter = new AtomicInteger();
	int taskSeqeunceNumber;
	
	ForkingRecursiveTreeTask(ForkJoinPool pool, int depth){
		this.pool = pool;
		this.depth = depth;
		//System.out.println("Depth " + depth);
		taskSeqeunceNumber = instanceCounter.incrementAndGet();
		//System.out.println("Creation Thread: " + Thread.currentThread());
	}
	
	@Override
	protected void compute() {
		//System.out.println(pool);
		if(depth < 1000) {
			
			ForkingRecursiveTreeTask leftTask = new ForkingRecursiveTreeTask(pool, ++depth);
			ForkingRecursiveTreeTask rightTask = new ForkingRecursiveTreeTask(pool, ++depth);
			
			//System.out.println(pool);
			//System.out.println("Task " + instanceCounter.get() + " " + Thread.currentThread());
			System.out.println("Task " + taskSeqeunceNumber + "  " + "StealCount: " + pool.getStealCount());
			System.out.println("Active Threads: " + pool.getActiveThreadCount());
			
		
			
			
				
			
			leftTask.fork();
			rightTask.fork();
			
			cpuIntensiveLoop();
	
			rightTask.join();
			leftTask.join();
	
			
			System.out.println("Task returns");
		}
	}
	
	static void cpuIntensiveLoop(){
		for(long l = 0; l < 10000L; ++l) {
			
		}
	}
}


/*
 * Diese Klasse funktioniert nicht in meinem Sinne. Es laufen meist nur zwei Tasks gleichzeitig, daher Steal Count 0
 * // Bei dem  Ablauf
	// 	1.fork()
	// 	2.cpuIntesiveWork()
	// 	3.join()
	 Führt zu:
 * 		- KEin Steaal Count
 * -	- geringe CPU Load
 * 		- Zwei Aktive Threads
 * -  Ist die CPU Schleife lang, so laufen nur zwei Tasks parallel (am Anfang einmal 8, danach nur noch zwei) WARUM?
 * 
 *  Ohne Join habe ich eine gute CPU AUslastung, 8 aktive Threads und einen hohen Steal Count
 *   ALSO: Dieses PAttern funtkioniert mit Join nicht gut. 
 *   Mir ist aber noch nicht ganz klar warum.
 */
class ForkingRecursiveLinearTask extends RecursiveAction {
	
	int depth;
	int taskSeqeunceNumber;
	static final int maxDepth = 1000;
	ForkJoinPool pool;

	static final AtomicInteger instanceCounter = new AtomicInteger();
	
	ForkingRecursiveLinearTask(ForkJoinPool pool, int depth){
		this.pool = pool;
		this.depth = depth;
		//System.out.println("Depth " + depth);
		taskSeqeunceNumber = instanceCounter.incrementAndGet();
		//System.out.println("Creation Thread: " + Thread.currentThread());
	}
	
	@Override
	protected void compute() {
		//System.out.println(pool);
		if(depth < 1000) {
			
			ForkingRecursiveLinearTask recursiveTask = new ForkingRecursiveLinearTask(pool, ++depth);
			
			//System.out.println(pool);
			System.out.println("StealCount" + pool.getStealCount());
			System.out.println("Task " + taskSeqeunceNumber + "  " + "StealCount: " + pool.getStealCount());
			System.out.println("Active Threads: " + pool.getActiveThreadCount());
			
			
			// Bei dem  Ablauf
			// 	1.fork()
			// 	2.cpuIntesiveWork()
			// 	3.join()
			// habe ich keine Steals (theoretisch müsste ich mindestes vier haben):
			// Grund: Task wird geforkt, ein Thread holt ihn sich (steal) und dann arbeitet der Thrad vor sich hin
			// mit weiterer rekursiver Erzeugung
			
			
			recursiveTask.fork();
			
			cpuIntensiveLoop();
			// Wenn ich aber das join auskommentiere, habe ich viele Steals
			// Grund Die Tasks werden alle SOFORT geforkt, im selben Thread UNKLAR
			// JOIN IST PROBLEMATISCH
			recursiveTask.join();
	
			
			System.out.println("Task returns");
		}
	}
	
	static void cpuIntensiveLoop(){
		for(long l = 0; l < 100000000L; ++l) {
			
		}
	}
}


