package com.konnect.jpms.export.ecr;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class EPFECRExport implements ServletRequestAware, ServletResponseAware, IStatements {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	HttpSession session;
	HttpServletRequest request;
	HttpServletResponse response;
	CommonFunctions CF = null;
	
	public void execute() throws Exception {
		session = request.getSession();
		
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return;
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		String TILDA  = "#~#";
		
		
		
		
		try {
			
			
			String strFinancialYear = (String)request.getParameter("financialYear");
			String strMonth = (String)request.getParameter("month");
			
			String []arrFinancialYear = null;
			if(strFinancialYear!=null){
				arrFinancialYear = strFinancialYear.split("-");
			}
			
			
			con = db.makeConnection(con);
			
			Map<String, Map<String, String>> hmEmpInfo = CF.getEmpInfoMap(con, true);
//			StringBuilder sb = new StringBuilder();
			StringBuffer sb = new StringBuffer();
			
			pst = con.prepareStatement("select * from emp_epf_details where financial_year_start = ? and financial_year_end = ? and _month in (?) ");
			pst.setDate(1, uF.getDateFormat(arrFinancialYear[0], DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(arrFinancialYear[1], DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(strMonth));
			rs = pst.executeQuery();
			Map<String, String> hmEEPF = new HashMap<String, String>();
			Map<String, String> hmERPF = new HashMap<String, String>();
			List<String> alEmployees = new ArrayList<String>();
			while(rs.next()){
				
				double dblAmount = uF.parseToDouble(hmERPF.get(rs.getString("emp_id")));
				dblAmount += uF.parseToDouble(rs.getString("erps_contribution"));
				hmERPF.put(rs.getString("emp_id"), uF.formatIntoZeroWithOutComma(dblAmount));
				hmERPF.put(rs.getString("emp_id")+"_MAX", uF.formatIntoZeroWithOutComma(uF.parseToDouble(rs.getString("eps_max_limit"))));
				
				

				dblAmount = uF.parseToDouble(hmEEPF.get(rs.getString("emp_id")));
				dblAmount += uF.parseToDouble(rs.getString("eepf_contribution"));
				hmEEPF.put(rs.getString("emp_id"), uF.formatIntoZeroWithOutComma(dblAmount));
				hmEEPF.put(rs.getString("emp_id")+"_MAX", uF.formatIntoZeroWithOutComma(uF.parseToDouble(rs.getString("epf_max_limit"))));
				
				
				if(!alEmployees.contains(rs.getString("emp_id"))){
					alEmployees.add(rs.getString("emp_id"));
				}
			}
			rs.close();
			pst.close();
			
			
			
			for(int i=0; i<alEmployees.size(); i++){
			
				Map<String, String> hmInfoInner = (Map)hmEmpInfo.get(alEmployees.get(i));
						
				double dblEPF = uF.parseToDouble(hmEEPF.get(alEmployees.get(i)));
				double dblEPS = uF.parseToDouble(hmERPF.get(alEmployees.get(i)));
				
				double dblEPFMAX = uF.parseToDouble(hmEEPF.get(alEmployees.get(i)+"_MAX"));
				double dblEPSMAX = uF.parseToDouble(hmERPF.get(alEmployees.get(i)+"_MAX"));
				
				String []arr = null;
				if(hmInfoInner.get("EPFNO")!=null){
					arr = hmInfoInner.get("EPFNO").split("/");
				}
				
				
				if(arr!=null && arr.length>0){
					sb.append(arr[arr.length-1]); //01 - Member ID (character (7))
					sb.append(TILDA);
				}else{
					sb.append(""); //01 - Member ID (character (7))
					sb.append(TILDA);
				}
				
				
				
				sb.append(uF.showData(hmInfoInner.get("FNAME"), "")); //02 - Member Name (character (85))
				sb.append(TILDA);
				
				sb.append(uF.formatIntoZeroWithOutComma(dblEPFMAX)); //03 - EPF Wages (Number (10))
				sb.append(TILDA);
				
				sb.append(uF.formatIntoZeroWithOutComma(dblEPSMAX)); //04 - EPS Wages (Number (10))
				sb.append(TILDA);
				
				sb.append(uF.formatIntoZeroWithOutComma(dblEPF)); //05 - EPF contribution (EE Share) due (Number (10))
				sb.append(TILDA);
				
				sb.append(uF.formatIntoZeroWithOutComma(dblEPF)); //06 - EPF contribution (EE Share) being remitted (Number (10))
				sb.append(TILDA);
				
				sb.append(uF.formatIntoZeroWithOutComma(dblEPS)); //07 - EPS contribution due (Number (10))
				sb.append(TILDA);
				
				sb.append(uF.formatIntoZeroWithOutComma(dblEPS)); //08 - EPS contribution being remitted (Number (10))
				sb.append(TILDA);
				
				sb.append(uF.formatIntoZeroWithOutComma(dblEPF-dblEPS)); //09 - Diff EPF and EPS contribution (ER Share) due (Number (10))
				sb.append(TILDA);
				
				sb.append(uF.formatIntoZeroWithOutComma(dblEPF-dblEPS)); //10 - Diff EPF and EPS contribution (ER Share) being remitted (Number (10))
				sb.append(TILDA);
				
				sb.append("0"); //11 - NCP Days (Number (2))
				sb.append(TILDA);
				
				sb.append("0"); //12 - Refund of advances (Number (10))
				sb.append(TILDA);
				
				sb.append("0"); //13 - Arrear EPF Wages (Number (10))
				sb.append(TILDA);
				
				sb.append("0"); //14 - Arrear EPF EE Share (Number (10))
				sb.append(TILDA);
				
				sb.append(""); //15 - Arrear EPF ER Share (Number (10))
				sb.append(TILDA);
				
				sb.append(""); //16 - Arrear EPS Share (Number (10))
				sb.append(TILDA);
				
				
				if(strMonth!=null && strMonth.indexOf(uF.getDateFormat(hmInfoInner.get("JOINING_DATE"), DBDATE, "MM"))>=0){
					
					if("F".equalsIgnoreCase(hmInfoInner.get("GENDER")) && "M".equalsIgnoreCase(hmInfoInner.get("MARITAL_STATUS"))){
						sb.append(uF.showData(hmInfoInner.get("SIBLING"), "")); //17 - Father's/Husband Name (character (85))
						sb.append(TILDA);
						
						sb.append("S"); //18 - Relation with member (character (85))
						sb.append(TILDA);
					}else{
						sb.append(uF.showData(hmInfoInner.get("FATHER"), "")); //17 - Father's/Husband Name (character (85))
						sb.append(TILDA);
						
						sb.append("F"); //18 - Relation with member (character (85))
						sb.append(TILDA);
					}
					
					
					sb.append(uF.getDateFormat(hmInfoInner.get("DOB"), DBDATE, DATE_FORMAT)); //19 - Date of Birth (date (10))
					sb.append(TILDA);
					
					sb.append(uF.showData(hmInfoInner.get("GENDER"), "")); //20 - Gender (character (1))
					sb.append(TILDA);
					
				}else{
					sb.append(""); //17 - Father's/Husband Name (character (85))
					sb.append(TILDA);
					
					sb.append(""); //18 - Relation with member (character (85))
					sb.append(TILDA);
					
					sb.append(""); //19 - Date of Birth (date (10))
					sb.append(TILDA);
					
					sb.append(""); //20 - Gender (character (1))
					sb.append(TILDA);
				}
				
				sb.append(""); //21 - Date of joining EPF (date (10))
				sb.append(TILDA);
				
				sb.append(""); //22 - Date of joining EPS (date (10))
				sb.append(TILDA);
				
				sb.append(""); //23 - Date of exit from EPF (date (10))
				sb.append(TILDA);
				
				sb.append(""); //24 - Date of exit from EPS (date (10))
				sb.append(TILDA);
				
				sb.append(""); //25 - Reason for leaving (character (1))
				sb.append(TILDA);
				
				sb.append(System.getProperty("line.separator"));
				
			}
			
			publishReport(sb.toString(), uF, strMonth);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	public void publishReport(String strData, UtilityFunctions uF, String strMonth) {
		try {

			String strMonth1 = uF.getDateFormat(strMonth+"", "MM", "MMM");
			String strYear = uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, "yy");
			
			ServletOutputStream op = response.getOutputStream();
			response.setContentType("application/octet-stream");
			response.setContentLength((int) strData.length());
			response.setHeader("Content-Disposition", "attachment; filename=\"" + "EPF_ECR_"+strMonth1+"_"+strYear+".txt" + "\"");
			op.write(strData.getBytes());
			op.flush();
			op.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setServletRequest(HttpServletRequest arg0) {
		request = arg0;
	}

	@Override
	public void setServletResponse(HttpServletResponse arg0) {
		response = arg0;
	}

}
