package seminar.exercises.stream.mapreduce.u_ps_1_3_flat_map_api.base;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import seminar.exercises.stream.mapreduce.u_ps_1_3_flat_map_api.Customer;
import seminar.exercises.stream.mapreduce.u_ps_1_3_flat_map_api.Order;

/*
 * 1. Zeigen Sie 
 * 
 * wie man eine verschachtelte Datenstruktur
 * 
 * Customer -> List<Order>
 * 
 * mit Hilfe von flatMAp so traversieren kann, dass man die Preise aller Orders aller Custmer in einer
 * List<Customer> aufsummiert.
 * 
 * 2. Prüfen Sie ob die Summation mit flapMap() durch Parallelisierung schneller wird, als die handgeschriebene Schleife 
 * in sumUpPriceHandcodedLoop()
 * 
 * 3. Finden Sie eine Stream-Verarbeitung für diesen Algorithmus, der schneller ist als die handgeschriebne Schleife
 * 
 * 
 * */
public class U_ApiFlatMapBase {

	
	public static void main(String[] args) {
		U_ApiFlatMapBase instance = new U_ApiFlatMapBase();
		instance.testMain();

	}

	
	void testMain(){
		
		System.out.println("U_ApiFlatMapBase");
		
		long arrayStartTime = System.currentTimeMillis();
		
		List<Customer> customerList = createCustomerListSmall();
		
		System.out.println("Array creation time: " + (System.currentTimeMillis() - arrayStartTime));
		
		long algortithmStartTime = System.currentTimeMillis();
		
		sumUpPricesHandcodedLoop(customerList);
		
		System.out.println("Algorithm time: " + (System.currentTimeMillis() - algortithmStartTime));
	}
	
	/*
	 * Dies ist der Algortihmus, jedoch ohne Stream
	 * 
	 * Sums up all prices of all orders of all customres
	 */
	void sumUpPricesHandcodedLoop(List<Customer> customerList) {	
		
		int sumPrices = 0;
		for(Customer customer : customerList) {
			for(Order order : customer.orders) {
				sumPrices += order.price;
			}
		}
		
		System.out.println("sumUpPriceHandcodedLoop(): Sum of all customer's bills: " + sumPrices);
	}
	
	/*
	 * TODO: Eine Straem Version des Algorithmus bauen,
	 * unter Nutzung von flatMap()
	 */
	void sumUpPricesFlatMap(List<Customer> customerList ) {
		 
	}
	
	
	List<Customer> createCustomerListSmall() {
		
		Customer[] customerArray = {
				new Customer("Hans", "Waschpulver", 4),
				new Customer("Jupp", "Cola", 2),
				new Customer("Susi", "Socken", 6)};
		
		ArrayList<Customer> customerList = new ArrayList<Customer>(Arrays.asList(customerArray));
		
		System.out.println(customerList);
		
		return customerList;
	}


}
