package com.konnect.jpms.roster;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillEmployee;
import com.konnect.jpms.select.FillGender;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillRosterWeeklyOff;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.ArrayUtils;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class ManageRoster extends ActionSupport implements ServletRequestAware, IStatements, ServletResponseAware {

	private static final long serialVersionUID = 1L;

	HttpSession session;
	CommonFunctions CF;
	private List<FillShift> shiftList;
	private List<FillCostCenter> costList;
	private List<FillEmployee> empList; 
	private List<FillOrganisation> orgList;
	private List<FillWLocation> wLocationList;
	private List<FillRosterWeeklyOff> rosterWOffList;
	
	private String strLocation;
	private String strDepartment;
	private String strLevel;
	private String strCostCenterName;
	private String manager;
	
	private String strOrg;
	private String[] f_strWLocation;
	private String[] f_level;
	private String[] f_department;

	private List<FillDepartment> departmentList;
	private List<FillLevel> levelList;
	
	private boolean isWeekEnd;
	private boolean isHoliday;
	private String[] strIsAssig;
	
	private String[] rosterPolicyRuleIds;
	
	private String shiftName;
	private String[] costCenterName; 
	private String toDate;
	private String fromDate; 
	private String employeeName;
	private boolean selected = false;
	private List<String> innerShiftName;
	private String shiftCode;
	private List<List<String>> al;
	
	Map hmEmpName = null;
	Map hmShiftMap = null;
	 
	List<FillGender> genderList = null;
	
	private String strGender;
	String strUserType = null;
	String strSesionEmpId = null;
	
	private String isRosterDependant;
	private List<String> strRosterWOff;
	private String f_RosterOff;
	
	private String[] strManager;
	private List<FillEmployee> managerList;
	
	private String strUpdate;
	private String update;
	private String checkRule;
	
	private String defaultRosterDependant;
	
	public String execute() throws Exception {
		
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null) return LOGIN;
		
		UtilityFunctions uF = new UtilityFunctions();
		request.setAttribute(PAGE, "/jsp/roster/ManageRoster.jsp");
		request.setAttribute(TITLE, "Manage Shifts");
		strUserType = (String) session.getAttribute(USERTYPE);
		strSesionEmpId = (String)session.getAttribute(EMPID);
		
		/*boolean isView  = CF.getAccess(session, request, uF);
		if(!isView) {
			request.setAttribute(PAGE, PAccessDenied);
			request.setAttribute(TITLE, TAccessDenied);
			return ACCESS_DENIED;
		}*/
		
		if(uF.parseToInt(getStrOrg()) == 0) {
			setStrOrg((String)session.getAttribute(ORGID));
		}
		
		if(getIsRosterDependant() == null || getIsRosterDependant().trim().equals("") || getIsRosterDependant().trim().equalsIgnoreCase("NULL")) {
			setDefaultRosterDependant("true");
		} else {
			setDefaultRosterDependant(""+uF.parseToBoolean(getIsRosterDependant()));
		}

		shiftDetails(uF);
		
		shiftList = new FillShift(request).fillShiftByOrg(uF.parseToInt(getStrOrg()));
		request.setAttribute("shift", shiftList);
		
		if(getStrLocation() != null && !getStrLocation().equals("")) {
			setF_strWLocation(getStrLocation().split(","));
		}/* else {
			setF_strWLocation(null);
		}*/
		if(getStrDepartment() != null && !getStrDepartment().equals("")) {
			setF_department(getStrDepartment().split(","));
		}/* else {
			setF_department(null);
		}*/
		if(getStrLevel() != null && !getStrLevel().equals("")) {
			setF_level(getStrLevel().split(","));
		}/* else {
			setF_level(null);
		}*/
		
		if(getManager() != null) {
			setStrManager(getManager().split(","));
		}
		if(getStrCostCenterName() != null) {
			setCostCenterName(getStrCostCenterName().split(","));
		}		
		
		genderList = new FillGender().fillGender();
		costList = new FillCostCenter(request).fillCostCenter(getStrOrg());
		departmentList = new FillDepartment(request).fillDepartment(uF.parseToInt(getStrOrg()));
		levelList = new FillLevel(request).fillLevel(uF.parseToInt(getStrOrg()));
		
		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)) {
			orgList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
			wLocationList = new FillWLocation(request).fillWLocation(getStrOrg(),(String) session.getAttribute(WLOCATION_ACCESS));
		} else {
			orgList = new FillOrganisation(request).fillOrganisation();
			wLocationList = new FillWLocation(request).fillWLocation(getStrOrg());
		}
		rosterWOffList = new FillRosterWeeklyOff(request).fillRosterWOffByOrg(uF.parseToInt(getStrOrg()));
		
		managerList = new FillEmployee(request).fillManagerByorg(uF.parseToInt(getStrOrg()));

