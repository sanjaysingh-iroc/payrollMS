package com.konnect.jpms.master;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillClassAndDivisionLevel;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IConstants;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class AddEditDeleteClassAndDivision extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	
	String strOrg;
	String classId;
	String className;
	String classDescription;
	String classCode;
	String classLevel;
	String[] divCount;;
	List<FillOrganisation> orgList;
	List<FillClassAndDivisionLevel> classLevelList;
	List<FillClassAndDivisionLevel> divLevelList;
	String operation;
	
	HttpSession session;
	CommonFunctions CF;
	String strUsertypeId;
	String strSessionEmpId;
	
	String userscreen;
	String navigationId;
	String toPage;
	
	public String execute() throws Exception {

//		request.setAttribute(PAGE, PAddService);
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null){
			return LOGIN;
		}
		strUsertypeId = (String) session.getAttribute(BASEUSERTYPEID);
		strSessionEmpId = (String) session.getAttribute(EMPID);
		
		loadValidateService();
		
		if (operation!=null && operation.equals("D")) {
			return deleteClass();
		}
		if (operation!=null && operation.equals("E")) {
			return viewClass();
		}
		
		if (getClassId()!=null && getClassId().length()>0) {
			return updateClass();
		}
		
		if (getClassName()!=null && getClassName().length()>0) {
			return insertClass();
		}
		System.out.println("in classdiv add load");
		return LOAD;
		
	}

	public String loadValidateService() {
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {

			System.out.println("in addclassdiv");
			con = db.makeConnection(con);
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con, request);
			request.setAttribute("hmFeatureStatus", hmFeatureStatus);
			Map<String, List<String>> hmFeatureUserTypeId = (Map<String, List<String>>)request.getAttribute("hmFeatureUserTypeId");
			String empOrgId = null;
			if(uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_ADD_IN_LOGIN_USER_ORG)) && hmFeatureUserTypeId.get(IConstants.F_ADD_IN_LOGIN_USER_ORG).contains(strUsertypeId)) {
				empOrgId = (String)session.getAttribute(ORGID);
			}
			orgList = new FillOrganisation(request).fillOrganisation(empOrgId);
			classLevelList = new FillClassAndDivisionLevel().fillClassLevel();
			divLevelList = new FillClassAndDivisionLevel().fillDivisionLevel();
			
			if(getStrOrg()==null && orgList!=null && orgList.size()>0) {
				setStrOrg((String)session.getAttribute(ORGID));
			}
			
			StringBuilder sbDivLevelList = new StringBuilder();
			for (int i = 0; i < divLevelList.size(); i++) {
				sbDivLevelList.append("<option value=\'"+divLevelList.get(i).getClassOrDivId()+"\'>"+divLevelList.get(i).getClassOrDivName()+"</option>");
			}
			System.out.println("sbDivLevelList ===>> " + sbDivLevelList.toString());
			request.setAttribute("sbDivLevelList", sbDivLevelList.toString());
			
		} catch (Exception e) {
			e.printStackTrace();
			return ERROR;
		} finally {
			db.closeConnection(con);
		}
		return SUCCESS;
	}

	
	public String insertClass() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("insert into class_details (class_name, class_level, class_desc, org_id, added_by, entry_date) values (?,?,?,?, ?,?)");
			pst.setString(1, getClassName());
			pst.setInt(2, uF.parseToInt(getClassLevel()));
			pst.setString(3, getClassDescription());
			pst.setInt(4, uF.parseToInt(getStrOrg()));
			pst.setInt(5, uF.parseToInt(strSessionEmpId));
			pst.setTimestamp(6, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone()), DBDATE+DBTIME));
			pst.executeUpdate();
			pst.close();
			
			String strNewClassId=null;
			pst = con.prepareStatement("select max(class_id) as class_id from class_details");
			rst = pst.executeQuery();
			while(rst.next()) {
				strNewClassId = rst.getString("class_id");
			}
			rst.close();
			pst.close();
			
			for (int i = 0; i <getDivCount().length; i++) {
//				String divId = request.getParameter("divId_"+getDivCount()[i]);
				String strDiv = request.getParameter("strDiv_"+getDivCount()[i]);
				String divLevel = request.getParameter("divLevel_"+getDivCount()[i]);
				
				pst = con.prepareStatement("insert into division_details (division_name, division_level, class_id, added_by, entry_date) values (?,?,?,?, ?)");
				pst.setString(1, strDiv);
				pst.setInt(2, uF.parseToInt(divLevel));
				pst.setInt(3, uF.parseToInt(strNewClassId));
				pst.setInt(4, uF.parseToInt(strSessionEmpId));
				pst.setTimestamp(5, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone()), DBDATE+DBTIME));
				pst.executeUpdate();
				pst.close();
			
			}

			session.setAttribute(MESSAGE, SUCCESSM+getClassName()+" saved successfully."+END);

		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute(MESSAGE, "Error in insertion");
			return ERROR;
		} finally {
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;
	}

	
	public String viewClass() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from class_details where class_id=?");
			pst.setInt(1, uF.parseToInt(getClassId()));
			rs = pst.executeQuery();
			while(rs.next()){
				setClassId(rs.getString("class_id"));
				setClassName(rs.getString("class_name"));
				setClassLevel(rs.getString("class_level"));
				setClassDescription(rs.getString("class_desc"));
				setStrOrg(rs.getString("org_id"));
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("SELECT * FROM division_details where class_id=? order by division_name");
			pst.setInt(1, uF.parseToInt(getClassId()));
			rs = pst.executeQuery();
			Map<String, List<String>> hmClassDiv = new HashMap<String, List<String>>();
			while(rs.next()) {
				List<String> alInner = new ArrayList<String>();
				alInner.add(rs.getString("division_id"));
				alInner.add(rs.getString("division_name"));
				StringBuilder sbDivLevelList = new StringBuilder();
				sbDivLevelList.append("<option value=''>Select Level</option>");
				for (int i = 0; i < divLevelList.size(); i++) {
					sbDivLevelList.append("<option value=\'"+divLevelList.get(i).getClassOrDivId()+"\' ");
					if(uF.parseToInt(rs.getString("division_level")) == uF.parseToInt(divLevelList.get(i).getClassOrDivId())) {
						sbDivLevelList.append(" selected=\"selected\" ");
					}
					sbDivLevelList.append(" >"+divLevelList.get(i).getClassOrDivName()+"</option>");
				}
				alInner.add(sbDivLevelList.toString());
				hmClassDiv.put(rs.getString("division_id"), alInner);
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmClassDiv", hmClassDiv);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return LOAD;
	}
	
	
	public String updateClass() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
	
		try {
			
			con = db.makeConnection(con);
			pst = con.prepareStatement("UPDATE class_details SET class_name=?, class_level=?, class_desc=?, org_id=?, updated_by=?, update_date=? WHERE class_id=?");
			pst.setString(1, getClassName());
			pst.setInt(2, uF.parseToInt(getClassLevel()));
			pst.setString(3, getClassDescription());
			pst.setInt(4, uF.parseToInt(getStrOrg()));
			pst.setInt(5, uF.parseToInt(strSessionEmpId));
			pst.setTimestamp(6, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone()), DBDATE+DBTIME));
			pst.setInt(7, uF.parseToInt(getClassId()));
			pst.execute();
			pst.close();

			
			pst = con.prepareStatement("select * from division_details where class_id = ?");
			pst.setInt(1, uF.parseToInt(getClassId()));
			List<String> divIdList = new ArrayList<String>();
			rst = pst.executeQuery();
			while (rst.next()) {
				divIdList.add(rst.getString("division_id"));
			}
			
			
			for (int i = 0; i <getDivCount().length; i++) {
				String divId = request.getParameter("divId_"+getDivCount()[i]);
				String strDiv = request.getParameter("strDiv_"+getDivCount()[i]);
				String divLevel = request.getParameter("divLevel_"+getDivCount()[i]);
				
				if(uF.parseToInt(divId)>0) {
					pst = con.prepareStatement("update division_details set division_name=?,division_level=?,class_id=?,updated_by=?,update_date=? where division_id=?");
					pst.setString(1, strDiv);
					pst.setInt(2, uF.parseToInt(divLevel));
					pst.setInt(3, uF.parseToInt(getClassId()));
					pst.setInt(4, uF.parseToInt(strSessionEmpId));
					pst.setTimestamp(5, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone()), DBDATE+DBTIME));
					pst.setInt(6, uF.parseToInt(divId));
					pst.executeUpdate();
					pst.close();
					
					if(divIdList.contains(divId)) {
						divIdList.remove(divId);
					}
					
				} else {
					pst = con.prepareStatement("insert into division_details (division_name,division_level,class_id,added_by,entry_date) values (?,?,?,?, ?)");
					pst.setString(1, strDiv);
					pst.setInt(2, uF.parseToInt(divLevel));
					pst.setInt(3, uF.parseToInt(getClassId()));
					pst.setInt(4, uF.parseToInt(strSessionEmpId));
					pst.setTimestamp(5, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone()), DBDATE+DBTIME));
					pst.executeUpdate();
					pst.close();
				}
				
			}
			
			for(int i=0; i<divIdList.size(); i++) {
				if(uF.parseToInt(divIdList.get(i))==0) {
					continue;
				}
				pst = con.prepareStatement("delete from division_details where division_id=?");
				pst.setInt(1, uF.parseToInt(divIdList.get(i)));
				pst.executeUpdate();
				pst.close();
			}
			
			session.setAttribute(MESSAGE, SUCCESSM+getClassName()+" updated successfully."+END);
			
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute(MESSAGE, "Error in updation");
		} finally {
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;
		
	}
	
	

	public String deleteClass() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement("delete from division_details where class_id=?");
			pst.setInt(1, uF.parseToInt(getClassId()));
			pst.execute();
			pst.close();
			
			pst = con.prepareStatement("delete from class_details where class_id=?");
			pst.setInt(1, uF.parseToInt(getClassId()));
			pst.execute();
			pst.close();
			
			session.setAttribute(MESSAGE, SUCCESSM+"Class deleted successfully."+END);
			
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute(MESSAGE, "Error in deletion");
			return ERROR;
		} finally {
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}

	public String getStrOrg() {
		return strOrg;
	}

	public void setStrOrg(String strOrg) {
		this.strOrg = strOrg;
	}

	public List<FillOrganisation> getOrgList() {
		return orgList;
	}

	public void setOrgList(List<FillOrganisation> orgList) {
		this.orgList = orgList;
	}

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public String getUserscreen() {
		return userscreen;
	}

	public void setUserscreen(String userscreen) {
		this.userscreen = userscreen;
	}

	public String getNavigationId() {
		return navigationId;
	}

	public void setNavigationId(String navigationId) {
		this.navigationId = navigationId;
	}

	public String getToPage() {
		return toPage;
	}

	public void setToPage(String toPage) {
		this.toPage = toPage;
	}

	public String getClassId() {
		return classId;
	}

	public void setClassId(String classId) {
		this.classId = classId;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getClassDescription() {
		return classDescription;
	}

	public void setClassDescription(String classDescription) {
		this.classDescription = classDescription;
	}

	public String getClassCode() {
		return classCode;
	}

	public void setClassCode(String classCode) {
		this.classCode = classCode;
	}

	public String getClassLevel() {
		return classLevel;
	}

	public void setClassLevel(String classLevel) {
		this.classLevel = classLevel;
	}

	public List<FillClassAndDivisionLevel> getClassLevelList() {
		return classLevelList;
	}

	public void setClassLevelList(List<FillClassAndDivisionLevel> classLevelList) {
		this.classLevelList = classLevelList;
	}

	public List<FillClassAndDivisionLevel> getDivLevelList() {
		return divLevelList;
	}

	public void setDivLevelList(List<FillClassAndDivisionLevel> divLevelList) {
		this.divLevelList = divLevelList;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String[] getDivCount() {
		return divCount;
	}

	public void setDivCount(String[] divCount) {
		this.divCount = divCount;
	}

}