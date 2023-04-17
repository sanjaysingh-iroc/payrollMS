package com.konnect.jpms.test;
import java.util.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Iterator;

import com.konnect.jpms.util.Database;
import com.sun.org.apache.bcel.internal.generic.NEWARRAY;

public class CandiTest {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		CandiTest ct = new CandiTest();
		
		ct.getEmployeeList();
	}

	private void getEmployeeList() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
//		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			System.out.println("Hello");
			pst=con.prepareStatement("select concat(epd.emp_fname,epd.emp_lname) as EmployeeName,ld.level_name as LevelName ,dd.designation_name as DesignationName,\n" + 
					" gd.grade_name as GradeName,ud.username as UserName,ud.password as password \n" + 
					" from \n" + 
					" employee_personal_details epd ,employee_official_details eod ,level_details ld ,designation_details dd,grades_details gd ,\n" + 
					" user_details ud \n" + 
					" where epd.emp_per_id =eod.emp_id and ld.level_id =dd.level_id and dd.designation_id =gd.designation_id \n" + 
					" and gd.grade_id =eod.grade_id and ud.emp_id =eod.emp_id order by dd.level_id,gd.designation_id,eod.grade_id");
			rs = pst.executeQuery();
			
//			System.out.println("Level\t"+"|"+"Designation\t"+"|"+"Grade\t"+" | "+"Employeename");
			Stack<String> levelNameStack=new Stack<String>();
			Stack<String> designationNameStack=new Stack<String>();
			Stack<String> gradeNameStack=new Stack<String>();
			int i=0;
			boolean isNewDesignation=false;
			while(rs.next()) {
			//System.out.println(rs.getString("levelname")+"|"+rs.getString("designationname")+"|"+rs.getString("gradename")+"|"+rs.getString("employeename"));
			if(i==0) {
				levelNameStack.push(rs.getString("levelname"));
				designationNameStack.push(rs.getString("designationname"));
				gradeNameStack.push(rs.getString("gradename"));
				System.out.println(rs.getString("levelname")+"\t|"+rs.getString("designationname")+"|"+rs.getString("gradename")+"\t|"+rs.getString("employeename"));

			}else {
				if(levelNameStack.peek().equals(rs.getString("levelname"))) {
					System.out.print("\t"+"|");
				}else {
					levelNameStack.pop();
					levelNameStack.push(rs.getString("levelname"));	
					System.out.print(rs.getString("levelname")+"\t|");
				}
				if(designationNameStack.peek().equals(rs.getString("designationname"))) {
					System.out.print("\t"+"|");
					isNewDesignation=false;
				}else {
					designationNameStack.pop();
					designationNameStack.push(rs.getString("designationname"));
					System.out.print(rs.getString("designationname")+"|");
					 isNewDesignation=true;
				}
				if(gradeNameStack.peek().equals(rs.getString("gradename")) && !isNewDesignation) {
					System.out.print("\t"+"|");
				}else {
					gradeNameStack.pop();
					gradeNameStack.push(rs.getString("gradename"));
					System.out.print(rs.getString("gradename")+"\t|");
				}
				System.out.print(rs.getString("employeename")+"\n");
				
			}
			
			i++;
			}
			rs.close();
			pst.close();
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}
}
