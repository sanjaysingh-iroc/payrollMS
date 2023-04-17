package com.konnect.jpms.employee;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class AddKRA extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	String strSessionEmpId = null;
	CommonFunctions CF = null;
	
	public String execute() {
		  
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if (CF==null) {
			return LOGIN;
		}
		
		strSessionEmpId = (String)session.getAttribute(EMPID);
		
		if(getKraSubmit()!=null) {

			//updateKRA();
			if(getType()!=null && getType().equals("type")) {
				return UPDATE;
			}else{
				return SUCCESS;
			}
		}
		getKRA();
		return LOAD;
	}
	
	String strCurrentEffectiveDate;
	String strCurrentIds;
	String strEffectiveDate;
	String kraSubmit;
	String empId;
	String orgId;
	String levelId;
	String gradeId;
	String[] goalElements;
	String[] cgoalAlignAttribute;
	String[] empKra;
	String[] empKraTask;
	String[] empKraId;
	String[] empKraTaskId;
	String[] desigKraId;
	String[] status;
	String type;
	String KRAType;
	
	
	public void getKRA(){
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();
		try {
			
			con = db.makeConnection(con);
			
			
//			PreparedStatement pst = con.prepareStatement("select * from emp_kras where emp_id = ? and effective_date = (select max(effective_date) from emp_kras where emp_id = ?)");
			pst = con.prepareStatement("select * from goal_kras where emp_ids like '%,"+getEmpId()+",%' and goal_type = "+EMPLOYEE_KRA+" and is_assign = true and effective_date = (select max(effective_date) from goal_kras where emp_ids like '%,"+getEmpId()+",%' and goal_type = "+EMPLOYEE_KRA+")");
			rs = pst.executeQuery();

			List<String> alKRAs = new ArrayList<String>();
			StringBuilder sbCurrentKRAs = new StringBuilder();
			while(rs.next()){
				alKRAs.add(rs.getString("goal_kra_id"));
				alKRAs.add(rs.getString("kra_description"));
				setStrCurrentEffectiveDate(uF.getDateFormat(rs.getString("effective_date"), DBDATE, DATE_FORMAT));
				setStrEffectiveDate(uF.getDateFormat(rs.getString("effective_date"), DBDATE, DATE_FORMAT));
				sbCurrentKRAs.append(rs.getString("goal_kra_id")+",");
			}
			rs.close();
			pst.close();
			setStrCurrentIds(sbCurrentKRAs.toString());
			
			request.setAttribute("alKRAs", alKRAs);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	
	
	public void insertKRA() {
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst =null;
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			
			
			int empGoalId = 0;
			pst = con.prepareStatement("select goal_id from goal_details where goal_type = "+EMPLOYEE_KRA+" and emp_ids like ',%"+getEmpId()+"%,' ");
			rs = pst.executeQuery();
			while (rs.next()) {
				empGoalId = rs.getInt("goal_id");
			}
			rs.close();
			pst.close();
			
			if(empGoalId == 0 && getEmpKra() != null && getEmpKra().length > 0) {
				pst = con.prepareStatement("insert into goal_details(goal_type,goal_parent_id,goal_title,goal_attribute,measure_type,weightage,emp_ids," +
					"level_id,grade_id,is_measure_kra,measure_kra,entry_date,user_id,priority,goal_creater_id,goal_element,org_id,effective_date,goal_objective)" +
					"values(?,?,?,? ,?,?,?,? ,?,?,?,? ,?,?,?,? ,?,?,?)");
				pst.setInt(1, EMPLOYEE_KRA);
				pst.setInt(2, 0);
				pst.setString(3, "Designation Based KRA");
				pst.setInt(4, uF.parseToInt(getGoalElements()[0]));
				pst.setString(5, "");
				pst.setDouble(6, 100);
				pst.setString(7, ","+getEmpId()+",");
				pst.setString(8, getLevelId());
				pst.setString(9, getGradeId());
				pst.setBoolean(10, false);
				pst.setString(11, "KRA");
				pst.setDate(12, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setInt(13, uF.parseToInt(strSessionEmpId));
				pst.setInt(14, 3);
				pst.setInt(15, uF.parseToInt(strSessionEmpId));
				pst.setInt(16, uF.parseToInt(getCgoalAlignAttribute()[0]));
				pst.setInt(17, uF.parseToInt(getOrgId()));
				pst.setDate(18, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setString(19, "Designation Based KRA");
				pst.execute();
				pst.close();
				
				pst = con.prepareStatement("select max(goal_id) from goal_details");
				rs = pst.executeQuery();
				while (rs.next()) {
					empGoalId = rs.getInt(1);
				}
				rs.close();
				pst.close();
			}
		
			
			
			StringBuilder sbKRAIds = null;
			StringBuilder sbQue = new StringBuilder();
			sbQue.append("select * from goal_kras where emp_ids like '%,"+getEmpId()+",%' and goal_type = "+EMPLOYEE_KRA+" ");
			if(getKRAType() != null && getKRAType().equals("DESIG")) {
				sbQue.append(" and desig_kra_id > 0 ");
			} else {
				sbQue.append(" and desig_kra_id = 0 ");
			}
			sbQue.append(" order by goal_kra_id");
			pst = con.prepareStatement(sbQue.toString());
			rs = pst.executeQuery();
//			System.out.println("getEmpKraId() ===>> " + getEmpKraId());
			List<String> goalKraIdList = new ArrayList<String>();
			if(getEmpKraId() != null) {
				goalKraIdList = Arrays.asList(getEmpKraId());
			}
//			System.out.println("goalKraIdList ===>> " + goalKraIdList);
			while(rs.next()) {
				if(!goalKraIdList.contains(rs.getString("goal_kra_id"))) {
					if(sbKRAIds == null) {
						sbKRAIds = new StringBuilder();
						sbKRAIds.append(rs.getString("goal_kra_id"));
					} else {
						sbKRAIds.append(","+rs.getString("goal_kra_id"));
					}
				}
			}
			rs.close();
			pst.close();
			
//			for(int i=0; goalKraIdList != null && !goalKraIdList.isEmpty() && i<goalKraIdList.size(); i++) {
			if(sbKRAIds != null && sbKRAIds.length() > 0) {
				pst = con.prepareStatement("delete from goal_kras where goal_kra_id in ("+sbKRAIds.toString()+")");
				pst.executeUpdate();
				pst.close();
				
				pst = con.prepareStatement("delete from goal_kra_tasks where kra_id in ("+sbKRAIds.toString()+")");
				pst.executeUpdate();
				pst.close();
			}
//			}
			
			
			for(int i=0; getEmpKraId()!=null && i<getEmpKraId().length; i++){
				if(getEmpKraId()[i]!=null && getEmpKraId()[i].length()>0){
					
					pst = con.prepareStatement("update goal_kras set update_date=?,is_approved=?,approved_by=?,kra_order=?,kra_description=?," +
							"goal_type=?,element_id=?,attribute_id=?,emp_ids=?,is_assign=?,desig_kra_id=?,updated_by=?,goal_id=? where goal_kra_id = ? ");
					pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setBoolean(2, true);
					pst.setInt(3, uF.parseToInt(strSessionEmpId));
					pst.setInt(4, 0);
					pst.setString(5, getEmpKra()[i]);
					pst.setInt(6, EMPLOYEE_KRA);
					pst.setInt(7, uF.parseToInt(getGoalElements()[i]));
					pst.setInt(8, uF.parseToInt(getCgoalAlignAttribute()[i]));
					pst.setString(9, ","+getEmpId()+",");
					pst.setBoolean(10, uF.parseToBoolean(getStatus() != null ? getStatus()[i] : "1"));
					pst.setInt(11, uF.parseToInt(getDesigKraId() != null ? getDesigKraId()[i] : "0"));
					pst.setInt(12, uF.parseToInt(strSessionEmpId));
					pst.setInt(13, empGoalId);
					pst.setInt(14, uF.parseToInt(getEmpKraId()[i]));
					pst.execute();
					pst.close();
					if(getEmpKraTaskId()!=null && getEmpKraTaskId()[i]!=null && getEmpKraTaskId()[i].length()>0 && uF.parseToInt(getEmpKraTaskId()[i])>0){
						pst = con.prepareStatement("update goal_kra_tasks set goal_id=?, kra_id=?, task_name=?, emp_ids=?, update_date=?, updated_by=? where " +
								"goal_kra_task_id =?");
						pst.setInt(1, empGoalId);
						pst.setInt(2, uF.parseToInt(getEmpKraId()[i]));
						pst.setString(3, getEmpKraTask()[i]);
						pst.setString(4, ","+getEmpId()+",");
						pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
						pst.setInt(6, uF.parseToInt(strSessionEmpId));
						pst.setInt(7, uF.parseToInt(getEmpKraTaskId()[i]));
						pst.execute();
						pst.close();
					} else {
						pst = con.prepareStatement("insert into goal_kra_tasks(goal_id, kra_id, task_name, emp_ids, entry_date, added_by)"
								+ "values(?,?,?,?, ?,?)");
						pst.setInt(1, empGoalId);
						pst.setInt(2, uF.parseToInt(getEmpKraId()[i]));
						pst.setString(3, getEmpKraTask()[i]);
						pst.setString(4, ","+getEmpId()+",");
						pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
						pst.setInt(6, uF.parseToInt(strSessionEmpId));
						pst.execute();
						pst.close();
					}
			}
		}
			
			int cnt = getEmpKraId() != null ? getEmpKraId().length : 0;
//			System.out.println("cnt ===>> " + cnt);
//			System.out.println("getEmpKra ===>> " + getEmpKra()!=null ? getEmpKra().length : "0");
			
			for(int i=cnt; getEmpKra()!=null && i<getEmpKra().length; i++) {
//				System.out.println("getEmpKra()[i] ===>> " + getEmpKra()[i]);
				if(getEmpKra()[i]!=null && getEmpKra()[i].length()>0) {
//					System.out.println("getEmpKra()[i] in if ===>> " + getEmpKra()[i]);
					
					pst = con.prepareStatement("insert into goal_kras(goal_id,entry_date,effective_date,is_approved,approved_by,kra_order," +
							"kra_description,goal_type,element_id,attribute_id,emp_ids,is_assign,desig_kra_id,added_by)"
									+ "values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?)");
					pst.setInt(1, empGoalId);
					pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
					if(cnt > 0) {
						pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
					} else {
						pst.setDate(3, uF.getDateFormat(getStrEffectiveDate(), DATE_FORMAT));
					}
					pst.setBoolean(4, true);
					pst.setInt(5, uF.parseToInt(strSessionEmpId));
					pst.setInt(6, 0);
					pst.setString(7, getEmpKra()[i]);
					pst.setInt(8, EMPLOYEE_KRA);
					pst.setInt(9, uF.parseToInt(getGoalElements()[i]));
					pst.setInt(10, uF.parseToInt(getCgoalAlignAttribute()[i]));
					pst.setString(11, ","+getEmpId()+",");
					pst.setBoolean(12, uF.parseToBoolean(getStatus() != null ? getStatus()[i] : "1"));
					pst.setInt(13, uF.parseToInt(getDesigKraId() != null ? getDesigKraId()[i] : "0"));
					pst.setInt(14, uF.parseToInt(strSessionEmpId));
//					System.out.println("pst ===>> " + pst);
					pst.execute();
//					System.out.println("pst after ===>> " + pst);
					pst.close();
					
					int empKRAId = 0;
					pst = con.prepareStatement("select max(goal_kra_id) from goal_kras");
					rs = pst.executeQuery();
					while (rs.next()) {
						empKRAId = rs.getInt(1);
					}
					rs.close();
					pst.close();
					
					if(getEmpKraTask()!=null && getEmpKraTask()[i]!=null && !getEmpKraTask()[i].trim().equals("") && !getEmpKraTask()[i].trim().equalsIgnoreCase("NULL")) {
						pst = con.prepareStatement("insert into goal_kra_tasks(goal_id, kra_id, task_name, emp_ids, entry_date, added_by)"
								+ "values(?,?,?,?, ?,?)");
						pst.setInt(1, empGoalId);
						pst.setInt(2, empKRAId);
						pst.setString(3, getEmpKraTask()[i]);
						pst.setString(4, ","+getEmpId()+",");
						pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
						pst.setInt(6, uF.parseToInt(strSessionEmpId));
						pst.execute();
						pst.close();
					}
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
	
	
	
	HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}
	public String getEmpId() {
		return empId;
	}
	public void setEmpId(String empId) {
		this.empId = empId;
	}
	public String[] getEmpKra() {
		return empKra;
	}
	public void setEmpKra(String[] empKra) {
		this.empKra = empKra;
	}
	public String[] getEmpKraId() {
		return empKraId;
	}
	public void setEmpKraId(String[] empKraId) {
		this.empKraId = empKraId;
	}

	public String getKraSubmit() {
		return kraSubmit;
	}

	public void setKraSubmit(String kraSubmit) {
		this.kraSubmit = kraSubmit;
	}

	public String getStrEffectiveDate() {
		return strEffectiveDate;
	}

	public void setStrEffectiveDate(String strEffectiveDate) {
		this.strEffectiveDate = strEffectiveDate;
	}

	public String getStrCurrentIds() {
		return strCurrentIds;
	}

	public void setStrCurrentIds(String strCurrentIds) {
		this.strCurrentIds = strCurrentIds;
	}

	public String getStrCurrentEffectiveDate() {
		return strCurrentEffectiveDate;
	}

	public void setStrCurrentEffectiveDate(String strCurrentEffectiveDate) {
		this.strCurrentEffectiveDate = strCurrentEffectiveDate;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

	public String getLevelId() {
		return levelId;
	}

	public void setLevelId(String levelId) {
		this.levelId = levelId;
	}

	public String[] getGoalElements() {
		return goalElements;
	}

	public void setGoalElements(String[] goalElements) {
		this.goalElements = goalElements;
	}

	public String[] getCgoalAlignAttribute() {
		return cgoalAlignAttribute;
	}

	public void setCgoalAlignAttribute(String[] cgoalAlignAttribute) {
		this.cgoalAlignAttribute = cgoalAlignAttribute;
	}

	public String[] getDesigKraId() {
		return desigKraId;
	}

	public void setDesigKraId(String[] desigKraId) {
		this.desigKraId = desigKraId;
	}

	public String[] getStatus() {
		return status;
	}

	public void setStatus(String[] status) {
		this.status = status;
	}

	public String getGradeId() {
		return gradeId;
	}

	public void setGradeId(String gradeId) {
		this.gradeId = gradeId;
	}

	public String[] getEmpKraTask() {
		return empKraTask;
	}

	public void setEmpKraTask(String[] empKraTask) {
		this.empKraTask = empKraTask;
	}

	public String[] getEmpKraTaskId() {
		return empKraTaskId;
	}

	public void setEmpKraTaskId(String[] empKraTaskId) {
		this.empKraTaskId = empKraTaskId;
	}

	public String getKRAType() {
		return KRAType;
	}

	public void setKRAType(String kRAType) {
		KRAType = kRAType;
	}

	public void setCF(CommonFunctions CF) {
		this.CF = CF;
	}
	
	public void setStrSessionEmpId(String strSessionEmpId) {
		this.strSessionEmpId = strSessionEmpId;
	}
}
