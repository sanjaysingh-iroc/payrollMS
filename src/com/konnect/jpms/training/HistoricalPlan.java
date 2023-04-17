package com.konnect.jpms.training;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class HistoricalPlan extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	String strSessionEmpId = null;

	private int empId;  

	CommonFunctions CF=null; 
	 
   
	
	
	private static Logger log = Logger.getLogger(HistoricalPlan.class);
	
	public String execute() {
		
		UtilityFunctions uF=new UtilityFunctions();
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if (CF==null){
			return LOGIN;
		}
		
/*		boolean isView  = CF.getAccess(session, request, uF);
		if(!isView){
			request.setAttribute(PAGE, PAccessDenied);
			request.setAttribute(TITLE, TAccessDenied);
			return ACCESS_DENIED;
		}*/
		
		wLocationList=new FillWLocation(request).fillWLocation();
/*		gradeList=new FillGrade().fillGrade();
		desigList=new FillDesig().fillDesig();*/
		levelList = new FillLevel(request).fillLevel();
		
		
		request.setAttribute(PAGE, "/jsp/training/HistoricalPlan.jsp");
		request.setAttribute(TITLE, "Historical Plan");


		getHisoricalPlanDetails();
		getRatingDetails();
		
		return "success";
	

	}

	private void getRatingDetails() {

		Database db = new Database();
		db.setRequest(request);
		PreparedStatement pst = null;
		Connection con = null;		
		ResultSet rst=null;		
		
		try{
			con=db.makeConnection(con);
			
			Map<String,String> hmPlanRating=new HashMap<String, String>();
			pst=con.prepareStatement("select ((marks/weightage)*100) as total,plan_id from (select sum(marks)as marks,sum(weightage)as weightage,plan_id " +
					" from training_question_answer where weightage>0 group by plan_id order by plan_id) a "); 
			rst=pst.executeQuery();
			while(rst.next()){
				hmPlanRating.put(rst.getString("plan_id"),rst.getString("total"));
			}
			rst.close();
			pst.close();
			
			request.setAttribute("hmPlanRating", hmPlanRating);
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
			
		}
		
	}

	private void getHisoricalPlanDetails() {

		Database db = new Database();
		db.setRequest(request);
		PreparedStatement pst = null;
		Connection con = null;		
		ResultSet rst=null;		
		UtilityFunctions uF=new UtilityFunctions();
		List<List<String>> historicalPlanList=new ArrayList<List<String>>();	
		
		try{
			con=db.makeConnection(con);
			Map<String,String> hmwlocation=CF.getWLocationMap(con, null, null);
				
			StringBuilder strquery=new StringBuilder();
			
			strquery.append("select * from training_plan tp,training_schedule ts where tp.plan_id=ts.plan_id and end_date < ? ");
			pst=con.prepareStatement(strquery.toString());
			pst.setDate(1,uF.getCurrentDate(CF.getStrTimeZone()));
			rst=pst.executeQuery();
			
			while(rst.next()){				
				List<String> alinner=new ArrayList<String>();
				alinner.add(rst.getString("plan_id"));
				alinner.add(rst.getString("training_title"));
				
				String location="";
				int i=0;
				if(rst.getString("location_id")!=null && !rst.getString("location_id").equals("")){
					List<String> locationList=Arrays.asList(rst.getString("location_id").split(","));
					Set<String> locationSet = new HashSet<String>(locationList);
					Iterator<String> itr = locationSet.iterator();
					
					while (itr.hasNext()) {
						String locationId = (String) itr.next();
						if(i==0){
							location=hmwlocation.get(locationId.trim());
						}else{
							location+=","+hmwlocation.get(locationId.trim());
						}
						i++;
					}
				}				
				alinner.add(location);
				
				alinner.add(rst.getString("training_duration"));
				
				String training_period="";
				if((rst.getString("start_date")!=null && !rst.getString("start_date").equals("")) && (rst.getString("end_date")!=null && !rst.getString("end_date").equals(""))){
					training_period=uF.getDateFormat(rst.getString("start_date"), DBDATE, DATE_FORMAT)+" - "+uF.getDateFormat(rst.getString("end_date"), DBDATE, DATE_FORMAT);
				}				
				alinner.add(training_period);
				
				String certificate="No";
				if(uF.parseToBoolean(rst.getString("is_certificate"))){
					if(rst.getString("certificate_id")!=null && !rst.getString("certificate_id").equals("")){
						certificate=CF.getCertificateName(con, rst.getString("certificate_id").trim());
					}else{
						certificate="";
					}
				}
				alinner.add(certificate);
				
				int attended=0;
				if(rst.getString("emp_ids")!=null && !rst.getString("emp_ids").equals("")){
					attended=countTrainees(rst.getString("emp_ids"),uF);
				}
				
				alinner.add(""+attended);		
				
				historicalPlanList.add(alinner);

			}
			rst.close();
			pst.close();
			
			request.setAttribute("historicalPlanList", historicalPlanList);
			
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
			
		}
		
	}

	private int countTrainees(String empIds,UtilityFunctions uF) {

		  String[] empID=empIds.split(",");

		  return empID.length -1;
	}
	
	String strLocation;
	public String getStrLocation() {
		return strLocation;
	}

	public void setStrLocation(String strLocation) {
		this.strLocation = strLocation;
	}


	String strLevel;


	public String getStrLevel() {
		return strLevel;
	}

	public void setStrLevel(String strLevel) {
		this.strLevel = strLevel;
	}


	List<FillWLocation> wLocationList;


	public List<FillWLocation> getwLocationList() {
		return wLocationList;
	}

	public void setwLocationList(List<FillWLocation> wLocationList) {
		this.wLocationList = wLocationList;
	}

	
	List<FillLevel> levelList;

	
	public List<FillLevel> getLevelList() {
		return levelList;
	}

	public void setLevelList(List<FillLevel> levelList) {
		this.levelList = levelList;
	}



	HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {

		this.request=request;
		
	}
	

	
}