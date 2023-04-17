package com.konnect.jpms.document;

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

import com.konnect.jpms.select.FillNodes;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class DocumentList extends ActionSupport implements ServletRequestAware, IStatements{

	
	private static final long serialVersionUID = 1L;
	HttpSession session;
	CommonFunctions CF;
	String strUserType;

	List <FillOrganisation> orgList;
	String strOrg;
	List<FillNodes> nodeList;
	
	String userscreen;
	String navigationId;
	String toPage;
	
	public String execute(){
		session= request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN; 

		strUserType = (String) session.getAttribute(USERTYPE);
		request.setAttribute(PAGE, "/jsp/document/DocumentList.jsp");
		request.setAttribute(TITLE, "Document Settings");
		UtilityFunctions uF = new UtilityFunctions();
		
		/*boolean isView  = CF.getAccess(session, request, uF);
		if(!isView){
			request.setAttribute(PAGE, PAccessDenied);
			request.setAttribute(TITLE, TAccessDenied);
			return ACCESS_DENIED;
		} */
		
		documentList(uF, CF);
		
		getSelectedFilter(uF);
		
		return SUCCESS;
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
		
		String selectedFilter = CF.getSelectedFilter(CF, uF, alFilter, hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}
	

	
	public void documentList(UtilityFunctions uF, CommonFunctions CF){
	
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs =null;
		Database db=new Database();
		db.setRequest(request);
		
		try {
			nodeList = new FillNodes(request).fillNodes("D", CF);
			if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)){
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
			
			con = db.makeConnection(con);
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con, request);
			request.setAttribute("hmFeatureStatus", hmFeatureStatus);
			
			Map<String, String> hmNodes = CF.getNodes(con);
			Map<String, String> hmEmployeeNameMap = new HashMap<String, String>();
			Map<String, String> hmEmployeeCodeMap = new HashMap<String, String>();
			CF.getEmpNameCodeMap(con, null, null, hmEmployeeCodeMap, hmEmployeeNameMap);

			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from document_comm_details where status in (1, 2) ");
			if(uF.parseToInt(getStrOrg())>0){
				sbQuery.append(" and org_id = "+uF.parseToInt(getStrOrg()));
			}else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null){
				sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			sbQuery.append(" order by document_id desc");
			pst = con.prepareStatement(sbQuery.toString());
//			pst = con.prepareStatement("select * from document_comm_details  where org_id =? and status in (1, 2) order by document_id desc");
//			pst.setInt(1, uF.parseToInt(getStrOrg()));
			rs = pst.executeQuery();			
			List<List<String>> alReport = new ArrayList<List<String>>();
			while(rs.next()){
				List<String> alInner = new ArrayList<String>();
				StringBuilder sbNodes = new StringBuilder();
				if(rs.getString("trigger_nodes")!=null){
					String arr[] =rs.getString("trigger_nodes").split(",");
					for(int i=0; i<arr.length; i++){
						if(i==0)continue;
						sbNodes.append(""+hmNodes.get(arr[i]));
						if(i==arr.length-3){
							sbNodes.append(" and ");
						}else if(i<arr.length-2){
							sbNodes.append(", ");
						}
					}
				}
				alInner.add(rs.getString("document_id"));
				alInner.add(rs.getString("org_id"));
				alInner.add(uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));
				alInner.add(rs.getString("document_name"));
				alInner.add(uF.showData(hmEmployeeNameMap.get(rs.getString("emp_id")), ""));
				
				if(uF.parseToInt(rs.getString("status"))==1){
					/*alInner.add("<img src=\"images1/icons/approved.png\" title=\"Published\" style=\"float:left;padding-right:10px\">"); */
					
					alInner.add("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d;float:left;padding-right:10px;padding-top: 6px;\" title=\"Published\"></i>");
					
					
				}else if(uF.parseToInt(rs.getString("status"))==2){ 
					/*alInner.add("<img src=\"images1/icons/pullout.png\" title=\"Draft\" style=\"float:left;padding-right:10px\">");*/
					alInner.add("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#ea9900;float:left;padding-right:10px;padding-top: 6px;\" title=\"Draft\"></i>");
					
					
				}else{
					alInner.add("");
				}
				alInner.add(sbNodes.toString());
				alInner.add(rs.getString("doc_id"));
				
				alReport.add(alInner);
				
			}
			rs.close();
			pst.close();
			request.setAttribute("alReport", alReport);
			
			sbQuery = new StringBuilder();
			sbQuery.append("select * from document_comm_details where status=3 ");
			if(uF.parseToInt(getStrOrg())>0){
				sbQuery.append(" and org_id = "+uF.parseToInt(getStrOrg()));
			}else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null){
				sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			sbQuery.append(" order by document_id desc");
			pst = con.prepareStatement(sbQuery.toString());
//			pst = con.prepareStatement("select * from document_comm_details  where org_id =? and status=3 order by document_id desc");
//			pst.setInt(1, uF.parseToInt(getStrOrg()));
			rs = pst.executeQuery();			
			Map<String,List<List<String>>> hmOldReport = new HashMap<String, List<List<String>>>();
			while(rs.next()){				
				
				List<List<String>> alOldReport =hmOldReport.get(rs.getString("doc_id"));
				if(alOldReport ==null)alOldReport = new ArrayList<List<String>>();
				
				List<String> alInner = new ArrayList<String>();
				StringBuilder sbNodes = new StringBuilder();
				if(rs.getString("trigger_nodes")!=null){
					String arr[] =rs.getString("trigger_nodes").split(",");
					for(int i=0; i<arr.length; i++){
						if(i==0)continue;
						sbNodes.append(""+hmNodes.get(arr[i]));
						if(i==arr.length-3){
							sbNodes.append(" and ");
						}else if(i<arr.length-2){
							sbNodes.append(", ");
						}
					}
				}
				alInner.add(rs.getString("document_id"));
				alInner.add(rs.getString("org_id"));
				alInner.add(uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));
				alInner.add(rs.getString("document_name"));
				alInner.add(uF.showData(hmEmployeeNameMap.get(rs.getString("emp_id")), ""));
				alInner.add("");
				alInner.add(sbNodes.toString());
				
				alOldReport.add(alInner);
				
				hmOldReport.put(rs.getString("doc_id"), alOldReport);
				
			}
			rs.close();
			pst.close();
			request.setAttribute("hmOldReport", hmOldReport);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}
	
	HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request=request;
	}
	
	public List<FillOrganisation> getOrgList() {
		return orgList;
	}
	
	public void setOrgList(List<FillOrganisation> orgList) {
		this.orgList = orgList;
	}
	
	public String getStrOrg() {
		return strOrg;
	}
	
	public void setStrOrg(String strOrg) {
		this.strOrg = strOrg;
	}
	
	public List<FillNodes> getNodeList() {
		return nodeList;
	}
	
	public void setNodeList(List<FillNodes> nodeList) {
		this.nodeList = nodeList;
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