package com.konnect.jpms.performance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class ReviewDiscussionEmpList extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = -4581306410273940249L;
	HttpSession session;
	String strSessionEmpId;
	String strEmpOrgId; 
//	String strSessionUserType;
	String strUserTypeId;
	String strBaseUserTypeId;
	String strBaseUserType;
	
	CommonFunctions CF;
	
	String strUserType;
	private String dataType;
	
	private String strLocation;
	private String strDepartment;
	private String strLevel;
	
	private String f_org;
	
	private String proPage;
	private String minLimit;
	
	private String fromPage;
	private String currUserType;
	private String strEmpId;
	private String strSearchJob;
	
	public String execute() {
		session = request.getSession();
		strSessionEmpId = (String) session.getAttribute(EMPID);
		strEmpOrgId = (String) session.getAttribute(ORGID);
//		strSessionUserType = (String) session.getAttribute(USERTYPE);
		strUserTypeId = (String) session.getAttribute(USERTYPEID);
		strUserType = (String) session.getAttribute(USERTYPE);
		strBaseUserType = (String) session.getAttribute(BASEUSERTYPE);
		strBaseUserTypeId = (String) session.getAttribute(BASEUSERTYPEID);
		
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null) return "login";
		
		request.setAttribute("DOC_RETRIVE_LOCATION", CF.getStrDocRetriveLocation());
		UtilityFunctions uF = new UtilityFunctions();
		
		if(CF != null) {
			CF.getOrientationMemberDetails(request);
		}
		
		if(getF_org()==null) {
			setF_org((String)session.getAttribute(ORGID));
		}
		
		if(getDataType() == null || getDataType().trim().equals("") || getDataType().trim().equalsIgnoreCase("NULL")) {
			setDataType("L");
		}
		if(uF.parseToInt(getProPage()) == 0) {
			setProPage("1");
		}
		if(getCurrUserType()==null && strUserType != null && strUserType.equals(MANAGER)) {
			setCurrUserType("MYTEAM");
		}
		
		List<String> empList = getEmployeeList(uF);
		getEmpAppraisalDetails(uF,empList);
		return LOAD;
	}
	
	public List<String> getEmployeeList(UtilityFunctions uF) {
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		Map<String,String> hmEmpNames = new LinkedHashMap<String,String>();
		List<String> empList = new ArrayList<String>();
		try {
			
			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			
			if(strUserType!=null && strUserType.equals(EMPLOYEE)) {
				hmEmpNames.put(strSessionEmpId,"You");
				empList.add(strSessionEmpId);
			} else {
							
				StringBuilder sbQuery=new StringBuilder();
				sbQuery.append("select count(*) as cnt from employee_official_details eod,employee_personal_details epd where " +
					" epd.emp_per_id=eod.emp_id and is_alive = true ");

				if(uF.parseToInt(getF_org())>0) {
					sbQuery.append(" and eod.org_id = "+uF.parseToInt(getF_org()));
				} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
					sbQuery.append(" and eod.org_id in ("+(String)session.getAttribute(ORG_ACCESS)+") ");
				}
				
				if(getStrLocation()!=null && !getStrLocation().equals("") && !getStrLocation().equalsIgnoreCase("null")) {
	                sbQuery.append(" and eod.wlocation_id in ("+getStrLocation()+") ");
	            } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
					sbQuery.append(" and eod.wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+") ");
				}
				
				if(strUserType!=null && strUserType.equals(MANAGER) && strBaseUserType!=null && !getCurrUserType().equals(strBaseUserType)) {
					sbQuery.append(" and (eod.supervisor_emp_id = "+strSessionEmpId+" or eod.emp_id = "+strSessionEmpId+") ");
				}
				
				if(getStrDepartment()!=null && !getStrDepartment().equals("") && !getStrDepartment().equalsIgnoreCase("null")) {
		            sbQuery.append(" and eod.depart_id in ("+getStrDepartment()+") ");
	            }
	            if(getStrLevel()!=null && !getStrLevel().equals("") && !getStrLevel().equalsIgnoreCase("null")) {
	                sbQuery.append(" and eod.grade_id in (select gd.grade_id from grades_details gd, level_details ld, designation_details dd where gd.designation_id = dd.designation_id " +
	                		"and dd.level_id = ld.level_id  and ld.level_id in ( "+getStrLevel()+") ) ");
	            }
				
	            if(getStrSearchJob()!=null && !getStrSearchJob().trim().equals("") && !getStrSearchJob().trim().equalsIgnoreCase("null")) {
					sbQuery.append(" and upper(epd.emp_fname)|| ' '||upper(epd.emp_lname) like'%"+getStrSearchJob().trim().toUpperCase()+"%'");
				}
				pst = con.prepareStatement(sbQuery.toString());
//				System.out.println("pst1======>"+pst);
				rs=pst.executeQuery();
				int proCount = 0;
				int proCnt = 0;
				while(rs.next()) {
					proCnt = rs.getInt("cnt");
					proCount = rs.getInt("cnt")/15;
					if(rs.getInt("cnt")%15 != 0) {
						proCount++;
					}
				}
				rs.close();
				pst.close();
			//	System.out.println("proCount==>"+ proCount);
				request.setAttribute("proCount", proCount+"");
				request.setAttribute("proCnt", proCnt+"");
				
				int i=0;
				sbQuery=new StringBuilder();
				sbQuery.append("select * from employee_official_details eod,employee_personal_details epd where epd.emp_per_id=eod.emp_id " +
					"and is_alive = true ");
				if(uF.parseToInt(getF_org())>0) {
					sbQuery.append(" and eod.org_id = "+uF.parseToInt(getF_org()));
				} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
					sbQuery.append(" and eod.org_id in ("+(String)session.getAttribute(ORG_ACCESS)+") ");
				}
				
				if(getStrLocation()!=null && !getStrLocation().equals("") && !getStrLocation().equalsIgnoreCase("null")) {
	                sbQuery.append(" and eod.wlocation_id in ("+getStrLocation()+") ");
	            } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
					sbQuery.append(" and eod.wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+") ");
				}
					
				if(strUserType!=null && strUserType.equals(MANAGER) && strBaseUserType!=null && !getCurrUserType().equals(strBaseUserType)) {
					sbQuery.append(" and (eod.supervisor_emp_id = "+strSessionEmpId+" or eod.emp_id = "+strSessionEmpId+") ");
				}
					
				if(getStrDepartment()!=null && !getStrDepartment().equals("") && !getStrDepartment().equalsIgnoreCase("null")) {
		            sbQuery.append(" and eod.depart_id in ("+getStrDepartment()+") ");
	            }
				
	            if(getStrLevel()!=null && !getStrLevel().equals("") && !getStrLevel().equalsIgnoreCase("null")) {
	                sbQuery.append(" and eod.grade_id in (select gd.grade_id from grades_details gd, level_details ld, designation_details dd where gd.designation_id = dd.designation_id " +
	                		"and dd.level_id = ld.level_id  and ld.level_id in ( "+getStrLevel()+") ) ");
	            }
					
	            if(getStrSearchJob()!=null && !getStrSearchJob().trim().equals("") && !getStrSearchJob().trim().equalsIgnoreCase("null")) {
					sbQuery.append(" and upper(epd.emp_fname)|| ' '||upper(epd.emp_lname) like'%"+getStrSearchJob().trim().toUpperCase()+"%'");
				}		
				sbQuery.append(" order by epd.emp_fname");	

				Map<String, String> hmHrIds = new HashMap<String, String>();
				Map<String, String> hmEmpSuperIds = new HashMap<String, String>();
				int intOffset = uF.parseToInt(getMinLimit());
				sbQuery.append(" limit 15 offset "+intOffset+"");		
				pst = con.prepareStatement(sbQuery.toString());
				System.out.println("pst2======>"+pst);
				rs = pst.executeQuery();
				while (rs.next()) {
					if(i == 0) {
						if(uF.parseToInt(getStrEmpId())==0) {
							setStrEmpId(rs.getString("emp_per_id"));
						}
					}
					
					String strMiddleName = "";
					
					if(flagMiddleName) {
						if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
							strMiddleName = " "+rs.getString("emp_mname");
						}
					}
					
					
					hmEmpNames.put(rs.getString("emp_per_id"), rs.getString("emp_fname")+strMiddleName+" "+rs.getString("emp_lname"));
					empList.add(rs.getString("emp_per_id"));
					i++;
										
				}
				rs.close();
				pst.close();
							
			}
