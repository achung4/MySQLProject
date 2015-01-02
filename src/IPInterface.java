import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.*;

public class IPInterface  extends JFrame{
	JPanel panel;
	private Connection con;
	private branch uselessBranch;
	private inStorePurchase isp = new inStorePurchase(con);
	private JDialog jd = new JDialog();
	private ArrayList<PurchaseItem> purItms = new ArrayList<PurchaseItem>();
	
	
	public IPInterface(String title, Connection connection){
		super(title);
		con = connection;
		panel = new JPanel();
		panel.setLayout(new GridLayout(2,1));
		
		jd.setBounds(100, 100, 450, 300);
		
		
		getContentPane().add(panel);
		
		JButton addItemsButton = new JButton("Add items to purchase");
		JButton goBackButton = new JButton("Go back to main menu");
		
		
		addItemsButton.setPreferredSize(new Dimension(250, 30));
		goBackButton.setPreferredSize(new Dimension(250, 30));
		
		setUpAddItemsButton(addItemsButton);
		goBackButton.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e){
				jd.setVisible(false);
			}
		});

		
		panel.add(addItemsButton);
		panel.add(goBackButton);
		
		jd.getContentPane().add(panel);
		jd.setVisible(true);
		
		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				System.exit(0);
			}
		});
	
	}
	
	//TODO: add expire date for card field
	private void setUpAddItemsButton(JButton addItemsButton){
		addItemsButton.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e){
				JPanel p = new JPanel();
				final ArrayList<PurchaseItem> purItms = new ArrayList<PurchaseItem>();

				//use grid layout
				final JDialog jd_addItems = new JDialog();
				jd_addItems.setBounds(550, 200, 450, 300);
				
				getContentPane().add(p);
				
				JButton addMoreButton = new JButton("Add more");
				JButton submitButton = new JButton("Submit");
				JButton cancelButton = new JButton("Cancel");// OR JUST CLOSE THE WINDOW
				JButton completeButton = new JButton ("Complete");
				
				JLabel upc_label = new JLabel("UPC: ");
				final JTextField upcTF = new JTextField(10);
				JLabel quant_label = new JLabel("Quantity: ");
				final JTextField quantF = new JTextField(10);
				
				final JLabel message_label = new JLabel("");
				
				JLabel total_label = new JLabel("Your grand total is: $");
				final JTextField totalTF = new JTextField(10);
				totalTF.setEditable(false);
				
				CheckboxGroup group = new CheckboxGroup();
				
				final Checkbox cashCB = new Checkbox("Cash", group, true);
				final Checkbox creditCardCB = new Checkbox("Credit Card", group, false);
				
				final JTextField cardNoTF = new JTextField (16);
				
				
				upc_label.setLabelFor(upcTF);
				quant_label.setLabelFor(quantF); 
				
				p.add(upc_label);
				p.add(upcTF);
				p.add(quant_label);
				p.add(quantF);
				p.add(message_label);
				
				addMoreButton.addMouseListener(new MouseAdapter(){
					public void mouseClicked(MouseEvent e){
						
						String upc = upcTF.getText();
						String quantity = quantF.getText();
						if((!upc.equals(""))&&(!quantity.equals(""))){
							System.out.println("UPC : " + upc + " Quantity : " + Integer.valueOf(quantity));		
							inStorePurchase isp = new inStorePurchase(con);
							if(isp.addAnItemGUI(upc)){
								System.out.println("YAY it worked");
								PurchaseItem pi = new PurchaseItem(con);
								pi.setQuantity(Integer.valueOf(quantity));
								pi.setUPC(upc);
								purItms.add(pi);
								quantF.setText("");
								upcTF.setText("");
							}
						else{
							upcTF.setText("");
							quantF.setText("");
							message_label.setText("No item with upc " + upc + "exists. Enter another value");
						}	
					}
				}
				});
				
				submitButton.addMouseListener(new MouseAdapter(){
					public void mouseClicked(MouseEvent e){
						PurchaseItem pi = new PurchaseItem(con);
						inStorePurchase isp = new inStorePurchase(con);
						String upc = upcTF.getText();
						String quantity = quantF.getText();
						double totalPrice;
	
						System.out.println("UPC : " + upc + " Quantity : " + quantity);
						if((!upc.equals(""))&&(!quantity.equals(""))){
							if(isp.addAnItemGUI(upc)){
								System.out.println("YAY it worked");
							
								pi.setQuantity(Integer.valueOf(quantity));
								pi.setUPC(upc);
								purItms.add(pi);
								quantF.setText("");
								upcTF.setText("");
						}
							else{
								upcTF.setText("");
								quantF.setText("");
								message_label.setText("No item with upc " + upc + "exists");
						}
						totalPrice = isp.calculatePriceGUI(purItms);
						totalTF.setText(String.valueOf(totalPrice));
						}
					}
				});
				
				cancelButton.addMouseListener(new MouseAdapter(){
					public void mouseClicked(MouseEvent e){
						jd_addItems.setVisible(false);
					}
				});
				
				//TODO: must check that fields are empty! or null
				completeButton.addMouseListener(new MouseAdapter(){
					public void mouseClicked(MouseEvent e){
						inStorePurchase isp = new inStorePurchase(con);
						String cardNo, expire;
						String cid = "stor";
						if(cashCB.getState()){
							expire = null;
							cardNo = null;
						}
						else{
							cardNo = cardNoTF.getText();
							expire = "dexp";
							
						}
						try{
						isp.registerPurchaseGUI(purItms, cardNo, cid, expire);
						jd_addItems.setVisible(false);
						}catch(InvalidCardException e1){
							final JDialog jd_exception = new JDialog();
							jd_exception.setBounds(100, 100, 450, 300);
							JPanel excepP = new JPanel();
							JLabel exepMessage = new JLabel("Error : Credit card declined");
							JLabel instructMessage = new JLabel("Continue purchase, and select cash or cancel the purchase");
							excepP.add(exepMessage);
							excepP.add(instructMessage);
							jd_exception.getContentPane().add(excepP);
							jd_exception.setVisible(true);		
	
						}
					
					}
				});
				
				p.add(addMoreButton);
				p.add(submitButton);
				
				p.add(total_label);
				p.add(totalTF);
				
				p.add(cashCB);
				p.add(creditCardCB);
				p.add(cardNoTF);
				
				p.add(cancelButton);
				p.add(completeButton);
				
				jd_addItems.getContentPane().add(p);
				jd_addItems.setVisible(true);
				
				jd.setVisible(true);
				
				addWindowListener(new WindowAdapter(){
					public void windowClosing(WindowEvent e){
						System.exit(0);
					}
				});
				
				
			}
		});
	}


}
