package com.konnect.jpms.master;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.performance.FillAttribute;
import com.konnect.jpms.recruitment.FillEducational;
import com.konnect.jpms.select.FillGender;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillSkills;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;
 
public class AddDesig extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;

	HttpSession session;
	
	String strSessionEmpId = null;
	List<FillGender> genderList;
	List<FillSkills> skillsList;
	List<FillEducational> educationalList;
	
	String education;
	String totalexpYear;
	String totalexpMonth;
	String relevantYear;
	String relevantMonth;
	String expusYear;
	String expusMonth;
	String skill;
	String strGender;
	String strMinAge;
	String strMinCTC;
	String strMaxCTC;
	
	String jobDescription;
	String profile;
	
	String idealcandidate;
	String fromPage;
	String empId;
	
	String[] designKRA;
	String[] desigKraId;
	String[] goalElements;
	String[] elementAttribute;
	String[] designKRATask;
	
	List<String> skillsID = new ArrayList<String>();
	List<String> educationID = new ArrayList<String>();
	List<FillAttribute> attributeList;
	
	CommonFunctions CF;
	
	String userscreen;
	String navigationId;
	String toPage;
	
	String noOfGrades;
	
	public String execute() throws Exception {

		request.setAttribute(PAGE, PAddDesignation);
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null){
			return LOGIN;
		}
		
		strSessionEmpId = (String)session.getAttribute(EMPID);
		
		request.setAttribute("salaryStructure", CF.getStrSalaryStructure());
		
		String operation = request.getParameter("operation");
		String strId = request.getParameter("ID");
		
		genderList=new FillGender().fillGender();
		skillsList = new FillSkills(request).fillSkillsWithId();
		educationalList = new FillEducational(request).fillEducationalQual();
		
		loadValidateDesig();
		getAttributeDetails();
		
		if (operation!=null && operation.equals("D")) {
			return deleteDesig(strId);
		}
		if (getDesigId()!=null && getDesigId().length()>0) { 
				return updateDesig();
		}
		if (operation!=null && operation.equals("E")) {
			
			return viewDesig(strId);
		}
		 
		if(getDesigCode()!=null && getDesigCode().length()>0) {
			return insertDesig();
		}
		return LOAD;
		
	}

	private void getAttributeDetails() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			
			String sb = CF.getElementDetails(con, request, null);
//			System.out.println("sb =========>> " + sb);
			
