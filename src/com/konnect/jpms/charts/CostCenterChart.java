package com.konnect.jpms.charts;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class CostCenterChart extends ActionSupport implements
		ServletRequestAware, IStatements {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6684038378838021787L;
	HttpSession session;
	private HttpServletRequest request;
	CommonFunctions CF;

	public String execute() {

		System.out.println("execute of CostCenterChart..");
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;

		return loadCostCenterDetails();

	}

	public String loadCostCenterDetails() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		Map servicesMap = CF.getServicesMap(con,true);
		ArrayList<String> alCostCenterNames = new ArrayList<String>();
		ArrayList<String> alCostCenterCnt = new ArrayList<String>();
		String service_Ids = null;
		
		try {
			
			String EMPID = (String)session.getAttribute("EMPID");
			
			con = db.makeConnection(con);
			pst = con.prepareStatement(selectCostCenter);
			pst.setInt(1, uF.parseToInt(EMPID));
//			System.out.println("pst selectCostCenter=>"+pst);
			rs = pst.executeQuery();
			
			while(rs.next()) {
				service_Ids = rs.getString("service_id");
			}
			
			rs.close();
			pst.close();
			
			if(service_Ids!=null){
				
				String serviceIds[] = service_Ids.split(",");
				
				
				for(int i=0; i<serviceIds.length; i++) {
					alCostCenterNames.add((String)servicesMap.get(serviceIds[i]));
				}
				
//				System.out.println("alCostCenterNames===>>"+alCostCenterNames);
				
				int cnt = 0;
				for(int i=0; i<serviceIds.length; i++) {
					con = db.makeConnection(con);
					pst = con.prepareStatement(selectEmpFromCostCenter);
					pst.setString(1, "%"+serviceIds[i]+"%");
					rs = pst.executeQuery();
					while(rs.next()) {
						alCostCenterCnt.add(rs.getInt("count")+"");
					}
			        rs.close();
			        pst.close();
				}
				
				System.out.println("alCostCenterCnt====>>"+alCostCenterCnt);
				
			}
			
			request.setAttribute("alCostCenterNames", alCostCenterNames);
			request.setAttribute("alCostCenterCnt", alCostCenterCnt);
			
			
		} catch (SQLException se) {
			se.printStackTrace();

		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

		return SUCCESS;
	}

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

}
