package seminar.exercises.stream.runtimebehavior.u_3_2_pipline_parallelism_analysis.base;
import java.util.Comparator;
import java.util.Optional;
import java.util.Spliterator;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.StreamSupport;


/*
 * Lösung zur Übung PS 3.1 Analyse der Threads in der Map-Reduce Pipeline 
 * 
 * Schreiben Sie einen einfachen Map-Reduce Stream und untersuchen Sie folgendes:
 * - Wie viele und welche Threads sind in der Split-Phase aktiv (hier müssen Sie evtl einen eigenen Spliterator verwenden)
 * - Wie viele und welche Threads sind in der Map-Phase aktiv
 * - Wie viele und welche Threads sind in der Reduce-Phase aktiv
 * 
 * 
 * Demonstriert das Verhalten von Array-Spliterator im Zusammenspiel mit Map und Reduce
 * (entspricht weitgehend folgender Klasse im Lifecoding package:
 * 	src\seminar\lifecoding\stream\behavior_array_splitterator\ArraySpliteratorBehavior.java
 * )
 * 
 * Der Modifizierte Array-Spliterator schreibt auf die Concole wenn trySpilit und traAdvance Aufgerufen werden, 
 * und ebenso schreiben map und reduce die Threads heraus, aus denen sie gerufen werden.
 * 
 * Folgende Erkenntnisse werden im Output sichtbar:
 * 1. Am Beispiel eines einfach Map-Reduce lässt sich so am Output erkennen, wie Splits, Threads und der Map-Reduce Baum zusammenarbeiten.
 * 2. Wenn dieser Algorithmus ein gößeres Array verarbeitet, wird deutlich, wie der Threshold des Frameworks wirkt,
 *    indem die Anzahl der Leaf-Splits auf einen Zielwert  begrenzt wird.  
 * 
 * Enthält eine Kopie des Original-Codes von java.util.Spliterators.ArraySpliterator sowie eine modifizierte Version 
 */
public class PipelineParallelismAnalysisBase {
	

	public static void main(String[] args) {
		PipelineParallelismAnalysisBase instance = new PipelineParallelismAnalysisBase();
		
		// Kleines Array
		//instance.smallArraySpliteration();
		
		// Großes Array
		instance.bigArraySpliteration();
		
		
	}
	
	
	void smallArraySpliteration() {
		//String[] stringArray = { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10" };
		String[] stringArray = { "0", "1", "2", "3", "4" }; // für einfachen shell output: Belegung = Array-Index
	

		ArraySpliteratorModified<String> modifiedArraySpliterator = new ArraySpliteratorModified<String>(stringArray, 0);

		// TODO print out the threads that are active in map and reduce
		// true: parallel
		StreamSupport.stream(modifiedArraySpliterator, true)
			.map(s -> Integer.parseInt(s))
			.reduce((a, b) -> Math.max(a, b));
		
		//printFrameworkTargetSizes(stringArray.length);
		
		System.out.println(
				"\nNumber of Splits: " + ArraySpliteratorModified.splitCount.get() + " " +
				" Number of Spliterator Instances: " + ArraySpliteratorModified.instancesCount.get() + " " +
				" Minimal Split Size: " + ArraySpliteratorModified.minimalSplitSize);
	}
	
	
	void bigArraySpliteration() {
		
		int ARRAYSIZE = 10000;
		String[] stringArray = new String[ARRAYSIZE];
		
		for(int i = 0; i < ARRAYSIZE; ++i) {
			stringArray[i] = String.valueOf(i);
		}
		
		ArraySpliteratorModified<String> modifiedArraySpliterator = new ArraySpliteratorModified<String>(stringArray, 0);

		// true: parallel
		StreamSupport.stream(modifiedArraySpliterator, true)
		.map(s -> Integer.parseInt(s))
		.reduce((a, b) -> Math.max(a, b));
		
		printFrameworkTargetSizes(stringArray.length);
		
		System.out.println(
			"\nNumber of Splits: " + ArraySpliteratorModified.splitCount.get() + 
			", Number of Leaf-Splits " + ArraySpliteratorModified.leafCount +
			",  Minimal Split Size: " + ArraySpliteratorModified.minimalSplitSize);
		
		
	}
	
