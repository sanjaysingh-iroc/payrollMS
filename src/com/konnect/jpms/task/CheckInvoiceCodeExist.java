package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class CheckInvoiceCodeExist extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;

	CommonFunctions CF;
	HttpSession session;
	String strSessionEmpId;
	
	String strInvoiceCode;
	String strProId;
	String strProjectOwner; 
	
	public String execute() throws Exception {
		UtilityFunctions uF=new UtilityFunctions();
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;
		
		strSessionEmpId = (String)session.getAttribute(EMPID);
		
		if(uF.parseToInt(getStrProjectOwner())==0) {
			setStrProjectOwner(strSessionEmpId);
		}
		getInvoiceCodeExist(uF);
		return SUCCESS; 

	}
	
	
	private void getInvoiceCodeExist(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			con = db.makeConnection(con);
			int count = 0;
			pst = con.prepareStatement("select * from promntc_invoice_details where invoice_code = ? ");
			pst.setString(1, getStrInvoiceCode().trim());
			rs = pst.executeQuery();
			while (rs.next()) {
				count++;
			}
			rs.close();
			pst.close();
			
			Map<String, Map<String, String>> hmWorkLocation = CF.getWorkLocationMap(con);
			Map<String, String> hmDept = CF.getDeptMap(con);
			Map<String, String> hmEmpDepartment = CF.getEmpDepartmentMap(con);
			
			String wLocationId = null;
			String departId = null;
			if(uF.parseToInt(getStrProId())> 0) {
				pst = con.prepareStatement("select * from projectmntnc pmt, client_details cd where pmt.client_id = cd.client_id and pmt.pro_id=?");
				pst.setInt(1, uF.parseToInt(getStrProId()));
				//System.out.println("pst ====>>> " + pst);
				rs = pst.executeQuery();
				while (rs.next()) {
					wLocationId = rs.getString("wlocation_id");
					departId = rs.getString("department_id");
				}  
				rs.close();
				pst.close();
			} else {
				wLocationId = CF.getEmpWlocationId(con, uF, getStrProjectOwner());
				departId = hmEmpDepartment.get(getStrProjectOwner());
			}
			
//	 		=============================== Invoice Code Generation ======================================
			Map<String, String> hmLocation = hmWorkLocation.get(wLocationId);
			
			String[] arr = CF.getFinancialYear(con, uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT), CF, uF);
			String locationCode = hmLocation.get("WL_NAME");			
			String departCode = "";
			if(hmDept.get(departId)!=null && hmDept.get(departId).contains(" ")) {
				String[] temp = hmDept.get(departId).toUpperCase().split(" ");
				for(int i=0;i<temp.length;i++) {
					departCode+=temp[i].substring(0,1);
				}
			} else if(hmDept.get(departId)!=null) {
				departCode = hmDept.get(departId).substring(0,hmDept.get(departId).length()>3 ? 3 : hmDept.get(departId).length());
			}
			
			int cnt = 0;
			pst=con.prepareStatement("select max(invoice_no) as invoice_no from promntc_invoice_details where wlocation_id=?");
			pst.setInt(1, uF.parseToInt(wLocationId));
			rs=pst.executeQuery();
			while(rs.next()) {
				cnt = rs.getInt("invoice_no");
			}
			rs.close();
			pst.close();
			cnt++;
			
			String invoiceCode = cnt + "-" + uF.getDateFormat(arr[0], DATE_FORMAT, "yyyy") + "-" + uF.getDateFormat(arr[1], DATE_FORMAT, "yy") + "/" + locationCode.toUpperCase(); //+"/"+departCode.toUpperCase()
// 		========================================================= End =========================================================			
		
//			String msg = count+"::::"+ownerEmail+"::::"+ownerSign+"::::"+wLocation+"::::"+locationTel+"::::"+locationFax+"::::"+orgPan
//				+"::::"+orgMCARegNo+"::::"+orgSTRegNo; //+"::::"+locationECCNo2
			String msg = count+"::::"+invoiceCode+"::::";
			request.setAttribute("STATUS_MSG", msg);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}


	public String getStrInvoiceCode() {
		return strInvoiceCode;
	}

	public void setStrInvoiceCode(String strInvoiceCode) {
		this.strInvoiceCode = strInvoiceCode;
	}

	public String getStrProId() {
		return strProId;
	}

	public void setStrProId(String strProId) {
		this.strProId = strProId;
	}

	public String getStrProjectOwner() {
		return strProjectOwner;
	}

	public void setStrProjectOwner(String strProjectOwner) {
		this.strProjectOwner = strProjectOwner;
	}


	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

}
