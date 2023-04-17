package com.konnect.jpms.master;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillSalaryHeads;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;
 
public class AddOverTime extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;

	HttpSession session;
	CommonFunctions CF;
	
	String id;
	List<FillLevel> levelList;
	String operation;
	String submit;
	
	String dayCalculation;
	String fixedDayCal;
	String stWorkingHr;
	String fixedStWorkingHr;
	String standardTime;
	String bufferStandardTime;
	String overTimeWHrs;
	String fixedOverTimeWHrs;
	String minOverTime;
	
	String org_id;
	String calBasis;
	String defaultCalBasis;
	
	String strOvertimeId;
	String strOvertimeCode;
	String strOvertimeDescription;
	String strLevel;
	String strFrom;
	String strTo;
	String strOverTimeType;
	String strOverTimePaymentType;
	String strSalaryHead;
	String strAmount;
	String roundOffTime;
	
	List<FillSalaryHeads> salaryHeadList;
	
	String userscreen;
	String navigationId;
	String toPage;
	
	String[] strMinMinute;
	String[] strMaxMinute;
	String[] strRoundOffMinunte;
	
	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null) return LOGIN;
		UtilityFunctions uF = new UtilityFunctions();		
		
		salaryHeadList = new FillSalaryHeads(request).fillSalaryHeadsWithDuplicationWithoutCTC(getStrLevel());
		levelList = new FillLevel(request).fillLevel(uF.parseToInt(getOrg_id()));
		
		if (getOperation().equals("D")) { 
			return deleteOverTime();
		}else if (getOperation().equals("U")) {
			if(getSubmit()!=null && !getSubmit().trim().equals("") && !getSubmit().trim().equalsIgnoreCase("NULL")){
				updateOverTime(uF);
				return "successupdate"; 
			}
			getOverTime(uF);
			return LOAD;
		}else if (getOperation().equals("A")) {
			if(getSubmit()!=null && !getSubmit().trim().equals("") && !getSubmit().trim().equalsIgnoreCase("NULL")){
				insertOverTime(uF); 
				return "successupdate";  
			}
		}
		
		setDefaultCalBasis("FD");
	
		return LOAD;
		
	}

	private void getOverTime(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database(); 
		db.setRequest(request);
		
		ResultSet rs=null;
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from overtime_details where overtime_id = ? ");
			pst.setInt(1, uF.parseToInt(getId()));
			rs=pst.executeQuery();
			Map<String,String> hmOverTime=new HashMap<String, String>();
			List<String> headList1=new ArrayList<String>();
			String level_id=null;
			while(rs.next()){
				
				if(rs.getString("salaryhead_id")!=null && !rs.getString("salaryhead_id").equals("")){
					headList1=Arrays.asList(rs.getString("salaryhead_id").split(","));
				}else{
					headList1.add("");
				}
				
				hmOverTime.put("OVERTIME_CODE",rs.getString("overtime_code"));
				hmOverTime.put("OVERTIME_DESCRIPTION",rs.getString("overtime_description"));
				hmOverTime.put("LEVEL_ID",rs.getString("level_id"));
				hmOverTime.put("OVERTIME__type",rs.getString("overtime_type"));
				hmOverTime.put("OVERTIME__payment_type",rs.getString("overtime_payment_type"));
				hmOverTime.put("DATE_FROM",uF.getDateFormat(rs.getString("date_from"), DBDATE, DATE_FORMAT));
				hmOverTime.put("DATE_TO",uF.getDateFormat(rs.getString("date_to"), DBDATE, DATE_FORMAT));
				hmOverTime.put("OVERTIME_PAYMENT_AMOUNT",rs.getString("overtime_payment_amount"));

				hmOverTime.put("DAY_CALCULATION",rs.getString("day_calculation"));
				hmOverTime.put("FIXED_DAY_CALCULATION",rs.getString("fixed_day_calculation"));
				hmOverTime.put("STANDARD_WKG_HOURS",rs.getString("standard_wkg_hours"));
				hmOverTime.put("FIXED_STWKG_HOURS",rs.getString("fixed_stwkg_hrs"));
				hmOverTime.put("STANDARD_TIME",rs.getString("standard_time"));
				hmOverTime.put("BUFFER_STANDARD_TIME",rs.getString("buffer_standard_time"));
				hmOverTime.put("OVERTIME_HOURS",rs.getString("over_time_hrs"));
				hmOverTime.put("FIXED_OVERTIME_HOURS",rs.getString("fixed_overtime_hrs"));
				hmOverTime.put("MIN_OVERTIME",rs.getString("min_over_time"));
				hmOverTime.put("CAL_BASIS",rs.getString("calculation_basis"));
				hmOverTime.put("ROUND_OFF_OVERTIME",rs.getString("round_off_time"));
				
				setDayCalculation(rs.getString("day_calculation"));
				setStWorkingHr(rs.getString("standard_wkg_hours"));
				setOverTimeWHrs(rs.getString("over_time_hrs"));
				setOrg_id(rs.getString("org_id"));
				level_id=rs.getString("level_id");
				setStrOverTimeType(rs.getString("overtime_type"));
				setStrOverTimePaymentType(rs.getString("overtime_payment_type"));
				setCalBasis(rs.getString("calculation_basis"));
				setRoundOffTime(rs.getString("round_off_time"));
				
				setDefaultCalBasis(rs.getString("calculation_basis"));
				
			}
			rs.close();
			pst.close();
			
			if(getCalBasis() !=null && getCalBasis().trim().equalsIgnoreCase("M")){
				pst = con.prepareStatement("select * from overtime_minute_slab where overtime_id = ? order by min_minute");
				pst.setInt(1, uF.parseToInt(getId()));
				rs=pst.executeQuery();
				List<Map<String, String>> alMinuteSlab = new ArrayList<Map<String,String>>();
				while(rs.next()){
					Map<String, String> hmMinuteSlab = new HashMap<String, String>();
					hmMinuteSlab.put("OVERTIME_MINUTE_ID", ""+uF.parseToInt(rs.getString("overtime_minute_id")));
					hmMinuteSlab.put("OVERTIME_ID", ""+uF.parseToInt(rs.getString("overtime_id")));
					hmMinuteSlab.put("MIN_MINUTE", ""+uF.parseToInt(rs.getString("min_minute")));
					hmMinuteSlab.put("MAX_MINUTE", ""+uF.parseToInt(rs.getString("max_minute")));
					hmMinuteSlab.put("ROUNDOFF_MINUTE", ""+uF.parseToInt(rs.getString("roundoff_minute")));
					
					alMinuteSlab.add(hmMinuteSlab);
				}
				rs.close();
				pst.close();
				
				request.setAttribute("alMinuteSlab",alMinuteSlab); 
			}
			
			Set<String> setHeadList=new HashSet<String>(headList1);
			List<String> headList=new ArrayList<String>();
			
			Iterator<String> it=setHeadList.iterator();
			while (it.hasNext()) {
				String headid = (String) it.next();
				headList.add(headid.trim());
			}
			request.setAttribute("hmOverTime",hmOverTime); 
			request.setAttribute("headList",headList);
			
			levelList = new FillLevel(request).fillLevel(uF.parseToInt(getOrg_id()));
			salaryHeadList = new FillSalaryHeads(request).fillSalaryHeadsWithDuplication(level_id, null);
			
			request.setAttribute("LEVEL_ID",level_id);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}

	private void updateOverTime(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("update overtime_details set overtime_code=?, overtime_description=?, level_id=?, " +
					"overtime_type=?, overtime_payment_type=?, salaryhead_id=?, date_from=?, date_to=?, overtime_payment_amount=?," +
					"day_calculation=?,fixed_day_calculation=?,standard_wkg_hours=?,fixed_stwkg_hrs=?,standard_time=?,buffer_standard_time=?," +
					"over_time_hrs=?,fixed_overtime_hrs=?,min_over_time=?,calculation_basis=?,round_off_time=? where overtime_id = ? ");
			pst.setString(1, getStrOvertimeCode());
			pst.setString(2, getStrOvertimeDescription());
			pst.setInt(3, uF.parseToInt(getStrLevel()));
			pst.setString(4, getStrOverTimeType());
			pst.setString(5, getStrOverTimePaymentType());
			pst.setString(6, getStrSalaryHead());
			pst.setDate(7, uF.getDateFormat(getStrFrom(), DATE_FORMAT));
			pst.setDate(8, uF.getDateFormat(getStrTo(), DATE_FORMAT));
			pst.setDouble(9, uF.parseToDouble(getStrAmount()));
			
			pst.setString(10,getDayCalculation());
			pst.setString(11,getDayCalculation()!=null && getDayCalculation().equals("F") ? getFixedDayCal() : "0");
			pst.setString(12,getStWorkingHr());
			pst.setString(13,getStWorkingHr()!=null && getStWorkingHr().equals("F") ? getFixedStWorkingHr() : "0");
			pst.setString(14,getStandardTime());
			pst.setString(15, getBufferStandardTime());
			pst.setString(16,getOverTimeWHrs()); 
			pst.setString(17,getFixedOverTimeWHrs());
			pst.setString(18,getMinOverTime());
			pst.setString(19,getCalBasis());
			pst.setInt(20, uF.parseToInt(getRoundOffTime()));
			pst.setInt(21, uF.parseToInt(getId())); 
			int x = pst.executeUpdate();
			pst.close();
			
//			System.out.println("x ===>> " + x + " -- getId() ===>> " + getId() + " -- getCalBasis() ===>> " + getCalBasis());
			
			if(x > 0 && getCalBasis() !=null && getCalBasis().trim().equalsIgnoreCase("M")){
				pst = con.prepareStatement("delete from overtime_minute_slab where overtime_id=?");
				pst.setInt(1, uF.parseToInt(getId()));
				int y = pst.executeUpdate();
				pst.close();
				
				if(uF.parseToInt(getId()) > 0){
					for(int i = 0; getStrMinMinute() !=null && i < getStrMinMinute().length; i++){
						pst = con.prepareStatement("INSERT INTO overtime_minute_slab (overtime_id,min_minute,max_minute,roundoff_minute) " +
								"values (?,?,?,?)");
						pst.setInt(1, uF.parseToInt(getId()));
						pst.setInt(2, uF.parseToInt(getStrMinMinute()[i]));
						pst.setInt(3, uF.parseToInt(getStrMaxMinute()[i]));
						pst.setInt(4, uF.parseToInt(getStrRoundOffMinunte()[i]));
						pst.execute();
						pst.close();						
					}
				}				
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}

	public String insertOverTime(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs=null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);

			pst = con.prepareStatement("INSERT INTO overtime_details (overtime_code, overtime_description, level_id, overtime_type, " +
					"overtime_payment_type, salaryhead_id, date_from, date_to, overtime_payment_amount,day_calculation,fixed_day_calculation," +
					"standard_wkg_hours,fixed_stwkg_hrs,standard_time,buffer_standard_time,over_time_hrs,fixed_overtime_hrs,min_over_time," +
					"org_id,calculation_basis,round_off_time) " +
					"values (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?)");
			pst.setString(1, getStrOvertimeCode());
			pst.setString(2, getStrOvertimeDescription());
			pst.setInt(3, uF.parseToInt(getStrLevel()));
			pst.setString(4, getStrOverTimeType());
			pst.setString(5, getStrOverTimePaymentType());
			pst.setString(6, getStrSalaryHead());
			pst.setDate(7, uF.getDateFormat(getStrFrom(), DATE_FORMAT));
			pst.setDate(8, uF.getDateFormat(getStrTo(), DATE_FORMAT));
			pst.setDouble(9, uF.parseToDouble(getStrAmount()));			
			pst.setString(10,getDayCalculation());
			pst.setString(11,getDayCalculation()!=null && getDayCalculation().equals("F") ? getFixedDayCal() : "0");
			pst.setString(12,getStWorkingHr());
			pst.setString(13,getStWorkingHr()!=null && getStWorkingHr().equals("F") ? getFixedStWorkingHr() : "0");
			pst.setString(14,getStandardTime());
			pst.setString(15, getBufferStandardTime());
			pst.setString(16,getOverTimeWHrs()); 
			pst.setString(17,getFixedOverTimeWHrs()); 
			pst.setString(18,getMinOverTime());	
			pst.setInt(19, uF.parseToInt(getOrg_id()));
			pst.setString(20,getCalBasis());
			pst.setInt(21, uF.parseToInt(getRoundOffTime()));
			int x = pst.executeUpdate();
			pst.close();
			
			if(x > 0 && getCalBasis() !=null && getCalBasis().trim().equalsIgnoreCase("M")){
				pst = con.prepareStatement("select max(overtime_id) as overtime_id from overtime_details");
				rs=pst.executeQuery();
				int overTimeId = 0;
				while(rs.next()){
					overTimeId = rs.getInt("overtime_id");
				}
				rs.close();
				pst.close();
				
				if(overTimeId > 0){
					for(int i = 0; getStrMinMinute() !=null && i < getStrMinMinute().length; i++){
						pst = con.prepareStatement("INSERT INTO overtime_minute_slab (overtime_id,min_minute,max_minute,roundoff_minute) " +
								"values (?,?,?,?)");
						pst.setInt(1, overTimeId);
						pst.setInt(2, uF.parseToInt(getStrMinMinute()[i]));
						pst.setInt(3, uF.parseToInt(getStrMaxMinute()[i]));
						pst.setInt(4, uF.parseToInt(getStrRoundOffMinunte()[i]));
						pst.execute();
						pst.close();						
					}
				}				
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}
	
	public String deleteOverTime() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement(deleteOverTime);
			pst.setInt(1, uF.parseToInt(getId()));
			pst.execute();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
			return ERROR;
		} finally {
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return "successupdate";

	}

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public String getStrOvertimeId() {
		return strOvertimeId;
	}

	public void setStrOvertimeId(String strOvertimeId) {
		this.strOvertimeId = strOvertimeId;
	}

	public String getStrOvertimeCode() {
		return strOvertimeCode;
	}

	public void setStrOvertimeCode(String strOvertimeCode) {
		this.strOvertimeCode = strOvertimeCode;
	}

	public String getStrOvertimeDescription() {
		return strOvertimeDescription;
	}

	public void setStrOvertimeDescription(String strOvertimeDescription) {
		this.strOvertimeDescription = strOvertimeDescription;
	}

	public String getStrLevel() {
		return strLevel;
	}

	public void setStrLevel(String strLevel) {
		this.strLevel = strLevel;
	}

	public String getStrFrom() {
		return strFrom;
	}

	public void setStrFrom(String strFrom) {
		this.strFrom = strFrom;
	}

	public String getStrTo() {
		return strTo;
	}

	public void setStrTo(String strTo) {
		this.strTo = strTo;
	}

	public String getStrOverTimeType() {
		return strOverTimeType;
	}

	public void setStrOverTimeType(String strOverTimeType) {
		this.strOverTimeType = strOverTimeType;
	}

	public String getStrOverTimePaymentType() {
		return strOverTimePaymentType;
	}

	public void setStrOverTimePaymentType(String strOverTimePaymentType) {
		this.strOverTimePaymentType = strOverTimePaymentType;
	}

	public String getStrSalaryHead() {
		return strSalaryHead;
	}

	public void setStrSalaryHead(String strSalaryHead) {
		this.strSalaryHead = strSalaryHead;
	}

	public String getStrAmount() {
		return strAmount;
	}

	public void setStrAmount(String strAmount) {
		this.strAmount = strAmount;
	}

	public List<FillSalaryHeads> getSalaryHeadList() {
		return salaryHeadList;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<FillLevel> getLevelList() {
		return levelList;
	}

	public void setLevelList(List<FillLevel> levelList) {
		this.levelList = levelList;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getSubmit() {
		return submit;
	}

	public void setSubmit(String submit) {
		this.submit = submit;
	}

	public String getDayCalculation() {
		return dayCalculation;
	}

	public void setDayCalculation(String dayCalculation) {
		this.dayCalculation = dayCalculation;
	}

	public String getFixedDayCal() {
		return fixedDayCal;
	}

	public void setFixedDayCal(String fixedDayCal) {
		this.fixedDayCal = fixedDayCal;
	}

	public String getStWorkingHr() {
		return stWorkingHr;
	}

	public void setStWorkingHr(String stWorkingHr) {
		this.stWorkingHr = stWorkingHr;
	}

	public String getFixedStWorkingHr() {
		return fixedStWorkingHr;
	}

	public void setFixedStWorkingHr(String fixedStWorkingHr) {
		this.fixedStWorkingHr = fixedStWorkingHr;
	}

	public String getStandardTime() {
		return standardTime;
	}

	public void setStandardTime(String standardTime) {
		this.standardTime = standardTime;
	}

	public String getBufferStandardTime() {
		return bufferStandardTime;
	}

	public void setBufferStandardTime(String bufferStandardTime) {
		this.bufferStandardTime = bufferStandardTime;
	}

	public String getOverTimeWHrs() {
		return overTimeWHrs;
	}

	public void setOverTimeWHrs(String overTimeWHrs) {
		this.overTimeWHrs = overTimeWHrs;
	}

	public String getFixedOverTimeWHrs() {
		return fixedOverTimeWHrs;
	}

	public void setFixedOverTimeWHrs(String fixedOverTimeWHrs) {
		this.fixedOverTimeWHrs = fixedOverTimeWHrs;
	}

	public String getMinOverTime() {
		return minOverTime;
	}

	public void setMinOverTime(String minOverTime) {
		this.minOverTime = minOverTime;
	}

	public String getOrg_id() {
		return org_id;
	}

	public void setOrg_id(String org_id) {
		this.org_id = org_id;
	}

	public String getCalBasis() {
		return calBasis;
	}

	public void setCalBasis(String calBasis) {
		this.calBasis = calBasis;
	}

	public String getDefaultCalBasis() {
		return defaultCalBasis;
	}

	public void setDefaultCalBasis(String defaultCalBasis) {
		this.defaultCalBasis = defaultCalBasis;
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

	public String getRoundOffTime() {
		return roundOffTime;
	}

	public void setRoundOffTime(String roundOffTime) {
		this.roundOffTime = roundOffTime;
	}
	public String[] getStrMinMinute() {
		return strMinMinute;
	}

	public void setStrMinMinute(String[] strMinMinute) {
		this.strMinMinute = strMinMinute;
	}

	public String[] getStrMaxMinute() {
		return strMaxMinute;
	}

	public void setStrMaxMinute(String[] strMaxMinute) {
		this.strMaxMinute = strMaxMinute;
	}

	public String[] getStrRoundOffMinunte() {
		return strRoundOffMinunte;
	}

	public void setStrRoundOffMinunte(String[] strRoundOffMinunte) {
		this.strRoundOffMinunte = strRoundOffMinunte;
	}
}