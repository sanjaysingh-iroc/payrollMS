package com.konnect.jpms.reports;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.LinkedHashMap;
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

public class Reports extends ActionSupport implements  IStatements, ServletRequestAware {
	
	private static final long serialVersionUID = -5846636523966720273L;
	HttpServletRequest request;
	private HttpSession session;
	CommonFunctions CF = null;
	String strUserType = null;


	public String execute(){
		
		session = request.getSession();  
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN; 
		strUserType = (String) session.getAttribute(USERTYPE);
		UtilityFunctions uF = new UtilityFunctions();
		
		request.setAttribute(TITLE, "Reports");
		request.setAttribute(PAGE, "/jsp/reports/Reports.jsp");
		
		if(strUserType==null || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && !strUserType.equalsIgnoreCase(ACCOUNTANT)
			&& !strUserType.equalsIgnoreCase(MANAGER) && !strUserType.equalsIgnoreCase(HRMANAGER) && !strUserType.equalsIgnoreCase(CEO))) {
			
			request.setAttribute(PAGE, PAccessDenied);
			request.setAttribute(TITLE, TAccessDenied); 
			return ACCESS_DENIED;
		}
		
		getReports(uF);
		return SUCCESS;
		
	}
	

	public String getReports(UtilityFunctions uF){
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		String productType = (String)session.getAttribute(PRODUCT_TYPE);
		String userId = (String)session.getAttribute(USERID);
		
		try {
			con=db.makeConnection(con);
			
			String navigationID="";
			StringBuilder sbQueryL = new StringBuilder();
			sbQueryL.append("select navigation_id from navigation_1 where ((_position = ? and user_type_id like ?)");
			sbQueryL.append(") and _type='N' and  _action='Reports.action' and (product_type = '1'");
			if(productType != null && !productType.equals("")) {
				sbQueryL.append(" or product_type = '" + productType + "'");
			}
			sbQueryL.append(") order by weight");
			pst = con.prepareStatement(sbQueryL.toString());
			pst.setString(1, "L");
			if(productType != null && productType.equals("3")) {
				pst.setString(2, "%,"+(String)session.getAttribute(BASEUSERTYPEID)+",%");
			} else {
				pst.setString(2, "%,"+(String)session.getAttribute(USERTYPEID)+",%");
			}
 		   System.out.println("pst=====>"+pst);
			rs = pst.executeQuery();
			while(rs.next()){
				navigationID=uF.showData(rs.getString("navigation_id"), "");
			}
			rs.close();
			pst.close();
			
			
			Map<String,String>hmParentLable=new LinkedHashMap<String,String>();
			Map<String,String>hmParentNavigation=new LinkedHashMap<String,String>();
			
			pst=con.prepareStatement("select navigation_id,_label,_action from navigation_1 where parent in ("+navigationID+") ");
			rs=pst.executeQuery();
			System.out.println("pst2===>"+pst);
			while(rs.next()){
				hmParentLable.put(uF.showData(rs.getString("navigation_id"), ""),uF.showData(rs.getString("_label"), ""));
				hmParentNavigation.put(uF.showData(rs.getString("navigation_id"), ""),uF.showData(rs.getString("_action"), ""));
				
			}
			rs.close();
			pst.close();
			
			Map<String,Map<String,List<String>>>hmParentChild=new LinkedHashMap<String,Map<String,List<String>>>();
			pst=con.prepareStatement("select parent,navigation_id,_label,_action " +
					"from navigation_1 where parent in(select navigation_id from navigation_1 where parent in ("+navigationID+")) order by parent");
			rs=pst.executeQuery();
			System.out.println("pst3==>"+pst);
			while(rs.next())
			{
				Map<String,List<String>>hmChild=hmParentChild.get(rs.getString("parent"));
				if(hmChild==null)hmChild=new LinkedHashMap<String,List<String>>();
				
				List<String>alLabelAction=hmChild.get(uF.showData(rs.getString("navigation_id"), ""));
				if(alLabelAction==null)alLabelAction=new ArrayList<String>();
				
				alLabelAction.add(uF.showData(rs.getString("_action"),""));
				alLabelAction.add(uF.showData(rs.getString("_label"),""));

				hmChild.put(uF.showData(rs.getString("navigation_id"), ""),alLabelAction);
				
				hmParentChild.put(uF.showData(rs.getString("parent"), ""), hmChild);
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmParentNavigation", hmParentNavigation);
			request.setAttribute("hmParentLable", hmParentLable);
			request.setAttribute("hmParentChild", hmParentChild);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return LOAD;
	}
	
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request=request;
		// TODO Auto-generated method stub
		
	}
	
	  
}
