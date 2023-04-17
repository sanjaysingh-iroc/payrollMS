package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
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

public class AddCommentOnPost extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	
	HttpSession session;
	CommonFunctions CF = null;
	String strSessionEmpId;
	String strSessionOrgId;
	String strUserType;
	
	private String strComment;
	private String mainPostId;
	private String postId;
	private String type; 
	private String strCommentTaggedWith;
	private String pageFrm;
	public String execute() throws Exception {
		
		session = request.getSession(true);
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
//		if (CF == null) {
//			CF = new CommonFunctions();
//		}
		if(CF==null)return LOGIN;
		
		strSessionEmpId = (String)session.getAttribute(EMPID);
		strSessionOrgId = (String)session.getAttribute(ORGID);
		strUserType = (String)session.getAttribute(BASEUSERTYPE);
		
		UtilityFunctions uF = new UtilityFunctions();
		
		//System.out.println("getPostId() ===>> " + getPostId());
		//System.out.println("getStrComment() ===>> " + getStrComment());
		
		addNewComment(uF);
	//	System.out.println("pageFrm==>"+getPageFrm());
		return LOAD;
	}
	
	
	private void addNewComment(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmResourceName = CF.getEmpNameMap(con, null, null);
			Map<String, String> hmCustName = CF.getCustomerNameMap(con);
			Map<String, String> hmResourceImage = CF.getEmpProfileImage(con);
			Map<String, String> hmCustImage = CF.getCustomerImageMap(con);
			
			if(getType() == null || !getType().equals("C")) {
				if(getType() != null && getType().equals("E")) {
					pst = con.prepareStatement("update communication_1 set communication=?,update_time=?,tagged_with=? where communication_id=?"); //created_by=?,
					pst.setString(1, getStrComment());
//					pst.setInt(2, uF.parseToInt(strSessionEmpId));
					pst.setTimestamp(2, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone())+"", DBDATE+DBTIME));
					pst.setString(3, getStrCommentTaggedWith());
					pst.setInt(4, uF.parseToInt(getPostId()));
					pst.executeUpdate();
					pst.close();
				} else {
					if(strUserType != null && strUserType.equals(CUSTOMER)) {
						pst = con.prepareStatement("insert into communication_1(communication,parent_id,client_created_by,create_time,tagged_with) values(?,?,?,?, ?)");
					} else {
						pst = con.prepareStatement("insert into communication_1(communication,parent_id,created_by,create_time,tagged_with) values(?,?,?,?, ?)");
					}
					pst.setString(1, getStrComment());
					pst.setInt(2, uF.parseToInt(getPostId()));
					pst.setInt(3, uF.parseToInt(strSessionEmpId));
					pst.setTimestamp(4, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone())+"", DBDATE+DBTIME));
					pst.setString(5, getStrCommentTaggedWith());
					pst.executeUpdate();
					pst.close(); 
				}
			}
			
			if(getType() != null && (getType().equals("E") || getType().equals("C"))) {
				pst = con.prepareStatement("select * from communication_1 where communication_id=?");
				pst.setInt(1, uF.parseToInt(getPostId()));
			} else {
				pst = con.prepareStatement("select * from communication_1 where communication_id = (select max(communication_id) from communication_1)");
			}
			rs = pst.executeQuery();
			List<String> innerList = new ArrayList<String>();
			
			while(rs.next()) {
				List<String> alLikeIds = new ArrayList<String>();
				String yourLike = null;
				if(strUserType != null && strUserType.equals(CUSTOMER)) {
					if(rs.getString("client_like_ids") != null && !rs.getString("client_like_ids").equals("")) {
						alLikeIds = Arrays.asList(rs.getString("client_like_ids").split(","));
						if(alLikeIds.contains(strSessionEmpId)) {
							yourLike = "Y";
						}
					}
				} else {
					if(rs.getString("like_ids") != null && !rs.getString("like_ids").equals("")) {
						alLikeIds = Arrays.asList(rs.getString("like_ids").split(","));
						if(alLikeIds.contains(strSessionEmpId)) {
							yourLike = "Y";
						}
					}
				}
				innerList.add(rs.getString("communication_id")); //0
				innerList.add(rs.getString("communication")); //1
				innerList.add(uF.showData(rs.getString("likes"), "0")); //2
				innerList.add(getResourceName(hmResourceName, rs.getString("like_ids"), hmCustName, rs.getString("client_like_ids"))); //3
				if(uF.parseToInt(rs.getString("client_created_by")) > 0) {
					String createdByImage = hmCustImage.get(rs.getString("client_created_by"));
					String strClientId = CF.getClientIdBySPOCId(con, uF, rs.getString("client_created_by"));
					
//					String addedImage = CF.getStrDocRetriveLocation()+I_CUSTOMER+"/"+strClientId+"/"+I_IMAGE+"/"+I_CUSTOMER_SPOC+"/"+I_22x22+"/"+((createdByImage!=null && !createdByImage.equals("")) ? createdByImage : "avatar_photo.png");
//					String addedImg = "<img class=\"lazy\" src=\""+ addedImage +"\" data-original=\""+ addedImage +"\" height=\"25\" width=\"25\">";
					String addedImg = "<img class='lazy' border=\"0\" height=\"25\" width=\"25\" src=\"userImages/avatar_photo.png\" />"; 
					if(CF.getStrDocRetriveLocation()==null) { 
						addedImg = "<img class='lazy' border=\"0\" height=\"25\" width=\"25\" src=\"userImages/avatar_photo.png\" data-original=\""+DOCUMENT_LOCATION + createdByImage+"\" />";
				  	} else if(createdByImage != null && !createdByImage.equals("")) {
				  		addedImg = "<img class='lazy' border=\"0\" height=\"25\" width=\"25\" src=\"userImages/avatar_photo.png\" data-original=\""+CF.getStrDocRetriveLocation() +I_CUSTOMER+"/"+strClientId+"/"+I_IMAGE+"/"+I_CUSTOMER_SPOC+"/"+I_22x22+"/"+createdByImage+"\" />";
		            }
					innerList.add(addedImg); //4
					innerList.add(hmResourceName.get(rs.getString("client_created_by"))); //5
				} else {
					String createdByImage = hmResourceImage.get(rs.getString("created_by"));
//					String addedImage = CF.getStrDocRetriveLocation() +I_PEOPLE+"/"+I_IMAGE+"/"+rs.getString("created_by")+"/"+I_60x60+"/"+((createdByImage!=null && !createdByImage.equals("")) ? createdByImage : "avatar_photo.png");
//					String addedImg = "<img class='lazy' src='" + addedImage + "' data-original='" + addedImage + "' height='25' width='25'>";
					String addedImg = "<img class='lazy' border=\"0\" height=\"25\" width=\"25\" src=\"userImages/avatar_photo.png\" />"; 
					if(CF.getStrDocRetriveLocation()==null) { 
						addedImg = "<img class='lazy' border=\"0\" height=\"25\" width=\"25\" src=\"userImages/avatar_photo.png\" data-original=\""+DOCUMENT_LOCATION + createdByImage+"\" />";
				  	} else if(createdByImage != null && !createdByImage.equals("")) {
				  		addedImg = "<img class='lazy' border=\"0\" height=\"25\" width=\"25\" src=\"userImages/avatar_photo.png\" data-original=\""+CF.getStrDocRetriveLocation() +I_PEOPLE+"/"+I_IMAGE+"/"+rs.getString("created_by")+"/"+I_22x22+"/"+createdByImage+"\" />";
		            }
					innerList.add(addedImg); //4
					innerList.add(hmResourceName.get(rs.getString("created_by"))); //5
				}
//				System.out.println(" =====>> " + uF.getDateFormat(rs.getString("create_time"), DBTIMESTAMP, DATE_FORMAT_STR+" "+TIME_FORMAT_AM_PM));
				innerList.add(uF.getDateFormat(rs.getString("create_time"), DBTIMESTAMP, DATE_FORMAT_STR+" "+TIME_FORMAT_AM_PM)); //6
				innerList.add(uF.showData(yourLike, "N")); //7
				if(strUserType != null && strUserType.equals(CUSTOMER)) {
					innerList.add(rs.getString("client_created_by")); //8
				} else {
					innerList.add(rs.getString("created_by")); //8
				}
				innerList.add(getAllResourceName(hmResourceName, rs.getString("tagged_with"))); //9
				innerList.add(rs.getString("like_ids")); //10
				innerList.add(rs.getString("client_like_ids")); //11
				String  clientLikes = rs.getString("client_like_ids");
				int likes = uF.parseToInt(uF.showData(rs.getString("likes"),"0"));
				
				int clikeCount = 0;
				if(clientLikes!=null && !clientLikes.equals("") && !clientLikes.equals(",")){
					String[] clikes = clientLikes.split(",");
					int length = clikes.length;
					clikeCount = length-1;
				}
				
				//System.out.println("comment likes+clikes==>"+(likes+clikeCount));
				innerList.add(String.valueOf(likes+clikeCount)); //12
			}
			rs.close();
			pst.close();
