package com.konnect.jpms.payroll.reports;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.konnect.jpms.reports.MyProfile;
import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillFinancialYears;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class EmpCTCReport extends ActionSupport implements ServletRequestAware, ServletResponseAware, IStatements {
	
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	boolean isEmpUserType = false;
	CommonFunctions CF = null;
	
	private String strLocation;
	private String strDepartment;
	private String strSbu;
	private String strLevel;
	
	
	private String f_org;
	private String[] f_strWLocation;
	private String[] f_department;
	private String[] f_level;
	private String[] f_service;
	
	private List<FillOrganisation> orgList;
	private List<FillWLocation> wLocationList;
	private List<FillDepartment> departmentList;
	private List<FillLevel> levelList;
	private List<FillServices> serviceList;
	
	private String proPage;
	private String minLimit;
	private String strSearch;
	
	public String execute() throws Exception {
		
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN; 
		strUserType = (String) session.getAttribute(USERTYPE);
		 
		request.setAttribute(TITLE, TReportEmpCTC);
		request.setAttribute(PAGE, PReportEmpCTC);
		
		UtilityFunctions uF = new UtilityFunctions();
		
		request.setAttribute("roundOffCondition", ""+uF.parseToInt(CF.getRoundOffCondtion()));

//		boolean isView  = CF.getAccess(session, request, uF);
//		if(!isView) { 
//			request.setAttribute(PAGE, PAccessDenied);
//			request.setAttribute(TITLE, TAccessDenied);
//			return ACCESS_DENIED;
//		}

		if(getF_org()==null || getF_org().trim().equals("")) {
			setF_org((String)session.getAttribute(ORGID));
		}

		if(getStrLocation() != null && !getStrLocation().equals("")) {
			setF_strWLocation(getStrLocation().split(","));
		} else {
			setF_strWLocation(null);
		}
		if(getStrDepartment() != null && !getStrDepartment().equals("")) {
			setF_department(getStrDepartment().split(","));
		} else {
			setF_department(null);
		}
		if(getStrSbu() != null && !getStrSbu().equals("")) {
			setF_service(getStrSbu().split(","));
		} else {
			setF_service(null);
		}
		if(getStrLevel() != null && !getStrLevel().equals("")) {
			setF_level(getStrLevel().split(","));
		} else {
			setF_level(null);
		}
		
		if(uF.parseToInt(getProPage()) == 0) {
			setProPage("1");
		}
		
		getSearchAutoCompleteData(uF);
		viewSalaryYearlyReport(uF);
		

		return loadSalaryYearlyReport(uF);
	}
	
	private void getSearchAutoCompleteData(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			SortedSet<String> setSearchList = new TreeSet<String>();
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from employee_official_details eod,employee_personal_details epd where epd.emp_per_id=eod.emp_id ");
			if(uF.parseToInt(getF_org())>0) {
				sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
			}else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
					sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			if(getF_strWLocation()!=null && getF_strWLocation().length>0) {
	            sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
	        }else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
				sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
            if(getF_department()!=null && getF_department().length>0) {
                sbQuery.append(" and depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
            }
            if(getF_level()!=null && getF_level().length>0) {
                sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
            }
            if(getF_service()!=null && getF_service().length>0) {
                sbQuery.append(" and (");
                for(int i=0; i<getF_service().length; i++) {
                    sbQuery.append(" service_id like '%,"+getF_service()[i]+",%'");
                    
                    if(i<getF_service().length-1) {
                        sbQuery.append(" OR "); 
                    }
                }
                sbQuery.append(" ) ");
                
            }
			sbQuery.append(" order by epd.emp_fname, epd.emp_lname");
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst=Search=="+pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
			
				
				setSearchList.add(rs.getString("emp_fname")+strEmpMName+" "+rs.getString("emp_lname"));
			}
			rs.close();
			pst.close();
			
			StringBuilder sbData = null;
			Iterator<String> it = setSearchList.iterator();
			while (it.hasNext()) {
				String strData = it.next();
				if(sbData == null) {
					sbData = new StringBuilder();
					sbData.append("\""+strData+"\"");
				} else {
					sbData.append(",\""+strData+"\"");
				}
			}
			
			if(sbData == null) {
				sbData = new StringBuilder();
			}
			request.setAttribute("sbData", sbData.toString());
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	public String loadSalaryYearlyReport(UtilityFunctions uF) {
		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)) {
			wLocationList = new FillWLocation(request).fillWLocation(getF_org(), (String)session.getAttribute(WLOCATION_ACCESS));
			orgList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
		}else{
			orgList = new FillOrganisation(request).fillOrganisation();
			wLocationList = new FillWLocation(request).fillWLocation(getF_org());
		}
		departmentList = new FillDepartment(request).fillDepartment(uF.parseToInt(getF_org()));
		levelList = new FillLevel(request).fillLevel(uF.parseToInt(getF_org()));
		serviceList = new FillServices(request).fillServices(getF_org(), uF);

		getSelectedFilter(uF);
		
		return LOAD;
	}
	private void getSelectedFilter(UtilityFunctions uF) {
		Map<String,String> hmFilter=new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();

		alFilter.add("ORGANISATION");
		if(getF_org()!=null)  {
			String strOrg="";
			int k=0;
			for(int i=0;orgList!=null && i<orgList.size();i++) {
				if(getF_org().equals(orgList.get(i).getOrgId())) {
					if(k==0) {
						strOrg=orgList.get(i).getOrgName();
					} else {
						strOrg+=", "+orgList.get(i).getOrgName();
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
		if(getF_strWLocation()!=null) {
			String strLocation="";
			int k=0;
			for(int i=0;wLocationList!=null && i<wLocationList.size();i++) {
				for(int j=0;j<getF_strWLocation().length;j++) {
					if(getF_strWLocation()[j].equals(wLocationList.get(i).getwLocationId())) {
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
				hmFilter.put("SERVICE", "All SBUs");
			}
		} else {
			hmFilter.put("SERVICE", "All SBUs");
		}
		
		alFilter.add("LEVEL");
		if(getF_level()!=null) {
			String strLevel="";
			int k=0;
			for(int i=0;levelList!=null && i<levelList.size();i++) {
				for(int j=0;j<getF_level().length;j++) {
					if(getF_level()[j].equals(levelList.get(i).getLevelId())) {
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
				hmFilter.put("LEVEL", "All Level's");
			}
		} else {
			hmFilter.put("LEVEL", "All Level's");
		}

		String selectedFilter=CF.getSelectedFilter2(CF,uF,alFilter,hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}
	
	
	public String viewSalaryYearlyReport(UtilityFunctions uF) {
	
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			
			con = db.makeConnection(con);
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			StringBuilder sbQuery=new StringBuilder();
			sbQuery.append("select count(eod.emp_id) as empCount from employee_official_details eod,employee_personal_details epd " +
				"where epd.emp_per_id=eod.emp_id and epd.is_alive=true ");
			
			if(uF.parseToInt(getF_org())>0) {
				sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
			}else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
					sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			if(getF_strWLocation()!=null && getF_strWLocation().length>0) {
	            sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
	        }else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
				sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
            if(getF_department()!=null && getF_department().length>0) {
                sbQuery.append(" and depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
            }
            if(getF_level()!=null && getF_level().length>0) {
                sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
            }
            if(getF_service()!=null && getF_service().length>0) {
                sbQuery.append(" and (");
                for(int i=0; i<getF_service().length; i++) {
                    sbQuery.append(" service_id like '%,"+getF_service()[i]+",%'");
                    
                    if(i<getF_service().length-1) {
                        sbQuery.append(" OR "); 
                    }
                }
                sbQuery.append(" ) ");
                
            }
            if(getStrSearch()!=null && !getStrSearch().trim().equals("") && !getStrSearch().trim().equalsIgnoreCase("NULL")) {
            	if(flagMiddleName) {
					sbQuery.append(" and (upper(epd.emp_fname)||' '||upper(epd.emp_mname)||' '||upper(epd.emp_lname) like '%"+getStrSearch().trim().toUpperCase()+"%' or upper(epd.emp_fname)||' '||upper(epd.emp_lname) like '%"+getStrSearch().trim().toUpperCase()+"%')");
				} else {
					sbQuery.append(" and upper(epd.emp_fname)||' '||upper(epd.emp_lname) like '%"+getStrSearch().trim().toUpperCase()+"%'");
				}
			}
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();
			int recCnt = 0;
			int pageCount = 0;
			while (rs.next()) {
				recCnt = rs.getInt("empCount");
				pageCount = rs.getInt("empCount")/100;
				if(rs.getInt("empCount")%100 != 0) {
					pageCount++;
				}
			}
			rs.close();
			pst.close();
			request.setAttribute("pageCount", pageCount+"");
			request.setAttribute("recCnt", recCnt+"");
			
			sbQuery=new StringBuilder();
			sbQuery.append("select * from employee_official_details eod,employee_personal_details epd where epd.emp_per_id=eod.emp_id and epd.is_alive=true ");
			
			if(uF.parseToInt(getF_org())>0) {
				sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
			}else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
					sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			if(getF_strWLocation()!=null && getF_strWLocation().length>0) {
	            sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
	        }else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
				sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
            if(getF_department()!=null && getF_department().length>0) {
                sbQuery.append(" and depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
            }
            if(getF_level()!=null && getF_level().length>0) {
                sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
            }
            if(getF_service()!=null && getF_service().length>0) {
                sbQuery.append(" and (");
                for(int i=0; i<getF_service().length; i++) {
                    sbQuery.append(" service_id like '%,"+getF_service()[i]+",%'");
                    
                    if(i<getF_service().length-1) {
                        sbQuery.append(" OR "); 
                    }
                }
                sbQuery.append(" ) ");
                
            }
            if(getStrSearch()!=null && !getStrSearch().trim().equals("") && !getStrSearch().trim().equalsIgnoreCase("NULL")) {
            	if(flagMiddleName) {
					sbQuery.append(" and (upper(epd.emp_fname)||' '||upper(epd.emp_mname)||' '||upper(epd.emp_lname) like '%"+getStrSearch().trim().toUpperCase()+"%' or upper(epd.emp_fname)||' '||upper(epd.emp_lname) like '%"+getStrSearch().trim().toUpperCase()+"%')");
				} else {
					sbQuery.append(" and upper(epd.emp_fname)||' '||upper(epd.emp_lname) like '%"+getStrSearch().trim().toUpperCase()+"%'");
				}
			}
			sbQuery.append(" order by epd.emp_fname, epd.emp_lname");
			int intOffset = uF.parseToInt(getMinLimit());
			sbQuery.append(" limit 100 offset "+intOffset+"");
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();
			List<String> alEmp = new ArrayList<String>();
			Map<String,String> hmEmpName = new HashMap<String,String>();
			Map<String,String> hmEmpCode = new HashMap<String,String>();
			Map<String,String> hmEmpPanNo = new HashMap<String,String>();
			while (rs.next()) {
				if(!alEmp.contains(rs.getString("emp_per_id"))) {
					alEmp.add(rs.getString("emp_per_id"));
					
					//String strMiddleName = (rs.getString("emp_mname") != null && !rs.getString("emp_mname").trim().equals("")) ? rs.getString("emp_mname").trim() + " " : "";
					String strEmpMName = "";
					if(flagMiddleName) {
						if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
							strEmpMName = " "+rs.getString("emp_mname");
						}
					}
				
					String strEmpName = rs.getString("emp_fname") +strEmpMName+ " " + rs.getString("emp_lname");
					hmEmpName.put(rs.getString("emp_per_id"), strEmpName);
					hmEmpCode.put(rs.getString("emp_per_id"), rs.getString("empcode"));
					hmEmpPanNo.put(rs.getString("emp_per_id"), rs.getString("emp_pan_no"));
				}
			}
			rs.close();
			pst.close();
			
			if(alEmp.size() > 0) {
				String strEmpIds = StringUtils.join(alEmp.toArray(),",");
				
				Map<String, String> hmEmpWLocation = CF.getEmpWLocationMap(con,uF,strEmpIds);
				Map<String, String> hmEmpDesignation = CF.getEmpDesignationMap(con,uF,strEmpIds);
				Map<String, String> hmEmpGrade = CF.getEmpGradeMap(con,uF,strEmpIds);
				
				int nSalaryStrucuterType = uF.parseToInt(CF.getStrSalaryStructure());
				
				MyProfile myProfile = new MyProfile();
				myProfile.session = session;
				myProfile.request = request;
				myProfile.CF = CF;
				
				List<List<String>> reportList = new ArrayList<List<String>>();
				for(int j = 0; j < alEmp.size(); j++) {
					String strEmpId = alEmp.get(j);
					int nEmpId = uF.parseToInt(alEmp.get(j));
					
					Map<String, String> hmEmpProfile = CF.getEmpProfileDetail(con, request, session, CF, uF, null, strEmpId);
					if(nSalaryStrucuterType == S_GRADE_WISE) {
						myProfile.getSalaryHeadsforEmployeeByGrade(con, uF, nEmpId, hmEmpProfile);
					} else {
						myProfile.getSalaryHeadsforEmployee(con, uF, nEmpId, hmEmpProfile);
					}
					
					String[] strFinancialYearDates = new FillFinancialYears(request).fillLatestFinancialYears();
					String strFinancialYearStart = strFinancialYearDates[0];
					String strFinancialYearEnd = strFinancialYearDates[1];

					String strOrgId = CF.getEmpOrgId(con, uF, strEmpId);
					String strLevelId = CF.getEmpLevelId(con, strEmpId);
					
					String currDate = uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT);
					String[] strPayCycleDate = CF.getPayCycleFromDate(con, currDate, CF.getStrTimeZone(), CF, strOrgId);
					
					Map<String, String> hmReimCTC = new HashMap<String, String>();
					Map<String, String> hmReimCTCHeadAmount = new HashMap<String, String>();
					
					if(strPayCycleDate !=null && strPayCycleDate.length > 0) {
						String startDate = strPayCycleDate[0];
						String endDate = strPayCycleDate[1];
						String strPC = strPayCycleDate[2];
					
						CF.getReimbursementCTC(con, uF, uF.parseToInt(strEmpId), strFinancialYearStart, strFinancialYearEnd, startDate, endDate, strPC, uF.parseToInt(strOrgId), uF.parseToInt(strLevelId), hmReimCTC);
						
						CF.getReimbursementCTCHeadAmount(con, uF, uF.parseToInt(strEmpId), strFinancialYearStart, strFinancialYearEnd, startDate, endDate, strPC, uF.parseToInt(strOrgId), uF.parseToInt(strLevelId), hmReimCTCHeadAmount);
//						request.setAttribute("hmReimCTCHeadAmount", hmReimCTCHeadAmount);
					}
					List<List<String>> salaryHeadDetailsList = (List<List<String>>) request.getAttribute("salaryHeadDetailsList");
					
//					if(uF.parseToInt(strEmpId) == 174) {
//						System.out.println("salaryHeadDetailsList ===>> " + salaryHeadDetailsList);
//					}
					double grossAmount = 0.0d;
                    double grossYearAmount = 0.0d;
					double netTakeHome = 0.0d;
                    for(int i=0; salaryHeadDetailsList != null && !salaryHeadDetailsList.isEmpty() && i<salaryHeadDetailsList.size(); i++) {
                    	List<String> innerList = salaryHeadDetailsList.get(i);
                		if(innerList.get(1).equals("E")) {
                			double dblEarnMonth = uF.parseToDouble(innerList.get(2));
							double dblEarnAnnual = uF.parseToDouble(innerList.get(3));
							grossAmount += dblEarnMonth;
							grossYearAmount += dblEarnAnnual;
							
							netTakeHome += dblEarnMonth;
                		}
                    }
//                  if(uF.parseToInt(strEmpId) == 174) {
//						System.out.println("netTakeHome E ===>> " + netTakeHome);
//					}
                    double deductAmount = 0.0d;
                    double deductYearAmount = 0.0d;
                    
                    for(int i=0; salaryHeadDetailsList != null && !salaryHeadDetailsList.isEmpty() && i<salaryHeadDetailsList.size(); i++) {
                    	List<String> innerList = salaryHeadDetailsList.get(i);
                    	if(innerList.get(1).equals("D")) {
                    		double dblDeductMonth = uF.parseToDouble(innerList.get(2));
							double dblDeductAnnual = uF.parseToDouble(innerList.get(3));
							deductAmount += dblDeductMonth;
							deductYearAmount += dblDeductAnnual;
							
							netTakeHome -= dblDeductMonth;
                    	}
                    }
//                  if(uF.parseToInt(strEmpId) == 174) {
//						System.out.println("netTakeHome D ===>> " + netTakeHome);
//					}
                    double dblCTCMonthly = grossAmount;
					double dblCTCAnnualy = grossYearAmount;
//					Map<String, String> hmReimCTC = (Map<String, String>)request.getAttribute("hmReimCTC");
					if(hmReimCTC == null) hmReimCTC = new HashMap<String, String>();
					
//					Map<String, String> hmReimCTCHeadAmount = (Map<String, String>)request.getAttribute("hmReimCTCHeadAmount");
					if(hmReimCTCHeadAmount == null) hmReimCTCHeadAmount = new HashMap<String, String>();
//					if(uF.parseToInt(strEmpId) == 174) {
//						System.out.println("hmReimCTC ===>> " + hmReimCTC);
//						System.out.println("hmReimCTCHeadAmount ===>> " + hmReimCTCHeadAmount);
//					}
					if(hmReimCTC.size() > 0 && hmReimCTCHeadAmount.size() > 0) {
						double grossReimbursementAmount = 0.0d;
						double grossReimbursementYearAmount = 0.0d;
						Iterator<String> it = hmReimCTC.keySet().iterator();
						while(it.hasNext()) {
							String strReimCTCId = it.next();
//							String strReimCTCName = hmReimCTC.get(strReimCTCId);
							
							double dblReimMonth = uF.parseToDouble(hmReimCTCHeadAmount.get(strReimCTCId));
							double dblReimAnnual = uF.parseToDouble(hmReimCTCHeadAmount.get(strReimCTCId+"_ANNUAL"));
							grossReimbursementAmount += dblReimMonth;
							grossReimbursementYearAmount += dblReimAnnual;
							
							netTakeHome += dblReimMonth;
						}	
						dblCTCMonthly += grossReimbursementAmount;
						dblCTCAnnualy += grossReimbursementYearAmount;
					}
					
//					if(uF.parseToInt(strEmpId) == 174) {
//						System.out.println("netTakeHome R ===>> " + netTakeHome);
//					}
					
					List<List<String>> salaryAnnualVariableDetailsList = (List<List<String>>)request.getAttribute("salaryAnnualVariableDetailsList");
					if(salaryAnnualVariableDetailsList == null) salaryAnnualVariableDetailsList = new ArrayList<List<String>>();
					
					int nAnnualVariSize = salaryAnnualVariableDetailsList.size();
//					if(uF.parseToInt(strEmpId) == 174) {
//						System.out.println("salaryAnnualVariableDetailsList ===>> " + salaryAnnualVariableDetailsList);
//						System.out.println("nAnnualVariSize ===>> " + nAnnualVariSize);
//					}
					if(nAnnualVariSize > 0) {
						double grossAnnualAmount = 0.0d;
						double grossAnnualYearAmount = 0.0d;
						for(int i = 0; i < nAnnualVariSize; i++) {
							List<String> innerList = salaryAnnualVariableDetailsList.get(i);
							double dblEarnMonth = uF.parseToDouble(innerList.get(2));
							double dblEarnAnnual = uF.parseToDouble(innerList.get(3));
							grossAnnualAmount += dblEarnMonth;
							grossAnnualYearAmount += dblEarnAnnual;
						}
//						if(uF.parseToInt(strEmpId) == 174) {
//							System.out.println("dblCTCAnnualy before grossAnnualYearAmount ===>> " + dblCTCAnnualy);
//						}
						dblCTCMonthly += grossAnnualAmount;
						dblCTCAnnualy += grossAnnualYearAmount;
//						if(uF.parseToInt(strEmpId) == 174) {
//							System.out.println("dblCTCAnnualy after grossAnnualYearAmount ===>> " + dblCTCAnnualy);
//						}
					}
					
					Map<String,String> hmContribution = (Map<String,String>) request.getAttribute("hmContribution");
					if(hmContribution == null) hmContribution = new HashMap<String, String>();
//					if(uF.parseToInt(strEmpId) == 174) {
//						System.out.println("hmContribution ===>> " + hmContribution);
//					}
					double dblMonthContri = 0.0d;
					double dblAnnualContri = 0.0d;
					boolean isEPF = uF.parseToBoolean((String)request.getAttribute("isEPF"));
					boolean isESIC = uF.parseToBoolean((String)request.getAttribute("isESIC"));
					boolean isLWF = uF.parseToBoolean((String)request.getAttribute("isLWF"));
					if(isEPF || isESIC || isLWF) {
						if(isEPF) {
							double dblEPFMonth = uF.parseToDouble(hmContribution.get("EPF_MONTHLY"));
							double dblEPFAnnual = uF.parseToDouble(hmContribution.get("EPF_ANNUALY"));
							dblMonthContri += dblEPFMonth;
							dblAnnualContri += dblEPFAnnual;
						}
						if(isESIC) {
							double dblESIMonth = uF.parseToDouble(hmContribution.get("ESI_MONTHLY"));
							double dblESIAnnual = uF.parseToDouble(hmContribution.get("ESI_ANNUALY"));
							dblMonthContri += dblESIMonth;
							dblAnnualContri += dblESIAnnual;
						}	
						if(isLWF) {
							double dblLWFMonth = uF.parseToDouble(hmContribution.get("LWF_MONTHLY"));
							double dblLWFAnnual = uF.parseToDouble(hmContribution.get("LWF_ANNUALY"));
							dblMonthContri += dblLWFMonth;
							dblAnnualContri += dblLWFAnnual;
						}
						
						dblCTCMonthly += dblMonthContri;
						dblCTCAnnualy += dblAnnualContri;
					}
//					if(uF.parseToInt(strEmpId) == 174) {
//						System.out.println("dblCTCAnnualy after Contribution ===>> " + hmContribution);
//					}
					
					List<String> alInner = new ArrayList<String>();
					alInner.add(uF.showData(hmEmpCode.get(strEmpId), ""));
					alInner.add(uF.showData(hmEmpName.get(strEmpId), ""));
					alInner.add(uF.showData(hmEmpPanNo.get(strEmpId), ""));
					alInner.add(uF.showData(hmEmpDesignation.get(strEmpId+"_EMP_DESIGNATION_NAME"), ""));
					alInner.add(uF.showData(hmEmpGrade.get(strEmpId+"_EMP_GRADE_NAME"), ""));
					alInner.add(uF.showData(hmEmpWLocation.get(strEmpId+"_EMP_WLOCATION_NAME"), ""));
					alInner.add(uF.formatIntoOneDecimal(dblCTCAnnualy));
					alInner.add(uF.formatIntoOneDecimal(dblCTCMonthly));
					alInner.add(uF.formatIntoOneDecimal(netTakeHome));
					alInner.add(uF.formatIntoOneDecimal(grossAmount));
					alInner.add(uF.formatIntoOneDecimal(deductAmount));
					alInner.add(uF.formatIntoOneDecimal(dblMonthContri));
					
					reportList.add(alInner);
				}
				
//				System.out.println("reportList ===>> " + reportList);
				
				request.setAttribute("reportList", reportList);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;
	
	}

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}
	private HttpServletResponse response;

	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
	}

	public String getF_org() {
		return f_org;
	}

	public void setF_org(String f_org) {
		this.f_org = f_org;
	}

	public String[] getF_strWLocation() {
		return f_strWLocation;
	}

	public void setF_strWLocation(String[] f_strWLocation) {
		this.f_strWLocation = f_strWLocation;
	}

	public String[] getF_department() {
		return f_department;
	}

	public void setF_department(String[] f_department) {
		this.f_department = f_department;
	}

	public String[] getF_level() {
		return f_level;
	}

	public void setF_level(String[] f_level) {
		this.f_level = f_level;
	}

	public String[] getF_service() {
		return f_service;
	}

	public void setF_service(String[] f_service) {
		this.f_service = f_service;
	}

	public List<FillOrganisation> getOrgList() {
		return orgList;
	}

	public void setOrgList(List<FillOrganisation> orgList) {
		this.orgList = orgList;
	}

	public List<FillWLocation> getwLocationList() {
		return wLocationList;
	}

	public void setwLocationList(List<FillWLocation> wLocationList) {
		this.wLocationList = wLocationList;
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

	public String getStrLocation() {
		return strLocation;
	}

	public void setStrLocation(String strLocation) {
		this.strLocation = strLocation;
	}

	public String getStrDepartment() {
		return strDepartment;
	}

	public void setStrDepartment(String strDepartment) {
		this.strDepartment = strDepartment;
	}

	public String getStrSbu() {
		return strSbu;
	}

	public void setStrSbu(String strSbu) {
		this.strSbu = strSbu;
	}

	public String getStrLevel() {
		return strLevel;
	}

	public void setStrLevel(String strLevel) {
		this.strLevel = strLevel;
	}
	
	public String getProPage() {
		return proPage;
	}

	public void setProPage(String proPage) {
		this.proPage = proPage;
	}

	public String getMinLimit() {
		return minLimit;
	}

	public void setMinLimit(String minLimit) {
		this.minLimit = minLimit;
	}
	
	public String getStrSearch() {
		return strSearch;
	}

	public void setStrSearch(String strSearch) {
		this.strSearch = strSearch;
	}
}