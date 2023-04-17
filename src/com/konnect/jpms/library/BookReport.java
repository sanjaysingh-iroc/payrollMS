package com.konnect.jpms.library;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
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

import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillEducation;
import com.konnect.jpms.select.FillEmployeeStatus;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UserAlerts;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class BookReport extends ActionSupport implements ServletRequestAware, IStatements {
	private static final long serialVersionUID = 1L;
	public HttpSession session;
	String strSessionOrgId;
	String strSessionEmpId;
	String strUserType;
	
	public CommonFunctions CF;
	
	private String f_org;
	private String[] f_wlocation;
		
	private List<FillWLocation> wLocationList;
	private List<FillOrganisation> organisationList;
	
	private String strSearchJob;
	private String dataType;
	
	private String bookId;
	
	private String operation;
	
	private String currPage;
	private String minLimit;
	
	private String alertID;
	
	private static Logger log = Logger.getLogger(BookReport.class);
	
	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)	return LOGIN;

		strUserType = (String)session.getAttribute(USERTYPE);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		
		StringBuilder sbpageTitleNaviTrail = new StringBuilder();
		sbpageTitleNaviTrail.append("<li><i class=\"fa fa-wrench\"></i><a href=\"Library.action\" style=\"color: #3c8dbc;\"> Utility</a></li>" +
			"<li class=\"active\">Library</li>");
		request.setAttribute("PAGETITLE_NAVITRAIL", sbpageTitleNaviTrail.toString());
		
		UtilityFunctions uF = new UtilityFunctions();
		if(getF_org() == null) {
			setF_org((String)session.getAttribute(ORGID));
		}
		request.setAttribute(PAGE, PBookReport);
		request.setAttribute(TITLE, "Library");
		
		String strDomain = request.getServerName().split("\\.")[0];
		CF.deleteWRUserAlerts(CF, request, strDomain, getAlertID());
		
