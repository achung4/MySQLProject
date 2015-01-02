import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;


public class ShipItem {
	
	
	public static void insertShipItem(Connection con, String upc, double supPrice, int quantity)
    {
	
	
	PreparedStatement  ps;
	  
	try
	{
	  ps = con.prepareStatement("INSERT INTO shipItem VALUES (shipCounter.currVal,?,?,?)");
	 
	  ps.setString(1, upc);
	  ps.setDouble(2, supPrice);
	  ps.setInt(3, quantity);
	  
	  ps.execute();

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
	public static void deleteShipItem(Connection con, String sid, String upc)
    {
	PreparedStatement  ps;
	  
	try
	{
	  ps = con.prepareStatement("DELETE FROM ShipItem WHERE sid = ? AND upc = ?");
	  ps.setString(1, sid);
	  ps.setString(2, upc);

	  int rowCount = ps.executeUpdate();

	  if (rowCount == 0)
	  {
	      System.out.println("\nShipItem " + sid + " " + upc + " does not exist!");
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
    


public static void updateShipItem(Connection con, String sid, String upc,  double supPrice, int quantity)
{
PreparedStatement  ps;
  
try
{
  ps = con.prepareStatement("UPDATE item SET supPrice = ?, quantity = ? WHERE sid = ? AND upc = ?");

  ps.setString(3, sid);
  ps.setString(4, upc);
  ps.setDouble(1, supPrice);
  ps.setInt(2, quantity);

  int rowCount = ps.executeUpdate();
  if (rowCount == 0)
  {
      System.out.println("\nShipItem" + sid + " " + upc + " does not exist!");
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