package com.konnect.jpms.recruitment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillMonth;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillSkills;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.select.FillYears;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class ShowAllCandidate extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(AddCandidateModePopup.class);
	
	String strUserType;
	String strSessionEmpId;
	HttpSession session;
	
	CommonFunctions CF;
	
	String f_org;
	String[] f_wlocation;
	String[] strExperience;
	String[] checkStatus_reportfilter;
	String recruitId;
	String currCTC;
	String expectedCTC;
	String noticePeriod;

	String minCurrCTC;
	String minExpectedCTC;
	String minNoticePeriod;
	String maxCurrCTC;
	String maxExpectedCTC;
	String maxNoticePeriod;
	List<FillMonth> monthList;
	List<FillYears> yearList;
	String strMonth;
	String strYear;

	List<FillOrganisation> organisationList;
	List<FillWLocation> workList;
	List<FillEducational> eduList;
	List<FillSkills> skillsList;

	String[] strMinEducation;
	String[] strSkills;
	
	public String execute() throws Exception {
		
		session = request.getSession();		
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if (CF==null) return LOGIN;
		
		request.setAttribute(PAGE, "/jsp/recruitment/ShowAllCandidate.jsp");
		request.setAttribute(TITLE, "Show All Candidate");
		request.setAttribute("DOC_RETRIVE_LOCATION", CF.getStrDocRetriveLocation());
		
//		System.out.println("getMailID()===> "+getMailID());
		UtilityFunctions uF = new UtilityFunctions();
		
		if(getF_org()==null){
			setF_org((String)session.getAttribute(ORGID));
		}
		
		organisationList=new FillOrganisation(request).fillOrganisation();
		
		if(getF_org()!=null){
			workList=new FillWLocation(request).fillWLocation(getF_org());
		} else {
			workList=new FillWLocation(request).fillWLocation();
		}
		eduList=new FillEducational(request).fillEducationalQual();
		skillsList=new FillSkills(request).fillSkillsWithIdOnOrg(uF.parseToInt(getF_org()));
		monthList = new FillMonth().fillMonth();
		
		viewEmployee(uF);
		return LOAD;
}