//		System.out.println("getStrUpdate=="+getStrUpdate()+"--getUpdate=="+getUpdate());
//		if(uF.parseToInt(getCostCenterName()) > 0 && getShiftName()!=null) {
//		if(getShiftName()!=null) {
		empList = getEmployeeList();			
		request.setAttribute("empList", empList);
		getRosterPolicyRules();
		
		al = new ArrayList<List<String>>();
		for(int i=0;i<empList.size();i++) {
			String Empid = empList.get(i).getEmployeeId();
			al.add(shiftIdFromDates(Empid));
		}
		
		if(getCheckRule() != null && !getCheckRule().trim().equals("") && !getCheckRule().trim().equalsIgnoreCase("NULL")) {
			checkShiftRosterRules(uF);
			return "ajax";
		}
		
		if(getStrUpdate() != null && !getStrUpdate().trim().equals("") && !getStrUpdate().trim().equalsIgnoreCase("NULL")) {	
//			System.out.println("getInnerShiftName=="+getInnerShiftName());
			if(getShiftName()!=null) {
				insertShiftRoster(uF);
				al.clear();
				for(int i=0;i<empList.size();i++) {
					String Empid = empList.get(i).getEmployeeId();
					al.add(shiftIdFromDates(Empid));
				}
				
			}
		} else if(getUpdate() != null && !getUpdate().trim().equals("") && !getUpdate().trim().equalsIgnoreCase("NULL")) {
			if(getInnerShiftName()!=null) {
				insertShiftRoster(uF);
				al.clear();
				for(int i=0;i<empList.size();i++) {
					String Empid = empList.get(i).getEmployeeId();
					al.add(shiftIdFromDates(Empid));
				}
			}
		}
		
		if(al!=null) {
			getSalaryPaidEmployee(uF,empList);
			request.setAttribute("shiftList", al);
		}
		
		return SUCCESS;
	}
	

	private String checkShiftRosterRules(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst1=null;
		PreparedStatement pst=null;
		PreparedStatement pst3=null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request); 

		try {
			con = db.makeConnection(con);
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			Map<String, String> hmLevelName = CF.getLevelMap(con);
			Map<String, String> hmLocationName = CF.getWLocationMap(con, null, null);
			
			List<String> alAssignEmp = getStrIsAssig() != null ? Arrays.asList(getStrIsAssig()) : new ArrayList<String>();
			
			List<String> alRPRulesIds = getRosterPolicyRuleIds() != null ? Arrays.asList(getRosterPolicyRuleIds()) : new ArrayList<String>();
			
			int z=0;
			int nEmpSize = empList!=null ? empList.size() : 0;
			Map<String, String> hmEmpCurrShiftId = new HashMap<String, String>();
			for(int i = 0; i < nEmpSize;i++) {
				String strEmpId = empList.get(i).getEmployeeId();
				
				String strShiftId = getShiftName();
				hmEmpCurrShiftId.put(strEmpId, strShiftId);

				z++;
			}
			
			StringBuilder sbMsg = new StringBuilder();
			System.out.println("alRPRulesIds ===>> " + alRPRulesIds);
			if(alRPRulesIds != null && !alRPRulesIds.isEmpty()) {
				List<String> alList = new ArrayList<String>();
				
				for(int i=0; i<alRPRulesIds.size(); i++) {
					pst = con.prepareStatement("select * from roster_policy_rules where roster_policy_rule_id=?");
					pst.setInt(1, uF.parseToInt(alRPRulesIds.get(i)));
					rs = pst.executeQuery();
					while(rs.next()){
						alList.add(rs.getString("min_no_of_member_in_shift")); //0
						alList.add(rs.getString("min_no_of_member_in_shift_at_weekend")); //1
						String str1 = rs.getString("no_of_leads_from_levels_for_no_of_member"); 
						if(str1 != null) {
							String[] strArr = str1.split(":_:");
							if(strArr.length>1) {
								alList.add(strArr[0]); //2
								if(strArr[1]!=null && !strArr[1].equals("")) {
									String[] arr = strArr[1].split(",");
									StringBuilder sbLevel = null;
									StringBuilder sbLevelName = null;
									for(i=0; arr!=null && i<arr.length; i++) {
										if(sbLevel == null) {
											sbLevel = new StringBuilder();
											sbLevel.append(arr[i]);
											sbLevelName = new StringBuilder();
											sbLevelName.append(hmLevelName.get(arr[i]));
										} else {
											sbLevel.append(","+arr[i]);
											sbLevelName.append(", "+hmLevelName.get(arr[i]));
										}
									}
									alList.add(sbLevel.toString()); //3
									alList.add(sbLevelName.toString()); //4
								} else {
									alList.add(""); //3
									alList.add(""); //4
								}
								alList.add(strArr[2]); //5
							} else {
								alList.add(""); //2
								alList.add(""); //3
								alList.add(""); //4
								alList.add(""); //5
							}
						}
						
						alList.add(rs.getString("min_weekend_off_per_month")); //6
						alList.add(rs.getString("max_no_of_shifts_per_member_per_month")); //7
						alList.add(rs.getString("min_break_days_in_stretch_shift")); //8
						alList.add(rs.getString("min_days_off_between_shifts")); //9
						
						String str2 = rs.getString("member_location_associated_locations"); //
						if(str2 != null && !str2.equals("''")) {
							String[] strArr = str2.split(":_:");
							if(strArr.length>1) {
								alList.add(strArr[0]); //10
								if(strArr[1]!=null && !strArr[1].equals("")) {
									String[] arr = strArr[1].split(",");
									StringBuilder sbLocation = null;
									StringBuilder sbLocationName = null;
									for(i=0; arr!=null && i<arr.length; i++) {
										if(sbLocation == null) {
											sbLocation = new StringBuilder();
											sbLocation.append(arr[i]);
											sbLocationName = new StringBuilder();
											sbLocationName.append(hmLocationName.get(arr[i]));
										} else {
											sbLocation.append(","+arr[i]);
											sbLocationName.append(", "+hmLocationName.get(arr[i]));
										}
									}
									alList.add(sbLocation.toString()); //11
									alList.add(sbLocationName.toString()); //12
								} else {
									alList.add(""); //11
									alList.add(""); //12
								}
							} else {
								alList.add(""); //10
								alList.add(""); //11
								alList.add(""); //12
							}
						}
						alList.add(rs.getString("min_male_member_in_shift")); //13
					}
					rs.close();
					pst.close();
				}
				
				
//				Map<String, Set<String>> hmWeekEndDates = CF.getWeekEndDateList(con, getFromDate(), getToDate(), CF, uF,null,null);
//				Map<String, Map<String, String>> hmRosterWeeklyoff = CF.getRosterWeeklyOffDetails(con,CF,uF);
//				if(hmRosterWeeklyoff == null) hmRosterWeeklyoff = new HashMap<String, Map<String,String>>();
				
				Map<String, List<String>> hmEmpBasicData = allLiveEmployeeBasicInfo(con);
				Map<String, Map<String, String>> hmEmpLastShiftData = getAllLiveEmployeeLastShiftDetails(con, uF);
				if(alAssignEmp!= null && !alAssignEmp.isEmpty()) {
					int existLvlEmpCnt = 0;
					int maleEmpCnt = 0;
					List<String> alLvls = new ArrayList<String>();
					if(alList.get(3) != null && !alList.get(3).isEmpty()) {
						alLvls = Arrays.asList(alList.get(3).split(","));
					}
					List<String> alAssociatedLocs = new ArrayList<String>();
					if(alList.get(11) != null && !alList.get(11).isEmpty()) {
						alAssociatedLocs = Arrays.asList(alList.get(11).split(","));
					}
//					System.out.println("alAssociatedLocs ===>> " + alAssociatedLocs);
					Map<String, String> hmEmpLocIds = new HashMap<String, String>();
					
					StringBuilder sbEmpNames = null;
					StringBuilder sbExistShiftEmpNames = null;
					for(int i=0; i<alAssignEmp.size(); i++) {
						List<String> alEmpBasicData = hmEmpBasicData.get(alAssignEmp.get(i));
						
						if(alLvls.contains(alEmpBasicData.get(3))) {
							existLvlEmpCnt++;
						}
						if(alEmpBasicData.get(3) != null && (alEmpBasicData.get(1).equalsIgnoreCase("M") || alEmpBasicData.get(1).equalsIgnoreCase("MALE"))) {
							maleEmpCnt++;
						}
						int empLocCnt = uF.parseToInt(hmEmpLocIds.get(alEmpBasicData.get(2)));
						empLocCnt++;
						hmEmpLocIds.put(alEmpBasicData.get(2), empLocCnt+"");
						int nOfBeforeDays = -1;
						int nOfAfterDays = -1;
						int nOfExistShiftCnt = 0;
						if(hmEmpLastShiftData!=null && !hmEmpLastShiftData.isEmpty()) {
							Map<String, String> hmInn = hmEmpLastShiftData.get(alAssignEmp.get(i));
							if(hmInn != null) {
								if(hmInn.get("MAX_DATE") != null) {
									nOfBeforeDays = uF.parseToInt(uF.dateDifference(hmInn.get("MAX_DATE"), DATE_FORMAT, getFromDate(), DATE_FORMAT, CF.getStrTimeZone()));
									nOfBeforeDays = nOfBeforeDays-1;
								}
								if(hmInn.get("MIN_DATE") != null) {
									nOfAfterDays = uF.parseToInt(uF.dateDifference(getToDate(), DATE_FORMAT, hmInn.get("MIN_DATE"), DATE_FORMAT, CF.getStrTimeZone()));
									nOfAfterDays = nOfAfterDays-1;
								}
								if(hmInn.get("SHIFT_COUNT") != null) {
									nOfExistShiftCnt = uF.parseToInt(hmInn.get("SHIFT_COUNT"));
									System.out.println(hmEmpName.get(alAssignEmp.get(i))+ " -- nOfExistShiftCnt ===>> " +nOfExistShiftCnt +" ===>> hmEmpCurrShiftId.get(alAssignEmp.get(i)) ===>> " + hmEmpCurrShiftId.get(alAssignEmp.get(i)) + " -- SHIFT_IDS ===>> " + hmInn.get("SHIFT_IDS"));
									if(hmInn.get("SHIFT_IDS") != null) {
										List<String> al = Arrays.asList(hmInn.get("SHIFT_IDS").split(","));
										if(!al.contains(hmEmpCurrShiftId.get(alAssignEmp.get(i)))) {
											nOfExistShiftCnt = nOfExistShiftCnt+1;
										}
									}
								}
							}
						}
						
						System.out.println(hmEmpName.get(alAssignEmp.get(i))+ " -- nOfBeforeDays ===>> " + nOfBeforeDays + " -- nOfAfterDays ===>> " + nOfAfterDays);
						if((nOfBeforeDays > -1 && nOfBeforeDays<=uF.parseToInt(alList.get(9))) || (nOfAfterDays > -1 && nOfAfterDays<=uF.parseToInt(alList.get(9)))) {
							if(sbEmpNames == null) {
								sbEmpNames = new StringBuilder();
								sbEmpNames.append(hmEmpName.get(alAssignEmp.get(i)));
							} else {
								sbEmpNames.append(", "+hmEmpName.get(alAssignEmp.get(i)));
							}
						}
						
						System.out.println(hmEmpName.get(alAssignEmp.get(i))+ " -- nOfExistShiftCnt ===>> " + nOfExistShiftCnt);
						if(nOfExistShiftCnt > uF.parseToInt(alList.get(7))) {
							if(sbExistShiftEmpNames == null) {
								sbExistShiftEmpNames = new StringBuilder();
								sbExistShiftEmpNames.append(hmEmpName.get(alAssignEmp.get(i)));
							} else {
								sbExistShiftEmpNames.append(", "+hmEmpName.get(alAssignEmp.get(i)));
							}
						}
						
					}
//					--------------- End Emp For Loop -----------------
					
//					System.out.println("hmEmpLocIds ===>> " + hmEmpLocIds);
					
					if(alAssignEmp.size() < uF.parseToInt(alList.get(0))) {
						sbMsg.append("Please select minimum '"+alList.get(0)+"' member in each shift.\n\n");
					}
					if(alAssignEmp.size() < uF.parseToInt(alList.get(1))) {
						sbMsg.append("Please select minimum '"+alList.get(1)+"' members in each shift on the weekend.\n\n");
					}
					
					if(uF.parseToDouble(existLvlEmpCnt+"") < uF.parseToDouble((alAssignEmp.size()/uF.parseToInt(alList.get(5))+"")) || existLvlEmpCnt < uF.parseToInt(alList.get(2))) {
						sbMsg.append("Shift must have at least '"+alList.get(2)+"' member(s) from these '"+alList.get(4)+"' level(s), for every '"+alList.get(5)+"' members.\n\n");
					}
					
					if(sbEmpNames != null) {
						sbMsg.append("Please set '"+alList.get(9)+"' days week off between shift transition for member '"+sbEmpNames .toString()+"'.\n\n");
					}
					
					if(sbExistShiftEmpNames != null) {
						sbMsg.append("You are trying to allocate more than '"+alList.get(7)+"' different kind of shifts in a month for member '"+sbExistShiftEmpNames .toString()+"'.\n\n");
					}
					
					int associateLocCnt = 0;
					int memberLocCnt = 0;
					if(hmEmpLocIds.keySet().contains(alList.get(10))) {
						memberLocCnt++;
					}
//					System.out.println("memberLocCnt ===>> " +memberLocCnt);
					
					for(int i=0; alAssociatedLocs!=null && i<alAssociatedLocs.size(); i++) {
						if(hmEmpLocIds.keySet().contains(alAssociatedLocs.get(i))) {
							associateLocCnt++;
						}
					}
//					System.out.println("associateLocCnt ===>> " +associateLocCnt);
					
					if(associateLocCnt < 1 || memberLocCnt < 1) {
						sbMsg.append("Members from '"+hmLocationName.get(alList.get(10))+"' work location should be associated with '"+alList.get(12)+"' work locations at all time.\n\n");
					}
					
					if(maleEmpCnt < uF.parseToInt(alList.get(13))) {
						sbMsg.append("A shift should constitute '"+alList.get(13)+"' male member.\n\n");
					}

				}
			}
			request.setAttribute("STATUS_MSG", sbMsg.toString());
			
			
			
			
//			Map<String, Map<String, String>> hmShiftTime = CF.getShiftTime(con);
//			Map hmEmpWLocation = CF.getEmpWlocationMap(con);
//			hmEmpName = CF.getEmpNameMap(con,null, null);
//			hmShiftMap = CF.getShiftMap(con);
//			 
//			Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
//			
//			Map<String, Set<String>> hmWeekEndDates = CF.getWeekEndDateList(con, getFromDate(), getToDate(), CF, uF,null,null);
//			Map<String,String> hmHolidays = new HashMap<String,String>();
//			Map<String,String> hmHolidayDates = new HashMap<String,String>();
//			CF.getHolidayList(con,request,getFromDate(), getToDate(), CF, hmHolidayDates, hmHolidays, true);
//			
//			Map<String, Map<String, String>> hmRosterWeeklyoff = CF.getRosterWeeklyOffDetails(con,CF,uF);
//			if(hmRosterWeeklyoff == null) hmRosterWeeklyoff = new HashMap<String, Map<String,String>>();
//			
//			int nOfdays=uF.parseToInt(uF.dateDifference(getFromDate(),DATE_FORMAT, getToDate(), DATE_FORMAT,CF.getStrTimeZone()));
//			int shiftSize=0;
//			int z=0;
//			Map<String, String> shiftMap ;
//			int nEmpSize = empList!=null ? empList.size() : 0;
//			for(int i = 0; i < nEmpSize;i++) {
//				String strEmpId = empList.get(i).getEmployeeId();
//				String strEmpServiceId = CF.getEmpServiceId(con, uF, uF.parseToInt(strEmpId));
//				
//				String userlocation = CF.getEmpWlocationId(con, uF, strEmpId);
//				pst = con.prepareStatement("select wlocation_id,wlocation_start_time,wlocation_end_time from work_location_info where wlocation_id =?");
//				pst.setInt(1, uF.parseToInt(userlocation));
//				rs = pst.executeQuery();
//				String locationstarttime = null;
//				String locationendtime = null;
//				while (rs.next()) {
//					locationstarttime = rs.getString("wlocation_start_time");
//					locationendtime = rs.getString("wlocation_end_time");
//				}
//				rs.close();
//				pst.close();
//				
//				String strShiftId = getInnerShiftName().get(z);
//				String strRosterOffId = getStrRosterWOff().get(z);
//				
//				if(getStrUpdate() != null && !getStrUpdate().trim().equals("") && !getStrUpdate().trim().equalsIgnoreCase("NULL")) {
//					strShiftId = getShiftName();
//					strRosterOffId = getF_RosterOff();
//				}
//				if(getUpdate() != null && !getUpdate().trim().equals("") && !getUpdate().trim().equalsIgnoreCase("NULL") && alAssignEmp.contains(strEmpId)) {
//					if(uF.parseToInt(strShiftId) == 0 || uF.parseToInt(strShiftId) ==-1) {
//						strShiftId = getShiftName();
//					}
//					if(uF.parseToInt(strRosterOffId) == 0 || uF.parseToInt(strRosterOffId) == -1) {
//						strRosterOffId = getF_RosterOff();
//					}
//				}
//				
//				if(uF.parseToInt(strShiftId)!=0) {
//					shiftSize= (al.get(i).size()+1)/4;
//				
//					if(shiftSize == 0 || shiftSize == 1) {
//						if(uF.parseToInt(strShiftId) == -1 || uF.parseToInt(strRosterOffId) == -1) {
//							z++;
//							continue;
//						}
//						for(int j = 0;j < nOfdays;j++) {
//							shiftMap = hmShiftTime.get(strShiftId);	
//							String strShiftFrom = shiftMap.get("FROM");
//							String strShiftTo = shiftMap.get("TO");
//							if(uF.parseToInt(strShiftId) == 1) {
//								strShiftFrom = locationstarttime;
//								strShiftTo = locationendtime;
//							}
//							int nRosterWOff = uF.parseToInt(strRosterOffId);
//						
//							dblTimeDiff=uF.parseToDouble((uF.getTimeDiffInHoursMins(uF.getTimeFormat(strShiftFrom, DBTIME ).getTime(),uF.getTimeFormat(strShiftTo, DBTIME ).getTime())));
//							int updateValue=updateRoster(con, getFromDate(),DATE_FORMAT,j,strShiftFrom,strShiftTo,dblTimeDiff,strShiftId,strEmpId, strEmpServiceId, hmEmpWLocation, hmWeekEndDates, hmHolidays,hmHolidayDates,hmEmpLevelMap,nRosterWOff,uF);
//						
//							if(updateValue == 0) {
//								insertRoster(con, strEmpId, getFromDate(),DATE_FORMAT,j, strShiftFrom,strShiftTo, strEmpServiceId, dblTimeDiff, strShiftId, hmEmpWLocation, hmWeekEndDates, hmHolidays,hmHolidayDates,hmEmpLevelMap,nRosterWOff,uF);
//								alNewRoster.add(strEmpId);
//							}else{
//								alUpdateRoster.add(strEmpId);
//							}
//							if(ArrayUtils.contains(getStrIsAssig(), strEmpId)>=0) {
//								if(nRosterWOff > 1) {
//									insertUpdateRosterWeeklyOff(con,uF,getFromDate(),getToDate(),DATE_FORMAT,j,strEmpId, strEmpServiceId,nRosterWOff,hmRosterWeeklyoff,strShiftId);
//								} else {
//									deleteRosterWeelyOff(con,uF,getFromDate(),DATE_FORMAT,j,strEmpId, strEmpServiceId,nRosterWOff,hmRosterWeeklyoff,strShiftId);
//								}
//							}
//						}z++;
//						
//					} else {  
//						int q=2;
//						for(int k=0;k<shiftSize;k++) {
//							String fromDate	= al.get(i).get(q);
//							String toDate	= al.get(i).get(++q);
//							int newdays=uF.parseToInt(uF.dateDifference(fromDate,CF.getStrReportDateFormat(), toDate, CF.getStrReportDateFormat(),CF.getStrTimeZone()));
//							
//							if(uF.parseToInt(strShiftId) == -1 || uF.parseToInt(strRosterOffId) == -1) {
//								q = q+3;
//								z++;
//								continue;
//							}
//							
//							for(int j=0;j<newdays;j++) {
//								shiftMap = hmShiftTime.get(strShiftId);
//								String strShiftFrom = shiftMap.get("FROM");
//								String strShiftTo = shiftMap.get("TO");
//								if(uF.parseToInt(strShiftId) == 1) {
//									strShiftFrom = locationstarttime;
//									strShiftTo = locationendtime;
//								}
//								int nRosterWOff = uF.parseToInt(strRosterOffId);
//								
//								dblTimeDiff = uF.parseToDouble((uF.getTimeDiffInHoursMins(uF.getTimeFormat(strShiftFrom, DBTIME ).getTime(),uF.getTimeFormat(strShiftTo, DBTIME ).getTime())));
//								int updateValue = updateRoster(con, fromDate,CF.getStrReportDateFormat(),j,strShiftFrom,strShiftTo,dblTimeDiff,strShiftId,strEmpId, strEmpServiceId, hmEmpWLocation, hmWeekEndDates, hmHolidays,hmHolidayDates,hmEmpLevelMap,nRosterWOff,uF);
//								
//								if(updateValue==0) {
//									insertRoster(con, strEmpId, fromDate,CF.getStrReportDateFormat(),j, strShiftFrom,strShiftTo, strEmpServiceId, dblTimeDiff, strShiftId, hmEmpWLocation, hmWeekEndDates, hmHolidays,hmHolidayDates,hmEmpLevelMap,nRosterWOff,uF);
//									alNewRoster.add(strEmpId);
//								} else {
//									alUpdateRoster.add(strEmpId);
//								}
//								if(ArrayUtils.contains(getStrIsAssig(), strEmpId)>=0) {
//									if(nRosterWOff > 1) {
//										insertUpdateRosterWeeklyOff(con,uF,getFromDate(),getToDate(),DATE_FORMAT,j,strEmpId, strEmpServiceId,nRosterWOff,hmRosterWeeklyoff,strShiftId);
//									} else {
//										deleteRosterWeelyOff(con,uF,getFromDate(),DATE_FORMAT,j,strEmpId, strEmpServiceId,nRosterWOff,hmRosterWeeklyoff,strShiftId);
//									}
//								}
//							} q = q+3; z++;
//						}
//					}
//			
//				} else {
//					
//					List<String> wlocationInner = workLocationInfo(uF.parseToInt(strEmpId));
//					if(uF.parseToInt(strShiftId) == -1 || uF.parseToInt(strRosterOffId) == -1) {
//						z++;
//						continue;
//					}
//					for(int j=0; j<nOfdays; j++) {
//						Date date = uF.getDateFormat(getDate(getFromDate(), DATE_FORMAT, j), DATE_FORMAT);
//						String day = uF.getDateFormat(date+"", DBDATE, "EEEE");
//						shiftMap = hmShiftTime.get(strShiftId);
//						
//						if(day!=null && wlocationInner!=null && wlocationInner.size()>=8 && ("HD".equalsIgnoreCase(wlocationInner.get(3)) || "HD".equalsIgnoreCase(wlocationInner.get(5))) && (day.equalsIgnoreCase(wlocationInner.get(2)) || day.equalsIgnoreCase(wlocationInner.get(4)))) { 
//							if(wlocationInner.get(5).equalsIgnoreCase("HD")) {
//								String halfDayStart = null;
//								String halfDayEnd = null;
//								if(wlocationInner.get(6)!=null) {
//									halfDayStart = wlocationInner.get(6);
//									halfDayEnd = wlocationInner.get(7);
//								} else {
//									halfDayStart = wlocationInner.get(0);
//									halfDayEnd = wlocationInner.get(1);
//								}
//								
//								int nRosterWOff = uF.parseToInt(strRosterOffId);
//								
//								long fromTime = uF.getTimeFormat(halfDayStart, TIME_FORMAT ).getTime();
//								long toTime = uF.getTimeFormat(halfDayEnd, TIME_FORMAT ).getTime();
//								
//								dblTimeDiff = uF.parseToDouble((uF.getTimeDiffInHoursMins(fromTime,toTime)));
//								
//								int updateValue = updateRoster(con, getFromDate(),DATE_FORMAT,j,halfDayStart,halfDayEnd,dblTimeDiff,strShiftId,strEmpId, strEmpServiceId, hmEmpWLocation, hmWeekEndDates, hmHolidays,hmHolidayDates,hmEmpLevelMap,nRosterWOff,uF);
//								if(updateValue==0) {
//									insertRoster(con, strEmpId, getFromDate(),DATE_FORMAT,j, halfDayStart,halfDayEnd, strEmpServiceId, dblTimeDiff, strShiftId, hmEmpWLocation, hmWeekEndDates, hmHolidays,hmHolidayDates,hmEmpLevelMap,nRosterWOff,uF);
//								}
//								if(ArrayUtils.contains(getStrIsAssig(), strEmpId)>=0) {
//									if(nRosterWOff > 1) {
//										insertUpdateRosterWeeklyOff(con,uF,getFromDate(),getToDate(),DATE_FORMAT,j,strEmpId, strEmpServiceId,nRosterWOff,hmRosterWeeklyoff,strShiftId);
//									} else {
//										deleteRosterWeelyOff(con,uF,getFromDate(),DATE_FORMAT,j,strEmpId, strEmpServiceId,nRosterWOff,hmRosterWeeklyoff,strShiftId);
//									}
//								}
//							} else if(wlocationInner.get(3).equalsIgnoreCase("HD")) {
//								String halfDayStart = null;
//								String halfDayEnd = null;
//								if(wlocationInner.get(6)!=null) {
//									halfDayStart = wlocationInner.get(6);
//									halfDayEnd = wlocationInner.get(7);
//								} else {
//									halfDayStart = wlocationInner.get(0);
//									halfDayEnd = wlocationInner.get(1);
//								}
//								
//								int nRosterWOff = uF.parseToInt(strRosterOffId);
//								
//								long fromTime = uF.getTimeFormat(halfDayStart, TIME_FORMAT ).getTime();
//								long toTime = uF.getTimeFormat(halfDayEnd, TIME_FORMAT ).getTime();
//								
//								dblTimeDiff = uF.parseToDouble((uF.getTimeDiffInHoursMins(fromTime,toTime)));
//								
//								int updateValue = updateRoster(con, getFromDate(),DATE_FORMAT,j,halfDayStart,halfDayEnd,dblTimeDiff,strShiftId,strEmpId, strEmpServiceId, hmEmpWLocation, hmWeekEndDates, hmHolidays,hmHolidayDates,hmEmpLevelMap,nRosterWOff,uF);
//								if(updateValue == 0) {
//									insertRoster(con, strEmpId, getFromDate(),DATE_FORMAT,j, halfDayStart,halfDayEnd, strEmpServiceId, dblTimeDiff, strShiftId, hmEmpWLocation, hmWeekEndDates, hmHolidays,hmHolidayDates,hmEmpLevelMap,nRosterWOff,uF);
//								}
//								if(ArrayUtils.contains(getStrIsAssig(), strEmpId)>=0) {
//									if(nRosterWOff > 1) {
//										insertUpdateRosterWeeklyOff(con,uF,getFromDate(),getToDate(),DATE_FORMAT,j,strEmpId, strEmpServiceId,nRosterWOff,hmRosterWeeklyoff,strShiftId);
//									} else {
//										deleteRosterWeelyOff(con,uF,getFromDate(),DATE_FORMAT,j,strEmpId, strEmpServiceId,nRosterWOff,hmRosterWeeklyoff,strShiftId);
//									}
//								}
//							}
//							else{
//								/* For full day*/
//								int nRosterWOff = uF.parseToInt(strRosterOffId);
//								
//								dblTimeDiff = uF.parseToDouble((uF.getTimeDiffInHoursMins(uF.getTimeFormat(wlocationInner.get(0), DBTIME ).getTime(),uF.getTimeFormat(wlocationInner.get(1), DBTIME ).getTime())));
//								int updateValue = updateRoster(con, getFromDate(),DATE_FORMAT,j,wlocationInner.get(0),wlocationInner.get(1),dblTimeDiff,strShiftId,strEmpId, strEmpServiceId, hmEmpWLocation, hmWeekEndDates, hmHolidays,hmHolidayDates,hmEmpLevelMap,nRosterWOff,uF);
//								if(updateValue==0) {
//									insertRoster(con, strEmpId, getFromDate(),DATE_FORMAT,j, wlocationInner.get(0),wlocationInner.get(1), strEmpServiceId, dblTimeDiff, strShiftId, hmEmpWLocation, hmWeekEndDates, hmHolidays,hmHolidayDates,hmEmpLevelMap,nRosterWOff,uF);
//								}
//								if(ArrayUtils.contains(getStrIsAssig(), strEmpId)>=0) {
//									if(nRosterWOff > 1) {
//										insertUpdateRosterWeeklyOff(con,uF,getFromDate(),getToDate(),DATE_FORMAT,j,strEmpId, strEmpServiceId,nRosterWOff,hmRosterWeeklyoff,strShiftId);
//									} else {
//										deleteRosterWeelyOff(con,uF,getFromDate(),DATE_FORMAT,j,strEmpId, strEmpServiceId,nRosterWOff,hmRosterWeeklyoff,strShiftId);
//									}
//								}
//							}
//							
//						}
//						else if(wlocationInner!=null && wlocationInner.size()>1) {
//							/* For full day*/
//							int nRosterWOff = uF.parseToInt(strRosterOffId);
//							dblTimeDiff=uF.parseToDouble((uF.getTimeDiffInHoursMins(uF.getTimeFormat(wlocationInner.get(0), DBTIME ).getTime(),uF.getTimeFormat(wlocationInner.get(1), DBTIME ).getTime())));
//							int updateValue=updateRoster(con, getFromDate(),DATE_FORMAT,j,wlocationInner.get(0),wlocationInner.get(1),dblTimeDiff,strShiftId,strEmpId, strEmpServiceId, hmEmpWLocation, hmWeekEndDates, hmHolidays,hmHolidayDates,hmEmpLevelMap,nRosterWOff,uF);
//							if(updateValue==0) {
//								insertRoster(con, strEmpId, getFromDate(),DATE_FORMAT,j, wlocationInner.get(0),wlocationInner.get(1), strEmpServiceId, dblTimeDiff, strShiftId, hmEmpWLocation, hmWeekEndDates, hmHolidays,hmHolidayDates,hmEmpLevelMap,nRosterWOff,uF);
//							}
//							
//							if(ArrayUtils.contains(getStrIsAssig(), strEmpId)>=0) {
//								if(nRosterWOff > 1) {
//									insertUpdateRosterWeeklyOff(con,uF,getFromDate(),getToDate(),DATE_FORMAT,j,strEmpId, strEmpServiceId,nRosterWOff,hmRosterWeeklyoff,strShiftId);
//								} else {
//									deleteRosterWeelyOff(con,uF,getFromDate(),DATE_FORMAT,j,strEmpId, strEmpServiceId,nRosterWOff,hmRosterWeeklyoff,strShiftId);
//								}
//							}
//						}
//					}z++; /*After complision of no. of days z will increment */
//				}
//			}
//			session.setAttribute(MESSAGE, SUCCESSM+"Roster summary<br>"+sbRosterSummary.toString()+END);
		
		}
		catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeStatements(pst1);
			db.closeStatements(pst3);
			db.closeConnection(con);
		}
		return SUCCESS;
	}


	private Map<String, Map<String, String>> getAllLiveEmployeeLastShiftDetails(Connection con, UtilityFunctions uF) {
		PreparedStatement pst=null;
		ResultSet rs= null;
		
		Map<String, Map<String, String>> hmEmpLastShiftData = new HashMap<String, Map<String, String>>();
		try {
//			String insertRoster="insert into roster_details (emp_id, _date, _from, _to, isapproved, user_id, service_id, actual_hours, attended,is_lunch_ded,shift_id,entry_date,roster_weeklyoff_id) values(?,?,?,?,?,(select user_id from user_details where emp_id=?),?,?,?,?,?,?,?)";
			
			pst = con.prepareStatement("select max(_date) as maxDate, emp_id from roster_details where _date<=? group by emp_id");
			pst.setDate(1, uF.getDateFormat(getFromDate(), DATE_FORMAT));
			rs = pst.executeQuery();
			while(rs.next()) {
				Map<String, String> hmInner = hmEmpLastShiftData.get(rs.getString("emp_id"));
				if(hmInner == null) hmInner = new HashMap<String, String>();
				hmInner.put("MAX_DATE", uF.getDateFormat(rs.getString("maxDate"), DBDATE, DATE_FORMAT));
				hmEmpLastShiftData.put(rs.getString("emp_id"), hmInner);
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select min(_date) as minDate, emp_id from roster_details where _date>=? group by emp_id");
			pst.setDate(1, uF.getDateFormat(getToDate(), DATE_FORMAT));
			rs = pst.executeQuery();
			while(rs.next()) {
				Map<String, String> hmInner = hmEmpLastShiftData.get(rs.getString("emp_id"));
				if(hmInner == null) hmInner = new HashMap<String, String>();
				hmInner.put("MIN_DATE", uF.getDateFormat(rs.getString("minDate"), DBDATE, DATE_FORMAT));
				hmEmpLastShiftData.put(rs.getString("emp_id"), hmInner);
			}
			rs.close();
			pst.close();
			
			String monthMinMaxDate = uF.getCurrentMonthMinMaxDate(getFromDate(), DATE_FORMAT);
			String[] mnthMinMaxDate = monthMinMaxDate.split("::::");
			pst = con.prepareStatement("select count(distinct shift_id) as shiftCount, emp_id from roster_details where _date between ? and ? group by emp_id");
			pst.setDate(1, uF.getDateFormat(mnthMinMaxDate[0], DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(mnthMinMaxDate[1], DATE_FORMAT));
			rs = pst.executeQuery();
			while(rs.next()) {
				Map<String, String> hmInner = hmEmpLastShiftData.get(rs.getString("emp_id"));
				if(hmInner == null) hmInner = new HashMap<String, String>();
				hmInner.put("SHIFT_COUNT", rs.getString("shiftCount"));
				hmEmpLastShiftData.put(rs.getString("emp_id"), hmInner);
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select shift_id, emp_id from roster_details where _date between ? and ? group by emp_id,shift_id order by emp_id");
			pst.setDate(1, uF.getDateFormat(mnthMinMaxDate[0], DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(mnthMinMaxDate[1], DATE_FORMAT));
			rs = pst.executeQuery();
			while(rs.next()) {
				Map<String, String> hmInner = hmEmpLastShiftData.get(rs.getString("emp_id"));
				if(hmInner == null) hmInner = new HashMap<String, String>();
				StringBuilder sbShiftIds = null;
				String strShiftIds = hmInner.get("SHIFT_IDS");
				if(strShiftIds == null) {
					sbShiftIds = new StringBuilder();
					sbShiftIds.append(rs.getString("shift_id"));
				} else {
					if(sbShiftIds == null) {
						sbShiftIds = new StringBuilder();
						sbShiftIds.append(strShiftIds+","+rs.getString("shift_id"));
					}
				}
				hmInner.put("SHIFT_IDS", sbShiftIds.toString());
				hmEmpLastShiftData.put(rs.getString("emp_id"), hmInner);
			}
			rs.close();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
	//				
		}
		return hmEmpLastShiftData;
		
	}


	private void getRosterPolicyRules() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs=null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF=new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmRosterPolicyRules = new HashMap<String, String>();
			pst = con.prepareStatement("select * from roster_policy_rules where org_id=?");
			pst.setInt(1, uF.parseToInt(getStrOrg()));
			rs = pst.executeQuery();
			while (rs.next()) {
				hmRosterPolicyRules.put(rs.getString("roster_policy_rule_id"), rs.getString("roster_policy_rule_name"));
			}
			rs.close();
			pst.close();
			request.setAttribute("hmRosterPolicyRules", hmRosterPolicyRules);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}


	private void getSalaryPaidEmployee(UtilityFunctions uF, List<FillEmployee> empList) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			List<String> alEmp = new ArrayList<String>();
			for(int i=0;i<empList.size();i++) {
				String Empid = empList.get(i).getEmployeeId();
				alEmp.add(Empid);
			}
			if(alEmp.size() > 0) {
				String strEmpIds = StringUtils.join(alEmp.toArray(),",");
				if(strEmpIds!=null && !strEmpIds.trim().equals("") && !strEmpIds.trim().equalsIgnoreCase("NULL")) {
					StringBuilder sbQuery = new StringBuilder();
					sbQuery.append("select emp_id from payroll_generation where emp_id>0 and emp_id in ("+strEmpIds+") and ((? between paid_from and paid_to) " +
						"or (? between paid_from and paid_to)) group by emp_id order by emp_id");
					pst = con.prepareStatement(sbQuery.toString());
					pst.setDate(1, uF.getDateFormat(getFromDate(), DATE_FORMAT));
					pst.setDate(2, uF.getDateFormat(getToDate(), DATE_FORMAT));
//					System.out.println("pst==>"+pst);
					rs = pst.executeQuery();
					List<String> alSalPaidEmpList = new ArrayList<String>();
					while (rs.next()) {
						alSalPaidEmpList.add(rs.getString("emp_id"));
					}
					rs.close();
					pst.close();
					request.setAttribute("alSalPaidEmpList", alSalPaidEmpList);
//					System.out.println("alSalPaidEmpList==>"+alSalPaidEmpList);
					
					sbQuery = new StringBuilder();
					sbQuery.append("select * from approve_attendance where emp_id in ("+strEmpIds+")and ((? between approve_from and approve_to) " +
						"or (? between approve_from and approve_to))");
					pst = con.prepareStatement(sbQuery.toString());
					pst.setDate(1, uF.getDateFormat(getFromDate(), DATE_FORMAT));
					pst.setDate(2, uF.getDateFormat(getToDate(), DATE_FORMAT));
//					System.out.println("pst==>"+pst);
					rs = pst.executeQuery();
					List<String> alApproveClockEntrieEmp = new ArrayList<String>();
					while(rs.next()) {
						alApproveClockEntrieEmp.add(rs.getString("emp_id"));
					}
					rs.close();
					pst.close();
					request.setAttribute("alApproveClockEntrieEmp", alApproveClockEntrieEmp);
//					System.out.println("alApproveClockEntrieEmp==>"+alApproveClockEntrieEmp);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	
	private List<FillEmployee> getEmployeeList() {
		List<FillEmployee> al = new ArrayList<FillEmployee>();
		StringBuilder sbQuery=new StringBuilder();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rsEmpName = null,rs=null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF=new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			String strLocation=null;
			for(int i = 0; getF_strWLocation() != null && i < getF_strWLocation().length; i++) {
				if(getF_strWLocation()[i]!=null && !getF_strWLocation()[i].equals("")) {
					if(strLocation == null) {
						strLocation = getF_strWLocation()[i].trim();
					}else{
						strLocation += ","+getF_strWLocation()[i].trim();
					}
				}
			}
			
			String strDepart=null;
			for(int i = 0; getF_department()!=null && i < getF_department().length;i++) {
				if(getF_department()[i]!=null && !getF_department()[i].equals("")) {
					if(strDepart == null) {
						strDepart = getF_department()[i].trim();
					}else{
						strDepart += ","+getF_department()[i].trim();
					}
				}
			} 
			
			String strLevel=null;
			for(int i = 0; getF_level() != null && i < getF_level().length;i++) {
				if(getF_level()[i] != null && !getF_level()[i].equals("")) {
					if(strLevel == null) {
						strLevel = getF_level()[i].trim();
					} else {
						strLevel += ","+getF_level()[i].trim();
					}
				}
			}
			
			String stManager = null;
			for(int i = 0; getStrManager() != null && i < getStrManager().length; i++) {
				if(getStrManager()[i]!=null && !getStrManager()[i].equals("")) {
					if(stManager == null) {
						stManager = getStrManager()[i].trim();
					}else{
						stManager += ","+getStrManager()[i].trim();
					}
				}
			}
			
			String stCostCenter = null;
			for(int i = 0; getCostCenterName() != null && i < getCostCenterName().length; i++) {
				if(getCostCenterName()[i]!=null && !getCostCenterName()[i].equals("")) {
					if(stCostCenter == null) {
						stCostCenter = getCostCenterName()[i].trim();
					}else{
						stCostCenter += ","+getCostCenterName()[i].trim();
					}
				}
			}
			List<String> costCenterList = null;
			if(stCostCenter != null) {
				costCenterList = Arrays.asList(stCostCenter.split(","));
			}
			
			int nCostCenterSize = costCenterList != null ? costCenterList.size() : 0;
			
			sbQuery.append("select * from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id and emp_per_id > 0 and epd.is_alive=true "
					+"and  epd.joining_date <= ? and (employment_end_date is null or (employment_end_date >= ? or employment_end_date between ? and ?))");
			sbQuery.append(" and eod.is_roster="+uF.parseToBoolean(getIsRosterDependant()));
			
			if(uF.parseToInt(getStrOrg())>0) {
				sbQuery.append(" and org_id = "+uF.parseToInt(getStrOrg()));
			}else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && session.getAttribute(ORG_ACCESS)!=null) {
				sbQuery.append(" and org_id in ("+session.getAttribute(ORG_ACCESS)+")");
			}
//			if(uF.parseToInt(getCostCenterName())>0) {
//            	sbQuery.append(" and service_id like '%," + getCostCenterName() + ",%' ");
//          }
			if(nCostCenterSize > 0) {
                sbQuery.append(" and (");
                for(int i = 0; i < nCostCenterSize; i++) {
                    sbQuery.append(" service_id like '%,"+costCenterList.get(i).trim()+",%'");
                    
                    if(i < nCostCenterSize-1) {
                        sbQuery.append(" OR "); 
                    }
                }
                sbQuery.append(" ) ");
            }
			
			if(getStrGender()!=null && !getStrGender().trim().equals("") && !getStrGender().trim().equalsIgnoreCase("NULL")) {
				sbQuery.append(" and epd.emp_gender='"+getStrGender()+"' ");
			}
			
			if(strUserType!=null && strUserType.equalsIgnoreCase(MANAGER)) {
				sbQuery.append(" and eod.supervisor_emp_id = "+uF.parseToInt(strSesionEmpId));
			} else {
				if(strLocation!=null && !strLocation.trim().equals("") && !strLocation.trim().equalsIgnoreCase("NULL")) {
					sbQuery.append(" and wlocation_id in ("+strLocation+")");
	            } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && session.getAttribute(WLOCATION_ACCESS)!=null) {
	            	sbQuery.append(" and wlocation_id in ("+session.getAttribute(WLOCATION_ACCESS)+")");
				}
				
				if(strDepart!=null && !strDepart.equals("") && !strDepart.equalsIgnoreCase("NULL")) {
					sbQuery.append(" and depart_id in ("+strDepart+") ");
		        }
				
				if(strLevel!=null && !strLevel.equals("") && !strLevel.equalsIgnoreCase("NULL")) {
	                sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+strLevel+") ) ");
	            }
				if(stManager != null && !stManager.equals("") && !stManager.equalsIgnoreCase("NULL")) {
					sbQuery.append(" and eod.supervisor_emp_id in("+stManager+") ");
				}
			}
			sbQuery.append(" order by emp_fname, emp_lname");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(getToDate(), DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(getToDate(), DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(getFromDate(), DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(getToDate(), DATE_FORMAT));
//			System.out.println("pst=====>" + pst); 
			rsEmpName = pst.executeQuery();
			StringBuilder sbEmpId = null; 
			while (rsEmpName.next()) {
				if (rsEmpName.getInt("emp_per_id") < 0) {
					continue;
				}
				boolean serviceFlag = false;
				if(rsEmpName.getString("service_id") != null && !rsEmpName.getString("service_id").trim().equals("") 
						&& !rsEmpName.getString("service_id").trim().equalsIgnoreCase("NULL")) {
					List<String> sbuIdList = Arrays.asList(rsEmpName.getString("service_id").split(","));
					int nSbuIdListSize = sbuIdList != null ? sbuIdList.size() : 0; 
					for(int i=0; i < nSbuIdListSize; i++) {
						if(uF.parseToInt(sbuIdList.get(1).trim()) > 0) {
							serviceFlag = true;
							break;
						}
					}
				}
				if(!serviceFlag) {
					continue;
				}
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rsEmpName.getString("emp_mname") != null && rsEmpName.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rsEmpName.getString("emp_mname");
					}
				}
				
				al.add(new FillEmployee(rsEmpName.getString("emp_per_id"), rsEmpName.getString("emp_fname") +strEmpMName+ " " + rsEmpName.getString("emp_lname"), rsEmpName.getString("is_roster")));
				
				if(sbEmpId == null) {
					sbEmpId = new StringBuilder(); 
					sbEmpId.append(rsEmpName.getString("emp_per_id"));
				} else {
					sbEmpId.append(","+rsEmpName.getString("emp_per_id"));
				}
			}
			rsEmpName.close();
			pst.close();
			
			if(sbEmpId !=null) {
				Map<String, String> hmEmpDepartment = new HashMap<String, String>();
				pst = con.prepareStatement("select emp_id,dept_name from (SELECT emp_id,depart_id FROM employee_official_details eod, employee_personal_details epd " +
						"WHERE epd.emp_per_id=eod.emp_id and eod.emp_id in ("+sbEmpId.toString()+") ) a, department_info di where a.depart_id = di.dept_id order by emp_id");
				rs = pst.executeQuery();
				while (rs.next()) {
					hmEmpDepartment.put(rs.getString("emp_id"), rs.getString("dept_name"));
				}
				rs.close();
				pst.close();
				request.setAttribute("hmEmpDepartment", hmEmpDepartment);
				
				Map<String, String> hmEmpCodeDesig = new HashMap<String, String>();
				pst = con.prepareStatement("select * from grades_details gd, designation_details dd, level_details ld, employee_official_details eod " +
						"where dd.designation_id = gd.designation_id and ld.level_id = dd.level_id and gd.grade_id = eod.grade_id and eod.emp_id in ("+sbEmpId.toString()+") ");
				rs = pst.executeQuery();
				while (rs.next()) {
					hmEmpCodeDesig.put(rs.getString("emp_id"), rs.getString("designation_name"));
				}
				rs.close();
				pst.close();
				request.setAttribute("hmEmpCodeDesig", hmEmpCodeDesig);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeResultSet(rsEmpName);			
			db.closeStatements(pst);
			db.closeConnection(con);
		}

		return al;
	}

	List<String> alNewRoster = new ArrayList<String>();
	List<String> alUpdateRoster = new ArrayList<String>();
	
	public String insertShiftRoster(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst1=null;
		PreparedStatement pst=null;
		PreparedStatement pst3=null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request); 
		double dblTimeDiff=0;
			
		try {
			
			con = db.makeConnection(con);
			
			List<String> alAssignEmp = getStrIsAssig() != null ? Arrays.asList(getStrIsAssig()) : new ArrayList<String>();
			
			Map<String, Map<String, String>> hmShiftTime = CF.getShiftTime(con);
			
			Map hmEmpWLocation = CF.getEmpWlocationMap(con);
			hmEmpName = CF.getEmpNameMap(con,null, null);
			hmShiftMap = CF.getShiftMap(con);
			 
			Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
			
			Map<String, Set<String>> hmWeekEndDates = CF.getWeekEndDateList(con, getFromDate(), getToDate(), CF, uF,null,null);
			Map<String,String> hmHolidays = new HashMap<String,String>();
			Map<String,String> hmHolidayDates = new HashMap<String,String>();
			CF.getHolidayList(con,request,getFromDate(), getToDate(), CF, hmHolidayDates, hmHolidays, true);
			
			Map<String, Map<String, String>> hmRosterWeeklyoff = CF.getRosterWeeklyOffDetails(con,CF,uF);
			if(hmRosterWeeklyoff == null) hmRosterWeeklyoff = new HashMap<String, Map<String,String>>();
			
			int nOfdays=uF.parseToInt(uF.dateDifference(getFromDate(),DATE_FORMAT, getToDate(), DATE_FORMAT,CF.getStrTimeZone()));
//			System.out.println("nOfdays ========="+nOfdays);
			int shiftSize=0;
			int z=0;
			Map<String, String> shiftMap ;
			int nEmpSize = empList!=null ? empList.size() : 0;
			for(int i = 0; i < nEmpSize;i++) {
				String strEmpId = empList.get(i).getEmployeeId();
//				if(!alAssignEm njp.contains(strEmpId)) {
//					continue;
//				}
				String strEmpServiceId = CF.getEmpServiceId(con, uF, uF.parseToInt(strEmpId));
				
				String userlocation = CF.getEmpWlocationId(con, uF, strEmpId);
				pst = con.prepareStatement("select wlocation_id,wlocation_start_time,wlocation_end_time from work_location_info where wlocation_id =?");
				pst.setInt(1, uF.parseToInt(userlocation));
				rs = pst.executeQuery();
				String locationstarttime = null;
				String locationendtime = null;
				while (rs.next()) {
					locationstarttime = rs.getString("wlocation_start_time");
					locationendtime = rs.getString("wlocation_end_time");
				}
				rs.close();
				pst.close();
				
				String strShiftId = getInnerShiftName().get(z);
				String strRosterOffId = getStrRosterWOff().get(z);
				
//				System.out.println("1 strEmpId==>"+strEmpId+"--strShiftId==>"+strShiftId+"--strRosterOffId==>"+strRosterOffId);
				if(getStrUpdate() != null && !getStrUpdate().trim().equals("") && !getStrUpdate().trim().equalsIgnoreCase("NULL")) {
//					System.out.println("if strEmpId==>"+strEmpId+"--strShiftId==>"+strShiftId+"--strRosterOffId==>"+strRosterOffId);
					strShiftId = getShiftName();
					strRosterOffId = getF_RosterOff();
//					System.out.println("after strEmpId==>"+strEmpId+"--strShiftId==>"+strShiftId+"--strRosterOffId==>"+strRosterOffId);
				}
//				System.out.println("if 1 strEmpId==>"+strEmpId+"--strShiftId==>"+strShiftId+"--strRosterOffId==>"+strRosterOffId);
				if(getUpdate() != null && !getUpdate().trim().equals("") && !getUpdate().trim().equalsIgnoreCase("NULL") && alAssignEmp.contains(strEmpId)) {
					if(uF.parseToInt(strShiftId) == 0 || uF.parseToInt(strShiftId) ==-1) {
						strShiftId = getShiftName();
					}
//					System.out.println("if 2 strEmpId==>"+strEmpId+"--strShiftId==>"+strShiftId+"--strRosterOffId==>"+strRosterOffId);
					if(uF.parseToInt(strRosterOffId) == 0 || uF.parseToInt(strRosterOffId) == -1) {
						strRosterOffId = getF_RosterOff();
					}
				}
//				System.out.println("if 3strEmpId==>"+strEmpId+"--strShiftId==>"+strShiftId+"--strRosterOffId==>"+strRosterOffId);
				
//				if(uF.parseToInt(strShiftId) == -1 || uF.parseToInt(strRosterOffId) == -1) {
//					continue;
//				}
				
//				if(uF.parseToInt(getInnerShiftName().get(z))!=0) {
				if(uF.parseToInt(strShiftId)!=0) {
//					System.out.println("if strEmpId==>"+strEmpId+"--strShiftId==>"+strShiftId+"--strRosterOffId==>"+strRosterOffId);
					shiftSize= (al.get(i).size()+1)/4;
				
					if(shiftSize == 0 || shiftSize == 1) {
						if(uF.parseToInt(strShiftId) == -1 || uF.parseToInt(strRosterOffId) == -1) {
							z++;
							continue;
						}
						for(int j = 0;j < nOfdays;j++) {
//							String strShiftId = getInnerShiftName().get(z);
//							String strRosterOffId = getStrRosterWOff().get(z);
//							
//							if(getStrUpdate() != null && !getStrUpdate().trim().equals("") && !getStrUpdate().trim().equalsIgnoreCase("NULL")) {
//								strShiftId = getShiftName();
//								strRosterOffId = getF_RosterOff();
//							}
							shiftMap = hmShiftTime.get(strShiftId);	
							String strShiftFrom = shiftMap.get("FROM");
							String strShiftTo = shiftMap.get("TO");
							if(uF.parseToInt(strShiftId) == 1) {
								strShiftFrom = locationstarttime;
								strShiftTo = locationendtime;
							}
							int nRosterWOff = uF.parseToInt(strRosterOffId);
							
//							System.out.println("MR/1239---strShiftFrom="+strShiftFrom+"---strShiftTo="+strShiftTo+"---strShiftId=="+strShiftId);
						
							dblTimeDiff=uF.parseToDouble((uF.getTimeDiffInHoursMins(uF.getTimeFormat(strShiftFrom, DBTIME ).getTime(),uF.getTimeFormat(strShiftTo, DBTIME ).getTime())));
							int updateValue=updateRoster(con, getFromDate(),DATE_FORMAT,j,strShiftFrom,strShiftTo,dblTimeDiff,strShiftId,strEmpId, strEmpServiceId, hmEmpWLocation, hmWeekEndDates, hmHolidays,hmHolidayDates,hmEmpLevelMap,nRosterWOff,uF);
						
//							System.out.println("updateValue"+updateValue+" "+strEmpId+" get Shift Id ="+strShiftId);
						
							if(updateValue == 0) {
								insertRoster(con, strEmpId, getFromDate(),DATE_FORMAT,j, strShiftFrom,strShiftTo, strEmpServiceId, dblTimeDiff, strShiftId, hmEmpWLocation, hmWeekEndDates, hmHolidays,hmHolidayDates,hmEmpLevelMap,nRosterWOff,uF);
								alNewRoster.add(strEmpId);
	//							System.out.println("Emp Id ="+strEmpId);
							}else{
								alUpdateRoster.add(strEmpId);
							}
							if(ArrayUtils.contains(getStrIsAssig(), strEmpId)>=0) {
	//							int nRosterWOff = uF.parseToInt(strRosterOffId);
//								System.out.println("1 nRosterWOff=======>"+nRosterWOff);
								if(nRosterWOff > 1) {
									insertUpdateRosterWeeklyOff(con,uF,getFromDate(),getToDate(),DATE_FORMAT,j,strEmpId, strEmpServiceId,nRosterWOff,hmRosterWeeklyoff,strShiftId);
								} else {
									deleteRosterWeelyOff(con,uF,getFromDate(),DATE_FORMAT,j,strEmpId, strEmpServiceId,nRosterWOff,hmRosterWeeklyoff,strShiftId);
								}
							}
						}z++;
						
					} else {  /* if Shift between two dates is greater than 1*/
						int q=2;
//						System.out.println("shiftSize ========="+shiftSize+"--al==>"+(al.get(i)));
						
						for(int k=0;k<shiftSize;k++) {
							String fromDate	= al.get(i).get(q);
							String toDate	= al.get(i).get(++q);
							int newdays=uF.parseToInt(uF.dateDifference(fromDate,CF.getStrReportDateFormat(), toDate, CF.getStrReportDateFormat(),CF.getStrTimeZone()));
//							System.out.println("fromDate==>"+fromDate+"--toDate==>"+toDate+"--newDayss ========="+newdays);
							
							if(uF.parseToInt(strShiftId) == -1 || uF.parseToInt(strRosterOffId) == -1) {
								q = q+3;
								z++;
								continue;
							}
							
							for(int j=0;j<newdays;j++) {
								shiftMap = hmShiftTime.get(strShiftId);
								String strShiftFrom = shiftMap.get("FROM");
								String strShiftTo = shiftMap.get("TO");
								if(uF.parseToInt(strShiftId) == 1) {
									strShiftFrom = locationstarttime;
									strShiftTo = locationendtime;
								}
								int nRosterWOff = uF.parseToInt(strRosterOffId);
								
								dblTimeDiff = uF.parseToDouble((uF.getTimeDiffInHoursMins(uF.getTimeFormat(strShiftFrom, DBTIME ).getTime(),uF.getTimeFormat(strShiftTo, DBTIME ).getTime())));
	//							System.out.println("fromDate ========="+fromDate);
								int updateValue = updateRoster(con, fromDate,CF.getStrReportDateFormat(),j,strShiftFrom,strShiftTo,dblTimeDiff,strShiftId,strEmpId, strEmpServiceId, hmEmpWLocation, hmWeekEndDates, hmHolidays,hmHolidayDates,hmEmpLevelMap,nRosterWOff,uF);
	//							System.out.println("Emp Id =================="+strEmpId);
	//							System.out.println("Emp Id ========="+fromDate);
	//							System.out.println("updateValue ========="+updateValue);
								
								if(updateValue==0) {
									insertRoster(con, strEmpId, fromDate,CF.getStrReportDateFormat(),j, strShiftFrom,strShiftTo, strEmpServiceId, dblTimeDiff, strShiftId, hmEmpWLocation, hmWeekEndDates, hmHolidays,hmHolidayDates,hmEmpLevelMap,nRosterWOff,uF);
									alNewRoster.add(strEmpId);
								} else {
									alUpdateRoster.add(strEmpId);
								}
								if(ArrayUtils.contains(getStrIsAssig(), strEmpId)>=0) {
	//								int nRosterWOff = uF.parseToInt(strRosterOffId);
	//								System.out.println("2 nRosterWOff=======>"+nRosterWOff);
									if(nRosterWOff > 1) {
										insertUpdateRosterWeeklyOff(con,uF,getFromDate(),getToDate(),DATE_FORMAT,j,strEmpId, strEmpServiceId,nRosterWOff,hmRosterWeeklyoff,strShiftId);
									} else {
										deleteRosterWeelyOff(con,uF,getFromDate(),DATE_FORMAT,j,strEmpId, strEmpServiceId,nRosterWOff,hmRosterWeeklyoff,strShiftId);
									}
								}
							} q = q+3; z++;
						}
					}
			
				} else {
//					System.out.println("else strEmpId==>"+strEmpId+"--strShiftId==>"+strShiftId+"--strRosterOffId==>"+strRosterOffId);
					/*IF Standard Shift is selected*/
					
					List<String> wlocationInner = workLocationInfo(uF.parseToInt(strEmpId));
					if(uF.parseToInt(strShiftId) == -1 || uF.parseToInt(strRosterOffId) == -1) {
						z++;
						continue;
					}
					for(int j=0; j<nOfdays; j++) {
						Date date = uF.getDateFormat(getDate(getFromDate(), DATE_FORMAT, j), DATE_FORMAT);
						String day = uF.getDateFormat(date+"", DBDATE, "EEEE");
						shiftMap = hmShiftTime.get(strShiftId);
						
						if(day!=null && wlocationInner!=null && wlocationInner.size()>=8 && ("HD".equalsIgnoreCase(wlocationInner.get(3)) || "HD".equalsIgnoreCase(wlocationInner.get(5))) && (day.equalsIgnoreCase(wlocationInner.get(2)) || day.equalsIgnoreCase(wlocationInner.get(4)))) { 
							if(wlocationInner.get(5).equalsIgnoreCase("HD")) {
								/*For weekly  half day*/
								String halfDayStart = null;
								String halfDayEnd = null;
								if(wlocationInner.get(6)!=null) {
									halfDayStart = wlocationInner.get(6);
									halfDayEnd = wlocationInner.get(7);
								} else {
									halfDayStart = wlocationInner.get(0);
									halfDayEnd = wlocationInner.get(1);
								}
								
								int nRosterWOff = uF.parseToInt(strRosterOffId);
								
								long fromTime = uF.getTimeFormat(halfDayStart, TIME_FORMAT ).getTime();
								long toTime = uF.getTimeFormat(halfDayEnd, TIME_FORMAT ).getTime();
								
								dblTimeDiff = uF.parseToDouble((uF.getTimeDiffInHoursMins(fromTime,toTime)));
								
								int updateValue = updateRoster(con, getFromDate(),DATE_FORMAT,j,halfDayStart,halfDayEnd,dblTimeDiff,strShiftId,strEmpId, strEmpServiceId, hmEmpWLocation, hmWeekEndDates, hmHolidays,hmHolidayDates,hmEmpLevelMap,nRosterWOff,uF);
								if(updateValue==0) {
	//								System.out.println(" inserting......");
									insertRoster(con, strEmpId, getFromDate(),DATE_FORMAT,j, halfDayStart,halfDayEnd, strEmpServiceId, dblTimeDiff, strShiftId, hmEmpWLocation, hmWeekEndDates, hmHolidays,hmHolidayDates,hmEmpLevelMap,nRosterWOff,uF);
								}
								if(ArrayUtils.contains(getStrIsAssig(), strEmpId)>=0) {
									if(nRosterWOff > 1) {
										insertUpdateRosterWeeklyOff(con,uF,getFromDate(),getToDate(),DATE_FORMAT,j,strEmpId, strEmpServiceId,nRosterWOff,hmRosterWeeklyoff,strShiftId);
									} else {
										deleteRosterWeelyOff(con,uF,getFromDate(),DATE_FORMAT,j,strEmpId, strEmpServiceId,nRosterWOff,hmRosterWeeklyoff,strShiftId);
									}
								}
							} else if(wlocationInner.get(3).equalsIgnoreCase("HD")) {
								/*For weekly  half day*/
								String halfDayStart = null;
								String halfDayEnd = null;
								if(wlocationInner.get(6)!=null) {
									halfDayStart = wlocationInner.get(6);
									halfDayEnd = wlocationInner.get(7);
								} else {
									halfDayStart = wlocationInner.get(0);
									halfDayEnd = wlocationInner.get(1);
								}
								
								int nRosterWOff = uF.parseToInt(strRosterOffId);
								
								long fromTime = uF.getTimeFormat(halfDayStart, TIME_FORMAT ).getTime();
								long toTime = uF.getTimeFormat(halfDayEnd, TIME_FORMAT ).getTime();
								
								dblTimeDiff = uF.parseToDouble((uF.getTimeDiffInHoursMins(fromTime,toTime)));
								
								int updateValue = updateRoster(con, getFromDate(),DATE_FORMAT,j,halfDayStart,halfDayEnd,dblTimeDiff,strShiftId,strEmpId, strEmpServiceId, hmEmpWLocation, hmWeekEndDates, hmHolidays,hmHolidayDates,hmEmpLevelMap,nRosterWOff,uF);
	//							System.out.println("updateValue=="+updateValue +" for empid="+strEmpId);
								if(updateValue == 0) {
	//								System.out.println(" inserting......");
									insertRoster(con, strEmpId, getFromDate(),DATE_FORMAT,j, halfDayStart,halfDayEnd, strEmpServiceId, dblTimeDiff, strShiftId, hmEmpWLocation, hmWeekEndDates, hmHolidays,hmHolidayDates,hmEmpLevelMap,nRosterWOff,uF);
								}
								if(ArrayUtils.contains(getStrIsAssig(), strEmpId)>=0) {
									if(nRosterWOff > 1) {
										insertUpdateRosterWeeklyOff(con,uF,getFromDate(),getToDate(),DATE_FORMAT,j,strEmpId, strEmpServiceId,nRosterWOff,hmRosterWeeklyoff,strShiftId);
									} else {
										deleteRosterWeelyOff(con,uF,getFromDate(),DATE_FORMAT,j,strEmpId, strEmpServiceId,nRosterWOff,hmRosterWeeklyoff,strShiftId);
									}
								}
							}
							else{
								/* For full day*/
								int nRosterWOff = uF.parseToInt(strRosterOffId);
								
								dblTimeDiff = uF.parseToDouble((uF.getTimeDiffInHoursMins(uF.getTimeFormat(wlocationInner.get(0), DBTIME ).getTime(),uF.getTimeFormat(wlocationInner.get(1), DBTIME ).getTime())));
								int updateValue = updateRoster(con, getFromDate(),DATE_FORMAT,j,wlocationInner.get(0),wlocationInner.get(1),dblTimeDiff,strShiftId,strEmpId, strEmpServiceId, hmEmpWLocation, hmWeekEndDates, hmHolidays,hmHolidayDates,hmEmpLevelMap,nRosterWOff,uF);
								if(updateValue==0) {
									insertRoster(con, strEmpId, getFromDate(),DATE_FORMAT,j, wlocationInner.get(0),wlocationInner.get(1), strEmpServiceId, dblTimeDiff, strShiftId, hmEmpWLocation, hmWeekEndDates, hmHolidays,hmHolidayDates,hmEmpLevelMap,nRosterWOff,uF);
								}
								if(ArrayUtils.contains(getStrIsAssig(), strEmpId)>=0) {
									if(nRosterWOff > 1) {
										insertUpdateRosterWeeklyOff(con,uF,getFromDate(),getToDate(),DATE_FORMAT,j,strEmpId, strEmpServiceId,nRosterWOff,hmRosterWeeklyoff,strShiftId);
									} else {
										deleteRosterWeelyOff(con,uF,getFromDate(),DATE_FORMAT,j,strEmpId, strEmpServiceId,nRosterWOff,hmRosterWeeklyoff,strShiftId);
									}
								}
							}
							
						}
						else if(wlocationInner!=null && wlocationInner.size()>1) {
							/* For full day*/
							int nRosterWOff = uF.parseToInt(strRosterOffId);
							dblTimeDiff=uF.parseToDouble((uF.getTimeDiffInHoursMins(uF.getTimeFormat(wlocationInner.get(0), DBTIME ).getTime(),uF.getTimeFormat(wlocationInner.get(1), DBTIME ).getTime())));
							int updateValue=updateRoster(con, getFromDate(),DATE_FORMAT,j,wlocationInner.get(0),wlocationInner.get(1),dblTimeDiff,strShiftId,strEmpId, strEmpServiceId, hmEmpWLocation, hmWeekEndDates, hmHolidays,hmHolidayDates,hmEmpLevelMap,nRosterWOff,uF);
							if(updateValue==0) {
								insertRoster(con, strEmpId, getFromDate(),DATE_FORMAT,j, wlocationInner.get(0),wlocationInner.get(1), strEmpServiceId, dblTimeDiff, strShiftId, hmEmpWLocation, hmWeekEndDates, hmHolidays,hmHolidayDates,hmEmpLevelMap,nRosterWOff,uF);
							}
							
							if(ArrayUtils.contains(getStrIsAssig(), strEmpId)>=0) {
								if(nRosterWOff > 1) {
									insertUpdateRosterWeeklyOff(con,uF,getFromDate(),getToDate(),DATE_FORMAT,j,strEmpId, strEmpServiceId,nRosterWOff,hmRosterWeeklyoff,strShiftId);
								} else {
									deleteRosterWeelyOff(con,uF,getFromDate(),DATE_FORMAT,j,strEmpId, strEmpServiceId,nRosterWOff,hmRosterWeeklyoff,strShiftId);
								}
							}
						}
					}z++; /*After complision of no. of days z will increment */
				}
			}
			session.setAttribute(MESSAGE, SUCCESSM+"Roster summary<br>"+sbRosterSummary.toString()+END);
		
		}
		catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeStatements(pst1);
			db.closeStatements(pst3);
			db.closeConnection(con);
		}
		return SUCCESS;
	}
	
