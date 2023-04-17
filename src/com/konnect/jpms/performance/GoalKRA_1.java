package com.konnect.jpms.performance;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class GoalKRA_1 extends ActionSupport implements ServletRequestAware, IStatements {
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
	private String[] f_strWLocation;
	private String[] f_department;
	private String[] f_level;
	
	private List<FillOrganisation> orgList;
	private List<FillWLocation> wLocationList;
	private List<FillDepartment> departmentList;
	private List<FillLevel> levelList;
	
	private String exportType;
	
	private String strSearchJob;
	
	private String proPage;
	private String minLimit;
	private String strPerspective;
	
	private String fromPage;
	private String currUserType;
	private String strEmpId;
	public String execute() {
//		System.out.println("In goalKRA1.jsva");
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
		
		request.setAttribute(PAGE, "/jsp/performance/GoalKRA_1.jsp");
		request.setAttribute(TITLE, "Goals, KRAs, Targets"); //TKRAs
		
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
		
		getEmployeeData(uF);
		if(getStrEmpId() != null && uF.parseToInt(getStrEmpId()) > 0) {
			List<String> empList = new ArrayList<String>();
			empList.add(getStrEmpId());
			getEmpGoalKRADetails(uF,empList);
			getEmpKRADetails(uF,empList);
			getKRARatingAndCompletionStatus(uF,empList);
			getEmpTargetDetails(uF,empList);
			checkGoalKRATargetStatus(uF);
			checkGoalKRATargetAlognedWithAllowance(uF);
			getActualAchievedGoal(uF);
			
		} 
		
		return loadKRAData(uF);
	}

	public void getActualAchievedGoal(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			con = db.makeConnection(con);
			String updateMsg = null;
			Map<String, List<String>> hmActualAchievedGoal = new HashMap<String,List<String>>();
			pst=con.prepareStatement("select * from goal_kra_target_finalization where emp_id = ? order by gkt_finalization_id desc");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			/*System.out.println("pst==>"+pst);*/
			rs=pst.executeQuery();
			while(rs.next()) {
				List<String> alInner = new ArrayList<String>();
				alInner.add(rs.getString("gkt_finalization_id"));//0
				alInner.add(rs.getString("emp_id"));//1
				alInner.add(rs.getString("goal_id"));//2
				alInner.add(rs.getString("goal_weightage"));//3
				alInner.add(rs.getString("goal_achieve_share"));//4
				alInner.add(rs.getString("goal_actual_achieved"));//5
				alInner.add(rs.getString("goal_freq_id"));//6
			//	System.out.println("goal_freq_id==>"+ rs.getString("goal_freq_id"));
				hmActualAchievedGoal.put(rs.getString("emp_id")+"_"+rs.getString("goal_id")+"_"+rs.getString("goal_freq_id"), alInner);
			}
			rs.close();
			pst.close();
			//System.out.println("hmActualAchievedGoal ===>> " + hmActualAchievedGoal);
			request.setAttribute("hmActualAchievedGoal", hmActualAchievedGoal);			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeResultSet(rs);
			db.closeConnection(con);
		}
	}
	
	public void checkGoalKRATargetAlognedWithAllowance(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try { 
			con = db.makeConnection(con);
			
	
			pst = con.prepareStatement("select goal_id from goal_details where is_close = false and (measure_kra != 'KRA' or measure_kra is null)");
			rs = pst.executeQuery();
			List<String> alGoalIds = new ArrayList<String>();
			while (rs.next()) {
				alGoalIds.add(rs.getString("goal_id"));
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select goal_kra_id from goal_kras where is_close = false and (is_assign = true or is_assign is null)");
			rs = pst.executeQuery();
			List<String> alKRAIds = new ArrayList<String>();
			while (rs.next()) {
				alKRAIds.add(rs.getString("goal_kra_id"));
			}
			rs.close();
			pst.close();
			
			Map<String, String> hmCheckGTWithAllowance = new HashMap<String, String>();
			for(int i=0; alGoalIds != null && i<alGoalIds.size(); i++) {
				boolean flag = false;
				pst = con.prepareStatement("select allowance_condition_id from allowance_condition_details where goal_kra_target_ids like '%,"+alGoalIds.get(i)+",%'");
				rs = pst.executeQuery();
				while (rs.next()) {
					flag = true;
				}
				rs.close();
				pst.close();
				hmCheckGTWithAllowance.put(alGoalIds.get(i), ""+flag);
			}
			request.setAttribute("hmCheckGTWithAllowance", hmCheckGTWithAllowance);
			
			
			Map<String, String> hmCheckKWithAllowance = new HashMap<String, String>();
			for(int i=0; alKRAIds != null && i<alKRAIds.size(); i++) {
				boolean flag = false;
				pst = con.prepareStatement("select allowance_condition_id from allowance_condition_details where kra_ids like '%,"+alKRAIds.get(i)+",%'");
				rs = pst.executeQuery();
				while (rs.next()) {
					flag = true;
				}
				rs.close();
				pst.close();
				hmCheckKWithAllowance.put(alKRAIds.get(i), ""+flag);
			}
			request.setAttribute("hmCheckKWithAllowance", hmCheckKWithAllowance);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
		
	public void getKRARatingAndCompletionStatus(UtilityFunctions uF, List<String> empList) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			Map<String, String> hmKraAverage = new HashMap<String, String>();
			StringBuilder sbEmpId = null;
			
			for(int i=0; empList!= null && !empList.isEmpty() && i<empList.size(); i++) {
				if(uF.parseToInt(empList.get(i).trim()) > 0) {
					if(sbEmpId == null) {
						sbEmpId = new StringBuilder();
						sbEmpId.append(empList.get(i).trim());
					} else {
						sbEmpId.append(","+empList.get(i).trim());
					}
				}
			}
			
			
			Map<String, String> hmTargetRatingAndComment = new HashMap<String, String>();
			Map<String, String> hmEmpwiseGoalAndTargetRating = new HashMap<String, String>();
			if(sbEmpId != null) {
				pst=con.prepareStatement("select gksrd.*, gd.goal_id from goal_kra_status_rating_details gksrd, goal_details gd where gksrd.goal_id = gd.goal_id and gksrd.emp_id in ("+sbEmpId.toString()+")");
				rs=pst.executeQuery();
				while (rs.next()) {
					
					if((rs.getString("manager_rating") != null && !rs.getString("manager_rating").equals("")) || (rs.getString("hr_rating") != null && !rs.getString("hr_rating").equals("")) ) {
						double strCurrGoalORTargetRating = (uF.parseToDouble(rs.getString("manager_rating")) + uF.parseToDouble(rs.getString("hr_rating"))) / 2;
						if(rs.getString("manager_rating") == null) {
							strCurrGoalORTargetRating = uF.parseToDouble(rs.getString("hr_rating"));
						} else if(rs.getString("hr_rating") == null) {
							strCurrGoalORTargetRating = uF.parseToDouble(rs.getString("manager_rating"));
						}
						hmEmpwiseGoalAndTargetRating.put(rs.getString("emp_id")+"_"+rs.getString("goal_id")+"_"+rs.getString("goal_freq_id")+"_RATING", strCurrGoalORTargetRating+"");
					}
					
					hmTargetRatingAndComment.put(rs.getString("emp_id")+"_"+rs.getString("goal_id")+"_"+rs.getString("goal_freq_id")+"_STATUS", rs.getString("complete_percent"));
					hmTargetRatingAndComment.put(rs.getString("emp_id")+"_"+rs.getString("goal_id")+"_"+rs.getString("goal_freq_id")+"_MGR_RATING", rs.getString("manager_rating"));
					hmTargetRatingAndComment.put(rs.getString("emp_id")+"_"+rs.getString("goal_id")+"_"+rs.getString("goal_freq_id")+"_MGR_COMMENT", rs.getString("manager_comment"));
					hmTargetRatingAndComment.put(rs.getString("emp_id")+"_"+rs.getString("goal_id")+"_"+rs.getString("goal_freq_id")+"_HR_RATING", rs.getString("hr_rating"));
					hmTargetRatingAndComment.put(rs.getString("emp_id")+"_"+rs.getString("goal_id")+"_"+rs.getString("goal_freq_id")+"_HR_COMMENT", rs.getString("hr_comment"));
				}
				rs.close();
				pst.close();
			}
//			System.out.println("hmTargetRatingAndComment ===>> " + hmTargetRatingAndComment);
			request.setAttribute("hmTargetRatingAndComment", hmTargetRatingAndComment);
			request.setAttribute("hmEmpwiseGoalAndTargetRating", hmEmpwiseGoalAndTargetRating);
			
			
			Map<String, String> hmEmpwiseGoalAndTargetEmpRating = new HashMap<String, String>();
			if(sbEmpId != null) {
				pst=con.prepareStatement("select sum(gksrd.user_rating) as user_rating, count(*) as cnt, gksrd.goal_id, gksrd.emp_id," +
					" gksrd.goal_freq_id from goal_kra_emp_status_rating_details gksrd, goal_details gd where gksrd.goal_id = gd.goal_id and " +
					" gksrd.emp_id in ("+sbEmpId.toString()+") and user_type != '-' group by gksrd.goal_freq_id,gksrd.goal_id,gksrd.emp_id");
				rs=pst.executeQuery();
				while (rs.next()) {
					if((rs.getString("user_rating") != null && !rs.getString("user_rating").equals("")) ) {
//						double strCurrGoalORTargetRating = rs.getDouble("user_rating")/ rs.getInt("cnt");
						double strCurrGoalORTargetRating = rs.getDouble("user_rating");
						hmEmpwiseGoalAndTargetEmpRating.put(rs.getString("emp_id")+"_"+rs.getString("goal_id")+"_"+rs.getString("goal_freq_id")+"_RATING", strCurrGoalORTargetRating+"");
						hmEmpwiseGoalAndTargetEmpRating.put(rs.getString("emp_id")+"_"+rs.getString("goal_id")+"_"+rs.getString("goal_freq_id")+"_COUNT", rs.getInt("cnt")+"");
					}
				}
				rs.close();
				pst.close();
			}
//			System.out.println("hmTargetRatingAndComment ===>> " + hmTargetRatingAndComment);
			request.setAttribute("hmEmpwiseGoalAndTargetEmpRating", hmEmpwiseGoalAndTargetEmpRating);
				
			Map<String,String> hmKraCompletedPercentage = new HashMap<String,String>();
			
			Map<String, String> hmKRATaskStatusAndRating = new HashMap<String, String>();
			Map<String, String> hmEmpwiseKRARating = new HashMap<String, String>();
			Map<String, String> hmEmpwiseGoalRating = new HashMap<String, String>();
			if(sbEmpId != null) {
				pst=con.prepareStatement("select gksrd.*, gk.goal_id from goal_kra_status_rating_details gksrd, goal_kras gk where gksrd.kra_id = gk.goal_kra_id and gksrd.emp_id in ("+sbEmpId.toString()+")");
				rs=pst.executeQuery();
				while (rs.next()) {
					
					if((rs.getString("manager_rating") != null && !rs.getString("manager_rating").equals("")) || (rs.getString("hr_rating") != null && !rs.getString("hr_rating").equals("")) ) {
						double strTaskRating = uF.parseToDouble(hmEmpwiseKRARating.get(rs.getString("emp_id")+"_"+rs.getString("goal_id")+"_"+rs.getString("goal_freq_id")+"_"+rs.getString("kra_id")+"_RATING"));
						int strTaskCount = uF.parseToInt(hmEmpwiseKRARating.get(rs.getString("emp_id")+"_"+rs.getString("goal_id")+"_"+rs.getString("goal_freq_id")+"_"+rs.getString("kra_id")+"_COUNT"));
						strTaskCount++;
						double strCurrTaskRating = (uF.parseToDouble(rs.getString("manager_rating")) + uF.parseToDouble(rs.getString("hr_rating"))) / 2;
						if(rs.getString("manager_rating") == null) {
							strCurrTaskRating = uF.parseToDouble(rs.getString("hr_rating"));
						} else if(rs.getString("hr_rating") == null) {
							strCurrTaskRating = uF.parseToDouble(rs.getString("manager_rating"));
						}
						strTaskRating += strCurrTaskRating;
						hmEmpwiseKRARating.put(rs.getString("emp_id")+"_"+rs.getString("goal_id")+"_"+rs.getString("goal_freq_id")+"_"+rs.getString("kra_id")+"_RATING", strTaskRating+"");
						hmEmpwiseKRARating.put(rs.getString("emp_id")+"_"+rs.getString("goal_id")+"_"+rs.getString("goal_freq_id")+"_"+rs.getString("kra_id")+"_COUNT", strTaskCount+"");
						
						double strGoalwiseTaskRating = uF.parseToDouble(hmEmpwiseGoalRating.get(rs.getString("emp_id")+"_"+rs.getString("goal_id")+"_"+rs.getString("goal_freq_id")+"_RATING"));
						int strGoalwiseTaskCount = uF.parseToInt(hmEmpwiseGoalRating.get(rs.getString("emp_id")+"_"+rs.getString("goal_id")+"_"+rs.getString("goal_freq_id")+"_COUNT"));
						strGoalwiseTaskCount++;
						double strGoalwiseCurrTaskRating = (uF.parseToDouble(rs.getString("manager_rating")) + uF.parseToDouble(rs.getString("hr_rating"))) / 2;
						if(rs.getString("manager_rating") == null) {
							strGoalwiseCurrTaskRating = uF.parseToDouble(rs.getString("hr_rating"));
						} else if(rs.getString("hr_rating") == null) {
							strGoalwiseCurrTaskRating = uF.parseToDouble(rs.getString("manager_rating"));
						}
						strGoalwiseTaskRating += strGoalwiseCurrTaskRating;
						hmEmpwiseGoalRating.put(rs.getString("emp_id")+"_"+rs.getString("goal_id")+"_"+rs.getString("goal_freq_id")+"_RATING", strGoalwiseTaskRating+"");
						hmEmpwiseGoalRating.put(rs.getString("emp_id")+"_"+rs.getString("goal_id")+"_"+rs.getString("goal_freq_id")+"_COUNT", strGoalwiseTaskCount+"");
					}
					
					int totalTaskCount = uF.parseToInt(hmKraCompletedPercentage.get(rs.getString("emp_id")+"_"+rs.getString("goal_id")+"_"+rs.getString("goal_freq_id")+"_TASKCOUNT"));
					totalTaskCount++;
					
					double kraPercentage = uF.parseToDouble(hmKraCompletedPercentage.get(rs.getString("emp_id")+"_"+rs.getString("goal_id")+"_"+rs.getString("goal_freq_id")+"_PERCENTAGE"));
					kraPercentage = kraPercentage + uF.parseToDouble(rs.getString("complete_percent"));
					
					hmKraCompletedPercentage.put(rs.getString("emp_id")+"_"+rs.getString("goal_id")+"_"+rs.getString("goal_freq_id")+"_TASKCOUNT",""+totalTaskCount);
					hmKraCompletedPercentage.put(rs.getString("emp_id")+"_"+rs.getString("goal_id")+"_"+rs.getString("goal_freq_id")+"_PERCENTAGE",""+kraPercentage );
					
					hmKRATaskStatusAndRating.put(rs.getString("emp_id")+"_"+rs.getString("goal_id")+"_"+rs.getString("goal_freq_id")+"_"+rs.getString("kra_id")+"_"+rs.getString("kra_task_id")+"_STATUS", rs.getString("complete_percent"));
					hmKRATaskStatusAndRating.put(rs.getString("emp_id")+"_"+rs.getString("goal_id")+"_"+rs.getString("goal_freq_id")+"_"+rs.getString("kra_id")+"_"+rs.getString("kra_task_id")+"_MGR_RATING", rs.getString("manager_rating"));
					hmKRATaskStatusAndRating.put(rs.getString("emp_id")+"_"+rs.getString("goal_id")+"_"+rs.getString("goal_freq_id")+"_"+rs.getString("kra_id")+"_"+rs.getString("kra_task_id")+"_MGR_COMMENT", rs.getString("manager_comment"));
					hmKRATaskStatusAndRating.put(rs.getString("emp_id")+"_"+rs.getString("goal_id")+"_"+rs.getString("goal_freq_id")+"_"+rs.getString("kra_id")+"_"+rs.getString("kra_task_id")+"_HR_RATING", rs.getString("hr_rating"));
					hmKRATaskStatusAndRating.put(rs.getString("emp_id")+"_"+rs.getString("goal_id")+"_"+rs.getString("goal_freq_id")+"_"+rs.getString("kra_id")+"_"+rs.getString("kra_task_id")+"_HR_COMMENT", rs.getString("hr_comment"));
				}
				rs.close();
				pst.close();
			} 

			Map<String, String> hmKRATaskEmpRating = new HashMap<String, String>();
			Map<String, String> hmEmpwiseKRAEmpRating = new HashMap<String, String>();
			Map<String, String> hmEmpwiseGoalEmpRating = new HashMap<String, String>();
			if(sbEmpId != null) {
				pst = con.prepareStatement("select sum(gksrd.user_rating) as user_rating, count(*) as cnt, gk.goal_id, gksrd.emp_id, " +
					" gksrd.kra_id, gksrd.kra_task_id,gksrd.goal_freq_id from goal_kra_emp_status_rating_details gksrd, goal_kras gk where " +
					" gksrd.kra_id = gk.goal_kra_id and gksrd.emp_id in ("+sbEmpId.toString()+") and user_type != '-' " +
					" group by gksrd.goal_freq_id,gk.goal_id,gksrd.emp_id,gksrd.kra_id,gksrd.kra_task_id");
				rs = pst.executeQuery();
				while (rs.next()) {
					
					if((rs.getString("user_rating") != null && !rs.getString("user_rating").equals("")) ) {
						double strTaskRating = uF.parseToDouble(hmEmpwiseKRAEmpRating.get(rs.getString("emp_id")+"_"+rs.getString("goal_id")+"_"+rs.getString("goal_freq_id")+"_"+rs.getString("kra_id")+"_RATING"));
						int strTaskCount = uF.parseToInt(hmEmpwiseKRAEmpRating.get(rs.getString("emp_id")+"_"+rs.getString("goal_id")+"_"+rs.getString("goal_freq_id")+"_"+rs.getString("kra_id")+"_COUNT"));
						strTaskCount++;
						double strCurrTaskRating = rs.getDouble("user_rating")/ rs.getInt("cnt");
						
						hmKRATaskEmpRating.put(rs.getString("emp_id")+"_"+rs.getString("goal_id")+"_"+rs.getString("goal_freq_id")+"_"+rs.getString("kra_id")+"_"+rs.getString("kra_task_id")+"_RATING", strCurrTaskRating+"");
						hmKRATaskEmpRating.put(rs.getString("emp_id")+"_"+rs.getString("goal_id")+"_"+rs.getString("goal_freq_id")+"_"+rs.getString("kra_id")+"_"+rs.getString("kra_task_id")+"_COUNT", rs.getInt("cnt")+"");
						
						strTaskRating += strCurrTaskRating;
						hmEmpwiseKRAEmpRating.put(rs.getString("emp_id")+"_"+rs.getString("goal_id")+"_"+rs.getString("goal_freq_id")+"_"+rs.getString("kra_id")+"_RATING", strTaskRating+"");
						hmEmpwiseKRAEmpRating.put(rs.getString("emp_id")+"_"+rs.getString("goal_id")+"_"+rs.getString("goal_freq_id")+"_"+rs.getString("kra_id")+"_COUNT", strTaskCount+"");
						
						
						double strGoalwiseTaskRating = uF.parseToDouble(hmEmpwiseGoalEmpRating.get(rs.getString("emp_id")+"_"+rs.getString("goal_id")+"_"+rs.getString("goal_freq_id")+"_RATING"));
						int strGoalwiseTaskCount = uF.parseToInt(hmEmpwiseGoalEmpRating.get(rs.getString("emp_id")+"_"+rs.getString("goal_id")+"_"+rs.getString("goal_freq_id")+"_COUNT"));
						strGoalwiseTaskCount++;
						double strGoalwiseCurrTaskRating = rs.getDouble("user_rating")/ rs.getInt("cnt");
						strGoalwiseTaskRating += strGoalwiseCurrTaskRating;
						hmEmpwiseGoalEmpRating.put(rs.getString("emp_id")+"_"+rs.getString("goal_id")+"_"+rs.getString("goal_freq_id")+"_RATING", strGoalwiseTaskRating+"");
						hmEmpwiseGoalEmpRating.put(rs.getString("emp_id")+"_"+rs.getString("goal_id")+"_"+rs.getString("goal_freq_id")+"_COUNT", strGoalwiseTaskCount+"");
					}
					
				}
				rs.close();
				pst.close();
			}
			
			
			request.setAttribute("hmKraCompletedPercentage", hmKraCompletedPercentage);
			
			request.setAttribute("hmEmpwiseGoalRating", hmEmpwiseGoalRating);
			request.setAttribute("hmEmpwiseKRARating", hmEmpwiseKRARating);
			
			request.setAttribute("hmEmpwiseGoalEmpRating", hmEmpwiseGoalEmpRating);
			request.setAttribute("hmEmpwiseKRAEmpRating", hmEmpwiseKRAEmpRating);
//			System.out.println("hmEmpwiseGoalEmpRating ====>>> " + hmEmpwiseGoalEmpRating);
//			System.out.println("hmEmpwiseKRAEmpRating ====>>> " + hmEmpwiseKRAEmpRating);
			
			request.setAttribute("hmKRATaskStatusAndRating", hmKRATaskStatusAndRating);
			
			request.setAttribute("hmKRATaskEmpRating", hmKRATaskEmpRating);
//			System.out.println("hmKRATaskEmpRating ====>>> " + hmKRATaskEmpRating);
			
			request.setAttribute("hmKraAverage", hmKraAverage);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	
	public void checkGoalKRATargetStatus(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try { 
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from target_details");
			rs = pst.executeQuery();
			List<String> alCheckList = new ArrayList<String>();
			while (rs.next()) {
				if(!alCheckList.contains(rs.getString("goal_id"))) {
					alCheckList.add(rs.getString("goal_id"));
				}
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select goal_kra_target_id from question_bank where goal_kra_target_id is not null");
			rs = pst.executeQuery();
			while (rs.next()) {
				if(!alCheckList.contains(rs.getString("goal_kra_target_id"))) {
					alCheckList.add(rs.getString("goal_kra_target_id"));
				}
			}
			rs.close();
			pst.close();
			
			request.setAttribute("alCheckList", alCheckList);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	void getEmpTargetDetails(UtilityFunctions uF,List<String> empList) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			Map<String, String> hmEmpCodeName =CF.getEmpNameMap(con, null, null);
			request.setAttribute("hmEmpCodeName", hmEmpCodeName);

			Map<String, String> hmTargetValue= new HashMap<String,String>();
			Map<String, String> hmTargetID= new HashMap<String,String>();
			Map<String, String> hmTargetRemark= new HashMap<String,String>();
			Map<String, String> hmTargetTmpValue= new HashMap<String,String>();
			Map<String, String> hmUpdateBy= new HashMap<String,String>();
			pst = con.prepareStatement("select * from target_details where target_id in (select max(target_id) from target_details group by goal_id,emp_id,goal_freq_id)");
			rs= pst.executeQuery();
			while(rs.next()) {
				
				hmTargetValue.put(rs.getString("emp_id")+"_"+rs.getString("goal_id")+"_"+rs.getString("goal_freq_id"), rs.getString("amt_percentage"));
				hmTargetID.put(rs.getString("emp_id")+"_"+rs.getString("goal_id")+"_"+rs.getString("goal_freq_id"), rs.getString("target_id"));
				hmTargetRemark.put(rs.getString("emp_id")+"_"+rs.getString("goal_id")+"_"+rs.getString("goal_freq_id"), rs.getString("target_remark"));
				hmTargetTmpValue.put(rs.getString("emp_id")+"_"+rs.getString("goal_id")+"_"+rs.getString("goal_freq_id"), rs.getString("emp_amt_percentage"));
				hmUpdateBy.put(rs.getString("emp_id")+"_"+rs.getString("goal_id")+"_"+rs.getString("goal_freq_id"), hmEmpCodeName.get(rs.getString("added_by"))+" on "+uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));
			}
			rs.close();
			pst.close();
//			System.out.println("hmTargetValue ===>> " + hmTargetValue);
			pst=con.prepareStatement("select emp_image,emp_per_id from employee_personal_details ");
			rs=pst.executeQuery();
			Map<String,String> empImageMap=new HashMap<String,String>();
			while(rs.next()) {
				empImageMap.put(rs.getString("emp_per_id"),rs.getString("emp_image"));
			}
			rs.close();
			pst.close();
			
			request.setAttribute("empImageMap", empImageMap);
			request.setAttribute("hmUpdateBy", hmUpdateBy);
			request.setAttribute("hmTargetValue", hmTargetValue);
			request.setAttribute("hmTargetID", hmTargetID);
			request.setAttribute("hmTargetRemark", hmTargetRemark);
			
			request.setAttribute("hmTargetTmpValue", hmTargetTmpValue);
		
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}


	void getEmpKRADetails(UtilityFunctions uF,List<String> empList) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			con = db.makeConnection(con);

			Map<String,String> hmEmpName=CF.getEmpNameMap(con, null,null);	
			request.setAttribute("hmEmpName", hmEmpName);
			Map<String, List<List<String>>> hmEmpKra= new HashMap<String, List<List<String>>>(); 
			
			for(int i=0; empList!=null && !empList.isEmpty() && i<empList.size(); i++) {
				
				StringBuilder sbQuery = new StringBuilder();
				sbQuery.append("select * from goal_kras where goal_type = "+EMPLOYEE_KRA+" and is_assign = true and emp_ids like '%,"+empList.get(i)+",%' ");
				if(getDataType() != null && getDataType().equals("L")) {
					sbQuery.append(" and is_close = false ");
				} else if(getDataType() != null && getDataType().equals("C")) {
					sbQuery.append(" and is_close = true ");
				}
				pst=con.prepareStatement(sbQuery.toString());
//				System.out.println("pst == >> " +pst);
				rs = pst.executeQuery();
				while (rs.next()) {
					List<List<String>> outerList = hmEmpKra.get(empList.get(i));
					if(outerList==null) outerList = new ArrayList<List<String>>();
					
					List<String> innerList=new ArrayList<String>();
					innerList.add(rs.getString("goal_kra_id"));
					innerList.add(empList.get(i));
					innerList.add(rs.getString("kra_description"));	
					innerList.add(uF.showData(uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()),""));
					innerList.add(uF.showData(uF.getDateFormat(rs.getString("effective_date"), DBDATE, CF.getStrReportDateFormat()),"-"));
					innerList.add(rs.getString("is_approved"));
					innerList.add(rs.getString("approved_by"));
					innerList.add(rs.getString("kra_order"));
					
					if(uF.parseToInt(strSessionEmpId)>0 && uF.parseToInt(strSessionEmpId)==uF.parseToInt(rs.getString("added_by"))) {
						innerList.add("Self");
					} else {
						innerList.add(uF.showData(hmEmpName.get(rs.getString("added_by")),"-"));
					}
					innerList.add(rs.getString("is_close")); //9
					innerList.add(rs.getString("kra_weightage")); //10
					
					outerList.add(innerList);
					hmEmpKra.put(empList.get(i), outerList);
				}
				rs.close();
				pst.close();
			}
			
			pst=con.prepareStatement("select emp_image,emp_per_id from employee_personal_details ");
			rs=pst.executeQuery();
			Map<String,String> empImageMap=new HashMap<String,String>();
			while(rs.next()) {
				empImageMap.put(rs.getString("emp_per_id"),rs.getString("emp_image"));
			}
			rs.close();
			pst.close();
			
			request.setAttribute("empImageMap", empImageMap);
//			System.out.println("hmEmpKra ====>>> " + hmEmpKra);
			request.setAttribute("hmEmpKra", hmEmpKra);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	
	void getEmpGoalKRADetails(UtilityFunctions uF, List<String> empList) {
		Connection con = null;
		PreparedStatement pst = null;
		PreparedStatement pst1 = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			
			con = db.makeConnection(con);
						
			String strOrgCurrId = CF.getOrgCurrencyIdByOrg(con, getF_org());
			Map<String, Map<String, String>> hmCurrencyDetailsMap = CF.getCurrencyDetailsForPDF(con);
			if(hmCurrencyDetailsMap == null) hmCurrencyDetailsMap = new HashMap<String, Map<String, String>>();
			String strCurrency = "";
			if(uF.parseToInt(strOrgCurrId) > 0) {
				Map<String, String> hmCurrency = hmCurrencyDetailsMap.get(strOrgCurrId);
				if(hmCurrency == null) hmCurrency = new HashMap<String, String>();
				strCurrency = uF.showData(hmCurrency.get("SHORT_CURR"), ""); 
			}
			request.setAttribute("strCurrency", strCurrency);
			
			Map<String, List<List<String>>> hmGoalKra = new LinkedHashMap<String, List<List<String>>>();
			
			Map<String, Map<String, List<List<String>>>> hmGoalKraSuperIdwise = new LinkedHashMap<String, Map<String, List<List<String>>>>();
			Map<String, Map<String, Map<String, List<List<String>>>>> hmGoalKraEmpwise = new LinkedHashMap<String, Map<String, Map<String, List<List<String>>>>>();
			Map<String, List<String>> hmGoalDetails = new LinkedHashMap<String, List<String>>();
			Map<String, List<String>> hmGoalKraPerspective = new LinkedHashMap<String, List<String>>();
//			Map<String, List<List<String>>> hmIndividualGoalKra = new HashMap<String, List<List<String>>>();
//			Map<String, Map<String, List<List<String>>>> hmIndividualGoalKraSuperIdwise = new HashMap<String, Map<String, List<List<String>>>>();
//			Map<String, Map<String, Map<String, List<List<String>>>>> hmIndividualGoalKraEmpwise = new LinkedHashMap<String, Map<String, Map<String, List<List<String>>>>>();
//			Map<String, List<String>> hmIndividualGoalDetails = new LinkedHashMap<String, List<String>>();
			
			Map<String, String> hmEmpwiseKRACnt = new HashMap<String, String>();
			
			Map<String, String> hmGoalName = CF.getGoalNameMap(con, uF);
			Map<String,String> hmAttribute = CF.getAttributeMap(con);
			Map<String, String> hmEmpCodeName =CF.getEmpNameMap(con, null, null);
			Map<String, String> hmEmpUserId = CF.getEmpUserIdMap(con);
			Map<String, String> hmUserTypeMap = CF.getUserTypeMap(con);
			//Map<String, String> hmworkingHrsByLocation = CF.getWorkLocationWorkingHrs(con);
//			Map<String, String> hmEmpWlocationMap = CF.getEmpWlocationMap(con);
			
			request.setAttribute("hmEmpUserId",hmEmpUserId);
			request.setAttribute("hmUserTypeMap",hmUserTypeMap);
//			request.setAttribute("hmworkingHrsByLocation",hmworkingHrsByLocation);
			
			Map<String, Map<String, String>> hmPerspectiveData = new HashMap<String, Map<String, String>>();
			pst = con.prepareStatement("Select * from bsc_perspective_details");
			rs = pst.executeQuery();
			while (rs.next()) {
				Map<String, String> hm = new HashMap<String, String>();
				hm.put("PERSPECTIVE_ID", rs.getString("bsc_perspective_id"));
				hm.put("PERSPECTIVE_NAME", rs.getString("bsc_perspective_name"));
				hm.put("PERSPECTIVE_WEIGHTAGE", rs.getString("weightage"));
				hm.put("PERSPECTIVE_DESCRIPTION", rs.getString("perspective_description"));
				hm.put("PERSPECTIVE_COLOR", rs.getString("perspective_color"));

				hmPerspectiveData.put(rs.getString("bsc_perspective_id"), hm);
			}
			rs.close();
			pst.close();
			request.setAttribute("hmPerspectiveData", hmPerspectiveData);
//			System.out.println("hmPerspectiveData ===>> " + hmPerspectiveData);
			
			Map<String, List<List<String>>> hmKRATasks = new LinkedHashMap<String, List<List<String>>>();
			
			for(int i=0;empList!=null && !empList.isEmpty() && i<empList.size();i++) {
				
				String goalTyp = INDIVIDUAL_GOAL+","+INDIVIDUAL_KRA+","+INDIVIDUAL_TARGET+","+PERSONAL_GOAL+","+EMPLOYEE_KRA;
				StringBuilder sbQuery = new StringBuilder();
//				sbQuery.append("select gd.*, gk.kra_description,gk.goal_kra_id,gk.is_assign,gk.kra_weightage from goal_details gd left join " +
//					" goal_kras gk on gd.goal_id=gk.goal_id where gd.emp_ids like '%,"+empList.get(i)+ ",%' and gd.goal_type in ("+goalTyp+") ");
				/*sbQuery.append("select freq_end_date, freq_start_date, gd.*, gk.kra_description, gk.goal_kra_id, gk.is_assign, gk.kra_weightage, gdf.goal_freq_id, " +
					" gdf.goal_freq_name from goal_details gd left join goal_kras gk on gd.goal_id=gk.goal_id join goal_details_frequency gdf on gd.goal_id=gdf.goal_id " +
					" where gd.emp_ids like '%,"+empList.get(i)+ ",%' and gd.goal_type in ("+goalTyp+") and (gdf.is_delete is null or gdf.is_delete = false)");
				*/
				System.out.println("getFromPage::"+getFromPage());
				sbQuery.append("select freq_end_date,freq_start_date,gd.*, gk.kra_description,gk.goal_kra_id,gk.is_assign,gk.kra_weightage,gdf.goal_freq_id " +
				",gdf.goal_freq_name from goal_details gd, goal_kras gk,goal_details_frequency gdf where gd.goal_id=gk.goal_id and gd.goal_id=gdf.goal_id " +
				" and gd.emp_ids like '%,"+empList.get(i)+ ",%' and gd.goal_type in ("+goalTyp+") ");
				
				if(getFromPage() != null && getFromPage().equals("MYGKT")) {
					sbQuery.append(" and (gd.peer_ids like '%,"+strSessionEmpId+",%' or gd.anyone_ids like '%,"+strSessionEmpId+",%' or gd.emp_ids like '%,"+strSessionEmpId+ ",%') ");
				}
				if(getDataType() != null && getDataType().equals("L")) {
					sbQuery.append(" and gd.is_close = false ");
				} else if(getDataType() != null && getDataType().equals("C")) {
					sbQuery.append(" and gd.is_close = true ");
				}
				
				sbQuery.append(" order by freq_start_date");
				pst=con.prepareStatement(sbQuery.toString());
//				pst.setInt(1, INDIVIDUAL_GOAL);
//				pst.setInt(2, INDIVIDUAL_KRA);
//				System.out.println("select pst====>"+pst);
				rs = pst.executeQuery();
				while (rs.next()) {
					if(rs.getInt("goal_type") == EMPLOYEE_KRA && rs.getString("is_assign") != null && rs.getString("is_assign").equals("f")) {
						continue;
					}
					
	//				System.out.println("hmEmpwiseKRACnt::"+hmEmpwiseKRACnt);
					
					int kraCnt = uF.parseToInt(hmEmpwiseKRACnt.get(empList.get(i)));
					kraCnt++;
					String strCnt = kraCnt+"";
					hmEmpwiseKRACnt.put(empList.get(i), strCnt);
					
					hmGoalKraSuperIdwise = hmGoalKraEmpwise.get(empList.get(i));
					if(hmGoalKraSuperIdwise == null) hmGoalKraSuperIdwise = new LinkedHashMap<String, Map<String, List<List<String>>>>();
					
					String superId = rs.getString("super_id");
					if(uF.parseToInt(superId) == 0) {
						//superId = rs.getString("goal_freq_id");
						
					}
					
//					System.out.println("superId=>"+superId);
					hmGoalKra = hmGoalKraSuperIdwise.get(superId);
					if(hmGoalKra == null) hmGoalKra = new LinkedHashMap<String, List<List<String>>>();
					
					List<List<String>> outerList = hmGoalKra.get(rs.getString("goal_id")+"_"+rs.getString("goal_freq_id"));
					if(outerList==null) outerList = new ArrayList<List<String>>();
					
					List<String> innerList=new ArrayList<String>();
					innerList.add(empList.get(i));
					innerList.add(rs.getString("goal_id"));
					innerList.add(rs.getString("kra_description"));
					innerList.add(rs.getString("goal_title"));
					if(uF.parseToInt(strSessionEmpId)>0 && uF.parseToInt(strSessionEmpId)==uF.parseToInt(rs.getString("user_id"))) {
						innerList.add("Self");
					} else {
						innerList.add(uF.showData(hmEmpCodeName.get(rs.getString("user_id")), "-"));
					}
					
					innerList.add(uF.showData(uF.getDateFormat(rs.getString("freq_end_date"), DBDATE, CF.getStrReportDateFormat()),"-")); //5
					
					String tGoalId = getPerentGoalId(con, uF, rs.getString("goal_id"));
					innerList.add(tGoalId); // team goal id 6
					String mGoalId = getPerentGoalId(con, uF, tGoalId);
					innerList.add(mGoalId); // manager goal id 7
					String cGoalId = getPerentGoalId(con, uF, mGoalId);
					innerList.add(cGoalId); // Corporate goal id 8
					String priority="";
					String pClass="";
					if(rs.getString("priority")!=null && !rs.getString("priority").equals("")) {
						if(rs.getString("priority").equals("1")) {
							pClass="high";
							priority="High";
						} else if(rs.getString("priority").equals("2")) {
							pClass="medium";
							priority="Medium";
						} else if(rs.getString("priority").equals("3")) {
							pClass="low";
							priority="Low";
						}
					}
					innerList.add(priority); // Priority 9
					innerList.add(pClass); // Priority class 10
					innerList.add(rs.getString("goal_kra_id")); //11
					innerList.add(rs.getString("is_close")); //12
					innerList.add(rs.getString("goal_objective")); //13
					innerList.add(uF.showData(hmAttribute.get(rs.getString("goal_attribute")),"")); //14
					innerList.add(uF.getDateFormat(rs.getString("freq_start_date"), DBDATE, CF.getStrReportDateFormat()) ); //15
					
					innerList.add(rs.getString("weightage")); //16

					innerList.add(rs.getString("goal_parent_id")); //17
					innerList.add(rs.getString("goal_type")); //18
					innerList.add(rs.getString("measure_type")); //19
					innerList.add(rs.getString("measure_kra")); //20
					String val="",daysHRVal="";
					if(rs.getString("measure_type")!=null && rs.getString("measure_type").equals("Effort")) {
						val=" "+rs.getString("measure_effort_days")+" Days and "+rs.getString("measure_effort_hrs")+" Hrs.";
						daysHRVal = (rs.getString("measure_effort_days") != null && !rs.getString("measure_effort_days").equals("") ? rs.getString("measure_effort_days") : "0")+"."
						+(rs.getString("measure_effort_hrs") != null && !rs.getString("measure_effort_hrs").equals("") ? rs.getString("measure_effort_hrs") : "0");
					} else {
						val= uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(rs.getString("measure_currency_value")));
					}
					innerList.add(val); //21
					innerList.add(daysHRVal); //22

					if(rs.getString("freq_end_date") != null) {
						Date dtDeadLine = uF.getDateFormat(rs.getString("freq_end_date"), DBDATE);
						Date dtCurrDate = uF.getCurrentDate(CF.getStrTimeZone());
						if(dtCurrDate.after(dtDeadLine)) {
							innerList.add("1"); //23
						} else {
							innerList.add("0"); //23
						}
					} else {
						innerList.add("0"); //23
					}
					innerList.add(rs.getString("goalalign_with_teamgoal")); //24
					innerList.add(uF.showData(hmGoalName.get(rs.getString("goal_parent_id")), "")); //25
					
					innerList.add(rs.getString("close_reason")); //26
					innerList.add(rs.getString("kra_weightage")); //27
					innerList.add(rs.getString("peer_ids")); //28
					innerList.add(rs.getString("manager_ids")); //29
					innerList.add(rs.getString("hr_ids")); //30
					innerList.add(rs.getString("anyone_ids")); //31
					innerList.add(rs.getString("goal_freq_id")); //32
					innerList.add(rs.getString("goal_freq_name")); //33
					innerList.add(rs.getString("orientation_id")); //34
					innerList.add(rs.getString("emp_ids")); //35
					innerList.add(rs.getString("user_id"));//36
					String strStatusAndCount = getAllEmpFinalisationStatus(con, uF, rs.getString("goal_id"), rs.getString("goal_freq_id"), rs.getString("emp_ids"), empList.get(i));
					String tempStr[] = strStatusAndCount.split("::::");
					innerList.add(tempStr[0]);//37
					innerList.add(tempStr[1]);//38
					innerList.add(rs.getString("perspective_id"));//39
					innerList.add(rs.getString("align_with_perspective"));//40
					innerList.add(rs.getString("org_id"));//41
					
					outerList.add(innerList);
					hmGoalKra.put(rs.getString("goal_id")+"_"+rs.getString("goal_freq_id"), outerList);
					hmGoalDetails.put(rs.getString("goal_id")+"_"+rs.getString("goal_freq_id"), innerList);
					hmGoalKraPerspective.put(rs.getString("goal_id"), innerList);
					hmGoalKraSuperIdwise.put(superId, hmGoalKra);
					hmGoalKraEmpwise.put(empList.get(i), hmGoalKraSuperIdwise);
				}
				rs.close();
				pst.close();
				
//				System.out.println("hmGoalKra111==>"+hmGoalKra);
				/*if(hmGoalKraSuperIdwise != null && hmGoalKraSuperIdwise.size()>0) {
					System.out.println("==>hmGoalKraSuperIdwise==>"+hmGoalKraSuperIdwise.size());
				}*/
				//updated by kalpana on 19/10/2016  start
				pst = con.prepareStatement("select gkt.goal_kra_task_id, gkt.task_name,gkt.added_by,gkt.kra_id,gdf.goal_id,gdf.goal_freq_id,gdf.freq_end_date" +
					" from goal_kra_tasks gkt, goal_details_frequency gdf where gkt.goal_id = gdf.goal_id and (gdf.is_delete is null or gdf.is_delete = false) and gkt.emp_ids like '%,"+empList.get(i)+",%' order by goal_kra_task_id");
//				System.out.println("pst==>"+pst);
				rs = pst.executeQuery();
				while (rs.next()) {
					List<List<String>> outerList = hmKRATasks.get(empList.get(i)+"_"+rs.getString("goal_id")+"_"+rs.getString("goal_freq_id")+"_"+rs.getString("kra_id"));
					if (outerList == null) outerList = new ArrayList<List<String>>();
					
					List<String> innerList = new ArrayList<String>();
					innerList.add(rs.getString("goal_kra_task_id"));//0
					innerList.add(rs.getString("task_name"));//1
//					System.out.println("task_id==>"+rs.getString("goal_kra_task_id")+"==>task_name==>"+rs.getString("task_name"));
					if(rs.getString("freq_end_date") != null) {
						Date dtDeadLine = uF.getDateFormat(rs.getString("freq_end_date"), DBDATE);
						Date dtCurrDate = uF.getCurrentDate(CF.getStrTimeZone());
						if(dtCurrDate.after(dtDeadLine)) {
							innerList.add("1");//2
						} else {
							innerList.add("0");//2
						}
					}
					innerList.add("0");//3
					innerList.add(rs.getString("added_by"));//4
					outerList.add(innerList);
//					System.out.println("task key==>"+empList.get(i)+"_"+rs.getString("goal_id")+"_"+rs.getString("goal_freq_id")+"_"+rs.getString("kra_id"));
					hmKRATasks.put(empList.get(i)+"_"+rs.getString("goal_id")+"_"+rs.getString("goal_freq_id")+"_"+rs.getString("kra_id"), outerList);
				}
				rs.close();
				pst.close();

				request.setAttribute("hmKRATasks", hmKRATasks);
			}
			
			request.setAttribute("hmGoalKraEmpwise", hmGoalKraEmpwise);
//			System.out.println("hmGoalKraEmpwise ===>> " + hmGoalKraEmpwise);
			request.setAttribute("hmGoalDetails", hmGoalDetails);
			request.setAttribute("hmGoalKraPerspective", hmGoalKraPerspective);
			request.setAttribute("hmEmpwiseKRACnt", hmEmpwiseKRACnt);
			

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	
	private String getAllEmpFinalisationStatus(Connection con, UtilityFunctions uF, String goalId, String goalFreqId, String empIds, String empId) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		String strStatusAndCount = "NO::::0";
		try {
			String query1 = "select count(distinct(emp_id)) as cnt from goal_kra_target_finalization where goal_id=? and goal_freq_id=? and emp_id != ?";
			pst = con.prepareStatement(query1);
			pst.setInt(1, uF.parseToInt(goalId));
			pst.setInt(2, uF.parseToInt(goalFreqId));
			pst.setInt(3, uF.parseToInt(empId));
			rs = pst.executeQuery();
			int empCount = 0;
			while (rs.next()) {
				empCount = rs.getInt("cnt");
			}
			rs.close();
			pst.close();
			empCount++;
			String temEmpIds = (empIds != null && empIds.length()>0) ? empIds.substring(1, empIds.length()-1) : "";
			List<String> alEmpIds = new ArrayList<String>();
			if(temEmpIds != null && !temEmpIds.equals("")) {
				alEmpIds = Arrays.asList(temEmpIds.split(","));
			}
			if(alEmpIds != null && !alEmpIds.isEmpty() && alEmpIds.size() == empCount) {
				strStatusAndCount = "YES::::0";
			} else {
				int remainEmp = alEmpIds.size() - empCount;
				strStatusAndCount = "NO::::"+remainEmp;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return strStatusAndCount;
	}
	

	private String getPerentGoalId(Connection con, UtilityFunctions uF, String goalID) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		String goalId = null;
		try {
			String query1 = "select goal_parent_id from goal_details where goal_id = ?";
			pst = con.prepareStatement(query1);
			pst.setInt(1, uF.parseToInt(goalID));
			rs = pst.executeQuery();
			while (rs.next()) {
				goalId = rs.getString("goal_parent_id");
			}
			rs.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return goalId;
	}
	
	
	 void  getEmployeeData(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			con = db.makeConnection(con);

			Map<String, String> hmHrIds = new HashMap<String, String>();
			Map<String, String> hmEmpSuperIds = new HashMap<String, String>();
			Map<String, String> hmworkingHrsByLocation = CF.getWorkLocationWorkingHrs(con);
			Map<String, String> hmEmpWorkingHrs = new HashMap<String, String>();
			
			StringBuilder sbQuery=new StringBuilder();
			sbQuery.append("select * from employee_official_details eod,employee_personal_details epd where epd.emp_per_id=eod.emp_id and  epd.emp_per_id = ?");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				
				hmHrIds.put(rs.getString("emp_per_id"), rs.getString("emp_hr"));
				hmEmpSuperIds.put(rs.getString("emp_per_id"), rs.getString("supervisor_emp_id"));
				String empWorkingHrs = hmworkingHrsByLocation.get(rs.getString("wlocation_id"));
				hmEmpWorkingHrs.put(rs.getString("emp_per_id"), empWorkingHrs);
				
			}
			rs.close();
			pst.close();
		
			request.setAttribute("hmHrIds", hmHrIds);
			request.setAttribute("hmEmpSuperIds", hmEmpSuperIds);
			request.setAttribute("hmEmpWorkingHrs", hmEmpWorkingHrs);
	} catch (Exception e) {
		e.printStackTrace();
	} finally {
		db.closeResultSet(rs);
		db.closeStatements(pst);
		db.closeConnection(con);			
	}

	}



	private String loadKRAData(UtilityFunctions uF) {
		
		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)) {
			wLocationList = new FillWLocation(request).fillWLocation(getF_org(), (String)session.getAttribute(WLOCATION_ACCESS));
			orgList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
		} else {
			orgList = new FillOrganisation(request).fillOrganisation();
			wLocationList = new FillWLocation(request).fillWLocation(getF_org());
		}
		departmentList = new FillDepartment(request).fillDepartment(uF.parseToInt(getF_org()));
		levelList = new FillLevel(request).fillLevel(uF.parseToInt(getF_org()));

		if(strUserType != null && (strUserType.equals(HRMANAGER) || strUserType.equals(ADMIN) || (getCurrUserType() != null && getCurrUserType().equals(strBaseUserType)))) { 
    		getSelectedFilter(uF);
    	}
		
		return SUCCESS;
	}
	
	private void getSelectedFilter(UtilityFunctions uF) {
		Map<String,String> hmFilter=new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();

		alFilter.add("ORGANISATION");
		if(getF_org()!=null) {
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

	public String getExportType() {
		return exportType;
	}

	public void setExportType(String exportType) {
		this.exportType = exportType;
	}

	public String getStrSearchJob() {
		return strSearchJob;
	}

	public void setStrSearchJob(String strSearchJob) {
		this.strSearchJob = strSearchJob;
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

	public String getPerspective() {
		return strPerspective;
	}

	public void setPerspective(String strPerspective) {
		
		this.strPerspective = strPerspective;
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
	
	
}
