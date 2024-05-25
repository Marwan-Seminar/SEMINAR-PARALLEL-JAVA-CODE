package seminar.exercises.stream.split_collect.u_2_1_spliterator_implementation.solution;

import java.util.Arrays;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/*
 * Musterlösung zur Übung: 
 * Schreiben Sie einen eigenen Array-Spliterator und benutzen Sie ihn in einer Stream-Verarbeitung. 
 * Zeigen Sie, dass die Spliteration in mehreren Threads gleichzeitig erfolgt.
 * 
 */
public class SpliteratorImplementationSolution {

	final int ARRAYSIZE = 10;
	
	//int[] intArray = new int[ARRAYSIZE];
	int[] intArray = {1,2,3,4,5,6,7,8,9,10};
	
	
	public static void main(String[] args) {
		
		SpliteratorImplementationSolution instance = new SpliteratorImplementationSolution();
		
		instance.applySeminarSpliterator();
		//instance.applySeminarSpliteratorAndMapper();

	}
	
	void applySeminarSpliterator() {
		
		SeminarSpliteator seminarStreamSpliterator = new SeminarSpliteator(intArray, 0, intArray.length);
		
		// true: parallel
		StreamSupport.stream(seminarStreamSpliterator, true).forEach(e -> {});
		
	}
	
	
	// Uses custom mapper and custom spliterator to understand hov Stream.map(Fuction<R,T> mapper) and Spliterator.tryAdvance(Consumer<t> ) 
	// work together. Für mich überraschend: Die Function wird nur auf wenigen Elementen aufgerufen. 
	void applySeminarSpliteratorAndMapper() {
		
		SeminarSpliteator seminarStreamSpliterator = new SeminarSpliteator(intArray, 0, intArray.length);
		
		// true: parallel
		StreamSupport.stream(seminarStreamSpliterator, true).map(new FunctionIntToInt()).forEach(e -> {});
		
	}
	
	
	
}

/*
 * Siehe auch  ArrayListSpliterator implements Spliterator<E> in der Klasse  ArrayList<E> 
 * java.util.ArrayList.ArrayListSpliterator
 * 
 * 
 * Diese Implementierung eines Spliterators zeigt auf, wie das Stream Framework mit dem Spliterator den Range des Arrays zerlegt, 
 * und wie auf den Splits mit Hilfe des Fork-Join Frameworks die traAdvance() Mehtode für jedes Element aufgerufen wird.
 * 
 * Der SeminarSpliteator zerlegt ein Array "naiv" in so viele Teile wie der Rechner CPUs hat, hier im Beispiel 4 Teile.
 * 
 * Der Ablauf wird im Shell -Output und im Debugger sichtbar:
 * 
 * - Zunächst teilt Stream-Framework den Range des Arrays durch wiederhote Aufrufe der Mehtode trySplit() immer weiter auf,
 *   bis die vier gleichgroßen Splits übrig bleiben.
 *  
 * - Dann wird für jeden dieser Splits die Methode tryAdvance() für jedes einzelne Element des Splits aufgerufen.
 * 
 * - Alle Aufrufe von tryAdvance() eines Splits werden im selben Thread, aus dem Fork-Join Pool, bearbeitet.
 * 
 * - Alle Aufrufe von tryAdvance() eines Splits werden im selben Fork-Join-Task ausgeführt, das kann man im Debugger beobachten.
 *  
 * 
*/
class SeminarSpliteator implements Spliterator<Integer>{
	
	// The array to operate on (the stream source)
	int[] intArray;
	
	// Define a sub-array to process, half open, [lower, upper)
	int lower, upper; 
	
	// a current position in the array
	int currentPos; 

	//int threshold = Runtime.getRuntime().availableProcessors();
	
	int threshold = 4;
	
	SeminarSpliteator(int[] data, int lower, int upper){
		
		System.err.println("New SeminarSpliteator created for range: " + lower + " " +upper
				 + " in thread " + Thread.currentThread());
		
		intArray = data;
		
		this.lower = lower;
		this.upper = upper;
		currentPos = lower;
		
	}
	
	@Override
	public boolean tryAdvance(Consumer<? super Integer> action) {
		
		System.out.println("tryAdvance() called for position: " + currentPos
				 + " in thread " + Thread.currentThread());
		
		if(currentPos > upper -1) {
			return false;
		
		} else {
		
			// Accept the action
			action.accept(intArray[currentPos]);
			
			// Increase current pos
			currentPos ++;
			
			return true;
			
		}
			
	}

	@Override
	public Spliterator<Integer> trySplit() {
		
		// splits only into threshold many parts where threshold is the number of CPUs
		
		if (upper-lower <= intArray.length / threshold) { 
			return null;
		}
		else {
			// Split into two equal parts
			
			// Left split range
			int leftLower = lower;
			int leftUpper = lower + ((upper - lower) / 2);
			
			// Right split range
			int rightLower = leftUpper;
			int rightUpper = upper;
			
			// New Spliterator for left half
			SeminarSpliteator leftSpliterator = new SeminarSpliteator(intArray, leftLower,  leftUpper);
			
			System.err.println( "Old SeminarSpliteator range adjusted to: "
					 + rightLower + " " + rightUpper +
					 " from old range " + lower + " " +upper
					 + " in thread " + Thread.currentThread());
					
			
			// Adjust own range
			lower = rightLower;
			upper = rightUpper;
			currentPos = rightLower;
			
			return leftSpliterator;
		}
	}

	@Override
	public long estimateSize() {
		
		return upper-lower;
	}

	@Override
	public int characteristics() {
		
		return IMMUTABLE;
	}

}

// Custom Mapper, for debugging and understanding, how the map(Function<> mapper) and the Spliteraor.tryAdvance(Consumer) work together
class FunctionIntToInt implements Function<Integer, Integer>{
	  public Integer apply(Integer i) {
			
			System.out.println(" FunctionIntToInt.apply() called for " + i 
					 + " in thread " + Thread.currentThread());
		   return i;
	   }
}

