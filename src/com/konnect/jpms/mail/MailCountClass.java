package com.konnect.jpms.mail;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.DesktopUnavailableException;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Label;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

public class MailCountClass extends ActionSupport implements IStatements, ServletRequestAware {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8384879893267521835L;
	String myName; 
	String strSessionEmpId;
	Label mailCountLabel;
	HttpSession session;
	HttpServletRequest request;
	private static Logger log = Logger.getLogger(MailCountClass.class);

	public String execute() {
		session = request.getSession(); 
		return SUCCESS;
	}

	public void MailCountClassDo(Label mailCountLabel) throws Exception {
		 HttpSession hSess = (HttpSession)
		 ((HttpServletRequest)Executions.getCurrent().getNativeRequest()).getSession();
//		HttpSession hSess = session;

		 log.debug("Mail Count session==>"+session);
//		 session = request.getSession();
//		 strSessionEmpId = (String)session.getAttribute(EMPID);
		 strSessionEmpId = (String)hSess.getAttribute(EMPID);
		 
		if (hSess != null) {

			String attributeTest = (String) hSess.getAttribute("MAILID");
			if (attributeTest != null) {
				this.myName = attributeTest;
				this.mailCountLabel = mailCountLabel;
				startThread();
			}
		}
	}

	public void getMailCount(HttpServletRequest request, String strSessionEmpId){
		ResultSet rs = null;
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		try {
			
			con = db.makeConnection(con);
			pst = con.prepareStatement(getUnreadMailCount);
			pst.setInt(1, uF.parseToInt(strSessionEmpId));
			rs = pst.executeQuery();
			while(rs.next()){
				if(rs.getInt(1)>0){
					request.setAttribute("MAIL_COUNT", rs.getString(1));
				}
			}
			rs.close();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		}finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	public void startThread() throws Exception {
		RunningThreadMailCount t = new RunningThreadMailCount(myName, mailCountLabel, strSessionEmpId, request);
//		t.start();
	}

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

}

class RunningThreadMailCount extends Thread implements IStatements {
	Desktop desktop;
	int nMailCount;
	String myName = "";
	
	String strSessionEmpId=null;
	Label mailCountLabel;
	HttpServletRequest request;
	
	Database db = new Database();

	
	
	UtilityFunctions uF = new UtilityFunctions();
	private static Logger log = Logger.getLogger(RunningThreadMailCount.class);

	public RunningThreadMailCount(String myName, Label mailCountLabel, String strSessionEmpId, HttpServletRequest request) {
		this.myName = myName;
		this.mailCountLabel = mailCountLabel;
		this.strSessionEmpId = strSessionEmpId;
		this.request = request;
//		this.uF = new UtilityFunctions();
		try {

			desktop = Executions.getCurrent().getDesktop();
			desktop.enableServerPush(true);

//			con = db.makeConnection(con);
//			pst = con.prepareStatement(getReadUnreadCount);
//			pst.setString(1, myName.trim());

			log.debug("my Name is in thread" + myName);

//		} catch (SQLException e) {
//			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		}
	}

	public void run() {
		try {
			int x= 0;
			while (x++<1) {

				if (desktop.isAlive() && desktop.isServerPushEnabled()) {
					
					Executions.activate(desktop);
					
					ResultSet rs = null;
					Connection con = null;
					PreparedStatement pst = null;
					
					try {
						db.setRequest(request);
						con = db.makeConnection(con);
//						pst = con.prepareStatement(getReadUnreadCount);
						pst = con.prepareStatement(getUnreadMailCount);
//						pst.setString(1, myName.trim());
						pst.setInt(1, uF.parseToInt(strSessionEmpId));
						rs = pst.executeQuery();
						rs.next();
						nMailCount = rs.getInt(1);
						rs.close();
						pst.close();
						
					} catch (Exception e) {
						e.printStackTrace();
						log.error(e.getClass() + ": " +  e.getMessage(), e);
					}finally{
						
						db.closeResultSet(rs);
						db.closeStatements(pst);
						db.closeConnection(con);
					}
					
					if (nMailCount == 0) {
						mailCountLabel.setValue("");
					} else {
						mailCountLabel.setValue(nMailCount+"");
						mailCountLabel.setStyle("background-image: url('images1/bubble.png');background-repeat: no-repeat;color: white;font-family: serif;font-size: 10px;font-weight: bold;line-height: 14px;position: absolute;text-align: center;width: 14px;top:0;");
					}
					Executions.deactivate(desktop);
					sleep(10000); 
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		} 
	}
	
	
	
	
}
