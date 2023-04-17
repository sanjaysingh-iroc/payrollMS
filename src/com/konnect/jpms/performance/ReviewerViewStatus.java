package com.konnect.jpms.performance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Element;
import com.konnect.jpms.export.DataStyle;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;

public class ReviewerViewStatus implements ServletRequestAware, IStatements {

	
	HttpSession session;
	String strSessionEmpId;
	String strSessionUserType;
	String strBaseUserType;
	String strUserTypeId;
	CommonFunctions CF; 
	private String id;

	private String alertStatus;
	private String alert_type;
	
	private String type;
	private String strMessage;
	
	private String appFreqId;
	private String empID;
	
	private String fromPage;
	private String dataType;
	private String operation;
	private String userType;
	
	public String execute() {
		
		session = request.getSession(); 
		strSessionEmpId = (String) session.getAttribute(EMPID);
		strSessionUserType = (String) session.getAttribute(USERTYPE);
		strBaseUserType = (String) session.getAttribute(BASEUSERTYPE);
		strUserTypeId = (String) session.getAttribute(USERTYPEID);

		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return "login";
		UtilityFunctions uF = new UtilityFunctions();
		request.setAttribute("DOC_RETRIVE_LOCATION", CF.getStrDocRetriveLocation());
		
		getOrientationMember();
		request.setAttribute(PAGE, "/jsp/performance/ReviewerViewStatus.jsp");
		request.setAttribute(TITLE, "Review Status");

		if(getOperation() != null && getOperation().equalsIgnoreCase("ApproveFeedbak")) {
			approveAverageFeedbackOfReviewer();
			return "status";
		}
		
		checkReviewerFeedback();
		getAppraisalStatus(uF);
		getAppraisalStatusReport(uF);
		getRemarks();
		getEmpSupervisor();
		getOrientationCount(uF);
		//getSingleOpenWithoutMarksQueCount(uF);
		getSingleOpenWithoutMarksQueReadUnreadCount(uF);
		if(getFromPage() != null && getFromPage().equalsIgnoreCase("AD")) {
			return "success";
		}
		return LOAD;
	}
	
	
	private void approveAverageFeedbackOfReviewer() {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			boolean flag = false;
			pst = con.prepareStatement("select * from appraisal_question_answer where emp_id=? and appraisal_id=? and appraisal_freq_id=? " + //  and user_type_id=? and user_id=?
				"and reviewer_or_appraiser=0 order by section_id,appraisal_level_id,appraisal_question_details_id ");
			pst.setInt(1, uF.parseToInt(getEmpID()));
			pst.setInt(2, uF.parseToInt(getId()));
			pst.setInt(3, uF.parseToInt(getAppFreqId()));
//			System.out.println("pst ===>> " + pst);
			Map<String, List<String>> hmAppQueData = new HashMap<String, List<String>>();
			Map<String, List<String>> hmQueIdwiseData = new HashMap<String, List<String>>();
			Map<String, List<List<String>>> hmUserQueIds = new HashMap<String, List<List<String>>>();
			rs = pst.executeQuery();
			while (rs.next()) {
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("appraisal_question_details_id")); //0
				innerList.add(rs.getString("question_id")); //1
				innerList.add(rs.getString("weightage")); //2
				innerList.add(rs.getString("answer")); //3
				innerList.add(rs.getString("marks")); //4
				innerList.add(rs.getString("remark")); //5
				innerList.add(rs.getString("answers_comment")); //6
				innerList.add(rs.getString("read_status")); //7
				innerList.add(rs.getString("read_status_comment")); //8
				innerList.add(rs.getString("section_id")); //9
				innerList.add(rs.getString("appraisal_level_id")); //10
				innerList.add(rs.getString("appraisal_attribute")); //11
				innerList.add(rs.getString("other_id")); //12
				innerList.add(rs.getString("scorecard_id")); //13
				innerList.add(rs.getString("reviewer_marks")); //14
				innerList.add(rs.getString("reviewer_id")); //15
				
				hmAppQueData.put(rs.getString("user_type_id")+"_"+rs.getString("user_id")+"_"+rs.getString("appraisal_question_details_id"), innerList);
				hmQueIdwiseData.put(rs.getString("appraisal_question_details_id"), innerList);
				
				List<List<String>> outList = hmUserQueIds.get(rs.getString("appraisal_question_details_id"));
				if(outList == null) outList = new ArrayList<List<String>>();
				
				List<String> innList = new ArrayList<String>();
				innList.add(rs.getString("user_type_id"));
				innList.add(rs.getString("user_id"));
				outList.add(innList);
				
				hmUserQueIds.put(rs.getString("appraisal_question_details_id"), outList);
			}
			rs.close();
			pst.close();
			
			Map<String, String> hmQueAvgMarksForReviewer = new HashMap<String, String>();
			
			Iterator<String> it = hmUserQueIds.keySet().iterator();
			while (it.hasNext()) {
				String appQueDetailId = it.next();
				List<List<String>> outList = hmUserQueIds.get(appQueDetailId);
				double dblTotMarks = 0.0;
				double dblTotWeightage = 0.0;
				int dblTotUserCnt = 0;
				double dblAvgMarks = 0.0;
				for (int i=0; outList != null && i<outList.size(); i++) {
					List<String> innList = outList.get(i);
//					System.out.println(" ===>> " + innList.get(0)+"_"+innList.get(1)+"_"+appQueDetailId);
					List<String> innerList = hmAppQueData.get(innList.get(0)+"_"+innList.get(1)+"_"+appQueDetailId);
//					System.out.println("innerList ===>> " + innerList);
					if(innerList != null && innerList.size()>0) {
						if(uF.parseToInt(innerList.get(15))>0) {
							dblTotMarks += uF.parseToDouble(innerList.get(14));
						} else {
							dblTotMarks += uF.parseToDouble(innerList.get(4));
						}
						dblTotWeightage += uF.parseToDouble(innerList.get(2));
						dblTotUserCnt++;
					}
				}
				if(dblTotUserCnt>0) {
					dblAvgMarks = (dblTotMarks) /dblTotUserCnt;
				}
				hmQueAvgMarksForReviewer.put(appQueDetailId+"_AVG_MARKS", dblAvgMarks+"");
			}
			
			Iterator<String> it1 = hmQueIdwiseData.keySet().iterator();
			while(it1.hasNext()) {
				String appQueAnsId = it1.next();
				List<String> innerList = hmQueIdwiseData.get(appQueAnsId);
				
				pst = con.prepareStatement("insert into appraisal_question_answer(emp_id,answer,appraisal_id,question_id,"
					+ "user_id,user_type_id,attempted_on,weightage,marks,appraisal_level_id,scorecard_id,appraisal_attribute,"
					+ "remark,other_id,appraisal_question_details_id,section_id,answers_comment,appraisal_freq_id,reviewer_or_appraiser" +
					") values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?)");
				pst.setInt(1, uF.parseToInt(getEmpID()));
				pst.setString(2, ""); //givenAnswer
				pst.setInt(3, uF.parseToInt(id));
				pst.setInt(4, uF.parseToInt(innerList.get(1)));
				pst.setInt(5, uF.parseToInt(strSessionEmpId));
				pst.setInt(6, uF.parseToInt(getUserType()));
				pst.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setDouble(8, uF.parseToDouble(innerList.get(2)));
				pst.setDouble(9, uF.parseToDouble(hmQueAvgMarksForReviewer.get(appQueAnsId+"_AVG_MARKS")));
				pst.setInt(10, uF.parseToInt(innerList.get(10)));
				pst.setInt(11, uF.parseToInt(innerList.get(13)));
				pst.setInt(12, uF.parseToInt(innerList.get(11)));
				pst.setString(13, ""); //remark
				pst.setInt(14, uF.parseToInt(innerList.get(12)));
				pst.setInt(15, uF.parseToInt(appQueAnsId));
				pst.setInt(16, uF.parseToInt(innerList.get(9)));
				pst.setString(17, ""); //ansComment
				pst.setInt(18, uF.parseToInt(getAppFreqId()));
				pst.setInt(19, 1);
//				System.out.println("pst2 =====>"+ pst);
				pst.executeUpdate();
				pst.close();
				
			}
			request.setAttribute("STATUS_MSG", SUCCESSM+"Feedback approved successfully."+END);
			
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("STATUS_MSG", ERRORM+"Feedback not approved, please try again."+END);
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}


	private void checkReviewerFeedback() {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			boolean flag = false;
			pst = con.prepareStatement("select * from appraisal_question_answer where emp_id=? and appraisal_id=? and appraisal_freq_id=? " + //  and user_type_id=? and user_id=?
				"and reviewer_or_appraiser=?");
			pst.setInt(1, uF.parseToInt(getEmpID()));
			pst.setInt(2, uF.parseToInt(getId()));
//			pst.setInt(3, uF.parseToInt(getMemberId()));
			pst.setInt(3, uF.parseToInt(getAppFreqId()));
			pst.setInt(4, 1);
//			System.out.println("pst ===>> " + pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				flag = true;
			}
			rs.close();
			pst.close();
