package com.konnect.jpms.performance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
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

public class SelfReviewRequests extends ActionSupport implements ServletRequestAware, IStatements {
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	String strUserTypeId = null;
	String strBaseUserType = null;
	String strBaseUserTypeId = null;
	boolean isEmpUserType = false;
	CommonFunctions CF = null;

	String strSessionEmpId = null;
		
	private String dataType;
	private String currUserType;
	private String appId;
	private String appFreqId;
	
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
		
		getSelfReviewRequest(uF);
		return LOAD;

	}
	
	private void getSelfReviewRequest(UtilityFunctions uF) {
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			con = db.makeConnection(con);

			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			Map<String, String> hmUserTypeIdMap = CF.getUserTypeIdMap(con);
			if(hmUserTypeIdMap == null) hmUserTypeIdMap = new HashMap<String, String>();
			
			StringBuilder sbQuery=new StringBuilder();
			sbQuery.append("select effective_id, min(member_position) as member_position from work_flow_details wf where is_approved=0 " +
					"and effective_type='"+WORK_FLOW_SELF_REVIEW+"' and effective_id = ? group by effective_id");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(getAppId()));
//			System.out.println("hmNextApproval pst==>"+pst);
			rs = pst.executeQuery();
			Map<String, String> hmNextApproval = new HashMap<String, String>();
			while(rs.next()) {
				hmNextApproval.put(rs.getString("effective_id"), rs.getString("member_position"));
			}
			rs.close();
			pst.close();

//			System.out.println("hmNextApproval==>"+hmNextApproval);
			sbQuery=new StringBuilder();
			sbQuery.append("select effective_id,user_type_id,min(member_position)as member_position from work_flow_details wfd where emp_id=? " +
					" and is_approved=0 and effective_type='"+WORK_FLOW_SELF_REVIEW+"' and effective_id = ?");
			if(strUserType != null && strUserType.equals(ADMIN)) {
				sbQuery.append(" and (user_type_id=? or user_type_id=?) ");
			} else {
				sbQuery.append(" and user_type_id=? ");
			}
			sbQuery.append(" group by effective_id,user_type_id");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1,uF.parseToInt(strSessionEmpId) );
			pst.setInt(2,uF.parseToInt(getAppId()));
			if (strBaseUserType != null && strBaseUserType.equalsIgnoreCase(getCurrUserType())) {
				pst.setInt(3, uF.parseToInt(strBaseUserTypeId));
			} else {
				pst.setInt(3, uF.parseToInt(strUserTypeId));
			}
			if(strUserType != null && strUserType.equals(ADMIN)) {
				pst.setInt(4, uF.parseToInt(hmUserTypeIdMap.get(HRMANAGER)));
			}
//			System.out.println("hmMemNextApproval pst==>"+pst);
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
				"  effective_id = ?");
			if(strUserType != null && strUserType.equals(ADMIN)) {
				sbQuery.append(" and (user_type_id=? or user_type_id=?) ");
			} else {
				sbQuery.append(" and user_type_id=? ");
			}
			sbQuery.append(" order by effective_id,member_position");
//			sbQuery.append(") and user_type_id=? order by effective_id,member_position");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(getAppId()));
			if (strBaseUserType != null && strBaseUserType.equalsIgnoreCase(getCurrUserType())) {
				pst.setInt(2, uF.parseToInt(strBaseUserTypeId));
			} else {
				pst.setInt(2, uF.parseToInt(strUserTypeId));
			}
			if(strUserType != null && strUserType.equals(ADMIN)) {
				pst.setInt(3, uF.parseToInt(hmUserTypeIdMap.get(HRMANAGER)));
			}
//			System.out.println("pst1 ===>> " + pst);
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
				
				hmCheckEmp.put(rs.getString("effective_id"), checkEmpList);
				hmCheckEmpUserType.put(rs.getString("effective_id")+"_"+rs.getString("emp_id"), checkEmpUserTypeList);
			}
			rs.close();
			pst.close();
			
//			System.out.println("hmCheckEmp==>"+hmCheckEmp);
//			System.out.println("hmCheckEmpUserType==>"+hmCheckEmpUserType);
			
			Map<String, String> hmUserTypeName = CF.getUserTypeMap(con);
			sbQuery = new StringBuilder();
			sbQuery.append("select ad.*,wfd.user_type_id as user_type from appraisal_details ad, work_flow_details wfd where "
					+" ad.appraisal_details_id = wfd.effective_id and effective_id=? ");
			if(strUserType != null && !strUserType.equals(ADMIN)) {
				sbQuery.append(" and wfd.emp_id = "+uF.parseToInt(strSessionEmpId)+" ");
//				if (strBaseUserType != null && (strBaseUserType.equalsIgnoreCase(CEO) || strBaseUserType.equalsIgnoreCase(HOD))) {
				if (strBaseUserType != null && strBaseUserType.equalsIgnoreCase(getCurrUserType())) {
//					sbQuery.append(" and (wfd.user_type_id = "+uF.parseToInt(strUserTypeId)+" or wfd.user_type_id = "+uF.parseToInt(strBaseUserTypeId)+" ) ");
					sbQuery.append(" and wfd.user_type_id = "+uF.parseToInt(strBaseUserTypeId)+" ");
				} else {
					sbQuery.append(" and wfd.user_type_id = "+uF.parseToInt(strUserTypeId)+" ");
				}
			}
