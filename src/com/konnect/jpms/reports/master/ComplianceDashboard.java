package com.konnect.jpms.reports.master;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.konnect.jpms.payroll.ViewEmpTDSProjection;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillUnderSection;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class ComplianceDashboard extends ActionSupport implements  ServletRequestAware, ServletResponseAware, IStatements{

	private static final long serialVersionUID = -5846636523966720273L;
	
    HttpSession session;
	CommonFunctions CF = null;
	String strUserType = null;
	private String f_org;
	
	private List<FillLevel> levelList;
	private String strLevel;
	
	String userscreen;
	String navigationId;
	String toPage;
	String minLimit;

public String execute(){
		
		//System.out.println("in viewComplianceDashboard class");
		session = request.getSession();  
		
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN; 
		strUserType = (String) session.getAttribute(USERTYPE);
		UtilityFunctions uF = new UtilityFunctions();
		
		request.setAttribute(TITLE, "ComplianceDashboard");
		request.setAttribute(PAGE, "/jsp/reports/master/ComplianceDashboard.jsp");
		
		if(strUserType==null || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && !strUserType.equalsIgnoreCase(ACCOUNTANT)
			&& !strUserType.equalsIgnoreCase(MANAGER) && !strUserType.equalsIgnoreCase(HRMANAGER) && !strUserType.equalsIgnoreCase(CEO))) {
			
			request.setAttribute(PAGE, PAccessDenied);
			request.setAttribute(TITLE, TAccessDenied); 
			return ACCESS_DENIED;
		}
		if(uF.parseToInt(getF_org()) == 0) {
			setF_org((String)session.getAttribute(ORGID));
		}
		
		levelList = new FillLevel(request).fillLevel(uF.parseToInt(getF_org()));
		
		if(uF.parseToInt(getStrLevel()) == 0 && levelList.size() > 0){
			setStrLevel(levelList.get(0).getLevelId());
		}
		
		//System.out.println("f_org=="+f_org);
		viewITDeclartionCount(uF);
		
		return SUCCESS;
	}

	public String viewITDeclartionCount(UtilityFunctions uF){
	
		//System.out.println("hii in viewComplianceDashboard function");
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
	
		Calendar cal = Calendar.getInstance();
		
		int CurrentYear = cal.get(Calendar.YEAR);
		//System.out.println("endyear=="+CurrentYear);
		int CurrentMonth = cal.get(Calendar.MONTH);
			CurrentMonth=CurrentMonth+1;
		int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
		
		String todayDate=dayOfMonth+"/"+CurrentMonth+"/"+CurrentYear;
	
		int endyear =CurrentYear+1; 
		cal.set(Calendar.MONTH, 2 );
		int maxDays = cal.getActualMaximum(Calendar.DATE);
		
		String startyearDate=01+"/"+04+"/"+CurrentYear;//01/04/2019
		String endYearDate=maxDays+"/"+03+"/"+endyear;//31/04/2020
		
		cal.set(Calendar.DATE, uF.parseToInt(uF.getDateFormat(startyearDate, DATE_FORMAT, "dd")));
		cal.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(startyearDate, DATE_FORMAT, "MM"))-1);
		cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(startyearDate, DATE_FORMAT, "yyyy")));
		
		List<String> alMonth = new ArrayList<String>();
		for(int i=0; i<12;i++){
			String strDate = cal.get(Calendar.DATE)+"/"+(cal.get(Calendar.MONTH) + 1)+"/"+cal.get(Calendar.YEAR);
			alMonth.add(uF.getDateFormat(strDate, DATE_FORMAT, "MM"));
			cal.add(Calendar.MONTH, 1);
		}
		
		
		//System.out.println("startyearDate=="+startyearDate+"endYearDate=="+endYearDate);
		
		int prevStartyear=CurrentYear-1;//
		int PrevEndyear=endyear-1;//
		
		String prevStartyearDate=01+"/"+04+"/"+prevStartyear;
		String prevEndYearDate=maxDays+"/"+03+"/"+PrevEndyear;
		//System.out.println("prevStartyearDate=="+prevStartyearDate+"prevEndYearDate=="+prevEndYearDate);
		try {
			con=db.makeConnection(con);
			int empTotalCount=0,empApprovedCount=0,empUnApprovedCount=0,empPendingCount=0;
			
			pst=con.prepareStatement("select count( distinct emp_id) as empTotalCount from employee_official_details eod,employee_personal_details epd where eod.emp_id=epd.emp_per_id and epd.joining_date <= ? and org_id=?");
			pst.setDate(1, uF.getDateFormat(todayDate, DATE_FORMAT));
			pst.setInt(2, uF.parseToInt(getF_org()));
			//System.out.println("pst1===>"+pst);
			rs=pst.executeQuery();
			while(rs.next()){
				empTotalCount=rs.getInt("empTotalCount");
			}
			rs.close();
			pst.close();
			
			pst=con.prepareStatement("select count( distinct id.emp_id) as empApprovedCount from investment_details id, employee_official_details eod, employee_personal_details epd where status='t' and agreed_date <= ? and org_id=? and eod.emp_id=epd.emp_per_id");
			pst.setDate(1, uF.getDateFormat(todayDate, DATE_FORMAT));
			pst.setInt(2, uF.parseToInt(getF_org()));
			//System.out.println("pst2===>"+pst);
			rs=pst.executeQuery();
			while(rs.next()){
				empApprovedCount=rs.getInt("empApprovedCount");
			}
			rs.close();
			pst.close();
			
			pst=con.prepareStatement("select count( distinct id.emp_id) as empUnApprovedCount from investment_details id, employee_official_details eod, employee_personal_details epd where status='f' and agreed_date <= ? and org_id=? and eod.emp_id=epd.emp_per_id");
			pst.setDate(1, uF.getDateFormat(todayDate, DATE_FORMAT));
			pst.setInt(2, uF.parseToInt(getF_org()));
			//System.out.println("pst3===>"+pst);
			rs=pst.executeQuery();
			while(rs.next()){
				empUnApprovedCount=rs.getInt("empUnApprovedCount");
			}
			rs.close();
			pst.close();
			empPendingCount=empTotalCount-(empApprovedCount+empUnApprovedCount);
			
			request.setAttribute("empApprovedCount", ""+empApprovedCount);
			request.setAttribute("empUnApprovedCount", ""+empUnApprovedCount);
			request.setAttribute("empPendingCount", ""+empPendingCount);

			ViewYTDPaidStatutaryCompliance(uF,con,startyearDate,endYearDate,prevStartyearDate,prevEndYearDate);
			statutaryComplianceReport(uF,con,startyearDate,endYearDate,prevStartyearDate,prevEndYearDate);
			TDSBarchar(uF,con,startyearDate,endYearDate,prevStartyearDate,prevEndYearDate, alMonth,todayDate);
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return LOAD;
	}

	public void ViewYTDPaidStatutaryCompliance(UtilityFunctions uF,Connection con,String startyearDate,String endYearDate,String prevStartyearDate,String prevEndYearDate){
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		try{
			
			Map<String,String>hmPtCurentYearamount=new LinkedHashMap<String,String>();
			double ptCurrentYearAmount=0.0;
			pst=con.prepareStatement("select sum(amount) as ptCurrentYearAmount, financial_year_from_date from payroll_generation where financial_year_from_date=? and financial_year_to_date=? and salary_head_id=11 and" +
					" emp_id in(select emp_id  from employee_official_details eod,employee_personal_details epd where eod.emp_id=epd.emp_per_id and org_id=?) " +
					"group by financial_year_from_date");

			pst.setDate(1, uF.getDateFormat(startyearDate, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(endYearDate, DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(getF_org()));
			rs=pst.executeQuery();
			//System.out.println("pst PT=="+pst);
			while(rs.next()){
				
				ptCurrentYearAmount=rs.getDouble("ptCurrentYearAmount");
				hmPtCurentYearamount.put(rs.getDate("financial_year_from_date").toString(), uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),(ptCurrentYearAmount)));
			}
			rs.close();
			pst.close();
			//System.out.println("ptCurrentYearAmount==>"+ptCurrentYearAmount);
			request.setAttribute("hmPtCurentYearamount", hmPtCurentYearamount);

			Map<String,String>hmPtPrevYearamount=new LinkedHashMap<String,String>();
			double ptPrevYearAmount=0.0;
			pst=con.prepareStatement("select sum(amount) as ptPrevYearAmount,financial_year_from_date from payroll_generation " +
					"where financial_year_from_date=? and financial_year_to_date=? and salary_head_id=11 and emp_id in(select emp_id  from employee_official_details eod,employee_personal_details epd where eod.emp_id=epd.emp_per_id and org_id=?) group by financial_year_from_date");
			pst.setDate(1, uF.getDateFormat(prevStartyearDate, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(prevEndYearDate, DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(getF_org()));
			rs=pst.executeQuery();
			//System.out.println("pst PT=="+pst);
			while(rs.next()){
				ptPrevYearAmount=rs.getDouble("ptPrevYearAmount");
				hmPtPrevYearamount.put(rs.getDate("financial_year_from_date").toString(), uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),(ptPrevYearAmount)));
			}
			rs.close();
			pst.close();
			//System.out.println("ptPrevYearAmount==>"+ptPrevYearAmount);
			request.setAttribute("hmPtPrevYearamount", hmPtPrevYearamount);

			
			
			Map<String,String>hmTdsCurrentYearamount=new LinkedHashMap<String,String>();
			double tdsCurrentYearAmount=0.0;
			pst=con.prepareStatement("select sum(actual_tds_amount) as tdsCurrentYearAmount ,financial_year_start from emp_tds_details " +
					"where financial_year_start=? and financial_year_end=? and emp_id in(select emp_id  from employee_official_details eod,employee_personal_details epd where eod.emp_id=epd.emp_per_id and org_id=?) group by financial_year_start");
			pst.setDate(1, uF.getDateFormat(startyearDate, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(endYearDate, DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(getF_org()));
			rs=pst.executeQuery();
			//System.out.println("pst curr tds=="+pst);
			while(rs.next()){
				tdsCurrentYearAmount=rs.getDouble("tdsCurrentYearAmount");
				hmTdsCurrentYearamount.put(rs.getDate("financial_year_start").toString(), uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),(tdsCurrentYearAmount)));
			}
			rs.close();
			pst.close();
			//System.out.println("tdsCurrentYearAmount==>"+tdsCurrentYearAmount);
			request.setAttribute("hmTdsCurrentYearamount", hmTdsCurrentYearamount);

			
			Map<String,String>hmTdsPrevYearamount=new LinkedHashMap<String,String>();
			double tdsPrevYearAmount=0.0;
			pst=con.prepareStatement("select sum(actual_tds_amount) as tdsPrevYearAmount,financial_year_start from emp_tds_details " +
					"where financial_year_start=? and financial_year_end=? and emp_id in(select emp_id  from employee_official_details eod,employee_personal_details epd where eod.emp_id=epd.emp_per_id and org_id=?) group by financial_year_start");
			pst.setDate(1, uF.getDateFormat(prevStartyearDate, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(prevEndYearDate, DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(getF_org()));
			rs=pst.executeQuery();
			//System.out.println("pst prev tds=="+pst);
			while(rs.next()){
				tdsPrevYearAmount=rs.getDouble("tdsPrevYearAmount");
				hmTdsPrevYearamount.put(rs.getDate("financial_year_start").toString(), uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),(tdsPrevYearAmount)));
			}
			rs.close();
			pst.close();
			//System.out.println("tdsPrevYearAmount==>"+tdsPrevYearAmount);
			request.setAttribute("hmTdsPrevYearamount", hmTdsPrevYearamount);

			
			Map<String,String>hmEpfcurrentYearamount=new LinkedHashMap<String,String>();
			StringBuilder sbQuery =new StringBuilder();
			sbQuery.append("select eed.eepf_contribution as eed_eepf_contribution," +
					"eed.evpf_contribution as eed_evpf_contribution, " +
					"eed.erpf_contribution as eed_erpf_contribution," +
					"eed.erdli_contribution as eed_erdli_contribution," +
					"eed.erps_contribution as eed_erps_contribution," +
					"eed.pf_admin_charges as eed_pf_admin_charges," +
					"eed.edli_admin_charges as eed_edli_admin_charges," +
					"ed.is_erpf_contribution as ed_is_erpf_contribution," +
					"ed.is_erdli_contribution as ed_is_erdli_contribution," +
					"ed.is_erps_contribution as ed_is_erps_contribution ," +
					"ed.is_pf_admin_charges as ed_is_pf_admin_charges," +
					"ed.is_edli_admin_charges as ed_is_edli_admin_charges," +
					"eed.financial_year_start as eed_financial_year_start" +
					" from emp_epf_details eed, epf_details ed" +
					" where eed.financial_year_start=ed.financial_year_start and eed.financial_year_end=ed.financial_year_end " +
					" and eed.financial_year_start=? and eed.financial_year_end=? " +
					" and emp_id in(select distinct emp_id  from employee_official_details eod,employee_personal_details epd where eod.emp_id=  epd.emp_per_id and org_id= ?)");
			
			pst=con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(startyearDate, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(endYearDate, DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(getF_org()));
			//System.out.println("pst EPF2===>"+pst);
			rs=pst.executeQuery();
			double epfCurrentamount=0.0;
			while(rs.next()){
			
				double grossAmount=rs.getDouble("eed_eepf_contribution");
				
				grossAmount=grossAmount+rs.getDouble("eed_evpf_contribution");
				
				if(rs.getString("ed_is_erpf_contribution").equalsIgnoreCase("t")){
					grossAmount=grossAmount+rs.getDouble("eed_erpf_contribution");
				}
				if(rs.getString("ed_is_erdli_contribution").equalsIgnoreCase("t")){
					grossAmount=grossAmount+rs.getDouble("eed_erdli_contribution");
				}
				if(rs.getString("ed_is_erps_contribution").equalsIgnoreCase("t")){
					grossAmount=grossAmount+rs.getDouble("eed_erps_contribution");
				}
				if(rs.getString("ed_is_pf_admin_charges").equalsIgnoreCase("t")){
					grossAmount=grossAmount+rs.getDouble("eed_pf_admin_charges");
				}
				if(rs.getString("ed_is_edli_admin_charges").equalsIgnoreCase("t")){
					grossAmount=grossAmount+rs.getDouble("eed_edli_admin_charges");
				}
				
				epfCurrentamount=epfCurrentamount+grossAmount;
				hmEpfcurrentYearamount.put(rs.getDate("eed_financial_year_start").toString(), uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),(epfCurrentamount)));
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmEpfcurrentYearamount", hmEpfcurrentYearamount);
			
			Map<String,String>hmEpfPrevYearamount=new LinkedHashMap<String,String>();
			sbQuery =new StringBuilder();
			sbQuery.append("select eed.eepf_contribution as eed_eepf_contribution," +
					"eed.evpf_contribution as eed_evpf_contribution, " +
					"eed.erpf_contribution as eed_erpf_contribution," +
					"eed.erdli_contribution as eed_erdli_contribution," +
					"eed.erps_contribution as eed_erps_contribution," +
					"eed.pf_admin_charges as eed_pf_admin_charges," +
					"eed.edli_admin_charges as eed_edli_admin_charges," +
					"ed.is_erpf_contribution as ed_is_erpf_contribution," +
					"ed.is_erdli_contribution as ed_is_erdli_contribution," +
					"ed.is_erps_contribution as ed_is_erps_contribution ," +
					"ed.is_pf_admin_charges as ed_is_pf_admin_charges," +
					"ed.is_edli_admin_charges as ed_is_edli_admin_charges," +
					"eed.financial_year_start as eed_financial_year_start" +
					" from emp_epf_details eed, epf_details ed" +
					" where eed.financial_year_start=ed.financial_year_start and eed.financial_year_end=ed.financial_year_end " +
					" and eed.financial_year_start=? and eed.financial_year_end=? " +
					" and emp_id in(select distinct emp_id  from employee_official_details eod,employee_personal_details epd where eod.emp_id=  epd.emp_per_id and org_id= ?)");
			
			pst=con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(prevStartyearDate, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(prevEndYearDate, DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(getF_org()));
			//System.out.println("pst EPF===>"+pst);
			rs=pst.executeQuery();
			double epfPrevamount=0.0;
			while(rs.next()){
			
				double grossAmount=rs.getDouble("eed_eepf_contribution");
				
				grossAmount=grossAmount+rs.getDouble("eed_evpf_contribution");
				
				if(rs.getString("ed_is_erpf_contribution").equalsIgnoreCase("t")){
					grossAmount=grossAmount+rs.getDouble("eed_erpf_contribution");
				}
				if(rs.getString("ed_is_erdli_contribution").equalsIgnoreCase("t")){
					grossAmount=grossAmount+rs.getDouble("eed_erdli_contribution");
				}
				if(rs.getString("ed_is_erps_contribution").equalsIgnoreCase("t")){
					grossAmount=grossAmount+rs.getDouble("eed_erps_contribution");
				}
				if(rs.getString("ed_is_pf_admin_charges").equalsIgnoreCase("t")){
					grossAmount=grossAmount+rs.getDouble("eed_pf_admin_charges");
				}
				if(rs.getString("ed_is_edli_admin_charges").equalsIgnoreCase("t")){
					grossAmount=grossAmount+rs.getDouble("eed_edli_admin_charges");
				}
				epfPrevamount=epfPrevamount+grossAmount;
				hmEpfPrevYearamount.put(rs.getDate("eed_financial_year_start").toString(), uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),(epfPrevamount)));
			}
			rs.close();
			pst.close();
			request.setAttribute("hmEpfPrevYearamount", hmEpfPrevYearamount);
			
			Map<String,String>hmEsiCurrentYearamount=new LinkedHashMap<String,String>();
			pst=con.prepareStatement("select * from emp_esi_details where financial_year_start=? and financial_year_end=? and emp_id in(select emp_id  from employee_official_details eod,employee_personal_details epd where eod.emp_id=epd.emp_per_id and org_id=?)");
			pst.setDate(1, uF.getDateFormat(startyearDate, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(endYearDate, DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(getF_org()));
			//System.out.println("pst==esi current=="+pst);
			rs=pst.executeQuery();
			double esiCurrentAmount=0.0;
			while(rs.next()){
				double amount=rs.getDouble("eesi_contribution");
				amount=amount+rs.getDouble("ersi_contribution");
				esiCurrentAmount=esiCurrentAmount+amount;
				hmEsiCurrentYearamount.put(rs.getDate("financial_year_start").toString(), uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),(esiCurrentAmount)));
			}
			rs.close();
			pst.close();
			//System.out.println("esiCurrentAmount==>"+esiCurrentAmount);
			request.setAttribute("hmEsiCurrentYearamount", hmEsiCurrentYearamount);
			
			
			Map<String,String>hmEsiPrevYearamount=new LinkedHashMap<String,String>();
			pst=con.prepareStatement("select * from emp_esi_details where financial_year_start=? and financial_year_end=? and emp_id in(select emp_id  from employee_official_details eod,employee_personal_details epd where eod.emp_id=epd.emp_per_id and org_id=?)");
			pst.setDate(1, uF.getDateFormat(prevStartyearDate, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(prevEndYearDate, DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(getF_org()));
			//System.out.println("pst esi prev=="+pst);
			rs=pst.executeQuery();
			double esiPrevAmount=0.0;
			
			while(rs.next()){
				double amount=rs.getDouble("eesi_contribution");
				amount=amount+rs.getDouble("ersi_contribution");
				esiPrevAmount=esiPrevAmount+amount;
				hmEsiPrevYearamount.put(rs.getDate("financial_year_start").toString(), uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),(esiPrevAmount)));
			}
			rs.close();
			pst.close();
			//System.out.println("esiPrevAmount==>"+esiPrevAmount);
			request.setAttribute("hmEsiPrevYearamount", hmEsiPrevYearamount);
			
			
			
			Map<String,String>hmLwfCurrentYearamount=new LinkedHashMap<String,String>();
			pst=con.prepareStatement("select * from emp_lwf_details where financial_year_start=? and financial_year_end=? and emp_id in(select emp_id  from employee_official_details eod,employee_personal_details epd where eod.emp_id=epd.emp_per_id and org_id=?)");
			pst.setDate(1, uF.getDateFormat(startyearDate, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(endYearDate, DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(getF_org()));
			//System.out.println("pst==lwf current=="+pst);
			rs=pst.executeQuery();
			double lwfCurrentAmount=0.0;
			while(rs.next()){
				double amount=rs.getDouble("eelwf_contribution");
				amount=amount+rs.getDouble("erlwf_contribution");
				lwfCurrentAmount=lwfCurrentAmount+amount;
				hmLwfCurrentYearamount.put(rs.getDate("financial_year_start").toString(), uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),(lwfCurrentAmount)));
			}
			rs.close();
			pst.close();
			//System.out.println("lwfCurrentAmount==>"+lwfCurrentAmount);
			request.setAttribute("hmLwfCurrentYearamount", hmLwfCurrentYearamount);
			
			
			Map<String,String>hmLwfPrevYearamount=new LinkedHashMap<String,String>();
			pst=con.prepareStatement("select * from emp_lwf_details where financial_year_start=? and financial_year_end=? and emp_id in(select emp_id  from employee_official_details eod,employee_personal_details epd where eod.emp_id=epd.emp_per_id and org_id=?)");
			pst.setDate(1, uF.getDateFormat(prevStartyearDate, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(prevEndYearDate, DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(getF_org()));
			//System.out.println("pst lwf prev=="+pst);
			rs=pst.executeQuery();
			double lwfPrevAmount=0.0;
			
			while(rs.next()){
				double amount=rs.getDouble("eelwf_contribution");
				amount=amount+rs.getDouble("eelwf_contribution");
				lwfPrevAmount=lwfPrevAmount+amount;
				hmLwfPrevYearamount.put(rs.getDate("financial_year_start").toString(), uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),(lwfPrevAmount)));
			}
			rs.close();
			pst.close();
			//System.out.println("lwfPrevAmount==>"+lwfPrevAmount);
			request.setAttribute("hmLwfPrevYearamount", hmLwfPrevYearamount);
		
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if (pst != null) {
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}
		
	}
	
	public void statutaryComplianceReport(UtilityFunctions uF,Connection con,String startyearDate,String endYearDate,String prevStartyearDate,String prevEndYearDate){
		PreparedStatement pst = null;
		ResultSet rs = null;
		try{
			List<List<String>> alM = new ArrayList<List<String>>();
			List<List<String>> alF = new ArrayList<List<String>>();
			
			pst = con.prepareStatement(selectDeductionRIndia);
			pst.setDate(1, uF.getDateFormat(startyearDate, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(endYearDate, DATE_FORMAT));
			rs = pst.executeQuery();
			while(rs.next()){
				List<String> alInner = new ArrayList<String>();
				alInner.add(rs.getString("deduction_id"));
				alInner.add(rs.getString("income_from"));
				alInner.add(rs.getString("income_to"));
				alInner.add(rs.getString("deduction_paycycle"));
				alInner.add(rs.getString("deduction_amount"));
				alInner.add(rs.getString("state_name"));
				
				if(rs.getString("gender")!=null && rs.getString("gender").equalsIgnoreCase("M")){
					alM.add(alInner);
				}else if(rs.getString("gender")!=null && rs.getString("gender").equalsIgnoreCase("F")){
					alF.add(alInner);
				}
			}
			rs.close();
			pst.close();
			
			request.setAttribute("reportListM", alM);
			request.setAttribute("reportListF", alF);
			
	//********************deductionReportTax*********************	
			List<List<String>> alMTax = new ArrayList<List<String>>();
			List<List<String>> alFTax = new ArrayList<List<String>>();
			List<String> alInnerTax = new ArrayList<String>();
			
			pst = con.prepareStatement(selectDeductionRTax);
			pst.setDate(1, uF.getDateFormat(startyearDate, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(endYearDate, DATE_FORMAT));
			rs = pst.executeQuery();
			while(rs.next()){
				alInnerTax = new ArrayList<String>();
				alInnerTax.add(rs.getString("deduction_tax_id"));
				alInnerTax.add(rs.getString("age_from"));
				alInnerTax.add(rs.getString("age_to"));
				alInnerTax.add(uF.charMappingMaleFemale(rs.getString("gender")));
				alInnerTax.add(rs.getString("_from"));
				alInnerTax.add(rs.getString("_to"));
				alInnerTax.add(rs.getString("deduction_amount"));
				alInnerTax.add(uF.charMapping(rs.getString("deduction_type")));
				
				if(rs.getString("gender")!=null && rs.getString("gender").equalsIgnoreCase("M")){
					alMTax.add(alInnerTax);
				}else if(rs.getString("gender")!=null && rs.getString("gender").equalsIgnoreCase("F")){
					alFTax.add(alInnerTax);
				}
			}
			rs.close();
			pst.close();
			
			request.setAttribute("reportListMTax", alMTax);
			request.setAttribute("reportListFTax", alFTax);
	
	//***********************Section Report************************
			List<List<String>> al = new ArrayList<List<String>>();
			List<String> alInnerSection = new ArrayList<String>();
			
			Map<String, String> hmEmpName = CF.getEmpNameMap(con,null, null);
			Map<String, String> hmSalaryHeadMap = CF.getSalaryHeadsMap(con);
			if(hmSalaryHeadMap==null) hmSalaryHeadMap = new LinkedHashMap<String, String>();
			//pst = con.prepareStatement(selectSection);
			pst = con.prepareStatement("SELECT * FROM section_details where isdisplay=true and financial_year_start=? and financial_year_end=? order by section_code");
			pst.setDate(1, uF.getDateFormat(startyearDate, DATE_FORMAT));		
			pst.setDate(2, uF.getDateFormat(endYearDate, DATE_FORMAT));
			rs = pst.executeQuery();
			while(rs.next()){
				
				alInnerSection = new ArrayList<String>();
				alInnerSection.add(Integer.toString(rs.getInt("section_id")));
				alInnerSection.add(rs.getString("section_code"));
				alInnerSection.add(rs.getString("section_description"));
				alInnerSection.add(rs.getString("section_exemption_limit"));
				alInnerSection.add(uF.charMappingForAmountType(rs.getString("section_limit_type")));
				alInnerSection.add(CF.getUnderSectionName(uF.parseToInt(rs.getString("under_section"))));
				alInnerSection.add(rs.getString("salary_head_id")!=null && rs.getString("salary_head_id").length()>0 ? getSalaryHeadNames(hmSalaryHeadMap,rs.getString("salary_head_id")) : "");
				al.add(alInnerSection);
				
			}
			rs.close();
			pst.close();
			
			request.setAttribute("reportListSection", al);
			
	//*****************ExemptionReport ************************
			List<List<String>> alExcemption = new ArrayList<List<String>>();
			List<String> alInnerExcemption = new ArrayList<String>();
			List<FillUnderSection> underSec10and16List = new FillUnderSection().fillUnderSection10and16();

			pst = con.prepareStatement(selectExemption);
			pst.setDate(1, uF.getDateFormat(startyearDate, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(endYearDate, DATE_FORMAT));
			rs = pst.executeQuery();
			while(rs.next()){
				alInnerExcemption = new ArrayList<String>();
				alInnerExcemption.add(Integer.toString(rs.getInt("exemption_id")));
				alInnerExcemption.add(rs.getString("exemption_code"));
				alInnerExcemption.add(rs.getString("exemption_name"));
				alInnerExcemption.add(rs.getString("exemption_description"));
				alInnerExcemption.add(rs.getString("exemption_limit"));
				
				String strUnderSection = "";
				if(uF.parseToInt(rs.getString("under_section")) > 0){
					for(int i=0; i<underSec10and16List.size();i++ ) {
						String underSectionId = (underSec10and16List.get(i)).getUnderSectionId();
				    	String underSectionName = underSec10and16List.get(i).getUnderSectionName();
				    	if(uF.parseToInt(underSectionId) == uF.parseToInt(rs.getString("under_section"))){
				    		strUnderSection = underSectionName;
				    	}
					}
				}
				alInnerExcemption.add(strUnderSection);
				
				alInnerExcemption.add(uF.parseToBoolean(rs.getString("investment_form")) ? "True" : "False");
				
				alExcemption.add(alInnerExcemption);
			}
			rs.close();
			pst.close();
				
			request.setAttribute("reportListExcemption", alExcemption);
	
//*******************HRAReport************************			
			List<List<String>> alHRASettings = new ArrayList<List<String>> ();
			Map<String, String> hmSalaryHeads  = CF.getSalaryHeadsMap(con);
			
			pst = con.prepareStatement(selectHRA);
			pst = con.prepareStatement("select * from hra_exemption_details order by financial_year_from desc");
			rs = pst.executeQuery();
			while(rs.next()){
				
				List<String> alInnerHRA = new ArrayList<String>();
				
				alInnerHRA.add(uF.getDateFormat(rs.getString("financial_year_from"), DBDATE, "yyyy")+"-"+uF.getDateFormat(rs.getString("financial_year_to"), DBDATE, "yy"));
				alInnerHRA.add(uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("condition1"))));
				alInnerHRA.add(uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("condition2"))));
				alInnerHRA.add(uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("condition3"))));
				
				StringBuilder sb = new StringBuilder();
				if(rs.getString("salary_head_id")!=null){
					String []arr = rs.getString("salary_head_id").split(",");
					for(int i=0; i<arr.length; i++){
						sb.append((String)hmSalaryHeads.get(arr[i])+",");
					}
				}
				alInnerHRA.add(sb.toString());
				alHRASettings.add(alInnerHRA);
			}	
			rs.close();
			pst.close();
			
			request.setAttribute("alHRASettings", alHRASettings);
			
 //******************EPF Setting*****************************************
			Map<String, Map<String, String>> hmCurrencyDetails = CF.getCurrencyDetailsForPDF(con);
			
			String currId = CF.getOrgCurrencyIdByOrg(con, getF_org());
			if(uF.parseToInt(currId) > 0){
				Map<String, String> hmCurr = hmCurrencyDetails.get(currId);
				if (hmCurr == null) hmCurr = new HashMap<String, String>();
				String currency = hmCurr.get("SHORT_CURR")!=null && !hmCurr.get("SHORT_CURR").trim().equals("") ? hmCurr.get("SHORT_CURR") : "";
				request.setAttribute("currency", currency);
			}
			List<List<String>> alEPFSettings = new ArrayList<List<String>> ();
			
			pst = con.prepareStatement("select * from epf_details where org_id=? and level_id=? order by financial_year_start desc");
			pst.setInt(1, uF.parseToInt(getF_org()));
			pst.setInt(2, uF.parseToInt(getStrLevel()));
			rs = pst.executeQuery();
			while(rs.next()){
				
				List<String> alInnerEPF = new ArrayList<String>();
				alInnerEPF.add(uF.getDateFormat(rs.getString("financial_year_start"), DBDATE, "yyyy")+"-"+uF.getDateFormat(rs.getString("financial_year_end"), DBDATE, "yy"));
				alInnerEPF.add(uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("eepf_contribution"))));
				alInnerEPF.add(uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("epf_max_limit"))));
				alInnerEPF.add(uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("erpf_contribution"))));
				alInnerEPF.add(uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("erpf_max_limit"))));				
				alInnerEPF.add(uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("erps_contribution"))));
				alInnerEPF.add(rs.getString("eps_max_limit"));
				alInnerEPF.add(uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("erdli_contribution"))));
				alInnerEPF.add(rs.getString("edli_max_limit"));
				alInnerEPF.add(uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("pf_admin_charges"))));
				alInnerEPF.add(uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("edli_admin_charges"))));
				
				StringBuilder sb = new StringBuilder();
				if(rs.getString("salary_head_id")!=null){
					String []arr = rs.getString("salary_head_id").split(",");
					for(int i=0; i<arr.length; i++){
						sb.append((String)hmSalaryHeads.get(arr[i])+",");
					}
				}
				alInnerEPF.add(sb.toString());
				alEPFSettings.add(alInnerEPF);
				
			}	
			rs.close();
			pst.close();
			
			request.setAttribute("alEPFSettings", alEPFSettings);

