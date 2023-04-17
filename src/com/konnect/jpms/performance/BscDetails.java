package com.konnect.jpms.performance;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class BscDetails extends ActionSupport implements ServletRequestAware, IStatements {
	private static final long serialVersionUID = -4581306410273940249L;
	HttpSession session;
	CommonFunctions CF;
	
	String strSessionEmpId;
	String strEmpOrgId;
	String strUserType;
	
	String strBscId;
	
	public String execute() {
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		strSessionEmpId = (String) session.getAttribute(EMPID);
		strEmpOrgId = (String) session.getAttribute(ORGID);
		strUserType = (String) session.getAttribute(USERTYPE);
		
		request.setAttribute(PAGE, "/jsp/performance/BscDetails.jsp");
		request.setAttribute(TITLE, "BscDeatils"); //TKRAs
		UtilityFunctions uF = new UtilityFunctions();
		
		getBSCDetails(uF);
		
		return LOAD;
	}
	
	
	private void getBSCDetails(UtilityFunctions uF){
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try{
			con = db.makeConnection(con);
		
			String bscPerspectiveIds = null;
			List<String> alBSCDetails = new ArrayList<String>();
			pst=con.prepareStatement("select * from bsc_details where bsc_id = ?");
			pst.setInt(1, uF.parseToInt(getStrBscId()));
			rs = pst.executeQuery();
			while(rs.next()) {
				alBSCDetails.add(uF.showData(rs.getString("bsc_name"), ""));
				alBSCDetails.add(uF.showData(rs.getString("bsc_vision"), ""));
				alBSCDetails.add(uF.showData(rs.getString("bsc_mission"), ""));
				bscPerspectiveIds = rs.getString("bsc_perspective_ids");
			}
			rs.close();
			pst.close();
			request.setAttribute("alBSCDetails", alBSCDetails);
			
			String[] perspective = bscPerspectiveIds.split(",");
			
			Map<String, List<String>> hmBSCPerspectives = new LinkedHashMap<String,List<String>>();
			for(int i = 0 ;i < perspective.length; i++ ){
				if(perspective[i] != null && perspective[i].length() > 0) {
					pst=con.prepareStatement("select * from  bsc_perspective_details where bsc_perspective_id = ?");
					pst.setInt(1,uF.parseToInt(perspective[i]));
//					System.out.println("pst123===>"+pst);
					rs = pst.executeQuery();
					List<String> innerList = new ArrayList<String>();
					while(rs.next()) {
						innerList.add(rs.getString("bsc_perspective_name"));
						innerList.add(rs.getString("weightage"));
						innerList.add(rs.getString("perspective_description"));
						innerList.add(rs.getString("perspective_color"));
					}
					rs.close();
					pst.close();
					hmBSCPerspectives.put(perspective[i], innerList);
				}
			}
			request.setAttribute("hmBSCPerspectives", hmBSCPerspectives);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);			
		}
	}
	
//	public void getBscData(UtilityFunctions uF) {
//		Connection con = null;
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		Database db = new Database();
//		db.setRequest(request);
//		Map<String,String> hmBscNames = new LinkedHashMap<String,String>();
//		//List<String> empList = new ArrayList<String>();
//		double achievedBsc = 50.0d;
//		double remainBsc = 50.0d;
//		
//		try {
//			con = db.makeConnection(con);
//			pst=con.prepareStatement("select * from  bsc_details where bsc_id is not null");
//			rs = pst.executeQuery();
//			while (rs.next()) {
//				String bscId = Integer.toString(rs.getInt("bsc_id"));
//				String bscName = rs.getString("bsc_name");
//				hmBscNames.put(bscId, bscName);
//			}
//		}catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			db.closeResultSet(rs);
//			db.closeStatements(pst);
//			db.closeConnection(con);			
//		}
//		
//		StringBuilder sbTotalBscs = new StringBuilder();
//			sbTotalBscs.append("{'Bsc':'Achieved', 'BscAmt': " + uF.parseToDouble(uF.formatIntoOneDecimalWithOutComma(achievedBsc))
//					+ "},");
//			sbTotalBscs.append("{'Bsc':'Missed', 'BscAmt': " + uF.parseToDouble(uF.formatIntoOneDecimalWithOutComma(remainBsc)) + "},");
//
//		request.setAttribute("BscNames", hmBscNames);
//		request.setAttribute("sbTotalBscs", sbTotalBscs);
//			
//	}
	

	public String getStrBscId() {
		return strBscId;
	}

	public void setStrBscId(String strBscId) {
		this.strBscId = strBscId;
	}



	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}
}
	