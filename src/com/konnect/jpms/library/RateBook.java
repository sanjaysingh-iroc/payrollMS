package com.konnect.jpms.library;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
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

public class RateBook extends ActionSupport implements ServletRequestAware, IStatements {
	private static final long serialVersionUID = 1L;
	public HttpSession session;
	String strSessionOrgId;
	String strSessionEmpId;
	String strUserType;
	public CommonFunctions CF; 
	
	private String bookId;
	private String empId;
	private String bookRating;
	private String strComment;
	
	private String strSubmit;
	private String strUpdate;
	
	private String operation;
	
	private static Logger log = Logger.getLogger(RateBook.class);
	
	public String execute() throws Exception{
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)	return LOGIN;

		strUserType = (String)session.getAttribute(USERTYPE);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		
		UtilityFunctions uF = new UtilityFunctions();
		
		request.setAttribute(PAGE, PRateBook);
		request.setAttribute(TITLE, TRateBook);
		
//		System.out.println("getOperation==>"+getOperation()+"==>getStrUpdate()==>"+getStrUpdate());
		if(getOperation() != null && getOperation().equals("VIEW")) {
			getAllReviews(uF);
		} else {
			getEmpRating(uF);
			if(getStrSubmit() != null) {
				addBookRating(uF);
				return LOAD;
			}
			
			if(getStrUpdate() != null) {
				updateBookRating(uF);
				return LOAD;
			}
		}
		return SUCCESS;
	}
	
	private void getAllReviews(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from book_reviews where book_id=?");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1 ,uF.parseToInt(getBookId()));
			rs = pst.executeQuery();
			List<List<String>> alReviews = new ArrayList<List<String>>();
			while(rs.next()) {
				if(rs.getString("emp_rate")!=null && !rs.getString("emp_rate").equals("")) {
					List<String> innerList = new ArrayList<String>();
					innerList.add(rs.getString("book_review_id"));//0
					innerList.add(hmEmpName.get(rs.getString("emp_id")));//1
					innerList.add(rs.getString("emp_rate"));//2
					innerList.add(rs.getString("emp_comment"));//3
					alReviews.add(innerList);
				}
			}
			request.setAttribute("alReviews", alReviews);
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

	private void updateBookRating(UtilityFunctions uF){
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			if(getBookRating()!=null && !getBookRating().equals("") && getStrComment()!=null && !getStrComment().equals("")) {
				StringBuilder sbQuery = new StringBuilder();
				sbQuery.append("update book_reviews set emp_rate=?, emp_comment=?, last_updated_date=? where emp_id=? and book_id=?");
				pst = con.prepareStatement(sbQuery.toString());
				pst.setDouble(1, uF.parseToDouble(uF.formatIntoOneDecimalWithOutComma(uF.parseToDouble(getBookRating()))));
				pst.setString(2, getStrComment());
				pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setInt(4, uF.parseToInt(strSessionEmpId));
				pst.setInt(5, uF.parseToInt(getBookId()));
//				System.out.println("update rating==>"+pst);
				pst.executeUpdate();
				pst.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	private void getEmpRating(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from book_reviews where emp_id = ? and book_id =?");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(strSessionEmpId));
			pst.setInt(2 ,uF.parseToInt(getBookId()));
			rs = pst.executeQuery();
			double emp_rate = 0;
			String empCmment = null;
			while(rs.next()) {
				if(rs.getString("emp_rate")!=null && !rs.getString("emp_rate").equals("")) {
					emp_rate = rs.getDouble("emp_rate");
					empCmment = rs.getString("emp_comment");
				}
			}
			request.setAttribute("empRating", uF.showData(""+emp_rate, ""));
			request.setAttribute("empComment", uF.showData(empCmment, ""));
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
	
	private void addBookRating(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("insert into  book_reviews (emp_id,book_id,emp_rate,emp_comment,entry_date,last_updated_date)"+
		        "values(?,?,?,?,?,?)");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(strSessionEmpId));
			pst.setInt(2, uF.parseToInt(getBookId()));
			pst.setDouble(3, uF.parseToDouble(uF.formatIntoOneDecimalWithOutComma(uF.parseToDouble(getBookRating()))));
			pst.setString(4, getStrComment());
			pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setDate(6, uF.getCurrentDate(CF.getStrTimeZone()));
//			System.out.println("insert rating==>"+pst);
			pst.executeUpdate();
			pst.close();
									
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

	public String getEmpId() {
		return empId;
	}

	public void setEmpId(String empId) {
		this.empId = empId;
	}

	public String getBookRating() {
		return bookRating;
	}

	public void setBookRating(String bookRating) {
		this.bookRating = bookRating;
	}

	public String getStrComment() {
		return strComment;
	}

	public void setStrComment(String strComment) {
		this.strComment = strComment;
	}

	public String getStrSubmit() {
		return strSubmit;
	}

	public void setStrSubmit(String strSubmit) {
		this.strSubmit = strSubmit;
	}

	public String getStrUpdate() {
		return strUpdate;
	}

	public void setStrUpdate(String strUpdate) {
		this.strUpdate = strUpdate;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}
	
}
