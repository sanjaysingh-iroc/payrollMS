package com.konnect.jpms.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.konnect.jpms.task.GetAlignedTypeData;

public class UserActivities implements Runnable, IConstants {
	Connection con;
	UtilityFunctions uF;
	CommonFunctions CF;
	String strSessionEmpId;
	String strAlignWith;
	String strAlignWithId;
	String strTaggedWith;
	String strVisibility;
	String strVisibilityWith;
	HttpServletRequest request;
	String strDomain;
	String activityType;
	String strData; 
	String alertID;
	String status;
	String strOther;
	String strUserType;
	String bDayEmpId;

	public UserActivities(Connection con, UtilityFunctions uF, CommonFunctions CF, HttpServletRequest request) {
		this.con = con;
		this.uF = uF;
		this.CF = CF;
		this.request = request;
	}
	
	public void run() {
		if(getStrOther()!=null && getStrOther().equalsIgnoreCase("other")) {
			if (status.equals(INSERT_TR_ACTIVITY)) {
				insertOtherUserAlerts(con, CF, uF);
			}else if (status.equals(DELETE_TR_ACTIVITY)) {
//				deleteTRUserAlerts(con, CF, uF, alertID);
			}
		} else if (status.equals(INSERT_TR_ACTIVITY)) {
			insertTRUserAlerts(con, CF, uF);
			
		} else if (status.equals(DELETE_TR_ACTIVITY)) {
//			deleteTRUserAlerts(con, CF, uF, alertID);
			
		}
	}

	
	private void deleteTRUserAlerts(Connection con, CommonFunctions CF, UtilityFunctions uF, String alertID) {
		
		PreparedStatement pst = null;
		try {
			 pst = con.prepareStatement("delete from communication_1 where communication_id=?");			
			 pst.setInt(1, uF.parseToInt(alertID));
//			 System.out.println("pst ===> " +pst);
			 pst.executeUpdate();
			 pst.close();
			 
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	


	private void insertOtherUserAlerts(Connection con, CommonFunctions CF, UtilityFunctions uF) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("insert into communication_1(communication,align_with,align_with_id,client_tagged_with,doc_shared_with_id,doc_id," +
				"visibility,client_visibility_with_id");
			if(getStrUserType() == null || !getStrUserType().equals("C")) {
			sbQuery.append(",created_by");
			} else {
				sbQuery.append(",client_created_by");
			}
			sbQuery.append(",create_time,feed_type");
			if(getActivityType() != null && getActivityType().equalsIgnoreCase("BDAY")) {
				sbQuery.append(",bday_emp_id");
			} else if(getActivityType() != null && getActivityType().equalsIgnoreCase("MANNIVERSARY")) {
				sbQuery.append(",manniversary_emp_id");
			} else if(getActivityType() != null && getActivityType().equalsIgnoreCase("WANNIVERSARY")) {
				sbQuery.append(",wanniversary_emp_id");
			}
			sbQuery.append(") values(?,?,?,?, ?,?,?,?, ?,?,?");
			if(getActivityType() != null && (getActivityType().equalsIgnoreCase("BDAY") || getActivityType().equalsIgnoreCase("MANNIVERSARY") || getActivityType().equalsIgnoreCase("WANNIVERSARY"))) {
				sbQuery.append(",?");
			}
			sbQuery.append(")");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setString(1, strData);
			pst.setInt(2, uF.parseToInt(getStrAlignWith()));
			pst.setInt(3, uF.parseToInt(getStrAlignWithId()));
			pst.setString(4, getStrTaggedWith());
			pst.setString(5, "");
			pst.setString(6, "");
			pst.setInt(7, uF.parseToInt(getStrVisibility()));
			pst.setString(8, getStrVisibilityWith());
			pst.setInt(9, uF.parseToInt(strSessionEmpId));
			pst.setTimestamp(10, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone())+"", DBDATE+DBTIME));
			pst.setInt(11, FT_ACTIVITY);
			if(getActivityType() != null && (getActivityType().equalsIgnoreCase("BDAY") || getActivityType().equalsIgnoreCase("MANNIVERSARY") || getActivityType().equalsIgnoreCase("WANNIVERSARY"))) {
				pst.setInt(12, uF.parseToInt(getbDayEmpId()));
			}
//			System.out.println("pst ===>>>> " + pst);
			pst.executeUpdate();
			pst.close();
			
			
			
