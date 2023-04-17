package com.konnect.jpms.performance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;

public class FillOrientation implements IStatements{

	private String id;
	private String name;
	
	private FillOrientation(String levelId, String levelCodeName) {
		this.id = levelId;
		this.name = levelCodeName;
	}
	
	HttpServletRequest request;
	public FillOrientation(HttpServletRequest request) {
		this.request = request; 
	}
	
//	public FillOrientation() {
//	}
	
	public List<FillOrientation> fillOrientation(){
		
		List<FillOrientation> al = new ArrayList<FillOrientation>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			Map<String,String> memberMp=getOrientationValue(con);
//			System.out.println("memberMp==>"+memberMp);
			pst = con.prepareStatement("select * from apparisal_orientation");
			rs = pst.executeQuery();
			while(rs.next()){
				al.add(new FillOrientation(rs.getString("apparisal_orientation_id"), rs.getString("orientation_name")+"["+memberMp.get(rs.getString("apparisal_orientation_id"))+"]"));
				
				/*if(rs.getString("apparisal_orientation_id").equals("5")) {
					al.add(new FillOrientation(rs.getString("apparisal_orientation_id"), rs.getString("orientation_name")+"[Anyone]"));
				} else {
					al.add(new FillOrientation(rs.getString("apparisal_orientation_id"), rs.getString("orientation_name")+"["+memberMp.get(rs.getString("apparisal_orientation_id"))+"]"));
				}*/
			}	
			rs.close();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return al;
	}
	
	
	
	private Map<String,String> getOrientationValue(Connection con) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String,String> memberMp=new HashMap<String,String>();

		try {
			
			pst = con.prepareStatement("select member_name,orientation_id from orientation_details od,orientation_member om  where  od.member_id=om.member_id order by orientation_id,orientation_details_id");
			
			rs=pst.executeQuery();
			while(rs.next()) {
				String member=memberMp.get(rs.getString("orientation_id"));
				if(member==null)member=rs.getString("member_name");
				else
					member+=","+rs.getString("member_name");
					memberMp.put(rs.getString("orientation_id"),member);
			}
			rs.close();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return memberMp;
		}


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	

}  
