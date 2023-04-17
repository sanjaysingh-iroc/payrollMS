package com.konnect.jpms.training;

import java.io.File;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillApproval;
import com.konnect.jpms.select.FillBank;
import com.konnect.jpms.select.FillBloodGroup;
import com.konnect.jpms.select.FillCity;
import com.konnect.jpms.select.FillCountry;
import com.konnect.jpms.select.FillDegreeDuration;
import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillDesig;
import com.konnect.jpms.select.FillEmployee;
import com.konnect.jpms.select.FillEmploymentType;
import com.konnect.jpms.select.FillGender;
import com.konnect.jpms.select.FillGrade;
import com.konnect.jpms.select.FillLeaveType;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillMaritalStatus;
import com.konnect.jpms.select.FillNoticeDuration;
import com.konnect.jpms.select.FillPayCycleDuration;
import com.konnect.jpms.select.FillPayMode;
import com.konnect.jpms.select.FillProbationDuration;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.select.FillSkills;
import com.konnect.jpms.select.FillState;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.select.FillYears;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IConstants;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.Notifications;
import com.konnect.jpms.util.UploadImage;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class TrainingStatus extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	String strSessionEmpId = null;
	private String operation;
	Boolean autoGenerate = false;
 
	CommonFunctions CF=null;
	
	private static Logger log = Logger.getLogger(TrainingStatus.class);
	private String fromPage;
	public String execute() {
		
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if (CF==null){
			return LOGIN;
		}
		UtilityFunctions uF = new UtilityFunctions();
		/*boolean isView  = CF.getAccess(session, request, uF);
		if(!isView){
			request.setAttribute(PAGE, PAccessDenied);
			request.setAttribute(TITLE, TAccessDenied);
			return ACCESS_DENIED;
		}*/
		
		strUserType = (String)session.getAttribute(USERTYPE);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		
		request.setAttribute(PAGE, "/jsp/training/TrainingStatus.jsp");
		request.setAttribute(TITLE, "Add Status");


		if(updateStatusSubmit!=null)
		 return insertStatus(uF);
	
		prepareStatus(getTrainingId(), getlPlanId());
		
		if(getStatusDate()==null){
			setStatusDate(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, DATE_FORMAT));
		}
		
		return "popup";

	}

	
	private String insertStatus(UtilityFunctions uF) {
		Database db = new Database();
		db.setRequest(request);
		PreparedStatement pst = null;
		Connection con = null;
		
		ResultSet rst=null;
		try {
			con=db.makeConnection(con);
		
//			String learnerID = null; 
			boolean flag = false;
			pst=con.prepareStatement("select * from training_status where training_id=? and learning_plan_id =? and emp_id =? and status >= ?");
			pst.setInt(1,uF.parseToInt(getTrainingId()));
			pst.setInt(2,uF.parseToInt(getlPlanId()));
			pst.setInt(3,uF.parseToInt(strSessionEmpId));
			pst.setDouble(4, uF.parseToDouble(getPercStatus()));
			rst=pst.executeQuery();
			if(rst.next()){
				flag = true;
			}
			rst.close();
			pst.close();
			
			
			if(flag == false) {
				pst=con.prepareStatement("insert into training_status (emp_id,training_id,status,is_completed,_date,_notes,learning_plan_id)" +
						"values(?,?,?,?, ?,?,?)");
	
				pst.setInt(1,uF.parseToInt(strSessionEmpId));
				pst.setInt(2,uF.parseToInt(getTrainingId()));
				pst.setDouble(3, uF.parseToDouble(getPercStatus()));
				if(getIscompleted().equalsIgnoreCase("true"))
					pst.setInt(4, uF.parseToInt("1"));
				else
					pst.setInt(4, uF.parseToInt("0"));
				pst.setDate(5,uF.getDateFormat(statusDate, DATE_FORMAT) );
				pst.setString(6, getTrainingNotes());
				pst.setInt(7, uF.parseToInt(getlPlanId()));
				pst.execute();
				pst.close();
//				System.out.println("pst insert ===> " + pst);
			}
//			pst=con.prepareStatement("update training_learnings set status=?, is_completed=? where plan_id=?");
//			pst.setInt(1,uF.parseToInt(getPercStatus()));
//			pst.setInt(2, uF.parseToInt("0"));
//			pst.setInt(3,uF.parseToInt(getPlanId()));
//			pst.execute();

			prepareStatus(getTrainingId(), getlPlanId());
		
		}catch(Exception e){
		e.printStackTrace();	
		}finally{
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
		if(getFromPage() != null && getFromPage().equals("MyHR")) {
			return VIEW;
		} else{
			return SUCCESS;
		}
		
	}


	private void prepareStatus(String trainingId, String lPlanId) {
		
		Database db = new Database();
		db.setRequest(request);
		PreparedStatement pst = null;
		Connection con = null;
		UtilityFunctions uF = new UtilityFunctions();
		ResultSet rst=null;
		try {
			con=db.makeConnection(con);
			
//			String learnerID = null; 
			
			/*pst=con.prepareStatement("select learning_id from training_learnings where plan_id=?");
			pst.setInt(1,uF.parseToInt(getPlanId()));
			rst=pst.executeQuery();
			if(rst.next()){
				learnerID=rst.getString("learning_id");
			}*/
			
			List<List<String>> alStatusInfo=new ArrayList<List<String>>();
			int isCompleted = 0;
			pst=con.prepareStatement("select * from training_status where training_id=? and learning_plan_id=? and emp_id = ? order by _date desc");
			pst.setInt(1, uF.parseToInt(trainingId));
			pst.setInt(2, uF.parseToInt(lPlanId));
			pst.setInt(3, uF.parseToInt(strSessionEmpId));
			rst=pst.executeQuery();
//			System.out.println("pst select ===> " + pst);
			while(rst.next()){
				
				List<String> alInner=new ArrayList<String>();
				if(isCompleted == 0){
					isCompleted = rst.getInt("is_completed");
				}
				alInner.add(rst.getString("status"));  
				alInner.add(uF.getDateFormat( rst.getString("_date"), DBDATE, DATE_FORMAT) );
				alInner.add(rst.getString("_notes"));
				
				alStatusInfo.add(alInner);
				
			}
			rst.close();
			pst.close();
			request.setAttribute("alStatusInfo", alStatusInfo);
			request.setAttribute("isCompleted", ""+isCompleted);
			
		}catch(Exception e){
			e.printStackTrace();
			
		}finally{
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}


	HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {

		this.request=request;
		
	}
	
	String trainingId;
	String lPlanId;

	public String getTrainingId() {
		return trainingId;
	}

	public void setTrainingId(String trainingId) {
		this.trainingId = trainingId;
	}

	public String getlPlanId() {
		return lPlanId;
	}

	public void setlPlanId(String lPlanId) {
		this.lPlanId = lPlanId;
	}

	private String updateStatusSubmit;
	public String getUpdateStatusSubmit() {
		return updateStatusSubmit;
	}

	public void setUpdateStatusSubmit(String updateStatusSubmit) {
		this.updateStatusSubmit = updateStatusSubmit;
	}

	private String statusDate;
	private String percStatus;
	private String iscompleted;
	private String trainingNotes;

	public String getStatusDate() {
		return statusDate;
	}

	public void setStatusDate(String statusDate) {
		this.statusDate = statusDate;
	}

	public String getPercStatus() {
		return percStatus;
	}

	public void setPercStatus(String percStatus) {
		this.percStatus = percStatus;
	}

	public String getIscompleted() {
		return iscompleted;
	}

	public void setIscompleted(String iscompleted) {
		this.iscompleted = iscompleted;
	}

	public String getTrainingNotes() {
		return trainingNotes;
	}

	public void setTrainingNotes(String trainingNotes) {
		this.trainingNotes = trainingNotes;
	}
	
	private String mode;

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}


	public String getFromPage() {
		return fromPage;
	}


	public void setFromPage(String fromPage) {
		this.fromPage = fromPage;
	}

	
}
