package seminar.exercises.task.counted_completer.node_count.solution;

import java.util.concurrent.CountedCompleter;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

/*
 * Copyright: Marwan Abu-Khalil, 2019
 * 
 
 * Demonstrates the CountedCompleter API
 * Builds a trivial tree and counts its nodes
 * 
 * 
 * Contains two Versions
 * 
 * 1.NodeCounterCompleterDualFork: Two subtasks are forked (simplified Version, Marwan)
 * 
 * 2 NodeCounterCompleterJDK_Style: Efficient version, following the JDK example
 * 			Forks one subtask, and calls the other subtasks compute () method in the current thread
 * 
 * 
 * 2  is a simplified implementation of the Example "Recording Subtasks" explained
 * in the JDK documentation of CountedCompleter
 * see:
 * https://docs.oracle.com/javase/8/docs/api/index.html?java/util/concurrent/CountedCompleter.html
 * 
 * 1 Is a version derived from that and changed. The JDK Version 2 is much more efficient! 
 * 
 * 
 */
public class CountedCompleterNodeCountSolution {

	
	public static void main(String[] args) throws InterruptedException {
		CountedCompleterNodeCountSolution instance = new CountedCompleterNodeCountSolution();
		instance.runNodeCounter();
	}
	
	void runNodeCounter() throws InterruptedException{
		
		long startTime = System.currentTimeMillis();
		
		//Integer nodeCount = new NodeCounterCompleterJDK_Style(null, 0).invoke();
		
		Integer nodeCount = new NodeCounterCompleterDualFork(null, 0).invoke();
		
		System.out.println("NodeCounterCompleter: The number of nodes in the Tree  is " + nodeCount);
		System.out.println("Time:  " + (System.currentTimeMillis() - startTime));
		
	}
}


/*
 * 1. Simple but slow version
 */
class NodeCounterCompleterDualFork extends CountedCompleter<Integer>{

	private static final boolean VERBOSE = false;
	// 5 should yield 63
	static int MAX_DEPTH = 25;
	int depth;
	
	Integer result; 
	
	
	NodeCounterCompleterDualFork leftChild;
	NodeCounterCompleterDualFork rightChild;
	
	public NodeCounterCompleterDualFork(NodeCounterCompleterDualFork parentCompeleter, int depth) {
		// super Constructor sets parentCompleter as Completion-Action of this
		// The Completion-Action is the Task to be executed after this Task is completed.
		super(parentCompeleter);
		//System.out.println("Ctor :  depth " + depth );
		this.depth = depth;
		
		if(parentCompeleter == null) {
			System.out.println("NodeCounterCompleterMarwDualFork: Tree-Height: " + MAX_DEPTH);
		}
	}
	
	@Override
	public void compute() {
		
		
		
		// Recursively builds a binary tree of NodeCounterCompleter instances 
		if(depth < MAX_DEPTH) {
			
			
			
		leftChild = new NodeCounterCompleterDualFork(this, depth+1);
		rightChild = new NodeCounterCompleterDualFork(this, depth+1);
		
		addToPendingCount(2);
		
		leftChild.fork();
		rightChild.fork();	
		
		
		}
		else {
			// Leaf-Node, end of recursion
			this.result = 1;	
			
			
		}
		
		// The tryComplete() call does two things:
		// 1. if this.pending-count > 0, it decrements pending count
		// 2. if this.pending-count == 0, it calls onCompletion() of all completions in the chain of completions
		// 	  where the pending-count is 0. As soon as it finds a node with pending-cout > 0, it decrements that pending count
		// 	  and returns.
		tryComplete();
		
		//System.out.println("Compute " + this + " depth " + depth);

		
		
	}
	
	// This method can be called in two situations:
	// 1. This Task calls tryComplete() and this.pending-count is 0.
	// 		I check for null values, and if everything is available, i process the results of the subnodes.
	//		In case of null values, I do nothing and return
	//	    In the JDK Example, in this case nothing needs to be done
	//		In my case, this approach does not work!
	// 2. A child Task calls tryComplete() and its pending-count is 0. Also this.pending-count is 0.
	//   	In this case, caller is the child Task.
	//   	This case indicates, that the child tasks both are complete, as otherwise this.pending-count wound not be 0 
	// 		I check for null values (maybe redundant? ) and process the results of the child tasks
	@Override
	public void onCompletion(CountedCompleter<?> caller) {

		
		// DEBUG OUTPUT ONLY
		if(VERBOSE) { 
			if(caller == this) {
				System.out.println(" CAller == this" );
			}
			
			if(leftChild == null || leftChild.result == null || rightChild == null || rightChild.result == null) {
				System.out.println("NULL CASE");
			}
		}
			
		
		// ALGORITHM STARTS HERE
		if(leftChild == null || leftChild.result == null || rightChild == null || rightChild.result == null) {
					return;
		}
		// ONLY IF NOT NULL
		this.result = leftChild.result + rightChild.result + 1;
		
	
	
	}

