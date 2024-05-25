// Copyright Marwan Abu-Khalil 2012

package seminar.exercises.task.counted_completer.fibonacci.base;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;



/*
 * Basis für die Übung 
 * 
 * Parallelisieren Sie den rekursiven Fibonacci-Algorithmus mit CountedCompleter
 * 
 * Enthält eine parallelisierte Fibonacci Implementierung auf Basis von ForkJoinTask
 * 
 * Diese ist nicht perfomance-optimal, ohne Threshold, da es in dieser Aufgabe nicht primär um Perfoamace sondern 
 * um die Benutzung der CountedCompleter API geht.
*/
public class FibonacciCountedCompleterBase{
	
	public static void main(String[] args) {
	
		int arg =  32; //10;  // 46;
		long result = 0;	
		
		System.out.println("FibonacciCountedCompleterBase: ForkJoinTasks Version. Arguemnt:  " + arg); 
		
		long startTime = System.currentTimeMillis();
		long endTime = 0;
	
		System.out.println("Fibonacci start with Argument:  " + arg); 
		
		ForkJoinPool fjPool = new ForkJoinPool();
			
		// Naive approach, without threshold. DO NOT USE
		FibTasks rootTask = new FibTasks(arg);
		
		result = fjPool.invoke(rootTask);
		
		endTime = System.currentTimeMillis();
		System.out.println("Fibonacci Result  of "  + arg + " is " +result + " time(ms) " + (endTime - startTime));
	}

}


class FibTasks extends RecursiveTask<Long>{

	int fib_argument; 
	long fib_result;

	FibTasks(int i){
		this.fib_argument = i;
	}
	
	@Override
	protected
	Long compute(){
		
		if(this.fib_argument <= 1){
			fib_result = 1;
			return fib_result;
		} else {
	
			FibTasks fib_min_1_task = new FibTasks(fib_argument - 1);
			FibTasks fib_min_2_task = new FibTasks(fib_argument - 2);
			
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
