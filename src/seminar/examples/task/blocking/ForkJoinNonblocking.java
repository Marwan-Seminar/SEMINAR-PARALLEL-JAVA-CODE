// Copyright Marwan Abu-Khalil 2012

package seminar.examples.task.blocking;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.atomic.AtomicInteger;

// The same scenario as is ExecutorDeadlocking works with 
// Fork-Join-Pool
// Tasks, rekursively forking other tasks and waiting for their results do 
// not deadlock in FJ-Pool
public class ForkJoinNonblocking {

	static final ForkJoinPool fjPool = new ForkJoinPool();
	static final AtomicInteger atInt = new AtomicInteger();
	
    void runWaitingTasks() throws InterruptedException, ExecutionException{
		
    	ForkingTask task = new ForkingTask();
    	fjPool.invoke(task); //   .submit(new ForkingTask());
		int taskReturnValue = task.get();
		System.out.println("ID returned by tasks: " + taskReturnValue);
	}
	
	static class ForkingTask extends RecursiveTask<Integer> {
		@Override
		protected Integer compute() {
			int id = atInt.getAndIncrement();
			if(id>= 7){return id;}
			System.out.println("call() " + id + " thread: " + Thread.currentThread().getId());
			ForkingTask task = new ForkingTask();  
			
			// Starts task running in the currently active FJ-Pool
			task.fork();
			int retval = -1;
			try {
				retval = task.get();
				System.out.println("ret " + id + " thread: " + Thread.currentThread().getId());
			} catch (Exception e) {
				throw new Error(e);
			}
			return retval;
			}	
	}
	public static void main(String[] args) throws InterruptedException, ExecutionException{
		new ForkJoinNonblocking().runWaitingTasks();
	}
}
