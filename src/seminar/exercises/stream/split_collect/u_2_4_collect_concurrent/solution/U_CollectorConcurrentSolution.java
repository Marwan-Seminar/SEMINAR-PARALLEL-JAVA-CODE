package seminar.exercises.stream.split_collect.u_2_4_collect_concurrent.solution;

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



public class U_CollectorConcurrentSolution {

	
	public static void main(String[] args) {
		U_CollectorConcurrentSolution instance = new U_CollectorConcurrentSolution();
		
		instance.useSmartOrderCollector();
		
	}
	
	
	
	
	void useSmartOrderCollector() {
		
		// Short array
		//Customer[] customerArray = {new Customer("Hans", "Waschpulver"), new Customer("Jupp", "Cola"), new Customer("Susi", "Socken")};
		
		
		// Long array
		Customer[] customerArray = new Customer[100];
		for(int i = 0; i < 100; i++) {
			Customer newCustomer =  new Customer("Cust "+ i);
			customerArray[i] = newCustomer;
			for(int j = 0; j < 10; ++j) {
				newCustomer.addOrder(new Order("Order C " + i + " + Order  " , j ));
			}
		}
		
		List<Order> allOrders =  Arrays.stream(customerArray).
		parallel().
		collect(new OrderCollectorSmart());
		
		System.out.println(allOrders);
	}
}



/*
 * OrderCollectorSmart: Macht flatMap überflüssig, indem er direkt auf die
 * Elemente der orders Liste zugreift
 * 
 * Das interessante Verhalten in Bezug auf CONCURRENT und parallel():
 * 
 * Wenn CONCURRENT und ORDERED gesetzt sind, dann wird der supplier() nur ein
 * Mal gerufen und die Combiner-Phase fällt weg. Ebenfalls wird accumulator()
 * aus vielen Threads gerufen, aber dabei stets die selbe Collection Instanz
 * verwendet.
 * 
 * Wird in disem Fall z.B. eine LinkedList verwendet, dann kann es leicht zu
 * Inkonsistenzen kommen. Beim Sysout der List tritt der Fehler zu Tage:
 * 
 *  Exception in thread "main" java.lang.NullPointerException
 *  at java.base/java.util.LinkedList$ListItr.next(LinkedList.java:897)
 *  at java.base/java.util.AbstractCollection.toString(AbstractCollection.java:472)
 *  at java.base/java.lang.String.valueOf(String.java:2951)
 *  ...
 * 
 * Der Fehler ist nicht immer leicht reporduzierbar!!! Um es zu reproduzieren: Sysout im accumulator
 * auskommentieren, und ca 100 Listenobjekte verarbeiten.
 * 
 * FIX: Eine synchronized LinkedList verwenden
 */
class OrderCollectorSmart implements Collector<Customer,  List<Order>, List<Order>>{

	@Override
	public Supplier<List<Order>> supplier() {
		return () -> {
			List<Order> newList;
			
			// Varianten der Liste haben unterschiedliche Eigenschaften in Bezug auf Concurrency
			// 1. ArrayList
			//newList = new ArrayList<Order>();
			
			// 2. LinkedList
			// LinkedList<Order>() can create an inconsistent List behavior in case of CONCURRENT and UNORDERED, NullPointer in ListIter.next!!
			newList = new LinkedList<Order>(); 
			
			// 3. synchronized LinkedList
			// Fix:
			//newList = Collections.synchronizedList(newList);
			
			System.out.println("supplier() called in:  " +Thread.currentThread() + " Container instance: " +  System.identityHashCode(newList) );
			return newList;
		};
	}

	@Override
	public BiConsumer<List<Order>, Customer> accumulator() {
		return (container, customer) -> {
			System.out.println("accumulator called in:  " +Thread.currentThread() + " Container instance: " +  System.identityHashCode(container));
			container.addAll(customer.orders);
		};
	}

	@Override
	public BinaryOperator<List<Order>> combiner() {
		return (list_left, list_right) -> {
			System.out.println("combiner called in:  " +Thread.currentThread());
			list_left.addAll(list_right);
			return list_left;
		};
	}

	@Override
	public Function<List<Order>, List<Order>> finisher() {
		return list -> list;
	}

	@Override
	public Set<Characteristics> characteristics() {
		
		return  Collections.unmodifiableSet(EnumSet.of(
					Collector.Characteristics.IDENTITY_FINISH
					// CONCURRENT und UNORDERED müssen beide geseetzt sein, 
					// sonst wird nicht mit parallelelen accumulator()-Aufrufen auf eine Collection zugegriffen
					, Collector.Characteristics.CONCURRENT
					, Collector.Characteristics.UNORDERED
				)
			);
	}
}
