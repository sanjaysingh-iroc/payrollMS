package com.konnect.jpms.training;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class LearnersInfoDashboard extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	String strSessionEmpId = null;

	private int empId;  

	CommonFunctions CF=null; 
	     
      
	
	
	private static Logger log = Logger.getLogger(LearnersInfoDashboard.class);
	
	public String execute() {
		
		
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if (CF==null){
			return LOGIN;
		}
		
		/*boolean isView  = CF.getAccess(session, request, uF);
		if(!isView){
			request.setAttribute(PAGE, PAccessDenied);
			request.setAttribute(TITLE, TAccessDenied);
			return ACCESS_DENIED;
		}*/
		

	//	request.setAttribute(PAGE, "/jsp/training/LearnersInfoDashboard.jsp");
		request.setAttribute(TITLE, "Learners Info");

				
		prepareInformation(CF);
		
		return SUCCESS;
	

	}

	private void prepareInformation(CommonFunctions CF) {

		Database db = new Database();
		db.setRequest(request);
		PreparedStatement pst = null;
		Connection con = null;
		UtilityFunctions uF=new UtilityFunctions();
		ResultSet rst=null;
		
		List<List<String>> alLearnersInfoDashboard=new ArrayList<List<String>>();
		try{
			con=db.makeConnection(con);
//			Map<String,String> hmempName=CF.getEmpNameMap(con, null, null);
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			// calculation of Attendance 

/*			int totalDaysCount=0;
			pst=con.prepareStatement("select count(*) from ( select count(*) from training_schedule join training_session using (schedule_id)   " +
					" where plan_id=? group by frequency_date )as a");
			pst.setInt(1,uF.parseToInt(getPlanId()));
			rst=pst.executeQuery();
			while(rst.next()){
				totalDaysCount=rst.getInt(1);
			}*/
			int totalDaysCount=0;
			pst=con.prepareStatement("select training_duration from training_schedule where plan_id=? ");
			pst.setInt(1,uF.parseToInt(getPlanId()));
				rst=pst.executeQuery();
			 if(rst.next()){
				 totalDaysCount=rst.getInt(1);
			 }
			rst.close();
			pst.close();
	
			
			Map<String,String> hmDaysPresent=new HashMap<String,String>();
			pst=con.prepareStatement(" select count(*),ts.emp_id,plan_id from training_status ts " +
					"join training_learnings using(learning_id) where plan_id=? group by ts.emp_id,plan_id");
			pst.setInt(1,uF.parseToInt(getPlanId()));
			rst=pst.executeQuery();
			while(rst.next()){
				double totalAttendance=(uF.parseToDouble(""+rst.getInt("count"))/uF.parseToDouble(""+totalDaysCount))*100;
				hmDaysPresent.put(rst.getString("emp_id"),""+uF.formatIntoComma(totalAttendance) );
				
			}
			rst.close();
			pst.close();
			// learners status  Information 
			
//			Map hmwlocation=CF.getWLocationMap(con, null, null);
			
			StringBuilder strquery=new StringBuilder();
			strquery.append("select emp_id,emp_fname,emp_mname, emp_lname,plan_id,is_certificate_given,trainer_comments,is_completed" +
					" from training_learnings join employee_personal_details on emp_id=emp_per_id where plan_id=?");
			pst=con.prepareStatement(strquery.toString());
			pst.setInt(1,uF.parseToInt(getPlanId()));
			rst=pst.executeQuery();
			while(rst.next()){
				
				List<String> alinner=new ArrayList<String>();
				 alinner.add(rst.getString("emp_id"));
				 
				 String strEmpMName = "";
					if(flagMiddleName) {
						if(rst.getString("emp_mname") != null && rst.getString("emp_mname").trim().length()>0) {
							strEmpMName = " "+rst.getString("emp_mname");
						}
					}

                 alinner.add(rst.getString("emp_fname")+strEmpMName+" "+rst.getString("emp_lname"));
                 
                 alinner.add(hmDaysPresent.get(rst.getString("emp_id").trim()));
                 
                 if(rst.getString("is_completed").equals("1"))
                	 alinner.add("YES");
                	 else
                 alinner.add("NO");
                 
                 alinner.add(rst.getString("is_certificate_given"));
                 
                 alinner.add(uF.showData(rst.getString("trainer_comments"),""));
                 
		         alinner.add(getPlanId());
                 
                 alLearnersInfoDashboard.add(alinner);
			}
			rst.close();
			pst.close();
			
			String empIds=null;
			pst=con.prepareStatement("select emp_ids from training_schedule  where plan_id=?");
			pst.setInt(1,uF.parseToInt(getPlanId()));
			rst=pst.executeQuery();
			while(rst.next()){
				empIds=rst.getString(1);
			}
			rst.close();
			pst.close();
			
			// those who have not started training yet  
			if(empIds!=null){
			pst=con.prepareStatement(" select emp_per_id,emp_fname, emp_mname,emp_lname from " +
					"(select emp_per_id,emp_fname,emp_lname from employee_personal_details " +
					"left join training_learnings on  emp_id=emp_per_id where emp_per_id in ("+empIds.substring(1, empIds.length()-1)+")) a  " +
					"where emp_per_id not in (select emp_id from training_learnings where plan_id=?) ");
			
			pst.setInt(1,uF.parseToInt(getPlanId()));
//			System.out.println("pst=====>"+pst);
			rst=pst.executeQuery();
			while(rst.next()){
				List<String> alinner=new ArrayList<String>();
				 alinner.add(rst.getString("emp_per_id"));
				 
				 String strEmpMName = "";
					if(flagMiddleName) {
						if(rst.getString("emp_mname") != null && rst.getString("emp_mname").trim().length()>0) {
							strEmpMName = " "+rst.getString("emp_mname");
						}
					}

                alinner.add(rst.getString("emp_fname")+strEmpMName+" "+rst.getString("emp_lname"));
                
                alinner.add("0");
                alinner.add("Not Started Yet");
                alinner.add("-1");
                alinner.add("NA");
                alinner.add(getPlanId());
                alLearnersInfoDashboard.add(alinner);
			}
			rst.close();
			pst.close();
			}
			
			request.setAttribute("alLearnersInfoDashboard", alLearnersInfoDashboard);
			
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
			
		}
		
	}

	
	String planId;


	public String getPlanId() {
		return planId;
	}

	public void setPlanId(String planId) {
		this.planId = planId;
	}


	HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {

		this.request=request;
		
	}
	

}