//********************************ESIS Setting*************************
			LinkedHashMap<String, List<List<String>>> hmESISettings = new LinkedHashMap<String, List<List<String>>>();
			List<List<String>> alESISettings = new ArrayList<List<String>> ();
			
			Map<String, String> hmSalaryMap = CF.getSalaryHeadsMap(con);
			if(hmSalaryMap == null) hmSalaryMap = new HashMap<String, String>();
			Map<String, String> hmStateMap = CF.getStateMap(con);
			if(hmStateMap == null) hmStateMap = new HashMap<String, String>();
			Map<String, String> hmLevelMap = CF.getLevelMap(con);
			if(hmLevelMap == null) hmLevelMap = new HashMap<String, String>();
			
			pst = con.prepareStatement("select * from esi_details where org_id=? order by state_id, financial_year_start desc");
			pst.setInt(1, uF.parseToInt(getF_org()));			
			rs = pst.executeQuery();			
			String strStateNew = null;
			String strStateOld = null;
			List<String> alInnerESIS = new ArrayList<String>();
			while(rs.next()){
				strStateNew  = rs.getString("state_id");
			
				alInnerESIS = new ArrayList<String>();
				if(strStateNew!=null && !strStateNew.equalsIgnoreCase(strStateOld)){
					alESISettings = new ArrayList<List<String>> ();
				}
				
				alInnerESIS.add(uF.getDateFormat(rs.getString("financial_year_start"), DBDATE, "yyyy")+"-"+uF.getDateFormat(rs.getString("financial_year_end"), DBDATE, "yy"));
				alInnerESIS.add(rs.getString("eesi_contribution"));
				alInnerESIS.add(rs.getString("ersi_contribution"));
				alInnerESIS.add(rs.getString("max_limit"));
				
				StringBuilder sb = null;
				if(rs.getString("salary_head_id") != null){
					String []arr = rs.getString("salary_head_id").split(",");
					for(int i=0; i<arr.length; i++){
						if(uF.parseToInt(arr[i].trim()) > 0){
							if(sb == null){
								sb = new StringBuilder();
								sb.append(uF.showData(hmSalaryMap.get(arr[i]), ""));
							} else {
								sb.append(", "+uF.showData(hmSalaryMap.get(arr[i]), ""));
							}
						}
					}
				}
				if(sb == null) sb = new StringBuilder();
				alInnerESIS.add(sb.toString());
				
				StringBuilder sbEligible = null;
				if(rs.getString("eligible_salary_head_ids") != null){
					String []arr = rs.getString("eligible_salary_head_ids").split(",");
					for(int i = 0; i < arr.length; i++){
						if(uF.parseToInt(arr[i].trim()) > 0){
							if(sbEligible == null) {
								sbEligible = new StringBuilder();
								sbEligible.append(uF.showData(hmSalaryMap.get(arr[i]), ""));
							} else {
								sbEligible.append(", "+uF.showData(hmSalaryMap.get(arr[i]), ""));
							}
						}
					}
				}
				if(sbEligible == null) sbEligible = new StringBuilder();
				alInnerESIS.add(sbEligible.toString());
				
				alInnerESIS.add(uF.showData(hmLevelMap.get(rs.getString("level_id")), ""));
				
				alESISettings.add(alInnerESIS);
				
				hmESISettings.put(hmStateMap.get(strStateNew), alESISettings);
				
				strStateOld = strStateNew;
			}	
			rs.close();
			pst.close();
			
			request.setAttribute("alESISettings", alESISettings);
			request.setAttribute("hmESISettings", hmESISettings);
			
	//************************LWF Setting****************************//		
			LinkedHashMap hmESISettingsLWF = new LinkedHashMap();
			//Map<String, String> hmStateMapL = CF.getStateMap(con);

			List<List<String>> alESISettingsLWF = new ArrayList<List<String>>();
			pst = con.prepareStatement("select * from lwf_details order by state_id, financial_year_start desc");

			rs = pst.executeQuery();

			String strStateNew1 = null;
			String strStateOld1 = null;
			
			List<String> alInnerLWF = new ArrayList<String>();

			while (rs.next()) {

				strStateNew1 = rs.getString("state_id");

				alInnerLWF = new ArrayList<String>();
				if (strStateNew1 != null && !strStateNew1.equalsIgnoreCase(strStateOld1)) {
					alESISettingsLWF = new ArrayList<List<String>>();
				}

				alInnerLWF.add(uF.getDateFormat(rs.getString("financial_year_start"), DBDATE, "yyyy") + "-" + uF.getDateFormat(rs.getString("financial_year_end"), DBDATE, "yy"));
				alInnerLWF.add(rs.getString("eelfw_contribution"));
				alInnerLWF.add(rs.getString("erlfw_contribution"));
				alInnerLWF.add(rs.getString("min_limit"));
				alInnerLWF.add(rs.getString("max_limit"));

				StringBuilder sb = new StringBuilder();
				if (rs.getString("salary_head_id") != null) {
					String[] arr = rs.getString("salary_head_id").split(",");
					for (int i = 0; i < arr.length; i++) {
						sb.append((String) hmSalaryHeads.get(arr[i]) + ",");
						
					}
				}
				alInnerLWF.add(sb.toString());
				
				
				sb.replace(0, sb.length(), "");
				if (rs.getString("months") != null) {
					String[] arr = rs.getString("months").split(",");
					for (int i = 0; i < arr.length; i++) {
						sb.append(uF.getMonth(uF.parseToInt(arr[i])) + ",");
					}
				}
				alInnerLWF.add(sb.toString());

				alESISettingsLWF.add(alInnerLWF);

				hmESISettingsLWF.put(hmStateMap.get(strStateNew1), alESISettingsLWF);

				strStateOld1 = strStateNew1;

			}	
			rs.close();
			pst.close();

			request.setAttribute("alESISettingsLWF", alESISettingsLWF);
			request.setAttribute("hmESISettingsLWF", hmESISettingsLWF);
	
	//*************************MISCSettings****************************		
			LinkedHashMap hmMiscSettings = new LinkedHashMap();
			List<List<String>> alMiscSettings = new ArrayList<List<String>> ();
			
			pst = con.prepareStatement("select * from deduction_tax_misc_details where trail_status=1 order by state_id, financial_year_from desc");
			rs = pst.executeQuery();	
		//	System.out.println("pst==>"+pst);
			String strStateNewm = null;
			String strStateOldm = null;
			
			List<String> alInnerMisc = new ArrayList<String>();			
			while(rs.next()){
				strStateNewm  = rs.getString("state_id");
				//System.out.println("strStateNewm==>"+strStateNewm);
			
				alInnerMisc = new ArrayList<String>();
				if(strStateNewm!=null && !strStateNewm.equalsIgnoreCase(strStateOldm)){
					alMiscSettings = new ArrayList<List<String>> ();
				}
				
				alInnerMisc.add(uF.getDateFormat(rs.getString("financial_year_from"), DBDATE, "yyyy")+"-"+uF.getDateFormat(rs.getString("financial_year_to"), DBDATE, "yy"));
				alInnerMisc.add(uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("flat_tds"))));
				alInnerMisc.add(uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("service_tax"))));
				alInnerMisc.add(uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("standard_tax"))));
				alInnerMisc.add(uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("education_tax"))));
				
				alInnerMisc.add(uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("max_net_tax_income"))));
				alInnerMisc.add(uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("rebate_amt"))));
				
				alInnerMisc.add(uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("swachha_bharat_cess"))));
				alInnerMisc.add(uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("krishi_kalyan_cess"))));
				alInnerMisc.add(uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("cgst"))));
				alInnerMisc.add(uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("sgst"))));
				
				alMiscSettings.add(alInnerMisc);
				
				hmMiscSettings.put(hmStateMap.get(strStateNewm), alMiscSettings);
				
				strStateOldm = strStateNewm;
			}	
			rs.close();
			pst.close();
			
			
			request.setAttribute("alMiscSettings", alMiscSettings);
			request.setAttribute("hmMiscSettings", hmMiscSettings);
		
