import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class InsertionTransactions {

	private Connection con;
	private BufferedReader in = new BufferedReader(new InputStreamReader(
            	System.in));
   

	public InsertionTransactions( Connection connection)
	{
		con = connection;
	}
		/*
		1. insert a new shipment -  INSERT into Shipment Values(newId, supplierName, currentDate)
		2. insert a new ship item - INSERT into ShipItem Values (newId, upc, quantity)
		3. update the quantities and prices of each item.
		*/   
	
	
	public void putShipment(String supName)
	{
	Shipment.insertShipment(con, supName);
	   
	}
	
	public void putShipItem(String upc, int quantity, double supCost)
	{
	ShipItem.insertShipItem(con, upc, supCost, quantity);    
	item.updateItemQuantity(con, upc, quantity);
	if (supCost > 0)
	item.updateItemPrice(con, upc, supCost*1.2);
	
	}
	
	/*
	} catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	   
	}
	
	
	}*/
	
	public void processReturn(){
	   	 boolean quit = false;
	   	 
	   	 String upc;
	   	 String receiptId;
	   	 int quantity;
	   	 int choice;
	   	 while(!quit){
	   		 try {
	   	      	// disable auto commit mode
	   			 con.setAutoCommit(false);
	   			 
	   	 			 System.out.println("Input the RECEIPT ID:"); //ask for receiptId
	   	 			 receiptId = in.readLine();
	   	 			 
	   	 			 System.out.println("Input the UPC of the item:"); // ask for upc
	   	 			 upc = in.readLine();
	   	 			 
	   	 			 System.out.println("How many of this ITEM would you like to return?"); // ask for quantity
	   	         	quantity = Integer.parseInt(in.readLine());
	   	 			 
	   	         	putReturn(upc,receiptId,quantity); // method in this class
	   	     	 
	   	      	System.out.println("Would you like to return another Item?");
	   	      	System.out.println("1. Yes");
	   	      	System.out.println("2. No");
	   	      	choice = Integer.parseInt(in.readLine());
	   	     	 
	   	      	switch(choice){
	   		      	case 1: break;
	   		      	case 2: quit = true;// b.showMenu();
	   		      	default: quit = true; //b.showMenu();
	   	      	}
	   	     	}
	   		 
	   		 catch(NumberFormatException e) {
	   				 System.out.println("That is not a valid number!\nexiting...");
	   				 quit = true;
	   	 	}
	   	 	catch(IOException e1){
	   	     	e1.printStackTrace();
	   	 	}
	   		 catch(SQLException e2){
	   	     	e2.printStackTrace();
	   	 	}
	   	 }
	   	 //b.showMenu();
	    }
	    public String putReturn(String upc, String receiptId, int quantity){
	   	try {
	       	con.setAutoCommit(false);
	       	PreparedStatement  ps;
	       	ResultSet rs;
	       	String upcData;
	       	String receiptIdData;
	       	int quantityData;
	       	double price;
	       	ps = con.prepareStatement("SELECT upc, receiptID, quantity FROM purchaseItem WHERE receiptId = ?");
	       	ps.setString(1, receiptId);    
	       	rs = ps.executeQuery();
	       	con.commit();
	      	 
	       	// check if query exists
	       	if (rs.next()) {
	               	upcData = rs.getString(1);
	               	receiptIdData = rs.getString(2);	// get receiptId from purchaseItem
	               	quantityData = rs.getInt(3);    	// get quantity from purchase Item
	              	//System.out.println("receipt data: "+receiptIdData); //for testing
	              	//System.out.println("quantity data: "+quantityData); //for testing
	           	// check if person has purchased this item
	           	// if quantityData is null it's 0 therefore  no such item has been purchased
	           	String respond = "";
	           	if(receiptIdData==null || quantityData == 0 || !upcData.equals(upc)){
	               	if(receiptIdData==null)
	                   	respond += "Sorry, receipt ID doesn't exist.";
	               	if(quantityData == 0)
	           			respond += "Sorry, quantity is not within your purchase quantity.";
	               	if(!upcData.equals(upc))
	           			respond += "Sorry, upc does not exists..";
	           			respond += "Please try again.";
	            	return respond;
	           	}
	              	else{ //receiptId exists and quantity is valid ||
	                  	String [] res = new String[1]; //magic trick! ;)
	                  	// check if you can return the item within 15 days from purchase
	                  	if(Return.insertReturn(con,receiptId,res)){ //parameters: Connection con, String receiptId
	                      	if(ReturnItem.insertReturnItem(con,upc,quantity) && (quantity<=quantityData)){ //parameters: Connection con,String upc, int quantity
	                          	item.updateItemQuantity(con, upc, quantity);
	                          	PurchaseItem pi = new PurchaseItem(con);
	                          	pi.updatePurchaseItem(Integer.parseInt(receiptId),upc,quantityData-quantity);
	                      	// refund their money because they want their money back!
	                      	ps = con.prepareStatement("select sellPrice from item where upc = ?");
	                      	ps.setString(1, upc);
	                      	rs = ps.executeQuery();
	                      	if(rs.next()){
	                          	price = rs.getDouble(1);
	                          	double total = ((int)price*quantity*100)/100; // round to 2 decimals (I think this works.)
	                          	//System.out.println("$" + total + " has been refunded to you!");
	                        	return("$" + total + " has been refunded to you!");
	                      	}
	                     		 else return "Quantity is invalid";
	                      	}
	                      	else return ("Quantity: "+quantity+" is greater than quantity in data: " +quantityData);
	                  	}
	                  	else return res[0];
	              	}
	       	}
	       	else {
	           	return("Invalid inputs for receiptId: " + receiptId  + " or quantity: " + quantity +
	       				"\n Please try again.");
	       	}
	      	 
	   	} catch (SQLException ex) {
	          	return ("Message: " + ex.getMessage());
	   	}
	   }
	}