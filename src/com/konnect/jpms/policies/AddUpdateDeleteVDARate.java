package com.konnect.jpms.policies;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.payroll.ArrearPay;
import com.konnect.jpms.select.FillPayCycles;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class AddUpdateDeleteVDARate extends ActionSupport implements ServletRequestAware, IStatements, Runnable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	CommonFunctions CF;
	HttpSession session;
	String strOrgId;
	String strSessionEmpId;
	
	String userscreen;
	String navigationId;
	String toPage;
	String paycycle;
	List<FillPayCycles> paycycleList;
	String strPaycycleDuration;
	String operation;
	String strVdaRateId;
	String vdaRate;
	String strOrgName;
	String strPaycycleName;
	String strPaycycleStartDate;
	
	public String execute() throws Exception {
		
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		strSessionEmpId = (String) session.getAttribute(EMPID);
		
		if (getStrPaycycleDuration() == null || getStrPaycycleDuration().trim().equals("") || getStrPaycycleDuration().trim().equalsIgnoreCase("NULL")) {
			setStrPaycycleDuration("M");
		}
		loadPaySlipSetting();
		
		System.out.println("getOperation ============>> " + getOperation());
		if (getOperation()!=null && getOperation().equals("E")) {
			return viewVDARateDetails();
		} else if (getOperation()!=null && getOperation().equals("U")) {
			return updateVDARate();
		} else if (getOperation()!=null && getOperation().equals("D")) {
			return deleteVDARate();
		} else if (getOperation()!=null && getOperation().equals("A")) {
			return insertVDARate();
		} else if (getOperation()!=null && getOperation().equals("CHECKVDA")) {
			return getPrevVDARate();
		} else {
			setOperation("A");
		}
		return LOAD;
	}
	
	
	private String deleteVDARate() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs =null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from vda_rate_details where vda_rate_id=?");
			pst.setInt(1, uF.parseToInt(getStrVdaRateId()));
			rs = pst.executeQuery();
			Map<String, String> hmPaycycleData = new HashMap<String, String>();
			while (rs.next()) {
				hmPaycycleData.put("PAYCYCLE_ID", rs.getString("paycycle"));
				hmPaycycleData.put("FROM_DATE", rs.getString("from_date"));
				hmPaycycleData.put("TO_DATE", rs.getString("to_date"));
			}
			rs.close();
			pst.close();
//			System.out.println("hmPaycycleData ===>> " + hmPaycycleData);
			
			pst = con.prepareStatement("delete from vda_rate_details where vda_rate_id=?");
			pst.setInt(1, uF.parseToInt(getStrVdaRateId()));
//			System.out.println("pst ===>> " + pst);
			pst.executeUpdate();
			pst.close();
			
			pst = con.prepareStatement("delete from vda_rate_details where paycycle=? and from_date=? and to_date=?");
			pst.setInt(1, uF.parseToInt(hmPaycycleData.get("PAYCYCLE_ID")));
			pst.setDate(2, uF.getDateFormat(hmPaycycleData.get("FROM_DATE"), DBDATE));
			pst.setDate(3, uF.getDateFormat(hmPaycycleData.get("TO_DATE"), DBDATE));
