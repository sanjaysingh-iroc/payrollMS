package com.konnect.jpms.util;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;

public class MoveDocuments implements IStatements,ServletRequestAware {

	/**
	 * @param args
	 */
	
	public static void main(String[] args) {
		MoveDocuments documents = new MoveDocuments();
		documents.moveEmpImageDocument();
		documents.moveInvestmentDocument();
		documents.moveReimbursementsDocument();
		documents.movePerkDocument();
		documents.moveCTCVariablesDocument();
	}

	public void moveCTCVariablesDocument() {
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			con = db.makeConnection(con);
			String saveLocation = getSaveLoacation(con);
			
			if(saveLocation != null && !saveLocation.trim().equals("")){
//				System.out.println("CTCVariables document");
				pst = con.prepareStatement("select emp_id,ref_document from emp_lta_details where ref_document is not null and ref_document !='' order by emp_id");
				rs = pst.executeQuery();
				Map<String, List<List<String>>> hmMap = new LinkedHashMap<String, List<List<String>>>();
				while (rs.next()){
					List<List<String>> outerList =  hmMap.get(rs.getString("emp_id"));
					if (outerList == null) outerList = new ArrayList<List<String>>();
					
					List<String> innerList = new ArrayList<String>();
					innerList.add(rs.getString("ref_document"));
					
					outerList.add(innerList);
					
					hmMap.put(rs.getString("emp_id"), outerList);
				}
				rs.close();
				pst.close();
				
//				System.out.println("hmMap=====>"+hmMap);
				Iterator<String> it = hmMap.keySet().iterator();
				while (it.hasNext()){
					String strEmpId = it.next();
					List<List<String>> outerList =  hmMap.get(strEmpId);
					for(int i = 0; outerList!=null && i<outerList.size(); i++){
						List<String> innerList = outerList.get(i);
						
						for(int j = 0; innerList!=null && j < innerList.size(); j++){
							String strEmpDoc = innerList.get(j);
							
							String strDocumentPath = saveLocation+strEmpDoc;
							File dirPath = new File(strDocumentPath);
							if (!dirPath.exists()) {
								continue;
							}
							
//							System.out.println(strEmpId+"====CTCVariables====>"+strEmpDoc);
							
							String strMoveDocumentPath = saveLocation+I_CTCVARIABLES+"/"+I_DOCUMENT+"/"+strEmpId;;
							File dirMovePath = new File(strMoveDocumentPath);
							if (!dirMovePath.exists()) {
								dirMovePath.mkdirs(); 
							}
							
							Runtime.getRuntime().exec("mv "+strDocumentPath+" "+strMoveDocumentPath);
						}
					}
				}
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	public void movePerkDocument() {
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			con = db.makeConnection(con);
			String saveLocation = getSaveLoacation(con);
			
			if(saveLocation != null && !saveLocation.trim().equals("")){
				System.out.println("Perks document");
				pst = con.prepareStatement("select emp_id,ref_document from emp_perks where ref_document is not null and ref_document !='' order by emp_id");
				rs = pst.executeQuery();
				Map<String, List<List<String>>> hmMap = new LinkedHashMap<String, List<List<String>>>();
				while (rs.next()){
					List<List<String>> outerList =  hmMap.get(rs.getString("emp_id"));
					if (outerList == null) outerList = new ArrayList<List<String>>();
					
					List<String> innerList = new ArrayList<String>();
					innerList.add(rs.getString("ref_document"));
					
					outerList.add(innerList);
					
					hmMap.put(rs.getString("emp_id"), outerList);
				}
				
				rs.close();
				pst.close();
//				System.out.println("hmMap=====>"+hmMap);
				Iterator<String> it = hmMap.keySet().iterator();
				while (it.hasNext()){
					String strEmpId = it.next();
					List<List<String>> outerList =  hmMap.get(strEmpId);
					for(int i = 0; outerList!=null && i<outerList.size(); i++){
						List<String> innerList = outerList.get(i);
						
						for(int j = 0; innerList!=null && j < innerList.size(); j++){
							String strEmpDoc = innerList.get(j);
							
							String strDocumentPath = saveLocation+strEmpDoc;
							File dirPath = new File(strDocumentPath);
							if (!dirPath.exists()) {
								continue;
							}
							
//							System.out.println(strEmpId+"====Perks====>"+strEmpDoc);
							
							String strMoveDocumentPath = saveLocation+I_PERKS+"/"+I_DOCUMENT+"/"+strEmpId;;
							File dirMovePath = new File(strMoveDocumentPath);
							if (!dirMovePath.exists()) {
								dirMovePath.mkdirs(); 
							}
							
							Runtime.getRuntime().exec("mv "+strDocumentPath+" "+strMoveDocumentPath);
						}
					}
				}
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	public void moveReimbursementsDocument() {
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			con = db.makeConnection(con);
			String saveLocation = getSaveLoacation(con);
			
			if(saveLocation != null && !saveLocation.trim().equals("")){
//				System.out.println("Reimbursements document");
				pst = con.prepareStatement("select emp_id,ref_document from emp_reimbursement where ref_document is not null and ref_document !='' order by emp_id");
				rs = pst.executeQuery();
				Map<String, List<List<String>>> hmMap = new LinkedHashMap<String, List<List<String>>>();
				while (rs.next()){
					List<List<String>> outerList =  hmMap.get(rs.getString("emp_id"));
					if (outerList == null) outerList = new ArrayList<List<String>>();
					
					String[] strDocs = null;
					if (rs.getString("ref_document") != null && rs.getString("ref_document").length()>2) {
						strDocs = rs.getString("ref_document").split(":_:");
					}
					for (int k = 0; strDocs != null && k < strDocs.length; k++) {
						List<String> innerList = new ArrayList<String>();
						innerList.add(strDocs[k]);
						
						outerList.add(innerList);
					}
					
					hmMap.put(rs.getString("emp_id"), outerList);
				}
				
				rs.close();
				pst.close();
//				System.out.println("hmMap=====>"+hmMap);
				Iterator<String> it = hmMap.keySet().iterator();
				while (it.hasNext()){
					String strEmpId = it.next();
					List<List<String>> outerList =  hmMap.get(strEmpId);
					for(int i = 0; outerList!=null && i<outerList.size(); i++){
						List<String> innerList = outerList.get(i);
						
						for(int j = 0; innerList!=null && j < innerList.size(); j++){
							String strEmpDoc = innerList.get(j);
							
							String strDocumentPath = saveLocation+strEmpDoc;
							File dirPath = new File(strDocumentPath);
							if (!dirPath.exists()) {
								continue;
							}
							
//							System.out.println(strEmpId+"====Reimbursements====>"+strEmpDoc);
							
							String strMoveDocumentPath = saveLocation+I_REIMBURSEMENTS+"/"+I_DOCUMENT+"/"+strEmpId;;
							File dirMovePath = new File(strMoveDocumentPath);
							if (!dirMovePath.exists()) {
								dirMovePath.mkdirs(); 
							}
							
							Runtime.getRuntime().exec("mv "+strDocumentPath+" "+strMoveDocumentPath);
						}
					}
				}
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	public void moveInvestmentDocument() {
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			con = db.makeConnection(con);
			String saveLocation = getSaveLoacation(con);
			
			if(saveLocation != null && !saveLocation.trim().equals("")){
				System.out.println("investment document");
				pst = con.prepareStatement("select emp_id,document_name from investment_documents order by emp_id");
				rs = pst.executeQuery();
				Map<String, List<List<String>>> hmMap = new LinkedHashMap<String, List<List<String>>>();
				while (rs.next()){
					List<List<String>> outerList =  hmMap.get(rs.getString("emp_id"));
					if (outerList == null) outerList = new ArrayList<List<String>>();
					
					List<String> innerList = new ArrayList<String>();
					innerList.add(rs.getString("document_name"));
					
					outerList.add(innerList);
					
					hmMap.put(rs.getString("emp_id"), outerList);
				}
				
				rs.close();
				pst.close();
//				System.out.println("hmMap=====>"+hmMap);
				Iterator<String> it = hmMap.keySet().iterator();
				while (it.hasNext()){
					String strEmpId = it.next();
					List<List<String>> outerList =  hmMap.get(strEmpId);
					for(int i = 0; outerList!=null && i<outerList.size(); i++){
						List<String> innerList = outerList.get(i);
						
						for(int j = 0; innerList!=null && j < innerList.size(); j++){
							String strEmpDoc = innerList.get(j);
							
							String strDocumentPath = saveLocation+strEmpDoc;
							File dirPath = new File(strDocumentPath);
							if (!dirPath.exists()) {
								continue;
							}
							
//							System.out.println(strEmpId+"====investment====>"+strEmpDoc);
							
							String strMoveDocumentPath = saveLocation+I_INVESTMENTS+"/"+I_DOCUMENT+"/"+strEmpId;;
							File dirMovePath = new File(strMoveDocumentPath);
							if (!dirMovePath.exists()) {
								dirMovePath.mkdirs(); 
							}
							
							Runtime.getRuntime().exec("mv "+strDocumentPath+" "+strMoveDocumentPath);
						}
					}
				}
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	public void moveEmpImageDocument() {
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			con = db.makeConnection(con);
			String saveLocation = getSaveLoacation(con);
			//mv /var/www/html/Logo/7374512689050673logo.png /var/www/html/Logo/doc
			
			if(saveLocation != null && !saveLocation.trim().equals("")){
				pst = con.prepareStatement("select emp_per_id,emp_image from employee_personal_details where emp_image is not null and emp_image !='avatar_photo.png' order by emp_per_id");
				rs = pst.executeQuery();
				Map<String, String> hmMap = new LinkedHashMap<String, String>();
				while (rs.next()){
					hmMap.put(rs.getString("emp_per_id"), rs.getString("emp_image"));
				}
				
				rs.close();
				pst.close();
				
				Iterator<String> it = hmMap.keySet().iterator();
				while (it.hasNext()){
					String strEmpId = it.next();
					String strEmpImage = hmMap.get(strEmpId);
					
					String strDocumentPath = saveLocation+strEmpImage;
					File dirPath = new File(strDocumentPath);
					if (!dirPath.exists()) {
						continue;
					}
					
//					System.out.println(strEmpId+"========>"+strEmpImage);
					
					String strMoveDocumentPath = saveLocation+I_PEOPLE+"/"+I_IMAGE+"/"+strEmpId;;
					File dirMovePath = new File(strMoveDocumentPath);
					if (!dirMovePath.exists()) {
						dirMovePath.mkdirs(); 
					}
					
					Runtime.getRuntime().exec("mv "+strDocumentPath+" "+strMoveDocumentPath); 
				}
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
		
	}

	private String getSaveLoacation(Connection con) {
		PreparedStatement pst = null;
		ResultSet rst = null;
		String saveLocation = null;
		try {
				pst = con.prepareStatement("select value from settings where options = 'DOC_SAVE_LOCATION'");
				rst = pst.executeQuery();
				while (rst.next()) {
					saveLocation = rst.getString("value");
				}
				rst.close();
				pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rst !=null){
				try {
					rst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return saveLocation;
	}

	HttpServletRequest request;
	
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

}
