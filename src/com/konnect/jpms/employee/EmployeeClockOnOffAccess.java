package com.konnect.jpms.employee;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class EmployeeClockOnOffAccess extends ActionSupport implements ServletRequestAware, IStatements {
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	boolean isEmpUserType = false;
	CommonFunctions CF = null;

	String strSessionEmpId = null;

	String []wLocation;
	String []level;
	String []f_department;
	String []f_service; 
	
	List<FillDepartment> departmentList;
	List<FillLevel> levelList;
	List<FillServices> serviceList;
	List<FillWLocation> wLocationList;

	String f_org;
	
	List<FillOrganisation> organisationList;
	
	String[] hideEmpIds;
	String btnUpdate;
	
	boolean isWebAccess;
	boolean isMobAccess;
	boolean isBioAccess;
	
	public String execute() throws Exception {

		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;
		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);

		request.setAttribute(TITLE, "Clock On/Off Access");
		request.setAttribute(PAGE, "/jsp/employee/EmployeeClockOnOffAccess.jsp");
		UtilityFunctions uF = new UtilityFunctions();
		if(getF_org()==null) {
			setF_org((String)session.getAttribute(ORGID));
		}
		StringBuilder sbpageTitleNaviTrail = new StringBuilder();
		sbpageTitleNaviTrail.append("<li><i class=\"fa fa-group\"></i><a href=\"People.action\" style=\"color: #3c8dbc;\"> People</a></li>" +
			"<li class=\"active\">People</li>");
		request.setAttribute("PAGETITLE_NAVITRAIL", sbpageTitleNaviTrail.toString());
		
		loadEmpData(uF);
//		System.out.println("getBtnUpdate() ===>> " + getBtnUpdate());
		if(getBtnUpdate() != null) {
			updateEmployeeAccess(uF);
		}
		getEmployeeData();
		
		return LOAD;

	}

	
	private void updateEmployeeAccess(UtilityFunctions uF) {

		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			con = db.makeConnection(con);
			
//			System.out.println("hideEmpIds ===>> " + hideEmpIds);
			for(int i=0; hideEmpIds!=null && i<hideEmpIds.length; i++) {
//				System.out.println("chboxWeb_ ===>> " + (String)request.getParameter("chboxWeb_"+hideEmpIds[i]));
				pst = con.prepareStatement("update emp_clock_on_off_access set is_web_access=?,is_mobile_access=?,is_biomatric_access=? where emp_id=?");
				pst.setBoolean(1, (uF.parseToInt((String)request.getParameter("chboxWeb_"+hideEmpIds[i]))>0) ? true : false);
				pst.setBoolean(2, (uF.parseToInt((String)request.getParameter("chboxMob_"+hideEmpIds[i]))>0) ? true : false);
				pst.setBoolean(3, (uF.parseToInt((String)request.getParameter("chboxBio_"+hideEmpIds[i]))>0) ? true : false);
				pst.setInt(4, uF.parseToInt(hideEmpIds[i]));
//				pst.addBatch();
//				System.out.println("pst ===>> " + pst);
				int x = pst.executeUpdate();
				pst.close();
				if(x==0) {
//					System.out.println("chboxWeb_ ===>> " + (String)request.getParameter("chboxWeb_"+hideEmpIds[i]));
					pst = con.prepareStatement("insert into emp_clock_on_off_access (emp_id,is_web_access,is_mobile_access,is_biomatric_access) values(?,?,?,?)");
					pst.setInt(1, uF.parseToInt(hideEmpIds[i]));
					pst.setBoolean(2, (uF.parseToInt((String)request.getParameter("chboxWeb_"+hideEmpIds[i]))>0) ? true : false);
					pst.setBoolean(3, (uF.parseToInt((String)request.getParameter("chboxMob_"+hideEmpIds[i]))>0) ? true : false);
					pst.setBoolean(4, (uF.parseToInt((String)request.getParameter("chboxBio_"+hideEmpIds[i]))>0) ? true : false);
//					System.out.println("pst ===>> " + pst);
					pst.executeUpdate();
					pst.close();
				}
			}
//			int[] x = pst.executeBatch();
			pst.close();
//			System.out.println("x ====>> " + x.length);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}


	public String loadEmpData(UtilityFunctions uF) {
//		paycycleList = new FillPayCycles(getStrPaycycleDuration()).fillPayCycles(CF); 
//		wLocationList = new FillWLocation().fillWLocation();
		departmentList = new FillDepartment(request).fillDepartment(uF.parseToInt(getF_org()));
		levelList = new FillLevel(request).fillLevel(uF.parseToInt(getF_org()));
		serviceList = new FillServices(request).fillServices(getF_org(),uF);
		
		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)) {
			wLocationList = new FillWLocation(request).fillWLocation(getF_org(),(String) session.getAttribute(WLOCATION_ACCESS));
			organisationList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
		} else {
			organisationList = new FillOrganisation(request).fillOrganisation();
			wLocationList = new FillWLocation(request).fillWLocation(getF_org());
		}
		
		getSelectedFilter(uF);
		
		return LOAD;
	}
	
	
	private void getSelectedFilter(UtilityFunctions uF) {
		Map<String,String> hmFilter=new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();
		
		System.out.println();
		alFilter.add("ORGANISATION");
		if(getF_org()!=null) {			
			String strOrg="";
			int k=0;
			for(int i=0;organisationList!=null && i<organisationList.size();i++) {
				if(getF_org().equals(organisationList.get(i).getOrgId())) {
					if(k==0) {
						strOrg=organisationList.get(i).getOrgName();
					} else {
						strOrg+=", "+organisationList.get(i).getOrgName();
					}
					k++;
				}
			}
			if(strOrg!=null && !strOrg.equals("")) {
				hmFilter.put("ORGANISATION", strOrg);
			} else {
				hmFilter.put("ORGANISATION", "All Organisation");
			}
		} else {
			hmFilter.put("ORGANISATION", "All Organisation");
		}
		
		
		alFilter.add("LOCATION");
		if(getwLocation()!=null) {
			String strLocation="";
			int k=0;
			for(int i=0;wLocationList!=null && i<wLocationList.size();i++) {
				for(int j=0;j<getwLocation().length;j++) {
					if(getwLocation()[j].equals(wLocationList.get(i).getwLocationId())) {
						if(k==0) {
							strLocation=wLocationList.get(i).getwLocationName();
						} else {
							strLocation+=", "+wLocationList.get(i).getwLocationName();
						}
						k++;
					}
				}
			}
			if(strLocation!=null && !strLocation.equals("")) {
				hmFilter.put("LOCATION", strLocation);
			} else {
				hmFilter.put("LOCATION", "All Locations");
			}
		} else {
			hmFilter.put("LOCATION", "All Locations");
		}
		
		alFilter.add("DEPARTMENT");
		if(getF_department()!=null) {
			String strDepartment="";
			int k=0;
			for(int i=0;departmentList!=null && i<departmentList.size();i++) {
				for(int j=0;j<getF_department().length;j++) {
					if(getF_department()[j].equals(departmentList.get(i).getDeptId())) {
						if(k==0) {
							strDepartment=departmentList.get(i).getDeptName();
						} else {
							strDepartment+=", "+departmentList.get(i).getDeptName();
						}
						k++;
					}
				}
			}
			if(strDepartment!=null && !strDepartment.equals("")) {
				hmFilter.put("DEPARTMENT", strDepartment);
			} else {
				hmFilter.put("DEPARTMENT", "All Departments");
			}
		} else {
			hmFilter.put("DEPARTMENT", "All Departments");
		}
		
		alFilter.add("SERVICE");
		if(getF_service()!=null) {
			String strService="";
			int k=0;
			for(int i=0;serviceList!=null && i<serviceList.size();i++) {
				for(int j=0;j<getF_service().length;j++) {
					if(getF_service()[j].equals(serviceList.get(i).getServiceId())) {
						if(k==0) {
							strService=serviceList.get(i).getServiceName();
						} else {
							strService+=", "+serviceList.get(i).getServiceName();
						}
						k++;
					}
				}
			}
			if(strService!=null && !strService.equals("")) {
				hmFilter.put("SERVICE", strService);
			} else {
				hmFilter.put("SERVICE", "All Services");
			}
		} else {
			hmFilter.put("SERVICE", "All Services");
		}
		
		alFilter.add("LEVEL");
		if(getLevel()!=null) {
			String strLevel="";
			int k=0;
			for(int i=0;levelList!=null && i<levelList.size();i++) {
				for(int j=0;j<getLevel().length;j++) {
					if(getLevel()[j].equals(levelList.get(i).getLevelId())) {
						if(k==0) {
							strLevel=levelList.get(i).getLevelCodeName();
						} else {
							strLevel+=", "+levelList.get(i).getLevelCodeName();
						}
						k++;
					}
				}
			}
			if(strLevel!=null && !strLevel.equals("")) {
				hmFilter.put("LEVEL", strLevel);
			} else {
				hmFilter.put("LEVEL", "All Levels");
			}
		} else {
			hmFilter.put("LEVEL", "All Levels");
		}
		
		String selectedFilter=CF.getSelectedFilter1(CF,uF,alFilter,hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}
	
	
	public void getEmployeeData() {

		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		PreparedStatement pst = null;
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();

		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			Map<String, List<String>> hmEmpData = new LinkedHashMap<String, List<String>>();
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id and is_alive = true and emp_per_id > 0 ");
			
			if(getLevel()!=null && getLevel().length>0) {
                sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getLevel(), ",")+") ) ");
            }
            if(getF_department()!=null && getF_department().length>0) {
                sbQuery.append(" and depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
            }
            
            if(getF_service()!=null && getF_service().length>0) {
                sbQuery.append(" and (");
                for(int i=0; i<getF_service().length; i++) {
                    sbQuery.append(" eod.service_id like '%,"+getF_service()[i]+",%'");
                    
                    if(i<getF_service().length-1) {
                        sbQuery.append(" OR "); 
                    }
                }
                sbQuery.append(" ) ");
            }
			if(getwLocation()!=null && getwLocation().length>0) {
                sbQuery.append(" and wlocation_id in ("+StringUtils.join(getwLocation(), ",")+") ");
            } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && session.getAttribute(WLOCATION_ACCESS)!=null) {
				sbQuery.append(" and wlocation_id in ("+session.getAttribute(WLOCATION_ACCESS)+")");
			}
            
			if(uF.parseToInt(getF_org())>0) {
				sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
			} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && session.getAttribute(ORG_ACCESS)!=null) {
				sbQuery.append(" and org_id in ("+session.getAttribute(ORG_ACCESS)+")");
			}
			sbQuery.append(" order by emp_fname, emp_lname"); 
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst=====>"+pst); 
			rs = pst.executeQuery();
			while(rs.next()) {
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("emp_per_id"));
				innerList.add(rs.getString("empcode"));
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				
				innerList.add(rs.getString("emp_fname")+strEmpMName+" "+rs.getString("emp_lname"));
				innerList.add(rs.getString("emp_image"));
				
				if(!getIsWebAccess()) {
					setWebAccess(true);
				}
				if(!getIsMobAccess()) {
					setMobAccess(true);
				}
				if(!getIsWebAccess()) {
					setBioAccess(true);
				}
				hmEmpData.put(rs.getString("emp_per_id"), innerList);
				
			}
			rs.close();
			pst.close();

			sbQuery = new StringBuilder();
			sbQuery.append("select * from emp_clock_on_off_access ");
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst=====>"+pst);
			Map<String, List<String>> hmEmpAccessData = new HashMap<String, List<String>>();
			rs = pst.executeQuery();
			while(rs.next()) {
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("is_web_access"));
				innerList.add(rs.getString("is_mobile_access"));
				innerList.add(rs.getString("is_biomatric_access"));
				
				hmEmpAccessData.put(rs.getString("emp_id"), innerList);
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmEmpAccessData", hmEmpAccessData);
			request.setAttribute("hmEmpData", hmEmpData);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}


	public String[] getwLocation() {
		return wLocation;
	}

	public void setwLocation(String[] wLocation) {
		this.wLocation = wLocation;
	}

	public String[] getLevel() {
		return level;
	}

	public void setLevel(String[] level) {
		this.level = level;
	}

	public String[] getF_department() {
		return f_department;
	}

	public void setF_department(String[] f_department) {
		this.f_department = f_department;
	}

	public String[] getF_service() {
		return f_service;
	}

	public void setF_service(String[] f_service) {
		this.f_service = f_service;
	}

	public List<FillDepartment> getDepartmentList() {
		return departmentList;
	}

	public void setDepartmentList(List<FillDepartment> departmentList) {
		this.departmentList = departmentList;
	}

	public List<FillLevel> getLevelList() {
		return levelList;
	}

	public void setLevelList(List<FillLevel> levelList) {
		this.levelList = levelList;
	}

	public List<FillServices> getServiceList() {
		return serviceList;
	}

	public void setServiceList(List<FillServices> serviceList) {
		this.serviceList = serviceList;
	}

	public List<FillWLocation> getwLocationList() {
		return wLocationList;
	}

	public void setwLocationList(List<FillWLocation> wLocationList) {
		this.wLocationList = wLocationList;
	}

	public String getF_org() {
		return f_org;
	}

	public void setF_org(String f_org) {
		this.f_org = f_org;
	}

	public List<FillOrganisation> getOrganisationList() {
		return organisationList;
	}

	public void setOrganisationList(List<FillOrganisation> organisationList) {
		this.organisationList = organisationList;
	}

	public String[] getHideEmpIds() {
		return hideEmpIds;
	}

	public void setHideEmpIds(String[] hideEmpIds) {
		this.hideEmpIds = hideEmpIds;
	}

	public String getBtnUpdate() {
		return btnUpdate;
	}

	public void setBtnUpdate(String btnUpdate) {
		this.btnUpdate = btnUpdate;
	}

	public boolean getIsWebAccess() {
		return isWebAccess;
	}

	public void setWebAccess(boolean isWebAccess) {
		this.isWebAccess = isWebAccess;
	}

	public boolean getIsMobAccess() {
		return isMobAccess;
	}

	public void setMobAccess(boolean isMobAccess) {
		this.isMobAccess = isMobAccess;
	}

	public boolean getIsBioAccess() {
		return isBioAccess;
	}

	public void setBioAccess(boolean isBioAccess) {
		this.isBioAccess = isBioAccess;
	}


	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

}
