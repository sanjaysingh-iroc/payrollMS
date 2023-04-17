package com.konnect.jpms.recruitment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillDesig;
import com.konnect.jpms.select.FillGrade;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class PublishJob extends ActionSupport implements ServletRequestAware,
		IStatements {
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	boolean isEmpUserType = false;
	CommonFunctions CF = null;
	private static Logger log = Logger.getLogger(RequirementApproval.class);

	String strSessionEmpId = null;

	public String execute() throws Exception {
		UtilityFunctions uF = new UtilityFunctions();
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;
		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);

		request.setAttribute(TITLE, "Publish Job");
		request.setAttribute(PAGE, "/jsp/recruitment/PublishJob.jsp");

		workLocationList = new FillWLocation(request).fillWLocation();
		desigList = new FillDesig(request).fillDesig();
		gradeList = new FillGrade(request).fillGrade();
		serviceslist = new FillServices(request).fillServices();

		viewAllJobPublishList(uF);
		return LOAD;

	}

	List<FillDesig> desigList;
	List<FillGrade> gradeList;
	List<FillWLocation> workLocationList;
	List<FillServices> serviceslist;

	String empGrade;
	String location;
	String designation;
	String services;
	String checkStatus;
	String fdate;
	String tdate;

	public List<FillDesig> getDesigList() {
		return desigList;
	}

	public void setDesigList(List<FillDesig> desigList) {
		this.desigList = desigList;
	}

	public List<FillGrade> getGradeList() {
		return gradeList;
	}

	public void setGradeList(List<FillGrade> gradeList) {
		this.gradeList = gradeList;
	}

	public List<FillWLocation> getWorkLocationList() {
		return workLocationList;
	}

	public void setWorkLocationList(List<FillWLocation> workLocationList) {
		this.workLocationList = workLocationList;
	}

	public List<FillServices> getServiceslist() {
		return serviceslist;
	}

	public void setServiceslist(List<FillServices> serviceslist) {
		this.serviceslist = serviceslist;
	}

	public String getEmpGrade() {
		return empGrade;
	}

	public void setEmpGrade(String empGrade) {
		this.empGrade = empGrade;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getDesignation() {
		return designation;
	}

	public void setDesignation(String designation) {
		this.designation = designation;
	}

	public String getServices() {
		return services;
	}

	public void setServices(String services) {
		this.services = services;
	}

	public String getCheckStatus() {
		return checkStatus;
	}

	public void setCheckStatus(String checkStatus) {
		this.checkStatus = checkStatus;
	}

	public String getFdate() {
		return fdate;
	}

	public void setFdate(String fdate) {
		this.fdate = fdate;
	}

	public String getTdate() {
		return tdate;
	}

	public void setTdate(String tdate) {
		this.tdate = tdate;
	}

	private void viewAllJobPublishList(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);

		List<List<String>> requestList = new ArrayList<List<String>>();
		List<String> innerRequestList;

		int depart_id = 0;
		int wlocation_id = 0;

		try {

			if (getFdate() == null && getTdate() == null) {

				Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(CF
						.getStrTimeZone()));
				int nMaxDate = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
				int nMinDate = cal.getActualMinimum(Calendar.DAY_OF_MONTH);

				setFdate(uF.getDateFormat(
						nMinDate + "/" + (cal.get(Calendar.MONTH) + 1) + "/"
								+ cal.get(Calendar.YEAR), DATE_FORMAT,
						DATE_FORMAT));
				setTdate(uF.getDateFormat(
						nMaxDate + "/" + (cal.get(Calendar.MONTH) + 1) + "/"
								+ cal.get(Calendar.YEAR), DATE_FORMAT,
						DATE_FORMAT));

			}

			con = db.makeConnection(con);

			String query1 = "select depart_id,wlocation_id from employee_official_details where emp_id=?";
			pst = con.prepareStatement(query1);
			pst.setInt(1, uF.parseToInt(strSessionEmpId));
			rst = pst.executeQuery();
			while (rst.next()) {
				depart_id = rst.getInt(1);
				wlocation_id = rst.getInt(2);
			}
			rst.close();
			pst.close();
			
//			System.out.println("dept id in ra " + depart_id + " location " + wlocation_id);
			StringBuilder strQuery = new StringBuilder();

			strQuery.append("select r.recruitment_id,d.designation_name,g.grade_code,w.wlocation_name,r.no_position,r.effective_date,r.job_code,r.job_approval_status,s.service_name,di.dept_name,e.emp_fname,e.emp_lname,l.level_code,l.level_name,close_job_status  "
					+ "from recruitment_details r,grades_details g,work_location_info w,designation_details d,services s,department_info di,employee_personal_details e,level_details l where r.grade_id=g.grade_id and "
					+ "r.wlocation=w.wlocation_id and r.designation_id=d.designation_id and r.status=1 and min_exp is not null and r.services=s.service_id and r.dept_id=di.dept_id and r.added_by=e.emp_per_id  and r.level_id=l.level_id"
					+ " and r.job_approval_status=1 and close_job_status=false  ");
			/*
			 * if (strUserType != null &&
			 * (strUserType.equalsIgnoreCase(HRMANAGER))) {
			 * System.out.println("in HRMANAGER dept query");
			 * System.out.println("strSessionEmpId " + strSessionEmpId);
			 * strQuery.append(" and r.dept_id=" + depart_id); }
			 */
			if (strUserType != null
					&& (strUserType.equalsIgnoreCase(ADMIN))) {
			}else if (strUserType != null
					&& (strUserType.equalsIgnoreCase(HRMANAGER))) {
				strQuery.append(" and r.wlocation=" + wlocation_id);
			}
			
			if (uF.parseToInt(getLocation()) > 0) {
//				System.out.println("in location query");
				strQuery.append(" and r.wlocation="
						+ uF.parseToInt(getLocation()));
			}
			if (uF.parseToInt(getEmpGrade()) > 0) {
//				System.out.println("in grade query");
				strQuery.append("and r.grade_id="
						+ uF.parseToInt(getEmpGrade()));
			}
			if (uF.parseToInt(getDesignation()) > 0) {
//				System.out.println("in designation query");
				strQuery.append(" and r.designation_id="
						+ uF.parseToInt(getDesignation()));
			}
			if (uF.parseToInt(getServices()) > 0) {
//				System.out.println("in services query");
				strQuery.append(" and r.services="
						+ uF.parseToInt(getServices()));
			}
			if ((getFdate() != null && !getFdate().equals(""))
					&& (getTdate() != null && !getTdate().equals(""))) {
//				System.out.println("in between date query");
//				System.out.println("Fdate "
//						+ uF.getDateFormat(getFdate(), DATE_FORMAT) + " Tdate "
//						+ uF.getDateFormat(getTdate(), DATE_FORMAT));

				strQuery.append(" and r.effective_date >='"
						+ uF.getDateFormat(getFdate(), DATE_FORMAT)
						+ "' and r.effective_date <='"
						+ uF.getDateFormat(getTdate(), DATE_FORMAT) + "' ");
			}

			/*
			 * if (getCheckStatus() != null && uF.parseToInt(getCheckStatus()) >
			 * -2) { System.out.println("in status");
			 * System.out.println("in status==>" + getCheckStatus());
			 * strQuery.append(" and r.job_approval_status = " +
			 * getCheckStatus() + " "); }
			 */
			/*
			 * else{ strQuery.append(" and r.job_approval_status=1 "); }
			 */
			strQuery.append(" order by r.job_code desc");

			pst = con.prepareStatement(strQuery.toString());
//			pst.setInt(1,uF.parseToInt(strSessionEmpId));
			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			int nCount = 0;
			StringBuilder sb = new StringBuilder();
			while (rst.next()) {
				if (nCount == 0) {
					sb.append(rst.getString(1));
				} else {
					sb.append("," + rst.getString(1));
				}
				innerRequestList = new ArrayList<String>();
				innerRequestList.add(rst.getString(1));
				innerRequestList.add(rst.getString(2));
				innerRequestList.add(rst.getString(3));
				innerRequestList.add(rst.getString(4));
				innerRequestList.add(rst.getString(5));
				innerRequestList.add(uF.getDateFormat(
						removeNUll(rst.getString(6)), DBDATE, DATE_FORMAT));
				innerRequestList.add(rst.getString(7));

				StringBuilder sbApproveDeny = new StringBuilder();
				
				sbApproveDeny
						.append("<a href=\"javascript:void(0)\" onclick=\"if(confirm('Do you want to download xml file?'))"
								+ "downloadXML('"
								+ rst.getString(1)
								+ "');\" ><img src=\"images1/file-xml.png\" title=\"Download XML\" /></a> ");

				innerRequestList.add(sbApproveDeny.toString());
				innerRequestList.add(rst.getString(9));
				innerRequestList.add(rst.getString(10));
				innerRequestList.add(removeNUll(rst.getString(11)) + " "
						+ removeNUll(rst.getString(12)));
				innerRequestList.add(removeNUll("[" + rst.getString(13)) + "] "
						+ removeNUll(rst.getString(14)));

				requestList.add(innerRequestList);
				nCount++;
			}
			rst.close();
			pst.close();

			request.setAttribute("sb", sb.toString());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

		request.setAttribute("publishJobList", requestList);
	}

	private String removeNUll(String strNull) {

		if (strNull == null) {
			strNull = "";
		}
		return strNull;
	}

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}
}
