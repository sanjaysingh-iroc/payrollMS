package com.konnect.jpms.performance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class GetAttributeList extends ActionSupport implements ServletRequestAware {

	private String strOrg;
	private String level;
	private String elementID;
	private String orgId;
	private String goalCnt;
	
	private String type;
	
//	List<FillEmployee> empList;
	private List<FillAttribute> attributeList;
	
	private static final long serialVersionUID = 1L;

	public String execute() {

//		System.out.println("orgId ==>>> " + orgId);
//		System.out.println("getType() ==>>> " + getType());
//		empList=getEmployeeList();
		attributeList = getAttributeListElementwise();
		
		if(getType() != null && getType().equals("MULTIKRA")) {
			StringBuilder sbOption = new StringBuilder();
			for(int i=0; attributeList != null && !attributeList.isEmpty() && i<attributeList.size(); i++) {
				sbOption.append("<option value='" + attributeList.get(i).getId() + "'>"+ attributeList.get(i).getName() + "</option>");
			}
			request.setAttribute("sbOption", sbOption.toString());
		}
		return SUCCESS;

	}

public List<FillAttribute> getAttributeListElementwise() {
		
		List<FillAttribute> al = new ArrayList<FillAttribute>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {

			con = db.makeConnection(con);
			StringBuilder sbLevelids = new StringBuilder();
			pst = con.prepareStatement("select level_id from level_details where org_id = ?");
			pst.setInt(1, uF.parseToInt(orgId));
			rs = pst.executeQuery();
//			System.out.println("pst Level ===> "+pst);
			int cnt=0;
			while(rs.next()){
				if(cnt==0){	
					sbLevelids.append(rs.getString("level_id"));
					cnt++;
				}else{
					sbLevelids.append(","+rs.getString("level_id"));
				}
			}
			rs.close();
			pst.close();
			
			al = new FillAttribute(request).fillElementAttributeElementwise(sbLevelids.toString(), getElementID());
//			pst = con.prepareStatement("select * from appraisal_attribute where status=true order by attribute_name");
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return al;
	}
	

	public String getStrOrg() {
		return strOrg;
	}

	public void setStrOrg(String strOrg) {
		this.strOrg = strOrg;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getElementID() {
		return elementID;
	}

	public List<FillAttribute> getAttributeList() {
		return attributeList;
	}

	public void setAttributeList(List<FillAttribute> attributeList) {
		this.attributeList = attributeList;
	}

	public void setElementID(String elementID) {
		this.elementID = elementID;
	}

	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

	public String getGoalCnt() {
		return goalCnt;
	}

	public void setGoalCnt(String goalCnt) {
		this.goalCnt = goalCnt;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

}
