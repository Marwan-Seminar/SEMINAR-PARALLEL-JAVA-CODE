package seminar.exercises.stream.mapreduce.u_ps_1_3_flat_map_api.solution;

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
 * Dieses Beispiel zegt, wie man eine verschachtelte Datenstruktur
 * 
 * Customer -> List<Order>
 * 
 * mit Hilfe verschiedener Stream APIs traversieren kann. 
 * Man erkennt daran, dass flatMap() am besten dafür geegnet ist.
 * 
 * 1. sumUpPriceHandcodedLoop(): ohne Stream
 * 2. sumUpPricesMapReduce(): klassischer Map-Reduce Stream, mit innerer Schleife
 * 3. sumUpPricesInnerStream(): Stream mit innerem Stream
 * 4. sumUpPricesFlatMap(): mit flatMap() DIES IST DIE ELEGANTESTE LÖSUNG 
 * 
 * 
 * 
 * Zeitmessungen 
 * bei 
 * CUSTOMER_COUNT =  2000;
 * ORDER_COUNT = 600000000;
 * 
 * sumUpPriceHandcodedLoop(): Algorithm time: 1378
 * sumUpPricesMapReduce(): Algorithm time: 585
 * sumUpPricesInnerStream(): Algorithm time: 1848
 * sumUpPricesFlatMap(): Algorithm time: 1989
 */
public class U_ApiFlatMapSolution {

	static final int CUSTOMER_COUNT =  2000;
	static final int ORDER_COUNT = 600000000;
	static final int ORDERS_PER_CUSTOMER = ORDER_COUNT / CUSTOMER_COUNT;
	
	static final boolean VERBOSE  = false;
	
	public static void main(String[] args) {
		U_ApiFlatMapSolution instance = new U_ApiFlatMapSolution();
		instance.testMain();

	}

	static final Order[] products = {
		new Order("Socken", 12),
		new Order("Müsli", 2),
		new Order("Yoghurt", 1),
		new Order("Bananen", 4),
		new Order("Pullover", 110),
		new Order("Brot", 5),
		new Order("Handschuhe", 45),
		new Order("Kekse", 6),
		new Order("Pizza", 12),
		new Order("Staubsauger", 250),
		new Order("Theaterkarten", 56),
		new Order("Büromaterial", 25)
	};
	
	
	void testMain(){
		
		System.out.println("U_ApiFlatMapSolution");
		
		long arrayStartTime = System.currentTimeMillis();
		
		List<Customer> customerList = createCustomerListBig();
		//List<Customer> customerList = createCustomerListSmall();
		
		System.out.println("Array creation time: " + (System.currentTimeMillis() - arrayStartTime));
		
		long algortithmStartTime = System.currentTimeMillis();
		
		// Basic handcoded loop
		//sumUpPricesHandcodedLoop(customerList);
		
		// Basic Stream approach Map-Reduce: Fast
		sumUpPricesMapReduce(customerList);

		// Komplex Stream Approach: Motivates flatMap()
		//sumUpPricesInnerStream(customerList);
		
		// Elegant Stream Approach with flatMap(), but slower than handcoded loop!!!
		//sumUpPricesFlatMap(customerList);
		
		System.out.println("Algorithm time: " + (System.currentTimeMillis() - algortithmStartTime));
	}
	
	/*
	 * Stream handling of Cusomter / Orders 
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
	 * Stream handling of Cusomter / Orders 
	 * Sums up all prices of all orders of all customers
	 * with classic map-reduce
	 * without flatMAp()
	 * 
	 * This is the fastest approach
	 */
	void sumUpPricesMapReduce(List<Customer> customerList ) {
		 
		Stream<Customer> customerStream = customerList.stream();
		
		Optional<Integer> sumPrices = 
		customerStream.
		parallel().
		map(customer -> customer.orders).
		map(orders -> {
			int customerBill = 0;
			for(Order order : orders) {
				customerBill += order.price; 
			}
			return customerBill;
		}).
		reduce((bill_1, bill_2) -> bill_1 + bill_2);
	
		System.out.println("sumUpPricesMapReduce(): Sum of all customer's bills: " + sumPrices.get());
	}
	

	/*
	 * Komplizierte Lösung als Motivation für flatMap
	 */
	void sumUpPricesInnerStream(List<Customer> customerList ) {
		 
		Stream<Customer> customerStream = customerList.stream();
		
		// Einer der parallel() Aufrufe genügt. Beide zu benutzen bringt keinen messbaren weiteren Vorteil
		
		Optional<Integer> sumPrices = 
		customerStream.
		parallel().
		map(customer -> customer.orders).
		map(orders -> orders.stream()).
		map(ordersStream ->
			ordersStream.
			//parallel().
			map(order -> order.price).
			reduce((a,b) -> a+b)).
		map(optionalInteger -> optionalInteger.get()).
		reduce((a,b) -> (a+b));
				

	
		System.out.println("sumUpPricesInnerStream(): Sum of all customer's bills: " + sumPrices.get());
	}
	
	/*
	 * flatMAp() - Approach
	 * Stream handling of Cusomter / Orders 
	 * Sums up all prices of all orders of all customres
	 */
	void sumUpPricesFlatMap(List<Customer> customerList ) {
		 
		Stream<Customer> customerStream = customerList.stream();
		
		Optional<Integer> sumPrices = 
		customerStream.
		parallel().
		flatMap(customer -> customer.orders.stream()).
		map(order -> order.price).
		reduce((a,b) -> a+b);
		
		System.out.println("sumUpPricesFlatMap(): Sum of all customer's bills: " + sumPrices.get());
	}
	
	/*
	 * Inhaltlich das selbe wie die Methode sumUpPricesMapReduce
	 * Aber hier werden die Typen jdeder Pipeline-Stufe explizit gezeigt
	 */
	void showMapReturnTypes(Stream<Customer> customerStream ) {
		
		
		// List<Order>-STream
		Stream<List<Order>> ordersListStream = customerStream.map(customer -> customer.orders);
		
		// Integer-Stream
		Stream<Integer> integerStream =  ordersListStream.map(orders -> {
			int customerBill = 0;
			for(Order order : orders) {
				customerBill += order.price; 
			}
			return customerBill;
		});
		
		Optional<Integer> sumPrices = integerStream.reduce((bill_1, bill_2) -> bill_1 + bill_2);
	
		System.out.println("sumUpPricesMapReduce(): Sum of all customer's bills: " + sumPrices.get());
	}
	
	
	
	List<Customer> createCustomerListBig() {
		
		List<Customer> customers = new ArrayList<Customer>();
		// no measurable difference
		//List<Customer> customers = new LinkedList<Customer>();
		
		int prodIndex = 0;
		for(int cust = 0 ; cust < CUSTOMER_COUNT; cust ++) {
			
			Customer customer = new Customer("Customer_" + cust);
			customers.add(customer);
			
			
			// Add Orders to Customer
			for(int ord = 0; ord < ORDERS_PER_CUSTOMER; ord ++) {
				customer.addOrder(products[(prodIndex++ % (products.length-1))]);
			}
		}
		return customers;
	}
	
	
	List<Customer> createCustomerListSmall() {
		
		Customer[] customerArray = {new Customer("Hans", "Waschpulver"), new Customer("Jupp", "Cola"), new Customer("Susi", "Socken")};
		
		ArrayList<Customer> customerList = new ArrayList<Customer>(Arrays.asList(customerArray));
		
		System.out.println(customerList);
		
		return customerList;
	}
	
	void printCustomersArray(List<Customer> customers ){
		
		System.out.println(customers);
		
	}

}
