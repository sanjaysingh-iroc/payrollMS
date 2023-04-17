package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.konnect.jpms.util.Database;

public class FillTaskDates {
String taskDateEmpId;
String taskDate;
public FillTaskDates(String taskDateEmpId, String taskDate) {
	this.taskDateEmpId = taskDateEmpId;
	this.taskDate = taskDate;
}

HttpServletRequest request;
public FillTaskDates(HttpServletRequest request) {
	this.request = request;
}

public FillTaskDates() {
	
}


public List<FillTaskDates> fillTaskDate(int emp_id){
	
	List<FillTaskDates> al = new ArrayList<FillTaskDates>();
	Connection con = null;
	PreparedStatement pst = null;
	ResultSet rsEmpID = null;
	Database db = new Database();
	db.setRequest(request);
	try {
		
		con = db.makeConnection(con);
		pst = con.prepareStatement("select DISTINCT ON (task_date) task_date,emp_id  from task_activity where emp_id=? and sent!='n' ORDER BY task_date DESC");
		pst.setInt(1,emp_id);
		rsEmpID = pst.executeQuery();
		while(rsEmpID.next()){
			al.add(new FillTaskDates(rsEmpID.getString("emp_id"), rsEmpID.getString("task_date")));				
		}
		rsEmpID.close();
		pst.close();	
	} catch (Exception e) {
		e.printStackTrace();
	}finally{
		db.closeResultSet(rsEmpID);
		db.closeStatements(pst);
		db.closeConnection(con);
	}
	
	return al;
}

public String getTaskDate() {
	return taskDate;
}
public void setTaskDate(String taskDate) {
	this.taskDate = taskDate;
}


public String getTaskDateEmpId() {
	return taskDateEmpId;
}


public void setTaskDateEmpId(String taskDateEmpId) {
	this.taskDateEmpId = taskDateEmpId;
}



}
