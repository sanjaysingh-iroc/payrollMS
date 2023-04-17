package com.konnect.jpms.library;

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
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.select.FillYears;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UploadImage;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class AddBook extends ActionSupport implements ServletRequestAware, IStatements {
	private static final long serialVersionUID = 1L;
	public HttpSession session;
	String strSessionOrgId;
	String strSessionEmpId;
	String strUserType;
	public CommonFunctions CF; 
	
	private String f_org;      
	private String location;
	
	
	private String strBookTitle;
	private String strBookAuthor; 
	private String strBookPublisher;
	private String strBook_year_published;
	private String strBook_isbn_no;
	private String strBookCategory;
	private String strBook_short_description;
	private String strBook_quantity;
	
	private File strBookImage;
	private String strBookImageFileName;
	
	private List<FillOrganisation> orgList;
	private List<FillWLocation> wLocationList;
	private List<FillYears> yearsList;
	
	
	private String bookId;
	private String operation;
	private String issuedQuantity;
	
	private String strSubmit;
	private String strUpdate;
	private static Logger log = Logger.getLogger(AddBook.class);
	
	public String execute() throws Exception {
		
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)	return LOGIN;

		strUserType = (String)session.getAttribute(USERTYPE);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		
		UtilityFunctions uF = new UtilityFunctions();
		
		/*	boolean isView  = CF.getAccess(session, request, uF);
		if(!isView){
			request.setAttribute(PAGE, PAccessDenied);
			request.setAttribute(TITLE, TAccessDenied);
			return ACCESS_DENIED;
		}
		*/
		request.setAttribute(PAGE, PAddBook);
		request.setAttribute(TITLE, TAddBook);

		if(getF_org() == null) {
			setF_org((String)session.getAttribute(ORGID));
		}
		
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
		
		
		yearsList = new FillYears().fillYears(uF.getCurrentDate(CF.getStrTimeZone()));
		
		if(getOperation()!=null && getOperation().equals("D")) {
			deleteBook(uF);
		} else if(getStrUpdate()==null && getOperation()!=null && getOperation().equals("E")) {
			getBookDetails(uF);
		}
		
		if(getStrUpdate()!=null) {
			updateBookDetails(uF);
			return LOAD;
		}
		
		if(getStrSubmit()!=null) {
			insertBookDetails(uF);
//			clearForm(uF);
			return LOAD;
		}
		return SUCCESS;
	}
	
