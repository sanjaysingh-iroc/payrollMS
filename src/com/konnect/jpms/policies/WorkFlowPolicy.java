package com.konnect.jpms.policies;

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

import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class WorkFlowPolicy extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;

	HttpSession session;
	String strSessionEmpId;
	String strSessionUserType;
	CommonFunctions CF;

	String submit;
	String policy_type;
	String operation; 
	
	List<FillWLocation> workList;
	List<FillOrganisation> organisationList;
	
	String effectiveDate;
	String location;
	String organization;
	String policyName;
	String pcount;
	
	
	String pnameValue;
	String eDatevalue;
	String locationvalue;
	String organizationvalue;
	
	String status;
	String count;
	String group_id;
	
	String anyOne;
	String anyOneRegular;
	String anyOneContengency;
	String anyOneType;
	String empselected;

	String userscreen;
	String navigationId;
	String toPage;
	
	public String execute() {
		session = request.getSession();
		strSessionEmpId = (String) session.getAttribute(EMPID);
		strSessionUserType = (String) session.getAttribute(USERTYPE);
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return "login";

		request.setAttribute(PAGE, "/jsp/policies/WorkFlowPolicy.jsp");
		request.setAttribute(TITLE, "Work Flow Policy");

		getWorkFlowMember();
		
		organisationList = new FillOrganisation(request).fillOrganisation();
		workList = new FillWLocation(request).fillWLocation(null);		
		
//		System.out.println("getOrganization() ===>> " + getOrganization());
//		System.out.println("getLocation() ===>> " + getLocation());
		if (getOperation() != null && getOperation().equals("E") && getPcount()!=null) {
			getWorkFlowPolicy();
			if (getSubmit() != null) {
				updateWorkFlowPolicy();
				return SUCCESS;
			}
		}else if (getOperation() != null && getOperation().equals("S") && getPcount()!=null) {			
			enableDisablePolicy();		
			//return "status";
			return SUCCESS;
		}else if (getOperation() != null && getOperation().equals("D") && getPcount()!=null) {
			
			deleteWorkFlowPolicy();		
			return SUCCESS;
		}else if (getOperation() != null && getOperation().equals("A")) {
			if (getSubmit() != null) {
				insertWorkFlowPolicy();
				return SUCCESS;
			}
		}
		
		return LOAD;
	}

	private void enableDisablePolicy() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("update work_flow_policy set policy_status=? where  policy_count=? and trial_status=1");
			pst.setInt(1, uF.parseToInt(getStatus()));
			pst.setInt(2, uF.parseToInt(getPcount()));
//			System.out.println("pst====>" + pst);
			pst.execute();
			pst.close();
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}

	private void deleteWorkFlowPolicy() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("delete from work_flow_policy where policy_count=?");
			pst.setInt(1, uF.parseToInt(getPcount()));
