package com.konnect.jpms.task;

import java.io.File;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.employee.AddEmployeeMode;
import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillDesig;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IConstants;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.Notifications;
import com.konnect.jpms.util.UploadImage;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class AddNewCustomer extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	
	HttpSession session;
	CommonFunctions CF = null;
	String strSessionEmpId;
	String strSessionOrgId;
	
	String fromPage;
	String proId;
	String clientId;
	String operation;
	
	String btnSave;
	String btnSaveAndSend;

	String strClientContactId;
	String strClientContactFName;
	String strClientContactMName; 
	String strClientContactLName;
	String strClientContactNo;
	String strClientContactEmail;
	String strClientContactDesig;
	String otherDesignation;
	String strClientContactDepartment;
	String otherDepartment;
	String strClientContactLocation;
	String otherLocation;
	File strClientContactPhoto;
	String strClientContactPhotoFileName;
	String strClientContactPhotoFile;
	String clientBrand;
	
	List<FillDesig> desigList;
	List<FillWLocation> workLocationList;
	List<FillDepartment> departmentList;
	List<FillClientBrand> clientBrandList;  
	
	public String execute() throws Exception {
		
		session = request.getSession(true);
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
//		if (CF == null) {
//			CF = new CommonFunctions();
//		}
		if(CF==null)return LOGIN;
		
		strSessionEmpId = (String)session.getAttribute(EMPID);
		strSessionOrgId = (String)session.getAttribute(ORGID);
		
		request.setAttribute(PAGE, PAddClient);
		String strId = request.getParameter("ID");
		
		UtilityFunctions uF = new UtilityFunctions();
		request.setAttribute("DOC_RETRIVE_LOCATION", CF.getStrDocRetriveLocation());
		
		CF.getFormValidationFields(request, ADD_UPDATE_COMPANY);
		
		desigList = new FillDesig(request).fillClientDesig();
		workLocationList = new FillWLocation(request).fillClientLocation();
		departmentList = new FillDepartment(request).fillClientDepartment();
		clientBrandList = new FillClientBrand(request).fillClientBrand(uF.parseToInt(getClientId()));
		
//		getFormValidationFields();
		
//		System.out.println("operation ===>> " + operation + "strId ===>> " + strId);
		
		if (operation!=null && operation.equals("D")) {
			return deleteCustomer(strId);
		}
		
		if (operation!=null && operation.equals("RA")) {
			return resendCustomerAccess(strId);
		}
		
		if (operation!=null && operation.equals("DA")) {
			return disableCustomerAccess(strId);
		}
		
		if (operation!=null && operation.equals("E")) {
			return viewCustomer(strId);
		}
		
		if (getStrClientContactId() !=null && getStrClientContactId().length()>0) {
//				deletePoc(getStrClientId());
				return updateCustomer();
		}
		
		if (getStrClientContactFName()!=null && getStrClientContactFName().length()>0) {
			return insertCustomer();
		} else if ((getStrClientContactFName()==null || getStrClientContactFName().length()==0) && (operation == null || operation.equals(""))) {
			session.setAttribute(MESSAGE, SUCCESSM+"No customer added, please try again."+END);
			return SUCCESS;
		}
		
		loadingData();
		
		return LOAD;
	}
	
	
	private void loadingData() {
		// TODO Auto-generated method stub
		
		UtilityFunctions uF = new UtilityFunctions();
		
		StringBuilder sbDesig = new StringBuilder();
		for(FillDesig fillDesigList: desigList) {
			String strSelected ="";
			if(uF.parseToInt(fillDesigList.getDesigId()) == uF.parseToInt(getStrClientContactDesig())) {
				strSelected = " selected";
			}
			sbDesig.append("<option value=\""+fillDesigList.getDesigId()+"\" "+strSelected+">"+fillDesigList.getDesigCodeName()+"</option>");
		}
		StringBuilder sbDepart = new StringBuilder();
		for(FillDepartment fillDepartList: departmentList) {
			String strSelected ="";
			if(uF.parseToInt(fillDepartList.getDeptId()) == uF.parseToInt(getStrClientContactDepartment())) {
				strSelected = " selected";
			}
			sbDepart.append("<option value=\""+fillDepartList.getDeptId()+"\" "+strSelected+">"+fillDepartList.getDeptName()+"</option>");
//			sbDepartAjax.append("<option value="+fillDepartList.getDeptId()+">"+fillDepartList.getDeptName()+"</option>");
		}
		
		StringBuilder sbWLoc = new StringBuilder();
		for(FillWLocation fillWLocList: workLocationList) {
			String strSelected ="";
			if(uF.parseToInt(fillWLocList.getwLocationId()) == uF.parseToInt(getStrClientContactLocation())) {
				strSelected = " selected";
			}
			sbWLoc.append("<option value=\""+fillWLocList.getwLocationId()+"\" "+strSelected+">"+fillWLocList.getwLocationName()+"</option>");
		}
		
		request.setAttribute("sbDesig", sbDesig.toString());
		request.setAttribute("sbDepart", sbDepart.toString());
		request.setAttribute("sbWLoc", sbWLoc.toString());
		
//		request.setAttribute("sbDesigAjax", sbDesigAjax.toString());
//		request.setAttribute("sbDepartAjax", sbDepartAjax.toString());
//		request.setAttribute("sbWLocAjax", sbWLocAjax.toString());
	}
	
