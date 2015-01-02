import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.*;

class MainInterface extends JFrame {
    JPanel panel;
    private Connection con;

    public MainInterface(String title) {
   	 // call the superclass constructor
   	 super(title);
   	 panel = new JPanel();
   	 panel.setLayout(new FlowLayout());
   	 // this line adds the panel to the
   	 // Frame's content pane
   	 getContentPane().add(panel);
   	 // Initiate buttons
   	 JButton ispButton = new JButton("Make an In-store purchase");
   	 JButton opButton = new JButton("Make an Online purchase");
   	 JButton regButton = new JButton("Register a Customer");
   	 JButton retButton = new JButton("Process a Return");
   	 JButton topSellButton = new JButton("See Top-Selling Items");
   	 JButton dailyButton = new JButton("See a Daily Sales Report");
   	 JButton shpButton = new JButton("Process a Shipment");
   	 
   	 setUpIspButton(ispButton);
   	 setUpOpButton(opButton);   				 // button #2
   	 
   	 OpInterface opi = new OpInterface("INVISIBLE", con);
   	 opi.setVisible(false);
   	 opi.setUpRegisterButton(regButton);   		 // button #3 also setup button 3
   	 setUpReturnButton(retButton);   			 // button #4
   	 setUptopSellButton(topSellButton);   		 // button #5
   	 setUpDailyButton(dailyButton);    			 // button #6
   	 setUpShpButton(shpButton);   				 // button #7

   	 panel.setLayout(new GridLayout(8, 1));
   	 panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
   	 // setup the title of the page
   	 panel.add(createGreeting());
   	 // add all 6 buttons to panel
   	 panel.add(ispButton);   					 // button #1
   	 panel.add(opButton);   					 // button #2
   	 panel.add(regButton);   					 // button #3
   	 panel.add(retButton);   					 // button #4
   	 panel.add(topSellButton);   				 // button #5
   	 panel.add(dailyButton);   					 // button #6
   	 panel.add(shpButton);   					 // button #7

   	 // this line terminates the program when the X button
   	 // located at the top right hand corner of the
   	 // window is clicked
   	 addWindowListener(new WindowAdapter() {
   		 public void windowClosing(WindowEvent e) {
   			 System.exit(0);
   		 }
   	 });
    }
    
    

