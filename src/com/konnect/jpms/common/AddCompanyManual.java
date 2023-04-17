package com.konnect.jpms.common;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.Notifications;
import com.konnect.jpms.util.UploadImage;
import com.konnect.jpms.util.UserAlerts;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class AddCompanyManual extends ActionSupport implements IStatements,ServletRequestAware, ServletResponseAware {
  
	private static final long serialVersionUID = 1L;

	HttpSession session;
	CommonFunctions CF;
	String strUserType;
	String strSessionEmpId;
	private String orgId;
	
	private String strManualId;
	private String strTitle;
	private String strBody;
	private String strPublish;
	private String strSaveDraft;
	private String strPriview;
	private String strId;
	
	private String pageFrom;
	private List<FillOrganisation> orgList;
	private String strOrg;
	private String strSubmit;
	private File strCompanyManual;
	private String strCompanyManualFileName;
	
	
	private String  userscreen;
	private String  navigationId;
	private String  toPage;
	private String  manualDoc;
	private String  manualDocStatus;
	public String execute() throws Exception {
		session = request.getSession(true);
		
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		String strEdit = (String)request.getParameter("E");
		String strDelete = (String)request.getParameter("D");
		String str_manualId = (String)request.getParameter("manualId");
		
		strSessionEmpId = (String)session.getAttribute(EMPID);
		strUserType = (String)session.getAttribute(USERTYPE);
		request.setAttribute(PAGE, PAddCompanyManual);
		request.setAttribute(TITLE, TCompanyManual);
			
		UtilityFunctions uF = new UtilityFunctions();
		orgList = new FillOrganisation(request).fillOrganisation();
		
		if(getStrOrg() == null || getStrOrg().equals("")) {
			setStrOrg((String)session.getAttribute(ORGID));
		}
		
		if(getPageFrom()!=null && getPageFrom().trim().equalsIgnoreCase("MyHub")) {
			
			if(getStrTitle()!=null) {
				
				addManual(strEdit,str_manualId);
				if(getStrPriview()!=null) {
					getManualId();
					return "priview";
				}
				return "myhubtab";
			
			} else if(strDelete!=null) {
				deleteManual(strDelete);
				return "myhubtab";
			} else if(str_manualId!=null) {
//				System.out.println("inside edit");
				viewManual(str_manualId);
				
				return "tab";
			} else {
				return "tab";
			}
			
		} else {
			if(uF.parseToInt(getStrManualId())>0) {
				//updateManual();
				addManual(strEdit,str_manualId);
				if(getStrPriview()!=null) {
					getManualId();
					return "priview";
				} else {
					return SUCCESS;
				}
			} else if(strEdit!=null) {
				viewManual(strEdit);
			} else if(strDelete!=null) {
				deleteManual(strDelete);
				return VIEW;
			} else if(getStrTitle()!=null) {
				addManual(strEdit,str_manualId);
				if(getStrPriview()!=null) {
					getManualId();
					return "priview";
				} else {
					return SUCCESS;
				}
			}
			
			return LOAD;
	  
			}
	
	}
	
		
	public void addManual(String strEdit, String str_manualId) {
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		
		try{
			
			con = db.makeConnection(con);
//			System.out.println("getStrBody==>"+getStrBody());
			if((getStrBody()!=null && !getStrBody().equals("")) || (getStrCompanyManual() != null && getStrCompanyManualFileName() != null)
					|| (uF.parseToInt(getManualDoc()) == 1)) {
			
				String manual_id = getStrManualId();
				if(strEdit != null && !strEdit.equals("")) {
					manual_id = strEdit;
				}
				
				if(str_manualId != null && !str_manualId.equals("")) {
					manual_id = str_manualId;
				}
				pst = con.prepareStatement("select * from company_manual where manual_id=?");
				pst.setInt(1, uF.parseToInt(manual_id));
				rs = pst.executeQuery();
				String manualDoc = null;
				String empId = "";
				String proId = null;
				while(rs.next()) {
					manualDoc = rs.getString("manual_doc");
					empId = rs.getString("emp_id");
				}
				rs.close();
				pst.close();
				
//				System.out.println("getManualDoc==>"+uF.parseToInt(getManualDoc()));
				if((uF.parseToInt(getManualDocStatus()) == 1 && getStrCompanyManual() != null) || (uF.parseToInt(getManualDocStatus()) == 0 && getStrBody() !=null && !getStrBody().equals(""))) {
					String strFilePath = null;
					if(CF.getStrDocSaveLocation()==null) {
							strFilePath = DOCUMENT_LOCATION +"/"+manual_id+"/"+manualDoc; //+"/"+ empId
					} else {
							strFilePath = CF.getStrDocSaveLocation()+I_COMPANY_MANUAL +"/"+manual_id+"/"+manualDoc; //+"/"+empId
					}
					File file = new File(strFilePath);
					file.delete();
					
				}
				
				pst = con.prepareStatement("delete from company_manual where manual_id = ?");
				pst.setInt(1, uF.parseToInt(manual_id));
//				System.out.println("pst====>"+pst);
				pst.execute();
				pst.close();
				
				if(getStrCompanyManual() != null) {
					pst = con.prepareStatement("insert into company_manual (manual_title,emp_id, _date, status, org_id) values (?,?,?,?,?)");
					pst.setString(1, getStrTitle());
					pst.setInt(2, uF.parseToInt((String)session.getAttribute(EMPID)));
					pst.setTimestamp(3, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone()),DBDATE+DBTIME) );
					
					if(getStrPublish()!=null) {
						pst.setInt(4, 1);
					} else if(getStrSaveDraft()!=null) {
						pst.setInt(4, 0);
					} else if(getStrPriview()!=null) {
						pst.setInt(4, -1);
					}
					pst.setInt(5, uF.parseToInt(getStrOrg()));
				
				} else {
					pst = con.prepareStatement("insert into company_manual (manual_title,emp_id, _date, status, org_id, manual_body) values (?,?,?,?,?,?)");
					pst.setString(1, getStrTitle());
					pst.setInt(2, uF.parseToInt((String)session.getAttribute(EMPID)));
					pst.setTimestamp(3, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone()),DBDATE+DBTIME) );
					
					if(getStrPublish()!=null) {
						pst.setInt(4, 1);
					} else if(getStrSaveDraft()!=null) {
						pst.setInt(4, 0);
					} else if(getStrPriview()!=null) {
						pst.setInt(4, -1);
					}
					pst.setInt(5, uF.parseToInt(getStrOrg()));
					pst.setString(6, getStrBody());
				}
