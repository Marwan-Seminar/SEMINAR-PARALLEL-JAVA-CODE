// Copyright Marwan Abu-Khalil 2012

package seminar.exercises.thread.parallel.quicksort_threads.base;

import java.util.Random;

/*
 * Diese Datei ist die Ausgangsbasis fuer die Uebung  Parallelisierung des Quicksort Algorithmus.
 * 
 * Diese Datei enthaelt die Klassen:
 * 1. U3_QuicksortParallel_Base: Ausfuehrungsrahmen, Aufbau eines Arrays, Zeitmessung,
 * 2. QuicksortParallelBase: Eine sequentielle (!) Quicksort Implementierung, die als Ausgangsbasis fuer die Parallelisierung dienen kann.
 * 
 */
public class Quicksort_Base_Main {

	public static void main(String[] args) {
	  	  
	  System.out.println("Ueb_2_2_QuicksortParallel_Base");
	  
		// max arraysize: 250.000.000, adjust heap: -Xmx1500m: approx 30 Sec. sequential
		int ARRAYSIZE = 250000000; // 10000000; // 
		int[] testData = new int[ARRAYSIZE];

		System.out.println("Arraysize: " + ARRAYSIZE);
		 
		Random rand = new Random();
		for (int i = 0; i < ARRAYSIZE; ++i) {
			int nextInt = rand.nextInt();
			testData[i] = nextInt;
		}
	  
		System.out.println("start sorting");
	  
		long beginTime = System.currentTimeMillis();
		
		QuicksortParallelBase parallelSortingInstance = new QuicksortParallelBase(
				testData);
		parallelSortingInstance.sort();

		System.out.println("Time used for sorting: " + (System.currentTimeMillis() - beginTime) / 1000);
		
		//print(testData);

	}

	public static void print(int[] data) {
		for (int i = 0; i < data.length; ++i) {
			System.out.println(data[i]);
		}
	}
}

/*
 * Eine SEQUENTIELLE Implementierung von Quicksort, die als Ausgangsbasis für die Parallelisierung
 * verwendet werden soll.
 */
class QuicksortParallelBase {
  // Array of integers to be sorted
  private int[] data;

  public QuicksortParallelBase(int[] data) {
    this.data = data;
  }

  public void sort() {
    sortRecursively(0, data.length - 1);
  }

  void sortRecursively(int l, int u) {

    if (l >= u) {
      return;
    }

    // Choose a pivot element, arbitrarily.
    int pivotIdx = l;

    // running indices:
    int i = l;
    int j = u + 1;

    // Sorts the subrange [l,u] of the data array into two sub-arrays such
    // that
    // all members of the left sub-array are smaller than all members of the
    // right sub-array.
    // After this loop j is the index of the dividing element.
    while (true) {
      // i points to the left end of the range under consideration.
      // Shift i to the right as long as elements are <= pivot.
      // After this loop, i points to an element which is bigger than
      // pivot, of i == j.
      do {
        i++;
      } while (i <= u && data[i] <= data[pivotIdx]);

      // Do the analogous action for the right end pointer j
      do {
        j--;
      } while (data[j] > data[pivotIdx]);

      if (i > j)
        break;

      // Swap
      Swap(i, j);
    }

    Swap(pivotIdx, j);

    // Recursive Calls
    sortRecursively(l, j - 1);
    sortRecursively(j + 1, u);
  }

  private void Swap(int i, int j) {

    int tmp = data[i];
    data[i] = data[j];
    data[j] = tmp;
  }

}
