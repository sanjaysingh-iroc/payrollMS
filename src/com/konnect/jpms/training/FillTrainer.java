package com.konnect.jpms.training;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.konnect.jpms.util.Database;

public class FillTrainer {

	private String trainer_id;
	private String trainer_name;
	
	public String getTrainer_id() {
		return trainer_id;
	}

	public void setTrainer_id(String trainer_id) {
		this.trainer_id = trainer_id;
	}

	public String getTrainer_name() {
		return trainer_name;
	}

	public void setTrainer_name(String trainer_name) {
		this.trainer_name = trainer_name;
	}

	HttpServletRequest request;
	public FillTrainer(HttpServletRequest request) {
		this.request = request;
	}
	
	public  FillTrainer(){
	}
		
	public FillTrainer(String id,String name){
		this.trainer_id=id;
		this.trainer_name=name;
	}
	 
	public	List<FillTrainer>  fillTrainer(){
			
		Database db=new Database();
		db.setRequest(request);
		Connection con=null;
		ResultSet rst=null;
		PreparedStatement pst=null;
		List<FillTrainer> al=new ArrayList<FillTrainer>();
		
		try{
			
			con=db.makeConnection(con);
		
		pst=con.prepareStatement("select * from training_trainer");
		
		rst=pst.executeQuery();
		
		while(rst.next()){
			al.add(new FillTrainer(rst.getString("trainer_id"),rst.getString("trainer_name")));
		}
		rst.close();
		pst.close();

		}catch(Exception e){
			e.printStackTrace();
		}finally{
			
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return al;
		
		
	}
	
	
	
	
}
