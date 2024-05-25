// Copyright Marwan Abu-Khalil 2012

package seminar.examples.thread.quicksort;

import java.util.Random;

public class QuicksortTest {


    final static boolean PARALLEL = false;

    public static void main(String[] args)
    {

    	long begin = System.currentTimeMillis();



         //MiniTest();
         BigTest();

        System.out.println("Time elapsed: " +( System.currentTimeMillis() - begin));
    }

    private static void BigTest()
    {
        // Max arraysize: 250000000. Adjust heap: -Xmx1500m 
        int ARRAYSIZE = 250000000; // approx. 38 Seconds
    	//int ARRAYSIZE = 100000000; // approx 12 Seconds vs. 20 Sec. sequntial
    	
        int[] testData = new int[ARRAYSIZE];

        Random rand = new Random();
        for (int i = 0; i < ARRAYSIZE; ++i)
        {
            int nextInt = rand.nextInt();
            testData[i] = nextInt;
        }

        if (PARALLEL)
        {
            QuicksortParallelThreads parallelSortingInstance = new QuicksortParallelThreads(testData);
            parallelSortingInstance.sort();

            //print(testData);
        }
        else
        {
            // Sequential test
            QuicksortSequential instance = new QuicksortSequential(testData);
            instance.sort();
        }



    }

    private static void MiniTest()
    {
       
    	int[] testData = { 1, 5, 89, 7, 9, 2, 1, 3, 45, -56 };
    	
        if (PARALLEL)
        {
            QuicksortParallelThreads parallelSortingInstance = new QuicksortParallelThreads(testData);
            parallelSortingInstance.sort();
        }
        else
        {
        	 
             QuicksortSequential instance = new QuicksortSequential(testData);
             instance.sort();
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