//			request.setAttribute("elementOptions", sb);
			
			StringBuilder sbelements = new StringBuilder();
			
			sbelements.append("<span><select name=goalElements id=goalElements style='width:130px !important;' >"+
		    "<option value= >Select Element</option>"+sb+"</select>");
			request.setAttribute("elementSelectBox", sbelements.toString());
		    
		    attributeList = new ArrayList<FillAttribute>();
			
			pst = con.prepareStatement("select * from attribute_details");
			rs = pst.executeQuery();
			List<List<String>> outerList=new ArrayList<List<String>>();
			while(rs.next()) {
				List<String> innerList=new ArrayList<String>();
				innerList.add(rs.getString("attribute_id"));
				innerList.add(rs.getString("attribute_name"));
				innerList.add(rs.getString("description"));
				innerList.add(rs.getString("weightage"));
				outerList.add(innerList);
			}
			rs.close();
			pst.close();
			
			request.setAttribute("attributeList",outerList);
			//System.out.println("attributeList=====>"+outerList);
			
		} catch (Exception e) {
			e.printStackTrace();
			
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}
	
	
//	private String getElementDetails(Connection con,  String elementId) {
//
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		StringBuilder sb = new StringBuilder();
//		try {
//			
//			pst = con.prepareStatement("select * from appraisal_element order by appraisal_element_id");
//			rs = pst.executeQuery();
//			while (rs.next()) {
//				if(elementId != null && rs.getString("appraisal_element_id").equals(elementId)) {
//					sb.append("<option value=" + rs.getString("appraisal_element_id") + " selected >"+ rs.getString("appraisal_element_name") + "</option>");
//				} else {
//					sb.append("<option value=" + rs.getString("appraisal_element_id") + ">"+ rs.getString("appraisal_element_name") + "</option>");
//				}
//			}
//			request.setAttribute("elementOptions", sb.toString());
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		} 
//		return sb.toString();
//	}
	

	String orgId;
	List<FillLevel> levelList;
	public String loadValidateDesig() {
		
		request.setAttribute(PAGE, PAddDesignation);
		
		levelList = new FillLevel(request).fillLevel();
		
		if(levelList.size()!=0) {
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
			sbLevelList.append("\""+ levelId+"\":\""+levelName+"\"");	
			sbLevelList.append("}");
			request.setAttribute("sbLevelList", sbLevelList.toString());
		}
		
		return LOAD;
	}

	public String insertDesig() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		try {
			con = db.makeConnection(con);
			//pst = con.prepareStatement(insertDesig);
			pst = con.prepareStatement("INSERT INTO designation_details (designation_code, designation_name, " +
					"designation_description, level_id, attribute_ids, ideal_candidate, profile) VALUES (?,?,?,?, ?,?,?)");
			pst.setString(1, getDesigCode());
			pst.setString(2, getDesigName());
			pst.setString(3, uF.showData(getDesigDesc(),""));
			pst.setInt(4, uF.parseToInt(getDesiglevel()));
			pst.setString(5, getAttributeid());
			pst.setString(6, getIdealcandidate());
			pst.setString(7, getProfile());
			int x= pst.executeUpdate();
			pst.close();
			
			pst = con.prepareStatement("select max(designation_id)as desigid from designation_details");
			rs = pst.executeQuery();
			String desigid = null;
			while(rs.next()){
				desigid = rs.getString("desigid");
			}
			rs.close();
			pst.close();
			
			
			int nSalaryStrucuterType = uF.parseToInt(CF.getStrSalaryStructure());
			if(x > 0 && uF.parseToInt(getNoOfGrades()) > 0 && nSalaryStrucuterType == S_GRADE_WISE){
				for(int i=0;i < uF.parseToInt(getNoOfGrades()); i++){
					pst = con.prepareStatement("INSERT INTO grades_details (grade_code, grade_name, grade_description, designation_id,is_fitment,weightage) " +
							"VALUES (?,?,?,?, ?,?)");
					pst.setString(1, getDesigCode()+"-1."+(i+1));
					pst.setString(2, getDesigName()+"-1."+(i+1));
					pst.setString(3, null);
					pst.setInt(4, uF.parseToInt(desigid));
					pst.setBoolean(5, true);
					pst.setInt(6, (uF.parseToInt(getNoOfGrades())-i));
					pst.execute();
					pst.close();
				}
			} else {
				pst = con.prepareStatement(insertGrade);
				pst.setString(1, getDesigCode());
				pst.setString(2, getDesigName());
				pst.setString(3, null);
				pst.setInt(4, uF.parseToInt(desigid));
				pst.execute();
				pst.close();
			}
			
			if(getDesignKRA()!=null && getDesignKRA().length > 0) {
				for(int i=0; i<getDesignKRA().length; i++) {
					if(getDesignKRA()[i].length() != 0) { 
						pst = con.prepareStatement("INSERT INTO designation_kra_details(designation_id, kra_name, element_id, attribute_id, added_by, " +
							"entry_date, task_name) VALUES (?,?,?,?, ?,?,?)");
						pst.setInt(1, uF.parseToInt(getDesigId()));
						pst.setString(2, getDesignKRA()[i]);
						pst.setInt(3, uF.parseToInt(getGoalElements()[i]));
						pst.setInt(4, uF.parseToInt(getElementAttribute()[i]));
						pst.setInt(5, uF.parseToInt(strSessionEmpId));
						pst.setDate(6, uF.getCurrentDate(CF.getStrTimeZone()));
						pst.setString(7, getDesignKRATask()[i]);
						pst.execute();
						pst.close();
					}
				}
			}
			
			
			if(getAttributeid()!=null && !getAttributeid().equals("")) {
				List<String> attList=Arrays.asList(getAttributeid().split(","));
				for(int i=0;attList!=null && !attList.isEmpty() && i<attList.size();i++) {
					String id = attList.get(i).trim();
					String desig_value = null;
					String value_type = "";
					boolean flag = false;
					if(id.equals("1")) {
						desig_value = getEducation();
						value_type= ",";
						flag=true;
					} else if(id.equals("2")) {
						desig_value = getTotalexpYear()+"."+getTotalexpMonth();
						value_type = ",";
						flag=true;
					} else if(id.equals("3")) {
						desig_value = getRelevantYear()+"."+getRelevantMonth();
						flag=true;
					} else if(id.equals("4")) {
						desig_value = getExpusYear()+"."+getExpusMonth();
						flag=true;
					} else if(id.equals("5")) {
						desig_value = getSkill();
						value_type = ",";
						flag=true;
					} else if(id.equals("6")) {
						desig_value = getStrGender();
						flag=true;
					} else if(id.equals("7")) {
						desig_value = getStrMinAge();
						flag=true;
					} else if(id.equals("8")) {
						desig_value = uF.showData(getStrMinCTC(), "0")+"-"+uF.showData(getStrMaxCTC(), "0");
						flag=true;
					}
					
					if(flag) {
						pst = con.prepareStatement("insert into desig_attribute(desig_id, desig_value, _type, value_type)values(?,?,?,?)");
						pst.setInt(1, uF.parseToInt(desigid));
						pst.setString(2, desig_value);
						pst.setInt(3, uF.parseToInt(id));
						pst.setString(4, value_type);
						pst.execute();
						pst.close();
						
						/*if(id.equals("5")) {
							List<String> skillList=Arrays.asList(getSkill().split(","));
							for(int j=0;skillList!=null && !skillList.isEmpty() && j<skillList.size();j++) {
								String skill=skillList.get(j).trim();
								pst = con.prepareStatement("select * from skills_details where upper(skill_name) like ?");
								pst.setString(1, skill.toUpperCase());
								rs=pst.executeQuery();
								boolean flg=false;
								while(rs.next()) {
									flg=true;
								}
								if(!flg) {
									pst=con.prepareStatement("insert into skills_details(skill_name,org_id)values(?,?)");
									pst.setString(1, skill);
									pst.setInt(2, uF.parseToInt(orgid));
									pst.execute();										
								}
							}
						} else if(id.equals("1")) {
							List<String> eduList=Arrays.asList(getEducation().split(","));
							for(int j=0;eduList!=null && !eduList.isEmpty() && j<eduList.size();j++) {
								String education=eduList.get(j).trim();
								pst = con.prepareStatement("select * from educational_details where upper(education_name) like ?");
								pst.setString(1, education.toUpperCase());
								rs=pst.executeQuery();
								boolean flg=false;
								while(rs.next()) {
									flg=true;
								}
								if(!flg) {
									pst=con.prepareStatement("insert into educational_details(education_name,org_id)values(?,?)");
									pst.setString(1, education);
									pst.setInt(2, uF.parseToInt(orgid));
									pst.execute();										
								}
							}
						}*/
					}
				}
			}
			
			session.setAttribute(MESSAGE, SUCCESSM+getDesigCode()+" saved successfully."+END);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}
	
	public String updateDesig() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs=null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		String updateDesig = "UPDATE designation_details SET designation_code =?, designation_name=?, designation_description=?," +
				"attribute_ids=?, ideal_candidate=?, profile=? WHERE designation_id=?";
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement(updateDesig);
			pst.setString(1, getDesigCode());
			pst.setString(2, getDesigName());
			pst.setString(3, getDesigDesc());
			pst.setString(4, getAttributeid());
			pst.setString(5, getIdealcandidate());
			pst.setString(6, getProfile());
			pst.setInt(7, uF.parseToInt(getDesigId()));			
			int y= pst.executeUpdate();
			pst.close();
			
			int nSalaryStrucuterType = uF.parseToInt(CF.getStrSalaryStructure());
			if(y > 0 && uF.parseToInt(getNoOfGrades()) > 0 && nSalaryStrucuterType == S_GRADE_WISE){
				
				pst = con.prepareStatement("select count(grade_id) as cnt from grades_details gd, designation_details dd where " +
						"gd.designation_id = dd.designation_id and dd.level_id in (select ld.level_id from level_details ld) and dd.designation_id=?");
				pst.setInt(1, uF.parseToInt(getDesigId()));
				rs = pst.executeQuery();
				int nGradeCnt = 0;
				while(rs.next()){
					nGradeCnt = uF.parseToInt(rs.getString("cnt"));
				}
				rs.close();
				pst.close();
				
				if(nGradeCnt == 0){
					for(int i=0;i < uF.parseToInt(getNoOfGrades()); i++){
						pst = con.prepareStatement("INSERT INTO grades_details (grade_code, grade_name, grade_description, designation_id,is_fitment,weightage) " +
								"VALUES (?,?,?,?, ?,?)");
						pst.setString(1, getDesigCode()+"-1."+(i+1));
						pst.setString(2, getDesigName()+"-1."+(i+1));
						pst.setString(3, null);
						pst.setInt(4, uF.parseToInt(getDesigId()));
						pst.setBoolean(5, true);
						pst.setInt(6, (uF.parseToInt(getNoOfGrades())-i));
						pst.execute();
						pst.close();
					}
				}
			}
			
			pst = con.prepareStatement("select * from designation_kra_details where designation_id=? order by designation_kra_id");
			pst.setInt(1, uF.parseToInt(getDesigId()));
			rs = pst.executeQuery();
			List<String> desigKraIdList = new ArrayList<String>();
			while(rs.next()){
				desigKraIdList.add(rs.getString("designation_kra_id"));
			}
			rs.close();
			pst.close();
			
