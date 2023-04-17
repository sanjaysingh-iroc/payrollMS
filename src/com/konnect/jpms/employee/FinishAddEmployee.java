package com.konnect.jpms.employee;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.struts2.interceptor.ServletRequestAware;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.Notifications;
import com.konnect.jpms.util.UserAlerts;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class FinishAddEmployee extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	public HttpSession session;
	public String strUserType = null;
	String strBaseUserType =  null;
	String strWLocationAccess =  null;
	public String strSessionEmpId = null; 
	public CommonFunctions CF = null;
	
	private String empId;
	public String execute() {
		
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if (CF==null) {
			CF = new CommonFunctions();
			CF.setRequest(request);
			CF.getCommonFunctionsDetails(CF,request);   //added by parvez date: 26-08-2022
		} 
		
		strUserType = (String)session.getAttribute(USERTYPE);
		strBaseUserType = (String)session.getAttribute(BASEUSERTYPE);
		strWLocationAccess = (String)session.getAttribute(WLOCATION_ACCESS);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		
		request.setAttribute(TITLE, TEditEmployee);
		request.setAttribute(PAGE, PPolicyPayroll2);		
		UtilityFunctions uF = new UtilityFunctions();
		/*EncryptionUtility eU = new EncryptionUtility();
		if(getEmpId() != null && uF.parseToInt(getEmpId()) == 0) {
			String decodeEmpId = eU.decode(getEmpId());
			setEmpId(decodeEmpId);
		}*/
		
		List<String> accessEmpList = CF.viewEmployeeIdsList(request, uF, strBaseUserType, strSessionEmpId, strWLocationAccess);
		if((strBaseUserType != null && strUserType != null && (strBaseUserType.equals(EMPLOYEE) || strUserType.equals(EMPLOYEE))) || !accessEmpList.contains(getEmpId())) {
			setEmpId((String)session.getAttribute(EMPID));
		}
		
		getEmpNameById(uF);
		return LOAD;
	}

	private void getEmpNameById(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst =null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			
			String empName="employee";
			pst = con.prepareStatement("select added_by, emp_fname, emp_lname from employee_personal_details where emp_per_id =?");
			pst.setInt(1, uF.parseToInt(getEmpId()));
			rs = pst.executeQuery();
			int nAddedBy = 0;
			String strEmpName = "";
			while (rs.next()) {
				request.setAttribute("EMP_FNAME", rs.getString("emp_fname"));
				nAddedBy = uF.parseToInt(rs.getString("added_by"));
				strEmpName = rs.getString("emp_fname")+" "+uF.showData(rs.getString("emp_lname"),"");
			}
			rs.close();
			pst.close();
			
			String strUserEmail = "";
			pst = con.prepareStatement("select * from employee_personal_details where emp_per_id = ?");
			pst.setInt(1, nAddedBy);
			rs = pst.executeQuery();
			while(rs.next()) {
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				
				empName = rs.getString("emp_fname")+strEmpMName+" "+ uF.showData(rs.getString("emp_lname"),"");
				if(rs.getString("emp_email_sec")!=null && rs.getString("emp_email_sec").length()>0) {
					request.setAttribute("ADDED_BY_EMAIL", rs.getString("emp_email_sec"));
				} else {
					request.setAttribute("ADDED_BY_EMAIL", rs.getString("emp_email"));
				}
				//request.setAttribute("EMP_FNAME",rs.getString("emp_fname"));
				
				strUserEmail = rs.getString("emp_email");
			}
			rs.close();
			pst.close();
			
			request.setAttribute("empName",empName);
			
	//===start parvez date: 26-08-2022===
			pst=con.prepareStatement("select usertype_id from user_details where emp_id=?");
			pst.setInt(1, nAddedBy);
			rs=pst.executeQuery();
			int nUserTypeId = 0;
			while(rs.next()) {
				nUserTypeId = uF.parseToInt(rs.getString("usertype_id"));
			}
			rs.close();
			pst.close();
			
			String strDomain = request.getServerName().split("\\.")[0];
			String alertData = "<div style=\"float: left;\"> <b>"+strEmpName+"</b> has Filled Onboarded form"+". </div>";
			String alertAction = "EmployeeActivity.action?pType=WR";//&callFrom=APPROVE_MARKS_ENTRY
			
			UserAlerts userAlerts = new UserAlerts(con, uF, CF, request);
			userAlerts.setStrDomain(strDomain);
			userAlerts.setStrEmpId(nAddedBy+"");
			userAlerts.setStrData(alertData);
			userAlerts.setStrAction(alertAction);
			userAlerts.setCurrUserTypeID(nUserTypeId+"");
			userAlerts.setStatus(INSERT_WR_ALERT);
			Thread t = new Thread(userAlerts);
			t.run();
			
			
			Notifications nF = new Notifications(N_EMPLOYEE_ONBOARDED_BY_SELF, CF);
			nF.setDomain(strDomain);
			nF.request = request;
			nF.setStrEmpId(getEmpId());
			nF.setStrHrName(empName);
			nF.setStrEmpEmail(strUserEmail);
			nF.setStrEmailTo(strUserEmail);
			nF.setStrHostAddress(CF.getStrEmailLocalHost());
			nF.setStrHostPort(CF.getStrHostPort());
			nF.setStrContextPath(request.getContextPath());
			nF.setEmailTemplate(true);
			nF.sendNotifications();
			
	//===end parvez date: 26-08-2022===		
			
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}
	public String getEmpId() {
		return empId;
	}

	public void setEmpId(String empId) {
		this.empId = empId;
	}


	public HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

}
