package com.konnect.jpms.performance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
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

public class GoalKRABsc extends ActionSupport implements ServletRequestAware, IStatements{

	public HttpSession session;
	HttpServletRequest request;
	CommonFunctions CF;
	
	private String dataType;
	private String bscName;
	private String bscVision;
	private String bscMision;
	private String[] perspectiveCount;
	private String submit; 
	private String operation;
	private String callFrom;

	String strBscId;
	
	public String execute() {
		UtilityFunctions uF = new UtilityFunctions();
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null) return LOGIN;
		
		request.setAttribute(PAGE, "/jsp/performance/GoalKRABsc.jsp");
		
		if(getOperation() != null && getOperation().equalsIgnoreCase("E")) {
			getBSCDetails(uF);
			return LOAD;
		} else if(getOperation() != null && getOperation().equalsIgnoreCase("U")) {
			updateBSC(uF);
//			return LOAD;
		} else if(getOperation() != null && getOperation().equalsIgnoreCase("D")) {
			deleteBSC(uF);
//			return LOAD;
		} else if(getSubmit() != null && getSubmit().equalsIgnoreCase("Save") ) {
		  insertBsc();
		}
	
		 return LOAD;
	}
	
	
	private void deleteBSC(UtilityFunctions uF){
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try{
			con = db.makeConnection(con);
			
			String bscPerspectiveIds = null;
			pst = con.prepareStatement("select *  from bsc_details where bsc_id = ?");
			pst.setInt(1, uF.parseToInt(getStrBscId()));
			rs = pst.executeQuery();
			while(rs.next()){
				bscPerspectiveIds = rs.getString("bsc_perspective_ids");
			}
			rs.close();
			pst.close();
			
			if(bscPerspectiveIds !=null && bscPerspectiveIds.length()>1) {
				bscPerspectiveIds = bscPerspectiveIds.substring(1, bscPerspectiveIds.length()-1);
			}
//			System.out.println("bscPerspectiveIds ====> "+bscPerspectiveIds);
			
			pst = con.prepareStatement("delete from bsc_perspective_details where bsc_perspective_id in ("+bscPerspectiveIds+") ");
//			System.out.println("pst2====>::"+pst);
			pst.executeUpdate();
			pst.close();
			
			pst=con.prepareStatement("delete from bsc_details where bsc_id = ?");
			pst.setInt(1, uF.parseToInt(getStrBscId()));
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
	
	
	private void updateBSC(UtilityFunctions uF){
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		try{
			con = db.makeConnection(con);
			
			String bscPerspectiveIds = null;
			pst = con.prepareStatement("select * from bsc_details where bsc_id = ?");
			pst.setInt(1, uF.parseToInt(getStrBscId()));
			rst = pst.executeQuery();
			while (rst.next()) {
				bscPerspectiveIds = rst.getString("bsc_perspective_ids");
			}
			List<String> alPersIds = new ArrayList<String>();
			List<String> alRemovePersIds = new ArrayList<String>();
			if(bscPerspectiveIds !=null) {
				alPersIds = Arrays.asList(bscPerspectiveIds.split(","));
			}
			for(int i=0; i<alPersIds.size(); i++) {
				if(uF.parseToInt(alPersIds.get(i))>0) {
					alRemovePersIds.add(alPersIds.get(i));
				}
			}
//			System.out.println("alPersIds ===> " + alPersIds);
//			System.out.println("alRemovePersIds ===> " + alRemovePersIds);
			
			int newPerspectiveId=0;
			pst = con.prepareStatement("select max(bsc_perspective_id) from bsc_perspective_details");
			rst = pst.executeQuery();
			while (rst.next()) {
				newPerspectiveId = rst.getInt(1);
			}
			rst.close();
			pst.close();
			
			StringBuilder sbPersIds = null;
			for (int i = 0; i <getPerspectiveCount().length; i++) {
				String perspectiveId = request.getParameter("perspectiveId_"+getPerspectiveCount()[i]);
				String strPerspective = request.getParameter("strPerspective_"+getPerspectiveCount()[i]);
				String strPerspectivedesc = request.getParameter("strPerspectiveDescription_"+getPerspectiveCount()[i]);
				int strWeightage =uF.parseToInt(request.getParameter("perspectiveWeightage_"+getPerspectiveCount()[i]));
				String strPerspectiveColor = request.getParameter("strPerspectiveColourCode_"+getPerspectiveCount()[i]);
				
				if(uF.parseToInt(perspectiveId)>0) {
					pst = con.prepareStatement("update bsc_perspective_details set bsc_perspective_name=?,perspective_description=?,perspective_color=?,weightage=? where bsc_perspective_id=?");
					pst.setString(1, strPerspective);
					pst.setString(2, strPerspectivedesc);
					pst.setString(3, strPerspectiveColor);
					pst.setInt(4, strWeightage);
					pst.setInt(5, uF.parseToInt(perspectiveId));
					pst.executeUpdate();
//					System.out.println("pst ===>> " + pst);
					pst.close();
					if(sbPersIds == null) {
						sbPersIds = new StringBuilder();
						sbPersIds.append(","+perspectiveId+",");
					} else {
						sbPersIds.append(perspectiveId+",");
					}
					if(alRemovePersIds.contains(perspectiveId)) {
						alRemovePersIds.remove(perspectiveId);
					}
//					alPersIds.remove("\""+newPerspectiveId+"\"");
				} else {
					pst = con.prepareStatement("insert into bsc_perspective_details(bsc_perspective_name,perspective_description,perspective_color,weightage) values(?,?,?,?)");
					pst.setString(1, strPerspective);
					pst.setString(2, strPerspectivedesc);
					pst.setString(3, strPerspectiveColor);
					pst.setInt(4, strWeightage);
					pst.executeUpdate();
					pst.close();
				}
			}
			
			for(int i=0; i<alRemovePersIds.size(); i++) {
				if(uF.parseToInt(alRemovePersIds.get(i))==0) {
					continue;
				}
				pst = con.prepareStatement("delete from bsc_perspective_details where bsc_perspective_id=?");
				pst.setInt(1, uF.parseToInt(alRemovePersIds.get(i)));
				pst.executeUpdate();
				pst.close();
			}
			
			pst = con.prepareStatement("select bsc_perspective_id from bsc_perspective_details where bsc_perspective_id >"+newPerspectiveId);
			rst = pst.executeQuery();
			while (rst.next()) {
				if(sbPersIds == null) {
					sbPersIds = new StringBuilder();
					sbPersIds.append(","+rst.getString("bsc_perspective_id")+",");
				} else {
					sbPersIds.append(rst.getString("bsc_perspective_id")+",");
				}
			}
			rst.close();
			pst.close();

			if(sbPersIds==null) {
				sbPersIds = new StringBuilder();
			}
			
			if(getBscName()!=null && getBscName().trim().length()>0) {
				pst = con.prepareStatement("update  bsc_details set bsc_name = ?, bsc_vision = ?, bsc_mission=?,bsc_perspective_ids = ? where bsc_id =?");
				pst.setString(1, getBscName());
				pst.setString(2, getBscVision());
				pst.setString(3, getBscMision());
				pst.setString(4, sbPersIds.toString());
				pst.setInt(5, uF.parseToInt(getStrBscId()));
				System.out.print("insert pst3===>"+pst);
				pst.executeUpdate();
				pst.close();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);			
		}
	}
	
	
	private void getBSCDetails(UtilityFunctions uF){
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		String bscPerspectiveIds = null;
		try{
			con = db.makeConnection(con);
			pst=con.prepareStatement("select * from bsc_details where bsc_id = ?");
			pst.setInt(1, uF.parseToInt(getStrBscId()));
			rs = pst.executeQuery();
			while(rs.next()) {
				setBscName(uF.showData(rs.getString("bsc_name"), ""));
				setBscVision(uF.showData(rs.getString("bsc_vision"), ""));
				setBscMision(uF.showData(rs.getString("bsc_mission"), ""));
				bscPerspectiveIds = rs.getString("bsc_perspective_ids");
				setOperation("U");
			}
			rs.close();
			pst.close();
			
			String[] perspective = bscPerspectiveIds.split(",");
			
			Map<String, List<String>> hmBSCPerspectives = new LinkedHashMap<String,List<String>>();
			for(int i = 0 ;i < perspective.length; i++ ){
				if(perspective[i] != null && perspective[i].length() > 0) {
					pst=con.prepareStatement("select * from  bsc_perspective_details where bsc_perspective_id = ?");
					pst.setInt(1,uF.parseToInt(perspective[i]));
//					System.out.println("pst123===>"+pst);
					rs = pst.executeQuery();
					List<String> innerList = new ArrayList<String>();
					while(rs.next()) {
						innerList.add(rs.getString("bsc_perspective_name"));
						innerList.add(rs.getString("weightage"));
						innerList.add(rs.getString("perspective_description"));
						innerList.add(rs.getString("perspective_color"));
					}
					rs.close();
					pst.close();
					hmBSCPerspectives.put(perspective[i], innerList);
				}
			}
			request.setAttribute("hmBSCPerspectives", hmBSCPerspectives);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);			
		}
	}
	
	
	private void insertBsc() {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rst = null;
		UtilityFunctions uF = new UtilityFunctions();
	
		try {
			con = db.makeConnection(con);
//			countserial = uF.parseToInt(getPerspectiveCount());
			int newPerspectiveId=0;
			pst = con.prepareStatement("select max(bsc_perspective_id) from bsc_perspective_details");
			rst = pst.executeQuery();
			while (rst.next()) {
				newPerspectiveId = rst.getInt(1);
			}
			rst.close();
			pst.close();
			
			for (int i = 0; i <getPerspectiveCount().length; i++) {
				String strPerspective = request.getParameter("strPerspective_"+getPerspectiveCount()[i]);
				String strPerspectivedesc = request.getParameter("strPerspectiveDescription_"+getPerspectiveCount()[i]);
				int strWeightage =uF.parseToInt(request.getParameter("perspectiveWeightage_"+getPerspectiveCount()[i]));
				String strPerspectiveColor = request.getParameter("strPerspectiveColourCode_"+getPerspectiveCount()[i]);
				pst = con.prepareStatement("insert into bsc_perspective_details(bsc_perspective_name,perspective_description,perspective_color,weightage) values(?,?,?,?)");
				pst.setString(1, strPerspective);
				pst.setString(2, strPerspectivedesc);
				pst.setString(3, strPerspectiveColor);
				pst.setInt(4, strWeightage);
				pst.executeUpdate();
				pst.close();
			}
			
			StringBuilder sb = null; 
			pst = con.prepareStatement("select bsc_perspective_id from bsc_perspective_details where bsc_perspective_id >"+newPerspectiveId);
			rst = pst.executeQuery();
			while (rst.next()) {
				if(sb==null) {
					sb = new StringBuilder();
					sb.append(","+rst.getString("bsc_perspective_id")+",");
				} else {
					sb.append(rst.getString("bsc_perspective_id")+",");
				}
			}
			rst.close();
			pst.close();
			
			if(sb==null) {
				sb = new StringBuilder();
			}
			
			if(getBscName()!=null && getBscName().trim().length()>0) {
				pst = con.prepareStatement("insert into bsc_details(bsc_name,bsc_vision,bsc_mission,bsc_perspective_ids) values(?,?,?,?)");
				pst.setString(1, getBscName());
				pst.setString(2, getBscVision());
				pst.setString(3, getBscMision());
				pst.setString(4, sb.toString());
	//			System.out.print("insert pst===>"+pst);
				pst.executeUpdate();
				pst.close();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} 
		finally {
			db.closeStatements(pst);
			db.closeResultSet(rst);
			db.closeConnection(con);
		}
	}

	public String getBscName() {
		return bscName;
	}

	public void setBscName(String bscName) {
		this.bscName = bscName;
	}

	public String getBscVision() {
		return bscVision;
	}

	public void setBscVision(String bscVision) {
		this.bscVision = bscVision;
	}

	public String getBscMision() {
		return bscMision;
	}

	public void setBscMision(String bscMision) {
		this.bscMision = bscMision;
	}
	
	public String getCallFrom() {
		return callFrom;
	}

	public void setCallFrom(String callFrom) {
		this.callFrom = callFrom;
	}

	public String getSubmit() {
		return submit;
	}

	public void setSubmit(String submit) {
		this.submit = submit;
	}

	public String[] getPerspectiveCount() {
		return perspectiveCount;
	}

	public void setPerspectiveCount(String[] perspectiveCount) {
		this.perspectiveCount = perspectiveCount;
	}
	
	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getStrBscId() {
		return strBscId;
	}

	public void setStrBscId(String strBscId) {
		this.strBscId = strBscId;
	}


	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}
	
	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

}
