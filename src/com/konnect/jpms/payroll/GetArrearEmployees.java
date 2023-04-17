package com.konnect.jpms.payroll;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillEmployee;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class GetArrearEmployees extends ActionSupport implements IStatements, ServletRequestAware{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3105338539839910045L;
	HttpSession session;
	CommonFunctions CF;
	
	
	String[] strArrearEmpId;
	String strArearCode;
	String strArearName;
	String strArearDesc;
	String strArearAmount;
	String strArearDuration;
	String strArearEffectiveDate;
	String strArrearId;
	
	String strBasic;
	String arrearType;
	String strArrearDays;
	
	String arrearPaycycle;
	
	public String execute() throws Exception {
		
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		UtilityFunctions uF = new UtilityFunctions();
		
		String f_org = (String) request.getParameter("f_org");
		String f_WLocation = (String)request.getParameter("wlocationId");
		String f_department = (String)request.getParameter("departmentId");
		String f_service = (String)request.getParameter("serviceId");
		String f_level = (String)request.getParameter("levelId");
		
		
		String deleteId = (String)request.getParameter("DID");
		
//		System.out.println("f_WLocation ===> " + f_WLocation);
//		System.out.println("f_department===>"+f_department);
//		System.out.println("f_service===>"+f_service);
//		System.out.println("f_level===>"+f_level);
		
		if(deleteId!=null && uF.parseToInt(deleteId)>0) {
			deleteArearDetails(uF, deleteId);
			return "delete";
		}		
		
//		System.out.println("===>"+getStrArrearId());
		if(uF.parseToInt(getStrArrearId())>0) {
			updateArrearDetails(uF);
			return "save";
		} else if(getStrArrearEmpId()!=null && getStrArrearEmpId().length>0) {
			saveArearDetails(uF);
			return "save";
		}
		
		getArearEmployeeList(uF, f_org, f_WLocation, f_department, f_service, f_level);
		return SUCCESS;
	}
	
	
	public void saveArearDetails(UtilityFunctions uF) {
		
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		try {
			
			con = db.makeConnection(con);
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			
//			System.out.println("getStrArrearEmpId() ===>> " + getStrArrearEmpId().length);
			int nArrearType = uF.parseToInt(getArrearType()); 
			double dblArrearDays = 0.0d;
			String paycycyle_from = null;
			String paycycyle_to = null;
			int nPaycycle = 0;
			double arrearAmount = uF.parseToDouble(getStrArearAmount());
			double dblBasic = uF.parseToDouble(getStrBasic());
			String strEffectiveDate = getStrArearEffectiveDate();
			if(nArrearType == 1) {
				dblArrearDays = uF.parseToDouble(getStrArrearDays());
				
				String[] strPayCycleDates = getArrearPaycycle().split("-");
				paycycyle_from = strPayCycleDates[0];
				paycycyle_to = strPayCycleDates[1];
				nPaycycle = uF.parseToInt(strPayCycleDates[2]);
				
				arrearAmount = 0.0d;
				dblBasic = 0.0d;
				strEffectiveDate = null;
			}
			
			List<String> alEmpAlreadyApplied = new ArrayList<String>();
			for(int i=0; getStrArrearEmpId()!=null && i<getStrArrearEmpId().length; i++) {
				boolean arrearDaysFlag = false;
				if(nArrearType == 1) {
					pst = con.prepareStatement("select * from arear_details where arrear_type=1 and emp_id=? and paycycle_from=? and paycycle_to=? and paycycle=?");
					pst.setInt(1, uF.parseToInt(getStrArrearEmpId()[i]));
					pst.setDate(2, uF.getDateFormat(paycycyle_from, DATE_FORMAT));
					pst.setDate(3, uF.getDateFormat(paycycyle_to, DATE_FORMAT));
					pst.setInt(4, nPaycycle);
					rs = pst.executeQuery();
					while(rs.next()) {
						arrearDaysFlag = true;
						if(!alEmpAlreadyApplied.contains(rs.getString("emp_id"))) {
							alEmpAlreadyApplied.add(rs.getString("emp_id"));
						}
					}
					rs.close();
					pst.close();
				}
				
				if(arrearDaysFlag) {
					continue;
				}				
				
				pst = con.prepareStatement("insert into arear_details (emp_id, arear_amount, effective_date, duration_months, user_id, entry_date, " +
					"total_amount_paid, arear_description, arear_amount_balance, monthly_arear, arear_code, arear_name, basic_amount, arrear_type, " +
					"arrear_days, paycycle_from, paycycle_to, paycycle) values (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?) ");
				pst.setInt(1, uF.parseToInt(getStrArrearEmpId()[i]));
				pst.setDouble(2, arrearAmount);
				pst.setDate(3, uF.getDateFormat(strEffectiveDate, DATE_FORMAT));
//				pst.setInt(4, uF.parseToInt(getStrArearDuration()));
				pst.setInt(4, 1);
				pst.setInt(5, uF.parseToInt((String)session.getAttribute(EMPID)));
				pst.setDate(6, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setDouble(7, 0);
				pst.setString(8, getStrArearDesc());
				pst.setDouble(9, arrearAmount);
				pst.setDouble(10, arrearAmount);
				pst.setString(11, getStrArearCode());
				pst.setString(12, getStrArearName());
				pst.setDouble(13, dblBasic);
				pst.setInt(14, nArrearType);
				pst.setDouble(15, dblArrearDays);
				pst.setDate(16, uF.getDateFormat(paycycyle_from, DATE_FORMAT));
				pst.setDate(17, uF.getDateFormat(paycycyle_to, DATE_FORMAT));
				pst.setInt(18, nPaycycle);
//				System.out.println("pst ===>> " + pst);
				pst.executeUpdate();
				pst.close();
			}
			
			if(alEmpAlreadyApplied.size() > 0) {
				String strAppliedArrearEmpIds = StringUtils.join(alEmpAlreadyApplied.toArray(),",");
				pst = con.prepareStatement("select * from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id " +
					"and eod.emp_id in ("+strAppliedArrearEmpIds+")");
				rs = pst.executeQuery();
				StringBuilder sbEmpName = null;
				while(rs.next()) {
					//String strMiddleName=(rs.getString("emp_mname")!=null && !rs.getString("emp_mname").trim().equals("")) ? rs.getString("emp_mname").trim()+" " : "";
					
					String strEmpMName = "";
					if(flagMiddleName) {
						if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
							strEmpMName = " "+rs.getString("emp_mname");
						}
					}
					
					String strEmpName = rs.getString("emp_fname") + strEmpMName+" "+ rs.getString("emp_lname");
					
					if(sbEmpName == null) {
						sbEmpName = new StringBuilder();
						sbEmpName.append(strEmpName);
					} else {
						sbEmpName.append(", "+strEmpName);
					}
				}
				rs.close();
				pst.execute();
				
				if(sbEmpName != null) {
					String strMsg = sbEmpName.toString()+" arrears are already applied for this Paycycle "+nPaycycle+", "+paycycyle_from+" - "+paycycyle_to;
					session.setAttribute(MESSAGE, ERRORM+strMsg+END);
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
	
	
	public void deleteArearDetails(UtilityFunctions uF, String strDeleteId) {
		
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		
		try {
			
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("delete from arear_details where arear_id=?");
			pst.setInt(1, uF.parseToInt(strDeleteId));
			pst.execute();
			pst.close();
			
			session.setAttribute(MESSAGE, SUCCESSM+"Arrear deleted Successfully"+END);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	public void updateArrearDetails(UtilityFunctions uF) {
		
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		try {
			
			con = db.makeConnection(con);
			
			int nArrearType = uF.parseToInt(getArrearType()); 
			double dblArrearDays = 0.0d;
			String paycycyle_from = null;
			String paycycyle_to = null;
			int nPaycycle = 0;
			double arrearAmount = uF.parseToDouble(getStrArearAmount());
			double dblBasic = uF.parseToDouble(getStrBasic());
			String strEffectiveDate = getStrArearEffectiveDate();
			if(nArrearType == 1) {
				dblArrearDays = uF.parseToDouble(getStrArrearDays());
				
				String[] strPayCycleDates = getArrearPaycycle().split("-");
				paycycyle_from = strPayCycleDates[0];
				paycycyle_to = strPayCycleDates[1];
				nPaycycle = uF.parseToInt(strPayCycleDates[2]);
				
				arrearAmount = 0.0d;
				dblBasic = 0.0d;
				strEffectiveDate = null;
			}
			
			
			pst = con.prepareStatement("select * from arear_details where arear_id= ?");
			pst.setInt(1, uF.parseToInt(getStrArrearId()));
			rs = pst.executeQuery();
			int nEmpId = 0;
			String strPaycycleFrom = "";
			String strPaycycleTo = "";
			String strPaycycle = "";
			while(rs.next()) {
				nEmpId = uF.parseToInt(rs.getString("emp_id"));
				
				strPaycycleFrom = uF.getDateFormat(rs.getString("paycycle_from"), DBDATE, DATE_FORMAT);;
				strPaycycleTo = uF.getDateFormat(rs.getString("paycycle_to"), DBDATE, DATE_FORMAT);
				strPaycycle = rs.getString("paycycle");
			}
			rs.close();
			pst.execute();
			
			boolean arrearDaysFlag = false;
			String strPaycycleMsg = "";
			if(nArrearType == 1) {
				pst = con.prepareStatement("select * from arear_details where emp_id=? and arrear_type=1 and arear_id !=? and paycycle_from=? and paycycle_to=? and paycycle=?");
				pst.setInt(1, nEmpId);
				pst.setInt(2, uF.parseToInt(getStrArrearId()));
				pst.setDate(3, uF.getDateFormat(paycycyle_from, DATE_FORMAT));
				pst.setDate(4, uF.getDateFormat(paycycyle_to, DATE_FORMAT));
				pst.setInt(5, nPaycycle);
				rs = pst.executeQuery();
				while(rs.next()) {
					arrearDaysFlag = true;
				}
				rs.close();
				pst.close();
				
				if(arrearDaysFlag) {
					strPaycycleMsg = nPaycycle+", "+paycycyle_from+" - "+paycycyle_to;
				}
				
				pst = con.prepareStatement("select * from arrear_generation where emp_id=? and arear_id in (select arear_id from arear_details where " +
					"emp_id=? and arrear_type=1 and arear_id=?)");
				pst.setInt(1, nEmpId);
				pst.setInt(2, nEmpId);
				pst.setInt(3, uF.parseToInt(getStrArrearId()));
				rs = pst.executeQuery();
				while(rs.next()) {
					arrearDaysFlag = true;
					
					strPaycycleMsg = strPaycycle+", "+strPaycycleFrom+" - "+strPaycycleTo;
				}
				rs.close();
				pst.close();
			}
			
			if(!arrearDaysFlag) {
				pst = con.prepareStatement("update arear_details set arear_amount=?, effective_date=?, duration_months=?, user_id=?, entry_date=?, " +
					"arear_description=?, monthly_arear=?, arear_code=?, arear_name=?,basic_amount=?,arear_amount_balance  = arear_amount - total_amount_paid, " +
					"arrear_type=?,arrear_days=?,paycycle_from=?,paycycle_to=?,paycycle=? where arear_id = ?");
				pst.setDouble(1, arrearAmount);
				pst.setDate(2, uF.getDateFormat(strEffectiveDate, DATE_FORMAT));
	//			pst.setInt(3, uF.parseToInt(getStrArearDuration()));
				pst.setInt(3, 1);
				pst.setInt(4, uF.parseToInt((String)session.getAttribute(EMPID)));
				pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setString(6, getStrArearDesc());
				pst.setDouble(7, arrearAmount);
				pst.setString(8, getStrArearCode());
				pst.setString(9, getStrArearName());
				pst.setDouble(10, dblBasic);
				pst.setInt(11, nArrearType);
				pst.setDouble(12, dblArrearDays);
				pst.setDate(13, uF.getDateFormat(paycycyle_from, DATE_FORMAT));
				pst.setDate(14, uF.getDateFormat(paycycyle_to, DATE_FORMAT));
				pst.setInt(15, nPaycycle);
				pst.setInt(16, uF.parseToInt(getStrArrearId()));
	//			System.out.println("pst===>"+pst);
				pst.execute();
				
				pst = con.prepareStatement("update arear_details set arear_amount_balance = arear_amount - total_amount_paid where arear_id = ?");
				pst.setInt(1, uF.parseToInt(getStrArrearId()));
				
	//			System.out.println("pst===>"+pst);
				pst.execute();
				pst.close();
			}
			
			if(arrearDaysFlag) {
				String strEmpName = CF.getEmpNameMapByEmpId(con, ""+nEmpId);
				String strMsg = strEmpName+" arrears are already applied for this Paycycle "+strPaycycleMsg;
				session.setAttribute(MESSAGE, ERRORM+strMsg+END);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	

	public void getArearEmployeeList(UtilityFunctions uF, String f_org, String f_WLocation, String f_department, String f_service, String f_level) {
		
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		List<List<String>> alEmpList = new ArrayList<List<String>>();
		
		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			StringBuilder sbQuery =  new StringBuilder();
			sbQuery.append("select * from employee_official_details eod, employee_personal_details epd where eod.emp_id = epd.emp_per_id and is_alive = true ");
			if(uF.parseToInt(f_org)>0) {
				sbQuery.append(" and org_id ="+uF.parseToInt(f_org));
			}
			if(uF.parseToInt(f_WLocation)>0) {
				sbQuery.append(" and wlocation_id ="+uF.parseToInt(f_WLocation));
			}
			if(uF.parseToInt(f_department)>0) {
				sbQuery.append(" and depart_id ="+uF.parseToInt(f_department));
			}
			if(uF.parseToInt(f_service)>0) {
				sbQuery.append(" and service_id like '%,"+uF.parseToInt(f_service)+",%'");
			}
			if(uF.parseToInt(f_level)>0) {
				sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id = "+uF.parseToInt(f_level)+")");
			}
			sbQuery.append(" order by emp_fname, emp_lname ");
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();
			
			while(rs.next()) {
				List<String> alInner = new ArrayList<String>();
				alInner.add(rs.getString("emp_per_id"));
				String empCode = "";
				String empMName = "";
				if (rs.getString("empcode") != null && rs.getString("empcode").length() > 0) {
					empCode = " [" +rs.getString("empcode") + "]";
				}
				
				/*if (rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length() > 0) {
					empMName = " " +rs.getString("emp_mname");
				}*/
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				
				alInner.add(rs.getString("emp_fname") + strEmpMName +" "+rs.getString("emp_lname") + empCode);
				alEmpList.add(alInner);
			}
			rs.close();
			pst.close();
			
			request.setAttribute("alEmpList", alEmpList);
			
		} catch (Exception e) {
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

	public String[] getStrArrearEmpId() {
		return strArrearEmpId;
	}

	public void setStrArrearEmpId(String[] strArrearEmpId) {
		this.strArrearEmpId = strArrearEmpId;
	}

	public String getStrArearCode() {
		return strArearCode;
	}

	public void setStrArearCode(String strArearCode) {
		this.strArearCode = strArearCode;
	}

	public String getStrArearName() {
		return strArearName;
	}

	public void setStrArearName(String strArearName) {
		this.strArearName = strArearName;
	}

	public String getStrArearDesc() {
		return strArearDesc;
	}

	public void setStrArearDesc(String strArearDesc) {
		this.strArearDesc = strArearDesc;
	}

	public String getStrArearAmount() {
		return strArearAmount;
	}

	public void setStrArearAmount(String strArearAmount) {
		this.strArearAmount = strArearAmount;
	}

	public String getStrArearDuration() {
		return strArearDuration;
	}

	public void setStrArearDuration(String strArearDuration) {
		this.strArearDuration = strArearDuration;
	}

	public String getStrArearEffectiveDate() {
		return strArearEffectiveDate;
	}

	public void setStrArearEffectiveDate(String strArearEffectiveDate) {
		this.strArearEffectiveDate = strArearEffectiveDate;
	}

	public String getStrArrearId() {
		return strArrearId;
	}

	public void setStrArrearId(String strArrearId) {
		this.strArrearId = strArrearId;
	}

	public String getStrBasic() {
		return strBasic;
	}

	public void setStrBasic(String strBasic) {
		this.strBasic = strBasic;
	}

	public String getArrearType() {
		return arrearType;
	}

	public void setArrearType(String arrearType) {
		this.arrearType = arrearType;
	}

	public String getStrArrearDays() {
		return strArrearDays;
	}

	public void setStrArrearDays(String strArrearDays) {
		this.strArrearDays = strArrearDays;
	}

	public String getArrearPaycycle() {
		return arrearPaycycle;
	}

	public void setArrearPaycycle(String arrearPaycycle) {
		this.arrearPaycycle = arrearPaycycle;
	}
	
}
