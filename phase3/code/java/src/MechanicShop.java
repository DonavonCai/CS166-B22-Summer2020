/*
 * Template JAVA User Interface
 * =============================
 *
 * Database Management Systems
 * Department of Computer Science &amp; Engineering
 * University of California - Riverside
 *
 * Target DBMS: 'Postgres'
 *
 */


import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;

/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 *
 */

public class MechanicShop{
	//reference to physical database connection
	private Connection _connection = null;
	static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
	
	public MechanicShop(String dbname, String dbport, String user, String passwd) throws SQLException {
		System.out.print("Connecting to database...");
		try{
			// constructs the connection URL
			String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
			System.out.println ("Connection URL: " + url + "\n");
			
			// obtain a physical connection
	        this._connection = DriverManager.getConnection(url, user, passwd);
	        System.out.println("Done");
		}catch(Exception e){
			System.err.println("Error - Unable to Connect to Database: " + e.getMessage());
	        System.out.println("Make sure you started postgres on this machine");
	        System.exit(-1);
		}
	}
	
	/**
	 * Method to execute an update SQL statement.  Update SQL instructions
	 * includes CREATE, INSERT, UPDATE, DELETE, and DROP.
	 * 
	 * @param sql the input SQL string
	 * @throws java.sql.SQLException when update failed
	 * */
	public void executeUpdate (String sql) throws SQLException { 
		// creates a statement object
		Statement stmt = this._connection.createStatement ();

		// issues the update instruction
		stmt.executeUpdate (sql);

		// close the instruction
	    stmt.close ();
	}//end executeUpdate

	/**
	 * Method to execute an input query SQL instruction (i.e. SELECT).  This
	 * method issues the query to the DBMS and outputs the results to
	 * standard out.
	 * 
	 * @param query the input query string
	 * @return the number of rows returned
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	public int executeQueryAndPrintResult (String query) throws SQLException {
		//creates a statement object
		Statement stmt = this._connection.createStatement ();

		//issues the query instruction
		ResultSet rs = stmt.executeQuery (query);

		/*
		 *  obtains the metadata object for the returned result set.  The metadata
		 *  contains row and column info.
		 */
		ResultSetMetaData rsmd = rs.getMetaData ();
		int numCol = rsmd.getColumnCount ();
		int rowCount = 0;
		
