package com.konnect.jpms.recruitment;

import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;

public class DownloadXML implements ServletRequestAware, ServletResponseAware,
		IStatements {

	HttpSession session;
	private HttpServletRequest request;
	private CommonFunctions CF;

	String id;

	HttpServletResponse response;

	public void execute() throws Exception {

		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		// if (CF == null)
		// return "login";

		downloadXML();

	}

	private void downloadXML() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		Map<String, Map<String, String>> JobProfile_details = new LinkedHashMap<String, Map<String, String>>();

		try {

			con = db.makeConnection(con);
			StringBuilder strQuery = new StringBuilder();
			strQuery.append("select r.job_code,r.no_position,l.level_code,l.level_name,d.designation_name,g.grade_code,g.grade_name,"
					+ "s.service_name,r.skills,w.wlocation_name,r.recruitment_id,r.candidate_profile from recruitment_details r,grades_details g,work_location_info w,"
					+ "designation_details d,services s,department_info di,employee_personal_details e,level_details l where r.grade_id=g.grade_id "
					+ "and r.wlocation=w.wlocation_id and r.designation_id=d.designation_id and r.services=s.service_id and r.dept_id=di.dept_id "
					+ "and r.added_by=e.emp_per_id  and r.level_id=l.level_id and r.recruitment_id in("
					+ getId() + ")");
			pst = con.prepareStatement(strQuery.toString());
			rs = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while (rs.next()) {
				Map<String, String> InnerJobProfile_details = new LinkedHashMap<String, String>();
				InnerJobProfile_details.put("jobcode", rs.getString(1));
				InnerJobProfile_details.put("positions", rs.getString(2));
				InnerJobProfile_details.put("level", "[" + rs.getString(3)
						+ "] " + rs.getString(4));
				InnerJobProfile_details.put("designation", rs.getString(5));
				InnerJobProfile_details.put("grade", "[" + rs.getString(6)
						+ "] " + rs.getString(7));
				InnerJobProfile_details.put("services", rs.getString(8));
				InnerJobProfile_details.put("skills", rs.getString(9));
				InnerJobProfile_details.put("location", rs.getString(10));
				InnerJobProfile_details.put("profile", rs.getString(12));
				
				JobProfile_details.put(rs.getString(11),
						InnerJobProfile_details);
			}
			rs.close();
			pst.close();
			
			createXML(JobProfile_details);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}

	private void createXML(Map<String, Map<String, String>> jobProfile_details) {
		try {

			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory
					.newDocumentBuilder();
			Document document = documentBuilder.newDocument();

			Element orderElement = document.createElement("job");
			document.appendChild(orderElement);

			Set<String> set = jobProfile_details.keySet();
			Iterator<String> iterator = set.iterator();

			while (iterator.hasNext()) {

				Element orderDetailElement = document.createElement("jobid");
				orderElement.appendChild(orderDetailElement);
				
				String key = iterator.next();

				Map<String, String> InnerJobProfile_details = jobProfile_details
						.get(key);

				Set<String> set1 = InnerJobProfile_details.keySet();
				Iterator<String> iterator1 = set1.iterator();

				while (iterator1.hasNext()) {

					String key1 = (String) iterator1.next();
					Element detailElement1 = document.createElement(key1);
					detailElement1.appendChild(document
							.createTextNode((String) InnerJobProfile_details
									.get(key1)));
					orderDetailElement.appendChild(detailElement1);

				}				
			}

			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			TransformerFactory transformerFactory = TransformerFactory
					.newInstance();
			Transformer transformer = transformerFactory.newTransformer();

			DOMSource source = new DOMSource(document);
			StreamResult result = new StreamResult(buffer);
			transformer.transform(source, result);

			response.setContentType("application/xml");
			response.setContentLength(buffer.size());
			response.setHeader("Content-Disposition",
					"attachment; filename=JobProfile.xml");
			ServletOutputStream out = response.getOutputStream();
			buffer.writeTo(out);
			out.flush();
			out.close();

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
	}

}
