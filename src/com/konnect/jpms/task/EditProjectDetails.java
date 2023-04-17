package com.konnect.jpms.task;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.apache.struts2.interceptor.SessionAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

public class EditProjectDetails extends ActionSupport implements
		ServletRequestAware, ServletResponseAware, SessionAware, IStatements {

	private static final long serialVersionUID = 1L;
	int pro_id;
	String pro_name;
	String service;
	String description;
	int idealtime;
	String deadline;
	String submit;
	CommonFunctions CF; 
	String filename;
	private HttpServletRequest request;
	File document;
	private String documentContentType;
	private String documentFileName;
	Map session;

	public String execute() {
			session = ActionContext.getContext().getSession();
			CF = (CommonFunctions) session.get(CommonFunctions);
			if (CF == null)
				return LOGIN;
			
			request.setAttribute(PAGE, "/jsp/task/EditProjectDetails.jsp");
			request.setAttribute(TITLE, "Edit Project Details");
			if (submit != null) {
				updateProjectDetails();
				session.put("pro_id", pro_id);
			}
		return SUCCESS;
	}

	public void updateProjectDetails() {
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			if (document != null) {
				upload(document, documentFileName);
			}
			pst = con.prepareStatement("UPDATE projectmntnc SET pro_name=?,service=?,description=?,idealtime=?,deadline=?,document_name=? WHERE pro_id =?");
			pst.setString(1, pro_name);
			pst.setString(2, service);
			pst.setString(3, description);
			pst.setInt(4, idealtime);
			pst.setDate(5, uF.getDateFormat(getDeadline(), DATE_FORMAT));
			pst.setString(6, filename);
			pst.setInt(7, pro_id);
			pst.executeUpdate();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	

	public void upload(File file, String fileFileName) throws Exception {
		if (fileFileName.contains(" ")) {
			fileFileName = fileFileName.replace(" ", "");
		}
		String uploadDir = ServletActionContext.getServletContext().getRealPath("/taskuploads") + "/";
		File dirPath = new File(uploadDir);
		if (!dirPath.exists()) {
			dirPath.mkdirs();
		}
		InputStream stream = new FileInputStream(file);
		filename = pro_name + "_" + fileFileName;
		OutputStream bos = new FileOutputStream(uploadDir + filename);
		int bytesRead;
		byte[] buffer = new byte[8192];
		while ((bytesRead = stream.read(buffer, 0, 8192)) != -1) {
			bos.write(buffer, 0, bytesRead);
		}
		bos.close();
		stream.close();
	}

	public int getIdealtime() {
		return idealtime;
	}

	public void setIdealtime(int idealtime) {
		this.idealtime = idealtime;
	}

	public String getSubmit() {
		return submit;
	}

	public void setSubmit(String submit) {
		this.submit = submit;
	}

	public Map getSession() {
		return session;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public File getDocument() {
		return document;
	}

	public void setDocument(File document) {
		this.document = document;
	}

	public String getDocumentContentType() {
		return documentContentType;
	}

	public void setDocumentContentType(String documentContentType) {
		this.documentContentType = documentContentType;
	}

	public String getDocumentFileName() {
		return documentFileName;
	}

	public void setDocumentFileName(String documentFileName) {
		this.documentFileName = documentFileName;
	}

	public int getPro_id() {
		return pro_id;
	}

	public void setPro_id(int pro_id) {
		this.pro_id = pro_id;
	}

	public String getPro_name() {
		return pro_name;
	}

	public void setPro_name(String pro_name) {
		this.pro_name = pro_name;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDeadline() {
		return deadline;
	}

	public void setDeadline(String deadline) {
		this.deadline = deadline;
	}

	@Override
	public void setSession(Map arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setServletResponse(HttpServletResponse arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}
}
