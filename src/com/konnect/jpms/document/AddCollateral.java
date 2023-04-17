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

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UploadImage;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class AddCollateral extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;

	HttpSession session;
	CommonFunctions CF;
	
	private String orgId;
	private String strCollateralName; 
	private String strCollateralType;
	 
	private String strCollateralId;
	
	private File strCollateralImg;
	private String strCollateralImgFileName;
	
	private String imgAlign;
	private String strCollateralText;
	private String textAlign;
	 
	private String collateralID;
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

		
		
		if (operation != null && operation.equals("D")) {
			return deleteCollateral(getCollateralID());
		}
		/*if (operation != null && operation.equals("E")) {
			return viewDocument(strId);
		}*/
		/*if (getStrCollateralId() != null && getStrCollateralId().length() > 0) {
			return updateDocument();
		}*/
		if ((operation == null || !operation.equals("D")) && getStrCollateralName() != null && getStrCollateralName().length() > 0) {
			return insertCollateral();
		}
		
		getCollateral();
		
		loadDocument(strId);

		return "load";
	}

	private String deleteCollateral(String collateralID) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("delete from document_collateral where collateral_id = ?");
			pst.setInt(1, uF.parseToInt(collateralID));
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

	
	private void getCollateral() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs=null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			con = db.makeConnection(con);
			List<Map<String,String>> collateralList=new ArrayList<Map<String,String>>(); 
			pst = con.prepareStatement("select * from  document_collateral order by collateral_id desc");
			rs=pst.executeQuery();
			while(rs.next()){
				Map<String, String> hmInner=new HashMap<String, String>();
				hmInner.put("COLLATERAL_ID", rs.getString("collateral_id"));
				hmInner.put("COLLATERAL_NAME", rs.getString("collateral_name"));
				String type="Header";
				if(rs.getString("_type")!=null && rs.getString("_type").equals("F")){
					type="Footer";
				}
				hmInner.put("COLLATERAL_TYPE", type);
				String collateral_image= "";
				if(rs.getString("collateral_image") != null && !rs.getString("collateral_image").equals("")) {
//					collateral_image="<img  class=\"lazy\" src=\"userImages/avatar_photo.png\" data-original=\""+CF.getStrDocRetriveLocation()+"/"+uF.showData(rs.getString("collateral_image"), "avatar_photo.png")+"\" border=\"0\"  height=\"100\" />";
					if(CF.getStrDocRetriveLocation()==null) {  
						collateral_image = "<img class=\"lazy img-circle\" src=\"userImages/company_avatar_photo.png\" data-original=\""+ DOCUMENT_LOCATION +rs.getString("collateral_image")+"\" width=\"50%\"/> ";
					} else { 
						collateral_image = "<img class=\"lazy img-circle\" src=\"userImages/company_avatar_photo.png\" data-original=\""+ CF.getStrDocRetriveLocation() +I_COLLATERAL+"/"+I_IMAGE+"/"+rs.getString("collateral_image")+"\" width=\"50%\"/>";
					}
				}
				hmInner.put("COLLATERAL_PATH", collateral_image);
				hmInner.put("COLLATERAL_IMG_ALIGN", rs.getString("image_align"));
				hmInner.put("COLLATERAL_TEXT", uF.showData(rs.getString("collateral_text"),""));
				hmInner.put("COLLATERAL_TEXT_ALIGN", rs.getString("text_align"));
				
				collateralList.add(hmInner);
			}
			rs.close();
			pst.close();
			request.setAttribute("collateralList",collateralList);

			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}

	private String insertCollateral() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		ResultSet rs = null;
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			con = db.makeConnection(con);
			
			String filename=null;
			
			/*if(getStrCollateralImgFileName()!=null) {*/

				/*if(CF.getStrDocSaveLocation()!=null) {
					filename = uF.uploadFile(request, CF.getStrDocSaveLocation(), getStrCollateralImg(), getStrCollateralImgFileName(), CF.getIsRemoteLocation(), CF);
				} else {
					filename = uF.uploadFile(request, DOCUMENT_LOCATION, getStrCollateralImg(), getStrCollateralImgFileName(), CF.getIsRemoteLocation(), CF);
				}*/
							
				pst = con.prepareStatement("insert into document_collateral (collateral_name, _type, text_align, image_align, collateral_text) values(?,?,?,?,?)");
				pst.setString(1, getStrCollateralName());
				pst.setString(2, getStrCollateralType()!=null && !getStrCollateralType().equals("")?getStrCollateralType():"H");
				pst.setString(3, getTextAlign());
				if(getStrCollateralImgFileName()!=null) {
					pst.setString(4, getImgAlign());
				} else {
					pst.setString(4, "");
				}
				pst.setString(5, getStrCollateralText());
