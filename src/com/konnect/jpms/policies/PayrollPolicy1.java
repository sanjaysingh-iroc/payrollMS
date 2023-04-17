package com.konnect.jpms.policies;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillDesignation;
import com.konnect.jpms.select.FillEmploymentType;
import com.konnect.jpms.select.FillFrequency;
import com.konnect.jpms.select.FillPayMode;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class PayrollPolicy1 extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	CommonFunctions CF = null;
	public String execute() throws Exception {

		session = request.getSession();if(session==null)return LOGIN;
		request.setAttribute(PAGE, PPolicyPayroll1);		
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		
		

		String strEdit = request.getParameter("E");
		String strDelete = request.getParameter("D");
		String strEMPID = request.getParameter("EMPID");
		String strCCID = request.getParameter("CCID");
		String strP = request.getParameter("P");

		
//		System.out.println("=====>strP==>"+strP);
		
		
		
		String referer = request.getHeader("Referer");

		if (referer != null) {
			int index1 = referer.indexOf(request.getContextPath());
			int index2 = request.getContextPath().length();
			referer = referer.substring(index1 + index2 + 1);
		}
		setRedirectUrl(referer);
		
		if(strP!=null){
			setStrP(strP);
		}
		
		
		if (strEMPID != null && strCCID!=null) {
			viewPolicyPayroll(strEMPID, strCCID);
			
			request.setAttribute(TITLE, TViewPayrollPolicy);
			return SUCCESS;
		}
		if (strDelete != null) {
			deletePolicyPayroll(strDelete);
			request.setAttribute(TITLE, TDeletePayrollPolicy);
			return VIEW;
		}

		
		if (getPayrollPolicyId() != null && getPayrollPolicyId().length()>0) {
			updatePolicyPayroll();
			request.setAttribute(TITLE, TEditPayrollPolicy);
			
			System.out.println("==  getStrP()==>"+getStrP()+"<===");
			
			if(getStrP()!=null && getStrP().length()>0){
				
				return REPORT;
			}else{
				request.setAttribute(PAGE, PPolicyPayroll2);
				return UPDATE;	
			}
		}else if (getPayMode() != null) {
			
			insertPolicyPayroll(strEMPID, strCCID);
			request.setAttribute(TITLE, TAddPayrollPolicy);
			
			System.out.println("getStrP()="+getStrP());
			
			if(getStrP()!=null && !getStrP().equalsIgnoreCase("")){
				return REPORT;
			}
		} 
		return loadPolicyPayroll();
	}
	
	
	public String loadValidatePolicyPayroll() {
		request.setAttribute(PAGE, PPolicyPayroll1);
		request.setAttribute(TITLE, TAddPayrollPolicy);
		
		frequencyTypeList = new FillFrequency().fillFrequency();
		payModeList = new FillPayMode().fillPayMode();
		desigList = new FillDesignation(request).fillDesignation();
		empTypeList = new FillEmploymentType().fillEmploymentType(request);
		serviceList = new FillServices(request).fillServices();
		
		return LOAD;
	}
	
	public String loadPolicyPayroll() {
		request.setAttribute(PAGE, PPolicyPayroll2);
		request.setAttribute(TITLE, TAddPayrollPolicy);
		
		setPayrollPolicyId(null);
		setFrequencyType(null);
		setPayDay(null);
		setDesignation(null);
		setPayMode(null);
		setFxdAmount(null);
		
		setMonAmount(null);
		setTuesAmount(null);
		setWedAmount(null);
		setThursAmount(null);
		setFriAmount(null);
		setSatAmount(null);
		setSunAmount(null);
		setHolidayLoading("0");
		
		setHolidayLoading("0");
		setMonHolidayLoading("0");
		setTueHolidayLoading("0");
		setWedHolidayLoading("0");
		setThursHolidayLoading("0");
		setFriHolidayLoading("0");
		setSatHolidayLoading("0");
		setSunHolidayLoading("0");
		
		frequencyTypeList = new FillFrequency().fillFrequency();
		payModeList = new FillPayMode().fillPayMode();
		desigList = new FillDesignation(request).fillDesignation();
		empTypeList = new FillEmploymentType().fillEmploymentType(request);
		serviceList = new FillServices(request).fillServices();
		
		return LOAD;
	}

	
	public String updatePolicyPayroll() {

		Connection con = null;
		PreparedStatement pst =null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			
			
			
			
			con = db.makeConnection(con);
			pst = con.prepareStatement(updatePayrollPolicy);
			
//			pst.setString(1, getFrequencyType());
//			pst.setString(2, getPayDay());
//			pst.setString(3, getEmpType());
			pst.setInt(1, uF.parseToInt((String)session.getAttribute("EMPID")));
//			pst.setInt(2, uF.parseToInt(getDesignation()));			
			pst.setString(2, getPayMode());
			pst.setDouble(3, uF.parseToDouble(getFxdAmount()));
//			pst.setInt(4, uF.parseToInt(getService()));
			
			pst.setDouble(4, uF.parseToDouble(getMonAmount()));
			pst.setDouble(5, uF.parseToDouble(getTuesAmount()));
			pst.setDouble(6, uF.parseToDouble(getWedAmount()));
			pst.setDouble(7, uF.parseToDouble(getThursAmount()));
			pst.setDouble(8, uF.parseToDouble(getFriAmount()));
			pst.setDouble(9, uF.parseToDouble(getSatAmount()));
			pst.setDouble(10, uF.parseToDouble(getSunAmount()));
			pst.setDouble(11, uF.parseToDouble(getHolidayLoading()));
			
			pst.setDouble(12, uF.parseToDouble(getMonHolidayLoading()));
			pst.setDouble(13, uF.parseToDouble(getTueHolidayLoading()));
			pst.setDouble(14, uF.parseToDouble(getWedHolidayLoading()));
			pst.setDouble(15, uF.parseToDouble(getThursHolidayLoading()));
			pst.setDouble(16, uF.parseToDouble(getFriHolidayLoading()));
			pst.setDouble(17, uF.parseToDouble(getSatHolidayLoading()));
			pst.setDouble(18, uF.parseToDouble(getSunHolidayLoading()));
			
			pst.setInt(19, uF.parseToInt(getPayrollPolicyId()));
			
			
			pst.execute();
			pst.close();
			
			Map hmServicesMap = CF.getServicesMap(con, true);
			StringBuilder sb = new  StringBuilder();
//			String str = (String) session.getAttribute("COST");
			Map hm = (Map) session.getAttribute("COST");
			
			if(hm==null){
				hm = new HashMap();
			}
			sb = new  StringBuilder();
			if(getPayMode()!=null && getPayMode().equalsIgnoreCase("H")){
				
				sb.append(
						"<tr>" +
						"<td colspan=\"15\" class=\"reportHeading\">"+(String)hmServicesMap.get(getService())+"</td>" +
						"</tr>" +
						"<tr>" +
						"<td class=\"reportHeading\">Monday</td><td class=\"reportHeading\">Monday Loading</td><td class=\"reportHeading\">Tuesday</td><td class=\"reportHeading\">Tuesday Loading</td><td class=\"reportHeading\">Wednesday</td><td class=\"reportHeading\">Wednesday Loading</td><td class=\"reportHeading\">Thursday</td><td class=\"reportHeading\">Thursday Loading</td><td class=\"reportHeading\">Friday</td><td class=\"reportHeading\">Friday Loading</td><td class=\"reportHeading\">Saturday</td><td class=\"reportHeading\">Saturday Loading</td><td class=\"reportHeading\">Sunday</td><td class=\"reportHeading\">Sunday Loading</td>" +
						"</tr>" +
						"<tr>" +
						"<td class=\"reportLabel\">"+getMonAmount()+"</td><td class=\"reportLabel\">"+getMonHolidayLoading()+"%"+"</td><td class=\"reportLabel\">"+getTuesAmount()+"</td><td class=\"reportLabel\">"+getTueHolidayLoading()+"%"+"</td><td class=\"reportLabel\">"+getWedAmount()+"</td><td class=\"reportLabel\">"+getWedHolidayLoading()+"%"+"</td><td class=\"reportLabel\">"+getThursAmount()+"</td><td class=\"reportLabel\">"+getThursHolidayLoading()+"%"+"</td><td class=\"reportLabel\">"+getFriAmount()+"</td><td class=\"reportLabel\">"+getFriHolidayLoading()+"%"+"</td><td class=\"reportLabel\">"+getSatAmount()+"</td><td class=\"reportLabel\">"+getSatHolidayLoading()+"%"+"</td><td class=\"reportLabel\">"+getSunAmount()+"</td><td class=\"reportLabel\">"+getSunHolidayLoading()+"%"+"</td>" +
						"</tr>"+
						"<tr>" +
						"<td colspan=\"15\" >&nbsp;</td>" +
						"</tr>"
						);
				
			}else if(getPayMode()!=null && getPayMode().equalsIgnoreCase("X")){
				
				sb.append(
						"<tr>" +
						"<td colspan=\"8\" class=\"reportHeading\">"+(String)hmServicesMap.get(getService())+"</td>" +
						"</tr>" +
						"<tr>" +
						"<td colspan=\"7\" class=\"reportHeading\">Fixed</td><td class=\"reportHeading\">Loading</td>" +
						"</tr>" +
						"<tr>" +
						"<td colspan=\"7\" class=\"reportLabel\">"+getFxdAmount()+"</td><td class=\"reportLabel\">"+getHolidayLoading()+"%</td>" +
						"</tr>"+
						"<tr>" +
						"<td colspan=\"8\" >&nbsp;</td>" +
						"</tr>"
						);
			}
			hm.put(getService(), sb.toString());
			
			session.setAttribute("COST", hm);
			
			
			request.setAttribute(MESSAGE, "Rate updated successfully!");

		} catch (Exception e) {
			e.printStackTrace();			
			request.setAttribute(MESSAGE, "Error in updation");
		}finally{
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}
	
	public String insertPolicyPayroll(String strEMPID, String strCCID) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		try {
			
			Map hmServicesMap = CF.getServicesMap(con, true);
			
			
			con = db.makeConnection(con);
			pst = con.prepareStatement(insertPayrollPolicy);
//			pst.setString(1, getFrequencyType());
//			pst.setString(2, getPayDay());
//			pst.setString(3, getEmpType());
			pst.setInt(1, uF.parseToInt((String)session.getAttribute("EMPID")));
			pst.setInt(2, uF.parseToInt(getDesignation()));
//			pst.setInt(2, uF.parseToInt(strEMPID));
			pst.setString(3, getPayMode());
			pst.setDouble(4, uF.parseToDouble(getFxdAmount()));
			pst.setInt(5, uF.parseToInt(getService()));
//			pst.setInt(5, uF.parseToInt(strCCID));
			
			pst.setDouble(6, uF.parseToDouble(getMonAmount()));
			pst.setDouble(7, uF.parseToDouble(getTuesAmount()));
			pst.setDouble(8, uF.parseToDouble(getWedAmount()));
			pst.setDouble(9, uF.parseToDouble(getThursAmount()));
			pst.setDouble(10, uF.parseToDouble(getFriAmount()));
			pst.setDouble(11, uF.parseToDouble(getSatAmount()));
			pst.setDouble(12, uF.parseToDouble(getSunAmount()));
			pst.setDouble(13, uF.parseToDouble(getHolidayLoading()));
			
			pst.setDouble(14, uF.parseToDouble(getMonHolidayLoading()));
			pst.setDouble(15, uF.parseToDouble(getTueHolidayLoading()));
			pst.setDouble(16, uF.parseToDouble(getWedHolidayLoading()));
			pst.setDouble(17, uF.parseToDouble(getThursHolidayLoading()));
			pst.setDouble(18, uF.parseToDouble(getFriHolidayLoading()));
			pst.setDouble(19, uF.parseToDouble(getSatHolidayLoading()));
			pst.setDouble(20, uF.parseToDouble(getSunHolidayLoading()));
			
			pst.execute();
			pst.close();
			request.setAttribute(MESSAGE, "Rates added successfully!");
			
			
//			StringBuilder sb = new  StringBuilder();
			
			StringBuilder sb = new  StringBuilder();
			
			Map hm = (Map) session.getAttribute("COST");
			
			if(hm==null){
				hm = new HashMap();
			}
			sb = new  StringBuilder();
			
			if(getPayMode()!=null && getPayMode().equalsIgnoreCase("H")){
			
				sb.append(
						"<tr>" +
						"<td colspan=\"15\" class=\"reportHeading\">"+(String)hmServicesMap.get(getService())+"</td>" +
						"</tr>" +
						"<tr>" +
						"<td class=\"reportHeading\">Monday</td><td class=\"reportHeading\">Monday Loading</td><td class=\"reportHeading\">Tuesday</td><td class=\"reportHeading\">Tuesday Loading</td><td class=\"reportHeading\">Wednesday</td><td class=\"reportHeading\">Wednesday Loading</td><td class=\"reportHeading\">Thursday</td><td class=\"reportHeading\">Thursday Loading</td><td class=\"reportHeading\">Friday</td><td class=\"reportHeading\">Friday Loading</td><td class=\"reportHeading\">Saturday</td><td class=\"reportHeading\">Saturday Loading</td><td class=\"reportHeading\">Sunday</td><td class=\"reportHeading\">Sunday Loading</td>" +
						"</tr>" +
						"<tr>" +
						"<td class=\"reportLabel\">"+getMonAmount()+"</td><td class=\"reportLabel\">"+getMonHolidayLoading()+"%"+"</td><td class=\"reportLabel\">"+getTuesAmount()+"</td><td class=\"reportLabel\">"+getTueHolidayLoading()+"%"+"</td><td class=\"reportLabel\">"+getWedAmount()+"</td><td class=\"reportLabel\">"+getWedHolidayLoading()+"%"+"</td><td class=\"reportLabel\">"+getThursAmount()+"</td><td class=\"reportLabel\">"+getThursHolidayLoading()+"%"+"</td><td class=\"reportLabel\">"+getFriAmount()+"</td><td class=\"reportLabel\">"+getFriHolidayLoading()+"%"+"</td><td class=\"reportLabel\">"+getSatAmount()+"</td><td class=\"reportLabel\">"+getSatHolidayLoading()+"%"+"</td><td class=\"reportLabel\">"+getSunAmount()+"</td><td class=\"reportLabel\">"+getSunHolidayLoading()+"%"+"</td>" +
						"</tr>"+
						"<tr>" +
						"<td colspan=\"15\" >&nbsp;</td>" +
						"</tr>"
						);
				
			}else if(getPayMode()!=null && getPayMode().equalsIgnoreCase("X")){
				
				sb.append(
						"<tr>" +
						"<td colspan=\"8\" class=\"reportHeading\">"+(String)hmServicesMap.get(getService())+"</td>" +
						"</tr>" +
						"<tr>" +
						"<td colspan=\"7\" class=\"reportHeading\">Fixed</td><td class=\"reportHeading\">Loading</td>" +
						"</tr>" +
						"<tr>" +
						"<td colspan=\"7\" class=\"reportLabel\">"+getFxdAmount()+"</td><td class=\"reportLabel\">"+getHolidayLoading()+"%</td>" +
						"</tr>"+
						"<tr>" +
						"<td colspan=\"8\" >&nbsp;</td>" +
						"</tr>"
						);
				
			}
			
			hm.put(getService(), sb.toString());
			
			session.setAttribute("COST", hm);
			int ServiceNo = uF.parseToInt((String)session.getAttribute("ServicesLinkNo"));
//			session.setAttribute("ServicesLinkNo", (ServiceNo-1)+""); // Uncomment this code if you wish to use salary cost center wise.
			session.setAttribute("ServicesLinkNo", 1+"");
			

		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute(MESSAGE, "Error in insertion");
		}finally{
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}

	public String viewPolicyPayroll(String strEMPID, String strCCID) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {

			
			setService(strCCID);
			setDesignation(strEMPID);
			
			
			
			con = db.makeConnection(con);
			pst = con.prepareStatement(selectPayrollPolicyV);
			pst.setInt(1, uF.parseToInt(strEMPID));
			pst.setInt(2, uF.parseToInt(strCCID));
			rs = pst.executeQuery();
			while (rs.next()) {
				
				setPayrollPolicyId(rs.getString("payroll_policy_id"));
				setFrequencyType(rs.getString("frequency"));
				setPayDay(rs.getString("pay_day"));
				setService(rs.getString("service_id"));
				setDesignation(rs.getString("desig_id"));
				setPayMode(rs.getString("paymode"));
				setEmpType(rs.getString("emptype"));
				setFxdAmount(rs.getString("fxdamount"));
				
				
				setMonAmount(rs.getString("monamount"));
				setTuesAmount(rs.getString("tuesamount"));
				setWedAmount(rs.getString("wedamount"));
				setThursAmount(rs.getString("thursamount"));
				setFriAmount(rs.getString("friamount"));
				setSatAmount(rs.getString("satamount"));
				setSunAmount(rs.getString("sunamount"));
				setHolidayLoading(rs.getString("loading"));
				
				setMonHolidayLoading(rs.getString("loading_mon"));
				setTueHolidayLoading(rs.getString("loading_tue"));
				setWedHolidayLoading(rs.getString("loading_wed"));
				setThursHolidayLoading(rs.getString("loading_thurs"));
				setFriHolidayLoading(rs.getString("loading_fri"));
				setSatHolidayLoading(rs.getString("loading_sat"));
				setSunHolidayLoading(rs.getString("loading_sun"));
			}
			rs.close();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
		frequencyTypeList = new FillFrequency().fillFrequency();
		payModeList = new FillPayMode().fillPayMode();
		desigList = new FillDesignation(request).fillDesignation();
		empTypeList = new FillEmploymentType().fillEmploymentType(request);
		serviceList = new FillServices(request).fillServices();
		
		return SUCCESS;

	}

	public String deletePolicyPayroll(String strDelete) {

		Connection con = null;
		PreparedStatement pst=null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement(deletePayrollPolicy);
			pst.setInt(1, uF.parseToInt(strDelete));
			pst.execute();
			pst.close();
			
			request.setAttribute(MESSAGE, "Rate deleted successfully!");
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute(MESSAGE, "Error in deletion");
			return ERROR;
		}finally{
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}
	
	
	String redirectUrl;
	String strP;
	
	String payrollPolicyId;
	String frequencyType;
	String payDay;
	String designation;
	String payMode;
	String fxdAmount;
	String service;
	
	
	String monAmount;
	String tuesAmount;
	String wedAmount;
	String thursAmount;
	String friAmount;
	String satAmount;
	String sunAmount;
	
	String empType;
	String holidayLoading;
	
	String monHolidayLoading;
	String tueHolidayLoading;
	String wedHolidayLoading;
	String thursHolidayLoading;
	String friHolidayLoading;
	String satHolidayLoading;
	String sunHolidayLoading;
	
	public void validate() {
		
		UtilityFunctions uF = new UtilityFunctions();
		
	if ((getFrequencyType()!=null && getFrequencyType().equalsIgnoreCase("Select Frequency") ) ) {
	        addFieldError("Frequency", "Frequency is required");
	  }
	if (getPayDay()!=null && getPayDay().length() == 0) {
        addFieldError("payDay", "Pay Day is required");
    }
	
    if (getEmpType()!=null && getEmpType().equalsIgnoreCase("0")) {
        addFieldError("empType", "Select Employment Type is required");
    }
    if (getDesignation()!=null && Integer.parseInt(getDesignation()) == 0) {
        addFieldError("designation", "Select Designation is required");
    }
    if (getPayMode()!=null && getPayMode().equalsIgnoreCase("0")) {
        addFieldError("payMode", "Pay Mode is required");
    }
    if (getService()!=null && Integer.parseInt(getService()) == 0) {
        addFieldError("service", "Cost Centre Type is required");
    }
    if (getFxdAmount()!=null && getFxdAmount().length() == 0) {
        addFieldError("fxdAmount", "Amount is required");
    }else if(getFxdAmount()!=null && !uF.isNumber(getFxdAmount())){
    	addFieldError("fxdAmount", "Please add fixed amount in numbers only.");
    }
    if (getMonAmount()!=null && getMonAmount().length() == 0) {
        addFieldError("monAmount", "Monday is required");
    }else if(getMonAmount()!=null && !uF.isNumber(getMonAmount())){
    	addFieldError("monAmount", "Please add rate for Monday in numbers only.");
    }
    if (getTuesAmount()!=null && getTuesAmount().length() == 0) {
        addFieldError("tuesAmount", "Tuesday is required");
    }else if(getTuesAmount()!=null && !uF.isNumber(getTuesAmount())){
    	addFieldError("tuesAmount", "Please add rate for Tuesday in numbers only.");
    }
    if (getWedAmount()!=null && getWedAmount().length() == 0) {
        addFieldError("wedAmount", "Wednesday is required");
    }else if(getWedAmount()!=null && !uF.isNumber(getWedAmount())){
    	addFieldError("wedAmount", "Please add rate for Wednesday in numbers only.");
    }
    if (getThursAmount()!=null && getThursAmount().length() == 0) {
        addFieldError("thursAmount", "Thursday is required");
    }else if(getThursAmount()!=null && !uF.isNumber(getThursAmount())){
    	addFieldError("thursAmount", "Please add rate for Thursday in numbers only.");
    }
    if (getFriAmount()!=null && getFriAmount().length() == 0) {
        addFieldError("friAmount", "Friday is required");
    }else if(getFriAmount()!=null && !uF.isNumber(getFriAmount())){
    	addFieldError("friAmount", "Please add rate for Friday in numbers only.");
    }
    if (getSatAmount()!=null && getSatAmount().length() == 0) {
        addFieldError("satAmount", "Saturday is required");
    }else if(getSatAmount()!=null && !uF.isNumber(getSatAmount())){
    	addFieldError("satAmount", "Please add rate for Saturday in numbers only.");
    }
    if (getSunAmount()!=null && getSunAmount().length() == 0) {
        addFieldError("sunAmount", "Sunday is required");
    }else if(getSunAmount()!=null && !uF.isNumber(getSunAmount())){
    	addFieldError("sunAmount", "Please add rate for Sunday in numbers only.");
    }
   
    
    loadValidatePolicyPayroll();
    
    }
	List<FillServices> serviceList;
	List<FillEmploymentType> empTypeList;
	
	List<FillFrequency> frequencyTypeList;
	List<FillDesignation> desigList;
	List<FillPayMode> payModeList;
	
	public String getPayrollPolicyId() {
		return payrollPolicyId;
	}

	public void setPayrollPolicyId(String payrollPolicyId) {
		this.payrollPolicyId = payrollPolicyId;
	}

	public String getFrequencyType() {
		return frequencyType;
	}

	public void setFrequencyType(String frequencyType) {
		this.frequencyType = frequencyType;
	}

	public String getPayDay() {
		return payDay;
	}

	public void setPayDay(String payDay) {
		this.payDay = payDay;
	}

	public String getDesignation() {
		return designation;
	}

	public void setDesignation(String designation) {
		this.designation = designation;
	}

	public String getPayMode() {
		return payMode;
	}

	public void setPayMode(String payMode) {
		this.payMode = payMode;
	}

	public String getFxdAmount() {
		return fxdAmount;
	}

	public void setFxdAmount(String fxdAmount) {
		this.fxdAmount = fxdAmount;
	}

	public String getMonAmount() {
		return monAmount;
	}

	public void setMonAmount(String monAmount) {
		this.monAmount = monAmount;
	}

	public String getTuesAmount() {
		return tuesAmount;
	}

	public void setTuesAmount(String tuesAmount) {
		this.tuesAmount = tuesAmount;
	}

	public String getWedAmount() {
		return wedAmount;
	}

	public void setWedAmount(String wedAmount) {
		this.wedAmount = wedAmount;
	}

	public String getThursAmount() {
		return thursAmount;
	}

	public void setThursAmount(String thursAmount) {
		this.thursAmount = thursAmount;
	}

	public String getFriAmount() {
		return friAmount;
	}

	public void setFriAmount(String friAmount) {
		this.friAmount = friAmount;
	}

	public String getSatAmount() {
		return satAmount;
	}

	public void setSatAmount(String satAmount) {
		this.satAmount = satAmount;
	}

	public String getSunAmount() {
		return sunAmount;
	}

	public void setSunAmount(String sunAmount) {
		this.sunAmount = sunAmount;
	}
	public List<FillFrequency> getFrequencyTypeList() {
		return frequencyTypeList;
	}

	public List<FillDesignation> getDesigList() {
		return desigList;
	}
	
	public List<FillPayMode> getPayModeList() {
		return payModeList;
	}
	
	public String getEmpType() {
		return empType;
	}

	public void setEmpType(String empType) {
		this.empType = empType;
	}

	public List<FillEmploymentType> getEmpTypeList() {
		return empTypeList;
	}
	
	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public List<FillServices> getServiceList() {
		return serviceList;
	}
	public String getHolidayLoading() {
		return holidayLoading;
	}

	public void setHolidayLoading(String holidayLoading) {
		this.holidayLoading = holidayLoading;
	}
	
	private HttpServletRequest request;
	

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}


	public String getRedirectUrl() {
		return redirectUrl;
	}


	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}


	public String getStrP() {
		return strP;
	}


	public void setStrP(String strP) {
		this.strP = strP;
	}


	public String getMonHolidayLoading() {
		return monHolidayLoading;
	}


	public void setMonHolidayLoading(String monHolidayLoading) {
		this.monHolidayLoading = monHolidayLoading;
	}


	public String getTueHolidayLoading() {
		return tueHolidayLoading;
	}


	public void setTueHolidayLoading(String tueHolidayLoading) {
		this.tueHolidayLoading = tueHolidayLoading;
	}


	public String getWedHolidayLoading() {
		return wedHolidayLoading;
	}


	public void setWedHolidayLoading(String wedHolidayLoading) {
		this.wedHolidayLoading = wedHolidayLoading;
	}


	public String getThursHolidayLoading() {
		return thursHolidayLoading;
	}


	public void setThursHolidayLoading(String thursHolidayLoading) {
		this.thursHolidayLoading = thursHolidayLoading;
	}


	public String getFriHolidayLoading() {
		return friHolidayLoading;
	}


	public void setFriHolidayLoading(String friHolidayLoading) {
		this.friHolidayLoading = friHolidayLoading;
	}


	public String getSatHolidayLoading() {
		return satHolidayLoading;
	}


	public void setSatHolidayLoading(String satHolidayLoading) {
		this.satHolidayLoading = satHolidayLoading;
	}


	public String getSunHolidayLoading() {
		return sunHolidayLoading;
	}


	public void setSunHolidayLoading(String sunHolidayLoading) {
		this.sunHolidayLoading = sunHolidayLoading;
	}
	
}
