package com.konnect.jpms.reports.master;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillSection;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class InvestmentDetails extends ActionSupport implements ServletRequestAware, IStatements {
 
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	List<FillSection> sectionList;
	CommonFunctions CF=null;
	HttpSession session;
	String strUserType = null;
	String strSessionEmpId = null; 
	
	String emp_id;
	String fy_from;
	String fy_to;
	
	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		strUserType = (String)session.getAttribute(USERTYPE);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		
		viewInvestment();		
		return loadInvestment();
	}
	
	
	public String loadInvestment(){	
		return "load";
	}
	
	public String viewInvestment(){
		
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs= null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {

			con = db.makeConnection(con);
			
			String slabType = CF.getEmpIncomeTaxSlabType(con, CF, getEmp_id(), uF.getDateFormat(getFy_from(), DBDATE, DATE_FORMAT), uF.getDateFormat(getFy_to(), DBDATE, DATE_FORMAT));
			request.setAttribute("slabType", slabType);
//			Map<String, String> hmEmpSlabMap = CF.getEmpSlabMap(con, CF);
//			request.setAttribute("hmEmpSlabMap", hmEmpSlabMap);
//			String slabType = hmEmpSlabMap.get(getEmp_id());
			
			Map<String, String> hmSectionMap = CF.getSectionMap(con,uF.getDateFormat(getFy_from(), DBDATE, DATE_FORMAT),uF.getDateFormat(getFy_to(), DBDATE, DATE_FORMAT));
			Map<String, String> hmEmployeeNameMap = CF.getEmpNameMap(con, null, null);
			
			pst = con.prepareStatement("select * from form16_documents where financial_year_start=? and financial_year_end=? and emp_id=?");
			pst.setDate(1, uF.getDateFormat(getFy_from(), DBDATE));
			pst.setDate(2, uF.getDateFormat(getFy_to(), DBDATE));
			pst.setInt(3, uF.parseToInt(getEmp_id()));
//			System.out.println("pst=====>"+pst);
			rs = pst.executeQuery();
			boolean flag = false;
			while(rs.next()){
				flag = true;
			}
			rs.close();
			pst.close();
			request.setAttribute("isApproveRelease", ""+flag);
			
			pst = con.prepareStatement("select * from investment_documents where emp_id=? and fy_from =? and fy_to =? and section_id >0 order by section_id");
			pst.setInt(1, uF.parseToInt(getEmp_id()));
			pst.setDate(2, uF.getDateFormat(getFy_from(), DBDATE));
			pst.setDate(3, uF.getDateFormat(getFy_to(), DBDATE));
			rs = pst.executeQuery();			
			Map<String, List<String>> hmSectionDetails = new HashMap<String, List<String>>();
			List<String> alSection = new ArrayList<String>();			
//			System.out.println("pst===>"+pst);
			String strSectionNew = null;
			String strSectionOld = null;
			while(rs.next()){
				strSectionNew = rs.getString("section_id");
				if(strSectionNew!=null && !strSectionNew.equalsIgnoreCase(strSectionOld)){
					alSection = new ArrayList<String>();
				}
				alSection.add(rs.getString("document_name"));
				hmSectionDetails.put(strSectionNew, alSection);
				strSectionOld = strSectionNew;
			}	
			rs.close();
			pst.close();		
//			System.out.println("hmSectionDetails===>"+hmSectionDetails);
			Map<String, String> hmEmpCurrency = CF.getEmpCurrency(con);
			if(hmEmpCurrency == null) hmEmpCurrency = new HashMap<String, String>();
			Map<String, Map<String, String>> hmCurrencyDetailsMap = CF.getCurrencyDetailsForPDF(con);
			if(hmCurrencyDetailsMap == null) hmCurrencyDetailsMap = new HashMap<String, Map<String, String>>();
			String strCurrency = "";
			if(uF.parseToInt(hmEmpCurrency.get(getEmp_id())) > 0){
				Map<String, String> hmCurrency = hmCurrencyDetailsMap.get(hmEmpCurrency.get(getEmp_id()));
				if(hmCurrency == null) hmCurrency = new HashMap<String, String>();
				strCurrency = uF.showData(hmCurrency.get("SHORT_CURR"), ""); 
			} 
			
//			pst = con.prepareStatement("select * from investment_details where fy_from =? and fy_to =? and emp_id=? and trail_status = 1  and parent_section =0");
			pst=con.prepareStatement("select sd.under_section,id.* from investment_details id,section_details sd where id.section_id = sd.section_id " +
					"and id.fy_from =? and id.fy_to =? and id.emp_id=? and id.trail_status = 1 and id.parent_section =0 " +
					"and sd.financial_year_start=? and sd.financial_year_end=? and (sd.slab_type=? or sd.slab_type=2) order by sd.under_section");
			pst.setDate(1, uF.getDateFormat(getFy_from(), DBDATE));
			pst.setDate(2, uF.getDateFormat(getFy_to(), DBDATE)); 
			pst.setInt(3, uF.parseToInt(getEmp_id()));
			pst.setDate(4, uF.getDateFormat(getFy_from(), DBDATE));
			pst.setDate(5, uF.getDateFormat(getFy_to(), DBDATE));
			pst.setInt(6, uF.parseToInt(slabType));
//			System.out.println("pst 1 ==>"+pst);
			rs = pst.executeQuery();			
			StringBuilder sb = new StringBuilder();
			StringBuilder sbSectionDocs = new StringBuilder();
			List<String> alInvestmentInner = new ArrayList<String>();
			List<List<String>> alInvestment = new ArrayList<List<String>>();
			
			int nCount = 0;
			double dblTotal = 0;
			while(rs.next()){
				if(uF.parseToInt(rs.getString("trail_status"))>0 || uF.parseToInt(rs.getString("approved_by"))>0){
					alInvestmentInner = new ArrayList<String>();
					List alSectionId = (List)hmSectionDetails.get(rs.getString("section_id"));					
					alInvestmentInner.add(hmSectionMap.get(rs.getString("section_id")));
					alInvestmentInner.add(strCurrency+rs.getString("amount_paid"));
				
					if(uF.parseToInt(rs.getString("denied_by"))>0){
						 /*alInvestmentInner.add("<img src=\"images1/icons/denied.png\">");*/
						alInvestmentInner.add("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#e22d25\"></i>");
						
					}else if(uF.parseToBoolean(rs.getString("status"))){
						 /*alInvestmentInner.add("<a href=\"javascript:void(0)\" onclick=\"(confirm('Are you sure you want to disapprove this amount?')?getContent('myDiv_"+nCount+"', 'UpdateInvestment.action?status=false&investment_id="+rs.getString("investment_id")+"'):'')\"><img src=\"images1/icons/approved.png\"></a>");*/
						alInvestmentInner.add("<a href=\"javascript:void(0);\" onclick=\"(confirm('Are you sure you want to disapprove this amount?')?getContent('myDiv_"+nCount+"', 'UpdateInvestment.action?status=false&investment_id="+rs.getString("investment_id")+"'):'')\" ><i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\"></i></a>");
					}else{
						/*alInvestmentInner.add("<a href=\"javascript:void(0)\" onclick=\"(confirm('Are you sure you want to approve this amount?')?getContent('myDiv_"+nCount+"', 'UpdateInvestment.action?status=true&investment_id="+rs.getString("investment_id")+"'):'')\"><img src=\"images1/icons/approved.png\"></a>"*/
						alInvestmentInner.add("<a href=\"javascript:void(0);\" onclick=\"(confirm('Are you sure you want to approve this amount?')?getContent('myDiv_"+nCount+"', 'UpdateInvestment.action?status=true&investment_id="+rs.getString("investment_id")+"'):'')\"><i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\"></i></a>" +
								/*"&nbsp;<a href=\"javascript:void(0)\" onclick=\"(confirm('Are you sure you want to disapprove this amount?')?getContent('myDiv_"+nCount+"', 'UpdateInvestment.action?status=false&investment_id="+rs.getString("investment_id")+"'):'')\"><img src=\"images1/icons/denied.png\"></a>");*/
								"&nbsp;<a href=\"javascript:void(0);\" onclick=\"(confirm('Are you sure you want to disapprove this amount?')?getContent('myDiv_"+nCount+"', 'UpdateInvestment.action?status=false&investment_id="+rs.getString("investment_id")+"'):'')\" > <i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#e22d25\"></i></a>");
					}
					
					sbSectionDocs.replace(0, sbSectionDocs.length(), "");
					for(int i=0; alSectionId!=null && i<alSectionId.size(); i++){
//						if(CF.getStrDocRetriveLocation()!=null){
//							sbSectionDocs.append("<a target=\"_blank\" title=\"Reference Document\" href=\""+CF.getStrDocRetriveLocation() +alSectionId.get(i)+"\"><img src=\"images1/payslip.png\"></a> ");
//						}else{
//							sbSectionDocs.append("<a target=\"_blank\" title=\"Reference Document\" href=\""+request.getContextPath()+DOCUMENT_LOCATION+alSectionId.get(i)+"\"><img src=\"images1/payslip.png\"></a> ");
//						}
						if(CF.getStrDocRetriveLocation()==null){
							sbSectionDocs.append("<a target=\"blank\" href=\"" +request.getContextPath()+ DOCUMENT_LOCATION + alSectionId.get(i) + "\" title=\"Reference Document\" ><i class=\"fa fa-file-o\" aria-hidden=\"true\"></i></a>");
						}else{
							sbSectionDocs.append("<a target=\"blank\" href=\""+CF.getStrDocRetriveLocation() + I_INVESTMENTS+"/"+I_DOCUMENT+"/"+getEmp_id() +"/"+ alSectionId.get(i) + "\" title=\"Reference Document\" ><i class=\"fa fa-file-o\" aria-hidden=\"true\"></i></a>");
						}
					}					
					alInvestmentInner.add(sbSectionDocs.toString());
					
					
					if(uF.parseToInt(rs.getString("denied_by"))>0){
						sb.append("Disapproved by ");
						alInvestmentInner.add(sb.toString()+uF.showData(hmEmployeeNameMap.get(rs.getString("denied_by")), "")+" on "+uF.getDateFormat(rs.getString("denied_date"), DBDATE, CF.getStrReportDateFormat()));
					}else if(uF.parseToInt(rs.getString("approved_by"))>0){
						sb.append("Approved by ");
						alInvestmentInner.add(sb.toString()+uF.showData(hmEmployeeNameMap.get(rs.getString("approved_by")), "")+" on "+uF.getDateFormat(rs.getString("approved_date"), DBDATE, CF.getStrReportDateFormat()));
						dblTotal += uF.parseToDouble(rs.getString("amount_paid")); 
					}else{
						alInvestmentInner.add("");
						dblTotal += uF.parseToDouble(rs.getString("amount_paid"));
					}
					
					sb.replace(0, sb.length(), "");
					
					
					if(uF.parseToInt(rs.getString("denied_by"))>0){
						alInvestmentInner.add("-1");
					}else{
						alInvestmentInner.add("0");
					}
					alInvestmentInner.add(CF.getUnderSectionName(uF.parseToInt(rs.getString("under_section"))));
					alInvestmentInner.add(rs.getString("section_id"));
					
					alInvestmentInner.add(rs.getString("denied_by"));
					alInvestmentInner.add(rs.getString("status"));
					alInvestmentInner.add(rs.getString("investment_id"));
					
					alInvestment.add(alInvestmentInner);
					nCount++;
				} 
			}
			rs.close();
			pst.close();
//			System.out.println("dblTotal="+dblTotal+" ==>"+Math.round(dblTotal));
			
			Map<String, List<Map<String, String>>> hmSubInvestment = new HashMap<String, List<Map<String, String>>>();			
			pst=con.prepareStatement("select id.* from investment_details id,section_details sd where id.section_id = sd.section_id " +
				"and id.fy_from =? and id.fy_to =? and id.emp_id=? and id.trail_status = 1 and id.parent_section > 0 " +
				"and sd.financial_year_start=? and sd.financial_year_end=? and (sd.slab_type=? or sd.slab_type=2) order by sd.under_section");
			pst.setDate(1, uF.getDateFormat(getFy_from(), DBDATE));
			pst.setDate(2, uF.getDateFormat(getFy_to(), DBDATE));
			pst.setInt(3, uF.parseToInt(getEmp_id()));
			pst.setDate(4, uF.getDateFormat(getFy_from(), DBDATE));
			pst.setDate(5, uF.getDateFormat(getFy_to(), DBDATE));
			pst.setInt(6, uF.parseToInt(slabType));
//			System.out.println("pst 2 ==>"+pst);
			rs = pst.executeQuery();		
			while(rs.next()){
				List<Map<String, String>> alSubInvestment =hmSubInvestment.get(rs.getString("parent_section"));
				if(alSubInvestment ==null)alSubInvestment = new ArrayList<Map<String, String>>();
				
				Map<String, String> hm = new HashMap<String, String>();
				hm.put("SECTION_ID", rs.getString("parent_section"));
				hm.put("SECTION_NAME", rs.getString("child_section"));
				hm.put("INVESTMENT_ID", rs.getString("investment_id"));
				hm.put("PAID_AMOUNT", uF.showData(rs.getString("amount_paid"), "0"));
				hm.put("STATUS", rs.getString("status"));
				
				alSubInvestment.add(hm);
				
				hmSubInvestment.put(rs.getString("parent_section"), alSubInvestment);
			}
			rs.close();
			pst.close();
			request.setAttribute("hmSubInvestment", hmSubInvestment);
			request.setAttribute("alInvestment", alInvestment);
			
			
			/**
			 * other investment
			 * */
			
			Map<String, String> hmOtherSectionMap = new HashMap<String, String>();
			pst = con.prepareStatement("select * from exemption_details where exemption_from=? and exemption_to=? and (slab_type=? or slab_type=2) and investment_form=true");
			pst.setDate(1, uF.getDateFormat(getFy_from(), DBDATE));
			pst.setDate(2, uF.getDateFormat(getFy_to(), DBDATE));
			pst.setInt(3, uF.parseToInt(slabType));
			rs=pst.executeQuery();
			while(rs.next()){
				hmOtherSectionMap.put(rs.getString("salary_head_id"), rs.getString("exemption_code"));
			}
			rs.close();
			pst.close();
			request.setAttribute("hmOtherSectionMap", hmOtherSectionMap);
			
			pst = con.prepareStatement("select * from investment_documents where emp_id=? and fy_from =? and fy_to =? and salary_head_id >0 order by salary_head_id");
			pst.setInt(1, uF.parseToInt(getEmp_id()));
			pst.setDate(2, uF.getDateFormat(getFy_from(), DBDATE));
			pst.setDate(3, uF.getDateFormat(getFy_to(), DBDATE));
			rs = pst.executeQuery();			
			Map<String, List<String>> hmOtherSectionDetails = new HashMap<String, List<String>>();
			List<String> alOtherSection = new ArrayList<String>();			
//			System.out.println("pst===>"+pst);
			String strOtherSectionNew = null;
			String strOtherSectionOld = null;
			while(rs.next()){
				strOtherSectionNew = rs.getString("salary_head_id");
				if(strOtherSectionNew!=null && !strOtherSectionNew.equalsIgnoreCase(strOtherSectionOld)){
					alOtherSection = new ArrayList<String>();
				}
				alOtherSection.add(rs.getString("document_name"));
				hmOtherSectionDetails.put(strOtherSectionNew, alOtherSection);
				strOtherSectionOld = strOtherSectionNew;
			}	
			rs.close();
			pst.close();		
//			System.out.println("hmOtherSectionDetails===>"+hmOtherSectionDetails);
			
//			pst = con.prepareStatement("select * from investment_details where fy_from =? and fy_to =? and emp_id=? and trail_status = 1  and parent_section =0");
			pst=con.prepareStatement("select ed.under_section,id.* from investment_details id,exemption_details ed where id.salary_head_id = ed.salary_head_id " +
					"and id.fy_from =? and id.fy_to =? and id.emp_id=? and id.trail_status = 1 and id.parent_section =0 " +
					"and ed.exemption_from=? and ed.exemption_to=? and (ed.slab_type=? or ed.slab_type=2) and ed.investment_form=true order by ed.under_section");
			pst.setDate(1, uF.getDateFormat(getFy_from(), DBDATE));
			pst.setDate(2, uF.getDateFormat(getFy_to(), DBDATE));
			pst.setInt(3, uF.parseToInt(getEmp_id()));
			pst.setDate(4, uF.getDateFormat(getFy_from(), DBDATE));
			pst.setDate(5, uF.getDateFormat(getFy_to(), DBDATE));
			pst.setInt(6, uF.parseToInt(slabType));
//			System.out.println("pst 3 ==>"+pst);
			rs = pst.executeQuery();			
			StringBuilder sbOther = new StringBuilder();
			StringBuilder sbOtherSectionDocs = new StringBuilder();
			List<String> alOtherInvestmentInner = new ArrayList<String>();
			List<List<String>> alOtherInvestment = new ArrayList<List<String>>();
			
//			int nCount = 0;
//			double dblTotal = 0;
			while(rs.next()){
				if(uF.parseToInt(rs.getString("trail_status"))>0 || uF.parseToInt(rs.getString("approved_by"))>0){
					alOtherInvestmentInner = new ArrayList<String>();
					List alOtherSectionId = (List)hmOtherSectionDetails.get(rs.getString("salary_head_id"));					
					alOtherInvestmentInner.add(hmOtherSectionMap.get(rs.getString("salary_head_id")));
					alOtherInvestmentInner.add(strCurrency+rs.getString("amount_paid"));
				
					if(uF.parseToInt(rs.getString("denied_by"))>0){
						/*alOtherInvestmentInner.add("<img src=\"images1/icons/denied.png\">");*/	
						alOtherInvestmentInner.add("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#e22d25\"></i>");
						
					}else if(uF.parseToBoolean(rs.getString("status"))){
						/*alOtherInvestmentInner.add("<a href=\"javascript:void(0)\" onclick=\"(confirm('Are you sure you want to disapprove this amount?')?getContent('myDiv_"+nCount+"', 'UpdateInvestment.action?status=false&investment_id="+rs.getString("investment_id")+"'):'')\"><img src=\"images1/icons/approved.png\"></a>");*/
						alOtherInvestmentInner.add("<a href=\"javascript:void(0);\" onclick=\"(confirm('Are you sure you want to disapprove this amount?')?getContent('myDiv_"+nCount+"', 'UpdateInvestment.action?status=false&investment_id="+rs.getString("investment_id")+"'):'')\" ><i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\"></i> </a>");
						
					}else{
						 /*alOtherInvestmentInner.add("<a href=\"javascript:void(0)\" onclick=\"(confirm('Are you sure you want to approve this amount?')?getContent('myDiv_"+nCount+"', 'UpdateInvestment.action?status=true&investment_id="+rs.getString("investment_id")+"'):'')\"><img src=\"images1/icons/approved.png\"></a>" */
						alOtherInvestmentInner.add("<a href=\"javascript:void(0);\" onclick=\"(confirm('Are you sure you want to approve this amount?')?getContent('myDiv_"+nCount+"', 'UpdateInvestment.action?status=true&investment_id="+rs.getString("investment_id")+"'):'')\"> <i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\"></i> </a>" +
								/*"&nbsp;<a href=\"javascript:void(0)\" onclick=\"(confirm('Are you sure you want to disapprove this amount?')?getContent('myDiv_"+nCount+"', 'UpdateInvestment.action?status=false&investment_id="+rs.getString("investment_id")+"'):'')\"><img src=\"images1/icons/denied.png\"></a>");*/
								"&nbsp;<a href=\"javascript:void(0);\" onclick=\"(confirm('Are you sure you want to disapprove this amount?')?getContent('myDiv_"+nCount+"', 'UpdateInvestment.action?status=false&investment_id="+rs.getString("investment_id")+"'):'')\" > <i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#e22d25\"></i> </a>");
					}
					
					sbOtherSectionDocs.replace(0, sbOtherSectionDocs.length(), "");
					for(int i=0; alOtherSectionId!=null && i<alOtherSectionId.size(); i++){
//						if(CF.getIsRemoteLocation()){
//							sbOtherSectionDocs.append("<a target=\"_blank\" title=\"Reference Document\" href=\""+CF.getStrDocRetriveLocation() +alOtherSectionId.get(i)+"\"><img src=\"images1/payslip.png\"></a> ");
//						}else{
//							sbOtherSectionDocs.append("<a target=\"_blank\" title=\"Reference Document\" href=\""+request.getContextPath()+DOCUMENT_LOCATION+alOtherSectionId.get(i)+"\"><img src=\"images1/payslip.png\"></a> ");
//							
//						}
						if(CF.getStrDocRetriveLocation()==null){
							sbOtherSectionDocs.append("<a target=\"blank\" href=\"" +request.getContextPath()+ DOCUMENT_LOCATION + alOtherSectionId.get(i) + "\" title=\"Reference Document\" ><i class=\"fa fa-file-o\" aria-hidden=\"true\"></i></a>");
						}else{
							sbOtherSectionDocs.append("<a target=\"blank\" href=\""+CF.getStrDocRetriveLocation() + I_INVESTMENTS+"/"+I_DOCUMENT+"/"+getEmp_id() +"/"+ alOtherSectionId.get(i) + "\" title=\"Reference Document\" ><i class=\"fa fa-file-o\" aria-hidden=\"true\"></i></a>");
						}
					}					
					alOtherInvestmentInner.add(sbOtherSectionDocs.toString());
					
					
					if(uF.parseToInt(rs.getString("denied_by"))>0){
						sbOther.append("Disapproved by ");
						alOtherInvestmentInner.add(sbOther.toString()+uF.showData(hmEmployeeNameMap.get(rs.getString("denied_by")), "")+" on "+uF.getDateFormat(rs.getString("denied_date"), DBDATE, CF.getStrReportDateFormat()));
					}else if(uF.parseToInt(rs.getString("approved_by"))>0){
						sbOther.append("Approved by ");
						alOtherInvestmentInner.add(sbOther.toString()+uF.showData(hmEmployeeNameMap.get(rs.getString("approved_by")), "")+" on "+uF.getDateFormat(rs.getString("approved_date"), DBDATE, CF.getStrReportDateFormat()));
						dblTotal += uF.parseToDouble(rs.getString("amount_paid")); 
					}else{
						alOtherInvestmentInner.add("");
						dblTotal += uF.parseToDouble(rs.getString("amount_paid"));
					}
					
					sbOther.replace(0, sbOther.length(), "");
					
					
					if(uF.parseToInt(rs.getString("denied_by"))>0){
						alOtherInvestmentInner.add("-1");
					}else{
						alOtherInvestmentInner.add("0");
					}
					alOtherInvestmentInner.add(CF.getUnderSectionName(uF.parseToInt(rs.getString("under_section"))));
					alOtherInvestmentInner.add(rs.getString("salary_head_id"));
					
					alOtherInvestmentInner.add(rs.getString("denied_by"));
					alOtherInvestmentInner.add(rs.getString("status"));
					alOtherInvestmentInner.add(rs.getString("investment_id"));
					
					alOtherInvestment.add(alOtherInvestmentInner);
					nCount++;
				} 
			}
			rs.close();
			pst.close();
//			System.out.println("dblTotal="+dblTotal+" ==>"+Math.round(dblTotal));
			
			Map<String, List<Map<String, String>>> hmOtherSubInvestment = new HashMap<String, List<Map<String, String>>>();			
			pst=con.prepareStatement("select id.* from investment_details id,exemption_details ed where id.salary_head_id = ed.salary_head_id " +
				"and id.fy_from =? and id.fy_to =? and id.emp_id=? and id.trail_status = 1 and id.parent_section > 0 " +
				" and ed.exemption_from=? and ed.exemption_to=? and (ed.slab_type=? or ed.slab_type=2) and ed.investment_form=true order by ed.under_section");
			
			pst.setDate(1, uF.getDateFormat(getFy_from(), DBDATE));
			pst.setDate(2, uF.getDateFormat(getFy_to(), DBDATE));
			pst.setInt(3, uF.parseToInt(getEmp_id()));
			pst.setDate(4, uF.getDateFormat(getFy_from(), DBDATE));
			pst.setDate(5, uF.getDateFormat(getFy_to(), DBDATE));
			pst.setInt(6, uF.parseToInt(slabType));
//			System.out.println("pst 4 ==>"+pst);
			rs = pst.executeQuery();		
			while(rs.next()){
				List<Map<String, String>> alOtherSubInvestment =hmOtherSubInvestment.get(rs.getString("parent_section"));
				if(alOtherSubInvestment ==null)alOtherSubInvestment = new ArrayList<Map<String, String>>();
				
				Map<String, String> hmOther = new HashMap<String, String>();
				hmOther.put("SECTION_ID", rs.getString("parent_section"));
				hmOther.put("SECTION_NAME", rs.getString("child_section"));
				hmOther.put("INVESTMENT_ID", rs.getString("investment_id"));
				hmOther.put("PAID_AMOUNT", uF.showData(rs.getString("amount_paid"), "0"));
				hmOther.put("STATUS", rs.getString("status"));
				
				alOtherSubInvestment.add(hmOther);
				
				hmOtherSubInvestment.put(rs.getString("parent_section"), alOtherSubInvestment);
			}
			rs.close();
			pst.close();
			request.setAttribute("hmOtherSubInvestment", hmOtherSubInvestment);
			request.setAttribute("alOtherInvestment", alOtherInvestment);
			
			
			
			request.setAttribute("uF", uF);
			request.setAttribute("TOTAL_INVESTMENT", strCurrency+uF.formatIntoComma(Math.round(dblTotal)));
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;
		
	}

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public String getEmp_id() {
		return emp_id;
	}

	public void setEmp_id(String emp_id) {
		this.emp_id = emp_id;
	}

	public String getFy_from() {
		return fy_from;
	}

	public void setFy_from(String fy_from) {
		this.fy_from = fy_from;
	}

	public String getFy_to() {
		return fy_to;
	}

	public void setFy_to(String fy_to) {
		this.fy_to = fy_to;
	}
}