package com.konnect.jpms.reports.master;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IConstants;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class DepartmentReport extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	List<FillWLocation> wLocationList;
	List<FillServices> serviceList;
	private static Logger log = Logger.getLogger(DepartmentReport.class);
	
	HttpSession session;
	CommonFunctions CF;
	String strUsertypeId;
	String strUserType;
	String strBaseUserType;
	
	List<FillOrganisation> orgList;
	String strOrg;
	String strSerivce;
	
	String userscreen;
	String navigationId;
	String toPage;
	
	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null) {
			return LOGIN;
		} 
		strUserType = (String) session.getAttribute(USERTYPE);
		strBaseUserType = (String) session.getAttribute(BASEUSERTYPE);
		strUsertypeId = (String) session.getAttribute(BASEUSERTYPEID);
		
		UtilityFunctions uF = new UtilityFunctions();
		request.setAttribute(PAGE, PReportDepartment);
		request.setAttribute(TITLE, TViewDepartment);
		
		/*boolean isView  = CF.getAccess(session, request, uF);
		if(!isView) {
			request.setAttribute(PAGE, PAccessDenied);
			request.setAttribute(TITLE, TAccessDenied);
			return ACCESS_DENIED;
		}*/
		if(strBaseUserType!=null && !strBaseUserType.equalsIgnoreCase(ADMIN)){
			orgList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
			if(uF.parseToInt(getStrOrg()) == 0  && orgList!=null && orgList.size()>0){
				setStrOrg(orgList.get(0).getOrgId());
			}
		}else{
			if(uF.parseToInt(getStrOrg()) == 0){
				setStrOrg((String) session.getAttribute(ORGID));
			}
			orgList = new FillOrganisation(request).fillOrganisation();
		}
		
		viewDepartment();	  		
		return loadDepartment(uF); 

	}
	
	public String loadDepartment(UtilityFunctions uF){
		
		wLocationList = new FillWLocation(request).fillWLocation(getStrOrg());
		serviceList = new FillServices(request).fillServices(getStrOrg(), uF);
		
		request.setAttribute("wLocationList", wLocationList);
		int wLocationId, i=0;
		String wLocationName;
		
		if(wLocationList.size()!=0) {
			StringBuilder sbWLocationList = new StringBuilder();
			sbWLocationList.append("{");
		    for(i=0; i<wLocationList.size()-1;i++ ) {
		    		wLocationId = Integer.parseInt((wLocationList.get(i)).getwLocationId());
		    		wLocationName = wLocationList.get(i).getwLocationName();
		    		sbWLocationList.append("\""+ wLocationId+"\":\""+wLocationName+"\",");
		    }
		    wLocationId = Integer.parseInt((wLocationList.get(i)).getwLocationId());
			wLocationName = wLocationList.get(i).getwLocationName();
			sbWLocationList.append("\""+ wLocationId+"\":\""+wLocationName+"\"");	
			sbWLocationList.append("}");
		    request.setAttribute("sbWLocationList", sbWLocationList.toString());
		}
	    
		getSelectedFilter(uF);
		
		return LOAD;
	}
	

	private void getSelectedFilter(UtilityFunctions uF) {
		Map<String,String> hmFilter=new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();
		
		alFilter.add("ORGANISATION");
		if(getStrOrg()!=null) {
			String strOrg="";
			int k=0;
			for(int i=0;orgList!=null && i<orgList.size();i++) {
//				for(int j=0;j<getF_sbu().length;j++) {
					if(getStrOrg().equals(orgList.get(i).getOrgId())) {
						strOrg=orgList.get(i).getOrgName();
					}
//				}
			}
			if(strOrg!=null && !strOrg.equals("")) {
				hmFilter.put("ORGANISATION", strOrg);
			} else {
				hmFilter.put("ORGANISATION", "All Organizations");
			}
		} else {
			hmFilter.put("ORGANISATION", "All Organizations");
		}
		
		String selectedFilter = CF.getSelectedFilter1(CF, uF, alFilter, hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}
	

	public void viewDepartment(){
        
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs =null;
        Database db = new Database();
        db.setRequest(request);
        UtilityFunctions uF = new UtilityFunctions();

        try {
                con = db.makeConnection(con);
                
                Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con, request);
    			request.setAttribute("hmFeatureStatus", hmFeatureStatus);
//    			Map<String, List<String>> hmFeatureUserTypeId = (Map<String, List<String>>)request.getAttribute("hmFeatureUserTypeId");
//    			String empOrgId = null;
//    			if(uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_LOGIN_USER_ORG_IN_FILTER)) && hmFeatureUserTypeId.get(IConstants.F_LOGIN_USER_ORG_IN_FILTER).contains(strUsertypeId)) {
//    				empOrgId = (String)session.getAttribute(ORGID);
//    			}
//    			orgList = new FillOrganisation(request).fillOrganisation(empOrgId);
//    			if((getStrOrg()==null || getStrOrg().equals("")) && orgList!=null && orgList.size()>0){
//    				setStrOrg((String)session.getAttribute(ORGID));
//    			}
    			
//                Map<String, String> hmWLocation = CF.getWlocationFromDept(con);
        		Map<String, String> hmServices = CF.getServicesMap(con,false);
        		
        		Map<String, String> hmDepartEmpCount = new HashMap<String, String>();
				pst = con.prepareStatement("select count(*) as count, depart_id from employee_official_details eod, employee_personal_details epd " +
						"where epd.emp_per_id = eod.emp_id and epd.is_alive=true and eod.emp_id >0 group by depart_id");
				rs = pst.executeQuery();
				while(rs.next()) {
					hmDepartEmpCount.put(rs.getString("depart_id"), rs.getString("count"));
				}
				rs.close();
				pst.close();
//			request.setAttribute("hmDepartEmpCount", hmDepartEmpCount);
			
//				if(uF.parseToInt(getStrOrg()) > 0){
//	        		pst = con.prepareStatement("select di.*,od.org_name,od.org_code from department_info di,org_details od where di.org_id = od.org_id and di.org_id = ? order by dept_name");
//	    			pst.setInt(1, uF.parseToInt(getStrOrg()));
//                } else {
//                	pst = con.prepareStatement("select di.*,od.org_name,od.org_code from department_info di,org_details od where di.org_id = od.org_id order by dept_name");
//                }
				
				StringBuilder sbQuery = new StringBuilder();
				sbQuery.append("select di.*,od.org_name,od.org_code from department_info di,org_details od where di.org_id = od.org_id ");
				if(uF.parseToInt(getStrOrg())>0){
					sbQuery.append(" and di.org_id = "+uF.parseToInt(getStrOrg()));
				}else if(strBaseUserType!=null && !strBaseUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null){
					sbQuery.append(" and di.org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
				}
				sbQuery.append("  order by dept_name");
				pst = con.prepareStatement(sbQuery.toString());
                rs = pst.executeQuery();
                Map<String, List<List<String>>> hmDepartmentDataOrgwise = new HashMap<String, List<List<String>>>();
                List<List<String>> parentList = new ArrayList<List<String>>();
                Map<String, List<List<String>>> childMap = new HashMap<String, List<List<String>>>();
                Map<String, String> hmOrgName = new HashMap<String, String>();
                while(rs.next()){

                		parentList = hmDepartmentDataOrgwise.get(rs.getString("org_id"));
                		if(parentList == null) parentList = new ArrayList<List<String>>();
                		
                        List<String> alInner = new ArrayList<String>();
                        alInner.add(uF.showData(Integer.toString(rs.getInt("dept_id")),""));
        				alInner.add(uF.showData(rs.getString("dept_code"),""));
        				alInner.add(uF.showData(rs.getString("dept_name"),""));
        				alInner.add(uF.showData(rs.getString("dept_contactno"),""));
        				alInner.add(uF.showData(rs.getString("dept_faxno"),""));
        				alInner.add(uF.showData(rs.getString("dept_desc"), ""));
        				
//        				alInner.add(uF.showData(hmWLocation.get(rs.getString("dept_id")),""));
        				alInner.add("");
        				if(rs.getString("service_id")!=null){
        					alInner.add(uF.showData(hmServices.get(rs.getString("service_id").trim()), ""));
        				}else{
        					alInner.add("N/A");
        				}
        				
        				alInner.add(rs.getString("parent"));
                        
                        if(uF.parseToInt(rs.getString("parent"))==0){
                                parentList.add(alInner);
                        }else { 
                                List<List<String>> outerList=childMap.get(rs.getString("parent"));
                                if(outerList==null)outerList=new ArrayList<List<String>>();
                                outerList.add(alInner);
                                childMap.put(rs.getString("parent"), outerList);
                        }
                        
                    hmOrgName.put(rs.getString("org_id"), rs.getString("org_name")+" ["+ rs.getString("org_code") +"]");
                    hmDepartmentDataOrgwise.put(rs.getString("org_id"), parentList);
                }
    			rs.close();
    			pst.close();
                
                StringBuilder sb=new StringBuilder("<ul>");
                Set<String> setDepartMap = hmDepartmentDataOrgwise.keySet();
    			Iterator<String> it = setDepartMap.iterator();
    			while(it.hasNext()) {
    				String strOrgId = (String)it.next();
    				sb.append("<li>"); 
    				sb.append("<strong>"+hmOrgName.get(strOrgId)+"</strong>");
    				sb.append("<ul class=\"level_list\">");
    				List<List<String>> parentList1 = hmDepartmentDataOrgwise.get(strOrgId);
		                for(int i=0;i<parentList1.size();i++) {
		               	 List<String> alInner =parentList1.get(i);
		               	 String depart=alInner.get(0);
		               	sb.append("<li>");
//		               	if(uF.parseToBoolean((String)request.getAttribute(IConstants.DELETE_ACCESS))) {
		               		if(uF.parseToInt(hmDepartEmpCount.get(depart)) > 0) {
		               			String strMsg = "Sorry! You have " + uF.parseToInt(hmDepartEmpCount.get(depart)) + " employees added with this Department, therefore we cannot delete the Department. To consider this option, please ensure that you have ZERO Employees added.";
		               			sb.append("<a href=\"javascript:void(0);\" style=\"color: red;\" onclick=\"alert('"+strMsg+"')\"> <i class=\"fa fa-trash\" aria-hidden=\"true\"></i></a>");
		               		} else {
		               			sb.append("<a href=\"AddDepartment.action?strOrg="+getStrOrg()+"&operation=D&ID="+depart+"&userscreen="+getUserscreen()+"&navigationId="+getNavigationId()+"&toPage="+getToPage()+"\" style=\"color: red;\" onclick=\"return confirm('Are you sure you wish to delete this department?')\"><i class=\"fa fa-trash\" aria-hidden=\"true\"></i></a>");
		               		}
//						}
//						if(uF.parseToBoolean((String)request.getAttribute(IConstants.UPDATE_ACCESS))) { 
							sb.append("<a href=\"javascript:void(0);\" onclick=\"editDept('"+getStrOrg()+"','"+depart+"','"+getUserscreen()+"','"+getNavigationId()+"','"+getToPage()+"')\"><i class=\"fa fa-pencil-square-o\" aria-hidden=\"true\"></i></a>"); 
//						}
						sb.append("Name: <strong>"+alInner.get(2)+" ["+alInner.get(1)+"]</strong> &nbsp;&nbsp;&nbsp;"); 
						sb.append("Contact No: <strong>"+alInner.get(3)+"</strong>&nbsp;&nbsp;&nbsp;"); 
						sb.append("Fax No: <strong>"+alInner.get(4)+"</strong>&nbsp;&nbsp;&nbsp;");
						 
//						sb.append("SBU: <strong>"+alInner.get(7)+"</strong>&nbsp;&nbsp;&nbsp;");
						sb.append("<p style=\"font-weight: normal; font-size: 14px;padding-left:40px\">"+alInner.get(5)+"</p>");
		               	 
		               	rec(sb,depart,childMap,uF);
		               	
		               	sb.append("</li>");
		               }
		                
		            	sb.append("</ul>");
		            	sb.append("</li>");
    			}
                sb.append("</ul>");
                
//                System.out.println("=="+sb.toString());
                request.setAttribute("departList", sb.toString());
        } catch (Exception e) {
                e.printStackTrace();
        }finally{
               db.closeResultSet(rs);
               db.closeStatements(pst);
               db.closeConnection(con);
        }
        
}
	 
	 public void rec(StringBuilder sb,String parent, Map<String, List<List<String>>> childMap,UtilityFunctions uF){
//		 System.out.println("===>"+parent);
		 List<List<String>> outer= childMap.get(parent);
		 
		 if(outer!=null && !outer.isEmpty()){
			 sb.append("<ul>");
			 for(int i=0;i<outer.size();i++){
		       	 List<String> alInner =outer.get(i);
		       	 String depart=alInner.get(0);
		       	sb.append("<li>");
		       	if(uF.parseToBoolean((String)request.getAttribute(IConstants.DELETE_ACCESS))){
               		sb.append("<a href=\"AddDepartment.action?orgId="+getStrOrg()+"&operation=D&ID="+depart+"\" class=\"del\" onclick=\"return confirm('Are you sure you wish to delete this department?')\"> - </a>"); 
				}
				if(uF.parseToBoolean((String)request.getAttribute(IConstants.UPDATE_ACCESS))){ 
				
					sb.append("<a href=\"javascript:void(0);\" class=\"edit_lvl\" onclick=\"editDept('"+getStrOrg()+"','"+depart+"')\">Edit</a>"); 
				} 
				sb.append("Name: <strong>"+alInner.get(2)+" ["+alInner.get(1)+"]</strong> &nbsp;&nbsp;&nbsp;"); 
				sb.append("Contact No: <strong>"+alInner.get(3)+"</strong>&nbsp;&nbsp;&nbsp;"); 
				sb.append("Fax No: <strong>"+alInner.get(4)+"</strong>&nbsp;&nbsp;&nbsp;");
				 
				sb.append("SBU: <strong>"+alInner.get(7)+"</strong>&nbsp;&nbsp;&nbsp;");
				sb.append("<p style=\"font-weight: normal; font-size: 10px;padding-left:40px\">"+alInner.get(5)+"</p>");
		       	 
		       	 rec(sb,depart,childMap,uF);
		       	 sb.append("</li>");
	        }
			 sb.append("</ul>");
		 }
		 
	 }







	/*public String viewDepartment(){
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs =null;
		Database db = new Database();
		UtilityFunctions uF = new UtilityFunctions();

		try {

			List<List<String>> al = new ArrayList<List<String>>();
			List<String> alInner = new ArrayList<String>();
			Map hmDepartmentReport = new HashMap();
			
			
			
			Map<String, String> hmServices = CF.getServiceDesc();
			
			con = db.makeConnection(con);
			pst = con.prepareStatement(selectDepartmentR1);
			pst.setInt(1, uF.parseToInt(getStrOrg()));
			rs = pst.executeQuery();
			while(rs.next()){
				alInner = new ArrayList<String>();
				alInner.add(uF.showData(Integer.toString(rs.getInt("dept_id")),""));
				alInner.add(uF.showData(rs.getString("dept_code"),""));
				alInner.add(uF.showData(rs.getString("dept_name"),""));
				
//				alInner.add(uF.showData(rs.getString("city_name"),""));
//				alInner.add(uF.showData(rs.getString("state_name"),""));
//				alInner.add(uF.showData(rs.getString("country_name"),""));
				alInner.add(uF.showData(rs.getString("dept_contactno"),""));
				alInner.add(uF.showData(rs.getString("dept_faxno"),""));
				alInner.add(uF.showData(rs.getString("dept_desc"), ""));
				
				alInner.add(uF.showData(hmWLocation.get(rs.getString("dept_id")),""));
				alInner.add(uF.showData(hmServices.get(rs.getString("service_id")), ""));
				
//				alInner.add("<a href="+request.getContextPath()+"/AddDepartment.action?E="+rs.getString("dept_id")+">Edit</a> | <a onclick=\"return confirm('"+ConfirmDelete+"');\" href="+request.getContextPath()+"/AddDepartment.action?D="+rs.getString("dept_id")+">Delete</a>");
				al.add(alInner);
				
				hmDepartmentReport.put(rs.getString("dept_id"), alInner);
			}
			request.setAttribute("hmDepartmentReport", hmDepartmentReport);
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		}finally{
			db.closeConnection(con);
		}
		return SUCCESS;
		
	}*/
	 
	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public List<FillWLocation> getwLocationList() {
		return wLocationList;
	}

	public void setwLocationList(List<FillWLocation> wLocationList) {
		this.wLocationList = wLocationList;
	}

	public String getStrOrg() {
		return strOrg;
	}

	public void setStrOrg(String strOrg) {
		this.strOrg = strOrg;
	}

	public List<FillOrganisation> getOrgList() {
		return orgList;
	}

	public void setOrgList(List<FillOrganisation> orgList) {
		this.orgList = orgList;
	}

	public List<FillServices> getServiceList() {
		return serviceList;
	}

	public void setServiceList(List<FillServices> serviceList) {
		this.serviceList = serviceList;
	}

	public String getStrSerivce() {
		return strSerivce;
	}

	public void setStrSerivce(String strSerivce) {
		this.strSerivce = strSerivce;
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

}
