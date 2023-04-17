package com.konnect.jpms.performance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class GetElementwiseAttributeList extends ActionSupport implements ServletRequestAware {

	private String level;
	private String elementID;
	private String orgId;
	private String count;
	
//	List<FillEmployee> empList;
//	List<FillAttribute> attributeList;
	
	private static final long serialVersionUID = 1L;

	public String execute() {

//		System.out.println("orgId ==>>> " + orgId);
//		empList=getEmployeeList();
		getAttributeListElementwise();
		return SUCCESS;

	}

public void getAttributeListElementwise() {
		
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
			while(rs.next()) {
				if(cnt==0){	
					sbLevelids.append(rs.getString("level_id"));
					cnt++;
				}else{
					sbLevelids.append(","+rs.getString("level_id"));
				}
			}
			rs.close();
			pst.close();
			
			List<FillAttribute> attribList = new FillAttribute().fillElementAttributeElementwise(sbLevelids.toString(), getElementID());
			
			StringBuilder sb = new StringBuilder(); 
			for(int i=0; attribList != null && !attribList.isEmpty() && i< attribList.size(); i++) {
				sb.append("<option value=" + attribList.get(i).getId() + ">"+ attribList.get(i).getName() + "</option>");
			}
			request.setAttribute("attributeOptions", sb.toString());
			
			
			/*StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select distinct(a.appraisal_attribute),a.appraisal_element,aa.attribute_name from (select " +
					"appraisal_element,appraisal_attribute from appraisal_element_attribute where appraisal_element = ?" +
					") as a, appraisal_attribute aa where a.appraisal_attribute=aa.arribute_id ");
			if(sbLevelids != null && !sbLevelids.toString().equals("")){
			sbQuery.append("and level_id in ("+sbLevelids.toString()+")");
			}
			sbQuery.append(" order by appraisal_attribute");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(getElementID()));
			rs = pst.executeQuery();
//			System.out.println("pst ===> "+pst);
			StringBuilder sb = new StringBuilder(); 
			while(rs.next()) {
				sb.append("<option value=" + rs.getString("appraisal_attribute") + ">"+ rs.getString("attribute_name") + "</option>");
				//al.add(new FillAttribute(rs.getString("appraisal_attribute"), rs.getString("attribute_name")));				
			}
			request.setAttribute("attributeOptions", sb.toString());*/
//			System.out.println("ATTRIBUTES =====> "+al.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			
			db.closeStatements(pst);
			db.closeResultSet(rs);
			db.closeConnection(con);
		}
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

	public void setElementID(String elementID) {
		this.elementID = elementID;
	}

	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

}