//			System.out.println("pst ===>> " + pst);
			pst.executeUpdate();
			pst.close();
			
			session.setAttribute(MESSAGE, SUCCESSM+"VDA Rate deleted successfully."+END);
		} catch (Exception e) {
			e.printStackTrace();			
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;
	}

	
	private String getPrevVDARate() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			String[] strPaycycle = getPaycycle().split("-");
			pst = con.prepareStatement("select * from vda_rate_details where paycycle=? and from_date=? and to_date=?");
			pst.setInt(1, uF.parseToInt(strPaycycle[2]));
			pst.setDate(2, uF.getDateFormat(strPaycycle[0], DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strPaycycle[1], DATE_FORMAT));
			rs = pst.executeQuery();
			while(rs.next()) {
				String strVDARate = rs.getString("vda_rate");
				request.setAttribute("STATUS_MSG", strVDARate);
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
		return "ajax";
	}

	
	public String viewVDARateDetails() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from vda_rate_details where vda_rate_id=?");
			pst.setInt(1, uF.parseToInt(getStrVdaRateId()));
			rs = pst.executeQuery();
			while(rs.next()) {
				setOperation("U");
				String strPaycycle = rs.getString("paycycle");
				setPaycycle(uF.getDateFormat(rs.getString("from_date"), DBDATE, DATE_FORMAT)+"-"+uF.getDateFormat(rs.getString("to_date"), DBDATE, DATE_FORMAT) +"-"+rs.getString("paycycle"));
				String[] strPayCycleDate = CF.getPayCycleDatesOnPaycycleId(con, strPaycycle, getStrOrgId(), CF.getStrTimeZone(), CF, request);
				strPaycycleName = "Paycycle "+ strPayCycleDate[2] + ", "+strPayCycleDate[0]+" - "+strPayCycleDate[1];
				setVdaRate(rs.getString("vda_rate"));
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
		return LOAD;
	}
	
	
	public String updateVDARate() {
		Connection con = null;
		PreparedStatement pst = null;
		PreparedStatement pst1 = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			String[] strPaycycle = getPaycycle().split("-");
			boolean flag1 = false;
			boolean flag2 = false;
			pst = con.prepareStatement("select * from designation_details where level_id in (select level_id from level_details where org_id=?)");
			pst.setInt(1, uF.parseToInt(getStrOrgId()));
			rs = pst.executeQuery();
			Map<String, List<String>> hmDesigVDAIndexRates = new HashMap<String, List<String>>();
			while(rs.next()) {
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("vda_index_probation"));
				innerList.add(rs.getString("vda_index_permanent"));
				innerList.add(rs.getString("vda_index_temporary"));
				hmDesigVDAIndexRates.put(rs.getString("designation_id"), innerList);
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from vda_rate_details where paycycle=? and from_date=? and to_date=?");
			pst.setInt(1, uF.parseToInt(strPaycycle[2]));
			pst.setDate(2, uF.getDateFormat(strPaycycle[0], DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strPaycycle[1], DATE_FORMAT));
			rs = pst.executeQuery();
			List<String> alExistDesigIds = new ArrayList<String>();
			while(rs.next()) {
				if(rs.getString("desig_id") != null && rs.getInt("desig_id")>0) {
					alExistDesigIds.add(rs.getString("desig_id"));
				}
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("update vda_rate_details set vda_rate=? where vda_rate_id=?");
			pst.setDouble(1, uF.parseToDouble(getVdaRate()));
			pst.setInt(2, uF.parseToInt(getStrVdaRateId()));
			System.out.println("pst ===>> " + pst);
			pst.execute();
			pst.close();
			
			pst = con.prepareStatement("update vda_rate_details set vda_rate=?,vda_index_probation=?,vda_index_permanent=?,vda_index_temporary=?," +
				"vda_amount_probation=?,vda_amount_permanent=?,vda_amount_temporary=?,updated_by=?,update_date=? where desig_id=? and " +
				"paycycle=? and from_date=? and to_date=?");
			pst1 = con.prepareStatement("insert into vda_rate_details (org_id,paycycle,from_date,to_date,vda_rate,desig_id,vda_index_probation," +
				"vda_index_permanent,vda_index_temporary,vda_amount_probation,vda_amount_permanent,vda_amount_temporary,added_by,entry_date)" +
				"values (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?)");
			Iterator<String> it = hmDesigVDAIndexRates.keySet().iterator();
			while (it.hasNext()) {
				String strDesigId = it.next();
				List<String> innerList = hmDesigVDAIndexRates.get(strDesigId);
				double vdaAmtProbation = ((uF.parseToDouble(getVdaRate()) * VDA_FORMULA_FIX_VAL_1 * VDA_FORMULA_FIX_VAL_2) - VDA_FORMULA_FIX_VAL_3) * uF.parseToDouble(innerList.get(0));
				double vdaAmtPermanent = ((uF.parseToDouble(getVdaRate()) * VDA_FORMULA_FIX_VAL_1 * VDA_FORMULA_FIX_VAL_2) - VDA_FORMULA_FIX_VAL_3) * uF.parseToDouble(innerList.get(1));
				double vdaAmtTemporary = ((uF.parseToDouble(getVdaRate()) * VDA_FORMULA_FIX_VAL_1 * VDA_FORMULA_FIX_VAL_2) - VDA_FORMULA_FIX_VAL_3) * uF.parseToDouble(innerList.get(2));
				if(alExistDesigIds.contains(strDesigId)) {
					pst.setDouble(1, uF.parseToDouble(getVdaRate()));
					pst.setDouble(2, uF.parseToDouble(innerList.get(0)));
					pst.setDouble(3, uF.parseToDouble(innerList.get(1)));
					pst.setDouble(4, uF.parseToDouble(innerList.get(2)));
					pst.setDouble(5, vdaAmtProbation);
					pst.setDouble(6, vdaAmtPermanent);
					pst.setDouble(7, vdaAmtTemporary);
					pst.setInt(8, uF.parseToInt(strSessionEmpId));
					pst.setDate(9, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setInt(10, uF.parseToInt(strDesigId));
					pst.setInt(11, uF.parseToInt(strPaycycle[2]));
					pst.setDate(12, uF.getDateFormat(strPaycycle[0], DATE_FORMAT));
					pst.setDate(13, uF.getDateFormat(strPaycycle[1], DATE_FORMAT));
					pst.addBatch();
					flag2 = true;
				} else {
					pst1.setInt(1, uF.parseToInt(getStrOrgId()));
					pst1.setInt(2, uF.parseToInt(strPaycycle[2]));
					pst1.setDate(3, uF.getDateFormat(strPaycycle[0], DATE_FORMAT));
					pst1.setDate(4, uF.getDateFormat(strPaycycle[1], DATE_FORMAT));
					pst1.setDouble(5, uF.parseToDouble(getVdaRate()));
					pst1.setInt(6, uF.parseToInt(strDesigId));
					pst1.setDouble(7, uF.parseToDouble(innerList.get(0)));
					pst1.setDouble(8, uF.parseToDouble(innerList.get(1)));
					pst1.setDouble(9, uF.parseToDouble(innerList.get(2)));
					pst1.setDouble(10, vdaAmtProbation);
					pst1.setDouble(11, vdaAmtPermanent);
					pst1.setDouble(12, vdaAmtTemporary);
					pst1.setInt(13, uF.parseToInt(strSessionEmpId));
					pst1.setDate(14, uF.getCurrentDate(CF.getStrTimeZone()));
					pst1.addBatch();
					flag1 = true;
				}
			}
			if (flag2) {
				int[] x = pst.executeBatch();
				if (x.length > 0) {
					con.commit();
				} else {
					con.rollback();
					session.setAttribute(MESSAGE, ERRORM + "Colud not update VDA Rate. Please,try again." + END);
				}
			}
			if (flag1) {
				int[] x = pst1.executeBatch();
				if (x.length > 0) {
					con.commit();
				} else {
					con.rollback();
					session.setAttribute(MESSAGE, ERRORM + "Colud not update VDA Rate. Please,try again." + END);
				}
			}
				
			session.setAttribute(MESSAGE, SUCCESSM+"VDA Rate updated successfully."+END);
			
			System.out.println(" =====>> 1");
			setStrPaycycleStartDate(strPaycycle[0]);
			Thread th = new Thread(this);
			th.start();
//			updateAllEmpSalaryStruture(con, uF, strPaycycle[0]);
			
		} catch (Exception e) {
			e.printStackTrace();			
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;
	}
	
	
	public String insertVDARate() {

		Connection con = null;
		PreparedStatement pst = null;
		PreparedStatement pst1 = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			String[] strPaycycle = getPaycycle().split("-");
			boolean flag = false;
			boolean flag1 = false;
			boolean flag2 = false;
			pst = con.prepareStatement("select * from designation_details where level_id in (select level_id from level_details where org_id=?)");
			pst.setInt(1, uF.parseToInt(getStrOrgId()));
			rs = pst.executeQuery();
			Map<String, List<String>> hmDesigVDAIndexRates = new HashMap<String, List<String>>();
			while(rs.next()) {
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("vda_index_probation"));
				innerList.add(rs.getString("vda_index_permanent"));
				innerList.add(rs.getString("vda_index_temporary"));
				hmDesigVDAIndexRates.put(rs.getString("designation_id"), innerList);
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from vda_rate_details where paycycle=? and from_date=? and to_date=?");
			pst.setInt(1, uF.parseToInt(strPaycycle[2]));
			pst.setDate(2, uF.getDateFormat(strPaycycle[0], DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strPaycycle[1], DATE_FORMAT));
			rs = pst.executeQuery();
			List<String> alExistDesigIds = new ArrayList<String>();
			while(rs.next()) {
				if(rs.getString("desig_id") != null && rs.getInt("desig_id")>0) {
					alExistDesigIds.add(rs.getString("desig_id"));
				} else {
					flag = true;
				}
			}
			rs.close();
			pst.close();
			
			if(flag) {
				pst = con.prepareStatement("update vda_rate_details set vda_rate=? where paycycle=? and from_date=? and to_date=?");
				pst.setDouble(1, uF.parseToDouble(getVdaRate()));
				pst.setInt(2, uF.parseToInt(strPaycycle[2]));
				pst.setDate(3, uF.getDateFormat(strPaycycle[0], DATE_FORMAT));
				pst.setDate(4, uF.getDateFormat(strPaycycle[1], DATE_FORMAT));
//				System.out.println("pst ===>> " + pst);
				pst.executeUpdate();
				pst.close();
				
				pst = con.prepareStatement("update vda_rate_details set vda_rate=?,vda_index_probation=?,vda_index_permanent=?,vda_index_temporary=?," +
					"vda_amount_probation=?,vda_amount_permanent=?,vda_amount_temporary=?,updated_by=?,update_date=? where desig_id=? and " +
					"paycycle=? and from_date=? and to_date=?");
				pst1 = con.prepareStatement("insert into vda_rate_details (org_id,paycycle,from_date,to_date,vda_rate,desig_id,vda_index_probation," +
					"vda_index_permanent,vda_index_temporary,vda_amount_probation,vda_amount_permanent,vda_amount_temporary,added_by,entry_date)" +
					"values (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?)");
				Iterator<String> it = hmDesigVDAIndexRates.keySet().iterator();
				while (it.hasNext()) {
					String strDesigId = it.next();
					List<String> innerList = hmDesigVDAIndexRates.get(strDesigId);
					double vdaAmtProbation = ((uF.parseToDouble(getVdaRate()) * VDA_FORMULA_FIX_VAL_1 * VDA_FORMULA_FIX_VAL_2) - VDA_FORMULA_FIX_VAL_3) * uF.parseToDouble(innerList.get(0));
					double vdaAmtPermanent = ((uF.parseToDouble(getVdaRate()) * VDA_FORMULA_FIX_VAL_1 * VDA_FORMULA_FIX_VAL_2) - VDA_FORMULA_FIX_VAL_3) * uF.parseToDouble(innerList.get(1));
					double vdaAmtTemporary = ((uF.parseToDouble(getVdaRate()) * VDA_FORMULA_FIX_VAL_1 * VDA_FORMULA_FIX_VAL_2) - VDA_FORMULA_FIX_VAL_3) * uF.parseToDouble(innerList.get(2));
					if(alExistDesigIds.contains(strDesigId)) {
						pst.setDouble(1, uF.parseToDouble(getVdaRate()));
						pst.setDouble(2, uF.parseToDouble(innerList.get(0)));
						pst.setDouble(3, uF.parseToDouble(innerList.get(1)));
						pst.setDouble(4, uF.parseToDouble(innerList.get(2)));
						pst.setDouble(5, vdaAmtProbation);
						pst.setDouble(6, vdaAmtPermanent);
						pst.setDouble(7, vdaAmtTemporary);
						pst.setInt(8, uF.parseToInt(strSessionEmpId));
						pst.setDate(9, uF.getCurrentDate(CF.getStrTimeZone()));
						pst.setInt(10, uF.parseToInt(strDesigId));
						pst.setInt(11, uF.parseToInt(strPaycycle[2]));
						pst.setDate(12, uF.getDateFormat(strPaycycle[0], DATE_FORMAT));
						pst.setDate(13, uF.getDateFormat(strPaycycle[1], DATE_FORMAT));
						pst.addBatch();
						flag2 = true;
					} else {
						pst1.setInt(1, uF.parseToInt(getStrOrgId()));
						pst1.setInt(2, uF.parseToInt(strPaycycle[2]));
						pst1.setDate(3, uF.getDateFormat(strPaycycle[0], DATE_FORMAT));
						pst1.setDate(4, uF.getDateFormat(strPaycycle[1], DATE_FORMAT));
						pst1.setDouble(5, uF.parseToDouble(getVdaRate()));
						pst1.setInt(6, uF.parseToInt(strDesigId));
						pst1.setDouble(7, uF.parseToDouble(innerList.get(0)));
						pst1.setDouble(8, uF.parseToDouble(innerList.get(1)));
						pst1.setDouble(9, uF.parseToDouble(innerList.get(2)));
						pst1.setDouble(10, vdaAmtProbation);
						pst1.setDouble(11, vdaAmtPermanent);
						pst1.setDouble(12, vdaAmtTemporary);
						pst1.setInt(13, uF.parseToInt(strSessionEmpId));
						pst1.setDate(14, uF.getCurrentDate(CF.getStrTimeZone()));
						pst1.addBatch();
						flag1 = true;
					}
				}
				if (flag2) {
					int[] x = pst.executeBatch();
					if (x.length > 0) {
						con.commit();
					} else {
						con.rollback();
						session.setAttribute(MESSAGE, ERRORM + "Colud not update VDA Rate. Please,try again." + END);
					}
				}
				if (flag1) {
					int[] x = pst1.executeBatch();
					if (x.length > 0) {
						con.commit();
					} else {
						con.rollback();
						session.setAttribute(MESSAGE, ERRORM + "Colud not update VDA Rate. Please,try again." + END);
					}
				}
				
			} else {
				pst = con.prepareStatement("insert into vda_rate_details (org_id,paycycle,from_date,to_date,vda_rate) values (?,?,?,?, ?)");
				pst.setInt(1, uF.parseToInt(getStrOrgId()));
				pst.setInt(2, uF.parseToInt(strPaycycle[2]));
				pst.setDate(3, uF.getDateFormat(strPaycycle[0], DATE_FORMAT));
				pst.setDate(4, uF.getDateFormat(strPaycycle[1], DATE_FORMAT));
				pst.setDouble(5, uF.parseToDouble(getVdaRate()));
				System.out.println("pst ===>> " + pst);
				pst.executeUpdate();
				pst.close();
				
				pst = con.prepareStatement("insert into vda_rate_details (org_id,paycycle,from_date,to_date,vda_rate,desig_id,vda_index_probation," +
					"vda_index_permanent,vda_index_temporary,vda_amount_probation,vda_amount_permanent,vda_amount_temporary,added_by,entry_date)" +
					"values (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?)");
				Iterator<String> it = hmDesigVDAIndexRates.keySet().iterator();
				while (it.hasNext()) {
					String strDesigId = it.next();
					List<String> innerList = hmDesigVDAIndexRates.get(strDesigId);
					double vdaAmtProbation = ((uF.parseToDouble(getVdaRate()) * VDA_FORMULA_FIX_VAL_1 * VDA_FORMULA_FIX_VAL_2) - VDA_FORMULA_FIX_VAL_3) * uF.parseToDouble(innerList.get(0));
					double vdaAmtPermanent = ((uF.parseToDouble(getVdaRate()) * VDA_FORMULA_FIX_VAL_1 * VDA_FORMULA_FIX_VAL_2) - VDA_FORMULA_FIX_VAL_3) * uF.parseToDouble(innerList.get(1));
					double vdaAmtTemporary = ((uF.parseToDouble(getVdaRate()) * VDA_FORMULA_FIX_VAL_1 * VDA_FORMULA_FIX_VAL_2) - VDA_FORMULA_FIX_VAL_3) * uF.parseToDouble(innerList.get(2));
					pst.setInt(1, uF.parseToInt(getStrOrgId()));
					pst.setInt(2, uF.parseToInt(strPaycycle[2]));
					pst.setDate(3, uF.getDateFormat(strPaycycle[0], DATE_FORMAT));
					pst.setDate(4, uF.getDateFormat(strPaycycle[1], DATE_FORMAT));
					pst.setDouble(5, uF.parseToDouble(getVdaRate()));
					pst.setInt(6, uF.parseToInt(strDesigId));
					pst.setDouble(7, uF.parseToDouble(innerList.get(0)));
					pst.setDouble(8, uF.parseToDouble(innerList.get(1)));
					pst.setDouble(9, uF.parseToDouble(innerList.get(2)));
					pst.setDouble(10, vdaAmtProbation);
					pst.setDouble(11, vdaAmtPermanent);
					pst.setDouble(12, vdaAmtTemporary);
					pst.setInt(13, uF.parseToInt(strSessionEmpId));
					pst.setDate(14, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.addBatch();
					flag1 = true;
				}
				if (flag1) {
					int[] x = pst.executeBatch();
					if (x.length > 0) {
						con.commit();
					} else {
						con.rollback();
						session.setAttribute(MESSAGE, ERRORM + "Colud not update VDA Rate. Please,try again." + END);
					}
				}
			}
			session.setAttribute(MESSAGE, SUCCESSM+"VDA Rate added successfully."+END);
			setStrPaycycleStartDate(strPaycycle[0]);
			
			Thread th = new Thread(this);
			th.start();
			
		} catch (Exception e) {
			e.printStackTrace();			
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;
	}
	
	
	@Override
	public void run() {
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			updateAllEmpSalaryStruture(con, uF, getStrPaycycleStartDate());
//			addGoalFrequency(uF);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeConnection(con);
		}
		
	}
	
	public void updateAllEmpSalaryStruture(Connection con, UtilityFunctions uF, String strPaycycleStartDate) {
	
		Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
		boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
//			System.out.println(" =====>> in updateAllEmpSalaryStruture ");
			Map<String, String> hmGradeVDARate = new HashMap<String, String>();
			pst = con.prepareStatement("select * from vda_rate_details vrd, grades_details gd where vrd.desig_id>0 and gd.designation_id=vrd.desig_id " +
				"and from_date = (select max(from_date) as from_date from vda_rate_details where from_date<=?) ");
			pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
			rs = pst.executeQuery();
			String strMaxFromDate = uF.getCurrentDate(CF.getStrTimeZone())+"";
			while(rs.next()) {
				strMaxFromDate = rs.getString("from_date");
				hmGradeVDARate.put(rs.getString("grade_id")+"_PROBATION", rs.getString("vda_amount_probation"));
				hmGradeVDARate.put(rs.getString("grade_id")+"_PERMANENT", rs.getString("vda_amount_permanent"));
				hmGradeVDARate.put(rs.getString("grade_id")+"_TEMPORARY", rs.getString("vda_amount_temporary"));
			}
			rs.close();
			pst.close();
//			System.out.println("getStrPaycycleStartDate ===>> " + getStrPaycycleStartDate());
			
			Date datePaycycleStDt = uF.getDateFormatUtil(strPaycycleStartDate, DATE_FORMAT);
			Date dateMaxFrmDt = uF.getDateFormatUtil(strMaxFromDate, DBDATE);
			
//			System.out.println("datePaycycleStDt ===>> " + datePaycycleStDt);
//			System.out.println("dateMaxFrmDt ===>> " + dateMaxFrmDt);
			if(dateMaxFrmDt.equals(datePaycycleStDt)) {
				StringBuilder sbQuery = new StringBuilder();
				sbQuery.append("select * from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id and " +
					"(epd.employment_end_date is null OR epd.employment_end_date >=?) and epd.joining_date<=? and is_alive=true and approved_flag=true ");
	//			sbQuery.append(" and epd.emp_per_id = 2764");
	//			System.out.println("pst ===>>  " + pst);
				pst = con.prepareStatement(sbQuery.toString());
				pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
	//			System.out.println("pst 1 ===>>  " + pst);
				rs = pst.executeQuery();
	//			System.out.println("pst 2 ===>> " + pst);
				Map<String, Map<String, String>> hmEmp = new LinkedHashMap<String, Map<String, String>>();
				List<String> alGradeId = new ArrayList<String>();
				while (rs.next()) {
					Map<String, String> hmEmpPay = new HashMap<String, String>();
					hmEmpPay.put("EMP_ID", rs.getString("emp_id"));
					hmEmpPay.put("EMPCODE", rs.getString("empcode"));
	
					/*String strMiddleName = (rs.getString("emp_mname") != null && !rs.getString("emp_mname").trim().equals("")) ? rs.getString("emp_mname").trim()
							+ " " : "";*/
					
					String strEmpMName = "";
					if(flagMiddleName) {
						if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
							strEmpMName = " "+rs.getString("emp_mname");
						}
					}
					
					String strEmpName = rs.getString("emp_fname") +strEmpMName+ " " +rs.getString("emp_lname");
					hmEmpPay.put("EMP_NAME", strEmpName);
					hmEmpPay.put("EMP_PAYMENT_MODE_ID", rs.getString("payment_mode"));
					hmEmpPay.put("EMP_BIRTH_DATE", uF.getDateFormat(rs.getString("emp_date_of_birth"), DBDATE, DATE_FORMAT));
					hmEmpPay.put("EMP_JOINING_DATE", uF.getDateFormat(rs.getString("joining_date"), DBDATE, DATE_FORMAT));
	
					if (rs.getString("employment_end_date") != null) {
						hmEmpPay.put("EMP_END_DATE", uF.getDateFormat(rs.getString("employment_end_date"), DBDATE, DATE_FORMAT));
					}
					hmEmpPay.put("EMP_GENDER", rs.getString("emp_gender"));
					String strDays = uF.dateDifference(rs.getString("emp_date_of_birth"), DBDATE, uF.getCurrentDate(CF.getStrTimeZone()) + "", DBDATE,CF.getStrTimeZone());
					double dblYears = uF.parseToDouble(strDays) / 365;
					hmEmpPay.put("EMP_AGE", dblYears + "");
					hmEmpPay.put("EMP_GRADE_ID", rs.getString("grade_id"));
					hmEmpPay.put("EMP_STATUS", rs.getString("emp_status"));
	
					if (rs.getString("service_id") != null) {
						String[] tempService = rs.getString("service_id").split(",");
						if (tempService.length > 0) {
							hmEmpPay.put("EMP_SERVICE_ID", tempService[0]);
						}
					}
					hmEmp.put(rs.getString("emp_id"), hmEmpPay);
					if (!alGradeId.contains(rs.getString("grade_id"))) {
						alGradeId.add(rs.getString("grade_id"));
					}
				}
				rs.close();
				pst.close();
	//			System.out.println("hmEmp ===>> " + hmEmp);
				
				
				String strGradeIds = StringUtils.join(alGradeId.toArray(), ",");
				Map<String,Map<String,Map<String,String>>> hmSalaryDetails1 = new HashMap<String,Map<String,Map<String,String>>>();
				pst = con.prepareStatement("select * from salary_details where grade_id in("+strGradeIds+") and (is_delete is null or is_delete=false) " +
					"order by earning_deduction desc, weight, salary_head_id");
				rs = pst.executeQuery();
	//			System.out.println("pst ===>> " + pst);
				while (rs.next()) {
					Map<String,Map<String,String>> hmSalInner = hmSalaryDetails1.get(rs.getString("grade_id"));
					if(hmSalInner == null) hmSalInner = new HashMap<String, Map<String,String>>(); 
					
					Map<String, String> hmInnerSal = new HashMap<String, String>();
					hmInnerSal.put("SALARY_HEAD_ID", rs.getString("salary_head_id"));
					hmInnerSal.put("EARNING_DEDUCTION", rs.getString("earning_deduction"));
					hmInnerSal.put("SALARY_AMOUNT_TYPE", rs.getString("salary_head_amount_type"));
					hmInnerSal.put("SUB_SALARY_HEAD_ID", rs.getString("sub_salary_head_id"));
					hmInnerSal.put("SALARY_HEAD_AMOUNT", rs.getString("salary_head_amount"));
					hmInnerSal.put("IS_CTC_VARIABLE", ""+uF.parseToBoolean(rs.getString("is_ctc_variable")));
					hmInnerSal.put("MULTIPLE_CALCULATION", rs.getString("multiple_calculation"));
					hmInnerSal.put("IS_ALIGN_WITH_PERK", ""+uF.parseToBoolean(rs.getString("is_align_with_perk")));
					hmInnerSal.put("IS_DEFAULT_CAL_ALLOWANCE", ""+uF.parseToBoolean(rs.getString("is_default_cal_allowance")));
					hmInnerSal.put("SALARY_TYPE", rs.getString("salary_type"));
					
					hmSalInner.put(rs.getString("salary_head_id"), hmInnerSal);
					hmSalaryDetails1.put(rs.getString("grade_id"), hmSalInner);
				}
				rs.close();
				pst.close();
				
				Map<String, String> hmEmpServiceTaxMap = CF.getEmpServiceTax(con, uF, CF);
				if (hmEmpServiceTaxMap == null)hmEmpServiceTaxMap = new HashMap<String, String>();
				
				boolean flag1 = false;
				pst = con.prepareStatement("update emp_salary_details set amount=? where emp_salary_id=?");
				Iterator<String> it1 = hmEmp.keySet().iterator();
				while(it1.hasNext()) {
					String strEmpId = it1.next();
	//				System.out.println("strEmpId ===>> " + strEmpId);
					Map<String, String> hmEmpPay = hmEmp.get(strEmpId);
					String strEmpVDAAmount = hmGradeVDARate.get(hmEmpPay.get("EMP_GRADE_ID")+"_"+hmEmpPay.get("EMP_STATUS"));
					Map<String, Map<String, String>> hmInner = new LinkedHashMap<String, Map<String, String>>();
					Map<String,Map<String,String>> hmSalInner = hmSalaryDetails1.get(hmEmpPay.get("EMP_GRADE_ID"));
					hmInner = getSalaryCalculationByGrade(con, uF.parseToInt(strEmpId), hmEmpPay.get("EMP_GRADE_ID"), uF, CF, hmSalInner, strEmpVDAAmount);
					
	//				System.out.println("hmInner ===>> " + hmInner);
					
					Map<String, String> hmInnerTemp = new HashMap<String, String>();
					
					if (hmEmpServiceTaxMap.size() > 0 && !hmInner.containsKey(CGST + "")) {
						hmInnerTemp = new HashMap<String, String>();
						hmInnerTemp.put("AMOUNT", "0");
						hmInnerTemp.put("EARNING_DEDUCTION", "E");
						hmInnerTemp.put("SALARY_AMOUNT_TYPE", "A");
						hmInner.put(CGST + "", hmInnerTemp);
						
						hmInnerTemp = new HashMap<String, String>();
						hmInnerTemp.put("AMOUNT", "0");
						hmInnerTemp.put("EARNING_DEDUCTION", "E");
						hmInnerTemp.put("SALARY_AMOUNT_TYPE", "A");
						hmInner.put(SGST + "", hmInnerTemp);
						
					}
	
					if (hmInner.size() > 0 && hmInner.containsKey(TDS + "")) {
						hmInnerTemp = new HashMap<String, String>();
						hmInnerTemp = hmInner.get(TDS + "");
						hmInnerTemp.put("AMOUNT", hmInnerTemp.get("AMOUNT"));
						hmInnerTemp.put("EARNING_DEDUCTION", "D");
						hmInnerTemp.put("SALARY_AMOUNT_TYPE", "A");
						hmInner.remove(TDS + "");
						hmInner.put(TDS + "", hmInnerTemp);
					}
	
					Map<String, String> hmTotal = new HashMap<String, String>();
					Map<String, String> hmVDAINFormulaTotal = new HashMap<String, String>();
					Iterator<String> it = hmInner.keySet().iterator();
					// double dblPerkTDS = 0.0d;
					Set<String> setContriSalHead = new HashSet<String>();
					
					while (it.hasNext()) {
						String strSalaryId = it.next();
						int nSalayHead = uF.parseToInt(strSalaryId);
	
						Map<String, String> hm = hmInner.get(strSalaryId);
						if (hm == null) hm = new HashMap<String, String>();
						
						String strMulCal = hm.get("MULTIPLE_CALCULATION");
						List<String> al = new ArrayList<String>();
						if(strMulCal != null && !strMulCal.equals("")) {
							al = Arrays.asList(strMulCal.trim().split(","));
						}
						if(al != null && al.contains(""+EMPLOYER_EPF)) {
							setContriSalHead.add(""+EMPLOYER_EPF);
						}
						if(al != null && al.contains(""+EMPLOYER_ESI)) {
							setContriSalHead.add(""+EMPLOYER_ESI);
						}
						if(al != null && al.contains(""+EMPLOYER_LWF)) {
							setContriSalHead.add(""+EMPLOYER_LWF);
						}
						
						String str_E_OR_D = hm.get("EARNING_DEDUCTION");
	//					if (str_E_OR_D != null && str_E_OR_D.equalsIgnoreCase("E") && (hm.get("SALARY_AMOUNT_TYPE") != null && !hm.get("SALARY_AMOUNT_TYPE").trim().equalsIgnoreCase("P"))) {
						if (str_E_OR_D != null && str_E_OR_D.equalsIgnoreCase("E")) {
	
							if (uF.parseToInt(strSalaryId) != GROSS) {
								boolean isMultipePerWithParticularHead = false;
								if(hm.get("SALARY_AMOUNT_TYPE") != null && hm.get("SALARY_AMOUNT_TYPE").trim().equalsIgnoreCase("P")) {
									isMultipePerWithParticularHead = checkMultipleCalPerWithParticularHead(con, uF, hm);
								}
	//							System.out.println(strSalaryId + " --- in else E isMultipePerWithParticularHead ===>> " + isMultipePerWithParticularHead);
								if(!isMultipePerWithParticularHead) {
	//								System.out.println("in else E strSalaryId ===>> " + strSalaryId + " AMOUNT ===>> " + uF.parseToDouble(hm.get("AMOUNT")));
									hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hm.get("AMOUNT"))));
								}
							}
	
	//					} else if (str_E_OR_D != null && str_E_OR_D.equalsIgnoreCase("D") && (hm.get("SALARY_AMOUNT_TYPE") != null && !hm.get("SALARY_AMOUNT_TYPE").trim().equalsIgnoreCase("P"))) {
						} else if (str_E_OR_D != null && str_E_OR_D.equalsIgnoreCase("D")) {	
							/**
							 * TAX CALCULATION STARTS HERE
							 * 
							 * */
								if (uF.parseToInt(strSalaryId) != PROFESSIONAL_TAX && uF.parseToInt(strSalaryId) != TDS && uF.parseToInt(strSalaryId) != EMPLOYEE_ESI && uF.parseToInt(strSalaryId) != EMPLOYER_ESI && uF.parseToInt(strSalaryId) != EMPLOYEE_LWF && uF.parseToInt(strSalaryId) != EMPLOYER_LWF) {
	//								System.out.println("in else D Deduction strSalaryId ===>> " + strSalaryId + " AMOUNT ===>> " + uF.parseToDouble(hm.get("AMOUNT")));
									hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), uF.parseToDouble(hm.get("AMOUNT"))));
								}
							}
						hmTotal.put("SALARY_HEAD_ID", strSalaryId);
					}
	
	//				System.out.println("hmTotal ==>>> " + hmTotal);
					/**
					 * Multiple cal start
					 * */
					Iterator<String> itMulti = hmInner.keySet().iterator();
					while (itMulti.hasNext()) {
						String strSalaryId = itMulti.next();
	//					System.out.println("strSalaryId ===>> " + strSalaryId);
						int nSalayHead = uF.parseToInt(strSalaryId);
	
						Map<String, String> hm = hmInner.get(strSalaryId);
						if (hm == null) {
							hm = new HashMap<String, String>();
						}
						String str_E_OR_D = hm.get("EARNING_DEDUCTION");
						List<String> alCheckVDAInFormula = new ArrayList<String>();
	//					System.out.println("MULTIPLE_CALCULATION ===>> " + hm.get("MULTIPLE_CALCULATION"));
						if(hm.get("MULTIPLE_CALCULATION") != null) {
							alCheckVDAInFormula = Arrays.asList(hm.get("MULTIPLE_CALCULATION").split(","));
						}
	//					System.out.println("alCheckVDAInFormula ===>> " + alCheckVDAInFormula);
						if (str_E_OR_D != null && str_E_OR_D.equalsIgnoreCase("E") && hm.get("SALARY_AMOUNT_TYPE") != null && hm.get("SALARY_AMOUNT_TYPE").trim().equalsIgnoreCase("P")) {
	//						System.out.println("before -- multi head formula Earning strSalaryId ===>> " + strSalaryId);
							double dblMulCalAmt = getMultipleCalAmtDetailsByGrade(con, uF, CF, nSalayHead, strEmpId, hmEmpPay.get("EMP_GRADE_ID"), hm, hmTotal, null);
							if(!hmTotal.containsKey(strSalaryId) && alCheckVDAInFormula.contains(""+VDA)) {
	//							System.out.println("multi head formula Earning strSalaryId ===>> " + strSalaryId + " AMOUNT ===>> " + dblMulCalAmt);
								hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), dblMulCalAmt));
								hmVDAINFormulaTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), dblMulCalAmt));
								pst.setDouble(1, dblMulCalAmt);
								pst.setInt(2, uF.parseToInt(hm.get("EMP_SALARY_ID")));
								pst.addBatch();
								flag1 = true;
							}
						} else if (str_E_OR_D != null && str_E_OR_D.equalsIgnoreCase("D") && (hm.get("SALARY_AMOUNT_TYPE") != null && hm.get("SALARY_AMOUNT_TYPE").trim().equalsIgnoreCase("P"))) {
							double dblMulCalAmt = getMultipleCalAmtDetailsByGrade(con, uF, CF, nSalayHead, strEmpId, hmEmpPay.get("EMP_GRADE_ID"), hm, hmTotal, null);
							if(!hmTotal.containsKey(strSalaryId) && alCheckVDAInFormula.contains(""+VDA)) {
	//							System.out.println("multi head formula Deduction strSalaryId ===>> " + strSalaryId + " AMOUNT ===>> " + uF.parseToDouble(hm.get("AMOUNT")));
								hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), dblMulCalAmt));
								hmVDAINFormulaTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), dblMulCalAmt));
								pst.setDouble(1, dblMulCalAmt);
								pst.setInt(2, uF.parseToInt(hm.get("EMP_SALARY_ID")));
								pst.addBatch();
								flag1 = true;
							}
						}
	
						hmTotal.put("SALARY_HEAD_ID", strSalaryId);
					}
					/**
					 * Multiple cal end
					 * */
					
