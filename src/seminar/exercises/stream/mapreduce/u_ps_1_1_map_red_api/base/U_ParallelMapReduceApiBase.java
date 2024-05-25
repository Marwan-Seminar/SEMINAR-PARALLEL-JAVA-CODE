package seminar.exercises.stream.mapreduce.u_ps_1_1_map_red_api.base;

import java.util.Arrays;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.IntStream;
import java.util.stream.Stream;



/*
 * Ausgangsbasis zu Übung PS 1.0 Trivialen Map-Reduce Stream bauen und parallelisieren
 * 
 * 
 * a) Bauen Sie einen Stream der folgendes leistet
 *		Strings in Integers umwandeln („1“ -> 1)
 *	 	Ungerade Zahlen herausfiltern, so dass nur noch gerade übrig bleiben
 * 		Quadrate aller im Stream verbliebenen Zahlen berechnen
 * 		Maximum dieser Quadrate finden
 * 
 * b) Parallelisieren Sie den Stream aus a)
 * 		Zeigen Sie, dass der Stream tatsächlich parallel ausgeführt wird (z.B. durch Shell Ausgaben)
 * 		Finden Sie heraus, mit wie vielen Therads der Stream ausgeführt wird, und wie diese Threads heißen (ohne den Debugger zu benutzen)
 * 
 * c) Benutzen Sie einen spezialisierten Stream-Typ und dessen Methoden, um das Maximum eines int[] (Array vom typ int) zu finden.
 * 
 * d) Übergeben Sie eine Method-Reference an reduce() um das Maximum wie in a) zu berechnen
 * 
 */
public class U_ParallelMapReduceApiBase {


	public static void main(String[] args) {

		System.out.println("U_ParallelMapReduceApiBase:  Start Processing ");

		StreamPipelineMapReduceAPI instance = new StreamPipelineMapReduceAPI();

		instance.runTests();

	}

	

	
}

/*
 * Demonstrates:
 * a) usage of the basic stream map-reduce API: streamApiMapReduceParallel()
 * b) implicit parallelization with Stream.parallel(): streamApiMapReduceParallelLog()
 * d) Map mit Method-Reference Math::max 
 * c) Use a specialized Stream Type IntStream and apply its methods to calculate Max
 */
class StreamPipelineMapReduceAPI {
	
	void runTests() {
		// a)
		new StreamPipelineMapReduceAPI().streamApiMapReduceParallel();
		// b)
		new StreamPipelineMapReduceAPI().streamApiMapReduceParallelLog();
		
		
	}
	
	/*
	 * a) usage of the basic stream map-reduce API: streamApiMapReduceParallel()
	 */
	void streamApiMapReduceParallel(){
		
		System.out.println("a) streamApiMapReduceParallel()");
		
		String[] stringArray = {"7", "2", "3", "12" };
		
		Integer maxInteger = null;
		
		
		// TODO 1:  Build stream. Hint  Arrays.stream()
		
		// TODO 2: apply map / reduce to stream. Hints: Stream.map(), Stream.reduce(), Stream.filter()
		
		// TODO 3: add logic to the map() and reduce() calls. Hint: map(a -> a*a).
		
		// TODO 4: execute Stream in parallel. Hint: Stream.parallel()
		
		// TODO 5: Fetch result from Stream processing. Hint: Optional<Integer>
							
		System.out.println("Stream Result Max Integer:" + maxInteger  +"\n");
	}
	
	/*
	 * b) implicit parallelization with Stream.parallel()
	 */
	void streamApiMapReduceParallelLog(){
		
		System.out.println("b) streamApiMapReduceParallelLog() ");
		
		// TODO: Use the stream impelmentatin from task a) and insert Log output into the 
		//		map() and reduce() calls. Write the name of the threads to the shell. Hint: Thread.currentThread()
		
	}
	

	/* 
	 * c) 
	 *  Specialized Stream Types: IntStream
	 *  Use a specialized Stream Type and apply its methods
	 */
	void streamApiSpecializedStreamType(){
		
		System.out.println("c) streamApiSpecializedStreamType() ");
		
		// TODO: Hints: IntStream, IntStream..max()
	}
	
	/*
	 * d)
	 * Map with Method-Reference Math::max 
	 */
	void streamApiMapMethodReference(){
	
		System.out.println("d) streamApiMapMethodReference()");
		
		// TODO use Method reference. Hint: reduce(Math::max)
		
	}
	
	
	

}