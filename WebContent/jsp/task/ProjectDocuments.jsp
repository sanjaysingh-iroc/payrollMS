<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.Iterator"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="java.util.List"%>
<%@taglib uri="/struts-tags" prefix="s"%>


<% 	int proId = (Integer)request.getAttribute("proId");
	String proType = (String)request.getAttribute("proType");
	String pageType = (String)request.getAttribute("pageType");
	
	String strUserType = (String)session.getAttribute(IConstants.BASEUSERTYPE);
	UtilityFunctions uF = new UtilityFunctions();
	
	String taskId = (String)request.getAttribute("taskId");
	String fromPage = (String)request.getAttribute("fromPage");
	Map<String, String> hmFileIcon = (Map<String, String>) request.getAttribute("hmFileIcon");
	if(hmFileIcon == null) hmFileIcon = new HashMap<String, String>();
	
	//System.out.println("fromPage ===>> " + fromPage);
	if(fromPage != null && fromPage.equalsIgnoreCase("MyProject")) {
%>
		<form id="formProjectDocuments<%=proId %>_<%=taskId %>" class="formcss" action="ProjectDocuments.action" name="frmProjectDocuments<%=proId %>_<%=taskId %>" method="post" enctype="multipart/form-data" onsubmit="showLoading();">
		<div class="box-body table-responsive no-padding">
		<!-- <div id="tblDiv" style="float: left; width: 100%;"> -->
			<s:hidden name="proId"></s:hidden>
			<s:hidden name="fromPage"></s:hidden>
			<s:hidden name="taskId"></s:hidden>
			<s:hidden name="pageType"></s:hidden>
			<!-- <h3 style="margin-bottom: -10px; margin-left: 27px;">Documents</h3> -->
			<table class="table table-hover">
				<tr>
					<td class="txtlabel alignRight" valign="top">Documents:</td>
					<!-- <td nowrap="nowrap" class="txtlabel" valign="top"> -->
					<td>
					<% if(proType ==null || proType.equals("null") || proType.equals("") || !proType.equals("C")) { %>
						&nbsp;&nbsp;&nbsp;
						<input type="hidden" name="folderDocscount<%=proId %>_<%=taskId %>" id="folderDocscount<%=proId %>_<%=taskId %>" value="0" />
						<input type="hidden" name="projectTasks<%=proId %>_<%=taskId %>" id="projectTasks<%=proId %>_<%=taskId %>" value="<%=(String)request.getAttribute("sbProTasks") %>" />
						<input type="hidden" name="resourceIds<%=proId %>_<%=taskId %>" id="resourceIds<%=proId %>_<%=taskId %>" value="<%=(String)request.getAttribute("sbProEmp") %>" />
						<input type="hidden" name="projectCategoryType<%=proId %>_<%=taskId %>" id="projectCategoryType<%=proId %>_<%=taskId %>" value="<%=(String)request.getAttribute("sbProCategory") %>" />
						<input type="hidden" name="projectPoc<%=proId %>_<%=taskId %>" id="projectPoc<%=proId %>_<%=taskId %>" value="<%=(String)request.getAttribute("sbProSPOC") %>" />
						<a href="javascript:void(0);" style="font-size: 13px; width: auto;" onclick="addNewFolder('<%=proId %>', '<%=taskId %>', 'documentTable<%=proId %>_<%=taskId %>', 'folderDocscount<%=proId %>_<%=taskId %>'), showTblHeader('<%=proId %>', '<%=taskId %>');"><i class="fa fa-plus-circle"></i>Add Folder</a> 
						<a href="javascript:void(0);" style="font-size: 13px; width: auto; margin-left: 7px;" onclick="addNewDocs('<%=proId %>', '<%=taskId %>', 'documentTable<%=proId %>_<%=taskId %>', 'folderDocscount<%=proId %>_<%=taskId %>'), showTblHeader('<%=proId %>', '<%=taskId %>');"><i class="fa fa-plus-circle"></i>Add Document</a>
					<% } %>	
					</td>
				</tr>
				<tr>
					<td colspan="2">
						<table class="table table-hover" id="documentTable<%=proId %>_<%=taskId %>" style="width: 100%; margin-bottom: 0px;">
							<tr id="folderTR<%=proId %>_<%=taskId %>_0" style="display: none;">
								<td style="width: 50%;">Folder/Documents</td>
								<!-- <td style="width: 20%;">Scope Document</td> -->
								<td style="width: 12%;">Category</td>
								<td style="width: 12%;">Alignment</td>
								<td style="min-width: 15%;">Sharing</td>
								<td >Rights</td>
							</tr>
						</table>
					<div class="clr"></div>
					<% if(proType ==null || proType.equals("null") || proType.equals("") || !proType.equals("C")) { %>
						<div id="buttonDiv<%=proId %>_<%=taskId %>" style="display: none; width: 100%; text-align: center; padding: 5px 0px; border-top: 1px solid #CCCCCC;">
							<input type="submit" value="Save" class="btn btn-primary" style="margin-right: 5px; padding: 3px;" name="btnSave"/>
							<input type="button" value="Cancel" class="btn btn-danger" style="margin-right: 5px; padding: 3px;" name="cancel" onclick="viewDocuments('<%=taskId %>', '<%=proId %>', '<%=proType %>');">
						</div>
					<% } %>
					</td>
				</tr>
				<tr>
					<td colspan="2">
						
					<div style="float: left;">  <!-- border: 1px solid gray; -->
						<% 	
							//Map<String, List<Map<String,String>>> hmFolder = (Map<String, List<Map<String,String>>>)request.getAttribute("hmFolder");
							List<Map<String, String>> alProFolder = (List<Map<String, String>>) request.getAttribute("alProFolder");
							//Map<String, List<Map<String,String>>> hmDoc = (Map<String, List<Map<String,String>>>)request.getAttribute("hmDoc");
							List<Map<String, String>> alMainDoc = (List<Map<String, String>>) request.getAttribute("alMainDoc");
							
							Map<String, List<Map<String,String>>> hmSubFolder = (Map<String, List<Map<String,String>>>)request.getAttribute("hmSubFolder");
							Map<String, List<Map<String,String>>> hmSubDoc = (Map<String, List<Map<String,String>>>)request.getAttribute("hmSubDoc");
							
							String proDocMainPath = (String) request.getAttribute("proDocMainPath");
							String proDocRetrivePath = (String) request.getAttribute("proDocRetrivePath");
							String strOrgId = (String) request.getAttribute("strOrgId");
							
							//System.out.println("alProFolder ===>> " + alProFolder + " - hmDoc ===>> " + hmDoc);
							if((alProFolder != null && !alProFolder.isEmpty()) || (alMainDoc !=null && !alMainDoc.isEmpty())) {
						%>
							<% 	
									//System.out.println("alProFolder ===>> " + alProFolder.size());
									for(int ii=0; alProFolder!=null && ii<alProFolder.size(); ii++) {
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
								%>
										<div id="folderTR_<%=proId %>_<%=taskId %>_<%=ii %>" style="float: left;">
											<div style="float: left; width: 340px; margin-right: 9px;">
												<input type="hidden" name="hideFolder_<%=proId %>_<%=taskId %>_<%=ii %>" id="hideFolder_<%=proId %>_<%=taskId %>_<%=ii %>" value="0" /> 
												<div style="float: left; margin-right: 5px;"> <a href="javascript:void(0);" style="font-weight: normal; color: black;" onclick="openCloseDocs('<%=proId %>_<%=taskId %>', '<%=ii %>', '<%=alSubFolder.size()%>', '<%=alFolderDoc.size()%>');"> 
												<%-- <img height="15" width="20" src="<%=request.getContextPath()%>/images1/icons/icons/folder_icon.png" /> --%>
												<i class="fa fa-folder-open-o" style="font-size: 20px;height: 15px;"></i>
												
												 </a> </div>
												<strong><%=hmInnerFolder.get("FOLDER_NAME") %></strong>
												<div style="float: left; color: gray; width: 90%; margin-top: -5px; margin-left: 25px;"><%=fileCount +" items" %></div>
												<div style="float: left; color: gray; width: 90%; margin-top: -5px; margin-left: 25px;"><%=hmInnerFolder.get("DESCRIPTION") %></div>
												<div style="float: left; color: gray; width: 90%; margin-top: -5px; margin-left: 25px;"><%=hmInnerFolder.get("ENTRY_DATE") %></div>
												<% if(uF.parseToInt(fileCount)>0) { %>
												<div style="float: left; color: gray; width: 90%; margin-top: -5px; margin-left: 25px;">
												<span style="float: left;"><%=fileCount +" items" %> view folders & files </span> 
												<a href="javascript:void(0);" style="font-weight: normal; color: black;" onclick="openCloseDocs('<%=proId %>_<%=taskId %>', '<%=ii %>', '<%=alSubFolder.size()%>', '<%=alFolderDoc.size()%>');">
													<span id="FDDownarrowSpan_<%=proId %>_<%=taskId %>_<%=ii %>" style="float: left; margin-left: 2px; margin-top: 3px;"> 
														<i class="fa fa-chevron-down" style="padding: 0px;"></i> 
													</span>
													<span id="FDUparrowSpan_<%=proId %>_<%=taskId %>_<%=ii %>" style="float: left; margin-left: 2px; margin-top: 3px; display: none;">
														<i class="fa fa-chevron-up" style="padding: 0px;"></i>
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
											<% if(uF.parseToBoolean(hmInnerFolder.get("EDIT_STATUS_VAL")) || uF.parseToBoolean(hmInnerFolder.get("DELETE_STATUS_VAL"))) { %>
												<div style="float: left; width: 100%;">Actions</div>
												<div style="float: left; width: 100%;">
													<select name="proCatAction_<%=proId %>_<%=taskId %>_<%=ii %>" id="proCatAction_<%=proId %>_<%=taskId %>_<%=ii %>" style="width: 80px !important;" onchange="executeFolderActions('<%=taskId %>', this.value, '<%=hmInnerFolder.get("CLIENT_ID")%>','<%=hmInnerFolder.get("PRO_ID")%>','<%=hmInnerFolder.get("FOLDER_NAME") %>','<%=hmInnerFolder.get("PRO_DOCUMENT_ID") %>', 'F', '', '', 'folderTR_<%=proId %>_<%=taskId %>_<%=ii %>', '<%=folderSavePath %>', '<%=pageType %>');">
								                    	<option value="">Actions</option>
								                    	<% if(uF.parseToBoolean(hmInnerFolder.get("EDIT_STATUS_VAL"))) { %>
								                    		<option value="1">Edit</option>
								                    	<% } %>
								                    	<% if(uF.parseToBoolean(hmInnerFolder.get("DELETE_STATUS_VAL"))) { %>
							                    			<option value="2">Delete</option>
							                    		<% } %>
								                    </select>
												</div>
											<% } %>
											</div>
											
											<%
											for(int j = 0; j < alSubFolder.size(); j++){
												Map<String, String> hmInnerSubFolder = (Map<String, String>) alSubFolder.get(j);
												List<Map<String, String>> alDoc = (List<Map<String, String>>) hmSubDoc.get(hmInnerSubFolder.get("PRO_DOCUMENT_ID")); 
												if(alDoc == null) alDoc = new ArrayList<Map<String,String>>();
												
												String subFolderSavePath = proDocMainPath+strOrgId+"/Projects/"+hmInnerSubFolder.get("PRO_ID")+"/"+hmInnerFolder.get("FOLDER_NAME")+"/"+hmInnerSubFolder.get("FOLDER_NAME");
												String fileSubCount ="-";
												if(alDoc !=null && alDoc.size() > 0) {
													fileSubCount = alDoc.size()+"";
												} 
											%>
												<div id="folderTR_<%=proId %>_<%=taskId %>_<%=ii %>_<%=j %>"  style="float: left; width: 100%; margin-top: 10px; display: none;">
													
													<div style="float: left; margin-left: 23px; width: 317px; margin-right: 9px;">
														<input type="hidden" name="hideFolder_<%=proId %>_<%=taskId %>_<%=ii %>_<%=j %>" id="hideFolder_<%=proId %>_<%=taskId %>_<%=ii %>_<%=j %>" value="0" /> 
														<div style="float: left; margin-right: 5px;"> <a href="javascript:void(0);" style="font-weight: normal; color: black;" onclick="openCloseDocs1('<%=proId %>_<%=taskId %>', '<%=ii %>', '<%=j %>', '<%=alDoc.size() %>');"> 
														<%-- <img height="15" width="20" src="<%=request.getContextPath()%>/images1/icons/icons/folder_icon.png" /> --%> 
														
														<i class="fa fa-folder-open-o" style="font-size: 20px;height: 15px;"></i></a> </div>
														
														<strong><%=hmInnerSubFolder.get("FOLDER_NAME") %></strong>
														<div style="float: left; color: gray; width: 90%; margin-top: -5px; margin-left: 25px;"><%=fileSubCount +" items" %></div>
														<div style="float: left; color: gray; width: 90%; margin-top: -5px; margin-left: 25px;"><%=hmInnerSubFolder.get("DESCRIPTION") %></div>
														<div style="float: left; color: gray; width: 90%; margin-top: -5px; margin-left: 25px;"><%=hmInnerSubFolder.get("ENTRY_DATE") %></div>
														<% if(uF.parseToInt(fileSubCount)>0) { %>
														<div style="float: left; color: gray; width: 90%; margin-top: -5px; margin-left: 25px;">
														<span style="float: left;"><%=fileSubCount +" items" %> view files </span> 
														<a href="javascript:void(0);" style="font-weight: normal; color: black;" onclick="openCloseDocs1('<%=proId %>_<%=taskId %>', '<%=ii %>', '<%=j %>', '<%=alDoc.size() %>');">
															<span id="FDDownarrowSpan_<%=proId %>_<%=taskId %>_<%=ii %>_<%=j %>" style="float: left; margin-left: 2px; margin-top: 3px;"> 
																<i class="fa fa-chevron-down" style="padding: 0px;"></i> 
															</span>
															<span id="FDUparrowSpan_<%=proId %>_<%=taskId %>_<%=ii %>_<%=j %>" style="float: left; margin-left: 2px; margin-top: 3px; display: none;">
																<i class="fa fa-chevron-up" style="padding: 0px;"></i>
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
														<% if(uF.parseToBoolean(hmInnerSubFolder.get("EDIT_STATUS_VAL")) || uF.parseToBoolean(hmInnerSubFolder.get("DELETE_STATUS_VAL"))) { %>
															<div style="float: left; width: 100%;">Actions</div>
															<div style="float: left; width: 100%;">
																<select name="proCatAction_<%=proId %>_<%=taskId %>_<%=ii %>_<%=j %>" id="proCatAction_<%=proId %>_<%=taskId %>_<%=ii %>_<%=j %>" style="width: 80px !important;" onchange="executeFolderActions('<%=taskId %>', this.value, '<%=hmInnerSubFolder.get("CLIENT_ID")%>','<%=hmInnerSubFolder.get("PRO_ID")%>','<%=hmInnerSubFolder.get("FOLDER_NAME") %>','<%=hmInnerSubFolder.get("PRO_DOCUMENT_ID") %>', 'SF', '', '', 'folderTR_<%=proId %>_<%=taskId %>_<%=ii %>_<%=j %>', '<%=subFolderSavePath %>', '<%=pageType %>');">
											                    	<option value="">Actions</option>
											                    	<% if(uF.parseToBoolean(hmInnerSubFolder.get("EDIT_STATUS_VAL"))) { %>
											                    		<option value="1">Edit</option>
											                    	<% } %>
											                    	<% if(uF.parseToBoolean(hmInnerSubFolder.get("DELETE_STATUS_VAL"))) { %>
										                    			<option value="2">Delete</option>
										                    		<% } %>
											                    </select>
															</div>
														<% } %>
													</div>
													
													<%
														for(int k = 0; k<alDoc.size(); k++){
															Map<String, String> hmInnerSubDoc = (Map<String, String>) alDoc.get(k);
															String filePath1 = proDocRetrivePath+strOrgId+"/Projects/"+hmInnerSubDoc.get("PRO_ID")+"/"+hmInnerFolder.get("FOLDER_NAME")+"/"+hmInnerSubFolder.get("FOLDER_NAME")+"/"+hmInnerSubDoc.get("DOCUMENT_NAME");
															String fileDir = proDocRetrivePath+strOrgId+"/Projects/"+hmInnerSubDoc.get("PRO_ID")+"/"+hmInnerFolder.get("FOLDER_NAME")+"/"+hmInnerSubFolder.get("FOLDER_NAME");

															String fileSavePath = proDocMainPath+strOrgId+"/Projects/"+hmInnerSubDoc.get("PRO_ID")+"/"+hmInnerFolder.get("FOLDER_NAME")+"/"+hmInnerSubFolder.get("FOLDER_NAME")+"/"+hmInnerSubDoc.get("DOCUMENT_NAME");
															
															String fileIcon = request.getContextPath()+"/images1/icons/icons/file_icon.png";
															if(hmFileIcon.containsKey(hmInnerSubDoc.get("FILE_EXTENSION"))){
																fileIcon = request.getContextPath()+"/images1/file_icon/"+hmFileIcon.get(hmInnerSubDoc.get("FILE_EXTENSION"));
															}
															String action = "ProjectDocumentFact.action?clientId="+hmInnerSubDoc.get("CLIENT_ID")+"&proId="+hmInnerSubDoc.get("PRO_ID")+"&folderName="+hmInnerSubDoc.get("DOCUMENT_NAME")+"&proFolderId="+hmInnerSubDoc.get("PRO_DOCUMENT_ID")+"&type=2&filePath="+URLEncoder.encode(filePath1)+"&fileDir="+URLEncoder.encode(fileDir);
															int nDocVersion = uF.parseToInt(hmInnerSubDoc.get("DOC_VERSION")) > 0 ? uF.parseToInt(hmInnerSubDoc.get("DOC_VERSION")) : 1;
													%>
															<div id="folderTR_<%=proId %>_<%=taskId %>_<%=ii %>_<%=j %>_<%=k %>" style="float: left; width: 100%; margin-top: 10px; display: none;">
																<div style="float: left; margin-left: 48px; width: 292px; margin-right: 9px;">
																	<%-- <a href="javascript:void(0)" style="font-weight: normal; color: black;" onclick="projectDocFact('<%=hmInnerSubDoc.get("CLIENT_ID")%>','<%=hmInnerSubDoc.get("PRO_ID")%>','<%=hmInnerSubDoc.get("DOCUMENT_NAME") %>','<%=hmInnerSubDoc.get("PRO_DOCUMENT_ID") %>',2,'<%=filePath1 %>','<%=fileDir %>');" title="View Document"> --%>
																	<a target="_blank" href="<%=action %>" style="font-weight: normal; color: black;" title="View Document"> 
																		<img height="18" width="18" src="<%=fileIcon %>" />&nbsp;<%=hmInnerSubDoc.get("DOCUMENT_NAME") %>
																	</a> 
																	<div style="float: left; color: gray; width: 90%; margin-top: -5px; margin-left: 25px;"><%=hmInnerSubDoc.get("FILE_SIZE") %></div>
																	<div style="float: left; color: gray; width: 90%; margin-top: -5px; margin-left: 25px;"><%=hmInnerSubDoc.get("DESCRIPTION") %></div>
																	<div style="float: left; color: gray; width: 90%; margin-top: -5px; margin-left: 25px;"><%=hmInnerSubDoc.get("ENTRY_DATE") %></div>
																	<div style="float: left; color: gray; width: 90%; margin-top: -5px; margin-left: 25px;">
																		<span style="float: left;">
																			<%-- <a href="javascript:void(0)" style="font-weight: normal;" onclick="projectDocFact('<%=hmInnerSubDoc.get("CLIENT_ID")%>','<%=hmInnerSubDoc.get("PRO_ID")%>','<%=hmInnerSubDoc.get("DOCUMENT_NAME") %>','<%=hmInnerSubDoc.get("PRO_DOCUMENT_ID") %>',2,'<%=filePath1 %>','<%=fileDir %>');" title="View Document">View File</a> --%>
																			<a target="_blank" href="<%=action %>" style="font-weight: normal;" title="View Document">View File</a>
																		|</span>
																		<span style="float: left; margin-left: 2px;"><a href="<%=filePath1 %>" style="font-weight: normal;">Download</a></span>
																	</div>
																	
																</div>
																<div style="float: left; width: 120px; margin-right: 9px;">
																	<div style="float: left; width: 100%;">Version <strong><%=nDocVersion %></strong> </div>
																	<div style="float: left; width: 100%; margin-top: -5px;">
																	<%if(nDocVersion > 1){ %>
																		<span style="float: left;">Version history </span>
																		<input type="hidden" name="proDocsSpanStatus" id="proDocsSpanStatus_<%=proId %>_<%=taskId %>_<%=ii %>_<%=j %>_<%=k %>" value = "0"/> 
																		<a href="javascript:void(0);" style="font-weight: normal; color: black;" onclick="viewVersionHistory(<%=hmInnerSubDoc.get("PRO_DOCUMENT_ID") %>,'proDocsSpanStatus_<%=proId %>_<%=taskId %>_<%=ii %>_<%=j %>_<%=k %>','proDocsVersionDiv_<%=proId %>_<%=taskId %>_<%=ii %>_<%=j %>_<%=k %>','VHDownarrowSpan_<%=proId %>_<%=taskId %>_<%=ii %>_<%=j %>_<%=k %>', 'VHUparrowSpan_<%=proId %>_<%=taskId %>_<%=ii %>_<%=j %>_<%=k %>','2','<%=filePath1%>','<%=fileDir %>')">
																			<span id="VHDownarrowSpan_<%=proId %>_<%=taskId %>_<%=ii %>_<%=j %>_<%=k %>" style="float: left; margin-left: 2px; margin-top: 3px;"> 
																				<i class="fa fa-chevron-down" style="padding: 0px;"></i> 
																			</span>
																			<span id="VHUparrowSpan_<%=proId %>_<%=taskId %>_<%=ii %>_<%=j %>_<%=k %>" style="float: left; margin-left: 2px; margin-top: 3px; display: none;">
																				<i class="fa fa-chevron-up" style="padding: 0px;"></i>
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
																		<% if(uF.parseToBoolean(hmInnerSubDoc.get("EDIT_STATUS_VAL")) || uF.parseToBoolean(hmInnerSubDoc.get("DELETE_STATUS_VAL"))) { %>
																		<div style="float: left; width: 100%;">Actions</div>
																		<div style="float: left; width: 100%;">
																			<select name="proCatAction_<%=proId %>_<%=taskId %>_<%=ii %>_<%=j %>_<%=k %>" id="proCatAction_<%=proId %>_<%=taskId %>_<%=ii %>_<%=j %>_<%=k %>" style="width: 80px !important;" onchange="executeFolderActions('<%=taskId %>', this.value, '<%=hmInnerSubDoc.get("CLIENT_ID")%>','<%=hmInnerSubDoc.get("PRO_ID")%>','<%=hmInnerSubDoc.get("DOCUMENT_NAME") %>','<%=hmInnerSubDoc.get("PRO_DOCUMENT_ID") %>', 'SFD', '<%=filePath1 %>', '<%=fileDir %>', 'folderTR_<%=proId %>_<%=taskId %>_<%=ii %>_<%=j %>_<%=k %>', '<%=fileSavePath %>', '<%=pageType %>');">
														                    	<option value="">Actions</option>
														                    	<% if(uF.parseToBoolean(hmInnerSubDoc.get("EDIT_STATUS_VAL"))) { %>
														                    		<option value="1">Edit</option>
														                    	<% } %>
														                    	<% if(uF.parseToBoolean(hmInnerSubDoc.get("DELETE_STATUS_VAL"))) { %>
													                    			<option value="2">Delete</option>
													                    		<% } %>
														                    </select>
																		</div>
																		<% } %>
																</div>
															</div>
															<div id="proDocsVersionDiv_<%=proId %>_<%=taskId %>_<%=ii %>_<%=j %>_<%=k %>" style="float: left; width: 100%; margin-top: 10px; display: none;"></div>
													<%} %>
												</div>
											<%} %>
											<%
											for(int j = 0; j < alFolderDoc.size();j++){
												Map<String, String> hmInnerDoc = (Map<String, String>) alFolderDoc.get(j);
												String filePath1 = proDocRetrivePath+strOrgId+"/Projects/"+hmInnerDoc.get("PRO_ID")+"/"+hmInnerFolder.get("FOLDER_NAME")+"/"+hmInnerDoc.get("DOCUMENT_NAME");
												String fileDir = proDocRetrivePath+strOrgId+"/Projects/"+hmInnerDoc.get("PRO_ID")+"/"+hmInnerFolder.get("FOLDER_NAME");
												
												String fileSavePath = proDocMainPath+strOrgId+"/Projects/"+hmInnerDoc.get("PRO_ID")+"/"+hmInnerFolder.get("FOLDER_NAME")+"/"+hmInnerDoc.get("DOCUMENT_NAME");
												
												String fileIcon = request.getContextPath()+"/images1/icons/icons/file_icon.png";
												if(hmFileIcon.containsKey(hmInnerDoc.get("FILE_EXTENSION"))) {
													fileIcon = request.getContextPath()+"/images1/file_icon/"+hmFileIcon.get(hmInnerDoc.get("FILE_EXTENSION"));
												}
												String action = "ProjectDocumentFact.action?clientId="+hmInnerDoc.get("CLIENT_ID")+"&proId="+hmInnerDoc.get("PRO_ID")+"&folderName="+hmInnerDoc.get("DOCUMENT_NAME")+"&proFolderId="+hmInnerDoc.get("PRO_DOCUMENT_ID")+"&type=3&filePath="+URLEncoder.encode(filePath1)+"&fileDir="+URLEncoder.encode(fileDir);
												int nDocVersion = uF.parseToInt(hmInnerDoc.get("DOC_VERSION")) > 0 ? uF.parseToInt(hmInnerDoc.get("DOC_VERSION")) : 1;
											%>
												<div id="docFolderTR_<%=proId %>_<%=taskId %>_<%=ii %>_<%=j %>" style="float: left; width: 100%; margin-top: 10px; display: none;"> 
													<div style="float: left; margin-left: 23px; width: 317px; margin-right: 9px;">
														<%-- <a href="javascript:void(0)" style="font-weight: normal; color: black;" onclick="projectDocFact('<%=hmInnerDoc.get("CLIENT_ID")%>','<%=hmInnerDoc.get("PRO_ID")%>','<%=hmInnerDoc.get("DOCUMENT_NAME") %>','<%=hmInnerDoc.get("PRO_DOCUMENT_ID") %>',3,'<%=filePath1 %>','<%=fileDir %>');" title="View Document"> --%>
														<a target="_blank" href="<%=action %>" style="font-weight: normal; color: black;" title="View Document"> 
															<img height="18" width="18" src="<%=fileIcon %>" />&nbsp;<%=hmInnerDoc.get("DOCUMENT_NAME") %>
														</a> 
														<div style="float: left; color: gray; width: 90%; margin-top: -5px; margin-left: 25px;"><%=hmInnerDoc.get("FILE_SIZE") %></div>
														<div style="float: left; color: gray; width: 90%; margin-top: -5px; margin-left: 25px;"><%=hmInnerDoc.get("DESCRIPTION") %></div>
														<div style="float: left; color: gray; width: 90%; margin-top: -5px; margin-left: 25px;"><%=hmInnerDoc.get("ENTRY_DATE") %></div>
														<div style="float: left; color: gray; width: 90%; margin-top: -5px; margin-left: 25px;">
															<span style="float: left;">
																<%-- <a href="javascript:void(0)" style="font-weight: normal;" onclick="projectDocFact('<%=hmInnerDoc.get("CLIENT_ID")%>','<%=hmInnerDoc.get("PRO_ID")%>','<%=hmInnerDoc.get("DOCUMENT_NAME") %>','<%=hmInnerDoc.get("PRO_DOCUMENT_ID") %>',3,'<%=filePath1 %>','<%=fileDir %>');" title="View Document">View File</a> --%>
																<a target="_blank" href="<%=action %>" style="font-weight: normal;" title="View Document">View File</a>
															|</span>
															<span style="float: left; margin-left: 2px;"><a href="<%=filePath1 %>" style="font-weight: normal;">Download</a></span>
														</div>
														
													</div>
													<div style="float: left; width: 120px; margin-right: 9px;">
														<div style="float: left; width: 100%;">Version <strong><%=nDocVersion %></strong> </div>
														<div style="float: left; width: 100%; margin-top: -5px;">
														<%if(nDocVersion > 1){ %>
															<span style="float: left;">Version history </span>
															<input type="hidden" name="proDocsSpanStatus" id="proDocsSpanStatus_<%=proId %>_<%=taskId %>_<%=ii %>_<%=j %>" value = "0"/> 
															<a href="javascript:void(0);" style="font-weight: normal; color: black;" onclick="viewVersionHistory(<%=hmInnerDoc.get("PRO_DOCUMENT_ID") %>,'proDocsSpanStatus_<%=proId %>_<%=taskId %>_<%=ii %>_<%=j %>','proDocsVersionDiv_<%=proId %>_<%=taskId %>_<%=ii %>_<%=j %>','VHDownarrowSpan_<%=proId %>_<%=taskId %>_<%=ii %>_<%=j %>', 'VHUparrowSpan_<%=proId %>_<%=taskId %>_<%=ii %>_<%=j %>','3','<%=filePath1%>','<%=fileDir %>')">
																<span id="VHDownarrowSpan_<%=proId %>_<%=taskId %>_<%=ii %>_<%=j %>" style="float: left; margin-left: 2px; margin-top: 3px;"> 
																	<i class="fa fa-chevron-down" style="padding: 0px;"></i> 
																</span>
																<span id="VHUparrowSpan_<%=proId %>_<%=taskId %>_<%=ii %>_<%=j %>" style="float: left; margin-left: 2px; margin-top: 3px; display: none;">
																	<i class="fa fa-chevron-up" style="padding: 0px;"></i>
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
														<% if(uF.parseToBoolean(hmInnerDoc.get("EDIT_STATUS_VAL")) || uF.parseToBoolean(hmInnerDoc.get("DELETE_STATUS_VAL"))) { %>
														<div style="float: left; width: 100%;">Actions</div>
														<div style="float: left; width: 100%;">
															<select name="proCatFDocAction_<%=proId %>_<%=taskId %>_<%=ii %>_<%=j %>" id="proCatFDocAction_<%=proId %>_<%=taskId %>_<%=ii %>_<%=j %>" style="width: 80px !important;" onchange="executeFolderActions('<%=taskId %>', this.value, '<%=hmInnerDoc.get("CLIENT_ID")%>','<%=hmInnerDoc.get("PRO_ID")%>','<%=hmInnerDoc.get("DOCUMENT_NAME") %>','<%=hmInnerDoc.get("PRO_DOCUMENT_ID") %>', 'FD', '<%=filePath1 %>', '<%=fileDir %>', 'docFolderTR_<%=proId %>_<%=taskId %>_<%=ii %>_<%=j %>', '<%=fileSavePath %>', '<%=pageType %>');">
										                    	<option value="">Actions</option>
										                    	<% if(uF.parseToBoolean(hmInnerDoc.get("EDIT_STATUS_VAL"))) { %>
										                    		<option value="1">Edit</option>
										                    	<% } %>
										                    	<% if(uF.parseToBoolean(hmInnerDoc.get("DELETE_STATUS_VAL"))) { %>
									                    			<option value="2">Delete</option>
									                    		<% } %>
										                    </select>
														</div>
														<% } %>
													</div>
													
												</div>
												<div id="proDocsVersionDiv_<%=proId %>_<%=taskId %>_<%=ii %>_<%=j %>" style="float: left; width: 100%; margin-top: 10px; display: none;"></div>
											<%} %>
											
										</div>
									<%} %>
									
								<% 	
								/* Iterator<String> it1 = hmDoc.keySet().iterator();
								while(it1.hasNext()) {
									String proFolderId = it1.next();
									List<Map<String, String>> alDoc = (List<Map<String, String>>) hmDoc.get(proFolderId); 
									if(alDoc == null) alDoc = new ArrayList<Map<String,String>>(); */
									
									for(int i = 0; alMainDoc!= null && i<alMainDoc.size(); i++) {
										Map<String, String> hmInnerDoc = (Map<String, String>) alMainDoc.get(i);
										String filePath1 = proDocRetrivePath+strOrgId+"/Projects/"+hmInnerDoc.get("PRO_ID")+"/"+hmInnerDoc.get("DOCUMENT_NAME");
										String fileDir = proDocRetrivePath+strOrgId+"/Projects/"+hmInnerDoc.get("PRO_ID");
										
										String fileSavePath = proDocMainPath+strOrgId+"/Projects/"+hmInnerDoc.get("PRO_ID")+"/"+hmInnerDoc.get("DOCUMENT_NAME");
										
										String fileIcon = request.getContextPath()+"/images1/icons/icons/file_icon.png";
										if(hmFileIcon.containsKey(hmInnerDoc.get("FILE_EXTENSION"))){
											fileIcon = request.getContextPath()+"/images1/file_icon/"+hmFileIcon.get(hmInnerDoc.get("FILE_EXTENSION"));
										}
										String action = "ProjectDocumentFact.action?clientId="+hmInnerDoc.get("CLIENT_ID")+"&proId="+hmInnerDoc.get("PRO_ID")+"&folderName="+hmInnerDoc.get("DOCUMENT_NAME")+"&proFolderId="+hmInnerDoc.get("PRO_DOCUMENT_ID")+"&type=3&filePath="+URLEncoder.encode(filePath1)+"&fileDir="+URLEncoder.encode(fileDir);
										int nDocVersion = uF.parseToInt(hmInnerDoc.get("DOC_VERSION")) > 0 ? uF.parseToInt(hmInnerDoc.get("DOC_VERSION")) : 1;
								%>
										<div id="docTR_<%=proId %>_<%=taskId %>_<%=i %>" style="float: left; margin-top: 10px;">
											<div style="float: left; width: 240px; margin-right: 9px;">
												<%-- <a href="javascript:void(0)" style="font-weight: normal; color: black;" onclick="projectDocFact('<%=hmInnerDoc.get("CLIENT_ID")%>','<%=hmInnerDoc.get("PRO_ID")%>','<%=hmInnerDoc.get("DOCUMENT_NAME") %>','<%=hmInnerDoc.get("PRO_DOCUMENT_ID") %>',3,'<%=filePath1 %>','<%=fileDir %>');" title="View Document"> --%>
												<a target="_blank" href="<%=action %>" style="font-weight: normal; color: black;" title="View Document"> 
													<img height="18" width="18" src="<%=fileIcon %>" />&nbsp;<%=hmInnerDoc.get("DOCUMENT_NAME") %>
												</a> 
												<div style="float: left; color: gray; width: 90%; margin-top: -5px; margin-left: 25px;"><%=hmInnerDoc.get("FILE_SIZE") %></div>
												<div style="float: left; color: gray; width: 90%; margin-top: -5px; margin-left: 25px;"><%=hmInnerDoc.get("DESCRIPTION") %></div>
												<div style="float: left; color: gray; width: 90%; margin-top: -5px; margin-left: 25px;"><%=hmInnerDoc.get("ENTRY_DATE") %></div>
												<div style="float: left; color: gray; width: 90%; margin-top: -5px; margin-left: 25px;">
													<span style="float: left;">
														<%-- <a href="javascript:void(0)" style="font-weight: normal;" onclick="projectDocFact('<%=hmInnerDoc.get("CLIENT_ID")%>','<%=hmInnerDoc.get("PRO_ID")%>','<%=hmInnerDoc.get("DOCUMENT_NAME") %>','<%=hmInnerDoc.get("PRO_DOCUMENT_ID") %>',3,'<%=filePath1 %>','<%=fileDir %>');" title="View Document">View File</a> --%>
														<a target="_blank" href="<%=action %>" style="font-weight: normal;" title="View Document">View File</a>
													|</span>
													<span style="float: left; margin-left: 2px;"><a href="<%=filePath1 %>" style="font-weight: normal;">Download</a></span>
												</div>
												
											</div>
											<div style="float: left; width: 120px; margin-right: 9px;">
												<div style="float: left; width: 100%;">Version <strong><%=nDocVersion %></strong> </div>
												<div style="float: left; width: 100%; margin-top: -5px;">
												<%if(nDocVersion > 1){ %>
													<span style="float: left;">Version history </span>
													<input type="hidden" name="proDocsSpanStatus" id="proDocsSpanStatus_<%=proId %>_<%=taskId %>_<%=i %>" value = "0"/> 
													<a href="javascript:void(0);" style="font-weight: normal; color: black;" onclick="viewVersionHistory(<%=hmInnerDoc.get("PRO_DOCUMENT_ID") %>,'proDocsSpanStatus_<%=proId %>_<%=taskId %>_<%=i %>','proDocsVersionDiv_<%=proId %>_<%=taskId %>_<%=i %>','VHDownarrowSpan_<%=proId %>_<%=taskId %>_<%=i %>', 'VHUparrowSpan_<%=proId %>_<%=taskId %>_<%=i %>','3','<%=filePath1%>','<%=fileDir %>')">
														<span id="VHDownarrowSpan_<%=proId %>_<%=taskId %>_<%=i %>" style="float: left; margin-left: 2px; margin-top: 3px;"> 
															<i class="fa fa-chevron-down" style="padding: 0px;"></i> 
														</span>
														<span id="VHUparrowSpan_<%=proId %>_<%=taskId %>_<%=i %>" style="float: left; margin-left: 2px; margin-top: 3px; display: none;">
															<i class="fa fa-chevron-up" style="padding: 0px;"></i>
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
												<% if(uF.parseToBoolean(hmInnerDoc.get("EDIT_STATUS_VAL")) || uF.parseToBoolean(hmInnerDoc.get("DELETE_STATUS_VAL"))) { %>
												<div style="float: left; width: 100%;">Actions</div>
												<div style="float: left; width: 100%;">
													<select name="proCatDocAction_<%=proId %>_<%=taskId %>_<%=i %>" id="proCatDocAction_<%=proId %>_<%=taskId %>_<%=i %>" style="width: 80px !important;" onchange="executeFolderActions('<%=taskId %>', this.value, '<%=hmInnerDoc.get("CLIENT_ID")%>','<%=hmInnerDoc.get("PRO_ID")%>','<%=hmInnerDoc.get("DOCUMENT_NAME") %>','<%=hmInnerDoc.get("PRO_DOCUMENT_ID") %>', 'D', '<%=filePath1 %>', '<%=fileDir %>', 'docTR_<%=proId %>_<%=taskId %>_<%=i %>', '<%=fileSavePath %>', '<%=pageType %>');">
								                    	<option value="">Actions</option>
								                    	<% if(uF.parseToBoolean(hmInnerDoc.get("EDIT_STATUS_VAL"))) { %>
								                    		<option value="1">Edit</option>
								                    	<% } %>
								                    	<% if(uF.parseToBoolean(hmInnerDoc.get("DELETE_STATUS_VAL"))) { %>
							                    			<option value="2">Delete</option>
							                    		<% } %>
								                    </select>
												</div>
												<% } %>
											</div>
											
										</div>
										<div id="proDocsVersionDiv_<%=proId %>_<%=taskId %>_<%=i %>" style="float: left; width: 100%; margin-top: 10px; display: none;"></div>
									<% } %>
								<% 
								//} %>	
						<%} else {%>
							<div style="float: left; width: 1050px;" class="nodata msg">
								<span>No documents attached.</span>
							</div>
						<%} %>
					</div>
						
			</td>
		</tr>

	</table>
</div>
		
	</form>


<% } else { %>
	<form id="formProjectDocuments<%=proId %>" class="formcss" action="ProjectDocuments.action" name="frmProjectDocuments<%=proId %>" method="post" enctype="multipart/form-data" onsubmit="showLoading();">
		<div class="box-body table-responsive no-padding">
		<!-- <div id="tblDiv" style="float: left; width: 100%;"> -->
			<s:hidden name="proId"></s:hidden>
			<s:hidden name="pageType"></s:hidden>
			<!-- <h3 style="margin-bottom: -10px; margin-left: 27px;">Documents</h3> -->
			<table class="table table-hover">
				<tr>
					<td class="txtlabel alignRight" valign="top">Documents:</td>
					<td>
					<% if(proType ==null || proType.equals("null") || proType.equals("") || proType.equals("L")) { %>
						<input type="hidden" name="folderDocscount<%=proId %>" id="folderDocscount<%=proId %>" value="0" />
						<input type="hidden" name="projectTasks<%=proId %>" id="projectTasks<%=proId %>" value="<%=(String)request.getAttribute("sbProTasks") %>" />
						<input type="hidden" name="resourceIds<%=proId %>" id="resourceIds<%=proId %>" value="<%=(String)request.getAttribute("sbProEmp") %>" />
						<input type="hidden" name="projectCategoryType<%=proId %>" id="projectCategoryType<%=proId %>" value="<%=(String)request.getAttribute("sbProCategory") %>" />
						<input type="hidden" name="projectPoc<%=proId %>" id="projectPoc<%=proId %>" value="<%=(String)request.getAttribute("sbProSPOC") %>" />
						<a class="fa fa-fw fa-plus-circle" href="javascript:void(0);" style="font-size: 13px; width: auto;" onclick="addNewFolder('<%=proId %>', 'documentTable<%=proId %>', 'folderDocscount<%=proId %>'), showTblHeader('<%=proId %>');">Add Folder</a> 
						<a class="fa fa-fw fa-plus-circle" href="javascript:void(0);" style="font-size: 13px; width: auto; margin-left: 7px;" onclick="addNewDocs('<%=proId %>', 'documentTable<%=proId %>', 'folderDocscount<%=proId %>'), showTblHeader('<%=proId %>');">Add Document</a>
					<% } %>	
					</td>
				</tr>
				<tr>
					<td colspan="2">
						<table class="table table-hover" id="documentTable<%=proId %>" style="width: 100%; margin-bottom: 0px;">
							<tr id="folderTR<%=proId %>0" style="display: none;">
								<td style="width: 50%;">Folder/Documents</td>
								<!-- <td style="width: 20%;">Scope Document</td> -->
								<td style="width: 12%;">Category</td>
								<td style="width: 12%;">Alignment</td>
								<td style="min-width: 15%;">Sharing</td>
								<td >Rights</td>
							</tr>
						</table>
					
					<div class="clr"></div>
					<% if(proType ==null || proType.equals("null") || proType.equals("") || proType.equals("L")) { %>
						<div id="buttonDiv<%=proId %>" style="display: none; float: left; width: 100%; text-align: center; padding: 5px 0px; border-top: 1px solid #CCCCCC;">
							<input type="submit" value="Save" class="btn btn-primary" style="margin-right: 5px; padding: 3px;" name="btnSave"/>
							<input type="button" value="Cancel" class="btn btn-danger" style="margin-right: 5px; padding: 3px;" name="cancel" onclick="viewDocuments('<%=proId %>', '<%=proType %>');">
						</div>
					<% } %>
					</td>
				</tr>
				<tr>
					<td colspan="2">
						
						
						<div style="float: left;">  <!-- border: 1px solid gray; -->
							<% 	
								//Map<String, List<Map<String,String>>> hmFolder = (Map<String, List<Map<String,String>>>)request.getAttribute("hmFolder");
								List<Map<String, String>> alProFolder = (List<Map<String, String>>) request.getAttribute("alProFolder");
								//Map<String, List<Map<String,String>>> hmDoc = (Map<String, List<Map<String,String>>>)request.getAttribute("hmDoc");
								List<Map<String, String>> alMainDoc = (List<Map<String, String>>) request.getAttribute("alMainDoc");
								
								Map<String, List<Map<String,String>>> hmSubFolder = (Map<String, List<Map<String,String>>>)request.getAttribute("hmSubFolder");
								Map<String, List<Map<String,String>>> hmSubDoc = (Map<String, List<Map<String,String>>>)request.getAttribute("hmSubDoc");
								
								String proDocMainPath = (String) request.getAttribute("proDocMainPath");
								String proDocRetrivePath = (String) request.getAttribute("proDocRetrivePath");
								String strOrgId = (String) request.getAttribute("strOrgId");
								if((alProFolder != null && !alProFolder.isEmpty()) || (alMainDoc !=null && !alMainDoc.isEmpty())) {
							%>
								<% 	
										//System.out.println("alProFolder ===>> " + alProFolder.size());
										for(int ii=0; alProFolder!=null && ii<alProFolder.size(); ii++) {
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
									%>
											<div id="folderTR_<%=proId %>_<%=ii %>" style="float: left;">
												<div style="float: left; width: 340px; margin-right: 9px;">
													<input type="hidden" name="hideFolder_<%=proId %>_<%=ii %>" id="hideFolder_<%=proId %>_<%=ii %>" value="0" /> 
													<div style="float: left; margin-right: 5px;"> <a href="javascript:void(0);" style="font-weight: normal; color: black;" onclick="openCloseDocs('<%=proId %>', '<%=ii %>', '<%=alSubFolder.size()%>', '<%=alFolderDoc.size()%>');"> 
													<%-- <img height="15" width="20" src="<%=request.getContextPath()%>/images1/icons/icons/folder_icon.png" /> --%> 
													
													<i class="fa fa-folder-open-o" style="font-size: 20px;height: 15px;"></i></a> </div>
													<strong><%=hmInnerFolder.get("FOLDER_NAME") %></strong>
													<div style="float: left; color: gray; width: 90%; margin-top: -5px; margin-left: 25px;"><%=fileCount +" items" %></div>
													<div style="float: left; color: gray; width: 90%; margin-top: -5px; margin-left: 25px;"><%=hmInnerFolder.get("DESCRIPTION") %></div>
													<div style="float: left; color: gray; width: 90%; margin-top: -5px; margin-left: 25px;"><%=hmInnerFolder.get("ENTRY_DATE") %></div>
													<% if(uF.parseToInt(fileCount)>0) { %>
													<div style="float: left; color: gray; width: 90%; margin-top: -5px; margin-left: 25px;">
													<span style="float: left;"><%=fileCount +" items" %> view folders & files </span> 
													<a href="javascript:void(0);" style="font-weight: normal; color: black;" onclick="openCloseDocs('<%=proId %>', '<%=ii %>', '<%=alSubFolder.size()%>', '<%=alFolderDoc.size()%>');">
														<span id="FDDownarrowSpan_<%=proId %>_<%=ii %>" style="float: left; margin-left: 2px; margin-top: 3px;"> 
															<i class="fa fa-chevron-down" style="padding: 0px;"></i> 
														</span>
														<span id="FDUparrowSpan_<%=proId %>_<%=ii %>" style="float: left; margin-left: 2px; margin-top: 3px; display: none;">
															<i class="fa fa-chevron-up" style="padding: 0px;"></i>
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
														<select name="proCatAction_<%=proId %>_<%=ii %>" id="proCatAction_<%=proId %>_<%=ii %>" style="width: 80px !important;" onchange="executeFolderActions(this.value, '<%=hmInnerFolder.get("CLIENT_ID")%>','<%=hmInnerFolder.get("PRO_ID")%>','<%=hmInnerFolder.get("FOLDER_NAME") %>','<%=hmInnerFolder.get("PRO_DOCUMENT_ID") %>', 'F', '', '', 'folderTR_<%=proId %>_<%=ii %>', '<%=folderSavePath %>', '<%=pageType %>');">
									                    	<option value="">Actions</option>
									                    	<option value="1">Edit</option>
								                    		<option value="2">Delete</option>
									                    </select>
													</div>
												</div>
												
												<%
												for(int j = 0; j < alSubFolder.size(); j++){
													Map<String, String> hmInnerSubFolder = (Map<String, String>) alSubFolder.get(j);
													List<Map<String, String>> alDoc = (List<Map<String, String>>) hmSubDoc.get(hmInnerSubFolder.get("PRO_DOCUMENT_ID")); 
													if(alDoc == null) alDoc = new ArrayList<Map<String,String>>();
													
													String subFolderSavePath = proDocMainPath+strOrgId+"/Projects/"+hmInnerSubFolder.get("PRO_ID")+"/"+hmInnerFolder.get("FOLDER_NAME")+"/"+hmInnerSubFolder.get("FOLDER_NAME");
													String fileSubCount ="-";
													if(alDoc !=null && alDoc.size() > 0) {
														fileSubCount = alDoc.size()+"";
													}
												%>
													<div id="folderTR_<%=proId %>_<%=ii %>_<%=j %>"  style="float: left; width: 100%; margin-top: 10px; display: none;">
														
														<div style="float: left; margin-left: 23px; width: 317px; margin-right: 9px;">
															<input type="hidden" name="hideFolder_<%=proId %>_<%=ii %>_<%=j %>" id="hideFolder_<%=proId %>_<%=ii %>_<%=j %>" value="0" /> 
															<div style="float: left; margin-right: 5px;"> <a href="javascript:void(0);" style="font-weight: normal; color: black;" onclick="openCloseDocs1('<%=proId %>', '<%=ii %>', '<%=j %>', '<%=alDoc.size() %>');"> 
															<%-- <img height="15" width="20" src="<%=request.getContextPath()%>/images1/icons/icons/folder_icon.png" /> --%>
															<i class="fa fa-folder-open-o" style="font-size: 20px;height: 15px;"></i>
															 </a> </div>
															
															<strong><%=hmInnerSubFolder.get("FOLDER_NAME") %></strong>
															<div style="float: left; color: gray; width: 90%; margin-top: -5px; margin-left: 25px;"><%=fileSubCount +" items" %></div>
															<div style="float: left; color: gray; width: 90%; margin-top: -5px; margin-left: 25px;"><%=hmInnerSubFolder.get("DESCRIPTION") %></div>
															<div style="float: left; color: gray; width: 90%; margin-top: -5px; margin-left: 25px;"><%=hmInnerSubFolder.get("ENTRY_DATE") %></div>
															<% if(uF.parseToInt(fileSubCount)>0) { %>
															<div style="float: left; color: gray; width: 90%; margin-top: -5px; margin-left: 25px;">
															<span style="float: left;"><%=fileSubCount +" items" %> view files </span> 
															<a href="javascript:void(0);" style="font-weight: normal; color: black;" onclick="openCloseDocs1('<%=proId %>', '<%=ii %>', '<%=j %>', '<%=alDoc.size() %>');">
																<span id="FDDownarrowSpan_<%=proId %>_<%=ii %>_<%=j %>" style="float: left; margin-left: 2px; margin-top: 3px;"> 
																	<i class="fa fa-chevron-down" style="padding: 0px;"></i> 
																</span>
																<span id="FDUparrowSpan_<%=proId %>_<%=ii %>_<%=j %>" style="float: left; margin-left: 2px; margin-top: 3px; display: none;">
																	<i class="fa fa-chevron-up" style="padding: 0px;"></i>
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
																<select name="proCatAction_<%=proId %>_<%=ii %>_<%=j %>" id="proCatAction_<%=proId %>_<%=ii %>_<%=j %>" style="width: 80px !important;" onchange="executeFolderActions(this.value, '<%=hmInnerSubFolder.get("CLIENT_ID")%>','<%=hmInnerSubFolder.get("PRO_ID")%>','<%=hmInnerSubFolder.get("FOLDER_NAME") %>','<%=hmInnerSubFolder.get("PRO_DOCUMENT_ID") %>', 'SF', '', '', 'folderTR_<%=proId %>_<%=ii %>_<%=j %>', '<%=subFolderSavePath %>', '<%=pageType %>');">
											                    	<option value="">Actions</option>
											                    	<option value="1">Edit</option>
										                    		<option value="2">Delete</option>
											                    </select>
															</div>
														</div>
														
														<%
															for(int k = 0; k<alDoc.size(); k++){
																Map<String, String> hmInnerSubDoc = (Map<String, String>) alDoc.get(k);
																String filePath1 = proDocRetrivePath+strOrgId+"/Projects/"+hmInnerSubDoc.get("PRO_ID")+"/"+hmInnerFolder.get("FOLDER_NAME")+"/"+hmInnerSubFolder.get("FOLDER_NAME")+"/"+hmInnerSubDoc.get("DOCUMENT_NAME");
																String fileDir = proDocRetrivePath+strOrgId+"/Projects/"+hmInnerSubDoc.get("PRO_ID")+"/"+hmInnerFolder.get("FOLDER_NAME")+"/"+hmInnerSubFolder.get("FOLDER_NAME");
																
																String fileSavePath = proDocMainPath+strOrgId+"/Projects/"+hmInnerSubDoc.get("PRO_ID")+"/"+hmInnerFolder.get("FOLDER_NAME")+"/"+hmInnerSubFolder.get("FOLDER_NAME")+"/"+hmInnerSubDoc.get("DOCUMENT_NAME");;
																
																String fileIcon = request.getContextPath()+"/images1/icons/icons/file_icon.png";
																if(hmFileIcon.containsKey(hmInnerSubDoc.get("FILE_EXTENSION"))){
																	fileIcon = request.getContextPath()+"/images1/file_icon/"+hmFileIcon.get(hmInnerSubDoc.get("FILE_EXTENSION"));
																}
																String action = "ProjectDocumentFact.action?clientId="+hmInnerSubDoc.get("CLIENT_ID")+"&proId="+hmInnerSubDoc.get("PRO_ID")+"&folderName="+hmInnerSubDoc.get("DOCUMENT_NAME")+"&proFolderId="+hmInnerSubDoc.get("PRO_DOCUMENT_ID")+"&type=2&filePath="+URLEncoder.encode(filePath1)+"&fileDir="+URLEncoder.encode(fileDir);
																int nDocVersion = uF.parseToInt(hmInnerSubDoc.get("DOC_VERSION")) > 0 ? uF.parseToInt(hmInnerSubDoc.get("DOC_VERSION")) : 1;
														%>
																<div id="folderTR_<%=proId %>_<%=ii %>_<%=j %>_<%=k %>" style="float: left; width: 100%; margin-top: 10px; display: none;">
																	<div style="float: left; margin-left: 48px; width: 292px; margin-right: 9px;">
																		<%-- <a href="javascript:void(0)" style="font-weight: normal; color: black;" onclick="projectDocFact('<%=hmInnerSubDoc.get("CLIENT_ID")%>','<%=hmInnerSubDoc.get("PRO_ID")%>','<%=hmInnerSubDoc.get("DOCUMENT_NAME") %>','<%=hmInnerSubDoc.get("PRO_DOCUMENT_ID") %>',2,'<%=filePath1 %>','<%=fileDir %>');" title="View Document"> --%>
																		<a target="_blank" href="<%=action %>" style="font-weight: normal; color: black;" title="View Document"> 
																			<img height="18" width="18" src="<%=fileIcon %>" />&nbsp;<%=hmInnerSubDoc.get("DOCUMENT_NAME") %>
																		</a> 
																		<div style="float: left; color: gray; width: 90%; margin-top: -5px; margin-left: 25px;"><%=hmInnerSubDoc.get("FILE_SIZE") %></div>
																		<div style="float: left; color: gray; width: 90%; margin-top: -5px; margin-left: 25px;"><%=hmInnerSubDoc.get("DESCRIPTION") %></div>
																		<div style="float: left; color: gray; width: 90%; margin-top: -5px; margin-left: 25px;"><%=hmInnerSubDoc.get("ENTRY_DATE") %></div>
																		<div style="float: left; color: gray; width: 90%; margin-top: -5px; margin-left: 25px;">
																			<span style="float: left;">
																				<%-- <a href="javascript:void(0)" style="font-weight: normal;" onclick="projectDocFact('<%=hmInnerSubDoc.get("CLIENT_ID")%>','<%=hmInnerSubDoc.get("PRO_ID")%>','<%=hmInnerSubDoc.get("DOCUMENT_NAME") %>','<%=hmInnerSubDoc.get("PRO_DOCUMENT_ID") %>',2,'<%=filePath1 %>','<%=fileDir %>');" title="View Document">View File</a> --%>
																				<a target="_blank" href="<%=action %>" style="font-weight: normal;" title="View Document">View File</a>
																			|</span>
																			<span style="float: left; margin-left: 2px;"><a href="<%=filePath1 %>" style="font-weight: normal;">Download</a></span>
																		</div>
																		
																	</div>
																	<div style="float: left; width: 120px; margin-right: 9px;">
																		<div style="float: left; width: 100%;">Version <strong><%=nDocVersion %></strong> </div>
																		<div style="float: left; width: 100%; margin-top: -5px;">
																		<%if(nDocVersion > 1){ %>
																			<span style="float: left;">Version history </span>
																			<input type="hidden" name="proDocsSpanStatus" id="proDocsSpanStatus_<%=proId %>_<%=ii %>_<%=j %>_<%=k %>" value = "0"/> 
																			<a href="javascript:void(0);" style="font-weight: normal; color: black;" onclick="viewVersionHistory(<%=hmInnerSubDoc.get("PRO_DOCUMENT_ID") %>,'proDocsSpanStatus_<%=proId %>_<%=ii %>_<%=j %>_<%=k %>','proDocsVersionDiv_<%=proId %>_<%=ii %>_<%=j %>_<%=k %>','VHDownarrowSpan_<%=proId %>_<%=ii %>_<%=j %>_<%=k %>', 'VHUparrowSpan_<%=proId %>_<%=ii %>_<%=j %>_<%=k %>','2','<%=filePath1%>','<%=fileDir %>')">
																				<span id="VHDownarrowSpan_<%=proId %>_<%=ii %>_<%=j %>_<%=k %>" style="float: left; margin-left: 2px; margin-top: 3px;"> 
																					<i class="fa fa-chevron-down" style="padding: 0px;"></i> 
																				</span>
																				<span id="VHUparrowSpan_<%=proId %>_<%=ii %>_<%=j %>_<%=k %>" style="float: left; margin-left: 2px; margin-top: 3px; display: none;">
																					<i class="fa fa-chevron-up" style="padding: 0px;"></i>
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
																			<select name="proCatAction_<%=proId %>_<%=ii %>_<%=j %>_<%=k %>" id="proCatAction_<%=proId %>_<%=ii %>_<%=j %>_<%=k %>" style="width: 80px !important;" onchange="executeFolderActions(this.value, '<%=hmInnerSubDoc.get("CLIENT_ID")%>','<%=hmInnerSubDoc.get("PRO_ID")%>','<%=hmInnerSubDoc.get("DOCUMENT_NAME") %>','<%=hmInnerSubDoc.get("PRO_DOCUMENT_ID") %>', 'SFD', '<%=filePath1 %>', '<%=fileDir %>', 'folderTR_<%=proId %>_<%=ii %>_<%=j %>_<%=k %>', '<%=fileSavePath %>', '<%=pageType %>');">
														                    	<option value="">Actions</option>
														                    	<option value="1">Edit</option>
													                    		<option value="2">Delete</option>
														                    </select>
																		</div>
																	</div>
																</div>
																<div id="proDocsVersionDiv_<%=proId %>_<%=ii %>_<%=j %>_<%=k %>" style="float: left; width: 100%; margin-top: 10px; display: none;"></div>
														<%} %>
													</div>
												<%} %>
												<%
												for(int j = 0; j < alFolderDoc.size();j++){
													Map<String, String> hmInnerDoc = (Map<String, String>) alFolderDoc.get(j);
													String filePath1 = proDocRetrivePath+strOrgId+"/Projects/"+hmInnerDoc.get("PRO_ID")+"/"+hmInnerFolder.get("FOLDER_NAME")+"/"+hmInnerDoc.get("DOCUMENT_NAME");
													String fileDir = proDocRetrivePath+strOrgId+"/Projects/"+hmInnerDoc.get("PRO_ID")+"/"+hmInnerFolder.get("FOLDER_NAME");
													
													String fileSavePath = proDocMainPath+strOrgId+"/Projects/"+hmInnerDoc.get("PRO_ID")+"/"+hmInnerFolder.get("FOLDER_NAME")+"/"+hmInnerDoc.get("DOCUMENT_NAME");
													
													String fileIcon = request.getContextPath()+"/images1/icons/icons/file_icon.png";
													if(hmFileIcon.containsKey(hmInnerDoc.get("FILE_EXTENSION"))){
														fileIcon = request.getContextPath()+"/images1/file_icon/"+hmFileIcon.get(hmInnerDoc.get("FILE_EXTENSION"));
													}
													String action = "ProjectDocumentFact.action?clientId="+hmInnerDoc.get("CLIENT_ID")+"&proId="+hmInnerDoc.get("PRO_ID")+"&folderName="+hmInnerDoc.get("DOCUMENT_NAME")+"&proFolderId="+hmInnerDoc.get("PRO_DOCUMENT_ID")+"&type=3&filePath="+URLEncoder.encode(filePath1)+"&fileDir="+URLEncoder.encode(fileDir);
													int nDocVersion = uF.parseToInt(hmInnerDoc.get("DOC_VERSION")) > 0 ? uF.parseToInt(hmInnerDoc.get("DOC_VERSION")) : 1;
												%>
													<div id="docFolderTR_<%=proId %>_<%=ii %>_<%=j %>" style="float: left; width: 100%; margin-top: 10px; display: none;"> 
														<div style="float: left; margin-left: 23px; width: 317px; margin-right: 9px;">
															<%-- <a href="javascript:void(0)" style="font-weight: normal; color: black;" onclick="projectDocFact('<%=hmInnerDoc.get("CLIENT_ID")%>','<%=hmInnerDoc.get("PRO_ID")%>','<%=hmInnerDoc.get("DOCUMENT_NAME") %>','<%=hmInnerDoc.get("PRO_DOCUMENT_ID") %>',3,'<%=filePath1 %>','<%=fileDir %>');" title="View Document"> --%>
															<a target="_blank" href="<%=action %>" style="font-weight: normal; color: black;" title="View Document"> 
																<img height="18" width="18" src="<%=fileIcon %>" />&nbsp;<%=hmInnerDoc.get("DOCUMENT_NAME") %>
															</a> 
															<div style="float: left; color: gray; width: 90%; margin-top: -5px; margin-left: 25px;"><%=hmInnerDoc.get("FILE_SIZE") %></div>
															<div style="float: left; color: gray; width: 90%; margin-top: -5px; margin-left: 25px;"><%=hmInnerDoc.get("DESCRIPTION") %></div>
															<div style="float: left; color: gray; width: 90%; margin-top: -5px; margin-left: 25px;"><%=hmInnerDoc.get("ENTRY_DATE") %></div>
															<div style="float: left; color: gray; width: 90%; margin-top: -5px; margin-left: 25px;">
																<span style="float: left;">
																	<%-- <a href="javascript:void(0)" style="font-weight: normal;" onclick="projectDocFact('<%=hmInnerDoc.get("CLIENT_ID")%>','<%=hmInnerDoc.get("PRO_ID")%>','<%=hmInnerDoc.get("DOCUMENT_NAME") %>','<%=hmInnerDoc.get("PRO_DOCUMENT_ID") %>',3,'<%=filePath1 %>','<%=fileDir %>');" title="View Document">View File</a> --%>
																	<a target="_blank" href="<%=action %>" style="font-weight: normal;" title="View Document">View File</a>
																|</span>
																<span style="float: left; margin-left: 2px;"><a href="<%=filePath1 %>" style="font-weight: normal;">Download</a></span>
															</div>
															
														</div>
														<div style="float: left; width: 120px; margin-right: 9px;">
															<div style="float: left; width: 100%;">Version <strong><%=nDocVersion %></strong> </div>
															<div style="float: left; width: 100%; margin-top: -5px;">
															<%if(nDocVersion > 1){ %>
																<span style="float: left;">Version history </span>
																<input type="hidden" name="proDocsSpanStatus" id="proDocsSpanStatus_<%=proId %>_<%=ii %>_<%=j %>" value = "0"/> 
																<a href="javascript:void(0);" style="font-weight: normal; color: black;" onclick="viewVersionHistory(<%=hmInnerDoc.get("PRO_DOCUMENT_ID") %>,'proDocsSpanStatus_<%=proId %>_<%=ii %>_<%=j %>','proDocsVersionDiv_<%=proId %>_<%=ii %>_<%=j %>','VHDownarrowSpan_<%=proId %>_<%=ii %>_<%=j %>', 'VHUparrowSpan_<%=proId %>_<%=ii %>_<%=j %>','3','<%=filePath1%>','<%=fileDir %>')">
																	<span id="VHDownarrowSpan_<%=proId %>_<%=ii %>_<%=j %>" style="float: left; margin-left: 2px; margin-top: 3px;"> 
																		<i class="fa fa-chevron-down" style="padding: 0px;"></i> 
																	</span>
																	<span id="VHUparrowSpan_<%=proId %>_<%=ii %>_<%=j %>" style="float: left; margin-left: 2px; margin-top: 3px; display: none;">
																		<i class="fa fa-chevron-up" style="padding: 0px;"></i>
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
																<select name="proCatFDocAction_<%=proId %>_<%=ii %>_<%=j %>" id="proCatFDocAction_<%=proId %>_<%=ii %>_<%=j %>" style="width: 80px !important;" onchange="executeFolderActions(this.value, '<%=hmInnerDoc.get("CLIENT_ID")%>','<%=hmInnerDoc.get("PRO_ID")%>','<%=hmInnerDoc.get("DOCUMENT_NAME") %>','<%=hmInnerDoc.get("PRO_DOCUMENT_ID") %>', 'FD', '<%=filePath1 %>', '<%=fileDir %>', 'docFolderTR_<%=proId %>_<%=ii %>_<%=j %>', '<%=fileSavePath %>', '<%=pageType %>');">
											                    	<option value="">Actions</option>
											                    	<option value="1">Edit</option>
										                    		<option value="2">Delete</option>
											                    </select>
															</div>
														</div>
													</div>
													<div id="proDocsVersionDiv_<%=proId %>_<%=ii %>_<%=j %>" style="float: left; width: 100%; margin-top: 10px; display: none;"></div>
												<%} %>
												
											</div>
										<%} %>
										
									<% 	
									/* Iterator<String> it1 = hmDoc.keySet().iterator();
									int docCnt = 0;
									while(it1.hasNext()) {
										String proFolderId = it1.next();
										List<Map<String, String>> alDoc = (List<Map<String, String>>) hmDoc.get(proFolderId); 
										if(alDoc == null) alDoc = new ArrayList<Map<String,String>>(); */
										
										for(int i = 0; alMainDoc!=null && i<alMainDoc.size(); i++) {
											Map<String, String> hmInnerDoc = (Map<String, String>) alMainDoc.get(i);
											String filePath1 = proDocRetrivePath+strOrgId+"/Projects/"+hmInnerDoc.get("PRO_ID")+"/"+hmInnerDoc.get("DOCUMENT_NAME");
											String fileDir = proDocRetrivePath+strOrgId+"/Projects/"+hmInnerDoc.get("PRO_ID");
											
											String fileSavePath = proDocMainPath+strOrgId+"/Projects/"+hmInnerDoc.get("PRO_ID")+"/"+hmInnerDoc.get("DOCUMENT_NAME");
											
											String fileIcon = request.getContextPath()+"/images1/icons/icons/file_icon.png";
											if(hmFileIcon.containsKey(hmInnerDoc.get("FILE_EXTENSION"))){
												fileIcon = request.getContextPath()+"/images1/file_icon/"+hmFileIcon.get(hmInnerDoc.get("FILE_EXTENSION"));
											}
											String action = "ProjectDocumentFact.action?clientId="+hmInnerDoc.get("CLIENT_ID")+"&proId="+hmInnerDoc.get("PRO_ID")+"&folderName="+hmInnerDoc.get("DOCUMENT_NAME")+"&proFolderId="+hmInnerDoc.get("PRO_DOCUMENT_ID")+"&type=3&filePath="+URLEncoder.encode(filePath1)+"&fileDir="+URLEncoder.encode(fileDir);
											int nDocVersion = uF.parseToInt(hmInnerDoc.get("DOC_VERSION")) > 0 ? uF.parseToInt(hmInnerDoc.get("DOC_VERSION")) : 1;
									%>
											<div id="docTR_<%=proId %>_<%=i %>" style="float: left; margin-top: 10px;">
												<div style="float: left; width: 240px; margin-right: 9px;">
													<%-- <a href="javascript:void(0)" style="font-weight: normal; color: black;" onclick="projectDocFact('<%=hmInnerDoc.get("CLIENT_ID")%>','<%=hmInnerDoc.get("PRO_ID")%>','<%=hmInnerDoc.get("DOCUMENT_NAME") %>','<%=hmInnerDoc.get("PRO_DOCUMENT_ID") %>',3,'<%=filePath1 %>','<%=fileDir %>');" title="View Document"> --%>
													<a target="_blank" href="<%=action %>" style="font-weight: normal; color: black;" title="View Document"> 
														<img height="18" width="18" src="<%=fileIcon %>" />&nbsp;<%=hmInnerDoc.get("DOCUMENT_NAME") %>
													</a> 
													<div style="float: left; color: gray; width: 90%; margin-top: -5px; margin-left: 25px;"><%=hmInnerDoc.get("FILE_SIZE") %></div>
													<div style="float: left; color: gray; width: 90%; margin-top: -5px; margin-left: 25px;"><%=hmInnerDoc.get("DESCRIPTION") %></div>
													<div style="float: left; color: gray; width: 90%; margin-top: -5px; margin-left: 25px;"><%=hmInnerDoc.get("ENTRY_DATE") %></div>
													<div style="float: left; color: gray; width: 90%; margin-top: -5px; margin-left: 25px;">
														<span style="float: left;">
															<%-- <a href="javascript:void(0)" style="font-weight: normal;" onclick="projectDocFact('<%=hmInnerDoc.get("CLIENT_ID")%>','<%=hmInnerDoc.get("PRO_ID")%>','<%=hmInnerDoc.get("DOCUMENT_NAME") %>','<%=hmInnerDoc.get("PRO_DOCUMENT_ID") %>',3,'<%=filePath1 %>','<%=fileDir %>');" title="View Document">View File</a> --%>
															<a target="_blank" href="<%=action %>" style="font-weight: normal;" title="View Document">View File</a>
														|</span>
														<span style="float: left; margin-left: 2px;"><a href="<%=filePath1 %>" style="font-weight: normal;">Download</a></span>
													</div>
													
												</div>
												<div style="float: left; width: 120px; margin-right: 9px;">
													<div style="float: left; width: 100%;">Version <strong><%=nDocVersion %></strong> </div>
													<div style="float: left; width: 100%; margin-top: -5px;">
													<%if(nDocVersion > 1){ %>
														<span style="float: left;">Version history </span>
														<input type="hidden" name="proDocsSpanStatus" id="proDocsSpanStatus_<%=proId %>_<%=i %>" value = "0"/> 
														<a href="javascript:void(0);" style="font-weight: normal; color: black;" onclick="viewVersionHistory(<%=hmInnerDoc.get("PRO_DOCUMENT_ID") %>,'proDocsSpanStatus_<%=proId %>_<%=i %>','proDocsVersionDiv_<%=proId %>_<%=i %>','VHDownarrowSpan_<%=proId %>_<%=i %>', 'VHUparrowSpan_<%=proId %>_<%=i %>','3','<%=filePath1%>','<%=fileDir %>')">
															<span id="VHDownarrowSpan_<%=proId %>_<%=i %>" style="float: left; margin-left: 2px; margin-top: 3px;"> 
																<i class="fa fa-chevron-down" style="padding: 0px;"></i> 
															</span>
															<span id="VHUparrowSpan_<%=proId %>_<%=i %>" style="float: left; margin-left: 2px; margin-top: 3px; display: none;">
																<i class="fa fa-chevron-up" style="padding: 0px;"></i>
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
														<select name="proCatDocAction_<%=proId %>_<%=i %>" id="proCatDocAction_<%=proId %>_<%=i %>" style="width: 80px !important;" onchange="executeFolderActions(this.value, '<%=hmInnerDoc.get("CLIENT_ID")%>','<%=hmInnerDoc.get("PRO_ID")%>','<%=hmInnerDoc.get("DOCUMENT_NAME") %>','<%=hmInnerDoc.get("PRO_DOCUMENT_ID") %>', 'D', '<%=filePath1 %>', '<%=fileDir %>', 'docTR_<%=proId %>_<%=i %>', '<%=fileSavePath %>', '<%=pageType %>');">
									                    	<option value="">Actions</option>
									                    	<option value="1">Edit</option>
								                    		<option value="2">Delete</option>
									                    </select>
													</div>
												</div>
											</div>
											<div id="proDocsVersionDiv_<%=proId %>_<%=i %>" style="float: left; width: 100%; margin-top: 10px; display: none;"></div>
										<% } %>
							<% } else { %>
								<div class="alert" style="background-color: #FCF8E3 !important; border-color: #FAEBCC; color: #8A6D3B !important; padding: 10px; margin-bottom: 0px;">No document attached.</div>
							<% } %>
						</div>
						
						
					</td>
				</tr>
			</table>
		</div>
		
	</form>
<% } %>

