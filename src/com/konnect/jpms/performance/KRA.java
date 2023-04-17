package com.konnect.jpms.performance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;

public class KRA implements ServletRequestAware, IStatements {

	HttpSession session;
	String strSessionEmpId;
	String strSessionUserType;
	String strUserTypeId;
	CommonFunctions CF;

	private String empid;
	private String id;

	public String execute() {
		session = request.getSession();
		strSessionEmpId = (String) session.getAttribute(EMPID);
		strSessionUserType = (String) session.getAttribute(USERTYPE);
		strUserTypeId = (String) session.getAttribute(USERTYPEID);

		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return "login";

		request.setAttribute(PAGE, "/jsp/performance/KRA.jsp");
		request.setAttribute(TITLE, "KRA");

		getKRADetails();

		String submit = request.getParameter("submit");
		if (submit != null && submit.equals("Submit")) {
			insertKRARating();
		}
		getKRARatingDetails();
		return "success";
	}
	
	private void getKRARatingDetails() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			con = db.makeConnection(con);

			pst = con.prepareStatement("select * from kra_rating_details order by kra_rating_id");
			rs = pst.executeQuery();
			Map<String, String> hmKRARating = new LinkedHashMap<String, String>();
			while (rs.next()) {
				String kraValue = rs.getString("kra_rating_id") + ":_:"+ rs.getString("rating");
				
				hmKRARating.put(rs.getString("goal_id") + rs.getString("goal_kra_id") + rs.getString("emp_id")+ rs.getString("appraisal_id") 
						+ rs.getString("added_by")+ rs.getString("user_type_id"), kraValue);
			}
			rs.close();
			pst.close();