//	private void setValidationToField() {
//
//		UtilityFunctions uF = new UtilityFunctions();
//		
//		Map<String, List<String>> hmValidationFields = (Map<String, List<String>> ) request.getAttribute("hmValidationFields");
//		List<String> compContactFNameValidList = hmValidationFields.get("COMPANY_CONTACT_FIRST_NAME"); 
//		String compContactFNameValidReqOpt = "";
//		String compContactFNameValidAsterix = "";
//		if(uF.parseToBoolean(compContactFNameValidList.get(0))) {
//			compContactFNameValidReqOpt = "validate[required]";
//			compContactFNameValidAsterix = "<sup>*</sup>";
//		}
//		
//		List<String> compContactLNameValidList = hmValidationFields.get("COMPANY_CONTACT_LAST_NAME"); 
//		String compContactLNameValidReqOpt = "";
//		String compContactLNameValidAsterix = "";
//		if(uF.parseToBoolean(compContactLNameValidList.get(0))) {
//			compContactLNameValidReqOpt = "validate[required]";
//			compContactLNameValidAsterix = "<sup>*</sup>";
//		}
//	
//		request.setAttribute("compContactFNameValidReqOpt", compContactFNameValidReqOpt);
//		request.setAttribute("compContactFNameValidAsterix", compContactFNameValidAsterix);
//		request.setAttribute("compContactLNameValidReqOpt", compContactLNameValidReqOpt);
//		request.setAttribute("compContactLNameValidAsterix", compContactLNameValidAsterix);
//	}


	private String disableCustomerAccess(String strId) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		String strCustName = null;
		
		try {
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("select contact_fname,contact_lname from client_poc where poc_id=?");
			pst.setInt(1, uF.parseToInt(strId));
			rs = pst.executeQuery();
			while (rs.next()) {
				strCustName = rs.getString("contact_fname") + " " + rs.getString("contact_lname");
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("update user_details_customer set status='INACTIVE' where emp_id=?");
			pst.setInt(1, uF.parseToInt(strId));
			pst.executeUpdate();
			pst.close();
					
			session.setAttribute(MESSAGE, SUCCESSM+"Disable access for "+uF.showData(strCustName, "")+" successfully."+END);
		} catch (Exception e) {
			e.printStackTrace();
			session.setAttribute(MESSAGE, ERRORM+"Error while updating "+uF.showData(strCustName, "")+". Please try again."+END);
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;
	}


	private String resendCustomerAccess(String strId) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		String strCustName = null;
		
		try {
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("select contact_fname,contact_lname from client_poc where poc_id=?");
			pst.setInt(1, uF.parseToInt(strId));
			rs = pst.executeQuery();
			while (rs.next()) {
				strCustName = rs.getString("contact_fname") + " " + rs.getString("contact_lname");
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("update user_details_customer set status='ACTIVE' where emp_id=?");
			pst.setInt(1, uF.parseToInt(strId));
			pst.executeUpdate();
			pst.close();
					
			session.setAttribute(MESSAGE, SUCCESSM+"Resend access to "+uF.showData(strCustName, "")+" successfully."+END);
		} catch (Exception e) {
			e.printStackTrace();
			session.setAttribute(MESSAGE, ERRORM+"Error while updating "+uF.showData(strCustName, "")+". Please try again."+END);
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;
	}


//	private void getFormValidationFields() {
//
//		Connection con = null;
//		PreparedStatement pst;
//		ResultSet rs = null;
//		Database db = new Database();
//		db.setRequest(request);
//		try {
//			con = db.makeConnection(con);
//			Map<String, List<String>> hmValidationFields = new HashMap<String, List<String>>();
//			
//			pst = con.prepareStatement("select * from validation_details where form_name = ?");
//			pst.setString(1, ADD_UPDATE_COMPANY);
//			rs = pst.executeQuery();
//			List<String> innerList = new ArrayList<String>();
//			while(rs.next()) {
//				innerList = hmValidationFields.get(rs.getString("field_name"));
//				if(innerList == null) innerList = new ArrayList<String>();
//				innerList.add(rs.getString("required_field"));
//				innerList.add(rs.getString("optional_field"));
//				innerList.add(rs.getString("optional_field_value"));
//				hmValidationFields.put(rs.getString("field_name"), innerList);
//			}
//			
//			request.setAttribute("hmValidationFields", hmValidationFields);
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			db.closeConnection(con);
//		}
//	
//	}


	public String viewCustomer(String strId) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from client_poc where poc_id=?");
			pst.setInt(1, uF.parseToInt(strId));
			rs = pst.executeQuery();
			
			int i=0;
			while(rs.next()) {
				if(i==0) {
					setStrClientContactId(rs.getString("poc_id"));
					setStrClientContactFName(uF.showData(rs.getString("contact_fname"), ""));
					setStrClientContactMName(uF.showData(rs.getString("contact_mname"), ""));
					setStrClientContactLName(uF.showData(rs.getString("contact_lname"), ""));
					setStrClientContactNo(uF.showData(rs.getString("contact_number"), ""));
					setStrClientContactEmail(uF.showData(rs.getString("contact_email"), ""));
					setStrClientContactDesig(uF.showData(rs.getString("contact_desig_id"), ""));
					setStrClientContactDepartment(uF.showData(rs.getString("contact_department_id"), ""));
					setStrClientContactLocation(uF.showData(rs.getString("contact_location_id"), ""));
//					sb444.append(uF.showData(rs.getString("contact_photo"), ""));
					
//					CF.getStrDocSaveLocation()+"/"+I_CUSTOMER+"/"+getCustomerID()+"/"+I_IMAGE+"/"+I_CUSTOMER_SPOC+"/"
					String contactImage = "";
					if(CF.getStrDocRetriveLocation() == null) {
						contactImage = IConstants.IMAGE_LOCATION + ((rs.getString("contact_photo") !=null && !rs.getString("contact_photo").equals("")) ? rs.getString("contact_photo"):"avatar_photo.png");
					} else { 
					contactImage = IConstants.IMAGE_LOCATION + ((rs.getString("contact_photo")!=null && !rs.getString("contact_photo").equals("")) ? rs.getString("contact_photo"):"avatar_photo.png");
						if(rs.getString("contact_photo")!=null && !rs.getString("contact_photo").equals("")) {
							contactImage = CF.getStrDocRetriveLocation() +IConstants.I_CUSTOMER+"/"+rs.getString("client_id")+"/"+IConstants.I_IMAGE+"/"+I_CUSTOMER_SPOC+"/"+((rs.getString("contact_photo")!=null && !rs.getString("contact_photo").equals("")) ? rs.getString("contact_photo"):"avatar_photo.png");
						}
					}
					setStrClientContactPhotoFile(uF.showData(contactImage, ""));
					
					setClientBrand(rs.getString("client_brand_id"));
				}
				i++;
			}
			rs.close();
			pst.close();
			
			loadingData();
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return LOAD;
	}
	
	
	public String loadValidateDepartment() {
		request.setAttribute(PAGE, PAddDepartment);
		request.setAttribute(TITLE, TAddDepartment);
		
		return LOAD;
	}

	public String insertCustomer() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			
			con = db.makeConnection(con);
			
			int otherDesigId = 0;
			int otherDepartId = 0;
			int otherLocId = 0;
			
			System.out.println("getOtherDesignation() ===>> " + getOtherDesignation());
			System.out.println("getOtherDepartment() ===>> " + getOtherDepartment());
			System.out.println("getOtherLocation() ===>> " + getOtherLocation());
			if(getOtherDesignation() != null && !getOtherDesignation().equals("")) {
				pst = con.prepareStatement("insert into client_designations(client_desig_name) values (?)");
				pst.setString(1, getOtherDesignation());
				pst.executeUpdate();
				pst.close();
				
				pst = con.prepareStatement("select max(client_desig_id) as client_desig_id from client_designations");
				rs = pst.executeQuery();
				if(rs.next()) {
					otherDesigId = rs.getInt("client_desig_id");
				}
				rs.close();
				pst.close();
			}
			if(getOtherDepartment() != null && !getOtherDepartment().equals("")) {
				pst = con.prepareStatement("insert into client_departments(client_depart_name) values (?)");
				pst.setString(1, getOtherDepartment());
				pst.executeUpdate();
				pst.close();
				
				pst = con.prepareStatement("select max(client_depart_id) as client_depart_id from client_departments");
				rs = pst.executeQuery();
				if(rs.next()) {
					otherDepartId = rs.getInt("client_depart_id");
				}
				rs.close();
				pst.close();
			}
			if(getOtherLocation() != null && !getOtherLocation().equals("")) {
				pst = con.prepareStatement("insert into client_locations(client_loc_name) values (?)");
				pst.setString(1, getOtherLocation());
				pst.executeUpdate();
				pst.close();
				
				pst = con.prepareStatement("select max(client_loc_id) as client_loc_id from client_locations");
				rs = pst.executeQuery();
				if(rs.next()) {
					otherLocId = rs.getInt("client_loc_id");
				}
				rs.close();
				pst.close();
			}
			
			pst = con.prepareStatement("insert into client_poc (contact_fname, contact_mname, contact_lname, contact_email, contact_number, " +
				"contact_desig_id, contact_department_id, contact_location_id, client_id, added_by,client_brand_id,entry_date) " +
				"values (?,?,?,?, ?,?,?,?, ?,?,?,?)");
			pst.setString(1, showValue(getStrClientContactFName(), "N/A"));
			pst.setString(2, showValue(getStrClientContactMName(), "N/A"));
			pst.setString(3, showValue(getStrClientContactLName(), "N/A"));
			pst.setString(4, showValue(getStrClientContactEmail(), "N/A")); 
			pst.setString(5, showValue(getStrClientContactNo(), "N/A")); 
			pst.setInt(6, (uF.parseToInt(getStrClientContactDesig()) > 0) ? uF.parseToInt(getStrClientContactDesig()) : otherDesigId);
			pst.setInt(7, (uF.parseToInt(getStrClientContactDepartment())>0) ? uF.parseToInt(getStrClientContactDepartment()) : otherDepartId);
			pst.setInt(8, (uF.parseToInt(getStrClientContactLocation())>0) ? uF.parseToInt(getStrClientContactLocation()) : otherLocId);
			pst.setInt(9, uF.parseToInt(getClientId()));
			pst.setInt(10, uF.parseToInt(strSessionEmpId));
			pst.setInt(11, uF.parseToInt(getClientBrand()));
			pst.setDate(12, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.executeUpdate();
//				System.out.println("pst ===>> " + pst);
			pst.close();
			
			String poc_id = "";
			pst = con.prepareStatement("select max(poc_id) as poc_id from client_poc");
			rs = pst.executeQuery();
			while(rs.next()) {
				poc_id = rs.getString("poc_id");
			}
			rs.close();
			pst.close();
			
			
			Map<String,String> userPresent = CF.getCustomerUsersMap(con);
			SecureRandom random = new SecureRandom();
			String password = new BigInteger(130, random).toString(32).substring(5, 13);
			
			AddEmployeeMode aE = new AddEmployeeMode();
			aE.setServletRequest(request);
			aE.CF = CF;
			aE.setFname(getStrClientContactFName().trim());
			aE.setLname(getStrClientContactLName().trim());
			String username = aE.getUserName(userPresent);
			
			// insert into employ User detail
			pst = con.prepareStatement("insert into user_details_customer(username, password, usertype_id, emp_id, status, " +
				"is_termscondition, added_timestamp) values (?,?,?,?,?,?,?) ");
			pst.setString(1, username);
			pst.setString(2, password);
			pst.setInt(3, 12);
			pst.setInt(4, uF.parseToInt(poc_id));
			pst.setString(5, "ACTIVE");
			pst.setBoolean(6, true);
			pst.setTimestamp(7, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone())+"", DBDATE+DBTIME));
			pst.executeUpdate();
			pst.close();
			
