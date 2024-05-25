package seminar.exercises.stream.split_collect.u_2_1_spliterator_implementation.base;


import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/*
 * Basis zur Übung: PS 2.1 Spliterator Implementierung und Verhalten: Eigenen Spliterator schreiben
 * 
 * Schreiben Sie einen eigenen Array-Spliterator und benutzen Sie ihn in einer Stream-Verarbeitung. 
 * Zeigen Sie, dass die Spliteration in mehreren Threads gleichzeitig erfolgt.
 * 
 * Hiweis: Schauen Sie dich den ArraySpliterator im JDK an:
 * java.util.ArrayList.ArrayListSpliterator
 */
public class SpliteratorImplementationBase {
	
	int[] intArray = {1,2,3,4,5,6,7,8,9,10};
	
	
	public static void main(String[] args) {
		
		SpliteratorImplementationBase instance = new SpliteratorImplementationBase();
		
		instance.applySeminarSpliterator();

	}
	
	void applySeminarSpliterator() {
		
		SeminarSpliteator seminarStreamSpliterator = new SeminarSpliteator(intArray, 0, intArray.length);
		
		// true: parallel
		StreamSupport.stream(seminarStreamSpliterator, true).forEach(e -> {});
		
	}
	
}

/*
 * Siehe auch  ArrayListSpliterator implements Spliterator<E> in der Klasse  ArrayList<E>
 * java.util.ArrayList.ArrayListSpliterator
 * 
 * Die hier gezeigte eigene Implementierung eines Spliterators zeigt auf, wie das Stream Framework mit dem Spliterator
 * den Range des Arrays zerlegt, und wie auf den Splits mit Hilfe des Fork-Join Frameworks die traAdvance() Mehtode für jedes
 * Element aufgerufen wird.
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
	
	// the current position in the array
	int currentPos; 

	
	SeminarSpliteator(int[] data, int lower, int upper){
		
		System.out.println("New SeminarSpliteator created for range: " + lower + " " +upper
				 + " in thread " + Thread.currentThread());
		
		intArray = data;
		
		this.lower = lower;
		this.upper = upper;
		currentPos = lower;
		
	}
	
	@Override
	public boolean tryAdvance(Consumer<? super Integer> action) {
		
		
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
	public Spliterator<Integer> trySplit() {
		
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