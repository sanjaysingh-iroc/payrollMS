package com.konnect.jpms.select;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;

public class FillBank implements IStatements{
	String bankId;
	String bankCode;
	String bankName;

	public FillBank(String bankId, String bankCode, String bankName) {
		this.bankId = bankId;
		this.bankCode = bankCode;
		this.bankName = bankName;
	}
	HttpServletRequest request;
	public FillBank(HttpServletRequest request) {
		this.request = request;
	}
	public FillBank() {
	}
	
	public List<FillBank> fillBankCode(){
		List<FillBank> al = new ArrayList<FillBank>();
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs1 = null;
		Database db = new Database();
		db.setRequest(request);
		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement(selectBank);
			rs1 = pst.executeQuery();
			while (rs1.next()) {
				al.add(new FillBank(rs1.getString("bank_id"), rs1.getString("bank_code"), null));
			}
			rs1.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs1);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	
		return al;
	} 
	
	
	public List<FillBank> fillBankDetails(){
		List<FillBank> al = new ArrayList<FillBank>();
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs1 = null;
		Database db = new Database();
		db.setRequest(request);
		try {

			con = db.makeConnection(con);
//			pst = con.prepareStatement("select bd.bank_account_no, bd.branch_id, bd1.bank_name,bd.bank_branch, bd.branch_code from bank_details bd1, branch_details bd where bd1.bank_id = bd.bank_id order by bd1.bank_name,bd.bank_branch");
			pst = con.prepareStatement("select bd.bank_account_no, bd.branch_id, bd1.bank_name,bd.bank_branch, bd.branch_code from bank_details bd1, branch_details bd where bd1.bank_id = bd.bank_id order by bd1.bank_name,bd.branch_code");
			rs1 = pst.executeQuery();
			while (rs1.next()) {
				al.add(new FillBank(rs1.getString("branch_id"), null, "["+rs1.getString("branch_code")+"]"+rs1.getString("bank_name") +", "+rs1.getString("bank_branch")));
			}
			rs1.close();
			pst.close();
			al.add(new FillBank("-1", null, "Other Bank"));
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs1);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	
		return al;
	}
	
	public List<FillBank> fillBankName(){
		List<FillBank> al = new ArrayList<FillBank>();
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs1 = null;
		Database db = new Database();
		db.setRequest(request);
		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement("select bd.bank_account_no, bd.branch_id, bd1.bank_name,bd.bank_branch from bank_details bd1, branch_details bd where bd1.bank_id = bd.bank_id  order by bd1.bank_name,bd.bank_branch");
			rs1 = pst.executeQuery();
			while (rs1.next()) {
				al.add(new FillBank(rs1.getString("branch_id"), null, rs1.getString("bank_name")));
			}
			rs1.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs1);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	
		return al;
	}
	
	public List<FillBank> fillBankAccNo(){
		List<FillBank> al = new ArrayList<FillBank>();
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs1 = null;
		Database db = new Database();
		db.setRequest(request);
		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement("select bd.bank_account_no, bd.branch_id, bd1.bank_name,bd.bank_branch from bank_details bd1, branch_details bd where bd1.bank_id = bd.bank_id");
			rs1 = pst.executeQuery();
			while (rs1.next()) {
				if(rs1.getString("bank_account_no")!=null && rs1.getString("bank_account_no").length()>0){
					al.add(new FillBank(rs1.getString("branch_id"), null, rs1.getString("bank_account_no")+", "+rs1.getString("bank_name")+","+rs1.getString("bank_branch")));
				}
			}
			rs1.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs1);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	
		return al;
	}
	
	public List<FillBank> fillBankAccNoForDocuments(com.konnect.jpms.util.CommonFunctions CF, UtilityFunctions uF, String strOrgId){
		List<FillBank> al = new ArrayList<FillBank>();
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {

			con = db.makeConnection(con);
			
			Map<String, String> hmActivityNode = CF.getActivityNode(con);
			if(hmActivityNode == null) hmActivityNode = new HashMap<String, String>();
			
			int nTriggerNode = uF.parseToInt(hmActivityNode.get(""+ACTIVITY_BANK_ORDER_ID));
			
			pst = con.prepareStatement("select bd.bank_account_no, bd.branch_id, bd1.bank_name,bd.bank_branch,bd.branch_code from bank_details bd1, branch_details bd where bd1.bank_id = bd.bank_id");
			rs = pst.executeQuery();
			Map<String, String> hmBranch = new HashMap<String, String>();
			Map<String, String> hmBranchDetails = new HashMap<String, String>();
			while (rs.next()) {
				if(rs.getString("bank_account_no")!=null && rs.getString("bank_account_no").length()>0) {
					hmBranch.put(rs.getString("branch_id"), rs.getString("branch_code"));
					hmBranchDetails.put(rs.getString("branch_id"), rs.getString("bank_account_no")+", "+rs.getString("bank_name")+","+rs.getString("bank_branch"));
				}
			}
			rs.close();
			pst.close();
			
			Iterator<String> it = hmBranch.keySet().iterator();
			while(it.hasNext()){
				String strBranchId = it.next(); 
				String strBankCode = hmBranch.get(strBranchId);
				
				pst = con.prepareStatement("select * from document_comm_details where document_text like '%["+strBankCode+"]%' and trigger_nodes like '%,"+nTriggerNode+",%' and status=1 and org_id=? order by document_id desc limit 1");
				pst.setInt(1, uF.parseToInt(strOrgId));
				rs = pst.executeQuery();
				boolean isExist = false;
				while(rs.next()) {
					isExist = true;
				} 
				rs.close();
				pst.close();
				
				if(isExist){
					String strBranchDetails = hmBranchDetails.get(strBranchId);
					al.add(new FillBank(strBranchId, null, strBranchDetails));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	
		return al;
	}

	public String getBankId() {
		return bankId;
	}

	public void setBankId(String bankId) {
		this.bankId = bankId;
	}

	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	
}
