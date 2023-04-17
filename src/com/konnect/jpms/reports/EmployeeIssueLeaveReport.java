package com.konnect.jpms.reports;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillApproval;
import com.konnect.jpms.select.FillLeaveType;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class EmployeeIssueLeaveReport extends ActionSupport implements ServletRequestAware, IStatements {

	/**
	 *  
	 */
	private static final long serialVersionUID = 1L;
//	List<FillUserType> userTypeList;
	List<FillLevel> levelTypeList;
	List<FillLeaveType> empLeaveTypeList;
	List<FillApproval> approvalList;
	private static Logger log = Logger.getLogger(EmployeeIssueLeaveReport.class);
	
	public String execute() throws Exception {
		
		log.debug("EmployeeIssueLeaveReport execute()");
		request.setAttribute(PAGE, PEmployeeIssueLeaveReport);
		request.setAttribute(TITLE, TViewEmployeeIssueLeave);
		viewEmployeeIssueLeave();			
			return loadEmployeeIssueLeave();
	}
	
	public String loadEmployeeIssueLeave() {	
		
//		userTypeList = new FillUserType().fillUserType();
		levelTypeList = new FillLevel(request).fillLevel();
		empLeaveTypeList = new FillLeaveType(request).fillLeave();
		approvalList = new FillApproval().fillLeaveStartDate();
		
//		request.setAttribute("userTypeList", userTypeList);
		request.setAttribute("levelTypeList", levelTypeList);
		request.setAttribute("empLeaveTypeList", empLeaveTypeList);
		request.setAttribute("approvalList", approvalList);
		
//		if(userTypeList.size()!=0) {
//		int userTypeId, i11;
//		String userTypeName;
//		StringBuilder sbUserTypeList = new StringBuilder();
//		sbUserTypeList.append("{");
//	    for(i11=0; i11<userTypeList.size()-1;i11++ ) {
//	    		userTypeId = Integer.parseInt((userTypeList.get(i11)).getUserTypeId());
//	    		userTypeName = userTypeList.get(i11).getUserTypeName();
//	    		sbUserTypeList.append("\""+ userTypeId+"\":\""+userTypeName+"\",");
//	    }
//	    userTypeId = Integer.parseInt((userTypeList.get(i11)).getUserTypeId());
//	    userTypeName = userTypeList.get(i11).getUserTypeName();
//		sbUserTypeList.append("\""+ userTypeId+"\":\""+userTypeName+"\"");	
//	    sbUserTypeList.append("}");
//	    request.setAttribute("sbUserTypeList", sbUserTypeList.toString());
//		}
		
		
		if(levelTypeList.size()!=0) {
			String empLeavelName, empLeavelId;
			StringBuilder sbLevelTypeList = new StringBuilder();
			sbLevelTypeList.append("{");
			int i;
			for (i = 0; i < levelTypeList.size() - 1; i++) {
				empLeavelId = (levelTypeList.get(i)).getLevelId();
				empLeavelName = levelTypeList.get(i).getLevelCodeName();
				sbLevelTypeList.append("\"" + empLeavelId + "\":\"" + empLeavelName+ "\",");
			}
			empLeavelId = (levelTypeList.get(i)).getLevelId();
			empLeavelName = levelTypeList.get(i).getLevelCodeName();
			sbLevelTypeList.append("\"" + empLeavelId + "\":\"" + empLeavelName+ "\",");
			sbLevelTypeList.append("}");
			request.setAttribute("sbLevelTypeList", sbLevelTypeList.toString());
		}
		
		
		if(empLeaveTypeList.size()!=0) {
			String empLeaveTypeName, empLeaveTypeId;
			StringBuilder sbEmpLeaveTypeList = new StringBuilder();
			sbEmpLeaveTypeList.append("{");
			int i;
			for (i = 0; i < empLeaveTypeList.size() - 1; i++) {
				empLeaveTypeId = (empLeaveTypeList.get(i)).getLeaveTypeId();
				empLeaveTypeName = empLeaveTypeList.get(i).getLeavetypeName();
				sbEmpLeaveTypeList.append("\"" + empLeaveTypeId + "\":\"" + empLeaveTypeName+ "\",");
			}
			empLeaveTypeId = (empLeaveTypeList.get(i)).getLeaveTypeId();
			empLeaveTypeName = empLeaveTypeList.get(i).getLeavetypeName();
			sbEmpLeaveTypeList.append("\"" + empLeaveTypeId + "\":\"" + empLeaveTypeName + "\"");
			sbEmpLeaveTypeList.append("}");
			request.setAttribute("sbEmpLeaveTypeList", sbEmpLeaveTypeList.toString());
		}
		
		if(approvalList.size()!=0) {
			
			String approvalName, approvalId;
			StringBuilder sbApprovalList = new StringBuilder();
			sbApprovalList.append("{");
			int i;
			for (i = 0; i < approvalList.size() - 1; i++) {
				approvalId = (approvalList.get(i)).getApprovalId();
				approvalName = approvalList.get(i).getApprovalName();
				sbApprovalList.append("\"" + approvalId + "\":\"" + approvalName + "\",");
			}
			
			approvalId = (approvalList.get(i)).getApprovalId();
			approvalName = approvalList.get(i).getApprovalName();
			sbApprovalList.append("\"" + approvalId + "\":\"" + approvalName + "\"");
			sbApprovalList.append("}");
			request.setAttribute("sbApprovalList", sbApprovalList.toString());
		}
		
		return "load";
	}
	
	public String viewEmployeeIssueLeave(){
		
		Connection con = null;
		PreparedStatement pst=null, pst1=null;
		ResultSet rs= null, rs1= null;
		Database db = new Database();
		db.setRequest(request);
		
		UtilityFunctions uF = new UtilityFunctions();
		com.konnect.jpms.util.CommonFunctions cF = new CommonFunctions();
		cF.setRequest(request);
		try{
			List<List<String>> al = new ArrayList<List<String>>();
			List<String> alInner = new ArrayList<String>();
			StringBuilder sb = new StringBuilder();
			
			con = db.makeConnection(con);
			pst = con.prepareStatement(selectEmpLeaveType);
			rs = pst.executeQuery();
			int cnt=-1;
			while(rs.next()) {
				
				alInner  = new ArrayList<String>();
				alInner.add(rs.getString("emp_leave_type_id"));
				
				/*if(al.size()>0) {
					
					for(int i=0; i<al.size(); i++) {
						
						if( ((ArrayList)al.get(i)).contains(rs.getString("leave_type_name")) ) {
							alInner.add("");
							break;
						}else {
							if(i==al.size() - 1) {
								alInner.add(rs.getString("leave_type_name"));
								break;
							}
						}
					}
					
				}else*/
					
				alInner.add(rs.getString("leave_type_name"));
					
				alInner.add(rs.getString("level_code"));
				alInner.add(rs.getString("no_of_leave"));
				alInner.add(cF.getLeaveStartDate(rs.getString("effective_date_type")));
				alInner.add(uF.showYesNo(rs.getString("is_paid")));
				alInner.add(uF.showYesNo(rs.getString("is_carryforward")));
				alInner.add(rs.getString("entrydate"));
				
				al.add(alInner);
				cnt++;
			}
			rs.close();
			pst.close();
			
			log.debug("al=====>>>"+al);
			request.setAttribute("reportList", al);
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		}finally{
			db.closeResultSet(rs1);
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
		/*try {
			List<List<String>> alTotal = new ArrayList<List<String>>();
			List<String> alInnerTotal = new ArrayList<String>();
			StringBuilder sb = new StringBuilder();
			
			con = db.makeConnection(con);
			pst = con.prepareStatement(selectEmployeeIssueLeaveV);
			rs = pst.executeQuery();
			while(rs.next()){
				alInnerTotal = new ArrayList<String>();
				alInnerTotal.add("<a href=\"#?w=350\" rel=\"popup_name" + rs.getString("user_type_id") + "\" class=\"poplight\" \">"+rs.getString("user_type")+"</a>");
				alInnerTotal.add(rs.getString("total_no_of_leave"));
				alTotal.add(alInnerTotal);
				
				pst1 = con.prepareStatement(selectEmployeeIssueLeaveVP);
				pst1.setInt(1, rs.getInt("user_type_id"));
				rs1 = pst1.executeQuery();
				String empOld = null;
				String empNew = null;
				
				while (rs1.next()) {
					empNew = rs.getString("user_type_id");
					if (empNew != null && !empNew.equalsIgnoreCase(empOld)) {
							sb.append("<div id=\"popup_name" + rs1.getString("user_type_id") + "\" class=\"popup_block\">" + "<h2 class=\"textcolorWhite\">" + rs1.getString("user_type")+" Leave details</h2>" + "<table>"+
							"<td class=\"reportHeading\">Leave Type</td>" + "<td class=\"reportHeading\">Total No of Leave</td>" + "<td class=\"reportHeading\">Entrydate</td>" + "<td class=\"reportHeading\">&nbsp;</td></tr>");
					}
							sb.append("<tr>" + "<td class=\"reportLabel\">" + rs1.getString("leave_type_name")+ "</td>" + "</td>" + "<td class=\"reportLabel alignRight\">" + rs1.getString("no_of_leave") +"</td>"+ "<td class=\"reportLabel alignRight\">" + rs1.getString("entrydate") +"</td>" +
									"<td class=\"reportLabel alignRight\"><a href=" + request.getContextPath() + "/EmployeeIssueLeave.action?E=" + rs1.getString("emp_leave_type_id") + ">Edit</a> | <a onclick=\"return confirm('"+ConfirmDelete+"');\" href="+request.getContextPath()+"/EmployeeIssueLeave.action?D="+rs1.getString("emp_leave_type_id")+">Delete</a></td></tr>");
							empOld = empNew;			
				}
				sb.append("</table>" + "</div>");
			}
			request.setAttribute("reportListTotal", alTotal);
			request.setAttribute("empLeaveTypes", sb.toString());

		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute(MESSAGE, "Error in insertion");
			return ERROR;
		}finally{
			db.closeConnection(con);
			db.closeStatements(pst);
			db.closeResultSet(rs);
		}*/
		return SUCCESS;
		
	}
	
	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public List<FillLeaveType> getEmpLeaveTypeList() {
		return empLeaveTypeList;
	}

	public void setEmpLeaveTypeList(List<FillLeaveType> empLeaveTypeList) {
		this.empLeaveTypeList = empLeaveTypeList;
	}

	public List<FillApproval> getApprovalList() {
		return approvalList;
	}

	public void setApprovalList(List<FillApproval> approvalList) {
		this.approvalList = approvalList;
	}

	public List<FillLevel> getLevelTypeList() {
		return levelTypeList;
	}

	public void setLevelTypeList(List<FillLevel> levelTypeList) {
		this.levelTypeList = levelTypeList;
	}

}
