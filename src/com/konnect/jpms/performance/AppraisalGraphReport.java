package com.konnect.jpms.performance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;

public class AppraisalGraphReport implements ServletRequestAware, IStatements {

	HttpSession session;
	String strSessionEmpId;
	String strUserType;
	String strBaseUserType;
	String strUserTypeId;
	CommonFunctions CF;
	List<FillFrequency> frequencyList;
	private String frequency;

	private String appraisal;

	private String currUserType;
	private String[] check;
	public String execute() {
		session = request.getSession();
		strSessionEmpId = (String) session.getAttribute(EMPID);
		strUserType = (String) session.getAttribute(USERTYPE);
		strBaseUserType = (String) session.getAttribute(BASEUSERTYPE);
		strUserTypeId = (String) session.getAttribute(USERTYPEID);
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return "login";
		frequencyList = new FillFrequency(request).fillFrequency();
		/*request.setAttribute(PAGE, "/jsp/performance/AppraisalGraphReport.jsp");
		request.setAttribute(TITLE, "Appraisal Graph Report");

		StringBuilder sbpageTitleNaviTrail = new StringBuilder();
		sbpageTitleNaviTrail.append("<li><i class=\"fa fa-cubes\"></i><a href=\"TeamPerformance.action\" style=\"color: #3c8dbc;\"> Analytics</a></li>" +
			"<li class=\"active\">Bell Curves</li>");
		request.setAttribute("PAGETITLE_NAVITRAIL", sbpageTitleNaviTrail.toString());*/
		
		if(getCurrUserType()==null && strUserType != null && strUserType.equals(MANAGER)) {
			setCurrUserType("MYTEAM");
		}
		String strDefaultAppraisal = getAppraisalReport();
//		System.out.println("strDefaultAppraisal==>"+strDefaultAppraisal);
		if (strDefaultAppraisal != null) {
			setAppraisal(strDefaultAppraisal);
		}

//		System.out.println("getAppraisal() ===>> " + getAppraisal());
		if (getAppraisal() != null) {
			getData();
		}

		return "success";
	}

	

	public List<FillFrequency> getFrequencyList() {
		return frequencyList;
	}

	public void setFrequencyList(List<FillFrequency> frequencyList) {
		this.frequencyList = frequencyList;
	}

