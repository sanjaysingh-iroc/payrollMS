package com.konnect.jpms.reports.master;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillDesig;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class GradeReport extends ActionSupport implements ServletRequestAware, IStatements {
 
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	List<FillLevel> levelList;
	List<FillDesig> desigList;
	private static Logger log = Logger.getLogger(GradeReport.class);
	
	public String execute() throws Exception {
		
		request.setAttribute(PAGE, PGrade);
		request.setAttribute(TITLE, TGrade);
		viewGrade();			
		return loadGrade(); 
	}
	
	
	public String loadGrade(){	
		
		levelList = new FillLevel(request).fillLevel();
		request.setAttribute("levelList", levelList);
		int levelId, i=0;
		String levelName;
		
		StringBuilder sbLevelList = new StringBuilder();
		sbLevelList.append("{");
		for(i=0; i<levelList.size()-1;i++ ) {
    		levelId = Integer.parseInt((levelList.get(i)).getLevelId());
    		levelName = levelList.get(i).getLevelCodeName();
    		sbLevelList.append("\""+ levelId+"\":\""+levelName+"\",");
		}
		
		levelId = Integer.parseInt((levelList.get(i)).getLevelId());
		levelName = levelList.get(i).getLevelCodeName();
		sbLevelList.append("\""+ levelId+"\":\""+levelName+"\"");	//no comma for last record
		sbLevelList.append("}");
		request.setAttribute("sbLevelList", sbLevelList.toString());
		
		
		desigList = new FillDesig(request).fillDesig();
		request.setAttribute("desigList", desigList);
		int desigId;
		String desigName;
		
		StringBuilder sbDesigList = new StringBuilder();
		sbDesigList.append("{");
		for(i=0; i<desigList.size()-1;i++ ) {
    		desigId = Integer.parseInt((desigList.get(i)).getDesigId());
    		desigName = desigList.get(i).getDesigCodeName();
    		sbDesigList.append("\""+ desigId+"\":\""+desigName+"\",");
		}
		desigId = Integer.parseInt((desigList.get(i)).getDesigId());
		desigName = desigList.get(i).getDesigCodeName();
		sbDesigList.append("\""+ desigId+"\":\""+desigName+"\"");	//no comma for last record
		sbDesigList.append("}");
		request.setAttribute("sbDesigList", sbDesigList.toString());
		
		return "load";
	}
	
	public String viewGrade(){
		
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs= null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {

			List<List<String>> al = new ArrayList<List<String>>();
			List<String> alInner = new ArrayList<String>();
			
			con = db.makeConnection(con);
			pst = con.prepareStatement(selectGrade);
			rs = pst.executeQuery();
			while(rs.next()){
				alInner = new ArrayList<String>();
				alInner.add(rs.getInt("grade_id")+"");
				alInner.add(rs.getString("grade_code"));
				alInner.add(rs.getString("grade_name"));
				alInner.add(uF.showData(rs.getString("grade_description"),""));
				alInner.add("["+uF.showData(rs.getString("designation_code"),"not selected")+"] "+uF.showData(rs.getString("designation_name"),""));
				alInner.add("["+uF.showData(rs.getString("level_code"), "not selected")+"] "+uF.showData(rs.getString("level_name"),""));
				al.add(alInner);
			}
			rs.close();
			pst.close();
			
			request.setAttribute("reportList", al);
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);			
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;
		
	}
	
	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

}
