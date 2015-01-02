// We need to import the java.sql package to use JDBC
import java.sql.*;

// for reading from the command line
import java.io.*;

// for the login window
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/*
 * This class implements a graphical login window and a simple text
 * interface for interacting with the branch table
 */
public class branch implements ActionListener {
    // command line reader
    private BufferedReader in = new BufferedReader(new InputStreamReader(
   		 System.in));

    private Connection con;

    // user is allowed 3 login attempts
    private int loginAttempts = 0;

    // components of the login window
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JFrame mainFrame;

    /*
     * constructs login window and loads JDBC driver
     */
    public branch() {
   	 mainFrame = new JFrame("User Login");

   	 JLabel usernameLabel = new JLabel("Enter username: ");
   	 JLabel passwordLabel = new JLabel("Enter password: ");

   	 usernameField = new JTextField(10);
   	 passwordField = new JPasswordField(10);
   	 passwordField.setEchoChar('*');

   	 JButton loginButton = new JButton("Log In");

   	 JPanel contentPane = new JPanel();
   	 mainFrame.setContentPane(contentPane);

   	 // layout components using the GridBag layout manager

   	 GridBagLayout gb = new GridBagLayout();
   	 GridBagConstraints c = new GridBagConstraints();

   	 contentPane.setLayout(gb);
   	 contentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

   	 // place the username label
   	 c.gridwidth = GridBagConstraints.RELATIVE;
   	 c.insets = new Insets(10, 10, 5, 0);
   	 gb.setConstraints(usernameLabel, c);
   	 contentPane.add(usernameLabel);

   	 // place the text field for the username
   	 c.gridwidth = GridBagConstraints.REMAINDER;
   	 c.insets = new Insets(10, 0, 5, 10);
   	 gb.setConstraints(usernameField, c);
   	 contentPane.add(usernameField);

   	 // place password label
   	 c.gridwidth = GridBagConstraints.RELATIVE;
   	 c.insets = new Insets(0, 10, 10, 0);
   	 gb.setConstraints(passwordLabel, c);
   	 contentPane.add(passwordLabel);

   	 // place the password field
   	 c.gridwidth = GridBagConstraints.REMAINDER;
   	 c.insets = new Insets(0, 0, 10, 10);
   	 gb.setConstraints(passwordField, c);
   	 contentPane.add(passwordField);

   	 // place the login button
   	 c.gridwidth = GridBagConstraints.REMAINDER;
   	 c.insets = new Insets(5, 10, 10, 10);
   	 c.anchor = GridBagConstraints.CENTER;
   	 gb.setConstraints(loginButton, c);
   	 contentPane.add(loginButton);

   	 // register password field and OK button with action event handler
   	 passwordField.addActionListener(this);
   	 loginButton.addActionListener(this);

   	 // anonymous inner class for closing the window
   	 mainFrame.addWindowListener(new WindowAdapter() {
   		 public void windowClosing(WindowEvent e) {
   			 System.exit(0);
   		 }
   	 });

   	 // size the window to obtain a best fit for the components
   	 mainFrame.pack();

   	 // center the frame
   	 Dimension d = mainFrame.getToolkit().getScreenSize();
   	 Rectangle r = mainFrame.getBounds();
   	 mainFrame.setLocation((d.width - r.width) / 2,
   			 (d.height - r.height) / 2);

   	 // make the window visible
   	 mainFrame.setVisible(true);

   	 // place the cursor in the text field for the username
   	 usernameField.requestFocus();

   	 try {
   		 // Load the Oracle JDBC driver
   		 DriverManager.registerDriver(new com.mysql.jdbc.Driver());
   	 } catch (SQLException ex) {
   		 System.out.println("Message: " + ex.getMessage());
   		 System.exit(-1);
   	 }
    }

    /*
     * connects to Oracle database named ug using user supplied username and
     * password
     */
    private boolean connect(String username, String password) {
   	 String connectURL = ""; //"jdbc:oracle:thin:@----:----:--";

   	 try {
   		 con = DriverManager.getConnection(connectURL, username, password);

   		 System.out.println("\nConnected to Oracle!");
   		 return true;
   	 } catch (SQLException ex) {
   		 System.out.println("Message: " + ex.getMessage());
   		 return false;
   	 }
    }

    /*
     * event handler for login window
     */
    public void actionPerformed(ActionEvent e) {
   	 if (connect(usernameField.getText(),
   			 String.valueOf(passwordField.getPassword()))) {
   		 // if the username and password are valid,
   		 // remove the login window and display a text menu
   		 mainFrame.dispose();
   		 showMenu();
   	 } else {
   		 loginAttempts++;

   		 if (loginAttempts >= 3) {
   			 mainFrame.dispose();
   			 System.exit(-1);
   		 } else {
   			 // clear the password
   			 passwordField.setText("");
   		 }
   	 }

    }

