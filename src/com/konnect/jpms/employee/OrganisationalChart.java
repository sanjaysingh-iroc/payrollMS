package com.konnect.jpms.employee;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class OrganisationalChart  extends ActionSupport implements ServletRequestAware, IStatements  {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	CommonFunctions CF;
	String strEmpId;
	String strUserType;
	
	private static Logger log = Logger.getLogger(OrganisationalChart.class);
	
	private String orgId;
	private List<FillOrganisation> organisationList;
	
	private String strBaseUserType;
	private String dataType;
	private String productType;
	private String fromPage;
	private String divResult;
	
	private String alertID;
	
	public String execute() throws Exception { 
		
	//	System.out.println("hii in OrganisationalChart");
		
	//	System.out.println("fromPage==>"+fromPage);
		session = request.getSession(true);
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		productType = (String)session.getAttribute(PRODUCT_TYPE);
		strBaseUserType = (String)session.getAttribute(BASEUSERTYPE);
//		System.out.println("productType java ===>> " + productType);
		
		if(getProductType() != null && getProductType().equals("3")) {
			strUserType = MANAGER;
		} else {
			strUserType = (String)session.getAttribute(USERTYPE);
			if(getDataType()==null && strUserType != null && strUserType.equals(MANAGER)) {
				setDataType("MYTEAM");
			}
		}
		String strDomain = request.getServerName().split("\\.")[0];
		CF.deleteWRUserAlerts(CF, request, strDomain, getAlertID());
		
//		System.out.println("strEmpId java ===>> " + strEmpId);
//		System.out.println("orgId java ===>> " + orgId);
		if(getStrEmpId() == null) {
			strEmpId = (String)session.getAttribute(EMPID);
		}
//		System.out.println("getDivResult() 0 ===>> "  +getDivResult());
		if(getDivResult() == null || getDivResult().equalsIgnoreCase("null") || getDivResult().equals("")) {
			setDivResult("divResult");
		}
//		System.out.println("getDivResult() ===>> "  +getDivResult());
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
//		System.out.println("strEmpId ===> " + strEmpId);
//		System.out.println("fromPage ===> " + fromPage);
//		System.out.println("L ===> " + request.getParameter("l"));
//		System.out.println("M ===> " + request.getParameter("m"));
//		System.out.println("D ===> " + request.getParameter("d"));
//		System.out.println("T ===> " + request.getParameter("t"));
		
		if(getFromPage() == null || (getFromPage() != null && !getFromPage().equals("TS"))) {
			StringBuilder sbpageTitleNaviTrail = new StringBuilder();
			if(strUserType != null && strUserType.equals(MANAGER)) {
				sbpageTitleNaviTrail.append("<li><i class=\"fa fa-group\"></i><a href=\"OrganisationalChart.action\" style=\"color: #3c8dbc;\"> Team</a></li>" +
				"<li class=\"active\">Team Structure</li>");
			} else if(strUserType != null && strUserType.equals(EMPLOYEE)) {
				sbpageTitleNaviTrail.append("<li><i class=\"fa fa-group\"></i><a href=\"MyHR.action\" style=\"color: #3c8dbc;\"> My HR</a></li>" +
				"<li class=\"active\">Position</li>");
			} else {
				sbpageTitleNaviTrail.append("<li><i class=\"fa fa-group\"></i><a href=\"People.action\" style=\"color: #3c8dbc;\"> People</a></li>" +
				"<li class=\"active\">Organisation Chart</li>");
			}
			request.setAttribute("PAGETITLE_NAVITRAIL", sbpageTitleNaviTrail.toString());
		
		}
		
		if(request.getParameter("l")!=null) {
			//	request.setAttribute(PAGE, POrganisationalChart1);
				request.setAttribute(PAGE, "/jsp/employee/OrganisationalChart4.jsp");
				request.setAttribute(TITLE, TLevelChart);
				getLevelLocation();
		} else if(request.getParameter("m")!=null) {
			//	request.setAttribute(PAGE, POrganisationalChart1); 
				request.setAttribute(PAGE, "/jsp/employee/OrganisationalChart3.jsp");
				request.setAttribute(TITLE, TOrganisationalChart);
				getLocationsforMap();
		} else if(request.getParameter("d")!=null) {
			//	request.setAttribute(PAGE, POrganisationalChart1);
				request.setAttribute(PAGE, "/jsp/employee/OrganisationalChart2.jsp");
				request.setAttribute(TITLE, TDepartmentChart);
				getDepartmentLocation();
		} else if(request.getParameter("t")!=null) {
		//	request.setAttribute(PAGE, POrganisationalChart1);
			request.setAttribute(PAGE, "/jsp/employee/OrganisationalChart1.jsp");
			request.setAttribute(TITLE, TOrganisationalChart);
			getAllEmployees();
		} else {
			request.setAttribute(PAGE, POrganisationalChart);
			request.setAttribute(TITLE, THierarchicalChart);
			organisationList = new FillOrganisation(request).fillOrganisation();
//			System.out.println("getOrgId() ===>> " + getOrgId());
			if(getOrgId() == null || getOrgId().equals("")) {
				setOrgId((String)session.getAttribute(ORGID));
//				setOrgId("8");
			}
			if(strUserType != null && strUserType.equalsIgnoreCase(EMPLOYEE)) {
				getHireracyLevels();
				getPosition();
			} else {
				getHireracyLevels();
			}
		}
			
		if(getFromPage() != null && (getFromPage().equals("MP") || getFromPage().equals("TS"))){
	//		System.out.println("getFromPage==>"+getFromPage());
	//		System.out.println("orgId==>"+orgId);
			
			if(request.getParameter("l")!=null) {
					return "chart4";
			} else if(request.getParameter("m")!=null) {

					return "chart3";
			} else if(request.getParameter("d")!=null) {

					return "chart2";
			} else if(request.getParameter("t")!=null) {

				return "chart1";
			} else {
				
				return LOAD;
			}
		}
		return SUCCESS;
	}
	 
	private void getSelectedFilter(UtilityFunctions uF) {
		Map<String,String> hmFilter=new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();
		
		if((strUserType != null && !strUserType.equals(MANAGER)) || (getDataType() != null && getDataType().equals(strBaseUserType))) {
			alFilter.add("ORGANISATION");
			if(getOrgId()!=null) {
				String strOrg="";
				for(int i=0;organisationList!=null && i<organisationList.size();i++) {
					if(getOrgId().equals(organisationList.get(i).getOrgId())) {
						strOrg = organisationList.get(i).getOrgName();
					}
				}
				if(strOrg!=null && !strOrg.equals("")) {
					hmFilter.put("ORGANISATION", strOrg);
				} else {
					hmFilter.put("ORGANISATION", "All Organisations");
				}
			} else {
				hmFilter.put("ORGANISATION", "All Organisations");
			}
		}
		
		String selectedFilter= CF.getSelectedFilter(CF, uF, alFilter, hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}
	

	
	public void getPosition() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		StringBuilder sbPosition = new StringBuilder();
//		StringBuilder sbEmpIds = new StringBuilder();
		UtilityFunctions uF = new UtilityFunctions();
		
//		System.out.println("in getPosition ====>> ");
		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			
			Map<String, String> hmEmpNames = new HashMap<String, String>();
			Map<String, String> hmEmpImages = new HashMap<String, String>();
			
			pst = con.prepareStatement("SELECT eod.supervisor_emp_id, eod.grade_id,eod.org_id FROM employee_personal_details epd, employee_official_details eod " +
				" where is_alive= true and emp_id = ? ");
			pst.setInt(1, uF.parseToInt(strEmpId));
			rs = pst.executeQuery();
			String strSuprvisorId = null;
			String strGradeId = null;
			String strOrgId = null;
			while(rs.next()) {
				strSuprvisorId = rs.getString("supervisor_emp_id");
				strGradeId = rs.getString("grade_id");
				strOrgId = rs.getString("org_id");
//				sbEmpIds.append(rs.getString("supervisor_emp_id")+",");
			}
//			System.out.println("strSuprvisorId ===>> " + strSuprvisorId);
//			System.out.println("strGradeId ===>> " + strGradeId);
//			System.out.println("strOrgId ===>> " + strOrgId);
			rs.close();
			pst.close();
			
			
			if(uF.parseToInt(strSuprvisorId)==0) {
				pst = con.prepareStatement("select * from org_details where org_id=?");
				pst.setInt(1, uF.parseToInt(strOrgId));
				
				rs = pst.executeQuery();
				while(rs.next()) {
					hmEmpNames.put("0", rs.getString("org_name"));
//					hmEmpImages.put("0", rs.getString("org_logo"));
					String fileName = "";
					if(rs.getString("org_logo")!=null && !rs.getString("org_logo").equalsIgnoreCase("avatar_photo.png")) {
						if(CF.getStrDocSaveLocation()==null) {
							fileName = DOCUMENT_LOCATION+rs.getString("org_logo");
						} else {
							File f = new File(CF.getStrDocSaveLocation()+I_ORGANISATION+"/"+I_IMAGE+"/"+I_60x60+"/"+rs.getString("org_logo"));
					        if (f.exists()) {
					        	fileName = CF.getStrDocRetriveLocation()+I_ORGANISATION+"/"+I_IMAGE+"/"+I_60x60+"/"+rs.getString("org_logo");
					        } else {
					        	fileName = "userImages/avatar_photo.png";
					        }
						}	
					} else {
						fileName = "userImages/avatar_photo.png";
					}
					hmEmpImages.put("0", fileName);
				}
//				System.out.println("uF.parseToInt(strSuprvisorId)==0 hmEmpImages ===>> " + hmEmpImages);
				rs.close();
				pst.close();
			}
			
			
			pst = con.prepareStatement("SELECT emp_id,supervisor_emp_id FROM employee_personal_details epd, employee_official_details eod where is_alive= true and " +
				"eod.emp_id = epd.emp_per_id and eod.grade_id > 0 and eod.supervisor_emp_id = ? and eod.emp_id not in (?) and eod.org_id=? " + //and eod.grade_id = ? 
				"and eod.emp_id > 0 limit 2");
//			pst.setInt(1, uF.parseToInt(strGradeId));
			pst.setInt(1, uF.parseToInt(strSuprvisorId));
			pst.setInt(2, uF.parseToInt(strEmpId));
			pst.setInt(3, uF.parseToInt(strOrgId));
//			System.out.println("pst ===>> " + pst);
			rs = pst.executeQuery();
			List<String> alPeer = new ArrayList<String>();
			while(rs.next()) {
				if(uF.parseToInt(rs.getString("supervisor_emp_id")) == uF.parseToInt(rs.getString("emp_id"))) {
					continue;
				}
				alPeer.add(rs.getString("emp_id"));
//				sbEmpIds.append(rs.getString("emp_id")+",");
			}
//			System.out.println("alPeer ===>> " + alPeer);
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select emp_per_id,emp_fname,emp_lname,emp_image,supervisor_emp_id from employee_personal_details epd join " +
	        		"employee_official_details eod on (epd.emp_per_id = eod.emp_id) join department_info di on (depart_id=dept_id) " +
	        		"join grades_details gd on eod.grade_id=gd.grade_id join designation_details dd on gd.designation_id=dd.designation_id " +
	        		"join level_details ld on dd.level_id=ld.level_id join org_details od  on ld.org_id=od.org_id where is_alive= true " +
	        		" and emp_per_id >0 and supervisor_emp_id = ? order by emp_id"); // limit 2
			pst.setInt(1, uF.parseToInt(strEmpId));
	//		System.out.println("pst ===> " + pst);
			rs = pst.executeQuery();
			List<String> alSub = new ArrayList<String>();
			while(rs.next()) {
				if(uF.parseToInt(rs.getString("supervisor_emp_id")) == uF.parseToInt(rs.getString("emp_per_id"))) {
					continue;
				}
				alSub.add(rs.getString("emp_per_id"));
//				sbEmpIds.append(rs.getString("emp_per_id")+",");
			}
//			System.out.println("alSub ===>> " + alSub);
			rs.close();
			pst.close();
//			sbEmpIds.append((String)session.getAttribute(EMPID));

//			System.out.println("alPeer ===>> " + alPeer);
//			System.out.println("alSub ===>> " + alSub);
			
//			pst = con.prepareStatement("select * from employee_personal_details where emp_per_id in ("+sbEmpIds.toString()+")");
			pst = con.prepareStatement("select * from employee_personal_details where is_alive= true ");
			rs = pst.executeQuery();
			while(rs.next()) {
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				
				hmEmpNames.put(rs.getString("emp_per_id"), rs.getString("emp_fname")+strEmpMName+" "+rs.getString("emp_lname"));
//				hmEmpImages.put(rs.getString("emp_per_id"), rs.getString("emp_image"));
				String fileName = "";
				if(rs.getString("emp_image")!=null && !rs.getString("emp_image").equalsIgnoreCase("avatar_photo.png") && !rs.getString("emp_image").equalsIgnoreCase("")) {
					if(CF.getStrDocSaveLocation()==null) {
						fileName = DOCUMENT_LOCATION+rs.getString("emp_image");
					} else {
						File f = new File(CF.getStrDocSaveLocation()+I_PEOPLE+"/"+I_IMAGE+"/"+rs.getString("emp_per_id")+"/"+I_60x60+"/"+rs.getString("emp_image"));
				        if (f.exists()) {
				        	fileName = CF.getStrDocRetriveLocation()+I_PEOPLE+"/"+I_IMAGE+"/"+rs.getString("emp_per_id")+"/"+I_60x60+"/"+rs.getString("emp_image");
				        } else {
				        	fileName = "userImages/avatar_photo.png";
				        }
					}
				} else {
					fileName = "userImages/avatar_photo.png";
				}
				/*if(uF.parseToInt(rs.getString("emp_per_id"))==602) {
					System.out.println(rs.getString("emp_per_id") + " -- fileName ===>> " + fileName);
				}*/
				hmEmpImages.put(rs.getString("emp_per_id"), fileName);
			}
			rs.close();
			pst.close();
//			System.out.println("hmEmpImages ===>> " + hmEmpImages);
			
			Map<String, List<String>> hmHireracyLevels = (Map<String, List<String>>) request.getAttribute("hmHireracyLevels"); 
			List<String> al = (List<String>) hmHireracyLevels.get(strSuprvisorId); 
			List<String> empIDList1 = new ArrayList<String>();
			List<String> cntList1 = new ArrayList<String>();
			
			if(strSuprvisorId != null && !strSuprvisorId.trim().equals("")) {
//				System.out.println("in strSuprvisorId != null ====>> ");
				cntList1 = getChildCount(hmHireracyLevels, strSuprvisorId.trim(), empIDList1, null);
			}
//			String empImg = (hmEmpImages.get(strSuprvisorId) != null && !hmEmpImages.get(strSuprvisorId).equals("avatar_photo.png")) ? CF.getStrDocRetriveLocation()+hmEmpImages.get(strSuprvisorId) : "userImages/avatar_photo.png";
			String empImg1 = hmEmpImages.get(strSuprvisorId) != null ? hmEmpImages.get(strSuprvisorId) : "userImages/avatar_photo.png";
			sbPosition.append("<li>"+"<img class=\"lazy\" src=\""+ empImg1 +"\" width=\"40px\"><div class=\"emp\" style=\"margin-top: 5px;\" id=\""+strSuprvisorId+"\">"+uF.showData(hmEmpNames.get(strSuprvisorId), "N/A")+"</div>");
			sbPosition.append("<div class=\"desg_tree\" style=\"font-size: 9.5px; min-width: 85px; border-top: 1px solid #CCC;\"><span style=\"float: left\">Direct(");
			sbPosition.append(al != null ? al.size() : "0");
			sbPosition.append(")</span><span style=\"float: right\">Total(");
			sbPosition.append(cntList1 != null ? cntList1.size() : "0");
			sbPosition.append(")</span></div>");
			   
			sbPosition.append("");  
			sbPosition.append("<ul>");
			
			if(alPeer!=null && alPeer.size()>0) {
//				empImg = (hmEmpImages.get(alPeer.get(0)) != null && !hmEmpImages.get(alPeer.get(0)).equals("avatar_photo.png")) ? CF.getStrDocRetriveLocation()+hmEmpImages.get(alPeer.get(0)) : "userImages/avatar_photo.png";
				String empImg = hmEmpImages.get(alPeer.get(0)) != null ? hmEmpImages.get(alPeer.get(0)) : "userImages/avatar_photo.png";
				
				al = (List<String>) hmHireracyLevels.get(alPeer.get(0)); 
				empIDList1 = new ArrayList<String>();
				if(alPeer.get(0) != null && !alPeer.get(0).trim().equals("")) {
//					System.out.println("in alPeer.get(0) != null ====>> ");
					cntList1 = getChildCount(hmHireracyLevels, alPeer.get(0).trim(), empIDList1, null);
				}
//				sbPosition.append("<li>"+"<img class=\"lazy\" src=\"userImages/"+hmEmpImages.get(alPeer.get(0))+"\" width=\"22px\"><br/>"+uF.showData(hmEmpNames.get(alPeer.get(0)), "N/A")+"</li>");
				sbPosition.append("<li>"+"<img class=\"lazy\" src=\"" + empImg + "\" width=\"40px\"><div class=\"emp\" style=\"margin-top: 5px;\" id=\""+alPeer.get(0)+"\">"+uF.showData(hmEmpNames.get(alPeer.get(0)), "N/A")+"</div>");
				sbPosition.append("<div class=\"desg_tree\" style=\"font-size: 9.5px; min-width: 85px; border-top: 1px solid #CCC;\"><span style=\"float: left\">Direct(");
				sbPosition.append(al != null ? al.size() : "0");
				sbPosition.append(")</span><span style=\"float: right\">Total(");
				sbPosition.append(cntList1 != null ? cntList1.size() : "0");
				sbPosition.append(")</span></div>");
				sbPosition.append("</li>");
				
			}
			
//			empImg = (hmEmpImages.get(strEmpId) != null && !hmEmpImages.get(strEmpId).equals("avatar_photo.png")) ? CF.getStrDocRetriveLocation()+hmEmpImages.get(strEmpId) : "userImages/avatar_photo.png";
			String empImg11 = hmEmpImages.get(strEmpId) != null ? hmEmpImages.get(strEmpId) : "userImages/avatar_photo.png";
			
			al = (List<String>) hmHireracyLevels.get(strEmpId); 
			empIDList1 = new ArrayList<String>();
			if(strEmpId != null && !strEmpId.trim().equals("")) {
//				System.out.println("in strEmpId != null ====>> ");
				cntList1 = getChildCount(hmHireracyLevels, strEmpId.trim(), empIDList1, null);
			}	
			sbPosition.append("<li>"+"<img class=\"lazy\" src=\"" + empImg11 + "\" width=\"40px\"><div class=\"emp\" style=\"margin-top: 5px;\" id=\""+strEmpId+"\">You</div>");
			sbPosition.append("<div class=\"desg_tree\" style=\"font-size: 9.5px; min-width: 85px; border-top: 1px solid #CCC;\"><span style=\"float: left\">Direct(");
			sbPosition.append(al != null ? al.size() : "0");
			sbPosition.append(")</span><span style=\"float: right\">Total(");
			sbPosition.append(cntList1 != null ? cntList1.size() : "0");
			sbPosition.append(")</span></div>");
			
			sbPosition.append("<ul>");
			for(int i=0; i<alSub.size(); i++) {
//				empImg = (hmEmpImages.get(alSub.get(i)) != null && !hmEmpImages.get(alSub.get(i)).equals("avatar_photo.png")) ? CF.getStrDocRetriveLocation()+hmEmpImages.get(alSub.get(i)) : "userImages/avatar_photo.png";
				String empImg = hmEmpImages.get(alSub.get(i)) != null ? hmEmpImages.get(alSub.get(i)) : "userImages/avatar_photo.png";
				
				al = (List<String>) hmHireracyLevels.get(alSub.get(i)); 
				empIDList1 = new ArrayList<String>();
				if(alSub.get(i) != null && !alSub.get(i).trim().equals("")) {
//					System.out.println("in alSub.get(i) != null ====>> ");
					cntList1 = getChildCount(hmHireracyLevels, alSub.get(i).trim(), empIDList1, null);
				}
//				sbPosition.append("<li>"+"<img class=\"lazy\" src=\"userImages/"+hmEmpImages.get(alSub.get(i))+"\" width=\"22px\"><br/>"+uF.showData(hmEmpNames.get(alSub.get(i)), "N/A")+"</li>");
				sbPosition.append("<li>"+"<img class=\"lazy\" src=\"" + empImg + "\" width=\"40px\"><div class=\"emp\" style=\"margin-top: 5px;\" id=\""+alSub.get(i)+"\">"+uF.showData(hmEmpNames.get(alSub.get(i)), "N/A")+"</div>");
				sbPosition.append("<div class=\"desg_tree\" style=\"font-size: 9.5px; min-width: 85px; border-top: 1px solid #CCC;\"><span style=\"float: left\">Direct(");
				sbPosition.append(al != null ? al.size() : "0");
				sbPosition.append(")</span><span style=\"float: right\">Total(");
				sbPosition.append(cntList1 != null ? cntList1.size() : "0");
				sbPosition.append(")</span></div>");
				sbPosition.append("</li>");
			}
			sbPosition.append("</ul>");
			sbPosition.append("</li>");
			
			if(alPeer!=null && alPeer.size()>1) {
//				System.out.println(alPeer.get(1)+ " -- hmEmpImages.get(alPeer.get(1)) ===>> " + hmEmpImages.get(alPeer.get(1)));
				String empImg = (hmEmpImages.get(alPeer.get(1)) != null && !hmEmpImages.get(alPeer.get(1)).equals("avatar_photo.png")) ? hmEmpImages.get(alPeer.get(1)) : "userImages/avatar_photo.png";
				al = (List<String>) hmHireracyLevels.get(alPeer.get(1)); 
				empIDList1 = new ArrayList<String>();
				if(alPeer.get(1) != null && !alPeer.get(1).trim().equals("")) {
//					System.out.println("in alPeer.get(1) != null ====>> ");
					cntList1 = getChildCount(hmHireracyLevels, alPeer.get(1).trim(), empIDList1, null);
				}
//				sbPosition.append("<li>"+"<img class=\"lazy\" src=\"userImages/"+hmEmpImages.get(alPeer.get(1))+"\" width=\"22px\"><br/>"+uF.showData(hmEmpNames.get(alPeer.get(1)), "N/A")+"</li>");
				sbPosition.append("<li>"+"<img class=\"lazy\" src=\"" + empImg + "\" width=\"40px\"><div class=\"emp\" style=\"margin-top: 5px;\" id=\""+alPeer.get(1)+"\">"+uF.showData(hmEmpNames.get(alPeer.get(1)), "N/A")+"</div>");
				sbPosition.append("<div class=\"desg_tree\" style=\"font-size: 9.5px; min-width: 85px; border-top: 1px solid #CCC;\"><span style=\"float: left\">Direct(");
				sbPosition.append(al != null ? al.size() : "0");
				sbPosition.append(")</span><span style=\"float: right\">Total(");
				sbPosition.append(cntList1 != null ? cntList1.size() : "0");
				sbPosition.append(")</span></div>");
				sbPosition.append("</li>");
			}
			
			sbPosition.append("</ul>");			
			sbPosition.append("</li>");
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		//System.out.println("sbPosition in organization_java===>> " + sbPosition);
		request.setAttribute("sbPosition", sbPosition);
	}
	
	
	
	private void getEmployeeChieldEmpCount(Map<String, List<String>> hmHireracyLevels, List<String> alHireracyLevels, List<String> alChain) {
		try {
			
			Map<String, String> hmChieldEmpCnt = new HashMap<String, String>();
			List<String> empIDList = new ArrayList<String>();
//			System.out.println("in getEmployeeChieldEmpCount ====>> ");
			List<String> cntList = getChildCount(hmHireracyLevels, "0", empIDList, alChain);
			hmChieldEmpCnt.put("0", cntList.size()+"");
//			System.out.println("empIDList ===>> " + empIDList + " empIDList.size ===>> " + empIDList.size());
			for(int i= 0; alHireracyLevels != null && !alHireracyLevels.isEmpty() && i<alHireracyLevels.size(); i++) {
				/*if(strUserType!=null && alChain!=null && (strUserType.equalsIgnoreCase(IConstants.EMPLOYEE) || strUserType.equalsIgnoreCase(IConstants.MANAGER)) && !alChain.contains(alHireracyLevels.get(i))) {
					continue;
				}*/
				List<String> empIDList1 = new ArrayList<String>();
				List<String> cntList1 = new ArrayList<String>();
				if(alHireracyLevels.get(i) != null && !alHireracyLevels.get(i).trim().equals("")) {
//					System.out.println("hmHireracyLevels ===>> " + hmHireracyLevels);
//					System.out.println("in alHireracyLevels.get(i) != null ===== ===== >> " + alHireracyLevels.get(i));
					cntList1 = getChildCount(hmHireracyLevels, alHireracyLevels.get(i), empIDList1, alChain);
				}
//				System.out.println("empIDList1 ===>> " + empIDList1 + " empIDList1.size ===>> " + empIDList1.size());
				hmChieldEmpCnt.put(alHireracyLevels.get(i), cntList1.size()+"");
			}
//			System.out.println("hmHireracyLevels 1 ===>> " + hmHireracyLevels);
//			System.out.println("alHireracyLevels 1===>> " + alHireracyLevels);
//			System.out.println("alChain 1 ===>> " + alChain);
//			System.out.println("hmChieldEmpCnt ===>> " + hmChieldEmpCnt);
			request.setAttribute("hmChieldEmpCnt", hmChieldEmpCnt);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		}
	}
	 
	
	public List<String> getChildCount(Map<String, List<String>> hmHireracyLevels, String empId, List<String> empIDList, List<String> alChain) {

//		System.out.println(" in getChildCount =====>> ");
//		System.out.println("ChildCount hmHireracyLevels ===>>>> " + hmHireracyLevels);
		if(empId != null && !empId.trim().equals("")) {
//			empId = "0";
			if(hmHireracyLevels == null) hmHireracyLevels = new HashMap<String, List<String>>();
			List<String> innerList = (List<String>)hmHireracyLevels.get(empId.trim());
	
			for(int i= 0; innerList != null && !innerList.isEmpty() && i<innerList.size(); i++) {
				String empId1 = innerList.get(i);
				/*if(strUserType!=null && alChain!=null && (strUserType.equalsIgnoreCase(IConstants.EMPLOYEE) || strUserType.equalsIgnoreCase(IConstants.MANAGER)) && !alChain.contains(empId1)) {
					continue;
				}*/
				if(empId1 != null && !empId1.trim().equals("") && !empIDList.contains(empId1.trim())) {
					empIDList.add(empId1.trim());
				}
				if(empId1 != null && !empId1.trim().equals("")) {
//					System.out.println(" in getChildCount in =====>> ");
					getChildCount(hmHireracyLevels, empId1, empIDList, alChain);
				}
			}
		}
		return empIDList;
	}
	
	
	
	public void getAllEmployees() {
		PreparedStatement pst = null;
		ResultSet rs = null;
		Connection con = null;
		UtilityFunctions uF = new UtilityFunctions();
		Database db = new Database();
		db.setRequest(request);
		try {
    
//			System.out.println("in getAllEmployees ...........");
			con = db.makeConnection(con);

			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			
			// colour Logic for Department .....
			
			
			///	itemC.itemTitleColor = primitives.common.Colors.Yellow;
			String colourArray[]={"Pink" ,"DarkCyan" ,"Gold", "Indigo", "Limegreen", "Orange", "Olive" };
//			,"LightSeaGreen",  "DarkSeaGreen" , "Turquoise" , "LightSteelBlue",  "BurlyWood" , "Goldenrod" };
			Map<String,String> hmDeptColourCode=new HashMap<String, String>();
			pst=con.prepareStatement("select dept_id from department_info");
	        rs=pst.executeQuery();
			int colourCount=0;
	        while(rs.next()) {
				
	        	if(colourCount>colourArray.length-1)
					colourCount=0;
				
	        	hmDeptColourCode.put(rs.getString("dept_id"),colourArray[colourCount]);

				colourCount++;	
			}
			rs.close();
			pst.close();
			
	        colourCount=0;
			String colourArrayLevel[]={ "LightSeaGreen",  "DarkSeaGreen" , "Turquoise" , "LightSteelBlue",  "BurlyWood" , "Goldenrod" };
					Map<String,String> hmLevelColourCode=new HashMap<String, String>();
					pst=con.prepareStatement("select level_id from level_details");
			        rs=pst.executeQuery();
					int colourCount1=0;
			        while(rs.next()) {
						
			        	if(colourCount1>colourArrayLevel.length-1)
							colourCount1=0;
						
			        	hmLevelColourCode.put(rs.getString("level_id"),colourArrayLevel[colourCount1]);

						colourCount1++;	
					}
	        
	        
	        pst = con.prepareStatement("select od.org_id,org_name,org_city,org_logo,level_name,designation_name,emp_fname,emp_mname,emp_lname," +
	        		" depart_id,supervisor_emp_id,emp_id,dept_name,dd.designation_id,ld.level_id from employee_personal_details epd " +
	        		" join  employee_official_details eod on (epd.emp_per_id = eod.emp_id) join department_info di on (depart_id=dept_id) " +
	        		" join grades_details gd on eod.grade_id=gd.grade_id join designation_details dd on gd.designation_id=dd.designation_id " +
	        		" join level_details ld on dd.level_id=ld.level_id join org_details od on ld.org_id=od.org_id where is_alive= true " +
	        		" and emp_per_id >0 order by supervisor_emp_id,emp_id "); 
			rs = pst.executeQuery();
			
			Map<String,List<String>> hmHireracyLevels = new LinkedHashMap<String, List<String>>();
			Map<String, String> hmEmpSuperMap = new LinkedHashMap<String, String>();
			Map<String, String> hmEmpOrgName = new LinkedHashMap<String, String>();
			Map<String, List<String>> hmOrganisations = new LinkedHashMap<String, List<String>>();
/*			List<String> alHireracyLevels = new ArrayList();;
			List alInner = new ArrayList();*/
			Map<String, String> hmEmpDepartmentMap=new HashMap<String, String>();
			Map<String, String> hmEmpNameMap=new HashMap<String, String>();
			Map<String, String> hmEmpDesigMap=new HashMap<String, String>();
			Map<String, String> hmEmpLevelMap=new HashMap<String, String>();
			Map<String, String> hmEmpDepartmentColourMap=new HashMap<String, String>();
			Map<String, String> hmEmpLevelColourMap=new HashMap<String, String>();
			List<String> alInnerList = null;
			while(rs.next()) {
				
				alInnerList= hmHireracyLevels.get(rs.getString("supervisor_emp_id"));
				if(alInnerList==null)
					alInnerList=new ArrayList<String>();
				
				if(uF.parseToInt(rs.getString("supervisor_emp_id")) == uF.parseToInt(rs.getString("emp_id"))) {
					continue;
				}
				
				alInnerList.add(rs.getString("emp_id"));
				
				hmHireracyLevels.put(rs.getString("supervisor_emp_id"), alInnerList);
				
				hmEmpSuperMap.put(rs.getString("emp_id"), rs.getString("supervisor_emp_id"));
				hmEmpOrgName.put(rs.getString("emp_id"),rs.getString("org_id"));

				if(!hmOrganisations.keySet().contains((rs.getString("org_id")))) {
					List<String> alInner=new ArrayList<String>();
					alInner.add(rs.getString("org_name"));
					alInner.add(rs.getString("org_city"));
					alInner.add(rs.getString("org_logo"));
					
					hmOrganisations.put(rs.getString("org_id"),alInner);
				}
				
				hmEmpDepartmentColourMap.put(rs.getString("emp_id"),showDataUpdated(hmDeptColourCode.get(rs.getString("depart_id")) ,"Red"));
				
				hmEmpLevelColourMap.put(rs.getString("emp_id"),showDataUpdated(hmLevelColourCode.get(rs.getString("level_id")) ,"Red"));
			
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				
				
				hmEmpNameMap.put(rs.getString("emp_id"), rs.getString("emp_fname") +strEmpMName+ " " + rs.getString("emp_lname"));			
				hmEmpDesigMap.put(rs.getString("emp_id"), rs.getString("designation_name"));				
				hmEmpLevelMap.put(rs.getString("emp_id"), rs.getString("level_name"));
				hmEmpDepartmentMap.put(rs.getString("emp_id"), rs.getString("dept_name"));

			}
			rs.close();
			pst.close();
			
//			System.out.println("hmHireracyLevels ===> " + hmHireracyLevels);
			

			StringBuilder sbrootItemData=new StringBuilder();
			StringBuilder sbChartData=new StringBuilder();
 	
			Iterator<String> itrOrganisations=hmOrganisations.keySet().iterator();
			
			while(itrOrganisations.hasNext()) {
				String orgID=itrOrganisations.next();
			
				sbrootItemData.append("var rootItem"+orgID+"=new primitives.orgdiagram.ItemConfig('Dailyhrz', 'DAILY HRZ SOFT. SOLUTIONS LLP'," +
						" '"+request.getContextPath()+"/images1/banner_dailyhrz.png'); " +
						"rootItem.items.push(rootItem"+orgID+") ");
			}

//			    Iterator<String> itrSuperEmp = hmHireracyLevels.keySet().iterator();
			   
			 // those who are under Super NODE ie.  dont have Super emp IDs..................
		
			    		List<String> alEmpListSuper=(List<String>) hmHireracyLevels.get("0");
//			    		System.out.println("alEmpListSuper ===> " + alEmpListSuper);
//			    		System.out.println("alEmpListSuper.size() ===> " + alEmpListSuper.size());
			    		
			    		for(int i=0; alEmpListSuper!=null && i<alEmpListSuper.size();i++) {
			    			String orgID=hmEmpOrgName.get(alEmpListSuper.get(i));
			    			if(i%2==0) {
			    			sbChartData.append("var empSuper"+alEmpListSuper.get(i)+" =new primitives.orgdiagram.ItemConfig('"+showDataUpdated(hmEmpLevelMap.get(alEmpListSuper.get(i)),"")+"'," +
							"'"+showDataUpdated(hmEmpNameMap.get(alEmpListSuper.get(i)),"")+"             "+showDataUpdated(hmEmpDesigMap.get(alEmpListSuper.get(i)),"")+"','"+request.getContextPath()+"/userImages/avatar_photo.png'); " +
							"empSuper"+alEmpListSuper.get(i)+".itemType = primitives.orgdiagram.ItemType.Assistant;" +
							"empSuper"+alEmpListSuper.get(i)+".adviserPlacementType = primitives.orgdiagram.AdviserPlacementType.Right;" +
							"empSuper"+alEmpListSuper.get(i)+".phone = '00000000000';" +
							"empSuper"+alEmpListSuper.get(i)+".email = 'abc@abc.com';" +
							"empSuper"+alEmpListSuper.get(i)+".groupTitle = '"+showDataUpdated(hmEmpDepartmentMap.get(alEmpListSuper.get(i)),"")+"';" +
							"empSuper"+alEmpListSuper.get(i)+".itemTitleColor = primitives.common.Colors."+showDataUpdated(hmEmpLevelColourMap.get(alEmpListSuper.get(i)),"Red")+" ;" +
							"empSuper"+alEmpListSuper.get(i)+".groupTitleColor = primitives.common.Colors."+showDataUpdated(hmEmpDepartmentColourMap.get(alEmpListSuper.get(i)),"Red")+" ;" +
							"rootItem"+orgID+".items.push(empSuper"+alEmpListSuper.get(i)+");");
			    			} else {
			    				sbChartData.append("var empSuper"+alEmpListSuper.get(i)+" =new primitives.orgdiagram.ItemConfig('"+showDataUpdated(hmEmpLevelMap.get(alEmpListSuper.get(i)),"")+"'," +
									"'"+showDataUpdated(hmEmpNameMap.get(alEmpListSuper.get(i)),"")+"             "+showDataUpdated(hmEmpDesigMap.get(alEmpListSuper.get(i)),"")+"','"+request.getContextPath()+"/userImages/avatar_photo.png'); " +
									"empSuper"+alEmpListSuper.get(i)+".itemType = primitives.orgdiagram.ItemType.Assistant;" +
									"empSuper"+alEmpListSuper.get(i)+".adviserPlacementType = primitives.orgdiagram.AdviserPlacementType.Left;" +
									"empSuper"+alEmpListSuper.get(i)+".phone = '00000000000';" +
									"empSuper"+alEmpListSuper.get(i)+".email = 'abc@abc.com';" +
									"empSuper"+alEmpListSuper.get(i)+".groupTitle = '"+showDataUpdated(hmEmpDepartmentMap.get(alEmpListSuper.get(i)),"")+"';" +
									"empSuper"+alEmpListSuper.get(i)+".itemTitleColor = primitives.common.Colors."+showDataUpdated(hmEmpLevelColourMap.get(alEmpListSuper.get(i)),"Red")+" ;" +
									"empSuper"+alEmpListSuper.get(i)+".groupTitleColor = primitives.common.Colors."+showDataUpdated(hmEmpDepartmentColourMap.get(alEmpListSuper.get(i)),"Red")+" ;" +
									"rootItem"+orgID+".items.push(empSuper"+alEmpListSuper.get(i)+");");
			    			}
			    		}
			
			    // Adding rest of records *********************************
			  
			   Iterator<String> itrEmp=hmEmpSuperMap.keySet().iterator();
			    int count=0;
			    while(itrEmp.hasNext()) {
			    	
			    	String empId=itrEmp.next(); 
			    	String superEmpId=hmEmpSuperMap.get(empId);
			    	
			    	if(!alEmpListSuper.contains(empId)) {
					     if(count%2==0) {

			    	        if(alEmpListSuper.contains(superEmpId)) {
			    	        	sbChartData.append(" var emp"+empId+" = new primitives.orgdiagram.ItemConfig('"+showDataUpdated(hmEmpLevelMap.get(empId),"")+"'," +
			    	        			"'"+showDataUpdated(hmEmpNameMap.get(empId),"")+" "+showDataUpdated(hmEmpDesigMap.get(empId),"")+"','"+request.getContextPath()+"/userImages/avatar_photo.png'); " +
							"emp"+empId+".itemType = primitives.orgdiagram.ItemType.Assistant;" +
							"emp"+empId+".adviserPlacementType = primitives.orgdiagram.AdviserPlacementType.Left;" +
							"emp"+empId+".phone = '00000000000';" +
							"emp"+empId+".email = 'abc@abc.com';" +
							"emp"+empId+".groupTitle = '"+showDataUpdated(hmEmpDepartmentMap.get(empId),"")+"';" +
							"emp"+empId+".itemTitleColor = primitives.common.Colors."+showDataUpdated(hmEmpLevelColourMap.get(empId),"Red")+" ;" +
							"emp"+empId+".groupTitleColor = primitives.common.Colors."+showDataUpdated(hmEmpDepartmentColourMap.get(empId),"Red")+" ;" +
							"empSuper"+superEmpId+".items.push(emp"+empId+");");
			    	       
		
			    	        } else {
			    	        	sbChartData.append(" var emp"+empId+" = new primitives.orgdiagram.ItemConfig('"+showDataUpdated(hmEmpLevelMap.get(empId),"")+"'," +
		    	        			"'"+showDataUpdated(hmEmpNameMap.get(empId),"")+" "+showDataUpdated(hmEmpDesigMap.get(empId),"")+"','"+request.getContextPath()+"/userImages/avatar_photo.png'); " +
									"emp"+empId+".itemType = primitives.orgdiagram.ItemType.Assistant;" +
									"emp"+empId+".adviserPlacementType = primitives.orgdiagram.AdviserPlacementType.Left;" +
									"emp"+empId+".phone = '00000000000';" +
									"emp"+empId+".email = 'abc@abc.com';" +
									"emp"+empId+".groupTitle = '"+showDataUpdated(hmEmpDepartmentMap.get(empId),"")+"';" +
									"emp"+empId+".itemTitleColor = primitives.common.Colors."+showDataUpdated(hmEmpLevelColourMap.get(empId),"Red")+" ;" +
									"emp"+empId+".groupTitleColor = primitives.common.Colors."+showDataUpdated(hmEmpDepartmentColourMap.get(empId),"Red")+" ;" +
									"emp"+superEmpId+".items.push(emp"+empId+");");
					    		
			    	        	}
			
			    	         } else {
			    		       
				    	        if(alEmpListSuper.contains(superEmpId)) {
				    	        	sbChartData.append(" var emp"+empId+" = new primitives.orgdiagram.ItemConfig('"+showDataUpdated(hmEmpLevelMap.get(empId),"")+"'," +
				    	        			"'"+showDataUpdated(hmEmpNameMap.get(empId),"")+" "+showDataUpdated(hmEmpDesigMap.get(empId),"")+"','"+request.getContextPath()+"/userImages/avatar_photo.png'); " +
								"emp"+empId+".itemType = primitives.orgdiagram.ItemType.Assistant;" +
								"emp"+empId+".adviserPlacementType = primitives.orgdiagram.AdviserPlacementType.Right;" +
								"emp"+empId+".phone = '00000000000';" +
								"emp"+empId+".email = 'abc@abc.com';" +
								"emp"+empId+".groupTitle = '"+showDataUpdated(hmEmpDepartmentMap.get(empId),"")+"';" +
								"emp"+empId+".itemTitleColor = primitives.common.Colors."+showDataUpdated(hmEmpLevelColourMap.get(empId),"Red")+" ;" +
								"emp"+empId+".groupTitleColor = primitives.common.Colors."+showDataUpdated(hmEmpDepartmentColourMap.get(empId),"Red")+" ;" +
								"empSuper"+superEmpId+".items.push(emp"+empId+");");
			
				    	        } else {
				    	        	sbChartData.append(" var emp"+empId+" = new primitives.orgdiagram.ItemConfig('"+showDataUpdated(hmEmpLevelMap.get(empId),"")+"'," +
			    	        			"'"+showDataUpdated(hmEmpNameMap.get(empId),"")+" "+showDataUpdated(hmEmpDesigMap.get(empId),"")+"','"+request.getContextPath()+"/userImages/avatar_photo.png'); " +
										"emp"+empId+".itemType = primitives.orgdiagram.ItemType.Assistant;" +
										"emp"+empId+".adviserPlacementType = primitives.orgdiagram.AdviserPlacementType.Right;" +
										"emp"+empId+".phone = '00000000000';" +
										"emp"+empId+".email = 'abc@abc.com';" +
										"emp"+empId+".groupTitle = '"+showDataUpdated(hmEmpDepartmentMap.get(empId),"")+"';" +
										"emp"+empId+".itemTitleColor = primitives.common.Colors."+showDataUpdated(hmEmpLevelColourMap.get(empId),"Red")+" ;" +
										"emp"+empId+".groupTitleColor = primitives.common.Colors."+showDataUpdated(hmEmpDepartmentColourMap.get(empId),"Red")+" ;" +
										"emp"+superEmpId+".items.push(emp"+empId+");");
						    		
				    	        }

			    	         }
					     count++;
			    	}
			    	
			    }
         
				request.setAttribute("sbrootItemData", sbrootItemData);
				request.setAttribute("sbCharData", sbChartData);
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	 
	
	public void getHireracyLevels() {
		PreparedStatement pst = null;
		ResultSet rs = null;
		Connection con = null;
		UtilityFunctions uF = new UtilityFunctions();
		Database db = new Database();
		db.setRequest(request);
		try {
			//System.out.println("in getHireracyLevels ...........");
			getSelectedFilter(uF);
			
			con = db.makeConnection(con);
//			Map hmEmpMap = CF.getEmpNameMap(strUserType, strEmpId);
			Map hmEmpMap = CF.getEmpNameMap(con, null, strEmpId);
			Map hmEmpProfileImage = CF.getEmpProfileImage(con);
			Map hmEmpDesigMap = CF.getEmpDesigMap(con); 
			
			Map<String, String> hmOrgData = new HashMap<String, String>();
			pst = con.prepareStatement("select * from org_details where org_id=?");
			pst.setInt(1, uF.parseToInt(getOrgId()));
			rs = pst.executeQuery();
			while(rs.next()) {
				hmOrgData.put("ORG_NAME", rs.getString("org_name"));
				hmOrgData.put("ORG_LOGO", rs.getString("org_logo"));
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmOrgData", hmOrgData);
			/*pst = con.prepareStatement("select * from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id and is_alive= true and emp_id = ?");
			pst.setInt(1, uF.parseToInt(strEmpId));
			
			rs = pst.executeQuery();
			String strSupId = null;
			while(rs.next()) {
				strSupId = rs.getString("supervisor_emp_id");
			}*/
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select emp_id,supervisor_emp_id from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id " +
					"and is_alive= true and emp_per_id >0 and org_id = ? ");
			if(strUserType !=null && (!strUserType.equals(MANAGER) || (getDataType()!=null && getDataType().equals(strBaseUserType))) && session.getAttribute(WLOCATION_ACCESS) != null && !((String)session.getAttribute(WLOCATION_ACCESS)).equals("")) {
				sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
			sbQuery.append(" order by supervisor_emp_id");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(getOrgId()));
			rs = pst.executeQuery();
//			System.out.println("PST ===>> " + pst);
			Map<String, List<String>> hmHireracyLevels = new LinkedHashMap<String, List<String>>();
			Map<String, String> hmEmpSuperMap = new LinkedHashMap<String, String>();
			List<String> alHireracyLevels = new ArrayList<String>();
			List<String> alInner = new ArrayList<String>();
			
			String strSupervisorOld = null;
			String strSupervisorNew = null;
			
			while(rs.next()) {
				strSupervisorNew = rs.getString("supervisor_emp_id");
				if(strSupervisorNew!=null && !strSupervisorNew.equalsIgnoreCase(strSupervisorOld)) {
					alInner = new ArrayList<String>();
				}
				
				if(uF.parseToInt(rs.getString("supervisor_emp_id")) == uF.parseToInt(rs.getString("emp_id"))) {
					continue;
				}
				alHireracyLevels.add(rs.getString("emp_id"));
				
				alInner.add(rs.getString("emp_id"));
				
				hmHireracyLevels.put(strSupervisorNew, alInner);
				
				hmEmpSuperMap.put(rs.getString("emp_id"), strSupervisorNew);
				
				strSupervisorOld = strSupervisorNew;
			}
			rs.close();
			pst.close();
			
//			System.out.println("hmHireracyLevels === ===> " + hmHireracyLevels);
			
			List<String> alChain = new ArrayList<String>();
			
//			if(strUserType!=null && (strUserType.equalsIgnoreCase(EMPLOYEE) || strUserType.equalsIgnoreCase(MANAGER))) {
			if((strUserType!=null && strUserType.equalsIgnoreCase(MANAGER) && getProductType() != null && getProductType().equals("3")) || (getProductType() != null && getProductType().equals("2") && getDataType()!=null && getDataType().equals("MYTEAM") && strUserType!=null && strUserType.equalsIgnoreCase(MANAGER))) {
				alChain.add(strEmpId);
//				System.out.println("strEmpId ===>> " + strEmpId);
				int count=0;
//				for(;;) {

//					String strSuper = hmEmpSuperMap.get(strEmpId);
//					System.out.println("strSuper ===>> " + strSuper);
//					alChain.add(strSuper);

					if(strUserType!=null && strUserType.equalsIgnoreCase(MANAGER) && count == 0) {
						List<String> alInner1 = new ArrayList<String>();
						alInner1.add(strEmpId);
						Map<String, List<String>> hmHireracyLevels1 = new LinkedHashMap<String, List<String>>();
						hmHireracyLevels1 = hmHireracyLevels;
						List<String> alEmpList = (List<String>)hmHireracyLevels1.get(strEmpId);

						hmHireracyLevels = new LinkedHashMap<String, List<String>>();
						hmHireracyLevels.put("0", alInner1);
						
						hmHireracyLevels.put(strEmpId, alEmpList);
//						System.out.println("alEmpList ===>> " + alEmpList);
						for(int i=0; alEmpList != null && i<alEmpList.size(); i++) {
							alChain.add((String)alEmpList.get(i));
//							getMyTeamEmployees(hmHireracyLevels, hmHireracyLevels1, (String)alEmpList.get(i), alChain);
						}
					}
//					count++;
//					strEmpId = strSuper;
//					
//					if(uF.parseToInt(strSuper) == 0) {
//						break;
//					}
//				}
//				System.out.println("hmHireracyLevels ===>> " + hmHireracyLevels);
//				System.out.println("alHireracyLevels ===>> " + alHireracyLevels);
//				System.out.println("alChain ===>> " + alChain);
			}
			 
//			System.out.println("hmEmpProfileImage ===>> " + hmEmpProfileImage);
			
			request.setAttribute("hmHireracyLevels", hmHireracyLevels);
			request.setAttribute("alHireracyLevels", alHireracyLevels);
			request.setAttribute("hmEmpMap", hmEmpMap);
			request.setAttribute("hmEmpDesigMap", hmEmpDesigMap);
			request.setAttribute("hmEmpProfileImage", hmEmpProfileImage);
			request.setAttribute("alChain", alChain);
			
			getEmployeeChieldEmpCount(hmHireracyLevels, alHireracyLevels, alChain);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	

	public List<String> getMyTeamEmployees(Map<String, List<String>> hmHireracyLevels, Map<String, List<String>> hmHireracyLevels1, String empId, List<String> alChain) {

//		System.out.println("ChildCount hmHireracyLevels ===>>>> " + hmHireracyLevels);
		if(empId != null && !empId.trim().equals("")) { 
//			empId = "0";
			if(hmHireracyLevels1 == null) hmHireracyLevels1 = new LinkedHashMap<String, List<String>>();
			List<String> innerList = (List<String>)hmHireracyLevels1.get(empId.trim());
			
			if(hmHireracyLevels == null) hmHireracyLevels = new LinkedHashMap<String, List<String>>();
			hmHireracyLevels.put(empId, innerList);
			
			for(int i= 0; innerList != null && !innerList.isEmpty() && i<innerList.size(); i++) {
				String empId1 = innerList.get(i);
				if(empId1 != null && !empId1.trim().equals("") && !alChain.contains(empId1.trim())) {
					alChain.add(empId1.trim());
				}
				if(empId1 != null && !empId1.trim().equals("")) {
					getMyTeamEmployees(hmHireracyLevels, hmHireracyLevels1, empId1, alChain);
				}
			}
		}
		return alChain;
	}
	
	
    public void getLocationsforMap() {
		PreparedStatement pst = null;
		ResultSet rs = null;
		Connection con = null;
		
		Database db = new Database();
		db.setRequest(request);
		try {
//			System.out.println("in getLocationsforMap ...........");
			StringBuilder sbLocation = new StringBuilder(); 
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from work_location_info order by org_id");
			rs = pst.executeQuery();
			String strOrgOld= null;
			String strOrgNew= null;
			int count = 0;
			while(rs.next()) {
				strOrgNew = rs.getString("org_id");
				if(strOrgNew!=null && !strOrgNew.equalsIgnoreCase(strOrgOld)) {
					count++;
				}
				sbLocation.append("['"+rs.getString("wlocation_city")+"', '"+rs.getString("wlocation_city")+"', "+count+", 10,'"+rs.getString("wlocation_address")+","+rs.getString("wlocation_city")+"'],");   
				   
				strOrgOld = strOrgNew;
			}
			rs.close();
			pst.close();
			
			sbLocation.replace(0, sbLocation.length(), sbLocation.substring(0, sbLocation.length()-1));
			
			request.setAttribute("sbLocation", sbLocation);
			
		} catch (Exception e) {
			e.printStackTrace();   
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	public void getDepartmentLocation() {
		PreparedStatement pst = null;
		ResultSet rs = null;
		ResultSet rs1 = null;
		Connection con = null;
		UtilityFunctions uF = new UtilityFunctions();
		Database db = new Database();
		db.setRequest(request);
		try {
//			System.out.println("in getDepartmentLocation ...........");
			con = db.makeConnection(con);
			
			Map<String, String> hmServiceMap = CF.getServicesMap(con, false);
			Map<String, String> hmEmpMap = new HashMap<String, String>();
			Map<String, String> hmStyleClassMap = new HashMap<String, String>();
			Map<String, String> hmStyleColorMap = new HashMap<String, String>();
			
			Map<String, String> hmCount = new HashMap<String, String>();
			pst = con.prepareStatement("select count(*), parent,service_id from department_info group by parent, service_id order by service_id");
			rs = pst.executeQuery();
			while(rs.next()) {
				hmCount.put(rs.getString("service_id")+"_S", rs.getString("count"));
				hmCount.put(rs.getString("parent")+"_D", rs.getString("count"));
			}
			rs.close();
			pst.close();
			request.setAttribute("hmCount", hmCount);
			
			Map<String, List<String>> hmHireracyLevels = new LinkedHashMap<String, List<String>>();
			Map<String, String> hmEmpSuperMap = new LinkedHashMap<String, String>();
			List<String> alHireracyLevels = new ArrayList<String>();;
			List<String> alInner = new ArrayList<String>();
			

			pst = con.prepareStatement("select * from org_details"); 
			rs = pst.executeQuery();
			alInner = new ArrayList<String>();
			List<String> alOrg = new ArrayList<String>();
			List<String> alOrgN = new ArrayList<String>();
			while(rs.next()) {
				
				String strOrgId = rs.getString("org_id");
				
				alInner.add(strOrgId+"_O");
				hmHireracyLevels.put("0", alInner);
				
				
				alOrg.add(strOrgId);
				alOrgN.add(strOrgId+"_O");
				
				
				hmEmpMap.put(strOrgId+"_O", rs.getString("org_name"));
				hmStyleClassMap.put(strOrgId+"_O", "org");
				
			}
			rs.close();
			pst.close();

			Map<String, String> hmLocationColor = new HashMap<String, String>();
			pst = con.prepareStatement("select * from work_location_info");
			rs = pst.executeQuery();
			while(rs.next()) {
				hmLocationColor.put(rs.getString("wlocation_id"), rs.getString("wlocation_color"));
				hmEmpMap.put(rs.getString("wlocation_id")+"_WL", rs.getString("wlocation_name")+", "+rs.getString("wlocation_city"));
				
			}
			rs.close();
			pst.close();

			List<String> alSBUs  = new ArrayList<String>();
			List<String> alSBUsN  = new ArrayList<String>();
			for(int i=0; i<alOrgN.size(); i++) {
				
				String strLocationId = (String)alOrgN.get(i);
				
//				pst = con.prepareStatement("select * from department_info where org_id =? order by service_id");
				pst = con.prepareStatement("select * from services where org_id =? order by service_id");
				
				pst.setInt(1, uF.parseToInt((String)alOrg.get(i)));
				rs1 = pst.executeQuery();
				String strSBUIdNew = null;
				String strSBUIdOld = null;
				alInner = new ArrayList<String>();
				while(rs1.next()) {
					strSBUIdNew = rs1.getString("service_id");
					
					if(strSBUIdNew!=null && strSBUIdNew.equalsIgnoreCase(strSBUIdOld)) {
						continue;
					}
					
					
					alInner.add(strSBUIdNew+"_S");
					
					hmHireracyLevels.put(strLocationId, alInner);
					hmEmpSuperMap.put(strSBUIdNew+"_S", strLocationId);
					alHireracyLevels.add(strSBUIdNew+"_S");
					
					alSBUs.add(strSBUIdNew);
					alSBUsN.add(strSBUIdNew+"_S");
					
					hmEmpMap.put(strSBUIdNew+"_S", (String)hmServiceMap.get(strSBUIdNew));
					hmStyleClassMap.put(strSBUIdNew+"_S", "sbu");
					
					
					strSBUIdOld = strSBUIdNew;
				}
				rs1.close();
				pst.close();
			
			}
			
			
			List<String> alLevels  = new ArrayList<String>();
			List<String> alLevelsN  = new ArrayList<String>();
			for(int i=0; i<alSBUs.size(); i++) {
				
//				String strSBUId = (String)alSBUsN.get(i);
				pst = con.prepareStatement("select * from department_info where service_id =?  order by parent");
				pst.setInt(1, uF.parseToInt((String)alSBUs.get(i)));
				rs1 = pst.executeQuery();
				
				
//				System.out.println("pst===>"+pst);
				
				String strLevelNew = null;
				String strLevelOld = null;
				alInner = new ArrayList<String>();
				
				while(rs1.next()) {
					
					strLevelNew = rs1.getString("parent");
					if(strLevelNew!=null && !strLevelNew.equalsIgnoreCase(strLevelOld)) {
						alInner = new ArrayList<String>();
					}
					
					alHireracyLevels.add(rs1.getString("dept_id")+"_D");
					alInner.add(rs1.getString("dept_id")+"_D");
					
					
					if(uF.parseToInt(strLevelNew)==0) {
						hmHireracyLevels.put((String)alSBUsN.get(i), alInner);
					} else {
						hmHireracyLevels.put(strLevelNew+"_D", alInner);
					}
					
//					hmHireracyLevels.put(strLevelNew+"_LP", alInner);
					
					hmEmpSuperMap.put(rs1.getString("dept_id")+"_D", strLevelNew);
					
					
					hmEmpMap.put(rs1.getString("dept_id")+"_D", rs1.getString("dept_name"));
					hmStyleClassMap.put(rs1.getString("dept_id")+"_D", "dept collapsed");
					
					
					alLevels.add(rs1.getString("dept_id"));
					alLevelsN.add(rs1.getString("dept_id")+"_D");
					
					strLevelOld = strLevelNew;
				}
				rs1.close();
				pst.close();
			}
			
			String strLocationId= null;
			pst = con.prepareStatement("select * from department_info where service_id =? order by org_id");
			pst.setInt(1, 0);
			rs1 = pst.executeQuery();
			String strSBUIdNew = null;
//			String strSBUIdOld = null;
			alInner = new ArrayList<String>();
			while(rs1.next()) {
				
				
				if(uF.parseToInt(rs1.getString("parent"))==0) {
					strLocationId = rs1.getString("org_id")+"_O" ;  
				} else {
					strLocationId = rs1.getString("parent")+"_D" ;
				}
				
				strSBUIdNew = rs1.getString("dept_id");
				
				alInner = hmHireracyLevels.get(strLocationId);
				if(alInner==null) {
					alInner = new ArrayList<String>();
				}
				 
				alInner.add(strSBUIdNew+"_D");
				
				hmHireracyLevels.put(strLocationId, alInner);
				hmEmpSuperMap.put(strSBUIdNew+"_D", strLocationId);
				alHireracyLevels.add(strSBUIdNew+"_D");
				
				alSBUs.add(strSBUIdNew);
				alSBUsN.add(strSBUIdNew+"_D");
				
				hmEmpMap.put(strSBUIdNew+"_D", rs1.getString("dept_name"));
				hmStyleClassMap.put(strSBUIdNew+"_D", "dept");
				
				
//				strSBUIdOld = strSBUIdNew;
				
			}
			rs1.close();
			pst.close();
		
		
			
			List<String> alChain = new ArrayList<String>();
			
			if(strUserType!=null && (strUserType.equalsIgnoreCase(EMPLOYEE) || strUserType.equalsIgnoreCase(MANAGER))) {
				
				alChain.add(strEmpId);
//				int count=0;
				for(;;) {
					
					String strSuper = hmEmpSuperMap.get(strEmpId);
					alChain.add(strSuper);
					
					if(strUserType!=null && strUserType.equalsIgnoreCase(MANAGER)) {
						List<String> alEmpList = (List<String>)hmHireracyLevels.get(strEmpId);
						for(int i=0;alEmpList!=null && i<alEmpList.size(); i++) {
							alChain.add((String)alEmpList.get(i));
						}
					}
					
					strEmpId = strSuper;
					
					if(uF.parseToInt(strSuper)==0) {
						break;
					}
				}	
			}
			
			  
			request.setAttribute("hmHireracyLevels", hmHireracyLevels);
			request.setAttribute("alHireracyLevels", alHireracyLevels);
			request.setAttribute("hmEmpMap", hmEmpMap);
			request.setAttribute("hmStyleClassMap", hmStyleClassMap);
			request.setAttribute("hmStyleColorMap", hmStyleColorMap);
			request.setAttribute("hmLocationColor", hmLocationColor);
			
			
//			request.setAttribute("hmEmpDesigMap", hmEmpDesigMap);
//			request.setAttribute("hmEmpProfileImage", hmEmpProfileImage);
			request.setAttribute("alChain", alChain);
			
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		} finally {
			db.closeResultSet(rs1);
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	public void getLevelLocation() {
		PreparedStatement pst = null;
		ResultSet rs = null;
		ResultSet rs1 = null;
		Connection con = null;
		UtilityFunctions uF = new UtilityFunctions();
		Database db = new Database();
		db.setRequest(request);
		try {
//			System.out.println("in getLevelLocation ...........");
			con = db.makeConnection(con);
			
//			Map hmEmpMap = CF.getEmpNameMap(strUserType, strEmpId);
//			Map hmEmpMap = CF.getEmpNameMap(null, strEmpId);
//			Map hmEmpProfileImage = CF.getEmpProfileImage();
//			Map hmEmpDesigMap = CF.getEmpDesigMap();
			//Map hmServiceMap = CF.getServicesMap(con, false);
			Map<String, String> hmEmpMap = new HashMap<String, String>();
			Map<String, String> hmStyleClassMap = new HashMap<String, String>();
			Map<String, String> hmStyleColorMap = new HashMap<String, String>();
			
			
			/*pst = con.prepareStatement("select * from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id and is_alive= true and emp_id = ?");
			pst.setInt(1, uF.parseToInt(strEmpId));
			
			rs = pst.executeQuery();
			String strSupId = null;
			while(rs.next()) {
				strSupId = rs.getString("supervisor_emp_id");
			}
			
			*/
			
			Map<String, List<String>> hmHireracyLevels = new LinkedHashMap<String, List<String>>();
			Map<String, String> hmEmpSuperMap = new LinkedHashMap<String, String>();
			List<String> alHireracyLevels = new ArrayList<String>();
			List<String> alInner = new ArrayList<String>();
			
			/*pst = con.prepareStatement("select * from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id and is_alive= true and emp_per_id >0 order by supervisor_emp_id"); 
			rs = pst.executeQuery();
			
			Map hmHireracyLevels = new LinkedHashMap();
			Map<String, String> hmEmpSuperMap = new LinkedHashMap<String, String>();
			List<String> alHireracyLevels = new ArrayList();;
			List alInner = new ArrayList();
			
			String strSupervisorOld = null;
			String strSupervisorNew = null;
			
			while(rs.next()) {
				strSupervisorNew = rs.getString("supervisor_emp_id");
				if(strSupervisorNew!=null && !strSupervisorNew.equalsIgnoreCase(strSupervisorOld)) {
					alInner = new ArrayList();
				}
				
				alHireracyLevels.add(rs.getString("emp_id"));
				
				alInner.add(rs.getString("emp_id"));
				
				hmHireracyLevels.put(strSupervisorNew, alInner);
				
				hmEmpSuperMap.put(rs.getString("emp_id"), strSupervisorNew);
				
				strSupervisorOld = strSupervisorNew;
			}*/
			
			
			
			
			
			/*
			
			alHireracyLevels.add("10");
			alHireracyLevels.add("9");
			alHireracyLevels.add("9a");
			
			hmEmpSuperMap.put("10", "11");
			hmEmpSuperMap.put("9", "10");
			hmEmpSuperMap.put("9a", "10");
			
			alInner = new ArrayList();
			alInner.add("10");
			hmHireracyLevels.put("11", alInner);
			alInner = new ArrayList();
			alInner.add("9");
			alInner.add("9a");
			hmHireracyLevels.put("10", alInner);
			
			alInner = new ArrayList();
			alInner.add("11");
			hmHireracyLevels.put("0", alInner);
			
			*/
			
			
			
			pst = con.prepareStatement("select * from org_details"); 
			rs = pst.executeQuery();
			
			alInner = new ArrayList<String>();
			List<String> alOrg = new ArrayList<String>();
			List<String> alOrgN = new ArrayList<String>();
			while(rs.next()) {
				
				String strOrgId = rs.getString("org_id");
				
				alInner.add(strOrgId+"_O");
				hmHireracyLevels.put("0", alInner);
				
				
				alOrg.add(strOrgId);
				alOrgN.add(strOrgId+"_O");
				
				
				hmEmpMap.put(strOrgId+"_O", rs.getString("org_name"));
				hmStyleClassMap.put(strOrgId+"_O", "org");
				
			}
			rs.close();
			pst.close();

			
			
			
			/*List alLocations  = new ArrayList();
			List alLocationsN  = new ArrayList();
			for(int i=0; i<alOrgN.size(); i++) {
				
				String strOrgId = (String)alOrgN.get(i);
				
				pst = con.prepareStatement("select * from work_location_info where org_id =?");
				pst.setInt(1, uF.parseToInt((String)alOrg.get(i)));
				ResultSet rs1 = pst.executeQuery();
				String strLocationIdNew = null;
				alInner = new ArrayList();
				while(rs1.next()) {
					strLocationIdNew = rs1.getString("wlocation_id");
					
					alInner.add(strLocationIdNew+"_WL");
					
					hmHireracyLevels.put(strOrgId, alInner);
					hmEmpSuperMap.put(strLocationIdNew+"_WL", strOrgId);
					alHireracyLevels.add(strLocationIdNew+"_WL");
					
					alLocations.add(strLocationIdNew);
					alLocationsN.add(strLocationIdNew+"_WL");
					
					hmEmpMap.put(strLocationIdNew+"_WL", rs1.getString("wlocation_name")+", "+rs1.getString("wlocation_city"));
					hmStyleClassMap.put(strLocationIdNew+"_WL", "wlocation");
				}
				
				
			}
			*/
			
			
			
			/*
			List alSBUs  = new ArrayList();
			List alSBUsN  = new ArrayList();
			for(int i=0; i<alLocationsN.size(); i++) {
				
				String strLocationId = (String)alLocationsN.get(i);
				
				pst = con.prepareStatement("select * from department_info where wlocation_id =? order by service_id");
				pst.setInt(1, uF.parseToInt((String)alLocations.get(i)));
				ResultSet rs1 = pst.executeQuery();
				String strSBUIdNew = null;
				String strSBUIdOld = null;
				alInner = new ArrayList();
				while(rs1.next()) {
					strSBUIdNew = rs1.getString("service_id");
					
					if(strSBUIdNew!=null && strSBUIdNew.equalsIgnoreCase(strSBUIdOld)) {
						continue;
					}
					
					
					alInner.add(strSBUIdNew+"_S");
					
					hmHireracyLevels.put(strLocationId, alInner);
					hmEmpSuperMap.put(strSBUIdNew+"_S", strLocationId);
					alHireracyLevels.add(strSBUIdNew+"_S");
					
					alSBUs.add(strSBUIdNew);
					alSBUsN.add(strSBUIdNew+"_S");
					
					hmEmpMap.put(strSBUIdNew+"_S", (String)hmServiceMap.get(strSBUIdNew));
					hmStyleClassMap.put(strSBUIdNew+"_S", "sbu");
					
					
					strSBUIdOld = strSBUIdNew;
				}
			
			}*/
			
			
			
			Map<String, String> hmLocationColor = new HashMap<String, String>();
			pst = con.prepareStatement("select * from work_location_info");
			rs = pst.executeQuery();
			while(rs.next()) {
				hmLocationColor.put(rs.getString("wlocation_id"), rs.getString("wlocation_color"));
				hmEmpMap.put(rs.getString("wlocation_id")+"_WL", rs.getString("wlocation_name")+", "+rs.getString("wlocation_city"));
				
			}
			rs.close();
			pst.close();
			
			List<String> alLevels  = new ArrayList<String>();
			List<String> alLevelsN  = new ArrayList<String>();
			for(int i=0; i<alOrgN.size(); i++) {
				
//				String strLocationId = alOrgN.get(i);
				
//				pst = con.prepareStatement("select * from level_details where org_id =? and level_parent=0");
				pst = con.prepareStatement("select * from level_details where org_id =? order by level_parent");
				pst.setInt(1, uF.parseToInt((String)alOrg.get(i)));
				rs1 = pst.executeQuery();
				String strLevelNew = null;
				String strLevelOld = null;
				alInner = new ArrayList<String>();
				
				while(rs1.next()) {
					/*strLevelNew = rs1.getString("level_id");
					
					if(strLevelNew!=null && strLevelNew.equalsIgnoreCase(strLevelOld)) {
						continue;
					}
					
					if(uF.parseToInt(rs1.getString("level_parent"))==0) {
						alInner.add(strLevelNew+"_LP");
					}
					
					hmHireracyLevels.put(strLocationId, alInner);
					hmEmpSuperMap.put(strLevelNew+"_LP", strLocationId);
					alHireracyLevels.add(strLevelNew+"_LP");
					
					alLevels.add(strLevelNew);
					alLevelsN.add(strLevelNew+"_LP");
					
					
					hmEmpMap.put(strLevelNew+"_LP", rs1.getString("level_name"));
					hmStyleClassMap.put(strLevelNew+"_LP", "sbu");*/
					
					
					
					strLevelNew = rs1.getString("level_parent");
					if(strLevelNew!=null && !strLevelNew.equalsIgnoreCase(strLevelOld)) {
						alInner = new ArrayList<String>();
					}
					
					alHireracyLevels.add(rs1.getString("level_id")+"_LP");
					
					
					alInner.add(rs1.getString("level_id")+"_LP");
					
					
					if(uF.parseToInt(strLevelNew)==0) {
						hmHireracyLevels.put((String)alOrg.get(i)+"_O", alInner);
					} else {
						hmHireracyLevels.put(strLevelNew+"_LP", alInner);
					}
					
//					hmHireracyLevels.put(strLevelNew+"_LP", alInner);
					
					hmEmpSuperMap.put(rs1.getString("level_id")+"_LP", strLevelNew);
					
					
					hmEmpMap.put(rs1.getString("level_id")+"_LP", rs1.getString("level_name"));
					hmStyleClassMap.put(rs1.getString("level_id")+"_LP", "sbu");
					
					
					alLevels.add(rs1.getString("level_id"));
					alLevelsN.add(rs1.getString("level_id")+"_LP");
					
					strLevelOld = strLevelNew;
				}
				rs1.close();
				pst.close();
			}
			
//			List<String> alDesignations  = new ArrayList<String>();
//			List<String> alDesignationsN  = new ArrayList<String>();

			for(int i=0; i<alLevelsN.size(); i++) {
				
				String strLevleId = (String)alLevelsN.get(i);
				
				pst = con.prepareStatement("select * from designation_details where level_id =?");
				pst.setInt(1, uF.parseToInt((String)alLevels.get(i)));
				rs1 = pst.executeQuery();
				String strDesigNew = null;
				String strDesigOld = null;
				
				alInner = hmHireracyLevels.get(strLevleId);
				if(alInner == null)alInner = new ArrayList<String>();
				
				while(rs1.next()) {
					strDesigNew = rs1.getString("designation_id");
					
					if(strDesigNew!=null && strDesigNew.equalsIgnoreCase(strDesigOld)) {
						continue;
					}
					
					
					
					alInner.add(strDesigNew+"_DP");
					
					hmHireracyLevels.put(strLevleId, alInner);
					hmEmpSuperMap.put(strDesigNew+"_DP", strLevleId);
					alHireracyLevels.add(strDesigNew+"_DP");
					
					
					hmEmpMap.put(strDesigNew+"_DP", rs1.getString("designation_name"));
					hmStyleClassMap.put(strDesigNew+"_DP", "sbu1");
					
					
					strDesigOld = strDesigNew;
				}
				rs1.close();
				pst.close();
			
			}
			/*
			
			
			
			for(int i=0; i<alLevelsCN.size(); i++) {
				
				String strLevleId = (String)alLevelsCN.get(i);
				
				pst = con.prepareStatement("select * from designation_details where level_id =?");
				pst.setInt(1, uF.parseToInt((String)alLevelsC.get(i)));
				ResultSet rs1 = pst.executeQuery();
				String strDesigNew = null;
				String strDesigOld = null;
				
				alInner = (ArrayList)hmHireracyLevels.get(strLevleId);
				if(alInner==null)alInner = new ArrayList();
				
				while(rs1.next()) {
					strDesigNew = rs1.getString("designation_id");
					
					if(strDesigNew!=null && strDesigNew.equalsIgnoreCase(strDesigOld)) {
						continue;
					}
					
					alInner.add(strDesigNew+"_D");
					
					hmHireracyLevels.put(strLevleId, alInner);
					hmEmpSuperMap.put(strDesigNew+"_D", strLevleId);
					alHireracyLevels.add(strDesigNew+"_D");
					
					
					hmEmpMap.put(strDesigNew+"_D", rs1.getString("designation_name"));
					hmStyleClassMap.put(strDesigNew+"_D", "sbu1");
					
					
					strDesigOld = strDesigNew;
				}
			
			}
			*/
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			List<String> alChain = new ArrayList<String>();
			
			if(strUserType!=null && (strUserType.equalsIgnoreCase(EMPLOYEE) || strUserType.equalsIgnoreCase(MANAGER))) {
				
				alChain.add(strEmpId);
				int count=0;
				for(;;) {
					
					String strSuper = hmEmpSuperMap.get(strEmpId);
					alChain.add(strSuper); 
					
					if(strUserType!=null && strUserType.equalsIgnoreCase(MANAGER)) {
						List alEmpList = (List)hmHireracyLevels.get(strEmpId);
						for(int i=0;alEmpList!=null && i<alEmpList.size(); i++) {
							alChain.add((String)alEmpList.get(i));
						}
					}
					
					strEmpId = strSuper;
					
					if(uF.parseToInt(strSuper)==0) {
						break;
					}
				}	
			}
			
			  
			request.setAttribute("hmHireracyLevels", hmHireracyLevels);
			request.setAttribute("alHireracyLevels", alHireracyLevels);
			request.setAttribute("hmEmpMap", hmEmpMap);
			request.setAttribute("hmStyleClassMap", hmStyleClassMap);
			request.setAttribute("hmStyleColorMap", hmStyleColorMap);
			request.setAttribute("hmLocationColor", hmLocationColor);
			
			
//			request.setAttribute("hmEmpDesigMap", hmEmpDesigMap);
//			request.setAttribute("hmEmpProfileImage", hmEmpProfileImage);
			request.setAttribute("alChain", alChain);
			
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		} finally {
			db.closeResultSet(rs1);
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	public String showDataUpdated(String str, String showValue) {

		if (str == null) {
			return showValue;
		} else if (str != null && str.equalsIgnoreCase("NULL")) {
			return showValue;
		} else {

			if (str.contains("'"))
				str=str.replace("'", "\\'");
			return str;
			
		}
	}
	

	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

	public List<FillOrganisation> getOrganisationList() {
		return organisationList;
	}

	public void setOrganisationList(List<FillOrganisation> organisationList) {
		this.organisationList = organisationList;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getProductType() {
		return productType;
	}

	public String getStrEmpId() {
		return strEmpId;
	}

	public void setStrEmpId(String strEmpId) {
		this.strEmpId = strEmpId;
	}

	public void setProductType(String productType) {
		this.productType = productType;
	}

	private HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public String getFromPage() {
		return fromPage;
	}

	public void setFromPage(String fromPage) {
		this.fromPage = fromPage;
	}

	public String getDivResult() {
		return divResult;
	}

	public void setDivResult(String divResult) {
		this.divResult = divResult;
	}

	public String getAlertID() {
		return alertID;
	}

	public void setAlertID(String alertID) {
		this.alertID = alertID;
	}
	
}
