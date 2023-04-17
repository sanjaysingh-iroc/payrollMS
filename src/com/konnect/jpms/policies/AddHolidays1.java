package com.konnect.jpms.policies;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class AddHolidays1 extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	CommonFunctions CF; 
	List<FillDepartment> deptList;
	List<FillWLocation> wLocationList;
	String orgId;
	private static Logger log = Logger.getLogger(AddHolidays1.class);
	
	public String execute() {
		
		log.debug("AddHolidays: execute()"); 
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		request.setAttribute(PAGE, PAddHolidays1);
		String operation = request.getParameter("operation");
		 
		
//		System.out.println("===>"+getOrgId());
		
		
		if(operation.equals("A"))
		{
			return insertHolidays();
		}
		else if (operation.equals("U"))
		{
			return updateHolidays();
		}
		else if (operation.equals("D"))
		{
			return deleteHolidays();
		}
		
		
		
		
		return SUCCESS;
		
	}

	public String loadValidateHolidays() {
		request.setAttribute(PAGE, PAddHolidays);
		request.setAttribute(TITLE, TAddHolidays);

		return LOAD;
	}
	
	public void loadLists() {
		deptList =  new FillDepartment(request).fillDepartment();
		wLocationList = new FillWLocation(request).fillWLocation();
	}
	
	public String updateHolidays() {
		log.debug("inside update");
		log.debug("---------------"+Integer.parseInt(request.getParameter("columnId")));
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		setHolidayId(request.getParameter("id"));
		int columnId = Integer.parseInt(request.getParameter("columnId"));
		String columnName=null;
		
		switch(columnId) {
			case 0 : columnName = "_date"; break;
			case 1 : columnName = "day";	break;
			case 2 : columnName = "description"; break;
			case 3 : columnName = "wlocation_id"; break;
			case 4 : columnName = "colour_code"; break;
		}
		String updateHolidays = "UPDATE holidays SET "+columnName+"=? WHERE holiday_id=?";
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement(updateHolidays);
			if(columnId==0){
				pst.setDate(1, uF.getDateFormat(request.getParameter("value"), CF.getStrReportDateFormat()));
			}else if(columnId==2){
				pst.setString(1, uF.strEncoding(request.getParameter("value")));
			}else if (columnId==4){
				pst.setString(1, request.getParameter("value"));
			}else if (columnId==3){
				pst.setInt(1, uF.parseToInt((String)request.getParameter("value")));
			}
			
			if (columnId!=1){
				
				pst.setInt(2, uF.parseToInt(request.getParameter("id")));
				pst.execute();
				pst.close();
				
				
				log.debug("pst===>"+pst);
				
				
			}
			
			
			
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return UPDATE;
	}
	
	public String insertHolidays() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {

			con = db.makeConnection(con);
			
			if(getWLocationName()!=null){
				String []arr = getWLocationName().split(",");
				for(int i=0; i<arr.length; i++){
					pst = con.prepareStatement(insertHolidays);
					pst.setDate(1, uF.getDateFormat(getHolidayDate(), DATE_FORMAT));
					pst.setInt(2, uF.parseToInt(uF.getDateFormat(getHolidayDate(), DATE_FORMAT, "yyyy")));
					pst.setString(3, getHolidayDesc());
					pst.setInt(4, uF.parseToInt(arr[i]));
					pst.setString(5, getColourCode());
					pst.setInt(6, uF.parseToInt(getOrgId()));
					pst.execute();
					pst.close();

				}
			}
			
			
			
			request.setAttribute(MESSAGE, "Holiday added successfully!");

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		loadLists();
		log.debug("returning success after insertion..");
		return SUCCESS;

	}

	public String deleteHolidays() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement(deleteHolidays);
			pst.setInt(1, uF.parseToInt(request.getParameter("id")));
			pst.execute();
			pst.close();
			request.setAttribute(MESSAGE, "Holiday deleted successfully!");

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}

		return SUCCESS;

	}

	private String holidayId;
	private String holidayDate;
	private String holidayDesc;
	private String deptName;
	private String wLocationName;
	private String colourCode;

	public void validate() {
		if (getHolidayDate() != null && getHolidayDate().length() == 0) {
			addFieldError("holidayDate", "Date is required");
		}
		if (getHolidayDesc() != null && getHolidayDesc().length() == 0) {
			addFieldError("holidayDesc", "Description is required");
		}
		if (getColourCode() != null && getColourCode().length() == 0) {
			addFieldError("colourCode", "Please choose a colour");
		}
		loadValidateHolidays();
	}

	public String getHolidayId() {
		return holidayId;
	}

	public void setHolidayId(String holidayId) {
		this.holidayId = holidayId;
	}

	public String getHolidayDate() {
		return holidayDate;
	}

	public void setHolidayDate(String holidayDate) {
		this.holidayDate = holidayDate;
	}

	public String getHolidayDesc() {
		return holidayDesc;
	}

	public void setHolidayDesc(String holidayDesc) {
		this.holidayDesc = holidayDesc;
	}

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public String getColourCode() {
		return colourCode;
	}

	public void setColourCode(String colourCode) {
		this.colourCode = colourCode;
	}

	public String getDeptName() {
		return deptName;
	}

	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}

	public List<FillDepartment> getDeptList() {
		return deptList;
	}

	public void setDeptList(List<FillDepartment> deptList) {
		this.deptList = deptList;
	}

	public List<FillWLocation> getWLocationList() {
		return wLocationList;
	}

	public String getWLocationName() {
		return wLocationName;
	}

	public void setWLocationName(String wLocationName) {
		this.wLocationName = wLocationName;
	}

	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

	

}
