package com.konnect.jpms.payroll;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillMonth;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.select.FillYears;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;
 
public class PayArrears extends ActionSupport implements ServletRequestAware, IStatements {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strEmpId = null;
	String strUserType = null;
	
	CommonFunctions CF = null;
	String profileEmpId;

	String strLocation;
	String strDepartment;
	String strSbu;
	String strLevel;
	
	String[] f_strWLocation;
	String[] f_level;
	String[] f_department;
	String[] f_service;
	String f_org;
	
	List<FillDepartment> departmentList;
	List<FillLevel> levelList;
	List<FillServices> serviceList;
	List<FillWLocation> wLocationList;
	List<FillOrganisation> organisationList;
	

	String strMonth;
	String strYear;
	List<FillMonth> monthList;
	List<FillYears> yearList;
	
	String strBaseUserType = null;
	String strAction = null;
	
	private static Logger log = Logger.getLogger(PayArrears.class);
	
	public String execute() throws Exception {
		
		try {
			UtilityFunctions uF = new UtilityFunctions();
			session = request.getSession();
			request.setAttribute(PAGE, PPayArears);
			request.setAttribute(TITLE, "Apply Arrears");
			CF = (CommonFunctions) session.getAttribute(CommonFunctions);
			if(CF==null)return LOGIN;
			
			strEmpId = (String)session.getAttribute(EMPID);
			strUserType = (String)session.getAttribute(USERTYPE);
			strBaseUserType = (String)session.getAttribute(BASEUSERTYPE);//Created By Dattatray 14-06-2022

			//Created By Dattatray 14-06-2022
					strAction = request.getServletPath();
					if(strAction!=null) {
						strAction = strAction.replace("/","");
					}
//			boolean isView  = CF.getAccess(session, request, uF);
//			if(!isView) {
//				request.setAttribute(PAGE, PAccessDenied);
//				request.setAttribute(TITLE, TAccessDenied);
//				return ACCESS_DENIED;
//			}
//			if(getF_org()==null) {
//				setF_org((String)session.getAttribute(ORGID));
//			}
		
			/*organisationList = new FillOrganisation(request).fillOrganisation();
			if(getF_org()==null) {
				setF_org(organisationList.get(0).getOrgId());
			}*/
			
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
			
			viewArears(uF);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return loadArears();

	}
	
	
	public String loadArears() {		
		UtilityFunctions uF=new UtilityFunctions();
		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)) {
			wLocationList = new FillWLocation(request).fillWLocation(getF_org(), (String)session.getAttribute(WLOCATION_ACCESS));
			organisationList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
		} else {
			organisationList = new FillOrganisation(request).fillOrganisation();
			wLocationList = new FillWLocation(request).fillWLocation(getF_org());
		}
		departmentList = new FillDepartment(request).fillDepartment(uF.parseToInt(getF_org()));
		levelList = new FillLevel(request).fillLevel(uF.parseToInt(getF_org()));
		serviceList = new FillServices(request).fillServices(getF_org(),uF);
		monthList = new FillMonth().fillMonth();
		yearList = new FillYears().fillFutureYears(uF.getCurrentDate(CF.getStrTimeZone()), 2, 2);
		
