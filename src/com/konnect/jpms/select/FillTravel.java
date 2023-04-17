package com.konnect.jpms.select;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;

public class FillTravel implements IStatements{
	String leaveId;
	String planName;

	private FillTravel(String leaveId, String planName) {
		this.leaveId = leaveId;
		this.planName = planName;
	}
	HttpServletRequest request;
	public FillTravel(HttpServletRequest request) {
		this.request = request;
	}
	public FillTravel() {
	}
	
	public List<FillTravel> fillTravelPlan(int nEmpId){
		List<FillTravel> al = new ArrayList<FillTravel>();
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs1 = null;
		Database db = new Database();
		db.setRequest(request);
		try {

			con = db.makeConnection(con);
			
			//pst = con.prepareStatement("select * from emp_leave_entry where emp_id =? and istravel = true and leave_id not in (select travel_id from travel_advance where settlement_status = true) ");
			pst = con.prepareStatement("select * from emp_leave_entry where emp_id =? and istravel = true and is_approved=1");
			pst.setInt(1, nEmpId);
			rs1 = pst.executeQuery();
			while (rs1.next()) {
				al.add(new FillTravel(rs1.getString("leave_id"), rs1.getString("plan_name")));
			}	
			rs1.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs1);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	
		return al;
	}
	
	public String getLeaveId() {
		return leaveId;
	}

	public void setLeaveId(String leaveId) {
		this.leaveId = leaveId;
	}

	public String getPlanName() {
		return planName;
	}

	public void setPlanName(String planName) {
		this.planName = planName;
	}

	
	
}
