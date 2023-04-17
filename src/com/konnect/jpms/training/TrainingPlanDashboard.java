package com.konnect.jpms.training;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class TrainingPlanDashboard extends ActionSupport implements ServletRequestAware, IStatements {
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	boolean isEmpUserType = false;
	CommonFunctions CF = null;

	
	String strSessionEmpId = null;
	UtilityFunctions uF = new UtilityFunctions();

	public String execute() throws Exception {

		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;
		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);

		request.setAttribute(TITLE, "Trainings");
		request.setAttribute(PAGE, "/jsp/training/TrainingPlanDashboard.jsp");

		prepareInformation();

		return LOAD;

	}


	private void prepareInformation() {

		Database db = new Database();
		db.setRequest(request);
		PreparedStatement pst = null;
		Connection con = null;
		UtilityFunctions uF=new UtilityFunctions();
		ResultSet rst=null;
		try{
			con=db.makeConnection(con);
			Map<String,String> hmwlocationMap=CF.getWLocationMap(con, null,null);
			List<List<String>> alTrainingPlan=new ArrayList<List<String>>();
			Map<String,String> hmOrg=getOrganization(con);
			Map<String,String> hmLocationName=getLocationName(con);
			
			pst = con.prepareStatement("select * from training_certificate");
			rst = pst.executeQuery();
			Map<String,String> hmCertificatePrintMode=new HashMap<String, String>();
			while(rst.next()){
				hmCertificatePrintMode.put(rst.getString("certificate_id"), rst.getString("print_mode"));
			}
			rst.close();
			pst.close();
			
			request.setAttribute("hmCertificatePrintMode", hmCertificatePrintMode);
			
			pst=con.prepareStatement("select * from training_plan tp left join training_schedule ts using(plan_id) order by plan_id desc");
			rst=pst.executeQuery();
			
			while(rst.next()){
				
				List<String> alinner=new ArrayList<String>();
				
				alinner.add(rst.getString("plan_id"));
//				alinner.add("<img src=\"images1/icons/approved.png\"/> <a target=\"_new\" href=\"AddTrainingPlan.action?operation=E"+rst.getString("appraisal_details_id")+"&type=\">"+rst.getString("appraisal_name")+"</a>");
				alinner.add(rst.getString("training_title"));
				
				List<String> traineeList =new ArrayList<String>();
				traineeList = getAppendData(con, rst.getString("emp_ids"));
				alinner.add(traineeList != null && !traineeList.isEmpty() && traineeList.size() > 1 ? traineeList.get(1) : "0");
				
				StringBuilder sbWlocation=new StringBuilder();
				if(rst.getString("wlocation_id")==null || rst.getString("wlocation_id").equals("")){
					sbWlocation.append("");
				}else{
					List<String> locationList=Arrays.asList(rst.getString("wlocation_id").split(","));
					if(locationList!=null){
						for(int i=0;i<locationList.size();i++){
							if(i==0){
								sbWlocation.append(hmwlocationMap.get(locationList.get(i).trim()));
							}else{
								sbWlocation.append(","+hmwlocationMap.get(locationList.get(i).trim()));
							}
						}
					}else{
						sbWlocation.append("");
					}
				}
				
				alinner.add(sbWlocation.toString());
				alinner.add(rst.getString("training_duration"));
				alinner.add(uF.getDateFormat(rst.getString("start_date"), DBDATE, CF.getStrReportDateFormat()));
				alinner.add(uF.getDateFormat(rst.getString("end_date"), DBDATE, CF.getStrReportDateFormat()));

				String yesno="";
				if(rst.getString("is_certificate").equalsIgnoreCase("t")){
					yesno="YES";
				}else{
					yesno="NO";
				}
				alinner.add(yesno);
				String org_id=rst.getString("org_id");
				String org_name=org_id==null?"":hmOrg.get(org_id.trim());
				alinner.add(org_name);
							
				String location="";
				int i=0;
				if(rst.getString("location_id")!=null && !rst.getString("location_id").equals("")){
					List<String> locationList=Arrays.asList(rst.getString("location_id").split(","));
					Set<String> locationSet = new HashSet<String>(locationList);
					Iterator<String> itr = locationSet.iterator();
					
					while (itr.hasNext()) {
						String locationId = (String) itr.next();
						if(i==0){
							location=hmLocationName.get(locationId.trim());
						}else{
							location+=","+hmLocationName.get(locationId.trim());
						}
						i++;
					}
				}
				alinner.add(location);
				alinner.add(rst.getString("certificate_id"));
				int underTrainingCnt = getTraineeUnderTrainingCount(con, rst.getString("plan_id"));
				int pendingCount = uF.parseToInt(traineeList != null && !traineeList.isEmpty() && traineeList.size() > 1 ? traineeList.get(1).toString() : "0") - underTrainingCnt;
				alinner.add(""+pendingCount);
				alinner.add(""+underTrainingCnt);
				
				alTrainingPlan.add(alinner);
			}
			rst.close();
			pst.close();
			
			request.setAttribute("alTrainingPlan", alTrainingPlan);
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	public int getTraineeUnderTrainingCount(Connection con, String trainingID) {

		PreparedStatement pst = null;
		ResultSet rst = null;
		int count = 0;
		try {
			pst = con.prepareStatement("select count(*) as count from training_learnings where plan_id = ? ");
			pst.setInt(1, uF.parseToInt(trainingID));
			rst = pst.executeQuery();
			while (rst.next()) {
				count = rst.getInt("count");
			}
			rst.close();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(rst !=null){
				try {
					rst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return count;
	}
	
	
	
	private Map<String, String> getLocationName(Connection con) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String,String> hmLocationName=new HashMap<String, String>();
		
		try {
			pst = con.prepareStatement("select * from work_location_info");
			rs=pst.executeQuery();
			
			while(rs.next()){
				hmLocationName.put(rs.getString("wlocation_id"),rs.getString("wlocation_name"));
			}
			rs.close();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(rs !=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return hmLocationName;
	}

	private Map<String, String> getOrganization(Connection con) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String,String> hmOrg=new HashMap<String, String>();
		
		try {
			pst = con.prepareStatement("select org_id,org_name from org_details");
			rs=pst.executeQuery();
			while(rs.next()){
				hmOrg.put(rs.getString("org_id"),rs.getString("org_name"));
			}
			rs.close();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(rs !=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return hmOrg;
	}
	
	
//	public int getAppraisalEmpQueAnsCount(Connection con, String appraisal_id) {
//
//		PreparedStatement pst = null;
//		ResultSet rst = null;
//		int count = 0;
//		try {
//			pst = con.prepareStatement("select count(distinct emp_id) as count from appraisal_question_answer where appraisal_id = ?");
//			pst.setInt(1, uF.parseToInt(appraisal_id));
//			rst = pst.executeQuery();
//			while (rst.next()) {
//				count = rst.getInt("count");
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return count;
//	}
	
	
//	public int getAppraisalFinalCount(Connection con, String appraisal_id) {
//
//		PreparedStatement pst = null;
//		ResultSet rst = null;
//		int count = 0;
//		try {
//			pst = con.prepareStatement("select count(*) as count from appraisal_final_sattlement where appraisal_id = ? ");
//			pst.setInt(1, uF.parseToInt(appraisal_id));
//			rst = pst.executeQuery();
//			while (rst.next()) {
//				count = rst.getInt("count");
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return count;
//	}

	
	private List<String> getAppendData(Connection con, String strID) {
		StringBuilder sb = new StringBuilder();
		List<String> empList = new ArrayList<String>();
//		EncryptionUtils encryption = new EncryptionUtils();// Created By Dattatray Date : 21-July-2021 Note : Encryption
		if (strID != null && !strID.equals("")) {
			int flag = 0, empcnt = 0;
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);

			String[] temp = strID.split(",");
			empcnt = temp.length - 1;
			for (int i = 0; i < temp.length; i++) {

				if (temp[i] != null && !temp[i].equals("")) {
					if (flag == 0) { //encryption.encrypt(temp[i])
						sb.append("<a href=\"MyProfile.action?empId=" + temp[i] + "\">" + hmEmpName.get(temp[i].trim()) + "</a>");// Created By Dattatray Date : 21-July-2021 Note : empId Encryption
					} else {
						sb.append(", " + "<a href=\"MyProfile.action?empId=" + temp[i] + "\">" + hmEmpName.get(temp[i].trim()) + "</a>");// Created By Dattatray Date : 21-July-2021 Note : empId Encryption
					}
					flag = 1;
				}
			}
			empList.add(sb.toString());
			empList.add(empcnt + "");
			// System.out.println("empList ========== >>>> "+empList.toString());
		}
		return empList;
	}
	
	
	String type;
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

}
