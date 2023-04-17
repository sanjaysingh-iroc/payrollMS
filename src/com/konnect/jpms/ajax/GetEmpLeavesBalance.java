package com.konnect.jpms.ajax;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.reports.EmployeeLeaveEntryReport;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class GetEmpLeavesBalance extends ActionSupport implements ServletRequestAware, IStatements {
	
	private static final long serialVersionUID = 1L;
	HttpSession session;
	CommonFunctions CF = null;
	String strUserType = null;
	String strSessionEmpId = null;
	String strSessionOrgId = null;
	
	String strEmpId;
	String strLeaves;
	String levelId;
	String wLocationId;
	String strOrg;
	String joiningDate;
	String empStatus;
	
	public String execute() {
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if (CF==null) return LOGIN;
		
		strUserType = (String)session.getAttribute(USERTYPE);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		strSessionOrgId = (String)session.getAttribute(ORGID);
		
		UtilityFunctions uF = new UtilityFunctions();
		if(uF.parseToInt(getStrEmpId()) == 0){
			getEmpLeaveBalanceFor1Step(uF);
		} else {
			getEmpLeaveBalance(uF);
		}
		
		return LOAD;
	}

	private void getEmpLeaveBalanceFor1Step(UtilityFunctions uF) {
//		System.out.println("GELB/61--getEmpLeaveBalanceFor1Step");
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmLeaveTypeMap = CF.getLeaveTypeMap(con);
			if(hmLeaveTypeMap == null) hmLeaveTypeMap = new HashMap<String, String>();
//			List<List<String>> leaveTypeListWithBalance = CF.getLevelLeaveTypeBalanceForEmp(con, levelId, getStrEmpId(), CF,alExistLeave);
//			System.out.println("GELB/--getStrLeaves="+getStrLeaves());
			Map<String, String> hmProRataLeaveBalance = CF.getLevelLeaveTypeBalanceForEmp(con, CF, uF, uF.parseToInt(getStrOrg()), uF.parseToInt(getwLocationId()),uF.parseToInt(getLevelId()),getJoiningDate(),getEmpStatus(),getStrLeaves());
			List<String> alAccrueLeave = CF.getAccrueLeave(con, CF, uF, uF.parseToInt(getStrOrg()), uF.parseToInt(getwLocationId()),uF.parseToInt(getLevelId()),getStrLeaves());
			if(alAccrueLeave == null) alAccrueLeave = new ArrayList<String>();
			
			List<List<String>> leaveTypeListWithBalance1 = new ArrayList<List<String>>();
			if(getStrLeaves()!=null && !getStrLeaves().trim().equals("") && !getStrLeaves().trim().equalsIgnoreCase("NULL")){
				List<String>  alLeaves = Arrays.asList(getStrLeaves().split(","));
//				System.out.println("GELB/80--alLeaves="+alLeaves);
				for(int i = 0; alLeaves!=null && i < alLeaves.size(); i++){
					String strLeaveTypeId = alLeaves.get(i).trim();
//					System.out.println("GELB/82--strLeaveTypeId ========>> " + strLeaveTypeId);
			
			//===start parvez date: 22-11-2021===		
					/*if(alAccrueLeave.contains(strLeaveTypeId)){
						continue;
					}*/
			//===end parvez date: 22-11-2021===
					if(uF.parseToInt(strLeaveTypeId) > 0) {
						List<String> innerList = new ArrayList<String>();
						innerList.add(strLeaveTypeId);
						innerList.add(uF.showData(hmLeaveTypeMap.get(strLeaveTypeId), ""));
						innerList.add(""+uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(hmProRataLeaveBalance.get(strLeaveTypeId))));
						leaveTypeListWithBalance1.add(innerList);
					}
				}
			}
			
//			System.out.println("GELB/96--leaveTypeListWithBalance1 ===>> " + leaveTypeListWithBalance1);
			request.setAttribute("leaveTypeListWithBalance", leaveTypeListWithBalance1);
			request.setAttribute("alAccrueLeave", alAccrueLeave);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private void getEmpLeaveBalance(UtilityFunctions uF) {
//		System.out.println("GELB/109--getEmpLeaveBalance");
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmLeaveTypeMap = CF.getLeaveTypeMap(con);
			if(hmLeaveTypeMap == null) hmLeaveTypeMap = new HashMap<String, String>();
			 
			String levelId = CF.getEmpLevelId(con, getStrEmpId());
			
			List<String> alExistLeave = new ArrayList<String>();
//			List<List<String>> leaveTypeListWithBalance = CF.getLevelLeaveTypeBalanceForEmp(con, levelId, getStrEmpId(), CF,alExistLeave);
			Map<String, String> hmProRataLeaveBalance = CF.getLevelLeaveTypeBalanceForEmp(con, CF, uF, uF.parseToInt(getStrEmpId()), uF.parseToInt(getStrOrg()), uF.parseToInt(getwLocationId()),uF.parseToInt(getLevelId()),getJoiningDate(),getEmpStatus(),getStrLeaves(),alExistLeave);
			List<String> alAccrueLeave = CF.getAccrueLeave(con, CF, uF, uF.parseToInt(getStrOrg()), uF.parseToInt(getwLocationId()),uF.parseToInt(getLevelId()),getStrLeaves());
			if(alAccrueLeave == null) alAccrueLeave = new ArrayList<String>();
			
			Map<String, String> hmEmpLeaveBalance = new HashMap<String, String>();
			EmployeeLeaveEntryReport leaveEntryReport = new EmployeeLeaveEntryReport();
			leaveEntryReport.request = request;
			leaveEntryReport.session = session;
			leaveEntryReport.CF = CF;
			leaveEntryReport.setStrEmpId(getStrEmpId());
			leaveEntryReport.setDataType("L");
			leaveEntryReport.viewEmployeeLeaveEntry1();
			
			  
			java.util.List leaveList = (java.util.List)request.getAttribute("leaveList");
			
//			System.out.println("GELB/145--leaveList="+leaveList);
			if(leaveList == null) leaveList = new ArrayList();
			int nLeaveList = leaveList.size();
			List<List<String>> leaveTypeListWithBalance1 = new ArrayList<List<String>>();
			if(getStrLeaves()!=null && !getStrLeaves().trim().equals("") && !getStrLeaves().trim().equalsIgnoreCase("NULL")){
				List<String>  alLeaves = Arrays.asList(getStrLeaves().split(","));

				for(int i = 0; alLeaves!=null && i < alLeaves.size(); i++){
					String strLeaveTypeId = alLeaves.get(i).trim();
//					System.out.println("GELB/154--strLeaveTypeId ========>> " + strLeaveTypeId);  
					
			//===start parvez date: 22-11-2021 Note:commented becouse on employee onboarding if in leave policy isAccure is true balance for accure leave is not updating ===		
					/*if(alAccrueLeave.contains(strLeaveTypeId)){
						continue;
					}*/
			//===end parvez date: 22-11-2021===
					
					if(uF.parseToInt(strLeaveTypeId) > 0) {
//						System.out.println("GELB/159--alExistLeave="+alExistLeave);
						if(alExistLeave.contains(strLeaveTypeId)){
							if(nLeaveList > 0){
								
								//===start parvez on 03-07-2021===
								List<String> leaveIdList = new ArrayList<String>();
								//===end parvez on 03-07-2021===
								
								for (int j=0; j<nLeaveList; j++) {
									List<String> cinnerlist = (List<String>)leaveList.get(j);
									
									//===start parvez on 03-08-2021===
									leaveIdList.add(cinnerlist.get(6));
									//==end parvez on 03-08-2021===
									
									System.out.println("GELB/179--cinnerlist.get(6) ===>> " + cinnerlist.get(6)+"--cinnerlist.get(5)===>"+cinnerlist.get(5));  
									if(uF.parseToInt(strLeaveTypeId) == uF.parseToInt(cinnerlist.get(6))){
									  
										if(uF.parseToDouble((String)cinnerlist.get(5)) > 0.0d){
											System.out.println("GELB/183--if if========>> ");  
											hmEmpLeaveBalance.put(""+cinnerlist.get(6), ""+cinnerlist.get(5));
											List<String> innerList = new ArrayList<String>();
											innerList.add(strLeaveTypeId);
											innerList.add(uF.showData(hmLeaveTypeMap.get(strLeaveTypeId), ""));
											innerList.add("0");
											leaveTypeListWithBalance1.add(innerList);
											
										} else {
											System.out.println("GELB/192--Balance="+hmProRataLeaveBalance.get(strLeaveTypeId));
											List<String> innerList = new ArrayList<String>();
											innerList.add(strLeaveTypeId);
											innerList.add(uF.showData(hmLeaveTypeMap.get(strLeaveTypeId), ""));
											innerList.add(""+uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(hmProRataLeaveBalance.get(strLeaveTypeId))));
											leaveTypeListWithBalance1.add(innerList);
										}
									}
								}
								//===start parvez on 03-08-2021===
									if(!leaveIdList.contains(strLeaveTypeId)){
										List<String> innerList = new ArrayList<String>();
										innerList.add(strLeaveTypeId);
										innerList.add(uF.showData(hmLeaveTypeMap.get(strLeaveTypeId), ""));
										innerList.add(""+uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(hmProRataLeaveBalance.get(strLeaveTypeId))));
										leaveTypeListWithBalance1.add(innerList);
										System.out.println("GELB/208--LeaveBalance="+hmProRataLeaveBalance.get(strLeaveTypeId));
									}
								//===end parvez on 03-08-2021===
								
							} else {
								System.out.println("GELB/213--LeaveBalance="+hmProRataLeaveBalance.get(strLeaveTypeId));
								List<String> innerList = new ArrayList<String>();
								innerList.add(strLeaveTypeId);
								innerList.add(uF.showData(hmLeaveTypeMap.get(strLeaveTypeId), ""));
								innerList.add(""+uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(hmProRataLeaveBalance.get(strLeaveTypeId))));
								leaveTypeListWithBalance1.add(innerList);
							}
						} else {
							System.out.println("GELB/221--else ==uF.parseToDouble(hmProRataLeaveBalance.get(strLeaveTypeId))==>> "+uF.parseToDouble(hmProRataLeaveBalance.get(strLeaveTypeId)));
							List<String> innerList = new ArrayList<String>();
							innerList.add(strLeaveTypeId);
							innerList.add(uF.showData(hmLeaveTypeMap.get(strLeaveTypeId), ""));
							innerList.add(""+uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(hmProRataLeaveBalance.get(strLeaveTypeId))));
							leaveTypeListWithBalance1.add(innerList);
						}
					}
				}
			}
			
			request.setAttribute("leaveTypeListWithBalance", leaveTypeListWithBalance1);
			request.setAttribute("hmEmpLeaveBalance", hmEmpLeaveBalance);
			request.setAttribute("alAccrueLeave", alAccrueLeave);
			System.out.println("GELB/200--hmEmpLeaveBalance ==> " + hmEmpLeaveBalance);
			System.out.println("GELB/200--leaveTypeListWithBalance1 ==> " + leaveTypeListWithBalance1);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public String getStrEmpId() {
		return strEmpId;
	}

	public void setStrEmpId(String strEmpId) {
		this.strEmpId = strEmpId;
	}

	public String getStrLeaves() {
		return strLeaves;
	}

	public void setStrLeaves(String strLeaves) {
		this.strLeaves = strLeaves;
	}

	public String getLevelId() {
		return levelId;
	}

	public void setLevelId(String levelId) {
		this.levelId = levelId;
	}

	public String getwLocationId() {
		return wLocationId;
	}

	public void setwLocationId(String wLocationId) {
		this.wLocationId = wLocationId;
	}

	public String getJoiningDate() {
		return joiningDate;
	}

	public void setJoiningDate(String joiningDate) {
		this.joiningDate = joiningDate;
	}

	public String getEmpStatus() {
		return empStatus;
	}

	public void setEmpStatus(String empStatus) {
		this.empStatus = empStatus;
	}

	public String getStrOrg() {
		return strOrg;
	}

	public void setStrOrg(String strOrg) {
		this.strOrg = strOrg;
	}
	
}