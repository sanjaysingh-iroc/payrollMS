package com.konnect.jpms.performance;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillEmployee;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;

public class AppraisalReport implements ServletRequestAware, IStatements {

	HttpSession session;
	String strSessionEmpId;
	String strSessionUserType;
	String strSessionUserTypeId;
	CommonFunctions CF;

	private String userlocation;
	private String type;
	
	private String strLocation; 
	private String strEmployee;
	
	private String appraisalType;
	private String oreinted;
	private String frequency;

	private List<FillWLocation> workList;
	private List<FillEmployee> empList;
	private List<FillFrequency> frequencyList;
	private List<FillOrientation> orientationList;

	public String execute() {
		session = request.getSession();
		strSessionEmpId = (String) session.getAttribute(EMPID);
		strSessionUserType = (String) session.getAttribute(USERTYPE);
		strSessionUserTypeId = (String) session.getAttribute(USERTYPEID);
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return "login";

		request.setAttribute(PAGE, "/jsp/performance/AppraisalReport.jsp");
		request.setAttribute(TITLE, "Performance Reviews");

		userlocation = getUserLocation();
		initialize();
		getAppraisalReport();

		return "success";
	}

	private void getAppraisalReport() {
		UtilityFunctions uF = new UtilityFunctions();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {

			con = db.makeConnection(con);
			
			Map<String,String> orientationMp= CF.getOrientationValue(con);
			Map<String, String> hmEmpName = CF.getEmpNameMap(con,null, null);
			Map<String, String> hmLocation = getLcationMap(con);
		
			Map<String, String> hmFrequency = new HashMap<String, String>();
			pst = con.prepareStatement("select * from appraisal_frequency");
			rs = pst.executeQuery();
			while (rs.next()) {
				hmFrequency.put(rs.getString("appraisal_frequency_id"), rs.getString("frequency_name"));
			}
			rs.close();
			pst.close();
			
			StringBuilder sbQuery=new StringBuilder();
			sbQuery.append("select * from appraisal_details a, appraisal_details_frequency adf  where a.appraisal_details_id  = adf.appraisal_id and "
					+" (is_delete is null or is_delete = false) ");
			if (strSessionUserType != null && strSessionUserType.equalsIgnoreCase(MANAGER)) {
				sbQuery.append(" and  supervisor_id like '%," + strSessionEmpId+ ",%' ");
			} else if (strSessionUserType != null && strSessionUserType.equalsIgnoreCase(HRMANAGER)) {
				sbQuery.append(" and hr_ids like '%" + strSessionEmpId + "%' ");
			} else if (strSessionUserType != null && strSessionUserType.equalsIgnoreCase(EMPLOYEE)) {
				sbQuery.append(" and peer_ids like '%," + strSessionEmpId+ ",%' ");
			} else if (strSessionUserType != null && strSessionUserType.equalsIgnoreCase(CEO)) {
				sbQuery.append(" and ceo_ids like '%," + strSessionEmpId+ ",%' ");
			}  else if (strSessionUserType != null && strSessionUserType.equalsIgnoreCase(HOD)) {
				sbQuery.append(" and hod_ids like '%," + strSessionEmpId+ ",%' ");
			} 
			
			if(getStrLocation()!=null && !getStrLocation().equals("")){
				sbQuery.append(" and wlocation_id  like '%"+getStrLocation().trim()+"%'");
			}
			
			if(getStrEmployee()!=null && !getStrEmployee().equals("")){
				sbQuery.append(" and self_ids like '%"+getStrEmployee().trim()+",%'");
			}
			
			if(getAppraisalType()!=null && !getAppraisalType().equals("")){
				sbQuery.append(" and appraisal_type='"+getAppraisalType().trim()+"'");
			}
			if(getOreinted()!=null && !getOreinted().equals("")){
				sbQuery.append(" and oriented_type='"+getOreinted().trim()+"'");
			}
			if(getFrequency()!=null && !getFrequency().equals("")){
				sbQuery.append(" and frequency='"+getFrequency().trim()+"'");
			}
			
			sbQuery.append(" order by appraisal_details_id desc");
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst=====>"+pst);
			rs = pst.executeQuery();

			List<List<String>> liveList = new ArrayList<List<String>>();
			List<List<String>> futureList = new ArrayList<List<String>>();
			List<List<String>> previousList = new ArrayList<List<String>>();
			
			Date currentDate=uF.getCurrentDate(CF.getStrTimeZone());
//			System.out.println("currentDate======>"+currentDate);
			
			while (rs.next()) { 
				
				if(currentDate.after(rs.getDate("from_date")) && currentDate.before(rs.getDate("to_date"))){
				
					List<String> liveInnerList = new ArrayList<String>();
					liveInnerList.add(rs.getString("appraisal_details_id"));//0
					liveInnerList.add(rs.getString("appraisal_name"));//1
					liveInnerList.add(orientationMp.get(rs.getString("oriented_type")));//2

					liveInnerList.add(uF.showData(rs.getString("appraisal_type"), ""));//3
					liveInnerList.add(uF.showData(hmFrequency.get(rs.getString("frequency")), ""));//4
					liveInnerList.add(uF.showData(uF.getDateFormat(rs.getString("from_date"), DBDATE, DATE_FORMAT), ""));//5
					liveInnerList.add(uF.showData(uF.getDateFormat(rs.getString("to_date"), DBDATE, DATE_FORMAT), ""));//6
					liveInnerList.add(uF.showData(getAppendData(rs.getString("wlocation_id"), hmLocation), ""));//7
					liveInnerList.add(uF.showData(getAppendData(rs.getString("added_by"), hmEmpName), ""));//8
					liveInnerList.add(uF.showData(uF.getDateFormat(rs.getString("entry_date"), DBDATE, DATE_FORMAT), "")); //9
					liveInnerList.add(rs.getString("is_publish"));//10
					liveInnerList.add(""+uF.parseToInt(rs.getString("template_id")));//11
					liveInnerList.add(rs.getString("appraisal_freq_id"));//12
					liveInnerList.add(rs.getString("appraisal_freq_name"));//13
					liveInnerList.add(rs.getString("is_appraisal_publish"));//14
					liveInnerList.add(rs.getString("freq_publish_expire_status"));//15
					liveInnerList.add(rs.getString("is_appraisal_close"));//16
					liveInnerList.add(rs.getString("close_reason"));//17
					liveInnerList.add(uF.showData(uF.getDateFormat(rs.getString("freq_start_date"), DBDATE, DATE_FORMAT), ""));//18
					liveInnerList.add(uF.showData(uF.getDateFormat(rs.getString("freq_end_date"), DBDATE, DATE_FORMAT), ""));//19
					liveList.add(liveInnerList);
				}
				
				if(currentDate.before(rs.getDate("from_date"))){
					
					List<String> futureInnerList = new ArrayList<String>();
					futureInnerList.add(rs.getString("appraisal_details_id"));//0
					futureInnerList.add(rs.getString("appraisal_name"));//1
					futureInnerList.add(orientationMp.get(rs.getString("oriented_type")));//2

					futureInnerList.add(uF.showData(rs.getString("appraisal_type"), ""));//3
					futureInnerList.add(uF.showData(hmFrequency.get(rs.getString("frequency")), ""));//4
					futureInnerList.add(uF.showData(uF.getDateFormat(rs.getString("from_date"), DBDATE, DATE_FORMAT), ""));//5
					futureInnerList.add(uF.showData(uF.getDateFormat(rs.getString("to_date"), DBDATE, DATE_FORMAT), ""));//6
					futureInnerList.add(uF.showData(getAppendData(rs.getString("wlocation_id"), hmLocation), ""));//7
					futureInnerList.add(uF.showData(getAppendData(rs.getString("added_by"), hmEmpName), ""));//8
					futureInnerList.add(uF.showData(uF.getDateFormat(rs.getString("entry_date"), DBDATE, DATE_FORMAT), "")); //9
					futureInnerList.add(rs.getString("is_publish"));//10
					futureInnerList.add(""+uF.parseToInt(rs.getString("template_id")));//11
					
					futureInnerList.add(rs.getString("appraisal_freq_id"));//12
					futureInnerList.add(rs.getString("appraisal_freq_name"));//13
					futureInnerList.add(rs.getString("is_appraisal_publish"));//14
					futureInnerList.add(rs.getString("freq_publish_expire_status"));//15
					futureInnerList.add(rs.getString("is_appraisal_close"));//16
					futureInnerList.add(rs.getString("close_reason"));//17
					futureInnerList.add(uF.showData(uF.getDateFormat(rs.getString("freq_start_date"), DBDATE, DATE_FORMAT), ""));//18
					futureInnerList.add(uF.showData(uF.getDateFormat(rs.getString("freq_end_date"), DBDATE, DATE_FORMAT), ""));//19
					futureList.add(futureInnerList);
				}
				
				if(currentDate.after(rs.getDate("to_date"))){
					
					List<String> previousInnerList = new ArrayList<String>();
					previousInnerList.add(rs.getString("appraisal_details_id"));//0
					previousInnerList.add(rs.getString("appraisal_name"));//1
					previousInnerList.add(orientationMp.get(rs.getString("oriented_type")));//2

					previousInnerList.add(uF.showData(rs.getString("appraisal_type"), ""));//3
					previousInnerList.add(uF.showData(hmFrequency.get(rs.getString("frequency")), ""));//4
					previousInnerList.add(uF.showData(uF.getDateFormat(rs.getString("from_date"), DBDATE, DATE_FORMAT), ""));//5
					previousInnerList.add(uF.showData(uF.getDateFormat(rs.getString("to_date"), DBDATE, DATE_FORMAT), ""));//6
					previousInnerList.add(uF.showData(getAppendData(rs.getString("wlocation_id"), hmLocation), ""));//7
					previousInnerList.add(uF.showData(getAppendData(rs.getString("added_by"), hmEmpName), ""));//8
					previousInnerList.add(uF.showData(uF.getDateFormat(rs.getString("entry_date"), DBDATE, DATE_FORMAT), "")); //9
					previousInnerList.add(rs.getString("is_publish"));//10
					previousInnerList.add(""+uF.parseToInt(rs.getString("template_id")));//11
					previousInnerList.add(rs.getString("appraisal_freq_id"));//12
					previousInnerList.add(rs.getString("appraisal_freq_name"));//13
					previousInnerList.add(rs.getString("is_appraisal_publish"));//14
					previousInnerList.add(rs.getString("freq_publish_expire_status"));//15
					previousInnerList.add(rs.getString("is_appraisal_close"));//16
					previousInnerList.add(rs.getString("close_reason"));//17
					previousInnerList.add(uF.showData(uF.getDateFormat(rs.getString("freq_start_date"), DBDATE, DATE_FORMAT), ""));//18
					previousInnerList.add(uF.showData(uF.getDateFormat(rs.getString("freq_end_date"), DBDATE, DATE_FORMAT), ""));//19

					previousList.add(previousInnerList);
				}
				
			}
			rs.close();
			pst.close();
			
			request.setAttribute("liveList", liveList);
			request.setAttribute("futureList", futureList);
			request.setAttribute("previousList", previousList);
			request.setAttribute("orientationMp", orientationMp);
			
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private void initialize() {
		workList = new FillWLocation(request).fillWLocation();
		empList = new FillEmployee(request).fillEmployeeName();
		frequencyList=new FillFrequency(request).fillFrequency();
		orientationList=new FillOrientation(request).fillOrientation();
	}

	
	private String getUserLocation() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		String location = "";

		con = db.makeConnection(con);
		try {
			pst = con.prepareStatement("select  wlocation_id from employee_official_details eod,user_details ud where eod.emp_id=ud.emp_id and ud.emp_id=?");
			pst.setInt(1, uF.parseToInt(strSessionEmpId));
			rst = pst.executeQuery();
			while (rst.next()) {
				location = rst.getString(1);
			}
			rst.close();
			pst.close();
			
			/*System.out.println("location=" + location);
			System.out.println("WLOCATIONID=" + (String) session.getAttribute(WLOCATIONID));*/

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

		return location;
	}

	
	private Map<String, String> getLcationMap(Connection con) {
		Map<String, String> mplocation = new HashMap<String, String>();
		PreparedStatement pst = null;
		ResultSet rst = null;

		try {
			pst = con.prepareStatement("select * from work_location_info");
			rst = pst.executeQuery();
			while (rst.next()) {
				mplocation.put(rst.getString("wlocation_id"), rst.getString("wlocation_name"));
			}
			rst.close();
			pst.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} 

		return mplocation;
	}

	
	private String getAppendData(String strID, Map<String, String> mp) {
		StringBuilder sb = new StringBuilder();

		if (strID != null && !strID.equals("")) {

			if (strID.contains(",")) {
				
				String[] temp = strID.split(",");

				for (int i = 0; i < temp.length; i++) {
					if (i == 0) {
						sb.append(mp.get(temp[i].trim()));
					} else {
						
						sb.append("," + mp.get(temp[i].trim()));
					}
				}
			} else {
				return mp.get(strID);
			}

		} else {
			return null;
		}

		return sb.toString();
	}

	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {

		this.request = request;
	}

	public Map<String, String> getLevelMap() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String, String> hmLevelMap = new HashMap<String, String>();
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement(selectLevel);
			rs = pst.executeQuery();

			while (rs.next()) {
				hmLevelMap.put(rs.getString("level_id"), rs.getString("level_name") + "[" + rs.getString("level_code") + "]");
			}
			rs.close();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return hmLevelMap;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public String getUserlocation() {
		return userlocation;
	}

	public void setUserlocation(String userlocation) {
		this.userlocation = userlocation;
	}

	public List<FillWLocation> getWorkList() {
		return workList;
	}

	public void setWorkList(List<FillWLocation> workList) {
		this.workList = workList;
	}

	public List<FillEmployee> getEmpList() {
		return empList;
	}

	public void setEmpList(List<FillEmployee> empList) {
		this.empList = empList;
	}

	public String getStrLocation() {
		return strLocation;
	}

	public void setStrLocation(String strLocation) {
		this.strLocation = strLocation;
	}

	public String getStrEmployee() {
		return strEmployee;
	}

	public void setStrEmployee(String strEmployee) {
		this.strEmployee = strEmployee;
	}

	public List<FillFrequency> getFrequencyList() {
		return frequencyList;
	}

	public void setFrequencyList(List<FillFrequency> frequencyList) {
		this.frequencyList = frequencyList;
	}

	public List<FillOrientation> getOrientationList() {
		return orientationList;
	}

	public void setOrientationList(List<FillOrientation> orientationList) {
		this.orientationList = orientationList;
	}

	public String getAppraisalType() {
		return appraisalType;
	}

	public void setAppraisalType(String appraisalType) {
		this.appraisalType = appraisalType;
	}

	public String getOreinted() {
		return oreinted;
	}

	public void setOreinted(String oreinted) {
		this.oreinted = oreinted;
	}

	public String getFrequency() {
		return frequency;
	}

	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}
	
}
