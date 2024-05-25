// Copyright Marwan Abu-Khalil 2012

package seminar.examples.thread.fibonacci;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

// Demonstrates Thread-Pool based parallelization of Fibonacci
// Intorduces: ExcecutorService, Callable, Future 
// THIS CODE DOES NOT WORK: The ThreadPool does not handle the blocking Tasks. 
//   If a fixed size Thread-Pool is used, deadlock occurs. If a CachedThreadPool
//   is used, too many threads are created!
public class FibonacciThreadPool {
	
	public static void main(String[] args) throws InterruptedException, ExecutionException{
		int arg = 10;
		FibonacciThreadPool instance = new FibonacciThreadPool();
		long result = instance.fibonacci(arg);
		System.out.println("FibonacciThreadPool: arg: " + arg + " result: " + result);
	}
	
	public long fibonacci(int arg) throws InterruptedException, ExecutionException{
		FibonacciCallable fiboCallableRoot = new FibonacciCallable(arg);
		Future<Long> futureResult =  threadPool.submit(fiboCallableRoot);
		return futureResult.get();
	}
	
	// Thread-Pool Service
	//final static ExecutorService threadPool = Executors.newCachedThreadPool();
	final static ExecutorService threadPool = Executors.newFixedThreadPool(25);
	
	// A Callable is a Task that returns a value. A Callable can be run inside a Thread-Pool
	// FibonacciCallable: Creates Callables recursively, throughout the tree
	static class FibonacciCallable implements Callable<Long>{
		long arg;
		FibonacciCallable(long arg){
			this.arg = arg;
		}
		
		public Long call() throws Exception{
		  if(arg<=1){
			  return 1L;
		  }else{
			  // Create two Callable instances
			  FibonacciCallable fibo_n_1 = new FibonacciCallable(arg - 1);
			  FibonacciCallable fibo_n_2 = new FibonacciCallable(arg - 2);
			  
			  System.out.println("Submitting tasks " + (arg - 1) + " and " + (arg - 2) + " from Thread" + Thread.currentThread());
			  // Submitt Callables to Thread-Pool, receive Futures
			  Future<Long> futureResult_n_1 = threadPool.submit(fibo_n_1);
			  Future<Long> futureResult_n_2 = threadPool.submit(fibo_n_2);
			  
			  return futureResult_n_1.get() + futureResult_n_2.get();
			}
		}
	}

}
