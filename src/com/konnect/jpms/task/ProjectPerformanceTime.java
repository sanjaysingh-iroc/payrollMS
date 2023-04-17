package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class ProjectPerformanceTime extends ActionSupport implements ServletRequestAware, IStatements{
	/**
	 * 
	 */
	private static final long serialVersionUID = -898510654267112890L;
	HttpServletRequest request;
	CommonFunctions CF;
	HttpSession session;
	public String execute(){
		
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;

		request.setAttribute(PAGE, "/jsp/task/ProjectPerformanceTime.jsp");
		request.setAttribute(TITLE, "Project Performance Time Report");
		 
		UtilityFunctions uF=new UtilityFunctions();
		String strUserType = (String)session.getAttribute(BASEUSERTYPE);
		
		if(strUserType!=null && strUserType.equalsIgnoreCase(MANAGER)){
			getProjectDetails(uF.parseToInt((String)session.getAttribute(EMPID)), uF, CF, 0);
		}else if(strUserType!=null && strUserType.equalsIgnoreCase(ADMIN)){
			getProjectDetails(0, uF, CF, 0);
		}
		
		return SUCCESS;
	}

	public void getProjectDetails(int nManagerId, UtilityFunctions uF, CommonFunctions CF, int nLimit){
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs=null;
		try {
			con = db.makeConnection(con);
						
		/*	pst = con.prepareStatement("select ped.*,epd.emp_fname from project_emp_details ped,employee_personal_details epd where _isteamlead =true and ped.emp_id=epd.emp_per_id");
			rs=pst.executeQuery();
			Map<String,String> proMap1=new HashMap<String,String>();
			while(rs.next()){
				proMap1.put(rs.getString("pro_id"), rs.getString("emp_fname"));
			}*/
			
			
			if(nManagerId==0){
				pst = con.prepareStatement("select pc.*,pmntc.deadline from project_time pc, projectmntnc pmntc where pc.pro_id = pmntc.pro_id and approve_status = 'n' order by pmntc.pro_id "+((nLimit>0)?" limit "+nLimit:""));
			}else{
				pst = con.prepareStatement("select pc.*,pmntc.deadline from project_time pc, projectmntnc pmntc where pc.pro_id = pmntc.pro_id and approve_status = 'n' and added_by=? order by pmntc.pro_id "+((nLimit>0)?" limit "+nLimit:""));
				pst.setInt(1, nManagerId);
			}
			
			
			rs=pst.executeQuery();
			List<List<String>> alOuter=new ArrayList<List<String>>();
			while(rs.next()){
				List<String> alInner=new ArrayList<String>();
				alInner.add(rs.getString("pro_id"));
				alInner.add(rs.getString("pro_name"));
				alInner.add(rs.getString("idealtime"));
				alInner.add(uF.roundOffInTimeInHoursMins(uF.parseToDouble(rs.getString("actual_hrs"))));
				alInner.add(rs.getString("completed"));
				
				
				Date dtDeadline = uF.getDateFormat(rs.getString("deadline"), DBDATE);
				Date dtCurrent = uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE);
				
				if(dtDeadline!=null && dtCurrent!=null && dtDeadline.before(dtCurrent)){
					alInner.add("<div style=\"background-color:red;padding-left:10px\">"+uF.getDateFormat(rs.getString("deadline"), DBDATE, CF.getStrReportDateFormat())+"</div>");
				}else if(dtDeadline!=null && dtCurrent!=null && dtDeadline.equals(dtCurrent)){
					alInner.add("<div style=\"background-color:orange;padding-left:10px\">"+uF.getDateFormat(rs.getString("deadline"), DBDATE, CF.getStrReportDateFormat())+"</div>");
				}else if(dtDeadline!=null && dtCurrent!=null && dtDeadline.after(dtCurrent)){
					alInner.add("<div style=\"background-color:lightgreen;padding-left:10px\">"+uF.getDateFormat(rs.getString("deadline"), DBDATE, CF.getStrReportDateFormat())+"</div>");
				}else{
					alInner.add("<div style=\"background-color:yellow;padding-left:10px\">"+uF.getDateFormat(rs.getString("deadline"), DBDATE, CF.getStrReportDateFormat())+"</div>");
				}
				
				
				
				alOuter.add(alInner);
			}
			rs.close();
			pst.close();
			
			request.setAttribute("alOuter",alOuter);

			
//			pst = con.prepareStatement("select * from(select * from(select (sum(completed) /count(*))::Integer as completed,pro_id  from  activity_info   group by pro_id) as a where completed=100) as b LEFT JOIN project_cost pc ON b.pro_id=pc.pro_id");
//			rs=pst.executeQuery();
//			String budgeted_cost="";
//			String billable_amount="";
//			String actual_amount="";
//			String pro_name="";
//
//			while(rs.next()){
//				pro_name+="'"+proMap.get(rs.getString("pro_id"))+"',";
//				billable_amount+=uF.parseToInt(rs.getString("billable_amount"));
//				budgeted_cost+=uF.parseToInt(rs.getString("budgeted_cost"));
//				actual_amount+=uF.parseToInt(rs.getString("actual_amount"));
//
//			}
//			request.setAttribute("pro_name",pro_name);
//			request.setAttribute("billable_amount",billable_amount);
//			request.setAttribute("budgeted_cost",budgeted_cost);
//			request.setAttribute("actual_amount",actual_amount);

			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	
		
	}
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request=request;
	}

}
