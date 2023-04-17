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

public class ProjectOwnerDetails extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;

	CommonFunctions CF;
	HttpSession session;
	String strProjectOwner;
	
	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null) return LOGIN;
		
		UtilityFunctions uF=new UtilityFunctions();
		getProjectOwnerDetails(uF);
		return SUCCESS; 

	}
	
	private void getProjectOwnerDetails(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			Map<String, String> hmEmpCodeDesig =CF.getEmpDesigMap(con);
			Map<String, String> hmProjectOwnerDetails=new HashMap<String, String>();
			String ownerOrg="";
			String ownerEmail="";
			String ownerSign="";
			String locationTel="";
			String locationFax="";
			String orgPan="";
			String orgMCARegNo="";
			String orgSTRegNo="";
//			String locationPan="";
//			String locationRegNo="";
//			String locationECCNo1="";
//			String locationECCNo2="";
			
//			pst=con.prepareStatement("select * from org_details");
//			Map<String, Map<String, String>> hmOrg=new HashMap<String, Map<String, String>>();
//			rs=pst.executeQuery();
//			while(rs.next()){
//				Map<String, String> hmInner=new HashMap<String, String>();
//				hmInner.put("ORG_ID", rs.getString("org_id"));
//				hmInner.put("ORG_NAME", rs.getString("org_name"));
//				hmInner.put("ORG_LOGO", rs.getString("org_logo"));
//				hmInner.put("ORG_ADDRESS", rs.getString("org_address"));
//				hmInner.put("ORG_PINCODE", rs.getString("org_pincode"));
//				hmInner.put("ORG_CONTACT", rs.getString("org_contact1"));
//				hmInner.put("ORG_EMAIL", rs.getString("org_email"));
//				hmInner.put("ORG_STATE_ID", rs.getString("org_state_id"));
//				hmInner.put("ORG_COUNTRY_ID", rs.getString("org_country_id"));
//				hmInner.put("ORG_CITY", rs.getString("org_city"));
//				hmInner.put("ORG_CODE", rs.getString("org_code"));
//				
//				hmOrg.put(rs.getString("org_id"), hmInner);
//			}
//			rs.close();
//			pst.close();
			
			
			pst = con.prepareStatement("select * from employee_personal_details epd, employee_official_details eod " +
				"where eod.emp_id = epd.emp_per_id and eod.emp_id = ? ");
			pst.setInt(1, uF.parseToInt(getStrProjectOwner()));
//			System.out.println("pst ===>> " + pst);
			
			rs = pst.executeQuery();
			while (rs.next()) {
				

				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
			
				
				hmProjectOwnerDetails.put("EMP_NAME", rs.getString("emp_fname")+strEmpMName+" "+rs.getString("emp_lname"));
				hmProjectOwnerDetails.put("EMP_ORG_ID", rs.getString("org_id"));
				hmProjectOwnerDetails.put("EMP_PAN_NO", rs.getString("emp_pan_no"));
				hmProjectOwnerDetails.put("EMP_EMAIL", rs.getString("emp_email_sec"));
				hmProjectOwnerDetails.put("EMP_WORK_LOCATION", rs.getString("wlocation_id"));
			}
			rs.close();
			pst.close();
			
////			pst = con.prepareStatement("select * from work_location_info where wlocation_id != 460");
//			pst = con.prepareStatement("select * from work_location_info where weightage > 0 order by weightage");
//			rs = pst.executeQuery();
//			String wLocation="Offices at : ";
//			int j=0;
//			while(rs.next()) {
//				if(j==0){
//					wLocation+=rs.getString("wlocation_name");
//				}else{
//					wLocation+=","+rs.getString("wlocation_name");
//				}
//				j++;				
//			}
//			rs.close();
//			pst.close();
			
			Map<String, Map<String, String>> hmWorkLocation = CF.getWorkLocationMapForBilling(con);
//			System.out.println("hmWorkLocation ===>> " + hmWorkLocation);
//			System.out.println("EMP_WORK_LOCATION ===>> " + hmProjectOwnerDetails.get("EMP_WORK_LOCATION"));
			
			if(hmWorkLocation == null) hmWorkLocation = new HashMap<String, Map<String,String>>();
			Map<String, String> hmWlocation = hmWorkLocation.get(hmProjectOwnerDetails.get("EMP_WORK_LOCATION"));
			if(hmWlocation == null) hmWlocation = new HashMap<String, String>();
			
//			Map<String, String> hmInner = hmOrg.get(uF.showData(hmProjectOwnerDetails.get("EMP_ORG_ID"), ""));
			Map<String, String> hmInner = CF.getOrgDetails(con, uF, uF.showData(hmProjectOwnerDetails.get("EMP_ORG_ID"), ""));
			if(hmInner == null) hmInner = new HashMap<String, String>();
			
			String wLocation="Offices at : "+hmInner.get("ORG_OFFICES_AT");
			ownerOrg="<p style=\"text-align:center;\">"+uF.showData(hmInner.get("ORG_NAME"), "&nbsp;")+"</p>" +
				"<p style=\"text-align:center;\">"+uF.showData(hmInner.get("ORG_SUB_TITLE"), "&nbsp;")+"</p>" +
				"<p style=\"text-align:center;\">"+uF.showData(hmInner.get("ORG_ADDRESS"), "&nbsp;")+"</p>";
			
			orgPan = uF.showData(hmInner.get("ORG_PAN_NO"), "");
			orgMCARegNo = uF.showData(hmInner.get("ORG_REG_NO"), "");
			orgSTRegNo = uF.showData(hmInner.get("ORG_ST_REG_NO"), "");
			
			ownerEmail=uF.showData(hmProjectOwnerDetails.get("EMP_EMAIL"), "&nbsp;");
			
			ownerSign="For "+uF.showData(hmInner.get("ORG_NAME"), "&nbsp;")+"<br/>"+uF.showData(hmInner.get("ORG_SUB_TITLE"), "&nbsp;")+
					"<br/><br/>"+uF.showData(hmProjectOwnerDetails.get("EMP_NAME"), "&nbsp;")+"<br/>"+uF.showData(hmEmpCodeDesig.get(getStrProjectOwner()), "");
			locationTel = uF.showData(hmWlocation.get("WL_CONTACT_NO"), "");
			locationFax = uF.showData(hmWlocation.get("WL_FAX_NO"), "");
//			locationPan = uF.showData(hmWlocation.get("WL_PAN_NO"), "");
//			locationRegNo = uF.showData(hmWlocation.get("WL_REG_NO"), "");
//			locationECCNo1 = uF.showData(hmWlocation.get("WL_ECC1_NO"), "");
//			locationECCNo2 = uF.showData(hmWlocation.get("WL_ECC2_NO"), "");
			
//			 if(wLocation!=null && wLocation.contains(",")){
//		        	wLocation=wLocation.substring(0,wLocation.lastIndexOf(",")) + " and " + wLocation.substring(wLocation.lastIndexOf(",")+1);
//		     }
			
			Map<String, String> hmDept = CF.getDeptMap(con);
			Map<String, String> hmEmpDepartment = CF.getEmpDepartmentMap(con);
			
			String wLocationId = CF.getEmpWlocationId(con, uF, getStrProjectOwner());
			String departId = hmEmpDepartment.get(getStrProjectOwner());
			
//	 		=============================== Invoice Code Generation ======================================
			Map<String, String> hmLocation = hmWorkLocation.get(wLocationId);
			if(hmLocation == null) hmLocation = new HashMap<String, String>();
			
			String[] arr = CF.getFinancialYear(con, uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT), CF, uF);
			String locationCode = hmLocation.get("WL_NAME");			
			String departCode = "";
			if(hmDept.get(departId)!=null && hmDept.get(departId).contains(" ")) {
				String[] temp = hmDept.get(departId).toUpperCase().split(" ");
				for(int i=0;i<temp.length;i++) {
					departCode+=temp[i].substring(0,1);
				}
			} else if(hmDept.get(departId)!=null) {
				departCode = hmDept.get(departId).substring(0,hmDept.get(departId).length()>3 ? 3 : hmDept.get(departId).length());
			}
			
			int cnt = 0;
			pst=con.prepareStatement("select max(invoice_no) as invoice_no from promntc_invoice_details where wlocation_id=?");
			pst.setInt(1, uF.parseToInt(wLocationId));
			rs=pst.executeQuery();
			while(rs.next()) {
				cnt = rs.getInt("invoice_no");
			}
			rs.close();
			pst.close();
			cnt++;
			
			String invoiceCode = cnt + "-" + uF.getDateFormat(arr[0], DATE_FORMAT, "yyyy") + "-" + uF.getDateFormat(arr[1], DATE_FORMAT, "yy") + "/" + locationCode.toUpperCase(); //+"/"+departCode.toUpperCase()
// 		========================================================= End =========================================================			
		
			
			String msg = ownerOrg+"::::"+ownerEmail+"::::"+ownerSign+"::::"+wLocation+"::::"+locationTel+"::::"+locationFax+"::::"+orgPan
				+"::::"+orgMCARegNo+"::::"+orgSTRegNo+"::::"+invoiceCode; //+"::::"+locationECCNo2
			request.setAttribute("STATUS_MSG", msg);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	
	public String getStrProjectOwner() {
		return strProjectOwner;
	}

	public void setStrProjectOwner(String strProjectOwner) {
		this.strProjectOwner = strProjectOwner;
	}

	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

}
