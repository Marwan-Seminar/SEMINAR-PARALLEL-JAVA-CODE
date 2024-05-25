package seminar.exercises.stream.runtimebehavior.u_3_1_threshold_spliterator.solution;
import java.util.Comparator;
import java.util.Optional;
import java.util.Spliterator;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.StreamSupport;


/*
 * Demonstriert das Verhalten von Array-Spliterator
 * Enthält eine leicht modifizierte Kopie des Original-Codes von java.util.Spliterators.ArraySpliterator
 * 
 * Der Modifizierte Array-Spliterator ThresholdArraySpliterator zeigt folgendes Verhalten:
 * - 1. Splitsize: schreibt auf die Console wenn trySplit und tryAdvance aufgerufen werden.
 * 		Im Orignial-Splitearator enthalten in meinem Fall  die Leaf-Splits bei einem Array der Größe 10.000 jeweils 312 Elemente
 * - 2. Threshold:  Der veränderte Spliterator gibt in trySplit() null zurück, wenn das zu sortierende verbleibende Array weniger
 * 		als 1000 Elemente hat.
 *      Dadruch wird dem Framweork ein anderer Threshold diktiert
 * 
 *    
 */
public class U_ThresholdArraySpliteratorSolution {
	
	public static void main(String[] args) {
		U_ThresholdArraySpliteratorSolution instance = new U_ThresholdArraySpliteratorSolution();
		
		// Kleines Array verarbeiten
		//instance.smallArraySpliteration();
		
		// Großes Array verarbeiten
		instance.bigArraySpliteration();
		
		
	}

	void smallArraySpliteration() {

		String[] stringArray = { "0", "1", "2", "3", "4" }; // für einfachen shell output: Belegung = Array-Index

		ThresholdArraySpliterator<String> thresholdArarySpliterator = new ThresholdArraySpliterator<String>(stringArray, 0);
		// true: parallel
		Optional<Integer> result = 
		StreamSupport.stream(thresholdArarySpliterator, true).
			map(s ->  Integer.parseInt(s)).
			reduce((a, b) ->  Math.max(a, b));
		
		System.out.println("Stream Result: " + result);
	}
	
	
	void bigArraySpliteration() {
		
		int ARRAYSIZE = 10000;
		String[] stringArray = new String[ARRAYSIZE];
		
		for(int i = 0; i < ARRAYSIZE; ++i) {
			stringArray[i] = String.valueOf(i);
		}
		
	
		ThresholdArraySpliterator<String> thresholdArarySpliterator = new ThresholdArraySpliterator<String>(stringArray, 0);
		// true: parallel
		Optional<Integer> result = 
		StreamSupport.stream(thresholdArarySpliterator, true).
			map(s ->  Integer.parseInt(s)).
			reduce((a, b) ->  Math.max(a, b));
		
		System.out.println("Stream Result: " + result);
		
	}
}

/////////////// Ist  weitgehend eine Kopie des ArraySpliterator aus dem JDK java.util.Spliterators.ArraySpliterator ///////////////////
/**
 * A Spliterator designed for use by sources that traverse and split
 * elements maintained in an unmodifiable {@code Object[]} array.
 */
 class ThresholdArraySpliterator<T> implements Spliterator<T> {
    /**
     * The array, explicitly typed as Object[]. Unlike in some other
     * classes (see for example CR 6260652), we do not need to
     * screen arguments to ensure they are exactly of type Object[]
     * so long as no methods write into the array or serialize it,
     * which we ensure here by defining this class as final.
     */
    private final Object[] array;
    private int index;        // current index, modified on advance/split
    private final int fence;  // one past last index
    private final int characteristics;

    /**
     * Creates a spliterator covering all of the given array.
     * @param array the array, assumed to be unmodified during use
     * @param additionalCharacteristics Additional spliterator characteristics
     * of this spliterator's source or elements beyond {@code SIZED} and
     * {@code SUBSIZED} which are always reported
     */
    public ThresholdArraySpliterator(Object[] array, int additionalCharacteristics) {
        this(array, 0, array.length, additionalCharacteristics);
    }

    /**
     * Creates a spliterator covering the given array and range
     * @param array the array, assumed to be unmodified during use
     * @param origin the least index (inclusive) to cover
     * @param fence one past the greatest index to cover
     * @param additionalCharacteristics Additional spliterator characteristics
     * of this spliterator's source or elements beyond {@code SIZED} and
     * {@code SUBSIZED} which are always reported
     */
    public ThresholdArraySpliterator(Object[] array, int origin, int fence, int additionalCharacteristics) {
        this.array = array;
        this.index = origin;
        this.fence = fence;
        this.characteristics = additionalCharacteristics | Spliterator.SIZED | Spliterator.SUBSIZED;
    }

    @Override
    public Spliterator<T> trySplit() {
    	System.out.println("Spliterator adapted trySplit(): called on Split of Size:" + (this.fence - this.index));
    	// TODO: Added for Seminar : THRESHOLD = 1000
    	if((this.fence - this.index) < 1000) {
    		return null;
    	}
        int lo = index, mid = (lo + fence) >>> 1;
        return (lo >= mid)
               ? null
               : new ThresholdArraySpliterator<>(array, lo, index = mid, characteristics);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void forEachRemaining(Consumer<? super T> action) {
    	// TODO Added for Seminar: print out the size of the leaf-split
    	System.out.println("Spliterator adapted forEachRemaining(): called on Split of Size:" + (this.fence - this.index));
        Object[] a; int i, hi; // hoist accesses and checks from loop
        if (action == null)
            throw new NullPointerException();
        if ((a = array).length >= (hi = fence) &&
            (i = index) >= 0 && i < (index = hi)) {
            do { action.accept((T)a[i]); } while (++i < hi);
        }
    }

    @Override
    public boolean tryAdvance(Consumer<? super T> action) {
    	// TODO Added for Seminar: print out the size of the leaf-split
    	System.out.println("Spliterator adapted tryAdvance(): called on Split of Size:" + (this.fence - this.index));
        if (action == null)
            throw new NullPointerException();
        if (index >= 0 && index < fence) {
            @SuppressWarnings("unchecked") T e = (T) array[index++];
            action.accept(e);
            return true;
        }
        return false;
    }

    @Override
    public long estimateSize() { return (long)(fence - index); }

    @Override
    public int characteristics() {
        return characteristics;
    }

    @Override
    public Comparator<? super T> getComparator() {
        if (hasCharacteristics(Spliterator.SORTED))
            return null;
        throw new IllegalStateException();
    }
}