			request.setAttribute("hmKRARating", hmKRARating);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private void insertKRARating() {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uf = new UtilityFunctions();

		String[] goalid = request.getParameterValues("goalid");
		String[] goalkraid = request.getParameterValues("goalkraid");
		String[] weightage = request.getParameterValues("weightage");
		String[] usertype = request.getParameterValues("usertype"); 
		String[] kra_rating_id = request.getParameterValues("kra_rating_id");
		
		try {
			con = db.makeConnection(con);

//			System.out.println("getId===>"+getId());
//			System.out.println("getEmpid===>"+getEmpid());
			con.setAutoCommit(false);
			if (usertype != null && !usertype.equals("")) {
				

				for (int i = 0; i < usertype.length; i++) { 
					
					if(kra_rating_id[i]!=null && !kra_rating_id[i].equals("")){
						  pst = con.prepareStatement("delete from kra_rating_details where kra_rating_id=?");
						  pst.setInt(1, uf.parseToInt(kra_rating_id[i]));
						  pst.execute(); 
						  pst.close();
					  } 
					
					
					  //goal_kra_id,goal_id,appraisal_id,emp_id,user_type_id,added_by,rating,weightage,entry_date,
//					System.out.println("usertype[i]==== "+usertype[i]+" ==goalkraid[i]==== "+goalkraid[i]);
					String rating=request.getParameter("gradewithrating"+usertype[i]+"_"+goalkraid[i]);
//					System.out.println("rating[i]==== ============> "+rating);
					pst = con
							.prepareStatement("insert into kra_rating_details(goal_kra_id,goal_id,appraisal_id,emp_id,user_type_id,added_by," +
									"rating,weightage,entry_date)values(?,?,?,?,?,?,?,?,?)");
					pst.setInt(1, uf.parseToInt(goalkraid[i]));
					pst.setInt(2, uf.parseToInt(goalid[i]));
					pst.setInt(3, uf.parseToInt(getId()));
					pst.setInt(4, uf.parseToInt(getEmpid()));
					pst.setInt(5, uf.parseToInt(usertype[i]));
					pst.setInt(6, uf.parseToInt(strSessionEmpId));
					double marks=uf.parseToDouble(rating) * uf.parseToDouble(weightage[i]) / 5;
//					System.out.println("marks===== "+marks+" ==rating[i]==== "+rating+" ==weightage[i]==== "+weightage[i]);
					pst.setDouble(7,marks);
					pst.setDouble(8,uf.parseToDouble(weightage[i]));
					pst.setDate(9,uf.getCurrentDate(CF.getStrTimeZone()) );					
					pst.execute();
					pst.close();
				}
				con.commit();
			}

		} catch (Exception e) {
			try {
				con.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private void getKRADetails() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uf = new UtilityFunctions();

				Map<String, List<List<String>>> hmKRA = new LinkedHashMap<String, List<List<String>>>();

		try {
			con = db.makeConnection(con);
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			request.setAttribute("hmEmpName", hmEmpName);
			
			Map<String, String> hmGoalOrientation = new HashMap<String, String>();
			Map<String, String> hmMesures = new HashMap<String, String>();
			Map<String, String> hmMesuresType = new HashMap<String, String>();
			Map<String, String> hmGoalTitle = new HashMap<String, String>();
//			Map<String, String> orientationMp = getOrientationValue(con);
//			Map<String, String> orientationMemberMp = getOrientationMember(con);

			pst = con
					.prepareStatement("select * from appraisal_details where appraisal_details_id =?");
			pst.setInt(1, uf.parseToInt(getId()));
			rs = pst.executeQuery();

			Map<String, String> appraisalMp = new HashMap<String, String>();
			int memberCount = 0;
			List<String> memberList = new ArrayList<String>();
			while (rs.next()) {
				memberList = Arrays.asList(rs.getString("usertype_member")
						.split(","));
			}
			rs.close();
			pst.close();
			
			request.setAttribute("memberList", memberList);

			if (getEmpid() != null && !getEmpid().equals("")) {

				StringBuilder sb = new StringBuilder();
				sb.append("select * from goal_kras k join goal_details g on k.goal_id=g.goal_id and g.emp_ids like '%,"
						+ getEmpid()+ ",%' and g.goal_type=4 and (measure_type='' or measure_type is null) order by k.goal_id");

				pst = con.prepareStatement(sb.toString());
//				System.out.println("pst==== == >"+pst);
				rs = pst.executeQuery();

				while (rs.next()) {
					List<List<String>> outerList = hmKRA.get(rs.getString("goal_id"));
					if (outerList == null) outerList = new ArrayList<List<String>>();
					List<String> innerList = new ArrayList<String>();
					innerList.add(rs.getString("goal_kra_id"));
					innerList.add(rs.getString("goal_id"));
					innerList.add(rs.getString("entry_date"));
					innerList.add(rs.getString("effective_date"));
					innerList.add(rs.getString("is_approved"));
					innerList.add(rs.getString("approved_by"));
					innerList.add(rs.getString("kra_order"));
					innerList.add(rs.getString("kra_description"));
					innerList.add(rs.getString("goal_type"));
					innerList.add(rs.getString("weightage"));

					hmGoalOrientation.put(rs.getString("goal_id"),
							rs.getString("orientation_id"));

					hmGoalTitle.put(rs.getString("goal_id"),
							rs.getString("goal_title"));

					String measures = "";
					if (rs.getString("measure_type").equals("Amount")) {
						measures = rs.getString("measure_currency_value");
					} else if (rs.getString("measure_type").equals("Effort")) {
						measures = rs.getString("measure_effort_days")
								+ " Days and "
								+ rs.getString("measure_effort_hrs") + " Hrs.";
					}
					hmMesures.put(rs.getString("goal_id"), measures);
					hmMesuresType.put(rs.getString("goal_id"),
							rs.getString("measure_type"));

					outerList.add(innerList);
					hmKRA.put(rs.getString("goal_id"), outerList);
				}
				rs.close();
				pst.close();
			}
			request.setAttribute("hmKRA", hmKRA);
//			System.out.println("hmKRA======>"+hmKRA);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

//	private Map<String, String> getOrientationMember(Connection con) {
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//
//		Map<String, String> orientationMemberMp = new HashMap<String, String>();
//		try {
//			pst = con
//					.prepareStatement("select * from orientation_member where status=true order by weightage");
//			rs = pst.executeQuery();
//			while (rs.next()) {
//				orientationMemberMp.put(rs.getString("orientation_member_id"),
//						rs.getString("member_name"));
//			}
//
//			request.setAttribute("orientationMemberMp", orientationMemberMp);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return orientationMemberMp;
//	}

//	private Map<String, String> getOrientationValue(Connection con) {
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//
//		Map<String, String> orientationMp = new HashMap<String, String>();
//		try {
//
//			pst = con.prepareStatement("select * from apparisal_orientation");
//			rs = pst.executeQuery();
//			while (rs.next()) {
//				orientationMp.put(rs.getString("apparisal_orientation_id"),
//						rs.getString("orientation_name"));
//			}
//
//			request.setAttribute("orientationMp", orientationMp);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return orientationMp;
//	}

	public String getEmpid() {
		return empid;
	}

	public void setEmpid(String empid) {
		this.empid = empid;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {

		this.request = request;
	}
}
