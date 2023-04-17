package com.konnect.jpms.training;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillDesig;
import com.konnect.jpms.select.FillEmployee;
import com.konnect.jpms.select.FillGrade;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class TrainerCheck extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	String strSessionEmpId = null;

	CommonFunctions CF=null;
	private static Logger log = Logger.getLogger(TrainerInfo.class);
	
	private List<FillOrganisation> organisationList;
	private List<FillWLocation> locationList;
	private List<FillLevel> levelList;
	private List<FillDesig> desigList;
	private List<FillGrade> gradeList;
	private List<FillEmployee> empList;
	
	private String trainerType;
	private String strTrainerId;
	private String submit;
	private String fromPage;
	public String execute() {
		
		UtilityFunctions uF=new UtilityFunctions();
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if (CF==null){
			return LOGIN;
		}
//		System.out.println("getSubmit()==>"+getSubmit());
		System.out.println("fromPage==>"+getFromPage()+"==>getTrainerType()==>"+getTrainerType());
		if(getTrainerType()!=null && getTrainerType().equals("1")){
			if(getSubmit()!=null){
				insertEmpAsTrainer();
				return "success";
			}
			
		}else if(getTrainerType()!=null && getTrainerType().equals("2")){
				return "load";
		}
		loadData();
		return "view";
	}

	
	private void loadData() {
		organisationList = new FillOrganisation(request).fillOrganisation();
		locationList = new FillWLocation(request).fillWLocation();
		levelList = new FillLevel(request).fillLevel();
		desigList = new FillDesig(request).fillDesig();
		gradeList = new FillGrade(request).fillGrade();
		empList = new FillEmployee(request).fillEmployeeName();
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			Map<String, String> hmEmpLocation = CF.getEmpWlocationMap(con);
			Map<String, String> hmWLocation = CF.getWLocationMap(con, null, null);
			Map<String, String> hmEmpCodeDesig = CF.getEmpDesigMap(con);
			request.setAttribute("hmEmpLocation", hmEmpLocation);
			request.setAttribute("hmWLocation", hmWLocation);
			request.setAttribute("hmEmpCodeDesig", hmEmpCodeDesig);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeConnection(con);
		}
	}
	
	
	private void insertEmpAsTrainer() {

		Database db = new Database();
		db.setRequest(request);
		PreparedStatement pst = null;
		Connection con = null;		
		ResultSet rst=null;
		UtilityFunctions uF=new UtilityFunctions();
		try{
			con=db.makeConnection(con);
//			Map<String,String> hmEmpLocation = CF.getEmpWlocationMap(con);
			
			Map<String,String> hmCheckEmp=new HashMap<String, String>();
			pst=con.prepareStatement("select emp_id from training_trainer where emp_id is not null");	
			rst=pst.executeQuery();			
			while(rst.next()){
				hmCheckEmp.put(rst.getString("emp_id") ,rst.getString("emp_id"));
				hmCheckEmp.put(rst.getString("emp_id")+"_NAME" ,rst.getString("emp_id"));
				hmCheckEmp.put(rst.getString("emp_id")+"_ADDRESS" ,rst.getString("emp_id"));
				hmCheckEmp.put(rst.getString("emp_id")+"_CITY" ,rst.getString("emp_id"));
				hmCheckEmp.put(rst.getString("emp_id")+"_STATE" ,rst.getString("emp_id"));
				hmCheckEmp.put(rst.getString("emp_id")+"_COUNTRY" ,rst.getString("emp_id"));
				hmCheckEmp.put(rst.getString("emp_id")+"_EMAIL" ,rst.getString("emp_id"));
				hmCheckEmp.put(rst.getString("emp_id")+"_MOBILE" ,rst.getString("emp_id"));
			}
			rst.close();
			pst.close();
			
//			System.out.println("select emptrianer====>"+getStrTrainerId());
//			System.out.println("select TrainerType ====>"+getTrainerType());
			
			if(getStrTrainerId()!=null && !getStrTrainerId().equals("")){
				StringBuilder trainersName = new StringBuilder();
				List<String> tempEmpList=Arrays.asList(getStrTrainerId().split(","));
				
				for(int i=0;i<tempEmpList.size();i++){					
					if(hmCheckEmp.get(tempEmpList.get(i).trim())==null){
						Map<String, String> hmEmpDetails = getEmpDetails(con, tempEmpList.get(i).trim());
						pst=con.prepareStatement("insert into training_trainer (emp_id,trainer_name," +
								"trainer_address,trainer_city,trainer_state,trainer_country,trainer_mobile,trainer_email" +
								")values(?,?,?,?,?, ?,?,?)");	//trainer_work_location, ,? 
						pst.setInt(1, uF.parseToInt(tempEmpList.get(i).trim()));
//						pst.setInt(2, uF.parseToInt(hmEmpLocation.get(tempEmpList.get(i).trim())));
						pst.setString(2, hmEmpDetails.get("NAME"));
						pst.setString(3, hmEmpDetails.get("ADDRESS"));
						pst.setString(4, hmEmpDetails.get("CITY"));
						pst.setInt(5, uF.parseToInt(hmEmpDetails.get("STATE")));
						pst.setInt(6, uF.parseToInt(hmEmpDetails.get("COUNTRY")));
						pst.setString(7, hmEmpDetails.get("MOBILE"));
						pst.setString(8, hmEmpDetails.get("EMAIL"));
						pst.execute();
						pst.close();
						
						if(i > 0){
							trainersName.append(", ");
						}
						trainersName.append(hmEmpDetails.get("NAME"));
					}
				}
				session.setAttribute(MESSAGE, SUCCESSM+"You have added trainer "+trainersName+" successfully."+END);
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}


	
	private Map<String, String> getEmpDetails(Connection con, String empId) {

		PreparedStatement pst = null;
		ResultSet rst=null;
		UtilityFunctions uF=new UtilityFunctions();
		Map<String,String> hmEmpDetails = new HashMap<String, String>();
		try{
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			pst=con.prepareStatement("select emp_per_id,emp_fname,emp_mname, emp_lname,emp_address1,emp_state_id,emp_country_id," +
					"emp_contactno,emp_email,emp_city_id from employee_personal_details where emp_per_id = ?");
			pst.setInt(1, uF.parseToInt(empId));
			rst=pst.executeQuery();			
			while(rst.next()){
				hmEmpDetails.put(rst.getString("emp_per_id"), rst.getString("emp_per_id"));
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rst.getString("emp_mname") != null && rst.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rst.getString("emp_mname");
					}
				}
				
				hmEmpDetails.put("NAME", rst.getString("emp_fname")+strEmpMName+" "+rst.getString("emp_lname"));
				hmEmpDetails.put("ADDRESS", rst.getString("emp_address1"));
				hmEmpDetails.put("CITY", rst.getString("emp_city_id"));
				hmEmpDetails.put("STATE", rst.getString("emp_state_id"));
				hmEmpDetails.put("COUNTRY", rst.getString("emp_country_id"));
				hmEmpDetails.put("EMAIL", rst.getString("emp_email"));
				hmEmpDetails.put("MOBILE", rst.getString("emp_contactno"));
			}
			rst.close();
			pst.close();
			
//			System.out.println("hmEmpDetails ====>"+hmEmpDetails);
			
		}catch(Exception e){
			e.printStackTrace();
		}finally {
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
		return hmEmpDetails;
	}

	
	public List<FillOrganisation> getOrganisationList() {
		return organisationList;
	}

	public void setOrganisationList(List<FillOrganisation> organisationList) {
		this.organisationList = organisationList;
	}

	public List<FillWLocation> getLocationList() {
		return locationList;
	}

	public void setLocationList(List<FillWLocation> locationList) {
		this.locationList = locationList;
	}

	public List<FillLevel> getLevelList() {
		return levelList;
	}

	public void setLevelList(List<FillLevel> levelList) {
		this.levelList = levelList;
	}

	public List<FillDesig> getDesigList() {
		return desigList;
	}

	public void setDesigList(List<FillDesig> desigList) {
		this.desigList = desigList;
	}

	public List<FillGrade> getGradeList() {
		return gradeList;
	}

	public void setGradeList(List<FillGrade> gradeList) {
		this.gradeList = gradeList;
	}

	public List<FillEmployee> getEmpList() {
		return empList;
	}

	public void setEmpList(List<FillEmployee> empList) {
		this.empList = empList;
	}

	public String getTrainerType() {
		return trainerType;
	}

	public void setTrainerType(String trainerType) {
		this.trainerType = trainerType;
	}

	public String getStrTrainerId() {
		return strTrainerId;
	}

	public void setStrTrainerId(String strTrainerId) {
		this.strTrainerId = strTrainerId;
	}

	public String getSubmit() {
		return submit;
	}

	public void setSubmit(String submit) {
		this.submit = submit;
	}

	HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {

		this.request=request;
		
	}

	public String getFromPage() {
		return fromPage;
	}

	public void setFromPage(String fromPage) {
		this.fromPage = fromPage;
	}

}
