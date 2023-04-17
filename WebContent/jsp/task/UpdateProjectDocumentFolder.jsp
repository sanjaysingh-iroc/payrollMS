<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="java.net.URLEncoder"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<%@ page import="java.util.*,com.konnect.jpms.util.UtilityFunctions,com.konnect.jpms.task.*"%>
<%@ taglib uri="http://granule.com/tags" prefix="g" %>



<script>
	
	jQuery(document).ready(function() {
		jQuery("#frmUpdateProjectDocumentFolder").validationEngine();
		
		var val = document.getElementById("proFolderSharingType0").value;
		showHideResources(val, 1);
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
	
	
	$("#frmUpdateProjectDocumentFolder").submit(function(e){
		//alert("check ........");
		e.preventDefault();
		var form_data = $("form[name='frmUpdateProjectDocumentFolder']").serialize();
		//alert("form_data =====>> " + form_data);
     	$("#subSubDivResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
     	$.ajax({
 			url : "UpdateProjectDocumentFolder.action",
 			data: form_data,
 			cache: false,
            type: 'POST',
 		    success : function(res) {
 				$("#subSubDivResult").html(res);
 			}
 		});
	});
	
</script>


<%
	UtilityFunctions uF = new UtilityFunctions();
	String strUserType = (String) request.getAttribute("strUserType");	
	String pageType = (String)request.getAttribute("pageType");
	
	Map<String, String> hmProDocumentDetails = (Map<String, String>) request.getAttribute("hmProDocumentDetails");
	String type = (String)request.getAttribute("type");
	String proId = (String)request.getAttribute("proId");
	String taskId = (String)request.getAttribute("taskId");
	String fromPage = (String) request.getAttribute("fromPage");
%>

	<s:form id="frmUpdateProjectDocumentFolder" cssClass="formcss" action="UpdateProjectDocumentFolder" name="frmUpdateProjectDocumentFolder" method="post" enctype="multipart/form-data" theme="simple">
		<div style="float: left; width: 100%;" id="tblDiv">
		<s:hidden name="proId"></s:hidden>
		<s:hidden name="taskId"></s:hidden>
		<s:hidden name="clientId"></s:hidden>
		<s:hidden name="folderName"></s:hidden>
		<s:hidden name="proFolderId"></s:hidden>
		<s:hidden name="operation" value="U"></s:hidden>
		<s:hidden name="type"></s:hidden>
		<s:hidden name="fromPage"></s:hidden>
		<s:hidden name="pageType"></s:hidden>
		
			<table class="table form-table" id="folderdocTable" style="width: 100%;">
			
				<tr id="folderTR_0">
					<td style="width: 50%;">
					<input type="hidden" name="docsCount" id="docsCount" value="1" />
					Folder/Documents</td>
					<!-- <td style="width: 20%;">Scope Document</td> -->
					<td style="width: 12%;">Category</td>
					<td style="width: 12%;">Alignment</td>
					<td style="min-width: 20%;">Sharing</td>
					<td >Rights</td>
				</tr>
				<tr id="folderTR1">
					<!-- <td class="txtlabel alignRight" valign="top" style="width: 18%;">Folder Name:</td> -->
					<td valign="top"><%=(String)request.getAttribute("folderName") %><br/>
						<span style="float:left; width: 100%;"><textarea rows="3" name="strFolderDescription" id="strFolderDescription1" style="width: 330px !important;"><%=uF.showData(hmProDocumentDetails.get("DESCRIPTION"),"") %></textarea></span>
						
						<%if(type != null && type.equals("F")) { %>
						<span style="float:left; width: 100%;"> 
							<% if(fromPage != null && fromPage.equals("VAP")) { %>
								<a href="javascript:void(0);" style="margin: -15px 0px 15px 50px;" onclick="addNewSubFolder('<%=proId %>', '1', this.parentNode.parentNode.parentNode.rowIndex, 'folderdocTable', 'docsCount');"> +Add Folder</a>
								<a href="javascript:void(0);" style="margin: -15px 0px 15px 15px;" onclick="addNewFolderDocs('<%=proId %>', '1', this.parentNode.parentNode.parentNode.rowIndex, 'folderdocTable', 'docsCount');"> +Add Document</a>
							<% } else if(fromPage != null && fromPage.equals("MP")) { %>
								<a href="javascript:void(0);" style="margin: -15px 0px 15px 50px;" onclick="addNewSubFolder('<%=proId %>', '<%=taskId %>', '1', this.parentNode.parentNode.parentNode.rowIndex, 'folderdocTable', 'docsCount');"> +Add Folder</a>
								<a href="javascript:void(0);" style="margin: -15px 0px 15px 15px;" onclick="addNewFolderDocs('<%=proId %>', '<%=taskId %>', '1', this.parentNode.parentNode.parentNode.rowIndex, 'folderdocTable', 'docsCount');"> +Add Document</a>
							<% } else { %>
								<a href="javascript:void(0);" style="margin: -15px 0px 15px 50px;" onclick="addNewSubFolder('1', this.parentNode.parentNode.parentNode.rowIndex, 'folderdocTable', 'docsCount');"> +Add Folder</a>
								<a href="javascript:void(0);" style="margin: -15px 0px 15px 15px;" onclick="addNewFolderDocs('1', this.parentNode.parentNode.parentNode.rowIndex, 'folderdocTable', 'docsCount');"> +Add Document</a>
							<% } %>
						</span>
						<% } else if(type != null && type.equals("SF")) { %>
							<span style="float:left; width: 100%;">
							<% if(fromPage != null && fromPage.equals("VAP")) { %>
								<a href="javascript:void(0);" style="float:left; width:86%; margin: 4px 0px 5px 100px;" onclick="addNewSubFolderDocs('<%=proId %>', '1', this.parentNode.parentNode.parentNode.rowIndex, 'folderdocTable', 'docsCount');"> +Add Document</a>
							<% } else if(fromPage != null && fromPage.equals("MP")) { %>
								<a href="javascript:void(0);" style="float:left; width:86%; margin: 4px 0px 5px 100px;" onclick="addNewSubFolderDocs('<%=proId %>', '<%=taskId %>', '1', this.parentNode.parentNode.parentNode.rowIndex, 'folderdocTable', 'docsCount');"> +Add Document</a>
							<% } else { %>
								<a href="javascript:void(0);" style="float:left; width:86%; margin: 4px 0px 5px 100px;" onclick="addNewSubFolderDocs('1', this.parentNode.parentNode.parentNode.rowIndex, 'folderdocTable', 'docsCount');"> +Add Document</a>
							<% } %>
								 
							</span>
						<% } %>
						
						<!-- <a href="javascript:void(0);" onclick="addFolderDocs('1', this.parentNode.parentNode.rowIndex);"> +Add Document</a> -->
					</td>
					<%	String strTaskSpan = "block";
						String strCategorySpan = "none";
						if(uF.parseToInt(hmProDocumentDetails.get("CATEGORY")) == 2) {
							strTaskSpan = "none";
							strCategorySpan = "block"; 
						} %>
						
					<td valign="top">
					<select name="proCategoryTypeFolder" id="proCategoryTypeFolder1" style="width:100px !important;" onchange="changeCategoryType(this.value, 'proFolderCategorySpan1', 'proFolderTaskSpan1');">
						<%=(String)request.getAttribute("sbProCategoryTypeFolder") %>
					</select>
					</td>
					<td valign="top">
						<span id="proFolderTaskSpan1" style="float: left; display: <%=strTaskSpan %>;">
						<select name="proFolderTask" id="proFolderTask1" style="width:100px !important;">
							<%=(String)request.getAttribute("sbProFolderTasks") %>
						</select>
						</span>
						<span id="proFolderCategorySpan1" style="float: left; display: <%=strCategorySpan %>;">
						<select name="proFolderCategory" id="proFolderCategory1" style="width:100px !important;">
							<%=(String)request.getAttribute("sbProFolderCategory") %>
						</select>
						</span>
					</td>
					
					<td valign="top">
						<div style="float: left; width: 100%;">
							<span style="float: left;"><%=(String)request.getAttribute("sbProSharingType") %></span> 
							<% 
								String proResourceSpan = "proResourceSpan1";
								String proPocSpan = "proPocSpan1";
								if(fromPage != null && fromPage.equals("VAP")) {
									proResourceSpan = "proResourceSpan"+proId+"_1";
									proPocSpan = "proPocSpan"+proId+"_1";
								} else if(fromPage != null && fromPage.equals("MP")) {
									proResourceSpan = "proResourceSpan"+proId+"_"+taskId+"_1";
									proPocSpan = "proPocSpan"+proId+"_"+taskId+"_1";
								}
							%>
							<%	String strResourceSpan = "none";
								if(uF.parseToInt(hmProDocumentDetails.get("SHARING_TYPE")) == 2) {
									strResourceSpan = "block";
								} %>
							<span id="<%=proResourceSpan %>" style="display: <%=strResourceSpan %>; float: left; margin-left: 9px;"><%=(String)request.getAttribute("sbProFolderEmp") %></span>
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
								<a href="javascript:void(0);" style="font-weight:normal;" id="sharePoc" onclick="showEditPoc('<%=proPocSpan %>');">share customer</a></span>
					    		<input type="hidden" name="showPocType" id="showPocType" value="<%=showPocType %>"/>
					    	<span id="<%=proPocSpan %>" style="display: <%=showPocDiv %>; float: left; margin-top: 3px;">
					    		<select name="proFolderPoc" id="proFolderPoc0" style="width:100px !important;" multiple size="4"><%=(String)request.getAttribute("sbOrgSPOC") %></select>
					    	</span>
					    </div>
					</td>
					<td valign="top">
						<span id="isFolderEditSpan1" style="float: left; width: 100%;"><input type="hidden" name="isFolderEdit" id="isFolderEdit1" value="<%=hmProDocumentDetails.get("EDIT_STATUS_VAL") %>">
						<input type="checkbox" name="folderEdit" id="folderEdit1" <%=hmProDocumentDetails.get("EDIT_STATUS") %> onclick="checkStatus(this, 'isFolderEdit1');"/>Edit</span>
						<span id="isFolderDeleteSpan1" style="float: left; width: 100%;">
							<input type="hidden" name="isFolderDelete" id="isFolderDelete1" value="<%=hmProDocumentDetails.get("DELETE_STATUS_VAL") %>">
						<%
							boolean flag= false;	
							if (strUserType!=null && strUserType.equals(IConstants.EMPLOYEE) && uF.parseToInt(hmProDocumentDetails.get("DELETE_STATUS_VAL")) == 1){
								flag =true;
							} else if(strUserType!=null && !strUserType.equals(IConstants.EMPLOYEE)) {
								flag = true;
							}
							if(flag) {
						%>
							<input type="checkbox" name="folderDelete" id="folderDelete1" <%=hmProDocumentDetails.get("DELETE_STATUS") %> onclick="checkStatus(this, 'isFolderDelete1');"/>Delete
						<% } %>
						</span>
					</td>
				</tr>
			</table>
		</div>
		<div class="clr"></div>
		
		<div style="float: left; width: 100%; text-align: center; padding-top: 10px; margin-bottom: 5px; border-top: 1px solid #CCCCCC;">
			<s:submit value="Update" cssClass="btn btn-primary" name="submit" />
		</div>
			
		</s:form>