//		if(strUserType != null && strUserType.equals(ADMIN)) {
//			updateUserAlerts(uF, LIBRARY_REQUEST_ALERT);
//		} else if(strUserType != null && strUserType.equals(EMPLOYEE)) {
//			updateUserAlerts(uF, LIBRARY_REQUEST_APPROVED_ALERT);
//		}
		
		if(getCurrPage() == null || getCurrPage().equals("") || getCurrPage().equals("null")) {
			setCurrPage("1");
		}
		
		loadEmployee(uF);
		getSelectedFilter(uF);
		getSearchAutoCompleteData(uF);
		
		if(strUserType != null && (strUserType.equals(HRMANAGER) || strUserType.equals(ADMIN))) {
			getBookTypewiseCounts(uF);
		} else if(strUserType != null && strUserType.equals(EMPLOYEE)) {
			getEmpBookTypewiseCounts(uF);
		}
		
		if(getDataType()==null || getDataType().trim().equals("") || getDataType().trim().equalsIgnoreCase("NULL")) {
			setDataType("B");    
		}

		if(getDataType()!=null && getDataType().trim().equals("B")) {
			getBookDetails(uF);
		}
		
		if(getDataType()!=null && getDataType().trim().equals("PR")) {
			getBookPurchaseDetails(uF);
		}
		
		if(getDataType()!=null && getDataType().trim().equals("IR")) {
			getIssueBookRequest(uF);
		}
		
		if(getDataType()!=null && getDataType().trim().equals("IB")) {
			getReturnBookDetails(uF);
		}
		
		if(getDataType()!=null && getDataType().trim().equals("EMPPB")) {
			getEmpPurchasedBookDetails(uF);
		}
		
		if(getDataType()!=null && getDataType().trim().equals("EMPIB")) {
			getEmpIssuedBookDetails(uF);
		}
		
		if(getOperation() != null && getOperation().equals("D")) {
			deleteBook(uF);
//			return VIEW;
		}
		
		return SUCCESS;
	}
	
	
	private void updateUserAlerts(UtilityFunctions uF, String alertType) {
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		int nEmpId = uF.parseToInt((String)session.getAttribute(EMPID));
		try {
			con = db.makeConnection(con);
			String strDomain = request.getServerName().split("\\.")[0];
			UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
			userAlerts.setStrDomain(strDomain);
			userAlerts.setStrEmpId(""+nEmpId);
			userAlerts.set_type(alertType);
			userAlerts.setStatus(UPDATE_ALERT);
			Thread t = new Thread(userAlerts);
			t.run();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeConnection(con);
		}
	}
	
	
	private void getEmpBookTypewiseCounts(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select count(*) as cnt from book_details bd, book_purchases bp where bd.book_id = bp.book_id and request_status = 1 and purchased_by=? ");
			if(getStrSearchJob()!=null && !getStrSearchJob().trim().equals("")) {
				sbQuery.append(" and (upper(book_title) like '%"+getStrSearchJob().trim().toUpperCase()+"%' or upper(book_category) like '%"+getStrSearchJob().trim().toUpperCase()+"%' or upper(book_author) like '%"+getStrSearchJob().trim().toUpperCase()+"%' )");
			}
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(strSessionEmpId));
//			System.out.println("pst ===>> " + pst);  
			rs = pst.executeQuery(); 
			int empPurchasedCnt = 0;
			while(rs.next()) {
				empPurchasedCnt = rs.getInt("cnt");
			}
			rs.close();
			pst.close();
			
			sbQuery = new StringBuilder();
			sbQuery.append("select count(*) as cnt from book_details bd,book_issues_returns br where bd.book_id = br.book_id and issue_status=1 and " +
				"(return_status=0 or return_status is null) and requested_by=? ");
			if(getStrSearchJob()!=null && !getStrSearchJob().trim().equals("")) {
				sbQuery.append(" and (upper(book_title) like '%"+getStrSearchJob().trim().toUpperCase()+"%' or upper(book_category) like '%"+getStrSearchJob().trim().toUpperCase()+"%' or upper(book_author) like '%"+getStrSearchJob().trim().toUpperCase()+"%' )");
			}
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(strSessionEmpId));
			rs = pst.executeQuery();
			int empIssuedCnt = 0;
			while(rs.next()) {
				empIssuedCnt = rs.getInt("cnt");
			}
			rs.close();
			pst.close();
			
			request.setAttribute("empPurchasedCnt", empPurchasedCnt+"");
			request.setAttribute("empIssuedCnt", empIssuedCnt+"");
		
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	private void getBookTypewiseCounts(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select count (*) as cnt from book_details bd, book_purchases bp where bd.book_id = bp.book_id and request_status = 0 ");
			
			if(getF_org()!=null && !getF_org().equals("")) {
				sbQuery.append(" and bd.org_id in ("+getF_org()+")");
			} else {
				if(strUserType != null && strUserType.equals(HRMANAGER)) {
					 if(session.getAttribute(ORG_ACCESS) != null && !session.getAttribute(ORG_ACCESS).equals("")) {
						 sbQuery.append(" and bd.org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
					 } else {
						 sbQuery.append(" and bd.org_id in ("+(String)session.getAttribute(ORGID)+")");
					 }
				} else if(strUserType != null && !strUserType.equals(HRMANAGER) && !strUserType.equals(ADMIN)) {
					sbQuery.append(" and bd.org_id in ("+(String)session.getAttribute(ORGID)+")");
				}
			}
			
			if(getF_wlocation()!=null && !getF_wlocation().equals("")) {
				sbQuery.append(" and bd.wlocation_id in ("+StringUtils.join(getF_wlocation(), ",")+")");
			} else {
				if(strUserType != null && strUserType.equals(HRMANAGER)) {
					 if(session.getAttribute(WLOCATION_ACCESS) != null && !session.getAttribute(WLOCATION_ACCESS).equals("")){
						 sbQuery.append(" and bd.wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
					 } else {
						 sbQuery.append(" and bd.wlocation_id in ("+(String)session.getAttribute(WLOCATIONID)+")");
					 }
				} else if(strUserType != null && !strUserType.equals(HRMANAGER) && !strUserType.equals(ADMIN)) {
					sbQuery.append(" and bd.wlocation_id in ("+(String)session.getAttribute(WLOCATIONID)+")");
				}
			}
			
			if(getStrSearchJob()!=null && !getStrSearchJob().trim().equals("")) {
				sbQuery.append(" and (upper(book_title) like '%"+getStrSearchJob().trim().toUpperCase()+"%' or upper(book_category) like '%"+getStrSearchJob().trim().toUpperCase()+"%' or upper(book_author) like '%"+getStrSearchJob().trim().toUpperCase()+"%' )");
			}
			pst = con.prepareStatement(sbQuery.toString());   
//			System.out.println("pst ===>> " + pst);
			rs = pst.executeQuery();
			int purchaseReqCnt = 0;
			while(rs.next()) {
				purchaseReqCnt = rs.getInt("cnt");
			}
			rs.close();
			pst.close();
			
			sbQuery = new StringBuilder();
			sbQuery.append("select count(*) as cnt from book_details bd,book_issues_returns br where bd.book_id = br.book_id and issue_status = 0 ");

			if(getF_org()!=null && !getF_org().equals("")) {
				sbQuery.append(" and org_id in ("+getF_org()+")");
			} else {
				if(strUserType != null && strUserType.equals(HRMANAGER)) {
					 if(session.getAttribute(ORG_ACCESS) != null && !session.getAttribute(ORG_ACCESS).equals("")) {
						 sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
					 } else {
						 sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORGID)+")");
					 }
				} else if(strUserType != null && !strUserType.equals(HRMANAGER) && !strUserType.equals(ADMIN)) {
					sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORGID)+")");
				}
			}
			
			if(getF_wlocation()!=null && !getF_wlocation().equals("")) {
				sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_wlocation(), ",")+")");
			} else {
				if(strUserType != null && strUserType.equals(HRMANAGER)) {
					 if(session.getAttribute(WLOCATION_ACCESS) != null && !session.getAttribute(WLOCATION_ACCESS).equals("")){
						 sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
					 } else {
						 sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATIONID)+")");
					 }
				} else if(strUserType != null && !strUserType.equals(HRMANAGER) && !strUserType.equals(ADMIN)) {
					sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATIONID)+")");
				}
			}
						
			if(getStrSearchJob()!=null && !getStrSearchJob().trim().equals("")) {
				sbQuery.append(" and (upper(book_title) like '%"+getStrSearchJob().trim().toUpperCase()+"%' or upper(book_category) like '%"+getStrSearchJob().trim().toUpperCase()+"%' or upper(book_author) like '%"+getStrSearchJob().trim().toUpperCase()+"%' )");
			}
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();
			int issueReqCnt = 0;
			while(rs.next()) {
				issueReqCnt = rs.getInt("cnt");
			}
			rs.close();
			pst.close();
			
			
			sbQuery = new StringBuilder();
			sbQuery.append("select count(*) as cnt from book_details bd,book_issues_returns br where bd.book_id = br.book_id and issue_status = 1 and return_status is null ");

			if(getF_org()!=null && !getF_org().equals("")) {
				sbQuery.append(" and org_id in ("+getF_org()+")");
			} else {
				if(strUserType != null && strUserType.equals(HRMANAGER)) {
					 if(session.getAttribute(ORG_ACCESS) != null && !session.getAttribute(ORG_ACCESS).equals("")) {
						 sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
					 } else {
						 sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORGID)+")");
					 }
				} else if(strUserType != null && !strUserType.equals(HRMANAGER) && !strUserType.equals(ADMIN)) {
					sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORGID)+")");
				}
			}
			
			if(getF_wlocation()!=null && !getF_wlocation().equals("")) {
				sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_wlocation(), ",")+")");
			} else {
				if(strUserType != null && strUserType.equals(HRMANAGER)) {
					 if(session.getAttribute(WLOCATION_ACCESS) != null && !session.getAttribute(WLOCATION_ACCESS).equals("")){
						 sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
					 } else {
						 sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATIONID)+")");
					 }
				} else if(strUserType != null && !strUserType.equals(HRMANAGER) && !strUserType.equals(ADMIN)) {
					sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATIONID)+")");
				}
			}
			
			/*if(getF_org()!=null && !getF_org().equals("")) {
				sbQuery.append("  and org_id in ("+getF_org()+")");
			} else {
				sbQuery.append("  and org_id in ("+(String)session.getAttribute(ORGID)+")");
			}
			if(getF_wlocation()!=null && !getF_wlocation().equals("")) {
				sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_wlocation(), ",")+")");
			}*/
			if(getStrSearchJob()!=null && !getStrSearchJob().trim().equals("")) {
				sbQuery.append(" and (upper(book_title) like '%"+getStrSearchJob().trim().toUpperCase()+"%' or upper(book_category) like '%"+getStrSearchJob().trim().toUpperCase()+"%' or upper(book_author) like '%"+getStrSearchJob().trim().toUpperCase()+"%' )");
			}
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();
			int issuedCnt = 0;
			while(rs.next()) {
				issuedCnt = rs.getInt("cnt");
			}
			rs.close();
			pst.close();
			
			request.setAttribute("issuedCnt", issuedCnt+"");
			request.setAttribute("issueReqCnt", issueReqCnt+"");
			request.setAttribute("purchaseReqCnt", purchaseReqCnt+"");
						
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}


	private void getSelectedFilter(UtilityFunctions uF) {
		Map<String,String> hmFilter=new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();
		
		alFilter.add("ORGANISATION");
		if(getF_org()!=null)  {
			String strOrg="";
			int k=0;
			for(int i=0;organisationList!=null && i<organisationList.size();i++) {
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
			sbQuery.append("select * from book_details where book_id>0 ");

			if(getF_org()!=null && !getF_org().equals("")) {
				sbQuery.append(" and org_id in ("+getF_org()+")");
			} else {
				if(strUserType != null && strUserType.equals(HRMANAGER)) {
					 if(session.getAttribute(ORG_ACCESS) != null && !session.getAttribute(ORG_ACCESS).equals("")) {
						 sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
					 } else {
						 sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORGID)+")");
					 }
				} else if(strUserType != null && !strUserType.equals(HRMANAGER) && !strUserType.equals(ADMIN)) {
					sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORGID)+")");
				}
			}
			
			if(getF_wlocation()!=null && !getF_wlocation().equals("")) {
				sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_wlocation(), ",")+")");
			} else {
				if(strUserType != null && strUserType.equals(HRMANAGER)) {
					 if(session.getAttribute(WLOCATION_ACCESS) != null && !session.getAttribute(WLOCATION_ACCESS).equals("")){
						 sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
					 } else {
						 sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATIONID)+")");
					 }
				} else if(strUserType != null && !strUserType.equals(HRMANAGER) && !strUserType.equals(ADMIN)) {
					sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATIONID)+")");
				}
			}
			/*if(getF_org()!=null && !getF_org().equals("")) {
				sbQuery.append(" and org_id in ("+getF_org()+")");
			} else {
				sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORGID)+")");
			}
			
			if(getF_wlocation() != null && !getF_wlocation().equals("")) {
				sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_wlocation(), ",")+")");
			}*/
			
