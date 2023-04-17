package com.konnect.jpms.document;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UploadImage;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class EditCollateral extends ActionSupport implements ServletRequestAware, IStatements {

	/**
	 * 
	 */

	private static final long serialVersionUID = 1L;

	HttpSession session;
	CommonFunctions CF;
	
	String orgId;
	String strCollateralName;
	String strCollateralType;
	String strCollateralTypeValue;
	 
	String strCollateralId;
	
	private File strCollateralImg;
	String strCollateralImgFileName;
	
	String imgAlign;
	String imgAlignValue;
	String strCollateralText;
	String textAlign;
	String textAlignValue;
	String strImageName;
	 
	String collateralID;
	String operation;
	
	String userscreen;
	String navigationId;
	String toPage;
	
	public String execute() {

		String strId = request.getParameter("strId");
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;

		if ((operation != null && operation.equals("U"))) {
			return updateCollateral();
		}
		
		getCollateral();
		
		return "load";
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
			pst = con.prepareStatement("select * from  document_collateral where collateral_id =?");
			pst.setInt(1, uF.parseToInt(getCollateralID()));
			rs=pst.executeQuery();
//			System.out.println("pst ===>>>> "+ pst);
			while(rs.next()){
				setStrCollateralId(rs.getString("collateral_id"));
				setStrCollateralName(rs.getString("collateral_name"));
				setStrCollateralTypeValue(rs.getString("_type"));
				setStrImageName(rs.getString("collateral_image"));
				/*if(rs.getString("collateral_image") != null && !rs.getString("collateral_image").equals("")) {
					collateral_image="<img  class=\"lazy\" src=\"userImages/avatar_photo.png\" data-original=\""+CF.getStrDocRetriveLocation()+"/"+uF.showData(rs.getString("collateral_image"), "avatar_photo.png")+"\" border=\"0\"  height=\"100\" />";
				}*/
//				System.out.println("collateral_image ===>>> " + collateral_image);
				
//				hmInner.put("COLLATERAL_PATH", collateral_image);
				setImgAlignValue(rs.getString("image_align"));
				setStrCollateralText(uF.showData(rs.getString("collateral_text"),""));
				setTextAlignValue(rs.getString("text_align"));
			}
			rs.close();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private String updateCollateral() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		ResultSet rs = null;
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("update document_collateral set collateral_name=?, _type=?, text_align=?, image_align=?, collateral_text=? " +
					"where collateral_id = ?");
			pst.setString(1, getStrCollateralName());
			pst.setString(2, getStrCollateralType()!=null && !getStrCollateralType().equals("")?getStrCollateralType():"H");
			pst.setString(3, "L");
//			pst.setString(3, getTextAlign());
//			if(getStrImageName()!=null || getStrCollateralImgFileName()!=null) {
//				pst.setString(4, getImgAlign());
//			} else {
//				pst.setString(4, "");
//			}
			pst.setString(4, "L");
			if(getStrCollateralType()!=null && getStrCollateralType().equals("F")) {
				pst.setString(5, getStrCollateralText());
			} else {
				pst.setString(5, "");
			}
			pst.setInt(6, uF.parseToInt(getCollateralID()));
			pst.execute();
			pst.close();
			
			if(getStrCollateralImgFileName()!=null) {
				uploadImage(uF.parseToInt(getCollateralID()));
			}
			
			session.setAttribute(MESSAGE, SUCCESSM+"Collateral updated successfully."+END);
			/*} else {
				session.setAttribute(MESSAGE, ERRORM+"Collateral could not be updated.<br/>Please try again."+END);
			} */ 

		} catch (Exception e) {
			e.printStackTrace();
			session.setAttribute(MESSAGE, ERRORM+"Collateral could not be updated.<br/>Please try again."+END);
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

	public String getStrCollateralTypeValue() {
		return strCollateralTypeValue;
	}

	public void setStrCollateralTypeValue(String strCollateralTypeValue) {
		this.strCollateralTypeValue = strCollateralTypeValue;
	}

	public String getImgAlignValue() {
		return imgAlignValue;
	}

	public void setImgAlignValue(String imgAlignValue) {
		this.imgAlignValue = imgAlignValue;
	}

	public String getTextAlignValue() {
		return textAlignValue;
	}

	public void setTextAlignValue(String textAlignValue) {
		this.textAlignValue = textAlignValue;
	}

	public String getStrImageName() {
		return strImageName;
	}

	public void setStrImageName(String strImageName) {
		this.strImageName = strImageName;
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
