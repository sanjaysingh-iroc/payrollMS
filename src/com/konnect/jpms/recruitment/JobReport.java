package com.konnect.jpms.recruitment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillDesig;
import com.konnect.jpms.select.FillGrade;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillSourceTypeAndName;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class JobReport extends ActionSupport implements
		ServletRequestAware, IStatements {
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	boolean isEmpUserType = false;
	CommonFunctions CF = null;
	String strSessionEmpId = null;
//	
	
	private String dataType;
	private String recruitId;
	private String operation;
	
	private String pageNumber;
	private String minLimit;
	
	private String f_org;
	private String[] location;
	private String[] designation;
	
	private List<FillOrganisation> organisationList;
	private List<FillDesig> desigList;
	private List<FillWLocation> workLocationList;
	
	private List<FillSourceTypeAndName> sourceTypeList;
	private List<FillSourceTypeAndName> sourceNameList;
	
	private String strSearchJob;
	
	private String strLocation;
	private String strDesignation;
	private String strAppliSourceName;
	
	private String appliSourceType;
	private String[] appliSourceName;
	
	private String currRecruitId;
	private String fromPage;
	public String execute() throws Exception {
	    
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null) 
			return LOGIN;
		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);

		request.setAttribute(TITLE, TRequirement);
		request.setAttribute(PAGE, "/jsp/recruitment/jobreport.jsp");
		UtilityFunctions uF = new UtilityFunctions();
		
		organisationList = new FillOrganisation(request).fillOrganisation();
		workLocationList = new FillWLocation(request).fillWLocation(getF_org());
		desigList = new FillDesig(request).fillDesig(uF.parseToInt(getF_org()));
		
//		System.out.println("getFromPage==>"+getFromPage());
		if(getOperation() != null && getOperation().equals("D")) {
			deleteRecruitment();
			return VIEW;
		}
		
//		System.out.println("dataType ===>> " + getDataType() + " -- pageNumber ===>> " + getPageNumber() + " -- minLimit ===>> " + getMinLimit());
		
		sourceTypeList = new FillSourceTypeAndName(request).fillSourceType();
		if(getAppliSourceType() !=null && !getAppliSourceType().equals("")) {
			if(uF.parseToInt(getAppliSourceType()) != SOURCE_WEBSITE) {
				sourceNameList = new FillSourceTypeAndName(request).fillSourceNameOnType(getAppliSourceType());
			}
		} else {
			sourceNameList = new FillSourceTypeAndName(request).fillAllSourceName();
		}
		
		if(getDataType() == null || getDataType().trim().equals("") || getDataType().trim().equalsIgnoreCase("NULL")){
			setDataType("L");
		}
		
		if(getStrLocation() != null && !getStrLocation().equals("") && !getStrLocation().equalsIgnoreCase("null")) {
			setLocation(getStrLocation().split(","));
		}
		
		if(getStrDesignation() != null && !getStrDesignation().equals("") && !getStrDesignation().equalsIgnoreCase("null")) {
			setDesignation(getStrDesignation().split(","));
		}
		
		if(getStrAppliSourceName() != null && !getStrAppliSourceName().equals("") && !getStrAppliSourceName().equalsIgnoreCase("null")) {
			setAppliSourceName(getStrAppliSourceName().split(","));
		}
		
		getSearchAutoCompleteData(uF);
		preparejobreport();
		getApplicationCountOfRecruitmentID();
		
		prepChart1Data();
		prepChart2Data();
		
		
		return LOAD;

	}

	
private void getSearchAutoCompleteData(UtilityFunctions uF) {
	Database db = new Database();
	db.setRequest(request);
	Connection con = null;
	PreparedStatement pst = null;
	ResultSet rs = null;
	try {
		con = db.makeConnection(con);

		SortedSet<String> setJobList = new TreeSet<String>();
		StringBuilder sbQuery = new StringBuilder();
		sbQuery.append("select job_code,job_title,skills,essential_skills from recruitment_details where job_approval_status=1");
		if(getDataType() != null && getDataType().equals("L")) {
			sbQuery.append(" and close_job_status = false ");
		} else if(getDataType() != null && getDataType().equals("C")) {
			sbQuery.append(" and close_job_status = true ");
		}
		if(getAppliSourceType() != null && !getAppliSourceType().equals("")) {
			sbQuery.append(" and recruitment_id in (select recruitment_id from candidate_application_details where source_type="+uF.parseToInt(getAppliSourceType())+" ");
			if(getAppliSourceName()!=null && getAppliSourceName().length > 0 && !getAppliSourceName()[0].trim().equals("")) {
				if(uF.parseToInt(getAppliSourceType()) == SOURCE_HR || uF.parseToInt(getAppliSourceType()) == SOURCE_RECRUITER) {
					sbQuery.append(" and added_by in ("+StringUtils.join(getAppliSourceName(), ",")+")");
				} else if(uF.parseToInt(getAppliSourceType()) == SOURCE_REFERENCE) {
					sbQuery.append(" and source_or_ref_code in ("+StringUtils.join(getAppliSourceName(), ",")+")");
				}
			}
			sbQuery.append(") ");
		} else if(getAppliSourceName()!=null && getAppliSourceName().length > 0 && !getAppliSourceName()[0].trim().equals("")) {
			sbQuery.append(" and recruitment_id in (select recruitment_id from candidate_application_details where candidate_id>0 ");
			sbQuery.append(" and (added_by in ("+StringUtils.join(getAppliSourceName(), ",")+")");
			sbQuery.append(" or source_or_ref_code in ("+StringUtils.join(getAppliSourceName(), ",")+")) ");
			sbQuery.append(") ");
		}
		if(strUserType != null && (strUserType.equals(MANAGER) || strUserType.equals(RECRUITER))) {
			sbQuery.append(" and added_by = "+ uF.parseToInt(strSessionEmpId) +" ");
		}
		if(uF.parseToInt(getF_org()) > 0) {
		 sbQuery.append(" and org_id = "+uF.parseToInt(getF_org())+" ");
        }
        if(getLocation()!=null && getLocation().length > 0 && !getLocation()[0].trim().equals("")) {
        	sbQuery.append(" and wlocation in ("+StringUtils.join(getLocation(), ",")+") ");
		}
        if(getDesignation()!=null && getDesignation().length > 0 && !getDesignation()[0].trim().equals("")) {
        	sbQuery.append(" and designation_id in ("+StringUtils.join(getDesignation(), ",")+") ");
		}
		pst = con.prepareStatement(sbQuery.toString());
//		System.out.println("pst======>"+pst);
		rs = pst.executeQuery();
		StringBuilder sbSkills = null;
		while (rs.next()) {
			setJobList.add(rs.getString("job_code"));
			if(rs.getString("job_title")!=null && !rs.getString("job_title").trim().equals("")){
				setJobList.add(rs.getString("job_title"));
			}
			
			if(rs.getString("skills")!=null && !rs.getString("skills").trim().equals("")){
				if(sbSkills == null){
					sbSkills = new StringBuilder();
					sbSkills.append(rs.getString("skills"));
				} else {
					sbSkills.append(","+rs.getString("skills"));
				}
			}
			
			if(rs.getString("essential_skills")!=null && !rs.getString("essential_skills").trim().equals("")){
				if(sbSkills == null){
					sbSkills = new StringBuilder();
					sbSkills.append(rs.getString("essential_skills"));
				} else {
					sbSkills.append(","+rs.getString("essential_skills"));
				}
			}
		}
		rs.close();
		pst.close();
		
		if(sbSkills != null){
			List<String> alSkills = Arrays.asList(sbSkills.toString().trim().split(","));
			StringBuilder sbSkillsId = null;
			for(int i=0; alSkills != null && i < alSkills.size(); i++){
				if(alSkills.get(i)!=null && !alSkills.get(i).trim().equals("") && uF.parseToInt(alSkills.get(i).trim()) > 0){
					if(sbSkillsId == null){
						sbSkillsId = new StringBuilder();
						sbSkillsId.append(alSkills.get(i).trim());
					} else {
						sbSkillsId.append(","+alSkills.get(i).trim());
					}
				}
			}
			
			if(sbSkillsId!=null){
				pst = con.prepareStatement("select skill_name from skills_details where skill_name is not null " +
						"and skill_name != '' and skill_id in("+sbSkillsId.toString()+")");
				rs = pst.executeQuery();
				while (rs.next()) {
					setJobList.add(rs.getString("skill_name"));
				}
				rs.close();
				pst.close();
			}
		}
		
		StringBuilder sbData = null;
		Iterator<String> it = setJobList.iterator();
		while (it.hasNext()){
			String strData = it.next();
			if(sbData == null){
				sbData = new StringBuilder();
				sbData.append("\""+strData+"\"");
			} else {
				sbData.append(",\""+strData+"\"");
			}
		}
		
		if(sbData == null){
			sbData = new StringBuilder();
		}
		request.setAttribute("sbData", sbData.toString());
			
	} catch (Exception e) {
		e.printStackTrace();
	} finally {
		db.closeResultSet(rs);
		db.closeStatements(pst);
		db.closeConnection(con);
	}
	
}


