package com.konnect.jpms.training;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class GetSelectedEmployeeAjax extends ActionSupport implements
		ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	String strSessionEmpId = null;

	

	CommonFunctions CF = null;

	private static Logger log = Logger.getLogger(GetSelectedEmployeeAjax.class);
//	String chboxStatus;
//	String planId;
//	String selectedEmp;
//	String type;

	private String selectedEmp;
	private String chboxStatus;
	private String existemp;
	private String oldempids;
	private String boolPublished;
	
	public String execute() {

		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null) {
			return LOGIN;
		}
		UtilityFunctions uF=new UtilityFunctions();
				
//		if(getType()!=null && getType().equals("one")){
//			if (uF.parseToBoolean(getChboxStatus())) {
//				addEmployee();
//			} else {
//				removeEmployee();
//			}
//		}else if(getType()!=null && getType().equals("all")){
//			if (uF.parseToBoolean(getChboxStatus())) {
//				addAllEmployee();
//			} else {
//				removeAllEmployee();
//			}
//		}
//
//		getSelectEmployeeList();
		
		List<String> list=new ArrayList<String>();
		if(existemp!=null){
			List<String> list1=Arrays.asList(existemp.split(","));
			list.addAll(list1);
		}
		
//		System.out.println("list===>"+list);
//		System.out.println("selectedEmp ===> "+selectedEmp);
//		System.out.println("type ===> "+type);
//		System.out.println("chboxStatus ===> "+chboxStatus);
//		System.out.println("existemp ===> "+existemp);
		
			if (uF.parseToBoolean(getChboxStatus())) {
				getSelectEmployeeList(list,true,true);
			} else {
				getSelectEmployeeList(list,false,true);
			}
			
		setName(list);
		
		return SUCCESS;

	}
	
	
	public void setName(List<String> list){
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try{
			con = db.makeConnection(con);
		Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
		StringBuilder sb=new StringBuilder();
		List<List<String>> empNameList=new ArrayList<List<String>>();
		
		for(String empId:list){
			List<String> innerList = new ArrayList<String>();
				if(empId.equals("0") || empId.equals("")) {
					continue;
				}
				innerList.add(empId);
				innerList.add(hmEmpName.get(empId));
			empNameList.add(innerList);
			if(sb == null || sb.toString().equals("")){
				sb.append(","+empId+",");
			}else{
				sb.append(empId+",");
			}			
		}
		
		StringBuffer allData = new StringBuffer();
		List<String> exitEmpList = Arrays.asList(oldempids.split(","));
//		System.out.println("exitEmpList ===> " + exitEmpList);
//		System.out.println("boolPublished ===> " + boolPublished);
		
		if(empNameList != null && !empNameList.isEmpty()) {

			allData.append("<div style=\"border: 2px solid #ccc;\">");
			
			if(empNameList != null && !empNameList.isEmpty() && empNameList.size() > 0){
				allData.append("<div style=\"padding: 8px 20px; border: 2px solid lightgray;\"><b>Learners</b></div> ");

			for(int i=0;i<empNameList.size();i++){
				List<String> innerList = empNameList.get(i);
				allData.append("<div style=\"float: left; width: 100%; margin: 5px;\"><strong>"+ (i+1) +".</strong>&nbsp;&nbsp;"+innerList.get(1) +"&nbsp;&nbsp;");
				if(uF.parseToBoolean(boolPublished) == true) {
					if(!exitEmpList.contains(innerList.get(0))){
						allData.append("<a href=\"javascript: void(0)\" onclick=\"getSelectedLearner('false','"+innerList.get(0)+"','"+getBoolPublished()+"');\"><img border=\"0\" style=\"width: 12px; height: 12px;\" src=\""+request.getContextPath()+"/images1/arrow_reset1.png\"/></a>");
					}
				} else {
					allData.append("<a href=\"javascript: void(0)\" onclick=\"getSelectedLearner('false','"+innerList.get(0)+"','"+getBoolPublished()+"');\"><img border=\"0\" style=\"width: 12px; height: 12px;\" src=\""+request.getContextPath()+"/images1/arrow_reset1.png\"/></a>");
				}
				allData.append("</div>");
			 }
			
			}
				if(empNameList.isEmpty() || empNameList.size() == 0){
					allData.append("<div class=\"nodata msg\" style=\"width: 85%\"> <span>No Employee selected</span> </div>");
				}
				allData.append("</div>");
		 } else {
			 allData.append("<div class=\"nodata msg\" style=\"width:85%\"><span>No Employee selected</span></div>");
		 }
		String selectEmpIDS = sb.toString()!=null && !sb.toString().equals("") ? sb.toString() :"0";
		allData.append("<input type=\"hidden\" name=\"oldempids\" id=\"oldempids\" value=\""+getOldempids()+"\"/>" +
				"<input type=\"hidden\" name=\"empselected\" id=\"empselected\" value=\""+selectEmpIDS +"\"/>");
		
	/*	allData.append("::::<input type=\"hidden\" name=\"oldempids\" id=\"oldempids1\" value=\""+getOldempids()+"\"/>" +
				"<input type=\"hidden\" name=\"empselected\" id=\"empselected1\" value=\""+selectEmpIDS +"\"/>");*/
		
		request.setAttribute("empNameList", empNameList);
		request.setAttribute("selectedID", sb.toString());
		request.setAttribute("allData", allData.toString());
		
		}catch (Exception e) {
			e.printStackTrace();
		} finally{
			db.closeConnection(con);
		}
	}
	

	private void getSelectEmployeeList(List<String> list,boolean flag,boolean flag1) {
		
			List<String> li=Arrays.asList(selectedEmp.split(","));
			for(String a:li){
				if(flag){
					if(!list.contains(a)){
					list.add(a);
					}
				}else if(!flag){
					list.remove(a);
				}
			}
	}


