package com.konnect.jpms.recruitment;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;

public class AutoGenerateRequirement extends HttpServlet implements Runnable,IStatements{
	private static final long serialVersionUID = 1L;
	Thread thread = null;
	
	public AutoGenerateRequirement() {
        super();
        // TODO Auto-generated constructor stub
    }
	@Override
	public void init() throws ServletException {
		// TODO Auto-generated method stub
		super.init();
		thread = new Thread(this);
		thread.start();
	}

	HttpSession session;
    String organisation;
    String strSessionEmpId = null;
    CommonFunctions CF = null; 
    
	private String getDesignationList() {
			Connection con = null;
			PreparedStatement pst = null;
			ResultSet rst = null;
			Database db = new Database();
			db.setRequest(request);
			UtilityFunctions uF = new UtilityFunctions();
			String location = "";
	
			con = db.makeConnection(con);
			try {
				pst = con.prepareStatement("select designation_id,level_id from designation_details order by designation_id");
//				pst.setInt(1, uF.parseToInt(strSessionEmpId2));
				rst = pst.executeQuery();
//				System.out.println("new Date ===> "+ new Date());
				while (rst.next()) {
//					location = rst.getString(1);
					setDesignation(rst.getString("designation_id"));
					setLevel(rst.getString("level_id"));
					setGrade(getDesignationGrade(con, getDesignation())); 
					setOrganisation(getDesignationOrg(con, getLevel()));
					setServices(getDesignationWServices(con, getOrganisation()));
					setLocation(getDesignationWLocation(con, getOrganisation()));
					setDepartment(getDesignationDepartment(con, getLocation()));
					String plannedcnt = getPlannedEmpCount(con, getDesignation());
					String requiredcnt = getRequirementEmpCount(con, getDesignation());
					String inductcnt = getInductEmpCount(con, getDesignation());
					int veriance = (uF.parseToInt(plannedcnt)+uF.parseToInt(requiredcnt))-uF.parseToInt(inductcnt);
					setPosition(veriance+"");
					if(veriance > 0){
					insertRecruitmentRequst(con, uF);
					}
				}
				rst.close();
				pst.close();
	
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				db.closeResultSet(rst);
				db.closeStatements(pst);
				db.closeConnection(con);
			}
	
			return location;
		}
	