//			System.out.println("innerList ===>> " + innerList);
			
			request.setAttribute("innerList", innerList);
			
//			session.setAttribute(MESSAGE, SUCCESSM+"Disable access for "+uF.showData(strCustName, "")+" successfully."+END);
		} catch (Exception e) {
			e.printStackTrace();
//			session.setAttribute(MESSAGE, ERRORM+"Error while updating "+uF.showData(strCustName, "")+". Please try again."+END);
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	
	private String getResourceName(Map<String, String> hmResourceName, String resourceIds, Map<String, String> hmCustName, String spocId) {
		StringBuilder resouceName = null;
		UtilityFunctions uF = new UtilityFunctions();
		List<String> alResIds = new ArrayList<String>();
		int cnt = 0;
		
		if(resourceIds != null && resourceIds.length()>0) {
			alResIds = Arrays.asList(resourceIds.split(","));
			for(int i=0; alResIds != null && !alResIds.isEmpty() && i<alResIds.size(); i++) {
				if(uF.parseToInt(alResIds.get(i)) > 0) {
					if(resouceName == null) {
						resouceName = new StringBuilder();
						resouceName.append(hmResourceName.get(alResIds.get(i)));
					} else {
						cnt++;
//						resouceName.append(", "+hmResourceName.get(alResIds.get(i)));
					}
				}
			}
		}
		if(spocId != null &&spocId.length() > 0) {
			alResIds = Arrays.asList(spocId.split(","));
			for(int i=0; alResIds != null && !alResIds.isEmpty() && i<alResIds.size(); i++) {
				if(uF.parseToInt(alResIds.get(i)) > 0) {
					if(resouceName == null) {
						resouceName = new StringBuilder();
						resouceName.append(hmCustName.get(alResIds.get(i)));
					} else {
						cnt++;
					}
				}
			}
		}
		
		if(cnt>0) {
			resouceName.append(" <span style=\"color: gray;\">and</span> "+cnt+" others");
		}
		
		if(resouceName == null) {
			resouceName = new StringBuilder();
		}
		return resouceName.toString();
	}
	
	
	private String getAllResourceName(Map<String, String> hmResourceName, String resourceIds) {
		StringBuilder resouceName = null;
		UtilityFunctions uF = new UtilityFunctions();
		List<String> alResIds = new ArrayList<String>();
		
		if(resourceIds != null && resourceIds.length()>0) {
			int cnt = 0;
			alResIds = Arrays.asList(resourceIds.split(","));
			for(int i=0; alResIds != null && !alResIds.isEmpty() && i<alResIds.size(); i++) {
				if(uF.parseToInt(alResIds.get(i)) > 0) {
					if(resouceName == null) {
						resouceName = new StringBuilder();
						resouceName.append(hmResourceName.get(alResIds.get(i)));
					} else {
//						cnt++;
						resouceName.append(", "+hmResourceName.get(alResIds.get(i)));
					}
				}
			}
//			if(cnt>0) {
//				resouceName.append(" <span style=\"color: gray;\">and</span> "+cnt+" others");
//			}
		}
		if(resouceName == null) {
			resouceName = new StringBuilder();
		}
		return resouceName.toString();
	}

	
	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public String getStrComment() {
		return strComment;
	}

	public void setStrComment(String strComment) {
		this.strComment = strComment;
	}

	public String getPostId() {
		return postId;
	}

	public void setPostId(String postId) {
		this.postId = postId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getMainPostId() {
		return mainPostId;
	}

	public void setMainPostId(String mainPostId) {
		this.mainPostId = mainPostId;
	}

	public String getStrCommentTaggedWith() {
		return strCommentTaggedWith;
	}

	public void setStrCommentTaggedWith(String strCommentTaggedWith) {
		this.strCommentTaggedWith = strCommentTaggedWith;
	}


	public String getPageFrm() {
		return pageFrm;
	}


	public void setPageFrm(String pageFrm) {
		this.pageFrm = pageFrm;
	}

	

	
}