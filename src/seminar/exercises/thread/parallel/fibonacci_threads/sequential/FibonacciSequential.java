// Copyright Marwan Abu-Khalil 2012

package seminar.exercises.thread.parallel.fibonacci_threads.sequential;


/*
 * Base for exercise Fibonacci Parallelization with Threads 
 * 
 * Expected values on my machine, core i7, 8-Core:
 * Fibo: 46 = 2971215073
 * Fibo 46: Sequential: 9 Sec, Parallel 2 Sec, Naive Crash 
 *
 */
public class FibonacciSequential {

	public static void main(String[] args) throws InterruptedException{
		FibonacciSequential instance = new FibonacciSequential();
		
		
		int arg =  46; //25; // 46
		
		instance.testFiboSequential(arg);
		
	}
	
	 
	
	void testFiboSequential(int arg ){
		
		System.out.println("SequentialFibonacci stared with Argument: " + arg);
		long start = System.currentTimeMillis();
		long result = new SequentialFibonacci().fibo(arg);
		long end = System.currentTimeMillis();
		System.out.println("SequentialFibonacci:  Argument: " + arg + " Result: " + result +  " time(ms): " + (end - start));
			
	}
	
}

/*
 * The sequential recursive Fibonacci Algorithm.
 */
class SequentialFibonacci{
	
	long fibo(long n){
		if(n<=1){
			return 1;
		}else{
			// TODO: Introduce Threads here, to execute the recursive calls in parallel. 
			return fibo(n-1) + fibo(n-2);
			
			// TODO: Wait for Threads to return
			
			// TODO: Be aware: This can create so many threads, that your program crashes. Find a solution for that!
		}
	}
}