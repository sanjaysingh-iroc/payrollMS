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

public class ApproveOrDenyBookPurchase extends ActionSupport implements ServletRequestAware, IStatements{
	private static final long serialVersionUID = 1L;
	public HttpSession session;
	String strSessionOrgId;
	String strSessionEmpId;
	String strUserType;
	
	public CommonFunctions CF;
	
	private String bookId;
	private String empId;
	private String bookPurchaseId;
	private String bookName;
	private String empName;
	private String quantity;
	private String reqDate;
	private String availQuantity;
	private String reqQuantity;
	
	private String strComment;
	private String strQuantityApproved;
	private String strAmount;
		
	private String strSubmit;
	private String strCancel;
	private String operation;
	
	private static Logger log = Logger.getLogger(ApproveOrDenyBookPurchase.class);
	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)	return LOGIN;

		strUserType = (String)session.getAttribute(USERTYPE);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		
		UtilityFunctions uF = new UtilityFunctions();
		
		request.setAttribute(PAGE, PApproveOrDenyBookPurchase);
		request.setAttribute(TITLE, TPurchaseBookRequest);
		
//		System.out.println("availQuantity==>"+getAvailQuantity()+"==>operation==>"+getOperation());
		
		getPurchaseRequestDetails(uF);
		
		if(getOperation()!=null && getOperation().equals("A") && getStrSubmit() != null) {
			approvePurchaseRequest(uF, "A");
			return LOAD;
		}
		
		if(getOperation()!=null && getOperation().equals("D") && getStrCancel() != null) {
			approvePurchaseRequest(uF, "D");
			return LOAD;
		}
		
		return SUCCESS;
	}
	
	private void getPurchaseRequestDetails(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			Map<String, String> hmEmpNames = CF.getEmpNameMap(con,null, null);
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from book_details bd, book_purchases bp where bd.book_id = bp.book_id and book_purchase_id=? ");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(getBookPurchaseId()));
//			System.out.println("pst1==> " + pst);
			rs = pst.executeQuery();
			List<String> requestList = new ArrayList<String>();
			while (rs.next()) {
				requestList.add(rs.getString("book_title"));
				requestList.add(uF.showData(hmEmpNames.get(rs.getString("purchased_by")), "-"));
				requestList.add(uF.showData(rs.getString("requested_quantity"), "0"));
				requestList.add(uF.getDateFormat(rs.getString("requested_date"), DBDATE, CF.getStrReportDateFormat()));
				setReqQuantity(rs.getString("requested_quantity"));
				setAvailQuantity(rs.getString("available_book_quantity"));
				setQuantity(rs.getString("book_quantity"));
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

	
	private void approvePurchaseRequest(UtilityFunctions uF,String action) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs =null;
		Database db = new Database();
		db.setRequest(request);
		try {
			
			
			con = db.makeConnection(con);
			Map<String, String> hmUserTypeId = CF.getUserTypeIdMap(con);
			
			StringBuilder sbQuery = new StringBuilder();
			if(action!=null && action.equals("A")) {
				sbQuery.append("update book_purchases set approved_by=?,purchased_date=?,approved_quantity=?,request_status=?,book_amount=?,emp_reason=? "
					+" where book_purchase_id=? ");
				pst = con.prepareStatement(sbQuery.toString());
				pst.setInt(1, uF.parseToInt(strSessionEmpId));
				pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setInt(3, uF.parseToInt(getStrQuantityApproved()));
				pst.setInt(4, 1);
				pst.setDouble(5,uF.parseToDouble(getStrAmount()));
				pst.setString(6, getStrComment());
				pst.setInt(7,uF.parseToInt(getBookPurchaseId()));
			} else if(action!=null && action.equals("D")) {   
				sbQuery.append("update book_purchases set approved_by=?,request_status=?, emp_reason=? where book_purchase_id = ?");
				pst = con.prepareStatement(sbQuery.toString());
				pst.setInt(1, uF.parseToInt(strSessionEmpId));
				pst.setInt(2, -1);
				pst.setString(3, getStrComment());
				pst.setInt(4,uF.parseToInt(getBookPurchaseId()));
			}
//			System.out.println("pst ===>> " + pst);
			pst.executeUpdate();
			pst.close();
			
//			System.out.println("getStrQuantityApproved==>"+getStrQuantityApproved()+"==>getStrQuantityApproved==>"+getStrQuantityApproved());
			if(uF.parseToInt(getStrQuantityApproved()) > 0) {
				int aQuant = uF.parseToInt(getAvailQuantity());
				if(getAvailQuantity()!= null && uF.parseToInt(getAvailQuantity()) >0) {
					aQuant = uF.parseToInt(getAvailQuantity())- uF.parseToInt(getStrQuantityApproved());
				}
				int bQuant = uF.parseToInt(getQuantity());
				if(getQuantity() != null && uF.parseToInt(getQuantity()) > 0) {
				 bQuant = uF.parseToInt(getQuantity()) - uF.parseToInt(getStrQuantityApproved()); ;
				}
//				System.out.println("aQuant==>"+aQuant+"==>bQuant==>"+bQuant);
			  	pst = con.prepareStatement("update book_details set available_book_quantity=? ,book_quantity=? where book_id=?");
				pst.setInt(1, aQuant);
				pst.setInt(2, bQuant);
				pst.setInt(3,uF.parseToInt(getBookId()));
//				System.out.println("pst2==>"+pst);
				pst.executeUpdate();
				pst.close();
			}
			
			String purchased_by = "";
			pst = con.prepareStatement("select * from book_purchases where book_purchase_id = ?");
			pst.setInt(1, uF.parseToInt(getBookPurchaseId()));
			rs= pst.executeQuery();
			while(rs.next()) {
				purchased_by = rs.getString("purchased_by").trim();
			}
			rs.close();
			pst.close();
			
			String strDomain = request.getServerName().split("\\.")[0];
			if(!purchased_by.equals("") && uF.parseToInt(purchased_by) > 0) {
				String strApproveDeny = "denied";
				if(action!=null && action.equals("A")) {
					strApproveDeny = "approved";
				}
				String alertData = "<div style=\"float: left;\"> A new Book Purchase request is "+strApproveDeny+" by <b>"+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>. </div>";
				String alertAction = "Library.action?pType=WR&dataType=EMPPB";
				
				UserAlerts userAlerts = new UserAlerts(con, uF, CF, request);
				userAlerts.setStrDomain(strDomain);
				userAlerts.setStrEmpId(purchased_by);
				userAlerts.setStrData(alertData);
				userAlerts.setStrAction(alertAction);
				userAlerts.setCurrUserTypeID(hmUserTypeId.get(EMPLOYEE));
				userAlerts.setStatus(INSERT_WR_ALERT);
				Thread t = new Thread(userAlerts);
				t.run();
				
//				UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
//				userAlerts.setStrDomain(strDomain);
//				userAlerts.setStrEmpId(purchased_by);
//				userAlerts.set_type(LIBRARY_REQUEST_APPROVED_ALERT);
//				userAlerts.setStatus(INSERT_ALERT);
//				Thread t = new Thread(userAlerts);
//				t.run();
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

	public String getBookPurchaseId() {
		return bookPurchaseId;
	}

	public void setBookPurchaseId(String bookPurchaseId) {
		this.bookPurchaseId = bookPurchaseId;
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

	public String getBookName() {
		return bookName;
	}

	public void setBookName(String bookName) {
		this.bookName = bookName;
	}

	public String getEmpName() {
		return empName;
	}

	public void setEmpName(String empName) {
		this.empName = empName;
	}

	public String getQuantity() {
		return quantity;
	}

	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}

	public String getReqDate() {
		return reqDate;
	}

	public void setReqDate(String reqDate) {
		this.reqDate = reqDate;
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

	public String getStrQuantityApproved() {
		return strQuantityApproved;
	}

	public void setStrQuantityApproved(String strQuantityApproved) {
		this.strQuantityApproved = strQuantityApproved;
	}

	public String getStrAmount() {
		return strAmount;
	}

	public void setStrAmount(String strAmount) {
		this.strAmount = strAmount;
	}

	public String getEmpId() {
		return empId;
	}

	public void setEmpId(String empId) {
		this.empId = empId;
	}

	public String getAvailQuantity() {
		return availQuantity;
	}

	public void setAvailQuantity(String availQuantity) {
		this.availQuantity = availQuantity;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getReqQuantity() {
		return reqQuantity;
	}

	public void setReqQuantity(String reqQuantity) {
		this.reqQuantity = reqQuantity;
	}
	
}