//	public void clearForm(UtilityFunctions uF){
//		setStrBookTitle("");
//		setStrBookAuthor("");
//		setStrBookPublisher("");
//		setStrBook_year_published("");
//		setStrBook_isbn_no("");
//		setStrBookCategory("");
//		setStrBook_short_description("");
//		setStrBook_quantity("");
//		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)) {
//			orgList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
//			if(uF.parseToInt(getF_org()) == 0  && orgList!=null && orgList.size()>0) {
//				setF_org(orgList.get(0).getOrgId());
//			}
//		} else {
//			if(uF.parseToInt(getF_org()) == 0) {
//				setF_org((String) session.getAttribute(ORGID));
//			}
//			orgList = new FillOrganisation(request).fillOrganisation();
//		}
//		
//		wLocationList = new FillWLocation(request).fillWLocation(getF_org());
//		
//		yearsList = new FillYears().fillYears(uF.getCurrentDate(CF.getStrTimeZone()));
//	
//	}
	
	private void getBookDetails(UtilityFunctions uF){
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from book_details where book_id = ?");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1,uF.parseToInt(getBookId()));
			rs = pst.executeQuery();
			while(rs.next()){
				setStrBookTitle(uF.showData(rs.getString("book_title"), ""));
				setStrBookAuthor(uF.showData(rs.getString("book_author"), ""));
				setStrBookPublisher(uF.showData(rs.getString("book_publisher"), ""));
				setStrBook_year_published(uF.showData(rs.getString("book_year_published"), ""));
				setStrBook_isbn_no(uF.showData(rs.getString("book_isbn_no"), ""));
				setStrBookCategory(uF.showData(rs.getString("book_category"), ""));
				setStrBook_short_description(uF.showData(rs.getString("book_short_description"), ""));
				setF_org(uF.showData(rs.getString("org_id"), ""));
				setLocation(uF.showData(rs.getString("wlocation_id"), ""));
				setStrBook_quantity(uF.showData(rs.getString("book_quantity"), ""));
				int availQuantity = uF.parseToInt(rs.getString("available_book_quantity"));
				int quantityIssued = uF.parseToInt(getStrBook_quantity()) - availQuantity;
				setIssuedQuantity(""+quantityIssued);
				String extenstion = null;
				if(rs.getString("book_image") !=null && !rs.getString("book_image").trim().equals("")){
					extenstion = FilenameUtils.getExtension(rs.getString("book_image").trim());
				}
				
				request.setAttribute("extenstion",extenstion);
//				
				String bookImgPath = "";
				if(rs.getString("book_image")!=null && !rs.getString("book_image").equals("")){
					if(CF.getStrDocSaveLocation()==null){
						bookImgPath =  DOCUMENT_LOCATION +"/"+rs.getString("added_by")+"/"+getBookId()+"/"+rs.getString("book_image") ;
					} else {
						bookImgPath = CF.getStrDocRetriveLocation()+I_BOOKS+"/"+rs.getString("added_by")+"/"+getBookId()+"/"+rs.getString("book_image");

					}
				}
				
				String bookImage = "<img class='lazy' id=\"bookImage\" border=\"0\" style=\"height: 100px; width:100px; border: 1px solid #CCCCCC; padding: 5px;\" src=\""+bookImgPath+"\" data-original=\""+bookImgPath+"\" />";
				request.setAttribute("bookImage",bookImage);
				request.setAttribute("bookImgPath",bookImgPath);
				request.setAttribute("bImage",(String)rs.getString("book_image"));
				
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
	
	private void updateBookDetails(UtilityFunctions uF){
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select book_quantity, available_book_quantity from book_details where book_id=?");
			pst.setInt(1, uF.parseToInt(getBookId()));
			rs = pst.executeQuery();
			int bookQnty = 0;
			int bookAvlblQnty = 0;
			while(rs.next()) {
				bookQnty = rs.getInt("book_quantity");
				bookAvlblQnty = rs.getInt("available_book_quantity");
			}
			rs.close();
			pst.close();
			
//			System.out.println("1......bookQnty==>"+bookQnty+"==>bookAvlblQnty==>"+bookAvlblQnty+"==getStrBook_quantity==>"+getStrBook_quantity());
			if(uF.parseToInt(getStrBook_quantity()) > bookQnty) {
				bookAvlblQnty = bookAvlblQnty + (uF.parseToInt(getStrBook_quantity()) - bookQnty);
			} else if(uF.parseToInt(getStrBook_quantity()) < bookQnty) {
//				bookAvlblQnty = bookAvlblQnty - (bookQnty - uF.parseToInt(getStrBook_quantity()) );
				bookAvlblQnty = uF.parseToInt(getStrBook_quantity()) - (bookQnty - bookAvlblQnty);
				
			}
			
//			System.out.println("2......==>bookAvlblQnty==>"+bookAvlblQnty);
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("update book_details set book_title=?, book_author=?, book_publisher=?, book_year_published=?, book_isbn_no=?, " +
					"book_category=?, book_short_description=?, org_id=?, wlocation_id=?, book_quantity=?, last_updated_by=?, last_updated_date=?," +
					"available_book_quantity=? where book_id=?");
			
			pst = con.prepareStatement(sbQuery.toString());
			pst.setString(1, getStrBookTitle());
			pst.setString(2, getStrBookAuthor());
			pst.setString(3, getStrBookPublisher());
			pst.setInt(4, uF.parseToInt(getStrBook_year_published()));
			pst.setString(5, getStrBook_isbn_no());
			pst.setString(6, getStrBookCategory());
			pst.setString(7, getStrBook_short_description());
			pst.setInt(8, uF.parseToInt(getF_org()));
			pst.setInt(9, uF.parseToInt(getLocation()));
			pst.setInt(10, uF.parseToInt(getStrBook_quantity()));
			pst.setInt(11, uF.parseToInt(strSessionEmpId));
			pst.setDate(12, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(13, bookAvlblQnty);
			pst.setInt(14, uF.parseToInt(getBookId()));
//			System.out.println("pst==>"+pst);
			pst.executeUpdate();
			pst.close();
//			System.out.println("getStrBookImage()==>"+getStrBookImage());
			if(getStrBookImage()!=null && !getStrBookImage().equals("")){
				StringBuilder sbQuery1 = new StringBuilder();
				sbQuery1.append("select * from book_details where book_id = ?");
				pst = con.prepareStatement(sbQuery1.toString());
				pst.setInt(1,uF.parseToInt(getBookId()));
//				System.out.println("pst2==>"+pst);
				rs = pst.executeQuery();
				String book_image = null;
				String addedBy = null;
				while(rs.next()) {
				    book_image = rs.getString("book_image");
				    addedBy = rs.getString("added_by");
				}
				rs.close();
				pst.close();
				
				String strFilePath = null;
				
				if(CF.getStrDocSaveLocation()==null) {
						strFilePath = DOCUMENT_LOCATION +"/"+addedBy+"/"+getBookId()+"/"+book_image;
				} else {
						strFilePath = CF.getStrDocSaveLocation()+I_BOOKS+"/"+addedBy+"/"+getBookId() +"/"+book_image;
				}
				
				File file = new File(strFilePath);
				file.delete();
				
				uploadImage(getBookId());
			}
			
	    } catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	private void insertBookDetails(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("insert into book_details(book_title,book_author,book_publisher,book_year_published,book_isbn_no,book_category,"
				+"book_short_description,org_id,wlocation_id,book_quantity,added_by,entry_date,last_updated_by,last_updated_date, " +
				"available_book_quantity) values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?)");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setString(1, getStrBookTitle());
			pst.setString(2, getStrBookAuthor());
			pst.setString(3, getStrBookPublisher());
			pst.setInt(4, uF.parseToInt(getStrBook_year_published()));
			pst.setString(5, getStrBook_isbn_no());
			pst.setString(6, getStrBookCategory());
			pst.setString(7, getStrBook_short_description());
			pst.setInt(8,uF.parseToInt(getF_org()));
			pst.setInt(9, uF.parseToInt(getLocation()));
			pst.setInt(10, uF.parseToInt(getStrBook_quantity()));
			pst.setInt(11, uF.parseToInt(strSessionEmpId));
			pst.setDate(12, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(13, uF.parseToInt(strSessionEmpId));
			pst.setDate(14, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(15, uF.parseToInt(getStrBook_quantity()));
		
			pst.executeUpdate();
			pst.close();
			
			String book_Id = null;
			pst = con.prepareStatement("select max(book_id) as book_id from book_details");
			rs = pst.executeQuery();
			while(rs.next()) {
				book_Id = rs.getString("book_id");
			}
			rs.close();
			pst.close();
			
			uploadImage(book_Id);
						
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	private void uploadImage(String bookId) {
		try {
			UploadImage uI = new UploadImage();
			uI.setServletRequest(request);
			uI.setImageType("BOOK_IMAGE");
			uI.setEmpImage(getStrBookImage());
			uI.setEmpImageFileName(getStrBookImageFileName());
			uI.setEmpId(strSessionEmpId);
			uI.setBookId(bookId);
			uI.setCF(CF);
			uI.upoadImage();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void deleteBook(UtilityFunctions uF){
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from book_details where book_id=?");
			pst.setInt(1, uF.parseToInt(getBookId()));
			rs = pst.executeQuery();
			String book_image = null;
			String addedBy = null;
			while(rs.next()) {
			    book_image = rs.getString("book_image");
			    addedBy = rs.getString("added_by");
			}
			rs.close();
			pst.close();
			
			String strFilePath = null;
			if(CF.getStrDocSaveLocation()==null) {
					strFilePath = DOCUMENT_LOCATION +"/"+addedBy+"/"+getBookId()+"/"+"/"+book_image;
			} else {
					strFilePath = CF.getStrDocSaveLocation()+I_BOOKS+"/"+addedBy+"/"+getBookId() +"/"+book_image;
			}
			File file = new File(strFilePath);
			file.delete();
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("delete from book_details where book_id = ?");
					
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1,uF.parseToInt(getBookId()));
			pst.execute();
			pst.close();
						
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
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

	public String getStrBookTitle() {
		return strBookTitle;
	}

	public void setStrBookTitle(String strBookTitle) {
		this.strBookTitle = strBookTitle;
	}

	public String getStrBookAuthor() {
		return strBookAuthor;
	}

	public void setStrBookAuthor(String strBookAuthor) {
		this.strBookAuthor = strBookAuthor;
	}

	public String getStrBookPublisher() {
		return strBookPublisher;
	}

	public void setStrBookPublisher(String strBookPublisher) {
		this.strBookPublisher = strBookPublisher;
	}

	public String getStrBook_year_published() {
		return strBook_year_published;
	}

	public void setStrBook_year_published(String strBook_year_published) {
		this.strBook_year_published = strBook_year_published;
	}

	public String getStrBook_isbn_no() {
		return strBook_isbn_no;
	}

	public void setStrBook_isbn_no(String strBook_isbn_no) {
		this.strBook_isbn_no = strBook_isbn_no;
	}

	public String getStrBookCategory() {
		return strBookCategory;
	}

	public void setStrBookCategory(String strBookCategory) {
		this.strBookCategory = strBookCategory;
	}

	public String getStrBook_short_description() {
		return strBook_short_description;
	}

	public void setStrBook_short_description(String strBook_short_description) {
		this.strBook_short_description = strBook_short_description;
	}

	public String getStrBook_quantity() {
		return strBook_quantity;
	}

	public void setStrBook_quantity(String strBook_quantity) {
		this.strBook_quantity = strBook_quantity;
	}

	public File getStrBookImage() {
		return strBookImage;
	}

	public void setStrBookImage(File strBookImage) {
		this.strBookImage = strBookImage;
	}

	public String getStrBookImageFileName() {
		return strBookImageFileName;
	}

	public void setStrBookImageFileName(String strBookImageFileName) {
		this.strBookImageFileName = strBookImageFileName;
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

	public List<FillYears> getYearsList() {
		return yearsList;
	}

	public void setYearsList(List<FillYears> yearsList) {
		this.yearsList = yearsList;
	}

	public String getBookId() {
		return bookId;
	}

	public void setBookId(String bookId) {
		this.bookId = bookId;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
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

	public String getIssuedQuantity() {
		return issuedQuantity;
	}

	public void setIssuedQuantity(String issuedQuantity) {
		this.issuedQuantity = issuedQuantity;
	}

	
}
