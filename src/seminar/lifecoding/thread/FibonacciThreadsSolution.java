// Copyright Marwan Abu-Khalil 2012

package seminar.lifecoding.thread;

import java.util.concurrent.atomic.AtomicInteger;

// Fibo: 46 = 2971215073

// These classes show how to recursively calculate the Fibonacci-Sequence
// 1. Sequentially
// 2. Naively parallelized
// 3. Parallelized with threshold
// 4. Parallelized with Thread-Pool
// Expected values on my machine, core i7, 8-Core:
// Fibo 46: Sequential: 9 Sec, Parallel 2 Sec, Naive Crash 
public class FibonacciThreadsSolution {

	public static void main(String[] args) throws InterruptedException{
		FibonacciThreadsSolution instance = new FibonacciThreadsSolution();
		
		
		int arg =  48; //25; // 46
		
		//instance.testFiboSequential(arg);
		
		instance.testFiboThreshold(arg);
		
		//instance.testFiboNaive(arg);		
		
	}
	
	 
	
	// FiboThreshold: 46 = 535828592 time: 4797
	void testFiboThreshold(int arg) throws InterruptedException{
		
		System.out.println("FiboThreshold started with: Argument: " + arg); 
		FiboThreshold fiboThreshold = new FiboThreshold(arg);
		long start = System.currentTimeMillis();
		fiboThreshold.start();
		
		fiboThreshold.join();
		long end = System.currentTimeMillis();
		System.out.println("FiboThreshold returned: Argument: " + arg + " Result: " + fiboThreshold.result +  " time(ms): "  + (end-start) );
		System.out.println("Threshold: " + FiboThreshold.threshold + " NrOFThreads: " + FiboThreshold.threadCount  );
	}
	
	void testFiboNaive(int arg ) throws InterruptedException{
		
		System.out.println("FiboNaive started with argument: " + arg);
		FiboNaive fiboNaive = new FiboNaive(arg);
		fiboNaive.start();
		
		fiboNaive.join();
		System.out.println("FiboNaive: " + arg + " = " + fiboNaive.result );
	}
	
	void testFiboSequential(int arg ){
		
		System.out.println("SequentialFibonacci stared with Argument: " + arg);
		long start = System.currentTimeMillis();
		long result = new SequentialFibonacci().fibo(arg);
		long end = System.currentTimeMillis();
		System.out.println("SequentialFibonacci:  Argument: " + arg + " Result: " + result +  " time(ms): " + (end - start));	
	}
}

class SequentialFibonacci{
	
	long fibo(long n){
		if(n<=1){
			return 1;
		}else{
			return fibo(n-1) + fibo(n-2);
		}
	}
}

/*
 * This class realizes a naive parallelization of fibonacci. It can cause out of memory errors.
 */
class FiboNaive extends Thread{
	long arg;
	long result;
	
	FiboNaive(long arg){
		this.arg = arg;
	}
	
	public void run(){
		if(arg <= 1){
			result = 1;
		}else{
			// Start new Threads for recursion
			FiboNaive fibo_n_1 = new FiboNaive(arg - 1);
			FiboNaive fibo_n_2 = new FiboNaive(arg - 2);
			// Start the threads
			fibo_n_1.start();
			fibo_n_2.start();
			// Wait for the threads run() method to complete
			try {
				fibo_n_1.join();
				fibo_n_2.join();
			} catch (InterruptedException e) {
				throw new Error();
			}
			
			// use threads results
			result = fibo_n_1.result + fibo_n_2.result;
		}
	}
}

 
/*
 * Solution: This class realizes an efficient and stable parallelization. It limits the number of crated threads with a threshold.
 */
class FiboThreshold extends Thread{
	int arg;
	long result;
	
	static volatile int threshold;
	static volatile boolean thresholdInitialized = false;
	static AtomicInteger threadCount = new AtomicInteger();
	
	FiboThreshold(int arg){
		this.arg = arg;
		
		if(!thresholdInitialized){
			threshold = arg - Runtime.getRuntime().availableProcessors();
			System.out.println("theshold " + threshold + " CPU count: " + Runtime.getRuntime().availableProcessors());
			thresholdInitialized = true;
		}
	}
	
	long fiboSequential(int seqArg){
		if(seqArg <= 1){
			return 1;
		}else{
			return fiboSequential(seqArg - 1) + fiboSequential(seqArg -2);
		}
	}
	public void run(){
		threadCount.incrementAndGet();
		if(arg <= 1){
			result = 1;
		}else if( arg < threshold ){
			// Sequential calculation
			result = fiboSequential(arg);
		}
		else{
			// Parallel calculation: Start new Threads for recursion
			FiboThreshold fibo_n_1 = new FiboThreshold(arg - 1);
			FiboThreshold fibo_n_2 = new FiboThreshold(arg - 2);
			// Start the threads
			fibo_n_1.start();
			fibo_n_2.start();
			// Wait for the threads run() method to complete
			try {
				fibo_n_1.join();
				fibo_n_2.join();
			} catch (InterruptedException e) {
				throw new Error();
			}
			
			// use threads results
			result = fibo_n_1.result + fibo_n_2.result;
		}
	}
}

