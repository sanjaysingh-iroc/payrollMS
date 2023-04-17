package com.konnect.jpms.recruitment;

import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.itextpdf.text.BaseColor;
import com.konnect.jpms.export.DataStyle;
import com.konnect.jpms.select.FillMonth;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillSkills;
import com.konnect.jpms.select.FillText;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.select.FillYears;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;

public class CandidateXMLReport  implements ServletRequestAware, ServletResponseAware,IStatements {

	HttpSession session;
	private HttpServletRequest request;
	private CommonFunctions CF;
	
	String strUserType = null;
	String strSessionEmpId = null;


	private String operation;
	private String candidateId;
	private String currCTC;
	private String expectedCTC;
	private String noticePeriod;
	
	private String minCurrCTC;
	private String minExpectedCTC;
	private String minNoticePeriod;
	private String maxCurrCTC;
	private String maxExpectedCTC;
	private String maxNoticePeriod;
	private List<FillMonth> monthList;
	private List<FillYears> yearList;
	private String strMonth;
	private String strYear;
	
	private List<FillText> alEmail = new ArrayList<FillText>();
	private List<FillText> alPanCard = new ArrayList<FillText>();
	
	private String f_pancard;
	private String f_email;
	
	private String recruitId;
	private String jobcode;
	private String empName;
	private String[] checkStatus_reportfilter;
	
	private String f_org;
	private List<FillOrganisation> organisationList;

	private  String[] f_wlocation;
	private  List<FillWLocation> workList;
    
	private String[] strMinEducation;
	private  List<FillEducational> eduList;
    
	private String[] strSkills;
	private  List<FillSkills> skillsList;
	
	private String[] strExperience;
	
	HttpServletResponse response;

	public void execute() throws Exception {
		session = request.getSession();
		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);
	
		
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
	
		UtilityFunctions uF = new UtilityFunctions();
	
