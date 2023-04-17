package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.konnect.jpms.common.ViewCompanyManual;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class ViewFeedsFilePopup1 extends ActionSupport implements IStatements,ServletRequestAware, ServletResponseAware 
{
  
	private static final long serialVersionUID = 1L;

	HttpSession session;
	CommonFunctions CF;
	String strOrgId;
	
	private static Logger log = Logger.getLogger(ViewCompanyManual.class);
	
	private String strFeedId;
	String strUserType;
		
	public ViewFeedsFilePopup1(HttpServletRequest request,CommonFunctions CF, String strOrgId) {
		super();
		this.request = request;
		this.CF = CF;
		this.strOrgId = strOrgId;
		
	}
	
	public ViewFeedsFilePopup1() {
		
	}
	public String execute() throws Exception {
		session = request.getSession(true);
		
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
	
		strOrgId = (String)session.getAttribute(ORGID);
		strUserType = (String)session.getAttribute(BASEUSERTYPE);
				
		request.setAttribute(PAGE, "/jsp/reports/ViewFeedsFilePopup.jsp");
		request.setAttribute(TITLE, "View Feed File");
			
		
		UtilityFunctions uF = new UtilityFunctions();
//		System.out.println("feedId==>"+getStrFeedId());
		viewFeedsFile(uF);
		return LOAD;
	}
	
	
	private void viewFeedsFile(UtilityFunctions uF) {
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try{
			
			con = db.makeConnection(con);
			if(uF.parseToInt(getStrFeedId()) > 0) {
				pst = con.prepareStatement("select * from communication_1 where communication_id = ?");
				pst.setInt(1, uF.parseToInt(getStrFeedId()));
				rs = pst.executeQuery();
			/*	System.out.println("pst ===>> " + pst);		*/		
				while(rs.next()) {
					String extenstion = null;
					if(rs.getString("doc_or_image") !=null && !rs.getString("doc_or_image").trim().equals("")){
						extenstion = FilenameUtils.getExtension(rs.getString("doc_or_image").trim());
					}
					String strAddedby = null;
					if(strUserType != null && strUserType.equals(CUSTOMER)) {
						
						strAddedby = rs.getString("client_created_by");
					} else {
						
						strAddedby = rs.getString("created_by");
					}					
					String feedMainPath  = "";
					if(rs.getString("doc_or_image")!=null && !rs.getString("doc_or_image").equals("")){
						feedMainPath = getFeedMainPath(con, uF, rs.getString("communication_id"),strAddedby)+"/"+ rs.getString("doc_or_image");
					}
					
					List<String> availableExt = CF.getAvailableExtention();
					request.setAttribute("availableExt", availableExt);
					request.setAttribute("feedFile",uF.showData(rs.getString("doc_or_image"),"Not Available"));
					request.setAttribute("extention",extenstion);
					request.setAttribute("feedDocPath",feedMainPath);
				}
				rs.close();
				pst.close();
			} 
						
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}
	
	private String getFeedMainPath(Connection con, UtilityFunctions uF, String postId, String strEmpId) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		String feedPath = null;
		try {
			
			pst = con.prepareStatement("select * from project_document_details where feed_id = ?");
			pst.setInt(1, uF.parseToInt(postId));
			rs = pst.executeQuery();
			String docFolderId = null;
			String docMainFolderId = null;
			String docFolderName = null;
			String docMainFolderName = null;
			String proOrCat = null;
			String proId = null;
			while(rs.next()) {
				docFolderId = rs.getString("pro_folder_id");
				proOrCat = rs.getString("project_category");
				proId = rs.getString("pro_id");
			}
			rs.close();
			pst.close();
			String empOrgId = CF.getEmpOrgId(con, uF, strEmpId);
			if(uF.parseToInt(proOrCat) == 0) {
				feedPath = CF.getRetriveProjectDocumentFolder() +empOrgId+"/Uncategorised";
			} else if(uF.parseToInt(proOrCat) == 1) {
				feedPath = CF.getRetriveProjectDocumentFolder() +empOrgId+"/Projects";
			} else if(uF.parseToInt(proOrCat) == 2) {
				feedPath = CF.getRetriveProjectDocumentFolder() +empOrgId+"/Categories";
			}
			
			String folderPath = null;
			if(uF.parseToInt(docFolderId) > 0) {
				pst = con.prepareStatement("select * from project_document_details where pro_document_id = ?");
				pst.setInt(1, uF.parseToInt(docFolderId));
				rs = pst.executeQuery();
				while(rs.next()) {
					docFolderName = rs.getString("folder_name");
					docMainFolderId = rs.getString("pro_folder_id");
					proId = rs.getString("pro_id");
				}
				rs.close();
				pst.close();
				
				folderPath = "/"+docFolderName;
			}
			
			if(uF.parseToInt(docMainFolderId) > 0) {
				pst = con.prepareStatement("select * from project_document_details where pro_document_id = ?");
				pst.setInt(1, uF.parseToInt(docMainFolderId));
				rs = pst.executeQuery();
				while(rs.next()) {
					docMainFolderName = rs.getString("folder_name");
					proId = rs.getString("pro_id");
				}
				rs.close();
				pst.close();
				
				folderPath = "/"+docMainFolderName+"/"+docFolderName;
			}
			if(uF.parseToInt(proOrCat) == 1) {
				feedPath = feedPath +"/"+proId;
			}
			if(folderPath != null && !folderPath.equals("")) {
				feedPath = feedPath + folderPath;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return feedPath;
	}
	
	private HttpServletRequest request;
	private HttpServletResponse response;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}
	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response= response;
		
	}

	public String getStrFeedId() {
		return strFeedId;
	}

	public void setStrFeedId(String strFeedId) {
		this.strFeedId = strFeedId;
	}

	
}