package com.konnect.jpms.payroll;

import java.io.ByteArrayOutputStream;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.omg.CORBA.PRIVATE_MEMBER;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Element;
import com.konnect.jpms.export.DataStyle;
import com.konnect.jpms.export.payroll.ExcelSheetDesign;
import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillEmploymentType;
import com.konnect.jpms.select.FillFinancialYears;
import com.konnect.jpms.select.FillGrade;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillPayCycles;
import com.konnect.jpms.select.FillPayMode;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;
 
public class TDSProjection extends ActionSupport implements ServletRequestAware, ServletResponseAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	  
	public CommonFunctions CF = null; 
	
	private String strLocation;
	private String strDepartment;
	private String strLevel;
	private String strEmployeType;
	private String strGrade;
	private String[] f_strWLocation; 
	private String[] f_level;
	private String[] f_department;
//	String[] f_service;
	String[] f_employeType;
	String[] f_grade;
	
	private List<FillPayCycles> paycycleList ;
	private List<FillFinancialYears> financialYearList;
	private List<FillDepartment> departmentList;
	private List<FillLevel> levelList;
	private List<FillServices> serviceList;
	private List<FillWLocation> wLocationList;
	
	private List<FillPayMode> paymentModeList;
	private List<FillOrganisation> organisationList;
	List<FillEmploymentType> employementTypeList;
	List<FillGrade> gradeList;

	private String f_org;
	private String f_paymentMode;
	private List <FillFinancialYears> strFinancialYearList;
	private String financialYear;
	private String strAmount;
	
	private String submit;
	
	private String []strEmpId_strMonth;
	private String []strTDS;
	
	String proPage;
	String minLimit;
	
	String strSearch;
	String exceldownload;
	public String execute() throws Exception {
		
		try {
			
			session = request.getSession();
			request.setAttribute(PAGE, "/jsp/payroll/TDSProjection.jsp");
			request.setAttribute(TITLE, "TDS Projection");
			CF = (CommonFunctions) session.getAttribute(CommonFunctions);
			if(CF==null)return LOGIN;
			
			strUserType = (String)session.getAttribute(USERTYPE);
			
			UtilityFunctions uF = new UtilityFunctions();
			String action = request.getParameter("action");
			String month = request.getParameter("month");
			String empId = request.getParameter("empId");
			
//			System.out.println("action="+action+" month="+month+" empId="+empId);
			if(action!=null && month!=null && empId!=null){
				updateTDSPayroll(uF, action, month, empId);
				return "ajax";
			}
			
//			boolean isView  = CF.getAccess(session, request, uF);
//			if(!isView){
//				request.setAttribute(PAGE, PAccessDenied);
//				request.setAttribute(TITLE, TAccessDenied);
//				return ACCESS_DENIED;
//			}
			
//			System.out.println("Excel===>"+getExceldownload()); 
			if(uF.parseToInt(getProPage()) == 0) {
				setProPage("1");
			}
			
			if(getF_org()==null || getF_org().trim().equals("")){
				setF_org((String)session.getAttribute(ORGID));
			}

			if(getStrLocation() != null && !getStrLocation().equals("")) {
				setF_strWLocation(getStrLocation().split(","));
			} else {
				setF_strWLocation(null);
			}
			if(getStrDepartment() != null && !getStrDepartment().equals("")) {
				setF_department(getStrDepartment().split(","));
			} else {
				setF_department(null);
			}
			
			if(getStrLevel() != null && !getStrLevel().equals("")) {
				setF_level(getStrLevel().split(","));
			} else {
				setF_level(null);
			}
			
			if(getStrEmployeType() !=null && !getStrEmployeType().equals("")){
				setF_employeType(getStrEmployeType().split(","));
			}else{
				setF_employeType(null);
			}
			
			if(getStrGrade() !=null && !getStrGrade().equals("")){
				setF_grade(getStrGrade().split(","));
			}else{
				setF_grade(null);
			}
			
			
			if(f_level!=null)
			{
				
				String level_id ="";
				for (int i = 0; i < f_level.length; i++) {
					
					if(i==0)
					{
						level_id = f_level[i];
						level_id.concat(f_level[i]);
					}else{
						level_id =level_id+","+f_level[i];
					}
				}
				
				gradeList = new FillGrade(request).fillGrade(level_id,getF_org());
			}else{
				gradeList = new FillGrade(request).fillGradebyorganisation(getF_org());
			}
			
//			if(getSubmit()!=null){
//				saveTDS(uF);
//			}
			getSearchAutoCompleteData(uF);
			viewTDSNew(uF,null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return loadPaySlips();
	}
	
	private void getSearchAutoCompleteData(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			
			
			String[] strFianacialDates = null;
			String strFinancialYearStart = null;
			String strFinancialYearEnd = null;
			if (getFinancialYear() != null && !getFinancialYear().trim().equals("") && !getFinancialYear().trim().equalsIgnoreCase("NULL")) {				
				String str = URLDecoder.decode(getFinancialYear());
				setFinancialYear(str);
				strFianacialDates = getFinancialYear().split("-");
				strFinancialYearStart = strFianacialDates[0];
				strFinancialYearEnd = strFianacialDates[1];			
			} else {				
				strFianacialDates = new FillFinancialYears(request).fillLatestFinancialYears();
				setFinancialYear(strFianacialDates[0] + "-" + strFianacialDates[1]);				
				strFinancialYearStart = strFianacialDates[0];
				strFinancialYearEnd = strFianacialDates[1];				 
			}
			
			con = db.makeConnection(con);
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			SortedSet<String> setSearchList = new TreeSet<String>();
			StringBuilder sbQuery=new StringBuilder();
			sbQuery.append("select eod.emp_id,epd.emp_fname,epd.emp_mname,epd.emp_lname,epd.empcode,eod.depart_id,epd.emp_pan_no,epd.employment_end_date," +
					"epd.joining_date,epd.emp_address1,epd.emp_city_id, epd.emp_gender from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id");
			if(getF_level()!=null && getF_level().length>0){
                sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
            }
            if(getF_department()!=null && getF_department().length>0){
                sbQuery.append(" and depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
            }
            
//            if(getF_service()!=null && getF_service().length>0){
//                sbQuery.append(" and (");
//                for(int i=0; i<getF_service().length; i++){
//                    sbQuery.append(" eod.service_id like '%"+getF_service()[i]+",%'");
//                    
//                    if(i<getF_service().length-1){
//                        sbQuery.append(" OR "); 
//                    }
//                }
//                sbQuery.append(" ) ");
//                 
//            } 
            
            if(getF_strWLocation()!=null && getF_strWLocation().length>0){
                sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
            }else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && session.getAttribute(WLOCATION_ACCESS)!=null){
				sbQuery.append(" and wlocation_id in ("+session.getAttribute(WLOCATION_ACCESS)+")");
			}
            
            if(uF.parseToInt(getF_org())>0){
				sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
			}else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && session.getAttribute(ORG_ACCESS)!=null){
				sbQuery.append(" and org_id in ("+session.getAttribute(ORG_ACCESS)+")");
			}
			
			if(uF.parseToInt(getF_paymentMode())>0){
				sbQuery.append(" and payment_mode = "+uF.parseToInt(getF_paymentMode()));	
			}
			sbQuery.append(" and eod.emp_id in(select emp_id from emp_salary_details where salary_head_id="+TDS+" and isdisplay=true and is_approved=true)");
			sbQuery.append(" and (epd.employment_end_date is null or epd.employment_end_date<=?)");
			
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			System.out.println("pst=Search=="+pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				
				setSearchList.add(rs.getString("emp_fname")+strEmpMName+" "+rs.getString("emp_lname"));
			}
			rs.close();
			pst.close();
			
			StringBuilder sbData = null;
			Iterator<String> it = setSearchList.iterator();
			while (it.hasNext()){
				String strData = it.next();
				if(sbData == null){
					sbData = new StringBuilder();
					sbData.append("\""+strData+"\"");
				} else {
					sbData.append(",\""+strData+"\"");
				}
			}
			
			if(sbData == null){
				sbData = new StringBuilder();
			}
			request.setAttribute("sbData", sbData.toString());
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	public String loadPaySlips(){
		UtilityFunctions uF=new UtilityFunctions();
		
//		paycycleList = new FillPayCycles(getStrPaycycleDuration(), request).fillPayCycles(CF);
		financialYearList = new FillFinancialYears(request).fillFinancialYears(CF);
//		alMonthList = new FillMonth().fillMonth();
		
//		paycycleDurationList = new FillPayCycleDuration().fillPayCycleDuration();
		paymentModeList = new FillPayMode().fillPaymentMode();
		
		departmentList = new FillDepartment(request).fillDepartment(uF.parseToInt(getF_org()));
		levelList = new FillLevel(request).fillLevel(uF.parseToInt(getF_org()));
		serviceList = new FillServices(request).fillServices(getF_org(),uF);
//		bankList = new FillBank(request).fillBankAccNo();
		
		employementTypeList = new FillEmploymentType().fillEmploymentType(request);
		
		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)){
			wLocationList = new FillWLocation(request).fillWLocation(getF_org(), (String)session.getAttribute(WLOCATION_ACCESS));
			organisationList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
		}else{
			organisationList = new FillOrganisation(request).fillOrganisation();
			wLocationList = new FillWLocation(request).fillWLocation(getF_org());
		}
		
		strFinancialYearList = new FillFinancialYears(request).fillFinancialYears(CF);
		
		getSelectedFilter(uF);
		
		return LOAD;
	}

	public void updateTDSPayroll(UtilityFunctions uF, String action, String month, String empId){
		
		Connection con = null;
		PreparedStatement pst=null;		
		Database db = new Database();
		db.setRequest(request);
		
		
		try {
			String[] strFinancialYearDates = null;

			if (getFinancialYear() != null && !getFinancialYear().trim().equals("") && !getFinancialYear().trim().equalsIgnoreCase("NULL")) {
				String str = URLDecoder.decode(getFinancialYear());
				setFinancialYear(str);
				strFinancialYearDates = getFinancialYear().split("-");
			} else {
				strFinancialYearDates = new FillFinancialYears(request).fillLatestFinancialYears();
				setFinancialYear(strFinancialYearDates[0] + "-" + strFinancialYearDates[1]);
			}
			
			con = db.makeConnection(con);
						
			if(action!=null && action.equalsIgnoreCase("D")){
				
				pst = con.prepareStatement(deleteTDSProjection);
				pst.setInt(1, uF.parseToInt(month));
				pst.setInt(2, uF.parseToInt(empId));
				pst.setDate(3, uF.getDateFormat(strFinancialYearDates[0], DATE_FORMAT));
				pst.setDate(4, uF.getDateFormat(strFinancialYearDates[1], DATE_FORMAT));
				pst.execute();
				pst.close();
				request.setAttribute("STATUS_MSG", "Deleted");
				
			}else if(action!=null && action.equalsIgnoreCase("U")){
				pst = con.prepareStatement(updateTDSProjection);
				pst.setDouble(1, uF.parseToDouble(getStrAmount()));
				pst.setInt(2, uF.parseToInt(month));
				pst.setInt(3, uF.parseToInt(empId));
				pst.setDate(4, uF.getDateFormat(strFinancialYearDates[0], DATE_FORMAT));
				pst.setDate(5, uF.getDateFormat(strFinancialYearDates[1], DATE_FORMAT));
				int x = pst.executeUpdate();
//				System.out.println("update pst ===>> "+ pst);
				pst.close();
				if(x==0){
					pst = con.prepareStatement(insertTDSProjection);
					pst.setDouble(1, uF.parseToDouble(getStrAmount()));
					pst.setInt(2, uF.parseToInt(month));
					pst.setInt(3, uF.parseToInt(empId));
					pst.setInt(4, TDS);
					pst.setDate(5, uF.getDateFormat(strFinancialYearDates[0], DATE_FORMAT));
					pst.setDate(6, uF.getDateFormat(strFinancialYearDates[1], DATE_FORMAT));
					pst.execute();
//					System.out.println("insert pst ===>> "+ pst);
					pst.close();
				}
				request.setAttribute("STATUS_MSG", "Updated");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	public String viewTDSNew(UtilityFunctions uF,String empId) {
		Connection con = null;
		PreparedStatement pst=null;
		PreparedStatement pst1 = null;
		ResultSet rs = null;
		ResultSet rs1 = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			String[] strFinancialYearDates = null;
			String strFinancialYearStart = null;
			String strFinancialYearEnd = null;

			if (getFinancialYear() != null && !getFinancialYear().trim().equals("") && !getFinancialYear().trim().equalsIgnoreCase("NULL")) {
				String str = URLDecoder.decode(getFinancialYear());
				setFinancialYear(str);
				strFinancialYearDates = getFinancialYear().split("-");
				strFinancialYearStart = strFinancialYearDates[0];
				strFinancialYearEnd = strFinancialYearDates[1];
				setFinancialYear(getFinancialYear());
			} else {
				strFinancialYearDates = new FillFinancialYears(request).fillLatestFinancialYears();
				setFinancialYear(strFinancialYearDates[0] + "-" + strFinancialYearDates[1]);
				strFinancialYearStart = strFinancialYearDates[0];
				strFinancialYearEnd = strFinancialYearDates[1];
			}
			request.setAttribute("strD1", strFinancialYearDates[0]);
			request.setAttribute("strD2", strFinancialYearDates[1]);
			
			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			Map<String, String> hmEmpMap = CF.getEmpNameMap(con,null, null);
			Map<String, String> hmEmpCodeMap = CF.getEmpCodeMap(con);
			
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.DATE, uF.parseToInt(uF.getDateFormat(strFinancialYearStart, DATE_FORMAT, "dd")));
			cal.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(strFinancialYearStart, DATE_FORMAT, "MM"))-1);
			cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strFinancialYearStart, DATE_FORMAT, "yyyy")));
			
			List<String> alMonth = new ArrayList<String>();
			for(int i=0; i<12;i++){
				String strDate = cal.get(Calendar.DATE)+"/"+(cal.get(Calendar.MONTH) + 1)+"/"+cal.get(Calendar.YEAR);
				alMonth.add(uF.getDateFormat(strDate, DATE_FORMAT, "MM"));
				cal.add(Calendar.MONTH, 1);
			}
			
			/*System.out.println("alMonth.size()"+alMonth.size());
			for(int i=0;i<alMonth.size();i++){
				System.out.println("Month=="+uF.getMonth(uF.parseToInt((String) alMonth.get(i))));
			}*/
