package com.konnect.jpms.master;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillLodgingType;
import com.konnect.jpms.select.FillRimbursementType;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class AddClaimReimbursement extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;

	HttpSession session;
	CommonFunctions CF;
	String strUserType;  
	String strSessionEmpId; 
	
	String operation;
	String strOrg;
	String strLevel;
	
	List<FillLodgingType> lodgingTypeList;	
	List<FillRimbursementType> localConveyanceTranTypeList;	
	
	String reimbPolicyId;
	String transportType;
	String trainType;
	String busType;
	String flightType;
	String carType;
	String travelLimitType;
	String travelLimit;
	String lodgingType;
	String lodgingLimitType;
	String lodgingLimit;
	String localConveyanceTranType;
	String localConveyanceLimit;
	String foodLimitType;
	String foodLimit;
	String laundryLimitType;
	String laundryLimit;
	String sundryLimitType;
	String sundryLimit;
	
	String userscreen;
	String navigationId;
	String toPage;
	
	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null) return LOGIN;
		
		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId =  (String)session.getAttribute(EMPID);		
		
		UtilityFunctions uF = new UtilityFunctions();
		
		String submit = (String) request.getParameter("submit");
		if(getOperation()!=null && getOperation().trim().equalsIgnoreCase("D")){
			deleteReimbursementPolicy(uF);
			return SUCCESS;
		} else if(getOperation()!=null && getOperation().trim().equalsIgnoreCase("A")){
			if(submit!=null){
				insertReimbursementPolicy(uF);
				return SUCCESS;
			}
		} else if(uF.parseToInt(getReimbPolicyId())>0 && getOperation()!=null && getOperation().trim().equalsIgnoreCase("E")){
			setOperation("U");
			viewReimbursementPolicy(uF);
			return loadReimbursementPolicy(uF);
		} else if(uF.parseToInt(getReimbPolicyId())>0 && getOperation()!=null && getOperation().trim().equalsIgnoreCase("U")){
			updateReimbursementPolicy(uF);
			return SUCCESS;
		}
	
		return loadReimbursementPolicy(uF);
	}


	private String loadReimbursementPolicy(UtilityFunctions uF) {
		lodgingTypeList = new FillLodgingType().fillLodgingType();
		localConveyanceTranTypeList = new FillRimbursementType().fillmodeoftravel();
		return LOAD;
	}


	private void deleteReimbursementPolicy(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("delete from reimbursement_policy where reimbursement_policy_id=?");
			pst.setInt(1, uF.parseToInt(getReimbPolicyId()));
			int x = pst.executeUpdate();
			if(x > 0){
				session.setAttribute(MESSAGE, SUCCESSM+"You have successfully deleted claim reimbursements."+END);
			} else {
				session.setAttribute(MESSAGE, ERRORM+"Colud not delete claim reimbursements. Please,try again."+END);
			}
			

		} catch (Exception e) {
			session.setAttribute(MESSAGE, ERRORM+"Colud not delete claim reimbursements. Please,try again."+END);
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}


	private void updateReimbursementPolicy(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);

			int ntrainType = 0;
			int nBusType = 0;
			int nFlightType = 0;
			int nCarType = 0;
			if(uF.parseToInt(getTransportType()) == 1){
				ntrainType = uF.parseToInt(getTrainType());
				nBusType = 0;
				nFlightType = 0;
				nCarType = 0;
			} else if(uF.parseToInt(getTransportType()) == 2){
				ntrainType = 0;
				nBusType = uF.parseToInt(getBusType());
				nFlightType = 0;
				nCarType = 0;
			} else if(uF.parseToInt(getTransportType()) == 3){
				ntrainType = 0;
				nBusType = 0;
				nFlightType = uF.parseToInt(getFlightType());
				nCarType = 0;
			} else if(uF.parseToInt(getTransportType()) == 4){
				ntrainType = 0;
				nBusType = 0;
				nFlightType = 0;
				nCarType = uF.parseToInt(getCarType());
			} 
			
			int nLodgingLimitType = 0;
			double dblLodgingLimit = 0;
			if(uF.parseToInt(getLodgingType()) == 9){
				nLodgingLimitType = uF.parseToInt(getLodgingLimitType());
				if(uF.parseToInt(getLodgingLimitType()) == 2){
					dblLodgingLimit = uF.parseToDouble(getLodgingLimit());
				}
			}
			
			pst = con.prepareStatement("update reimbursement_policy set added_by=?,entry_date=?,travel_transport_type=?,train_type=?," +
					"bus_type=?,flight_type=?,car_type=?,travel_limit_type=?,travel_limit=?,lodging_type=?,lodging_limit_type=?," +
					"lodging_limit=?,local_conveyance_tran_type=?,local_conveyance_limit=?,food_limit_type=?,food_limit=?," +
					"laundry_limit_type=?,laundry_limit=?,sundry_limit_type=?,sundry_limit=? where reimbursement_policy_id=?");
			pst.setInt(1, uF.parseToInt(strSessionEmpId));
			pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(3, uF.parseToInt(getTransportType()));
			pst.setInt(4, ntrainType);
			pst.setInt(5, nBusType);
			pst.setInt(6, nFlightType);
			pst.setInt(7, nCarType);
			pst.setInt(8, uF.parseToInt(getTravelLimitType()));
			pst.setDouble(9, uF.parseToInt(getTravelLimitType()) == 2 ? uF.parseToDouble(getTravelLimit()) : 0.0d);
			pst.setInt(10, uF.parseToInt(getLodgingType()));
			pst.setInt(11, nLodgingLimitType);
			pst.setDouble(12, dblLodgingLimit);
			pst.setString(13, getLocalConveyanceTranType());
			pst.setDouble(14, uF.parseToDouble(getLocalConveyanceLimit()));
			pst.setInt(15, uF.parseToInt(getFoodLimitType()));
			pst.setDouble(16, uF.parseToInt(getFoodLimitType()) == 2 ? uF.parseToDouble(getFoodLimit()) : 0.0d);
			pst.setInt(17, uF.parseToInt(getLaundryLimitType()));
			pst.setDouble(18, uF.parseToInt(getLaundryLimitType()) == 2 ? uF.parseToDouble(getLaundryLimit()) : 0.0d);
			pst.setInt(19, uF.parseToInt(getSundryLimitType()));
			pst.setDouble(20, uF.parseToInt(getSundryLimitType()) == 2 ? uF.parseToDouble(getSundryLimit()) : 0.0d);
			pst.setInt(21, uF.parseToInt(getReimbPolicyId()));
