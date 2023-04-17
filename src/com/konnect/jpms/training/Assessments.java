package com.konnect.jpms.training;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.IStatements;
import com.opensymphony.xwork2.ActionSupport;

public class Assessments extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	String strSessionEmpId = null;

	
	private int empId;

	CommonFunctions CF = null;

	private static Logger log = Logger.getLogger(Assessments.class);

	public String execute() {

		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null) {
			return LOGIN;
		}

		/*
		 * boolean isView = CF.getAccess(session, request, uF); if(!isView){
		 * request.setAttribute(PAGE, PAccessDenied);
		 * request.setAttribute(TITLE, TAccessDenied); return ACCESS_DENIED; }
		 */

		request.setAttribute(PAGE, "/jsp/training/Assessments.jsp");
		request.setAttribute(TITLE, "Assessments");

	//	prepareInformation();

		return SUCCESS;

	}

	/*private void prepareInformation() {

		Database db = new Database();
		PreparedStatement pst = null;
		Connection con = null;
		UtilityFunctions uF = new UtilityFunctions();
		ResultSet rst = null;

		List<List<String>> alCertificateinfo = new ArrayList<List<String>>();
		try {
			con = db.makeConnection(con);

			pst = con.prepareStatement("select * from training_certificate order by certificate_id desc");
			rst = pst.executeQuery();

			while (rst.next()) {

				List<String> alInner = new ArrayList<String>();

				alInner.add(rst.getString("certificate_id"));
				alInner.add(rst.getString("certificate_name"));
				alInner.add(uF.limitContent(rst.getString("certificate_desc"),100));
				if (rst.getString("certificate_image1") != null) {
					alInner.add("<a href=" + request.getContextPath() + DOCUMENT_LOCATION + rst.getString("certificate_image1")
							+ "><img src=\"images1/certificate_small.png\" title=\"Download Smaller Image\"/></a>");
				} else {
					alInner.add("");
				}

				if (rst.getString("certificate_image2") != null) {
					alInner.add("<a href=" + request.getContextPath() + DOCUMENT_LOCATION + rst.getString("certificate_image2")
							+ "><img src=\"images1/certificate_large.png\" title=\"Download Larger Image\"/></a>");
				} else {
					alInner.add("");
				}
				alInner.add(rst.getString("print_mode"));
				
				alCertificateinfo.add(alInner);
			}
			System.out.println("printin alCertificateinfo info====="+alCertificateinfo);
			request.setAttribute("alCertificateinfo", alCertificateinfo);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			db.closeConnection(con);
			db.closeResultSet(rst);
			db.closeStatements(pst);

		}
	}*/

	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {

		this.request = request;

	}

}