	private void getSelectedFilter(UtilityFunctions uF) {
		Map<String,String> hmFilter=new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();
		
		if((strUserType != null && !strUserType.equals(MANAGER)) || (getCurrUserType() != null && getCurrUserType().equals(strBaseUserType))) {
			alFilter.add("FREQ");
			if(getFrequency()!=null && !getFrequency().equals("")) {
				String strFrequency="";
				for(int i=0;frequencyList!=null && i<frequencyList.size();i++) {
					if(getFrequency().equals(frequencyList.get(i).getId())) {
						strFrequency=frequencyList.get(i).getName();
					}
				}
				if(strFrequency!=null && !strFrequency.equals("")) {
					hmFilter.put("FREQ", strFrequency);
				} else {
					hmFilter.put("FREQ", "All Frequency");
				}
			} else {
				hmFilter.put("FREQ", "All Frequency");
			}
		}
		
		String selectedFilter= CF.getSelectedFilter1(CF, uF, alFilter, hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}
	

	
	public String getAppraisalReport() {
		UtilityFunctions uF = new UtilityFunctions();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		String strDefaultAppraisal = null;

		try {
			
			con = db.makeConnection(con);
			getSelectedFilter(uF);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			Map<String,String> hmOrientationMap = CF.getOrientationValue(con);
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			Map<String, String> hmLocation = getLcationMap(con);
			Map<String, String> hmFrequency = new HashMap<String, String>();
			Map<String, String> hmAppraisalCount = new HashMap<String, String>();
			
			pst = con.prepareStatement("select * from appraisal_frequency");
			rs = pst.executeQuery();
			while (rs.next()) {
				hmFrequency.put(rs.getString("appraisal_frequency_id"), rs.getString("frequency_name"));
			}
			rs.close();
			pst.close();

			pst = con.prepareStatement("select count(*) as cnt, appraisal_id,appraisal_freq_id from appraisal_final_sattlement group by appraisal_id,appraisal_freq_id");
			rs = pst.executeQuery();
			while (rs.next()) {
				hmAppraisalCount.put(rs.getString("appraisal_id")+"_"+rs.getString("appraisal_freq_id"), rs.getString("cnt"));
			}
			rs.close();
			pst.close();
//			System.out.println("hmAppraisalCount =====>> " + hmAppraisalCount);
			
			StringBuilder sbEmpId = null;
			if(session.getAttribute(WLOCATION_ACCESS) != null && !((String)session.getAttribute(WLOCATION_ACCESS)).equals("") && session.getAttribute(ORG_ACCESS) != null && !((String)session.getAttribute(ORG_ACCESS)).equals("")) {
				pst = con.prepareStatement("select eod.emp_id from employee_official_details eod,employee_personal_details epd where " +
					" epd.emp_per_id=eod.emp_id and is_alive = true and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+") and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+") ");
				rs = pst.executeQuery();
				while (rs.next()) {
					if(sbEmpId == null) {
						sbEmpId = new StringBuilder();
						sbEmpId.append(rs.getString("emp_id"));
					} else {
						sbEmpId.append(","+rs.getString("emp_id"));
					}
				}
				rs.close();
				pst.close();
			}
			String[] str_EmpIds = null;
			if(sbEmpId != null) {
				str_EmpIds = sbEmpId.toString().split(",");
			}
//			System.out.println("str_EmpIds ===>> " + str_EmpIds);
			 
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from appraisal_details a, appraisal_details_frequency adf where a.appraisal_details_id = adf.appraisal_id and (is_delete is null or is_delete = false) ");
			if (getFrequency() != null && !getFrequency().equals("")) {
				sbQuery.append(" and frequency= '"+getFrequency()+"' ");
			}
			if(strUserType != null && strUserType.equals(MANAGER) && getCurrUserType() != null && !getCurrUserType().equals(strBaseUserType)) {
				sbQuery.append(" and (a.added_by = "+uF.parseToInt(strSessionEmpId)+" or supervisor_id like '%,"+strSessionEmpId+",%') ");
			} else if(strUserType != null && strUserType.equals(CEO) && getCurrUserType() != null && !getCurrUserType().equals(strBaseUserType)) {
				sbQuery.append(" and (a.added_by = "+uF.parseToInt(strSessionEmpId)+" or ceo_ids like '%,"+strSessionEmpId+",%') ");
			}  else if(strUserType != null && strUserType.equals(HOD) && getCurrUserType() != null && !getCurrUserType().equals(strBaseUserType)) {
				sbQuery.append(" and (a.added_by = "+uF.parseToInt(strSessionEmpId)+" or hod_ids like '%,"+strSessionEmpId+",%') ");
			}  else if(str_EmpIds!=null && str_EmpIds.length>0) {
                sbQuery.append(" and (");
                for(int i=0; i<str_EmpIds.length; i++) {
                    sbQuery.append(" self_ids like '%,"+str_EmpIds[i]+",%'");
                    if(i<str_EmpIds.length-1) {
                        sbQuery.append(" OR ");
                    }
                }
                sbQuery.append(") ");
            }
			sbQuery.append(" order by a.entry_date desc");
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst  =====>> " + pst);
			rs = pst.executeQuery();
			List<List<String>> outerList = new ArrayList<List<String>>();
			while (rs.next()) {
				List<String> empList =new ArrayList<String>();
				empList = getAppendData(con, rs.getString("self_ids"));
//				System.out.println("rs.getString(self_ids))==>"+rs.getString("self_ids"));
//				System.out.println("hmAppraisalCount app_id =====>> " + hmAppraisalCount.get(rs.getString("appraisal_details_id")));
//				System.out.println(rs.getString("appraisal_details_id")+" -- empList.size() =====>> " + empList.size());
//				System.out.println(rs.getString("appraisal_details_id")+" -- empList.get(1) =====>> " + empList.get(1));
//				System.out.println(rs.getString("appraisal_details_id")+" -- hmAppraisalCount =====>> " + hmAppraisalCount.get(rs.getString("appraisal_details_id")));
			//===start parvez date: 24-03-2023===	
				if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(F_TEN_STAR_RATING_FOR_REVIEW))){
					if (strDefaultAppraisal == null && getAppraisal() == null) {
						strDefaultAppraisal = rs.getString("appraisal_details_id")+"::::"+rs.getString("appraisal_freq_id");
					}
					List<String> innerList = new ArrayList<String>();
					innerList.add(rs.getString("appraisal_details_id"));//0
					innerList.add(rs.getString("appraisal_name")+" ("+rs.getString("appraisal_freq_name")+")");//1
					innerList.add(hmOrientationMap.get(rs.getString("oriented_type")));//2
	
					innerList.add(uF.showData(rs.getString("appraisal_type"), ""));//3
					innerList.add(uF.showData(hmFrequency.get(rs.getString("frequency")), ""));//4
					innerList.add(uF.showData(uF.getDateFormat(rs.getString("from_date"), DBDATE, DATE_FORMAT), ""));//5
					innerList.add(uF.showData(uF.getDateFormat(rs.getString("to_date"), DBDATE, DATE_FORMAT), ""));//6
					innerList.add(uF.showData(getAppendData(rs.getString("wlocation_id"), hmLocation), ""));//7
					innerList.add(uF.showData(getAppendData(rs.getString("added_by"), hmEmpName), ""));//8
					innerList.add(uF.showData(uF.getDateFormat(rs.getString("entry_date"), DBDATE, DATE_FORMAT), ""));//9
					innerList.add(rs.getString("appraisal_freq_id"));//10
					innerList.add(rs.getString("is_appraisal_publish"));//11
					innerList.add(rs.getString("freq_publish_expire_status"));//12
					innerList.add(rs.getString("is_appraisal_close"));//13
					innerList.add(rs.getString("close_reason"));//14
					innerList.add(uF.showData(uF.getDateFormat(rs.getString("freq_start_date"), DBDATE, DATE_FORMAT), ""));//15
					innerList.add(uF.showData(uF.getDateFormat(rs.getString("freq_end_date"), DBDATE, DATE_FORMAT), ""));//16
					outerList.add(innerList);
				}else{
					if(empList.size() > 0 && uF.parseToInt(hmAppraisalCount.get(rs.getString("appraisal_details_id")+"_"+rs.getString("appraisal_freq_id")))== uF.parseToInt(empList.get(1))) {	
						if (strDefaultAppraisal == null && getAppraisal() == null) {
							strDefaultAppraisal = rs.getString("appraisal_details_id")+"::::"+rs.getString("appraisal_freq_id");
						}
						List<String> innerList = new ArrayList<String>();
						innerList.add(rs.getString("appraisal_details_id"));//0
						innerList.add(rs.getString("appraisal_name")+" ("+rs.getString("appraisal_freq_name")+")");//1
						innerList.add(hmOrientationMap.get(rs.getString("oriented_type")));//2
		
						innerList.add(uF.showData(rs.getString("appraisal_type"), ""));//3
						innerList.add(uF.showData(hmFrequency.get(rs.getString("frequency")), ""));//4
						innerList.add(uF.showData(uF.getDateFormat(rs.getString("from_date"), DBDATE, DATE_FORMAT), ""));//5
						innerList.add(uF.showData(uF.getDateFormat(rs.getString("to_date"), DBDATE, DATE_FORMAT), ""));//6
						innerList.add(uF.showData(getAppendData(rs.getString("wlocation_id"), hmLocation), ""));//7
						innerList.add(uF.showData(getAppendData(rs.getString("added_by"), hmEmpName), ""));//8
						innerList.add(uF.showData(uF.getDateFormat(rs.getString("entry_date"), DBDATE, DATE_FORMAT), ""));//9
						innerList.add(rs.getString("appraisal_freq_id"));//10
						innerList.add(rs.getString("is_appraisal_publish"));//11
						innerList.add(rs.getString("freq_publish_expire_status"));//12
						innerList.add(rs.getString("is_appraisal_close"));//13
						innerList.add(rs.getString("close_reason"));//14
						innerList.add(uF.showData(uF.getDateFormat(rs.getString("freq_start_date"), DBDATE, DATE_FORMAT), ""));//15
						innerList.add(uF.showData(uF.getDateFormat(rs.getString("freq_end_date"), DBDATE, DATE_FORMAT), ""));//16
						outerList.add(innerList);
					}
				}
			//===end parvez date: 24-03-2023===	
			}
			rs.close();
			pst.close();
			
//			System.out.println("outerList ===>> " + outerList);
			
