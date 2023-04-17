package com.konnect.jpms.performance;

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

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class AppraisalApproveMembers extends ActionSupport  implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = -6145081837111360463L;
	HttpSession session;
	String strSessionEmpId;
	String strSessionUserType;
	CommonFunctions CF;
	private String id;
	private String empID;
	private String appFreqId;
	public String execute() {
		session = request.getSession();
		strSessionEmpId = (String) session.getAttribute(EMPID);
		strSessionUserType = (String) session.getAttribute(USERTYPE);
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		
		request.setAttribute(PAGE, "/jsp/performance/EmpListPopup.jsp");
		request.setAttribute(TITLE, "Employee");
		getEmpList();
		return SUCCESS;
	}

	
	public void getEmpList(){
		
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		UtilityFunctions uF=new UtilityFunctions();
		
		try {
			con = db.makeConnection(con);
			Map<String,String> hmEmpName=CF.getEmpNameMap(con, null,null);
			Map<String, String> hmEmpWlocationMap=CF.getEmpWlocationMap(con);
			Map<String,String> hmMemberMP=new HashMap<String, String>();			
			
			pst = con.prepareStatement("select * from appraisal_details where appraisal_details_id =?");
			pst.setInt(1, uF.parseToInt(id));
			rs = pst.executeQuery();
			String mem="";
			String self_ids="";
			String oriented_type="";
			String other_ids = "";
			String hr_ids = "";
			String supervisor_id = "";
			String peer_ids = "";
			String ceo_ids = "";
			String hod_ids = "";
			while (rs.next()) {
				mem=rs.getString("usertype_member");	
				self_ids=rs.getString("self_ids");
				oriented_type = rs.getString("oriented_type");
				other_ids = rs.getString("other_ids");
				hr_ids = rs.getString("hr_ids");
				supervisor_id = rs.getString("supervisor_id");
				peer_ids = rs.getString("peer_ids");
				ceo_ids = rs.getString("ceo_ids");
				hod_ids = rs.getString("hod_ids");
			}
			rs.close();
			pst.close();
			
			List<String> memberList= CF.getOrientationMemberDetails(con,uF.parseToInt(oriented_type));
			Map<String, String> hmOrientMemberID = CF.getOrientMemberID(con);
			List<String> hrList = new ArrayList<String>();
			List<String> managerList = new ArrayList<String>();
			List<String> peerList = new ArrayList<String>();
			List<String> ceoList = new ArrayList<String>();
			List<String> hodList = new ArrayList<String>();
			
			if(hr_ids != null && !hr_ids.equals("")){
				hrList = Arrays.asList(hr_ids.split(","));
			}
			
			if(supervisor_id != null && !supervisor_id.equals("")){
				managerList = Arrays.asList(supervisor_id.split(","));
			}
			
			if(peer_ids != null && !peer_ids.equals("")){
				peerList = Arrays.asList(peer_ids.split(","));
			}
			
			if(ceo_ids != null && !ceo_ids.equals("")){
				ceoList = Arrays.asList(ceo_ids.split(","));
			}
			if(hod_ids != null && !hod_ids.equals("")){
				hodList = Arrays.asList(hod_ids.split(","));
			}
			
			System.out.println("hrList ===>> " + hrList);
			System.out.println("managerList ===>> " + managerList);
			System.out.println("peerList ===>> " + peerList);
			System.out.println("ceoList ===>> " + ceoList);
			System.out.println("hodList ===>> " + hodList);
			
			pst=con.prepareStatement("select emp_image,emp_per_id from employee_personal_details ");
			rs=pst.executeQuery();
			Map<String,String> empImageMap=new HashMap<String,String>();
			while(rs.next()){
				empImageMap.put(rs.getString("emp_per_id"),rs.getString("emp_image"));
			}
			rs.close();
			pst.close();
			
			if(getEmpID()!=null && !getEmpID().equals("")) {
//				pst=con.prepareStatement("select wlocation_id,grade_id from employee_official_details where emp_id=?");
//				pst.setInt(1, uF.parseToInt(getEmpID()));
//				rs=pst.executeQuery();
//				String empLocation="";
//				String gradeid="";
//				while(rs.next()){
//					empLocation=rs.getString("wlocation_id");
//					gradeid=rs.getString("grade_id");
//				}
//				rs.close();
//				pst.close();
				
				pst=con.prepareStatement("select emp_id,user_id,user_type_id from appraisal_question_answer where appraisal_id=? and emp_id=? " +
						" and appraisal_freq_id=? and appraisal_question_answer_id in(select max(appraisal_question_answer_id) from appraisal_question_answer " +
						" where appraisal_id=? and emp_id=? and appraisal_freq_id= ? group by user_type_id,user_id)");
				pst.setInt(1, uF.parseToInt(getId()));
				pst.setInt(2, uF.parseToInt(getEmpID()));
				pst.setInt(3, uF.parseToInt(getAppFreqId()));
				pst.setInt(4, uF.parseToInt(getId()));
				pst.setInt(5, uF.parseToInt(getEmpID()));
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
					}else if(rs.getString("user_type_id").equals("2")){						
						hmCheckMgr.put(MANAGER, rs.getString("user_id"));
					} else if(rs.getString("user_type_id").equals("5")){						
						hmCheckCeo.put(CEO, rs.getString("user_id"));
					} else if(rs.getString("user_type_id").equals("13")){						
						hmCheckHod.put(HOD, rs.getString("user_id"));
					}
				}
				rs.close();
				pst.close();
				
//				System.out.println("hmCheckAppraisal ===>> " + hmCheckAppraisal);
//				System.out.println("hmCheckHR ===>> " + hmCheckHR);
//				System.out.println("hmCheckMgr ===>> " + hmCheckMgr);
//				System.out.println("hmCheckCeo ===>> " + hmCheckCeo);
//				System.out.println("hmCheckHod ===>> " + hmCheckHod);
				
				StringBuilder sbMemList=new StringBuilder();
//				int cnt=0;
				
				//self
				if(hmOrientMemberID.get("Self") != null && memberList.contains(hmOrientMemberID.get("Self"))) {
					String brdrColor = "red";
					if(hmCheckAppraisal!=null && hmCheckAppraisal.containsKey(getEmpID()+"_"+getEmpID()+"_3")){
						brdrColor = "green";
					}
					sbMemList.append("<div style=\"float: left; width: 100%; margin: 5px 0px;\"><span style=\"float: left;\"><a href=\"javascript:void(0)\" style=\"margin-right: 4px;\" onclick=\"getEmpProfile('"+getEmpID()+"','"+hmEmpName.get(getEmpID())+"')\" >" +
						"<img class=\"lazy img-circle img-circle\" src=\"userImages/avatar_photo.png\" style=\"border-bottom:2px solid "+brdrColor+"\" ");
					sbMemList.append(" data-original=\""+CF.getStrDocRetriveLocation()+I_PEOPLE+"/"+I_IMAGE+"/"+getEmpID()+"/"+I_60x60+"/"+empImageMap.get(getEmpID())+"\" " +
						"border=\"0\" height=\"50px\" width=\"50px;\"/></a></span>" +
						"<span style=\"float: left; margin-top: 30px;\">"+hmEmpName.get(getEmpID())+"(Role-SELF)</span></div>"); // title=\""+hmEmpName.get(getEmpID())+"(Role-SELF)\"
				}
				
				//HRMANAGER
				if(hmOrientMemberID.get("HR") != null && memberList.contains(hmOrientMemberID.get("HR"))) {
					if(hmCheckHR.get(HRMANAGER)!=null) {
//						cnt++;
						sbMemList.append("<div style=\"float: left; width: 100%; margin: 5px 0px;\"><span style=\"float: left;\"><a href=\"javascript:void(0)\" style=\"margin-right: 4px;\" onclick=\"getEmpProfile('"+hmCheckHR.get(HRMANAGER).trim()+"','"+hmEmpName.get(hmCheckHR.get(HRMANAGER).trim())+"')\" >" +
							"<img class=\"lazy img-circle img-circle\" src=\"userImages/avatar_photo.png\" style=\"border-bottom:2px solid green\" data-original=\""+CF.getStrDocRetriveLocation()+I_PEOPLE+"/"+I_IMAGE+"/"+hmCheckHR.get(HRMANAGER)+"/"+I_60x60+"/"+empImageMap.get(hmCheckHR.get(HRMANAGER))+"\" " +
							"border=\"0\" height=\"50px\" width=\"50px;\"/></a></span>" +
							"<span style=\"float: left; margin-top: 30px;\">"+hmEmpName.get(hmCheckHR.get(HRMANAGER))+"(Role-HR)</span></div>"); // title=\""+hmEmpName.get(hmCheckHR.get(HRMANAGER))+"(Role-HR)\"
					} else {
							for (int i = 0; hrList != null && !hrList.isEmpty() && i < hrList.size(); i++) {
								if(!hrList.get(i).trim().equals("")){
//									if(cnt>7) {
//										break;
//									} else {
//										cnt++;
//									}
									String brdrColor = "red";
									if(hmCheckAppraisal!=null && hmCheckAppraisal.containsKey(getEmpID()+"_"+hrList.get(i).trim()+"_7")){
										brdrColor = "green";
									}
									sbMemList.append("<div style=\"float: left; width: 100%; margin: 5px 0px;\"><span style=\"float: left;\"><a href=\"javascript:void(0)\" style=\"margin-right: 4px;\" onclick=\"getEmpProfile('"+hrList.get(i).trim()+"','"+hmEmpName.get(hrList.get(i).trim())+"')\" >" +
										"<img class=\"lazy img-circle\" src=\"userImages/avatar_photo.png\" style=\"border-bottom:2px solid "+brdrColor+"\" data-original=\""+CF.getStrDocRetriveLocation()+I_PEOPLE+"/"+I_IMAGE+"/"+hrList.get(i).trim()+"/"+I_60x60+"/"+empImageMap.get(hrList.get(i).trim())+"\" " +
										"border=\"0\" height=\"50px\" width=\"50px;\"/></a></span>" +
										"<span style=\"float: left; margin-top: 30px;\">"+hmEmpName.get(hrList.get(i).trim())+"(Role-HR)</span></div>"); // title=\""+hmEmpName.get(hrList.get(i).trim())+"(Role-HR)\"
								}
							}
//						}
					}
				}
				
				//Manager
				if(hmOrientMemberID.get("Manager") != null && memberList.contains(hmOrientMemberID.get("Manager"))) {
					if(hmCheckMgr.get(MANAGER)!=null) {
//						cnt++;
						sbMemList.append("<div style=\"float: left; width: 100%; margin: 5px 0px;\"><span style=\"float: left;\"><a href=\"javascript:void(0)\" style=\"margin-right: 4px;\" onclick=\"getEmpProfile('"+hmCheckMgr.get(MANAGER).trim()+"','"+hmEmpName.get(hmCheckMgr.get(MANAGER).trim())+"')\" >" +
								"<img class=\"lazy img-circle\" src=\"userImages/avatar_photo.png\"  style=\"border-bottom:2px solid green\" data-original=\"userImages/"+CF.getStrDocRetriveLocation()+I_PEOPLE+"/"+I_IMAGE+"/"+hmCheckMgr.get(MANAGER)+"/"+I_60x60+"/"+empImageMap.get(hmCheckMgr.get(MANAGER))+"\" " +
								"border=\"0\" height=\"50px\" width=\"50px;\"/></a></span>" +
								"<span style=\"float: left; margin-top: 30px;\">"+hmEmpName.get(hmCheckMgr.get(MANAGER))+"(Role-Manager)</span></div>"); // title=\""+hmEmpName.get(hmCheckMgr.get(MANAGER))+"(Role-Manager)\"
					} else {
						for (int i = 0; managerList != null && !managerList.isEmpty() && i < managerList.size(); i++) {
							if(!managerList.get(i).trim().equals("")) {
//								if(cnt>7) {
//									break;
//								} else {
//									cnt++;
//								}
								String brdrColor = "red";
								if(hmCheckAppraisal!=null && hmCheckAppraisal.containsKey(getEmpID()+"_"+managerList.get(i).trim()+"_2")) {
									brdrColor = "green";
								}
								sbMemList.append("<div style=\"float: left; width: 100%; margin: 5px 0px;\"><span style=\"float: left;\"><a href=\"javascript:void(0)\" style=\"margin-right: 4px;\" onclick=\"getEmpProfile('"+managerList.get(i).trim()+"','"+hmEmpName.get(managerList.get(i).trim())+"')\" >" +
									"<img class=\"lazy img-circle\" src=\"userImages/avatar_photo.png\"  style=\"border-bottom:2px solid "+brdrColor+"\" data-original=\""+CF.getStrDocRetriveLocation()+I_PEOPLE+"/"+I_IMAGE+"/"+managerList.get(i).trim()+"/"+I_60x60+"/"+empImageMap.get(managerList.get(i).trim())+"\" " +
									"border=\"0\" height=\"50px\" width=\"50px;\"/></a></span>" +
									"<span style=\"float: left; margin-top: 30px;\">"+hmEmpName.get(managerList.get(i).trim())+"(Role-Manager)</span></div>"); // title=\""+hmEmpName.get(managerList.get(i).trim())+"(Role-Manager)\" 
							}
						}
					}
				}
									
				//peer
				if(hmOrientMemberID.get("Peer") != null && memberList.contains(hmOrientMemberID.get("Peer"))) {
					for (int i = 0; peerList != null && !peerList.isEmpty() && i < peerList.size(); i++) {
						if(!peerList.get(i).trim().equals("")){
//							if(cnt>7) {
//								break;
//							} else {
//								cnt++;
//							}
							String brdrColor = "red";
							if(hmCheckAppraisal!=null && hmCheckAppraisal.containsKey(getEmpID()+"_"+peerList.get(i).trim()+"_4")){
								brdrColor = "green";
							}
							sbMemList.append("<div style=\"float: left; width: 100%; margin: 5px 0px;\"><span style=\"float: left;\"><a href=\"javascript:void(0)\" style=\"margin-right: 4px;\" onclick=\"getEmpProfile('"+peerList.get(i).trim()+"','"+hmEmpName.get(peerList.get(i).trim())+"')\" >" +
								"<img class=\"lazy img-circle\" src=\"userImages/avatar_photo.png\"  style=\"border-bottom:2px solid "+brdrColor+"\"  " +
								"data-original=\""+CF.getStrDocRetriveLocation()+I_PEOPLE+"/"+I_IMAGE+"/"+peerList.get(i).trim()+"/"+I_60x60+"/"+empImageMap.get(peerList.get(i).trim())+"\" " +
								"border=\"0\" height=\"50px\" width=\"50px;\"/></a></span>" +
								"<span style=\"float: left; margin-top: 30px;\">"+hmEmpName.get(peerList.get(i).trim())+"(Role-Peer)</span></div>"); // title=\""+hmEmpName.get(peerList.get(i).trim())+"(Role-Peer)\"
						}
					}
//					}
				}
				
				//CEO
				if(hmOrientMemberID.get("CEO") != null && memberList.contains(hmOrientMemberID.get("CEO"))) {
					if(hmCheckCeo.get(CEO)!=null) {
//						cnt++;
						sbMemList.append("<div style=\"float: left; width: 100%; margin: 5px 0px;\"><span style=\"float: left;\"><a href=\"javascript:void(0)\" style=\"margin-right: 4px;\" onclick=\"getEmpProfile('"+hmCheckCeo.get(CEO).trim()+"','"+hmEmpName.get(hmCheckCeo.get(CEO).trim())+"')\" >" +
							"<img class=\"lazy img-circle\" src=\"userImages/avatar_photo.png\" style=\"border-bottom:2px solid green\" data-original=\"userImages/"+CF.getStrDocRetriveLocation()+I_PEOPLE+"/"+I_IMAGE+"/"+hmCheckCeo.get(CEO)+"/"+I_60x60+"/"+empImageMap.get(hmCheckCeo.get(CEO))+"\" " +
							"border=\"0\" height=\"50px\" width=\"50px;\"/></a></span>" +
							"<span style=\"float: left; margin-top: 30px;\">"+hmEmpName.get(hmCheckCeo.get(CEO))+"(Role-CEO)</span></div>"); // title=\""+hmEmpName.get(hmCheckCeo.get(CEO))+"(Role-CEO)\"
					} else {
						for (int i = 0; ceoList != null && !ceoList.isEmpty() && i < ceoList.size(); i++) {
							if(!ceoList.get(i).trim().equals("")){
//								if(cnt>7) {
//									break;
//								} else {
//									cnt++;
//								}
								String brdrColor = "red";
								if(hmCheckAppraisal!=null && hmCheckAppraisal.containsKey(getEmpID()+"_"+ceoList.get(i).trim()+"_5")){
									brdrColor = "green";
								}
								sbMemList.append("<div style=\"float: left; width: 100%; margin: 5px 0px;\"><span style=\"float: left;\"><a href=\"javascript:void(0)\" style=\"margin-right: 4px;\" onclick=\"getEmpProfile('"+ceoList.get(i).trim()+"','"+hmEmpName.get(ceoList.get(i).trim())+"')\" >" +
									"<img class=\"lazy img-circle\" src=\"userImages/avatar_photo.png\"  style=\"border-bottom:2px solid "+brdrColor+"\"  data-original=\""+CF.getStrDocRetriveLocation()+I_PEOPLE+"/"+I_IMAGE+"/"+ceoList.get(i).trim()+"/"+I_60x60+"/"+empImageMap.get(ceoList.get(i).trim())+"\" " +
									"border=\"0\" height=\"50px\" width=\"50px;\"/></a></span>" +
									"<span style=\"float: left; margin-top: 30px;\">"+hmEmpName.get(ceoList.get(i).trim())+"(Role-CEO)</span></div>"); // title=\""+hmEmpName.get(ceoList.get(i).trim())+"(Role-CEO)\"
							}
						}
					}
				}
				
				//HOD
				if(hmOrientMemberID.get("HOD") != null && memberList.contains(hmOrientMemberID.get("HOD"))) {
					if(hmCheckHod.get(HOD)!=null) {
//						cnt++;
						sbMemList.append("<div style=\"float: left; width: 100%; margin: 5px 0px;\"><span style=\"float: left;\"><a href=\"javascript:void(0)\" style=\"margin-right: 4px;\" onclick=\"getEmpProfile('"+hmCheckHod.get(HOD).trim()+"','"+hmEmpName.get(hmCheckHod.get(HOD).trim())+"')\" >" +
								"<img class=\"lazy img-circle\" src=\"userImages/avatar_photo.png\"  style=\"border-bottom:2px solid green\"  data-original=\"userImages/"+CF.getStrDocRetriveLocation()+I_PEOPLE+"/"+I_IMAGE+"/"+hmCheckHod.get(HOD)+"/"+I_60x60+"/"+empImageMap.get(hmCheckHod.get(HOD))+"\" " +
								"border=\"0\" height=\"50px\" width=\"50px;\"/></a></span>" +
								"<span style=\"float: left; margin-top: 30px;\">"+hmEmpName.get(hmCheckHod.get(HOD))+"(Role-HOD)</span></div>"); // title=\""+hmEmpName.get(hmCheckHod.get(HOD))+"(Role-HOD)\"
					} else {
						for (int i = 0; hodList != null && !hodList.isEmpty() && i < hodList.size(); i++) {
							if(!hodList.get(i).trim().equals("")) {
//								if(cnt>7) {
//									break;
//								} else {
//									cnt++;
//								}
								String brdrColor = "red";
								if(hmCheckAppraisal!=null && hmCheckAppraisal.containsKey(getEmpID()+"_"+hodList.get(i).trim()+"_13")) {
									brdrColor = "green";
								}
								sbMemList.append("<div style=\"float: left; width: 100%; margin: 5px 0px;\"><span style=\"float: left;\"><a href=\"javascript:void(0)\" style=\"margin-right: 4px;\" onclick=\"getEmpProfile('"+hodList.get(i).trim()+"','"+hmEmpName.get(hodList.get(i).trim())+"')\" >" +
									"<img class=\"lazy img-circle\" src=\"userImages/avatar_photo.png\"  style=\"border-bottom:2px solid "+brdrColor+"\"  data-original=\""+CF.getStrDocRetriveLocation()+I_PEOPLE+"/"+I_IMAGE+"/"+hodList.get(i).trim()+"/"+I_60x60+"/"+empImageMap.get(hodList.get(i).trim())+"\" " +
									"border=\"0\" height=\"50px\" width=\"50px;\"/></a></span>" +
									"<span style=\"float: left; margin-top: 30px;\">"+hmEmpName.get(hodList.get(i).trim())+"(Role-HOD)</span></div>"); // title=\""+hmEmpName.get(hodList.get(i).trim())+"(Role-HOD)\"
							}
						}
					}
				}
				
				//other
				if(hmOrientMemberID.get("Anyone") != null && memberList.contains(hmOrientMemberID.get("Anyone"))) {
					String othrIds = "";
					if(other_ids != null && other_ids.trim().length() > 1){
						othrIds = other_ids.substring(1, other_ids.trim().length()-1);
//					System.out.println("othrIds =====> "+othrIds);
						pst = con.prepareStatement("select emp_per_id from employee_personal_details where  emp_per_id in("+ othrIds + ")");
//						System.out.println("pst=====>"+pst);
						rs = pst.executeQuery();
						while (rs.next()) {
//							if(cnt>7) {
//								break;
//							} else {
//								cnt++;
//							}
							String brdrColor = "red";
							if(hmCheckAppraisal!=null && hmCheckAppraisal.containsKey(getEmpID()+"_"+rs.getString("emp_per_id")+"_10")){
								brdrColor = "green";
							}		
							sbMemList.append("<div style=\"float: left; width: 100%; margin: 5px 0px;\"><span style=\"float: left;\"><a href=\"javascript:void(0)\" style=\"margin-right: 4px;\" onclick=\"getEmpProfile('"+rs.getString("emp_per_id")+"','"+hmEmpName.get(rs.getString("emp_per_id"))+"')\" >" +
								"<img class=\"lazy img-circle\" src=\"userImages/avatar_photo.png\"  style=\"border-bottom:2px solid "+brdrColor+"\" data-original=\""+CF.getStrDocRetriveLocation()+I_PEOPLE+"/"+I_IMAGE+"/"+rs.getString("emp_per_id")+"/"+I_60x60+"/"+empImageMap.get(rs.getString("emp_per_id"))+"\" " +
								"border=\"0\" height=\"16px\" width=\"16px;\"/></a></span>" +
								"<span style=\"float: left; margin-top: 30px;\">"+hmEmpName.get(rs.getString("emp_per_id"))+"(Role-Anyone)</span></div>"); //  title=\""+hmEmpName.get(rs.getString("emp_per_id"))+"(Role-Anyone)\"
						}
						rs.close();
						pst.close();
						
					}
				  }
//				if(cnt>7) {
//					sbMemList.append("<a href=\"javascript:void(0);\" style=\"margin-right: 4px;\" onclick=\"seeEmpList('"+getEmpID()+"','"+id+"','"+getAppFreqId()+"');\" class=\"OR testa\">more..</a>");
//				}
				hmMemberMP.put(getEmpID(), sbMemList.toString());
			
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
	
	
//	public void getEmpList(){
//	
//		Connection con = null;
//		PreparedStatement pst = null;
//		Database db = new Database();
//		db.setRequest(request);
//		ResultSet rs = null;
//		UtilityFunctions uF=new UtilityFunctions();
//		
//		try {
//			con = db.makeConnection(con);
//			Map<String,String> hmEmpName=CF.getEmpNameMap(con, null,null);
//			Map<String, String> hmEmpWlocationMap=CF.getEmpWlocationMap(con);
//			Map<String,String> hmMemberMP=new HashMap<String, String>();			
//			
//			pst = con.prepareStatement("select * from appraisal_details where appraisal_details_id =?");
//			pst.setInt(1, uF.parseToInt(id));
////			System.out.println("pst=====>"+pst);
//			rs = pst.executeQuery();
//			String mem="";
//			String self_ids="";
//			String oriented_type="";
//			while (rs.next()) {
//				mem=rs.getString("usertype_member");	
//				self_ids=rs.getString("self_ids");
//				oriented_type=rs.getString("oriented_type");
//			}
//			rs.close();
//			pst.close();
//			
//			List<String> memberList=CF.getOrientationMemberDetails(con,uF.parseToInt(oriented_type));
//			Map<String, String> hmOrientMemberID = CF.getOrientMemberID(con);
//			if(getEmpID()!=null && !getEmpID().equals("")){
//				
//				pst=con.prepareStatement("select emp_image,emp_per_id from employee_personal_details ");
//				rs=pst.executeQuery();
//				Map<String,String> empImageMap=new HashMap<String,String>();
//				while(rs.next()){
//					empImageMap.put(rs.getString("emp_per_id"),rs.getString("emp_image"));
//				}
//				rs.close();
//				pst.close();
//				
//				pst=con.prepareStatement("select wlocation_id,grade_id from employee_official_details where emp_id=?");
//				pst.setInt(1, uF.parseToInt(getEmpID().trim()));
////				System.out.println("pst=====>"+pst);
//				rs=pst.executeQuery();
//				String empLocation="";
//				String gradeid="";
//				while(rs.next()){
//					empLocation=rs.getString("wlocation_id");
//					gradeid=rs.getString("grade_id");
//				}
//				rs.close();
//				pst.close();
//					
//				pst=con.prepareStatement("select emp_id,user_id,user_type_id from appraisal_question_answer where appraisal_id=? and emp_id=? and appraisal_freq_id = ?" +
//						" and appraisal_question_answer_id in(select max(appraisal_question_answer_id) from appraisal_question_answer " +
//						" where appraisal_id=? and emp_id=? and appraisal_freq_id = ? group by user_type_id)");
//				pst.setInt(1, uF.parseToInt(id));
//				pst.setInt(2, uF.parseToInt(getEmpID().trim()));
//				pst.setInt(3, uF.parseToInt(getAppFreqId().trim()));
//				pst.setInt(4, uF.parseToInt(id));
//				pst.setInt(5, uF.parseToInt(getEmpID().trim()));
//				pst.setInt(6, uF.parseToInt(getAppFreqId().trim()));
////				System.out.println("pst=====>"+pst);
//				rs=pst.executeQuery();
//				Map<String,String> hmCheckAppraisal=new HashMap<String, String>();
//				while(rs.next()){
//					String key=rs.getString("emp_id")+"_"+rs.getString("user_id")+"_"+rs.getString("user_type_id")+"_"+rs.getString("appraisal_freq_id");
//					hmCheckAppraisal.put(key, rs.getString("emp_id"));
//				}
//				rs.close();
//				pst.close();
//				
//				
//				StringBuilder sbMemList=new StringBuilder();
//					
//				//self
//				if(memberList!= null && hmOrientMemberID.get("Self")!= null && memberList.contains(hmOrientMemberID.get("Self"))) {
//					if(hmCheckAppraisal!=null && hmCheckAppraisal.containsKey(getEmpID().trim()+"_"+getEmpID().trim()+"_3")){
//						sbMemList.append("<span style=\"margin-right: 4px;\"><img class=\"lazy img-circle\" src=\"userImages/avatar_photo.png\"  style=\"border-bottom:2px solid green\"  data-original=\"userImages/"+uF.showData(empImageMap.get(getEmpID()), "avatar_photo.png")+"\" border=\"0\" height=\"16px\" " +
//											"width=\"16px;\" title=\""+hmEmpName.get(getEmpID())+"(Role-SELF)\"/></span>");
//					}else{
//						sbMemList.append("<a href=\"javascript:void(0)\" style=\"margin-right: 4px;\" onclick=\"getEmpProfile('"+getEmpID()+"','"+hmEmpName.get(getEmpID())+"')\" >" +
//								"<img class=\"lazy img-circle\" src=\"userImages/avatar_photo.png\"  style=\"border-bottom:2px solid red\"  data-original=\"userImages/"+uF.showData(empImageMap.get(getEmpID()), "avatar_photo.png")+"\" border=\"0\" height=\"16px\" " +
//										"width=\"16px;\" title=\""+hmEmpName.get(getEmpID())+"(Role-SELF)\"/></a>");
//					}
//				}
//				
//				//HR
//				if(memberList!= null && hmOrientMemberID.get("HR")!= null && memberList.contains(hmOrientMemberID.get("HR"))) {
//					pst = con.prepareStatement("select eod.emp_id from employee_official_details eod,user_details ud " +
//							" join employee_personal_details on emp_per_id=emp_id and is_alive=true " +
//							" where eod.wlocation_id=? and eod.emp_id=ud.emp_id and ud.usertype_id=7");
//					pst.setInt(1,uF.parseToInt(hmEmpWlocationMap.get(getEmpID().trim())));
////					System.out.println("pst=====>"+pst);
//					rs = pst.executeQuery();
//					while (rs.next()) {
//						
//						if(hmCheckAppraisal!=null && hmCheckAppraisal.containsKey(getEmpID().trim()+"_"+rs.getString("emp_id")+"_7")){
//							sbMemList.append("<span style=\"margin-right: 4px;\"><img class=\"lazy img-circle\" src=\"userImages/avatar_photo.png\" style=\"border-bottom:2px solid green\" data-original=\"userImages/"+uF.showData(empImageMap.get(rs.getString("emp_id")), "avatar_photo.png")+"\" border=\"0\" height=\"16px\" " +
//										"width=\"16px;\" title=\""+hmEmpName.get(rs.getString("emp_id"))+"(Role-HR)\"/></span>");
//						}else{ 
//							sbMemList.append("<a href=\"javascript:void(0)\" style=\"margin-right: 4px;\" onclick=\"getEmpProfile('"+rs.getString("emp_id")+"','"+hmEmpName.get(rs.getString("emp_id"))+"')\" >" +
//									"<img class=\"lazy img-circle\" src=\"userImages/avatar_photo.png\" style=\"border-bottom:2px solid red\" data-original=\"userImages/"+uF.showData(empImageMap.get(rs.getString("emp_id")), "avatar_photo.png")+"\" border=\"0\" height=\"16px\" " +
//											"width=\"16px;\" title=\""+hmEmpName.get(rs.getString("emp_id"))+"(Role-HR)\"/></a>");
//						}
//						
//					}
//					rs.close();
//					pst.close();
//				}
//							
//				//manager
//				if(hmOrientMemberID.get("Manager") != null && memberList!= null && memberList.contains(hmOrientMemberID.get("Manager"))) {
//					pst = con.prepareStatement("select supervisor_emp_id from employee_official_details eod where eod.emp_id=? and supervisor_emp_id>0");
//					pst.setInt(1,uF.parseToInt(getEmpID().trim()));
////					System.out.println("pst=====>"+pst);
//					rs = pst.executeQuery();
//					while (rs.next()) {
////						String empName = rs.getString("emp_fname")+"_"+rs.getString("emp_lname");
//						if(hmCheckAppraisal!=null && hmCheckAppraisal.containsKey(getEmpID().trim()+"_"+rs.getString("supervisor_emp_id")+"_2")){
//							sbMemList.append("<span style=\"margin-right: 4px;\"><img class=\"lazy img-circle\" src=\"userImages/avatar_photo.png\"  style=\"border-bottom:2px solid green\"  data-original=\"userImages/"+uF.showData(empImageMap.get(rs.getString("supervisor_emp_id")), "avatar_photo.png")+"\" border=\"0\" height=\"16px\" " +
//								"width=\"16px;\" title=\""+hmEmpName.get(rs.getString("supervisor_emp_id"))+"(Role-Manager)\"/></span>");
//						}else{
//							sbMemList.append("<a href=\"javascript:void(0)\" style=\"margin-right: 4px;\" onclick=\"getEmpProfile('"+rs.getString("supervisor_emp_id")+"','"+hmEmpName.get(rs.getString("supervisor_emp_id"))+"')\" >" +
//								"<img class=\"lazy img-circle\" src=\"userImages/avatar_photo.png\"  style=\"border-bottom:2px solid red\"  data-original=\"userImages/"+uF.showData(empImageMap.get(rs.getString("supervisor_emp_id")), "avatar_photo.png")+"\" border=\"0\" height=\"16px\" " +
//								"width=\"16px;\" title=\""+hmEmpName.get(rs.getString("supervisor_emp_id"))+"(Role-Manager)\"/></a>");
//						}
//					}
//					rs.close();
//					pst.close();
//				}
//				
//				//peer
//				if (hmOrientMemberID.get("Peer") != null && memberList!= null && memberList.contains(hmOrientMemberID.get("Peer"))) {
//					pst = con.prepareStatement("select emp_id from employee_official_details where wlocation_id=? and grade_id=?");
//					pst.setInt(1,uF.parseToInt(empLocation));
//					pst.setInt(2,uF.parseToInt(gradeid));
////					System.out.println("pst=====>"+pst);
//					rs = pst.executeQuery();
//					while (rs.next()) {
//						if(hmCheckAppraisal!=null && hmCheckAppraisal.containsKey(getEmpID().trim()+"_"+rs.getString("emp_id")+"_4")){
//							sbMemList.append("<span style=\"margin-right: 4px;\"><img class=\"lazy img-circle\" src=\"userImages/avatar_photo.png\"  style=\"border-bottom:2px solid green\"  " +
//								"data-original=\"userImages/"+uF.showData(empImageMap.get(rs.getString("emp_id")), "avatar_photo.png")+"\" border=\"0\" height=\"16px\" " +
//								"width=\"16px;\" title=\""+hmEmpName.get(rs.getString("emp_id"))+"(Role-Peer)\"/></span>");
//						}else{
//							sbMemList.append("<a href=\"javascript:void(0)\" style=\"margin-right: 4px;\" onclick=\"getEmpProfile('"+rs.getString("emp_id")+"','"+hmEmpName.get(rs.getString("emp_id"))+"')\" >" +
//								"<img class=\"lazy img-circle\" src=\"userImages/avatar_photo.png\"  style=\"border-bottom:2px solid red\"  " +
//								"data-original=\"userImages/"+uF.showData(empImageMap.get(rs.getString("emp_id")), "avatar_photo.png")+"\" border=\"0\" height=\"16px\" " +
//								"width=\"16px;\" title=\""+hmEmpName.get(rs.getString("emp_id"))+"(Role-Peer)\"/></a>");
//						}
//					}
//					rs.close();
//					pst.close();
//				}
//				
//				//CEO
//				if(hmOrientMemberID.get("CEO") != null && memberList!= null && memberList.contains(hmOrientMemberID.get("CEO"))) {
//					pst = con.prepareStatement("select eod.emp_id from employee_official_details eod,user_details ud join employee_personal_details on " +
//							"emp_per_id=emp_id and is_alive=true where eod.wlocation_id=? and eod.emp_id=ud.emp_id and ud.usertype_id=5");
//					pst.setInt(1,uF.parseToInt(hmEmpWlocationMap.get(getEmpID().trim())));
//					System.out.println("pst=====>"+pst);
//					rs = pst.executeQuery();
//					while (rs.next()) {
//						if(hmCheckAppraisal!=null && hmCheckAppraisal.containsKey(getEmpID().trim()+"_"+rs.getString("emp_id")+"_5")){
//							sbMemList.append("<span style=\"margin-right: 4px;\"><img class=\"lazy img-circle\" src=\"userImages/avatar_photo.png\" style=\"border-bottom:2px solid green\" data-original=\"userImages/"+uF.showData(empImageMap.get(rs.getString("emp_id")), "avatar_photo.png")+"\" border=\"0\" height=\"16px\" " +
//										"width=\"16px;\" title=\""+hmEmpName.get(rs.getString("emp_id"))+"(Role-CEO)\"/></span>");
//						}else{ 
//							sbMemList.append("<a href=\"javascript:void(0)\" style=\"margin-right: 4px;\" onclick=\"getEmpProfile('"+rs.getString("emp_id")+"','"+hmEmpName.get(rs.getString("emp_id"))+"')\" >" +
//									"<img class=\"lazy img-circle\" src=\"userImages/avatar_photo.png\" style=\"border-bottom:2px solid red\" data-original=\"userImages/"+uF.showData(empImageMap.get(rs.getString("emp_id")), "avatar_photo.png")+"\" border=\"0\" height=\"16px\" " +
//											"width=\"16px;\" title=\""+hmEmpName.get(rs.getString("emp_id"))+"(Role-CEO)\"/></a>");
//						}
//						
//					}
//					rs.close();
//					pst.close();
//				}
//				
//				//CEO
//				if(hmOrientMemberID.get("HOD") != null && memberList!= null && memberList.contains(hmOrientMemberID.get("HOD"))) {
//					pst = con.prepareStatement("select hod_emp_id from employee_official_details eod where eod.emp_id=? and hod_emp_id>0");
//					pst.setInt(1,uF.parseToInt(getEmpID().trim()));
//	//				System.out.println("pst=====>"+pst);
//					rs = pst.executeQuery();
//					while (rs.next()) {
//						if(hmCheckAppraisal!=null && hmCheckAppraisal.containsKey(getEmpID().trim()+"_"+rs.getString("hod_emp_id")+"_13")){
//							sbMemList.append("<span style=\"margin-right: 4px;\"><img class=\"lazy img-circle\" src=\"userImages/avatar_photo.png\"  style=\"border-bottom:2px solid green\"  data-original=\"userImages/"+uF.showData(empImageMap.get(rs.getString("hod_emp_id")), "avatar_photo.png")+"\" border=\"0\" height=\"16px\" " +
//											"width=\"16px;\" title=\""+hmEmpName.get(rs.getString("hod_emp_id"))+"(Role-HOD)\"/></span>");
//						}else{
//							sbMemList.append("<a href=\"javascript:void(0)\" style=\"margin-right: 4px;\" onclick=\"getEmpProfile('"+rs.getString("hod_emp_id")+"','"+hmEmpName.get(rs.getString("hod_emp_id"))+"')\" >" +
//									"<img class=\"lazy img-circle\" src=\"userImages/avatar_photo.png\"  style=\"border-bottom:2px solid red\"  data-original=\"userImages/"+uF.showData(empImageMap.get(rs.getString("hod_emp_id")), "avatar_photo.png")+"\" border=\"0\" height=\"16px\" " +
//											"width=\"16px;\" title=\""+hmEmpName.get(rs.getString("hod_emp_id"))+"(Role-HOD)\"/></a>");
//						}
//					}
//					rs.close();
//					pst.close();
//				}
//				hmMemberMP.put(getEmpID().trim(), sbMemList.toString());
//								
//			}
//			request.setAttribute("hmMemberMP", hmMemberMP);
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			db.closeResultSet(rs);
//			db.closeStatements(pst);
//			db.closeConnection(con);
//		}
//	}

	
	public String getEmpID() {
		return empID;
	}

	public void setEmpID(String empID) {
		this.empID = empID;
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