//			System.out.println("pst====>" + pst);
			pst.execute();
			pst.close();
			
			pst = con.prepareStatement("delete from specific_emp where policy_id=?");
			pst.setInt(1, uF.parseToInt(getPcount()));
			pst.execute();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}

	private void updateWorkFlowPolicy() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		List<String> memIdList = (List<String>) request.getAttribute("memIdList");

		try {
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("select * from work_flow_policy where  policy_count=? and trial_status=1");
			pst.setInt(1, uF.parseToInt(getPcount()));
			rs = pst.executeQuery();
			int nStatus = -1;
			while (rs.next()) {
				nStatus = uF.parseToInt(rs.getString("policy_status"));
			}
			
			pst = con.prepareStatement("delete from work_flow_policy where  policy_count=?");
			pst.setInt(1, uF.parseToInt(getPcount()));
			pst.execute();
			pst.close();

			if(uF.parseToInt(getAnyOneType())<3){
					if (memIdList != null && memIdList.size() > 0) {
		
						for (int i = 0; i < memIdList.size(); i++) {
							String mem_id = memIdList.get(i);
							String mem_position = request.getParameter(mem_id.trim());
		
							pst = con.prepareStatement("insert into work_flow_policy(work_flow_member_id,member_position,policy_type,"
											+ " trial_status,added_by,added_date,policy_count,policy_name,effective_date,org_id,location_id,policy_status,group_id)" +
													"values(?,?,?,?,?,?,?,?,?,?,?,?,?)");
							pst.setInt(1, uF.parseToInt(mem_id));
							pst.setDouble(2, uF.parseToDouble(mem_position));
							pst.setString(3, "1");
							pst.setInt(4, 1);
							pst.setInt(5, uF.parseToInt(strSessionEmpId));
							pst.setDate(6, uF.getCurrentDate(CF.getStrTimeZone()));
							pst.setInt(7, uF.parseToInt(getPcount()));
							pst.setString(8, getPolicyName());
							pst.setDate(9, null);
							pst.setString(10, getOrganization());
							pst.setString(11, getLocation());
							pst.setInt(12, nStatus);
							pst.setInt(13,uF.parseToInt(getGroup_id()));
							pst.execute();
							pst.close();
		
						}
						
					}
			}else{
				pst = con.prepareStatement("insert into work_flow_policy(work_flow_member_id,member_position,policy_type,"
						+ " trial_status,added_by,added_date,policy_count,policy_name,effective_date,org_id,location_id,policy_status,group_id)" +
							"values(?,?,?,?,?,?,?,?,?,?,?,?,?)");
				pst.setInt(1, uF.parseToInt(getAnyOneRegular()));
				pst.setDouble(2, 1);
				pst.setString(3, "1");
				pst.setInt(4, 1);
				pst.setInt(5, uF.parseToInt(strSessionEmpId));
				pst.setDate(6, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setInt(7, uF.parseToInt(getPcount()));
				pst.setString(8, getPolicyName());
				pst.setDate(9, null);
				pst.setString(10, getOrganization());
				pst.setString(11, getLocation());
				pst.setInt(12, nStatus);
				pst.setInt(13,uF.parseToInt(getGroup_id()));
				int x = pst.executeUpdate();
				pst.close();
				
				if(x > 0){
					pst = con.prepareStatement("delete from specific_emp where policy_id=?");
					pst.setInt(1, uF.parseToInt(getPcount()));
					pst.execute();
					pst.close();
					
					if(getEmpselected()!=null && !getEmpselected().trim().equals("") && !getEmpselected().trim().equalsIgnoreCase("NULL")){
						List<String> alEmp = Arrays.asList(getEmpselected().split(","));
						if(alEmp == null) alEmp = new ArrayList<String>();
						for(int i=0; i<alEmp.size(); i++){
							int nEmpId = uF.parseToInt(alEmp.get(i).trim());
							if(nEmpId > 0){
								pst = con.prepareStatement("insert into specific_emp(emp_id,policy_id) values(?,?)");
								pst.setInt(1, nEmpId);
								pst.setInt(2, uF.parseToInt(getPcount()));
								pst.execute();
								pst.close();
							}
						}
					}
				}
				
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}

	private void getWorkFlowPolicy() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF=new UtilityFunctions();
		
		Map<String, String> hmSelected = new HashMap<String, String>();
		try {
			con = db.makeConnection(con);
		
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			
			pst = con.prepareStatement("select work_flow_member_id,member_position from work_flow_policy where policy_type='1' and policy_count=? and trial_status=1");
			pst.setInt(1, uF.parseToInt(getPcount()));
			rs = pst.executeQuery();
			while (rs.next()) {
				String member_position = "" + (int)rs.getDouble("member_position");
				hmSelected.put(rs.getString("work_flow_member_id"), member_position);
			}
			rs.close();
			pst.close();
			request.setAttribute("hmSelected", hmSelected);
//			System.out.println("hmSelected=====>"+hmSelected);
			
			pst = con
			.prepareStatement("select work_flow_member_id,member_position from work_flow_policy where policy_type='2' and policy_count=? and trial_status=1");
			pst.setInt(1, uF.parseToInt(getPcount()));
			rs = pst.executeQuery();
			Map<String, String> hmSelectedSecond = new HashMap<String, String>();
			while (rs.next()) {
				String member_position = "" + (int)rs.getDouble("member_position");
				hmSelectedSecond.put(rs.getString("work_flow_member_id"), member_position);
			}
			rs.close();
			pst.close();
			request.setAttribute("hmSelectedSecond", hmSelectedSecond);
//			System.out.println("hmSelectedSecond=====>"+hmSelectedSecond);
			
			
			pst = con.prepareStatement("select * from work_flow_policy where policy_count=? and trial_status=1");
			pst.setInt(1, uF.parseToInt(getPcount()));
			rs = pst.executeQuery();
			while (rs.next()) {
				//setPnameValue(uF.showData(rs.getString("policy_name"), ""));
//				seteDatevalue(rs.getString("effective_date")!=null?uF.getDateFormat(rs.getString("effective_date"), DBDATE, DATE_FORMAT):"");
				setOrganizationvalue(uF.showData(rs.getString("org_id"), ""));
				setLocationvalue(uF.showData(rs.getString("location_id"), ""));
				
				request.setAttribute("pname", uF.showData(rs.getString("policy_name"), ""));
				request.setAttribute("valuedate", rs.getString("effective_date")!=null ? uF.getDateFormat(rs.getString("effective_date"), DBDATE, DATE_FORMAT) : "");
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from specific_emp sp, employee_personal_details epd, employee_official_details eod " +
					"where sp.emp_id=epd.emp_per_id and eod.emp_id=epd.emp_per_id and sp.emp_id=eod.emp_id " +
					"and epd.is_alive=true and sp.policy_id=?");
			pst.setInt(1, uF.parseToInt(getPcount()));
			rs = pst.executeQuery();
			List<String> alSelectedEmp = new ArrayList<String>();
			List<Map<String, String>> alSelectedEmpMap = new ArrayList<Map<String,String>>();
			StringBuilder sbEmp = null;
			while(rs.next()){
				if(uF.parseToInt(rs.getString("emp_id")) > 0){
					if(!alSelectedEmp.contains(rs.getString("emp_id"))){
						alSelectedEmp.add(rs.getString("emp_id"));
						
						Map<String, String> hmEmp = new HashMap<String, String>();
						hmEmp.put("EMP_ID", rs.getString("emp_per_id"));
						hmEmp.put("EMP_CODE", uF.showData(rs.getString("empcode"), ""));
						
						//String strMiddleName=(rs.getString("emp_mname")!=null && !rs.getString("emp_mname").trim().equals("")) ? rs.getString("emp_mname").trim()+" " : "";
						
						String strEmpMName = "";
						if(flagMiddleName) {
							if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
								strEmpMName = " "+rs.getString("emp_mname");
							}
						}
						
						String strEmpName = rs.getString("emp_fname") +strEmpMName+ " " + rs.getString("emp_lname");
						hmEmp.put("EMP_NAME", uF.showData(strEmpName, ""));
						
						alSelectedEmpMap.add(hmEmp);
						
						if(sbEmp == null){
							sbEmp = new StringBuilder();
							sbEmp.append(","+rs.getString("emp_id")+",");
						} else {
							sbEmp.append(rs.getString("emp_id")+",");
						}
					}
				}
			}
			rs.close();
			pst.close();
			
			if(sbEmp == null){
				sbEmp = new StringBuilder();
			}
			
			request.setAttribute("alSelectedEmp", alSelectedEmp);
			request.setAttribute("alSelectedEmpMap", alSelectedEmpMap);
			request.setAttribute("sbEmp", sbEmp.toString());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private void insertWorkFlowPolicy() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		List<String> memIdList = (List<String>) request.getAttribute("memIdList");

		try {
			con = db.makeConnection(con);
			int policy_count = 0;
			pst = con.prepareStatement("select max(policy_count)as count from work_flow_policy ");
//			System.out.println("pst====>" + pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				policy_count = rs.getInt("count");
			}
			rs.close();
			pst.close();
			policy_count++;

//			System.out.println("anyOneType====>"+anyOneType);
			if(uF.parseToInt(getAnyOneType())<3){
				if (memIdList != null && memIdList.size() > 0) {
	
					for (int i = 0; i < memIdList.size(); i++) {
						String mem_id = memIdList.get(i);
						String mem_position = request.getParameter(mem_id.trim());
	//					System.out.println("mem_position====>" + mem_position);
	
						if(mem_position!=null){
							pst = con.prepareStatement("insert into work_flow_policy(work_flow_member_id,member_position,policy_type,"
											+ " trial_status,added_by,added_date,policy_count,policy_name,effective_date,org_id,location_id,policy_status,group_id)" +
													"values(?,?,?,?,?,?,?,?,?,?,?,?,?)");
							pst.setInt(1, uF.parseToInt(mem_id));
							pst.setDouble(2, uF.parseToDouble(mem_position));
							pst.setString(3, "1");
							pst.setInt(4, 1);
							pst.setInt(5, uF.parseToInt(strSessionEmpId));
							pst.setDate(6, uF.getCurrentDate(CF.getStrTimeZone()));
							pst.setInt(7, policy_count);
							pst.setString(8, getPolicyName());
							pst.setDate(9, null);
							pst.setString(10, getOrganization());
							pst.setString(11, getLocation());
							pst.setInt(12,1);
							pst.setInt(13,uF.parseToInt(getGroup_id()));
							pst.execute();
							pst.close();
						}
					}
				}
			}else{
					pst = con.prepareStatement("insert into work_flow_policy(work_flow_member_id,member_position,policy_type,"
							+ " trial_status,added_by,added_date,policy_count,policy_name,effective_date,org_id,location_id,policy_status,group_id)" +
									"values(?,?,?,?,?,?,?,?,?,?,?,?,?)");
					pst.setInt(1, uF.parseToInt(getAnyOneRegular()));
					pst.setDouble(2, 1);
					pst.setString(3, "1");
					pst.setInt(4, 1);
					pst.setInt(5, uF.parseToInt(strSessionEmpId));
					pst.setDate(6, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setInt(7, policy_count);
					pst.setString(8, getPolicyName());
					pst.setDate(9,null);
					pst.setString(10, getOrganization());
					pst.setString(11, getLocation());
					pst.setInt(12,1);
					pst.setInt(13,uF.parseToInt(getGroup_id()));
					int x = pst.executeUpdate();
					pst.close();
					
					if(x > 0){
						pst = con.prepareStatement("delete from specific_emp where policy_id=?");
						pst.setInt(1, policy_count);
						pst.execute();
						pst.close();
						
						if(getEmpselected()!=null && !getEmpselected().trim().equals("") && !getEmpselected().trim().equalsIgnoreCase("NULL")){
							List<String> alEmp = Arrays.asList(getEmpselected().split(","));
							if(alEmp == null) alEmp = new ArrayList<String>();
							for(int i=0; i<alEmp.size(); i++){
								int nEmpId = uF.parseToInt(alEmp.get(i).trim());
								if(nEmpId > 0){
									pst = con.prepareStatement("insert into specific_emp(emp_id,policy_id) values(?,?)");
									pst.setInt(1, nEmpId);
									pst.setInt(2, policy_count);
									pst.execute();
									pst.close();
								}
							}
						}
					}
				}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}

	private void getWorkFlowMember() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF=new UtilityFunctions();
		List<List<String>> outerList = new ArrayList<List<String>>();
		List<String> memIdList = new ArrayList<String>();
		int mem_count = 0;
		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			pst = con.prepareStatement("select * from work_flow_member where group_id=?");
			pst.setInt(1,uF.parseToInt(getGroup_id()));
			rs = pst.executeQuery();
			String anyOne=null;
			String anyOneType=null;
			while (rs.next()) {
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("work_flow_member_id"));
				innerList.add(rs.getString("work_flow_mem"));
				innerList.add(rs.getString("member_type"));
				innerList.add(rs.getString("member_id"));

				outerList.add(innerList);
				mem_count++;

				memIdList.add(rs.getString("work_flow_member_id"));
				if(rs.getString("member_type").equals("3")){
					anyOne=rs.getString("work_flow_member_id");
					anyOneType=rs.getString("member_type");
				}
			}
			rs.close();
			pst.close();
			request.setAttribute("memIdList", memIdList);
			request.setAttribute("outerList", outerList);
			request.setAttribute("mem_count", mem_count);
			request.setAttribute("anyOne", anyOne);
			request.setAttribute("anyOneType", anyOneType);

			if(uF.parseToInt(anyOneType) == 3){
				pst = con.prepareStatement("select * from employee_personal_details epd,employee_official_details eod " +
						"where epd.emp_per_id=eod.emp_id and epd.is_alive=true and eod.wlocation_id='"+getLocation()+"' " +
						"and eod.org_id='"+getOrganization()+"' order by epd.emp_fname,epd.emp_lname");
				rs = pst.executeQuery();
				List<Map<String, String>> alEmpList = new ArrayList<Map<String,String>>();
				while(rs.next()){
					Map<String, String> hmEmp = new HashMap<String, String>();
					hmEmp.put("EMP_ID", rs.getString("emp_per_id"));
					hmEmp.put("EMP_CODE", uF.showData(rs.getString("empcode"), ""));
					
					//String strMiddleName=(rs.getString("emp_mname")!=null && !rs.getString("emp_mname").trim().equals("")) ? rs.getString("emp_mname").trim()+" " : "";
					String strEmpMName = "";
					if(flagMiddleName) {
						if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
							strEmpMName = " "+rs.getString("emp_mname");
						}
					}
					
					String strEmpName = rs.getString("emp_fname") +strEmpMName+ " " +rs.getString("emp_lname");
					hmEmp.put("EMP_NAME", uF.showData(strEmpName, ""));
					
					alEmpList.add(hmEmp);
				}
				rs.close();
				pst.close();
				
				request.setAttribute("alEmpList", alEmpList);
				
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}

	public String getSubmit() {
		return submit;
	}

	public void setSubmit(String submit) {
		this.submit = submit;
	}

	public String getPolicy_type() {
		return policy_type;
	}

	public void setPolicy_type(String policy_type) {
		this.policy_type = policy_type;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public List<FillWLocation> getWorkList() {
		return workList;
	}

	public void setWorkList(List<FillWLocation> workList) {
		this.workList = workList;
	}

	public List<FillOrganisation> getOrganisationList() {
		return organisationList;
	}

	public void setOrganisationList(List<FillOrganisation> organisationList) {
		this.organisationList = organisationList;
	}

	public String getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(String effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getOrganization() {
		return organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}

	public String getPolicyName() {
		return policyName;
	}

	public void setPolicyName(String policyName) {
		this.policyName = policyName;
	}

	public String getPcount() {
		return pcount;
	}

	public void setPcount(String pcount) {
		this.pcount = pcount;
	}

	public String getPnameValue() {
		return pnameValue;
	}

	public void setPnameValue(String pnameValue) {
		this.pnameValue = pnameValue;
	}

	public String geteDatevalue() {
		return eDatevalue;
	}

	public void seteDatevalue(String eDatevalue) {
		this.eDatevalue = eDatevalue;
	}

	public String getLocationvalue() {
		return locationvalue;
	}

	public void setLocationvalue(String locationvalue) {
		this.locationvalue = locationvalue;
	}

	public String getOrganizationvalue() {
		return organizationvalue;
	}

	public void setOrganizationvalue(String organizationvalue) {
		this.organizationvalue = organizationvalue;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}

	public String getGroup_id() {
		return group_id;
	}

	public void setGroup_id(String group_id) {
		this.group_id = group_id;
	}

	public String getAnyOne() {
		return anyOne;
	}

	public void setAnyOne(String anyOne) {
		this.anyOne = anyOne;
	}

	public String getAnyOneRegular() {
		return anyOneRegular;
	}

	public void setAnyOneRegular(String anyOneRegular) {
		this.anyOneRegular = anyOneRegular;
	}

	public String getAnyOneContengency() {
		return anyOneContengency;
	}

	public void setAnyOneContengency(String anyOneContengency) {
		this.anyOneContengency = anyOneContengency;
	}

	public String getAnyOneType() {
		return anyOneType;
	}

	public void setAnyOneType(String anyOneType) {
		this.anyOneType = anyOneType;
	}

	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {

		this.request = request;
	}

	public String getEmpselected() {
		return empselected;
	}

	public void setEmpselected(String empselected) {
		this.empselected = empselected;
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
	
}