			request.setAttribute("outerList", outerList);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return strDefaultAppraisal;
	}

	
	private List<String> getAppendData(Connection con, String strID) {
		StringBuilder sb = new StringBuilder();
		List<String> empList = new ArrayList<String>();
//		EncryptionUtils encryption = new EncryptionUtils();// Created By Dattatray Date : 20-July-2021 Note : Encryption
		if (strID != null && !strID.equals("")) {
			int flag = 0, empcnt = 0;
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);

			String[] temp = strID.split(",");
			empcnt = temp.length - 1;
			for (int i = 0; i < temp.length; i++) {

				if (temp[i] != null && !temp[i].equals("")) {
					if (flag == 0) { //encryption.encrypt(temp[i])
						sb.append("<a href=\"MyProfile.action?empId=" + temp[i] + "\">" + hmEmpName.get(temp[i].trim()) + "</a>");// Created By Dattatray Date : 20-July-2021 Note : empId Encryption
					} else {
						sb.append(", " + "<a href=\"MyProfile.action?empId=" + temp[i] + "\">" + hmEmpName.get(temp[i].trim()) + "</a>");// Created By Dattatray Date : 20-July-2021 Note : empId Encryption
					}
					flag = 1;
				}
			}
			empList.add(sb.toString());
			empList.add(empcnt + "");
//			 System.out.println("empList ========== >>>> "+empList.toString());
		}
		return empList;
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

		if (strID != null && !strID.equals("") && strID.length() > 1) {
			if (strID.substring(0, 1).equals(",") && strID.substring(strID.length()-1, strID.length()).equals(",")) {
				strID = strID.substring(1, strID.length()-1);
			}
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

	public String getAppraisal() {
		return appraisal;
	}

	public void setAppraisal(String appraisal) {
		this.appraisal = appraisal;
	}

	public void getData() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		con = db.makeConnection(con);

		UtilityFunctions uF = new UtilityFunctions();
		try {
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			StringBuilder sbAppId = null;
			StringBuilder sbAppFreqId = null;
			if (appraisal != null) {
				List<String> alAppraisalList = Arrays.asList(getAppraisal().split(","));
//				System.out.println("alAppraisalList ===>> " + alAppraisalList);
//				if (appraisal.charAt(appraisal.length() - 1) == ',') {
//					appraisal = appraisal.substring(0, appraisal.length()-1);
//				}
				for(int i=0; alAppraisalList != null && i<alAppraisalList.size(); i++) {
					String[] tempAppAndFreqId = alAppraisalList.get(i).split("::::");
					if(sbAppId == null) {
						sbAppId = new StringBuilder();
						sbAppId.append(tempAppAndFreqId[0]);
					} else {
						sbAppId.append(","+tempAppAndFreqId[0]);
					}
					if(tempAppAndFreqId.length>1) {
						if(sbAppFreqId == null) {
							sbAppFreqId = new StringBuilder();
							sbAppFreqId.append(tempAppAndFreqId[1]);
						} else {
							sbAppFreqId.append(","+tempAppAndFreqId[1]);
						}
					}
				}
			}

			/*if (appraisal != null) {
				pst = con.prepareStatement("select * from appraisal_details where appraisal_details_id in(" + appraisal + ")");
			} else {
				pst = con.prepareStatement("select * from appraisal_details  ");
			}*/
			System.out.println("sbAppId=="+sbAppId+"---sbAppFreqId=="+sbAppFreqId);
			if(sbAppId!=null){
				pst=con.prepareStatement("select * from appraisal_details a, appraisal_details_frequency adf where a.appraisal_details_id = adf.appraisal_id "
					+"  and (is_delete is null or is_delete = false) and appraisal_details_id in ("+sbAppId.toString()+") and appraisal_freq_id in ("+sbAppFreqId.toString()+")");
			} else {
//				pst=con.prepareStatement("select * from appraisal_details a, appraisal_details_frequency adf where a.appraisal_details_id = adf.appraisal_id "
//					+" and (is_delete is null or is_delete = false) order by appraisal_details_id");

			}
			Map<String, String> appraisalMp = new HashMap<String, String>();
//			System.out.println("pst2 ===>> " + pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				appraisalMp.put(rs.getString("appraisal_details_id")+"_"+rs.getString("appraisal_freq_id"), rs.getString("appraisal_name")+" ("+rs.getString("appraisal_freq_name")+")");
			}
			rs.close();
			pst.close();
			request.setAttribute("appraisalMp", appraisalMp);
			
//			System.out.println("sbAppId ===>> " + sbAppId);
			if(sbAppId != null) {
			//===start parvez date: 24-03-2023===	
				/*pst=con.prepareStatement("select count(*)as emp,appraisal_id,average,appraisal_freq_id from(select *,cast(marks*5/weightage as integer) as average from ( "+
					"select sum(marks) as marks,sum(weightage) as weightage,emp_id,appraisal_id,appraisal_freq_id from appraisal_question_answer where appraisal_id in ("+sbAppId.toString()+") " +
					"and appraisal_freq_id in ("+sbAppFreqId.toString()+") and weightage>0 group by emp_id,appraisal_id,appraisal_freq_id) as a ) as b group by appraisal_id,average,appraisal_freq_id order by average");*/
				if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(F_TEN_STAR_RATING_FOR_REVIEW))){
					pst=con.prepareStatement("select count(*)as emp,appraisal_id,average,appraisal_freq_id from(select *,cast(marks*10/weightage as integer) as average from ( "+
							"select sum(marks) as marks,sum(weightage) as weightage,emp_id,appraisal_id,appraisal_freq_id from appraisal_question_answer where appraisal_id in ("+sbAppId.toString()+") " +
							"and appraisal_freq_id in ("+sbAppFreqId.toString()+") and weightage>0 group by emp_id,appraisal_id,appraisal_freq_id) as a ) as b group by appraisal_id,average,appraisal_freq_id order by average");
				} else{
					pst=con.prepareStatement("select count(*)as emp,appraisal_id,average,appraisal_freq_id from(select *,cast(marks*5/weightage as integer) as average from ( "+
							"select sum(marks) as marks,sum(weightage) as weightage,emp_id,appraisal_id,appraisal_freq_id from appraisal_question_answer where appraisal_id in ("+sbAppId.toString()+") " +
							"and appraisal_freq_id in ("+sbAppFreqId.toString()+") and weightage>0 group by emp_id,appraisal_id,appraisal_freq_id) as a ) as b group by appraisal_id,average,appraisal_freq_id order by average");
				}
			//===end parvez date: 24-03-2023===	
			}else{
//				pst=con.prepareStatement("select count(*)as emp,appraisal_id,average,appraisal_freq_id from(select *,cast(marks*5/weightage as integer) as average from("+
//					"select sum(marks) as marks,sum(weightage) as weightage,emp_id,appraisal_id,appraisal_freq_id from appraisal_question_answer  group by emp_id,appraisal_id,appraisal_freq_id"+
//					") as a ) as b group by appraisal_id,average,appraisal_freq_id order by average");
			}
			
			/*if (appraisal != null) {
				pst = con.prepareStatement("select count(*)as emp,appraisal_id,average,appraisal_freq_id from(select *,cast(marks*10/weightage as integer) as average from("
						+ "select sum(marks) as marks,sum(weightage) as weightage,emp_id,appraisal_id,appraisal_freq_id from appraisal_question_answer where appraisal_id in(" + appraisal
						+ ") group by emp_id,appraisal_id) as a ) as b group by appraisal_id,average,appraisal_freq_id order by average");
			} else {
				pst = con.prepareStatement("select count(*)as emp,appraisal_id,average,appraisal_freq_id from(select *,cast(marks*10/weightage as integer) as average from("
						+ "select sum(marks) as marks,sum(weightage) as weightage,emp_id,appraisal_id,appraisal_freq_id from appraisal_question_answer  group by emp_id,appraisal_id"
						+ ") as a ) as b group by appraisal_id,average,appraisal_freq_id order by average");
			}*/

