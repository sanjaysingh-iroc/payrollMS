package com.konnect.jpms.performance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
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

public class MyReviewStatus implements ServletRequestAware, IStatements {

	HttpSession session;
	String strSessionEmpId;
	String strSessionUserType;
	String strUserTypeId;
	CommonFunctions CF; 
	
	private String id;
	private String appFreqId;
	
	public String execute() {
		
		session = request.getSession(); 
		strSessionEmpId = (String) session.getAttribute(EMPID);
		strSessionUserType = (String) session.getAttribute(USERTYPE);
		strUserTypeId = (String) session.getAttribute(USERTYPEID);

		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return "login";
		UtilityFunctions uF = new UtilityFunctions();
		getOrientationMember();
		request.setAttribute(PAGE, "/jsp/performance/MyReviewStatus.jsp");
		request.setAttribute(TITLE, "My Review Status");

		StringBuilder sbpageTitleNaviTrail = new StringBuilder();
		sbpageTitleNaviTrail.append("<li><i class=\"fa fa-th\"></i><a href=\"MyHR.action\" style=\"color: #3c8dbc;\">My HR</a></li>" +
				"<li class=\"active\">My Review Status</li>");
		request.setAttribute("PAGETITLE_NAVITRAIL", sbpageTitleNaviTrail.toString());
		
		request.setAttribute("DOC_RETRIVE_LOCATION", CF.getStrDocRetriveLocation());
		
		getAppraisalStatus(uF);
		getRemarks();
		getEmpSupervisor();
		getOrientationCount(uF);
		//getSingleOpenWithoutMarksQueCount(uF);
		getSingleOpenWithoutMarksQueReadUnreadCount(uF);
		return "success";
	}
	
	
	private void getSingleOpenWithoutMarksQueReadUnreadCount(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;

		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			Map<String, String> hmReadUnreadCount = new HashMap<String, String>();
			pst = con.prepareStatement("Select count(*) as count,aqa.user_type_id,aqa.emp_id,aqa.read_status from appraisal_question_answer aqa," +
					"appraisal_question_details aqd where aqa.appraisal_id = ? and aqa.appraisal_freq_id = ? and aqa.appraisal_id = aqd.appraisal_id and " +
					"aqa.question_id = aqd.question_id and aqd.answer_type = 12 group by aqa.read_status,aqa.user_type_id,aqa.emp_id");
			pst.setInt(1, uF.parseToInt(id));
			pst.setInt(2, uF.parseToInt(getAppFreqId()));
			rs = pst.executeQuery();
//			System.out.println("pst ============ > "+ pst);
			while (rs.next()) {
				hmReadUnreadCount.put(rs.getString("user_type_id")+"_"+rs.getString("emp_id")+"_"+rs.getString("read_status"), rs.getString("count"));
			}	
			rs.close();
			pst.close();
			
			request.setAttribute("hmReadUnreadCount", hmReadUnreadCount);
//			System.out.println("hmReadUnreadCount =====> "+ hmReadUnreadCount);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst); 
			db.closeConnection(con);
		}
	}

	
	private void getOrientationCount(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			Map<String, String> orientationMemberMp = getOrientationMember();
			con = db.makeConnection(con);
			Map<String, String> hmEmpWlocationMap=CF.getEmpWlocationMap(con);
			Map<String,String> hmEmpName=CF.getEmpNameMap(con, null,null);
				
			Map<String,String> hmMemberMP=new HashMap<String, String>();			
			
			
			pst = con.prepareStatement("select * from appraisal_details where appraisal_details_id =?");
			pst.setInt(1, uF.parseToInt(id));
			rs = pst.executeQuery();
			Map<String, String> hmOrientationCount = new HashMap<String, String>();
			String mem="";
			String self_ids="";
			String oriented_type="";
			while (rs.next()) {
				mem=rs.getString("usertype_member");	
				self_ids=rs.getString("self_ids");
				oriented_type=rs.getString("oriented_type");
			}
			rs.close();
			pst.close();
			
			List<String> memberList1= CF.getOrientationMemberDetails(con,uF.parseToInt(oriented_type));
			Map<String, String> hmOrientMemberID = CF.getOrientMemberID(con);
			if(self_ids!=null && !self_ids.equals("")){
				
				pst=con.prepareStatement("select emp_image,emp_per_id from employee_personal_details ");
				rs=pst.executeQuery();
				Map<String,String> empImageMap=new HashMap<String,String>();
				while(rs.next()){
					empImageMap.put(rs.getString("emp_per_id"),rs.getString("emp_image"));
				}
				rs.close();
				pst.close();
				
				List<String> memberList = Arrays.asList(mem.split(","));
				for(int i = 0; i < memberList.size(); i++) {
					hmOrientationCount.put(memberList.get(i),orientationMemberMp.get(memberList.get(i)));
				}
				
				getEmpWlocation(self_ids);
				self_ids=self_ids.substring(1,self_ids.length()-1);
				List<String> selfList = Arrays.asList(self_ids.split(","));
				
				for(int j=0;selfList!=null && !selfList.isEmpty() && j<selfList.size();j++){
					pst=con.prepareStatement("select wlocation_id,grade_id from employee_official_details where emp_id=?");
					pst.setInt(1, uF.parseToInt(selfList.get(j).trim()));
					rs=pst.executeQuery();
					String empLocation="";
					String gradeid="";
					while(rs.next()){
						empLocation=rs.getString("wlocation_id");
						gradeid=rs.getString("grade_id");
					}
					rs.close();
					pst.close();
					
					pst=con.prepareStatement("select emp_id,user_id,user_type_id from appraisal_question_answer where appraisal_id=? and appraisal_freq_id = ? and emp_id=? " +
							" and appraisal_question_answer_id in(select max(appraisal_question_answer_id) from appraisal_question_answer " +
							" where appraisal_id=? and emp_id=? and appraisal_freq_id = ? group by user_type_id)");
					pst.setInt(1, uF.parseToInt(id));
					pst.setInt(2, uF.parseToInt(getAppFreqId()));
					pst.setInt(3, uF.parseToInt(selfList.get(j).trim()));
					pst.setInt(4, uF.parseToInt(id));
					pst.setInt(5, uF.parseToInt(selfList.get(j).trim()));
					pst.setInt(6, uF.parseToInt(getAppFreqId()));
					rs=pst.executeQuery();
					Map<String,String> hmCheckAppraisal=new HashMap<String, String>();
					Map<String,String> hmCheckHR=new HashMap<String, String>();
					Map<String,String> hmCheckMgr=new HashMap<String, String>();
					Map<String,String> hmCheckCeo=new HashMap<String, String>();
					Map<String,String> hmCheckHod=new HashMap<String, String>();
					while(rs.next()){
						String key=rs.getString("emp_id")+"_"+rs.getString("user_id")+"_"+rs.getString("user_type_id");
						hmCheckAppraisal.put(key, rs.getString("emp_id"));
						if(rs.getString("user_type_id").equals("7")){
							hmCheckHR.put(HRMANAGER, rs.getString("user_id"));	
						} else if(rs.getString("user_type_id").equals("2")) {						
							hmCheckMgr.put(MANAGER, rs.getString("user_id"));
						} else if(rs.getString("user_type_id").equals("5")) {						
							hmCheckCeo.put(CEO, rs.getString("user_id"));
						} else if(rs.getString("user_type_id").equals("13")) {						
							hmCheckHod.put(HOD, rs.getString("user_id"));
						}
					}
					rs.close();
					pst.close();
					
					StringBuilder sbMemList=new StringBuilder();
					int cnt=0;
					
					//self
					if(hmOrientMemberID.get("Self") != null && memberList.contains(hmOrientMemberID.get("Self"))) {
						if(hmCheckAppraisal!=null && hmCheckAppraisal.containsKey(selfList.get(j).trim()+"_"+selfList.get(j).trim()+"_3")){
							sbMemList.append("<span style=\"margin-right: 4px;\"><img class=\"lazy img-circle\" src=\"userImages/avatar_photo.png\" style=\"border-bottom: 2px solid green\" height=\"16px\" width=\"16px;\" title=\"Anonymous User (Role-SELF)\"/></span>");
							
//							sbMemList.append("<span style=\"margin-right: 4px;\"><img class=\"lazy img-circle\" src=\"userImages/avatar_photo.png\"  style=\"border-bottom:2px solid green\"  data-original=\""+CF.getStrDocRetriveLocation()+I_PEOPLE+"/"+I_IMAGE+"/"+selfList.get(j)+"/"+I_16x16+"/"+empImageMap.get(selfList.get(j))+"\" border=\"0\" height=\"16px\" " +
//								"width=\"16px;\" title=\""+hmEmpName.get(selfList.get(j))+"(Role-SELF)\"/></span>");
						} else {
							
							sbMemList.append("<span style=\"margin-right: 4px;\"><img class=\"lazy img-circle\" src=\"userImages/avatar_photo.png\" style=\"border-bottom:2px solid red\" height=\"16px\" width=\"16px;\" title=\"Anonymous User (Role-SELF)\"/></span>");
							
//							sbMemList.append("<a href=\"javascript:void(0)\" style=\"margin-right: 4px;\" onclick=\"getEmpProfile('"+selfList.get(j)+"')\" >" +
//								"<img class=\"lazy img-circle\" src=\"userImages/avatar_photo.png\"  style=\"border-bottom:2px solid red\"  data-original=\"userImages/"+uF.showData(empImageMap.get(selfList.get(j)), "avatar_photo.png")+"\" border=\"0\" height=\"16px\" " +
//								"width=\"16px;\" title=\""+hmEmpName.get(selfList.get(j))+"(Role-SELF)\"/></a>");
						}
					}
					
					// HRManager	
					if(hmOrientMemberID.get("HR") != null && memberList.contains(hmOrientMemberID.get("HR"))) {
						if(hmCheckHR.get(HRMANAGER)!=null) {
							cnt++;
							sbMemList.append("<span style=\"margin-right: 4px;\"><img class=\"lazy img-circle\" src=\"userImages/avatar_photo.png\" style=\"border-bottom:2px solid green\" data-original=\""+CF.getStrDocRetriveLocation()+I_PEOPLE+"/"+I_IMAGE+"/"+hmCheckHR.get(HRMANAGER)+"/"+I_16x16+"/"+empImageMap.get(hmCheckHR.get(HRMANAGER))+"\" border=\"0\" height=\"16px\" " +
									"width=\"16px;\" title=\""+hmEmpName.get(hmCheckHR.get(HRMANAGER))+"(Role-HR)\"/></span>");
						}else{
										
							pst = con.prepareStatement("select eod.emp_id from employee_official_details eod,user_details ud " +
									" join employee_personal_details on emp_per_id=emp_id and is_alive=true " +
									" where eod.wlocation_id=? and eod.emp_id=ud.emp_id and ud.usertype_id=?");
							pst.setInt(1,uF.parseToInt(hmEmpWlocationMap.get(selfList.get(j))));
							pst.setInt(2, 7); // 7 = HRManager
							rs = pst.executeQuery();
							List<String> hrList=new ArrayList<String>();
							while (rs.next()) {
								
								hrList.add(rs.getString("emp_id"));
								cnt++;
								if(cnt>7){
									sbMemList.append("<a href=\"javascript:void(0);\" style=\"margin-right: 4px;\" onclick=\"seeEmpList('"+selfList.get(j)+"','"+id+"');\" class=\"OR testa\">more..</a>");
									break;
								}
								
								if(hmCheckAppraisal!=null && hmCheckAppraisal.containsKey(selfList.get(j).trim()+"_"+rs.getString("emp_id")+"_7")){
									sbMemList.append("<span style=\"margin-right: 4px;\"><img class=\"lazy img-circle\" src=\"userImages/avatar_photo.png\" style=\"border-bottom:2px solid green\" data-original=\""+CF.getStrDocRetriveLocation()+I_PEOPLE+"/"+I_IMAGE+"/"+rs.getString("emp_id")+"/"+I_16x16+"/"+empImageMap.get(rs.getString("emp_id"))+"\" border=\"0\" height=\"16px\" " +
												"width=\"16px;\" title=\""+hmEmpName.get(rs.getString("emp_id"))+"(Role-HR)\"/></span>");
								}else{ 
									sbMemList.append("<a href=\"javascript:void(0)\" style=\"margin-right: 4px;\" onclick=\"getEmpProfile('"+rs.getString("emp_id")+"')\" >" +
											"<img class=\"lazy img-circle\" src=\"userImages/avatar_photo.png\" style=\"border-bottom:2px solid red\" data-original=\""+CF.getStrDocRetriveLocation()+I_PEOPLE+"/"+I_IMAGE+"/"+rs.getString("emp_id")+"/"+I_16x16+"/"+empImageMap.get(rs.getString("emp_id"))+"\" border=\"0\" height=\"16px\" " +
													"width=\"16px;\" title=\""+hmEmpName.get(rs.getString("emp_id"))+"(Role-HR)\"/></a>");
								}
							}
							rs.close();
							pst.close();
						}
				   }
					//manager
					if(hmOrientMemberID.get("Manager") != null && memberList.contains(hmOrientMemberID.get("Manager"))) {
						if(hmCheckMgr.get(MANAGER)!=null){
							cnt++;
							sbMemList.append("<span style=\"margin-right: 4px;\"><img class=\"lazy img-circle\" src=\"userImages/avatar_photo.png\"  style=\"border-bottom:2px solid green\"  data-original=\""+CF.getStrDocRetriveLocation()+I_PEOPLE+"/"+I_IMAGE+"/"+hmCheckMgr.get(MANAGER)+"/"+I_16x16+"/"+empImageMap.get(hmCheckMgr.get(MANAGER))+"\" border=\"0\" height=\"16px\" " +
									"width=\"16px;\" title=\""+hmEmpName.get(hmCheckMgr.get(MANAGER))+"(Role-Manager)\"/></span>");
						}else{
							pst = con.prepareStatement("select supervisor_emp_id from employee_official_details eod where eod.emp_id=? and supervisor_emp_id>0");
							pst.setInt(1,uF.parseToInt(selfList.get(j)));
							rs = pst.executeQuery();
							while (rs.next()) {
								cnt++;
								if(cnt>7){
									sbMemList.append("<a href=\"javascript:void(0);\" style=\"margin-right: 4px;\" onclick=\"seeEmpList('"+selfList.get(j)+"','"+id+"');\" class=\"OR testa\">more..</a>");
									break;
								}
								if(hmCheckAppraisal!=null && hmCheckAppraisal.containsKey(selfList.get(j).trim()+"_"+rs.getString("supervisor_emp_id")+"_2")){
									sbMemList.append("<span style=\"margin-right: 4px;\"><img class=\"lazy img-circle\" src=\"userImages/avatar_photo.png\"  style=\"border-bottom:2px solid green\"  data-original=\""+CF.getStrDocRetriveLocation()+I_PEOPLE+"/"+I_IMAGE+"/"+rs.getString("supervisor_emp_id")+"/"+I_16x16+"/"+empImageMap.get(rs.getString("supervisor_emp_id"))+"\" border=\"0\" height=\"16px\" " +
													"width=\"16px;\" title=\""+hmEmpName.get(rs.getString("supervisor_emp_id"))+"(Role-Manager)\"/></span>");
								}else{
									sbMemList.append("<a href=\"javascript:void(0)\" style=\"margin-right: 4px;\" onclick=\"getEmpProfile('"+rs.getString("supervisor_emp_id")+"')\" >" +
											"<img class=\"lazy img-circle\" src=\"userImages/avatar_photo.png\"  style=\"border-bottom:2px solid red\"  data-original=\""+CF.getStrDocRetriveLocation()+I_PEOPLE+"/"+I_IMAGE+"/"+rs.getString("supervisor_emp_id")+"/"+I_16x16+"/"+empImageMap.get(rs.getString("supervisor_emp_id"))+"\" border=\"0\" height=\"16px\" " +
													"width=\"16px;\" title=\""+hmEmpName.get(rs.getString("supervisor_emp_id"))+"(Role-Manager)\"/></a>");
								}
							}
							rs.close();
							pst.close();
						}
					}
					
					
					
					//peer
					if(hmOrientMemberID.get("Peer") != null && memberList.contains(hmOrientMemberID.get("Peer"))) {
						pst = con.prepareStatement("select emp_id from employee_official_details where  wlocation_id=? and grade_id=?");
						pst.setInt(1,uF.parseToInt(empLocation));
						pst.setInt(2,uF.parseToInt(gradeid));
//						System.out.println("pst=====>"+pst);
						rs = pst.executeQuery();
						while (rs.next()) {
							cnt++;
							if(cnt>7){
								sbMemList.append("<a href=\"javascript:void(0);\" style=\"margin-right: 4px;\" onclick=\"seeEmpList('"+selfList.get(j)+"','"+id+"');\" class=\"OR testa\">more..</a>");
								break;
							}
							/*if(selfList.get(j).trim().equals(rs.getString("emp_id"))){
								continue;
							}*/
							if(hmCheckAppraisal!=null && hmCheckAppraisal.containsKey(selfList.get(j).trim()+"_"+rs.getString("emp_id")+"_4")){
								sbMemList.append("<span style=\"margin-right: 4px;\"><img class=\"lazy img-circle\" src=\"userImages/avatar_photo.png\"  style=\"border-bottom:2px solid green\"  " +
										"data-original=\""+CF.getStrDocRetriveLocation()+I_PEOPLE+"/"+I_IMAGE+"/"+rs.getString("emp_id")+"/"+I_16x16+"/"+empImageMap.get(rs.getString("emp_id"))+"\" border=\"0\" height=\"16px\" " +
												"width=\"16px;\" title=\""+hmEmpName.get(rs.getString("emp_id"))+"(Role-Peer)\"/></span>");
							}else{
								sbMemList.append("<a href=\"javascript:void(0)\" style=\"margin-right: 4px;\" onclick=\"getEmpProfile('"+rs.getString("emp_id")+"')\" >" +
										"<img class=\"lazy img-circle\" src=\"userImages/avatar_photo.png\"  style=\"border-bottom:2px solid red\"  " +
										"data-original=\""+CF.getStrDocRetriveLocation()+I_PEOPLE+"/"+I_IMAGE+"/"+rs.getString("emp_id")+"/"+I_16x16+"/"+empImageMap.get(rs.getString("emp_id"))+"\" border=\"0\" height=\"16px\" " +
												"width=\"16px;\" title=\""+hmEmpName.get(rs.getString("emp_id"))+"(Role-Peer)\"/></a>");
							}
						}
						rs.close();
						pst.close();
					}
					
					//CEO
					if(hmOrientMemberID.get("CEO") != null && memberList.contains(hmOrientMemberID.get("CEO"))) {
						if(hmCheckCeo.get(CEO)!=null) {
							cnt++;
							sbMemList.append("<span style=\"margin-right: 4px;\"><img class=\"lazy img-circle\" src=\"userImages/avatar_photo.png\" style=\"border-bottom:2px solid green\" data-original=\""+CF.getStrDocRetriveLocation()+I_PEOPLE+"/"+I_IMAGE+"/"+hmCheckCeo.get(CEO)+"/"+I_16x16+"/"+empImageMap.get(hmCheckCeo.get(CEO))+"\" border=\"0\" height=\"16px\" " +
									"width=\"16px;\" title=\""+hmEmpName.get(hmCheckCeo.get(CEO))+"(Role-CEO)\"/></span>");
						}else{
										
							pst = con.prepareStatement("select eod.emp_id from employee_official_details eod,user_details ud " +
									" join employee_personal_details on emp_per_id=emp_id and is_alive=true " +
									" where eod.wlocation_id=? and eod.emp_id=ud.emp_id and ud.usertype_id=?");
							pst.setInt(1,uF.parseToInt(hmEmpWlocationMap.get(selfList.get(j))));
							pst.setInt(2, 5); 
							rs = pst.executeQuery();
							List<String> ceoList=new ArrayList<String>();
							while (rs.next()) {
								
								ceoList.add(rs.getString("emp_id"));
								cnt++;
								if(cnt>7){
									sbMemList.append("<a href=\"javascript:void(0);\" style=\"margin-right: 4px;\" onclick=\"seeEmpList('"+selfList.get(j)+"','"+id+"');\" class=\"OR testa\">more..</a>");
									break;
								}
								
								if(hmCheckAppraisal!=null && hmCheckAppraisal.containsKey(selfList.get(j).trim()+"_"+rs.getString("emp_id")+"_5")){
									sbMemList.append("<span style=\"margin-right: 4px;\"><img class=\"lazy img-circle\" src=\"userImages/avatar_photo.png\" style=\"border-bottom:2px solid green\" data-original=\""+CF.getStrDocRetriveLocation()+I_PEOPLE+"/"+I_IMAGE+"/"+rs.getString("emp_id")+"/"+I_16x16+"/"+empImageMap.get(rs.getString("emp_id"))+"\" border=\"0\" height=\"16px\" " +
												"width=\"16px;\" title=\""+hmEmpName.get(rs.getString("emp_id"))+"(Role-CEO)\"/></span>");
								}else{ 
									sbMemList.append("<a href=\"javascript:void(0)\" style=\"margin-right: 4px;\" onclick=\"getEmpProfile('"+rs.getString("emp_id")+"')\" >" +
											"<img class=\"lazy img-circle\" src=\"userImages/avatar_photo.png\" style=\"border-bottom:2px solid red\" data-original=\""+CF.getStrDocRetriveLocation()+I_PEOPLE+"/"+I_IMAGE+"/"+rs.getString("emp_id")+"/"+I_16x16+"/"+empImageMap.get(rs.getString("emp_id"))+"\" border=\"0\" height=\"16px\" " +
													"width=\"16px;\" title=\""+hmEmpName.get(rs.getString("emp_id"))+"(Role-CEO)\"/></a>");
								}
							}
							rs.close();
							pst.close();
						}
					}
					
					//HOD
					if(hmOrientMemberID.get("HOD") != null && memberList.contains(hmOrientMemberID.get("HOD"))) {
						if(hmCheckHod.get(HOD)!=null){
							cnt++;
							sbMemList.append("<span style=\"margin-right: 4px;\"><img class=\"lazy img-circle\" src=\"userImages/avatar_photo.png\"  style=\"border-bottom:2px solid green\"  data-original=\""+CF.getStrDocRetriveLocation()+I_PEOPLE+"/"+I_IMAGE+"/"+hmCheckHod.get(HOD)+"/"+I_16x16+"/"+empImageMap.get(hmCheckHod.get(HOD))+"\" border=\"0\" height=\"16px\" " +
									"width=\"16px;\" title=\""+hmEmpName.get(hmCheckHod.get(HOD))+"(Role-HOD)\"/></span>");
						}else{
							pst = con.prepareStatement("select hod_emp_id from employee_official_details eod where eod.emp_id=? and hod_emp_id>0");
							pst.setInt(1,uF.parseToInt(selfList.get(j)));
							rs = pst.executeQuery();
							while (rs.next()) {
								cnt++;
								if(cnt>7){
									sbMemList.append("<a href=\"javascript:void(0);\" style=\"margin-right: 4px;\" onclick=\"seeEmpList('"+selfList.get(j)+"','"+id+"');\" class=\"OR testa\">more..</a>");
									break;
								}
								if(hmCheckAppraisal!=null && hmCheckAppraisal.containsKey(selfList.get(j).trim()+"_"+rs.getString("hod_emp_id")+"_13")){
									sbMemList.append("<span style=\"margin-right: 4px;\"><img class=\"lazy img-circle\" src=\"userImages/avatar_photo.png\"  style=\"border-bottom:2px solid green\"  data-original=\""+CF.getStrDocRetriveLocation()+I_PEOPLE+"/"+I_IMAGE+"/"+rs.getString("hod_emp_id")+"/"+I_16x16+"/"+empImageMap.get(rs.getString("hod_emp_id"))+"\" border=\"0\" height=\"16px\" " +
													"width=\"16px;\" title=\""+hmEmpName.get(rs.getString("hod_emp_id"))+"(Role-HOD)\"/></span>");
								}else{
									sbMemList.append("<a href=\"javascript:void(0)\" style=\"margin-right: 4px;\" onclick=\"getEmpProfile('"+rs.getString("hod_emp_id")+"')\" >" +
											"<img class=\"lazy img-circle\" src=\"userImages/avatar_photo.png\"  style=\"border-bottom:2px solid red\"  data-original=\""+CF.getStrDocRetriveLocation()+I_PEOPLE+"/"+I_IMAGE+"/"+rs.getString("hod_emp_id")+"/"+I_16x16+"/"+empImageMap.get(rs.getString("hod_emp_id"))+"\" border=\"0\" height=\"16px\" " +
													"width=\"16px;\" title=\""+hmEmpName.get(rs.getString("hod_emp_id"))+"(Role-HOD)\"/></a>");
								}
							}
							rs.close();
							pst.close();
						}
					}
					hmMemberMP.put(selfList.get(j).trim(), sbMemList.toString());
				}
				
			}
			request.setAttribute("hmMemberMP", hmMemberMP);
			

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst); 
			db.closeConnection(con);
		}

	}
	
	
	

	private void getEmpSupervisor() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		Map<String,String> hmEmpSuperVisor=new HashMap<String, String>();
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("Select emp_id,supervisor_emp_id from employee_official_details");
			rs = pst.executeQuery();
			
			while (rs.next()) {
				hmEmpSuperVisor.put(rs.getString("emp_id"),rs.getString("supervisor_emp_id"));
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmEmpSuperVisor", hmEmpSuperVisor);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst); 
			db.closeConnection(con);
		}

	}

	private void getRemarks() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();

		Database db = new Database();
		db.setRequest(request);
		String remark = null;
		String strApprovedBy = null;
		Map<String,String> hmRemark=new HashMap<String, String>();
		boolean flag= false;
		try {
			con = db.makeConnection(con);
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			
			pst = con.prepareStatement("Select sattlement_comment,if_approved,user_id, emp_fname,emp_mname, emp_lname,activity_id1,afs.emp_id,appraisal_id,appraisal_freq_id" +
							",_date from appraisal_final_sattlement afs,employee_personal_details epd  where afs.user_id = epd.emp_per_id");
			rs = pst.executeQuery();
			
			while (rs.next()) {
				remark = rs.getString("sattlement_comment");
				if (remark != null) {
					remark = remark.replace("\n", "<br/>");
				}
				flag = uF.parseToBoolean(rs.getString("if_approved"));
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
			
				strApprovedBy = rs.getString("emp_fname") +strEmpMName+ " "+ rs.getString("emp_lname");
				
				hmRemark.put(rs.getString("appraisal_id")+"_"+rs.getString("appraisal_freq_id")+"_"+rs.getString("emp_id"), strApprovedBy+" on "+ uF.getDateFormat(rs.getString("_date"), DBDATE, DATE_FORMAT));
			}
			rs.close();
			pst.close();
			
			/*request.setAttribute("hrremark", remark);
			request.setAttribute("flag", flag);
			request.setAttribute("strApprovedBy", strApprovedBy);*/
			
			request.setAttribute("hmRemark", hmRemark);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst); 
			db.closeConnection(con);
		}

	}

	private void getAppraisalStatus(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		
		try {
			con = db.makeConnection(con);
			Map<String, String> hmFrequency = new HashMap<String, String>();
			pst = con.prepareStatement("select * from appraisal_frequency");
			rs = pst.executeQuery();
			while (rs.next()) {
				hmFrequency.put(rs.getString("appraisal_frequency_id"), rs.getString("frequency_name"));
			}
			rs.close();
			pst.close();
			
			String self_ids = null;
			String oriented_type = null;
			Map<String, String> orientationMemberMp = getOrientationMember();
			Map<String, String> hmEmpCode = CF.getEmpCodeMap(con);
			Map<String, String> hmEmpCodeDesig = CF.getEmpDesigMap(con);
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			request.setAttribute("hmEmpCode", hmEmpCode);
			request.setAttribute("hmEmpCodeDesig", hmEmpCodeDesig);
			request.setAttribute("hmEmpName", hmEmpName);
			Map<String, String> hmDesignation = CF.getDesigMap(con);
			Map<String, String> hmGradeMap = CF.getGradeMap(con);
			Map<String, String> hmLevelMap = getLevelMap(con);
			Map<String, String> orientationMp = getOrientationValue(con);
			pst = con.prepareStatement("select * from appraisal_details a, appraisal_details_frequency adf where a.appraisal_details_id= adf.appraisal_id" 
					+ " and (adf.is_delete is null or adf.is_delete = false) and a.appraisal_details_id=?");
			pst.setInt(1, uF.parseToInt(id));
			rs = pst.executeQuery();
			Map<String, String> hmUserTypeId = CF.getUserTypeIdMap(con);
			Map<String, String> appraisalMp = new HashMap<String, String>();
			int memberCount=0;
			while (rs.next()) {
				List<String> memberList = Arrays.asList(rs.getString("usertype_member").split(","));

				String memberName = "";
				for (int i = 0; i < memberList.size(); i++) {
					if (i == 0) {
						memberName += orientationMemberMp.get(memberList.get(i));
					} else {
						memberName += ", " + orientationMemberMp.get(memberList.get(i));
					}
					memberCount++;
				}
				appraisalMp.put("ID", rs.getString("appraisal_details_id"));
				appraisalMp.put("APPRAISAL", rs.getString("appraisal_name"));
				appraisalMp.put("APPRAISALTYPE", uF.showData(rs.getString("appraisal_type"), ""));
				appraisalMp.put("DESCRIPTION", uF.showData(rs.getString("appraisal_description"), ""));
				appraisalMp.put("INSTRUCTION", uF.showData(rs.getString("appraisal_instruction"), ""));
				appraisalMp.put("ORIENT", orientationMp.get(rs.getString("oriented_type")) + "&deg( " + memberName + " )");
				appraisalMp.put("EMPLOYEE", uF.showData(getAppendData(rs.getString("self_ids"), hmEmpName), ""));
				appraisalMp.put("LEVEL", uF.showData(hmLevelMap.get(rs.getString("level_id")), ""));
				appraisalMp.put("DESIG", hmDesignation.get(rs.getString("desig_id")));
				appraisalMp.put("GRADE", hmGradeMap.get(rs.getString("grade_id")));
				appraisalMp.put("WLOCATION", rs.getString("wlocation_id"));
				appraisalMp.put("PEER", rs.getString("peer_ids"));
				appraisalMp.put("SELFID", rs.getString("self_ids"));
				appraisalMp.put("APPRAISEE", uF.showData(getAppendData(rs.getString("self_ids"), hmEmpName), ""));
				appraisalMp.put("SUPERVISORID", rs.getString("supervisor_id"));
				appraisalMp.put("FREQUENCY", uF.showData(hmFrequency.get(rs.getString("frequency")), ""));
				appraisalMp.put("FROM", uF.getDateFormat(rs.getString("from_date"), DBDATE, CF.getStrReportDateFormat()));
				appraisalMp.put("TO", uF.getDateFormat(rs.getString("to_date"), DBDATE, CF.getStrReportDateFormat()));
				appraisalMp.put("APP_FREQ_ID", rs.getString("appraisal_freq_id"));
				appraisalMp.put("FREQ_START_DATE", uF.getDateFormat(rs.getString("freq_start_date"), DBDATE, CF.getStrReportDateFormat()));
				appraisalMp.put("FREQ_END_DATE", uF.getDateFormat(rs.getString("freq_end_date"), DBDATE, CF.getStrReportDateFormat()));
				
				StringBuilder sbAppraisers = new StringBuilder();
				if(rs.getString("usertype_member") != null && rs.getString("usertype_member").length()>0) {
					List<String> alAppraiserMember = Arrays.asList(rs.getString("usertype_member").split(","));
					for(int i=0; alAppraiserMember != null && i<alAppraiserMember.size(); i++) {
						if(uF.parseToInt(alAppraiserMember.get(i)) == uF.parseToInt(hmUserTypeId.get(MANAGER))) {
							sbAppraisers.append("Managers: " + uF.showData(getAppendData(rs.getString("supervisor_id"), hmEmpName), "N/A")+"</br>");
						} else if(uF.parseToInt(alAppraiserMember.get(i)) == uF.parseToInt(hmUserTypeId.get(HOD))) {
							sbAppraisers.append("HODs: " + uF.showData(getAppendData(rs.getString("hod_ids"), hmEmpName), "N/A")+"</br>");
						} else if(uF.parseToInt(alAppraiserMember.get(i)) == uF.parseToInt(hmUserTypeId.get(CEO))) {
							sbAppraisers.append("CEOs: " + uF.showData(getAppendData(rs.getString("ceo_ids"), hmEmpName), "N/A")+"</br>");
						} else if(uF.parseToInt(alAppraiserMember.get(i)) == uF.parseToInt(hmUserTypeId.get(HRMANAGER))) {
							sbAppraisers.append("HRs: " + uF.showData(getAppendData(rs.getString("hr_ids"), hmEmpName), "N/A")+"</br>");
						} else if(uF.parseToInt(alAppraiserMember.get(i)) == uF.parseToInt(hmUserTypeId.get(ADMIN))) {
							sbAppraisers.append("Global HRs: " + uF.showData(getAppendData(rs.getString("hr_ids"), hmEmpName), "N/A")+"</br>");
						} else if(uF.parseToInt(alAppraiserMember.get(i)) == 4) {
							sbAppraisers.append("Peers: " + uF.showData(getAppendData(rs.getString("peer_ids"), hmEmpName), "N/A")+"</br>");
						} else if(uF.parseToInt(alAppraiserMember.get(i)) == 10) {
							sbAppraisers.append("Anyone: " + uF.showData(getAppendData(rs.getString("other_ids"), hmEmpName), "N/A")+"</br>");
						}
					}
				}
				appraisalMp.put("APPRAISER", uF.showData(sbAppraisers.toString(), ""));
				appraisalMp.put("REVIEWER", uF.showData(getAppendData(rs.getString("reviewer_id"), hmEmpName), ""));


				request.setAttribute("memberList", memberList);
			}
			rs.close();
			pst.close();
			
			request.setAttribute("memberCount", memberCount);
			getEmpWlocation(appraisalMp.get("SELFID"));
			
			String empids=appraisalMp.get("SELFID")!=null && !appraisalMp.get("SELFID").equals("") ? appraisalMp.get("SELFID").substring(1, appraisalMp.get("SELFID").length()-1) : "";
			
			pst = con.prepareStatement("select emp_image,emp_per_id from employee_personal_details where emp_per_id in(" + empids + ")");
			rs = pst.executeQuery();
			Map<String, String> empImageMap = new HashMap<String, String>();
			while (rs.next()) {
				empImageMap.put(rs.getString("emp_per_id"), rs.getString("emp_image"));
			}
			rs.close();
			pst.close();
			request.setAttribute("empImageMap", empImageMap);

			request.setAttribute("appraisalMp", appraisalMp);

			pst = con.prepareStatement("select self_ids,appraisal_details_id,oriented_type from appraisal_details where appraisal_details_id=?");
			pst.setInt(1, uF.parseToInt(id));
			rs = pst.executeQuery();
			while (rs.next()) {
				self_ids = rs.getString("self_ids");
				// appraisal_details_id = rs.getInt(2);
				oriented_type = rs.getString(3);
			}
			rs.close();
			pst.close();
			
			self_ids=self_ids!=null && !self_ids.equals("") ? self_ids.substring(1, self_ids.length()-1) :"";
			List<String> empList = Arrays.asList(self_ids.split(","));

			// Map<String, String> hmUserTypeID = new HashMap<String, String>();
			// pst = con
			// .prepareStatement("select user_type_id,user_type from user_type");
			// rs = pst.executeQuery();
			// while (rs.next()) {
			// hmUserTypeID.put(rs.getString(2), rs.getString(1));
			// }

			pst = con.prepareStatement("select *,(marks*100/weightage) as average from(select sum(marks) as marks ,sum(weightage) as weightage,user_type_id,emp_id "
					+ "from appraisal_question_answer where appraisal_id=? and appraisal_freq_id = ? and weightage>0 group by user_type_id,emp_id)as a");
			pst.setInt(1, uF.parseToInt(getId()));
			pst.setInt(2, uF.parseToInt(getAppFreqId()));
			rs = pst.executeQuery();
			Map<String, Map<String, String>> outerMp = new HashMap<String, Map<String, String>>();
			
			String strEmpIdNew = null;
			String strEmpIdOld = null;
			
			double dblTotalMarks = 0; 
			double dblTotalWeightage = 0;
			while (rs.next()) {
 
				strEmpIdNew = rs.getString("emp_id");
				if(strEmpIdNew!=null && !strEmpIdNew.equalsIgnoreCase(strEmpIdOld)){
					dblTotalMarks = 0;
					dblTotalWeightage = 0;
				}
				
				
				dblTotalMarks += uF.parseToDouble(rs.getString("marks"));
//				System.out.println("dblTotalMarks"+dblTotalMarks);
				
				dblTotalWeightage += uF.parseToDouble(rs.getString("weightage"));
				Map<String, String> value = outerMp.get(rs.getString("emp_id"));
				if (value == null)
					value = new HashMap<String, String>();
				value.put(rs.getString("user_type_id"), uF.formatIntoTwoDecimal(rs.getDouble("average")));
				if(dblTotalWeightage>0){
					value.put("AGGREGATE", uF.formatIntoTwoDecimal((dblTotalMarks * 100)/dblTotalWeightage));
				}
				outerMp.put(rs.getString("emp_id"), value);
				
				strEmpIdOld = strEmpIdNew;
			}
			rs.close();
			pst.close();
			
			// request.setAttribute("hmUserTypeID", hmUserTypeID);
			request.setAttribute("outerMp", outerMp);
			request.setAttribute("empList", empList);
			request.setAttribute("oriented_type", oriented_type);
			
			
			pst = con.prepareStatement("select count(*)as count,emp_id,appraisal_id, appraisal_freq_id from (select emp_id,appraisal_id,appraisal_freq_id,user_type_id from appraisal_question_answer "
					+ "where appraisal_id=? and appraisal_freq_id =? group by emp_id,user_type_id,appraisal_id,appraisal_freq_id)as a group by emp_id,appraisal_id,appraisal_freq_id");
			pst.setInt(1, uF.parseToInt(getId()));	
			pst.setInt(2, uF.parseToInt(getAppFreqId()));
//			System.out.println("pst===>"+pst);
			rs = pst.executeQuery();
			Map<String,String> hmEmpCount=new HashMap<String, String>();
			while (rs.next()) {
				hmEmpCount.put(rs.getString("emp_id"), rs.getString("count"));				
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmEmpCount", hmEmpCount);

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst); 
			db.closeConnection(con);
		}
	}

	
	private void getEmpWlocation(String empIds) {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;

		try {
			Map<String, String> locationMp = new HashMap<String, String>();
			con = db.makeConnection(con);
//			System.out.println("empIds 1====> "+empIds);
			empIds=empIds !=null && !empIds.equals("") ? empIds.substring(1, empIds.length()-1) : "";
//			System.out.println("empIds 2 ====> "+empIds);
			pst = con.prepareStatement("select eod.wlocation_id,emp_id,wlocation_name from employee_official_details eod,work_location_info wli where eod.wlocation_id=wli.wlocation_id and emp_id in("
							+ empIds + ")");
//			System.out.println("pst====> "+pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				locationMp.put(rs.getString("emp_id"), rs.getString("wlocation_name"));
			}
			rs.close();
			pst.close();
			
//			System.out.println("locationMp ====> "+locationMp);
			request.setAttribute("locationMp", locationMp);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst); 
			db.closeConnection(con);
		}
	}

	// private String getOrientationMemberDetails(int id) {
	// Connection con = null;
	// PreparedStatement pst = null;
	// Database db = new Database();
	// ResultSet rs = null;
	// Map<String,String>
	// orientationMp=(Map<String,String>)request.getAttribute("orientationMemberMp");
	// StringBuilder sb=new StringBuilder();
	// try {
	// List<String> memberList=new ArrayList<String>();
	// con = db.makeConnection(con);
	//
	// pst =
	// con.prepareStatement("select * from orientation_details where orientation_id=?");
	// pst.setInt(1,id);
	// rs=pst.executeQuery();
	//
	// while(rs.next()){
	// sb.append(orientationMp.get(rs.getString("member_id"))+",");
	// memberList.add(rs.getString("member_id"));
	// }
	//
	// request.setAttribute("memberList", memberList);
	// } catch (Exception e) {
	// e.printStackTrace();
	// } finally {
	// db.closeConnection(con);
	// db.closeStatements(pst);
	// }
	// return sb.toString();
	// }

	private Map<String, String> getOrientationValue(Connection con) {
		PreparedStatement pst = null;
		ResultSet rs = null;

		Map<String, String> orientationMp = new HashMap<String, String>();
		try {

			pst = con.prepareStatement("select * from apparisal_orientation");
			rs = pst.executeQuery();
			while (rs.next()) {
				orientationMp.put(rs.getString("apparisal_orientation_id"), rs.getString("orientation_name"));
			}
			rs.close();
			pst.close();

			request.setAttribute("orientationMp", orientationMp);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if(pst != null) {
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}
		return orientationMp;
	}

	
	public Map<String, String> getLevelMap(Connection con) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String, String> hmLevelMap = new HashMap<String, String>();
		try {
			pst = con.prepareStatement(selectLevel);
			rs = pst.executeQuery();
			while (rs.next()) {
				hmLevelMap.put(rs.getString("level_id"), rs.getString("level_name") + "[" + rs.getString("level_code") + "]");
			}
			rs.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if(pst != null) {
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}
		return hmLevelMap;
	}

	private Map<String, String> getOrientationMember() {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;

		Map<String, String> orientationMemberMp = new HashMap<String, String>();
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from orientation_member where status=true order by weightage");
			rs = pst.executeQuery();
			while (rs.next()) {
				//orientationMemberMp.put(rs.getString("orientation_member_id"), rs.getString("member_name"));
				orientationMemberMp.put(rs.getString("member_id"), rs.getString("member_name"));
			}
			rs.close();
			pst.close();

			request.setAttribute("orientationMemberMp", orientationMemberMp);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst); 
			db.closeConnection(con);
		}
		return orientationMemberMp;
	}

	
	// private List<String> getEmpList(String self_ids) {
	// List<String> empList = new ArrayList<String>();
	//
	// if (self_ids != null && !self_ids.equals("")) {
	//
	// if (self_ids.contains(",")) {
	//
	// String[] temp = self_ids.split(",");
	//
	// for (int i = 1; i < temp.length; i++) {
	//
	// empList.add(temp[i].trim());
	//
	// }
	// } else {
	// empList.add(self_ids);
	// }
	//
	// } else {
	// return null;
	// }
	//
	// return empList;
	// }

	private String getAppendData(String strID, Map<String, String> mp) {
		StringBuilder sb = new StringBuilder();

		if (strID != null && !strID.equals("")) {
			strID=strID.substring(1, strID.length()-1);
			if (strID.contains(",")) {

				String[] temp = strID.split(",");

				for (int i = 0; i < temp.length; i++) {
					if (i == 0) {
						sb.append(mp.get(temp[i].trim()));
					} else {
						sb.append(", " + mp.get(temp[i].trim()));
					}
				}
			} else {
				return mp.get(strID);
			}

		} else {
			return null;
		}

		return sb.toString();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {

		this.request = request;
	}


	public String getAppFreqId() {
		return appFreqId;
	}


	public void setAppFreqId(String appFreqId) {
		this.appFreqId = appFreqId;
	}
}