//			if(getStrSearchJob()!=null && !getStrSearchJob().trim().equals("")) {
//				sbQuery.append(" and (upper(book_title) like '%"+getStrSearchJob().trim().toUpperCase()+"%' or upper(book_category) like '%"+getStrSearchJob().trim().toUpperCase()+"%' or upper(book_author) like '%"+getStrSearchJob().trim().toUpperCase()+"%' )");
//			}
			
			sbQuery.append(" order by book_title");
			pst = con.prepareStatement(sbQuery.toString());
		
			rs = pst.executeQuery();
			while (rs.next()) {
				setSearchList.add(rs.getString("book_title"));
				if(rs.getString("book_category") !=null && !rs.getString("book_category").trim().equals("")) {
					setSearchList.add(rs.getString("book_category").trim());
				}
				if(rs.getString("book_author") !=null && !rs.getString("book_author").trim().equals("")) {
					setSearchList.add(rs.getString("book_author").trim());
				}
			}
			rs.close();
			pst.close();
			
			StringBuilder sbData = null;
			Iterator<String> it = setSearchList.iterator();
			while (it.hasNext()) {
				String strData = it.next();
				if(sbData == null) {
					sbData = new StringBuilder();
					sbData.append("\""+strData+"\"");
				} else {
					sbData.append(",\""+strData+"\"");
				}
			}
			
			if(sbData == null) {
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
	
	private void deleteBook(UtilityFunctions uF){
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from book_details where book_id=?");
			pst.setInt(1, uF.parseToInt(getBookId()));
			rs = pst.executeQuery();
			String book_image = null;
			String addedBy = null;
			while(rs.next()) {
			    book_image = rs.getString("book_image");
			    addedBy = rs.getString("added_by");
			}
			rs.close();
			pst.close();
			
			String strFilePath = null;
			if(CF.getStrDocSaveLocation()==null) {
					strFilePath = DOCUMENT_LOCATION +"/"+addedBy+"/"+getBookId()+"/"+"/"+book_image;
			} else {
					strFilePath = CF.getStrDocSaveLocation()+I_BOOKS+"/"+addedBy+"/"+getBookId() +"/"+book_image;
			}
			File file = new File(strFilePath);
			file.delete();
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("delete from book_details where book_id=?");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1,uF.parseToInt(getBookId()));
//			System.out.println("deleteQuery==>"+pst);
			pst.execute();
			pst.close();
						
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	private void getEmpIssuedBookDetails(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			Map<String, String> hmOrgName = CF.getOrgName(con);
			Map<String, String> hmEmpNames = CF.getEmpNameMap(con,null, null);
			Map<String, String> hmEmpWLocation = CF.getWLocationMap(con,null,request,null); 
			Map<String,List<String>> hmEmpIssuedBookDetails = new LinkedHashMap<String,List<String>>();
			Map<String,String> hmAvgBookRating = getAverageRating(con, uF);
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select count(*) as cnt from book_details bd,book_issues_returns br where bd.book_id = br.book_id and issue_status=1 and " +
				"(return_status=0 or return_status is null) and requested_by=? ");
			if(getStrSearchJob()!=null && !getStrSearchJob().trim().equals("")) {
				sbQuery.append(" and (upper(book_title) like '%"+getStrSearchJob().trim().toUpperCase()+"%' or upper(book_category) like '%"+getStrSearchJob().trim().toUpperCase()+"%' or upper(book_author) like '%"+getStrSearchJob().trim().toUpperCase()+"%' )");
			}
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(strSessionEmpId));
			rs = pst.executeQuery();
			int booksCount = 0;
			while(rs.next()) {
				booksCount = rs.getInt("cnt");
			}
			rs.close();
			pst.close();
			
			int bookPageCount = booksCount/10;
			if(booksCount%10 != 0) {
				bookPageCount++;
			}
			request.setAttribute("bookPageCount", bookPageCount+"");
			
			sbQuery = new StringBuilder();
			sbQuery.append("select * from book_details bd,book_issues_returns br where bd.book_id = br.book_id and issue_status=1 and " +
				"(return_status=0 or return_status is null) and requested_by=? ");
			if(getStrSearchJob()!=null && !getStrSearchJob().trim().equals("")) {
				sbQuery.append(" and (upper(book_title) like '%"+getStrSearchJob().trim().toUpperCase()+"%' or upper(book_category) like '%"+getStrSearchJob().trim().toUpperCase()+"%' or upper(book_author) like '%"+getStrSearchJob().trim().toUpperCase()+"%' )");
			}
//			sbQuery.append(" order by book_title ");
			int intOffset = uF.parseToInt(minLimit);
			sbQuery.append(" order by issued_date desc limit 10 offset "+intOffset+"");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(strSessionEmpId));
//			System.out.println("issue pst==>"+pst);
			rs = pst.executeQuery();
			while(rs.next()) {
				List<String> alInner = new ArrayList<String>();
				
				alInner.add(uF.showData(rs.getString("book_issued_id"), "-"));//0
				alInner.add(uF.showData(rs.getString("book_id"), "-"));//1
				alInner.add(uF.showData(rs.getString("book_title"), "-"));//2
				alInner.add(uF.showData(rs.getString("book_author"), "-"));//3
				alInner.add(uF.showData(rs.getString("book_publisher"), "-"));//4
				alInner.add(uF.showData(rs.getString("book_year_published"),"-"));//5
				alInner.add(uF.showData(rs.getString("book_category"), "-"));//6
				alInner.add(uF.showData(rs.getString("book_short_description"), "-"));//7
				alInner.add(uF.showData(rs.getString("book_quantity"),""));//8
				alInner.add(uF.showData(hmOrgName.get(rs.getString("org_id")), "-"));//9
				alInner.add(uF.showData(hmEmpWLocation.get(rs.getString("wlocation_id")), "-"));//10
				alInner.add(uF.showData(hmEmpNames.get(rs.getString("approved_by")), "-"));//11
				alInner.add(uF.getDateFormat(rs.getString("issued_date"), DBDATE, CF.getStrReportDateFormat()));//12
				alInner.add(uF.showData(rs.getString("book_issued_quantity"), "0"));//13
				alInner.add(uF.getDateFormat(rs.getString("from_date"), DBDATE, CF.getStrReportDateFormat()));//14
				alInner.add(uF.getDateFormat(rs.getString("to_date"), DBDATE, CF.getStrReportDateFormat()));//15
				String extenstion = null;
				if(rs.getString("book_image") !=null && !rs.getString("book_image").trim().equals("")) {
					extenstion = FilenameUtils.getExtension(rs.getString("book_image").trim());
				}
				alInner.add(extenstion);//16
//				
				String bookImgPath = "userImages/book_avatar_photo.png";
				if(rs.getString("book_image")!=null && !rs.getString("book_image").equals("")) {
					if(CF.getStrDocSaveLocation()==null) {
						bookImgPath =  DOCUMENT_LOCATION +"/"+rs.getString("added_by")+"/"+rs.getString("book_id")+"/"+rs.getString("book_image") ;
					} else {
						bookImgPath = CF.getStrDocRetriveLocation()+I_BOOKS+"/"+rs.getString("added_by")+"/"+rs.getString("book_id")+"/"+rs.getString("book_image");
					}
				}
//				String bookImage = "<img class='lazy' border=\"0\" style=\"height:40px; width:40%; border: 1px solid #CCCCCC; padding: 5px;\" src=\""+bookImgPath+"\" data-original=\""+bookImgPath+"\" />";
				String bookImage = "<img class=\"img1\" src=\""+bookImgPath+"\" data-original=\""+bookImgPath+"\" />";
				alInner.add(bookImage); //17
				alInner.add(hmAvgBookRating.get(rs.getString("book_id")+"_RATE"));//18
				alInner.add(hmAvgBookRating.get(rs.getString("book_id")+"_COUNT"));//19
				
				hmEmpIssuedBookDetails.put(rs.getString("book_issued_id"), alInner);
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmEmpIssuedBookDetails", hmEmpIssuedBookDetails);
						
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}


	private void getEmpPurchasedBookDetails(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			Map<String, String> hmOrgName = CF.getOrgName(con);
			Map<String, String> hmEmpNames = CF.getEmpNameMap(con,null, null);
			Map<String, String> hmEmpWLocation = CF.getWLocationMap(con,null,request,null); 
			Map<String,List<String>> hmEmpPurchasedBookDetails = new LinkedHashMap<String,List<String>>();
			Map<String,String> hmAvgBookRating = getAverageRating(con, uF);
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select count(*) as cnt from book_details bd, book_purchases bp where bd.book_id = bp.book_id and request_status = 1 and purchased_by=? ");
			
			if(getStrSearchJob()!=null && !getStrSearchJob().trim().equals("")) {
				sbQuery.append(" and (upper(book_title) like '%"+getStrSearchJob().trim().toUpperCase()+"%' or upper(book_category) like '%"+getStrSearchJob().trim().toUpperCase()+"%' or upper(book_author) like '%"+getStrSearchJob().trim().toUpperCase()+"%' )");
			}
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(strSessionEmpId));
//			System.out.println("pst ===>> " + pst);  
			rs = pst.executeQuery(); 
			int booksCount = 0;
			while(rs.next()) {
				booksCount = rs.getInt("cnt");
			}
			rs.close();
			pst.close();
			
			int bookPageCount = booksCount/10;
			if(booksCount%10 != 0) {
				bookPageCount++;
			}
			request.setAttribute("bookPageCount", bookPageCount+"");
			
			
			sbQuery = new StringBuilder();
			sbQuery.append("select * from book_details bd, book_purchases bp where bd.book_id = bp.book_id and request_status = 1 and purchased_by=? ");
			
			if(getStrSearchJob()!=null && !getStrSearchJob().trim().equals("")) {
				sbQuery.append(" and (upper(book_title) like '%"+getStrSearchJob().trim().toUpperCase()+"%' or upper(book_category) like '%"+getStrSearchJob().trim().toUpperCase()+"%' or upper(book_author) like '%"+getStrSearchJob().trim().toUpperCase()+"%' )");
			}
//			sbQuery.append(" order by book_title ");
			int intOffset = uF.parseToInt(minLimit);
			sbQuery.append(" order by purchased_date desc limit 10 offset "+intOffset+"");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(strSessionEmpId));
