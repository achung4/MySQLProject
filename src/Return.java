import java.sql.*;
import java.sql.Date;
import java.util.*; // for GregorianCalendar

public class Return {

    // generate retid and returnDate
    public static boolean insertReturn(Connection con, String receiptId,
   		 String[] res) {
   	 PreparedStatement ps;
   	 boolean proccessComplete = false;
   	 try {
   		 con.setAutoCommit(false);
   		 Date returnSqlDate = new java.sql.Date(
   				 new java.util.Date().getTime()); // get today's date

   		 // select the purchase date based on receiptId
   		 ps = con.prepareStatement("SELECT purchaseDate FROM purchase where receiptId = ?");
   		 ps.setString(1, receiptId);
   		 ResultSet rs = ps.executeQuery();
   		 con.commit();

   		 rs.next();

   		 java.sql.Date purchaseSqlDate = rs.getDate(1); // get the date;

   		 Calendar cal = Calendar.getInstance();
   		 cal.setTime(purchaseSqlDate);
   		 cal.add(Calendar.DATE, 15); // add 15 days
   		 purchaseSqlDate = new java.sql.Date(cal.getTime().getTime()); // type
   																		 // casted
   																		 // calendar
   																		 // into
   																		 // sql
   																		 // date

   		 // if return date is before purchase date + 15 days
   		 if (purchaseSqlDate.after(returnSqlDate)) {
   			 ps = con.prepareStatement("INSERT INTO return VALUES (retidCounter.nextVal,CURRENT_TIMESTAMP,?)");
   			 ps.setString(1, receiptId);

   			 ps.executeUpdate();
   			 // System.out.println("Return complete.");
   			 proccessComplete = true;
   		 } else {
   			 /*
   			  * System.out.println("Sorry, proccess cannot be completed\n" +
   			  * "because it has been past 15 days since " +
   			  * "you bought this product");
   			  */
   			 res[0] = "Sorry, proccess cannot be completed\n"
   					 + "because it has been past 15 days\n "
   					 + "since you bought this product";
   			 proccessComplete = false;
   		 }
   		 // con.commit();
   		 ps.close();

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
   	 return proccessComplete;
    }

    public static void deleteReturn(Connection con, String receiptID) {

   	 PreparedStatement ps;

   	 try {
   		 ps = con.prepareStatement("DELETE FROM Return WHERE receiptID = ?");
   		 ps.setString(1, receiptID);

   		 int rowCount = ps.executeUpdate();
   		 if (rowCount == 0) {
   			 System.out.println("\n Return " + receiptID
   					 + " does not exist!");
   		 }
   		 con.commit();
   		 ps.close();
   	 } catch (SQLException ex) {
   		 System.out.println("Message: " + ex.getMessage());
   		 try {
   			 con.rollback();
   		 } catch (SQLException ex2) {
   			 System.out.println("Message: " + ex2.getMessage());
   			 System.exit(-1);
   		 }
   	 }
   	 System.out.println("Return with receipt number: " + receiptID
   			 + " has been deleted");
    }

    public static void updateReturn(Connection con, String retid,String receiptID) {

   	 PreparedStatement ps;

   	 try {
   		 ps = con.prepareStatement("UPDATE purchase SET retid = ? WHERE receiptID = ?");

   		 ps.setString(1, retid);
   		 ps.setString(2, receiptID);

   		 int rowCount = ps.executeUpdate();
   		 if (rowCount == 0) {
   			 System.out.println("\nReturn " + receiptID + " does not exist!");
   		 }

   		 con.commit();

   		 ps.close();
   	 } catch (SQLException ex) {
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