package com.konnect.jpms.policies;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillApproval;
import com.konnect.jpms.select.FillDesignation;
import com.konnect.jpms.select.FillInOut;
import com.konnect.jpms.select.FillTimeType;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class RosterPolicy extends ActionSupport implements ServletRequestAware, IStatements {
	
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String rosterpolicyId;
	String timeValue;
	String textMsg;
	String timeTypeId;
	String timeTypeName;
	String in_out_Id;
	String in_out_Name;
	String approvalId;
	String approvalName;
	String designationId;
	String designationName;
	String timeType;
	String in_out;
	String approval;
	String designation;
	String strEffectiveDate;
	
	List<FillTimeType> timeTypeList;
	List<FillInOut> in_out_List;
	List<FillApproval> approvalList;
	List<FillDesignation> designationList;
	private static Logger log = Logger.getLogger(RosterPolicy.class);
	CommonFunctions CF = null; 
	String orgId;
	String strWlocation;
	  
	String userscreen;
	String navigationId;
	String toPage;
	
	public String execute() throws Exception {
		
		request.setAttribute(PAGE, PPolicyRoster);
		session = request.getSession();

		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		String operation = request.getParameter("operation");
		String strId = request.getParameter("ID");
		
		loadValidatePolicyRoster();
		
		if (operation!=null && operation.equals("D")) {
			return deletePolicyRoster(strId);
		}
		if (operation!=null && operation.equals("E")) {
			return viewPolicyRoster(strId);
		}
		
		if (getRosterpolicyId()!=null && getRosterpolicyId().length()>0) {
				return updatePolicyRoster();
		}
		if (getTimeType()!=null && getTimeType().length()>0) {
				return insertPolicyRoster();
		}
		
		return LOAD;
	} 
	
	
	
	public String loadValidatePolicyRoster() {
		request.setAttribute(PAGE, PPolicyRoster);
		request.setAttribute(TITLE, TAddRosterPolicy);
		UtilityFunctions uF = new UtilityFunctions();
		
		timeTypeList = new FillTimeType().fillTimeType();
		in_out_List = new FillInOut().fillInOut();
		approvalList = new FillApproval().fillYesNo();
		designationList = new FillDesignation(request).fillDesignation();
		
		if(getStrEffectiveDate() == null){
			setStrEffectiveDate(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, DATE_FORMAT));
		}
		return LOAD;
	}
	
	public String updatePolicyRoster() {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		String updateRosterPolicy = "UPDATE roster_policy SET time_value=?,message=?,time_type=?, mode=?,isapproval=?, effective_date=?, entry_date=?, user_id=?  WHERE roster_policy_id=?";
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement(updateRosterPolicy);
			pst.setDouble(1, ((getTimeType()!=null && getTimeType().equalsIgnoreCase("LATE"))?uF.parseToDouble(getTimeValue()):-uF.parseToDouble(getTimeValue())));
			pst.setString(2, getTextMsg());
			pst.setString(3, getTimeType());
			pst.setString(4, getIn_out());
			pst.setBoolean(5, uF.parseToBoolean(getApproval()));
			pst.setDate(6, uF.getDateFormat(getStrEffectiveDate(), DATE_FORMAT));
			pst.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(8, uF.parseToInt((String)session.getAttribute(EMPID)));
			pst.setInt(9, uF.parseToInt(getRosterpolicyId()));
			pst.execute();
			pst.close();
			
			
			session.setAttribute(MESSAGE, SUCCESSM+"Policy updated successfully."+END);

		} catch (Exception e) {
			e.printStackTrace();			
		}finally{
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}
	
	public String viewPolicyRoster(String strId) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement(selectRosterPolicyV);
			pst.setInt(1, uF.parseToInt(strId));
			rs = pst.executeQuery();
			
			while(rs.next()){
				setRosterpolicyId(rs.getString("roster_policy_id"));
				setTimeValue(Math.abs(rs.getInt("time_value"))+"");
				setTimeType(rs.getString("time_type"));
				setIn_out(rs.getString("mode"));
				setOrgId(rs.getString("org_id"));

				if(rs.getBoolean("isapproval")){
					setApproval("YES");
				}else{
					setApproval("NO");
				}
				setTextMsg(rs.getString("message"));
				setStrEffectiveDate(uF.getDateFormat(rs.getString("effective_date"), DBDATE, DATE_FORMAT));
			}
			rs.close();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return LOAD;
	}
	public String insertPolicyRoster() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		try {

			con = db.makeConnection(con);
			//pst = con.prepareStatement(insertRosterPolicy);
			pst = con.prepareStatement("INSERT INTO roster_policy (time_value, message, time_type, mode, isapproval, user_id, effective_date, " +
					"entry_date, org_id,wlocation_id) VALUES (?,?,?,?,?,?,?,?,?,?)");
			pst.setDouble(1, ((getTimeType()!=null && getTimeType().equalsIgnoreCase("LATE"))?uF.parseToDouble(getTimeValue()):-uF.parseToDouble(getTimeValue())));
			pst.setString(2, getTextMsg());
			pst.setString(3, getTimeType());
			pst.setString(4, getIn_out());			
			pst.setBoolean(5, uF.parseToBoolean(getApproval()));
//			pst.setInt(6, uF.parseToInt(getDesignation()));
			pst.setInt(6, uF.parseToInt((String)session.getAttribute("EMPID")));
			pst.setDate(7, uF.getDateFormat(getStrEffectiveDate(), DATE_FORMAT));
			pst.setDate(8, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(9, uF.parseToInt(getOrgId()));
			pst.setInt(10, uF.parseToInt(getStrWlocation()));
			pst.execute();
			pst.close();
			
			session.setAttribute(MESSAGE, SUCCESSM+"Policy saved successfully."+END);

		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}

	public String deletePolicyRoster(String strId) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement(deleteRosterPolicy);
			pst.setInt(1, uF.parseToInt(strId));
			pst.execute();
			pst.close();

			session.setAttribute(MESSAGE, SUCCESSM+"Policy deleted successfully."+END);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}
	
	public void validate() {
        
    }
	
	public String getRosterpolicyId() {
		return rosterpolicyId;
	}
	public void setRosterpolicyId(String rosterpolicyId) {
		this.rosterpolicyId = rosterpolicyId;
	}
	public String getTimeValue() {
		return timeValue;
	}
	public void setTimeValue(String timeValue) {
		this.timeValue = timeValue;
	}
	public String getTextMsg() {
		return textMsg;
	}
	public void setTextMsg(String textMsg) {
		this.textMsg = textMsg;
	}
	public String getTimeTypeId() {
		return timeTypeId;
	}
	public void setTimeTypeId(String timeTypeId) {
		this.timeTypeId = timeTypeId;
	}
	public String getTimeTypeName() {
		return timeTypeName;
	}
	public void setTimeTypeName(String timeTypeName) {
		this.timeTypeName = timeTypeName;
	}
	public String getIn_out_Id() {
		return in_out_Id;
	}
	public void setIn_out_Id(String in_out_Id) {
		this.in_out_Id = in_out_Id;
	}
	public String getIn_out_Name() {
		return in_out_Name;
	}
	public void setIn_out_Name(String in_out_Name) {
		this.in_out_Name = in_out_Name;
	}
	public String getApprovalId() {
		return approvalId;
	}
	public void setApprovalId(String approvalId) {
		this.approvalId = approvalId;
	}
	public String getApprovalName() {
		return approvalName;
	}
	public void setApprovalName(String approvalName) {
		this.approvalName = approvalName;
	}
	public String getDesignationId() {
		return designationId;
	}
	public void setDesignationId(String designationId) {
		this.designationId = designationId;
	}
	public String getDesignationName() {
		return designationName;
	}
	public void setDesignationName(String designationName) {
		this.designationName = designationName;
	}
	public String getTimeType() {
		return timeType;
	}
	public void setTimeType(String timeType) {
		this.timeType = timeType;
	}
	public String getIn_out() {
		return in_out;
	}
	public void setIn_out(String in_out) {
		this.in_out = in_out;
	}
	public String getApproval() {
		return approval;
	}
	public void setApproval(String approval) {
		this.approval = approval;
	}
	public String getDesignation() {
		return designation;
	}
	public void setDesignation(String designation) {
		this.designation = designation;
	}
	public List<FillTimeType> getTimeTypeList() {
		return timeTypeList;
	}
	public List<FillInOut> getIn_out_List() {
		return in_out_List;
	}
	public List<FillApproval> getApprovalList() {
		return approvalList;
	}
	public List<FillDesignation> getDesignationList() {
		return designationList;
	}
	
	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public String getStrEffectiveDate() {
		return strEffectiveDate;
	}

	public void setStrEffectiveDate(String strEffectiveDate) {
		this.strEffectiveDate = strEffectiveDate;
	}

	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

	public String getStrWlocation() {
		return strWlocation;
	}

	public void setStrWlocation(String strWlocation) {
		this.strWlocation = strWlocation;
	}

	public String getUserscreen() {
		return userscreen;
	}

	public void setUserscreen(String userscreen) {
		this.userscreen = userscreen;
	}

	public String getNavigationId() {
		return navigationId;
	}

	public void setNavigationId(String navigationId) {
		this.navigationId = navigationId;
	}

	public String getToPage() {
		return toPage;
	}

	public void setToPage(String toPage) {
		this.toPage = toPage;
	}
	
}
