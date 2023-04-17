package com.konnect.jpms.training;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.konnect.jpms.util.Database;

public class FillCertificate{

	private String id;
	
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	String name;
	
	HttpServletRequest request;
	public  FillCertificate(HttpServletRequest request){
		this.request=request;
	}
	
	public  FillCertificate(String idCertificate,String nameCertificate){
		
		this.id=idCertificate;
		this.name=nameCertificate;
		
	}
	
	public  FillCertificate(){
		
	}
	
	
	/*public List<FillCertificate> fillCertificateList(){
		
		List<FillCertificate> list=new ArrayList<FillCertificate>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			pst=con.prepareStatement("select * from training_certificate");
			rs=pst.executeQuery();
		while(rs.next()){
			
			list.add(new FillCertificate(rs.getString("certificate_id"),rs.getString("certificate_name")));
		}
		
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeStatements(pst);
			db.closeResultSet(rs);
			db.closeConnection(con);
		}
		return list;
	}*/
	
	public List<FillCertificate> fillCertificateList(){
		
		List<FillCertificate> list=new ArrayList<FillCertificate>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			pst=con.prepareStatement("select * from certificate_details");
			rs=pst.executeQuery();
			while(rs.next()){
				list.add(new FillCertificate(rs.getString("certificate_details_id"), rs.getString("certificate_name")));
			}
			rs.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return list;
	}

	
	public List<FillCertificate> fillBorderList(){
		
		List<FillCertificate> borderList = new ArrayList<FillCertificate>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			pst=con.prepareStatement("select data_value,data_text from certificate_master_data_details where data_type_value = 1 and data_text is not null and data_text != '' order by data_value");
			rs=pst.executeQuery();
			while(rs.next()){
				borderList.add(new FillCertificate(rs.getString("data_value"),rs.getString("data_text")));
			}
			rs.close();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return borderList;
	}


	public List<FillCertificate> fillFirstLineList(){
		
		List<FillCertificate> firstLineList=new ArrayList<FillCertificate>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			pst=con.prepareStatement("select data_value,data_text from certificate_master_data_details where data_type_value = 2 and data_text is not null and data_text != '' order by data_value");
			rs=pst.executeQuery();
			while(rs.next()){
				firstLineList.add(new FillCertificate(rs.getString("data_value"),rs.getString("data_text")));
			}
			rs.close();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return firstLineList;
	}
	
	
	public List<FillCertificate> fillSecondLineList(){
		
		List<FillCertificate> secondLineList=new ArrayList<FillCertificate>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			pst=con.prepareStatement("select data_value,data_text from certificate_master_data_details where data_type_value = 3 and data_text is not null and data_text != '' order by data_value");
			rs=pst.executeQuery();
			while(rs.next()){
				secondLineList.add(new FillCertificate(rs.getString("data_value"),rs.getString("data_text")));
			}
			rs.close();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return secondLineList;
	}


	public List<FillCertificate> fillThirdLineList(){
		
		List<FillCertificate> thirdLineList=new ArrayList<FillCertificate>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			pst=con.prepareStatement("select data_value,data_text from certificate_master_data_details where data_type_value = 4 and data_text is not null and data_text != '' order by data_value");
			rs=pst.executeQuery();
			while(rs.next()){
				thirdLineList.add(new FillCertificate(rs.getString("data_value"),rs.getString("data_text")));
			}
			rs.close();
			pst.close();
		
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return thirdLineList;
	}


	public List<FillCertificate> fillFontSizeList(){
		
		List<FillCertificate> fontSizeList=new ArrayList<FillCertificate>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			pst=con.prepareStatement("select data_value,data_text from certificate_master_data_details where data_type_value = 5 order by data_value");
			rs=pst.executeQuery();
			while(rs.next()){
				fontSizeList.add(new FillCertificate(rs.getString("data_value"),rs.getString("data_text")));
			}
			rs.close();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return fontSizeList;
	}

	public List<FillCertificate> fillCertiLogoAlignList(){
		
		List<FillCertificate> certiLogoList=new ArrayList<FillCertificate>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			pst=con.prepareStatement("select data_value,data_text from certificate_master_data_details where data_type_value = 6  order by data_text");
			rs=pst.executeQuery();
			while(rs.next()){
				certiLogoList.add(new FillCertificate(rs.getString("data_value"), rs.getString("data_text")));
			}
			rs.close();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return certiLogoList;
	}

}
