package com.konnect.jpms.roster;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

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

public class AddRosterPolicyRules extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	
	CommonFunctions CF;
	HttpSession session;
	
	String strOrg;
	String f_department;
	
	String userscreen;
	String navigationId;
	String toPage;

//	private List<FillGender> empGenderList;
//	private List<FillRosterPolicyRule> rosterPolicyRuleList;
//	private List<FillShift> shiftList;
	
	private String rPolicyRuleId;
//	private String rosterPolicyRuleType;
//	private String shiftName;
//	private String[] multiShiftName;
//	private String noOfDays;
//	private String empGender;
	
	private List<FillOrganisation> organisationList;
	private List<FillLevel> levelList;
	private List<FillWLocation> wLocationList;
	private List<FillDepartment> departList;
	
	private String rosterPolicyRuleName;
	private String minNoofMemberInShift;
	private String minNoofMemberInShiftOnWeekend;
	private String minNoofLeadersInShift;
	private String[] strLevel;
	private String strLevelNames;
	private String minNoofMemberWithLeadersInShift;
	private String noofWeekendOffInMonth;
	private String maxNoofShiftsAssignInMonth;
	private String minNoofDaysBreakInStretchShift;
	private String minNoofDaysWeekOffbetweenChangeShift;
	private String strWLocation;
	private String[] strWLocationCombined;
	private String strWLocationCombinedName;
	private String minNoofMaleMemberInShift;
	private String shiftName1;
	private String shiftName2;
	private String shiftName3;
	private String shiftNameOther;
	
	private String operation;
	private List<FillShift> shiftList;
	
	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		request.setAttribute(PAGE, "/jsp/roster/AddRosterPolicyRules.jsp");
		UtilityFunctions uF = new UtilityFunctions();
			
		if (uF.parseToInt(getStrOrg()) <= 0) {
			setStrOrg((String) session.getAttribute(ORGID));
		}
		
//		String operation = request.getParameter("operation");
		String strId = request.getParameter("ID");
