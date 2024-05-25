// Copyright Marwan Abu-Khalil 2012

package seminar.lifecoding.stream.performance_quicksort_spliter.task_thread_parallel;

import java.util.Random;

/**
 * Main Entry Point fo rthe three traditional versions of
 * Quicksort-Parallelization
 * 
 * 
 * 1  sequential 
 * 2. thread-parallel
 * 3. FJ task parallel
 * 
 */
public class QuicksortTaskThreadPerformanceMain {


	enum Mode{PARALLEL_THREAD, PARALLEL_TASK, SEQUENTIAL}
	enum Size{BIG, MINI}
	
    final static Mode mode =  Mode.PARALLEL_TASK;
    //final static Mode mode =  Mode.PARALLEL_THREAD;
    //final static Mode mode =  Mode.SEQUENTIAL;
   
    final static Size size = Size.BIG; //Size.MINI;

    public static void main(String[] args)
    {

    	long begin = System.currentTimeMillis();


    	if(size == Size.MINI){
    		MiniTest();
    	}else if(size == Size.BIG){ 
    		BigTest();
    	}else {
    		throw new Error ("Unknown Size-Mode" + size);
    	}

        System.out.println("Time elapsed: " +( System.currentTimeMillis() - begin));
    }

    private static void BigTest()
    {
        // Max arraysize: 250000000. Adjust heap: -Xmx1500m 
        // int ARRAYSIZE = 250000000; // approx. 38 Seconds with threads, 38 with Tasks
    	//int ARRAYSIZE = 200000000; // Tasks: 29 Sec.
    	
    	int ARRAYSIZE = 100000000; // DEFAULT approx 4 Seconds Thread or Task vs. 12 Sec. sequntial
    	//int ARRAYSIZE = 10000000; // 
    	
    	//int ARRAYSIZE = 1000000;
        
    	int[] testData = new int[ARRAYSIZE];

        Random rand = new Random();
        for (int i = 0; i < ARRAYSIZE; ++i)
        {
        	// ACHTUNG: Patterns im Array können die Algorithmen zum Absturz bringen
            int nextInt = rand.nextInt();
        	//int nextInt = ARRAYSIZE -i % 1000;
        	testData[i] = nextInt;
        }

        if (mode == Mode.PARALLEL_THREAD)
        {
            QuicksortParallelThreads parallelSortingInstance = new QuicksortParallelThreads(testData);
            parallelSortingInstance.sort();

            //print(testData);
        }else if(mode == Mode.PARALLEL_TASK){
        	QuicksortTasks parallelTasksInstance = new QuicksortTasks(testData);
        	parallelTasksInstance.sort();
        }
        else if(mode == Mode.SEQUENTIAL)
        {
            // Sequential test
            QuicksortSequential instance = new QuicksortSequential(testData);
            instance.sort();
        }



    }

    private static void MiniTest()
    {
       
    	int[] testData = { 1, 5, 89, 7, 9, 2, 1, 3, 45, -56 };
    	
        if (mode == Mode.PARALLEL_THREAD)
        {
            QuicksortParallelThreads parallelSortingInstance = new QuicksortParallelThreads(testData);
            parallelSortingInstance.sort();
        }
        else if( mode == Mode.PARALLEL_TASK)
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