//			System.out.println("pst ===>> " + pst);
			Map<String, String> appraisalIds = new LinkedHashMap<String, String>();
			Map<String, String> appraisalData = new HashMap<String, String>();
			rs = pst.executeQuery();

			while (rs.next()) {
			//===start parvez date: 01-04-2023===	
//				appraisalData.put(rs.getString("appraisal_id")+"_"+rs.getString("appraisal_freq_id")+"_"+rs.getString("average"), rs.getString("emp"));
				if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(F_TEN_STAR_RATING_FOR_REVIEW))){
					
					if(uF.parseToDouble(rs.getString("average")) == 1){
						appraisalData.put(rs.getString("appraisal_id")+"_"+rs.getString("appraisal_freq_id")+"_1", rs.getString("emp"));
					} else if(uF.parseToDouble(rs.getString("average")) > 1 && uF.parseToDouble(rs.getString("average")) < 4){
						appraisalData.put(rs.getString("appraisal_id")+"_"+rs.getString("appraisal_freq_id")+"_2", rs.getString("emp"));
					} else if(uF.parseToDouble(rs.getString("average")) == 4){
						appraisalData.put(rs.getString("appraisal_id")+"_"+rs.getString("appraisal_freq_id")+"_3L", rs.getString("emp"));
					} else if(uF.parseToDouble(rs.getString("average")) == 5){
						appraisalData.put(rs.getString("appraisal_id")+"_"+rs.getString("appraisal_freq_id")+"_3H", rs.getString("emp"));
					} else if(uF.parseToDouble(rs.getString("average"))>5 && uF.parseToDouble(rs.getString("average"))<9){
						appraisalData.put(rs.getString("appraisal_id")+"_"+rs.getString("appraisal_freq_id")+"_4", rs.getString("emp"));
					} else{
						appraisalData.put(rs.getString("appraisal_id")+"_"+rs.getString("appraisal_freq_id")+"_5", rs.getString("emp"));
					}
				} else{
					appraisalData.put(rs.getString("appraisal_id")+"_"+rs.getString("appraisal_freq_id")+"_"+rs.getString("average"), rs.getString("emp"));
				}
			//===end parvez date: 01-04-2023===	
				appraisalIds.put(rs.getString("appraisal_id")+"_"+rs.getString("appraisal_freq_id"), rs.getString("appraisal_id"));
			}
			rs.close();
			pst.close();
