// Copyright Marwan Abu-Khalil 2012

package seminar.examples.task.blocking;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

// ExecutorService can not handle Tasks that wait for 
// other tasks. DEADLOCK (in Fork-Join-Pool this is fixed)
public class ExecutorDeadlockingTasks {


	static final ExecutorService executorService = Executors.newFixedThreadPool(4);
	static final AtomicInteger atInt = new AtomicInteger();
	
	// DEADLOCK
	void runWaitingTasks(){
		executorService.submit(new ForkingTask());
	}
	static class ForkingTask implements Callable<Integer>{

		@Override
		public Integer call() {
			int id = atInt.getAndIncrement();
			System.out.println("ForkingCallable call() " + id);
			Callable<Integer> task = new ForkingTask();  
			Future<Integer> future = executorService.submit( task);
			try {
				future.get();
			} catch (Exception e) {
				throw new Error(e);
			}
			return id;
		}
	}
	
	public static void main(String[] args){
		new ExecutorDeadlockingTasks().runWaitingTasks();
	}
}
