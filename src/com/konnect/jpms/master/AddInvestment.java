package com.konnect.jpms.master;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UserAlerts;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;
 
public class AddInvestment extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	CommonFunctions CF= null;
	HttpSession session;
	boolean isAgree;
	String strSessionEmpId;
	String strUserType = null;
	
	String []sectionId;
	File []sectionDoc;
	String []sectionDocFileName;
	String strEmployeeId;
	String f_strFinancialYear;
	
	String section;
	String amountPaid;
	
	String []strInvestmentId;
	String []strSectionId;
	String []strAmountPaid;
	
	String []othersectionId;
	File []othersectionDoc;
	String []othersectionDocFileName;
	
	
	String []strOtherInvestmentId;
	String []strOtherSectionId;
	String []strOtherAmountPaid;
	
	String removeSubInvestmentId;
	String removeSubOtherInvestmentId;
	
	public String execute() throws Exception {
		
		session = request.getSession();
		if(session==null)return LOGIN;
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		strSessionEmpId = (String)session.getAttribute(EMPID);
		strUserType = (String) session.getAttribute(USERTYPE);
		
		UtilityFunctions uF = new UtilityFunctions();
//		EncryptionUtility eU = new EncryptionUtility();
//		if(uF.parseToInt(getStrEmployeeId()) > 0) {
//			String encodeStrEmployeeId = eU.encode(getStrEmployeeId());
//			setStrEmployeeId(encodeStrEmployeeId);
//		}
		
		String isAgree = request.getParameter("isAgree");
		
//		if(isAgree()) {  
		if(uF.parseToInt(getStrEmployeeId())==0){
			setStrEmployeeId(strSessionEmpId);
		}
		String[] arrFinancialYear = null;
		if (getF_strFinancialYear() == null) {
			arrFinancialYear = CF.getFinancialYear(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, DATE_FORMAT), CF, uF);
			setF_strFinancialYear(arrFinancialYear[0] + "-" + arrFinancialYear[1]);
		}
		
		String operation = request.getParameter("operation");
		if (operation!=null && operation.equals("D")) { 
			deleteInvestment();
			if(strUserType != null && strUserType.equals(EMPLOYEE)){
				return SUCCESS;
			} else {
				return "successother";
			}
		}
		if (operation!=null && operation.equals("U")) { 
				return updateInvestment();
		}
		if (operation!=null && operation.equals("A")) {
			insertInvestment();
		}
		
		if(isAgree!=null && uF.parseToBoolean(isAgree)) {
			//return addAgreedInvestments();
		}
	
		if(strUserType != null && strUserType.equals(EMPLOYEE)){
			return SUCCESS;
		} else {
			return "successother";
		}
		
	}

	