//			System.out.println("appraisalIds ===>> " + appraisalIds);
//			System.out.println("appraisalData ===>> " + appraisalData);
			
			StringBuilder sbAppraisalEmpCnt = new StringBuilder();
			
		//===start parvez date: 24-03-2023===	
			/*for(int i = 1; i<=5; i++) {
				sbAppraisalEmpCnt.append("{'rate':'"+i+"', ");
				Iterator<String> it = appraisalIds.keySet().iterator();
				while (it.hasNext()) {
					String appraisalId = it.next();
					String empCnt = appraisalData.get(appraisalId+"_"+i);
					String strAppraisalEmpCnt = uF.showData(empCnt, "0");
					sbAppraisalEmpCnt.append("'"+appraisalId.substring(0, appraisalId.length())+"_empCnt': "+uF.parseToInt(strAppraisalEmpCnt)+",");
				}
				if(sbAppraisalEmpCnt.length()>1) {
					sbAppraisalEmpCnt.replace(0, sbAppraisalEmpCnt.length(), sbAppraisalEmpCnt.substring(0, sbAppraisalEmpCnt.length()-1));
		        }
				sbAppraisalEmpCnt.append("},");
			}*/
			if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(F_TEN_STAR_RATING_FOR_REVIEW))){
				/*for(int i = 1; i<=10; i++) {
					sbAppraisalEmpCnt.append("{'rate':'"+i+"', ");
					Iterator<String> it = appraisalIds.keySet().iterator();
					while (it.hasNext()) {
						String appraisalId = it.next();
						String empCnt = appraisalData.get(appraisalId+"_"+i);
						String strAppraisalEmpCnt = uF.showData(empCnt, "0");
						sbAppraisalEmpCnt.append("'"+appraisalId.substring(0, appraisalId.length())+"_empCnt': "+uF.parseToInt(strAppraisalEmpCnt)+",");
					}
					if(sbAppraisalEmpCnt.length()>1) {
						sbAppraisalEmpCnt.replace(0, sbAppraisalEmpCnt.length(), sbAppraisalEmpCnt.substring(0, sbAppraisalEmpCnt.length()-1));
			        }
					sbAppraisalEmpCnt.append("},");
				}*/
				
				String[] arr = {"1", "2", "3L", "3H", "4", "5"};
				for(int i = 0; i<arr.length; i++){
					sbAppraisalEmpCnt.append("{'rate':'"+arr[i]+"', ");
					Iterator<String> it = appraisalIds.keySet().iterator();
					while (it.hasNext()) {
						String appraisalId = it.next();
						String empCnt = appraisalData.get(appraisalId+"_"+arr[i]);
						String strAppraisalEmpCnt = uF.showData(empCnt, "0");
						sbAppraisalEmpCnt.append("'"+appraisalId.substring(0, appraisalId.length())+"_empCnt': "+uF.parseToInt(strAppraisalEmpCnt)+",");
					}
					if(sbAppraisalEmpCnt.length()>1) {
						sbAppraisalEmpCnt.replace(0, sbAppraisalEmpCnt.length(), sbAppraisalEmpCnt.substring(0, sbAppraisalEmpCnt.length()-1));
			        }
					sbAppraisalEmpCnt.append("},");
				}
				
			}else{
				for(int i = 1; i<=5; i++) {
					sbAppraisalEmpCnt.append("{'rate':'"+i+"', ");
					Iterator<String> it = appraisalIds.keySet().iterator();
					while (it.hasNext()) {
						String appraisalId = it.next();
						String empCnt = appraisalData.get(appraisalId+"_"+i);
						String strAppraisalEmpCnt = uF.showData(empCnt, "0");
						sbAppraisalEmpCnt.append("'"+appraisalId.substring(0, appraisalId.length())+"_empCnt': "+uF.parseToInt(strAppraisalEmpCnt)+",");
					}
					if(sbAppraisalEmpCnt.length()>1) {
						sbAppraisalEmpCnt.replace(0, sbAppraisalEmpCnt.length(), sbAppraisalEmpCnt.substring(0, sbAppraisalEmpCnt.length()-1));
			        }
					sbAppraisalEmpCnt.append("},");
				}
			}
		//===end parvez date: 24-03-2023===	
			
