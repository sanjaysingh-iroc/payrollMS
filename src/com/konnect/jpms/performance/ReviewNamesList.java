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

public class ReviewNamesList extends ActionSupport implements ServletRequestAware, IStatements {
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	String strUserTypeId = null;
	String strBaseUserType = null;
	String strBaseUserTypeId = null;

	
	CommonFunctions CF = null;

	String strSessionEmpId = null;

	private String dataType;
	private String proPage;
	private String minLimit;
	private String sProPage;
	private String sMinLimit;
	private String currUserType;
	private String strReviewId;
	private String appFreqId;	
	private String strSearchJob;

	private String callFrom;
	private String alertID;
	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;
			
		strUserType = (String) session.getAttribute(USERTYPE);
		strUserTypeId = (String) session.getAttribute(USERTYPEID);
		strBaseUserType = (String) session.getAttribute(BASEUSERTYPE);
		strBaseUserTypeId = (String) session.getAttribute(BASEUSERTYPEID);
		
		strSessionEmpId = (String) session.getAttribute(EMPID);

		UtilityFunctions uF = new UtilityFunctions();
		
		if(getDataType() == null || getDataType().equals("") || getDataType().equalsIgnoreCase("Null")) {
			setDataType("L");
		}
		
		if(uF.parseToInt(getProPage()) == 0) {
			setProPage("1");
		}
		
		if(getCurrUserType()==null && strUserType != null && strUserType.equals(MANAGER)) {
			setCurrUserType("MYTEAM");
		}
	
