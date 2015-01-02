import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;


public class RetreivalTransactions {
   
private Connection con;
private BufferedReader in = new BufferedReader(new InputStreamReader(
      	System.in));

public RetreivalTransactions(Connection connection)
{
	con = connection;
}

public ResultSet getReport(String rptDate){
  	 
    	PreparedStatement 	ps;
    	ResultSet 	rs;
        	try {
        	con.setAutoCommit(false);
        	ps = con.prepareStatement("SELECT I.upc, category, sellPrice, sum(PI.quantity) as TotalQuantity, sum(PI.quantity)*sellPrice as TotalPrice FROM item I, purchase P, purchaseItem PI Where P.receiptID= PI.receiptID AND I.upc = PI.upc AND P.purchaseDate >= ? AND P.purchaseDate < ? Group by category, i.upc, sellPrice");
      	 
        	Date repDate = Date.valueOf(rptDate);
      	 
// adds the number of milliseconds that are in a day.
        	Date repDatePlus = new Date(repDate.getTime() + 86400000);
      	 
        	ps.setDate(1, repDate);
        	ps.setDate(2, repDatePlus);
      	 
      	 
        	rs = ps.executeQuery();
      	 
        	con.commit();
        	return rs;
      	 
      	 
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
        	

      	 
    	return null;
	}

public ResultSet getShipSummary()
{
    
	PreparedStatement 	ps;
	ResultSet 	rs;
    	try {
    	con.setAutoCommit(false);
    	ps = con.prepareStatement("SELECT upc, SI.quantity, SI.supPrice from shipment S1, shipItem SI where S1.sid = SI.sid AND not exists (Select S2.sid from Shipment S2 where S2.sid > S1.sid)");	 
  	 
    	rs = ps.executeQuery();
  	 
    	con.commit();
    	return rs;
  	 
  	 
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

  	 
	return null;    
}

public DefaultTableModel buildTableModel(ResultSet rs)
    	throws SQLException {

	ResultSetMetaData metaData = rs.getMetaData();

	// names of columns
	Vector<String> columnNames = new Vector<String>();
	int columnCount = metaData.getColumnCount();
	for (int column = 1; column <= columnCount; column++) {
    	columnNames.add(metaData.getColumnName(column));
	}

	// data of the table
	Vector<Vector<Object>> data = new Vector<Vector<Object>>();
	while (rs.next()) {
    	Vector<Object> vector = new Vector<Object>();
    	for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
        	vector.add(rs.getObject(columnIndex));
    	}
    	data.add(vector);
	}

	return new DefaultTableModel(data, columnNames);

}

}
