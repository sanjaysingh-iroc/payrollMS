package com.konnect.jpms.library;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UserAlerts;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class ApproveOrDenyBookIssueRequest extends ActionSupport implements ServletRequestAware, IStatements{
	private static final long serialVersionUID = 1L;
	public HttpSession session;
	String strSessionOrgId;
	String strSessionEmpId;
	String strUserType;
	
	public CommonFunctions CF;
	
	private String bookId;
	private String bookIssuedId;
	private String bookName;
	private String bookAuthor;
	private String empName;
	private String availQuantity;
	private String reqQuantity;
	private String fromDate;
	private String toDate;
	private String reqDate;
	
	private String strQuantityIssued;
	private String strComment;
	private String operation;
	
	private String strSubmit;
	private String strCancel;
	
	private static Logger log = Logger.getLogger(ApproveOrDenyBookPurchase.class);
	public String execute() throws Exception{
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)	return LOGIN;

		strUserType = (String)session.getAttribute(USERTYPE);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		
		UtilityFunctions uF = new UtilityFunctions();
		
		request.setAttribute(PAGE, PApproveOrDenyBookIssueRequest);
		request.setAttribute(TITLE, TApproveOrDenyBookIssueRequest);
		
		getIssueRequestDetails(uF);
		
		if(getOperation()!=null && getOperation().equals("A") && getStrSubmit() != null) {
			approveOrDenyBookIssueRequest(uF,"A");
			return LOAD;
		}
		
		if(getOperation()!=null && getOperation().equals("D") && getStrCancel() != null) {
			approveOrDenyBookIssueRequest(uF,"D");
			return LOAD;
		}
		
		return SUCCESS;
	}
	
	
	private void getIssueRequestDetails(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			Map<String, String> hmEmpNames = CF.getEmpNameMap(con,null, null);
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from book_details bd, book_issues_returns bir where bd.book_id = bir.book_id and book_issued_id=? ");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(getBookIssuedId()));
//			System.out.println("pst1==> " + pst);
			rs = pst.executeQuery();
			List<String> requestList = new ArrayList<String>();
			while (rs.next()) {
				requestList.add(uF.showData(rs.getString("book_title"), "-"));//0
				requestList.add(uF.showData(hmEmpNames.get(rs.getString("requested_by")), "-"));//1
				requestList.add(uF.showData(rs.getString("book_author"), "-"));//2
				requestList.add(uF.showData(rs.getString("request_quantity"), "0"));//3
				requestList.add(uF.showData(rs.getString("request_comment"), "-"));//4
				requestList.add(uF.getDateFormat(rs.getString("request_date"), DBDATE, CF.getStrReportDateFormat()));//5
				requestList.add(uF.getDateFormat(rs.getString("from_date"), DBDATE, CF.getStrReportDateFormat()));//6
				requestList.add(uF.getDateFormat(rs.getString("to_date"), DBDATE, CF.getStrReportDateFormat()));//7
				setReqQuantity(rs.getString("request_quantity"));
				setAvailQuantity(rs.getString("available_book_quantity"));
			}
			rs.close();
			pst.close();
			
			request.setAttribute("requestList", requestList);
			
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	private void approveOrDenyBookIssueRequest(UtilityFunctions uF,String action) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			
			
			con = db.makeConnection(con);
			Map<String, String> hmUserTypeId = CF.getUserTypeIdMap(con);
			
			StringBuilder sbQuery = new StringBuilder();
			if(action!=null && action.equals("A")) {
				sbQuery.append("update book_issues_returns set approved_by=?, issued_date=?, book_issued_quantity=?, issue_status=?, issue_comment=? "
						+" where book_issued_id=?");
				pst = con.prepareStatement(sbQuery.toString());
				pst.setInt(1, uF.parseToInt(strSessionEmpId));
				pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setInt(3, uF.parseToInt(getStrQuantityIssued()));
				pst.setInt(4, 1);
				pst.setString(5,getStrComment());
				pst.setInt(6, uF.parseToInt(getBookIssuedId()));
			} else if(action!=null && action.equals("D")) {
				sbQuery.append("update book_issues_returns set approved_by=?, issue_status=?, issue_comment=? where book_issued_id=?");
				pst = con.prepareStatement(sbQuery.toString());
				pst.setInt(1, uF.parseToInt(strSessionEmpId));
				pst.setInt(2, -1);
				pst.setString(3, getStrComment());
				pst.setInt(4, uF.parseToInt(getBookIssuedId()));
			}
			pst.executeUpdate();
//			System.out.println("pst ===>> " + pst);
			pst.close();
			
//			System.out.println("getStrQuantityApproved==>"+getStrQuantityIssued());
			if(uF.parseToInt(getStrQuantityIssued()) > 0) {
				int quant = uF.parseToInt(getAvailQuantity()) - uF.parseToInt(getStrQuantityIssued());
//				System.out.println("quant==>"+quant);
			  	pst = con.prepareStatement("update book_details set available_book_quantity=? where book_id=?");
				pst.setInt(1, quant);
				pst.setInt(2, uF.parseToInt(getBookId()));
//				System.out.println("pst2==>"+pst);
				pst.executeUpdate();
				pst.close();
			}
			
			String requestedBy = "";
			pst = con.prepareStatement("select * from book_issues_returns where book_issued_id = ?");
			pst.setInt(1, uF.parseToInt(getBookIssuedId()));
			rs= pst.executeQuery();
			while(rs.next()) {
				requestedBy = rs.getString("requested_by").trim();	
			}
			rs.close();
			pst.close();
			
			String strDomain = request.getServerName().split("\\.")[0];
			if(!requestedBy.equals("") && uF.parseToInt(requestedBy) > 0) {
				String strApproveDeny = "denied";
				if(action!=null && action.equals("A")) {
					strApproveDeny = "approved";
				}
				String alertData = "<div style=\"float: left;\"> A new Book Issue request is "+strApproveDeny+" by <b>"+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>. </div>";
				String alertAction = "Library.action?pType=WR&dataType=EMPIB";
				
				UserAlerts userAlerts = new UserAlerts(con, uF, CF, request);
				userAlerts.setStrDomain(strDomain);
				userAlerts.setStrEmpId(requestedBy);
				userAlerts.setStrData(alertData);
				userAlerts.setStrAction(alertAction);
				userAlerts.setCurrUserTypeID(hmUserTypeId.get(EMPLOYEE));
				userAlerts.setStatus(INSERT_WR_ALERT);
				Thread t = new Thread(userAlerts);
				t.run();
				
//				UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
//				userAlerts.setStrDomain(strDomain);
//				userAlerts.setStrEmpId(requestedBy);
//				userAlerts.set_type(LIBRARY_REQUEST_APPROVED_ALERT);
//				userAlerts.setStatus(INSERT_ALERT);
//				Thread t = new Thread(userAlerts);
//				t.run();
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
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

	
	public String getStrSubmit() {
		return strSubmit;
	}

	public void setStrSubmit(String strSubmit) {
		this.strSubmit = strSubmit;
	}

	public String getStrCancel() {
		return strCancel;
	}

	public void setStrCancel(String strCancel) {
		this.strCancel = strCancel;
	}

	public String getStrComment() {
		return strComment;
	}

	public void setStrComment(String strComment) {
		this.strComment = strComment;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getBookIssuedId() {
		return bookIssuedId;
	}

	public void setBookIssuedId(String bookIssuedId) {
		this.bookIssuedId = bookIssuedId;
	}


	public String getBookName() {
		return bookName;
	}


	public void setBookName(String bookName) {
		this.bookName = bookName;
	}


	public String getBookAuthor() {
		return bookAuthor;
	}


	public void setBookAuthor(String bookAuthor) {
		this.bookAuthor = bookAuthor;
	}


	public String getEmpName() {
		return empName;
	}


	public void setEmpName(String empName) {
		this.empName = empName;
	}


	public String getAvailQuantity() {
		return availQuantity;
	}


	public void setAvailQuantity(String availQuantity) {
		this.availQuantity = availQuantity;
	}


	public String getReqQuantity() {
		return reqQuantity;
	}


	public void setReqQuantity(String reqQuantity) {
		this.reqQuantity = reqQuantity;
	}


	public String getFromDate() {
		return fromDate;
	}


	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}


	public String getToDate() {
		return toDate;
	}


	public void setToDate(String toDate) {
		this.toDate = toDate;
	}


	public String getReqDate() {
		return reqDate;
	}


	public void setReqDate(String reqDate) {
		this.reqDate = reqDate;
	}


	public String getStrQuantityIssued() {
		return strQuantityIssued;
	}


	public void setStrQuantityIssued(String strQuantityIssued) {
		this.strQuantityIssued = strQuantityIssued;
	}

}
