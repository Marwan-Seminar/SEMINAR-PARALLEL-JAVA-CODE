package seminar.exercises.stream.split_collect.u_2_4_collect_concurrent.base;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

import seminar.exercises.stream.split_collect.u_2_4_collect_concurrent.Customer;
import seminar.exercises.stream.split_collect.u_2_4_collect_concurrent.Order;



public class U_CollectorConcurrentBase {

	
	public static void main(String[] args) {
		U_CollectorConcurrentBase instance = new U_CollectorConcurrentBase();
		
		instance.useSmartOrderCollector();
		
	}
	
	
	
	
	void useSmartOrderCollector() {
		
		// Short array
		Customer[] customerArray = {new Customer("Hans", "Waschpulver"), new Customer("Jupp", "Cola"), new Customer("Susi", "Socken")};
		
		
		// Long array
		/*
		for(int i = 0; i < 100; i++) {
			Customer newCustomer =  new Customer("Cust "+ i);
			customerArray[i] = newCustomer;
			for(int j = 0; j < 10; ++j) {
				newCustomer.addOrder(new Order("Order C " + i + " + Order  " , j ));
			}
		}
		*/
		
		// TODO 1: Erzeuge Stream aus customerArray
		
		// TODO 2: wende collect() mit dem selbst geschriebenen Collector auf den Stream an
		
		// TODO 3: führe den Stream parallel aus
		
		// TODO 4: Ergebnis ist eine List<Order>
		
		// TODO 5: Schreibe die Ergebnisliste auf die Shell
		
	}
}


/*
 * OrderCollectorSmart: 
 * - Sammelt die Orders aller Customers in einem Stream<Customer> in einer List
 * - Macht somit flatMap() überflüssig, indem er direkt auf die Elemente der orders Liste zugreift
 */
// TODO 6: Mit welche Typen muss das Interface Collector parametriert werden?
//			T ist der Typ des Streams
//			A der Typ der internen Collection des Collectors
//			R der finale Rückgabetyp des Finishers
class  OrderCollectorSmart<T, A, R> implements Collector<T, A, R>{

	@Override
	public Supplier<A> supplier() {
		return null;
		// TODO 7: Einen Supplier zurückgeben, der eine Collection erzeugt. Diese dient dazu die Stream-Elemente aufzunehmen
	}

	@Override
	public BiConsumer<A, T> accumulator() {
		// TODO 8: Einen Accumulator zurückgeben.
		//			Dieser ist dafür verantwortlich, die Stream-Elemente in die interne Collection einzufügen, 
		//			die vom Supplier erzeugt wurde.
		return null;
	}

	@Override
	public BinaryOperator<A> combiner() {
		// TODO 9: Einen Combiner zurückgeben.
		//			Dieser ist dafür zuständig, zwei Collections zu vereinigen, die vorher vom Supplier erzeugt wurden
		return null;
	}

	@Override
	public Function<A, R> finisher() {
		// TODO 10: Einen Finisher zurückgeben, die die interne Collection, die das Resultat der mutable Reduction ist
		//			ein letztes Mal umzuwandeln. 
		return null;
	}

	@Override
	public Set<Characteristics> characteristics() {
		
		return  Collections.unmodifiableSet(EnumSet.of(
					// IDENTITY_FINISH: Besagt, dass der Finisher nichts tut. 
					Collector.Characteristics.IDENTITY_FINISH
					// TODO 11: Weitere Charakteristics definieren, die das Nebenläufigkeitsverhalten beschreiben
				)
			);
	}
}
