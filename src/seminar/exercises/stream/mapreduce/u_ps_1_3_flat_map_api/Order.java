package seminar.exercises.stream.mapreduce.u_ps_1_3_flat_map_api;

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

///// Short Version for Slide
class OrderSlide{
	public String itemName = "";
	public int price = 0;
}