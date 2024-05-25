// Copyright Marwan Abu-Khalil 2012

package seminar.examples.task.api;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;


/* Übung 3.2: Trivialen Baum aus Tasks bauen und warten

Bauen Sie einen Baum aus Tasks (z.B. der Tiefe 5, und jeder Knoten hat zwei Kinder)

Zeigen Sie, dass es passieren kann, dass das Programm sich beendet, bevor alle Tasks zuende gelaufen sind.

Reparieren Sie das Verhalten, so dass das Programm genau dann zurückkehrt, wenn alle Tasks zuende gelaufen sind.
*/


// Fork-Join Framework
// This class spans up binary tree (actually of height HEIGHT +1)
// Performance (Dual-Core): 
// Tree-height: 20 recursiveNrOfNodes: 2097151 numberOfNodesRun 2097151 time: 797
// Tree-height: 23 recursiveNrOfNodes: 16777215 numberOfNodesRun 16777215 time: 6562
// 
public class TrivialRecursiveTask extends RecursiveTask<Integer> {

	
	final static int HEIGHT = 23;
	
	static ForkJoinPool fjPool = new ForkJoinPool();
	
	static final AtomicLong atInt = new AtomicLong(0);
	long height;
	long id;
	
	public static void main(String[] args){
		
		long begin = System.currentTimeMillis();
		System.out.println("TrivialRecursiveTask main");
		TrivialRecursiveTask rootTask = new TrivialRecursiveTask(HEIGHT);
		
		
		fjPool.invoke(rootTask);
		//fjPool.execute(rootTask);
		
		long recursiveNrOfNodes = 0;
		// if the join call is commented out, it becomes obvious, that the program does not wait for the tasks to complete
		recursiveNrOfNodes = rootTask.join();
		long numberOfNodesRun = atInt.get();
		long end = System.currentTimeMillis();
		System.out.println("Tree-height: " + HEIGHT + " recursiveNrOfNodes: " + recursiveNrOfNodes + " numberOfNodesRun " + 
				numberOfNodesRun + " time: " + (end-begin));
	}
	
	TrivialRecursiveTask(long height){
		this.height = height;
	}
	
	protected Integer compute(){
		// getting the id also indicates that this node has started running.
		this.id = atInt.incrementAndGet();
		
		int recursiveNrOfSubnodes = 1;
		
		String selfRepresentation = "";
		for(int i = 0; i < height; ++i){
			selfRepresentation += "--";
		}
		selfRepresentation += ("Node "+ id + "  height: " + height + " " );
		selfRepresentation += Thread.currentThread().getName() + " " + Thread.currentThread().getId();
		
		if(height == 0){
			
			//System.out.println(selfRepresentation + "Nr. of subnodes:" + recursiveNrOfSubnodes);
			return recursiveNrOfSubnodes;
		}
		// fork two child tasks
		TrivialRecursiveTask leftChildTask = new TrivialRecursiveTask(height -1);
		TrivialRecursiveTask rightChildTask = new TrivialRecursiveTask(height -1);
		
		
		// Approach 1 invokeAll: stable.
		invokeAll(leftChildTask, rightChildTask);
		recursiveNrOfSubnodes += leftChildTask.getRawResult();
		recursiveNrOfSubnodes += rightChildTask.getRawResult();
		
		// Approach 2 also a stable approach: fork() is asynch, invoke is synch()
		//leftChildTask.fork();
		//recursiveNrOfSubnodes += rightChildTask.invoke();
		//recursiveNrOfSubnodes += leftChildTask.join();
		
		// Approach 3 (less stable): If both subtasks are forked, the pool creates uncontrollable many threads
		//leftChildTask.fork();
		//rightChildTask.fork();
		//recursiveNrOfSubnodes += leftChildTask.join();
		//recursiveNrOfSubnodes += rightChildTask.join();
		
		
		
		//System.out.println(selfRepresentation + "Nr. of subnodes:" + recursiveNrOfSubnodes);
		
		return recursiveNrOfSubnodes;
	}
}