		//iterates through the result set and output them to standard out.
		boolean outputHeader = true;
		while (rs.next()){
			if(outputHeader){
				for(int i = 1; i <= numCol; i++){
					System.out.print(rsmd.getColumnName(i) + "\t");
			    }
			    System.out.println();
			    outputHeader = false;
			}
			for (int i=1; i<=numCol; ++i)
				System.out.print (rs.getString (i) + "\t");
			System.out.println ();
			++rowCount;
		}//end while
		stmt.close ();
		return rowCount;
	}
	
	/**
	 * Method to execute an input query SQL instruction (i.e. SELECT).  This
	 * method issues the query to the DBMS and returns the results as
	 * a list of records. Each record in turn is a list of attribute values
	 * 
	 * @param query the input query string
	 * @return the query result as a list of records
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	public List<List<String>> executeQueryAndReturnResult (String query) throws SQLException { 
		//creates a statement object 
		Statement stmt = this._connection.createStatement (); 
		
		//issues the query instruction 
		ResultSet rs = stmt.executeQuery (query); 
	 
		/*
		 * obtains the metadata object for the returned result set.  The metadata 
		 * contains row and column info. 
		*/ 
		ResultSetMetaData rsmd = rs.getMetaData (); 
		int numCol = rsmd.getColumnCount (); 
		int rowCount = 0; 
	 
		//iterates through the result set and saves the data returned by the query. 
		boolean outputHeader = false;
		List<List<String>> result  = new ArrayList<List<String>>(); 
		while (rs.next()){
			List<String> record = new ArrayList<String>(); 
			for (int i=1; i<=numCol; ++i) 
				record.add(rs.getString (i)); 
			result.add(record); 
		}//end while 
		stmt.close (); 
		return result; 
	}//end executeQueryAndReturnResult
	
	/**
	 * Method to execute an input query SQL instruction (i.e. SELECT).  This
	 * method issues the query to the DBMS and returns the number of results
	 * 
	 * @param query the input query string
	 * @return the number of rows returned
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	public int executeQuery (String query) throws SQLException {
		//creates a statement object
		Statement stmt = this._connection.createStatement ();

		//issues the query instruction
		ResultSet rs = stmt.executeQuery (query);

		int rowCount = 0;

		//iterates through the result set and count nuber of results.
		if(rs.next()){
			rowCount++;
		}//end while
		stmt.close ();
		return rowCount;
	}
	
	/**
	 * Method to fetch the last value from sequence. This
	 * method issues the query to the DBMS and returns the current 
	 * value of sequence used for autogenerated keys
	 * 
	 * @param sequence name of the DB sequence
	 * @return current value of a sequence
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	
	public int getCurrSeqVal(String sequence) throws SQLException {
		Statement stmt = this._connection.createStatement ();
		
		ResultSet rs = stmt.executeQuery (String.format("Select currval('%s')", sequence));
		if (rs.next()) return rs.getInt(1);
		return -1;
	}

	/**
	 * Method to close the physical connection if it is open.
	 */
	public void cleanup(){
		try{
			if (this._connection != null){
				this._connection.close ();
			}//end if
		}catch (SQLException e){
	         // ignored.
		}//end try
	}//end cleanup

	/**
	 * The main execution method
	 * 
	 * @param args the command line arguments this inclues the <mysql|pgsql> <login file>
	 */
	public static void main (String[] args) {
		if (args.length != 3) {
			System.err.println (
				"Usage: " + "java [-classpath <classpath>] " + MechanicShop.class.getName () +
		            " <dbname> <port> <user>");
			return;
		}//end if
		
		MechanicShop esql = null;
		
		try{
			System.out.println("(1)");
			
			try {
				Class.forName("org.postgresql.Driver");
			}catch(Exception e){

				System.out.println("Where is your PostgreSQL JDBC Driver? " + "Include in your library path!");
				e.printStackTrace();
				return;
			}
			
			System.out.println("(2)");
			String dbname = args[0];
			String dbport = args[1];
			String user = args[2];
			
			esql = new MechanicShop (dbname, dbport, user, "");
			
			boolean keepon = true;
			while(keepon){
				System.out.println("MAIN MENU");
				System.out.println("---------");
				System.out.println("1. AddCustomer");
				System.out.println("2. AddMechanic");
				System.out.println("3. AddCar");
				System.out.println("4. InsertServiceRequest");
				System.out.println("5. CloseServiceRequest");
				System.out.println("6. ListCustomersWithBillLessThan100");
				System.out.println("7. ListCustomersWithMoreThan20Cars");
				System.out.println("8. ListCarsBefore1995With50000Milles");
				System.out.println("9. ListKCarsWithTheMostServices");
				System.out.println("10. ListCustomersInDescendingOrderOfTheirTotalBill");
				System.out.println("11. < EXIT");
				
				/*
				 * FOLLOW THE SPECIFICATION IN THE PROJECT DESCRIPTION
				 */
				switch (readChoice()){
					case 1: AddCustomer(esql); break;
					case 2: AddMechanic(esql); break;
					case 3: AddCar(esql); break;
					case 4: InsertServiceRequest(esql); break;
					case 5: CloseServiceRequest(esql); break;
					case 6: ListCustomersWithBillLessThan100(esql); break;
					case 7: ListCustomersWithMoreThan20Cars(esql); break;
					case 8: ListCarsBefore1995With50000Milles(esql); break;
					case 9: ListKCarsWithTheMostServices(esql); break;
					case 10: ListCustomersInDescendingOrderOfTheirTotalBill(esql); break;
					case 11: keepon = false; break;
				}
			}
		}catch(Exception e){
			System.err.println (e.getMessage ());
		}finally{
			try{
				if(esql != null) {
					System.out.print("Disconnecting from database...");
					esql.cleanup ();
					System.out.println("Done\n\nBye !");
				}//end if				
			}catch(Exception e){
				// ignored.
			}
		}
	}

	public static int readChoice() {
		int input;
		// returns only if a correct value is given.
		do {
			System.out.print("Please make your choice: ");
			try { // read the integer, parse it and break.
				input = Integer.parseInt(in.readLine());
				break;
			}catch (Exception e) {
				System.out.println("Your input is invalid!");
				continue;
			}//end try
		}while (true);
		return input;
	}//end readChoice

	// Print a prompt and read user input as string
	private static String readString(String prompt) {
		String input;		
		do {
			System.out.println(prompt);
			try {
				input = in.readLine();
				break;
			} catch (Exception e) {
				System.out.println("invalid input");
				continue;
			}
		} while(true);

		return input;
	}

	private static int readInt(String prompt) {
		int input;
		do {
			System.out.println(prompt);
			try {
				input = Integer.parseInt(in.readLine());
				break;	
			} catch (Exception e) {
				System.out.println("invalid input");
				continue;
			}
		} while(true);

		return input;
	}

	public static void AddCustomer(MechanicShop esql){//1
		// prompt user for data
		String fname;
		String lname;
		String phone;
		int id;
		String address;

		id = readInt("ID:");
		fname = readString("First name:");
		lname = readString("Last name:");
		phone = readString("Phone number:");
		address = readString("Address:");

		// create SQL instruction
		String sql;
		sql = "INSERT INTO Customer(id, fname, lname, phone, address) VALUES (\'" + id + "\', \'" + fname + "\', \'" + lname + "\', \'" + phone + "\', \'" + address + "\');";
		System.out.println(sql);

		// execute
		try {
			esql.executeUpdate(sql);
		} catch (Exception e) {
			System.out.println("Add Customer failed:");
			System.out.println(e);
		}
	}
	
	public static void AddMechanic(MechanicShop esql){//2
		// prompt user for data
		String fname;
		String lname;
		int id;
		int years_exp;

		id = readInt("ID:");
		fname = readString("First name:");
		lname = readString("Last name:");

		do {
			years_exp = readInt("Years of experience:");
			if (years_exp < 0 || years_exp > 100) {
				System.out.println("Years of experience must be between 0-100");
				continue;
			}
			break;
		} while(true);

		// create SQL instruction
		String sql;
		sql = "INSERT INTO Mechanic(id, fname, lname, experience) VALUES (\'" + id + "\', \'" + fname + "\', \'" + lname + "\', \'" + years_exp + "\');";
		System.out.println(sql);

		// execute
		try {
			esql.executeUpdate(sql);
		} catch (Exception e) {
			System.out.println("Add Mechanic failed:");
			System.out.println(e);
		}
	}
	
	public static void AddCar(MechanicShop esql){//3
		// prompt user for data
		String vin;
		String make;
		String model;
		int year;

		vin = readString("VIN:");
		make = readString("Make:");
		model = readString("Model:");

		do {
			year = readInt("Year:");
			if (year < 1970) {
				System.out.println("Year must be >= 1970");
				continue;
			}
			break;
		} while(true);

		// create SQL instruction
		String sql;
		sql = "INSERT INTO Car(vin, make, model, year) VALUES (\'" + vin + "\', \'" + make + "\', \'" + model + "\', \'" + Integer.toString(year) + "\');";
		System.out.println(sql);

		// execute
		try {
			esql.executeUpdate(sql);
		} catch (Exception e) {
			System.out.println("Add Car failed:");
			System.out.println(e);
		}	
	}
	
	public static void InsertServiceRequest(MechanicShop esql){//4
		int rid;
		int customer_id;
		String car_vin;
		String date; // YYYY-MM-DD
		int odometer;
		String complain;

		rid = readInt("rid:");
		customer_id = readInt("customer id:");
		car_vin = readString("car vin:");
		date = readString("date: (YYYY-MM-DD)");
		
		do {
			odometer = readInt("odometer:");
			if (odometer <= 0) {
				System.out.println("odometer reading must be > 0");
				continue;
			}
			break;
		} while(true);

		complain = readString("complain:");

		String sql;
		sql = "INSERT INTO Service_Request(rid, customer_id, car_vin, date, odometer, complain) VALUES (\'" + rid + "\', \'" + customer_id + "\', \'" + car_vin + "\', \'" + date + "\', \'" + odometer + "\', \'" + complain + "\');";
		System.out.println(sql);
		
		try {
			esql.executeUpdate(sql);
		} catch (Exception e) {
			System.out.println("Add Service Request failed:");
			System.out.println(e);
		}
	}
	
	public static void CloseServiceRequest(MechanicShop esql) throws Exception{//5
		int wid;
		int rid;
		int mid;
		String date;
		String comment;
		int bill;

		wid = readInt("wid:");
		rid = readInt("rid:");
		mid = readInt("mechanic id:");
		date = readString("date: (YYYY-MM-DD)");
		comment = readString("comment:");
		do {
			bill = readInt("bill:");
			if (bill <= 0) {
				System.out.println("bill must be > 0");
				continue;
			}
			break;
		} while(true);

		String sql;
		sql = "INSERT INTO Closed_Request(wid, rid, mid, date, comment, bill) VALUES (\'" + wid + "\', \'" + rid + "\', \'" + mid + "\', \'" + date + "\', \'" + comment + "\', \'" + bill + "\');";

		try {
			System.out.println(sql);
			esql.executeUpdate(sql);
		} catch (Exception e) {
			System.out.println("Close Service Request failed:");
			System.out.println(e);
		}
	}
	
	public static void ListCustomersWithBillLessThan100(MechanicShop esql){//6
		String select = "SELECT C.fname, C.lname, CR.bill\n"; 
		String from = "FROM Customer C, Service_Request SR, Closed_Request CR\n";
		String where = "WHERE C.id = SR.customer_id AND SR.rid = CR.rid AND CR.bill < 100;";

		String sql = select + from + where;
		try {
			System.out.println(sql);
			esql.executeQueryAndPrintResult(sql);
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	
	public static void ListCustomersWithMoreThan20Cars(MechanicShop esql){//7
		/*String select = "SELECT C.fname, C.lname\n"; 
		String from = "FROM Customer C, \n";
		String where = "WHERE C.id = SR.customer_id AND SR.rid = CR.rid AND CR.bill < 100;";

		String sql = select + from + where;
		try {
			System.out.println(sql);
			esql.executeQueryAndPrintResult(sql);
		} catch (Exception e) {
			System.out.println(e);
		}*/
	}
	
	public static void ListCarsBefore1995With50000Milles(MechanicShop esql){//8
		String select = "SELECT C.vin, C.year, SR.odometer\n"; 
		String from = "FROM Car C, Service_Request SR\n";
		String where = "WHERE C.vin = SR.car_vin AND C.year < 1995 AND SR.odometer >= 50000;";

		String sql = select + from + where;
		try {
			System.out.println(sql);
			esql.executeQueryAndPrintResult(sql);
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	
	public static void ListKCarsWithTheMostServices(MechanicShop esql){//9
		//
		
	}
	
	public static void ListCustomersInDescendingOrderOfTheirTotalBill(MechanicShop esql){//9
		String select = "SELECT C.id, SUM(CR.bill)\n";
		String from = "FROM Customer C, Service_Request SR, Closed_Request CR\n";
		String where = "WHERE C.id = SR.customer_id AND SR.rid = CR.rid\n";
		String groupby = "GROUP BY C.id\n";
		String orderby = "ORDER BY SUM(CR.bill) DESC;";

		String sql = select + from + where + groupby + orderby;
		try {
			System.out.println(sql);
			esql.executeQueryAndPrintResult(sql);
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	
}
