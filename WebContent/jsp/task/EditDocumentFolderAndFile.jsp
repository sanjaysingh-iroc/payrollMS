<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="java.net.URLEncoder"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<%@ page import="java.util.*,com.konnect.jpms.util.UtilityFunctions,com.konnect.jpms.task.*"%>
<%@ taglib uri="http://granule.com/tags" prefix="g" %>

<style>
.formcss select,.formcss button {
	width: 125px !important;
}
</style>

<script>
	
	$(function() {
		$("#strOrgPocFolderDoc").multiselect().multiselectfilter();
		$("#strOrgResources1").multiselect().multiselectfilter();
	});
	
	function showEditPoc(id) {
		var val = document.getElementById("showPocType").value;
		if(val == '1') {
			document.getElementById(id).style.display = 'block';
			document.getElementById("showPocType").value = '0';
		} else {
			document.getElementById(id).style.display = 'none';
			document.getElementById("showPocType").value = '1';
		}
	}
</script>
<%
	UtilityFunctions uF = new UtilityFunctions();
	String strUserType = (String) request.getAttribute("strUserType");

	Map<String, String> hmProDocumentDetails = (Map<String, String>) request.getAttribute("hmProDocumentDetails");
	String type = (String)request.getAttribute("type");
	String filePath = (String) request.getAttribute("filePath");
	
	Map<String, String> hmFileIcon = (Map<String, String>) request.getAttribute("hmFileIcon");
	if(hmFileIcon == null) hmFileIcon = new HashMap<String, String>();
	
	String operation = (String) request.getAttribute("operation");
	String tableId = (String) request.getAttribute("tableId");
	String tableName = "folderdocTable";
	String divName = "divSubmitCancel";
	if(operation != null && operation.equalsIgnoreCase("ADD")) {
		tableName = "folderdocTable_"+tableId;
		divName = "divSubmitCancel_"+tableId;
	}