//			System.out.println("flag ===>> " + flag);
			request.setAttribute("flag", flag);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
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
				"appraisal_question_details aqd where aqa.appraisal_id=? and aqa.appraisal_freq_id=? and aqa.emp_id=? and aqa.appraisal_id = aqd.appraisal_id and " +
				"aqa.question_id = aqd.question_id and aqd.answer_type = 12 group by aqa.read_status,aqa.user_type_id,aqa.emp_id");
			pst.setInt(1, uF.parseToInt(getId()));
			pst.setInt(2, uF.parseToInt(getAppFreqId()));
			pst.setInt(3, uF.parseToInt(getEmpID()));
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
		Map<String, String> orientationMemberMp = getOrientationMember();
		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmEmpWlocationMap=CF.getEmpWlocationMap(con);
			Map<String,String> hmEmpName=CF.getEmpNameMap(con, null,null);
			Map<String, String> hmEmpCode = CF.getEmpCodeMap(con);
			Map<String, String> hmEmpCodeDesig = CF.getEmpDesigMap(con); 
			request.setAttribute("hmEmpName", hmEmpName);
			request.setAttribute("hmEmpCode", hmEmpCode);
			request.setAttribute("hmEmpCodeDesig", hmEmpCodeDesig);
			
			Map<String,String> hmMemberMP=new HashMap<String, String>();			
			
			pst = con.prepareStatement("select * from appraisal_details where appraisal_details_id =?");
			pst.setInt(1, uF.parseToInt(id));
			rs = pst.executeQuery();

			//Map<String, String> hmOrientationCount = new HashMap<String, String>();
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
//				self_ids = rs.getString("self_ids");
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
			
			self_ids = ","+getEmpID()+",";
//			System.out.println("oriented_type ===>> " + oriented_type);
			
			List<String> memberList = CF.getOrientationMemberDetails(con,uF.parseToInt(oriented_type));
//			System.out.println("memberList ===>> " + memberList);
			
			Map<String, String> hmOrientMemberID = CF.getOrientMemberID(con);
			List<String> hrList = new ArrayList<String>();
			List<String> managerList = new ArrayList<String>();
			List<String> peerList = new ArrayList<String>();
			List<String> ceoList = new ArrayList<String>();
			List<String> hodList = new ArrayList<String>();
			
			if(hr_ids != null && !hr_ids.equals("")) {
				hrList = Arrays.asList(hr_ids.split(","));
			}
			
			if(supervisor_id != null && !supervisor_id.equals("")) {
				managerList = Arrays.asList(supervisor_id.split(","));
			}
			
			if(peer_ids != null && !peer_ids.equals("")) {
				peerList = Arrays.asList(peer_ids.split(","));
			}
			
			if(ceo_ids != null && !ceo_ids.equals("")) {
				ceoList = Arrays.asList(ceo_ids.split(","));
			}
			if(hod_ids != null && !hod_ids.equals("")) {
				hodList = Arrays.asList(hod_ids.split(","));
			}
			
