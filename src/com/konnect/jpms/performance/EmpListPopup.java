package com.konnect.jpms.performance;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class EmpListPopup extends ActionSupport  implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = -6145081837111360463L;
	HttpSession session;
	String strSessionEmpId;
	String strSessionUserType;
	CommonFunctions CF;
	private String id;
	private String empID;
	
	public String execute() {
		session = request.getSession();
		strSessionEmpId = (String) session.getAttribute(EMPID);
		strSessionUserType = (String) session.getAttribute(USERTYPE);
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		
		request.setAttribute(PAGE, "/jsp/performance/EmpListPopup.jsp");
		request.setAttribute(TITLE, "Employee");
		getEmpList();
		return SUCCESS;
	}

	public void getEmpList(){
	
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		UtilityFunctions uF=new UtilityFunctions();
		
		try {
			con = db.makeConnection(con);
		
			Map<String,String> hmEmpName=CF.getEmpNameMap(con, null,null);
			pst=con.prepareStatement("select emp_image,emp_per_id from employee_personal_details ");
			rs=pst.executeQuery();
			Map<String,String> empImageMap=new HashMap<String,String>();
			while(rs.next()){
				empImageMap.put(rs.getString("emp_per_id"),rs.getString("emp_image"));
			}
			rs.close();
			pst.close();
			
			String emp=getEmpID().substring(0, getEmpID().length()-1);
			List<String> templist = Arrays.asList(emp.split(","));
			
			List<String> emplist = new ArrayList<String>();
			
			for(int i=0;templist!=null && !templist.isEmpty() && i<templist.size();i++) {
				String strImage = "";
				if(CF.getStrDocRetriveLocation()==null) { 
					strImage = "<img class=\"img-circle lazy\" border=\"0\" height=\"24\" width=\"24\" src=\""+DOCUMENT_LOCATION + uF.showData(empImageMap.get(templist.get(i).trim()), "avatar_photo.png")+"\" />";
			  	} else if(empImageMap.get(templist.get(i).trim())!=null && !empImageMap.get(templist.get(i).trim()).equals("") && !empImageMap.get(templist.get(i).trim()).equals("avatar_photo.png")) {
//			  		strImage = "<img class=\"img-circle lazy\" border=\"0\" height=\"24\" width=\"24\" src=\""+CF.getStrDocRetriveLocation() +I_PEOPLE+"/"+I_IMAGE+"/"+templist.get(i).trim()+"/"+I_60x60+"/"+uF.showData(empImageMap.get(templist.get(i).trim()), "avatar_photo.png")+"\" />";
			  		File f = new File(CF.getStrDocSaveLocation()+I_PEOPLE+"/"+I_IMAGE+"/"+templist.get(i).trim()+"/"+I_60x60+"/"+uF.showData(empImageMap.get(templist.get(i).trim()), "avatar_photo.png"));
			        if (f.exists()) {
			        	strImage = "<img class=\"img-circle lazy\" border=\"0\" height=\"24\" width=\"24\" src=\""+CF.getStrDocRetriveLocation() +I_PEOPLE+"/"+I_IMAGE+"/"+templist.get(i).trim()+"/"+I_60x60+"/"+uF.showData(empImageMap.get(templist.get(i).trim()), "avatar_photo.png")+"\" />";
			        } else {
			        	strImage = "<img class=\"img-circle lazy\" border=\"0\" height=\"24\" width=\"24\" src=\"userImages/avatar_photo.png\" />";
			        }
	            } else {
	            	strImage = "<img class=\"img-circle lazy\" border=\"0\" height=\"24\" width=\"24\" src=\"userImages/avatar_photo.png\" />";
	            }
				emplist.add("<a href=\"javascript:void(0)\" style=\"float: left;\" title=\""+hmEmpName.get(templist.get(i).trim())+"\" onclick=\"getEmpProfile('"+templist.get(i).trim()+"')\" >"+ strImage + "</a>");
//				emplist.add("<a href=\"javascript:void(0)\" onclick=\"getEmpProfile('"+templist.get(i).trim()+"')\" >" +
//						"<img src=\"userImages/"+uF.showData(empImageMap.get(templist.get(i).trim()), "avatar_photo.png")+"\" border=\"0\" height=\"16px\" width=\"16px\" title=\""+hmEmpName.get(templist.get(i).trim())+"\"/>" +
//						"</a>");
			}
			
			request.setAttribute("emplist", emplist);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	
		
	}

	
	public String getEmpID() {
		return empID;
	}

	public void setEmpID(String empID) {
		this.empID = empID;
	}


	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {

		this.request = request;
	}

}