    /*
     * displays simple text interface
     */
    void showMenu() {
   	 int choice;
   	 boolean quit;

   	 quit = false;

   	 try {
   		 // disable auto commit mode
   		 con.setAutoCommit(false);

   		 while (!quit) {
   			 System.out.print("\n\nPlease choose one of the following: \n");
   			 System.out.print("1. In-store purchase\n");
   			 System.out.print("2. Online purchase\n");
   			 System.out.print("3. Registration\n");
   			 System.out.print("4. Return\n");
   			 System.out.print("5. Report: top-selling items\n");
   			 System.out.print("6. Daily sales report\n");
   			 System.out.print("7. Process shipment\n");
   			 System.out.print("8. Quit\n>> ");

   			 choice = Integer.parseInt(in.readLine());

   			 System.out.println(" ");

   			 switch (choice) {
   			 case 1:
   				 instorePurchase(this);
   				 break;
   			 case 2:
   				 purchaseOnline(this);
   				 break;
   			 case 3:
   				 registration(this);
   				 break;
   			 case 4:
   				 instoreReturn(this);
   				 break;
   			 case 5:
   				 topSelling(this);
   				 break;
   			 case 6:
   				 dailySalesReport(this);
   				 break;
   			 case 7:
   				 processShipment(this);
   				 break;
   			 case 8:
   				 quit = true;
   			 }
   		 }

   		 con.close();
   		 in.close();
   		 System.out.println("\nGood Bye!\n\n");
   		 System.exit(0);
   	 } catch (IOException e) {
   		 System.out.println("IOException!");

   		 try {
   			 con.close();
   			 System.exit(-1);
   		 } catch (SQLException ex) {
   			 System.out.println("Message: " + ex.getMessage());
   		 }
   	 } catch (SQLException ex) {
   		 System.out.println("Message: " + ex.getMessage());
   	 }
    }


   
	private void purchaseOnline(branch b) {
		OnlineTransaction t = new OnlineTransaction(con, b);
		t.startTransaction();
	}

	private void processShipment(branch b) {
		// TODO Auto-generated method stub
		InsertionTransactions i = new InsertionTransactions(con);
		//i.processShipment();

	}

	private void dailySalesReport(branch b) {
		RetreivalTransactions rt = new RetreivalTransactions(con);
		//rt.displayReport();

	}

	private void instoreReturn(branch b) {
		// TODO Auto-generated method stub
		InsertionTransactions i = new InsertionTransactions(con);
		i.processReturn();
	}

	private void registration(branch b) throws IOException {
		OnlineTransaction ot = new OnlineTransaction(con, b);
		ot.register();
	}

	private void topSelling(branch b) {
		TopSellingItems tsi = new TopSellingItems(con);
		tsi.printTopSellingItems("", 5);

	}

	private void instorePurchase(branch b) {
		inStorePurchase isp = new inStorePurchase(con);
		isp.StartInStorePurchase();
	}


    	public void displayEnumTable(ResultSet rs) throws SQLException {
		ResultSetMetaData meta = rs.getMetaData();
		int arity = meta.getColumnCount();
		String categories[] = new String[arity];
		int index = 0;
		String indexString;

		System.out.println();
		System.out.printf("     ");
		for (int i = 0; i < arity; i++) {
			categories[i] = meta.getColumnLabel(i + 1);
			String currentColumn = meta.getColumnLabel(i + 1);
			System.out.printf("%-20s", currentColumn);
		}
		while (rs.next()) {
			index++;
			indexString = Integer.toString(index);
			System.out.println();
			System.out.printf("%3s. ", indexString);
			for (int c = 0; c < arity; c++) {
				String currentAttribute = rs.getString(categories[c]);
				System.out.printf("%-20s", currentAttribute);
			}
		}
	}

/*
    public static void main(String args[]) {
   	 branch b = new branch();
    }
    
    // TODO Auto-generated method stub
    /*- Instore/Online purchase needs to decrement quantity
    - Implement receiptCounter for in-store purchase:
	ps = con.prepareStatement("INSERT INTO Purchase VALUES(receiptCounter.nextVal, CURRENT_TIMESTAMP, ?, ?, ?, CURRENT_TIMESTAMP + interval '5' day, CURRENT_TIMESTAMP + interval '1' day)");

	ps.setString(1, cid);
	ps.setString(2, cardNo);
	ps.setString(3, expiryDate);
    
	- Make sure change type from STRING to INTEGER for receiptID
	- Use CURRENT_TIMESTAMP for dates
	- Use Cash as a STRING for in-store purchases*/
}
