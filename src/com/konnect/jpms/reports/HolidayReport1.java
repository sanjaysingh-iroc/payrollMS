package com.konnect.jpms.reports;

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

import com.konnect.jpms.select.FillColour;
import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class HolidayReport1 extends ActionSupport implements ServletRequestAware, IStatements {

	/**     
	 * 
	 */ 
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType;
	String strSessionEmpId;
	CommonFunctions CF=null;
	List<FillDepartment> deptList;
	List<FillWLocation> wLocationList;
	List<FillColour> colourCodeList;
	
	
	List<FillOrganisation> orgList;
	String strOrg;
	String strWLocation;
	
	Map<String, String> hm_DeptId_DeptName;
	Map<String, String> hm_DeptId_WlocationName;
	private static Logger log = Logger.getLogger(HolidayReport.class);
	
	public String execute() throws Exception {
		UtilityFunctions uF = new UtilityFunctions();
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		strUserType = (String)session.getAttribute(USERTYPE);
		strSessionEmpId = (String)session.getAttribute(EMPID);
				
		if(strUserType!=null&& strUserType.equalsIgnoreCase(EMPLOYEE)){
			request.setAttribute(PAGE, PReportHolidayE);
			request.setAttribute(TITLE, TViewHolidays);
		}else if(strUserType!=null){
			request.setAttribute(PAGE, PReportHoliday1);
			request.setAttribute(TITLE, TViewHolidays);
		}
		
		
		boolean isView  = CF.getAccess(session, request, uF);
		if(!isView){
			request.setAttribute(PAGE, PAccessDenied);
			request.setAttribute(TITLE, TAccessDenied);
			return ACCESS_DENIED;
		}
		
		
		orgList = new FillOrganisation(request).fillOrganisation();
		if(getStrOrg()==null && orgList!=null && orgList.size()>0){
			setStrOrg(orgList.get(0).getOrgId());
		}
		
		viewHolidayReport(uF);			
		return loadHolidayReport();

	}
	
	
	public String loadHolidayReport() {
		 
		deptList =  new FillDepartment(request).fillDepartment();
		wLocationList = new FillWLocation(request).fillWLocation(getStrOrg());
		colourCodeList = new FillColour(request).fillColour();
		
		request.setAttribute("deptList", deptList);
		request.setAttribute("wLocationList", wLocationList);
		request.setAttribute("colourCodeList", colourCodeList);
		
		int i;
		if(wLocationList.size()!=0) {
			int wLocationId;
			String wLocationName;
			StringBuilder sbWLocationList = new StringBuilder();
			sbWLocationList.append("{");
			for (i = 0; i < wLocationList.size() - 1; i++) {
				wLocationId = Integer.parseInt((wLocationList.get(i))
						.getwLocationId());
				wLocationName = wLocationList.get(i).getwLocationName();
				sbWLocationList.append("\"" + wLocationId + "\":\"" + wLocationName
						+ "\",");
			}
			wLocationId = Integer.parseInt((wLocationList.get(i)).getwLocationId());
			wLocationName = wLocationList.get(i).getwLocationName();
			sbWLocationList.append("\"" + wLocationId + "\":\"" + wLocationName
					+ "\"");
			sbWLocationList.append("}");
			request.setAttribute("sbWLocationList", sbWLocationList.toString());
		}

		if(deptList.size()!=0) {
			int deptId;
			String deptName;
			StringBuilder sbDeptList = new StringBuilder();
			sbDeptList.append("{");
			for (i = 0; i < deptList.size() - 1; i++) {
				deptId = Integer.parseInt((deptList.get(i)).getDeptId());
				deptName = deptList.get(i).getDeptName();
				sbDeptList.append("\"" + deptId + "\":\"" + deptName + "\",");
			}
			deptId = Integer.parseInt((deptList.get(i)).getDeptId());
			deptName = deptList.get(i).getDeptName();
			sbDeptList.append("\"" + deptId + "\":\"" + deptName + "\"");
			sbDeptList.append("}");
			request.setAttribute("sbDeptList", sbDeptList.toString());
		}
		
		if(colourCodeList.size()!=0) {
			String colourValue;
			String colourName;
			StringBuilder sbColourList = new StringBuilder();
			sbColourList.append("{");
			for (i = 0; i < colourCodeList.size() - 1; i++) {
				colourValue = (colourCodeList.get(i)).getColourValue();
				colourName = colourCodeList.get(i).getColourName();
				sbColourList.append("\"" + colourValue + "\":\"" + colourName + "\",");
			}
			colourValue = (colourCodeList.get(i)).getColourValue();
			colourName = colourCodeList.get(i).getColourName();
			sbColourList.append("\"" + colourValue + "\":\"" + colourName + "\",");
			sbColourList.append("}");
			log.debug("sbColourList==>"+sbColourList.toString());
			request.setAttribute("sbColorList", sbColourList.toString());
		}
		
	
		return LOAD;
	}
	
	public String viewHolidayReport(UtilityFunctions uF) {
		
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		String Deptids[]={};
		
		try {

			if(session==null) {
				session = request.getSession();
				CF = (CommonFunctions)session.getAttribute(CommonFunctions);
			}
			
			List<List<String>> al = new ArrayList<List<String>>();
			List<String> alEmp = new ArrayList<String>();
			List<String> alInner = new ArrayList<String>();
			List<String> alTempInner = new ArrayList<String>();
			
			con = db.makeConnection(con);
			
			Map<String, Map<String,String>> hmWLocationMap = CF.getWorkLocationMap(con);
			
			if(strUserType!=null && strUserType.equalsIgnoreCase(EMPLOYEE)){
				pst = con.prepareStatement(selectHolidaysE);
				pst.setInt(1, uF.parseToInt(strSessionEmpId));
			}else{
				StringBuilder sb = new StringBuilder();
				sb.append("SELECT * FROM holidays where org_id =?");
				if(uF.parseToInt(getStrWLocation())>0){
					sb.append(" and wlocation_id =?");	
				}
				sb.append(" order by _date desc");
				
				pst = con.prepareStatement(sb.toString());
				pst.setInt(1, uF.parseToInt(getStrOrg()));
			}
			
			rs = pst.executeQuery();
			while(rs.next()) {
				
				alInner = new ArrayList<String>();
				
				alInner.add(Integer.toString(rs.getInt("holiday_id")));
//				log.debug("date====>"+uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat()));
				alInner.add(uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat()));
				alInner.add(uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDayFormat()));
				alInner.add(uF.strDecoding(rs.getString("description")));
				
				
				Map<String, String> hmWLocation = hmWLocationMap.get(rs.getString("wlocation_id"));
				if(hmWLocation==null)hmWLocation=new HashMap<String, String>();
				
				alInner.add(uF.showData(hmWLocation.get("WL_NAME"), ""));
				
				alInner.add("<div style=\"height:10px; background-color:"+rs.getString("colour_code")+"\">");
//				alInner.add("<a href="+request.getContextPath()+"/AddHolidays.action?E="+rs.getString("holiday_id")+">Edit</a> | <a onclick=\"return confirm('"+ConfirmDelete+"');\" href="+request.getContextPath()+"/AddHolidays.action?D="+rs.getString("holiday_id")+">Delete</a>");
				al.add(alInner);
				
				alEmp.add("{title: '"+rs.getString("description")+"',start: new Date("+uF.getDateFormat(rs.getString("_date"), "yyyy-MM-dd", "yyyy")+", "+(uF.parseToInt(uF.getDateFormat(rs.getString("_date"), "yyyy-MM-dd", "M"))-1)+", "+uF.getDateFormat(rs.getString("_date"), "yyyy-MM-dd", "dd")+")}");

			}
			rs.close();
			pst.close();
			
			request.setAttribute("reportList", al);
			request.setAttribute("reportListEmp", alEmp);
			
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
	
	public void getDepartmentDetails() {

		hm_DeptId_DeptName = new HashMap<String, String>();
		hm_DeptId_WlocationName = new HashMap<String, String>();
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		try {

			con = db.makeConnection(con);
			
//			pst = con.prepareStatement(selectDepartmentWlocationName);
			pst = con.prepareStatement(selectDepartment);
			
			rs = pst.executeQuery();

			while (rs.next()) {
				hm_DeptId_DeptName.put(rs.getString("dept_id"), rs.getString("dept_name"));
//				hm_DeptId_WlocationName.put(rs.getString("dept_id"), rs.getString("wlocation_name"));
			}
			rs.close();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}


	public List<FillOrganisation> getOrgList() {
		return orgList;
	}


	public void setOrgList(List<FillOrganisation> orgList) {
		this.orgList = orgList;
	}


	public String getStrOrg() {
		return strOrg;
	}


	public void setStrOrg(String strOrg) {
		this.strOrg = strOrg;
	}


	public List<FillWLocation> getwLocationList() {
		return wLocationList;
	}


	public void setwLocationList(List<FillWLocation> wLocationList) {
		this.wLocationList = wLocationList;
	}


	public String getStrWLocation() {
		return strWLocation;
	}


	public void setStrWLocation(String strWLocation) {
		this.strWLocation = strWLocation;
	}

}
