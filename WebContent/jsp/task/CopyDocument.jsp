<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="java.net.URLEncoder"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<%@ page import="java.util.*,com.konnect.jpms.util.UtilityFunctions,com.konnect.jpms.task.*"%>
<%@ taglib uri="http://granule.com/tags" prefix="g" %>

<script>
	
	jQuery(document).ready(function() {
		jQuery("#frmCopyDocument").validationEngine();
		
		/* var val = document.getElementById("proFolderSharingType0").value;
		showHideResources(val, 1); */
	});
</script>
<%
	UtilityFunctions uF = new UtilityFunctions();  
	String strUserType = (String) request.getAttribute("strUserType");	

	Map<String, String> hmProDocumentDetails = (Map<String, String>) request.getAttribute("hmProDocumentDetails");
	String type = (String)request.getAttribute("type");
	String filePath = (String) request.getAttribute("filePath");
	
	Map<String, String> hmFileIcon = (Map<String, String>) request.getAttribute("hmFileIcon");
	if(hmFileIcon == null) hmFileIcon = new HashMap<String, String>();
	
	String fileIcon = request.getContextPath()+"/images1/icons/icons/file_icon.png";
	if(hmFileIcon.containsKey(hmProDocumentDetails.get("FILE_EXTENSION"))){ 
		fileIcon = request.getContextPath()+"/images1/file_icon/"+hmFileIcon.get(hmProDocumentDetails.get("FILE_EXTENSION"));
	}
	
%>

	<div class="box box-body">
		<s:form id="frmCopyDocument" cssClass="formcss" action="CopyDocument" name="frmCopyDocument" method="post" enctype="multipart/form-data" theme="simple">
			<div class="box-body table-responsive no-padding">
			<s:hidden name="strId"></s:hidden>
			<s:hidden name="folderName"></s:hidden>
			<s:hidden name="operation" value="U"></s:hidden>
			<s:hidden name="type"></s:hidden>
			<s:hidden name="fromPage"></s:hidden>
			<s:hidden name="existPath"></s:hidden>

				<table class="table table-hover" id="folderdocTable">
					<tr id="folderTR_0">
						<td style="width: 50%; border-top: 0px;">
						<input type="hidden" name="docsCount" id="docsCount" value="1" />Documents</td>
						<!-- <td style="width: 20%;">Scope Document</td> -->
						<td style="width: 12%; border-top: 0px;">Category</td>
						<td style="width: 12%; border-top: 0px;">Alignment</td>
						<td style="min-width: 19%; border-top: 0px;">Sharing</td>
						<td style="border-top: 0px;">Rights</td>
					</tr>
					<tr id="folderTR1">
						<td valign="top">
							<span style="float:left; margin-left: 50px; margin-right: 9px;">
								<input type="text" name="strFolderScopeDoc" id="strFolderScopeDoc1" style="width:150px !important;" value="<%=uF.showData(hmProDocumentDetails.get("DOCUMENT_SCOPE"), "") %>"/> 
								<!-- <input type="file" id="strFolderDoc1" name="strFolderDoc" size="5"> -->
							</span>
							<span style="float:left; width: 100%; margin: 10px 9px 10px 50px;">
								<a target="_blank" href="<%=filePath %>" style="font-weight: normal; color: black;"> 
									<img height="15" width="15" src="<%=fileIcon %>" />&nbsp;<%=uF.showData(hmProDocumentDetails.get("DOCUMENT_NAME"),"") %>
								</a>
							</span>
							<span style="float:left; width: 100%;"><textarea rows="3" name="strFolderDescription" id="strFolderDescription1" style="width: 330px !important;"><%=uF.showData(hmProDocumentDetails.get("DESCRIPTION"),"") %></textarea></span>
						</td>
						<td valign="top">
							<span style="float: left;"> <select name="proCategoryTypeFolder" id="proCategoryTypeFolder1" style="width:100px !important;" onchange="changeCategoryType(this.value, 'orgCatagorySpan1', 'orgProjectSpan1');">
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
							<span id="orgCatagorySpan1" style="float: left; display: <%=strCatSpan %>;"><select name="strOrgCategory" id="strOrgCategory1" style="width:100px !important;"><%=(String)request.getAttribute("sbOrgCategory") %></select></span>
							<span id="orgProjectSpan1" style="float: left; display: <%=strProSpan %>;"><select name="strOrgProject" id="strOrgProject1" style="width:100px !important;"><%=(String)request.getAttribute("sbOrgProjects") %></select></span>
						</td>
						<%	String strResourceSpan = "none";
							if(uF.parseToInt(hmProDocumentDetails.get("SHARING_TYPE")) == 2) {
								strResourceSpan = "block";
							} %>
						<td valign="top"><span style="float: left;"><select name="folderSharingType" id="folderSharingType1" style="width:100px !important;" class="validateRequired" onchange="showHideResources(this.value, 'orgResourceSpan1');"><%=(String)request.getAttribute("sbProSharingType") %></select></span> 
							<br/><span id="orgResourceSpan1" style="display: <%=strResourceSpan %>; float: left; margin-top: 3px;"><select name="strOrgResources" id="strOrgResources1" style="width:100px !important;" multiple size="4"><%=(String)request.getAttribute("sbOrgResources") %></select></span>
						</td>
						
						<td valign="top">
							<span id="isFolderDocEditSpan1" style="float: left; width: 100%;">
								<input type="hidden" name="isFolderDocEdit" id="isFolderDocEdit1" value="<%=hmProDocumentDetails.get("EDIT_STATUS_VAL") %>"/>
								<input type="checkbox" name="folderDocEdit" id="folderDocEdit1" <%=hmProDocumentDetails.get("EDIT_STATUS") %> onclick="checkStatus(this, 'isFolderDocEdit1');"/>Edit
							</span>
							<span id="isFolderDocDeleteSpan1" style="float: left; width: 100%;">
								<input type="hidden" name="isFolderDocDelete" id="isFolderDocDelete1" value="<%=hmProDocumentDetails.get("DELETE_STATUS_VAL") %>"/>
								<input type="checkbox" name="folderDocDelete" id="folderDocDelete1" <%=hmProDocumentDetails.get("DELETE_STATUS") %> onclick="checkStatus(this, 'isFolderDocDelete1');"/>Delete
							</span>
						</td>
					</tr>
					
				</table>
			</div>
		
			<div class="clr"></div>
		
			<div style="float: left; width: 100%; text-align: center; padding: 5px; border-top: 1px solid lightgray;">
				<s:submit value="Copy" cssClass="btn btn-primary" cssStyle="margin-right: 5px; padding: 3px;" name="submit" />
				<input type="button" value="Cancel" class="btn btn-danger" style="margin-right: 5px; padding: 3px;" name="cancel" onclick="closeForm();">
			</div>
		</s:form>
	</div>