private void deleteRecruitment() {
	Connection con=null;
	Database db=new Database();
	db.setRequest(request);
	PreparedStatement pst = null;
	UtilityFunctions uF = new UtilityFunctions();
	try {
		con=db.makeConnection(con);
		pst=con.prepareStatement("delete from candidate_activity_details where recruitment_id=?");
		pst.setInt(1, uF.parseToInt(getRecruitId()));
		pst.executeUpdate();
		pst.close();
		
		pst=con.prepareStatement("delete from candidate_application_details where recruitment_id=?");
		pst.setInt(1, uF.parseToInt(getRecruitId()));
		pst.executeUpdate();
		pst.close();
		
		pst=con.prepareStatement("delete from candidate_interview_panel where recruitment_id=?");
		pst.setInt(1, uF.parseToInt(getRecruitId()));
		pst.executeUpdate();
		pst.close();
		
		pst=con.prepareStatement("delete from panel_interview_details where recruitment_id=?");
		pst.setInt(1, uF.parseToInt(getRecruitId()));
		pst.executeUpdate();
		pst.close();
		
		pst=con.prepareStatement("delete from recruitment_details where recruitment_id=?");
		pst.setInt(1, uF.parseToInt(getRecruitId()));
		pst.executeUpdate();
		pst.close();
		
	} catch(Exception e) {
		e.printStackTrace();
	} finally {
		db.closeStatements(pst);
		db.closeConnection(con);
	}
}


