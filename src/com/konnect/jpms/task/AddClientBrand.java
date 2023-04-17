package com.konnect.jpms.task;

import java.io.File;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.dispatcher.multipart.MultiPartRequestWrapper;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.employee.AddEmployeeMode;
import com.konnect.jpms.select.FillClientIndustries;
import com.konnect.jpms.select.FillCountry;
import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillDesig;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillState;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IConstants;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.Notifications;
import com.konnect.jpms.util.UploadImage;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class AddClientBrand extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	
	HttpSession session;
	CommonFunctions CF = null;
	String strSessionEmpId;
	String strSessionOrgId;
	
	String fromPage;
	String proId;
	String clientId;
	String clientBrandId;
	String clientBrandName;
	String operation;
	
	String btnSave;
	String btnDelete;
	String btnSaveAndSend;

	String[] clientBrandCntId;
	String[] strClientBrandId;
	String[] strClientBrandName;
	File[] clientBrandLogo;
	String[] clientBrandLogoFileName;
	String[] strClientBrandAddress;
	String[] strClientBrandCity;
	String[] strClientBrandCountry;
	String[] strClientBrandState;
	String[] strBrandPinCode;
	String[] otherBrandIndustry;
	String[] companyBrandDescription;
	String[] companyBrandWebsite;
	String[] clientBrandTds;
	String[] clientBrandRegistrationNo;
	
	List<FillDesig> desigList;
	List<FillWLocation> workLocationList;
	List<FillDepartment> departmentList;
	List<FillClientBrand> clientBrandList;  
	
	List<FillClientIndustries> clientIndustryList;
	List<FillCountry> countryList;
	List<FillState> stateList;
	
	public String execute() throws Exception {
		
		session = request.getSession(true);
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
//		if (CF == null) {
//			CF = new CommonFunctions();
//		}
		if(CF==null)return LOGIN;
		
		strSessionEmpId = (String)session.getAttribute(EMPID);
		strSessionOrgId = (String)session.getAttribute(ORGID);
		
		request.setAttribute(PAGE, "/jsp/employee/AddClientBrand.jsp");
//		String strId = request.getParameter("ID");
		
		UtilityFunctions uF = new UtilityFunctions();
		request.setAttribute("DOC_RETRIVE_LOCATION", CF.getStrDocRetriveLocation());
		
		CF.getFormValidationFields(request, ADD_UPDATE_COMPANY);
		
		desigList = new FillDesig(request).fillClientDesig();
		workLocationList = new FillWLocation(request).fillClientLocation();
		departmentList = new FillDepartment(request).fillClientDepartment();
		clientBrandList = new FillClientBrand(request).fillClientBrand(uF.parseToInt(getClientId()));
		
		clientIndustryList = new FillClientIndustries(request).fillClientIndustries();
		countryList = new FillCountry(request).fillCountry();
		stateList = new FillState(request).fillState();
		
//		getFormValidationFields();
		
		System.out.println("operation ===>> " + operation + " -- clientId ===>> " + clientId + " -- clientBrandId ===>> " + clientBrandId);
		System.out.println("getBtnDelete() ===>> " + getBtnDelete() +" -- getBtnSave() ===>> " + getBtnSave());
		loadingData();
		
		if (getBtnDelete()!=null && getBtnDelete().equals("Delete")) {
			return deleteCustomer();
		}
		
		if (getOperation()!=null && getOperation().equals("E")) {
			return viewClientBrand();
		}
		
		if (getClientBrandId() !=null && getClientBrandId().length()>0) {
//				deletePoc(getStrClientId());
				return updateClientBrand();
		} else if ((getStrClientBrandName()!=null && getStrClientBrandName().length>0)) {
			return insertClientBrand();
		} else if ((getStrClientBrandName()==null || getStrClientBrandName().length==0) && (getOperation() == null || getOperation().equals(""))) {
			session.setAttribute(MESSAGE, SUCCESSM+"No customer added, please try again."+END);
			return SUCCESS;
		}
		
		
		
		return LOAD;
	}
	
	
	private void loadingData() {
		// TODO Auto-generated method stub
		
		UtilityFunctions uF = new UtilityFunctions();
		
		StringBuilder sbDesig = new StringBuilder();
		StringBuilder sbDesigAjax = new StringBuilder();
		for(FillDesig fillDesigList: desigList) {
			String strSelected ="";
//			if(uF.parseToInt(fillDesigList.getDesigId()) == uF.parseToInt(getStrClientContactDesig())) {
//				strSelected = " selected";
//			}
			sbDesig.append("<option value=\""+fillDesigList.getDesigId()+"\" "+strSelected+">"+fillDesigList.getDesigCodeName()+"</option>");
			sbDesigAjax.append("<option value="+fillDesigList.getDesigId()+">"+fillDesigList.getDesigCodeName()+"</option>");
		}
		StringBuilder sbDepart = new StringBuilder();
		StringBuilder sbDepartAjax = new StringBuilder();
		for(FillDepartment fillDepartList: departmentList) {
			String strSelected ="";
//			if(uF.parseToInt(fillDepartList.getDeptId()) == uF.parseToInt(getStrClientContactDepartment())) {
//				strSelected = " selected";
//			}
			sbDepart.append("<option value=\""+fillDepartList.getDeptId()+"\" "+strSelected+">"+fillDepartList.getDeptName()+"</option>");
			sbDepartAjax.append("<option value="+fillDepartList.getDeptId()+">"+fillDepartList.getDeptName()+"</option>");
		}
		
		StringBuilder sbWLoc = new StringBuilder();
		StringBuilder sbWLocAjax = new StringBuilder();
		for(FillWLocation fillWLocList: workLocationList) {
			String strSelected ="";
//			if(uF.parseToInt(fillWLocList.getwLocationId()) == uF.parseToInt(getStrClientContactLocation())) {
//				strSelected = " selected";
//			}
			sbWLoc.append("<option value=\""+fillWLocList.getwLocationId()+"\" "+strSelected+">"+fillWLocList.getwLocationName()+"</option>");
			sbWLocAjax.append("<option value="+fillWLocList.getwLocationId()+">"+fillWLocList.getwLocationName()+"</option>");
		}
		
		request.setAttribute("sbDesig", sbDesig.toString());
		request.setAttribute("sbDepart", sbDepart.toString());
		request.setAttribute("sbWLoc", sbWLoc.toString());
		request.setAttribute("sbDesigAjax", sbDesigAjax.toString());
		request.setAttribute("sbDepartAjax", sbDepartAjax.toString());
		request.setAttribute("sbWLocAjax", sbWLocAjax.toString());
		
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


	public String viewClientBrand() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			
			List<List<String>> alBrandData = new ArrayList<List<String>>();
			pst = con.prepareStatement("select * from client_brand_details where client_brand_id=?");
			pst.setInt(1, uF.parseToInt(getClientBrandId()));
			rs = pst.executeQuery();
			while(rs.next()) {
				
				setClientBrandName(uF.showData(rs.getString("client_brand_name"), ""));
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("client_brand_id")); //0
				innerList.add(rs.getString("client_brand_name")); //1
				innerList.add(rs.getString("client_brand_logo")); //2
				innerList.add(rs.getString("client_brand_address")); //3
				innerList.add(rs.getString("client_brand_city")); //4
				StringBuilder sbBrandCountry = new StringBuilder();
				for(FillCountry fillCountryList: countryList) {
					if(rs.getString("brand_country") != null && fillCountryList.getCountryId().equals(rs.getString("brand_country"))) {
						sbBrandCountry.append("<option value=\""+fillCountryList.getCountryId()+"\" selected>"+fillCountryList.getCountryName()+"</option>");
					} else {
						sbBrandCountry.append("<option value=\""+fillCountryList.getCountryId()+"\">"+fillCountryList.getCountryName()+"</option>");
					}
				}
				innerList.add(sbBrandCountry.toString()); //5
				
				if(rs.getString("brand_country")!=null && uF.parseToInt(rs.getString("brand_country"))>0) {
					stateList = new FillState(request).fillState(rs.getString("brand_country"));
				}
				StringBuilder sbBrandState = new StringBuilder();
				for(FillState fillStateList: stateList) {
					if(rs.getString("brand_state") != null && fillStateList.getStateId().equals(rs.getString("brand_state"))) {
						sbBrandState.append("<option value=\""+fillStateList.getStateId()+"\" selected>"+fillStateList.getStateName()+"</option>");
					} else {
						sbBrandState.append("<option value=\""+fillStateList.getStateId()+"\">"+fillStateList.getStateName()+"</option>");
					}
				}
				innerList.add(sbBrandState.toString()); //6
				innerList.add(rs.getString("brand_pin_code")); //7

				List<String> alClntBrandInd = new ArrayList<String>();
				if(rs.getString("client_brand_industry")!=null) {
					alClntBrandInd = Arrays.asList(rs.getString("client_brand_industry").split(","));
				}
				StringBuilder sbBrandIndustry = new StringBuilder();
				for(FillClientIndustries fillIndustriesList: clientIndustryList) {
					if(rs.getString("client_brand_industry") != null && alClntBrandInd.contains(fillIndustriesList.getIndustryId()) ) {
						sbBrandIndustry.append("<option value=\""+fillIndustriesList.getIndustryId()+"\" selected>"+fillIndustriesList.getIndustryName()+"</option>");
					} else {
						sbBrandIndustry.append("<option value=\""+fillIndustriesList.getIndustryId()+"\">"+fillIndustriesList.getIndustryName()+"</option>");
					}
				}
				innerList.add(sbBrandIndustry.toString()); //8
				
				innerList.add(rs.getString("brand_website")); //9
				innerList.add(rs.getString("client_brand_description")); //10
				innerList.add(rs.getString("brand_tds_percent")); //11
				innerList.add(rs.getString("brand_registration_no")); //12
				
				alBrandData.add(innerList);
			}
			rs.close();
			pst.close();
			
			
			pst = con.prepareStatement("select * from client_poc where client_brand_id=?");
			pst.setInt(1, uF.parseToInt(getClientBrandId()));
			rs = pst.executeQuery();