public String viewEmployee(UtilityFunctions uF) {

	Connection con = null;
	PreparedStatement pst = null;
	ResultSet rst = null;
	Database db = new Database();
	db.setRequest(request);
	
	try {
		con = db.makeConnection(con);
		Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
		boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
		
		int nCount = 0;

		StringBuilder sbQuery1 = new StringBuilder();
		List<String> lstSkills = new ArrayList<String>();
		List<String> lstEducation = new ArrayList<String>();
		StringBuilder sbRecruitID = new StringBuilder();
		
		if (strSkills != null) {
			for (int i = 0; i < strSkills.length; i++) {
				if (!strSkills[i].trim().equals("")) {
						lstSkills.add(strSkills[i].trim());
				}
			}
		}
		
		pst = con.prepareStatement("select financial_year_from FROM financial_year_details order by financial_year_from limit 1");
		rst = pst.executeQuery();
//		System.out.println("new Date ===> " + new Date());
		String fYear = "";
		while (rst.next()) {
			fYear = rst.getString("financial_year_from");
		}
		rst.close();
		pst.close();
		
		int fStartYear = uF.parseToInt(uF.getDateFormat(fYear, DBDATE, "yyyy"));
		yearList = new FillYears().fillYears(uF.getCurrentDate(CF.getStrTimeZone()), ""+fStartYear); 
		
//		System.out.println("sbSkills ===> "+sbSkills);
		if((uF.parseToInt(getF_org())>0) ||(getF_wlocation() != null && getF_wlocation().length > 0)) {
			sbQuery1.append("select recruitment_id from recruitment_details where org_id > 0 ");
			if(getF_org() != null && !getF_org().equals("")) {
				sbQuery1.append(" and org_id = "+uF.parseToInt(getF_org()));	
			}
			if(getF_wlocation() != null && getF_wlocation().length > 0) {
//				sbQuery1.append(" and wlocation = "+getF_wlocation()+" ");	
				sbQuery1.append(" and wlocation in ("+StringUtils.join(getF_wlocation(), ",")+") ");
			}
			pst = con.prepareStatement(sbQuery1.toString());
			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
//			System.out.println("sbSkills pst===> "+pst);
			while (rst.next()) {
				if(sbRecruitID == null || sbRecruitID.toString().equals("")){
					sbRecruitID.append(rst.getString("recruitment_id"));
				} else {
					sbRecruitID.append(","+rst.getString("recruitment_id"));
				}
			}
			rst.close();
			pst.close();
			
			if(sbRecruitID == null || sbRecruitID.toString().equals("")) {
				sbRecruitID.append("0");
			}
		}
//		
		List<String> allFilterCandidate = new ArrayList<String>();
		List<String> orgLocCandidate = new ArrayList<String>();
		List<String> skillCandidate = new ArrayList<String>();
		List<String> eduCandidate = new ArrayList<String>();
		
//		if((getF_org() != null && !getF_org().equals("")) && (getF_wlocation() != null && getF_wlocation().length > 0) && (getCheckStatus_reportfilter() == null || getCheckStatus_reportfilter().length == 0)) {
		if((getF_org() != null && !getF_org().equals("")) && (getF_wlocation() == null || getF_wlocation().length == 0) && (getCheckStatus_reportfilter() == null || getCheckStatus_reportfilter().length == 0)) {
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select emp_per_id from candidate_personal_details where org_id = ? ");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(getF_org()));
			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			//System.out.println("RecruitID pst===> "+pst);
			while (rst.next()) {
				if(!allFilterCandidate.contains(rst.getString("emp_per_id"))) {
					allFilterCandidate.add(rst.getString("emp_per_id"));
				}
				if(!orgLocCandidate.contains(rst.getString("emp_per_id"))) {
					orgLocCandidate.add(rst.getString("emp_per_id"));
				}
			}
			rst.close();
			pst.close();
			
		}
		
		
		if((sbRecruitID != null && !sbRecruitID.toString().equals("")) ||(getCheckStatus_reportfilter() != null && getCheckStatus_reportfilter().length > 0)){
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select distinct(candidate_id) from candidate_application_details where recruitment_id > 0 ");
			if(sbRecruitID != null && !sbRecruitID.toString().equals("")){
				sbQuery.append(" and recruitment_id in("+sbRecruitID.toString()+") ");
			}
			if(getCheckStatus_reportfilter() != null && getCheckStatus_reportfilter().length > 0){
//				sbQuery.append(" and application_status = "+getCheckStatus_reportfilter()+" ");
				sbQuery1.append(" and application_status in ("+StringUtils.join(getCheckStatus_reportfilter(), ",")+") ");
			}
			pst = con.prepareStatement(sbQuery.toString());
			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			//System.out.println("RecruitID pst===> "+pst);
			while (rst.next()) {
				if(!allFilterCandidate.contains(rst.getString("candidate_id"))) {
					allFilterCandidate.add(rst.getString("candidate_id"));
				}
				if(!orgLocCandidate.contains(rst.getString("candidate_id"))) {
					orgLocCandidate.add(rst.getString("candidate_id"));
				}
			}
			rst.close();
			pst.close();
			
		}
		
		for(int i=0; lstSkills != null && !lstSkills.isEmpty() && i < lstSkills.size(); i++) {
			pst = con.prepareStatement("select skill_id,emp_id from candidate_skills_description where skill_id in("+lstSkills.get(i)+")");
			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			//System.out.println("sbSkills pst===> "+pst);
			while (rst.next()) {
				if(!allFilterCandidate.contains(rst.getString("emp_id"))) {
					allFilterCandidate.add(rst.getString("emp_id"));
				}
				if(!skillCandidate.contains(rst.getString("emp_id"))) {
					skillCandidate.add(rst.getString("emp_id"));
				}
			}	
			rst.close();
			pst.close();
		}
		
		if (strMinEducation != null) {
			for (int i = 0; i < strMinEducation.length; i++) {
				if (!strMinEducation[i].trim().equals("")) {
						lstEducation.add(strMinEducation[i].trim());
				}
			}
		}
		for(int i=0; lstEducation != null && !lstEducation.isEmpty() && i < lstEducation.size(); i++){
			pst = con.prepareStatement("select education_id,emp_id from candidate_education_details where education_id in("+lstEducation.get(i)+")");
			rst = pst.executeQuery();
			while (rst.next()) {
				if(!allFilterCandidate.contains(rst.getString("emp_id"))) {
					allFilterCandidate.add(rst.getString("emp_id"));
				}
				if(!eduCandidate.contains(rst.getString("emp_id"))) {
					eduCandidate.add(rst.getString("emp_id"));
				}
			}
			rst.close();
			pst.close();
			
		}
//		System.out.println("allFilterCandidate ===>> " + allFilterCandidate);
//		System.out.println("orgLocCandidate ===>> " + orgLocCandidate);
//		System.out.println("skillCandidate ===>> " + skillCandidate);
//		System.out.println("eduCandidate ===>> " + eduCandidate);
		
		List<String> finalCandiId = new ArrayList<String>();
		for(int i=0; allFilterCandidate!=null && !allFilterCandidate.isEmpty() && i<allFilterCandidate.size(); i++) {
			if(orgLocCandidate != null && !orgLocCandidate.isEmpty() && lstSkills != null && !lstSkills.isEmpty() && lstEducation != null && !lstEducation.isEmpty()) {
				if(orgLocCandidate.contains(allFilterCandidate.get(i)) && skillCandidate.contains(allFilterCandidate.get(i)) && eduCandidate.contains(allFilterCandidate.get(i))) {
					finalCandiId.add(allFilterCandidate.get(i));
				}
			} else if(orgLocCandidate != null && !orgLocCandidate.isEmpty() && lstSkills != null && !lstSkills.isEmpty()) {
				if(orgLocCandidate.contains(allFilterCandidate.get(i)) && skillCandidate.contains(allFilterCandidate.get(i))) {
					finalCandiId.add(allFilterCandidate.get(i));
				}
			} else if(orgLocCandidate != null && !orgLocCandidate.isEmpty() && lstEducation != null && !lstEducation.isEmpty()) {
				if(orgLocCandidate.contains(allFilterCandidate.get(i)) && eduCandidate.contains(allFilterCandidate.get(i))) {
					finalCandiId.add(allFilterCandidate.get(i));
				}
			} else if(lstSkills != null && !lstSkills.isEmpty() && lstEducation != null && !lstEducation.isEmpty()) {
				if(skillCandidate.contains(allFilterCandidate.get(i)) && eduCandidate.contains(allFilterCandidate.get(i))) {
					finalCandiId.add(allFilterCandidate.get(i));
				}
			} else {
				finalCandiId.add(allFilterCandidate.get(i));
			}
		}
		StringBuilder sbFinalCadiIDS = null;
//		System.out.println("finalCandiId ===>> " + finalCandiId);
		for(int a=0; finalCandiId != null && !finalCandiId.isEmpty() && a<finalCandiId.size(); a++) {
			if(sbFinalCadiIDS == null) {
				sbFinalCadiIDS = new StringBuilder();
				sbFinalCadiIDS.append(finalCandiId.get(a));
			} else {
				sbFinalCadiIDS.append(","+finalCandiId.get(a));
			}
		}
//		System.out.println("sbFinalCadiIDS ===> "+sbFinalCadiIDS.toString());
		List<String> alInner;
		List<List<String>> al = new ArrayList<List<String>>();
		if(getCurrCTC() == null) {
			setCurrCTC("0 - 10000000");
		}
		if(getExpectedCTC() == null) {
			setExpectedCTC("0 - 10000000");
		}
		if(getNoticePeriod() == null) {
			setNoticePeriod("0 - 365");
		}
		String[] arrCurrCTC = getCurrCTC().split("-");
		String[] arrExpectedCTC = getExpectedCTC().split("-");
		String[] arrNoticePeriod = getNoticePeriod().split("-");
		
		setMinCurrCTC(arrCurrCTC[0].trim());
		setMinExpectedCTC(arrExpectedCTC[0].trim());
		setMinNoticePeriod(arrNoticePeriod[0].trim());
		
		setMaxCurrCTC(arrCurrCTC[1].trim());
		setMaxExpectedCTC(arrExpectedCTC[1].trim());
		setMaxNoticePeriod(arrNoticePeriod[1].trim());
		
		String minDate = "";
		String maxDate = "";
		if(getStrMonth() != null && !getStrMonth().equals("") && getStrYear() != null && !getStrYear().equals("")) {
			Calendar cal = GregorianCalendar.getInstance();
			cal.set(Calendar.MONTH, uF.parseToInt(getStrMonth()) -1 );
			cal.set(Calendar.YEAR, uF.parseToInt(getStrYear()));
			
			int maxDays = cal.getActualMaximum(Calendar.DATE);
			int minDays = cal.getActualMinimum(Calendar.DATE);
			cal.set(Calendar.DAY_OF_MONTH, minDays);
			minDate = ((cal.get(Calendar.DAY_OF_MONTH) < 10) ? "0" + cal.get(Calendar.DAY_OF_MONTH) : cal.get(Calendar.DAY_OF_MONTH)) + "/" + (((cal.get(Calendar.MONTH) + 1) < 10) ? "0" + (cal.get(Calendar.MONTH) + 1) : (cal.get(Calendar.MONTH) + 1)) + "/" + cal.get(Calendar.YEAR);
			
			cal.set(Calendar.DAY_OF_MONTH, maxDays);
			maxDate = ((cal.get(Calendar.DAY_OF_MONTH) < 10) ? "0" + cal.get(Calendar.DAY_OF_MONTH) : cal.get(Calendar.DAY_OF_MONTH)) + "/" + (((cal.get(Calendar.MONTH) + 1) < 10) ? "0" + (cal.get(Calendar.MONTH) + 1) : (cal.get(Calendar.MONTH) + 1)) + "/" + cal.get(Calendar.YEAR);
		}
//		System.out.println("minDate=====>"+minDate+"===maxDate=====>"+maxDate);
		StringBuilder query=new StringBuilder();
		
		if((getF_org()!=null && !getF_org().equals("")) || (getF_wlocation()!=null && getF_wlocation().length > 0) || (allFilterCandidate != null && !allFilterCandidate.isEmpty()) || (lstSkills != null && !lstSkills.isEmpty()) || (lstEducation!= null && !lstEducation.isEmpty())) {
			query.append("select years,emp_per_id,emp_fname,emp_mname,emp_lname,emp_date_of_birth,emp_city_id,emp_email,emp_pan_no ,current_ctc," +
					"expected_ctc,notice_period,emp_image,availability_for_interview " +
					"from candidate_personal_details cpd left join (select sum(to_date-from_date)/365 as years ,emp_id from " +
					"candidate_prev_employment cpe group by emp_id) as a on cpd.emp_per_id = a.emp_id where cpd.emp_per_id>0 and " +
					"cpd.candididate_emp_id is null and cpd.emp_per_id not in (select candidate_id from candidate_application_details where recruitment_id = "+recruitId+") ");
			if(minDate != null && !minDate.equals("") && maxDate != null && !maxDate.equals("")){
				query.append(" and emp_per_id in(select candidate_id from candidate_application_details where candidate_id in ("+sbFinalCadiIDS.toString()+") and " +
					"application_date between '"+uF.getDateFormat(minDate, DATE_FORMAT)+"' and '" +uF.getDateFormat(maxDate, DATE_FORMAT)+"') ");
			} else if(sbFinalCadiIDS != null && !sbFinalCadiIDS.toString().equals("")){
				query.append(" and  emp_per_id in("+sbFinalCadiIDS.toString()+") ");
			} else {
				query.append(" and  emp_per_id in(0) ");
			}
			if(uF.parseToDouble(arrCurrCTC[0].trim()) == 0) {
				query.append(" and (cpd.current_ctc is null or (cpd.current_ctc >= "+uF.parseToDouble(arrCurrCTC[0].trim())+" and cpd.current_ctc <= "+ uF.parseToDouble(arrCurrCTC[1].trim())+"))");
			} else {
				query.append(" and cpd.current_ctc >= "+uF.parseToDouble(arrCurrCTC[0].trim())+" and cpd.current_ctc <= "+ uF.parseToDouble(arrCurrCTC[1].trim()));
			}
			if(uF.parseToDouble(arrExpectedCTC[0].trim()) == 0) {
				query.append(" and (cpd.expected_ctc is null or (cpd.expected_ctc >= "+uF.parseToDouble(arrExpectedCTC[0].trim())+" and cpd.expected_ctc <= "+ uF.parseToDouble(arrExpectedCTC[1].trim())+"))");
			} else {
				query.append(" and cpd.expected_ctc >= "+uF.parseToDouble(arrExpectedCTC[0].trim())+" and cpd.expected_ctc <= "+ uF.parseToDouble(arrExpectedCTC[1].trim()));
			}
			if(uF.parseToInt(arrNoticePeriod[0].trim()) == 0) {
				query.append(" and (cpd.notice_period is null or (cpd.notice_period >= "+uF.parseToInt(arrNoticePeriod[0].trim())+" and cpd.notice_period <= "+ uF.parseToInt(arrNoticePeriod[1].trim())+"))");
			} else {
				query.append(" and cpd.notice_period >= "+uF.parseToInt(arrNoticePeriod[0].trim())+" and cpd.notice_period <= "+ uF.parseToInt(arrNoticePeriod[1].trim()));
			}
//			if(getStrExperience()!=null && uF.parseToInt(getStrExperience())!=0){
//			    if(uF.parseToInt(getStrExperience())==1)
//			    	query.append(" and years<=1 and years>=0");
//			    else if(uF.parseToInt(getStrExperience())==2)
//			    	query.append(" and years<2 and years>=1");
//			    else if(uF.parseToInt(getStrExperience())==3)
//			    	query.append(" and years<5 and years>=2");
//		    	else if(uF.parseToInt(getStrExperience())==4)
//		    		query.append(" and years=10 and years>=5");
//	    		else if(uF.parseToInt(getStrExperience())==5)
//	    			query.append(" and years>=10");
//			} 
			if (getStrExperience() != null && getStrExperience().length > 0) {
				StringBuilder sbExp = null; 
				for (int i = 0; i < getStrExperience().length; i++) {
					if (!getStrExperience()[i].trim().equals("")) {
						if(uF.parseToInt(getStrExperience()[i].trim())==1){
							if(sbExp == null){
								sbExp = new StringBuilder();
								sbExp.append(" (years<=1 and years>=0)");
							} else {
								sbExp.append(" or (years<=1 and years>=0)");
							}
						} else if(uF.parseToInt(getStrExperience()[i].trim())==2) {
							if(sbExp == null){
								sbExp = new StringBuilder();
								sbExp.append(" (years<2 and years>=1)");
							} else {
								sbExp.append(" or (years<2 and years>=1)");
							}
						} else if(uF.parseToInt(getStrExperience()[i].trim())==3) {
							if(sbExp == null){
								sbExp = new StringBuilder();
								sbExp.append(" (years<5 and years>=2)");
							} else {
								sbExp.append(" or (years<5 and years>=2)");
							}
						} else if(uF.parseToInt(getStrExperience()[i].trim())==4) {
							if(sbExp == null){
								sbExp = new StringBuilder();
								sbExp.append(" (years=10 and years>=5)");
							} else {
								sbExp.append(" or (years=10 and years>=5)");
							}
						} else if(uF.parseToInt(getStrExperience()[i].trim())==5){
							if(sbExp == null){
								sbExp = new StringBuilder(); 
								sbExp.append(" (years>=10)");
							} else {
								sbExp.append(" or (years>=10)");
							}
						}
					}
				}
				if(sbExp!=null){
					query.append(" and ("+sbExp.toString()+")");
				}
			}
			
		} else {
			query.append("select years,emp_per_id,emp_fname,emp_mname,emp_lname,emp_date_of_birth,emp_city_id,emp_email,emp_pan_no ,current_ctc," +
					"expected_ctc,notice_period,emp_image " +
					"from candidate_personal_details cpd left join (select sum(to_date-from_date)/365 as years ,emp_id from " +
					"candidate_prev_employment cpe group by emp_id) as a on cpd.emp_per_id = a.emp_id where cpd.emp_per_id>0 and " +
					"cpd.candididate_emp_id is null and cpd.emp_per_id not in (select candidate_id from candidate_application_details where recruitment_id = "+recruitId+") ");
			if(minDate != null && !minDate.equals("") && maxDate != null && !maxDate.equals("")){
				query.append(" and emp_per_id in(select candidate_id from candidate_application_details where candidate_id in ("+sbFinalCadiIDS.toString()+") and " +
					"application_date between '"+uF.getDateFormat(minDate, DATE_FORMAT)+"' and '" +uF.getDateFormat(maxDate, DATE_FORMAT)+"') ");
			}
			if(uF.parseToDouble(arrCurrCTC[0].trim()) == 0) {
				query.append(" and (cpd.current_ctc is null or (cpd.current_ctc >= "+uF.parseToDouble(arrCurrCTC[0].trim())+" and cpd.current_ctc <= "+ uF.parseToDouble(arrCurrCTC[1].trim())+"))");
			} else {
				query.append(" and cpd.current_ctc >= "+uF.parseToDouble(arrCurrCTC[0].trim())+" and cpd.current_ctc <= "+ uF.parseToDouble(arrCurrCTC[1].trim()));
			}
			if(uF.parseToDouble(arrExpectedCTC[0].trim()) == 0) {
				query.append(" and (cpd.expected_ctc is null or (cpd.expected_ctc >= "+uF.parseToDouble(arrExpectedCTC[0].trim())+" and cpd.expected_ctc <= "+ uF.parseToDouble(arrExpectedCTC[1].trim())+"))");
			} else {
				query.append(" and cpd.expected_ctc >= "+uF.parseToDouble(arrExpectedCTC[0].trim())+" and cpd.expected_ctc <= "+ uF.parseToDouble(arrExpectedCTC[1].trim()));
			}
			if(uF.parseToInt(arrNoticePeriod[0].trim()) == 0) {
				query.append(" and (cpd.notice_period is null or (cpd.notice_period >= "+uF.parseToInt(arrNoticePeriod[0].trim())+" and cpd.notice_period <= "+ uF.parseToInt(arrNoticePeriod[1].trim())+"))");
			} else {
				query.append(" and cpd.notice_period >= "+uF.parseToInt(arrNoticePeriod[0].trim())+" and cpd.notice_period <= "+ uF.parseToInt(arrNoticePeriod[1].trim()));
			}
//			if(getStrExperience()!=null && uF.parseToInt(getStrExperience())!=0){
//			    if(uF.parseToInt(getStrExperience())==1)
//			    	query.append(" and years<=1 and years>=0");
//			    else if(uF.parseToInt(getStrExperience())==2)
//			    	query.append(" and years<2 and years>=1");
//			    else if(uF.parseToInt(getStrExperience())==3)
//			    	query.append(" and years<5 and years>=2");
//		    	else if(uF.parseToInt(getStrExperience())==4)
//		    		query.append(" and years=10 and years>=5");
//	    		else if(uF.parseToInt(getStrExperience())==5)
//	    			query.append(" and years>=10");
//			}
			if (getStrExperience() != null && getStrExperience().length > 0) {
				StringBuilder sbExp = null; 
				for (int i = 0; i < getStrExperience().length; i++) {
					if (!getStrExperience()[i].trim().equals("")) {
						if(uF.parseToInt(getStrExperience()[i].trim())==1){
							if(sbExp == null){
								sbExp = new StringBuilder();
								sbExp.append(" (years<=1 and years>=0)");
							} else {
								sbExp.append(" or (years<=1 and years>=0)");
							}
						} else if(uF.parseToInt(getStrExperience()[i].trim())==2) {
							if(sbExp == null){
								sbExp = new StringBuilder();
								sbExp.append(" (years<2 and years>=1)");
							} else {
								sbExp.append(" or (years<2 and years>=1)");
							}
						} else if(uF.parseToInt(getStrExperience()[i].trim())==3) {
							if(sbExp == null){
								sbExp = new StringBuilder();
								sbExp.append(" (years<5 and years>=2)");
							} else {
								sbExp.append(" or (years<5 and years>=2)");
							}
						} else if(uF.parseToInt(getStrExperience()[i].trim())==4) {
							if(sbExp == null){
								sbExp = new StringBuilder();
								sbExp.append(" (years=10 and years>=5)");
							} else {
								sbExp.append(" or (years=10 and years>=5)");
							}
						} else if(uF.parseToInt(getStrExperience()[i].trim())==5){
							if(sbExp == null){
								sbExp = new StringBuilder();
								sbExp.append(" (years>=10)");
							} else {
								sbExp.append(" or (years>=10)");
							}
						}
					}
				}
				if(sbExp!=null){
					query.append(" and ("+sbExp.toString()+")");
				}
			}
		}
		query.append(" order by emp_fname,emp_lname");
		pst = con.prepareStatement(query.toString());
		rst = pst.executeQuery();
//		System.out.println("new Date ===> " + new Date());
//		System.out.println("pst ===> "+pst);
		StringBuilder sbCandidateIds = new StringBuilder(); 
		String oldEmp =null;
		while (rst.next()) {
			
			if(oldEmp==null || (oldEmp!=null && !oldEmp.equals(rst.getString("emp_per_id")))){
				 alInner=new ArrayList<String>();	

			if(sbCandidateIds.length()==0){
				sbCandidateIds.append(rst.getString("emp_per_id"));
			}else{
				sbCandidateIds.append(","+rst.getString("emp_per_id"));
			}
			alInner.add("");
			alInner.add("");
			alInner.add(rst.getString("emp_per_id"));
			
			alInner.add(rst.getString("emp_image"));
			
			String strEmpMName = "";
			if(flagMiddleName) {
				if(rst.getString("emp_mname") != null && rst.getString("emp_mname").trim().length()>0) {
					strEmpMName = " "+rst.getString("emp_mname");
				}
			}
			
			String empFullName = rst.getString("emp_fname") +strEmpMName+ " " + rst.getString("emp_lname");
			alInner.add(rst.getString("emp_fname") + " " + rst.getString("emp_lname"));
			
			alInner.add(""); 
			alInner.add(""); 
			alInner.add("<a class=\"factsheet\" href=\"javascript:void(0)\" onclick=\"openCandidateProfilePopup('" + rst.getString("emp_per_id") + "','');\"> </a> ");
//			alInner.add("<a class=\"factsheet\" href=\"CandidateMyProfile.action?CandID=" +rst.getString("emp_per_id")+"\" > </a>");
			alInner.add(""); //8
			
			if (strUserType != null && (strUserType.equalsIgnoreCase(ADMIN) || strUserType.equalsIgnoreCase(HRMANAGER))) {
				
				StringBuilder sbApproveDeny = new StringBuilder();

					sbApproveDeny.append("<div id=\"myDivM" + nCount + "\" > ");
//					System.out.println(" Candi ID ===> "+rst.getString("emp_per_id"));
					sbApproveDeny.append("<a target=\"_parent\" href=\"javascript:void(0)\" onclick=\"addCandidateApplicationsDetails('" + rst.getString("emp_per_id") + "', '"+recruitId+"');\">" + " <i class=\"fa fa-plus-circle\" name=\"submit\"></i></a> ");
					sbApproveDeny.append("<a href=\"javascript:void(0)\" onclick=\"getCandiApplicationsDetailsPopup('" + rst.getString("emp_per_id") + "', '"+empFullName+"', '"+recruitId+"');\">" + " <img src=\"images1/setting.png\" title=\"Application Tracker\" /></a> ");
//					sbApproveDeny.append("<input type=\"button\" class=\"btn btn-primary\" name=\"submit\" value=\"Add\" onclick=\"javascript:window.location=CandidateApplicationsDetails.action?candidateId=" + rst.getString("emp_per_id") + "&jobCode=" + recruitId + "\" /> ");
					sbApproveDeny.append("</div>");
					alInner.add(sbApproveDeny.toString()); //9
				
			} else {
				//MANAGERempId
				StringBuilder sbApproveDeny = new StringBuilder();
				
				sbApproveDeny.append("<div id=\"myDivM" + nCount + "\" > ");
//				sbApproveDeny.append("<a href=\"javascript:void(0)\" onclick=\"getCandiApplicationsDetailsPopup('" + rst.getString("emp_per_id") + "');\">" + " <img src=\"images1/setting.png\" title=\"Applications Details\" /></a> ");
				sbApproveDeny.append("<a target=\"_parent\" href=\"javascript:void(0)\" onclick=\"addCandidateApplicationsDetails('" + rst.getString("emp_per_id") + "', '"+recruitId+"');\">" + " <i class=\"fa fa-plus-circle\" name=\"submit\"></i></a> ");
				sbApproveDeny.append("<a href=\"javascript:void(0)\" onclick=\"getCandiApplicationsDetailsPopup('" + rst.getString("emp_per_id") + "', '"+empFullName+"', '"+recruitId+"');\">" + " <img src=\"images1/setting.png\" title=\"Application Tracker\" /></a> ");
//				sbApproveDeny.append("<input type=\"button\" class=\"btn btn-primary\" name=\"submit\" value=\"Add\" onclick=\"javascript:window.location=CandidateApplicationsDetails.action?candidateId=" + rst.getString("emp_per_id") + "&jobCode=" + recruitId + "\" /> ");
				sbApproveDeny.append("</div>");
				alInner.add(sbApproveDeny.toString()); //9
			}
			
			
			if(rst.getString("emp_date_of_birth") != null && !rst.getString("emp_date_of_birth").equals("")){
				alInner.add(uF.getTimeDurationBetweenDates(rst.getString("emp_date_of_birth"), DBDATE, uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, CF, uF, request, true, true, false)); //10
			}else{
				alInner.add("-"); //10
			}
			alInner.add(uF.showData(rst.getString("emp_city_id"),"-"));  //11
			
			Map<String, String> hmLastJobData = getLastJobData(con, uF, rst.getString("emp_per_id"));
			alInner.add(uF.showData(hmLastJobData.get("JOB_NAME"), "-")); //12
			alInner.add(uF.showData(hmLastJobData.get("LAST_APPLIED_DATE"), "-")); //13
			alInner.add(uF.showData(rst.getString("current_ctc"), "N/A")); //14
			alInner.add(uF.showData(rst.getString("expected_ctc"), "N/A")); //15
			if(rst.getString("notice_period") != null && !rst.getString("notice_period").equals("")) {
				alInner.add(rst.getString("notice_period")+" days"); //16
			} else {
				alInner.add("N/A"); //16
			}
			alInner.add(rst.getString("availability_for_interview")); //17
			
			al.add(alInner);
			oldEmp=rst.getString("emp_per_id");
			}
			
		}
		rst.close();
		pst.close();
		
		request.setAttribute("reportList", al);
		
		Map<String,String> hmCandidateExperience=new HashMap<String, String>();
		Map<String, String> hmEducationDetails = new HashMap<String, String>();
		Map<String, String> hmSkillDetails = new HashMap<String, String>();
		
		
		if(sbCandidateIds.length()>=1){
		
			Map<String, String> hmDegreeName = CF.getDegreeNameMap(con);
		pst = con.prepareStatement("select * from candidate_education_details where emp_id in ("+sbCandidateIds.toString()+") order by emp_id");
		rst = pst.executeQuery();
//		System.out.println("new Date ===> " + new Date());
		String strCandidateIdOld = null;
		String strCandidateIdNew = null;
		StringBuilder sbContainer = new StringBuilder();

		while(rst.next()){
				strCandidateIdNew = rst.getString("emp_id");
				if (strCandidateIdNew != null && !strCandidateIdNew.equals(strCandidateIdOld)) {
					sbContainer.replace(0, sbContainer.length(), "");
				}

				if (strCandidateIdNew != null && strCandidateIdNew.equals(strCandidateIdOld))
					sbContainer.append(", " + hmDegreeName.get(rst.getString("education_id")));
				else
					sbContainer.append(hmDegreeName.get(rst.getString("education_id")));

				hmEducationDetails.put(strCandidateIdNew,sbContainer.toString());
				strCandidateIdOld = strCandidateIdNew;
		}
		rst.close();
		pst.close();
		

		Map<String, String> hmSkillsName = CF.getSkillNameMap(con);
		pst = con.prepareStatement("select * from candidate_skills_description where emp_id in ("+sbCandidateIds.toString()+") order by emp_id");
		rst = pst.executeQuery();
//		System.out.println("new Date ===> " + new Date());
		strCandidateIdOld = null;
		strCandidateIdNew = null;
		sbContainer.replace(0, sbContainer.length(), "");
	
		while(rst.next()){
			strCandidateIdNew = rst.getString("emp_id");
			if (strCandidateIdNew != null && !strCandidateIdNew.equals(strCandidateIdOld)) {
				sbContainer.replace(0, sbContainer.length(), "");
			}
			
			if (strCandidateIdNew != null && strCandidateIdNew.equals(strCandidateIdOld))
				sbContainer.append(", " + hmSkillsName.get(rst.getString("skill_id")));
			else
				sbContainer.append(hmSkillsName.get(rst.getString("skill_id")));
			
			hmSkillDetails.put(strCandidateIdNew, sbContainer.toString());
			strCandidateIdOld = strCandidateIdNew;
		}
		rst.close();
		pst.close();

		// Logic for multiple experience
		
		pst = con.prepareStatement("select * from candidate_prev_employment where emp_id in ("+sbCandidateIds.toString()+") order by emp_id");
		rst=pst.executeQuery();
//		System.out.println("new Date ===> " + new Date());
		strCandidateIdOld = null;
		strCandidateIdNew = null;
		int noyear = 0,nomonth = 0,nodays = 0;
		//====start parvez on 07-07-2021===
		StringBuilder strCandidateIdNew1 = new StringBuilder();
		//====end parvez on 07-07-2021====
		int k=0;
		while(rst.next()) {
			strCandidateIdNew = rst.getString("emp_id");
			if(strCandidateIdNew!=null && !strCandidateIdNew.equals(strCandidateIdOld)){
			
				noyear=0;
				nomonth=0;
				nodays=0;
			}
				String datedif=uF.dateDifference(rst.getString("from_date"),DBDATE , rst.getString("to_date"), DBDATE);
				long datediff=uF.parseToLong(datedif);		    		 
		    	noyear+=(int) (datediff/365);
		    	nomonth+=(int) ((datediff%365)/30);
		    	nodays+=(int) ((datediff%365)%30);
		     
		    	if(nodays>30){
		    		nomonth=nomonth+1;
		    	}
		    	if(nomonth>12){
		    		nomonth=nomonth-12;
		    		noyear=noyear+1;
		    	}
		    	hmCandidateExperience.put(rst.getString("emp_id"),""+noyear+" Y "+nomonth+" M"); 
		    	strCandidateIdOld = strCandidateIdNew;
		    	//====start parvez on 07-07-2021====
		    	if (k == 0) {
		    		strCandidateIdNew1.append(rst.getString("emp_id"));
				} else {
					strCandidateIdNew1.append(","+rst.getString("emp_id"));
				}
				k++;
		    	/*if(strCandidateIdNew.length()==0){
		    		strCandidateIdNew1.append(rst.getString("emp_id"));
		    	}else{
		    		strCandidateIdNew1.append(","+rst.getString("emp_id"));
		    	}*/
		    	//====end parvez 07-07-2021====
		}
		
		rst.close();
		pst.close();
		
		//=====start parvez on 07-07-2021===
		if(strCandidateIdNew1.length()==0){
			pst = con.prepareStatement("select * from candidate_personal_details where emp_per_id > 0 and emp_per_id in ("+sbCandidateIds.toString()+") order by emp_per_id");
			rst=pst.executeQuery();
			while(rst.next()){
				hmCandidateExperience.put(rst.getString("emp_per_id"), rst.getString("total_experience"));
			}
		}else{
			pst = con.prepareStatement("select * from candidate_personal_details where emp_per_id > 0 and emp_per_id in ("+sbCandidateIds.toString()+") and emp_per_id not in ( "+strCandidateIdNew1+") order by emp_per_id");
			rst=pst.executeQuery();
			while(rst.next()){
				hmCandidateExperience.put(rst.getString("emp_per_id"), rst.getString("total_experience"));
			}
		}
			
		//====end parvez on 07-07-2021====
		
		}
		request.setAttribute("hmExperienceDetails", hmCandidateExperience);
		
		request.setAttribute("hmEducationDetails", hmEducationDetails);
		request.setAttribute("hmSkillDetails", hmSkillDetails);
	} catch (Exception e) {
		e.printStackTrace();
		log.error(e.getClass() + ": " + e.getMessage(), e);
	} finally {
		db.closeResultSet(rst);
		db.closeStatements(pst);
		db.closeConnection(con);
	}
	return SUCCESS;

}


