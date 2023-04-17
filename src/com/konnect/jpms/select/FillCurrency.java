package com.konnect.jpms.select;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;

public class FillCurrency implements IStatements{

	String currencyId;
	String currencyName;
	     
	private FillCurrency(String currencyId, String currencyName) {
		this.currencyId = currencyId;
		this.currencyName = currencyName;
	}
	
	HttpServletRequest request;
	public FillCurrency(HttpServletRequest request) {
		this.request = request;
	}
	public FillCurrency() {
	}
	
	final static public String selectCurrency = "SELECT * FROM currency_details";
	public List<FillCurrency> fillCurrency(){
		
		List<FillCurrency> al = new ArrayList<FillCurrency>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			
			con = db.makeConnection(con);
			pst = con.prepareStatement(selectCurrency);
			rs = pst.executeQuery();
			while(rs.next()){
				al.add(new FillCurrency(rs.getString("currency_id"), rs.getString("currency_desc")));				
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

	public String getCurrencyId() {
		return currencyId;
	}

	public void setCurrencyId(String currencyId) {
		this.currencyId = currencyId;
	}

	public String getCurrencyName() {
		return currencyName;
	}

	public void setCurrencyName(String currencyName) {
		this.currencyName = currencyName;
	}
	
}  
