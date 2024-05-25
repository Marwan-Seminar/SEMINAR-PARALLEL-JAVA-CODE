// Copyright Marwan Abu-Khalil 2012

package seminar.examples.task.blocking;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.atomic.AtomicInteger;

// Shows behavior of FJ-Pool in case of
// longrunning tasks
// Longrunning Tasks block the Threads in the pool
// Tasks that are not scheduled to the (e.g 4) Thereads
// suffer from STARVATTION until the Tasks in the pool 
// are done.
public class LongrunningTasks {

	ForkJoinPool fjPool = new ForkJoinPool();
	AtomicInteger atInt = new AtomicInteger();
	
	
	void runTasks() {
		int NR_OF_TASKS = 5;
		ForkJoinTask[] tasks = new ForkJoinTask[NR_OF_TASKS];
		for(int i = 0 ; i < NR_OF_TASKS; ++i){
			tasks[i]  = new RecursiveAction(){
				protected void compute(){
					System.out.println("longrunningMethod: " + atInt.incrementAndGet() 
							+ " called in Thread " + Thread.currentThread().getId() );
					try {
						Thread.sleep(100000);
					} catch (InterruptedException e) {
						throw new Error(e);
					}
				}};
			fjPool.execute(tasks[i]);
		}
		for(int i = 0; i < NR_OF_TASKS; ++i){
			tasks[i].join();
		}
	}
	public static void main(String[] args){
		new LongrunningTasks().runTasks();
	}
}

class LongRunningTask extends RecursiveAction{
	
	int global_data;
	
	@Override
	protected void compute(){
		longrunningMethod();
	}
	
	void longrunningMethod(){
		System.out.println("longrunningMethod");
		while(true){
			global_data = global_data * global_data;
		}
	}
}
