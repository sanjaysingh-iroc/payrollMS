package com.konnect.jpms.recruitment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IConstants;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class AddAndGetPanelRound extends ActionSupport implements
		ServletRequestAware,IConstants {
	
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	String strSessionEmpId = null;
	
	CommonFunctions CF = null;
	
	private String recruitId;
	private String roundId;
	private String mode;
	private String empId;
	private String allEmpId;
	private String strRound;
	
	public String execute() { 
		
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);
		if (CF == null) {
			return LOGIN;
		}
		
		UtilityFunctions uF = new UtilityFunctions();
		if(mode!=null && mode.equals("add")){
			addNewRound(uF);
		} else if(mode!=null && mode.equals("remove")){
			deleteRound(uF);
		} else if(mode!=null && mode.equals("addemp")){
			addEmployeeInRound(uF);
		}  else if(mode!=null && mode.equals("addallemp")){
			addAllEmployeeInRound(uF);
		} else if(mode!=null && mode.equals("removeemp")){
			deleteEmployeeFromRound(uF);
		} else if(mode!=null && mode.equals("emproundchange")){
			changePanelRound(uF);
		}else{
			
//			deleteRound(uF);
		}
		getRoundIds(uF);
		getEmpIdsRoundwise(uF);
		getRoundIds1();
  			return LOAD;
	}

	private void addAllEmployeeInRound(UtilityFunctions uF) {
		// TODO Auto-generated method stub
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rst = null;
		try {
			
			con=db.makeConnection(con);
//			String maxRoundId=null;
//			pst=con.prepareStatement("select max(round_id) as maxround from panel_interview_details where recruitment_id=?");
//			pst.setInt(1,uF.parseToInt(getRecruitID()));
//			rst=pst.executeQuery();
//			while(rst.next()){
//				maxRoundId = rst.getString("maxround");
//			}
//			rst.close();
//			pst.close();
//	
//			System.out.println("getAllEmpId =====> " + getAllEmpId());
			List<String> empIDList =Arrays.asList(getAllEmpId().split(","));
			for(int i=0; empIDList != null && i< empIDList.size(); i++){
				boolean flag = false;
				pst=con.prepareStatement("select round_id from panel_interview_details where recruitment_id=? and round_id = ? and " +
						"panel_emp_id = ? ");
				pst.setInt(1,uF.parseToInt(getRecruitId()));
				pst.setInt(2, uF.parseToInt(getRoundId()));
				pst.setInt(3, uF.parseToInt(empIDList.get(i)));
				rst=pst.executeQuery();
//				System.out.println("new Date =====> "+new Date());
				while(rst.next()){
					flag = true;
				}
				rst.close();
				pst.close();
				
				if(flag == false && uF.parseToInt(empIDList.get(i)) > 0){
					pst = con.prepareStatement("insert into panel_interview_details(recruitment_id,round_id,panel_emp_id,added_by,added_date)" +
						"values(?,?,?,?,?)");
					pst.setInt(1, uF.parseToInt(getRecruitId()));
					pst.setInt(2, uF.parseToInt(getRoundId()));
					pst.setInt(3, uF.parseToInt(empIDList.get(i)));
					pst.setInt(4, uF.parseToInt(strSessionEmpId));
					pst.setTimestamp(5, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone()), DBDATE+DBTIME));
					pst.executeUpdate();
					pst.close();
//					System.out.println("insert pst =====> "+pst);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private void getRoundIds1() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();

		try {
			StringBuilder sb = new StringBuilder("");

			con = db.makeConnection(con);
			pst = con.prepareStatement("select distinct(round_id),recruitment_id from panel_interview_details where recruitment_id = ? order by round_id");
			pst.setInt(1,uF.parseToInt(getRecruitId()));
			rs = pst.executeQuery();
//			System.out.println("new Date =====> "+new Date());
			sb.append("<option value=\"\">" + "Select"+"</option>");
			while (rs.next()) {
				sb.append("<option value=\""+ rs.getString("round_id") + "\">" + "Round "+rs.getString("round_id")+"</option>");
			}
			rs.close();
			pst.close();
			request.setAttribute("option", sb.toString());

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	private void changePanelRound(UtilityFunctions uF) {
		// TODO Auto-generated method stub
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rst = null;
		try {
			boolean flag = false;
			con=db.makeConnection(con);

			pst=con.prepareStatement("select round_id from panel_interview_details where recruitment_id=? and round_id = ? and " +
					"panel_emp_id = ? ");
			pst.setInt(1,uF.parseToInt(getRecruitId()));
			pst.setInt(2, uF.parseToInt(getStrRound()));
			pst.setInt(3, uF.parseToInt(getEmpId()));
			rst=pst.executeQuery();
//			System.out.println("new Date =====> "+new Date());
			while(rst.next()){
				flag = true;
			}
			rst.close();
			pst.close();
			
//			System.out.println("flag ===> "+flag);
			if(flag == false){
				pst = con.prepareStatement("update panel_interview_details set round_id=?,added_by=?,added_date=? where recruitment_id=? and round_id = ? and " +
					"panel_emp_id = ? ");
				pst.setInt(1, uF.parseToInt(getStrRound()));
				pst.setInt(2, uF.parseToInt(strSessionEmpId));
				pst.setTimestamp(3, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone()), DBDATE+DBTIME));
				pst.setInt(4, uF.parseToInt(getRecruitId()));
				pst.setInt(5, uF.parseToInt(getRoundId()));
				pst.setInt(6, uF.parseToInt(getEmpId()));
				pst.executeUpdate();
				pst.close();
//				System.out.println("pst ===> "+pst);
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	private void getEmpIdsRoundwise(UtilityFunctions uF) {
		// TODO Auto-generated method stub
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rst = null;
		
		try {
			con=db.makeConnection(con);
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			Map<String, String> hmEmpProfile = CF.getEmpProfileImage(con);
				
			Map<String,String> hmEmpCode=new HashMap<String, String>();
	          pst=con.prepareStatement("select emp_per_id,empcode from employee_personal_details");
	          rst=pst.executeQuery();
//	          System.out.println("new Date =====> "+new Date());
	          while(rst.next()){
	        	  hmEmpCode.put(rst.getString("emp_per_id"), rst.getString("empcode"));
	          }
	          rst.close();
			  pst.close();
	    
	        Map<String, List<List<String>>> hmEmpIdsRoundwise = new LinkedHashMap<String, List<List<String>>>();  
			pst=con.prepareStatement("select distinct(panel_emp_id), round_id, recruitment_id from panel_interview_details where recruitment_id=? and panel_emp_id is not null order by panel_emp_id,round_id");
			pst.setInt(1,uF.parseToInt(getRecruitId()));
			rst=pst.executeQuery();
//			System.out.println("new Date =====> "+new Date());
			while(rst.next()){
				List<List<String>> listEmpIds = hmEmpIdsRoundwise.get(rst.getString("round_id"));
				if (listEmpIds == null)listEmpIds = new ArrayList<List<String>>();
				
				List<String> innerList = new ArrayList<String>();
				innerList.add(rst.getString("panel_emp_id"));
				if(rst.getString("panel_emp_id")!=null){
					innerList.add("<img style=\"margin-right:5px\" class=\"lazy img-circle\" width=\"18\" src=\"userImages/avatar_photo.png\" data-original=\"userImages/"+uF.showData(hmEmpProfile.get(rst.getString("panel_emp_id").trim()),"avatar_photo.png")+"\">"+hmEmpName.get(rst.getString("panel_emp_id").trim()) +" ["+hmEmpCode.get(rst.getString("panel_emp_id").trim())+"]");
				}else{
					innerList.add("");
				}
				listEmpIds.add(innerList);
				hmEmpIdsRoundwise.put(rst.getString("round_id"), listEmpIds);
			}
			rst.close();
			pst.close();
			
		request.setAttribute("hmEmpIdsRoundwise", hmEmpIdsRoundwise);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	private void deleteEmployeeFromRound(UtilityFunctions uF) {
		// TODO Auto-generated method stub
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rst = null;
		try {
			con=db.makeConnection(con);
			pst=con.prepareStatement("delete from panel_interview_details where recruitment_id=? and round_id = ? and panel_emp_id = ?");
			pst.setInt(1,uF.parseToInt(getRecruitId()));
			pst.setInt(2, uF.parseToInt(getRoundId()));
			pst.setInt(3, uF.parseToInt(getEmpId()));
			pst.executeUpdate();
			pst.close();
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	private void addEmployeeInRound(UtilityFunctions uF) {
		// TODO Auto-generated method stub
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rst = null;
		try {
			boolean flag = false;
			con=db.makeConnection(con);
//			String maxRoundId=null;
//			pst=con.prepareStatement("select max(round_id) as maxround from panel_interview_details where recruitment_id=?");
//			pst.setInt(1,uF.parseToInt(getRecruitId()));
//			rst=pst.executeQuery();
////			System.out.println("new Date =====> "+new Date());
//			while(rst.next()){
//				maxRoundId = rst.getString("maxround");
//			}
//			rst.close();
//			pst.close();
	
			pst=con.prepareStatement("select round_id from panel_interview_details where recruitment_id=? and round_id = ? and " +
					"panel_emp_id = ? ");
			pst.setInt(1,uF.parseToInt(getRecruitId()));
			pst.setInt(2, uF.parseToInt(getRoundId()));
			pst.setInt(3, uF.parseToInt(getEmpId()));
			rst=pst.executeQuery();
//			System.out.println("new Date =====> "+new Date());
			while(rst.next()){
				flag = true;
			}
			rst.close();
			pst.close();
			
			if(flag == false){
				pst = con.prepareStatement("insert into panel_interview_details(recruitment_id,round_id,panel_emp_id,added_by,added_date)" +
					"values(?,?,?,?,?)");
				pst.setInt(1, uF.parseToInt(getRecruitId()));
				pst.setInt(2, uF.parseToInt(getRoundId()));
				pst.setInt(3, uF.parseToInt(getEmpId()));
				pst.setInt(4, uF.parseToInt(strSessionEmpId));
				pst.setTimestamp(5, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone()), DBDATE+DBTIME));
				pst.executeUpdate();
				pst.close();
			}

			/*List<String> empIDList = new ArrayList<String>();
			pst=con.prepareStatement("select panel_emp_id from panel_interview_details where recruitment_id=?");
			pst.setInt(1,uF.parseToInt(getRecruitId()));
			rst=pst.executeQuery();
			while(rst.next()){
				if(rst.getString("panel_emp_id") != null && uF.parseToInt(rst.getString("panel_emp_id")) > 0){
					empIDList.add(rst.getString("panel_emp_id"));
				}
			}
			
			for(int i=0; empIDList != null && !empIDList.isEmpty() && i<empIDList.size(); i++) {
				String strDomain = request.getServerName().split("\\.")[0];
				UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
				userAlerts.setStrDomain(strDomain);
				userAlerts.setStrEmpId(empIDList.get(i));
				userAlerts.set_type(INTERVIEWS_ALERT);
				userAlerts.setStatus(INSERT_ALERT);
//				Thread t = new Thread(userAlerts);
//				t.run();
			}*/
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	private void getRoundIds(UtilityFunctions uF) {
		// TODO Auto-generated method stub
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rst = null;
		List<String> listRoundId = new ArrayList<String>();
		Map<String, String> hmRoundName = new HashMap<String, String>();
		
		try {
			con=db.makeConnection(con);
			Map<String, String> hmAssessmentName = CF.getAssessmentNameMap(con, uF);
			Map<String, String> hmRoundAssessment = new HashMap<String, String>();
			pst=con.prepareStatement("select distinct(round_id), recruitment_id, assessment_id from panel_interview_details where recruitment_id=? and panel_emp_id is null order by round_id");
			pst.setInt(1,uF.parseToInt(getRecruitId()));
			rst=pst.executeQuery();
			while(rst.next()){
				listRoundId.add(rst.getString("round_id"));
				hmRoundAssessment.put(rst.getString("round_id")+"_ASSESSID", rst.getString("assessment_id"));
				hmRoundAssessment.put(rst.getString("round_id")+"_ASSESSNAME", hmAssessmentName.get(rst.getString("assessment_id")));
			}
			rst.close();
			pst.close();
			
			pst=con.prepareStatement("select distinct(round_id), round_name, recruitment_id from panel_interview_details where recruitment_id=? and round_name is not null order by round_id");
			pst.setInt(1,uF.parseToInt(getRecruitId()));
			rst=pst.executeQuery();
			while(rst.next()){
				hmRoundName.put(rst.getString("round_id"), rst.getString("round_name"));
			}
			rst.close();
			pst.close();
			
//			System.out.println("listRoundId =====> " + listRoundId);
			request.setAttribute("hmRoundName", hmRoundName);
			request.setAttribute("listRoundId", listRoundId);
			request.setAttribute("hmRoundAssessment", hmRoundAssessment);
		
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	private void deleteRound(UtilityFunctions uF) {
		// TODO Auto-generated method stub
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con=db.makeConnection(con);
			pst=con.prepareStatement("delete from panel_interview_details where recruitment_id=? and round_id = ?");
			pst.setInt(1,uF.parseToInt(getRecruitId()));
			pst.setInt(2, uF.parseToInt(getRoundId()));
			pst.executeUpdate();
			pst.close();
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	private void addNewRound(UtilityFunctions uF) {
		// TODO Auto-generated method stub
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rst = null;
		try {
			boolean flag = false;
			con=db.makeConnection(con);
			pst=con.prepareStatement("select round_id from panel_interview_details where recruitment_id=? and round_id = ?");
			pst.setInt(1,uF.parseToInt(getRecruitId()));
			pst.setInt(2, uF.parseToInt(getRoundId()));
			rst=pst.executeQuery();
//			System.out.println("check id pst =====> " + pst);
//			System.out.println("new Date =====> "+new Date());
			while(rst.next()){
				flag = true;
			}
			rst.close();
			pst.close();
			
			if(flag == false){
				pst = con.prepareStatement("insert into panel_interview_details(recruitment_id,round_id,added_by,added_date)values(?,?,?,?)");
				pst.setInt(1, uF.parseToInt(getRecruitId()));
				pst.setInt(2, uF.parseToInt(getRoundId()));
				pst.setInt(3, uF.parseToInt(strSessionEmpId));
				pst.setTimestamp(4, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone()), DBDATE+DBTIME));
				pst.executeUpdate();
				pst.close();
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}


	public String getRecruitId() {
		return recruitId;
	}

	public void setRecruitId(String recruitId) {
		this.recruitId = recruitId;
	}

	public String getRoundId() {
		return roundId;
	}

	public void setRoundId(String roundId) {
		this.roundId = roundId;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public String getEmpId() {
		return empId;
	}

	public void setEmpId(String empId) {
		this.empId = empId;
	}

	public String getStrRound() {
		return strRound;
	}

	public void setStrRound(String strRound) {
		this.strRound = strRound;
	}

	public String getAllEmpId() {
		return allEmpId;
	}

	public void setAllEmpId(String allEmpId) {
		this.allEmpId = allEmpId;
	}



	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		// TODO Auto-generated method stub
		this.request = request;
	}

}
