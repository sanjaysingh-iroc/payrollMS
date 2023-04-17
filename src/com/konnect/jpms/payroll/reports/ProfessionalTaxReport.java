package com.konnect.jpms.payroll.reports;

import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.konnect.jpms.select.FillFinancialYears;
import com.konnect.jpms.select.FillMonth;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillPayCycles;
import com.konnect.jpms.select.FillState;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class ProfessionalTaxReport extends ActionSupport implements  ServletRequestAware, ServletResponseAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	CommonFunctions CF;
	String strUserType;
	String strSessionEmpId;
	String strSubmit;
	String formType;
	String f_org;
	String f_state;
	String paycycle;
	List<FillOrganisation> orgList;
	List<FillPayCycles> paycycleList;
	List<FillState> stateList;
	
	@Override
	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		request.setAttribute(PAGE, "/jsp/payroll/reports/ProfessionalTaxReport.jsp");
		request.setAttribute(TITLE, "Summary of Profession Tax Deduction");
		
		strUserType = (String)session.getAttribute(USERTYPE);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		UtilityFunctions uF = new UtilityFunctions();
		
		if(uF.parseToInt(getF_org()) == 0){
			setF_org((String)session.getAttribute(ORGID));
		}
		viewdata();
		
		
		return loadfilter(uF);
	}
	
	
	public String loadfilter(UtilityFunctions UF){
		paycycleList = new FillPayCycles(request).fillPayCycles(CF,getF_org());
		orgList = new FillOrganisation(request).fillOrganisation();
		stateList = new FillState(request).fillWLocationStates();
		getSelectedFilter(UF);
		return LOAD;
	}
	
	
	private void getSelectedFilter(UtilityFunctions uF) {
		Map<String,String> hmFilter=new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();

		alFilter.add("ORGANISATION");
		if(getF_org()!=null)  {
			String strOrg="";
			int k=0;
			for(int i=0;orgList!=null && i<orgList.size();i++){
				if(getF_org().equals(orgList.get(i).getOrgId())) {
					if(k==0) {
						strOrg=orgList.get(i).getOrgName();
					} else {
						strOrg+=", "+orgList.get(i).getOrgName();
					}
					k++;
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
		
		alFilter.add("STATE");
		if(getF_state()!=null)  {
			String strOrg="";
			int k=0;
			for(int i=0;stateList!=null && i<stateList.size();i++){
				if(getF_state().equals(stateList.get(i).getStateId())) {
					if(k==0) {
						strOrg=stateList.get(i).getStateName();
					} else {
						strOrg+=", "+stateList.get(i).getStateName();
					}
					k++;
				}
			}
			if(strOrg!=null && !strOrg.equals("")) {
				hmFilter.put("STATE", strOrg);
			} else {
				hmFilter.put("STATE", "All State");
			}
			
		} else {
			hmFilter.put("STATE", "All State");
		}
		
		alFilter.add("PAYCYCLE");
		if(getPaycycle()!=null)  {
			String strOrg="";
			int k=0;
			for(int i=0;paycycleList!=null && i<paycycleList.size();i++){
				if(getPaycycle().equals(paycycleList.get(i).getPaycycleId())) {
					if(k==0) {
						strOrg=paycycleList.get(i).getPaycycleName();
					} else {
						strOrg+=", "+paycycleList.get(i).getPaycycleName();
					}
					k++;
				}
			}
			if(strOrg!=null && !strOrg.equals("")) {
				hmFilter.put("PAYCYCLE", strOrg);
			} else {
				hmFilter.put("PAYCYCLE", "All Paycycle");
			}
			
		} else {
			hmFilter.put("PAYCYCLE", "All Paycycle");
		}
		
		String selectedFilter=CF.getSelectedFilter2(CF,uF,alFilter,hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}
	

	
	public String viewdata(){
		
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			
			String[] strPayCycleDates = null;
			if (getPaycycle() != null && !getPaycycle().trim().equals("") && !getPaycycle().trim().equalsIgnoreCase("NULL")) {
				strPayCycleDates = getPaycycle().split("-");
			} else {
				strPayCycleDates = CF.getCurrentPayCycleByOrg(CF.getStrTimeZone(), CF, getF_org(),request);
				setPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1] + "-" + strPayCycleDates[2]);
			}
			String strD1 = strPayCycleDates[0];
			String strD2 = strPayCycleDates[1];
			String strPC =strPayCycleDates[2];
			
			con = db.makeConnection(con);
			
			String[] strFinancialYear = CF.getFinancialYear(con, strD2, CF, uF); 
			String strFinancialYearStart= strFinancialYear[0];
			String strFinancialYearEnd= strFinancialYear[1];
					
			pst = con.prepareStatement("select * from deduction_details_india where state_id=? and financial_year_from=? and financial_year_to=?");
			pst.setInt(1, uF.parseToInt(getF_state()));
			pst.setDate(2,  uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(3,  uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();
			Map<String, Map<String, String>> hmPTSlab=new LinkedHashMap<String, Map<String, String>>();
			String strPtAmt = "";
			while (rs.next()) {
				Map<String, String> hmInner = new HashMap<String, String>();
				hmInner.put("DEDUCTION_ID", rs.getString("deduction_id"));
				hmInner.put("INCOME_FROM", rs.getString("income_from"));
				hmInner.put("INCOME_TO", rs.getString("income_to"));
				hmInner.put("STATE_ID", rs.getString("state_id"));
				hmInner.put("DEDUCTION_AMOUNT", rs.getString("deduction_amount"));
				hmInner.put("DEDUCTION_PAYCYCLE", rs.getString("deduction_paycycle"));
				
				hmPTSlab.put(rs.getString("deduction_paycycle"), hmInner);
				if(strPtAmt.equals("")){
					strPtAmt = rs.getString("deduction_paycycle");
				} else {
					strPtAmt += ","+ rs.getString("deduction_paycycle");
				}
			}
			rs.close();
			pst.close();  			
//			System.out.println("hmPTSlab=====>"+hmPTSlab);
//			System.out.println("strPtAmt=====>"+strPtAmt);
			if(strPtAmt!=null && !strPtAmt.equals("")){
				StringBuilder sbQuery = new StringBuilder();
				sbQuery.append("select count(eod.emp_id) as empcnt, amount,sum(amount) as totalamt from payroll_generation pg,employee_official_details eod " +
						"where pg.emp_id=eod.emp_id and  pg.paycycle= ? and pg.salary_head_id =? and pg.is_paid=true and " +
						"eod.org_id=? and pg.amount in ("+strPtAmt+") " +
						"and eod.wlocation_id in (select wlocation_id from work_location_info where wlocation_state_id=?)  group by pg.amount");
				
				pst = con.prepareStatement(sbQuery.toString());
				pst.setInt(1, uF.parseToInt(strPC));
				pst.setInt(2, PROFESSIONAL_TAX);
				pst.setInt(3, uF.parseToInt(getF_org()));
				pst.setInt(4, uF.parseToInt(getF_state()));
//				System.out.println("pst=====>"+pst);
				Map<String, Map<String, String>> hmPTDetails = new LinkedHashMap<String, Map<String, String>>();
				rs = pst.executeQuery();
				while (rs.next()){
					Map<String, String> hmInner = new HashMap<String, String>();
					hmInner.put("EMP_COUNT", rs.getString("empcnt"));
					hmInner.put("AMOUNT", rs.getString("amount"));
					hmInner.put("TOTAL_AMOUNT", rs.getString("totalamt"));
					hmPTDetails.put(rs.getString("amount"), hmInner);
				}
				rs.close();
				pst.close();	
				request.setAttribute("hmPTDetails", hmPTDetails);
//				System.out.println("hmPTDetails=====>"+hmPTDetails);
				
			}
			
			request.setAttribute("hmPTSlab", hmPTSlab);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;
	}

	private HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	
	}
	
	private HttpServletResponse response;
	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
	}
	public String getStrUserType() {
		return strUserType;
	}
	public void setStrUserType(String strUserType) {
		this.strUserType = strUserType;
	}
	public String getStrSessionEmpId() {
		return strSessionEmpId;
	}
	public void setStrSessionEmpId(String strSessionEmpId) {
		this.strSessionEmpId = strSessionEmpId;
	}
	public String getStrSubmit() {
		return strSubmit;
	}
	public void setStrSubmit(String strSubmit) {
		this.strSubmit = strSubmit;
	}
	public String getFormType() {
		return formType;
	}
	public void setFormType(String formType) {
		this.formType = formType;
	}
	public String getF_org() {
		return f_org;
	}
	public void setF_org(String f_org) {
		this.f_org = f_org;
	}
	public String getF_state() {
		return f_state;
	}
	public void setF_state(String f_state) {
		this.f_state = f_state;
	}
	public List<FillOrganisation> getOrgList() {
		return orgList;
	}
	public void setOrgList(List<FillOrganisation> orgList) {
		this.orgList = orgList;
	}
	
	public List<FillState> getStateList() {
		return stateList;
	}
	public void setStateList(List<FillState> stateList) {
		this.stateList = stateList;
	}
	
	public List<FillPayCycles> getPaycycleList() {
		return paycycleList;
	}

	public void setPaycycleList(List<FillPayCycles> paycycleList) {
		this.paycycleList = paycycleList;
	}
	
	public String getPaycycle() {
		return paycycle;
	}

	public void setPaycycle(String paycycle) {
		this.paycycle = paycycle;
	}
}