//package com.konnect.jpms.charts;
//
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpSession;
//import org.apache.struts2.interceptor.ServletRequestAware;
//import com.konnect.jpms.reports.RosterReport;
//import com.konnect.jpms.salary.SalaryDetails;
//import com.konnect.jpms.tms.ClockEntries;
//import com.konnect.jpms.util.CommonFunctions;
//import com.konnect.jpms.util.Database;
//import com.konnect.jpms.util.IStatements;
//import com.konnect.jpms.util.UtilityFunctions;
//import com.opensymphony.xwork2.ActionSupport;
//
//@SuppressWarnings("rawtypes")
//public class ClockInClockOutChart extends ActionSupport implements ServletRequestAware, IStatements{
//
//	private static final long serialVersionUID = 4967035488969759624L;
//	private String chartType;
//	HttpSession session;
//	
//	List _alDate = new ArrayList();
//	Map hmStart = new HashMap();
//	Map hmRosterStart = new HashMap();
//	Map hmRosterEnd = new HashMap();
//	Map hmEnd = new HashMap();
//	ArrayList outer=new ArrayList();
//	
//	StringBuffer Data= new StringBuffer();
//	UtilityFunctions uF = new UtilityFunctions();
//	String strEmpID;
//	String strType; 
//	String strPAY;
//	String strPC;
//	String strD1;
//	String strD2;
//	
//	String strEmpType = null;
//	CommonFunctions CF = null;
//	
//	public String execute() {
//		
//		System.out.println("execute of ClockInClockOutChart..");
//		session = request.getSession();if(session==null)return LOGIN;
//		return returnChartType();
//		
//	}
//
//	public String returnChartType() {
//		
//		setChartType(request.getParameter("chart"));
//		
//		if(getChartType().equals("candlestick")) {
//			FindNewClockEntries();
//			return "candlestick";
//		}
//		else if(getChartType().equals("spline")) {
//			FindClockEntries();
//			return "spline";
//		}
//		else if(getChartType().equals("pie")) {
//			FindLeaveEntries();
//			return "pie";
//		}
//		else if(getChartType().equals("scatter")) {
//			FindClockEntriesPerService();
//			return "scatter";
//		}
//		else if(getChartType().equals("bar")) {
//			FindSalaryEntries();
//			return "bar";
//		}
//		return SUCCESS;		
//	}
//	
//	public void FindNewClockEntries() {
//		
//		ClockEntries cE = new ClockEntries();
//		cE.setServletRequest(request);
//		cE.getAllEntries();
//		
//		_alDate = (List) request.getAttribute("alDate");
//		hmStart = (Map) request.getAttribute("hmStart");
//		hmRosterStart = (Map) request.getAttribute("hmRosterStart");
//		hmRosterEnd = (Map) request.getAttribute("hmRosterEnd");
//		hmEnd = (Map) request.getAttribute("hmEnd");
//
//		//Find services employee has worked upon
//		
//		for(int i=0; i< _alDate.size(); i++) {
//
//			if(!strDate.equals("0")) {
//				
//				startTime = (String)hmStart.get((String) _alDate.get(i));
//						
//			}else
//				
//				continue;
//			
//		}
//		
//		String strService, strDate, startTime, endTime, RosterStartTime, RosterEndTime;
//		
//		
//		
//		for(int i=0; i< _alDate.size(); i++) {
//			
////			System.out.println("inside for loop->"+i+" "+(String) _alDate.get(i));
//			
//			String date = (String)_alDate.get(i);
//			
//			strDate = uF.showData((String) hmStart.get((String) _alDate.get(i)), "0");
//			if(!strDate.equals("0")) {
//				
//				startTime = (String)hmStart.get((String) _alDate.get(i));
//						
//			}else
//				continue;
//			
//			strDate = uF.showData((String) hmRosterStart.get((String) _alDate.get(i)), "0");
//			if(!strDate.equals("0")) {
//				
//				RosterStartTime = (String)hmRosterStart.get((String) _alDate.get(i));
//				
//			}else
//				continue;
//			
//			strDate = uF.showData((String) hmRosterEnd.get((String) _alDate.get(i)), "0");
//			if(!strDate.equals("0")) {
//				
//				RosterEndTime = (String)hmRosterEnd.get((String) _alDate.get(i));
//				
//			}else
//				continue;
//
//			strDate = uF.showData((String) hmEnd.get((String) _alDate.get(i)), "0");
//			if(!strDate.equals("0")) {
//				
//				endTime = (String)hmEnd.get((String) _alDate.get(i));
//				
//			}else
//				continue;
//
//			//open,high,low,close
//			outer.add(date+";"+startTime+";"+RosterStartTime+";"+endTime+";"+RosterEndTime);
//			
//		}
//		
////		System.out.println("size of outer-->>"+outer.size()+"\nouter=>"+outer);
//		
//		
//		/*for(int i=0;i<outer.size();i++){
//			System.out.println(outer.get(i));
//		}*/
//		
//		request.setAttribute("Data", outer);
//		
//	}
//	
//	
//	public void FindSalaryEntries() {
//		
//		//value 10,000, 2000 used as sample ammount for Earnings & Deduction Heads Calculation 
//		
//		int Total_Earnings = 10000;
//		int Total_Deduction = 2000;
//		
//		List<List<String>> al = new ArrayList<List<String>>();
//		SalaryDetails sD = new SalaryDetails();
//		sD.setServletRequest(request);
//		sD.execute();
//		al = (List<List<String>>) request.getAttribute("reportList");
//		request.setAttribute("reportList", al);
//		
//		StringBuffer sbHeadName = new StringBuffer();
//		StringBuffer sbHeadValue = new StringBuffer();
//		sbHeadName.append("[");
//		sbHeadValue.append("[");
//		
//		int i;
//		for(i=0; i<al.size()-1; i++) {
//			
//			sbHeadName.append("'"+al.get(i).get(1)+"'"+",");
//			String PorA =  al.get(i).get(3);
//			
//			if(PorA.equalsIgnoreCase("A")) {
//				
//				sbHeadValue.append(al.get(i).get(4)+",");
//				
//			}else {
//				
//				if(al.get(i).get(2).equalsIgnoreCase("E")) {
//					float percentValue = Total_Earnings * (Integer.parseInt(al.get(i).get(4))) / 100;
//					sbHeadValue.append(percentValue+",");
//				
//				}else {
//					float percentValue = Total_Deduction * (Integer.parseInt(al.get(i).get(4))) / 100;
//					sbHeadValue.append(percentValue+",");
//				}
//			}
//		}
//		
//		sbHeadName.append("'"+al.get(i).get(1)+"'");
//		String PorA =  al.get(i).get(3);
//		
//		if(PorA.equalsIgnoreCase("A")) {
//			
//			sbHeadValue.append(al.get(i).get(4));
//			
//		}else {
//			
//			if(al.get(i).get(2).equalsIgnoreCase("E")) {
//				float percentValue = Total_Earnings * (Integer.parseInt(al.get(i).get(4))) / 100;
//				System.out.println("percentValue for earnings====>>"+percentValue);
//				sbHeadValue.append(percentValue);
//			
//			}else {
//				float percentValue = Total_Deduction * (Integer.parseInt(al.get(i).get(4))) / 100;
////				System.out.println("percentValue for deduciton====>>"+percentValue);
//				sbHeadValue.append(percentValue);
//			}
//		}
//		
//		sbHeadName.append("]");
//		sbHeadValue.append("]");
//		
////		System.out.println("sbHeadName=>"+sbHeadName.toString());
////		System.out.println("sbHeadValue=>"+sbHeadValue.toString());
//		
//		request.setAttribute("sbHeadName", sbHeadName.toString());
//		request.setAttribute("sbHeadValue", sbHeadValue.toString());
//		
//	}
//	
//	public void FindClockEntriesPerService() {
//		
//		//Find Roster Entries For Whole Year
////		Map<String, Map<String, String>> hmServicesWorkrdFor = new HashMap<String, Map<String, String>>();
//		RosterReport rP = new RosterReport();
//		rP.setServletRequest(request);
//		rP.setStrUserType("Employee");	
//		rP.setPaycycle("13/01/2011-11/01/2012");
//		
//		try {
//			rP.execute();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//		//Get the data
////		List alDay = (List) request.getAttribute("alDay");
//		List alDate = (List) request.getAttribute("alDate");
//		List alEmpId = (List) request.getAttribute("alEmpId");	//180 - Aaron 1234
////		Map hmRosterServiceId = (HashMap) request.getAttribute("hmRosterServiceId");
//		Map hmRosterServiceName = (HashMap) request.getAttribute("hmRosterServiceName");
//		List alServiceId = (List) request.getAttribute("alServiceId");
//		Map hmList = (Map) request.getAttribute("hmList");
//		
//		
////		List alDay = (List) request.getAttribute("alDay");
////		List alDate = (List) request.getAttribute("alDate");
////		List alEmpId = (List) request.getAttribute("alEmpId");
////		Map hmList = (HashMap) request.getAttribute("hmList");
////		List _alHolidays = (List) request.getAttribute("_alHolidays");		// [170, 254, 255]
////		Map _hmHolidaysColour = (HashMap) request.getAttribute("_hmHolidaysColour");
////		Map hmServicesWorkrdFor = (HashMap) request.getAttribute("hmServicesWorkrdFor");
////		Map hmRosterServiceId = (HashMap) request.getAttribute("hmRosterServiceId");
////		Map hmRosterServiceName = (HashMap) request.getAttribute("hmRosterServiceName");	//{43=IT, 37=OracleCMS, 38=Annecto}
////		List alServiceId = (List) request.getAttribute("alServiceId");			//[37, 38, 43]
////		Map hmServices = (HashMap) request.getAttribute("hmServices");			//{43=IT, 37=OracleCMS, 38=Annecto}
////		String empRosterDetails = (String) request.getAttribute("empRosterDetails");
//		
//		
////		System.out.println("alDay="+alDay);
////		System.out.println("alDate="+alDate);
////		System.out.println("alEmpId="+alEmpId);
////		System.out.println("hmList="+hmList);
////		System.out.println("_alHolidays="+_alHolidays);
////		System.out.println("_hmHolidaysColour="+_hmHolidaysColour);
////		System.out.println("hmServicesWorkrdFor="+hmServicesWorkrdFor);
////		System.out.println("hmRosterServiceId="+hmRosterServiceId);
////		System.out.println("hmRosterServiceName="+hmRosterServiceName);
////		System.out.println("alServiceId="+alServiceId);
////		System.out.println("hmServices="+hmServices);
////		System.out.println("empRosterDetails="+empRosterDetails);
//		
//		/* Arrange the Data For Charts
//		 * Outer Hashmap for Services, Inner Hashmap for Dates per service, Innermost for IN & OUT TimeEntries per date 
//		*/
//		
//		Map alClockEntries = new HashMap();
//		Map alPerService = new HashMap();
//		Map alPerDate = new HashMap();
//		
//		for (int k = 0; k < alServiceId.size() && alEmpId.size() > 0; k++) {
//			
//			Map hm = (Map) hmList.get((String) alEmpId.get(0));
//			String strServiceId = (String) alServiceId.get(k);
//			String strServiceName = (String) hmRosterServiceName.get(strServiceId);
//			
//			alPerService = new HashMap();
//			
//			for (int i = 7; i < alDate.size(); i++) {
//				
//				alPerDate = new HashMap();	
//				alPerDate.put("IN" ,((hm.containsKey((String) alDate.get(i) + "FROM_" + strServiceId)) ? (String) hm.get((String) alDate.get(i) + "FROM_" + strServiceId) : "-"));
//				alPerDate.put("OUT",((hm.containsKey((String) alDate.get(i) + "TO_" + strServiceId)) ? (String) hm.get((String) alDate.get(i) + "TO_" + strServiceId) : "-"));
//				alPerService.put( alDate.get(i) ,alPerDate);
//			}
//			alClockEntries.put(strServiceName,alPerService);
//		}
//		
////		System.out.println("alClockEntries="+alClockEntries);
//		
//		//Make Variables to pass into Charts
//		
//		//In Entries for all Days for all Services
//		
//		ArrayList outerIn = new ArrayList();
//		ArrayList innerIn = new ArrayList();
//		for (int k = 0; k < alServiceId.size() && alEmpId.size() > 0; k++) {
//			
//			Map hm = (Map) hmList.get((String) alEmpId.get(0));
//			String strServiceId = (String) alServiceId.get(k);
//			String strServiceName = (String) hmRosterServiceName.get(strServiceId);
//			
//			innerIn = new ArrayList();
//			
//			for (int i = 7; i < alDate.size(); i++) {
//				
//				if(!((HashMap)((HashMap)alClockEntries.get(strServiceName)).get(alDate.get(i))).get("IN").equals("-")) {
//					
//					innerIn.add(
//							strServiceName + " -IN Entries ;" +
//							alDate.get(i) + ";" +
//							((HashMap)((HashMap)alClockEntries.get(strServiceName)).get(alDate.get(i))).get("IN") + ";" + 
//							"IN"
//							);
//				}
//			}
//			outerIn.add(innerIn);
//		}
//		
////		System.out.println("outerIn=>"+outerIn);
//		
//		//Remove the 0 size lists if any!
//		for(int i=0; i<outerIn.size(); i++) {
//			
////			System.out.println("((ArrayList)outerIn.get(i)).size()="+((ArrayList)outerIn.get(i)).size());
//			if(((ArrayList)outerIn.get(i)).size() == 0) {
//				outerIn.remove(i);
//				i = i - 1;
//			}
//		}
//		
////		System.out.println("outerIn after remove=>"+outerIn);
//		
//		//Find the size of Service Entries
//		
////		System.out.println("outerIn().size()=>"+outerIn.size());
//		ArrayList alServiceSizeIn = new ArrayList();
//		int size = 0;
//		for (int k = 0; k < outerIn.size(); k++) {
//			
//			Map hm = (Map) hmList.get((String) alEmpId.get(0));
//			String strServiceId = (String) alServiceId.get(k);
//			String strServiceName = (String) hmRosterServiceName.get(strServiceId);
//			
//			size += (((ArrayList)outerIn.get(k)).size());
//			
//			alServiceSizeIn.add(Integer.toString(size));
//		}
//		
//		//Out Entries for all Days for all Services
//		
//		ArrayList outerOut = new ArrayList();
//		ArrayList innerOut = new ArrayList();
//		
//		for (int k = 0; k < alServiceId.size() && alEmpId.size() > 0; k++) {
//			
//			Map hm = (Map) hmList.get((String) alEmpId.get(0));
//			String strServiceId = (String) alServiceId.get(k);
//			String strServiceName = (String) hmRosterServiceName.get(strServiceId);
//			
//			innerOut = new ArrayList();
//			for (int i = 7; i < alDate.size(); i++) {
//				if(!((HashMap)((HashMap)alClockEntries.get(strServiceName)).get(alDate.get(i))).get("OUT").equals("-")) {
//				innerOut.add(
//						strServiceName + " OUT Entries;" + 
//						alDate.get(i) + ";" +
//						((HashMap)((HashMap)alClockEntries.get(strServiceName)).get(alDate.get(i))).get("OUT") + ";" + "OUT"
//					);
//				}
//			}
//			outerOut.add(innerOut);
//		}
//		
//		for(int i=0; i<outerOut.size(); i++) {
//			
//			if(((ArrayList)outerOut.get(i)).size() == 0) {
//				outerOut.remove(i);
//				i= i -1;
//			}
//		}
//		
//		//Find the size of Service Entries 
//		
//		System.out.println("outerOut().size()=>"+outerOut.size());
//		System.out.println("outerOut after remove=>"+outerOut);
//		
//		ArrayList alServiceSizeOut = new ArrayList();
//		
//		for (int k = 0; k < outerOut.size(); k++) {
//			
//			Map hm = (Map) hmList.get((String) alEmpId.get(0));
//			String strServiceId = (String) alServiceId.get(k);
//			String strServiceName = (String) hmRosterServiceName.get(strServiceId);
//			size += ((ArrayList)outerOut.get(k)).size(); 
//			alServiceSizeOut.add(Integer.toString(size));
//			
//		}
//		
//		System.out.println("alServiceSizeIn====>>"+alServiceSizeIn);
//		System.out.println("alServiceSizeOut===>>"+alServiceSizeOut);
//		System.out.println("outerIn====>>"+outerIn);
//		System.out.println("outerOut====>>"+outerOut);
//		System.out.println("ServicesCount====>>"+alServiceId.size()+"");
//		
//		request.setAttribute("alServiceSizeIn", alServiceSizeIn);
//		request.setAttribute("alServiceSizeOut", alServiceSizeOut);
//		request.setAttribute("ServicesCount", (alServiceId.size()+""));
//		request.setAttribute("outerIn", outerIn);
//		request.setAttribute("outerOut", outerOut);
//		
//	}
//	
//	public void FindLeaveEntries() {
//		
//		Connection con=null;
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		Database db = new Database();
//		db.setRequest(request);
//		ArrayList al = new ArrayList();
//		ArrayList alTotalLeaves = new ArrayList();
//		String EMPID = (String) session.getAttribute("EMPID");
//		
//		//Select Leaves Taken
//		try {
//			con = db.makeConnection(con);
//			pst = con.prepareStatement(selectLeavesTaken);
//			pst.setInt(1, Integer.parseInt(EMPID));
//			rs = pst.executeQuery();
//			
//			while(rs.next()) {
//				ArrayList inner = new ArrayList();
//				inner.add(rs.getString("leave_type_name"));
//				inner.add(rs.getString("leaves_taken"));
//				al.add(inner);
//			}
//	        rs.close();
//	        pst.close();
//		}catch(SQLException se) {
//			se.printStackTrace();
//		} finally {
//			db.closeResultSet(rs);
//			db.closeStatements(pst);
//			db.closeConnection(con);
//		}
//		
//		//select total leaves available
//		try {
//			con = db.makeConnection(con);
//			pst = con.prepareStatement(selectTotalLeaves);
//			pst.setInt(1, 3);
//			rs = pst.executeQuery();
//			while(rs.next()) {
//				ArrayList inner = new ArrayList();
//				inner.add(rs.getString("leave_type_name"));
//				inner.add(rs.getString("no_of_leave"));
//				alTotalLeaves.add(inner);
//			}
//	        rs.close();
//	        pst.close();
//		}catch(SQLException se) {
//			se.printStackTrace();
//		} finally {
//			db.closeResultSet(rs);
//			db.closeStatements(pst);
//			db.closeConnection(con);
//		}
//		
//		//Calculate Remaining Leaves
//		
//		ArrayList alRemainingLeaves = new ArrayList();
//		
//		for(int i=0; i<al.size(); i++) {
//			
//			for(int j=0; j<alTotalLeaves.size(); j++) {
//			
//				if( ((ArrayList)al.get(i)).get(0).equals( ((ArrayList)alTotalLeaves.get(j)).get(0) ) ){
//		
//					int remaining_leaves = Integer.parseInt((String)((ArrayList)(alTotalLeaves.get(j))).get(1)) - Integer.parseInt((String)((ArrayList)al.get(i)).get(1));
//					alRemainingLeaves.add(Integer.toString(remaining_leaves));
//				}
//			}
//		}
//		
//		request.setAttribute("alLeavesTaken", al);
//		request.setAttribute("alRemainingLeaves", alRemainingLeaves);
//		
//	}
//	
//	@SuppressWarnings("unchecked")
//	public void FindClockEntries() {
//		
//		ClockEntries cE = new ClockEntries();
//		cE.setServletRequest(request);
//		cE.getAllEntries();
//		
//		_alDate = (List) request.getAttribute("alDate");
//		hmStart = (Map) request.getAttribute("hmStart");
//		hmRosterStart = (Map) request.getAttribute("hmRosterStart");
//		hmRosterEnd = (Map) request.getAttribute("hmRosterEnd");
//		hmEnd = (Map) request.getAttribute("hmEnd");
//		
//		System.out.println(_alDate.size());
//		System.out.println(hmStart.size());
//		System.out.println(hmRosterStart.size());
//		System.out.println(hmRosterEnd.size());
//		System.out.println(hmEnd.size());
//		
//		System.out.println("hmStart->"+hmStart);
//		System.out.println("hmRosterStart->"+hmRosterStart);
//		System.out.println("hmRosterEnd->"+hmRosterEnd);
//		System.out.println("hmEnd->"+hmEnd);
//		
//		for(int i=0; i<_alDate.size(); i++) {
//			System.out.println("hmStart-"+i+" "+hmStart.get((String) _alDate.get(i)));
//		}
//		for(int i=0; i<_alDate.size(); i++) {
//			System.out.println("hmRosterStart-"+i+" "+hmRosterStart.get((String) _alDate.get(i)));
//		}
//		for(int i=0; i<_alDate.size(); i++) {
//			System.out.println("hmRosterEnd-"+i+" "+hmRosterEnd.get((String) _alDate.get(i)));
//		}
//		for(int i=0; i<_alDate.size(); i++) {
//			System.out.println("hmEnd-"+i+" "+hmEnd.get((String) _alDate.get(i)));
//		}
//		
//		String strDate, startTime, endTime, RosterStartTime, RosterEndTime;
//		
//		for(int i=0; i< _alDate.size(); i++) {
//			
//			System.out.println("inside for loop->"+i+" "+(String) _alDate.get(i));
//			
//			String date = (String)_alDate.get(i);
//			
//			strDate = uF.showData((String) hmStart.get((String) _alDate.get(i)), "0");
//			
//			if(!strDate.equals("0")) {
//				
//				startTime = (String)hmStart.get((String) _alDate.get(i));
//						
//			}else
//				continue;
//			
//			strDate = uF.showData((String) hmRosterStart.get((String) _alDate.get(i)), "0");
//			if(!strDate.equals("0")) {
//				
//				RosterStartTime = (String)hmRosterStart.get((String) _alDate.get(i));
//			}else
//				continue;
//			
//			strDate = uF.showData((String) hmRosterEnd.get((String) _alDate.get(i)), "0");
//			if(!strDate.equals("0")) {
//				
//				RosterEndTime = (String)hmRosterEnd.get((String) _alDate.get(i));
//			}else
//				continue;
//
//			strDate = uF.showData((String) hmEnd.get((String) _alDate.get(i)), "0");
//			if(!strDate.equals("0")) {
//				
//				endTime = (String)hmEnd.get((String) _alDate.get(i));
//			}else
//				continue;
//
//			//open,high,low,close
//			outer.add(date+";"+startTime+";"+RosterStartTime+";"+endTime+";"+RosterEndTime);
//			
//		}
//		
//		System.out.println("size of outer-->>"+outer.size()+"\nouter=>"+outer);
//		
//		
//		/*for(int i=0;i<outer.size();i++){
//			System.out.println(outer.get(i));
//		}*/
//		
//		request.setAttribute("Data", outer);
//		
//	}
//	
//	private HttpServletRequest request;
//
//	@Override
//	public void setServletRequest(HttpServletRequest request) {
//		this.request = request;
//	}
//	
//	public void setChartType(String chartType) {
//		this.chartType = chartType;
//	}
//	
//	public String getChartType() {
//		return chartType;
//	}
//	
//}