//			System.out.println("hrList ===>> " + hrList);
//			System.out.println("managerList ===>> " + managerList);
//			System.out.println("peerList ===>> " + peerList);
//			System.out.println("ceoList ===>> " + ceoList);
//			System.out.println("hodList ===>> " + hodList);
			
			//if(oriented_type != null && !oriented_type.equals("5")) {
			if(self_ids!=null && !self_ids.equals("")) {
				
				pst=con.prepareStatement("select emp_image,emp_per_id from employee_personal_details ");
				rs=pst.executeQuery();
				Map<String,String> empImageMap=new HashMap<String,String>();
				while(rs.next()) {
					empImageMap.put(rs.getString("emp_per_id"), rs.getString("emp_image"));
				}
				rs.close();
				pst.close();
				
				getEmpWlocation(self_ids);
				self_ids=self_ids.substring(1,self_ids.length()-1);
				List<String> selfList = Arrays.asList(self_ids.split(","));
				
				for(int j=0;selfList!=null && !selfList.isEmpty() && j<selfList.size();j++) {
					pst=con.prepareStatement("select wlocation_id,grade_id from employee_official_details where emp_id=?");
					pst.setInt(1, uF.parseToInt(selfList.get(j).trim()));
					rs=pst.executeQuery();
					String empLocation="";
					String gradeid="";
					while(rs.next()) {
						empLocation=rs.getString("wlocation_id");
						gradeid=rs.getString("grade_id");
					}
					rs.close();
					pst.close();
					
					pst = con.prepareStatement("select emp_id,user_id,user_type_id from appraisal_question_answer where appraisal_id=? and emp_id=? " +
						" and appraisal_freq_id=? and appraisal_question_answer_id in(select max(appraisal_question_answer_id) from appraisal_question_answer " +
						" where appraisal_id=? and emp_id=? and appraisal_freq_id= ? group by user_type_id,user_id)");
					pst.setInt(1, uF.parseToInt(getId()));
					pst.setInt(2, uF.parseToInt(selfList.get(j).trim()));
					pst.setInt(3, uF.parseToInt(getAppFreqId()));
					pst.setInt(4, uF.parseToInt(getId()));
					pst.setInt(5, uF.parseToInt(selfList.get(j).trim()));
					pst.setInt(6, uF.parseToInt(getAppFreqId()));
					rs=pst.executeQuery();
					Map<String, String> hmCheckAppraisal = new HashMap<String, String>();
					Map<String, List<String>> hmCheckHR = new HashMap<String, List<String>>();
					Map<String, List<String>> hmCheckMgr = new HashMap<String, List<String>>();
					Map<String, List<String>> hmCheckCeo = new HashMap<String, List<String>>();
					Map<String, List<String>> hmCheckHod = new HashMap<String, List<String>>();
					while(rs.next()) {
						String key=rs.getString("emp_id")+"_"+rs.getString("user_id")+"_"+rs.getString("user_type_id");
						hmCheckAppraisal.put(key, rs.getString("emp_id"));
						List<String> innerList = new ArrayList<String>();
						if(rs.getString("user_type_id").equals("7")) {
							innerList = hmCheckHR.get(HRMANAGER);
							if(innerList == null) innerList = new ArrayList<String>();
							if(!innerList.contains(rs.getString("user_id"))) {
								innerList.add(rs.getString("user_id"));
							}
							hmCheckHR.put(HRMANAGER, innerList);	
						}else if(rs.getString("user_type_id").equals("2")) {
							innerList = hmCheckMgr.get(MANAGER);
							if(innerList == null) innerList = new ArrayList<String>();
							if(!innerList.contains(rs.getString("user_id"))) {
								innerList.add(rs.getString("user_id"));
							}
							hmCheckMgr.put(MANAGER, innerList);
						} else if(rs.getString("user_type_id").equals("5")) {
							innerList = hmCheckCeo.get(CEO);
							if(innerList == null) innerList = new ArrayList<String>();
							if(!innerList.contains(rs.getString("user_id"))) {
								innerList.add(rs.getString("user_id"));
							}
							hmCheckCeo.put(CEO, innerList);
//							hmCheckCeo.put(CEO, rs.getString("user_id"));
						} else if(rs.getString("user_type_id").equals("13")) {
							innerList = hmCheckHod.get(HOD);
							if(innerList == null) innerList = new ArrayList<String>();
							if(!innerList.contains(rs.getString("user_id"))) {
								innerList.add(rs.getString("user_id"));
							}
							hmCheckHod.put(HOD, innerList);
//							hmCheckHod.put(HOD, rs.getString("user_id"));
						}
					}
					rs.close();
					pst.close();
					
//					System.out.println("hmCheckAppraisal ===>> " + hmCheckAppraisal);
//					System.out.println("hmCheckHR ===>> " + hmCheckHR);
//					System.out.println("hmCheckMgr ===>> " + hmCheckMgr);
//					System.out.println("hmCheckCeo ===>> " + hmCheckCeo);
//					System.out.println("hmCheckHod ===>> " + hmCheckHod);
					
					StringBuilder sbMemList=new StringBuilder();
					int cnt=0;
					
					//self
					if(hmOrientMemberID.get("Self") != null && memberList!=null && memberList.contains(hmOrientMemberID.get("Self"))) {
						String brdrColor = "red";
						if(hmCheckAppraisal!=null && hmCheckAppraisal.containsKey(selfList.get(j).trim()+"_"+selfList.get(j).trim()+"_3")) {
							brdrColor = "green";
						}
						sbMemList.append("<a href=\"javascript:void(0)\" style=\"margin-right: 4px;\" onclick=\"getEmpProfile('"+selfList.get(j)+"','"+hmEmpName.get(selfList.get(j))+"')\" >" +
							"<img class=\"lazy img-circle img-circle\" src=\"userImages/avatar_photo.png\" style=\"border-bottom:2px solid "+brdrColor+"\" ");
						sbMemList.append(" data-original=\""+CF.getStrDocRetriveLocation()+I_PEOPLE+"/"+I_IMAGE+"/"+selfList.get(j).trim()+"/"+I_16x16+"/"+empImageMap.get(selfList.get(j).trim())+"\" " +
							"border=\"0\" height=\"16px\" width=\"16px;\" title=\""+hmEmpName.get(selfList.get(j))+"(Role-SELF)\"/></a>");
					}
					
					//HRMANAGER
					if(hmOrientMemberID.get("HR") != null && memberList!=null && memberList.contains(hmOrientMemberID.get("HR"))) {
						if(hmCheckHR.get(HRMANAGER)!=null) {
//							System.out.println("hmCheckHR.get(HRMANAGER) ===>> " + hmCheckHR.get(HRMANAGER));
							List<String> innerList = hmCheckHR.get(HRMANAGER);
							for(int i=0; i<innerList.size(); i++) {
								cnt++;
								sbMemList.append("<a href=\"javascript:void(0)\" style=\"margin-right: 4px;\" onclick=\"getEmpProfile('"+innerList.get(i)+"','"+hmEmpName.get(innerList.get(i))+"')\" ><img class=\"lazy img-circle img-circle\" src=\"userImages/avatar_photo.png\" style=\"border-bottom:2px solid green\" data-original=\""+CF.getStrDocRetriveLocation()+I_PEOPLE+"/"+I_IMAGE+"/"+innerList.get(i)+"/"+I_16x16+"/"+empImageMap.get(innerList.get(i))+"\" border=\"0\" height=\"16px\" " +
									"width=\"16px;\" title=\""+hmEmpName.get(innerList.get(i))+"(Role-HR)\"/></a>");
							}
						} else {
								for (int i = 0; hrList != null && !hrList.isEmpty() && i < hrList.size(); i++) {
//									System.out.println("hrList.get(i).trim() ===>> " + hrList.get(i).trim());
									if(!hrList.get(i).trim().equals("")) {
										if(cnt>7) {
											break;
										} else {
											cnt++;
										}
										String brdrColor = "red";
										if(hmCheckAppraisal!=null && hmCheckAppraisal.containsKey(selfList.get(j).trim()+"_"+hrList.get(i).trim()+"_7")) {
											brdrColor = "green";
										}
										sbMemList.append("<a href=\"javascript:void(0)\" style=\"margin-right: 4px;\" onclick=\"getEmpProfile('"+hrList.get(i).trim()+"','"+hmEmpName.get(hrList.get(i).trim())+"')\" >" +
											"<img class=\"lazy img-circle\" src=\"userImages/avatar_photo.png\" style=\"border-bottom:2px solid "+brdrColor+"\" data-original=\""+CF.getStrDocRetriveLocation()+I_PEOPLE+"/"+I_IMAGE+"/"+hrList.get(i).trim()+"/"+I_16x16+"/"+empImageMap.get(hrList.get(i).trim())+"\" border=\"0\" height=\"16px\" " +
											"width=\"16px;\" title=\""+hmEmpName.get(hrList.get(i).trim())+"(Role-HR)\"/></a>");
									}
								}
//							}
						
						}
					}
					
					//Manager
					if(hmOrientMemberID.get("Manager") != null && memberList!=null && memberList.contains(hmOrientMemberID.get("Manager"))) {
						if(hmCheckMgr.get(MANAGER)!=null) {
							List<String> innerList = hmCheckMgr.get(MANAGER);
							for(int i=0; i<innerList.size(); i++) {
								cnt++;
								sbMemList.append("<a href=\"javascript:void(0)\" style=\"margin-right: 4px;\" onclick=\"getEmpProfile('"+innerList.get(i)+"','"+hmEmpName.get(innerList.get(i))+"')\" >" +
									"<img class=\"lazy img-circle\" src=\"userImages/avatar_photo.png\"  style=\"border-bottom:2px solid green\"  data-original=\"userImages/"+CF.getStrDocRetriveLocation()+I_PEOPLE+"/"+I_IMAGE+"/"+innerList.get(i)+"/"+I_16x16+"/"+empImageMap.get(innerList.get(i))+"\" border=\"0\" height=\"16px\" " +
									"width=\"16px;\" title=\""+hmEmpName.get(innerList.get(i))+"(Role-Manager)\"/></a>");
							}
						} else {
							for (int i = 0; managerList != null && !managerList.isEmpty() && i < managerList.size(); i++) {
								if(!managerList.get(i).trim().equals("")) {
									if(cnt>7) {
//										sbMemList.append("<a href=\"javascript:void(0);\" style=\"margin-right: 4px;\" onclick=\"seeEmpList('"+selfList.get(j)+"','"+id+"','"+getAppFreqId()+"');\" class=\"OR testa\">more..</a>");
										break;
									} else {
										cnt++;
									}
									String brdrColor = "red";
									if(hmCheckAppraisal!=null && hmCheckAppraisal.containsKey(selfList.get(j).trim()+"_"+managerList.get(i).trim()+"_2")) {
										brdrColor = "green";
									}
									sbMemList.append("<a href=\"javascript:void(0)\" style=\"margin-right: 4px;\" onclick=\"getEmpProfile('"+managerList.get(i).trim()+"','"+hmEmpName.get(managerList.get(i).trim())+"')\" >" +
										"<img class=\"lazy img-circle\" src=\"userImages/avatar_photo.png\"  style=\"border-bottom:2px solid "+brdrColor+"\"  data-original=\""+CF.getStrDocRetriveLocation()+I_PEOPLE+"/"+I_IMAGE+"/"+managerList.get(i).trim()+"/"+I_16x16+"/"+empImageMap.get(managerList.get(i).trim())+"\" border=\"0\" height=\"16px\" " +
										"width=\"16px;\" title=\""+hmEmpName.get(managerList.get(i).trim())+"(Role-Manager)\"/></a>");
								}
							}
						}
					}
										
					//peer
					if(hmOrientMemberID.get("Peer") != null && memberList!=null && memberList.contains(hmOrientMemberID.get("Peer"))) {
						for (int i = 0; peerList != null && !peerList.isEmpty() && i < peerList.size(); i++) {
							if(!peerList.get(i).trim().equals("") && uF.parseToInt(peerList.get(i).trim()) != uF.parseToInt(selfList.get(j).trim())) {
								if(cnt>7) {
//									sbMemList.append("<a href=\"javascript:void(0);\" style=\"margin-right: 4px;\" onclick=\"seeEmpList('"+selfList.get(j)+"','"+id+"','"+getAppFreqId()+"');\" class=\"OR testa\">more..</a>");
									break;
								} else {
									cnt++;
								}
								String brdrColor = "red";
								if(hmCheckAppraisal!=null && hmCheckAppraisal.containsKey(selfList.get(j).trim()+"_"+peerList.get(i).trim()+"_4")) {
									brdrColor = "green";
								}
								sbMemList.append("<a href=\"javascript:void(0)\" style=\"margin-right: 4px;\" onclick=\"getEmpProfile('"+peerList.get(i).trim()+"','"+hmEmpName.get(peerList.get(i).trim())+"')\" >" +
									"<img class=\"lazy img-circle\" src=\"userImages/avatar_photo.png\"  style=\"border-bottom:2px solid "+brdrColor+"\"  " +
									"data-original=\""+CF.getStrDocRetriveLocation()+I_PEOPLE+"/"+I_IMAGE+"/"+peerList.get(i).trim()+"/"+I_16x16+"/"+empImageMap.get(peerList.get(i).trim())+"\" border=\"0\" height=\"16px\" " +
									"width=\"16px;\" title=\""+hmEmpName.get(peerList.get(i).trim())+"(Role-Peer)\"/></a>");
							}
						}
//						}
					}
					
					//CEO
					if(hmOrientMemberID.get("CEO") != null && memberList!=null && memberList.contains(hmOrientMemberID.get("CEO"))) {
						if(hmCheckCeo.get(CEO)!=null) {
							List<String> innerList = hmCheckCeo.get(CEO);
							for(int i=0; i<innerList.size(); i++) {
								cnt++;
								sbMemList.append("<a href=\"javascript:void(0)\" style=\"margin-right: 4px;\" onclick=\"getEmpProfile('"+innerList.get(i)+"','"+hmEmpName.get(innerList.get(i))+"')\" >" +
									"<img class=\"lazy img-circle\" src=\"userImages/avatar_photo.png\"  style=\"border-bottom:2px solid green\"  data-original=\"userImages/"+CF.getStrDocRetriveLocation()+I_PEOPLE+"/"+I_IMAGE+"/"+innerList.get(i)+"/"+I_16x16+"/"+empImageMap.get(innerList.get(i))+"\" border=\"0\" height=\"16px\" " +
									"width=\"16px;\" title=\""+hmEmpName.get(innerList.get(i))+"(Role-CEO)\"/></a>");
							}
						} else {
							for (int i = 0; ceoList != null && !ceoList.isEmpty() && i < ceoList.size(); i++) {
								if(!ceoList.get(i).trim().equals("")) {
									if(cnt>7) {
//										sbMemList.append("<a href=\"javascript:void(0);\" style=\"margin-right: 4px;\" onclick=\"seeEmpList('"+selfList.get(j)+"','"+id+"','"+getAppFreqId()+"');\" class=\"OR testa\">more..</a>");
										break;
									} else {
										cnt++;
									}
									String brdrColor = "red";
									if(hmCheckAppraisal!=null && hmCheckAppraisal.containsKey(selfList.get(j).trim()+"_"+ceoList.get(i).trim()+"_5")) {
										brdrColor = "green";
									}
									sbMemList.append("<a href=\"javascript:void(0)\" style=\"margin-right: 4px;\" onclick=\"getEmpProfile('"+ceoList.get(i).trim()+"','"+hmEmpName.get(ceoList.get(i).trim())+"')\" >" +
										"<img class=\"lazy img-circle\" src=\"userImages/avatar_photo.png\"  style=\"border-bottom:2px solid "+brdrColor+"\"  data-original=\""+CF.getStrDocRetriveLocation()+I_PEOPLE+"/"+I_IMAGE+"/"+ceoList.get(i).trim()+"/"+I_16x16+"/"+empImageMap.get(ceoList.get(i).trim())+"\" border=\"0\" height=\"16px\" " +
										"width=\"16px;\" title=\""+hmEmpName.get(ceoList.get(i).trim())+"(Role-CEO)\"/></a>");
								}
							}
						}
					}
					
					//HOD
					if(hmOrientMemberID.get("HOD") != null && memberList!=null && memberList.contains(hmOrientMemberID.get("HOD"))) {
						if(hmCheckHod.get(HOD)!=null) {
							List<String> innerList = hmCheckHod.get(HOD);
							for(int i=0; i<innerList.size(); i++) {
								cnt++;
								sbMemList.append("<a href=\"javascript:void(0)\" style=\"margin-right: 4px;\" onclick=\"getEmpProfile('"+innerList.get(i)+"','"+hmEmpName.get(innerList.get(i))+"')\" >" +
									"<img class=\"lazy img-circle\" src=\"userImages/avatar_photo.png\"  style=\"border-bottom:2px solid green\"  data-original=\"userImages/"+CF.getStrDocRetriveLocation()+I_PEOPLE+"/"+I_IMAGE+"/"+innerList.get(i)+"/"+I_16x16+"/"+empImageMap.get(innerList.get(i))+"\" border=\"0\" height=\"16px\" " +
									"width=\"16px;\" title=\""+hmEmpName.get(innerList.get(i))+"(Role-HOD)\"/></a>");
							}
						} else {
							for (int i = 0; hodList != null && !hodList.isEmpty() && i < hodList.size(); i++) {
								if(!hodList.get(i).trim().equals("")) {
									if(cnt>7) {
//										sbMemList.append("<a href=\"javascript:void(0);\" style=\"margin-right: 4px;\" onclick=\"seeEmpList('"+selfList.get(j)+"','"+id+"','"+getAppFreqId()+"');\" class=\"OR testa\">more..</a>");
										break;
									} else {
										cnt++;
									}
									String brdrColor = "red";
									if(hmCheckAppraisal!=null && hmCheckAppraisal.containsKey(selfList.get(j).trim()+"_"+hodList.get(i).trim()+"_13")) {
										brdrColor = "green";
									}
									sbMemList.append("<a href=\"javascript:void(0)\" style=\"margin-right: 4px;\" onclick=\"getEmpProfile('"+hodList.get(i).trim()+"','"+hmEmpName.get(hodList.get(i).trim())+"')\" >" +
										"<img class=\"lazy img-circle\" src=\"userImages/avatar_photo.png\"  style=\"border-bottom:2px solid "+brdrColor+"\"  data-original=\""+CF.getStrDocRetriveLocation()+I_PEOPLE+"/"+I_IMAGE+"/"+hodList.get(i).trim()+"/"+I_16x16+"/"+empImageMap.get(hodList.get(i).trim())+"\" border=\"0\" height=\"16px\" " +
										"width=\"16px;\" title=\""+hmEmpName.get(hodList.get(i).trim())+"(Role-HOD)\"/></a>");
								}
							}
						}
					}
					
					//other
					if(hmOrientMemberID.get("Anyone") != null && memberList!=null && memberList.contains(hmOrientMemberID.get("Anyone"))) {
						String othrIds = "";
						if(other_ids != null && other_ids.trim().length() > 1) {
							othrIds = other_ids.substring(1, other_ids.trim().length()-1);
//						System.out.println("othrIds =====> "+othrIds);
							pst = con.prepareStatement("select emp_per_id from employee_personal_details where  emp_per_id in("+ othrIds + ")");
//							System.out.println("pst=====>"+pst);
							rs = pst.executeQuery();
							while (rs.next()) {
								if(cnt>7) {
									break;
								} else {
									cnt++;
								}
								String brdrColor = "red";
								if(hmCheckAppraisal!=null && hmCheckAppraisal.containsKey(selfList.get(j).trim()+"_"+rs.getString("emp_per_id")+"_10")) {
									brdrColor = "green";
								}		
								sbMemList.append("<a href=\"javascript:void(0)\" style=\"margin-right: 4px;\" onclick=\"getEmpProfile('"+rs.getString("emp_per_id")+"','"+hmEmpName.get(rs.getString("emp_per_id"))+"')\" >" +
									"<img class=\"lazy img-circle\" src=\"userImages/avatar_photo.png\"  style=\"border-bottom:2px solid "+brdrColor+"\" data-original=\""+CF.getStrDocRetriveLocation()+I_PEOPLE+"/"+I_IMAGE+"/"+rs.getString("emp_per_id")+"/"+I_16x16+"/"+empImageMap.get(rs.getString("emp_per_id"))+"\" border=\"0\" height=\"16px\" " +
									"width=\"16px;\" title=\""+hmEmpName.get(rs.getString("emp_per_id"))+"(Role-Anyone)\"/></a>");
//								}
							}
							rs.close();
							pst.close();
						}
					  }
					
					if(cnt>7) {
						sbMemList.append("<a href=\"javascript:void(0);\" style=\"margin-right: 4px;\" onclick=\"seeEmpList('"+selfList.get(j)+"','"+id+"','"+getAppFreqId()+"');\" class=\"OR testa\">more..</a>");
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
			
			
			pst = con.prepareStatement("Select sattlement_comment,if_approved,user_id, emp_fname,emp_mname, emp_lname,activity_ids,afs.emp_id,appraisal_id" +
				",_date,appraisal_freq_id from appraisal_final_sattlement afs,employee_personal_details epd where afs.user_id = epd.emp_per_id"
				+" and appraisal_id=? and appraisal_freq_id=? and afs.emp_id=?");
			pst.setInt(1, uF.parseToInt(getId()));
			pst.setInt(2, uF.parseToInt(getAppFreqId()));
			pst.setInt(3, uF.parseToInt(getEmpID()));
//			System.out.println("getRemarks pst==>"+pst);
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
				
				strApprovedBy = rs.getString("emp_fname") + strEmpMName+" " + rs.getString("emp_lname");
				hmRemark.put(rs.getString("appraisal_id")+"_"+rs.getString("emp_id"), strApprovedBy+" on "+ uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat()));
			}
			rs.close();
			pst.close();
			request.setAttribute("hmRemark", hmRemark);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst); 
			db.closeConnection(con);
		}
	}

	
	private void getAppraisalStatusReport(UtilityFunctions uF) {
		// TODO Auto-generated method stub
		List<List<DataStyle>> reportListExport = new ArrayList<List<DataStyle>>();
		List<DataStyle> alInnerExport = new ArrayList<DataStyle>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		Map<String, String> orientationMemberMp = getOrientationMember();
		con = db.makeConnection(con);
		String self_ids = null;
		String oriented_type = null;
		try {
			Map<String, String> hmAttributeThreshhold = new HashMap<String, String>();
			pst = con.prepareStatement("select attribute_id,threshhold from appraisal_attribute_level");
			rs = pst.executeQuery(); 
			while (rs.next()) {
				hmAttributeThreshhold.put(rs.getString("attribute_id"), rs.getString("threshhold"));
			}
			rs.close();
			pst.close();
			
			double dblTotalMarks1 = 0;
			double dblTotalWeightage1 = 0;
			double dblTotalAggregate1 = 0;
			Map<String, String> hmScoreAggregateMap = new HashMap<String, String>();
			pst = con.prepareStatement("select sum(marks) as marks, sum(weightage) as weightage, aqw.appraisal_attribute, aqw.emp_id from appraisal_question_answer aqw " +
				" where aqw.appraisal_id=? and aqw.appraisal_freq_id=? and aqw.emp_id=? group by aqw.appraisal_attribute,aqw.emp_id");
			pst.setInt(1, uF.parseToInt(id));
			pst.setInt(2, uF.parseToInt(getAppFreqId()));
			pst.setInt(3, uF.parseToInt(getEmpID()));
			rs = pst.executeQuery();
			List<String> attribIdList = new ArrayList<String>();
			while (rs.next()) {

				dblTotalMarks1 = uF.parseToDouble(rs.getString("marks"));
				dblTotalWeightage1 = uF.parseToDouble(rs.getString("weightage"));
				dblTotalAggregate1 = uF.parseToDouble(uF.formatIntoTwoDecimal(((dblTotalMarks1 / dblTotalWeightage1) * 100)));

				if(!attribIdList.contains(rs.getString("appraisal_attribute"))) {
					attribIdList.add(rs.getString("appraisal_attribute"));
				}
				hmScoreAggregateMap.put(rs.getString("emp_id")+"_"+rs.getString("appraisal_attribute"), uF.showData("" + dblTotalAggregate1, "0"));
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmAttributeThreshhold", hmAttributeThreshhold);
			request.setAttribute("attribIdList", attribIdList);
			request.setAttribute("hmScoreAggregateMap", hmScoreAggregateMap);
			Map<String, String> hmFrequency = new HashMap<String, String>();
			pst = con.prepareStatement("select * from appraisal_frequency");
			rs = pst.executeQuery();
			while (rs.next()) {
				hmFrequency.put(rs.getString("appraisal_frequency_id"), rs.getString("frequency_name"));
			}
			rs.close();
			pst.close();
			
			Map<String, String> hmDesignation = CF.getDesigMap(con);
			Map<String, String> hmGradeMap = CF.getGradeMap(con);
			Map<String, String> hmLevelMap = getLevelMap(con);
			Map<String, String> hmEmpName = CF.getEmpNameMap(con,null, null);
			Map<String, String> hmEmpCode = CF.getEmpCodeMap(con);
			Map<String, String> orientationMp = getOrientationValue(con);
			/*pst = con.prepareStatement("select * from appraisal_details a, appraisal_details_frequency adf where a.appraisal_details_id = adf.appraisal_id " 
					+ " and (is_delete is null or is_delete = false) and appraisal_details_id =?");*/
			pst = con.prepareStatement("select * from appraisal_details where appraisal_details_id =?");
			pst.setInt(1, uF.parseToInt(id));
			rs = pst.executeQuery();
//			System.out.println("==>pstAppraisaldetails"+pst);
			Map<String, String> appraisalMp = new HashMap<String, String>();
			Map<String, String> hmUserTypeId = CF.getUserTypeIdMap(con);
			int memberCount=0;
			while (rs.next()) {
				List<String> memberList = new ArrayList<String>();
				if(rs.getString("usertype_member") != null && !rs.getString("usertype_member").equals("")) {
					memberList = Arrays.asList(rs.getString("usertype_member").split(","));
				}
				String memberName = "";
				for (int i=0; memberList!=null && !memberList.isEmpty() && i<memberList.size(); i++) {
					if (i == 0) {
						memberName += orientationMemberMp.get(memberList.get(i));
					} else {
						memberName += ", " + orientationMemberMp.get(memberList.get(i));
					}
					memberCount++;
				}
				if(memberName == null || memberName.equals("null")) {
					memberName = "Anyone"; 
				}
				appraisalMp.put("ID", rs.getString("appraisal_details_id"));
				appraisalMp.put("APPRAISAL", rs.getString("appraisal_name"));
				appraisalMp.put("APPRAISALTYPE", uF.showData(rs.getString("appraisal_type"), ""));
				appraisalMp.put("DESCRIPTION", uF.showData(rs.getString("appraisal_description"), ""));
				appraisalMp.put("INSTRUCTION", uF.showData(rs.getString("appraisal_instruction"), ""));
				appraisalMp.put("ORIENT", orientationMp.get(rs.getString("oriented_type")) + " (" + memberName + ")");
				appraisalMp.put("EMPLOYEE", uF.showData(getAppendData(rs.getString("self_ids"), hmEmpName), ""));
				appraisalMp.put("LEVEL", uF.showData(hmLevelMap.get(rs.getString("level_id")), ""));
				appraisalMp.put("DESIG", hmDesignation.get(rs.getString("desig_id")));
				appraisalMp.put("GRADE", hmGradeMap.get(rs.getString("grade_id")));
				appraisalMp.put("WLOCATION", rs.getString("wlocation_id"));
				appraisalMp.put("PEER", rs.getString("peer_ids"));
				appraisalMp.put("SELFID", ","+getEmpID()+","); //rs.getString("self_ids")
				appraisalMp.put("APPRAISEE", uF.showData(getAppendData(rs.getString("self_ids"), hmEmpName), ""));
				appraisalMp.put("SUPERVISORID", rs.getString("supervisor_id"));
				appraisalMp.put("FREQUENCY", uF.showData(hmFrequency.get(rs.getString("frequency")), ""));
				appraisalMp.put("FROM", uF.getDateFormat(rs.getString("from_date"), DBDATE, CF.getStrReportDateFormat()));
				appraisalMp.put("TO", uF.getDateFormat(rs.getString("to_date"), DBDATE, CF.getStrReportDateFormat()));
				appraisalMp.put("IS_CLOSE", rs.getString("is_close"));
				appraisalMp.put("CEO", rs.getString("ceo_ids"));
				appraisalMp.put("HOD", rs.getString("hod_ids"));
				
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
			pst = con.prepareStatement("select * from appraisal_details_frequency where (is_delete =false or is_delete is null) and appraisal_id = ? and appraisal_freq_id =?");
			pst.setInt(1, uF.parseToInt(getId()));
			pst.setInt(2, uF.parseToInt(getAppFreqId()));
			rs = pst.executeQuery();
//			System.out.println("pst ===>> " + pst);
			while (rs.next()) {
				appraisalMp.put("APP_FREQ_ID", rs.getString("appraisal_freq_id"));
				appraisalMp.put("APP_FREQ_CLOSE", rs.getString("is_appraisal_close"));
				appraisalMp.put("APP_FREQ_PUBLISH", rs.getString("is_appraisal_publish"));
				appraisalMp.put("APP_FREQ_EXPIRE", rs.getString("freq_publish_expire_status"));
				appraisalMp.put("APP_FREQ_CLOSE_REASON", rs.getString("close_reason"));
				appraisalMp.put("APP_FREQ_FROM", uF.getDateFormat(rs.getString("freq_start_date"), DBDATE, CF.getStrReportDateFormat()));
				appraisalMp.put("APP_FREQ_TO", uF.getDateFormat(rs.getString("freq_end_date"), DBDATE, CF.getStrReportDateFormat()));
				
			}
			rs.close();
			pst.close();
			getEmpWlocation(appraisalMp.get("SELFID"));
			String empids = appraisalMp.get("SELFID")!=null && !appraisalMp.get("SELFID").equals("") ? appraisalMp.get("SELFID").substring(1, appraisalMp.get("SELFID").length()-1) : "";
			Map<String, String> empImageMap = new HashMap<String, String>();
			if(empids != null && !empids.equals("") && !empids.equalsIgnoreCase("null")) {
				pst = con.prepareStatement("select emp_image,emp_per_id from employee_personal_details where emp_per_id in(" + empids + ")");
				rs = pst.executeQuery();
				while (rs.next()) {
					empImageMap.put(rs.getString("emp_per_id"), rs.getString("emp_image"));
				} 
				rs.close();
				pst.close();
			}
			request.setAttribute("empImageMap", empImageMap);
			request.setAttribute("appraisalMp", appraisalMp);
			
			alInnerExport.add(new DataStyle("Balance Score Card_"+appraisalMp.get("APPRAISAL"), Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			reportListExport.add(alInnerExport);
			alInnerExport = new ArrayList<DataStyle>();	
			alInnerExport.add(new DataStyle(appraisalMp.get("APPRAISAL"),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			reportListExport.add(alInnerExport);
		
			alInnerExport = new ArrayList<DataStyle>();
			alInnerExport.add(new DataStyle("Review Type:",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle(appraisalMp.get("APPRAISALTYPE"),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			reportListExport.add(alInnerExport);
			
			alInnerExport = new ArrayList<DataStyle>();
			alInnerExport.add(new DataStyle("Description:",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle(appraisalMp.get("DESCRIPTION"),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			reportListExport.add(alInnerExport);
			
			alInnerExport = new ArrayList<DataStyle>();
			alInnerExport.add(new DataStyle("Frequency:",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle(appraisalMp.get("FREQUENCY"),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			reportListExport.add(alInnerExport);
			
			alInnerExport = new ArrayList<DataStyle>();
			alInnerExport.add(new DataStyle("Effective Date: ",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle(appraisalMp.get("APP_FREQ_FROM"),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			reportListExport.add(alInnerExport);
			
			alInnerExport = new ArrayList<DataStyle>();
			alInnerExport.add(new DataStyle("Due Date:",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle(appraisalMp.get("APP_FREQ_TO"),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			reportListExport.add(alInnerExport);
			
			alInnerExport = new ArrayList<DataStyle>();
			alInnerExport.add(new DataStyle("Orientation",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle(appraisalMp.get("ORIENT"),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			reportListExport.add(alInnerExport);
			
			alInnerExport = new ArrayList<DataStyle>();
			alInnerExport.add(new DataStyle("Score Cards",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			reportListExport.add(alInnerExport);
			
			alInnerExport = new ArrayList<DataStyle>();
			alInnerExport.add(new DataStyle("Employee Code",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Employee Name",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Designation",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Location",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));						
			
			pst = con.prepareStatement("select self_ids,appraisal_details_id,oriented_type from appraisal_details where appraisal_details_id=?");
			pst.setInt(1, uF.parseToInt(id));
			rs = pst.executeQuery();
			while (rs.next()) {
				self_ids = ","+getEmpID()+","; //rs.getString("self_ids")
				// appraisal_details_id = rs.getInt(2);
				oriented_type = rs.getString(3);
			}
			rs.close();
			pst.close();
			
			self_ids=self_ids!=null && !self_ids.equals("") ? self_ids.substring(1, self_ids.length()-1) :"";
			List<String> empList = new ArrayList<String>();
			if(getType() != null && getType().equals("KRATARGET")) {
				empList.add(strSessionEmpId);
			} else {
				empList = Arrays.asList(self_ids.split(","));
			}

		//===start parvez date: 02-03-2023===	
//			pst = con.prepareStatement("select *,(marks*100/weightage) as average ,(reviewer_marks*100/weightage) as reviewer_average from " +
//				"(select sum(marks) as marks ,sum(weightage) as weightage, sum(reviewer_marks) as reviewer_marks,user_type_id,emp_id " +
//				"from appraisal_question_answer where appraisal_id=? and appraisal_freq_id=? and emp_id=? and weightage>0 and " +
//				"reviewer_or_appraiser=0 group by user_type_id,emp_id) as a order by emp_id ");
			/*pst = con.prepareStatement("select *,(marks*100/weightage) as average ,(reviewer_marks*100/weightage) as reviewer_average from " +
					"(select sum(marks) as marks ,sum(weightage) as weightage, sum(reviewer_marks) as reviewer_marks,user_type_id,emp_id,section_weightage " +
					"from appraisal_question_answer aqa, appraisal_main_level_details amld where aqa.appraisal_id=? and aqa.appraisal_freq_id=? and emp_id=? and weightage>0 and " +
					"reviewer_or_appraiser=0 and aqa.section_id=amld.main_level_id group by user_type_id,emp_id,amld.main_level_id) as a order by emp_id,user_type_id ");*/
			pst = con.prepareStatement("select *,(marks*100/weightage) as average ,(reviewer_marks*100/weightage) as reviewer_average from " +
					"(select sum(marks) as marks ,sum(aqa.weightage) as weightage, sum(reviewer_marks) as reviewer_marks,user_type_id,emp_id,section_weightage," +
					"score_calculation_basis from appraisal_question_answer aqa, appraisal_main_level_details amld,appraisal_question_details aqd " +
					"where aqa.appraisal_id=? and aqa.appraisal_freq_id=? and emp_id=? and aqa.weightage>0 and reviewer_or_appraiser=0 and " +
					" aqa.section_id=amld.main_level_id and aqa.appraisal_question_details_id=aqd.appraisal_question_details_id " +
					" group by user_type_id,emp_id,amld.main_level_id,score_calculation_basis) as a order by emp_id,user_type_id ");
		//===end parvez date: 02-03-2023===	
			pst.setInt(1, uF.parseToInt(getId()));
			pst.setInt(2, uF.parseToInt(getAppFreqId()));
			pst.setInt(3, uF.parseToInt(getEmpID()));
//			System.out.println("RVS/1024--pst2==>"+pst);
			rs = pst.executeQuery();
//			System.out.println("pst ========> "+pst);
			Map<String, Map<String, String>> outerMp = new HashMap<String, Map<String, String>>();
			String strEmpIdNew = null;
			String strEmpIdOld = null;
			double dblTotalMarks = 0; 
			double dblTotalWeightage = 0;
			double dblUsertypeAvg = 0;		//===created by parvez date: 17-12-2021===
			String strUserTypeIdNew = null;
			String strUserTypeIdOld = null;
			while (rs.next()) {
				strEmpIdNew = rs.getString("emp_id");
				strUserTypeIdNew = rs.getString("user_type_id");
				if(strEmpIdNew!=null && !strEmpIdNew.equalsIgnoreCase(strEmpIdOld)) {
					dblTotalMarks = 0;
					dblTotalWeightage = 0;
				}
				if(rs.getDouble("reviewer_marks") > 0) {
					dblTotalMarks += uF.parseToDouble(rs.getString("reviewer_marks"));
				} else {
					dblTotalMarks += uF.parseToDouble(rs.getString("marks"));
				}
//				System.out.println("dblTotalMarks"+dblTotalMarks);
				dblTotalWeightage += uF.parseToDouble(rs.getString("weightage"));
				Map<String, String> value = outerMp.get(rs.getString("emp_id"));
				if (value == null)
					value = new HashMap<String, String>();
//				value.put(rs.getString("user_type_id"), uF.formatIntoTwoDecimal(rs.getDouble("average")));
				value.put(rs.getString("user_type_id")+"_REVIEWER", uF.formatIntoTwoDecimal(rs.getDouble("reviewer_average")));
				if(strUserTypeIdNew!=null && !strUserTypeIdNew.equalsIgnoreCase(strUserTypeIdOld)) {
					dblUsertypeAvg = 0;
				}
				double dblLevelAvg = (rs.getDouble("average")*uF.parseToDouble(rs.getString("section_weightage"))) /100;
				dblUsertypeAvg += dblLevelAvg;
				value.put(rs.getString("user_type_id"), uF.formatIntoTwoDecimal(dblUsertypeAvg));
				
				if(dblTotalWeightage>0) {
					value.put("AGGREGATE", uF.formatIntoTwoDecimal((dblTotalMarks * 100)/dblTotalWeightage));
				}
			//===start parvez date: 02-03-2023===	
				value.put("ACTUAL_CAL_BASIS", rs.getString("score_calculation_basis"));
			//===end parvez date: 02-03-2023===	
				outerMp.put(rs.getString("emp_id"), value);
				
				strEmpIdOld = strEmpIdNew;
				strUserTypeIdOld = strUserTypeIdNew;
			}
			rs.close();
			pst.close();
//			System.out.println("outerMp==>"+outerMp);
			// request.setAttribute("hmUserTypeID", hmUserTypeID);
			request.setAttribute("outerMp", outerMp);
			request.setAttribute("empList", empList);
			request.setAttribute("oriented_type", oriented_type);
			
			
	//===start parvez date: 02-03-2023===
//			pst = con.prepareStatement("select *,(marks*100/weightage) as average from (select sum(marks) as marks ,sum(weightage) as weightage," +
//				"user_type_id,emp_id from appraisal_question_answer where appraisal_id=? and appraisal_freq_id=? and emp_id=? and weightage>0 " +
//				"and reviewer_or_appraiser=1 and is_submit=true group by user_type_id,emp_id) as a order by emp_id ");
			/*pst = con.prepareStatement("select *,(marks*100/weightage) as average from (select sum(aqa.marks) as marks ,sum(aqa.weightage) as weightage," +
					"user_type_id,emp_id,section_weightage from appraisal_question_answer aqa,appraisal_main_level_details amld where aqa.appraisal_id=? and aqa.appraisal_freq_id=? and emp_id=? and weightage>0 " +
					"and reviewer_or_appraiser=1 and is_submit=true and aqa.section_id=amld.main_level_id group by user_type_id,emp_id,amld.main_level_id) as a order by emp_id ");*/
			pst = con.prepareStatement("select *,(marks*100/weightage) as average from (select sum(aqa.marks) as marks ,sum(aqa.weightage) as weightage," +
					"user_type_id,emp_id,section_weightage,score_calculation_basis " +
					"from appraisal_question_answer aqa,appraisal_main_level_details amld, appraisal_question_details aqd where aqa.appraisal_id=? " +
					"and aqa.appraisal_freq_id=? and emp_id=? and aqa.weightage>0 and reviewer_or_appraiser=1 and is_submit=true and aqa.section_id=amld.main_level_id " +
					" and aqa.appraisal_question_details_id=aqd.appraisal_question_details_id group by user_type_id,emp_id,amld.main_level_id,score_calculation_basis) as a order by emp_id ");
			
		
	//===end parvez date: 02-03-2023===
			pst.setInt(1, uF.parseToInt(getId()));
			pst.setInt(2, uF.parseToInt(getAppFreqId()));
			pst.setInt(3, uF.parseToInt(getEmpID()));
//			System.out.println("pst2==>"+pst);
			rs = pst.executeQuery();
//			System.out.println("RVS/1111--pst ========> "+pst);
			Map<String, Map<String, String>> reviewerOutMp = new HashMap<String, Map<String, String>>();
			String strEmpIdNewReviewer = null;
			String strEmpIdOldReviewer = null;
			double dblTotalMarksReviewer = 0; 
			double dblTotalWeightageReviewer = 0;
			double dblReviewerAvg = 0;
			while (rs.next()) {
				strEmpIdNewReviewer = rs.getString("emp_id");
				if(strEmpIdNewReviewer!=null && !strEmpIdNewReviewer.equalsIgnoreCase(strEmpIdOldReviewer)) {
					dblTotalMarksReviewer = 0;
					dblTotalWeightageReviewer = 0;
				}
				dblTotalMarksReviewer += uF.parseToDouble(rs.getString("marks"));
//					System.out.println("dblTotalMarks"+dblTotalMarks);
				dblTotalWeightageReviewer += uF.parseToDouble(rs.getString("weightage"));
				Map<String, String> value = reviewerOutMp.get(rs.getString("emp_id"));
				if (value == null)
					value = new HashMap<String, String>();
//				value.put("REVIEWER", uF.formatIntoTwoDecimal(rs.getDouble("average")));
//				value.put("REVIEWER_USERTYPE", rs.getString("user_type_id"));
				
				double dblLevelAvg = (rs.getDouble("average")*uF.parseToDouble(rs.getString("section_weightage"))) /100;
				dblReviewerAvg += dblLevelAvg;
				value.put("REVIEWER", uF.formatIntoTwoDecimal(dblReviewerAvg));
				value.put("REVIEWER_USERTYPE", rs.getString("user_type_id"));
				
				if(dblTotalWeightageReviewer>0) {
					value.put("AGGREGATE", uF.formatIntoTwoDecimal((dblTotalMarksReviewer * 100)/dblTotalWeightageReviewer));
				}
			//===start parvez date: 02-03-2023===	
				value.put("ACTUAL_CAL_BASIS", rs.getString("score_calculation_basis"));
			//===end parvez date: 02-03-2023===	
				reviewerOutMp.put(rs.getString("emp_id"), value);
				
				strEmpIdOld = strEmpIdNew;
			}
			rs.close();
			pst.close();
//			System.out.println("reviewerOutMp==>"+reviewerOutMp);
			// request.setAttribute("hmUserTypeID", hmUserTypeID);
			request.setAttribute("reviewerOutMp", reviewerOutMp);
				
			
			pst = con.prepareStatement("select count(*) as count,emp_id,appraisal_id,appraisal_freq_id from (select emp_id,appraisal_id,user_type_id,appraisal_freq_id from appraisal_question_answer "
				+ "where appraisal_id=? and appraisal_freq_id=? and emp_id=? group by emp_id,user_type_id,appraisal_id,appraisal_freq_id)as a group by emp_id,appraisal_id,appraisal_freq_id");
			pst.setInt(1, uF.parseToInt(getId()));		
			pst.setInt(2, uF.parseToInt(getAppFreqId()));
			pst.setInt(3, uF.parseToInt(getEmpID()));
//			System.out.println("hmEmpCount pst3==>"+pst);
			rs = pst.executeQuery();
			Map<String,String> hmEmpCount=new HashMap<String, String>();
			while (rs.next()) {
				hmEmpCount.put(rs.getString("emp_id"), rs.getString("count"));				
			}		
			rs.close();
			pst.close();
			request.setAttribute("hmEmpCount", hmEmpCount);
			Map<String, String> locationMp = (Map<String, String>) request.getAttribute("locationMp");
			List<String> memberList = (List<String>) request.getAttribute("memberList"); 
			Map<String, String> hmEmpWlocationMap=CF.getEmpWlocationMap(con);
			Map<String, String> hmEmpCodeDesig = CF.getEmpDesigMap(con);
			for (int i=0; memberList!=null && i<memberList.size(); i++) {
//				System.out.println("MemberList"+orientationMemberMp.get(memberList.get(i)));
				alInnerExport.add(new DataStyle(orientationMemberMp.get(memberList.get(i)),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			}
			alInnerExport.add(new DataStyle("Balanced Score",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));		
			
			reportListExport.add(alInnerExport);
			for (int i = 0; empList != null && i < empList.size(); i++) {
				alInnerExport = new ArrayList<DataStyle>();
                Map<String, String> value = outerMp.get(empList.get(i).trim());
         		if (value == null)
         			value = new HashMap<String, String>();
         			alInnerExport.add(new DataStyle(hmEmpCode.get(empList.get(i)),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
       		  		alInnerExport.add(new DataStyle(hmEmpName.get(empList.get(i)),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
       		  		alInnerExport.add(new DataStyle(hmEmpCodeDesig.get(empList.get(i)),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
       		  		alInnerExport.add(new DataStyle(locationMp.get(empList.get(i)),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
         		  
			        for (int j=0; memberList!=null && j<memberList.size(); j++) {
			        	if(value.get(memberList.get(j).trim())!=null) {
				        	alInnerExport.add(new DataStyle(uF.showData(value.get(memberList.get(j)),"0"),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				        } else {
				        	alInnerExport.add(new DataStyle("0",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				        }
					}
			        
                    boolean flag = false;
                    StringBuilder attribIds = new StringBuilder();
                    int attribCnt = 0;
                    int aggregateCnt = 0;
                    for(int a=0; attribIdList != null && !attribIdList.isEmpty() && a<attribIdList.size(); a++) {
                    	double aggregate = uF.parseToDouble(hmScoreAggregateMap.get(empList.get(i).trim()+"_"+attribIdList.get(a)));
                  
                    	if(aggregate < uF.parseToDouble(hmAttributeThreshhold.get(attribIdList.get(a)))) {
                    	
                    		attribIds.append(attribIdList.get(a)+"::");
                    		aggregateCnt++;
                    	}
                    	attribCnt++;
                    }
                   
                    if(attribCnt == aggregateCnt) {
                    	flag = true;
                    }                                                   
                        String aggregate="0.0";
                        if(memberCount==uF.parseToInt(hmEmpCount.get(empList.get(i).trim()))) {
                        	aggregate=value.get("AGGREGATE") != null ? uF.parseToDouble(value.get("AGGREGATE")) / 20 + "" : "0";
                       
                     if(!flag) {
                   
                     } else {

                     }                                  
                         alInnerExport.add(new DataStyle(uF.showData(uF.getRoundOffValue(2,uF.parseToDouble(value.get("AGGREGATE"))), "NA"),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
                    } else { 
                    	alInnerExport.add(new DataStyle("NA",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
                    }                                                                  				      				        				        				        				         
		      reportListExport.add(alInnerExport);
			
			}			
			session.setAttribute("reportListExportScoreCard", reportListExport);
		} catch (SQLException e) {
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
		Map<String, String> orientationMemberMp = getOrientationMember();
		con = db.makeConnection(con);
		String self_ids = null;
		String oriented_type = null;
		try {

			Map<String, String> hmAttributeThreshhold = new HashMap<String, String>();
			pst = con.prepareStatement("select attribute_id,threshhold from appraisal_attribute_level");
//			pst=con.prepareStatement(selectAttribute);
			rs = pst.executeQuery(); 
			while (rs.next()) {
				hmAttributeThreshhold.put(rs.getString("attribute_id"), rs.getString("threshhold"));
			}
			rs.close();
			pst.close();
			
			double dblTotalMarks1 = 0;
			double dblTotalWeightage1 = 0;
			double dblTotalAggregate1 = 0;
			Map<String, String> hmScoreAggregateMap = new HashMap<String, String>();
			pst = con.prepareStatement("select sum(marks) as marks, sum(weightage) as weightage, aqw.appraisal_attribute, aqw.emp_id from appraisal_question_answer aqw where aqw.appraisal_id=? and aqw.appraisal_freq_id = ? group by aqw.appraisal_attribute,aqw.emp_id");
			pst.setInt(1, uF.parseToInt(id));
			pst.setInt(2, uF.parseToInt(getAppFreqId()));
//			System.out.println("getAppraisalStatus pst1==>"+pst);
			rs = pst.executeQuery();
			List<String> attribIdList = new ArrayList<String>();
			while (rs.next()) {

				dblTotalMarks1 = uF.parseToDouble(rs.getString("marks"));
				dblTotalWeightage1 = uF.parseToDouble(rs.getString("weightage"));
				dblTotalAggregate1 = uF.parseToDouble(uF.formatIntoTwoDecimal(((dblTotalMarks1 / dblTotalWeightage1) * 100)));

				if(!attribIdList.contains(rs.getString("appraisal_attribute"))) {
					attribIdList.add(rs.getString("appraisal_attribute"));
				}
				hmScoreAggregateMap.put(rs.getString("emp_id")+"_"+rs.getString("appraisal_attribute"), uF.showData("" + dblTotalAggregate1, "0"));
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmAttributeThreshhold", hmAttributeThreshhold);
			request.setAttribute("attribIdList", attribIdList);
			request.setAttribute("hmScoreAggregateMap", hmScoreAggregateMap);
			
			
			
			Map<String, String> hmFrequency = new HashMap<String, String>();
			pst = con.prepareStatement("select * from appraisal_frequency");
			rs = pst.executeQuery();
			while (rs.next()) {
				hmFrequency.put(rs.getString("appraisal_frequency_id"), rs.getString("frequency_name"));
			}
			rs.close();
			pst.close();
			
			Map<String, String> hmDesignation = CF.getDesigMap(con);
			Map<String, String> hmGradeMap = CF.getGradeMap(con);
			Map<String, String> hmLevelMap = getLevelMap(con);
			Map<String, String> hmEmpName = CF.getEmpNameMap(con,null, null);
			Map<String, String> orientationMp = getOrientationValue(con);
			pst = con.prepareStatement("select * from appraisal_details where appraisal_details_id =?");
			pst.setInt(1, uF.parseToInt(id));
			rs = pst.executeQuery();
//			System.out.println("==>pstAppraisaldetails"+pst);
			Map<String, String> appraisalMp = new HashMap<String, String>();
			Map<String, String> hmUserTypeId = CF.getUserTypeIdMap(con);
			int memberCount=0;
			while (rs.next()) {

				List<String> memberList = new ArrayList<String>();

				if(rs.getString("usertype_member") != null && !rs.getString("usertype_member").equals("")) {
					memberList = Arrays.asList(rs.getString("usertype_member").split(","));
				}
				String memberName = "";
				
				for (int i = 0; memberList != null && !memberList.isEmpty() && i < memberList.size(); i++) {
					if (i == 0) {
						memberName += orientationMemberMp.get(memberList.get(i));
					} else {
						memberName += ", " + orientationMemberMp.get(memberList.get(i));
					}
					memberCount++;
				}
				if(memberName == null || memberName.equals("null")) {
					memberName = "Anyone"; 
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
				appraisalMp.put("IS_CLOSE", rs.getString("is_close"));
				appraisalMp.put("CEO", rs.getString("ceo_ids"));
				appraisalMp.put("HOD", rs.getString("hod_ids"));
				
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
			
			pst = con.prepareStatement("select * from appraisal_details_frequency  where (is_delete =false or is_delete is null) and appraisal_id = ? and appraisal_freq_id =?");
			pst.setInt(1, uF.parseToInt(getId()));
			pst.setInt(2, uF.parseToInt(getAppFreqId()));
			rs = pst.executeQuery();
		
			while (rs.next()) {

				appraisalMp.put("APP_FREQ_ID", rs.getString("appraisal_freq_id"));
				appraisalMp.put("APP_FREQ_CLOSE", rs.getString("is_appraisal_close"));
				appraisalMp.put("APP_FREQ_PUBLISH", rs.getString("is_appraisal_publish"));
				appraisalMp.put("APP_FREQ_EXPIRE", rs.getString("freq_publish_expire_status"));
				appraisalMp.put("APP_FREQ_CLOSE_REASON", rs.getString("close_reason"));
				
				appraisalMp.put("APP_FREQ_FROM", uF.getDateFormat(rs.getString("freq_start_date"), DBDATE, CF.getStrReportDateFormat()));
				appraisalMp.put("APP_FREQ_TO", uF.getDateFormat(rs.getString("freq_end_date"), DBDATE, CF.getStrReportDateFormat()));
				
			}
			rs.close();
			pst.close();
			
			getEmpWlocation(appraisalMp.get("SELFID"));
			
			String empids=appraisalMp.get("SELFID")!=null && !appraisalMp.get("SELFID").equals("") ? appraisalMp.get("SELFID").substring(1, appraisalMp.get("SELFID").length()-1) : "";
			Map<String, String> empImageMap = new HashMap<String, String>();
			if(empids != null && !empids.equals("") && !empids.equalsIgnoreCase("null")) {
				pst = con.prepareStatement("select emp_image,emp_per_id from employee_personal_details where emp_per_id in(" + empids + ")");
				rs = pst.executeQuery();
				
				while (rs.next()) {
					empImageMap.put(rs.getString("emp_per_id"), rs.getString("emp_image"));
				} 
				rs.close();
				pst.close();
			}
			request.setAttribute("empImageMap", empImageMap);

			request.setAttribute("appraisalMp", appraisalMp);

			pst = con.prepareStatement("select self_ids,appraisal_details_id,oriented_type from appraisal_details where appraisal_details_id=?");
			pst.setInt(1, uF.parseToInt(id));
			rs = pst.executeQuery();
			while (rs.next()) {
				self_ids = rs.getString("self_ids");
				oriented_type = rs.getString(3);
			}
			rs.close();
			pst.close();
			
			self_ids=self_ids!=null && !self_ids.equals("") ? self_ids.substring(1, self_ids.length()-1) : "";
			List<String> empList = new ArrayList<String>();
			if(getType() != null && getType().equals("KRATARGET")) {
				empList.add(strSessionEmpId);
			} else {
				empList = Arrays.asList(self_ids.split(","));
			}

		//===start parvez date: 02-03-2023===	
			/*pst = con.prepareStatement("select *,(marks*100/weightage) as average from(select sum(marks) as marks ,sum(weightage) as weightage," +
					"user_type_id,emp_id from appraisal_question_answer where appraisal_id=? and appraisal_freq_id = ? and weightage>0 group by user_type_id,emp_id)as a order by emp_id ");*/
			pst = con.prepareStatement("select *,(marks*100/weightage) as average from(select sum(marks) as marks ,sum(aqa.weightage) as weightage," +
				"aqa.user_type_id,aqa.emp_id,score_calculation_basis from appraisal_question_answer aqa, appraisal_question_details aqd " +
				" where aqa.appraisal_id=? and aqa.appraisal_freq_id = ? and aqa.weightage>0 and aqa.appraisal_question_details_id=aqd.appraisal_question_details_id " +
				" group by user_type_id,emp_id,score_calculation_basis)as a order by emp_id ");
//			pst = con.prepareStatement("select *,(marks*100/weightage) as average from(select sum(aqa.marks) as marks ,sum(aqa.weightage) as weightage," +
//			"user_type_id,emp_id,section_weightage from appraisal_question_answer aqa, appraisal_main_level_details amld where aqa.appraisal_id=? and aqa.appraisal_freq_id = ? and weightage>0 and aqa.section_id=amld.main_level_id "
//			+" group by user_type_id,emp_id,amld.main_level_id)as a order by emp_id ");
		//===start parvez date: 02-03-2023===	
			pst.setInt(1, uF.parseToInt(getId()));
			pst.setInt(2, uF.parseToInt(getAppFreqId()));
//			System.out.println("RVS/1408--pst2==>"+pst);
			rs = pst.executeQuery();
//			System.out.println("pst ========> "+pst);
			Map<String, Map<String, String>> outerMp = new HashMap<String, Map<String, String>>();
			String strEmpIdNew = null;
			String strEmpIdOld = null;
			double dblTotalMarks = 0; 
			double dblTotalWeightage = 0;
			double dblUsertypeAvg = 0;		//===created by parvez date: 17-12-2021===
			while (rs.next()) {
				strEmpIdNew = rs.getString("emp_id");
				if(strEmpIdNew!=null && !strEmpIdNew.equalsIgnoreCase(strEmpIdOld)) {
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
				
//				value.put(rs.getString("user_type_id"), uF.formatIntoTwoDecimal(rs.getDouble("average")));
//				double dblLevelAvg = (rs.getDouble("average")*uF.parseToDouble(rs.getString("section_weightage"))) /100;
//				dblUsertypeAvg += dblLevelAvg;
//				value.put(rs.getString("user_type_id"), uF.formatIntoTwoDecimal(dblUsertypeAvg));
//				System.out.println("RVS/1432--dblUsertypeAvg="+dblUsertypeAvg+"---user_type_id="+rs.getString("user_type_id"));
			//===end parvez date: 17-12-2021===
				
				if(dblTotalWeightage>0) {
					value.put("AGGREGATE", uF.formatIntoTwoDecimal((dblTotalMarks * 100)/dblTotalWeightage));
//					System.out.println("RVS/1432--AGGREGATE="+uF.formatIntoTwoDecimal((dblTotalMarks * 100)/dblTotalWeightage));
				}
			//===start parvez date: 02-03-2023===	
				value.put("ACTUAL_CAL_BASIS", rs.getString("score_calculation_basis"));
			//===end parvez date: 02-03-2023===
				
				outerMp.put(rs.getString("emp_id"), value);
				
				strEmpIdOld = strEmpIdNew;
			}
			rs.close();
			pst.close();
//			System.out.println("outerMp==>"+outerMp);
			// request.setAttribute("hmUserTypeID", hmUserTypeID);
			request.setAttribute("outerMp", outerMp);
			request.setAttribute("empList", empList);
			request.setAttribute("oriented_type", oriented_type);
			
			pst = con.prepareStatement("select count(*) as count,emp_id,appraisal_id,appraisal_freq_id from (select emp_id,appraisal_id,user_type_id,appraisal_freq_id from appraisal_question_answer "
					+ "where appraisal_id=? and appraisal_freq_id = ? group by emp_id,user_type_id,appraisal_id,appraisal_freq_id)as a group by emp_id,appraisal_id,appraisal_freq_id");
			pst.setInt(1, uF.parseToInt(getId()));		
			pst.setInt(2, uF.parseToInt(getAppFreqId()));		
//			System.out.println("hmEmpCount pst3==>"+pst);
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
			empIds=empIds !=null && !empIds.equals("") ? empIds.substring(1, empIds.length()-1) : "";
			if(empIds != null && !empIds.equals("") && !empIds.equalsIgnoreCase("null")) {
				pst = con.prepareStatement("select eod.wlocation_id,emp_id,wlocation_name from employee_official_details eod,work_location_info wli " +
					" where eod.wlocation_id=wli.wlocation_id and emp_id in(" + empIds + ")");
	//			System.out.println("pst====> "+pst);
				rs = pst.executeQuery();
				while (rs.next()) {
					locationMp.put(rs.getString("emp_id"), rs.getString("wlocation_name"));
				}
				rs.close();
				pst.close();
			}
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
		}
		return hmLevelMap;
	}

	
	
	private Map<String, String> getOrientationMember() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
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
	
	

	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {

		this.request = request;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAlertStatus() {
		return alertStatus;
	}

	public void setAlertStatus(String alertStatus) {
		this.alertStatus = alertStatus;
	}

	public String getAlert_type() {
		return alert_type;
	}

	public void setAlert_type(String alert_type) {
		this.alert_type = alert_type;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getStrMessage() {
		return strMessage;
	}

	public void setStrMessage(String strMessage) {
		this.strMessage = strMessage;
	}

	public String getAppFreqId() {
		return appFreqId;
	}

	public void setAppFreqId(String appFreqId) {
		this.appFreqId = appFreqId;
	}

	public String getFromPage() {
		return fromPage;
	}

	public void setFromPage(String fromPage) {
		this.fromPage = fromPage;
	}

	public String getEmpID() {
		return empID;
	}

	public void setEmpID(String empID) {
		this.empID = empID;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}
	
}