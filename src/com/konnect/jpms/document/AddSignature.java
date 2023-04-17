package com.konnect.jpms.document;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.itextpdf.text.pdf.PdfStructTreeController.returnType;
import com.konnect.jpms.select.FillEmployee;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UploadImage;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class AddSignature extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;

	HttpSession session;
	CommonFunctions CF;
	
	String strSessionEmpId = null;
	
	private String orgId;
	private String strSignatureType;
	 
	private File strSignatureImg;
	private String strSignatureImgFileName;
	private List<FillEmployee> empList;
	
	private String employeeId;
	private String signatureID;
	private String operation;
	
	private String userscreen;
	private String navigationId;
	private String toPage;
	
	public String execute() { 

//		String operation = request.getParameter("operation");
		String strId = request.getParameter("strId");
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;

		strSessionEmpId = (String)session.getAttribute(EMPID);
		
		if (operation != null && operation.equals("D")) {
			return deleteSignature(getSignatureID());
		}

		if (operation != null && operation.equals("A")) {
			return insertSignature();
		}
		
		empList = new FillEmployee(request).fillRecruiterName(getOrgId());
		getSignature();
		loadSignature(strId);
		return "load";
	}

	
	private String deleteSignature(String signatureID) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("delete from document_signature where signature_id=?");
			pst.setInt(1, uF.parseToInt(signatureID));
//			System.out.println("pst delete ==>"+pst);
			pst.executeUpdate();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;
	}

	
	private void getSignature() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs=null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			Map<String, String> hmSignType = getSignatureType();
			
			List<Map<String,String>> signatureList = new ArrayList<Map<String,String>>(); 
			pst = con.prepareStatement("select * from document_signature where org_id=? order by signature_id desc");
			pst.setInt(1, uF.parseToInt(getOrgId()));
			rs=pst.executeQuery();
			while(rs.next()) {
				Map<String, String> hmInner = new HashMap<String, String>();
				hmInner.put("SIGNATURE_ID", rs.getString("signature_id"));
				hmInner.put("SIGNATURE_NAME", hmSignType.get(rs.getString("signature_type")));
				String signature_image= "";
				if(rs.getString("signature_image") != null && !rs.getString("signature_image").equals("")) {
//					signature_image="<img  class=\"lazy\" src=\"userImages/avatar_photo.png\" data-original=\""+CF.getStrDocRetriveLocation()+"/"+uF.showData(rs.getString("signature_image"), "avatar_photo.png")+"\" border=\"0\"  height=\"100\" />";
					if(CF.getStrDocRetriveLocation()==null) {  
						signature_image = "<img class=\"lazy\" src=\"userImages/avatar_photo.png\" data-original=\""+ DOCUMENT_LOCATION +rs.getString("signature_image")+"\" width=\"50%\"/> ";
					} else { 
						StringBuilder sbPath = new StringBuilder(CF.getStrDocRetriveLocation()+I_ORGANISATION+"/"+rs.getString("org_id")+"/"+I_DOC_SIGN+"/");
						if(uF.parseToInt(rs.getString("user_id")) > 0) {
							sbPath.append(rs.getString("user_id")+"/");
						}
						signature_image = "<img class=\"lazy\" style=\"border-radius: 0 !important;\" src=\"userImages/avatar_photo.png\" data-original=\""+ sbPath.toString()+rs.getString("signature_image")+"\" width=\"50%\"/>";
					}
				}
				hmInner.put("SIGNATURE_IMAGE", signature_image);
//				hmInner.put("SIGNATURE_IMAGE", rs.getString("signature_image"));
				if(rs.getInt("user_id") > 0) {
					hmInner.put("USER_NAME", hmEmpName.get(rs.getString("user_id")));
				} else {
					hmInner.put("USER_NAME", "");
				}
				signatureList.add(hmInner);
			}
			rs.close();
			pst.close();
			request.setAttribute("signatureList", signatureList);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	
	private String insertSignature() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		ResultSet rs = null;
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			int newSignatureId = 0;
			pst = con.prepareStatement("select * from document_signature where signature_type =? and user_id=? and org_id =?");
			pst.setInt(1, uF.parseToInt(getStrSignatureType()));
			pst.setInt(2, uF.parseToInt(getEmployeeId()));
			pst.setInt(3, uF.parseToInt(getOrgId()));
