package com.konnect.jpms.daoimpl.performance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.konnect.jpms.bean.performance.AppraisalRemarkBean;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.UtilityFunctions;

public class AppraisalRemarkDaoImpl {


	public boolean insertComment(AppraisalRemarkBean bean,CommonFunctions CF, HttpServletRequest request) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();
 
		Database db = new Database();
		db.setRequest(request);
		boolean flag = false;
		try {
			con = db.makeConnection(con);    

			pst = con.prepareStatement("delete from appraisal_final_sattlement where emp_id=? and appraisal_id=?");
//			pst=con.prepareStatement(deleteFinalSattlement);
			pst.setInt(1, uF.parseToInt(bean.getEmpid()));
			pst.setInt(2, uF.parseToInt(bean.getId()));
			pst.execute(); 
			pst.close();
 
			pst = con.prepareStatement("insert into appraisal_final_sattlement(emp_id,appraisal_id,user_id,sattlement_comment," +
							"if_approved,_date,activity_ids)values(?,?,?,?,?,?,?)");
//			pst = con.prepareStatement(insertFinalSattlement);
			pst.setInt(1, uF.parseToInt(bean.getEmpid()));
			pst.setInt(2, uF.parseToInt(bean.getId()));
			pst.setInt(3, uF.parseToInt(bean.getStrSessionEmpId()));
			pst.setString(4, bean.getRemark());
			pst.setBoolean(5, uF.parseToBoolean(bean.getApprove()));
			pst.setDate(6, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setString(7, bean.getStrActivity());
			pst.execute();
			pst.close();

			flag = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

		if (flag == true) {
			checkAttributeThreshhold(bean,CF,request);
		}
		
		return flag;
	}

	private void checkAttributeThreshhold(AppraisalRemarkBean bean, CommonFunctions CF,HttpServletRequest request) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			Map<String, String> hmDesignation = CF.getEmpDesigMapId(con);
			Map<String, String> hmWlocationMap = CF.getEmpWlocationMap(con);
			Map<String, String> hmAttributeThreshhold = new HashMap<String, String>();
			pst = con
					.prepareStatement("select attribute_id,threshhold from appraisal_attribute_level");
//			pst=con.prepareStatement(selectAttribute);
			rs = pst.executeQuery(); 
			while (rs.next()) {
				hmAttributeThreshhold.put(rs.getString("attribute_id"),rs.getString("threshhold"));
			}
			rs.close();
			pst.close();

			double dblTotalMarks = 0;
			double dblTotalWeightage = 0;
			double dblTotalAggregate = 0;
			Map<String, String> hmScoreAggregateMap = new HashMap<String, String>();

			pst = con.prepareStatement("select sum(marks) as marks, sum(weightage) as weightage, aqw.appraisal_attribute from appraisal_question_answer aqw where aqw.appraisal_id=? and emp_id=? group by aqw.appraisal_attribute");
			pst.setInt(1, uF.parseToInt(bean.getId()));
			pst.setInt(2, uF.parseToInt(bean.getEmpid()));
			rs = pst.executeQuery();
			while (rs.next()) {

				dblTotalMarks = uF.parseToDouble(rs.getString("marks"));
				dblTotalWeightage = uF.parseToDouble(rs.getString("weightage"));

				dblTotalAggregate = uF.parseToDouble(uF.formatIntoTwoDecimal(((dblTotalMarks / dblTotalWeightage) * 100)));

				hmScoreAggregateMap.put(rs.getString("appraisal_attribute"),uF.showData("" + dblTotalAggregate, "0"));
			}
			rs.close();
			pst.close();

			if (!hmScoreAggregateMap.isEmpty()) {
				Iterator<String> it = hmScoreAggregateMap.keySet().iterator();
				while (it.hasNext()) {
					String key = it.next();
					double aggregate = uF.parseToDouble(hmScoreAggregateMap.get(key));

					if (aggregate < uF.parseToDouble(hmAttributeThreshhold.get(key))) {
						
						pst = con.prepareStatement("insert into training_gap_details(emp_id,designation_id,wlocation_id,attribute_id,appraisal_id,"
							+ "actual_score,required_score,training_completed_status,is_training_schedule,added_by,entry_date)" +
							"values(?,?,?,?, ?,?,?,?, ?,?,?)");
//						pst=con.prepareStatement(insertTrainingGap);
						pst.setInt(1,uF.parseToInt(bean.getEmpid()));
						pst.setInt(2,uF.parseToInt(hmDesignation.get(bean.getEmpid())));
						pst.setInt(3,uF.parseToInt(hmWlocationMap.get(bean.getEmpid())));
						pst.setInt(4, uF.parseToInt(key));
						pst.setInt(5, uF.parseToInt(bean.getId()));
						pst.setDouble(6, aggregate);
						pst.setDouble(7, uF.parseToDouble(hmAttributeThreshhold.get(key)));
						pst.setBoolean(8, false);
						pst.setBoolean(9, false);
						pst.setInt(10, uF.parseToInt(bean.getStrSessionEmpId()));
						pst.setDate(11, uF.getCurrentDate(CF.getStrTimeZone()));
						pst.execute();
						pst.close();
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}

	

}