//	private String addAgreedInvestments() {
//		Connection con = null;
//		PreparedStatement pst = null;
//		Database db = new Database();
//		db.setRequest(request);
//		ResultSet rs = null;
//		UtilityFunctions uF = new UtilityFunctions();
//
//		try {
//			List<List<String>> al = new ArrayList<List<String>>();
//			List<String> alInner = new ArrayList<String>();
//			
//			con = db.makeConnection(con);
//			pst = con.prepareStatement(selectSettings);
//			rs = pst.executeQuery();
//			String startDate ="";
//			String endDate = "";
//			
//			while(rs.next()) {
//				String options = rs.getString("options");
//				if(options.equals(O_FINANCIAL_YEAR_START)) {
//					startDate = rs.getString("value");
//				}
//				else if(options.equals(O_FINANCIAL_YEAR_END)) {
//					endDate = rs.getString("value");
//				}
//			}
//			rs.close();
//			pst.close();
//			
//			Date sDate = uF.getDateFormatUtil(startDate, DATE_FORMAT);
//			Date eDate = uF.getDateFormatUtil(endDate, DATE_FORMAT);
//			
//			pst = con.prepareStatement(selectInvestment);
////			pst.setInt(1, uF.parseToInt((String)session.getAttribute(EMPID)));
//			pst.setInt(1, uF.parseToInt(getStrEmployeeId()));
//			pst.setBoolean(2, false);
////			System.out.println("pst selectInvestment=>"+pst);
//			rs = pst.executeQuery();
//			while(rs.next()) {
//				Date date = uF.getDateFormatUtil(rs.getString("entrydate"), DBDATE);
////				System.out.println("date====>>"+date);
//				if(date!=null && uF.isDateBetween(sDate, eDate, date)) { 
//					alInner = new ArrayList<String>();
//					alInner.add(rs.getString("investment_id"));
//					alInner.add(rs.getString("section_code"));
//					alInner.add(rs.getString("amount_paid"));
//					alInner.add(rs.getString("status"));
//					alInner.add(rs.getString("emp_id"));
//					alInner.add(uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));
//					al.add(alInner);
//				}
//			}
//			rs.close();
//			pst.close();
//			
//			for(int i=0; i<al.size(); i++) {
//				String id = ((String)((ArrayList<String>)al.get(i)).get(0));
//				pst = con.prepareStatement(updateInvestment);
////				pst.setBoolean(1, true);
//				pst.setBoolean(1, false);
//				pst.setDate(2, uF.getDateFormat(startDate, DATE_FORMAT));
//				pst.setDate(3, uF.getDateFormat(endDate, DATE_FORMAT));
//				pst.setDate(4, uF.getCurrentDate(CF.getStrTimeZone()));
//				pst.setInt(5, uF.parseToInt(id));
//				pst.execute();
//				pst.close();
//			}
//				
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			
//			db.closeStatements(pst);
//			db.closeConnection(con);
//		}
//		return VIEW;
//	}
	
	public String loadValidateInvestment() {
		return LOAD;
	}

	
	
	public String insertInvestment() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			String[] arrFinancialYear = null;
			if (getF_strFinancialYear() != null) {
				arrFinancialYear = getF_strFinancialYear().split("-");
			}else{
				arrFinancialYear = CF.getFinancialYear(con, uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, DATE_FORMAT), CF, uF);
				setF_strFinancialYear(arrFinancialYear[0] + "-" + arrFinancialYear[1]);
			}
			
			con = db.makeConnection(con);
			
			
			pst = con.prepareStatement("select investment_id from investment_details where status=true");
			rs = pst.executeQuery();
			List<String> alInvestmentApproved = new ArrayList<String>();
			while(rs.next()){
				alInvestmentApproved.add(rs.getString("investment_id"));
			}
			rs.close();
			pst.close();
