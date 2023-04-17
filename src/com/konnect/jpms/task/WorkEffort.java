package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillCalendarYears;
import com.konnect.jpms.select.FillMonth;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class WorkEffort extends ActionSupport implements ServletRequestAware, IStatements {
 
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	HttpSession session; 
	String strSessionEmpId;
	String strUserType =  null;
	CommonFunctions CF; 
	
	String strMonth;
	String strYear;
	
	List<FillMonth> monthList;
	
	String proPage;
	String minLimit;
	String proType;
	
	String proOwner;
	List<FillProjectOwnerList> proOwnerList;
	
	String calendarYear;
	List<FillCalendarYears> calendarYearList;

	String strProType;
	boolean poFlag;
	
	String btnSubmit;
	
	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)return LOGIN;

		UtilityFunctions uF = new UtilityFunctions();
		boolean isView  = CF.getAccess(session, request, uF);
		if(!isView){
			request.setAttribute(PAGE, PAccessDenied);
			request.setAttribute(TITLE, TAccessDenied);
			return ACCESS_DENIED;
		}
		
		strSessionEmpId = (String)session.getAttribute(EMPID);
		strUserType = (String)session.getAttribute(BASEUSERTYPE);
		
		request.setAttribute(PAGE, "/jsp/task/WorkEffort.jsp");
		request.setAttribute(TITLE, "Work Effort");
		
		request.setAttribute("arrEnabledModules", CF.getArrEnabledModules());
		request.setAttribute("DOC_RETRIVE_LOCATION", CF.getStrDocRetriveLocation());
		request.setAttribute("IS_DEVICE_INTEGRATION", CF.getIsDeviceIntegration());
		
//		System.out.println("getProOwner() ===>> " + getProOwner());
		if(getProOwner() == null) {
			setProOwner(strSessionEmpId);
		}
		
		if(uF.parseToInt(getProPage()) == 0) {
			setProPage("1");
		}
		
		loadWorkEffort(uF);
		checkProjectOwner(uF);
		getResourceWorkEffortDetails(uF);
		
