package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UserActivities;
import com.konnect.jpms.util.UserAlerts;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class GetTeamInfoAjax extends ActionSupport implements IStatements, ServletRequestAware {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	HttpSession session;
	CommonFunctions CF;
	String strSessionEmpId;
	
	String chboxStatus;
	String empId;
	String proId;
	String type;
	String strActualBillingType;
	String rateDayAmount;
	String rateHourAmount;
	String rateMonthAmount;
	String costAmount;
	String strShortCurrency;
	String fromPage;

	String proType;
	
	public String execute() {
		
		
		session = request.getSession();
		strSessionEmpId = (String)session.getAttribute(EMPID);
		
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		UtilityFunctions uF = new UtilityFunctions();
		
//		System.out.println("strActualBillingType ===>> " + strActualBillingType);
//		System.out.println("rateAmount ===>> " + rateAmount);
//		System.out.println("getFromPage() ===>> " + getFromPage());
		
		if(type!=null && type.equals("tl")) {
			if (uF.parseToBoolean(getChboxStatus())) {
				insertTLEmployee(CF);
			} else {
				deleteTLEmployee();
			}
		} else if(type!=null && type.equals("emp")) {
			if (uF.parseToBoolean(getChboxStatus())) {
				insertEmployee(CF);
			} else {
				deleteEmployee();
			}
		}
		
		getData(CF, uF);
		
		return SUCCESS;
	}
	
	
	
	
	private void deleteEmployee() {		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {
			
			con = db.makeConnection(con);
			pst = con.prepareStatement("delete from project_emp_details where pro_id=? and emp_id=? and _isteamlead=false");
			pst.setInt(1, uF.parseToInt(getProId()));
			pst.setInt(2, uF.parseToInt(getEmpId()));
			int x = pst.executeUpdate();
			pst.close();
			
			if(x > 0){
				
				Map<String, String> hmProDetails = CF.getProjectDetailsByProId(con, getProId());
				
				Set<String> setEmp = new HashSet<String>(); 
				if(uF.parseToInt(hmProDetails.get("PRO_OWNER_ID")) > 0){
					setEmp.add(hmProDetails.get("PRO_OWNER_ID"));
				}
				
				pst = con.prepareStatement("select emp_id from project_emp_details where pro_id=? and _isteamlead = true");
				pst.setInt(1, uF.parseToInt(getProId()));
				rs = pst.executeQuery();
				while(rs.next()){
					if(uF.parseToInt(rs.getString("emp_id")) > 0){
						setEmp.add(rs.getString("emp_id"));
					}
				}
				rs.close();
				pst.close();
				for(String strEmp : setEmp){
					int cnt = 0;
					pst = con.prepareStatement("select pro_new_resource from user_alerts where emp_id=?");
					pst.setInt(1, uF.parseToInt(strEmp));
					rs = pst.executeQuery();
					while(rs.next()){
						cnt = uF.parseToInt(rs.getString("pro_new_resource"));
					}
					rs.close();
					pst.close();
					
					if(cnt > 0){
						cnt = cnt - 1;
					}
					pst = con.prepareStatement("update user_alerts set pro_new_resource=? where emp_id=?");
					pst.setInt(1, cnt);
					pst.setInt(2, uF.parseToInt(strEmp));
					pst.execute();
					pst.close();
				}
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	

	public void insertEmployee(CommonFunctions CF) {		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {
			
			con = db.makeConnection(con);
//			pst = con.prepareStatement("insert into project_emp_details (pro_id, emp_id, _isteamlead) values (?,?,?)");
//			pst.setInt(1, uF.parseToInt(getProId()));
//			pst.setInt(2, uF.parseToInt(getEmpId()));
//			pst.setBoolean(3, false);
//			pst.execute();
			
			pst = con.prepareStatement("update project_emp_details set _isteamlead=?,emp_rate_per_hour=?, emp_rate_per_day=?, emp_rate_per_month=?, " +
			"emp_actual_rate_per_hour=?, emp_actual_rate_per_day=?, emp_actual_rate_per_month=? where emp_id =? and pro_id = ?");
			pst.setBoolean(1, false);
			pst.setDouble(2, uF.parseToDouble(getRateHourAmount()));
			pst.setDouble(3, uF.parseToDouble(getRateDayAmount()));
			pst.setDouble(4, uF.parseToDouble(getRateMonthAmount()));
			if(getStrActualBillingType() != null && getStrActualBillingType().equals("H")) {
				pst.setDouble(5, uF.parseToDouble(getCostAmount()));
			} else if(getStrActualBillingType() != null && getStrActualBillingType().equals("M")) {
				double dbldayCostAmt = uF.parseToDouble(getCostAmount()) / 30;
				double dblhrCostAmt = 0;
				if(uF.parseToDouble(CF.getStrStandardHrs()) > 0) {
					dblhrCostAmt = dbldayCostAmt / uF.parseToDouble(CF.getStrStandardHrs());
				}
				pst.setDouble(5, uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblhrCostAmt)));
			} else {
				double dblhrCostAmt = 0;
				if(uF.parseToDouble(CF.getStrStandardHrs()) > 0) {
					dblhrCostAmt = uF.parseToDouble(getCostAmount()) / uF.parseToDouble(CF.getStrStandardHrs());
				}
				pst.setDouble(5, uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblhrCostAmt)));
			}
			if(getStrActualBillingType() != null && getStrActualBillingType().equals("D")) {
				pst.setDouble(6, uF.parseToDouble(getCostAmount()));
			} else if(getStrActualBillingType() != null && getStrActualBillingType().equals("M")) {
				double dbldayCostAmt = uF.parseToDouble(getCostAmount()) / 30;
				pst.setDouble(6, uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dbldayCostAmt)));
			} else {
				double dbldayCostAmt = uF.parseToDouble(getCostAmount()) * uF.parseToDouble(CF.getStrStandardHrs());
				pst.setDouble(6, uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dbldayCostAmt)));
			}
			if(getStrActualBillingType() != null && getStrActualBillingType().equals("M")) {
				pst.setDouble(7, uF.parseToDouble(getCostAmount()));
			} else if(getStrActualBillingType() != null && getStrActualBillingType().equals("D")) {
				double dblMonthCostAmt = uF.parseToDouble(getCostAmount()) * 30;
				pst.setDouble(7, uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblMonthCostAmt)));
			} else {
				double dbldayCostAmt = uF.parseToDouble(getCostAmount()) * uF.parseToDouble(CF.getStrStandardHrs());
				double dblMonthCostAmt = dbldayCostAmt * 30;
				pst.setDouble(7, uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblMonthCostAmt)));
			}
			pst.setInt(8, uF.parseToInt(getEmpId()));
			pst.setInt(9, uF.parseToInt(getProId()));
