package com.konnect.jpms.util;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.opensymphony.xwork2.ActionSupport;

public class UploadImage extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;

	public CommonFunctions CF;
	HttpSession session;
	
	private File empImage;
	private String empImageFileName;
	private String imageType;
	private String empId;
	private String contentID;
	private String performanceID;
	private String customerID;
	private String orgId;
	private String eventId;
	private String bookId;
	private String dishId;
	private String manualId;
	
	String userscreen;
	String navigationId;
	String toPage;
	
	public String execute() throws Exception {
		request.setAttribute(PAGE, PMyProfile);
		 
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		String strReturn = upoadImage();
 
		return strReturn; 
	}
	
	public String upoadImage() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		System.out.println("image type==>"+getImageType() +" -- getEmpImage() ===>> " + getEmpImage());

		try {
			// File Uploading code goes here....

			int random = new Random().nextInt();
		//	String filePath = request.getRealPath("/userImages/");
			String fileName = "";

			con = db.makeConnection(con);
			
			if(getImageType()!=null && getImageType().equalsIgnoreCase("COMPANY_LOGO")){
				 
//				if(CF.getStrDocSaveLocation()==null) {
//					fileName = uF.uploadImageDocuments(request, DOCUMENT_LOCATION, getEmpImage(), getEmpImageFileName(), getEmpImageFileName(), CF);
//				} else {
//					fileName = uF.uploadImageDocuments(request, CF.getStrDocSaveLocation()+I_COMPANY+"/"+I_IMAGE+"/", getEmpImage(), getEmpImageFileName(), getEmpImageFileName(), CF);
//				}
				if( getEmpImage()!=null) {
					if(CF.getStrDocSaveLocation()==null) { 
						fileName = uF.uploadImage(request, DOCUMENT_LOCATION, getEmpImage(), getEmpImageFileName(), getEmpImageFileName(), CF, false);
					} else {
						fileName = uF.uploadImage(request, CF.getStrDocSaveLocation()+I_COMPANY+"/"+I_IMAGE+"/", getEmpImage(), getEmpImageFileName(), getEmpImageFileName(), CF, true);
					}
					pst = con.prepareStatement(updateCompanyLogo);
					pst.setString(1, fileName);
					pst.setString(2, O_ORG_LOGO);	
					pst.execute();
					pst.close();

					session.setAttribute("ORG_LOGO", fileName);
				}
				
				return "config";
				
			}else if(getImageType()!=null && getImageType().equalsIgnoreCase("COMPANY_LOGO_SMALL")) {
				
				if( getEmpImage()!=null) {
					if(CF.getStrDocSaveLocation()==null) { 
						fileName = uF.uploadImage(request, DOCUMENT_LOCATION, getEmpImage(), getEmpImageFileName(), getEmpImageFileName(), CF, false);
					} else {
						fileName = uF.uploadImage(request, CF.getStrDocSaveLocation()+I_COMPANY+"/"+I_IMAGE_SMALL+"/", getEmpImage(), getEmpImageFileName(), getEmpImageFileName(), CF, true);
					}
					pst = con.prepareStatement("update settings set value=? where options=?");
					pst.setString(1, fileName);
					pst.setString(2, O_ORG_LOGO_SMALL);	
					pst.execute();
					pst.close();

					session.setAttribute("ORG_LOGO_SMALL", fileName);
				}
				
				return "config";
				
			} else if(getImageType()!=null && getImageType().equalsIgnoreCase("EMPLOYEE_IMAGE")) {
				
//				if(CF.getStrDocSaveLocation()==null){
//					fileName = uF.uploadFile(request, DOCUMENT_LOCATION, getEmpImage(), getEmpImageFileName(), false, CF);
//				}else{
//					fileName = uF.uploadFile(request, CF.getStrDocSaveLocation(), getEmpImage(), getEmpImageFileName(), CF.getIsRemoteLocation(), CF);
//				}
//				if(CF.getStrDocSaveLocation()==null) {
//					fileName = uF.uploadImageDocuments(request, DOCUMENT_LOCATION, getEmpImage(), getEmpImageFileName(), getEmpImageFileName(), CF);
//				} else {
//					fileName = uF.uploadImageDocuments(request, CF.getStrDocSaveLocation()+I_PEOPLE+"/"+I_IMAGE+"/"+getEmpId(), getEmpImage(), getEmpImageFileName(), getEmpImageFileName(), CF);
//				}
				if( getEmpImage()!=null) {
					if(CF.getStrDocSaveLocation()==null) {
						fileName = uF.uploadImage(request, DOCUMENT_LOCATION, getEmpImage(), getEmpImageFileName(), getEmpImageFileName(), CF, false);
					} else {
						fileName = uF.uploadImage(request, CF.getStrDocSaveLocation()+I_PEOPLE+"/"+I_IMAGE+"/"+getEmpId(), getEmpImage(), getEmpImageFileName(), getEmpImageFileName(), CF, true);
					}
//					System.out.println("fileName==>"+fileName);
					pst = con.prepareStatement(updateEmployeeImage);
					pst.setString(1, fileName);
					pst.setInt(2, uF.parseToInt(getEmpId()));
					pst.execute();
					pst.close();
				}
				
				return "profile";
			} else if(getImageType()!=null && getImageType().equalsIgnoreCase("EMPLOYEE_COVER_IMAGE")) {

				if( getEmpImage()!=null) {
					if(CF.getStrDocSaveLocation()==null) {
						fileName = uF.uploadImage(request, DOCUMENT_LOCATION, getEmpImage(), getEmpImageFileName(), getEmpImageFileName(), CF, false);
					} else {
						fileName = uF.uploadImage(request, CF.getStrDocSaveLocation()+I_PEOPLE+"/"+I_IMAGE_COVER+"/"+getEmpId(), getEmpImage(), getEmpImageFileName(), getEmpImageFileName(), CF, true);
					}
//					System.out.println("fileName==>"+fileName);
					pst = con.prepareStatement("update employee_personal_details set emp_cover_image =? where emp_per_id = ?");
					pst.setString(1, fileName);
					pst.setInt(2, uF.parseToInt(getEmpId()));
					pst.execute();
					pst.close();
				}
				
				return "profile";
			}  else if(getImageType()!=null && getImageType().equalsIgnoreCase("ORG_LOGO")) {
				
//				if(CF.getStrDocSaveLocation()==null) {
//					fileName = uF.uploadImageDocuments(request, DOCUMENT_LOCATION, getEmpImage(), getEmpImageFileName(), getEmpImageFileName(), CF);
//				} else {
//					fileName = uF.uploadImageDocuments(request, CF.getStrDocSaveLocation()+I_ORGANISATION+"/"+I_IMAGE+"/", getEmpImage(), getEmpImageFileName(), getEmpImageFileName(), CF);
//				}
				if( getEmpImage()!=null) {
					if(CF.getStrDocSaveLocation()==null) {
						fileName = uF.uploadImage(request, DOCUMENT_LOCATION, getEmpImage(), getEmpImageFileName(), getEmpImageFileName(), CF, false);
					} else {
						fileName = uF.uploadImage(request, CF.getStrDocSaveLocation()+I_ORGANISATION+"/"+I_IMAGE+"/", getEmpImage(), getEmpImageFileName(), getEmpImageFileName(), CF, true);
					}
					
					pst = con.prepareStatement(updateOrgLogo);
	//				pst.setString(1, (fileName!=null && fileName.length()>0)?"logo_"+uF.parseToInt(getOrgId())+".png":"avatar_photo.png");
					pst.setString(1, fileName);
					pst.setInt(2, uF.parseToInt(getOrgId()));
					pst.execute();
					pst.close();
				}
				
				return "location";
			} else if(getImageType()!=null && getImageType().equalsIgnoreCase("ORG_LOGO_SMALL")) {

				if( getEmpImage()!=null) {
					if(CF.getStrDocSaveLocation()==null) {
						fileName = uF.uploadImage(request, DOCUMENT_LOCATION, getEmpImage(), getEmpImageFileName(), getEmpImageFileName(), CF, false);
					} else {
						fileName = uF.uploadImage(request, CF.getStrDocSaveLocation()+I_ORGANISATION+"/"+I_IMAGE_SMALL+"/", getEmpImage(), getEmpImageFileName(), getEmpImageFileName(), CF, true);
					}
					
					pst = con.prepareStatement("update org_details set org_logo_small = ? where org_id=?");
					pst.setString(1, fileName);
					pst.setInt(2, uF.parseToInt(getOrgId()));
					pst.execute();
					pst.close();
				}
				
				return "location";
			} else if(getImageType()!=null && getImageType().equalsIgnoreCase("CANDIDATE_IMAGE")) {
				
				if( getEmpImage()!=null) {
					if(CF.getStrDocSaveLocation()==null) {
						fileName = uF.uploadImage(request, DOCUMENT_LOCATION, getEmpImage(), getEmpImageFileName(), getEmpImageFileName(), CF, false);
					} else {
						fileName = uF.uploadImage(request, CF.getStrDocSaveLocation()+I_CANDIDATE+"/"+I_IMAGE+"/"+getEmpId(), getEmpImage(), getEmpImageFileName(), getEmpImageFileName(), CF, true);
					}
					pst = con.prepareStatement(updateCandidateImage);
					pst.setString(1, (fileName!=null && fileName.length()>0)?fileName:"avatar_photo.png");
					pst.setInt(2, uF.parseToInt(getEmpId()));
					pst.execute();
					pst.close();
				}
				
				return "update";
			} else if(getImageType()!=null && getImageType().equalsIgnoreCase("TRAINER_IMAGE")) {

				if( getEmpImage()!=null) {
					if(CF.getStrDocSaveLocation()==null) {
						fileName = uF.uploadImage(request, DOCUMENT_LOCATION, getEmpImage(), getEmpImageFileName(), getEmpImageFileName(), CF, false);
					} else {
						fileName = uF.uploadImage(request, CF.getStrDocSaveLocation()+I_TRAINER+"/"+I_IMAGE+"/"+getEmpId(), getEmpImage(), getEmpImageFileName(), getEmpImageFileName(), CF, true);
					}
					
					pst = con.prepareStatement("update trainer_personal_details set trainer_image =? where trainer_id = ?");
					pst.setString(1, (fileName!=null && fileName.length()>0)?fileName:"avatar_photo.png");
					pst.setInt(2, uF.parseToInt(getEmpId()));
					pst.execute();
					pst.close();
				}
				
				return "update";
				
			} else if(getImageType()!=null && getImageType().equalsIgnoreCase("CONTENT_IMAGE")) {
				if( getEmpImage()!=null) {
					if(CF.getStrDocSaveLocation()==null){
						fileName = uF.uploadFile(request, DOCUMENT_LOCATION, getEmpImage(), getEmpImageFileName(), false, CF);
					}else{
						fileName = uF.uploadFile(request, CF.getStrDocSaveLocation(), getEmpImage(), getEmpImageFileName(), CF.getIsRemoteLocation(), CF);
					}
	//				System.out.println("fileName ===> "+ fileName);
					pst = con.prepareStatement("update course_content_details set course_content_name =? where course_content_id = ?");
					pst.setString(1, (fileName!=null && fileName.length()>0)?fileName:"avatar_photo.png");
					pst.setInt(2, uF.parseToInt(getContentID()));
					pst.execute();
					pst.close();
				}
				
//				System.out.println("pst ===========> " + pst); 
				return "update";
				
			} else if(getImageType()!=null && getImageType().equalsIgnoreCase("CERTIFICATE_IMAGE1")) {
				if( getEmpImage()!=null) {
					if(CF.getStrDocSaveLocation()==null) {
						fileName = uF.uploadFile(request, DOCUMENT_LOCATION, getEmpImage(), getEmpImageFileName(), false, CF);
					} else {
						fileName = uF.uploadFile(request, CF.getStrDocSaveLocation(), getEmpImage(), getEmpImageFileName(), CF.getIsRemoteLocation(), CF);
					}
					//System.out.println("fileName ===> "+ fileName);
					pst=con.prepareStatement("update training_certificate set certificate_image1=? where certificate_id=?");
					pst.setString(1, (fileName!=null && fileName.length()>0) ? fileName : "avatar_photo.png");
					pst.setInt(2, uF.parseToInt(getContentID()));
					pst.execute();
					pst.close();
				}
				
				return "update";
				
			} else if(getImageType()!=null && getImageType().equalsIgnoreCase("CERTIFICATE_IMAGE2")) {
				if( getEmpImage()!=null) {
					if(CF.getStrDocSaveLocation()==null){
						fileName = uF.uploadFile(request, DOCUMENT_LOCATION, getEmpImage(), getEmpImageFileName(), false, CF);
					}else{
						fileName = uF.uploadFile(request, CF.getStrDocSaveLocation(), getEmpImage(), getEmpImageFileName(), CF.getIsRemoteLocation(), CF);
					}
					//System.out.println("fileName ===> "+ fileName);
					pst=con.prepareStatement("update training_certificate set certificate_image2=? where certificate_id=?");
					pst.setString(1, (fileName!=null && fileName.length()>0) ? fileName : "avatar_photo.png");
					pst.setInt(2, uF.parseToInt(getContentID()));
					pst.execute();
					pst.close();
				}
				
				return "update";
			} else if(getImageType()!=null && getImageType().equalsIgnoreCase("COLLATERAL_IMAGE")) {
//				if(CF.getStrDocSaveLocation()==null){
//					fileName = uF.uploadFile(request, DOCUMENT_LOCATION, getEmpImage(), getEmpImageFileName(), false, CF);
//				}else{
////					fileName = uF.uploadFile(request, CF.getStrDocSaveLocation(), getEmpImage(), getEmpImageFileName(), CF.getIsRemoteLocation(), CF);
//					fileName = uF.uploadImageDocuments(request, CF.getStrDocSaveLocation()+I_COLLATERAL+"/"+I_IMAGE+"/", getEmpImage(), getEmpImageFileName(), getEmpImageFileName(), CF);
//				}
				if( getEmpImage()!=null) {
					if(CF.getStrDocSaveLocation()==null) {
						fileName = uF.uploadImage(request, DOCUMENT_LOCATION, getEmpImage(), getEmpImageFileName(), getEmpImageFileName(), CF, false);
					} else {
						fileName = uF.uploadImage(request, CF.getStrDocSaveLocation()+I_COLLATERAL+"/"+I_IMAGE+"/", getEmpImage(), getEmpImageFileName(), getEmpImageFileName(), CF, true);
					}
	//				System.out.println("fileName ===> "+ fileName);
					pst=con.prepareStatement("update document_collateral set collateral_image = ? where collateral_id = ?");
					pst.setString(1, (fileName!=null && fileName.length()>0) ? fileName : "avatar_photo.png");
					pst.setInt(2, uF.parseToInt(getContentID()));
	//				System.out.println("pst ===> "+ pst);
					pst.execute();
					pst.close();
				}
				
				return "update";
			} else if(getImageType()!=null && getImageType().equalsIgnoreCase("CLIENT_COMPANY_LOGO")) {
//				System.out.println("CLIENT_COMPANY_LOGO ========>> " + getEmpImage());
//				if(CF.getStrDocSaveLocation()==null) {
//					fileName = uF.uploadFile(request, DOCUMENT_LOCATION, getEmpImage(), getEmpImageFileName(), false, CF);
//				} else {
//					fileName = uF.uploadImageDocuments(request, CF.getStrDocSaveLocation()+I_CUSTOMER+"/"+getContentID()+"/"+I_IMAGE+"/", getEmpImage(), getEmpImageFileName(), getEmpImageFileName(), CF);
////					fileName = uF.uploadFile(request, CF.getStrDocSaveLocation(), getEmpImage(), getEmpImageFileName(), CF.getIsRemoteLocation(), CF);
//				}
				if(getEmpImage()!=null) {
					if(CF.getStrDocSaveLocation()==null) {
						fileName = uF.uploadImage(request, DOCUMENT_LOCATION, getEmpImage(), getEmpImageFileName(), getEmpImageFileName(), CF, false);
					} else {
						fileName = uF.uploadImage(request, CF.getStrDocSaveLocation()+I_CUSTOMER+"/"+getContentID()+"/"+I_IMAGE+"/", getEmpImage(), getEmpImageFileName(), getEmpImageFileName(), CF, true);
					}
	//				System.out.println("fileName ===> "+ fileName);
					pst=con.prepareStatement("update client_details set client_logo = ? where client_id = ?");
					pst.setString(1, (fileName!=null && fileName.length()>0) ? fileName : "avatar_photo.png");
					pst.setInt(2, uF.parseToInt(getContentID()));
					pst.execute();
					pst.close();
				}
//				System.out.println("pst ===> " + pst);

				//return "update";
			} else if(getImageType()!=null && getImageType().equalsIgnoreCase("CLIENT_CONTACT_PHOTO")) {
//				if(CF.getStrDocSaveLocation()==null){
//					fileName = uF.uploadFile(request, DOCUMENT_LOCATION, getEmpImage(), getEmpImageFileName(), false, CF);
//				} else {
//					fileName = uF.uploadImageDocuments(request, CF.getStrDocSaveLocation()+I_CUSTOMER+"/"+getCustomerID()+"/"+I_IMAGE+"/"+I_CUSTOMER_SPOC+"/", getEmpImage(), getEmpImageFileName(), getEmpImageFileName(), CF);
////					fileName = uF.uploadFile(request, CF.getStrDocSaveLocation(), getEmpImage(), getEmpImageFileName(), CF.getIsRemoteLocation(), CF);
//				}
				if(getEmpImage()!=null) {
					if(CF.getStrDocSaveLocation()==null) {
						fileName = uF.uploadImage(request, DOCUMENT_LOCATION, getEmpImage(), getEmpImageFileName(), getEmpImageFileName(), CF, false);
					} else {
						fileName = uF.uploadImage(request, CF.getStrDocSaveLocation()+I_CUSTOMER+"/"+getCustomerID()+"/"+I_IMAGE+"/"+I_CUSTOMER_SPOC+"/", getEmpImage(), getEmpImageFileName(), getEmpImageFileName(), CF, true);
					}
					//System.out.println("fileName ===> "+ fileName);
					pst=con.prepareStatement("update client_poc set contact_photo = ? where poc_id = ?");
					pst.setString(1, (fileName!=null && fileName.length()>0) ? fileName : "avatar_photo.png");
					pst.setInt(2, uF.parseToInt(getContentID()));
					pst.execute();
					pst.close();
				}
				//return "update";
			} else if(getImageType()!=null && getImageType().equalsIgnoreCase("CLIENT_BRAND_PHOTO")) {
				if(getEmpImage()!=null) {
					if(CF.getStrDocSaveLocation()==null) {
						fileName = uF.uploadImage(request, DOCUMENT_LOCATION, getEmpImage(), getEmpImageFileName(), getEmpImageFileName(), CF, false);
					} else {
						fileName = uF.uploadImage(request, CF.getStrDocSaveLocation()+I_CUSTOMER+"/"+getCustomerID()+"/"+I_IMAGE+"/"+I_CUSTOMER_BRAND+"/", getEmpImage(), getEmpImageFileName(), getEmpImageFileName(), CF, true);
					}
					//System.out.println("fileName ===> "+ fileName);
					pst=con.prepareStatement("update client_brand_details set client_brand_logo = ? where client_brand_id = ?");
					pst.setString(1, (fileName!=null && fileName.length()>0) ? fileName : "avatar_photo.png");
					pst.setInt(2, uF.parseToInt(getContentID()));
					pst.execute();
					pst.close();
				}
				//return "update";
			} else if(getImageType()!=null && getImageType().equalsIgnoreCase("PERFORMANCE_SAMPLE") && getEmpImage()!=null) {
				if(CF.getStrDocSaveLocation()==null){
					fileName = uF.uploadFile(request, DOCUMENT_LOCATION, getEmpImage(), getEmpImageFileName(), false, CF);
				} else {
					fileName = uF.uploadImageDocuments(request, CF.getStrDocSaveLocation()+I_PERFORMANCE+"/"+getPerformanceID()+"/", getEmpImage(), getEmpImageFileName(), getEmpImageFileName(), CF);
//					fileName = uF.uploadFile(request, CF.getStrDocSaveLocation(), getEmpImage(), getEmpImageFileName(), CF.getIsRemoteLocation(), CF);
				}
				//System.out.println("fileName ===> "+ fileName);
				pst=con.prepareStatement("update performance_details set performance_file_name = ? where performance_id = ?");
				pst.setString(1, (fileName!=null && fileName.length()>0) ? fileName : "-");
				pst.setInt(2, uF.parseToInt(getPerformanceID()));
				pst.execute();
				pst.close();
				
				pst=con.prepareStatement("update performance_details_empwise set performance_file_name = ? where performance_id = ?");
				pst.setString(1, (fileName!=null && fileName.length()>0) ? fileName : "-");
				pst.setInt(2, uF.parseToInt(getPerformanceID()));
				pst.execute();
				pst.close();

			} else if(getImageType()!=null && getImageType().equalsIgnoreCase("EMP_PERFORMANCE_FILE") && getEmpImage()!=null) {
				if(CF.getStrDocSaveLocation()==null){
					fileName = uF.uploadFile(request, DOCUMENT_LOCATION, getEmpImage(), getEmpImageFileName(), false, CF);
				} else {
					fileName = uF.uploadImageDocuments(request, CF.getStrDocSaveLocation()+I_PERFORMANCE+"/"+getEmpId()+"/", getEmpImage(), getEmpImageFileName(), getEmpImageFileName(), CF);
//					fileName = uF.uploadFile(request, CF.getStrDocSaveLocation(), getEmpImage(), getEmpImageFileName(), CF.getIsRemoteLocation(), CF);
				}
				//System.out.println("fileName ===> "+ fileName);
				
				pst=con.prepareStatement("update performance_details_empwise set emp_performance_file_name=? where performance_id=? and emp_id=?");
				pst.setString(1, (fileName!=null && fileName.length()>0) ? fileName : "-");
				pst.setInt(2, uF.parseToInt(getPerformanceID()));
				pst.setInt(3, uF.parseToInt(getEmpId()));
				pst.execute();
				pst.close();
			}else if(getImageType()!=null && getImageType().equalsIgnoreCase("EVENT_IMAGE") && getEmpImage()!=null) {
				//System.out.println("image name==>"+getEmpImageFileName());
				if(CF.getStrDocSaveLocation()==null){
					fileName = uF.uploadFile(request, DOCUMENT_LOCATION, getEmpImage(), getEmpImageFileName(), false, CF);
				} else {
					fileName = uF.uploadImageDocuments(request, CF.getStrDocSaveLocation()+I_EVENTS+"/"+getEmpId(), getEmpImage(), getEmpImageFileName(), getEmpImageFileName(), CF);

				}
				
				//System.out.println("fileName==> "+fileName);
				pst=con.prepareStatement("update events set event_image=? where event_id=? ");
				pst.setString(1, (fileName!=null && fileName.length()>0) ? fileName : "-");
				pst.setInt(2, uF.parseToInt(getEventId()));
				pst.execute();
				pst.close();
				
			}else if(getImageType()!=null && getImageType().equalsIgnoreCase("BOOK_IMAGE") && getEmpImage()!=null) {
				  if(CF.getStrDocSaveLocation()==null){
					fileName = uF.uploadFile(request, DOCUMENT_LOCATION, getEmpImage(), getEmpImageFileName(), false, CF);
				} else {
					fileName = uF.uploadImageDocuments(request, CF.getStrDocSaveLocation()+I_BOOKS+"/"+getEmpId()+"/"+getBookId(), getEmpImage(), getEmpImageFileName(), getEmpImageFileName(), CF);
				}
				
				pst=con.prepareStatement("update book_details set book_image=? where book_id=?");
				pst.setString(1, (fileName!=null && fileName.length()>0) ? fileName : "-");
				pst.setInt(2, uF.parseToInt(getBookId()));
//				System.out.println("pst==>"+pst);
				pst.execute();
				pst.close();
			}else if(getImageType()!=null && getImageType().equalsIgnoreCase("DISH_IMAGE") && getEmpImage()!=null) {
				  if(CF.getStrDocSaveLocation()==null){
						fileName = uF.uploadFile(request, DOCUMENT_LOCATION, getEmpImage(), getEmpImageFileName(), false, CF);
					} else {
						fileName = uF.uploadImageDocuments(request, CF.getStrDocSaveLocation()+I_DISHES+"/"+getEmpId()+"/"+getDishId(), getEmpImage(), getEmpImageFileName(), getEmpImageFileName(), CF);
					}
					
					pst=con.prepareStatement("update dish_details set dish_image=? where dish_id=? ");
					pst.setString(1, (fileName!=null && fileName.length()>0) ? fileName : "-");
					pst.setInt(2, uF.parseToInt(getDishId()));
					pst.execute();
					pst.close();
			}else if(getImageType()!=null && getImageType().equalsIgnoreCase("COMPANY_MANUAL") && getEmpImage()!=null) {
				  if(CF.getStrDocSaveLocation()==null){
						fileName = uF.uploadFile(request, DOCUMENT_LOCATION, getEmpImage(), getEmpImageFileName(), false, CF);
					} else {
						fileName = uF.uploadImageDocuments(request, CF.getStrDocSaveLocation()+I_COMPANY_MANUAL+"/"+getManualId(), getEmpImage(), getEmpImageFileName(), getEmpImageFileName(), CF); //+"/"+getEmpId()
					}
					
					pst=con.prepareStatement("update company_manual set manual_doc =? where manual_id =? ");
					pst.setString(1, (fileName!=null && fileName.length()>0) ? fileName : "-");
					pst.setInt(2, uF.parseToInt(getManualId()));
//					System.out.println("pst==>"+pst);
					pst.execute();
					pst.close();
			} else if(getImageType()!=null && getImageType().equalsIgnoreCase("SIGNATURE_IMAGE")) {
			if( getEmpImage()!=null) {
				if(CF.getStrDocSaveLocation()==null) {
					fileName = uF.uploadImage(request, DOCUMENT_LOCATION, getEmpImage(), getEmpImageFileName(), getEmpImageFileName(), CF, false);
				} else {
					StringBuilder sbPath = new StringBuilder(CF.getStrDocSaveLocation()+I_ORGANISATION+"/"+getOrgId()+"/"+I_DOC_SIGN+"/");
					if(uF.parseToInt(getEmpId()) > 0) {
						sbPath.append(getEmpId()+"/");
					}
					fileName = uF.uploadImage(request, sbPath.toString(), getEmpImage(), getEmpImageFileName(), getEmpImageFileName(), CF, true);
				}
//				System.out.println("fileName ===> "+ fileName);
				pst=con.prepareStatement("update document_signature set signature_image = ? where signature_id = ?");
				pst.setString(1, (fileName!=null && fileName.length()>0) ? fileName : "avatar_photo.png");
				pst.setInt(2, uF.parseToInt(getContentID()));
//				System.out.println("pst ===> "+ pst);
				pst.execute();
				pst.close();
			}
			
			return "update";
		}
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS; 
	}

	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}
	public File getEmpImage() {
		return empImage;
	}

	public void setEmpImage(File empImage) {
		this.empImage = empImage;
	}
	public String getImageType() {
		return imageType;
	}

	public void setImageType(String imageType) {
		this.imageType = imageType;
	}

	public String getContentID() {
		return contentID;
	}

	public void setContentID(String contentID) {
		this.contentID = contentID;
	}

	public String getCustomerID() {
		return customerID;
	}

	public void setCustomerID(String customerID) {
		this.customerID = customerID;
	}

	public String getEmpId() {
		return empId;
	}

	public void setEmpId(String empId) {
		this.empId = empId;
	}

	public String getPerformanceID() {
		return performanceID;
	}

	public void setPerformanceID(String performanceID) {
		this.performanceID = performanceID;
	}

	public String getEmpImageFileName() {
		return empImageFileName;
	}

	public String getEventId() {
		return eventId;
	}

	public void setEventId(String eventId) {
		this.eventId = eventId;
	}

	public void setEmpImageFileName(String empImageFileName) {
		this.empImageFileName = empImageFileName;
	}

	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}
	
	public void setCF(CommonFunctions CF) {
		this.CF = CF;
	}

	public String getBookId() {
		return bookId;
	}

	public void setBookId(String bookId) {
		this.bookId = bookId;
	}

	public String getDishId() {
		return dishId;
	}

	public void setDishId(String dishId) {
		this.dishId = dishId;
	}

	public String getManualId() {
		return manualId;
	}

	public void setManualId(String manualId) {
		this.manualId = manualId;
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

}