		getSelectedFilter(uF);
		loadPageVisitAuditTrail();//Created By Dattatray 14-06-2022
		return LOAD;
	}
	
	//Created By Dattatray 14-06-2022
		private void loadPageVisitAuditTrail() {
			Connection con=null;
			Database db = new Database();
			db.setRequest(request);
			UtilityFunctions uF=new UtilityFunctions();
			try {
				con = db.makeConnection(con);
//				Map<String, String> hmEmpProfile = CF.getEmpNameMap(con,null,getEmployee());
				StringBuilder builder = new StringBuilder();
				builder.append("Filter : ");
				builder.append("\nOrganisation : "+getF_org());
				builder.append("\nMonth : "+getStrMonth());
				builder.append("\nYear : "+getStrYear());
				builder.append("\nLocation : "+StringUtils.join(getF_strWLocation(),","));
				builder.append("\nDepartment : "+StringUtils.join(getF_department(),","));
				builder.append("\nLevel : "+StringUtils.join(getF_level(),","));
				builder.append("\nService : "+StringUtils.join(getF_service(),","));
				
				CF.pageVisitAuditTrail(con,CF,uF, strEmpId, strAction, strBaseUserType, builder.toString());
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}finally {
				db.closeConnection(con);
			}
			
		}
		
	public String viewArears(UtilityFunctions uF) {
		
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			List<List<String>> alArearList = new ArrayList<List<String>>();
			con = db.makeConnection(con);
			
			Map<String, String> hmEmpMap = CF.getEmpNameMap(con,null, null);
			if(getStrMonth() ==null) {
				setStrMonth(uF.parseToInt(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, "MM"))+"");
				setStrYear(uF.parseToInt(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, "yyyy"))+"");
			}
			
			Calendar calendar = Calendar.getInstance();         
