// Copyright Marwan Abu-Khalil 2012

package seminar.examples.thread.executor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TrivialExecutorTest {

	static final int NR_OF_THREADS = 4;
	static final int NR_OF_TASKS = 16;
	// Inserts some tasks into a Executor
	public void startTest(){
		
		
		ExecutorService executor = Executors.newFixedThreadPool(NR_OF_THREADS); 
	
		for(int iTask = 0 ; iTask < NR_OF_TASKS; iTask++){
			executor.submit(new TrivialTask());
		}
		
		
	}
	
	static class TrivialTask implements Runnable{
		
		static int idCounter = 0;
		int taskID;
		
		TrivialTask(){
			synchronized(TrivialTask.class){
				taskID = idCounter++;
			}
		}
		
		public void run(){
			System.out.println("Task " + taskID + " running in Thread " + Thread.currentThread());
		}
	}
}
