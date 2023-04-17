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

import com.konnect.jpms.performance.SetQuestionToTextfield;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UserAlerts;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class PurchaseBook extends ActionSupport implements ServletRequestAware, IStatements{
	private static final long serialVersionUID = 1L;
	public HttpSession session;
	String strSessionOrgId;
	String strSessionEmpId;
	String strUserType;
	
	public CommonFunctions CF;
	
	private String bookId;
	private String strQuantity;
	private String strSubmit;
	private String bookName;
	private String availQuantity;
	private static Logger log = Logger.getLogger(PurchaseBook.class);
	
	public String execute() throws Exception{
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)	return LOGIN;

		strUserType = (String)session.getAttribute(USERTYPE);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		
		UtilityFunctions uF = new UtilityFunctions();
		
		request.setAttribute(PAGE, PPurchaseBook);
		request.setAttribute(TITLE, TPurchaseBook);
		
		getBookDetails(uF);
		
		if(getStrSubmit() != null) {
			insertBookPurchaseDetails(uF);
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
			sbQuery.append("select * from  book_details where book_id=? ");
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

	private void insertBookPurchaseDetails(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs =null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			Map<String, String> hmUserTypeId = CF.getUserTypeIdMap(con);
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("insert into book_purchases(book_id, purchased_by, request_status, requested_date,requested_quantity)"
				+" values(?,?,?,?,?)");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(getBookId()));
			pst.setInt(2, uF.parseToInt(strSessionEmpId));
			pst.setInt(3, 0);
			pst.setDate(4, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(5, uF.parseToInt(getStrQuantity()));
			pst.executeUpdate();
			pst.close();
			
			pst=con.prepareStatement("select emp_per_id from employee_official_details eod, user_details ud, employee_personal_details epd where epd.emp_per_id=eod.emp_id and ud.emp_id=eod.emp_id and is_alive = true and ud.usertype_id=?");
			pst.setInt(1, uF.parseToInt(hmUserTypeId.get(ADMIN)));
			rs=pst.executeQuery();
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
					String alertData = "<div style=\"float: left;\"> A new Library Book Purchase Request from <b>"+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>. </div>";
					String alertAction = "Library.action?pType=WR&dataType=PR";
					
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

	public String getStrQuantity() {
		return strQuantity;
	}

	public void setStrQuantity(String strQuantity) {
		this.strQuantity = strQuantity;
	}

	public String getStrSubmit() {
		return strSubmit;
	}

	public void setStrSubmit(String strSubmit) {
		this.strSubmit = strSubmit;
	}

	public String getBookName() {
		return bookName;
	}

	public void setBookName(String bookName) {
		this.bookName = bookName;
	}


	public String getAvailQuantity() {
		return availQuantity;
	}


	public void setAvailQuantity(String availQuantity) {
		this.availQuantity = availQuantity;
	}
}
