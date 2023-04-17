package com.konnect.jpms.performance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;

public class CorporateGoalNameList implements ServletRequestAware, IStatements {

	public HttpSession session;
	String strSessionEmpId;
	String strEmpOrgId;
	String strUserType;
	String strBaseUserType;
	String strUserTypeId;
	public CommonFunctions CF;

//	private List<FillOrganisation> organisationList;
//	private String strOrg;
	private String f_org;
	
	private String dataType;
	private String currUserType;
	
	public String execute() {
		session = request.getSession();
		strSessionEmpId = (String) session.getAttribute(EMPID);
		strEmpOrgId = (String) session.getAttribute(ORGID);
		strUserType = (String) session.getAttribute(USERTYPE);
		strBaseUserType = (String) session.getAttribute(BASEUSERTYPE);
		strUserTypeId = (String) session.getAttribute(USERTYPEID);

		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null) 
			return "login";
		request.setAttribute(PAGE, "/jsp/performance/GoalSummary.jsp");
		request.setAttribute(TITLE, "Goals");
		UtilityFunctions uF = new UtilityFunctions();
	
		request.setAttribute("DOC_RETRIVE_LOCATION", CF.getStrDocRetriveLocation());
		
		
		if(getDataType() == null || getDataType().trim().equals("") || getDataType().trim().equalsIgnoreCase("NULL")) {
			setDataType("L");
		}
		if(getCurrUserType()==null && strUserType != null && strUserType.equals(MANAGER)) {
			setCurrUserType("MYTEAM");
		}
		getCorporateDetails(uF);
		
		return LOAD ;

	}	


	private void getCorporateDetails(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			Map<String,List<String>> hmCorporate = new LinkedHashMap<String, List<String>>();
			Map<String,String> hmAttribute = CF.getAttributeMap(con);
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from goal_details where goal_type="+CORPORATE_GOAL+" ");
			if(strUserType != null && strUserType.equals(MANAGER) && getCurrUserType() != null && !getCurrUserType().equals(strBaseUserType)) {
				sbQuery.append(" and emp_ids like '%,"+strSessionEmpId+",%' ");
			} else {
				sbQuery.append(" and org_id = "+uF.parseToInt(getF_org())+" ");
			}
			if(getDataType() != null && getDataType().equals("L")) {
				sbQuery.append(" and is_close = false ");
			} else if(getDataType() != null && getDataType().equals("C")) {
				sbQuery.append(" and is_close = true ");
			}
			sbQuery.append(" order by goal_id desc");
			pst = con.prepareStatement(sbQuery.toString());
//			pst.setInt(1, uF.parseToInt(getStrOrg()));
			rs = pst.executeQuery();
//			System.out.println("pst ===> "+pst);
			while (rs.next()) {
				
				List<String> cinnerList=new ArrayList<String>();
				cinnerList.add(rs.getString("goal_id")); //0
				cinnerList.add(rs.getString("goal_type")); //1
				cinnerList.add(rs.getString("goal_parent_id")); //2
				cinnerList.add(rs.getString("goal_title")); //3
				cinnerList.add(rs.getString("goal_objective")); //4
				cinnerList.add(uF.showData(hmAttribute.get(rs.getString("goal_attribute")), "")); //5
				cinnerList.add(uF.getDateFormat(rs.getString("effective_date"), DBDATE, CF.getStrReportDateFormat())); //6
				cinnerList.add(uF.getDateFormat(rs.getString("due_date"), DBDATE, CF.getStrReportDateFormat())); //7
				String priority="";
				String pClass = "";
				if(rs.getString("priority")!=null && !rs.getString("priority").equals("")) {
					if(rs.getString("priority").equals("1")) {
						pClass = "high";
						priority="High";
					} else if(rs.getString("priority").equals("2")) {
						pClass = "medium";
						priority="Medium";
					} else if(rs.getString("priority").equals("3")) {
						pClass = "low";
						priority="Low";
					}
				}
				cinnerList.add(priority); //8
				cinnerList.add(pClass); //9
				cinnerList.add(rs.getString("is_close")); //10
				cinnerList.add(hmEmpName.get(rs.getString("user_id"))); //11
				cinnerList.add(uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat())); //12
				
				hmCorporate.put(rs.getString("goal_id"), cinnerList);
			}
			rs.close();
			pst.close();
			request.setAttribute("hmCorporate", hmCorporate);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	public String getF_org() {
		return f_org;
	}

	public void setF_org(String f_org) {
		this.f_org = f_org;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getCurrUserType() {
		return currUserType;
	}

	public void setCurrUserType(String currUserType) {
		this.currUserType = currUserType;
	}

	
	public HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {

		this.request = request;
	}
	
}