	private String getDesignationGrade(Connection con, String desigid) {
		
		PreparedStatement pst = null;
		ResultSet rst = null;
		String gradeid = "";

		try {
			pst = con.prepareStatement("select grade_id from grades_details where designation_id="+ desigid +" order by grade_id limit 1");
			rst = pst.executeQuery();
//			System.out.println("new Date ===> "+ new Date());
			while (rst.next()) {
				gradeid = rst.getString(1);
			}
			rst.close();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rst != null) {
				try {
					rst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if(pst != null) {
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		} 

		return gradeid;
	}
	
private String getDesignationOrg(Connection con, String levelid) {
		
		PreparedStatement pst = null;
		ResultSet rst = null;
		UtilityFunctions uF = new UtilityFunctions();
		String orgid = "";

		try {
			pst = con.prepareStatement("select org_id from level_details where level_id="+ uF.parseToInt(levelid) +"");
			rst = pst.executeQuery();
//			System.out.println("new Date ===> "+ new Date());
			while (rst.next()) {
				orgid = rst.getString(1);
			}
			rst.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rst != null) {
				try {
					rst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if(pst != null) {
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		} 

		return orgid;
	}


private String getDesignationWLocation(Connection con, String orgid) {
	
	PreparedStatement pst = null;
	ResultSet rst = null;
	String wlocationid = "";

	try {
		pst = con.prepareStatement("select wlocation_id from work_location_info where org_id="+ orgid +" order by wlocation_id limit 1");
		rst = pst.executeQuery();
//		System.out.println("new Date ===> "+ new Date());
		while (rst.next()) {
			wlocationid = rst.getString(1);
		}
		rst.close();
		pst.close();
	} catch (Exception e) {
		e.printStackTrace();
	} finally {
		if(rst != null) {
			try {
				rst.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		if(pst != null) {
			try {
				pst.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
	} 
	return wlocationid;
}


private String getDesignationWServices(Connection con, String orgid) {
	
	PreparedStatement pst = null;
	ResultSet rst = null;
	String serviceid = "";

	try {
		pst = con.prepareStatement("select service_id from services where org_id="+ orgid +" order by service_id limit 1");
		rst = pst.executeQuery();
//		System.out.println("new Date ===> "+ new Date());
		while (rst.next()) {
			serviceid = rst.getString(1);
		}
		rst.close();
		pst.close();
	} catch (Exception e) {
		e.printStackTrace();
	} finally {
		if(rst != null) {
			try {
				rst.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		if(pst != null) {
			try {
				pst.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
	} 
	return serviceid;
}


private String getDesignationDepartment(Connection con, String wlocationid) {
	
	PreparedStatement pst = null;
	ResultSet rst = null;
	String departid = "";

	try {
		pst = con.prepareStatement("select dept_id from department_info where wlocation_id="+ wlocationid +" order by dept_id limit 1");
		rst = pst.executeQuery();
//		System.out.println("new Date ===> "+ new Date());
		while (rst.next()) {
			departid = rst.getString(1);
		}
		rst.close();
		pst.close();
	} catch (Exception e) {
		e.printStackTrace();
	} finally {
		if(rst != null) {
			try {
				rst.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		if(pst != null) {
			try {
				pst.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
	} 

	return departid;
}
	
	private String getPlannedEmpCount(Connection con, String desigid) {

		PreparedStatement pst = null;
		ResultSet rst = null;
		UtilityFunctions uF = new UtilityFunctions();
		String plannedcout="";
		try {
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.MONTH, -1);
			int month = calendar.get(Calendar.MONTH)+1;
			int year = calendar.get(Calendar.YEAR);
			StringBuilder sbQuery=new StringBuilder();
			sbQuery.append("select resource_requirement,rmonth,ryear from resource_planner_details where " +
					"designation_id =? and rmonth =? and ryear =?");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(desigid));
			pst.setInt(2, month);
			pst.setInt(3, year);
			rst = pst.executeQuery();
//			System.out.println("new Date ===> "+ new Date());
			while (rst.next()) {
				plannedcout =  rst.getString("resource_requirement");				
			}
			rst.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rst != null) {
				try {
					rst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if(pst != null) {
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		} 
	return plannedcout;
	}

	
	
	private String getRequirementEmpCount(Connection con, String desigid) {
		PreparedStatement pst = null;
		ResultSet rst = null;
		UtilityFunctions uF = new UtilityFunctions();
		String requiredcnt="";
		try {
			Calendar calendar = Calendar.getInstance();
		    calendar.add(Calendar.MONTH, -1);
		    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		    Date currdt = calendar.getTime();
//		    String currdate = sdf.format(currdt);
		    int min = calendar.getActualMinimum(Calendar.DAY_OF_MONTH);
		    int max = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		     calendar.set(Calendar.DAY_OF_MONTH, min);
		     Date fstdt = calendar.getTime();
		     String firstdate = sdf.format(fstdt);
		     calendar.set(Calendar.DAY_OF_MONTH, max);
		     Date lstdt = calendar.getTime();
		     String lastdate = sdf.format(lstdt);
//			  System.out.println("lastdate : " + lastdate);
//			  System.out.println("firstdate : " + firstdate);
			StringBuilder sbQuery=new StringBuilder();
			sbQuery.append("select no_position,designation_id from recruitment_details where effective_date is not null ");
			/*if(uF.parseToInt(getF_strWLocation())>0){
				sbQuery.append(" and wlocation="+uF.parseToInt(getF_strWLocation()));
			}
			if(uF.parseToInt(getF_service())>0){   
				sbQuery.append(" and services="+uF.parseToInt(getF_service()));	
			}
			if(uF.parseToInt(getF_level())>0){
				sbQuery.append(" and level_id="+uF.parseToInt(getF_level()));	
			}
			if(uF.parseToInt(getF_department())>0){
				sbQuery.append(" and dept_id = "+uF.parseToInt(getF_department()));
			}
			*/
			sbQuery.append("and effective_date between ? and ? and designation_id =?");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1,uF.getDateFormat(firstdate, "dd/MM/yyyy"));
			pst.setDate(2,uF.getDateFormat(lastdate, "dd/MM/yyyy"));
			pst.setInt(3, uF.parseToInt(desigid));
			rst = pst.executeQuery();
//			System.out.println("new Date ===> "+ new Date());
			while (rst.next()) {
				requiredcnt = rst.getString("no_position");
			}
			rst.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rst != null) {
				try {
					rst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if(pst != null) {
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		} 
		return requiredcnt;
	}

	private String getInductEmpCount(Connection con, String desigid) {
		PreparedStatement pst = null;
		ResultSet rst = null;
		UtilityFunctions uF = new UtilityFunctions();
		String inductcnt="";
 		try {
			Calendar calendar = Calendar.getInstance();
		    calendar.add(Calendar.MONTH, -1);
		    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		    Date currdt = calendar.getTime();
//		    String currdate = sdf.format(currdt);
		    int min = calendar.getActualMinimum(Calendar.DAY_OF_MONTH);
		    int max = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		     calendar.set(Calendar.DAY_OF_MONTH, min);
		     Date fstdt = calendar.getTime();
		     String firstdate = sdf.format(fstdt);
		     calendar.set(Calendar.DAY_OF_MONTH, max);
		     Date lstdt = calendar.getTime();
		     String lastdate = sdf.format(lstdt);
//			  System.out.println("lastdate : " + lastdate);
//			  System.out.println("firstdate : " + firstdate);
			StringBuilder sbQuery=new StringBuilder(); 
			sbQuery.append(" select count(*)as count from candidate_personal_details cpd " +
					" inner join recruitment_details rd on cpd.recruitment_id=rd.recruitment_id where candidate_status=1 " +
					" and candidate_final_status=1 and candidate_joining_date is not null ");
			
			/*if(uF.parseToInt(getF_strWLocation())>0){
				sbQuery.append(" and rd.wlocation="+uF.parseToInt(getF_strWLocation()));
			}
			
			if(uF.parseToInt(getF_service())>0){    
				sbQuery.append(" and rd.services="+uF.parseToInt(getF_service()));	
			}
			
			if(uF.parseToInt(getF_level())>0){
				sbQuery.append(" and rd.level_id="+uF.parseToInt(getF_level()));	
			}
			
			if(uF.parseToInt(getF_department())>0){
				sbQuery.append(" and rd.dept_id = "+uF.parseToInt(getF_department()));
			}*/
			
			sbQuery.append("and candidate_joining_date between ? and ? and designation_id = ?");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1,uF.getDateFormat(firstdate, "dd/MM/yyyy"));
			pst.setDate(2,uF.getDateFormat(lastdate, "dd/MM/yyyy"));
			pst.setInt(3, uF.parseToInt(desigid));
			rst = pst.executeQuery();
//			System.out.println("new Date ===> "+ new Date());
			while (rst.next()) {
				inductcnt = rst.getString("count");
			}
			rst.close();
			pst.close();
			
		} catch (Exception e) {
			
			e.printStackTrace();
			
		} finally {
			if(rst != null) {
				try {
					rst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if(pst != null) {
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		} 
		return inductcnt;
	}


	private String insertRecruitmentRequst(Connection con, UtilityFunctions uF) {
		PreparedStatement pst = null;
		ResultSet rst = null;

		try {
			
			Calendar calendar = Calendar.getInstance();
//		    calendar.add(Calendar.MONTH, -1);
		    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		    Date currdt = calendar.getTime();
		    String currdate = sdf.format(currdt);
		    
		     int min = calendar.getActualMinimum(Calendar.DAY_OF_MONTH);
		    int max = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		    calendar.set(Calendar.DAY_OF_MONTH, min);
		    Date fstdt = calendar.getTime();
		     String firstdate = sdf.format(fstdt);
		     calendar.set(Calendar.DAY_OF_MONTH, max);
		     Date lstdt = calendar.getTime();
		     String lastdate = sdf.format(lstdt);
//			  System.out.println("lastdate : " + lastdate);
			  
			String jobCode = "";
			pst=con.prepareStatement("select wloacation_code,designation_code,custum_designation from recruitment_details " +
					" left  join designation_details using (designation_id) join work_location_info on (wlocation_id=wlocation)" +
					" where recruitment_id=? ");
			pst.setInt(1, uF.parseToInt(getDesignation()));
			rst=pst.executeQuery();
//			System.out.println("new Date ===> "+ new Date());
			if(rst.next()){
				if(rst.getString("designation_code")==null)
					jobCode+=rst.getString("wloacation_code")+"-NEW";
				else
					jobCode+=rst.getString("wloacation_code")+"-"+rst.getString("designation_code");
			}
			rst.close();
			pst.close();
			
			pst = con.prepareStatement("select count(*) as count from recruitment_details where job_code like '"+jobCode+"%'");
			rst = pst.executeQuery();
			
			while (rst.next()) {
				int count=uF.parseToInt(rst.getString("count"));
					count++;
					// Conversion to 3 decimal places
					DecimalFormat decimalFormat = new DecimalFormat();
					decimalFormat.setMinimumIntegerDigits(3);
					
				jobCode+="-"+decimalFormat.format(count);
			}
			rst.close();
			pst.close();
			
				String query = "insert into recruitment_details(designation_id,grade_id,"
						+ "no_position,effective_date,comments,wlocation,entry_date,added_by,skills,services,dept_id,level_id," +
								"priority_job_int,target_deadline,org_id,ideal_candidate,status,requirement_status,approved_by," +
								"approved_date,job_code)"
						+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
				pst = con.prepareStatement(query);
				pst.setInt(1, uF.parseToInt(getDesignation()));
				pst.setInt(2, uF.parseToInt(getGrade()));
				pst.setInt(3, uF.parseToInt(getPosition())); //
				pst.setDate(4, uF.getDateFormat(firstdate, "dd/MM/yyyy"));
				pst.setString(5, "");
				pst.setInt(6, uF.parseToInt(getLocation()));
				pst.setDate(7, uF.getDateFormat(currdate, "dd/MM/yyyy"));
				pst.setInt(8, uF.parseToInt(getStrSessionEmpId())); //
				pst.setString(9, "");
				pst.setInt(10, uF.parseToInt(getServices())); //
				pst.setInt(11, uF.parseToInt(getDepartment())); //
				pst.setInt(12, uF.parseToInt(getLevel())); //
				pst.setInt(13, 1);
//				pst.setString(14, getCustumdesignation());
				pst.setDate(14,uF.getDateFormat(lastdate, "dd/MM/yyyy"));
				pst.setInt(15, uF.parseToInt(getOrganisation()));
				pst.setString(16, "");
//				pst.setString(18, getCustumgrade());
				pst.setInt(17, 1);
				pst.setString(18, "autogenerate");
				pst.setInt(19, uF.parseToInt(getStrSessionEmpId()));
				pst.setDate(20, uF.getDateFormat(currdate, "dd/MM/yyyy"));
				pst.setString(21, jobCode);
				pst.executeUpdate();
				pst.close(); 
					
				int existingEmpCount=0;
				
				if(uF.parseToInt(getDesignation())!=0){
				pst=con.prepareStatement("Select count(*) as count from employee_official_details join grades_details" +
						" using (grade_id) where designation_id=?");
				pst.setInt(1, uF.parseToInt(getDesignation()));
				rst=pst.executeQuery();
//				System.out.println("new Date ===> "+ new Date());
				while(rst.next()){
					existingEmpCount=rst.getInt("count");	
				}
				rst.close();
				pst.close();

				}
				
				pst=con.prepareStatement("update recruitment_details set existing_emp_count="+existingEmpCount+" where " +
						"recruitment_id=(select max(recruitment_id) from recruitment_details)");
				pst.execute();
				pst.close();
//				System.out.println("pst=="+pst);
				
				pst = con.prepareStatement("select emp_id from user_details where usertype_id=7");
				rst = pst.executeQuery();
				hrIDList = new ArrayList<String>();
				while (rst.next()) {
					hrIDList.add(rst.getString(1));
				}
				pst.close();
				pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rst != null) {
				try {
					rst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if(pst != null) {
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		} 
		return "a";
	}
	

	private HttpServletRequest request;

	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public String getOrganisation() {
		return organisation;
	}

	public void setOrganisation(String organisation) {
		this.organisation = organisation;
	}
	
	String idealCandidate;

	public String getIdealCandidate() {
		return idealCandidate;
	}

	public void setIdealCandidate(String idealCandidate) {
		this.idealCandidate = idealCandidate;
	}
	
	
	String grade;
	String designation;
	String level;
	String department;
	String location;
	String position;
	String rdate;
	String notes;
	
    String[] skills;
	public String[] getSkills() {
		return skills;
	}

	public void setSkills(String[] skills) {
		this.skills = skills;
	}
	String services;

	// addition of fields********
	String priority;
	String targetdeadline;

		public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

		public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

		public String getGrade() {
		return grade;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}

	public String getDesignation() {
		return designation;
	}

	public void setDesignation(String designation) {
		this.designation = designation;
	}

		public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public String getTargetdeadline() {
		return targetdeadline;
	}

	public void setTargetdeadline(String targetdeadline) {
		this.targetdeadline = targetdeadline;
	}

	

	List<String> hrIDList;

	public List<String> getHrIDList() {
		return hrIDList;
	}

	public void setHrIDList(List<String> hrIDList) {
		this.hrIDList = hrIDList;
	}

	public String getServices() {
		return services;
	}

	public void setServices(String services) {
		this.services = services;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public String getRdate() {
		return rdate;
	}

	public void setRdate(String rdate) {
		this.rdate = rdate;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}
	
	public String getStrSessionEmpId() {
		return strSessionEmpId;
	}
	public void setStrSessionEmpId(String strSessionEmpId) {
		this.strSessionEmpId = strSessionEmpId;
	}
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(true){
			if(Thread.currentThread()==thread){
//					notificationSendToMcrid(sbDayQuery.toString());
				session = request.getSession();
				CF = (CommonFunctions) session.getAttribute(CommonFunctions);
				if (CF != null){
					strSessionEmpId = (String) session.getAttribute(EMPID);
					Calendar calendar = Calendar.getInstance();
					int todaydate = calendar.get(Calendar.DATE);
					if(todaydate == 1){
					getDesignationList();
					}
					try {
						Thread.sleep(2*60*60*1000);
						System.gc();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}	
}