		if(getDataType() != null && !getDataType().equals("SRR")) {
			getReviewNamesList(uF);
		} else {
			getSelfReviewRequest(uF);
		}
		return LOAD;
	}
	
	private void getSelfReviewRequest(UtilityFunctions uF) {
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		Map<String,List<String>> hmSelfReviewNames = new LinkedHashMap<String,List<String>>();
		
		try {
			con = db.makeConnection(con);

			Map<String, String> hmUserTypeIdMap = CF.getUserTypeIdMap(con);
			if(hmUserTypeIdMap == null) hmUserTypeIdMap = new HashMap<String, String>();
			
			StringBuilder sbQuery=new StringBuilder();
			sbQuery.append("select effective_id, min(member_position) as member_position from work_flow_details wf where is_approved=0 " +
					"and effective_type='"+WORK_FLOW_SELF_REVIEW+"' and effective_id in (select appraisal_details_id from appraisal_details " +
					" where my_review_status = 1) group by effective_id");
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst1==>"+pst);
			rs = pst.executeQuery();
			Map<String, String> hmNextApproval = new LinkedHashMap<String, String>();
			while(rs.next()) {
				hmNextApproval.put(rs.getString("effective_id"), rs.getString("member_position"));
			}
			rs.close();
			pst.close();
//			System.out.println("hmNextApproval==>"+hmNextApproval);
			sbQuery=new StringBuilder();
			sbQuery.append("select effective_id,user_type_id,min(member_position)as member_position from work_flow_details wfd where emp_id=? " +
					" and is_approved=0 and effective_type='"+WORK_FLOW_SELF_REVIEW+"' and effective_id in (select appraisal_details_id " +
					" from appraisal_details where my_review_status = 1");
			if(strUserType != null && strUserType.equals(ADMIN)) {
				sbQuery.append(") and (user_type_id=? or user_type_id=?) ");
			} else {
				sbQuery.append(") and user_type_id=? ");
			}
			
			sbQuery.append(" group by effective_id,user_type_id");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1,uF.parseToInt(strSessionEmpId));
			if (strBaseUserType != null && strBaseUserType.equalsIgnoreCase(getCurrUserType())) {
				pst.setInt(2, uF.parseToInt(strBaseUserTypeId));
			} else {
				pst.setInt(2, uF.parseToInt(strUserTypeId));
			}
			if(strUserType != null && strUserType.equals(ADMIN)) {
				pst.setInt(3, uF.parseToInt(hmUserTypeIdMap.get(HRMANAGER)));
			}
			
//			System.out.println("pst2==>"+pst);
			rs = pst.executeQuery();
			Map<String, String> hmMemNextApproval = new HashMap<String, String>();
			while(rs.next()) {
				hmMemNextApproval.put(rs.getString("effective_id")+"_"+rs.getString("user_type_id"), rs.getString("member_position"));
			}
			rs.close();
			pst.close();
//			System.out.println("hmMemNextApproval==>"+hmMemNextApproval);
			
			sbQuery=new StringBuilder();
			sbQuery.append("select emp_id,effective_id,user_type_id from work_flow_details where effective_type='"+WORK_FLOW_SELF_REVIEW+"' and " +
				" effective_id in (select appraisal_details_id from appraisal_details where my_review_status = 1 ");
			if(strUserType != null && strUserType.equals(ADMIN)) {
				sbQuery.append(") and (user_type_id=? or user_type_id=?) ");
			} else {
				sbQuery.append(") and user_type_id=? ");
			}
			sbQuery.append(" order by effective_id,member_position");
			
			pst = con.prepareStatement(sbQuery.toString());
			if (strBaseUserType != null && strBaseUserType.equalsIgnoreCase(getCurrUserType())) {
				pst.setInt(1, uF.parseToInt(strBaseUserTypeId));
			} else {
				pst.setInt(1, uF.parseToInt(strUserTypeId));
			}
			if(strUserType != null && strUserType.equals(ADMIN)) {
				pst.setInt(2, uF.parseToInt(hmUserTypeIdMap.get(HRMANAGER)));
			}
//			System.out.println("pst3 ===>> " + pst);
			rs = pst.executeQuery();
			Map<String, List<String>> hmCheckEmp = new HashMap<String, List<String>>();
			Map<String, List<String>> hmCheckEmpUserType = new HashMap<String, List<String>>();
			while(rs.next()) {
				List<String> checkEmpList = hmCheckEmp.get(rs.getString("effective_id"));
				
				if(checkEmpList == null)checkEmpList = new ArrayList<String>();				
				checkEmpList.add(rs.getString("emp_id"));
				
				List<String> checkEmpUserTypeList = hmCheckEmpUserType.get(rs.getString("effective_id")+"_"+rs.getString("emp_id"));
				if(checkEmpUserTypeList == null)checkEmpUserTypeList = new ArrayList<String>();				
				checkEmpUserTypeList.add(rs.getString("user_type_id"));
//				System.out.println(".......................................................");
//				System.out.println("appId==>"+rs.getString("effective_id"));
//				System.out.println("checkEmpList==>"+checkEmpList);
//				System.out.println("checkEmpUserTypeList==>"+checkEmpUserTypeList);
				hmCheckEmp.put(rs.getString("effective_id"), checkEmpList);
				hmCheckEmpUserType.put(rs.getString("effective_id")+"_"+rs.getString("emp_id"), checkEmpUserTypeList);
			}
			rs.close();
			pst.close();

//			System.out.println("hmCheckEmp==>"+hmCheckEmp);
//			System.out.println("checkEmpUserTypeList==>"+hmCheckEmpUserType);
			
			int j = 0;
			Map<String, String> hmOrientationName = CF.getOrientationValue(con);
//			List<String> selfReviewRequestList = new ArrayList<String>();
			sbQuery = new StringBuilder();
			sbQuery.append("select ad.*,adf.appraisal_freq_id,wfd.user_type_id as user_type from appraisal_details ad, work_flow_details wfd ,appraisal_details_frequency adf "+
							" where ad.appraisal_details_id = wfd.effective_id and wfd.effective_id = adf.appraisal_id and ad.appraisal_details_id = adf.appraisal_id "+
							" and ad.my_review_status = 1 ");
			if(strUserType != null && !strUserType.equals(ADMIN)) {
				sbQuery.append(" and wfd.emp_id = "+uF.parseToInt(strSessionEmpId)+" ");
				if (strBaseUserType != null && strBaseUserType.equalsIgnoreCase(getCurrUserType())) {
					sbQuery.append(" and wfd.user_type_id = "+uF.parseToInt(strBaseUserTypeId)+" ");
				} else {
					sbQuery.append(" and wfd.user_type_id = "+uF.parseToInt(strUserTypeId)+" ");
				}
			}

			sbQuery.append(" and wfd.effective_type = '"+WORK_FLOW_SELF_REVIEW+"' and wfd.is_approved = 0 and ad.is_publish = false and ad.is_close = false and (adf.is_delete is null or is_delete = false)");
			if(getStrSearchJob()!=null && !getStrSearchJob().trim().equals("") && !getStrSearchJob().trim().equalsIgnoreCase("null")) {
				sbQuery.append(" and upper(appraisal_name) like '%"+getStrSearchJob().trim().toUpperCase()+"%'");
			}
			sbQuery.append(" order by appraisal_id desc  ");
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("SRR pst5===> "+ pst);
			rs = pst.executeQuery();
			List<String> alList = new ArrayList<String>();
			while (rs.next()) {
				
				List<String> checkEmpList = hmCheckEmp.get(rs.getString("appraisal_details_id"));
				if(checkEmpList==null) checkEmpList = new ArrayList<String>();
				
				List<String> checkEmpUserTypeList = hmCheckEmpUserType.get(rs.getString("appraisal_details_id")+"_"+strSessionEmpId);
				if(checkEmpUserTypeList==null) checkEmpUserTypeList = new ArrayList<String>();
												
				if(!checkEmpList.contains(strSessionEmpId) && !strUserType.equalsIgnoreCase(ADMIN)) {
					continue;
				}
			
				String userType = rs.getString("user_type");		
				
				if(!checkEmpList.contains(strSessionEmpId) && strUserType!=null && strUserType.equalsIgnoreCase(ADMIN) && alList.contains(rs.getString("appraisal_details_id"))) {
//					System.out.println("in if");
					continue;
				} else if(!checkEmpList.contains(strSessionEmpId) && strUserType!=null && strUserType.equalsIgnoreCase(ADMIN) && !alList.contains(rs.getString("appraisal_details_id"))) {
					userType = "";
//					System.out.println("in else if1");
					alList.add(rs.getString("appraisal_details_id"));
				} else if(!checkEmpUserTypeList.contains(userType)) {
//					System.out.println("in else if2");
					continue;	
				}
			
				if(j == 0) {
					if(uF.parseToInt(getStrReviewId()) == 0) {
						setStrReviewId(rs.getString("appraisal_details_id"));
						setAppFreqId(rs.getString("appraisal_freq_id"));
					}
				}
				
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("appraisal_details_id"));//0
				innerList.add(rs.getString("appraisal_freq_id"));//1
				innerList.add(rs.getString("appraisal_name")+"(One time)");//2
				innerList.add(rs.getString("appraisal_type"));//3
				innerList.add(hmOrientationName.get(rs.getString("oriented_type")));//4
				innerList.add(rs.getString("user_type"));//3
				hmSelfReviewNames.put(rs.getString("appraisal_details_id")+"_"+rs.getString("user_type"),innerList);
				j++;
				
			}
			rs.close();
			pst.close();
