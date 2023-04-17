package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class EditEmpRateAndCost extends ActionSupport implements IStatements, ServletRequestAware {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	String proID;
	String empID; 
	String type;
	String submitType;
	String billingType;
	String empRatePerHour;
	String empRatePerDay;
	String empRatePerMonth;
	String empActualRatePerHour;
	String empActualRatePerDay;
	String empActualRatePerMonth;
	String empRateOverheadsLbl;
	String empRateOverheadsAmt;
	String empActualRateOverheadsLbl;
	String empActualRateOverheadsAmt;
	String strProCurrId;
	String strShortCurrency;
	String submit;
	
	String allocationPercent;
	String isBilledUnbilled;
	String proResReqId;
	String resGap;
	
	private String allocationDate;
	private String releaseDate;
	
	public String execute() {
		
		HttpSession session = request.getSession();
		
		CommonFunctions CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		UtilityFunctions uF = new UtilityFunctions();

//		System.out.println("getSubmit() ===>> " + getSubmit());
		if(getSubmitType()!=null && getSubmitType().equals("DELETESRR")) {
			deleteSkillResReq(uF);
			return "ajax";
		}
		if(getSubmitType()!=null && getSubmitType().equals("UpdateSRRGAP")) {
			updateSkillResReqGap(uF);
			return "ajax";
		}
		if(getSubmitType()!=null && getSubmitType().equals("ALLOCATION")) {
			insertUpdateAllocationPercent(uF);
			return "ajax";
		} else if(getSubmitType()!=null && getSubmitType().equals("DATEALLOCATION")) {
			updateAllocationAndReleaseDate(uF);
			return "ajax";
		} else if(getSubmitType()!=null && getSubmitType().equals("BILLED")) {
			insertUpdateBilledUnbilled(uF);
			return "ajax";
		}
		if(getSubmit() != null) {
			insertData(uF);
			return SUCCESS;
		} else {
			getData(CF, uF);
			return LOAD;
		}
	}
	
	
	private void updateAllocationAndReleaseDate(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			if((getAllocationDate()!=null && !getAllocationDate().equals("")) || (getReleaseDate()!=null && !getReleaseDate().equals(""))) {
				con = db.makeConnection(con);
				StringBuilder sbQuery = new StringBuilder();
				sbQuery.append("update project_emp_details set ");
				if(getAllocationDate()!=null && !getAllocationDate().equals("")) {
					sbQuery.append(" allocation_date=? ");
				} else if(getReleaseDate()!=null && !getReleaseDate().equals("")) {
					sbQuery.append(" release_date=? ");
				}
				sbQuery.append(" where pro_id=? and emp_id=?");
				pst = con.prepareStatement(sbQuery.toString());
				if(getAllocationDate()!=null) {
					pst.setDate(1, uF.getDateFormat(getAllocationDate(), DATE_FORMAT));	
				} else if(getReleaseDate()!=null) {
					pst.setDate(1, uF.getDateFormat(getReleaseDate(), DATE_FORMAT));
				}
				pst.setInt(2, uF.parseToInt(getProID()));
				pst.setInt(3, uF.parseToInt(getEmpID()));
				int x = pst.executeUpdate();
				pst.close();
	//			System.out.println("pst ===>> " + pst);
				if(x==1) {
					if(getAllocationDate()!=null && !getAllocationDate().equals("")) {
						request.setAttribute("STATUS_MSG", uF.showData(getAllocationDate(), "-"));
					} else if(getReleaseDate()!=null && !getReleaseDate().equals("")) {
						request.setAttribute("STATUS_MSG", uF.showData(getReleaseDate(), "-"));
					}
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}


	private void updateSkillResReqGap(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("update project_resource_req_details set resource_gap=? where project_resource_req_id=?");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDouble(1, uF.parseToDouble(getResGap()));
			pst.setInt(2, uF.parseToInt(getProResReqId()));
			pst.executeUpdate();
			pst.close();
//			System.out.println("pst ===>> " + pst);
			request.setAttribute("STATUS_MSG", "Updated!");
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}


	private void deleteSkillResReq(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("delete from project_resource_req_details where project_resource_req_id=?");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(getProResReqId()));
			pst.executeUpdate();
			pst.close();
//			System.out.println("pst ===>> " + pst);
			request.setAttribute("STATUS_MSG", "Deleted!");
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}


	private void insertUpdateAllocationPercent(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("update project_emp_details set allocation_percent=? where pro_id=? and emp_id=?");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDouble(1, uF.parseToDouble(getAllocationPercent()));
			pst.setInt(2, uF.parseToInt(getProID()));
			pst.setInt(3, uF.parseToInt(getEmpID()));
			pst.executeUpdate();
			pst.close();
//			System.out.println("pst ===>> " + pst);
			request.setAttribute("STATUS_MSG", uF.showData(getAllocationPercent(), "0"));
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}


	private void insertUpdateBilledUnbilled(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("update project_emp_details set is_billed=? where pro_id=? and emp_id=?");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setBoolean(1, uF.parseToBoolean(getIsBilledUnbilled()));
			pst.setInt(2, uF.parseToInt(getProID()));
			pst.setInt(3, uF.parseToInt(getEmpID()));
			pst.executeUpdate();
			pst.close();
//			System.out.println("pst ===>> " + pst);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	private void insertData(UtilityFunctions uF) {
		
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			
			con = db.makeConnection(con);

			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("update project_emp_details set ");
			if(type != null && type.equals("rate") && billingType != null && billingType.equals("H")) {
				sbQuery.append(" emp_rate_per_hour = "+uF.parseToDouble(getEmpRatePerHour())+" ");
			}
			if(type != null && type.equals("rate") && billingType != null && billingType.equals("D")) {
				sbQuery.append(" emp_rate_per_day = "+uF.parseToDouble(getEmpRatePerDay())+" ");
			}
			if(type != null && type.equals("rate") && billingType != null && billingType.equals("M")) {
				sbQuery.append(" emp_rate_per_month = "+uF.parseToDouble(getEmpRatePerMonth())+" ");
			}
			if(type != null && type.equals("cost") && billingType != null && billingType.equals("H")) {
				sbQuery.append(" emp_actual_rate_per_hour = "+uF.parseToDouble(getEmpActualRatePerHour())+" ");
			}
			if(type != null && type.equals("cost") && billingType != null && billingType.equals("D")) {
				sbQuery.append(" emp_actual_rate_per_day = "+uF.parseToDouble(getEmpActualRatePerDay())+" ");
			}
			if(type != null && type.equals("cost") && billingType != null && billingType.equals("M")) {
				sbQuery.append(" emp_actual_rate_per_month = "+uF.parseToDouble(getEmpActualRatePerMonth())+" ");
			}
			sbQuery.append(" where pro_id=? and emp_id=?");
			
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(getProID()));
			pst.setInt(2, uF.parseToInt(getEmpID()));
			pst.executeUpdate();
			pst.close();
//			System.out.println("pst ===>> " + pst);

//			StringBuilder sbQuery1 = new StringBuilder();
//			sbQuery1.append("update variable_cost set variable_name = ?, variable_cost=? where pro_id=? and emp_id=? and amount_type =?");
//			pst = con.prepareStatement(sbQuery1.toString());
//			if(type != null && type.equals("rate")) {
//				pst.setString(1, getEmpRateOverheadsLbl());
//				pst.setDouble(2, uF.parseToDouble(getEmpRateOverheadsAmt()));
//			} else if(type != null && type.equals("cost")) {
//				pst.setString(1, getEmpActualRateOverheadsLbl());
//				pst.setDouble(2, uF.parseToDouble(getEmpActualRateOverheadsAmt()));
//			}
//			pst.setInt(3, uF.parseToInt(getProID()));
//			pst.setInt(4, uF.parseToInt(getEmpID()));
//			pst.setString(5, getType());
//			int cnt = pst.executeUpdate();
//			
//			if(cnt == 0) {
//				StringBuilder sbQueryy = new StringBuilder();
//				sbQueryy.append("insert into variable_cost (variable_name, variable_cost, pro_id, emp_id, amount_type) values (?,?,?,?,?)");
//				pst = con.prepareStatement(sbQueryy.toString());
//				if(type != null && type.equals("rate")) {
//					pst.setString(1, getEmpRateOverheadsLbl());
//					pst.setDouble(2, uF.parseToDouble(getEmpRateOverheadsAmt()));
//				} else if(type != null && type.equals("cost")) {
//					pst.setString(1, getEmpActualRateOverheadsLbl());
//					pst.setDouble(2, uF.parseToDouble(getEmpActualRateOverheadsAmt()));
//				}
//				pst.setInt(3, uF.parseToInt(getProID()));
//				pst.setInt(4, uF.parseToInt(getEmpID()));
//				pst.setString(5, getType());
//				pst.executeUpdate();
//			}
//			System.out.println("pst ===>> " + pst);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	
	private void getData(CommonFunctions CF, UtilityFunctions uF) {
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			
			con = db.makeConnection(con);

			Map<String, Map<String, String>> hmCurrData = CF.getCurrencyDetails(con);
			Map<String, String> hmCurrInner = hmCurrData.get(strProCurrId);
			if(hmCurrInner == null) hmCurrInner = new HashMap<String, String>();
			
			setStrShortCurrency(hmCurrInner.get("SHORT_CURR"));
			
			pst = con.prepareStatement("select * from project_emp_details where pro_id=? and emp_id=?");
			pst.setInt(1, uF.parseToInt(getProID()));
			pst.setInt(2, uF.parseToInt(getEmpID()));
			rs = pst.executeQuery();
//			System.out.println("pst ===>> " + pst);
			
			while (rs.next()) {
				setEmpRatePerHour(uF.showData(rs.getString("emp_rate_per_hour"), "0"));
				setEmpRatePerDay(uF.showData(rs.getString("emp_rate_per_day"), "0"));
				setEmpRatePerMonth(uF.showData(rs.getString("emp_rate_per_month"), "0"));
				setEmpActualRatePerHour(uF.showData(rs.getString("emp_actual_rate_per_hour"), "0"));
				setEmpActualRatePerDay(uF.showData(rs.getString("emp_actual_rate_per_day"), "0"));
				setEmpActualRatePerMonth(uF.showData(rs.getString("emp_actual_rate_per_month"), "0"));
			}
			rs.close();
			pst.close();

			/*pst = con.prepareStatement("select * from variable_cost where pro_id=? and emp_id=?");
			pst.setInt(1, uF.parseToInt(getProID()));
			pst.setInt(2, uF.parseToInt(getEmpID()));
			rs = pst.executeQuery();
			System.out.println("pst ===>> " + pst);
			
			while (rs.next()) {
				if(rs.getString("amount_type")!= null && rs.getString("amount_type").equals("rate")) {
					setEmpRateOverheadsLbl(rs.getString("variable_name"));
					setEmpRateOverheadsAmt(rs.getString("variable_cost"));
				} else if(rs.getString("amount_type")!= null && rs.getString("amount_type").equals("cost")) {
					setEmpActualRateOverheadsLbl(rs.getString("variable_name"));
					setEmpActualRateOverheadsAmt(rs.getString("variable_cost"));
				}
			}*/
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}


	HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public String getProID() {
		return proID;
	}

	public void setProID(String proID) {
		this.proID = proID;
	}

	public String getEmpID() {
		return empID;
	}

	public void setEmpID(String empID) {
		this.empID = empID;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getEmpRatePerHour() {
		return empRatePerHour;
	}

	public void setEmpRatePerHour(String empRatePerHour) {
		this.empRatePerHour = empRatePerHour;
	}

	public String getEmpRatePerDay() {
		return empRatePerDay;
	}

	public void setEmpRatePerDay(String empRatePerDay) {
		this.empRatePerDay = empRatePerDay;
	}

	public String getEmpActualRatePerHour() {
		return empActualRatePerHour;
	}

	public void setEmpActualRatePerHour(String empActualRatePerHour) {
		this.empActualRatePerHour = empActualRatePerHour;
	}

	public String getEmpActualRatePerDay() {
		return empActualRatePerDay;
	}

	public void setEmpActualRatePerDay(String empActualRatePerDay) {
		this.empActualRatePerDay = empActualRatePerDay;
	}

	public String getEmpRatePerMonth() {
		return empRatePerMonth;
	}

	public void setEmpRatePerMonth(String empRatePerMonth) {
		this.empRatePerMonth = empRatePerMonth;
	}

	public String getEmpActualRatePerMonth() {
		return empActualRatePerMonth;
	}

	public void setEmpActualRatePerMonth(String empActualRatePerMonth) {
		this.empActualRatePerMonth = empActualRatePerMonth;
	}

	public String getBillingType() {
		return billingType;
	}

	public void setBillingType(String billingType) {
		this.billingType = billingType;
	}

	public String getEmpRateOverheadsLbl() {
		return empRateOverheadsLbl;
	}

	public void setEmpRateOverheadsLbl(String empRateOverheadsLbl) {
		this.empRateOverheadsLbl = empRateOverheadsLbl;
	}

	public String getEmpRateOverheadsAmt() {
		return empRateOverheadsAmt;
	}

	public void setEmpRateOverheadsAmt(String empRateOverheadsAmt) {
		this.empRateOverheadsAmt = empRateOverheadsAmt;
	}

	public String getEmpActualRateOverheadsLbl() {
		return empActualRateOverheadsLbl;
	}

	public void setEmpActualRateOverheadsLbl(String empActualRateOverheadsLbl) {
		this.empActualRateOverheadsLbl = empActualRateOverheadsLbl;
	}

	public String getEmpActualRateOverheadsAmt() {
		return empActualRateOverheadsAmt;
	}

	public void setEmpActualRateOverheadsAmt(String empActualRateOverheadsAmt) {
		this.empActualRateOverheadsAmt = empActualRateOverheadsAmt;
	}

	public String getStrProCurrId() {
		return strProCurrId;
	}

	public void setStrProCurrId(String strProCurrId) {
		this.strProCurrId = strProCurrId;
	}

	public String getStrShortCurrency() {
		return strShortCurrency;
	}

	public void setStrShortCurrency(String strShortCurrency) {
		this.strShortCurrency = strShortCurrency;
	}

	public String getSubmit() {
		return submit;
	}

	public void setSubmit(String submit) {
		this.submit = submit;
	}

	public String getSubmitType() {
		return submitType;
	}

	public void setSubmitType(String submitType) {
		this.submitType = submitType;
	}

	public String getAllocationPercent() {
		return allocationPercent;
	}

	public void setAllocationPercent(String allocationPercent) {
		this.allocationPercent = allocationPercent;
	}

	public String getIsBilledUnbilled() {
		return isBilledUnbilled;
	}

	public void setIsBilledUnbilled(String isBilledUnbilled) {
		this.isBilledUnbilled = isBilledUnbilled;
	}

	public String getProResReqId() {
		return proResReqId;
	}

	public void setProResReqId(String proResReqId) {
		this.proResReqId = proResReqId;
	}

	public String getResGap() {
		return resGap;
	}

	public void setResGap(String resGap) {
		this.resGap = resGap;
	}

	public String getAllocationDate() {
		return allocationDate;
	}

	public void setAllocationDate(String allocationDate) {
		this.allocationDate = allocationDate;
	}

	public String getReleaseDate() {
		return releaseDate;
	}

	public void setReleaseDate(String releaseDate) {
		this.releaseDate = releaseDate;
	}

}