//	private void removeAllEmployee() {
//
//		Connection con = null;
//		PreparedStatement pst = null;
//		Database db = new Database();
//		db.setRequest(request);
//		ResultSet rst = null;
//		String newEmpid = null;
//
//		UtilityFunctions uF = new UtilityFunctions();
//
//		try {
//			con = db.makeConnection(con);
//
//			String existing = null;
//			pst = con
//					.prepareStatement("select emp_ids from training_schedule where plan_id=?");
//			pst.setInt(1, uF.parseToInt(getPlanId()));
//			rst = pst.executeQuery();
//
//			while (rst.next()) {
//
//				existing = rst.getString("emp_ids");
//			}
//
//			List<String> alnewEmplist = new ArrayList<String>();
//			if (existing != null && !existing.equals("")) {				
//				
//					List<String> emplist=Arrays.asList(existing.split(","));
//					List<String> checkemp=Arrays.asList(getSelectedEmp().split(","));
//					
//						for(int i=1;i<emplist.size();i++){
//							if(i==emplist.size()-1){
//								continue;
//							}
//							if (!checkemp.contains(emplist.get(i).trim())) {
//								alnewEmplist.add(emplist.get(i));
//							} 
//						}								
//			}else{
//				alnewEmplist.add("");
//			}
//			
//			
//			String newpanel = null;
//			if(!alnewEmplist.isEmpty()){
//				for (int i = 0; i < alnewEmplist.size(); i++) {
//	
//					if (i == 0)
//						newpanel = "," +alnewEmplist.get(i)+"," ;
//					else
//						newpanel +=  alnewEmplist.get(i)+",";
//	
//				} 
//			}
//			//System.out.println("newpanel====>"+newpanel);
//			
//			pst = con.prepareStatement("update training_schedule set  "
//					+ " emp_ids= ? " + " where plan_id= ? ");
//			
//			pst.setString(1, newpanel);
//			pst.setInt(2, uF.parseToInt(getPlanId()));
//
//			pst.executeUpdate();
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			db.closeConnection(con);
//			db.closeResultSet(rst);
//			db.closeStatements(pst);
//		}
//
//	}

