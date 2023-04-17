package com.konnect.jpms.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

public class LogDetails implements ServletRequestAware,IStatements{
	public CommonFunctions CF;
	public HttpSession session;
	int processId;
	String processType;
	int processActivity;
	String processMsg;
	int processStep;
	int processBy;
	
	public int getProcessId() {
		return processId;
	} 
	public void setProcessId(int processId) {
		this.processId = processId;
	}
	public String getProcessType() {
		return processType;
	}
	public void setProcessType(String processType) {
		this.processType = processType;
	}
	public int getProcessActivity() {
		return processActivity;
	}
	public void setProcessActivity(int processActivity) {
		this.processActivity = processActivity;
	}
	public String getProcessMsg() {
		return processMsg;
	}
	public void setProcessMsg(String processMsg) {
		this.processMsg = processMsg;
	}
	public int getProcessStep() {
		return processStep;
	}
	public void setProcessStep(int processStep) {
		this.processStep = processStep;
	}
	
	public int getProcessBy() {
		return processBy;
	}
	public void setProcessBy(int processBy) {
		this.processBy = processBy;
	}

	public HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}
	public void insertLog(Connection con, UtilityFunctions uF) {
		PreparedStatement pst = null;
		try {
			pst = con.prepareStatement("insert into log_details(process_id, process_type, process_activity, process_msg, process_step, " +
					"process_by, process_time) values (?,?,?,?, ?,?,?)");
			pst.setInt(1, getProcessId());
			pst.setString(2, getProcessType());
			pst.setInt(3, getProcessActivity());
			pst.setString(4, getProcessMsg());
			pst.setInt(5, getProcessStep());
			pst.setInt(6, getProcessBy());
			pst.setTimestamp(7, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone())+"", DBDATE+DBTIME));
			pst.execute();
			pst.close();			  
			
		} catch (Exception e) {
			e.printStackTrace();
			
		} finally {
			if(pst !=null) {
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
}
