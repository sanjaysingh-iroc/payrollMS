package com.konnect.jpms.recruitment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class DesignationDetails extends ActionSupport implements ServletRequestAware, IStatements {
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	boolean isEmpUserType = false;
	CommonFunctions CF = null;
	private static Logger log = Logger.getLogger(DesignationDetails.class);

	String strSessionEmpId = null;

	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;
		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);

		request.setAttribute(TITLE, "DesignationDetails");
		request.setAttribute(PAGE, "/jsp/recruitment/DesignationDetails.jsp");

		String strDesig_id = (String) request.getParameter("desig_id");
		getDesignationDetails(strDesig_id);

		return LOAD;

	}

	private void getDesignationDetails(String strDesig_id) {
		
		Connection con = null;
		PreparedStatement pst = null,pst1 = null;
		ResultSet rs = null,rs1 = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		Map<String, String> hmDesignationDetails = new HashMap<String, String>();
		try {

			con = db.makeConnection(con);
			StringBuilder sbGradeIds = new StringBuilder();
			
			pst = con.prepareStatement("select * from grades_details gd where gd.designation_id = ? order by gd.grade_id desc");
			pst.setInt(1, uF.parseToInt(strDesig_id));
			rs = pst.executeQuery();
		//	System.out.println("new Date ===> " + new Date());
			while (rs.next()) {
				if(sbGradeIds.toString().equals("")){
					sbGradeIds.append(rs.getString("grade_id"));
				} else {
					sbGradeIds.append(","+rs.getString("grade_id"));
				}
			}
			rs.close();
			pst.close();
			
			Map<String, String> hmGradeName = CF.getGradeMap(con);
			String gradesName = getAppendDataWithoutStartEndComma(sbGradeIds.toString(), hmGradeName, uF);
			pst = con.prepareStatement("select * from designation_details dd, level_details ld where ld.level_id = dd.level_id and dd.designation_id =?");
			pst.setInt(1, uF.parseToInt(strDesig_id));
			rs = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while (rs.next()) {
				hmDesignationDetails.put("LEVEL_NAME", rs.getString("level_name"));
				hmDesignationDetails.put("GRADE_NAME", uF.showData(gradesName, "Not Defined")); //rs.getString("grade_name")
				hmDesignationDetails.put("DESIG_NAME", uF.showData(rs.getString("designation_name"), "Not Defined"));
				hmDesignationDetails.put("DESIG_DESCRIPTION", uF.showData(rs.getString("designation_description"), "Not Defined"));

				hmDesignationDetails.put("JOB_DESCRIPTION", uF.showData(rs.getString("job_description"), "Not Defined"));
				hmDesignationDetails.put("JOB_PROFILE", uF.showData(rs.getString("profile"), "Not Defined"));
				hmDesignationDetails.put("IDEAL_CANDIDATE", uF.showData(rs.getString("ideal_candidate"), "Not Defined"));

				StringBuilder sbAttributes = new StringBuilder();
				if (rs.getString("attribute_ids") != null) {
					pst1 = con.prepareStatement("select * from desig_attribute where _type in (" + rs.getString("attribute_ids") + ") and desig_id = ?");
					pst1.setInt(1, uF.parseToInt(strDesig_id));
					rs1 = pst1.executeQuery();
					
					while (rs1.next()) {
						int _type = uF.parseToInt(rs1.getString("_type"));

						switch (_type) {
						case 1:
							sbAttributes.append("<b>Education: </b>");
							sbAttributes.append(rs1.getString("desig_value"));
							sbAttributes.append("<br/>");
							break;

						case 2:
							sbAttributes.append("<b>Total Experience: </b>");
							sbAttributes.append(rs1.getString("desig_value")+" years");
							sbAttributes.append("<br/>");
							break;
						case 3:
							sbAttributes.append("<b>Relevant Experience: </b>");
							sbAttributes.append(rs1.getString("desig_value")+" years");
							sbAttributes.append("<br/>");
							break;
						case 4:
							sbAttributes.append("<b>Experience with us: </b>");
							sbAttributes.append(rs1.getString("desig_value")+" years");
							sbAttributes.append("<br/>");
							break;
						case 5:
							sbAttributes.append("<b>Skill: </b>");
							sbAttributes.append(rs1.getString("desig_value"));
							sbAttributes.append("<br/>");
							break;
						case 6:
							sbAttributes.append("<b>Gender: </b>");
							sbAttributes.append(rs1.getString("desig_value"));
							sbAttributes.append("<br/>");
							break;
						}
					}
					
					rs1.close();
					pst1.close();
				}
				
				if(sbAttributes.length()>0){
					hmDesignationDetails.put("ATTRIBUTES", sbAttributes.toString());
				}else{
					hmDesignationDetails.put("ATTRIBUTES", "Not Defined");
				}
			}
			rs.close();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs1);
			db.closeStatements(pst1);
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		request.setAttribute("hmDesignationDetails", hmDesignationDetails);
	}

	
	public String getAppendDataWithoutStartEndComma(String strID, Map<String, String> hmGradeName, UtilityFunctions uF) {
		StringBuilder sb = new StringBuilder();
		
		if (strID != null && !strID.equals("")) {
			if (strID.contains(",")) {
				String[] temp = strID.split(",");
				for (int i = 0; i < temp.length; i++) {
					if (i == 0) {
						sb.append(hmGradeName.get(temp[i].trim()));
					} else {
						sb.append(", " + hmGradeName.get(temp[i].trim()));
					}
				}
			} else {
				return hmGradeName.get(strID);
			}
		} else {
			return null;
		}
		return sb.toString();
	}
	
	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		// TODO Auto-generated method stub
		this.request = request;

	}
}