//			System.out.println("alInvestmentApproved====>"+alInvestmentApproved);
			
			if(getRemoveSubInvestmentId() !=null && !getRemoveSubInvestmentId().trim().equals("") && !getRemoveSubInvestmentId().trim().equalsIgnoreCase("")){
				List<String> alInvestmentId = Arrays.asList(getRemoveSubInvestmentId().trim().split(","));
				if(alInvestmentId == null) alInvestmentId = new ArrayList<String>();
				
				StringBuilder sbSubInvestmentId = null;
				for(String id : alInvestmentId){
					if(uF.parseToInt(id.trim()) == 0){
						continue;
					}
						
					if(sbSubInvestmentId == null){
						sbSubInvestmentId = new StringBuilder();
						sbSubInvestmentId.append(id.trim());
					} else {
						sbSubInvestmentId.append(","+id.trim());
					}
				}
				if(sbSubInvestmentId !=null && sbSubInvestmentId.length() > 0) {
					pst = con.prepareStatement("update investment_details set status = false, trail_status=0 where investment_id in ("+sbSubInvestmentId.toString()+")");
//					System.out.println("sub pst ====> " + pst);
					pst.execute();
					pst.close();
				}
			}
			
			String []sectionId = getStrSectionId();
			String []strAmountPaid = getStrAmountPaid();
			
			for(int i=0; i<sectionId.length; i++) {
				int nUpdate = 0;
				
				pst = con.prepareStatement("insert into investment_details (section_id , amount_paid, status, emp_id, entry_date, trail_status, fy_from, fy_to, agreed_date) values (?,?,?,?,?,?,?,?,?)");
				pst.setInt(1, uF.parseToInt(sectionId[i]));
				pst.setDouble(2, uF.parseToDouble(strAmountPaid[i]));
				pst.setBoolean(3, uF.parseToBoolean("FALSE"));
				pst.setInt(4, uF.parseToInt(getStrEmployeeId()));
				
				pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
				//pst.setInt(6, uF.parseToInt(getStrInvestmentId()[i]));
				pst.setInt(6, 1);
				pst.setDate(7, uF.getDateFormat(arrFinancialYear[0], DATE_FORMAT));
				pst.setDate(8, uF.getDateFormat(arrFinancialYear[1], DATE_FORMAT));
				pst.setDate(9, uF.getCurrentDate(CF.getStrTimeZone()));
				
//				if(strAmountPaid[i]!=null && strAmountPaid[i].length()>0 && !alInvestmentApproved.contains(getStrInvestmentId()[i])) {
				if(!alInvestmentApproved.contains(getStrInvestmentId()[i])) {
					nUpdate = pst.executeUpdate();
					pst.close();
					
					pst = con.prepareStatement("update investment_details set status = false, trail_status=0 where investment_id=?");
					pst.setInt(1, uF.parseToInt(getStrInvestmentId()[i]));
					pst.execute();
					pst.close();
				}
				//pst.clearParameters();
				
				if(nUpdate==0 && !alInvestmentApproved.contains(getStrInvestmentId()[i])){
					pst = con.prepareStatement(insertInvestment);
					pst.setInt(1, uF.parseToInt(sectionId[i]));
					pst.setDouble(2, uF.parseToDouble(strAmountPaid[i]));
					pst.setBoolean(3, uF.parseToBoolean("FALSE"));
					
					pst.setInt(4, uF.parseToInt(getStrEmployeeId()));
					
					pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
					
					if(strAmountPaid[i]!=null && strAmountPaid[i].length()>0){
						pst.execute();
						pst.close();
//						System.out.println("==+Inserting ===="+strAmountPaid[i]);
					}
//					pst.clearParameters();
				}
				
			} 
			
			for(int i=0; i<sectionId.length; i++) {
				String[] strSubSectionNo = request.getParameterValues("strSubSectionNo_"+sectionId[i]);
				String[] strSubSectionAmount = request.getParameterValues("strSubSectionAmount_"+sectionId[i]);
				String[] strSubSectionLimitType = request.getParameterValues("strSubSectionLimitType_"+sectionId[i]);
				String[] strSubSectionName=request.getParameterValues("strSubSectionId_"+sectionId[i]);
				String[] strSubAmountPaid=request.getParameterValues("strAmountPaid_"+sectionId[i]);
				String[] strSubInvestmentId=request.getParameterValues("strSubInvestmentId_"+sectionId[i]);
				 
				for(int j=0; strSubSectionName!=null && j<strSubSectionName.length; j++){
					int nSubUpdate = 0;	
					if(strSubSectionName[j]!=null && !strSubSectionName[j].equals("")) {
						pst = con.prepareStatement("insert into investment_details (section_id, amount_paid, status, emp_id, entry_date, trail_status," +
							" fy_from, fy_to, agreed_date,child_section,parent_section,sub_section_no,sub_section_amt,sub_section_limit_type) " +
							"values (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?)");
						pst.setInt(1, uF.parseToInt(sectionId[i]));
						pst.setDouble(2, uF.parseToDouble(strSubAmountPaid[j]));
						pst.setBoolean(3, uF.parseToBoolean("FALSE"));
						pst.setInt(4, uF.parseToInt(getStrEmployeeId()));
						pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
						pst.setInt(6, 1);
						pst.setDate(7, uF.getDateFormat(arrFinancialYear[0], DATE_FORMAT));
						pst.setDate(8, uF.getDateFormat(arrFinancialYear[1], DATE_FORMAT));
						pst.setDate(9, uF.getCurrentDate(CF.getStrTimeZone()));
						pst.setString(10, strSubSectionName[j]);
						pst.setInt(11, uF.parseToInt(sectionId[i]));
						pst.setInt(12, uF.parseToInt(strSubSectionNo[j]));
						pst.setDouble(13, uF.parseToDouble(strSubSectionAmount[j]));
						pst.setString(14, strSubSectionLimitType[j]);
//						System.out.println("sub pst====>"+pst);
//						if(strSubAmountPaid[j]!=null && strSubAmountPaid[j].length()>0 && !alInvestmentApproved.contains(strSubInvestmentId[j])){
						if(!alInvestmentApproved.contains(strSubInvestmentId[j])) {
							nSubUpdate = pst.executeUpdate();
							pst.close();
							
							pst = con.prepareStatement("update investment_details set status = false, trail_status=0 where investment_id=?");
							pst.setInt(1, uF.parseToInt(strSubInvestmentId[j]));
//							System.out.println("sub false pst====>"+pst);
							pst.execute();
							pst.close();
							
						} 
						
						if(nSubUpdate==0 && !alInvestmentApproved.contains(strSubInvestmentId[j])){
							pst = con.prepareStatement("INSERT INTO investment_details (section_id, amount_paid, status, emp_id, entry_date," +
									"child_section,parent_section) values (?,?,?,?,?,?,?)");
							pst.setInt(1, uF.parseToInt(sectionId[i]));
							pst.setDouble(2, uF.parseToDouble(strSubAmountPaid[j]));
							pst.setBoolean(3, uF.parseToBoolean("FALSE"));						
							pst.setInt(4, uF.parseToInt(getStrEmployeeId()));						
							pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
							pst.setString(6, strSubSectionName[j]);
							pst.setInt(7, uF.parseToInt(sectionId[i]));
							if(strSubAmountPaid[j]!=null && strSubAmountPaid[j].length()>0) {
//								System.out.println("==+sub section Inserting ===="+strAmountPaid[j]);
								pst.execute();
								pst.close();
								
							}						
						}
					}
				}
			}
			
			
			for(int i=0; getSectionId()!=null && i<getSectionId().length; i++) {

				String strFileName = null;
//				if(CF.getStrDocSaveLocation()!=null){
//					strFileName = uF.uploadFile(request, CF.getStrDocSaveLocation(), getSectionDoc()[i], getSectionDocFileName()[i], CF.getIsRemoteLocation(), CF);
//				}else{
//					strFileName = uF.uploadFile(request, DOCUMENT_LOCATION, getSectionDoc()[i], getSectionDocFileName()[i], CF.getIsRemoteLocation(), CF);
//				}
				if(CF.getStrDocSaveLocation()==null){
					strFileName = uF.uploadImageDocuments(request, DOCUMENT_LOCATION, getSectionDoc()[i], getSectionDocFileName()[i], getSectionDocFileName()[i], CF);
				}else{
					strFileName = uF.uploadImageDocuments(request, CF.getStrDocSaveLocation()+I_INVESTMENTS+"/"+I_DOCUMENT+"/"+getStrEmployeeId(), getSectionDoc()[i], getSectionDocFileName()[i], getSectionDocFileName()[i], CF);
				} 
				
				
				pst = con.prepareStatement("insert into investment_documents (document_name, emp_id, section_id, fy_from, fy_to, entry_date) values (?,?,?,?,?,?)");
				pst.setString(1, strFileName);
//				pst.setInt(2, uF.parseToInt((String)session.getAttribute(EMPID)));
				pst.setInt(2, uF.parseToInt(getStrEmployeeId()));
				
				pst.setInt(3, uF.parseToInt(getSectionId()[i]));
				pst.setDate(4, uF.getDateFormat(arrFinancialYear[0], DATE_FORMAT));
				pst.setDate(5, uF.getDateFormat(arrFinancialYear[1], DATE_FORMAT));
				pst.setTimestamp(6, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone()), DBDATE+DBTIME));
				pst.execute();
				pst.close();
				
			}
			
			/**
			 * Other Investment
			 * */
			
			if(getRemoveSubOtherInvestmentId() !=null && !getRemoveSubOtherInvestmentId().trim().equals("") 
					&& !getRemoveSubOtherInvestmentId().trim().equalsIgnoreCase("")){
				List<String> alInvestmentId = Arrays.asList(getRemoveSubOtherInvestmentId().trim().split(","));
				if(alInvestmentId == null) alInvestmentId = new ArrayList<String>();
				
				StringBuilder sbSubInvestmentId = null;
				for(String id : alInvestmentId){
					if(uF.parseToInt(id.trim()) == 0){
						continue;
					}
						
					if(sbSubInvestmentId == null){
						sbSubInvestmentId = new StringBuilder();
						sbSubInvestmentId.append(id.trim());
					} else {
						sbSubInvestmentId.append(","+id.trim());
					}
				}
				if(sbSubInvestmentId !=null && sbSubInvestmentId.length() > 0){
					pst = con.prepareStatement("update investment_details set status = false, trail_status=0 where investment_id in ("+sbSubInvestmentId.toString()+")");
//					System.out.println("sub other pst====>"+pst);
					pst.execute();
					pst.close();
				}
			}
			
			String []OthersectionId = getStrOtherSectionId();
			String []strOtherAmountPaid = getStrOtherAmountPaid();
			
			for(int i=0; i<OthersectionId.length; i++){
				
				int nUpdate = 0;
				
				pst = con.prepareStatement("insert into investment_details (salary_head_id , amount_paid, status, emp_id, entry_date, trail_status, fy_from, fy_to, agreed_date) values (?,?,?,?,?,?,?,?,?)");
				pst.setInt(1, uF.parseToInt(OthersectionId[i]));
				pst.setDouble(2, uF.parseToDouble(strOtherAmountPaid[i]));
				pst.setBoolean(3, uF.parseToBoolean("FALSE"));
				pst.setInt(4, uF.parseToInt(getStrEmployeeId()));
				
				pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
				//pst.setInt(6, uF.parseToInt(getStrInvestmentId()[i]));
				pst.setInt(6, 1);
				pst.setDate(7, uF.getDateFormat(arrFinancialYear[0], DATE_FORMAT));
				pst.setDate(8, uF.getDateFormat(arrFinancialYear[1], DATE_FORMAT));
				pst.setDate(9, uF.getCurrentDate(CF.getStrTimeZone()));
				
//				if(strOtherAmountPaid[i]!=null && strOtherAmountPaid[i].length()>0 && !alInvestmentApproved.contains(getStrOtherInvestmentId()[i])){
				if(!alInvestmentApproved.contains(getStrOtherInvestmentId()[i])){
					nUpdate = pst.executeUpdate();
					pst.close();
					
					pst = con.prepareStatement("update investment_details set status = false, trail_status=0 where investment_id=?");
					pst.setInt(1, uF.parseToInt(getStrOtherInvestmentId()[i]));
					pst.execute();
				}
				//pst.clearParameters();
				
				if(nUpdate==0 && !alInvestmentApproved.contains(getStrOtherInvestmentId()[i])){
					pst = con.prepareStatement("INSERT INTO investment_details (salary_head_id, amount_paid, status, emp_id, entry_date) values (?,?,?,?,?)");
					pst.setInt(1, uF.parseToInt(OthersectionId[i]));
					pst.setDouble(2, uF.parseToDouble(strOtherAmountPaid[i]));
					pst.setBoolean(3, uF.parseToBoolean("FALSE"));
					
					pst.setInt(4, uF.parseToInt(getStrEmployeeId()));
					
					pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
					
					if(strOtherAmountPaid[i]!=null && strOtherAmountPaid[i].length()>0){
						pst.execute();
						pst.close();
//						System.out.println("==+Inserting ===="+strAmountPaid[i]);
					}
//					pst.clearParameters();
				}
				
			} 
			
			for(int i=0; i<OthersectionId.length; i++){				
				String[] strSubOtherSectionName=request.getParameterValues("strSubOtherSectionId_"+OthersectionId[i]);
				String[] strSubOtherAmountPaid=request.getParameterValues("strOtherAmountPaid_"+OthersectionId[i]);
				String[] strSubOtherInvestmentId=request.getParameterValues("strSubOtherInvestmentId_"+OthersectionId[i]);
				 
				for(int j=0; strSubOtherSectionName!=null && j<strSubOtherSectionName.length; j++){
					int nSubUpdate = 0;	
					if(strSubOtherSectionName[j]!=null && !strSubOtherSectionName[j].equals("")){
						pst = con.prepareStatement("insert into investment_details (salary_head_id, amount_paid, status, emp_id, entry_date, trail_status," +
								" fy_from, fy_to, agreed_date,child_section,parent_section) values (?,?,?,?,?,?,?,?,?,?,?)");
						pst.setInt(1, uF.parseToInt(OthersectionId[i]));
						pst.setDouble(2, uF.parseToDouble(strSubOtherAmountPaid[j]));
						pst.setBoolean(3, uF.parseToBoolean("FALSE"));
						pst.setInt(4, uF.parseToInt(getStrEmployeeId()));
						pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
						pst.setInt(6, 1);
						pst.setDate(7, uF.getDateFormat(arrFinancialYear[0], DATE_FORMAT));
						pst.setDate(8, uF.getDateFormat(arrFinancialYear[1], DATE_FORMAT));
						pst.setDate(9, uF.getCurrentDate(CF.getStrTimeZone()));
						pst.setString(10, strSubOtherSectionName[j]);
						pst.setInt(11, uF.parseToInt(OthersectionId[i]));
//						if(strSubOtherAmountPaid[j]!=null && strSubOtherAmountPaid[j].length()>0 && !alInvestmentApproved.contains(strSubOtherInvestmentId[j])){
						if(!alInvestmentApproved.contains(strSubOtherInvestmentId[j])){
							nSubUpdate = pst.executeUpdate();
							pst.close();
							
							pst = con.prepareStatement("update investment_details set status = false, trail_status=0 where investment_id=?");
							pst.setInt(1, uF.parseToInt(strSubOtherInvestmentId[j]));
							pst.execute();
							pst.close();
							
						} 
						
						if(nSubUpdate==0 && !alInvestmentApproved.contains(strSubOtherInvestmentId[j])){
							pst = con.prepareStatement("INSERT INTO investment_details (salary_head_id , amount_paid, status, emp_id, entry_date," +
									"child_section,parent_section) values (?,?,?,?,?,?,?)");
							pst.setInt(1, uF.parseToInt(OthersectionId[i]));
							pst.setDouble(2, uF.parseToDouble(strSubOtherAmountPaid[j]));
							pst.setBoolean(3, uF.parseToBoolean("FALSE"));						
							pst.setInt(4, uF.parseToInt(getStrEmployeeId()));						
							pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
							pst.setString(6, strSubOtherSectionName[j]);
							pst.setInt(7, uF.parseToInt(OthersectionId[i]));
							if(strSubOtherAmountPaid[j]!=null && strSubOtherAmountPaid[j].length()>0){
								pst.execute();
								pst.close();
//								System.out.println("==+sub section Inserting ===="+strAmountPaid[j]);
							}						
						}
					}
				}
			}
			
			
			for(int i=0; getOthersectionId()!=null && i<getOthersectionId().length; i++){

				String strFileName = null;
//				if(CF.getStrDocSaveLocation()!=null){
//					strFileName = uF.uploadFile(request, CF.getStrDocSaveLocation(), getOthersectionDoc()[i], getOthersectionDocFileName()[i], CF.getIsRemoteLocation(), CF);
//				}else{
//					strFileName = uF.uploadFile(request, DOCUMENT_LOCATION, getOthersectionDoc()[i], getOthersectionDocFileName()[i], CF.getIsRemoteLocation(), CF);
//				} 
				if(CF.getStrDocSaveLocation()==null){
					strFileName = uF.uploadImageDocuments(request, DOCUMENT_LOCATION, getOthersectionDoc()[i], getOthersectionDocFileName()[i], getOthersectionDocFileName()[i], CF);
				}else{
					strFileName = uF.uploadImageDocuments(request, CF.getStrDocSaveLocation()+I_INVESTMENTS+"/"+I_DOCUMENT+"/"+getStrEmployeeId(), getOthersectionDoc()[i], getOthersectionDocFileName()[i], getOthersectionDocFileName()[i], CF);
				} 
				
				pst = con.prepareStatement("insert into investment_documents (document_name, emp_id, salary_head_id, fy_from, fy_to, entry_date) values (?,?,?,?,?,?)");
				pst.setString(1, strFileName);
//				pst.setInt(2, uF.parseToInt((String)session.getAttribute(EMPID)));
				pst.setInt(2, uF.parseToInt(getStrEmployeeId()));
				pst.setInt(3, uF.parseToInt(getOthersectionId()[i]));
				pst.setDate(4, uF.getDateFormat(arrFinancialYear[0], DATE_FORMAT));
				pst.setDate(5, uF.getDateFormat(arrFinancialYear[1], DATE_FORMAT));
				pst.setTimestamp(6, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone()), DBDATE+DBTIME));
				pst.execute();
				pst.close();
				
			}
			
			String strDomain = request.getServerName().split("\\.")[0];
			Map<String, String> hmUserTypeId = CF.getUserTypeIdMap(con);
			List<String> alAccountant = CF.getEmpAccountantList(con,uF,getStrEmployeeId(),hmUserTypeId);
			if(alAccountant == null) alAccountant = new ArrayList<String>();
