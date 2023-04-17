package com.konnect.jpms.task;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.konnect.jpms.select.FillClientPoc;
import com.konnect.jpms.select.FillClients;
import com.konnect.jpms.select.FillEmployee;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.select.FillSkills;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
public class AddNewProject extends ActionSupport implements
		ServletRequestAware, ServletResponseAware, IStatements {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String prjectname;
	private String prjectCode;
	private String priority;
	private String service; 
	private String taskstatus;
	private String deadline; 
	private String startDate;
	private String alreadyworked;
	private String completed;
	private String time;
	private File document;
	private String documentContentType;
	private String documentFileName;
	String[] docid; 
	String[] update_doc_title;
	String[] doc_name1;
	private File[] document1;
	private String[] document1FileName;
	private String[] document1ContentType;
	String filename;
	String doc_name;
	String description;
	String[] teamleadId;
	String[] empId;
	String[] skillsName;
	String pro_id;
	Map session;
	
	CommonFunctions CF;
	HttpSession session1;
	String operation;
	List<FillServices> serviceList;
	List<FillEmployee> projectOwnerList;
	String strProjectOwner;
	private String serviceId;
	private String ServiceName; 
	List<FillEmployee> empNamesList;
	List<FillSkills> skillList;
	List<GetPriorityList> priorityList;
	private String priId;
	private String proName;
	List<FillClients> clientList;
	List<FillClientPoc> clientPocList;   
	String clientPoc;
	String clientId;
	String client;
	String clientPocId;
	String clientPocName;
	String clientName;
	String filePath;
	String[] skill;
	List<FillEmployee> teamleadNamesList;
	List<FillDaysList> daysList;
	String[] days;
	String months;
	String isMonthly;
	UtilityFunctions uF = new UtilityFunctions();

	public String execute() throws Exception {
//		System.out.println("Month is "+months);
		session = ActionContext.getContext().getSession();
		session1 = request.getSession();
		CF = (CommonFunctions) session1.getAttribute(CommonFunctions);
		if (CF == null)
		{
//			System.out.println("Add new Project line no- 114");
			return LOGIN;
		}
		
		priorityList = new GetPriorityList().fillPriorityList();
		daysList=new FillDaysList().fillDayList();
		if (getOperation() != null) {
			String s = performOperation();
			
			return s;
		} else {
//			empNamesList = new FillEmployee().fillEmployeeNameByServiceID(service);
			empNamesList = new FillEmployee(request).fillEmployeeName(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, DATE_FORMAT), 588);
			projectOwnerList = new FillEmployee(request).fillEmployeeNameByParentLevel(0);
			skillList=new FillSkills(request).fillSkills(service);
			insertProjectDetails();
			
//			System.out.println("Add new project line no - 128 ");
			return SUCCESS;
		}

		// return SUCCESS;
	}

	public String performOperation() {
		String s = "success";
		
//		empNamesList = new FillEmployee().fillEmployeeName(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, DATE_FORMAT), 588);
		
		if (getOperation() != null && prjectname == null) {
			serviceList = new FillServices(request).fillServices();
			clientList = new FillClients(request).fillClients(false);
			
			getProjectDetailsToUpdate();
			clientPocList = new FillClientPoc(request).fillClientPoc(getClient());
			
			s = "edit";
		} else if (getOperation() != null && uF.parseToInt(getPro_id()) > 0
				&& prjectname != null) {
			updateProjectDetails();
			deletePreviousTeam();
			deletePreviousSkillDetails();
			insertSkillDetails();
			projectTeamLeadDetails();
			projectEmpDetails(); 
			// setOperation(null);
			s = "success";
		} else if (getOperation() != null
				&& getOperation().equalsIgnoreCase("I")) {
			
			
			
			empNamesList = new FillEmployee(request).fillEmployeeName(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, DATE_FORMAT), 588); // Partner
			projectOwnerList = new FillEmployee(request).fillEmployeeNameByParentLevel(0);
//			empNamesList = new FillEmployee().fillEmployeeNameByServiceID(service);
			insertProjectDetails();
			selectProId();
			projectTeamLeadDetails();
			projectEmpDetails();
			insertSkillDetails();
			
			s = "success";
		}
		return s;

	}

	public void deletePreviousTeam() {

		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("DELETE FROM project_emp_details WHERE pro_id=?");
			pst.setInt(1, uF.parseToInt(getPro_id()));
			pst.executeUpdate();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	public void deletePreviousSkillDetails() {
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("DELETE FROM project_skill_details WHERE pro_id=?");
			pst.setInt(1, uF.parseToInt(getPro_id()));
			pst.executeUpdate();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	public void getProjectDetailsToUpdate() {
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from projectmntnc where pro_id=?");
			pst.setInt(1, uF.parseToInt(pro_id));
			rs = pst.executeQuery();

			while (rs.next()) {
				setPrjectname(rs.getString("pro_name"));
				setService(rs.getString("service"));
				setDescription(rs.getString("description"));
				setPriority(rs.getString("priority"));
				setDeadline(uF.getDateFormat(rs.getString("deadline"), DBDATE,DATE_FORMAT));
				setDoc_name(rs.getString("document_name2"));
				setClient(rs.getString("client_id"));
				setPrjectCode(rs.getString("project_code"));
				setClientPoc(rs.getString("poc"));
				setStartDate(uF.getDateFormat(rs.getString("start_date"), DBDATE,DATE_FORMAT));
				setIsMonthly(rs.getString("ismonthly"));
				setMonths(rs.getString("months"));
				 
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from project_documents_details where pro_id=?");
			pst.setInt(1, uF.parseToInt(pro_id));
			rs = pst.executeQuery();
			StringBuilder sb = new StringBuilder();
			StringBuilder sb1 = new StringBuilder();
			StringBuilder sb2 = new StringBuilder();
						
			int i=0;
			while(rs.next()){	
				if(i==0){
					sb.append(rs.getString("doc_id"));		
					sb1.append(rs.getString("doc_path"));
					sb2.append(rs.getString("doc_name"));
					
				}else{
					sb.append(","+rs.getString("doc_id"));		
					sb1.append(","+rs.getString("doc_path"));
					sb2.append(","+rs.getString("doc_name"));
				}
				i++;
			}
			rs.close();
			pst.close();
			
			request.setAttribute("docSrNo", sb.toString());
			request.setAttribute("docName", sb1.toString());
			request.setAttribute("docTitle", sb2.toString());
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		selectSkills();
		skillList = new FillSkills(request).fillSkills(service);
		empNamesList =new FillEmployee(request).fillEmployeeNameBySkills(skill, CF);
		teamleadNamesList=new FillEmployee(request).fillEmployeeNameBySkills(skill, CF);
		
		selectProTeamLeadDetails();
		selectProEmpDetails();

	}
	
	public void selectSkills() {
		StringBuilder sb = new StringBuilder();
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select skill_id from project_skill_details where pro_id=?");
			pst.setInt(1, uF.parseToInt(pro_id));
			rs = pst.executeQuery();

			while (rs.next()) {
				sb.append(CF.getSkillNameBySkillId(con, rs.getString("skill_id")) + ",");
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
		if (sb != null) {
			skill= sb.toString().split(",");
		}

	}
	public void selectProTeamLeadDetails() {
		StringBuilder sb = new StringBuilder();
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from project_emp_details where pro_id=? and _isteamlead = true");
			pst.setInt(1, uF.parseToInt(pro_id));
			rs = pst.executeQuery();

			while (rs.next()) {
				sb.append(rs.getString("emp_id") + ",");
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
		if (sb != null) {
			teamleadId = sb.toString().split(",");
		}

	}

	public void selectProEmpDetails() {
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from project_emp_details where pro_id=? and _isteamlead = false");
			pst.setInt(1, uF.parseToInt(pro_id));
			rs = pst.executeQuery();
			while (rs.next()) {
				sb.append(rs.getString("emp_id") + ",");
				
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
		
//		System.out.println("=====+>"+sb.toString());
		
		if (sb != null) {
			empId = sb.toString().split(",");
		}
		// setOperation("U");
	}

	public void updateProjectDetails() {

		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
//		ResultSet rs = null;
		try {
			
			con = db.makeConnection(con);
			pst = con.prepareStatement("UPDATE projectmntnc SET pro_name=?,priority=?,description=?,service=?,deadline=?,client_id=?, project_code=?, poc=?, start_date=? WHERE pro_id=?");

			pst.setString(1, getPrjectname());
			pst.setString(2, getPriority());
			pst.setString(3, getDescription());
			pst.setString(4, getService());
			pst.setDate(5, uF.getDateFormat(getDeadline(), DATE_FORMAT));
			/*pst.setString(6, filename);
			pst.setString(7, getDoc_name());*/
			pst.setInt(6, uF.parseToInt(getClient()));
			pst.setString(7, getPrjectCode());
			pst.setInt(8, uF.parseToInt(getClientPoc()));
			pst.setDate(9, uF.getDateFormat(getStartDate(), DATE_FORMAT));
			pst.setInt(10, uF.parseToInt(pro_id));
			pst.executeUpdate();
			pst.close();
		} catch (Exception e) {

			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		updateDocuments();
	}
	
	
	public void updateDocuments() {

		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		
		try {
			con = db.makeConnection(con);
			for(int i=0;getDocid()!=null && i<getDocid().length;i++) 
			{
				pst = con.prepareStatement("UPDATE project_documents_details SET doc_name=? WHERE doc_id=?");
				pst.setString(1, uF.showData(getUpdate_doc_title()[i],"f"));
				pst.setInt(2,uF.parseToInt(getDocid()[i]));
				pst.executeUpdate();
				pst.close();
			}
			
			if(getDocument1()!=null)
			{
				for(int a=0;a<getDocument1().length;a++)
				{
					setFilePath(upload(getDocument1()[a], document1FileName[a]));	
					pst = con.prepareStatement("insert into project_documents_details(doc_path,pro_id,doc_name) values(?,?,?)");
					pst.setString(1, getFilePath());
					pst.setInt(2,uF.parseToInt(pro_id) );
					pst.setString(3, getDoc_name1()[a]);
					pst.executeUpdate();
					pst.close();
				}
			}
			
			
		} catch (Exception e) {

			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	

	public void insertProjectDetails() {
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		boolean isMonthly=false;
		try {
			con = db.makeConnection(con);
			/*if (document != null) {
				upload(document, documentFileName);
			}*/
			String workingDays="";
			for(int i=0;getDays()!=null && i<getDays().length;i++){
				
				workingDays+=getDays()[i]+",";
				
			}
			
			
//			System.out.println("Get Month is"+getMonths().equalsIgnoreCase(""));
			if(!getMonths().equalsIgnoreCase("")){
				isMonthly=true;
				
			DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		    String newStartPeriod = null;
		    String newEndPeriod = null;
		    String oldStartPeriod = getStartDate();
		    String oldEndPeriod = getDeadline();
			for(int i=0;getMonths()!=null && i< uF.parseToInt(getMonths());i++){
				
			Date startdate = (Date)formatter.parse(oldStartPeriod);
			Date enddate = (Date)formatter.parse(oldEndPeriod);
		    Calendar calstart=Calendar.getInstance();
		    Calendar calend=Calendar.getInstance();

		    calstart.setTime(startdate);
			calend.setTime(enddate);

			calstart.add(Calendar.MONTH, i);
			calend.add(Calendar.MONTH, i);

			newStartPeriod = sdf.format(calstart.getTime());
			newEndPeriod = sdf.format(calend.getTime());
				
				
					
			pst = con.prepareStatement("insert into projectmntnc(pro_name,priority,description,activity,service,taskstatus,deadline,already_work,completed,timestatus,approve_status, client_id, project_code, poc, start_date, added_by,working_days,ismonthly,months) values(?,?,?,?,?,'New Task',?,'0','0','n','n',?,?,?,?,?,?,?,?)");
			pst.setString(1, getPrjectname());
			pst.setString(2, getPriority());
			pst.setString(3, getDescription());
			pst.setString(4, getPrjectname());
			pst.setString(5, getService());
			pst.setDate(6, uF.getDateFormat(newEndPeriod, DATE_FORMAT));
			pst.setInt(7, uF.parseToInt(getClient()));
			pst.setString(8, getPrjectCode());
			pst.setInt(9, uF.parseToInt(getClientPoc()));
			pst.setDate(10, uF.getDateFormat(newStartPeriod, DATE_FORMAT));
			pst.setInt(11, uF.parseToInt((String)session.get(EMPID)));
			pst.setString(12, workingDays);
			pst.setBoolean(13,isMonthly);
			pst.setString(14,getMonths());
			pst.executeUpdate();
			pst.close();
			
			insertDocuments();
			selectProId();
//			projectTeamLeadDetails();
//			projectEmpDetails();
		
			}
			}else{
				pst = con.prepareStatement("insert into projectmntnc(pro_name,priority,description,activity,service,taskstatus,deadline,already_work,completed,timestatus,approve_status, client_id, project_code, poc, start_date, added_by,working_days,ismonthly,months) values(?,?,?,?,?,'New Task',?,'0','0','n','n',?,?,?,?,?,?,?,?)");
				pst.setString(1, getPrjectname());
				pst.setString(2, getPriority());
				pst.setString(3, getDescription());
				pst.setString(4, getPrjectname());
				pst.setString(5, getService());
				pst.setDate(6, uF.getDateFormat(getDeadline(), DATE_FORMAT));
				pst.setInt(7, uF.parseToInt(getClient()));
				pst.setString(8, getPrjectCode());
				pst.setInt(9, uF.parseToInt(getClientPoc()));
				pst.setDate(10, uF.getDateFormat(getStartDate().substring(2,12), DATE_FORMAT));
				pst.setInt(11, uF.parseToInt((String)session.get(EMPID)));
				pst.setString(12, workingDays);
				pst.setBoolean(13,isMonthly);
				pst.setString(14,getMonths());
				pst.executeUpdate();
				pst.close();
				
				insertDocuments();
				selectProId();
//				projectTeamLeadDetails();
//				projectEmpDetails();
			}
			
			
		} catch (Exception e) {

			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		

	}
	public void insertDocuments()
	{
		int proid=0;
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			con = db.makeConnection(con);
		if(getDocument1()!=null)
		{
			for(int i=0;i<getDocument1().length;i++)
			{
				setFilePath(upload(getDocument1()[i], document1FileName[i]));	
			
				pst = con.prepareStatement("select max(pro_id) from projectmntnc");
				rs = pst.executeQuery();
				while(rs.next())
				{
					proid=rs.getInt(1);
					
				}
				rs.close();
				pst.close();
				
				pst = con.prepareStatement("insert into project_documents_details(doc_path,pro_id,doc_name) values(?,?,?)");
				pst.setString(1, getFilePath());
				pst.setInt(2, proid);
				pst.setString(3, getDoc_name1()[i]);
				pst.executeUpdate();
				pst.close();
				
			}
			
		}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}
	
	public String upload(File file, String fileFileName) throws Exception {

		double randomname = Math.random();
        String random = randomname + "";
        random = random.replace("0.", "");
        if (fileFileName.contains(" ")) {
            fileFileName = fileFileName.replace(" ", "");
        }
        // the directory to upload to
        String uploadDir = ServletActionContext.getServletContext()
                .getRealPath("/taskuploads") + "/";

        // write the file to the file specified
        File dirPath = new File(uploadDir);

        if (!dirPath.exists()) {
            dirPath.mkdirs();
        }

        // retrieve the file data
        InputStream stream = new FileInputStream(file);

        // write the file to the file specified
        OutputStream bos = new FileOutputStream(uploadDir + random
                + fileFileName);
        int bytesRead;
        byte[] buffer = new byte[8192];

        while ((bytesRead = stream.read(buffer, 0, 8192)) != -1) {
            bos.write(buffer, 0, bytesRead);
        }

        bos.close();
        stream.close();

        // place the data into the request for retrieval on next page
        request.setAttribute("location", dirPath.getAbsolutePath() + "/"
                + fileFileName);
        // log.debug("location" + dirPath.getAbsolutePath() + "/" +
        // fileFileName);

//        String link = request.getContextPath() + "/";
//
//        request.setAttribute("link", link + fileFileName);
        // log.debug("link" + link + fileFileName);

        return "taskuploads/"+random + fileFileName;
	
	}
	
	
	
	public void selectProId() {

		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			con = db.makeConnection(con);
			pst = con
					.prepareStatement("select max(pro_id) as pro_id from projectmntnc");
			rs = pst.executeQuery();
			while (rs.next()) {
				pro_id = rs.getString("pro_id");

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

	}

	public void projectTeamLeadDetails() {

		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		try {
			
//			System.out.println("getTeamleadId().length=="+getTeamleadId().length);
			
			
			con = db.makeConnection(con);
			for (int i = 0; i < getTeamleadId().length; i++) {
				
//				System.out.println("===>"+getTeamleadId()[i]);
				
				pst = con.prepareStatement("insert into project_emp_details(pro_id,emp_id,_isteamlead) values(?,?,true)");
				pst.setInt(1, uF.parseToInt(pro_id));
				pst.setInt(2, uF.parseToInt(getTeamleadId()[i]));
				pst.executeUpdate();
				pst.close();
			}
		} catch (Exception e) {

			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}
	public void insertSkillDetails() {

		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		try {
			con = db.makeConnection(con);
			for (int i = 0; i < getSkill().length; i++) {
				pst = con.prepareStatement("insert into project_skill_details(pro_id,skill_id) values(?,?)");
				pst.setInt(1, uF.parseToInt(pro_id));
				pst.setInt(2, uF.parseToInt(getSkill()[i]));
				pst.executeUpdate();
				pst.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}

	public void projectEmpDetails() {
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		try {
			con = db.makeConnection(con);
			for (int i = 0; i < getEmpId().length; i++) {
				pst = con
						.prepareStatement("insert into project_emp_details(pro_id,emp_id) values(?,?)");
				pst.setInt(1, uF.parseToInt(pro_id));
				pst.setInt(2, uF.parseToInt(getEmpId()[i]));
				pst.executeUpdate();
				pst.close();
			}
		} catch (Exception e) {

			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	public List<FillEmployee> getEmpNamesList() {
		return empNamesList;
	}

	public void setEmpNamesList(List<FillEmployee> empNamesList) {
		this.empNamesList = empNamesList;
	}

	public List<FillDaysList> getDaysList() {
		return daysList;
	}

	public void setDaysList(List<FillDaysList> daysList) {
		this.daysList = daysList;
	}

	public String[] getDays() {
		return days;
	}

	public void setDays(String[] days) {
		this.days = days;
	}

	public String getDoc_name() {
		return doc_name;
	}

	public void setDoc_name(String doc_name) {
		this.doc_name = doc_name;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	/*public boolean isMonthly() {
		return isMonthly;
	}

	public void setMonthly(boolean isMonthly) {
		this.isMonthly = isMonthly;
	}*/

	public String getClientPocId() {
		return clientPocId;
	}

	public void setClientPocId(String clientPocId) {
		this.clientPocId = clientPocId;
	}

	public String getIsMonthly() {
		return isMonthly;
	}

	public void setIsMonthly(String isMonthly) {
		this.isMonthly = isMonthly;
	}

	public String getClientPocName() {
		return clientPocName;
	}

	public void setClientPocName(String clientPocName) {
		this.clientPocName = clientPocName;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public List<FillEmployee> getTeamleadNamesList() {
		return teamleadNamesList;
	}

	public void setTeamleadNamesList(List<FillEmployee> teamleadNamesList) {
		this.teamleadNamesList = teamleadNamesList;
	}

	public List<FillServices> getServiceList() {
		return serviceList;
	}

	public String[] getSkillsName() {
		return skillsName;
	}

	public void setSkillsName(String[] skillsName) {
		this.skillsName = skillsName;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String[] getDoc_name1() {
		return doc_name1;
	}

	public void setDoc_name1(String[] doc_name1) {
		this.doc_name1 = doc_name1;
	}

	public File[] getDocument1() {
		return document1;
	}

	public void setDocument1(File[] document1) {
		this.document1 = document1;
	}

	public String getMonths() {
		return months;
	}

	public void setMonths(String months) {
		this.months = months;
	}

	public String[] getDocid() {
		return docid;
	}

	public void setDocid(String[] docid) {
		this.docid = docid;
	}

	public String[] getUpdate_doc_title() {
		return update_doc_title;
	}

	public void setUpdate_doc_title(String[] update_doc_title) {
		this.update_doc_title = update_doc_title;
	}

	public String[] getDocument1FileName() {
		return document1FileName;
	}

	public void setDocument1FileName(String[] document1FileName) {
		this.document1FileName = document1FileName;
	}

	public String[] getDocument1ContentType() {
		return document1ContentType;
	}

	public void setDocument1ContentType(String[] document1ContentType) {
		this.document1ContentType = document1ContentType;
	}

	public void setServiceList(List<FillServices> serviceList) {
		this.serviceList = serviceList;
	}

	public String getServiceId() {
		return serviceId;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	public String getServiceName() {
		return ServiceName;
	}

	public void setServiceName(String serviceName) {
		ServiceName = serviceName;
	}

	public String[] getSkill() {
		return skill;
	}

	public void setSkill(String[] skill) {
		this.skill = skill;
	}

	public List<FillClients> getClientList() {
		return clientList;
	}

	public void setClientList(List<FillClients> clientList) {
		this.clientList = clientList;
	}

	public List<FillClientPoc> getClientPocList() {
		return clientPocList;
	}

	public void setClientPocList(List<FillClientPoc> clientPocList) {
		this.clientPocList = clientPocList;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getPro_id() {
		return pro_id;
	}

	public void setPro_id(String pro_id) {
		this.pro_id = pro_id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String[] getTeamleadId() {
		return teamleadId;
	}

	public void setTeamleadId(String[] teamleadId) {
		this.teamleadId = teamleadId;
	}

	public String[] getEmpId() {
		return empId;
	}

	public void setEmpId(String[] empId) {
		this.empId = empId;
	}

	public String getPriId() {
		return priId;
	}

	public void setPriId(String priId) {
		this.priId = priId;
	}

	public String getProName() {
		return proName;
	}

	public void setProName(String proName) {
		this.proName = proName;
	}

	public List<GetPriorityList> getPriorityList() {
		return priorityList;
	}

	public void setPriorityList(List<GetPriorityList> priorityList) {
		this.priorityList = priorityList;
	}

	private HttpServletRequest servletRequest;

	public HttpServletRequest getServletRequest() {
		return servletRequest;
	}

	public File getDocument() {
		return document;
	}

	public void setDocument(File document) {
		this.document = document;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getTaskstatus() {
		return taskstatus;
	}

	public void setTaskstatus(String taskstatus) {
		this.taskstatus = taskstatus;
	}

	public String getDeadline() {
		return deadline;
	}

	public void setDeadline(String deadline) {
		this.deadline = deadline;
	}

	public String getAlreadyworked() {
		return alreadyworked;
	}

	public void setAlreadyworked(String alreadyworked) {
		this.alreadyworked = alreadyworked;
	}

	public String getCompleted() {
		return completed;
	}

	public void setCompleted(String completed) {
		this.completed = completed;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getPrjectname() {
		return prjectname;
	}

	public void setPrjectname(String prjectname) {
		this.prjectname = prjectname;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

//	public String getFilename() {
//		return filename;
//	}
//
//	public void setFilename(String filename) {
//		this.filename = filename;
//	}

	public String getDocumentContentType() {
		return documentContentType;
	}

	public void setDocumentContentType(String documentContentType) {
		this.documentContentType = documentContentType;
	}

	public String getDocumentFileName() {
		return documentFileName;
	}

	public void setDocumentFileName(String documentFileName) {
		this.documentFileName = documentFileName;
	}

	private HttpServletRequest request;
	private HttpServletResponse response;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
	}

	public List<FillSkills> getSkillList() {
		return skillList;
	}

	public void setSkillList(List<FillSkills> skillList) {
		this.skillList = skillList;
	}

	public String getClient() {
		return client;
	}

	public void setClient(String client) {
		this.client = client;
	}

	public String getPrjectCode() {
		return prjectCode;
	}

	public void setPrjectCode(String prjectCode) {
		this.prjectCode = prjectCode;
	}

	public String getClientPoc() {
		return clientPoc;
	}

	public void setClientPoc(String clientPoc) {
		this.clientPoc = clientPoc;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public List<FillEmployee> getProjectOwnerList() {
		return projectOwnerList;
	}

	public void setProjectOwnerList(List<FillEmployee> projectOwnerList) {
		this.projectOwnerList = projectOwnerList;
	}

	public String getStrProjectOwner() {
		return strProjectOwner;
	}
    
	public void setStrProjectOwner(String strProjectOwner) {
		this.strProjectOwner = strProjectOwner;
	}
}