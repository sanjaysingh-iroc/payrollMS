<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="java.net.URLEncoder"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<%@ page import="java.util.*,com.konnect.jpms.util.UtilityFunctions,com.konnect.jpms.task.*"%>
<%@ taglib uri="http://granule.com/tags" prefix="g" %>



<script>
	
	jQuery(document).ready(function() {
		jQuery("#frmUpdateProjectDocumentFile").validationEngine();
		
		var val = document.getElementById("proFolderSharingType0").value;
		showHideResources(val, 1);
	});
	
	
	var proTasks = '<%=request.getAttribute("sbProTasks")%>';
	var proEmployee = '<%=request.getAttribute("sbProEmp")%>';
	var proCategory = '<%=request.getAttribute("sbProCategory")%>';
	
	function changeCategoryType(val,id,cnt,folderTRcnt,type){
		if(val == '1'){
			document.getElementById(id).innerHTML = proTasks;
			document.getElementById("proTasks").name = "proFolderTasks";
			document.getElementById("proTasks").id = "proFolderTasks";
			
		} else if(val == '2'){
			document.getElementById(id).innerHTML = proCategory;
			document.getElementById("proTasks").name = "proFolderTasks";
			document.getElementById("proTasks").id = "proFolderTasks";
		}
	}
	
	function deleteFDocs(count) {
		if(confirm('Are you sure, you want to delete this document?')) {
			var trIndex = document.getElementById("docTR"+count).rowIndex;
		    document.getElementById("folderdocTable").deleteRow(trIndex);
		}
	}
	
	function showHideResources(val, count) {
		if(val == '2') {
			document.getElementById("proResourceSpan"+count).style.display = 'block';
		} else {
			document.getElementById("proResourceSpan"+count).style.display = 'none';
		}
	}
	
	function showTblHeader() {
		document.getElementById("folderTR0").style.display = 'table-row';
	}
	
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
	
	
	$("#frmUpdateProjectDocumentFile").submit(function(e){
		//alert("check ........");
		e.preventDefault();
		var form_data = $("form[name='frmUpdateProjectDocumentFile']").serialize();
     	$("#subSubDivResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
     	$.ajax({
 			url : "UpdateProjectDocumentFile.action",
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
	String filePath = (String) request.getAttribute("filePath");
	
	Map<String, String> hmFileIcon = (Map<String, String>) request.getAttribute("hmFileIcon");
	if(hmFileIcon == null) hmFileIcon = new HashMap<String, String>();
	
	String fileIcon = request.getContextPath()+"/images1/icons/icons/file_icon.png";
	if(hmFileIcon.containsKey(hmProDocumentDetails.get("FILE_EXTENSION"))){ 
		fileIcon = request.getContextPath()+"/images1/file_icon/"+hmFileIcon.get(hmProDocumentDetails.get("FILE_EXTENSION"));
	}
%>

	<s:form id="frmUpdateProjectDocumentFile" cssClass="formcss" action="UpdateProjectDocumentFile" name="frmUpdateProjectDocumentFile" method="post" enctype="multipart/form-data" theme="simple">
		<div style="float: left; width: 100%;" id="tblDiv">
			<s:hidden name="proId"></s:hidden>
			<s:hidden name="taskId"></s:hidden>
			<s:hidden name="clientId"></s:hidden>
			<s:hidden name="folderName"></s:hidden>
			<s:hidden name="proFolderId"></s:hidden>
			<s:hidden name="operation" value="U"></s:hidden>
			<s:hidden name="type"></s:hidden>
			<s:hidden name="filePath"></s:hidden>
			<s:hidden name="fileDir"></s:hidden>
			<s:hidden name="fromPage"></s:hidden>
			<s:hidden name="pageType"></s:hidden>
			
			<table class="table form-table" id="folderdocTable" style="width: 100%;">
			
				<tr id="folderTR0">
					<td style="width: 45%;">
					<input type="hidden" name="docscount" id="docscount" value="1" />
					Folder/Documents</td>
					<!-- <td style="width: 20%;">Scope Document</td> -->
					<td style="width: 12%;">Category</td>
					<td style="width: 12%;">Alignment</td>
					<td style="width: 20%;">Sharing</td>
					<td>Rights</td>
				</tr>
				<tr id="docTR2">
					<td>
						<span style="float:left; margin-left: 50px; margin-right: 9px;">
							<input type="text" style="width:150px !important;" id="strFolderScopeDoc" name="strFolderScopeDoc" value="<%=uF.showData(hmProDocumentDetails.get("SCOPE_DOCUMENT"), "") %>"/> 
							<input type="file" id="strFolderDoc" name="strFolderDoc" size="5"/>
						</span>
						<span style="float:left; width: 100%; margin: 10px 9px 10px 50px;">
							<a target="_blank" href="<%=filePath %>" style="font-weight: normal; color: black;"> 
								<img height="18" width="18" src="<%=fileIcon %>" />&nbsp;<%=uF.showData(hmProDocumentDetails.get("DOCUMENT_NAME"), "") %>
							</a>
						</span>
						<span style="float:left; width: 100%; margin-left: 50px; margin-right: 9px;">
							<textarea style="width: 330px !important;" id="strFolderDocDescription" name="strFolderDocDescription" rows="3"><%=uF.showData(hmProDocumentDetails.get("DESCRIPTION"), "") %></textarea>
						</span>
					</td>
					<td valign="top"><%=(String)request.getAttribute("sbProCategoryTypeFolder") %></td>
					<td valign="top"><span id="proFolderTaskSpan1" style="float: left;"><%=(String)request.getAttribute("sbProFolderTasks") %></span></td>
					
					<td valign="top">
						<div style="float: left; width: 100%;">
							<span style="float: left;"><%=(String)request.getAttribute("sbProSharingType") %></span> 
							<span id="proResourceSpan1"style="display: none; float: left; margin-left: 9px;"><%=(String)request.getAttribute("sbProFolderEmp") %></span>
						</div>
						<% 
							boolean isSpoc = (Boolean) request.getAttribute("isSpoc");
							String showPocType = "1";
							String showPocDiv = "none";
							if(isSpoc) {
								showPocType = "0";
								showPocDiv = "block";
							}
						%>
						<div style="float: left; width: 100%; margin-top: 5px;">
							<span style="float: left;">
								<a href="javascript:void(0);" style="font-weight:normal;" id="sharePoc" onclick="showEditPoc('proPocSpan1');">share customer</a></span>
					    		<input type="hidden" name="showPocType" id="showPocType" value="<%=showPocType %>"/>
					    	<span id="proPocSpan1" style="display: <%=showPocDiv %>; float: left; margin-top: 3px;">
					    		<select name="proFolderPoc" id="proFolderPoc0" style="width:100px !important;" multiple size="4"><%=(String)request.getAttribute("sbOrgSPOC") %></select>
					    	</span>
					    </div>
					</td>
					<td valign="top">
					<span id="isFolderDocEditSpan1" style="float: left; width: 100%;"><input type="hidden" name="isFolderDocEdit" id="isFolderDocEdit1" value="<%=hmProDocumentDetails.get("EDIT_STATUS_VAL") %>">
						<input type="checkbox" name="folderDocEdit" id="folderDocEdit1" <%=hmProDocumentDetails.get("EDIT_STATUS") %> onclick="checkStatus(this, 'isFolderDocEdit1');"/>Edit</span>
						<span id="isFolderDocDeleteSpan1" style="float: left; width: 100%;">
							<input type="hidden" name="isFolderDocDelete" id="isFolderDocDelete1" value="<%=hmProDocumentDetails.get("DELETE_STATUS_VAL") %>">
							<%
							boolean flag = false;	
							if (strUserType!=null && strUserType.equals(IConstants.EMPLOYEE) && uF.parseToInt(hmProDocumentDetails.get("DELETE_STATUS_VAL")) == 1) {
								flag = true;
							} else if(strUserType!=null && !strUserType.equals(IConstants.EMPLOYEE)) {
								flag = true;
							}
							if(flag){
							%>
							<input type="checkbox" name="folderDocDelete" id="folderDocDelete1" <%=hmProDocumentDetails.get("DELETE_STATUS") %> onclick="checkStatus(this, 'isFolderDocDelete1');"/>Delete
							<%} %>
						</span>
					</td>
				</tr>
			</table>
		</div>
		<div class="clr"></div>
		
			<div style="float: left; width: 100%; text-align: center; padding-top: 10px; margin-bottom: 5px; border-top: 1px solid #CCCCCC;">
				<s:submit value="Update" cssClass="btn btn-primary" name="submit" />
			</div>
			
		<%-- <div style="margin: 0px 0px 0px 210px">
			<table class="table_style">
				<tr> <td class="txtlabel alignLeft"><s:submit value="Update" cssClass="input_button" name="submit" ></s:submit></td>
					<td></td>
				</tr>
			</table>
		</div> --%>
		
		</s:form>