//			System.out.println("pst ===>> " + pst);  
			rs = pst.executeQuery(); 
			while(rs.next()) {
				List<String> alInner = new ArrayList<String>();
				alInner.add(uF.showData(rs.getString("book_id"), "-"));//0
				alInner.add(uF.showData(rs.getString("book_title"), "-"));//1
				alInner.add(uF.showData(rs.getString("book_author"), "-"));//2
				alInner.add(uF.showData(rs.getString("book_publisher"), "-"));//3
				alInner.add(uF.showData(rs.getString("book_year_published"),"-"));//4
				alInner.add(uF.showData(rs.getString("book_category"), "0"));//5
				alInner.add(uF.showData(rs.getString("book_short_description"),"-"));//6
				alInner.add(uF.showData(hmOrgName.get(rs.getString("org_id")), "-"));//7
				alInner.add(uF.showData(hmEmpWLocation.get(rs.getString("wlocation_id")), "-"));//8
				alInner.add(uF.showData(hmEmpNames.get(rs.getString("approved_by")), "-"));//9
				alInner.add(uF.showData(rs.getString("approved_quantity"), "0"));//10
				alInner.add(uF.getDateFormat(rs.getString("purchased_date"), DBDATE, CF.getStrReportDateFormat()));//11
				alInner.add(uF.showData(rs.getString("book_purchase_id"), "-"));//12
				String extenstion = null;
				if(rs.getString("book_image") !=null && !rs.getString("book_image").trim().equals("")) {
					extenstion = FilenameUtils.getExtension(rs.getString("book_image").trim());
				}
				alInner.add(extenstion);//13
//				
				String bookImgPath = "userImages/book_avatar_photo.png";
				if(rs.getString("book_image")!=null && !rs.getString("book_image").equals("")) {
					if(CF.getStrDocSaveLocation()==null) {
						bookImgPath =  DOCUMENT_LOCATION +"/"+rs.getString("added_by")+"/"+rs.getString("book_id")+"/"+rs.getString("book_image") ;
					} else {
						bookImgPath = CF.getStrDocRetriveLocation()+I_BOOKS+"/"+rs.getString("added_by")+"/"+rs.getString("book_id")+"/"+rs.getString("book_image");
					}
				}
//				String bookImage = "<img class='lazy' border=\"0\" style=\"height:40px; width:40%; border: 1px solid #CCCCCC; padding: 5px;\" src=\""+bookImgPath+"\" data-original=\""+bookImgPath+"\" />";
				String bookImage = "<img class=\"img1\" src=\""+bookImgPath+"\" data-original=\""+bookImgPath+"\" />";
				alInner.add(bookImage); //14
				alInner.add(uF.showData(rs.getString("book_quantity"), "-"));//15
				alInner.add(hmAvgBookRating.get(rs.getString("book_id")+"_RATE"));//16
				alInner.add(hmAvgBookRating.get(rs.getString("book_id")+"_COUNT"));//17
				alInner.add(uF.showData(rs.getString("book_amount"), "0"));//18
				
				hmEmpPurchasedBookDetails.put(rs.getString("book_purchase_id"), alInner);
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmEmpPurchasedBookDetails",hmEmpPurchasedBookDetails);
						
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
		
		try {
			con = db.makeConnection(con);
			Map<String, String> hmOrgName = CF.getOrgName(con);
			Map<String, String> hmEmpNames = CF.getEmpNameMap(con,null, null);
			Map<String, String> hmEmpWLocation = CF.getWLocationMap(con,null,request,null); 
			Map<String,List<String>> hmBookIssuesDetails = new LinkedHashMap<String,List<String>>();
			Map<String,String> hmAvgBookRating = getAverageRating(con, uF);
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select count(*) as cnt from book_details bd,book_issues_returns br where bd.book_id = br.book_id and issue_status = 0 ");

			if(getF_org()!=null && !getF_org().equals("")) {
				sbQuery.append(" and org_id in ("+getF_org()+")");
			} else {
				if(strUserType != null && strUserType.equals(HRMANAGER)) {
					 if(session.getAttribute(ORG_ACCESS) != null && !session.getAttribute(ORG_ACCESS).equals("")) {
						 sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
					 } else {
						 sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORGID)+")");
					 }
				} else if(strUserType != null && !strUserType.equals(HRMANAGER) && !strUserType.equals(ADMIN)) {
					sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORGID)+")");
				}
			}
			
			if(getF_wlocation()!=null && !getF_wlocation().equals("")) {
				sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_wlocation(), ",")+")");
			} else {
				if(strUserType != null && strUserType.equals(HRMANAGER)) {
					 if(session.getAttribute(WLOCATION_ACCESS) != null && !session.getAttribute(WLOCATION_ACCESS).equals("")){
						 sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
					 } else {
						 sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATIONID)+")");
					 }
				} else if(strUserType != null && !strUserType.equals(HRMANAGER) && !strUserType.equals(ADMIN)) {
					sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATIONID)+")");
				}
			}
			/*
			if(getF_org()!=null && !getF_org().equals("")) {
				sbQuery.append("  and org_id in ("+getF_org()+")");
			} else {
				sbQuery.append("  and org_id in ("+(String)session.getAttribute(ORGID)+")");
			}
			if(getF_wlocation()!=null && !getF_wlocation().equals("")) {
				sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_wlocation(), ",")+")");
			}*/
			if(getStrSearchJob()!=null && !getStrSearchJob().trim().equals("")) {
				sbQuery.append(" and (upper(book_title) like '%"+getStrSearchJob().trim().toUpperCase()+"%' or upper(book_category) like '%"+getStrSearchJob().trim().toUpperCase()+"%' or upper(book_author) like '%"+getStrSearchJob().trim().toUpperCase()+"%' )");
			}
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();
			int booksCount = 0;
			while(rs.next()) {
				booksCount = rs.getInt("cnt");
			}
			rs.close();
			pst.close();
			
			int bookPageCount = booksCount/10;
			if(booksCount%10 != 0) {
				bookPageCount++;
			}
			request.setAttribute("bookPageCount", bookPageCount+"");
			
			
			sbQuery = new StringBuilder();
			sbQuery.append("select * from book_details bd,book_issues_returns br where bd.book_id = br.book_id and issue_status = 0 ");

			if(getF_org()!=null && !getF_org().equals("")) {
				sbQuery.append(" and org_id in ("+getF_org()+")");
			} else {
				if(strUserType != null && strUserType.equals(HRMANAGER)) {
					 if(session.getAttribute(ORG_ACCESS) != null && !session.getAttribute(ORG_ACCESS).equals("")) {
						 sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
					 } else {
						 sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORGID)+")");
					 }
				} else if(strUserType != null && !strUserType.equals(HRMANAGER) && !strUserType.equals(ADMIN)) {
					sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORGID)+")");
				}
			}
			
			if(getF_wlocation()!=null && !getF_wlocation().equals("")) {
				sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_wlocation(), ",")+")");
			} else {
				if(strUserType != null && strUserType.equals(HRMANAGER)) {
					 if(session.getAttribute(WLOCATION_ACCESS) != null && !session.getAttribute(WLOCATION_ACCESS).equals("")){
						 sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
					 } else {
						 sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATIONID)+")");
					 }
				} else if(strUserType != null && !strUserType.equals(HRMANAGER) && !strUserType.equals(ADMIN)) {
					sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATIONID)+")");
				}
			}
			
			/*if(getF_org()!=null && !getF_org().equals("")) {
				sbQuery.append("  and org_id in ("+getF_org()+")");
			} else {
				sbQuery.append("  and org_id in ("+(String)session.getAttribute(ORGID)+")");
			}
			if(getF_wlocation()!=null && !getF_wlocation().equals("")) {
				sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_wlocation(), ",")+")");
			}*/
			if(getStrSearchJob()!=null && !getStrSearchJob().trim().equals("")) {
				sbQuery.append(" and (upper(book_title) like '%"+getStrSearchJob().trim().toUpperCase()+"%' or upper(book_category) like '%"+getStrSearchJob().trim().toUpperCase()+"%' or upper(book_author) like '%"+getStrSearchJob().trim().toUpperCase()+"%' )");
			}
