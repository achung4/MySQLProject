
//USE java.sql.date not java.Date;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.*;

public class Purchase {
	
	static Connection con;
	
	public Purchase(Connection connection){
		con = connection;
	}
	
	public String padWithZeros (String s, char n){
		char [] charArray = new char [4];
		charArray[0] = '%';
		charArray[1] = '0';
		charArray[2] = n;
		charArray[3] = 'd';
		System.out.println("Padding with zeros" + n);
		int intVal = Integer.parseInt(s);
		String sformat =  new String(charArray);
				
		return String.format(sformat, intVal);
	}
	
	//public vs private methods within a class
	public void insertPurchase(String cid, String cardNo, String expire){
		
		System.out.println("Insert purchase method");
		PreparedStatement ps;
		
		SimpleDateFormat fm = new SimpleDateFormat ("dd/MM/yy");
		java.util.Date utilDate;
		
		
		try{
			ps = con.prepareStatement("INSERT INTO Purchase VALUES (receiptCounter.nextVal, CURRENT_TIMESTAMP, ?, ?, ?,  CURRENT_TIMESTAMP + interval '5' day, CURRENT_TIMESTAMP + interval '1' day)");
			//ps.setInt(1, 5);
			ps.setString(1, cid);
			ps.setString(2, cardNo);
			ps.setString(3, expire);
			
			ps.executeUpdate();
			con.commit();
			
			ps.close();
			
			System.out.println("Succesfull registration of purchase");
			
			
		}catch (SQLException ex){
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
	
	public static void deletePurchase(int receiptID){
		
		PreparedStatement ps;
		
		try{
			ps = con.prepareStatement("DELETE FROM Purchase WHERE receiptID = ?");
			ps.setInt(1, receiptID);
			
			int rowCount = ps.executeUpdate();
			if (rowCount == 0){
				System.out.println("\n Purchase" + receiptID + " does not exist!");
			}
			con.commit();
			ps.close();
		}catch (SQLException ex)
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
		System.out.println("Purchase with receipt number: "+ receiptID + " has been deleted");
	}

	public static void updatePurchase(int receiptID, String deliveredDate){
		
		PreparedStatement ps;
		
		try
		{
		  ps = con.prepareStatement("UPDATE purchase SET delivereddate = ? WHERE receiptID = ?");


		  ps.setInt(1, receiptID);
		  ps.setDate(2, Date.valueOf(deliveredDate));


		  int rowCount = ps.executeUpdate();
		  if (rowCount == 0)
		  {
		      System.out.println("\nPurchase " + receiptID + " does not exist!");
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
