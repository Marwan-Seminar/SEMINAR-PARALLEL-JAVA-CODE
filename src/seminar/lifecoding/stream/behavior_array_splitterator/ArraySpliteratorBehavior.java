package seminar.lifecoding.stream.behavior_array_splitterator;
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
 * 
 * 
 * Der Modifirte Array-Spliterator schreibt auf die Concole wenn trySpilit und traAdvance Aufgerufen werden.
 * 
 * Folgende Erkenntnisse werden im Output sichtbar:
 * 1. Am Beispiel eines einfach Map-Reduce lässt sich so am Output erkennen, wie Splits, Threads und der Map-Reduce Baum zusammenarbeiten.
 * 2. Wenn dieser Algorithmus ein gößeres Array verarbeitet, wird deutlich, wie der Threshold des Frameworks wirkt,
 *    indem die Anzahl der Leaf-Splits auf einen Zielwert  begrenzt wird.  
 * 
 * Enthält eine Kopie des Original-Codes von java.util.Spliterators.ArraySpliterator sowie eine modifizierte Version 
 */
public class ArraySpliteratorBehavior {
	

	public static void main(String[] args) {
		ArraySpliteratorBehavior instance = new ArraySpliteratorBehavior();
				
		//instance.smallArraySpliteration();
		
		instance.bigArraySpliteration();
		
		
	}
	
	

	// shows runtime behavior in case of small array
	// Uses custom spliterator to understand hov Stream.map(Fuction<R,T> mapper) and
	// Spliterator.tryAdvance(Consumer<t> )
	// work together.
	void smallArraySpliteration() {
		//String[] stringArray = { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10" };
		String[] stringArray = { "0", "1", "2", "3", "4" }; // für einfachen shell output: Belegung = Array-Index
		//String[] stringArray = { "1", "2", "3", "4"};

		ArraySpliteratorModified<String> modifiedArraySpliterator = new ArraySpliteratorModified<String>(stringArray, 0);

		// true: parallel
		StreamSupport.stream(modifiedArraySpliterator, true).map(s -> {
			System.out.println("map called for arg " + s + " in Thread:  " + Thread.currentThread());
			return Integer.parseInt(s);

		}).reduce((a, b) -> {
			System.out.println("reduce called for args " + a + ", " + b + " in Thread:  " + Thread.currentThread());
			return Math.max(a, b);
		});
		
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
		StreamSupport.stream(modifiedArraySpliterator, true).map(s -> {
			System.out.println("map called for arg " + s + " in Thread:  " + Thread.currentThread());
			return Integer.parseInt(s);

		}).reduce((a, b) -> {
			System.out.println("reduce called for args " + a + ", " + b + " in Thread:  " + Thread.currentThread());
			return Math.max(a, b);
		});
		
		printFrameworkTargetSizes(stringArray.length);
		
		System.out.println(
			"\nNumber of Splits: " + ArraySpliteratorModified.splitCount.get() + 
			", Number of Leaf-Splits " + ArraySpliteratorModified.leafCount +
			",  Minimal Split Size: " + ArraySpliteratorModified.minimalSplitSize);
		
		
	}
	
	private static void printFrameworkTargetSizes(int arraysize) {
		
		// Defines the desired NUMBER of Leaf-Tasks
		// Taken from AbstractTask getLeafTarget()
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
 * 
 * Ich habe darin Veränderungen vorgenommen.  
*/
/**
 * A Spliterator designed for use by sources that traverse and split
 * elements maintained in an unmodifiable {@code Object[]} array.
 */
  class ArraySpliteratorModified<T> implements Spliterator<T> {
	  
	 // counted as number of splits created, i.e. all calls to a constructor plus all calls to trySplit that do not return null
	 public static AtomicInteger splitCount = new AtomicInteger(0);
	 // counted as number of ArraySpliteratorModified instances to assign unique IDs
	 public static AtomicInteger instancesCount = new AtomicInteger(0);
	 // counted as number of splits, on which tryAdvace() gets called
	 public static AtomicInteger leafCount = new AtomicInteger(0);
	 public static int  minimalSplitSize = Integer.MAX_VALUE;
	 
	 AtomicBoolean isLeafSplit = new AtomicBoolean(false); 
	 final int splitID;
	 
	 static synchronized void setMinimalSplitSize(int splitsize) {
		 minimalSplitSize = Math.min(minimalSplitSize, splitsize);
	 }
	 
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
        
        // marwan extensions
        ArraySpliteratorModified.splitCount.incrementAndGet();
        ArraySpliteratorModified.setMinimalSplitSize(fence-origin);
        this.splitID = ArraySpliteratorModified.instancesCount.incrementAndGet();
        System.out.println("Spliterator " +this.splitID + " constructed:\t origin " +  origin + " fence " + fence + " in Thread:  " + Thread.currentThread());
        
    }

    @Override
    public Spliterator<T> trySplit() {
        int lo = index, mid = (lo + fence) >>> 1;
     
        // marwan extensions
        // number of splis is incremented here as well as in the constructor, because the adjustment of this.index 
        // creates conceptually a new split allthough it does not create a new Spliterator instance
        if(!(lo >= mid)){
        		ArraySpliteratorModified.splitCount.incrementAndGet();
        		System.out.println("Spliterator " + this.splitID + " adapted:\t\t origin " + mid + " fence " + fence + " in Thread:  " + Thread.currentThread());
        }
        // end marwan extensions
        
        return (lo >= mid)
               ? null
               : new ArraySpliteratorModified<>(array, lo, index = mid, characteristics);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void forEachRemaining(Consumer<? super T> action) {
    	// marwan extensions
    	boolean firstHit = isLeafSplit.compareAndSet(false, true);
    	if(firstHit) {
    		leafCount.incrementAndGet();
    	}
    	// end marwan extensions
    	
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
    	
    	// marwan extensions
    	boolean firstHit = isLeafSplit.compareAndSet(false, true);
    	if(firstHit) {
    		leafCount.incrementAndGet();
    	}
    	// end marwan extensions
    	
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

/////////////// ORIGINAL AUS DEM JDK /////////////////////////////////////
/**
 * A Spliterator designed for use by sources that traverse and split
 * elements maintained in an unmodifiable {@code Object[]} array.
 */
 class ORIGArraySpliterator<T> implements Spliterator<T> {
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
    public ORIGArraySpliterator(Object[] array, int additionalCharacteristics) {
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
    public ORIGArraySpliterator(Object[] array, int origin, int fence, int additionalCharacteristics) {
        this.array = array;
        this.index = origin;
        this.fence = fence;
        this.characteristics = additionalCharacteristics | Spliterator.SIZED | Spliterator.SUBSIZED;
    }

    @Override
    public Spliterator<T> trySplit() {
        int lo = index, mid = (lo + fence) >>> 1;
        return (lo >= mid)
               ? null
               : new ORIGArraySpliterator<>(array, lo, index = mid, characteristics);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void forEachRemaining(Consumer<? super T> action) {
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