//			System.out.println("desigKraIdList ===>> " + desigKraIdList);
			
			if(getDesigKraId()!=null && getDesigKraId().length > 0) {
				for(int i=0; i<getDesigKraId().length; i++) {
					if(desigKraIdList.contains(getDesigKraId()[i])) {
						desigKraIdList.remove(getDesigKraId()[i]);
					}
				}
			}
			
//			System.out.println("desigKraIdList 1 ===>> " + desigKraIdList);
			
			for(int i=0; desigKraIdList != null && !desigKraIdList.isEmpty() && i<desigKraIdList.size(); i++) {
				pst = con.prepareStatement("delete from designation_kra_details where designation_kra_id = ?");
				pst.setInt(1, uF.parseToInt(desigKraIdList.get(i)));
				pst.executeUpdate();
				pst.close();
			}
			
			if(getDesigKraId()!=null && getDesigKraId().length > 0) {
				for(int i=0; i<getDesigKraId().length; i++) {
					pst = con.prepareStatement("update designation_kra_details set kra_name=?, element_id=?, attribute_id=?, updated_by=?, " +
						"update_date=?, task_name=? where designation_kra_id = ?");
					pst.setString(1, getDesignKRA()[i]);
					pst.setInt(2, uF.parseToInt(getGoalElements()[i]));
					pst.setInt(3, uF.parseToInt(getElementAttribute()[i]));
					pst.setInt(4, uF.parseToInt(strSessionEmpId));
					pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setString(6, getDesignKRATask()[i]);
					pst.setInt(7, uF.parseToInt(getDesigKraId()[i]));
					pst.execute();
					pst.close();
					
					pst = con.prepareStatement("update goal_kras set kra_description=?, element_id=?, attribute_id=? where desig_kra_id=?");
					pst.setString(1, getDesignKRA()[i]);
					pst.setInt(2, uF.parseToInt(getGoalElements()[i]));
					pst.setInt(3, uF.parseToInt(getElementAttribute()[i]));
					pst.setInt(4, uF.parseToInt(getDesigKraId()[i]));
					pst.execute();	
					pst.close();				
				}
			}
			
			if(getDesignKRA()!=null && getDesignKRA().length > 0) {
				int cnt = getDesigKraId() != null ? getDesigKraId().length : 0;
				for(int i=cnt; i<getDesignKRA().length; i++) {
					if(getDesignKRA()[i].length() != 0) { 
						pst = con.prepareStatement("INSERT INTO designation_kra_details (designation_id, kra_name, element_id, attribute_id, " +
							"added_by, entry_date, task_name) VALUES (?,?,?,?, ?,?,?)");
						pst.setInt(1, uF.parseToInt(getDesigId()));
						pst.setString(2, getDesignKRA()[i]);
						pst.setInt(3, uF.parseToInt(getGoalElements()[i]));
						pst.setInt(4, uF.parseToInt(getElementAttribute()[i]));
						pst.setInt(5, uF.parseToInt(strSessionEmpId));
						pst.setDate(6, uF.getCurrentDate(CF.getStrTimeZone()));
						pst.setString(7, getDesignKRATask()[i]);
						pst.execute();
						pst.close();
					}
				}
			}
			
			
