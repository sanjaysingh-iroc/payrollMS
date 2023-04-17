package com.konnect.jpms.performance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;
import java.util.*;

public class EditAndDeleteBSC extends ActionSupport implements ServletRequestAware, IStatements {
	
	private static final long serialVersionUID = -4581306410273940249L;
	HttpSession session;
	String strSessionEmpId;
	String strEmpOrgId;
	String strUserTypeId;
	String strBaseUserTypeId;
	String strBaseUserType;
	String strcallFRom;
	String strBscName;
	String strBscVision;
	String strBscMission;
	String strPerspectiveName;
	String strPerspectiveColor;
	String strPerspectiveDesc;
	String strPerspectiveWeightage;
	private String submit;
	private String perspectiveCount;
	private String callFrom;
	private String perspectiveCount1;
	private String perspectiveoldCount;
	private String perspectiveId;
	CommonFunctions CF;
	
	String strBscId;
	String Operation;
	String strUserType;

	public String execute() {
		session = request.getSession();
		strSessionEmpId = (String) session.getAttribute(EMPID);
		strEmpOrgId = (String) session.getAttribute(ORGID);
		strUserTypeId = (String) session.getAttribute(USERTYPEID);
		strUserType = (String) session.getAttribute(USERTYPE);
		strBaseUserType = (String) session.getAttribute(BASEUSERTYPE);
		strBaseUserTypeId = (String) session.getAttribute(BASEUSERTYPEID);
		request.setAttribute(PAGE, "/jsp/performance/BscDetails.jsp");
		request.setAttribute(TITLE, "BscDeatils"); //TKRAs
		UtilityFunctions uF = new UtilityFunctions();
		if(getOperation() != null && getOperation().equalsIgnoreCase("E") && getStrcallFRom() == null){
			getBSCDetails(uF);
			return LOAD;
		}
		if(getSubmit()!=null && getSubmit().equalsIgnoreCase("update")){
			updateBSC(uF);
			//return SUCCESS;
		}
		if(getSubmit()!=null && getSubmit().equalsIgnoreCase("delete")){
			deletePerspective(uF);
			//return SUCCESS;
		}
		
		deleteBSc(uF);
		return LOAD;

	}
	
	
	private void deletePerspective(UtilityFunctions uF) {
		System.out.println("in delete::");
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		String bscPerspectiveIds = null;
		String sbper = null;
		try{
			con = db.makeConnection(con);
			System.out.println("getPerspectiveId()"+getPerspectiveId());
			System.out.println("strBscId::"+strBscId);
			
			pst = con.prepareStatement("select *  from bsc_details where bsc_id = ?");
			pst.setInt(1, uF.parseToInt(strBscId));
			rs = pst.executeQuery();
			while(rs.next()){
				bscPerspectiveIds = rs.getString("bsc_perspective_ids");
			}
			System.out.println("pst1====>::"+pst);
			rs.close();
			pst.close();
			
			bscPerspectiveIds.replace(","+getPerspectiveId(), "");
			System.out.println("bscPerspectiveIds::"+bscPerspectiveIds);
			
			pst = con.prepareStatement("delete  from bsc_perspective_details where bsc_perspective_id = ?");
			pst.setInt(1, uF.parseToInt(getPerspectiveId()));
			pst.execute();
			System.out.println("pst2====>::"+pst);
			pst.close();
			
			pst = con.prepareStatement("update  bsc_details set bsc_perspective_ids = ? where bsc_id =?");
			pst.setString(1, bscPerspectiveIds);
			pst.setInt(2, uF.parseToInt(strBscId));
			
			pst.executeUpdate();
			System.out.println("pst3====>::"+pst);
			pst.close();
			
			
			
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);			
		}
	}
	
	
	private void updateBSC(UtilityFunctions uF){
		String newPerspectiveId = null;
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		String bscPerspectiveIds = null;
		String sbper = null;
		try{
			con = db.makeConnection(con);
			
//			System.out.println("newCount==>"+getPerspectiveCount1());
//			System.out.println("oldCount==>"+getPerspectiveoldCount());
//			System.out.println("getStrBscId()==>"+getStrBscId());
			String[] newcount1 = getPerspectiveCount1().split(",");
//			System.out.println("newcount1()==>"+newcount1);
			System.out.println("getPerspectiveId() ===>> " + getPerspectiveId());
			
			int newcount = uF.parseToInt(newcount1[0]);
			System.out.print("insertCount==>"+newcount);
			int oldCount = uF.parseToInt(getPerspectiveoldCount());
			int insertCount = newcount - oldCount;
//			System.out.print("insertCount==>"+insertCount);
			
			System.out.print("perspectiveCount==>"+perspectiveCount);
			
			
			pst = con.prepareStatement("select * from bsc_details where bsc_id = ?");
			pst.setInt(1, uF.parseToInt(getStrBscId()));
			rs = pst.executeQuery();
			
			while (rs.next()) {
				sbper = rs.getString("bsc_perspective_ids");
			}
			System.out.print("pst2==>"+pst);
			rs.close();
			pst.close();
			
			System.out.print("sbper==>"+sbper);
			String[] perIds = sbper.split(",");
			for(int i = 0; i<perIds.length;i++ ){
				if(perIds[i] != null && perIds[i].length() > 0){
					System.out.print("perIds[i]==>"+perIds[i]);
					String strPerspective = request.getParameter("strPerspective_"+i);
					 String strPerspectivedesc = request.getParameter("strPerspectiveDescription_"+i);
					  System.out.println("strPerspectivedesc::"+strPerspectivedesc);
					 int strWeightage =uF.parseToInt(request.getParameter("perspectiveWeightage_"+i));
					 String strPerspectiveColor = request.getParameter("strPerspectiveColourCode_"+i);
					 String strPerspective1 = request.getParameter("strPerspective_"+i);
					 
					pst = con.prepareStatement("update bsc_perspective_details set bsc_perspective_name = ?,perspective_description=?,perspective_color=?,weightage=? where bsc_perspective_id = ? ");
						 pst.setString(1, strPerspective);
						 pst.setString(2, strPerspectivedesc);
						 pst.setString(3, strPerspectiveColor);
						 pst.setInt(4, strWeightage);
						 pst.setInt(5,uF.parseToInt(perIds[i]));
						 System.out.print("pst1==>"+pst);
						 pst.executeUpdate();
						 pst.close();
					
				}
			}
			
			pst = con.prepareStatement("update  bsc_details set bsc_name = ?, bsc_vision = ?, bsc_mission=?,bsc_perspective_ids = ? where bsc_id =?");
			pst.setString(1, getStrBscName());
			pst.setString(2, getStrBscVision());
			pst.setString(3, getStrBscMission());
			pst.setString(4, sbper.toString());
			pst.setInt(5, uF.parseToInt(getStrBscId()));
			System.out.print("update pst3===>"+pst);
			pst.executeUpdate();
			pst.close();
			
			
			if(insertCount > 0 ){
				
				StringBuilder sb =  new StringBuilder();
				pst = con.prepareStatement("select max(bsc_perspective_id) from bsc_perspective_details");
				rs = pst.executeQuery();
				while (rs.next()) {
					newPerspectiveId = rs.getString(1);
				}
				rs.close();
				pst.close();
				
				
				System.out.println("bscPerspectiveIds::"+bscPerspectiveIds);
				if(bscPerspectiveIds == null){
					bscPerspectiveIds =  newPerspectiveId;
				}else{
					bscPerspectiveIds += "," +  newPerspectiveId;
				}
				System.out.println("bscPerspectiveIds111::"+bscPerspectiveIds);
				  System.out.println("sbper::"+sbper);
				for (int i = oldCount+1; i <= newcount; i++) {
					 String strPerspective = request.getParameter("strPerspective_"+i);
					 String strPerspectivedesc = request.getParameter("strPerspectiveDescription_"+i);
					  System.out.println("strPerspectivedesc::"+strPerspectivedesc);
					 int strWeightage =uF.parseToInt(request.getParameter("perspectiveWeightage_"+i));
					 String strPerspectiveColor = request.getParameter("strPerspectiveColourCode_"+i);
					 String strPerspective1 = request.getParameter("strPerspective_"+i);
					 String strCo = "strPerspectiveColourCode_"+i;
					 
					 if(strPerspective !=null ){
					 	pst = con.prepareStatement("insert into bsc_perspective_details(bsc_perspective_name,perspective_description,perspective_color,weightage) values(?,?,?,?)");
						 pst.setString(1, strPerspective);
						 pst.setString(2, strPerspectivedesc);
						 pst.setString(3, strPerspectiveColor);
						 pst.setInt(4, strWeightage);
						 System.out.print("pst1==>"+pst);
						 pst.executeUpdate();
						 pst.close();
					 }
					
				}
				
				pst = con.prepareStatement("select bsc_perspective_id from bsc_perspective_details where bsc_perspective_id >"+newPerspectiveId);
				rs = pst.executeQuery();
				while (rs.next()) {
					if(sb==null) {
						sb = new StringBuilder();
						sb.append(","+rs.getString("bsc_perspective_id")+",");
					} else {
						sb.append(rs.getString("bsc_perspective_id")+",");
					}
				}
				System.out.println("sbbbb::"+sb);
				
				String strPerspectiveIds = sbper + sb;
				System.out.println("strPerspectiveIds::"+strPerspectiveIds);
				pst = con.prepareStatement("update  bsc_details set bsc_name = ?, bsc_vision = ?, bsc_mission=?,bsc_perspective_ids = ? where bsc_id =?");
				pst.setString(1, getStrBscName());
				pst.setString(2, getStrBscVision());
				pst.setString(3, getStrBscMission());
				pst.setString(4, strPerspectiveIds);
				pst.setInt(5, uF.parseToInt(getStrBscId()));
				System.out.print("insert pst3===>"+pst);
				pst.executeUpdate();
				pst.close();
				
				
			}
			
		
			
		/*	pst = con.prepareStatement("select max(bsc_perspective_id) from bsc_perspective_details");
			rs = pst.executeQuery();
			while (rs.next()) {
				newPerspectiveId = rs.getInt(1);
			}
			rs.close();
			pst.close();
			for (int i = 1; i <=countserial; i++) {
			
				 String strPerspective = request.getParameter("strPerspective_"+i);
				 String strPerspectivedesc = request.getParameter("strPerspectiveDescription_"+i);
				  System.out.println("strPerspectivedesc::"+strPerspectivedesc);
				 int strWeightage =uF.parseToInt(request.getParameter("perspectiveWeightage_"+i));
				 String strPerspectiveColor = request.getParameter("strPerspectiveColourCode_"+i);
				 String strPerspective1 = request.getParameter("strPerspective_"+i);

				 	String strCo = "strPerspectiveColourCode_"+i;
				  pst = con.prepareStatement("insert into bsc_perspective_details(bsc_perspective_name,perspective_description,perspective_color,weightage) values(?,?,?,?)");
					 pst.setString(1, strPerspective);
					 pst.setString(2, strPerspectivedesc);
					 pst.setString(3, strPerspectiveColor);
					 pst.setInt(4, strWeightage);
					 System.out.print("pst1==>"+pst);
					 pst.executeUpdate();
					 pst.close();
				 
			}
			
			StringBuilder sb = null; 
			pst = con.prepareStatement("select bsc_perspective_id from bsc_perspective_details where bsc_perspective_id >"+newPerspectiveId);
			rs = pst.executeQuery();
			while (rs.next()) {
				if(sb==null) {
					sb = new StringBuilder();
					sb.append(","+rs.getString("bsc_perspective_id")+",");
				} else {
					sb.append(rs.getString("bsc_perspective_id")+",");
				}
			}
			System.out.print("pst2==>"+pst);
			rs.close();
			pst.close();
			if(sb==null) {
				sb = new StringBuilder();
			}
			pst = con.prepareStatement("update  bsc_details set bsc_name = ?, bsc_vision = ?, bsc_mission=?,bsc_perspective_ids = ? where bsc_id =?");
			pst.setString(1, getStrBscName());
			pst.setString(2, getStrBscVision());
			pst.setString(3, getStrBscMission());
			pst.setString(4, sb.toString());
			pst.setInt(5, uF.parseToInt(getStrBscId()));
			System.out.print("insert pst3===>"+pst);
			pst.executeUpdate();
			pst.close();
			*/
			setCallFrom("GoalKRABsc");
			
		}catch (Exception e) {
				e.printStackTrace();
		} finally {
				db.closeResultSet(rs);
				db.closeStatements(pst);
				db.closeConnection(con);			
		}
	}
	
	
	
	private void getBSCDetails(UtilityFunctions uF){
		int count = 0;
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		Map<String,List<String>> hmPerspectives = new HashMap<String,List<String>>();
		String bscPerspectiveIds = null;
		try{
			con = db.makeConnection(con);
			pst=con.prepareStatement("select *  from  bsc_details where bsc_id  = ?");
			pst.setInt(1, uF.parseToInt(getStrBscId()));
			rs = pst.executeQuery();
			while(rs.next())
			{
				strBscName = rs.getString("bsc_name");
				strBscVision = rs.getString("bsc_vision");
				strBscMission = rs.getString("bsc_mission");
				bscPerspectiveIds = rs.getString("bsc_perspective_ids");
				
			}
			rs.close();
			pst.close();
			
			String[] perspective = bscPerspectiveIds.split(",");
			
			Map<String,List<String>> hmperspectiveDetails = new HashMap<String,List<String>>();
			List<String> alperspectives = new ArrayList<String>();
			for(int i = 0 ;i < perspective.length; i++ ){
				if(perspective[i] != null && perspective[i].length() > 0){
					count++;
					alperspectives.add(perspective[i]);
					pst=con.prepareStatement("select *  from  bsc_perspective_details where bsc_perspective_id = ?");
					pst.setInt(1,uF.parseToInt(perspective[i]));
					System.out.println("pst123===>"+pst);
					rs = pst.executeQuery();
					List<String> InnerList = new ArrayList<String>();
					while(rs.next())
						{
							InnerList.add(rs.getString("bsc_perspective_name"));
							InnerList.add(rs.getString("weightage"));
							InnerList.add(rs.getString("perspective_description"));
							InnerList.add(rs.getString("perspective_color"));
						}
					hmperspectiveDetails.put(perspective[i], InnerList);
				
					
				}
			}
			
			hmPerspectives.put(getStrBscId(), alperspectives);
			request.setAttribute("strBscId", getStrBscId());
			request.setAttribute("hmPerspectives", hmPerspectives);
			request.setAttribute("strBscName", strBscName);
			request.setAttribute("strBscVision", strBscVision);
			request.setAttribute("strBscMission", strBscMission);
			request.setAttribute("hmperspectiveDetails", hmperspectiveDetails);
			request.setAttribute("perspectiveCount", count);
			rs.close();
			pst.close();
		}catch (Exception e) {
				e.printStackTrace();
		} finally {
				db.closeResultSet(rs);
				db.closeStatements(pst);
				db.closeConnection(con);			
		}
	}
	
	private void deleteBSc(UtilityFunctions uF){
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try{
			con = db.makeConnection(con);
			if(getOperation() !=  null && getOperation().equalsIgnoreCase("D") && getStrBscId() != null ){
				
				pst=con.prepareStatement("delete from  bsc_details where bsc_id  = ?");
				pst.setInt(1, uF.parseToInt(getStrBscId()));
				pst.execute();

				pst.close();
			}
			setCallFrom("GoalKRABsc");
		}catch (Exception e) {
			e.printStackTrace();
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
	public String getStrBscId() {
		return strBscId;
	}

	public void setStrBscId(String strBscId) {
		this.strBscId = strBscId;
	}

	public String getOperation() {
		return Operation;
	}

	public void setOperation(String operation) {
		Operation = operation;
	}
	public String getStrcallFRom() {
		return strcallFRom;
	}
	public void setStrcallFRom(String strcallFRom) {
		this.strcallFRom = strcallFRom;
	}
	public String getStrBscName() {
		return strBscName;
	}
	public void setStrBscName(String strBscName) {
		this.strBscName = strBscName;
	}
	public String getStrBscVision() {
		return strBscVision;
	}
	public void setStrBscVision(String strBscVision) {
		this.strBscVision = strBscVision;
	}
	public String getStrBscMission() {
		return strBscMission;
	}
	public void setStrBscMission(String strBscMission) {
		this.strBscMission = strBscMission;
	}
	public String getStrPerspectiveName() {
		return strPerspectiveName;
	}
	public void setStrPerspectiveName(String strPerspectiveName) {
		this.strPerspectiveName = strPerspectiveName;
	}
	public String getStrPerspectiveColor() {
		return strPerspectiveColor;
	}
	public void setStrPerspectiveColor(String strPerspectiveColor) {
		this.strPerspectiveColor = strPerspectiveColor;
	}
	public String getStrPerspectiveDesc() {
		return strPerspectiveDesc;
	}
	public void setStrPerspectiveDesc(String strPerspectiveDesc) {
		this.strPerspectiveDesc = strPerspectiveDesc;
	}
	public String getStrPerspectiveWeightage() {
		return strPerspectiveWeightage;
	}
	public void setStrPerspectiveWeightage(String strPerspectiveWeightage) {
		this.strPerspectiveWeightage = strPerspectiveWeightage;
	}
	public String getSubmit() {
		return submit;
	}
	public void setSubmit(String submit) {
		this.submit = submit;
	}
	public String getPerspectiveCount() {
		return perspectiveCount;
	}
	public void setPerspectiveCount(String perspectiveCount) {
		this.perspectiveCount = perspectiveCount;
	}
	public String getCallFrom() {
		return callFrom;
	}
	public void setCallFrom(String callFrom) {
		this.callFrom = callFrom;
	}
	public String getPerspectiveCount1() {
		return perspectiveCount1;
	}
	public void setPerspectiveCount1(String perspectiveCount1) {
		this.perspectiveCount1 = perspectiveCount1;
	}
	int countserial;
	
	public int getCountserial() {
		return countserial;
	}
	public void setCountserial(int countserial) {
		this.countserial = countserial;
	}
	public String getPerspectiveoldCount() {
		return perspectiveoldCount;
	}
	public void setPerspectiveoldCount(String perspectiveoldCount) {
		this.perspectiveoldCount = perspectiveoldCount;
	}
	public String getPerspectiveId() {
		return perspectiveId;
	}
	public void setPerspectiveId(String perspectiveId) {
		this.perspectiveId = perspectiveId;
	}
	
	

}
