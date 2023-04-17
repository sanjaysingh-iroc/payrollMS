/*package com.konnect.jpms.salary;
import com.konnect.jpms.util.*;
import com.opensymphony.xwork2.ActionSupport;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

public class HourlyPaySlipData extends ActionSupport implements IStatements,IConstants,ServletRequestAware{
	
	private HttpServletRequest request;
	HttpSession session = request.getSession();
	String strEmpId = (String) session.getAttribute("EMPID");
	String strDateFormat = "MM";
	SimpleDateFormat sdf = new SimpleDateFormat(strDateFormat);
	String monthNo=sdf.format(new Date());
	String userServiceListStr;
	
	
	private ResultSet rsLeftPart,rsWorkingDaysUpper,rsDayWise,rsLeave;
	private Connection con;
	private PreparedStatement stmt,stmt2,stmt3,stmtLeaves;
	private PreparedStatement prepForLeaves; 
	String[] temp;

	
	int countPayableOnThisMonth=0,countPayableOnThisMonthOff=0;
	public HourlyPaySlipData()
	{
		try
		{
			Database dbConnect=new Database();
			con =  dbConnect.makeConnection(con);
			stmt = con.prepareStatement(salaryDetailsLeftValues,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
			stmt.setString(1, strEmpId);
			stmt.setString(2, "_____"+monthNo+"%");
			rsLeftPart=stmt.executeQuery();	
			rsLeftPart.next();
			
			stmt2 = con.prepareStatement(countWorkingDays,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
			stmt2.setString(1, "_____"+monthNo+"%");
			stmt2.setString(2, strEmpId);
			rsWorkingDaysUpper=stmt2.executeQuery();	
			rsWorkingDaysUpper.next();
			countServices();
			
	
		 }
			catch(SQLException e)
			{
				e.printStackTrace();
			}	
	}
	
	public List<String> leftUpperData()
	{
		List<String> leftDataList=new ArrayList<String>();
		String str="";
		
		try
		{
			rsLeftPart.first();
			leftDataList.add(rsLeftPart.getString("emp_per_id"));
			leftDataList.add(rsLeftPart.getString("emp_fname")+" "+rsLeftPart.getString("emp_lname"));
			leftDataList.add(rsLeftPart.getString("dept_name"));
			leftDataList.add(rsLeftPart.getString("emp_bank_name"));
			leftDataList.add(rsLeftPart.getString("emp_bank_acct_nbr"));
			
			for(int j=0;j<temp.length;j++)
			{
				if(temp[j].equals("NA")==true)
					continue;
				else
					str=str+temp[j]+",";
			}
			userServiceListStr=str;
		
			
			leftDataList.add(str);			
			leftDataList.add(rsLeftPart.getString("emptype"));
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
	
		
		return leftDataList;
	}
	
	
	public List<String> rightUpperData()
	{
		List<String> rightDataList=new ArrayList<String>();
	
		try
		{
			rsLeftPart.first();
			rightDataList.add(rsWorkingDaysUpper.getString(1));
			rightDataList.add("");
			rightDataList.add("");
			rightDataList.add(rsLeftPart.getString("emp_pan_no"));
			
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return rightDataList;
	}

	
	
	public int countServicesServedInAMonth()
	{
		int count=0;
		for(int i=0; i<temp.length; i++)
		{
			if(temp[i].equals("NA"))
				continue;
			else
				count++;
		}
		return count;
	}
	
	
	public int countServices()
	{
		int services=0;
		String str="";
		try
		{
			rsLeftPart.first();
			do
			{
				str=str+rsLeftPart.getString("service_name")+",";
			}
			while(rsLeftPart.next());
		
			temp=str.split(",");
			for(int i=0; i<temp.length; i++)
			{
				for(int j=i+1; j<temp.length; j++)
				{
					if(temp[i].equals(temp[j]))
					{
						temp[j]="NA";
					}
				}
			}
			
		rsLeftPart.first();
		}catch(SQLException e)
		{
			e.printStackTrace();
			
		} 
		
		return services;
	}
	
	public List<String> countLigitimateDays(String service_name)
	{
		List<String> WorkingDaysList=new ArrayList<String>();
		int dayOfWeek,tempCount=0; 
		Date dateNoApprovalRequired=new Date();
		Date dateApprovalRequired=new Date();
	
		Calendar calendar = new GregorianCalendar();
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		countPayableOnThisMonth=0;
		countPayableOnThisMonthOff=0;
		try
		{
			
			
			//taking directly count of worked days whose _in _out entries are present=========== 
			stmt3 = con.prepareStatement(countWeekWorkingDaysInTheMonth,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
			stmt3.setString(1, strEmpId);
			stmt3.setString(2,"_____"+monthNo+"%" );
			stmt3.setString(3,service_name);
			rsDayWise=stmt3.executeQuery();	
			rsDayWise.next();
			
			
						
			countPayableOnThisMonth=Integer.parseInt((rsDayWise.getString("weekDaysWorked")));
			WorkingDaysList.add(Integer.toString(countPayableOnThisMonth));
			//=================================================================================
		
			//to check whether absent days were week ends. 
			stmt3 = con.prepareStatement(absentButPayableDays,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
			stmt3.setString(1, strEmpId);
			stmt3.setString(2,"_____"+monthNo+"%" );
			stmt3.setString(3,service_name);
			rsDayWise=stmt3.executeQuery();	
		
		
					
			
		
			
				while(rsDayWise.next())// loop to check where absent days were sat sunday or a holiday=======
				{
					dateNoApprovalRequired = (Date)formatter.parse(rsDayWise.getString("generate_date"));  
					calendar.setTime(dateNoApprovalRequired);
					dayOfWeek= calendar.get(Calendar.DAY_OF_WEEK);
									
					if(dayOfWeek==1 || dayOfWeek==7) //1 is sunday and 7 is saturday
					{
						countPayableOnThisMonthOff=countPayableOnThisMonthOff+1;
					}
					else
					{
						//code to check whether it was a holiday======
					}
				}
				WorkingDaysList.add(Integer.toString(countPayableOnThisMonthOff));
				
						
				//=========================================================================================================
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return WorkingDaysList;
	}
	
	
	public List<String> permanentDeductionValues()
	{
		List<String> rightDataList=new ArrayList<String>();
		
		
		
		return rightDataList;
		
	}
	public List<String> getRates(String service_name)
	{
		
		List<String> rates=new ArrayList<String>();
		try
		{
			stmt3 = con.prepareStatement(countWeekWorkingDaysInTheMonth,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
			stmt3.setString(1, strEmpId);
			stmt3.setString(2,"_____"+monthNo+"%" );
			stmt3.setString(3,service_name);
			rsDayWise=stmt3.executeQuery();	
					
			if(rsDayWise.next())
			{
				String rate=rsDayWise.getString("rate");
				rates.add(rate);
			}
			else
			{
				rates.add("0");
			}
			
			stmt3 = con.prepareStatement(countWeekWorkingDaysInTheMonth,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
			stmt3.setString(1, strEmpId);
			stmt3.setString(2,"_____"+monthNo+"%" );
			stmt3.setString(3,service_name);
			rsDayWise=stmt3.executeQuery();	
			
			
			if(rsDayWise.next())
			{
				String rate=rsDayWise.getString("rate");
				System.out.println(service_name+" Worked on holiday with rate::>"+rate);
				rates.add(rate);
			}
			else
			{
				rates.add("0");
			}
			
		}
		catch(SQLException e)
		{
			System.out.println("error here");
			e.printStackTrace();
		}
		return rates;
	}
	
	public String leavesPay()
	{
		Calendar cal;
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		int tempCount=0;
		int countLeaves=0;
		try
		{
			stmtLeaves = con.prepareStatement(countApprovedLeaves,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
			stmtLeaves.setString(1, strEmpId);
			stmtLeaves.setString(2, "_____"+monthNo+"%");
			stmtLeaves.setString(3, "_____"+monthNo+"%");
			rsLeave=stmtLeaves.executeQuery();	
			
		//============count the approved leaves of the 'current month' excluding saturday sunday and holidays===========================
			while(rsLeave.next())
			{
				Date toDate=rsLeave.getDate("approval_to_date");
				Date fromDate=rsLeave.getDate("approval_from");
		
				tempCount=0;
				
				cal = Calendar.getInstance();
				while(!(cal.getTime().equals(rsLeave.getDate("approval_to_date"))))
				{
					cal.setTime(formatter.parse(rsLeave.getString("approval_from")));
					cal.add( Calendar.DATE, tempCount );
					tempCount++;
					
					if(cal.get(Calendar.DAY_OF_WEEK)!=1 && cal.get(Calendar.DAY_OF_WEEK)!=7 && Integer.parseInt(monthNo)-1==cal.get(Calendar.MONTH))//if day is not sat sun or of another month holiday part has to be added in this 
					{
						countLeaves++;
					}
				}
			
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return Integer.toString(countLeaves);	
	}
	
	
	protected void finalize ()
	{
		try
		{
			rsLeftPart.close();
			rsWorkingDaysUpper.close();
			con.close();
			stmt.close();
			stmt2.close();
			stmt3.close();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void setServletRequest(HttpServletRequest arg0) 
	{
		this.request=request;
	}
	
	
 }*/
