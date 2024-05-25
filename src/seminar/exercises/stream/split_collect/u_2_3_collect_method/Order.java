package seminar.exercises.stream.split_collect.u_2_3_collect_method;

public class Order{
	
	public String itemName = "";
	public int price = 0;
	
	public Order(String itemName, int price) {
		this.itemName = itemName;
		this.price = price;
	}
	
	public String toString() {
		return this.itemName + " " + this.price + " Euro ";
	}
}