//			Map<String, String> hmTDSProjectedEmp = new HashMap<String, String>();
			
			Map<String, String> hmEmpTDSPaidAmountDetails=new HashMap<String, String>();
			Map<String, Map<String, String>> hmPrevOrgTDSDetailsAllEmp = new HashMap<String, Map<String,String>>(); 
			List<String> alEmp = new ArrayList<String>();
			Map<String, String> hmTDSEmp = new HashMap<String, String>();
			
			if(empId!=null){					

			}else{
				StringBuilder sbQuery=new StringBuilder();
				sbQuery.append("select count(eod.emp_id) as empCount from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id");
				/*if(getF_level()!=null && getF_level().length>0){
	                sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
	            }*/
				  if(getF_grade()!=null && getF_grade().length>0 && getF_level()!=null && getF_level().length>0)
		            {
		            	sbQuery.append(" and grade_id in ("+StringUtils.join(getF_grade(), ",")+" ) ");
		            	//sbQuery.append(" and grade_id in ( " +StringUtils.join(getF_grade(), ",")+" ) and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");	
		            }else {
		            	 if(getF_level()!=null && getF_level().length>0){
		                     sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
		                 }
		            	 if(getF_grade()!=null && getF_grade().length>0){
		                     sbQuery.append(" and grade_id in ("+StringUtils.join(getF_grade(), ",")+" ) ");
		                 }
					}
				if (getF_employeType() != null && getF_employeType().length > 0) {
					sbQuery.append(" and emptype in ( '" + StringUtils.join(getF_employeType(), "','") + "') ");
				}
	            if(getF_department()!=null && getF_department().length>0){
	                sbQuery.append(" and depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
	            }
	            
//	            if(getF_service()!=null && getF_service().length>0){
//	                sbQuery.append(" and (");
//	                for(int i=0; i<getF_service().length; i++){
//	                    sbQuery.append(" eod.service_id like '%"+getF_service()[i]+",%'");
//	                    
//	                    if(i<getF_service().length-1){
//	                        sbQuery.append(" OR "); 
//	                    }
//	                }
//	                sbQuery.append(" ) ");
//	                
//	            } 
	            
	            if(getF_strWLocation()!=null && getF_strWLocation().length>0){
	                sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
	            }else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && session.getAttribute(WLOCATION_ACCESS)!=null){
					sbQuery.append(" and wlocation_id in ("+session.getAttribute(WLOCATION_ACCESS)+")");
				}
	            
	            if(uF.parseToInt(getF_org())>0){
					sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
				}else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && session.getAttribute(ORG_ACCESS)!=null){
					sbQuery.append(" and org_id in ("+session.getAttribute(ORG_ACCESS)+")");
				}
				
				if(uF.parseToInt(getF_paymentMode())>0){
					sbQuery.append(" and payment_mode = "+uF.parseToInt(getF_paymentMode()));	
				}
				sbQuery.append(" and eod.emp_id in(select emp_id from emp_salary_details where salary_head_id="+TDS+" and isdisplay=true and is_approved=true)");
				sbQuery.append(" and (epd.employment_end_date is null or epd.employment_end_date<=?)");
				if(getStrSearch()!=null && !getStrSearch().trim().equals("") && !getStrSearch().trim().equalsIgnoreCase("NULL")){
					if(flagMiddleName) {
						sbQuery.append(" and (upper(emp_fname)||' '||upper(emp_mname)||' '||upper(emp_lname) like '%"+getStrSearch().trim().toUpperCase()+"%' or upper(emp_fname)||' '||upper(emp_lname) like '%"+getStrSearch().trim().toUpperCase()+"%')");
					} else {
						sbQuery.append(" and upper(emp_fname)||' '||upper(emp_lname) like '%"+getStrSearch().trim().toUpperCase()+"%'");
					}
				}
			//===start parvez date: 03-11-2022====	
				sbQuery.append(" and epd.is_alive=true ");
			//===end parvez date: 03-11-2022===	
				
				pst = con.prepareStatement(sbQuery.toString());
				pst.setDate(1, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//				System.out.println("pst=0==>"+pst);
				rs = pst.executeQuery();
				int recCnt = 0;
				int pageCount = 0;
				while(rs.next()){
					recCnt = rs.getInt("empCount");
					pageCount = rs.getInt("empCount")/15;
					if(rs.getInt("empCount")%15 != 0) {
						pageCount++;
					}
				}
				rs.close();
				pst.close();
				request.setAttribute("pageCount", pageCount+"");
				request.setAttribute("recCnt", recCnt+"");
			}
			
			if(empId!=null){
				pst = con.prepareStatement("select * from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id and emp_per_id=?");
				pst.setInt(1,uF.parseToInt(empId));
//				System.out.println("pst==="+pst);

			}else{
				StringBuilder sbQuery = new StringBuilder();
				sbQuery.append("select * from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id");
				/*if(getF_level()!=null && getF_level().length>0){
	                sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
	            }*/
				  if(getF_grade()!=null && getF_grade().length>0 && getF_level()!=null && getF_level().length>0)
		            {
		            	sbQuery.append(" and grade_id in ("+StringUtils.join(getF_grade(), ",")+" ) ");
		            	//sbQuery.append(" and grade_id in ( " +StringUtils.join(getF_grade(), ",")+" ) and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");	
		            } else {
		            	 if(getF_level()!=null && getF_level().length>0){
		                     sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
		                 }
		            	 if(getF_grade()!=null && getF_grade().length>0){
		                     sbQuery.append(" and grade_id in ("+StringUtils.join(getF_grade(), ",")+" ) ");
		                 }
					}
				if (getF_employeType() != null && getF_employeType().length > 0) {
					sbQuery.append(" and emptype in ( '" + StringUtils.join(getF_employeType(), "','") + "') ");
				}
	            if(getF_department()!=null && getF_department().length>0){
	                sbQuery.append(" and depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
	            }
	            
//	            if(getF_service()!=null && getF_service().length>0){
//	                sbQuery.append(" and (");
//	                for(int i=0; i<getF_service().length; i++){
//	                    sbQuery.append(" eod.service_id like '%"+getF_service()[i]+",%'");
//	                    
//	                    if(i<getF_service().length-1){
//	                        sbQuery.append(" OR "); 
//	                    }
//	                }
//	                sbQuery.append(" ) ");
//	            } 
	            
	            if(getF_strWLocation()!=null && getF_strWLocation().length>0){
	                sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
	            }else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && session.getAttribute(WLOCATION_ACCESS)!=null){
					sbQuery.append(" and wlocation_id in ("+session.getAttribute(WLOCATION_ACCESS)+")");
				}
	            
	            if(uF.parseToInt(getF_org())>0){
					sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
				}else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && session.getAttribute(ORG_ACCESS)!=null){
					sbQuery.append(" and org_id in ("+session.getAttribute(ORG_ACCESS)+")");
				}
				
				if(uF.parseToInt(getF_paymentMode())>0){
					sbQuery.append(" and payment_mode = "+uF.parseToInt(getF_paymentMode()));	
				}
				sbQuery.append(" and eod.emp_id in(select emp_id from emp_salary_details where salary_head_id="+TDS+" and isdisplay=true and is_approved=true)");
				sbQuery.append(" and (epd.employment_end_date is null or epd.employment_end_date<=?)");
		//===start parvez date: 06-10-2022===		
				sbQuery.append(" and epd.is_alive=true ");
		//===end parvez date: 06-10-2022===		
				if(getStrSearch()!=null && !getStrSearch().trim().equals("") && !getStrSearch().trim().equalsIgnoreCase("NULL")){
					if(flagMiddleName) {
						sbQuery.append(" and (upper(emp_fname)||' '||upper(emp_mname)||' '||upper(emp_lname) like '%"+getStrSearch().trim().toUpperCase()+"%' or upper(emp_fname)||' '||upper(emp_lname) like '%"+getStrSearch().trim().toUpperCase()+"%')");
					} else {
						sbQuery.append(" and upper(emp_fname)||' '||upper(emp_lname) like '%"+getStrSearch().trim().toUpperCase()+"%'");
					}
				}
				sbQuery.append(" order by emp_fname, emp_lname");
				
				int intOffset = uF.parseToInt(minLimit);
				sbQuery.append(" limit 15 offset "+intOffset+"");
				pst = con.prepareStatement(sbQuery.toString());
				pst.setDate(1, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//				System.out.println("pst for tds projection ==="+pst);
			}
			rs = pst.executeQuery();
			while(rs.next()){
				String strEmpId = rs.getString("emp_per_id");

				alEmp.add(strEmpId);
				
				ViewEmpTDSProjection empTDSProjection = new ViewEmpTDSProjection();
				empTDSProjection.request = request;
				empTDSProjection.session = session;
				empTDSProjection.CF = CF;
				empTDSProjection.setStrEmpId(strEmpId);
				empTDSProjection.setStrFinancialYearStart(strFinancialYearStart);
				empTDSProjection.setStrFinancialYearEnd(strFinancialYearEnd);
				empTDSProjection.getEmpTDSProjection(uF);
				
				Map<String, String> hmTDSEmp1 = (Map<String, String>)request.getAttribute("hmTDSEmp1");
				if(hmTDSEmp1 == null) hmTDSEmp1 = new HashMap<String, String>();
				Iterator<String> it = hmTDSEmp1.keySet().iterator(); 
				while(it.hasNext()){
					String key = it.next();
					
					hmTDSEmp.put(key, hmTDSEmp1.get(key));
				}
				
				Map<String, String> hmEmpTDSPaidAmountDetails1 = (Map<String, String>)request.getAttribute("hmTDSPaidEmp1");
				if(hmEmpTDSPaidAmountDetails1 == null) hmEmpTDSPaidAmountDetails1 = new HashMap<String, String>();
				Iterator<String> it1 = hmEmpTDSPaidAmountDetails1.keySet().iterator(); 
				while(it1.hasNext()){
					String key = it1.next();
					
					hmEmpTDSPaidAmountDetails.put(key, hmEmpTDSPaidAmountDetails1.get(key));
				}
				
				Map<String, String> hmPrevOrgTDSDetails = (Map<String, String>) request.getAttribute("hmPrevOrgTDSDetails");
				if(hmPrevOrgTDSDetails==null) hmPrevOrgTDSDetails = new HashMap<String, String>();
				hmPrevOrgTDSDetailsAllEmp.put(strEmpId, hmPrevOrgTDSDetails);
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmPrevOrgTDSDetailsAllEmp", hmPrevOrgTDSDetailsAllEmp);
//			System.out.println("TDSProjection hmPrevOrgTDSDetailsAllEmp ===>> " + hmPrevOrgTDSDetailsAllEmp);
			
			if(getExceldownload()!=null && !getExceldownload().equalsIgnoreCase("null")) {
				if(getExceldownload().equalsIgnoreCase("true")) {
					genratedexcel(uF,null);
				}
			}
			
			request.setAttribute("hmEmpMap", hmEmpMap);
			request.setAttribute("hmEmpCodeMap", hmEmpCodeMap);
			request.setAttribute("alEmp", alEmp);
			request.setAttribute("alMonth", alMonth);
			request.setAttribute("hmTDSEmp", hmTDSEmp);
			request.setAttribute("hmTDSPaidEmp", hmEmpTDSPaidAmountDetails);
			
//			System.out.println("hmEmpMap=======>"+hmEmpMap);
//			System.out.println("hmEmpCodeMap=====>"+hmEmpCodeMap);
//			System.out.println("alEmp=====>"+alEmp);
//			System.out.println("alMonth=====>"+alMonth);
//			System.out.println("hmTDSEmp======>"+hmTDSEmp);  
//			System.out.println("hmTDSPaidEmp=======>"+hmEmpTDSPaidAmountDetails);
//			System.out.println("hmTDSProjectedEmp1=====>"+hmTDSProjectedEmp1);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs1);
			db.closeResultSet(rs);
			db.closeStatements(pst1);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;
	}

