package seminar.exercises.task.loop_parallel.base;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveTask;

/*
 * Basis für Aufgabe: 
 * PArallelisieren Sie ein langlaufende Schleife mit ForkJoinTasks 
 */
public class LoopParallelizationTasksBase {
	
	
	
	final long LOOPSIZE = 40000000000L;

	
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		
		
	
		new LoopParallelizationTasksBase().sequentialLoop();
		
	}
	
	void sequentialLoop() {
		System.out.println("Sequential Loop started  ");
	
		long startTime = System.currentTimeMillis();
		
		long counter = 0;
		for(long i = 0; i < LOOPSIZE; ++i) {
			counter++;
		}
		
		System.out.println("Sequential Time: " + (System.currentTimeMillis() - startTime) + " Result: " + counter);
		
	}
	
}

/////////////// Hinweise zur ForkJoinTask API //////////////////////////

/*
 * Hinweise zur ForkJoinTask API
 * 
 * 1. Task Klasse schreiben
 * 
 * 2. Task in den ForkJoinPool einstellen und auf Ergebnis warten
 */

/*
 * 1 eine ForkJoinTask Klasse
 */
class LoopTask extends RecursiveTask<Long>{
	
	@Override
	public Long compute(){
		// TODO: hier den Code schreiben, der nebenläufig ausgeführt werden soll
		
		return 0L;
	}
}

/*
 * 2 ForkJoinTask mit Hilfe des Common-Pools starten und auf Ergebnis warten
 */
class StartAndWait{
	
	void doStartAndWait(){
		
		// Task erzeugen
		LoopTask loopTask = new LoopTask();
		
		// Future Objekt ermöglicht asynchrones Warten
		ForkJoinTask<Long> future =
				
				// Task im Common Pool starten
				ForkJoinPool.commonPool().submit(loopTask);
		
		future.join();
	}
	
}




