package seminar.exercises.stream.mapreduce.u_ps_1_3_flat_map_api;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Customer{
	public String customerName; 
	public List<Order> orders = new ArrayList<Order>();
	//public List<Order> orders = new LinkedList<Order>();
	
	public Customer(String name, String orderItem, int price){
		this.customerName = name;
		this.addOrder(new Order(orderItem, price));
	}
	
	public Customer(String name, String orderName){
		this(name, orderName, 1);

	}
	
	public Customer(String name, Order... orders){
		this.customerName = name;
		for(Order order : orders) {
			this.addOrder(order);
		}

	}
	public void addOrder(Order newOrder) {
		orders.add(newOrder);
	}
	
	public String toString() {
		
		return "\n" + this.customerName + " " + orders.toString(); 
 	}
}

/// Short Version for Slide
class CustomerSlide{
	public String customerName; 
	public List<Order> orders = new ArrayList<Order>();
}