//			System.out.println("getAttributeid()=====>"+getAttributeid());
			StringBuilder sbAttribId = null;
				if(getAttributeid()!=null && !getAttributeid().equals("")) {
					List<String> attList=Arrays.asList(getAttributeid().split(","));
					for(int i=0;attList!=null && !attList.isEmpty() && i<attList.size();i++) {
						String id=attList.get(i).trim();
						String desig_value = null;
						String value_type = "";
						boolean flag = false;
						if(id.equals("1")){
							desig_value = getEducation();
							value_type = ",";
							flag=true;
							if(sbAttribId == null) {
								sbAttribId = new StringBuilder();
								sbAttribId.append(id);
							} else {
								sbAttribId.append(","+id);
							}
						}else if(id.equals("2")){
							desig_value = getTotalexpYear()+"."+getTotalexpMonth();
							value_type = ",";
							flag=true;
							if(sbAttribId == null) {
								sbAttribId = new StringBuilder();
								sbAttribId.append(id);
							} else {
								sbAttribId.append(","+id);
							}
						}else if(id.equals("3")){
							desig_value = getRelevantYear()+"."+getRelevantMonth();
							flag=true;
							if(sbAttribId == null) {
								sbAttribId = new StringBuilder();
								sbAttribId.append(id);
							} else {
								sbAttribId.append(","+id);
							}
						}else if(id.equals("4")){
							desig_value = getExpusYear()+"."+getExpusMonth();
							flag=true;
							if(sbAttribId == null) {
								sbAttribId = new StringBuilder();
								sbAttribId.append(id);
							} else {
								sbAttribId.append(","+id);
							}
						}else if(id.equals("5")){
							desig_value = getSkill();
							value_type=",";
							flag=true;
							if(sbAttribId == null) {
								sbAttribId = new StringBuilder();
								sbAttribId.append(id);
							} else {
								sbAttribId.append(","+id);
							}
						}else if(id.equals("6")){
							desig_value = getStrGender();
							flag=true;
							if(sbAttribId == null) {
								sbAttribId = new StringBuilder();
								sbAttribId.append(id);
							} else {
								sbAttribId.append(","+id);
							}
						} else if(id.equals("7")) {
							desig_value = getStrMinAge();
							flag=true;
							if(sbAttribId == null) {
								sbAttribId = new StringBuilder();
								sbAttribId.append(id);
							} else {
								sbAttribId.append(","+id);
							}
						} else if(id.equals("8")) {
							desig_value = uF.showData(getStrMinCTC(), "0")+"-"+uF.showData(getStrMaxCTC(), "0");
							flag=true;
							if(sbAttribId == null) {
								sbAttribId = new StringBuilder();
								sbAttribId.append(id);
							} else {
								sbAttribId.append(","+id);
							}
						}
						
						if(flag) {
							pst=con.prepareStatement("update desig_attribute set desig_value = ?,value_type = ? where desig_id = ? and _type = ?");
							pst.setString(1, desig_value);
							pst.setString(2, value_type);
							pst.setInt(3, uF.parseToInt(getDesigId()));
							pst.setInt(4, uF.parseToInt(id));
							int x=pst.executeUpdate();
							pst.close();
							
							if(x==0) {
								pst=con.prepareStatement("insert into desig_attribute(desig_id,desig_value,_type,value_type)values(?,?,? ,?)");
								pst.setInt(1, uF.parseToInt(getDesigId()));
								pst.setString(2, desig_value);
								pst.setInt(3, uF.parseToInt(id));
								pst.setString(4, value_type);
								pst.execute();
								pst.close();
							}
							
							/*if(id.equals("5")){
								List<String> skillList=Arrays.asList(getSkill().split(","));
								for(int j=0;skillList!=null && !skillList.isEmpty() && j<skillList.size();j++){
									String skill=skillList.get(j).trim();
									pst = con.prepareStatement("select * from skills_details where upper(skill_name) like ?");
									pst.setString(1, skill.toUpperCase());
									rs=pst.executeQuery();
									boolean flg=false;
									while(rs.next()){
										flg=true;
									}
									if(!flg){
										pst=con.prepareStatement("insert into skills_details(skill_name,org_id)values(?,?)");
										pst.setString(1, skill);
										pst.setInt(2, uF.parseToInt(orgid));
										pst.execute();										
									}
								}
							}else if(id.equals("1")){
								List<String> eduList=Arrays.asList(getEducation().split(","));
								for(int j=0;eduList!=null && !eduList.isEmpty() && j<eduList.size();j++){
									String education=eduList.get(j).trim();
									pst = con.prepareStatement("select * from educational_details where upper(education_name) like ?");
									pst.setString(1, education.toUpperCase());
									rs=pst.executeQuery();
									boolean flg=false;
									while(rs.next()){
										flg=true;
									}
									if(!flg){
										pst=con.prepareStatement("insert into educational_details(education_name,org_id)values(?,?)");
										pst.setString(1, education);
										pst.setInt(2, uF.parseToInt(orgid));
										pst.execute();										
									}
								}
							}*/
							
						}
					}

					StringBuilder sbQuery = new StringBuilder();
					sbQuery.append("delete from desig_attribute where desig_id = ?");
					if(sbAttribId !=null) {
						sbQuery.append(" and _type not in ("+sbAttribId.toString()+")");
					}
					pst=con.prepareStatement(sbQuery.toString());
					pst.setInt(1, uF.parseToInt(getDesigId()));
					System.out.println("pst =====>> " + pst);
					pst.executeUpdate();
					pst.close();
					
				}
			
			session.setAttribute(MESSAGE, SUCCESSM+getDesigCode()+" updated successfully."+END);
			
		} catch (Exception e) {
			e.printStackTrace();
			return ERROR;
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		if(getFromPage() != null && getFromPage().equals("AE")) {
			return "AESUCCESS";
		} else {
			return SUCCESS;
		}
	}
	
	public String viewDesig(String strEdit) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from designation_details where designation_id = ?");
			pst.setInt(1, uF.parseToInt(strEdit));
			rs = pst.executeQuery();
			String attribute_ids=null;
			int nLevelId = 0;
			while(rs.next()){
				setDesigName(rs.getString("designation_name"));
				setDesigCode(rs.getString("designation_code"));
				setDesigDesc(rs.getString("designation_description"));
				setDesigId(rs.getString("designation_id"));
				attribute_ids = rs.getString("attribute_ids");
				setJobDescription(rs.getString("job_description"));
				setProfile(rs.getString("profile"));
				nLevelId = uF.parseToInt(rs.getString("level_id")); 
				request.setAttribute("desc", rs.getString("designation_description"));
				request.setAttribute("ideal_candidate", rs.getString("ideal_candidate"));
				request.setAttribute("profile_desc", rs.getString("profile"));
			}
			rs.close();
			pst.close(); 
			 
			pst = con.prepareStatement("select * from level_details where level_id=?");
			pst.setInt(1, nLevelId);
			rs = pst.executeQuery();
			while(rs.next()){
				setOrgId(rs.getString("org_id"));
			}
			rs.close();
			pst.close();
			
			
			pst = con.prepareStatement("select * from designation_kra_details where designation_id=? order by designation_kra_id");
			pst.setInt(1, uF.parseToInt(strEdit));
			rs = pst.executeQuery();
			List<List<String>> desigKraDetails = new ArrayList<List<String>>();
			while(rs.next()){
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("designation_kra_id"));
				innerList.add(uF.showData(rs.getString("kra_name"), ""));
				innerList.add(CF.getElementDetails(con, request, rs.getString("element_id")));
				innerList.add(CF.getAttributeListElementwise(con, uF, request, getOrgId(), rs.getString("element_id"), rs.getString("attribute_id")));
				innerList.add(uF.showData(rs.getString("task_name"), ""));
				desigKraDetails.add(innerList);
			}
			rs.close();
			pst.close();
			request.setAttribute("desigKraDetails", desigKraDetails);
			
			pst = con.prepareStatement("select * from desig_attribute where desig_id=?");
			pst.setInt(1, uF.parseToInt(strEdit));
			rs = pst.executeQuery();
			Map<String,String> hmDesigAttribute=new HashMap<String, String>();
			while(rs.next()){
				String id=rs.getString("_type");
				if(id.equals("1")) {
					if (rs.getString("desig_value") != null && !rs.getString("desig_value").equals("")) {
						List<String> educationValue = new ArrayList<String>();
						educationValue = Arrays.asList(rs.getString("desig_value").split(","));
						for (int k = 0; k < educationValue.size(); k++) {
							if(educationValue.get(k) != null && !educationValue.get(k).equals("")) {
								educationID.add(educationValue.get(k).trim());
							}
						}
					}
//					hmDesigAttribute.put(id, uF.showData(rs.getString("desig_value"), ""));
//					setEducation(uF.showData(rs.getString("desig_value"), ""));
				} else if(id.equals("2")) {
					String desig_value;
					if (rs.getString("desig_value") == null || rs.getString("desig_value").equals("")) {
						desig_value = "0.0";
					} else if (rs.getString("desig_value").contains(".1")) {
						desig_value = rs.getString("desig_value") + "0";
					} else if (rs.getString("desig_value").contains(".")) {
						desig_value = rs.getString("desig_value");
					} else {
						desig_value = rs.getString("desig_value") + ".0";
					}					
					hmDesigAttribute.put(id, uF.showData(splitString(desig_value), "0_0"));
				}else if(id.equals("3")){
					String desig_value;
					if (rs.getString("desig_value") == null || rs.getString("desig_value").equals("")) {
						desig_value = "0.0";
					} else if (rs.getString("desig_value").contains(".1")) {
						desig_value = rs.getString("desig_value") + "0";
					} else if (rs.getString("desig_value").contains(".")) {
						desig_value = rs.getString("desig_value");
					} else {
						desig_value = rs.getString("desig_value") + ".0";
					}
					
					hmDesigAttribute.put(id, uF.showData(splitString(desig_value), "0_0"));
				}else if(id.equals("4")){
					String desig_value;
					if (rs.getString("desig_value") == null || rs.getString("desig_value").equals("")) {
						desig_value = "0.0";
					} else if (rs.getString("desig_value").contains(".1")) {
						desig_value = rs.getString("desig_value") + "0";
					} else if (rs.getString("desig_value").contains(".")) {
						desig_value = rs.getString("desig_value");
					} else {
						desig_value = rs.getString("desig_value") + ".0";
					}					 
					hmDesigAttribute.put(id, uF.showData(splitString(desig_value), "0_0"));
				}else if(id.equals("5")){
					if (rs.getString("desig_value") != null && !rs.getString("desig_value").equals("")) {
						List<String> skillValue = new ArrayList<String>();
						skillValue = Arrays.asList(rs.getString("desig_value").split(","));
						for (int k = 0; k < skillValue.size(); k++) {
							if(skillValue.get(k) != null && !skillValue.get(k).equals("")){
								skillsID.add(skillValue.get(k).trim());
							}
						}
					}
//					hmDesigAttribute.put(id, uF.showData(rs.getString("desig_value"), ""));
//					setSkill(uF.showData(rs.getString("desig_value"), ""));
				} else if(id.equals("6")) {
					hmDesigAttribute.put(id, uF.showData(rs.getString("desig_value"), ""));
					setStrGender(uF.showData(rs.getString("desig_value"), ""));
				
				} else if(id.equals("7")) {
					hmDesigAttribute.put(id, uF.showData(rs.getString("desig_value"), ""));
					setStrMinAge(uF.showData(rs.getString("desig_value"), ""));
				}  else if(id.equals("8")) {
//					hmDesigAttribute.put(id, uF.showData(rs.getString("desig_value"), ""));
					List<String> CTCValue = new ArrayList<String>();
					CTCValue = Arrays.asList(rs.getString("desig_value").split("-"));
					setStrMinCTC(uF.showData(CTCValue.get(0), ""));
					setStrMaxCTC(uF.showData(CTCValue.get(1), ""));
				}
			}
			rs.close();
			pst.close();
			request.setAttribute("hmDesigAttribute",hmDesigAttribute);			
			request.setAttribute("attribute_ids",attribute_ids);
			
			pst = con.prepareStatement("select count(grade_id) as cnt from grades_details gd, designation_details dd where " +
					"gd.designation_id = dd.designation_id and dd.level_id in (select ld.level_id from level_details ld) and dd.designation_id=?");
			pst.setInt(1, uF.parseToInt(strEdit));
			rs = pst.executeQuery();
			int nGradeCnt = 0;
			while(rs.next()){
				nGradeCnt = uF.parseToInt(rs.getString("cnt"));
			}
			rs.close();
			pst.close();
		
			setNoOfGrades(""+nGradeCnt);
			
		} catch (Exception e) {
			e.printStackTrace();
			return ERROR;
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
			return UPDATE;
		
	}
	
	
