package com.konnect.jpms.training;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class TrainersNameList extends ActionSupport implements ServletRequestAware, IStatements {
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	CommonFunctions CF = null;
	String strSessionEmpId = null;

	private String proPage;
	private String minLimit;
	
	private String strTrainerId;
	private String strEmpId;
	private String trainerEmpId;
	private String strLevel;
	private String strLocation;
	private List<FillLevel> levelList;
	private List<FillWLocation> wLocationList;
	private String strSearchJob;
	
	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;
			
		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);

		UtilityFunctions uF = new UtilityFunctions();
		
		if(uF.parseToInt(getProPage()) == 0) {
			setProPage("1");
		}
		
		getTrainersNamesList(uF);
		return LOAD;
	}

	
	private void getTrainersNamesList(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		 
	    try {
	    	con=db.makeConnection(con);
	    	
	       	Map<String, String> hmDesignation = CF.getEmpDesigMap(con);
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			request.setAttribute("hmEmpName", hmEmpName);
//			System.out.println("getStrSearchJob==>"+getStrSearchJob());
			Map<String,List<String>> hmTrainers = new LinkedHashMap<String,List<String>>();
			int i = 0;
			StringBuilder strquery=new StringBuilder();
			strquery.append("select count(trainer_id) as cnt from training_trainer where trainer_id > 0   ");
			if((strLocation!=null && !strLocation.equals("")) && (strLevel!=null && !strLevel.equals("")) ){
				strquery.append(" and trainer_work_location="+strLocation+" and trainer_level_id="+strLevel);
			}else if(strLocation!=null && !strLocation.equals("")){
				strquery.append(" and trainer_work_location="+strLocation);
			}else if(strLevel!=null && !strLevel.equals("")){
				strquery.append(" and trainer_level_id="+strLevel);
			}
			if(getStrSearchJob()!=null && !getStrSearchJob().trim().equals("") &&  !getStrSearchJob().equalsIgnoreCase("null")) {
				strquery.append(" and (upper(trainer_name) like '%"+getStrSearchJob().trim().toUpperCase()+"%' )");
				
            }
			
		
			pst=con.prepareStatement(strquery.toString());
//			System.out.println("pst1 ==>"+pst);
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
			
//			System.out.println("proCount==>"+proCount);
//			System.out.println("proCnt==>"+proCnt);
			
			strquery=new StringBuilder();
			strquery.append("select * from training_trainer where trainer_id > 0 ");
			if((strLocation!=null && !strLocation.equals("")) && (strLevel!=null && !strLevel.equals("")) ){
				strquery.append(" and trainer_work_location="+strLocation+" and trainer_level_id="+strLevel);
			}else if(strLocation!=null && !strLocation.equals("")){
				strquery.append(" and trainer_work_location="+strLocation);
			}else if(strLevel!=null && !strLevel.equals("")){
				strquery.append(" and trainer_level_id="+strLevel);
			}
			
			if(getStrSearchJob()!=null && !getStrSearchJob().trim().equals("") &&  !getStrSearchJob().equalsIgnoreCase("null")) {
				strquery.append(" and (upper(trainer_name) like '%"+getStrSearchJob().trim().toUpperCase()+"%' )");
				
            }
			strquery.append(" order by trainer_name");
			int intOffset = uF.parseToInt(getMinLimit());
			strquery.append(" limit 15 offset "+intOffset+"");
			pst=con.prepareStatement(strquery.toString());
//			System.out.println("pst2 ====>>> " + pst);
			rst=pst.executeQuery();
			while(rst.next()) {
				if(i == 0) {
					setStrTrainerId(rst.getString("trainer_id"));
					setStrEmpId(rst.getString("emp_id"));
					setTrainerEmpId(rst.getString("trainer_emp_id"));
				}
				
				List<String> alInner = new ArrayList<String>();
				alInner.add(rst.getString("trainer_id"));//0		
				alInner.add(rst.getString("trainer_name"));//1		
				alInner.add(rst.getString("emp_id"));//2
				alInner.add(rst.getString("trainer_emp_id"));//3
				StringBuilder sbStatus=new StringBuilder();
				if(rst.getString("emp_id")==null || rst.getString("emp_id").equals("null")) {
					alInner.add("-");//4
					sbStatus.append("<div style=\"float:left;border-left:4px solid #ff9a02;padding:10px;\" class=\"custom-legend pullout\"><div class=\"legend-info\"></div></div>");
				}else {
					alInner.add(uF.showData(hmDesignation.get(rst.getString("emp_id").trim().toString()), ""));//4
					sbStatus.append("<div style=\"float:left;border-left:4px solid #15AA08;padding:10px;\" class=\"custom-legend approved\"><div class=\"legend-info\"></div></div>");
				}
				alInner.add(sbStatus.toString());//5
				hmTrainers.put(rst.getString("trainer_id"), alInner);
				i++;
			}
			rst.close();
			pst.close();
			request.setAttribute("hmTrainers", hmTrainers);
		} catch (Exception e) {
				e.printStackTrace();
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}
	
	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
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


	public String getStrTrainerId() {
		return strTrainerId;
	}

	public void setStrTrainerId(String strTrainerId) {
		this.strTrainerId = strTrainerId;
	}


	public String getStrLevel() {
		return strLevel;
	}


	public void setStrLevel(String strLevel) {
		this.strLevel = strLevel;
	}


	public String getStrLocation() {
		return strLocation;
	}


	public void setStrLocation(String strLocation) {
		this.strLocation = strLocation;
	}


	public List<FillLevel> getLevelList() {
		return levelList;
	}


	public void setLevelList(List<FillLevel> levelList) {
		this.levelList = levelList;
	}


	public List<FillWLocation> getwLocationList() {
		return wLocationList;
	}


	public void setwLocationList(List<FillWLocation> wLocationList) {
		this.wLocationList = wLocationList;
	}


	public String getStrEmpId() {
		return strEmpId;
	}


	public void setStrEmpId(String strEmpId) {
		this.strEmpId = strEmpId;
	}


	public String getTrainerEmpId() {
		return trainerEmpId;
	}


	public void setTrainerEmpId(String trainerEmpId) {
		this.trainerEmpId = trainerEmpId;
	}

	public String getStrSearchJob() {
		return strSearchJob;
	}

	public void setStrSearchJob(String strSearchJob) {
		this.strSearchJob = strSearchJob;
	}
}
