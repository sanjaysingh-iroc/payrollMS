package com.konnect.jpms.recruitment;



import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;


import com.konnect.jpms.select.FillEmployee;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IConstants;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class GetCandidateEmployeeList extends ActionSupport implements
		ServletRequestAware,IConstants {
	
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	String strSessionEmpId = null;

	String recruitmentID;
	String organisation;
	String grade;
	String location;
	String depart;
	String level;
	String design;
	List<FillEmployee> empList;
	CommonFunctions CF = null;
	String type;
	String submit;
	public String execute() {
		
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null) {
			return LOGIN;
		}
		
		
		UtilityFunctions uF = new UtilityFunctions();
		
		if (getViewSelection() != null && getViewSelection().equalsIgnoreCase("list")) {
			empList = new FillEmployee(request).fillCandidateRecruitmentEmployee(organisation, location, level, design, grade);
		} else {
//			if(getSubmit()!=null && getSubmit().equals("Save")){

				if (uF.parseToBoolean(getChboxStatus())){
					addEmployee(uF);
				}else{
					removeEmployee(uF);
				}
			}
		getRoundIds(uF);
//		}
  		getSelectEmployeeList(uF);

//  		if(type!=null && type.equals("elist")){
//  			return SUCCESS;
//  		}else{
        	
  			return LOAD;
//  		}
	}


	
	private void getRoundIds(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rst = null;
		List<String> listRoundId = new ArrayList<String>();
		
		try {
			con=db.makeConnection(con);
			pst=con.prepareStatement("select distinct(round_id), recruitment_id from panel_interview_details where recruitment_id=? order by round_id");
			pst.setInt(1,uF.parseToInt(getRecruitmentID()));
			rst=pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while(rst.next()){
				listRoundId.add(rst.getString("round_id"));
			}
			rst.close();
			pst.close();
			
		request.setAttribute("listRoundId", listRoundId);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	

private void getSelectEmployeeList(UtilityFunctions uF) {
		
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rst = null;
		
		try {
		
			con=db.makeConnection(con);
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			Map<String, String> hmEmpProfile = CF.getEmpProfileImage(con);
							
			pst=con.prepareStatement("select panel_employee_id,effective_date from recruitment_details where recruitment_id=?");
			pst.setInt(1,uF.parseToInt(getRecruitmentID()));
			rst=pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			String selectEmpIDs=null;
			String effectDate=null;
			while(rst.next()){
				selectEmpIDs=rst.getString("panel_employee_id");
				effectDate = rst.getString("effective_date");
			}
			rst.close();
			pst.close();
			
			Map<String, String> sltEmpDtCmprHm = new HashMap<String, String>();
			if(effectDate!=null && effectDate.length() > 0){
				boolean comparedate = uF.getCurrentDate(CF.getStrTimeZone()).before(uF.getDateFormatUtil(effectDate,DBDATE));
				if(selectEmpIDs!=null && !selectEmpIDs.equals("")){
					String tmpsltempids = selectEmpIDs.substring(1, selectEmpIDs.length()-1);
					List<String> selectedEmpIdsLst=Arrays.asList(tmpsltempids.split(","));
					
					for(int i=0; selectedEmpIdsLst!= null && i<selectedEmpIdsLst.size(); i++){
						sltEmpDtCmprHm.put(selectedEmpIdsLst.get(i).trim(), ""+comparedate);
					}
				}
			}
//			System.out.println("sltEmpDtCmprHm ========== "+sltEmpDtCmprHm);

			Map<String,String> hmEmpCode=new HashMap<String, String>();
	          pst=con.prepareStatement("select emp_per_id,empcode from employee_personal_details");
	          rst=pst.executeQuery();
//	          System.out.println("new Date ===> " + new Date());
	          while(rst.next()){
	        	  hmEmpCode.put(rst.getString("emp_per_id"), rst.getString("empcode"));
	          }
	          rst.close();
			  pst.close();
				
			List<String> selectEmpIds=new ArrayList<String>();
			List<String> selectEmpNameList=new ArrayList<String>();
			if(selectEmpIDs!=null && !selectEmpIDs.equals("")){
				Set<String> empSet = new HashSet<String>(Arrays.asList(selectEmpIDs.split(",")));
				Iterator<String> itr = empSet.iterator();
				while (itr.hasNext()) {
					String empId = (String) itr.next();
					if(empId!=null && !empId.equals("")){
//						<img  class=\"lazy\" src=\"userImages/avatar_photo.png\"  data-original=\"userImages/"+uF.showData(empImageMap.get(empList.get(i).trim()), "avatar_photo.png")+"\" border=\"0\" height=\"16px\" width=\"16px\" title=\""+hmEmpName.get(empList.get(i).trim())+"\"/>
						selectEmpNameList.add("<img style=\"margin-right:5px\" class=\"lazy img-circle\" width=\"18\" src=\"userImages/avatar_photo.png\" data-original=\"userImages/"+uF.showData(hmEmpProfile.get(empId.trim()),"avatar_photo.png")+"\">"+hmEmpName.get(empId.trim()) +" ["+hmEmpCode.get(empId.trim())+"]");
						selectEmpIds.add(empId.trim());
					}
				}
			}else{
				selectEmpNameList=null;
			}
			
			
			
		  Map<String,String> hmWlocation=new HashMap<String, String>();
          pst=con.prepareStatement("select emp_id,wlocation_name from employee_official_details join work_location_info using (wlocation_id)");
          rst=pst.executeQuery();
//          System.out.println("new Date ===> " + new Date());
          while(rst.next()){
        	  hmWlocation.put(rst.getString("emp_id"), rst.getString("wlocation_name"));
        	  
          }
          rst.close();
		  pst.close();
			
          request.setAttribute("selectEmpIds", selectEmpIds);
          request.setAttribute("sltEmpDtCmprHm", sltEmpDtCmprHm);
          
			request.setAttribute("hmWlocation", hmWlocation);	
			
			request.setAttribute("selectEmpNameList", selectEmpNameList);
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}
	
	/*private void getSelectEmployeeList(UtilityFunctions uF) {
		
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		ResultSet rst = null;

		Map<String, String> hmEmpName = CF.getEmpNameMap(null, null);
		Map<String, String> hmEmpProfile = CF.getEmpProfileImage();
		
		try {
		
			con=db.makeConnection(con);
						
			pst=con.prepareStatement("select panel_employee_id from recruitment_details where recruitment_id=?");
			pst.setInt(1,uF.parseToInt(getRecruitmentID()));
			rst=pst.executeQuery();
			String selectEmpIDs=null;
			while(rst.next()){
				
				selectEmpIDs=rst.getString("panel_employee_id");
			}
		
			List<String> selectEmpNameList=new ArrayList<String>();
			List<String> selectEmpIds=new ArrayList<String>();
			if(selectEmpIDs!=null && !selectEmpIDs.equals("")){
			
				Set<String> trainerSet = new HashSet<String>(Arrays.asList(selectEmpIDs.split(",")));
				Iterator<String> itr = trainerSet.iterator();
				while (itr.hasNext()) {
					String trainerId = (String) itr.next();
					if(trainerId!=null && !trainerId.equals("")){
						selectEmpNameList.add("<img style=\"margin-right:5px\" width=\"18\" src=\"userImages/"+hmEmpProfile.get(trainerId.trim())+"\">"+hmEmpName.get(trainerId.trim()));
						selectEmpIds.add(trainerId.trim());
					}
				}
			}else{
				selectEmpNameList=null;
			}
			request.setAttribute("selectEmpIds", selectEmpIds);
			request.setAttribute("selectEmpNameList", selectEmpNameList);
			
			
			// Location of employees  
			Map<String,String> hmWlocation=new HashMap<String, String>();
	          pst=con.prepareStatement("select emp_id,wlocation_name from employee_official_details join work_location_info using (wlocation_id)");
	          rst=pst.executeQuery();
	          while(rst.next()){
	        	  hmWlocation.put(rst.getString("emp_id"), rst.getString("wlocation_name"));
	        	  
	          }

				request.setAttribute("hmWlocation", hmWlocation);	
			
				
				
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
		
			db.closeConnection(con);
			db.closeResultSet(rst);
			db.closeStatements(pst);
		}
		
	}*/
	
	private void removeEmployee(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rst = null;
		String oldpanel = null;

		try {

			con = db.makeConnection(con);

			pst = con.prepareStatement("select panel_employee_id from recruitment_details where recruitment_id=?");
			pst.setInt(1, uF.parseToInt(getRecruitmentID()));
			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while (rst.next()) {
				oldpanel = rst.getString("panel_employee_id");
			}
			rst.close();
			pst.close();
			
			List<String> alnewEmplist = new ArrayList<String>();
			
			if(oldpanel!=null && !oldpanel.equals("")){

				String[] oldEmp = oldpanel.split(",");
				for (int i = 0;oldEmp!=null && i < oldEmp.length; i++) {
					if (!oldEmp[i].equalsIgnoreCase(getSelectedEmp().trim()) && !oldEmp[i].equals(""))
						alnewEmplist.add(oldEmp[i]);	
				}
			}
			
			String newpanel = null;
			for (int i = 0; i < alnewEmplist.size(); i++) {

				if (i == 0)
					newpanel ="," + alnewEmplist.get(i)+",";
				else
					newpanel +=alnewEmplist.get(i)+"," ;
			}

			pst = con.prepareStatement("update recruitment_details set  "
					+ " panel_employee_id= ?  where recruitment_id= ? ");
			pst.setString(1, newpanel);
			pst.setInt(2, uF.parseToInt(getRecruitmentID()));
			pst.executeUpdate();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}

	private void addEmployee(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rst = null;
		String newEmpid;

		try {
			con = db.makeConnection(con);

			String existing = null;
			pst = con.prepareStatement("select panel_employee_id from recruitment_details where recruitment_id=?");
			pst.setInt(1, uF.parseToInt(getRecruitmentID()));
			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while (rst.next()) {
				existing = rst.getString("panel_employee_id");
			}
			rst.close();
			pst.close();
                
			if (existing == null || existing.equals("")) {
				newEmpid = ","+getSelectedEmp()+",";
			} else {
					if(!existing.contains(","+getSelectedEmp().trim()+","))			
					newEmpid = existing+getSelectedEmp() + "," ;
					else
					newEmpid=existing;	
				}

			pst = con.prepareStatement("update recruitment_details set  "
					+ " panel_employee_id= ?  where recruitment_id= ? ");
			
			pst.setString(1, newEmpid);
			pst.setInt(2, uF.parseToInt(getRecruitmentID()));
			pst.executeUpdate();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}

	
	public String getOrganisation() {
		return organisation;
	}

	public void setOrganisation(String organisation) {
		this.organisation = organisation;
	}

	public String getDepart() {
		return depart;
	}

	public void setDepart(String depart) {
		this.depart = depart;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getGrade() {
		return grade;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}

	public List<FillEmployee> getEmpList() {
		return empList;
	}

	public void setEmpList(List<FillEmployee> empList) {
		this.empList = empList;
	}
	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getDesign() {
		return design;
	}

	public void setDesign(String design) {
		this.design = design;
	}

	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}
	
	
	String viewSelection;
	// for display variables
	
	public String getViewSelection() {
		return viewSelection;
	}

	public void setViewSelection(String viewSelection) {
		this.viewSelection = viewSelection;
	}

	String chboxStatus;
	String selectedEmp;
	
	
	public String getChboxStatus() {
		return chboxStatus;
	}

	public void setChboxStatus(String chboxStatus) {
		this.chboxStatus = chboxStatus;
	}

	public String getSelectedEmp() {
		return selectedEmp;
	}

	public void setSelectedEmp(String selectedEmp) {
		this.selectedEmp = selectedEmp;
	}

	public String getRecruitmentID() {
		return recruitmentID;
	}

	public void setRecruitmentID(String recruitmentID) {
		this.recruitmentID = recruitmentID;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSubmit() {
		return submit;
	}

	public void setSubmit(String submit) {
		this.submit = submit;
	}
	
	
}