//			System.out.println("pst select ==>"+pst);
			rs = pst.executeQuery();
			while(rs.next()) {
				newSignatureId = rs.getInt("signature_id");
			}
			rs.close();
			pst.close();
			
			if(newSignatureId == 0) {
				String filename=null;
				pst = con.prepareStatement("insert into document_signature (signature_type,user_id,org_id,added_by,added_date) values(?,?,?,?, ?)");
				pst.setInt(1, uF.parseToInt(getStrSignatureType()));
				pst.setInt(2, uF.parseToInt(getEmployeeId()));
				pst.setInt(3, uF.parseToInt(getOrgId()));
				pst.setInt(4, uF.parseToInt(strSessionEmpId));
				pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
//				System.out.println("pst insert ==>"+pst);
				pst.executeUpdate();
				pst.close();
				
				pst = con.prepareStatement("select max(signature_id) as signature_id from document_signature");
				rs = pst.executeQuery();
				if(rs.next()) {
					newSignatureId = rs.getInt("signature_id");
				}
				rs.close();
				pst.close();
			} else {
				pst = con.prepareStatement("update document_signature set user_id=?, updated_by=?, updated_date=? where signature_id=?");
				pst.setInt(1, uF.parseToInt(getEmployeeId()));
				pst.setInt(2, uF.parseToInt(strSessionEmpId));
				pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setInt(4, newSignatureId);
//				System.out.println("pst insert ==>"+pst);
				pst.executeUpdate();
				pst.close();
			}
			
			if(getStrSignatureImgFileName()!=null) {
				uploadImage(newSignatureId);
			}
				session.setAttribute(MESSAGE, SUCCESSM+"Signature saved successfully."+END);
			/*} else {
				session.setAttribute(MESSAGE, ERRORM+"Collateral could not be saved.<br/>Please try again."+END);
			} */ 

		} catch (Exception e) {
			e.printStackTrace();
			session.setAttribute(MESSAGE, ERRORM+"Signature could not be saved.<br/>Please try again."+END);
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;
	}

	private Map<String, String> getSignatureType() {
		
		Map<String, String> hmSignType = new HashMap<String, String>();
		hmSignType.put("1", "Authority Signature");
		hmSignType.put("2", "HR Signature");
		hmSignType.put("3", "Recruiter Signature");
		
		return hmSignType;
	}
	
	
	private void uploadImage(int contentID) {
		try {
			UploadImage uI = new UploadImage();
			uI.setServletRequest(request);
			uI.setImageType("SIGNATURE_IMAGE");
			uI.setEmpId(getEmployeeId());
			uI.setOrgId(getOrgId());
			uI.setEmpImage(getStrSignatureImg());
			uI.setEmpImageFileName(getStrSignatureImgFileName());
			uI.setContentID(contentID+"");
			uI.setCF(CF);
			uI.upoadImage();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	private void loadSignature(String strId) {
//		setStrCollateralId(strId);
	}

	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getUserscreen() {
		return userscreen;
	}

	public void setUserscreen(String userscreen) {
		this.userscreen = userscreen;
	}

	public String getNavigationId() {
		return navigationId;
	}

	public void setNavigationId(String navigationId) {
		this.navigationId = navigationId;
	}

	public String getToPage() {
		return toPage;
	}

	public void setToPage(String toPage) {
		this.toPage = toPage;
	}

	public String getStrSignatureType() {
		return strSignatureType;
	}

	public void setStrSignatureType(String strSignatureType) {
		this.strSignatureType = strSignatureType;
	}

	public File getStrSignatureImg() {
		return strSignatureImg;
	}

	public void setStrSignatureImg(File strSignatureImg) {
		this.strSignatureImg = strSignatureImg;
	}

	public String getStrSignatureImgFileName() {
		return strSignatureImgFileName;
	}

	public void setStrSignatureImgFileName(String strSignatureImgFileName) {
		this.strSignatureImgFileName = strSignatureImgFileName;
	}

	public String getSignatureID() {
		return signatureID;
	}

	public void setSignatureID(String signatureID) {
		this.signatureID = signatureID;
	}

	public List<FillEmployee> getEmpList() {
		return empList;
	}

	public void setEmpList(List<FillEmployee> empList) {
		this.empList = empList;
	}

	public String getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}


	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	
}