//	public String insertShiftRoster(UtilityFunctions uF) {
//		Connection con = null;
//		PreparedStatement pst1=null;
//		PreparedStatement pst=null;
//		PreparedStatement pst3=null;
//		Database db = new Database();
//		db.setRequest(request);
//		double dblTimeDiff=0;
//			
//		try {
//			
//			con = db.makeConnection(con);
//			
//			Map<String, Map<String, String>> hmShiftTime = CF.getShiftTime(con);
//			
//			Map hmEmpWLocation = CF.getEmpWlocationMap(con);
//			hmEmpName = CF.getEmpNameMap(con,null, null);
//			hmShiftMap = CF.getShiftMap(con);
//			 
//			Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
//			
//			Map<String, Set<String>> hmWeekEndDates = CF.getWeekEndDateList(con, getFromDate(), getToDate(), CF, uF,null,null);
//			Map<String,String> hmHolidays = new HashMap<String,String>();
//			Map<String,String> hmHolidayDates = new HashMap<String,String>();
//			CF.getHolidayList(con,request,getFromDate(), getToDate(), CF, hmHolidayDates, hmHolidays, true);
//			
//			Map<String, Map<String, String>> hmRosterWeeklyoff = CF.getRosterWeeklyOffDetails(con,CF,uF);
//			if(hmRosterWeeklyoff == null) hmRosterWeeklyoff = new HashMap<String, Map<String,String>>();
//			
//			int nOfdays=uF.parseToInt(uF.dateDifference(getFromDate(),DATE_FORMAT, getToDate(), DATE_FORMAT));
//			System.out.println("nOfdays ========="+nOfdays);
//			int shiftSize=0;
//			int z=0;
//			Map<String, String> shiftMap ;
//			int nEmpSize = empList!=null ? empList.size() : 0;
//			for(int i = 0; i < nEmpSize;i++) {
//				String strEmpId = empList.get(i).getEmployeeId();
//				String strEmpServiceId = CF.getEmpServiceId(con, uF, uF.parseToInt(strEmpId));
//				
//				String strShiftId = getInnerShiftName().get(z);
//				String strRosterOffId = getStrRosterWOff().get(z);
//				
//				if(getStrUpdate() != null && !getStrUpdate().trim().equals("") && !getStrUpdate().trim().equalsIgnoreCase("NULL")) {
//					strShiftId = getShiftName();
//					strRosterOffId = getF_RosterOff();
//				}
//				
////				if(uF.parseToInt(strShiftId) == -1 || uF.parseToInt(strRosterOffId) == -1) {
////					continue;
////				}
//				
////				if(uF.parseToInt(getInnerShiftName().get(z))!=0) {
//				if(uF.parseToInt(strShiftId)!=0) {
////					System.out.println("if strEmpId==>"+strEmpId+"--strShiftId==>"+strShiftId+"--strRosterOffId==>"+strRosterOffId);
//					shiftSize= (al.get(i).size()+1)/4;
//				
//					if(shiftSize == 0 || shiftSize == 1) {
//						if(uF.parseToInt(strShiftId) == -1 || uF.parseToInt(strRosterOffId) == -1) {
//							z++;
//							continue;
//						}
//						for(int j = 0;j < nOfdays;j++) {
////							String strShiftId = getInnerShiftName().get(z);
////							String strRosterOffId = getStrRosterWOff().get(z);
////							
////							if(getStrUpdate() != null && !getStrUpdate().trim().equals("") && !getStrUpdate().trim().equalsIgnoreCase("NULL")) {
////								strShiftId = getShiftName();
////								strRosterOffId = getF_RosterOff();
////							}
//							shiftMap = hmShiftTime.get(strShiftId);						
//							int nRosterWOff = uF.parseToInt(strRosterOffId);
//						
//							dblTimeDiff=uF.parseToDouble((uF.getTimeDiffInHoursMins(uF.getTimeFormat(shiftMap.get("FROM"), DBTIME ).getTime(),uF.getTimeFormat(shiftMap.get("TO"), DBTIME ).getTime())));
//							int updateValue=updateRoster(con, getFromDate(),DATE_FORMAT,j,shiftMap.get("FROM"),shiftMap.get("TO"),dblTimeDiff,strShiftId,strEmpId, strEmpServiceId, hmEmpWLocation, hmWeekEndDates, hmHolidays,hmHolidayDates,hmEmpLevelMap,nRosterWOff,uF);
//						
////							System.out.println("updateValue"+updateValue+" "+strEmpId+" get Shift Id ="+strShiftId);
//						
//							if(updateValue == 0) {
//								insertRoster(con, strEmpId, getFromDate(),DATE_FORMAT,j, shiftMap.get("FROM"),shiftMap.get("TO"), strEmpServiceId, dblTimeDiff, strShiftId, hmEmpWLocation, hmWeekEndDates, hmHolidays,hmHolidayDates,hmEmpLevelMap,nRosterWOff,uF);
//								alNewRoster.add(strEmpId);
//	//							System.out.println("Emp Id ="+strEmpId);
//							}else{
//								alUpdateRoster.add(strEmpId);
//							}
//							if(ArrayUtils.contains(getStrIsAssig(), strEmpId)>=0) {
//	//							int nRosterWOff = uF.parseToInt(strRosterOffId);
////								System.out.println("1 nRosterWOff=======>"+nRosterWOff);
//								if(nRosterWOff > 1) {
//									insertUpdateRosterWeeklyOff(con,uF,getFromDate(),getToDate(),DATE_FORMAT,j,strEmpId, strEmpServiceId,nRosterWOff,hmRosterWeeklyoff,strShiftId);
//								} else {
//									deleteRosterWeelyOff(con,uF,getFromDate(),DATE_FORMAT,j,strEmpId, strEmpServiceId,nRosterWOff,hmRosterWeeklyoff,strShiftId);
//								}
//							}
//						}z++;
//						
//					} else {  /* if Shift between two dates is greater than 1*/
//						int q=2;
//						System.out.println("shiftSize ========="+shiftSize+"--al==>"+(al.get(i)));
//						
//						for(int k=0;k<shiftSize;k++) {
//							String fromDate	= al.get(i).get(q);
//							String toDate	= al.get(i).get(++q);
//							int newdays=uF.parseToInt(uF.dateDifference(fromDate,CF.getStrReportDateFormat(), toDate, CF.getStrReportDateFormat()));
//							
//							System.out.println("fromDate==>"+fromDate+"--toDate==>"+toDate+"--newDayss ========="+newdays);
//							
//							if(uF.parseToInt(strShiftId) == -1 || uF.parseToInt(strRosterOffId) == -1) {
//								q=q+3;
//								z++;
//								continue;
//							}
//							
//							for(int j=0;j<newdays;j++) {
//								
//								shiftMap = hmShiftTime.get(strShiftId);
//								
//								int nRosterWOff = uF.parseToInt(strRosterOffId);
//								
//								dblTimeDiff=uF.parseToDouble((uF.getTimeDiffInHoursMins(uF.getTimeFormat(shiftMap.get("FROM"), DBTIME ).getTime(),uF.getTimeFormat(shiftMap.get("TO"), DBTIME ).getTime())));
//	//							System.out.println("fromDate ========="+fromDate);
//								int updateValue=updateRoster(con, fromDate,CF.getStrReportDateFormat(),j,shiftMap.get("FROM"),shiftMap.get("TO"),dblTimeDiff,strShiftId,strEmpId, strEmpServiceId, hmEmpWLocation, hmWeekEndDates, hmHolidays,hmHolidayDates,hmEmpLevelMap,nRosterWOff,uF);
//	//							System.out.println("Emp Id =================="+strEmpId);
//	//							System.out.println("Emp Id ========="+fromDate);
//	//							System.out.println("updateValue ========="+updateValue);
//								
//								if(updateValue==0) {
//									insertRoster(con, strEmpId, fromDate,CF.getStrReportDateFormat(),j, shiftMap.get("FROM"),shiftMap.get("TO"), strEmpServiceId, dblTimeDiff, strShiftId, hmEmpWLocation, hmWeekEndDates, hmHolidays,hmHolidayDates,hmEmpLevelMap,nRosterWOff,uF);
//									alNewRoster.add(strEmpId);
//								}else{
//									alUpdateRoster.add(strEmpId);
//								}
//								if(ArrayUtils.contains(getStrIsAssig(), strEmpId)>=0) {
//	//								int nRosterWOff = uF.parseToInt(strRosterOffId);
//	//								System.out.println("2 nRosterWOff=======>"+nRosterWOff);
//									if(nRosterWOff > 1) {
//										insertUpdateRosterWeeklyOff(con,uF,getFromDate(),getToDate(),DATE_FORMAT,j,strEmpId, strEmpServiceId,nRosterWOff,hmRosterWeeklyoff,strShiftId);
//									} else {
//										deleteRosterWeelyOff(con,uF,getFromDate(),DATE_FORMAT,j,strEmpId, strEmpServiceId,nRosterWOff,hmRosterWeeklyoff,strShiftId);
//									}
//								}
//							
//							}q=q+3;z++;
//						}
//					}
//			
//				} else {
////					System.out.println("else strEmpId==>"+strEmpId+"--strShiftId==>"+strShiftId+"--strRosterOffId==>"+strRosterOffId);
//					/*IF Standard Shift is selected*/
//					List<String> wlocationInner = workLocationInfo(uF.parseToInt(strEmpId));
//					
//					if(uF.parseToInt(strShiftId) == -1 || uF.parseToInt(strRosterOffId) == -1) {
//						z++;
//						continue;
//					}
//					for(int j=0;j<nOfdays;j++) {
//						Date date=uF.getDateFormat(getDate(getFromDate(),DATE_FORMAT,j), DATE_FORMAT);
//						
//						String day=uF.getDateFormat(date+"", DBDATE, "EEEE");
//						shiftMap = hmShiftTime.get(strShiftId);
//						
//						if(day!=null && wlocationInner!=null && wlocationInner.size()>=8 && ("HD".equalsIgnoreCase(wlocationInner.get(3)) || "HD".equalsIgnoreCase(wlocationInner.get(5))) && (day.equalsIgnoreCase(wlocationInner.get(2)) || day.equalsIgnoreCase(wlocationInner.get(4)))) { 
//							if(wlocationInner.get(5).equalsIgnoreCase("HD")) {
//								/*For weekly  half day*/
//								String halfDayStart=null;
//								String halfDayEnd=null;
//								if(wlocationInner.get(6)!=null) {
//									halfDayStart=wlocationInner.get(6);
//									halfDayEnd=wlocationInner.get(7);
//								}
//								else{
//									halfDayStart=wlocationInner.get(0);
//									halfDayEnd=wlocationInner.get(1);
//								}
//								
//								int nRosterWOff = uF.parseToInt(strRosterOffId);
//								
//								long fromTime=uF.getTimeFormat(halfDayStart, TIME_FORMAT ).getTime();
//								long toTime=uF.getTimeFormat(halfDayEnd, TIME_FORMAT ).getTime();
//								
//								dblTimeDiff=uF.parseToDouble((uF.getTimeDiffInHoursMins(fromTime,toTime)));
//								
//								int updateValue=updateRoster(con, getFromDate(),DATE_FORMAT,j,halfDayStart,halfDayEnd,dblTimeDiff,strShiftId,strEmpId, strEmpServiceId, hmEmpWLocation, hmWeekEndDates, hmHolidays,hmHolidayDates,hmEmpLevelMap,nRosterWOff,uF);
//								if(updateValue==0) {
//	//								System.out.println(" inserting......");
//									insertRoster(con, strEmpId, getFromDate(),DATE_FORMAT,j, halfDayStart,halfDayEnd, strEmpServiceId, dblTimeDiff, strShiftId, hmEmpWLocation, hmWeekEndDates, hmHolidays,hmHolidayDates,hmEmpLevelMap,nRosterWOff,uF);
//								}
//								if(ArrayUtils.contains(getStrIsAssig(), strEmpId)>=0) {
//									if(nRosterWOff > 1) {
//										insertUpdateRosterWeeklyOff(con,uF,getFromDate(),getToDate(),DATE_FORMAT,j,strEmpId, strEmpServiceId,nRosterWOff,hmRosterWeeklyoff,strShiftId);
//									} else {
//										deleteRosterWeelyOff(con,uF,getFromDate(),DATE_FORMAT,j,strEmpId, strEmpServiceId,nRosterWOff,hmRosterWeeklyoff,strShiftId);
//									}
//								}
//							}else if(wlocationInner.get(3).equalsIgnoreCase("HD")) {
//								/*For weekly  half day*/
//								String halfDayStart=null;
//								String halfDayEnd=null;
//								if(wlocationInner.get(6)!=null) {
//									halfDayStart=wlocationInner.get(6);
//									halfDayEnd=wlocationInner.get(7);
//								}
//								else{
//									halfDayStart=wlocationInner.get(0);
//									halfDayEnd=wlocationInner.get(1);
//								}
//								
//								int nRosterWOff = uF.parseToInt(strRosterOffId);
//								
//								long fromTime=uF.getTimeFormat(halfDayStart, TIME_FORMAT ).getTime();
//								long toTime=uF.getTimeFormat(halfDayEnd, TIME_FORMAT ).getTime();
//								
//								dblTimeDiff=uF.parseToDouble((uF.getTimeDiffInHoursMins(fromTime,toTime)));
//								
//								int updateValue=updateRoster(con, getFromDate(),DATE_FORMAT,j,halfDayStart,halfDayEnd,dblTimeDiff,strShiftId,strEmpId, strEmpServiceId, hmEmpWLocation, hmWeekEndDates, hmHolidays,hmHolidayDates,hmEmpLevelMap,nRosterWOff,uF);
//	//							System.out.println("updateValue=="+updateValue +" for empid="+strEmpId);
//								if(updateValue == 0) {
//	//								System.out.println(" inserting......");
//									insertRoster(con, strEmpId, getFromDate(),DATE_FORMAT,j, halfDayStart,halfDayEnd, strEmpServiceId, dblTimeDiff, strShiftId, hmEmpWLocation, hmWeekEndDates, hmHolidays,hmHolidayDates,hmEmpLevelMap,nRosterWOff,uF);
//								}
//								if(ArrayUtils.contains(getStrIsAssig(), strEmpId)>=0) {
//									if(nRosterWOff > 1) {
//										insertUpdateRosterWeeklyOff(con,uF,getFromDate(),getToDate(),DATE_FORMAT,j,strEmpId, strEmpServiceId,nRosterWOff,hmRosterWeeklyoff,strShiftId);
//									} else {
//										deleteRosterWeelyOff(con,uF,getFromDate(),DATE_FORMAT,j,strEmpId, strEmpServiceId,nRosterWOff,hmRosterWeeklyoff,strShiftId);
//									}
//								}
//							}
//							else{
//								/* For full day*/
//								int nRosterWOff = uF.parseToInt(strRosterOffId);
//								
//								dblTimeDiff=uF.parseToDouble((uF.getTimeDiffInHoursMins(uF.getTimeFormat(wlocationInner.get(0), DBTIME ).getTime(),uF.getTimeFormat(wlocationInner.get(1), DBTIME ).getTime())));
//								int updateValue=updateRoster(con, getFromDate(),DATE_FORMAT,j,wlocationInner.get(0),wlocationInner.get(1),dblTimeDiff,strShiftId,strEmpId, strEmpServiceId, hmEmpWLocation, hmWeekEndDates, hmHolidays,hmHolidayDates,hmEmpLevelMap,nRosterWOff,uF);
//								if(updateValue==0) {
//									insertRoster(con, strEmpId, getFromDate(),DATE_FORMAT,j, wlocationInner.get(0),wlocationInner.get(1), strEmpServiceId, dblTimeDiff, strShiftId, hmEmpWLocation, hmWeekEndDates, hmHolidays,hmHolidayDates,hmEmpLevelMap,nRosterWOff,uF);
//								}
//								if(ArrayUtils.contains(getStrIsAssig(), strEmpId)>=0) {
//									if(nRosterWOff > 1) {
//										insertUpdateRosterWeeklyOff(con,uF,getFromDate(),getToDate(),DATE_FORMAT,j,strEmpId, strEmpServiceId,nRosterWOff,hmRosterWeeklyoff,strShiftId);
//									} else {
//										deleteRosterWeelyOff(con,uF,getFromDate(),DATE_FORMAT,j,strEmpId, strEmpServiceId,nRosterWOff,hmRosterWeeklyoff,strShiftId);
//									}
//								}
//							}
//							
//						}
//						else if(wlocationInner!=null && wlocationInner.size()>1) {
//							/* For full day*/
//							int nRosterWOff = uF.parseToInt(strRosterOffId);
//							dblTimeDiff=uF.parseToDouble((uF.getTimeDiffInHoursMins(uF.getTimeFormat(wlocationInner.get(0), DBTIME ).getTime(),uF.getTimeFormat(wlocationInner.get(1), DBTIME ).getTime())));
//							int updateValue=updateRoster(con, getFromDate(),DATE_FORMAT,j,wlocationInner.get(0),wlocationInner.get(1),dblTimeDiff,strShiftId,strEmpId, strEmpServiceId, hmEmpWLocation, hmWeekEndDates, hmHolidays,hmHolidayDates,hmEmpLevelMap,nRosterWOff,uF);
//							if(updateValue==0) {
//								insertRoster(con, strEmpId, getFromDate(),DATE_FORMAT,j, wlocationInner.get(0),wlocationInner.get(1), strEmpServiceId, dblTimeDiff, strShiftId, hmEmpWLocation, hmWeekEndDates, hmHolidays,hmHolidayDates,hmEmpLevelMap,nRosterWOff,uF);
//							}
//							
//							if(ArrayUtils.contains(getStrIsAssig(), strEmpId)>=0) {
//								if(nRosterWOff > 1) {
//									insertUpdateRosterWeeklyOff(con,uF,getFromDate(),getToDate(),DATE_FORMAT,j,strEmpId, strEmpServiceId,nRosterWOff,hmRosterWeeklyoff,strShiftId);
//								} else {
//									deleteRosterWeelyOff(con,uF,getFromDate(),DATE_FORMAT,j,strEmpId, strEmpServiceId,nRosterWOff,hmRosterWeeklyoff,strShiftId);
//								}
//							}
//						}
//					}z++; /*After complision of no. of days z will increment */
//				}
//			}
//			session.setAttribute(MESSAGE, SUCCESSM+"Roster summary<br>"+sbRosterSummary.toString()+END);
//		
//		}
//		catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			db.closeStatements(pst);
//			db.closeStatements(pst1);
//			db.closeStatements(pst3);
//			db.closeConnection(con);
//		}
//		return SUCCESS;
//	}
	
	
	private void deleteRosterWeelyOff(Connection con, UtilityFunctions uF, String strFromDate, String strDateformat, int valueOfJ, String strEmpId,
			String costCenterName, int nRosterWOff, Map<String, Map<String, String>> hmRosterWeeklyoff, String strShiftId) {
		PreparedStatement pst=null;
		try {
			pst = con.prepareStatement("delete from roster_weekly_off where emp_id=? and weekoff_date=? and service_id =?");
			pst.setInt(1, uF.parseToInt(strEmpId));
			pst.setDate(2,  uF.getDateFormat(getDate(strFromDate,strDateformat,valueOfJ),strDateformat));
			pst.setInt(3, uF.parseToInt(costCenterName));
			pst.execute();	
			pst.close();
			
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(pst !=null) {
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void insertUpdateRosterWeeklyOff(Connection con, UtilityFunctions uF, String strFromDate, String strEndDate, String strDateformat, int valueOfJ, String strEmpId,
			String costCenterName, int nRosterWOff, Map<String, Map<String, String>> hmRosterWeeklyoff, String strShiftId) {
		PreparedStatement pst=null;
		ResultSet rs = null;
		try {
			
			String strDay = uF.getDateFormat(getDate(strFromDate,strDateformat,valueOfJ),strDateformat, "EEEE");
			if(strDay!=null) strDay = strDay.toUpperCase();
				
			Map<String, String> hmInner = (Map<String, String>) hmRosterWeeklyoff.get(""+nRosterWOff);
			List<String> weeklyOffDayList = hmInner.get("WEEKLYOFF_DAY")!=null && !hmInner.get("WEEKLYOFF_DAY").equals("") ? Arrays.asList(hmInner.get("WEEKLYOFF_DAY").trim().split(",")) : new ArrayList<String>();
			List<String> weekNoList = hmInner.get("WEEKLYOFF_WEEKNO")!=null && !hmInner.get("WEEKLYOFF_WEEKNO").equals("") ? Arrays.asList(hmInner.get("WEEKLYOFF_WEEKNO").trim().split(",")) : new ArrayList<String>();
			
			
			if (weeklyOffDayList != null && weeklyOffDayList.contains(strDay)) {
				String strDate = uF.getDateFormat(getDate(strFromDate,strDateformat,valueOfJ),DATE_FORMAT, DATE_FORMAT);
				int checkWeek = CF.getMonthCount(uF, strDate);  
				if (weekNoList.contains("" + checkWeek)) {
					pst = con.prepareStatement("update roster_weekly_off set roster_weeklyoff_id=?,shift_id=? where emp_id=? and weekoff_date=? and service_id =?");
					pst.setInt(1, nRosterWOff);
					pst.setInt(2, uF.parseToInt(strShiftId));
					pst.setInt(3, uF.parseToInt(strEmpId));
					pst.setDate(4,  uF.getDateFormat(getDate(strFromDate,strDateformat,valueOfJ),strDateformat));
					pst.setInt(5, uF.parseToInt(costCenterName));
					int x = pst.executeUpdate();	
					pst.close();
					
					if (x == 0) {
						pst = con.prepareStatement("insert into roster_weekly_off (emp_id,weekoff_date,service_id,roster_weeklyoff_id,shift_id) values(?,?,?,?,?)");
						pst.setInt(1, uF.parseToInt(strEmpId));
						pst.setDate(2,  uF.getDateFormat(getDate(strFromDate,strDateformat,valueOfJ),strDateformat));
						pst.setInt(3, uF.parseToInt(costCenterName));
						pst.setInt(4, nRosterWOff);
						pst.setInt(5, uF.parseToInt(strShiftId));  
						pst.execute();	
						pst.close();
					}
				}
			}
			
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs !=null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null) {
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	 public int getMonthCount(UtilityFunctions uF,String strDate) {
	        Calendar mycal = Calendar.getInstance();
	        mycal.setTime(uF.getDateFormat(strDate, DATE_FORMAT));
	        
	        java.util.Date d1=uF.getDateFormatUtil("01"+"/"+(mycal.get(Calendar.MONTH) + 1)+"/"+mycal.get(Calendar.YEAR), DATE_FORMAT);
	        java.util.Date d2=uF.getDateFormatUtil(strDate, DATE_FORMAT);

	        int cnt =0;
	        while(d1.compareTo(d2)<=0) {
	            cnt++;
	            mycal.add(Calendar.DATE,-7);
	            d2=mycal.getTime();
	            if (cnt==10) {
	                break;
	            }
	        }
	        return cnt;
		}
	
	

	public void insertRoster(Connection con, String strEmpId,String strFromDate,String strDateformat,int valueOfJ,String strFromTime,String strToTime,String strCostCenterName,double dblTimeDiff,String strShiftId, Map hmEmpWLocation, Map<String, Set<String>> hmWeekEndDates,Map<String,String> hmHolidays ,Map<String,String> hmHolidayDates,Map<String, String> hmEmpLevelMap,int nRosterWOff, UtilityFunctions uF) {
		PreparedStatement pst=null;
		ResultSet rs= null;
		String insertRoster="insert into roster_details (emp_id, _date, _from, _to, isapproved, user_id, service_id, actual_hours, attended,is_lunch_ded,shift_id,entry_date,roster_weeklyoff_id) values(?,?,?,?,?,(select user_id from user_details where emp_id=?),?,?,?,?,?,?,?)";
		try {
			
			String strLevelId = hmEmpLevelMap.get(strEmpId);
			Set<String> weeklyOffSet= hmWeekEndDates.get(strLevelId);
			if(weeklyOffSet==null)weeklyOffSet=new HashSet<String>();
			
			String strLocationId = (String)hmEmpWLocation.get(strEmpId);
			String strDay = uF.getDateFormat(getDate(strFromDate,strDateformat,valueOfJ),strDateformat, "EEEE");
			if(strDay!=null) strDay = strDay.toUpperCase();
//			System.out.println("strEmpId==>"+strEmpId+"--getStrIsAssig()==>"+getStrIsAssig()+"--ArrayUtils.contains(getStrIsAssig(), strEmpId)>=0==>"+(ArrayUtils.contains(getStrIsAssig(), strEmpId)>=0));
//			for(String s:getStrIsAssig()) {
//				System.out.println("s==>"+s);
//			}
			List<String> alAssignEmp = getStrIsAssig() != null ? Arrays.asList(getStrIsAssig()) : new ArrayList<String>();
			if(alAssignEmp.contains(strEmpId)) {
				pst = con.prepareStatement(insertRoster); 
				pst.setInt(1, uF.parseToInt(strEmpId));
				pst.setDate(2,  uF.getDateFormat(getDate(strFromDate,strDateformat,valueOfJ),strDateformat));
				pst.setTime(3, uF.getTimeFormat(strFromTime, TIME_FORMAT));
				pst.setTime(4, uF.getTimeFormat(strToTime, TIME_FORMAT));
				pst.setBoolean(5, false);
				pst.setInt(6, uF.parseToInt(strEmpId));
				pst.setInt(7, uF.parseToInt(strCostCenterName));
				pst.setDouble(8, dblTimeDiff);
				pst.setInt(9, 0);
				pst.setBoolean(10, false);
				pst.setInt(11, uF.parseToInt(strShiftId));
				pst.setDate(12, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setInt(13, nRosterWOff);
				pst.execute();	
				pst.close();
				
				sbRosterSummary.append("<span>New shift "+hmShiftMap.get(strShiftId)+" saved for "+hmEmpName.get(strEmpId)+" for "+uF.getDateFormat(getDate(strFromDate,strDateformat,valueOfJ),strDateformat, CF.getStrReportDateFormat())+"</span><br/>");
			}
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs !=null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null) {
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	StringBuilder sbRosterSummary = new StringBuilder();
	
	
	public int updateRoster(Connection con, String strFromDate,String strDateformat,int valueOfJ,String strFromTime,String strToTime,double dblTimeDiff,String strShiftId,String strEmpId, String strServiceId, Map hmEmpWLocation, Map<String, Set<String>> hmWeekEndDates,Map<String,String> hmHolidays ,Map<String,String> hmHolidayDates,Map<String, String> hmEmpLevelMap, int nRosterWOff,UtilityFunctions uF) {
		PreparedStatement pst=null;
		ResultSet rs= null;
		int xIn=0;
		try {
			
			String strLevelId = hmEmpLevelMap.get(strEmpId);
			Set<String> weeklyOffSet = hmWeekEndDates.get(strLevelId);
			if(weeklyOffSet == null) weeklyOffSet = new HashSet<String>();
			
			String strLocationId = (String)hmEmpWLocation.get(strEmpId);
			String strDay = uF.getDateFormat(getDate(strFromDate,strDateformat,valueOfJ),strDateformat, "EEEE");
			if(strDay!=null) strDay = strDay.toUpperCase();
			
			if(ArrayUtils.contains(getStrIsAssig(), strEmpId)>=0) {
				pst = con.prepareStatement("UPDATE roster_details SET  _date= ?, _from= ?, _to= ?,  actual_hours= ?, shift_id=?, entry_date=?, roster_weeklyoff_id=? where emp_id=? and _date=? and service_id =?");
				pst.setDate(1, uF.getDateFormat(getDate(strFromDate,strDateformat,valueOfJ),strDateformat));
				pst.setTime(2, uF.getTimeFormat(strFromTime, TIME_FORMAT));
				pst.setTime(3, uF.getTimeFormat(strToTime, TIME_FORMAT));
				pst.setDouble(4, dblTimeDiff);
				pst.setInt(5, uF.parseToInt(strShiftId));
				pst.setDate(6, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setInt(7, nRosterWOff);
				pst.setInt(8, uF.parseToInt(strEmpId));
				pst.setDate(9,  uF.getDateFormat(getDate(strFromDate,strDateformat,valueOfJ),strDateformat));
				pst.setInt(10, uF.parseToInt(strServiceId));
				xIn=pst.executeUpdate();	
				pst.close();
				
				if(xIn>0) {
					sbRosterSummary.append("<span>Existing shift "+hmShiftMap.get(strShiftId)+" updated for "+hmEmpName.get(strEmpId)+" for "+uF.getDateFormat(getDate(strFromDate,strDateformat,valueOfJ),strDateformat, CF.getStrReportDateFormat())+"</span><br/>");
				}
			}
			
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs !=null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null) {
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return xIn;
	}
	
	public List<String>  shiftIdFromDates(String id) {
		
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs= null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		int empId=Integer.parseInt(id);
		List<String> alInner =new ArrayList<String>();
		
		try {
			con = db.makeConnection(con);
			
			String strEmpServiceId = CF.getEmpServiceId(con, uF, uF.parseToInt(id));
			Map<String, String> hmRosterWeeklyOff = CF.getRosterWeeklyOffPolicy(con,uF);
				
			pst = con.prepareStatement("SELECT sd.shift_id,shift_code,_date,roster_weeklyoff_id FROM roster_details rd,shift_details sd where rd.shift_id=sd.shift_id and " +
					"rd.service_id=? and rd.emp_id=? and rd._date between ? and ? order by _date");
			pst.setInt(1, uF.parseToInt(strEmpServiceId));
			pst.setInt(2, uF.parseToInt(id));
			pst.setDate(3, uF.getDateFormat(getFromDate(), DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(getToDate(), DATE_FORMAT));
//			if(uF.parseToInt(id) == 364) {
//				System.out.println("pst=======>"+pst);
//			}
			rs = pst.executeQuery();
			int shiftId = 0;
			int oldShiftId = 0;
			int wOffId = 0;
			int oldwOffId = 0;
			String endDate = null;
			Map<String, List<List<String>>> hmShiftData = new LinkedHashMap<String, List<List<String>>>();
			List<List<String>> alInner1 =new ArrayList<List<String>>();
			List<String> innerList = new ArrayList<String>();
			int count = 0;
			while(rs.next()) {
				shiftId = rs.getInt("shift_id");
				wOffId = rs.getInt("roster_weeklyoff_id");
//				if(oldShiftId == 0 || oldShiftId != shiftId || oldwOffId == 0 || oldwOffId != wOffId) {
//					if(uF.parseToInt(id) == 117) {
//						System.out.println("1=======>");
//					}
//					if(endDate != null) {
//						if(uF.parseToInt(id) == 117) {
//							System.out.println("2=======>");
//						}
//						innerList.add(uF.getDateFormat(endDate, DBDATE, CF.getStrReportDateFormat()));
//						endDate = null;
//						alInner1.add(innerList);
//						hmShiftData.put(oldShiftId+"_"+oldwOffId, alInner1);
//					}
//					oldShiftId = shiftId;
//					oldwOffId = wOffId;
//					alInner1 = hmShiftData.get(rs.getString("shift_id")+"_"+rs.getString("roster_weeklyoff_id"));
//					if(alInner1 == null) alInner1 = new ArrayList<List<String>>();
//					innerList = new ArrayList<String>();
//					innerList.add(rs.getString("shift_code"));
//					innerList.add(uF.showData(hmRosterWeeklyOff.get(rs.getString("roster_weeklyoff_id")), ""));
//					innerList.add(uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat()));
//					
//				} else if(rs.isLast()) {
//					if(uF.parseToInt(id) == 117) {
//						System.out.println("3=======>");
//					}
//					innerList.add(uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat()));
//					endDate = null;
//					alInner1.add(innerList);
//					hmShiftData.put(rs.getString("shift_id")+"_"+rs.getString("roster_weeklyoff_id"), alInner1);
//				}				
				
//				if(!rs.isLast() && (oldShiftId == 0 || oldShiftId != shiftId || oldwOffId == 0 || oldwOffId != wOffId)) {
				if((oldShiftId == 0 || oldShiftId != shiftId || oldwOffId == 0 || oldwOffId != wOffId)) {
					count++;
//					if(uF.parseToInt(id) == 364) {
//						System.out.println("1=======>");
//					}
					if(endDate != null) {
//						if(uF.parseToInt(id) == 364) {
//							System.out.println("2=======>");
//						}
						innerList.add(uF.getDateFormat(endDate, DBDATE, CF.getStrReportDateFormat()));
						endDate = null;
						alInner1.add(innerList);
						hmShiftData.put(oldShiftId+"_"+oldwOffId, alInner1);
					}
					oldShiftId = shiftId;
					oldwOffId = wOffId;
					alInner1 = hmShiftData.get(rs.getString("shift_id")+"_"+rs.getString("roster_weeklyoff_id"));
					if(alInner1 == null) alInner1 = new ArrayList<List<String>>();
					innerList = new ArrayList<String>();
					innerList.add(rs.getString("shift_code"));
					innerList.add(uF.showData(hmRosterWeeklyOff.get(rs.getString("roster_weeklyoff_id")), ""));
					innerList.add(uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat()));
					 if(rs.isLast()) {
//						if(uF.parseToInt(id) == 364) {
//							System.out.println("4=======>");
//						}
						innerList.add(uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat()));
						endDate = null;
						alInner1.add(innerList);
						hmShiftData.put(rs.getString("shift_id")+"_"+rs.getString("roster_weeklyoff_id"), alInner1);						
					 }
				} else if(rs.isLast()) {
//					if(uF.parseToInt(id) == 364) {
//						System.out.println("3=======>");
//					}
					if(count == 0) {
//						if(uF.parseToInt(id) == 364) {
//							System.out.println("count====>"+count);
//						}
						alInner1 = hmShiftData.get(rs.getString("shift_id")+"_"+rs.getString("roster_weeklyoff_id"));
						if(alInner1 == null) alInner1 = new ArrayList<List<String>>();
						innerList = new ArrayList<String>();
						innerList.add(rs.getString("shift_code"));
						innerList.add(uF.showData(hmRosterWeeklyOff.get(rs.getString("roster_weeklyoff_id")), ""));
						innerList.add(uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat()));
						innerList.add(uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat()));
						alInner1.add(innerList);
						hmShiftData.put(rs.getString("shift_id")+"_"+rs.getString("roster_weeklyoff_id"), alInner1);
					} else {
						innerList.add(uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat()));
						endDate = null;
						alInner1.add(innerList);
						hmShiftData.put(rs.getString("shift_id")+"_"+rs.getString("roster_weeklyoff_id"), alInner1);
					}
				}
				
				endDate = rs.getString("_date");
				
			}	
			rs.close();
			pst.close();
//			if(uF.parseToInt(id) == 364) {
//				System.out.println("hmShiftData ===>> " + hmShiftData);
//			}
			Iterator<String> it = hmShiftData.keySet().iterator();
			while(it.hasNext()) {
				String strShiftId = it.next();
				List<List<String>> alInner11 = hmShiftData.get(strShiftId);
				for(int i=0; alInner11!=null && i < alInner11.size(); i++) {
					List<String> al = alInner11.get(i);
					alInner.add(al.get(0));
					alInner.add(al.get(1));
					alInner.add(al.get(2));
					alInner.add(al.get(3));
				}
			}
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return alInner;
	}
	
	public void  shiftDetails(UtilityFunctions uF) {
		
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs= null;
		Database db = new Database();
		db.setRequest(request);
		
		List<String> shiftDetails =new ArrayList<String>();
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("SELECT * FROM shift_details where org_id=? order by shift_code");
			pst.setInt(1, uF.parseToInt(getStrOrg()));
			rs = pst.executeQuery();
			while(rs.next()) {
				if(!(rs.getString("shift_code").equalsIgnoreCase("ST"))) {
					
					shiftDetails.add(rs.getString("colour_code"));	
					shiftDetails.add(rs.getString("shift_code"));
					shiftDetails.add(uF.getDateFormat(rs.getString("_from"), DBTIME, CF.getStrReportTimeFormat()));
					shiftDetails.add(uF.getDateFormat(rs.getString("_to"), DBTIME, CF.getStrReportTimeFormat()));
					shiftDetails.add(uF.getDateFormat(rs.getString("break_start"), DBTIME, CF.getStrReportTimeFormat()));
					shiftDetails.add(uF.getDateFormat(rs.getString("break_end"), DBTIME, CF.getStrReportTimeFormat()));
				}
			}	
			rs.close();
			pst.close();
			request.setAttribute("shiftDetails", shiftDetails);
			
			Map<String, String> hmEmpGenderMap =CF.getEmpGenderMap(con);
			request.setAttribute("hmEmpGenderMap", hmEmpGenderMap);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}

	public List<String>  workLocationInfo(int id) {
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs= null;
		Database db = new Database();
		db.setRequest(request);
		
		List<String> wlocationInner =new ArrayList<String>();
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("SELECT * FROM (SELECT * FROM employee_official_details where emp_id=? ) aepd  JOIN work_location_info wpd ON aepd.wlocation_id = wpd.wlocation_id ");
			pst.setInt(1, id);
			rs = pst.executeQuery();
			
			while(rs.next()) {
				
				wlocationInner.add(rs.getString("wlocation_start_time"));
				wlocationInner.add(rs.getString("wlocation_end_time"));
				wlocationInner.add(rs.getString("wlocation_weeklyoff1"));
				wlocationInner.add(rs.getString("wlocation_weeklyofftype1"));
				wlocationInner.add(rs.getString("wlocation_weeklyoff2"));
				wlocationInner.add(rs.getString("wlocation_weeklyofftype2"));
				wlocationInner.add(rs.getString("wlocation_start_time_halfday"));
				wlocationInner.add(rs.getString("wlocation_end_time_halfday"));
			}	
			rs.close();
			pst.close();
			request.setAttribute("wlocationInner", wlocationInner);
		} catch (Exception e) {
			e.printStackTrace();
	//				
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return wlocationInner;
		
	}
	
	
	public Map<String, List<String>> allLiveEmployeeBasicInfo(Connection con) {
		PreparedStatement pst=null;
		ResultSet rs= null;
		
		Map<String, List<String>> hmEmpBasicData = new HashMap<String, List<String>>();
		try {
			pst = con.prepareStatement("select epd.emp_per_id, epd.emp_fname, epd.emp_lname, epd.emp_gender, ld.level_id, eod.wlocation_id from employee_personal_details epd, " +
				"employee_official_details eod left join grades_details gd on gd.grade_id = eod.grade_id left join designation_details dd on dd.designation_id = gd.designation_id " +
				"left join level_details ld on ld.level_id = dd.level_id where epd.emp_per_id > 1 and epd.is_alive = true and epd.emp_per_id=eod.emp_id order by epd.emp_per_id");
			rs = pst.executeQuery();
			while(rs.next()) {
				List<String> alInner = new ArrayList<String>();
				alInner.add(rs.getString("emp_per_id"));
				alInner.add(rs.getString("emp_gender")); //1
				alInner.add(rs.getString("wlocation_id")); //2
				alInner.add(rs.getString("level_id")); //3
				
				hmEmpBasicData.put(rs.getString("emp_per_id"), alInner);
			}
			rs.close();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
	//				
		}
		return hmEmpBasicData;
		
	}
		
	
	public String getDate(String userDate,String strFromat, int nDays )  {
		String date=userDate;
		SimpleDateFormat dateFormat= new SimpleDateFormat(strFromat);
		Calendar calendar=Calendar.getInstance();
		try {
			calendar.setTime(dateFormat.parse(date));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		calendar.add(Calendar.DATE,nDays);
		date=dateFormat.format(calendar.getTime());
		
		return date;
	}
	
	public List<FillEmployee> getEmpList() {
		return empList;
	}

	public void setEmpList(List<FillEmployee> empList) {
		this.empList = empList;
	}

	public List<String> getInnerShiftName() {
		return innerShiftName;
	}

	public void setInnerShiftName(List<String> innerShiftName) {
		this.innerShiftName = innerShiftName;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public String getShiftCode() {
		return shiftCode;
	}

	public void setShiftCode(String shiftCode) {
		this.shiftCode = shiftCode;
	}


	public List<FillCostCenter> getCostList() {
		return costList;
	}

	public void setCostList(List<FillCostCenter> costList) {
		this.costList = costList;
	}

	public List<FillShift> getShiftList() {
		return shiftList;
	}

	public void setShiftList(List<FillShift> shiftList) {
		this.shiftList = shiftList;
	}

	public String getShiftName() {
		return shiftName;
	}

	public void setShiftName(String shiftName) {
		this.shiftName = shiftName;
	}
	public String getToDate() {
		return toDate;
	}

	public void setToDate(String toDate) {
		this.toDate = toDate;
	}

	public String getFromDate() {
		return fromDate;
	}

	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}

	public String[] getCostCenterName() {
		return costCenterName;
	}

	public void setCostCenterName(String[] costCenterName) {
		this.costCenterName = costCenterName;
	}

	private HttpServletRequest request;
	private HttpServletResponse response;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
		
	}

	public boolean getIsWeekEnd() {
		return isWeekEnd;
	}

	public void setIsWeekEnd(boolean isWeekEnd) {
		this.isWeekEnd = isWeekEnd;
	}

	public boolean getIsHoliday() {
		return isHoliday;
	}

	public void setIsHoliday(boolean isHoliday) {
		this.isHoliday = isHoliday;
	}

	public String[] getStrIsAssig() {
		return strIsAssig;
	}

	public void setStrIsAssig(String[] strIsAssig) {
		this.strIsAssig = strIsAssig;
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

	public String getStrOrg() {
		return strOrg;
	}

	public void setStrOrg(String strOrg) {
		this.strOrg = strOrg;
	}

	public List<FillGender> getGenderList() {
		return genderList;
	}

	public void setGenderList(List<FillGender> genderList) {
		this.genderList = genderList;
	}

	public String getStrGender() {
		return strGender;
	}

	public void setStrGender(String strGender) {
		this.strGender = strGender;
	}

	public String[] getF_department() {
		return f_department;
	}

	public void setF_department(String[] f_department) {
		this.f_department = f_department;
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

	public String getIsRosterDependant() {
		return isRosterDependant;
	}

	public void setIsRosterDependant(String isRosterDependant) {
		this.isRosterDependant = isRosterDependant;
	}

	public List<FillRosterWeeklyOff> getRosterWOffList() {
		return rosterWOffList;
	}

	public void setRosterWOffList(List<FillRosterWeeklyOff> rosterWOffList) {
		this.rosterWOffList = rosterWOffList;
	}

	public List<String> getStrRosterWOff() {
		return strRosterWOff;
	}

	public void setStrRosterWOff(List<String> strRosterWOff) {
		this.strRosterWOff = strRosterWOff;
	}

	public String getF_RosterOff() {
		return f_RosterOff;
	}

	public void setF_RosterOff(String f_RosterOff) {
		this.f_RosterOff = f_RosterOff;
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
	public String[] getStrManager() {
		return strManager;
	}

	public void setStrManager(String[] strManager) {
		this.strManager = strManager;
	}

	public List<FillEmployee> getManagerList() {
		return managerList;
	}

	public void setManagerList(List<FillEmployee> managerList) {
		this.managerList = managerList;
	}

	public String getStrUpdate() {
		return strUpdate;
	}

	public void setStrUpdate(String strUpdate) {
		this.strUpdate = strUpdate;
	}

	public String getUpdate() {
		return update;
	}

	public void setUpdate(String update) {
		this.update = update;
	}

	public String getDefaultRosterDependant() {
		return defaultRosterDependant;
	}

	public void setDefaultRosterDependant(String defaultRosterDependant) {
		this.defaultRosterDependant = defaultRosterDependant;
	}

	public String getStrCostCenterName() {
		return strCostCenterName;
	}

	public void setStrCostCenterName(String strCostCenterName) {
		this.strCostCenterName = strCostCenterName;
	}

	public String getManager() {
		return manager;
	}

	public void setManager(String manager) {
		this.manager = manager;
	}

	public String getCheckRule() {
		return checkRule;
	}

	public void setCheckRule(String checkRule) {
		this.checkRule = checkRule;
	}

	public String[] getRosterPolicyRuleIds() {
		return rosterPolicyRuleIds;
	}

	public void setRosterPolicyRuleIds(String[] rosterPolicyRuleIds) {
		this.rosterPolicyRuleIds = rosterPolicyRuleIds;
	}
	
}