package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.konnect.jpms.select.FillTask;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class GetAlignedTypeData extends ActionSupport implements IStatements, ServletRequestAware,ServletResponseAware {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	HttpSession session;
	CommonFunctions CF = null;
	String strSessionEmpId;
	String strSessionOrgId;
	String strUserType;
	
	List<FillProjectList> projectList;
	List<FillProjectList> projectFreqList;
	List<FillTask> taskList;
	List<FillProjectDocument> documentList;
	List<FillProjectInvoice> invoiceList;
	
	String alignedType;
	String postId;
	String pageFrom;
	String proId;
	
	public String execute() {
		
		session = request.getSession(true);
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
//		if (CF == null) {
//			CF = new CommonFunctions();
//		}
		if(CF==null)return LOGIN;
		
		strSessionEmpId = (String)session.getAttribute(EMPID);
		strSessionOrgId = (String)session.getAttribute(ORGID);
		strUserType = (String)session.getAttribute(BASEUSERTYPE);
		
//		System.out.println("pageFrom ====>>> " + getPageFrom());
		UtilityFunctions uF = new UtilityFunctions();
		
		if(uF.parseToInt(getAlignedType()) == PROJECT) {
			if(strUserType != null && strUserType.equals(CUSTOMER)) {
				projectList = new FillProjectList(request).fillProjectDetailsByCustomer(uF.parseToInt(strSessionEmpId), true, true, true);
			} else {
				projectList = new FillProjectList(request).fillProjectAllDetails();
			}
			StringBuilder sbProjectsOption = new StringBuilder();
			for(int i=0; projectList!=null && i<projectList.size(); i++) {
				sbProjectsOption.append("<option value='"+projectList.get(i).getProjectID()+"'>"+projectList.get(i).getProjectName()+"</option>");
			}
			request.setAttribute("sbProjectsOption", sbProjectsOption.toString());
			
		} else if(uF.parseToInt(getAlignedType()) == TASK) {
			if(getPageFrom()==null || (!getPageFrom().trim().equalsIgnoreCase("Project") && !getPageFrom().trim().equalsIgnoreCase("VAPProject"))) {
				if(strUserType != null && strUserType.equals(CUSTOMER)) {
					String custProIds = getCustomerProIds(uF);
					taskList = new FillTask(request).fillProjectTasks(custProIds);
				} else {
					taskList = new FillTask(request).fillTask(uF.parseToInt(strSessionEmpId));
				}
			} else {
				taskList = new FillTask(request).fillProjectTasks(getProId());
			}
			StringBuilder sbTaskOption = new StringBuilder();
			for(int i=0; taskList!=null && i<taskList.size(); i++) {
				sbTaskOption.append("<option value='"+taskList.get(i).getTaskId()+"'>"+taskList.get(i).getTaskName()+"</option>");
			}
			request.setAttribute("sbTaskOption", sbTaskOption.toString());
			
		} else if(uF.parseToInt(getAlignedType()) == DOCUMENT) {
			if(strUserType != null && strUserType.equals(CUSTOMER)) {
				String custProIds = getCustomerProIds(uF);
				documentList = new FillProjectDocument(request).fillProjectWiseDocument(custProIds);
			} else {	
				documentList = new FillProjectDocument(request).fillProjectDocument();
			}
			StringBuilder sbDocumentsOption = new StringBuilder();
			for(int i=0; documentList!=null && i<documentList.size(); i++) {
				sbDocumentsOption.append("<option value='"+documentList.get(i).getDocumentId()+"'>"+documentList.get(i).getDocumentName()+"</option>");
			}
			request.setAttribute("sbDocumentsOption", sbDocumentsOption.toString());
			
		} else if(uF.parseToInt(getAlignedType()) == PRO_TIMESHEET) {
			if(strUserType != null && strUserType.equals(CUSTOMER)) {
//				String custProIds = getCustomerProIds(uF);
				projectFreqList = new FillProjectList(request).fillProjectFrequencyList(strUserType, strSessionEmpId, null);
			} else {
				projectFreqList = new FillProjectList(request).fillProjectFrequencyList(null, null, null);
			}
			StringBuilder sbProTimesheetOption = new StringBuilder();
			for(int i=0; projectFreqList!=null && i<projectFreqList.size(); i++) {
				sbProTimesheetOption.append("<option value='"+projectFreqList.get(i).getProjectID()+"'>"+projectFreqList.get(i).getProjectName()+"</option>");
			}
			request.setAttribute("sbProTimesheetOption", sbProTimesheetOption.toString());
			
		} else if(uF.parseToInt(getAlignedType()) == INVOICE) {
			if(strUserType != null && strUserType.equals(CUSTOMER)) {
				String custProIds = getCustomerProIds(uF);
				invoiceList = new FillProjectInvoice(request).fillProjectWiseInvoices(custProIds);
			} else {
				invoiceList = new FillProjectInvoice(request).fillProjectInvoices();
			}
			StringBuilder sbProInvoiceOption = new StringBuilder();
			for(int i=0; invoiceList!=null && i<invoiceList.size(); i++) {
				sbProInvoiceOption.append("<option value='"+invoiceList.get(i).getInvoiceId()+"'>"+invoiceList.get(i).getInvoiceCode()+"</option>");
			}
			request.setAttribute("sbProInvoiceOption", sbProInvoiceOption.toString());
		}
		return SUCCESS;
	}
	
	
	private String getCustomerProIds(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		StringBuilder sbProIds = null; 
		try {
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("select pro_id from projectmntnc where poc = ?");
			pst.setInt(1, uF.parseToInt(strSessionEmpId));
			rs = pst.executeQuery();
			while(rs.next()) {
				if(sbProIds == null) {
					sbProIds = new StringBuilder();
					sbProIds.append(rs.getString("pro_id"));
				} else {
					sbProIds.append(","+rs.getString("pro_id"));
				}
			}
			rs.close();
			pst.close();
			
			if(sbProIds == null) {
				sbProIds = new StringBuilder();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return sbProIds.toString();
	}

	HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		// TODO Auto-generated method stub
		this.request = request;
		
	}
	
	@Override
	public void setServletResponse(HttpServletResponse arg0) {
		// TODO Auto-generated method stub
	}

	public String getAlignedType() {
		return alignedType;
	}

	public void setAlignedType(String alignedType) {
		this.alignedType = alignedType;
	}

	public List<FillProjectList> getProjectList() {
		return projectList;
	}

	public void setProjectList(List<FillProjectList> projectList) {
		this.projectList = projectList;
	}

	public List<FillTask> getTaskList() {
		return taskList;
	}

	public void setTaskList(List<FillTask> taskList) {
		this.taskList = taskList;
	}

	public List<FillProjectDocument> getDocumentList() {
		return documentList;
	}

	public void setDocumentList(List<FillProjectDocument> documentList) {
		this.documentList = documentList;
	}

	public List<FillProjectList> getProjectFreqList() {
		return projectFreqList;
	}

	public void setProjectFreqList(List<FillProjectList> projectFreqList) {
		this.projectFreqList = projectFreqList;
	}

	public String getPostId() {
		return postId;
	}

	public void setPostId(String postId) {
		this.postId = postId;
	}

	public String getPageFrom() {
		return pageFrom;
	}

	public void setPageFrom(String pageFrom) {
		this.pageFrom = pageFrom;
	}

	public String getProId() {
		return proId;
	}

	public void setProId(String proId) {
		this.proId = proId;
	}

	public List<FillProjectInvoice> getInvoiceList() {
		return invoiceList;
	}

	public void setInvoiceList(List<FillProjectInvoice> invoiceList) {
		this.invoiceList = invoiceList;
	}

}