//			sbQuery.append(" order by book_title ");
			int intOffset = uF.parseToInt(minLimit);
			sbQuery.append(" order by request_date desc limit 10 offset "+intOffset+"");
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();
			while(rs.next()) {
				List<String> alInner = new ArrayList<String>();
				
				alInner.add(uF.showData(rs.getString("book_issued_id"), "-"));//0
				alInner.add(uF.showData(rs.getString("book_id"), "-"));//1
				alInner.add(uF.showData(rs.getString("book_title"), "-"));//2
				alInner.add(uF.showData(rs.getString("book_author"), "-"));//3
				alInner.add(uF.showData(rs.getString("book_publisher"), "-"));//4
				alInner.add(uF.showData(rs.getString("book_year_published"),"-"));//5
				alInner.add(uF.showData(rs.getString("book_category"), "-"));//6
				alInner.add(uF.showData(rs.getString("book_short_description"), "-"));//7
				alInner.add(uF.showData(rs.getString("book_quantity"),""));//8
				alInner.add(uF.showData(hmOrgName.get(rs.getString("org_id")), "-"));//9
				alInner.add(uF.showData(hmEmpWLocation.get(rs.getString("wlocation_id")), "-"));//10
				alInner.add(uF.showData(hmEmpNames.get(rs.getString("requested_by")), "-"));//11
				alInner.add(uF.getDateFormat(rs.getString("request_date"), DBDATE, CF.getStrReportDateFormat()));//12
				alInner.add(uF.showData(rs.getString("request_quantity"), "0"));//13
				alInner.add(uF.getDateFormat(rs.getString("from_date"), DBDATE, CF.getStrReportDateFormat()));//14
				alInner.add(uF.getDateFormat(rs.getString("to_date"), DBDATE, CF.getStrReportDateFormat()));//15
				String extenstion = null;
				if(rs.getString("book_image") !=null && !rs.getString("book_image").trim().equals("")) {
					extenstion = FilenameUtils.getExtension(rs.getString("book_image").trim());
				}
				alInner.add(extenstion);//16
//				
				String bookImgPath = "userImages/book_avatar_photo.png";
				if(rs.getString("book_image")!=null && !rs.getString("book_image").equals("")) {
					if(CF.getStrDocSaveLocation()==null) {
						bookImgPath =  DOCUMENT_LOCATION +"/"+rs.getString("added_by")+"/"+rs.getString("book_id")+"/"+rs.getString("book_image") ;
					} else {
						bookImgPath = CF.getStrDocRetriveLocation()+I_BOOKS+"/"+rs.getString("added_by")+"/"+rs.getString("book_id")+"/"+rs.getString("book_image");
					}
				}
//				String bookImage = "<img class='lazy' border=\"0\" style=\"height:40px; width:40%; border: 1px solid #CCCCCC; padding: 5px;\" src=\""+bookImgPath+"\" data-original=\""+bookImgPath+"\" />";
				String bookImage = "<img class=\"img1\" src=\""+bookImgPath+"\" data-original=\""+bookImgPath+"\" />";
				alInner.add(bookImage); //17
				alInner.add(hmAvgBookRating.get(rs.getString("book_id")+"_RATE"));//18
				alInner.add(hmAvgBookRating.get(rs.getString("book_id")+"_COUNT"));//19
				alInner.add(rs.getString("available_book_quantity"));//20
				hmBookIssuesDetails.put(rs.getString("book_issued_id"), alInner);
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmBookIssuesDetails", hmBookIssuesDetails);
						
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
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
		
		try {
			con = db.makeConnection(con);
			Map<String, String> hmOrgName = CF.getOrgName(con);
			Map<String, String> hmEmpNames = CF.getEmpNameMap(con, null, null);
			Map<String, String> hmEmpWLocation = CF.getWLocationMap(con, null, request, null); 
			Map<String,String> hmAvgBookRating = getAverageRating(con, uF);

			Map<String,List<String>> hmReturnBookDetails = new LinkedHashMap<String,List<String>>();
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select count(*) as cnt from book_details bd,book_issues_returns br where bd.book_id = br.book_id and issue_status = 1 and return_status is null ");

			if(getF_org()!=null && !getF_org().equals("")) {
				sbQuery.append(" and org_id in ("+getF_org()+")");
			} else {
				if(strUserType != null && strUserType.equals(HRMANAGER)) {
					 if(session.getAttribute(ORG_ACCESS) != null && !session.getAttribute(ORG_ACCESS).equals("")) {
						 sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
					 } else {
						 sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORGID)+")");
					 }
				} else if(strUserType != null && !strUserType.equals(HRMANAGER) && !strUserType.equals(ADMIN)) {
					sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORGID)+")");
				}
			}
			
			if(getF_wlocation()!=null && !getF_wlocation().equals("")) {
				sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_wlocation(), ",")+")");
			} else {
				if(strUserType != null && strUserType.equals(HRMANAGER)) {
					 if(session.getAttribute(WLOCATION_ACCESS) != null && !session.getAttribute(WLOCATION_ACCESS).equals("")){
						 sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
					 } else {
						 sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATIONID)+")");
					 }
				} else if(strUserType != null && !strUserType.equals(HRMANAGER) && !strUserType.equals(ADMIN)) {
					sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATIONID)+")");
				}
			}
			/*if(getF_org()!=null && !getF_org().equals("")) {
				sbQuery.append("  and org_id in ("+getF_org()+")");
			} else {
				sbQuery.append("  and org_id in ("+(String)session.getAttribute(ORGID)+")");
			}
			if(getF_wlocation()!=null && !getF_wlocation().equals("")) {
				sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_wlocation(), ",")+")");
			}*/
			if(getStrSearchJob()!=null && !getStrSearchJob().trim().equals("")) {
				sbQuery.append(" and (upper(book_title) like '%"+getStrSearchJob().trim().toUpperCase()+"%' or upper(book_category) like '%"+getStrSearchJob().trim().toUpperCase()+"%' or upper(book_author) like '%"+getStrSearchJob().trim().toUpperCase()+"%' )");
			}
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();
			int booksCount = 0;
			while(rs.next()) {
				booksCount = rs.getInt("cnt");
			}
			rs.close();
			pst.close();
			
			int bookPageCount = booksCount/10;
			if(booksCount%10 != 0) {
				bookPageCount++;
			}
			request.setAttribute("bookPageCount", bookPageCount+"");
			
			
			sbQuery = new StringBuilder();
			sbQuery.append("select * from book_details bd,book_issues_returns br where bd.book_id = br.book_id and issue_status = 1 and return_status is null ");

			if(getF_org()!=null && !getF_org().equals("")) {
				sbQuery.append(" and org_id in ("+getF_org()+")");
			} else {
				if(strUserType != null && strUserType.equals(HRMANAGER)) {
					 if(session.getAttribute(ORG_ACCESS) != null && !session.getAttribute(ORG_ACCESS).equals("")) {
						 sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
					 } else {
						 sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORGID)+")");
					 }
				} else if(strUserType != null && !strUserType.equals(HRMANAGER) && !strUserType.equals(ADMIN)) {
					sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORGID)+")");
				}
			}
			
			if(getF_wlocation()!=null && !getF_wlocation().equals("")) {
				sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_wlocation(), ",")+")");
			} else {
				if(strUserType != null && strUserType.equals(HRMANAGER)) {
					 if(session.getAttribute(WLOCATION_ACCESS) != null && !session.getAttribute(WLOCATION_ACCESS).equals("")){
						 sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
					 } else {
						 sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATIONID)+")");
					 }
				} else if(strUserType != null && !strUserType.equals(HRMANAGER) && !strUserType.equals(ADMIN)) {
					sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATIONID)+")");
				}
			}
			/*if(getF_org()!=null && !getF_org().equals("")) {
				sbQuery.append("  and org_id in ("+getF_org()+")");
			} else {
				sbQuery.append("  and org_id in ("+(String)session.getAttribute(ORGID)+")");
			}
			if(getF_wlocation()!=null && !getF_wlocation().equals("")) {
				sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_wlocation(), ",")+")");
			}*/
			if(getStrSearchJob()!=null && !getStrSearchJob().trim().equals("")) {
				sbQuery.append(" and (upper(book_title) like '%"+getStrSearchJob().trim().toUpperCase()+"%' or upper(book_category) like '%"+getStrSearchJob().trim().toUpperCase()+"%' or upper(book_author) like '%"+getStrSearchJob().trim().toUpperCase()+"%' )");
			}
