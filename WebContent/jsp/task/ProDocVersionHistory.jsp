<%@page import="java.net.URLEncoder"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>


<div id="tblDiv" style="float: left; width: 77%; border: 1px solid #CCCCCC; padding: 5px;margin-left: 23px;">
	<h2>Versions</h2>
	<%
	UtilityFunctions uF = new UtilityFunctions();
	String filePath = (String) request.getAttribute("filePath");
	String fileDir = (String) request.getAttribute("fileDir");
	
	String type = (String) request.getAttribute("type");
	
	Map<String, String> hmProDocumentDetails = (Map<String, String>) request.getAttribute("hmProDocumentDetails");
	List<Map<String, String>> alVersion = (List<Map<String, String>>)request.getAttribute("alVersion");
	if(alVersion == null) alVersion = new ArrayList<Map<String,String>>();
	Map<String, String> hmFileIcon = (Map<String, String>) request.getAttribute("hmFileIcon");
	if(hmFileIcon == null) hmFileIcon = new HashMap<String, String>();
	
	List<String> availableExt = (List<String>)request.getAttribute("availableExt");
	if(availableExt == null) availableExt = new ArrayList<String>();
	
	
		/* String fileIcon = request.getContextPath()+"/images1/icons/icons/file_icon.png";
		if(hmFileIcon.containsKey(hmProDocumentDetails.get("FILE_EXTENSION"))){ 
			fileIcon = request.getContextPath()+"/images1/file_icon/"+hmFileIcon.get(hmProDocumentDetails.get("FILE_EXTENSION"));
		}
		boolean flag = false;
		if(availableExt.contains(hmProDocumentDetails.get("FILE_EXTENSION"))){
			flag = true;
		} */
		for(int i = 0; i < alVersion.size(); i++){ 
			Map<String, String> hmVersionHistory = (Map<String, String>) alVersion.get(i);
			String fileIcon = request.getContextPath()+"/images1/icons/icons/file_icon.png";
			if(hmFileIcon.containsKey(hmVersionHistory.get("FILE_EXTENSION"))){
				fileIcon = request.getContextPath()+"/images1/file_icon/"+hmFileIcon.get(hmVersionHistory.get("FILE_EXTENSION"));
			}
			String strFilePath=fileDir+"/"+hmVersionHistory.get("DOCUMENT_NAME");
			String action = "ProjectDocumentFact.action?clientId="+hmVersionHistory.get("CLIENT_ID")+"&proId="+hmVersionHistory.get("PRO_ID")+"&folderName="+hmVersionHistory.get("DOCUMENT_NAME")+"&proFolderId="+hmVersionHistory.get("PRO_DOCUMENT_ID")+"&type="+type+"&filePath="+URLEncoder.encode(strFilePath)+"&fileDir="+URLEncoder.encode(fileDir);
	%>
		<div style="float: left; width: 100%;margin-top:10px;">
			<div style="float: left; width: 35%; margin-right: 9px;">
				<a target="_blank" href="<%=action %>" style="font-weight: normal; color: black;" title="View Document"> 
					<img height="18" width="18" src="<%=fileIcon %>" />&nbsp;<%=hmVersionHistory.get("DOCUMENT_NAME") %>
				</a>
				<div style="float: left; color: gray; width: 90%; margin-top: -5px; margin-left: 25px;"><%=hmVersionHistory.get("FILE_SIZE") %></div>
				<div style="float: left; color: gray; width: 90%; margin-top: -5px; margin-left: 25px;"><%=hmVersionHistory.get("DESCRIPTION") %></div>
				<div style="float: left; color: gray; width: 90%; margin-top: -5px; margin-left: 25px;"><%=hmVersionHistory.get("ENTRY_DATE") %></div>
				<div style="float: left; color: gray; width: 90%; margin-top: -5px; margin-left: 25px;">
					<span style="float: left;">
						<a target="_blank" href="<%=action %>" style="font-weight: normal;" title="View Document">
							View File
						</a>
					|</span>
					<span style="float: left; margin-left: 2px;"><a href="<%=fileDir+"/"+hmVersionHistory.get("DOCUMENT_NAME") %>" style="font-weight: normal;">Download</a></span>
				</div>
				
			</div>
			<div style="float: left; width: 15%; margin-right: 9px;">
				<div style="float: left; width: 100%;">Version <strong><%=uF.parseToInt(hmVersionHistory.get("DOC_VERSION")) > 0 ? hmVersionHistory.get("DOC_VERSION") : "1" %></strong> </div>
			</div>
			<div style="float: left; width: 23%; margin-right: 9px;">
				<div style="float: left; width: 100%;">Aligned with</div>
				<div style="float: left; width: 100%; margin-top: -5px;"><%=hmVersionHistory.get("ALIGN") %></div>
			</div>
			<div style="float: left; width: 15%;">
				<div style="float: left; width: 100%; margin-right: 9px;">Shared with</div>
				<div style="float: left; width: 100%; margin-top: -5px;"><%=hmVersionHistory.get("SHARING_TYPE") %></div>
			</div>
		</div>
	<%} %>
</div>