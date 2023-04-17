package com.konnect.jpms.performance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UserAlerts;
import com.konnect.jpms.util.UtilityFunctions;

public class GoalSummary implements ServletRequestAware, IStatements {

	public HttpSession session;
	String strSessionEmpId;
	String strEmpOrgId;
	String strUserType;
	String strBaseUserType;
	String strUserTypeId;
	public CommonFunctions CF;

//	private List<FillOrganisation> organisationList;
//	private String strOrg;
	private String f_org;
	
	private String dataType;
	private String currUserType;
	
	private String goalId;
	
	public String execute() {
		session = request.getSession();
		strSessionEmpId = (String) session.getAttribute(EMPID);
		strEmpOrgId = (String) session.getAttribute(ORGID);
		strUserType = (String) session.getAttribute(USERTYPE);
		strBaseUserType = (String) session.getAttribute(BASEUSERTYPE);
		strUserTypeId = (String) session.getAttribute(USERTYPEID);

		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null) 
			return "login";
		request.setAttribute(PAGE, "/jsp/performance/GoalSummary.jsp");
		request.setAttribute(TITLE, "Goals");
		UtilityFunctions uF = new UtilityFunctions();
	
		request.setAttribute("DOC_RETRIVE_LOCATION", CF.getStrDocRetriveLocation());
		
		if(getDataType() == null || getDataType().trim().equals("") || getDataType().trim().equalsIgnoreCase("NULL")) {
			setDataType("L");
		}
		if(getCurrUserType()==null && strUserType != null && strUserType.equals(MANAGER)) {
			setCurrUserType("MYTEAM");
		}
		
		getGoalTypeDetails(uF);
		getGoalSummary(uF);
		getGoalKRADetails(uF);
		getEmpImage(uF);
		
		getTargetProgressBarDetails(uF);
		calculateScore(uF);
		
