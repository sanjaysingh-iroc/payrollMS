package com.konnect.jpms.export;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class LoanInfoExport implements ServletRequestAware, ServletResponseAware, IStatements {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	HttpSession session;
	HttpServletRequest request;
	HttpServletResponse response;
	CommonFunctions CF = null;
	public void execute() throws Exception {
		session = request.getSession();
		request.setAttribute(PAGE, PGeneratePaySlip);
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		try {
			con = db.makeConnection(con);
			
			
			Map hmWlocationMap = CF.getWorkLocationMap(con);
			Map hmEmpWlocationMap = CF.getEmpWlocationMap(con);
			
			
			pst = con.prepareStatement("select * from loan_details");
			rs = pst.executeQuery();
			Map hmLoanCode = new HashMap();
			while(rs.next()){
				hmLoanCode.put(rs.getString("loan_id"), rs.getString("loan_code"));
			}
			rs.close();
			pst.close();
			
			
			
			
//			StringBuilder sb = new StringBuilder();
			StringBuffer sb = new StringBuffer();
			pst = con.prepareStatement("select * from loan_applied_details where is_approved = 1 and balance_amount>0");
			rs = pst.executeQuery();
//			System.out.println("pst====>"+pst);
			while(rs.next()){
				
				String strWlocationId = (String)hmEmpWlocationMap.get(rs.getString("emp_id"));
				Map<String, String> hm = (Map<String, String>)hmWlocationMap.get(strWlocationId);
				if(hm==null){
					hm = new HashMap<String, String>();
				}
				
//				sb.append("0000"+uF.showData(hm.get("WL_CODE"), "")+rs.getString("loan_code"));
				sb.append("000099"+uF.showData((String)hmLoanCode.get(rs.getString("loan_id")), ""));
				sb.append("     ");// 5 spaces
				
				int nTotalLen1 = 16;
				String strLoanAccNo = rs.getString("loan_acc_no");
				if(strLoanAccNo==null){
					strLoanAccNo="000";
				}
				int nRem1 = nTotalLen1 - strLoanAccNo.length(); 
				for(int i=0; i<nRem1; i++){
					sb.append("0");	
				}
				sb.append(strLoanAccNo);
				
				String strLoanEmi  = uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(rs.getString("loan_emi")));
				int nTotalLen = 16;
				int nRem = nTotalLen - strLoanEmi.length(); 
				for(int i=0; i<nRem; i++){
					sb.append("0");	
				}
				sb.append(strLoanEmi);	
				sb.append(System.getProperty("line.separator"));
			}
			rs.close();
			pst.close();
			
			
			publishReport(sb.toString(), uF);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	public void publishReport(String strData, UtilityFunctions uF) {
		try {

			String strMonth = uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, "MMM");
			String strYear = uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, "yy");
			
			ServletOutputStream op = response.getOutputStream();
			response.setContentType("application/octet-stream");
			response.setContentLength((int) strData.length());
			response.setHeader("Content-Disposition", "attachment; filename=\"" + "loan_"+strMonth+"_"+strYear+".txt" + "\"");
			op.write(strData.getBytes());
			op.flush();
			op.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
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
