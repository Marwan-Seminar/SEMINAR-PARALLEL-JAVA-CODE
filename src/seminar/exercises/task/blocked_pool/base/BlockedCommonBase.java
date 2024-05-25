package seminar.exercises.task.blocked_pool.base;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

/*
 * Basis für die Übung Blocking Pool 
 * Starten Sie im Common-Pool blockierende Tasks oder langlaufende Tasks.
 * Starten Sie dann weitere Tasks im Common-Pool, und zeigen Sie, dass diese nie geschedult werden.
 */
public class BlockedCommonBase {

	
	public static void main(String[] args) throws InterruptedException {
		
		BlockedCommonBase instance = new BlockedCommonBase();
		
		new Thread() {
			public void run() {
				System.out.println("Starting bad Algorithm");
				instance.startBadAlgorithm();
			}
		}.start();
		
		// HACK: make sure, that first all the bad tasks are started, and only than the friendly task is started
		Thread.sleep(1000);
	
		new Thread() {
			public void run() {
				System.out.println("Starting good Algorithm: friendly Task should run...");
				instance.startFriendlyAlgorithm();
			}
		}.start();
		
		ForkJoinPool.commonPool().awaitQuiescence(100000, TimeUnit.SECONDS);
	}
	


	void startBadAlgorithm() {
		
		// TODO: viele "böse" Tasks in den Pool submitten, die diesen blockieren, durch IO oder lang laufende CPU Aktivität
	}
	
	void startFriendlyAlgorithm() {
		// TODO: eine einfache Task in den Pool einstellen, die z.B. auf die Shell schreibt
		ForkJoinPool.commonPool().submit(() -> {});
	}	

	/*
	 * Methode, die lange die CPU beansprucht
	 */
	void cpuIntensiveMethod() {
		System.out.println("CPU Intesive Loop");
		for(long l = 0; l < 100000000000L; ++l) {
				
		}
		System.out.println("LOOP returns");
	}
	
	/*
	 * Methode die blockiert, indem sie auf Shell Eingaben wartet.
	 */
	void blockingIOMethod() {
		System.out.println("Reading next Line, Thread: " + Thread.currentThread().getName());
		
		// Blocking IO call
		new Scanner(System.in).nextLine();
		
		/*
		try {
			new BufferedReader(new InputStreamReader(System.in)).readLine();
		} catch (IOException e) {
			throw new Error(e);
		}
		*/
		
		System.out.println("Got next Line");
	}
	
	
}


////////// HINWEISE ZUR POOL API /////////
class DemoPool{

	void demo() {
		// Die Anzahl der Threads im Pool erfragen:
		int nrOfPoolThreads = ForkJoinPool.getCommonPoolParallelism();
	
		// Warten, bis keine Aktivität mehr im Pool stattfindet
		ForkJoinPool.commonPool().awaitQuiescence(100000, TimeUnit.SECONDS);
	}	
}