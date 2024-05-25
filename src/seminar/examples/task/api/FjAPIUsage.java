// Copyright Marwan Abu-Khalil 2012

package seminar.examples.task.api;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.RecursiveTask;

// Demonstrates basic usage of the Fork-Join API
public class FjAPIUsage {

	static ForkJoinPool fjPool = new ForkJoinPool();
	
	// Creating
	void creatingTasks(){
		ForkJoinTask task = new MyTaskWithoutResult();
	}
	
	// Starting
	void startingTasks(){
		// Start from outside a task:
		fjPool.execute(new MyTaskWithoutResult());
		
		// Start from inside a Task:  task.fork() (so here it would be illegal, see MyForkingTask)
		ForkJoinTask task = new MyTaskWithoutResult();
		fjPool.execute(new MyForkingTask());
		
	}
	
	// Waiting
	void waitingForTasks(){
		ForkJoinTask<Void> task = new MyTaskWithoutResult(); 
		fjPool.execute(task);
		task.join();
		System.out.println("Task Returned" );
	}
	
	// Data return
	void getDataFromTask() throws InterruptedException, ExecutionException{
		ForkJoinTask<Integer> future = fjPool.submit(new MyTaskWithResult());
		
		int result = future.get();
		System.out.println("Result: " + result);
	}
	
	// Continuation
	
	
	public static void main(String[] args) throws InterruptedException, ExecutionException{
		FjAPIUsage instance = new FjAPIUsage();
		
		//instance.creatingTasks();
		
		//instance.startingTasks();
		
		//instance.getDataFromTask();
		
		instance.waitingForTasks();
		Thread.sleep(3000);
	}
}


// ForkJoinTask<T> is a base class, that usually is not directly subclassed
// User tasks inherit from 
// RecursiveTask<Integer> if they return a result
// or from RecursiveAction
class MyTaskWithResult extends RecursiveTask<Integer>{

	@Override
	protected Integer compute() {
		System.out.println("MyTaskWithResult " + this);
		return 4711;
	}
}

class MyTaskWithoutResult extends RecursiveAction{

	@Override
	protected void compute() {
		System.out.println("MyTaskWithoutResult: " + this);
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {throw new Error(e);	}
	}
	
}

class MyForkingTask  extends RecursiveAction{
	static int depth = 0;
	protected void compute() {
		System.out.println("MyForkingTask: " + this);
		if(depth++ > 0) {return;};
		// Start from within a Task
		new MyForkingTask().fork();
	}
}