public Map<String, String> getEmpHomeLoanExemptions(Connection con, UtilityFunctions uF, String strFinancialYearStart, String strFinancialYearEnd){
	
	PreparedStatement pst = null;
	ResultSet rs = null;
	Map<String,String> hmEmpHomeLoanMap = new HashMap<String,String>();
	
	try {
		
//		pst = con.prepareStatement("select * from section_details where section_id = (select section_id from section_details where upper(section_code) like '%HOME% %INTEREST%') and isdisplay=true");
		pst = con.prepareStatement("select * from section_details where section_id = 11 and financial_year_start=? and financial_year_end=?");
		pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
		pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
		rs = pst.executeQuery();
		double dblLoanExemptionLimit = 0;
		while (rs.next()) {
			dblLoanExemptionLimit = rs.getDouble("section_exemption_limit");
		}
		rs.close();
		pst.close();
		
		
		
//		pst = con.prepareStatement("select sum(amount_paid) as amount_paid, emp_id from investment_details where fy_from = ? and fy_to = ? and status = true and  section_id = (select section_id from section_details where upper(section_code) like '%HOME% %INTEREST%' and isdisplay=true)  group by emp_id");
//		pst = con.prepareStatement("select sum(amount_paid) as amount_paid, emp_id from investment_details where fy_from = ? and fy_to = ? " +
//				"and status = true and  section_id = 11  group by emp_id");
		pst = con.prepareStatement("select sum(amount_paid) as amount_paid, emp_id from investment_details where fy_from =? and fy_to =? and status = true" +
						" and trail_status = 1 and parent_section=0 and  section_id in (select section_id from section_details where section_id = 11 and financial_year_start=? " +
						"and financial_year_end=?) group by emp_id");
		pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
		pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
		pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
		pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
		rs = pst.executeQuery();
		while (rs.next()) {
			
			if(uF.parseToDouble(rs.getString("amount_paid"))>dblLoanExemptionLimit){
				hmEmpHomeLoanMap.put(rs.getString("emp_id"), dblLoanExemptionLimit+"");
			}else{
				hmEmpHomeLoanMap.put(rs.getString("emp_id"), rs.getString("amount_paid"));
			}
		}
		rs.close();
		pst.close();
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
	return hmEmpHomeLoanMap;

}


	
	public Map<String, String> getEmpPaidAmountDetails(Connection con, UtilityFunctions uF, String strFinancialYearStart, String strFinancialYearEnd){
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String, String> hmEmpPaidAmountDetails = new HashMap<String, String>();
		
		try {
			pst = con.prepareStatement("select amount, emp_id, month,salary_head_id from payroll_generation where financial_year_from_date = ? and financial_year_to_date = ? and salary_head_id =?");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(3, TDS);
			rs = pst.executeQuery();
			while (rs.next()) {
				hmEmpPaidAmountDetails.put(rs.getString("emp_id"), rs.getString("amount"));
			}
			rs.close();
			pst.close();
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
		return hmEmpPaidAmountDetails;
	
	}
	
	private void getSelectedFilter(UtilityFunctions uF) {
		Map<String,String> hmFilter=new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();
		
		alFilter.add("FINANCIALYEAR");
		String[] strFinancialYears = null;
		if (getFinancialYear() != null && !getFinancialYear().trim().equals("") && !getFinancialYear().trim().equalsIgnoreCase("NULL")) {
			String str = URLDecoder.decode(getFinancialYear());
			setFinancialYear(str);
			strFinancialYears = getFinancialYear().split("-");
			setFinancialYear(strFinancialYears[0] + "-" + strFinancialYears[1]);
		} else {
			strFinancialYears = CF.getFinancialYear(uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT), CF, uF);
			setFinancialYear(strFinancialYears[0] + "-" + strFinancialYears[1]);
		}
		hmFilter.put("FINANCIALYEAR", uF.getDateFormat(strFinancialYears[0], DATE_FORMAT, CF.getStrReportDateFormat()) +" - "+ uF.getDateFormat(strFinancialYears[1], DATE_FORMAT, CF.getStrReportDateFormat()));
		
		alFilter.add("ORGANISATION");
		if(getF_org()!=null) {
			String strOrg="";
			for(int i=0;organisationList!=null && i<organisationList.size();i++) {
				if(getF_org().equals(organisationList.get(i).getOrgId())) {
					strOrg=organisationList.get(i).getOrgName();
				}
			}
			if(strOrg!=null && !strOrg.equals("")) {
				hmFilter.put("ORGANISATION", strOrg);
			} else {
				hmFilter.put("ORGANISATION", "All Organisation");
			}
		} else {
			hmFilter.put("ORGANISATION", "All Organisation");
		}
		
		alFilter.add("LOCATION");
		if(getF_strWLocation()!=null) {
			String strLocation="";
			int k=0;
			for(int i=0;wLocationList!=null && i<wLocationList.size();i++) {
				for(int j=0; j < getF_strWLocation().length; j++){
					if(getF_strWLocation()[j].equals(wLocationList.get(i).getwLocationId())) {
						if(k==0) {
							strLocation=wLocationList.get(i).getwLocationName();
						} else {
							strLocation+=", "+wLocationList.get(i).getwLocationName();
						}
						k++;
					}
				}
			}
			if(strLocation!=null && !strLocation.equals("")) {
				hmFilter.put("LOCATION", strLocation);
			} else {
				hmFilter.put("LOCATION", "All Locations");
			}
		} else {
			hmFilter.put("LOCATION", "All Locations");
		}
		
		alFilter.add("DEPARTMENT");
		if(getF_department()!=null) {
			String strDepartment="";
			int k=0;
			for(int i=0;departmentList!=null && i<departmentList.size();i++) {
				for(int j=0;j<getF_department().length;j++) {
					if(getF_department()[j].equals(departmentList.get(i).getDeptId())) {
						if(k==0) {
							strDepartment=departmentList.get(i).getDeptName();
						} else {
							strDepartment+=", "+departmentList.get(i).getDeptName();
						}
						k++;
					}
				}
			}
			if(strDepartment!=null && !strDepartment.equals("")) {
				hmFilter.put("DEPARTMENT", strDepartment);
			} else {
				hmFilter.put("DEPARTMENT", "All Departments");
			}
		} else {
			hmFilter.put("DEPARTMENT", "All Departments");
		}
	
		alFilter.add("LEVEL");
		if(getF_level()!=null){
			String strLevel="";
			int k=0;
			for(int i=0;levelList!=null && i<levelList.size();i++){
				for(int j=0;j<getF_level().length;j++){
					if(getF_level()[j].equals(levelList.get(i).getLevelId())){
						if(k==0){
							strLevel=levelList.get(i).getLevelCodeName();
						}else{
							strLevel+=", "+levelList.get(i).getLevelCodeName();
						}
						k++;
					}
				}
			}
			if(strLevel!=null && !strLevel.equals("")){
				hmFilter.put("LEVEL", strLevel);
			}else{
				hmFilter.put("LEVEL", "All Levels");
			}
		}else{
			hmFilter.put("LEVEL", "All Levels");
		}
		
		alFilter.add("PAYMENTMODE");
		if(getF_paymentMode()!=null){			
			String strPayMode="";
			int k=0;
			for(int i=0;paymentModeList!=null && i<paymentModeList.size();i++){
				if(getF_paymentMode().equals(paymentModeList.get(i).getPayModeId())){
					if(k==0){
						strPayMode=paymentModeList.get(i).getPayModeName();
					}else{
						strPayMode+=", "+paymentModeList.get(i).getPayModeName();
					}
					k++;
				}
			}
			if(strPayMode!=null && !strPayMode.equals("")){
				hmFilter.put("PAYMENTMODE", strPayMode);
			}else{
				hmFilter.put("PAYMENTMODE", "All Payment Mode");
			}
		}else{
			hmFilter.put("PAYMENTMODE", "All Payment Mode");
		}
		
		alFilter.add("EMPTYPE");
		if (getF_employeType() != null) {
			String stremptype = "";
			int k = 0;
			for (int i = 0; employementTypeList != null && i < employementTypeList.size(); i++) {
				for (int j = 0; j < getF_employeType().length; j++) {
					if (getF_employeType()[j].equals(employementTypeList.get(i).getEmpTypeId())) {
						if (k == 0) {
							stremptype = employementTypeList.get(i).getEmpTypeName();
						} else {
							stremptype += ", " + employementTypeList.get(i).getEmpTypeName();
						}
						k++;
					}
				}
			}
			if (stremptype != null && !stremptype.equals("")) {
				hmFilter.put("EMPTYPE", stremptype);
			} else {
				hmFilter.put("EMPTYPE", "All Employee Type");
			}
		} else {
			hmFilter.put("EMPTYPE", "All Employee Type");
		}
		
		alFilter.add("GRADE");
		if (getF_grade() != null) {
			String strgrade = "";
			int k = 0;
			for (int i = 0; gradeList != null && i < gradeList.size(); i++) {
				for (int j = 0; j < getF_grade().length; j++) {
					if (getF_grade()[j].equals(gradeList.get(i).getGradeId())) {
						if (k == 0) {
							strgrade = gradeList.get(i).getGradeCode();
						} else {
							strgrade += ", " + gradeList.get(i).getGradeCode();
						}
						k++;
					}
				}
			}
			if (strgrade != null && !strgrade.equals("")) {
				hmFilter.put("GRADE", strgrade);
			} else {
				hmFilter.put("GRADE", "All Grade's");
			}
		} else {
			hmFilter.put("GRADE", "All Grade's");
		}
		
	String selectedFilter=CF.getSelectedFilter2(CF,uF,alFilter,hmFilter);
	request.setAttribute("selectedFilter", selectedFilter);
  }
	
	public void genratedexcel(UtilityFunctions uF,String empId)
	{
		Connection con = null;
		PreparedStatement pst=null;
		PreparedStatement pst1 = null;
		ResultSet rs = null;
		ResultSet rs1 = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			String[] strFinancialYearDates = null;
			String strFinancialYearStart = null;
			String strFinancialYearEnd = null;

			if (getFinancialYear() != null) {
				String str = URLDecoder.decode(getFinancialYear());
				setFinancialYear(str);
				strFinancialYearDates = getFinancialYear().split("-");
				strFinancialYearStart = strFinancialYearDates[0];
				strFinancialYearEnd = strFinancialYearDates[1];
				setFinancialYear(getFinancialYear());
			} else {
				strFinancialYearDates = new FillFinancialYears(request).fillLatestFinancialYears();
				setFinancialYear(strFinancialYearDates[0] + "-" + strFinancialYearDates[1]);
				strFinancialYearStart = strFinancialYearDates[0];
				strFinancialYearEnd = strFinancialYearDates[1];
			}
			request.setAttribute("strD1", strFinancialYearDates[0]);
			request.setAttribute("strD2", strFinancialYearDates[1]);
			
			con = db.makeConnection(con);
			
			Map<String, String> hmEmpMap = CF.getEmpNameMap(con,null, null);
			Map<String, String> hmEmpCodeMap = CF.getEmpCodeMap(con);
			
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.DATE, uF.parseToInt(uF.getDateFormat(strFinancialYearStart, DATE_FORMAT, "dd")));
			cal.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(strFinancialYearStart, DATE_FORMAT, "MM"))-1);
			cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strFinancialYearStart, DATE_FORMAT, "yyyy")));
			
			List<String> alMonth = new ArrayList<String>();
			for(int i=0; i<12;i++){
				String strDate = cal.get(Calendar.DATE)+"/"+(cal.get(Calendar.MONTH) + 1)+"/"+cal.get(Calendar.YEAR);
				alMonth.add(uF.getDateFormat(strDate, DATE_FORMAT, "MM"));
				cal.add(Calendar.MONTH, 1);
			}					
			List<String> alEmp = new ArrayList<String>();
			Map<String, String> hmTDSEmp = new HashMap<String, String>();
			if(empId!=null){
				pst = con.prepareStatement("select * from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id and emp_per_id=?");
				pst.setInt(1,uF.parseToInt(empId));

			}else{
				StringBuilder sbQuery = new StringBuilder();
				sbQuery.append("select * from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id");
				/*if(getF_level()!=null && getF_level().length>0){
	                sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
	            }*/
				  if(getF_grade()!=null && getF_grade().length>0 && getF_level()!=null && getF_level().length>0)
		            {
		            	sbQuery.append(" and grade_id in ("+StringUtils.join(getF_grade(), ",")+" ) ");
		            	//sbQuery.append(" and grade_id in ( " +StringUtils.join(getF_grade(), ",")+" ) and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");	
		            }else {
		            	 if(getF_level()!=null && getF_level().length>0){
		                     sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
		                 }
		            	 if(getF_grade()!=null && getF_grade().length>0){
		                     sbQuery.append(" and grade_id in ("+StringUtils.join(getF_grade(), ",")+" ) ");
		                 }
					}	
	            if(getF_department()!=null && getF_department().length>0){
	                sbQuery.append(" and depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
	            }
	            
	           /* if(getF_service()!=null && getF_service().length>0){
	                sbQuery.append(" and (");
	                for(int i=0; i<getF_service().length; i++){
	                    sbQuery.append(" eod.service_id like '%"+getF_service()[i]+",%'");
	                    
	                    if(i<getF_service().length-1){
	                        sbQuery.append(" OR "); 
	                    }
	                }
	                sbQuery.append(" ) ");
	            } */
	            
	            if(getF_strWLocation()!=null && getF_strWLocation().length>0){
	                sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
	            }else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && session.getAttribute(WLOCATION_ACCESS)!=null){
					sbQuery.append(" and wlocation_id in ("+session.getAttribute(WLOCATION_ACCESS)+")");
				}
	            
	            if(uF.parseToInt(getF_org())>0){
					sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
				}else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && session.getAttribute(ORG_ACCESS)!=null){
					sbQuery.append(" and org_id in ("+session.getAttribute(ORG_ACCESS)+")");
				}
				
				if(uF.parseToInt(getF_paymentMode())>0){
					sbQuery.append(" and payment_mode = "+uF.parseToInt(getF_paymentMode()));	
				}
				sbQuery.append(" and eod.emp_id in(select emp_id from emp_salary_details where salary_head_id="+TDS+" and isdisplay=true and is_approved=true)");
				sbQuery.append(" and (epd.employment_end_date is null or epd.employment_end_date<=?)");
				sbQuery.append(" order by emp_fname, emp_lname");
				pst = con.prepareStatement(sbQuery.toString());
				pst.setDate(1, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				//System.out.println("Pst===>"+pst);
			}
			rs = pst.executeQuery();
			while(rs.next()){
				String strEmpId = rs.getString("emp_per_id");
				alEmp.add(strEmpId);
				ViewEmpTDSProjection empTDSProjection = new ViewEmpTDSProjection();
				empTDSProjection.request = request;
				empTDSProjection.session = session;
				empTDSProjection.CF = CF;
				empTDSProjection.setStrEmpId(strEmpId);
				empTDSProjection.setStrFinancialYearStart(strFinancialYearStart);
				empTDSProjection.setStrFinancialYearEnd(strFinancialYearEnd);
				empTDSProjection.getEmpTDSProjection(uF);
				Map<String, String> hmTDSEmp1 = (Map<String, String>)request.getAttribute("hmTDSEmp1");
				if(hmTDSEmp1 == null) hmTDSEmp1 = new HashMap<String, String>();
				Iterator<String> it = hmTDSEmp1.keySet().iterator(); 
				while(it.hasNext()){
					String key = it.next();
					hmTDSEmp.put(key, hmTDSEmp1.get(key));
				}
			}
			rs.close();
			pst.close();
			try {
				HSSFWorkbook workbook = new HSSFWorkbook();
				HSSFSheet sheet = workbook.createSheet("TDS Projection");
				List<DataStyle> header = new ArrayList<DataStyle>();
				header.add(new DataStyle("Sr. No.",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				header.add(new DataStyle("Employee Code",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				header.add(new DataStyle("Employee Name",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				header.add(new DataStyle("Amount",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				header.add(new DataStyle("Month",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				
				List<List<DataStyle>> reportData = new ArrayList<List<DataStyle>>();
				for (int i = 0; i < alEmp.size(); i++) {			
					 List<DataStyle> alInnerExport = new ArrayList<DataStyle>();
					    alInnerExport.add(new DataStyle(String.valueOf(i+1),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
						alInnerExport.add(new DataStyle(hmEmpCodeMap.get(alEmp.get(i)),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
						alInnerExport.add(new DataStyle(hmEmpMap.get(alEmp.get(i)),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
						alInnerExport.add(new DataStyle("0",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
						alInnerExport.add(new DataStyle(" ",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
						reportData.add(alInnerExport);
				}
				ExcelSheetDesign sheetDesign = new ExcelSheetDesign();
				sheetDesign.generateDefualtExcelSheet(workbook, sheet, header, reportData);		
				ByteArrayOutputStream buffer = new ByteArrayOutputStream();
				workbook.write(buffer);
				response.setContentType("application/vnd.ms-excel:UTF-8");
				response.setContentLength(buffer.size());
				response.setHeader("Content-Disposition", "attachment; filename=ImportTdsProjection"+".xls");
				ServletOutputStream out = response.getOutputStream();
				buffer.writeTo(out);
				out.flush();
				buffer.close();
				out.close();
			} catch (Exception e) {
				// TODO: handle exception
			}  finally {
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs1);
			db.closeResultSet(rs);
			db.closeStatements(pst1);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	private HttpServletResponse response;

	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
	}
	
	
	
	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}
	
	public String getFinancialYear() {
		return financialYear;
	}
	public void setFinancialYear(String financialYear) {
		this.financialYear = financialYear;
	}
	
	public List<FillPayCycles> getPaycycleList() {
		return paycycleList;
	}
	
	public void setPaycycleList(List<FillPayCycles> paycycleList) {
		this.paycycleList = paycycleList;
	}
	
	public List<FillFinancialYears> getFinancialYearList() {
		return financialYearList;
	}
	public void setFinancialYearList(List<FillFinancialYears> financialYearList) {
		this.financialYearList = financialYearList;
	}
	public List<FillLevel> getLevelList() {
		return levelList;
	}
	public void setLevelList(List<FillLevel> levelList) {
		this.levelList = levelList;
	}
	
	public List<FillWLocation> getwLocationList() {
		return wLocationList;
	}
	
	public void setwLocationList(List<FillWLocation> wLocationList) {
		this.wLocationList = wLocationList;
	}
	
	public List<FillDepartment> getDepartmentList() {
		return departmentList;
	}
	
	public void setDepartmentList(List<FillDepartment> departmentList) {
		this.departmentList = departmentList;
	}
	
	public List<FillServices> getServiceList() {
		return serviceList;
	}
	
	public void setServiceList(List<FillServices> serviceList) {
		this.serviceList = serviceList;
	}
	
	public List<FillPayMode> getPaymentModeList() {
		return paymentModeList;
	}
	
	public void setPaymentModeList(List<FillPayMode> paymentModeList) {
		this.paymentModeList = paymentModeList;
	}
	
	public String getF_paymentMode() {
		return f_paymentMode;
	}
	
	public void setF_paymentMode(String f_paymentMode) {
		this.f_paymentMode = f_paymentMode;
	}
	
	public List<FillOrganisation> getOrganisationList() {
		return organisationList;
	}
	
	public void setOrganisationList(List<FillOrganisation> organisationList) {
		this.organisationList = organisationList;
	}
	
	public String getF_org() {
		return f_org;
	}
	
	public void setF_org(String f_org) {
		this.f_org = f_org;
	}
	
	public String getSubmit() {
		return submit;
	}
	
	public void setSubmit(String submit) {
		this.submit = submit;
	}
	
	public String[] getStrEmpId_strMonth() {
		return strEmpId_strMonth;
	}
	
	public void setStrEmpId_strMonth(String[] strEmpId_strMonth) {
		this.strEmpId_strMonth = strEmpId_strMonth;
	}
	
	public String[] getStrTDS() {
		return strTDS;
	}
	
	public void setStrTDS(String[] strTDS) {
		this.strTDS = strTDS;
	}
	
	public List<FillFinancialYears> getStrFinancialYearList() {
		return strFinancialYearList;
	}
	
	public void setStrFinancialYearList(List<FillFinancialYears> strFinancialYearList) {
		this.strFinancialYearList = strFinancialYearList;
	}
	
	public String getStrAmount() {
		return strAmount;
	}
	
	public void setStrAmount(String strAmount) {
		this.strAmount = strAmount;
	}

	public String[] getF_strWLocation() {
		return f_strWLocation;
	}
	
	public void setF_strWLocation(String[] f_strWLocation) {
		this.f_strWLocation = f_strWLocation;
	}
	
	public String[] getF_level() {
		return f_level;
	}

	public void setF_level(String[] f_level) {
		this.f_level = f_level;
	}

	public String[] getF_department() {
		return f_department;
	}

	public void setF_department(String[] f_department) {
		this.f_department = f_department;
	}
	
	public String getStrLocation() {
		return strLocation;
	}
	
	public void setStrLocation(String strLocation) {
		this.strLocation = strLocation;
	}
	
	public String getStrDepartment() {
		return strDepartment;
	}
	
	public void setStrDepartment(String strDepartment) {
		this.strDepartment = strDepartment;
	}
	
	public String getStrLevel() {
		return strLevel;
	}
	
	public void setStrLevel(String strLevel) {
		this.strLevel = strLevel;
	}

	public String[] getF_employeType() {
		return f_employeType;
	}

	public void setF_employeType(String[] f_employeType) {
		this.f_employeType = f_employeType;
	}

	public String[] getF_grade() {
		return f_grade;
	}

	public void setF_grade(String[] f_grade) {
		this.f_grade = f_grade;
	}

	public List<FillEmploymentType> getEmployementTypeList() {
		return employementTypeList;
	}

	public void setEmployementTypeList(List<FillEmploymentType> employementTypeList) {
		this.employementTypeList = employementTypeList;
	}

	public List<FillGrade> getGradeList() {
		return gradeList;
	}

	public void setGradeList(List<FillGrade> gradeList) {
		this.gradeList = gradeList;
	}

	public String getProPage() {
		return proPage;
	}

	public void setProPage(String proPage) {
		this.proPage = proPage;
	}

	public String getMinLimit() {
		return minLimit;
	}

	public void setMinLimit(String minLimit) {
		this.minLimit = minLimit;
	}

	public String getStrSearch() {
		return strSearch;
	}

	public void setStrSearch(String strSearch) {
		this.strSearch = strSearch;
	}

	public String getExceldownload() {
		return exceldownload;
	}

	public void setExceldownload(String exceldownload) {
		this.exceldownload = exceldownload;
	}

	public String getStrEmployeType() {
		return strEmployeType;
	}

	public void setStrEmployeType(String strEmployeType) {
		this.strEmployeType = strEmployeType;
	}

	public String getStrGrade() {
		return strGrade;
	}

	public void setStrGrade(String strGrade) {
		this.strGrade = strGrade;
	}
	
	
}