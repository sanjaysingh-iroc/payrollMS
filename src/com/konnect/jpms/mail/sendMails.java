package com.konnect.jpms.mail;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.zkoss.zul.Bandbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.Notifications;
import com.konnect.jpms.util.UtilityFunctions;

public class sendMails implements IStatements, ServletRequestAware{
	Textbox subject,bodyText;
	Bandbox to,cc;
	Label from;
	ResultSet rs,rsAssignMailNo;
	public static int mailNumber=1;

	public sendMails(){
		
		PreparedStatement pst = null;
		ResultSet rsAssignMailNo = null;
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		UtilityFunctions uF = new UtilityFunctions();
		
		try
		{
			
			con = db.makeConnection(con);
			pst =con.prepareStatement(selectMaxMailNo);
			rsAssignMailNo=pst.executeQuery();
			rsAssignMailNo.next();
			mailNumber=uF.parseToInt((rsAssignMailNo.getString(1)))+1;
			rsAssignMailNo.close();
			pst.close();
			
		} catch(SQLException e){
			e.printStackTrace();
		}finally{
			
			db.closeStatements(pst);
			db.closeResultSet(rsAssignMailNo);
			db.closeConnection(con);
		}
		
	}

	public void assign(Label from,Bandbox to, Bandbox cc, Textbox subject, Textbox bodyText)
	{
		this.from=from;
		this.to=to;
		this.cc=cc;
		this.subject=subject;
		this.bodyText=bodyText;
	}
	
	
	public boolean validate(Label toValidate, Label ccValidate,Label subjectValidate, Label bodyValidate)
	{
		String addresses[]=to.getValue().split(",");
		String ccAddresses[]=cc.getValue().split(",");
		
		boolean validate=true;
		toValidate.setVisible(false);
		ccValidate.setVisible(false);
		subjectValidate.setVisible(false);
		bodyValidate.setVisible(false);
		
		
			
	//====================to field============================================	
		if(to.getValue().trim().equals(""))
		{
			toValidate.setVisible(true);
			validate=false;
		}
		
//======================================================================
		

//====================subject============================================
		if(subject.getValue().trim()=="")
		{
			subjectValidate.setVisible(true);
			validate=false;
		}
				
		if(bodyText.getValue().trim()=="" || bodyText==null)
		{
			
			bodyValidate.setVisible(true);
			validate=false;
		}
//================================================================
		return validate;
}
	
	
public void send()
{
	PreparedStatement pst = null;
	ResultSet rs = null;
	Database db = new Database();
	db.setRequest(request);
	Connection con = null;
	Map<String, String> hmEmpCodes = new CommonFunctions(request).getEmpCodeMap1(con);
	UtilityFunctions uF = new UtilityFunctions();
	
		
		try
		{
			String rec=to.getValue();
			String rec2=cc.getValue();
			String[] addresses=rec.split(",");
			String[] ccAddresses=rec2.split(",");
			con = db.makeConnection(con);			
			for(int i=0;i<addresses.length;i++)
			{
				
				
				String str = addresses[i].trim();
				int nIndex1 = str.lastIndexOf("(")+1;
				int nIndex2 = str.lastIndexOf(")");
				if(str!=null){
					str = str.substring(nIndex1, nIndex2);
				}
				
								
				pst = con.prepareStatement(insertMail);
				pst.setInt(1, mailNumber);
				pst.setString(2, from.getValue());
				pst.setString(3, addresses[i].trim());
				pst.setString(4, subject.getValue());
				pst.setString(5, "upload");
				pst.setString(6, bodyText.getValue());
				pst.setString(7, "0");
				pst.setString(8, "0");
				pst.setString(9, "to");
				pst.setBoolean(10, false);
				pst.setInt(11, uF.parseToInt(hmEmpCodes.get(str)));
				
				pst.execute();
				pst.close();
				
				
				
				String strDomain = request.getServerName().split("\\.")[0];
				Notifications nF = new Notifications(N_NEW_MAIL); 
				nF.setDomain(strDomain); 
				nF.request = request;
				nF.setStrEmpId(hmEmpCodes.get(str));
				nF.setStrNewEmailFrom(from.getValue());
				nF.setStrNewEmailSubject(subject.getValue());
				nF.setStrNewEmailBody(bodyText.getValue());
				nF.setEmailTemplate(true);
				nF.sendNotifications();
				
				
			}
			
			
			for(int i=0;i<ccAddresses.length;i++)
			{
				if(ccAddresses[i].trim().equals("")==false)
				{
			
					String str = ccAddresses[i].trim();
					int nIndex1 = str.lastIndexOf("(")+1;
					int nIndex2 = str.lastIndexOf(")");
					if(str!=null){
						str = str.substring(nIndex1, nIndex2);
					}
					
					pst = con.prepareStatement(insertMail);
					pst.setInt(1, mailNumber);
					pst.setString(2, from.getValue());
					pst.setString(3, ccAddresses[i].trim());
					pst.setString(4, subject.getValue());
					pst.setString(5, "upload");
					pst.setString(6, bodyText.getValue());
					pst.setString(7, "0");
					pst.setString(8, "0");
					pst.setString(9, "cc");
					pst.setBoolean(10, false);
					pst.setInt(11, uF.parseToInt(hmEmpCodes.get(str)));
					
					if(ccAddresses.length!=0){
						pst.execute();
						pst.close();
						
						
						String strDomain = request.getServerName().split("\\.")[0];
						Notifications nF = new Notifications(N_NEW_MAIL);
						nF.setDomain(strDomain);
						nF.request = request;
						nF.setStrEmpId(hmEmpCodes.get(str));
						nF.setStrNewEmailFrom(from.getValue());
						nF.setStrNewEmailSubject(subject.getValue());
						nF.setStrNewEmailBody(bodyText.getValue());
						nF.setEmailTemplate(true);
						nF.sendNotifications();
					}
						
				}
			}
				
		}
		catch(SQLException e){
			e.printStackTrace();
		}finally{
			mailNumber++;
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
		
	}
	
	public void moveDrafts(String sender1,String receiver, String subject,String body)
	{
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		
		try
		{
			
			con = db.makeConnection(con);
			
			pst = con.prepareStatement(insertDraftMail);
			pst.setInt(1, 0);
			pst.setString(2, sender1);
			pst.setString(3, receiver);
			pst.setString(4, subject);
			pst.setString(5, "upload");
			pst.setString(6, body);
			pst.setString(7, sender1);
			pst.setString(8, "0");
			pst.execute();
			pst.close();
			
			
			
		}
		catch(SQLException e){
			e.printStackTrace();
		}finally{
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
}


