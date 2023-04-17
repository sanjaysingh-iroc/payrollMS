package com.konnect.jpms.recruitment;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FilenameUtils;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class ViewCandidateResume extends ActionSupport implements ServletRequestAware, ServletResponseAware, IStatements {

	private static final long serialVersionUID = 1L;

	HttpSession session;
	String strUserType = null;

	String strSessionEmpId = null;

	CommonFunctions CF;
	private String CandID;
	private String userId;
	private String recruitId;
	private String operation;
	
	String candiApplicationId;
	
	public String getRecruitId() {
		return recruitId;
	}

	public void setRecruitId(String recruitId) {
		this.recruitId = recruitId;
	}
	
	public String execute() throws Exception {

		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;

		UtilityFunctions uF = new UtilityFunctions();

		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);

		request.setAttribute("DOC_RETRIVE_LOCATION", CF.getStrDocRetriveLocation());
		
		viewProfile(getCandID(), uF);

		return SUCCESS;
	}




	
	public String viewProfile(String strEmpIdReq, UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		List<List<Object>> alResumes;
		// List<List<String>> alActivityDetails;

		try {
			con = db.makeConnection(con);
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			List<String> availableExt = CF.getAvailableExtention();
			request.setAttribute("availableExt",availableExt);
			
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			request.setAttribute("hmEmpName", hmEmpName);
			Map<String, String> hm = new HashMap<String, String>();

			pst = con.prepareStatement("select * from recruitment_details where recruitment_id=?");
			pst.setInt(1, uF.parseToInt(getRecruitId()));
			rs = pst.executeQuery();
//			System.out.println("pst ===>> " + pst);
//			System.out.println("new Date ===> " + new Date());
			while (rs.next()) {
				hm.put("JOB_CODE", rs.getString("job_code"));
			}
			rs.close();
			pst.close();
//			System.out.println("hm ===>> " + hm);
			
			pst = con.prepareStatement("Select * from candidate_personal_details where emp_per_id=?");
			if (strEmpIdReq != null) {
				pst.setInt(1, uF.parseToInt(strEmpIdReq));
			} else {
				pst.setInt(1, uF.parseToInt(getCandID()));
			}
			rs = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while (rs.next()) {
			
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				request.setAttribute(TITLE, rs.getString("emp_fname") +strEmpMName+ " "+ rs.getString("emp_lname") + "'s Profile");
				
				hm.put("CANDI_ID", rs.getString("emp_per_id"));
				hm.put("NAME", uF.showData(rs.getString("emp_fname"),"") +strEmpMName+ " " + uF.showData(rs.getString("emp_lname"),""));
				hm.put("ADDRESS", uF.showData(rs.getString("emp_address1"),"") + " " +uF.showData(rs.getString("emp_address2"),""));
//				hm.put("CITY", rs.getString("emp_state_id").equals("0") ? "-" : rs.getString("emp_state_id"));
				hm.put("STATE", uF.showData(CF.getStateNameById(con, uF, rs.getString("emp_state_id")), "-"));
				hm.put("COUNTRY", uF.showData(CF.getCountryNameById(con, uF, rs.getString("emp_country_id")), "-"));
				hm.put("TMP_ADDRESS", uF.showData(rs.getString("emp_address1_tmp"),"") + " " +uF.showData(rs.getString("emp_address2_tmp"),""));
//				hm.put("TMP_CITY", rs.getString("emp_state_id").equals("0") ? "-" : rs.getString("emp_state_id"));
				hm.put("TMP_STATE", uF.showData(CF.getStateNameById(con, uF, rs.getString("emp_state_id_tmp")), "-"));
				hm.put("TMP_COUNTRY", uF.showData(CF.getCountryNameById(con, uF, rs.getString("emp_country_id_tmp")), "-"));
				hm.put("PINCODE", rs.getString("emp_pincode"));
				hm.put("CONTACT", rs.getString("emp_contactno"));
				hm.put("CONTACT_MOB", rs.getString("emp_contactno_mob"));
				hm.put("IMAGE", rs.getString("emp_image"));
				hm.put("EMAIL", rs.getString("emp_email"));
				String gStatus = "";
				if(rs.getString("emp_gender") != null && rs.getString("emp_gender").equals("M")){
					gStatus = "Male";
				}else if(rs.getString("emp_gender") != null && rs.getString("emp_gender").equals("F")){
					gStatus = "Female";
				}else{
					gStatus = "-";
				}
				hm.put("GENDER", gStatus);
				hm.put("GENDER_FORSTAR", rs.getString("emp_gender"));
				hm.put("DOB", uF.getDateFormat(rs.getString("emp_date_of_birth"), DBDATE, CF.getStrReportDateFormat()));
				String mStatus = "";
				if(rs.getString("marital_status") != null && rs.getString("marital_status").equals("U")){
					mStatus = "Unmarried";
				}else if(rs.getString("marital_status") != null && rs.getString("marital_status").equals("M")){
					mStatus = "Married";
				}else if(rs.getString("marital_status") != null && rs.getString("marital_status").equals("D")){
					mStatus = "Divorced";
				}else if(rs.getString("marital_status") != null && rs.getString("marital_status").equals("W")){
					mStatus = "Widow";
				}else{
					mStatus = "-";
				}
				hm.put("MARITAL_STATUS", mStatus);
				
				hm.put("isaccepted", rs.getString("application_status"));

				hm.put("SUPER_CODE", rs.getString("empcode"));
				hm.put("SUPER_NAME", rs.getString("emp_fname") +strEmpMName+ " " + rs.getString("emp_lname"));
				hm.put("PASSPORT_NO", rs.getString("passport_no"));
				hm.put("PASSPORT_EXPIRY", uF.getDateFormat(rs.getString("passport_expiry_date"), DBDATE, CF.getStrReportDateFormat()));
				hm.put("AVAILABILITY",  uF.showYesNo(rs.getString("availability_for_interview")));
				
//				System.out.println(rs.getDouble("current_ctc"));
				
				hm.put("CURRENT_CTC", uF.formatIntoOneDecimal(rs.getDouble("current_ctc")));
				hm.put("EXPECTED_CTC", uF.formatIntoOneDecimal(rs.getDouble("expected_ctc")));
				hm.put("NOTICE_PERIOD", uF.showData(rs.getString("notice_period"), "0") +" days");
			}
			rs.close();
			pst.close();
			
			int intEmpIdReq = uF.parseToInt(strEmpIdReq);

			request.setAttribute("myProfile", hm);

			String filePath = request.getRealPath("/userDocuments/");
			alResumes = selectResumes(con, intEmpIdReq, filePath);
			
			System.out.println("alResumes =============>> " + alResumes);
			
			request.setAttribute("alResumes", alResumes);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}

	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	
	public List<List<Object>> selectResumes(Connection con, int empId, String filePath) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String,String> hmCandNameMap = CF.getCandNameMap(con, null,null); 
		List<List<Object>> alResumes = new ArrayList<List<Object>>();
		try {

			pst = con.prepareStatement("SELECT * FROM candidate_documents_details where emp_id = ? and documents_type =? ");
			pst.setInt(1, empId);
			pst.setString(2, "Resume");
			rs = pst.executeQuery();
			
//			System.out.println("new Date ===> " + new Date());
			while (rs.next()) {
				
					ArrayList<Object> alInner1 = new ArrayList<Object>();
					alInner1.add(rs.getInt("documents_id") + "");
					alInner1.add(rs.getString("documents_name"));
					alInner1.add(rs.getString("documents_type"));
					alInner1.add(rs.getInt("emp_id") + "");
		
					File fileName = new File(rs.getString("documents_file_name") != null ? rs.getString("documents_file_name") : "");
					alInner1.add(fileName);
					String extenstion = null;
					if(rs.getString("documents_file_name") !=null && !rs.getString("documents_file_name").trim().equals("")){
						extenstion = FilenameUtils.getExtension(rs.getString("documents_file_name").trim());
					}
					alInner1.add(extenstion);//5
					
					alResumes.add(alInner1);
				
				
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmCandNameMap", hmCandNameMap);

		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if(pst != null) {
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		} 
		return alResumes;
	}
	
	
	public String getCandID() {
		return CandID;
	}

	public void setCandID(String candID) {
		CandID = candID;
	}

	public String getCandiApplicationId() {
		return candiApplicationId;
	}

	public void setCandiApplicationId(String candiApplicationId) {
		this.candiApplicationId = candiApplicationId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}
	
	HttpServletResponse response;
	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
	}

}