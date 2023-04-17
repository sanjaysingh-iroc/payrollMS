package com.konnect.jpms.meetingroom;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillDishes;
import com.konnect.jpms.select.FillEmployee;
import com.konnect.jpms.select.FillMealType;
import com.konnect.jpms.select.FillMeetingRooms;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UserAlerts;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class MeetingRoomBookingPopup extends ActionSupport implements ServletRequestAware, IStatements {
	private static final long serialVersionUID = 1L;
	public HttpSession session;
	String strSessionOrgId;
	String strSessionEmpId;
	String strUserType;
	public CommonFunctions CF; 
	
	
	private String strMeetingRoomId;
	private String strBooking_from;
	private String strBooking_to;
	private String strFrom_time;
	private String strTo_time;
	private String[] strParticipants;
	private String strFoodServiceId;
	private String strFoodServiceDetails;
	private String strLocation;
	private String strBookingComment;
	
	private String isFoodRequired;
	private String strNoOfParticipants;
	private String strFoodServiceLocation;
	private String bookingDate ;
	
	private List<FillEmployee> participantsList;
	private List<FillMeetingRooms> meetingRoomsList;
	
	
	private String operation;
	private String bookingId;
	private String strSubmit;
	private String strUpdate;
	
	private String isFoodServiceRequired;
	private String strEmpIds;
	private String location;
	
	private String strDishIds;
	private String mealType;
	
	private String meetingRoomId;
	private String strCapacity;
	
	private List<FillMealType> mealTypeList;
	private List<FillDishes> dishList;
	
	private String strCancel;
	private static Logger log = Logger.getLogger(MeetingRoomBookingPopup.class);
	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)	return LOGIN;

		strUserType = (String)session.getAttribute(USERTYPE);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		
		UtilityFunctions uF = new UtilityFunctions();
		
		request.setAttribute(PAGE, "/jsp/meetingroom/MeetingRoomBookingPopup.jsp");
		request.setAttribute(TITLE, "Meeting Room Booking");
		
		loadData(uF);