//				String strSubject = "Please use the link below to start using Taskrig";
//				
//				String strBody = "Dear "+getStrClientContactFName().trim()+" "+getStrClientContactLName().trim()+"," + "<br>" + 
//				
//				"A Login has been created in Taskrig for the role- customer, under the "+CF.getClientNameById(con, getClientId())+" Company. " +
//				"Kindly use the link below to start using Taskrig" + "<br>" +
//				
//				CF.getStrEmailLocalHost() +request.getContextPath() +"/Login.action<br>" +
//
//				"Please use the following username and password <br>"+
//				
//				"Username: " + username + "<br> Password: " + password + "<br><br>"+
//				
//				"Please use the Customer radio button to use the software.<br>"+
//				
//				"In case you have any queries drop a mail to (info@taskrig.com)" + "<br>" +
//
//				"Sincerely," + "<br>" +
//
//				"HR Division" + "<br>";
			
			/*String strBody = "Dear "+getStrClientContactFName().trim()+" "+getStrClientContactLName().trim()+"," + "<br>" + 
					
					"A Login has been created in Taskrig for the role- customer, under the "+CF.getClientNameById(con, getClientId())+" Company. " +
					"Kindly use the Link below to register and start using Taskrig" + "<br>" +
					
					CF.getStrEmailLocalHost() +request.getContextPath() +"/CustomerLogin.action<br>" +

					"In case you have any queries drop a mail to (info@taskrig.com)" + "<br>" +

					"Sincerely," + "<br>" +

					"HR Division" + "<br>";*/
					