//			List<List<String>> alBrandSpocData = new ArrayList<List<String>>();
			Map<String, List<List<String>>> hmBrandSpocData = new HashMap<String, List<List<String>>>();
			while(rs.next()) {
				
				List<List<String>> alBrandSpocData = hmBrandSpocData.get(rs.getString("client_brand_id"));
				if(alBrandSpocData==null) alBrandSpocData = new ArrayList<List<String>>();
				
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("poc_id"));
				innerList.add(uF.showData(rs.getString("contact_fname"), ""));//1
				innerList.add(uF.showData(rs.getString("contact_mname"), ""));//2
				innerList.add(uF.showData(rs.getString("contact_lname"), ""));//3
				innerList.add(uF.showData(rs.getString("contact_number"), ""));//4
				innerList.add(uF.showData(rs.getString("contact_email"), ""));//5
				
				StringBuilder sbDesig = new StringBuilder();
				for(FillDesig fillDesigList: desigList) {
					if(rs.getString("contact_desig_id") != null && fillDesigList.getDesigId().equals(rs.getString("contact_desig_id"))) {
						sbDesig.append("<option value=\""+fillDesigList.getDesigId()+"\" selected>"+fillDesigList.getDesigCodeName()+"</option>");
					} else {
						sbDesig.append("<option value=\""+fillDesigList.getDesigId()+"\">"+fillDesigList.getDesigCodeName()+"</option>");
					}
				}
				innerList.add(sbDesig.toString());//6
				
				StringBuilder sbDepart = new StringBuilder();
				for(FillDepartment fillDepartList: departmentList) {
					if(rs.getString("contact_department_id") != null && fillDepartList.getDeptId().equals(rs.getString("contact_department_id"))) {
						sbDepart.append("<option value=\""+fillDepartList.getDeptId()+"\" selected>"+fillDepartList.getDeptName()+"</option>");
					} else {
						sbDepart.append("<option value=\""+fillDepartList.getDeptId()+"\">"+fillDepartList.getDeptName()+"</option>");
					}
				}
				innerList.add(sbDepart.toString());//7
				
				StringBuilder sbWLoc = new StringBuilder();
				for(FillWLocation fillWLocList: workLocationList) {
					if(rs.getString("contact_location_id") != null && fillWLocList.getwLocationId().equals(rs.getString("contact_location_id"))) {
						sbWLoc.append("<option value=\""+fillWLocList.getwLocationId()+"\" selected>"+fillWLocList.getwLocationName()+"</option>");
					} else {
						sbWLoc.append("<option value=\""+fillWLocList.getwLocationId()+"\">"+fillWLocList.getwLocationName()+"</option>");
					}
				}
				innerList.add(sbWLoc.toString());//8
				
				String contactImage = "";
				if(CF.getStrDocRetriveLocation() == null) {
					contactImage = IConstants.IMAGE_LOCATION + ((rs.getString("contact_photo") !=null && !rs.getString("contact_photo").equals("")) ? rs.getString("contact_photo"):"avatar_photo.png");
				} else { 
				contactImage = IConstants.IMAGE_LOCATION + ((rs.getString("contact_photo")!=null && !rs.getString("contact_photo").equals("")) ? rs.getString("contact_photo"):"avatar_photo.png");
					if(rs.getString("contact_photo")!=null && !rs.getString("contact_photo").equals("")) {
						contactImage = CF.getStrDocRetriveLocation() +IConstants.I_CUSTOMER+"/"+getClientId()+"/"+IConstants.I_IMAGE+"/"+I_CUSTOMER_SPOC+"/"+((rs.getString("contact_photo")!=null && !rs.getString("contact_photo").equals("")) ? rs.getString("contact_photo"):"avatar_photo.png");
					}
				}
				innerList.add(contactImage); //9

				alBrandSpocData.add(innerList);
				
				hmBrandSpocData.put(rs.getString("client_brand_id"), alBrandSpocData);
			}
			rs.close();
			pst.close();
			
			System.out.println("hmBrandSpocData ===>> " + hmBrandSpocData);
			request.setAttribute("hmBrandSpocData", hmBrandSpocData);
			request.setAttribute("alBrandData", alBrandData);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
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

	public String insertClientBrand() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			
			con = db.makeConnection(con);
			
			MultiPartRequestWrapper mpRequest = (MultiPartRequestWrapper)request;
			
			String clientBrandName = null;
			for(int i=0; getStrClientBrandName()!=null && i<getStrClientBrandName().length;i++) {
				
				String clientOrgId = CF.getClientOrgIdById(con, getClientId());
				if(getStrClientBrandName()[i] !=null && !getStrClientBrandName()[i].equals("")) {
					clientBrandName = getStrClientBrandName()[i];
					StringBuilder sbCBIndustry = null;
					String cbIndustryId = null;			
					if(getOtherBrandIndustry()[i] != null && !getOtherBrandIndustry()[i].equals("")) {
						pst = con.prepareStatement("insert into client_industry_details(industry_name) values (?)");
						pst.setString(1, getOtherBrandIndustry()[i]);
						pst.executeUpdate();
						pst.close();
						
						pst = con.prepareStatement("select max(industry_id) as industry_id from client_industry_details");
						rs = pst.executeQuery();
						if(rs.next()) {
							cbIndustryId = rs.getString("industry_id");
						}
						rs.close();
						pst.close();
					}
					
					if(uF.parseToInt(cbIndustryId)>0) {
						if(sbCBIndustry == null) {
							sbCBIndustry = new StringBuilder();
							sbCBIndustry.append(","+cbIndustryId+",");
						}
					}
					
					String[] clientBrandIndustry = request.getParameterValues("clientBrandIndustry"+getClientBrandCntId()[i]);
					
					for(int a=0; clientBrandIndustry!=null && a<clientBrandIndustry.length; a++) {
						if(sbCBIndustry == null) {
							sbCBIndustry = new StringBuilder();
							sbCBIndustry.append(","+clientBrandIndustry[a]+",");
						} else {
							sbCBIndustry.append(clientBrandIndustry[a]+",");
						}
					}
					if(sbCBIndustry == null) {
						sbCBIndustry = new StringBuilder();
					}
					System.out.println("sbCBIndustry ===>> " + sbCBIndustry.toString());
					
					pst = con.prepareStatement("insert into client_brand_details(client_brand_name, client_brand_address, client_brand_industry, brand_tds_percent, " +
							"brand_registration_no, brand_country, brand_state, brand_pin_code, client_brand_description, brand_website, org_id, client_brand_city, " +
							"added_by, client_id, entry_date) values (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?)");
					pst.setString(1, getStrClientBrandName()[i]);
					pst.setString(2, getStrClientBrandAddress()[i]); 
					pst.setString(3, sbCBIndustry.toString());
					pst.setDouble(4, uF.parseToDouble(getClientBrandTds()[i]));
					pst.setString(5, getClientBrandRegistrationNo()[i]);
					pst.setInt(6, uF.parseToInt(getStrClientBrandCountry()[i]));
					pst.setInt(7, uF.parseToInt(getStrClientBrandState()[i]));
					pst.setString(8, getStrBrandPinCode()[i]);
					pst.setString(9, getCompanyBrandDescription()[i]);
					pst.setString(10, getCompanyBrandWebsite()[i]);
					pst.setInt(11, uF.parseToInt(clientOrgId));
					pst.setString(12, getStrClientBrandCity()[i]);
					pst.setInt(13, uF.parseToInt(strSessionEmpId));
					pst.setInt(14, uF.parseToInt(getClientId()));
					pst.setDate(15, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.executeUpdate();
					pst.close();
					
					String strClientBrandId = null;
					pst = con.prepareStatement("select max(client_brand_id) as client_brand_id from client_brand_details");
					rs = pst.executeQuery();
					if(rs.next()) {
						strClientBrandId = rs.getString("client_brand_id");
					}
					rs.close();
					pst.close();
					
					if(getClientBrandLogo() != null && getClientBrandLogo().length > i && getClientBrandLogo()[i] != null) {
						uploadImage2(uF.parseToInt(getClientId()), uF.parseToInt(strClientBrandId), getClientBrandLogo()[i], getClientBrandLogoFileName()[i]);
					}
				
					
					String[] strClientBrandContactFName = request.getParameterValues("strClientBrandContactFName"+getClientBrandCntId()[i]);
					String[] strClientBrandContactMName = request.getParameterValues("strClientBrandContactMName"+getClientBrandCntId()[i]);
					String[] strClientBrandContactLName = request.getParameterValues("strClientBrandContactLName"+getClientBrandCntId()[i]);
					String[] strClientBrandContactNo = request.getParameterValues("strClientBrandContactNo"+getClientBrandCntId()[i]);
					String[] strClientBrandContactEmail = request.getParameterValues("strClientBrandContactEmail"+getClientBrandCntId()[i]);
					String[] strClientBrandContactDesig = request.getParameterValues("strClientBrandContactDesig"+getClientBrandCntId()[i]);
					String[] strClientBrandContactDepartment = request.getParameterValues("strClientBrandContactDepartment"+getClientBrandCntId()[i]);
					String[] strClientBrandContactLocation = request.getParameterValues("strClientBrandContactLocation"+getClientBrandCntId()[i]);
					File[] strClientBrandContactPhoto = mpRequest.getFiles("strClientBrandContactPhoto"+getClientBrandCntId()[i]);
					String[] strClientBrandContactPhotoFileNames = mpRequest.getFileNames("strClientBrandContactPhoto"+getClientBrandCntId()[i]);
					
					String[] otherBrandDesignation = request.getParameterValues("otherBrandDesignation"+getClientBrandCntId()[i]);
					String[] otherBrandDepartment = request.getParameterValues("otherBrandDepartment"+getClientBrandCntId()[i]);
					String[] otherBrandLocation = request.getParameterValues("otherBrandLocation"+getClientBrandCntId()[i]);
					
					
					for(int j=0; strClientBrandContactFName!=null && j<strClientBrandContactFName.length;j++) {
						
						if((strClientBrandContactFName[j] !=null && !strClientBrandContactFName[j].equals("")) || (strClientBrandContactFName[j]!=null && !strClientBrandContactFName[j].equals(""))) {
							
							int otherDesigId = 0;
							int otherDepartId = 0;
							int otherLocId = 0;
							if(otherBrandDesignation != null && !otherBrandDesignation[j].equals("")) {
								pst = con.prepareStatement("insert into client_designations(client_desig_name) values (?)");
								pst.setString(1, otherBrandDesignation[j]);
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
							if(otherBrandDepartment != null && !otherBrandDepartment[j].equals("")) {
								pst = con.prepareStatement("insert into client_departments(client_depart_name) values (?)");
								pst.setString(1, otherBrandDepartment[j]);
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
							if(otherBrandLocation != null && !otherBrandLocation[j].equals("")) {
								pst = con.prepareStatement("insert into client_locations(client_loc_name) values (?)");
								pst.setString(1, otherBrandLocation[j]);
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
							
							pst = con.prepareStatement("insert into client_poc (contact_fname, contact_mname, contact_lname, contact_email, " +
								"contact_number, contact_desig_id, contact_department_id, contact_location_id, client_id, client_brand_id, added_by, " +
								"entry_date) values (?,?,?,?, ?,?,?,?, ?,?,?,?)");
							pst.setString(1, showValue(strClientBrandContactFName[j], "N/A"));
							pst.setString(2, showValue(strClientBrandContactMName[j], "N/A"));
							pst.setString(3, showValue(strClientBrandContactLName[j], "N/A"));
							pst.setString(4, showValue(strClientBrandContactEmail[j], "N/A")); 
							pst.setString(5, showValue(strClientBrandContactNo[j], "N/A")); 
							pst.setInt(6, (uF.parseToInt(strClientBrandContactDesig[j]) > 0) ? uF.parseToInt(strClientBrandContactDesig[j]) : otherDesigId); 
							pst.setInt(7, (uF.parseToInt(strClientBrandContactDepartment[j])>0) ? uF.parseToInt(strClientBrandContactDepartment[j]) : otherDepartId);
							pst.setInt(8, (uF.parseToInt(strClientBrandContactLocation[j])>0) ? uF.parseToInt(strClientBrandContactLocation[j]) : otherLocId);
							pst.setInt(9, uF.parseToInt(getClientId()));
							pst.setInt(10, uF.parseToInt(strClientBrandId));
							pst.setInt(11, uF.parseToInt(strSessionEmpId));
							pst.setDate(12, uF.getCurrentDate(CF.getStrTimeZone()));
							pst.executeUpdate();
							System.out.println("pst CB SPOC pst ===>> " + pst);
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
							aE.request = request;
							aE.session = session; 
							aE.CF = CF;
							aE.setFname(strClientBrandContactFName[j].trim());
							aE.setLname(strClientBrandContactLName[j].trim());
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
//							System.out.println("pst ===>> " + pst);
							int z = pst.executeUpdate();
							pst.close();
							
							
							if(getBtnSaveAndSend() != null) {
								if(strClientBrandContactEmail[j].trim()!=null && strClientBrandContactEmail[j].trim().indexOf("@")>0) {
									String strDomain = request.getServerName().split("\\.")[0];
									Notifications nF = new Notifications(N_NEW_CLIENT_CONTACT, CF);
									nF.setDomain(strDomain);
									
									nF.request = request;
									nF.setStrOrgId(strSessionOrgId);
									nF.setEmailTemplate(true);
									
									nF.setStrCustFName(strClientBrandContactFName[j].trim());
									nF.setStrCustLName(strClientBrandContactLName[j].trim());
									nF.setStrEmpMobileNo(strClientBrandContactNo[j].trim());
									nF.setStrEmpEmail(strClientBrandContactEmail[j].trim());
									nF.setStrEmailTo(strClientBrandContactEmail[j].trim());
									nF.setStrHostAddress(CF.getStrEmailLocalHost());
									nF.setStrHostPort(CF.getStrHostPort());
									nF.setStrContextPath(request.getContextPath());
									nF.setStrCustomerRegisterLink("?custId="+poc_id);
									nF.setStrUserName(username);
									nF.setStrPassword(password);
									nF.sendNotifications();
								}
							}
							
							if(strClientBrandContactPhoto != null && strClientBrandContactPhoto.length > j && strClientBrandContactPhoto[j] != null) {
								uploadImage1(uF.parseToInt(getClientId()), uF.parseToInt(poc_id), strClientBrandContactPhoto[j], strClientBrandContactPhotoFileNames[j]);
							}
						}
					}
					
				}
			
			}
			
			
			List<String> accAndPOIds = new ArrayList<String>();
			pst = con.prepareStatement("select emp_id from user_details where usertype_id in(4,2)");
			rs = pst.executeQuery();
			while (rs.next()) {
				if(uF.parseToInt(rs.getString("emp_id"))> 0 && !accAndPOIds.contains(rs.getString("emp_id"))) {
					accAndPOIds.add(rs.getString("emp_id"));
				}
			}
			rs.close();
			pst.close();
			
			
			for(int i=0; accAndPOIds!=null && !accAndPOIds.isEmpty() && i<accAndPOIds.size(); i++) {
				String strDomain = request.getServerName().split("\\.")[0];
				Notifications nF = new Notifications(N_NEW_CLIENT, CF); 
				nF.setDomain(strDomain);
				
				nF.request = request;
				nF.setStrOrgId(strSessionOrgId);
				nF.setEmailTemplate(true);
				
				nF.setStrEmpId(accAndPOIds.get(i));
				nF.setStrClientName(clientBrandName);
				nF.setStrHostAddress(CF.getStrEmailLocalHost());
				nF.setStrHostPort(CF.getStrHostPort());
				nF.setStrContextPath(request.getContextPath());
				nF.sendNotifications();
			}
			
			session.setAttribute(MESSAGE, SUCCESSM+clientBrandName+" added successfully!"+END);
			
		} catch (Exception e) {
			e.printStackTrace();
			session.setAttribute(MESSAGE, ERRORM+"Error while saving Client Subsidiary/ Brand. Please try again."+END);
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

	private void uploadImage2(int customerId, int clientBrandId, File clientBrandLogo, String clientBrandLogoFileName) {
		try {
			UploadImage uI = new UploadImage();
			uI.setServletRequest(request);
			uI.setImageType("CLIENT_BRAND_PHOTO");
			uI.setEmpImage(clientBrandLogo);
			uI.setEmpImageFileName(clientBrandLogoFileName);
			uI.setContentID(clientBrandId+"");
			uI.setCustomerID(customerId+"");
			uI.setCF(CF);
			uI.upoadImage();
			
		} catch (Exception e) {
			e.printStackTrace();
			
		}
	}
	
	
	public String updateClientBrand() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		try {
			con = db.makeConnection(con);

			MultiPartRequestWrapper mpRequest = (MultiPartRequestWrapper)request;
			
			String clientBrandName = null;
			for(int i=0; getStrClientBrandName()!=null && i<getStrClientBrandName().length;i++) {

				String clientOrgId = CF.getClientOrgIdById(con, getClientId());
				if(getStrClientBrandName()[i] !=null && !getStrClientBrandName()[i].equals("")) {
					clientBrandName = getStrClientBrandName()[i];
					System.out.println("getStrClientBrandState()[i] ===>> " + getStrClientBrandState()[i]);
					
					StringBuilder sbCBIndustry = null;
					String cbIndustryId = null;			
					if(getOtherBrandIndustry()[i] != null && !getOtherBrandIndustry()[i].equals("")) {
						pst = con.prepareStatement("insert into client_industry_details(industry_name) values (?)");
						pst.setString(1, getOtherBrandIndustry()[i]);
						pst.executeUpdate();
						pst.close();
						
						pst = con.prepareStatement("select max(industry_id) as industry_id from client_industry_details");
						rs = pst.executeQuery();
						if(rs.next()) {
							cbIndustryId = rs.getString("industry_id");
						}
						rs.close();
						pst.close();
					}
					
					if(uF.parseToInt(cbIndustryId)>0) {
						if(sbCBIndustry == null) {
							sbCBIndustry = new StringBuilder();
							sbCBIndustry.append(","+cbIndustryId+",");
						}
					}
					
					String[] clientBrandIndustry = request.getParameterValues("clientBrandIndustry"+getClientBrandCntId()[i]);
					
					for(int a=0; clientBrandIndustry!=null && a<clientBrandIndustry.length; a++) {
						if(sbCBIndustry == null) {
							sbCBIndustry = new StringBuilder();
							sbCBIndustry.append(","+clientBrandIndustry[a]+",");
						} else {
							sbCBIndustry.append(clientBrandIndustry[a]+",");
						}
					}
					if(sbCBIndustry == null) {
						sbCBIndustry = new StringBuilder();
					}
					System.out.println("sbCBIndustry ===>> " + sbCBIndustry.toString());
					
					String strClientBrandId = null;
					if(getStrClientBrandId() != null && i<getStrClientBrandId().length && uF.parseToInt(getStrClientBrandId()[i])>0) {
						pst = con.prepareStatement("update client_brand_details set client_brand_name=?, client_brand_address=?, client_brand_industry=?, brand_tds_percent=?, " +
							"brand_registration_no=?, brand_country=?, brand_state=?, brand_pin_code=?, client_brand_description=?, brand_website=?, org_id=?, client_brand_city=?, " +
							"updated_by=?, client_id=?, update_date=? where client_brand_id=?");
						pst.setString(1, getStrClientBrandName()[i]);
						pst.setString(2, getStrClientBrandAddress()[i]); 
						pst.setString(3, sbCBIndustry.toString());
						pst.setDouble(4, uF.parseToDouble(getClientBrandTds()[i]));
						pst.setString(5, getClientBrandRegistrationNo()[i]);
						pst.setInt(6, uF.parseToInt(getStrClientBrandCountry()[i]));
						pst.setInt(7, uF.parseToInt(getStrClientBrandState()[i]));
						pst.setString(8, getStrBrandPinCode()[i]);
						pst.setString(9, getCompanyBrandDescription()[i]);
						pst.setString(10, getCompanyBrandWebsite()[i]);
						pst.setInt(11, uF.parseToInt(clientOrgId));
						pst.setString(12, getStrClientBrandCity()[i]);
						pst.setInt(13, uF.parseToInt(strSessionEmpId));
						pst.setInt(14, uF.parseToInt(getClientId()));
						pst.setDate(15, uF.getCurrentDate(CF.getStrTimeZone()));
						pst.setInt(16, uF.parseToInt(getStrClientBrandId()[i]));
						pst.executeUpdate();
						System.out.println("pst =====>> " + pst);
						pst.close();
						
						strClientBrandId = getStrClientBrandId()[i];
						
					} else {
						pst = con.prepareStatement("insert into client_brand_details(client_brand_name, client_brand_address, client_brand_industry, brand_tds_percent, " +
							"brand_registration_no, brand_country, brand_state, brand_pin_code, client_brand_description, brand_website, org_id, client_brand_city, " +
							"added_by, client_id, entry_date) values (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?)");
						pst.setString(1, getStrClientBrandName()[i]);
						pst.setString(2, getStrClientBrandAddress()[i]); 
						pst.setString(3, sbCBIndustry.toString());
						pst.setDouble(4, uF.parseToDouble(getClientBrandTds()[i]));
						pst.setString(5, getClientBrandRegistrationNo()[i]);
						pst.setInt(6, uF.parseToInt(getStrClientBrandCountry()[i]));
						pst.setInt(7, uF.parseToInt(getStrClientBrandState()[i]));
						pst.setString(8, getStrBrandPinCode()[i]);
						pst.setString(9, getCompanyBrandDescription()[i]);
						pst.setString(10, getCompanyBrandWebsite()[i]);
						pst.setInt(11, uF.parseToInt(clientOrgId));
						pst.setString(12, getStrClientBrandCity()[i]);
						pst.setInt(13, uF.parseToInt(strSessionEmpId));
						pst.setInt(14, uF.parseToInt(getClientId()));
						pst.setDate(15, uF.getCurrentDate(CF.getStrTimeZone()));
						pst.executeUpdate();
						pst.close();
						
						pst = con.prepareStatement("select max(client_brand_id) as client_brand_id from client_brand_details");
						rs = pst.executeQuery();
						if(rs.next()) {
							strClientBrandId = rs.getString("client_brand_id");
						}
						rs.close();
						pst.close();
					}
					
					if(getClientBrandLogo() != null && getClientBrandLogo().length > i && getClientBrandLogo()[i] != null) {
						uploadImage2(uF.parseToInt(getClientId()), uF.parseToInt(strClientBrandId), getClientBrandLogo()[i], getClientBrandLogoFileName()[i]);
					}
				
					
					String[] strClientBrandContactId = request.getParameterValues("strClientBrandContactId"+getClientBrandCntId()[i]);
					String[] strClientBrandContactFName = request.getParameterValues("strClientBrandContactFName"+getClientBrandCntId()[i]);
					String[] strClientBrandContactMName = request.getParameterValues("strClientBrandContactMName"+getClientBrandCntId()[i]);
					String[] strClientBrandContactLName = request.getParameterValues("strClientBrandContactLName"+getClientBrandCntId()[i]);
					String[] strClientBrandContactNo = request.getParameterValues("strClientBrandContactNo"+getClientBrandCntId()[i]);
					String[] strClientBrandContactEmail = request.getParameterValues("strClientBrandContactEmail"+getClientBrandCntId()[i]);
					String[] strClientBrandContactDesig = request.getParameterValues("strClientBrandContactDesig"+getClientBrandCntId()[i]);
					String[] strClientBrandContactDepartment = request.getParameterValues("strClientBrandContactDepartment"+getClientBrandCntId()[i]);
					String[] strClientBrandContactLocation = request.getParameterValues("strClientBrandContactLocation"+getClientBrandCntId()[i]);
					File[] strClientBrandContactPhoto = mpRequest.getFiles("strClientBrandContactPhoto"+getClientBrandCntId()[i]);
					String[] strClientBrandContactPhotoFileNames = mpRequest.getFileNames("strClientBrandContactPhoto"+getClientBrandCntId()[i]);
					
					String[] otherBrandDesignation = request.getParameterValues("otherBrandDesignation"+getClientBrandCntId()[i]);
					String[] otherBrandDepartment = request.getParameterValues("otherBrandDepartment"+getClientBrandCntId()[i]);
					String[] otherBrandLocation = request.getParameterValues("otherBrandLocation"+getClientBrandCntId()[i]);
					
					for(int j=0; strClientBrandContactFName!=null && j<strClientBrandContactFName.length;j++) {
						
						if((strClientBrandContactFName[j] !=null && !strClientBrandContactFName[j].equals("")) || (strClientBrandContactFName[j]!=null && !strClientBrandContactFName[j].equals(""))) {
							
							int otherDesigId = 0;
							int otherDepartId = 0;
							int otherLocId = 0;
							if(otherBrandDesignation != null && !otherBrandDesignation[j].equals("")) {
								pst = con.prepareStatement("insert into client_designations(client_desig_name) values (?)");
								pst.setString(1, otherBrandDesignation[j]);
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
							if(otherBrandDepartment != null && !otherBrandDepartment[j].equals("")) {
								pst = con.prepareStatement("insert into client_departments(client_depart_name) values (?)");
								pst.setString(1, otherBrandDepartment[j]);
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
							if(otherBrandLocation != null && !otherBrandLocation[j].equals("")) {
								pst = con.prepareStatement("insert into client_locations(client_loc_name) values (?)");
								pst.setString(1, otherBrandLocation[j]);
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
							
							String poc_id = null;
							if(strClientBrandContactId != null && j<strClientBrandContactId.length && uF.parseToInt(strClientBrandContactId[j])>0) {
								
								pst = con.prepareStatement("update client_poc set contact_fname=?, contact_mname=?, contact_lname=?, contact_email=?, contact_number=?, " +
									"contact_desig_id=?, contact_department_id=?, contact_location_id=?, updated_by=?, update_date=? where poc_id=?");
								pst.setString(1, showValue(strClientBrandContactFName[j], "N/A"));
								pst.setString(2, showValue(strClientBrandContactMName[j], "N/A"));
								pst.setString(3, showValue(strClientBrandContactLName[j], "N/A"));
								
								pst.setString(4, showValue(strClientBrandContactEmail[j], "N/A")); 
								pst.setString(5, showValue(strClientBrandContactNo[j], "N/A")); 
								pst.setInt(6, (uF.parseToInt(strClientBrandContactDesig[j]) > 0) ? uF.parseToInt(strClientBrandContactDesig[j]) : otherDesigId); 
								pst.setInt(7, (uF.parseToInt(strClientBrandContactDepartment[j])>0) ? uF.parseToInt(strClientBrandContactDepartment[j]) : otherDepartId);
								pst.setInt(8, (uF.parseToInt(strClientBrandContactLocation[j])>0) ? uF.parseToInt(strClientBrandContactLocation[j]) : otherLocId);
								pst.setInt(9, uF.parseToInt(strSessionEmpId));
								pst.setDate(10, uF.getCurrentDate(CF.getStrTimeZone()));
								pst.setInt(11, uF.parseToInt(strClientBrandContactId[j]));
								pst.executeUpdate();
								pst.close();
								
								boolean userFlag = false;
								pst = con.prepareStatement("select user_id from user_details_customer where emp_id = ?");
								pst.setInt(1, uF.parseToInt(strClientBrandContactId[j]));
								rs = pst.executeQuery();
								while(rs.next()) {
									userFlag = true;
								}
								rs.close();
								pst.close();
								
								if(!userFlag) {
									Map<String,String> userPresent = CF.getCustomerUsersMap(con);
									SecureRandom random = new SecureRandom();
									String password = new BigInteger(130, random).toString(32).substring(5, 13);
									
									AddEmployeeMode aE = new AddEmployeeMode();
									aE.request = request;
									aE.session = session;
									aE.CF = CF;
									aE.setFname(strClientBrandContactFName[j].trim());
									aE.setLname(strClientBrandContactLName[j].trim());
									String username = aE.getUserName(userPresent);
									
									// insert into employ User detail
									pst = con.prepareStatement("insert into user_details_customer(username, password, usertype_id, emp_id, status, " +
											"is_termscondition, added_timestamp) values (?,?,?,?,?,?,?) ");
									pst.setString(1, username);
									pst.setString(2, password);
									pst.setInt(3, 12);
									pst.setInt(4, uF.parseToInt(strClientBrandContactId[j]));
									pst.setString(5, "ACTIVE");
									pst.setBoolean(6, true);
									pst.setTimestamp(7, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone())+"", DBDATE+DBTIME));
	//								System.out.println("pst ===>> " + pst);
									pst.executeUpdate();
									pst.close();
									
									if(getBtnSaveAndSend() != null) {
										if(strClientBrandContactEmail[j].trim()!=null && strClientBrandContactEmail[j].trim().indexOf("@")>0) {
											String strDomain = request.getServerName().split("\\.")[0];
											Notifications nF = new Notifications(N_NEW_CLIENT_CONTACT, CF);
											nF.setDomain(strDomain);
											
											nF.request = request;
											nF.setStrOrgId(strSessionOrgId);
											nF.setEmailTemplate(true);
											
											nF.setStrCustFName(strClientBrandContactFName[j].trim());
											nF.setStrCustLName(strClientBrandContactLName[j].trim());
											nF.setStrEmpMobileNo(strClientBrandContactNo[j].trim());
											nF.setStrEmpEmail(strClientBrandContactEmail[j].trim());
											nF.setStrEmailTo(strClientBrandContactEmail[j].trim());
											nF.setStrHostAddress(CF.getStrEmailLocalHost());
											nF.setStrHostPort(CF.getStrHostPort());
											nF.setStrContextPath(request.getContextPath());
											nF.setStrCustomerRegisterLink("?custId="+poc_id);
											nF.setStrUserName(username);
											nF.setStrPassword(password);
											nF.sendNotifications();
										}
									}
								}
							
							} else {
								pst = con.prepareStatement("insert into client_poc (contact_fname, contact_mname, contact_lname, contact_email, " +
									"contact_number, contact_desig_id, contact_department_id, contact_location_id, client_id, client_brand_id, added_by, " +
									"entry_date) values (?,?,?,?, ?,?,?,?, ?,?,?,?)");
								pst.setString(1, showValue(strClientBrandContactFName[j], "N/A"));
								pst.setString(2, showValue(strClientBrandContactMName[j], "N/A"));
								pst.setString(3, showValue(strClientBrandContactLName[j], "N/A"));
								
								pst.setString(4, showValue(strClientBrandContactEmail[j], "N/A")); 
								pst.setString(5, showValue(strClientBrandContactNo[j], "N/A")); 
								pst.setInt(6, (uF.parseToInt(strClientBrandContactDesig[j]) > 0) ? uF.parseToInt(strClientBrandContactDesig[j]) : otherDesigId); 
								pst.setInt(7, (uF.parseToInt(strClientBrandContactDepartment[j])>0) ? uF.parseToInt(strClientBrandContactDepartment[j]) : otherDepartId);
								pst.setInt(8, (uF.parseToInt(strClientBrandContactLocation[j])>0) ? uF.parseToInt(strClientBrandContactLocation[j]) : otherLocId);
								pst.setInt(9, uF.parseToInt(getClientId()));
								pst.setInt(10, uF.parseToInt(strClientBrandId));
								pst.setInt(11, uF.parseToInt(strSessionEmpId));
								pst.setDate(12, uF.getCurrentDate(CF.getStrTimeZone()));
								pst.executeUpdate();
								System.out.println("pst CB SPOC pst ===>> " + pst);
								pst.close();
							
								
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
								aE.request = request;
								aE.session = session; 
								aE.CF = CF;
								aE.setFname(strClientBrandContactFName[j].trim());
								aE.setLname(strClientBrandContactLName[j].trim());
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
	//							System.out.println("pst ===>> " + pst);
								int z = pst.executeUpdate();
								pst.close();
								
								
								if(getBtnSaveAndSend() != null) {
									if(strClientBrandContactEmail[j].trim()!=null && strClientBrandContactEmail[j].trim().indexOf("@")>0) {
										String strDomain = request.getServerName().split("\\.")[0];
										Notifications nF = new Notifications(N_NEW_CLIENT_CONTACT, CF);
										nF.setDomain(strDomain);
										
										nF.request = request;
										nF.setStrOrgId(strSessionOrgId);
										nF.setEmailTemplate(true);
										
										nF.setStrCustFName(strClientBrandContactFName[j].trim());
										nF.setStrCustLName(strClientBrandContactLName[j].trim());
										nF.setStrEmpMobileNo(strClientBrandContactNo[j].trim());
										nF.setStrEmpEmail(strClientBrandContactEmail[j].trim());
										nF.setStrEmailTo(strClientBrandContactEmail[j].trim());
										nF.setStrHostAddress(CF.getStrEmailLocalHost());
										nF.setStrHostPort(CF.getStrHostPort());
										nF.setStrContextPath(request.getContextPath());
										nF.setStrCustomerRegisterLink("?custId="+poc_id);
										nF.setStrUserName(username);
										nF.setStrPassword(password);
										nF.sendNotifications();
									}
								}
							}
							
							if(strClientBrandContactPhoto != null && strClientBrandContactPhoto.length > j && strClientBrandContactPhoto[j] != null) {
								uploadImage1(uF.parseToInt(getClientId()), uF.parseToInt(poc_id), strClientBrandContactPhoto[j], strClientBrandContactPhotoFileNames[j]);
							}
						}
					}
					
				}
			
			}
			
			session.setAttribute(MESSAGE, SUCCESSM+clientBrandName+" updated successfully!"+END);
		} catch (Exception e) {
			e.printStackTrace();
			session.setAttribute(MESSAGE, ERRORM+"Error while updating Subsidiary/ Brand. Please try again."+END);
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
	
	
	public String deleteCustomer() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			con = db.makeConnection(con);
			String clientBrandName="";
			pst = con.prepareStatement("select client_brand_name from client_brand_details where client_brand_id=?");
			pst.setInt(1, uF.parseToInt(getClientBrandId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				clientBrandName = rs.getString("client_brand_name");
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("delete from user_details_customer where emp_id in ( select poc_id from client_poc where client_brand_id=?)");
			pst.setInt(1, uF.parseToInt(getClientBrandId()));
			pst.execute();
			pst.close();
			
			pst = con.prepareStatement("delete from client_poc where client_brand_id=?");
			pst.setInt(1, uF.parseToInt(getClientBrandId()));
			pst.execute();
			pst.close();
			
			pst = con.prepareStatement("delete from client_brand_details where client_brand_id=?");
			pst.setInt(1, uF.parseToInt(getClientBrandId()));
			pst.execute();
			pst.close();
			
			session.setAttribute(MESSAGE, SUCCESSM+clientBrandName+" deleted successfully!"+END);
		} catch (Exception e) {
			e.printStackTrace();
			session.setAttribute(MESSAGE, "Error in deletion");
		} finally {
			db.closeResultSet(rs);
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

	public List<FillClientBrand> getClientBrandList() {
		return clientBrandList;
	}

	public void setClientBrandList(List<FillClientBrand> clientBrandList) {
		this.clientBrandList = clientBrandList;
	}

	public String getClientBrandId() {
		return clientBrandId;
	}

	public void setClientBrandId(String clientBrandId) {
		this.clientBrandId = clientBrandId;
	}

	public List<FillClientIndustries> getClientIndustryList() {
		return clientIndustryList;
	}

	public void setClientIndustryList(List<FillClientIndustries> clientIndustryList) {
		this.clientIndustryList = clientIndustryList;
	}

	public List<FillCountry> getCountryList() {
		return countryList;
	}

	public void setCountryList(List<FillCountry> countryList) {
		this.countryList = countryList;
	}

	public List<FillState> getStateList() {
		return stateList;
	}

	public void setStateList(List<FillState> stateList) {
		this.stateList = stateList;
	}

	public String[] getClientBrandCntId() {
		return clientBrandCntId;
	}

	public void setClientBrandCntId(String[] clientBrandCntId) {
		this.clientBrandCntId = clientBrandCntId;
	}

	public String[] getStrClientBrandId() {
		return strClientBrandId;
	}

	public void setStrClientBrandId(String[] strClientBrandId) {
		this.strClientBrandId = strClientBrandId;
	}

	public String[] getStrClientBrandName() {
		return strClientBrandName;
	}

	public void setStrClientBrandName(String[] strClientBrandName) {
		this.strClientBrandName = strClientBrandName;
	}

	public File[] getClientBrandLogo() {
		return clientBrandLogo;
	}

	public void setClientBrandLogo(File[] clientBrandLogo) {
		this.clientBrandLogo = clientBrandLogo;
	}

	public String[] getStrClientBrandAddress() {
		return strClientBrandAddress;
	}

	public void setStrClientBrandAddress(String[] strClientBrandAddress) {
		this.strClientBrandAddress = strClientBrandAddress;
	}

	public String[] getStrClientBrandCity() {
		return strClientBrandCity;
	}

	public void setStrClientBrandCity(String[] strClientBrandCity) {
		this.strClientBrandCity = strClientBrandCity;
	}

	public String[] getStrClientBrandCountry() {
		return strClientBrandCountry;
	}

	public void setStrClientBrandCountry(String[] strClientBrandCountry) {
		this.strClientBrandCountry = strClientBrandCountry;
	}

	public String[] getStrClientBrandState() {
		return strClientBrandState;
	}

	public void setStrClientBrandState(String[] strClientBrandState) {
		this.strClientBrandState = strClientBrandState;
	}

	public String[] getStrBrandPinCode() {
		return strBrandPinCode;
	}

	public void setStrBrandPinCode(String[] strBrandPinCode) {
		this.strBrandPinCode = strBrandPinCode;
	}

	public String[] getOtherBrandIndustry() {
		return otherBrandIndustry;
	}

	public void setOtherBrandIndustry(String[] otherBrandIndustry) {
		this.otherBrandIndustry = otherBrandIndustry;
	}

	public String[] getCompanyBrandDescription() {
		return companyBrandDescription;
	}

	public void setCompanyBrandDescription(String[] companyBrandDescription) {
		this.companyBrandDescription = companyBrandDescription;
	}

	public String[] getCompanyBrandWebsite() {
		return companyBrandWebsite;
	}

	public void setCompanyBrandWebsite(String[] companyBrandWebsite) {
		this.companyBrandWebsite = companyBrandWebsite;
	}

	public String[] getClientBrandTds() {
		return clientBrandTds;
	}

	public void setClientBrandTds(String[] clientBrandTds) {
		this.clientBrandTds = clientBrandTds;
	}

	public String[] getClientBrandRegistrationNo() {
		return clientBrandRegistrationNo;
	}

	public void setClientBrandRegistrationNo(String[] clientBrandRegistrationNo) {
		this.clientBrandRegistrationNo = clientBrandRegistrationNo;
	}

	public String[] getClientBrandLogoFileName() {
		return clientBrandLogoFileName;
	}

	public void setClientBrandLogoFileName(String[] clientBrandLogoFileName) {
		this.clientBrandLogoFileName = clientBrandLogoFileName;
	}

	public String getBtnDelete() {
		return btnDelete;
	}

	public void setBtnDelete(String btnDelete) {
		this.btnDelete = btnDelete;
	}

	public String getClientBrandName() {
		return clientBrandName;
	}

	public void setClientBrandName(String clientBrandName) {
		this.clientBrandName = clientBrandName;
	}

}