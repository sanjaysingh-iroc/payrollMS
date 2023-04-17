package com.konnect.jpms.policies;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

public class AddHolidays extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	CommonFunctions CF; 
	List<FillDepartment> deptList;
	List<FillWLocation> wLocationList;
	String orgId;
	String submit;
	String strWlocation;
	String operation;
	String calendarYear;
	
	private static Logger log = Logger.getLogger(AddHolidays.class);
	
	private String holidayId;
	private String holidayDate;
	private String holidayDesc;
	private String deptName;
	private String wLocationName;
	private String colourCode;
	private String holidayType;
	String defaultHolidayType;
	
	String type;
	
	String userscreen;
	String navigationId;
	String toPage;
	
	public String execute() {
		
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		request.setAttribute(PAGE, PAddHolidays);
		
		//request.setAttribute(PAGE, PAddHolidays);
		request.setAttribute(TITLE, TAddHolidays);

		wLocationList = new FillWLocation(request).fillWLocation(getOrgId());
		
		if(operation.equals("A")){
			if(getSubmit()!=null){
				return insertHolidays();
			}
			
		} else if (operation.equals("U")){
			getHolidayDetails();
			if(getSubmit()!=null){
				return updateHolidays();
			}
		} else if (operation.equals("D")) {
			return deleteHolidays();
		}
		setDefaultHolidayType("FD");
		return LOAD;		
	}

	private void getHolidayDetails() {
		
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			Map<String,String> hmHoliday = new HashMap<String, String>();
			pst = con.prepareStatement("SELECT * FROM holidays where holiday_id =? ");
			pst.setInt(1, uF.parseToInt(getHolidayId()));
			rs = pst.executeQuery();
			while(rs.next()) {				
				hmHoliday.put("HOLIDAY_ID",rs.getString("holiday_id"));
				hmHoliday.put("HOLIDAY_DATE",uF.getDateFormat(rs.getString("_date"), DBDATE, DATE_FORMAT));
				hmHoliday.put("HOLIDAY_YEAR",rs.getString("_year"));
				hmHoliday.put("HOLIDAY_DESCRIPTION",rs.getString("description"));
				hmHoliday.put("COLOUR_CODE",rs.getString("colour_code"));
				hmHoliday.put("WLOCATION_ID",rs.getString("wlocation_id"));
				hmHoliday.put("ORG_ID",rs.getString("org_id"));
				hmHoliday.put("HOLIDAY_TYPE",rs.getString("holiday_type"));
				setDefaultHolidayType(rs.getString("holiday_type")!=null ? rs.getString("holiday_type") : "FD");
				
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmHoliday", hmHoliday);
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
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

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {

			con = db.makeConnection(con);
			
	//===start parvez date: 01-10-2022===		
			String strOrgId = null;
			String strLocationId = null;
			String strDate = null;
			pst = con.prepareStatement("select * from holidays where holiday_id=?");
			pst.setInt(1, uF.parseToInt(getHolidayId()));
			rs = pst.executeQuery();
			while(rs.next()){
				strOrgId = rs.getString("org_id");
				strLocationId = rs.getString("wlocation_id");
				strDate = uF.getDateFormat(rs.getString("_date"), DBDATE, DATE_FORMAT);
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from employee_personal_details epd, employee_official_details eod where epd.emp_per_id=eod.emp_id and " +
					" is_alive = true and emp_per_id>0 and eod.org_id=? and eod.wlocation_id=?");
			pst.setInt(1, uF.parseToInt(strOrgId));
			pst.setInt(2, uF.parseToInt(strLocationId));
			rs = pst.executeQuery();
			List<String> alEmp = new ArrayList<String>();
			Map<String, String> hmEmpJoiningDate = new HashMap<String, String>();
			while (rs.next()) {
				alEmp.add(rs.getString("emp_id"));
				hmEmpJoiningDate.put(rs.getString("emp_id"), uF.getDateFormat(rs.getString("joining_date"), DBDATE, DATE_FORMAT));
			}
			rs.close();
			pst.close();
			
			for (int j = 0; alEmp != null && j < alEmp.size(); j++) {
				String strEmpId = alEmp.get(j);
				String nLevelId = CF.getEmpLevelId(con, strEmpId);
				
				String strLeaveTypeId = null;
				pst = con.prepareStatement("select * from leave_type lt, emp_leave_type elt where lt.leave_type_id = elt.leave_type_id and " +
						" elt.org_id=? and elt.wlocation_id=? and elt.level_id=? and lt.is_align_weekend_holiday=true");
				pst.setInt(1, uF.parseToInt(strOrgId));
				pst.setInt(2, uF.parseToInt(strLocationId));
				pst.setInt(3, uF.parseToInt(nLevelId));
				rs = pst.executeQuery();
				while(rs.next()){
					strLeaveTypeId = rs.getString("leave_type_id");
				}
				rs.close();
				pst.close();
				
				
				boolean isEmpRosterWeekOff = CF.checkEmpRosterWeeklyOff(con, CF, uF, strEmpId, getHolidayDate(), nLevelId, strLocationId, strOrgId);
				
				if(isEmpRosterWeekOff){
					pst = con.prepareStatement("delete from leave_register1 where emp_id=? and align_holiday_weekend_date = ?");
					pst.setInt(1, uF.parseToInt(strEmpId));
					pst.setDate(2, uF.getDateFormat(strDate, DATE_FORMAT));
					pst.execute();
					pst.close();
					
					boolean avilableFlag = false;
					pst = con.prepareStatement("select leave_type_id, balance from leave_register1 where register_id in (select max(register_id) from leave_register1 " +
										"where emp_id=? and _type='C' and leave_type_id=? group by emp_id,leave_type_id)");
					pst.setInt(1, uF.parseToInt(strEmpId));
					pst.setInt(2, uF.parseToInt(strLeaveTypeId));
					rs = pst.executeQuery();
					while (rs.next()) {
						avilableFlag = true;
					}
					rs.close();
		            pst.close();
		            if(!avilableFlag){
		            	pst = con.prepareStatement("insert into leave_register1 (taken_paid, taken_unpaid, accrued, emp_id,leave_type_id," +
								" _date,update_balance,_type,balance,compensate_id) values (?,?,?,?, ?,?,?,?, ?,?)");
		            	
		            	pst.setDouble(1, 0);
						pst.setDouble(2, 0);
						pst.setDouble(3, 0);
						pst.setInt(4, uF.parseToInt(strEmpId));
						pst.setInt(5, uF.parseToInt(strLeaveTypeId));
						pst.setDate(6, uF.getDateFormat(hmEmpJoiningDate.get(strEmpId), DATE_FORMAT));
						pst.setInt(7, 0);
						pst.setString(8, "C");
						pst.setDouble(9, 0.0d);
						pst.setInt(10, 0);
						System.out.println("AH/231--pst="+pst);
						pst.executeUpdate();
						pst.close();
		            }
					
					pst = con.prepareStatement("insert into leave_register1 (taken_paid, taken_unpaid, accrued, emp_id,leave_type_id," +
							" _date,update_balance,_type,balance,compensate_id,align_holiday_weekend_date) values (?,?,?,?, ?,?,?,?, ?,?,?)");
					pst.setDouble(1, 0);
					pst.setDouble(2, 0);
					pst.setDouble(3, 1);
					pst.setInt(4, uF.parseToInt(strEmpId));
					pst.setInt(5, uF.parseToInt(strLeaveTypeId));
					pst.setDate(6, uF.getDateFormat(getHolidayDate(), DATE_FORMAT));
					pst.setInt(7, 0);
					pst.setString(8, "A");
					pst.setDouble(9, 0.0d);
					pst.setInt(10, 0);
					pst.setDate(11, uF.getDateFormat(getHolidayDate(), DATE_FORMAT));
//					System.out.println("AH/216--pst="+pst);
					pst.executeUpdate();
					pst.close();
				}
				
			}
	//===end parvez date: 01-10-2022===		
			
			pst = con.prepareStatement("update holidays set _date=?, _year=?, description=?, colour_code=?,holiday_type=?,is_optional_holiday=? " +
					"where holiday_id=?");
			pst.setDate(1, uF.getDateFormat(getHolidayDate(), DATE_FORMAT));
			pst.setInt(2, uF.parseToInt(uF.getDateFormat(getHolidayDate(), DATE_FORMAT, "yyyy")));
			pst.setString(3, getHolidayDesc());
			pst.setString(4, getColourCode());
			pst.setString(5, getHolidayType());
			pst.setBoolean(6, getType()!=null && getType().trim().equalsIgnoreCase("O") ? true : false);
			pst.setInt(7, uF.parseToInt(getHolidayId()));
			pst.execute();
			pst.close();
			
			request.setAttribute(MESSAGE, "Holiday updated successfully!");
			

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
		return "view";

	}
	
	public String insertHolidays() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {

			con = db.makeConnection(con);
			
			if(getWLocationName()!=null){
				String []arr = getWLocationName().split(",");
				for(int i=0; i<arr.length; i++){
					boolean flag= false;
					pst = con.prepareStatement("select * from holidays where _date=? and org_id=? and wlocation_id=?");
					pst.setDate(1, uF.getDateFormat(getHolidayDate(), DATE_FORMAT));
					pst.setInt(2, uF.parseToInt(getOrgId()));
					pst.setInt(3, uF.parseToInt(arr[i]));
					rs = pst.executeQuery();
					while(rs.next()){
						flag = true;
					}
					rs.close();
					pst.close();
					
					if(!flag){
						pst=con.prepareStatement("INSERT INTO holidays (_date, _year, description, wlocation_id, colour_code, org_id,holiday_type," +
								"is_optional_holiday) VALUES (?,?,?,?, ?,?,?,?)");
						pst.setDate(1, uF.getDateFormat(getHolidayDate(), DATE_FORMAT));
						pst.setInt(2, uF.parseToInt(uF.getDateFormat(getHolidayDate(), DATE_FORMAT, "yyyy")));
						pst.setString(3, getHolidayDesc());
						pst.setInt(4, uF.parseToInt(arr[i]));
						pst.setString(5, getColourCode());
						pst.setInt(6, uF.parseToInt(getOrgId()));
						pst.setString(7, getHolidayType());
						pst.setBoolean(8, getType()!=null && getType().trim().equalsIgnoreCase("O") ? true : false);
						pst.execute();
						pst.close();
					}
					
			//===start parvez date: 30-09-2022===
					pst = con.prepareStatement("select * from employee_personal_details epd, employee_official_details eod where epd.emp_per_id=eod.emp_id and " +
							" is_alive = true and emp_per_id>0 and eod.org_id=? and eod.wlocation_id=?");
					pst.setInt(1, uF.parseToInt(getOrgId()));
					pst.setInt(2, uF.parseToInt(arr[i]));
					rs = pst.executeQuery();
					List<String> alEmp = new ArrayList<String>();
					Map<String,String> hmEmpJoiningDate = new HashMap<String, String>();
					while (rs.next()) {
						alEmp.add(rs.getString("emp_id"));
						hmEmpJoiningDate.put(rs.getString("emp_id"), uF.getDateFormat(rs.getString("joining_date"), DBDATE, DATE_FORMAT));
					}
					rs.close();
					pst.close();
					
					for (int j = 0; alEmp != null && j < alEmp.size(); j++) {
						String strEmpId = alEmp.get(j);
						String nLevelId = CF.getEmpLevelId(con, strEmpId);
						
						String strLeaveTypeId = null;
						pst = con.prepareStatement("select * from leave_type lt, emp_leave_type elt where lt.leave_type_id = elt.leave_type_id and " +
								" elt.org_id=? and elt.wlocation_id=? and elt.level_id=? and lt.is_align_weekend_holiday=true");
						pst.setInt(1, uF.parseToInt(getOrgId()));
						pst.setInt(2, uF.parseToInt(arr[i]));
						pst.setInt(3, uF.parseToInt(nLevelId));
						rs = pst.executeQuery();
						while(rs.next()){
							strLeaveTypeId = rs.getString("leave_type_id");
						}
						rs.close();
						pst.close();
						
						boolean isEmpRosterWeekOff = CF.checkEmpRosterWeeklyOff(con, CF, uF, strEmpId, getHolidayDate(), nLevelId, ""+arr[i], ""+getOrgId());
						
						if(isEmpRosterWeekOff){
//							System.out.println("isHoliday===true");
							
							boolean avilableFlag = false;
							pst = con.prepareStatement("select leave_type_id, balance from leave_register1 where register_id in (select max(register_id) from leave_register1 " +
							"where emp_id=? and _type='C' and leave_type_id=? group by emp_id,leave_type_id)");
							pst.setInt(1, uF.parseToInt(strEmpId));
							pst.setInt(2, uF.parseToInt(strLeaveTypeId));
							rs = pst.executeQuery();
							while (rs.next()) {
								avilableFlag = true;
							}
							rs.close();
				            pst.close();
				            if(!avilableFlag){
				            	pst = con.prepareStatement("insert into leave_register1 (taken_paid, taken_unpaid, accrued, emp_id,leave_type_id," +
										" _date,update_balance,_type,balance,compensate_id) values (?,?,?,?, ?,?,?,?, ?,?)");
				            	
				            	pst.setDouble(1, 0);
								pst.setDouble(2, 0);
								pst.setDouble(3, 0);
								pst.setInt(4, uF.parseToInt(strEmpId));
								pst.setInt(5, uF.parseToInt(strLeaveTypeId));
								pst.setDate(6, uF.getDateFormat(hmEmpJoiningDate.get(strEmpId), DATE_FORMAT));
								pst.setInt(7, 0);
								pst.setString(8, "C");
								pst.setDouble(9, 0.0d);
								pst.setInt(10, 0);
//								System.out.println("AH/342--pst="+pst);
								pst.executeUpdate();
								pst.close();
				            }
							
							pst = con.prepareStatement("insert into leave_register1 (taken_paid, taken_unpaid, accrued, emp_id,leave_type_id," +
									" _date,update_balance,_type,balance,compensate_id,align_holiday_weekend_date) values (?,?,?,?, ?,?,?,?, ?,?,?)");
							pst.setDouble(1, 0);
							pst.setDouble(2, 0);
							pst.setDouble(3, 1);
							pst.setInt(4, uF.parseToInt(strEmpId));
							pst.setInt(5, uF.parseToInt(strLeaveTypeId));
							pst.setDate(6, uF.getDateFormat(getHolidayDate(), DATE_FORMAT));
							pst.setInt(7, 0);
							pst.setString(8, "A");
							pst.setDouble(9, 0.0d);
							pst.setInt(10, 0);
							pst.setDate(11, uF.getDateFormat(getHolidayDate(), DATE_FORMAT));
//							System.out.println("AH/342--pst="+pst);
							pst.executeUpdate();
							pst.close();
						}
						
					}
			//===end parvez date: 30-09-2022===	

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
		return "view";

	}

	public String deleteHolidays() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {

			con = db.makeConnection(con);
			
	//===start parvez date: 01-10-2022===
			String strOrgId = null;
			String strLocationId = null;
			String strDate = null;
			pst = con.prepareStatement("select * from holidays where holiday_id=?");
			pst.setInt(1, uF.parseToInt(getHolidayId()));
			rs = pst.executeQuery();
			while(rs.next()){
				strOrgId = rs.getString("org_id");
				strLocationId = rs.getString("wlocation_id");
				strDate = uF.getDateFormat(rs.getString("_date"), DBDATE, DATE_FORMAT);
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from employee_personal_details epd, employee_official_details eod where epd.emp_per_id=eod.emp_id and " +
					" is_alive = true and emp_per_id>0 and eod.org_id=? and eod.wlocation_id=?");
			pst.setInt(1, uF.parseToInt(strOrgId));
			pst.setInt(2, uF.parseToInt(strLocationId));
			rs = pst.executeQuery();
			List<String> alEmp = new ArrayList<String>();
			while (rs.next()) {
				alEmp.add(rs.getString("emp_id"));
			}
			rs.close();
			pst.close();
			
			for (int j = 0; alEmp != null && j < alEmp.size(); j++) {
				String strEmpId = alEmp.get(j);
				
				pst = con.prepareStatement("delete from leave_register1 where emp_id=? and align_holiday_weekend_date = ?");
				pst.setInt(1, uF.parseToInt(strEmpId));
				pst.setDate(2, uF.getDateFormat(strDate, DATE_FORMAT));
//				System.out.println("AH/416---pst="+pst);
				pst.execute();
				pst.close();
			}
	
	//===end parvez date: 01-10-2022===		
			
			pst = con.prepareStatement(deleteHolidays);
			pst.setInt(1, uF.parseToInt(getHolidayId()));
			pst.execute();
			pst.close();
			request.setAttribute(MESSAGE, "Holiday deleted successfully!");

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}

		return "view";

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

	public String getHolidayType() {
		return holidayType;
	}

	public void setHolidayType(String holidayType) {
		this.holidayType = holidayType;
	}

	public String getSubmit() {
		return submit;
	}

	public void setSubmit(String submit) {
		this.submit = submit;
	}

	public String getStrWlocation() {
		return strWlocation;
	}

	public void setStrWlocation(String strWlocation) {
		this.strWlocation = strWlocation;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getDefaultHolidayType() {
		return defaultHolidayType;
	}

	public void setDefaultHolidayType(String defaultHolidayType) {
		this.defaultHolidayType = defaultHolidayType;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getUserscreen() {
		return userscreen;
	}

	public void setUserscreen(String userscreen) {
		this.userscreen = userscreen;
	}

	public String getNavigationId() {
		return navigationId;
	}

	public void setNavigationId(String navigationId) {
		this.navigationId = navigationId;
	}

	public String getToPage() {
		return toPage;
	}

	public void setToPage(String toPage) {
		this.toPage = toPage;
	}

	public String getCalendarYear() {
		return calendarYear;
	}

	public void setCalendarYear(String calendarYear) {
		this.calendarYear = calendarYear;
	}
	
}