//				String strDomain = request.getServerName().split("\\.")[0];
//				CustomEmailer ce = new CustomEmailer(getStrClientContactEmail(), strSubject, strBody, strDomain);
//				ce.start();
			
			if(getBtnSaveAndSend() != null) {
				if(getStrClientContactEmail().trim()!=null && getStrClientContactEmail().trim().indexOf("@")>0) {
					String strDomain = request.getServerName().split("\\.")[0];
					Notifications nF = new Notifications(N_NEW_CLIENT_CONTACT, CF); 
					nF.setDomain(strDomain);
					
					nF.request = request;
					nF.setStrOrgId(strSessionOrgId);
					nF.setEmailTemplate(true);
					
					nF.setStrCustFName(getStrClientContactFName().trim());
					nF.setStrCustLName(getStrClientContactLName().trim());
					nF.setStrEmpMobileNo(getStrClientContactNo().trim());
					nF.setStrEmpEmail(getStrClientContactEmail().trim());
					nF.setStrEmailTo(getStrClientContactEmail().trim());
					nF.setStrHostAddress(CF.getStrEmailLocalHost());
					nF.setStrHostPort(CF.getStrHostPort());
					nF.setStrContextPath(request.getContextPath());
					nF.setStrCustomerRegisterLink("?custId="+poc_id);
					nF.setStrUserName(username);
					nF.setStrPassword(password);
					nF.sendNotifications();
				}
			}
			
			if(getStrClientContactPhoto() != null) {
				uploadImage1(uF.parseToInt(getClientId()), uF.parseToInt(poc_id), getStrClientContactPhoto(), getStrClientContactPhotoFileName());
			}

			session.setAttribute(MESSAGE, SUCCESSM+getStrClientContactFName().trim()+" "+getStrClientContactLName().trim()+" added successfully in client "+CF.getClientNameById(con, getClientId())+"."+END);
			
		} catch (Exception e) {
			e.printStackTrace();
			session.setAttribute(MESSAGE, ERRORM+"Error while saving "+getStrClientContactFName().trim()+" "+getStrClientContactLName().trim()+". Please try again."+END);
			return ERROR;
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		if(getFromPage() != null && getFromPage().equals("Project")) {
			return UPDATE;
		} else {
			return SUCCESS;
		}
	}

	public String updateCustomer() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		try {
			con = db.makeConnection(con);

			int otherDesigId = 0;
			int otherDepartId = 0;
			int otherLocId = 0;
			if(getOtherDesignation() != null && !getOtherDesignation().equals("")) {
				pst = con.prepareStatement("insert into client_designations(client_desig_name) values (?)");
				pst.setString(1, getOtherDesignation());
				pst.executeUpdate();
				pst.close();
				
				pst = con.prepareStatement("select max(client_desig_id) as client_desig_id from client_designations");
				rs = pst.executeQuery();
				if(rs.next()) {
					otherDesigId = rs.getInt("client_desig_id");
				}
				rs.close();
				pst.close();
			}
			if(getOtherDepartment() != null && !getOtherDepartment().equals("")) {
				pst = con.prepareStatement("insert into client_departments(client_depart_name) values (?)");
				pst.setString(1, getOtherDepartment());
				pst.executeUpdate();
				pst.close();
				
				pst = con.prepareStatement("select max(client_depart_id) as client_depart_id from client_departments");
				rs = pst.executeQuery();
				if(rs.next()) {
					otherDepartId = rs.getInt("client_depart_id");
				}
				rs.close();
				pst.close();
			}
			if(getOtherLocation() != null && !getOtherLocation().equals("")) {
				pst = con.prepareStatement("insert into client_locations(client_loc_name) values (?)");
				pst.setString(1, getOtherLocation());
				pst.executeUpdate();
				pst.close();
				
				pst = con.prepareStatement("select max(client_loc_id) as client_loc_id from client_locations");
				rs = pst.executeQuery();
				if(rs.next()) {
					otherLocId = rs.getInt("client_loc_id");
				}
				rs.close();
				pst.close();
			}
			pst = con.prepareStatement("update client_poc set contact_fname=?, contact_mname=?, contact_lname=?, contact_email=?, " +
				"contact_number=?, contact_desig_id=?, contact_department_id=?, contact_location_id=?, client_brand_id=?, " +
				"updated_by=?, update_date=? where poc_id=?");
			pst.setString(1, showValue(getStrClientContactFName(),"N/A"));
			pst.setString(2, showValue(getStrClientContactMName(),"N/A"));
			pst.setString(3, showValue(getStrClientContactLName(),"N/A"));
			
			pst.setString(4, showValue(getStrClientContactEmail(),"N/A")); 
			pst.setString(5, showValue(getStrClientContactNo(),"N/A")); 
			pst.setInt(6, (uF.parseToInt(getStrClientContactDesig()) > 0) ? uF.parseToInt(getStrClientContactDesig()) : otherDesigId);
			pst.setInt(7, (uF.parseToInt(getStrClientContactDepartment())>0) ? uF.parseToInt(getStrClientContactDepartment()) : otherDepartId);
			pst.setInt(8, (uF.parseToInt(getStrClientContactLocation())>0) ? uF.parseToInt(getStrClientContactLocation()) : otherLocId);
			pst.setInt(9, uF.parseToInt(getClientBrand()));
			pst.setInt(10, uF.parseToInt(strSessionEmpId));
			pst.setDate(11, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(12, uF.parseToInt(getStrClientContactId()));
			pst.executeUpdate();
			pst.close();
			
			if(getStrClientContactPhoto() != null) {
				uploadImage1(uF.parseToInt(getClientId()), uF.parseToInt(getStrClientContactId()), getStrClientContactPhoto(), getStrClientContactPhotoFileName());
			}
					
			session.setAttribute(MESSAGE, SUCCESSM+getStrClientContactFName().trim()+" "+getStrClientContactLName().trim()+" updated successfully "+CF.getClientNameById(con, getClientId())+"."+END);
		} catch (Exception e) {
			e.printStackTrace();
			session.setAttribute(MESSAGE, ERRORM+"Error while updating "+getStrClientContactFName().trim()+" "+getStrClientContactLName().trim()+". Please try again."+END);
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		if(getFromPage() != null && getFromPage().equals("Project")) {
			return "updatePClient"; 
		} else {
			return SUCCESS;
		}
	}
	
	
	private void uploadImage1(int customerId, int contactId, File contactPhoto, String contactPhotoFileName) {
			
			try {
				
				UploadImage uI = new UploadImage();
				uI.setServletRequest(request);
				uI.setImageType("CLIENT_CONTACT_PHOTO");
				uI.setEmpImage(contactPhoto);
				uI.setEmpImageFileName(contactPhotoFileName);
				uI.setContentID(contactId+"");
				uI.setCustomerID(customerId+"");
				uI.setCF(CF);
				uI.upoadImage();
				
			}catch (Exception e) {
				e.printStackTrace();
				
			}
			
		}

	public String showValue(String str, String showValue) {

		if(str == null){
			return showValue;
		}else if (str.equals(" ")) {
			return showValue;
		}else if (str.length()==0) {
			return showValue;
		}else if (str.equals("")){
			return showValue;
		}else if (str != null && str.equalsIgnoreCase("NULL")) {
			return showValue;
		} else {
			return str;
		}
	}
	
	
	public String deleteCustomer(String strId) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("delete from client_poc where poc_id=?");
			pst.setInt(1, uF.parseToInt(strId));
			pst.execute();
			pst.close();
			
			pst = con.prepareStatement("delete from user_details_customer where emp_id=?");
			pst.setInt(1, uF.parseToInt(strId));
			pst.execute();
			pst.close();
			
			session.setAttribute(MESSAGE, "Deleted successfully!");
		} catch (Exception e) {
			e.printStackTrace();
			session.setAttribute(MESSAGE, "Error in deletion");
		} finally {
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;
	}
	
	
	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public String getStrClientContactId() {
		return strClientContactId;
	}

	public void setStrClientContactId(String strClientContactId) {
		this.strClientContactId = strClientContactId;
	}

	public String getStrClientContactFName() {
		return strClientContactFName;
	}

	public void setStrClientContactFName(String strClientContactFName) {
		this.strClientContactFName = strClientContactFName;
	}

	public String getStrClientContactMName() {
		return strClientContactMName;
	}

	public void setStrClientContactMName(String strClientContactMName) {
		this.strClientContactMName = strClientContactMName;
	}

	public String getStrClientContactLName() {
		return strClientContactLName;
	}

	public void setStrClientContactLName(String strClientContactLName) {
		this.strClientContactLName = strClientContactLName;
	}

	public String getStrClientContactNo() {
		return strClientContactNo;
	}

	public void setStrClientContactNo(String strClientContactNo) {
		this.strClientContactNo = strClientContactNo;
	}

	public String getStrClientContactEmail() {
		return strClientContactEmail;
	}

	public void setStrClientContactEmail(String strClientContactEmail) {
		this.strClientContactEmail = strClientContactEmail;
	}

	public String getStrClientContactDesig() {
		return strClientContactDesig;
	}

	public void setStrClientContactDesig(String strClientContactDesig) {
		this.strClientContactDesig = strClientContactDesig;
	}

	public String getStrClientContactDepartment() {
		return strClientContactDepartment;
	}

	public void setStrClientContactDepartment(String strClientContactDepartment) {
		this.strClientContactDepartment = strClientContactDepartment;
	}

	public String getStrClientContactLocation() {
		return strClientContactLocation;
	}

	public void setStrClientContactLocation(String strClientContactLocation) {
		this.strClientContactLocation = strClientContactLocation;
	}

	public File getStrClientContactPhoto() {
		return strClientContactPhoto;
	}

	public void setStrClientContactPhoto(File strClientContactPhoto) {
		this.strClientContactPhoto = strClientContactPhoto;
	}

	public String getStrClientContactPhotoFileName() {
		return strClientContactPhotoFileName;
	}

	public void setStrClientContactPhotoFileName(String strClientContactPhotoFileName) {
		this.strClientContactPhotoFileName = strClientContactPhotoFileName;
	}

	public String getStrClientContactPhotoFile() {
		return strClientContactPhotoFile;
	}

	public void setStrClientContactPhotoFile(String strClientContactPhotoFile) {
		this.strClientContactPhotoFile = strClientContactPhotoFile;
	}

	public String getFromPage() {
		return fromPage;
	}

	public void setFromPage(String fromPage) {
		this.fromPage = fromPage;
	}

	public String getProId() {
		return proId;
	}

	public void setProId(String proId) {
		this.proId = proId;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
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

	public List<FillDepartment> getDepartmentList() {
		return departmentList;
	}

	public void setDepartmentList(List<FillDepartment> departmentList) {
		this.departmentList = departmentList;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getBtnSave() {
		return btnSave;
	}

	public void setBtnSave(String btnSave) {
		this.btnSave = btnSave;
	}

	public String getBtnSaveAndSend() {
		return btnSaveAndSend;
	}

	public void setBtnSaveAndSend(String btnSaveAndSend) {
		this.btnSaveAndSend = btnSaveAndSend;
	}

	public String getOtherDesignation() {
		return otherDesignation;
	}

	public void setOtherDesignation(String otherDesignation) {
		this.otherDesignation = otherDesignation;
	}

	public String getOtherDepartment() {
		return otherDepartment;
	}

	public void setOtherDepartment(String otherDepartment) {
		this.otherDepartment = otherDepartment;
	}

	public String getOtherLocation() {
		return otherLocation;
	}

	public void setOtherLocation(String otherLocation) {
		this.otherLocation = otherLocation;
	}

	public List<FillClientBrand> getClientBrandList() {
		return clientBrandList;
	}

	public void setClientBrandList(List<FillClientBrand> clientBrandList) {
		this.clientBrandList = clientBrandList;
	}

	public String getClientBrand() {
		return clientBrand;
	}

	public void setClientBrand(String clientBrand) {
		this.clientBrand = clientBrand;
	}

}