//			sbQuery.append(" order by book_title ");
			int intOffset = uF.parseToInt(minLimit);
			sbQuery.append(" order by issued_date desc limit 10 offset "+intOffset+"");
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();
			while(rs.next()) {
				List<String> alInner = new ArrayList<String>();
				alInner.add(uF.showData(rs.getString("book_issued_id"), "-"));//0
				alInner.add(uF.showData(rs.getString("book_id"), "-"));//1
				alInner.add(uF.showData(rs.getString("book_title"), "-"));//2
				alInner.add(uF.showData(rs.getString("book_author"), "-"));//3
				alInner.add(uF.showData(rs.getString("book_publisher"), "-"));//4
				alInner.add(uF.showData(rs.getString("book_year_published"),"-"));//5
				alInner.add(uF.showData(rs.getString("book_category"), "-"));//6
				alInner.add(uF.showData(rs.getString("book_short_description"), "-"));//7
				alInner.add(uF.showData(rs.getString("book_quantity"), "0"));//8
				alInner.add(uF.showData(hmOrgName.get(rs.getString("org_id")), "-"));//9
				alInner.add(uF.showData(hmEmpWLocation.get(rs.getString("wlocation_id")), "-"));//10
				alInner.add(uF.showData(hmEmpNames.get(rs.getString("requested_by")), "-"));//11
				alInner.add(uF.getDateFormat(rs.getString("issued_date"), DBDATE, CF.getStrReportDateFormat()));//12
				alInner.add(uF.showData(rs.getString("book_issued_quantity"), "0"));//13
				alInner.add(uF.getDateFormat(rs.getString("from_date"), DBDATE, CF.getStrReportDateFormat()));//14
				alInner.add(uF.getDateFormat(rs.getString("to_date"), DBDATE, CF.getStrReportDateFormat()));//15
				String extenstion = null;
				if(rs.getString("book_image") !=null && !rs.getString("book_image").trim().equals("")) {
					extenstion = FilenameUtils.getExtension(rs.getString("book_image").trim());
				}
				alInner.add(extenstion);//16
//				
				String bookImgPath = "userImages/book_avatar_photo.png";
				if(rs.getString("book_image")!=null && !rs.getString("book_image").equals("")) {
					if(CF.getStrDocSaveLocation()==null) {
						bookImgPath =  DOCUMENT_LOCATION +"/"+rs.getString("added_by")+"/"+rs.getString("book_id")+"/"+rs.getString("book_image") ;
					} else {
						bookImgPath = CF.getStrDocRetriveLocation()+I_BOOKS+"/"+rs.getString("added_by")+"/"+rs.getString("book_id")+"/"+rs.getString("book_image");
					}
				}
//				String bookImage = "<img class='lazy' border=\"0\" style=\"height:40px; width:40%; border: 1px solid #CCCCCC; padding: 5px;\" src=\""+bookImgPath+"\" data-original=\""+bookImgPath+"\" />";
				String bookImage = "<img class=\"img1\" src=\""+bookImgPath+"\" data-original=\""+bookImgPath+"\" />";
				alInner.add(bookImage); //17
				alInner.add(hmAvgBookRating.get(rs.getString("book_id")+"_RATE"));//18
				alInner.add(hmAvgBookRating.get(rs.getString("book_id")+"_COUNT"));//19
				alInner.add(uF.showData(rs.getString("issue_comment"), ""));//20
				
				hmReturnBookDetails.put(rs.getString("book_issued_id"), alInner);
			}
			rs.close();
			pst.close();
			request.setAttribute("hmReturnBookDetails", hmReturnBookDetails);
						
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	private void getBookDetails(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			Map<String, String> hmOrgName = CF.getOrgName(con);
			Map<String, String> hmEmpNames = CF.getEmpNameMap(con, null, null);
			Map<String, String> hmEmpWLocation = CF.getWLocationMap(con, null, request, null); 
			Map<String,String> hmAvgBookRating = getAverageRating(con, uF);
			
			Map<String,List<String>> hmBookDetails = new LinkedHashMap<String,List<String>>();
			List<String> bookIssuedList = new ArrayList<String>();
			List<String>  bookPurchasedList = new ArrayList<String>();
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select count(*) as cnt from book_details where book_id>0 ");
			if(getF_org()!=null && !getF_org().equals("")) {
				sbQuery.append(" and org_id in ("+getF_org()+")");
			} else {
				if(strUserType != null && strUserType.equals(HRMANAGER)) {
					 if(session.getAttribute(ORG_ACCESS) != null && !session.getAttribute(ORG_ACCESS).equals("")) {
						 sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
					 } else {
						 sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORGID)+")");
					 }
				} else if(strUserType != null && !strUserType.equals(HRMANAGER) && !strUserType.equals(ADMIN)) {
					sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORGID)+")");
				}
			}
			
			if(getF_wlocation()!=null && !getF_wlocation().equals("")) {
				sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_wlocation(), ",")+")");
			} else {
				if(strUserType != null && strUserType.equals(HRMANAGER)) {
					 if(session.getAttribute(WLOCATION_ACCESS) != null && !session.getAttribute(WLOCATION_ACCESS).equals("")){
						 sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
					 } else {
						 sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATIONID)+")");
					 }
				} else if(strUserType != null && !strUserType.equals(HRMANAGER) && !strUserType.equals(ADMIN)) {
					sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATIONID)+")");
				}
			}
			
			/*if(getF_org()!=null && !getF_org().equals("")) {
				sbQuery.append(" and org_id in ("+getF_org()+")");
			} else {
				sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORGID)+")");
			}
			
			if(getF_wlocation()!=null && !getF_wlocation().equals("")) {
				sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_wlocation(), ",")+")");
			} else {
				sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATIONID)+") ");
			}*/
				
			if(getStrSearchJob()!=null && !getStrSearchJob().trim().equals("")) {
				sbQuery.append(" and (upper(book_title) like '%"+getStrSearchJob().trim().toUpperCase()+"%' or upper(book_category) like '%"+getStrSearchJob().trim().toUpperCase()+"%' or upper(book_author) like '%"+getStrSearchJob().trim().toUpperCase()+"%' )");
			}
			pst = con.prepareStatement(sbQuery.toString());
			
			rs = pst.executeQuery();
			int booksCount = 0;
			while(rs.next()) {
				booksCount = rs.getInt("cnt");
			}
			rs.close();
			pst.close();
			
			int bookPageCount = booksCount/10;
			if(booksCount%10 != 0) {
				bookPageCount++;
			}
			request.setAttribute("bookPageCount", bookPageCount+"");
			
						
			Map<String, String> hmBookUsed = new HashMap<String, String>();
			pst = con.prepareStatement("select * from book_issues_returns where issue_status = 1");
			rs = pst.executeQuery();
			while(rs.next()) {
				hmBookUsed.put(rs.getString("book_id"), rs.getString("book_id"));
				bookIssuedList.add(rs.getString("book_id")+"_"+rs.getString("requested_by"));
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from book_purchases where request_status = 1");
			rs = pst.executeQuery();
			while(rs.next()) {
				hmBookUsed.put(rs.getString("book_id"), rs.getString("book_id"));
				bookPurchasedList.add(rs.getString("book_id")+"_"+rs.getString("purchased_by"));
			}
			rs.close();
			pst.close();
			
			
			sbQuery = new StringBuilder();
			sbQuery.append("select * from book_details where book_id>0 ");
			if(getF_org()!=null && !getF_org().equals("")) {
				sbQuery.append(" and org_id in ("+getF_org()+")");
			} else {
				if(strUserType != null && strUserType.equals(HRMANAGER)) {
					 if(session.getAttribute(ORG_ACCESS) != null && !session.getAttribute(ORG_ACCESS).equals("")) {
						 sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
					 } else {
						 sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORGID)+")");
					 }
				} else if(strUserType != null && !strUserType.equals(HRMANAGER) && !strUserType.equals(ADMIN)) {
					sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORGID)+")");
				}
			}
			
			if(getF_wlocation()!=null && !getF_wlocation().equals("")) {
				sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_wlocation(), ",")+")");
			} else {
				if(strUserType != null && strUserType.equals(HRMANAGER)) {
					 if(session.getAttribute(WLOCATION_ACCESS) != null && !session.getAttribute(WLOCATION_ACCESS).equals("")){
						 sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
					 } else {
						 sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATIONID)+")");
					 }
				} else if(strUserType != null && !strUserType.equals(HRMANAGER) && !strUserType.equals(ADMIN)) {
					sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATIONID)+")");
				}
			}
			/*if(getF_org()!=null && !getF_org().equals("")) {
				sbQuery.append(" and org_id in ("+getF_org()+")");
			} else {
				sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORGID)+")");
			}
			
			if(getF_wlocation()!=null && !getF_wlocation().equals("")) {
				sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_wlocation(), ",")+")");
			} else {
				sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATIONID)+") ");
			}*/
				System.out.println("getStrSearchJob==>"+getStrSearchJob());
			if(getStrSearchJob()!=null && !getStrSearchJob().trim().equals("")) {
				sbQuery.append(" and (upper(book_title) like '%"+getStrSearchJob().trim().toUpperCase()+"%' or upper(book_category) like '%"+getStrSearchJob().trim().toUpperCase()+"%' or upper(book_author) like '%"+getStrSearchJob().trim().toUpperCase()+"%' )");
			}
