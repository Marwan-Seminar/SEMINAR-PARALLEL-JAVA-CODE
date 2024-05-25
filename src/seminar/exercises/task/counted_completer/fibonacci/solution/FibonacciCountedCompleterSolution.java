package seminar.exercises.task.counted_completer.fibonacci.solution;

import java.util.concurrent.CountedCompleter;
import java.util.concurrent.ForkJoinPool;

/*
 * Implements Fibonacci based on CountedCompleter
 * 
 * For API documentation and usage hints fro CountedCompleter see:
 * https://docs.oracle.com/javase/8/docs/api/index.html?java/util/concurrent/CountedCompleter.html
 */
public class FibonacciCountedCompleterSolution {

	public static void main(String[] args) {

		FibonacciCountedCompleterSolution instance = new FibonacciCountedCompleterSolution();
		instance.runAlgo();

	}

	void runAlgo() {
		
		int fibonacciArgument = 32;
		
		FibonacciCountedCompleter fibo = new FibonacciCountedCompleter(null, fibonacciArgument);

		long startTime = System.currentTimeMillis();
		
		
		ForkJoinPool.commonPool().submit(fibo);

		fibo.join();

		System.out.println("Fibonacci Result is " +fibo.fiboResult);
		System.out.println("time(ms) " + (System.currentTimeMillis() - startTime));
	}

}

/*
 * Nur zum Zweck der Demonstration der CountedCompleter API, nicht effizient implementiert!
 * 
 * Diese Implemntierung weicht vom Doug Lea Beispiel im JDK ab, sie forkt beide
 * Subtaks. Das macht es aber erforderlich, dass in
 * onCompletion(CountedCompleter<?> caller) folgendes geschieht:
 * - 1. alles auf null geprüft wird, 
 * - 2. auch wenn der calller == this ist, die summation druchgeführt werden muss
 * 
 * 
 * Bemerkungen dazu:
 * 1. Der Grund für die Summation im Fall caller == this, ist, dass folgender Ablauf möglich ist:
 *  	Die beiden geforketen Tasks laufen und dekrementieren über tryComplete() this.pendingCount auf 0.
 * 		- Dann wird in compute() tryComplete() gerufen, und somit onCompletion(this) aufgerufen 
 * 		- In diesem Fall muss die Addition durchgeführt werden, da die beiden tryComplte() calls, die durch die
 * 			geforkten Tasks gerufen wurden, noch nicht pendingCount 0 vorgefunden haben,
 * 			also nicht onCompletion() aufgerufen haben.
 * 
 * 2. Die vier (!!!) null Abfragen in onCompletion() sind nötig (warum, ist mir nicht ganz klar).
 *  
 */
class FibonacciCountedCompleter extends CountedCompleter<Integer> {

	int argument;
		Integer fiboResult;

	FibonacciCountedCompleter fib_min_1;
	FibonacciCountedCompleter fib_min_2;

	FibonacciCountedCompleter(FibonacciCountedCompleter parent, int arg) {
		super(parent);
		this.argument = arg;

		if (parent == null) {
			System.out.println("FibonacciMarwCompleter: NON JDK STYLE, TWO FORKS started with argument: " + arg);
		}
	}

	public void compute() { 
		// System.out.println("compute()" + argument);
		if (argument > 1) {

			setPendingCount(2);
			fib_min_1 = new FibonacciCountedCompleter(this, argument - 1);
			fib_min_1.fork();

			fib_min_2 = new FibonacciCountedCompleter(this, argument - 2); // left child
			fib_min_2.fork();
		} else {
			// System.out.println("compute() set result" + argument + " " + this);
			this.fiboResult = 1;
		}
		tryComplete();
	}


	@Override
	public void onCompletion(CountedCompleter<?> caller) {

		// Abweichend von der JDK Vorlage, wird hier auch addiert, wenn caller == this ist

		// !!! DIESE NULL ABRAGEN SIND ESSENTIELL; SONST LÄUFT DAS PROGRAMM NICHT!!!
		// Mir ist noch nicht ganz klar warum
		if (fib_min_1 == null || fib_min_1.fiboResult == null || fib_min_2 == null || fib_min_2.fiboResult == null) {
			return;
		}

		fiboResult = fib_min_1.fiboResult + fib_min_2.fiboResult;

	}
}