//			System.out.println("pst=====>"+pst);
			int cnt = pst.executeUpdate();
			pst.close();
			
			if(cnt == 0) {
				pst = con.prepareStatement("insert into project_emp_details (pro_id, emp_id,_isteamlead,emp_rate_per_hour,emp_rate_per_day," +
					"emp_rate_per_month,emp_actual_rate_per_hour,emp_actual_rate_per_day,emp_actual_rate_per_month) values (?,?,?,? ,?,?,?,? ,?)");
				pst.setInt(1, uF.parseToInt(getProId()));
				pst.setInt(2, uF.parseToInt(getEmpId()));
				pst.setBoolean(3, false);
				pst.setDouble(4, uF.parseToDouble(getRateHourAmount()));
				pst.setDouble(5, uF.parseToDouble(getRateDayAmount()));
				pst.setDouble(6, uF.parseToDouble(getRateMonthAmount()));
				if(getStrActualBillingType() != null && getStrActualBillingType().equals("H")) {
					pst.setDouble(7, uF.parseToDouble(getCostAmount()));
				} else if(getStrActualBillingType() != null && getStrActualBillingType().equals("M")) {
					double dbldayCostAmt = uF.parseToDouble(getCostAmount()) / 30;
					double dblhrCostAmt = 0;
					if(uF.parseToDouble(CF.getStrStandardHrs()) > 0) {
						dblhrCostAmt = dbldayCostAmt / uF.parseToDouble(CF.getStrStandardHrs());
					}
					pst.setDouble(7, uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblhrCostAmt)));
				} else {
					double dblhrCostAmt = 0;
					if(uF.parseToDouble(CF.getStrStandardHrs()) > 0) {
						dblhrCostAmt = uF.parseToDouble(getCostAmount()) / uF.parseToDouble(CF.getStrStandardHrs());
					}
					pst.setDouble(7, uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblhrCostAmt)));
				}
				if(getStrActualBillingType() != null && getStrActualBillingType().equals("D")) {
					pst.setDouble(8, uF.parseToDouble(getCostAmount()));
				} else if(getStrActualBillingType() != null && getStrActualBillingType().equals("M")) {
					double dbldayCostAmt = uF.parseToDouble(getCostAmount()) / 30;
					pst.setDouble(8, uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dbldayCostAmt)));
				} else {
					double dbldayCostAmt = uF.parseToDouble(getCostAmount()) * uF.parseToDouble(CF.getStrStandardHrs());
					pst.setDouble(8, uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dbldayCostAmt)));
				}
				if(getStrActualBillingType() != null && getStrActualBillingType().equals("M")) {
					pst.setDouble(9, uF.parseToDouble(getCostAmount()));
				} else if(getStrActualBillingType() != null && getStrActualBillingType().equals("D")) {
					double dblMonthCostAmt = uF.parseToDouble(getCostAmount()) * 30;
					pst.setDouble(9, uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblMonthCostAmt)));
				} else {
					double dbldayCostAmt = uF.parseToDouble(getCostAmount()) * uF.parseToDouble(CF.getStrStandardHrs());
					double dblMonthCostAmt = dbldayCostAmt * 30;
					pst.setDouble(9, uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblMonthCostAmt)));
				}