//			sbQuery.append(" order by book_title ");
			int intOffset = uF.parseToInt(minLimit);
			sbQuery.append(" order by entry_date desc limit 10 offset "+intOffset+"");
			pst = con.prepareStatement(sbQuery.toString());
			System.out.println("pst==>"+pst);
			rs = pst.executeQuery();
			while(rs.next()) {
				List<String> alInner = new ArrayList<String>();
				alInner.add(uF.showData(rs.getString("book_quantity"),"0"));//0
				alInner.add(uF.showData(rs.getString("book_title"),"-"));//1
				alInner.add(uF.showData(rs.getString("book_author"),"-"));//2
				alInner.add(uF.showData(rs.getString("book_publisher"),"-"));//3
				alInner.add(uF.showData(rs.getString("book_year_published"),"-"));//4
				alInner.add(uF.showData(rs.getString("book_isbn_no"),"-"));//5
				alInner.add(uF.showData(rs.getString("book_category"),"-"));//6
				alInner.add(uF.showData(rs.getString("book_short_description"),"-"));//7
				alInner.add(uF.showData(hmOrgName.get(rs.getString("org_id")),"-"));//8
				alInner.add(uF.showData(hmEmpWLocation.get(rs.getString("wlocation_id")),"-"));//9
				alInner.add(uF.showData(hmEmpNames.get(rs.getString("added_by")),"-"));//10
				alInner.add(uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));//11
				alInner.add(uF.showData(rs.getString("last_updated_by"),"-"));//12
				alInner.add(uF.getDateFormat(rs.getString("last_updated_date"), DBDATE, CF.getStrReportDateFormat()));//13
				alInner.add(rs.getString("book_id"));//14
				
				String extenstion = null;
				if(rs.getString("book_image") !=null && !rs.getString("book_image").trim().equals("")) {
					extenstion = FilenameUtils.getExtension(rs.getString("book_image").trim());
				}
				alInner.add(extenstion);//15
//				
				String bookImgPath = "userImages/book_avatar_photo.png";
				if(rs.getString("book_image")!=null && !rs.getString("book_image").equals("")) {
					if(CF.getStrDocSaveLocation()==null) {
						bookImgPath =  DOCUMENT_LOCATION +"/"+rs.getString("added_by")+"/"+rs.getString("book_id")+"/"+rs.getString("book_image") ;
					} else {
						bookImgPath = CF.getStrDocRetriveLocation()+I_BOOKS+"/"+rs.getString("added_by")+"/"+rs.getString("book_id")+"/"+rs.getString("book_image");
					}
				}
//				String bookImage = "<img class='lazy' border=\"0\" style=\"height:40px; width:40%; border: 1px solid #CCCCCC; padding: 5px;\" src=\""+bookImgPath+"\" data-original=\""+bookImgPath+"\" />";
				String bookImage = "<img class=\"img1\" src=\""+bookImgPath+"\" data-original=\""+bookImgPath+"\" />";
				alInner.add(bookImage); //16
				
				if((bookIssuedList.contains(rs.getString("book_id")+"_"+strSessionEmpId)) || (bookPurchasedList.contains(rs.getString("book_id")+"_"+strSessionEmpId))) {
					alInner.add("T"); //17
				} else {
					alInner.add("F"); //17
				}
				alInner.add(hmAvgBookRating.get(rs.getString("book_id")+"_RATE")); //18
				alInner.add(hmAvgBookRating.get(rs.getString("book_id")+"_COUNT")); //19
				alInner.add(rs.getString("available_book_quantity"));//20
				if(uF.parseToInt(hmBookUsed.get(rs.getString("book_id"))) > 0) {
					alInner.add("T"); //21
				} else {
					alInner.add("F"); //21
				}
				
				hmBookDetails.put(rs.getString("book_id"), alInner);
			}
			rs.close();
			pst.close();
