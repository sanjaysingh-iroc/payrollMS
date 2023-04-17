package com.konnect.jpms.common;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FilenameUtils;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.Notifications;
import com.konnect.jpms.util.UserActivities;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class AddNotice extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	CommonFunctions CF;
	String operation;
	
	String strUserType;
	String strSessionEmpId;
	
	String displayStartDate;
	String noticeId;
	String heading;
	String content;
	
	String displayEndDate;
	String publish;	
	
	String noticePost;
	String ispublish;
	
	

	String heading1;
	String content1;
	String displayStartDate1;
	String displayEndDate1;
	String ispublish1;	
	public String execute() throws Exception {
		
		session = request.getSession(true);
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		strUserType = (String)session.getAttribute(USERTYPE);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		request.setAttribute(PAGE, "/jsp/reports/NoticeReport.jsp");
		request.setAttribute(TITLE, "Announcements");
		
		//System.out.println("noticeId==>"+noticeId);
		//setNoticeId(request.getParameter("noticeId"));
		if(getOperation() != null && getOperation().equals("E")) {
			getNoticeData();
		}else if (operation != null && operation.equals("U")) {
			return updateNotice();
		} else if (operation != null && operation.equals("D")) {
			return deleteNotice();
		}
		
		return LOAD;
		
	}
	  
	public String loadValidateNotice() {
		request.setAttribute(PAGE, PAddNotice);
		request.setAttribute(TITLE, TAddNotice);
		return LOAD;
	}
	
	public void getNoticeData(){
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			Map<String, String> hmResourceImage = CF.getEmpProfileImage(con);
			Map<String, String> hmCustImage = CF.getCustomerImageMap(con);
			
			pst = con.prepareStatement("select * from events where event_id = ?");
			pst.setInt(1, uF.parseToInt(getNoticeId()));
			rs = pst.executeQuery();
			while(rs.next()) {
				
				setContent(rs.getString("content"));
				setHeading(rs.getString("heading"));
			    setDisplayStartDate(uF.getDateFormat(rs.getString("display_date"),DBDATE,DATE_FORMAT));
			    setDisplayEndDate(uF.getDateFormat(rs.getString("display_end_date"),DBDATE,DATE_FORMAT));
				setIspublish(rs.getString("ispublish"));
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
	public String insertNotice() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		System.out.println("in save notice");
		try {

			con = db.makeConnection(con);
//			pst = con.prepareStatement(insertNotice);
			pst = con.prepareStatement("INSERT INTO notices (heading, content, _date, display_date,display_end_date,ispublish,added_by,posted_date) VALUES (?,?,?,?,?,?,?,?)");
			pst.setString(1, getHeading());
			pst.setString(2, getContent());
			pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setDate(4, uF.getDateFormat(getDisplayStartDate(), DATE_FORMAT));
			pst.setDate(5, uF.getDateFormat(getDisplayEndDate(), DATE_FORMAT));
			if(getNoticePost()!=null && !getNoticePost().equals("") && getIspublish()!=null){
				if(getIspublish().equals("1")){
					pst.setBoolean(6, true); //Publish
				}else{
					pst.setBoolean(6, false); //Unpublish
				}
			}else{
				if(getPublish()!=null){
					pst.setBoolean(6, true); //Publish
				}else{
					pst.setBoolean(6, false); //Unpublish
				}
			}
			pst.setInt(7,uF.parseToInt((String)session.getAttribute(EMPID)));
			pst.setTimestamp(8, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone())+"", DBDATE+DBTIME));
			pst.execute();
			pst.close();
			request.setAttribute(MESSAGE, "Notice added successfully!");
			System.out.println("after save notice");

			/*if(getPublish() != null) {
				String strDomain = request.getServerName().split("\\.")[0];
				UserActivities userAct = new UserActivities(con, uF, CF, request);
				userAct.setStrDomain(strDomain);
				userAct.setStrVisibility("0");
				userAct.setStrData(getHeading());
				userAct.setStrOther("other");
				if(strUserType.equals(CUSTOMER)) {
					userAct.setStrUserType("C");
				}
				userAct.setStatus(INSERT_TR_ACTIVITY);
				Thread tt = new Thread(userAct);
				tt.run();
			}*/
//			String strDomain = request.getServerName().split("\\.")[0];
//			Notifications nF = new Notifications(N_NEW_NOTICE, CF);
//			nF.setDomain(strDomain);
//			nF.request = request;
//			nF.setStrEmpId(EVERYONE);
//			nF.setStrContextPath(request.getContextPath());
////			nF.setStrHostAddress(request.getRemoteHost());
//			nF.setStrHostAddress(CF.getStrEmailLocalHost());
//			nF.setStrHostPort(CF.getStrHostPort());
//			nF.setStrAnnouncementHeading(getHeading());
//			nF.setStrAnnouncementBody(getContent());
//			nF.setEmailTemplate(true);
//			nF.sendNotifications();   
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
			return ERROR;
		}finally{
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}

		return VIEW;

	}

	public String updateNotice() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		try {
			con = db.makeConnection(con);
			
			if(getOperation() != null && getOperation().equals("U")) {
				
				pst = con.prepareStatement("update notices set heading=?, content=?, _date=?, display_date=?,display_end_date=?,ispublish=?,added_by=?,posted_date=? where noticeId=?");
				System.out.println("heading==>"+getHeading1());
				pst.setString(1,getHeading1());
				pst.setString(2,getContent1());
				pst.setDate(3,uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setDate(4,uF.getDateFormat(getDisplayStartDate1(), DATE_FORMAT));
				pst.setDate(5,uF.getDateFormat(getDisplayEndDate1(), DATE_FORMAT));
				pst.setTimestamp(8, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone())+"", DBDATE+DBTIME));
				pst.setInt(7,uF.parseToInt((String)session.getAttribute(EMPID)));
				pst.setString(6,getIspublish1());
				
				
				pst.setInt(9,uF.parseToInt(getNoticeId()));
				pst.executeUpdate();
				pst.close();
			}
			
			pst = con.prepareStatement("select * from notices where noticeId = ?");
			pst.setInt(1, uF.parseToInt(getNoticeId()));
			rs = pst.executeQuery();
			
			List<String> alInner = new ArrayList<String>();
			Map<String, String> hmResourceName = CF.getEmpNameMap(con, null, null);
			
			while(rs.next()){
				alInner.add(Integer.toString(rs.getInt("notice_id")));//0
				alInner.add(uF.getDateFormat(rs.getString("display_date"), DBDATE, CF.getStrReportDateFormat()));//1
				alInner.add(uF.getDateFormat(rs.getString("display_end_date"), DBDATE, CF.getStrReportDateFormat()));//2
				alInner.add(uF.limitContent(rs.getString("heading"), 50));//3
				alInner.add(uF.limitContent(rs.getString("content"), 50));//4
				alInner.add(rs.getString("ispublish"));//5
				alInner.add(rs.getString("added_by"));//6
				alInner.add(uF.getDateFormat(rs.getString("posted_date"),DBTIMESTAMP, DATE_FORMAT_STR+" "+TIME_FORMAT_AM_PM));//7
				alInner.add(hmResourceName.get(rs.getString("added_by")));//8
			}
			rs.close();
			pst.close();
			request.setAttribute("alInner", alInner);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	/*	Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		int columnId = Integer.parseInt(request.getParameter("columnId"));
		String columnName=null;
		switch(columnId) {
			case 1 : columnName = "display_date"; break;
			case 2 : columnName = "display_end_date"; break;
			case 3 : columnName = "heading"; break;
			case 4 : columnName = "content"; break;
			case 6 : columnName = "ispublish"; break;
		}
		
		try {
			con = db.makeConnection(con);
			if(columnId==1 || columnId==2) {
				String updateNotice = "UPDATE notices SET "+columnName+"=? , _date = ? WHERE notice_id=?";
				pst = con.prepareStatement(updateNotice);
				pst.setDate(1, uF.getDateFormat(request.getParameter("value"), CF.getStrReportDateFormat() ));
				pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setInt(3, uF.parseToInt(request.getParameter("id")));
				pst.execute();
				pst.close();
			} else if(columnId==3 || columnId==4) {
				String updateNotice = "UPDATE notices SET "+columnName+"=? WHERE notice_id=?";
				pst = con.prepareStatement(updateNotice);
				pst.setString(1, request.getParameter("value"));
				pst.setInt(2, uF.parseToInt(request.getParameter("id")));
				pst.execute();
				pst.close();
			} else if(columnId==6) {
				String updateNotice = "UPDATE notices SET "+columnName+"=? WHERE notice_id=?";
				pst = con.prepareStatement(updateNotice);
				pst.setBoolean(1, uF.parseToBoolean(request.getParameter("value")));
				pst.setInt(2, uF.parseToInt(request.getParameter("id")));
				pst.execute();
				pst.close();
				
				if(uF.parseToBoolean(request.getParameter("value"))) {
					pst = con.prepareStatement("select heading from notices WHERE notice_id=?");
					pst.setInt(2, uF.parseToInt(request.getParameter("id")));
					rs = pst.executeQuery();
					String heading = null;
					while(rs.next()) {
						heading = rs.getString("heading");
					}
					pst.close();
					if(heading != null) {
						String strDomain = request.getServerName().split("\\.")[0];
						UserActivities userAct = new UserActivities(con, uF, CF, request);
						userAct.setStrDomain(strDomain);
						userAct.setStrVisibility("0");
						userAct.setStrData(getHeading());
						userAct.setStrSessionEmpId(strSessionEmpId);
						userAct.setStatus(INSERT_TR_ACTIVITY);
						Thread tt = new Thread(userAct);
						tt.run();
					}
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeStatements(pst);
			db.closeConnection(con);
		}*/
		return UPDATE;
	}
	
	
	/*public String viewNotice(String strEdit) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement(selectNoticeV);
			pst.setInt(1, uF.parseToInt(strEdit));
			rs = pst.executeQuery();
			while (rs.next()) {
				setNoticeId(rs.getString("notice_id"));
				setDisplayStartDate(uF.getDateFormat(rs.getString("display_date"), DBDATE, CF.getStrReportDateFormat()));
				setHeading(rs.getString("heading"));
				setContent(rs.getString("content"));
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
		
			return SUCCESS;

	}
	
	*/

	public String deleteNotice() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement(deleteNotice);
			pst.setInt(1, uF.parseToInt(request.getParameter("noticeId")));
			pst.execute();
			pst.close();
			request.setAttribute(MESSAGE, "Notice deleted successfully!");
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute(MESSAGE, "Error in deletion");
		}finally{
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}
	
	
	
	public void validate() {
		
        if (getDisplayStartDate()!=null && getDisplayStartDate().length() == 0) {
            addFieldError("displayStartDate", "Dispaly start date is required");
        }
        if (getDisplayEndDate()!=null && getDisplayEndDate().length() == 0) {
            addFieldError("displayEndDate", "Dispaly end date is required");
        }
        if (getHeading()!=null && getHeading().length() == 0) {
            addFieldError("heading", "Heading is required");
        }
        loadValidateNotice();
    }

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}


	public String getDisplayStartDate() {
		return displayStartDate;
	}

	public void setDisplayStartDate(String displayStartDate) {
		this.displayStartDate = displayStartDate;
	}

	public String getNoticeId() {
		return noticeId;
	}

	public void setNoticeId(String noticeId) {
		this.noticeId = noticeId;
	}

	public String getHeading() {
		return heading;
	}

	public void setHeading(String heading) {
		this.heading = heading;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getDisplayEndDate() {
		return displayEndDate;
	}

	public void setDisplayEndDate(String displayEndDate) {
		this.displayEndDate = displayEndDate;
	}

	public String getPublish() {
		return publish;
	}

	public void setPublish(String publish) {
		this.publish = publish;
	}

	public String getNoticePost() {
		return noticePost;
	}

	public void setNoticePost(String noticePost) {
		this.noticePost = noticePost;
	}

	public String getIspublish() {
		return ispublish;
	}

	public void setIspublish(String ispublish) {
		this.ispublish = ispublish;
	}

	

	public String getHeading1() {
		return heading1;
	}

	public void setHeading1(String heading1) {
		this.heading1 = heading1;
	}

	public String getContent1() {
		return content1;
	}

	public void setContent1(String content1) {
		this.content1 = content1;
	}

	public String getDisplayStartDate1() {
		return displayStartDate1;
	}

	public void setDisplayStartDate1(String displayStartDate1) {
		this.displayStartDate1 = displayStartDate1;
	}

	public String getDisplayEndDate1() {
		return displayEndDate1;
	}

	public void setDisplayEndDate1(String displayEndDate1) {
		this.displayEndDate1 = displayEndDate1;
	}

	public String getIspublish1() {
		return ispublish1;
	}

	public void setIspublish1(String ispublish1) {
		this.ispublish1 = ispublish1;
	}
	
}