package com.konnect.jpms.ajax;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UserAlerts;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class SetGratuityAmount extends ActionSupport implements IStatements,ServletRequestAware {

	String strUserType;  
	String strSessionEmpId;
	
	private String strEmpId;
	private String strAmount;
	private String strActualAmount;
	String paycycle;

	HttpSession session;
	CommonFunctions CF;
	
	String isFullAndFinal;

	public String execute() throws Exception {

		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null) return LOGIN;
		
		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId =  (String)session.getAttribute(EMPID);

		setGratuityAmount();

		return SUCCESS;

	}

	public void setGratuityAmount() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		if (getPaycycle()==null) {
			return;
		}
		
		if (uF.parseToDouble(getStrActualAmount()) < uF.parseToDouble(getStrAmount())) {
			return;
		}

		try {
			

			if (getStrAmount() != null && uF.parseToInt(getStrAmount()) > 0) {
				con = db.makeConnection(con);

				String[] strPayCycleDates = getPaycycle().split("-");
				Map<String, String> hmEmpCurrency = CF.getEmpCurrency(con);
								
				pst = con.prepareStatement("insert into emp_gratuity_details(emp_id,gratuity_amount,paid_from,paid_to,paycycle,added_by,entry_date," +
						"currency_id,is_fullandfinal) values(?,?,?,?, ?,?,?,?, ?)");
				pst.setInt(1, uF.parseToInt(getStrEmpId()));
				pst.setDouble(2, uF.parseToDouble(getStrAmount()));
				pst.setDate(3,uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
				pst.setDate(4,uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
				pst.setInt(5, uF.parseToInt(strPayCycleDates[2]));
				pst.setInt(6, uF.parseToInt(strSessionEmpId));
				pst.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setInt(8, uF.parseToInt(hmEmpCurrency.get(strEmpId)));
				pst.setBoolean(9, uF.parseToBoolean(getIsFullAndFinal()));
//				System.out.println("pst======>"+pst);
				int x = pst.executeUpdate();
	            pst.close();
				
				if(x > 0){
					if(uF.parseToInt(getStrEmpId()) > 0){
						String strDomain = request.getServerName().split("\\.")[0];
						UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
						userAlerts.setStrDomain(strDomain);
						userAlerts.setStrEmpId(getStrEmpId());
						userAlerts.set_type(PAY_GRATUITY);
						userAlerts.setStatus(INSERT_ALERT);
						Thread t = new Thread(userAlerts);
						t.run();
					}
				}
			}

			request.setAttribute("GratuityAmount", getStrAmount());
			request.setAttribute("EMPID", getStrEmpId());

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public String getStrEmpId() {
		return strEmpId;
	}

	public void setStrEmpId(String strEmpId) {
		this.strEmpId = strEmpId;
	}

	public String getStrAmount() {
		return strAmount;
	}

	public void setStrAmount(String strAmount) {
		this.strAmount = strAmount;
	}

	public String getStrActualAmount() {
		return strActualAmount;
	}

	public void setStrActualAmount(String strActualAmount) {
		this.strActualAmount = strActualAmount;
	}

	public String getPaycycle() {
		return paycycle;
	}

	public void setPaycycle(String paycycle) {
		this.paycycle = paycycle;
	}

	public String getIsFullAndFinal() {
		return isFullAndFinal;
	}

	public void setIsFullAndFinal(String isFullAndFinal) {
		this.isFullAndFinal = isFullAndFinal;
	}

}