package com.konnect.jpms.recruitment;



import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillEmployee;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IConstants;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class GetSelectedCandidateEmployeeList extends ActionSupport implements
		ServletRequestAware,IConstants {

	
	HttpSession session;
	CommonFunctions CF=null;
	private static final long serialVersionUID = 1L;
	
	String recruitID;

	public String getRecruitID() {
		return recruitID;
	}

	public void setRecruitID(String recruitID) {
		this.recruitID = recruitID;
	}

	public String execute() {
		
		session = request.getSession(); 
		CF= (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN; 
		

		/*boolean isView  = CF.getAccess(session, request, uF);
		if(!isView){
			request.setAttribute(PAGE, PAccessDenied);
			request.setAttribute(TITLE, TAccessDenied);
			return ACCESS_DENIED;
		}*/

        
		UtilityFunctions uF = new UtilityFunctions();

	
		Map<String, String> hmEmpName = getEmployeeName();
	
	if(type.equals("add")){
		employeename=hmEmpName.get(getSelectedEmp());
	
		addEmployee();
	}else if(type.equals("remove")){
	
		removeEmployee();
		setEmployeename("");
	}
	
	request.setAttribute("empname",employeename);
	return SUCCESS;

	}
	
	
private Map<String, String> getEmployeeName() {
		
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		Map<String, String> hmEmpName = null;
		try {
			con=db.makeConnection(con);
			hmEmpName = CF.getEmpNameMap(con, null, null);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			db.closeConnection(con);
		}
		return hmEmpName;
	}
	

	private void removeEmployee() {
		
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rst = null;
		String oldpanel=null;
		try {
			UtilityFunctions uF=new UtilityFunctions();
			
			con=db.makeConnection(con);
			
			pst=con.prepareStatement("select panel_employee_id from recruitment_details where recruitment_id=?");
			pst.setInt(1,uF.parseToInt(getRecruitID()));
			rst=pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while(rst.next()){
				oldpanel=rst.getString("panel_employee_id");
			}
			rst.close();
			pst.close();
			
			String[] temp=oldpanel.split(",");
			String newpanel="";
			
			for(int i=0;i<temp.length;i++){
			if(!temp[i].equals(getSelectedEmp()))
			newpanel+=temp[i]+",";
			}
			pst = con.prepareStatement("update recruitment_details set panel_employee_id= ? where recruitment_id= ? ");
			pst.setString(1,newpanel);
			pst.setInt(2, uF.parseToInt(getRecruitID()));
			pst.executeUpdate();
			pst.close();
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	String type;
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	String employeename;
	public String getEmployeename() {
		return employeename;
	}

	public void setEmployeename(String employeename) {
		this.employeename = employeename;
	}
	
	String selectedEmp;

	public String getSelectedEmp() {
		return selectedEmp;
	}

	public void setSelectedEmp(String selectedEmp) {
		this.selectedEmp = selectedEmp;
	}

	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		// TODO Auto-generated method stub
		this.request = request;

	}
	
private void addEmployee() {
		
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rst = null;
		String newpanel;
		
		try {		
			UtilityFunctions uF=new UtilityFunctions();
			con=db.makeConnection(con);
			
			String existingpanel=null;
			pst=con.prepareStatement("select panel_employee_id from recruitment_details where recruitment_id=?");
			pst.setInt(1,uF.parseToInt(getRecruitID()));
			rst=pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while(rst.next()){
				existingpanel=rst.getString("panel_employee_id");
			}
			rst.close();
			pst.close();
			
			if(existingpanel==null){
				newpanel=","+getSelectedEmp()+",";
				}else if(existingpanel.equals("MODIFY")){
					newpanel=","+getSelectedEmp()+",";  
				}else{
					newpanel=existingpanel+getSelectedEmp()+",";
				}
			
			
			pst = con.prepareStatement("update recruitment_details set panel_location= ? ,panel_level= ? , "
							+ "panel_designation= ? , panel_grade= ? , panel_employee_id= ? "
							+" where recruitment_id= ? ");
			pst.setString(1, getStrlocation());
			pst.setString(2, getStrlevel());
			pst.setString(3, getStrdesignation());
			pst.setString(4, getStrgrade());
			pst.setString(5, newpanel);
			pst.setInt(6, uF.parseToInt(getRecruitID()));
			pst.executeUpdate();
			pst.close();
			
	}catch(Exception e){
		e.printStackTrace();
	}finally{
		
		db.closeResultSet(rst);
		db.closeStatements(pst);
		db.closeConnection(con);
	}
}

String strlocation;
String strlevel;
String strdesignation;
String strgrade;
String strjobcode;

public String getStrlocation() {
	return strlocation;
}

public void setStrlocation(String strlocation) {
	this.strlocation = strlocation;
}

public String getStrlevel() {
	return strlevel;
}

public void setStrlevel(String strlevel) {
	this.strlevel = strlevel;
}

public String getStrdesignation() {
	return strdesignation;
}

public void setStrdesignation(String strdesignation) {
	this.strdesignation = strdesignation;
}

public String getStrgrade() {
	return strgrade;
}

public void setStrgrade(String strgrade) {
	this.strgrade = strgrade;
}

public String getStrjobcode() {
	return strjobcode;
}

public void setStrjobcode(String strjobcode) {
	this.strjobcode = strjobcode;
}



}
     