//	    System.out.println("getOperation==>"+getOperation());
//	    System.out.println("getStrSubmit==>"+getStrSubmit());
	    
		if(getOperation()!=null && getOperation().equals("D")) {
			deleteBooking(uF);
			return LOAD;
		}else if(getStrUpdate()==null && getOperation()!=null && getOperation().equals("E")) {
			getBookingDetails(uF);
		}

		if(getStrUpdate()!=null ) {
			updateBookingDetails(uF);
			return LOAD;
		}
		
		if(getStrSubmit() != null) {
			
				bookMeetingRooms(uF);
				return LOAD;
			
		}
	
		if(getOperation()!=null && getOperation().equals("C")) {
			return LOAD;
		}
		
		return SUCCESS;
	}
	
	
	private void getBookingDetails(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try{
			con = db.makeConnection(con);
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from meeting_room_details md, meeting_room_booking_details mb  where md.meeting_room_id = mb.meeting_room_id and booking_id = ?");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1,uF.parseToInt(getBookingId()));
			rs = pst.executeQuery();
			while(rs.next()){
				setStrBooking_from(uF.getDateFormat(rs.getString("booking_from"),DBDATE, DATE_FORMAT));
				setStrBooking_to(uF.getDateFormat(rs.getString("booking_to"),DBDATE, DATE_FORMAT));
				String from_time = rs.getString("from_time");
				String to_time = rs.getString("to_time");
				
				if(from_time != null && !from_time.equals("")){
					setStrFrom_time(from_time.substring(0,from_time.lastIndexOf(":")));//14
				}else{
					setStrFrom_time("");
				}
				if(to_time != null  && !to_time.equals("")){
					setStrTo_time(to_time.substring(0,to_time.lastIndexOf(":")));//15
				}else{
					setStrTo_time("");
				}
				
				if(rs.getString("meeting_room_id") != null && !rs.getString("meeting_room_id").equals("")) {
					setStrMeetingRoomId(rs.getString("meeting_room_id"));
					setMeetingRoomId(rs.getString("meeting_room_id"));
				}
				
				setStrBookingComment(uF.showData(rs.getString("booking_request_comment"),""));
				setStrNoOfParticipants(uF.showData(rs.getString("no_of_people"),""));
				setStrFoodServiceDetails(uF.showData(rs.getString("food_service_details"),""));
				setStrFoodServiceLocation(uF.showData(rs.getString("food_service_location"),""));
				setIsFoodServiceRequired(rs.getBoolean("is_food_required") ? "1" : "2");
			
				if(rs.getString("food_dish_types") != null && !rs.getString("food_dish_types").equals("")) {
					setMealType(rs.getString("food_dish_types"));
				}
				
				
				String dishId = rs.getString("food_service_details");
				StringBuilder sbDishes = new StringBuilder();
				if( dishId != null && !dishId.equals("")) {
					List<FillDishes> dishLst  = new ArrayList<FillDishes>();
					if(strUserType != null && strUserType.equals(ADMIN)) {
						dishLst = new FillDishes(request).fillDishes("",rs.getString("food_dish_types"), rs.getString("booking_from"),rs.getString("booking_to"),strUserType,CF);
					} else if(strUserType != null && strUserType.equals(HRMANAGER)) {
						if((String)session.getAttribute(WLOCATION_ACCESS) != null && !((String)session.getAttribute(WLOCATION_ACCESS)).equals("")) {
							dishLst = new FillDishes(request).fillDishes((String)session.getAttribute(WLOCATION_ACCESS),rs.getString("food_dish_types"), rs.getString("booking_from"),rs.getString("booking_to"),strUserType,CF);
						} else {
							dishLst = new FillDishes(request).fillDishes((String)session.getAttribute(WLOCATIONID),rs.getString("food_dish_types"), rs.getString("booking_from"),rs.getString("booking_to"),strUserType,CF);
						}
					} else {
						dishLst = new FillDishes(request).fillDishes((String)session.getAttribute(WLOCATIONID),rs.getString("food_dish_types"), rs.getString("booking_from"),rs.getString("booking_to"),strUserType,CF);
					}
					
					//List<FillDishes> dishLst = new FillDishes(request).fillDishes(rs.getString("food_dish_types"), rs.getString("booking_from"),rs.getString("booking_to"),strUserType,CF);
					for(int i=0; dishLst!=null && i<dishLst.size(); i++){
//						System.out.println("dishLst.get(i).getDishId()==>"+dishLst.get(i).getDishId()+"==>dishId==>"+dishId);
						if(dishLst.get(i).getDishId().equals(dishId)) {
							sbDishes.append("<option value='"+dishLst.get(i).getDishId()+"' selected>"+dishLst.get(i).getDishName()+"</option>");
						} else {
							sbDishes.append("<option value='"+dishLst.get(i).getDishId()+"'>"+dishLst.get(i).getDishName()+"</option>");
						}
					}
				}
				
				String strParticipants =  rs.getString("participants");
				List<String> empList = new ArrayList<String>();
				if(strParticipants != null && !strParticipants.equals("")) {
					empList = Arrays.asList(strParticipants.split(","));
				}
				
				StringBuilder sbParticipants = new StringBuilder();
				if(empList.size() > 0){
					
					for(int i=0; getParticipantsList()!=null && i<getParticipantsList().size(); i++) {
						if(empList.contains(getParticipantsList().get(i).getEmployeeId())) {
							sbParticipants.append("<option value='"+getParticipantsList().get(i).getEmployeeId()+"' selected>"+getParticipantsList().get(i).getEmployeeName()+"</option>");
						} else {
							sbParticipants.append("<option value='"+getParticipantsList().get(i).getEmployeeId()+"'>"+getParticipantsList().get(i).getEmployeeName()+"</option>");
						}
					}
				}else if(empList.size() == 0){
					
					for(int i=0; getParticipantsList()!=null && i<getParticipantsList().size(); i++) {
						sbParticipants.append("<option value='"+getParticipantsList().get(i).getEmployeeId()+"'>"+getParticipantsList().get(i).getEmployeeName()+"</option>");
					}
				}
				
				List<String> guestList = new ArrayList<String>();
				String strGuests = rs.getString("guests");
				String[] guests = null;
				StringBuilder sbGuests = null;
				if(strGuests!=null && !strGuests.equals("")){
					guests = strGuests.split(",");
					
					for(String guest : guests) {
						if(guest==null || guest.trim().equals("") || guest.trim().equalsIgnoreCase("")){
							continue;
						}
						
						guestList.add(guest);
						
					}
					
					
				}
				if(sbGuests == null) {
					sbGuests = new StringBuilder();
				}
				
				request.setAttribute("sbDishes", sbDishes.toString());
				request.setAttribute("sbGuests", sbGuests.toString());
				request.setAttribute("sbParticipants", sbParticipants.toString());
				request.setAttribute("guestList", guestList);
				
				
			}
			rs.close();
			pst.close();
			
						
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	

	private void deleteBooking(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		
		try{
			
			con = db.makeConnection(con);
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("delete from meeting_room_booking_details where booking_id=?");
					
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1,uF.parseToInt(getBookingId()));
			pst.execute();
			pst.close();
			
			pst = con.prepareStatement("delete from dish_order_details where booking_id=?");
			pst.setInt(1,uF.parseToInt(getBookingId()));
			pst.execute();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	private void updateBookingDetails(UtilityFunctions uF) {
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		try {
			
			con = db.makeConnection(con);
			Map<String, String> hmUserTypeId = CF.getUserTypeIdMap(con);
			
			StringBuilder participants = null;
			if(getStrParticipants() != null && getStrParticipants().length>0 ){
				for(String emp :getStrParticipants()) {
					if(participants == null) {
						participants = new StringBuilder();
						participants.append(","+emp+",");
					} else {
						participants.append(emp+",");
					}
				}
			}
			
			if(participants == null) {
				participants = new StringBuilder();
			}
			
			StringBuilder guests = null;
			StringBuilder sbGuests = null;
			String[] strGuest = request.getParameterValues("strGuest");
			if(strGuest != null && strGuest.length>0 ){
				for(String guest :strGuest) {
					if(guests == null) {
						guests = new StringBuilder();
						guests.append(","+guest+",");
					} else {
						guests.append(guest+",");
					}
					
					if(sbGuests == null) {
						sbGuests = new StringBuilder();
						sbGuests.append(guest);
					} else {
						sbGuests.append(","+guest);
					}
				}
			}
			
			if(guests == null) {
				guests = new StringBuilder();
			}
			
			if(sbGuests == null) {
				sbGuests = new StringBuilder();
			}
			
			pst = con.prepareStatement("update meeting_room_booking_details set meeting_room_id=?, booking_from=?, booking_to=?, from_time=?,"
				+"to_time=?,updated_by=?,updated_date=?,participants=?,request_status=?,no_of_people=? ,food_service_details=? ,meeting_room_location=?, " +
				"guests=? ,booking_request_comment=?,food_dish_types=?,is_food_required=? where booking_id=?");
			pst.setInt(1, uF.parseToInt(getStrMeetingRoomId()));
			pst.setDate(2, uF.getDateFormat(getStrBooking_from(),DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(getStrBooking_to(),DATE_FORMAT));
			pst.setTime(4, uF.getTimeFormat(getStrFrom_time(), TIME_FORMAT));
			pst.setTime(5, uF.getTimeFormat(getStrTo_time(), TIME_FORMAT));
			pst.setInt(6, uF.parseToInt(strSessionEmpId));
			pst.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setString(8, participants.toString());
			pst.setInt(9, 0);
			pst.setInt(10, uF.parseToInt(getStrNoOfParticipants()));
			pst.setString(11, getStrDishIds());
			pst.setInt(12, uF.parseToInt(getLocation()));
			pst.setString(13, guests.toString());
			pst.setString(14, getStrBookingComment());
			pst.setInt(15, uF.parseToInt(getMealType()));
			pst.setBoolean(16, uF.parseToBoolean(getIsFoodRequired()));
			pst.setInt(17, uF.parseToInt(getBookingId()));
			pst.execute();
			pst.close();
			
			
			pst=con.prepareStatement("select emp_per_id from employee_official_details eod,user_details ud, employee_personal_details epd where epd.emp_per_id=eod.emp_id and ud.emp_id=eod.emp_id and is_alive = true and ud.usertype_id=?");
			pst.setInt(1, uF.parseToInt(hmUserTypeId.get(ADMIN)));
			rs=pst.executeQuery();
			List<String> empList = new ArrayList<String>();
			while(rs.next()) {
				if(!empList.contains(rs.getString("emp_per_id").trim())) {
					empList.add(rs.getString("emp_per_id").trim());	
				}
			}
			rs.close();
			pst.close();
			
			String strDomain = request.getServerName().split("\\.")[0];
			if(getStrDishIds() != null && !getStrDishIds().equals("")) {
				
				pst = con.prepareStatement("update dish_order_details set dish_id = ?, emp_id = ?, dish_quantity = ?, order_status = ?, order_date = ?,booking_id = ?,guest_names = ? where booking_id =?");
				pst.setInt(1, uF.parseToInt(getStrDishIds()));
				pst.setInt(2,uF.parseToInt(strSessionEmpId));
				pst.setInt(3, uF.parseToInt(getStrNoOfParticipants()));
				pst.setInt(4, 0);
				pst.setDate(5,uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setInt(6,uF.parseToInt(getBookingId()));
				pst.setString(7,sbGuests.toString());
				pst.setInt(8,uF.parseToInt(getBookingId()));
				int x= pst.executeUpdate();
				pst.close();
				
				if(x == 0) {
				    pst = con.prepareStatement("insert into dish_order_details (dish_id, emp_id, dish_quantity, order_status, order_date,booking_id,guest_names) values (?,?,?,?,?,?,?)");
					pst.setInt(1, uF.parseToInt(getStrDishIds()));
					pst.setInt(2,uF.parseToInt(strSessionEmpId));
					pst.setInt(3, uF.parseToInt(getStrNoOfParticipants()));
					pst.setInt(4, 0);
					pst.setDate(5,uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setInt(6,uF.parseToInt(getBookingId()));
					pst.setString(7,sbGuests.toString());
					pst.execute();
					pst.close();
				}		
					
				for(int i=0; empList!= null && !empList.isEmpty() && i<empList.size(); i++) {
					if(!empList.get(i).equals("") && uF.parseToInt(empList.get(i)) > 0) {
						
						String alertData = "<div style=\"float: left;\"> A new Cafeteria Request from <b>"+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>. </div>";
						String alertAction = "Cafeteria.action?pType=WR";
						
						UserAlerts userAlerts = new UserAlerts(con, uF, CF, request);
						userAlerts.setStrDomain(strDomain);
						userAlerts.setStrEmpId(empList.get(i));
						userAlerts.setStrData(alertData);
						userAlerts.setStrAction(alertAction);
						userAlerts.setCurrUserTypeID(hmUserTypeId.get(ADMIN));
						userAlerts.setStatus(INSERT_WR_ALERT);
						Thread t = new Thread(userAlerts);
						t.run();
						
//						UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
//						userAlerts.setStrDomain(strDomain);
//						userAlerts.setStrEmpId(empList.get(i));
//						userAlerts.set_type(FOOD_REQUEST_ALERT);
//						userAlerts.setStatus(INSERT_ALERT);
//						Thread t = new Thread(userAlerts);
//						t.run();
					}
				}

			}
		
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	private void loadData(UtilityFunctions uF) {
		
		String time = uF.timeNow(CF.getStrTimeZone());
		
		
		if(getStrBooking_from() == null || getStrBooking_from().equals("")) {
			
			setStrBooking_from(uF.showData(getBookingDate(),""));
		}
		
		if(getStrBooking_to() == null || getStrBooking_to().equals("")) {
			setStrBooking_to(uF.showData(getBookingDate(),""));
		}

		if(getBookingDate() != null && !getBookingDate().equals("") && getBookingDate().length()>0){
			
			/*if(getStrFrom_time() == null || getStrFrom_time().equals("")) {
				setStrFrom_time(time.substring(0,time.lastIndexOf(":")));
			}
			
			if(getStrTo_time() == null || getStrTo_time().equals("")) {
				setStrTo_time(time.substring(0,time.lastIndexOf(":")));
				
			}*/
		}
		
		mealTypeList = new FillMealType().fillMealType();
		dishList = new ArrayList<FillDishes>();
		if(strUserType != null && strUserType.equals(HRMANAGER)) {
			if((String)session.getAttribute(WLOCATION_ACCESS) != null && !session.getAttribute(WLOCATION_ACCESS).equals("")) {
				
				participantsList = new FillEmployee(request).fillCafeEmployees(strUserType,(String)session.getAttribute(WLOCATION_ACCESS));
				meetingRoomsList = new FillMeetingRooms(request).fillMeetingRooms(strUserType,(String)session.getAttribute(WLOCATION_ACCESS));
				/*if(getMealType() != null && !getMealType().equals("")) {
					dishList = new FillDishes().fillDishes(getMealType(), getStrBooking_from(), getStrBooking_to(), (String)session.getAttribute(WLOCATION_ACCESS),"");
				}*/
				
			} else {
				
				participantsList = new FillEmployee(request).fillCafeEmployees(strUserType,(String)session.getAttribute(WLOCATIONID));
				meetingRoomsList = new FillMeetingRooms(request).fillMeetingRooms(strUserType,(String)session.getAttribute(WLOCATIONID));
				/*if(getMealType() != null && !getMealType().equals("")) {
					dishList = new FillDishes().fillDishes(getMealType(), getStrBooking_from(), getStrBooking_to(), (String)session.getAttribute(WLOCATIONID),"");
				}*/
			}
		}else if(strUserType != null && strUserType.equals(ADMIN)) {
			
			participantsList = new FillEmployee(request).fillCafeEmployees(strUserType,"");
			meetingRoomsList = new FillMeetingRooms().fillMeetingRooms(strUserType,"");
			/*if(getMealType() != null && !getMealType().equals("")) {
				dishList = new FillDishes().fillDishes(getMealType(), getStrBooking_from(), getStrBooking_to(),"","");
			}*/
		}else {
			
			participantsList = new FillEmployee(request).fillCafeEmployees(strUserType,(String)session.getAttribute(WLOCATIONID));
			meetingRoomsList = new FillMeetingRooms(request).fillMeetingRooms(strUserType,(String)session.getAttribute(WLOCATIONID));
			/*if(getMealType() != null && !getMealType().equals("")) {
				dishList = new FillDishes().fillDishes(getMealType(), getStrBooking_from(), getStrBooking_to(), (String)session.getAttribute(WLOCATIONID),"");
			}*/
		
		}
	}
	
	private void bookMeetingRooms(UtilityFunctions uF) {
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		try {
			
			con = db.makeConnection(con);
			Map<String, String> hmUserTypeId = CF.getUserTypeIdMap(con);
			
			StringBuilder participants = null;
			if(getStrParticipants() != null && getStrParticipants().length>0 ){
				for(String emp :getStrParticipants()) {
					if(participants == null) {
						participants = new StringBuilder();
						participants.append(","+emp+",");
					} else {
						participants.append(emp+",");
					}
				}
			}
			
			if(participants == null) {
				participants = new StringBuilder();
			}
			
			StringBuilder guests = null;
			StringBuilder sbGuests = null;
			String[] strGuest = request.getParameterValues("strGuest");
			if(strGuest != null && strGuest.length>0 ){
				for(String guest :strGuest) {
					if(guests == null) {
						guests = new StringBuilder();
						guests.append(","+guest+",");
					} else {
						guests.append(guest+",");
					}
					
					if(sbGuests == null) {
						sbGuests = new StringBuilder();
						sbGuests.append(guest);
					} else {
						sbGuests.append("," + guest);
					}
				}
			}
			
			if(guests == null) {
				guests = new StringBuilder();
			}
			
			if(sbGuests == null) {
				sbGuests = new StringBuilder();
			}
			
			pst = con.prepareStatement("insert into meeting_room_booking_details(meeting_room_id,booking_from,booking_to,from_time,"
					+"to_time,booked_by,request_date,participants,request_status,no_of_people,food_service_details,meeting_room_location,guests,booking_request_comment,is_food_required,food_dish_types)"
					+" values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
			pst.setInt(1, uF.parseToInt(getStrMeetingRoomId()));
			pst.setDate(2, uF.getDateFormat(getStrBooking_from(),DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(getStrBooking_to(),DATE_FORMAT));
			pst.setTime(4, uF.getTimeFormat(getStrFrom_time(), TIME_FORMAT));
			pst.setTime(5, uF.getTimeFormat(getStrTo_time(), TIME_FORMAT));
			pst.setInt(6, uF.parseToInt(strSessionEmpId));
			pst.setDate(7,uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setString(8,participants.toString());
			pst.setInt(9,0);
			pst.setInt(10,uF.parseToInt(getStrNoOfParticipants()));
			pst.setInt(11,uF.parseToInt(getStrDishIds()));
			pst.setInt(12,uF.parseToInt(getLocation()));
			
			pst.setString(13,guests.toString());
			pst.setString(14,getStrBookingComment());
			pst.setBoolean(15,uF.parseToBoolean(getIsFoodRequired()));
			pst.setString(16,getMealType());
//			System.out.println("pst==>"+pst);
			pst.execute();
			pst.close();
			
			String maxBookingId = "";
			pst = con.prepareStatement("select max(booking_id) as bookingCnt from meeting_room_booking_details");
//			System.out.println("pst1==>"+pst);
			rs= pst.executeQuery();
			while(rs.next()) {
				maxBookingId = rs.getString("bookingCnt");
			}
			rs.close();
			pst.close();
			
			
			pst=con.prepareStatement("select emp_per_id from employee_official_details eod,user_details ud, employee_personal_details epd where epd.emp_per_id=eod.emp_id and ud.emp_id=eod.emp_id and is_alive = true and ud.usertype_id=?");
			pst.setInt(1, uF.parseToInt(hmUserTypeId.get(ADMIN)));
//			System.out.println("pst2==>"+pst);
			rs=pst.executeQuery();
			List<String> empList = new ArrayList<String>();
			while(rs.next()) {
				if(!empList.contains(rs.getString("emp_per_id").trim())) {
					empList.add(rs.getString("emp_per_id").trim());	
				}
			}
			rs.close();
			pst.close();
			
			String strDomain = request.getServerName().split("\\.")[0];
			
			if(getStrDishIds() != null && !getStrDishIds().equals("")) {
			    pst = con.prepareStatement("insert into dish_order_details (dish_id, emp_id, dish_quantity, order_status, order_date,booking_id,guest_names) values (?,?,?,?,?,?,?)");
				pst.setInt(1, uF.parseToInt(getStrDishIds()));
				pst.setInt(2,uF.parseToInt(strSessionEmpId));
				pst.setInt(3, uF.parseToInt(getStrNoOfParticipants()));
				pst.setInt(4, 0);
				pst.setDate(5,uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setInt(6,uF.parseToInt(maxBookingId));
				pst.setString(7,sbGuests.toString());
				pst.execute();
				pst.close();
					
					
				for(int i=0; empList!= null && !empList.isEmpty() && i<empList.size(); i++) {
					if(!empList.get(i).equals("") && uF.parseToInt(empList.get(i)) > 0) {
						String alertData = "<div style=\"float: left;\"> A new Cafeteria Request from <b>"+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>. </div>";
						String alertAction = "Cafeteria.action?pType=WR";
						
						UserAlerts userAlerts = new UserAlerts(con, uF, CF, request);
						userAlerts.setStrDomain(strDomain);
						userAlerts.setStrEmpId(empList.get(i));
						userAlerts.setStrData(alertData);
						userAlerts.setStrAction(alertAction);
						userAlerts.setCurrUserTypeID(hmUserTypeId.get(ADMIN));
						userAlerts.setStatus(INSERT_WR_ALERT);
						Thread t = new Thread(userAlerts);
						t.run();
						
//						UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
//						userAlerts.setStrDomain(strDomain);
//						userAlerts.setStrEmpId(empList.get(i));
//						userAlerts.set_type(FOOD_REQUEST_ALERT);
//						userAlerts.setStatus(INSERT_ALERT);
//						Thread t = new Thread(userAlerts);
//						t.run();
					}
				}

			}
			
			
			for(int i=0; empList!= null && !empList.isEmpty() && i<empList.size(); i++) {
				if(!empList.get(i).equals("") && uF.parseToInt(empList.get(i)) > 0) {
					
					String alertData = "<div style=\"float: left;\"> A new Meeting Room Booking Request from <b>"+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b> date "+uF.getDateFormat(getStrBooking_from(), DATE_FORMAT, CF.getStrReportDateFormat())+" to "+uF.getDateFormat(getStrBooking_to(), DATE_FORMAT, CF.getStrReportDateFormat())+". </div>";
					String alertAction = "MeetingRooms.action?pType=WR&dataType=MBR";
					
					UserAlerts userAlerts = new UserAlerts(con, uF, CF, request);
					userAlerts.setStrDomain(strDomain);
					userAlerts.setStrEmpId(empList.get(i));
					userAlerts.setStrData(alertData);
					userAlerts.setStrAction(alertAction);
					userAlerts.setCurrUserTypeID(hmUserTypeId.get(ADMIN));
					userAlerts.setStatus(INSERT_WR_ALERT);
					Thread t = new Thread(userAlerts);
					t.run();
					
//					UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
//					userAlerts.setStrDomain(strDomain);
//					userAlerts.setStrEmpId(empList.get(i));
//					userAlerts.set_type(MEETING_ROOM_BOOKING_REQUEST_ALERT);
//					userAlerts.setStatus(INSERT_ALERT);
//					Thread t = new Thread(userAlerts);
//					t.run();
				}
			}
		
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
		
	}
	
	public HttpServletRequest request;
	 
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public String getStrSessionOrgId() {
		return strSessionOrgId;
	}

	public void setStrSessionOrgId(String strSessionOrgId) {
		this.strSessionOrgId = strSessionOrgId;
	}

	public String getStrSessionEmpId() {
		return strSessionEmpId;
	}

	public void setStrSessionEmpId(String strSessionEmpId) {
		this.strSessionEmpId = strSessionEmpId;
	}

	

	public String getStrBooking_from() {
		return strBooking_from;
	}

	public void setStrBooking_from(String strBooking_from) {
		this.strBooking_from = strBooking_from;
	}

	public String getStrBooking_to() {
		return strBooking_to;
	}

	public void setStrBooking_to(String strBooking_to) {
		this.strBooking_to = strBooking_to;
	}

	public String getStrFrom_time() {
		return strFrom_time;
	}

	public void setStrFrom_time(String strFrom_time) {
		this.strFrom_time = strFrom_time;
	}

	public String getStrTo_time() {
		return strTo_time;
	}

	public void setStrTo_time(String strTo_time) {
		this.strTo_time = strTo_time;
	}

	public String[] getStrParticipants() {
		return strParticipants;
	}

	public void setStrParticipants(String[] strParticipants) {
		this.strParticipants = strParticipants;
	}

	public String getStrFoodServiceId() {
		return strFoodServiceId;
	}

	public void setStrFoodServiceId(String strFoodServiceId) {
		this.strFoodServiceId = strFoodServiceId;
	}

	public String getStrFoodServiceDetails() {
		return strFoodServiceDetails;
	}

	public void setStrFoodServiceDetails(String strFoodServiceDetails) {
		this.strFoodServiceDetails = strFoodServiceDetails;
	}

	public String getStrLocation() {
		return strLocation;
	}

	public void setStrLocation(String strLocation) {
		this.strLocation = strLocation;
	}

	public List<FillEmployee> getParticipantsList() {
		return participantsList;
	}

	public void setParticipantsList(List<FillEmployee> participantsList) {
		this.participantsList = participantsList;
	}

	public String getBookingDate() {
		return bookingDate;
	}

	public void setBookingDate(String bookingDate) {
		this.bookingDate = bookingDate;
	}

	public String getStrMeetingRoomId() {
		return strMeetingRoomId;
	}

	public void setStrMeetingRoomId(String strMeetingRoomId) {
		this.strMeetingRoomId = strMeetingRoomId;
	}

	public List<FillMeetingRooms> getMeetingRoomsList() {
		return meetingRoomsList;
	}

	public void setMeetingRoomsList(List<FillMeetingRooms> meetingRoomsList) {
		this.meetingRoomsList = meetingRoomsList;
	}

	public String getStrBookingComment() {
		return strBookingComment;
	}

	public void setStrBookingComment(String strBookingComment) {
		this.strBookingComment = strBookingComment;
	}

	public String getIsFoodRequired() {
		return isFoodRequired;
	}

	public void setIsFoodRequired(String isFoodRequired) {
		this.isFoodRequired = isFoodRequired;
	}

	public String getStrNoOfParticipants() {
		return strNoOfParticipants;
	}

	public void setStrNoOfParticipants(String strNoOfParticipants) {
		this.strNoOfParticipants = strNoOfParticipants;
	}

	public String getStrFoodServiceLocation() {
		return strFoodServiceLocation;
	}

	public void setStrFoodServiceLocation(String strFoodServiceLocation) {
		this.strFoodServiceLocation = strFoodServiceLocation;
	}

	public String getStrSubmit() {
		return strSubmit;
	}

	public void setStrSubmit(String strSubmit) {
		this.strSubmit = strSubmit;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getBookingId() {
		return bookingId;
	}

	public void setBookingId(String bookingId) {
		this.bookingId = bookingId;
	}

	public String getStrUpdate() {
		return strUpdate;
	}

	public void setStrUpdate(String strUpdate) {
		this.strUpdate = strUpdate;
	}


	public String getIsFoodServiceRequired() {
		return isFoodServiceRequired;
	}


	public void setIsFoodServiceRequired(String isFoodServiceRequired) {
		this.isFoodServiceRequired = isFoodServiceRequired;
	}


	public String getStrEmpIds() {
		return strEmpIds;
	}


	public void setStrEmpIds(String strEmpIds) {
		this.strEmpIds = strEmpIds;
	}


	public String getLocation() {
		return location;
	}


	public void setLocation(String location) {
		this.location = location;
	}


	public String getMealType() {
		return mealType;
	}


	public void setMealType(String mealType) {
		this.mealType = mealType;
	}


	public List<FillMealType> getMealTypeList() {
		return mealTypeList;
	}


	public void setMealTypeList(List<FillMealType> mealTypeList) {
		this.mealTypeList = mealTypeList;
	}


	public List<FillDishes> getDishList() {
		return dishList;
	}


	public void setDishList(List<FillDishes> dishList) {
		this.dishList = dishList;
	}


	public String getStrDishIds() {
		return strDishIds;
	}


	public void setStrDishIds(String strDishIds) {
		this.strDishIds = strDishIds;
	}


	public String getMeetingRoomId() {
		return meetingRoomId;
	}


	public void setMeetingRoomId(String meetingRoomId) {
		this.meetingRoomId = meetingRoomId;
	}


	public String getStrCapacity() {
		return strCapacity;
	}


	public void setStrCapacity(String strCapacity) {
		this.strCapacity = strCapacity;
	}


	public String getStrCancel() {
		return strCancel;
	}


	public void setStrCancel(String strCancel) {
		this.strCancel = strCancel;
	}
	
	
}