//		System.out.println("strId ===>> " + strId);
		
		if (operation !=null && operation.equals("D")) {
			return deleteRosterPolicyRules(uF, strId);
		}
		
		if (operation!=null && operation.equals("E")) {
			loadRosterPolicyRules(uF);
			return viewRosterPolicyRules(uF, strId);
		}
		
		if (operation!=null && operation.equals("PREVIEW")) {
			loadRosterPolicyRules(uF);
			return previewRosterPolicyRules(uF, strId);
		}
		
		if (getrPolicyRuleId()!=null && getrPolicyRuleId().length()>0) {
			return updateRosterPolicyRules(uF);
		}
		
		if (getRosterPolicyRuleName()!=null && getRosterPolicyRuleName().length()>0) {
			return insertRosterPolicyRules(uF);
		}
		
		return loadRosterPolicyRules(uF);
		
	}
	
	
	private String previewRosterPolicyRules(UtilityFunctions uF, String strId) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			Map<String, String> hmLevelName = CF.getLevelMap(con);
			Map<String, String> hmShiftCode = CF.getShiftMap(con);
			Map<String, String> hmLocationName = CF.getWLocationMap(con, null, null);
			Map<String, String> hmDepartName = CF.getDeptMap(con);
			Map<String, String> hmOrgName = CF.getOrgName(con);
			pst = con.prepareStatement("select * from roster_policy_rules where roster_policy_rule_id=?");
			pst.setInt(1, uF.parseToInt(strId));
			rs = pst.executeQuery();
			while(rs.next()) {
				setrPolicyRuleId(rs.getString("roster_policy_rule_id"));
				setRosterPolicyRuleName(rs.getString("roster_policy_rule_name"));
				setMinNoofMemberInShift(rs.getString("min_no_of_member_in_shift"));
				setMinNoofMemberInShiftOnWeekend(rs.getString("min_no_of_member_in_shift_at_weekend"));
				
				String str1 = rs.getString("no_of_leads_from_levels_for_no_of_member");
				if(str1 != null) {
					String[] strArr = str1.split(":_:");
					if(strArr.length>1) {
						setMinNoofLeadersInShift(strArr[0]);
						if(strArr[1]!=null && !strArr[1].equals("")) {
							String[] arr = strArr[1].split(",");
							StringBuilder sbLevel = null;
							for(int i=0; arr!=null && i<arr.length; i++) {
								if(sbLevel == null) {
									sbLevel = new StringBuilder();
									sbLevel.append(hmLevelName.get(arr[i]));
								} else {
									sbLevel.append(", "+hmLevelName.get(arr[i]));
								}
							}
							setStrLevelNames(sbLevel.toString());
						}
						setMinNoofMemberWithLeadersInShift(strArr[2]);
					}
				}
				
				setNoofWeekendOffInMonth(rs.getString("min_weekend_off_per_month"));
				setMaxNoofShiftsAssignInMonth(rs.getString("max_no_of_shifts_per_member_per_month"));
				setMinNoofDaysBreakInStretchShift(rs.getString("min_break_days_in_stretch_shift"));
				setMinNoofDaysWeekOffbetweenChangeShift(rs.getString("min_days_off_between_shifts"));
				
				String str2 = rs.getString("member_location_associated_locations");
				if(str2 != null && !str2.equals("''")) {
					String[] strArr = str2.split(":_:");
					if(strArr.length>1) {
						setStrWLocation(hmLocationName.get(strArr[0]));
						if(strArr[1]!=null && !strArr[1].equals("")) {
							String[] arr = strArr[1].split(",");
							StringBuilder sbLocation = null;
							for(int i=0; arr!=null && i<arr.length; i++) {
								if(sbLocation == null) {
									sbLocation = new StringBuilder();
									sbLocation.append(hmLocationName.get(arr[i]));
								} else {
									sbLocation.append(", "+hmLocationName.get(arr[i]));
								}
							}
							setStrWLocationCombinedName(sbLocation.toString());
						}
					}
				}
				setMinNoofMaleMemberInShift(rs.getString("min_male_member_in_shift"));
				
				String str3 = rs.getString("rotation_of_shift");
				if(str3 != null && !str3.equals("''")) {
					String[] strArr = str3.split(":_:");
					if(strArr.length>2) {
						setShiftName1(uF.showData(hmShiftCode.get(strArr[0]), "-"));
						setShiftName2(uF.showData(hmShiftCode.get(strArr[1]), "-"));
						setShiftName3(uF.showData(hmShiftCode.get(strArr[2]), "-"));
					}
				}
				setShiftNameOther(hmShiftCode.get(rs.getString("remaining_emp_shift")));
				setF_department(hmDepartName.get(rs.getString("depart_id")));
				setStrOrg(hmOrgName.get(rs.getString("org_id")));
			}
			rs.close();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return LOAD;
	}


	private String insertRosterPolicyRules(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);

			pst = con.prepareStatement("insert into roster_policy_rules (roster_policy_rule_name, min_no_of_member_in_shift, min_no_of_member_in_shift_at_weekend, no_of_leads_from_levels_for_no_of_member, " +
				"min_weekend_off_per_month, max_no_of_shifts_per_member_per_month, min_break_days_in_stretch_shift, min_days_off_between_shifts, member_location_associated_locations, min_male_member_in_shift, " +
				"org_id, entry_date, added_by,rotation_of_shift,remaining_emp_shift,depart_id) values (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?)");
			pst.setString(1, getRosterPolicyRuleName());
			pst.setInt(2, uF.parseToInt(getMinNoofMemberInShift()));
			pst.setInt(3, uF.parseToInt(getMinNoofMemberInShiftOnWeekend()));
			
			StringBuffer sbLevels = null;
			for(int i=0; getStrLevel() != null && i<getStrLevel().length; i++) {
				if(sbLevels == null) {
					sbLevels = new StringBuffer();
					sbLevels.append(getStrLevel()[i]);
				} else {
					sbLevels.append(","+getStrLevel()[i]);
				}
			}
			if(sbLevels == null) {
				sbLevels = new StringBuffer();
			}
			StringBuilder sbData = new StringBuilder();
			if(uF.parseToDouble(getMinNoofLeadersInShift())>0 && uF.parseToDouble(getMinNoofMemberWithLeadersInShift())>0) {
				sbData.append(getMinNoofLeadersInShift() + ":_:" + sbLevels.toString() + ":_:" + getMinNoofMemberWithLeadersInShift());
			}
