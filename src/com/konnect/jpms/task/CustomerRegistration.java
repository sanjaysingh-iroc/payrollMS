package com.konnect.jpms.task;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillDesig;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IConstants;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UploadImage;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class CustomerRegistration extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	
	HttpSession session;
	CommonFunctions CF = null;
	String strSessionEmpId;
	String strSessionOrgId;
	
	String fromPage;
	String proId;
	String clientId; 
	String custId;
	String operation;

	String strClientContactId;
	String strClientContactFName;
	String strClientContactMName;
	String strClientContactLName;
	String strClientContactNo;
	String strClientContactEmail;
	String strClientContactDesig;
	String strClientContactDepartment;
	String strClientContactLocation;
	File strClientContactPhoto;
	String strClientContactPhotoFileName;
	String strClientContactPhotoFile;
	 
	String strUsername;
	String strPassword;
	
	List<FillDesig> desigList;
	List<FillWLocation> workLocationList;
	List<FillDepartment> departmentList;
	
	public String execute() throws Exception {
		
		session = request.getSession(true);
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
//		if (CF == null) {
//			CF = new CommonFunctions();
//		}
		if(CF==null)return LOGIN;
		
		strSessionEmpId = (String)session.getAttribute(EMPID);
		strSessionOrgId = (String)session.getAttribute(ORGID);
		
		request.setAttribute(PAGE, "/jsp/task/CustomerRegistration.jsp");
		request.setAttribute(TITLE, "Customer Registration Form");
		
		request.setAttribute("DOC_RETRIVE_LOCATION", CF.getStrDocRetriveLocation());
		
		CF.getFormValidationFields(request, ADD_UPDATE_COMPANY);
		
		desigList = new FillDesig(request).fillDesig();
		workLocationList = new FillWLocation(request).fillWLocation();
		departmentList = new FillDepartment(request).fillDepartment();
		
//		System.out.println("operation ===>> " + operation);
		
		if (operation==null || operation.equals("")) {
			return viewCustomer(getCustId());
		}
		
		if (operation != null && getStrClientContactId() !=null && getStrClientContactId().length()>0) {
				return updateCustomer();
		}
		
		return LOAD;
	}


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
			pst = con.prepareStatement("select cp.*, udc.username from client_poc cp, user_details_customer udc where cp.poc_id = udc.emp_id and cp.poc_id=? ");
			pst.setInt(1, uF.parseToInt(strId));
			rs = pst.executeQuery();
			
			int i=0;
			while(rs.next()) {	
				if(i==0){
					setStrClientContactId(rs.getString("poc_id"));
					setStrClientContactFName(uF.showData(rs.getString("contact_fname"), ""));
					setStrClientContactMName(uF.showData(rs.getString("contact_mname"), ""));
					setStrClientContactLName(uF.showData(rs.getString("contact_lname"), ""));
					setStrClientContactNo(uF.showData(rs.getString("contact_number"), ""));
					setStrClientContactEmail(uF.showData(rs.getString("contact_email"), ""));
					setStrClientContactDesig(uF.showData(rs.getString("contact_desig_id"), ""));
					setStrClientContactDepartment(uF.showData(rs.getString("contact_department_id"), ""));
					setStrClientContactLocation(uF.showData(rs.getString("contact_location_id"), ""));
					setStrUsername(uF.showData(rs.getString("username"), ""));
					
//					sb444.append(uF.showData(rs.getString("contact_photo"), ""));
					
//					CF.getStrDocSaveLocation()+"/"+I_CUSTOMER+"/"+getCustomerID()+"/"+I_IMAGE+"/"+I_CUSTOMER_SPOC+"/"
					String contactImage = "";
					if(CF.getStrDocRetriveLocation() == null) {
						contactImage = IConstants.IMAGE_LOCATION + ((rs.getString("contact_photo") !=null && !rs.getString("contact_photo").equals("")) ? rs.getString("contact_photo"):"avatar_photo.png");
					} else { 
					contactImage = IConstants.IMAGE_LOCATION + ((rs.getString("contact_photo")!=null && !rs.getString("contact_photo").equals("")) ? rs.getString("contact_photo"):"avatar_photo.png");
						if(rs.getString("contact_photo")!=null && !rs.getString("contact_photo").equals("")) {
							contactImage = CF.getStrDocRetriveLocation() +IConstants.I_CUSTOMER+"/"+strId+"/"+IConstants.I_IMAGE+"/"+I_CUSTOMER_SPOC+"/"+((rs.getString("contact_photo")!=null && !rs.getString("contact_photo").equals("")) ? rs.getString("contact_photo"):"avatar_photo.png");
						}
					}
					setStrClientContactPhotoFile(uF.showData(contactImage, ""));
				}
				i++;
			}
			rs.close();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return LOAD;
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

				pst = con.prepareStatement("update client_poc set contact_fname=?, contact_mname=?, contact_lname=?, contact_email=?, " +
					"contact_number=?, contact_desig_id=?, contact_department_id=?, contact_location_id=? where poc_id=?");
				pst.setString(1, showValue(getStrClientContactFName(),"N/A"));
				pst.setString(2, showValue(getStrClientContactMName(),"N/A"));
				pst.setString(3, showValue(getStrClientContactLName(),"N/A"));
				
				pst.setString(4, showValue(getStrClientContactEmail(),"N/A")); 
				pst.setString(5, showValue(getStrClientContactNo(),"N/A")); 
				pst.setInt(6, uF.parseToInt(getStrClientContactDesig())); 
				pst.setInt(7, uF.parseToInt(getStrClientContactDepartment()));
				pst.setInt(8, uF.parseToInt(getStrClientContactLocation()));
				pst.setInt(9, uF.parseToInt(getStrClientContactId()));
				pst.executeUpdate();
				pst.close();
				
				pst = con.prepareStatement("update user_details_customer set username=?, password=? where emp_id=?");
				pst.setString(1, getStrUsername());
				pst.setString(2, getStrPassword());
				pst.setInt(3, uF.parseToInt(getStrClientContactId()));
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
		return SUCCESS;
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

	public String getCustId() {
		return custId;
	}

	public void setCustId(String custId) {
		this.custId = custId;
	}

	public String getStrUsername() {
		return strUsername;
	}

	public void setStrUsername(String strUsername) {
		this.strUsername = strUsername;
	}

	public String getStrPassword() {
		return strPassword;
	}

	public void setStrPassword(String strPassword) {
		this.strPassword = strPassword;
	}

}