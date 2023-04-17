package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillCalendarYears;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class BillsReceipts extends ActionSupport implements ServletRequestAware, IStatements {
 
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strSessionEmpId; 
	String strUserType =  null;
	CommonFunctions CF; 
	
	String proOwner; 
	String calendarYear;
	
	List<FillProjectOwnerList> proOwnerList;
	List<FillCalendarYears> calendarYearList;
	
	String strProType;
	boolean poFlag;

	String btnSubmit;
	
	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)return LOGIN;

		strSessionEmpId = (String)session.getAttribute(EMPID);
		strUserType = (String)session.getAttribute(BASEUSERTYPE);
		
		request.setAttribute(PAGE, "/jsp/task/BillsReceipts.jsp");
		request.setAttribute(TITLE, "Bills and Receipts");
		
		UtilityFunctions uF = new UtilityFunctions();
		boolean isView  = CF.getAccess(session, request, uF);
		if(!isView){
			request.setAttribute(PAGE, PAccessDenied);
			request.setAttribute(TITLE, TAccessDenied);
			return ACCESS_DENIED;
		}
		
		System.out.println("BillReceipts--");
		request.setAttribute("arrEnabledModules", CF.getArrEnabledModules());
		request.setAttribute("DOC_RETRIVE_LOCATION", CF.getStrDocRetriveLocation());
		request.setAttribute("IS_DEVICE_INTEGRATION", CF.getIsDeviceIntegration());
		
		if(getProOwner() == null) {
			setProOwner(strSessionEmpId);
		}
		loadBillsReceipts(uF);
		checkProjectOwner(uF);
		getBillsReceiptsDetails(uF);
		
		if(getBtnSubmit() != null) {
			return SUCCESS;
		} else {
			return LOAD;
		}
//		return loadBillsReceipts(uF);

	}

	private void checkProjectOwner(UtilityFunctions uF) {
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		boolean poFlag = false;
		try {
			con = db.makeConnection(con);
			
			StringBuilder sbQuery = new StringBuilder();
		//===start parvez date: 12-10-2022===	
//			sbQuery.append("select * from projectmntnc pmc where project_owner=?");
			sbQuery.append("select * from projectmntnc pmc where project_owners like '%," + strSessionEmpId + ",%' ");
			pst = con.prepareStatement(sbQuery.toString());
//			pst.setInt(1, uF.parseToInt(strSessionEmpId));
		//===end parvez date: 12-10-2022===	
			rs = pst.executeQuery();
			if(rs.next()) {
				poFlag = true;
			}
			rs.close();
			pst.close();
			
			setPoFlag(poFlag);
			if(poFlag && uF.parseToInt(getStrProType()) == 0 && !getProOwner().equals("") && uF.parseToInt(getProOwner()) == 0){
//			if(poFlag && uF.parseToInt(getStrProType()) == 0 && uF.parseToInt(getProOwner()) == 0) {
				setStrProType("2");
			}
		} catch (Exception e) {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	private void getBillsReceiptsDetails(UtilityFunctions uF) {
		
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs=null;
		try { 
			
			String[] strCalendarYearDates = null;
			String strCalendarYearStart = null;
			String strCalendarYearEnd = null;

			if (getCalendarYear() != null) {
				strCalendarYearDates = getCalendarYear().split("-");
				strCalendarYearStart = strCalendarYearDates[0];
				strCalendarYearEnd = strCalendarYearDates[1];
			} else {
				strCalendarYearDates = new FillCalendarYears(request).fillLatestCalendarYears();
				setCalendarYear(strCalendarYearDates[0] + "-" + strCalendarYearDates[1]);
				strCalendarYearStart = strCalendarYearDates[0];
				strCalendarYearEnd = strCalendarYearDates[1];
			}
			int nYear = uF.parseToInt(uF.getDateFormat(strCalendarYearEnd+"", DATE_FORMAT, "yy"));
			
			con = db.makeConnection(con);
			
			/**
			 * Bills & receipts
			 * */
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select sum(oc_invoice_amount) as oc_invoice_amount,extract(month from invoice_generated_date) as invoice_month " +
					"from promntc_invoice_details where invoice_generated_date between ? and ? and pro_id in (select pro_id from projectmntnc where pro_id > 0 ");
		//===start parvez date: 12-10-2022===	
			if(isPoFlag() && uF.parseToInt(getStrProType()) == 2){
//				sbQuery.append(" and project_owner="+uF.parseToInt(strSessionEmpId));
				sbQuery.append(" and project_owners like '%,"+strSessionEmpId+",%'");
			} else if(uF.parseToInt(getProOwner()) > 0){
//				sbQuery.append("and project_owner="+uF.parseToInt(getProOwner()));
				sbQuery.append("and project_owners like '%,"+getProOwner()+",%'");
			}
		//===end parvez date: 12-10-2022===	
			sbQuery.append(") group by extract(month from invoice_generated_date) order by extract(month from invoice_generated_date)");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(strCalendarYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strCalendarYearEnd, DATE_FORMAT));
//			System.out.println("pst=====>"+pst);
			rs = pst.executeQuery();
			Map<String, String> hmBills = new HashMap<String, String>();
			while(rs.next()){
				hmBills.put(rs.getString("invoice_month"), uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(rs.getString("oc_invoice_amount"))));
			}
			rs.close();
			pst.close();
			
			sbQuery = new StringBuilder();
			sbQuery.append("select sum(received_amount) as received_amount,extract(month from entry_date) as entry_month from promntc_bill_amt_details " +
					"where entry_date between ? and ? and pro_id in (select pro_id from projectmntnc where pro_id > 0 ");
		//===start parvez date: 12-10-2022===	
			if(isPoFlag() && uF.parseToInt(getStrProType()) == 2){
//				sbQuery.append(" and project_owner="+uF.parseToInt(strSessionEmpId));
				sbQuery.append(" and project_owners like '%,"+strSessionEmpId+",%' ");
			} else if(uF.parseToInt(getProOwner()) > 0){
//				sbQuery.append("and project_owner="+uF.parseToInt(getProOwner()));
				sbQuery.append(" and project_owners like '%,"+getProOwner()+",%' ");
			}
		//===end parvez date: 12-10-2022===	
			sbQuery.append(") group by extract(month from entry_date) order by extract(month from entry_date)");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(strCalendarYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strCalendarYearEnd, DATE_FORMAT));
