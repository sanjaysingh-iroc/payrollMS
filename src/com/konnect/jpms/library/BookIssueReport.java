package com.konnect.jpms.library;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class BookIssueReport extends ActionSupport implements ServletRequestAware, IStatements {
	private static final long serialVersionUID = 1L;
	public HttpSession session;
	String strSessionOrgId;
	String strSessionEmpId;
	String strUserType;
	
	public CommonFunctions CF;
	
	private String f_org;
	private String[] f_wlocation;
	
	List<FillWLocation> wLocationList;
	List<FillOrganisation> organisationList;
	
	String strSearchJob;
	String dataType;
	private static Logger log = Logger.getLogger(BookReport.class);
	
	public String execute() throws Exception{
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)	return LOGIN;

		strUserType = (String)session.getAttribute(USERTYPE);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		
		UtilityFunctions uF = new UtilityFunctions();
		if(getF_org() == null){
			setF_org((String)session.getAttribute(ORGID));
		}
		request.setAttribute(PAGE, PBookIssueReport);
		request.setAttribute(TITLE, TBookIssueReport);
		
//		loadEmployee(uF);
//		getSelectedFilter(uF);
//		getSearchAutoCompleteData(uF);
		
		if(getDataType() == null || getDataType().trim().equals("") || getDataType().trim().equalsIgnoreCase("NULL")) {
			setDataType("I");    
		}

		if(getDataType()!=null && getDataType().trim().equals("I")){
			getIssueBookRequest(uF);
		}
		
		if(getDataType()!=null && getDataType().trim().equals("R")){
			getReturnBookDetails(uF);
		}
		
		return SUCCESS;
	}
	
	private String loadEmployee(UtilityFunctions uF) {
		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)) {
				wLocationList = new FillWLocation(request).fillWLocation(getF_org(), (String)session.getAttribute(WLOCATION_ACCESS));
				organisationList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
		} else {
				organisationList = new FillOrganisation(request).fillOrganisation();
				wLocationList = new FillWLocation(request).fillWLocation(getF_org());
		}
		return LOAD;
	}

	private void getSelectedFilter(UtilityFunctions uF) {
		Map<String,String> hmFilter=new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();
		
		alFilter.add("ORGANISATION");
		if(getF_org()!=null)  {
			String strOrg="";
			int k=0;
			for(int i=0;organisationList!=null && i<organisationList.size();i++){
				if(getF_org().equals(organisationList.get(i).getOrgId())) {
					if(k==0) {
						strOrg=organisationList.get(i).getOrgName();
					} else {
						strOrg+=", "+organisationList.get(i).getOrgName();
					}
					k++;
				}
			}
			if(strOrg!=null && !strOrg.equals("")) {
				hmFilter.put("ORGANISATION", strOrg);
			} else {
				hmFilter.put("ORGANISATION", "All Organisation");
			}
			
		} else {
			hmFilter.put("ORGANISATION", "All Organisation");
		}
		
		alFilter.add("LOCATION");
		if(getF_wlocation()!=null) {
			String strLocation="";
			int k=0;
			for(int i=0;wLocationList!=null && i<wLocationList.size();i++) {
				for(int j=0;j<getF_wlocation().length;j++) {
					if(getF_wlocation()[j].equals(wLocationList.get(i).getwLocationId())) {
						if(k==0) {
							strLocation=wLocationList.get(i).getwLocationName();
						} else {
							strLocation+=", "+wLocationList.get(i).getwLocationName();
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
		
		
		String selectedFilter=CF.getSelectedFilter1(CF,uF,alFilter,hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}
	
	private void getSearchAutoCompleteData(UtilityFunctions uF) {
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			con = db.makeConnection(con);
			
			SortedSet<String> setSearchList = new TreeSet<String>();
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from book_details where ");
			if(getF_org()!=null && !getF_org().equals("")){
				sbQuery.append(" org_id in ("+getF_org()+")");
			}else{
				sbQuery.append(" org_id in ("+(String)session.getAttribute(ORGID)+")");
			}
			
			if(getF_wlocation() != null && !getF_wlocation().equals("")){
				sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_wlocation(), ",")+")");
			}
			
			if(getStrSearchJob()!=null && !getStrSearchJob().trim().equals("")){
				sbQuery.append(" and (upper(book_title) like '%"+getStrSearchJob().trim().toUpperCase()+"%' or upper(book_category) like '%"+getStrSearchJob().trim().toUpperCase()+"%' or upper(book_author) like '%"+getStrSearchJob().trim().toUpperCase()+"%' )");
			}
			
			sbQuery.append(" order by book_title");
			pst = con.prepareStatement(sbQuery.toString());
		
			rs = pst.executeQuery();
			while (rs.next()) {
				setSearchList.add(rs.getString("book_title"));
				if(rs.getString("book_category") !=null && !rs.getString("book_category").trim().equals("")){
					setSearchList.add(rs.getString("book_category").trim());
				}
				if(rs.getString("book_author") !=null && !rs.getString("book_author").trim().equals("")){
					setSearchList.add(rs.getString("book_author").trim());
				}
			}
			rs.close();
			pst.close();
			
			StringBuilder sbData = null;
			Iterator<String> it = setSearchList.iterator();
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
  	private void getIssueBookRequest(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try{
			
			con = db.makeConnection(con);
			Map<String, String> hmOrgName = CF.getOrgName(con);
			Map<String, String> hmEmpNames = CF.getEmpNameMap(con,null, null);
			Map<String, String> hmEmpWLocation = CF.getWLocationMap(con,null,request,null); 
			
			Map<String,List<String>> hmBookIssuesDetails = new HashMap<String,List<String>>();
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from book_details bd,book_issues_returns br where bd.book_id = br.book_id and issue_status = 0 ");
			if(getF_org()!=null && !getF_org().equals("")){
				sbQuery.append("  and org_id in ("+getF_org()+")");
			}else{
				sbQuery.append("  and org_id in ("+(String)session.getAttribute(ORGID)+")");
			}
			
			if(getF_wlocation()!=null && !getF_wlocation().equals("")){
				sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_wlocation(), ",")+")");
			}
				
			if(getStrSearchJob()!=null && !getStrSearchJob().trim().equals("")){
				sbQuery.append(" and (upper(book_title) like '%"+getStrSearchJob().trim().toUpperCase()+"%' or upper(book_category) like '%"+getStrSearchJob().trim().toUpperCase()+"%' or upper(book_author) like '%"+getStrSearchJob().trim().toUpperCase()+"%' )");
			}
		
			sbQuery.append(" order by book_title ");
			
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();
			while(rs.next()) {
				List<String> alInner = new ArrayList<String>();
				alInner.add(uF.showData(rs.getString("book_id"),"-"));//0
				alInner.add(uF.showData(rs.getString("book_title"),"-"));//1
				alInner.add(uF.showData(rs.getString("book_author"),"-"));//2
				alInner.add(uF.showData(rs.getString("book_category"),"-"));//3
				alInner.add(uF.showData(rs.getString("book_quantity"),""));//4
				alInner.add(rs.getString("book_issued_id"));//5
				alInner.add(rs.getString("requested_by"));//6
				alInner.add(uF.showData(rs.getString("request_quantity"),""));//7
				alInner.add(uF.getDateFormat(rs.getString("from_date"), DBDATE, CF.getStrReportDateFormat()));//8
				alInner.add(uF.getDateFormat(rs.getString("to_date"), DBDATE, CF.getStrReportDateFormat()));//9
				alInner.add(uF.showData(hmEmpNames.get(rs.getString("requested_by")),""));//10
				
				String extenstion = null;
				if(rs.getString("book_image") !=null && !rs.getString("book_image").trim().equals("")) {
					extenstion = FilenameUtils.getExtension(rs.getString("book_image").trim());
				}
				alInner.add(extenstion);//11
//				
				String bookImgPath = "";
				if(rs.getString("book_image")!=null && !rs.getString("book_image").equals("")) {
					if(CF.getStrDocSaveLocation()==null) {
						bookImgPath =  DOCUMENT_LOCATION +"/"+rs.getString("added_by")+"/"+rs.getString("book_id")+"/"+rs.getString("book_image") ;
					} else {
						bookImgPath = CF.getStrDocRetriveLocation()+I_BOOKS+"/"+rs.getString("added_by")+"/"+rs.getString("book_id")+"/"+rs.getString("book_image");
					}
				}
				String bookImage = "<img class='lazy' border=\"0\" style=\"height:40px; width:40%; border: 1px solid #CCCCCC; padding: 5px;\" src=\""+bookImgPath+"\" data-original=\""+bookImgPath+"\" />";
				alInner.add(bookImage); //12
				alInner.add(uF.getDateFormat(rs.getString("request_date"), DBDATE, CF.getStrReportDateFormat()));//13
				hmBookIssuesDetails.put(rs.getString("book_id")+"_"+rs.getString("requested_by"), alInner);
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmBookIssuesDetails",hmBookIssuesDetails);
						
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
  	private void getReturnBookDetails(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try{
			
			con = db.makeConnection(con);
			Map<String, String> hmOrgName = CF.getOrgName(con);
			Map<String, String> hmEmpNames = CF.getEmpNameMap(con,null, null);
			Map<String, String> hmEmpWLocation = CF.getWLocationMap(con,null,request,null); 
			
			Map<String,List<String>> hmReturnBookDetails = new HashMap<String,List<String>>();
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from book_details bd,book_issues_returns br where bd.book_id = br.book_id and issue_status = 1 and return_status is null ");
			if(getF_org()!=null && !getF_org().equals("")){
				sbQuery.append("  and org_id in ("+getF_org()+")");
			}else{
				sbQuery.append("  and org_id in ("+(String)session.getAttribute(ORGID)+")");
			}
			
			if(getF_wlocation()!=null && !getF_wlocation().equals("")){
				sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_wlocation(), ",")+")");
			}
				
			if(getStrSearchJob()!=null && !getStrSearchJob().trim().equals("")){
				sbQuery.append(" and (upper(book_title) like '%"+getStrSearchJob().trim().toUpperCase()+"%' or upper(book_category) like '%"+getStrSearchJob().trim().toUpperCase()+"%' or upper(book_author) like '%"+getStrSearchJob().trim().toUpperCase()+"%' )");
			}
		
			sbQuery.append(" order by book_title ");
			
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();
			while(rs.next()) {
				List<String> alInner = new ArrayList<String>();
				alInner.add(uF.showData(rs.getString("book_id"),"-"));//0
				alInner.add(uF.showData(rs.getString("book_title"),"-"));//1
				alInner.add(uF.showData(rs.getString("book_author"),"-"));//2
				alInner.add(uF.showData(rs.getString("book_category"),"-"));//3
				alInner.add(rs.getString("book_issued_id"));//4
				alInner.add(rs.getString("requested_by"));//5
				alInner.add(uF.showData(rs.getString("book_issued_quantity"),""));//6
				alInner.add(uF.getDateFormat(rs.getString("to_date"), DBDATE, CF.getStrReportDateFormat()));//7
				alInner.add(uF.showData(hmEmpNames.get(rs.getString("requested_by")),""));//8
				alInner.add(uF.getDateFormat(rs.getString("issued_date"), DBDATE, CF.getStrReportDateFormat()));//9
				alInner.add(uF.showData(rs.getString("issue_comment"),""));//10
				String extenstion = null;
				if(rs.getString("book_image") !=null && !rs.getString("book_image").trim().equals("")) {
					extenstion = FilenameUtils.getExtension(rs.getString("book_image").trim());
				}
				alInner.add(extenstion);//11
//				
				String bookImgPath = "";
				if(rs.getString("book_image")!=null && !rs.getString("book_image").equals("")) {
					if(CF.getStrDocSaveLocation()==null) {
						bookImgPath =  DOCUMENT_LOCATION +"/"+rs.getString("added_by")+"/"+rs.getString("book_id")+"/"+rs.getString("book_image") ;
					} else {
						bookImgPath = CF.getStrDocRetriveLocation()+I_BOOKS+"/"+rs.getString("added_by")+"/"+rs.getString("book_id")+"/"+rs.getString("book_image");
					}
				}
				String bookImage = "<img class='lazy' border=\"0\" style=\"height:40px; width:40%; border: 1px solid #CCCCCC; padding: 5px;\" src=\""+bookImgPath+"\" data-original=\""+bookImgPath+"\" />";
				alInner.add(bookImage); //12
				
				hmReturnBookDetails.put(rs.getString("book_id")+"_"+rs.getString("requested_by"), alInner);
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmReturnBookDetails",hmReturnBookDetails);
						
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	private String loadReportData(UtilityFunctions uF) {
		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)) {
				wLocationList = new FillWLocation(request).fillWLocation(getF_org(), (String)session.getAttribute(WLOCATION_ACCESS));
				organisationList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
		} else {
				organisationList = new FillOrganisation(request).fillOrganisation();
				wLocationList = new FillWLocation(request).fillWLocation(getF_org());
		}
		return LOAD;
	}
	
	public HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public String getF_org() {
		return f_org;
	}

	public void setF_org(String f_org) {
		this.f_org = f_org;
	}

	public String[] getF_wlocation() {
		return f_wlocation;
	}

	public void setF_wlocation(String[] f_wlocation) {
		this.f_wlocation = f_wlocation;
	}

	public List<FillWLocation> getwLocationList() {
		return wLocationList;
	}

	public void setwLocationList(List<FillWLocation> wLocationList) {
		this.wLocationList = wLocationList;
	}

	public List<FillOrganisation> getOrganisationList() {
		return organisationList;
	}

	public void setOrganisationList(List<FillOrganisation> organisationList) {
		this.organisationList = organisationList;
	}

	public String getStrSearchJob() {
		return strSearchJob;
	}

	public void setStrSearchJob(String strSearchJob) {
		this.strSearchJob = strSearchJob;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	
	
}
