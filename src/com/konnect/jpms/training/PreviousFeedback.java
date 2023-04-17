package com.konnect.jpms.training;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class PreviousFeedback extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	String strSessionEmpId = null;
	
	private String trainerID;
	
	CommonFunctions CF=null; 
	     
      
	
	private static Logger log = Logger.getLogger(LearnersInfoDashboard.class);
	
	public String execute() {
		
		
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if (CF==null){
			return LOGIN;
		}
		

	//	request.setAttribute(PAGE, "/jsp/training/LearnersInfoDashboard.jsp");
		request.setAttribute(TITLE, "Feedback Information Info");

				
		getFeedbackList(CF);
		getFeedbackAggregate();
		
		return SUCCESS;
	

	}

	private void getFeedbackAggregate() {

		Database db = new Database();
		db.setRequest(request);
		PreparedStatement pst = null;
		Connection con = null;
		ResultSet rst=null;
		
		try{
			con=db.makeConnection(con);
			
			Map<String,String> hmPlanRating=new HashMap<String, String>();
			pst=con.prepareStatement("select ((marks/weightage)*100) as total,plan_id from (select sum(marks)as marks,sum(weightage)as weightage,plan_id " +
					" from training_question_answer where weightage>0 group by plan_id order by plan_id ) a ");
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

	private void getFeedbackList(CommonFunctions CF) {
		Database db = new Database();
		db.setRequest(request);
		PreparedStatement pst = null;
		Connection con = null;
		UtilityFunctions uF=new UtilityFunctions();
		ResultSet rst=null;
		
		try{
			con=db.makeConnection(con);
			Map<String,String> hmwlocationMap=CF.getWLocationMap(con, null,null);
			List<List<String>> alTrainingPlan=new ArrayList<List<String>>();
			Map<String,String> hmOrg=getOrganization(con);
			Map<String,String> hmLocationName=getLocationName(con);
			
//			pst=con.prepareStatement("select * from training_plan tp,training_schedule ts where tp.plan_id=ts.plan_id " +
//					" and ts.trainer_ids like '%,"+getTrainerID().trim()+",%' order by tp.plan_id");
			pst=con.prepareStatement("select * from training_plan tp,training_schedule ts where tp.plan_id=ts.plan_id " +
					" and ts.trainer_ids like '%,"+getTrainerID().trim()+",%' and to_date(CURRENT_TIMESTAMP::text,'YYYY-MM-DD') > end_date order by tp.plan_id");
			rst = pst.executeQuery();
			
			while(rst.next()){
				
				List<String> alinner=new ArrayList<String>();
				
				alinner.add(rst.getString("plan_id"));
				alinner.add(rst.getString("training_title"));				

				StringBuilder sbWlocation=new StringBuilder();
				if(rst.getString("wlocation_id")==null || rst.getString("wlocation_id").equals("")){
					sbWlocation.append("");
				}else{
					List<String> locationList=Arrays.asList(rst.getString("wlocation_id").split(","));
					if(locationList!=null){
						for(int i=0;i<locationList.size();i++){
							if(i==0){
								sbWlocation.append(hmwlocationMap.get(locationList.get(i).trim()));
							}else{
								sbWlocation.append(","+hmwlocationMap.get(locationList.get(i).trim()));
							}
						}
					}else{
						sbWlocation.append("");
					}
				}
				
				alinner.add(sbWlocation.toString());
				alinner.add(uF.showData(rst.getString("training_duration"), "0"));
				alinner.add(uF.getDateFormat(rst.getString("start_date"), DBDATE, DATE_FORMAT));
				alinner.add( uF.getDateFormat(rst.getString("end_date"), DBDATE, DATE_FORMAT));

				String yesno="";
				if(rst.getString("is_certificate").equalsIgnoreCase("t")){
					yesno="YES";
				}else{
					yesno="NO";
				}
				alinner.add(yesno);
				String org_id = rst.getString("org_id");
				String org_name= org_id==null ? "" : hmOrg.get(org_id.trim());
				alinner.add(org_name);
				
							
				String location="";
				int i=0;
				if(rst.getString("location_id")!=null && !rst.getString("location_id").equals("")){
					List<String> locationList=Arrays.asList(rst.getString("location_id").split(","));
					Set<String> locationSet = new HashSet<String>(locationList);
					Iterator<String> itr = locationSet.iterator();
					
					while (itr.hasNext()) {
						String locationId = (String) itr.next();
						if(i==0){
							location=hmLocationName.get(locationId.trim());
						}else{
							location+=","+hmLocationName.get(locationId.trim());
						}
						i++;
					}
				}
				
				alinner.add(location);
				
				alinner.add(rst.getString("certificate_id"));
				alTrainingPlan.add(alinner);
			}
			rst.close();
			pst.close();
			request.setAttribute("alTrainingPlan", alTrainingPlan);
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	
	
	private Map<String, String> getLocationName(Connection con) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String,String> hmLocationName=new HashMap<String, String>();
		
		try {
			pst = con.prepareStatement("select * from work_location_info");
			rs=pst.executeQuery();
			while(rs.next()){
				hmLocationName.put(rs.getString("wlocation_id"),rs.getString("wlocation_name"));
			}
			rs.close();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(rs !=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return hmLocationName;
	}

	
	
	private Map<String, String> getOrganization(Connection con) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String,String> hmOrg=new HashMap<String, String>();
		
		try {
			pst = con.prepareStatement("select org_id,org_name from org_details");
			rs=pst.executeQuery();
			
			while(rs.next()){
				hmOrg.put(rs.getString("org_id"),rs.getString("org_name"));
			}
			rs.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(rs !=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return hmOrg;
	}
	
	
	public String getTrainerID() {
		return trainerID;
	}

	public void setTrainerID(String trainerID) {
		this.trainerID = trainerID;
	}




	HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {

		this.request=request;
		
	}
	

}