//	        calendar.add(Calendar.MONTH, 1);
	        calendar.set(uF.parseToInt(getStrYear()), uF.parseToInt(getStrMonth())-1, 1);
	        calendar.set(Calendar.DATE, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
	        String startDate = ((calendar.get(Calendar.DAY_OF_MONTH) < 10) ? "0" + calendar.get(Calendar.DAY_OF_MONTH) : calendar.get(Calendar.DAY_OF_MONTH)) + "/" + (((calendar.get(Calendar.MONTH) + 1) < 10) ? "0" + (calendar.get(Calendar.MONTH) + 1) : (calendar.get(Calendar.MONTH) + 1)) + "/"+ calendar.get(Calendar.YEAR);
	        calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
	        String endDate = ((calendar.get(Calendar.DAY_OF_MONTH) < 10) ? "0" + calendar.get(Calendar.DAY_OF_MONTH) : calendar.get(Calendar.DAY_OF_MONTH)) + "/" + (((calendar.get(Calendar.MONTH) + 1) < 10) ? "0" + (calendar.get(Calendar.MONTH) + 1) : (calendar.get(Calendar.MONTH) + 1)) + "/"+ calendar.get(Calendar.YEAR);;

	        StringBuilder sbQuery = new StringBuilder();
//			sbQuery.append("select * from arear_details ad, employee_official_details eod where ad.emp_id = eod.emp_id and arrear_type=0 and effective_date between ? and ? ");
	        sbQuery.append("select ad.* from arear_details ad, employee_official_details eod where ad.emp_id = eod.emp_id and ((arrear_type=0 and effective_date between ? and ?) or (arrear_type=2) or (arrear_type=1 and entry_date between ? and ?)) ");
			if(getF_level()!=null && getF_level().length>0) {
                sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd, level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
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
            if(getF_strWLocation()!=null && getF_strWLocation().length>0) {
                sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
            }else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
				sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
            
            if(uF.parseToInt(getF_org())>0) {
				sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
			}else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
				sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
            
            /*sbQuery.append(" union ");
            sbQuery.append("select * from arear_details ad, employee_official_details eod where ad.emp_id = eod.emp_id and arrear_type=1 and entry_date between ? and ? ");
			if(getF_level()!=null && getF_level().length>0) {
		        sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd, level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
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
		    if(getF_strWLocation()!=null && getF_strWLocation().length>0) {
		        sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
		    } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
				sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
		    if(uF.parseToInt(getF_org())>0) {
				sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
			} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
				sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}*/
            
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(startDate, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(endDate, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(startDate, DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(endDate, DATE_FORMAT));
			System.out.println("pst=======> " + pst);
			rs = pst.executeQuery();
			while(rs.next()) {
				List<String> alInner = new ArrayList<String>();	
				alInner.add(rs.getString("arear_id"));
				alInner.add(uF.showData(rs.getString("arear_code"), ""));
				alInner.add(uF.showData(rs.getString("arear_name"), ""));
				alInner.add(uF.showData(rs.getString("arear_description"), ""));
				alInner.add(uF.showData(hmEmpMap.get(rs.getString("emp_id")), ""));
				alInner.add(uF.getDateFormat(rs.getString("effective_date"), DBDATE, CF.getStrReportDateFormat()));
				if(uF.parseToInt(rs.getString("duration_months"))==1) {
					alInner.add(rs.getString("duration_months")+" month");
				} else {
					alInner.add(rs.getString("duration_months")+" months");
				}
				alInner.add(uF.showData(rs.getString("arear_amount"), "0"));
				alInner.add(uF.showData(rs.getString("total_amount_paid"), "0"));
				alInner.add(uF.showData(rs.getString("arear_amount_balance"), "0"));
				alInner.add(uF.showData(rs.getString("monthly_arear"), "0"));
				if(!rs.getBoolean("is_paid")) {
					if(rs.getInt("arrear_type") == 2) {
						if(rs.getInt("is_approved") == 0) {
						alInner.add("<a onclick=\"approveDenyArrear("+rs.getString("arear_id")+","+"'"+hmEmpMap.get(rs.getString("emp_id"))+"', 'A')\" href=\"javascript:void(0)\"><i class=\"fa fa-check-circle checknew\" aria-hidden=\"true\"></i></a> " +
							"<a href=\"javascript:void(0);\" onclick=\"approveDenyArrear("+rs.getString("arear_id")+","+"'"+hmEmpMap.get(rs.getString("emp_id"))+"', 'D')\"><i class=\"fa fa-times-circle cross\" aria-hidden=\"true\"></i></a>");
						} else if(rs.getInt("is_approved") == 1) {
							alInner.add("Approved");
						} else {
							alInner.add("Denied");
						}
					} else {
						alInner.add("<a onclick=\"addArear("+rs.getString("arear_id")+","+"'"+hmEmpMap.get(rs.getString("emp_id"))+"')\" class=\"edit\" href=\"javascript:void(0)\"></a> " +
							"<a href=\"javascript:void(0);\" onclick=\"deleteArrear("+rs.getString("arear_id")+")\"><i class=\"fa fa-trash\" aria-hidden=\"true\"></i></a>");
					}
				} else {
					alInner.add("Paid");
				}
				alInner.add(uF.showData(rs.getString("basic_amount"), "0"));
				
				String strArrearType = "Amount";
				String strArrearDays = "0";
				String strArrearPaycycle = "";
				if(uF.parseToInt(rs.getString("arrear_type")) == 1) {
					strArrearType = "Days";
					strArrearDays = ""+uF.parseToDouble(rs.getString("arrear_days"));
					
					String dt1 = uF.getDateFormat(rs.getString("paycycle_from"), DBDATE, DATE_FORMAT);
					String dt2 = uF.getDateFormat(rs.getString("paycycle_to"), DBDATE, DATE_FORMAT);
					int nPayCycle = uF.parseToInt(rs.getString("paycycle"));
					strArrearPaycycle = "Paycycle "+nPayCycle+", "+dt1+" - "+dt2;					
				} else if(uF.parseToInt(rs.getString("arrear_type")) == 2) {
					strArrearType = rs.getString("arear_name");
					strArrearDays = ""+uF.parseToDouble(rs.getString("arrear_days"));					
					List<String> alPaycycles = new ArrayList<String>();
					StringBuilder sbPaycycle = null;
					if(rs.getString("paycycles") != null && !rs.getString("paycycles").equals("")) {
						String strEmpOrgId = CF.getEmpOrgId(con, uF, rs.getString("emp_id"));
						alPaycycles = Arrays.asList(rs.getString("paycycles").split(","));
						for(int i=0; alPaycycles != null && i<alPaycycles.size(); i++) {
							String[] arPaycycle = CF.getPayCycleDatesOnPaycycleId(con, alPaycycles.get(i), strEmpOrgId, CF.getStrTimeZone(), CF, request);
//							String dt1 = uF.getDateFormat(rs.getString("paycycle_from"), DBDATE, DATE_FORMAT);
//							String dt2 = uF.getDateFormat(rs.getString("paycycle_to"), DBDATE, DATE_FORMAT);
//							int nPayCycle = uF.parseToInt(rs.getString("paycycle"));
							if(sbPaycycle == null) { 
								sbPaycycle = new StringBuilder();
								sbPaycycle.append("Paycycle "+arPaycycle[2]+", "+arPaycycle[0]+" - "+arPaycycle[1]);
							} else {
								sbPaycycle.append(",<br/>  Paycycle "+arPaycycle[2]+", "+arPaycycle[0]+" - "+arPaycycle[1]);
							}
						}
						if(sbPaycycle != null) {
							strArrearPaycycle = sbPaycycle.toString();
						}
					}
										
				}
				alInner.add(strArrearType);
				alInner.add(strArrearDays);
				alInner.add(strArrearPaycycle);
				
				alArearList.add(alInner);
			}
			rs.close();
			pst.close();
			request.setAttribute("alArearList", alArearList);
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;
	}
	
private void getSelectedFilter(UtilityFunctions uF) {
		
	Map<String,String> hmFilter=new HashMap<String, String>();
	List<String> alFilter = new ArrayList<String>();

	alFilter.add("ORGANISATION");
	if(getF_org()!=null)  {
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
	
	alFilter.add("MONTH");
	if(getStrMonth()!=null) {
		String strMonth="";
		for(int i=0;monthList!=null && i<monthList.size();i++) {
			if(getStrMonth().equals(monthList.get(i).getMonthId())) {
				strMonth=monthList.get(i).getMonthName();
			}
		}
		if(strMonth!=null && !strMonth.equals("")) {
			hmFilter.put("MONTH", strMonth);
		} else {
			hmFilter.put("MONTH", "-");
		}
	} else {
		hmFilter.put("MONTH", "-");
	}
	
	//if(strUserType != null && strUserType.equals(EMPLOYEE)) {
		alFilter.add("YEAR");
		if(getStrYear()!=null) {
			String strYear="";
			for(int i=0;yearList!=null && i<yearList.size();i++) {
				if(uF.parseToInt(getStrYear()) == yearList.get(i).getYearsID()) {
					strYear=yearList.get(i).getYearsName();
				}
			}
			if(strYear!=null && !strYear.equals("")) {
				hmFilter.put("YEAR", strYear);
			} else {
				hmFilter.put("YEAR", "-");
			}
		} else {
			hmFilter.put("YEAR", "-");
		}
	//}
	
	
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
			hmFilter.put("LEVEL", "All Levels");
		}
	} else {
		hmFilter.put("LEVEL", "All Levels");
	}
	
	
	String selectedFilter=CF.getSelectedFilter2(CF,uF,alFilter,hmFilter);
	request.setAttribute("selectedFilter", selectedFilter);
 }

	
	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
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

	public List<FillOrganisation> getOrganisationList() {
		return organisationList;
	}


	public void setOrganisationList(List<FillOrganisation> organisationList) {
		this.organisationList = organisationList;
	}

	public String[] getF_strWLocation() {
		return f_strWLocation;
	}

	public void setF_strWLocation(String[] f_strWLocation) {
		this.f_strWLocation = f_strWLocation;
	}


	public String[] getF_level() {
		return f_level;
	}


	public void setF_level(String[] f_level) {
		this.f_level = f_level;
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


	public String getF_org() {
		return f_org;
	}


	public void setF_org(String f_org) {
		this.f_org = f_org;
	}


	public String getStrMonth() {
		return strMonth;
	}


	public void setStrMonth(String strMonth) {
		this.strMonth = strMonth;
	}


	public String getStrYear() {
		return strYear;
	}


	public void setStrYear(String strYear) {
		this.strYear = strYear;
	}


	public List<FillMonth> getMonthList() {
		return monthList;
	}


	public void setMonthList(List<FillMonth> monthList) {
		this.monthList = monthList;
	}


	public List<FillYears> getYearList() {
		return yearList;
	}


	public void setYearList(List<FillYears> yearList) {
		this.yearList = yearList;
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
	
}