//public String getAttributeListElementwise(Connection con, UtilityFunctions uF, String elementId, String attributeId) {
//		
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		StringBuilder sb = new StringBuilder();
//		try {
//			StringBuilder sbLevelids = new StringBuilder();
//			pst = con.prepareStatement("select level_id from level_details where org_id = ?");
//			pst.setInt(1, uF.parseToInt(orgId));
//			rs = pst.executeQuery();
//			int cnt=0;
//			while(rs.next()){
//				if(cnt==0){	
//					sbLevelids.append(rs.getString("level_id"));
//					cnt++;
//				}else{
//					sbLevelids.append(","+rs.getString("level_id"));
//				}
//			}
//			
//			StringBuilder sbQuery = new StringBuilder();
//			sbQuery.append("select distinct(a.appraisal_attribute),a.appraisal_element,aa.attribute_name from (select " +
//					"appraisal_element,appraisal_attribute from appraisal_element_attribute where appraisal_element = ?" +
//					") as a, appraisal_attribute aa where a.appraisal_attribute=aa.arribute_id ");
//			if(sbLevelids != null && !sbLevelids.toString().equals("")){
//			sbQuery.append("and level_id in ("+sbLevelids.toString()+")");
//			}
//			sbQuery.append(" order by appraisal_attribute");
//			pst = con.prepareStatement(sbQuery.toString());
//			pst.setInt(1, uF.parseToInt(elementId));
//			rs = pst.executeQuery();
////			System.out.println("pst ===> "+pst);
//			while(rs.next()) {
//				if(attributeId != null && rs.getString("appraisal_attribute").equals(attributeId)) {
//					sb.append("<option value=" + rs.getString("appraisal_attribute") + " selected>"+ rs.getString("attribute_name") + "</option>");
//				} else {
//					sb.append("<option value=" + rs.getString("appraisal_attribute") + ">"+ rs.getString("attribute_name") + "</option>");
//				}
//				//al.add(new FillAttribute(rs.getString("appraisal_attribute"), rs.getString("attribute_name")));				
//			}
//			request.setAttribute("attributeOptions", sb.toString());
////			System.out.println("ATTRIBUTES =====> "+al.toString());
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return sb.toString();
//	}


	
	private String splitString(String st) {
		if (st.equals("") || st.equals("0")) {
			st = "0.0";
		}
		st = st.replace('.', '_');
		
		return st;
	}
	
	public String deleteDesig(String strId) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement(deleteDesig);
			pst.setInt(1, uF.parseToInt(strId));
			pst.execute();
			pst.close();
			
			pst = con.prepareStatement(deleteGrade1);
			pst.setInt(1, uF.parseToInt(strId));
			pst.execute();
			pst.close();
			
			session.setAttribute(MESSAGE, SUCCESSM+"Deleted successfully."+END);
			
		} catch (Exception e) {
			e.printStackTrace();
			return ERROR;
		} finally {
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}

	String desigId;
	String desigCode;
	String desigName;
	String desigDesc;
	String desiglevel;
	String attributeid;
	
