package com.konnect.jpms.test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.UtilityFunctions;

	
	public class AESEncryption {
		
		HttpServletRequest request;
		public void getAndInsertSalaryHeadsOfAllGreades() {
			
			Connection con = null;
			PreparedStatement pst = null;
			ResultSet rs = null;
			Database db = new Database();
			db.setRequest(request);
			UtilityFunctions uF = new UtilityFunctions();
			try {
				con = db.makeConnection(con);
				
				StringBuilder sbQuery2 = new StringBuilder();
				sbQuery2.append("select * from salary_details where grade_id = 26 and salary_head_id > 1"); //grade_id
				pst = con.prepareStatement(sbQuery2.toString());
//				System.out.println("pst ===>> " + pst);
				List<List<String>> salHeadList = new ArrayList<List<String>>();
				rs = pst.executeQuery();
				while(rs.next()) {
					List<String> alList = new ArrayList<String>();
					alList.add(rs.getString("salary_head_name"));
					alList.add(rs.getString("earning_deduction"));
					alList.add(rs.getString("salary_head_amount_type"));
					alList.add(rs.getString("sub_salary_head_id"));
					alList.add(rs.getString("salary_head_amount"));
					alList.add(rs.getString("weight"));
					alList.add(rs.getString("salary_head_id"));
					alList.add(rs.getString("org_id"));
					alList.add(rs.getString("is_variable"));
					alList.add(rs.getString("salary_type"));
					alList.add(rs.getString("is_ctc_variable"));
					alList.add(rs.getString("is_tax_exemption"));
					alList.add(rs.getString("added_by"));
					alList.add(rs.getString("is_incentive"));
					alList.add(rs.getString("is_allowance"));
					alList.add(rs.getString("multiple_calculation"));
					salHeadList.add(alList);
				}
				rs.close();
				pst.close();
				
				List<String> gradesList = new ArrayList<String>();
				pst = con.prepareStatement("select grade_id from grades_details where grade_id > 72");
				rs = pst.executeQuery();
				while (rs.next()) {
					gradesList.add(rs.getString("grade_id"));
				}
				rs.close();
				pst.close();
				
				System.out.println("gradesList ====>>> " + gradesList);
				for(int i=0; gradesList != null && i<gradesList.size(); i++) {
					for(int j=0; salHeadList != null && j<salHeadList.size(); j++) {
						List<String> alInner = salHeadList.get(j);
						pst = con.prepareStatement("insert into salary_details (salary_head_name, earning_deduction, salary_head_amount_type," +
							" sub_salary_head_id, salary_head_amount, weight, salary_head_id, org_id, is_variable, salary_type, is_ctc_variable," +
							" is_tax_exemption, added_by, is_incentive, is_allowance, multiple_calculation, grade_id) values (?,?,?,?, ?,?,?,?, " +
							"?,?,?,?, ?,?,?,?, ?)");
						pst.setString(1, alInner.get(0));
						pst.setString(2, alInner.get(1));
						pst.setString(3, alInner.get(2));
						pst.setInt(4, uF.parseToInt(alInner.get(3)));
						pst.setDouble(5, uF.parseToDouble(alInner.get(4)));
						pst.setInt(6, uF.parseToInt(alInner.get(5)));
						pst.setInt(7, uF.parseToInt(alInner.get(6)));
						pst.setInt(8, uF.parseToInt(alInner.get(7)));
						pst.setBoolean(9, uF.parseToBoolean(alInner.get(8)));
						pst.setString(10, alInner.get(9));
						pst.setBoolean(11, uF.parseToBoolean(alInner.get(10)));
						pst.setBoolean(12, uF.parseToBoolean(alInner.get(11)));
						pst.setInt(13, uF.parseToInt(alInner.get(12)));
						pst.setBoolean(14, uF.parseToBoolean(alInner.get(13)));
						pst.setBoolean(15, uF.parseToBoolean(alInner.get(14)));
						pst.setString(16, alInner.get(15));
						pst.setInt(17, uF.parseToInt(gradesList.get(i)));
						pst.executeUpdate();
					}
					System.out.println("grade Id ===>> " + gradesList.get(i));
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				db.closeResultSet(rs);
				db.closeStatements(pst);
				db.closeConnection(con);
			}
		}
		
	    public static void main(String[] args) throws Exception {
	    	AESEncryption aes = new AESEncryption();
	    	aes.getAndInsertSalaryHeadsOfAllGreades();
	    }

	   
	}