	@Override
	public Integer getRawResult() {
		return result;
	}
	
	@Override
	public String toString() {
		return "NodeCounterCompleter" + " depth: " + depth + " result " + result + " pendingCount " + getPendingCount();
	}
	
	
}

/*
 * Fast but subtle logic
 * 
 */
class NodeCounterCompleterJDK_Style extends CountedCompleter<Integer>{

	static int MAX_DEPTH = 25;
	int depth;
	
	//AtomicInteger  result = new AtomicInteger(0);
	int result; 
	
	NodeCounterCompleterJDK_Style sibling;
	
	public NodeCounterCompleterJDK_Style(NodeCounterCompleterJDK_Style parentCompeleter, int depth) {
		// super Constructor sets parentCompleter as Completion-Action of this
		// The Completion-Action is the Task to be executed after this Task is completed.
		super(parentCompeleter);
		//System.out.println("Ctor :  depth " + depth );
		this.depth = depth;
		
		if(parentCompeleter == null) {
			System.out.println("NodeCounterCompleterJDK_Style Tree-Height: " + MAX_DEPTH);
		}
	}
	
	@Override
	public void compute() {
		
		
		
		// Recursively builds a binary tree of NodeCounterCompleter instances 
		if(depth < MAX_DEPTH) {
			
			
			
			NodeCounterCompleterJDK_Style leftChild = new NodeCounterCompleterJDK_Style(this, depth+1);
			NodeCounterCompleterJDK_Style rightChild = new NodeCounterCompleterJDK_Style(this, depth+1);
			
			
			// siblings need to know each other, for use in onCompletion(CountedCompleter<?> caller)
			leftChild.sibling = rightChild;
			rightChild.sibling = leftChild;

			// This is a crucial call: This Task does not maintain references to its subtasks.
			// Instead, it notes in the pending-count how many subtasks it has. 
			
			addToPendingCount(1);
			// Fork the left child
			leftChild.fork();
			
			// Compute the other child in the current thread
			rightChild.compute();
			
		
		}
		else {
			// Leaf-Node, end of recursion
			this.result = 1;
			
			// The tryComplete() call does two things:
			// 1. if this.pending-count > 0, it decrements pending count
			// 2. if this.pending-count == 0, it calls onCompletion() of all completions in the chain of completions
			// 	  where the pending-count is 0. As soon as it finds a node with pending-cout > 0, it decrements that pending count
			// 	  and returns.
			tryComplete();
			
		}
		
		//System.out.println("Compute " + this + " depth " + depth);

		
		
	}
	
	@Override
	public void onCompletion(CountedCompleter<?> caller) {
		//System.out.println("onCompletion this " + this + " caller " + caller);
		
		// This method can be called in two situations:
		// 1. This Task calls tryComplete() and this.pending-count is 0. In this case, nothing needs to be done.
		// 2. A child Task calls tryComplete() and its pending-count is 0. Also this.pending-count is 0.
		//   In this case, caller is the child Task.
		//   This case indicates, that the child tasks both are complete, as otherwise this.pending-count wound not be 0 
		if(caller == this) {
			// do nothing
			return;
		}
		else {
			//child called tryComplete
			NodeCounterCompleterJDK_Style childOne = (NodeCounterCompleterJDK_Style) caller;
			NodeCounterCompleterJDK_Style childTwo = childOne.sibling;
		
			
			this.result = childOne.result + childTwo.result + 1;	
			//System.out.println("Result " +result + " " + this ) ;
		}
	}

	@Override
	public Integer getRawResult() {
		return result;
	}
	
	@Override
	public String toString() {
		return "NodeCounterCompleter" + " depth: " + depth + " result " + result + " pendingCount " + getPendingCount();
	}
	
	
}