private Map<String, String> getLastJobData(Connection con, UtilityFunctions uF, String candidateId) {
	
	PreparedStatement pst = null;
	ResultSet rst = null;
	Map<String, String> hmLastJobData = new HashMap<String, String>();
	try {

//		pst=con.prepareStatement("select * from candidate_application_details where recruitment_id = (select max(recruitment_id) from " +
//				"candidate_application_details where candidate_id = ?) and candidate_id = ?");
		pst=con.prepareStatement("select cad.application_date,rd.job_code,rd.job_title from candidate_application_details cad, recruitment_details rd " +
		" where cad.recruitment_id = rd.recruitment_id and candidate_id = ? order by candi_application_deatils_id desc limit 1");
		pst.setInt(1, uF.parseToInt(candidateId));
//		pst.setInt(2, uF.parseToInt(candidateId));
		rst=pst.executeQuery();
//		System.out.println("pst ===> " + pst);
		while(rst.next()){
			hmLastJobData.put("JOB_NAME", rst.getString("job_title"));
			hmLastJobData.put("LAST_APPLIED_DATE", uF.getDateFormat(rst.getString("application_date"), DBDATE, CF.getStrReportDateFormat()));
		}
		rst.close();
		pst.close();
		
	}catch(Exception e){
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
	return hmLastJobData;
}


	public String[] getCheckStatus_reportfilter() {
		return checkStatus_reportfilter;
	}
	
	public void setCheckStatus_reportfilter(String[] checkStatus_reportfilter) {
		this.checkStatus_reportfilter = checkStatus_reportfilter;
	}
	
	public String getF_org() {
		return f_org;
	}
	
	public void setF_org(String f_org) {
		this.f_org = f_org;
	}
	
	public List<FillOrganisation> getOrganisationList() {
		return organisationList;
	}
	
	public void setOrganisationList(List<FillOrganisation> organisationList) {
		this.organisationList = organisationList;
	}
	
	public String[] getF_wlocation() {
		return f_wlocation;
	}
	
	public void setF_wlocation(String[] f_wlocation) {
		this.f_wlocation = f_wlocation;
	}
	
	public List<FillWLocation> getWorkList() {
		return workList;
	}
	
	public void setWorkList(List<FillWLocation> workList) {
		this.workList = workList;
	}
	
	public String[] getStrMinEducation() {
		return strMinEducation;
	}
	
	public void setStrMinEducation(String[] strMinEducation) {
		this.strMinEducation = strMinEducation;
	}
	
	public List<FillEducational> getEduList() {
		return eduList;
	}
	
	public void setEduList(List<FillEducational> eduList) {
		this.eduList = eduList;
	}
	
	public String[] getStrSkills() {
		return strSkills;
	}
	
	public void setStrSkills(String[] strSkills) {
		this.strSkills = strSkills;
	}
	
	public List<FillSkills> getSkillsList() {
		return skillsList;
	}
	
	public void setSkillsList(List<FillSkills> skillsList) {
		this.skillsList = skillsList;
	}
	
	public String[] getStrExperience() {
		return strExperience;
	}
	
	public void setStrExperience(String[] strExperience) {
		this.strExperience = strExperience;
	}
	
	public String getRecruitId() {
		return recruitId;
	}
	
	public void setRecruitId(String recruitId) {
		this.recruitId = recruitId;
	}

	public String getCurrCTC() {
		return currCTC;
	}

	public void setCurrCTC(String currCTC) {
		this.currCTC = currCTC;
	}

	public String getExpectedCTC() {
		return expectedCTC;
	}

	public void setExpectedCTC(String expectedCTC) {
		this.expectedCTC = expectedCTC;
	}

	public String getNoticePeriod() {
		return noticePeriod;
	}

	public void setNoticePeriod(String noticePeriod) {
		this.noticePeriod = noticePeriod;
	}

	public String getMinCurrCTC() {
		return minCurrCTC;
	}

	public void setMinCurrCTC(String minCurrCTC) {
		this.minCurrCTC = minCurrCTC;
	}

	public String getMinExpectedCTC() {
		return minExpectedCTC;
	}

	public void setMinExpectedCTC(String minExpectedCTC) {
		this.minExpectedCTC = minExpectedCTC;
	}

	public String getMinNoticePeriod() {
		return minNoticePeriod;
	}

	public void setMinNoticePeriod(String minNoticePeriod) {
		this.minNoticePeriod = minNoticePeriod;
	}

	public String getMaxCurrCTC() {
		return maxCurrCTC;
	}

	public void setMaxCurrCTC(String maxCurrCTC) {
		this.maxCurrCTC = maxCurrCTC;
	}

	public String getMaxExpectedCTC() {
		return maxExpectedCTC;
	}

	public void setMaxExpectedCTC(String maxExpectedCTC) {
		this.maxExpectedCTC = maxExpectedCTC;
	}

	public String getMaxNoticePeriod() {
		return maxNoticePeriod;
	}

	public void setMaxNoticePeriod(String maxNoticePeriod) {
		this.maxNoticePeriod = maxNoticePeriod;
	}

	public List<FillMonth> getMonthList() {
		return monthList;
	}

	public void setMonthList(List<FillMonth> monthList) {
		this.monthList = monthList;
	}

	public List<FillYears> getYearList() {
		return yearList;
	}

	public void setYearList(List<FillYears> yearList) {
		this.yearList = yearList;
	}

	public String getStrMonth() {
		return strMonth;
	}

	public void setStrMonth(String strMonth) {
		this.strMonth = strMonth;
	}

	public String getStrYear() {
		return strYear;
	}

	public void setStrYear(String strYear) {
		this.strYear = strYear;
	}

	private HttpServletRequest request;
	
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}
	
}