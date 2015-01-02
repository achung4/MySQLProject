import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.lang.NumberFormatException;
import java.awt.GridLayout;
import java.sql.Connection;

import javax.swing.*;

public class TopSellingInterface extends JFrame {
	private JPanel datePanel;
	private JPanel displayPanel;
	private JPanel numberPanel;
	private Connection con;
	private TopSellingItems tsi;// = new TopSellingItems(con);
   
	public TopSellingInterface(final Connection  con){
    	this.con = con;
   	 displayPanel = new JPanel();
    	datePanel = new JPanel();
    	numberPanel = new JPanel();
    	displayPanel.setLayout(new GridLayout(4,1));

    	JDialog jd = new JDialog();
    	jd.setTitle("Top Selling Items Interface");
    	jd.setBounds(100, 100, 300, 300);
   	 
    	JButton submitButton = new JButton("Submit");
    	jd.getContentPane().add(datePanel);   
  	 
    	JLabel dateLabel = new JLabel("Enter date in format YYYY-MM-DD:");
    	final JTextField dateField = new JTextField(10);
    	JLabel numberLabel = new JLabel("Number of items to display: ");
    	final JTextField numberField = new JTextField(3);
    	final JLabel resLabel = new JLabel();
  	 
    	datePanel.add(dateLabel);
    	datePanel.add(dateField);
   	 
    	numberPanel.add(numberLabel);
    	numberPanel.add(numberField);
  	 
   	 
    	submitButton.addMouseListener(new MouseAdapter(){
        	public void mouseClicked(MouseEvent e){
       		 final String res;
       		 final String inputDate = dateField.getText();
            	final String inputNum = numberField.getText();
           	 
       		 if((!inputDate.equals(""))&&(!inputNum.equals(""))){
           		 tsi = new TopSellingItems(con);
           		 try{
           			 res = tsi.printTopSellingItems(inputDate, Integer.parseInt(inputNum));
           			 resLabel.setText(res);
           		 }catch(NumberFormatException nfe){
           			 resLabel.setText("Cannot input letters into number!");
           		 }
       		 }
       		 else
       			 resLabel.setText("Date or number cannot be empty!");
        	}
    	});
    	displayPanel.add(datePanel);
    	displayPanel.add(numberPanel);
    	displayPanel.add(resLabel);
    	displayPanel.add(submitButton);
   	 
    	jd.getContentPane().add(displayPanel);
    	jd.setVisible(true);
	}
   
}
