package com.konnect.jpms.location;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillBank;
import com.konnect.jpms.select.FillCity;
import com.konnect.jpms.select.FillCountry;
import com.konnect.jpms.select.FillCurrency;
import com.konnect.jpms.select.FillShiftBase;
import com.konnect.jpms.select.FillState;
import com.konnect.jpms.select.FillTimezones;
import com.konnect.jpms.select.FillWeekDays;
import com.konnect.jpms.select.FillWlocationType;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class AddWLocation extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	String strSessionEmpId = null;
	String strBaseUserType = null;
	
	CommonFunctions CF;
	HttpSession session;
	
	private String businessId;
	private String businessName;	
	private String businessCode;
	private String address;
	private String country;
	private String state; 
	private String city;
	private String email; 
	private String pincode;  
	private String contactNo;
	private String faxNo;
	
	private String officeBillingInfoStatus;
	private String billingAddress;
	private String billingCity;
	private String billingCountry;
	private String billingState;
	private String billingPincode;
	private String billingContactNo;
	private String billingFaxNo;
	private String billingEmail;
	
	private String tanNo;
	private String panNo; 
	private String bankName;
	private String bankAcctNbr;
	private String timezone;
	private String wlocationType;
	
	private String strStdTimeStart;
	private String strStdTimeEnd;
	private String strStdTimeStartHd1;
	private String strStdTimeEndHd1;
	private String strStdTimeStartHd2;
	private String strStdTimeEndHd2;
	private String strStdTimeStartHd3;
	private String strStdTimeEndHd3;	
	
	private String weeklyOff1; 
	private String weeklyOff2;
	private String weeklyOff3;
	private String weeklyOffType1; 
	private String weeklyOffType2;
	private String weeklyOffType3;
	private String[] weekno1;
	private String[] weekno2;
	private String[] weekno3;
	private boolean isMetro;
	private String strCurrency;
	
	private String strEccCode1;
	private String strEccCode2;
	private String strRegNo;
	private String strOrg;
	
	private List<FillCountry> countryList;
	private List<FillState> stateList;
	private List<FillCountry> billingCountryList;
	private List<FillState> billingStateList;
	private List<FillCity> cityList;
	private List<FillTimezones> timezoneList;
	private List<FillWlocationType> wlocationTypeList;
	private List<FillWeekDays> weeklyOffList;
	private List<FillWeekDays> weeklyOffList1;
	private List<FillWeekDays> weeklyOffTypeList;
	private List<FillBank> bankList;
	private List<FillCurrency> currencyList;
	private String strPTRegNo;
	private String citAddress;
	
	private String userscreen;
	private String navigationId;
	private String toPage;
	

	private String strLunchBreak;
	private String addBreakTime;
	private String shiftBase;
	private List<FillShiftBase> shiftBaseList;
	private String shiftBufferTime;
	
	private String strWLocationLatitude;
	private String strWLocationLongitude;
	private String strGeofenceDistance;
	
	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		request.setAttribute(PAGE, PAddWLocation);
		
		strSessionEmpId = (String) session.getAttribute(EMPID);
		strBaseUserType = (String) session.getAttribute(BASEUSERTYPE);
		
		UtilityFunctions uF = new UtilityFunctions();
		String operation = request.getParameter("operation");
		String strId = request.getParameter("ID");

		loadValidateWLocation();
		
		if (operation!=null && operation.equals("D")) {
			return deleteWLocation(uF, strId);
		} 
		if (operation!=null && operation.equals("E")) {
			return viewWLocation(uF, strId);
		}
		
		if (getBusinessId()!=null && getBusinessId().length()>0) {
			return updateWLocation(uF);
		}
		if (getBusinessCode()!=null && getBusinessCode().length()>0) { 
			return insertWLocation(uF);
		}
		return LOAD;
		
	}
	
	public String insertWLocation(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);

			int count = 0;
			pst = con.prepareStatement("select count(*) as count from work_location_info");
			rs = pst.executeQuery();
			count = 0;
			while(rs.next()){
				count = rs.getInt("count");
			}
			rs.close();
			pst.close();
