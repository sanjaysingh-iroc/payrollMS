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

public class TrainingPlanDetails extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	String strSessionEmpId = null;

	private int empId;  

	CommonFunctions CF=null;

	private static Logger log = Logger.getLogger(TrainingPlanInfo.class);
	
	private String fromPage; 
	private String planId;
	public String execute() {
		
		
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if (CF==null){
			return LOGIN;
		}
		
		getTrainingPlanDetails();

		
		return SUCCESS;
	

	}
	
	private void getTrainingPlanDetails() {

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

			pst=con.prepareStatement("select training_id,count(training_schedule_details_id) as cnt from training_schedule_details group by training_id");
			rst=pst.executeQuery();
			Map<String, String> hmTrainingDuration = new HashMap<String, String>();
			while(rst.next()){
				hmTrainingDuration.put(rst.getString("training_id"), rst.getString("cnt"));
			}
			rst.close();
			pst.close();
			
			
			pst=con.prepareStatement("select * from training_plan tp left join training_schedule ts using(plan_id) where plan_id=?");
			pst.setInt(1, uF.parseToInt(getPlanId()));
			rst=pst.executeQuery();
			while(rst.next()){
				
				List<String> alinner=new ArrayList<String>();
				
				alinner.add(rst.getString("plan_id"));//0
				alinner.add(rst.getString("training_title"));		//1	
				
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
				alinner.add(sbWlocation.toString());//2
				alinner.add(""+uF.parseToInt(hmTrainingDuration.get(rst.getString("plan_id"))));//3
				alinner.add(uF.getDateFormat(rst.getString("start_date"), DBDATE, DATE_FORMAT));//4
				alinner.add( uF.getDateFormat(rst.getString("end_date"), DBDATE, DATE_FORMAT));//5

				String yesno="";
				if(rst.getString("is_certificate").equalsIgnoreCase("t")){
					yesno="YES";
				}else{
					yesno="NO";
				}
				alinner.add(yesno);//6
				String org_id=rst.getString("org_id");
				String org_name=org_id==null?"":hmOrg.get(org_id.trim());
				alinner.add(org_name);//7
							
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
				
				alinner.add(location);//8
				
				alinner.add(rst.getString("certificate_id"));//9
				alinner.add(CF.getCertificateName(con, rst.getString("certificate_id").trim()));//10
				alTrainingPlan.add(alinner);
			}
			rst.close();
			pst.close();
//			System.out.println("alTrainingPlan =====> " + alTrainingPlan);
			request.setAttribute("alTrainingPlan", alTrainingPlan);
			
			pst=con.prepareStatement("select learning_plan_stage_name_id from learning_plan_stage_details where learning_plan_stage_name_id > 0 and learning_type='Training'");
//			System.out.println("pst======>"+pst);
			rst=pst.executeQuery();
			List<String> alAssignTrainPlan = new ArrayList<String>();
			while(rst.next()){
				if(!alAssignTrainPlan.contains(rst.getString("learning_plan_stage_name_id"))){
					alAssignTrainPlan.add(rst.getString("learning_plan_stage_name_id"));
				}
			}
//			System.out.println("alAssignTrainPlan======>"+alAssignTrainPlan);
			request.setAttribute("alAssignTrainPlan", alAssignTrainPlan);
			
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

	HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {

		this.request=request;
		
	}

	public String getFromPage() {
		return fromPage;
	}

	public void setFromPage(String fromPage) {
		this.fromPage = fromPage;
	}

	public String getPlanId() {
		return planId;
	}

	public void setPlanId(String planId) {
		this.planId = planId;
	}
	
}