//			System.out.println("pst====>"+pst);
			int x = pst.executeUpdate();
			if(x > 0){
				session.setAttribute(MESSAGE, SUCCESSM+"You have successfully updated claim reimbursements."+END);
			} else {
				session.setAttribute(MESSAGE, ERRORM+"Colud not update claim reimbursements. Please,try again."+END);
			}

		} catch (Exception e) {
			session.setAttribute(MESSAGE, ERRORM+"Colud not update claim reimbursements. Please,try again."+END);
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}


	private void viewReimbursementPolicy(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);

			pst = con.prepareStatement("select * from reimbursement_policy where reimbursement_policy_id=?");
			pst.setInt(1, uF.parseToInt(getReimbPolicyId()));
			rs = pst.executeQuery();
			while(rs.next()){
				setReimbPolicyId(rs.getString("reimbursement_policy_id"));
				setTransportType(rs.getString("travel_transport_type"));
				setTrainType(rs.getString("train_type"));
				setBusType(rs.getString("bus_type"));
				setFlightType(rs.getString("flight_type"));
				setCarType(rs.getString("car_type"));
				setTravelLimitType(rs.getString("travel_limit_type"));
				setTravelLimit(""+uF.parseToDouble(rs.getString("travel_limit")));
				setLodgingType(rs.getString("lodging_type"));
				setLodgingLimitType(rs.getString("lodging_limit_type"));
				setLodgingLimit(""+uF.parseToDouble(rs.getString("lodging_limit")));
				setLocalConveyanceTranType(rs.getString("local_conveyance_tran_type"));
				setLocalConveyanceLimit(""+uF.parseToDouble(rs.getString("local_conveyance_limit")));
				setFoodLimitType(rs.getString("food_limit_type"));
				setFoodLimit(""+uF.parseToDouble(rs.getString("food_limit")));
				setLaundryLimitType(rs.getString("laundry_limit_type"));
				setLaundryLimit(""+uF.parseToDouble(rs.getString("laundry_limit")));
				setSundryLimitType(rs.getString("sundry_limit_type"));
				setSundryLimit(""+uF.parseToDouble(rs.getString("sundry_limit")));
			}
			rs.close();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}


	private void insertReimbursementPolicy(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			
			int ntrainType = 0;
			int nBusType = 0;
			int nFlightType = 0;
			int nCarType = 0;
			if(uF.parseToInt(getTransportType()) == 1){
				ntrainType = uF.parseToInt(getTrainType());
				nBusType = 0;
				nFlightType = 0;
				nCarType = 0;
			} else if(uF.parseToInt(getTransportType()) == 2){
				ntrainType = 0;
				nBusType = uF.parseToInt(getBusType());
				nFlightType = 0;
				nCarType = 0;
			} else if(uF.parseToInt(getTransportType()) == 3){
				ntrainType = 0;
				nBusType = 0;
				nFlightType = uF.parseToInt(getFlightType());
				nCarType = 0;
			} else if(uF.parseToInt(getTransportType()) == 4){
				ntrainType = 0;
				nBusType = 0;
				nFlightType = 0;
				nCarType = uF.parseToInt(getCarType());
			} 
			
			int nLodgingLimitType = 0;
			double dblLodgingLimit = 0;
			if(uF.parseToInt(getLodgingType()) == 9){
				nLodgingLimitType = uF.parseToInt(getLodgingLimitType());
				if(uF.parseToInt(getLodgingLimitType()) == 2){
					dblLodgingLimit = uF.parseToDouble(getLodgingLimit());
				}
			}
			
			pst = con.prepareStatement("insert into reimbursement_policy(reimbursement_policy_type,is_default_policy,level_id,org_id,added_by," +
					"entry_date,travel_transport_type,train_type,bus_type,flight_type,car_type,travel_limit_type,travel_limit,lodging_type," +
					"lodging_limit_type,lodging_limit,local_conveyance_tran_type,local_conveyance_limit,food_limit_type,food_limit," +
					"laundry_limit_type,laundry_limit,sundry_limit_type,sundry_limit)" +
					"values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?)");
			pst.setInt(1, REIMBURSEMENTS_CLAIM);
			pst.setBoolean(2, false);
			pst.setInt(3, uF.parseToInt(getStrLevel()));
			pst.setInt(4, uF.parseToInt(getStrOrg()));
			pst.setInt(5, uF.parseToInt(strSessionEmpId));
			pst.setDate(6, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(7, uF.parseToInt(getTransportType()));
			pst.setInt(8, ntrainType);
			pst.setInt(9, nBusType);
			pst.setInt(10, nFlightType);
			pst.setInt(11, nCarType);
			pst.setInt(12, uF.parseToInt(getTravelLimitType()));
			pst.setDouble(13, uF.parseToInt(getTravelLimitType()) == 2 ? uF.parseToDouble(getTravelLimit()) : 0.0d);
			pst.setInt(14, uF.parseToInt(getLodgingType()));
			pst.setInt(15, nLodgingLimitType);
			pst.setDouble(16, dblLodgingLimit);
			pst.setString(17, getLocalConveyanceTranType());
			pst.setDouble(18, uF.parseToDouble(getLocalConveyanceLimit()));
			pst.setInt(19, uF.parseToInt(getFoodLimitType()));
			pst.setDouble(20, uF.parseToInt(getFoodLimitType()) == 2 ? uF.parseToDouble(getFoodLimit()) : 0.0d);
			pst.setInt(21, uF.parseToInt(getLaundryLimitType()));
			pst.setDouble(22, uF.parseToInt(getLaundryLimitType()) == 2 ? uF.parseToDouble(getLaundryLimit()) : 0.0d);
			pst.setInt(23, uF.parseToInt(getSundryLimitType()));
			pst.setDouble(24, uF.parseToInt(getSundryLimitType()) == 2 ? uF.parseToDouble(getSundryLimit()) : 0.0d);
			int x = pst.executeUpdate();
			if(x > 0){
				session.setAttribute(MESSAGE, SUCCESSM+"You have successfully inserted claim reimbursements."+END);
			} else {
				session.setAttribute(MESSAGE, ERRORM+"Colud not insert claim reimbursements. Please,try again."+END);
			}
			
		} catch (Exception e) {
			session.setAttribute(MESSAGE, ERRORM+"Colud not insert claim reimbursements. Please,try again."+END);
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


	public String getOperation() {
		return operation;
	}


	public void setOperation(String operation) {
		this.operation = operation;
	}


	public String getStrOrg() {
		return strOrg;
	}


	public void setStrOrg(String strOrg) {
		this.strOrg = strOrg;
	}


	public String getStrLevel() {
		return strLevel;
	}


	public void setStrLevel(String strLevel) {
		this.strLevel = strLevel;
	}


	public String getReimbPolicyId() {
		return reimbPolicyId;
	}


	public void setReimbPolicyId(String reimbPolicyId) {
		this.reimbPolicyId = reimbPolicyId;
	}


	public List<FillLodgingType> getLodgingTypeList() {
		return lodgingTypeList;
	}


	public void setLodgingTypeList(List<FillLodgingType> lodgingTypeList) {
		this.lodgingTypeList = lodgingTypeList;
	}


	public List<FillRimbursementType> getLocalConveyanceTranTypeList() {
		return localConveyanceTranTypeList;
	}


	public void setLocalConveyanceTranTypeList(List<FillRimbursementType> localConveyanceTranTypeList) {
		this.localConveyanceTranTypeList = localConveyanceTranTypeList;
	}


	public String getTransportType() {
		return transportType;
	}


	public void setTransportType(String transportType) {
		this.transportType = transportType;
	}


	public String getTrainType() {
		return trainType;
	}


	public void setTrainType(String trainType) {
		this.trainType = trainType;
	}


	public String getBusType() {
		return busType;
	}


	public void setBusType(String busType) {
		this.busType = busType;
	}


	public String getFlightType() {
		return flightType;
	}


	public void setFlightType(String flightType) {
		this.flightType = flightType;
	}


	public String getCarType() {
		return carType;
	}


	public void setCarType(String carType) {
		this.carType = carType;
	}


	public String getTravelLimitType() {
		return travelLimitType;
	}


	public void setTravelLimitType(String travelLimitType) {
		this.travelLimitType = travelLimitType;
	}


	public String getTravelLimit() {
		return travelLimit;
	}


	public void setTravelLimit(String travelLimit) {
		this.travelLimit = travelLimit;
	}


	public String getLodgingType() {
		return lodgingType;
	}


	public void setLodgingType(String lodgingType) {
		this.lodgingType = lodgingType;
	}


	public String getLodgingLimitType() {
		return lodgingLimitType;
	}


	public void setLodgingLimitType(String lodgingLimitType) {
		this.lodgingLimitType = lodgingLimitType;
	}


	public String getLodgingLimit() {
		return lodgingLimit;
	}


	public void setLodgingLimit(String lodgingLimit) {
		this.lodgingLimit = lodgingLimit;
	}


	public String getLocalConveyanceTranType() {
		return localConveyanceTranType;
	}


	public void setLocalConveyanceTranType(String localConveyanceTranType) {
		this.localConveyanceTranType = localConveyanceTranType;
	}


	public String getLocalConveyanceLimit() {
		return localConveyanceLimit;
	}


	public void setLocalConveyanceLimit(String localConveyanceLimit) {
		this.localConveyanceLimit = localConveyanceLimit;
	}


	public String getFoodLimitType() {
		return foodLimitType;
	}


	public void setFoodLimitType(String foodLimitType) {
		this.foodLimitType = foodLimitType;
	}


	public String getFoodLimit() {
		return foodLimit;
	}


	public void setFoodLimit(String foodLimit) {
		this.foodLimit = foodLimit;
	}


	public String getLaundryLimitType() {
		return laundryLimitType;
	}


	public void setLaundryLimitType(String laundryLimitType) {
		this.laundryLimitType = laundryLimitType;
	}


	public String getLaundryLimit() {
		return laundryLimit;
	}


	public void setLaundryLimit(String laundryLimit) {
		this.laundryLimit = laundryLimit;
	}


	public String getSundryLimitType() {
		return sundryLimitType;
	}


	public void setSundryLimitType(String sundryLimitType) {
		this.sundryLimitType = sundryLimitType;
	}


	public String getSundryLimit() {
		return sundryLimit;
	}


	public void setSundryLimit(String sundryLimit) {
		this.sundryLimit = sundryLimit;
	}


	public String getUserscreen() {
		return userscreen;
	}


	public void setUserscreen(String userscreen) {
		this.userscreen = userscreen;
	}


	public String getNavigationId() {
		return navigationId;
	}


	public void setNavigationId(String navigationId) {
		this.navigationId = navigationId;
	}


	public String getToPage() {
		return toPage;
	}


	public void setToPage(String toPage) {
		this.toPage = toPage;
	}
	
}