			if(getActivityType() != null && (getActivityType().equalsIgnoreCase("BDAY") || getActivityType().equalsIgnoreCase("MANNIVERSARY") || getActivityType().equalsIgnoreCase("WANNIVERSARY"))) {
//				String strDomain = request.getServerName().split("\\.")[0];
				int strNotiId = 0; 
				if(getActivityType() != null && getActivityType().equalsIgnoreCase("BDAY")) {
					strNotiId = N_EMPLOYEE_BIRTHDAY;
				} else if(getActivityType() != null && getActivityType().equalsIgnoreCase("MANNIVERSARY")) {
					strNotiId = N_EMPLOYEE_MARRIAGE_ANNIVERSARY;
				} else if(getActivityType() != null && getActivityType().equalsIgnoreCase("WANNIVERSARY")) {
					strNotiId = N_EMPLOYEE_WORK_ANNIVERSARY;
				}
				
				pst = con.prepareStatement("Select * FROM notifications where notification_code=?");
				pst.setInt(1, strNotiId); 
				rs = pst.executeQuery();
//				System.out.println("pst ===>> " + pst);
				String strEmailBody =null;
				String strEmailSubject =null;
				String strTextContent =null;
				boolean isEmailNotifications = false;
				boolean isTextNotifications = false;
				while(rs.next()) {
					strEmailBody = rs.getString("email_notification");
					strEmailSubject = rs.getString("email_subject");
					strTextContent = rs.getString("text_notification");
					isEmailNotifications = uF.parseToBoolean(rs.getString("isemail"));
					isTextNotifications = uF.parseToBoolean(rs.getString("istext"));
				}
				rs.close();
				pst.close();
				
				if((isEmailNotifications || isTextNotifications) && ((strTextContent!=null && !strTextContent.trim().equals("")) || (strEmailBody!=null && !strEmailBody.trim().equals("")))) {
					String strJoiningDate = null;
					String strWorkTimeDuration = null;
					if(getActivityType() != null && getActivityType().equalsIgnoreCase("WANNIVERSARY")) {
						pst = con.prepareStatement("select * from employee_personal_details where emp_per_id=? and is_alive=true and approved_flag=true");
						pst.setInt(1, uF.parseToInt(getbDayEmpId()));
		//				System.out.println("pst ===>> " + pst);
						rs = pst.executeQuery();
						while(rs.next()) {
							strJoiningDate = uF.getDateFormat(rs.getString("joining_date"), DBDATE, DATE_FORMAT_STR);
							strWorkTimeDuration = uF.getTimeDurationWithNoSpan(rs.getString("joining_date"), CF, uF, request);
						}
						rs.close();
						pst.close();
					}
					
					pst = con.prepareStatement("select * from employee_personal_details where emp_per_id!=? and is_alive=true " +
						" and approved_flag=true and (emp_email_sec is not null or emp_email is not null)");
					pst.setInt(1, uF.parseToInt(getbDayEmpId()));
//					System.out.println("pst ===>> " + pst);
					rs = pst.executeQuery();
					while(rs.next()) {
						boolean flg=false;
						Notifications nF = new Notifications(strNotiId, CF);
	//					System.out.println("getStrDomain() ===>> " + getStrDomain());
	//					System.out.println("request ===>> " + request);
						nF.setDomain(getStrDomain());
						nF.request = request;
						nF.setStrHostAddress(CF.getStrEmailLocalHost());
						nF.setStrHostPort(CF.getStrHostPort());
						nF.setStrContextPath(request.getContextPath());
						nF.setStrEmpId(getbDayEmpId());
						nF.setSupervisor(false);
						nF.setEmailTemplate(true);
						nF.setStrSupervisorEmail(rs.getString("emp_email"));					
						
						String strEmpMName = "";
						if(flagMiddleName) {
							if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
								strEmpMName = " "+rs.getString("emp_mname");
							}
						}
						
						nF.setStrEmpMobileNo(rs.getString("emp_contactno_mob"));
						nF.setStrRecipientName(rs.getString("emp_fname")+strEmpMName+" "+rs.getString("emp_lname"));
						nF.setStrSupervisorContactNo(rs.getString("emp_contactno"));
						nF.setStrJoiningDate(strJoiningDate);
						nF.setStrNoOfYearsWorking(strWorkTimeDuration);
						if(rs.getString("emp_email_sec")!=null && rs.getString("emp_email_sec").indexOf("@")>0) {
							nF.setStrEmpEmail(rs.getString("emp_email_sec"));
							nF.setStrEmailTo(rs.getString("emp_email_sec"));
							flg=true;
						} else if(rs.getString("emp_email")!=null && rs.getString("emp_email").indexOf("@")>0) {
							nF.setStrEmpEmail(rs.getString("emp_email"));
							nF.setStrEmailTo(rs.getString("emp_email"));
							flg=true;
						}
//						System.out.println("flg ===>> " + flg);
						if(flg) {
							nF.sendNotifications();
						}
					}
					rs.close();
					pst.close();
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
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
	}

	
	private void insertTRUserAlerts(Connection con, CommonFunctions CF, UtilityFunctions uF) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = con.prepareStatement("insert into communication_1(communication,align_with,align_with_id,tagged_with,doc_shared_with_id,doc_id," +
				"visibility,visibility_with_id,created_by,create_time,feed_type) values(?,?,?,?, ?,?,?,?, ?,?,?)");
			pst.setString(1, strData);
			pst.setInt(2, uF.parseToInt(getStrAlignWith()));
			pst.setInt(3, uF.parseToInt(getStrAlignWithId()));
			pst.setString(4, getStrTaggedWith());
			pst.setString(5, "");
			pst.setString(6, "");
			pst.setInt(7, uF.parseToInt(getStrVisibility()));
			pst.setString(8, getStrVisibilityWith());
			pst.setInt(9, uF.parseToInt(strSessionEmpId));
			pst.setTimestamp(10, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone())+"", DBDATE+DBTIME));
			pst.setInt(11, FT_ACTIVITY);
			pst.executeUpdate();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
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
	}
	
	
	public String getStrDomain() {
		return strDomain;
	}

	public void setStrDomain(String strDomain) {
		this.strDomain = strDomain;
	}

	public String getActivityType() {
		return activityType;
	}

	public void setActivityType(String activityType) {
		this.activityType = activityType;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStrOther() {
		return strOther;
	}

	public void setStrOther(String strOther) {
		this.strOther = strOther;
	}

	public String getStrData() {
		return strData;
	}

	public void setStrData(String strData) {
		this.strData = strData;
	}

	public String getAlertID() {
		return alertID;
	}

	public void setAlertID(String alertID) {
		this.alertID = alertID;
	}

	public String getStrSessionEmpId() {
		return strSessionEmpId;
	}

	public void setStrSessionEmpId(String strSessionEmpId) {
		this.strSessionEmpId = strSessionEmpId;
	}

	public String getStrTaggedWith() {
		return strTaggedWith;
	}

	public void setStrTaggedWith(String strTaggedWith) {
		this.strTaggedWith = strTaggedWith;
	}

	public String getStrVisibility() {
		return strVisibility;
	}

	public void setStrVisibility(String strVisibility) {
		this.strVisibility = strVisibility;
	}

	public String getStrVisibilityWith() {
		return strVisibilityWith;
	}

	public void setStrVisibilityWith(String strVisibilityWith) {
		this.strVisibilityWith = strVisibilityWith;
	}

	public String getStrAlignWith() {
		return strAlignWith;
	}

	public void setStrAlignWith(String strAlignWith) {
		this.strAlignWith = strAlignWith;
	}

	public String getStrAlignWithId() {
		return strAlignWithId;
	}

	public void setStrAlignWithId(String strAlignWithId) {
		this.strAlignWithId = strAlignWithId;
	}

	public String getStrUserType() {
		return strUserType;
	}

	public void setStrUserType(String strUserType) {
		this.strUserType = strUserType;
	}

	public String getbDayEmpId() {
		return bDayEmpId;
	}

	public void setbDayEmpId(String bDayEmpId) {
		this.bDayEmpId = bDayEmpId;
	}

}