//			System.out.println("alAccountant.size() ===>> " + alAccountant.size());
			if(alAccountant.size() > 0){
				int nAccountant = alAccountant.size();
				for(int i = 0; i < nAccountant; i++){
					String strAccountant = alAccountant.get(i);
//					System.out.println("strAccountant ===>> " + strAccountant);
					String alertData = "<div style=\"float: left;\"> IT Declaration, <b>"+CF.getEmpNameMapByEmpId(con, getStrEmployeeId())+"</b> has been submitted for financial year <b>"+uF.getDateFormat(arrFinancialYear[0], DATE_FORMAT, CF.getStrReportDateFormat())+"</b> - <b>"+uF.getDateFormat(arrFinancialYear[1], DATE_FORMAT, CF.getStrReportDateFormat())+"</b>. PLease Check. </div>";
					String alertAction = "Compliance.action?pType=WR";
					UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
					userAlerts.setStrDomain(strDomain);
					userAlerts.setStrEmpId(strAccountant);
					userAlerts.setStrData(alertData);
					userAlerts.setStrAction(alertAction);
					userAlerts.setCurrUserTypeID(hmUserTypeId.get(ACCOUNTANT));
					userAlerts.setStatus(INSERT_WR_ALERT);
					Thread t = new Thread(userAlerts);
					t.run();
				}
			} else {
				List<String> alGlobalHR = CF.getGlobalHRList(con,uF,hmUserTypeId);
				if(alGlobalHR == null) alGlobalHR = new ArrayList<String>();
				int nGlobalHR = alGlobalHR.size();
				for(int i = 0; i < nGlobalHR; i++){
					String strGlobalHR = alGlobalHR.get(i);
//					System.out.println("strGlobalHR ===>> " + strGlobalHR);
					String alertData = "<div style=\"float: left;\"> IT Declaration, <b>"+CF.getEmpNameMapByEmpId(con, getStrEmployeeId())+"</b> has been submitted for financial year <b>"+uF.getDateFormat(arrFinancialYear[0], DATE_FORMAT, CF.getStrReportDateFormat())+"</b> - <b>"+uF.getDateFormat(arrFinancialYear[1], DATE_FORMAT, CF.getStrReportDateFormat())+"</b>. PLease Check. </div>";
					String alertAction = "Compliance.action?pType=WR";
					UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
					userAlerts.setStrDomain(strDomain);
					userAlerts.setStrEmpId(strGlobalHR);
					userAlerts.setStrData(alertData);
					userAlerts.setStrAction(alertAction);
					userAlerts.setCurrUserTypeID(hmUserTypeId.get(ADMIN));
					userAlerts.setStatus(INSERT_WR_ALERT);
					Thread t = new Thread(userAlerts);
					t.run();
				}
			}
			
			session.setAttribute(MESSAGE, SUCCESSM+"IT Declaration inserted Successfully."+END);

		} catch (Exception e) {
			session.setAttribute(MESSAGE, ERRORM+"IT Declaration insert Failed."+END);
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;
	}
	
	
	
	public String updateInvestment() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		int columnId = Integer.parseInt(request.getParameter("columnId"));
		String columnName=null;
		
		switch(columnId) {
			
			case 0 : columnName = "section_id"; break;
			case 1 : columnName = "amount_paid"; break;
		
		}
		
		String updateInvestment = "UPDATE investment_details SET "+columnName+"=? WHERE investment_id=?";
		
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement(updateInvestment);
			if(columnId==0)
				pst.setInt(1, uF.parseToInt(request.getParameter("value")));
			else
				pst.setDouble(1, uF.parseToDouble(request.getParameter("value")));
			
			pst.setInt(2, uF.parseToInt(request.getParameter("id")));
			pst.execute();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
			return ERROR;
		} finally {
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return UPDATE;

	}
	
	public String deleteInvestment() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement(deleteInvestment);
			pst.setInt(1, uF.parseToInt(request.getParameter("id")));
			pst.execute();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
			return ERROR;
		} finally {
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}

	
	
	
	
	
	public void validate() {

		/*if (getInvestmentId() != null && getInvestmentId().length() == 0) {
			addFieldError("investmentId", "Investment ID is required");
		}
		if (getInvestmentName() != null && getInvestmentName().length() == 0) {
			addFieldError("password", "Investment Name is required");
		}
		if (getInvestmentCode() != null && getInvestmentCode().length() == 0) {
			addFieldError("investmentCode", "Investment Code is required");
		}*/
		
		loadValidateInvestment();

	}

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public String getSection() {
		return section;
	}

	public void setSection(String section) {
		this.section = section;
	}

	public String getAmountPaid() {
		return amountPaid;
	}

	public void setAmountPaid(String amountPaid) {
		this.amountPaid = amountPaid;
	}
	
	public boolean isAgree() {
		return isAgree;
	}

	public void setAgree(boolean isAgree) {
		this.isAgree = isAgree;
	}

	public String[] getStrSectionId() {
		return strSectionId;
	}

	public void setStrSectionId(String[] strSectionId) {
		this.strSectionId = strSectionId;
	}

	public String[] getStrAmountPaid() {
		return strAmountPaid;
	}

	public void setStrAmountPaid(String[] strAmountPaid) {
		this.strAmountPaid = strAmountPaid;
	}

	public String[] getSectionId() {
		return sectionId;
	}

	public void setSectionId(String[] sectionId) {
		this.sectionId = sectionId;
	}

	public File[] getSectionDoc() {
		return sectionDoc;
	}

	public void setSectionDoc(File[] sectionDoc) {
		this.sectionDoc = sectionDoc;
	}

	public String[] getSectionDocFileName() {
		return sectionDocFileName;
	}

	public void setSectionDocFileName(String[] sectionDocFileName) {
		this.sectionDocFileName = sectionDocFileName;
	}

	public String[] getStrInvestmentId() {
		return strInvestmentId;
	}

	public void setStrInvestmentId(String[] strInvestmentId) {
		this.strInvestmentId = strInvestmentId;
	}

	public String getStrEmployeeId() {
		return strEmployeeId;
	}

	public void setStrEmployeeId(String strEmployeeId) {
		this.strEmployeeId = strEmployeeId;
	}

	public String getF_strFinancialYear() {
		return f_strFinancialYear;
	}

	public void setF_strFinancialYear(String f_strFinancialYear) {
		this.f_strFinancialYear = f_strFinancialYear;
	}

	public String[] getOthersectionId() {
		return othersectionId;
	}

	public void setOthersectionId(String[] othersectionId) {
		this.othersectionId = othersectionId;
	}

	public File[] getOthersectionDoc() {
		return othersectionDoc;
	}

	public void setOthersectionDoc(File[] othersectionDoc) {
		this.othersectionDoc = othersectionDoc;
	}

	public String[] getOthersectionDocFileName() {
		return othersectionDocFileName;
	}

	public void setOthersectionDocFileName(String[] othersectionDocFileName) {
		this.othersectionDocFileName = othersectionDocFileName;
	}

	public String[] getStrOtherInvestmentId() {
		return strOtherInvestmentId;
	}

	public void setStrOtherInvestmentId(String[] strOtherInvestmentId) {
		this.strOtherInvestmentId = strOtherInvestmentId;
	}

	public String[] getStrOtherSectionId() {
		return strOtherSectionId;
	}

	public void setStrOtherSectionId(String[] strOtherSectionId) {
		this.strOtherSectionId = strOtherSectionId;
	}

	public String[] getStrOtherAmountPaid() {
		return strOtherAmountPaid;
	}

	public void setStrOtherAmountPaid(String[] strOtherAmountPaid) {
		this.strOtherAmountPaid = strOtherAmountPaid;
	}

	public String getRemoveSubInvestmentId() {
		return removeSubInvestmentId;
	}

	public void setRemoveSubInvestmentId(String removeSubInvestmentId) {
		this.removeSubInvestmentId = removeSubInvestmentId;
	}


	public String getRemoveSubOtherInvestmentId() {
		return removeSubOtherInvestmentId;
	}


	public void setRemoveSubOtherInvestmentId(String removeSubOtherInvestmentId) {
		this.removeSubOtherInvestmentId = removeSubOtherInvestmentId;
	}
	
}