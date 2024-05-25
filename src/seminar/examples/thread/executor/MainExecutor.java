// Copyright Marwan Abu-Khalil 2012

package seminar.examples.thread.executor;

/* Demonstrates usage of Executor Services
 * Executor is an interface to a thread pool
 * Runnable or Callable instances can be used to encapsulate a "task".
 * The executor service returns a Future
 */
public class MainExecutor {

	public static void main(String[] args) throws Exception{
		//new TrivialExecutorTest().startTest();
		new ExecuteTasksWithReturnValue().runTest();
	}
}
