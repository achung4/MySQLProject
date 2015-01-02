import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class item {
   
	public static void insertItem(Connection con, String upc, String title, String type, String category, String company, Date itemYear, double sellPrice, int quantity){
   
   	 PreparedStatement  ps;
   	 try
   	 {
   		 ps = con.prepareStatement("INSERT INTO item VALUES (?,?,?,?,?,?,?,?)");
   	 
   		 ps.setString(1, upc);
   		 ps.setString(2, title);
   		 ps.setString(3, type);
   		 ps.setString(4, category);
   		 ps.setString(5, company);
   		 ps.setDate(6, itemYear);
   		 ps.setDouble(7, sellPrice);
   		 ps.setInt(8, quantity);

   		 ps.executeUpdate();

   		 // commit work
   		 con.commit();
   		 ps.close();
   	 }
   	 catch (SQLException ex){
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
   
    public static void deleteItem(Connection con, String upc){
   	 PreparedStatement  ps;
 	 
   	 try{
   		 ps = con.prepareStatement("DELETE FROM Item WHERE upc = ?");
   		 ps.setString(1, upc);

   		 int rowCount = ps.executeUpdate();

   		 if (rowCount == 0) {
   			 System.out.println("\nItem " + upc + " does not exist!");
   		 }
   		 con.commit();
   		 ps.close();
   	 }
   	 catch (SQLException ex){
   		 System.out.println("Message: " + ex.getMessage());
   		 try    {
   		 con.rollback();   
   		 }
   		 catch (SQLException ex2){
   			 System.out.println("Message: " + ex2.getMessage());
   			 System.exit(-1);
   		 }
   	 }
   }
   


    public static void updateItemQuantity(Connection con, String upc, int quantity){
   	 PreparedStatement  ps;
   	 ResultSet 			rs;
   	 int 				newQuant = quantity;
   	 
   	 try    {
   		 
   		 ps = con.prepareStatement("Select quantity from item where upc = ?");
   		 
   		 ps.setString(1, upc);
   		 
   		 rs = ps.executeQuery();
   		 
   		 while(rs.next())
   		 newQuant = quantity + rs.getInt("quantity");
   		 
   		 ps = con.prepareStatement("UPDATE item SET quantity = ? WHERE upc = ?");

   		 ps.setString(2, upc);

   		 ps.setInt(1, newQuant);

   		 int rowCount = ps.executeUpdate();
   		 if (rowCount == 0) {
   			  System.out.println("\nItem " + upc + " does not exist!");
   		 }

   		 con.commit();

   		 ps.close();
   	 }
   	 catch (SQLException ex){
   		System.out.println("Message: " + ex.getMessage());
   	    
   		 try  {
   		 con.rollback();   
   		 }
   		 catch (SQLException ex2) {
   			 System.out.println("Message: " + ex2.getMessage());
   			 System.exit(-1);
   		 }
   	 }   
    }
    
    public static void updateItemPrice(Connection con, String upc, double sellPrice) {
      	 PreparedStatement ps;
      	 try {
      		 ps = con.prepareStatement("UPDATE item SET sellPrice = ? WHERE upc = ?");

      		 ps.setString(2, upc);
      		 
      		 ps.setDouble(1, sellPrice);

      		 int rowCount = ps.executeUpdate();
      		 if (rowCount == 0) {
      			 System.out.println("\nItem " + upc + " does not exist!");
      		 }

      		 con.commit();

      		 ps.close();
      	 }  catch (SQLException ex) {
      		 System.out.println("Message: " + ex.getMessage());

      		 try {
      			 con.rollback();
      		 } catch (SQLException ex2) {
      			 System.out.println("Message: " + ex2.getMessage());
      			 System.exit(-1);
      		 }
      	 }
       }
}