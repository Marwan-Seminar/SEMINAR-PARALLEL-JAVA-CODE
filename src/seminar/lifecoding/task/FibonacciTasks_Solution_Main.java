// Copyright Marwan Abu-Khalil 2012

package seminar.lifecoding.task;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;



/*
 * This class implements Fibonacci Calcuataion in three versions
 * 1. Sequential approach
 * 2. Parallel approach that uses a threshold to limit parallelism to an efficient degree
 * 3. A naive approach, that parallelelizes each step in the tree of recursions
 *  
 *  Typical performance measurements on my machine: Core I7 Quadcore
 *  Fibonacci start SEQUENTIAL with Argument:  46 Fibonacci Result  of 46 is 2971215073 time(ms) 8516
 *  Fibonacci TASK starts PARALLEL with Argument:  46 and Threshold 38 Fibonacci Result  of 46 is 2971215073 time(ms) 2069
 *  Fibonacci start NAIVE with Argument:  46 Fibonacci Result  of 46 is 2971215073 time(ms) 34515
 *  
 *  The results are interesting with regard to the following:
 *  1. The parallelization can provide a linear speedup
 *  2. The naive parallelization with Tasks is much more stable than a naive parallelization with threads. 
 *  	The naive approach shown here is slow, but it does not crash the program, and its memory consumption 
 *  	is moderate. An identical approach with threads would crash the program.
 *  
 

*/
public class FibonacciTasks_Solution_Main{

	
	//static final boolean PARALLEL = true;
	
	static enum PARALLEL_MODE {SEQUENTIAL, PARALLEL, NAIVE} 
	
	static PARALLEL_MODE parallelMode = PARALLEL_MODE.SEQUENTIAL;
	
	
	
	public static void main(String[] args) {
		
		// TODO CHOOSE MODE 
		//parallelMode = PARALLEL_MODE.SEQUENTIAL;
		//parallelMode = PARALLEL_MODE.PARALLEL;
		parallelMode = PARALLEL_MODE.NAIVE;
		
	
		
		int arg = 46; // 48; // 46;
		long result =0;
		
		
		System.out.println("FibonacciTasks_Solution_Main(): Parallelized Tasks Version. Arguemnt:  " + arg); 
		
		long startTime = System.currentTimeMillis();
		long endTime = 0;
		
		if(parallelMode.equals(PARALLEL_MODE.SEQUENTIAL)){
			// sequential
			System.out.println("Fibonacci start SEQUENTIAL with Argument:  " + arg); 
			result = FibTask.sequentialFibo(arg);			
				
		}
		else if(parallelMode.equals(PARALLEL_MODE.PARALLEL)){
			// PARALLEL MODE WITH THRESHOLD
			
			// Threshold required for going over from parallel to sequential calculation if the recursion argument becomes small 
			FibTask.threshold = arg - Runtime.getRuntime().availableProcessors();
			
			System.out.println("Fibonacci TASK starts PARALLEL with Argument:  " + arg + " and Threshold " + FibTask.threshold); 
			
			// Create root task. This Task will create further Tasks recursively
			FibTask rootTask = new FibTask(arg);
			
			// Fork-Join Pool instance
			ForkJoinPool fjPool = new ForkJoinPool();
			
			// Start the Task in the Fork-Join-Pool and wait for its result
			result = fjPool.invoke(rootTask);	
			
			// Alternative Wait API: join()
			// result = rootTask.join();
			
		}else if (parallelMode.equals(PARALLEL_MODE.NAIVE)){
	
			System.out.println("Fibonacci start NAIVE with Argument:  " + arg); 
			
			ForkJoinPool fjPool = new ForkJoinPool();
				
			// Naive approach, without threshold. DO NOT USE
			FibTaskNaive rootTask = new FibTaskNaive(arg);
			
			result = fjPool.invoke(rootTask);
			
		}
		
		endTime = System.currentTimeMillis();
		System.out.println("Fibonacci Result  of "  + arg + " is " +result + " time(ms) " + (endTime - startTime));
	}

}

class FibTask extends RecursiveTask<Long>{

	int fib_argument; 
	long fib_result;
	
	volatile static int threshold;
	
	FibTask(int i){
		this.fib_argument = i;
	}
	
	@Override
	protected
	Long compute(){
		
		if(this.fib_argument <= 1){
			fib_result = 1;
			return fib_result;
		}else if(fib_argument < threshold){
			// Sequential calculation
			fib_result = sequentialFibo(fib_argument);
			return fib_result;
		}
	
		FibTask fib_min_1_task = new FibTask(fib_argument - 1);
		FibTask fib_min_2_task = new FibTask(fib_argument - 2);
		
		// Fork concurrent Child-Tasks
		fib_min_1_task.fork();
		fib_min_2_task.fork();
		
		
		// Wait for Child-Tasks to complete their compute() method
		// and fetch their results
		long fib_min_2 = fib_min_2_task.join();
		long fib_min_1 = fib_min_1_task.join();
		
		this.fib_result = fib_min_1 + fib_min_2;
	
		return this.fib_result;
	}
	
	
	static long sequentialFibo(long n){
		if(n<=1){
			return 1;
		}else{
			return sequentialFibo(n-1) + sequentialFibo(n-2);
		}
	}
}

////////// NAIVE APPROACH; DO NOT USE, SLOW!!! ///////////////////////////////

class FibTaskNaive extends RecursiveTask<Long>{

	int fib_argument; 
	long fib_result;

	FibTaskNaive(int i){
		this.fib_argument = i;
	}
	
	@Override
	protected
	Long compute(){
		
		if(this.fib_argument <= 1){
			fib_result = 1;
			return fib_result;
		} else {
	
			FibTaskNaive fib_min_1_task = new FibTaskNaive(fib_argument - 1);
			FibTaskNaive fib_min_2_task = new FibTaskNaive(fib_argument - 2);
			
			// Fork concurrent Child-Tasks
			fib_min_1_task.fork();
			fib_min_2_task.fork();
			
			
			// Wait for Child-Tasks to complete their compute() method
			// and fetch their results
			long fib_min_2 = fib_min_2_task.join();
			long fib_min_1 = fib_min_1_task.join();
			
			this.fib_result = fib_min_1 + fib_min_2;
		
			return this.fib_result;
		}
	}
}