//			System.out.println("pst=====>"+pst);
			rs = pst.executeQuery();
			Map<String, String> hmReceipts = new HashMap<String, String>();
			while(rs.next()){
				hmReceipts.put(rs.getString("entry_month"), uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(rs.getString("received_amount"))));
			}
			rs.close();
			pst.close();
			
			
			StringBuilder sbBillsReceipts 	= new StringBuilder();
			for(int i = 1; i<=12; i++){
				String strBills = uF.showData(hmBills.get(""+i), "0");
				String strReceipts = uF.showData(hmReceipts.get(""+i), "0");
				
				sbBillsReceipts.append("{'month':'"+uF.getShortMonth(i)+" "+nYear+"', " +
						"'bills': "+uF.parseToDouble(strBills)+"," +
						"'receipts': "+uF.parseToDouble(strReceipts)+"},");
				
			}
			
			if(sbBillsReceipts.length()>1) {
				sbBillsReceipts.replace(0, sbBillsReceipts.length(), sbBillsReceipts.substring(0, sbBillsReceipts.length()-1));
	        }
			request.setAttribute("sbBillsReceipts", sbBillsReceipts.toString());
			/**
			 * Bills & receipts end
			 * */
			
			/**
			 * Bills donuts by sbu
			 * */
			
			sbQuery = new StringBuilder();
			sbQuery.append("select pmt.sbu_id, sum(pid.oc_invoice_amount) as oc_invoice_amount from promntc_invoice_details pid, projectmntnc pmt " +
					"where pid.pro_id>0 and pid.pro_id=pmt.pro_id and pid.invoice_generated_date between ? and ? ");
		//===start parvez date: 12-10-2022===	
			if(isPoFlag() && uF.parseToInt(getStrProType()) == 2){
//				sbQuery.append(" and pmt.project_owner="+uF.parseToInt(strSessionEmpId));
				sbQuery.append(" and pmt.project_owners like '%,"+strSessionEmpId+",%' ");
			} else if(uF.parseToInt(getProOwner()) > 0){
//				sbQuery.append("and pmt.project_owner="+uF.parseToInt(getProOwner()));
				sbQuery.append(" and pmt.project_owners like '%,"+getProOwner()+",%' ");
			}
		//===end parvez date: 12-10-2022===	
			sbQuery.append(" group by pmt.sbu_id order by pmt.sbu_id");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(strCalendarYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strCalendarYearEnd, DATE_FORMAT));
