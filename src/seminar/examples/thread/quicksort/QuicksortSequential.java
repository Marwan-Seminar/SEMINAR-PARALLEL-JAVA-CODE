// Copyright Marwan Abu-Khalil 2012

package seminar.examples.thread.quicksort;

class QuicksortSequential {
	// Array of integers to be sorted
	private int[] data;

	public QuicksortSequential(int[] data) {
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
