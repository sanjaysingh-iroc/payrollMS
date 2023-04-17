package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.konnect.jpms.select.FillClientPoc;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class GetClientPocAjax extends ActionSupport implements IStatements, ServletRequestAware,ServletResponseAware {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	List<FillClientPoc> clientPocList;
	List<FillClientBrand> clientBrandList;
	String clientId;
	String strClientBrand;
	String clientPoc;
	boolean brandFlag;
	
	public String execute() {
		
//		System.out.println("getClientId() ====>>> " + getClientId());
//		System.out.println("getStrClientBrand() ====>>> " + getStrClientBrand());
		UtilityFunctions uF = new UtilityFunctions();
		if(uF.parseToInt(getClientId())>0) {
			
			boolean flag = checkClientBrandAvailability(getClientId());
			setBrandFlag(flag);
//			System.out.println("flag ===>> " + flag);
			if(flag && uF.parseToInt(getStrClientBrand())>0) {
				clientPocList = new FillClientPoc(request).fillClientBrandPoc(getStrClientBrand());
			} else if(flag && uF.parseToInt(getStrClientBrand())==0) {
				clientPocList = new ArrayList<FillClientPoc>();
			} else {
				clientPocList = new FillClientPoc(request).fillClientPoc(getClientId());
			}
			clientBrandList = new FillClientBrand(request).fillClientBrands(uF.parseToInt(getClientId()));
		} else if(uF.parseToInt(getStrClientBrand())>0) {
			clientPocList = new FillClientPoc(request).fillClientBrandPoc(getStrClientBrand());
		} else {
			clientPocList = new ArrayList<FillClientPoc>();
		}
		
		return SUCCESS;
	}
	
	
	private boolean checkClientBrandAvailability(String clientId) {
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);   
		UtilityFunctions uF = new UtilityFunctions();
		boolean flag = false;
		try {
			
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from client_brand_details where client_id=?");
			pst.setInt(1, uF.parseToInt(getClientId()));
			rs = pst.executeQuery();
			while(rs.next()){
				flag = true;
			}
			rs.close();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return flag;
	}


	HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		// TODO Auto-generated method stub
		this.request = request;
		
	}
	
	@Override
	public void setServletResponse(HttpServletResponse arg0) {
		// TODO Auto-generated method stub
		
	}

	public List<FillClientPoc> getClientPocList() {
		return clientPocList;
	}

	public void setClientPocList(List<FillClientPoc> clientPocList) {
		this.clientPocList = clientPocList;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
	
	public String getClientPoc() {
		return clientPoc;
	}

	public void setClientPoc(String clientPoc) {
		this.clientPoc = clientPoc;
	}

	public List<FillClientBrand> getClientBrandList() {
		return clientBrandList;
	}

	public void setClientBrandList(List<FillClientBrand> clientBrandList) {
		this.clientBrandList = clientBrandList;
	}

	public String getStrClientBrand() {
		return strClientBrand;
	}

	public void setStrClientBrand(String strClientBrand) {
		this.strClientBrand = strClientBrand;
	}

	public boolean isBrandFlag() {
		return brandFlag;
	}

	public void setBrandFlag(boolean brandFlag) {
		this.brandFlag = brandFlag;
	}

}