//				System.out.println("pst =====>> " + pst);
				pst.executeUpdate();
				pst.close();
			}	
			
			
			Map<String, String> hmProDetails = CF.getProjectDetailsByProId(con, getProId());
			
			Set<String> setEmp = new HashSet<String>(); 
			if(uF.parseToInt(hmProDetails.get("PRO_OWNER_ID")) > 0){
				setEmp.add(hmProDetails.get("PRO_OWNER_ID"));
			}
			
			pst = con.prepareStatement("select emp_id from project_emp_details where pro_id=? and _isteamlead = true");
			pst.setInt(1, uF.parseToInt(getProId()));
			rs = pst.executeQuery();
			while(rs.next()){
				if(uF.parseToInt(rs.getString("emp_id")) > 0){
					setEmp.add(rs.getString("emp_id"));
				}
			}
			rs.close();
			pst.close();
			
			String strDomain = request.getServerName().split("\\.")[0];
			
			String proName = CF.getProjectNameById(con, getProId());
			String resourceName = CF.getEmpNameMapByEmpId(con, getEmpId());
			String resourceImg = CF.getEmpImageByEmpId(con, uF, getEmpId());
			String alertData = "<div style=\"float: left;\"> <b>"+resourceName+"</b> has been added for <b>"+proName+"</b> project by <b> "+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>.</div>";
			String alertAction = "ViewAllProjects.action";
			StringBuilder taggedWith = null;
			for(String strEmp : setEmp) {
				if(taggedWith == null) {
					taggedWith = new StringBuilder();
					taggedWith.append(","+strEmp.trim()+",");
				} else {
					taggedWith.append(strEmp.trim()+",");
				}
				UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
				userAlerts.setStrDomain(strDomain);
				userAlerts.setStrEmpId(strEmp);
				userAlerts.setStrData(alertData);
				userAlerts.setStrAction(alertAction);
//				userAlerts.set_type(PRO_NEW_RESOURCE_ALERT);
				userAlerts.setStatus(INSERT_TR_ALERT);
				Thread t = new Thread(userAlerts);
				t.run();
			}
			
			if(taggedWith == null) {
				taggedWith = new StringBuilder();
			}
			
			String activityData = "<div style=\"float: left;\"><img class=\"lazy\" src=\"userImages/avatar_photo.png\" data-original=\""+ CF.getStrDocRetriveLocation() +I_PEOPLE+"/"+I_IMAGE+"/"+getEmpId()+"/"+I_22x22+"/"+resourceImg+"\" height=\"20\" width=\"20\"> <b>"+resourceName+"</b> has been added for <b>"+proName+"</b> project by <b> "+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>.</div>";
			UserActivities userAct = new UserActivities(con, uF, CF, request);
			userAct.setStrDomain(strDomain);
			userAct.setStrAlignWith(PROJECT+"");
			userAct.setStrAlignWithId(getProId());
			userAct.setStrTaggedWith(taggedWith.toString());
			userAct.setStrVisibilityWith(taggedWith.toString());
			userAct.setStrVisibility("2");
			userAct.setStrData(activityData);
			userAct.setStrSessionEmpId(strSessionEmpId);
			userAct.setStatus(INSERT_TR_ACTIVITY);
			Thread tt = new Thread(userAct);
			tt.run();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	private void deleteTLEmployee() {		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {
			
			con = db.makeConnection(con);
			pst = con.prepareStatement("delete from project_emp_details where pro_id=? and emp_id=? and _isteamlead=true");
			pst.setInt(1, uF.parseToInt(getProId()));
			pst.setInt(2, uF.parseToInt(getEmpId()));
			int x = pst.executeUpdate();
			pst.close();
			
			if(x > 0){
				int cnt = 0;
				pst = con.prepareStatement("select pro_created from user_alerts where emp_id=?");
				pst.setInt(1, uF.parseToInt(getEmpId()));
				rs = pst.executeQuery();
				while(rs.next()){
					cnt = uF.parseToInt(rs.getString("pro_created"));
				}
				rs.close();
				pst.close();
				
				if(cnt > 0){
					cnt = cnt - 1;
				}
				pst = con.prepareStatement("update user_alerts set pro_created=? where emp_id=?");
				pst.setInt(1, cnt);
				pst.setInt(2, uF.parseToInt(getEmpId()));
				pst.execute();
				pst.close();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}


	private void insertTLEmployee(CommonFunctions CF) {		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {
			
			con = db.makeConnection(con);
//			pst = con.prepareStatement("insert into project_emp_details (pro_id, emp_id, _isteamlead) values (?,?,?)");
//			pst.setInt(1, uF.parseToInt(getProId()));
//			pst.setInt(2, uF.parseToInt(getEmpId()));
//			pst.setBoolean(3, true);
//			pst.execute();
			pst = con.prepareStatement("update project_emp_details set _isteamlead=?,emp_rate_per_hour=?, emp_rate_per_day=?, emp_rate_per_month=?, " +
				"emp_actual_rate_per_hour=?, emp_actual_rate_per_day=?, emp_actual_rate_per_month=? where emp_id =? and pro_id = ?");
			pst.setBoolean(1, true);
			pst.setDouble(2, uF.parseToDouble(getRateHourAmount()));
			pst.setDouble(3, uF.parseToDouble(getRateDayAmount()));
			pst.setDouble(4, uF.parseToDouble(getRateMonthAmount()));
			if(getStrActualBillingType() != null && getStrActualBillingType().equals("H")) {
				pst.setDouble(5, uF.parseToDouble(getCostAmount()));
			} else if(getStrActualBillingType() != null && getStrActualBillingType().equals("M")) {
				double dbldayCostAmt = uF.parseToDouble(getCostAmount()) / 30;
				double dblhrCostAmt = 0;
				if(uF.parseToDouble(CF.getStrStandardHrs()) > 0) {
					dblhrCostAmt = dbldayCostAmt / uF.parseToDouble(CF.getStrStandardHrs());
				}
				pst.setDouble(5, uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblhrCostAmt)));
			} else {
				double dblhrCostAmt = 0;
				if(uF.parseToDouble(CF.getStrStandardHrs()) > 0) {
					dblhrCostAmt = uF.parseToDouble(getCostAmount()) / uF.parseToDouble(CF.getStrStandardHrs());
				}
				pst.setDouble(5, uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblhrCostAmt)));
			}
			if(getStrActualBillingType() != null && getStrActualBillingType().equals("D")) {
				pst.setDouble(6, uF.parseToDouble(getCostAmount()));
			} else if(getStrActualBillingType() != null && getStrActualBillingType().equals("M")) {
				double dbldayCostAmt = uF.parseToDouble(getCostAmount()) / 30;
				pst.setDouble(6, uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dbldayCostAmt)));
			} else {
				double dbldayCostAmt = uF.parseToDouble(getCostAmount()) * uF.parseToDouble(CF.getStrStandardHrs());
				pst.setDouble(6, uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dbldayCostAmt)));
			}
			if(getStrActualBillingType() != null && getStrActualBillingType().equals("M")) {
				pst.setDouble(7, uF.parseToDouble(getCostAmount()));
			} else if(getStrActualBillingType() != null && getStrActualBillingType().equals("D")) {
				double dblMonthCostAmt = uF.parseToDouble(getCostAmount()) * 30;
				pst.setDouble(7, uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblMonthCostAmt)));
			} else {
				double dbldayCostAmt = uF.parseToDouble(getCostAmount()) * uF.parseToDouble(CF.getStrStandardHrs());
				double dblMonthCostAmt = dbldayCostAmt * 30;
				pst.setDouble(7, uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblMonthCostAmt)));
			}
			pst.setInt(8, uF.parseToInt(getEmpId()));
			pst.setInt(9, uF.parseToInt(getProId()));
			int cnt = pst.executeUpdate();
			pst.close();
			
			if(cnt == 0) {
				pst = con.prepareStatement("insert into project_emp_details (pro_id, emp_id,_isteamlead,emp_rate_per_hour,emp_rate_per_day," +
					"emp_rate_per_month,emp_actual_rate_per_hour,emp_actual_rate_per_day,emp_actual_rate_per_month) values (?,?,?,? ,?,?,?,? ,?)");
				pst.setInt(1, uF.parseToInt(getProId()));
				pst.setInt(2, uF.parseToInt(getEmpId()));
				pst.setBoolean(3, true);
				pst.setDouble(4, uF.parseToDouble(getRateHourAmount()));
				pst.setDouble(5, uF.parseToDouble(getRateDayAmount()));
				pst.setDouble(6, uF.parseToDouble(getRateMonthAmount()));
				if(getStrActualBillingType() != null && getStrActualBillingType().equals("H")) {
					pst.setDouble(7, uF.parseToDouble(getCostAmount()));
				} else if(getStrActualBillingType() != null && getStrActualBillingType().equals("M")) {
					double dbldayCostAmt = uF.parseToDouble(getCostAmount()) / 30;
					double dblhrCostAmt = 0;
					if(uF.parseToDouble(CF.getStrStandardHrs()) > 0) {
						dblhrCostAmt = dbldayCostAmt / uF.parseToDouble(CF.getStrStandardHrs());
					}
					pst.setDouble(7, uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblhrCostAmt)));
				} else {
					double dblhrCostAmt = 0;
					if(uF.parseToDouble(CF.getStrStandardHrs()) > 0) {
						dblhrCostAmt = uF.parseToDouble(getCostAmount()) / uF.parseToDouble(CF.getStrStandardHrs());
					}
					pst.setDouble(7, uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblhrCostAmt)));
				}
				if(getStrActualBillingType() != null && getStrActualBillingType().equals("D")) {
					pst.setDouble(8, uF.parseToDouble(getCostAmount()));
				} else if(getStrActualBillingType() != null && getStrActualBillingType().equals("M")) {
					double dbldayCostAmt = uF.parseToDouble(getCostAmount()) / 30;
					pst.setDouble(8, uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dbldayCostAmt)));
				} else {
					double dbldayCostAmt = uF.parseToDouble(getCostAmount()) * uF.parseToDouble(CF.getStrStandardHrs());
					pst.setDouble(8, uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dbldayCostAmt)));
				}
				if(getStrActualBillingType() != null && getStrActualBillingType().equals("M")) {
					pst.setDouble(9, uF.parseToDouble(getCostAmount()));
				} else if(getStrActualBillingType() != null && getStrActualBillingType().equals("D")) {
					double dblMonthCostAmt = uF.parseToDouble(getCostAmount()) * 30;
					pst.setDouble(9, uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblMonthCostAmt)));
				} else {
					double dbldayCostAmt = uF.parseToDouble(getCostAmount()) * uF.parseToDouble(CF.getStrStandardHrs());
					double dblMonthCostAmt = dbldayCostAmt * 30;
					pst.setDouble(9, uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblMonthCostAmt)));
				}
	//			System.out.println("pst =====>> " + pst);
				pst.executeUpdate();
				pst.close();
			}
			
			
			Map<String, String> hmProDetails = CF.getProjectDetailsByProId(con, getProId());
			
			Set<String> setEmp = new HashSet<String>(); 
			if(uF.parseToInt(hmProDetails.get("PRO_OWNER_ID")) > 0){
				setEmp.add(hmProDetails.get("PRO_OWNER_ID"));
			}
			
			pst = con.prepareStatement("select emp_id from project_emp_details where pro_id=? and _isteamlead = true");
			pst.setInt(1, uF.parseToInt(getProId()));
			rs = pst.executeQuery();
			while(rs.next()){
				if(uF.parseToInt(rs.getString("emp_id")) > 0){
					setEmp.add(rs.getString("emp_id"));
				}
			}
			rs.close();
			pst.close();
			
			String strDomain = request.getServerName().split("\\.")[0];
			String proName = CF.getProjectNameById(con, getProId());
			String resourceName = CF.getEmpNameMapByEmpId(con, getEmpId());
			String resourceImg = CF.getEmpImageByEmpId(con, uF, getEmpId());
			String alertData = "<div style=\"float: left;\"> <b>"+resourceName+"</b> has been added for <b>"+proName+"</b> project by <b> "+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>.</div>";
			String alertAction = "ViewAllProjects.action";
			StringBuilder taggedWith = null;
			for(String strEmp : setEmp) {
				if(taggedWith == null) {
					taggedWith = new StringBuilder();
					taggedWith.append(","+strEmp.trim()+",");
				} else {
					taggedWith.append(strEmp.trim()+",");
				}
				UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
				userAlerts.setStrDomain(strDomain);
				userAlerts.setStrEmpId(strEmp);
				userAlerts.setStrData(alertData);
				userAlerts.setStrAction(alertAction);
	//			userAlerts.set_type(PRO_CREATED_ALERT);
				userAlerts.setStatus(INSERT_TR_ALERT);
				Thread t = new Thread(userAlerts);
				t.run();
			}
			
			if(taggedWith == null) {
				taggedWith = new StringBuilder();
			}
			
			String activityData = "<div style=\"float: left;\"><img class=\"lazy\" src=\"userImages/avatar_photo.png\" data-original=\""+ CF.getStrDocRetriveLocation() +I_PEOPLE+"/"+I_IMAGE+"/"+getEmpId()+"/"+I_22x22+"/"+resourceImg+"\" height=\"20\" width=\"20\"> <b>"+resourceName+"</b> has been added for <b>"+proName+"</b> project by <b> "+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>.</div>";
			UserActivities userAct = new UserActivities(con, uF, CF, request);
			userAct.setStrDomain(strDomain);
			userAct.setStrAlignWith(PROJECT+"");
			userAct.setStrAlignWithId(getProId());
			userAct.setStrTaggedWith(taggedWith.toString());
			userAct.setStrVisibilityWith(taggedWith.toString());
			userAct.setStrVisibility("2");
			userAct.setStrData(activityData);
			userAct.setStrSessionEmpId(strSessionEmpId);
			userAct.setStatus(INSERT_TR_ACTIVITY);
			Thread tt = new Thread(userAct);
			tt.run();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	private void getData(CommonFunctions CF, UtilityFunctions uF) {
		
		List<String> alEmpId = new ArrayList<String>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			
			con = db.makeConnection(con);
			
			Map<String, String> hmProInfoDisplay = CF.getProjectInformationDisplay(con);
			request.setAttribute("hmProInfoDisplay", hmProInfoDisplay);
			Map<String, String> hmSkillName = CF.getSkillNameMap(con);
			
			Map<String, String> hmEmpNames = CF.getEmpNameMap(con,null, null);
			Map<String, String> hmEmpLevel = CF.getEmpLevelMap(con);
			Map<String, String> hmLevel = CF.getLevelMap(con);
			Map<String, String> hmEmpWLocation = CF.getEmpWlocationMap(con);
			Map<String, Map<String, String>> hmWLocation = CF.getWorkLocationMap(con);
			
//			Map<String,String> hmLevelDayRateMap=new HashMap<String,String>();
//			Map<String,String> hmLevelHourRateMap=new HashMap<String,String>();
			Map<String,String> hmEmpList = new HashMap<String,String>();
			
			pst = con.prepareStatement("select * from project_emp_details where pro_id=?");
			pst.setInt(1, uF.parseToInt(getProId()));
			rs = pst.executeQuery();
//			System.out.println("pst ===>> " +pst);
			
			Map<String, String> hmTLMembEmp = new HashMap<String, String>();
			StringBuilder existEmpIds = null;
			while (rs.next()) {
				if(existEmpIds == null) {
					existEmpIds = new StringBuilder();
					existEmpIds.append(rs.getString("emp_id"));
				} else {
					existEmpIds.append("," + rs.getString("emp_id"));
				}
				
				if(uF.parseToBoolean(rs.getString("_isteamlead"))) {
					hmTLMembEmp.put(rs.getString("emp_id")+"_T", "TL");
				} else {
					hmTLMembEmp.put(rs.getString("emp_id")+"_M", "MEMB");
				}
			}
			rs.close();
			pst.close();
	
//			System.out.println("hmTLMembEmp ===>>>> " + hmTLMembEmp);
			
			request.setAttribute("hmTLMembEmp", hmTLMembEmp);
	
			
			Map<String, String> hmProDetails = CF.getProjectDetailsByProId(con, getProId());
			Map<String, String> hmCurrToFromVal = CF.getCurrencyFromIdToIdValue(con);
			Map<String, Map<String, String>> hmCurrData = CF.getCurrencyDetails(con);
			
			request.setAttribute("strProCurrId", hmProDetails.get("PRO_REPORT_CURR_ID"));
			
			Map<String, String> hmCurrInner = hmCurrData.get(hmProDetails.get("PRO_REPORT_CURR_ID"));
			
//			setActBillingType(hmProDetails.get("PRO_BILLING_ACTUAL_TYPE"));
			if(hmCurrInner != null) {
				setStrShortCurrency(hmCurrInner.get("SHORT_CURR"));
			}
			pst = con.prepareStatement("SELECT * FROM project_emp_details where pro_id=?");
			pst.setInt(1, uF.parseToInt(getProId()));
			rs=pst.executeQuery();
			Map<String,String> hmEmpCostAndRate = new HashMap<String,String>();
			Map<String,String> hmEmpAllocatePercentAndBilledUnbilled = new HashMap<String,String>();
			while(rs.next()) {
				
				if(hmProDetails.get("PRO_BILLING_ACTUAL_TYPE")!=null && hmProDetails.get("PRO_BILLING_ACTUAL_TYPE").equalsIgnoreCase("D")) {
					hmEmpCostAndRate.put(rs.getString("emp_id") + "_RATE", uF.formatIntoTwoDecimalWithOutComma(rs.getDouble("emp_rate_per_day")));
					hmEmpCostAndRate.put(rs.getString("emp_id") + "_COST", uF.formatIntoTwoDecimalWithOutComma(rs.getDouble("emp_actual_rate_per_day")));
				} else if(hmProDetails.get("PRO_BILLING_ACTUAL_TYPE")!=null && hmProDetails.get("PRO_BILLING_ACTUAL_TYPE").equalsIgnoreCase("H")) {
					hmEmpCostAndRate.put(rs.getString("emp_id") + "_RATE", uF.formatIntoTwoDecimalWithOutComma(rs.getDouble("emp_rate_per_hour")));
					hmEmpCostAndRate.put(rs.getString("emp_id") + "_COST", uF.formatIntoTwoDecimalWithOutComma(rs.getDouble("emp_actual_rate_per_hour")));
				} else if(hmProDetails.get("PRO_BILLING_ACTUAL_TYPE")!=null && hmProDetails.get("PRO_BILLING_ACTUAL_TYPE").equalsIgnoreCase("M")) {
					hmEmpCostAndRate.put(rs.getString("emp_id") + "_RATE", uF.formatIntoTwoDecimalWithOutComma(rs.getDouble("emp_rate_per_month")));
					hmEmpCostAndRate.put(rs.getString("emp_id") + "_COST", uF.formatIntoTwoDecimalWithOutComma(rs.getDouble("emp_actual_rate_per_month")));
				} else {
					hmEmpCostAndRate.put(rs.getString("emp_id") + "_RATE", uF.formatIntoTwoDecimalWithOutComma(rs.getDouble("emp_rate_per_hour")));
					hmEmpCostAndRate.put(rs.getString("emp_id") + "_COST", uF.formatIntoTwoDecimalWithOutComma(rs.getDouble("emp_actual_rate_per_hour")));
				}
				hmEmpAllocatePercentAndBilledUnbilled.put(rs.getString("emp_id") + "_ALLOCATION_PERCENT", uF.showData(rs.getString("allocation_percent"), "0"));
				hmEmpAllocatePercentAndBilledUnbilled.put(rs.getString("emp_id") + "_BILLED_UNBILLED", rs.getString("is_billed"));
				hmEmpAllocatePercentAndBilledUnbilled.put(rs.getString("emp_id") + "_ALLOCATION_DATE", rs.getString("allocation_date")!=null ? uF.getDateFormat(rs.getString("allocation_date"), DBDATE, DATE_FORMAT) : "");
				hmEmpAllocatePercentAndBilledUnbilled.put(rs.getString("emp_id") + "_RELEASE_DATE", rs.getString("release_date")!=null ? uF.getDateFormat(rs.getString("release_date"), DBDATE, DATE_FORMAT) : "");
			}
			rs.close();
			pst.close();
			request.setAttribute("hmEmpCostAndRate", hmEmpCostAndRate);
			request.setAttribute("hmEmpAllocatePercentAndBilledUnbilled", hmEmpAllocatePercentAndBilledUnbilled);
			
			
			Map<String, String> hmEmpSkills = new HashMap<String, String>();
//			Map<String, String> hmEmpSkillsRates = new HashMap<String, String>();
			Map<String, String> hmTaskAllocation  = new HashMap<String, String>();
			
			
			if(existEmpIds != null && !existEmpIds.equals("")) {
				StringBuilder sbQuery = new StringBuilder();
				//sbQuery.append("SELECT * FROM employee_personal_details epd, employee_official_details eod,skills_description sd where epd.emp_per_id=sd.emp_id and eod.emp_id = epd.emp_per_id and eod.emp_id = sd.emp_id and is_alive=true ");
	//			sbQuery.append("SELECT eod.emp_id, skill_id FROM employee_official_details eod left join skills_description sd on eod.emp_id = sd.emp_id where eod.emp_id in ("+existEmpIds.toString()+") ");
				sbQuery.append("SELECT eod.emp_id, sd.skill_id,sd.skills_value FROM employee_official_details eod left join skills_description sd on eod.emp_id = sd.emp_id " +
					"left join skills_details sd1 on sd.skill_id=sd1.skill_id where eod.emp_id in ("+existEmpIds.toString()+") "); //and sd.skills_value is not null and sd.skills_value !='' 
				sbQuery.append(" order by sd.skills_value desc,eod.emp_id");
	//			sbQuery.append(" order by eod.emp_id");
				pst = con.prepareStatement(sbQuery.toString());
				rs = pst.executeQuery();
	//			System.out.println("pst ===>> " +pst);
				String empSkills = "";
				while(rs.next()) {
					empSkills = hmEmpSkills.get(rs.getString("emp_id"));
					if(empSkills == null) empSkills = "";
					
					double skillVal = (uF.parseToDouble(rs.getString("skills_value"))/2);
					if(rs.getInt("skill_id")>0) {
						if(empSkills != null && empSkills.equals("")) {
							empSkills = uF.showData(hmSkillName.get(rs.getString("skill_id")),"")+" ("+uF.formatIntoOneDecimalIfDecimalValIsThere(skillVal)+")";
						} else {
							empSkills = empSkills + "<br/>"+ uF.showData(hmSkillName.get(rs.getString("skill_id")),"")+" ("+uF.formatIntoOneDecimalIfDecimalValIsThere(skillVal)+")";
						}
					}
					hmEmpSkills.put(rs.getString("emp_id"), empSkills);
					if(!alEmpId.contains(rs.getString("emp_id"))) {
						alEmpId.add(rs.getString("emp_id"));
					}
				}
				rs.close();
				pst.close();
	//			System.out.println("hmEmpSkills ====>>> " +hmEmpSkills);
	//			System.out.println("alEmpId ===>> " + alEmpId);
				
				List<String> liveProEmpIds = new ArrayList<String>();
				pst = con.prepareStatement("select resource_ids from activity_info ai, projectmntnc p where ai.pro_id = p.pro_id and p.approve_status = 'n' " +
					"and (ai.completed < 100 or ai.completed is null) and ((ai.start_date >= ? and ai.deadline <= ?) or (ai.start_date <= ? and ai.deadline >= ?) or (ai.start_date >= ? and ai.start_date <= ?))");
				pst.setDate(1, uF.getDateFormat(hmProDetails.get("PRO_START_DATE"), DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(hmProDetails.get("PRO_END_DATE"), DATE_FORMAT));
				pst.setDate(3, uF.getDateFormat(hmProDetails.get("PRO_START_DATE"), DATE_FORMAT));
				pst.setDate(4, uF.getDateFormat(hmProDetails.get("PRO_START_DATE"), DATE_FORMAT));
				pst.setDate(5, uF.getDateFormat(hmProDetails.get("PRO_START_DATE"), DATE_FORMAT));
				pst.setDate(6, uF.getDateFormat(hmProDetails.get("PRO_END_DATE"), DATE_FORMAT));
				rs = pst.executeQuery();
//				System.out.println("liveProEmpIds pst === >> " +pst);
				
				while(rs.next()) {
					if(rs.getString("resource_ids") != null && !rs.getString("resource_ids").equals("")) {
						List<String> empIdList = Arrays.asList(rs.getString("resource_ids").split(","));
						 for(String empId : empIdList) {
							 if(empId!=null && !empId.equals("")) {
								 if(!liveProEmpIds.contains(empId)) {
									 liveProEmpIds.add(empId);
								 }
							 }
						 }  
					}
				}
				rs.close();
				pst.close();
	
	//			System.out.println("liveProEmpIds ===>> " + liveProEmpIds);
				for(int a=0; liveProEmpIds != null && a< liveProEmpIds.size(); a++) {
					pst = con.prepareStatement("select count(a.*) as task_no from (select task_id,activity_name,parent_task_id,pro_id,start_date,deadline," +
						"completed from activity_info where resource_ids like '%,"+liveProEmpIds.get(a)+",%' and task_id not in (select parent_task_id " +
						" from activity_info where resource_ids like '%,"+liveProEmpIds.get(a)+",%' and parent_task_id is not null)) a, projectmntnc pmc " +
						" where pmc.pro_id=a.pro_id and pmc.approve_status='n' and (parent_task_id in (select task_id from activity_info where resource_ids " +
						" like '%,"+liveProEmpIds.get(a)+",%') or parent_task_id = 0) and (a.completed < 100 or a.completed is null) and ((a.start_date >= ? and a.deadline <= ?) or " +
						" (a.start_date <= ? and a.deadline >= ?) or (a.start_date >= ? and a.start_date <= ?))");
					pst.setDate(1, uF.getDateFormat(hmProDetails.get("PRO_START_DATE"), DATE_FORMAT));
					pst.setDate(2, uF.getDateFormat(hmProDetails.get("PRO_END_DATE"), DATE_FORMAT));
					pst.setDate(3, uF.getDateFormat(hmProDetails.get("PRO_START_DATE"), DATE_FORMAT));
					pst.setDate(4, uF.getDateFormat(hmProDetails.get("PRO_START_DATE"), DATE_FORMAT));
					pst.setDate(5, uF.getDateFormat(hmProDetails.get("PRO_START_DATE"), DATE_FORMAT));
					pst.setDate(6, uF.getDateFormat(hmProDetails.get("PRO_END_DATE"), DATE_FORMAT));
//					System.out.println("pst ===>> " + pst);
					rs = pst.executeQuery();
					while(rs.next()) {
						if(uF.parseToInt(rs.getString("task_no")) > 5) {
							hmTaskAllocation.put(liveProEmpIds.get(a), "<div style=\"width: 100%; height: 100%; text-align: center; background-color: red;\"><a href=\"javascript:void(0);\" onclick=\"viewWorkAllocation('"+liveProEmpIds.get(a)+"','"+hmProDetails.get("PRO_START_DATE")+"','"+hmProDetails.get("PRO_END_DATE")+"')\">"+uF.parseToInt(rs.getString("task_no"))+"</a></div>");
						} else if(uF.parseToInt(rs.getString("task_no")) >= 2) {
							hmTaskAllocation.put(liveProEmpIds.get(a), "<div style=\"width: 100%; height: 100%; text-align: center; background-color: yellow;\"><a href=\"javascript:void(0);\" onclick=\"viewWorkAllocation('"+liveProEmpIds.get(a)+"','"+hmProDetails.get("PRO_START_DATE")+"','"+hmProDetails.get("PRO_END_DATE")+"')\">"+uF.parseToInt(rs.getString("task_no"))+"</a></div>");
						} else if(uF.parseToInt(rs.getString("task_no")) > 0) {
							hmTaskAllocation.put(liveProEmpIds.get(a), "<div style=\"width: 100%; height: 100%; text-align: center; background-color: lightgreen;\"><a href=\"javascript:void(0);\" onclick=\"viewWorkAllocation('"+liveProEmpIds.get(a)+"','"+hmProDetails.get("PRO_START_DATE")+"','"+hmProDetails.get("PRO_END_DATE")+"')\">"+uF.parseToInt(rs.getString("task_no"))+"</a></div>");
						}
					}
					rs.close();
					pst.close();
				}
				
				sbQuery = new StringBuilder();
				sbQuery.append("select * from emp_prev_employment epe, employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id and " +
					" epe.emp_id=eod.emp_id and eod.emp_id in ("+existEmpIds.toString()+") and from_date is not null and to_date is not null and joining_date <=? order by emp_fname, emp_lname");
				pst = con.prepareStatement(sbQuery.toString());
				pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
//				System.out.println("pst=======>"+pst);
				rs = pst.executeQuery();
				Map<String,List<List<String>>>hmEmpStEndMonth=new LinkedHashMap<String,List<List<String>>>();
				while(rs.next()){
					List<List<String>>alOuterEmpStEndMonth = hmEmpStEndMonth.get(rs.getString("emp_id"));
					if(alOuterEmpStEndMonth==null)alOuterEmpStEndMonth = new ArrayList<List<String>>();
					
					List<String>alInnerEmpStEndMonth=new ArrayList<String>();
					alInnerEmpStEndMonth.add(uF.showData(rs.getString("from_date"), ""));
					alInnerEmpStEndMonth.add(uF.showData(rs.getString("to_date"), ""));
					alOuterEmpStEndMonth.add(alInnerEmpStEndMonth);
					hmEmpStEndMonth.put(uF.showData(rs.getString("emp_id"),""), alOuterEmpStEndMonth);
				}
				rs.close();
				pst.close();
//				System.out.println("hmEmpStEndMonth ===>> " + hmEmpStEndMonth);
				
				sbQuery = new StringBuilder();
				sbQuery.append("select * from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id " +
					"and eod.emp_id in ("+existEmpIds.toString()+") and joining_date <=? order by emp_fname, emp_lname");
				pst = con.prepareStatement(sbQuery.toString());
				pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
				rs = pst.executeQuery();
				Map<String, String> hmEmpTotRelExp = new HashMap<String, String>();
				while(rs.next()){
					hmEmpTotRelExp.put(rs.getString("emp_id")+"_TOT_EXP", rs.getString("total_experience"));
					hmEmpTotRelExp.put(rs.getString("emp_id")+"_REL_EXP", rs.getString("relevant_experience"));
				}
				rs.close();
				pst.close();
				
				Map<String, String> hmEmpJoiningDate = CF.getEmpJoiningDateMap(con, uF);
//				Iterator<String> it1=hmEmpStEndMonth.keySet().iterator();
//				alEmpId = new ArrayList<String>();
//				alEmpId.add("1707");
//				alEmpId.add("1749");
//				alEmpId.add("1750");
//				alEmpId.add("683");
				Map<String,String> hmEmployeeExperience=new LinkedHashMap<String, String>();
				for(int a=0; alEmpId!=null && a<alEmpId.size(); a++) {
					String empid = alEmpId.get(a);
					List<List<String>>alOuterEmpStEndMonth = hmEmpStEndMonth.get(empid);
					long datediffOuter=0;
					long datediffInner=0;
					long datediff=0;
					int noyear = 0,nomonth = 0,nodays = 0;
					for(int i=0;alOuterEmpStEndMonth!=null && i<alOuterEmpStEndMonth.size();i++) {
						List<String>alInnerEmpStEndMonth=alOuterEmpStEndMonth.get(i);
						String stdt=alInnerEmpStEndMonth.get(0);
						String endDt=alInnerEmpStEndMonth.get(1);
						if(stdt!=null && endDt!=null && !stdt.equals("") && !endDt.equals("")) {
							String datedif = uF.dateDifference(uF.showData(stdt, ""), DBDATE, uF.showData(endDt, ""), DBDATE);
							datediff = uF.parseToLong(datedif);
							datediffInner = datediff+datediffInner;
						}/* else {
							datediff=0;
							datediffInner=0;
						}*/
					}
					if(alOuterEmpStEndMonth==null || alOuterEmpStEndMonth.size()==0) {
						datediffInner = (uF.parseToLong(hmEmpTotRelExp.get(empid+"_TOT_EXP")) * 365);
					}
//					System.out.println(empid+ " -- hmEmpJoiningDate ===>> " + hmEmpJoiningDate);
					if(hmEmpJoiningDate!=null && hmEmpJoiningDate.get(empid)!=null && !hmEmpJoiningDate.get(empid).equals("")) {
						String datedif = uF.dateDifference(hmEmpJoiningDate.get(empid), DATE_FORMAT, uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE);
						datediff = uF.parseToLong(datedif);
						datediffInner = datediff+datediffInner;
					}
					
					datediffOuter = datediffInner;
					datediffInner=0;
					
					noyear+=(int) (datediffOuter/365);
			    	nomonth+=(int) ((datediffOuter%365)/30);
			    	nodays+=(int) ((datediffOuter%365)%30);
			     
			    	if(nodays>30){
			    		nomonth=nomonth+1;
			    	}
			    	if(nomonth>12){
			    		nomonth=nomonth-12;
			    		noyear=noyear+1;
			    	}
			    	
			    	String yearsLbl = " yrs ";
			    	if(noyear == 1) {
			    		yearsLbl = " yr ";
			    	}
			    	
			    	String monthLbl = " Months ";
			    	if(nomonth == 1) {
			    		monthLbl = " Month ";
			    	}
			    	
			    	hmEmployeeExperience.put(empid, ""+noyear+"."+nomonth+yearsLbl); 
//						System.out.println(empid+"--"+uF.showData((String)hmEmployeeExperience.get(empid), "N/A"));
				}
				request.setAttribute("hmEmployeeExperience", hmEmployeeExperience);
				
				pst = con.prepareStatement("SELECT p.pro_id, p.pro_name, pf.freq_end_date, ped.emp_id, ped.is_billed, ped.allocation_percent,ped.release_date from projectmntnc p, projectmntnc_frequency pf, project_emp_details ped where p.pro_id=pf.pro_id " +
					" and p.pro_id=ped.pro_id and p.pro_id not in ("+getProId()+") and (? between allocation_date and release_date or ? between allocation_date and release_date or allocation_date between ? and ? or release_date between ? and ?) and approve_status != 'approved' order by ped.emp_id,allocation_date");
				pst.setDate(1, uF.getDateFormat(hmProDetails.get("PRO_START_DATE"), DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(hmProDetails.get("PRO_END_DATE"), DATE_FORMAT));
				pst.setDate(3, uF.getDateFormat(hmProDetails.get("PRO_START_DATE"), DATE_FORMAT));
				pst.setDate(4, uF.getDateFormat(hmProDetails.get("PRO_END_DATE"), DATE_FORMAT));
				pst.setDate(5, uF.getDateFormat(hmProDetails.get("PRO_START_DATE"), DATE_FORMAT));
				pst.setDate(6, uF.getDateFormat(hmProDetails.get("PRO_END_DATE"), DATE_FORMAT));
				rs = pst.executeQuery();
		//		System.out.println("liveProEmpIds pst === >> " +pst);
				Map<String, Map<String, List<String>>> hmEmpProDetails = new HashMap<String, Map<String,List<String>>>();
				while(rs.next()) {
					Map<String, List<String>> hmProData = hmEmpProDetails.get(rs.getString("emp_id"));
					if(hmProData==null)hmProData = new HashMap<String, List<String>>();
					List<String> innerList = new ArrayList<String>();
					innerList.add(rs.getString("pro_name"));
					innerList.add(uF.getDateFormat(rs.getString("release_date"), DBDATE, DATE_FORMAT)); //freq_end_date
					innerList.add(rs.getString("is_billed"));
					innerList.add(rs.getString("allocation_percent"));
					hmProData.put(rs.getString("pro_id"), innerList);
					hmEmpProDetails.put(rs.getString("emp_id"), hmProData);
				}
				rs.close();
				pst.close();
				request.setAttribute("hmEmpProDetails", hmEmpProDetails);
			}
		
		
		
			Map<String, Map<String, String>> hmLeaveDatesType = new HashMap<String, Map<String, String>>();
//			Map<String, Map<String, String>> hmLeaves = CF.getLeaveDates(con, hmProDetails.get("PRO_START_DATE"), hmProDetails.get("PRO_END_DATE"), CF, hmLeaveDatesType, false, null);
			Map<String, Map<String, String>> hmLeaves = CF.getActualLeaveDates(con, CF, uF, hmProDetails.get("PRO_START_DATE"), hmProDetails.get("PRO_END_DATE"), hmLeaveDatesType, false, null);
			
//			Map<String, String> hmEmpSalaryMap = CF.getEmpNetSalary(uF, CF, con, uF.getCurrentDate(CF.getStrTimeZone())+"", strActualBillingType);
			
			request.setAttribute("strActualBillingType", hmProDetails.get("PRO_BILLING_ACTUAL_TYPE"));
			request.setAttribute("hmLeaves", hmLeaves);
//			request.setAttribute("hmEmpSalaryMap", hmEmpSalaryMap);
			request.setAttribute("hmEmpSkills", hmEmpSkills);
//			request.setAttribute("hmEmpSkillsRates", hmEmpSkillsRates);
			request.setAttribute("hmTaskAllocation", hmTaskAllocation);
			
			request.setAttribute("alEmpId", alEmpId);
			request.setAttribute("hmEmpList", hmEmpList);
			request.setAttribute("hmEmpNames", hmEmpNames);
			request.setAttribute("hmEmpLevel", hmEmpLevel);
			request.setAttribute("hmLevel", hmLevel);
			request.setAttribute("hmEmpWLocation", hmEmpWLocation);
			request.setAttribute("hmWLocation", hmWLocation);
			
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

	public String getChboxStatus() {
		return chboxStatus;
	}

	public void setChboxStatus(String chboxStatus) {
		this.chboxStatus = chboxStatus;
	}

	public String getEmpId() {
		return empId;
	}

	public void setEmpId(String empId) {
		this.empId = empId;
	}

	public String getProId() {
		return proId;
	}

	public void setProId(String proId) {
		this.proId = proId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getStrActualBillingType() {
		return strActualBillingType;
	}

	public void setStrActualBillingType(String strActualBillingType) {
		this.strActualBillingType = strActualBillingType;
	}

	public String getRateDayAmount() {
		return rateDayAmount;
	}

	public void setRateDayAmount(String rateDayAmount) {
		this.rateDayAmount = rateDayAmount;
	}

	public String getRateHourAmount() {
		return rateHourAmount;
	}

	public void setRateHourAmount(String rateHourAmount) {
		this.rateHourAmount = rateHourAmount;
	}

	public String getRateMonthAmount() {
		return rateMonthAmount;
	}

	public void setRateMonthAmount(String rateMonthAmount) {
		this.rateMonthAmount = rateMonthAmount;
	}

	public String getCostAmount() {
		return costAmount;
	}

	public void setCostAmount(String costAmount) {
		this.costAmount = costAmount;
	}

	public String getStrShortCurrency() {
		return strShortCurrency;
	}

	public void setStrShortCurrency(String strShortCurrency) {
		this.strShortCurrency = strShortCurrency;
	}

	public String getFromPage() {
		return fromPage;
	}

	public void setFromPage(String fromPage) {
		this.fromPage = fromPage;
	}

	public String getProType() {
		return proType;
	}

	public void setProType(String proType) {
		this.proType = proType;
	}

}