//	private void addAllEmployee() {
//
//		Connection con = null;
//		PreparedStatement pst = null;
//		Database db = new Database();
//		db.setRequest(request);
//		ResultSet rst = null;
//		String newEmpid = null;
//
//		UtilityFunctions uF = new UtilityFunctions();
//
//		try {
//			con = db.makeConnection(con);
//
//			String existing = null;
//			pst = con
//					.prepareStatement("select emp_ids from training_schedule where plan_id=?");
//			pst.setInt(1, uF.parseToInt(getPlanId()));
//			rst = pst.executeQuery();
//
//			while (rst.next()) {
//				existing = rst.getString("emp_ids");
//			}
//
//			if (existing == null || existing.equals("")) {
//				newEmpid = "," +getSelectedEmp()+",";
//			} else {
//				if(getSelectedEmp()!=null && !getSelectedEmp().equals("")){
//					List<String> emplist=Arrays.asList(getSelectedEmp().split(","));
//					newEmpid = existing;
//					if(emplist!=null){
//						for(int i=0;i<emplist.size();i++){
////							System.out.println("emplist.get(i).trim()====>"+emplist.get(i).trim());
//							if(emplist.get(i)!=null && !emplist.equals("")){
//								if (!existing.contains(","+emplist.get(i).trim()+",")) {
//									newEmpid+=  emplist.get(i).trim()+ ",";
//								} 
//							} 
//						}
//					}
//				}else{
//					newEmpid = existing;
//				}
//			}
////			System.out.println("existing)====>"+existing);
////			System.out.println("getSelectedEmp()====>"+getSelectedEmp());
////			System.out.println("newEmpid====>"+newEmpid);
//			pst = con.prepareStatement("update training_schedule set  "
//					+ " emp_ids= ? " + " where plan_id= ? ");
//			
//			pst.setString(1, newEmpid);
//			pst.setInt(2, uF.parseToInt(getPlanId()));
//
//			pst.executeUpdate();
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			db.closeConnection(con);
//			db.closeResultSet(rst);
//			db.closeStatements(pst);
//		}
//
//	}

//	private void getSelectEmployeeList() {
//		
//		Connection con = null;
//		PreparedStatement pst = null;
//		Database db = new Database();
//		db.setRequest(request);
//		ResultSet rst = null;
//		UtilityFunctions uF=new UtilityFunctions();
//		try {
//			
//			con=db.makeConnection(con);
//			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
//							
//			pst=con.prepareStatement("select emp_ids from training_schedule where plan_id=?");
//			pst.setInt(1,uF.parseToInt(getPlanId()));
//			rst=pst.executeQuery();
//			String selectEmpIDs=null;
//			while(rst.next()){
//				
//				selectEmpIDs=rst.getString("emp_ids");
//			}
////			System.out.println("selectEmpIDs==>"+selectEmpIDs);
//			List<String> selectEmpList=new ArrayList<String>();
//			if(selectEmpIDs!=null && !selectEmpIDs.equals("")){
//				List<String> tmpselectEmpList=Arrays.asList(selectEmpIDs.split(","));
//				Set<String> trainerSet = new HashSet<String>(tmpselectEmpList);
//				Iterator<String> itr = trainerSet.iterator();	
//				while (itr.hasNext()) {
//					String trainerId = (String) itr.next();
//					if(trainerId!=null && !trainerId.equals("")){
//						selectEmpList.add(hmEmpName.get(trainerId.trim()));
//					}
//				}
//			}else{
//				selectEmpList=null;
//			}
////			System.out.println("selectEmpList==>"+selectEmpList);
//			request.setAttribute("selectEmpList", selectEmpList);
//			
//		}catch(Exception e){
//			e.printStackTrace();
//		}finally{
//		
//			db.closeConnection(con);
//			db.closeResultSet(rst);
//			db.closeStatements(pst);
//		}
//		
//	}