//		return loadWorkEffort(uF);
		if(getBtnSubmit() != null) {
			return SUCCESS;
		} else {
			return LOAD;
		}

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
			
			if(poFlag && uF.parseToInt(getStrProType()) == 0 && !getProOwner().equals("") && uF.parseToInt(getProOwner()) == 0){
				setStrProType("2");
			}
			
		} catch (Exception e) {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	private void getResourceWorkEffortDetails(UtilityFunctions uF) {
		
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
			
			
			setStrYear(uF.parseToInt(uF.getDateFormat(strCalendarYearEnd+"", DATE_FORMAT, "yyyy"))+"");
			
			List<List<String>> weekdates = new ArrayList<List<String>>();
			uF.getMonthWeeksDate(weekdates,getStrMonth(),getStrYear(),DATE_FORMAT);
			
			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select count(*) as cnt from employee_personal_details epd,employee_official_details eod " +
					"where epd.emp_per_id = eod.emp_id ");
			sbQuery.append("and eod.emp_id in(select distinct(emp_id) from project_emp_details where emp_id > 0 ");
			
			if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
				sbQuery.append(" and eod.wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
            
			if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
				sbQuery.append(" and eod.org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
        
		//===start parvez date: 13-10-2022===	
			if(isPoFlag() && uF.parseToInt(getStrProType()) == 2) {
				/*sbQuery.append(" and (pro_id in (select pro_id from projectmntnc where project_owner="+uF.parseToInt(strSessionEmpId)+") " +
					" or pro_id in (select pro_id from project_emp_details where _isteamlead =true and emp_id="+uF.parseToInt(strSessionEmpId)+"))");*/
				sbQuery.append(" and (pro_id in (select pro_id from projectmntnc where project_owners like '%,"+strSessionEmpId+",%') " +
						" or pro_id in (select pro_id from project_emp_details where _isteamlead =true and emp_id="+uF.parseToInt(strSessionEmpId)+"))");
			} else if(uF.parseToInt(getProOwner()) > 0) {
				/*sbQuery.append(" and (pro_id in (select pro_id from projectmntnc where project_owner="+uF.parseToInt(getProOwner())+") " +
					"or pro_id in (select pro_id from project_emp_details where _isteamlead =true and emp_id="+uF.parseToInt(getProOwner())+"))");*/
				sbQuery.append(" and (pro_id in (select pro_id from projectmntnc where project_owners like '%,"+getProOwner()+",%') " +
						"or pro_id in (select pro_id from project_emp_details where _isteamlead =true and emp_id="+uF.parseToInt(getProOwner())+"))");
			}
		//===end parvez date: 13-10-2022===	
			sbQuery.append(" ) limit 50");
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst======>"+pst);
			rs = pst.executeQuery();
			int proCount = 0;
			int proCnt = 0;
			while(rs.next()) {
				proCnt = rs.getInt("cnt");
				proCount = rs.getInt("cnt")/20;
				if(rs.getInt("cnt")%20 != 0) {
					proCount++;
				}
			}
			rs.close();
			pst.close();
			request.setAttribute("proCount", proCount+"");
			request.setAttribute("proCnt", proCnt+"");
			
			
			sbQuery = new StringBuilder();
			sbQuery.append("select eod.emp_id,epd.emp_fname,epd.emp_mname,epd.emp_lname,epd.emp_image from employee_personal_details epd,employee_official_details eod " +
					"where epd.emp_per_id = eod.emp_id ");
			sbQuery.append("and eod.emp_id in(select distinct(emp_id) from project_emp_details where emp_id > 0 ");

			if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
				sbQuery.append(" and eod.wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
            
           if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
				sbQuery.append(" and eod.org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
        //===start parvez date: 13-10-2022===   
			if(isPoFlag() && uF.parseToInt(getStrProType()) == 2) {
				/*sbQuery.append(" and (pro_id in (select pro_id from projectmntnc where project_owner="+uF.parseToInt(strSessionEmpId)+") " +
					" or pro_id in (select pro_id from project_emp_details where _isteamlead =true and emp_id="+uF.parseToInt(strSessionEmpId)+"))");*/
				sbQuery.append(" and (pro_id in (select pro_id from projectmntnc where project_owners like '%,"+strSessionEmpId+",%') " +
						" or pro_id in (select pro_id from project_emp_details where _isteamlead =true and emp_id="+uF.parseToInt(strSessionEmpId)+"))");
			} else if(uF.parseToInt(getProOwner()) > 0){
				/*sbQuery.append(" and (pro_id in (select pro_id from projectmntnc where project_owner="+uF.parseToInt(getProOwner())+") " +
					"or pro_id in (select pro_id from project_emp_details where _isteamlead =true and emp_id="+uF.parseToInt(getProOwner())+"))");*/
				sbQuery.append(" and (pro_id in (select pro_id from projectmntnc where project_owners like '%,"+getProOwner()+",%') " +
						"or pro_id in (select pro_id from project_emp_details where _isteamlead =true and emp_id="+uF.parseToInt(getProOwner())+"))");
			}
		//===end parvez date: 13-10-2022===	
			sbQuery.append(")");
			sbQuery.append(" order by epd.emp_fname,epd.emp_lname");
			int intOffset = uF.parseToInt(getMinLimit());
			sbQuery.append(" limit 20 offset "+intOffset+"");
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst======>"+pst);
			rs = pst.executeQuery();
			List<Map<String, String>> alPeople = new ArrayList<Map<String,String>>();
			while(rs.next()) {
				Map<String, String> hmPeople = new HashMap<String, String>();
				hmPeople.put("EMP_ID", rs.getString("emp_id"));	
				/*String strMiddleName = "";
				if(rs.getString("emp_mname")!=null && !rs.getString("emp_mname").trim().equals("") && !rs.getString("emp_mname").trim().equalsIgnoreCase("NULL")){
					strMiddleName = rs.getString("emp_mname")+" ";
				}*/
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
			
				
				hmPeople.put("EMP_NAME", uF.showData(rs.getString("emp_fname"), "")+strEmpMName+" "+uF.showData(rs.getString("emp_lname"), ""));
				hmPeople.put("EMP_IMAGE", uF.showData(rs.getString("emp_image"), ""));
				
				alPeople.add(hmPeople); 
			}
			rs.close();
			pst.close();
			
			Map<String, String> hmBillable = new HashMap<String, String>();
            Map<String, String> hmNonBillable = new HashMap<String, String>();
            Map<String, String> hmOther = new HashMap<String, String>();
            
            Map<String, String> hmBillableTotalCnt = new HashMap<String, String>();
	        Map<String, String> hmNonBillableTotalCnt = new HashMap<String, String>();
	        Map<String, String> hmOtherTotalCnt = new HashMap<String, String>();
            
			if(alPeople != null && alPeople.size() > 0){
				for(int j =0; j<alPeople.size(); j++){
					Map<String, String> hmPeople = alPeople.get(j);
					String strEmpId = hmPeople.get("EMP_ID");
					StringBuilder sbBillableCount = null;
					StringBuilder sbNonBillableCount = null;
					StringBuilder sbOtherCount = null;
					double dblBillableCnt = 0.0d;
					double dblNonBillableCnt = 0.0d;
					double dblOtherCnt = 0.0d;
					int x = 0;
					
					for(int i = 0; weekdates!=null && i < weekdates.size();i++){
						List<String> week = weekdates.get(i);
						x++;
						/**Billable Count
						 * */
						pst = con.prepareStatement("select sum(billable_hrs) as billable_hrs from task_activity where emp_id = ? and activity_id > 0 " +
								"and task_date between ? and ? and is_billable =true");
						pst.setInt(1, uF.parseToInt(strEmpId));
						pst.setDate(2, uF.getDateFormat(week.get(0), DATE_FORMAT));
						pst.setDate(3, uF.getDateFormat(week.get(1), DATE_FORMAT));
//						System.out.println("pst======>"+pst);
						rs = pst.executeQuery();
						double dblBillable = 0.0d;
						while(rs.next()){
							dblBillable = uF.parseToDouble(rs.getString("billable_hrs"));
						}
						rs.close();
						pst.close();
						
						dblBillable = uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblBillable));
						
						if(sbBillableCount == null){
							sbBillableCount = new StringBuilder();
							sbBillableCount.append(""+dblBillable);
						} else {
							sbBillableCount.append(","+dblBillable);
						}
						dblBillableCnt +=dblBillable;
						
						double dblBillableTotalCnt = uF.parseToDouble(hmBillableTotalCnt.get(x+"week"));
						dblBillableTotalCnt +=dblBillable;
						hmBillableTotalCnt.put(x+"week", ""+dblBillableTotalCnt);
						/**Billable Count end
						 * */
						
						/**NonBillable Count
						 * */
						pst = con.prepareStatement("select sum(billable_hrs) as billable_hrs, sum(actual_hrs) as actual_hrs from task_activity " +
								"where emp_id = ? and activity_id > 0 and task_date between ? and ? ");
						pst.setInt(1, uF.parseToInt(strEmpId));
						pst.setDate(2, uF.getDateFormat(week.get(0), DATE_FORMAT));
						pst.setDate(3, uF.getDateFormat(week.get(1), DATE_FORMAT));
						rs = pst.executeQuery();
						double dblNonBillable = 0.0d;
						while(rs.next()){
							dblNonBillable = uF.parseToDouble(rs.getString("actual_hrs")) - uF.parseToDouble(rs.getString("billable_hrs"));
						}
						rs.close();
						pst.close();
						
						dblNonBillable = uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblNonBillable));
						
						if(sbNonBillableCount == null){
							sbNonBillableCount = new StringBuilder();
							sbNonBillableCount.append(""+dblNonBillable);
						} else {
							sbNonBillableCount.append(","+dblNonBillable);
						}
						dblNonBillableCnt +=dblNonBillable;
						
						double dblNonBillableTotalCnt = uF.parseToDouble(hmNonBillableTotalCnt.get(x+"week"));
						dblNonBillableTotalCnt +=dblNonBillable;
						hmNonBillableTotalCnt.put(x+"week", ""+dblNonBillableTotalCnt);
						
						/**NonBillable Count
						 * */
						/**Other Count
						 * */
						pst = con.prepareStatement("select sum(actual_hrs) as actual_hrs from task_activity where emp_id = ? and activity_id = 0 " +
								"and (activity_id > 0 and activity is not null) and task_date between ? and ?");
						pst.setInt(1, uF.parseToInt(strEmpId));
						pst.setDate(2, uF.getDateFormat(week.get(0), DATE_FORMAT));
						pst.setDate(3, uF.getDateFormat(week.get(1), DATE_FORMAT));
						rs = pst.executeQuery();
						double dblOther = 0.0d;
						while(rs.next()){
							dblOther = uF.parseToDouble(rs.getString("actual_hrs"));
						}
						rs.close();
						pst.close();
						
						dblOther = uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblOther));
						
						if(sbOtherCount == null){
							sbOtherCount = new StringBuilder();
							sbOtherCount.append(""+dblOther);
						} else {
							sbOtherCount.append(","+dblOther);
						}
						dblOtherCnt +=dblOther;
						
						double dblOtherTotalCnt = uF.parseToDouble(hmOtherTotalCnt.get(x+"week"));
						dblOtherTotalCnt +=dblOther;
						hmOtherTotalCnt.put(x+"week", ""+dblOtherTotalCnt);
						
						/**Other Count
						 * */
						
					}
					
					hmBillable.put(strEmpId+"_BILLABLE", sbBillableCount.toString());
					hmBillable.put(strEmpId+"_BILLABLE_COUNT", uF.formatIntoOneDecimalWithOutComma(dblBillableCnt));
					
					hmNonBillable.put(strEmpId+"_NON_BILLABLE", sbNonBillableCount.toString());
					hmNonBillable.put(strEmpId+"_NON_BILLABLE_COUNT", uF.formatIntoOneDecimalWithOutComma(dblNonBillableCnt));
					
					hmOther.put(strEmpId+"_OTHER", sbOtherCount.toString());
					hmOther.put(strEmpId+"_OTHER_COUNT", uF.formatIntoOneDecimalWithOutComma(dblOtherCnt));
				}
			}
			
			StringBuilder sbWork 	= new StringBuilder();
			int x = 0;
			for(int i = 0; weekdates!=null && i < weekdates.size();i++){
				x++;
				sbWork.append("{'week':'"+x+"wk', " +
						"'billable': "+uF.formatIntoOneDecimalWithOutComma(uF.parseToDouble(hmBillableTotalCnt.get(x+"week")))+"," +
						"'non-billable': "+uF.formatIntoOneDecimalWithOutComma(uF.parseToDouble(hmNonBillableTotalCnt.get(x+"week")))+"," +
						"'other': "+uF.formatIntoOneDecimalWithOutComma(uF.parseToDouble(hmOtherTotalCnt.get(x+"week")))+"},");
				
            }
            if(sbWork.length()>1) {
				sbWork.replace(0, sbWork.length(), sbWork.substring(0, sbWork.length()-1));
            }
			
			request.setAttribute("alPeople", alPeople);
			request.setAttribute("hmBillable", hmBillable);
			request.setAttribute("hmNonBillable", hmNonBillable);
			request.setAttribute("hmOther", hmOther);
			
			request.setAttribute("sbWork", sbWork.toString());
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private void loadWorkEffort(UtilityFunctions uF) {
		monthList = new FillMonth().fillMonth();
		calendarYearList = new FillCalendarYears(request).fillCalendarYears(CF);
		proOwnerList = new FillProjectOwnerList(request).fillProjectOwner();
		
		int cnt = 0;
//		System.out.println("getProOwner() ===>> " + getProOwner());
		for(int i=0; proOwnerList != null && i<proOwnerList.size(); i++) {
			if(getProOwner().equals(proOwnerList.get(i).getProOwnerId())) {
				cnt++;
			}
		}
//		System.out.println("cnt ===>> " + cnt);
		if(cnt == 0) {
			setProOwner("");
		}
		if(getStrMonth() == null){
			setStrMonth(uF.parseToInt(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, "MM"))+"");
		}