//	public void validate() {
//
////		if (getDesigId() != null && getDesigId().length() == 0) {
////			addFieldError("desigId", "Designation ID is required");
////		}
//		if (getDesigName() != null && getDesigName().length() == 0) {
//			addFieldError("password", "Designation Name is required");
//		}
//		if (getDesigCode() != null && getDesigCode().length() == 0) {
//			addFieldError("desigCode", "Designation Code is required");
//		}
//		loadValidateDesig();
//		getElement();
//		getAttributeDetails();
//	}

	private HttpServletRequest request;

	public String getAttributeid() {
		return attributeid;
	}

	public void setAttributeid(String attributeid) {
		this.attributeid = attributeid;
	}

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public String getDesigId() {
		return desigId;
	}

	public void setDesigId(String desigId) {
		this.desigId = desigId;
	}

	public String getDesigName() {
		return desigName;
	}

	public void setDesigName(String desigName) {
		this.desigName = desigName;
	}

	public String getDesigDesc() {
		return desigDesc;
	}

	public void setDesigDesc(String desigDesc) {
		this.desigDesc = desigDesc;
	}

	public String getDesigCode() {
		return desigCode;
	}

	public void setDesigCode(String desigCode) {
		this.desigCode = desigCode;
	}

	public String getDesiglevel() {
		return desiglevel;
	}

	public void setDesiglevel(String desiglevel) {
		this.desiglevel = desiglevel;
	}

	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

	public List<FillGender> getGenderList() {
		return genderList;
	}

	public void setGenderList(List<FillGender> genderList) {
		this.genderList = genderList;
	}

	public String getEducation() {
		return education;
	}

	public void setEducation(String education) {
		this.education = education;
	}

	public String getTotalexpYear() {
		return totalexpYear;
	}

	public void setTotalexpYear(String totalexpYear) {
		this.totalexpYear = totalexpYear;
	}

	public String getTotalexpMonth() {
		return totalexpMonth;
	}

	public void setTotalexpMonth(String totalexpMonth) {
		this.totalexpMonth = totalexpMonth;
	}

	public String getRelevantYear() {
		return relevantYear;
	}

	public void setRelevantYear(String relevantYear) {
		this.relevantYear = relevantYear;
	}

	public String getRelevantMonth() {
		return relevantMonth;
	}

	public void setRelevantMonth(String relevantMonth) {
		this.relevantMonth = relevantMonth;
	}

	public String getExpusYear() {
		return expusYear;
	}

	public void setExpusYear(String expusYear) {
		this.expusYear = expusYear;
	}

	public String getExpusMonth() {
		return expusMonth;
	}

	public void setExpusMonth(String expusMonth) {
		this.expusMonth = expusMonth;
	}

	public String getSkill() {
		return skill;
	}

	public void setSkill(String skill) {
		this.skill = skill;
	}
	
	public String getStrGender() {
		return strGender;
	}

	public void setStrGender(String strGender) {
		this.strGender = strGender;
	}

	public String getStrMinAge() {
		return strMinAge;
	}

	public void setStrMinAge(String strMinAge) {
		this.strMinAge = strMinAge;
	}

	public List<FillLevel> getLevelList() {
		return levelList;
	}

	public void setLevelList(List<FillLevel> levelList) {
		this.levelList = levelList;
	}

	public String getJobDescription() {
		return jobDescription;
	}

	public void setJobDescription(String jobDescription) {
		this.jobDescription = jobDescription;
	}

	public String getProfile() {
		return profile;
	}

	public void setProfile(String profile) {
		this.profile = profile;
	}

	public String getIdealcandidate() {
		return idealcandidate;
	}

	public void setIdealcandidate(String idealcandidate) {
		this.idealcandidate = idealcandidate;
	}

	public List<FillSkills> getSkillsList() {
		return skillsList;
	}

	public void setSkillsList(List<FillSkills> skillsList) {
		this.skillsList = skillsList;
	}

	public List<FillEducational> getEducationalList() {
		return educationalList;
	}

	public void setEducationalList(List<FillEducational> educationalList) {
		this.educationalList = educationalList;
	}

	public List<String> getSkillsID() {
		return skillsID;
	}

	public void setSkillsID(List<String> skillsID) {
		this.skillsID = skillsID;
	}

	public List<String> getEducationID() {
		return educationID;
	}

	public void setEducationID(List<String> educationID) {
		this.educationID = educationID;
	}

	public String[] getDesignKRA() {
		return designKRA;
	}

	public void setDesignKRA(String[] designKRA) {
		this.designKRA = designKRA;
	}

	public String[] getDesigKraId() {
		return desigKraId;
	}

	public void setDesigKraId(String[] desigKraId) {
		this.desigKraId = desigKraId;
	}

	public List<FillAttribute> getAttributeList() {
		return attributeList;
	}

	public void setAttributeList(List<FillAttribute> attributeList) {
		this.attributeList = attributeList;
	}

	public String[] getGoalElements() {
		return goalElements;
	}

	public void setGoalElements(String[] goalElements) {
		this.goalElements = goalElements;
	}

	public String[] getElementAttribute() {
		return elementAttribute;
	}

	public void setElementAttribute(String[] elementAttribute) {
		this.elementAttribute = elementAttribute;
	}

	public String getFromPage() {
		return fromPage;
	}

	public void setFromPage(String fromPage) {
		this.fromPage = fromPage;
	}

	public String getEmpId() {
		return empId;
	}

	public void setEmpId(String empId) {
		this.empId = empId;
	}

	public String[] getDesignKRATask() {
		return designKRATask;
	}

	public void setDesignKRATask(String[] designKRATask) {
		this.designKRATask = designKRATask;
	}

	public String getStrMinCTC() {
		return strMinCTC;
	}

	public void setStrMinCTC(String strMinCTC) {
		this.strMinCTC = strMinCTC;
	}

	public String getStrMaxCTC() {
		return strMaxCTC;
	}

	public void setStrMaxCTC(String strMaxCTC) {
		this.strMaxCTC = strMaxCTC;
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
	
	public String getNoOfGrades() {
		return noOfGrades;
	}


	public void setNoOfGrades(String noOfGrades) {
		this.noOfGrades = noOfGrades;
	}
}