//	private void removeEmployee() {
//
//		Connection con = null;
//		PreparedStatement pst = null;
//		Database db = new Database();
//		db.setRequest(request);
//		ResultSet rst = null;
//		String oldpanel = null;
//		UtilityFunctions uF = new UtilityFunctions();
//		try {
//
//			con = db.makeConnection(con);
//
//			pst = con
//					.prepareStatement("select emp_ids from training_schedule where plan_id=?");
//			pst.setInt(1, uF.parseToInt(getPlanId()));
//
//			rst = pst.executeQuery();
//
//			while (rst.next()) {
//				oldpanel = rst.getString("emp_ids");
//			}
//			
//			List<String> alnewEmplist = new ArrayList<String>();
//			
//			if(oldpanel!=null && !oldpanel.equals("")){
//
//				String[] oldEmp = oldpanel.split(",");
//				for (int i = 1; i < oldEmp.length; i++) {
//					if (!oldEmp[i].equalsIgnoreCase(getSelectedEmp().trim()))
//						alnewEmplist.add(oldEmp[i]);	
//				}
//			}else{
//				alnewEmplist.add("");
//			}
//			String newpanel = null;
//			for (int i = 0; i < alnewEmplist.size(); i++) {
//
//				if (i == 0)
//					newpanel = "," +alnewEmplist.get(i)+"," ;
//				else
//					newpanel +=  alnewEmplist.get(i)+",";
//
//			}
//
//			pst = con.prepareStatement("update training_schedule set  "
//					+ " emp_ids= ? " + " where plan_id= ? ");
//			pst.setString(1, newpanel);
//			pst.setInt(2, uF.parseToInt(getPlanId()));
//
//			pst.executeUpdate();
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//
//			db.closeConnection(con);
//			db.closeResultSet(rst);
//			db.closeStatements(pst);
//		}
//
//	}

//	private void addEmployee() {
//
//		Connection con = null;
//		PreparedStatement pst = null;
//		Database db = new Database();
//		db.setRequest(request);
//		ResultSet rst = null;
//		String newEmpid;
//
//		UtilityFunctions uF = new UtilityFunctions();
//
//		try {
//			con = db.makeConnection(con);
//
//			String existing = null;
//			pst = con
//					.prepareStatement("select emp_ids from training_schedule where plan_id=?");
//			pst.setInt(1, uF.parseToInt(getPlanId()));
//			rst = pst.executeQuery();
//
//			while (rst.next()) {
//
//				existing = rst.getString("emp_ids");
//			}
//
//			if (existing == null || existing.equals("")) {
//				newEmpid = "," +getSelectedEmp()+",";
//			} else {
//				if (existing.contains(","+getSelectedEmp()+ ",")) {
//					newEmpid = existing;
//				} else {
//					newEmpid = existing + getSelectedEmp()+ ",";
//				}
//			}
//			pst = con.prepareStatement("update training_schedule set  "
//					+ " emp_ids= ? " + " where plan_id= ? ");
//			
//			pst.setString(1, newEmpid);
//			pst.setInt(2, uF.parseToInt(getPlanId()));
//
//			pst.executeUpdate();
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			db.closeConnection(con);
//			db.closeResultSet(rst);
//			db.closeStatements(pst);
//		}
//
//	}

	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	String employeename;

	public String getEmployeename() {
		return employeename;
	}

	public void setEmployeename(String employeename) {
		this.employeename = employeename;
	}

	public String getSelectedEmp() {
		return selectedEmp;
	}

	public void setSelectedEmp(String selectedEmp) {
		this.selectedEmp = selectedEmp;
	}

//	public String getType() {
//		return type;
//	}
//
//	public void setType(String type) {
//		this.type = type;
//	}
//
//	public String getPlanId() {
//		return planId;
//	}
//
//	public void setPlanId(String planId) {
//		this.planId = planId;
//	}

	public String getExistemp() {
		return existemp;
	}

	public void setExistemp(String existemp) {
		this.existemp = existemp;
	}

	public String getChboxStatus() {
		return chboxStatus;
	}

	public void setChboxStatus(String chboxStatus) {
		this.chboxStatus = chboxStatus;
	}

	public String getOldempids() {
		return oldempids;
	}

	public void setOldempids(String oldempids) {
		this.oldempids = oldempids;
	}

	public String getBoolPublished() {
		return boolPublished;
	}

	public void setBoolPublished(String boolPublished) {
		this.boolPublished = boolPublished;
	}
	
	
}