//			MinNoofLeadersInShift, strLevel, 
			pst.setString(4, sbData.toString());
			pst.setDouble(5, uF.parseToDouble(getNoofWeekendOffInMonth()));
			pst.setInt(6, uF.parseToInt(getMaxNoofShiftsAssignInMonth()));
			pst.setDouble(7, uF.parseToDouble(getMinNoofDaysBreakInStretchShift()));
			pst.setDouble(8, uF.parseToDouble(getMinNoofDaysWeekOffbetweenChangeShift()));
			
			StringBuffer sbWLoc = null;
			for(int i=0; getStrWLocationCombined()!=null && i<getStrWLocationCombined().length; i++) {
				if(sbWLoc == null) {
					sbWLoc = new StringBuffer();
					sbWLoc.append(getStrWLocationCombined()[i]);
				} else {
					sbWLoc.append(","+getStrWLocationCombined()[i]);
				}
			}
			if(sbWLoc == null) {
				sbWLoc = new StringBuffer();
			}
			StringBuilder sbWLocData = new StringBuilder();
			if(uF.parseToDouble(getStrWLocation())>0) {
				sbWLocData.append(getStrWLocation() + ":_:" + sbWLoc.toString());
			}
//			strWLocation, strWLocationCombined
			pst.setString(9, sbWLocData.toString());
			
			pst.setInt(10, uF.parseToInt(getMinNoofMaleMemberInShift()));
			pst.setInt(11, uF.parseToInt(getStrOrg()));
			pst.setDate(12, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(13, uF.parseToInt((String)session.getAttribute(EMPID)));
			pst.setString(14, uF.showData(getShiftName1(), "-")+":_:"+uF.showData(getShiftName2(), "-")+":_:"+uF.showData(getShiftName3(), "-"));
			pst.setString(15, getShiftNameOther());
			pst.setString(16, getF_department());
			pst.executeUpdate();
//			System.out.println("pst ===>> " + pst);
			pst.close();

			session.setAttribute(MESSAGE, SUCCESSM+getRosterPolicyRuleName()+" added successfully."+END);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeStatements(pst);
			db.closeConnection(con);
		}

		return SUCCESS;
	}
	
	
	public String updateRosterPolicyRules(UtilityFunctions uF) {
		
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("update roster_policy_rules set roster_policy_rule_name=?, min_no_of_member_in_shift=?, min_no_of_member_in_shift_at_weekend=?, no_of_leads_from_levels_for_no_of_member=?, " +
				"min_weekend_off_per_month=?, max_no_of_shifts_per_member_per_month=?, min_break_days_in_stretch_shift=?, min_days_off_between_shifts=?, member_location_associated_locations=?, min_male_member_in_shift=?, " +
				"update_date=?, updated_by=?,rotation_of_shift=?,remaining_emp_shift=?,depart_id=?,org_id=? where roster_policy_rule_id=?");
			pst.setString(1, getRosterPolicyRuleName());
			pst.setInt(2, uF.parseToInt(getMinNoofMemberInShift()));
			pst.setInt(3, uF.parseToInt(getMinNoofMemberInShiftOnWeekend()));
			
			StringBuffer sbLevels = null;
			for(int i=0; getStrLevel() != null && i<getStrLevel().length; i++) {
				if(sbLevels == null) {
					sbLevels = new StringBuffer();
					sbLevels.append(getStrLevel()[i]);
				} else {
					sbLevels.append(","+getStrLevel()[i]);
				}
			}
			if(sbLevels == null) {
				sbLevels = new StringBuffer();
			}
			StringBuilder sbData = new StringBuilder();
			if(uF.parseToDouble(getMinNoofLeadersInShift())>0 && uF.parseToDouble(getMinNoofMemberWithLeadersInShift())>0) {
				sbData.append(getMinNoofLeadersInShift() + ":_:" + sbLevels.toString() + ":_:" + getMinNoofMemberWithLeadersInShift());
			}
			pst.setString(4, sbData.toString());
			pst.setDouble(5, uF.parseToDouble(getNoofWeekendOffInMonth()));
			pst.setInt(6, uF.parseToInt(getMaxNoofShiftsAssignInMonth()));
			pst.setDouble(7, uF.parseToDouble(getMinNoofDaysBreakInStretchShift()));
			pst.setDouble(8, uF.parseToDouble(getMinNoofDaysWeekOffbetweenChangeShift()));
			
			StringBuffer sbWLoc = null;
			for(int i=0; getStrWLocationCombined()!=null && i<getStrWLocationCombined().length; i++) {
				if(sbWLoc == null) {
					sbWLoc = new StringBuffer();
					sbWLoc.append(getStrWLocationCombined()[i]);
				} else {
					sbWLoc.append(","+getStrWLocationCombined()[i]);
				}
			}
			if(sbWLoc == null) {
				sbWLoc = new StringBuffer();
			}
			StringBuilder sbWLocData = new StringBuilder();
			if(uF.parseToDouble(getStrWLocation())>0) {
				sbWLocData.append(getStrWLocation() + ":_:" + sbWLoc.toString());
			}
			pst.setString(9, sbWLocData.toString());
			
			pst.setInt(10, uF.parseToInt(getMinNoofMaleMemberInShift()));
			pst.setDate(11, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(12, uF.parseToInt((String)session.getAttribute(EMPID)));
			pst.setString(13, uF.showData(getShiftName1(), "-")+":_:"+uF.showData(getShiftName2(), "-")+":_:"+uF.showData(getShiftName3(), "-"));
			pst.setString(14, getShiftNameOther());
			pst.setString(15, getF_department());
			pst.setInt(16, uF.parseToInt(getStrOrg()));
			pst.setInt(17, uF.parseToInt(getrPolicyRuleId()));
			pst.executeUpdate();
//			System.out.println("pst ===>> " + pst);
			pst.close();
			
			/*pst = con.prepareStatement("update roster_policy_rules set rule_type_id=?, shift_id=?, shift_ids=?, no_of_days=?, gender=?, update_date=?, updated_by=? where roster_policy_rule_id=?");
			pst.setInt(1, uF.parseToInt(getRosterPolicyRuleType()));
			pst.setInt(2, uF.parseToInt(getShiftName()));
			StringBuilder sb = new StringBuilder();
			for(int i=0; getMultiShiftName()!=null && i<getMultiShiftName().length; i++) {
				sb.append(getMultiShiftName()[i]+",");
			}
			pst.setString(3, sb.toString());
			pst.setInt(4, uF.parseToInt(getNoOfDays()));
			pst.setString(5, getEmpGender());
			pst.setDate(6, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(7,  uF.parseToInt((String)session.getAttribute(EMPID)));
			pst.setInt(8,  uF.parseToInt(getrPolicyRuleId()));
			pst.executeUpdate();
			pst.close();*/
			
			session.setAttribute(MESSAGE, SUCCESSM+getRosterPolicyRuleName()+" updated successfully."+END);
			
		} catch (Exception e) {
			e.printStackTrace();			
		}finally{
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;
	}
	
	
	public String viewRosterPolicyRules(UtilityFunctions uF,String strId) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from roster_policy_rules where roster_policy_rule_id=?");
			pst.setInt(1, uF.parseToInt(strId));
			rs = pst.executeQuery();
			while(rs.next()){
				setrPolicyRuleId(rs.getString("roster_policy_rule_id"));
				setRosterPolicyRuleName(rs.getString("roster_policy_rule_name"));
				setMinNoofMemberInShift(rs.getString("min_no_of_member_in_shift"));
				setMinNoofMemberInShiftOnWeekend(rs.getString("min_no_of_member_in_shift_at_weekend"));
				
				String str1 = rs.getString("no_of_leads_from_levels_for_no_of_member");
				if(str1 != null) {
					String[] strArr = str1.split(":_:");
					if(strArr.length>1) {
						setMinNoofLeadersInShift(strArr[0]);
						if(strArr[1]!=null && !strArr[1].equals("")) {
							String[] arr = strArr[1].split(",");
							setStrLevel(arr);
						}
						setMinNoofMemberWithLeadersInShift(strArr[2]);
					}
				}
				
				setNoofWeekendOffInMonth(rs.getString("min_weekend_off_per_month"));
				setMaxNoofShiftsAssignInMonth(rs.getString("max_no_of_shifts_per_member_per_month"));
				setMinNoofDaysBreakInStretchShift(rs.getString("min_break_days_in_stretch_shift"));
				setMinNoofDaysWeekOffbetweenChangeShift(rs.getString("min_days_off_between_shifts"));
				
				String str2 = rs.getString("member_location_associated_locations");
				if(str2 != null && !str2.equals("''")) {
					String[] strArr = str2.split(":_:");
					if(strArr.length>1) {
						setStrWLocation(strArr[0]);
						if(strArr[1]!=null && !strArr[1].equals("")) {
							String[] arr = strArr[1].split(",");
							setStrWLocationCombined(arr);
						}
					}
				}
				setMinNoofMaleMemberInShift(rs.getString("min_male_member_in_shift"));
				String str3 = rs.getString("rotation_of_shift");
				if(str3 != null && !str3.equals("''")) {
					String[] strArr = str3.split(":_:");
					if(strArr.length>2) {
						setShiftName1(strArr[0]);
						setShiftName2(strArr[1]);
						setShiftName3(strArr[2]);
					}
				}
				setShiftNameOther(rs.getString("remaining_emp_shift"));
				setF_department(rs.getString("depart_id"));
				setStrOrg(rs.getString("org_id"));
			}
			rs.close();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return LOAD;
	}
	
	public String deleteRosterPolicyRules(UtilityFunctions uF, String strId) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("delete from roster_policy_rules where roster_policy_rule_id=?");
			pst.setInt(1, uF.parseToInt(strId));
			pst.executeUpdate();
			pst.close();
			
			session.setAttribute(MESSAGE, SUCCESSM+"Roster Policy Rule Type deleted successfully."+END);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;
	}
	
	public String loadRosterPolicyRules(UtilityFunctions uF) {
		
		organisationList = new FillOrganisation(request).fillOrganisation();
		wLocationList = new FillWLocation(request).fillWLocation(getStrOrg());
		departList = new FillDepartment(request).fillDepartment(uF.parseToInt(getStrOrg()));
		levelList = new FillLevel(request).fillLevel(uF.parseToInt(getStrOrg()));
		shiftList = new FillShift(request).fillShiftByOrg(uF.parseToInt(getStrOrg()));
		
//		empGenderList = new FillGender().fillGender();
//		rosterPolicyRuleList = new FillRosterPolicyRule().fillRosterPolicyRule();
		
		return LOAD;
	}
	
	private HttpServletRequest request;
	
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}
	
	public String getStrOrg() {
		return strOrg;
	}
	
	public void setStrOrg(String strOrg) {
		this.strOrg = strOrg;
	}
	
	public String getUserscreen() {
		return userscreen;
	}
	
	public void setUserscreen(String userscreen) {
		this.userscreen = userscreen;
	}
	
	public String getNavigationId() {
		return navigationId;
	}
	
	public void setNavigationId(String navigationId) {
		this.navigationId = navigationId;
	}
	
	public String getToPage() {
		return toPage;
	}

	public void setToPage(String toPage) {
		this.toPage = toPage;
	}
	
	public String getrPolicyRuleId() {
		return rPolicyRuleId;
	}
	
	public void setrPolicyRuleId(String rPolicyRuleId) {
		this.rPolicyRuleId = rPolicyRuleId;
	}
	
	
	public String getRosterPolicyRuleName() {
		return rosterPolicyRuleName;
	}

	public void setRosterPolicyRuleName(String rosterPolicyRuleName) {
		this.rosterPolicyRuleName = rosterPolicyRuleName;
	}

	public String getMinNoofMemberInShift() {
		return minNoofMemberInShift;
	}

	public void setMinNoofMemberInShift(String minNoofMemberInShift) {
		this.minNoofMemberInShift = minNoofMemberInShift;
	}

	public String getMinNoofMemberInShiftOnWeekend() {
		return minNoofMemberInShiftOnWeekend;
	}

	public void setMinNoofMemberInShiftOnWeekend(String minNoofMemberInShiftOnWeekend) {
		this.minNoofMemberInShiftOnWeekend = minNoofMemberInShiftOnWeekend;
	}

	public String getMinNoofLeadersInShift() {
		return minNoofLeadersInShift;
	}

	public void setMinNoofLeadersInShift(String minNoofLeadersInShift) {
		this.minNoofLeadersInShift = minNoofLeadersInShift;
	}

	public String[] getStrLevel() {
		return strLevel;
	}

	public void setStrLevel(String[] strLevel) {
		this.strLevel = strLevel;
	}

	public String getMinNoofMemberWithLeadersInShift() {
		return minNoofMemberWithLeadersInShift;
	}

	public void setMinNoofMemberWithLeadersInShift(String minNoofMemberWithLeadersInShift) {
		this.minNoofMemberWithLeadersInShift = minNoofMemberWithLeadersInShift;
	}

	public String getNoofWeekendOffInMonth() {
		return noofWeekendOffInMonth;
	}

	public void setNoofWeekendOffInMonth(String noofWeekendOffInMonth) {
		this.noofWeekendOffInMonth = noofWeekendOffInMonth;
	}

	public String getMaxNoofShiftsAssignInMonth() {
		return maxNoofShiftsAssignInMonth;
	}

	public void setMaxNoofShiftsAssignInMonth(String maxNoofShiftsAssignInMonth) {
		this.maxNoofShiftsAssignInMonth = maxNoofShiftsAssignInMonth;
	}

	public String getMinNoofDaysBreakInStretchShift() {
		return minNoofDaysBreakInStretchShift;
	}

	public void setMinNoofDaysBreakInStretchShift(String minNoofDaysBreakInStretchShift) {
		this.minNoofDaysBreakInStretchShift = minNoofDaysBreakInStretchShift;
	}

	public String getMinNoofDaysWeekOffbetweenChangeShift() {
		return minNoofDaysWeekOffbetweenChangeShift;
	}

	public void setMinNoofDaysWeekOffbetweenChangeShift(String minNoofDaysWeekOffbetweenChangeShift) {
		this.minNoofDaysWeekOffbetweenChangeShift = minNoofDaysWeekOffbetweenChangeShift;
	}

	public String getStrWLocation() {
		return strWLocation;
	}

	public void setStrWLocation(String strWLocation) {
		this.strWLocation = strWLocation;
	}

	public String[] getStrWLocationCombined() {
		return strWLocationCombined;
	}

	public void setStrWLocationCombined(String[] strWLocationCombined) {
		this.strWLocationCombined = strWLocationCombined;
	}

	public String getMinNoofMaleMemberInShift() {
		return minNoofMaleMemberInShift;
	}

	public void setMinNoofMaleMemberInShift(String minNoofMaleMemberInShift) {
		this.minNoofMaleMemberInShift = minNoofMaleMemberInShift;
	}

	public List<FillLevel> getLevelList() {
		return levelList;
	}

	public void setLevelList(List<FillLevel> levelList) {
		this.levelList = levelList;
	}

	public List<FillWLocation> getwLocationList() {
		return wLocationList;
	}

	public void setwLocationList(List<FillWLocation> wLocationList) {
		this.wLocationList = wLocationList;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getStrLevelNames() {
		return strLevelNames;
	}

	public void setStrLevelNames(String strLevelNames) {
		this.strLevelNames = strLevelNames;
	}

	public String getStrWLocationCombinedName() {
		return strWLocationCombinedName;
	}

	public void setStrWLocationCombinedName(String strWLocationCombinedName) {
		this.strWLocationCombinedName = strWLocationCombinedName;
	}

	public List<FillShift> getShiftList() {
		return shiftList;
	}

	public void setShiftList(List<FillShift> shiftList) {
		this.shiftList = shiftList;
	}

	public String getShiftName1() {
		return shiftName1;
	}

	public void setShiftName1(String shiftName1) {
		this.shiftName1 = shiftName1;
	}

	public String getShiftName2() {
		return shiftName2;
	}

	public void setShiftName2(String shiftName2) {
		this.shiftName2 = shiftName2;
	}

	public String getShiftName3() {
		return shiftName3;
	}

	public void setShiftName3(String shiftName3) {
		this.shiftName3 = shiftName3;
	}

	public String getShiftNameOther() {
		return shiftNameOther;
	}

	public void setShiftNameOther(String shiftNameOther) {
		this.shiftNameOther = shiftNameOther;
	}

	public List<FillDepartment> getDepartList() {
		return departList;
	}

	public void setDepartList(List<FillDepartment> departList) {
		this.departList = departList;
	}

	public List<FillOrganisation> getOrganisationList() {
		return organisationList;
	}

	public void setOrganisationList(List<FillOrganisation> organisationList) {
		this.organisationList = organisationList;
	}

	public String getF_department() {
		return f_department;
	}

	public void setF_department(String f_department) {
		this.f_department = f_department;
	}

}
