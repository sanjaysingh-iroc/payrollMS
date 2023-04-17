package com.konnect.jpms.training;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class TakeLearningPlan extends ActionSupport implements ServletRequestAware, IStatements {

		private static final long serialVersionUID = 1L;
		HttpSession session;
		String strUserType = null;
		String strSessionEmpId = null;
		String operation;
		Boolean autoGenerate = false;
		CommonFunctions CF=null;
		
		
		private static Logger log = Logger.getLogger(MyLearningPlan.class);
		
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
			
			strUserType = (String)session.getAttribute(USERTYPE);
			strSessionEmpId = (String)session.getAttribute(EMPID);
			
		//	request.setAttribute(PAGE, "/jsp/training/MyLearningPlan.jsp");
		//	request.setAttribute(TITLE, "My Learning Plan");
			
			if(takeLearning!=null)
			return takeLearningsubmit();

			return	viewLearning();
		//	
			
		}

		private String viewLearning() {
			
			Database db = new Database();
			db.setRequest(request);
			PreparedStatement pst = null;
			Connection con = null;
			UtilityFunctions uF = new UtilityFunctions();
			ResultSet rst = null;
		
			try {
				con=db.makeConnection(con);
				
				Map<String,String> hmAttribute=new HashMap<String, String>();
				pst=con.prepareStatement("select * from appraisal_attribute");
				rst=pst.executeQuery();
				while(rst.next()){
					hmAttribute.put(rst.getString("arribute_id"), rst.getString("attribute_name"));
				}
				rst.close();
				pst.close();
				
				Map<String,String> hmWlocation = CF.getWLocationMap(con, null, null);
				
				List<String> alTrainingInfo = new ArrayList<String>();
				
				pst=con.prepareStatement("select plan_id,training_title,training_objective,training_type,is_certificate,attribute_id" +
						",wlocation_id,trainer_ids,start_date,end_date  " +
						"from training_plan join training_schedule using (plan_id) " +
						"where plan_id=? ");
				pst.setInt(1,uF.parseToInt(getPlanId()));
				rst=pst.executeQuery();
				while(rst.next()){
				alTrainingInfo.add(rst.getString("plan_id"));
				alTrainingInfo.add(rst.getString("training_title"));
				alTrainingInfo.add(rst.getString("training_objective"));
				
				if(rst.getString("training_type").equals("1"))
					alTrainingInfo.add("Trainer Driven");
				else
					alTrainingInfo.add("Self Driven");
				if(rst.getString("is_certificate").equals("1"))
					alTrainingInfo.add("YES");
					else
					alTrainingInfo.add("NO");
					
				alTrainingInfo.add(hmAttribute.get(rst.getString("attribute_id")));
				
				alTrainingInfo.add(hmWlocation.get(rst.getString("wlocation_id")));
				
			//	alTrainingInfo.add(uF.parset)

				}
				rst.close();
				pst.close();
				
				request.setAttribute("alTrainingInfo", alTrainingInfo);
				
			} catch (SQLException e) {
		
				e.printStackTrace();
			} finally {
				db.closeResultSet(rst);
				db.closeStatements(pst);
				db.closeConnection(con);
		}

			return LOAD;
			
			
			
		}

		private String takeLearningsubmit() {
			
			Database db = new Database();
			db.setRequest(request);
			PreparedStatement pst = null;
			Connection con = null;
			UtilityFunctions uF = new UtilityFunctions();

			try {
				con=db.makeConnection(con);
				
				
				// insertion variables *********
/*				String startDate;
				String endDate;

				
				pst=con.prepareStatement("select * from training_schedule where plan_id=?");
				pst.setInt(1,uF.parseToInt(getPlanId()));
				
				rst=pst.executeQuery();
				if(rst.next()){
					
					startDate=rst.getString("");
					
				}*/
				
				
				pst=con.prepareStatement("insert into training_learnings (plan_id,emp_id,start_date)" +
						"values(?,?,?)");
				pst.setInt(1,uF.parseToInt(getPlanId()));
				pst.setInt(2,uF.parseToInt(strSessionEmpId));
				pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.execute();
				pst.close();
				
			}catch (Exception e) {
					e.printStackTrace();
			}finally{
				
				db.closeStatements(pst);
				db.closeConnection(con);
		}
			return SUCCESS;


		}

		HttpServletRequest request;
		@Override
		public void setServletRequest(HttpServletRequest request) {

			this.request=request;
			
		}
		
		String planId;

		public String getPlanId() {
			return planId;
		}

		public void setPlanId(String planId) {
			this.planId = planId;
		}
		
		String takeLearning;

		public String getTakeLearning() {
			return takeLearning;
		}

		public void setTakeLearning(String takeLearning) {
			this.takeLearning = takeLearning;
		}
	}