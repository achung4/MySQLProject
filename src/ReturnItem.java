//import java.io.IOException;
import java.sql.Connection;
//import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ReturnItem{
    
    //generate retid
    public static boolean insertReturnItem(Connection con, String upc, int quantity){
   	 PreparedStatement  ps;
   	 boolean proccessComplete = false;
   	 try{
   		 con.setAutoCommit(false);
   		 ps = con.prepareStatement("INSERT INTO returnItem VALUES (retidCounter.currVal,?,?)");
   	    
   		 ps.setString(1, upc);
   		 ps.setInt(2, quantity);

   		 ps.executeUpdate();
   		 // commit work
   		 con.commit();

   		 ps.close();
   		 proccessComplete = true;
   	 }    
   	 catch (SQLException ex){
   		 System.out.println("Message: " + ex.getMessage());
   		 try{
   			 // undo the insert
   			 con.rollback();   	 
   		 }
   		 catch (SQLException ex2){
   			 System.out.println("Message: " + ex2.getMessage());
   			 System.exit(-1);
   		 }
   	 }
   	 return proccessComplete;
    }

    public void deleteReturnItem(Connection con, String retid, String upc){  
   	 PreparedStatement  ps;
   	 try{
   		 ps = con.prepareStatement("DELETE FROM returnItem WHERE retid = ? AND upc = ?");
   		 ps.setString(2, upc);
   		 ps.setString(1, retid);

   	 int rowCount = ps.executeUpdate();
   	 if (rowCount == 0) {
   		 System.out.println("\nupc " + upc + " or retid: " + retid + " does not exist!");
   	 }
   	 con.commit();
   	 ps.close();
   	 }
   	 catch (SQLException ex){
   		 System.out.println("Message: " + ex.getMessage());

   		 try{
   			 con.rollback();   	 
   		 }
   		 catch (SQLException ex2){
   			System.out.println("Message: " + ex2.getMessage());
   			System.exit(-1);
   		 }
   	 }
    }

    public void updateReturnItem(Connection con, String retid, String upc, int newQuantity) {
   	 PreparedStatement  ps;
   	 try{
   	 ps = con.prepareStatement("UPDATE returnItem SET quantity = ? WHERE upc = ? AND retid = ?");

   	 ps.setInt(1, newQuantity);
   	 ps.setString(2, upc);
   	 ps.setString(3, retid);

   	 int rowCount = ps.executeUpdate();
   	 if (rowCount == 0) {
   		 System.out.println("\nupc: " + upc + " or retid: " + retid + " does not exist!");
   	 }    

   	 con.commit();

   	 ps.close();
   	 }
   	 catch (SQLException ex){
   		 System.out.println("Message: " + ex.getMessage());
  	 
   		 try{
   			 con.rollback();   	 
   		 }
   		 catch (SQLException ex2){
   			 System.out.println("Message: " + ex2.getMessage());
   			 System.exit(-1);
   		 }
   	 }   	 
    }

}

