package com.konnect.jpms.document;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillCollateral;
import com.konnect.jpms.select.FillNodes;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class AddDocument extends ActionSupport implements ServletRequestAware, IStatements {

	/**
	 * 
	 */

	private static final long serialVersionUID = 1L;

	HttpSession session;
	CommonFunctions CF;
	List<FillCollateral> collHeaderList;
	List<FillCollateral> collFooterList;
	String strCollateralHeader;
	String strCollateralFooter;
	
	String strDocId;
	String strDocName;
	String strDocBody;
	String orgId;
	String []strNode;
	List<FillNodes> nodeList;
	  
	String userscreen;
	String navigationId;
	String toPage;
	
	public String execute() throws Exception { 

		String operation = request.getParameter("operation");
		String strId = request.getParameter("param");

		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;

		
		nodeList = new FillNodes(request).fillNodes("D", CF);
		collHeaderList=new FillCollateral(request).fillCollateral("H",CF);
		collFooterList=new FillCollateral(request).fillCollateral("F",CF);
		
		if (operation != null && operation.equals("D")) {
			return deleteDocument(strId);
		}
		if (operation != null && operation.equals("E")) {
			getBankCode();
			return viewDocument(strId);
		}
		if (getStrDocId() != null && getStrDocId().length() > 0) {
			return updateDocument();
		}
		if (getStrDocName() != null && getStrDocName().length() > 0) {
			return insertDocument();
		}
		getBankCode();
		loadDocument(strId);

		return "load";
	}

	private void getBankCode() {

		Connection con = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);		

		try {

			con = db.makeConnection(con);
			
			pst = con.prepareStatement("select bd.bank_account_no, bd.bank_id, bd1.bank_name,bd.bank_branch, bd.branch_code from bank_details bd1, branch_details bd where bd1.bank_id = bd.bank_id");
			rs = pst.executeQuery();
			StringBuilder sbBankCodes = new StringBuilder();
			while(rs.next()){
				sbBankCodes.append("["+rs.getString("branch_code")+"] <b>"+rs.getString("bank_name")+"</b><br/> ");
			}
			rs.close();
			pst.close();
			
			request.setAttribute("sbBankCodes", sbBankCodes.toString());
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	public String loadDocument(String strId) {

		setStrDocId(strId);
		return "load";
	}

	
	
	public String viewDocument(String strId) {

		Connection con = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);		
		UtilityFunctions uF = new UtilityFunctions();

		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from document_comm_details where document_id =?");
			pst.setInt(1, uF.parseToInt(strId));
			rs = pst.executeQuery();

			while (rs.next()) {

				setStrDocId(rs.getString("document_id"));
				setStrDocName(rs.getString("document_name"));
				setStrDocBody(rs.getString("document_text"));
				
				String arr[]=null;
				if(rs.getString("trigger_nodes")!=null){
					arr = rs.getString("trigger_nodes").split(",");
				}
				setStrNode(arr);
				setStrCollateralHeader(""+uF.parseToInt(rs.getString("collateral_header")));
				setStrCollateralFooter(""+uF.parseToInt(rs.getString("collateral_footer")));
			}
			rs.close();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return "update";
	}

	public String updateDocument() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs =null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			con = db.makeConnection(con);
//			pst = con.prepareStatement("UPDATE document_comm_details SET document_name=?, document_text=?, entry_date=?, emp_id=? where document_id=?");
//			pst.setString(1, getStrDocName());
//			pst.setString(2, getStrDocBody());
//			pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
//			pst.setInt(4, uF.parseToInt((String) session.getAttribute(EMPID)));
//			pst.setInt(5, uF.parseToInt(getStrDocId()));
//			pst.executeUpdate();
			
			pst = con.prepareStatement("select doc_id from document_comm_details where document_id=?");
			pst.setInt(1, uF.parseToInt(getStrDocId()));
			rs = pst.executeQuery();
			int nDoc_id = 0;
			while(rs.next()){
				nDoc_id = uF.parseToInt(rs.getString("doc_id"));
			}
			rs.close();
			pst.close();
			
//			if(getPublishDoc()!=null){
				pst = con.prepareStatement("UPDATE document_comm_details SET status=? where doc_id=?");
				pst.setInt(1, 3); //Archive
				pst.setInt(2, nDoc_id);
				pst.execute();
				System.out.print("archive pst===>"+pst);
				pst.close();
//			}
			
			
			StringBuilder sbNodes = new StringBuilder();
			sbNodes.append(",");	
			for(int i=0; getStrNode()!=null && i<getStrNode().length; i++){
				sbNodes.append(getStrNode()[i]);
				sbNodes.append(",");	
			}

			pst = con.prepareStatement("insert into document_comm_details (document_name, document_text, entry_date, emp_id, org_id, status, entry_time, trigger_nodes, doc_id,collateral_header,collateral_footer) values(?,?,?,?,?,?,?,?,?,?,?)");
			pst.setString(1, getStrDocName());
			pst.setString(2, getStrDocBody());
			pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(4, uF.parseToInt((String) session.getAttribute(EMPID)));
			pst.setInt(5, uF.parseToInt(getOrgId()));
			if(getPublishDoc()!=null){
				pst.setInt(6, 1); //Publish
			}else{
				pst.setInt(6, 2); //Save as Draft
			}
			pst.setTime(7, uF.getCurrentTime(CF.getStrTimeZone()));
			pst.setString(8, sbNodes.toString());
			pst.setInt(9, nDoc_id);
			pst.setInt(10, uF.parseToInt(getStrCollateralHeader()));
			pst.setInt(11, uF.parseToInt(getStrCollateralFooter()));
			
			
			pst.execute();
			System.out.print(" insert pst ==>"+pst);
			
			pst.close();
			
			

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;
	}

	String publishDoc;
	String draftDoc;
	
	public String insertDocument() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		try {

			StringBuilder sbNodes = new StringBuilder();
			sbNodes.append(",");	
			for(int i=0; getStrNode()!=null && i<getStrNode().length; i++){
				sbNodes.append(getStrNode()[i]);
				sbNodes.append(",");	
			}
			
			
			con = db.makeConnection(con);
			
			
			pst = con.prepareStatement("select max(doc_id) as doc_id from document_comm_details");
			rs = pst.executeQuery();
			int nDoc_id = 0;
			while(rs.next()){
				nDoc_id = uF.parseToInt(rs.getString("doc_id"));
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("insert into document_comm_details (document_name, document_text, entry_date, emp_id, org_id, status, entry_time, trigger_nodes, doc_id,collateral_header,collateral_footer) values(?,?,?,?,?,?,?,?,?,?,?)");
			pst.setString(1, getStrDocName());
			pst.setString(2, getStrDocBody());
			pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(4, uF.parseToInt((String) session.getAttribute(EMPID)));
			pst.setInt(5, uF.parseToInt(getOrgId()));
			
		
			if(getPublishDoc()!=null){
				pst.setInt(6, 1); //Publish
			}else{
				pst.setInt(6, 2); //Save as Draft
			}
			
			pst.setTime(7, uF.getCurrentTime(CF.getStrTimeZone()));
			pst.setString(8, sbNodes.toString());
			pst.setInt(9, ++nDoc_id);
			pst.setInt(10, uF.parseToInt(getStrCollateralHeader()));
			pst.setInt(11, uF.parseToInt(getStrCollateralFooter()));
			
			pst.execute();
			System.out.println("publish: "+pst);
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}

	public String deleteDocument(String strId) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement("delete from document_comm_details where document_id=?");
			pst.setInt(1, uF.parseToInt(strId));
			pst.execute();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
			return ERROR;
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}

	public void validate() {

	}

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public String getStrDocId() {
		return strDocId;
	}

	public void setStrDocId(String strDocId) {
		this.strDocId = strDocId;
	}

	public String getStrDocName() {
		return strDocName;
	}

	public void setStrDocName(String strDocName) {
		this.strDocName = strDocName;
	}

	public String getStrDocBody() {
		return strDocBody;
	}

	public void setStrDocBody(String strDocBody) {
		this.strDocBody = strDocBody;
	}

	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

	public String getPublishDoc() {
		return publishDoc;
	}

	public void setPublishDoc(String publishDoc) {
		this.publishDoc = publishDoc;
	}

	public String getDraftDoc() {
		return draftDoc;
	}

	public void setDraftDoc(String draftDoc) {
		this.draftDoc = draftDoc;
	}
	public String[] getStrNode() {
		return strNode;
	}

	public void setStrNode(String[] strNode) {
		this.strNode = strNode;
	}
	public List<FillNodes> getNodeList() {
		return nodeList;
	}

	public void setNodeList(List<FillNodes> nodeList) {
		this.nodeList = nodeList;
	}

	public List<FillCollateral> getCollHeaderList() {
		return collHeaderList;
	}

	public void setCollHeaderList(List<FillCollateral> collHeaderList) {
		this.collHeaderList = collHeaderList;
	}

	public List<FillCollateral> getCollFooterList() {
		return collFooterList;
	}

	public void setCollFooterList(List<FillCollateral> collFooterList) {
		this.collFooterList = collFooterList;
	}

	public String getStrCollateralHeader() {
		return strCollateralHeader;
	}

	public void setStrCollateralHeader(String strCollateralHeader) {
		this.strCollateralHeader = strCollateralHeader;
	}

	public String getStrCollateralFooter() {
		return strCollateralFooter;
	}

	public void setStrCollateralFooter(String strCollateralFooter) {
		this.strCollateralFooter = strCollateralFooter;
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
