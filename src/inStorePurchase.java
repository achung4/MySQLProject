import java.io.*;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.text.*;
import java.lang.*;

import javax.swing.JFrame;


//missing credit card approval
// TODO: fix print receipt- the problem is in the SQL query; getCurrentReceiptID, cancel, update
// stock of items
public class inStorePurchase extends JFrame {
	
	private BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
	private Connection con;
	private branch br;
	// TODO: receipt will be generated as a sequence
	//private String defaultDate = new String("18/08/01");
	private String defaultExpire = "0432";
	static SimpleDateFormat fm = new SimpleDateFormat("dd/MM/yy");
	

	public inStorePurchase(Connection connection){
		con = connection;
	}
	
	public void InStorePurchase(){
		StartInStorePurchase();
	}
	
	public boolean addAnItemGUI(String upc_in){
		ArrayList<PurchaseItem> purItms = new ArrayList<PurchaseItem>();
		PreparedStatement ps;
		Statement stmt;
		ResultSet rs;
		boolean queryResult,success;
		int rownum = 0;
		
		try{
					con.setAutoCommit(false);
					ps = con.prepareStatement("SELECT * FROM item WHERE upc = ?");
					ps.setString(1, upc_in);
					rs = ps.executeQuery(); 
					while (rs.next()){
						rownum++;
					}
					if (rownum == 1){
						System.out.println("UPC approved");
						success  = true;
						}
					//queryResult = ps.execute();
					else{
						success = false;
						System.out.println("No item with upc " + upc_in + "exists");
	//TODO: manage non existing upc, throw an exception/let the user know//if returns false, do something
					}
					return success;
	
		}catch (SQLException ex) {
	   		 System.out.println("Message: " + ex.getMessage());
	   		 try {
	   			 // undo the insert
	   			 con.rollback();
	   		 } catch (SQLException ex2) {
	   			 System.out.println("Message: " + ex2.getMessage());
	   			 System.exit(-1);
	   			 return false;
	   		 }
		}
		return false;
	}
	
	// Returns true if items are approved, returns false if the are not approved
	public ArrayList<PurchaseItem> addItems(){
		String choice, upc_in;
		String upc_choice = "";
		int quant_in;
		ArrayList<PurchaseItem> purItms = new ArrayList<PurchaseItem>();
		PreparedStatement ps;
		Statement stmt;
		ResultSet rs;
		boolean queryResult,success;
		int rownum = 0;
		
		System.out.println("Please enter the values for the following: ");
		
		try{
			do{
				do{
					System.out.println("UPC:");
					upc_in = in.readLine();
					con.setAutoCommit(false);
					ps = con.prepareStatement("SELECT * FROM item WHERE upc = ?");
					ps.setString(1, upc_in);
					rs = ps.executeQuery(); 
					while (rs.next()){
						rownum++;
					}
					if (rownum == 1){
						System.out.println("UPC approved");
						success  = true;
						}
					//queryResult = ps.execute();
					else{
						success = false;
						System.out.println("No item with upc " + upc_in + "exists");
						System.out.println("Would you like to try again? yes/no");
						upc_choice = in.readLine();
					}
				}while(upc_choice.equalsIgnoreCase("yes") && !success);
				
				if(success){
					System.out.println("Quantity");
					quant_in = Integer.parseInt(in.readLine());
					PurchaseItem pi = new PurchaseItem(con);
					pi.setUPC(upc_in);
					pi.setQuantity(quant_in);
					purItms.add(pi);
				}
				
				else{
					System.out.println("Failed to add item with upc " + upc_in);
				}
				
				System.out.println("Add more items: yes/no");
				choice = in.readLine();
			}while(choice.equalsIgnoreCase("yes"));
		}catch (IOException err){
			return null;
		}catch (SQLException ex) {
	   		 System.out.println("Message: " + ex.getMessage());
	   		 try {
	   			 // undo the insert
	   			 con.rollback();
	   		 } catch (SQLException ex2) {
	   			 System.out.println("Message: " + ex2.getMessage());
	   			 System.exit(-1);
	   			 return null;
	   		 }
		}
		return purItms;
	}
	