//			System.out.println("alList===>> " + alList);
//			System.out.println("j==>"+j+"==>java hmSelfReviewNames ===>> " + hmSelfReviewNames.size());
			
			request.setAttribute("hmSelfReviewNames", hmSelfReviewNames);
				
		} catch (Exception e) {
			e.printStackTrace(); 
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	private void getReviewNamesList(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		Map<String,List<String>> hmReviewNames = new LinkedHashMap<String,List<String>>(); 
	    try {
	    	con = db.makeConnection(con);
	    	Map<String,String> orientationMp = CF.getOrientationValue(con);
	    	Map<String, String> hmFrequency = new LinkedHashMap<String, String>();
	    		
			pst = con.prepareStatement("select * from appraisal_frequency");
			rst = pst.executeQuery();
			while (rst.next()) {
				hmFrequency.put(rst.getString("appraisal_frequency_id"), rst.getString("frequency_name"));
			}
			rst.close();
			pst.close();
			
		
			StringBuilder sbEmpId = null;
			if(strUserType != null && (strUserType.equals(HRMANAGER) || strUserType.equals(RECRUITER) || strUserType.equals(CEO)) && session.getAttribute(WLOCATION_ACCESS) != null && !((String)session.getAttribute(WLOCATION_ACCESS)).equals("") && session.getAttribute(ORG_ACCESS) != null && !((String)session.getAttribute(ORG_ACCESS)).equals("")) {
				pst = con.prepareStatement("select eod.emp_id from employee_official_details eod,employee_personal_details epd where " +
					" epd.emp_per_id=eod.emp_id and is_alive = true and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+") and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+") ");
				rst = pst.executeQuery();
				while (rst.next()) {
					if(sbEmpId == null) {
						sbEmpId = new StringBuilder();
						sbEmpId.append(rst.getString("emp_id"));
					} else {
						sbEmpId.append(","+rst.getString("emp_id"));
					}
				}
				rst.close();
				pst.close();
			}
			if(strUserType != null && strUserType.equals(MANAGER) && getCurrUserType() != null && !getCurrUserType().equals(strBaseUserType)) {
				pst = con.prepareStatement("select reviewee_id from appraisal_reviewee_details where supervisor_ids like '%,"+strSessionEmpId+",%' ");
				rst = pst.executeQuery();
				while (rst.next()) {
					if(sbEmpId == null) {
						sbEmpId = new StringBuilder();
						sbEmpId.append(rst.getString("reviewee_id"));
					} else {
						sbEmpId.append(","+rst.getString("reviewee_id"));
					}
				}
				rst.close();
				pst.close();
			}
			
			String[] str_EmpIds = null;
			if(sbEmpId != null) {
				str_EmpIds = sbEmpId.toString().split(",");
			}
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select count(*) as cnt from appraisal_details a,appraisal_details_frequency adf  where a.appraisal_details_id = adf.appraisal_id "
				+" and (adf.is_delete is null or adf.is_delete=false) and appraisal_details_id>0 "); //my_review_status = 0
			if(strUserType != null && strUserType.equals(MANAGER) && getCurrUserType() != null && !getCurrUserType().equals(strBaseUserType)) {
				sbQuery.append(" and (a.added_by = "+uF.parseToInt(strSessionEmpId)+" or a.appraisal_details_id in (select appraisal_id from appraisal_reviewee_details where supervisor_ids like '%,"+strSessionEmpId+",%')) " +
				//	" and (is_publish = true or publish_expire_status = 1))) ");
				" and (is_appraisal_publish = true or freq_publish_expire_status = 1) ");
			} else if(str_EmpIds!=null && str_EmpIds.length>0) {
                sbQuery.append(" and (");
                for(int i=0; i<str_EmpIds.length; i++) {
                    sbQuery.append(" self_ids like '%,"+str_EmpIds[i]+",%'");
                    if(i<str_EmpIds.length-1) {
                        sbQuery.append(" OR ");
                    }
                }
                sbQuery.append(") ");
            }
			
			if(getDataType() != null && getDataType().equals("L")) {
				sbQuery.append(" and is_appraisal_close = false ");
			} else if(getDataType() != null && getDataType().equals("C")) {
				sbQuery.append(" and is_appraisal_close = true ");
			}
			
			if(getStrSearchJob()!=null && !getStrSearchJob().trim().equals("") && !getStrSearchJob().trim().equalsIgnoreCase("null")) {
				sbQuery.append(" and upper(appraisal_name) like '%"+getStrSearchJob().trim().toUpperCase()+"%'");
			}
			pst=con.prepareStatement(sbQuery.toString());
//			System.out.println("pst reviews1=====>"+pst);
			rst=pst.executeQuery();
			int proCount = 0;
			int proCnt = 0;
			while(rst.next()) {
				proCnt = rst.getInt("cnt");
				proCount = rst.getInt("cnt")/15;
				if(rst.getInt("cnt")%15 != 0) {
					proCount++;
				}
			}
			rst.close();
			pst.close();
			request.setAttribute("proCount", proCount+"");
			request.setAttribute("proCnt", proCnt+"");
			
			Map<String, String> hmOrientationName = CF.getOrientationValue(con);
			int j =0;
			sbQuery = new StringBuilder();
			sbQuery.append("select * from appraisal_details a,appraisal_details_frequency adf  where a.appraisal_details_id = adf.appraisal_id "
				+" and (adf.is_delete is null or adf.is_delete = false )  and appraisal_details_id > 0 "); //my_review_status = 0 
			if(strUserType != null && strUserType.equals(MANAGER) && getCurrUserType() != null && !getCurrUserType().equals(strBaseUserType)) {
				sbQuery.append(" and (a.added_by = "+uF.parseToInt(strSessionEmpId)+" or a.appraisal_details_id in (select appraisal_id from appraisal_reviewee_details where supervisor_ids like '%,"+strSessionEmpId+",%')) " +
					" and (is_appraisal_publish = true or freq_publish_expire_status = 1) ");
			} else if(str_EmpIds!=null && str_EmpIds.length>0) {
                sbQuery.append(" and (");
                for(int i=0; i<str_EmpIds.length; i++) {
                    sbQuery.append(" self_ids like '%,"+str_EmpIds[i]+",%'");
                    if(i<str_EmpIds.length-1) {
                        sbQuery.append(" OR ");
                    }
                }
                sbQuery.append(") ");
            }
			if(getDataType() != null && getDataType().equals("L")) {
				sbQuery.append(" and is_appraisal_close = false ");
			} else if(getDataType() != null && getDataType().equals("C")) {
				sbQuery.append(" and is_appraisal_close = true ");
			}
		    if(getStrSearchJob()!=null && !getStrSearchJob().trim().equals("") && !getStrSearchJob().trim().equalsIgnoreCase("null")) {
				sbQuery.append(" and upper(appraisal_name) like '%"+getStrSearchJob().trim().toUpperCase()+"%'");
			}	
			sbQuery.append(" order by is_appraisal_close, is_appraisal_publish desc,freq_publish_expire_status desc,freq_end_date desc");
			int intOffset = uF.parseToInt(getMinLimit());
			sbQuery.append(" limit 15 offset "+intOffset+"");
			pst=con.prepareStatement(sbQuery.toString());
//			System.out.println("pst reviews ====>>> " + pst);
			rst=pst.executeQuery();
			StringBuilder sbIcons = new StringBuilder();
			while(rst.next()) {
				if(j == 0) {
					if(uF.parseToInt(getStrReviewId()) == 0) {
						setStrReviewId(rst.getString("appraisal_details_id"));
						setAppFreqId(rst.getString("appraisal_freq_id"));
					}
				}
				List<String> appraisal_info =new ArrayList<String>(); 
				appraisal_info.add(rst.getString("appraisal_details_id"));//0
				appraisal_info.add(rst.getString("appraisal_freq_id"));//1
				appraisal_info.add(rst.getString("appraisal_name")+" ("+rst.getString("appraisal_freq_name")+")");//2
				appraisal_info.add(rst.getString("appraisal_type"));//3
				appraisal_info.add(hmOrientationName.get(rst.getString("oriented_type")));//4
				hmReviewNames.put(rst.getString("appraisal_details_id")+"_"+rst.getString("appraisal_freq_id"), appraisal_info);
				j++;
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
		
	
		request.setAttribute("appFreqId",getAppFreqId());
		request.setAttribute("strReviewId",getStrReviewId());
		request.setAttribute("hmReviewNames",hmReviewNames);
		
	}
	
	private HttpServletRequest request;

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

	public String getCurrUserType() {
		return currUserType;
	}

	public void setCurrUserType(String currUserType) {
		this.currUserType = currUserType;
	}

	public String getStrReviewId() {
		return strReviewId;
	}

	public void setStrReviewId(String strReviewId) {
		this.strReviewId = strReviewId;
	}

	public String getAppFreqId() {
		return appFreqId;
	}

	public void setAppFreqId(String appFreqId) {
		this.appFreqId = appFreqId;
	}

	public String getsProPage() {
		return sProPage;
	}

	public void setsProPage(String sProPage) {
		this.sProPage = sProPage;
	}

	public String getsMinLimit() {
		return sMinLimit;
	}

	public void setsMinLimit(String sMinLimit) {
		this.sMinLimit = sMinLimit;
	}

	public String getStrSearchJob() {
		return strSearchJob;
	}

	public void setStrSearchJob(String strSearchJob) {
		this.strSearchJob = strSearchJob;
	}

	public String getCallFrom() {
		return callFrom;
	}

	public void setCallFrom(String callFrom) {
		this.callFrom = callFrom;
	}

	public String getAlertID() {
		return alertID;
	}

	public void setAlertID(String alertID) {
		this.alertID = alertID;
	}
	
}