//			else if (strUserType != null && strUserType.equalsIgnoreCase(ADMIN)) {
//				sbQuery.append(" and (wfd.user_type_id = "+uF.parseToInt(strUserTypeId)+" or wfd.user_type_id = "+uF.parseToInt(hmUserTypeIdMap.get(HRMANAGER))+" ) ");
//			}
			sbQuery.append(" and wfd.effective_type = '"+WORK_FLOW_SELF_REVIEW+"' and wfd.is_approved = 0 and ad.is_publish = false and ad.is_close = false");
			
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(getAppId()));
//			System.out.println("pst===> "+ pst);
			rs = pst.executeQuery();
			int nCount = 0;
			StringBuilder sbSelfReviewRequest = new StringBuilder();
			StringBuilder sbApproveDeny = new StringBuilder();
			StringBuilder sbStauts = new StringBuilder();
			List<String> alList = new ArrayList<String>();
			String usetTypeName = "";
			while (rs.next()) {
				
				List<String> checkEmpList = hmCheckEmp.get(rs.getString("appraisal_details_id"));
				if(checkEmpList==null) checkEmpList = new ArrayList<String>();
				
				List<String> checkEmpUserTypeList = hmCheckEmpUserType.get(rs.getString("appraisal_details_id")+"_"+strSessionEmpId);
				if(checkEmpUserTypeList==null) checkEmpUserTypeList = new ArrayList<String>();
				
				boolean checkGHRInWorkflow = true;
				if(checkEmpUserTypeList.contains(hmUserTypeIdMap.get(HRMANAGER)) && !checkEmpUserTypeList.contains(hmUserTypeIdMap.get(ADMIN)) && strUserType != null && strUserType.equals(ADMIN)) {
					checkGHRInWorkflow = false;
				}
				
				if(!checkEmpList.contains(strSessionEmpId) && !strUserType.equalsIgnoreCase(ADMIN)) {
					continue;
				}
			/*	System.out.println("checkEmpList==>"+checkEmpList);
				System.out.println("strUserType==>"+strUserType);
				System.out.println("alList==>"+alList);*/
				String userType = rs.getString("user_type");		
//				System.out.println("userType1==>"+userType);
			/*	if(!checkEmpList.contains(strSessionEmpId) && strUserType!=null && strUserType.equalsIgnoreCase(ADMIN) && alList.contains(rs.getString("appraisal_details_id"))) {
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
				
				if(checkEmpList.contains(strSessionEmpId)) {
					usetTypeName = "["+hmUserTypeName.get(rs.getString("user_type"))+"]";
				} else {
					usetTypeName = "";
				}*/
				
						
				if((!checkEmpList.contains(strSessionEmpId) && (uF.parseToInt(strSessionEmpId) == uF.parseToInt(rs.getString("added_by"))) || (strUserType!=null && strUserType.equalsIgnoreCase(ADMIN))) && alList.contains(rs.getString("appraisal_details_id"))) {
//					System.out.println("2 RID");
					continue;
				} else if(!checkEmpList.contains(strSessionEmpId) && strUserType !=null && strUserType.equalsIgnoreCase(ADMIN) && !alList.contains(rs.getString("appraisal_details_id"))) {
// 					System.out.println("3 RID");
					userType = "";
					alList.add(rs.getString("appraisal_details_id"));
				} else if(strUserType !=null && !strUserType.equalsIgnoreCase(ADMIN) && !alList.contains(rs.getString("appraisal_details_id")) && (uF.parseToInt(strSessionEmpId) == uF.parseToInt(rs.getString("added_by")) ) ) {
//					System.out.println("4 RID");
					if((!checkEmpList.contains(strSessionEmpId) && (uF.parseToInt(strSessionEmpId) == uF.parseToInt(rs.getString("added_by"))) ) 
						|| (checkEmpUserTypeList.contains(userType) && (uF.parseToInt(strSessionEmpId) == uF.parseToInt(rs.getString("added_by"))) )) {
				
					if (strBaseUserType != null && strBaseUserType.equalsIgnoreCase(getCurrUserType())) {
						userType = strBaseUserTypeId;	
					} else {
						userType = strUserTypeId;
					}
					alList.add(rs.getString("appraisal_details_id"));
					} else {

						continue;
					}
				} else if(!checkEmpUserTypeList.contains(userType)) {
//					System.out.println("5 ");
					continue;	
				} 

				if(checkEmpList.contains(strSessionEmpId)) {
					usetTypeName = "["+hmUserTypeName.get(rs.getString("user_type"))+"]";
				}else {
					usetTypeName = "";
				}
				
				sbSelfReviewRequest.replace(0, sbSelfReviewRequest.length(), "");
				sbApproveDeny.replace(0, sbApproveDeny.length(), "");
				sbStauts.replace(0, sbStauts.length(), "");
				
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("appraisal_details_id"));//0
				innerList.add(rs.getString("appraisal_name"));//1
				innerList.add(hmEmpName.get(rs.getString("added_by")));//2
				innerList.add(uF.getDateFormat(rs.getString("entry_date"), DBDATE, DATE_FORMAT_STR) );//3
				
				innerList.add(usetTypeName);
				if((strUserType != null && strUserType.equals(ADMIN) && (usetTypeName == null || usetTypeName.equals(""))) || (uF.parseToInt(hmNextApproval.get(rs.getString("appraisal_details_id")))==uF.parseToInt(hmMemNextApproval.get(rs.getString("appraisal_details_id")+"_"+userType)) && uF.parseToInt(hmNextApproval.get(rs.getString("appraisal_details_id")))>0)) {
//					System.out.println("===>> 1");
					sbStauts.append("<span style=\"padding-right: 5px;\" id=\"myDivStatus" + nCount + "\" > ");
					 /*sbStauts.append("<img src=\"images1/icons/pending.png\" title=\"Waiting for approval\" /> </a> ");*/
					sbStauts.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#b71cc5\" title=\"Waiting for approval\"></i> ");
					
					sbStauts.append("</span>");
					
					sbApproveDeny.append("<span id=\"myDivM" + nCount + "\" > ");
					sbApproveDeny.append("<a href=\"javascript:void(0)\" onclick=\"approveDenyRequest('"+ rs.getString("appraisal_details_id")+"','1','"+ nCount +"', '"+userType+"');\" >" +
							"<i class=\"fa fa-check-circle checknew\" aria-hidden=\"true\"  title=\"Approve to Publish Self Review\"></i></a> ");
					sbApproveDeny.append("<a href=\"javascript:void(0)\" onclick=\"approveDenyRequest('"+ rs.getString("appraisal_details_id")+"','-1','"+ nCount +"', '"+userType+"');\">" + "<i class=\"fa fa-times-circle cross\" aria-hidden=\"true\"  title=\"Denial of Publish Self Review\"></i></a>  ");
					sbApproveDeny.append("</span>");
				} else if(uF.parseToInt(hmNextApproval.get(rs.getString("appraisal_details_id")))<uF.parseToInt(hmMemNextApproval.get(rs.getString("appraisal_details_id")+"_"+userType))
							|| (uF.parseToInt(hmNextApproval.get(rs.getString("appraisal_details_id")))==0 && uF.parseToInt(hmNextApproval.get(rs.getString("appraisal_details_id")))==uF.parseToInt(hmMemNextApproval.get(rs.getString("appraisal_details_id")+"_"+userType)))) {
					
					if(strUserType!= null && strUserType.equalsIgnoreCase(ADMIN) && uF.parseToInt(strUserTypeId) == uF.parseToInt(userType)) { //!checkEmpList.contains(strSessionEmpId) && 
//						System.out.println("1===>> in ADMIN");
						sbStauts.append("<span style=\"padding-right: 5px;\" id=\"myDivStatus" + nCount + "\" > ");
						/*sbStauts.append("<img src=\"images1/icons/pending.png\" title=\"Waiting for approval\" /> </a> ");*/
						sbStauts.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" title=\"Waiting for approval\" style=\"color:#b71cc5\"></i> ");
						
						sbStauts.append("</span>");
						
						sbApproveDeny.append("<span id=\"myDivM" + nCount + "\" > ");
						sbApproveDeny.append("<a href=\"javascript:void(0)\" onclick=\"approveDenyRequest('"+ rs.getString("appraisal_details_id")+"','1','"+ nCount +"', '"+userType+"');\" >" +
								"<i class=\"fa fa-check-circle checknew\" aria-hidden=\"true\"  title=\"Approve to Publish Self Review\"></i></a> ");
						sbApproveDeny.append("<a href=\"javascript:void(0)\" onclick=\"approveDenyRequest('"+ rs.getString("appraisal_details_id")+"','-1','"+ nCount +"', '"+userType+"');\">" + " <i class=\"fa fa-times-circle cross\" aria-hidden=\"true\"  title=\"Denial of Self Review publish request\"></i></a>  ");
						sbApproveDeny.append("</span>");
					} else {
//						System.out.println("2===>> in ADMIN");
						sbStauts.append("<span style=\"padding-right: 5px;\" id=\"myDivStatus" + nCount + "\" > ");
						/*sbStauts.append("<img src=\"images1/icons/pullout.png\" title=\"Waiting for workflow\" />"); */
						sbStauts.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" title=\"Waiting for workflow\" style=\"color:#ea9900\"></i>"); 
						
						if(!checkGHRInWorkflow) {
							sbApproveDeny.append("<a href=\"javascript:void(0)\" onclick=\"approveDenyRequest('"+ rs.getString("appraisal_details_id")+"','1','"+ nCount +"', '');\" >" + "<i class=\"fa fa-check-circle checknew\" aria-hidden=\"true\"  title=\"Approve to Publish Self Review ("+ADMIN+")\"></i></a> ");
							sbApproveDeny.append("<a href=\"javascript:void(0)\" onclick=\"approveDenyRequest('"+ rs.getString("appraisal_details_id")+"','-1','"+ nCount +"', '');\">" + " <i class=\"fa fa-times-circle cross\" aria-hidden=\"true\"  title=\"Denial of Self Review publish request ("+ADMIN+")\"></i></a>  ");
						}
						sbStauts.append("</span>");
						
