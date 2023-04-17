package com.konnect.jpms.leave;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class AddLeaveComment extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
    HttpSession session;
    CommonFunctions CF;
    
    String strUserType;
    String strSessionEmpId;
    String leaveId;
    String empId; 
    String strComment;
    String currUserType;
    String leaveStatus;
    String strStartDate;
    String strEndDate;
    String commentSubmit;
    
    public String execute() throws Exception {
    	session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;

		if(getCommentSubmit()!=null && getCommentSubmit().equals("submit")){
			updateHRComment();
			return UPDATE;
		}
		viewHRComment();

		return LOAD;
    }
    
    public void viewHRComment(){
    	
    	Connection con = null;
        PreparedStatement pst = null;
        Database db = new Database();
        db.setRequest(request);
        UtilityFunctions uF = new UtilityFunctions();
        ResultSet rs = null;
        
    	try {
    		con = db.makeConnection(con);
    		pst = con.prepareStatement("select * from emp_leave_entry where leave_id=?");
            pst.setInt(1, uF.parseToInt(getLeaveId()));
            rs = pst.executeQuery();
//            System.out.println("pst="+pst);
            while (rs.next()) {
            	setStrComment(rs.getString("hr_comment"));
            }
            rs.close();
			pst.close();
    		
    	} catch (Exception e) {
            e.printStackTrace();
            request.setAttribute(MESSAGE, "Error in updation");
        } finally {
            db.closeResultSet(rs);
            db.closeStatements(pst);
            db.closeConnection(con);
        }
    }
    
    public void updateHRComment(){
    	Connection con = null;
        PreparedStatement pst = null;
        Database db = new Database();
        db.setRequest(request);
        UtilityFunctions uF = new UtilityFunctions();
        ResultSet rs = null;
        
    	try {
    		con = db.makeConnection(con);
    		pst = con.prepareStatement("UPDATE emp_leave_entry SET hr_comment=? where leave_id=?");
    		pst.setString(1, getStrComment());
            pst.setInt(2, uF.parseToInt(getLeaveId()));
            pst.execute();
			pst.close();
			
			request.setAttribute(MESSAGE, " updated successfully!");
    		
    	} catch (Exception e) {
            e.printStackTrace();
            request.setAttribute(MESSAGE, "Error in updation");
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

	public String getLeaveId() {
		return leaveId;
	}

	public void setLeaveId(String leaveId) {
		this.leaveId = leaveId;
	}

	public String getEmpId() {
		return empId;
	}

	public void setEmpId(String empId) {
		this.empId = empId;
	}

	public String getStrComment() {
		return strComment;
	}

	public void setStrComment(String strComment) {
		this.strComment = strComment;
	}

	public String getLeaveStatus() {
		return leaveStatus;
	}

	public void setLeaveStatus(String leaveStatus) {
		this.leaveStatus = leaveStatus;
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

	public String getCommentSubmit() {
		return commentSubmit;
	}

	public void setCommentSubmit(String commentSubmit) {
		this.commentSubmit = commentSubmit;
	}
	
}