    // helper method: it creates a greeting
    private JPanel createGreeting() {
   	 JLabel titleLabel = new JLabel();
   	 titleLabel.setText("Welcome User!!");
   	 titleLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 36));
   	 JPanel titlePanel = new JPanel();
   	 titlePanel.add(titleLabel);

   	 return titlePanel;
    }
    // setup button #1
    private void setUpIspButton(JButton ispButton) {
   	 ispButton.addMouseListener(new MouseAdapter() {
   		 public void mouseClicked(MouseEvent e) {
   			 IPInterface ipi = new IPInterface("In Store Purchase",con);
   		 }
   	 });
   	 
    }
    // setup button #2
    private void setUpOpButton(JButton opButton) {
   	 opButton.addMouseListener(new MouseAdapter() {
   		 public void mouseClicked(MouseEvent e) {
   			 OpInterface opi = new OpInterface("Online Purchase", con);
   			 opi.pack();
   			 opi.setVisible(true);

   		 }
   	 });
    }

    

    // setup button #4
    private void setUpReturnButton(JButton retButton) {
   	 // TODO Auto-generated method stub
   	 retButton.addMouseListener(new MouseAdapter() {
   		 public void mouseClicked(MouseEvent e) {

   			 JDialog jd = new JDialog();
   			 jd.setTitle("Return Item");
   			 final JPanel panel = new JPanel();
   			 JPanel inputPanel = new JPanel();
   			 final JLabel respondLabel = new JLabel();
   			 respondLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 12));
   			 jd.setBounds(100, 100, 650, 300);
   			 panel.setLayout(new GridLayout(4, 1));

   			 JLabel instructionLabel = new JLabel(
   					 "Please input the following:");
   			 instructionLabel.setFont(new Font("Comic Sans MS", Font.BOLD,24));
   			 JLabel upcLabel = new JLabel("UPC:");
   			 inputPanel.add(upcLabel);
   			 final JTextField upcField = new JTextField(3);
   			 inputPanel.add(upcField);

   			 JLabel receiptId = new JLabel("Receipt ID:");
   			 inputPanel.add(receiptId);
   			 final JTextField receiptIdField = new JTextField(3);
   			 inputPanel.add(receiptIdField);

   			 JLabel quantityLabel = new JLabel("Quantity:");
   			 inputPanel.add(quantityLabel);
   			 final JTextField quantityField = new JTextField(3);
   			 inputPanel.add(quantityField);

   			 // final JLabel summary = new JLabel();
   			 // panel.add(summary);
   			 panel.add(instructionLabel);
   			 panel.add(inputPanel);

   			 jd.getContentPane().add(panel);

   			 jd.setVisible(true);

   			 JButton submitButton = new JButton("Submit");
   			 submitButton.addMouseListener(new MouseAdapter() {
   				 public void mouseClicked(MouseEvent e) {
   					 if ((!upcField.getText().equals(""))
   							 && (!receiptIdField.getText().equals(""))
   							 && (!quantityField.getText().equals(""))) {

   						 InsertionTransactions it = new InsertionTransactions(con);
   						 try {
   							 respondLabel.setText(it.putReturn(upcField
   									 .getText(), receiptIdField.getText(),
   									 Integer.parseInt(quantityField
   											 .getText())));
   							 // System.out.println(it.putReturn(upcLabel.getText(),receiptIdField.getText(),Integer.parseInt(quantityField.getText())));
   							 upcField.setText("");
   							 receiptIdField.setText("");
   							 quantityField.setText("");
   							 // repaint();
   						 } catch (NumberFormatException nfe) {
   							 respondLabel
   									 .setText("That is not a valid number! Please try again.");
   							 upcField.setText("");
   							 receiptIdField.setText("");
   							 quantityField.setText("");
   						 }
   					 } else
   						 respondLabel
   								 .setText("Please input something to all text fields.");

   				 }

   			 });

   			 panel.add(respondLabel);
   			 panel.add(submitButton);
   		 }
   	 });
    }
    private void setUptopSellButton(JButton topSellButton) {
   	 topSellButton.addMouseListener(new MouseAdapter() {
   		 public void mouseClicked(MouseEvent e) {
   			 TopSellingInterface tsi = new TopSellingInterface(con);
   		 }
   	 });
    }
    private void setUpDailyButton(JButton dailyButton) {
   	 // TODO Auto-generated method stub
   	 dailyButton.addMouseListener(new MouseAdapter() {
   		 public void mouseClicked(MouseEvent e) {

   			 JDialog jd = new JDialog();
   			 jd.setBounds(100, 100, 300, 200);
   			 JPanel p = new JPanel();
   			 p.setLayout(new FlowLayout());

   			 JLabel rptDateLabel = new JLabel("Date (YYYY-MM-DD:");
   			 p.add(rptDateLabel);
   			 final JTextField rptDate = new JTextField(3);
   			 p.add(rptDate);

   			 final JLabel summary = new JLabel();
   			 p.add(summary);

   			 jd.getContentPane().add(p);

   			 jd.setVisible(true);

   			 JButton rptButton = new JButton("Get Report");
   			 rptButton.addMouseListener(new MouseAdapter() {
   				 public void mouseClicked(MouseEvent e) {

   					 RetreivalTransactions rt = new RetreivalTransactions(
   							 con);

   					 try {
   						 ResultSet rpt = rt.getReport(rptDate.getText());
   						 JTable table = new JTable(rt.buildTableModel(rpt));
   						 JScrollPane scrollPane = new JScrollPane(table);
   						 JDialog resultWindow = new JDialog();
   						 resultWindow.setBounds(100, 100, 450, 500);
   						 JPanel resultPanel = new JPanel();
   						 resultPanel.add(scrollPane);

   						 JLabel total = new JLabel("TOTAL = $"
   								 + sumTotal(table));

   						 resultPanel.add(total);

   						 resultWindow.setContentPane(resultPanel);
   						 resultWindow.setVisible(true);

   					 } catch (SQLException e1) {
   						 // TODO Auto-generated catch block
   						 e1.printStackTrace();
   					 }
   					catch(IllegalArgumentException ex)
   		        	{
   		        		
   		        	}
   					
   		        	

   				 }

   			 });

   			 p.add(rptButton);

   		 }
   	 });
 
    }

    private double sumTotal(JTable table) {
   	 double total = 0;
   	 for (int i = 0; i < table.getModel().getRowCount(); i++)
   		 total = total
   				 + Double.parseDouble(table.getModel().getValueAt(i, 4)
   						 .toString());

   	 return total;
      
    }

    private void setUpShpButton(JButton shpButton) {
   	 shpButton.addMouseListener(new MouseAdapter() {
   		 public void mouseClicked(MouseEvent e) {

   			 JDialog jd = new JDialog();
   			 jd.setBounds(100, 100, 450, 300);
   			 JPanel p = new JPanel();
   			 p.setLayout(new FlowLayout());
   			 jd.setContentPane(p);

   			 JLabel supNameLabel = new JLabel("Supplier Name:");
   			 p.add(supNameLabel);
   			 JTextField supName = new JTextField(3);
   			 p.add(supName);

   			 JButton shpItemButton = new JButton("Insert Shipment");
   			 setUpShpItemButton(shpItemButton, supName, jd);
   			 p.add(shpItemButton);

   			 jd.setVisible(true);

   		 }
   	 });
    }

    private void setUpShpItemButton(JButton shpItemButton,
   		 final JTextField supName, final JDialog parent) {
   	 shpItemButton.addMouseListener(new MouseAdapter() {

   		 public void mouseClicked(MouseEvent e) {
   			 parent.dispose();
   			 Shipment.insertShipment(con, supName.getText());

   			 JDialog jd = new JDialog();
   			 jd.setBounds(100, 100, 450, 300);
   			 JPanel p = new JPanel();
   			 p.setLayout(new FlowLayout());

   			 JLabel upcLabel = new JLabel("UPC:");
   			 p.add(upcLabel);
   			 final JTextField upc = new JTextField(3);
   			 p.add(upc);

   			 JLabel quantityLabel = new JLabel("Quantity:");
   			 p.add(quantityLabel);
   			 final JTextField quantity = new JTextField(3);
   			 p.add(quantity);

   			 JLabel supCostLabel = new JLabel("Cost:");
   			 p.add(supCostLabel);
   			 final JTextField supCost = new JTextField(3);
   			 p.add(supCost);

   			 final JLabel summary = new JLabel();
   			 p.add(summary);

   			 jd.getContentPane().add(p);
   			 jd.setVisible(true);

   			 JButton nextButton = new JButton("Next Item");
   			 final JButton sumButton = new JButton("Shipment Summary");

   			 nextButton.addMouseListener(new MouseAdapter() {
   				 public void mouseClicked(MouseEvent e) {
   					 InsertionTransactions it = new InsertionTransactions(
   							 con);
   					 try {
   			       	if(supCost.getText().length() > 0)
   			       	it.putShipItem( upc.getText(), Integer.parseInt(quantity.getText()), Double.parseDouble(supCost.getText()));
   			       	else
   			       	it.putShipItem( upc.getText(), Integer.parseInt(quantity.getText()), -1);
   			          	 
   			     	}catch(NumberFormatException Ne) {} // do nothing

   					 upc.setText("");
   					 quantity.setText("");
   					 supCost.setText("");
   					 sumButton.addMouseListener(new MouseAdapter() {
   						 public void mouseClicked(MouseEvent e) {
   							 RetreivalTransactions rt = new RetreivalTransactions(con);

   							 try {
   								 ResultSet rpt = rt.getShipSummary();
   								 JTable table = new JTable(rt
   										 .buildTableModel(rpt));
   								 JDialog resultWindow = new JDialog();
   								 resultWindow.setBounds(100, 100, 450, 300);
   								 JPanel resultPanel = new JPanel();
   								 JScrollPane scrollPane = new JScrollPane(
   										 table);
   								 resultPanel.add(scrollPane);
   								 resultWindow.setContentPane(resultPanel);
   								 resultWindow.setVisible(true);

   							 } catch (SQLException e1) {
   								 // TODO Auto-generated catch block
   								 e1.printStackTrace();
   							 }

   						 }

   					 });

   				 }

   			 });

   			 p.add(nextButton);
   			 p.add(sumButton);

   		 }
   	 });
    }

    private boolean connect(String username, String password) {
   	 String connectURL = "jdbc:mysql://localhost/";

   	 try {
   		 con = DriverManager.getConnection(connectURL, username, password);
   		 System.out.println("\nConnected to Oracle!");
   		 return true;
   	 } catch (SQLException ex) {
   		 System.out.println("Message: " + ex.getMessage());
   		 return false;
   	 }
    }

    public static void main(String[] args) {

   	 MainInterface mainInterface = new MainInterface("M.A.A.D.'s Store"); //Montse, Angelo, Andrew, David  
   	 // this line causes the window to be sized to its
   	 // preferred size (this essentially compresses the
   	 // window)
   	 mainInterface.pack();
   	 // Initially, the JFrame is invisible. This line
   	 // makes the window visible.
   	 mainInterface.setVisible(true);

   	 try {
   		 // Load the Oracle JDBC driver
   		 DriverManager.registerDriver(new com.mysql.jdbc.Driver());
   	 } catch (SQLException ex) {
   		 System.out.println("Message: " + ex.getMessage());
   		 System.exit(-1);
   	 }

   	  if (mainInterface.connect("root", "sqltest")) { // "",
   															 // ""
   		 // if the username and password are valid,
   		 // remove the login window and display a text menu
   	 
   //	if (mainInterface.connect("", "")) { 
   		 System.out.println("ok");
   		 mainInterface.setVisible(true);
   	 }
    }

}



