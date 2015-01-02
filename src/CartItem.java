
public class CartItem {
	public String upc, title;
	public int quantity;
	public double price;
   
	public CartItem(String upc, String title, int quantity, double price) {
    	this.upc = upc;
    	this.title = title;
    	this.quantity = quantity;
    	this.price = price;
	}
}