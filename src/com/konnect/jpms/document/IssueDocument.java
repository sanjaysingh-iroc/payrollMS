package com.konnect.jpms.document;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class IssueDocument extends ActionSupport implements ServletRequestAware, IStatements{

	
	private static final long serialVersionUID = 1L;
	HttpSession session;
	CommonFunctions CF;
	public String execute(){
		UtilityFunctions uF = new UtilityFunctions();
		session= request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN; 
		
		request.setAttribute(PAGE, "/jsp/document/IssueDocument.jsp");
		request.setAttribute(TITLE, "Issue Documents");
		 
		
//		boolean isView  = CF.getAccess(session, request, uF);
//		if(!isView){
//			request.setAttribute(PAGE, PAccessDenied);
//			request.setAttribute(TITLE, TAccessDenied);
//			return ACCESS_DENIED;
//		}
		
		documentList(uF, CF);
		
		return SUCCESS;
	} 
	
	List <FillOrganisation> organisationList;
	String f_org;
	
	
	public void documentList(UtilityFunctions uF, CommonFunctions CF){
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs =null;
		Database db=new Database();
		db.setRequest(request);
		
		try {
		
			organisationList = new FillOrganisation(request).fillOrganisation();
			if(getF_org()==null){
				if((String)session.getAttribute(ORGID)!=null){
					setF_org((String)session.getAttribute(ORGID));
				}else{
					setF_org(organisationList.get(0).getOrgId());
				}
			}
			
			
			con=db.makeConnection(con);

			Map<String, String> hmNodes = CF.getNodes(con);
			
			
			
			pst = con.prepareStatement("select * from document_comm_details where org_id = ? and status = 1 order by document_name");
			pst.setInt(1, uF.parseToInt(getF_org()));
			rs = pst.executeQuery();
			List<List<String>> alReport = new ArrayList<List<String>>();
			List<String> alInner = new ArrayList<String>();
			
			while(rs.next()){
				
				alInner = new ArrayList<String>();
				
				alInner.add(rs.getString("document_id"));
				alInner.add(rs.getString("document_name"));
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
				alInner.add(sbNodes.toString());
				
				
				alInner.add("<a href=\"DocumentPreview.action?header=header&doc_id="+rs.getString("document_id")+"\"><i class=\"fa fa-file-o\" aria-hidden=\"true\"></i></a>");
				alInner.add("<a href=\"javascript:void(0);\" onclick=\"sendDocument("+rs.getString("doc_id")+");\"><i class=\"fa fa-file-o\" aria-hidden=\"true\"></i></a>");
				
				alReport.add(alInner);
				
			}
			rs.close();
			pst.close();
			
			
			
			request.setAttribute("alReport", alReport);
			
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
	
	public List<FillOrganisation> getOrganisationList() {
		return organisationList;
	}
	public void setOrganisationList(List<FillOrganisation> organisationList) {
		this.organisationList = organisationList;
	}
	public String getF_org() {
		return f_org;
	}
	public void setF_org(String f_org) {
		this.f_org = f_org;
	}
}