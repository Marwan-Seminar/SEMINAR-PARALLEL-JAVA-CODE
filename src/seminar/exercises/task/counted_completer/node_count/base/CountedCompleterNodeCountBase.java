package seminar.exercises.task.counted_completer.node_count.base;

import java.util.concurrent.CountedCompleter;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.atomic.AtomicInteger;

/*
 * Copyright: Marwan Abu-Khalil, 2019
 * 
 * Basis für die Übung: 
 * Programmieren Sie einen Baum aus Counted-Completer Tasks, der seine Knoten zählt.
 * 
 * Baum aus geähnlichen ForkJoinTasks wird aufgebaut, und die Knoten werden gezählt.
 * 
 * TODO:
 * - Die ForkJoinTask durch CountedCompleter ersezen
 * - alle Join Calls eliminieren
 * 
 */
public class CountedCompleterNodeCountBase {

	
	public static void main(String[] args) throws InterruptedException {
		CountedCompleterNodeCountBase instance = new CountedCompleterNodeCountBase();
		instance.runNodeCounter();
	}
	
	void runNodeCounter() throws InterruptedException{
		
		long startTime = System.currentTimeMillis();
		
		System.out.println("NodeCounterCompleter: started");
		System.out.println("NodeCounterCompleter Tree-Height: " + TreeNodeTask.TREE_HEIGHT);
		
		Integer nodeCount = new TreeNodeTask(0).invoke();
		
		System.out.println("NodeCounterCompleter:  Number of nodes in the Tree: " + nodeCount);
		System.out.println("Time:  " + (System.currentTimeMillis() - startTime));
		
	}
}
/*
 * Builds recursively a binary tree and 
 * calculates the number of nodes in this tree.
 */
class TreeNodeTask extends RecursiveTask<Integer>{

	TreeNodeTask leftChild;
	TreeNodeTask rightChild;
	
	static int TREE_HEIGHT = 5;
	
	int depth;
	
	TreeNodeTask(int depth) {
		this.depth = depth;
	}

	protected Integer compute() {	
		
		if(depth < TREE_HEIGHT) {
		
			leftChild = new TreeNodeTask(depth +1);
			rightChild = new TreeNodeTask(depth +1);
			
			//System.out.println("Forking level: " + depth + " Task-ID: "  + System.identityHashCode(this));
			
			leftChild.fork();
			rightChild.fork();
			
			int rightResult = rightChild.join();
			int leftResult = leftChild.join();
			
			//System.out.println("Returning level: " + depth + " Task-ID: " + System.identityHashCode(this));
			
			return leftResult + rightResult + 1;
		} else {
			return 1;
		}
	}
}

