package seminar.exercises.task.steal_count.base;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/*
 * Basis für Übung: Steal-Count
 * 
 * Zeigen Sie, dass es Situationen gibt, in denen Work-Stealing stattfindet.
 * 
 * Programmieren Sie ein Szenario, bei dem Viele Tasks erzeugt werden.
 * Diese Erzeugung soll auf eine Art und Weise stattfinden, die dazu führt, dass der Fork-Join-Pool
 * einen Großteil diser Tasks mit Work-Stealing behandelt.
 * 
 */
public class ForkJoinPoolStealCountBase {

	
	public static void main(String[] args) throws InterruptedException {
		ForkJoinPoolStealCountBase instance = new ForkJoinPoolStealCountBase();
		
		// Steal-Count demonstration
		
		// Hint: Unrelated Tasks forked by a Thread create stealing
		instance.creatingManyNonRecursiveTasks();	
		
	}
	
	

	/*
	 * Crates unrelated tasks from within a thread that is not running in the pool.
	 * This creates a high steal count.
	 */
	void creatingManyNonRecursiveTasks() throws InterruptedException{
		
		System.out.println("creatingManyNonRecursiveTasks()");
		
		ForkJoinPool pool = new ForkJoinPool();
		
		// TODO Fork Tasks
		
		// HACK!!
		//Thread.sleep(100000);
		pool.awaitQuiescence(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
		
	}
}


/*
 * Task class to show  steal count
 *
 */
class NonRecursiveAction extends RecursiveAction{
	
	ForkJoinPool pool;
	
	@Override
	protected void compute() {
	
			
		System.out.println("Task:  " + Thread.currentThread());
		
		//System.out.println(pool);
		System.out.println("Steal Count: " + pool.getStealCount());
		System.out.println("Active Threads : " + pool.getActiveThreadCount());
		
		
	}

}

