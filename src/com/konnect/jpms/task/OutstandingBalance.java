package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillCalendarYears;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class OutstandingBalance extends ActionSupport implements ServletRequestAware, IStatements {
 
	/**
	 *
	 */
	
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strSessionEmpId;
	String strUserType =  null;
	CommonFunctions CF; 
	 
	String outstandingFrom;
	
	String strProType;
	boolean poFlag;
	
	String btnSubmit;
	
	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)return LOGIN;

		strSessionEmpId = (String)session.getAttribute(EMPID);
		strUserType = (String)session.getAttribute(BASEUSERTYPE);
		
		request.setAttribute(PAGE, "/jsp/task/OutstandingBalance.jsp");
		request.setAttribute(TITLE, "Outstanding Balance");
		
		request.setAttribute("arrEnabledModules", CF.getArrEnabledModules());
		request.setAttribute("DOC_RETRIVE_LOCATION", CF.getStrDocRetriveLocation());
		request.setAttribute("IS_DEVICE_INTEGRATION", CF.getIsDeviceIntegration());
		UtilityFunctions uF = new UtilityFunctions();
		boolean isView  = CF.getAccess(session, request, uF);
		if(!isView){
			request.setAttribute(PAGE, PAccessDenied);
			request.setAttribute(TITLE, TAccessDenied);
			return ACCESS_DENIED;
		}
		
		if(uF.parseToInt(getOutstandingFrom()) == 0){
			setOutstandingFrom("1");
		}
		checkProjectOwner(uF);
		getOutstandingBalanceDetails(uF);
		
		getSelectedFilter(uF);
		
		if(getBtnSubmit() != null) {
			return SUCCESS;
		} else {
			return LOAD;
		}

	}
	
	
	private void getSelectedFilter(UtilityFunctions uF) {
		Map<String, String> hmFilter = new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();
		
		alFilter.add("PERIOD");
		if(uF.parseToInt(getOutstandingFrom()) == 1) {
			hmFilter.put("PERIOD", "Since last 1 Year");
		} else if(uF.parseToInt(getOutstandingFrom()) == 2) {
			hmFilter.put("PERIOD", "Since last 6 months");
		} else if(uF.parseToInt(getOutstandingFrom()) == 3) {
			hmFilter.put("PERIOD", "Since last 3 months");
		} else if(uF.parseToInt(getOutstandingFrom()) == 4) {
			hmFilter.put("PERIOD", "Since last 1 month");
		}
		
		if(isPoFlag() || (strUserType!=null && (strUserType.equals(ADMIN) || strUserType.equals(HRMANAGER)))){
			alFilter.add("PROJECT_TYPE");
			if(uF.parseToInt(getStrProType()) == 1) {
				hmFilter.put("PROJECT_TYPE", "All Projects");
			} else {
				hmFilter.put("PROJECT_TYPE", "My Projects");
			}
		}
		
		String selectedFilter = getSelectedFilter2(CF, uF, alFilter, hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}
	
	
	public String getSelectedFilter2(CommonFunctions CF, UtilityFunctions uF, List<String> alFilter, Map<String, String> hmFilter) {
		StringBuilder sbFilter=new StringBuilder("<span style=\"float: left; margin-right: 5px;\"><i class=\"fa fa-filter\"></i></span>");
		sbFilter.append("<span style=\"float: left; width: 95%;\">");
		
		int cnt=0;
		for(int i=0;alFilter!=null && i<alFilter.size();i++) {
			if(i>0) {
				sbFilter.append(", ");
			} 
			if(alFilter.get(i).equals("PERIOD")) {
				sbFilter.append("<strong>PERIOD:</strong> ");
				sbFilter.append(hmFilter.get("PERIOD"));
				cnt++;
			} else if(alFilter.get(i).equals("PROJECT_TYPE")) {
				sbFilter.append("<strong>PROJECT TYPE:</strong> ");
				sbFilter.append(hmFilter.get("PROJECT_TYPE"));
				cnt++;
			}
		}
		sbFilter.append("</span>");
		
		return sbFilter.toString();
	}
	
	
	private void checkProjectOwner(UtilityFunctions uF) {
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		boolean poFlag = false;
		try{
			con = db.makeConnection(con);
			
			StringBuilder sbQuery = new StringBuilder();
			
		//===start parvez date: 13-10-2022===	
//			sbQuery.append("select * from projectmntnc pmc where project_owner=?");
			sbQuery.append("select * from projectmntnc pmc where project_owners like '%,"+strSessionEmpId+",%'");
			pst = con.prepareStatement(sbQuery.toString());
//			pst.setInt(1, uF.parseToInt(strSessionEmpId));
		//===end parvez date: 13-10-2022===	
			rs = pst.executeQuery();
			if(rs.next()) {
				poFlag = true;
			}
			rs.close();
			pst.close();
			
			setPoFlag(poFlag);
			
			if(poFlag && uF.parseToInt(getStrProType()) == 0){
				setStrProType("2");
			}
		} catch (Exception e) {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private void getOutstandingBalanceDetails(UtilityFunctions uF) {
		
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs=null;
		try { 
			String strSinceDate = null;
			if(uF.parseToInt(getOutstandingFrom()) == 1){
				strSinceDate = uF.getDateFormat(""+uF.getPrevDate(CF.getStrTimeZone(), 365), DBDATE, DATE_FORMAT);
			} else if(uF.parseToInt(getOutstandingFrom()) == 2){
				strSinceDate = uF.getDateFormat(""+uF.getPrevDate(CF.getStrTimeZone(), 180), DBDATE, DATE_FORMAT);
			} else if(uF.parseToInt(getOutstandingFrom()) == 3){
				strSinceDate = uF.getDateFormat(""+uF.getPrevDate(CF.getStrTimeZone(), 90), DBDATE, DATE_FORMAT);
			} else if(uF.parseToInt(getOutstandingFrom()) == 4){
				strSinceDate = uF.getDateFormat(""+uF.getPrevDate(CF.getStrTimeZone(), 30), DBDATE, DATE_FORMAT);
			} else {
				strSinceDate = uF.getDateFormat(""+uF.getPrevDate(CF.getStrTimeZone(), 365), DBDATE, DATE_FORMAT);
			}
			
			con = db.makeConnection(con);
			
			Map<String, String> hmSbu = (Map<String, String>)CF.getServicesMap(con, false);;
			if(hmSbu == null) hmSbu = new HashMap<String, String>();
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select sum(pa.received_amount) / (sum(pa.exchange_rate) / count(pa.exchange_rate)) as received_amount, " +
					"pmt.sbu_id,extract(month from pa.entry_date) as entry_month,extract(year from pa.entry_date) as entry_year " +
					"from promntc_bill_amt_details pa,projectmntnc pmt where pa.pro_id=pmt.pro_id and pmt.sbu_id > 0 and pa.entry_date between ? and ? ");
			
			if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
				sbQuery.append(" and pmt.org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
				sbQuery.append(" and pmt.wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
			if(isPoFlag() && uF.parseToInt(getStrProType()) == 2){
			//===start parvez date: 13-10-2022===	
//				sbQuery.append(" and pmt.project_owner="+uF.parseToInt(strSessionEmpId));
				sbQuery.append(" and pmt.project_owners like '%,"+strSessionEmpId+",%'");
			//===end parvez date: 13-10-2022===	
			}
			sbQuery.append(" group by pmt.sbu_id,extract(month from pa.entry_date),extract(year from pa.entry_date)");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(strSinceDate, DATE_FORMAT));
			pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
//			System.out.println("pst ===>> " + pst);
			rs = pst.executeQuery();
			Map<String, String> hmReceivedAmt = new HashMap<String, String>();
			while(rs.next()){
				hmReceivedAmt.put(rs.getString("sbu_id")+"_"+rs.getString("entry_month")+"_"+rs.getString("entry_year"), rs.getString("received_amount"));
			}
			rs.close();
			pst.close();
			sbQuery = new StringBuilder();
			sbQuery.append("select sum(pi.oc_invoice_amount) as invoice_amount,pcmc.sbu_id,extract(month from pi.invoice_generated_date) as invoice_month," +
					"extract(year from pi.invoice_generated_date) as invoice_year from promntc_invoice_details pi, projectmntnc pcmc " +
					"where pcmc.pro_id = pi.pro_id and pcmc.sbu_id > 0 and pi.invoice_generated_date between ? and ? ");
			if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
				sbQuery.append(" and pcmc.org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
				sbQuery.append(" and pcmc.wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
			if(isPoFlag() && uF.parseToInt(getStrProType()) == 2){
			//===start parvez date: 13-10-2022===	
//				sbQuery.append(" and pcmc.project_owner="+uF.parseToInt(strSessionEmpId));
				sbQuery.append(" and pcmc.project_owners like '%,"+strSessionEmpId+",%'");
			//===end parvez date: 13-10-2022===	
			}
			sbQuery.append(" group by pcmc.sbu_id," +
					"extract(month from pi.invoice_generated_date),extract(year from pi.invoice_generated_date) " +
					"order by extract(month from pi.invoice_generated_date),extract(year from pi.invoice_generated_date)");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(strSinceDate, DATE_FORMAT));
			pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
//			System.out.println("pst ===>> " + pst);
			rs = pst.executeQuery();
			Map<String, String> hmInvoiceAmt = new LinkedHashMap<String, String>();
			List<String> alMonthYear =  new ArrayList<String>();
			List<String> alSbu =  new ArrayList<String>();
			while(rs.next()){
				hmInvoiceAmt.put(rs.getString("sbu_id")+"_"+rs.getString("invoice_month")+"_"+rs.getString("invoice_year"), rs.getString("invoice_amount"));
				if(!alMonthYear.contains(rs.getString("invoice_month")+"_"+rs.getString("invoice_year"))){
					alMonthYear.add(rs.getString("invoice_month")+"_"+rs.getString("invoice_year"));
				}
				if(!alSbu.contains(rs.getString("sbu_id"))){
					alSbu.add(rs.getString("sbu_id"));
				}
			}
			rs.close();
			pst.close();
			StringBuilder sbOutstanding = new StringBuilder();
			List<String> alOutStanding = new ArrayList<String>();
			int x = 1;
			List<String> al = new ArrayList<String>();
			for(int i =0 ; i<alMonthYear.size(); i++){
				String strMonthYear = alMonthYear.get(i);
				String[] strTemp = strMonthYear.split("_");

				StringBuilder sbData = new StringBuilder();
				for(int j =0 ; j<alSbu.size(); j++){
					String strSbu = alSbu.get(j);
					double dblInvoiceAmt = uF.parseToDouble(hmInvoiceAmt.get(strSbu+"_"+strMonthYear));
					double dblReceivedAmt = uF.parseToDouble(hmReceivedAmt.get(strSbu+"_"+strMonthYear));
					double dblOutstandingAmt = uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma((dblInvoiceAmt - dblReceivedAmt)));
					if(dblOutstandingAmt < 0.0d){
						continue;
					}
					sbData.append(",'"+hmSbu.get(strSbu).replaceAll("[^a-zA-Z0-9]", "")+"': "+dblOutstandingAmt+"");
					
					if(!al.contains(strSbu)){
						al.add(strSbu);
						
						StringBuilder sbGraph = new StringBuilder();
						sbGraph.append(" var graph"+x+" = new AmCharts.AmGraph();" +
								"graph"+x+".title = '"+hmSbu.get(strSbu).replaceAll("[^a-zA-Z0-9]", "")+"';" +
								"graph"+x+".labelText = '[[value]]';" +
								"graph"+x+".valueField = '"+hmSbu.get(strSbu).replaceAll("[^a-zA-Z0-9]", "")+"';" +
								"graph"+x+".type = 'column';" +
								"graph"+x+".lineAlpha = 0;" +
								"graph"+x+".fillAlphas = 1;" +
								"graph"+x+".balloonText = \"<span style='color:#555555;'>[[category]]</span><br><span style='font-size:14px'>[[title]]:<b>[[value]]</b></span>\";" +
								"chart.addGraph(graph"+x+");");
						alOutStanding.add(sbGraph.toString());
						x++;
					}
				}
				if(sbData.length()>0) {
					sbOutstanding.append("{'month':'"+uF.getShortMonth(uF.parseToInt(strTemp[0].trim()))+" "+strTemp[1].trim()+"'");
					sbOutstanding.append(sbData.toString());
					sbOutstanding.append("},");
				}
			}
			if(sbOutstanding.length()>1) {
				sbOutstanding.replace(0, sbOutstanding.length(), sbOutstanding.substring(0, sbOutstanding.length()-1));
	        }

			request.setAttribute("sbOutstanding", sbOutstanding.toString());
			request.setAttribute("alOutStanding", alOutStanding);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public String getOutstandingFrom() {
		return outstandingFrom;
	}

	public void setOutstandingFrom(String outstandingFrom) {
		this.outstandingFrom = outstandingFrom;
	}

	public String getStrProType() {
		return strProType;
	}

	public void setStrProType(String strProType) {
		this.strProType = strProType;
	}

	public boolean isPoFlag() {
		return poFlag;
	}

	public void setPoFlag(boolean poFlag) {
		this.poFlag = poFlag;
	}

	public String getBtnSubmit() {
		return btnSubmit;
	}

	public void setBtnSubmit(String btnSubmit) {
		this.btnSubmit = btnSubmit;
	}
	
}