//				System.out.println("insert pst==>"+pst);
				pst.execute();
				pst.close();
			
				String manualId = "";
				pst = con.prepareStatement("select max(manual_id) as manualId from company_manual");
				rs = pst.executeQuery();
				while(rs.next()) {
					manualId = rs.getString("manualId");
				}
				rs.close();
				pst.close();
				if(getStrCompanyManual() != null) {
					
					uploadManual(manualId);
				} else if(uF.parseToInt(getManualDocStatus()) == 1) {
					pst=con.prepareStatement("update company_manual set manual_doc =? where manual_id =? ");
					pst.setString(1, (manualDoc!=null && manualDoc.length()>0) ? manualDoc : null);
					pst.setInt(2, uF.parseToInt(manualId));
//					System.out.println("pst==>"+pst);
					pst.execute();
					pst.close();
				}
						
				if( manual_id == null || manual_id.equals("")) {
					session.setAttribute(MESSAGE,SUCCESSM +"<b>"+getStrTitle()+"</b> manual added Successfully."+ END );
				} else if(manual_id != null && !manual_id.equals("")){
					session.setAttribute(MESSAGE,SUCCESSM +"<b>"+getStrTitle()+"</b> manual updated Successfully."+ END );
				}
				
				Map<String,String> hmEmpMailDetails = new HashMap<String,String>();
				Map<String,String> hmEmpName = new HashMap<String,String>();
				
				if(getStrPublish()!=null) {
				//===start parvez date: 15-02-2023===	
					pst = con.prepareStatement("select emp_per_id, emp_fname, emp_lname, emp_email, emp_email_sec from employee_official_details eod, user_details ud, employee_personal_details epd " +
						" where epd.emp_per_id=eod.emp_id and ud.emp_id=eod.emp_id and is_alive = true and eod.org_id=?");
				//===end parvez date: 15-02-2023===	
					pst.setInt(1, uF.parseToInt(getStrOrg()));
					rs = pst.executeQuery();
					List<String> empList = new ArrayList<String>();
					while(rs.next()) {
						if(!empList.contains(rs.getString("emp_per_id").trim())) {
							empList.add(rs.getString("emp_per_id").trim());	
						}
						
						if(rs.getString("emp_email_sec")!=null && rs.getString("emp_email_sec").indexOf("@")>0) {
							hmEmpMailDetails.put(rs.getString("emp_per_id"), rs.getString("emp_email_sec"));
						} else if(rs.getString("emp_email")!=null && rs.getString("emp_email").indexOf("@")>0) {
							hmEmpMailDetails.put(rs.getString("emp_per_id"), rs.getString("emp_email"));
						}
						hmEmpName.put(rs.getString("emp_per_id"), rs.getString("emp_fname") + " " +rs.getString("emp_lname"));
					}
					rs.close();
					pst.close();
					
					Map<String, String> hmUserType = CF.getUserTypeMap(con);
					String strDomain = request.getServerName().split("\\.")[0];
					for(int i=0; empList!= null && !empList.isEmpty() && i<empList.size(); i++) {
						if(!empList.get(i).equals("") && uF.parseToInt(empList.get(i)) > 0) {
							String alertData = "<div style=\"float: left;\"> A new Manual is published by <b>"+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>. </div>";
							String alertAction = "Hub.action?pType=WR&type=M";
							
							UserAlerts userAlerts = new UserAlerts(con, uF, CF, request);
							userAlerts.setStrDomain(strDomain);
							userAlerts.setStrEmpId(empList.get(i));
							userAlerts.setStrData(alertData);
							userAlerts.setStrAction(alertAction);
							userAlerts.setCurrUserTypeID(hmUserType.get(EMPLOYEE));
							userAlerts.setStatus(INSERT_WR_ALERT);
							Thread t = new Thread(userAlerts);
							t.run();
							
//							UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
//							userAlerts.setStrDomain(strDomain);
//							userAlerts.setStrEmpId(empList.get(i));
//							userAlerts.set_type(NEW_MANUAL_ALERT);
//							userAlerts.setStatus(INSERT_ALERT);
//							Thread t = new Thread(userAlerts);
//							t.run();
					
						//===start parvez date: 15-02-2023===	
							boolean flg=false;
							
							Notifications nF = new Notifications(N_ORG_CIRCULAR_PUBLISH, CF);
							nF.setDomain(strDomain);
							nF.request = request;
							nF.setStrHostAddress(CF.getStrEmailLocalHost());
							nF.setStrHostPort(CF.getStrHostPort());
							nF.setStrContextPath(request.getContextPath());
							nF.setEmailTemplate(true);
							nF.setStrAddedBy(CF.getEmpNameMapByEmpId(con, strSessionEmpId));
							nF.setStrEmpId(empList.get(i)+"");
							nF.setStrPublishDate(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, DATE_FORMAT));
							nF.setStrEmpName(hmEmpName.get(empList.get(i)) );
							if(hmEmpMailDetails.get(empList.get(i)) != null && !hmEmpMailDetails.get(empList.get(i)).isEmpty() && !hmEmpMailDetails.get(empList.get(i)).equals("")){
								nF.setStrEmailTo(hmEmpMailDetails.get(empList.get(i)));
								flg=true;
							}
							if(flg) {
								nF.sendNotifications();
							}
						//===end parvez date: 15-02-2023===	
						}
					}
				}
				

			} else {
				if(uF.parseToInt(getManualDocStatus()) == 0  && (getStrBody() ==null || getStrBody().equals("") || getStrBody().equalsIgnoreCase("null"))) {
					session.setAttribute(MESSAGE, ERRORM+"Manual Description is mandatory, please try again...!"+END);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
		
	}
	
	private void uploadManual(String manualId) {
		
		try {
			
			UploadImage uI = new UploadImage();
			uI.setServletRequest(request);
			uI.setImageType("COMPANY_MANUAL");
			uI.setEmpImage(getStrCompanyManual());
			uI.setEmpImageFileName(getStrCompanyManualFileName());
			uI.setEmpId((String)session.getAttribute(EMPID));
			uI.setManualId(manualId);
			uI.setCF(CF);
			uI.upoadImage();
			
		}catch (Exception e) {
			e.printStackTrace();
			
		}
		
	}
	
	public void viewManual(String strEdit) {
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		try {
			
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from company_manual where manual_id = ?");
			pst.setInt(1, uF.parseToInt(strEdit));
			rs = pst.executeQuery();
			String strDoc = "";
			String manualBody = "";
			while(rs.next()) {
				setStrTitle(rs.getString("manual_title"));
				setStrBody(rs.getString("manual_body"));
				setStrManualId(rs.getString("manual_id"));
				setOrgId(rs.getString("org_id"));
				setStrOrg(rs.getString("org_id"));
				String empId = rs.getString("emp_id");
				strDoc = rs.getString("manual_doc");
				manualBody = rs.getString("manual_body");
				if(getManualDocStatus() == null) {
					if(strDoc != null && !strDoc.equalsIgnoreCase("")) {
						setManualDocStatus("1");
					} else {
						setManualDocStatus("0");
					}
				}
//				if(strDoc != null && !strDoc.equals("") && !strDoc.equalsIgnoreCase("null")) {
//					String strFilePath = null;
//					if(CF.getStrDocSaveLocation()==null) {
//							strFilePath = DOCUMENT_LOCATION +"/"+ empId+"/"+strEdit+"/"+strDoc;
//					} else {
//							strFilePath = CF.getStrDocRetriveLocation()+I_COMPANY_MANUAL+"/"+empId +"/"+strEdit+"/"+strDoc;
//					}
//					File file = new File(strFilePath);
//					setStrCompanyManual(file);
//					setStrCompanyManualFileName(rs.getString("manual_doc"));
//				}
				
			}
			rs.close();
			pst.close();
			
			request.setAttribute("strDoc", strDoc);
			request.setAttribute("manualBody", manualBody);
						
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}
	
	public void deleteManual(String strDelete) {
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		try{
			
			con = db.makeConnection(con);
			
			String empId = "";
			pst = con.prepareStatement("select * from company_manual where manual_id=?");
			pst.setInt(1, uF.parseToInt(strDelete));
			rs = pst.executeQuery();
			String manualDoc = null;
			String proId = null;
			while(rs.next()) {
				manualDoc = rs.getString("manual_doc");
				empId = rs.getString("emp_id");
			}
			rs.close();
			pst.close();
			
			String strFilePath = null;
			if(CF.getStrDocSaveLocation()==null) {
					strFilePath = DOCUMENT_LOCATION+"/"+strDelete+"/"+manualDoc; // +"/"+ empId
			} else {
					strFilePath = CF.getStrDocSaveLocation()+I_COMPANY_MANUAL+"/"+strDelete+"/"+manualDoc; //+"/"+empId 
			}
			File file = new File(strFilePath);
			file.delete();
			
			
			pst = con.prepareStatement("delete from company_manual where manual_id = ?");
			pst.setInt(1, uF.parseToInt(strDelete));
			pst.execute();
			pst.close();
			
			session.setAttribute(MESSAGE, SUCCESSM+"Company Manual deleted successfully."+END);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	public void getManualId() {
		
		Connection con = null;
		PreparedStatement pst = null;		
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		ResultSet rs = null;
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from company_manual where status = -1 order by manual_id desc limit 1");
			
			rs = pst.executeQuery();
			if(rs.next()) {
				setStrId(rs.getString("manual_id"));
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
		
	}
	
//	public void updateManual(String strEdit) {
//
//		Connection con = null;
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		Database db = new Database();
//		db.setRequest(request);
//		UtilityFunctions uF = new UtilityFunctions();
//		
//		try{
//			
//			con = db.makeConnection(con);
//			
//			if(getStrPriview()!=null) {
//				addManual();
//			} else {
//				if((getStrTitle()!=null && !getStrTitle().equals("")) && (getStrBody()!=null && !getStrBody().equals(""))) {
//					pst = con.prepareStatement("update company_manual set manual_title=?, manual_body=?, _date=?, status=?, org_id=? where manual_id =?");
//					pst.setString(1, getStrTitle());
//					pst.setString(2, getStrBody());
//					pst.setTimestamp(3, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone()),DBDATE+DBTIME) );
//					
//					if(getStrPublish()!=null) {
//						pst.setInt(4, 1);
//					} else if(getStrSaveDraft()!=null) {
//						pst.setInt(4, 0);
//					}
//					pst.setInt(5, uF.parseToInt(getStrOrg()));
//					pst.setInt(6, uF.parseToInt(strEdit));
//					pst.execute();
//					pst.close();
////					System.out.println("orgId==>"+orgId+"==strEdit==>"+strEdit);
//					if(getStrPublish()!=null) {
//						pst = con.prepareStatement("update company_manual set status=? where  org_id = ? and manual_id != ?  and status=?");
//						pst.setInt(1, 2);
//						pst.setInt(2, uF.parseToInt(getOrgId()));
//						pst.setInt(3, uF.parseToInt(getStrManualId()));
//						pst.setInt(4, 1);
//						pst.execute();
//						pst.close();
//					}
//					session.setAttribute(MESSAGE, SUCCESSM+"Company Manual Updated successfully."+END);
//				} else {
//					session.setAttribute(MESSAGE, SUCCESSM+"Enter Required Field Data...!"+END);
//				}
//			}
//			
//						
//		} catch (Exception e) {
//			e.printStackTrace();
//		}finally{
//			db.closeResultSet(rs);
//			db.closeStatements(pst);
//			db.closeConnection(con);
//		}
//		
//	}

	private HttpServletRequest request;
	private HttpServletResponse response;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}
	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response= response;
		
	}
	public String getStrTitle() {
		return strTitle;
	}
	public void setStrTitle(String strTitle) {
		this.strTitle = strTitle;
	}
	public String getStrBody() {
		return strBody;
	}
	public void setStrBody(String strBody) {
		this.strBody = strBody;
	}
	
	public String getStrManualId() {
		return strManualId;
	}

	public void setStrManualId(String strManualId) {
		this.strManualId = strManualId;
	}

	public String getStrPublish() {
		return strPublish;
	}

	public void setStrPublish(String strPublish) {
		this.strPublish = strPublish;
	}

	public String getStrSaveDraft() {
		return strSaveDraft;
	}

	public void setStrSaveDraft(String strSaveDraft) {
		this.strSaveDraft = strSaveDraft;
	}

	public String getStrPriview() {
		return strPriview;
	}

	public void setStrPriview(String strPriview) {
		this.strPriview = strPriview;
	}

	public String getStrId() {
		return strId;
	}

	public void setStrId(String strId) {
		this.strId = strId;
	}

	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

	public String getPageFrom() {
		return pageFrom;
	}

	public void setPageFrom(String pageFrom) {
		this.pageFrom = pageFrom;
	}

	public List<FillOrganisation> getOrgList() {
		return orgList;
	}

	public void setOrgList(List<FillOrganisation> orgList) {
		this.orgList = orgList;
	}

	public String getStrOrg() {
		return strOrg;
	}

	public void setStrOrg(String strOrg) {
		this.strOrg = strOrg;
	}

	public File getStrCompanyManual() {
		return strCompanyManual;
	}

	public void setStrCompanyManual(File strCompanyManual) {
		this.strCompanyManual = strCompanyManual;
	}

	public String getStrCompanyManualFileName() {
		return strCompanyManualFileName;
	}

	public void setStrCompanyManualFileName(String strCompanyManualFileName) {
		this.strCompanyManualFileName = strCompanyManualFileName;
	}

	public String getStrSubmit() {
		return strSubmit;
	}

	public void setStrSubmit(String strSubmit) {
		this.strSubmit = strSubmit;
	}


	public String getUserscreen() {
		return userscreen;
	}


	public void setUserscreen(String userscreen) {
		this.userscreen = userscreen;
	}


	public String getNavigationId() {
		return navigationId;
	}


	public void setNavigationId(String navigationId) {
		this.navigationId = navigationId;
	}


	public String getToPage() {
		return toPage;
	}


	public void setToPage(String toPage) {
		this.toPage = toPage;
	}


	public String getManualDoc() {
		return manualDoc;
	}


	public void setManualDoc(String manualDoc) {
		this.manualDoc = manualDoc;
	}


	public String getManualDocStatus() {
		return manualDocStatus;
	}


	public void setManualDocStatus(String manualDocStatus) {
		this.manualDocStatus = manualDocStatus;
	}

}