%>

	<div class="box box-body">
		<s:form id="frmEditDocumentFolderAndFile" cssClass="formcss" action="EditDocumentFolderAndFile" name="frmEditDocumentFolderAndFile" method="post" enctype="multipart/form-data" theme="simple">
			<div class="box-body table-responsive no-padding">
			<s:hidden name="strId"></s:hidden>
			<s:hidden name="folderName"></s:hidden>
			<s:hidden name="operation" value="U"></s:hidden>
			<s:hidden name="type"></s:hidden>
			<s:hidden name="fromPage"></s:hidden>

			<table class="table table-hover" id="<%=tableName %>">
				<tr id="folderTR_0">
					<td style="width: 50%; border-top: 0px;">
					<input type="hidden" name="docsCount" id="docsCount" value="1"/>
					Folder/Documents</td>
					<!-- <td style="width: 20%;">Scope Document</td> -->
					<td style="width: 12%; border-top: 0px;">Category</td>
					<td style="width: 12%; border-top: 0px;">Alignment</td>
					<td style="min-width: 19%; border-top: 0px;">Sharing</td>
					<td style="border-top: 0px;">Rights</td>
				</tr>
				<% if(operation == null || !operation.equalsIgnoreCase("ADD")) { %>
					<tr id="folderTR1">
						<%
						String strDisable = "";
						if(type != null && (type.equals("F") || type.equals("SF"))) {
							strDisable = "disabled='disabled'";
						%>
						<td valign="top"><%=hmProDocumentDetails.get("FOLDER_NAME") %><br/>
							<span style="float:left; width: 100%;"><textarea rows="3" name="strFolderDescription" id="strFolderDescription1" style="width: 330px !important;"><%=uF.showData(hmProDocumentDetails.get("DESCRIPTION"),"") %></textarea></span>
							<!-- <a href="javascript:void(0);" onclick="addFolderDocs('1', this.parentNode.parentNode.rowIndex);"> +Add Document</a> -->
							<%if(type != null && type.equals("F")) { %>
							<span style="float:left; width: 100%;"><a href="javascript:void(0);" style="margin: -15px 0px 15px 50px;" onclick="addNewSubFolder('1', this.parentNode.parentNode.parentNode.rowIndex, '<%=tableName %>', 'docsCount', '<%=tableId %>');"> +Add Folder</a> 
							<a href="javascript:void(0);" style="margin: -15px 0px 15px 15px;" onclick="addNewFolderDocs('1', this.parentNode.parentNode.parentNode.rowIndex, '<%=tableName %>', 'docsCount', '<%=tableId %>');"> +Add Document</a></span>
							<% } else if(type != null && type.equals("SF")) { %>
								<span style="float:left; width: 100%;">
									<a href="javascript:void(0);" style="float:left; width:86%; margin: 4px 0px 5px 100px;" onclick="addNewSubFolderDocs('1', this.parentNode.parentNode.parentNode.rowIndex, '<%=tableName %>', 'docsCount', '<%=tableId %>');"> +Add Document</a> 
								</span>
							<% } %>
						</td>
						<% } else {
							String fileIcon = request.getContextPath()+"/images1/icons/icons/file_icon.png";
							if(hmFileIcon.containsKey(hmProDocumentDetails.get("FILE_EXTENSION"))){ 
								fileIcon = request.getContextPath()+"/images1/file_icon/"+hmFileIcon.get(hmProDocumentDetails.get("FILE_EXTENSION"));
							}	
						%>
						<td valign="top">
							<span style="float:left; margin-left: 50px; margin-right: 9px;">
								<input type="text" name="strFolderScopeDoc" id="strFolderScopeDoc1" style="width:150px !important;" value="<%=uF.showData(hmProDocumentDetails.get("DOCUMENT_SCOPE"), "") %>"/> 
								<input type="file" id="strFolderDoc1" name="strFolderDoc" size="5">
							</span>
							<span style="float:left; width: 100%; margin: 10px 9px 10px 50px;">
								<a target="_blank" href="<%=filePath %>" style="font-weight: normal; color: black;"> 
									<img height="18" width="18" src="<%=fileIcon %>" />&nbsp;<%=uF.showData(hmProDocumentDetails.get("DOCUMENT_NAME"),"") %>
								</a>
							</span>
							<span style="float:left; width: 100%;"><textarea rows="3" name="strFolderDescription" id="strFolderDescription1" style="width: 330px !important;"><%=uF.showData(hmProDocumentDetails.get("DESCRIPTION"),"") %></textarea></span>
						</td>
						<% } %>
						<!-- <td valign="top">&nbsp;</td> -->
						<td valign="top">
							<span style="float: left;"> <select name="proCategoryTypeFolder" id="proCategoryTypeFolder1" style="width:100px !important;" onchange="changeCategoryType(this.value, 'orgCatagorySpan1', 'orgProjectSpan1');" <%=strDisable %>>
								<%=(String) request.getAttribute("sbProCategoryTypeFolder") %>
							</select></span>
						</td>
						<td valign="top">
						<%	String strProSpan = "block";
							String strCatSpan = "none";
							if(uF.parseToInt(hmProDocumentDetails.get("CATEGORY")) == 2) {
								strProSpan = "none";
								strCatSpan = "block";
							} %>
							<span id="orgCatagorySpan1" style="float: left; display: <%=strCatSpan %>;"><select name="strOrgCategory" id="strOrgCategory1" style="width:100px !important;" <%=strDisable %>><%=(String)request.getAttribute("sbOrgCategory") %></select></span>
							<span id="orgProjectSpan1" style="float: left; display: <%=strProSpan %>;"><select name="strOrgProject" id="strOrgProject1" style="width:100px !important;" <%=strDisable %>><%=(String)request.getAttribute("sbOrgProjects") %></select></span>
						</td>
						<%	String strResourceSpan = "none";
							if(uF.parseToInt(hmProDocumentDetails.get("SHARING_TYPE")) == 2) {
								strResourceSpan = "block";
							} %>
						<td valign="top">
							<div style="float: left; width: 100%;">
								<span style="float: left;">
									<select name="folderSharingType" id="folderSharingType1" style="width:100px !important;" class="validateRequired" onchange="showHideResources(this.value, 'orgResourceSpan1');"><%=(String)request.getAttribute("sbProSharingType") %></select>
								</span>
								<span id="orgResourceSpan1" style="display: <%=strResourceSpan %>; float: left; margin-top: 3px;">
									<select name="strOrgResources" id="strOrgResources1" style="width:100px !important;" multiple size="4"><%=(String)request.getAttribute("sbOrgResources") %></select>
								</span>
							</div>
							<% 
								boolean isSpoc = (Boolean) request.getAttribute("isSpoc");
								String showPocType = "1";
								String showPocDiv = "none";
								if(isSpoc){
									showPocType = "0";
									showPocDiv = "block";
								}
							%>
							<div style="float: left; width: 100%; margin-top: 5px;">
								<span style="float: left;">
									<a href="javascript:void(0);" style="font-weight:normal;" id="sharePoc" onclick="showEditPoc('orgFolderDocPocSpan');">share customer</a></span>
						    		<input type="hidden" name="showPocType" id="showPocType" value="<%=showPocType %>"/>
						    	<span id="orgFolderDocPocSpan" style="display: <%=showPocDiv %>; float: left; margin-top: 3px;">
						    		<select name="strOrgPocFolderDoc" id="strOrgPocFolderDoc" style="width:100px !important;" multiple size="4"><%=(String)request.getAttribute("sbOrgSPOC") %></select>
						    	</span>
						    </div>
						</td>
						
						<td nowrap="nowrap" valign="top">
							<span id="isFolderDocEditSpan1" style="float: left; width: 100%;">
								<input type="hidden" name="isFolderDocEdit" id="isFolderDocEdit1" value="<%=hmProDocumentDetails.get("EDIT_STATUS_VAL") %>"/>
								<input type="checkbox" name="folderDocEdit" id="folderDocEdit1" <%=hmProDocumentDetails.get("EDIT_STATUS") %> onclick="checkStatus(this, 'isFolderDocEdit1');"/> Edit
							</span>
							<span id="isFolderDocDeleteSpan1" style="float: left; width: 100%;">
								<input type="hidden" name="isFolderDocDelete" id="isFolderDocDelete1" value="<%=hmProDocumentDetails.get("DELETE_STATUS_VAL") %>"/>
								<%
								boolean flag= false;	
								if (strUserType!=null && strUserType.equals(IConstants.EMPLOYEE) && uF.parseToInt(hmProDocumentDetails.get("DELETE_STATUS_VAL")) == 1){
									flag =true;
								} else if(strUserType!=null && !strUserType.equals(IConstants.EMPLOYEE)){
									flag = true;
								}
								if(flag){
								 %>
									<input type="checkbox" name="folderDocDelete" id="folderDocDelete1" <%=hmProDocumentDetails.get("DELETE_STATUS") %> onclick="checkStatus(this, 'isFolderDocDelete1');"/> Delete
								<%} %>
							</span>
						</td>
					</tr>
				<% } else { %>
					<tr id="folderTR1">
						<td valign="top">
							<%if(type != null && type.equals("F")) { %>
							<span style="float:left; width: 100%;"><a href="javascript:void(0);" style="margin: -15px 0px 15px 50px;" onclick="addNewSubFolder('1', this.parentNode.parentNode.parentNode.rowIndex, '<%=tableName %>', 'docsCount', '<%=tableId %>');"> +Add Folder</a> 
							<a href="javascript:void(0);" style="margin: -15px 0px 15px 15px;" onclick="addNewFolderDocs('1', this.parentNode.parentNode.parentNode.rowIndex, '<%=tableName %>', 'docsCount', '<%=tableId %>');"> +Add Document</a></span>
							<% } else if(type != null && type.equals("SF")) { %>
								<span style="float:left; width: 100%;">
									<a href="javascript:void(0);" style="float:left; width:86%; margin: 4px 0px 5px 100px;" onclick="addNewSubFolderDocs('1', this.parentNode.parentNode.parentNode.rowIndex, '<%=tableName %>', 'docsCount', '<%=tableId %>');"> +Add Document</a> 
								</span>
							<% } %>
						</td>
						<td valign="top"></td>
						<td valign="top"></td>
						<td valign="top"></td>
						<td valign="top"></td>
						<td valign="top"></td>
					</tr>
				<% } %>
			</table>
		</div>
		
		<div class="clr"></div>
			<div id="<%=divName %>" style="float: left; width: 100%; text-align: center; padding: 9px; border-top: 1px solid #CCCCCC; <% if(operation != null && operation.equalsIgnoreCase("ADD")) { %> display: none; <% } %>">
				<s:submit value="Update" cssClass="btn btn-primary" cssStyle="margin-right: 5px; padding: 3px;" name="submit" />
				<input type="button" value="Cancel" class="btn btn-danger" style="margin-right: 5px; padding: 3px;" name="cancel" onclick="closeForm();">
			</div>
			
		</s:form>
	</div>
