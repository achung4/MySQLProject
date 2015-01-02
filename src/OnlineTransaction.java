/* TODO System - Delivery Days, cannot purchase nothing;
System will inform the customer after commit the number of days
it will take to receive the goods (estimated based on outstanding/max orders).
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.sql.Date;

public class OnlineTransaction {

	public Connection con;
	public branch b;
	public BufferedReader in = new BufferedReader(new InputStreamReader(
			System.in));

	public OnlineTransaction(Connection c, branch br) {
		con = c;
		b = br;
	}

	public void startTransaction() {
		welcome();
	}

	public void welcome() {
		int choice;
		boolean quit = false;

		try {
			// disable auto commit mode
			con.setAutoCommit(false);

			while (!quit) {
				System.out
						.print("\n\nYou wanted to make an online transaction? \n");
				System.out.print("1. Login\n");
				System.out.print("2. Register\n");
				System.out.print("3. Back\n>> ");

				choice = Integer.parseInt(in.readLine());

				System.out.println(" ");

				switch (choice) {
				case 1:
					login();
					break;
				case 2:
					register();
					break;
				case 3:
					b.showMenu();
				default:
					System.out.println("Wrong format, please try again.");
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

	public void register() throws IOException {
		String cid = "CHECK YOUR CID";
		String password, name, address, phone;
		PreparedStatement ps;
		Statement stmt;
		ResultSet rs, rs_id;
		ArrayList<String> ids = new ArrayList<String>();
		boolean correctFormat = false, cidAvailable = false;

		System.out
				.print("\nPlease choose a customer ID, the following customer IDs are taken:\n");
		try {
			con.setAutoCommit(false);
			stmt = con.createStatement();
			rs = stmt.executeQuery("SELECT cid FROM Customer");
			b.displayEnumTable(rs);
			stmt.close();
		} catch (SQLException ex) {
			System.out.println("Message: " + ex.getMessage());
		}

		ids.clear();
		try {
			con.setAutoCommit(false);
			stmt = con.createStatement();
			rs_id = stmt.executeQuery("SELECT cid FROM Customer");

			// put all existing cid into arraylist
			while (rs_id.next()) {
				ids.add(rs_id.getString(1));
			}

			stmt.close();

		} catch (SQLException ex) {
			System.out.println("Message: " + ex.getMessage());
		}

		// Checks for correct format and cid availability
		while (!correctFormat || !cidAvailable) {
			System.out.print("\n\nType your desired 4-character ID: ");
			cid = in.readLine();

			if (!ids.contains(cid)) {
				cidAvailable = true;
			} else {
				cidAvailable = false;
				System.out
						.println("This CID is not available, please try again.");
			}

			if (cid.length() == 4) {
				correctFormat = true;
			} else {
				correctFormat = false;
				System.out
						.println("Incorrect format. We're looking for a 4-character id.");
			}
		}

		try {
			con.setAutoCommit(false);
			ps = con.prepareStatement("INSERT INTO Customer VALUES (?,?,?,?,?)");

			ps.setString(1, cid);

			System.out.print("\nPassword: ");
			password = in.readLine();
			ps.setString(2, password);

			System.out.print("\nName: ");
			name = in.readLine();
			ps.setString(3, name);

			System.out.print("\nAddress: ");
			address = in.readLine();
			ps.setString(4, address);

			System.out.print("\nPhone: ");
			phone = in.readLine();
			ps.setString(5, phone);

			ps.executeUpdate();

			// commit work
			con.commit();

			ps.close();

			System.out.println("Registration Successful!\n" + cid
					+ " is now on the database");

		} catch (IOException e) {
			System.out.println("IOException!");
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

		b.showMenu();
	}

	public void login() {
		String cid, password;
		PreparedStatement ps;
		ResultSet rs;
		int rowNumber = 0;
		String name = null;
		boolean correctLogin = false;

		try {
			while (!correctLogin) {
				con.setAutoCommit(false);
				ps = con.prepareStatement("SELECT * FROM Customer WHERE cid = ? AND password = ?");

				System.out
						.print("\nPlease type your unique 4-digit customer ID: ");
				cid = in.readLine();
				ps.setString(1, cid);

				System.out.print("\nPlease type your password: ");
				password = in.readLine();
				ps.setString(2, password);

				rs = ps.executeQuery();

				// commit work
				con.commit();

				while (rs.next()) {
					name = rs.getString(3);
					rowNumber++;
				}

				if (rowNumber == 1) {
					ArrayList<CartItem> wishList = new ArrayList<CartItem>();
					customerView(cid, wishList, name.trim());
					correctLogin = true;
					ps.close();
				} else {
					System.out.println("Wrong login, please try again");
				}
			}

		} catch (IOException e) {
			System.out.println("IOException!");
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

	public void customerView(String cid, ArrayList<CartItem> wishList,
			String name) throws IOException {
		boolean quit = false;
		int choice = 0;
		double total = 0;
		DecimalFormat money = new DecimalFormat("$0.00");

		while (!quit) {
			System.out.print("\n\nWelcome, " + name + "!\n");
			System.out
					.print("\nYou have the following items in your virtual cart:\n\n");

			System.out.printf("%-20s", "UPC");
			System.out.printf("%-20s", "TITLE");
			System.out.printf("%-20s", "QUANTITY");
			System.out.printf("%-20s", "SELLPRICE");
			System.out.printf("%-20s", "TOTAL PRICE");
			System.out.println();
			for (int i = 0; i < 100; i++) {
				System.out.printf("-");
			}
			System.out.println();

			total = 0;
			for (CartItem c : wishList) {
				total = total + c.quantity * c.price;
			}

			for (CartItem c : wishList) {
				System.out.printf("%-20s", c.upc);
				System.out.printf("%-20s", c.title);
				System.out.printf("%-20s", c.quantity);
				System.out.printf("%-20s", money.format(c.price));
				System.out.print(money.format(c.quantity * c.price));
				System.out.println();
			}

			System.out.println("\nYOUR GRAND TOTAL: " + money.format(total));

			if (wishList.size() == 0) {
				System.out.printf("\nYou have nothing in your virtual cart...");
			}

			System.out.print("\n\nWhat do you want to do? \n");
			System.out.print("1. Buy an item\n");
			System.out.print("2. Commit to all purchase\n");
			System.out.print("3. Leave without saving\n>> ");

			try {
				choice = Integer.parseInt(in.readLine());
			} catch (NumberFormatException e) {
				e.printStackTrace();
				System.out.println(e.getMessage());
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}

			System.out.println(" ");

			switch (choice) {
			case 1:
				itemSelection(cid, wishList);
				break;
			case 2:
				if (!wishList.isEmpty()) {
					commit(cid, wishList);
					wishList.clear();
				} else {
					System.out.println("You have no items to purchase!");
				}
				break;
			case 3:
				b.showMenu();
			default:
				System.out.println("Wrong format, please try again.");
			
			}
		}
	}

	public void commit(String cid, ArrayList<CartItem> wishList) {
		String cardNo, expiryDate;
		int receiptID = 0;
		PreparedStatement ps;
		Statement stmt;
		ResultSet rs;
		Long time = System.currentTimeMillis();
		java.sql.Date now = new java.sql.Date(time);
		Date[] deliveryAndExpectedDates = deliveryAndExpected(now);
		
		// Make the Purchase data point
		try {
			con.setAutoCommit(false);
			System.out
					.println("You have chosen to commit all the items in your virtual cart.");
			System.out.println("Please enter your credit card number: ");
			cardNo = in.readLine();

			System.out.println("Please enter your card's expiry date: ");
			expiryDate = in.readLine();

			System.out.println("Perfect. Processing your purchase now...");

			ps = con.prepareStatement("INSERT INTO Purchase VALUES(receiptCounter.nextVal, CURRENT_TIMESTAMP, ?, ?, ?, ?, ?)");

			ps.setString(1, cid);
			ps.setString(2, cardNo);
			ps.setString(3, expiryDate);
			ps.setDate(4, deliveryAndExpectedDates[0]);
			ps.setDate(5, deliveryAndExpectedDates[1]);

			ps.executeUpdate();

			// commit work
			con.commit();

			ps.close();

			System.out
					.println("Credit card authenticated!\nProceeding to committing your purchases...\n");

		} catch (IOException e) {
			System.out.println("IOException!");
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

		// Retrieve ReceiptID
		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery("SELECT max(receiptID) FROM Purchase");

			while (rs.next()) {
				receiptID = Integer.parseInt(rs.getString(1));
			}

			System.out.println("receiptID retrieval Successful, receiptID = "
					+ receiptID);
			// close the statement;
			// the ResultSet will also be closed
			stmt.close();
		} catch (SQLException ex) {
			System.out.println("Message: " + ex.getMessage());
		}

		// Register all the items in virtual cart
		try {
			for (CartItem c : wishList) {
				con.setAutoCommit(false);
				ps = con.prepareStatement("INSERT INTO PurchaseItem VALUES (?,?,?)");

				ps.setInt(1, receiptID);
				ps.setString(2, c.upc);
				ps.setInt(3, c.quantity);

				ps.executeUpdate();

				System.out.println("Added UPC#" + c.upc);

				// commit work
				con.commit();

				ps.close();

				con.setAutoCommit(false);
				ps = con.prepareStatement("UPDATE Item SET quantity = quantity - ? WHERE Item.upc = ?");
				ps.setInt(1, c.quantity);
				ps.setString(2, c.upc);
				ps.executeUpdate();
				con.commit();
				ps.close();
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
	}

	public void itemSelection(String cid, ArrayList<CartItem> wishList)
			throws IOException {
		String title, category, name, upc, response, quantityString, selectionString;
		CartItem c;
		String query = "SELECT i.upc, i.title, i.category, i.quantity, l.name, i.sellprice FROM Item I, LeadSinger L WHERE i.upc=l.upc AND i.quantity > 0";
		int selection, quantity = 0, quantityLimit = 0, rowCount = 0;
		Statement stmt;
		boolean proceed = false, sameSong = false;
		ResultSet rs, rsCheck, rsCart;

		try {
			con.setAutoCommit(false);

			while (!proceed || sameSong) {

				stmt = con.createStatement();

				System.out.println("\nLet's look for your item...");
				System.out
						.println("Please enter the item title, leave it blank to check everything.");
				title = in.readLine();
				if (!title.isEmpty()) {
					query = query.concat(" AND i.title = '" + title + "'");
				}
				System.out
						.println("Please enter the item category, leave it blank to check everything.");
				category = in.readLine();
				if (!category.isEmpty()) {
					query = query
							.concat(" AND i.category = '" + category + "'");
				}
				System.out
						.println("Please enter the artist name, leave it blank to check everything.");
				name = in.readLine();
				if (!name.isEmpty()) {
					query = query.concat(" AND l.name = '" + name + "'");
				}

				quantity = 0;
				while (quantity < 1) {
					System.out
							.println("Please enter the quantity, leave it blank to default to 1.");
					quantityString = in.readLine();
					if (quantityString.isEmpty()) {
						quantity = 1;
					} else {
						quantity = Integer.parseInt(quantityString);
					}
				}

				rsCheck = stmt.executeQuery(query);
				while (rsCheck.next()) {
					rowCount++;
				}
				if (rowCount == 0) {
					System.out.println("\nNo matches found, please try again.");
					proceed = false;
					return;
				} else {
					proceed = true;
				}

				stmt = con.createStatement();
				rs = stmt.executeQuery(query);
				b.displayEnumTable(rs);

				System.out
						.println("\n\nI have listed all the items that match your query,\nplease enter the choice# that you want to buy, leave it blank to default to the first choice:");
				selectionString = in.readLine();
				if (selectionString.isEmpty()) {
					selection = 1;
				} else {
					selection = Integer.parseInt(selectionString);
				}

				rsCart = stmt.executeQuery(query);
				c = rsToCartItem(rsCart, selection);
				upc = c.upc;
				title = c.title;
				quantityLimit = c.quantity;

				// Make sure this item is not already on the list
				sameSong = false;
				for (CartItem c1 : wishList) {
					if (c1.upc.equals(upc)) {
						System.out
								.println("You CANNOT buy the same item twice, please try again.");
						sameSong = true;
						break;
					}
				}

				if (!sameSong) {
					// We know everything, time to put the order in the cart
					if (quantityLimit < quantity) {
						System.out.println("There is only " + quantityLimit
								+ " availble, is that okay? (y/n)");
						response = in.readLine();
						System.out.println("Your reponse was " + response);
						if (response.equalsIgnoreCase("y")) {
							System.out
									.println("Great! You have placed an order for "
											+ quantityLimit + " copies.");
							wishList.add(c);
							proceed = true;
						} else {
							proceed = false;
						}
					} else {
						// changing c.quantity to become the desired quantity
						c.quantity = quantity;
						wishList.add(c);
						System.out.println("Selection successful!");
						System.out
								.println("You have added another item to your cart");
						proceed = true;
					}
				} else {
					break;
				}
			}

		} catch (IOException e) {
			System.out.println("IOException!");
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

	// given the rs and choice, returns the CartItem
	public CartItem rsToCartItem(ResultSet rs, int choice) throws SQLException {
		CartItem c = new CartItem("7704", "Recovery", 20, 19.99);
		int index = 0;

		while (rs.next()) {
			index++;
			if (index == choice) {
				c.upc = rs.getString(1);
				c.title = rs.getString(2);
				c.quantity = Integer.parseInt(rs.getString(4));
				c.price = Double.parseDouble(rs.getString(6));
			}
		}

		if (choice > index) {
			System.out
					.println("ERROR - your choice exceed the size of the RS!");
			System.out.println("You wanted " + choice);
			System.out.println("Your index is " + index);
		}

		return c;
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

		System.out.println("\nChecking if your purchases can be delivered on "
				+ deliveryDate + "...");

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

		System.out.println("On " + deliveryDate + ", there are currently "
				+ deliveryNumber + " deliveries.");

		if (deliveryNumber < limit) {
			System.out
					.println("Therefore, your purchase can be delivered on that date.");
			System.out.println("\nYou can expect your purchase to arrive on "
					+ expected + ".\n");
			return result;
		} else {
			System.out
					.println("Therefore, your purchase CANNOT be delivered on that date (MAX = "
							+ limit + ")...");
			result = deliveryAndExpected(deliveryDateAddOne);
			return result;
		}
	}
}