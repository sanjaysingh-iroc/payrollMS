package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class ProfitabilityByWLocation extends ActionSupport implements ServletRequestAware, IStatements{
	HttpServletRequest request;
	HttpSession session;
	CommonFunctions CF;

	String strUserType;
	
	public String execute(){
		session = request.getSession();
		
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		strUserType = (String)session.getAttribute(BASEUSERTYPE);
		
		
		request.setAttribute(PAGE, "/jsp/task/ProfitabilityByWLocation.jsp");
		request.setAttribute(TITLE, "Profitability By Work Location");
		getProjectDetailsBYLocation();
		return SUCCESS;
	}
	
	String f_start;
	String f_end;
	
	public void getProjectDetailsBYLocation(){

		UtilityFunctions uF=new UtilityFunctions();
				
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs=null;
		try {
			con = db.makeConnection(con);
			
			
			Map<String, String> hmReimbursementAmountMap = CF.getReimbursementAmount(con, "P", "WL", true, getF_start(), getF_end(), uF);
			
			pst = con.prepareStatement("  ");
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from(select sum(a.variable_cost) as variable_cost, sum(a.billable_amount)as billable_amount, sum(a.budgeted_cost) as budgeted_cost,  sum(a.already_work) as already_work, sum(a.actual_amount) as actual_amount,sum(a.actual_hrs) as actual_hrs,sum(a.idealtime) as idealtime,pmntc.wlocation_id  from (select pc.*,pt.actual_hrs,pt.idealtime from project_cost pc LEFT JOIN project_time pt ON pc.pro_id=pt.pro_id ) as a, projectmntnc pmntc where pmntc.pro_id=a.pro_id ");					
			if(getF_start()!=null && getF_end()!=null){
				sbQuery.append(" and start_date between '"+uF.getDateFormat(getF_start(), DATE_FORMAT, DBDATE)+"' and '"+uF.getDateFormat(getF_end(), DATE_FORMAT, DBDATE)+"' ");
			}
			if(strUserType!=null && strUserType.equalsIgnoreCase(MANAGER)){
				sbQuery.append(" and added_by="+uF.parseToInt((String)session.getAttribute(EMPID)));
			}
			
			sbQuery.append(" group by pmntc.wlocation_id) as d,work_location_info wli where d.wlocation_id=wli.wlocation_id");
			pst = con.prepareStatement(sbQuery.toString());
			
			
//					System.out.println("pst==="+pst);
			
			rs=pst.executeQuery();
			List<List<String>> alOuter=new ArrayList<List<String>>();
			
			StringBuilder sbName = new StringBuilder();
			StringBuilder sbActual = new StringBuilder();
			StringBuilder sbBilled = new StringBuilder();
			StringBuilder sbProfit = new StringBuilder();
			StringBuilder sbProfitC = new StringBuilder();
			
			while(rs.next()) {
				
				double dblReimbursement = uF.parseToDouble(hmReimbursementAmountMap.get(rs.getString("wlocation_id")));
				
				double dblBillable=uF.parseToDouble(rs.getString("billable_amount"));
				double dblBudgeted=uF.parseToDouble(rs.getString("budgeted_cost"))+uF.parseToDouble(rs.getString("variable_cost"));
				double dblActual= uF.parseToDouble(rs.getString("actual_amount"))   + dblReimbursement;
				
				double diff = 0;
				
				if(dblBillable>0){
					diff = ((dblBillable-dblActual)/dblBillable) * 100;
				}
				
				List<String> alInner=new ArrayList<String>();
				alInner.add(rs.getString("wlocation_id"));
				alInner.add(rs.getString("wlocation_name"));
				alInner.add(uF.formatIntoOneDecimal(dblBillable));
				alInner.add(uF.formatIntoOneDecimal(dblActual));
				alInner.add(uF.formatIntoOneDecimal(diff)+"%");
				
				alOuter.add(alInner);
				
				sbName.append("'"+rs.getString("wlocation_name")+"'"+",");
				sbBilled.append(uF.formatIntoOneDecimal(dblBillable)+",");
				sbActual.append(uF.formatIntoOneDecimal(dblActual)+",");
				sbProfit.append(uF.formatIntoOneDecimal(diff)+",");
				
				sbProfitC.append("{name: '"+rs.getString("wlocation_name")+"',data: ["+uF.formatIntoOneDecimal(diff)+"]}"+",");
				
			}
			rs.close();
			pst.close();
			
			if(sbName.length()>1){
				sbName.replace(0, sbName.length(), sbName.substring(0, sbName.length()-1));
				sbBilled.replace(0, sbBilled.length(), sbBilled.substring(0, sbBilled.length()-1));
				sbActual.replace(0, sbActual.length(), sbActual.substring(0, sbActual.length()-1));
				sbProfit.replace(0, sbProfit.length(), sbProfit.substring(0, sbProfit.length()-1));
				sbProfitC.replace(0, sbProfitC.length(), sbProfitC.substring(0, sbProfitC.length()-1));
			}
			
			
			
			request.setAttribute("sbName", sbName.toString());
			request.setAttribute("sbBilled", sbBilled.toString());
			request.setAttribute("sbActual", sbActual.toString());
			request.setAttribute("sbProfit", sbProfit.toString());
			request.setAttribute("sbProfitC", sbProfitC.toString());
			
			request.setAttribute("alOuter",alOuter);

			
			

			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	
	}
	
	
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request=request;
	}

	public String getF_start() {
		return f_start;
	}

	public void setF_start(String f_start) {
		this.f_start = f_start;
	}

	public String getF_end() {
		return f_end;
	}

	public void setF_end(String f_end) {
		this.f_end = f_end;
	}
}
