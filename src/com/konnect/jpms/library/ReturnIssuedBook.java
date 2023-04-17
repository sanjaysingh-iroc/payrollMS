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
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class ReturnIssuedBook extends ActionSupport implements ServletRequestAware, IStatements {
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
	private String issuedQuantity;
	private String toDate;
	private String issuedDate;
	
	
	private String strQuantityReturned;
	private String returnDate;
	private String strComment;
	private String operation;
	
	private String strSubmit;
	private String strCancel;
	
	private static Logger log = Logger.getLogger(ReturnIssuedBook.class);
	
	public String execute() throws Exception{
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)	return LOGIN;

		strUserType = (String)session.getAttribute(USERTYPE);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		
		UtilityFunctions uF = new UtilityFunctions();
		
		request.setAttribute(PAGE, PApproveOrDenyBookIssueRequest);
		request.setAttribute(TITLE, TApproveOrDenyBookIssueRequest);
		
		getIssuedBookDetails(uF);
		
		if(getOperation()!=null && getOperation().equals("R") && getStrSubmit() != null) {
			processReturnIssuedBook(uF);
			return LOAD;
		}
		
		return SUCCESS;
	}
	
	
	private void getIssuedBookDetails(UtilityFunctions uF) {
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
				requestList.add(uF.showData(rs.getString("book_title"), "-"));
				requestList.add(uF.showData(hmEmpNames.get(rs.getString("requested_by")), "-"));
				requestList.add(uF.showData(rs.getString("book_author"), "-"));
				requestList.add(uF.showData(rs.getString("book_issued_quantity"), "0"));
				requestList.add(uF.showData(rs.getString("issue_comment"), "-"));
				requestList.add(uF.getDateFormat(rs.getString("issued_date"), DBDATE, CF.getStrReportDateFormat()));
				requestList.add(uF.getDateFormat(rs.getString("from_date"), DBDATE, CF.getStrReportDateFormat()));
				requestList.add(uF.getDateFormat(rs.getString("to_date"), DBDATE, CF.getStrReportDateFormat()));
				
				setIssuedDate(uF.getDateFormat(rs.getString("issued_date"), DBDATE, DATE_FORMAT));
				setIssuedQuantity(rs.getString("book_issued_quantity"));
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

	private void processReturnIssuedBook(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("update book_issues_returns set received_by=?,return_date=?,returned_quantity=?,return_status=?, return_comment=? "
				+" where book_issued_id=?");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(strSessionEmpId));
			pst.setDate(2, uF.getDateFormat(getReturnDate(), DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(getStrQuantityReturned()));
			pst.setInt(4, 1);
			pst.setString(5,getStrComment());
			pst.setInt(6, uF.parseToInt(getBookIssuedId()));
			pst.executeUpdate();
			pst.close();
		
			if(uF.parseToInt(getStrQuantityReturned()) > 0) {
				pst = con.prepareStatement("select * from book_details where book_id = ?");
				pst.setInt(1, uF.parseToInt(getBookId()));
				rs = pst.executeQuery();
				int bookQuant = 0;
				while(rs.next()) {
					bookQuant = uF.parseToInt(rs.getString("available_book_quantity"));
				}
				rs.close();
				pst.close();
				
				int updateBookQuant = bookQuant + uF.parseToInt(getStrQuantityReturned());
				
			  	pst = con.prepareStatement("update book_details set available_book_quantity=? where book_id=?");
				pst.setInt(1, updateBookQuant);
				pst.setInt(2, uF.parseToInt(getBookId()));
				pst.executeUpdate();
				pst.close();
				
				pst = con.prepareStatement("select * from book_issues_returns where book_issued_id = ?");
				pst.setInt(1, uF.parseToInt(getBookIssuedId()));
				rs = pst.executeQuery();
				int bookIssuedQuant = 0;
				while(rs.next()) {
					bookIssuedQuant = uF.parseToInt(rs.getString("book_issued_quantity"));
				}
				rs.close();
				pst.close();
				
				if(bookIssuedQuant > uF.parseToInt(getStrQuantityReturned())) {
					pst = con.prepareStatement("select * from book_issues_returns where book_issued_id = ?");
					pst.setInt(1, uF.parseToInt(getBookIssuedId()));
					rs = pst.executeQuery();
					List<String> alData = new ArrayList<String>();
					while(rs.next()) {
						alData.add(rs.getString("book_id"));
						alData.add(rs.getString("request_quantity"));
						alData.add(rs.getString("approved_by"));
						alData.add(rs.getString("issue_status"));
						alData.add(rs.getString("book_issued_quantity"));
						alData.add(rs.getString("from_date"));
						alData.add(rs.getString("to_date"));
						alData.add(rs.getString("requested_by"));
						alData.add(rs.getString("issued_date"));
						alData.add(rs.getString("request_comment"));
						alData.add(rs.getString("request_date"));
						alData.add(rs.getString("issue_comment"));
					}
					rs.close();
					pst.close();
					
					int remainIssuedQnty = bookIssuedQuant - uF.parseToInt(getStrQuantityReturned());
					sbQuery = new StringBuilder();
					sbQuery.append("insert into book_issues_returns (book_id,request_quantity,approved_by,issue_status,book_issued_quantity," +
						"from_date,to_date,requested_by,issued_date,request_comment,request_date,issue_comment) values (?,?,?,?, ?,?,?,?, " +
						"?,?,?,?)");
					pst = con.prepareStatement(sbQuery.toString());
					pst.setInt(1, uF.parseToInt(alData.get(0)));
					pst.setInt(2, uF.parseToInt(alData.get(1)));
					pst.setInt(3, uF.parseToInt(alData.get(2)));
					pst.setInt(4, uF.parseToInt(alData.get(3)));
					pst.setInt(5, remainIssuedQnty);
					pst.setDate(6, uF.getDateFormat(alData.get(5), DBDATE));
					pst.setDate(7, uF.getDateFormat(alData.get(6), DBDATE));
					pst.setInt(8, uF.parseToInt(alData.get(7)));
					pst.setDate(9, uF.getDateFormat(alData.get(8), DBDATE));
					pst.setString(10, alData.get(9));
					pst.setDate(11, uF.getDateFormat(alData.get(10), DBDATE));
					pst.setString(12, alData.get(11));
					pst.executeUpdate();
					pst.close();
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
	public String getIssuedQuantity() {
		return issuedQuantity;
	}
	public void setIssuedQuantity(String issuedQuantity) {
		this.issuedQuantity = issuedQuantity;
	}
	public String getToDate() {
		return toDate;
	}
	public void setToDate(String toDate) {
		this.toDate = toDate;
	}
	public String getIssuedDate() {
		return issuedDate;
	}
	public void setIssuedDate(String issuedDate) {
		this.issuedDate = issuedDate;
	}
	public String getStrQuantityReturned() {
		return strQuantityReturned;
	}
	public void setStrQuantityReturned(String strQuantityReturned) {
		this.strQuantityReturned = strQuantityReturned;
	}
	public String getStrComment() {
		return strComment;
	}
	public void setStrComment(String strComment) {
		this.strComment = strComment;
	}
	public String getOperation() {
		return operation;
	}
	public void setOperation(String operation) {
		this.operation = operation;
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

	public String getReturnDate() {
		return returnDate;
	}

	public void setReturnDate(String returnDate) {
		this.returnDate = returnDate;
	}

	public String getBookId() {
		return bookId;
	}

	public void setBookId(String bookId) {
		this.bookId = bookId;
	}

	
}