//			sbAppraisalGraph.append("graphBill = new AmCharts.AmGraph();" +
//					"graphBill.type = \"smoothedLine\";" +
//					"graphBill.title = \""+appraisalMp.get(appraisalId)+"\";" +
//					"graphBill.valueField = \"empCnt\";" +
//					"graphBill.bullet = \"round\";" +
//					"graphBill.bulletSize = 8;" +
//					"graphBill.bulletBorderColor = \"#FFFFFF\";" +
//					"graphBill.bulletBorderAlpha = 1;" +
//					"graphBill.bulletBorderThickness = 2;" +
//					"graphBill.lineThickness = 2;" +
//					"graphBill.balloonText = \"[[category]]<br><b><span style='font-size:14px;'>[[value]]</span></b>\";");
			
			if(sbAppraisalEmpCnt.length()>1) {
				sbAppraisalEmpCnt.replace(0, sbAppraisalEmpCnt.length(), sbAppraisalEmpCnt.substring(0, sbAppraisalEmpCnt.length()-1));
	        }
//			System.out.println("sbAppraisalEmpCnt =====> "+sbAppraisalEmpCnt.toString());
//			System.out.println("sbAppraisalGraph =====> "+sbAppraisalGraph.toString());
			request.setAttribute("sbAppraisalEmpCnt", sbAppraisalEmpCnt.toString());
			request.setAttribute("appraisalIds", appraisalIds);
			
			List<String> alAppraisalIds = new ArrayList<String>();
			if(getAppraisal() != null) {
				alAppraisalIds = Arrays.asList(getAppraisal().split(","));
			}
			request.setAttribute("alAppraisalIds", alAppraisalIds);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}
	


	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {

		this.request = request;
	}

	public String[] getCheck() {
		return check;
	}

	public void setCheck(String[] check) {
		this.check = check;
	}

	public String getFrequency() {
		return frequency;
	}

	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}

	public String getCurrUserType() {
		return currUserType;
	}

	public void setCurrUserType(String currUserType) {
		this.currUserType = currUserType;
	}

}