//			System.out.println("strEmpId ===>> " + getStrEmpId());
			
			pst=con.prepareStatement("select emp_image,emp_per_id from employee_personal_details ");
			rs=pst.executeQuery();
			Map<String,String> empImageMap=new HashMap<String,String>();
			while(rs.next()) {
				empImageMap.put(rs.getString("emp_per_id"),rs.getString("emp_image"));
			}
			rs.close();
			pst.close();
			
			request.setAttribute("strEmpId", getStrEmpId());
			request.setAttribute("empImageMap", empImageMap);
			request.setAttribute("hmEmpNames", hmEmpNames);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);			
		}
		return empList;
	}
	
	void getEmpAppraisalDetails(UtilityFunctions uF,List<String> empList) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
//			System.out.println("getEmpGoalKRADetails==>"+empList);
			Map<String, String> hmEmpwiseAppCnt = new LinkedHashMap<String, String>();
			for(int i=0;empList!=null && !empList.isEmpty() && i<empList.size();i++) {
				
				StringBuilder sbQuery = new StringBuilder();
				
				sbQuery.append("select * from appraisal_details a,appraisal_details_frequency adf  where a.appraisal_details_id = adf.appraisal_id "
					+" and (adf.is_delete is null or adf.is_delete = false ) and is_appraisal_publish=true and appraisal_details_id > 0 "); //my_review_status = 0 
				
				sbQuery.append(" and self_ids like '%,"+empList.get(i)+",%'");
				
				if(getDataType() != null && getDataType().equals("L")) {
					sbQuery.append(" and is_appraisal_close = false ");
				} else if(getDataType() != null && getDataType().equals("C")) {
					sbQuery.append(" and is_appraisal_close = true ");
				}
				
				sbQuery.append(" order by is_appraisal_close, is_appraisal_publish desc,freq_publish_expire_status desc,freq_end_date desc");
				
				pst=con.prepareStatement(sbQuery.toString());
				rs=pst.executeQuery();
				while (rs.next()) {
					
					int appraisalCnt = uF.parseToInt(hmEmpwiseAppCnt.get(empList.get(i)));
					appraisalCnt++;
					String strCnt = appraisalCnt+"";
					hmEmpwiseAppCnt.put(empList.get(i), strCnt);
				}
				rs.close();
				pst.close();
				
			}
			request.setAttribute("hmEmpwiseAppCnt", hmEmpwiseAppCnt);
		

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
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

	public String getStrLevel() {
		return strLevel;
	}

	public void setStrLevel(String strLevel) {
		this.strLevel = strLevel;
	}

	public String getF_org() {
		return f_org;
	}

	public void setF_org(String f_org) {
		this.f_org = f_org;
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

	public String getFromPage() {
		return fromPage;
	}

	public void setFromPage(String fromPage) {
		this.fromPage = fromPage;
	}

	public String getCurrUserType() {
		return currUserType;
	}

	public void setCurrUserType(String currUserType) {
		this.currUserType = currUserType;
	}

	public String getStrEmpId() {
		return strEmpId;
	}

	public void setStrEmpId(String strEmpId) {
		this.strEmpId = strEmpId;
	}

	public String getStrSearchJob() {
		return strSearchJob;
	}

	public void setStrSearchJob(String strSearchJob) {
		this.strSearchJob = strSearchJob;
	}
	
}