//			System.out.println("location count ====>"+count);
			
			if(count>=uF.parseToInt(CF.getStrMaxLocations())){
				session.setAttribute(MESSAGE, ERRORM+"Your plan does not allow you to add locations more than "+count+END);
				return SUCCESS;
			}
			
			String[] machineName=request.getParameterValues("machineName");
			String[] machineSerial=request.getParameterValues("machineSerial");
			
			StringBuilder sb1=new StringBuilder();
			int j=0;
			for(int i=0;i<machineName.length;i++){
				if(machineSerial[i].length()==0 || machineName[i].length()==0){
					continue;
				}
				if(j==0){
					sb1.append(machineSerial[i]+"::"+machineName[i]);
					j++;
				}else{
					sb1.append(","+machineSerial[i]+"::"+machineName[i]);
				}
				
			}

			pst = con.prepareStatement("INSERT INTO work_location_info (wlocation_name, wlocation_city, wlocation_state_id, wlocation_country_id, " +
					"wlocation_pincode, wlocation_contactno, wlocation_faxno, wlocation_logo, wlocation_bank_id, wlocation_bank_acct_nbr, timezone_id, " +
					"wlocation_email, wloacation_code, wlocation_address, wlocation_type_id, wlocation_start_time, wlocation_end_time, ismetro, " +
					" org_id,biometric_info,wlocation_billing_address,wlocation_billing_city,wlocation_billing_country_id,wlocation_billing_state_id," +
					"wlocation_billing_pincode,wlocation_billing_contactno,wlocation_billing_faxno,wlocation_billing_email,lunch_break_deduct," +
					"is_break_time_policy,shift_base_type,shift_base_buffer_time,wlocation_lat,wlocation_long,geofence_distance) " +
					"VALUES (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?)");
			pst.setString(1, getBusinessName());
			pst.setString(2, getCity());
			pst.setInt(3, uF.parseToInt(getState()));
			pst.setInt(4, uF.parseToInt(getCountry()));
			pst.setString(5, getPincode());
			pst.setString(6, getContactNo());
			pst.setString(7, getFaxNo());
			pst.setString(8, "");
			pst.setInt(9, uF.parseToInt(getBankName()));
			pst.setString(10, getBankAcctNbr());
			pst.setInt(11, uF.parseToInt(getTimezone()));
			pst.setString(12, getEmail());
			pst.setString(13, getBusinessCode());
			pst.setString(14, getAddress());
			pst.setInt(15, uF.parseToInt(getWlocationType())); 
			pst.setTime(16, uF.getTimeFormat(getStrStdTimeStart(), TIME_FORMAT));
			pst.setTime(17, uF.getTimeFormat(getStrStdTimeEnd(), TIME_FORMAT));
			pst.setBoolean(18, getIsMetro());
			pst.setInt(19, uF.parseToInt(getStrOrg()));
			pst.setString(20, sb1.toString());
			pst.setString(21, getBillingAddress());
			pst.setString(22, getBillingCity());
			pst.setInt(23, uF.parseToInt(getBillingCountry()));
			pst.setInt(24, uF.parseToInt(getBillingState()));
			pst.setString(25, getBillingPincode());
			pst.setString(26, getBillingContactNo());
			pst.setString(27, getBillingFaxNo());
			pst.setString(28, getBillingEmail());
			pst.setDouble(29, uF.parseToDouble(getStrLunchBreak()));
			pst.setBoolean(30, uF.parseToBoolean(getAddBreakTime()));
			pst.setInt(31, uF.parseToInt(getShiftBase()));
			pst.setDouble(32, uF.parseToDouble(getShiftBufferTime()));
			pst.setDouble(33, uF.parseToDouble(getStrWLocationLatitude()));
			pst.setDouble(34, uF.parseToDouble(getStrWLocationLongitude()));
			pst.setDouble(35, uF.parseToDouble(getStrGeofenceDistance()));
			int x = pst.executeUpdate();
			pst.close();
			
			if(x > 0){
				pst = con.prepareStatement("select max(wlocation_id) as wlocation_id from work_location_info");
				rs = pst.executeQuery();
				int nWlocationId = 0;
				while(rs.next()){
					nWlocationId = rs.getInt("wlocation_id");
				}
				rs.close();
				pst.close();
				
				if(strBaseUserType != null && (strBaseUserType.equals(HRMANAGER) || strBaseUserType.equals(CEO) || strBaseUserType.equals(ACCOUNTANT))) {
					pst = con.prepareStatement("update user_details set wlocation_id_access = wlocation_id_access||'"+nWlocationId+"'||',' where emp_id=?");
					pst.setInt(1, uF.parseToInt(strSessionEmpId));
					pst.executeUpdate();
					pst.close();
					
					pst = con.prepareStatement("select wlocation_id_access from user_details where emp_id=?");
					pst.setInt(1, uF.parseToInt(strSessionEmpId));
					rs = pst.executeQuery();
					String strWLocationAccess = null;
					while(rs.next()){
						strWLocationAccess = rs.getString("wlocation_id_access");
						if(strWLocationAccess!=null && strWLocationAccess.lastIndexOf(",")>=0){
							strWLocationAccess = strWLocationAccess.substring(1, strWLocationAccess.length()-1);
						}
					}
					rs.close();
					pst.close();
					if(strWLocationAccess != null) {
						session.setAttribute(WLOCATION_ACCESS, strWLocationAccess);
					}
				}
				
				if(nWlocationId > 0 && uF.parseToInt(getStrOrg()) > 0){
					updateDefaultWorkflow(con,uF,nWlocationId,uF.parseToInt(getStrOrg()));
				}
			}

			session.setAttribute(MESSAGE, SUCCESSM+getBusinessName()+" saved successfully."+END);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

		return SUCCESS;
	}
	
	private void updateDefaultWorkflow(Connection con, UtilityFunctions uF, int nWlocationId, int nOrgId) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = con.prepareStatement("select * from work_flow_member where wlocation_id=? and org_id=? and is_default=true");
			pst.setInt(1, nWlocationId);
			pst.setInt(2, nOrgId);
			rs = pst.executeQuery();
			boolean flag = false;
			if(rs.next()){
				flag = true;
			}
			rs.close();
			pst.close();
			
			if(!flag){
				int group_id = 0;
				pst = con.prepareStatement("select max(group_id)as group_id from work_flow_member ");
				rs = pst.executeQuery();
				while (rs.next()) {
					group_id = rs.getInt("group_id");
				}
				rs.close();
				pst.close();
				
				group_id++;
				
				pst = con.prepareStatement("insert into work_flow_member(work_flow_mem,member_type,group_name,group_id,member_id,org_id," +
						"wlocation_id,is_default)values(?,?,?,?, ?,?,?,?)");
				pst.setString(1,ADMIN);
				pst.setInt(2,1);
				pst.setString(3, "Default Workflow");
				pst.setInt(4, group_id);
				pst.setInt(5, 1);
				pst.setInt(6, nOrgId);
				pst.setInt(7, nWlocationId);
				pst.setBoolean(8, true);
				int y = pst.executeUpdate();
				pst.close();
				
				if(y > 0){
					int nWorkFlowId = 0;
					pst = con.prepareStatement("select max(work_flow_member_id)as work_flow_member_id from work_flow_member ");
					rs = pst.executeQuery();
					while (rs.next()) {
						nWorkFlowId = rs.getInt("work_flow_member_id");
					}
					rs.close();
					pst.close();
					
					int policy_count = 0;
					pst = con.prepareStatement("select max(policy_count)as count from work_flow_policy ");
					rs = pst.executeQuery();
					while (rs.next()) {
						policy_count = rs.getInt("count");
					}
					rs.close();
					pst.close();
					
					policy_count++;
					
					pst = con.prepareStatement("insert into work_flow_policy(work_flow_member_id,member_position,policy_type,"
							+ " trial_status,added_by,added_date,policy_count,policy_name,effective_date,org_id,location_id,policy_status," +
									"group_id) values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?)");
					pst.setInt(1, nWorkFlowId);
					pst.setDouble(2, 1);
					pst.setString(3, "1");
					pst.setInt(4, 1);
					pst.setInt(5, uF.parseToInt(strSessionEmpId));
					pst.setDate(6, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setInt(7, policy_count);
					pst.setString(8, "Default Workflow Policy");
					pst.setDate(9, null);
					pst.setString(10, ""+nOrgId);
					pst.setString(11, ""+nWlocationId);
					pst.setInt(12,1);
					pst.setInt(13, group_id);
					pst.execute();
					pst.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs!=null){
				try {
					rs.close();
					rs = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst!=null){
				try {
					pst.close();
					pst = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public String updateWLocation(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		String[] machineName=request.getParameterValues("machineName");
		String[] machineSerial=request.getParameterValues("machineSerial");
		
		StringBuilder sb1=new StringBuilder();
		int j=0;
		for(int i=0;i<machineName.length;i++){
			if(machineSerial[i].length()==0 || machineName[i].length()==0){
				continue;
			}
			if(j==0){
				sb1.append(machineSerial[i]+"::"+machineName[i]);
				j++;
			}else{
				sb1.append(","+machineSerial[i]+"::"+machineName[i]);
			}
			
		}
		
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("update work_location_info set wlocation_name=?, wlocation_city=?, wlocation_state_id=?, wlocation_country_id=?, " +
				"wlocation_pincode=?, wlocation_contactno=?, wlocation_faxno=?, wlocation_logo=?, wlocation_bank_id=?, wlocation_bank_acct_nbr=?, timezone_id=?, " +
				"wlocation_email=?, wloacation_code=?, wlocation_address=?, wlocation_type_id=?, wlocation_start_time=?, wlocation_end_time=?, ismetro=?, " +
				"biometric_info=?,wlocation_billing_address=?,wlocation_billing_city=?,wlocation_billing_country_id=?,wlocation_billing_state_id=?," +
				"wlocation_billing_pincode=?,wlocation_billing_contactno=?,wlocation_billing_faxno=?,wlocation_billing_email=?,lunch_break_deduct=?," +
				"is_break_time_policy=?,shift_base_type=?,shift_base_buffer_time=?,wlocation_lat=?,wlocation_long=?,geofence_distance=? WHERE wlocation_id=?");
			pst.setString(1, getBusinessName());
			pst.setString(2, getCity());
			pst.setInt(3, uF.parseToInt(getState()));
			pst.setInt(4, uF.parseToInt(getCountry()));
			pst.setString(5, getPincode());
			pst.setString(6, getContactNo());
			pst.setString(7, getFaxNo());
			pst.setString(8, "");
			pst.setInt(9, uF.parseToInt(getBankName()));
			pst.setString(10, getBankAcctNbr());
			pst.setInt(11, uF.parseToInt(getTimezone()));
			pst.setString(12, getEmail());
			pst.setString(13, getBusinessCode());
			pst.setString(14, getAddress());
			pst.setInt(15, uF.parseToInt(getWlocationType())); 
			pst.setTime(16, uF.getTimeFormat(getStrStdTimeStart(), TIME_FORMAT));
			pst.setTime(17, uF.getTimeFormat(getStrStdTimeEnd(), TIME_FORMAT));
			pst.setBoolean(18, getIsMetro());
			pst.setString(19, sb1.toString());
			pst.setString(20, getBillingAddress());
			pst.setString(21, getBillingCity());
			pst.setInt(22, uF.parseToInt(getBillingCountry()));
			pst.setInt(23, uF.parseToInt(getBillingState()));
			pst.setString(24, getBillingPincode());
			pst.setString(25, getBillingContactNo());
			pst.setString(26, getBillingFaxNo());
			pst.setString(27, getBillingEmail());
			pst.setDouble(28, uF.parseToDouble(getStrLunchBreak()));
			pst.setBoolean(29, uF.parseToBoolean(getAddBreakTime()));
			pst.setInt(30, uF.parseToInt(getShiftBase()));
			pst.setDouble(31, uF.parseToDouble(getShiftBufferTime()));
			pst.setDouble(32, uF.parseToDouble(getStrWLocationLatitude()));
			pst.setDouble(33, uF.parseToDouble(getStrWLocationLongitude()));
			pst.setDouble(34, uF.parseToDouble(getStrGeofenceDistance()));
			pst.setInt(35, uF.parseToInt(getBusinessId()));
			pst.executeUpdate();
			pst.close();
			
			if(uF.parseToInt(getBusinessId()) > 0){
				int nOrgId = 0;
				pst = con.prepareStatement("Select org_id from work_location_info  WHERE wlocation_id=?");
				pst.setInt(1, uF.parseToInt(getBusinessId()));
				rs = pst.executeQuery();
				while (rs.next()) {
					nOrgId = rs.getInt("org_id");
				}
				rs.close();
				pst.close();		
				
				updateDefaultWorkflow(con,uF,uF.parseToInt(getBusinessId()),nOrgId);
			}
			
			session.setAttribute(MESSAGE, SUCCESSM+getBusinessName()+" updated successfully."+END);
			
		} catch (Exception e) {
			e.printStackTrace();			
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;
		
	}
	
	public String viewWLocation(UtilityFunctions uF, String strId) {

		List<String> machineName=new ArrayList<String>();
		List<String> machineSerial=new ArrayList<String>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("Select * from ( Select * from ( Select * from work_location_info  WHERE wlocation_id= ? ) ast left join state s on ast.wlocation_state_id = s.state_id ) aco left join country co on aco.wlocation_country_id = co.country_id");
			pst.setInt(1, uF.parseToInt(strId));
			rs = pst.executeQuery();
			
			while(rs.next()){
				setBusinessId(rs.getString("wlocation_id"));
				setBusinessCode(rs.getString("wloacation_code"));
				setBusinessName(rs.getString("wlocation_name"));
				
				setAddress(rs.getString("wlocation_address"));
				setCity(rs.getString("wlocation_city"));
				setState(rs.getString("state_id"));
				setCountry(rs.getString("wlocation_country_id"));
				setPincode(rs.getString("wlocation_pincode"));
				setContactNo(rs.getString("wlocation_contactno"));
				setFaxNo(rs.getString("wlocation_faxno"));
				setEmail(rs.getString("wlocation_email"));
				
				setBillingAddress(rs.getString("wlocation_billing_address"));
				setBillingCity(rs.getString("wlocation_billing_city"));
				setBillingState(rs.getString("wlocation_billing_state_id"));
				setBillingCountry(rs.getString("wlocation_billing_country_id"));
				setBillingPincode(rs.getString("wlocation_billing_pincode"));
				setBillingContactNo(rs.getString("wlocation_billing_contactno"));
				setBillingFaxNo(rs.getString("wlocation_billing_faxno"));
				setBillingEmail(rs.getString("wlocation_billing_email"));
				
				setTimezone(rs.getString("timezone_id"));
				
				setTanNo(rs.getString("wlocation_tan_no"));
				setPanNo(rs.getString("wlocation_pan_no"));

				setWlocationType(rs.getString("wlocation_type_id"));
				setStrCurrency(rs.getString("currency_id"));
				setStrStdTimeStart(uF.getDateFormat(rs.getString("wlocation_start_time"), DBTIME, TIME_FORMAT)); 
				setStrStdTimeEnd(uF.getDateFormat(rs.getString("wlocation_end_time"), DBTIME, TIME_FORMAT)); 
				setWeeklyOff1(rs.getString("wlocation_weeklyoff1"));
				setWeeklyOffType1(rs.getString("wlocation_weeklyofftype1"));
				setWeeklyOff2(rs.getString("wlocation_weeklyoff2"));
				setWeeklyOffType2(rs.getString("wlocation_weeklyofftype2"));
				setIsMetro(rs.getBoolean("ismetro"));
				
				setStrStdTimeStartHd1(uF.getDateFormat(rs.getString("wlocation_start_time_halfday"), DBTIME, TIME_FORMAT));
				setStrStdTimeEndHd1(uF.getDateFormat(rs.getString("wlocation_end_time_halfday"), DBTIME, TIME_FORMAT));
				setStrStdTimeStartHd2(uF.getDateFormat(rs.getString("wlocation_start_time_halfday1"), DBTIME, TIME_FORMAT));
				setStrStdTimeEndHd2(uF.getDateFormat(rs.getString("wlocation_end_time_halfday1"), DBTIME, TIME_FORMAT));
				setStrStdTimeStartHd3(uF.getDateFormat(rs.getString("wlocation_start_time_halfday2"), DBTIME, TIME_FORMAT));
				setStrStdTimeEndHd3(uF.getDateFormat(rs.getString("wlocation_end_time_halfday2"), DBTIME, TIME_FORMAT));
				
				setWeeklyOff3(rs.getString("wlocation_weeklyoff3"));
				setWeeklyOffType3(rs.getString("wlocation_weeklyofftype3"));
				
				setStrEccCode1(rs.getString("wlocation_ecc_code_1"));
				setStrEccCode2(rs.getString("wlocation_ecc_code_2"));
				setStrRegNo(rs.getString("wlocation_reg_no"));
				String machine=rs.getString("biometric_info");
				
				if(machine!=null && !machine.equals("")) {
					String[] a=machine.split(",");
					for(int i=0;i<a.length;i++) {
						String b=a[i];
						machineName.add(b.split("::")[1]);
						machineSerial.add(b.split("::")[0]);
					}
				}
				String []arr3 = null;
				if(rs.getString("wlocation_weeknos3")!=null && !rs.getString("wlocation_weeknos3").equals("")) {
					arr3 = rs.getString("wlocation_weeknos3").split(",");
					setWeekno3(arr3);
				}
				String []arr1 = null;
				if(rs.getString("wlocation_weeknos1")!=null && !rs.getString("wlocation_weeknos1").equals("")){
					arr1 = rs.getString("wlocation_weeknos1").split(",");
					setWeekno1(arr1);
				}
				String []arr2 = null;
				if(rs.getString("wlocation_weeknos2")!=null && !rs.getString("wlocation_weeknos2").equals("")){
					arr2 = rs.getString("wlocation_weeknos2").split(",");
					setWeekno2(arr2);
				}
				
				setStrPTRegNo(rs.getString("wlocation_ptreg_no"));
				
				setCitAddress(rs.getString("wlocation_cit_address"));
				
//				,lunch_break_deduct,is_break_time_policy
				double dblHr =0.00d;
				dblHr = uF.parseToDouble(uF.formatIntoTwoDecimal(rs.getDouble("lunch_break_deduct")));
				String strHr = ""+uF.convertInHoursMins(dblHr);
				setStrLunchBreak(strHr);
				setAddBreakTime(""+uF.parseToBoolean(rs.getString("is_break_time_policy")));
				
				setShiftBase(rs.getString("shift_base_type"));
				setShiftBufferTime(uF.showData(rs.getString("shift_base_buffer_time"), ""));
				setStrWLocationLatitude(uF.showData(rs.getString("wlocation_lat"), ""));
				setStrWLocationLongitude(uF.showData(rs.getString("wlocation_long"), ""));
				setStrGeofenceDistance(uF.showData(rs.getString("geofence_distance"), ""));
			}
			rs.close();
			pst.close();
			request.setAttribute("machineName", machineName);
			request.setAttribute("machineSerial", machineSerial);			

			stateList = new FillState(request).fillState(getCountry());
			billingStateList = new FillState(request).fillState(getBillingCountry());
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return LOAD;
	}
	
	public String deleteWLocation(UtilityFunctions uF,String strId) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement(deleteWLocation);
			pst.setInt(1, uF.parseToInt(strId));
			pst.execute();
			pst.close();
			
			session.setAttribute(MESSAGE, SUCCESSM+"Work location deleted successfully."+END);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;
	}
	
	public String loadValidateWLocation() {
		
		request.setAttribute(PAGE, PAddWLocation);
		request.setAttribute(TITLE, TAddWLocation);
			
		countryList = new FillCountry(request).fillCountry();
		stateList = new FillState(request).fillState(getCountry());
		billingCountryList = new FillCountry(request).fillCountry();
		billingStateList = new FillState(request).fillState(getBillingCountry());

		cityList = new FillCity(request).fillCity();
		timezoneList = new FillTimezones(request).fillTimezones();
		wlocationTypeList = new FillWlocationType(request).fillWlocationType();
		weeklyOffList= new FillWeekDays().fillWeekDays();
		weeklyOffList1= new FillWeekDays().fillWeekNos();
		weeklyOffTypeList= new FillWeekDays().fillWeeklyOffType();
		bankList = new FillBank(request).fillBankName();
		currencyList= new FillCurrency(request).fillCurrency();
		shiftBaseList = new FillShiftBase().fillShiftBase();		
		
		request.setAttribute("timezoneList", timezoneList);
		request.setAttribute("wlocationTypeList", wlocationTypeList);
		request.setAttribute("weeklyOffList", weeklyOffList);
		request.setAttribute("bankList", bankList);
		request.setAttribute("currencyList", currencyList);		
		
		return LOAD;
	}
	
	public String getBusinessName() {
		return businessName;
	}

	public void setBusinessName(String businessName) {
		this.businessName = businessName;
	}

	public String getBusinessId() {
		return businessId;
	}

	public void setBusinessId(String businessId) {
		this.businessId = businessId;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getPincode() {
		return pincode;
	}

	public void setPincode(String pincode) {
		this.pincode = pincode;
	}

	public String getFaxNo() {
		return faxNo;
	}

	public void setFaxNo(String faxNo) {
		this.faxNo = faxNo;
	}

	public String getContactNo() {
		return contactNo;
	}

	public void setContactNo(String contactNo) {
		this.contactNo = contactNo;
	}
	
	public String getTanNo() {
		return tanNo;
	}

	public void setTanNo(String tanNo) {
		this.tanNo = tanNo;
	}

	public String getPanNo() {
		return panNo;
	}

	public void setPanNo(String panNo) {
		this.panNo = panNo;
	}
	
	public List<FillCountry> getCountryList() {
		return countryList;
	}

	public void setCountryList(List<FillCountry> countryList) {
		this.countryList = countryList;
	}

	public List<FillState> getStateList() {
		return stateList;
	}

	public void setStateList(List<FillState> stateList) {
		this.stateList = stateList;
	}

	public List<FillCity> getCityList() {
		return cityList;
	}

	public void setCityList(List<FillCity> cityList) {
		this.cityList = cityList;
	}

	private HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getBankAcctNbr() {
		return bankAcctNbr;
	}

	public void setBankAcctNbr(String bankAcctNbr) {
		this.bankAcctNbr = bankAcctNbr;
	}

	public List<FillTimezones> getTimezoneList() {
		return timezoneList;
	}

	public void setTimezoneList(List<FillTimezones> timezoneList) {
		this.timezoneList = timezoneList;
	}

	public String getTimezone() {
		return timezone;
	}

	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}

	public String getBusinessCode() {
		return businessCode;
	}

	public void setBusinessCode(String businessCode) {
		this.businessCode = businessCode;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getWlocationType() {
		return wlocationType;
	}

	public void setWlocationType(String wlocationType) {
		this.wlocationType = wlocationType;
	}

	public String getStrStdTimeStart() {
		return strStdTimeStart;
	}

	public void setStrStdTimeStart(String strStdTimeStart) {
		this.strStdTimeStart = strStdTimeStart;
	}

	public String getStrStdTimeEnd() {
		return strStdTimeEnd;
	}

	public void setStrStdTimeEnd(String strStdTimeEnd) {
		this.strStdTimeEnd = strStdTimeEnd;
	}

	public List<FillWeekDays> getWeeklyOffList() {
		return weeklyOffList;
	}

	public List<FillWlocationType> getWlocationTypeList() {
		return wlocationTypeList;
	}

	public boolean getIsMetro() {
		return isMetro;
	}

	public void setIsMetro(boolean isMetro) {
		this.isMetro = isMetro;
	}

	public String getWeeklyOff1() {
		return weeklyOff1;
	}

	public void setWeeklyOff1(String weeklyOff1) {
		this.weeklyOff1 = weeklyOff1;
	}

	public String getWeeklyOff2() {
		return weeklyOff2;
	}

	public void setWeeklyOff2(String weeklyOff2) {
		this.weeklyOff2 = weeklyOff2;
	}

	public String getWeeklyOffType1() {
		return weeklyOffType1;
	}

	public void setWeeklyOffType1(String weeklyOffType1) {
		this.weeklyOffType1 = weeklyOffType1;
	}

	public String getWeeklyOffType2() {
		return weeklyOffType2;
	}

	public void setWeeklyOffType2(String weeklyOffType2) {
		this.weeklyOffType2 = weeklyOffType2;
	}
	
	public List<FillBank> getBankList() {
		return bankList;
	}

	public void setBankList(List<FillBank> bankList) {
		this.bankList = bankList;
	}

	public String getStrCurrency() {
		return strCurrency;
	}

	public void setStrCurrency(String strCurrency) {
		this.strCurrency = strCurrency;
	}

	public List<FillCurrency> getCurrencyList() {
		return currencyList;
	}

	public void setCurrencyList(List<FillCurrency> currencyList) {
		this.currencyList = currencyList;
	}

	public List<FillWeekDays> getWeeklyOffTypeList() {
		return weeklyOffTypeList;
	}

	public void setWeeklyOffTypeList(List<FillWeekDays> weeklyOffTypeList) {
		this.weeklyOffTypeList = weeklyOffTypeList;
	}

	public String getStrStdTimeStartHd1() {
		return strStdTimeStartHd1;
	}

	public void setStrStdTimeStartHd1(String strStdTimeStartHd1) {
		this.strStdTimeStartHd1 = strStdTimeStartHd1;
	}

	public String getStrStdTimeEndHd1() {
		return strStdTimeEndHd1;
	}

	public void setStrStdTimeEndHd1(String strStdTimeEndHd1) {
		this.strStdTimeEndHd1 = strStdTimeEndHd1;
	}

	public String getStrStdTimeStartHd2() {
		return strStdTimeStartHd2;
	}

	public void setStrStdTimeStartHd2(String strStdTimeStartHd2) {
		this.strStdTimeStartHd2 = strStdTimeStartHd2;
	}

	public String getStrStdTimeEndHd2() {
		return strStdTimeEndHd2;
	}

	public void setStrStdTimeEndHd2(String strStdTimeEndHd2) {
		this.strStdTimeEndHd2 = strStdTimeEndHd2;
	}

	public String getStrStdTimeStartHd3() {
		return strStdTimeStartHd3;
	}

	public void setStrStdTimeStartHd3(String strStdTimeStartHd3) {
		this.strStdTimeStartHd3 = strStdTimeStartHd3;
	}

	public String getStrStdTimeEndHd3() {
		return strStdTimeEndHd3;
	}

	public void setStrStdTimeEndHd3(String strStdTimeEndHd3) {
		this.strStdTimeEndHd3 = strStdTimeEndHd3;
	}

	public List<FillWeekDays> getWeeklyOffList1() {
		return weeklyOffList1;
	}

	public void setWeeklyOffList1(List<FillWeekDays> weeklyOffList1) {
		this.weeklyOffList1 = weeklyOffList1;
	}

	public String getWeeklyOff3() {
		return weeklyOff3;
	}

	public void setWeeklyOff3(String weeklyOff3) {
		this.weeklyOff3 = weeklyOff3;
	}

	public String getWeeklyOffType3() {
		return weeklyOffType3;
	}

	public void setWeeklyOffType3(String weeklyOffType3) {
		this.weeklyOffType3 = weeklyOffType3;
	}

	public String[] getWeekno1() {
		return weekno1;
	}

	public void setWeekno1(String[] weekno1) {
		this.weekno1 = weekno1;
	}

	public String[] getWeekno2() {
		return weekno2;
	}

	public void setWeekno2(String[] weekno2) {
		this.weekno2 = weekno2;
	}

	public String[] getWeekno3() {
		return weekno3;
	}

	public void setWeekno3(String []weekno3) {
		this.weekno3 = weekno3;
	}

	public String getStrEccCode1() {
		return strEccCode1;
	}

	public void setStrEccCode1(String strEccCode1) {
		this.strEccCode1 = strEccCode1;
	}

	public String getStrEccCode2() {
		return strEccCode2;
	}

	public void setStrEccCode2(String strEccCode2) {
		this.strEccCode2 = strEccCode2;
	}

	public String getStrRegNo() {
		return strRegNo;
	}

	public void setStrRegNo(String strRegNo) {
		this.strRegNo = strRegNo;
	}

	public String getStrOrg() {
		return strOrg;
	}

	public void setStrOrg(String strOrg) {
		this.strOrg = strOrg;
	}

	public String getStrPTRegNo() {
		return strPTRegNo;
	}

	public void setStrPTRegNo(String strPTRegNo) {
		this.strPTRegNo = strPTRegNo;
	}

	public String getBillingAddress() {
		return billingAddress;
	}

	public void setBillingAddress(String billingAddress) {
		this.billingAddress = billingAddress;
	}

	public String getBillingCity() {
		return billingCity;
	}

	public void setBillingCity(String billingCity) {
		this.billingCity = billingCity;
	}

	public String getBillingCountry() {
		return billingCountry;
	}

	public void setBillingCountry(String billingCountry) {
		this.billingCountry = billingCountry;
	}

	public String getBillingState() {
		return billingState;
	}

	public void setBillingState(String billingState) {
		this.billingState = billingState;
	}

	public String getBillingPincode() {
		return billingPincode;
	}

	public void setBillingPincode(String billingPincode) {
		this.billingPincode = billingPincode;
	}

	public String getBillingContactNo() {
		return billingContactNo;
	}

	public void setBillingContactNo(String billingContactNo) {
		this.billingContactNo = billingContactNo;
	}

	public String getBillingFaxNo() {
		return billingFaxNo;
	}

	public void setBillingFaxNo(String billingFaxNo) {
		this.billingFaxNo = billingFaxNo;
	}

	public String getBillingEmail() {
		return billingEmail;
	}

	public void setBillingEmail(String billingEmail) {
		this.billingEmail = billingEmail;
	}

	public String getOfficeBillingInfoStatus() {
		return officeBillingInfoStatus;
	}

	public void setOfficeBillingInfoStatus(String officeBillingInfoStatus) {
		this.officeBillingInfoStatus = officeBillingInfoStatus;
	}

	public String getCitAddress() {
		return citAddress;
	}

	public void setCitAddress(String citAddress) {
		this.citAddress = citAddress;
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
	public String getStrLunchBreak() {
		return strLunchBreak;
	}

	public void setStrLunchBreak(String strLunchBreak) {
		this.strLunchBreak = strLunchBreak;
	}

	public String getAddBreakTime() {
		return addBreakTime;
	}

	public void setAddBreakTime(String addBreakTime) {
		this.addBreakTime = addBreakTime;
	}

	public String getShiftBase() {
		return shiftBase;
	}

	public void setShiftBase(String shiftBase) {
		this.shiftBase = shiftBase;
	}

	public List<FillShiftBase> getShiftBaseList() {
		return shiftBaseList;
	}

	public void setShiftBaseList(List<FillShiftBase> shiftBaseList) {
		this.shiftBaseList = shiftBaseList;
	}

	public String getShiftBufferTime() {
		return shiftBufferTime;
	}

	public void setShiftBufferTime(String shiftBufferTime) {
		this.shiftBufferTime = shiftBufferTime;
	}

	public List<FillCountry> getBillingCountryList() {
		return billingCountryList;
	}

	public void setBillingCountryList(List<FillCountry> billingCountryList) {
		this.billingCountryList = billingCountryList;
	}

	public List<FillState> getBillingStateList() {
		return billingStateList;
	}

	public void setBillingStateList(List<FillState> billingStateList) {
		this.billingStateList = billingStateList;
	}

	public String getStrWLocationLatitude() {
		return strWLocationLatitude;
	}

	public void setStrWLocationLatitude(String strWLocationLatitude) {
		this.strWLocationLatitude = strWLocationLatitude;
	}

	public String getStrWLocationLongitude() {
		return strWLocationLongitude;
	}

	public void setStrWLocationLongitude(String strWLocationLongitude) {
		this.strWLocationLongitude = strWLocationLongitude;
	}

	public String getStrGeofenceDistance() {
		return strGeofenceDistance;
	}

	public void setStrGeofenceDistance(String strGeofenceDistance) {
		this.strGeofenceDistance = strGeofenceDistance;
	}
	
}