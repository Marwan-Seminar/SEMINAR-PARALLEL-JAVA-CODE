package seminar.exercises.stream.split_collect.u_2_2_splitarator_linkedlist.solution;


import java.util.LinkedList;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;


/*
 * Musterlösung zur Übung: Übung PS 2.2 Spliterator LinkedList Performance 
 * Schreiben Sie einen Spliterator der eine Linked-List verarbeitet. 
 * Schreiben Sie eine Stream-Verarbeitung, die diesen Spliterator benutzt. 
 * 
 * Erklären Sie vor diesem Hintergrund, warum Parallel-Streams auf Linked-Lists langsamer arbeiten als auf Arrays
 * 
 */
public class SpliteratorLinkedListSolution {

	final static int LISTSIZE = 100;
	
	
	public static void main(String[] args) {
		
		SpliteratorLinkedListSolution instance = new SpliteratorLinkedListSolution();
		
		instance.applySeminarSpliterator();
		

	}
	
	void applySeminarSpliterator() {
		
		LinkedList<Integer> linkedList = new LinkedList<Integer>();
		
		
		for(int i = 0; i < LISTSIZE; ++i) {
			linkedList.add(i);
		}
		
		SeminarLinkedListSpliteator<Integer> seminarStreamSpliterator = new SeminarLinkedListSpliteator<Integer>(linkedList, 0, linkedList.size());
		
		// true: parallel
		StreamSupport.stream(seminarStreamSpliterator, true).forEach(e -> {});
		
	}
	
}

/*
 * 
 * Diese Implementierung eines Spliterators für LinkedList ist naiv und ineffizient, 
 * da sie per Intdex auf Elemente der LinkedList zugreift.
 * 
 * Sinn dieser Implementierung ist lediglich, die Schwirigkeiten bei der Splitareation einer LinkedList zu demonstrieren,
 * um deutlich zu machen, warum eine ArrayList effizienter paralleliseirbar ist, im Streams-Framework.
 * 
 * Die Implementierung in LinkedList im JDK arbeitet im Gegensatz zu dieser Implementierung auf einem Array.
 * 
 * Der SeminarLinkedListSpliteator zerlegt die Liste in so viele Teile wie der Rechner CPUs hat, hier im Beispiel 4 Teile.
 * 
 * Der Ablauf wird im Shell-Output sichtbar:
 * 
 * - Zunächst teilt Stream-Framework den Range der LinkedList durch wiederhote Aufrufe der Mehtode trySplit() immer weiter auf,
 *   bis der SeminarLinkedListSpliteator null zurückgibt. 
 *   Hierdurch realisiert er einen Threashold, im vorliegenden Bespiel tut er das, wenn vier gleichgroße Splits übrig bleiben.
 *  
 * - Dann wird für jeden dieser Splits die Methode tryAdvance() für jedes einzelne Element des Splits aufgerufen.
 * 
 * - Alle Aufrufe von tryAdvance() eines Splits werden im selben Thread, aus dem Fork-Join Pool, bearbeitet.
 *  
 * 
*/
class SeminarLinkedListSpliteator<T> implements Spliterator<T>{
	
	
	// The array to operate on (the stream source)
	LinkedList<T> linkedList;
	
	// a current position in the array
	int currentPos; 

	//int threshold = Runtime.getRuntime().availableProcessors();
	int threshold = 4;
	
	// Define a sub-array to process, half open, [lower, upper)
	int lower, upper; 
	


	
	SeminarLinkedListSpliteator(LinkedList<T> linkedList, int lower, int upper){
		
		System.out.println("New SeminarLinkedListSpliteator created for range: " + lower + " " +upper + " in thread " + Thread.currentThread());
		
		this.linkedList = linkedList;
		
		this.lower = lower;
		this.upper = upper;
		
		currentPos = lower;
		
	}
	
	
	@Override
	public boolean tryAdvance(Consumer<? super T> action) {
		
		System.out.println("SeminarLinkedListSpliteator tryAdvance() called for position: " + currentPos + " in thread " + Thread.currentThread());

		if(currentPos > upper -1) {
			return false;
		
		} else {
		
			// THIS IS VERY INEFFICIENT, as the LinkedList must be traversed for each get() call. 
			// A "real" implementation would find a better solution (iterator, array....) 
			//Accept the action
			action.accept(linkedList.get(currentPos));
			
			// Increase current pos
			currentPos ++;
			
			return true;
			
		}
			
	}

	@Override
	public Spliterator<T> trySplit() {
		
		
		if (upper-lower <= linkedList.size() / threshold) { 
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
			SeminarLinkedListSpliteator<T> leftSpliterator = new SeminarLinkedListSpliteator<T>(this.linkedList, leftLower,  leftUpper);
			
			System.out.println( "Old SeminarLinkedListSpliteator range adjusted to: "
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


//Custom Mapper, for debugging and understanding, how the map(Function<> mapper) and the Spliteraor.tryAdvance(Consumer) work together
class FunctionIntToInt implements Function<Integer, Integer>{
	  public Integer apply(Integer i) {
			
			System.out.println(" FunctionIntToInt.apply() called for " + i 
					 + " in thread " + Thread.currentThread());
		   return i;
	   }
}