//*************************all statutary ids listed*********************
			
			String strOrgAccess = (String)session.getAttribute(ORG_ACCESS);
			String strWLocationAccess = (String)session.getAttribute(WLOCATION_ACCESS);
			
			List<String> alInner = new ArrayList<String>();
			Map<String, List<String>> hmOrganistaionMap = new HashMap<String, List<String>>();
			Map<String, List<String>> hmOfficeTypeMap = new HashMap<String, List<String>>();
			Map<String, List<String>> hmOfficeLocationMap = new HashMap<String, List<String>>();
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from org_details where org_id>0 ");
			if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && strOrgAccess != null && !strOrgAccess.trim().equals("") && !strOrgAccess.trim().equalsIgnoreCase("NULL")){
				sbQuery.append(" and org_id in( "+strOrgAccess+") ");
			}
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();
			while(rs.next()){
				
				alInner = new ArrayList<String>();
				alInner.add(rs.getString("org_id"));
				alInner.add(rs.getString("org_code")); 
				alInner.add(rs.getString("org_name"));
				alInner.add(CF.getStrDocRetriveLocation()+rs.getString("org_logo"));
				alInner.add(rs.getString("org_name"));
				
				hmOrganistaionMap.put(rs.getString("org_id"), alInner); 
				
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from work_location_type");
			rs = pst.executeQuery();
			String strOrgIdOldo = null;
			String strOrgIdNewo = null;
			
			while(rs.next()){
				
				strOrgIdNewo = rs.getString("org_id");
				//System.out.println("strOrgIdNewo=="+strOrgIdNewo);
				if(strOrgIdNewo!=null && !strOrgIdNewo.equalsIgnoreCase(strOrgIdOldo)){
					alInner = new ArrayList<String>();
				}
				
				alInner.add(rs.getString("wlocation_type_id"));
				alInner.add(rs.getString("wlocation_type_code"));
				alInner.add(rs.getString("wlocation_type_name"));
				alInner.add(rs.getString("wlocation_type_name"));
				
				hmOfficeTypeMap.put(rs.getString("org_id"), alInner);
				
				strOrgIdOldo = strOrgIdNewo;
				
			}
			rs.close();
			pst.close();
			
			
			sbQuery = new StringBuilder();
			sbQuery.append("SELECT * FROM ( SELECT *,wt.wlocation_type_id as type_id  FROM (SELECT * FROM ( SELECT * FROM (SELECT * " +
					"FROM work_location_info wl, timezones tz  WHERE tz.timezone_id=wl.timezone_id ");
			if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && strOrgAccess != null && !strOrgAccess.trim().equals("") && !strOrgAccess.trim().equalsIgnoreCase("NULL")){
				sbQuery.append(" and wl.org_id in ( "+strOrgAccess+") ");
			}
			if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && strWLocationAccess != null && !strWLocationAccess.trim().equals("") && !strWLocationAccess.trim().equalsIgnoreCase("NULL")){
				sbQuery.append(" and wl.wlocation_id in ("+strWLocationAccess+")");
			}
			sbQuery.append(") wl " +
					"left join state s on wl.wlocation_state_id=s.state_id ) wl " +
					"left join country co on wl.wlocation_country_id=co.country_id ) awt " +
					"LEFT JOIN work_location_type wt ON awt.wlocation_type_id = wt.wlocation_type_id order by wlocation_name) a " +
					"left join bank_details bd on bd.bank_id=a.wlocation_bank_id order by a.type_id");
			
			pst = con.prepareStatement(sbQuery.toString());
			//System.out.println("pst of locationmap===>"+pst);
			rs = pst.executeQuery();
			String strWLocationIdOld = null;
			String strWLocationNew = null;
			
			while(rs.next()){
				strWLocationNew = rs.getString("wlocation_type_id");
				if(strWLocationNew!=null && !strWLocationNew.equalsIgnoreCase(strWLocationIdOld)){
					alInner = new ArrayList<String>();
				}
				alInner.add(rs.getString("wlocation_id"));
				alInner.add(rs.getString("wlocation_name"));
				alInner.add(rs.getString("state_name"));
				
				alInner.add(rs.getString("country_name"));
				alInner.add(rs.getString("wlocation_pincode"));
				alInner.add(rs.getString("wlocation_contactno"));
				alInner.add(rs.getString("wlocation_faxno"));
				alInner.add(rs.getString("wlocation_city"));
				alInner.add(rs.getString("timezone_id"));
				
				alInner.add(rs.getString("wlocation_email"));
				alInner.add(rs.getString("wloacation_code"));
				alInner.add(rs.getString("wlocation_address"));
				alInner.add(rs.getString("wlocation_start_time"));
				alInner.add(rs.getString("wlocation_end_time"));
				alInner.add(rs.getString("ismetro"));
				
				alInner.add(rs.getString("wlocation_weeklyoff1"));
				alInner.add(rs.getString("wlocation_weeklyofftype1"));
				alInner.add(rs.getString("wlocation_weeklyoff2"));
				alInner.add(rs.getString("wlocation_weeklyofftype2"));
				alInner.add(rs.getString("currency_id"));
				
				hmOfficeLocationMap.put(rs.getString("wlocation_type_id"), alInner);
				
				strWLocationIdOld = strWLocationNew;
			}
			rs.close();
			pst.close();
			
			
			pst = con.prepareStatement("select count(*) as count, wlocation_id,org_id  from employee_official_details eod, employee_personal_details epd where epd.emp_per_id = eod.emp_id and epd.is_alive=true and eod.emp_id >0 group by wlocation_id,org_id ");
			rs = pst.executeQuery();
			Map<String, String> hmEmpCount = new HashMap<String, String>();
			Map<String, String> hmOrgEmpCount = new HashMap<String, String>();
			while(rs.next()) {
				hmEmpCount.put(rs.getString("wlocation_id"), rs.getString("count"));
				int empCount = uF.parseToInt(hmOrgEmpCount.get(rs.getString("org_id")));
				empCount += uF.parseToInt(rs.getString("count"));
				hmOrgEmpCount.put(rs.getString("org_id"), ""+empCount);			
			}
			rs.close();
			pst.close();
		
			request.setAttribute("hmOfficeLocationMap", hmOfficeLocationMap);
			request.setAttribute("hmOfficeTypeMap", hmOfficeTypeMap);
			request.setAttribute("hmEmpCount", hmEmpCount);
			request.setAttribute("hmOrgEmpCount", hmOrgEmpCount);
			request.setAttribute("hmOrganistaionMap", hmOrganistaionMap);
			
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(rs!=null){
				try{
					rs.close();
				}catch(SQLException e1){
					e1.printStackTrace();
				}
			}if(pst!=null){
				try{
					pst.close();
				}catch(SQLException e2){
					e2.printStackTrace();
				}
			}
		}
		
	}
	
	public void TDSBarchar(UtilityFunctions uF,Connection con,String startyearDate,String endYearDate,String prevStartyearDate,String prevEndYearDate, List<String> alMonth,String todayDate){
		PreparedStatement pst = null;
		ResultSet rs = null;
		try{
			
			Map<String, String> hmEmpTDSPaidAmountDetails=new HashMap<String, String>();
			List<String> alEmp = new ArrayList<String>();
			Map<String, String> hmTDSEmp = new HashMap<String, String>();
			int intOffset = uF.parseToInt(minLimit);
			
			pst=con.prepareStatement("select * from employee_official_details eod,employee_personal_details epd " +
					" where eod.emp_id=epd.emp_per_id and org_id=? " +
					" and eod.emp_id in(select emp_id from emp_salary_details where salary_head_id=12 and isdisplay=true and is_approved=true" +
					" and ( epd.joining_date <= ? )) order by emp_fname, emp_lname");
			pst.setInt(1, uF.parseToInt(getF_org()));
			pst.setDate(2, uF.getDateFormat(todayDate, DATE_FORMAT));
		//	System.out.println("pst for tds projection==>"+pst);
			rs=pst.executeQuery();
			while(rs.next()){
				String strEmpId = rs.getString("emp_per_id");
				alEmp.add(strEmpId);
				
				ViewEmpTDSProjection empTDSProjection = new ViewEmpTDSProjection();
				
				empTDSProjection.request = request;
				empTDSProjection.session = session;
				empTDSProjection.CF = CF;
				empTDSProjection.setStrEmpId(strEmpId);
				empTDSProjection.setStrFinancialYearStart(startyearDate);
				empTDSProjection.setStrFinancialYearEnd(endYearDate);
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
			}
			rs.close();
			pst.close();
			
			Map<String,String>tdsBarchart=new LinkedHashMap<String,String>();
		
			
			 for (int j = 0; j < alMonth.size(); j++) {
				 double totaldblPaidAmt1=0.0;
				 double dblPaidAmt1=0.0;
				 	for (int i = 0; alEmp != null && i < alEmp.size(); i++) { 
				 		//System.out.println("i==="+i);
				 			if (hmEmpTDSPaidAmountDetails.containsKey((String) alEmp.get(i)+ "_" + (String) alMonth.get(j))) { 
					
				 				//System.out.println(" "+uF.parseToDouble(hmEmpTDSPaidAmountDetails.get((String) alEmp.get(i)+ "_" + (String) alMonth.get(j))));
				 				dblPaidAmt1 = uF.parseToDouble(hmEmpTDSPaidAmountDetails.get((String) alEmp.get(i)+ "_" + (String) alMonth.get(j))); 
					 
				 			}else if (j < alMonth.size() - 1) {
						
				 				//System.out.println(" "+uF.parseToDouble(hmTDSEmp.get((String) alEmp.get(i)+ "_" + (String) alMonth.get(j))) );
				 				dblPaidAmt1 = uF.parseToDouble(hmTDSEmp.get((String) alEmp.get(i)+ "_" + (String) alMonth.get(j)));
					
				 			} else if (j == alMonth.size() - 1) {
				 				dblPaidAmt1 = uF.parseToDouble( hmTDSEmp.get((String) alEmp.get(i)+ "_" + (String) alMonth.get(j)));
				 			}
				 			
				 			totaldblPaidAmt1 +=dblPaidAmt1;
				 		}
				// System.out.println("totaldblPaidAmt1 "+j+" =="+totaldblPaidAmt1);
				 tdsBarchart.put(uF.getMonth(uF.parseToInt((String) alMonth.get(j))),""+totaldblPaidAmt1);
				 //totaldblPaidAmt1=0.0;
			}
			
			 StringBuilder sbMonthName = new StringBuilder();
			 for (int k = 0; k < alMonth.size(); k++) {
				 
				 sbMonthName.append("'" + uF.getMonth(uF.parseToInt((String) alMonth.get(k))) + "'");
				 	if (k < alMonth.size() - 1) {
					sbMonthName.append(",");
				}
			 }
			
			 StringBuilder sbTDSMonthAmountData = new StringBuilder();
			// sbTDSMonthAmountData.append("{name:");
			 if(tdsBarchart!=null && tdsBarchart.size()>0){
 	        	Iterator<String> it=tdsBarchart.keySet().iterator();
 	        	while(it.hasNext()){
 	        		String Month=it.next();
 	        		sbTDSMonthAmountData.append(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(tdsBarchart.get(Month))));
 	        		
 	        		if(it.hasNext()){
 	        			sbTDSMonthAmountData.append(",");
 	        		}
 	        	}
			 }
			 
			 
			 //System.out.println("sbTDSMonthAmountData==>"+sbTDSMonthAmountData);
			 
			// System.out.println("sbMonthName=="+sbMonthName);
			
			request.setAttribute("sbTDSMonthAmountData", sbTDSMonthAmountData);
			request.setAttribute("sbMonthName", sbMonthName);
			request.setAttribute("tdsBarchart", tdsBarchart);
			request.setAttribute("alMonth", alMonth);
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(rs!=null){
				try{
					rs.close();
				}catch(SQLException e1){
					e1.printStackTrace();
				}
				
			}if(pst!=null){
				try{
					pst.close();
				}catch(SQLException e2){
					e2.printStackTrace();
				}
			}
		}
		
	}
	
	
	private String getSalaryHeadNames(Map<String, String> hmSalaryHeadMap, String salaryHeadIds) {
		String salaryHeadName="";
		salaryHeadIds=salaryHeadIds.substring(1, salaryHeadIds.length()-1);
		String[] temp=salaryHeadIds.split(",");
		for(int i=0;i<temp.length;i++){
			if(i==0){
				salaryHeadName=hmSalaryHeadMap.get(temp[i].trim());
			}else{
				salaryHeadName+=","+hmSalaryHeadMap.get(temp[i].trim());
			}
		}
		return salaryHeadName;
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
	public String getF_org() {
		return f_org;
	}

	public void setF_org(String f_org) {
		this.f_org = f_org;
	}
	
	public String getStrLevel() {
		return strLevel;
	}

	public void setStrLevel(String strLevel) {
		this.strLevel = strLevel;
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

	public String getMinLimit() {
		return minLimit;
	}

	public void setMinLimit(String minLimit) {
		this.minLimit = minLimit;
	}
}
