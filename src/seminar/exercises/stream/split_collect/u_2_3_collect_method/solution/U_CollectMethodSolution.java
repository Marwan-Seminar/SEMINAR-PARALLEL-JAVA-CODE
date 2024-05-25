package seminar.exercises.stream.split_collect.u_2_3_collect_method.solution;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import seminar.exercises.stream.split_collect.u_2_3_collect_method.Customer;
import seminar.exercises.stream.split_collect.u_2_3_collect_method.Order;


/*
 * Zeigt, wie mit der Collector-Methode die Klassen Customer und Order behandelt werden können, 
 * ohne dass das Interface Collector implementiert werden muss
 * 
 * Es wird folgender trivialer Use-Case implementiert:
 * 
 * Input ist eine Liste von Customers, jeder dieser Customer hat eine oder mehrere Orders. Ein Stream wird benutzt, um eine Liste zu erzeugen, 
 * die alle Orders aller Customer enthält (eine triviale Konkatenation der Order-Listen der verschiedenen Customer Instanzen).
 * 
 * 
 * Die Methode Stream.collect() wird verwendet und ihr werden Functional-Interface Instanzen übergeben. 
 * Das Interface Collector wird nicht implementiert.
 */
public class U_CollectMethodSolution {

	
	String[] numberStrings = {"1", "3" , "5" , "7", "8", "12", "12"};
	
	public static void main(String[] args) {
		U_CollectMethodSolution instance = new U_CollectMethodSolution();
		
		// Aufruf
		instance.useCollectMethod();
		
	}
	

	// Methode collect() benutzen
	void useCollectMethod() {
				
		Customer[] customerArray = {new Customer("Hans", "Waschpulver", 1), new Customer("Jupp", "Cola", 2), new Customer("Susi", "Socken", 3)};
		
		/*
		Customer[] customerArray = new Customer[100];
		for(int i = 0; i < 100; i++) {
			Customer newCustomer =  new Customer("Cust "+ i);
			customerArray[i] = newCustomer;
			for(int j = 0; j < 10; ++j) {
				newCustomer.addOrder(new Order("Order C " + i + " + Order  " , j ));
			}
		}*/
		
		List<Order> allOrders =  Arrays.stream(customerArray).
		parallel().
		collect(
			ArrayList<Order>::new,  // supplier: Supplier<List<Order>>
			(List<Order> list, Customer customer) -> list.addAll(customer.orders), // accumulator: BiConsumer<List<Order>, ? super Customer> 
			(List<Order> list_left, List<Order> list_rigt) -> list_left.addAll(list_rigt) // combiner,  BiConsumer<List<Order>, List<Order>> 
		);
					
			
		System.out.println(allOrders);
	}
}