package com.konnect.jpms.performance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

/**
 * @author user
 *
 */
public class UpdateTarget extends ActionSupport implements ServletRequestAware,IStatements{

	HttpSession session;
	
	private String tgoalid;
	private String emptarget;
	
	String strSessionEmpId;
	String strSessionUserType;
	String strUserTypeId;
	CommonFunctions CF;
	
	private String type;
	private String status;
	private String empid;
	private String emptmptarget;
	private String typeas;
	private String measuretype;
	private String amount;
	private String dayHrs;
	
	private String goalFreqId;
	
	private static final long serialVersionUID = 1L;

	public String execute() {
		
		session = request.getSession();
		strSessionEmpId = (String) session.getAttribute(EMPID);
		strSessionUserType = (String) session.getAttribute(USERTYPE);
		strUserTypeId = (String) session.getAttribute(USERTYPEID);

		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return "login";
		UtilityFunctions uF=new UtilityFunctions();
//		System.out.println("goalFreqId==>"+getGoalFreqId());
		if(uF.parseToDouble(getEmptarget()) > 0.0) {
			updateTarget(uF);
		}
		
		
//		if(getType()!=null && getType().equals("type")){
//			if(typeas != null && typeas.equals("KRATarget")){
//				return SUCCESS;
//			} else if(typeas != null && typeas.equals("GoalTarget")){
//				return LOAD;
//			}
//		}else{
//			if(typeas != null && typeas.equals("KRATarget")){
//				return SUCCESS;
//			} else if(typeas != null && typeas.equals("GoalTarget")){
//				return LOAD;
//			}
//		}
		return SUCCESS;
		
	}
	private void updateTarget(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
//		System.out.println("getEmptarget() ===> "+getEmptarget());
//		System.out.println("getEmptmptarget() ===> "+getEmptmptarget());
		try {
			con = db.makeConnection(con);
			boolean flag = false;
			String updateMsg = null;
			StringBuilder sbAllData = new StringBuilder();
			pst=con.prepareStatement("select max(amt_percentage) as amt_percentage from target_details where goal_id =? and emp_id =? and " +
				" amt_percentage >= ? and goal_freq_id=?");
			pst.setInt(1, uF.parseToInt(getTgoalid()));
			pst.setInt(2, uF.parseToInt(getEmpid()));
			pst.setDouble(3,uF.parseToDouble(getEmptarget()));
			pst.setInt(4, uF.parseToInt(getGoalFreqId()));
			rs=pst.executeQuery();
//			System.out.println("pst ===>>>> " + pst); 
			while(rs.next()) {
				if(rs.getDouble("amt_percentage") > 0) {
					flag = true;
					setEmptarget(rs.getString("amt_percentage"));
				}
			}
			rs.close();
			pst.close();
			
			if(flag == false) {
				pst = con.prepareStatement("insert into target_details(goal_id,emp_id,added_by,amt_percentage,entry_date,entry_time," +
						"approve_status,goal_freq_id)values(?,?,?,?, ?,?,?,?)");
				pst.setInt(1, uF.parseToInt(getTgoalid()));
				pst.setInt(2, uF.parseToInt(getEmpid()));
				pst.setInt(3, uF.parseToInt(strSessionEmpId));
				pst.setDouble(4, uF.parseToDouble(getEmptarget()));
				pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));					
				pst.setTime(6, uF.getCurrentTime(CF.getStrTimeZone()));
				pst.setInt(7, uF.parseToInt(getStatus()));
				pst.setInt(8, uF.parseToInt(getGoalFreqId()));
				pst.execute();
//				System.out.println("pst ===>> " + pst);
				pst.close();
				
				updateMsg = "Target Updated";
				request.setAttribute("updateTarget", "Target Updated");
			} else {
				updateMsg = "Target Not Updated";
				request.setAttribute("updateTarget", "Target Not Updated");
			}
			
//			System.out.println("getEmptarget ===> "+getEmptarget());
//			System.out.println("getMeasuretype ===> "+getMeasuretype());
//			System.out.println("getAmount ===> "+getAmount());
//			System.out.println("getDayHrs ===> "+getDayHrs());
			
