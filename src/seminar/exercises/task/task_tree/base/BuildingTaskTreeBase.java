package seminar.exercises.task.task_tree.base;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.RecursiveTask;

/*
 * Basis für die Aufgabe Node-Count:
 * 
 * Bauen Sie einen binären Baum aus Tasks rekursiv auf.
 *  
 * Zählen Sie die Anzahl der erzeugen Knoten, durch rekursives Summieren der Anzahl der Kinder jedes Knotens
 */
public class BuildingTaskTreeBase {
	
	public static void main(String[] args) {
		
		TreeNodeTask rootTask = new TreeNodeTask();
		
		System.out.println("Starting to build a tree of height: " +TreeNodeTask.TREE_HEIGHT) ;
		
		Integer nodeCount = ForkJoinPool.commonPool().invoke(rootTask);
		
		System.out.println("Node Count of the Task Tree is: " + nodeCount) ;
	}
}


class TreeNodeTask extends RecursiveTask<Integer>{

	
	static int TREE_HEIGHT = 5;
	
	int depth;	

	protected Integer compute() {	
		
		if(depth < TREE_HEIGHT) {
		
			// TODO: Create and fork subasks
			
		} else {
			// TODO: End of recursion
		}
		
		return 1;
	}
}

