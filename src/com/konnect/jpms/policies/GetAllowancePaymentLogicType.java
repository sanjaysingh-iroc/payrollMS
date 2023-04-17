package com.konnect.jpms.policies;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillAllowancePaymentLogic;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class GetAllowancePaymentLogicType extends ActionSupport implements ServletRequestAware, IStatements {
	
	private static final long serialVersionUID = 1L;
	
	CommonFunctions CF;
	HttpSession session;
	

	String strAllowancePaymentLogic;
	List<FillAllowancePaymentLogic> allowancePaymentLogicList;

	public String execute() {
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		UtilityFunctions uF = new UtilityFunctions();
		String strConditionId = request.getParameter("conditionId");
		
		if(uF.parseToInt(strConditionId) > 0){
			getAllowancePaymentLogicType(uF,strConditionId);
		} else {
			allowancePaymentLogicList = new ArrayList<FillAllowancePaymentLogic>();
		}
		
		return SUCCESS;

	}

	private void getAllowancePaymentLogicType(UtilityFunctions uF, String strConditionId) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("select allowance_condition from allowance_condition_details where allowance_condition_id=?");
			pst.setInt(1, uF.parseToInt(strConditionId));
			rs = pst.executeQuery();
			int nAllowanceCondition = 0;
			while(rs.next()){
				nAllowanceCondition = uF.parseToInt(rs.getString("allowance_condition"));
			}
			rs.close();
			pst.close();
			
			if(nAllowanceCondition > 0){
				allowancePaymentLogicList = new FillAllowancePaymentLogic().fillAllowancePaymentLogic(nAllowanceCondition);
			} else {
				allowancePaymentLogicList = new ArrayList<FillAllowancePaymentLogic>();
			}
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

	public String getStrAllowancePaymentLogic() {
		return strAllowancePaymentLogic;
	}

	public void setStrAllowancePaymentLogic(String strAllowancePaymentLogic) {
		this.strAllowancePaymentLogic = strAllowancePaymentLogic;
	}

	public List<FillAllowancePaymentLogic> getAllowancePaymentLogicList() {
		return allowancePaymentLogicList;
	}

	public void setAllowancePaymentLogicList(List<FillAllowancePaymentLogic> allowancePaymentLogicList) {
		this.allowancePaymentLogicList = allowancePaymentLogicList;
	}

}