//					System.out.println("hmVDAINFormulaTotal ===>> " + hmVDAINFormulaTotal);
				}
				
				if (flag1) {
					int[] x = pst.executeBatch();
					if (x.length > 0) {
						con.commit();
					} else {
						con.rollback();
						session.setAttribute(MESSAGE, ERRORM + "Colud not update VDA Rate. Please,try again." + END);
					}
				}
			}
			
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			
		}
	}
	
	
	
	public boolean checkMultipleCalPerWithParticularHead(Connection con, UtilityFunctions uF, Map<String, String> hm) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		boolean flag = false;
		try {
			
			String strMulCal = hm.get("MULTIPLE_CALCULATION");
			if(strMulCal != null && !strMulCal.trim().equals("") && !strMulCal.trim().equalsIgnoreCase("NULL")) {
				List<String> al = Arrays.asList(strMulCal.trim().split(","));
				if(al == null) al = new ArrayList<String>();
				int nAl = al.size();
				
				for(int i = 0; i < nAl; i++) {
					int nHeadId = uF.parseToInt(al.get(i));
					if(nHeadId > 0 && nHeadId == REIMBURSEMENT_CTC) {
						flag = true;
					} else if(nHeadId > 0 && nHeadId == EMPLOYEE_EPF) {
						flag = true;
					} else if(nHeadId > 0 && nHeadId == INCENTIVES) {
						flag = true;
					} else if(nHeadId > 0 && nHeadId == AREARS) {
						flag = true;
					} else if(nHeadId > 0 && nHeadId == VDA) {
						flag = true;
					}
				}
			}	
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (pst != null) {
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return flag;
	}
	
	
	
	double getMultipleCalAmtDetailsByGrade(Connection con, UtilityFunctions uF, CommonFunctions CF, int nSalayHead,String strEmpId, String strGrade, Map<String, String> hm, Map<String, String> hmTotal, 
			Map<String, String> hmContriSalHeadAmt) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		double dblMulCalAmt = 0.0d;
		try {
			String strMulCal = hm.get("MULTIPLE_CALCULATION");
			if(strMulCal != null && !strMulCal.trim().equals("") && !strMulCal.trim().equalsIgnoreCase("NULL")) {
				List<String> al = Arrays.asList(strMulCal.trim().split(","));
				if(al == null) al = new ArrayList<String>();
				int nAl = al.size();
				boolean flag = false;
				for(int i = 0; i < nAl; i++) {
					int nHeadId = uF.parseToInt(al.get(i));
					if(nHeadId > 0 && nHeadId == REIMBURSEMENT_CTC) {
						flag = true;
					} else if(nHeadId > 0 && nHeadId == EMPLOYEE_EPF) { 
						flag = true;
					} else if(nHeadId > 0 && nHeadId == INCENTIVES) {
						flag = true;
					} else if(nHeadId > 0 && nHeadId == AREARS) {
						flag = true;
					} else if(nHeadId > 0 && nHeadId == VDA) {
						flag = true;
					}
				}
				
				if(flag) {
					Map<String, String> hmSalaryType = new HashMap<String, String>();
					pst = con.prepareStatement("select * from salary_details where grade_id = ? and (is_delete is null or is_delete=false) order by salary_head_id, salary_id");
					pst.setInt(1, uF.parseToInt(strGrade));
					rs = pst.executeQuery();
					while (rs.next()) {
						hmSalaryType.put(rs.getString("salary_head_id"), rs.getString("salary_type"));
					}
					rs.close();
					pst.close();
		
					pst = con.prepareStatement("select * from emp_salary_details where emp_id = ? and effective_date = (select max(effective_date) " +
						"from emp_salary_details where emp_id =? and effective_date <= ? and is_approved=true and grade_id=?) and salary_head_id " +
						"in ("+CTC+") and salary_head_id in (select salary_head_id from salary_details where (is_delete is null or is_delete=false) " +
						"and org_id in (select org_id from employee_personal_details epd, employee_official_details eod where epd.emp_per_id=eod.emp_id " +
						"and eod.emp_id=?) and grade_id = ?) and grade_id = ?");
					pst.setInt(1, uF.parseToInt(strEmpId));
					pst.setInt(2, uF.parseToInt(strEmpId));
					pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setInt(4, uF.parseToInt(strGrade));
					pst.setInt(5, uF.parseToInt(strEmpId));
					pst.setInt(6, uF.parseToInt(strGrade));
					pst.setInt(7, uF.parseToInt(strGrade));
					rs = pst.executeQuery();
					boolean isCtc = false;
					double dblCTC = 0.0d;
					while (rs.next()) {
						isCtc = true;
						dblCTC = uF.parseToDouble(rs.getString("amount"));
					}
					rs.close();
					pst.close();
		
					String strSalaryType = hmSalaryType.get("" + nSalayHead);
		
					if (isCtc) {
						if (strSalaryType != null && strSalaryType.equalsIgnoreCase("F")) {
							// dblCTC = dblCTC;
						} else if (strSalaryType != null && strSalaryType.equalsIgnoreCase("D")) {
							dblCTC = dblCTC;
						} else {
							dblCTC = dblCTC;
						}
					}
					
					StringBuilder sbFormula = new StringBuilder();
					for(int i = 0; i < nAl; i++) {
						String str = al.get(i);
						if(str != null && !str.trim().equals("") && !str.trim().equalsIgnoreCase("NULL")) {
							boolean isInteger = uF.isInteger(str.trim());
							if(isInteger) {
								double dblAmt = uF.parseToDouble(hmTotal.get(str.trim()));
//								System.out.println("str ===>> " + str+" -- dblAmt ===>> " + dblAmt);
								if (uF.parseToInt(str.trim()) == EMPLOYER_EPF && !hmTotal.containsKey(""+EMPLOYER_EPF)) {
									dblAmt = uF.parseToDouble(hmContriSalHeadAmt.get(""+EMPLOYER_EPF));
								}
								if (uF.parseToInt(str.trim()) == EMPLOYER_ESI && !hmTotal.containsKey(""+EMPLOYER_ESI)) {
									dblAmt = uF.parseToDouble(hmContriSalHeadAmt.get(""+EMPLOYER_ESI));
								}
								if (uF.parseToInt(str.trim()) == EMPLOYER_LWF && !hmTotal.containsKey(""+EMPLOYER_LWF)) {
									dblAmt = uF.parseToDouble(hmContriSalHeadAmt.get(""+EMPLOYER_LWF));
								}
								
								if (uF.parseToInt(str.trim()) == CTC) {
									dblAmt = dblCTC;
								}
								sbFormula.append(""+dblAmt);
							} else {
								sbFormula.append(str.trim());
							}
						}
					}
					String strPercentage = hm.get("SALARY_PERCENTAGE");
					if(uF.parseToDouble(strPercentage) > 0.0d && sbFormula != null && sbFormula.length() > 0) {
						double dblPerAmount = uF.eval(sbFormula.toString());	
						dblMulCalAmt = (uF.parseToDouble(strPercentage) * dblPerAmount)/100;
					} else {
						dblMulCalAmt = uF.parseToDouble(hm.get("AMOUNT"));
					}
					
				} else {
					dblMulCalAmt = uF.parseToDouble(hm.get("AMOUNT"));
				}
			} else {
				dblMulCalAmt = uF.parseToDouble(hm.get("AMOUNT"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (pst != null) {
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return dblMulCalAmt;
	}
	
	
	
	
	public Map<String, Map<String, String>> getSalaryCalculationByGrade(Connection con, int nEmpId, String strGradeId,
		UtilityFunctions uF, CommonFunctions CF, Map<String, Map<String, String>> hmSalaryDetails, String strEmpVDAAmount) {
			PreparedStatement pst = null;
			ResultSet rs = null;
			Map<String, Map<String, String>> hmSalaryHeadReCalculatedMap = new LinkedHashMap<String, Map<String, String>>();

			try {
				StringBuilder sbQuery = new StringBuilder();
				sbQuery.append("select * from (select *, 1 as aa from emp_salary_details where emp_id=? and effective_date=(select max(effective_date) " +
					"from emp_salary_details where emp_id=? and effective_date<=? and is_approved=true) and salary_head_id not in ("+CTC+","+TDS+","+DA1+","+GROSS+") " +
					"and is_approved=true and salary_head_id in (select salary_head_id from salary_details where (is_delete is null or is_delete=false) " +
					"and org_id in (select org_id from employee_personal_details epd, employee_official_details eod where epd.emp_per_id=eod.emp_id " +
					"and eod.emp_id=?) and grade_id=?) order by earning_deduction desc, salary_head_id, emp_salary_id ) ac " +
					" union " +
					"(select *, 2 as aa from emp_salary_details where emp_id=? and effective_date=(select max(effective_date) from emp_salary_details " +
					"where emp_id=? and effective_date<=? and is_approved=true) and salary_head_id in ("+TDS+") and salary_head_id not in ("+CTC+") " +
					"and salary_head_id in (select salary_head_id from salary_details where (is_delete is null or is_delete=false) and org_id in " +
					"(select org_id from employee_personal_details epd, employee_official_details eod where epd.emp_per_id=eod.emp_id and eod.emp_id=?) " +
					"and grade_id=?) order by earning_deduction desc, salary_head_id, emp_salary_id) order by aa, earning_deduction desc, salary_head_id, emp_salary_id");
				pst = con.prepareStatement(sbQuery.toString());
				pst.setInt(1, nEmpId);
				pst.setInt(2, nEmpId);
				pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setInt(4, nEmpId);
				pst.setInt(5, uF.parseToInt(strGradeId));
				pst.setInt(6, nEmpId);
				pst.setInt(7, nEmpId);
				pst.setDate(8, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setInt(9, nEmpId);
				pst.setInt(10, uF.parseToInt(strGradeId));
				// if(nEmpId==181){
//				System.out.println("pst ========>> " + pst);
				// }   
				rs = pst.executeQuery();
				while (rs.next()) {

					String strSalaryHeadId = rs.getString("salary_head_id");
					String strAmount = rs.getString("amount");
					double dblAmount = uF.parseToDouble(strAmount);
					if(uF.parseToInt(strSalaryHeadId) == VDA) {
						dblAmount = uF.parseToDouble(strEmpVDAAmount);
//						System.out.println("dblAmount ===>> " + dblAmount);
					}
					
					Map<String, String> hmInnerSal = hmSalaryDetails.get(strSalaryHeadId);
					if (hmInnerSal == null)
						hmInnerSal = new HashMap<String, String>();

					String strSalPercentage = hmInnerSal.get("SALARY_HEAD_AMOUNT");
					String strSalAmountType = hmInnerSal.get("SALARY_AMOUNT_TYPE");
					String isCTCVariable = hmInnerSal.get("IS_CTC_VARIABLE");
					String strMultipleCalculation = hmInnerSal.get("MULTIPLE_CALCULATION");
					String isAlignWithPerk = hmInnerSal.get("IS_ALIGN_WITH_PERK");
					String isDefaultCalAllowance = hmInnerSal.get("IS_DEFAULT_CAL_ALLOWANCE");

					String strEarningDeduction = hmInnerSal.get("EARNING_DEDUCTION");
					String strSalaryType = hmInnerSal.get("SALARY_TYPE");

					if (strEarningDeduction != null) {
						Map<String, String> hmSalaryInner = new HashMap<String, String>();
						hmSalaryInner.put("EMP_SALARY_ID", rs.getString("emp_salary_id"));
						hmSalaryInner.put("EARNING_DEDUCTION", strEarningDeduction);
						hmSalaryInner.put("AMOUNT", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), dblAmount));
						hmSalaryInner.put("SALARY_AMOUNT_TYPE", strSalAmountType);
						hmSalaryInner.put("MULTIPLE_CALCULATION", strMultipleCalculation);
						hmSalaryInner.put("IS_ALIGN_WITH_PERK", isAlignWithPerk);
						hmSalaryInner.put("SALARY_PERCENTAGE", strSalPercentage);
						hmSalaryInner.put("IS_DEFAULT_CAL_ALLOWANCE", isDefaultCalAllowance);

//						if (!rs.getBoolean("isdisplay") && uF.parseToBoolean(isCTCVariable)) {
//							hmInnerisDisplay.put(strSalaryHeadId, hmSalaryInner);
//						} else if (rs.getBoolean("isdisplay") && !uF.parseToBoolean(isCTCVariable)) {
							hmSalaryHeadReCalculatedMap.put(strSalaryHeadId, hmSalaryInner);
//						}
					}
				}
				rs.close();
				pst.close();

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (rs != null) {
					try {
						rs.close();
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
				}
				if (pst != null) {
					try {
						pst.close();
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
				}
			}
			return hmSalaryHeadReCalculatedMap;
		}
	
	
	
	public String loadPaySlipSetting() {
		paycycleList = new FillPayCycles(getStrPaycycleDuration(), request).fillPayCycles(CF, getStrOrgId());
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			strOrgName = CF.getOrgNameById(con, strOrgId);
			
		} catch (Exception e) {
			e.printStackTrace();			
		}finally{
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return LOAD;
	}

	private HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

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

	public String getStrOrgId() {
		return strOrgId;
	}

	public void setStrOrgId(String strOrgId) {
		this.strOrgId = strOrgId;
	}

	public String getPaycycle() {
		return paycycle;
	}

	public void setPaycycle(String paycycle) {
		this.paycycle = paycycle;
	}

	public List<FillPayCycles> getPaycycleList() {
		return paycycleList;
	}

	public void setPaycycleList(List<FillPayCycles> paycycleList) {
		this.paycycleList = paycycleList;
	}

	public String getStrPaycycleDuration() {
		return strPaycycleDuration;
	}

	public void setStrPaycycleDuration(String strPaycycleDuration) {
		this.strPaycycleDuration = strPaycycleDuration;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getStrVdaRateId() {
		return strVdaRateId;
	}

	public void setStrVdaRateId(String strVdaRateId) {
		this.strVdaRateId = strVdaRateId;
	}

	public String getVdaRate() {
		return vdaRate;
	}

	public void setVdaRate(String vdaRate) {
		this.vdaRate = vdaRate;
	}

	public String getStrOrgName() {
		return strOrgName;
	}

	public void setStrOrgName(String strOrgName) {
		this.strOrgName = strOrgName;
	}

	public String getStrPaycycleName() {
		return strPaycycleName;
	}

	public void setStrPaycycleName(String strPaycycleName) {
		this.strPaycycleName = strPaycycleName;
	}


	public String getStrPaycycleStartDate() {
		return strPaycycleStartDate;
	}


	public void setStrPaycycleStartDate(String strPaycycleStartDate) {
		this.strPaycycleStartDate = strPaycycleStartDate;
	}

	
}