//		System.out.println(" ------ getProOwner() ===>> " + getProOwner());
		getSelectedFilter(uF);
		/*if(getBtnSubmit() != null) {
			return SUCCESS;
		} else {
			return LOAD;
		}*/
	}

	private void getSelectedFilter(UtilityFunctions uF) {
		Map<String, String> hmFilter = new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();
		
		if(!isPoFlag() || (strUserType!=null && (strUserType.equals(ADMIN) || strUserType.equals(HRMANAGER)))) {
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
		
		alFilter.add("MONTH");
		int nselectedMonth = uF.parseToInt(getStrMonth());
		String strMonth = uF.getMonth(nselectedMonth);
		hmFilter.put("MONTH", strMonth);

		String selectedFilter = CF.getSelectedFilter2(CF, uF, alFilter, hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}

	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public String getStrMonth() {
		return strMonth;
	}

	public void setStrMonth(String strMonth) {
		this.strMonth = strMonth;
	}

	public String getStrYear() {
		return strYear;
	}

	public void setStrYear(String strYear) {
		this.strYear = strYear;
	}

	public List<FillMonth> getMonthList() {
		return monthList;
	}

	public void setMonthList(List<FillMonth> monthList) {
		this.monthList = monthList;
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

	public String getProType() {
		return proType;
	}

	public void setProType(String proType) {
		this.proType = proType;
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