//				System.out.println("pst insert ==>"+pst);
				pst.execute();
				pst.close();
				
				pst = con.prepareStatement("select max(collateral_id) as collateral_id from document_collateral");
				rs = pst.executeQuery();
				 int newCollateralId = 0;
				if(rs.next()) {
					newCollateralId = rs.getInt("collateral_id");
				}
				rs.close();
				pst.close();
				if(getStrCollateralImgFileName()!=null) {
					uploadImage(newCollateralId);
				}
				
				session.setAttribute(MESSAGE, SUCCESSM+"Collateral saved successfully."+END);
			/*} else {
				session.setAttribute(MESSAGE, ERRORM+"Collateral could not be saved.<br/>Please try again."+END);
			} */ 

		} catch (Exception e) {
			e.printStackTrace();
			session.setAttribute(MESSAGE, ERRORM+"Collateral could not be saved.<br/>Please try again."+END);
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}

	
	private void uploadImage(int contentID) {
		
		try {
			UploadImage uI = new UploadImage();
			uI.setServletRequest(request);
			uI.setImageType("COLLATERAL_IMAGE");
			uI.setEmpImage(getStrCollateralImg());
			uI.setEmpImageFileName(getStrCollateralImgFileName());
			uI.setContentID(contentID+"");
			uI.setCF(CF);
			uI.upoadImage();
			
		}catch (Exception e) {
			e.printStackTrace();
			
		}
		
	}

	private void loadDocument(String strId) {
		setStrCollateralId(strId);
	}

	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

	public String getStrCollateralName() {
		return strCollateralName;
	}

	public void setStrCollateralName(String strCollateralName) {
		this.strCollateralName = strCollateralName;
	}

	public String getStrCollateralType() {
		return strCollateralType;
	}

	public void setStrCollateralType(String strCollateralType) {
		this.strCollateralType = strCollateralType;
	}


	public String getStrCollateralId() {
		return strCollateralId;
	}

	public void setStrCollateralId(String strCollateralId) {
		this.strCollateralId = strCollateralId;
	}


	public File getStrCollateralImg() {
		return strCollateralImg;
	}

	public void setStrCollateralImg(File strCollateralImg) {
		this.strCollateralImg = strCollateralImg;
	}

	public String getStrCollateralImgFileName() {
		return strCollateralImgFileName;
	}

	public void setStrCollateralImgFileName(String strCollateralImgFileName) {
		this.strCollateralImgFileName = strCollateralImgFileName;
	}

	public String getImgAlign() {
		return imgAlign;
	}

	public void setImgAlign(String imgAlign) {
		this.imgAlign = imgAlign;
	}

	public String getStrCollateralText() {
		return strCollateralText;
	}

	public void setStrCollateralText(String strCollateralText) {
		this.strCollateralText = strCollateralText;
	}

	public String getTextAlign() {
		return textAlign;
	}

	public void setTextAlign(String textAlign) {
		this.textAlign = textAlign;
	}

	public String getCollateralID() {
		return collateralID;
	}

	public void setCollateralID(String collateralID) {
		this.collateralID = collateralID;
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


	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	
}
