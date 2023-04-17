package com.konnect.jpms.export;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.konnect.jpms.util.IStatements;
import com.opensymphony.xwork2.ActionSupport;

public class ExcelExportReports extends ActionSupport implements ServletRequestAware, ServletResponseAware, IStatements {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	HttpSession session;
	HttpServletRequest request;
	HttpServletResponse response;
	
	public String execute() throws Exception {
		
		String filePath = null;
		filePath = "C:\\temp\\";
		
		
		Excel p = new Excel(filePath, "Test");
		
		String str[] = {"A","B","C","D","A","B","C","D"};
		p.addHeaders(str);
		
		p.writeExcelFile(response, "test.xls");
		
		return SUCCESS;
	}
	
	@Override
	public void setServletRequest(HttpServletRequest arg0) {
		// TODO Auto-generated method stub
		request = arg0;
	}

	@Override
	public void setServletResponse(HttpServletResponse arg0) {
		// TODO Auto-generated method stub
		response = arg0;
	}
	
}
