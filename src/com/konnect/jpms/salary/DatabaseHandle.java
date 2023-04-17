/*package com.konnect.jpms.salary;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.konnect.jpms.util.IConstants;

public class DatabaseHandle  implements IConstants {

	String userId="69";
	public String salaryDetails="SELECT employee_personal_details.emp_fname, employee_personal_details.emp_lname, employee_personal_details.joining_date, designation_info.desig_name, (SELECT COUNT(*) FROM roster_details WHERE emp_id="+userId+") as no_of_working_days , (select COUNT(*)  from payroll where emp_id="+userId+") as days_present	FROM employee_personal_details  INNER JOIN employee_official_details ON employee_personal_details.emp_per_id = employee_official_details.emp_id  JOIN designation_info ON designation_info.desig_id=employee_official_details.designation_id WHERE employee_personal_details.emp_per_id="+userId;
	private  ResultSet rs,rs2;
	private Connection con;
	private Statement stmt,stmt2;
	double totalEarnings=10000,totalDeduction=2000;
	
	public DatabaseHandle()
	{
		try
		{
			try
			{
				Class.forName("org.postgresql.Driver");
			}
			catch(ClassNotFoundException e)
			{
				System.out.println("Class not found");
			}
			con = DriverManager.getConnection("jdbc:postgresql://" + HOST + ":"+PORT+"/"
					+ DBNAME, DBUSERNAME, DBPASSWORD );
			stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
			stmt2 = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
			rs=stmt.executeQuery(salaryDetails);
			rs.next();
			rs2=stmt2.executeQuery("select * from salary_details");
			//rs2.next();
			
			}
			catch(SQLException e)
			{
				e.printStackTrace();
			}	
		}
		public void closeConn()
		{
			try
			{
				this.con.close();
				System.out.println("Connection closed");
			}
			catch(SQLException e)
			{
				e.printStackTrace();
			}
		}
		
		public List<List<String>> getDataLeft()
		{
			List<List<String>> al = new ArrayList<List<String>>();
			List alInner = new ArrayList();
			
			try
			{
				alInner = new ArrayList<String>();
				alInner.add(rs.getString("emp_fname")+" "+rs.getString("emp_lname"));
				alInner.add("Emplyee ID No");
				alInner.add("Acc No.");
				alInner.add(rs.getString("no_of_working_days"));
				alInner.add(rs.getString("days_present"));
				alInner.add("Salary Number");
				al.add(alInner);                                    
			}
			catch(SQLException e)
			{
					e.printStackTrace();			
			}
			
			return al;
		}
		
		
		public List<List<String>> getDataRight()
		{
			List<List<String>> al = new ArrayList<List<String>>();
			List alInner = new ArrayList();
			
			try
			{
				alInner = new ArrayList<String>();
				alInner.add(rs.getString("joining_date"));
				alInner.add(rs.getString("desig_name"));
				alInner.add("Paid leaves");
				alInner.add("P.F.");
				alInner.add("Leave without pay");
				al.add(alInner);
			
				
			}
			catch(SQLException e)
			{
					e.printStackTrace();			
			}
			return al;
		}
		
		
		public String[] getData()
		{
			String array[]=new String[16];
			int i=0;
			try
			{
			
				while(rs2.next())
				{
					System.out.println("value of i------->>"+i);
					array[i]=rs2.getString("salary_head_name");
					i++;
					//rs2.next();
				}
							
			}
			catch(SQLException e)
			{
				e.printStackTrace();
				
			}
			return array;
		}
		
		public String[] getDataLocation()
		{
			String array2[]=new String[16];
			int i=0;
			
			try
			{
				rs2.first();
				while(rs2.next())
				{
					array2[i]=rs2.getString("salary_head_byte");
					i++;
						//rs2.next();
				}
				
			}
			catch(SQLException e)
			{
				e.printStackTrace();			
			}
			System.out.println(array2.length);
			return array2;
		}
		
		public String[] calculate(String[] head, String [] category)
		{
			String array[]=new String[16];
			String calculateType="";
			String type;
			double number;
			int j=0;
			try
			{
				rs2.first();
				while(rs2.next())
				{
					calculateType=rs2.getString("salary_head_amount_type");
					number=Double.parseDouble(((rs2.getString("salary_head_value"))));
					type=rs2.getString("salary_head_byte");
					
					if(calculateType.trim().equals("P"))
					{
						if(type.trim().equals("E"))
							array[j]=Double.toString((totalEarnings*(number/100)));
						else
							array[j]=Double.toString((totalDeduction*(number/100)));
						
					}
					else
					{
						array[j]=Double.toString(number);
					}
					j++;
				}
			}
			catch(SQLException e)
			{
				e.printStackTrace();				
			}
				return array;
		}
}*/