package com.konnect.jpms.task;

import java.io.File;
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

import org.apache.commons.io.FilenameUtils;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillEmployee;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class CopyDocument extends ActionSupport implements ServletRequestAware, IStatements {
	private static final long serialVersionUID = 1L;
  
	String msg;
	CommonFunctions CF; 
	HttpSession session1;
	String strSessionEmpId;
	String strOrgId;
	String strUserType;
	
	String folderName;
	String strOrg; 
	String operation;
	String strId;
	String type;
	
	String proCategoryTypeFolder;
	String strFolderDescription;
	String folderSharingType;
	
	String strOrgCategory;
	String strOrgProject;
	String[] strOrgResources;

	List<FillEmployee> resourceList;
	
	String filePath;
	String fileDir;
	File strFolderDoc;
	String strFolderDocFileName;
	String strFolderScopeDoc;
	String isFolderDocEdit;
	String isFolderDocDelete;
	String folderDocEdit;
	String folderDocDelete;
	
	String existPath;
	String fromPage;
	
	public String execute() throws Exception {
		session1 = request.getSession();
		strSessionEmpId = (String)session1.getAttribute(EMPID);
		strOrgId = (String)session1.getAttribute(ORGID);
		CF = (CommonFunctions) session1.getAttribute(CommonFunctions);
		if (CF == null) return LOGIN;
		
		strUserType = (String) session1.getAttribute(BASEUSERTYPE);
		request.setAttribute("strUserType", strUserType);
		
		UtilityFunctions uF = new UtilityFunctions();
//		System.out.println("getOperation() ===>> " + getOperation());
		if(getOperation() != null && getOperation().equals("U")) {
			copyDoc(uF);
			return SUCCESS;
		}
		
		getFolderAndDocuments(uF);
		
		return LOAD;
	}

	private void copyDoc(UtilityFunctions uF) {

		Database db = new Database();
		
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			con = db.makeConnection(con);
			String orgName = CF.getStrOrgName();
			request.setAttribute("orgName", orgName);
			
			String proDocMainPath = CF.getProjectDocumentFolder();
			request.setAttribute("proDocMainPath", proDocMainPath);
			
			String proDocRetrivePath = CF.getRetriveProjectDocumentFolder();
			request.setAttribute("proDocRetrivePath", proDocRetrivePath);
			request.setAttribute("strOrgId", strOrgId);
			

			pst = con.prepareStatement("select * from project_document_details where pro_document_id=?");
			pst.setInt(1, uF.parseToInt(getStrId()));
			rs = pst.executeQuery();
			Map<String, String> hmProDocumentDetails = new HashMap<String, String>();
			
			while (rs.next()) {
				hmProDocumentDetails.put("PRO_ID", rs.getString("pro_id"));
				hmProDocumentDetails.put("FOLDER_NAME", rs.getString("folder_name"));
				hmProDocumentDetails.put("DOCUMENT_NAME", rs.getString("document_name"));
				hmProDocumentDetails.put("DOCUMENT_SCOPE", rs.getString("scope_document"));
				hmProDocumentDetails.put("ALIGN_WITH", rs.getString("align_with"));
				hmProDocumentDetails.put("SHARING_TYPE", rs.getString("sharing_type"));
				hmProDocumentDetails.put("SHARING_RESOURCES", rs.getString("sharing_resources"));
				
				hmProDocumentDetails.put("CATEGORY", rs.getString("project_category"));
				hmProDocumentDetails.put("DESCRIPTION", rs.getString("description"));
				
				hmProDocumentDetails.put("EDIT_STATUS", uF.parseToBoolean(rs.getString("is_edit")) == true ? "checked" : "");
				hmProDocumentDetails.put("DELETE_STATUS", uF.parseToBoolean(rs.getString("is_delete")) == true ? "checked" : "");
				
				hmProDocumentDetails.put("EDIT_STATUS_VAL", uF.parseToBoolean(rs.getString("is_edit")) == true ? "1" : "0");
				hmProDocumentDetails.put("DELETE_STATUS_VAL", uF.parseToBoolean(rs.getString("is_delete")) == true ? "1" : "0");
				
				hmProDocumentDetails.put("FILE_SIZE", rs.getString("file_size"));
				hmProDocumentDetails.put("FILE_TYPE", rs.getString("file_type"));
				hmProDocumentDetails.put("SIZE_IN_BYTES", rs.getString("size_in_bytes"));
			}
			rs.close();
			pst.close();
			
//			System.out.println("exist path======>"+getExistPath()); 
			
			boolean flag = false;
			File existFile = new File(getExistPath());
			
	    	//make sure directory exists
//	    	if(directory.exists()) {
			if(existFile.isFile() && existFile.exists()) {
	    		flag = true;
	    	}

	    	if(flag){
//	    		System.out.println("exist path flag ======>"+flag);
	    		List<String> alEmployee = null;
				if(getStrOrgResources() != null) {
					alEmployee = Arrays.asList(getStrOrgResources());
				}
				StringBuilder sbEmps = null;
				
				for(int a=0; alEmployee != null && a<alEmployee.size(); a++) {
					if(alEmployee.get(a) != null && !alEmployee.get(a).trim().equals("")) {
						if(sbEmps == null) {
							sbEmps = new StringBuilder();
							sbEmps.append(","+ alEmployee.get(a).trim() +",");
						} else {
							sbEmps.append(alEmployee.get(a).trim() +",");
						}
					}
				}
				if(sbEmps == null) {
					sbEmps = new StringBuilder();
				}
	    		
				String mainPathWithOrg = CF.getProjectDocumentFolder()+strOrgId;
				File fileOrg = new File(mainPathWithOrg);
				if (!fileOrg.exists()) {
					if (fileOrg.mkdir()) {
						System.out.println("Org Directory is created!");
					}
				}
				
				String mainProPath = mainPathWithOrg+"/Projects";
				File file = new File(mainProPath);
				if (!file.exists()) { 
					if (file.mkdir()) {
						System.out.println("Projects Directory is created!");
					}
				}
				
				String mainCatPath = mainPathWithOrg+"/Categories";
				File fileCat = new File(mainCatPath);
				if (!fileCat.exists()) {
					if (fileCat.mkdir()) {
						System.out.println("Category Directory is created!");
					}
				}
				String docWithCatORProName = null;
				String folderWithCatORProName = null;
				
				String proNameFolder = null;
				if(uF.parseToInt(getProCategoryTypeFolder()) == 1) {
					docWithCatORProName = CF.getProjectNameById(con, getStrOrgProject())+ " project";
					proNameFolder = mainProPath +"/"+getStrOrgProject();
					File file1 = new File(proNameFolder);
					if (!file1.exists()) {
						if (file1.mkdir()) {
							System.out.println("pro_id Directory is created!");
						}
					} 
				} else if(uF.parseToInt(getProCategoryTypeFolder()) == 2) {
					docWithCatORProName = CF.getProjectCategory(con, uF, getStrOrgCategory())+ " category";
					proNameFolder = mainCatPath;
					File file1 = new File(proNameFolder);
					if (!file1.exists()) {
						if (file1.mkdir()) {
							System.out.println("cat_id Directory is created!");
						}
					}
				}
				String strNewFilePath = ""; 
				String strCheckFile = proNameFolder+"/"+hmProDocumentDetails.get("DOCUMENT_NAME"); 
				File f = new File(strCheckFile);
			    if(f.isFile() && f.exists()){
			    	String strCopyFile = uF.getCopyFile(proNameFolder+"/Copy_"+hmProDocumentDetails.get("DOCUMENT_NAME"),proNameFolder,hmProDocumentDetails.get("DOCUMENT_NAME"),0);
//			    	System.out.println("strCopyFile======>"+strCopyFile);
					strNewFilePath = strCopyFile;
				} else {
					strNewFilePath = proNameFolder+"/"+hmProDocumentDetails.get("DOCUMENT_NAME");
				}
//			    System.out.println("strNewFilePath======>"+strNewFilePath);
			    Process process = null;
			    process = Runtime.getRuntime().exec("cp "+getExistPath()+" "+strNewFilePath);
			    if(process != null){
			    	System.out.println("process is not null");
			    	
			    	File fName = new File(strNewFilePath);
			    	
			    	pst = con.prepareStatement("insert into project_document_details(client_id,pro_id,added_by,entry_date,folder_file_type," +
							"pro_folder_id,align_with,sharing_type,sharing_resources,project_category,scope_document,description,doc_parent_id," +
							"is_edit,is_delete,folder_name,document_name,file_size, file_type, size_in_bytes,doc_version) " +
							"values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?)");
						pst.setInt(1, 0);
						if(uF.parseToInt(getProCategoryTypeFolder()) == 1) {
							pst.setInt(2, uF.parseToInt(getStrOrgProject()));
						} else {
							pst.setInt(2, 0);
						}
						pst.setInt(3, uF.parseToInt(strSessionEmpId));
						pst.setTimestamp(4, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone())+"", DBDATE+DBTIME));
//						pst.setDate(4, uF.getCurrentDate(CF.getStrTimeZone()));
						pst.setString(5, "file");
						pst.setInt(6, 0);
						if(uF.parseToInt(getProCategoryTypeFolder()) == 1) {
							pst.setInt(7, 0);
						} else {
							pst.setInt(7, uF.parseToInt(getStrOrgCategory()));
						}
						pst.setInt(8, uF.parseToInt(getFolderSharingType()));
						pst.setString(9, sbEmps.toString());
						pst.setInt(10, uF.parseToInt(getProCategoryTypeFolder())); 
						pst.setString(11, getStrFolderScopeDoc());
						pst.setString(12, getStrFolderDescription());
						pst.setInt(13, 0);
						pst.setBoolean(14, uF.parseToBoolean(getFolderDocEdit()));
						pst.setBoolean(15, uF.parseToBoolean(getFolderDocDelete()));
						pst.setString(16, fName.getName());
						pst.setString(17, fName.getName());
						pst.setString(18, hmProDocumentDetails.get("FILE_SIZE"));
						pst.setString(19, hmProDocumentDetails.get("FILE_TYPE"));
						pst.setString(20, hmProDocumentDetails.get("SIZE_IN_BYTES"));
						pst.setInt(21, 1);
						System.out.println("pst====>"+pst);
						pst.execute();
						pst.close();
						
						
						
						session1.setAttribute(MESSAGE, SUCCESSM+hmProDocumentDetails.get("DOCUMENT_NAME")+" copied successfully."+END);
						
			    } else {
			    	session1.setAttribute(MESSAGE, ERRORM+hmProDocumentDetails.get("DOCUMENT_NAME")+" did not copy."+END);
			    }
	    	} else {
		    	session1.setAttribute(MESSAGE, ERRORM+hmProDocumentDetails.get("DOCUMENT_NAME")+" did not copy."+END);
		    }
			
			
		} catch (Exception e) {
			session1.setAttribute(MESSAGE, ERRORM+"Document did not copy."+END);
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

//	private String getCopyFile(String strFileName,String proNameFolder, String fileName, int cnt) {
//		File f = new File(strFileName);
//		boolean flag = false;
//	    if(f.isFile() && f.exists()){
//	    	cnt++;
//	    	strFileName = proNameFolder+"/Copy("+cnt+")_"+fileName;
//	    	flag = true;
//	    }
//		return flag ? getCopyFile(strFileName,proNameFolder,fileName,cnt) : strFileName;
//	}

	public void getFolderAndDocuments(UtilityFunctions uF) {

		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			con = db.makeConnection(con);
			String orgName = CF.getStrOrgName();
			request.setAttribute("orgName", orgName);
			
			String proDocMainPath = CF.getProjectDocumentFolder();
			request.setAttribute("proDocMainPath", proDocMainPath);
			
			String proDocRetrivePath = CF.getRetriveProjectDocumentFolder();
			request.setAttribute("proDocRetrivePath", proDocRetrivePath);
			request.setAttribute("strOrgId", strOrgId);
			
			Map<String, String> hmFileIcon = CF.getFileIcon();
			request.setAttribute("hmFileIcon",hmFileIcon);
			

			pst = con.prepareStatement("select * from project_document_details where pro_document_id=?");
			pst.setInt(1, uF.parseToInt(getStrId()));
			rs = pst.executeQuery();
			Map<String, String> hmProDocumentDetails = new HashMap<String, String>();
			
			while (rs.next()) {
				setFolderName(rs.getString("folder_name"));
				hmProDocumentDetails.put("PRO_ID", rs.getString("pro_id"));
				hmProDocumentDetails.put("FOLDER_NAME", rs.getString("folder_name"));
				hmProDocumentDetails.put("DOCUMENT_NAME", rs.getString("document_name"));
				hmProDocumentDetails.put("DOCUMENT_SCOPE", rs.getString("scope_document"));
				hmProDocumentDetails.put("ALIGN_WITH", rs.getString("align_with"));
				hmProDocumentDetails.put("SHARING_TYPE", rs.getString("sharing_type"));
				hmProDocumentDetails.put("SHARING_RESOURCES", rs.getString("sharing_resources"));
				
				hmProDocumentDetails.put("CATEGORY", rs.getString("project_category"));
				hmProDocumentDetails.put("DESCRIPTION", rs.getString("description"));
				
				hmProDocumentDetails.put("EDIT_STATUS", uF.parseToBoolean(rs.getString("is_edit")) == true ? "checked" : "");
				hmProDocumentDetails.put("DELETE_STATUS", uF.parseToBoolean(rs.getString("is_delete")) == true ? "checked" : "");
				
				hmProDocumentDetails.put("EDIT_STATUS_VAL", uF.parseToBoolean(rs.getString("is_edit")) == true ? "1" : "0");
				hmProDocumentDetails.put("DELETE_STATUS_VAL", uF.parseToBoolean(rs.getString("is_delete")) == true ? "1" : "0");
				
				hmProDocumentDetails.put("DOC_VERSION", rs.getString("doc_version"));
				String extenstion = null;
				if(rs.getString("document_name") !=null && !rs.getString("document_name").trim().equals("")){
					extenstion = FilenameUtils.getExtension(rs.getString("document_name").trim());
				}
				hmProDocumentDetails.put("FILE_EXTENSION", extenstion);
			}
			rs.close();
			pst.close();
			request.setAttribute("hmProDocumentDetails", hmProDocumentDetails);
//			System.out.println("hmProDocumentDetails ======>> " + hmProDocumentDetails);
			
			int categoryId = 0;
			String strProjectSelect = "";
			String strOtherSelect = "";
			if(uF.parseToInt(hmProDocumentDetails.get("CATEGORY")) == 1) {
				strProjectSelect = "selected";
				strOtherSelect = "";
			} else if(uF.parseToInt(hmProDocumentDetails.get("CATEGORY")) == 2) {
				strProjectSelect = "";
				strOtherSelect = "selected";
				categoryId = uF.parseToInt(hmProDocumentDetails.get("ALIGN_WITH"));
			}
			
			StringBuilder sbProCategoryTypeFolder = new StringBuilder("<option value=\"1\" "+strProjectSelect+">Project</option><option value=\"2\" "+strOtherSelect+">Category</option>");
			
			request.setAttribute("sbProCategoryTypeFolder", sbProCategoryTypeFolder.toString());
			
			
			
			StringBuilder sbOrgProjects = new StringBuilder();
			pst = con.prepareStatement("select * from projectmntnc where org_id=? order by pro_name");
			pst.setInt(1, uF.parseToInt(strOrgId));
			rs = pst.executeQuery();
			while (rs.next()) {
				sbOrgProjects.append("<option value='"+rs.getString("pro_id")+"'");
				if(uF.parseToInt(hmProDocumentDetails.get("PRO_ID")) == rs.getInt("pro_id")) {
					sbOrgProjects.append(" selected");
				}
				sbOrgProjects.append(">"+rs.getString("pro_name")+"</option>");
			}
			rs.close();
			pst.close();
			request.setAttribute("sbOrgProjects", sbOrgProjects.toString());
//			System.out.println("sbOrgProjects ======>> " + sbOrgProjects);

			StringBuilder sbOrgCategory = new StringBuilder();
			pst = con.prepareStatement("select * from project_category_details where org_id=? and project_category_id>1 order by project_category");
			pst.setInt(1, uF.parseToInt(strOrgId));
			rs = pst.executeQuery();
			while (rs.next()) {
				sbOrgCategory.append("<option value='"+rs.getString("project_category_id")+"'");
				if(categoryId == rs.getInt("project_category_id")) {
					sbOrgCategory.append(" selected");
				}
				sbOrgCategory.append(">"+rs.getString("project_category")+"</option>");
			}
			rs.close();
			pst.close();
			request.setAttribute("sbOrgCategory", sbOrgCategory.toString());
//			System.out.println("sbOrgCategory ======>> " + sbOrgCategory);
			
			
			StringBuilder sbProSharingType = new StringBuilder();
			sbProSharingType.append("<option value=\"0\"");
			if(hmProDocumentDetails.get("SHARING_TYPE") != null && hmProDocumentDetails.get("SHARING_TYPE").equals("0")) { 
				sbProSharingType.append(" selected"); 
			}
			sbProSharingType.append(">Public</option>");
			sbProSharingType.append("<option value=\"1\"");
			if(hmProDocumentDetails.get("SHARING_TYPE") != null && hmProDocumentDetails.get("SHARING_TYPE").equals("1")) { 
				sbProSharingType.append(" selected"); 
			}
			sbProSharingType.append(">Private Team</option>");
			sbProSharingType.append("<option value=\"2\"");
			if(hmProDocumentDetails.get("SHARING_TYPE") != null && hmProDocumentDetails.get("SHARING_TYPE").equals("2")) { 
				sbProSharingType.append(" selected"); 
			}
			sbProSharingType.append(">Individual Resource</option>");
			request.setAttribute("sbProSharingType", sbProSharingType.toString());
			
			
			List<String> existResourceList = new ArrayList<String>();
			
			if(hmProDocumentDetails.get("SHARING_RESOURCES") != null && !hmProDocumentDetails.get("SHARING_RESOURCES").trim().equals("")) {
				existResourceList = Arrays.asList(hmProDocumentDetails.get("SHARING_RESOURCES").split(","));
			}
			resourceList = new FillEmployee(request).fillEmployeeName(null, null, uF.parseToInt(strOrgId), 0, session1);
			StringBuilder sbOrgResources= new StringBuilder();
			for(int i=0; resourceList!=null && i<resourceList.size(); i++) {
				sbOrgResources.append("<option value='"+resourceList.get(i).getEmployeeId()+"'");
				if(existResourceList.contains(resourceList.get(i).getEmployeeId())) {
					sbOrgResources.append(" selected");
				}
				sbOrgResources.append(">"+resourceList.get(i).getEmployeeName()+"</option>");
			}
			request.setAttribute("sbOrgResources", sbOrgResources.toString());
			
			
		} catch (Exception e) {
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

	public String getFolderSharingType() {
		return folderSharingType;
	}

	public void setFolderSharingType(String folderSharingType) {
		this.folderSharingType = folderSharingType;
	}

	public String getFolderName() {
		return folderName;
	}

	public void setFolderName(String folderName) {
		this.folderName = folderName;
	}

	public String getStrOrg() {
		return strOrg;
	}

	public void setStrOrg(String strOrg) {
		this.strOrg = strOrg;
	}

	public String getStrId() {
		return strId;
	}

	public void setStrId(String strId) {
		this.strId = strId;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getProCategoryTypeFolder() {
		return proCategoryTypeFolder;
	}

	public void setProCategoryTypeFolder(String proCategoryTypeFolder) {
		this.proCategoryTypeFolder = proCategoryTypeFolder;
	}

	public String getStrFolderDescription() {
		return strFolderDescription;
	}

	public void setStrFolderDescription(String strFolderDescription) {
		this.strFolderDescription = strFolderDescription;
	}

	public List<FillEmployee> getResourceList() {
		return resourceList;
	}

	public void setResourceList(List<FillEmployee> resourceList) {
		this.resourceList = resourceList;
	}

	public String getStrOrgCategory() {
		return strOrgCategory;
	}

	public void setStrOrgCategory(String strOrgCategory) {
		this.strOrgCategory = strOrgCategory;
	}

	public String getStrOrgProject() {
		return strOrgProject;
	}

	public void setStrOrgProject(String strOrgProject) {
		this.strOrgProject = strOrgProject;
	}

	public String[] getStrOrgResources() {
		return strOrgResources;
	}

	public void setStrOrgResources(String[] strOrgResources) {
		this.strOrgResources = strOrgResources;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getFileDir() {
		return fileDir;
	}

	public void setFileDir(String fileDir) {
		this.fileDir = fileDir;
	}

	public File getStrFolderDoc() {
		return strFolderDoc;
	}

	public void setStrFolderDoc(File strFolderDoc) {
		this.strFolderDoc = strFolderDoc;
	}

	public String getStrFolderDocFileName() {
		return strFolderDocFileName;
	}

	public void setStrFolderDocFileName(String strFolderDocFileName) {
		this.strFolderDocFileName = strFolderDocFileName;
	}

	public String getStrFolderScopeDoc() {
		return strFolderScopeDoc;
	}

	public void setStrFolderScopeDoc(String strFolderScopeDoc) {
		this.strFolderScopeDoc = strFolderScopeDoc;
	}

	public String getIsFolderDocEdit() {
		return isFolderDocEdit;
	}

	public void setIsFolderDocEdit(String isFolderDocEdit) {
		this.isFolderDocEdit = isFolderDocEdit;
	}

	public String getIsFolderDocDelete() {
		return isFolderDocDelete;
	}

	public void setIsFolderDocDelete(String isFolderDocDelete) {
		this.isFolderDocDelete = isFolderDocDelete;
	}

	public String getExistPath() {
		return existPath;
	}

	public void setExistPath(String existPath) {
		this.existPath = existPath;
	}

	public String getFolderDocEdit() {
		return folderDocEdit;
	}

	public void setFolderDocEdit(String folderDocEdit) {
		this.folderDocEdit = folderDocEdit;
	}

	public String getFolderDocDelete() {
		return folderDocDelete;
	}

	public void setFolderDocDelete(String folderDocDelete) {
		this.folderDocDelete = folderDocDelete;
	}

	public String getFromPage() {
		return fromPage;
	}

	public void setFromPage(String fromPage) {
		this.fromPage = fromPage;
	}
	
}
