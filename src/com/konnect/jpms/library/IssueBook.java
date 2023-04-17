package com.konnect.jpms.library;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillEmployee;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IConstants;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UserAlerts;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class IssueBook extends ActionSupport implements ServletRequestAware, IStatements {
	private static final long serialVersionUID = 1L;
	public HttpSession session;
	String strSessionOrgId;
	String strSessionEmpId;
	String strUserType;
	
	public CommonFunctions CF;
	
	private String bookId;
	private String bookIssuedId;
	private String type;
	private String strEmpId;
	private String bookName;
	
	private String strQuantityIssued;
	private String strIssuedDate;
	private String strReturnDate;
	private String strComment;
	private String strStartDate;
	private String strEndDate;
	
	private String availQuantity;
	private String strQuantityReq;
	private List<FillEmployee> empList;
	
	private String strSubmit;
	
	private static Logger log = Logger.getLogger(IssueBook.class);
	
	public String execute() throws Exception{
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)	return LOGIN;

		strUserType = (String)session.getAttribute(USERTYPE);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		
		UtilityFunctions uF = new UtilityFunctions();
		
		request.setAttribute(PAGE, PIssueBook);
		request.setAttribute(TITLE, TIssueBookRequest);
		
		empList = new FillEmployee(request).fillAllLiveEmployees(CF, strUserType, strSessionEmpId,uF.parseToInt((String)session.getAttribute(ORGID)));
	
		getBookDetails(uF);
		
		if(getStrSubmit()!=null) {
			issueOrReturnBook(uF);
			return LOAD;
		}
		
		return SUCCESS;
	}
	
		
	private void getBookDetails(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from book_details where book_id=?");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(getBookId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				setBookName(rs.getString("book_title"));
				setAvailQuantity(rs.getString("available_book_quantity"));
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
	
	
	private void issueOrReturnBook(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs =null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			Map<String, String> hmUserTypeId = CF.getUserTypeIdMap(con);
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("insert into book_issues_returns (book_id, request_quantity, issue_status, from_date, to_date, requested_by, " +
				"request_date, request_comment) values (?,?,?,?,?,?,?,?)");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(getBookId()));
			pst.setInt(2, uF.parseToInt(getStrQuantityReq()));
			pst.setInt(3, 0);
			pst.setDate(4, uF.getDateFormat(getStrStartDate(), DATE_FORMAT));
			pst.setDate(5, uF.getDateFormat(getStrEndDate(), DATE_FORMAT));
			pst.setInt(6, uF.parseToInt(strSessionEmpId));
			pst.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setString(8, getStrComment());
		    pst.execute();
			pst.close();
			
			pst = con.prepareStatement("select emp_per_id from employee_official_details eod,user_details ud, employee_personal_details epd where epd.emp_per_id=eod.emp_id and ud.emp_id=eod.emp_id and is_alive = true and ud.usertype_id=?");
			pst.setInt(1, uF.parseToInt(hmUserTypeId.get(ADMIN)));
			rs = pst.executeQuery();
			List<String> empList = new ArrayList<String>();
			while(rs.next()) {
				if(!empList.contains(rs.getString("emp_per_id").trim())) {
					empList.add(rs.getString("emp_per_id").trim());
				}
			}
			rs.close();
			pst.close();
			
			String strDomain = request.getServerName().split("\\.")[0];
			for(int i=0; empList!= null && !empList.isEmpty() && i<empList.size(); i++) {
				if(!empList.get(i).equals("") && uF.parseToInt(empList.get(i)) > 0) {

					String alertData = "<div style=\"float: left;\"> A new Library Book Issue Request from <b>"+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>. </div>";
					String alertAction = "Library.action?pType=WR&dataType=IR";
					
					UserAlerts userAlerts = new UserAlerts(con, uF, CF, request);
					userAlerts.setStrDomain(strDomain);
					userAlerts.setStrEmpId(empList.get(i));
					userAlerts.setStrData(alertData);
					userAlerts.setStrAction(alertAction);
					userAlerts.setCurrUserTypeID(hmUserTypeId.get(ADMIN));
					userAlerts.setStatus(INSERT_WR_ALERT);
					Thread t = new Thread(userAlerts);
					t.run();
					
//					UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
//					userAlerts.setStrDomain(strDomain);
//					userAlerts.setStrEmpId(empList.get(i));
//					userAlerts.set_type(LIBRARY_REQUEST_ALERT);
//					userAlerts.setStatus(INSERT_ALERT);
//					Thread t = new Thread(userAlerts);
//					t.run();
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	public HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public String getBookId() {
		return bookId;
	}

	public void setBookId(String bookId) {
		this.bookId = bookId;
	}

	public String getBookIssuedId() {
		return bookIssuedId;
	}

	public void setBookIssuedId(String bookIssuedId) {
		this.bookIssuedId = bookIssuedId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getStrQuantityIssued() {
		return strQuantityIssued;
	}

	public void setStrQuantityIssued(String strQuantityIssued) {
		this.strQuantityIssued = strQuantityIssued;
	}

	public String getStrIssuedDate() {
		return strIssuedDate;
	}

	public void setStrIssuedDate(String strIssuedDate) {
		this.strIssuedDate = strIssuedDate;
	}

	public String getStrReturnDate() {
		return strReturnDate;
	}

	public void setStrReturnDate(String strReturnDate) {
		this.strReturnDate = strReturnDate;
	}

	public String getStrComment() {
		return strComment;
	}

	public void setStrComment(String strComment) {
		this.strComment = strComment;
	}

	public String getStrStartDate() {
		return strStartDate;
	}

	public void setStrStartDate(String strStartDate) {
		this.strStartDate = strStartDate;
	}

	public String getStrEndDate() {
		return strEndDate;
	}

	public void setStrEndDate(String strEndDate) {
		this.strEndDate = strEndDate;
	}

	
	public String getStrEmpId() {
		return strEmpId;
	}

	public void setStrEmpId(String strEmpId) {
		this.strEmpId = strEmpId;
	}

	public List<FillEmployee> getEmpList() {
		return empList;
	}

	public void setEmpList(List<FillEmployee> empList) {
		this.empList = empList;
	}

	public String getAvailQuantity() {
		return availQuantity;
	}

	public void setAvailQuantity(String availQuantity) {
		this.availQuantity = availQuantity;
	}

	public String getStrQuantityReq() {
		return strQuantityReq;
	}

	public void setStrQuantityReq(String strQuantityReq) {
		this.strQuantityReq = strQuantityReq;
	}

	public String getBookName() {
		return bookName;
	}

	public void setBookName(String bookName) {
		this.bookName = bookName;
	}

	public String getStrSubmit() {
		return strSubmit;
	}

	public void setStrSubmit(String strSubmit) {
		this.strSubmit = strSubmit;
	}
	
}
