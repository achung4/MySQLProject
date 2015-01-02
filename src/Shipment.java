import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;


public class Shipment {
	
	
	public static void insertShipment(Connection con, String supName)
    {
	
	
	PreparedStatement  ps;
	  
	try
	{
	  ps = con.prepareStatement("INSERT INTO Shipment VALUES (shipCounter.nextVal,?,CURRENT_TIMESTAMP)");
	 
	  ps.setString(1, supName);

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
	
	public static void deleteShipment(Connection con, String sid)
    {
	PreparedStatement  ps;
	  
	try
	{
	  ps = con.prepareStatement("DELETE FROM Shipment WHERE sid = ?");
	  ps.setString(1, sid);

	  int rowCount = ps.executeUpdate();

	  if (rowCount == 0)
	  {
	      System.out.println("\nItem " + sid + " does not exist!");
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
    


public static void updateShipment(Connection con, String sid, String supName)
{
PreparedStatement  ps;
  
try
{
  ps = con.prepareStatement("UPDATE item SET supName = ? WHERE sid = ?");

  ps.setString(2, sid);
  ps.setString(1, supName);

  int rowCount = ps.executeUpdate();
  if (rowCount == 0)
  {
      System.out.println("\nItem " + sid + " does not exist!");
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

