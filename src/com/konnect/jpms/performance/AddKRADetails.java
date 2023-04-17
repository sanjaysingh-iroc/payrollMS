package com.konnect.jpms.performance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class AddKRADetails extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 6066370977286203810L;
	HttpSession session;
	private HttpServletRequest request;
	String strUserType = null;
	String strSessionEmpId = null;
	private CommonFunctions CF;
	
	private String operation;
	private String submit;
	private List<FillLevel> levelList;
	private List<FillAttribute> attributeList;
	
	private String type;
	
	private String kra;
	private String kraDesc;
	private String strAttribute;
	private String strLevel;
	private String measurable;
	
	private String kraid;

	public String execute() {

		session = request.getSession();
		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return "login";
		request.setAttribute(PAGE, "/jsp/performance/AddKRADetails.jsp");
		request.setAttribute(TITLE, "Add KRA");
		
		attributeList = new FillAttribute(request).fillAttribute();
		levelList = new FillLevel(request).fillLevel();
		
//		System.out.println("getOperation() ===>"+getOperation() );
		
		if (getOperation() != null && getOperation().equals("A")) {
			
			if(getSubmit()!=null && getSubmit().equals("Save")){ 
				insertKRADetails();
				return "update";
			}
			
			return SUCCESS;
		} else if (getOperation() != null && getOperation().equals("E")) {

			if(getSubmit()!=null && getSubmit().equals("Save")){ 
				updateKRADetails();
				return "update";
			}
			request.setAttribute(PAGE, "/jsp/performance/EditKRA.jsp");
			getKRADetails();
			
			return LOAD;
		}if (getOperation() != null && getOperation().equals("D")) {
			
			deleteKRADetails();
			
			return "update";
		}

		return SUCCESS;

	}
	
	private void deleteKRADetails() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("delete from kra_details where kra_id=?");
			pst.setInt(1, uF.parseToInt(getKraid()));
			pst.execute();
			pst.close();
				
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);			
		}
	}

	private void updateKRADetails() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {

			con = db.makeConnection(con);
				
//			System.out.println("getStrLevel()=====>"+getStrLevel());
				
			pst = con.prepareStatement("update kra_details set kra=?,kra_desc=?,attribute_id=?,level_id=?,measurable=? where kra_id=?");
			pst.setString(1, getKra());
			pst.setString(2, getKraDesc()); 
			pst.setInt(3, uF.parseToInt(getStrAttribute()));
			pst.setInt(4, uF.parseToInt(getStrLevel()));
			pst.setInt(5, uF.parseToInt(getMeasurable()));
			pst.setInt(6, uF.parseToInt(getKraid()));
			pst.execute();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);			
		}
	}

	private void getKRADetails() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst=null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
 
		try {

			con = db.makeConnection(con);
			
			pst = con.prepareStatement("select * from kra_details where kra_id=?");
			pst.setInt(1, uF.parseToInt(getKraid()));
			rst=pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while(rst.next()){
				setKra(rst.getString("kra"));
				setKraDesc(rst.getString("kra_desc"));
				setStrAttribute(rst.getString("attribute_id"));
				setStrLevel(rst.getString("level_id"));
				setMeasurable(rst.getString("measurable"));
			}
			rst.close();
			pst.close();
				
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);			
		}
	}


	private void insertKRADetails() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {

			con = db.makeConnection(con);
				
//			System.out.println("getStrLevel()=====>"+getStrLevel());
			
			if(getStrLevel()!=null && !getStrLevel().equals("")){
				
				List<String> levelidList=Arrays.asList(getStrLevel().split(","));
				
				for(int i=0;levelidList!=null && !levelidList.isEmpty() && i<levelidList.size();i++){
					pst = con.prepareStatement("insert into kra_details(kra,kra_desc,attribute_id,level_id,measurable)values (?,?,?,?,?)");
					pst.setString(1, getKra());
					pst.setString(2, getKraDesc()); 
					pst.setInt(3, uF.parseToInt(getStrAttribute()));
					pst.setInt(4, uF.parseToInt(levelidList.get(i).trim()));
					pst.setInt(5, uF.parseToInt(getMeasurable()));
					pst.execute();
					pst.close();
				}
			}
				
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);			
		}
	}


	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}


	public String getOperation() {
		return operation;
	}


	public void setOperation(String operation) {
		this.operation = operation;
	}


	public List<FillLevel> getLevelList() {
		return levelList;
	}


	public void setLevelList(List<FillLevel> levelList) {
		this.levelList = levelList;
	}


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}


	public List<FillAttribute> getAttributeList() {
		return attributeList;
	}


	public void setAttributeList(List<FillAttribute> attributeList) {
		this.attributeList = attributeList;
	}


	public String getSubmit() {
		return submit;
	}


	public void setSubmit(String submit) {
		this.submit = submit;
	}


	public String getKra() {
		return kra;
	}


	public void setKra(String kra) {
		this.kra = kra;
	}


	public String getKraDesc() {
		return kraDesc;
	}


	public void setKraDesc(String kraDesc) {
		this.kraDesc = kraDesc;
	}


	public String getStrAttribute() {
		return strAttribute;
	}


	public void setStrAttribute(String strAttribute) {
		this.strAttribute = strAttribute;
	}


	public String getStrLevel() {
		return strLevel;
	}


	public void setStrLevel(String strLevel) {
		this.strLevel = strLevel;
	}


	public String getMeasurable() {
		return measurable;
	}


	public void setMeasurable(String measurable) {
		this.measurable = measurable;
	}


	public String getKraid() {
		return kraid;
	}

	public void setKraid(String kraid) {
		this.kraid = kraid;
	}

}
