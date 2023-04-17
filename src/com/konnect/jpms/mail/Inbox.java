package com.konnect.jpms.mail;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.Listbox;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;

public class Inbox implements IStatements, ServletRequestAware {
	List<Mail> mails;
	ResultSet rs;

	// String EmpId=(String)session.getAttribute("EMPID");
	String myName = "";
	String myEmpId;
	static int flag = 0;
	WorkingThread Wt;
	String trashed = "";
	CommonFunctions CF=null;
	private static Logger log = Logger.getLogger(Inbox.class);
	
	 
	public void assign(String myName, String myEmpId) {
		this.myName = myName.trim();
		this.myEmpId = myEmpId;
	}

	public List<Mail> getAllTrash(List<Mail> mails) throws SQLException {

		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		this.mails = mails;

		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement(getAllTrashQuery);
			pst.setString(1, myName);
			pst.setString(2, myName);
			pst.setString(3, "0");
			rs = pst.executeQuery();

			mails.clear();
			while (rs.next()) {

				Mail mail = new Mail(rs.getString("mail_id"), rs.getString("mail_from"), rs.getString("mail_subject"), rs.getTimestamp("timestamp"), rs.getString("mail_body"), rs.getString("mail_drafts"), rs.getString("mail_trash"), "false");

				mails.add(mail);
			}
			rs.close();
			pst.close();

		} catch (SQLException e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

		return mails;
	}