//							********************** Workflow *******************************
						sbApproveDeny.append("<span id=\"myDivM" + nCount + "\" > ");
						sbApproveDeny.append("<a href=\"javascript:void(0)\" onclick=\"getApprovalStatus('"+rs.getString("appraisal_details_id")+"', '"+innerList.get(2)+"', '"+getAppFreqId()+"');\" style=\"margin-left: 10px;\">Work flow</a>");
						sbApproveDeny.append("</span>");
//							********************** Workflow *******************************
					}
				} else {
									 
//						System.out.println("3===>> in ADMIN");
						sbStauts.append("<span style=\"padding-right: 5px;\" id=\"myDivStatus" + nCount + "\" > ");
						 /*sbStauts.append("<img src=\"images1/icons/pending.png\" title=\"Waiting for approval\" /> </a> ");*/
						sbStauts.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#b71cc5\" title=\"Waiting for approval\" ></i>");
						
						sbStauts.append("</span>");
						
						sbApproveDeny.append("<span id=\"myDivM" + nCount + "\" > ");
						sbApproveDeny.append("<a href=\"javascript:void(0)\" onclick=\"approveDenyRequest('"+ rs.getString("appraisal_details_id")+"','1','"+ nCount +"', '"+userType+"');\" >" +
								"<i class=\"fa fa-check-circle checknew\" aria-hidden=\"true\" title=\"Approve to Publish Self Review\" ></i></a> ");
						sbApproveDeny.append("<a href=\"javascript:void(0)\" onclick=\"approveDenyRequest('"+ rs.getString("appraisal_details_id")+"','-1','"+ nCount +"', '"+userType+"');\">" + " <i class=\"fa fa-times-circle cross\" aria-hidden=\"true\"  title=\"Denial of Self Review publish request\"></i></a>  ");
						/*sbApproveDeny.append("<a target=\"_new\" href=\"MyReviewSummary.action?id="+rs.getString("appraisal_details_id")+"&appFreqId="+getAppFreqId()+"\" >"
								+ " <img src=\"images1/edit.png\" title=\"Edit Request\" /> </a> ");*/
						sbApproveDeny.append("</span>");
					
				}
				
				//sbSelfReviewRequest.append(""+sbStauts + "<a target=\"_new\" href=\"MyReviewSummary.action?id="+innerList.get(0)+"&appFreqId="+getAppFreqId()+"\" >"+innerList.get(1)+"</a> (Self Review) requested by "+innerList.get(2)+" on "+innerList.get(3)+". "+innerList.get(4)+"&nbsp;"+sbApproveDeny.toString());
				sbSelfReviewRequest.append("<div class='margintop10'>"+sbStauts +"<span style=\"color:green;font-size:16px;\">"+innerList.get(1)+" </span>(Self Review) requested by "+innerList.get(2)+" on "+innerList.get(3)+". "+innerList.get(4)+"&nbsp;"+sbApproveDeny.toString()+"</div>");
				
				nCount++;
			}
			rs.close();
			pst.close();
//			System.out.println("selfReviewRequestList ===>> " + selfReviewRequestList);
			
			request.setAttribute("sbSelfReviewRequest", sbSelfReviewRequest.toString());
				
		} catch (Exception e) {
			e.printStackTrace(); 
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getCurrUserType() {
		return currUserType;
	}

	public void setCurrUserType(String currUserType) {
		this.currUserType = currUserType;
	}

	public String getAppFreqId() {
		return appFreqId;
	}

	public void setAppFreqId(String appFreqId) {
		this.appFreqId = appFreqId;
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
