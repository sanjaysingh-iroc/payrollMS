package com.konnect.jpms.training;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class GetSelectedTrainerAjax extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	String strSessionEmpId = null;

	

	CommonFunctions CF=null;

	private static Logger log = Logger.getLogger(GetSelectedTrainerAjax.class);
	
//	String chboxStatus;
//	String trainerId;
	private String planId;
	private String location;
	private String type;
	
	private String selectedEmp;
	private String chboxStatus;
	private String existemp;
	
	public String execute() {
		
//		Map<String, String> hmtrainerName=new HashMap<String, String>();
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if (CF==null){
			return LOGIN;
		}
		UtilityFunctions uF=new UtilityFunctions();
  
//		trainerInfo(hmtrainerName);
//		
//		if(getType()!=null && getType().equals("select")){
//		
//			if (uF.parseToBoolean(getChboxStatus())) {
//				addTrainer();
//			} else {
//				removeTrainer();
//			}
//			
//		}else
		
		if(getType()!=null && getType().equals("location")){
			getSelectedTrainerList();
			getTrainerListLocationWise();
			getAllTrainersList();
			
		}else{
			List<String> list=new ArrayList<String>();
			if(existemp!=null){
				List<String> list1=Arrays.asList(existemp.split(","));
				list.addAll(list1);
			}
				if (uF.parseToBoolean(getChboxStatus())) {
					getSelectEmployeeList(list,true,true);
				} else {
					getSelectEmployeeList(list,false,true);
				}
				
			setName(list);
		}
		return SUCCESS;		   

	}

	
	public void setName(List<String> list){
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		
		try{
			con = db.makeConnection(con);
		Map<String, String> hmTrainerName = getTrainerList();
//		System.out.println("hmTrainerName ===> " + hmTrainerName);
		StringBuilder sb=new StringBuilder();
		List<List<String>> empNameList=new ArrayList<List<String>>();
		
		for(String empId:list){
			List<String> innerList = new ArrayList<String>();
				if(empId.equals("0") || empId.equals("")){
					continue;
				}
				innerList.add(empId);
				innerList.add(hmTrainerName.get(empId));
			empNameList.add(innerList);
			if(sb == null || sb.toString().equals("")){
				sb.append(","+empId+",");
			}else{
				sb.append(empId+",");
			}
		}
		
		StringBuffer allData = new StringBuffer();
		if(empNameList != null && !empNameList.isEmpty()) {

			allData.append("<div style=\"border: 2px solid #ccc;\">");
			
			if(empNameList != null && !empNameList.isEmpty() && empNameList.size() > 0){
				allData.append("<div style=\"padding: 8px 20px; border: 2px solid lightgray;\"><b>Trainers</b></div> ");
			int trnCnt=0;	
			for(int i=0;i<empNameList.size();i++){
				List<String> innerList = empNameList.get(i);
				if(innerList.get(1) != null && !innerList.get(1).equals("null")) {
					trnCnt++;
					allData.append("<div style=\"float: left; width: 100%; margin: 5px;\"><strong>"+ (trnCnt) +".</strong>&nbsp;&nbsp;"+innerList.get(1) +"&nbsp;&nbsp;<a href=\"javascript: void(0)\"" +
							" onclick=\"getSelectedTrainer('false','"+innerList.get(0)+"');\"><img border=\"0\" style=\"width: 12px; height: 12px;\" src=\""+request.getContextPath()+"/images1/arrow_reset1.png\"/></a></div>");
				}
			 }
			
			}
				if(empNameList.isEmpty() || empNameList.size() == 0){
					allData.append("<div class=\"nodata msg\" style=\"width: 85%\"> <span>No Trainer selected</span> </div>");
				}
				allData.append("</div>");
		 } else {
			 allData.append("<div class=\"nodata msg\" style=\"width:85%\"><span>No Trainer selected</span></div>");
		 }
		String selectEmpIDS = sb.toString()!=null && !sb.toString().equals("") ? sb.toString() :"0";
		allData.append("<input type=\"hidden\" name=\"trnselected\" id=\"trnselected\" value=\""+selectEmpIDS +"\"/>");
		
		request.setAttribute("empNameList", empNameList);
		request.setAttribute("allData", allData.toString());
		
		}catch (Exception e) {
			e.printStackTrace();
		} finally{
			db.closeConnection(con);
		}
	}
	
	private void getAllTrainersList() {
		
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rst = null;
		try {
			
			con=db.makeConnection(con);
			pst=con.prepareStatement("select * from training_trainer");
			rst=pst.executeQuery();
			List<List<String>> trainerOuterList = new ArrayList<List<String>>();
	 		while(rst.next()){
				List<String> innerList=new ArrayList<String>();
				innerList.add(rst.getString("trainer_id"));	
				if(rst.getString("trainer_emp_id") != null && !rst.getString("trainer_emp_id").equals("")){
					innerList.add(rst.getString("trainer_emp_id"));
					innerList.add("EXTrainer");
				} else if(rst.getString("emp_id") != null && !rst.getString("emp_id").equals("")){
					innerList.add(rst.getString("emp_id"));
					innerList.add("INTrainer");
				}
				innerList.add(rst.getString("trainer_name"));
				trainerOuterList.add(innerList);
			}
	 		rst.close();
			pst.close();
			
			request.setAttribute("trainerOuterList", trainerOuterList);
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}

	private Map<String, String> getTrainerList() {
		
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rst = null;
		Map<String, String> hmTrainer = new HashMap<String, String>();
		try {
			con=db.makeConnection(con);
			pst=con.prepareStatement("select * from training_trainer");
			rst=pst.executeQuery();
			while(rst.next()){
				hmTrainer.put(rst.getString("trainer_id"),rst.getString("trainer_name"));
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
		return hmTrainer;
	}
	
	
	private void getSelectEmployeeList(List<String> list,boolean flag,boolean flag1) {
		
			List<String> li=Arrays.asList(selectedEmp.split(","));
			for(String a:li){
				if(flag){
					if(list != null && !list.contains(a)){
					list.add(a);
					}
				}else if(list != null && !flag){
					list.remove(a);
				}
			}
	}
	
	private void getTrainerListLocationWise() {
		
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rst = null;
		UtilityFunctions uF=new UtilityFunctions();
		try {
			
			con=db.makeConnection(con);
			Map<String, String> hmtrainer=new HashMap<String, String>();
			StringBuilder sbQuery=new StringBuilder();
			sbQuery.append("select * from training_trainer");
			//if(uF.parseToInt(getLocation())>0){
				sbQuery.append(" where trainer_work_location="+uF.parseToInt(getLocation()));
//			}
			pst=con.prepareStatement(sbQuery.toString());
//			System.out.println("pst=====>"+pst);
			rst=pst.executeQuery();
			while(rst.next()){				
				hmtrainer.put(rst.getString("trainer_id"),rst.getString("trainer_name"));
			}
			rst.close();
			pst.close();
//			System.out.println("hmtrainer=====>"+hmtrainer);
			request.setAttribute("hmtrainer", hmtrainer);
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
		
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}

	private void getSelectedTrainerList() {
		
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rst = null;
		UtilityFunctions uF=new UtilityFunctions();
		try {
			
			con=db.makeConnection(con);
			
			pst=con.prepareStatement("select trainer_ids from training_schedule where plan_id=?");
			pst.setInt(1,uF.parseToInt(getPlanId()));
//			System.out.println("pst=====>"+pst);
			rst=pst.executeQuery();
			String trainerIDs=null;
			while(rst.next()){
				trainerIDs=rst.getString("trainer_ids");
			}
			rst.close();
			pst.close();
			
//			List<String> SelectedTrainerList=new ArrayList<String>();
			Map<String, String> hmSelectedTrainer=new HashMap<String, String>();
			if(trainerIDs!=null){
				List<String> tmpTrainerList=Arrays.asList(trainerIDs.split(","));
				Set<String> trainerSet = new HashSet<String>(tmpTrainerList);
				Iterator<String> itr = trainerSet.iterator();
				while (itr.hasNext()) {
					String trainerId = (String) itr.next();
					if(trainerId!=null && !trainerId.equals("")){
//						SelectedTrainerList.add(hmtrainer.get(trainerId.trim()));
						hmSelectedTrainer.put(trainerId.trim(), trainerId.trim());
					}
				}
			}
//			System.out.println("hmSelectedTrainer=====>"+hmSelectedTrainer);
//			System.out.println("SelectedTrainerList=====>"+SelectedTrainerList);
			request.setAttribute("hmSelectedTrainer", hmSelectedTrainer);
//			request.setAttribute("SelectedTrainerList", SelectedTrainerList);
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
		
			
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}

//	private void trainerInfo(Map<String, String> hmtrainerName) {
//		
//		Connection con = null;
//		PreparedStatement pst = null;
//		Database db = new Database();
//		db.setRequest(request);
//		ResultSet rst = null;
//		try {
//			
//			con=db.makeConnection(con);
//			
//			pst=con.prepareStatement("select * from training_trainer");
//			System.out.println("pst=====>"+pst);
//			rst=pst.executeQuery();
//			while(rst.next()){
//				
//				hmtrainerName.put(rst.getString("trainer_id"),rst.getString("trainer_name"));
//			}
//			
//			
//			
//			
//		}catch(Exception e){
//			e.printStackTrace();
//		}finally{
//		
//			db.closeConnection(con);
//			db.closeResultSet(rst);
//			db.closeStatements(pst);
//		}
//		
//	}

//	private void removeTrainer() {
//		
//		Connection con = null;
//		PreparedStatement pst = null;
//		Database db = new Database();
//		db.setRequest(request);
//		ResultSet rst = null;
//		String oldpanel=null;
//		UtilityFunctions uF=new UtilityFunctions();
//		try {
//			
//			con=db.makeConnection(con);
//			
//			pst=con.prepareStatement("select trainer_ids from training_schedule where plan_id=?");
//			pst.setInt(1,uF.parseToInt(getPlanId()));
//			System.out.println("pst=====>"+pst);
//			
//			rst=pst.executeQuery();
//			
//			while(rst.next()){
//				oldpanel=rst.getString("trainer_ids");
//			}
//			
//		
//			String[] oldEmp=oldpanel.split(",");
//			List<String> alnewTrainerlist=new ArrayList<String>();
//             
//			for(int i=1;i<oldEmp.length;i++){
//				if(!oldEmp[i].equalsIgnoreCase(getTrainerId().trim()))
//					alnewTrainerlist.add(oldEmp[i]);
//			
//			}
//	
//			String newpanel="";
//			for(int i=0;i<alnewTrainerlist.size();i++){
//
//					if(i==0){
//						newpanel=","+alnewTrainerlist.get(i)+",";
//					}else{						
//						newpanel+=alnewTrainerlist.get(i)+",";
//					}
//			}
//			
//
//			
//			pst = con.prepareStatement("update training_schedule set trainer_ids=? where plan_id=?");
//			pst.setString(1,newpanel);
//			pst.setInt(2,uF.parseToInt(getPlanId()));
//			
//		
//			
//			pst.executeUpdate();
//		}catch(Exception e){
//			e.printStackTrace();
//		}finally{
//		
//			db.closeConnection(con);
//			db.closeResultSet(rst);
//			db.closeStatements(pst);
//		}
//		
//		
//	}

//	private void addTrainer() {
//
//		Connection con = null;
//		PreparedStatement pst = null;
//		Database db = new Database();
//		db.setRequest(request);
//		ResultSet rst = null;
//		String newEmpid;
//
//		UtilityFunctions uF=new UtilityFunctions();
//		
//
//		String oldpanel = null;
//		String newpanel;
//		
//		try {
//			con=db.makeConnection(con);
//
//			pst=con.prepareStatement("select trainer_ids from training_schedule where plan_id=?");
//			pst.setInt(1,uF.parseToInt(getPlanId()));
//		
//			System.out.println("pst=====>"+pst);
//			rst=pst.executeQuery();
//			
//			while(rst.next()){
//				oldpanel=rst.getString("trainer_ids");
//			}
//				
// 			if(oldpanel==null || oldpanel.equals("")){
// 					newpanel=","+getTrainerId()+","; 
// 			}else{
//					newpanel=oldpanel+getTrainerId()+",";  
// 			}
// 			
//			
//			pst = con.prepareStatement("update training_schedule set trainer_ids=? where plan_id=?");
//			pst.setString(1,newpanel);
//			pst.setInt(2,uF.parseToInt(getPlanId()));
//		
//			pst.execute();
//		
//			
//	}catch(Exception e){
//		e.printStackTrace();
//	}finally{
//		db.closeConnection(con);
//		db.closeResultSet(rst);
//		db.closeStatements(pst);
//	}
//		
//		
//	}

	HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request=request;
		
	}

	public String getSelectedEmp() {
		return selectedEmp;
	}

	public void setSelectedEmp(String selectedEmp) {
		this.selectedEmp = selectedEmp;
	}

	public String getExistemp() {
		return existemp;
	}

	public void setExistemp(String existemp) {
		this.existemp = existemp;
	}

	public String getChboxStatus() {
		return chboxStatus;
	}

	public void setChboxStatus(String chboxStatus) {
		this.chboxStatus = chboxStatus;
	}

//	public String getTrainerId() {
//		return trainerId;
//	}
//
//	public void setTrainerId(String trainerId) {
//		this.trainerId = trainerId;
//	}

	public String getPlanId() {
		return planId;
	}

	public void setPlanId(String planId) {
		this.planId = planId;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
	