			String twoDeciTotProgressAvg = "0";
			String twoDeciTot = "0";
			String total="100";
			double totalTarget=0;
			if(getMeasuretype()!=null && !getMeasuretype().equals("Effort")) {
				if(uF.parseToDouble(getAmount()) == 0) {
					totalTarget = 100;
				} else {
					totalTarget = (uF.parseToDouble(getEmptarget())/uF.parseToDouble(getAmount()))*100;
				}
				twoDeciTot = ""+Math.round(totalTarget);
			}else{
				
				String t = ""+uF.parseToDouble(getEmptarget());
				String days = "0";
				String hours = "0";
				if(t.contains(".")) {
					t = t.replace(".","_");
					String[] temp = t.split("_");
					days = temp[0];
					hours = temp[1];
				}	
				String t1 = ""+uF.parseToDouble(getDayHrs());
				String targetDays = "0";
				String targetHrs = "0";
				if(t1.contains(".")){
					t1 = t1.replace(".","_");
					String[] temp=t1.split("_");
					targetDays = temp[0];
					targetHrs = temp[1];
				}
				int daysInHrs = uF.parseToInt(days) * 8;
				int inttotHrs = daysInHrs + uF.parseToInt(hours);
				
				int targetDaysInHrs = uF.parseToInt(targetDays) * 8;
				int inttotTargetHrs = targetDaysInHrs + uF.parseToInt(targetHrs);
				if(inttotTargetHrs != 0) {
					totalTarget = uF.parseToDouble(""+inttotHrs) / uF.parseToDouble(""+inttotTargetHrs) * 100;
				} else {
					totalTarget = 100;
				}
				twoDeciTot = ""+Math.round(totalTarget);
			}
				if(totalTarget > new Double(100) && totalTarget<=new Double(150)){
					double totalTarget1=(totalTarget/150)*100;
					twoDeciTotProgressAvg=uF.formatIntoTwoDecimal(totalTarget1);
					total="150";
				}else if(totalTarget > new Double(150) && totalTarget<=new Double(200)){
					double totalTarget1=(totalTarget/200)*100;
					twoDeciTotProgressAvg=uF.formatIntoTwoDecimal(totalTarget1);
					total="200";
				}else if(totalTarget > new Double(200) && totalTarget<=new Double(250)){
					double totalTarget1=(totalTarget/250)*100;
					twoDeciTotProgressAvg=uF.formatIntoTwoDecimal(totalTarget1);
					total="250";
				}else if(totalTarget > new Double(250) && totalTarget<=new Double(300)){
					double totalTarget1=(totalTarget/300)*100;
					twoDeciTotProgressAvg=uF.formatIntoTwoDecimal(totalTarget1);
					total="300";
				}else if(totalTarget > new Double(300) && totalTarget<=new Double(350)){
					double totalTarget1=(totalTarget/350)*100;
					twoDeciTotProgressAvg=uF.formatIntoTwoDecimal(totalTarget1);
					total="350";
				}else if(totalTarget > new Double(350) && totalTarget<=new Double(400)){
					double totalTarget1=(totalTarget/400)*100;
					twoDeciTotProgressAvg=uF.formatIntoTwoDecimal(totalTarget1);
					total="400";
				}else if(totalTarget > new Double(400) && totalTarget<=new Double(450)){
					double totalTarget1=(totalTarget/450)*100;
					twoDeciTotProgressAvg=uF.formatIntoTwoDecimal(totalTarget1);
					total="450";
				}else if(totalTarget > new Double(450) && totalTarget<=new Double(500)){
					double totalTarget1=(totalTarget/500)*100;
					twoDeciTotProgressAvg=uF.formatIntoTwoDecimal(totalTarget1);
					total="500";
				}else{
					twoDeciTotProgressAvg=uF.formatIntoTwoDecimal(totalTarget);
					if(uF.parseToDouble(twoDeciTotProgressAvg) > 100){
						twoDeciTotProgressAvg = "100";
						total=""+Math.round(totalTarget);
					}else{
						total="100";
					}
				}
				String steadyWidth = "25";
				String spanMargin100 = "-10"; 
				if(getTypeas() != null && getTypeas().equals("GoalKRA")) {
					steadyWidth = "70";
					spanMargin100 = "-6";
				}
				
