package seminar.lifecoding.stream.behavior_collector_cuncurrent;

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
