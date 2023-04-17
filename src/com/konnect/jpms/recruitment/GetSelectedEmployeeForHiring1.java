package com.konnect.jpms.recruitment;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class GetSelectedEmployeeForHiring1 extends ActionSupport implements
		ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	String strSessionEmpId = null;
	CommonFunctions CF = null;

	private static Logger log = Logger.getLogger(GetSelectedEmployeeForHiring1.class);
	
	String selectedEmp;
	String chboxStatus;
	String existemp;
	
	public String execute() {

		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null) {
			return LOGIN;
		}
//		System.out.println("form ===> "+form);
		UtilityFunctions uF=new UtilityFunctions();
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
		
		try{
			con = db.makeConnection(con);
		Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
		StringBuilder sb=new StringBuilder();
		StringBuilder sbOption = new StringBuilder("");
		List<String> empNameList=new ArrayList<String>();
		
		for(String empId:list){
				if(empId.equals("0") || empId.equals("")){
					continue;
				}
				sbOption.append("<option value=\"" + empId + "\">" + hmEmpName.get(empId) + "</option>");
			empNameList.add(hmEmpName.get(empId));
			if(sb == null || sb.toString().equals("")){
				sb.append(","+empId+",");
			} else {
				sb.append(empId+",");
			}
		}
		
		StringBuffer allData = new StringBuffer();
		if(empNameList != null && !empNameList.isEmpty()) {

			allData.append("<div style=\"border: 2px solid #ccc;\">");
			
			if(empNameList != null && !empNameList.isEmpty() && empNameList.size() > 0){
				allData.append("<div style=\"padding: 8px 20px; border: 2px solid lightgray;\"><b>Employee</b></div> ");

			for(int i=0;i<empNameList.size();i++){

				allData.append("<div style=\"float: left; width: 100%; margin: 5px;\"><strong>"+ (i+1) +".</strong>&nbsp;&nbsp;"+empNameList.get(i) +"</div>");
			 }
			
			allData.append("</table>");
			}
				if(empNameList.isEmpty() || empNameList.size() == 0){
					allData.append("<div class=\"nodata msg\" style=\"width: 85%\"> <span>No Employee selected</span> </div>");
				}
				allData.append("</div>");
		 } else {
			 allData.append("<div class=\"nodata msg\" style=\"width:85%\"><span>No Employee selected</span></div>");
		 }
		String selectEmpIDS = sb.toString()!=null && !sb.toString().equals("") ? sb.toString() :"0";
		allData.append("<input type=\"hidden\" name=\"empselected1\" id=\"empselected1\" value=\""+selectEmpIDS +"\"/>");
		
//		System.out.println("SelectEmpOption ===> " + sbOption.toString());
		
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

	public String getSelectedEmp() {
		return selectedEmp;
	}

	public void setSelectedEmp(String selectedEmp) {
		this.selectedEmp = selectedEmp;
	}

	public String getChboxStatus() {
		return chboxStatus;
	}

	public void setChboxStatus(String chboxStatus) {
		this.chboxStatus = chboxStatus;
	}

	public String getExistemp() {
		return existemp;
	}

	public void setExistemp(String existemp) {
		this.existemp = existemp;
	}

}
