// Copyright Marwan Abu-Khalil 2012

package seminar.exercises.task.quicksort_tasks;

import java.util.Random;

import seminar.examples.task.quicksort.QuicksortTasks;
import seminar.exercises.task.quicksort_tasks.base.QuicksortBase;
import seminar.exercises.task.quicksort_tasks.sequential.QuicksortSequential;

/*
 * This class is the central entry point for the three provided Quicksort-Variants
 *  
 *  - SEQUENTIAL calls seminar.exercises.task.quicksort_tasks.sequential.QuicksortSequential
 *  - BASE calls seminar.exercises.task.quicksort_tasks.base.QuicksortBase (identical to sequential, starting point for this exercise)
 *  - PARALLEL_TASK  parallel
 *   both wit big or small array
 */
public class QuicksortTaskTestMain {


	enum Mode{ PARALLEL_TASK, SEQUENTIAL, BASE}
	enum Size{BIG, MINI}
	
	//final static Mode mode =  Mode.BASE;
    //final static Mode mode =  Mode.SEQUENTIAL;
	final static Mode mode =  Mode.PARALLEL_TASK;
   
    final static Size size = Size.BIG; 
    //final static Size size = Size.MINI;
    
    // for time measurment
    static long begin_time;

    public static void main(String[] args)
    {

    	System.out.println("QuicksortTaskTestMain");
    	

    	if(size == Size.MINI){
    		MiniTest();
    	}else if(size == Size.BIG){ 
    		BigTest();
    	}else {
    		throw new Error ("Unknown Size-Mode" + size);
    	}

        System.out.println("Time elapsed: " +( System.currentTimeMillis() - begin_time));
    }

    private static void BigTest()
    {
        // Max arraysize: 250000000. Adjust heap: -Xmx1500m 
        // int ARRAYSIZE = 250000000; 	// approx. 38  with Tasks
    	// int ARRAYSIZE = 200000000; 	// Tasks: 29 Sec.
    	int ARRAYSIZE = 100000000; 	// approx 4 Seconds vs. 12 Sec. sequntial
    	// int ARRAYSIZE = 10000000; 		// 1 Second
        //int ARRAYSIZE = 1000000;
        
    	System.out.println("Arraysize: " + ARRAYSIZE);
    	
    	int[] testData = new int[ARRAYSIZE];
    	
        Random rand = new Random();
        for (int i = 0; i < ARRAYSIZE; ++i)
        {
        	// ACHTUNG: Patterns im Array können die Algorithmen zum Absturz bringen
            int nextInt = rand.nextInt();
        	//int nextInt = ARRAYSIZE -i % 1000;
        	testData[i] = nextInt;
        }
        
        System.out.println("Array initialized ");
        
        // Start time measurment
        begin_time = System.currentTimeMillis();

        //print(testData);
        
        if(mode == Mode.BASE){
        	System.out.println("QuicksortBase call");
        	
        	QuicksortBase parallelTasksInstance = new QuicksortBase(testData);
        	parallelTasksInstance.sort();
        }
        else if(mode == Mode.PARALLEL_TASK){
        	System.out.println("QuicksortParallelTasks (Solutuion) call");
        	QuicksortTasks parallelTasksInstance = new QuicksortTasks(testData);
        	parallelTasksInstance.sort();
        }
        else if(mode == Mode.SEQUENTIAL)
        {
        	System.out.println("QuicksortSequential call");
            // Sequential test
            QuicksortSequential instance = new QuicksortSequential(testData);
            instance.sort();
        }



    }

    private static void MiniTest()
    {
       
    	int[] testData = { 1, 5, 89, 7, 9, 2, 1, 3, 45, -56 };
    	
       
        if( mode == Mode.PARALLEL_TASK)
        {
        	 
        	QuicksortTasks parallelTasksInstance = new QuicksortTasks(testData);
        	parallelTasksInstance.sort();
        }

        print(testData);
    }


    public static void print(int[] data)
    {
        for (int i = 0; i < data.length; ++i)
        {
            System.out.println(data[i]);
        }
    }
}