//			System.out.println("pst=====>"+pst);
			rs = pst.executeQuery();
			Map<String, String> hmBillsDoonut = new HashMap<String, String>();
			while(rs.next()){
				hmBillsDoonut.put(rs.getString("sbu_id"), uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(rs.getString("oc_invoice_amount"))));
			}
			rs.close();
			pst.close();
			
			Map<String, String> hmSbu = (Map<String, String>)CF.getServicesMap(con, false);;
			if(hmSbu == null) hmSbu = new HashMap<String, String>();
			
			Iterator<String> it1 = hmBillsDoonut.keySet().iterator();
			StringBuilder sbBillsDonut 	= new StringBuilder();
			double dblTotalAmt = 0.0d; 
			while(it1.hasNext()){
				String strSbuId = it1.next();
				String strBills = uF.showData(hmBillsDoonut.get(strSbuId), "0");
				String strSbuName = uF.showData(hmSbu.get(strSbuId), "");
				
				sbBillsDonut.append("{'sbu':'"+strSbuName.replaceAll("[^a-zA-Z0-9]", "")+"', " +
						"'bills': "+uF.parseToDouble(strBills)+"},");
				dblTotalAmt += uF.parseToDouble(strBills);
			}
			if(sbBillsDonut.length()>1) {
				sbBillsDonut.replace(0, sbBillsDonut.length(), sbBillsDonut.substring(0, sbBillsDonut.length()-1));
	        }
			request.setAttribute("sbBillsDonut", sbBillsDonut.toString());
			request.setAttribute("dblTotalAmt", uF.formatIntoTwoDecimal(dblTotalAmt));
			/**
			 * Bills donuts by sbu end
			 * */
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private void loadBillsReceipts(UtilityFunctions uF) {
		calendarYearList = new FillCalendarYears(request).fillCalendarYears(CF);
		proOwnerList = new FillProjectOwnerList(request).fillProjectOwner();
		
		getSelectedFilter(uF);
	}
	

	private void getSelectedFilter(UtilityFunctions uF) {
		Map<String, String> hmFilter = new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();
		
		if(!isPoFlag() || (strUserType!=null && (strUserType.equals(ADMIN) || strUserType.equals(HRMANAGER)))){
			alFilter.add("PROJECT_OWNER");
			if(getProOwner()!=null) {
				String strManager="";
				int k=0;
				for(int i=0;proOwnerList!=null && i<proOwnerList.size();i++) {
					if(getProOwner().equals(proOwnerList.get(i).getProOwnerId())) {
						if(k==0) {
							strManager=proOwnerList.get(i).getProOwnerName();
						} else {
							strManager+=", "+proOwnerList.get(i).getProOwnerName();
						}
						k++;
					}
				}
				if(strManager!=null && !strManager.equals("")) {
					hmFilter.put("PROJECT_OWNER", strManager);
				} else {
					hmFilter.put("PROJECT_OWNER", "All Project Owners");
				}
			} else {
				hmFilter.put("PROJECT_OWNER", "All Project Owners");
			}
		}
		alFilter.add("CALENDARYEAR");
		String[] strCalendarYearDates = null;
		if (getCalendarYear() != null) {
			strCalendarYearDates = getCalendarYear().split("-");
			setCalendarYear(strCalendarYearDates[0] + "-" + strCalendarYearDates[1]);
		} else {
			strCalendarYearDates = new FillCalendarYears(request).fillLatestCalendarYears();
			setCalendarYear(strCalendarYearDates[0] + "-" + strCalendarYearDates[1]);
		}
		hmFilter.put("CALENDARYEAR", uF.getDateFormat(strCalendarYearDates[0], DATE_FORMAT, CF.getStrReportDateFormat()) +" - "+ uF.getDateFormat(strCalendarYearDates[1], DATE_FORMAT, CF.getStrReportDateFormat()));
		
		String selectedFilter = CF.getSelectedFilter2(CF, uF, alFilter, hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}

	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public String getCalendarYear() {
		return calendarYear;
	}

	public void setCalendarYear(String calendarYear) {
		this.calendarYear = calendarYear;
	}

	public List<FillCalendarYears> getCalendarYearList() {
		return calendarYearList;
	}

	public void setCalendarYearList(List<FillCalendarYears> calendarYearList) {
		this.calendarYearList = calendarYearList;
	}

	public String getProOwner() {
		return proOwner;
	}

	public void setProOwner(String proOwner) {
		this.proOwner = proOwner;
	}

	public List<FillProjectOwnerList> getProOwnerList() {
		return proOwnerList;
	}

	public void setProOwnerList(List<FillProjectOwnerList> proOwnerList) {
		this.proOwnerList = proOwnerList;
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
