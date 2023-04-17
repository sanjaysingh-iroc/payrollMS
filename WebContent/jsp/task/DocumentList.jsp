<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.Iterator"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="java.util.List"%>
<%@taglib uri="/struts-tags" prefix="s"%>

<style>
.anaAttrib1 {
	font-size: 12px;
	font-family: digital;
	color: #3F82BF;
	font-weight: bold;
	text-align: center;
	height: 22px;
}
</style>

<script type="text/javascript"> 

/* function executeFolderActions(val, cnt, type, strId) {
	alert("strId ===>> " + strId);
		if(val == '1') {
			editFolder(strId, type);
		} else if(val == '2') {
			
		}
	} */
	
</script>

<div class="box box-body">
	<%
		UtilityFunctions uF = new UtilityFunctions();
		List alProDocuments = (List)request.getAttribute("alProDocuments");
		List alTaskDocuments = (List)request.getAttribute("alTaskDocuments");
		String strUserType = (String)session.getAttribute(IConstants.BASEUSERTYPE);
		String type = (String)request.getAttribute("type");
		
		Map<String, String> hmFileIcon = (Map<String, String>) request.getAttribute("hmFileIcon");
		if(hmFileIcon == null) hmFileIcon = new HashMap<String, String>();
	%>
	<form id="formProjectDocuments" class="formcss" action="ProDocumentList.action" name="frmProjectList" method="post" enctype="multipart/form-data" onsubmit="showLoading();">
		<div style="float: left;width: 100%; overflow-x: auto;" id="tblDiv">
			<s:hidden name="proId"></s:hidden>
			<s:hidden name="fromPage"></s:hidden>
			<table class="table_style" >
				<tr>
					<!-- <td class="txtlabel alignRight" valign="top">&nbsp;</td> -->
					<td class="txtlabel" colspan="2">
						<input type="hidden" name="folderDocscount" id="folderDocscount" value="0" />
						<a class="add_lvl" href="javascript:void(0);" onclick="addNewFolder('documentTable', 'folderDocscount', ''), showTblHeader();">Add Folder</a> 
						<a class="add_lvl" href="javascript:void(0);" onclick="addNewDocs('documentTable', 'folderDocscount', ''), showTblHeader();">Add Document</a>
					</td>
				</tr>
				<tr>
					<td colspan="2">
						<table class="table_style" id="documentTable" style="width: 100%;">
							<tr id="folderTR0" style="display: none;">
								<td style="width: 50%;">Folder/Documents</td>
								<!-- <td style="width: 20%;">Scope Document</td> -->
								<td style="width: 12%;">Category</td>
								<td style="width: 12%;">Alignment</td>
								<td style="min-width: 19%;">Sharing</td>
								<td >Rights</td>
							</tr>
						</table>
						<div id="submitDiv_0" style="float: left; width: 100%; text-align: center; margin: 5px; padding: 10px; border-top: 1px solid lightgray;display: none;">
							<input type="submit" value="Save" class="btn btn-primary" style="margin-right: 5px;" name="btnSave"/>
							<input type="button" value="Cancel" class="btn btn-danger" style="margin-right: 5px;" name="cancel" onclick="cancelFolderAndDocsBlock('documentTable');">
						</div>
					</td>
				</tr>
				<tr>
					<td colspan="2">
					<% if(type != null && (type.equalsIgnoreCase("CSF") || type.equalsIgnoreCase("PSF") || type.equalsIgnoreCase("ASF"))) { %>
					
						<div style="float: left; width: 100%;">  <!-- border: 1px solid gray; --> 
							<% 	
								//Map<String, List<Map<String,String>>> hmFolder = (Map<String, List<Map<String,String>>>)request.getAttribute("hmFolder");
								Map<String, List<Map<String,String>>> hmSubFolder = (Map<String, List<Map<String,String>>>)request.getAttribute("hmSubFolder");
								Map<String, List<Map<String,String>>> hmSubDoc = (Map<String, List<Map<String,String>>>)request.getAttribute("hmSubDoc");
								if(hmSubDoc == null) hmSubDoc = new HashMap<String, List<Map<String,String>>>();
								
								String proDocMainPath = (String) request.getAttribute("proDocMainPath");
								String proDocRetrivePath = (String) request.getAttribute("proDocRetrivePath");
								String strOrgId = (String) request.getAttribute("strOrgId");
								String folderName = (String) request.getAttribute("folderName");
								String categoryType = (String) request.getAttribute("categoryType");
								String strProId = (String) request.getAttribute("strProId");
								int nFolder = 0;
								int nSubFolder = 0;
								int nFiles = 0;
								//System.out.println("hmSubFolder --->> " + hmSubFolder);
								if(hmSubFolder != null) {
							%>
								<div style="float: right; border-bottom: 1px solid #CCCCCC; width: 100%; text-align: right; margin-bottom: 10px;">
									<span style="float: right; margin-right: 5px;"><span id="spanFilesId" class="anaAttrib1" style="font-size: 20px; margin-right: 2px;">0</span>Files</span>
									<span style="float: right; margin-right: 5px;"><span id="spanSubFolderId" class="anaAttrib1" style="font-size: 20px; margin-right: 2px;">0</span>Sub Folders</span>
								</div>
								<% 	
								Iterator<String> it = hmSubFolder.keySet().iterator();
								while(it.hasNext()) {
									String proFolderId = it.next();
									//System.out.println("proFolderId --->> " + proFolderId);
									List<Map<String, String>> alSubFolder = (List<Map<String, String>>) hmSubFolder.get(proFolderId); 
										if(alSubFolder == null) alSubFolder = new ArrayList<Map<String,String>>();
										
										List<Map<String, String>> alFolderDoc = (List<Map<String, String>>) hmSubDoc.get(proFolderId); 
										if(alFolderDoc == null) alFolderDoc = new ArrayList<Map<String,String>>();
									%>
												<%
												//System.out.println("alSubFolder --->> " + alSubFolder);
												for(int j = 0; j < alSubFolder.size(); j++) {
													Map<String, String> hmInnerSubFolder = (Map<String, String>) alSubFolder.get(j);
													List<Map<String, String>> alDoc = (List<Map<String, String>>) hmSubDoc.get(hmInnerSubFolder.get("PRO_DOCUMENT_ID")); 
													if(alDoc == null) alDoc = new ArrayList<Map<String,String>>();
													
													String subFolderSavePath = proDocMainPath+strOrgId+"/Projects/"+strProId+"/"+folderName+"/"+hmInnerSubFolder.get("FOLDER_NAME");
													if(uF.parseToInt(strProId) == 0 && uF.parseToInt(categoryType) == 2) {
														subFolderSavePath = proDocMainPath+strOrgId+"/Categories/"+folderName+"/"+hmInnerSubFolder.get("FOLDER_NAME");
													}
													
													nSubFolder++;
													
													String fileSubCount ="-";
													if(alDoc !=null && alDoc.size() > 0) {
														fileSubCount = alDoc.size()+"";
													}
												%>
													<div id="folderTR_0_<%=j %>"  style="float: left; width: 100%;">
														
														<div style="float: left; width: 340px; margin-right: 9px;">
															<input type="hidden" name="hideFolder_0_<%=j %>" id="hideFolder_0_<%=j %>" value="0" /> 
															<div style="float: left; margin-right: 5px;">
																<a href="javascript:void(0);" style="font-weight: normal; color: black;" onclick="openCloseDocs1('0','<%=j %>','<%=alDoc.size() %>');"> 
																<%--  	<img height="15" width="20" src="<%=request.getContextPath()%>/images1/icons/icons/folder_icon.png" />					 --%>
										 						<i class="fa fa-folder-open-o" style="font-size: 20px;height: 15px;"></i>
										 						</a>
									 						</div>
																				
															<strong><%=hmInnerSubFolder.get("FOLDER_NAME") %></strong>
															<div style="float: left; color: gray; width: 90%; margin-top: -5px; margin-left: 25px;"><%=fileSubCount+" items" %></div>
															<div style="float: left; color: gray; width: 90%; margin-top: -5px; margin-left: 25px;"><%=hmInnerSubFolder.get("DESCRIPTION") %></div>
															<div style="float: left; color: gray; width: 90%; margin-top: -5px; margin-left: 25px;"><%=hmInnerSubFolder.get("ENTRY_DATE") %></div>
															<% if(uF.parseToInt(fileSubCount)>0) { %>
																<div style="float: left; color: gray; width: 90%; margin-top: -5px; margin-left: 25px;">
																<span style="float: left;"><%=fileSubCount+" items" %> view files </span> 
																<a href="javascript:void(0);" style="font-weight: normal; color: black;" onclick="openCloseDocs1('0','<%=j %>','<%=alDoc.size() %>');">
																	<span id="FDDownarrowSpan0_<%=j %>" style="float: left; margin-left: 2px; margin-top: 3px;"> 
																		<i class="fa fa-angle-down" aria-hidden="true" style="width: 14px;"></i> 
																	</span>
																	<span id="FDUparrowSpan0_<%=j %>" style="float: left; margin-left: 2px; margin-top: 3px; display: none;">
																		<i class="fa fa-angle-up" aria-hidden="true" style="width: 14px;"></i>
																	</span>
																</a>
																</div>
															<% } %>
														</div>
														<div style="float: left; width: 120px; margin-right: 9px;">
															<div style="float: left; width: 100%;">Aligned with</div>
															<div style="float: left; width: 100%; margin-top: -5px;"><%=hmInnerSubFolder.get("ALIGN") %></div>
														</div>
														<div style="float: left; width: 120px; margin-right: 9px;">
															<div style="float: left; width: 100%;">Shared with</div>
															<div style="float: left; width: 100%; margin-top: -5px;"><%=hmInnerSubFolder.get("SHARING_TYPE") %></div>
														</div>
														<div style="float: left; width: 90px;">
															<div style="float: left; width: 100%;">Actions</div>
															<div style="float: left; width: 100%;">
																<select name="subFolderAction0_<%=j %>" id="subFolderAction0_<%=j %>" style="width: 80px !important;" onchange="executeFolderActions(this.value, '0_<%=j %>', 'SF', '<%=hmInnerSubFolder.get("PRO_DOCUMENT_ID") %>', '', '', '<%=hmInnerSubFolder.get("FOLDER_NAME") %>', 'folderTR_0_<%=j %>', '<%=subFolderSavePath %>');">
											                    	<option value="">Actions</option>
											                    	<option value="1">Edit</option>
										                    		<option value="2">Delete</option>
											                    </select>
																<%-- <a href="javascript:void(0)" class="edit_lvl" style="float: right;" onclick="editFolder('<%=hmInnerSubFolder.get("CLIENT_ID")%>','<%=hmInnerSubFolder.get("PRO_ID")%>','<%=hmInnerSubFolder.get("FOLDER_NAME") %>','<%=hmInnerSubFolder.get("PRO_DOCUMENT_ID") %>',2);" title="Edit Folder">Edit</a> --%>
															</div>
														</div>
														<div style="float: left; width: 100%; margin-top: 10px;">
															<span style="float:left; width: 100%;"><a href="javascript:void(0);" style="margin: -15px 0px 15px 50px;" onclick="addNewDocumentInSubFolder('addNewDocsInSubFolder_<%=j %>', 'SF', '<%=hmInnerSubFolder.get("PRO_DOCUMENT_ID") %>', '<%=j %>');"> +Add Document</a></span>
														</div>
														<div id="addNewDocsInSubFolder_<%=j %>" style="float: left; width: 100%; margin-top: 10px; display: none;"></div>
														<%
														//System.out.println("alDoc --->> " + alDoc);
															for(int k = 0; k<alDoc.size(); k++) {
																Map<String, String> hmInnerSubDoc = (Map<String, String>) alDoc.get(k);
																String filePath1 = proDocRetrivePath+strOrgId+"/Projects/"+strProId+"/"+folderName+"/"+hmInnerSubFolder.get("FOLDER_NAME")+"/"+hmInnerSubDoc.get("DOCUMENT_NAME");
																String fileDir = proDocRetrivePath+strOrgId+"/Projects/"+strProId+"/"+folderName+"/"+hmInnerSubFolder.get("FOLDER_NAME");
																String fileSavePath = proDocMainPath+strOrgId+"/Projects/"+strProId+"/"+folderName+"/"+hmInnerSubFolder.get("FOLDER_NAME")+"/"+hmInnerSubDoc.get("DOCUMENT_NAME");

																if(uF.parseToInt(strProId) == 0 && uF.parseToInt(categoryType) == 2) {
																	filePath1 = proDocRetrivePath+strOrgId+"/Categories/"+folderName+"/"+hmInnerSubFolder.get("FOLDER_NAME")+"/"+hmInnerSubDoc.get("DOCUMENT_NAME");
																	fileDir = proDocRetrivePath+strOrgId+"/Categories/"+folderName+"/"+hmInnerSubFolder.get("FOLDER_NAME");
																	fileSavePath = proDocMainPath+strOrgId+"/Categories/"+folderName+"/"+hmInnerSubFolder.get("FOLDER_NAME")+"/"+hmInnerSubDoc.get("DOCUMENT_NAME");
																}
																
																nFiles++;
																
																String fileIcon = request.getContextPath()+"/images1/icons/icons/file_icon.png";
																if(hmFileIcon.containsKey(hmInnerSubDoc.get("FILE_EXTENSION"))){ 
																	fileIcon = request.getContextPath()+"/images1/file_icon/"+hmFileIcon.get(hmInnerSubDoc.get("FILE_EXTENSION"));
																} 
																String action = "ProjectDocumentFact.action?clientId="+hmInnerSubDoc.get("CLIENT_ID")+"&proId="+hmInnerSubDoc.get("PRO_ID")+"&folderName="+hmInnerSubDoc.get("DOCUMENT_NAME")+"&proFolderId="+hmInnerSubDoc.get("PRO_DOCUMENT_ID")+"&type=2&filePath="+URLEncoder.encode(filePath1)+"&fileDir="+URLEncoder.encode(fileDir);
																int nDocVersion = uF.parseToInt(hmInnerSubDoc.get("DOC_VERSION")) > 0 ? uF.parseToInt(hmInnerSubDoc.get("DOC_VERSION")) : 1;
														%>
																<div id="folderTR_0_<%=j %>_<%=k %>" style="float: left; width: 100%; margin-top: 10px; display: none;">
																	<div style="float: left; margin-left: 23px; width: 317px; margin-right: 9px;">
																		<%-- <a href="<%=filePath1 %>" style="font-weight: normal; color: black;"> --%>
																		<%-- <a href="javascript:void(0)" style="font-weight: normal; color: black;"onclick="projectDocFact('<%=hmInnerSubDoc.get("CLIENT_ID")%>','<%=hmInnerSubDoc.get("PRO_ID")%>','<%=hmInnerSubDoc.get("DOCUMENT_NAME") %>','<%=hmInnerSubDoc.get("PRO_DOCUMENT_ID") %>',2,'<%=filePath1 %>','<%=fileDir %>');" title="View Document"> --%>
																		<a target="_blank" href="<%=action %>" style="font-weight: normal; color: black;" title="View Document"> 
																			<img height="18" width="18" src="<%=fileIcon %>" />&nbsp;<%=hmInnerSubDoc.get("DOCUMENT_NAME") %>
																		</a> 
																		<div style="float: left; color: gray; width: 90%; margin-top: -5px; margin-left: 25px;"><%=hmInnerSubDoc.get("FILE_SIZE") %></div>
																		<div style="float: left; color: gray; width: 90%; margin-top: -5px; margin-left: 25px;"><%=hmInnerSubDoc.get("DESCRIPTION") %></div>
																		<div style="float: left; color: gray; width: 90%; margin-top: -5px; margin-left: 25px;"><%=hmInnerSubDoc.get("ENTRY_DATE") %></div>
																		<div style="float: left; color: gray; width: 90%; margin-top: -5px; margin-left: 25px;">
																			<span style="float: left;">
																				<%-- <a href="javascript:void(0)" style="font-weight: normal;"onclick="projectDocFact('<%=hmInnerSubDoc.get("CLIENT_ID")%>','<%=hmInnerSubDoc.get("PRO_ID")%>','<%=hmInnerSubDoc.get("DOCUMENT_NAME") %>','<%=hmInnerSubDoc.get("PRO_DOCUMENT_ID") %>',2,'<%=filePath1 %>','<%=fileDir %>');" title="View Document"> --%>
																				<a target="_blank" href="<%=action %>" style="font-weight: normal;" title="View Document">
																					View File 
																				</a>
																			|</span> 
																			
																			<span style="float: left; margin-left: 2px;"><a href="<%=filePath1 %>" style="font-weight: normal;">Download</a></span>
																		</div>
																		
																	</div>
																	<div style="float: left; width: 120px; margin-right: 9px;">
																		<div style="float: left; width: 100%;">Version <strong><%=nDocVersion %></strong> </div>
																		<div style="float: left; width: 100%; margin-top: -5px;">
																			<%if(nDocVersion > 1){ %>
																				<span style="float: left;">Version history </span>
																				<input type="hidden" name="proDocsSpanStatus" id="proDocsSpanStatus0_<%=j %>_<%=k %>" value = "0"/> 
																				<a href="javascript:void(0);" style="font-weight: normal; color: black;" onclick="viewVersionHistory(<%=hmInnerSubDoc.get("PRO_DOCUMENT_ID") %>,'proDocsSpanStatus0_<%=j %>_<%=k %>','proDocsVersionDiv0_<%=j %>_<%=k %>','VHDownarrowSpan0_<%=j %>_<%=k %>', 'VHUparrowSpan0_<%=j %>_<%=k %>','2','<%=filePath1%>','<%=fileDir %>')">
																					<span id="VHDownarrowSpan0_<%=j %>_<%=k %>" style="float: left; margin-left: 2px; margin-top: 3px;"> 
																						<i class="fa fa-angle-down" aria-hidden="true" style="width: 14px;"></i>
																					</span>
																					<span id="VHUparrowSpan0_<%=j %>_<%=k %>" style="float: left; margin-left: 2px; margin-top: 3px; display: none;">
																						<i class="fa fa-angle-up" aria-hidden="true" style="width: 14px;"></i>
																					</span>
																				</a>
																			<%} %>
																		</div>
																	</div>
																	<div style="float: left; width: 120px; margin-right: 9px;">
																		<div style="float: left; width: 100%;">Aligned with</div>
																		<div style="float: left; width: 100%; margin-top: -5px;"><%=hmInnerSubDoc.get("ALIGN") %></div>
																	</div>
																	<div style="float: left; width: 120px; margin-right: 9px;">
																		<div style="float: left; width: 100%;">Shared with</div>
																		<div style="float: left; width: 100%; margin-top: -5px;"><%=hmInnerSubDoc.get("SHARING_TYPE") %></div>
																	</div>
																	<div style="float: left; width: 90px;">
																		<div style="float: left; width: 100%;">Actions</div>
																		<div style="float: left; width: 100%;">
																			<select name="subFolderAction0_<%=j %>_<%=k %>" id="subFolderAction0_<%=j %>_<%=k %>" style="width: 80px !important;" onchange="executeFolderActions(this.value, '0_<%=j %>_<%=k %>', 'SFD', '<%=hmInnerSubDoc.get("PRO_DOCUMENT_ID") %>', '<%=filePath1 %>', '<%=fileDir %>', '<%=hmInnerSubDoc.get("DOCUMENT_NAME") %>', 'folderTR_0_<%=j %>_<%=k %>' ,'<%=fileSavePath %>');">
														                    	<option value="">Actions</option>
														                    	<option value="1">Edit</option>
													                    		<option value="2">Delete</option>
													                    		<option value="3">Copy</option>
														                    </select>
																			<%-- <a href="javascript:void(0)" class="edit_lvl" style="float: right;" onclick="editDoc('<%=hmInnerSubDoc.get("CLIENT_ID")%>','<%=hmInnerSubDoc.get("PRO_ID")%>','<%=hmInnerSubDoc.get("DOCUMENT_NAME") %>','<%=hmInnerSubDoc.get("PRO_DOCUMENT_ID") %>',2,'<%=filePath1 %>','<%=fileDir %>');" title="Edit Document">Edit</a> --%>
																		</div>
																	</div>
																	
																</div>
																<div id="proDocsVersionDiv0_<%=j %>_<%=k %>" style="float: left; width: 100%; margin-top: 10px; display: none;"></div>
														<%} %>
													</div>
												<% } %>
										<% } %>	
							<% } else { %>
								<div style="float: left; width: width: 96%;" class="nodata msg">
									<span>No documents attached.</span>
								</div>
							<%} %>
							<input type="hidden" name="folderCnt" id="folderCnt" value="<%=nFolder%>"/>
							<input type="hidden" name="subFolderCnt" id="subFolderCnt" value="<%=nSubFolder%>"/>
							<input type="hidden" name="fileCnt" id="fileCnt" value="<%=nFiles%>"/>
							
							<%-- <script type="text/javascript">
								var timer = window.setTimeout(function() {
									getFolderCount();
								}, 400);
							</script> --%>
							
						</div>
						
					<% } else if(type != null && (type.equalsIgnoreCase("CF") || type.equalsIgnoreCase("PF") || type.equalsIgnoreCase("AF"))) { %>
					<div style="float: left; width: 100%;">  <!-- border: 1px solid gray; -->
					<% 	
						//Map<String, List<Map<String,String>>> hmFolder = (Map<String, List<Map<String,String>>>)request.getAttribute("hmFolder");
						List<Map<String, String>> alProFolder = (List<Map<String, String>>) request.getAttribute("alProFolder");
						Map<String, List<Map<String,String>>> hmDoc = (Map<String, List<Map<String,String>>>)request.getAttribute("hmDoc");
						if(hmDoc == null) hmDoc = new HashMap<String, List<Map<String,String>>>();
						Map<String, List<Map<String,String>>> hmSubFolder = (Map<String, List<Map<String,String>>>)request.getAttribute("hmSubFolder");
						if(hmSubFolder == null) hmSubFolder = new HashMap<String, List<Map<String,String>>>();
						Map<String, List<Map<String,String>>> hmSubDoc = (Map<String, List<Map<String,String>>>)request.getAttribute("hmSubDoc");
						if(hmSubDoc == null) hmSubDoc = new HashMap<String, List<Map<String,String>>>();
						
						int nFolder = 0;
						int nSubFolder = 0;
						int nFiles = 0;
						
						String proDocMainPath = (String) request.getAttribute("proDocMainPath");
						String proDocRetrivePath = (String) request.getAttribute("proDocRetrivePath");
						String strOrgId = (String) request.getAttribute("strOrgId");
						if(alProFolder != null) {
					%>
						<div style="float: right; border-bottom: 1px solid #CCCCCC; width: 100%; text-align: right; margin-bottom: 10px;">
							<span style="float: right; margin-right: 5px;"><span id="spanFilesId" class="anaAttrib1" style="font-size: 20px; margin-right: 2px;">0</span>Files</span>
							<span style="float: right; margin-right: 5px;"><span id="spanSubFolderId" class="anaAttrib1" style="font-size: 20px; margin-right: 2px;">0</span>Sub Folders</span>
							<span style="float: right; margin-right: 5px;"><span id="spanFolderId" class="anaAttrib1" style="font-size: 20px; margin-right: 2px;">0</span>Folders</span>
						</div>
						<% 	
								//System.out.println("alProFolder ===>> " + alProFolder.size());
								for(int ii = 0; ii < alProFolder.size(); ii++) {
									//System.out.println("ii ===>> " + ii);
									Map<String, String> hmInnerFolder = (Map<String, String>) alProFolder.get(ii);
									List<Map<String, String>> alSubFolder = (List<Map<String, String>>) hmSubFolder.get(hmInnerFolder.get("PRO_DOCUMENT_ID")); 
									if(alSubFolder == null) alSubFolder = new ArrayList<Map<String,String>>();
									List<Map<String, String>> alFolderDoc = (List<Map<String, String>>) hmSubDoc.get(hmInnerFolder.get("PRO_DOCUMENT_ID")); 
									if(alFolderDoc == null) alFolderDoc = new ArrayList<Map<String,String>>();
									String fileCount ="-";
									int nFoldrCnt = (alSubFolder.size());
									int nFileCnt = (alFolderDoc.size());
									if(nFoldrCnt > 0 || nFileCnt > 0) {
										fileCount = (nFoldrCnt + nFileCnt)+"";
									}
									String folderSavePath = proDocMainPath+strOrgId+"/Projects/"+hmInnerFolder.get("PRO_ID")+"/"+hmInnerFolder.get("FOLDER_NAME");
									if(uF.parseToInt(hmInnerFolder.get("PRO_ID")) == 0 && uF.parseToInt(hmInnerFolder.get("CATEGORY_ID")) == 2) {
										folderSavePath = proDocMainPath+strOrgId+"/Categories/"+hmInnerFolder.get("FOLDER_NAME");
									}
									nFolder++;
							%>
									<div id="folderTR_<%=ii %>" style="float: left; width: 100%; margin-top: 10px;">
										<div style="float: left; width: 340px; margin-right: 9px;">
											<input type="hidden" name="hideFolder_<%=ii %>" id="hideFolder_<%=ii %>" value="0" /> 
											<div style="float: left; margin-right: 5px;"> <a href="javascript:void(0);" style="font-weight: normal; color: black;" onclick="openCloseDocs('<%=ii %>','<%=alSubFolder.size()%>','<%=alFolderDoc.size()%>');"> 
											<%-- <img height="15" width="20" src="<%=request.getContextPath()%>/images1/icons/icons/folder_icon.png" /> </a> </div> --%>
											<i class="fa fa-folder-open-o" style="font-size: 20px;height: 15px;"></i> </a> </div>
											<strong><%=hmInnerFolder.get("FOLDER_NAME") %></strong>
											<div style="float: left; color: gray; width: 90%; margin-top: -5px; margin-left: 25px;"><%=fileCount +" items" %></div>
											<div style="float: left; color: gray; width: 90%; margin-top: -5px; margin-left: 25px;"><%=hmInnerFolder.get("DESCRIPTION") %></div>
											<div style="float: left; color: gray; width: 90%; margin-top: -5px; margin-left: 25px;"><%=hmInnerFolder.get("ENTRY_DATE") %></div>
											<% if(uF.parseToInt(fileCount)>0) { %>
											<div style="float: left; color: gray; width: 90%; margin-top: -5px; margin-left: 25px;">
											<span style="float: left;"><%=fileCount +" items" %> view folders & files </span> 
											<a href="javascript:void(0);" style="font-weight: normal; color: black;" onclick="openCloseDocs('<%=ii %>','<%=alSubFolder.size()%>','<%=alFolderDoc.size()%>');">
												<span id="FDDownarrowSpan<%=ii %>" style="float: left; margin-left: 2px; margin-top: 3px;"> 
													
													<i class="fa fa-angle-down" aria-hidden="true" style="width: 14px;"></i>
												</span>
												<span id="FDUparrowSpan<%=ii %>" style="float: left; margin-left: 2px; margin-top: 3px; display: none;">
													<i class="fa fa-angle-up" aria-hidden="true" style="width: 14px;"></i>
												</span>
											</a>
											</div>
											<% } %>
										</div>
										<div style="float: left; width: 120px; margin-right: 9px;">
											<div style="float: left; width: 100%;">Aligned with</div>
											<div style="float: left; width: 100%; margin-top: -5px;"><%=hmInnerFolder.get("ALIGN") %></div>
										</div>
										<div style="float: left; width: 120px; margin-right: 9px;">
											<div style="float: left; width: 100%;">Shared with</div>
											<div style="float: left; width: 100%; margin-top: -5px;"><%=hmInnerFolder.get("SHARING_TYPE") %></div>
										</div>
										<div style="float: left; width: 90px;">
											<div style="float: left; width: 100%;">Actions</div>
											<div style="float: left; width: 100%;">
												<select name="folderAction<%=ii %>" id="folderAction<%=ii %>" style="width: 80px !important;" onchange="executeFolderActions(this.value, '<%=ii %>', 'F', '<%=hmInnerFolder.get("PRO_DOCUMENT_ID") %>', '', '', '<%=hmInnerFolder.get("FOLDER_NAME") %>', 'folderTR_<%=ii %>', '<%=folderSavePath %>');">
							                    	<option value="">Actions</option>
							                    	<option value="1">Edit</option>
						                    		<option value="2">Delete</option>
							                    </select>
											</div>
										</div>
										<div style="float: left; width: 100%; margin-top: 10px;">
											<span style="float:left; width: 100%;"><a href="javascript:void(0);" style="margin: -15px 0px 15px 50px;" onclick="addNewSubFolderORDocumentInFolder('addNewSubFolderORDocsInFolder_<%=ii %>', 'F', '<%=hmInnerFolder.get("PRO_DOCUMENT_ID") %>', '<%=ii %>');"> +Add Folder/ Document</a></span>
										</div>
										<div id="addNewSubFolderORDocsInFolder_<%=ii %>" style="float: left; width: 100%; margin-top: 10px; display: none;"></div>
														
										<%
										for(int j = 0; j < alSubFolder.size(); j++){
											Map<String, String> hmInnerSubFolder = (Map<String, String>) alSubFolder.get(j);
											List<Map<String, String>> alDoc = (List<Map<String, String>>) hmSubDoc.get(hmInnerSubFolder.get("PRO_DOCUMENT_ID")); 
											if(alDoc == null) alDoc = new ArrayList<Map<String,String>>();
											
											String subFolderSavePath = proDocMainPath+strOrgId+"/Projects/"+hmInnerFolder.get("PRO_ID")+"/"+hmInnerFolder.get("FOLDER_NAME")+"/"+hmInnerSubFolder.get("FOLDER_NAME");
											if(uF.parseToInt(hmInnerFolder.get("PRO_ID")) == 0 && uF.parseToInt(hmInnerFolder.get("CATEGORY_ID")) == 2) {
												subFolderSavePath = proDocMainPath+strOrgId+"/Categories/"+hmInnerFolder.get("FOLDER_NAME")+"/"+hmInnerSubFolder.get("FOLDER_NAME");
											}
											
											String fileSubCount ="-";
											if(alDoc !=null && alDoc.size() > 0) {
												fileSubCount = alDoc.size()+"";
											}
											nSubFolder++;
										%>
											<div id="folderTR_<%=ii %>_<%=j %>" style="float: left; width: 100%; margin-top: 10px; display: none;">
												
												<div style="float: left; margin-left: 23px; width: 317px; margin-right: 9px;">
													<input type="hidden" name="hideFolder_<%=ii %>_<%=j %>" id="hideFolder_<%=ii %>_<%=j %>" value="0" /> 
													<div style="float: left; margin-right: 5px;"> <a href="javascript:void(0);" style="font-weight: normal; color: black;" onclick="openCloseDocs1('<%=ii %>','<%=j %>','<%=alDoc.size() %>');"> 
													<%-- <img height="15" width="20" src="<%=request.getContextPath()%>/images1/icons/icons/folder_icon.png" /> --%>
													<i class="fa fa-folder-open-o" style="font-size: 20px;height: 15px;"></i></a> </div> 
													
													<strong><%=hmInnerSubFolder.get("FOLDER_NAME") %></strong>
													<div style="float: left; color: gray; width: 90%; margin-top: -5px; margin-left: 25px;"><%=fileSubCount +" items" %></div>
													<div style="float: left; color: gray; width: 90%; margin-top: -5px; margin-left: 25px;"><%=hmInnerSubFolder.get("DESCRIPTION") %></div>
													<div style="float: left; color: gray; width: 90%; margin-top: -5px; margin-left: 25px;"><%=hmInnerSubFolder.get("ENTRY_DATE") %></div>
													<% if(uF.parseToInt(fileSubCount)>0) { %>
													<div style="float: left; color: gray; width: 90%; margin-top: -5px; margin-left: 25px;">
													<span style="float: left;"><%=fileSubCount +" items" %> view files </span> 
													<a href="javascript:void(0);" style="font-weight: normal; color: black;" onclick="openCloseDocs1('<%=ii %>','<%=j %>','<%=alDoc.size() %>');">
														<span id="FDDownarrowSpan<%=ii %>_<%=j %>" style="float: left; margin-left: 2px; margin-top: 3px;"> 
															<i class="fa fa-angle-down" aria-hidden="true" style="width: 14px;"></i>
														</span>
														<span id="FDUparrowSpan<%=ii %>_<%=j %>" style="float: left; margin-left: 2px; margin-top: 3px; display: none;">
															<i class="fa fa-angle-up" aria-hidden="true" style="width: 14px;"></i>
														</span>
													</a>
													</div>
													<% } %>
												</div>
												<div style="float: left; width: 120px; margin-right: 9px;">
													<div style="float: left; width: 100%;">Aligned with</div>
													<div style="float: left; width: 100%; margin-top: -5px;"><%=hmInnerSubFolder.get("ALIGN") %></div>
												</div>
												<div style="float: left; width: 120px; margin-right: 9px;">
													<div style="float: left; width: 100%;">Shared with</div>
													<div style="float: left; width: 100%; margin-top: -5px;"><%=hmInnerSubFolder.get("SHARING_TYPE") %></div>
												</div>
												<div style="float: left; width: 90px;">
													<div style="float: left; width: 100%;">Actions</div>
													<div style="float: left; width: 100%;">
														<select name="folderAction<%=ii %>_<%=j %>" id="folderAction<%=ii %>_<%=j %>" style="width: 80px !important;" onchange="executeFolderActions(this.value, '<%=ii %>_<%=j %>', 'SF', '<%=hmInnerSubFolder.get("PRO_DOCUMENT_ID") %>', '', '', '<%=hmInnerSubFolder.get("FOLDER_NAME") %>', 'folderTR_<%=ii %>_<%=j %>', '<%=subFolderSavePath %>');">
									                    	<option value="">Actions</option>
									                    	<option value="1">Edit</option>
								                    		<option value="2">Delete</option>
									                    </select>
													</div>
												</div>
												
												<div style="float: left; width: 100%; margin-top: 10px;">
													<span style="float:left; width: 100%;"><a href="javascript:void(0);" style="margin: -15px 0px 15px 50px;" onclick="addNewDocumentInSubFolder('addNewDocsInSubFolder<%=ii %>_<%=j %>', 'SF', '<%=hmInnerSubFolder.get("PRO_DOCUMENT_ID") %>', '<%=j %>');"> +Add Document</a></span>
												</div>
												<div id="addNewDocsInSubFolder<%=ii %>_<%=j %>" style="float: left; width: 100%; margin-top: 10px; display: none;"></div>
												<%
													for(int k = 0; k<alDoc.size(); k++){
														Map<String, String> hmInnerSubDoc = (Map<String, String>) alDoc.get(k);
														String filePath1 = proDocRetrivePath+strOrgId+"/Projects/"+hmInnerFolder.get("PRO_ID")+"/"+hmInnerFolder.get("FOLDER_NAME")+"/"+hmInnerSubFolder.get("FOLDER_NAME")+"/"+hmInnerSubDoc.get("DOCUMENT_NAME");
														String fileDir = proDocRetrivePath+strOrgId+"/Projects/"+hmInnerFolder.get("PRO_ID")+"/"+hmInnerFolder.get("FOLDER_NAME")+"/"+hmInnerSubFolder.get("FOLDER_NAME");
														
														String fileSavePath = proDocMainPath+strOrgId+"/Projects/"+hmInnerFolder.get("PRO_ID")+"/"+hmInnerFolder.get("FOLDER_NAME")+"/"+hmInnerSubFolder.get("FOLDER_NAME")+"/"+hmInnerSubDoc.get("DOCUMENT_NAME");
														if(uF.parseToInt(hmInnerFolder.get("PRO_ID")) == 0 && uF.parseToInt(hmInnerFolder.get("CATEGORY_ID")) == 2) {
															filePath1 = proDocRetrivePath+strOrgId+"/Categories/"+hmInnerFolder.get("FOLDER_NAME")+"/"+hmInnerSubFolder.get("FOLDER_NAME")+"/"+hmInnerSubDoc.get("DOCUMENT_NAME");
															fileDir = proDocRetrivePath+strOrgId+"/Categories/"+hmInnerFolder.get("FOLDER_NAME")+"/"+hmInnerSubFolder.get("FOLDER_NAME");
															fileSavePath = proDocMainPath+strOrgId+"/Categories/"+hmInnerFolder.get("FOLDER_NAME")+"/"+hmInnerSubFolder.get("FOLDER_NAME")+"/"+hmInnerSubDoc.get("DOCUMENT_NAME");
														}
														
														nFiles++;
														
														String fileIcon = request.getContextPath()+"/images1/icons/icons/file_icon.png";
														if(hmFileIcon.containsKey(hmInnerSubDoc.get("FILE_EXTENSION"))){ 
															fileIcon = request.getContextPath()+"/images1/file_icon/"+hmFileIcon.get(hmInnerSubDoc.get("FILE_EXTENSION"));
														}
														String action = "ProjectDocumentFact.action?clientId="+hmInnerSubDoc.get("CLIENT_ID")+"&proId="+hmInnerSubDoc.get("PRO_ID")+"&folderName="+hmInnerSubDoc.get("DOCUMENT_NAME")+"&proFolderId="+hmInnerSubDoc.get("PRO_DOCUMENT_ID")+"&type=2&filePath="+URLEncoder.encode(filePath1)+"&fileDir="+URLEncoder.encode(fileDir);
														int nDocVersion = uF.parseToInt(hmInnerSubDoc.get("DOC_VERSION")) > 0 ? uF.parseToInt(hmInnerSubDoc.get("DOC_VERSION")) : 1;
												%>
														<div id="folderTR_<%=ii %>_<%=j %>_<%=k %>" style="float: left; width: 100%; margin-top: 10px; display: none;">
															<div style="float: left; margin-left: 48px; width: 292px; margin-right: 9px;">
																<%-- <a href="<%=filePath1 %>" style="font-weight: normal; color: black;"> --%>
																<%-- <a href="javascript:void(0)" style="font-weight: normal; color: black;"onclick="projectDocFact('<%=hmInnerSubDoc.get("CLIENT_ID")%>','<%=hmInnerSubDoc.get("PRO_ID")%>','<%=hmInnerSubDoc.get("DOCUMENT_NAME") %>','<%=hmInnerSubDoc.get("PRO_DOCUMENT_ID") %>',2,'<%=filePath1 %>','<%=fileDir %>');" title="View Document"> --%>
																<a target="_blank" href="<%=action %>" style="font-weight: normal; color: black;" title="View Document"> 
																	<img height="18" width="18" src="<%=fileIcon %>" />&nbsp;<%=hmInnerSubDoc.get("DOCUMENT_NAME") %>
																</a> 
																<div style="float: left; color: gray; width: 90%; margin-top: -5px; margin-left: 25px;"><%=hmInnerSubDoc.get("FILE_SIZE") %></div>
																<div style="float: left; color: gray; width: 90%; margin-top: -5px; margin-left: 25px;"><%=hmInnerSubDoc.get("DESCRIPTION") %></div>
																<div style="float: left; color: gray; width: 90%; margin-top: -5px; margin-left: 25px;"><%=hmInnerSubDoc.get("ENTRY_DATE") %></div>
																<div style="float: left; color: gray; width: 90%; margin-top: -5px; margin-left: 25px;">
																	<span style="float: left;">
																		<%-- <a href="javascript:void(0)" style="font-weight: normal;"onclick="projectDocFact('<%=hmInnerSubDoc.get("CLIENT_ID")%>','<%=hmInnerSubDoc.get("PRO_ID")%>','<%=hmInnerSubDoc.get("DOCUMENT_NAME") %>','<%=hmInnerSubDoc.get("PRO_DOCUMENT_ID") %>',2,'<%=filePath1 %>','<%=fileDir %>');" title="View Document"> --%>
																		<a target="_blank" href="<%=action %>" style="font-weight: normal;" title="View Document">
																			View File 
																		</a>
																	|</span>
																	<span style="float: left; margin-left: 2px;"><a href="<%=filePath1 %>" style="font-weight: normal;">Download</a></span>
																</div>
																
															</div>
															<div style="float: left; width: 120px; margin-right: 9px;">
																<div style="float: left; width: 100%;">Version <strong><%=nDocVersion %></strong> </div>
																<div style="float: left; width: 100%; margin-top: -5px;">
																	<%if(nDocVersion > 1){ %>
																		<span style="float: left;">Version history </span> 
																		<input type="hidden" name="proDocsSpanStatus" id="proDocsSpanStatus<%=ii %>_<%=j %>_<%=k %>" value = "0"/> 
																		<a href="javascript:void(0);" style="font-weight: normal; color: black;" onclick="viewVersionHistory(<%=hmInnerSubDoc.get("PRO_DOCUMENT_ID") %>,'proDocsSpanStatus<%=ii %>_<%=j %>_<%=k %>','proDocsVersionDiv<%=ii %>_<%=j %>_<%=k %>','VHDownarrowSpan<%=ii %>_<%=j %>_<%=k %>', 'VHUparrowSpan<%=ii %>_<%=j %>_<%=k %>','2','<%=filePath1%>','<%=fileDir %>')">
																			<span id="VHDownarrowSpan<%=ii %>_<%=j %>_<%=k %>" style="float: left; margin-left: 2px; margin-top: 3px;"> 
																				<i class="fa fa-angle-down" aria-hidden="true" style="width: 14px;"></i> 
																			</span>
																			<span id="VHUparrowSpan<%=ii %>_<%=j %>_<%=k %>" style="float: left; margin-left: 2px; margin-top: 3px; display: none;">
																				<i class="fa fa-angle-up" aria-hidden="true" style="width: 14px;"></i>
																			</span>
																		</a>
																	<%} %>
																</div>
															</div>
															<div style="float: left; width: 120px; margin-right: 9px;">
																<div style="float: left; width: 100%;">Aligned with</div>
																<div style="float: left; width: 100%; margin-top: -5px;"><%=hmInnerSubDoc.get("ALIGN") %></div>
															</div>
															<div style="float: left; width: 120px; margin-right: 9px;">
																<div style="float: left; width: 100%;">Shared with</div>
																<div style="float: left; width: 100%; margin-top: -5px;"><%=hmInnerSubDoc.get("SHARING_TYPE") %></div>
															</div>
															<div style="float: left; width: 90px;">
																<div style="float: left; width: 100%;">Actions</div>
																<div style="float: left; width: 100%;">
																	<select name="folderAction<%=ii %>_<%=j %>_<%=k %>" id="folderAction<%=ii %>_<%=j %>_<%=k %>" style="width: 80px !important;" onchange="executeFolderActions(this.value, '<%=ii %>_<%=j %>_<%=k %>', 'SFD', '<%=hmInnerSubDoc.get("PRO_DOCUMENT_ID") %>', '<%=filePath1 %>', '<%=fileDir %>', '<%=hmInnerSubDoc.get("DOCUMENT_NAME") %>', 'folderTR_<%=ii %>_<%=j %>_<%=k %>', '<%=fileSavePath %>');">
												                    	<option value="">Actions</option>
												                    	<option value="1">Edit</option>
											                    		<option value="2">Delete</option>
													                    <option value="3">Copy</option>
												                    </select>
																</div>
															</div>
															
														</div>
														<div id="proDocsVersionDiv<%=ii %>_<%=j %>_<%=k %>" style="float: left; width: 100%; margin-top: 10px; display: none;"></div>
												<%} %>
											</div>
										<%} %>
										<%
										for(int j = 0; j < alFolderDoc.size();j++) {
											Map<String, String> hmInnerDoc = (Map<String, String>) alFolderDoc.get(j);
											String filePath1 = proDocRetrivePath+strOrgId+"/Projects/"+hmInnerFolder.get("PRO_ID")+"/"+hmInnerFolder.get("FOLDER_NAME")+"/"+hmInnerDoc.get("DOCUMENT_NAME");
											String fileDir = proDocRetrivePath+strOrgId+"/Projects/"+hmInnerFolder.get("PRO_ID")+"/"+hmInnerFolder.get("FOLDER_NAME");
											String fileSavePath = proDocMainPath+strOrgId+"/Projects/"+hmInnerFolder.get("PRO_ID")+"/"+hmInnerFolder.get("FOLDER_NAME")+"/"+hmInnerDoc.get("DOCUMENT_NAME");
											
											if(uF.parseToInt(hmInnerFolder.get("PRO_ID")) == 0 && uF.parseToInt(hmInnerFolder.get("CATEGORY_ID")) == 2) {
												filePath1 = proDocRetrivePath+strOrgId+"/Categories/"+hmInnerFolder.get("FOLDER_NAME")+"/"+hmInnerDoc.get("DOCUMENT_NAME");
												fileDir = proDocRetrivePath+strOrgId+"/Categories/"+hmInnerFolder.get("FOLDER_NAME");
												fileSavePath = proDocMainPath+strOrgId+"/Categories/"+hmInnerFolder.get("FOLDER_NAME")+"/"+hmInnerDoc.get("DOCUMENT_NAME");
											}
											
											nFiles++;
											
											String fileIcon = request.getContextPath()+"/images1/icons/icons/file_icon.png";
											if(hmFileIcon.containsKey(hmInnerDoc.get("FILE_EXTENSION"))){ 
												fileIcon = request.getContextPath()+"/images1/file_icon/"+hmFileIcon.get(hmInnerDoc.get("FILE_EXTENSION"));
											}
											String action = "ProjectDocumentFact.action?clientId="+hmInnerDoc.get("CLIENT_ID")+"&proId="+hmInnerDoc.get("PRO_ID")+"&folderName="+hmInnerDoc.get("DOCUMENT_NAME")+"&proFolderId="+hmInnerDoc.get("PRO_DOCUMENT_ID")+"&type=3&filePath="+URLEncoder.encode(filePath1)+"&fileDir="+URLEncoder.encode(fileDir);
											int nDocVersion = uF.parseToInt(hmInnerDoc.get("DOC_VERSION")) > 0 ? uF.parseToInt(hmInnerDoc.get("DOC_VERSION")) : 1;
										%>
											<div id="docFolderTR_<%=ii %>_<%=j %>" style="float: left; width: 100%; margin-top: 10px; display: none;"> 
												<div style="float: left; margin-left: 23px; width: 317px; margin-right: 9px;">
													<%-- <a href="<%=filePath1 %>" style="font-weight: normal; color: black;"> --%>
													<%-- <a href="javascript:void(0)" style="font-weight: normal; color: black;"onclick="projectDocFact('<%=hmInnerDoc.get("CLIENT_ID")%>','<%=hmInnerDoc.get("PRO_ID")%>','<%=hmInnerDoc.get("DOCUMENT_NAME") %>','<%=hmInnerDoc.get("PRO_DOCUMENT_ID") %>',3,'<%=filePath1 %>','<%=fileDir %>');" title="View Document"> --%>
													<a target="_blank" href="<%=action %>" style="font-weight: normal; color: black;" title="View Document"> 
														<img height="18" width="18" src="<%=fileIcon %>" />&nbsp;<%=hmInnerDoc.get("DOCUMENT_NAME") %>
													</a>
													<div style="float: left; color: gray; width: 90%; margin-top: -5px; margin-left: 25px;"><%=hmInnerDoc.get("FILE_SIZE") %></div>
													<div style="float: left; color: gray; width: 90%; margin-top: -5px; margin-left: 25px;"><%=hmInnerDoc.get("DESCRIPTION") %></div>
													<div style="float: left; color: gray; width: 90%; margin-top: -5px; margin-left: 25px;"><%=hmInnerDoc.get("ENTRY_DATE") %></div>
													<div style="float: left; color: gray; width: 90%; margin-top: -5px; margin-left: 25px;">
														<span style="float: left;">
															<%-- <a href="javascript:void(0)" style="font-weight: normal;"onclick="projectDocFact('<%=hmInnerDoc.get("CLIENT_ID")%>','<%=hmInnerDoc.get("PRO_ID")%>','<%=hmInnerDoc.get("DOCUMENT_NAME") %>','<%=hmInnerDoc.get("PRO_DOCUMENT_ID") %>',3,'<%=filePath1 %>','<%=fileDir %>');" title="View Document"> --%>
															<a target="_blank" href="<%=action %>" style="font-weight: normal;" title="View Document">
																View File
															</a> 
														|</span>
														<span style="float: left; margin-left: 2px;"><a href="<%=filePath1 %>" style="font-weight: normal;">Download</a></span>
													</div>
													
												</div>
												<div style="float: left; width: 120px; margin-right: 9px;">
													<div style="float: left; width: 100%;">Version <strong><%=nDocVersion %></strong> </div>
													<div style="float: left; width: 100%; margin-top: -5px;">
														<%if(nDocVersion > 1){ %>
															<span style="float: left;">Version history </span> 
															<input type="hidden" name="proDocsSpanStatus" id="proDocsSpanStatus<%=ii %>_<%=j %>" value = "0"/> 
															<a href="javascript:void(0);" style="font-weight: normal; color: black;" onclick="viewVersionHistory(<%=hmInnerDoc.get("PRO_DOCUMENT_ID") %>,'proDocsSpanStatus<%=ii %>_<%=j %>','proDocsVersionDiv<%=ii %>_<%=j %>','VHDownarrowSpan<%=ii %>_<%=j %>', 'VHUparrowSpan<%=ii %>_<%=j %>','3','<%=filePath1%>','<%=fileDir %>')">
																<span id="VHDownarrowSpan<%=ii %>_<%=j %>" style="float: left; margin-left: 2px; margin-top: 3px;"> 
																	<i class="fa fa-angle-down" aria-hidden="true" style="width: 14px;"></i> 
																</span>
																<span id="VHUparrowSpan<%=ii %>_<%=j %>" style="float: left; margin-left: 2px; margin-top: 3px; display: none;">
																	<i class="fa fa-angle-up" aria-hidden="true" style="width: 14px;"></i>
																</span>
															</a>
														<%} %>
													</div>
												</div>
												<div style="float: left; width: 120px; margin-right: 9px;">
													<div style="float: left; width: 100%;">Aligned with</div>
													<div style="float: left; width: 100%; margin-top: -5px;"><%=hmInnerDoc.get("ALIGN") %></div>
												</div>
												<div style="float: left; width: 120px; margin-right: 9px;">
													<div style="float: left; width: 100%;">Shared with</div>
													<div style="float: left; width: 100%; margin-top: -5px;"><%=hmInnerDoc.get("SHARING_TYPE") %></div>
												</div>
												<div style="float: left; width: 90px;">
													<div style="float: left; width: 100%;">Actions</div>
													<div style="float: left; width: 100%;">
														<select name="folderDocAction<%=ii %>_<%=j %>" id="folderDocAction<%=ii %>_<%=j %>" style="width: 80px !important;" onchange="executeFolderActions(this.value, '<%=ii %>_<%=j %>', 'FD', '<%=hmInnerDoc.get("PRO_DOCUMENT_ID") %>', '<%=filePath1 %>', '<%=fileDir %>', '<%=hmInnerDoc.get("DOCUMENT_NAME") %>', 'docFolderTR_<%=ii %>_<%=j %>', '<%=fileSavePath %>');">
									                    	<option value="">Actions</option>
									                    	<option value="1">Edit</option>
								                    		<option value="2">Delete</option>
													        <option value="3">Copy</option>
									                    </select>
													</div>
												</div>
												
											</div>
											<div id="proDocsVersionDiv<%=ii %>_<%=j %>" style="float: left; width: 100%; margin-top: 10px; display: none;"></div>
										<%} %>
										
									</div>
								<%} %>
								
					<%} else {%>
						<div style="float: left; width: width: 96%;" class="nodata msg">
							<span>No documents attached.</span>
						</div>
					<%} %>
					<input type="hidden" name="folderCnt" id="folderCnt" value="<%=nFolder%>"/>
					<input type="hidden" name="subFolderCnt" id="subFolderCnt" value="<%=nSubFolder%>"/>
					<input type="hidden" name="fileCnt" id="fileCnt" value="<%=nFiles%>"/>
					
					<%-- <script type="text/javascript">
						var timer = window.setTimeout(function() {
							getFolderCount();
						}, 400);
					</script> --%>
					
				</div>
				
				<% } else if(type != null && (type.equalsIgnoreCase("C") || type.equalsIgnoreCase("P"))) { %>
						<div style="float: left; width: 100%;">  <!-- border: 1px solid gray; -->
							<% 	
								//Map<String, List<Map<String,String>>> hmFolder = (Map<String, List<Map<String,String>>>)request.getAttribute("hmFolder");
								List<Map<String, String>> alProFolder = (List<Map<String, String>>) request.getAttribute("alProFolder");
								if(alProFolder == null) alProFolder = new ArrayList<Map<String, String>>(); 
								List<Map<String, String>> alMainDoc = (List<Map<String, String>>) request.getAttribute("alMainDoc");
								if(alMainDoc == null) alMainDoc = new ArrayList<Map<String, String>>();
								
								/* Map<String, List<Map<String,String>>> hmDoc = (Map<String, List<Map<String,String>>>)request.getAttribute("hmDoc");
								if(hmDoc == null) hmDoc = new HashMap<String, List<Map<String,String>>>(); */
								
								Map<String, List<Map<String,String>>> hmSubFolder = (Map<String, List<Map<String,String>>>)request.getAttribute("hmSubFolder");
								if(hmSubFolder == null) hmSubFolder = new HashMap<String, List<Map<String,String>>>();
								Map<String, List<Map<String,String>>> hmSubDoc = (Map<String, List<Map<String,String>>>)request.getAttribute("hmSubDoc");
								if(hmSubDoc == null) hmSubDoc = new HashMap<String, List<Map<String,String>>>();
								
								int nFolder = 0;
								int nSubFolder = 0;
								int nFiles = 0;
								
								String proDocMainPath = (String) request.getAttribute("proDocMainPath");
								String proDocRetrivePath = (String) request.getAttribute("proDocRetrivePath");
								String strOrgId = (String) request.getAttribute("strOrgId");
								if((alProFolder!=null && !alProFolder.isEmpty()) || (alMainDoc !=null && !alMainDoc.isEmpty())) {
							%>
							<div style="float: right; border-bottom: 1px solid #CCCCCC; width: 100%; text-align: right; margin-bottom: 10px;">
								<span style="float: right; margin-right: 5px;"><span id="spanFilesId" class="anaAttrib1" style="font-size: 20px; margin-right: 2px;">0</span>Files</span>
								<span style="float: right; margin-right: 5px;"><span id="spanSubFolderId" class="anaAttrib1" style="font-size: 20px; margin-right: 2px;">0</span>Sub Folders</span>
								<span style="float: right; margin-right: 5px;"><span id="spanFolderId" class="anaAttrib1" style="font-size: 20px; margin-right: 2px;">0</span>Folders</span>
							</div>
								<% 	
									/* Iterator<String> it = hmFolder.keySet().iterator();
									while(it.hasNext()) {
										String proFolderId = it.next();
										List<Map<String, String>> alFolder = (List<Map<String, String>>) hmFolder.get(proFolderId); 
										if(alFolder == null) alFolder = new ArrayList<Map<String,String>>(); */
										
										//System.out.println("alProFolder ===>> " + alProFolder.size());
										for(int ii = 0; ii < alProFolder.size(); ii++) {
											//System.out.println("ii ===>> " + ii);
											Map<String, String> hmInnerFolder = (Map<String, String>) alProFolder.get(ii);
											List<Map<String, String>> alSubFolder = (List<Map<String, String>>) hmSubFolder.get(hmInnerFolder.get("PRO_DOCUMENT_ID")); 
											if(alSubFolder == null) alSubFolder = new ArrayList<Map<String,String>>();
											List<Map<String, String>> alFolderDoc = (List<Map<String, String>>) hmSubDoc.get(hmInnerFolder.get("PRO_DOCUMENT_ID")); 
											if(alFolderDoc == null) alFolderDoc = new ArrayList<Map<String,String>>();
											String fileCount ="-";
											int nFoldrCnt = (alSubFolder.size());
											int nFileCnt = (alFolderDoc.size());
											if(nFoldrCnt > 0 || nFileCnt > 0) {
												fileCount = (nFoldrCnt + nFileCnt)+"";
											}
											
											nFolder++;
											
											String folderSavePath = proDocMainPath+strOrgId+"/Projects/"+hmInnerFolder.get("PRO_ID")+"/"+hmInnerFolder.get("FOLDER_NAME");
											if(uF.parseToInt(hmInnerFolder.get("PRO_ID")) == 0 && uF.parseToInt(hmInnerFolder.get("CATEGORY_ID")) == 2) {
												folderSavePath = proDocMainPath+strOrgId+"/Categories/"+hmInnerFolder.get("FOLDER_NAME");
											}
									%>
											<div id="folderTR_<%=ii %>" style="float: left; width: 100%; margin-top: 10px;">
												<div style="float: left; width: 340px; margin-right: 9px;">
													<input type="hidden" name="hideFolder_<%=ii %>" id="hideFolder_<%=ii %>" value="0" /> 
													<div style="float: left; margin-right: 5px;"> <a href="javascript:void(0);" style="font-weight: normal; color: black;" onclick="openCloseDocs('<%=ii %>','<%=alSubFolder.size()%>','<%=alFolderDoc.size()%>');"> 
													<%-- <img height="15" width="20" src="<%=request.getContextPath()%>/images1/icons/icons/folder_icon.png" /> </a> </div> --%>
													<i class="fa fa-folder-open-o" style="font-size: 20px;height: 15px;"></i></a> </div> 
													<strong><%=hmInnerFolder.get("FOLDER_NAME") %></strong>
													<div style="float: left; color: gray; width: 90%; margin-top: -5px; margin-left: 25px;"><%=fileCount +" items" %></div>
													<div style="float: left; color: gray; width: 90%; margin-top: -5px; margin-left: 25px;"><%=hmInnerFolder.get("DESCRIPTION") %></div>
													<div style="float: left; color: gray; width: 90%; margin-top: -5px; margin-left: 25px;"><%=hmInnerFolder.get("ENTRY_DATE") %></div>
													<% if(uF.parseToInt(fileCount)>0) { %>
													<div style="float: left; color: gray; width: 90%; margin-top: -5px; margin-left: 25px;">
													<span style="float: left;"><%=fileCount +" items" %> view folders & files </span> 
													<a href="javascript:void(0);" style="font-weight: normal; color: black;" onclick="openCloseDocs('<%=ii %>','<%=alSubFolder.size()%>','<%=alFolderDoc.size()%>');">
														<span id="FDDownarrowSpan<%=ii %>" style="float: left; margin-left: 2px; margin-top: 3px;"> 
															<i class="fa fa-angle-down" aria-hidden="true" style="width: 14px;"></i> 
														</span>
														<span id="FDUparrowSpan<%=ii %>" style="float: left; margin-left: 2px; margin-top: 3px; display: none;">
															<i class="fa fa-angle-up" aria-hidden="true" style="width: 14px;"></i>
														</span>
													</a>
													</div>
													<% } %>													
												</div>
												
												<div style="float: left; width: 120px; margin-right: 9px;">
													<div style="float: left; width: 100%;">Aligned with</div>
													<div style="float: left; width: 100%; margin-top: -5px;"><%=hmInnerFolder.get("ALIGN") %></div>
												</div>
												<div style="float: left; width: 120px; margin-right: 9px;">
													<div style="float: left; width: 100%;">Shared with</div>
													<div style="float: left; width: 100%; margin-top: -5px;"><%=hmInnerFolder.get("SHARING_TYPE") %></div>
												</div>
												<div style="float: left; width: 90px;">
													<div style="float: left; width: 100%;">Actions</div>
													<div style="float: left; width: 100%;">
														<select name="proCatAction<%=ii %>" id="proCatAction<%=ii %>" style="width: 80px !important;" onchange="executeFolderActions(this.value, '<%=ii %>', 'F', '<%=hmInnerFolder.get("PRO_DOCUMENT_ID") %>', '', '', '<%=hmInnerFolder.get("FOLDER_NAME") %>', 'folderTR_<%=ii %>', '<%=folderSavePath %>');">
									                    	<option value="">Actions</option>
									                    	<option value="1">Edit</option>
								                    		<option value="2">Delete</option>
									                    </select>
													<%-- <a href="javascript:void(0)" class="edit_lvl" style="float: right;" onclick="editFolder('<%=hmInnerFolder.get("CLIENT_ID")%>','<%=hmInnerFolder.get("PRO_ID")%>','<%=hmInnerFolder.get("FOLDER_NAME") %>','<%=hmInnerFolder.get("PRO_DOCUMENT_ID") %>',1);" title="Edit Folder">Edit</a> --%>
													</div>
												</div>
												<div style="float: left; width: 100%; margin-top: 10px;">
													<span style="float:left; width: 100%;"><a href="javascript:void(0);" style="margin: -15px 0px 15px 50px;" onclick="addNewSubFolderORDocumentInFolder('addNewSubFolderORDocsInFolder_<%=ii %>', 'F', '<%=hmInnerFolder.get("PRO_DOCUMENT_ID") %>', '<%=ii %>');"> +Add Folder/ Document</a></span>
												</div>
												<div id="addNewSubFolderORDocsInFolder_<%=ii %>" style="float: left; width: 100%; margin-top: 10px; display: none;"></div>
												
												<%
												for(int j = 0; j < alSubFolder.size(); j++){
													Map<String, String> hmInnerSubFolder = (Map<String, String>) alSubFolder.get(j);
													List<Map<String, String>> alDoc = (List<Map<String, String>>) hmSubDoc.get(hmInnerSubFolder.get("PRO_DOCUMENT_ID")); 
													if(alDoc == null) alDoc = new ArrayList<Map<String,String>>();
													
													String subFolderSavePath = proDocMainPath+strOrgId+"/Projects/"+hmInnerFolder.get("PRO_ID")+"/"+hmInnerFolder.get("FOLDER_NAME")+"/"+hmInnerSubFolder.get("FOLDER_NAME");
													if(uF.parseToInt(hmInnerFolder.get("PRO_ID")) == 0 && uF.parseToInt(hmInnerFolder.get("CATEGORY_ID")) == 2) {
														subFolderSavePath = proDocMainPath+strOrgId+"/Categories/"+hmInnerFolder.get("FOLDER_NAME")+"/"+hmInnerSubFolder.get("FOLDER_NAME");
													}
													
													nSubFolder++;
													
													String fileSubCount ="-";
													if(alDoc !=null && alDoc.size() > 0) {
														fileSubCount = alDoc.size()+"";
													}
												%>
													<div id="folderTR_<%=ii %>_<%=j %>" style="float: left; width: 100%; margin-top: 10px; display: none;">
														
														<div style="float: left; margin-left: 23px; width: 317px; margin-right: 9px;">
															<input type="hidden" name="hideFolder_<%=ii %>_<%=j %>" id="hideFolder_<%=ii %>_<%=j %>" value="0" /> 
															<div style="float: left; margin-right: 5px;"> <a href="javascript:void(0);" style="font-weight: normal; color: black;" onclick="openCloseDocs1('<%=ii %>','<%=j %>','<%=alDoc.size() %>');"> 
															<%-- <img height="15" width="20" src="<%=request.getContextPath()%>/images1/icons/icons/folder_icon.png" /> </a> </div> --%>
															<i class="fa fa-folder-open-o" style="font-size: 20px;height: 15px;"></i></a> </div> 
															
															<strong><%=hmInnerSubFolder.get("FOLDER_NAME") %></strong>
															<div style="float: left; color: gray; width: 90%; margin-top: -5px; margin-left: 25px;"><%=fileSubCount +" items" %></div>
															<div style="float: left; color: gray; width: 90%; margin-top: -5px; margin-left: 25px;"><%=hmInnerSubFolder.get("DESCRIPTION") %></div>
															<div style="float: left; color: gray; width: 90%; margin-top: -5px; margin-left: 25px;"><%=hmInnerSubFolder.get("ENTRY_DATE") %></div>
															<% if(uF.parseToInt(fileSubCount)>0) { %>
															<div style="float: left; color: gray; width: 90%; margin-top: -5px; margin-left: 25px;">
															<span style="float: left;"><%=fileSubCount +" items" %> view files </span> 
															<a href="javascript:void(0);" style="font-weight: normal; color: black;" onclick="openCloseDocs1('<%=ii %>','<%=j %>','<%=alDoc.size() %>');">
																<span id="FDDownarrowSpan<%=ii %>_<%=j %>" style="float: left; margin-left: 2px; margin-top: 3px;"> 
																	<i class="fa fa-angle-down" aria-hidden="true" style="width: 14px;"></i> 
																</span>
																<span id="FDUparrowSpan<%=ii %>_<%=j %>" style="float: left; margin-left: 2px; margin-top: 3px; display: none;">
																	<i class="fa fa-angle-up" aria-hidden="true" style="width: 14px;"></i>
																</span>
															</a>
															</div>
															<% } %>
														</div>
														<div style="float: left; width: 120px; margin-right: 9px;">
															<div style="float: left; width: 100%;">Aligned with</div>
															<div style="float: left; width: 100%; margin-top: -5px;"><%=hmInnerSubFolder.get("ALIGN") %></div>
														</div>
														<div style="float: left; width: 120px; margin-right: 9px;">
															<div style="float: left; width: 100%;">Shared with</div>
															<div style="float: left; width: 100%; margin-top: -5px;"><%=hmInnerSubFolder.get("SHARING_TYPE") %></div>
														</div>
														<div style="float: left; width: 90px;">
															<div style="float: left; width: 100%;">Actions</div>
															<div style="float: left; width: 100%;">
																<select name="proCatAction<%=ii %>_<%=j %>" id="proCatAction<%=ii %>_<%=j %>" style="width: 80px !important;" onchange="executeFolderActions(this.value, '<%=ii %>_<%=j %>', 'SF', '<%=hmInnerSubFolder.get("PRO_DOCUMENT_ID") %>', '', '', '<%=hmInnerSubFolder.get("FOLDER_NAME") %>', 'folderTR_<%=ii %>_<%=j %>', '<%=subFolderSavePath %>');">
											                    	<option value="">Actions</option>
											                    	<option value="1">Edit</option>
										                    		<option value="2">Delete</option>
											                    </select>
																<%-- <a href="javascript:void(0)" class="edit_lvl" style="float: right;" onclick="editFolder('<%=hmInnerSubFolder.get("CLIENT_ID")%>','<%=hmInnerSubFolder.get("PRO_ID")%>','<%=hmInnerSubFolder.get("FOLDER_NAME") %>','<%=hmInnerSubFolder.get("PRO_DOCUMENT_ID") %>',2);" title="Edit Folder">Edit</a> --%>
															</div>
														</div>
														
														<div style="float: left; width: 100%; margin-top: 10px;">
															<span style="float:left; width: 100%;"><a href="javascript:void(0);" style="margin: -15px 0px 15px 50px;" onclick="addNewDocumentInSubFolder('addNewDocsInSubFolder<%=ii %>_<%=j %>', 'SF', '<%=hmInnerSubFolder.get("PRO_DOCUMENT_ID") %>', '<%=ii %>_<%=j %>');"> +Add Document</a></span>
														</div>
														<div id="addNewDocsInSubFolder<%=ii %>_<%=j %>" style="float: left; width: 100%; margin-top: 10px; display: none;"></div>
														<%
															for(int k = 0; k<alDoc.size(); k++){
																Map<String, String> hmInnerSubDoc = (Map<String, String>) alDoc.get(k);
																String filePath1 = proDocRetrivePath+strOrgId+"/Projects/"+hmInnerFolder.get("PRO_ID")+"/"+hmInnerFolder.get("FOLDER_NAME")+"/"+hmInnerSubFolder.get("FOLDER_NAME")+"/"+hmInnerSubDoc.get("DOCUMENT_NAME");
																String fileDir = proDocRetrivePath+strOrgId+"/Projects/"+hmInnerFolder.get("PRO_ID")+"/"+hmInnerFolder.get("FOLDER_NAME")+"/"+hmInnerSubFolder.get("FOLDER_NAME");
																
																String fileSavePath = proDocMainPath+strOrgId+"/Projects/"+hmInnerFolder.get("PRO_ID")+"/"+hmInnerFolder.get("FOLDER_NAME")+"/"+hmInnerSubFolder.get("FOLDER_NAME")+"/"+hmInnerSubDoc.get("DOCUMENT_NAME");
																if(uF.parseToInt(hmInnerFolder.get("PRO_ID")) == 0 && uF.parseToInt(hmInnerFolder.get("CATEGORY_ID")) == 2) {
																	filePath1 = proDocRetrivePath+strOrgId+"/Categories/"+hmInnerFolder.get("FOLDER_NAME")+"/"+hmInnerSubFolder.get("FOLDER_NAME")+"/"+hmInnerSubDoc.get("DOCUMENT_NAME");
																	fileDir = proDocRetrivePath+strOrgId+"/Categories/"+hmInnerFolder.get("FOLDER_NAME")+"/"+hmInnerSubFolder.get("FOLDER_NAME");
																	fileSavePath = proDocMainPath+strOrgId+"/Categories/"+hmInnerFolder.get("FOLDER_NAME")+"/"+hmInnerSubFolder.get("FOLDER_NAME")+"/"+hmInnerSubDoc.get("DOCUMENT_NAME");
																}
																
																nFiles++;
																
																String fileIcon = request.getContextPath()+"/images1/icons/icons/file_icon.png";
																if(hmFileIcon.containsKey(hmInnerSubDoc.get("FILE_EXTENSION"))){ 
																	fileIcon = request.getContextPath()+"/images1/file_icon/"+hmFileIcon.get(hmInnerSubDoc.get("FILE_EXTENSION"));
																}
																String action = "ProjectDocumentFact.action?clientId="+hmInnerSubDoc.get("CLIENT_ID")+"&proId="+hmInnerSubDoc.get("PRO_ID")+"&folderName="+hmInnerSubDoc.get("DOCUMENT_NAME")+"&proFolderId="+hmInnerSubDoc.get("PRO_DOCUMENT_ID")+"&type=2&filePath="+URLEncoder.encode(filePath1)+"&fileDir="+URLEncoder.encode(fileDir);
																int nDocVersion = uF.parseToInt(hmInnerSubDoc.get("DOC_VERSION")) > 0 ? uF.parseToInt(hmInnerSubDoc.get("DOC_VERSION")) : 1;
														%>
																<div id="folderTR_<%=ii %>_<%=j %>_<%=k %>" style="float: left; width: 100%; margin-top: 10px; display: none;">
																	<div style="float: left; margin-left: 48px; width: 292px; margin-right: 9px;">
																		<%-- <a href="<%=filePath1 %>" style="font-weight: normal; color: black;"> --%>
																		<%-- <a href="javascript:void(0)" style="font-weight: normal; color: black;"onclick="projectDocFact('<%=hmInnerSubDoc.get("CLIENT_ID")%>','<%=hmInnerSubDoc.get("PRO_ID")%>','<%=hmInnerSubDoc.get("DOCUMENT_NAME") %>','<%=hmInnerSubDoc.get("PRO_DOCUMENT_ID") %>',2,'<%=filePath1 %>','<%=fileDir %>');" title="View Document"> --%>
																		<a target="_blank" href="<%=action %>" style="font-weight: normal; color: black;" title="View Document"> 
																			<img height="18" width="18" src="<%=fileIcon %>" />&nbsp;<%=hmInnerSubDoc.get("DOCUMENT_NAME") %>
																		</a> 
																		<div style="float: left; color: gray; width: 90%; margin-top: -5px; margin-left: 25px;"><%=hmInnerSubDoc.get("FILE_SIZE") %></div>
																		<div style="float: left; color: gray; width: 90%; margin-top: -5px; margin-left: 25px;"><%=hmInnerSubDoc.get("DESCRIPTION") %></div>
																		<div style="float: left; color: gray; width: 90%; margin-top: -5px; margin-left: 25px;"><%=hmInnerSubDoc.get("ENTRY_DATE") %></div>
																		<div style="float: left; color: gray; width: 90%; margin-top: -5px; margin-left: 25px;">
																			<span style="float: left;">
																				<%-- <a href="javascript:void(0)" style="font-weight: normal;"onclick="projectDocFact('<%=hmInnerSubDoc.get("CLIENT_ID")%>','<%=hmInnerSubDoc.get("PRO_ID")%>','<%=hmInnerSubDoc.get("DOCUMENT_NAME") %>','<%=hmInnerSubDoc.get("PRO_DOCUMENT_ID") %>',2,'<%=filePath1 %>','<%=fileDir %>');" title="View Document"> --%>
																				<a target="_blank" href="<%=action %>" style="font-weight: normal;" title="View Document">
																					View File
																				</a>
																	 		|</span>
																			<span style="float: left; margin-left: 2px;"><a href="<%=filePath1 %>" style="font-weight: normal;">Download</a></span>
																		</div>
																		
																	</div>
																	<div style="float: left; width: 120px; margin-right: 9px;">
																		<div style="float: left; width: 100%;">Version <strong><%=nDocVersion %></strong> </div>
																		<div style="float: left; width: 100%; margin-top: -5px;">
																			<%if(nDocVersion > 1){ %>
																				<span style="float: left;">Version history </span> 
																				<input type="hidden" name="proDocsSpanStatus" id="proDocsSpanStatus<%=ii %>_<%=j %>_<%=k %>" value = "0"/> 
																				<a href="javascript:void(0);" style="font-weight: normal; color: black;" onclick="viewVersionHistory(<%=hmInnerSubDoc.get("PRO_DOCUMENT_ID") %>,'proDocsSpanStatus<%=ii %>_<%=j %>_<%=k %>','proDocsVersionDiv<%=ii %>_<%=j %>_<%=k %>','VHDownarrowSpan<%=ii %>_<%=j %>_<%=k %>', 'VHUparrowSpan<%=ii %>_<%=j %>_<%=k %>','2','<%=filePath1%>','<%=fileDir %>')">
																					<span id="VHDownarrowSpan<%=ii %>_<%=j %>_<%=k %>" style="float: left; margin-left: 2px; margin-top: 3px;"> 
																						<i class="fa fa-angle-down" aria-hidden="true" style="width: 14px;"></i> 
																					</span>
																					<span id="VHUparrowSpan<%=ii %>_<%=j %>_<%=k %>" style="float: left; margin-left: 2px; margin-top: 3px; display: none;">
																						<i class="fa fa-angle-up" aria-hidden="true" style="width: 14px;"></i>
																					</span>
																				</a>
																			<%} %>
																		</div>
																	</div>
																	<div style="float: left; width: 120px; margin-right: 9px;">
																		<div style="float: left; width: 100%;">Aligned with</div>
																		<div style="float: left; width: 100%; margin-top: -5px;"><%=hmInnerSubDoc.get("ALIGN") %></div>
																	</div>
																	<div style="float: left; width: 120px; margin-right: 9px;">
																		<div style="float: left; width: 100%;">Shared with</div>
																		<div style="float: left; width: 100%; margin-top: -5px;"><%=hmInnerSubDoc.get("SHARING_TYPE") %></div>
																	</div>
																	<div style="float: left; width: 90px;">
																		<div style="float: left; width: 100%;">Actions</div>
																		<div style="float: left; width: 100%;">
																			<select name="proCatAction<%=ii %>_<%=j %>_<%=k %>" id="proCatAction<%=ii %>_<%=j %>_<%=k %>" style="width: 80px !important;" onchange="executeFolderActions(this.value, '<%=ii %>_<%=j %>_<%=k %>', 'SFD', '<%=hmInnerSubDoc.get("PRO_DOCUMENT_ID") %>', '<%=filePath1 %>', '<%=fileDir %>', '<%=hmInnerSubDoc.get("DOCUMENT_NAME") %>', 'folderTR_<%=ii %>_<%=j %>_<%=k %>', '<%=fileSavePath %>');">
														                    	<option value="">Actions</option>
														                    	<option value="1">Edit</option>
													                    		<option value="2">Delete</option>
													                    		<option value="3">Copy</option>
														                    </select>
																			<%-- <a href="javascript:void(0)" class="edit_lvl" style="float: right;" onclick="editDoc('<%=hmInnerSubDoc.get("CLIENT_ID")%>','<%=hmInnerSubDoc.get("PRO_ID")%>','<%=hmInnerSubDoc.get("DOCUMENT_NAME") %>','<%=hmInnerSubDoc.get("PRO_DOCUMENT_ID") %>',2,'<%=filePath1 %>','<%=fileDir %>');" title="Edit Document">Edit</a> --%>
																		</div>
																	</div>
																	
																</div>
																<div id="proDocsVersionDiv<%=ii %>_<%=j %>_<%=k %>" style="float: left; width: 100%; margin-top: 10px; display: none;"></div>
														<%} %>
													</div>
												<%} %>
												<%
												for(int j = 0; j < alFolderDoc.size();j++){
													Map<String, String> hmInnerDoc = (Map<String, String>) alFolderDoc.get(j);
													String filePath1 = proDocRetrivePath+strOrgId+"/Projects/"+hmInnerFolder.get("PRO_ID")+"/"+hmInnerFolder.get("FOLDER_NAME")+"/"+hmInnerDoc.get("DOCUMENT_NAME");
													String fileDir = proDocRetrivePath+strOrgId+"/Projects/"+hmInnerFolder.get("PRO_ID")+"/"+hmInnerFolder.get("FOLDER_NAME");
													
													String fileSavePath = proDocMainPath+strOrgId+"/Projects/"+hmInnerFolder.get("PRO_ID")+"/"+hmInnerFolder.get("FOLDER_NAME")+"/"+hmInnerDoc.get("DOCUMENT_NAME");
													if(uF.parseToInt(hmInnerFolder.get("PRO_ID")) == 0 && uF.parseToInt(hmInnerFolder.get("CATEGORY_ID")) == 2) {
														filePath1 = proDocRetrivePath+strOrgId+"/Categories/"+hmInnerFolder.get("FOLDER_NAME")+"/"+hmInnerDoc.get("DOCUMENT_NAME");
														fileDir = proDocRetrivePath+strOrgId+"/Categories/"+hmInnerFolder.get("FOLDER_NAME");
														fileSavePath = proDocMainPath+strOrgId+"/Categories/"+hmInnerFolder.get("FOLDER_NAME")+"/"+hmInnerDoc.get("DOCUMENT_NAME");
													}
													
													nFiles++;
													
													String fileIcon = request.getContextPath()+"/images1/icons/icons/file_icon.png";
													if(hmFileIcon.containsKey(hmInnerDoc.get("FILE_EXTENSION"))){ 
														fileIcon = request.getContextPath()+"/images1/file_icon/"+hmFileIcon.get(hmInnerDoc.get("FILE_EXTENSION"));
													}
													String action = "ProjectDocumentFact.action?clientId="+hmInnerDoc.get("CLIENT_ID")+"&proId="+hmInnerDoc.get("PRO_ID")+"&folderName="+hmInnerDoc.get("DOCUMENT_NAME")+"&proFolderId="+hmInnerDoc.get("PRO_DOCUMENT_ID")+"&type=3&filePath="+URLEncoder.encode(filePath1)+"&fileDir="+URLEncoder.encode(fileDir);
													int nDocVersion = uF.parseToInt(hmInnerDoc.get("DOC_VERSION")) > 0 ? uF.parseToInt(hmInnerDoc.get("DOC_VERSION")) : 1;
												%>
													<div id="docFolderTR_<%=ii %>_<%=j %>" style="float: left; width: 100%; margin-top: 10px; display: none;"> 
														<div style="float: left; margin-left: 23px; width: 317px; margin-right: 9px;">
															<%-- <a href="<%=filePath1 %>" style="font-weight: normal; color: black;"> --%>
															<%-- <a href="javascript:void(0)" style="font-weight: normal; color: black;"onclick="projectDocFact('<%=hmInnerDoc.get("CLIENT_ID")%>','<%=hmInnerDoc.get("PRO_ID")%>','<%=hmInnerDoc.get("DOCUMENT_NAME") %>','<%=hmInnerDoc.get("PRO_DOCUMENT_ID") %>',3,'<%=filePath1 %>','<%=fileDir %>');" title="View Document"> --%>
															<a target="_blank" href="<%=action %>" style="font-weight: normal; color: black;" title="View Document"> 
																<img height="18" width="18" src="<%=fileIcon %>" />&nbsp;<%=hmInnerDoc.get("DOCUMENT_NAME") %>
															</a>
															<div style="float: left; color: gray; width: 90%; margin-top: -5px; margin-left: 25px;"><%=hmInnerDoc.get("FILE_SIZE") %></div>
															<div style="float: left; color: gray; width: 90%; margin-top: -5px; margin-left: 25px;"><%=hmInnerDoc.get("DESCRIPTION") %></div>
															<div style="float: left; color: gray; width: 90%; margin-top: -5px; margin-left: 25px;"><%=hmInnerDoc.get("ENTRY_DATE") %></div>
															<div style="float: left; color: gray; width: 90%; margin-top: -5px; margin-left: 25px;">
																<span style="float: left;">
																	<%-- <a href="javascript:void(0)" style="font-weight: normal;"onclick="projectDocFact('<%=hmInnerDoc.get("CLIENT_ID")%>','<%=hmInnerDoc.get("PRO_ID")%>','<%=hmInnerDoc.get("DOCUMENT_NAME") %>','<%=hmInnerDoc.get("PRO_DOCUMENT_ID") %>',3,'<%=filePath1 %>','<%=fileDir %>');" title="View Document"> --%>
																	<a target="_blank" href="<%=action %>" style="font-weight: normal;" title="View Document">
																		View File
																	</a>
																|</span>
																<span style="float: left; margin-left: 2px;"><a href="<%=filePath1 %>" style="font-weight: normal;">Download</a></span>
															</div>
															
														</div>
														<div style="float: left; width: 120px; margin-right: 9px;">
															<div style="float: left; width: 100%;">Version <strong><%=nDocVersion %></strong> </div>
															<div style="float: left; width: 100%; margin-top: -5px;">
																<%if(nDocVersion > 1){ %>
																	<span style="float: left;">Version history </span> 
																	<input type="hidden" name="proDocsSpanStatus" id="proDocsSpanStatus<%=ii %>_<%=j %>" value = "0"/> 
																	<a href="javascript:void(0);" style="font-weight: normal; color: black;" onclick="viewVersionHistory(<%=hmInnerDoc.get("PRO_DOCUMENT_ID") %>,'proDocsSpanStatus<%=ii %>_<%=j %>','proDocsVersionDiv<%=ii %>_<%=j %>','VHDownarrowSpan<%=ii %>_<%=j %>', 'VHUparrowSpan<%=ii %>_<%=j %>','3','<%=filePath1%>','<%=fileDir %>')">
																		<span id="VHDownarrowSpan<%=ii %>_<%=j %>" style="float: left; margin-left: 2px; margin-top: 3px;"> 
																			<i class="fa fa-angle-down" aria-hidden="true" style="width: 14px;"></i> 
																		</span>
																		<span id="VHUparrowSpan<%=ii %>_<%=j %>" style="float: left; margin-left: 2px; margin-top: 3px; display: none;">
																			<i class="fa fa-angle-up" aria-hidden="true" style="width: 14px;"></i>
																		</span>
																	</a>
																<%} %>
															</div>
														</div>
														<div style="float: left; width: 120px; margin-right: 9px;">
															<div style="float: left; width: 100%;">Aligned with</div>
															<div style="float: left; width: 100%; margin-top: -5px;"><%=hmInnerDoc.get("ALIGN") %></div>
														</div>
														<div style="float: left; width: 120px; margin-right: 9px;">
															<div style="float: left; width: 100%;">Shared with</div>
															<div style="float: left; width: 100%; margin-top: -5px;"><%=hmInnerDoc.get("SHARING_TYPE") %></div>
														</div>
														<div style="float: left; width: 90px;">
															<div style="float: left; width: 100%;">Actions</div>
															<div style="float: left; width: 100%;">
																<select name="proCatFDocAction<%=ii %>_<%=j %>" id="proCatFDocAction<%=ii %>_<%=j %>" style="width: 80px !important;" onchange="executeFolderActions(this.value, '<%=ii %>_<%=j %>', 'FD', '<%=hmInnerDoc.get("PRO_DOCUMENT_ID") %>', '<%=filePath1 %>', '<%=fileDir %>', '<%=hmInnerDoc.get("DOCUMENT_NAME") %>', 'docFolderTR_<%=ii %>_<%=j %>', '<%=fileSavePath %>');">
											                    	<option value="">Actions</option>
											                    	<option value="1">Edit</option>
										                    		<option value="2">Delete</option>
													                <option value="3">Copy</option>
											                    </select>
																<%-- <a href="javascript:void(0)" class="edit_lvl" style="float: right;" onclick="editDoc('<%=hmInnerDoc.get("CLIENT_ID")%>','<%=hmInnerDoc.get("PRO_ID")%>','<%=hmInnerDoc.get("DOCUMENT_NAME") %>','<%=hmInnerDoc.get("PRO_DOCUMENT_ID") %>',3,'<%=filePath1 %>','<%=fileDir %>');" title="Edit Document">Edit</a> --%>
															</div>
														</div>
														
													</div>
													<div id="proDocsVersionDiv<%=ii %>_<%=j %>" style="float: left; width: 100%; margin-top: 10px; display: none;"></div>
												<%} %>
												
											</div>
										<%} %>
										
										
									<%-- <%} %> --%>
									<%
									
									/* Iterator<String> it1 = hmDoc.keySet().iterator();
									while(it1.hasNext()) {
										String proFolderId = it1.next();
										List<Map<String, String>> alDoc = (List<Map<String, String>>) hmDoc.get(proFolderId);
										if(alDoc == null) alDoc = new ArrayList<Map<String,String>>(); */
										
										for(int i = 0; i < alMainDoc.size(); i++) {
											Map<String, String> hmInnerDoc = (Map<String, String>) alMainDoc.get(i);
											String filePath1 = proDocRetrivePath+strOrgId+"/Projects/"+hmInnerDoc.get("PRO_ID")+"/"+hmInnerDoc.get("DOCUMENT_NAME");
											String fileDir = proDocRetrivePath+strOrgId+"/Projects/"+hmInnerDoc.get("PRO_ID");
											
											String fileSavePath = proDocMainPath+strOrgId+"/Projects/"+hmInnerDoc.get("PRO_ID")+"/"+hmInnerDoc.get("DOCUMENT_NAME");
											if(uF.parseToInt(hmInnerDoc.get("PRO_ID")) == 0 && uF.parseToInt(hmInnerDoc.get("CATEGORY_ID")) == 2) {
												filePath1 = proDocRetrivePath+strOrgId+"/Categories/"+hmInnerDoc.get("DOCUMENT_NAME");
												fileDir = proDocRetrivePath+strOrgId+"/Categories/";
												fileSavePath = proDocMainPath+strOrgId+"/Categories/"+hmInnerDoc.get("DOCUMENT_NAME");
											}
											
											nFiles++;
											String fileIcon = request.getContextPath()+"/images1/icons/icons/file_icon.png";
											if(hmFileIcon.containsKey(hmInnerDoc.get("FILE_EXTENSION"))) {
												fileIcon = request.getContextPath()+"/images1/file_icon/"+hmFileIcon.get(hmInnerDoc.get("FILE_EXTENSION"));
											}
											String action = "ProjectDocumentFact.action?clientId="+hmInnerDoc.get("CLIENT_ID")+"&proId="+hmInnerDoc.get("PRO_ID")+"&folderName="+hmInnerDoc.get("DOCUMENT_NAME")+"&proFolderId="+hmInnerDoc.get("PRO_DOCUMENT_ID")+"&type=3&filePath="+URLEncoder.encode(filePath1)+"&fileDir="+URLEncoder.encode(fileDir);
											int nDocVersion = uF.parseToInt(hmInnerDoc.get("DOC_VERSION")) > 0 ? uF.parseToInt(hmInnerDoc.get("DOC_VERSION")) : 1;
									%>
											<div id="docTR_<%=i %>" style="float: left; margin-top: 10px;">
												<div style="float: left; width: 240px; margin-right: 9px;">
													<%-- <a href="<%=filePath1 %>" style="font-weight: normal; color: black;"> --%>
													<%-- <a href="javascript:void(0)" style="font-weight: normal; color: black;"onclick="projectDocFact('<%=hmInnerDoc.get("CLIENT_ID")%>','<%=hmInnerDoc.get("PRO_ID")%>','<%=hmInnerDoc.get("DOCUMENT_NAME") %>','<%=hmInnerDoc.get("PRO_DOCUMENT_ID") %>',3,'<%=filePath1 %>','<%=fileDir %>');" title="View Document"> --%>
													<a target="_blank" href="<%=action %>" style="font-weight: normal; color: black;" title="View Document"> 
														<img height="18" width="18" src="<%=fileIcon %>" />&nbsp;<%=hmInnerDoc.get("DOCUMENT_NAME") %>
													</a>
													<div style="float: left; color: gray; width: 90%; margin-top: -5px; margin-left: 25px;"><%=hmInnerDoc.get("FILE_SIZE") %></div>
													<div style="float: left; color: gray; width: 90%; margin-top: -5px; margin-left: 25px;"><%=hmInnerDoc.get("DESCRIPTION") %></div>
													<div style="float: left; color: gray; width: 90%; margin-top: -5px; margin-left: 25px;"><%=hmInnerDoc.get("ENTRY_DATE") %></div>
													<div style="float: left; color: gray; width: 90%; margin-top: -5px; margin-left: 25px;">
														<span style="float: left;">
															<%-- <a href="javascript:void(0)" style="font-weight: normal;"onclick="projectDocFact('<%=hmInnerDoc.get("CLIENT_ID")%>','<%=hmInnerDoc.get("PRO_ID")%>','<%=hmInnerDoc.get("DOCUMENT_NAME") %>','<%=hmInnerDoc.get("PRO_DOCUMENT_ID") %>',3,'<%=filePath1 %>','<%=fileDir %>');" title="View Document"> --%>
															<a target="_blank" href="<%=action %>" style="font-weight: normal;" title="View Document">
																View File
															</a>
														|</span>
														<span style="float: left; margin-left: 2px;"><a href="<%=filePath1 %>" style="font-weight: normal;">Download</a></span>
													</div>
													
												</div>
												<div style="float: left; width: 120px; margin-right: 9px;">
													<div style="float: left; width: 100%;">Version <strong><%=nDocVersion %></strong> </div>
													<div style="float: left; width: 100%; margin-top: -5px;">
														<%if(nDocVersion > 1){ %>
															<span style="float: left;">Version history </span> 
															<input type="hidden" name="proDocsSpanStatus" id="proDocsSpanStatus<%=i %>" value = "0"/> 
															<a href="javascript:void(0);" style="font-weight: normal; color: black;" onclick="viewVersionHistory(<%=hmInnerDoc.get("PRO_DOCUMENT_ID") %>,'proDocsSpanStatus<%=i %>','proDocsVersionDiv<%=i %>','VHDownarrowSpan<%=i %>', 'VHUparrowSpan<%=i %>','3','<%=filePath1%>','<%=fileDir %>')">
																<span id="VHDownarrowSpan<%=i %>" style="float: left; margin-left: 2px; margin-top: 3px;"> 
																	<i class="fa fa-angle-down" aria-hidden="true" style="width: 14px;"></i>
																</span>
																<span id="VHUparrowSpan<%=i %>" style="float: left; margin-left: 2px; margin-top: 3px; display: none;">
																	<i class="fa fa-angle-up" aria-hidden="true" style="width: 14px;"></i>
																</span>
															</a>
														<%} %>
													</div>
												</div>
												<div style="float: left; width: 120px; margin-right: 9px;">
													<div style="float: left; width: 100%;">Aligned with</div>
													<div style="float: left; width: 100%; margin-top: -5px;"><%=hmInnerDoc.get("ALIGN") %></div>
												</div>
												<div style="float: left; width: 120px;">
													<div style="float: left; width: 100%; margin-right: 9px;">Shared with</div>
													<div style="float: left; width: 100%; margin-top: -5px;"><%=hmInnerDoc.get("SHARING_TYPE") %></div>
												</div>
												<div style="float: left; width: 90px;">
													<div style="float: left; width: 100%;">Actions</div>
													<div style="float: left; width: 100%;">
														<select name="proCatDocAction<%=i %>" id="proCatDocAction<%=i %>" style="width: 80px !important;" onchange="executeFolderActions(this.value, '<%=i %>', 'D', '<%=hmInnerDoc.get("PRO_DOCUMENT_ID") %>', '<%=filePath1 %>', '<%=fileDir %>', '<%=hmInnerDoc.get("DOCUMENT_NAME") %>', 'docTR_<%=i %>', '<%=fileSavePath %>');">
									                    	<option value="">Actions</option>
									                    	<option value="1">Edit</option>
								                    		<option value="2">Delete</option>
													        <option value="3">Copy</option>
									                    </select>
														<%-- <a href="javascript:void(0)" class="edit_lvl" style="float: right;" onclick="editDoc('<%=hmInnerDoc.get("CLIENT_ID")%>','<%=hmInnerDoc.get("PRO_ID")%>','<%=hmInnerDoc.get("DOCUMENT_NAME") %>','<%=hmInnerDoc.get("PRO_DOCUMENT_ID") %>',1,'<%=filePath1 %>','<%=fileDir %>');" title="Edit Document">Edit</a> --%>
													</div>
												</div>
												
											</div>
											<div id="proDocsVersionDiv<%=i %>" style="float: left; margin-top: 10px; display: none;"></div>
										<% } %>
									<%-- <%  } %> --%>	
							<%} else {%>
								<div style="float: left; width: 96%;" class="nodata msg">
									<span>No documents attached.</span>
								</div>
							<%} %>
							<input type="hidden" name="folderCnt" id="folderCnt" value="<%=nFolder%>"/>
							<input type="hidden" name="subFolderCnt" id="subFolderCnt" value="<%=nSubFolder%>"/>
							<input type="hidden" name="fileCnt" id="fileCnt" value="<%=nFiles%>"/>
							
							<%-- <script type="text/javascript">
								var timer = window.setTimeout(function() {
									getFolderCount();
								}, 400);
							</script> --%>
						</div>
						
						<% } %>
					</td>
				</tr>
			</table>
		</div>
		<div class="clr"></div>
	
	<%-- <% if(proType ==null || proType.equals("null") || proType.equals("") || proType.equals("L")) { %>
		<div style="float: left; width: 100%; text-align: center; margin-bottom: 5px;">
			<input type="submit" value="Save" class="input_button" style="margin-right: 5px;" name="btnSave"/>
			<input type="button" value="Cancel" class="cancel_button" style="margin-right: 5px;" name="cancel" onclick="viewDocuments('<%=proType %>');">
		</div>
	<% } %> --%>
	</form>
<%-- <% } %> --%>
</div>