public void getApplicationCountOfRecruitmentID() {
		Connection con=null;
		Database db=new Database();
		db.setRequest(request);
		PreparedStatement pst=null;
		ResultSet rst=null;
		UtilityFunctions uF = new UtilityFunctions();
		
		try{
			con=db.makeConnection(con);
			Map<String, String> hmAppCount = new HashMap<String, String>();
			pst=con.prepareStatement("select count(*) as count,recruitment_id from candidate_application_details group by recruitment_id");
			rst=pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while(rst.next()){
				hmAppCount.put(rst.getString("recruitment_id"), rst.getString("count"));
			}
			rst.close();
			pst.close();
//			System.out.println("hmAppCount ===> "+hmAppCount);
			request.setAttribute("hmAppCount", hmAppCount);
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	public void prepChart1Data() {
		
		Connection con=null;
		Database db=new Database();
		db.setRequest(request);
		PreparedStatement pst=null;
		ResultSet rst=null;
		
		try{
			con=db.makeConnection(con);
			
			Map<String,Integer> hmChart1=new HashMap<String, Integer>();
			pst=con.prepareStatement("select count(*) as count,application_status from candidate_application_details group by application_status");
			rst=pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while(rst.next()){
				
				if(rst.getInt("application_status")==2){
					
					int temp=0;
					
					if(hmChart1.get("acceptedAppl")!=null)
					temp=hmChart1.get("acceptedAppl");
					
					temp+=rst.getInt("count");
					hmChart1.put("acceptedAppl",temp);
					
				}else if(rst.getInt("application_status")==-1){
					
					int temp=0;
					
					if(hmChart1.get("rejectedAppl")!=null)
					temp=hmChart1.get("rejectedAppl");
					
					temp+=rst.getInt("count");
					hmChart1.put("rejectedAppl",temp);
					
				}else if(rst.getInt("application_status")==0){
					
					int temp=0;
					
					if(hmChart1.get("underprocessAppl")!=null)
					temp=hmChart1.get("underprocessAppl");
					
					temp+=rst.getInt("count");
					hmChart1.put("underprocessAppl",temp);
				}
			}
			rst.close();
			pst.close();

			if(hmChart1.get("acceptedAppl")==null)	hmChart1.put("acceptedAppl",0);
			if(hmChart1.get("underprocessAppl")==null)	hmChart1.put("underprocessAppl",0);
			if(hmChart1.get("rejectedAppl")==null)	hmChart1.put("rejectedAppl",0);
			
			request.setAttribute("hmchart1",hmChart1);
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	public void prepChart2Data() {
		
		Connection con=null;
		Database db=new Database();
		db.setRequest(request);
		PreparedStatement pst=null;
		ResultSet rst=null;
		
		try{
			con=db.makeConnection(con);
			
			Map<String,Integer> hmChart2=new HashMap<String, Integer>();
			pst=con.prepareStatement("select count(*) as count,candidate_status,candidate_final_status from candidate_application_details group by candidate_status,candidate_final_status");
			rst=pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while(rst.next()) {
				
				if(rst.getInt("candidate_final_status")==1 && rst.getInt("candidate_status")==1){
					hmChart2.put("acceptedCand",rst.getInt("count"));
				}else if(rst.getInt("candidate_final_status")==1 && rst.getInt("candidate_status")==-1){
					hmChart2.put("rejectedCand",rst.getInt("count"));
				}else if(rst.getInt("candidate_final_status")==1 && rst.getInt("candidate_status")==0){
					hmChart2.put("underprocessCand",rst.getInt("count"));
				}
/*				totalCand+=rst.getInt("count");
				hmChart2.put("total", totalCand);
				*/
			}
			rst.close();
			pst.close();
			if(hmChart2.get("rejectedCand")==null)	hmChart2.put("rejectedCand",0);
			if(hmChart2.get("acceptedCand")==null)	hmChart2.put("acceptedCand",0);
			if(hmChart2.get("underprocessCand")==null)	hmChart2.put("underprocessCand",0);

			
			request.setAttribute("hmchart2",hmChart2);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	public void preparejobreport() {
		
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		PreparedStatement pst = null;
		ResultSet rst = null;
		UtilityFunctions uF = new UtilityFunctions();
		
	    try {
	    	con = db.makeConnection(con);
	    	//preparing acceptance from candidate********************
	    	getSelectedFilter(uF);
	    	
	    	int pageCount = 0;
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select count(recruitment_id) as recCount from recruitment_details where job_approval_status=1 ");
				if(strUserType != null && (strUserType.equals(MANAGER) || strUserType.equals(RECRUITER))){
					sbQuery.append(" and (added_by = "+ uF.parseToInt(strSessionEmpId) +" or hiring_manager like '%,"+strSessionEmpId+",%' )");
				}
				if(getDataType() != null && getDataType().equals("L")) {
					sbQuery.append(" and close_job_status = false ");
				} else if(getDataType() != null && getDataType().equals("C")) {
					sbQuery.append(" and close_job_status = true ");
				}
				if(getAppliSourceType() != null && !getAppliSourceType().equals("")) {
					sbQuery.append(" and recruitment_id in (select recruitment_id from candidate_application_details where source_type="+uF.parseToInt(getAppliSourceType())+" ");
					if(getAppliSourceName()!=null && getAppliSourceName().length > 0 && !getAppliSourceName()[0].trim().equals("")) {
						if(uF.parseToInt(getAppliSourceType()) == SOURCE_HR || uF.parseToInt(getAppliSourceType()) == SOURCE_RECRUITER) {
							sbQuery.append(" and added_by in ("+StringUtils.join(getAppliSourceName(), ",")+")");
						} else if(uF.parseToInt(getAppliSourceType()) == SOURCE_REFERENCE) {
							sbQuery.append(" and source_or_ref_code in ("+StringUtils.join(getAppliSourceName(), ",")+")");
						}
					}
					sbQuery.append(") ");
				} else if(getAppliSourceName()!=null && getAppliSourceName().length > 0 && !getAppliSourceName()[0].trim().equals("")) {
					sbQuery.append(" and recruitment_id in (select recruitment_id from candidate_application_details where candidate_id>0 ");
					sbQuery.append(" and (added_by in ("+StringUtils.join(getAppliSourceName(), ",")+")");
					sbQuery.append(" or source_or_ref_code in ("+StringUtils.join(getAppliSourceName(), ",")+")) ");
					sbQuery.append(") ");
				}
				
				if(uF.parseToInt(getF_org()) > 0) {
		        	sbQuery.append(" and org_id = "+uF.parseToInt(getF_org())+" ");
		        }
		        if(getLocation()!=null && getLocation().length > 0 && !getLocation()[0].trim().equals("")) {
					sbQuery.append(" and wlocation in ("+StringUtils.join(getLocation(), ",")+") ");
				}
		        if(getDesignation()!=null && getDesignation().length > 0 && !getDesignation()[0].trim().equals("")) {
					sbQuery.append(" and designation_id in ("+StringUtils.join(getDesignation(), ",")+") ");
				}
//			sbQuery.append("order by recruitment_id desc");
//			System.out.println("sbQuery ===> " + sbQuery.toString());
			pst=con.prepareStatement(sbQuery.toString());
			rst=pst.executeQuery();
			int recCnt = 0;
		//	rst = pst.executeQuery();
			while(rst.next()) {
				recCnt = rst.getInt("recCount");
				pageCount = rst.getInt("recCount")/10;
				if(rst.getInt("recCount")%10 != 0) {
					pageCount++;
				}
			}
			rst.close();
			pst.close();
			request.setAttribute("pageCount", pageCount+"");
			request.setAttribute("recCnt", recCnt+"");
			
			
	    	List<String> recruitmentIDList = new ArrayList<String>();
	    	StringBuilder sbQuery1 = new StringBuilder();
			sbQuery1.append("select a.*,cpd.recruitment_id as p_recruitment_id from (select a.*,cpd.recruitment_id as r_recruitment_id from ( " +
				" select designation_name,job_code,recruitment_id,custum_designation,close_job_status,no_position from recruitment_details " +
				" left join designation_details using(designation_id) where job_approval_status=1 ");
				if(strUserType != null && (strUserType.equals(MANAGER) || strUserType.equals(RECRUITER))) {
					sbQuery1.append(" and (added_by = "+ uF.parseToInt(strSessionEmpId) +" or hiring_manager like '%,"+strSessionEmpId+",%' )");
				}
				if(getDataType() != null && getDataType().equals("L")) {
					sbQuery1.append(" and close_job_status = false ");
				} else if(getDataType() != null && getDataType().equals("C")) {
					sbQuery1.append(" and close_job_status = true ");
				}
				if(getAppliSourceType() != null && !getAppliSourceType().equals("")) {
					sbQuery.append(" and recruitment_id in (select recruitment_id from candidate_application_details where source_type="+uF.parseToInt(getAppliSourceType())+" ");
					if(getAppliSourceName()!=null && getAppliSourceName().length > 0 && !getAppliSourceName()[0].trim().equals("")) {
						if(uF.parseToInt(getAppliSourceType()) == SOURCE_HR || uF.parseToInt(getAppliSourceType()) == SOURCE_RECRUITER) {
							sbQuery.append(" and added_by in ("+StringUtils.join(getAppliSourceName(), ",")+")");
						} else if(uF.parseToInt(getAppliSourceType()) == SOURCE_REFERENCE) {
							sbQuery.append(" and source_or_ref_code in ("+StringUtils.join(getAppliSourceName(), ",")+")");
						}
					}
					sbQuery.append(") ");
				} else if(getAppliSourceName()!=null && getAppliSourceName().length > 0 && !getAppliSourceName()[0].trim().equals("")) {
					sbQuery.append(" and recruitment_id in (select recruitment_id from candidate_application_details where candidate_id>0 ");
					sbQuery.append(" and (added_by in ("+StringUtils.join(getAppliSourceName(), ",")+") ");
					sbQuery.append(" or source_or_ref_code in ("+StringUtils.join(getAppliSourceName(), ",")+")) ");
					sbQuery.append(") ");
				}
				
				if(uF.parseToInt(getF_org()) > 0) {
					sbQuery1.append(" and org_id = "+uF.parseToInt(getF_org())+" ");
		        }
		        if(getLocation()!=null && getLocation().length > 0 && !getLocation()[0].trim().equals("")) {
		        	sbQuery1.append(" and wlocation in ("+StringUtils.join(getLocation(), ",")+") ");
				}
		        if(getDesignation()!=null && getDesignation().length > 0 && !getDesignation()[0].trim().equals("")) {
		        	sbQuery1.append(" and designation_id in ("+StringUtils.join(getDesignation(), ",")+") ");
				}
		    sbQuery1.append(")a LEFT JOIN (select distinct(recruitment_id) from candidate_application_details) cpd on(cpd.recruitment_id=a.recruitment_id) " +
				"order by close_job_status,cpd.recruitment_id desc) a LEFT JOIN (select distinct(recruitment_id) from panel_interview_details " +
				"where panel_emp_id is not null) cpd on(cpd.recruitment_id=a.recruitment_id) " +
				"order by close_job_status,r_recruitment_id desc,cpd.recruitment_id desc");
			int intOffset = uF.parseToInt(minLimit);
			sbQuery1.append(" limit 10 offset "+intOffset+"");
//			sbQuery.append(" order by recruitment_id desc close_job_status,job_approval_date desc");
//			System.out.println("sbQuery1 ===> " + sbQuery1.toString());
			pst=con.prepareStatement(sbQuery1.toString());
			rst=pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
//			System.out.println("pst  ==== >>>> "+pst);
			StringBuilder recID1 = new StringBuilder();
			StringBuilder recID2 = new StringBuilder();
			StringBuilder recID3 = new StringBuilder();
			StringBuilder recID4 = new StringBuilder();
			StringBuilder recID5 = new StringBuilder();
			StringBuilder allRecIds = null;
			while(rst.next()){
				if((rst.getString("p_recruitment_id") == null || rst.getString("p_recruitment_id").equals("")) && (rst.getString("r_recruitment_id") == null || rst.getString("r_recruitment_id").equals(""))  && uF.parseToBoolean(rst.getString("close_job_status")) == false){
					recID1.append(rst.getString("recruitment_id").trim());
					recID1.append(",");
				} else if(rst.getString("p_recruitment_id") != null && rst.getString("r_recruitment_id") == null && uF.parseToBoolean(rst.getString("close_job_status")) == false){
					recID2.append(rst.getString("recruitment_id").trim());
					recID2.append(",");
				} else if(rst.getString("p_recruitment_id") == null && rst.getString("r_recruitment_id") != null && uF.parseToBoolean(rst.getString("close_job_status")) == false){
					recID3.append(rst.getString("recruitment_id").trim());
					recID3.append(",");
				} else if(rst.getString("p_recruitment_id") != null && rst.getString("r_recruitment_id") != null && uF.parseToBoolean(rst.getString("close_job_status")) == false){
					recID4.append(rst.getString("recruitment_id").trim());
					recID4.append(",");
				} else if(uF.parseToBoolean(rst.getString("close_job_status")) == true) {
					recID5.append(rst.getString("recruitment_id").trim());
					recID5.append(",");
				}
				if(allRecIds == null) {
					allRecIds = new StringBuilder();
					allRecIds.append(rst.getString("recruitment_id"));
				} else {
					allRecIds.append(","+rst.getString("recruitment_id"));
				}
			}
			rst.close();
			pst.close();
			
			if(allRecIds == null) {
				allRecIds = new StringBuilder();
			}
			
			if(allRecIds != null && !allRecIds.equals("")) {
				setCurrRecruitId(allRecIds.toString());
			}
			
			StringBuilder appendallID = new StringBuilder();
			appendallID.append(recID1);//0
			appendallID.append(recID2);//1
			appendallID.append(recID3);//2
			appendallID.append(recID4);//3
			appendallID.append(recID5);//4
			
			//System.out.println("appendallID ===="+appendallID.toString());
			
			recruitmentIDList = Arrays.asList(appendallID.toString().split(","));
			//System.out.println("recruitmentIDList ===="+recruitmentIDList);
			
			if(getCurrRecruitId() !=null && !getCurrRecruitId().equals("")) {
		    	Map<String,String> hmCandAccepted=new HashMap<String, String>();
		    	Map<String,String> hmCandRejected=new HashMap<String, String>();
		    	Map<String,String> hmCandRequired=new HashMap<String, String>();
		    	Map<String,String> hmCandOfferd=new HashMap<String, String>();
		    	Map<String,String> hmToday=new HashMap<String, String>();
		    	Map<String,String> hmDayafterTommorow=new HashMap<String, String>();
		    	
		    	int  candAccepted = 0;
		    	int candRejected = 0;
		    	int candOffred = 0;
		    	int cnt_today = 0;
		    	int cnt_dayaftertommorow = 0;
		    	
		    	Calendar cal=GregorianCalendar.getInstance();
		        SimpleDateFormat dateFormat=new  SimpleDateFormat(DBDATE);
		        String strCurrentDate=dateFormat.format(cal.getTime());
		        Date currentday=cal.getTime();
		        cal.add(Calendar.DATE,2);
		        Date dayAfterTommorow=cal.getTime();
		        
		        StringBuilder sbQue = new StringBuilder();
		        sbQue.append("select candidate_joining_date,no_position,recruitment_id,candidate_id,candidate_status,candidate_final_status from " +
	        		" candidate_application_details cad join recruitment_details rd using(recruitment_id) where rd.recruitment_id > 0 and " +
	        		" cad.recruitment_id in ("+getCurrRecruitId()+") ");
		        if(getAppliSourceType() != null && !getAppliSourceType().equals("")) {
					sbQue.append(" and cad.source_type = "+uF.parseToInt(getAppliSourceType())+" ");
					if(getAppliSourceName()!=null && getAppliSourceName().length > 0 && !getAppliSourceName()[0].trim().equals("")) {
						if(uF.parseToInt(getAppliSourceType()) == SOURCE_HR || uF.parseToInt(getAppliSourceType()) == SOURCE_RECRUITER) {
							sbQue.append(" and cad.added_by in ("+StringUtils.join(getAppliSourceName(), ",")+")");
						} else if(uF.parseToInt(getAppliSourceType()) == SOURCE_REFERENCE) {
							sbQue.append(" and cad.source_or_ref_code in ("+StringUtils.join(getAppliSourceName(), ",")+")");
						}
					}
				} else if(getAppliSourceName()!=null && getAppliSourceName().length > 0 && !getAppliSourceName()[0].trim().equals("")) {
					sbQue.append(" and (cad.added_by in ("+StringUtils.join(getAppliSourceName(), ",")+")");
					sbQue.append(" or cad.source_or_ref_code in ("+StringUtils.join(getAppliSourceName(), ",")+")) ");
				}
		        if(strUserType != null && strUserType.equals(RECRUITER)) {
		        	sbQue.append(" and cad.added_by = "+uF.parseToInt(strSessionEmpId)+" ");
		        }
	//	        System.out.println("sbQue ===> " + sbQue.toString());
		    	pst=con.prepareStatement(sbQue.toString());
		    	rst=pst.executeQuery();
	//	    	System.out.println("new Date ===> " + new Date());
		    	while(rst.next()) {
		    		if(rst.getString("candidate_final_status").equals("1") && rst.getString("candidate_status").equals("1")) {
		    			if(hmCandAccepted.keySet().contains(rst.getString("recruitment_id")))
		    				candAccepted= uF.parseToInt(hmCandAccepted.get(rst.getString("recruitment_id"))) ;
		    			else candAccepted=0;
		    				candAccepted++;
		                
		    			hmCandAccepted.put(rst.getString("recruitment_id"),""+candAccepted);
		                 
		    		}else if(rst.getString("candidate_final_status").equals("1") && rst.getString("candidate_status").equals("-1") ){
		    
		    			if(hmCandRejected.keySet().contains(rst.getString("recruitment_id")))
		    			candRejected=uF.parseToInt(hmCandRejected.get(rst.getString("recruitment_id"))) ;
		    			else candRejected=0;
		    			candRejected++;
		    			
		    			hmCandRejected.put(rst.getString("recruitment_id"),""+candRejected);
		    		}else if(rst.getString("candidate_final_status").equals("1") && rst.getString("candidate_status").equals("0") ){
		    
		    			if(hmCandOfferd.keySet().contains(rst.getString("recruitment_id")))
		    				candOffred=uF.parseToInt(hmCandOfferd.get(rst.getString("recruitment_id"))) ;
		    			else candOffred=0;
		    			candOffred++;
		    			
		    			hmCandOfferd.put(rst.getString("recruitment_id"),""+candOffred);
		    		}
		    		
		    		hmCandRequired.put(rst.getString("recruitment_id"),""+rst.getInt("no_position"));
		
	//	    		System.out.println("strCurrentDate ===> "+strCurrentDate);
	//	    		System.out.println("rst.getString(candidate_joining_date) ===> "+rst.getString("candidate_joining_date"));
		    		if(strCurrentDate.equals(rst.getString("candidate_joining_date"))) {
		    			if(hmToday.keySet().contains(rst.getString("recruitment_id"))) {
	//	    				System.out.println("cnt_today ===> "+uF.parseToInt(hmToday.get(rst.getString("recruitment_id"))));
	//	    				System.out.println("cnt_today ===> "+uF.parseToInt(hmToday.get(rst.getString("recruitment_id"))));
		    				cnt_today=uF.parseToInt(hmToday.get(rst.getString("recruitment_id")));
		    			} else {
		    				cnt_today=0;
	    				}
		    			cnt_today++;
		    			hmToday.put(rst.getString("recruitment_id"),""+cnt_today);
		    		} else {
		    			
		    			if(rst.getDate("candidate_joining_date")!=null) {
		    		
		    				if(uF.isDateBetween(currentday, dayAfterTommorow, rst.getDate("candidate_joining_date"))) {
		    					if(hmDayafterTommorow.keySet().contains(rst.getString("recruitment_id"))) {	
		    						cnt_dayaftertommorow=uF.parseToInt(hmDayafterTommorow.get(rst.getString("recruitment_id"))) ;		    			
		    					} else {
		    						cnt_dayaftertommorow=0;	    				
		    					}
		    					cnt_dayaftertommorow++;
		    					hmDayafterTommorow.put(rst.getString("recruitment_id"), ""+cnt_dayaftertommorow);	    					
		    				} 
		    				}	    			
		    		}	    		
		    	}
		    	rst.close();
				pst.close();
	
		    	
			// preparing  interview Status **************
			
	    	Map hmScheduledCandidate = new HashMap();
	    	Map hmUnderProcessCandidate = new HashMap();	
		    
	    	sbQue = new StringBuilder();
	    	sbQue.append("select cip.candidate_id, panel_round_id, cip.recruitment_id, status from candidate_interview_panel cip join " +
	    		"candidate_application_details cad on(cip.candidate_id=cad.candidate_id) where candidate_final_status=0 and is_selected = 0 " +
	    		" and cad.recruitment_id in ("+getCurrRecruitId()+") ");
	    	if(getAppliSourceType() != null && !getAppliSourceType().equals("")) {
				sbQue.append(" and cad.source_type = "+uF.parseToInt(getAppliSourceType())+" ");
				if(getAppliSourceName()!=null && getAppliSourceName().length > 0 && !getAppliSourceName()[0].trim().equals("")) {
					if(uF.parseToInt(getAppliSourceType()) == SOURCE_HR || uF.parseToInt(getAppliSourceType()) == SOURCE_RECRUITER) {
						sbQue.append(" and cad.added_by in ("+StringUtils.join(getAppliSourceName(), ",")+")");
					} else if(uF.parseToInt(getAppliSourceType()) == SOURCE_REFERENCE) {
						sbQue.append(" and cad.source_or_ref_code in ("+StringUtils.join(getAppliSourceName(), ",")+")");
					}
				}
			} else if(getAppliSourceName()!=null && getAppliSourceName().length > 0 && !getAppliSourceName()[0].trim().equals("")) {
				sbQue.append(" and (cad.added_by in ("+StringUtils.join(getAppliSourceName(), ",")+")");
				sbQue.append(" or cad.source_or_ref_code in ("+StringUtils.join(getAppliSourceName(), ",")+")) ");
			}
	    	if(strUserType != null && strUserType.equals(RECRUITER)) {
	    		sbQue.append(" and cad.added_by = "+uF.parseToInt(strSessionEmpId)+" ");
	    	}
	    	sbQue.append(" group by cip.candidate_id, panel_round_id, cip.recruitment_id, status order by cip.candidate_id, status");
		    pst = con.prepareStatement(sbQue.toString());
		    rst= pst.executeQuery();
	//	    System.out.println("new Date ===> " + new Date());
		    int nCount = 0;	    
		    String strCandidateNew = null;
		    String strCandidateOld = null;
		    
		    List alCandidateIdUP = new ArrayList();
		    List alCandidateIdS = new ArrayList();
	
		    while(rst.next()){
		    	strCandidateNew = rst.getString("recruitment_id");
		    	if(strCandidateNew!=null && !strCandidateNew.equalsIgnoreCase(strCandidateOld)){
		    		alCandidateIdS = new ArrayList();
		    		alCandidateIdUP = new ArrayList();
		    	}
		    	
		    	if(uF.parseToInt(rst.getString("status"))==0){
		    		
		    		if(!alCandidateIdS.contains(rst.getString("candidate_id"))){
		    			alCandidateIdS.add(rst.getString("candidate_id"));
		    		}
		    		hmScheduledCandidate.put(rst.getString("recruitment_id"), alCandidateIdS);
		    	}
		    	
		    	if(uF.parseToInt(rst.getString("status"))==1){
		    		
		    		if(alCandidateIdS.contains(rst.getString("candidate_id"))){
		    			alCandidateIdS.remove(rst.getString("candidate_id"));
		    		}
		    		
		    		if(!alCandidateIdUP.contains(rst.getString("candidate_id"))){
		    			alCandidateIdUP.add(rst.getString("candidate_id"));
		    		}	    		
		    		hmUnderProcessCandidate.put(rst.getString("recruitment_id"), alCandidateIdUP);
		    	}
		    	strCandidateOld = strCandidateNew;
		    }
		    rst.close();
			pst.close();
			
		    
		    Map<String,String> hmScheduling = new HashMap<String,String>();
		    sbQue = new StringBuilder();
		    sbQue.append("select recruitment_id,count(*) as count from candidate_application_details cad where application_status=2 " +
		    	" and candidate_id not in (select candidate_id from candidate_interview_panel) " +
		    	" and cad.recruitment_id in ("+getCurrRecruitId()+") ");
	    	if(getAppliSourceType() != null && !getAppliSourceType().equals("")) {
				sbQue.append(" and cad.source_type = "+uF.parseToInt(getAppliSourceType())+" ");
				if(getAppliSourceName()!=null && getAppliSourceName().length > 0 && !getAppliSourceName()[0].trim().equals("")) {
					if(uF.parseToInt(getAppliSourceType()) == SOURCE_HR || uF.parseToInt(getAppliSourceType()) == SOURCE_RECRUITER) {
						sbQue.append(" and cad.added_by in ("+StringUtils.join(getAppliSourceName(), ",")+")");
					} else if(uF.parseToInt(getAppliSourceType()) == SOURCE_REFERENCE) {
						sbQue.append(" and cad.source_or_ref_code in ("+StringUtils.join(getAppliSourceName(), ",")+")");
					}
				}
			} else if(getAppliSourceName()!=null && getAppliSourceName().length > 0 && !getAppliSourceName()[0].trim().equals("")) {
				sbQue.append(" and (cad.added_by in ("+StringUtils.join(getAppliSourceName(), ",")+")");
				sbQue.append(" or cad.source_or_ref_code in ("+StringUtils.join(getAppliSourceName(), ",")+")) ");
			}
		    if(strUserType != null && strUserType.equals(RECRUITER)) {
		    	sbQue.append(" and added_by = "+uF.parseToInt(strSessionEmpId)+" ");
		    }
		    sbQue.append(" group by recruitment_id");
		    pst=con.prepareStatement(sbQue.toString());
		    rst=pst.executeQuery();
	//	    System.out.println("new Date ===> " + new Date());
		    while(rst.next()) {
		    	hmScheduling.put(rst.getString("recruitment_id"), rst.getString("count"));
		    }
		    rst.close();
			pst.close();
			
	        ///Preparing application statuss***************
		
			Map<String ,String> applyMp=new HashMap<String,String>();
	      	
			Map<String, String> hmCandiRejectFromRound = new HashMap<String, String>();
			sbQue = new StringBuilder();
			sbQue.append("select * from candidate_interview_panel where status=-1 and recruitment_id in ("+getCurrRecruitId()+") ");
			if(getAppliSourceType() != null && !getAppliSourceType().equals("")) {
				sbQue.append(" and candidate_id in (select candidate_id from candidate_application_details where source_type="+uF.parseToInt(getAppliSourceType())+" ");
				if(getAppliSourceName()!=null && getAppliSourceName().length > 0 && !getAppliSourceName()[0].trim().equals("")) {
					if(uF.parseToInt(getAppliSourceType()) == SOURCE_HR || uF.parseToInt(getAppliSourceType()) == SOURCE_RECRUITER) {
						sbQue.append(" and added_by in ("+StringUtils.join(getAppliSourceName(), ",")+")");
					} else if(uF.parseToInt(getAppliSourceType()) == SOURCE_REFERENCE) {
						sbQue.append(" and source_or_ref_code in ("+StringUtils.join(getAppliSourceName(), ",")+")");
					}
				}
				sbQue.append(") ");
			} else if(getAppliSourceName()!=null && getAppliSourceName().length > 0 && !getAppliSourceName()[0].trim().equals("")) {
				sbQue.append(" and candidate_id in (select candidate_id from candidate_application_details where candidate_id>0 ");
				sbQue.append(" and (added_by in ("+StringUtils.join(getAppliSourceName(), ",")+")");
				sbQue.append(" or source_or_ref_code in ("+StringUtils.join(getAppliSourceName(), ",")+")) ");
				sbQue.append(") ");
			}
			pst = con.prepareStatement(sbQue.toString());
			rst = pst.executeQuery();
	//		System.out.println("new Date ===> " + new Date());
			while (rst.next()) {
				hmCandiRejectFromRound.put(rst.getString("recruitment_id")+"_"+rst.getString("candidate_id"), rst.getString("status"));
			}
			rst.close();
			pst.close();
			
			Map<String, String> hmClearRoundCnt = new HashMap<String, String>();
			sbQue = new StringBuilder();
			sbQue.append("select count (distinct(panel_round_id)) as count,recruitment_id,candidate_id from candidate_interview_panel where status = 1 " +
				" and recruitment_id in ("+getCurrRecruitId()+") ");
			if(getAppliSourceType() != null && !getAppliSourceType().equals("")) {
				sbQue.append(" and candidate_id in (select candidate_id from candidate_application_details where source_type="+uF.parseToInt(getAppliSourceType())+" ");
				if(getAppliSourceName()!=null && getAppliSourceName().length > 0 && !getAppliSourceName()[0].trim().equals("")) {
					if(uF.parseToInt(getAppliSourceType()) == SOURCE_HR || uF.parseToInt(getAppliSourceType()) == SOURCE_RECRUITER) {
						sbQue.append(" and added_by in ("+StringUtils.join(getAppliSourceName(), ",")+")");
					} else if(uF.parseToInt(getAppliSourceType()) == SOURCE_REFERENCE) {
						sbQue.append(" and source_or_ref_code in ("+StringUtils.join(getAppliSourceName(), ",")+")");
					}
				}
				sbQue.append(") ");
			} else if(getAppliSourceName()!=null && getAppliSourceName().length > 0 && !getAppliSourceName()[0].trim().equals("")) {
				sbQue.append(" and candidate_id in (select candidate_id from candidate_application_details where candidate_id>0 ");
				sbQue.append(" and (added_by in ("+StringUtils.join(getAppliSourceName(), ",")+")");
				sbQue.append(" or source_or_ref_code in ("+StringUtils.join(getAppliSourceName(), ",")+")) ");
				sbQue.append(") ");
			}
			sbQue.append(" group by recruitment_id,candidate_id");
			pst = con.prepareStatement(sbQue.toString());
			rst = pst.executeQuery();
	//		System.out.println("new Date ===> " + new Date());
			while (rst.next()) {
				hmClearRoundCnt.put(rst.getString("recruitment_id")+"_"+rst.getString("candidate_id"), rst.getString("count"));
			}
			rst.close();
			pst.close();
			
	//		System.out.println("hmClearRoundCnt ===> "+hmClearRoundCnt);
			
			Map<String, String> hmRoundCnt = new HashMap<String, String>();
			sbQue = new StringBuilder();
			sbQue.append("select count (distinct(round_id))as count,recruitment_id from panel_interview_details where recruitment_id>0 " +
				" and recruitment_id in ("+getCurrRecruitId()+") ");
			if(getAppliSourceType() != null && !getAppliSourceType().equals("")) {
				sbQue.append(" and recruitment_id in (select recruitment_id from candidate_application_details where source_type="+uF.parseToInt(getAppliSourceType())+" ");
				if(getAppliSourceName()!=null && getAppliSourceName().length > 0 && !getAppliSourceName()[0].trim().equals("")) {
					if(uF.parseToInt(getAppliSourceType()) == SOURCE_HR || uF.parseToInt(getAppliSourceType()) == SOURCE_RECRUITER) {
						sbQue.append(" and added_by in ("+StringUtils.join(getAppliSourceName(), ",")+")");
					} else if(uF.parseToInt(getAppliSourceType()) == SOURCE_REFERENCE) {
						sbQue.append(" and source_or_ref_code in ("+StringUtils.join(getAppliSourceName(), ",")+")");
					}
				}
				sbQue.append(") ");
			} else if(getAppliSourceName()!=null && getAppliSourceName().length > 0 && !getAppliSourceName()[0].trim().equals("")) {
				sbQue.append(" and recruitment_id in (select recruitment_id from candidate_application_details where candidate_id>0 ");
				sbQue.append(" and (added_by in ("+StringUtils.join(getAppliSourceName(), ",")+")");
				sbQue.append(" or source_or_ref_code in ("+StringUtils.join(getAppliSourceName(), ",")+")) ");
				sbQue.append(") ");
			}
			sbQue.append(" group by recruitment_id");
			pst = con.prepareStatement(sbQue.toString());
			rst = pst.executeQuery();
	//		System.out.println("new Date ===> " + new Date());
			while (rst.next()) {
				hmRoundCnt.put(rst.getString("recruitment_id"), rst.getString("count"));
			}
			rst.close();
			pst.close();
			
			sbQue = new StringBuilder();
			sbQue.append("select  count(*) as count,recruitment_id, application_status, candidate_final_status, candidate_id" +
				" from candidate_application_details cad where candidate_id > 0 and cad.recruitment_id in ("+getCurrRecruitId()+") ");
			if(getAppliSourceType() != null && !getAppliSourceType().equals("")) {
				sbQue.append(" and cad.source_type = "+uF.parseToInt(getAppliSourceType())+" ");
				if(getAppliSourceName()!=null && getAppliSourceName().length > 0 && !getAppliSourceName()[0].trim().equals("")) {
					if(uF.parseToInt(getAppliSourceType()) == SOURCE_HR || uF.parseToInt(getAppliSourceType()) == SOURCE_RECRUITER) {
						sbQue.append(" and cad.added_by in ("+StringUtils.join(getAppliSourceName(), ",")+")");
					} else if(uF.parseToInt(getAppliSourceType()) == SOURCE_REFERENCE) {
						sbQue.append(" and cad.source_or_ref_code in ("+StringUtils.join(getAppliSourceName(), ",")+")");
					}
				}
			} else if(getAppliSourceName()!=null && getAppliSourceName().length > 0 && !getAppliSourceName()[0].trim().equals("")) {
				sbQue.append(" and (cad.added_by in ("+StringUtils.join(getAppliSourceName(), ",")+")");
				sbQue.append(" or cad.source_or_ref_code in ("+StringUtils.join(getAppliSourceName(), ",")+")) ");
			}
			if(strUserType != null && strUserType.equals(RECRUITER)) {
				sbQue.append(" and added_by = "+uF.parseToInt(strSessionEmpId)+" ");
			}
			sbQue.append(" group by recruitment_id, application_status, candidate_final_status, candidate_id");
			pst = con.prepareStatement(sbQue.toString());				
			int dblTotalApplication = 0;
			rst = pst.executeQuery();
	//		System.out.println("new Date ===> " + new Date());
//			System.out.println("pst ===> "+pst);
			while (rst.next()) {
				if(applyMp.get(rst.getString("recruitment_id"))!=null)
				dblTotalApplication=uF.parseToInt(applyMp.get(rst.getString("recruitment_id")));
				else dblTotalApplication=0;
				
				dblTotalApplication+=rst.getInt("count");		            	 
	            applyMp.put(rst.getString("recruitment_id"),String.valueOf(dblTotalApplication));
							
			}
			rst.close();
			pst.close();
			
			
			Map<String, String> hmSelectCount = new LinkedHashMap<String, String>();
			Map<String, String> hmFinalCount = new LinkedHashMap<String, String>();
			
			sbQue = new StringBuilder();
			sbQue.append("select cad.recruitment_id,emp_fname,emp_lname,cad.job_code,emp_per_id,cad.candidate_final_status," +
				"emp_image from candidate_personal_details cpd, candidate_application_details cad where cpd.emp_per_id = cad.candidate_id " +
				" and cad.application_status=2 and not cad.candidate_final_status=-1 and cad.recruitment_id in ("+getCurrRecruitId()+") ");
				if(getAppliSourceType() != null && !getAppliSourceType().equals("")) {
					sbQue.append(" and cad.source_type = "+uF.parseToInt(getAppliSourceType())+" ");
					if(getAppliSourceName()!=null && getAppliSourceName().length > 0 && !getAppliSourceName()[0].trim().equals("")) {
						if(uF.parseToInt(getAppliSourceType()) == SOURCE_HR || uF.parseToInt(getAppliSourceType()) == SOURCE_RECRUITER) {
							sbQue.append(" and cad.added_by in ("+StringUtils.join(getAppliSourceName(), ",")+")");
						} else if(uF.parseToInt(getAppliSourceType()) == SOURCE_REFERENCE) {
							sbQue.append(" and cad.source_or_ref_code in ("+StringUtils.join(getAppliSourceName(), ",")+")");
						}
					}
				} else if(getAppliSourceName()!=null && getAppliSourceName().length > 0 && !getAppliSourceName()[0].trim().equals("")) {
					sbQue.append(" and (cad.added_by in ("+StringUtils.join(getAppliSourceName(), ",")+")");
					sbQue.append(" or cad.source_or_ref_code in ("+StringUtils.join(getAppliSourceName(), ",")+")) ");
				}
				pst = con.prepareStatement(sbQue.toString());
//				System.out.println("pst ===>> " + pst);
				rst = pst.executeQuery();
	//			System.out.println("new Date ===> " + new Date());
				int selectCnt=0, finalCnt=0;
				while (rst.next()) {
		
					if (rst.getInt("candidate_final_status") == 0 && (hmCandiRejectFromRound == null || 
						hmCandiRejectFromRound.get(rst.getString("recruitment_id")+"_"+rst.getString("emp_per_id")) == null || 
						!hmCandiRejectFromRound.get(rst.getString("recruitment_id")+"_"+rst.getString("emp_per_id")).equals("-1"))
						&& hmClearRoundCnt != null && hmRoundCnt != null &&
						hmClearRoundCnt.get(rst.getString("recruitment_id")+"_"+rst.getString("emp_per_id")) != null && hmRoundCnt.get(rst.getString("recruitment_id")) != null && 
						hmClearRoundCnt.get(rst.getString("recruitment_id")+"_"+rst.getString("emp_per_id")).equals(hmRoundCnt.get(rst.getString("recruitment_id")))){
						finalCnt = uF.parseToInt(hmFinalCount.get(rst.getString("recruitment_id")));
						finalCnt++;
						hmFinalCount.put(rst.getString("recruitment_id"), String.valueOf(finalCnt));
						
					} else if (rst.getInt("candidate_final_status") == 0 && (hmCandiRejectFromRound == null || 
						hmCandiRejectFromRound.get(rst.getString("recruitment_id")+"_"+rst.getString("emp_per_id")) == null || !hmCandiRejectFromRound.get(rst.getString("recruitment_id")+"_"+rst.getString("emp_per_id")).equals("-1"))){
						selectCnt = uF.parseToInt(hmSelectCount.get(rst.getString("recruitment_id")));
						selectCnt++;
						hmSelectCount.put(rst.getString("recruitment_id"), String.valueOf(selectCnt));
					}
				}
				rst.close();
				pst.close();
				
				Map<String, String> hmRejectCount = new LinkedHashMap<String, String>();
				sbQue = new StringBuilder();
				sbQue.append("select cad.recruitment_id,emp_fname,emp_lname,cad.job_code,emp_per_id, cad.candidate_final_status, emp_image, " +
					" cad.application_status from candidate_personal_details cpd,candidate_application_details cad where cpd.emp_per_id = cad.candidate_id " +
					" and cad.recruitment_id in ("+getCurrRecruitId()+") ");
				if(getAppliSourceType() != null && !getAppliSourceType().equals("")) {
					sbQue.append(" and cad.source_type = "+uF.parseToInt(getAppliSourceType())+" ");
					if(getAppliSourceName()!=null && getAppliSourceName().length > 0 && !getAppliSourceName()[0].trim().equals("")) {
						if(uF.parseToInt(getAppliSourceType()) == SOURCE_HR || uF.parseToInt(getAppliSourceType()) == SOURCE_RECRUITER) {
							sbQue.append(" and cad.added_by in ("+StringUtils.join(getAppliSourceName(), ",")+")");
						} else if(uF.parseToInt(getAppliSourceType()) == SOURCE_REFERENCE) {
							sbQue.append(" and cad.source_or_ref_code in ("+StringUtils.join(getAppliSourceName(), ",")+")");
						}
					}
				} else if(getAppliSourceName()!=null && getAppliSourceName().length > 0 && !getAppliSourceName()[0].trim().equals("")) {
					sbQue.append(" and (cad.added_by in ("+StringUtils.join(getAppliSourceName(), ",")+")");
					sbQue.append(" or cad.source_or_ref_code in ("+StringUtils.join(getAppliSourceName(), ",")+")) ");
				}
				pst = con.prepareStatement(sbQue.toString());
				rst = pst.executeQuery();
	//			System.out.println("new Date ===> " + new Date());
				int rejectCnt=0;
				while (rst.next()) {
					if(rst.getString("application_status").equals("-1") || rst.getString("candidate_final_status").equals("-1")
						|| (hmCandiRejectFromRound != null && hmCandiRejectFromRound.get(rst.getString("recruitment_id")+"_"+rst.getString("emp_per_id")) != null && hmCandiRejectFromRound.get(rst.getString("recruitment_id")+"_"+rst.getString("emp_per_id")).equals("-1"))){
					
					rejectCnt = uF.parseToInt(hmRejectCount.get(rst.getString("recruitment_id")));
					rejectCnt++;
					hmRejectCount.put(rst.getString("recruitment_id"), String.valueOf(rejectCnt));
					
					}
				}
				rst.close();
				pst.close();
		
				request.setAttribute("hmSelectCount", hmSelectCount);
				request.setAttribute("hmFinalCount", hmFinalCount);
				request.setAttribute("hmRejectCount", hmRejectCount);
			
				Map<String, List<String>> hmpanelIDS = new HashMap<String, List<String>>();
				
				List<String> panelEmpIDList = new ArrayList<String>();
				sbQue = new StringBuilder();
				sbQue.append("select recruitment_id, round_id, panel_emp_id from panel_interview_details where recruitment_id>0  " +
					" and recruitment_id in ("+getCurrRecruitId()+") ");
				if(getAppliSourceType() != null && !getAppliSourceType().equals("")) {
					sbQue.append(" and recruitment_id in (select recruitment_id from candidate_application_details where source_type="+uF.parseToInt(getAppliSourceType())+" ");
					if(getAppliSourceName()!=null && getAppliSourceName().length > 0 && !getAppliSourceName()[0].trim().equals("")) {
						if(uF.parseToInt(getAppliSourceType()) == SOURCE_HR || uF.parseToInt(getAppliSourceType()) == SOURCE_RECRUITER) {
							sbQue.append(" and added_by in ("+StringUtils.join(getAppliSourceName(), ",")+")");
						} else if(uF.parseToInt(getAppliSourceType()) == SOURCE_REFERENCE) {
							sbQue.append(" and source_or_ref_code in ("+StringUtils.join(getAppliSourceName(), ",")+")");
						}
					}
					sbQue.append(") ");
				} else if(getAppliSourceName()!=null && getAppliSourceName().length > 0 && !getAppliSourceName()[0].trim().equals("")) {
					sbQue.append(" and recruitment_id in (select recruitment_id from candidate_application_details where candidate_id>0 ");
					sbQue.append(" and (added_by in ("+StringUtils.join(getAppliSourceName(), ",")+")");
					sbQue.append(" or source_or_ref_code in ("+StringUtils.join(getAppliSourceName(), ",")+")) ");
					sbQue.append(") ");
				}
				pst = con.prepareStatement(sbQue.toString());
				rst = pst.executeQuery();
	//			System.out.println("new Date ===> " + new Date());
				while (rst.next()) {
					panelEmpIDList = hmpanelIDS.get(rst.getString("recruitment_id"));
					if(panelEmpIDList == null)panelEmpIDList = new ArrayList<String>();
					panelEmpIDList.add(rst.getString("panel_emp_id"));
					hmpanelIDS.put(rst.getString("recruitment_id"), panelEmpIDList);
				}
				rst.close();
				pst.close();
				
				Map<String, String> hmpanelName = new HashMap<String, String>();
				List<String> panelEmpIDList1 = new ArrayList<String>(); 
				sbQue = new StringBuilder();
				sbQue.append("select recruitment_id, round_id, panel_emp_id from panel_interview_details where recruitment_id>0  " +
					" and recruitment_id in ("+getCurrRecruitId()+") ");
				if(getAppliSourceType() != null && !getAppliSourceType().equals("")) {
					sbQue.append(" and recruitment_id in (select recruitment_id from candidate_application_details where source_type="+uF.parseToInt(getAppliSourceType())+" ");
					if(getAppliSourceName()!=null && getAppliSourceName().length > 0 && !getAppliSourceName()[0].trim().equals("")) {
						if(uF.parseToInt(getAppliSourceType()) == SOURCE_HR || uF.parseToInt(getAppliSourceType()) == SOURCE_RECRUITER) {
							sbQue.append(" and added_by in ("+StringUtils.join(getAppliSourceName(), ",")+")");
						} else if(uF.parseToInt(getAppliSourceType()) == SOURCE_REFERENCE) {
							sbQue.append(" and source_or_ref_code in ("+StringUtils.join(getAppliSourceName(), ",")+")");
						}
					}
					sbQue.append(") ");
				} else if(getAppliSourceName()!=null && getAppliSourceName().length > 0 && !getAppliSourceName()[0].trim().equals("")) {
					sbQue.append(" and recruitment_id in (select recruitment_id from candidate_application_details where candidate_id>0 ");
					sbQue.append(" and (added_by in ("+StringUtils.join(getAppliSourceName(), ",")+")");
					sbQue.append(" or source_or_ref_code in ("+StringUtils.join(getAppliSourceName(), ",")+")) ");
					sbQue.append(") ");
				}
				pst = con.prepareStatement(sbQue.toString());
				rst = pst.executeQuery();
	//			System.out.println("new Date ===> " + new Date());
				while (rst.next()) {
					panelEmpIDList1 = hmpanelIDS.get(rst.getString("recruitment_id"));
					String panelEmpNames = uF.showData(getAppendDataList(con, panelEmpIDList1), "");
					hmpanelName.put(rst.getString("recruitment_id"), panelEmpNames);
				}
				rst.close();
				pst.close();
				
				
				
	//			System.out.println("getF_org() ===>> " + getF_org());
				
	//			System.out.println("panel map===="+hmpanelName);
			List<String> alSkillId = null;
			if(getStrSearchJob()!=null && !getStrSearchJob().trim().equals("")){	
				pst = con.prepareStatement("select skill_id from skills_details where skill_name is not null " +
						"and skill_name != '' and upper(skill_name) like '%"+getStrSearchJob().trim().toUpperCase()+"%'");
				rst = pst.executeQuery();
				while(rst.next()){
					if(alSkillId == null){
						alSkillId = new ArrayList<String>();
						alSkillId.add(rst.getString("skill_id"));
					} else {
						alSkillId.add(rst.getString("skill_id"));
					}
				}
				
				rst.close();
				pst.close();
			}
			
			sbQuery = new StringBuilder();
			sbQuery.append("select designation_name,job_code,recruitment_id,custum_designation,close_job_status,no_position,priority_job_int,req_form_type,"+ 
	            " job_title from recruitment_details left join designation_details using(designation_id) where job_approval_status=1 and " +
	            " recruitment_id in ("+getCurrRecruitId()+") ");
			if(strUserType != null && (strUserType.equals(MANAGER) || strUserType.equals(RECRUITER))){
				sbQuery.append(" and (added_by = "+ uF.parseToInt(strSessionEmpId)  +" or hiring_manager like '%,"+strSessionEmpId+",%' )");
			}
			if(getDataType() != null && getDataType().equals("L")) {
				sbQuery.append(" and close_job_status = false ");
			} else if(getDataType() != null && getDataType().equals("C")) {
				sbQuery.append(" and close_job_status = true ");
			}
			if(getAppliSourceType() != null && !getAppliSourceType().equals("")) {
				sbQuery.append(" and recruitment_id in (select recruitment_id from candidate_application_details where source_type="+uF.parseToInt(getAppliSourceType())+" ");
				if(getAppliSourceName()!=null && getAppliSourceName().length > 0 && !getAppliSourceName()[0].trim().equals("")) {
					if(uF.parseToInt(getAppliSourceType()) == SOURCE_HR || uF.parseToInt(getAppliSourceType()) == SOURCE_RECRUITER) {
						sbQuery.append(" and added_by in ("+StringUtils.join(getAppliSourceName(), ",")+")");
					} else if(uF.parseToInt(getAppliSourceType()) == SOURCE_REFERENCE) {
						sbQuery.append(" and source_or_ref_code in ("+StringUtils.join(getAppliSourceName(), ",")+")");
					}
				}
				sbQuery.append(") ");
			} else if(getAppliSourceName()!=null && getAppliSourceName().length > 0 && !getAppliSourceName()[0].trim().equals("")) {
				sbQuery.append(" and recruitment_id in (select recruitment_id from candidate_application_details where candidate_id>0 ");
				sbQuery.append(" and (added_by in ("+StringUtils.join(getAppliSourceName(), ",")+")");
				sbQuery.append(" or source_or_ref_code in ("+StringUtils.join(getAppliSourceName(), ",")+")) ");
				sbQuery.append(") ");
			}
			if(uF.parseToInt(getF_org()) > 0) {
				sbQuery.append(" and org_id = "+uF.parseToInt(getF_org())+" ");
	        }
	        if(getLocation()!=null && getLocation().length > 0 && !getLocation()[0].trim().equals("")) {
	        	sbQuery.append(" and wlocation in ("+StringUtils.join(getLocation(), ",")+") ");
			}
	        if(getDesignation()!=null && getDesignation().length > 0 && !getDesignation()[0].trim().equals("")) {
	        	sbQuery.append(" and designation_id in ("+StringUtils.join(getDesignation(), ",")+") ");
			}
	        if(getStrSearchJob()!=null && !getStrSearchJob().trim().equals("")){
				sbQuery.append(" and (upper(job_code) like '%"+getStrSearchJob().trim().toUpperCase()+"%' or upper(job_title) like '%"+getStrSearchJob().trim().toUpperCase()+"%' ");
				if(alSkillId !=null && alSkillId.size() > 0){
					sbQuery.append(" or (");
	                for(int i=0; i<alSkillId.size(); i++){
	                    sbQuery.append(" skills like '%,"+alSkillId.get(i).trim()+",%'");
	                    if(i<alSkillId.size()-1){
	                        sbQuery.append(" OR "); 
	                    }
	                }
		            sbQuery.append(" ) ");
		            
		            sbQuery.append(" or (");
	                for(int i=0; i<alSkillId.size(); i++){
	                    sbQuery.append(" essential_skills like '%,"+alSkillId.get(i).trim()+",%'");
	                    if(i<alSkillId.size()-1){
	                        sbQuery.append(" OR "); 
	                    }
	                }
		            sbQuery.append(" ) ");
				}
				sbQuery.append(")");
			}
			sbQuery.append(" order by recruitment_id desc");
	//		sbQuery.append(" order by recruitment_id desc, close_job_status, job_approval_date desc");
			pst = con.prepareStatement(sbQuery.toString());
	//		System.out.println("pst  ==== >>>> "+pst);
			rst = pst.executeQuery();
			int count=0; 
	//		List<List<String>> aljobreport=new ArrayList<List<String> >();	
			Map<String, List<String>> hmJobReport = new HashMap<String, List<String>>();
			
			while(rst.next()) {
				List<String> job_code_info =new ArrayList<String>();
				job_code_info.add(rst.getString("recruitment_id"));
				job_code_info.add(rst.getString("job_code"));
				 
				job_code_info.add(hmpanelName.get(rst.getString("recruitment_id")));
				
				job_code_info.add(uF.showData(hmToday.get(rst.getString("recruitment_id")),"0"));
				job_code_info.add(uF.showData(hmDayafterTommorow.get(rst.getString("recruitment_id")),"0"));
				
				job_code_info.add(uF.showData(rst.getString("no_position"),"0"));
				job_code_info.add(uF.showData(hmCandAccepted.get(rst.getString("recruitment_id")),"0"));			
				job_code_info.add(uF.showData(hmCandRejected.get(rst.getString("recruitment_id")),"0"));
				job_code_info.add(uF.showData(hmCandOfferd.get(rst.getString("recruitment_id")),"0"));
				
				job_code_info.add(uF.showData(applyMp.get(rst.getString("recruitment_id")),"0"));
				job_code_info.add(uF.showData(hmSelectCount.get(rst.getString("recruitment_id")),"0"));
				job_code_info.add(uF.showData(hmFinalCount.get(rst.getString("recruitment_id")),"0"));
				job_code_info.add(uF.showData(hmRejectCount.get(rst.getString("recruitment_id")),"0")); //12
				
				job_code_info.add(uF.showData(hmScheduling.get(rst.getString("recruitment_id")),"0")); //13
				 
				List<String> alScheduled=(List<String>)hmScheduledCandidate.get(rst.getString("recruitment_id"));
				if(alScheduled==null)alScheduled=new ArrayList<String>();
				
				List<String> alUnderProcess=(List<String>) hmUnderProcessCandidate.get(rst.getString("recruitment_id"));
				if(alUnderProcess==null)alUnderProcess=new ArrayList<String>();
				
				job_code_info.add(""+alScheduled.size()); //14
				job_code_info.add(""+alUnderProcess.size());
			    job_code_info.add(uF.showData(rst.getString("designation_name"), "-")); //16
			    job_code_info.add(uF.parseToBoolean(rst.getString("close_job_status"))+""); //17
			    job_code_info.add(rst.getString("priority_job_int")); //18
			    boolean flag = getCandidateAddStatus(con, uF, rst.getString("recruitment_id"));
			    job_code_info.add(flag+""); //19
			    job_code_info.add(rst.getString("req_form_type")); //20
			    job_code_info.add(uF.showData(rst.getString("job_title"), "-")); //21
	//		    aljobreport.add(job_code_info);
		    	hmJobReport.put(rst.getString("recruitment_id"), job_code_info);
			}
			rst.close();
			pst.close();
			
			request.setAttribute("hmJobReport", hmJobReport);
	    }
		request.setAttribute("recruitmentIDList", recruitmentIDList);
//		request.setAttribute("job_code_info", aljobreport);
		
//		System.out.println("hmJobReport ==== >>>> "+hmJobReport);
		} catch (Exception e) {
				e.printStackTrace();
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	

	private void getSelectedFilter(UtilityFunctions uF) {
		Map<String,String> hmFilter=new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();
		
		alFilter.add("ORGANISATION");
		if(getF_org()!=null) {
			String strOrg="";
			int k=0;
			for(int i=0;organisationList!=null && i<organisationList.size();i++) {
//				for(int j=0;j<getF_sbu().length;j++) {
					if(getF_org().equals(organisationList.get(i).getOrgId())) {
						strOrg=organisationList.get(i).getOrgName();
					}
//				}
			}
			if(strOrg!=null && !strOrg.equals("")) {
				hmFilter.put("ORGANISATION", strOrg);
			} else {
				hmFilter.put("ORGANISATION", "All Organizations");
			}
		} else {
			hmFilter.put("ORGANISATION", "All Organizations");
		}
		
		
		alFilter.add("LOCATION");
		if(getLocation()!=null) {
			String strLocation="";
			int k=0;
			for(int i=0;workLocationList!=null && i<workLocationList.size();i++) {
				for(int j=0;j<getLocation().length;j++) {
					if(getLocation()[j].equals(workLocationList.get(i).getwLocationId())) {
						if(k==0) {
							strLocation=workLocationList.get(i).getwLocationName();
						} else {
							strLocation+=", "+workLocationList.get(i).getwLocationName();
						}
						k++;
					}
				}
			}
			if(strLocation!=null && !strLocation.equals("")) {
				hmFilter.put("LOCATION", strLocation);
			} else {
				hmFilter.put("LOCATION", "All Locations");
			}
		} else {
			hmFilter.put("LOCATION", "All Locations");
		}
		
		
		alFilter.add("DESIG");
		if(getDesignation()!=null) {
			String strDesig="";
			int k=0;
			for(int i=0;desigList!=null && i<desigList.size();i++) {
				for(int j=0;j<getDesignation().length;j++) {
					if(getDesignation()[j].equals(desigList.get(i).getDesigId())) {
						if(k==0) {
							strDesig = desigList.get(i).getDesigCodeName();
						} else {
							strDesig += ", " + desigList.get(i).getDesigCodeName();
						}
						k++;
					}
				}
			}
			if(strDesig!=null && !strDesig.equals("")) {
				hmFilter.put("DESIG", strDesig);
			} else {
				hmFilter.put("DESIG", "All Designations");
			}
		} else {
			hmFilter.put("DESIG", "All Designations");
		}
		
		alFilter.add("SOURCE");
		if(getF_org()!=null) {
			String strSourceType="";
			for(int i=0;sourceTypeList!=null && i<sourceTypeList.size();i++) {
				if(getAppliSourceType().equals(sourceTypeList.get(i).getSourceTypeId())) {
					strSourceType = sourceTypeList.get(i).getSourceTypeName();
				}
			}
			if(strSourceType!=null && !strSourceType.equals("")) {
				hmFilter.put("SOURCE", strSourceType);
			} else {
				hmFilter.put("SOURCE", "All");
			}
		} else {
			hmFilter.put("SOURCE", "All");
		}
		
		if(uF.parseToInt(getAppliSourceType()) != SOURCE_WEBSITE) {
			alFilter.add("SOURCE_NAME");
			if(getAppliSourceName()!=null) {
				String strSourceName="";
				int k=0;
				for(int i=0;sourceNameList!=null && i<sourceNameList.size();i++) {
					for(int j=0;j<getAppliSourceName().length;j++) {
						if(getAppliSourceName()[j].equals(sourceNameList.get(i).getSourceTypeId())) {
							if(k==0) {
								strSourceName = sourceNameList.get(i).getSourceTypeName();
							} else {
								strSourceName += ", " + sourceNameList.get(i).getSourceTypeName();
							}
							k++;
						}
					}
				}
				if(strSourceName!=null && !strSourceName.equals("")) {
					hmFilter.put("SOURCE_NAME", strSourceName);
				} else {
					hmFilter.put("SOURCE_NAME", "All");
				}
			} else {
				hmFilter.put("SOURCE_NAME", "All");
			}
		}
		
			
		String selectedFilter= CF.getSelectedFilter(CF, uF, alFilter, hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}
	


	private boolean getCandidateAddStatus(Connection con, UtilityFunctions uF, String recruitId) {
		PreparedStatement pst = null;
		ResultSet rst = null;
		boolean flag = false;
		try {
			pst = con.prepareStatement("select recruitment_id from candidate_application_details where recruitment_id = ?");
			pst.setInt(1, uF.parseToInt(recruitId));
			rst=pst.executeQuery();
//			System.out.println("pst ===> " + pst);
			while(rst.next()) {
				flag = true;
			}
			rst.close();
			pst.close();
			
		} catch(Exception e) {
			e.printStackTrace();
		}finally {
			if(rst != null) {
				try {
					rst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if(pst != null) {
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		} 
		return flag;
	}


	private String getAppendDataList(Connection con, List<String> strIDList) {
		StringBuilder sb = new StringBuilder();
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);

			for (int i =0; strIDList != null && i<strIDList.size(); i++) {

				if(strIDList.get(i)!=null && !strIDList.get(i).equals("")){
				 if(i==strIDList.size()-1){ 
					 sb.append(hmEmpName.get(strIDList.get(i).trim()));
				 } else {	
					sb.append(hmEmpName.get(strIDList.get(i).trim())+", ");
				 }
				}
		}
		return sb.toString();
	}

	
	private HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getRecruitId() {
		return recruitId;
	}

	public void setRecruitId(String recruitId) {
		this.recruitId = recruitId;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(String pageNumber) {
		this.pageNumber = pageNumber;
	}

	public String getMinLimit() {
		return minLimit;
	}

	public void setMinLimit(String minLimit) {
		this.minLimit = minLimit;
	}

	public String getF_org() {
		return f_org;
	}

	public void setF_org(String f_org) {
		this.f_org = f_org;
	}

	public String[] getLocation() {
		return location;
	}

	public void setLocation(String[] location) {
		this.location = location;
	}

	public String[] getDesignation() {
		return designation;
	}

	public void setDesignation(String[] designation) {
		this.designation = designation;
	}

	public List<FillOrganisation> getOrganisationList() {
		return organisationList;
	}

	public void setOrganisationList(List<FillOrganisation> organisationList) {
		this.organisationList = organisationList;
	}

	public List<FillDesig> getDesigList() {
		return desigList;
	}

	public void setDesigList(List<FillDesig> desigList) {
		this.desigList = desigList;
	}

	public List<FillWLocation> getWorkLocationList() {
		return workLocationList;
	}

	public void setWorkLocationList(List<FillWLocation> workLocationList) {
		this.workLocationList = workLocationList;
	}

	public String getStrSearchJob() {
		return strSearchJob;
	}

	public void setStrSearchJob(String strSearchJob) {
		this.strSearchJob = strSearchJob;
	}

	public List<FillSourceTypeAndName> getSourceTypeList() {
		return sourceTypeList;
	}

	public void setSourceTypeList(List<FillSourceTypeAndName> sourceTypeList) {
		this.sourceTypeList = sourceTypeList;
	}

	public List<FillSourceTypeAndName> getSourceNameList() {
		return sourceNameList;
	}

	public void setSourceNameList(List<FillSourceTypeAndName> sourceNameList) {
		this.sourceNameList = sourceNameList;
	}

	public String getAppliSourceType() {
		return appliSourceType;
	}

	public void setAppliSourceType(String appliSourceType) {
		this.appliSourceType = appliSourceType;
	}

	public String[] getAppliSourceName() {
		return appliSourceName;
	}

	public void setAppliSourceName(String[] appliSourceName) {
		this.appliSourceName = appliSourceName;
	}

	public String getCurrRecruitId() {
		return currRecruitId;
	}

	public void setCurrRecruitId(String currRecruitId) {
		this.currRecruitId = currRecruitId;
	}


	public String getFromPage() {
		return fromPage;
	}


	public void setFromPage(String fromPage) {
		this.fromPage = fromPage;
	}


	public String getStrLocation() {
		return strLocation;
	}


	public void setStrLocation(String strLocation) {
		this.strLocation = strLocation;
	}


	public String getStrDesignation() {
		return strDesignation;
	}


	public void setStrDesignation(String strDesignation) {
		this.strDesignation = strDesignation;
	}


	public String getStrAppliSourceName() {
		return strAppliSourceName;
	}


	public void setStrAppliSourceName(String strAppliSourceName) {
		this.strAppliSourceName = strAppliSourceName;
	}

	
	
}