				sbAllData.append(getEmptarget());
				sbAllData.append("::::");
				sbAllData.append(updateMsg);
				sbAllData.append("::::");
				
				sbAllData.append("<div class=\"anaAttrib1\"><span style=\"margin-left:" + (uF.parseToDouble(twoDeciTotProgressAvg) > 95 ? uF.parseToDouble(twoDeciTotProgressAvg)-6 : uF.parseToDouble(twoDeciTotProgressAvg)-2.5) +"%;\">" +twoDeciTot+ "%</span></div>" +
						"<div id=\"outbox\">");
						if(uF.parseToDouble(twoDeciTotProgressAvg) < 33.33){
							sbAllData.append("<div id=\"redbox\" style=\"width: " +twoDeciTotProgressAvg+ "%;\"></div>");	
						}else if(uF.parseToDouble(twoDeciTotProgressAvg) >= 33.33 && uF.parseToDouble(twoDeciTotProgressAvg) < 66.67){
							sbAllData.append("<div id=\"yellowbox\" style=\"width:" +twoDeciTotProgressAvg+ "%;\"></div>");
						}else if(uF.parseToDouble(twoDeciTotProgressAvg) >= 66.67){
							sbAllData.append("<div id=\"greenbox\" style=\"width: " +twoDeciTotProgressAvg+ "%;\"></div>");
						}
						sbAllData.append("</div>" +
						"<div class=\"anaAttrib1\" style=\"float: left; width: 100%;\"><span style=\"float: left; margin-left:-2.5%;\">0%</span>" +
						"<span style=\"float: right; margin-right:"+spanMargin100+"%;\">" +total+ "%</span></div>");
						if(getTypeas() == null || !getTypeas().equals("GoalKRA")) {
							sbAllData.append("<span style=\"color: #808080;\">Slow</span>" +
							"<span style=\"margin-left:"+steadyWidth+"px; color: #808080;\">Steady</span>" +
							"<span style=\"float: right; color: #808080;\">Momentum</span>");
						}
//				System.out.println("sbAllData ===> "+sbAllData);
						request.setAttribute("sbAllData", sbAllData.toString());			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeResultSet(rs);
			db.closeConnection(con);
		}
	}
	public String getTgoalid() {
		return tgoalid;
	}

	public void setTgoalid(String tgoalid) {
		this.tgoalid = tgoalid;
	}

	public String getEmptarget() {
		return emptarget;
	}

	public void setEmptarget(String emptarget) {
		this.emptarget = emptarget;
	}

	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}

	public String getStatus() {
		return status;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getEmpid() {
		return empid;
	}
	
	public void setEmpid(String empid) {
		this.empid = empid;
	}

	public String getEmptmptarget() {
		return emptmptarget;
	}
	
	public void setEmptmptarget(String emptmptarget) {
		this.emptmptarget = emptmptarget;
	}

	public String getTypeas() {
		return typeas;
	}
	
	public void setTypeas(String typeas) {
		this.typeas = typeas;
	}

	public String getMeasuretype() {
		return measuretype;
	}
	
	public void setMeasuretype(String measuretype) {
		this.measuretype = measuretype;
	}
	
	public String getAmount() {
		return amount;
	}
	
	public void setAmount(String amount) {
		this.amount = amount;
	}
	
	public String getDayHrs() {
		return dayHrs;
	}
	
	public void setDayHrs(String dayHrs) {
		this.dayHrs = dayHrs;
	}

	public String getGoalFreqId() {
		return goalFreqId;
	}

	public void setGoalFreqId(String goalFreqId) {
		this.goalFreqId = goalFreqId;
	}


	HttpServletRequest request;
	
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
		
	}	
}
