package com.konnect.jpms.reports.master;

import java.net.URLDecoder;
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

import com.konnect.jpms.select.FillFinancialYears;
import com.konnect.jpms.select.FillGender;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class DeductionReportTax extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	
	HttpSession session;
	CommonFunctions CF;
	private static Logger log = Logger.getLogger(DeductionReportTax.class);
	
	private String userscreen;
	private String navigationId;
	private String toPage;
	
	private String financialYear;
	private List<FillGender> genderList;
	private List<FillFinancialYears> financialYearList;
	
	public String execute() throws Exception {		
		UtilityFunctions uF = new UtilityFunctions(); 
		String strT = request.getParameter("T");
		String strS = request.getParameter("S");
		
		request.setAttribute(PAGE, PReportDeductionTax);
		request.setAttribute(TITLE, TViewDeduction);
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		 
		
		/*boolean isView  = CF.getAccess(session, request, uF);
		if(!isView){
			request.setAttribute(PAGE, PAccessDenied);
			request.setAttribute(TITLE, TAccessDenied);
			return ACCESS_DENIED;
		}*/
		
		viewDeduction(strT, uF);	
		
		loadDeduction();
		getSelectedFilter(uF);
		
		return LOAD;
	} 
	
	public void loadDeduction() {
		
		genderList = new FillGender().fillGender();
		request.setAttribute("genderList", genderList);
		int i=0;
		String genderId = "";
		String genderName = "";
		
		StringBuilder sbGenderList = new StringBuilder();
		sbGenderList.append("{");
		for(i=0; i<genderList.size()-1;i++ ) {
			genderId = (genderList.get(i)).getGenderId();
			genderName = genderList.get(i).getGenderName();
			sbGenderList.append("\""+ genderId+"\":\""+genderName+"\",");
		}
		if(genderList.size()>1) {
			genderId = (genderList.get(i)).getGenderId();
			genderName = genderList.get(i).getGenderName();
			sbGenderList.append("\""+ genderId+"\":\""+genderName+"\"}");
		}
		request.setAttribute("sbGenderList", sbGenderList.toString());
		
		financialYearList = new FillFinancialYears(request).fillFinancialYears(CF);
		
	}
	
	
	private void getSelectedFilter(UtilityFunctions uF) {
		Map<String,String> hmFilter=new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();
		
		alFilter.add("FINANCIALYEAR");
		String[] strFinancialYears = null;
		if (getFinancialYear() != null && !getFinancialYear().equals("") && !getFinancialYear().equals("null")) {
			strFinancialYears = getFinancialYear().split("-");
			setFinancialYear(strFinancialYears[0] + "-" + strFinancialYears[1]);
			
		} else {
			strFinancialYears = CF.getFinancialYear(uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT), CF, uF);
			setFinancialYear(strFinancialYears[0] + "-" + strFinancialYears[1]);
			
		}
		hmFilter.put("FINANCIALYEAR", uF.getDateFormat(strFinancialYears[0], DATE_FORMAT, CF.getStrReportDateFormat()) +" - "+ uF.getDateFormat(strFinancialYears[1], DATE_FORMAT, CF.getStrReportDateFormat()));
		

		String selectedFilter = CF.getSelectedFilter2(CF, uF, alFilter, hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}
	

	public String viewDeduction(String strT, UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs=null;
		Database db = new Database();
		db.setRequest(request);

		try {

			String[] strPayCycleDates = null;
			String strFinancialYearStart = null;
			String strFinancialYearEnd = null;
			
			if (getFinancialYear() != null && !getFinancialYear().equals("") && !getFinancialYear().equals("null")) {
				String str = URLDecoder.decode(getFinancialYear());
				setFinancialYear(str);
				strPayCycleDates = getFinancialYear().split("-");
				strFinancialYearStart = strPayCycleDates[0];
				strFinancialYearEnd = strPayCycleDates[1];
				setFinancialYear(strPayCycleDates[0] + "-" + strPayCycleDates[1]);
			} else {
				strPayCycleDates = new FillFinancialYears(request).fillLatestFinancialYears();
				setFinancialYear(strPayCycleDates[0] + "-" + strPayCycleDates[1]);
				
				strFinancialYearStart = strPayCycleDates[0];
				strFinancialYearEnd = strPayCycleDates[1];
			}
			
			con = db.makeConnection(con);

			Map<String, String> hmEmpName = CF.getEmpNameMap(con,null, null);
			
			List<List<String>> alM = new ArrayList<List<String>>();
			List<List<String>> alF = new ArrayList<List<String>>();
			List<String> alInner = new ArrayList<String>();
			
			pst = con.prepareStatement(selectDeductionRTax);
//			pst.setString(1, strT);
			
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();
			while(rs.next()){
				alInner = new ArrayList<String>();
				alInner.add(rs.getString("deduction_tax_id"));
				alInner.add(rs.getString("age_from"));
				alInner.add(rs.getString("age_to"));
				alInner.add(uF.charMappingMaleFemale(rs.getString("gender")));
				alInner.add(rs.getString("_from"));
				alInner.add(rs.getString("_to"));
				alInner.add(rs.getString("deduction_amount"));
				alInner.add(uF.charMapping(rs.getString("deduction_type")));
				alInner.add(uF.getDateFormat(rs.getString("financial_year_from"), DBDATE, CF.getStrReportDateFormat()));
				alInner.add(uF.getDateFormat(rs.getString("financial_year_to"), DBDATE, CF.getStrReportDateFormat()));
				alInner.add(getSlabType(rs.getInt("slab_type")));
//				alInner.add("<a href="+request.getContextPath()+"/AddDeduction.action?E="+rs.getString("deduction_id")+">Edit</a> | <a onclick=\"return confirm('"+ConfirmDelete+"');\" href="+request.getContextPath()+"/AddDeduction.action?D="+rs.getString("deduction_id")+">Delete</a>");
				 
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
			request.setAttribute("strFinancialYearStart", strFinancialYearStart);
			request.setAttribute("strFinancialYearEnd", strFinancialYearEnd);
			
			pst = con.prepareStatement("SELECT max(entry_date) as entry_date, user_id FROM deduction_tax_details where financial_year_from=? and financial_year_to=? group by user_id");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();
			while(rs.next()){
				request.setAttribute("UPDATED_DATE", uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));
				request.setAttribute("UPDATED_NAME", hmEmpName.get(rs.getString("user_id")));
			}
			rs.close();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;
		
	}
	
	public String getSlabType(Integer slabType) {
		
		switch (slabType) {
			case 0:
				return "Standard";
			case 1:
				return "New";
			default:
				return "-";
		}
	}
	
	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public List<FillFinancialYears> getFinancialYearList() {
		return financialYearList;
	}

	public void setFinancialYearList(List<FillFinancialYears> financialYearList) {
		this.financialYearList = financialYearList;
	}

	public String getFinancialYear() {
		return financialYear;
	}

	public void setFinancialYear(String financialYear) {
		this.financialYear = financialYear;
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
	

}