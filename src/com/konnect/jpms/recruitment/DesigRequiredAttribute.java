package com.konnect.jpms.recruitment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillGender;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class DesigRequiredAttribute extends ActionSupport implements ServletRequestAware, IStatements {
	private static final long serialVersionUID = 1L;
	HttpSession session;
	CommonFunctions CF;

	String strUserType = null;
	String strSessionEmpId = null;
	String strEmpWLocId = null;

	String desigID;
	String type;
	List<FillGender> genderList;
	
	public String execute() throws Exception {
		
		request.setAttribute(PAGE, "/jsp/recruitment/UpdateJobProfile.jsp");
		request.setAttribute(TITLE, "Update Job Profile");
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;

		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);
		strEmpWLocId = (String) session.getAttribute(WLOCATIONID);

//		recruitID = request.getParameter("recruitID");

		getDesigRequiredAttribute(getDesigID());
		return LOAD;
	
	}


	String job_desc_info=null;
	String cand_profile_info=null;

	
private void getDesigRequiredAttribute(String desigID) {
		
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		ResultSet rs = null;
		try {
			StringBuilder data = new StringBuilder();
			
			con=db.makeConnection(con);
			
			pst = con.prepareStatement("select _type,desig_value from desig_attribute where desig_id = ? and (_type=6 or _type=7)");
			pst.setInt(1, uF.parseToInt(desigID));
			rs = pst.executeQuery();
			String age = "";
			String gender = "";
			while(rs.next()) {
				if(rs.getString("_type") != null && rs.getString("_type").equals("6")) {
					gender = rs.getString("desig_value");
				}
				if(rs.getString("_type") != null && rs.getString("_type").equals("7")) {
					age = rs.getString("desig_value");
				}
			}
			rs.close();
			pst.close();
			
			
			pst = con.prepareStatement("select * from designation_details dd, level_details ld where ld.level_id = dd.level_id and dd.designation_id =?");
			pst.setInt(1, uF.parseToInt(desigID));
			rs = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			String strJobDescription = "";
			String strJobProfile = "";
			String strIdealCandidate = "";
			while (rs.next()) {
				strJobDescription = uF.showData(rs.getString("designation_description"), " ");
				strJobProfile = uF.showData(rs.getString("profile"), " ");
				strIdealCandidate = uF.showData(rs.getString("ideal_candidate"), " ");
			}
			rs.close();
			pst.close();
			
			
			genderList=new FillGender().fillGender();
			data.append("<select name=\"gender\" id=\"gender\" style=\"width: 80px;\">" +
					"<option value=\"0\">Any</option>");
			for (int i = 0; i < genderList.size(); i++) {
				if (genderList.get(i).getGenderId().equals(gender)) {
					data.append("<option value=\""+genderList.get(i).getGenderId()+"\" selected=\"selected\">"+genderList.get(i).getGenderName()+"</option>");
				} else {
					data.append("<option value=\""+genderList.get(i).getGenderId()+"\">"+genderList.get(i).getGenderName()+"</option>");
				}
			}
			data.append("</select");
			
			data.append("::::");
			
			data.append("<select name=\"minAge\" id=\"minAge\" style=\"width: 100px;\">" +
					"<option value=\"0\">Select Age</option>");
			for (int i = 0; i <=42; i++) {
				int minAge = 18;
				minAge += i;
				if (minAge == uF.parseToInt(age)) {
					data.append("<option value=\"" + minAge + "\" selected=\"selected\">" + minAge + " Years</option>");
				}else {
					data.append("<option value=\"" + minAge + "\">" + minAge + " Years</option>");
				}
			}
			data.append("</select>");
			strJobDescription = strJobDescription.replaceAll("<p>", "");
			strJobDescription = strJobDescription.replaceAll("</p>", "");
			data.append("::::"+strJobDescription);
			strIdealCandidate = strIdealCandidate.replaceAll("<p>", "");
			strIdealCandidate = strIdealCandidate.replaceAll("</p>", "");
			data.append("::::"+strIdealCandidate);
			
//			System.out.println("data ===>> " + data);
			request.setAttribute("data", data.toString());
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public String getDesigID() {
		return desigID;
	}

	public void setDesigID(String desigID) {
		this.desigID = desigID;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<FillGender> getGenderList() {
		return genderList;
	}

	public void setGenderList(List<FillGender> genderList) {
		this.genderList = genderList;
	}
	
}
