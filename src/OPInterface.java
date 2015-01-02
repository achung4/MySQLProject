// TODO Can't purchase same items
// TODO Dynamic virtual cart table update
// TODO 

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

class OpInterface extends JFrame {
	JPanel panel;
	private Connection con;
	public ArrayList<CartItem> wishList = new ArrayList<CartItem>();
	JTabbedPane tabbedPane = new JTabbedPane();
	String query = new String(
			"SELECT i.upc, i.title, i.category, i.quantity, l.name, i.sellprice FROM Item I, LeadSinger L WHERE i.upc=l.upc AND i.quantity > 10000000");
	int quantity = 1;
	DecimalFormat money = new DecimalFormat("$0.00");
	String cid = "0000";

	public OpInterface(String title, Connection con) {
		// call the superclass constructor
		super(title);

		// Pass on the connection
		this.con = con;

		panel = new JPanel();
		panel.setLayout(new GridLayout(2, 1));
		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		// this line adds the panel to the
		// Frame's content pane
		getContentPane().add(panel);
		// another way of doing the above is
		// setContentPane(panel);
		JButton loginButton = new JButton("Login");
		JButton registerButton = new JButton("Register");

		loginButton.setPreferredSize(new Dimension(250, 30));
		registerButton.setPreferredSize(new Dimension(250, 30));

		setUpLoginButton(loginButton);
		setUpRegisterButton(registerButton);

		panel.add(loginButton);
		panel.add(registerButton);
	}

