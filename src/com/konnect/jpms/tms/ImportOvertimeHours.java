package com.konnect.jpms.tms;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IConstants;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class ImportOvertimeHours extends ActionSupport implements ServletRequestAware, IConstants,IStatements {
	private static final long serialVersionUID = 1L;
	
	HttpServletRequest request;
	private File fileUpload1;
	private File fileUpload2;
	
	private String TRUE="true";
	private String FALSE="false";
	private String SUCCESS="success";
	private String FAILURE="failure";
	
	CommonFunctions CF;
	UtilityFunctions uF;
	HttpSession session;

	
	public String execute(){
		
		session = request.getSession();
		uF = new UtilityFunctions();
		CF  = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null){
			return LOGIN;
		}
		
		

		/*boolean isView  = CF.getAccess(session, request, uF);
		if(!isView){
			request.setAttribute(PAGE, PAccessDenied);
			request.setAttribute(TITLE, TAccessDenied);
			return ACCESS_DENIED;
		}*/
		
		 
		request.setAttribute(PAGE, "/jsp/tms/ImportOvertimeHours.jsp");
		request.setAttribute(TITLE, "Import Overtime Hours");
		
		
		
		String status = FALSE;
		if(fileUpload1 != null){
			System.out.println("Fully Qualified Name of The Excel File =="+fileUpload1);
//			status = insertAttendanceDetails(fileUpload1);
			status = format1Attendance(fileUpload1);
			//status = "true";
		}
		
		
		if(status.equals(TRUE))
			return SUCCESS;
		else
			return SUCCESS;
	}
	
	private String format1Attendance(File path) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs =null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		
			
		try {
			con = db.makeConnection(con);
			
		
			Map hmEmpLevel = CF.getEmpLevelMap(con);
			
			java.sql.Time _fromTime=null;
			java.sql.Time _toTime=null;
			
			FileInputStream fis = new FileInputStream(path);
			XSSFWorkbook workbook = new XSSFWorkbook(fis);
			System.out.println("Start Reading Excelsheet.... ");
			XSSFSheet attendanceSheet = workbook.getSheetAt(0);

			List<String> dateList = new ArrayList<String>();
			List<List<String>> outerList=new ArrayList<List<String>>();
			
			Iterator rows = attendanceSheet.rowIterator();
			int l=0;
			while (rows.hasNext()) {

				XSSFRow row = (XSSFRow) rows.next();
				Iterator cells = row.cellIterator();
				if(l==0){
					while (cells.hasNext()) {
						String cell =  cells.next().toString();
							dateList.add(cell);
					}
					l++;
					continue;
				}
				List<String> cellList = new ArrayList<String>();
				while (cells.hasNext()) {
					cellList.add(cells.next().toString());
				}
				outerList.add(cellList);

			}


			

			for (int k=0;k<outerList.size();k++) {
				List<String> innerList=outerList.get(k);
				String empcode=innerList.get(1);
				System.out.println("empcode==="+empcode);
				int emp_per_id = 0;
				int servic_id = 0;
				double actual_hours = 0.0;
				String orgId=null;
				
				if (empcode.contains(".")) {
					empcode = empcode.substring(0, empcode.indexOf("."));
					// System.out.println("emp code case 6====>??"+empcode);
				}

				// Select Employ ID
				pst = con.prepareStatement("Select emp_per_id from employee_personal_details where empcode=?");
				pst.setString(1, empcode);
				 rs = pst.executeQuery();
				while (rs.next()) {
					emp_per_id = rs.getInt(1);
				}
				rs.close();
				pst.close();
				System.out.println("emp_per_id==="+emp_per_id);
				
				if(emp_per_id==0){
					continue;
				}
				
				for(int j=2;j<innerList.size();j++){
					
					String dataType=innerList.get(j);
					System.out.println("dataType====>"+dataType+"=====");
					//dateList.get(j)
					
					Date sqlOvertimeDate=getDate(dateList.get(j));
					String strOvertimeDate=uF.getDateFormat(""+sqlOvertimeDate, DBDATE, DATE_FORMAT);
					String[] strPaycycle=CF.getCurrentPayCycle(con, CF.getStrTimeZone(), uF.getDateFormatUtil(strOvertimeDate, DATE_FORMAT), CF);
					System.out.println("strPaycycle====>"+strPaycycle.length);
					System.out.println("dateList.get(j)====>"+dateList.get(j));
					
					pst = con.prepareStatement("insert into overtime_hours (emp_id,approved_ot_hours,approved_by,approve_date,paycle," +
							"paycycle_from,paycycle_to,_date) values(?,?,?,?,?,?,?,?)");
					pst.setInt(1, emp_per_id);
					pst.setDouble(2, uF.parseToDouble(dataType));
					pst.setInt(3, 22);
					pst.setDate(4, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setInt(5, uF.parseToInt(strPaycycle[2]));
					pst.setDate(6, uF.getDateFormat(strPaycycle[0], DATE_FORMAT));
					pst.setDate(7, uF.getDateFormat(strPaycycle[1], DATE_FORMAT));
					pst.setDate(8, getDate(dateList.get(j)));
					int x = pst.executeUpdate();
					pst.close();
					
				}
				
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
			return FALSE;
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return TRUE;		 
	}
	
	private boolean checkLeaveBalance(Connection con, PreparedStatement pst,
			ResultSet rs, int emp_per_id, UtilityFunctions uF) {
		boolean flag=false;
		try{
			
			pst = con.prepareStatement("select * from leave_register1 lr,(select max(_date) as _date, leave_type_id, emp_id from leave_register1 where " +
					"to_date(_date::text,'yyyy-MM-dd')<= ? and emp_id = ?  group by leave_type_id, emp_id ) lr1 where lr1._date= lr._date " +
					"and lr.emp_id = lr1.emp_id and lr.leave_type_id = lr1.leave_type_id and lr.emp_id =? limit 1 ");
			pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(2, emp_per_id);
			pst.setInt(3, emp_per_id);			
			rs= pst.executeQuery();
			double dblBalance = 0;  
			while(rs.next()){
				dblBalance = uF.parseToDouble(rs.getString("balance"));	
			}
			rs.close();
			pst.close();
			
			if(dblBalance>0){
				flag=true;
			}
	
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs !=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return flag;
	}

	private void insertLeaveBalance(Connection con, PreparedStatement pst,
			ResultSet rs, int emp_per_id, int leaveTypeId, String strDate,
			String leave_type, String leave_id) {

		try{
			System.out.println("leave_type======>"+leave_type);
			System.out.println("leaveTypeId======>"+leaveTypeId);
			pst = con.prepareStatement("select is_compensate from emp_leave_entry where leave_id=?");
			pst.setInt(1, uF.parseToInt(leave_id));
			rs = pst.executeQuery();
			boolean iscompensate=false;
			while(rs.next()){
				iscompensate=uF.parseToBoolean(rs.getString("is_compensate"));
			}
			rs.close();
			pst.close();
			
			Map hmEmpLevel = CF.getEmpLevelMap(con);			
			
			pst = con.prepareStatement("select * from emp_leave_type where leave_type_id=? and  level_id = ? order by entrydate desc limit 1");
			pst.setInt(1, leaveTypeId);
			pst.setInt(2, uF.parseToInt((String)hmEmpLevel.get(""+emp_per_id)));
			rs = pst.executeQuery();
			boolean isPaid = false;
			
			
			while(rs.next()){
				isPaid = rs.getBoolean("is_paid");
			}
			rs.close();
			pst.close();
			
			if(isPaid){
				pst = con.prepareStatement("UPDATE emp_leave_entry SET ispaid=true WHERE leave_id=?");
				pst.setInt(1, uF.parseToInt(leave_id));
				pst.execute();
				pst.close();
			}
	
			
			
			pst = con.prepareStatement("select * from leave_register1 where emp_id = ? and leave_type_id=? and _date=? ");
			pst.setInt(1, emp_per_id);
			pst.setInt(2, leaveTypeId);
			pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
			rs = pst.executeQuery(); 
			double dblTakenLeaves = 0;
			while(rs.next()){
				dblTakenLeaves = rs.getDouble("taken_paid") + rs.getDouble("taken_unpaid");
			}
			rs.close();
			pst.close();
			
			
	
			pst = con.prepareStatement("select * from leave_register1 lr,(select max(_date) as _date, leave_type_id, emp_id from leave_register1 where _date<= ? and emp_id = ? and leave_type_id=? group by leave_type_id, emp_id ) lr1 where lr1._date= lr._date and lr.emp_id = lr1.emp_id and lr.leave_type_id = lr1.leave_type_id  and lr.emp_id = ? and lr.leave_type_id=?");
			pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(2, emp_per_id);
			pst.setInt(3, leaveTypeId);
			pst.setInt(4, emp_per_id);
			pst.setInt(5, leaveTypeId);
			
			rs= pst.executeQuery();
			double dblTakenPaid = 0;
			double dblTakenUnPaid = 0;
			double dblBalance = 0;
			double dblAccrued = 0;
			double dblTotalBalance = 0;  
			while(rs.next()){
				dblBalance = uF.parseToDouble(rs.getString("balance"));
	//			dblTakenPaid = uF.parseToDouble(rs.getString("taken_paid"));
	//			dblTakenUnPaid = uF.parseToDouble(rs.getString("taken_unpaid"));
	//			dblAccrued = uF.parseToDouble(rs.getString("accrued"));
			}
			rs.close();
			pst.close();
	
			
			
				// Needs to add a condition if manager approves leaves other the dates for which employee has applied.
				
				pst = con.prepareStatement("update leave_register set taken_leaves=? where emp_id = ? and leave_type_id=? and from_date<=? and to_date>=? ");
				if(leave_type.trim().equalsIgnoreCase("HD") || leave_type.trim().equalsIgnoreCase("HD/EL") || leave_type.trim().equalsIgnoreCase("PL/HD")){
					pst.setDouble(1, (dblTakenLeaves + 0.5));
				}else{
					pst.setDouble(1, (dblTakenLeaves + 1));
				}
				
				pst.setInt(2, emp_per_id);
				pst.setInt(3, leaveTypeId);
				pst.setDate(4, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.execute();
				pst.close();
				
				
				double dblLeavesApproved = 0;
				double dblLeavesPaid = 0;
				double dblLeavesUnPaid = 0;
				
				if(leave_type.trim().equalsIgnoreCase("HD") || leave_type.trim().equalsIgnoreCase("PL/HD")){
					dblLeavesApproved = 0.5;
				}else{
					dblLeavesApproved = 1;
				}
				if(dblBalance>=dblLeavesApproved){
					dblLeavesPaid =  dblLeavesApproved + dblTakenPaid;
					dblLeavesUnPaid =  dblTakenUnPaid; 
					dblTotalBalance = dblBalance - dblLeavesApproved;
				}else{
					dblLeavesPaid =  dblBalance + dblTakenPaid ;
					dblLeavesUnPaid =  dblLeavesApproved - dblBalance + dblTakenUnPaid;
					dblTotalBalance = 0;
				}
				
				dblLeavesApproved = 0;
				if(leave_type.trim().equalsIgnoreCase("HD") || leave_type.trim().equalsIgnoreCase("PL/HD")){
					dblLeavesApproved = 0.5;
				}else{
					dblLeavesApproved = 1;
				}
				  
				
				Calendar cal = GregorianCalendar.getInstance();
				cal.set(Calendar.DATE, uF.parseToInt(uF.getDateFormat(strDate, DATE_FORMAT, "dd")));
				cal.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(strDate, DATE_FORMAT, "MM")) - 1);
				cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strDate, DATE_FORMAT, "yyyy")));
				
				
				double dblCount=0;
				boolean isPaid1= false;
				double dblLeaveDed = 0;
				double dblBalance1 = dblBalance;
				for(int i=0; i<dblLeavesApproved; i++){
					
					
					Date dtCurrent = uF.getDateFormat(cal.get(Calendar.DATE)+"/"+(cal.get(Calendar.MONTH) + 1)+"/"+cal.get(Calendar.YEAR), DATE_FORMAT);
					
					if(leave_type.trim().equalsIgnoreCase("HD")|| leave_type.trim().equalsIgnoreCase("PL/HD")){
						dblCount+=0.5;
					}else{
						
						if((dblBalance-dblCount)==0.5){
							dblCount+=0.5;
						}else{
							dblCount++;
						}
						
					}
					
					
					
					if(dblBalance>=dblCount && isPaid){
						isPaid1 = true;
						
						if(leave_type.trim().equalsIgnoreCase("HD") || leave_type.trim().equalsIgnoreCase("PL/HD")){
							dblBalance1 -= 0.5;
							dblLeaveDed = 0.5;
						}else{
							
							if(dblBalance1>=1){
								dblBalance1 -= 1;
								dblLeaveDed = 1;
							}else if(dblBalance1>=0.5){
								dblBalance1 -= 0.5;
								dblLeaveDed = 0.5;
							}else{
								dblLeaveDed = 0;
							}
						}
					}else{
						isPaid1 = false;
						if(leave_type.trim().equalsIgnoreCase("HD")|| leave_type.trim().equalsIgnoreCase("PL/HD")){
							dblLeaveDed = 0.5;
						}else{
							dblLeaveDed = 1;
						}
						
					}
					
					
					pst = con.prepareStatement("insert into leave_application_register (_date, emp_id, leave_type_id, leave_id, leave_no, is_paid, balance, _type) values (?,?,?,?,?,?,?,?)");
					pst.setDate(1, dtCurrent);
					pst.setInt(2, emp_per_id);
					pst.setInt(3, leaveTypeId);
					pst.setDouble(4, uF.parseToInt(leave_id));
					pst.setDouble(5, dblLeaveDed);
					pst.setBoolean(6, isPaid1);
					pst.setDouble(7, dblBalance1);
					pst.setBoolean(8, true);
					System.out.println("pst=======>"+pst);
					pst.execute();
					pst.close();
					
					cal.add(Calendar.DATE, 1);
				}
	
				
				CF.updateLeaveRegister1(con, CF, uF, dblLeavesApproved, 0, ""+leaveTypeId, ""+emp_per_id);
				
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs !=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

		// To get the leave type id passing with leave type code.
		public int getLeaveTypeId(Connection con, PreparedStatement pst, String leaveType,int emp_id){
			int leaveTypeId = 0;
			Map<String, String> hmEmpLevelMap =CF.getEmpLevelMap(con);
			
			Map<String, String> hmEmpWlocationMap =new HashMap<String, String>();
			Map<String, String> hmEmpOrgMap =new HashMap<String, String>();
			
			CF.getEmpWlocationMap(con, null, hmEmpWlocationMap, null, hmEmpOrgMap);
			ResultSet rs = null;
			try{		
				//pst = con.prepareStatement("Select leave_type_id from leave_type where leave_type_code=?");
				pst=con.prepareStatement("Select lt.leave_type_id from leave_type lt join emp_leave_type elt on elt.leave_type_id=lt.leave_type_id " +
						" and level_id=? and wlocation_id=? and lt.org_id=? where leave_type_code=?");
				pst.setInt(1, uF.parseToInt(hmEmpLevelMap.get(""+emp_id)));
				pst.setInt(2, uF.parseToInt(hmEmpWlocationMap.get(""+emp_id)));
				pst.setInt(3, uF.parseToInt(hmEmpOrgMap.get(""+emp_id)));
				pst.setString(4, leaveType);
				rs = pst.executeQuery();
				if(rs.next()) {
					leaveTypeId = rs.getInt(1);
				}
				rs.close();
				pst.close();
				
			}catch(Exception e){
				e.printStackTrace();
//				System.out.println("Error occured in getLeaveTypeId function");
				return leaveTypeId;			
			} finally {
				if(rs !=null){
					try {
						rs.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
				if(pst !=null){
					try {
						pst.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}		
			return leaveTypeId;
		}
	
	public static java.sql.Date getDate(String s) throws Exception {
		java.sql.Date sqlToday = null;

		try {
			if (s.contains("/")) {
				DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
				sqlToday = new java.sql.Date(df.parse(s).getTime());
			}
			if (s.contains("-")) {
				DateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
				sqlToday = new java.sql.Date(df.parse(s).getTime());
			}
			if (s.contains(".")) {
				DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
				sqlToday = new java.sql.Date(df.parse(s).getTime());
			}
			
			return sqlToday;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return sqlToday;
	}
	
	public static java.sql.Timestamp getTimeStamp(String cell,
			java.sql.Time time) {
		Timestamp timeStamp = null;
		try {

			String ts = cell+ " " + time.toString();
			DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			timeStamp = new java.sql.Timestamp(df.parse(ts).getTime());
			return timeStamp;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return timeStamp;
	}
	

	public File getFileUpload1() {
		return fileUpload1;
	}
	public void setFileUpload1(File fileUpload1) {
		System.out.println("Setting the file Upload");
		this.fileUpload1 = fileUpload1;
	}
	@Override
	public void setServletRequest(HttpServletRequest request) {
		System.out.println("Request Received");
		this.request = request;		
	}

	public File getFileUpload2() {
		return fileUpload2;
	}

	public void setFileUpload2(File fileUpload2) {
		this.fileUpload2 = fileUpload2;
	}
	
}
