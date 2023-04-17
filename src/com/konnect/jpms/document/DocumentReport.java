package com.konnect.jpms.document;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class DocumentReport extends ActionSupport implements ServletRequestAware, IStatements{

	
	private static final long serialVersionUID = 1L;
	HttpSession session;
	CommonFunctions CF;
	public String execute(){
		UtilityFunctions uF = new UtilityFunctions();
		session= request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN; 
		
		request.setAttribute(PAGE, "/jsp/document/DocumentReport.jsp");
		request.setAttribute(TITLE, "Documents");
		  
		
		boolean isView  = CF.getAccess(session, request, uF);
		if(!isView){
			request.setAttribute(PAGE, PAccessDenied);
			request.setAttribute(TITLE, TAccessDenied);
			return ACCESS_DENIED;
		}
		
		documentList(uF, CF);
		
		return SUCCESS;
	} 
	
	List <FillOrganisation> organisationList;
	List <FillWLocation> wLocationList;
	List <FillDepartment> departmentList;
	List <FillServices> serviceList;
	
	String f_org;
	String f_strWLocation;
	String f_department;
	String f_service;
	
	
	public void documentList(UtilityFunctions uF, CommonFunctions CF){
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs =null;
		Database db=new Database();
		db.setRequest(request);
		
		try {
		
			organisationList = new FillOrganisation(request).fillOrganisation();
			if(getF_org()==null){
				if((String)session.getAttribute(ORGID)!=null){
					setF_org((String)session.getAttribute(ORGID));
				}else{
					setF_org(organisationList.get(0).getOrgId());
				}
			}
			
			wLocationList = new FillWLocation(request).fillWLocation(getF_org());
			departmentList = new FillDepartment(request).fillDepartment(uF.parseToInt(getF_org()));
			serviceList = new FillServices(request).fillServices(getF_org(), uF);
			
			con=db.makeConnection(con);

			
			Map<String, String> hmEmpCode = new HashMap<String, String>();
			Map<String, String> hmEmpName = new HashMap<String, String>();
			CF.getEmpNameCodeMap(con, null, null, hmEmpCode, hmEmpName);
			
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from document_activities da, employee_official_details eod where da.emp_id = eod.emp_id ");
			if(uF.parseToInt(getF_org())>0){
				sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
			}
			if(uF.parseToInt(getF_strWLocation())>0){
				sbQuery.append(" and wlocation_id = "+uF.parseToInt(getF_org()));
			}
			if(uF.parseToInt(getF_department())>0){
				sbQuery.append(" and depart_id = "+uF.parseToInt(getF_org()));
			}
			if(uF.parseToInt(getF_service())>0){
				sbQuery.append(" and service_id like '%,"+uF.parseToInt(getF_service())+",%'");
			}
			sbQuery.append("order by entry_date desc");
			
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();
			List<List<String>> alReport = new ArrayList<List<String>>();
			List<String> alInner = new ArrayList<String>();
			
			while(rs.next()){
				
				
				alInner = new ArrayList<String>();
				
				alInner.add(hmEmpCode.get(rs.getString("emp_id")));
				alInner.add(hmEmpName.get(rs.getString("emp_id")));
				alInner.add(rs.getString("document_name"));
				alInner.add(uF.showData(hmEmpName.get(rs.getString("emp_id")), ""));
				alInner.add(uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));
				
				alInner.add("<a href=\"DownloadDocument.action?header=header&doc_id="+rs.getString("document_id")+"\"><i class=\"fa fa-file-o\" aria-hidden=\"true\" ></i></a>");
				alInner.add("<a href=\"DownloadDocument.action?header=none&doc_id="+rs.getString("document_id")+"\"><i class=\"fa fa-file-o\" aria-hidden=\"true\"></i></a>");
				
				alReport.add(alInner);
				
			}
			rs.close();
			pst.close();
			
			
			
			request.setAttribute("alReport", alReport);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}
	
	

	
	HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request=request;
	}
	
	public List<FillOrganisation> getOrganisationList() {
		return organisationList;
	}
	public void setOrganisationList(List<FillOrganisation> organisationList) {
		this.organisationList = organisationList;
	}
	public List<FillWLocation> getwLocationList() {
		return wLocationList;
	}
	public void setwLocationList(List<FillWLocation> wLocationList) {
		this.wLocationList = wLocationList;
	}
	public List<FillDepartment> getDepartmentList() {
		return departmentList;
	}
	public void setDepartmentList(List<FillDepartment> departmentList) {
		this.departmentList = departmentList;
	}
	public List<FillServices> getServiceList() {
		return serviceList;
	}
	public void setServiceList(List<FillServices> serviceList) {
		this.serviceList = serviceList;
	}
	public String getF_org() {
		return f_org;
	}
	public void setF_org(String f_org) {
		this.f_org = f_org;
	}
	public String getF_strWLocation() {
		return f_strWLocation;
	}
	public void setF_strWLocation(String f_strWLocation) {
		this.f_strWLocation = f_strWLocation;
	}
	public String getF_department() {
		return f_department;
	}
	public void setF_department(String f_department) {
		this.f_department = f_department;
	}
	public String getF_service() {
		return f_service;
	}
	public void setF_service(String f_service) {
		this.f_service = f_service;
	}

	
	

}