	private static void printFrameworkTargetSizes(int arraysize) {
		
		// Defines the desired NUMBER of Leaf-Tasks
		// Taken from AbstractTas.getLeafTarget()
		int LEAF_TARGET = ForkJoinPool.getCommonPoolParallelism() << 2;
		
		// Defines the desired SIZE of each leaf-Split
		// Taken from AbstractTask suggestTargetSize(long sizeEstimate), where sizeEstimat is the size of the original array
		//long est = sizeEstimate / LEAF_TARGET;
		long suggestedTargetSize = arraysize / LEAF_TARGET;
		
		
		int parallelism = ForkJoinPool.getCommonPoolParallelism();
		
		int nrOfProcessors  = Runtime.getRuntime().availableProcessors();
		
		System.out.println("\n Desired NUMBER of Leaf-Tasks: " + LEAF_TARGET);
		System.out.println(" Desired SIZE of each Leaf-Split: " + suggestedTargetSize);
		System.out.println(" Number of Threads in Pool: " + parallelism);
		System.out.println(" Number of nrOfProcessors: " + nrOfProcessors);
		
	}
}





/* Dies ist eine modifizierte Kopie der Klasse 
 * java.util.Spliterators.ArraySpliterator
 **/
class ArraySpliteratorModified<T> implements Spliterator<T> {
	  
	// TODO: Finden sie geeignete Wege folgende Zähler im Verlauf des Streams benutzen, 
	// 1. Die anzahl der Splits
	public static AtomicInteger splitCount = new AtomicInteger(0);
	
	// 2. Die Anzahl der Spliterator-Instanzen
	public static AtomicInteger instancesCount = new AtomicInteger(0);
	
	// 3. die Anzahl der Leaf-Splits 
	public static AtomicInteger leafCount = new AtomicInteger(0);
	
	// 4. Die kleinste Split-Größe, die im Verlauf des Programmes entsteht
	public static int  minimalSplitSize = Integer.MAX_VALUE;
	 
	// Hilfsvaraiblen können die folgenden beiden sein
	// Handest es sich um einen Leaf-Split?
	AtomicBoolean isLeafSplit = new AtomicBoolean(false); 
	// Eine Eindeutige ID, damit Sie den Split / Spliterator im Output identifizieren können 
	int splitID;
	 
	
	 
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
    public ArraySpliteratorModified(Object[] array, int additionalCharacteristics) {
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
    public ArraySpliteratorModified(Object[] array, int origin, int fence, int additionalCharacteristics) {
        this.array = array;
        this.index = origin;
        this.fence = fence;
        this.characteristics = additionalCharacteristics | Spliterator.SIZED | Spliterator.SUBSIZED;
        
        // TODOs hier:
        // Split-Count hochzählen
        // minimale Split-Size ggf. dekrementieren
        // splitID zuweisen
    }

    @Override
    public Spliterator<T> trySplit() {
        int lo = index, mid = (lo + fence) >>> 1;
     
        if(!(lo >= mid)){
        	// TODO hier
        	// Split-Count hochzählen
        }
        
        return (lo >= mid)
               ? null
               : new ArraySpliteratorModified<>(array, lo, index = mid, characteristics);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void forEachRemaining(Consumer<? super T> action) {
    	// TODO hier: Prüfen, ob es sich um einen Leaf-Split handelt, ggf. Leaf-Splits hochzählen
    	
    	
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
    	
    	// TODO hier: Prüfen, ob es sich um einen Leaf-Split handelt, ggf. Leaf-Splits hochzählen
    	
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
