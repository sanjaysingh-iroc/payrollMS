package com.konnect.jpms.select;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;

public class FillDishes implements IStatements {

	private String dishId;
	private String dishName;
	
	public FillDishes() {
	}
	
	public FillDishes(String dishId, String dishName) {
		this.dishId = dishId;
		this.dishName = dishName;
	}

	HttpServletRequest request;
	public FillDishes(HttpServletRequest request) {
		this.request = request;
	}
	
	public List<FillDishes> fillDishes(String wlocation,String dishType, String startDate, String endDate,String strUserType,CommonFunctions CF){
		
		List<FillDishes> al = new ArrayList<FillDishes>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs1 = null;
		Database db = new Database();
		db.setRequest(request);
		try {

			con = db.makeConnection(con);
			Map<String, String> hmWLocation = CF.getWLocationMap(con,null,request,null); 
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from dish_details where dish_id >0 ");
			if(wlocation != null && !wlocation.equals("")) {
				sbQuery.append("  and wlocation_id in ("+wlocation+")");
			}
			
			/*if(strOrg != null && !strOrg.equals("")) {
				sbQuery.append("  and org_id in ("+strOrg+")");
			}*/
			
			if(dishType != null && !dishType.equals("")) {
				sbQuery.append("  and dish_type in ('"+dishType+"')");
			}
			
			if(startDate != null && !startDate.equals("") && endDate != null && !endDate.equals("")) {
				//sbQuery.append("  and (( dish_from_date >= '"+startDate+"' and dish_from_date <= '"+endDate+"' ) or ( dish_to_date >= '"+startDate+"' and dish_to_date <= '"+endDate+"'))");
				sbQuery.append("  and ( dish_from_date <= '"+startDate+"' and dish_to_date >= '"+endDate+"')");
			}
			
		
			sbQuery.append(" order by dish_name");
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst==>"+pst);
			rs1 = pst.executeQuery();
			while (rs1.next()) {
				if(strUserType != null && (strUserType.equals(ADMIN) || strUserType.equals(HRMANAGER) )) {
					al.add(new FillDishes(rs1.getString("dish_id"), rs1.getString("dish_name")+"["+hmWLocation.get(rs1.getString("wlocation_id"))+"]"));
				} else {
					al.add(new FillDishes(rs1.getString("dish_id"), rs1.getString("dish_name")));
				}
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
	public String getDishId() {
		return dishId;
	}

	public void setDishId(String dishId) {
		this.dishId = dishId;
	}

	public String getDishName() {
		return dishName;
	}

	public void setDishName(String dishName) {
		this.dishName = dishName;
	}
}
