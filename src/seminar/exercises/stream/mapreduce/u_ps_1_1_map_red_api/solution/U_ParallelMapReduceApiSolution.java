package seminar.exercises.stream.mapreduce.u_ps_1_1_map_red_api.solution;

import java.util.Arrays;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.IntStream;
import java.util.stream.Stream;



/*
 * Musterlösung zu Übung PS 1.0 Trivialen Map-Reduce Stream bauen und parallelisieren
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
 */
public class U_ParallelMapReduceApiSolution {


	public static void main(String[] args) {

		System.out.println("U_ParallelMapReduceApiSolution:  Start Processing ");

		StreamPipelineMapReduceAPI instance = new StreamPipelineMapReduceAPI();

		instance.runTests();

	}

	

	
}

/*
 * Demonstrates:
 * a) usage of the basic stream map-reduce parallel API: streamApiMapReduceParallel()
 * b) logging the threads that execute a parallel Stream: streamApiMapReduceParallelLog()
 * c) usage of specialized stream types: useSpecializedStreamType() 
 * d) usage of specialized Stream-Types
 */
class StreamPipelineMapReduceAPI {
	
	void runTests() {
		// a)
		new StreamPipelineMapReduceAPI().streamApiMapReduceParallel();
		// b)
		new StreamPipelineMapReduceAPI().streamApiMapReduceParallelLog();
		// c)
		new StreamPipelineMapReduceAPI().streamApiSpecializedStreamType();
		// d)
		new StreamPipelineMapReduceAPI().streamApiMapMethodReference();
		
	}
	
	void streamApiMapReduceParallel(){
		
		System.out.println("a) streamApiMapReduceParallel()");
		
		String[] stringArray = {"7", "2", "3", "12" };
		Stream<String> stringStream =  Arrays.stream(stringArray);
		Optional<Integer> maxInteger =
				stringStream.
				parallel().
				map(a -> Integer.parseInt(a)).
				filter(a -> a%2 == 0).
				map(a -> a*a).
				reduce((a,b) -> Math.max(a,b));
							
		System.out.println("Stream Result Max Integer:" + maxInteger  +"\n");
	}
	
	void streamApiMapReduceParallelLog(){
		
		System.out.println("b) streamApiMapReduceParallelLog() ");
			
		String[] stringArray = {"7", "2", "3", "12" };
		Stream<String> stringStream =  Arrays.stream(stringArray);
		Optional<Integer> maxInteger =
			stringStream.
				parallel().
				map(a -> {
					System.out.println(" map a " + a + " " + Thread.currentThread());
					return Integer.parseInt(a);
				}).
				filter(a -> {
					System.out.println(" filter a " + a + " " + Thread.currentThread());
					return a%2 == 0;
				}).
				map(a -> a*a).
				reduce((a,b) -> {
					System.out.println(" reduce a " + a + " b " +b + " " + Thread.currentThread()); 
					return Math.max(a,b);
				});
							
		System.out.println("Stream Result Max Integer:" + maxInteger  +"\n");
	}
	

	/*
	 * Specialized Stream Types: IntStream
	 */
	void streamApiSpecializedStreamType(){
		
		System.out.println("c) streamApiSpecializedStreamType() ");
		
		int[] intArray = {1,2,3};
		IntStream intStream = Arrays.stream(intArray);
	
		OptionalInt optionalIntMax =  intStream.max();
		
		System.out.println("Stream Result Max Integer:" + optionalIntMax  +"\n");
	}
	
	/*
	 * Map mit Method-REference Math::max 
	 */
	void streamApiMapMethodReference(){
	
		System.out.println("d) streamApiMapMethodReference()");
		
		String[] stringArray = {"7", "2", "3", "12" };
		Stream<String> stringStream =  Arrays.stream(stringArray);
		Optional<Integer> maxInteger =
			stringStream.
				parallel().
				map(Integer::parseInt).
				filter(x -> x < 7).
				reduce(Math::max);	
		
		System.out.println("Stream Result Max Integer:" + maxInteger +"\n");
	}
	
	
	

}