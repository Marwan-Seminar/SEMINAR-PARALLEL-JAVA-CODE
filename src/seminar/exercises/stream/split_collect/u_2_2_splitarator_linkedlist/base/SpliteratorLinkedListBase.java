package seminar.exercises.stream.split_collect.u_2_2_splitarator_linkedlist.base;


import java.util.LinkedList;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;



/*
 * Basis zur Übung: Übung PS 2.2 Spliterator LinkedList Performance 
 * 
 * Schreiben Sie einen Spliterator der eine Linked-List verarbeitet. 
 * Schreiben Sie eine Stream-Verarbeitung, die diesen Spliterator benutzt. 
 * 
 * Erklären Sie vor diesem Hintergrund, warum Parallel-Streams auf Linked-Lists langsamer arbeiten als auf Arrays
 * 
 */
public class SpliteratorLinkedListBase {
	
	final static int LISTSIZE = 100;
	
	public static void main(String[] args) {
		
		SpliteratorLinkedListBase instance = new SpliteratorLinkedListBase();
		
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
 * Der Ablauf 
 * 
 * - Zunächst teilt Stream-Framework den Range der Liste durch wiederholte Aufrufe der Mehtode trySplit() immer weiter auf,
 *   bis die vier gleichgroßen Splits übrig bleiben.
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
		
		
		/* 
		 * TODO 
		 * 
		 * Diese Methode muss folgendes leisten:
		 * 
		 * 1. Entweder den Consumer action auf das aktuelle Element anweden und true zurückgeben
		 * 		Hinweis: action.accept(intArray[currentPos]);
		 * 
		 * 2. Oder false zurückgeben und nichts tun
		 */
		return false;	
	}

	@Override
	public Spliterator<T> trySplit() {
		
		/*
		 * TODO:
		 * 
		 * Diese Methode muss das Array zerteilen. 
		 * 
		 * Dazu geht sie folgendermaßen vor:
		 * 
		 * 1. Wenn das Array zu klein zum weiteren zerteilen ist, tut sie nichts, und gibt false zurück
		 * 
		 * 2. Wenn das Array groß genug ist: Die Mitte des Arrays finden. 
		 * 
		 * 3. Einen neuen Sliterator erzeugen, der für die linke Hälfte des Arrays verantwortlich ist
		 *    und diesen am Ende der Methode zurückgeben. 
		 * 
		 * 4. Den eigenen Range so anpassen, dass dieser Splitarator, also this, 
		 *    von nun an für die rechte Hälfte des Arrays zuständig ist	  
		 */
		
		return null;
		
	}

	@Override
	public long estimateSize() {
		
		/*
		 * TODO:
		 * 
		 * Die Größe desjenigen Teilbereich des Arrays zurückgeben,
		 * für den dieser Splitarator zuständig ist.
		 */
		
		return 0;
	}

	@Override
	public int characteristics() {
		
		return IMMUTABLE;
	}

}