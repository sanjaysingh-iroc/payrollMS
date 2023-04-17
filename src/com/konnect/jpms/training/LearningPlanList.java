package com.konnect.jpms.training;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class LearningPlanList extends ActionSupport implements ServletRequestAware, IStatements {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	boolean isEmpUserType = false;
	CommonFunctions CF = null;
	
	private String dataType;
	String strSessionEmpId = null;
	private String learningPlanId;
	private String strFirstPlanId;
	private String proPage;
	private String minLimit;
	private String strSearchJob;
	public String execute() throws Exception {

		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;
		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);

		if(getDataType() == null || getDataType().equals("") || getDataType().equalsIgnoreCase("Null")) {
			setDataType("L");
		}
	
		getLearningPlanList();
		
		return LOAD;

	}
	private void getLearningPlanList() {
		
		List<List<String>> allLearningreport = new ArrayList<List<String>>();	
		Connection con=null;
		Database db=new Database();
		db.setRequest(request);
		PreparedStatement pst=null;
		ResultSet rst=null;
		UtilityFunctions uF = new UtilityFunctions();
		Map<String,List<String>> hmLearningPlanName = new LinkedHashMap<String,List<String>>(); 
		int count=0;
	    try {	
	    	
	    	con=db.makeConnection(con);
	    	Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
	    	
	    	StringBuilder sbQuery = new StringBuilder();
	    	sbQuery.append("select count(learning_plan_id) as cnt from learning_plan_details ");
	    	if(getDataType() != null && getDataType().equals("L")) {
				sbQuery.append(" where is_close = false ");
			} else if(getDataType() != null && getDataType().equals("C")) {
				sbQuery.append(" where is_close = true ");
			}
	    	
    	    if(getStrSearchJob()!=null && !getStrSearchJob().trim().equals("") &&  !getStrSearchJob().equalsIgnoreCase("null")) {
				sbQuery.append(" and (upper(learning_plan_name) like '%"+getStrSearchJob().trim().toUpperCase()+"%' " +
				" or upper(learning_plan_objective) like '%"+getStrSearchJob().trim().toUpperCase()+"%' )");
				
            }
			pst=con.prepareStatement(sbQuery.toString());
//			System.out.println("pst=====>"+pst);
			rst=pst.executeQuery();
			int proCount = 0;
			int proCnt = 0;
			while(rst.next()) {
				proCnt = rst.getInt("cnt");
				proCount = rst.getInt("cnt")/15;
				if(rst.getInt("cnt")%15 != 0) {
					proCount++;
				}
			}
			rst.close();
			pst.close();
			request.setAttribute("proCount", proCount+"");
			request.setAttribute("proCnt", proCnt+"");
			
			sbQuery = new StringBuilder();
	    	sbQuery.append("select * from learning_plan_details ");
	    	if(getDataType() != null && getDataType().equals("L")) {
				sbQuery.append(" where is_close = false ");
			} else if(getDataType() != null && getDataType().equals("C")) {
				sbQuery.append(" where is_close = true ");
			}
    	    if(getStrSearchJob()!=null && !getStrSearchJob().trim().equals("") &&  !getStrSearchJob().equalsIgnoreCase("null")) {
				sbQuery.append(" and (upper(learning_plan_name) like '%"+getStrSearchJob().trim().toUpperCase()+"%' " +
				" or upper(learning_plan_objective) like '%"+getStrSearchJob().trim().toUpperCase()+"%') ");
				
            }
			sbQuery.append(" order by learning_plan_id desc ");
			int intOffset = uF.parseToInt(getMinLimit());
			sbQuery.append(" limit 15 offset "+intOffset+"");
			pst=con.prepareStatement(sbQuery.toString());
//			System.out.println("pst1==>"+pst);
			rst=pst.executeQuery();
			
			while(rst.next()) {
				
				if(count==0){
					setStrFirstPlanId(rst.getString("learning_plan_id"));
				}
				
				List<String> learningPlanList =new ArrayList<String>(); 
							
				learningPlanList.add(rst.getString("learning_plan_id"));//0
				learningPlanList.add(rst.getString("learning_plan_name"));//1
				if(rst.getString("group_or_condition") != null && rst.getString("group_or_condition").equals("3")) {
					learningPlanList.add("General");//2
				} else if(rst.getString("group_or_condition") != null && rst.getString("group_or_condition").equals("2")) {
					learningPlanList.add("Gap");//2
				} else if(rst.getString("group_or_condition") != null && rst.getString("group_or_condition").equals("1")) {
					learningPlanList.add("Induction");//2
				} else {
					learningPlanList.add("");//2
				}

				if(uF.parseToBoolean(rst.getString("is_close"))) {
					learningPlanList.add("<div style=\"float:left;border-left:4px solid #E61626;padding:10px;\" class=\"custom-legend denied\"><div class=\"legend-info\"></div></div>");//3
				} else if(uF.parseToBoolean(rst.getString("is_publish"))) {
					learningPlanList.add("<div style=\"float:left;border-left:4px solid #15AA08;padding:10px;\" class=\"custom-legend approved\"><div class=\"legend-info\"></div></div>");//3
				} else {
					learningPlanList.add("<div style=\"float:left;border-left:4px solid #ff9a02;padding:10px;\" class=\"custom-legend pullout\"><div class=\"legend-info\"></div></div>");//3
				}
				hmLearningPlanName.put(rst.getString("learning_plan_id"), learningPlanList);
				count++;
			}
			
			rst.close();
			pst.close();
			
		} catch (Exception e){
				e.printStackTrace();
		}finally{
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
		request.setAttribute("hmLearningPlanName", hmLearningPlanName);
	
	}

	private HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request=request;
		
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getLearningPlanId() {
		return learningPlanId;
	}

	public void setLearningPlanId(String learningPlanId) {
		this.learningPlanId = learningPlanId;
	}

	public String getStrFirstPlanId() {
		return strFirstPlanId;
	}

	public void setStrFirstPlanId(String strFirstPlanId) {
		this.strFirstPlanId = strFirstPlanId;
	}

	public String getProPage() {
		return proPage;
	}

	public void setProPage(String proPage) {
		this.proPage = proPage;
	}

	public String getMinLimit() {
		return minLimit;
	}

	public void setMinLimit(String minLimit) {
		this.minLimit = minLimit;
	}

	public String getStrSearchJob() {
		return strSearchJob;
	}

	public void setStrSearchJob(String strSearchJob) {
		this.strSearchJob = strSearchJob;
	}
}
