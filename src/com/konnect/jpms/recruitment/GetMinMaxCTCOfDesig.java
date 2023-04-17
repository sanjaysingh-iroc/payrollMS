package com.konnect.jpms.recruitment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class GetMinMaxCTCOfDesig extends ActionSupport implements ServletRequestAware{

	  String strDesig;

	  private static final long serialVersionUID = 1L;

	  public String execute() {
			
		  getMinMaxCTCOfDesig();
		  
		return SUCCESS;			
	  }


	private void getMinMaxCTCOfDesig() {
		
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		ResultSet rst = null;
		try {
			con=db.makeConnection(con);
			pst = con.prepareStatement("select desig_value from desig_attribute where desig_id=? and _type = 8");
			pst.setInt(1, uF.parseToInt(getStrDesig()));
			rst=pst.executeQuery();
			String strMinMaxCTC = "0::::0";
			while(rst.next()) {		
				String[] tmpMinMaxCTC = rst.getString("desig_value").split("-");
				strMinMaxCTC = tmpMinMaxCTC[0]+"::::"+tmpMinMaxCTC[1];
			}
			rst.close();
			pst.close();
			
			request.setAttribute("strMinMaxCTC", strMinMaxCTC);
			
		}catch(Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}


	public String getStrDesig() {
		return strDesig;
	}

	public void setStrDesig(String strDesig) {
		this.strDesig = strDesig;
	}




	HttpServletRequest request;
		
	  @Override
	  public void setServletRequest(HttpServletRequest request) {
			this.request = request;
	  }

	
}
