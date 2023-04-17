package com.konnect.jpms.performance;

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

public class GetSelectedEmployee extends ActionSupport implements
		ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	String strSessionEmpId = null;
	CommonFunctions CF = null;

	private static Logger log = Logger.getLogger(GetSelectedEmployee.class);
	
	private String selectedEmp;
	private String chboxStatus;
	private String existemp;
	private String form;
	private String indiSbEmpIds;
	
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
		UtilityFunctions uF=new UtilityFunctions();
		
		try{
			con = db.makeConnection(con);
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			
			List<String> indiGoalEmpIds = new ArrayList<String>();
			if(getIndiSbEmpIds() != null) {
				indiGoalEmpIds = Arrays.asList(getIndiSbEmpIds().toString().split(","));
			}
			request.setAttribute("indiGoalEmpIds", indiGoalEmpIds);
			
			StringBuilder sb=new StringBuilder();
			StringBuilder sbOption = new StringBuilder("");
			List<String> empNameList=new ArrayList<String>();
			List<List<String>> selectEmpList = new ArrayList<List<String>>(); 
			for(String empId:list){
				if(uF.parseToInt(empId.trim()) == 0){
					continue;
				}
				sbOption.append("<option value=\"" + empId + "\">" + hmEmpName.get(empId) + "</option>");
				empNameList.add(hmEmpName.get(empId.trim()));
				sb.append(empId.trim()+",");	
				
				List<String> innerList = new ArrayList<String>();
				innerList.add(empId.trim());
				innerList.add(hmEmpName.get(empId.trim()));
				selectEmpList.add(innerList);
			}
			
			
			
			StringBuffer allData = new StringBuffer();
			if(selectEmpList != null && !selectEmpList.isEmpty() && selectEmpList.size() > 0) {
				allData.append("<div style=\"border: 2px solid #ccc;\">");
				allData.append("<div style=\"padding: 8px 20px; border: 2px solid lightgray;\"><b>Employee</b></div> ");
				for(int i = 0; i < selectEmpList.size(); i++){
					List<String> innerList = (List<String>) selectEmpList.get(i);
					String isInIndiGoal = "N";
					if(indiGoalEmpIds.contains(innerList.get(0))) {
						isInIndiGoal = "Y";
					}
					allData.append("<div style=\"float: left; width: 100%; margin: 5px;\"><strong>"+ (i+1) +".</strong>&nbsp;&nbsp;"+innerList.get(1));
					allData.append("<a href=\"javascript: void(0)\" onclick=\"getGoalSelectedEmp(false, '"+innerList.get(0)+"', '"+getForm()+"', '"+isInIndiGoal+"');\">" +
							"&nbsp;<img border=\"0\" style=\"width: 12px; height: 12px;\" src=\""+request.getContextPath()+"/images1/arrow_reset1.png\"/></a>");
					allData.append("</div>");
				}
				allData.append("</div>");
			} else {
				allData.append("<div class=\"nodata msg\" style=\"width:85%\"><span>No Employee selected</span></div>");
			}
			
			
//			if(empNameList != null && !empNameList.isEmpty()) {
//	
//				allData.append("<div style=\"border: 2px solid #ccc;\">");		
//				if(empNameList != null && !empNameList.isEmpty() && empNameList.size() > 0){
//					allData.append("<div style=\"padding: 8px 20px; border: 2px solid lightgray;\"><b>Employee</b></div> ");
//	
//				for(int i=0;i<empNameList.size();i++){
//					allData.append("<div style=\"float: left; width: 100%; margin: 5px;\"><strong>"+ (i+1) +".</strong>&nbsp;&nbsp;"+empNameList.get(i));
//					allData.append("<a href=\"javascript: void(0)\" onclick=\"getGoalSelectedEmp(false,this.value,'"+getForm()+"');\">" +
//							"<img border=\"0\" style=\"width: 12px; height: 12px;\" src=\""+request.getContextPath()+"/images1/arrow_reset1.png\"/></a>");
//					allData.append("</div>");
//				 }
//				
//				}
//					if(empNameList.isEmpty() || empNameList.size() == 0){
//						allData.append("<div class=\"nodata msg\" style=\"width: 85%\"> <span>No Employee selected</span> </div>");
//					}
//					allData.append("</div>");
//			 } else {
//				 allData.append("<div class=\"nodata msg\" style=\"width:85%\"><span>No Employee selected</span></div>");
//			 }
			String selectEmpIDS = sb.toString()!=null && !sb.toString().equals("") ? sb.toString() :"0";
			allData.append("<input type=\"hidden\" name=\"empselected\" id=\"empselected\" value=\""+selectEmpIDS +"\"/>");
			allData.append("<input type=\"hidden\" name=\"indiSbEmpIds\" id=\"indiSbEmpIds\" value=\""+getIndiSbEmpIds() +"\"/>");
			if(form == null || !form.equals("frmKRA")){
				allData.append("::::");
				allData.append("<select name =\"teamEmpID\" id =\"teamEmpID\" class=\"validateRequired form-control autoWidth\"> <option value=\"\">Select</option>");
				allData.append(sbOption.toString());
				allData.append("</select>"); 
			}
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

	public String getForm() {
		return form;
	}

	public void setForm(String form) {
		this.form = form;
	}

	public String getIndiSbEmpIds() {
		return indiSbEmpIds;
	}

	public void setIndiSbEmpIds(String indiSbEmpIds) {
		this.indiSbEmpIds = indiSbEmpIds;
	}

}
