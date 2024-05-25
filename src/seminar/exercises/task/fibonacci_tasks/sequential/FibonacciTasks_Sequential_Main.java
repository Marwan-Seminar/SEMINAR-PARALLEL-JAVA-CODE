// Copyright Marwan Abu-Khalil 2012

package seminar.exercises.task.fibonacci_tasks.sequential;



/*
 * Sequential Fibonacci as base for Task Parallelization
 */
public class FibonacciTasks_Sequential_Main{

	
	public static void main(String[] args) {
		
		int arg = 46;
		
		System.out.println("FibonacciTasks_Sequential_Main Sequential Version for Tasks Parallelization "  + arg);
		
		long startTime = System.currentTimeMillis();
		
		// sequential
		long result = FibSequential.sequentialFibo(arg);
		long endTime = System.currentTimeMillis();
		System.out.println("Fibonacci Sequential of "  + arg + " is " +result + " time(ms) " + (endTime - startTime));

	}
	

}

class FibSequential{
	
	static long sequentialFibo(long n){
		if(n<=1){
			return 1;
		}else{
			return sequentialFibo(n-1) + sequentialFibo(n-2);
		}
	}
}
