package com.konnect.jpms.reports;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class QuotesByAjax extends ActionSupport implements ServletRequestAware, IStatements{
private static final long serialVersionUID = 1L;
	
	HttpSession session;
	CommonFunctions CF = null;
	String strSessionEmpId;
	String strSessionOrgId;
	String strUserType;
	
	private String quotePost;
	private String strQuotedesc;
	private String strQuoteBy;
	
	private String offsetCnt;
	private String lastQuoteId;
	private String remainQuotes;
 public String execute() throws Exception {
		
		session = request.getSession(true);
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);

		if(CF==null)return LOGIN;
		
		strSessionEmpId = (String)session.getAttribute(EMPID);
		strSessionOrgId = (String)session.getAttribute(ORGID);
		strUserType = (String)session.getAttribute(BASEUSERTYPE);
		UtilityFunctions uF = new UtilityFunctions();
		getLoggedEmpImage(uF);
	//	System.out.println("lastQuoteId==>"+lastQuoteId+"\toffsetCnt==>"+offsetCnt);
		getAllQuotes(uF);
		return LOAD;
	}

	public void getLoggedEmpImage(UtilityFunctions uF){
		Connection con = null;
	
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			Map<String, String> hmCustName = CF.getCustomerNameMap(con);
			Map<String, String> hmResourceImage = CF.getEmpProfileImage(con);
			Map<String, String> hmCustImage = CF.getCustomerImageMap(con);
			
						
			if(strUserType != null && strUserType.equals(CUSTOMER)) {
				String createdByImage = hmCustImage.get(strSessionEmpId);
				String strClientId = CF.getClientIdBySPOCId(con, uF, strSessionEmpId);
				String MYLargeImage = "<img class='lazy img-circle' border=\"1px solid #CCCCCC\" height=\"80\" width=\"80\" src=\"userImages/avatar_photo.png\" />";
				String MYImage = "<img class='lazy img-circle' border=\"0\" height=\"30\" width=\"30\" src=\"userImages/avatar_photo.png\" />"; 
				if(CF.getStrDocRetriveLocation()==null) { 
					MYImage = "<img class='lazy img-circle' border=\"0\" height=\"30\" width=\"30\" src=\"userImages/avatar_photo.png\" data-original=\""+DOCUMENT_LOCATION + createdByImage+"\" />";
					MYLargeImage = "<img class='lazy' border=\"1px solid #CCCCCC\" height=\"80\" width=\"80\" src=\"userImages/avatar_photo.png\" data-original=\""+DOCUMENT_LOCATION + createdByImage+"\" />";
			  	} else if(createdByImage != null && !createdByImage.equals("")) {
			  		MYImage = "<img class='lazy img-circle' border=\"0\" height=\"30\" width=\"30\" src=\"userImages/avatar_photo.png\" data-original=\""+CF.getStrDocRetriveLocation() +I_CUSTOMER+"/"+strClientId+"/"+I_IMAGE+"/"+I_CUSTOMER_SPOC+"/"+I_22x22+"/"+createdByImage+"\" />";
			  		MYLargeImage = "<img class='lazy' border=\"1px solid #CCCCCC\" height=\"80\" width=\"80\" src=\"userImages/avatar_photo.png\" data-original=\""+CF.getStrDocRetriveLocation() +I_CUSTOMER+"/"+strClientId+"/"+I_IMAGE+"/"+I_CUSTOMER_SPOC+"/"+I_100x100+"/"+createdByImage+"\" />";
	            }
	//			System.out.println("MyLargeImage==>"+ MYLargeImage);
				request.setAttribute("MYLargeImg", MYLargeImage);
				request.setAttribute("MYImg", MYImage);
			} else {
				String createdByImage = hmResourceImage.get(strSessionEmpId);
				String MYLargeImage = "<img class='lazy img-circle' border=\"1px solid #CCCCCC\" height=\"80\" width=\"80\" src=\"userImages/avatar_photo.png\" />";
				String MYImage = "<img class='lazy img-circle' border=\"0\" height=\"30\" width=\"30\" src=\"userImages/avatar_photo.png\" />"; 
				if(CF.getStrDocRetriveLocation()==null) { 
					MYImage = "<img class='lazy img-circle' border=\"0\" height=\"30\" width=\"30\" src=\"userImages/avatar_photo.png\" data-original=\""+DOCUMENT_LOCATION + createdByImage+"\" />";
					MYLargeImage = "<img class='lazy img-circle' border=\"1px solid #CCCCCC\" height=\"80\" width=\"80\" src=\"userImages/avatar_photo.png\" data-original=\""+DOCUMENT_LOCATION + createdByImage+"\" />";
			  	} else if(createdByImage != null && !createdByImage.equals("")) {
			  		MYImage = "<img class='lazy img-circle' border=\"0\" height=\"30\" width=\"30\" src=\"userImages/avatar_photo.png\" data-original=\""+CF.getStrDocRetriveLocation() +I_PEOPLE+"/"+I_IMAGE+"/"+strSessionEmpId+"/"+I_22x22+"/"+createdByImage+"\" />";
			  		MYLargeImage = "<img class='lazy img-circle' border=\"1px solid #CCCCCC\" height=\"80\" width=\"80\" src=\"userImages/avatar_photo.png\" data-original=\""+CF.getStrDocRetriveLocation() +I_PEOPLE+"/"+I_IMAGE+"/"+strSessionEmpId+"/"+I_100x100+"/"+createdByImage+"\" />";
	            }
				request.setAttribute("MYLargeImg", MYLargeImage);
				request.setAttribute("MYImg", MYImage);
			}
					
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeConnection(con);
		}
	
	}
	public void getAllQuotes(UtilityFunctions uF){
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmQuoteIds = new LinkedHashMap<String, String>();
			Map<String, List<String>> hmQuotes = new LinkedHashMap<String, List<String>>();
			Map<String, String> hmCustName = CF.getCustomerNameMap(con);
			Map<String, String> hmResourceImage = CF.getEmpProfileImage(con);
			Map<String, String> hmCustImage = CF.getCustomerImageMap(con);
			
			
			Map<String, String> hmResourceName = CF.getEmpNameMap(con, null, null);
			
			pst = con.prepareStatement("select * from daythoughts where thought_id < "+uF.parseToInt(getLastQuoteId())+"order by thought_id desc");
//			System.out.println("pst =====/===>>> " + pst);
			rs = pst.executeQuery();
			StringBuilder sbQuoteIds = null;
			List<String> alQuoteIds = new ArrayList<String>();
			setRemainQuotes("NO");
			while(rs.next()) {
				if(alQuoteIds.size() == 10) {
					setRemainQuotes("YES");
					break;
				}
				alQuoteIds.add(rs.getString("thought_id"));
				hmQuoteIds.put(rs.getString("thought_id"), rs.getString("thought_id"));
				if(sbQuoteIds == null) {
					sbQuoteIds = new StringBuilder();
					sbQuoteIds.append(rs.getString("thought_id"));
				} else{
					sbQuoteIds.append(","+rs.getString("thought_id"));
				}
				
				setLastQuoteId(rs.getString("thought_id"));
			}
			rs.close();
			pst.close();
			request.setAttribute("hmQuoteIds", hmQuoteIds);
//			System.out.println("sbQuoteIds ===>> " + sbQuoteIds);
			
			if(sbQuoteIds != null) {
				StringBuilder sbQuery = new StringBuilder();
				sbQuery.append("select * from daythoughts where  thought_id in ("+sbQuoteIds.toString()+") order by thought_id desc ");
				pst = con.prepareStatement(sbQuery.toString());
//				System.out.println("pst ===>> " + pst);
				rs = pst.executeQuery();
				while(rs.next()) {
					List<String> alInner = new ArrayList<String>();
					alInner.add(Integer.toString(rs.getInt("thought_id")));//0
					alInner.add(uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));//1
					
					alInner.add(rs.getString("thought_by"));//2
					alInner.add(rs.getString("thought_text"));//3
					String addedBy = rs.getString("added_by");
					alInner.add(uF.showData(hmResourceName.get(rs.getString("added_by")),"Someone"));//4
					
					alInner.add(uF.getDateFormat(rs.getString("posted_date"),DBTIMESTAMP, DATE_FORMAT_STR+" "+TIME_FORMAT_AM_PM));//5
					alInner.add(rs.getString("added_by"));//6
					if(strUserType != null && strUserType.equals(CUSTOMER)) {
						String createdByImage1 = hmCustImage.get(rs.getString("added_by"));
						String strClientId = CF.getClientIdBySPOCId(con, uF, rs.getString("added_by"));
						String MYImage1 = "<img class='img-circle img-sm' alt=\"User Image\" style=\"width: 40px !important;height: 40px !important;\" src=\"userImages/avatar_photo.png\" />"; 
						if(CF.getStrDocRetriveLocation()==null) { 
							MYImage1 = "<img class='img-circle img-sm' alt=\"User Image\" style=\"width: 40px !important;height: 40px !important;\"src=\"userImages/avatar_photo.png\" data-original=\""+DOCUMENT_LOCATION + createdByImage1+"\" />";
					  	} else if(createdByImage1 != null && !createdByImage1.equals("")) {
					  		MYImage1= "<img class='img-circle img-sm' alt=\"User Image\" style=\"width: 40px !important;height: 40px !important;\" src=\"userImages/avatar_photo.png\" data-original=\""+CF.getStrDocRetriveLocation() +I_CUSTOMER+"/"+strClientId+"/"+I_IMAGE+"/"+I_CUSTOMER_SPOC+"/"+I_22x22+"/"+createdByImage1+"\" />";
			            }
						alInner.add(MYImage1);//7
					} else {
						String createdByImage1 = hmResourceImage.get(rs.getString("added_by"));
						String MYImage1 = "<img class='img-circle img-sm' alt=\"User Image\" style=\"width: 40px !important;height: 40px !important;\" src=\"userImages/avatar_photo.png\" />"; 
						if(CF.getStrDocRetriveLocation()==null) { 
							MYImage1 = "<img class='img-circle img-sm' alt=\"User Image\" style=\"width: 40px !important;height: 40px !important;\" src=\"userImages/avatar_photo.png\" data-original=\""+DOCUMENT_LOCATION + createdByImage1+"\" />";
						} else if(createdByImage1 != null && !createdByImage1.equals("")) {
					  		MYImage1 = "<img class='img-circle img-sm' alt=\"User Image\" style=\"width: 40px !important;height: 40px !important;\" src=\"userImages/avatar_photo.png\" data-original=\""+CF.getStrDocRetriveLocation() +I_PEOPLE+"/"+I_IMAGE+"/"+rs.getString("added_by")+"/"+I_22x22+"/"+createdByImage1+"\" />";
						}
						
						alInner.add(MYImage1);//7
					}
					
					hmQuotes.put(rs.getString("thought_id"), alInner);
				}
				rs.close();
				pst.close();
				
			}
			request.setAttribute("hmQuotes", hmQuotes);
//			System.out.println("hmquotes==>"+hmQuotes.size());
			
		} catch (Exception e) {
			e.printStackTrace();
			
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}
	
	public String getStrQuotedesc() {
		return strQuotedesc;
	}
	public void setStrQuotedesc(String strQuotedesc) {
		this.strQuotedesc = strQuotedesc;
	}
	public String getStrQuoteBy() {
		return strQuoteBy;
	}
	public void setStrQuoteBy(String strQuoteBy) {
		this.strQuoteBy = strQuoteBy;
	}
	public String getQuotePost() {
		return quotePost;
	}
	public void setQuotePost(String quotePost) {
		this.quotePost = quotePost;
	}
	public String getOffsetCnt() {
		return offsetCnt;
	}
	public void setOffsetCnt(String offsetCnt) {
		this.offsetCnt = offsetCnt;
	}
	public String getLastQuoteId() {
		return lastQuoteId;
	}
	public void setLastQuoteId(String lastQuoteId) {
		this.lastQuoteId = lastQuoteId;
	}
	public String getRemainQuotes() {
		return remainQuotes;
	}
	public void setRemainQuotes(String remainQuotes) {
		this.remainQuotes = remainQuotes;
	}
	
}