/*import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.*;

class MainInterface extends JFrame {
    JPanel panel;
    private Connection con;

    public MainInterface(String title) {
   	 // call the superclass constructor
   	 super(title);
   	 panel = new JPanel();
   	 panel.setLayout(new FlowLayout());
   	 // this line adds the panel to the
   	 // Frame's content pane
   	 getContentPane().add(panel);
   	 // another way of doing the above is
   	 // setContentPane(panel);
   	 JButton ispButton = new JButton("Make an In-store purchase");
   	 JButton opButton = new JButton("Make an Online purchase");
   	 JButton regButton = new JButton("Register a Customer");
   	 JButton retButton = new JButton("Process a Return");
   	 JButton topSellButton = new JButton("See Top-Selling Items");
   	 JButton dailyButton = new JButton("See a Daily Sales Report");
   	 JButton shpButton = new JButton("Process a Shipment");

   	 setUpDailyButton(dailyButton);
   	 setUpShpButton(shpButton);
   	 setUpOpButton(opButton);
	 setUpReturnButton(retButton);
   	 //setUptopSellButton(topSellButton);

   	 OpInterface opi = new OpInterface("INVISIBLE", con);
   	 opi.setVisible(false);
   	 opi.setUpRegisterButton(regButton);

	panel.setLayout(new GridLayout(8, 1));
	panel.add(createTitle());
   	 panel.add(ispButton);
   	 panel.add(opButton);
   	 panel.add(regButton);
   	 panel.add(retButton);
   	 panel.add(topSellButton);
   	 panel.add(dailyButton);
   	 panel.add(shpButton);

   	 // this line terminates the program when the X button
   	 // located at the top right hand corner of the
   	 // window is clicked
   	 addWindowListener(new WindowAdapter() {
   		 public void windowClosing(WindowEvent e) {
   			 System.exit(0);
   		 }
   	 });
    }
private JPanel createTitle() {
   	 JLabel titleLabel = new JLabel();
   	 titleLabel.setText("Welcome User!!");
   	 titleLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 36));
   	 JPanel titlePanel = new JPanel();
   	 titlePanel.add(titleLabel);

   	 return titlePanel;
    }

	private void setUpIspButton(JButton ispButton){
		ispButton.addMouseListener(new MouseAdapter(){
			IPInterface ipi = new IPInterface("In Store Purchase", con);
			//_ipi.setVisible(true);
			
		});
	}



    private void setUpOpButton(JButton opButton) {
   	 opButton.addMouseListener(new MouseAdapter() {
   		 public void mouseClicked(MouseEvent e) {
   			 OpInterface opi = new OpInterface("Online Purchase", con);
   			 opi.pack();
   			 opi.setVisible(true);

   		 }
   	 });
    }

    private void setUpReturnButton(JButton retButton) {
   	 // TODO Auto-generated method stub
   	 retButton.addMouseListener(new MouseAdapter() {
   		 public void mouseClicked(MouseEvent e) {

   			 JDialog jd = new JDialog();
   			 final JPanel panel = new JPanel();
   			 JPanel inputPanel = new JPanel();
   			 final JLabel respondLabel = new JLabel();
   			 jd.setBounds(100, 100, 450, 300);
   			 panel.setLayout(new GridLayout(4, 1));

   			 JLabel instructionLabel = new JLabel(
   					 "Please input the following:");

   			 JLabel upcLabel = new JLabel("UPC:");
   			 inputPanel.add(upcLabel);
   			 final JTextField upcField = new JTextField(3);
   			 inputPanel.add(upcField);

   			 JLabel receiptId = new JLabel("Receipt ID:");
   			 inputPanel.add(receiptId);
   			 final JTextField receiptIdField = new JTextField(3);
   			 inputPanel.add(receiptIdField);

   			 JLabel quantityLabel = new JLabel("Quantity:");
   			 inputPanel.add(quantityLabel);
   			 final JTextField quantityField = new JTextField(3);
   			 inputPanel.add(quantityField);

   			 // final JLabel summary = new JLabel();
   			 // panel.add(summary);
   			 panel.add(instructionLabel);
   			 panel.add(inputPanel);

   			 jd.getContentPane().add(panel);

   			 jd.setVisible(true);

   			 JButton submitButton = new JButton("Submit");
   			 submitButton.addMouseListener(new MouseAdapter() {
   				 public void mouseClicked(MouseEvent e) {
   					 if ((!upcField.getText().equals(""))
   							 && (!receiptIdField.getText().equals(""))
   							 && (!quantityField.getText().equals(""))) {

   						 InsertionTransactions it = new InsertionTransactions(
   								 con);
   						 try {
   							 respondLabel.setText(it.putReturn(upcField
   									 .getText(), receiptIdField.getText(),
   									 Integer.parseInt(quantityField
   											 .getText())));
   							 // System.out.println(it.putReturn(upcLabel.getText(),receiptIdField.getText(),Integer.parseInt(quantityField.getText())));
   							 upcField.setText("");
   							 receiptIdField.setText("");
   							 quantityField.setText("");
   							 // repaint();
   						 } catch (NumberFormatException nfe) {
   							 respondLabel
   									 .setText("That is not a valid number! Please try again.");
   							 upcField.setText("");
   							 receiptIdField.setText("");
   							 quantityField.setText("");
   						 }
   					 } else
   						 respondLabel
   								 .setText("Please input something to all text fields.");

   				 }

   			 });

   			 panel.add(respondLabel);
   			 panel.add(submitButton);
   		 }
   	 });
    }
    private void setUpDailyButton(JButton dailyButton) {
    	// TODO Auto-generated method stub
    	dailyButton.addMouseListener(new MouseAdapter(){
        	public void mouseClicked(MouseEvent e)
        	{

        	JDialog jd = new JDialog();
        	jd.setBounds(100, 100, 100, 100);
        	JPanel p = new JPanel();
        	p.setLayout(new FlowLayout());
      	 
        	JLabel rptDateLabel = new JLabel("Date:");
        	p.add(rptDateLabel);
        	final JTextField rptDate = new JTextField(3);
        	p.add(rptDate);
      	 
      	 
        	final JLabel summary = new JLabel();
        	p.add(summary);
      	 
        	jd.getContentPane().add(p);
      	 
        	jd.setVisible(true);
      	 
        	JButton rptButton = new JButton("Get Report");
        	rptButton.addMouseListener(new MouseAdapter(){
        	public void mouseClicked(MouseEvent e)
        	{
      	 
        	RetreivalTransactions rt = new RetreivalTransactions(con);
       	 
        	try {
       		 ResultSet rpt = rt.getReport(rptDate.getText());
   			 JTable table = new JTable(rt.buildTableModel(rpt));
   			 JScrollPane scrollPane = new JScrollPane(table);
   			 JDialog resultWindow = new JDialog();
   			 resultWindow.setBounds(100, 100, 450, 500);
   			 JPanel resultPanel = new JPanel();
   			 resultPanel.add(scrollPane);
   			 
   			 JLabel total = new JLabel("TOTAL = $" + sumTotal(table));
   			 
   			 resultPanel.add(total);
   			 
   			 resultWindow.setContentPane(resultPanel);
   			 resultWindow.setVisible(true);
   			 
   			 
   			 
    
   		 } catch (SQLException e1) {
   			 // TODO Auto-generated catch block
   			 e1.printStackTrace();
   		 }
    
        	}
       	 
       	 
          	 
        	});
      	 
      	 
        	p.add(rptButton);

        	}
       	});   
	}
   
   private double sumTotal(JTable table){
   double total = 0;
   for (int i = 0; i < table.getModel().getRowCount(); i++)
   total = total + Double.parseDouble(table.getModel().getValueAt(i, 4).toString());
   
   return total;
   }   

private void setUpShpButton(JButton shpButton){
   	shpButton.addMouseListener(new MouseAdapter(){
       	public void mouseClicked(MouseEvent e)
       	{

       	JDialog jd = new JDialog();
       	jd.setBounds(100, 100, 450, 300);
       	JPanel p = new JPanel();
       	p.setLayout(new FlowLayout());
       	jd.setContentPane(p);
      	 
       	JLabel supNameLabel = new JLabel("Supplier Name:");
       	p.add(supNameLabel);
       	JTextField supName = new JTextField(3);
       	p.add(supName);
           	 
       	JButton shpItemButton = new JButton("Insert Shipment");
       	setUpShpItemButton(shpItemButton, supName, jd);
       	p.add(shpItemButton);
      	 
       	jd.setVisible(true);
      	 
      	 
       	}});
   }
    	 

	private void setUpShpItemButton(JButton shpItemButton, final JTextField supName, final JDialog parent){
   	shpItemButton.addMouseListener(new MouseAdapter(){
   	    
       	public void mouseClicked(MouseEvent e)
       	{
       	parent.dispose();   
       	Shipment.insertShipment(con, supName.getText());   
      	 
       	JDialog jd = new JDialog();
       	jd.setBounds(100, 100, 450, 300);
       	JPanel p = new JPanel();
       	p.setLayout(new FlowLayout());
      	 
     	 
       	JLabel upcLabel = new JLabel("UPC:");
       	p.add(upcLabel);
       	final JTextField upc = new JTextField(3);
       	p.add(upc);
     	 
       	JLabel quantityLabel = new JLabel("Quantity:");
       	p.add(quantityLabel);
       	final JTextField quantity = new JTextField(3);
       	p.add(quantity);
     	 
       	JLabel supCostLabel = new JLabel("Cost:");
       	p.add(supCostLabel);
       	final JTextField supCost = new JTextField(3);
       	p.add(supCost);
     	 
       	final JLabel summary = new JLabel();
       	p.add(summary);
     	 
       	jd.getContentPane().add(p);
       	jd.setVisible(true);
      	 
       	JButton nextButton = new JButton("Next Item");
       	final JButton sumButton = new JButton("Shipment Summary");  
     	 
       	nextButton.addMouseListener(new MouseAdapter(){
       	public void mouseClicked(MouseEvent e)
       	{
       	InsertionTransactions it = new InsertionTransactions(con);
              try
       	{
       	if(supCost.getText().length() > 0)
       	it.putShipItem( upc.getText(), Integer.parseInt(quantity.getText()), Double.parseDouble(supCost.getText()));
       	else
       	it.putShipItem( upc.getText(), Integer.parseInt(quantity.getText()), -1);
   		    
       	}
       	catch(NumberFormatException Ne)
       	{
       	 
       	}
	 
       	upc.setText("");
       	quantity.setText("");
       	supCost.setText("");
       	sumButton.addMouseListener(new MouseAdapter(){
       	public void mouseClicked(MouseEvent e)
       	{
       	RetreivalTransactions rt = new RetreivalTransactions(con);
      	 
       	try {
      		 ResultSet rpt = rt.getShipSummary();
   			 JTable table = new JTable(rt.buildTableModel(rpt));
   			 JDialog resultWindow = new JDialog();
   			 resultWindow.setBounds(100, 100, 450, 300);
   			 JPanel resultPanel = new JPanel();
   			 JScrollPane scrollPane = new JScrollPane(table);
   			 resultPanel.add(scrollPane);
   			 resultWindow.setContentPane(resultPanel);
   			 resultWindow.setVisible(true);
   			 
   			 
    
   		 } catch (SQLException e1) {
   			 // TODO Auto-generated catch block
   			 e1.printStackTrace();
   		 }
 
       	}
         	 
       	});
      	 
       	}
         	 
       	});
     	 
     	 
       	p.add(nextButton);
       	p.add(sumButton);

       	}
      	});   
 	 
   }
    private boolean connect(String username, String password) {
   	 String connectURL = "jdbc:oracle:thin:@localhost:1521:ug";

   	 try {
   		 con = DriverManager.getConnection(connectURL, username, password);

   		 System.out.println("\nConnected to Oracle!");
   		 return true;
   	 } catch (SQLException ex) {
   		 System.out.println("Message: " + ex.getMessage());
   		 return false;
   	 }
    }

    public static void main(String[] args) {

   	 MainInterface mainInterface = new MainInterface("Test Frame");
   	 // this line causes the window to be sized to its
   	 // preferred size (this essentially compresses the
   	 // window)
   	 mainInterface.pack();
   	 // Initially, the JFrame is invisible. This line
   	 // makes the window visible.
   	 mainInterface.setVisible(true);

   	 try {
   		 // Load the Oracle JDBC driver
   		 DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
   	 } catch (SQLException ex) {
   		 System.out.println("Message: " + ex.getMessage());
   		 System.exit(-1);
   	 }

   	 if (mainInterface.connect("ora_q3s7", "a59723106")) {
   		 // if the username and password are valid,
   		 // remove the login window and display a text menu
   		 System.out.println("ok");
   		 mainInterface.setVisible(true);
   	 }
    }

}*/