package com.konnect.jpms.cafeteria;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.export.GenerateForm16;
import com.konnect.jpms.reports.NoticeReport;
import com.konnect.jpms.select.FillMealType;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.select.FillYears;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UploadImage;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class AddDish extends ActionSupport implements ServletRequestAware, IStatements {
	private static final long serialVersionUID = 1L;
	public HttpSession session;
	String strSessionOrgId;
	String strSessionEmpId;
	String strUserType;
	public CommonFunctions CF; 
	
	private String strDishName;

	private String strFromDate;
	private String strToDate; 
	private String strFromTime;
	private String strToTime;
	private String strDishComment;
	private String strDishPrice;
	
	private File strDishImage;
	private String strDishImageFileName;
	
	private List<FillOrganisation> orgList;
	private List<FillWLocation> wLocationList;
	private List<FillMealType> mealTypeList;
	
	private String f_org;
	private String location;
	private String mealType;
	
	private String strSubmit;
	private String strUpdate;
	
	private String operation;
	private String dishId;
	private static Logger log = Logger.getLogger(AddDish.class);
	public String execute() throws Exception {
		
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)	return LOGIN;

		strUserType = (String)session.getAttribute(USERTYPE);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		
		UtilityFunctions uF = new UtilityFunctions();
		
		request.setAttribute(PAGE, PAddDish);
		request.setAttribute(TITLE, TAddDish);
		loadEmployee(uF);
	
		mealTypeList = new FillMealType().fillMealType();
		
		
		if(getOperation()!=null && getOperation().equals("D")) {
			deleteDish(uF);
		} else if(getStrUpdate()==null && getOperation()!=null && getOperation().equals("E")) {
			getDishDetails(uF);
		}
		
		if(getStrUpdate()!=null) {
			updateDishDetails(uF);
			return LOAD;
		}
		
		if(getStrSubmit()!=null) {
			addNewDishDetails(uF);
//			clearForm();
			return LOAD;
		}
	
		return SUCCESS;
	}
	
	private String loadEmployee(UtilityFunctions uF) {
		
		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)) {
			if(session.getAttribute(WLOCATION_ACCESS) != null && !session.getAttribute(WLOCATION_ACCESS).equals("")) {
				orgList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
				if(uF.parseToInt(getF_org()) == 0  && orgList!=null && orgList.size()>0) {
					setF_org(orgList.get(0).getOrgId());
				}
				wLocationList = new FillWLocation(request).fillWLocation(getF_org(), (String)session.getAttribute(WLOCATION_ACCESS));
			} else {
				 orgList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORGID));
				 if(uF.parseToInt(getF_org()) == 0) {
					setF_org((String) session.getAttribute(ORGID));
				}
				 wLocationList = new FillWLocation(request).fillWLocation(getF_org(), (String)session.getAttribute(WLOCATIONID));
			 }
		} else if(strUserType!=null && strUserType.equalsIgnoreCase(ADMIN)) {
			orgList = new FillOrganisation(request).fillOrganisation();
			if(uF.parseToInt(getF_org()) == 0  && orgList!=null && orgList.size()>0) {
				setF_org(orgList.get(0).getOrgId());
			}
			wLocationList = new FillWLocation(request).fillWLocation(getF_org());
		} else {
			orgList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORGID));
			if(uF.parseToInt(getF_org()) == 0) {
				setF_org((String) session.getAttribute(ORGID));
			}
			wLocationList = new FillWLocation(request).fillWLocation(getF_org(), (String)session.getAttribute(WLOCATIONID));
		}
		
		return LOAD;
	}
	/*private void clearForm() {
		setStrDishName("");
		setStrFromDate("");
		setStrToDate("");
		setStrDishComment("");
		setStrDishPrice("");
		setF_org("");
		setMealType("");
		setF_wlocation("");
		setStrFromTime("");//14
		setStrToTime("");//15
		
	}*/
	
	private void getDishDetails(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from dish_details where dish_id = ?");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1,uF.parseToInt(getDishId()));
			rs = pst.executeQuery();
			while(rs.next()) {
				setStrDishName(uF.showData(rs.getString("dish_name"), ""));
				setMealType(uF.showData(rs.getString("dish_type"), ""));
				setStrFromDate(uF.showData(uF.getDateFormat(rs.getString("dish_from_date"),DBDATE,DATE_FORMAT),""));
				setStrToDate(uF.showData(uF.getDateFormat(rs.getString("dish_to_date"),DBDATE,DATE_FORMAT),""));
				setStrDishComment(uF.showData(rs.getString("dish_comment"), ""));
				setStrDishPrice(uF.showData(rs.getString("dish_price"), ""));
				setF_org(uF.showData(rs.getString("org_id"), ""));
				setLocation(uF.showData(rs.getString("wlocation_id"), ""));
				String from_time = rs.getString("dish_from_time");
				String to_time = rs.getString("dish_to_time");
				
				if(from_time != null && !from_time.equals("")) {
					setStrFromTime(from_time.substring(0,from_time.lastIndexOf(":")));//14
				} else {
					setStrFromTime("");
				}
				if(to_time != null  && !to_time.equals("")) {
					setStrToTime(to_time.substring(0,to_time.lastIndexOf(":")));//15
				} else {
					setStrToTime("");
				}
				
				String extenstion = null;
				if(rs.getString("dish_image") !=null && !rs.getString("dish_image").trim().equals("")) {
					extenstion = FilenameUtils.getExtension(rs.getString("dish_image").trim());
				}
				
				request.setAttribute("extenstion",extenstion);
//				
				String dishImgPath = "";
				if(rs.getString("dish_image")!=null && !rs.getString("dish_image").equals("")) {
					if(CF.getStrDocSaveLocation()==null) {
						dishImgPath =  DOCUMENT_LOCATION +"/"+rs.getString("added_by")+"/"+getDishId()+"/"+rs.getString("dish_image") ;
					} else {
						dishImgPath = CF.getStrDocRetriveLocation()+I_DISHES+"/"+rs.getString("added_by")+"/"+getDishId()+"/"+rs.getString("dish_image");
					}
				}
				
				String dishImage = "<img class='lazy' id=\"dishImage\" border=\"0\" style=\"height: 100px; width:100px; border: 1px solid #CCCCCC; padding: 5px;\" src=\""+dishImgPath+"\" data-original=\""+dishImgPath+"\" />";
				request.setAttribute("dishImage",dishImage);
				request.setAttribute("dishImgPath",dishImgPath);
				request.setAttribute("dImage",(String)rs.getString("dish_image"));
			}
			rs.close();
			pst.close();
			
			if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)) {
				if(session.getAttribute(WLOCATION_ACCESS) != null && !session.getAttribute(WLOCATION_ACCESS).equals("")) {
					wLocationList = new FillWLocation(request).fillWLocation(getF_org(), (String)session.getAttribute(WLOCATION_ACCESS));
				} else {
					 wLocationList = new FillWLocation(request).fillWLocation(getF_org(), (String)session.getAttribute(WLOCATIONID));
				 }
			} else if(strUserType!=null && strUserType.equalsIgnoreCase(ADMIN)) {
				wLocationList = new FillWLocation(request).fillWLocation(getF_org());
			} else {
				wLocationList = new FillWLocation(request).fillWLocation(getF_org(), (String)session.getAttribute(WLOCATIONID));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	private void addNewDishDetails(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			 con = db.makeConnection(con);
			 StringBuilder sbQuery = new StringBuilder();
			 sbQuery.append("insert into dish_details (dish_name, dish_type, dish_from_date, dish_to_date, dish_from_time, dish_to_time,"
						+" added_by, org_id, wlocation_id, entry_date, dish_price, updated_by,last_updated_date, dish_comment )"
						+" values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?) ");
			 pst = con.prepareStatement(sbQuery.toString());
			 pst.setString(1, getStrDishName());
			 pst.setString(2, getMealType());
			 pst.setDate(3, uF.getDateFormat(getStrFromDate(),DATE_FORMAT));
			 pst.setDate(4, uF.getDateFormat(getStrToDate(),DATE_FORMAT));
			 pst.setTime(5, uF.getTimeFormat(getStrFromTime(), TIME_FORMAT));
			 pst.setTime(6, uF.getTimeFormat(getStrToTime(), TIME_FORMAT));
			 pst.setInt(7, uF.parseToInt(strSessionEmpId));
			 pst.setInt(8, uF.parseToInt(getF_org()));
			 pst.setInt(9, uF.parseToInt(getLocation()));
			 pst.setDate(10,uF.getCurrentDate(CF.getStrTimeZone()));
			 pst.setInt(11, uF.parseToInt(getStrDishPrice()));
			 pst.setInt(12, uF.parseToInt(strSessionEmpId));
			 pst.setDate(13,uF.getCurrentDate(CF.getStrTimeZone()));
			 pst.setString(14, getStrDishComment());
			 pst.executeUpdate();
			 pst.close();
			 
			 String dish_Id = null;
			 pst = con.prepareStatement("select max(dish_id) as dish_id from dish_details");
			 rs = pst.executeQuery();
			 while(rs.next()) {
				dish_Id = rs.getString("dish_id");
			 }
				rs.close();
				pst.close();
				
				uploadImage(dish_Id);
			 
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	private void uploadImage(String dishId) {
		
		try {
			
			UploadImage uI = new UploadImage();
			uI.setServletRequest(request);
			uI.setImageType("DISH_IMAGE");
			uI.setEmpImage(getStrDishImage());
			uI.setEmpImageFileName(getStrDishImageFileName());
			uI.setEmpId(strSessionEmpId);
			uI.setDishId(dishId);
			uI.setCF(CF);
			uI.upoadImage();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	private void updateDishDetails(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("update dish_details set dish_name=?, dish_type=?, dish_from_date=?, dish_to_date=?, dish_from_time=?, dish_to_time=?,"
				+" updated_by=?, org_id=?, wlocation_id=?, last_updated_date=?, dish_price=?, dish_comment=? where dish_id=?");
			pst = con.prepareStatement(sbQuery.toString());
			 pst.setString(1, getStrDishName());
			 pst.setString(2, getMealType());
			 pst.setDate(3, uF.getDateFormat(getStrFromDate(),DATE_FORMAT));
			 pst.setDate(4, uF.getDateFormat(getStrToDate(),DATE_FORMAT));
			 pst.setTime(5, uF.getTimeFormat(getStrFromTime(), TIME_FORMAT));
			 pst.setTime(6, uF.getTimeFormat(getStrToTime(), TIME_FORMAT));
			 pst.setInt(7, uF.parseToInt(strSessionEmpId));
			 pst.setInt(8, uF.parseToInt(getF_org()));
			 pst.setInt(9, uF.parseToInt(getLocation()));
			 pst.setDate(10,uF.getCurrentDate(CF.getStrTimeZone()));
			 pst.setInt(11, uF.parseToInt(getStrDishPrice()));
			 pst.setString(12, getStrDishComment());
			 pst.setInt(13,uF.parseToInt(getDishId()));
			 pst.executeUpdate();
			 pst.close();
			
			if(getStrDishImage()!=null && !getStrDishImage().equals("")) {
				StringBuilder sbQuery1 = new StringBuilder();
				sbQuery1.append("select * from dish_details where dish_id=?");
				pst = con.prepareStatement(sbQuery1.toString());
				pst.setInt(1,uF.parseToInt(getDishId()));
				rs = pst.executeQuery();
				String dish_image = null;
				String addedBy = null;
				while(rs.next()) {
				    dish_image = rs.getString("dish_image");
				    addedBy = rs.getString("added_by");
				}
				rs.close();
				pst.close();
				
				String strFilePath = null;
				if(CF.getStrDocSaveLocation()==null) {
						strFilePath = DOCUMENT_LOCATION +"/"+addedBy+"/"+getDishId()+"/"+dish_image;
				} else {
						strFilePath = CF.getStrDocSaveLocation()+I_DISHES+"/"+addedBy+"/"+getDishId() +"/"+dish_image;
				}
				File file = new File(strFilePath);
				file.delete();
				
				uploadImage(getDishId());
			}
			
	    } catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}
	
	
	private void deleteDish(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from dish_details where dish_id=?");
			pst.setInt(1, uF.parseToInt(getDishId()));
			rs = pst.executeQuery();
			String dish_image = null;
			String addedBy = null;
			while(rs.next()) {
			    dish_image = rs.getString("dish_image");
			    addedBy = rs.getString("added_by");
			}
			rs.close();
			pst.close();
			
			String strFilePath = null;
			if(CF.getStrDocSaveLocation()==null) {
				strFilePath = DOCUMENT_LOCATION +"/"+addedBy+"/"+getDishId()+"/"+"/"+dish_image;
			} else {
				strFilePath = CF.getStrDocSaveLocation()+I_DISHES+"/"+addedBy+"/"+getDishId() +"/"+dish_image;
			}
			File file = new File(strFilePath);
			file.delete();
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("delete from dish_details where dish_id = ?");
					
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1,uF.parseToInt(getDishId()));
			pst.execute();
			pst.close();
			
			StringBuilder sbQuery1 = new StringBuilder();
			sbQuery1.append("delete from dish_order_details where order_status = 0 and dish_id = ?");
			pst = con.prepareStatement(sbQuery1.toString());
			pst.setInt(1,uF.parseToInt(getDishId()));
//			System.out.println("pst==>"+pst);
			pst.execute();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	public HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public String getStrDishName() {
		return strDishName;
	}

	public void setStrDishName(String strDishName) {
		this.strDishName = strDishName;
	}

	
	public String getStrFromDate() {
		return strFromDate;
	}

	public void setStrFromDate(String strFromDate) {
		this.strFromDate = strFromDate;
	}

	public String getStrToDate() {
		return strToDate;
	}

	public void setStrToDate(String strToDate) {
		this.strToDate = strToDate;
	}

	public String getStrFromTime() {
		return strFromTime;
	}

	public void setStrFromTime(String strFromTime) {
		this.strFromTime = strFromTime;
	}

	public String getStrToTime() {
		return strToTime;
	}

	public void setStrToTime(String strToTime) {
		this.strToTime = strToTime;
	}

	public String getStrDishComment() {
		return strDishComment;
	}

	public void setStrDishComment(String strDishComment) {
		this.strDishComment = strDishComment;
	}

	public String getStrDishPrice() {
		return strDishPrice;
	}

	public void setStrDishPrice(String strDishPrice) {
		this.strDishPrice = strDishPrice;
	}

	public File getStrDishImage() {
		return strDishImage;
	}

	public void setStrDishImage(File strDishImage) {
		this.strDishImage = strDishImage;
	}

	public String getStrDishImageFileName() {
		return strDishImageFileName;
	}

	public void setStrDishImageFileName(String strDishImageFileName) {
		this.strDishImageFileName = strDishImageFileName;
	}

	public List<FillOrganisation> getOrgList() {
		return orgList;
	}

	public void setOrgList(List<FillOrganisation> orgList) {
		this.orgList = orgList;
	}

	public List<FillWLocation> getwLocationList() {
		return wLocationList;
	}

	public void setwLocationList(List<FillWLocation> wLocationList) {
		this.wLocationList = wLocationList;
	}

	public List<FillMealType> getMealTypeList() {
		return mealTypeList;
	}

	public void setMealTypeList(List<FillMealType> mealTypeList) {
		this.mealTypeList = mealTypeList;
	}

	public String getF_org() {
		return f_org;
	}

	public void setF_org(String f_org) {
		this.f_org = f_org;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getMealType() {
		return mealType;
	}

	public void setMealType(String mealType) {
		this.mealType = mealType;
	}

	public String getStrSubmit() {
		return strSubmit;
	}

	public void setStrSubmit(String strSubmit) {
		this.strSubmit = strSubmit;
	}

	public String getStrUpdate() {
		return strUpdate;
	}

	public void setStrUpdate(String strUpdate) {
		this.strUpdate = strUpdate;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getDishId() {
		return dishId;
	}

	public void setDishId(String dishId) {
		this.dishId = dishId;
	}
}
