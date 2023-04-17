package com.konnect.jpms.reports.advance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.employee.OrganisationalChart;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class OrganisationTreeView extends ActionSupport implements  ServletRequestAware, IStatements  {

	HttpSession session;
	CommonFunctions CF;
	String strEmpId;
	String strUserType;
	private static Logger log = Logger.getLogger(OrganisationalChart.class);
	
	public String execute() throws Exception {
		
		session = request.getSession(true);
		strUserType = (String)session.getAttribute(USERTYPE);
		strEmpId = (String)session.getAttribute(EMPID);
		
		
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		

//		if(request.getParameter("t")!=null){
		//	request.setAttribute(PAGE, POrganisationalChart1);
			request.setAttribute(PAGE, "/jsp/reports/advance/OrganisationTreeView.jsp");
			request.setAttribute(TITLE, "Employee Organogram View");
//			getAllEmployees();
//		}else{
//			request.setAttribute(PAGE, POrganisationalChart);
//			request.setAttribute(TITLE, TOrganisationalChart);
			getHireracyLevels();
//		}
		

		return SUCCESS;
	}
	 
	
	public void getAllEmployees(){
		PreparedStatement pst = null;
		ResultSet rs = null;
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF=new UtilityFunctions();
		try {
    

			con = db.makeConnection(con);
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			// colour Logic for Department .....
			
			
			///	itemC.itemTitleColor = primitives.common.Colors.Yellow;
			String colourArray[]={"Pink" ,"DarkCyan" ,"Gold", "Indigo", "Limegreen", "Orange", "Olive" };
//			,"LightSeaGreen",  "DarkSeaGreen" , "Turquoise" , "LightSteelBlue",  "BurlyWood" , "Goldenrod" };
			Map<String,String> hmDeptColourCode=new HashMap<String, String>();
			pst=con.prepareStatement("select dept_id from department_info");
	        rs=pst.executeQuery();
			int colourCount=0;
	        while(rs.next()){
				
	        	if(colourCount>colourArray.length-1)
					colourCount=0;
				
	        	hmDeptColourCode.put(rs.getString("dept_id"),colourArray[colourCount]);

				colourCount++;	
			}
			rs.close();
			pst.close();
			
	        colourCount=0;
			String colourArrayLevel[]={ "LightSeaGreen",  "DarkSeaGreen" , "Turquoise" , "LightSteelBlue",  "BurlyWood" , "Goldenrod" };
					Map<String,String> hmLevelColourCode=new HashMap<String, String>();
					pst=con.prepareStatement("select level_id from level_details");
			        rs=pst.executeQuery();
					int colourCount1=0;
			        while(rs.next()){
						
			        	if(colourCount1>colourArrayLevel.length-1)
							colourCount1=0;
						
			        	hmLevelColourCode.put(rs.getString("level_id"),colourArrayLevel[colourCount1]);

						colourCount1++;	
					}
					rs.close();
					pst.close();
	        
	        
	        pst = con.prepareStatement("select od.org_id,org_name,org_city,org_logo,level_name,designation_name,emp_fname,emp_mname, emp_lname," +
	        		" depart_id,supervisor_emp_id,emp_id,dept_name,dd.designation_id,ld.level_id " +
	        		" from employee_personal_details epd join  employee_official_details eod on (epd.emp_per_id = eod.emp_id) " +
	        		" join department_info di on (depart_id=dept_id)  join grades_details gd on eod.grade_id=gd.grade_id " +
	        		" join designation_details dd on gd.designation_id=dd.designation_id join level_details ld on dd.level_id=ld.level_id " +
	        		" join org_details od  on ld.org_id=od.org_id where  is_alive=1 " +
	        		" and emp_per_id >0  order by supervisor_emp_id,emp_id "); 
			rs = pst.executeQuery();
			
			Map<String,List<String>> hmHireracyLevels = new LinkedHashMap<String, List<String>>();
			Map<String, String> hmEmpSuperMap = new LinkedHashMap<String, String>();
			
			Map<String, String> hmEmpOrgName = new LinkedHashMap<String, String>();
			Map<String, List<String>> hmOrganisations = new LinkedHashMap<String, List<String>>();
/*			List<String> alHireracyLevels = new ArrayList();;
			List alInner = new ArrayList();*/
			
			Map<String, String> hmEmpDepartmentMap=new HashMap<String, String>();
			Map<String, String> hmEmpNameMap=new HashMap<String, String>();
			Map<String, String> hmEmpDesigMap=new HashMap<String, String>();
			Map<String, String> hmEmpLevelMap=new HashMap<String, String>();
			
			Map<String, String> hmEmpDepartmentColourMap=new HashMap<String, String>();
			Map<String, String> hmEmpLevelColourMap=new HashMap<String, String>();


			
			List<String> alInnerList = null;
			while(rs.next()){
				
				alInnerList= hmHireracyLevels.get(rs.getString("supervisor_emp_id"));
				if(alInnerList==null)
					alInnerList=new ArrayList<String>();

				
				alInnerList.add(rs.getString("emp_id"));
				
				hmHireracyLevels.put(rs.getString("supervisor_emp_id"), alInnerList);
				
				hmEmpSuperMap.put(rs.getString("emp_id"), rs.getString("supervisor_emp_id"));
				hmEmpOrgName.put(rs.getString("emp_id"),rs.getString("org_id"));

				if(!hmOrganisations.keySet().contains((rs.getString("org_id"))))
				{
					List<String> alInner=new ArrayList<String>();
					alInner.add(rs.getString("org_name"));
					alInner.add(rs.getString("org_city"));
					alInner.add(rs.getString("org_logo"));
					
					hmOrganisations.put(rs.getString("org_id"),alInner);
					
				}
				
				hmEmpDepartmentColourMap.put(rs.getString("emp_id"),showDataUpdated(hmDeptColourCode.get(rs.getString("depart_id")) ,"Red"));
				
				hmEmpLevelColourMap.put(rs.getString("emp_id"),showDataUpdated(hmLevelColourCode.get(rs.getString("level_id")) ,"Red"));
			
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				
				hmEmpNameMap.put(rs.getString("emp_id"), rs.getString("emp_fname") +strEmpMName+ " " + rs.getString("emp_lname"));			
				hmEmpDesigMap.put(rs.getString("emp_id"), rs.getString("designation_name"));				
				hmEmpLevelMap.put(rs.getString("emp_id"), rs.getString("level_name"));
				hmEmpDepartmentMap.put(rs.getString("emp_id"), rs.getString("dept_name"));

			}
			rs.close();
			pst.close();
			

			StringBuilder sbrootItemData=new StringBuilder();
			StringBuilder sbChartData=new StringBuilder();
 	
			Iterator<String> itrOrganisations=hmOrganisations.keySet().iterator();
			
			while(itrOrganisations.hasNext()){
				String orgID=itrOrganisations.next();
			
				sbrootItemData.append("var rootItem"+orgID+"=new primitives.orgdiagram.ItemConfig('Dailyhrz', 'DAILY HRZ SOFT. SOLUTIONS LLP'," +
						" '"+request.getContextPath()+"/images1/banner_dailyhrz.png'); " +
						"rootItem.items.push(rootItem"+orgID+") ");
			}

			
		
			    Iterator<String> itrSuperEmp=hmHireracyLevels.keySet().iterator();
			   
			 // those who are under Super NODE ie.  dont have Super emp IDs..................
		
			    		List<String> alEmpListSuper=(List<String>) hmHireracyLevels.get("0");
			    		for(int i=0; alEmpListSuper!=null && i<alEmpListSuper.size();i++){
			    			String orgID=hmEmpOrgName.get(alEmpListSuper.get(i));
			    			if(i%2==0){
			    			sbChartData.append("var empSuper"+alEmpListSuper.get(i)+" =new primitives.orgdiagram.ItemConfig('"+showDataUpdated(hmEmpLevelMap.get(alEmpListSuper.get(i)),"")+"'," +
							"'"+showDataUpdated(hmEmpNameMap.get(alEmpListSuper.get(i)),"")+"             "+showDataUpdated(hmEmpDesigMap.get(alEmpListSuper.get(i)),"")+"','"+request.getContextPath()+"/userImages/avatar_photo.png'); " +
							"empSuper"+alEmpListSuper.get(i)+".itemType = primitives.orgdiagram.ItemType.Assistant;" +
							"empSuper"+alEmpListSuper.get(i)+".adviserPlacementType = primitives.orgdiagram.AdviserPlacementType.Right;" +
							"empSuper"+alEmpListSuper.get(i)+".phone = '00000000000';" +
							"empSuper"+alEmpListSuper.get(i)+".email = 'abc@abc.com';" +
							"empSuper"+alEmpListSuper.get(i)+".groupTitle = '"+showDataUpdated(hmEmpDepartmentMap.get(alEmpListSuper.get(i)),"")+"';" +
							"empSuper"+alEmpListSuper.get(i)+".itemTitleColor = primitives.common.Colors."+showDataUpdated(hmEmpLevelColourMap.get(alEmpListSuper.get(i)),"Red")+" ;" +
							"empSuper"+alEmpListSuper.get(i)+".groupTitleColor = primitives.common.Colors."+showDataUpdated(hmEmpDepartmentColourMap.get(alEmpListSuper.get(i)),"Red")+" ;" +
							"rootItem"+orgID+".items.push(empSuper"+alEmpListSuper.get(i)+");");
			    			}
			    			else{
			    				sbChartData.append("var empSuper"+alEmpListSuper.get(i)+" =new primitives.orgdiagram.ItemConfig('"+showDataUpdated(hmEmpLevelMap.get(alEmpListSuper.get(i)),"")+"'," +
										"'"+showDataUpdated(hmEmpNameMap.get(alEmpListSuper.get(i)),"")+"             "+showDataUpdated(hmEmpDesigMap.get(alEmpListSuper.get(i)),"")+"','"+request.getContextPath()+"/userImages/avatar_photo.png'); " +
										"empSuper"+alEmpListSuper.get(i)+".itemType = primitives.orgdiagram.ItemType.Assistant;" +
										"empSuper"+alEmpListSuper.get(i)+".adviserPlacementType = primitives.orgdiagram.AdviserPlacementType.Left;" +
										"empSuper"+alEmpListSuper.get(i)+".phone = '00000000000';" +
										"empSuper"+alEmpListSuper.get(i)+".email = 'abc@abc.com';" +
										"empSuper"+alEmpListSuper.get(i)+".groupTitle = '"+showDataUpdated(hmEmpDepartmentMap.get(alEmpListSuper.get(i)),"")+"';" +
										"empSuper"+alEmpListSuper.get(i)+".itemTitleColor = primitives.common.Colors."+showDataUpdated(hmEmpLevelColourMap.get(alEmpListSuper.get(i)),"Red")+" ;" +
										"empSuper"+alEmpListSuper.get(i)+".groupTitleColor = primitives.common.Colors."+showDataUpdated(hmEmpDepartmentColourMap.get(alEmpListSuper.get(i)),"Red")+" ;" +
										"rootItem"+orgID+".items.push(empSuper"+alEmpListSuper.get(i)+");");
			    			}
			    		}
			

			     
			    // Adding rest of records *********************************
			  
		

					
			   Iterator<String> itrEmp=hmEmpSuperMap.keySet().iterator();
			    int count=0;
			    while(itrEmp.hasNext()){
			    	
			    	String empId=itrEmp.next(); 
			    	String superEmpId=hmEmpSuperMap.get(empId);
			    	
			    	if(!alEmpListSuper.contains(empId)){
					     if(count%2==0){

			    	        if(alEmpListSuper.contains(superEmpId)){
			    	        	sbChartData.append(" var emp"+empId+" = new primitives.orgdiagram.ItemConfig('"+showDataUpdated(hmEmpLevelMap.get(empId),"")+"'," +
			    	        			"'"+showDataUpdated(hmEmpNameMap.get(empId),"")+" "+showDataUpdated(hmEmpDesigMap.get(empId),"")+"','"+request.getContextPath()+"/userImages/avatar_photo.png'); " +
							"emp"+empId+".itemType = primitives.orgdiagram.ItemType.Assistant;" +
							"emp"+empId+".adviserPlacementType = primitives.orgdiagram.AdviserPlacementType.Left;" +
							"emp"+empId+".phone = '00000000000';" +
							"emp"+empId+".email = 'abc@abc.com';" +
							"emp"+empId+".groupTitle = '"+showDataUpdated(hmEmpDepartmentMap.get(empId),"")+"';" +
							"emp"+empId+".itemTitleColor = primitives.common.Colors."+showDataUpdated(hmEmpLevelColourMap.get(empId),"Red")+" ;" +
							"emp"+empId+".groupTitleColor = primitives.common.Colors."+showDataUpdated(hmEmpDepartmentColourMap.get(empId),"Red")+" ;" +
							"empSuper"+superEmpId+".items.push(emp"+empId+");");
			    	       
		
			    	        }
			    	        else{
			    	        	sbChartData.append(" var emp"+empId+" = new primitives.orgdiagram.ItemConfig('"+showDataUpdated(hmEmpLevelMap.get(empId),"")+"'," +
			    	        			"'"+showDataUpdated(hmEmpNameMap.get(empId),"")+" "+showDataUpdated(hmEmpDesigMap.get(empId),"")+"','"+request.getContextPath()+"/userImages/avatar_photo.png'); " +
										"emp"+empId+".itemType = primitives.orgdiagram.ItemType.Assistant;" +
										"emp"+empId+".adviserPlacementType = primitives.orgdiagram.AdviserPlacementType.Left;" +
										"emp"+empId+".phone = '00000000000';" +
										"emp"+empId+".email = 'abc@abc.com';" +
										"emp"+empId+".groupTitle = '"+showDataUpdated(hmEmpDepartmentMap.get(empId),"")+"';" +
										"emp"+empId+".itemTitleColor = primitives.common.Colors."+showDataUpdated(hmEmpLevelColourMap.get(empId),"Red")+" ;" +
										"emp"+empId+".groupTitleColor = primitives.common.Colors."+showDataUpdated(hmEmpDepartmentColourMap.get(empId),"Red")+" ;" +
										"emp"+superEmpId+".items.push(emp"+empId+");");
					    		
					  
			    	        }
			
			    	         }else{
			    		       
				    	        if(alEmpListSuper.contains(superEmpId)){
				    	        	sbChartData.append(" var emp"+empId+" = new primitives.orgdiagram.ItemConfig('"+showDataUpdated(hmEmpLevelMap.get(empId),"")+"'," +
				    	        			"'"+showDataUpdated(hmEmpNameMap.get(empId),"")+" "+showDataUpdated(hmEmpDesigMap.get(empId),"")+"','"+request.getContextPath()+"/userImages/avatar_photo.png'); " +
								"emp"+empId+".itemType = primitives.orgdiagram.ItemType.Assistant;" +
								"emp"+empId+".adviserPlacementType = primitives.orgdiagram.AdviserPlacementType.Right;" +
								"emp"+empId+".phone = '00000000000';" +
								"emp"+empId+".email = 'abc@abc.com';" +
								"emp"+empId+".groupTitle = '"+showDataUpdated(hmEmpDepartmentMap.get(empId),"")+"';" +
								"emp"+empId+".itemTitleColor = primitives.common.Colors."+showDataUpdated(hmEmpLevelColourMap.get(empId),"Red")+" ;" +
								"emp"+empId+".groupTitleColor = primitives.common.Colors."+showDataUpdated(hmEmpDepartmentColourMap.get(empId),"Red")+" ;" +
								"empSuper"+superEmpId+".items.push(emp"+empId+");");
			
				    	        }
				    	        else{
				    	        	sbChartData.append(" var emp"+empId+" = new primitives.orgdiagram.ItemConfig('"+showDataUpdated(hmEmpLevelMap.get(empId),"")+"'," +
				    	        			"'"+showDataUpdated(hmEmpNameMap.get(empId),"")+" "+showDataUpdated(hmEmpDesigMap.get(empId),"")+"','"+request.getContextPath()+"/userImages/avatar_photo.png'); " +
											"emp"+empId+".itemType = primitives.orgdiagram.ItemType.Assistant;" +
											"emp"+empId+".adviserPlacementType = primitives.orgdiagram.AdviserPlacementType.Right;" +
											"emp"+empId+".phone = '00000000000';" +
											"emp"+empId+".email = 'abc@abc.com';" +
											"emp"+empId+".groupTitle = '"+showDataUpdated(hmEmpDepartmentMap.get(empId),"")+"';" +
											"emp"+empId+".itemTitleColor = primitives.common.Colors."+showDataUpdated(hmEmpLevelColourMap.get(empId),"Red")+" ;" +
											"emp"+empId+".groupTitleColor = primitives.common.Colors."+showDataUpdated(hmEmpDepartmentColourMap.get(empId),"Red")+" ;" +
											"emp"+superEmpId+".items.push(emp"+empId+");");
						    		
					
				    	        }
	                    	      	  
	                      }
					     count++;
	
			    	}
			    	
			    }

      
         
				request.setAttribute("sbrootItemData", sbrootItemData);
				request.setAttribute("sbCharData", sbChartData);
				

			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	public void getHireracyLevels(){
		PreparedStatement pst = null;
		ResultSet rs = null;
		Connection con = null;
		UtilityFunctions uF = new UtilityFunctions();
		Database db = new Database();
		db.setRequest(request);
		
		try{
			con = db.makeConnection(con);

//			Map hmEmpMap = CF.getEmpNameMap(strUserType, strEmpId);
			Map hmEmpMap = CF.getEmpNameMap(con,null, strEmpId);
			Map hmEmpProfileImage = CF.getEmpProfileImage(con);
			Map hmEmpDesigMap = CF.getEmpDesigMap(con);
			
			
			pst = con.prepareStatement("select * from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id and is_alive=1 and emp_id = ?");
			pst.setInt(1, uF.parseToInt(strEmpId));
			
			rs = pst.executeQuery();
			String strSupId = null;
			while(rs.next()){
				strSupId = rs.getString("supervisor_emp_id");
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id and is_alive= 1 and emp_per_id >0 order by supervisor_emp_id"); 
			rs = pst.executeQuery();
			
			Map hmHireracyLevels = new LinkedHashMap();
			Map<String, String> hmEmpSuperMap = new LinkedHashMap<String, String>();
			List<String> alHireracyLevels = new ArrayList();;
			List alInner = new ArrayList();
			
			String strSupervisorOld = null;
			String strSupervisorNew = null;
			
			while(rs.next()){
				strSupervisorNew = rs.getString("supervisor_emp_id");
				if(strSupervisorNew!=null && !strSupervisorNew.equalsIgnoreCase(strSupervisorOld)){
					alInner = new ArrayList();
				}
				
				alHireracyLevels.add(rs.getString("emp_id"));
				
				alInner.add(rs.getString("emp_id"));
				
				hmHireracyLevels.put(strSupervisorNew, alInner);
				
				hmEmpSuperMap.put(rs.getString("emp_id"), strSupervisorNew);
				
				strSupervisorOld = strSupervisorNew;
			}
			rs.close();
			pst.close();
			
			
			
			List<String> alChain = new ArrayList<String>();
			
			if(strUserType!=null && (strUserType.equalsIgnoreCase(EMPLOYEE) || strUserType.equalsIgnoreCase(MANAGER))){
				
				alChain.add(strEmpId);
				int count=0;
				for(;;){
					
					String strSuper = hmEmpSuperMap.get(strEmpId);
					alChain.add(strSuper);
					
					if(strUserType!=null && strUserType.equalsIgnoreCase(MANAGER)){
						List alEmpList = (List)hmHireracyLevels.get(strEmpId);
						for(int i=0;alEmpList!=null && i<alEmpList.size(); i++){
							alChain.add((String)alEmpList.get(i));
						}
					}
					
					strEmpId = strSuper;
					
					if(uF.parseToInt(strSuper)==0){
						break;
					}
				}	
			}
			
			
			  
			request.setAttribute("hmHireracyLevels", hmHireracyLevels);
			request.setAttribute("alHireracyLevels", alHireracyLevels);
			request.setAttribute("hmEmpMap", hmEmpMap);
			request.setAttribute("hmEmpDesigMap", hmEmpDesigMap);
			request.setAttribute("hmEmpProfileImage", hmEmpProfileImage);
			request.setAttribute("alChain", alChain);
			
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	public String showDataUpdated(String str, String showValue) {

		if (str == null) {
			return showValue;
		} else if (str != null && str.equalsIgnoreCase("NULL")) {
			return showValue;
		} else {

			if (str.contains("'"))
				str=str.replace("'", "\\'");
			return str;
			
		}
	}
	

	private HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}
}
