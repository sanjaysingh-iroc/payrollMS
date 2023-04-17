package com.konnect.jpms.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ReimbursementCTCBackDatedData implements IStatements{

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ReimbursementCTCBackDatedData backDatedData = new ReimbursementCTCBackDatedData();
		backDatedData.updateBackDatedData();
		
	}

	private void updateBackDatedData() {
		
		try {
			UtilityFunctions uF = new UtilityFunctions();
			PreparedStatement pst = null,pst1=null;
			ResultSet rs = null;
			Connection conn = null;
			
			String HOST 		= "192.168.1.6";
			String PORT 		= "5432"; 
			String DBUSERNAME	= "postgres";
			String DBPASSWORD 	= "postgres"; 
			String []arrDBNAME = {"xanadu_payroll2"};
			
//			String HOST 		= "52.204.34.153";
//			String PORT 		= "5432"; 
//			String DBUSERNAME	= "postgres";
//			String DBPASSWORD 	= "dbPassword"; 
//			String []arrDBNAME = {"xanadu_payroll2"};	
			 
			 
			for(int i=0; i<arrDBNAME.length; i++) {
				
				try {
				
					Class.forName("org.postgresql.Driver");
					conn = DriverManager.getConnection("jdbc:postgresql://" + HOST + ":"+PORT+"/" + arrDBNAME[i], DBUSERNAME, DBPASSWORD);
					
					System.out.println("======== EXECUTION STARTED ON "+arrDBNAME[i]);
					
					
					try {
						pst = conn.prepareStatement("select * from reimbursement_assign_head_details where paycycle=15 order by emp_id");
						rs = pst.executeQuery();
						while(rs.next()){
							
							pst1 = conn.prepareStatement("insert into reimbursement_assign_head_details (emp_id,reimbursement_head_id," +
									"reimbursement_ctc_id,level_id,org_id,amount,financial_year_start,financial_year_end,status,trail_status," +
									"update_by,update_date,paycycle_from,paycycle_to,paycycle) values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?)");
							pst1.setInt(1, rs.getInt("emp_id"));
							pst1.setInt(2, rs.getInt("reimbursement_head_id")); 
							pst1.setInt(3, rs.getInt("reimbursement_ctc_id"));
							pst1.setInt(4, rs.getInt("level_id"));
							pst1.setInt(5, rs.getInt("org_id"));
							pst1.setDouble(6, rs.getDouble("amount"));
							pst1.setDate(7, uF.getDateFormat("01/04/2017", DATE_FORMAT));
							pst1.setDate(8, uF.getDateFormat("31/03/2018", DATE_FORMAT));
							pst1.setBoolean(9, rs.getBoolean("status"));
							pst1.setBoolean(10, true);
							pst1.setInt(11, 1);
							pst1.setDate(12, uF.getDateFormat("01/05/2017", DATE_FORMAT));
							pst1.setDate(13, uF.getDateFormat("01/05/2017", DATE_FORMAT));
							pst1.setDate(14, uF.getDateFormat("31/05/2017", DATE_FORMAT));
							pst1.setInt(15, 14);
//							System.out.println("pst====>"+pst);
							pst1.execute();
						}
						rs.close();
						pst.close();
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						if(rs!=null){
							rs.close();
							rs = null;
						}
						if(pst!=null){
							pst.close();
							pst = null;
						}
						if(pst1!=null){
							pst1.close();
							pst1 = null;
						}
					}
					
					
					System.out.println("========EXECUTION FINISHED ON "+arrDBNAME[i]);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						if(conn != null){
							conn.close();
							conn = null;
						}
					} catch (Exception e2) {
						e2.printStackTrace();
					}
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	

}
