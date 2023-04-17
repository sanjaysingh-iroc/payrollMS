package com.konnect.jpms.task;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class GetOrgData extends ActionSupport implements ServletRequestAware{

	List<FillWLocation> wLocationList;
	List<FillServices> serviceList;
	List<FillDepartment> deptList;
	List<FillLevel> levelList;
	
	private static final long serialVersionUID = 1L;

	public String execute() {
	
		UtilityFunctions uF = new UtilityFunctions();
		String strOId = request.getParameter("OID");
		
		wLocationList = new FillWLocation(request).fillWLocation(strOId);
		deptList = new FillDepartment(request).fillDepartment(uF.parseToInt(strOId));
		serviceList = new FillServices(request).fillServices(strOId, uF);
		
//		String strLevels = getOrgwiseLevelId(uF, strOId);
		levelList = new FillLevel(request).fillLevel(uF.parseToInt(strOId));
//		levelList = new FillDesig(request).fillDesigFromLevel(strLevels);
		
		return SUCCESS;
		
	}
	
//private String getOrgwiseLevelId(UtilityFunctions uF, String strOrgId) {
//		
//		Connection con = null;
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		Database db = new Database();
//		db.setRequest(request);
//		StringBuilder sbLevelIds = null;
//		
//		try {
//
//			con = db.makeConnection(con);
//			
//			if(uF.parseToInt(strOrgId)> 0) {
//				pst = con.prepareStatement("select level_id from level_details where org_id = ?");
//				pst.setInt(1, uF.parseToInt(strOrgId));
//			} else {
//				pst = con.prepareStatement("select level_id from level_details");
//			}
//			rs = pst.executeQuery();			
//			while(rs.next()){
//				if(sbLevelIds == null) {
//					sbLevelIds = new StringBuilder();
//					sbLevelIds.append(rs.getString("level_id"));
//				} else {
//					sbLevelIds.append("," + rs.getString("level_id"));
//				}
//			}
//			if(sbLevelIds == null) {
//				sbLevelIds = new StringBuilder();
//			}
//            rs.close();
//            pst.close();
//			
//		} catch (Exception e) {
//			e.printStackTrace();			
//		}finally{
//			db.closeResultSet(rs);
//			db.closeStatements(pst);
//			db.closeConnection(con);
//		}
//		
//		return sbLevelIds.toString();
//	}

	HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public List<FillWLocation> getwLocationList() {
		return wLocationList;
	}
	
	public void setwLocationList(List<FillWLocation> wLocationList) {
		this.wLocationList = wLocationList;
	}
	
	public List<FillServices> getServiceList() {
		return serviceList;
	}
	
	public void setServiceList(List<FillServices> serviceList) {
		this.serviceList = serviceList;
	}
	
	public List<FillDepartment> getDeptList() {
		return deptList;
	}
	
	public void setDeptList(List<FillDepartment> deptList) {
		this.deptList = deptList;
	}
	
	public List<FillLevel> getLevelList() {
		return levelList;
	}
	
	public void setLevelList(List<FillLevel> levelList) {
		this.levelList = levelList;
	}
	
}
