import java.awt.GridLayout;
import java.io.*;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.text.*;
import java.lang.IllegalArgumentException;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

public class TopSellingItems {
    private static Connection con;

    private static BufferedReader in = new BufferedReader(
   		 new InputStreamReader(System.in));

    public TopSellingItems(Connection connection) {
   	 con = connection;
    }

    public String printTopSellingItems(String date, int n) {
   	 ResultSet rs;
   	 Date date_tsi;
   	 RetreivalTransactions rt = new RetreivalTransactions(con);
   	 String res = "Search Complete";
   	 //System.out.println("Top Selling Items");
   	 PreparedStatement ps;

   	 try {
   		 ps = con.prepareStatement("SELECT pi.upc, sum(pi.quantity) AS totalSold FROM purchaseitem pi, purchase p WHERE purchasedate >= ? AND purchaseDate < ? AND p.receiptID = pi.receiptID AND rownum < ? GROUP BY pi.upc  ORDER BY totalsold DESC");
   		 
   		 try{
   			 date_tsi = Date.valueOf(date);
   		 
   			 Date date_tsi_plus = new Date(date_tsi.getTime() + 86400000);
    
   			 ps.setDate(1, date_tsi);
   			 ps.setDate(2, date_tsi_plus);
    
   			 ps.setInt(3, n + 1);
   		 
   			 rs = ps.executeQuery();
   		 
   			 JPanel resultPanel = new JPanel();
   			 JTable table = new JTable(rt.buildTableModel(rs));
   			 JDialog resultWindow = new JDialog();
   			 resultWindow.setTitle("Top Selling Items");
   			 JScrollPane jsp = new JScrollPane(table);
   			 resultWindow.setBounds(100, 100, 600, 300);
   			 resultPanel.add(jsp);
   			 resultWindow.setContentPane(resultPanel);
   			 resultWindow.setVisible(true);
   		 }catch(IllegalArgumentException iae){
   			 res = "Date format is wrong!";
   		 }
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
   	 return res;
    }
}
