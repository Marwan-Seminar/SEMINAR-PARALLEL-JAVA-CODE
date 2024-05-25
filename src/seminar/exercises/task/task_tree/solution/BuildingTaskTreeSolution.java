package seminar.exercises.task.task_tree.solution;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.RecursiveTask;

/*
 * Demonstrates how to build Task-Trees
 */
public class BuildingTaskTreeSolution {
	
	public static void main(String[] args) {
		
		TreeNodeTask rootTask = new TreeNodeTask(0);
		
		System.out.println("Starting to build a tree of height: " +TreeNodeTask.TREE_HEIGHT) ;
		
		Integer nodeCount = ForkJoinPool.commonPool().invoke(rootTask);
		
		System.out.println("Node Count of the Task Tree is: " + nodeCount) ;
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
			
			System.out.println("Forking level: " + depth + " Task-ID: "  + System.identityHashCode(this));
			
			leftChild.fork();
			rightChild.fork();
			
			int rightResult = rightChild.join();
			int leftResult = leftChild.join();
			
			System.out.println("Returning level: " + depth + " Task-ID: " + System.identityHashCode(this));
			
			return leftResult + rightResult + 1;
		} else {
			return 1;
		}
	}
}