	private void setUpLoginButton(JButton loginButton) {

		loginButton.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {

				final JPanel p = new JPanel(new GridLayout(7, 1));
				final JButton confirmButton = new JButton("Confirm");
				p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

				JLabel cidL = new JLabel(
						"Please enter your unique 4-char CID: ");
				JLabel passwordL = new JLabel("Please enter your password: ");
				JLabel spaceL = new JLabel(" ");
				final JLabel summaryL = new JLabel("Please enter the fields");
				final JTextField cidTF = new JTextField(4);
				final JTextField passwordTF = new JTextField(8);
				cidL.setLabelFor(cidTF);
				passwordL.setLabelFor(passwordTF);

				final JDialog jd = new JDialog(new JFrame(), "Please log in...");
				jd.setBounds(350, 250, 400, 250);
				jd.getContentPane().add(p);
				jd.setVisible(true);

				p.add(cidL);
				p.add(cidTF);
				p.add(passwordL);
				p.add(passwordTF);
				p.add(spaceL);
				p.add(confirmButton);
				p.add(summaryL);

				confirmButton.addMouseListener(new MouseAdapter() {
					public void mouseClicked(MouseEvent e) {
						boolean successfulLogin = false;
						PreparedStatement ps;
						ResultSet rs;
						int rowNumber = 0;
						cid = cidTF.getText();
						String password = passwordTF.getText();
						String name = new String("DEFAULT");

						try {
							con.setAutoCommit(false);
							ps = con.prepareStatement("SELECT * FROM Customer WHERE cid = ? AND password = ?");
							ps.setString(1, cid);
							ps.setString(2, password);

							rs = ps.executeQuery();

							// commit work
							con.commit();

							while (rs.next()) {
								name = rs.getString(3);
								rowNumber++;
							}

							ps.close();

							if (rowNumber == 1) {
								successfulLogin = true;
							} else {
								summaryL.setText("Wrong login, please try again");
								successfulLogin = false;
							}

						} catch (SQLException ex) {
							System.out.println("Message: " + ex.getMessage());
							try {
								// undo the insert
								con.rollback();
							} catch (SQLException ex2) {
								System.out.println("Message: "
										+ ex2.getMessage());
								System.exit(-1);
							}
						}

						if (successfulLogin) {
							summaryL.setText("Login successful!");
							jd.setVisible(false);
							customerViewGUI(name);
						} else {
							summaryL.setText("Wrong login, please try again");
						}
					}

				});

			}
		});
	}

	public void setUpRegisterButton(JButton registerButton) {

		registerButton.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {

				final JPanel p = new JPanel(new GridLayout(16, 1));
				p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

				JLabel instructionL = new JLabel(
						"Please choose a customer ID, the following customer IDs are taken:");
				JLabel cidL = new JLabel("Please enter your desired CID: ");
				JLabel passwordL = new JLabel(
						"Please enter your desired password: ");
				JLabel nameL = new JLabel("Please enter your name: ");
				JLabel addressL = new JLabel("Please enter your address: ");
				JLabel phoneL = new JLabel("Please enter your phone: ");
				JLabel spaceL = new JLabel(" ");
				final JLabel summaryL = new JLabel(
						"Please complete the fields and press confirm.");

				final JTextField cidTF = new JTextField(4);
				final JTextField passwordTF = new JTextField(8);
				final JTextField nameTF = new JTextField(20);
				final JTextField addressTF = new JTextField(20);
				final JTextField phoneTF = new JTextField(20);
				final JButton confirmButton = new JButton("Confirm");

				final JDialog jd = new JDialog();
				jd.setBounds(350, 0, 500, 550);
				jd.getContentPane().add(p);
				jd.setVisible(true);

				p.add(instructionL);
				p.add(cidL);
				p.add(cidTF);
				p.add(passwordL);
				p.add(passwordTF);
				p.add(nameL);
				p.add(nameTF);
				p.add(addressL);
				p.add(addressTF);
				p.add(phoneL);
				p.add(phoneTF);
				p.add(spaceL);
				p.add(confirmButton);
				p.add(summaryL);

				confirmButton.addMouseListener(new MouseAdapter() {
					public void mouseClicked(MouseEvent e) {

						cid = cidTF.getText();
						String password = passwordTF.getText();
						String name = nameTF.getText();
						String address = addressTF.getText();
						String phone = phoneTF.getText();

						PreparedStatement ps;
						Statement stmt;
						ResultSet rs, rs_id;
						ArrayList<String> ids = new ArrayList<String>();
						boolean correctFormat = false, cidAvailable = false;

						ids.clear();
						try {
							con.setAutoCommit(false);
							stmt = con.createStatement();
							rs_id = stmt
									.executeQuery("SELECT cid FROM Customer");

							// put all existing cid into arraylist
							while (rs_id.next()) {
								ids.add(rs_id.getString(1));
							}

							stmt.close();

						} catch (SQLException ex) {
							System.out.println("Message: " + ex.getMessage());
						}

						// Checks for correct format and cid availability
						if (!correctFormat || !cidAvailable) {
							if (!ids.contains(cid)) {
								cidAvailable = true;
							} else {
								cidAvailable = false;
								JFrame frame = new JFrame("Error!");
								JOptionPane
										.showMessageDialog(frame,
												"This CID is not available, please try again.");
							}

							if (cid.length() == 4) {
								correctFormat = true;
							} else {
								correctFormat = false;
								JFrame frame = new JFrame("Error!");
								JOptionPane
										.showMessageDialog(frame,
												"Incorrect format. We're looking for a 4-character id.");
							}
						}

						if (correctFormat && cidAvailable) {

							try {
								con.setAutoCommit(false);
								ps = con.prepareStatement("INSERT INTO Customer VALUES (?,?,?,?,?)");

								ps.setString(1, cid);
								ps.setString(2, password);
								ps.setString(3, name);
								ps.setString(4, address);
								ps.setString(5, phone);

								ps.executeUpdate();

								// commit work
								con.commit();

								ps.close();

							} catch (SQLException ex) {
								System.out.println("Message: "
										+ ex.getMessage());
								try {
									con.rollback();
								} catch (SQLException ex2) {
									System.out.println("Message: "
											+ ex2.getMessage());
									System.exit(-1);
								}
							}

							JFrame frame = new JFrame("Success!");
							JOptionPane
									.showMessageDialog(frame,
											"You have successfully registered. Thank you.");
							jd.dispose();

						}
					}
				});
			}
		});
	}

	private void customerViewGUI(String name) {

		// DEFINE MAIN CUSTOMER PANEL HERE
		final JPanel mainCustomerPanel = new JPanel(new GridLayout(10, 1));
		mainCustomerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10,
				10));

		JLabel titleL = new JLabel(
				"Please enter the item title, leave it blank to check everything.");
		JLabel categoryL = new JLabel(
				"Please enter the item category, leave it blank to check everything.");
		JLabel artistL = new JLabel(
				"Please enter the artist name, leave it blank to check everything.");
		JLabel quantityL = new JLabel(
				"Please enter the quantity, leave it blank to default to 1.");
		JLabel spaceL = new JLabel(" ");

		final JTextField titleTF = new JTextField(20);
		final JTextField categoryTF = new JTextField(20);
		final JTextField artistTF = new JTextField(20);
		final JTextField quantityTF = new JTextField(20);
		final JButton searchButton = new JButton("Search");
		// END MAIN CUSTOMER PANEL HERE

		// DEFINE VIRTUAL CART PANEL HERE
		final JPanel virtualCartPanel = new JPanel(new FlowLayout());
		mainCustomerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10,
				10));
		final JLabel virtualCartTitle = new JLabel(
				"You currently have nothing in your virtual cart.");
		final JLabel virtualCartTitle2 = new JLabel(
				"Please search for an item.");
		virtualCartPanel.add(virtualCartTitle);
		virtualCartPanel.add(virtualCartTitle2);
		// END VIRTUAL CART PANEL HERE

		// DEFINE QUERY PANEL HERE

		final JPanel resultPanel = new JPanel();
		resultPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		resultPanel.setLayout(new FlowLayout());

		final JLabel queryTitleL = new JLabel(
				"Please to go the Search tab and enter a query first.");
		resultPanel.add(queryTitleL);
		// END QUERY PANEL HERE

		tabbedPane.addTab("Virtual Cart", virtualCartPanel);
		tabbedPane.addTab("Search", mainCustomerPanel);
		tabbedPane.addTab("Query", resultPanel);

		final JDialog mainCustomerJD = new JDialog(new JFrame(), "Welcome, "
				+ name.trim() + "!");
		mainCustomerJD.setBounds(350, 0, 500, 550);
		mainCustomerJD.getContentPane().add(tabbedPane);
		mainCustomerJD.setVisible(true);

		mainCustomerPanel.add(titleL);
		mainCustomerPanel.add(titleTF);
		mainCustomerPanel.add(categoryL);
		mainCustomerPanel.add(categoryTF);
		mainCustomerPanel.add(artistL);
		mainCustomerPanel.add(artistTF);
		mainCustomerPanel.add(quantityL);
		mainCustomerPanel.add(quantityTF);
		mainCustomerPanel.add(spaceL);
		mainCustomerPanel.add(searchButton);

		searchButton.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				String query = "SELECT i.upc, i.title, i.category, i.quantity, l.name, i.sellprice FROM Item I, LeadSinger L WHERE i.upc=l.upc AND i.quantity > 0";

				String title = titleTF.getText();
				String category = categoryTF.getText();
				String artist = artistTF.getText();
				String quantityString = quantityTF.getText();

				if (!title.isEmpty()) {
					query = query.concat(" AND i.title = '" + title + "'");
				}
				if (!category.isEmpty()) {
					query = query
							.concat(" AND i.category = '" + category + "'");
				}
				if (!artist.isEmpty()) {
					query = query.concat(" AND l.name = '" + artist + "'");
				}
				if (!quantityString.isEmpty()) {
					try {
						quantity = Integer.parseInt(quantityString);
					} catch (java.lang.NumberFormatException ex) {
						JFrame frame = new JFrame("Error!");
						JOptionPane.showMessageDialog(frame,
								"You need to enter a NUMBER for quantity");
					}
				}

				try {
					tabbedPane.setSelectedIndex(2);
				} catch (IndexOutOfBoundsException ie) {
					tabbedPane.setSelectedIndex(0);
					JFrame frame = new JFrame("Error!");
					JOptionPane.showMessageDialog(frame,
							"Tried to select invalid pane!");
				}

				queryTitleL
						.setText("Please select the desired item, then press confirm.");

				try {

					Statement cartStmt;
					ResultSet cartRs;
					final JButton confirmButton = new JButton("Confirm");
					con.setAutoCommit(false);
					cartStmt = con.createStatement();
					cartRs = cartStmt.executeQuery(query);

					con.commit();

					RetreivalTransactions rt = new RetreivalTransactions(con);
					final DefaultTableModel t = rt.buildTableModel(cartRs);
					final JTable table = new JTable(t);
					JScrollPane scrollPane = new JScrollPane(table);

					resultPanel.add(scrollPane);
					resultPanel.add(confirmButton);

					confirmButton.addMouseListener(new MouseAdapter() {
						public void mouseClicked(MouseEvent e) {
							int[] rowIndices = table.getSelectedRows();
							if (rowIndices.length > 0) {
								for (int c = 0; c < rowIndices.length; c++)
									if (Integer.parseInt(table.getModel()
											.getValueAt(rowIndices[c], 3)
											.toString()) < quantity) {
										int result = JOptionPane
												.showConfirmDialog(
														(Component) null,
														"You're trying to order more items than the store has, will you take everything the store has instead?",
														"alert",
														JOptionPane.OK_CANCEL_OPTION);
										if (result == 1) {
											wishList.add(new CartItem(
													(String) table
															.getModel()
															.getValueAt(
																	rowIndices[c],
																	0),
													(String) table
															.getModel()
															.getValueAt(
																	rowIndices[c],
																	1),
													Integer.parseInt(table
															.getModel()
															.getValueAt(
																	rowIndices[c],
																	3)
															.toString()),
													Double.parseDouble(table
															.getModel()
															.getValueAt(
																	rowIndices[c],
																	5)
															.toString())));
										} else {
											break;
										}
									} else {
										wishList.add(new CartItem(
												(String) table.getModel()
														.getValueAt(
																rowIndices[c],
																0),
												(String) table.getModel()
														.getValueAt(
																rowIndices[c],
																1), quantity,
												Double.parseDouble(table
														.getModel()
														.getValueAt(
																rowIndices[c],
																5).toString())));
									}
								if (rowIndices.length == 1) {
									JOptionPane
											.showMessageDialog(new JFrame(
													"Success!"),
													"You've added an item to your virtual cart");
								}

								if (rowIndices.length > 1) {
									JOptionPane.showMessageDialog(new JFrame(
											"Success!"), "You've added "
											+ rowIndices.length
											+ " items to your virtual cart");
								}

								tabbedPane.setSelectedIndex(0);
								final JButton commitButton = new JButton(
										"Commit Purchases");
								DefaultTableModel virtualCartTable = wishListToTable(wishList);
								final JTable table = new JTable(
										virtualCartTable);
								JScrollPane scrollPane = new JScrollPane(table);

								// Calculate grand total
								double total = 0;
								for (CartItem c : wishList) {
									total = total + c.quantity * c.price;
								}
								final JLabel grandTotal = new JLabel(
										"Grand total = " + money.format(total));

								virtualCartPanel.add(scrollPane);
								virtualCartTitle
										.setText("This is what you have in your cart so far:");
								virtualCartTitle2
										.setText("                                          ");
								virtualCartPanel.add(grandTotal);
								virtualCartPanel.add(commitButton);

								commitButton
										.addMouseListener(new MouseAdapter() {
											public void mouseClicked(
													MouseEvent e) {

												final JPanel p = new JPanel(
														new GridLayout(8, 1));
												p.setBorder(BorderFactory
														.createEmptyBorder(10,
																10, 10, 10));

												JLabel instructionL = new JLabel(
														"You have chosen to commit all the items in your virtual cart.");
												JLabel ccL = new JLabel(
														"Please enter your credit card number:");
												JLabel expirationL = new JLabel(
														"Please enter your your credit card's expiry date");
												final JLabel summaryL = new JLabel(
														"Please complete the fields and press confirm.");
												JLabel spaceL = new JLabel(" ");

												final JTextField ccTF = new JTextField(
														20);
												final JTextField expirationTF = new JTextField(
														20);
												final JButton confirmButton = new JButton(
														"Confirm");

												final JDialog jd = new JDialog(
														new JFrame(),
														"Enter your credentials");
												jd.setBounds(350, 0, 500, 300);
												jd.getContentPane().add(p);
												jd.setVisible(true);

												p.add(instructionL);
												p.add(ccL);
												p.add(ccTF);
												p.add(expirationL);
												p.add(expirationTF);
												p.add(spaceL);
												p.add(confirmButton);
												p.add(summaryL);

												confirmButton
														.addMouseListener(new MouseAdapter() {
															public void mouseClicked(
																	MouseEvent e) {

																tabbedPane
																		.removeAll();
																mainCustomerJD
																		.dispose();

																String cc = ccTF
																		.getText();
																String expiration = expirationTF
																		.getText();

																int receiptID = 0;
																PreparedStatement ps;
																Statement stmt;
																ResultSet rs;
																Long time = System
																		.currentTimeMillis();
																java.sql.Date now = new java.sql.Date(
																		time);
																Date[] deliveryAndExpectedDates = deliveryAndExpected(now);

																summaryL.setText("Processing payment...");

																// Make the
																// Purchase data
																// point
																try {
																	con.setAutoCommit(false);
																	ps = con.prepareStatement("INSERT INTO Purchase VALUES(receiptCounter.nextVal, CURRENT_TIMESTAMP, ?, ?, ?, ?, ?)");

																	ps.setString(
																			1,
																			cid);
																	ps.setString(
																			2,
																			cc);
																	ps.setString(
																			3,
																			expiration);
																	ps.setDate(
																			4,
																			deliveryAndExpectedDates[1]);
																	ps.setDate(
																			5,
																			deliveryAndExpectedDates[0]);

																	ps.executeUpdate();

																	// commit
																	// work
																	con.commit();
																	ps.close();

																	summaryL.setText("Credit card authenticated!\nProceeding to committing your purchases...\n");

																} catch (SQLException ex) {
																	System.out
																			.println("Message: "
																					+ ex.getMessage());
																	System.out.println("Checj catch");
																	try {
																		// undo
																		// the
																		// insert
																		con.rollback();
																	} catch (SQLException ex2) {
																		System.out
																				.println("Message: "
																						+ ex2.getMessage());
																		System.out.println("Checj c2");
																		System.exit(-1);
																		

																	}
																}

																// Retrieve
																// ReceiptID
																try {
																	stmt = con
																			.createStatement();
																	rs = stmt
																			.executeQuery("SELECT max(receiptID) FROM Purchase");

																	while (rs
																			.next()) {
																		receiptID = Integer
																				.parseInt(rs
																						.getString(1));
																	}

																	summaryL.setText("receiptID retrieval Successful, receiptID = "
																			+ receiptID);
																	// close the
																	// statement;
																	// the
																	// ResultSet
																	// will also
																	// be closed
																	stmt.close();
																} catch (SQLException ex) {
																	System.out
																			.println("Message: "
																					+ ex.getMessage());
																}

																// Register all
																// the items in
																// virtual cart
																try {
																	for (CartItem c : wishList) {
																		con.setAutoCommit(false);
																		ps = con.prepareStatement("INSERT INTO PurchaseItem VALUES (?,?,?)");

																		ps.setInt(
																				1,
																				receiptID);
																		ps.setString(
																				2,
																				c.upc);
																		ps.setInt(
																				3,
																				c.quantity);

																		ps.executeUpdate();

																		summaryL.setText("Added UPC#"
																				+ c.upc);

																		// commit
																		// work
																		con.commit();

																		ps.close();

																		con.setAutoCommit(false);
																		ps = con.prepareStatement("UPDATE Item SET quantity = quantity - ? WHERE Item.upc = ?");
																		ps.setInt(
																				1,
																				c.quantity);
																		ps.setString(
																				2,
																				c.upc);
																		ps.executeUpdate();
																		con.commit();
																		ps.close();
																	}
																} catch (SQLException ex) {
																	System.out
																			.println("Message: "
																					+ ex.getMessage());
																	try {
																		// undo
																		// the
																		// insert
																		con.rollback();
																	} catch (SQLException ex2) {
																		System.out
																				.println("Message: "
																						+ ex2.getMessage());
																		System.exit(-1);
																	}
																}

																// ON FINISH
																wishList.clear();
																jd.dispose();
															}
														});
											}
										});
							} else {
								JFrame frame = new JFrame("Error!");
								JOptionPane
										.showMessageDialog(frame,
												"You need to select a row from the table");
							}
						}

					});

					cartStmt.close();

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
			}
		});

	}

	private DefaultTableModel wishListToTable(ArrayList<CartItem> wishList) {

		// names of columns
		Vector<String> columnNames = new Vector<String>();
		columnNames.add("UPC");
		columnNames.add("Title");
		columnNames.add("Quantity");
		columnNames.add("Price");
		columnNames.add("Total");

		// data of the table
		Vector<Vector<Object>> data = new Vector<Vector<Object>>();

		for (CartItem c : wishList) {
			Vector<Object> vector = new Vector<Object>();
			for (int columnIndex = 0; columnIndex < 5; columnIndex++) {
				vector.add(c.upc);
				vector.add(c.title);
				vector.add(c.quantity);
				vector.add(money.format(c.price));
				vector.add(money.format(c.quantity * c.price));
			}
			data.add(vector);
		}

		return new DefaultTableModel(data, columnNames);
	}

	// Returns an array consisting of the delivery date and expected date
	// Only 10 deliveries are allowed per day.
	public Date[] deliveryAndExpected(Date d) {
		ResultSet rs;
		int deliveryNumber = 0, limit = 3;
		PreparedStatement ps;
		Date[] result = new Date[2];

		Date deliveryDate = d;
		Date deliveryDateAddOne = new Date(deliveryDate.getTime() + 86400000);
		Date expected = new Date(deliveryDate.getTime() + 86400000 * 5);

		result[0] = deliveryDate;
		result[1] = expected;

		try {
			con.setAutoCommit(false);
			ps = con.prepareStatement("select count(*) FROM Purchase WHERE deliveredDate > ? AND deliveredDate <= ?");
			ps.setDate(1, deliveryDate);
			ps.setDate(2, deliveryDateAddOne);

			rs = ps.executeQuery();
			while (rs.next()) {
				deliveryNumber = Integer.parseInt(rs.getString(1));
			}
			con.commit();

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

		if (deliveryNumber < limit) {

			JOptionPane
					.showMessageDialog(
							new JFrame(
									"Checking if your purchases can be delivered on "
											+ deliveryDate + "..."),
							"On "
									+ deliveryDate
									+ ", there are currently "
									+ deliveryNumber
									+ " deliveries. Therefore, your purchase can be delivered on that date. You can expect your purchase to arrive on "
									+ expected + ".");

			return result;
		} else {
			JOptionPane
					.showMessageDialog(
							new JFrame(
									"Checking if your purchases can be delivered on "
											+ deliveryDate + "..."),
							"On "
									+ deliveryDate
									+ ", there are currently "
									+ deliveryNumber
									+ " deliveries. Therefore, your purchase CANNOT be delivered on that date (MAX = "
									+ limit + ")...");

			result = deliveryAndExpected(deliveryDateAddOne);
			return result;
		}
	}
}