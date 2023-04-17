package com.konnect.jpms.payroll.reports;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.konnect.jpms.select.FillFinancialYears;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillPayCycles;
import com.konnect.jpms.select.FillState;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class SalarySummaryReport extends ActionSupport implements  ServletRequestAware, ServletResponseAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	CommonFunctions CF;
	String strUserType;
	String strSessionEmpId;
	String f_org;
	String paycycle;
	List<FillOrganisation> orgList;
	List<FillPayCycles> paycycleList;
	
	@Override
	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		UtilityFunctions uF = new UtilityFunctions();
		request.setAttribute(PAGE, "/jsp/payroll/reports/SalarySummaryReport.jsp");
		request.setAttribute(TITLE, "Salary Summary");
		
		strUserType = (String)session.getAttribute(USERTYPE);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		
		if(uF.parseToInt(getF_org()) == 0){
			setF_org((String)session.getAttribute(ORGID));
		}
		
		viewdata();
		
		return loadfilter(uF);
	}
	
	
	public String loadfilter(UtilityFunctions uF){
		paycycleList = new FillPayCycles(request).fillPayCycles(CF,getF_org());
		orgList = new FillOrganisation(request).fillOrganisation();
		getSelectedFilter(uF);
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
			
			pst = con.prepareStatement("select * from level_details where org_id=?");
			pst.setInt(1, uF.parseToInt(getF_org()));
//			System.out.println("==>pst"+pst);
			rs = pst.executeQuery();	
			List<String> alLevel = new ArrayList<String>();
			Map<String, String> hmlevel = new HashMap<String, String>();
			StringBuilder sbLevel = null;
			while (rs.next()) {
				if(uF.parseToInt(rs.getString("level_id")) > 0 && !alLevel.contains(rs.getString("level_id"))){
					alLevel.add(rs.getString("level_id"));
					hmlevel.put(rs.getString("level_id"), rs.getString("level_name"));
					if(sbLevel == null){
						sbLevel = new StringBuilder();
						sbLevel.append(rs.getString("level_id"));
					} else {
						sbLevel.append(","+rs.getString("level_id"));
					}
				}
			}
			rs.close();
			pst.close();
			
			if(sbLevel != null){
				pst = con.prepareStatement("select sd.salary_head_id,sd.salary_head_name,ld.level_id, ld.level_name,amount,sd.earning_deduction " +
						"from (select sd.salary_head_id,sum(amount)as amount, ld.level_id from payroll_generation pg " +
						"join payroll_history ph on pg.paycycle= ph.paycycle " +
						"join grades_details gd on gd.grade_id = ph.grade_id " +
						"join designation_details dd on gd.designation_id = dd.designation_id " +
						"join level_details ld on dd.level_id = ld.level_id " +
						"join salary_details sd on sd.salary_head_id=pg.salary_head_id where ph.paycycle_from=? " +
						"and ph.paycycle_to=? and ph.paycycle=? and pg.paid_from=? " +
						"and pg.paid_to=? and pg.paycycle=? and ph.emp_id=pg.emp_id " +
						"and pg.emp_id in(select emp_id from employee_official_details where org_id=?) and ld.level_id in("+sbLevel.toString()+") group by ld.level_id,sd.salary_head_id " +
						"order by ld.level_id ,sd.salary_head_id) a,level_details ld, salary_details sd " +
						"where sd.salary_head_id=a.salary_head_id and a.level_id = ld.level_id and ld.level_id=sd.level_id " +
						"order by ld.level_name asc,sd.earning_deduction desc,sd.weight ");
				pst.setDate(1, uF.getDateFormat(strD1, DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strD2, DATE_FORMAT));
				pst.setInt(3, uF.parseToInt(strPC));
				pst.setDate(4, uF.getDateFormat(strD1, DATE_FORMAT));
				pst.setDate(5, uF.getDateFormat(strD2, DATE_FORMAT));
				pst.setInt(6, uF.parseToInt(strPC));
				pst.setInt(7, uF.parseToInt(getF_org()));
		//		System.out.println("==>pst"+pst);
				rs = pst.executeQuery();			
				Map<String, String> hmhead = new HashMap<String, String>();
				Map<String, String> hmAmount = new HashMap<String, String>();
				List<String> alEarning = new ArrayList<String>();
				List<String> alDeduction = new ArrayList<String>();
				while (rs.next()) {
					hmhead.put(rs.getString("salary_head_id"), rs.getString("salary_head_name"));
					
					hmAmount.put(rs.getString("salary_head_id")+"_"+rs.getString("level_id"), uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(rs.getString("amount"))));
					
					if(rs.getString("earning_deduction") != null && rs.getString("earning_deduction").trim().equalsIgnoreCase("E")){
						if(!alEarning.contains(rs.getString("salary_head_id"))){
							alEarning.add(rs.getString("salary_head_id"));
						}
					}
					if(rs.getString("earning_deduction") != null && rs.getString("earning_deduction").trim().equalsIgnoreCase("D")){
						if(!alDeduction.contains(rs.getString("salary_head_id"))){
							alDeduction.add(rs.getString("salary_head_id"));
						}
					}
					
				}
				rs.close();
				pst.close();
			  
//				System.out.println("hmhead==>"+hmhead);
//				System.out.println("hmlevel==>"+hmlevel);
//				System.out.println("hmAmount==>"+hmAmount);
//				System.out.println("alEarning==>"+alEarning);
//				System.out.println("alDeduction==>"+alDeduction);
//				System.out.println("alLevel==>"+alLevel);
				
				request.setAttribute("hmhead", hmhead);
				request.setAttribute("hmlevel", hmlevel);
				request.setAttribute("hmAmount", hmAmount);
				
				request.setAttribute("alEarning", alEarning);
				request.setAttribute("alDeduction", alDeduction);
				request.setAttribute("alLevel", alLevel);
			}
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


	public String getF_org() {
		return f_org;
	}


	public void setF_org(String f_org) {
		this.f_org = f_org;
	}


	public String getPaycycle() {
		return paycycle;
	}


	public void setPaycycle(String paycycle) {
		this.paycycle = paycycle;
	}


	public List<FillOrganisation> getOrgList() {
		return orgList;
	}


	public void setOrgList(List<FillOrganisation> orgList) {
		this.orgList = orgList;
	}


	public List<FillPayCycles> getPaycycleList() {
		return paycycleList;
	}
	public void setPaycycleList(List<FillPayCycles> paycycleList) {
		this.paycycleList = paycycleList;
	}
	
}