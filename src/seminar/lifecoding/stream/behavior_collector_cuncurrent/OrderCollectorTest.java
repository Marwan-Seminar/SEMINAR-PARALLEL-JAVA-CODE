package seminar.lifecoding.stream.behavior_collector_cuncurrent;

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
import java.util.stream.Collectors;


/*
 * 
 * LC 1.2 Collector CONCURRENT und parallel():
 * 
 *  Zeigt folgendes Verhalten des Collectors
 *  
 *   a) Crash bei LinkedList im Collector
 *   b) Nur eine Collection
 *   c) Keine Combine-Phase
 */
public class OrderCollectorTest {

	// Must be set to false to disable output to achive the desired crash!
	static final boolean VERBOSE = false;
	
	String[] numberStrings = {"1", "3" , "5" , "7", "8", "12", "12"};
	
	public static void main(String[] args) {
		OrderCollectorTest instance = new OrderCollectorTest();
		
		
		//instance.useStandardCollectort();
		instance.useSmartOrderCollector();
		
	}
	
	
	void useStandardCollectort() {
		
		Customer[] customerArray = {new Customer("Hans", "Waschpulver"), new Customer("Jupp", "Cola"), new Customer("Susi", "Socken")};
		
		/*
		 Customer[] customerArray = new Customer[100];
		for(int i = 0; i < 100; i++) {
			customerArray[i] = new Customer("Cust "+ i, "ORder " + i,  i );
		}
		*/
		List<Order> allOrders =  Arrays.stream(customerArray).
		parallel().
		flatMap(customer -> customer.orders.stream()).
		collect( Collectors.toList());
		
		System.out.println(allOrders);
	}
	
	void useSmartOrderCollector() {
		
		//Customer[] customerArray = {new Customer("Hans", "Waschpulver"), new Customer("Jupp", "Cola"), new Customer("Susi", "Socken")};
		
		
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

////////////////////Smart Order Collector: Macht flatMap überflüssig, indem er direkt auf die Elemente der orders Liste zugreift  ////////////////////
/*
* Das interessante Verhalten in Bezug auf CONCURRENT und parallel():
* 
* Wenn CONCURRENT und ORDERED gesetzt sind, dann wird der supplier() nur ein mal gerufen und die Combiner-Phase fällt weg.
* Ebenfalls wird accumulator() aus vielen Threads gerufen, aber dabei stets die selbe Collection Instanz verwendet.
* 
* Wird in disem Fall z.B. eine LinkedList verwendet, dann kann es leicht zu inkonsistenzen kommen. Beim Sysout der List
* tritt der Fehler zu Tage: 
* Exception in thread "main" java.lang.NullPointerException
*   at java.base/java.util.LinkedList$ListItr.next(LinkedList.java:897)
*   at java.base/java.util.AbstractCollection.toString(AbstractCollection.java:472)
*   at java.base/java.lang.String.valueOf(String.java:2951)
*   ...
* Dies ist nicht immer leicht reporduzierbar!!!
* Um es zu reproduzieren: Sysout im accumulator auskommentieren, und ca 100 Listenobjekte verarbeiten.
*/
class OrderCollectorSmart implements Collector<Customer,  List<Order>, List<Order>>{

	@Override
	public Supplier<List<Order>> supplier() {
		return () -> {
			List<Order> newList;
			
			//newList = new ArrayList<Order>();
			
			// LinkedList<Order>() can create an inconsistent List behavior in case of CONCURRENT and UNORDERED, NullPointer in ListIter.next!!
			newList = new LinkedList<Order>(); 
			// TODO: Make List threadsafe
			// Fix:
			//newList = Collections.synchronizedList(newList);
			
			System.out.println("supplier() called in:  " +Thread.currentThread() + " Container instance: " +  System.identityHashCode(newList) );
			return newList;
		};
	}

	@Override
	public BiConsumer<List<Order>, Customer> accumulator() {
		return (container, customer) -> {
			if(OrderCollectorTest.VERBOSE) {
				System.out.println("accumulator called in:  " +Thread.currentThread() + " Container instance: " +  System.identityHashCode(container));
			}
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