	public double calculatePriceGUI(ArrayList<PurchaseItem> purItms){
		double total = 0.0;
		double itmTotal = 0.0;
		String upc = "";
		int quant;
		PreparedStatement ps;
		Statement stmt;
		ResultSet rs;
		try{
			con.setAutoCommit(false);
			for( PurchaseItem pi : purItms){
				upc = pi.getUPC();
				quant = pi.getQuantity();
				ps = con.prepareStatement("SELECT i.sellprice * ? as Total FROM Item i WHERE i.upc=?");
				ps.setInt(1, quant);
				ps.setString(2, upc);
				
				rs = ps.executeQuery();
				while (rs.next()){
					itmTotal = rs.getDouble("Total");
				}
				 System.out.println("per item total Successful, upc  = " + upc + "quantity: " + quant);
				 total += itmTotal;
			}
			return total;

	   	 } catch (SQLException ex) {
	   		 System.out.println("Message: " + ex.getMessage());
	   		 try {
	   			 // undo the insert
	   			 con.rollback();
	   		 } catch (SQLException ex2) {
	   			 System.out.println("Message: " + ex2.getMessage());
	   			 System.exit(-1);
	   		 }
		}
		return total;
	}
	
	public double calculatePrice(ArrayList<PurchaseItem> purItms){
		double total = 0.0;
		double itmTotal = 0.0;
		String upc = "";
		int quant;
		PreparedStatement ps;
		Statement stmt;
		ResultSet rs;
		try{
			con.setAutoCommit(false);
			for( PurchaseItem pi : purItms){
				upc = pi.getUPC();
				quant = pi.getQuantity();
				ps = con.prepareStatement("SELECT i.sellprice * ? as Total FROM Item i WHERE i.upc=?");
				ps.setInt(1, quant);
				ps.setString(2, upc);
				
				rs = ps.executeQuery();
				while (rs.next()){
					itmTotal = rs.getDouble("Total");
				}
				 System.out.println("per item total Successful, upc  = " + upc + "quantity: " + quant);
				 total += itmTotal;
			}
			return total;

	   	 } catch (SQLException ex) {
	   		 System.out.println("Message: " + ex.getMessage());
	   		 try {
	   			 // undo the insert
	   			 con.rollback();
	   		 } catch (SQLException ex2) {
	   			 System.out.println("Message: " + ex2.getMessage());
	   			 System.exit(-1);
	   		 }
		}
		return total;
	}
	
	public int retrieveReceiptID(){
		PreparedStatement ps;
		ResultSet rs;
		Statement stmt;
		int receiptID = 0;
		
		try {
			System.out.println("Retrieving receipt id");
	   		 stmt = con.createStatement();
	   		 rs = stmt.executeQuery("SELECT max(receiptID) FROM Purchase");
	   		 
	   		 while (rs.next()) {
	   			 receiptID = Integer.parseInt(rs.getString(1));
	   		 }
	   		 
	   		 System.out.println("receiptID retrieval Successful, receiptID = " + receiptID);
	   		
	   		 // close the statement;
	   		 // the ResultSet will also be closed
	   		 stmt.close();
	   		 return receiptID;
	   		 
	   	 } catch (SQLException ex) {
	   		 System.out.println("Message: " + ex.getMessage());
	   	 }
		return receiptID;
	}
	

	public int getCurrentReceiptID(){
		PreparedStatement ps;
		ResultSet rs;
		Statement stmt;
		int rid = 0;
	   	 try {
	   		 stmt = con.createStatement();

	   		 rs = stmt.executeQuery("SELECT receiptcounter.currval FROM purchase");

	   		 // get info on ResultSet
	   		 ResultSetMetaData rsmd = rs.getMetaData();

	   		 // get number of columns
	   		int numCols = rsmd.getColumnCount();
	   		System.out.println("num cols " + numCols);
	   		System.out.println(" ");

	   		 // display column names;
	   		 for (int i = 0; i < numCols; i++) {
	   			 // get column name and print it

	   			 System.out.printf("%-15s", rsmd.getColumnName(i + 1));
	   		 }

	   		 System.out.println(" ");
	   		 if (rs.next()){
   			 rid = rs.getInt("currval");
   			 System.out.printf("%-10.10s", rid);
   			 }
	   		 else
	   			System.out.println("empty resutl set");
	   		 // close the statement;
	   		 // the ResultSet will also be closed
	   		 stmt.close();
	   		 return rid;
	   	 } catch (SQLException ex) {
	   		 System.out.println("Message: " + ex.getMessage());
	   	 }
	   	 return rid;	
	   	 }
	
