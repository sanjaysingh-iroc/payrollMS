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

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.SessionAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;

public class EditMyReviewSectionAndSubsection implements ServletRequestAware, SessionAware,
		IStatements {
	Map session;
	CommonFunctions CF;

	String strUserType = null;
	String strSessionEmpId = null;

	private String id;
	private String SID;
	private String sectionID;
	private String sNO;
	private String type;
	private String oreinteId;
	private String sectionName;
	private String totWeightage;
	private String appFreqId;
	private String fromPage;
	public String execute() {
		strUserType = (String) session.get(USERTYPE);
		strSessionEmpId = (String) session.get(EMPID);

		CF = (CommonFunctions) session.get(CommonFunctions);
		if (CF == null)
			return "login";
  
		UtilityFunctions uF = new UtilityFunctions();
//		request.setAttribute(PAGE, "/jsp/performance/EditAppraisal.jsp");
//		request.setAttribute(TITLE, "Edit Appraisal");
//		System.out.println("type java==>"+getType());
		getOrientationValue(uF.parseToInt(getOreinteId()));
		if(type!= null && type.equals("section")){
			getSectionData(uF);
			getSelectedOrientationPosition(uF);
		}else{
			getSubSectionData(uF); 
			getOtherAnsType(uF);
		}
		String submit = request.getParameter("submit");
			if (submit != null && submit.equals("Save")) {
				if(type!= null && type.equals("section")){
				editSection(uF);
				return "success";
				}else{
					editSubSection(uF);
					return "success";
				}
			}
		return LOAD;
	}

	
	private void getOtherAnsType(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;

		try {
			StringBuilder sb = new StringBuilder("");

			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from appraisal_answer_type");
			rs = pst.executeQuery();
			while (rs.next()) {
				if (uF.parseToInt(rs.getString("appraisal_answer_type_id")) == 9) {
					sb.append("<option value=\""
							+ rs.getString("appraisal_answer_type_id")
							+ "\" selected>"
							+ rs.getString("appraisal_answer_type_name")
							+ "</option>");
				} else {
					sb.append("<option value=\""
							+ rs.getString("appraisal_answer_type_id") + "\">"
							+ rs.getString("appraisal_answer_type_name")
							+ "</option>");
				}
			}
			rs.close();
			pst.close();
			
			request.setAttribute("anstype", sb.toString());

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	public void getSelectedOrientationPosition(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		Map<String, Map<String, String>> hmOrientPosition = new LinkedHashMap<String, Map<String,String>>();
		Map<String, String> orientPosition = new HashMap<String, String>();
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from appraisal_main_level_details");
//			pst.setInt(1, uF.parseToInt(getMain_level_id()));
			rs = pst.executeQuery();
			while (rs.next()) {
				orientPosition = hmOrientPosition.get(rs.getString("main_level_id"));
				if(orientPosition == null)orientPosition = new HashMap<String, String>();
				orientPosition.put("HR", rs.getString("hr"));
				orientPosition.put("Manager", rs.getString("manager"));
				orientPosition.put("Self", rs.getString("self"));
				orientPosition.put("Peer", rs.getString("peer"));
				orientPosition.put("Client", rs.getString("client"));
				orientPosition.put("Sub-ordinate", rs.getString("subordinate"));
				orientPosition.put("GroupHead", rs.getString("grouphead"));
				orientPosition.put("Vendor", rs.getString("vendor"));
				orientPosition.put("CEO", rs.getString("ceo"));
				orientPosition.put("HOD", rs.getString("hod"));
				orientPosition.put("Other Peer", rs.getString("other_peer"));
				hmOrientPosition.put(rs.getString("main_level_id"), orientPosition);
			}
			rs.close();
			pst.close();
//			System.out.println("orientPosition ::: "+orientPosition);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		request.setAttribute("hmOrientPosition",hmOrientPosition);
	}
	
	private void getSectionData(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
//		UtilityFunctions uF = new UtilityFunctions();
		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from appraisal_attribute");
			Map<String, String> hmAttributeName = new HashMap<String, String>();
			rs = pst.executeQuery();
			while (rs.next()) {
				hmAttributeName.put(rs.getString("arribute_id"), rs.getString("attribute_name"));
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from appraisal_main_level_details where main_level_id=?");
			pst.setInt(1, uF.parseToInt(getSID()));
			//System.out.println("pst ============ > "+pst);
			rs = pst.executeQuery();
			List<String> sectionList = new ArrayList<String>();
			while (rs.next()) {
				sectionList.add(rs.getString("main_level_id"));
				sectionList.add(rs.getString("level_title"));
				sectionList.add(rs.getString("short_description"));
				sectionList.add(rs.getString("long_description"));
				sectionList.add(rs.getString("appraisal_id"));
				sectionList.add(rs.getString("attribute_id"));
				sectionList.add(rs.getString("section_weightage"));
				sectionList.add(hmAttributeName.get(rs.getString("attribute_id")));
				//System.out.println("mainLevelList1 in java ============ > "+mainLevelList1);
			}
			rs.close();
			pst.close();
			request.setAttribute("sectionList", sectionList);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	private void getSubSectionData(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
//		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("select * from appraisal_level_details where appraisal_level_id=?");
			pst.setInt(1, uF.parseToInt(getSID()));
			//System.out.println("pst ============ > "+pst);
			rs = pst.executeQuery();
			List<String> subSectionList = new ArrayList<String>();
			while (rs.next()) {
				subSectionList.add(rs.getString("appraisal_level_id"));
				subSectionList.add(rs.getString("level_title"));
				subSectionList.add(rs.getString("short_description"));
				subSectionList.add(rs.getString("long_description"));
				subSectionList.add(rs.getString("appraisal_id"));
				subSectionList.add(rs.getString("attribute_id"));
				subSectionList.add(rs.getString("subsection_weightage"));
				//System.out.println("mainLevelList1 in java ============ > "+mainLevelList1);
			}
			rs.close();
			pst.close();
			request.setAttribute("subSectionList", subSectionList);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	private void getOrientationValue(int id) {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;

		try {
			StringBuilder sb = new StringBuilder();
			con = db.makeConnection(con);

			pst = con.prepareStatement("select member_name from orientation_details od,orientation_member om  where orientation_id=? and od.member_id=orientation_member_id");
			pst.setInt(1, id);
			rs = pst.executeQuery();
			int i = 0;
			while (rs.next()) {
				if (i == 0)
					sb.append(rs.getString("member_name"));
				else
					sb.append("," + rs.getString("member_name"));
				i++;
			}
			rs.close();
			pst.close();
			request.setAttribute("member", sb.toString());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	private Map<String, String> getOrientMemberID(Connection con) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String, String> orientationMemberMp = new HashMap<String, String>();

		try {
			
			pst = con.prepareStatement("select * from orientation_member where status=true order by weightage");
			rs = pst.executeQuery();
			while (rs.next()) {
				orientationMemberMp.put(rs.getString("member_name"), rs.getString("member_id"));
			}
			rs.close();
			pst.close();
			
//			System.out.println("memberid=====>"+orientationMemberMp);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return orientationMemberMp;
	}
	
	
	private Map<String, String> getOrientationMember(Connection con) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String, String> orientationMemberMp = new HashMap<String, String>();

		try {

			pst = con
					.prepareStatement("select * from orientation_member where status=true order by weightage");
			rs = pst.executeQuery();
			while (rs.next()) {
				//orientationMemberMp.put(rs.getString("orientation_member_id"),rs.getString("member_name"));
				orientationMemberMp.put(rs.getString("member_id"), rs.getString("member_name"));
			}
			rs.close();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return orientationMemberMp;
	}
	
	
	private void editSection(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
//		UtilityFunctions uF = new UtilityFunctions();
		try {
			String levelTitle = request.getParameter("levelTitle");
			String shortDesrciption = request.getParameter("shortDesrciption");
			String longDesrciption = request.getParameter("longDesrciption");
			String attribute = request.getParameter("attribute");
			String sectionWeightage = request.getParameter("sectionWeightage");
//			System.out.println("HR workflow :: "+request.getParameter(orientationMemberMp.get(hmOrientMemberID.get("HR"))));
//			System.out.println("REQ HR workflow :: "+request.getParameter("HR"));
			con = db.makeConnection(con);
			Map<String, String> orientationMemberMp = getOrientationMember(con);
			Map<String, String> hmOrientMemberID = getOrientMemberID(con);
			pst = con.prepareStatement("update appraisal_main_level_details set level_title =?,short_description=?,long_description=?,attribute_id=?," +
				"section_weightage=?,added_by = ?,entry_date=?,hr=?,manager=?,peer=?,self=?,subordinate=?,grouphead=?,vendor=?,client=?,ceo=?," +
				"hod=?,other_peer=? where main_level_id=?");
			pst.setString(1, uF.showData(levelTitle, ""));
			pst.setString(2, uF.showData(shortDesrciption, ""));
			pst.setString(3, uF.showData(longDesrciption, ""));
			pst.setInt(4, uF.parseToInt(attribute));
			pst.setString(5, uF.showData(sectionWeightage, ""));
			pst.setInt(6, uF.parseToInt(strSessionEmpId));
			pst.setTimestamp(7, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone()) + "" + uF.getCurrentTime(CF.getStrTimeZone()), DBDATE + DBTIME));
			if (hmOrientMemberID.get("HR") != null && request.getParameter(orientationMemberMp.get(hmOrientMemberID.get("HR"))) != null) {
				pst.setInt(8,uF.parseToInt((String) request.getParameter(orientationMemberMp.get(hmOrientMemberID.get("HR")))));
			} else {
				pst.setInt(8, 0);
			}
			if (hmOrientMemberID.get("Manager") != null && request.getParameter(orientationMemberMp.get(hmOrientMemberID.get("Manager"))) != null) {
				pst.setInt(9, uF.parseToInt((String) request.getParameter(orientationMemberMp.get(hmOrientMemberID.get("Manager")))));
			} else {
				pst.setInt(9, 0);
			}
			if (hmOrientMemberID.get("Peer") != null && request.getParameter(orientationMemberMp.get(hmOrientMemberID.get("Peer"))) != null) {
				pst.setInt(10,uF.parseToInt((String) request.getParameter(orientationMemberMp.get(hmOrientMemberID.get("Peer")))));
			} else {
				pst.setInt(10, 0);
			}
			if (hmOrientMemberID.get("Self") != null && request.getParameter(orientationMemberMp.get(hmOrientMemberID.get("Self"))) != null) {
				pst.setInt(11,uF.parseToInt((String) request.getParameter(orientationMemberMp.get(hmOrientMemberID.get("Self")))));
			} else {
				pst.setInt(11, 0);
			}
			if (hmOrientMemberID.get("Sub-ordinate") != null && request.getParameter(orientationMemberMp.get(hmOrientMemberID.get("Sub-ordinate"))) != null) {
				pst.setInt(12,uF.parseToInt((String) request.getParameter(orientationMemberMp.get(hmOrientMemberID.get("Sub-ordinate")))));
			} else {
				pst.setInt(12, 0);
			}
			if (hmOrientMemberID.get("GroupHead") != null && request.getParameter(orientationMemberMp.get(hmOrientMemberID.get("GroupHead"))) != null) {
				pst.setInt(13,uF.parseToInt((String) request.getParameter(orientationMemberMp.get(hmOrientMemberID.get("GroupHead")))));
			} else {
				pst.setInt(13, 0);
			}
			if (hmOrientMemberID.get("Vendor") != null && request.getParameter(orientationMemberMp.get(hmOrientMemberID.get("Vendor"))) != null) {
				pst.setInt(14,uF.parseToInt((String) request.getParameter(orientationMemberMp.get(hmOrientMemberID.get("Vendor")))));
			} else {
				pst.setInt(14, 0);
			}
			if (hmOrientMemberID.get("Client") != null && request.getParameter(orientationMemberMp.get(hmOrientMemberID.get("Client"))) != null) {
				pst.setInt(15,uF.parseToInt((String) request.getParameter(orientationMemberMp.get(hmOrientMemberID.get("Client")))));
			} else {
				pst.setInt(15, 0);
			}
			
			if (hmOrientMemberID.get("CEO") != null && request.getParameter(orientationMemberMp.get(hmOrientMemberID.get("CEO"))) != null) {
				pst.setInt(16,uF.parseToInt((String) request.getParameter(orientationMemberMp.get(hmOrientMemberID.get("CEO")))));
			} else {
				pst.setInt(16, 0);
			}
			
			if (hmOrientMemberID.get("HOD") != null && request.getParameter(orientationMemberMp.get(hmOrientMemberID.get("HOD"))) != null) {
				pst.setInt(17,uF.parseToInt((String) request.getParameter(orientationMemberMp.get(hmOrientMemberID.get("HOD")))));
			} else {
				pst.setInt(17, 0);
			}
			if (hmOrientMemberID.get("Other Peer") != null && request.getParameter(orientationMemberMp.get(hmOrientMemberID.get("Other Peer"))) != null) {
				pst.setInt(18,uF.parseToInt((String) request.getParameter(orientationMemberMp.get(hmOrientMemberID.get("Other Peer")))));
			} else {
				pst.setInt(18, 0);
			}
			pst.setInt(19, uF.parseToInt(getSID()));
			//System.out.println("pst ============ > "+pst);
			pst.executeUpdate();
			
			
			
			StringBuilder sbQuesLevel = null;
			pst = con.prepareStatement("select appraisal_level_id from appraisal_level_details where main_level_id = ?");
			pst.setInt(1, uF.parseToInt(getSID()));
			
			rs = pst.executeQuery();
			while(rs.next()) {
				if(rs.getString("appraisal_level_id")!= null && rs.getInt("appraisal_level_id") > 0) {
					if(sbQuesLevel == null) {
						sbQuesLevel = new StringBuilder();
						sbQuesLevel.append(rs.getString("appraisal_level_id"));
					} else {
						sbQuesLevel.append(","+rs.getString("appraisal_level_id"));
					}
				}
			}
			rs.close();
			pst.close();
			
			if(sbQuesLevel == null){
				sbQuesLevel = new StringBuilder();
			}
//			System.out.println("==>sbQuesLevel==>"+sbQuesLevel);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	
	private void editSubSection(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
//		UtilityFunctions uF = new UtilityFunctions();
		try {
			String subsectionname = request.getParameter("subsectionname");
			String subsectionDescription = request.getParameter("subsectionDescription");
			String subsectionLongDescription = request.getParameter("subsectionLongDescription");
			String subSectionWeightage = request.getParameter("subSectionWeightage");
			
			con = db.makeConnection(con);
			pst = con.prepareStatement("update appraisal_level_details set level_title =?,short_description=?," +
					"long_description=?,subsection_weightage=?,added_by = ?,entry_date=? where appraisal_level_id=?");
			pst.setString(1, uF.showData(subsectionname, ""));
			pst.setString(2, uF.showData(subsectionDescription, ""));
			pst.setString(3, uF.showData(subsectionLongDescription, ""));
			pst.setString(4, uF.showData(subSectionWeightage, ""));
			pst.setInt(5, uF.parseToInt(strSessionEmpId));
			pst.setTimestamp(6, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone()) + "" + uF.getCurrentTime(CF.getStrTimeZone()), DBDATE + DBTIME));
			pst.setInt(7, uF.parseToInt(getSID()));
			pst.executeUpdate();
			
			pst = con.prepareStatement("update appraisal_main_level_details set added_by = ?, entry_date=? where main_level_id = ?");
			pst.setInt(1, uF.parseToInt(strSessionEmpId));
			pst.setTimestamp(2, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone()) + "" + uF.getCurrentTime(CF.getStrTimeZone()), DBDATE + DBTIME));
			pst.setInt(3, uF.parseToInt(getSectionID()));
			pst.executeUpdate();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSID() {
		return SID;
	}

	public void setSID(String sID) {
		SID = sID;
	}

	public String getsNO() {
		return sNO;
	}

	public void setsNO(String sNO) {
		this.sNO = sNO;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getOreinteId() {
		return oreinteId;
	}

	public void setOreinteId(String oreinteId) {
		this.oreinteId = oreinteId;
	}

	public String getSectionName() {
		return sectionName;
	}

	public void setSectionName(String sectionName) {
		this.sectionName = sectionName;
	}

	public String getTotWeightage() {
		return totWeightage;
	}
 
	public void setTotWeightage(String totWeightage) {
		this.totWeightage = totWeightage;
	}

	public String getSectionID() {
		return sectionID;
	}

	public void setSectionID(String sectionID) {
		this.sectionID = sectionID;
	}


	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {

		this.request = request;
	}

	@Override
	public void setSession(Map session) {
		this.session = session;
	}


	public String getAppFreqId() {
		return appFreqId;
	}


	public void setAppFreqId(String appFreqId) {
		this.appFreqId = appFreqId;
	}


	public String getFromPage() {
		return fromPage;
	}


	public void setFromPage(String fromPage) {
		this.fromPage = fromPage;
	}

	
}
