package test;
import java.sql.Connection;
import java.sql.DriverManager;

public class datacon {
	final public static String HOST 		= "localhost";
	final public static String PORT 		= "5432";
	final public static String DBNAME 	= "kpca_payroll_12Oct2022";
	final public static String DBUSERNAME	= "postgres";
	final public static String DBPASSWORD = "dbPassword";
	Connection con=null;
	public datacon(){
		makeConnection(con);
	}
	
	public Connection makeConnection(Connection con) {

		try {
				Class.forName("org.postgresql.Driver");
				//con = DriverManager.getConnection("jdbc:postgresql://192.168.1.14/jpms_payroll", "konnect", "konnect");
				con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/kpca_payroll_12Oct2022","postgres","dbPassword");
				
					System.out.println("==============Connection Established============================");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return con;

	}
	public void closeConnection(Connection con) {

		try {
			if(con!=null){
				con.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}	
	public static void main(String[] args) {
		new datacon();
	}
}