		downloadeXML(uF);
//		downloadXML();

	}
	
	private void createXML(Map<String, Map<String, String>> jobProfile_details) {
		try {

			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory
					.newDocumentBuilder();
			Document document = documentBuilder.newDocument();

			Element orderElement = document.createElement("CandidateDetails");
			document.appendChild(orderElement);

			Set<String> set = jobProfile_details.keySet();
			Iterator<String> iterator = set.iterator();

			while (iterator.hasNext()) {

				Element orderDetailElement = document.createElement("Candidate");
				orderElement.appendChild(orderDetailElement);
				
				String key = iterator.next();
				Map<String, String> InnerJobProfile_details = jobProfile_details.get(key);

				Set<String> set1 = InnerJobProfile_details.keySet();
				Iterator<String> iterator1 = set1.iterator();

				while (iterator1.hasNext()) {
					String key1 = (String) iterator1.next();
					Element detailElement1 = document.createElement(key1);
					detailElement1.appendChild(document.createTextNode((String) InnerJobProfile_details.get(key1)));
					orderDetailElement.appendChild(detailElement1);

				}				
			}

			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();

			DOMSource source = new DOMSource(document);
			StreamResult result = new StreamResult(buffer);
			transformer.transform(source, result);

			response.setContentType("application/xml");
			response.setContentLength(buffer.size());
			response.setHeader("Content-Disposition","attachment; filename=CandidateXMLReport.xml");
			ServletOutputStream out = response.getOutputStream();
			buffer.writeTo(out);
			out.flush();
			out.close();

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}
	
	
	
	public void downloadeXML(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null; 
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			Map<String,String> hmCandidateExperience1 =  getCandidateExpMap(con, uF);
			pst = con.prepareStatement("select financial_year_from FROM financial_year_details order by financial_year_from limit 1");
//			System.out.println("1 pst===> "+pst);
			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			String fYear = "";
			while (rst.next()) {
				fYear = rst.getString("financial_year_from");
			}
			rst.close();
			pst.close();
			
			int fStartYear = uF.parseToInt(uF.getDateFormat(fYear, DBDATE, "yyyy"));
			yearList = new FillYears().fillYears(uF.getCurrentDate(CF.getStrTimeZone()), ""+fStartYear); 
			
			Map<String, String> hmreq_designation_name = new HashMap<String, String>();

			Map<String, String> hmreq_job_location = new HashMap<String, String>();
			String strRequirement = null;
			pst = con.prepareStatement("Select job_code, no_position,designation_name,wlocation_name,recruitment_id from recruitment_details join designation_details using(designation_id) join work_location_info on(wlocation=wlocation_id) where job_approval_status=1 and recruitment_id=? ");
			pst.setInt(1, uF.parseToInt(getRecruitId()));
//			System.out.println("2 pst===> "+pst);
			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while (rst.next()) {
				hmreq_designation_name.put(rst.getString("recruitment_id"), rst.getString("designation_name"));
				hmreq_job_location.put(rst.getString("recruitment_id"), rst.getString("wlocation_name"));
				setJobcode(rst.getString("job_code"));  	// setting job code
				strRequirement = rst.getString("no_position");
			}
			rst.close();
			pst.close();
			
		
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
			
			
			if((uF.parseToInt(getF_org())>0) ||(getF_wlocation() != null && getF_wlocation().length > 0)) {
				sbQuery1.append("select recruitment_id from recruitment_details where org_id > 0 ");
				if(getF_org() != null && !getF_org().equals("")){
					sbQuery1.append(" and org_id = "+uF.parseToInt(getF_org()));	
				}
				
				
				if(getF_wlocation() != null && !getF_wlocation().equals("")){
					sbQuery1.append(" and wlocation in ("+StringUtils.join(getF_wlocation(), ",")+") ");
				}
				pst = con.prepareStatement(sbQuery1.toString());
//				System.out.println("3 pst===> "+pst);
				rst = pst.executeQuery();
				while (rst.next()) {
					if(sbRecruitID == null || sbRecruitID.toString().equals("")) {
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
//			System.out.println("sbRecruitID ===> "+sbRecruitID);
			
			List<String> allFilterCandidate = new ArrayList<String>();
			List<String> orgLocCandidate = new ArrayList<String>();
			List<String> skillCandidate = new ArrayList<String>();
			List<String> eduCandidate = new ArrayList<String>();
//			System.out.println("getF_wlocation()--->");
//			if((getF_org() != null && !getF_org().equals("")) && (getF_wlocation() != null && getF_wlocation().length > 0) && (getCheckStatus_reportfilter() == null || getCheckStatus_reportfilter().length == 0)) {
			if((getF_org() != null && !getF_org().equals("")) && (getF_wlocation() == null || getF_wlocation().length == 0) && (getCheckStatus_reportfilter() == null || getCheckStatus_reportfilter().length == 0)) {
				StringBuilder sbQuery = new StringBuilder(); 
				sbQuery.append("select emp_per_id from candidate_personal_details where org_id = ? ");
				pst = con.prepareStatement(sbQuery.toString());
				pst.setInt(1, uF.parseToInt(getF_org()));
//				System.out.println("4 pst===> "+pst);
				rst = pst.executeQuery();
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
				if(sbRecruitID != null && !sbRecruitID.toString().equals("")) {
					sbQuery.append(" and recruitment_id in("+sbRecruitID.toString()+") ");
				}
				if(getCheckStatus_reportfilter() != null && getCheckStatus_reportfilter().length > 0){
//					sbQuery.append(" and application_status = "+getCheckStatus_reportfilter()+" ");
					sbQuery.append(" and application_status in ("+StringUtils.join(getCheckStatus_reportfilter(), ",")+") ");
				}
				pst = con.prepareStatement(sbQuery.toString());
//				System.out.println("5 pst===> "+pst);
				rst = pst.executeQuery();
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
			
//			int skillCnt =0;
			for(int i=0; lstSkills != null && !lstSkills.isEmpty() && i < lstSkills.size(); i++) {
				pst = con.prepareStatement("select skill_id,emp_id from candidate_skills_description where skill_id in("+lstSkills.get(i)+")");
//				System.out.println("6 pst===> "+pst);
				rst = pst.executeQuery();
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
//			System.out.println("sbEducation ===> "+sbEducation);
//			int eduCnt=0;
			for(int i=0; lstEducation != null && !lstEducation.isEmpty() && i < lstEducation.size(); i++){
				pst = con.prepareStatement("select education_id,emp_id from candidate_education_details where education_id in("+lstEducation.get(i)+")");
//				System.out.println("7 pst===> "+pst);
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
			
//			System.out.println("allFilterCandidate ===>> " + allFilterCandidate);
//			System.out.println("orgLocCandidate ===>> " + orgLocCandidate);
//			System.out.println("skillCandidate ===>> " + skillCandidate);
//			System.out.println("eduCandidate ===>> " + eduCandidate); 
			
			List<String> finalCandiId = new ArrayList<String>();
			for(int i=0; allFilterCandidate!=null && !allFilterCandidate.isEmpty() && i<allFilterCandidate.size(); i++) {
				if(orgLocCandidate != null && !orgLocCandidate.isEmpty() && lstSkills != null && !lstSkills.isEmpty() && lstEducation != null && !lstEducation.isEmpty()) {
					if(orgLocCandidate.contains(allFilterCandidate.get(i)) && skillCandidate.contains(allFilterCandidate.get(i)) && eduCandidate.contains(allFilterCandidate.get(i))) {
						finalCandiId.add(allFilterCandidate.get(i));
					}
//					System.out.println("1");
				} else if(orgLocCandidate != null && !orgLocCandidate.isEmpty() && lstSkills != null && !lstSkills.isEmpty()) {
					if(orgLocCandidate.contains(allFilterCandidate.get(i)) && skillCandidate.contains(allFilterCandidate.get(i))) {
						finalCandiId.add(allFilterCandidate.get(i));
					}
//					System.out.println("2");
				} else if(orgLocCandidate != null && !orgLocCandidate.isEmpty() && lstEducation != null && !lstEducation.isEmpty()) {
					if(orgLocCandidate.contains(allFilterCandidate.get(i)) && eduCandidate.contains(allFilterCandidate.get(i))) {
						finalCandiId.add(allFilterCandidate.get(i));
					}
//					System.out.println("3");
				} else if(lstSkills != null && !lstSkills.isEmpty() && lstEducation != null && !lstEducation.isEmpty()) {
					if(skillCandidate.contains(allFilterCandidate.get(i)) && eduCandidate.contains(allFilterCandidate.get(i))) {
						finalCandiId.add(allFilterCandidate.get(i));
					}
//					System.out.println("4");
				} else {
					finalCandiId.add(allFilterCandidate.get(i));
//					System.out.println("5");
				}
			}
			StringBuilder sbFinalCadiIDS = null;
//			System.out.println("finalCandiId ===>> " + finalCandiId);
			for(int a=0; finalCandiId != null && !finalCandiId.isEmpty() && a<finalCandiId.size(); a++) {
				if(sbFinalCadiIDS == null) {
					sbFinalCadiIDS = new StringBuilder();
					sbFinalCadiIDS.append(finalCandiId.get(a));
				} else {
					sbFinalCadiIDS.append(","+finalCandiId.get(a));
				}
			}
//			System.out.println("sbFinalCadiIDS ===> " + sbFinalCadiIDS.toString());
			List<String> alInner;
			List<List<String>> al = new ArrayList<List<String>>();
			
//			System.out.println("getF_org() ===>> " + getF_org());
//			System.out.println("getF_wlocation() ===>> " + getF_wlocation());
//			System.out.println("allFilterCandidate ===>> " + allFilterCandidate);
//			System.out.println("lstSkills ===>> " + lstSkills);
//			System.out.println("lstEducation ===>> " + lstEducation);
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
			
			StringBuilder query=new StringBuilder();
			if((getF_org()!=null && !getF_org().equals("")) || (getF_wlocation()!=null && getF_wlocation().length > 0) || (allFilterCandidate != null && !allFilterCandidate.isEmpty()) || (lstSkills != null && !lstSkills.isEmpty()) || (lstEducation!= null && !lstEducation.isEmpty())) {
//				System.out.println("if ===> ");
				query.append("select years,emp_per_id,emp_fname,emp_mname, emp_lname,emp_date_of_birth,emp_city_id,emp_email,emp_pan_no,current_ctc," +
						"expected_ctc,notice_period,emp_image,availability_for_interview " +
						"from candidate_personal_details cpd left join (select sum(to_date-from_date)/365 as years ,emp_id from " +
						"candidate_prev_employment cpe group by emp_id) as a on cpd.emp_per_id = a.emp_id where cpd.emp_per_id>0 and " +
						"cpd.candididate_emp_id is null ");
				if(minDate != null && !minDate.equals("") && maxDate != null && !maxDate.equals("")){
					query.append(" and emp_per_id in(select candidate_id from candidate_application_details where candidate_id in ("+sbFinalCadiIDS.toString()+") and " +
						"application_date between '"+uF.getDateFormat(minDate, DATE_FORMAT)+"' and '" +uF.getDateFormat(maxDate, DATE_FORMAT)+"') ");
				} else if(sbFinalCadiIDS != null && !sbFinalCadiIDS.toString().equals("")){
					query.append(" and emp_per_id in("+sbFinalCadiIDS.toString()+") ");
				} else {
					query.append(" and emp_per_id in(0) ");
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

//				if(getStrExperience()!=null && uF.parseToInt(getStrExperience())!=0){
//				    if(uF.parseToInt(getStrExperience())==1)
//				    	query.append(" and years<=1 and years>=0");
//				    else if(uF.parseToInt(getStrExperience())==2)
//				    	query.append(" and years<2 and years>=1");
//				    else if(uF.parseToInt(getStrExperience())==3)
//				    	query.append(" and years<5 and years>=2");
//			    	else if(uF.parseToInt(getStrExperience())==4)
//			    		query.append(" and years=10 and years>=5");
//		    		else if(uF.parseToInt(getStrExperience())==5)
//		    			query.append(" and years>=10");
//				} 
				if (getStrExperience() != null && getStrExperience().length > 0) {

					StringBuilder sbExp = null; 
					for (int i = 0; i < getStrExperience().length; i++) {
						if (getStrExperience()[i] != null && !getStrExperience()[i].trim().equals("")) {
							if(uF.parseToInt(getStrExperience()[i].trim())==1){
								if(sbExp == null){
									sbExp = new StringBuilder();
									sbExp.append(" (years<=1)");
								} else {
									sbExp.append(" or (years<=1)");
								}
							} else if(uF.parseToInt(getStrExperience()[i].trim())==2) {
								if(sbExp == null){
									sbExp = new StringBuilder();
									sbExp.append(" (years<=2)");
								} else {
									sbExp.append(" or (years<=2)");
								}
							} else if(uF.parseToInt(getStrExperience()[i].trim())==3) {
								if(sbExp == null){
									sbExp = new StringBuilder();
									sbExp.append(" (years<=5)");
								} else {
									sbExp.append(" or (years<=5)");
								}
							} else if(uF.parseToInt(getStrExperience()[i].trim())==4) {
								if(sbExp == null){
									sbExp = new StringBuilder();
									sbExp.append(" (years<=10)");
								} else {
									sbExp.append(" or (years<=10)");
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
//				System.out.println("else ===> ");
				query.append("select years,emp_per_id,emp_fname,emp_mname,emp_lname,emp_date_of_birth,emp_city_id,emp_email,emp_pan_no,current_ctc," +
						"expected_ctc,notice_period,emp_image,availability_for_interview " +
						"from candidate_personal_details cpd left join (select sum(to_date-from_date)/365 as years ,emp_id from " +
						"candidate_prev_employment cpe group by emp_id) as a on cpd.emp_per_id = a.emp_id where cpd.emp_per_id>0 and " +
						"cpd.candididate_emp_id is null ");
				if(minDate != null && !minDate.equals("") && maxDate != null && !maxDate.equals("")){
					query.append(" and emp_per_id in(select candidate_id from candidate_application_details where " +
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
				
				if (getStrExperience() != null && getStrExperience().length > 0) {

					StringBuilder sbExp = null; 
					for (int i = 0; i < getStrExperience().length; i++) {
						if (getStrExperience()[i] != null && !getStrExperience()[i].trim().equals("")) {
							if(uF.parseToInt(getStrExperience()[i].trim())==1){
								if(sbExp == null){
									sbExp = new StringBuilder();
									sbExp.append(" (years<=1)");
								} else {
									sbExp.append(" or (years<=1)");
								}
							} else if(uF.parseToInt(getStrExperience()[i].trim())==2) {
								if(sbExp == null){
									sbExp = new StringBuilder();
									sbExp.append(" (years<=2 )");
								} else {
									sbExp.append(" or (years<=2)");
								}
							} else if(uF.parseToInt(getStrExperience()[i].trim())==3) {
								if(sbExp == null){
									sbExp = new StringBuilder();
									sbExp.append(" (years<=5)");
								} else {
									sbExp.append(" or (years<=5 )");
								}
							} else if(uF.parseToInt(getStrExperience()[i].trim())==4) {
								if(sbExp == null){
									sbExp = new StringBuilder();
									sbExp.append(" (years <=10)");
								} else {
									sbExp.append(" or (years <=10)");
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
			
			
//			query.append(" order by emp_fname,emp_lname"); 
			query.append(" order by emp_entry_date desc");
			pst = con.prepareStatement(query.toString());
//			System.out.println("XML pst ===> "+pst);
			rst = pst.executeQuery();

			
			StringBuilder sbCandidateIds = new StringBuilder(); 
			String oldEmp =null;
//			Map<String, Map<String, String>> hmMapCandidateDetails = new LinkedHashMap<String, Map<String, String>>();
			Map<String,List<String>>hmMapCandidateDetails = new HashMap<String, List<String>>();
			while (rst.next()) {
			
				if(oldEmp==null || (oldEmp!=null && !oldEmp.equals(rst.getString("emp_per_id")))){
					 alInner=new ArrayList<String>();	

					if(sbCandidateIds.length()==0){
						sbCandidateIds.append(rst.getString("emp_per_id"));
					}else{
						sbCandidateIds.append(","+rst.getString("emp_per_id"));
					}
					alInner.add(rst.getString("emp_per_id"));//0
				
					String strEmpMName = "";
					if(flagMiddleName) {
						if(rst.getString("emp_mname") != null && rst.getString("emp_mname").trim().length()>0) {
							strEmpMName = " "+rst.getString("emp_mname");
						}
					}
				
					alInner.add(rst.getString("emp_fname") +strEmpMName+ " " + rst.getString("emp_lname"));//1
				
					alInner.add(rst.getString("emp_date_of_birth"));					//2
					alInner.add(uF.showData(rst.getString("emp_city_id"),"-")); //3
					
					Map<String, String> hmLastJobData = getLastJobData(con, uF, rst.getString("emp_per_id"));
					alInner.add(uF.showData(hmLastJobData.get("JOB_NAME"), "-")); //4
					alInner.add(uF.showData(hmLastJobData.get("LAST_APPLIED_DATE"), "-")); //5
					alInner.add(uF.showData(rst.getString("current_ctc"), "N/A")); //6
					alInner.add(uF.showData(rst.getString("expected_ctc"), "N/A")); //7
					if(rst.getString("notice_period") != null && !rst.getString("notice_period").equals("")) {
						alInner.add(rst.getString("notice_period")+" days"); //8
					} else {
						alInner.add("N/A"); //8
					}
					
					int expYear = uF.parseToInt(hmCandidateExperience1.get(rst.getString("emp_per_id")+"_YEAR"));
					int expMonth = uF.parseToInt(hmCandidateExperience1.get(rst.getString("emp_per_id")+"_MONTH"));
					int expDay = uF.parseToInt(hmCandidateExperience1.get(rst.getString("emp_per_id")+"_DAY"));
					if (getStrExperience() != null && getStrExperience().length > 0) {
//						System.out.println("employee exp emp_id==>"+ rst.getString("emp_per_id")+"==>year==>"+expYear+"==>month==>"+expMonth+"==>days==>"+expDay);
						
						for (int i = 0; i < getStrExperience().length; i++) {
							if (getStrExperience()[i] != null && !getStrExperience()[i].trim().equals("")) {
								if(uF.parseToInt(getStrExperience()[i].trim())==1){
									if((expYear == 0 && expYear < 1) || (expYear == 1 && expMonth == 0 && expDay == 0)) {
										hmMapCandidateDetails.put(rst.getString("emp_per_id"), alInner);
									}
								} else if(uF.parseToInt(getStrExperience()[i].trim())==2) {
									if((expYear >= 1 && expYear < 2) || (expYear == 2 && expMonth == 0 && expDay == 0)) {
										hmMapCandidateDetails.put(rst.getString("emp_per_id"), alInner);
									}
								} else if(uF.parseToInt(getStrExperience()[i].trim())==3) {
									if((expYear >= 2 && expYear < 5) || (expYear == 5 && expMonth == 0 && expDay == 0)) {
										hmMapCandidateDetails.put(rst.getString("emp_per_id"), alInner);
									}
								} else if(uF.parseToInt(getStrExperience()[i].trim())==4) {
									if((expYear >= 5 && expYear < 10) || (expYear == 10 && expMonth == 0 && expDay == 0)) {
										hmMapCandidateDetails.put(rst.getString("emp_per_id"), alInner);
									}
								} else if(uF.parseToInt(getStrExperience()[i].trim())==5){
									if((expYear >= 10) ) {
										hmMapCandidateDetails.put(rst.getString("emp_per_id"), alInner);
									}
								}
							}
						}
					} else{
						hmMapCandidateDetails.put(rst.getString("emp_per_id"), alInner);
					}
					
					
					oldEmp=rst.getString("emp_per_id");
				}
				
			}
			rst.close();
			pst.close();
			
			StringBuilder sbNewCandidate = null;
			for(int i=0;al!=null && i<al.size(); i++){
				List<String> innerList = (List<String>) al.get(i);
				if(sbNewCandidate == null){
					sbNewCandidate = new StringBuilder();
					sbNewCandidate.append(innerList.get(2));
				} else {
					sbNewCandidate.append(","+innerList.get(2));
				}
			}
			if(sbNewCandidate!=null){
				List<String> alNewCandidate = new ArrayList<String>();
				pst=con.prepareStatement("select emp_per_id from candidate_personal_details where emp_entry_date>=? and emp_per_id in ("+sbNewCandidate.toString()+")");
				pst.setDate(1, uF.getPrevDate(CF.getStrTimeZone(), 10));
//				System.out.println("new Candidate pst======>"+pst);
				rst = pst.executeQuery();
				while(rst.next()){
					alNewCandidate.add(rst.getString("emp_per_id"));
				}
				rst.close();
				pst.close();
				request.setAttribute("alNewCandidate", alNewCandidate);
			}

			request.setAttribute("jobcode", getJobcode());
			request.setAttribute("reportList", al);
			
			Map<String,String> hmCandidateExperience=new HashMap<String, String>();
			Map<String, String> hmEducationDetails = new HashMap<String, String>();
			Map<String, String> hmSkillDetails = new HashMap<String, String>();
			
			
			if(sbCandidateIds.length()>=1) {
			
			pst = con.prepareStatement("select * from candidate_education_details where emp_id in ("+sbCandidateIds.toString()+") order by emp_id");
//			System.out.println("9 pst===> "+pst);
			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			String strCandidateIdOld = null;
			String strCandidateIdNew = null;
			StringBuilder sbContainer = new StringBuilder();

			Map<String, String> hmDegreeName = CF.getDegreeNameMap(con);
			
			while(rst.next()) {

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
//			System.out.println("hmEducationDetails ===>> " + hmEducationDetails);
			

			pst = con.prepareStatement("select * from candidate_skills_description where emp_id in ("+sbCandidateIds.toString()+") order by emp_id");
//			System.out.println("10 pst===> "+pst);
			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			strCandidateIdOld = null;
			strCandidateIdNew = null;
			sbContainer.replace(0, sbContainer.length(), "");
			Map<String, String> hmSkillsName = CF.getSkillNameMap(con);
			while(rst.next()){
				strCandidateIdNew = rst.getString("emp_id");
				if (strCandidateIdNew != null && !strCandidateIdNew.equals(strCandidateIdOld)) {
					sbContainer.replace(0, sbContainer.length(), "");
				}
				
				if (strCandidateIdNew != null && strCandidateIdNew.equals(strCandidateIdOld)) {
					sbContainer.append(", " + hmSkillsName.get(rst.getString("skill_id")));
				} else {
					sbContainer.append(hmSkillsName.get(rst.getString("skill_id")));
				}
				hmSkillDetails.put(strCandidateIdNew, sbContainer.toString());
				strCandidateIdOld = strCandidateIdNew;
			}
			rst.close();
			pst.close();

			// Logic for multiple experience
			
			pst = con.prepareStatement("select * from candidate_prev_employment where emp_id in ("+sbCandidateIds.toString()+") order by emp_id");
//			System.out.println("11 pst===> "+pst);
			rst=pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			strCandidateIdOld = null;
			strCandidateIdNew = null;
			int noyear = 0,nomonth = 0,nodays = 0;
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
			    		
			    	String yearsLbl = " Years ";
			    	if(noyear == 1) {
			    		yearsLbl = " Year ";
			    	}
			    	
			    	String monthLbl = " Months ";
			    	if(nomonth == 1) {
			    		monthLbl = " Month ";
			    	}
			    	
			    	hmCandidateExperience.put(rst.getString("emp_id"),""+noyear+yearsLbl+nomonth+monthLbl); 
			    	
			    	strCandidateIdOld = strCandidateIdNew;
			}
			rst.close();
			pst.close();
			
			}
			
			//			System.out.println("allist----------------------------------->"+hmMapCandidateDetails);
			Map<String, Map<String, String>> hmMapCandidateDetailsExport = new LinkedHashMap<String, Map<String, String>>();
			Iterator<String> it1 = hmMapCandidateDetails.keySet().iterator();
		 
		   	while(it1.hasNext()){
		   		String strEmpId = it1.next();
		   		
		   		List<String> strCandidateDetails = hmMapCandidateDetails.get(strEmpId);
		   		
		   		Map<String, String> hmMapInnerCandidateDetails = new LinkedHashMap<String, String>();
		   		hmMapInnerCandidateDetails.put("CandidateName", strCandidateDetails.get(1));
		   		hmMapInnerCandidateDetails.put("Education", uF.showData((String)hmEducationDetails.get((String)strCandidateDetails.get(0)), "N/A"));
		   		hmMapInnerCandidateDetails.put("Skills", uF.showData((String)hmSkillDetails.get((String)strCandidateDetails.get(0)), "N/A"));
		   		hmMapInnerCandidateDetails.put("Experience",uF.showData((String)hmCandidateExperience.get((String)strCandidateDetails.get(0)), "N/A"));
		   		hmMapInnerCandidateDetails.put("CurrentCTC", strCandidateDetails.get(6));
		   		hmMapInnerCandidateDetails.put("ExpectedCTC", strCandidateDetails.get(7));
		   		hmMapInnerCandidateDetails.put("NoticePeriod", strCandidateDetails.get(8));
		   		
		   		hmMapInnerCandidateDetails.put("LastAppliedUpdated", strCandidateDetails.get(5));
		   		hmMapInnerCandidateDetails.put("LastJobTitle", strCandidateDetails.get(4));
				
		   		hmMapCandidateDetailsExport.put(strEmpId,hmMapInnerCandidateDetails);
		   		
		   		
		   	}
//		   	System.out.println("hmMapCandidateDetailsExport---"+hmMapCandidateDetailsExport);
						
			createXML(hmMapCandidateDetailsExport);
						
		} catch (Exception e) {
			e.printStackTrace();
			
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}
	
	public Map<String,String> getCandidateExpMap(Connection con,UtilityFunctions uF) {
		PreparedStatement pst = null;
		ResultSet rst = null;
		Map<String,String> hmCandidateExperience = new HashMap<String,String>();
		try {
			pst = con.prepareStatement("select * from candidate_prev_employment order by emp_id");
//			System.out.println("exp pst==>"+pst);
			rst=pst.executeQuery();
			String strCandidateIdOld = null;
			String strCandidateIdNew = null;
			int noyear = 0,nomonth = 0,nodays = 0;
			while(rst.next()) {
				
					if(rst.getString("from_date") != null && !rst.getString("from_date").equals("") && rst.getString("to_date") != null && !rst.getString("to_date").equals("")) {
//						System.out.println("empId==>"+rst.getString("emp_id")+"==>fromDate==>"+rst.getString("from_date")+"==>todate==>"+rst.getString("to_date"));
						strCandidateIdNew = rst.getString("emp_id");
						if(strCandidateIdNew!=null && !strCandidateIdNew.equals(strCandidateIdOld)) {
						
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
				    	
				    	hmCandidateExperience.put(rst.getString("emp_id")+"_YEAR",""+noyear); 
				    	hmCandidateExperience.put(rst.getString("emp_id")+"_MONTH",""+nomonth); 
				    	hmCandidateExperience.put(rst.getString("emp_id")+"_DAY",""+nodays); 
				    	strCandidateIdOld = strCandidateIdNew;
				}
			}			    	
			rst.close();
			pst.close();
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
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
		return hmCandidateExperience;
	}
	
	

	private Map<String, String> getLastJobData(Connection con, UtilityFunctions uF, String candidateId) {
		PreparedStatement pst = null;
		ResultSet rst = null;
		Map<String, String> hmLastJobData = new HashMap<String, String>();
		try {

			pst=con.prepareStatement("select cad.application_date,rd.job_code,rd.job_title from candidate_application_details cad, recruitment_details rd " +
					" where cad.recruitment_id = rd.recruitment_id and candidate_id = ? order by candi_application_deatils_id desc limit 1");
			pst.setInt(1, uF.parseToInt(candidateId));
			rst=pst.executeQuery();

			while(rst.next()){
				hmLastJobData.put("JOB_NAME", rst.getString("job_title"));
				hmLastJobData.put("LAST_APPLIED_DATE", uF.getDateFormat(rst.getString("application_date"), DBDATE, CF.getStrReportDateFormat()));
			}
			rst.close();
			pst.close();
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
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

	public String getF_org() {
		return f_org;
	}

	public void setF_org(String f_org) {
		this.f_org = f_org;
	}

	
	
	public String[] getCheckStatus_reportfilter() {
		return checkStatus_reportfilter;
	}

	public void setCheckStatus_reportfilter(String[] checkStatus_reportfilter) {
		this.checkStatus_reportfilter = checkStatus_reportfilter;
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
	
	
	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getCandidateId() {
		return candidateId;
	}

	public void setCandidateId(String candidateId) {
		this.candidateId = candidateId;
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

	public List<FillText> getAlEmail() {
		return alEmail;
	}

	public void setAlEmail(List<FillText> alEmail) {
		this.alEmail = alEmail;
	}

	public List<FillText> getAlPanCard() {
		return alPanCard;
	}

	public void setAlPanCard(List<FillText> alPanCard) {
		this.alPanCard = alPanCard;
	}

	public String getF_pancard() {
		return f_pancard;
	}

	public void setF_pancard(String f_pancard) {
		this.f_pancard = f_pancard;
	}

	public String getF_email() {
		return f_email;
	}

	public void setF_email(String f_email) {
		this.f_email = f_email;
	}

	public String getRecruitId() {
		return recruitId;
	}

	public void setRecruitId(String recruitId) {
		this.recruitId = recruitId;
	}

	public String getJobcode() {
		return jobcode;
	}

	public void setJobcode(String jobcode) {
		this.jobcode = jobcode;
	}

	public String getEmpName() {
		return empName;
	}

	public void setEmpName(String empName) {
		this.empName = empName;
	}

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
	}

}

