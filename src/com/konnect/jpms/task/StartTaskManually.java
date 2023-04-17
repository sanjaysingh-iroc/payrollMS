	package com.konnect.jpms.task;

	import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;
	import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

	import org.apache.struts2.interceptor.ServletRequestAware;
	import org.apache.struts2.interceptor.ServletResponseAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

	public class StartTaskManually  extends ActionSupport implements ServletRequestAware, ServletResponseAware, IStatements {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		HttpSession session;
		CommonFunctions CF;
		
		String id;
		int pro_id; 
		 
		private String pro_deadline;
		private String pro_startDate;
		private String proBillingType;
		public String execute() {
			session = request.getSession();
			CF = (CommonFunctions) session.getAttribute(CommonFunctions);
			if (CF == null)
				return LOGIN;
			getProjectsDate();
//			System.out.println("pro_id on "+pro_id);
			return SUCCESS;
		}
		
		
		public void getProjectsDate() {

			Connection con = null;
			PreparedStatement pst = null;
			ResultSet rs = null;
			Database db = new Database();
			db.setRequest(request);
			
			UtilityFunctions uF = new UtilityFunctions();
			try {
				con = db.makeConnection(con);
				pst = con.prepareStatement("select deadline,start_date,actual_calculation_type from projectmntnc where pro_id=?");
				pst.setInt(1, getPro_id());
				rs = pst.executeQuery();
				while (rs.next()) {
					pro_deadline = uF.getDateFormat(rs.getString("deadline"), DBDATE, DATE_FORMAT);
					pro_startDate = uF.getDateFormat(rs.getString("start_date"), DBDATE, DATE_FORMAT);
					proBillingType = rs.getString("actual_calculation_type");
				}
//				System.out.println("pro_startDate ==>> " + pro_startDate +"  pro_deadline ===>> " + pro_deadline);
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
		
		public String getId() {
			return id;
		}
		public int getPro_id() {
			return pro_id;
		}
		public void setPro_id(int pro_id) {
			this.pro_id = pro_id;
		}
		public void setId(String id) {
			this.id = id;
		}

		public String getPro_deadline() {
			return pro_deadline;
		}

		public void setPro_deadline(String pro_deadline) {
			this.pro_deadline = pro_deadline;
		}

		public String getPro_startDate() {
			return pro_startDate;
		}

		public void setPro_startDate(String pro_startDate) {
			this.pro_startDate = pro_startDate;
		}

		public String getProBillingType() {
			return proBillingType;
		}

		public void setProBillingType(String proBillingType) {
			this.proBillingType = proBillingType;
		}

		private HttpServletRequest request;
		
		@Override
		public void setServletRequest(HttpServletRequest request) {
			this.request = request;
		}

		HttpServletResponse response;
		@Override
		public void setServletResponse(HttpServletResponse response) {
			this.response = response;
		}

	}
