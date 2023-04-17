package com.konnect.jpms.training;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class GetTrainerCalenderAjax extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	String strSessionEmpId = null;

	CommonFunctions CF=null;

	
	private static Logger log = Logger.getLogger(GetTrainerCalenderAjax.class);
	
	public String execute() {
		
		
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if (CF==null){
			return LOGIN;
		}
		
		
		/*boolean isView  = CF.getAccess(session, request, uF);
		if(!isView){
			request.setAttribute(PAGE, PAccessDenied);
			request.setAttribute(TITLE, TAccessDenied);
			return ACCESS_DENIED;
		}*/
		
		prepareCal();

		return SUCCESS;
	}


	private void prepareCal() {
		
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;

		UtilityFunctions uF=new UtilityFunctions();
		
		try {
			con = db.makeConnection(con);

			pst=con.prepareStatement("select trainer_name from training_trainer where trainer_id=? ");
			pst.setInt(1,uF.parseToInt(getTrainerID()));
			
			rst=pst.executeQuery();
			if(rst.next()){
				setTrainerName(rst.getString(1));
			}
			rst.close();
			pst.close();
			
			HashMap<String,List<String>> hmSchedule=new HashMap<String, List<String>>();
			
			pst=con.prepareStatement("select plan_id,training_title,start_date,end_date from training_schedule " +
					"join training_plan using(plan_id)" +
					" where trainer_ids like '%,"+uF.parseToInt(getTrainerID())+",%' ");
			rst=pst.executeQuery();
			while(rst.next()){
				List<String> alSchedule=new ArrayList<String>();
				
				Calendar calstart=new GregorianCalendar();
				Calendar calend=new GregorianCalendar();
				
				calstart.setTime(rst.getDate("start_date"));
				calend.setTime(rst.getDate("end_date"));

				if(calstart.get(Calendar.YEAR)==calend.get(Calendar.YEAR) && calstart.get(Calendar.MONTH)==calend.get(Calendar.MONTH) && calstart.get(Calendar.DAY_OF_MONTH)==calend.get(Calendar.DAY_OF_MONTH)){
			
					int year=calstart.get(Calendar.YEAR);
					int month=calstart.get(Calendar.MONTH);
					int day=calstart.get(Calendar.DAY_OF_MONTH);	
					
					String exportDate=""+year+","+month+","+day; 
					
					alSchedule.add(exportDate);
					
				}
				
				while(calstart.before(calend)){
					
					int year=calstart.get(Calendar.YEAR);
					int month=calstart.get(Calendar.MONTH);
					int day=calstart.get(Calendar.DAY_OF_MONTH);
					
					String exportDate=""+year+","+month+","+day; 
					
					alSchedule.add(exportDate);
					
					calstart.add(Calendar.DATE,1);
			
					if(calstart.get(Calendar.YEAR)==calend.get(Calendar.YEAR) && calstart.get(Calendar.MONTH)==calend.get(Calendar.MONTH) && calstart.get(Calendar.DAY_OF_MONTH)==calend.get(Calendar.DAY_OF_MONTH)){
							 year=calstart.get(Calendar.YEAR);
						 month=calstart.get(Calendar.MONTH);
						 day=calstart.get(Calendar.DAY_OF_MONTH);	
						
						 exportDate=""+year+","+month+","+day; 
			
						alSchedule.add(exportDate);
						
					}
				
				}
				
				hmSchedule.put(rst.getString("training_title"), alSchedule);
				
			}
			rst.close();
			pst.close();
			
			request.setAttribute("hmSchedule", hmSchedule);
//			System.out.println("printing arraylist hmSchedule======"+hmSchedule);
			
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

	
	String trainerID;
	String trainerName;

	public String getTrainerID() {
		return trainerID;
	}
	public void setTrainerID(String trainerID) {
		this.trainerID = trainerID;
	}
	public String getTrainerName() {
		return trainerName;
	}
	public void setTrainerName(String trainerName) {
		this.trainerName = trainerName;
	}
	
}