	public void registerPurchaseGUI(ArrayList<PurchaseItem> purItms, String cardNo, String cid, String expire) throws InvalidCardException {
		int choice, paymentChoice, receiptID, quantity;
		String upc;
		PreparedStatement ps;
		ResultSet rs;
		Statement stmt;
		item i= new item ();
		String payCash;
		Boolean completePurchase = false;
		
		cid = "stor";
	
			if(cardNo == null && expire == null){
				cardNo = "cash";
				expire = "noex";
				completePurchase = true;
				// Should  be set to null
			}
			// Should check for non-existing options - illegal entry
			// Manage credit card failure
			else {	
				if(!creditCardAuthorization(cardNo, expire)){
					throw (new InvalidCardException());
					}
			}
			if (completePurchase){
				Purchase p = new Purchase(con);
				p.insertPurchase(cid, cardNo, expire);
				receiptID = retrieveReceiptID();
				//register purchase items
				for (PurchaseItem pi : purItms){
					upc = pi.getUPC();
					quantity = pi.getQuantity();
					pi.insertPurchaseItem(upc, quantity);
					item.updateItemQuantity(con, upc, quantity);
					//TODO remember to update item's stock
				}
				System.out.println("Purchase completed");
				printReceipt(receiptID, cid);
			}
			else
				System.out.println("Purchase canceled");
		
	}
	
	public void registerPurchase(ArrayList<PurchaseItem> purItms){
		int choice, paymentChoice, receiptID, quantity;
		String cardNo, cid, upc, expire;
		PreparedStatement ps;
		ResultSet rs;
		Statement stmt;
		item i= new item ();
		String payCash;
		Boolean completePurchase = false;
		
		cid = "stor";
		
		try{
			con.setAutoCommit(false);
			
			System.out.println("Select payment :");
			System.out.println("1. Cash");
			System.out.println("2. Credit Card");
			paymentChoice = Integer.parseInt(in.readLine());
		
			if(paymentChoice == 1){
				cardNo = "cash";
				expire = "noex";
				completePurchase = true;
				// Should  be set to null
			}
			// Should check for non-existing options - illegal entry
			// Manage credit card failure
			else {
				System.out.println("Enter card number");
				cardNo =in.readLine();
				System.out.println("Enter the card's expire date");
				expire = in.readLine();	
				if(!creditCardAuthorization(cardNo, expire)){
					System.out.println("Credit Card denied");
					System.out.println("Would you like to make the payment by cash? yes/no");
					payCash = in.readLine();
					if(payCash.equalsIgnoreCase("yes")){
						cardNo = "cash";
						expire = "noex";
						completePurchase = true;
					}
					else{
						completePurchase = false;
					
					}
				}
			}
			if (completePurchase){
				Purchase p = new Purchase(con);
				p.insertPurchase(cid, cardNo, expire);
				//receiptID = retrieveReceiptID();
				//register purchase items
				for (PurchaseItem pi : purItms){
					upc = pi.getUPC();
					quantity = pi.getQuantity();
					pi.insertPurchaseItem( upc, quantity);
					item.updateItemQuantity(con, upc, quantity);
					//TODO remember to update item's stock
				}
				System.out.println("Purchase completed");
				//printReceipt(receiptID, cid);
			}
			else
				System.out.println("Purchase canceled");
	
		}catch (IOException err){
			//TODO: Write a message
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}			
	}
	
	public boolean creditCardAuthorization(String cardNo, String expire){
		System.out.println("Processing credit card request...");
		if(cardNo.length()!= 16){
			System.out.println("Credit card not authorized");
			return false;
		}
		System.out.println("Credit Card Approved");
		return true;
	}
	
	/*
	public void cancelPurchase(int receiptID){
		PurchaseItem pi = new PurchaseItem(con);
		pi.deleteAllFromSamePurchase(receiptID);
		//SHOULD ALSO DELETE ALL PURCHASEITEMS WITH RECEIPTID
	
		}*/
	
	// ANGELO!
	public ResultSet receiptGUI(int receiptID, String customerID){
		Date purchaseDate;
		//int quantity;
		float total, sellprice, quantity;
		String cardNo, upc;
		
		System.out.println("Printing receipt");
		PreparedStatement ps;
		ResultSet rs;
		
		try{
			
			ps = con.prepareStatement("SELECT p.receiptid, p.purchasedate, p.card_no, pi.upc, pi.quantity, i.sellPrice, pi.quantity*i.sellprice AS total FROM purchase p, purchaseitem pi, item i where p.cid = ? AND p.receiptID = ? AND p.receiptid = pi.receiptid AND i.upc = pi.upc");
			
			ps.setString(1, customerID);
			ps.setInt(2, receiptID);
			
			rs = ps.executeQuery();

			ps.close();
			return rs;
			
		}catch (SQLException ex)
		{
		    System.out.println("Message: " + ex.getMessage());
		    return null;
		}
	}
	
