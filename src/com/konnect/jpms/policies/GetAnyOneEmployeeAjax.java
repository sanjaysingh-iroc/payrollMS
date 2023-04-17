package com.konnect.jpms.policies;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.training.GetSelectedEmployeeAjax;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class GetAnyOneEmployeeAjax extends ActionSupport implements
		ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	String strSessionEmpId = null;
	CommonFunctions CF = null;

	String selectedEmp;
	String chboxStatus;
	String existemp;
	String oldempids;
	
	public String execute() {

		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null) {
			return LOGIN;
		}
		UtilityFunctions uF=new UtilityFunctions();
		List<String> list=new ArrayList<String>();
		if(existemp!=null){
			List<String> list1=Arrays.asList(existemp.split(","));
			list.addAll(list1);
		}
		
		
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
			if(empNameList != null && !empNameList.isEmpty()) {
				allData.append("<div style=\"border: 2px solid #ccc;\">");
				if(empNameList != null && !empNameList.isEmpty() && empNameList.size() > 0){
					allData.append("<div style=\"padding: 8px 20px; border: 2px solid lightgray;\"><b>Approval Employee</b></div> ");
	
				for(int i=0;i<empNameList.size();i++){
					List<String> innerList = empNameList.get(i);
					allData.append("<div style=\"float: left; width: 100%; margin: 5px;\"><strong>"+ (i+1) +".</strong>&nbsp;&nbsp;"+innerList.get(1) +"&nbsp;&nbsp;");
					allData.append("<a href=\"javascript: void(0)\" onclick=\"getSelectedEmp('false','"+innerList.get(0)+"');\"><img border=\"0\" style=\"width: 12px; height: 12px;\" src=\""+request.getContextPath()+"/images1/arrow_reset1.png\"/></a>");
					allData.append("</div>");
				 }
				
				} else if(empNameList.isEmpty() || empNameList.size() == 0){
					allData.append("<div class=\"nodata msg\" style=\"width: 85%\"> <span>No Employee selected</span> </div>");
				}
				allData.append("</div>");
			 } else {
				 allData.append("<div class=\"nodata msg\" style=\"width:85%\"><span>No Employee selected</span></div>");
			 }
			String selectEmpIDS = sb.toString()!=null && !sb.toString().equals("") ? sb.toString() :"0";
			allData.append("<input type=\"hidden\" name=\"oldempids\" id=\"oldempids\" value=\""+getOldempids()+"\"/>" +
					"<input type=\"hidden\" name=\"empselected\" id=\"empselected\" value=\""+selectEmpIDS +"\"/>");
			
//			allData.append("::::<input type=\"hidden\" name=\"oldempids\" id=\"oldempids1\" value=\""+getOldempids()+"\"/>" +
//					"<input type=\"hidden\" name=\"empselected\" id=\"empselected1\" value=\""+selectEmpIDS +"\"/>");
			
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

}