	public List<Mail> getAllSent(List<Mail> mails) throws SQLException {

		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		con = db.makeConnection(con);

		String tempMailNo = "";
		this.mails = mails;

		try {
			pst = con.prepareStatement(getAllSentQuery);
			pst.setString(1, myName);
			pst.setString(2, "0");
			pst.setString(3, "0");
			rs = pst.executeQuery();
			mails.clear();

			while (rs.next()) {

				if (tempMailNo.equals(rs.getString("mail_no")))
					continue;

				Mail mail = new Mail(rs.getString("mail_id"), rs.getString("mail_from"), rs.getString("mail_to"), rs.getString("mail_subject"), rs.getTimestamp("timestamp"), rs.getString("mail_body"), rs.getString("mail_drafts"));
				mails.add(mail);
			}
			rs.close();
			pst.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return mails;
	}

	public void moveTrash(String mail_id) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		con = db.makeConnection(con);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			pst = con.prepareStatement(moveToTrash);
			pst.setString(1, myName);
			pst.setInt(2, uF.parseToInt(mail_id));
			pst.executeUpdate();
			pst.close();
			trashed = mail_id;

		} catch (SQLException e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	public List<Mail> getAllDrafts(List<Mail> mails) throws SQLException {

		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		con = db.makeConnection(con);

		this.mails = mails;

		try {
			pst = con.prepareStatement(getAllDraftsQuery);
			pst.setString(1, myName);
			pst.setString(2, myName);
			rs = pst.executeQuery();
			mails.clear();
			while (rs.next()) {

				Mail mail = new Mail(rs.getString("mail_id"), rs.getString("mail_from"), rs.getString("mail_to"), rs.getString("mail_subject"), rs.getTimestamp("timestamp"), rs.getString("mail_body"), rs.getString("mail_drafts"), rs.getString("mail_trash"));
				mails.add(mail);
			}
			rs.close();
			pst.close();

		} catch (SQLException e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return mails;
	}

	public void checkUpdates(List<Mail> mails, Listbox thelistbox, ListModel model, String myName) {
		Wt = new WorkingThread(mails, thelistbox, model, myName, request);
//		Wt.start();
	}

	public List<Mail> getAllMails(List<Mail> mails) throws SQLException {
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		con = db.makeConnection(con);

		String read = "";

		this.mails = mails;

		String tempMail_No = "";
		try {
			
			
			UtilityFunctions uF = new UtilityFunctions();
			
			
			pst = con.prepareStatement("Select * from mail where emp_id=?  and mail_trash=? and mail_drafts=? ORDER BY mail_id DESC");
			pst.setInt(1, uF.parseToInt(myEmpId));
			pst.setString(2, "0");
			pst.setString(3, "0");
			rs = pst.executeQuery();
			
			
			/*pst = con.prepareStatement(getAllMailsquery);
			pst.setString(1, myName);
			pst.setString(2, "0");
			pst.setString(3, "0");
			rs = pst.executeQuery();*/
			mails.clear();

			
//			System.out.println("pst====>"+pst);
			
			while (rs.next()) {

				if (tempMail_No.equals(rs.getString("mail_no"))) {
					continue;
				}
				String readStatus = rs.getString("read_unread");

				if (readStatus.equals("f")) {
					read = "font-weight: bold;";
				} else {
					read = "";
				}
				Mail mail = new Mail(rs.getString("mail_id"), rs.getString("mail_from"), rs.getString("mail_subject"), rs.getTimestamp("timestamp"), rs.getString("mail_body"), rs.getString("mail_drafts"), rs.getString("mail_trash"), read);
				mails.add(mail);
			}
			rs.close();
			pst.close();
		} catch (SQLException e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return mails;
	}

	public void markRead(String mailID) {
		String mail_no = "";
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		con = db.makeConnection(con);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			pst = con.prepareStatement(getMailNo);
			pst.setInt(1, uF.parseToInt(mailID));
			rs = pst.executeQuery();
			rs.next();
			mail_no = rs.getString("mail_no");
			

			rs.close();
			pst.close();

//			pst = con.prepareStatement(markReadQuery);
//			pst.setBoolean(1, true);
//			pst.setInt(2, uF.parseToInt(mail_no));
//			pst.setString(3, myName);
//			pst.executeUpdate();
			
			
			pst = con.prepareStatement("update mail set read_unread=? where mail_no=? and emp_id=? and mail_drafts='0' and mail_trash='0'");
			pst.setBoolean(1, true);
			pst.setInt(2, uF.parseToInt(mail_no));
			pst.setInt(3, uF.parseToInt(myEmpId));
			pst.executeUpdate();
			pst.close();

		} catch (SQLException e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	public String getTrashed() {
		return trashed;
	}

	public List<Mail> getAllContacts(List<Mail> mails) {
		UtilityFunctions uF=new UtilityFunctions();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		con = db.makeConnection(con);
	
		Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(request);
		boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
		
		try {
			pst = con.prepareStatement(getAllContactsQuery);
			rs = pst.executeQuery();

			this.mails = mails;
			mails.clear();

			while (true) {
				if (!rs.next())
					break;
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				
				String receiver = rs.getString("emp_fname")+strEmpMName+" " + rs.getString("emp_lname") + "(" + rs.getString("empcode") + ")";
				Mail mail = new Mail(receiver);
				mails.add(mail);
			}
			rs.close();
			pst.close();

			HashSet tempHs = new HashSet();
			tempHs.addAll(mails);
			mails.clear();
			mails.addAll(tempHs);

		} catch (SQLException e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		Listbox thelistbox;
		// Listitem item=thelistbox.getSelectedItem().isSelected()
		return mails;
	}

	public String[] getToandCC(String mailID) {
		String arr[] = new String[2];
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		con = db.makeConnection(con);

		String tempMail_no = "";
		String to = "";
		String cc = "";
		UtilityFunctions uF = new UtilityFunctions();

		try {
			pst = con.prepareStatement(getMailId);
			pst.setInt(1, uF.parseToInt(mailID));
			rs = pst.executeQuery();

			rs.next();
			tempMail_no = rs.getString("mail_no");

			rs.close();
			pst.close();
			
			pst = con.prepareStatement(getToAndCCQuery);
			pst.setInt(1, uF.parseToInt(tempMail_no));
			rs = pst.executeQuery();
			while (rs.next()) {

				if (rs.getString("mail_type").equals("to")) {
					to = to + rs.getString("mail_to") + ",";
				} else {
					cc = cc + rs.getString("mail_to") + ",";
				}
			}
			rs.close();
			pst.close();

		} catch (SQLException e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		} catch (NullPointerException e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		arr[0] = to;
		arr[1] = cc;

		return arr;
	}

	public void deleteMail(String mailId) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		con = db.makeConnection(con);
		try {
			pst = con.prepareStatement(deleteMailForever);
			pst.setInt(1, Integer.parseInt(mailId));
			pst.executeUpdate();
			pst.close();
		} catch (SQLException e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		} catch (NullPointerException e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}
	
	HttpSession session;
	//HttpServletRequest request;

}

class WorkingThread extends Thread implements IStatements {
	Desktop desktop;
	List<Mail> mails;

//	ResultSet rs = null; 
//	PreparedStatement pst = null;
//	Database db = new Database();
//	Connection con = null;

	Listbox thelistbox;
	ListModel model;
	Statement stmt, stmt2;
	String query;
	String myName = "";
	String tempMailNo = "";
	HttpSession session;
	HttpServletRequest request;

	private static Logger log = Logger.getLogger(WorkingThread.class);
	
	public WorkingThread(List<Mail> mails, Listbox thelistbox, ListModel model, String myName, HttpServletRequest request) {

		desktop = Executions.getCurrent().getDesktop();
		desktop.enableServerPush(true);

		this.mails = mails;
		this.thelistbox = thelistbox;
		this.model = model;
		this.myName = myName;
		this.request = request;

//		try {

//			con = db.makeConnection(con);
//			pst = con.prepareStatement(checkUpdateInboxMails, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
//			pst.setString(1, myName);
//			pst.setString(2, "0");
//			pst.setString(3, "0");
//			rs = pst.executeQuery();
//			rs.next();
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
		
	}

	public void run() {
		while (true) {
			
			
			ResultSet rs = null; 
			PreparedStatement pst = null;
			Database db = new Database();
			db.setRequest(request);
			Connection con = null;
			
			try {
				
				
				con = db.makeConnection(con);
				pst = con.prepareStatement(checkUpdateInboxMails, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
				pst.setString(1, myName);
				pst.setString(2, "0");
				pst.setString(3, "0");
				rs = pst.executeQuery();
				rs.next();
				
				
				
				if (rs.getRow() > 0) {
					rs.first();
					String oldSid = rs.getString("mail_id");
					rs = pst.executeQuery();
					rs.next();

					String newSid = rs.getString("mail_id");

					if (Integer.parseInt(oldSid) < Integer.parseInt(newSid)) {
						while (true) {
							if (oldSid.equals(rs.getString("mail_id"))) {
								break;
							}

							Executions.activate(desktop);
							String sender = rs.getString("mail_from");
							String subject = rs.getString("mail_subject");
							String sid = rs.getString("mail_id");
							String body = rs.getString("mail_body");
							String drafts = rs.getString("mail_drafts");
							Timestamp newTime1 = rs.getTimestamp("timestamp");
							String tempTrash = rs.getString("mail_trash");
							if (tempMailNo.equals(rs.getString("mail_no"))) {
								continue;
							}
							tempMailNo = rs.getString("mail_no");
							Mail mail = new Mail(sid, sender, subject, newTime1, body, drafts, tempTrash, "font-weight: bold;");
							mails.add(0, mail);

							ListModel model = thelistbox.getModel();
							thelistbox.setModel(model);
							Executions.deactivate(desktop);
							rs.next();
							
						}
					}
				} else {
					tempMailNo = "";
					try {
						rs = pst.executeQuery();
						
						while (rs.next()) { 

								Executions.activate(desktop);
								String sender = rs.getString("mail_from");
								String subject = rs.getString("mail_subject");
								String sid = rs.getString("mail_id");
								Timestamp newTime1 = rs.getTimestamp("timestamp");
								String body = rs.getString("mail_body");
								String drafts = rs.getString("mail_drafts");
								String trash = rs.getString("mail_trash");

								if (tempMailNo.equals(rs.getString("mail_no"))) {
									continue;
								}
								tempMailNo = rs.getString("mail_no");
								Mail mail = new Mail(sid, sender, subject, newTime1, body, drafts, trash, "font-weight: bold;");
								mails.add(0, mail);

								ListModel model = thelistbox.getModel();
								thelistbox.setModel(model);
								Executions.deactivate(desktop);
								
							
						}

					} catch (SQLException e) {
						e.printStackTrace();
						log.error(e.getClass() + ": " +  e.getMessage(), e);
					}
				}
				rs.close();
				pst.close();
			} catch (InterruptedException e) {
				e.printStackTrace();
				log.error(e.getClass() + ": " +  e.getMessage(), e);
			} catch (SQLException e) {
				e.printStackTrace();
				log.error(e.getClass() + ": " +  e.getMessage(), e);
			}finally{
				db.closeResultSet(rs);
				db.closeStatements(pst);
				db.closeConnection(con);
			}
			
			
			
			try {
				Thread.sleep(1000);
			} catch (Exception e) {
				e.printStackTrace();
				log.error(e.getClass() + ": " +  e.getMessage(), e);
			}
		}

	}

	public void requestStop() {
		log.debug("Stopped!");
	}
}