		checkTargetStatus(uF);
		return "success" ;

	}	
	
	
	private void checkTargetStatus(UtilityFunctions uF) {
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
			StringBuilder sbGoalId = null;
			while (rs.next()) {
				if(!alCheckList.contains(rs.getString("goal_id"))) {
					alCheckList.add(rs.getString("goal_id"));
					if(sbGoalId == null) {
						sbGoalId = new StringBuilder();
						sbGoalId.append(rs.getString("goal_id"));
					} else {
						sbGoalId.append(","+rs.getString("goal_id"));
					}
				}
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select goal_kra_target_id from question_bank where goal_kra_target_id is not null");
			rs = pst.executeQuery();
			while (rs.next()) {
				if(!alCheckList.contains(rs.getString("goal_kra_target_id"))) {
					alCheckList.add(rs.getString("goal_kra_target_id"));
					if(sbGoalId == null) {
						sbGoalId = new StringBuilder();
						sbGoalId.append(rs.getString("goal_kra_target_id"));
					} else {
						sbGoalId.append(","+rs.getString("goal_kra_target_id"));
					}
				}
			}
			rs.close();
			pst.close();
			
			if(sbGoalId != null) {
				pst = con.prepareStatement("select * from goal_details where goal_id in ("+sbGoalId.toString()+") and goal_parent_id > 0");
				rs = pst.executeQuery();
				StringBuilder sbGoalId1 = null;
				while (rs.next()) {
					if(!alCheckList.contains(rs.getString("goal_parent_id"))) {
						alCheckList.add(rs.getString("goal_parent_id"));
						if(sbGoalId1 == null) {
							sbGoalId1 = new StringBuilder();
							sbGoalId1.append(rs.getString("goal_parent_id"));
						} else {
							sbGoalId1.append(","+rs.getString("goal_parent_id"));
						}
					}
				}
				rs.close();
				pst.close();
				
				if(sbGoalId1 != null) {
					pst = con.prepareStatement("select * from goal_details where goal_id in ("+sbGoalId1.toString()+") and goal_parent_id > 0");
					rs = pst.executeQuery();
					StringBuilder sbGoalId2 = null;
					while (rs.next()) {
						if(!alCheckList.contains(rs.getString("goal_parent_id"))) {
							alCheckList.add(rs.getString("goal_parent_id"));
							if(sbGoalId2 == null) {
								sbGoalId2 = new StringBuilder();
								sbGoalId2.append(rs.getString("goal_parent_id"));
							} else {
								sbGoalId2.append(","+rs.getString("goal_parent_id"));
							}
						}
					}
					rs.close();
					pst.close();
					
					if(sbGoalId2 != null) {
						pst = con.prepareStatement("select * from goal_details where goal_id in ("+sbGoalId2.toString()+") and goal_parent_id > 0");
						rs = pst.executeQuery();
						StringBuilder sbGoalId3 = null;
						while (rs.next()) {
							if(!alCheckList.contains(rs.getString("goal_parent_id"))) {
								alCheckList.add(rs.getString("goal_parent_id"));
								if(sbGoalId3 == null) {
									sbGoalId3 = new StringBuilder();
									sbGoalId3.append(rs.getString("goal_parent_id"));
								} else {
									sbGoalId3.append(","+rs.getString("goal_parent_id"));
								}
							}
						}
						rs.close();
						pst.close();
					}
				}
				
			}
//			System.out.println("alCheckList=======>"+alCheckList.toString());
			request.setAttribute("alCheckList", alCheckList);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	
	public void calculateScore(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		Map<String,String> parentScoreMp=new HashMap<String,String>();
		Map<String,String> goalScoreMp=new HashMap<String,String>();
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
			request.setAttribute("strCurrency",strCurrency);
			
			pst=con.prepareStatement("select * from goal_details where org_id= ? and is_close=?");
			pst.setInt(1, uF.parseToInt(getF_org()));
			pst.setBoolean(2,false);
			rs=pst.executeQuery();
			while(rs.next()) {
				double val=uF.parseToDouble(parentScoreMp.get(rs.getString("goal_type")+"_"+rs.getString("goal_parent_id")));
				val+=uF.parseToDouble(rs.getString("weightage"));
				parentScoreMp.put(rs.getString("goal_type")+"_"+rs.getString("goal_parent_id"),val+"");
				goalScoreMp.put(rs.getString("goal_id"),rs.getString("weightage"));
			}
			rs.close();
			pst.close();
			
//			System.out.println("==parentScoreMp= "+parentScoreMp);
//			System.out.println("==goalScoreMp= "+goalScoreMp);
			request.setAttribute("parentScoreMp",parentScoreMp);
			request.setAttribute("goalScoreMp",goalScoreMp);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	private void getTargetProgressBarDetails(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		Map<String,String> hmCorporateTargetValue=new HashMap<String,String>();
		Map<String,String> hmTargetedValue=new HashMap<String,String>();
		Map<String,Boolean> hmCorporateFlag=new HashMap<String,Boolean>();

		try {
			con = db.makeConnection(con);
			pst=con.prepareStatement("select * from goal_details where goal_type=1");
			rs=pst.executeQuery();
			Map<String,String> hmCorporateTarget=new HashMap<String,String>();
			while(rs.next()) {
				hmCorporateTarget.put(rs.getString("goal_id"),rs.getString("goal_id"));
			}
			rs.close();
			pst.close();
					
			if(!hmCorporateTarget.isEmpty()) {
				Iterator<String> it=hmCorporateTarget.keySet().iterator();
				while(it.hasNext()) {
					String key=it.next();
					String value=hmCorporateTarget.get(key);
					double amount=0;
					double targetedAmount=0;
					boolean flag=false;
					String managerID = null;
					pst=con.prepareStatement("select * from goal_details where  goal_type=2 and goal_parent_id=?");
					pst.setInt(1, uF.parseToInt(value));
					rs=pst.executeQuery();
					int i=0;
					while(rs.next()) {
						if(i==0) {
							managerID=rs.getString("goal_id");
						} else {
							managerID +=","+rs.getString("goal_id");
						}
							i++;			
					}
					rs.close();
					pst.close();
					
					if(managerID!=null) {
//						System.out.println("managerID====>"+managerID);
						String teamID = null;
						pst=con.prepareStatement("select * from goal_details where goal_type=3 and goal_parent_id in("+managerID+")");
						rs=pst.executeQuery();
						int j=0;
						while(rs.next()) {
							if(j==0) {
								teamID=rs.getString("goal_id");
							} else {
								teamID +=","+rs.getString("goal_id");
							}
							j++;				
						}
						rs.close();
						pst.close();
						
						if(teamID!=null) {
//							System.out.println("teamID====>"+teamID);
							String individualID = null;
							pst=con.prepareStatement("select sum(measure_currency_value)as amt,goal_id from goal_details where measure_type!='Effort' " +
									"and goal_type=4 and goal_parent_id in ("+teamID+") group by goal_id order by goal_id");
							rs=pst.executeQuery();
							int k=0;
							while(rs.next()) {
								if(k==0) {
									individualID=rs.getString("goal_id");
									amount=uF.parseToDouble(rs.getString("amt"));
								} else {
									amount +=uF.parseToDouble(rs.getString("amt"));
									individualID +=","+rs.getString("goal_id");
								}
								k++;				
							}	
							rs.close();
							pst.close();
//							System.out.println("individualID====>"+individualID);
							if(individualID!=null) { 
								
								pst=con.prepareStatement("select sum(amt_percentage)as amt from target_details where amt_percentage_type!='Effort' and goal_id in ("+individualID+")");
								rs=pst.executeQuery();
								int a=0;
								while(rs.next()) {
									if(a==0) {
										targetedAmount=uF.parseToDouble(rs.getString("amt"));
									} else {
										targetedAmount +=uF.parseToDouble(rs.getString("amt"));
									}
									a++;				
								}
								rs.close();
								pst.close();
								flag=true;
							}
						}
					}
					
					/*if(amount==0 || amount==0.0) {
						flag=false;
					} else {
						flag=true;
					}*/
					
					hmTargetedValue.put(key, ""+targetedAmount);
					hmCorporateTargetValue.put(key, ""+amount);
					hmCorporateFlag.put(key, flag);
				}
			} 
			request.setAttribute("hmCorporateTargetValue", hmCorporateTargetValue);
//			System.out.println("hmCorporateTargetValue====>"+hmCorporateTargetValue);
			request.setAttribute("hmTargetedValue", hmTargetedValue);
//			System.out.println("hmTargetedValue====>"+hmTargetedValue);
			request.setAttribute("hmCorporateFlag", hmCorporateFlag);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private void getEmpImage(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);

			pst=con.prepareStatement("select emp_image,emp_per_id from employee_personal_details ");
			rs=pst.executeQuery();
			Map<String,String> empImageMap=new HashMap<String,String>();
			while(rs.next()) {
				empImageMap.put(rs.getString("emp_per_id"),rs.getString("emp_image"));
			}
			rs.close();
			pst.close();
			request.setAttribute("empImageMap", empImageMap);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}


	private void getGoalKRADetails(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			request.setAttribute("hmEmpName", hmEmpName);
			pst = con.prepareStatement("select * from goal_kras order by goal_kra_id");
			rs = pst.executeQuery();
			Map<String, List<List<String>>> hmKRA = new LinkedHashMap<String, List<List<String>>>();
			while (rs.next()) {
				List<List<String>> outerList = hmKRA.get(rs.getString("goal_id"));
				if (outerList == null)
					outerList = new ArrayList<List<String>>();
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("goal_kra_id"));
				innerList.add(rs.getString("goal_id")); 
				innerList.add(rs.getString("entry_date"));
				innerList.add(rs.getString("effective_date"));
				innerList.add(rs.getString("is_approved"));
				innerList.add(rs.getString("approved_by"));
				innerList.add(rs.getString("kra_order"));
				innerList.add(rs.getString("kra_description"));
				innerList.add(rs.getString("goal_type"));

				outerList.add(innerList);
				hmKRA.put(rs.getString("goal_id"), outerList);
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from goal_kra_tasks order by goal_kra_task_id");
			rs = pst.executeQuery();
			Map<String, List<List<String>>> hmKRATasks = new LinkedHashMap<String, List<List<String>>>();
			while (rs.next()) {
				List<List<String>> outerList = hmKRATasks.get(rs.getString("kra_id"));
				if (outerList == null) outerList = new ArrayList<List<String>>();
				
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("goal_kra_task_id"));
				innerList.add(rs.getString("task_name")); 
				outerList.add(innerList);
				hmKRATasks.put(rs.getString("kra_id"), outerList);
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmKRATasks", hmKRATasks);
			
//			System.out.println("hmKRA ===>> " + hmKRA);
			request.setAttribute("hmKRA", hmKRA);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private void getGoalTypeDetails(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		Map<String, String> hmGoalType = new HashMap<String, String>();
		try {
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("select * from goal_type_details order by goal_type_id");
			rs = pst.executeQuery();
			while (rs.next()) {
				hmGoalType.put(rs.getString("goal_type_id"),rs.getString("goal_type_name"));				
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmGoalType",hmGoalType);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}


	private void getGoalSummary(UtilityFunctions uF) {
			if(strUserType!=null && strUserType.equals(EMPLOYEE)) {
				getEmpCorporateDetails(uF);
				getEmpManagerDetails(uF);
				getEmpTeamDetails(uF);
				getEmpIndividualDetails(uF);
				//getMaxAchievedTargetBYEmpAndGoalwise(uF);
				
				getGoalRating();
				getTeamGoalAverageDetails();
				getManagerGoalAverageDetails();
				getCorporateGoalAverageDetails();
			} else if(strUserType!=null && strUserType.equals(MANAGER)) {
//				System.out.println("strUserType =====> " + strUserType);
//				getManagerCorporateDetails(uF);
				getCorporateDetails(uF);
				getManagerDetails(uF);
				getTeamDetails(uF);
				getIndividualDetails(uF);
				//getMaxAchievedTargetBYEmpAndGoalwise(uF);
				
				getGoalRating();
				getKRARatingAndCompletionStatus(uF);
				
				getTeamGoalAverageDetails();
				getManagerGoalAverageDetails();
				getCorporateGoalAverageDetails();
			} else {
				getCorporateDetails(uF);
				getManagerDetails(uF);
				getTeamDetails(uF);
				getIndividualDetails(uF);
				//getMaxAchievedTargetBYEmpAndGoalwise(uF);
				
				getGoalRating();
				getKRARatingAndCompletionStatus(uF);
				
				getTeamGoalAverageDetails();
				getManagerGoalAverageDetails();
				getCorporateGoalAverageDetails();
			}
	}
	

	
	public void getKRARatingAndCompletionStatus(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			Map<String, String> hmGoalAndTargetRating = new HashMap<String, String>();
				pst=con.prepareStatement("select gksrd.*, gd.goal_id from goal_kra_status_rating_details gksrd, goal_details gd where gksrd.goal_id = gd.goal_id");
				rs=pst.executeQuery();
				while (rs.next()) {
					
					if((rs.getString("manager_rating") != null && !rs.getString("manager_rating").equals("")) || (rs.getString("hr_rating") != null && !rs.getString("hr_rating").equals("")) ) {
						
						double strGoalTargetRating = uF.parseToDouble(hmGoalAndTargetRating.get(rs.getString("goal_id")+"_RATING"));
						int strGoalTargetCount = uF.parseToInt(hmGoalAndTargetRating.get(rs.getString("goal_id")+"_COUNT"));
						strGoalTargetCount++;
						double strGoalTargetCurrRating = (uF.parseToDouble(rs.getString("manager_rating")) + uF.parseToDouble(rs.getString("hr_rating"))) / 2;
						if(rs.getString("manager_rating") == null) {
							strGoalTargetCurrRating = uF.parseToDouble(rs.getString("hr_rating"));
						} else if(rs.getString("hr_rating") == null) {
							strGoalTargetCurrRating = uF.parseToDouble(rs.getString("manager_rating"));
						}
						strGoalTargetRating += strGoalTargetCurrRating;
						hmGoalAndTargetRating.put(rs.getString("goal_id")+"_RATING", strGoalTargetRating+"");
						hmGoalAndTargetRating.put(rs.getString("goal_id")+"_COUNT", strGoalTargetCount+"");
					}
				}
				rs.close();
				pst.close();
//			System.out.println("hmTargetRatingAndComment ===>> " + hmTargetRatingAndComment);
			request.setAttribute("hmGoalAndTargetRating", hmGoalAndTargetRating);
			
			
			Map<String, String> hmKRATaskRating = new HashMap<String, String>();
			Map<String, String> hmKRARating = new HashMap<String, String>();
			Map<String, String> hmGoalRating = new HashMap<String, String>();
			
			Map<String, String> hmKRATaskStatus = new HashMap<String, String>();
			Map<String, String> hmKRAStatus = new HashMap<String, String>();
			Map<String, String> hmGoalStatus = new HashMap<String, String>();
			
				pst=con.prepareStatement("select gksrd.*, gk.goal_id from goal_kra_status_rating_details gksrd, goal_kras gk where gksrd.kra_id = gk.goal_kra_id ");
				rs=pst.executeQuery();
				while (rs.next()) {
					
					if((rs.getString("manager_rating") != null && !rs.getString("manager_rating").equals("")) || (rs.getString("hr_rating") != null && !rs.getString("hr_rating").equals("")) ) {
						double strKRATaskRating = uF.parseToDouble(hmKRATaskRating.get(rs.getString("kra_task_id")+"_RATING"));
						int strKRATaskCount = uF.parseToInt(hmKRATaskRating.get(rs.getString("kra_task_id")+"_COUNT"));
						strKRATaskCount++;
						double strCurrKRATaskRating = (uF.parseToDouble(rs.getString("manager_rating")) + uF.parseToDouble(rs.getString("hr_rating"))) / 2;
						if(rs.getString("manager_rating") == null) {
							strCurrKRATaskRating = uF.parseToDouble(rs.getString("hr_rating"));
						} else if(rs.getString("hr_rating") == null) {
							strCurrKRATaskRating = uF.parseToDouble(rs.getString("manager_rating"));
						}
						strKRATaskRating += strCurrKRATaskRating;
						hmKRATaskRating.put(rs.getString("kra_task_id")+"_RATING", strKRATaskRating+"");
						hmKRATaskRating.put(rs.getString("kra_task_id")+"_COUNT", strKRATaskCount+"");
						
						
						double strKRARating = uF.parseToDouble(hmKRARating.get(rs.getString("kra_id")+"_RATING"));
						int strKRACount = uF.parseToInt(hmKRARating.get(rs.getString("kra_id")+"_COUNT"));
						strKRACount++;
						double strCurrKRARating = (uF.parseToDouble(rs.getString("manager_rating")) + uF.parseToDouble(rs.getString("hr_rating"))) / 2;
						if(rs.getString("manager_rating") == null) {
							strCurrKRARating = uF.parseToDouble(rs.getString("hr_rating"));
						} else if(rs.getString("hr_rating") == null) {
							strCurrKRARating = uF.parseToDouble(rs.getString("manager_rating"));
						}
						strKRARating += strCurrKRARating;
						hmKRARating.put(rs.getString("kra_id")+"_RATING", strKRARating+"");
						hmKRARating.put(rs.getString("kra_id")+"_COUNT", strKRACount+"");
						
						
						double strGoalRating = uF.parseToDouble(hmGoalRating.get(rs.getString("goal_id")+"_RATING"));
						int strGoalCount = uF.parseToInt(hmGoalRating.get(rs.getString("goal_id")+"_COUNT"));
						strGoalCount++;
						double strGoalCurrRating = (uF.parseToDouble(rs.getString("manager_rating")) + uF.parseToDouble(rs.getString("hr_rating"))) / 2;
						if(rs.getString("manager_rating") == null) {
							strGoalCurrRating = uF.parseToDouble(rs.getString("hr_rating"));
						} else if(rs.getString("hr_rating") == null) {
							strGoalCurrRating = uF.parseToDouble(rs.getString("manager_rating"));
						}
						strGoalRating += strGoalCurrRating;
						hmGoalRating.put(rs.getString("goal_id")+"_RATING", strGoalRating+"");
						hmGoalRating.put(rs.getString("goal_id")+"_COUNT", strGoalCount+"");
					}
					
					
					double strKRATaskStatus = uF.parseToDouble(hmKRATaskStatus.get(rs.getString("kra_task_id")+"_STATUS"));
					int strKRATaskStatusCount = uF.parseToInt(hmKRATaskStatus.get(rs.getString("kra_task_id")+"_COUNT"));
					strKRATaskStatusCount++;
					double strCurrKRATaskStatus = uF.parseToDouble(rs.getString("complete_percent"));
					strKRATaskStatus += strCurrKRATaskStatus;
					hmKRATaskStatus.put(rs.getString("kra_task_id")+"_STATUS", strKRATaskStatus+"");
					hmKRATaskStatus.put(rs.getString("kra_task_id")+"_COUNT", strKRATaskStatusCount+"");
					
					
					double strKRAStatus = uF.parseToDouble(hmKRAStatus.get(rs.getString("kra_id")+"_STATUS"));
					int strKRAStatusCount = uF.parseToInt(hmKRAStatus.get(rs.getString("kra_id")+"_COUNT"));
					strKRAStatusCount++;
					double strCurrKRAStatus = uF.parseToDouble(rs.getString("complete_percent"));
					strKRAStatus += strCurrKRAStatus;
					hmKRAStatus.put(rs.getString("kra_id")+"_STATUS", strKRAStatus+"");
					hmKRAStatus.put(rs.getString("kra_id")+"_COUNT", strKRAStatusCount+"");
					
					double strGoalStatus = uF.parseToDouble(hmGoalStatus.get(rs.getString("goal_id")+"_STATUS"));
					int strGoalStatusCount = uF.parseToInt(hmGoalStatus.get(rs.getString("goal_id")+"_COUNT"));
					strGoalStatusCount++;
					double strGoalCurrStatus = uF.parseToDouble(rs.getString("complete_percent"));
					strGoalStatus += strGoalCurrStatus;
					hmGoalStatus.put(rs.getString("goal_id")+"_STATUS", strGoalStatus+"");
					hmGoalStatus.put(rs.getString("goal_id")+"_COUNT", strGoalStatusCount+"");
				}
				rs.close();
				pst.close();
			
				request.setAttribute("hmKRATaskStatus", hmKRATaskStatus);
				request.setAttribute("hmKRAStatus", hmKRAStatus);
				request.setAttribute("hmGoalStatus", hmGoalStatus);
				
			request.setAttribute("hmGoalRating", hmGoalRating);
			request.setAttribute("hmKRARating", hmKRARating);
//			System.out.println("hmKRATaskRating ====>>> " + hmKRATaskRating);
			
			request.setAttribute("hmKRATaskRating", hmKRATaskRating);
			
			
			List<String> alCorpGoalId = new ArrayList<String>();
			List<String> alMngrGoalId = new ArrayList<String>();
			List<String> alTeamGoalId = new ArrayList<String>();
			Map<String, List<String>> hmGoalIds = new HashMap<String, List<String>>();
			List<String> alGoalId = new ArrayList<String>();
			String goalType = CORPORATE_GOAL+","+MANAGER_GOAL+","+TEAM_GOAL;
			pst = con.prepareStatement("select goal_id,goal_type,goal_parent_id from goal_details "); //where goal_type in ("+goalType+")
//			System.out.println("pst ===>> " + pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				if(rs.getInt("goal_parent_id") > 0) {
					alGoalId = hmGoalIds.get(rs.getString("goal_parent_id"));
					if(alGoalId == null) alGoalId = new ArrayList<String>();
					
					alGoalId.add(rs.getString("goal_id"));
					hmGoalIds.put(rs.getString("goal_parent_id"), alGoalId);
				}
				
				if(rs.getInt("goal_type") == CORPORATE_GOAL) {
					if(uF.parseToInt(getGoalId())>0 && uF.parseToInt(getGoalId()) == uF.parseToInt(rs.getString("goal_id"))) {
						alCorpGoalId.add(rs.getString("goal_id"));
					} else {
						alCorpGoalId.add(rs.getString("goal_id"));
					}
					alCorpGoalId.add(rs.getString("goal_id"));
				} else if(rs.getInt("goal_type") == MANAGER_GOAL) {
					alMngrGoalId.add(rs.getString("goal_id"));
				} else if(rs.getInt("goal_type") == TEAM_GOAL) {
					alTeamGoalId.add(rs.getString("goal_id"));
				}
			}
			rs.close();
			pst.close();
			
//			System.out.println("alTeamGoalId ===>> " + alTeamGoalId);
//			System.out.println("hmGoalIds ===>> " + hmGoalIds);
			
			Map<String, String> hmCorpGoalRating = new HashMap<String, String>();
			Map<String, String> hmMngrGoalRating = new HashMap<String, String>();
			Map<String, String> hmTeamGoalRating = new HashMap<String, String>();
			
			for (int b = 0; alCorpGoalId != null && b < alCorpGoalId.size(); b++) {
				List<String> mngrGoalList = hmGoalIds.get(alCorpGoalId.get(b));
				double strCorpGoalRate = 0;
				int strCorpGoalCnt = 0;
				for (int a = 0; mngrGoalList != null && a < mngrGoalList.size(); a++) {
					List<String> teamGoalList = hmGoalIds.get(mngrGoalList.get(a));
					double strMngrGoalRate = 0;
					int strMngrGoalCnt = 0;
					for (int i = 0; teamGoalList != null && i < teamGoalList.size(); i++) {
						List<String> goalList = hmGoalIds.get(teamGoalList.get(i));
						double strTeamGoalRate = 0;
						int strTeamGoalCnt = 0;
						for(int j = 0; goalList != null && j<goalList.size(); j++) {
							double strGTRating = uF.parseToDouble(hmGoalAndTargetRating.get(goalList.get(j)+"_RATING"));
							int strGTCount = uF.parseToInt(hmGoalAndTargetRating.get(goalList.get(j)+"_COUNT"));
							if(strGTCount > 0) {
								strCorpGoalRate += strGTRating;
								strCorpGoalCnt += strGTCount;
								
								strMngrGoalRate += strGTRating;
								strMngrGoalCnt += strGTCount;
								
								strTeamGoalRate += strGTRating;
								strTeamGoalCnt += strGTCount;
								
								hmTeamGoalRating.put(teamGoalList.get(i)+"_RATING", strTeamGoalRate+"");
								hmTeamGoalRating.put(teamGoalList.get(i)+"_COUNT", strTeamGoalCnt+"");
								
								hmMngrGoalRating.put(mngrGoalList.get(a)+"_RATING", strMngrGoalRate+"");
								hmMngrGoalRating.put(mngrGoalList.get(a)+"_COUNT", strMngrGoalCnt+"");
								
								hmCorpGoalRating.put(alCorpGoalId.get(b)+"_RATING", strCorpGoalRate+"");
								hmCorpGoalRating.put(alCorpGoalId.get(b)+"_COUNT", strCorpGoalCnt+"");
							}
							
							double strGRating = uF.parseToDouble(hmGoalRating.get(goalList.get(j)+"_RATING"));
							int strGCount = uF.parseToInt(hmGoalRating.get(goalList.get(j)+"_COUNT"));
							if(strGCount > 0) {
								strCorpGoalRate += strGRating;
								strCorpGoalCnt += strGCount;
								
								strMngrGoalRate += strGRating;
								strMngrGoalCnt += strGCount;
								
								strTeamGoalRate += strGRating;
								strTeamGoalCnt += strGCount;
								
								hmTeamGoalRating.put(teamGoalList.get(i)+"_RATING", strTeamGoalRate+"");
								hmTeamGoalRating.put(teamGoalList.get(i)+"_COUNT", strTeamGoalCnt+"");
								
								hmMngrGoalRating.put(mngrGoalList.get(a)+"_RATING", strMngrGoalRate+"");
								hmMngrGoalRating.put(mngrGoalList.get(a)+"_COUNT", strMngrGoalCnt+"");
								
								hmCorpGoalRating.put(alCorpGoalId.get(b)+"_RATING", strCorpGoalRate+"");
								hmCorpGoalRating.put(alCorpGoalId.get(b)+"_COUNT", strCorpGoalCnt+"");
							}
						}
					}
				}
			}
			
			request.setAttribute("hmTeamGoalRating", hmTeamGoalRating);
			request.setAttribute("hmMngrGoalRating", hmMngrGoalRating);
			request.setAttribute("hmCorpGoalRating", hmCorpGoalRating);
			
//			System.out.println("hmTeamGoalRating ====>>> " + hmTeamGoalRating);
//			System.out.println("hmMngrGoalRating ====>>> " + hmMngrGoalRating);
//			System.out.println("hmCorpGoalRating ====>>> " + hmCorpGoalRating);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	public void getCorporateGoalAverageDetails() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		try {
			con = db.makeConnection(con);
//				pst=con.prepareStatement("select goal_parent_id, goal_id from goal_details where (goal_type= 4 or (goal_type= 5 and goalalign_with_teamgoal = true)) " +
//						" and org_id ="+uF.parseToInt(strEmpOrgId)+" and emp_ids like '%,"+strSessionEmpId+",%' order by goal_id desc");
			pst=con.prepareStatement("select goal_parent_id,goal_id from goal_details where goal_type = ? and goal_id in(" +
					"select goal_parent_id from goal_details where goal_type = ? and goal_id in(select goal_parent_id from " +
					"goal_details where (goal_type= ? or (goal_type= ? and goalalign_with_teamgoal = true)) and " +
					"org_id ="+uF.parseToInt(strEmpOrgId)+")) order by goal_id desc "); // and emp_ids like '%,"+strSessionEmpId+",%'
			pst.setInt(1, MANAGER_GOAL);
			pst.setInt(2, TEAM_GOAL);
			pst.setInt(3, INDIVIDUAL_GOAL);
			pst.setInt(4, PERSONAL_GOAL);
//			System.out.println("pst ======> " + pst);
			rs = pst.executeQuery();
			Map<String, List<String>> hmManagerGoalIdCorpwise = new HashMap<String, List<String>>();
			List<String> managerGoalIdList = new ArrayList<String>();
			while (rs.next()) {
				managerGoalIdList = hmManagerGoalIdCorpwise.get(rs.getString("goal_parent_id"));
				if(managerGoalIdList == null) managerGoalIdList = new ArrayList<String>();
				managerGoalIdList.add(rs.getString("goal_id"));
				hmManagerGoalIdCorpwise.put(rs.getString("goal_parent_id"), managerGoalIdList);
			}
			rs.close();
			pst.close();
			
			Map<String, String> hmManagerGoalAverage = (Map<String, String>) request.getAttribute("hmManagerGoalAverage");
			
			Map<String, String> hmCorpGoalAverage = new HashMap<String, String>();
//			System.out.println("hmTeamGoalIdManagerwise =====> " + hmTeamGoalIdManagerwise);
			
			Iterator<String> it = hmManagerGoalIdCorpwise.keySet().iterator();
			while (it.hasNext()) {
				String key = it.next();
				List<String> managerGoalIdList1 = hmManagerGoalIdCorpwise.get(key);
				double corpAllGoalAverage = 0.0d;
				double corpGoalAverage = 0.0d;
				int teamGoalCnt = 0;
				for(int i=0; managerGoalIdList1 != null && !managerGoalIdList1.isEmpty() && i< managerGoalIdList1.size(); i++) {
					if(hmManagerGoalAverage != null && !hmManagerGoalAverage.isEmpty()) {
						if(hmManagerGoalAverage.get(managerGoalIdList1.get(i)) != null) {
							corpAllGoalAverage += uF.parseToDouble(hmManagerGoalAverage.get(managerGoalIdList1.get(i)));
							teamGoalCnt++;
						}
					}
				}
				if(teamGoalCnt > 0) {
					corpGoalAverage = corpAllGoalAverage / teamGoalCnt;
				}
				hmCorpGoalAverage.put(key, ""+corpGoalAverage);
			}
//			System.out.println("hmCorpGoalAverage =====> " + hmCorpGoalAverage);
			request.setAttribute("hmCorpGoalAverage", hmCorpGoalAverage);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	public void getManagerGoalAverageDetails() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		try {
			con = db.makeConnection(con);
//				pst=con.prepareStatement("select goal_parent_id, goal_id from goal_details where (goal_type= 4 or (goal_type= 5 and goalalign_with_teamgoal = true)) " +
//						" and org_id ="+uF.parseToInt(strEmpOrgId)+" and emp_ids like '%,"+strSessionEmpId+",%' order by goal_id desc");
			pst=con.prepareStatement("select goal_parent_id,goal_id from goal_details where goal_type = ? and goal_id in(select goal_parent_id from " +
					"goal_details where (goal_type= ? or (goal_type= ? and goalalign_with_teamgoal = true)) and " +
					"org_id ="+uF.parseToInt(strEmpOrgId)+") order by goal_id desc "); //and emp_ids like '%,"+strSessionEmpId+",%'
			pst.setInt(1, TEAM_GOAL);
			pst.setInt(2, INDIVIDUAL_GOAL);
			pst.setInt(3, PERSONAL_GOAL);
//			System.out.println("pst ======> " + pst);
			rs = pst.executeQuery();
			Map<String, List<String>> hmTeamGoalIdManagerwise = new HashMap<String, List<String>>();
			List<String> teanGoalIdList = new ArrayList<String>();
			while (rs.next()) {
				teanGoalIdList = hmTeamGoalIdManagerwise.get(rs.getString("goal_parent_id"));
				if(teanGoalIdList == null) teanGoalIdList = new ArrayList<String>();
				teanGoalIdList.add(rs.getString("goal_id"));
				hmTeamGoalIdManagerwise.put(rs.getString("goal_parent_id"), teanGoalIdList);
			}
			rs.close();
			pst.close();
			
			Map<String, String> hmTeamGoalAverage = (Map<String, String>) request.getAttribute("hmTeamGoalAverage");
			
			Map<String, String> hmManagerGoalAverage = new HashMap<String, String>();
//			System.out.println("hmTeamGoalIdManagerwise =====> " + hmTeamGoalIdManagerwise);
			
			Iterator<String> it = hmTeamGoalIdManagerwise.keySet().iterator();
			while (it.hasNext()) {
				String key = it.next();
				List<String> teanGoalIdList1 = hmTeamGoalIdManagerwise.get(key);
				double managerAllGoalAverage = 0.0d;
				double mangerGoalAverage = 0.0d;
				int teamGoalCnt = 0;
				for(int i=0; teanGoalIdList1 != null && !teanGoalIdList1.isEmpty() && i< teanGoalIdList1.size(); i++) {
					if(hmTeamGoalAverage != null && !hmTeamGoalAverage.isEmpty()) {
						if(hmTeamGoalAverage.get(teanGoalIdList1.get(i)) != null) {
							managerAllGoalAverage += uF.parseToDouble(hmTeamGoalAverage.get(teanGoalIdList1.get(i)));
							teamGoalCnt++;
						}
					}
				}
				if(teamGoalCnt > 0) {
					mangerGoalAverage = managerAllGoalAverage / teamGoalCnt;
				}
				hmManagerGoalAverage.put(key, ""+mangerGoalAverage);
			}
//			System.out.println("hmManagerGoalAverage =====> " + hmManagerGoalAverage);
			request.setAttribute("hmManagerGoalAverage", hmManagerGoalAverage);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	public void getTeamGoalAverageDetails() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		try {
			con = db.makeConnection(con);
			
			pst=con.prepareStatement("select goal_parent_id, goal_id from goal_details where (goal_type= ? or (goal_type= ? and goalalign_with_teamgoal = true)" +
					"or goal_type= ? or goal_type= ?) " +
					" and org_id ="+uF.parseToInt(strEmpOrgId)+" order by goal_id desc"); //and emp_ids like '%,"+strSessionEmpId+",%'
			pst.setInt(1, INDIVIDUAL_GOAL);
			pst.setInt(2, PERSONAL_GOAL);
			pst.setInt(3, INDIVIDUAL_TARGET);
			pst.setInt(4, INDIVIDUAL_KRA);
//			System.out.println("pst ======> " + pst);
			rs = pst.executeQuery();
			Map<String, List<String>> hmGoalIdTeamwise = new HashMap<String, List<String>>();
			List<String> goalIdList = new ArrayList<String>();
			while (rs.next()) {
				goalIdList = hmGoalIdTeamwise.get(rs.getString("goal_parent_id"));
				if(goalIdList == null) goalIdList = new ArrayList<String>();
				goalIdList.add(rs.getString("goal_id"));
				hmGoalIdTeamwise.put(rs.getString("goal_parent_id"), goalIdList);
			}
			rs.close();
			pst.close();
			
			Map<String, String> hmGoalAverage = (Map<String, String>) request.getAttribute("hmGoalAverage");
			
			Map<String, String> hmTeamGoalAverage = new HashMap<String, String>();
//			System.out.println("hmGoalIdTeamwise =====> " + hmGoalIdTeamwise);
			
			Iterator<String> it = hmGoalIdTeamwise.keySet().iterator();
			while (it.hasNext()) {
				String key = it.next();
				List<String> goalIdList1 = hmGoalIdTeamwise.get(key);
				double teamAllGoalAverage = 0.0d;
				double teamGoalAverage = 0.0d;
				int teamGoalCnt = 0;
				for(int i=0; goalIdList1 != null && !goalIdList1.isEmpty() && i< goalIdList1.size(); i++) {
					if(hmGoalAverage != null && !hmGoalAverage.isEmpty()) {
						if(hmGoalAverage.get(goalIdList1.get(i)) != null) {
							teamAllGoalAverage += uF.parseToDouble(hmGoalAverage.get(goalIdList1.get(i)));
							teamGoalCnt++;
						}
					}
				}
				if(teamGoalCnt > 0) {
					teamGoalAverage = teamAllGoalAverage / teamGoalCnt;
				}
				hmTeamGoalAverage.put(key, ""+teamGoalAverage);
			}
//			System.out.println("hmTeamGoalAverage =====> " + hmTeamGoalAverage);
			request.setAttribute("hmTeamGoalAverage", hmTeamGoalAverage);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	public void getGoalRating() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try { 
			con = db.makeConnection(con);
			
			pst=con.prepareStatement("select goal_kra_target_id,sum(average)/count(goal_kra_target_id) as avg from ( select goal_kra_target_id," +
					" (marks*100/weightage) as average from (select question_bank_id,question_text,goal_kra_target_id from question_bank where " +
					" goal_kra_target_id in(select goal_id from goal_details where  (goal_type = ? or goal_type = ? or goal_type = ? or goal_type = ?))) as a," +
					" appraisal_question_answer aqa where a.question_bank_id=aqa.question_id group by goal_kra_target_id,marks,weightage having weightage > 0) b group by  goal_kra_target_id");
			pst.setInt(1, INDIVIDUAL_GOAL); 
			pst.setInt(2, PERSONAL_GOAL);
			pst.setInt(3, INDIVIDUAL_TARGET);
			pst.setInt(4, INDIVIDUAL_KRA);
//			System.out.println("pst ===>> " + pst);
			rs=pst.executeQuery();
			Map<String, String> hmGoalAverage = new HashMap<String, String>();
			while (rs.next()) {
				hmGoalAverage.put(rs.getString("goal_kra_target_id"), rs.getString("avg"));
			}
			rs.close();
			pst.close();
			
//			System.out.println("hmGoalAverage ===>> " + hmGoalAverage);
			request.setAttribute("hmGoalAverage", hmGoalAverage);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	
	private Map<String, String> getMaxAchievedTargetBYEmpAndGoalwise(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		Map<String, String> hmTargetValue = new HashMap<String,String>();
		try {
			con = db.makeConnection(con);
			
			
			Map<String, String> hmTargetID= new HashMap<String,String>();
			Map<String, String> hmTargetTmpValue= new HashMap<String,String>();
			//Map<String, String> hmUpdateBy= new HashMap<String,String>();
			pst = con.prepareStatement("select * from target_details where target_id in (select max(target_id) from target_details group by goal_id,emp_id)");
			rs= pst.executeQuery();
			while(rs.next()) {
				hmTargetValue.put(rs.getString("emp_id")+"_"+rs.getString("goal_id"), rs.getString("amt_percentage"));
				hmTargetID.put(rs.getString("emp_id")+"_"+rs.getString("goal_id"), rs.getString("target_id"));
				hmTargetTmpValue.put(rs.getString("emp_id")+"_"+rs.getString("goal_id"), rs.getString("emp_amt_percentage"));
				//hmUpdateBy.put(rs.getString("emp_id")+"_"+rs.getString("goal_id"), hmEmpCodeName.get(rs.getString("added_by"))+" on "+uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmTargetValue", hmTargetValue);
//			System.out.println("hmTargetValue ===> " + hmTargetValue);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return hmTargetValue;
	}
	
	
	private void getEmpIndividualDetails(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			Map<String,List<List<String>>> hmEmpIndividual = new LinkedHashMap<String, List<List<String>>>();
			Map<String,String> hmAttribute = CF.getAttributeMap(con);
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from goal_details where goal_type="+INDIVIDUAL_GOAL+" and emp_ids like '%"+strSessionEmpId+"%'");
			if(getDataType() != null && getDataType().equals("L")) {
				sbQuery.append(" and is_close = false ");
			} else if(getDataType() != null && getDataType().equals("C")) {
				sbQuery.append(" and is_close = true ");
			}
			sbQuery.append(" order by goal_id desc");
			
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();
			while (rs.next()) {
				List<List<String>> outerList=hmEmpIndividual.get(rs.getString("goal_parent_id"));
				if(outerList==null)outerList=new ArrayList<List<String>>();
				
				List<String> innerList=new ArrayList<String>();
				innerList.add(rs.getString("goal_id"));
				innerList.add(rs.getString("goal_type"));
				innerList.add(rs.getString("goal_parent_id"));
				innerList.add(rs.getString("goal_title"));
				innerList.add(rs.getString("goal_objective"));
				innerList.add(rs.getString("goal_description"));
				innerList.add(uF.showData(hmAttribute.get(rs.getString("goal_attribute")),""));
				innerList.add(rs.getString("measure_type"));
				innerList.add(rs.getString("measure_currency_value"));
				innerList.add(rs.getString("measure_currency_id"));
				innerList.add(rs.getString("measure_effort_days"));
				innerList.add(rs.getString("measure_effort_hrs"));
				innerList.add(rs.getString("measure_type1"));
				innerList.add(rs.getString("measure_kra"));
				innerList.add(rs.getString("measure_currency_value1"));
				innerList.add(rs.getString("measure_currency1_id"));
				innerList.add(uF.getDateFormat(rs.getString("due_date"), DBDATE, CF.getStrReportDateFormat()));
				innerList.add(rs.getString("is_feedback"));
				innerList.add(rs.getString("orientation_id"));
				innerList.add(rs.getString("weightage"));
				innerList.add(uF.showData(getAppendData(con, rs.getString("emp_ids"), hmEmpName), ""));
				innerList.add(uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));
				innerList.add(rs.getString("user_id"));
				innerList.add(rs.getString("is_measure_kra"));
				innerList.add(rs.getString("measure_kra_days"));
				innerList.add(rs.getString("measure_kra_hrs"));
				innerList.add(rs.getString("grade_id"));
				innerList.add(rs.getString("level_id"));
				innerList.add(rs.getString("kra"));
				
				innerList.add(rs.getString("emp_ids"));
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
				innerList.add(priority);
				innerList.add(uF.getDateFormat(rs.getString("effective_date"), DBDATE, CF.getStrReportDateFormat()) );
				innerList.add(pClass);
				
				outerList.add(innerList);
				hmEmpIndividual.put(rs.getString("goal_parent_id"), outerList);
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmEmpIndividual",hmEmpIndividual);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}


	private void getEmpManagerDetails(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			Map<String,List<List<String>>> hmEmpManager = new LinkedHashMap<String, List<List<String>>>();
			Map<String,String> hmAttribute = CF.getAttributeMap(con);	
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from goal_details where goal_type="+MANAGER_GOAL+" and emp_ids like '%"+strSessionEmpId+"%'");
			if(getDataType() != null && getDataType().equals("L")) {
				sbQuery.append(" and is_close = false ");
			} else if(getDataType() != null && getDataType().equals("C")) {
				sbQuery.append(" and is_close = true ");
			}
			sbQuery.append(" order by goal_id desc");
			
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();
		
			while (rs.next()) {
				List<List<String>> outerList=hmEmpManager.get(rs.getString("goal_parent_id"));
				if(outerList==null)outerList=new ArrayList<List<String>>();
				List<String> innerList=new ArrayList<String>();
				innerList.add(rs.getString("goal_id"));
				innerList.add(rs.getString("goal_type"));
				innerList.add(rs.getString("goal_parent_id"));
				innerList.add(rs.getString("goal_title"));
				innerList.add(rs.getString("goal_objective"));
				innerList.add(rs.getString("goal_description"));
				innerList.add(uF.showData(hmAttribute.get(rs.getString("goal_attribute")),""));
				innerList.add(rs.getString("measure_type"));
				innerList.add(rs.getString("measure_currency_value"));
				innerList.add(rs.getString("measure_currency_id"));
				innerList.add(rs.getString("measure_effort_days"));
				innerList.add(rs.getString("measure_effort_hrs"));
				innerList.add(rs.getString("measure_type1"));
				innerList.add(rs.getString("measure_kra"));
				innerList.add(rs.getString("measure_currency_value1"));
				innerList.add(rs.getString("measure_currency1_id"));
				innerList.add(uF.getDateFormat(rs.getString("due_date"), DBDATE, CF.getStrReportDateFormat()) );
				innerList.add(rs.getString("is_feedback"));
				innerList.add(rs.getString("orientation_id"));
				innerList.add(rs.getString("weightage"));
				innerList.add(uF.showData(getAppendData(con, rs.getString("emp_ids"), hmEmpName), ""));
				innerList.add(uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));
				innerList.add(rs.getString("user_id"));
				innerList.add(rs.getString("is_measure_kra"));
				innerList.add(rs.getString("measure_kra_days"));
				innerList.add(rs.getString("measure_kra_hrs"));
				innerList.add(rs.getString("grade_id"));
				innerList.add(rs.getString("level_id"));
				innerList.add(rs.getString("kra"));				
				innerList.add(rs.getString("emp_ids"));
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
				innerList.add(priority);
				innerList.add(uF.getDateFormat(rs.getString("effective_date"), DBDATE, CF.getStrReportDateFormat()) );
				innerList.add(pClass);
				
				outerList.add(innerList);
				hmEmpManager.put(rs.getString("goal_parent_id"), outerList);
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmEmpManager",hmEmpManager);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	
	}


	private void getEmpCorporateDetails(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			Map<String,List<String>> hmEmpCorporate = new LinkedHashMap<String, List<String>>();
			Map<String,String> hmAttribute = CF.getAttributeMap(con);
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from goal_details where goal_type="+CORPORATE_GOAL+" and emp_ids like '%"+strSessionEmpId+"%'");
			if(uF.parseToInt(getGoalId())>0) {
				sbQuery.append(" and goal_id = " +uF.parseToInt(getGoalId()));
			}
			if(getDataType() != null && getDataType().equals("L")) {
				sbQuery.append(" and is_close = false ");
			} else if(getDataType() != null && getDataType().equals("C")) {
				sbQuery.append(" and is_close = true ");
			}
			sbQuery.append(" order by goal_id desc");
			
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();
			while (rs.next()) {
				List<String> cinnerList=new ArrayList<String>();
				cinnerList.add(rs.getString("goal_id"));
				cinnerList.add(rs.getString("goal_type"));
				cinnerList.add(rs.getString("goal_parent_id"));
				cinnerList.add(rs.getString("goal_title"));
				cinnerList.add(rs.getString("goal_objective"));
				cinnerList.add(rs.getString("goal_description"));
				cinnerList.add(uF.showData(hmAttribute.get(rs.getString("goal_attribute")),""));
				cinnerList.add(rs.getString("measure_type"));
				cinnerList.add(rs.getString("measure_currency_value"));
				cinnerList.add(rs.getString("measure_currency_id"));
				cinnerList.add(rs.getString("measure_effort_days"));
				cinnerList.add(rs.getString("measure_effort_hrs"));
				cinnerList.add(rs.getString("measure_type1"));
				cinnerList.add(rs.getString("measure_kra"));
				cinnerList.add(rs.getString("measure_currency_value1"));
				cinnerList.add(rs.getString("measure_currency1_id"));
				cinnerList.add(uF.getDateFormat(rs.getString("due_date"), DBDATE, CF.getStrReportDateFormat()) );
				cinnerList.add(rs.getString("is_feedback"));
				cinnerList.add(rs.getString("orientation_id"));
				cinnerList.add(rs.getString("weightage"));
				cinnerList.add(uF.showData(getAppendData(con, rs.getString("emp_ids"), hmEmpName), ""));
				cinnerList.add(uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));
				cinnerList.add(rs.getString("user_id"));
				cinnerList.add(rs.getString("is_measure_kra"));
				cinnerList.add(rs.getString("measure_kra_days"));
				cinnerList.add(rs.getString("measure_kra_hrs"));
				cinnerList.add(rs.getString("grade_id"));
				cinnerList.add(rs.getString("level_id"));
				cinnerList.add(rs.getString("kra"));				
				cinnerList.add(rs.getString("emp_ids"));
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
				cinnerList.add(priority);
				cinnerList.add(uF.getDateFormat(rs.getString("effective_date"), DBDATE, CF.getStrReportDateFormat()) );
				cinnerList.add(pClass);
				
				hmEmpCorporate.put(rs.getString("goal_id"), cinnerList);
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmEmpCorporate",hmEmpCorporate);


		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	
	}


	private void getEmpTeamDetails(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			Map<String,List<List<String>>> hmEmpTeam = new LinkedHashMap<String, List<List<String>>>();
			Map<String,String> hmAttribute = CF.getAttributeMap(con);	
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from goal_details where goal_type="+TEAM_GOAL+" and emp_ids like '%"+strSessionEmpId+"%'");
			if(getDataType() != null && getDataType().equals("L")) {
				sbQuery.append(" and is_close = false ");
			} else if(getDataType() != null && getDataType().equals("C")) {
				sbQuery.append(" and is_close = true ");
			}
			sbQuery.append(" order by goal_id desc");
			
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();
			while (rs.next()) {
				List<List<String>> outerList=hmEmpTeam.get(rs.getString("goal_parent_id"));
				if(outerList==null)outerList=new ArrayList<List<String>>();
				
				List<String> innerList=new ArrayList<String>();
				innerList.add(rs.getString("goal_id"));
				innerList.add(rs.getString("goal_type"));
				innerList.add(rs.getString("goal_parent_id"));
				innerList.add(rs.getString("goal_title"));
				innerList.add(rs.getString("goal_objective"));
				innerList.add(rs.getString("goal_description"));
				innerList.add(uF.showData(hmAttribute.get(rs.getString("goal_attribute")),""));
				innerList.add(rs.getString("measure_type"));
				innerList.add(rs.getString("measure_currency_value"));
				innerList.add(rs.getString("measure_currency_id"));
				innerList.add(rs.getString("measure_effort_days"));
				innerList.add(rs.getString("measure_effort_hrs"));
				innerList.add(rs.getString("measure_type1"));
				innerList.add(rs.getString("measure_kra"));
				innerList.add(rs.getString("measure_currency_value1"));
				innerList.add(rs.getString("measure_currency1_id"));
				innerList.add(uF.getDateFormat(rs.getString("due_date"), DBDATE, CF.getStrReportDateFormat()) );
				innerList.add(rs.getString("is_feedback"));
				innerList.add(rs.getString("orientation_id"));
				innerList.add(rs.getString("weightage"));
				innerList.add(uF.showData(getAppendData(con, rs.getString("emp_ids"), hmEmpName), ""));
				innerList.add(uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));
				innerList.add(rs.getString("user_id"));
				innerList.add(rs.getString("is_measure_kra"));
				innerList.add(rs.getString("measure_kra_days"));
				innerList.add(rs.getString("measure_kra_hrs"));
				innerList.add(rs.getString("grade_id"));
				innerList.add(rs.getString("level_id"));
				innerList.add(rs.getString("kra"));				
				innerList.add(rs.getString("emp_ids"));
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
				innerList.add(priority);
				innerList.add(uF.getDateFormat(rs.getString("effective_date"), DBDATE, CF.getStrReportDateFormat()) );
				innerList.add(pClass);
				
				outerList.add(innerList);
				hmEmpTeam.put(rs.getString("goal_parent_id"), outerList);
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmEmpTeam", hmEmpTeam);
			

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}


	private void getIndividualDetails(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		Map<String,Map<String,String>> hmIndGoalCal= new HashMap<String, Map<String,String>>();
		try {
			con = db.makeConnection(con);
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			Map<String,List<List<String>>> hmIndividual = new LinkedHashMap<String, List<List<String>>>();
			Map<String,String> hmAttribute = CF.getAttributeMap(con);
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from goal_details where goal_type="+INDIVIDUAL_GOAL+" ");
			if(strUserType!=null && strUserType.equals(EMPLOYEE)) {
				sbQuery.append(" and emp_ids like '%"+strSessionEmpId+"%'");
			}
			if(getDataType() != null && getDataType().equals("L")) {
				sbQuery.append(" and is_close = false ");
			} else if(getDataType() != null && getDataType().equals("C")) {
				sbQuery.append(" and is_close = true ");
			}
			sbQuery.append(" order by goal_id desc");
			
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();
			while (rs.next()) {
				
				List<List<String>> outerList=hmIndividual.get(rs.getString("goal_parent_id"));
				if(outerList==null)outerList=new ArrayList<List<String>>();
				
				List<String> innerList=new ArrayList<String>();
				innerList.add(rs.getString("goal_id"));
				innerList.add(rs.getString("goal_type"));
				innerList.add(rs.getString("goal_parent_id"));
				innerList.add(rs.getString("goal_title"));
				innerList.add(rs.getString("goal_objective"));
				innerList.add(rs.getString("goal_description"));
				innerList.add(uF.showData(hmAttribute.get(rs.getString("goal_attribute")),""));
				innerList.add(rs.getString("measure_type"));
				innerList.add(rs.getString("measure_currency_value"));
				innerList.add(rs.getString("measure_currency_id"));
				innerList.add(rs.getString("measure_effort_days"));
				innerList.add(rs.getString("measure_effort_hrs"));
				innerList.add(rs.getString("measure_type1"));
				innerList.add(rs.getString("measure_kra"));
				innerList.add(rs.getString("measure_currency_value1"));
				innerList.add(rs.getString("measure_currency1_id"));
				innerList.add(uF.getDateFormat(rs.getString("due_date"), DBDATE, CF.getStrReportDateFormat()));
				innerList.add(rs.getString("is_feedback"));
				innerList.add(rs.getString("orientation_id"));
				innerList.add(rs.getString("weightage"));
				innerList.add(uF.showData(getAppendData(con, rs.getString("emp_ids"), hmEmpName), ""));
				innerList.add(uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));
				innerList.add(rs.getString("user_id"));
				innerList.add(rs.getString("is_measure_kra"));
				innerList.add(rs.getString("measure_kra_days"));
				innerList.add(rs.getString("measure_kra_hrs"));
				innerList.add(rs.getString("grade_id"));
				innerList.add(rs.getString("level_id"));
				innerList.add(rs.getString("kra"));
				
				innerList.add(rs.getString("emp_ids"));				
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
				innerList.add(priority); //30
				innerList.add(uF.getDateFormat(rs.getString("effective_date"), DBDATE, CF.getStrReportDateFormat()) ); //31
				innerList.add(pClass); //32
				innerList.add(rs.getString("is_close")); //33
				innerList.add(uF.showData(hmEmpName.get(rs.getString("user_id")), "-")); //34
				
				outerList.add(innerList);
				hmIndividual.put(rs.getString("goal_parent_id"), outerList);
				
				Map<String,String> hmIndGoalCalDetails=new HashMap<String, String>();
				getIndividualGoalTargetCalculation(rs.getString("goal_id"), rs.getString("emp_ids"), rs.getString("measure_type"),
						rs.getString("measure_currency_value"), rs.getString("measure_effort_days"), rs.getString("measure_effort_hrs"), uF,hmIndGoalCalDetails);
				
				hmIndGoalCal.put(rs.getString("goal_id"), hmIndGoalCalDetails);
				
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmIndividual",hmIndividual);
			request.setAttribute("hmIndGoalCal",hmIndGoalCal);
//			System.out.println("hmIndGoalCal ===> " + hmIndGoalCal);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	
	
	private void getIndividualGoalTargetCalculation(String indGoalId, String empIds, String measureType, String targetAmt, String strTargetDays, String strTargetHrs, UtilityFunctions uF,Map<String, String> hmIndGoalCalDetails) {
		String alltwoDeciTotProgressAvg ="0";
 		String alltotal100 ="100";
 		String strtwoDeciTot = "0";
 		String strTotTarget = "0";
 		String strTotDays = "0";
 		String strTotHrs = "0";
 		Map<String,String> hmTargetValue = getMaxAchievedTargetBYEmpAndGoalwise(uF);
 		Map<String,String> hmKRAPercent = getMaxAchievedKRAPercentBYEmpAndGoalwise(uF);
// 		System.out.println("hmTargetValue ===> "+hmTargetValue);
		if(empIds !=null) {
			List<String> emplistID=Arrays.asList(empIds.split(","));
			double alltotalTarget=0, allTotal=0, alltwoDeciTot=0, totTarget=0;
			int empListSize=0;
			int allTotHRS =0;
//			System.out.println(" indGoalId ===> "+indGoalId+" emplistID.size() ===> "+emplistID.size()+" emplistID ===> "+emplistID);
			for(int i=0; emplistID!=null && i<emplistID.size();i++) {
				empListSize = emplistID.size()-1;		
			if(emplistID.get(i)!=null && !emplistID.get(i).equals("")) {
			String target="0";
			String kraPercent="0";
			
			String twoDeciTotProgressAvg = "0";
			String twoDeciTot = "0";
			String total="100";
			double totalTarget=0;
			if(hmTargetValue != null && hmTargetValue.get(emplistID.get(i)+"_"+indGoalId)!= null) {
				
				if(hmTargetValue != null && hmTargetValue.get(emplistID.get(i)+"_"+indGoalId)!= null) {
					target=hmTargetValue.get(emplistID.get(i)+"_"+indGoalId);
				}
				if(measureType!=null && !measureType.equals("Effort")) {
					totalTarget=(uF.parseToDouble(target)/uF.parseToDouble(targetAmt))*100;
					twoDeciTot=uF.formatIntoTwoDecimal(totalTarget);
				} else {
					String t=""+uF.parseToDouble(target);
					String days="0";
					String hours="0";
					if(t.contains(".")) {
						t=t.replace(".","_");
						String[] temp=t.split("_");
						days=temp[0];
						hours=temp[1];
					}	
					String targetDays = strTargetDays;
					String targetHrs = strTargetHrs;
					int daysInHrs = uF.parseToInt(days) * 8;
					int inttotHrs = daysInHrs + uF.parseToInt(hours);
					allTotHRS += inttotHrs;
					
					int targetDaysInHrs = uF.parseToInt(targetDays) * 8;
					int inttotTargetHrs = targetDaysInHrs + uF.parseToInt(targetHrs);
					//System.out.println("inttotTargetHrs = "+ inttotTargetHrs + " inttotHrs = "+ inttotHrs + " allTotHRS = "+ allTotHRS);
					if(inttotTargetHrs != 0) {
						totalTarget= uF.parseToDouble(""+inttotHrs) / uF.parseToDouble(""+inttotTargetHrs) * 100;
					}
					twoDeciTot=uF.formatIntoTwoDecimal(totalTarget);
				}
			}			
//			System.out.println(emplistID.get(i)+"_"+indGoalId + " ===>> "+ hmKRAPercent.get(emplistID.get(i)+"_"+indGoalId));
			if(hmKRAPercent != null && hmKRAPercent.get(emplistID.get(i)+"_"+indGoalId)!= null) {
				kraPercent = hmKRAPercent.get(emplistID.get(i)+"_"+indGoalId);
				totalTarget = uF.parseToDouble(kraPercent);
				twoDeciTot=uF.formatIntoTwoDecimal(totalTarget);
			}
			
				if(totalTarget > new Double(100) && totalTarget<=new Double(150)) {
					double totalTarget1=(totalTarget/150)*100;
					twoDeciTotProgressAvg=uF.formatIntoTwoDecimal(totalTarget1);
					total="150";
				} else if(totalTarget > new Double(150) && totalTarget<=new Double(200)) {
					double totalTarget1=(totalTarget/200)*100;
					twoDeciTotProgressAvg=uF.formatIntoTwoDecimal(totalTarget1);
					total="200";
				} else if(totalTarget > new Double(200) && totalTarget<=new Double(250)) {
					double totalTarget1=(totalTarget/250)*100;
					twoDeciTotProgressAvg=uF.formatIntoTwoDecimal(totalTarget1);
					total="250";
				} else if(totalTarget > new Double(250) && totalTarget<=new Double(300)) {
					double totalTarget1=(totalTarget/300)*100;
					twoDeciTotProgressAvg=uF.formatIntoTwoDecimal(totalTarget1);
					total="300";
				} else if(totalTarget > new Double(300) && totalTarget<=new Double(350)) {
					double totalTarget1=(totalTarget/350)*100;
					twoDeciTotProgressAvg=uF.formatIntoTwoDecimal(totalTarget1);
					total="350";
				} else if(totalTarget > new Double(350) && totalTarget<=new Double(400)) {
					double totalTarget1=(totalTarget/400)*100;
					twoDeciTotProgressAvg=uF.formatIntoTwoDecimal(totalTarget1);
					total="400";
				} else if(totalTarget > new Double(400) && totalTarget<=new Double(450)) {
					double totalTarget1=(totalTarget/450)*100;
					twoDeciTotProgressAvg=uF.formatIntoTwoDecimal(totalTarget1);
					total="450";
				} else if(totalTarget > new Double(450) && totalTarget<=new Double(500)) {
					double totalTarget1=(totalTarget/500)*100;
					twoDeciTotProgressAvg=uF.formatIntoTwoDecimal(totalTarget1);
					total="500";
				} else {
					twoDeciTotProgressAvg=uF.formatIntoTwoDecimal(totalTarget);
					if(uF.parseToDouble(twoDeciTotProgressAvg) > 100) {
						twoDeciTotProgressAvg = "100";
						total=""+Math.round(totalTarget);
					} else {
						total="100";
					}
				}
				alltotalTarget += uF.parseToDouble(twoDeciTotProgressAvg);
				allTotal += uF.parseToDouble(total);
				alltwoDeciTot += uF.parseToDouble(twoDeciTot);
				totTarget += uF.parseToDouble(target);
			}
			}
//			System.out.println("indGoalId === " +indGoalId+" alltotalTarget === "+alltotalTarget + " allTotal === "+allTotal+ " empListSize === "+empListSize);
//			System.out.println("indGoalId === " +indGoalId+" alltwoDeciTot === "+alltwoDeciTot + " totTarget === "+totTarget);
//			System.out.println(" totTarget === "+totTarget);
			double alltotAvg = alltotalTarget/empListSize;;
			double alltot100Avg = allTotal/empListSize;
			double alltwoDeciTotAvg = alltwoDeciTot/empListSize;
			double allTotTagetAvg = totTarget/empListSize;
			int allTotHRSAvg = allTotHRS / empListSize;
			int avgDAYS = allTotHRSAvg / 8;
			int avgHRS  = allTotHRSAvg % 8;
			strTotDays = ""+avgDAYS;
			strTotHrs = ""+avgHRS;
			alltwoDeciTotProgressAvg = ""+Math.round(alltotAvg);
			alltotal100 = ""+Math.round(alltot100Avg);
			strtwoDeciTot = ""+Math.round(alltwoDeciTotAvg);
			strTotTarget = ""+Math.round(allTotTagetAvg);
		}
		hmIndGoalCalDetails.put(indGoalId+"_PERCENT", alltwoDeciTotProgressAvg);
		hmIndGoalCalDetails.put(indGoalId+"_TOTAL", alltotal100);
		hmIndGoalCalDetails.put(indGoalId+"_STR_PERCENT", strtwoDeciTot);
		hmIndGoalCalDetails.put(indGoalId+"_ACHIVED_TARGET", strTotTarget);
		hmIndGoalCalDetails.put(indGoalId+"_ACHIVED_DAYS", strTotDays);
		hmIndGoalCalDetails.put(indGoalId+"_ACHIVED_HRS", strTotHrs);
//		System.out.println("hmIndGoalCalDetails =====> " + hmIndGoalCalDetails);
	}
	
	

	private Map<String, String> getMaxAchievedKRAPercentBYEmpAndGoalwise(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		Map<String, String> hmKRAPercent = new HashMap<String,String>();
		try {
			con = db.makeConnection(con);
			
			
			Map<String, String> hmTargetID= new HashMap<String,String>();
			Map<String, String> hmTargetTmpValue= new HashMap<String,String>();
			//Map<String, String> hmUpdateBy= new HashMap<String,String>();
			pst = con.prepareStatement("select sum(complete_percent) as completePercent, count(goal_id) as goalCnt,emp_id,goal_id from goal_kra_status_rating_details group by emp_id,goal_id");
			rs= pst.executeQuery();
			while(rs.next()) {
				
				double avgPercent = 0.0d;
				if(uF.parseToInt(rs.getString("goalCnt"))>0) {
					avgPercent = uF.parseToDouble(rs.getString("completePercent")) / uF.parseToDouble(rs.getString("goalCnt"));
				}
				hmKRAPercent.put(rs.getString("emp_id")+"_"+rs.getString("goal_id"), uF.formatIntoTwoDecimalWithOutComma(avgPercent));
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmKRAPercent", hmKRAPercent);
//			System.out.println("hmKRAPercent ===> " + hmKRAPercent);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return hmKRAPercent;
	}


	private void getTeamDetails(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		Map<String, Map<String, String>> hmIndGoalCalDetailsTeam = new HashMap<String, Map<String, String>>();
		try {
			con = db.makeConnection(con);
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			Map<String,List<List<String>>> hmTeam = new LinkedHashMap<String, List<List<String>>>();
			Map<String,String> hmAttribute = CF.getAttributeMap(con);
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from goal_details where goal_type="+TEAM_GOAL+" "); //and emp_ids like '%"+strSessionEmpId+"%'
			if(getDataType() != null && getDataType().equals("L")) {
				sbQuery.append(" and is_close = false ");
			} else if(getDataType() != null && getDataType().equals("C")) {
				sbQuery.append(" and is_close = true ");
			}
			sbQuery.append(" order by goal_id desc");
			
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();
			while (rs.next()) {
				
				List<List<String>> outerList=hmTeam.get(rs.getString("goal_parent_id"));
				if(outerList==null)outerList=new ArrayList<List<String>>();
				
				List<String> innerList=new ArrayList<String>();
				innerList.add(rs.getString("goal_id"));
				innerList.add(rs.getString("goal_type"));
				innerList.add(rs.getString("goal_parent_id"));
				innerList.add(rs.getString("goal_title"));
				innerList.add(rs.getString("goal_objective"));
				innerList.add(rs.getString("goal_description"));
				innerList.add(uF.showData(hmAttribute.get(rs.getString("goal_attribute")),""));
				innerList.add(rs.getString("measure_type"));
				innerList.add(rs.getString("measure_currency_value"));
				innerList.add(rs.getString("measure_currency_id"));
				innerList.add(rs.getString("measure_effort_days"));
				innerList.add(rs.getString("measure_effort_hrs"));
				innerList.add(rs.getString("measure_type1"));
				innerList.add(rs.getString("measure_kra"));
				innerList.add(rs.getString("measure_currency_value1"));
				innerList.add(rs.getString("measure_currency1_id"));
				innerList.add(uF.getDateFormat(rs.getString("due_date"), DBDATE, CF.getStrReportDateFormat()) );
				innerList.add(rs.getString("is_feedback"));
				innerList.add(rs.getString("orientation_id"));
				innerList.add(rs.getString("weightage"));
				innerList.add(uF.showData(getAppendData(con, rs.getString("emp_ids"), hmEmpName), ""));
				innerList.add(uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));
				innerList.add(rs.getString("user_id"));
				innerList.add(rs.getString("is_measure_kra"));
				innerList.add(rs.getString("measure_kra_days"));
				innerList.add(rs.getString("measure_kra_hrs"));
				innerList.add(rs.getString("grade_id"));
				innerList.add(rs.getString("level_id"));
				innerList.add(rs.getString("kra"));
				
				innerList.add(rs.getString("emp_ids"));				
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
				innerList.add(priority); //30
				innerList.add(uF.getDateFormat(rs.getString("effective_date"), DBDATE, CF.getStrReportDateFormat()) ); //31
				innerList.add(pClass); //32
				innerList.add(rs.getString("is_close")); //33
				innerList.add(hmEmpName.get(rs.getString("user_id"))); //34
				
				outerList.add(innerList);
				hmTeam.put(rs.getString("goal_parent_id"), outerList);
				
				Map<String, String> hmIndGoalCalDetailsParent = new HashMap<String, String>();
				getIndGoalData(con, uF, rs.getString("goal_id"), hmIndGoalCalDetailsParent);
				hmIndGoalCalDetailsTeam.put(rs.getString("goal_id"), hmIndGoalCalDetailsParent);
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmTeam", hmTeam);
			request.setAttribute("hmIndGoalCalDetailsTeam", hmIndGoalCalDetailsTeam);
//			System.out.println("hmIndGoalCalDetailsTeam ===> "+hmIndGoalCalDetailsTeam);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	
	private void getIndGoalData(Connection con, UtilityFunctions uF, String parentID, Map<String, String> hmIndGoalCalDetailsParent) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String,List<List<String>>> hmIndividual=new LinkedHashMap<String, List<List<String>>>();
		Map<String,Map<String,String>> hmIndGoalCalTeam= new HashMap<String, Map<String,String>>();
		Map<String,Map<String,Map<String,String>>> hmIndGoalCalTeamParent = new HashMap<String, Map<String,Map<String,String>>>();
		try {
			
			pst = con.prepareStatement("select * from goal_details where goal_parent_id = ? and is_close != true and is_measure_kra = true order by goal_id "); //  and measure_type !='' and goal_type != 5 if you want only team goals then check personalgoal condition
			pst.setInt(1, uF.parseToInt(parentID));
			rs = pst.executeQuery();
			
//			System.out.println("PST ===> " + pst);
			while (rs.next()) {
				List<List<String>> outerList=hmIndividual.get(rs.getString("goal_parent_id"));
				if(outerList==null)outerList=new ArrayList<List<String>>();
				
				List<String> innerList=new ArrayList<String>();
				innerList.add(rs.getString("goal_id"));
				innerList.add(rs.getString("goal_type"));
				innerList.add(rs.getString("goal_parent_id"));
				innerList.add(rs.getString("measure_type"));
				innerList.add(rs.getString("measure_currency_value"));
				innerList.add(rs.getString("measure_effort_days"));
				innerList.add(rs.getString("measure_effort_hrs"));
				innerList.add(rs.getString("emp_ids"));				
				outerList.add(innerList);
				
				Map<String, String> hmIndGoalCalDetails = new HashMap<String, String>();
				getIndividualGoalTargetCalculation(rs.getString("goal_id"), rs.getString("emp_ids"), rs.getString("measure_type"), rs.getString("measure_currency_value"),
						rs.getString("measure_effort_days"), rs.getString("measure_effort_hrs"), uF, hmIndGoalCalDetails);
//				System.out.println("hmIndGoalCalDetails==="+hmIndGoalCalDetails);
				hmIndGoalCalTeam.put(rs.getString("goal_id"), hmIndGoalCalDetails);
				hmIndGoalCalTeamParent.put(rs.getString("goal_parent_id"), hmIndGoalCalTeam);
			}
			rs.close();
			pst.close();
//			System.out.println("hmIndGoalCalTeamParent ===>> " + hmIndGoalCalTeamParent);
			
			String alltwoDeciTotProgressAvg ="0";
	 		String alltotal100 ="100";
	 		String strtwoDeciTot = "0";
//	 		String strTotTarget = "0";
//	 		String strTotDays = "0";
//	 		String strTotHrs = "0"; 
	 		double dblalltwoDeciTotProgressAvg = 0;
	 		double dblalltotal100 = 0;
	 		double dblstrtwoDeciTot = 0;
//	 		double dblstrTotTarget = 0;
//	 		int dblstrTotDays = 0;
//	 		int dblstrTotHrs = 0;
//	 		Map<String, Map<String, String>> hmIndGoalCalDetailsTeam = new HashMap<String, Map<String, String>>();
			Iterator it1 = hmIndGoalCalTeamParent.keySet().iterator();
			while (it1.hasNext()) {
//				Map<String, String> hmIndGoalCalDetailsParent = new HashMap<String, String>();
				String parentid =(String)it1.next();
//				System.out.println("parentid ===> "+parentid);
				Map<String,Map<String,String>> hmIndGoalCalTeam1 = hmIndGoalCalTeamParent.get(parentid);
//				System.out.println("hmIndGoalCalTeam1 ===> "+hmIndGoalCalTeam1);
				Iterator it2 = hmIndGoalCalTeam1.keySet().iterator();
				int cnt=0;
				while (it2.hasNext()) {
					String goalid =(String)it2.next();
//					System.out.println("team goal ----------- goalid ===> "+goalid);
					Map<String, String> hmIndGoalCalDetails = hmIndGoalCalTeam1.get(goalid);
//					System.out.println("hmIndGoalCalDetails ===> "+hmIndGoalCalDetails);
//					System.out.println("hmIndGoalCalDetails_PERCENT ===> "+hmIndGoalCalDetails.get(goalid+"_PERCENT") +"hmIndGoalCalDetails_TOTAL ===> "+hmIndGoalCalDetails.get(goalid+"_TOTAL"));
					dblalltwoDeciTotProgressAvg += uF.parseToDouble(hmIndGoalCalDetails.get(goalid+"_PERCENT"));
//					dblalltotal100 += uF.parseToDouble(hmIndGoalCalDetails.get(goalid+"_TOTAL"));
					double tot100 = uF.parseToDouble(hmIndGoalCalDetails.get(goalid+"_TOTAL"));
//					System.out.println("tot100 -----> "+tot100);
					if(tot100 == 0) {
						tot100 = 100;	
					}
					dblalltotal100 += tot100;
					dblstrtwoDeciTot += uF.parseToDouble(hmIndGoalCalDetails.get(goalid+"_STR_PERCENT"));
//					dblstrTotTarget += uF.parseToDouble(hmIndGoalCalDetails.get(goalid+"_ACHIVED_TARGET"));
//					dblstrTotDays += uF.parseToInt(hmIndGoalCalDetails.get(goalid+"_ACHIVED_DAYS"));
//					dblstrTotHrs += uF.parseToInt(hmIndGoalCalDetails.get(goalid+"_ACHIVED_HRS"));
					cnt++;
				}
				double percentAmt = dblalltwoDeciTotProgressAvg / cnt;
				double totalAmt = dblalltotal100 / cnt;
				double strPercentAmt = dblstrtwoDeciTot / cnt;
//				double achievedTargetAmt = dblstrTotTarget / cnt;
//				int intTotDays = dblstrTotDays / cnt;
//				int intTotHrs = dblstrTotHrs / cnt;
				alltwoDeciTotProgressAvg = ""+Math.round(percentAmt);
				alltotal100 = ""+Math.round(totalAmt);
				strtwoDeciTot = ""+Math.round(strPercentAmt);
//				strTotTarget = uF.formatIntoTwoDecimalWithOutComma(achievedTargetAmt);
//				strTotDays = ""+intTotDays;
//				strTotHrs = ""+intTotHrs;
				hmIndGoalCalDetailsParent.put(parentid+"_PERCENT", alltwoDeciTotProgressAvg);
				hmIndGoalCalDetailsParent.put(parentid+"_TOTAL", alltotal100);
				hmIndGoalCalDetailsParent.put(parentid+"_STR_PERCENT", strtwoDeciTot);
//				hmIndGoalCalDetailsParent.put(parentid+"_ACHIVED_TARGET", strTotTarget);
//				hmIndGoalCalDetailsParent.put(parentid+"_ACHIVED_DAYS", strTotDays);
//				hmIndGoalCalDetailsParent.put(parentid+"_ACHIVED_HRS", strTotHrs);
				
//				hmIndGoalCalDetailsTeam.put(parentid, hmIndGoalCalDetailsParent);
//				System.out.println("hmIndGoalCalDetailsTeam ===> "+hmIndGoalCalDetailsTeam);
			}
//			request.setAttribute("hmIndGoalCalDetailsTeam", hmIndGoalCalDetailsTeam);
//			System.out.println("hmIndGoalCalTeam  "+hmIndGoalCalTeam);
//			System.out.println("hmIndGoalCalDetailsParent  "+hmIndGoalCalDetailsParent);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	private void getManagerDetails(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		Map<String, Map<String, String>> hmTeamGoalCalDetailsManager = new HashMap<String, Map<String, String>>();
		try {
			con = db.makeConnection(con);
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			Map<String,List<List<String>>> hmManager = new LinkedHashMap<String, List<List<String>>>();
			Map<String,String> hmAttribute = CF.getAttributeMap(con);
				
			StringBuilder sbQuery=new StringBuilder();
			sbQuery.append("select * from goal_details where goal_type="+MANAGER_GOAL+" ");
			if(strUserType != null && strUserType.equals(MANAGER) && getCurrUserType() != null && !getCurrUserType().equals(strBaseUserType)) {
				sbQuery.append(" and emp_ids like '%,"+strSessionEmpId+",%' ");
			} else {
				sbQuery.append(" and org_id = "+uF.parseToInt(getF_org())+" ");
			}
			if(getDataType() != null && getDataType().equals("L")) {
				sbQuery.append(" and is_close = false ");
			} else if(getDataType() != null && getDataType().equals("C")) {
				sbQuery.append(" and is_close = true ");
			}
			sbQuery.append(" order by goal_id desc");
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();
//			System.out.println("pst =====> "+pst);
			while (rs.next()) {
				
				List<List<String>> outerList=hmManager.get(rs.getString("goal_parent_id"));
				if(outerList==null)outerList=new ArrayList<List<String>>();
				List<String> innerList=new ArrayList<String>();
				innerList.add(rs.getString("goal_id"));
				innerList.add(rs.getString("goal_type"));
				innerList.add(rs.getString("goal_parent_id"));
				innerList.add(rs.getString("goal_title"));
				innerList.add(rs.getString("goal_objective"));
				innerList.add(rs.getString("goal_description"));
				innerList.add(uF.showData(hmAttribute.get(rs.getString("goal_attribute")),""));
				innerList.add(rs.getString("measure_type"));
				innerList.add(rs.getString("measure_currency_value"));
				innerList.add(rs.getString("measure_currency_id"));
				innerList.add(rs.getString("measure_effort_days"));
				innerList.add(rs.getString("measure_effort_hrs"));
				innerList.add(rs.getString("measure_type1"));
				innerList.add(rs.getString("measure_kra"));
				innerList.add(rs.getString("measure_currency_value1"));
				innerList.add(rs.getString("measure_currency1_id"));
				innerList.add(uF.getDateFormat(rs.getString("due_date"), DBDATE, CF.getStrReportDateFormat()) );
				innerList.add(rs.getString("is_feedback"));
				innerList.add(rs.getString("orientation_id"));
				innerList.add(rs.getString("weightage"));
				innerList.add(uF.showData(getAppendData(con, rs.getString("emp_ids"), hmEmpName), ""));
				innerList.add(uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));
				innerList.add(rs.getString("user_id"));
				innerList.add(rs.getString("is_measure_kra"));
				innerList.add(rs.getString("measure_kra_days"));
				innerList.add(rs.getString("measure_kra_hrs"));
				innerList.add(rs.getString("grade_id"));
				innerList.add(rs.getString("level_id"));
				innerList.add(rs.getString("kra"));
				
				innerList.add(rs.getString("emp_ids"));	
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
				innerList.add(priority); //30
				innerList.add(uF.getDateFormat(rs.getString("effective_date"), DBDATE, CF.getStrReportDateFormat()) ); //31
				innerList.add(rs.getString("goal_creater_id")); //32
				innerList.add(pClass); //33
				innerList.add(rs.getString("is_close")); //34
				innerList.add(hmEmpName.get(rs.getString("user_id"))); //34
				
				outerList.add(innerList);
				hmManager.put(rs.getString("goal_parent_id"), outerList);
				
				Map<String, String> hmTeamGoalCalDetailsParentManager = new HashMap<String, String>();
				getTeamGoalData(con, uF, rs.getString("goal_id"), hmTeamGoalCalDetailsParentManager);
				hmTeamGoalCalDetailsManager.put(rs.getString("goal_id"), hmTeamGoalCalDetailsParentManager);
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmManager",hmManager);
			request.setAttribute("hmTeamGoalCalDetailsManager", hmTeamGoalCalDetailsManager);
//			System.out.println("hmManager ===> "+hmManager);
//			System.out.println("hmTeamGoalCalDetailsManager ===> "+hmTeamGoalCalDetailsManager);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}


	private void getTeamGoalData(Connection con, UtilityFunctions uF, String parentID, Map<String, String> hmTeamGoalCalDetailsParentManager) {
		PreparedStatement pst = null;
		ResultSet rs = null;
//		Map<String,List<List<String>>> hmIndividual=new LinkedHashMap<String, List<List<String>>>();
		Map<String,Map<String,String>> hmTeamGoalCalManager = new HashMap<String, Map<String,String>>();
		Map<String,Map<String,Map<String,String>>> hmTeamGoalCalManagerParent= new HashMap<String, Map<String,Map<String,String>>>();
		try {
			
			pst = con.prepareStatement("select * from goal_details where goal_parent_id = ? order by goal_id ");  
			pst.setInt(1, uF.parseToInt(parentID));
			rs = pst.executeQuery();
			while (rs.next()) {
//				List<List<String>> outerList=hmIndividual.get(rs.getString("goal_parent_id"));
//				if(outerList==null)outerList=new ArrayList<List<String>>();
				
				List<String> innerList=new ArrayList<String>();
				innerList.add(rs.getString("goal_id"));
				innerList.add(rs.getString("goal_parent_id"));
//				outerList.add(innerList);
				
				Map<String, String> hmManagerGoalCalDetails = new HashMap<String, String>();
				getIndGoalData(con, uF, rs.getString("goal_id"), hmManagerGoalCalDetails);
				hmTeamGoalCalManager.put(rs.getString("goal_id"), hmManagerGoalCalDetails);
//				System.out.println("hmTeamGoalCalManager ===> "+hmTeamGoalCalManager);
				hmTeamGoalCalManagerParent.put(rs.getString("goal_parent_id"), hmTeamGoalCalManager);
			}
			rs.close();
			pst.close();
			
//			System.out.println("hmTeamGoalCalManagerParent ===> "+hmTeamGoalCalManagerParent);
			
			String alltwoDeciTotProgressAvg ="0";
	 		String alltotal100 ="100";
	 		String strtwoDeciTot = "0";
	 		double dblalltwoDeciTotProgressAvg = 0;
	 		double dblalltotal100 = 0;
	 		double dblstrtwoDeciTot = 0;
			Iterator it1 = hmTeamGoalCalManagerParent.keySet().iterator();
			while (it1.hasNext()) {
				String parentid =(String)it1.next();
//				System.out.println("parentid ===> "+parentid);
				Map<String,Map<String,String>> hmTeamGoalCalManager1 = hmTeamGoalCalManagerParent.get(parentid);
//				System.out.println("hmTeamGoalCalManager1 ===> " + hmTeamGoalCalManager1);
				Iterator it2 = hmTeamGoalCalManager1.keySet().iterator();
				int cnt=0;
				while (it2.hasNext()) {
					String goalid =(String)it2.next();
//					System.out.println("getTeamGoalData goalid ===> "+goalid);
					Map<String, String> hmTeamGoalCalDetails = hmTeamGoalCalManager1.get(goalid);
//					System.out.println("hmTeamGoalCalDetails_PERCENT ===> "+ hmTeamGoalCalDetails.get(goalid+"_PERCENT") + "hmTeamGoalCalDetails_TOTAL ===> "+ hmTeamGoalCalDetails.get(goalid+"_TOTAL"));
					dblalltwoDeciTotProgressAvg += uF.parseToDouble(hmTeamGoalCalDetails.get(goalid+"_PERCENT"));
//					dblalltotal100 += uF.parseToDouble(hmTeamGoalCalDetails.get(goalid+"_TOTAL"));
					double tot100 = uF.parseToDouble(hmTeamGoalCalDetails.get(goalid+"_TOTAL"));
//					System.out.println("tot100 -----> "+tot100);
					if(tot100 == 0) {
						tot100 = 100;	
					}
					dblalltotal100 += tot100;
					dblstrtwoDeciTot += uF.parseToDouble(hmTeamGoalCalDetails.get(goalid+"_STR_PERCENT"));
					cnt++;
				}
//				System.out.println("dblalltwoDeciTotProgressAvg team ===> "+dblalltwoDeciTotProgressAvg);
//				System.out.println("dblalltotal100 team ===> "+dblalltotal100);
//				System.out.println("cnt team ===> "+cnt);
				double percentAmt = dblalltwoDeciTotProgressAvg / cnt;
				double totalAmt = dblalltotal100 / cnt;
				double strPercentAmt = dblstrtwoDeciTot / cnt;
				alltwoDeciTotProgressAvg = ""+Math.round(percentAmt);
				alltotal100 = ""+Math.round(totalAmt);
				strtwoDeciTot = ""+Math.round(strPercentAmt);
				hmTeamGoalCalDetailsParentManager.put(parentid+"_PERCENT", alltwoDeciTotProgressAvg);
				hmTeamGoalCalDetailsParentManager.put(parentid+"_TOTAL", alltotal100);
				hmTeamGoalCalDetailsParentManager.put(parentid+"_STR_PERCENT", strtwoDeciTot);
			}
//			System.out.println("hmTeamGoalCalDetailsParentManager ===> "+hmTeamGoalCalDetailsParentManager);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	private void getCorporateDetails(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		Map<String, Map<String, String>> hmManagerGoalCalDetailsCorporate = new HashMap<String, Map<String, String>>();
		try {
			con = db.makeConnection(con);
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			Map<String,List<String>> hmCorporate = new LinkedHashMap<String, List<String>>();
			Map<String,String> hmAttribute = CF.getAttributeMap(con);
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from goal_details where goal_type="+CORPORATE_GOAL+" ");
//			if(uF.parseToInt(getGoalId())>0) {
				sbQuery.append(" and goal_id = " +uF.parseToInt(getGoalId()));
//			}
			if(strUserType != null && strUserType.equals(MANAGER) && getCurrUserType() != null && !getCurrUserType().equals(strBaseUserType)) {
				sbQuery.append(" and emp_ids like '%,"+strSessionEmpId+",%' ");
			} else {
				sbQuery.append(" and org_id = "+uF.parseToInt(getF_org())+" ");
			}
			if(getDataType() != null && getDataType().equals("L")) {
				sbQuery.append(" and is_close = false ");
			} else if(getDataType() != null && getDataType().equals("C")) {
				sbQuery.append(" and is_close = true ");
			}
			sbQuery.append(" order by goal_id desc");
			pst = con.prepareStatement(sbQuery.toString());
//			pst.setInt(1, uF.parseToInt(getStrOrg()));
			rs = pst.executeQuery();
//			System.out.println("pst ===> "+pst);
			while (rs.next()) {
				
				List<String> cinnerList=new ArrayList<String>();
				cinnerList.add(rs.getString("goal_id"));
				cinnerList.add(rs.getString("goal_type"));
				cinnerList.add(rs.getString("goal_parent_id"));
				cinnerList.add(rs.getString("goal_title"));
				cinnerList.add(rs.getString("goal_objective"));
				cinnerList.add(rs.getString("goal_description"));
				cinnerList.add(uF.showData(hmAttribute.get(rs.getString("goal_attribute")), ""));
				cinnerList.add(rs.getString("measure_type"));
				cinnerList.add(rs.getString("measure_currency_value"));
				cinnerList.add(rs.getString("measure_currency_id"));
				cinnerList.add(rs.getString("measure_effort_days"));
				cinnerList.add(rs.getString("measure_effort_hrs"));
				cinnerList.add(rs.getString("measure_type1"));
				cinnerList.add(rs.getString("measure_kra"));
				cinnerList.add(rs.getString("measure_currency_value1"));
				cinnerList.add(rs.getString("measure_currency1_id"));
				cinnerList.add(uF.getDateFormat(rs.getString("due_date"), DBDATE, CF.getStrReportDateFormat()));
				cinnerList.add(rs.getString("is_feedback"));
				cinnerList.add(rs.getString("orientation_id"));
				cinnerList.add(rs.getString("weightage"));
				cinnerList.add(uF.showData(getAppendData(con, rs.getString("emp_ids"), hmEmpName), ""));
				cinnerList.add(uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));
				cinnerList.add(rs.getString("user_id"));
				cinnerList.add(rs.getString("is_measure_kra"));
				cinnerList.add(rs.getString("measure_kra_days"));
				cinnerList.add(rs.getString("measure_kra_hrs"));
				cinnerList.add(rs.getString("grade_id"));
				cinnerList.add(rs.getString("level_id"));
				cinnerList.add(rs.getString("kra"));
				
				cinnerList.add(rs.getString("emp_ids"));
				String priority="";
				String pClass = "";
				if(rs.getString("priority")!=null && !rs.getString("priority").equals("")) {
					if(rs.getString("priority").equals("1")) {
						pClass = "high";
						priority="High";
					} else if(rs.getString("priority").equals("2")) {
						pClass = "medium";
						priority="Medium";
					} else if(rs.getString("priority").equals("3")) {
						pClass = "low";
						priority="Low";
					}
				}
				cinnerList.add(priority); //30
				cinnerList.add(uF.getDateFormat(rs.getString("effective_date"), DBDATE, CF.getStrReportDateFormat())); //31
				cinnerList.add(pClass); //32
				cinnerList.add(rs.getString("is_close")); //33
				cinnerList.add(hmEmpName.get(rs.getString("user_id"))); //34
				List<String> alWithDepartWithTeam = new ArrayList<String>();
				if(rs.getString("with_depart_with_team")!=null) {
					alWithDepartWithTeam = Arrays.asList(rs.getString("with_depart_with_team").split(","));
				} else {
					alWithDepartWithTeam.add("1");
					alWithDepartWithTeam.add("1");
				}
				cinnerList.add(alWithDepartWithTeam.get(0)); //35
				cinnerList.add(alWithDepartWithTeam.get(1)); //36
				
				hmCorporate.put(rs.getString("goal_id"), cinnerList);
				Map<String, String> hmManagerGoalCalDetailsParentCorporate = new HashMap<String, String>();
				getManagerGoalData(con, uF, rs.getString("goal_id"), hmManagerGoalCalDetailsParentCorporate);
				hmManagerGoalCalDetailsCorporate.put(rs.getString("goal_id"), hmManagerGoalCalDetailsParentCorporate);
			}
			rs.close();
			pst.close();
			request.setAttribute("hmCorporate", hmCorporate);
			request.setAttribute("hmManagerGoalCalDetailsCorporate", hmManagerGoalCalDetailsCorporate);
//			System.out.println("hmManagerGoalCalDetailsCorporate =========> "+hmManagerGoalCalDetailsCorporate);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	public void getManagerGoalData(Connection con, UtilityFunctions uF, String parentID, Map<String, String> hmManagerGoalCalDetailsParentCorporate) {
		PreparedStatement pst = null;
		ResultSet rs = null;
//		Map<String,List<List<String>>> hmIndividual=new LinkedHashMap<String, List<List<String>>>();
		Map<String,Map<String,String>> hmManagerGoalCalCorporate = new HashMap<String, Map<String,String>>();
		Map<String,Map<String,Map<String,String>>> hmManagerGoalCalCorporateParent= new HashMap<String, Map<String,Map<String,String>>>();
		try {
			
			pst = con.prepareStatement("select * from goal_details where goal_parent_id = ? order by goal_id ");
			pst.setInt(1, uF.parseToInt(parentID));
			rs = pst.executeQuery();
			while (rs.next()) {
//				List<List<String>> outerList=hmIndividual.get(rs.getString("goal_parent_id"));
//				if(outerList==null)outerList=new ArrayList<List<String>>();
				List<String> innerList=new ArrayList<String>();
				innerList.add(rs.getString("goal_id"));
				innerList.add(rs.getString("goal_parent_id"));
//				outerList.add(innerList);
				
				Map<String, String> hmCorporateGoalCalDetails = new HashMap<String, String>();
				getTeamGoalData(con, uF, rs.getString("goal_id"), hmCorporateGoalCalDetails);
				hmManagerGoalCalCorporate.put(rs.getString("goal_id"), hmCorporateGoalCalDetails);
//				System.out.println("hmManagerGoalCalCorporate ===> "+hmManagerGoalCalCorporate);
				hmManagerGoalCalCorporateParent.put(rs.getString("goal_parent_id"), hmManagerGoalCalCorporate);
			}
			rs.close();
			pst.close();
			
//			System.out.println("hmManagerGoalCalCorporateParent ===> "+hmManagerGoalCalCorporateParent);
			
			String alltwoDeciTotProgressAvg ="0";
	 		String alltotal100 ="100";
	 		String strtwoDeciTot = "0";
	 		double dblalltwoDeciTotProgressAvg = 0;
	 		double dblalltotal100 = 0;
	 		double dblstrtwoDeciTot = 0;
			Iterator it1 = hmManagerGoalCalCorporateParent.keySet().iterator();
			while (it1.hasNext()) {
				String parentid =(String)it1.next();
//				System.out.println("parentid ===> "+parentid);
				Map<String,Map<String,String>> hmManagerGoalCalCorporate1 = hmManagerGoalCalCorporateParent.get(parentid);
//				System.out.println("hmManagerGoalCalCorporate1 ===> " + hmManagerGoalCalCorporate1);
				Iterator it2 = hmManagerGoalCalCorporate1.keySet().iterator();
				int cnt=0;
				while (it2.hasNext()) {
					String goalid =(String)it2.next();
//					System.out.println("getManagerGoalData goalid ===> "+goalid);
					Map<String, String> hmManagerGoalCalDetails = hmManagerGoalCalCorporate1.get(goalid);
//					System.out.println("hmManagerGoalCalDetails_PERCENT ===> "+ hmManagerGoalCalDetails.get(goalid+"_PERCENT") + "hmManagerGoalCalDetails_TOTAL ===> "+ hmManagerGoalCalDetails.get(goalid+"_TOTAL"));
					dblalltwoDeciTotProgressAvg += uF.parseToDouble(hmManagerGoalCalDetails.get(goalid+"_PERCENT"));
					double tot100 = uF.parseToDouble(hmManagerGoalCalDetails.get(goalid+"_TOTAL"));
//					System.out.println("tot100 -----> "+tot100);
					if(tot100 == 0) {
						tot100 = 100;	
					}
					dblalltotal100 += tot100;
					dblstrtwoDeciTot += uF.parseToDouble(hmManagerGoalCalDetails.get(goalid+"_STR_PERCENT"));
					cnt++;
				}
//				System.out.println("dblalltwoDeciTotProgressAvg ===> " + dblalltwoDeciTotProgressAvg);
//				System.out.println("dblalltotal100 ===> " + dblalltotal100);
//				System.out.println("cnt ===> " + cnt);
				double percentAmt = dblalltwoDeciTotProgressAvg / cnt;
				double totalAmt = dblalltotal100 / cnt;
				double strPercentAmt = dblstrtwoDeciTot / cnt;
				alltwoDeciTotProgressAvg = ""+Math.round(percentAmt);
				alltotal100 = ""+Math.round(totalAmt);
				strtwoDeciTot = ""+Math.round(strPercentAmt);
				hmManagerGoalCalDetailsParentCorporate.put(parentid+"_PERCENT", alltwoDeciTotProgressAvg);
				hmManagerGoalCalDetailsParentCorporate.put(parentid+"_TOTAL", alltotal100);
				hmManagerGoalCalDetailsParentCorporate.put(parentid+"_STR_PERCENT", strtwoDeciTot);
			}
//			System.out.println("hmManagerGoalCalDetailsParentCorporate ===> "+hmManagerGoalCalDetailsParentCorporate);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	private String getAppendData(Connection con, String strID, Map<String, String> mp) {
		StringBuilder sb = new StringBuilder();
		Map<String, String> hmDesignation = CF.getEmpDesigMap(con);
		if (strID != null && !strID.equals("")) {

			if (strID.contains(",")) {

				String[] temp = strID.split(",");

				for (int i = 0; i < temp.length; i++) {
					if (i == 0) {
						sb.append(mp.get(temp[i].trim())+"("+hmDesignation.get(temp[i].trim())+")");
					} else {
						sb.append("," + mp.get(temp[i].trim())+"("+hmDesignation.get(temp[i].trim())+")");
					}
				}
			} else {
				return mp.get(strID)+"("+hmDesignation.get(strID)+")";
			}
		} else {
			return null;
		}
		return sb.toString();
	}
	
	public String getF_org() {
		return f_org;
	}

	public void setF_org(String f_org) {
		this.f_org = f_org;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getCurrUserType() {
		return currUserType;
	}

	public void setCurrUserType(String currUserType) {
		this.currUserType = currUserType;
	}

	public String getGoalId() {
		return goalId;
	}

	public void setGoalId(String goalId) {
		this.goalId = goalId;
	}


	public HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {

		this.request = request;
	}
	
}
