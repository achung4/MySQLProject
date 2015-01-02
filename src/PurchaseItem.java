import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class PurchaseItem {
	static Connection con;
	private int receiptID;
	private String upc;
	private int quantity;
	
	public PurchaseItem(Connection connection){
		con = connection;
	}
	
	public PurchaseItem(Connection connection, int arg_receiptID, String arg_upc, int arg_quantity){
		con = connection;
		receiptID = arg_receiptID;
		upc = arg_upc;
		quantity = arg_quantity;
	}
	
	public void setReceiptID(int id){
		receiptID = id; 
	}
	
	public int getReceiptID(){
		return receiptID;
	}
	
	public void setUPC(String upc_arg){
		upc = upc_arg;
	}
	
	public String getUPC(){
		return upc;
	}
	
	public void setQuantity(int q){
		quantity = q;
	}
	
	public int getQuantity(){
		return quantity;
	}
	
    public void insertPurchaseItem(String upc, int quantity)
    {
        
        
        PreparedStatement  ps;
          
        try
        {
          ps = con.prepareStatement("INSERT INTO purchaseItem VALUES (receiptCounter.currVal,?,?)");
        
          ps.setString(1, upc);
          ps.setInt(2, quantity);

          ps.executeUpdate();
          // commit work 
          con.commit();

          ps.close();
        }
        catch (SQLException ex)
        {
            System.out.println("Message: " + ex.getMessage());
            try 
            {
                // undo the insert
                con.rollback();        
            }
            catch (SQLException ex2)
            {
                System.out.println("Message: " + ex2.getMessage());
                System.exit(-1);
            }
        }
    }

    public static void deletePurchaseItem(int receiptID, String upc)
{
    PreparedStatement  ps;
      
    try
    {
      ps = con.prepareStatement("DELETE FROM purchaseItem WHERE receiptID = ? AND upc = ?");
      ps.setString(2, upc);
      ps.setInt(1, receiptID);


      int rowCount = ps.executeUpdate();


      if (rowCount == 0)
      {
          System.out.println("\nPurchaseItem " + upc + " receiptID: " + receiptID + " does not exist!");
      }


      con.commit();


      ps.close();
    }
    catch (SQLException ex)
    {
        System.out.println("Message: " + ex.getMessage());


        try 
        {
            con.rollback();        
        }
        catch (SQLException ex2)
        {
            System.out.println("Message: " + ex2.getMessage());
            System.exit(-1);
        }
    }
}

    public static void deleteAllFromSamePurchase(int receiptID){
  
    	   PreparedStatement  ps;
    	      
    	    try
    	    {
    	      ps = con.prepareStatement("DELETE FROM purchaseItem WHERE receiptID = ?");
    	      ps.setInt(1, receiptID);

    	      int rowCount = ps.executeUpdate();

    	      if (rowCount == 0)
    	      {
    	          System.out.println("\nPurchaseItems with receiptID: " + receiptID + " does not exist!");
    	      }

    	      con.commit();

    	      ps.close();
    	    }
    	    catch (SQLException ex)
    	    {
    	        System.out.println("Message: " + ex.getMessage());


    	        try 
    	        {
    	            con.rollback();        
    	        }
    	        catch (SQLException ex2)
    	        {
    	            System.out.println("Message: " + ex2.getMessage());
    	            System.exit(-1);
    	        }
    	    }
    	
    }
    
    public static void updatePurchaseItem(int receiptID, String upc, int newQuantity){
    PreparedStatement  ps;
    try{
      ps = con.prepareStatement("UPDATE purchaseItem SET quantity = ? WHERE upc = ? AND receiptID = ?");

      ps.setInt(1, newQuantity);
      ps.setString(2, upc);
      ps.setInt(3, receiptID);

      int rowCount = ps.executeUpdate();
      if (rowCount == 0) {
          System.out.println("\nPurchaseItem " + upc + " receiptID: " + receiptID + " does not exist!");
      }
      con.commit();
      ps.close();
    }
    catch (SQLException ex)
    {
        System.out.println("Message: " + ex.getMessage());
        
        try 
        {
            con.rollback();        
        }
        catch (SQLException ex2)
        {
            System.out.println("Message: " + ex2.getMessage());
            System.exit(-1);
        }
    }        
    }
    

    
}