//			System.out.println("hmBookDetails==>"+hmBookDetails);
			request.setAttribute("hmBookDetails",hmBookDetails);
						
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	private void getBookPurchaseDetails(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			Map<String, String> hmOrgName = CF.getOrgName(con);
			Map<String, String> hmEmpNames = CF.getEmpNameMap(con,null, null);
			Map<String, String> hmEmpWLocation = CF.getWLocationMap(con,null,request,null); 
			Map<String,List<String>> hmPurchaseBookDetails = new LinkedHashMap<String,List<String>>();
			Map<String,String> hmAvgBookRating = getAverageRating(con, uF);
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select count(*) as cnt from book_details bd, book_purchases bp where bd.book_id = bp.book_id and request_status = 0 ");

			if(getF_org()!=null && !getF_org().equals("")) {
				sbQuery.append(" and bd.org_id in ("+getF_org()+")");
			} else {
				if(strUserType != null && strUserType.equals(HRMANAGER)) {
					 if(session.getAttribute(ORG_ACCESS) != null && !session.getAttribute(ORG_ACCESS).equals("")) {
						 sbQuery.append(" and bd.org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
					 } else {
						 sbQuery.append(" and bd.org_id in ("+(String)session.getAttribute(ORGID)+")");
					 }
				} else if(strUserType != null && !strUserType.equals(HRMANAGER) && !strUserType.equals(ADMIN)) {
					sbQuery.append(" and bd.org_id in ("+(String)session.getAttribute(ORGID)+")");
				}
			}
			
			if(getF_wlocation()!=null && !getF_wlocation().equals("")) {
				sbQuery.append(" and bd.wlocation_id in ("+StringUtils.join(getF_wlocation(), ",")+")");
			} else {
				if(strUserType != null && strUserType.equals(HRMANAGER)) {
					 if(session.getAttribute(WLOCATION_ACCESS) != null && !session.getAttribute(WLOCATION_ACCESS).equals("")){
						 sbQuery.append(" and bd.wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
					 } else {
						 sbQuery.append(" and bd.wlocation_id in ("+(String)session.getAttribute(WLOCATIONID)+")");
					 }
				} else if(strUserType != null && !strUserType.equals(HRMANAGER) && !strUserType.equals(ADMIN)) {
					sbQuery.append(" and bd.wlocation_id in ("+(String)session.getAttribute(WLOCATIONID)+")");
				}
			}
			
			/*if(getF_org()!=null && !getF_org().equals("")) {
				sbQuery.append(" and bd.org_id in ("+getF_org()+")");
			} else {
				sbQuery.append(" and bd.org_id in ("+(String)session.getAttribute(ORGID)+")");
			}
			
			if(getF_wlocation()!=null && !getF_wlocation().equals("")) {
				sbQuery.append(" and bd.wlocation_id in ("+StringUtils.join(getF_wlocation(), ",")+")");
			}*/
				
			if(getStrSearchJob()!=null && !getStrSearchJob().trim().equals("")) {
				sbQuery.append(" and (upper(book_title) like '%"+getStrSearchJob().trim().toUpperCase()+"%' or upper(book_category) like '%"+getStrSearchJob().trim().toUpperCase()+"%' or upper(book_author) like '%"+getStrSearchJob().trim().toUpperCase()+"%' )");
			}
			pst = con.prepareStatement(sbQuery.toString());   
//			System.out.println("pst ===>> " + pst);  
			rs = pst.executeQuery(); 
			int booksCount = 0;
			while(rs.next()) {
				booksCount = rs.getInt("cnt");
			}
			rs.close();
			pst.close();
			
			int bookPageCount = booksCount/10;
			if(booksCount%10 != 0) {
				bookPageCount++;
			}
			request.setAttribute("bookPageCount", bookPageCount+"");
			
			
			sbQuery = new StringBuilder();
			sbQuery.append("select * from book_details bd, book_purchases bp where bd.book_id = bp.book_id and request_status = 0 ");

			if(getF_org()!=null && !getF_org().equals("")) {
				sbQuery.append(" and bd.org_id in ("+getF_org()+")");
			} else {
				if(strUserType != null && strUserType.equals(HRMANAGER)) {
					 if(session.getAttribute(ORG_ACCESS) != null && !session.getAttribute(ORG_ACCESS).equals("")) {
						 sbQuery.append(" and bd.org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
					 } else {
						 sbQuery.append(" and bd.org_id in ("+(String)session.getAttribute(ORGID)+")");
					 }
				} else if(strUserType != null && !strUserType.equals(HRMANAGER) && !strUserType.equals(ADMIN)) {
					sbQuery.append(" and bd.org_id in ("+(String)session.getAttribute(ORGID)+")");
				}
			}
			
			if(getF_wlocation()!=null && !getF_wlocation().equals("")) {
				sbQuery.append(" and bd.wlocation_id in ("+StringUtils.join(getF_wlocation(), ",")+")");
			} else {
				if(strUserType != null && strUserType.equals(HRMANAGER)) {
					 if(session.getAttribute(WLOCATION_ACCESS) != null && !session.getAttribute(WLOCATION_ACCESS).equals("")){
						 sbQuery.append(" and bd.wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
					 } else {
						 sbQuery.append(" and bd.wlocation_id in ("+(String)session.getAttribute(WLOCATIONID)+")");
					 }
				} else if(strUserType != null && !strUserType.equals(HRMANAGER) && !strUserType.equals(ADMIN)) {
					sbQuery.append(" and bd.wlocation_id in ("+(String)session.getAttribute(WLOCATIONID)+")");
				}
			}
			
			/*if(getF_org()!=null && !getF_org().equals("")) {
				sbQuery.append(" and bd.org_id in ("+getF_org()+")");
			} else {
				sbQuery.append(" and bd.org_id in ("+(String)session.getAttribute(ORGID)+")");
			}
			
			if(getF_wlocation()!=null && !getF_wlocation().equals("")) {
				sbQuery.append(" and bd.wlocation_id in ("+StringUtils.join(getF_wlocation(), ",")+")");
			}*/
				
			if(getStrSearchJob()!=null && !getStrSearchJob().trim().equals("")) {
				sbQuery.append(" and (upper(book_title) like '%"+getStrSearchJob().trim().toUpperCase()+"%' or upper(book_category) like '%"+getStrSearchJob().trim().toUpperCase()+"%' or upper(book_author) like '%"+getStrSearchJob().trim().toUpperCase()+"%' )");
			}
//			sbQuery.append(" order by book_title ");
			int intOffset = uF.parseToInt(minLimit);
			sbQuery.append(" order by requested_date desc limit 10 offset "+intOffset+"");
			pst = con.prepareStatement(sbQuery.toString());   
//			System.out.println("pst ===>> " + pst);  
			rs = pst.executeQuery(); 
			while(rs.next()) {
				List<String> alInner = new ArrayList<String>();
				alInner.add(uF.showData(rs.getString("book_id"), "-"));//0
				alInner.add(uF.showData(rs.getString("book_title"), "-"));//1
				alInner.add(uF.showData(rs.getString("book_author"), "-"));//2
				alInner.add(uF.showData(rs.getString("book_publisher"), "-"));//3
				alInner.add(uF.showData(rs.getString("book_year_published"),"-"));//4
				alInner.add(uF.showData(rs.getString("book_category"), "-"));//5
				alInner.add(uF.showData(rs.getString("book_short_description"),"-"));//6
				alInner.add(uF.showData(hmOrgName.get(rs.getString("org_id")), "-"));//7
				alInner.add(uF.showData(hmEmpWLocation.get(rs.getString("wlocation_id")), "-"));//8
				alInner.add(uF.showData(hmEmpNames.get(rs.getString("purchased_by")), "-"));//9
				alInner.add(uF.showData(rs.getString("requested_quantity"), "0"));//10
				alInner.add(uF.getDateFormat(rs.getString("requested_date"), DBDATE, CF.getStrReportDateFormat()));//11
				alInner.add(uF.showData(rs.getString("book_purchase_id"), "-"));//12
				String extenstion = null;
				if(rs.getString("book_image") !=null && !rs.getString("book_image").trim().equals("")) {
					extenstion = FilenameUtils.getExtension(rs.getString("book_image").trim());
				}
				alInner.add(extenstion);//13
//				
				String bookImgPath = "userImages/book_avatar_photo.png";
				if(rs.getString("book_image")!=null && !rs.getString("book_image").equals("")) {
					if(CF.getStrDocSaveLocation()==null) {
						bookImgPath =  DOCUMENT_LOCATION +"/"+rs.getString("added_by")+"/"+rs.getString("book_id")+"/"+rs.getString("book_image") ;
					} else {
						bookImgPath = CF.getStrDocRetriveLocation()+I_BOOKS+"/"+rs.getString("added_by")+"/"+rs.getString("book_id")+"/"+rs.getString("book_image");
					}
				}
//				String bookImage = "<img class='lazy' border=\"0\" style=\"height:40px; width:40%; border: 1px solid #CCCCCC; padding: 5px;\" src=\""+bookImgPath+"\" data-original=\""+bookImgPath+"\" />";
				String bookImage = "<img class=\"img1\" src=\""+bookImgPath+"\" data-original=\""+bookImgPath+"\" />";
				alInner.add(bookImage); //14
				alInner.add(uF.showData(rs.getString("book_quantity"),"-"));//15
				alInner.add(hmAvgBookRating.get(rs.getString("book_id")+"_RATE"));//16
				alInner.add(hmAvgBookRating.get(rs.getString("book_id")+"_COUNT"));//17
				alInner.add(rs.getString("available_book_quantity"));//18
				hmPurchaseBookDetails.put(rs.getString("book_purchase_id"), alInner);
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmPurchaseBookDetails",hmPurchaseBookDetails);
						
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	private Map<String,String> getAverageRating(Connection con ,UtilityFunctions uF) {
		Map<String,String> hmAvgBookRating = new LinkedHashMap<String,String>();
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		try {
			double averageRating = 0;
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select sum(emp_rate) as total_rating, count(emp_id) as emp_count, book_id from book_reviews group by book_id ");
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst==>"+pst);
			rs = pst.executeQuery();
			while(rs.next()) {
				double avgRating = 0;
				if(rs.getString("total_rating")!=null && !rs.getString("total_rating").equals("")) {
					avgRating =  rs.getDouble("total_rating") / rs.getDouble("emp_count");
				}
				
				hmAvgBookRating.put(rs.getString("book_id")+"_RATE", avgRating+"");
				hmAvgBookRating.put(rs.getString("book_id")+"_COUNT", rs.getString("emp_count"));
			}
			rs.close();
			pst.close();
									
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs != null) {
				try {
					rs.close();
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
		
		return hmAvgBookRating;
	}
	
	private String loadEmployee(UtilityFunctions uF) {
		if(strUserType!=null && strUserType.equalsIgnoreCase(HRMANAGER)) {
			 if(session.getAttribute(WLOCATION_ACCESS) != null && !session.getAttribute(WLOCATION_ACCESS).equals("")) {
				wLocationList = new FillWLocation(request).fillWLocation(getF_org(), (String)session.getAttribute(WLOCATION_ACCESS));
				organisationList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
			 } else {
				 wLocationList = new FillWLocation(request).fillWLocation(getF_org(), (String)session.getAttribute(WLOCATIONID));
				 organisationList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORGID));
			 }
		} else if(strUserType!=null && strUserType.equalsIgnoreCase(ADMIN)) {
			    organisationList = new FillOrganisation(request).fillOrganisation();
				wLocationList = new FillWLocation(request).fillWLocation(getF_org());
		} else {
			 wLocationList = new FillWLocation(request).fillWLocation(getF_org(), (String)session.getAttribute(WLOCATIONID));
			 organisationList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORGID));
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

	public String getBookId() {
		return bookId;
	}

	public void setBookId(String bookId) {
		this.bookId = bookId;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getCurrPage() {
		return currPage;
	}

	public void setCurrPage(String currPage) {
		this.currPage = currPage;
	}

	public String getMinLimit() {
		return minLimit;
	}

	public void setMinLimit(String minLimit) {
		this.minLimit = minLimit;
	}

	public String getAlertID() {
		return alertID;
	}

	public void setAlertID(String alertID) {
		this.alertID = alertID;
	}
	
}
