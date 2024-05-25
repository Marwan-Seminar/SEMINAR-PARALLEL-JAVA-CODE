// Copyright Marwan Abu-Khalil 2012

package seminar.examples.thread.executor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;
import java.util.Stack;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

/*
 * This class demonstrates usage of Future and Callable to obtain results from Executor scheduled Tasks
 */
public class ExecuteTasksWithReturnValue {

	ExecutorService executor = Executors.newFixedThreadPool(4);
	static final int NR_OF_TASKS = 8;
	
	Stack<Future<String>> stackOfFutures = new Stack<Future<String>>();
	
	public void runTest() throws IOException, InterruptedException, ExecutionException{
		
		startCallables();
		userDialogue();
	}


	private void startCallables() {
		for(int i = 0; i < NR_OF_TASKS; ++i){
			Future<String> futureResult = executor.submit(new TaskWithReturnValue());
		
			stackOfFutures.push(futureResult);
		}
	}
	
	
	/*
	 * Prompts the user to fetch the results
	 */
	void userDialogue() throws IOException, InterruptedException, ExecutionException{

		BufferedReader shellReader = new BufferedReader( new InputStreamReader(System.in));
		
		for(Future<String> future : stackOfFutures ){
			System.out.println("Hit any key for next Future-Restult");
			shellReader.readLine();
 

			String result = future.get();
			System.out.println("Future-Restult: " + result);
			
		}
	}
	
	static class TaskWithReturnValue implements Callable<String> {
	
		static final AtomicInteger ID_GENERATOR = new AtomicInteger(1);
		
		public String call(){
		
			String name = "TaskWithReturnValue_" + ID_GENERATOR.getAndIncrement();
			
			int result = new Random().nextInt();
			return  name + " returned: " + result;
		}
	}
}
