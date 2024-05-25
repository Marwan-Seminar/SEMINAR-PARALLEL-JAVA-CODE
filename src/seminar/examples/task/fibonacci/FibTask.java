// Copyright Marwan Abu-Khalil 2012

package seminar.examples.task.fibonacci;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;


// Fibonacci sequence calculation via tasks
// Performance example:
// Fibonacci Fork-Join of 46 is 2971215073 time(ms) 12343
// Sequential:
// Fibonacci Sequential of 46 is 2971215073 time(ms) 18547
public class FibTask extends RecursiveTask<Long>{

	static final boolean PARALLEL = true;
	
	int i; 
	long fib_i;
	volatile static int threshold;
	
	FibTask(int i){
		this.i = i;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		int arg = 46;
		
		threshold = arg - Runtime.getRuntime().availableProcessors();
		
		long startTime = System.currentTimeMillis();
		
		if(PARALLEL){
			ForkJoinPool fjPool = new ForkJoinPool();
			FibTask rootTask = new FibTask(arg);
			fjPool.invoke(rootTask);
			long endTime = System.currentTimeMillis();
			System.out.println("Fibonacci Fork-Join of "  + rootTask.i + " is " + rootTask.fib_i + " time(ms) " + (endTime - startTime));
		}else{
			// sequential
			long result = sequentialFibo(arg);
			long endTime = System.currentTimeMillis();
			System.out.println("Fibonacci Sequential of "  + arg + " is " +result + " time(ms) " + (endTime - startTime));
	
		}
	}

	@Override
	protected
	Long compute(){
		
		if(this.i <= 1){
			fib_i = 1;
			return fib_i;
		}else if(i < threshold){
			// Sequential calculation
			fib_i = sequentialFibo(i);
			return fib_i;
		}
	
		FibTask fib_min_1_task = new FibTask(i - 1);
		FibTask fib_min_2_task = new FibTask(i - 2);
		
		fib_min_1_task.fork();
		
		long fib_min_2 = fib_min_2_task.compute();
		
		long fib_min_1 = fib_min_1_task.join();
		
		
		this.fib_i = fib_min_1 + fib_min_2;
		return this.fib_i;
	}
	
	
	static long sequentialFibo(long n){
		if(n<=1){
			return 1;
		}else{
			return sequentialFibo(n-1) + sequentialFibo(n-2);
		}
	}
}
