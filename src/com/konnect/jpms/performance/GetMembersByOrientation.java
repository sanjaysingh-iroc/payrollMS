package com.konnect.jpms.performance;

import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.opensymphony.xwork2.ActionSupport;

public class GetMembersByOrientation extends ActionSupport  implements ServletRequestAware, IStatements {

	HttpSession session;
	String strSessionEmpId;
	String strSessionUserType;
	String strUserTypeId;
	CommonFunctions CF;
   
    private String orientation_id;
    private String operation;
    private String appraisalId;
	public String execute() {
		session = request.getSession();
		strSessionEmpId = (String) session.getAttribute(EMPID);
		strSessionUserType = (String) session.getAttribute(USERTYPE);
		strUserTypeId = (String) session.getAttribute(USERTYPEID);

		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		UtilityFunctions uF = new UtilityFunctions();
		
//		System.out.println("orientation_id ==>"+getOrientation_id());
		if(getAppraisalId()!= null && uF.parseToInt(getAppraisalId()) > 0) {
			getAppraisalData();
		}
		return SUCCESS;
	}
	
	
	
	private void getAppraisalData() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		try {

			con = db.makeConnection(con);
			CF.getOrientationMembers(con, uF, request, getOrientation_id());
			List<String> appraisalData = new ArrayList<String>();
			pst = con.prepareStatement("select * from appraisal_details where appraisal_details_id=?");
			pst.setInt(1, uF.parseToInt(getAppraisalId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				appraisalData.add(uF.showData(rs.getString("appraisal_details_id"), ""));//0
				appraisalData.add(uF.showData(rs.getString("appraisal_name"), ""));//1
				appraisalData.add(uF.showData(rs.getString("oriented_type"), ""));//2
				
				Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
				
				List<String> managerLst = new ArrayList<String>();
				if (rs.getString("supervisor_id") != null && rs.getString("supervisor_id").length() > 0) {
					List<String> manager=Arrays.asList(rs.getString("supervisor_id").split(","));
					for(int i=0;manager!=null && !manager.isEmpty() && i<manager.size();i++) {
						managerLst.add(manager.get(i).trim());
					}
				}
				String managerList = uF.showData(getAppendData11(managerLst, hmEmpName), "");
				
				List<String> hrLst = new ArrayList<String>();
				if (rs.getString("hr_ids") != null && rs.getString("hr_ids").length() > 0) {
					List<String> hr=Arrays.asList(rs.getString("hr_ids").split(","));
					for(int i=0;hr!=null && !hr.isEmpty() && i<hr.size();i++) {
						hrLst.add(hr.get(i).trim());
					}
				}
				String hrList = uF.showData(getAppendData11(hrLst, hmEmpName), "");
				
				List<String> peerLst = new ArrayList<String>();
				if (rs.getString("peer_ids") != null && rs.getString("peer_ids").length() > 0) {
					List<String> peer=Arrays.asList(rs.getString("peer_ids").split(","));
					for(int i=0;peer!=null && !peer.isEmpty() && i<peer.size();i++) {
						peerLst.add(peer.get(i).trim());
					}
				}
				String peerList = uF.showData(getAppendData11(peerLst, hmEmpName), "");
				List<String> otherLst = new ArrayList<String>();
				if (rs.getString("other_ids") != null && rs.getString("other_ids").length() > 0) {
					List<String> other=Arrays.asList(rs.getString("other_ids").split(","));
					for(int i=0;other!=null && !other.isEmpty() && i<other.size();i++) {
						otherLst.add(other.get(i).trim());
					}
				}
				String otherList = uF.showData(getAppendData11(otherLst, hmEmpName), "");
				
				appraisalData.add(uF.showData(managerList, "")); //3
				appraisalData.add(uF.showData(hrList, "")); //4
				appraisalData.add(uF.showData(peerList, "")); //5
				appraisalData.add(uF.showData(otherList, "")); //6
				if(rs.getInt("oriented_type") == 5) {
					appraisalData.add(uF.showData(getAppendDataEmpName(rs.getString("employee_id"), hmEmpName), "")); //7
				} else {
					appraisalData.add(""); //7
				}
				appraisalData.add(uF.showData(rs.getString("hr_ids"), "")); //8
				appraisalData.add(uF.showData(rs.getString("supervisor_id"), "")); //9
				appraisalData.add(uF.showData(rs.getString("peer_ids"), "")); //10
				appraisalData.add(uF.showData(rs.getString("other_ids"), "")); //11
				
				List<String> ceoLst = new ArrayList<String>();
				if (rs.getString("ceo_ids") != null && rs.getString("ceo_ids").length() > 0) {
					List<String> ceo=Arrays.asList(rs.getString("ceo_ids").split(","));
					for(int i=0;ceo!=null && !ceo.isEmpty() && i<ceo.size();i++) {
						ceoLst.add(ceo.get(i).trim());
					}
				}
				String ceoList = uF.showData(getAppendData11(ceoLst, hmEmpName), "");
				
				List<String> hodLst = new ArrayList<String>();
				if (rs.getString("hod_ids") != null && rs.getString("hod_ids").length() > 0) {
					List<String> hod=Arrays.asList(rs.getString("hod_ids").split(","));
					for(int i=0;hod!=null && !hod.isEmpty() && i<hod.size();i++) {
						hodLst.add(hod.get(i).trim());
					}
				}
				String hodList = uF.showData(getAppendData11(hodLst, hmEmpName), "");
				
				appraisalData.add(uF.showData(ceoList, "")); //12
				appraisalData.add(uF.showData(hodList, "")); //13
				appraisalData.add(uF.showData(rs.getString("ceo_ids"), "")); //14
				appraisalData.add(uF.showData(rs.getString("hod_ids"), "")); //15
				appraisalData.add(uF.showData(rs.getString("employee_id"), "")); //16
			//	setHideSelfFillEmpIds(uF.showData(rs.getString("employee_id"), ""));
				
				
			}
			rs.close();
			pst.close();
			
			request.setAttribute("appraisalData", appraisalData);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private String getAppendDataEmpName(String strEmpIds, Map<String, String> hmEmpName) {
		StringBuilder sbEmpName = null;
//		Map<String, String> hmDesignation = CF.getEmpDesigMap();
//		Map<String, String> hmEmpCode = CF.getEmpCodeMap();
		UtilityFunctions uF = new UtilityFunctions();
		if (strEmpIds != null) {
			List<String> strID = Arrays.asList(strEmpIds.split(","));
			for (int i = 0; i < strID.size(); i++) {
				if(uF.parseToInt(strID.get(i)) > 0) {
					if (sbEmpName == null) {
						sbEmpName = new StringBuilder();
						sbEmpName.append(hmEmpName.get(strID.get(i))+" <a onclick=\"removeRevieweeForSelfReview('"+strID.get(i)+"');\" href=\"javascript: void(0);\">X</a>");
					} else {
						sbEmpName.append(", " + hmEmpName.get(strID.get(i))+" <a onclick=\"removeRevieweeForSelfReview('"+strID.get(i)+"');\" href=\"javascript: void(0);\">X</a>");
					}
				}
			}
		} else {
			return null;
		}
		return sbEmpName.toString();
	}
	
	private String getAppendData11(List<String> strID, Map<String, String> mp) {
		StringBuilder sb = new StringBuilder();
//		Map<String, String> hmDesignation = CF.getEmpDesigMap();
//		Map<String, String> hmEmpCode = CF.getEmpCodeMap();
		if (strID != null) {
			int cnt=0;
			for (int i = 0; i < strID.size(); i++) {
				if(strID.get(i) != null && !strID.get(i).equals("")) {
					if (cnt == 0) {
						sb.append(mp.get(strID.get(i)));
						cnt++;
					} else {
						sb.append(", " + mp.get(strID.get(i)));
					}
				}
			}
		} else {
			return null;
		}
		return sb.toString();
	}
	
	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public String getOrientation_id() {
		return orientation_id;
	}

	public void setOrientation_id(String orientation_id) {
		this.orientation_id = orientation_id;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getAppraisalId() {
		return appraisalId;
	}

	public void setAppraisalId(String appraisalId) {
		this.appraisalId = appraisalId;
	}
	
	
}
