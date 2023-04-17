package com.konnect.jpms.reports;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillFAQSection;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class EditAndDeleteFaq extends ActionSupport implements ServletRequestAware, IStatements {

	HttpSession session;
	CommonFunctions CF = null;
	boolean Flag = false;
	String strSessionEmpId;
	String strSessionOrgId;
	String strUserType;
	private String operation;
	private String faqId;
	public String strQuestion;
	public String strAnswer;
	public String strSection;
	public String strfaqSection;
	 int sectionId;
	private HttpServletRequest request;
	private List<FillFAQSection> faqSectionList;
	public String sectionName;
	Map<String, String> hmSectionName = new HashMap<String, String>();

	

public String execute() throws Exception {
		session = request.getSession(true);
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		strSessionEmpId = (String)session.getAttribute(EMPID);
		strSessionOrgId = (String)session.getAttribute(ORGID);
		strUserType = (String)session.getAttribute(USERTYPE);
		faqSectionList = new FillFAQSection(request).fillSection();
		UtilityFunctions uF = new UtilityFunctions();
		if(getOperation() != null && getOperation().equals("A") && getStrQuestion()!=null && !getStrQuestion().trim().equals("")) {
			addnewQA();
			return SUCCESS;
		} else if(getOperation() != null && getOperation().equals("E")) {
			getFaqData();
			setOperation("U");
		} else if (getOperation() != null && getOperation().equals("U")) {
			updateFaq();
			return SUCCESS;
				
		} else if (operation != null && operation.equals("D")) {
			deleteFaq();
			return SUCCESS;
		}
		return LOAD;
	}


	public Boolean addnewQA() {
		int sectionId1 = 0;
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
	
		//documentList = new FillDocument(request).fillDocumentList(null, getF_org());
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select distinct section_id,section_name from faq_details");
			rs = pst.executeQuery();
			while(rs.next()) {
				hmSectionName.put(rs.getString("section_id"), rs.getString("section_name"));
			}
			rs.close();
			pst.close();
			 if(uF.parseToInt(getStrfaqSection()) == 0) {
				pst = con.prepareStatement("select max(section_id) section_id from faq_details");
				rs = pst.executeQuery();
				while(rs.next()) {
					sectionId1 = rs.getInt("section_id");
					sectionId1++;
				}
				rs.close();
				pst.close();
				hmSectionName.put(sectionId1+"", getStrSection());
				setStrfaqSection(sectionId1+"");
			 }
			pst= con.prepareStatement("insert into faq_details(faq_question,faq_answer,added_by,entry_date,org_id,section_id,section_name)values(?,?,?,?,?,?,?)");
			pst.setString(1, getStrQuestion());
			pst.setString(2, getStrAnswer());
			pst.setInt(3, uF.parseToInt(strSessionEmpId));
			pst.setTimestamp(4, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone()) +"" + uF.getCurrentTime(CF.getStrTimeZone()), DBDATE+DBTIME));
			pst.setString(5, strSessionOrgId);
			pst.setInt(6, uF.parseToInt(getStrfaqSection()));
			pst.setString(7, hmSectionName.get(getStrfaqSection()));
		//	System.out.print("insert pst with id ==>"+pst);
			pst.execute();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return Flag;
	}

	
	public void getFaqData() { 
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
//	System.out.println("get notice data");
	try {
		con = db.makeConnection(con);
		
		pst = con.prepareStatement("select * from faq_details where faq_id = ?");
		pst.setInt(1, uF.parseToInt(getFaqId()));
		rs = pst.executeQuery();
		if(rs.next()) {
				setStrQuestion(rs.getString("faq_question"));
				setStrAnswer(rs.getString("faq_answer"));
				setStrfaqSection(rs.getString("section_id"));
			}
		rs.close();
     	pst.close();
     	request.setAttribute("faq_id", getFaqId());
    	request.setAttribute("faq_question", getStrQuestion());
    	request.setAttribute("faq_answer", getStrAnswer());
    	
	   }catch (Exception e)
	   	{
			e.printStackTrace();
		} finally 
		{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	public void updateFaq() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		//	System.out.println("get notice data");
		int empid = Integer.parseInt(strSessionEmpId);
		try {
			con = db.makeConnection(con);
			//pst = con.prepareStatement("select * from notices where faq_id = ?");
			pst = con.prepareStatement("select distinct section_id,section_name from faq_details");
			rs = pst.executeQuery();
			while(rs.next()) {
				hmSectionName.put(rs.getString("section_id"), rs.getString("section_name"));
			}
			rs.close();
			pst.close();
			
			
			
			if(getOperation() != null && getOperation().equals("U"))  {
		 	pst = con.prepareStatement("update faq_details set faq_question = ?, faq_answer = ?, updated_by = ?, update_date = ?,section_id =?,section_name=?  where faq_id=?");
		 	
			pst.setString(1, getStrQuestion());
			pst.setString(2, getStrAnswer());
			pst.setInt(3, empid);
			pst.setTimestamp(4,  uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone()) +"" + uF.getCurrentTime(CF.getStrTimeZone()), DBDATE+DBTIME));
			pst.setInt(5, uF.parseToInt(getStrfaqSection()));
			pst.setString(6, hmSectionName.get(getStrfaqSection()));
			pst.setInt(7,uF.parseToInt(getFaqId()));
			pst.executeUpdate();
			pst.close();
			//rs.close();
		}
			
			pst = con.prepareStatement("select * from faq_details where faq_id = ?");
			pst.setInt(1, uF.parseToInt(getFaqId()));
			rs = pst.executeQuery();
			List<String> alInner = new ArrayList<String>();
			while(rs.next()){
				alInner.add(Integer.toString(rs.getInt("faq_id")));//0
				alInner.add(rs.getString("faq_question"));//5
				alInner.add(rs.getString("faq_answer"));//6
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
	}
	
	
	
	public void deleteFaq() {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
	
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("DELETE FROM faq_details WHERE faq_id=?");
			pst.setInt(1,uF.parseToInt(getFaqId()));
			pst.execute();
			//System.out.print("in delete pst===>"+pst);
			pst.close();
			request.setAttribute(MESSAGE, "faq deleted successfully!");
			
			
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute(MESSAGE, "Error in deletion");
		}finally{
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}
	
	
	
public String getOperation() {
	return operation;
}

public void setOperation(String operation) {
	this.operation = operation;
}

public String getFaqId() {
	return faqId;
}
public void setFaqId(String faqId) {
	this.faqId = faqId;
}
public String getStrQuestion() {
	return strQuestion;
}

public void setStrQuestion(String strQuestion) {
	this.strQuestion = strQuestion;
}

public String getStrAnswer() {
	return strAnswer;
}

public void setStrAnswer(String strAnswer) {
	this.strAnswer = strAnswer;
}
public List<FillFAQSection> getFaqSectionList() {
	return faqSectionList;
}


public void setFaqSectionList(List<FillFAQSection> faqSectionList) {
	this.faqSectionList = faqSectionList;
}


public String getStrfaqSection() {
	return strfaqSection;
}

public void setStrfaqSection(String strfaqSection) {
	this.strfaqSection = strfaqSection;
}

public String getSectionName() {
	return sectionName;
}

public void setSectionName(String sectionName) {
	this.sectionName = sectionName;
}

public String getStrSection() {
	return strSection;
}


public void setStrSection(String strSection) {
	this.strSection = strSection;
}


@Override
public void setServletRequest(HttpServletRequest request) {
	this.request = request;
}
		
}