	public void printReceipt(int receiptID, String customerID){
		Date purchaseDate;
		//int quantity;
		float total, sellprice, quantity;
		String cardNo, upc;
		
		System.out.println("Printing receipt");
		PreparedStatement ps;
		ResultSet rs;
		
		try{
			
			ps = con.prepareStatement("SELECT p.receiptid, p.purchasedate, p.card_no, pi.upc, pi.quantity, i.sellPrice, pi.quantity*i.sellprice AS total FROM purchase p, purchaseitem pi, item i where p.cid = ? AND p.receiptID = ? AND p.receiptid = pi.receiptid AND i.upc = pi.upc");
			
			ps.setString(1, customerID);
			ps.setInt(2, receiptID);
			
			rs = ps.executeQuery();
			
			//get info on ResultSet
			ResultSetMetaData rsmd = rs.getMetaData();
			
			// get number of columns
			int numCols = rsmd.getColumnCount();
			
			System.out.println(" ");
			
			for (int i = 0; i < numCols; i++)
			  {
			      // get column name and print it

			      System.out.printf("%-15s", rsmd.getColumnName(i+1));    
			  }			  
			  
		  while(rs.next()){
			  
			  System.out.printf("%-10.10s", receiptID);
			  
			  purchaseDate = rs.getDate("purchasedate");
			  System.out.printf("%-10.10s", purchaseDate);
				
			  //TODO: Just retrieve the last 5 digits, use sub-string
			  cardNo = rs.getString("card_no");
			  if (cardNo.length() == 1)
				  System.out.printf("%-10.10s", cardNo.substring(11));
			 
			  System.out.printf("%-10.10s", cardNo);
				  
			  upc = rs.getString("upc"); 
			  System.out.printf("%-10.10s",upc);
				  
			  quantity = rs.getFloat("quantity");
			  System.out.printf("%-10.10s", quantity);
				  
			  sellprice = rs.getFloat("sellprice");
			  System.out.printf("%-10.10s", sellprice);
				  
			  total = rs.getFloat("total");
			  System.out.printf("%-10.10s", total);
				  
			  }
		  
		
		  ps.close();
			
		}catch (SQLException ex)
		{
		    System.out.println("Message: " + ex.getMessage());
		}
		
	}
	
	public void StartInStorePurchase(){
		int choice, paymentChoice; 
		int rid = 0;
		String cid, cardNo;
		
		System.out.println("In Store Purchase");

		try{
			// TODO: We'll have a default customer id for instore purchases "stor"
			
			System.out.println("Select your option:");
			System.out.println("1. Add items to purchase");
			System.out.println("2. Go Back");
			//System.out.println("3. Print some receipt");
			choice = Integer.parseInt(in.readLine());
			
			switch(choice)
			{
			   case 1:  {
				   ArrayList <PurchaseItem> pitms = new ArrayList<PurchaseItem>();
				   double total;
				   String accept;
				   pitms = addItems();
				   if (pitms != null && !pitms.isEmpty()){
					   total = calculatePrice(pitms);
					   System.out.println("Your grand total is: " + total);
					   System.out.println("Do you accept? : yes/no");
					   //try catch input output exception
					   accept = in.readLine();
					   if(accept.equalsIgnoreCase("yes")){
						   System.out.println("Registering purchase");
						   registerPurchase(pitms);
					   }
					   else{
						   System.out.println("Purchase canceled");
						   br.showMenu();
					   }   
				   }
				   else
					   System.out.println("No purchase was made");
				   br.showMenu();
				   } break;
			   case 2: { 
				   // GET the receipt ID of the most recent purchase using max :)
				   //cancelPurchase(rid);
				   br.showMenu();
			   } break;
			   /*case 3: {
				   System.out.println("GIVE US THE RECEIPT ID");
				   rid = Integer.parseInt(in.readLine());
				   System.out.println("GIVE US THE CUSTOMER ID");
				   cid = in.readLine();
				   printReceipt(rid, cid);
				   }
			   break;*/
			}
		}catch (IOException err){
			//TODO: Write a message
		}
	}
	
	/* STEP 1: GET all the items the customer will buy (type and quantity)
	   STEP 2: Generate receiptID
	   STEP 3: Ask if its cash or card purchase
	   STEP 4: Credit card authorization
	   			if fails: a) cancel b) cash purchase
	   STEP 3: IF purchase not cancelled:
	   			INSERT purchase 
	   			INSERT purchaseItems
	 * STEP 6: Print receipt
	*/
	
}
