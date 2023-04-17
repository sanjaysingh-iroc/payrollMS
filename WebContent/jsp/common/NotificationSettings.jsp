<%@page import="com.konnect.jpms.util.CommonFunctions"%>
<%@page import="com.konnect.jpms.util.ArrayUtils"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@taglib uri="/struts-tags" prefix="s"%>
<script src="scripts/ckeditor_cust/ckeditor.js"></script>


<style>
.noti_title {
	margin: 0px 0px 10px 0px;
	padding-left: 100px;
	text-align: left;
}

.box {
	margin-bottom: 10px;
}

.box-header {
	padding: 5px;
}
</style>
<g:compress>
	<script>
	
	// The instanceReady event is fired, when an instance of CKEditor has finished
	// its initialization.  
	CKEDITOR.on( 'instanceReady', function( ev ) {
		// Show the editor name and description in the browser status bar.
		if(document.getElementById( 'eMessage' )) {
			document.getElementById( 'eMessage' ).innerHTML = 'Instance <code>' + ev.editor.name + '<\/code> loaded.';
		}
	
		// Show this sample buttons.
		if(document.getElementById( 'eButtons' )) {
		document.getElementById( 'eButtons' ).style.display = 'block';
		}
	}); 
	function InsertHTML() {
		// Get the editor instance that we want to interact with.
		var editor = CKEDITOR.instances.editor1;
		var value = document.getElementById( 'htmlArea' ).value;
	
		// Check the active editing mode.
		if ( editor.mode == 'wysiwyg' )
		{
			// Insert HTML code.
			// http://docs.ckeditor.com/#!/api/CKEDITOR.editor-method-insertHtml
			editor.insertHtml( value );
		}
		else
			alert( 'You must be in WYSIWYG mode!' );
	}
	
	function InsertText() {
		// Get the editor instance that we want to interact with.
		var editor = CKEDITOR.instances.editor1;
		var value = document.getElementById( 'txtArea' ).value;
	
		// Check the active editing mode.
		if ( editor.mode == 'wysiwyg' )
		{
			// Insert as plain text.
			// http://docs.ckeditor.com/#!/api/CKEDITOR.editor-method-insertText
			editor.insertText( value );
		}
		else
			alert( 'You must be in WYSIWYG mode!' );
	}
	
	function SetContents() {
		// Get the editor instance that we want to interact with.
		var editor = CKEDITOR.instances.editor1;
		var value = document.getElementById( 'htmlArea' ).value;
	
		// Set editor contents (replace current contents).
		// http://docs.ckeditor.com/#!/api/CKEDITOR.editor-method-setData
		editor.setData( value );
	}
	
	function GetContents() {
		// Get the editor instance that you want to interact with.
		var editor = CKEDITOR.instances.editor1;
	
		// Get editor contents
		// http://docs.ckeditor.com/#!/api/CKEDITOR.editor-method-getData
		alert( editor.getData() );
	}
	
	function ExecuteCommand( commandName ) {
		// Get the editor instance that we want to interact with.
		var editor = CKEDITOR.instances.editor1;
	
		// Check the active editing mode.
		if ( editor.mode == 'wysiwyg' )
		{
			// Execute the command.
			// http://docs.ckeditor.com/#!/api/CKEDITOR.editor-method-execCommand
			editor.execCommand( commandName );
		}
		else
			alert( 'You must be in WYSIWYG mode!' );
	}
	
	
	function CheckDirty() {
		// Get the editor instance that we want to interact with.
		var editor = CKEDITOR.instances.editor1;
		// Checks whether the current editor contents present changes when compared
		// to the contents loaded into the editor at startup
		// http://docs.ckeditor.com/#!/api/CKEDITOR.editor-method-checkDirty
		alert( editor.checkDirty() );
	}
	
	function ResetDirty() {
		// Get the editor instance that we want to interact with.
		var editor = CKEDITOR.instances.editor1;
		// Resets the "dirty state" of the editor (see CheckDirty())
		// http://docs.ckeditor.com/#!/api/CKEDITOR.editor-method-resetDirty
		editor.resetDirty();
		alert( 'The "IsDirty" status has been reset' );
	}
	
	function Focus() {
		CKEDITOR.instances.editor1.focus();
	}
	
	function onFocus() {
		if(document.getElementById( 'eMessage' )) {
			document.getElementById( 'eMessage' ).innerHTML = '<b>' + this.name + ' is focused </b>';
		}
	}
	
	function onBlur() {
		if(document.getElementById( 'eMessage' )) {
			document.getElementById( 'eMessage' ).innerHTML = this.name + ' lost focus';
		}
	}
	
	 function uncheckbox(x,z) {
	    	var arr = document.getElementsByName(z);
	    	 var count = 0;
	    	 for(i = 0 ;i<arr.length;i++){
			   if(arr[i].checked)
	    		 count = count + 1;
			 }
	    	 if(count > 1){
	    		for(i = 0 ;i<arr.length;i++) {
	    		if(arr[i].value != x.value){
	 						arr[i].checked =false;
	     		 }
	    	 	 }
	    	}
	   
	    }

	CKEDITOR.config.width='700px';
		</script>
</g:compress>

<script type="text/javascript">
	function updateEmailStatus(divId,notId,isMail,chId) {
		var action = "NotificationSettings.action?type=email&strId="+notId+"&status="+isMail+"&divId="+divId+"&chId="+chId;
		//console.log("action==>"+action);
		var xmlhttp = GetXmlHttpObject();
		if (xmlhttp == null) {
			alert("Browser does not support HTTP Request");
			return;
		} else {

			var xhr = $.ajax({
				url : action,
				cache : false,
				success : function(data) {
				//	console.log("data==>"+data);
					if(data == ""){
                		
                	}else if(data != "" && data.trim() === "NA"){
                		alert("Notification Constant not defined!");
                	}else {
                		var allData = data.split("::::");
                		document.getElementById(chId).checked = (allData[0] == "true");
                        document.getElementById(divId).innerHTML = allData[1];
                	}
				}
			});
		}
	}
	
	function GetXmlHttpObject() {
	    if (window.XMLHttpRequest) {
	            return new XMLHttpRequest();
	    }
	    if (window.ActiveXObject) {
	            return new ActiveXObject("Microsoft.XMLHTTP");
	    }
	    return null;
	}
	
	
	  function fillFileStatus(ids){
      	document.getElementById(ids).value=1;
      }
</script>


<%
	UtilityFunctions uF = new UtilityFunctions();
	String PRODUCTTYPE = (String) session.getAttribute(IConstants.PRODUCT_TYPE);

	Map<String, String> hmNotificationStatus = (Map<String, String>) request
			.getAttribute("hmNotificationStatus");
	if (hmNotificationStatus == null)
		hmNotificationStatus = new HashMap<String, String>();

	String[] arrEnabledModules = (String[]) session.getAttribute("arrEnabledModules");
//===start parvez date: 18-03-2023===	
	CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);
	Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(request);
//===end parvez date: 18-03-2023===	
%>

<div class="box-body">

	<div class="msg nodata">
		<span>If you are not sure of the setting please do not change
			them as they can affect the system.</span>
		<p
			style="font-size: 10px; padding-left: 42px; padding-right: 10px; font-style: italic; text-align: right;">
			Last updated by
			<%=uF.showData((String) request.getAttribute("UPDATED_NAME"), "N/A")%>
			on
			<%=uF.showData((String) request.getAttribute("UPDATED_DATE"), "N/A")%></p>
	</div>
	<%=uF.showData((String) request.getAttribute("MESSAGE"), "")%>


	<s:form theme="simple" name="frm_Notification"
		action="NotificationSettings" method="POST" cssClass="formcss"
		enctype="multipart/form-data" cssStyle="float:left;width:100%">

		<s:hidden name="strNotificationCode" />
		<s:hidden name="userscreen" id="userscreen" />
		<s:hidden name="navigationId" id="navigationId" />
		<s:hidden name="toPage" id="toPage" />


		<!-- -------------------------- People and Users --------------------------- -->
		<div
			style="width: 100%; text-align: center; font-size: 14px; font-weight: bold; margin: 7px;"
			class="">People and Users</div>
		<div class="box box-primary collapsed-box"
			style="border-top-color: #EEEEEE; margin-top: 10px;">
			<div class="box-header with-border">
				<h3 class="box-title" style="font-size: 14px; padding-right: 10px;">
					Employee Profile Updated Notification
					<%
					String strNotId = "" + IConstants.N_UPD_EMPLOYEE_PROFILE;
						boolean isMail = (Boolean) request.getAttribute("isEmailEmployeeProfileUpdated");
						String strEImage = isMail ? "mail_enbl.png" : "mail_disbl.png";
						String strEEnable = isMail ? "Disable Mail Notification" : "Enable Mail Notification";
						if (isMail) {
							isMail = false;
						} else {
							isMail = true;
						}
				%>
					<span id="myDivE<%=strNotId%>" style="float: right"> <img
						width="20px" src="images1/<%=strEImage%>"
						title="<%=strEEnable%>"
						onclick="updateEmailStatus('myDivE<%=strNotId%>',<%=strNotId%>,<%=isMail%>,'isEmailEmployeeProfileUpdated');" />
					</span>

				</h3>
				<div class="box-tools pull-right">
					<button class="btn btn-box-tool" data-widget="collapse">
						<i class="fa fa-plus"></i>
					</button>
					<button class="btn btn-box-tool" data-widget="remove">
						<i class="fa fa-times"></i>
					</button>
				</div>
			</div>
			<!-- /.box-header -->
			<div class="box-body"
				style="padding: 5px; overflow-y: auto; display: none;">
				<div class="content1" style="margin: 0px 0px 10px 0px">
					<table class="table table_no_border" cellspacing="2"
						cellpadding="2" width="100%">
						<tr>
							<td valign="top" class="txtlabel alignRight">Email
								Notification:</td>
							<td><s:checkbox name="isEmailEmployeeProfileUpdated"
									id="isEmailEmployeeProfileUpdated" /><span class="hint">Please
									select the email notification.<span class="hint-pointer">&nbsp;</span>
							</span></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Subject:</td>
							<td><s:textfield cssStyle="width:480px"
									name="strSubjectEmployeeProfileUpdated" /><span class="hint">Please
									enter the email Subject.<span class="hint-pointer">&nbsp;</span>
							</span></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Body:</td>
							<td><div style="float: left; margin-right: 20px;">
									<s:textarea name="strBodyEmployeeProfileUpdated" cols="63"
										rows="10" />
									<span class="hint">Please enter the email body.<span
										class="hint-pointer">&nbsp;</span></span>
								</div>
								<div style="float: left;">
									[LOGIN_LINK] <br /> [EMPCODE] <br /> [EMPFNAME] <br />
									[EMPLNAME] <br /> [LEGAL_ENTITY_NAME] <br />
								</div></td>
						</tr>

					</table>
				</div>
			</div>
			<!-- /.box-body -->
		</div>

		<div class="box box-primary collapsed-box"
			style="border-top-color: #EEEEEE; margin-top: 10px;">
			<div class="box-header with-border">
				<h3 class="box-title" style="font-size: 14px; padding-right: 10px;">
					New Employee Notification
					<%-- <%=uF.showData(hmNotificationStatus.get("NOT_"+IConstants.N_NEW_EMPLOYEE), "") %> --%>
					<%
						strNotId = "" + IConstants.N_NEW_EMPLOYEE;
							isMail = (Boolean) request.getAttribute("isEmailNewEmployee");
							strEImage = isMail ? "mail_enbl.png" : "mail_disbl.png";
							strEEnable = isMail ? "Disable Mail Notification" : "Enable Mail Notification";

							if (isMail) {
								isMail = false;
							} else {
								isMail = true;
							}
					%>
					<span id="myDivE<%=strNotId%>" style="float: right"> <img
						width="20px" src="images1/<%=strEImage%>"
						title="<%=strEEnable%>"
						onclick="updateEmailStatus('myDivE<%=strNotId%>',<%=strNotId%>,<%=isMail%>,'isEmailNewEmployee');" />
					</span>
				</h3>
				<div class="box-tools pull-right">
					<button class="btn btn-box-tool" data-widget="collapse">
						<i class="fa fa-plus"></i>
					</button>
					<button class="btn btn-box-tool" data-widget="remove">
						<i class="fa fa-times"></i>
					</button>
				</div>
			</div>
			<!-- /.box-header -->
			<div class="box-body"
				style="padding: 5px; overflow-y: auto; display: none;">
				<div class="content1" style="margin: 0px 0px 10px 0px">
					<table class="table table_no_border" cellspacing="2"
						cellpadding="2" width="100%">
						<tr>
							<td valign="top" class="txtlabel alignRight">Email
								Notification:</td>
							<td><s:checkbox name="isEmailNewEmployee"
									id="isEmailNewEmployee" /><span class="hint">Please
									select the email notification.<span class="hint-pointer">&nbsp;</span>
							</span></td>
						</tr>
						<%-- <tr>
								<td valign="top" class="txtlabel alignRight">Enable/Disable Text Notification:</td>
								<td><s:checkbox name="isTextNewEmployee"/><span class="hint">Please confirm the password by entering it once again.<span class="hint-pointer">&nbsp;</span></span></td>
							</tr>
							<tr>
								<td valign="top" class="txtlabel alignRight">Text Message</td>
								<td><s:textfield cssStyle="width:480px" name="strTextNewEmployee"/><span class="hint">Please confirm the password by entering it once again.<span class="hint-pointer">&nbsp;</span></span></td>
							</tr> --%>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Subject:</td>
							<td><s:textfield cssStyle="width:480px"
									name="strSubjectNewEmployee" /><span class="hint">Please
									enter the email Subject.<span class="hint-pointer">&nbsp;</span>
							</span></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Body:</td>
							<td><div style="float: left; margin-right: 20px;">
									<s:textarea name="strBodyNewEmployee" id="editor4" cols="63"
										rows="10" />
									<span class="hint">Please enter the email body.<span
										class="hint-pointer">&nbsp;</span></span>
								</div>
								<div style="float: left;">
									[LOGIN_LINK] <br /> [EMPCODE] <br /> [EMPFNAME] <br />
									[EMPLNAME] <br /> [USERNAME] <br /> [PASSWORD] <br />
									[LEGAL_ENTITY_NAME] <br />
								</div></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Backgroud
								Image:</td>
							<td><input type="hidden"
								name="strBackgroundNewEmpImageStatus"
								id="strBackgroundNewEmpImageStatus" value="0"></input> <input
								type="file" name="strBackgroundNewEmpImage"
								id="strBackgroundNewEmpImage"
								accept=".jpg,.png,.svg,.svgz,.doc,.docs,.docx,.pdf"
								onchange="fillFileStatus('strBackgroundNewEmpImageStatus')" /> <span
								class="hint">Please select the email background image.<span
									class="hint-pointer">&nbsp;</span></span></td>
						</tr>



					</table>
				</div>
			</div>
			<!-- /.box-body -->
		</div>

		<div class="box box-primary collapsed-box"
			style="border-top-color: #EEEEEE; margin-top: 10px;">

			<div class="box-header with-border">
				<h3 class="box-title" style="font-size: 14px; padding-right: 10px;">
					New Employee Joining Notification
					<%-- <%=uF.showData(hmNotificationStatus.get("NOT_"+IConstants.N_NEW_EMPLOYEE_JOINING), "") %> --%>
					<%
						strNotId = "" + IConstants.N_NEW_EMPLOYEE_JOINING;
							isMail = (Boolean) request.getAttribute("isEmailNewEmployeeJoining");
							strEImage = isMail ? "mail_enbl.png" : "mail_disbl.png";
							strEEnable = isMail ? "Disable Mail Notification" : "Enable Mail Notification";

							if (isMail) {
								isMail = false;
							} else {
								isMail = true;
							}
					%>
					<span id="myDivE<%=strNotId%>" style="float: right"> <img
						width="20px" src="images1/<%=strEImage%>"
						title="<%=strEEnable%>"
						onclick="updateEmailStatus('myDivE<%=strNotId%>',<%=strNotId%>,<%=isMail%>,'isEmailNewEmployeeJoining');" />
					</span>
				</h3>
				<div class="box-tools pull-right">
					<button class="btn btn-box-tool" data-widget="collapse">
						<i class="fa fa-plus"></i>
					</button>
					<button class="btn btn-box-tool" data-widget="remove">
						<i class="fa fa-times"></i>
					</button>
				</div>
			</div>
			<!-- /.box-header -->
			<div class="box-body"
				style="padding: 5px; overflow-y: auto; display: none;">
				<div class="content1" style="margin: 0px 0px 10px 0px">
					<table class="table table_no_border" cellspacing="2"
						cellpadding="2" width="100%">
						<tr>
							<td valign="top" class="txtlabel alignRight">Email
								Notification:</td>
							<td><s:checkbox name="isEmailNewEmployeeJoining"
									id="isEmailNewEmployeeJoining" /><span class="hint">Please
									select the email notification.<span class="hint-pointer">&nbsp;</span>
							</span></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Subject:</td>
							<td><s:textfield cssStyle="width:480px"
									name="strSubjectNewEmployeeJoining" /><span class="hint">Please
									enter the email Subject.<span class="hint-pointer">&nbsp;</span>
							</span></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Body:</td>
							<td><div style="float: left; margin-right: 20px;">
									<s:textarea name="strBodyNewEmployeeJoining" cols="63"
										rows="10" />
									<span class="hint">Please enter the email body.<span
										class="hint-pointer">&nbsp;</span></span>
								</div>
								<div style="float: left;">
									[ADD_EMP_LINK] <br /> [EMPFNAME] <br /> [EMPLNAME] <br />
									[LEGAL_ENTITY_NAME] <br />
								</div></td>
						</tr>

					</table>
				</div>
			</div>
			<!-- /.box-body -->
		</div>

		<%-- <div class="box box-primary collapsed-box" style="border-top-color: #EEEEEE; margin-top: 10px;">
                <div class="box-header with-border">
                    <h3 class="box-title" style="font-size: 14px;padding-right: 10px;">
                    	Employee Status Change Notification 
						<%
							strNotId = ""+IConstants.N_EMPLOYEE_STATUS_CHANGE; 
							isMail = (Boolean) request.getAttribute("isEmailNewEmployeeStatusChange");
							strEImage = isMail ? "mail_enbl.png" : "mail_disbl.png";
							strEEnable = isMail ? "Disable Mail Notification" : "Enable Mail Notification" ;
							
							if(isMail){
								isMail = false;
							} else{
								isMail = true;
							}
						%>
						<span id="myDivE<%=strNotId%>" style="float:right">
							<img width="20px" src="images1/<%=strEImage %>" title="<%=strEEnable %>" onclick="updateEmailStatus('myDivE<%=strNotId%>',<%=strNotId%>,<%=isMail%>,'isEmailNewEmployeeStatusChange');"/>
						</span>
					</h3>
                    <div class="box-tools pull-right">
                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
                    </div>
                </div>
                <!-- /.box-header -->
                <div class="box-body" style="padding: 5px; overflow-y: auto; display:none;">
                    <div class="content1" style="margin:0px 0px 10px 0px">
						<table class="table table_no_border" cellspacing="2" cellpadding="2" width="100%">
							<tr>
								<td valign="top" class="txtlabel alignRight">Email Notification:</td>
								<td><s:checkbox name="isEmailNewEmployeeStatusChange" id="isEmailNewEmployeeStatusChange"/><span class="hint">Please select the email notification.<span class="hint-pointer">&nbsp;</span></span></td>
							</tr>
							<tr>
								<td valign="top" class="txtlabel alignRight">Email Subject:</td>
								<td><s:textfield cssStyle="width:480px" name="strSubjectNewEmployeeStatusChange"/><span class="hint">Please enter the email Subject.<span class="hint-pointer">&nbsp;</span></span></td>
							</tr>
							<tr>
								<td valign="top" class="txtlabel alignRight">Email Body:</td>
								<td><div style="float: left; margin-right: 20px;"><s:textarea name="strBodyNewEmployeeStatusChange" cols="63" rows="10" /><span class="hint">Please enter the email body.<span class="hint-pointer">&nbsp;</span></span></div>
									<div style="float: left;">  
										[LOGIN_LINK] <br/> 	
										[EMPCODE] <br/>
										[EMPFNAME] <br/>
										[EMPLNAME] <br/>
										[USERNAME] <br/>
										[PASSWORD] <br/>
										[LEGAL_ENTITY_NAME] <br/>
									</div>
								</td>
							</tr>
							
						</table>
					</div>
                </div>
                <!-- /.box-body -->
            </div>	 --%>

		<div class="box box-primary collapsed-box"
			style="border-top-color: #EEEEEE; margin-top: 10px;">

			<div class="box-header with-border">
				<h3 class="box-title" style="font-size: 14px; padding-right: 10px;">
					Password Changed Notification
					<%-- <%=uF.showData(hmNotificationStatus.get("NOT_"+IConstants.N_UPD_PASSWORD), "") %> --%>
					<%
						strNotId = "" + IConstants.N_UPD_PASSWORD;
							isMail = (Boolean) request.getAttribute("isEmailPasswordChanged");
							strEImage = isMail ? "mail_enbl.png" : "mail_disbl.png";
							strEEnable = isMail ? "Disable Mail Notification" : "Enable Mail Notification";

							if (isMail) {
								isMail = false;
							} else {
								isMail = true;
							}
					%>
					<span id="myDivE<%=strNotId%>" style="float: right"> <img
						width="20px" src="images1/<%=strEImage%>"
						title="<%=strEEnable%>"
						onclick="updateEmailStatus('myDivE<%=strNotId%>',<%=strNotId%>,<%=isMail%>,'isEmailPasswordChanged');" />
					</span>
				</h3>
				<div class="box-tools pull-right">
					<button class="btn btn-box-tool" data-widget="collapse">
						<i class="fa fa-plus"></i>
					</button>
					<button class="btn btn-box-tool" data-widget="remove">
						<i class="fa fa-times"></i>
					</button>
				</div>
			</div>
			<!-- /.box-header -->
			<div class="box-body"
				style="padding: 5px; overflow-y: auto; display: none;">
				<div class="content1" style="margin: 0px 0px 10px 0px">
					<table class="table table_no_border" cellspacing="2"
						cellpadding="2" width="100%">
						<tr>
							<td valign="top" class="txtlabel alignRight">Email
								Notification:</td>
							<td><s:checkbox name="isEmailPasswordChanged"
									id="isEmailPasswordChanged" /><span class="hint">Please
									select the email notification.<span class="hint-pointer">&nbsp;</span>
							</span></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Subject:</td>
							<td><s:textfield cssStyle="width:480px"
									name="strSubjectPasswordChanged" /><span class="hint">Please
									enter the email Subject.<span class="hint-pointer">&nbsp;</span>
							</span></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Body:</td>
							<td><div style="float: left; margin-right: 20px;">
									<s:textarea name="strBodyPasswordChanged" cols="63" rows="10" />
									<span class="hint">Please enter the email body.<span
										class="hint-pointer">&nbsp;</span></span>
								</div>
								<div style="float: left;">
									[LOGIN_LINK] <br /> [EMPCODE] <br /> [EMPFNAME] <br />
									[EMPLNAME] <br /> [USERNAME] <br /> [NEW_PASSWORD] <br />
									[PASSWORD] <br /> [LEGAL_ENTITY_NAME] <br />
								</div></td>
						</tr>

					</table>
				</div>
			</div>
			<!-- /.box-body -->
		</div>
		<div class="box box-primary collapsed-box"
			style="border-top-color: #EEEEEE; margin-top: 10px;">

			<div class="box-header with-border">
				<h3 class="box-title" style="font-size: 14px; padding-right: 10px;">
					Password Reset Notification
					<%-- <%=uF.showData(hmNotificationStatus.get("NOT_"+IConstants.N_RESET_PASSWORD), "") %> --%>
					<%
						strNotId = "" + IConstants.N_RESET_PASSWORD;
							isMail = (Boolean) request.getAttribute("isEmailPasswordReset");
							strEImage = isMail ? "mail_enbl.png" : "mail_disbl.png";
							strEEnable = isMail ? "Disable Mail Notification" : "Enable Mail Notification";

							if (isMail) {
								isMail = false;
							} else {
								isMail = true;
							}
					%>
					<span id="myDivE<%=strNotId%>" style="float: right"> <img
						width="20px" src="images1/<%=strEImage%>"
						title="<%=strEEnable%>"
						onclick="updateEmailStatus('myDivE<%=strNotId%>',<%=strNotId%>,<%=isMail%>,'isEmailPasswordReset');" />
					</span>
				</h3>
				<div class="box-tools pull-right">
					<button class="btn btn-box-tool" data-widget="collapse">
						<i class="fa fa-plus"></i>
					</button>
					<button class="btn btn-box-tool" data-widget="remove">
						<i class="fa fa-times"></i>
					</button>
				</div>
			</div>
			<!-- /.box-header -->
			<div class="box-body"
				style="padding: 5px; overflow-y: auto; display: none;">
				<div class="content1" style="margin: 0px 0px 10px 0px">
					<table class="table table_no_border" cellspacing="2"
						cellpadding="2" width="100%">
						<tr>
							<td valign="top" class="txtlabel alignRight">Email
								Notification:</td>
							<td><s:checkbox name="isEmailPasswordReset"
									id="isEmailPasswordReset" /><span class="hint">Please
									select the email notification.<span class="hint-pointer">&nbsp;</span>
							</span></td>
						</tr>
						<%-- <tr>
								<td valign="top" class="txtlabel alignRight">Enable/Disable Text Notification:</td>
								<td><s:checkbox name="isTextPasswordReset"/><span class="hint">Please confirm the password by entering it once again.<span class="hint-pointer">&nbsp;</span></span></td>
							</tr>
							<tr>
								<td valign="top" class="txtlabel alignRight">Text Message</td>
								<td><s:textfield cssStyle="width:480px" name="strTextPasswordReset"/><span class="hint">Please confirm the password by entering it once again.<span class="hint-pointer">&nbsp;</span></span></td>
							</tr> --%>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Subject:</td>
							<td><s:textfield cssStyle="width:480px"
									name="strSubjectPasswordReset" /><span class="hint">Please
									enter the email Subject.<span class="hint-pointer">&nbsp;</span>
							</span></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Body:</td>
							<td><div style="float: left; margin-right: 20px;">
									<s:textarea name="strBodyPasswordReset" cols="63" rows="10" />
									<span class="hint">Please enter the email body.<span
										class="hint-pointer">&nbsp;</span></span>
								</div>
								<div style="float: left;">
									[LOGIN_LINK] <br /> [EMPCODE] <br /> [EMPFNAME] <br />
									[EMPLNAME] <br /> [USERNAME] <br /> [NEW_PASSWORD] <br />
									[PASSWORD] <br /> [LEGAL_ENTITY_NAME] <br />
								</div></td>
						</tr>
					</table>
				</div>
			</div>
			<!-- /.box-body -->
		</div>

		<div class="box box-primary collapsed-box"
			style="border-top-color: #EEEEEE; margin-top: 10px;">

			<div class="box-header with-border">
				<h3 class="box-title" style="font-size: 14px; padding-right: 10px;">
					Forgot Password Notification
					<%-- <%=uF.showData(hmNotificationStatus.get("NOT_"+IConstants.N_FORGOT_PASSWORD), "") %> --%>
					<%
						strNotId = "" + IConstants.N_FORGOT_PASSWORD;
							isMail = (Boolean) request.getAttribute("isEmailForgotPassword");
							strEImage = isMail ? "mail_enbl.png" : "mail_disbl.png";
							strEEnable = isMail ? "Disable Mail Notification" : "Enable Mail Notification";

							if (isMail) {
								isMail = false;
							} else {
								isMail = true;
							}
					%>
					<span id="myDivE<%=strNotId%>" style="float: right"> <img
						width="20px" src="images1/<%=strEImage%>"
						title="<%=strEEnable%>"
						onclick="updateEmailStatus('myDivE<%=strNotId%>',<%=strNotId%>,<%=isMail%>,'isEmailForgotPassword');" />
					</span>
				</h3>
				<div class="box-tools pull-right">
					<button class="btn btn-box-tool" data-widget="collapse">
						<i class="fa fa-plus"></i>
					</button>
					<button class="btn btn-box-tool" data-widget="remove">
						<i class="fa fa-times"></i>
					</button>
				</div>
			</div>
			<!-- /.box-header -->
			<div class="box-body"
				style="padding: 5px; overflow-y: auto; display: none;">
				<div class="content1" style="margin: 0px 0px 10px 0px">
					<table class="table table_no_border" cellspacing="2"
						cellpadding="2" width="100%">
						<tr>
							<td valign="top" class="txtlabel alignRight">Email
								Notification:</td>
							<td><s:checkbox name="isEmailForgotPassword"
									id="isEmailForgotPassword" /><span class="hint">Please
									select the email notification.<span class="hint-pointer">&nbsp;</span>
							</span></td>
						</tr>
						<%-- <tr>
							<td valign="top" class="txtlabel alignRight">Enable/Disable Text Notification:</td>
								<td><s:checkbox name="isTextForgotPassword"/><span class="hint">Please confirm the password by entering it once again.<span class="hint-pointer">&nbsp;</span></span></td>
							</tr>
							<tr>
								<td valign="top" class="txtlabel alignRight">Text Message</td>
								<td><s:textfield cssStyle="width:480px" name="strTextForgotPassword"/><span class="hint">Please confirm the password by entering it once again.<span class="hint-pointer">&nbsp;</span></span></td>
							</tr> --%>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Subject:</td>
							<td><s:textfield cssStyle="width:480px"
									name="strSubjectForgotPassword" /><span class="hint">Please
									enter the email Subject.<span class="hint-pointer">&nbsp;</span>
							</span></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Body:</td>
							<td><div style="float: left; margin-right: 20px;">
									<s:textarea name="strBodyForgotPassword" cols="63" rows="10" />
									<span class="hint">Please enter the email body.<span
										class="hint-pointer">&nbsp;</span></span>
								</div>
								<div style="float: left;">
									[LOGIN_LINK] <br /> [EMPCODE] <br /> [EMPFNAME] <br />
									[EMPLNAME] <br /> [USERNAME] <br /> [PASSWORD] <br />
									[LEGAL_ENTITY_NAME] <br />
								</div></td>
						</tr>

					</table>
				</div>
			</div>
			<!-- /.box-body -->
		</div>


		<%-- <div class="box box-primary collapsed-box" style="border-top-color: #EEEEEE; margin-top: 10px;">
                <div class="box-header with-border">
                    <h3 class="box-title" style="font-size: 14px;padding-right: 10px;">
                    	New Requisition Request Notification 
					 	<%
							strNotId = ""+IConstants.N_EMPLOYEE_REQUISITION_REQUEST; 
							isMail = (Boolean) request.getAttribute("isEmailNewRequisitionRequest");
							strEImage = isMail ? "mail_enbl.png" : "mail_disbl.png";
							strEEnable = isMail ? "Disable Mail Notification" : "Enable Mail Notification" ;
							
							if(isMail){
								isMail = false;
							} else{
								isMail = true;
							}
						%>
						<span id="myDivE<%=strNotId%>" style="float:right">
							<img width="20px" src="images1/<%=strEImage %>" title="<%=strEEnable %>" onclick="updateEmailStatus('myDivE<%=strNotId%>',<%=strNotId%>,<%=isMail%>,'isEmailNewRequisitionRequest');"/>
						</span>
                    </h3>
                    <div class="box-tools pull-right">
                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
                    </div>
                </div>
                <!-- /.box-header -->
                <div class="box-body" style="padding: 5px; overflow-y: auto; display:none;">
                    <div class="content1" style="margin:0px 0px 10px 0px">
						<table class="table table_no_border" cellspacing="2" cellpadding="2" width="100%">
							<tr>
								<td valign="top" class="txtlabel alignRight">Email Notification:</td>
								<td><s:checkbox name="isEmailNewRequisitionRequest" id="isEmailNewRequisitionRequest"/><span class="hint">Please select the email notification.<span class="hint-pointer">&nbsp;</span></span></td>
							</tr>
							<tr>
								<td valign="top" class="txtlabel alignRight">Email Subject:</td>
								<td><s:textfield cssStyle="width:480px" name="strSubjectNewRequisitionRequest"/><span class="hint">Please enter the email Subject.<span class="hint-pointer">&nbsp;</span></span></td>
							</tr>
							<tr>
								<td valign="top" class="txtlabel alignRight">Email Body:</td>
								<td><div style="float: left; margin-right: 20px;"><s:textarea name="strBodyNewRequisitionRequest" cols="63" rows="10" /><span class="hint">Please enter the email body.<span class="hint-pointer">&nbsp;</span></span></div>
									<div style="float: left;">
										[LOGIN_LINK] <br/> 	
										[EMPCODE] <br/>
										[EMPFNAME] <br/>
										[EMPLNAME] <br/>
										[REQ_PURPOSE] <br/>
										[REQ_MODE] <br/>
										[REQ_TYPE] <br/>
										[REQ_FROM] <br/>
										[REQ_TO] <br/>
										[LEGAL_ENTITY_NAME] <br/>
									</div>
									 
								</td>
							</tr>
							
						</table>
					</div>
                </div>
                <!-- /.box-body -->
            </div>  --%>


		<div class="box box-primary collapsed-box"
			style="border-top-color: #EEEEEE; margin-top: 10px;">
			<div class="box-header with-border">
				<h3 class="box-title" style="font-size: 14px; padding-right: 10px;">
					New Employee Activity
					<%
					strNotId = "" + IConstants.N_NEW_ACTIVITY;
						isMail = (Boolean) request.getAttribute("isEmailNewActivity");
						strEImage = isMail ? "mail_enbl.png" : "mail_disbl.png";
						strEEnable = isMail ? "Disable Mail Notification" : "Enable Mail Notification";
						if (isMail) {
							isMail = false;
						} else {
							isMail = true;
						}
				%>
					<span id="myDivE<%=strNotId%>" style="float: right"> <img
						width="20px" src="images1/<%=strEImage%>"
						title="<%=strEEnable%>"
						onclick="updateEmailStatus('myDivE<%=strNotId%>',<%=strNotId%>,<%=isMail%>,'isEmailNewActivity');" />
					</span>
				</h3>
				<div class="box-tools pull-right">
					<button class="btn btn-box-tool" data-widget="collapse">
						<i class="fa fa-plus"></i>
					</button>
					<button class="btn btn-box-tool" data-widget="remove">
						<i class="fa fa-times"></i>
					</button>
				</div>
			</div>
			<div class="box-body"
				style="padding: 5px; overflow-y: auto; display: none;">
				<div class="content1" style="margin: 0px 0px 10px 0px">
					<table class="table table_no_border" cellspacing="2"
						cellpadding="2" width="100%">
						<tr>
							<td valign="top" class="txtlabel alignRight">Email
								Notification:</td>
							<td><s:checkbox name="isEmailNewActivity"
									id="isEmailNewActivity" /><span class="hint">Please
									select the email notification.<span class="hint-pointer">&nbsp;</span>
							</span></td>
						</tr>
						<%-- <tr>
								<td valign="top" class="txtlabel alignRight">Enable/Disable Text Notification:</td>
								<td><s:checkbox name="isTextNewActivity"/><span class="hint">Please confirm the password by entering it once again.<span class="hint-pointer">&nbsp;</span></span></td>
							</tr>
							<tr>
								<td valign="top" class="txtlabel alignRight">Text Message</td>
								<td><s:textfield cssStyle="width:526px" name="strTextNewActivity"/><span class="hint">Please confirm the password by entering it once again.<span class="hint-pointer">&nbsp;</span></span></td>
							</tr> --%>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Subject:</td>
							<td><s:textfield cssStyle="width:526px"
									name="strSubjectNewActivity" /><span class="hint">Please
									enter the email Subject.<span class="hint-pointer">&nbsp;</span>
							</span></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Body:</td>
							<td><div style="float: left; margin-right: 20px;">
									<s:textarea name="strBodyNewActivity" cols="63" rows="10" />
									<span class="hint">Please enter the email body.<span
										class="hint-pointer">&nbsp;</span></span>
								</div>
								<div style="float: left;">
									[EMPFNAME] <br /> [EMPLNAME] <br /> [ACTIVITY_NAME] <br />
									[LEGAL_ENTITY_NAME] <br />
								</div></td>
						</tr>
					</table>
				</div>
			</div>
		</div>


		<div class="box box-primary collapsed-box"
			style="border-top-color: #EEEEEE; margin-top: 10px;">
			<div class="box-header with-border">
				<h3 class="box-title" style="font-size: 14px; padding-right: 10px;">
					Employee Resignation Request
					<%
					strNotId = "" + IConstants.N_EMPLOYEE_RESIGNATION_REQUEST;
						isMail = (Boolean) request.getAttribute("isEmailEmployeeResignationRequest");
						strEImage = isMail ? "mail_enbl.png" : "mail_disbl.png";
						strEEnable = isMail ? "Disable Mail Notification" : "Enable Mail Notification";
						if (isMail) {
							isMail = false;
						} else {
							isMail = true;
						}
				%>
					<span id="myDivE<%=strNotId%>" style="float: right"> <img
						width="20px" src="images1/<%=strEImage%>"
						title="<%=strEEnable%>"
						onclick="updateEmailStatus('myDivE<%=strNotId%>',<%=strNotId%>,<%=isMail%>,'isEmailEmployeeResignationRequest');" />
					</span>
				</h3>
				<div class="box-tools pull-right">
					<button class="btn btn-box-tool" data-widget="collapse">
						<i class="fa fa-plus"></i>
					</button>
					<button class="btn btn-box-tool" data-widget="remove">
						<i class="fa fa-times"></i>
					</button>
				</div>
			</div>
			<div class="box-body"
				style="padding: 5px; overflow-y: auto; display: none;">
				<div class="content1" style="margin: 0px 0px 10px 0px">
					<table class="table table_no_border" cellspacing="2"
						cellpadding="2" width="100%">
						<tr>
							<td valign="top" class="txtlabel alignRight">Email
								Notification:</td>
							<td><s:checkbox name="isEmailEmployeeResignationRequest"
									id="isEmailEmployeeResignationRequest" /><span class="hint">Please
									select the email notification.<span class="hint-pointer">&nbsp;</span>
							</span></td>
						</tr>
						<%-- <tr>
								<td valign="top" class="txtlabel alignRight">Enable/Disable Text Notification:</td>
								<td><s:checkbox name="isTextEmployeeResignationRequest"/><span class="hint">Please confirm the password by entering it once again.<span class="hint-pointer">&nbsp;</span></span></td>
							</tr>
							<tr>
								<td valign="top" class="txtlabel alignRight">Text Message</td>
								<td><s:textfield cssStyle="width:526px" name="strTextEmployeeResignationRequest"/><span class="hint">Please confirm the password by entering it once again.<span class="hint-pointer">&nbsp;</span></span></td>
							</tr> --%>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Subject:</td>
							<td><s:textfield cssStyle="width:526px"
									name="strSubjectEmployeeResignationRequest" /><span
								class="hint">Please enter the email Subject.<span
									class="hint-pointer">&nbsp;</span></span></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Body:</td>
							<td><div style="float: left; margin-right: 20px;">
									<s:textarea name="strBodyEmployeeResignationRequest" cols="63"
										rows="10" />
									<span class="hint">Please enter the email body.<span
										class="hint-pointer">&nbsp;</span></span>
								</div>
								<div style="float: left;">
									[EMPCODE] <br /> [EMPFNAME] <br /> [EMPLNAME] <br /> [MGRNAME]
									<br /> [RESIGNATION_DATE] <br /> [RESIGNATION_REASON] <br />
									[DESIGNATION] <br />
									<!-- [ACTIVITY_NAME] <br/> -->
									[LEGAL_ENTITY_NAME] <br />
								</div></td>
						</tr>
					</table>
				</div>
			</div>
		</div>


		<div class="box box-primary collapsed-box"
			style="border-top-color: #EEEEEE; margin-top: 10px;">
			<div class="box-header with-border">
				<h3 class="box-title" style="font-size: 14px; padding-right: 10px;">
					Employee Resignation Approval
					<%
					strNotId = "" + IConstants.N_EMPLOYEE_RESIGNATION_APPROVAL;
						isMail = (Boolean) request.getAttribute("isEmailEmployeeResignationApproval");
						strEImage = isMail ? "mail_enbl.png" : "mail_disbl.png";
						strEEnable = isMail ? "Disable Mail Notification" : "Enable Mail Notification";
						if (isMail) {
							isMail = false;
						} else {
							isMail = true;
						}
				%>
					<span id="myDivE<%=strNotId%>" style="float: right"> <img
						width="20px" src="images1/<%=strEImage%>"
						title="<%=strEEnable%>"
						onclick="updateEmailStatus('myDivE<%=strNotId%>',<%=strNotId%>,<%=isMail%>,'isEmailEmployeeResignationApproval');" />
					</span>
				</h3>
				<div class="box-tools pull-right">
					<button class="btn btn-box-tool" data-widget="collapse">
						<i class="fa fa-plus"></i>
					</button>
					<button class="btn btn-box-tool" data-widget="remove">
						<i class="fa fa-times"></i>
					</button>
				</div>
			</div>
			<div class="box-body"
				style="padding: 5px; overflow-y: auto; display: none;">
				<div class="content1" style="margin: 0px 0px 10px 0px">
					<table class="table table_no_border" cellspacing="2"
						cellpadding="2" width="100%">
						<tr>
							<td valign="top" class="txtlabel alignRight">Email
								Notification:</td>
							<td><s:checkbox name="isEmailEmployeeResignationApproval"
									id="isEmailEmployeeResignationApproval" /><span class="hint">Please
									select the email notification.<span class="hint-pointer">&nbsp;</span>
							</span></td>
						</tr>
						<%-- <tr>
								<td valign="top" class="txtlabel alignRight">Enable/Disable Text Notification:</td>
								<td><s:checkbox name="isTextEmployeeResignationApproval"/><span class="hint">Please confirm the password by entering it once again.<span class="hint-pointer">&nbsp;</span></span></td>
							</tr>
							<tr>
								<td valign="top" class="txtlabel alignRight">Text Message</td>
								<td><s:textfield cssStyle="width:526px" name="strTextEmployeeResignationApproval"/><span class="hint">Please confirm the password by entering it once again.<span class="hint-pointer">&nbsp;</span></span></td>
							</tr> --%>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Subject:</td>
							<td><s:textfield cssStyle="width:526px"
									name="strSubjectEmployeeResignationApproval" /><span
								class="hint">Please enter the email Subject.<span
									class="hint-pointer">&nbsp;</span></span></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Body:</td>
							<td><div style="float: left; margin-right: 20px;">
									<s:textarea name="strBodyEmployeeResignationApproval" cols="63"
										rows="10" />
									<span class="hint">Please enter the email body.<span
										class="hint-pointer">&nbsp;</span></span>
								</div>
								<div style="float: left;">
									[EMPCODE] <br /> [EMPFNAME] <br /> [EMPLNAME] <br /> [MGRNAME]
									<br /> [MGR_COMMENT] <br /> [APPR_DENY] <br />
									[LEGAL_ENTITY_NAME] <br />
								</div></td>
						</tr>
					</table>
				</div>
			</div>
		</div>
		
		
	<!-- ===start parvez date: 26-08-2022=== -->
		<div class="box box-primary collapsed-box"
			style="border-top-color: #EEEEEE; margin-top: 10px;">

			<div class="box-header with-border">
				<h3 class="box-title" style="font-size: 14px; padding-right: 10px;">
					Employee Onboarded by Self
					<%
					strNotId = "" + IConstants.N_EMPLOYEE_ONBOARDED_BY_SELF;
								isMail = (Boolean) request.getAttribute("isEmailEmpOnboardedBySelf");
								//System.out.println("isMail() : "+isMail);
								strEImage = isMail ? "mail_enbl.png" : "mail_disbl.png";	
								strEEnable = isMail ? "Disable Mail Notification" : "Enable Mail Notification";
								if (isMail) {
									isMail = false;
								} else {
									isMail = true;
								}
				%>
					<span id="myDivE<%=strNotId%>" style="float: right"> <img
						width="20px" src="images1/<%=strEImage%>"
						title="<%=strEEnable%>"
						onclick="updateEmailStatus('myDivE<%=strNotId%>',<%=strNotId%>,<%=isMail%>,'isEmailEmpOnboardedBySelf');" />
					</span>
				</h3>
				<div class="box-tools pull-right">
					<button class="btn btn-box-tool" data-widget="collapse">
						<i class="fa fa-plus"></i>
					</button>
					<button class="btn btn-box-tool" data-widget="remove">
						<i class="fa fa-times"></i>
					</button>
				</div>
			</div>
			<!-- /.box-header -->
			<div class="box-body"
				style="padding: 5px; overflow-y: auto; display: none;">
				<div class="content1" style="margin: 0px 0px 10px 0px">
					<table border="0" cellspacing="2" cellpadding="2" width="100%">
						<tr>
							<td valign="top" class="txtlabel alignRight">Email
								Notification:</td>
							<td><s:checkbox name="isEmailEmpOnboardedBySelf"
									id="isEmailEmpOnboardedBySelf" /></td>
						</tr>
						<%-- <tr>
						<td valign="top" class="txtlabel alignRight">Enable/Disable Text Notification:</td>
						<td><s:checkbox name="isTextCandiAssessmentForm"/></td>
					</tr>
					<tr>
						<td valign="top" class="txtlabel alignRight">Text Message</td>
						<td><s:textfield cssStyle="width:526px" name="strTextCandiAssessmentForm"/></td>
					</tr> --%>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Subject:</td>
							<td><s:textfield cssStyle="width:526px"
									name="strSubjectEmpOnboardedBySelf" /></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Body:</td>
							<td><div style="float: left; margin-right: 20px;">
									<s:textarea name="strBodyEmpOnboardedBySelf" cols="63" rows="10" />
								</div>
								<div style="float: left;">
									[EMPFNAME] <br /> 
									[EMPLNAME] <br/>
									[HRNAME] <br/>
									[DATE] <br/>
									[LEGAL_ENTITY_NAME] <br/>
									
								</div></td>
						</tr>
					</table>
				</div>
			</div>
			<!-- /.box-body -->
		</div>
	<!-- ===end parvez date: 26-08-2022=== -->

		<!-- -------------------------- Close People and Users --------------------------- -->



		<!-- -------------------------- Auto-Mail on Event --------------------------- -->
		<div
			style="width: 100%; text-align: center; font-size: 14px; font-weight: bold; margin: 7px;"
			class="">Events</div>
		<div class="box box-primary collapsed-box"
			style="border-top-color: #EEEEEE; margin-top: 10px;">
			<div class="box-header with-border">
				<h3 class="box-title" style="font-size: 14px; padding-right: 10px;">
					Employee Birthday
					<%
					strNotId = "" + IConstants.N_EMPLOYEE_BIRTHDAY;
						isMail = (Boolean) request.getAttribute("isEmailBirthday");
						strEImage = isMail ? "mail_enbl.png" : "mail_disbl.png";
						strEEnable = isMail ? "Disable Mail Notification" : "Enable Mail Notification";
						if (isMail) {
							isMail = false;
						} else {
							isMail = true;
						}
				%>
					<span id="myDivE<%=strNotId%>" style="float: right"> <img
						width="20px" src="images1/<%=strEImage%>"
						title="<%=strEEnable%>"
						onclick="updateEmailStatus('myDivE<%=strNotId%>',<%=strNotId%>,<%=isMail%>,'isEmailBirthday');" />
					</span>
				</h3>
				<div class="box-tools pull-right">
					<button class="btn btn-box-tool" data-widget="collapse">
						<i class="fa fa-plus"></i>
					</button>
					<button class="btn btn-box-tool" data-widget="remove">
						<i class="fa fa-times"></i>
					</button>
				</div>
			</div>
			<div class="box-body"
				style="padding: 5px; overflow-y: auto; display: none;">
				<div class="content1" style="margin: 0px 0px 10px 0px">
					<table class="table table_no_border" cellspacing="2"
						cellpadding="2" width="100%">
						<tr>
							<td valign="top" class="txtlabel alignRight">Email
								Notification:</td>
							<td><s:checkbox name="isEmailBirthday" id="isEmailBirthday" /><span
								class="hint">Please select the email notification.<span
									class="hint-pointer">&nbsp;</span></span></td>
						</tr>
						<%-- <tr>
								<td valign="top" class="txtlabel alignRight">Enable/Disable Text Notification:</td>
								<td><s:checkbox name="isTextBirthday"/><span class="hint">Please confirm the password by entering it once again.<span class="hint-pointer">&nbsp;</span></span></td>
							</tr>
							<tr>
								<td valign="top" class="txtlabel alignRight">Text Message</td>
								<td><s:textfield cssStyle="width:526px" name="strTextBirthday"/><span class="hint">Please confirm the password by entering it once again.<span class="hint-pointer">&nbsp;</span></span></td>
							</tr> --%>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Subject:</td>
							<td><s:textfield cssStyle="width:526px"
									name="strSubjectBirthday" /><span class="hint">Please
									enter the email Subject.<span class="hint-pointer">&nbsp;</span>
							</span></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Body:</td>
							<td><div style="float: left; margin-right: 20px;">
									<s:textarea name="strBodyBirthday" id="editor1" cols="63"
										rows="10" />
									<span class="hint">Please enter the email body.<span
										class="hint-pointer">&nbsp;</span></span>
								</div>
								<div style="float: left;">
									[EMPCODE] <br /> [EMPFNAME] <br /> [EMPLNAME] <br />
									[RECIPIENT_NAME] <br /> [LEGAL_ENTITY_NAME] <br />
								</div></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Backgroud
								Image:</td>
							<td><input type="hidden"
								name="strBackgroundBirthdayImageStatus"
								id="strBackgroundBirthdayImageStatus" value="0"></input> <input
								type="file" name="strBackgroundBirthdayImage"
								id="strBackgroundBirthdayImage"
								accept=".jpg,.png,.svg,.svgz,.doc,.docs,.docx,.pdf"
								onchange="fillFileStatus('strBackgroundBirthdayImageStatus')" />

								<span class="hint">Please select the email background
									image.<span class="hint-pointer">&nbsp;</span>
							</span></td>
						</tr>

					</table>
				</div>
			</div>
		</div>


		<div class="box box-primary collapsed-box"
			style="border-top-color: #EEEEEE; margin-top: 10px;">
			<div class="box-header with-border">
				<h3 class="box-title" style="font-size: 14px; padding-right: 10px;">
					Employee Marriage Anniversary
					<%
					strNotId = "" + IConstants.N_EMPLOYEE_MARRIAGE_ANNIVERSARY;
						isMail = (Boolean) request.getAttribute("isEmailMarriageAnniversary");
						strEImage = isMail ? "mail_enbl.png" : "mail_disbl.png";
						strEEnable = isMail ? "Disable Mail Notification" : "Enable Mail Notification";
						if (isMail) {
							isMail = false;
						} else {
							isMail = true;
						}
				%>
					<span id="myDivE<%=strNotId%>" style="float: right"> <img
						width="20px" src="images1/<%=strEImage%>"
						title="<%=strEEnable%>"
						onclick="updateEmailStatus('myDivE<%=strNotId%>',<%=strNotId%>,<%=isMail%>,'isEmailMarriageAnniversary');" />
					</span>
				</h3>
				<div class="box-tools pull-right">
					<button class="btn btn-box-tool" data-widget="collapse">
						<i class="fa fa-plus"></i>
					</button>
					<button class="btn btn-box-tool" data-widget="remove">
						<i class="fa fa-times"></i>
					</button>
				</div>
			</div>
			<div class="box-body"
				style="padding: 5px; overflow-y: auto; display: none;">
				<div class="content1" style="margin: 0px 0px 10px 0px">
					<table class="table table_no_border" cellspacing="2"
						cellpadding="2" width="100%">
						<tr>
							<td valign="top" class="txtlabel alignRight">Email
								Notification:</td>
							<td><s:checkbox name="isEmailMarriageAnniversary"
									id="isEmailMarriageAnniversary" /><span class="hint">Please
									select the email notification.<span class="hint-pointer">&nbsp;</span>
							</span></td>
						</tr>
						<%-- <tr>
								<td valign="top" class="txtlabel alignRight">Enable/Disable Text Notification:</td>
								<td><s:checkbox name="isTextMarriageAnniversary"/><span class="hint">Please confirm the password by entering it once again.<span class="hint-pointer">&nbsp;</span></span></td>
							</tr>
							<tr>
								<td valign="top" class="txtlabel alignRight">Text Message</td>
								<td><s:textfield cssStyle="width:526px" name="strTextMarriageAnniversary"/><span class="hint">Please confirm the password by entering it once again.<span class="hint-pointer">&nbsp;</span></span></td>
							</tr> --%>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Subject:</td>
							<td><s:textfield cssStyle="width:526px"
									name="strSubjectMarriageAnniversary" /><span class="hint">Please
									enter the email Subject.<span class="hint-pointer">&nbsp;</span>
							</span></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Body:</td>
							<td><div style="float: left; margin-right: 20px;">
									<s:textarea name="strBodyMarriageAnniversary" id="editor2"
										cols="63" rows="10" />
									<span class="hint">Please enter the email body.<span
										class="hint-pointer">&nbsp;</span></span>
								</div>
								<div style="float: left;">
									[EMPCODE] <br /> [EMPFNAME] <br /> [EMPLNAME] <br />
									[RECIPIENT_NAME] <br /> [LEGAL_ENTITY_NAME] <br />
								</div></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Backgroud
								Image:</td>
							<td><input type="hidden"
								name="strBackgroundMarriageImageStatus"
								id="strBackgroundMarriageImageStatus" value="0"></input> <input
								type="file" name="strBackgroundMarriageImage"
								id="strBackgroundMarriageImage"
								accept=".jpg,.png,.svg,.svgz,.doc,.docs,.docx,.pdf"
								onchange="fillFileStatus('strBackgroundMarriageImageStatus')" />

								<span class="hint">Please select the email background
									image.<span class="hint-pointer">&nbsp;</span>
							</span></td>
						</tr>
					</table>
				</div>
			</div>
		</div>


		<div class="box box-primary collapsed-box"
			style="border-top-color: #EEEEEE; margin-top: 10px;">
			<div class="box-header with-border">
				<h3 class="box-title" style="font-size: 14px; padding-right: 10px;">
					Employee Work Anniversary
					<%
					strNotId = "" + IConstants.N_EMPLOYEE_WORK_ANNIVERSARY;
						isMail = (Boolean) request.getAttribute("isEmailWorkAnniversary");
						strEImage = isMail ? "mail_enbl.png" : "mail_disbl.png";
						strEEnable = isMail ? "Disable Mail Notification" : "Enable Mail Notification";
						if (isMail) {
							isMail = false;
						} else {
							isMail = true;
						}
				%>
					<span id="myDivE<%=strNotId%>" style="float: right"> <img
						width="20px" src="images1/<%=strEImage%>"
						title="<%=strEEnable%>"
						onclick="updateEmailStatus('myDivE<%=strNotId%>',<%=strNotId%>,<%=isMail%>,'isEmailWorkAnniversary');" />
					</span>
				</h3>
				<div class="box-tools pull-right">
					<button class="btn btn-box-tool" data-widget="collapse">
						<i class="fa fa-plus"></i>
					</button>
					<button class="btn btn-box-tool" data-widget="remove">
						<i class="fa fa-times"></i>
					</button>
				</div>
			</div>
			<div class="box-body"
				style="padding: 5px; overflow-y: auto; display: none;">
				<div class="content1" style="margin: 0px 0px 10px 0px">
					<table class="table table_no_border" cellspacing="2"
						cellpadding="2" width="100%">
						<tr>
							<td valign="top" class="txtlabel alignRight">Email
								Notification:</td>
							<td><s:checkbox name="isEmailWorkAnniversary"
									id="isEmailWorkAnniversary" /><span class="hint">Please
									select the email notification.<span class="hint-pointer">&nbsp;</span>
							</span></td>
						</tr>
						<%-- <tr>
								<td valign="top" class="txtlabel alignRight">Enable/Disable Text Notification:</td>
								<td><s:checkbox name="isTextWorkAnniversary"/><span class="hint">Please confirm the password by entering it once again.<span class="hint-pointer">&nbsp;</span></span></td>
							</tr>
							<tr>
								<td valign="top" class="txtlabel alignRight">Text Message</td>
								<td><s:textfield cssStyle="width:526px" name="strTextWorkAnniversary"/><span class="hint">Please confirm the password by entering it once again.<span class="hint-pointer">&nbsp;</span></span></td>
							</tr> --%>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Subject:</td>
							<td><s:textfield cssStyle="width:526px"
									name="strSubjectWorkAnniversary" /><span class="hint">Please
									enter the email Subject.<span class="hint-pointer">&nbsp;</span>
							</span></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Body:</td>
							<td><div style="float: left; margin-right: 20px;">
									<s:textarea name="strBodyWorkAnniversary" id="editor3"
										cols="63" rows="10" />
									<span class="hint">Please enter the email body.<span
										class="hint-pointer">&nbsp;</span></span>
								</div>
								<div style="float: left;">
									[EMPCODE] <br /> [EMPFNAME] <br /> [EMPLNAME] <br />
									[RECIPIENT_NAME] <br /> [JOINING_DATE] <br />
									[NO_OF_YEARS_WORKING] <br /> [LEGAL_ENTITY_NAME] <br />
								</div></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Backgroud
								Image:</td>
							<td><input type="hidden" name="strBackgroundWorkImageStatus"
								id="strBackgroundWorkImageStatus" value="0"></input> <input
								type="file" name="strBackgroundWorkImage"
								id="strBackgroundWorkImage"
								accept=".jpg,.png,.svg,.svgz,.doc,.docs,.docx,.pdf"
								onchange="fillFileStatus('strBackgroundWorkImageStatus')" /> <span
								class="hint">Please select the email background image.<span
									class="hint-pointer">&nbsp;</span></span></td>
						</tr>
					</table>
				</div>
			</div>
		</div>

		<div class="box box-primary collapsed-box"
			style="border-top-color: #EEEEEE; margin-top: 10px;">
			<div class="box-header with-border">
				<h3 class="box-title" style="font-size: 14px; padding-right: 10px;">
					New Event
					<%
					strNotId = "" + IConstants.N_ORG_EVENT;
						//System.out.println("isEmailWorkAnniversary::" + request.getAttribute("isEmailWorkAnniversary"));
						isMail = (Boolean) request.getAttribute("isEmailWorkAnniversary");
						strEImage = isMail ? "mail_enbl.png" : "mail_disbl.png";
						strEEnable = isMail ? "Disable Mail Notification" : "Enable Mail Notification";
						if (isMail) {
							isMail = false;
						} else {
							isMail = true;
						}
				%>
					<span id="myDivE<%=strNotId%>" style="float: right"> <img
						width="20px" src="images1/<%=strEImage%>"
						title="<%=strEEnable%>"
						onclick="updateEmailStatus('myDivE<%=strNotId%>',<%=strNotId%>,<%=isMail%>,'isEmailWorkAnniversary');" />
					</span>
				</h3>
				<div class="box-tools pull-right">
					<button class="btn btn-box-tool" data-widget="collapse">
						<i class="fa fa-plus"></i>
					</button>
					<button class="btn btn-box-tool" data-widget="remove">
						<i class="fa fa-times"></i>
					</button>
				</div>
			</div>
			<div class="box-body"
				style="padding: 5px; overflow-y: auto; display: none;">
				<div class="content1" style="margin: 0px 0px 10px 0px">
					<table class="table table_no_border" cellspacing="2"
						cellpadding="2" width="100%">
						<tr>
							<td valign="top" class="txtlabel alignRight">Email
								Notification:</td>
							<td><s:checkbox name="isEmailOnEventCreation"
									id="isEmailOnEventCreation" /><span class="hint">Please
									select the email notification.<span class="hint-pointer">&nbsp;</span>
							</span></td>
						</tr>
						<%-- <tr>
								<td valign="top" class="txtlabel alignRight">Enable/Disable Text Notification:</td>
								<td><s:checkbox name="isTextWorkAnniversary"/><span class="hint">Please confirm the password by entering it once again.<span class="hint-pointer">&nbsp;</span></span></td>
							</tr>
							<tr>
								<td valign="top" class="txtlabel alignRight">Text Message</td>
								<td><s:textfield cssStyle="width:526px" name="strTextWorkAnniversary"/><span class="hint">Please confirm the password by entering it once again.<span class="hint-pointer">&nbsp;</span></span></td>
							</tr> --%>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Subject:</td>
							<td><s:textfield cssStyle="width:526px"
									name="strSubjectOnEventCreation" /><span class="hint">Please
									enter the email Subject.<span class="hint-pointer">&nbsp;</span>
							</span></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Body:</td>
							<td><div style="float: left; margin-right: 20px;">
									<s:textarea name="strBodyOnEventCreation" cols="63" rows="10" />
									<span class="hint">Please enter the email body.<span
										class="hint-pointer">&nbsp;</span></span>
								</div>
								<div style="float: left;">
									[EMPFNAME] [EMPLAME] <br /> [EVENT] <br /> [EVENT_DATE] <br />
									[EVENT_TIME] <br /> [LOCATION] <br /> [DEPARTMENT]<br />
								</div></td>
						</tr>
						<!--  <tr>
							<td valign="top" class="txtlabel alignRight">Backgroud Image:</td>
							<td>
								<input type="hidden" name="strBackgroundImageStatus" id="strBackgroundImageStatus" value="0"></input>
							
								<input type="file" name="strBackgroundImage" id="strBackgroundImage" accept=".jpg,.png,.svg,.svgz,.doc,.docs,.docx,.pdf"  onchange="fillFileStatus('strBackgroundImageStatus')"/>
								
								<span class="hint">Please select  the email background image.<span class="hint-pointer">&nbsp;</span></span>
							</td>
							</tr>-->
					</table>
				</div>
			</div>
		</div>

		<div class="box box-primary collapsed-box"
			style="border-top-color: #EEEEEE; margin-top: 10px;">
			<div class="box-header with-border">
				<h3 class="box-title" style="font-size: 14px; padding-right: 10px;">
					New Announcement
					<%
					strNotId = "" + IConstants.N_ORG_EVENT;
						//System.out.println("isEmailWorkAnniversary::" + request.getAttribute("isEmailWorkAnniversary"));
						//System.out.println("isEmailOnEventCreation::" + request.getAttribute("isEmailOnEventCreation"));

						isMail = (Boolean) request.getAttribute("isEmailWorkAnniversary");
						strEImage = isMail ? "mail_enbl.png" : "mail_disbl.png";
						strEEnable = isMail ? "Disable Mail Notification" : "Enable Mail Notification";
						if (isMail) {
							isMail = false;
						} else {
							isMail = true;
						}
				%>
					<span id="myDivE<%=strNotId%>" style="float: right"> <img
						width="20px" src="images1/<%=strEImage%>"
						title="<%=strEEnable%>"
						onclick="updateEmailStatus('myDivE<%=strNotId%>',<%=strNotId%>,<%=isMail%>,'isEmailWorkAnniversary');" />
					</span>
				</h3>
				<div class="box-tools pull-right">
					<button class="btn btn-box-tool" data-widget="collapse">
						<i class="fa fa-plus"></i>
					</button>
					<button class="btn btn-box-tool" data-widget="remove">
						<i class="fa fa-times"></i>
					</button>
				</div>
			</div>
			<div class="box-body"
				style="padding: 5px; overflow-y: auto; display: none;">
				<div class="content1" style="margin: 0px 0px 10px 0px">
					<table class="table table_no_border" cellspacing="2"
						cellpadding="2" width="100%">
						<tr>
							<td valign="top" class="txtlabel alignRight">Email
								Notification:</td>
							<td><s:checkbox name="isEmailOnAnnouncementCreation"
									id="isEmailOnAnnouncementCreation" /><span class="hint">Please
									select the email notification.<span class="hint-pointer">&nbsp;</span>
							</span></td>
						</tr>
						<%-- <tr>
								<td valign="top" class="txtlabel alignRight">Enable/Disable Text Notification:</td>
								<td><s:checkbox name="isTextWorkAnniversary"/><span class="hint">Please confirm the password by entering it once again.<span class="hint-pointer">&nbsp;</span></span></td>
							</tr>
							<tr>
								<td valign="top" class="txtlabel alignRight">Text Message</td>
								<td><s:textfield cssStyle="width:526px" name="strTextWorkAnniversary"/><span class="hint">Please confirm the password by entering it once again.<span class="hint-pointer">&nbsp;</span></span></td>
							</tr> --%>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Subject:</td>
							<td><s:textfield cssStyle="width:526px"
									name="strSubjectOnAnnouncementCreation" /><span class="hint">Please
									enter the email Subject.<span class="hint-pointer">&nbsp;</span>
							</span></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Body:</td>
							<td><div style="float: left; margin-right: 20px;">
									<s:textarea name="strBodyOnAnnouncementCreation" cols="63"
										rows="10" />
									<span class="hint">Please enter the email body.<span
										class="hint-pointer">&nbsp;</span></span>
								</div>
								<div style="float: left;">
									[ANNOUNCEMENT] <br /> [ADDED BY] <br />


								</div></td>
						</tr>
					</table>
				</div>
			</div>
		</div>







		<!-- -------------------------- Close Auto-Mail on Event --------------------------- -->


		<%
			if (PRODUCTTYPE != null && PRODUCTTYPE.equals("2")) {
		%>
		<!-- -------------------------- Absence --------------------------- -->
		<div
			style="width: 100%; text-align: center; font-size: 14px; font-weight: bold; margin: 7px;"
			class="">Absence</div>

		<%-- <div class="box box-primary collapsed-box" style="border-top-color: #EEEEEE; margin-top: 10px;">
                <div class="box-header with-border">
                    <h3 class="box-title" style="font-size: 14px;padding-right: 10px;">
                    	Employee New Leave Request Notification 
					<%
							strNotId = ""+IConstants.N_EMPLOYEE_LEAVE_REQUEST; 
							isMail = (Boolean) request.getAttribute("isEmailNewEmployeeLeaveRequest");
							strEImage = isMail ? "mail_enbl.png" : "mail_disbl.png";
							strEEnable = isMail ? "Disable Mail Notification" : "Enable Mail Notification" ;
							
							if(isMail){
								isMail = false;
							} else{
								isMail = true;
							}
						%>
						<span id="myDivE<%=strNotId%>" style="float:right">
							<img width="20px" src="images1/<%=strEImage %>" title="<%=strEEnable %>" onclick="updateEmailStatus('myDivE<%=strNotId%>',<%=strNotId%>,<%=isMail%>,'isEmailNewEmployeeLeaveRequest');"/>
						</span>
                    </h3>
                    <div class="box-tools pull-right">
                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
                    </div>
                </div>
                <!-- /.box-header -->
                <div class="box-body" style="padding: 5px; overflow-y: auto; display:none;">
                    <div class="content1" style="margin:0px 0px 10px 0px">
					<table border="0" cellspacing="2" cellpadding="2" width="100%">
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Notification:</td>
							<td><s:checkbox name="isEmailNewEmployeeLeaveRequest" id="isEmailNewEmployeeLeaveRequest"/><span class="hint">Please select the email notification.<span class="hint-pointer">&nbsp;</span></span></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Subject:</td>
							<td><s:textfield cssStyle="width:526px" name="strSubjectNewEmployeeLeaveRequest"/><span class="hint">Please enter the email Subject.<span class="hint-pointer">&nbsp;</span></span></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Body:</td>
							<td><div style="float: left; margin-right: 20px;"><s:textarea name="strBodyNewEmployeeLeaveRequest" cols="63" rows="10" /><span class="hint">Please enter the email body.<span class="hint-pointer">&nbsp;</span></span></div>
								<div style="float: left;">
									[LOGIN_LINK] <br/> 	
									[EMPCODE] <br/>
									[EMPFNAME] <br/>
									[EMPLNAME] <br/>
									[LEAVE_FROM] <br/>
									[LEAVE_TO] <br/>
									[LEAVE_NO_DAYS] <br/>
									[LEAVE_REASON] <br/>
									[LEGAL_ENTITY_NAME] <br/>
								</div>
								 
							</td>
						</tr>
						
					</table>
				</div>
                </div>
                <!-- /.box-body --> 
			</div> --%>


		<div class="box box-primary collapsed-box"
			style="border-top-color: #EEEEEE; margin-top: 10px;">
			<div class="box-header with-border">
				<h3 class="box-title" style="font-size: 14px; padding-right: 10px;">
					Manager New Leave Request
					<%
					strNotId = "" + IConstants.N_MANAGER_LEAVE_REQUEST;
							isMail = (Boolean) request.getAttribute("isEmailManagerNewLeaveRequest");
							strEImage = isMail ? "mail_enbl.png" : "mail_disbl.png";
							strEEnable = isMail ? "Disable Mail Notification" : "Enable Mail Notification";

							if (isMail) {
								isMail = false;
							} else {
								isMail = true;
							}
				%>
					<span id="myDivE<%=strNotId%>" style="float: right"> <img
						width="20px" src="images1/<%=strEImage%>"
						title="<%=strEEnable%>"
						onclick="updateEmailStatus('myDivE<%=strNotId%>',<%=strNotId%>,<%=isMail%>,'isEmailManagerNewLeaveRequest');" />
					</span>
				</h3>
				<div class="box-tools pull-right">
					<button class="btn btn-box-tool" data-widget="collapse">
						<i class="fa fa-plus"></i>
					</button>
					<button class="btn btn-box-tool" data-widget="remove">
						<i class="fa fa-times"></i>
					</button>
				</div>
			</div>
			<!-- /.box-header -->
			<div class="box-body"
				style="padding: 5px; overflow-y: auto; display: none;">
				<div class="content1" style="margin: 0px 0px 10px 0px">
					<table class="table" cellspacing="2" cellpadding="2" width="100%">
						<tr>
							<td valign="top" class="txtlabel alignRight">Email
								Notification:</td>
							<td><s:checkbox name="isEmailManagerNewLeaveRequest"
									id="isEmailManagerNewLeaveRequest" /><span class="hint">Please
									select the email notification.<span class="hint-pointer">&nbsp;</span>
							</span></td>
						</tr>
						<%-- <tr>
							<td valign="top" class="txtlabel alignRight">Enable/Disable Text Notification:</td>
							<td><s:checkbox name="isTextManagerNewLeaveRequest"/><span class="hint">Please confirm the password by entering it once again.<span class="hint-pointer">&nbsp;</span></span></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Text Message</td>
							<td><s:textfield cssStyle="width:480px" name="strTextManagerNewLeaveRequest"/><span class="hint">Please confirm the password by entering it once again.<span class="hint-pointer">&nbsp;</span></span></td>
						</tr> --%>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Subject:</td>
							<td><s:textfield cssStyle="width:480px"
									name="strSubjectManagerNewLeaveRequest" /><span class="hint">Please
									enter the email Subject.<span class="hint-pointer">&nbsp;</span>
							</span></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Body:</td>
							<td><div style="float: left; margin-right: 20px;">
									<s:textarea name="strBodyManagerNewLeaveRequest" cols="63"
										rows="10" />
									<span class="hint">Please enter the email body.<span
										class="hint-pointer">&nbsp;</span></span>
								</div>
								<div style="float: left;">
									<!-- [LOGIN_LINK] <br/> -->
									[EMPCODE] <br /> [MGRNAME]<br /> [EMPFNAME] <br /> [EMPLNAME] <br />
									[LEAVE_TYPE] <br /> [LEAVE_FROM] <br /> [LEAVE_TO] <br />
									[LEAVE_NO_DAYS] <br /> [LEAVE_REASON] <br />
								<!-- ===start parvez date:18-03-2023=== -->	 
									<%if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_BACKUP_EMPLOYEE_FOR_LEAVE))){ %>
										[BACKUP_EMP_LEAVE] <br />
									<%} %>
								<!-- ===end parvez date: 18-03-2023=== -->
									[LEGAL_ENTITY_NAME] <br />
								</div></td>
						</tr>

					</table>
				</div>
			</div>
			<!-- /.box-body -->
		</div>


		<div class="box box-primary collapsed-box"
			style="border-top-color: #EEEEEE; margin-top: 10px;">
			<div class="box-header with-border">
				<h3 class="box-title" style="font-size: 14px; padding-right: 10px;">
					Manager New Extra Working Request
					<%
					strNotId = "" + IConstants.N_MANAGER_EXTRA_WORK_REQUEST;
							isMail = (Boolean) request.getAttribute("isEmailManagerNewExtraWorkRequest");
							strEImage = isMail ? "mail_enbl.png" : "mail_disbl.png";
							strEEnable = isMail ? "Disable Mail Notification" : "Enable Mail Notification";

							if (isMail) {
								isMail = false;
							} else {
								isMail = true;
							}
				%>
					<span id="myDivE<%=strNotId%>" style="float: right"> <img
						width="20px" src="images1/<%=strEImage%>"
						title="<%=strEEnable%>"
						onclick="updateEmailStatus('myDivE<%=strNotId%>',<%=strNotId%>,<%=isMail%>,'isEmailManagerNewExtraWorkRequest');" />
					</span>
				</h3>
				<div class="box-tools pull-right">
					<button class="btn btn-box-tool" data-widget="collapse">
						<i class="fa fa-plus"></i>
					</button>
					<button class="btn btn-box-tool" data-widget="remove">
						<i class="fa fa-times"></i>
					</button>
				</div>
			</div>
			<!-- /.box-header -->
			<div class="box-body"
				style="padding: 5px; overflow-y: auto; display: none;">
				<div class="content1" style="margin: 0px 0px 10px 0px">
					<table class="table table_no_border" cellspacing="2"
						cellpadding="2" width="100%">
						<tr>
							<td valign="top" class="txtlabel alignRight">Email
								Notification:</td>
							<td><s:checkbox name="isEmailManagerNewExtraWorkRequest"
									id="isEmailManagerNewExtraWorkRequest" /><span class="hint">Please
									select the email notification.<span class="hint-pointer">&nbsp;</span>
							</span></td>
						</tr>
						<%-- <tr>
							<td valign="top" class="txtlabel alignRight">Enable/Disable Text Notification:</td>
							<td><s:checkbox name="isTextManagerNewLeaveRequest"/><span class="hint">Please confirm the password by entering it once again.<span class="hint-pointer">&nbsp;</span></span></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Text Message</td>
							<td><s:textfield cssStyle="width:480px" name="strTextManagerNewLeaveRequest"/><span class="hint">Please confirm the password by entering it once again.<span class="hint-pointer">&nbsp;</span></span></td>
						</tr> --%>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Subject:</td>
							<td><s:textfield cssStyle="width:480px"
									name="strSubjectManagerNewExtraWorkRequest" /><span
								class="hint">Please enter the email Subject.<span
									class="hint-pointer">&nbsp;</span></span></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Body:</td>
							<td><div style="float: left; margin-right: 20px;">
									<s:textarea name="strBodyManagerNewExtraWorkRequest" cols="63"
										rows="10" />
									<span class="hint">Please enter the email body.<span
										class="hint-pointer">&nbsp;</span></span>
								</div>
								<div style="float: left;">
									<!-- [LOGIN_LINK] <br/> -->
									[EMPCODE] <br /> [MGRNAME]<br /> [EMPFNAME] <br /> [EMPLNAME] <br />
									[LEAVE_FROM] <br /> [LEAVE_TO] <br /> [LEAVE_NO_DAYS] <br />
									[LEAVE_REASON] <br /> [LEGAL_ENTITY_NAME] <br />
								</div></td>
						</tr>

					</table>
				</div>
			</div>
			<!-- /.box-body -->
		</div>


		<div class="box box-primary collapsed-box"
			style="border-top-color: #EEEEEE; margin-top: 10px;">
			<div class="box-header with-border">
				<h3 class="box-title" style="font-size: 14px; padding-right: 10px;">
					Manager New Travel Request
					<%
					strNotId = "" + IConstants.N_MANAGER_TRAVEL_REQUEST;
							isMail = (Boolean) request.getAttribute("isEmailManagerNewTravelRequest");
							strEImage = isMail ? "mail_enbl.png" : "mail_disbl.png";
							strEEnable = isMail ? "Disable Mail Notification" : "Enable Mail Notification";

							if (isMail) {
								isMail = false;
							} else {
								isMail = true;
							}
				%>
					<span id="myDivE<%=strNotId%>" style="float: right"> <img
						width="20px" src="images1/<%=strEImage%>"
						title="<%=strEEnable%>"
						onclick="updateEmailStatus('myDivE<%=strNotId%>',<%=strNotId%>,<%=isMail%>,'isEmailManagerNewTravelRequest');" />
					</span>
				</h3>
				<div class="box-tools pull-right">
					<button class="btn btn-box-tool" data-widget="collapse">
						<i class="fa fa-plus"></i>
					</button>
					<button class="btn btn-box-tool" data-widget="remove">
						<i class="fa fa-times"></i>
					</button>
				</div>
			</div>
			<!-- /.box-header -->
			<div class="box-body"
				style="padding: 5px; overflow-y: auto; display: none;">
				<div class="content1" style="margin: 0px 0px 10px 0px">
					<table class="table table_no_border" cellspacing="2"
						cellpadding="2" width="100%">
						<tr>
							<td valign="top" class="txtlabel alignRight">Email
								Notification:</td>
							<td><s:checkbox name="isEmailManagerNewTravelRequest"
									id="isEmailManagerNewTravelRequest" /><span class="hint">Please
									select the email notification.<span class="hint-pointer">&nbsp;</span>
							</span></td>
						</tr>
						<%-- <tr>
							<td valign="top" class="txtlabel alignRight">Enable/Disable Text Notification:</td>
							<td><s:checkbox name="isTextManagerNewLeaveRequest"/><span class="hint">Please confirm the password by entering it once again.<span class="hint-pointer">&nbsp;</span></span></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Text Message</td>
							<td><s:textfield cssStyle="width:480px" name="strTextManagerNewLeaveRequest"/><span class="hint">Please confirm the password by entering it once again.<span class="hint-pointer">&nbsp;</span></span></td>
						</tr> --%>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Subject:</td>
							<td><s:textfield cssStyle="width:480px"
									name="strSubjectManagerNewTravelRequest" /><span class="hint">Please
									enter the email Subject.<span class="hint-pointer">&nbsp;</span>
							</span></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Body:</td>
							<td><div style="float: left; margin-right: 20px;">
									<s:textarea name="strBodyManagerNewTravelRequest" cols="63"
										rows="10" />
									<span class="hint">Please enter the email body.<span
										class="hint-pointer">&nbsp;</span></span>
								</div>
								<div style="float: left;">
									<!-- [LOGIN_LINK] <br/> -->
									[EMPCODE] <br /> [MGRNAME]<br /> [EMPFNAME] <br /> [EMPLNAME] <br />
									[LEAVE_FROM] <br /> [LEAVE_TO] <br /> [LEAVE_NO_DAYS] <br />
									[LEAVE_REASON] <br /> [LEGAL_ENTITY_NAME] <br />
								</div></td>
						</tr>

					</table>
				</div>
			</div>
			<!-- /.box-body -->
		</div>



		<div class="box box-primary collapsed-box"
			style="border-top-color: #EEEEEE; margin-top: 10px;">
			<div class="box-header with-border">
				<h3 class="box-title" style="font-size: 14px; padding-right: 10px;">
					Leave Approved Alert
					<%
					strNotId = "" + IConstants.N_EMPLOYEE_LEAVE_APPROVAL;
							isMail = (Boolean) request.getAttribute("isEmailNewEmployeeLeaveApproval");
							strEImage = isMail ? "mail_enbl.png" : "mail_disbl.png";
							strEEnable = isMail ? "Disable Mail Notification" : "Enable Mail Notification";

							if (isMail) {
								isMail = false;
							} else {
								isMail = true;
							}
				%>
					<span id="myDivE<%=strNotId%>" style="float: right"> <img
						width="20px" src="images1/<%=strEImage%>"
						title="<%=strEEnable%>"
						onclick="updateEmailStatus('myDivE<%=strNotId%>',<%=strNotId%>,<%=isMail%>,'isEmailNewEmployeeLeaveApproval');" />
					</span>
				</h3>
				<div class="box-tools pull-right">
					<button class="btn btn-box-tool" data-widget="collapse">
						<i class="fa fa-plus"></i>
					</button>
					<button class="btn btn-box-tool" data-widget="remove">
						<i class="fa fa-times"></i>
					</button>
				</div>
			</div>
			<!-- /.box-header -->
			<div class="box-body"
				style="padding: 5px; overflow-y: auto; display: none;">
				<div class="content1" style="margin: 0px 0px 10px 0px">
					<table class="table table_no_border" cellspacing="2"
						cellpadding="2" width="100%">
						<tr>
							<td valign="top" class="txtlabel alignRight">Email
								Notification:</td>
							<td><s:checkbox name="isEmailNewEmployeeLeaveApproval"
									id="isEmailNewEmployeeLeaveApproval" /><span class="hint">Please
									select the email notification.<span class="hint-pointer">&nbsp;</span>
							</span></td>
						</tr>
						<%-- <tr>
							<td valign="top" class="txtlabel alignRight">Enable/Disable Text Notification:</td>
							<td><s:checkbox name="isTextNewEmployeeLeaveApproval"/><span class="hint">Please confirm the password by entering it once again.<span class="hint-pointer">&nbsp;</span></span></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Text Message</td>
							<td><s:textfield cssStyle="width:480px" name="strTextNewEmployeeLeaveApproval"/><span class="hint">Please confirm the password by entering it once again.<span class="hint-pointer">&nbsp;</span></span></td>
						</tr> --%>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Subject:</td>
							<td><s:textfield cssStyle="width:480px"
									name="strSubjectNewEmployeeLeaveApproval" /><span class="hint">Please
									enter the email Subject.<span class="hint-pointer">&nbsp;</span>
							</span></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Body:</td>
							<td><div style="float: left; margin-right: 20px;">
									<s:textarea name="strBodyNewEmployeeLeaveApproval" cols="63"
										rows="10" />
									<span class="hint">Please enter the email body.<span
										class="hint-pointer">&nbsp;</span></span>
								</div>
								<div style="float: left;">
									[LOGIN_LINK] <br /> [EMPCODE] <br /> [MGRNAME]<br /> [EMPFNAME]
									<br /> [EMPLNAME] <br /> [LEAVE_TYPE] <br /> [LEAVE_FROM] <br />
									[LEAVE_TO] <br /> [LEAVE_NO_DAYS] <br /> [LEAVE_REASON] <br />
									[MGR_COMMENT] <br /> [APPR_DENY] <br /> 
								<!-- ===start parvez date:18-03-2023=== -->	
									<%if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_BACKUP_EMPLOYEE_FOR_LEAVE))){ %>
										[BACKUP_EMP_LEAVE] <br />
									<%} %>
									[LEGAL_ENTITY_NAME] <br />
								<!-- ===end parvez date: 18-03-2023=== -->	
								</div></td>
						</tr>

					</table>
				</div>
			</div>
			<!-- /.box-body -->
		</div>


		<div class="box box-primary collapsed-box"
			style="border-top-color: #EEEEEE; margin-top: 10px;">
			<div class="box-header with-border">
				<h3 class="box-title" style="font-size: 14px; padding-right: 10px;">
					Extra Working Approved Alert
					<%
					strNotId = "" + IConstants.N_EMPLOYEE_EXTRA_WORK_APPROVAL;
							isMail = (Boolean) request.getAttribute("isEmailNewEmployeeExtraWorkApproval");
							strEImage = isMail ? "mail_enbl.png" : "mail_disbl.png";
							strEEnable = isMail ? "Disable Mail Notification" : "Enable Mail Notification";

							if (isMail) {
								isMail = false;
							} else {
								isMail = true;
							}
				%>
					<span id="myDivE<%=strNotId%>" style="float: right"> <img
						width="20px" src="images1/<%=strEImage%>"
						title="<%=strEEnable%>"
						onclick="updateEmailStatus('myDivE<%=strNotId%>',<%=strNotId%>,<%=isMail%>,'isEmailNewEmployeeExtraWorkApproval');" />
					</span>
				</h3>
				<div class="box-tools pull-right">
					<button class="btn btn-box-tool" data-widget="collapse">
						<i class="fa fa-plus"></i>
					</button>
					<button class="btn btn-box-tool" data-widget="remove">
						<i class="fa fa-times"></i>
					</button>
				</div>
			</div>
			<!-- /.box-header -->
			<div class="box-body"
				style="padding: 5px; overflow-y: auto; display: none;">
				<div class="content1" style="margin: 0px 0px 10px 0px">
					<table class="table table_no_border" cellspacing="2"
						cellpadding="2" width="100%">
						<tr>
							<td valign="top" class="txtlabel alignRight">Email
								Notification:</td>
							<td><s:checkbox name="isEmailNewEmployeeExtraWorkApproval"
									id="isEmailNewEmployeeExtraWorkApproval" /><span class="hint">Please
									select the email notification.<span class="hint-pointer">&nbsp;</span>
							</span></td>
						</tr>
						<%-- <tr>
							<td valign="top" class="txtlabel alignRight">Enable/Disable Text Notification:</td>
							<td><s:checkbox name="isTextNewEmployeeLeaveApproval"/><span class="hint">Please confirm the password by entering it once again.<span class="hint-pointer">&nbsp;</span></span></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Text Message</td>
							<td><s:textfield cssStyle="width:480px" name="strTextNewEmployeeLeaveApproval"/><span class="hint">Please confirm the password by entering it once again.<span class="hint-pointer">&nbsp;</span></span></td>
						</tr> --%>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Subject:</td>
							<td><s:textfield cssStyle="width:480px"
									name="strSubjectNewEmployeeExtraWorkApproval" /><span
								class="hint">Please enter the email Subject.<span
									class="hint-pointer">&nbsp;</span></span></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Body:</td>
							<td><div style="float: left; margin-right: 20px;">
									<s:textarea name="strBodyNewEmployeeExtraWorkApproval"
										cols="63" rows="10" />
									<span class="hint">Please enter the email body.<span
										class="hint-pointer">&nbsp;</span></span>
								</div>
								<div style="float: left;">
									[LOGIN_LINK] <br /> [EMPCODE] <br /> [MGRNAME]<br /> [EMPFNAME]
									<br /> [EMPLNAME] <br /> [LEAVE_FROM] <br /> [LEAVE_TO] <br />
									[LEAVE_NO_DAYS] <br /> [LEAVE_REASON] <br /> [MGR_COMMENT] <br />
									[APPR_DENY] <br /> [LEGAL_ENTITY_NAME] <br />
								</div></td>
						</tr>

					</table>
				</div>
			</div>
			<!-- /.box-body -->
		</div>


		<div class="box box-primary collapsed-box"
			style="border-top-color: #EEEEEE; margin-top: 10px;">
			<div class="box-header with-border">
				<h3 class="box-title" style="font-size: 14px; padding-right: 10px;">
					Travel Approved Alert
					<%
					strNotId = "" + IConstants.N_EMPLOYEE_TRAVEL_APPROVAL;
							isMail = (Boolean) request.getAttribute("isEmailNewEmployeeTravelApproval");
							strEImage = isMail ? "mail_enbl.png" : "mail_disbl.png";
							strEEnable = isMail ? "Disable Mail Notification" : "Enable Mail Notification";

							if (isMail) {
								isMail = false;
							} else {
								isMail = true;
							}
				%>
					<span id="myDivE<%=strNotId%>" style="float: right"> <img
						width="20px" src="images1/<%=strEImage%>"
						title="<%=strEEnable%>"
						onclick="updateEmailStatus('myDivE<%=strNotId%>',<%=strNotId%>,<%=isMail%>,'isEmailNewEmployeeTravelApproval');" />
					</span>
				</h3>
				<div class="box-tools pull-right">
					<button class="btn btn-box-tool" data-widget="collapse">
						<i class="fa fa-plus"></i>
					</button>
					<button class="btn btn-box-tool" data-widget="remove">
						<i class="fa fa-times"></i>
					</button>
				</div>
			</div>
			<!-- /.box-header -->
			<div class="box-body"
				style="padding: 5px; overflow-y: auto; display: none;">
				<div class="content1" style="margin: 0px 0px 10px 0px">
					<table class="table table_no_border" cellspacing="2"
						cellpadding="2" width="100%">
						<tr>
							<td valign="top" class="txtlabel alignRight">Email
								Notification:</td>
							<td><s:checkbox name="isEmailNewEmployeeTravelApproval"
									id="isEmailNewEmployeeTravelApproval" /><span class="hint">Please
									select the email notification.<span class="hint-pointer">&nbsp;</span>
							</span></td>
						</tr>
						<%-- <tr>
							<td valign="top" class="txtlabel alignRight">Enable/Disable Text Notification:</td>
							<td><s:checkbox name="isTextNewEmployeeLeaveApproval"/><span class="hint">Please confirm the password by entering it once again.<span class="hint-pointer">&nbsp;</span></span></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Text Message</td>
							<td><s:textfield cssStyle="width:480px" name="strTextNewEmployeeLeaveApproval"/><span class="hint">Please confirm the password by entering it once again.<span class="hint-pointer">&nbsp;</span></span></td>
						</tr> --%>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Subject:</td>
							<td><s:textfield cssStyle="width:480px"
									name="strSubjectNewEmployeeTravelApproval" /><span class="hint">Please
									enter the email Subject.<span class="hint-pointer">&nbsp;</span>
							</span></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Body:</td>
							<td><div style="float: left; margin-right: 20px;">
									<s:textarea name="strBodyNewEmployeeTravelApproval" cols="63"
										rows="10" />
									<span class="hint">Please enter the email body.<span
										class="hint-pointer">&nbsp;</span></span>
								</div>
								<div style="float: left;">
									[LOGIN_LINK] <br /> [EMPCODE] <br /> [MGRNAME]<br /> [EMPFNAME]
									<br /> [EMPLNAME] <br /> [LEAVE_FROM] <br /> [LEAVE_TO] <br />
									[LEAVE_NO_DAYS] <br /> [LEAVE_REASON] <br /> [MGR_COMMENT] <br />
									[APPR_DENY] <br /> [LEGAL_ENTITY_NAME] <br />
								</div></td>
						</tr>

					</table>
				</div>
			</div>
			<!-- /.box-body -->
		</div>


		<div class="box box-primary collapsed-box"
			style="border-top-color: #EEEEEE; margin-top: 10px;">
			<div class="box-header with-border">
				<h3 class="box-title" style="font-size: 14px; padding-right: 10px;">
					Leave Canceled Alert
					<%
					strNotId = "" + IConstants.N_EMPLOYEE_LEAVE_CANCEL;
							isMail = (Boolean) request.getAttribute("isEmailEmployeeLeaveCancel");
							strEImage = isMail ? "mail_enbl.png" : "mail_disbl.png";
							strEEnable = isMail ? "Disable Mail Notification" : "Enable Mail Notification";

							if (isMail) {
								isMail = false;
							} else {
								isMail = true;
							}
				%>
					<span id="myDivE<%=strNotId%>" style="float: right"> <img
						width="20px" src="images1/<%=strEImage%>"
						title="<%=strEEnable%>"
						onclick="updateEmailStatus('myDivE<%=strNotId%>',<%=strNotId%>,<%=isMail%>,'isEmailEmployeeLeaveCancel');" />
					</span>
				</h3>
				<div class="box-tools pull-right">
					<button class="btn btn-box-tool" data-widget="collapse">
						<i class="fa fa-plus"></i>
					</button>
					<button class="btn btn-box-tool" data-widget="remove">
						<i class="fa fa-times"></i>
					</button>
				</div>
			</div>
			<!-- /.box-header -->
			<div class="box-body"
				style="padding: 5px; overflow-y: auto; display: none;">
				<div class="content1" style="margin: 0px 0px 10px 0px">
					<table class="table table_no_border" cellspacing="2"
						cellpadding="2" width="100%">
						<tr>
							<td valign="top" class="txtlabel alignRight">Email
								Notification:</td>
							<td><s:checkbox name="isEmailEmployeeLeaveCancel"
									id="isEmailEmployeeLeaveCancel" /><span class="hint">Please
									select the email notification.<span class="hint-pointer">&nbsp;</span>
							</span></td>
						</tr>
						<%-- <tr>
							<td valign="top" class="txtlabel alignRight">Enable/Disable Text Notification:</td>
							<td><s:checkbox name="isTextEmployeeLeaveCancel"/></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Text Message</td>
							<td><s:textfield cssStyle="width:526px" name="strTextEmployeeLeaveCancel"/></td>
						</tr> --%>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Subject</td>
							<td><s:textfield cssStyle="width:526px"
									name="strSubjectEmployeeLeaveCancel" /><span class="hint">Please
									enter the email Subject.<span class="hint-pointer">&nbsp;</span>
							</span></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Body</td>
							<td><div style="float: left; margin-right: 20px;">
									<s:textarea name="strBodyEmployeeLeaveCancel" cols="77"
										rows="10" />
									<span class="hint">Please enter the email body.<span
										class="hint-pointer">&nbsp;</span></span>
								</div>
								<div style="float: left;">
									[EMPCODE] <br /> [MGRNAME]<br /> [EMPFNAME] <br /> [EMPLNAME] <br />
									[LEAVE_TYPE] <br /> [LEAVE_FROM] <br /> [LEAVE_TO] <br />
									[LEAVE_NO_DAYS] <br /> [MGR_COMMENT] <br /> [LEGAL_ENTITY_NAME]
									<br />
								</div></td>
						</tr>
					</table>
				</div>
			</div>
			<!-- /.box-body -->
		</div>


		<div class="box box-primary collapsed-box"
			style="border-top-color: #EEEEEE; margin-top: 10px;">
			<div class="box-header with-border">
				<h3 class="box-title" style="font-size: 14px; padding-right: 10px;">
					Extra Working Canceled Alert
					<%
					strNotId = "" + IConstants.N_EMPLOYEE_EXTRA_WORK_CANCEL;
							isMail = (Boolean) request.getAttribute("isEmailEmployeeExtraWorkCancel");
							strEImage = isMail ? "mail_enbl.png" : "mail_disbl.png";
							strEEnable = isMail ? "Disable Mail Notification" : "Enable Mail Notification";

							if (isMail) {
								isMail = false;
							} else {
								isMail = true;
							}
				%>
					<span id="myDivE<%=strNotId%>" style="float: right"> <img
						width="20px" src="images1/<%=strEImage%>"
						title="<%=strEEnable%>"
						onclick="updateEmailStatus('myDivE<%=strNotId%>',<%=strNotId%>,<%=isMail%>,'isEmailEmployeeExtraWorkCancel');" />
					</span>
				</h3>
				<div class="box-tools pull-right">
					<button class="btn btn-box-tool" data-widget="collapse">
						<i class="fa fa-plus"></i>
					</button>
					<button class="btn btn-box-tool" data-widget="remove">
						<i class="fa fa-times"></i>
					</button>
				</div>
			</div>
			<!-- /.box-header -->
			<div class="box-body"
				style="padding: 5px; overflow-y: auto; display: none;">
				<div class="content1" style="margin: 0px 0px 10px 0px">
					<table class="table table_no_border" cellspacing="2"
						cellpadding="2" width="100%">
						<tr>
							<td valign="top" class="txtlabel alignRight">Email
								Notification:</td>
							<td><s:checkbox name="isEmailEmployeeExtraWorkCancel"
									id="isEmailEmployeeExtraWorkCancel" /><span class="hint">Please
									select the email notification.<span class="hint-pointer">&nbsp;</span>
							</span></td>
						</tr>
						<%-- <tr>
							<td valign="top" class="txtlabel alignRight">Enable/Disable Text Notification:</td>
							<td><s:checkbox name="isTextEmployeeExtraWorkCancel"/></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Text Message</td>
							<td><s:textfield cssStyle="width:526px" name="strTextEmployeeExtraWorkCancel"/></td>
						</tr> --%>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Subject</td>
							<td><s:textfield cssStyle="width:526px"
									name="strSubjectEmployeeExtraWorkCancel" /><span class="hint">Please
									enter the email Subject.<span class="hint-pointer">&nbsp;</span>
							</span></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Body</td>
							<td><div style="float: left; margin-right: 20px;">
									<s:textarea name="strBodyEmployeeExtraWorkCancel" cols="77"
										rows="10" />
									<span class="hint">Please enter the email body.<span
										class="hint-pointer">&nbsp;</span></span>
								</div>
								<div style="float: left;">
									[EMPCODE] <br /> [MGRNAME]<br /> [EMPFNAME] <br /> [EMPLNAME] <br />
									[LEAVE_TYPE] <br /> [LEAVE_FROM] <br /> [LEAVE_TO] <br />
									[LEAVE_NO_DAYS] <br /> [MGR_COMMENT] <br /> [LEGAL_ENTITY_NAME]
									<br />
								</div></td>
						</tr>
					</table>
				</div>
			</div>
			<!-- /.box-body -->
		</div>


		<div class="box box-primary collapsed-box"
			style="border-top-color: #EEEEEE; margin-top: 10px;">
			<div class="box-header with-border">
				<h3 class="box-title" style="font-size: 14px; padding-right: 10px;">
					Travel Canceled Alert
					<%
					strNotId = "" + IConstants.N_EMPLOYEE_TRAVEL_CANCEL;
							isMail = (Boolean) request.getAttribute("isEmailEmployeeTravelCancel");
							strEImage = isMail ? "mail_enbl.png" : "mail_disbl.png";
							strEEnable = isMail ? "Disable Mail Notification" : "Enable Mail Notification";

							if (isMail) {
								isMail = false;
							} else {
								isMail = true;
							}
				%>
					<span id="myDivE<%=strNotId%>" style="float: right"> <img
						width="20px" src="images1/<%=strEImage%>"
						title="<%=strEEnable%>"
						onclick="updateEmailStatus('myDivE<%=strNotId%>',<%=strNotId%>,<%=isMail%>,'isEmailEmployeeTravelCancel');" />
					</span>
				</h3>
				<div class="box-tools pull-right">
					<button class="btn btn-box-tool" data-widget="collapse">
						<i class="fa fa-plus"></i>
					</button>
					<button class="btn btn-box-tool" data-widget="remove">
						<i class="fa fa-times"></i>
					</button>
				</div>
			</div>
			<!-- /.box-header -->
			<div class="box-body"
				style="padding: 5px; overflow-y: auto; display: none;">
				<div class="content1" style="margin: 0px 0px 10px 0px">
					<table class="table table_no_border" cellspacing="2"
						cellpadding="2" width="100%">
						<tr>
							<td valign="top" class="txtlabel alignRight">Email
								Notification:</td>
							<td><s:checkbox name="isEmailEmployeeTravelCancel"
									id="isEmailEmployeeTravelCancel" /><span class="hint">Please
									select the email notification.<span class="hint-pointer">&nbsp;</span>
							</span></td>
						</tr>
						<%-- <tr>
							<td valign="top" class="txtlabel alignRight">Enable/Disable Text Notification:</td>
							<td><s:checkbox name="isTextEmployeeTravelCancel"/></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Text Message</td>
							<td><s:textfield cssStyle="width:526px" name="strTextEmployeeTravelCancel"/></td>
						</tr> --%>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Subject</td>
							<td><s:textfield cssStyle="width:526px"
									name="strSubjectEmployeeTravelCancel" /><span class="hint">Please
									enter the email Subject.<span class="hint-pointer">&nbsp;</span>
							</span></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Body</td>
							<td><div style="float: left; margin-right: 20px;">
									<s:textarea name="strBodyEmployeeTravelCancel" cols="77"
										rows="10" />
									<span class="hint">Please enter the email body.<span
										class="hint-pointer">&nbsp;</span></span>
								</div>
								<div style="float: left;">
									[EMPCODE] <br /> [MGRNAME]<br /> [EMPFNAME] <br /> [EMPLNAME] <br />
									[LEAVE_TYPE] <br /> [LEAVE_FROM] <br /> [LEAVE_TO] <br />
									[LEAVE_NO_DAYS] <br /> [MGR_COMMENT] <br /> [LEGAL_ENTITY_NAME]
									<br />
								</div></td>
						</tr>
					</table>
				</div>
			</div>
			<!-- /.box-body -->
		</div>


		<div class="box box-primary collapsed-box"
			style="border-top-color: #EEEEEE; margin-top: 10px;">
			<div class="box-header with-border">
				<h3 class="box-title" style="font-size: 14px; padding-right: 10px;">
					Leave Pullout
					<%
					strNotId = "" + IConstants.N_EMPLOYEE_LEAVE_PULLOUT;
							isMail = (Boolean) request.getAttribute("isEmailEmployeeLeavePullout");
							strEImage = isMail ? "mail_enbl.png" : "mail_disbl.png";
							strEEnable = isMail ? "Disable Mail Notification" : "Enable Mail Notification";
							if (isMail) {
								isMail = false;
							} else {
								isMail = true;
							}
				%>
					<span id="myDivE<%=strNotId%>" style="float: right"> <img
						width="20px" src="images1/<%=strEImage%>"
						title="<%=strEEnable%>"
						onclick="updateEmailStatus('myDivE<%=strNotId%>',<%=strNotId%>,<%=isMail%>,'isEmailEmployeeLeavePullout');" />
					</span>
				</h3>
				<div class="box-tools pull-right">
					<button class="btn btn-box-tool" data-widget="collapse">
						<i class="fa fa-plus"></i>
					</button>
					<button class="btn btn-box-tool" data-widget="remove">
						<i class="fa fa-times"></i>
					</button>
				</div>
			</div>
			<!-- /.box-header -->
			<div class="box-body"
				style="padding: 5px; overflow-y: auto; display: none;">
				<div class="content1" style="margin: 0px 0px 10px 0px">
					<table class="table table_no_border" cellspacing="2"
						cellpadding="2" width="100%">
						<tr>
							<td valign="top" class="txtlabel alignRight">Email
								Notification:</td>
							<td><s:checkbox cssStyle="width:0px"
									name="isEmailEmployeeLeavePullout"
									id="isEmailEmployeeLeavePullout" /><span class="hint">Please
									select the email notification.<span class="hint-pointer">&nbsp;</span>
							</span></td>
						</tr>
						<%-- <tr>
							<td valign="top" class="txtlabel alignRight">Enable/Disable Text Notification:</td>
							<td><s:checkbox name="isTextEmployeeLeaveCancel"/></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Text Message</td>
							<td><s:textfield cssStyle="width:526px" name="strTextEmployeeLeaveCancel"/></td>
						</tr> --%>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Subject</td>
							<td><s:textfield cssStyle="width:526px"
									name="strSubjectEmployeeLeavePullout" /><span class="hint">Please
									enter the email Subject.<span class="hint-pointer">&nbsp;</span>
							</span></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Body</td>
							<td><div style="float: left; margin-right: 20px;">
									<s:textarea name="strBodyEmployeeLeavePullout" cols="77"
										rows="10" />
									<span class="hint">Please enter the email body.<span
										class="hint-pointer">&nbsp;</span></span>
								</div>
								<div style="float: left;">
									[EMPCODE] <br /> [MGRNAME]<br /> [EMPFNAME] <br /> [EMPLNAME] <br />
									[LEAVE_TYPE] <br /> [LEAVE_FROM] <br /> [LEAVE_TO] <br />
									[LEAVE_NO_DAYS] <br /> [LEGAL_ENTITY_NAME] <br />
								</div></td>
						</tr>
					</table>
				</div>
			</div>
			<!-- /.box-body -->
		</div>


		<div class="box box-primary collapsed-box"
			style="border-top-color: #EEEEEE; margin-top: 10px;">
			<div class="box-header with-border">
				<h3 class="box-title" style="font-size: 14px; padding-right: 10px;">
					Extra Working Pullout
					<%
					strNotId = "" + IConstants.N_EMPLOYEE_EXTRA_WORK_PULLOUT;
							isMail = (Boolean) request.getAttribute("isEmailEmployeeExtraWorkPullout");
							strEImage = isMail ? "mail_enbl.png" : "mail_disbl.png";
							strEEnable = isMail ? "Disable Mail Notification" : "Enable Mail Notification";
							if (isMail) {
								isMail = false;
							} else {
								isMail = true;
							}
				%>
					<span id="myDivE<%=strNotId%>" style="float: right"> <img
						width="20px" src="images1/<%=strEImage%>"
						title="<%=strEEnable%>"
						onclick="updateEmailStatus('myDivE<%=strNotId%>',<%=strNotId%>,<%=isMail%>,'isEmailEmployeeExtraWorkPullout');" />
					</span>
				</h3>
				<div class="box-tools pull-right">
					<button class="btn btn-box-tool" data-widget="collapse">
						<i class="fa fa-plus"></i>
					</button>
					<button class="btn btn-box-tool" data-widget="remove">
						<i class="fa fa-times"></i>
					</button>
				</div>
			</div>
			<!-- /.box-header -->
			<div class="box-body"
				style="padding: 5px; overflow-y: auto; display: none;">
				<div class="content1" style="margin: 0px 0px 10px 0px">
					<table class="table table_no_border" cellspacing="2"
						cellpadding="2" width="100%">
						<tr>
							<td valign="top" class="txtlabel alignRight">Email
								Notification:</td>
							<td><s:checkbox cssStyle="width:0px"
									name="isEmailEmployeeExtraWorkPullout"
									id="isEmailEmployeeExtraWorkPullout" /><span class="hint">Please
									select the email notification.<span class="hint-pointer">&nbsp;</span>
							</span></td>
						</tr>
						<%-- <tr>
							<td valign="top" class="txtlabel alignRight">Enable/Disable Text Notification:</td>
							<td><s:checkbox name="isTextEmployeeExtraWorkCancel"/></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Text Message</td>
							<td><s:textfield cssStyle="width:526px" name="strTextEmployeeExtraWorkCancel"/></td>
						</tr> --%>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Subject</td>
							<td><s:textfield cssStyle="width:526px"
									name="strSubjectEmployeeExtraWorkPullout" /><span class="hint">Please
									enter the email Subject.<span class="hint-pointer">&nbsp;</span>
							</span></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Body</td>
							<td><div style="float: left; margin-right: 20px;">
									<s:textarea name="strBodyEmployeeExtraWorkPullout" cols="77"
										rows="10" />
									<span class="hint">Please enter the email body.<span
										class="hint-pointer">&nbsp;</span></span>
								</div>
								<div style="float: left;">
									[EMPCODE] <br /> [MGRNAME]<br /> [EMPFNAME] <br /> [EMPLNAME] <br />
									[LEAVE_TYPE] <br /> [LEAVE_FROM] <br /> [LEAVE_TO] <br />
									[LEAVE_NO_DAYS] <br /> [LEGAL_ENTITY_NAME] <br />
								</div></td>
						</tr>
					</table>
				</div>
			</div>
			<!-- /.box-body -->
		</div>


		<div class="box box-primary collapsed-box"
			style="border-top-color: #EEEEEE; margin-top: 10px;">
			<div class="box-header with-border">
				<h3 class="box-title" style="font-size: 14px; padding-right: 10px;">
					Travel Pullout
					<%
					strNotId = "" + IConstants.N_EMPLOYEE_TRAVEL_PULLOUT;
							isMail = (Boolean) request.getAttribute("isEmailEmployeeTravelPullout");
							strEImage = isMail ? "mail_enbl.png" : "mail_disbl.png";
							strEEnable = isMail ? "Disable Mail Notification" : "Enable Mail Notification";
							if (isMail) {
								isMail = false;
							} else {
								isMail = true;
							}
				%>
					<span id="myDivE<%=strNotId%>" style="float: right"> <img
						width="20px" src="images1/<%=strEImage%>"
						title="<%=strEEnable%>"
						onclick="updateEmailStatus('myDivE<%=strNotId%>',<%=strNotId%>,<%=isMail%>,'isEmailEmployeeTravelPullout');" />
					</span>
				</h3>
				<div class="box-tools pull-right">
					<button class="btn btn-box-tool" data-widget="collapse">
						<i class="fa fa-plus"></i>
					</button>
					<button class="btn btn-box-tool" data-widget="remove">
						<i class="fa fa-times"></i>
					</button>
				</div>
			</div>
			<!-- /.box-header -->
			<div class="box-body"
				style="padding: 5px; overflow-y: auto; display: none;">
				<div class="content1" style="margin: 0px 0px 10px 0px">
					<table class="table table_no_border" cellspacing="2"
						cellpadding="2" width="100%">
						<tr>
							<td valign="top" class="txtlabel alignRight">Email
								Notification:</td>
							<td><s:checkbox cssStyle="width:0px"
									name="isEmailEmployeeTravelPullout"
									id="isEmailEmployeeTravelPullout" /><span class="hint">Please
									select the email notification.<span class="hint-pointer">&nbsp;</span>
							</span></td>
						</tr>
						<%-- <tr>
							<td valign="top" class="txtlabel alignRight">Enable/Disable Text Notification:</td>
							<td><s:checkbox name="isTextEmployeeExtraWorkCancel"/></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Text Message</td>
							<td><s:textfield cssStyle="width:526px" name="strTextEmployeeExtraWorkCancel"/></td>
						</tr> --%>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Subject</td>
							<td><s:textfield cssStyle="width:526px"
									name="strSubjectEmployeeTravelPullout" /><span class="hint">Please
									enter the email Subject.<span class="hint-pointer">&nbsp;</span>
							</span></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Body</td>
							<td><div style="float: left; margin-right: 20px;">
									<s:textarea name="strBodyEmployeeTravelPullout" cols="77"
										rows="10" />
									<span class="hint">Please enter the email body.<span
										class="hint-pointer">&nbsp;</span></span>
								</div>
								<div style="float: left;">
									[EMPCODE] <br /> [MGRNAME]<br /> [EMPFNAME] <br /> [EMPLNAME] <br />
									[LEAVE_TYPE] <br /> [LEAVE_FROM] <br /> [LEAVE_TO] <br />
									[LEAVE_NO_DAYS] <br /> [LEGAL_ENTITY_NAME] <br />
								</div></td>
						</tr>
					</table>
				</div>
			</div>
			<!-- /.box-body -->
		</div>

		<!-- -------------------------- Close Absence --------------------------- -->


		<!-- -------------------------- Compensation --------------------------- -->
		<div
			style="width: 100%; text-align: center; font-size: 14px; font-weight: bold; margin: 7px;"
			class="">Compensation</div>

		<%-- <div class="box box-primary collapsed-box" style="border-top-color: #EEEEEE; margin-top: 10px;">
                
                <div class="box-header with-border">
                    <h3 class="box-title" style="font-size: 14px;padding-right: 10px;">
                    	Employee Salary Approved 
						<%
							strNotId = ""+IConstants.N_NEW_SALARY_APPROVED; 
							isMail = (Boolean) request.getAttribute("isEmailEmployeeSalaryApproved");
							strEImage = isMail ? "mail_enbl.png" : "mail_disbl.png";
							strEEnable = isMail ? "Disable Mail Notification" : "Enable Mail Notification" ;
							
							if(isMail){
								isMail = false;
							} else{
								isMail = true;
							}
						%>
						<span id="myDivE<%=strNotId%>" style="float:right">
							<img width="20px" src="images1/<%=strEImage %>" title="<%=strEEnable %>" onclick="updateEmailStatus('myDivE<%=strNotId%>',<%=strNotId%>,<%=isMail%>,'isEmailEmployeeSalaryApproved');"/>
						</span>
                    </h3>
                    <div class="box-tools pull-right">
                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
                    </div>
                </div>
                <!-- /.box-header -->
                <div class="box-body" style="padding: 5px; overflow-y: auto; display:none;">
                    <div class="content1" style="margin:0px 0px 10px 0px">
						<table class="table table_no_border" cellspacing="2" cellpadding="2" width="100%">
							<tr>
								<td valign="top" class="txtlabel alignRight">Email Notification:</td>
								<td><s:checkbox name="isEmailEmployeeSalaryApproved" id="isEmailEmployeeSalaryApproved"/><span class="hint">Please select the email notification.<span class="hint-pointer">&nbsp;</span></span></td>
							</tr>
							<tr>
								<td valign="top" class="txtlabel alignRight">Email Subject:</td>
								<td><s:textfield cssStyle="width:480px" name="strSubjectEmployeeSalaryApproved"/><span class="hint">Please enter the email Subject.<span class="hint-pointer">&nbsp;</span></span></td>
							</tr>
							<tr>
								<td valign="top" class="txtlabel alignRight">Email Body:</td>
								<td><div style="float: left; margin-right: 20px;"><s:textarea name="strBodyEmployeeSalaryApproved" cols="63" rows="10" /><span class="hint">Please enter the email body.<span class="hint-pointer">&nbsp;</span></span></div>
									<div style="float: left;">  
										[LOGIN_LINK] <br/> 	
										[EMPCODE] <br/>
										[EMPFNAME] <br/>
										[EMPLNAME] <br/>
										[SAL_AMOUNT] <br/>
										[PAYCYCLE] <br/>
										[LEGAL_ENTITY_NAME] <br/>
									</div>
									 
								</td>
							</tr>
							
						</table>
					</div>
                </div>
                <!-- /.box-body -->
            </div> --%>


		<%-- <div class="box box-primary collapsed-box" style="border-top-color: #EEEEEE; margin-top: 10px;">
                <div class="box-header with-border">
                    <h3 class="box-title" style="font-size: 14px;padding-right: 10px;">
                    	Employee Payslip Generated Notification 
					<%
							strNotId = ""+IConstants.N_NEW_PAYSLIP_GENERATED; 
							isMail = (Boolean) request.getAttribute("isEmailEmployeePayslipGenerated");
							strEImage = isMail ? "mail_enbl.png" : "mail_disbl.png";
							strEEnable = isMail ? "Disable Mail Notification" : "Enable Mail Notification" ;
							
							if(isMail){
								isMail = false;
							} else{
								isMail = true;
							}
						%>
						<span id="myDivE<%=strNotId%>" style="float:right">
							<img width="20px" src="images1/<%=strEImage %>" title="<%=strEEnable %>" onclick="updateEmailStatus('myDivE<%=strNotId%>',<%=strNotId%>,<%=isMail%>,'isEmailEmployeePayslipGenerated');"/>
						</span>
                    </h3>
                    <div class="box-tools pull-right">
                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
                    </div>
                </div>
                <!-- /.box-header -->
                <div class="box-body" style="padding: 5px; overflow-y: auto; display:none;">
                    <div class="content1" style="margin:0px 0px 10px 0px">
					<table class="table table_no_border" cellspacing="2" cellpadding="2" width="100%">
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Notification:</td>
							<td><s:checkbox name="isEmailEmployeePayslipGenerated" id="isEmailEmployeePayslipGenerated"/><span class="hint">Please select the email notification.<span class="hint-pointer">&nbsp;</span></span></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Subject:</td>
							<td><s:textfield cssStyle="width:480px" name="strSubjectEmployeePayslipGenerated"/><span class="hint">Please enter the email Subject.<span class="hint-pointer">&nbsp;</span></span></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Body:</td>
							<td><div style="float: left; margin-right: 20px;"><s:textarea name="strBodyEmployeePayslipGenerated" cols="63" rows="10" /><span class="hint">Please enter the email body.<span class="hint-pointer">&nbsp;</span></span></div>
								<div style="float: left;">
									[LOGIN_LINK] <br/> 	
									[EMPCODE] <br/>
									[EMPFNAME] <br/> 
									[EMPLNAME] <br/>
									[LEGAL_ENTITY_NAME] <br/>
								</div>
								 
							</td>
						</tr>
						
					</table>
				</div>
                </div>
                <!-- /.box-body -->
            </div> --%>


		<div class="box box-primary collapsed-box"
			style="border-top-color: #EEEEEE; margin-top: 10px;">

			<div class="box-header with-border">
				<h3 class="box-title" style="font-size: 14px; padding-right: 10px;">
					Salary Released
					<%-- <%=uF.showData(hmNotificationStatus.get("NOT_"+IConstants.N_NEW_SALARY_PAID), "") %> --%>
					<%
						strNotId = "" + IConstants.N_NEW_SALARY_PAID;
								isMail = (Boolean) request.getAttribute("isEmailSalaryReleased");
								strEImage = isMail ? "mail_enbl.png" : "mail_disbl.png";
								strEEnable = isMail ? "Disable Mail Notification" : "Enable Mail Notification";

								if (isMail) {
									isMail = false;
								} else {
									isMail = true;
								}
					%>
					<span id="myDivE<%=strNotId%>" style="float: right"> <img
						width="20px" src="images1/<%=strEImage%>"
						title="<%=strEEnable%>"
						onclick="updateEmailStatus('myDivE<%=strNotId%>',<%=strNotId%>,<%=isMail%>,'isEmailSalaryReleased');" />
					</span>
				</h3>
				<div class="box-tools pull-right">
					<button class="btn btn-box-tool" data-widget="collapse">
						<i class="fa fa-plus"></i>
					</button>
					<button class="btn btn-box-tool" data-widget="remove">
						<i class="fa fa-times"></i>
					</button>
				</div>
			</div>
			<!-- /.box-header -->
			<div class="box-body"
				style="padding: 5px; overflow-y: auto; display: none;">
				<div class="content1" style="margin: 0px 0px 10px 0px">
					<table class="table table_no_border" cellspacing="2"
						cellpadding="2" width="100%">
						<tr>
							<td valign="top" class="txtlabel alignRight">Email
								Notification:</td>
							<td><s:checkbox name="isEmailSalaryReleased"
									id="isEmailSalaryReleased" /><span class="hint">Please
									select the email notification.<span class="hint-pointer">&nbsp;</span>
							</span></td>
						</tr>
						<%-- <tr>
							<td valign="top" class="txtlabel alignRight">Enable/Disable Text Notification:</td>
							<td><s:checkbox name="isTextSalaryReleased"/><span class="hint">Please confirm the password by entering it once again.<span class="hint-pointer">&nbsp;</span></span></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Text Message</td>
							<td><s:textfield cssStyle="width:480px" name="strTextSalaryReleased"/><span class="hint">Please confirm the password by entering it once again.<span class="hint-pointer">&nbsp;</span></span></td>
						</tr> --%>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Subject:</td>
							<td><s:textfield cssStyle="width:480px"
									name="strSubjectSalaryReleased" /><span class="hint">Please
									enter the email Subject.<span class="hint-pointer">&nbsp;</span>
							</span></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Body:</td>
							<td><div style="float: left; margin-right: 20px;">
									<s:textarea name="strBodySalaryReleased" cols="63" rows="10" />
									<span class="hint">Please enter the email body.<span
										class="hint-pointer">&nbsp;</span></span>
								</div>
								<div style="float: left;">
									[LOGIN_LINK] <br /> [EMPCODE] <br /> [EMPFNAME] <br />
									[EMPLNAME] <br /> [SAL_AMOUNT] <br /> [ACC_NO] <br />
									[LEGAL_ENTITY_NAME] <br />
								</div></td>
						</tr>

					</table>
				</div>
			</div>
			<!-- /.box-body -->
		</div>


		<%-- <div class="box box-primary collapsed-box" style="border-top-color: #EEEEEE; margin-top: 10px;">
                <div class="box-header with-border">
                    <h3 class="box-title" style="font-size: 14px;padding-right: 10px;">
                    	New Reimbursement Request Notification 
					<%
						strNotId = ""+IConstants.N_EMPLOYEE_REIMBURSEMENT_REQUEST; 
						isMail = (Boolean) request.getAttribute("isEmailNewReimbursementRequest");
						strEImage = isMail ? "mail_enbl.png" : "mail_disbl.png";
						strEEnable = isMail ? "Disable Mail Notification" : "Enable Mail Notification" ;
						
						if(isMail){
							isMail = false;
						} else{
							isMail = true;
						}
					%>
					<span id="myDivE<%=strNotId%>" style="float:right">
						<img width="20px" src="images1/<%=strEImage %>" title="<%=strEEnable %>" onclick="updateEmailStatus('myDivE<%=strNotId%>',<%=strNotId%>,<%=isMail%>,'isEmailNewReimbursementRequest');"/>
					</span>
                    </h3>
                    <div class="box-tools pull-right">
                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
                    </div>
                </div>
                <!-- /.box-header -->
                <div class="box-body" style="padding: 5px; overflow-y: auto; display:none;">
                    <div class="content1" style="margin:0px 0px 10px 0px">
					<table class="table table_no_border" cellspacing="2" cellpadding="2" width="100%">
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Notification:</td>
							<td><s:checkbox name="isEmailNewReimbursementRequest" id="isEmailNewReimbursementRequest"/><span class="hint">Please select the email notification.<span class="hint-pointer">&nbsp;</span></span></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Subject:</td>
							<td><s:textfield cssStyle="width:480px" name="strSubjectNewReimbursementRequest"/><span class="hint">Please enter the email Subject.<span class="hint-pointer">&nbsp;</span></span></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Body:</td>
							<td><div style="float: left; margin-right: 20px;"><s:textarea name="strBodyNewReimbursementRequest" cols="63" rows="10" /><span class="hint">Please enter the email body.<span class="hint-pointer">&nbsp;</span></span></div>
								<div style="float: left;">
									[LOGIN_LINK] <br/> 	
									[EMPCODE] <br/>
									[EMPFNAME] <br/>
									[EMPLNAME] <br/>
									[RMB_FROM] <br/>
									[RMB_TO] <br/>
									[RMB_PURPOSE] <br/>
									[RMB_AMOUNT] <br/>
									[RMB_TYPE] <br/>
									[RMB_DATE] <br/>
									[RMB_CURRENCY] <br/>
									[LEGAL_ENTITY_NAME] <br/>
								</div>
								 
							</td>
						</tr>
						
					</table>
				</div>
                </div>
            </div> --%>

		<div class="box box-primary collapsed-box"
			style="border-top-color: #EEEEEE; margin-top: 10px;">

			<div class="box-header with-border">
				<h3 class="box-title" style="font-size: 14px; padding-right: 10px;">
					New Reimbursement Request
					<%-- <%=uF.showData(hmNotificationStatus.get("NOT_"+IConstants.N_EMPLOYEE_REIMBURSEMENT_APPROVAL), "") %> --%>
					<%
						strNotId = "" + IConstants.N_MANAGER_REIMBURSEMENT_REQUEST;
								isMail = (Boolean) request.getAttribute("isEmailNewManagerReimbursementRequest");
								strEImage = isMail ? "mail_enbl.png" : "mail_disbl.png";
								strEEnable = isMail ? "Disable Mail Notification" : "Enable Mail Notification";
								if (isMail) {
									isMail = false;
								} else {
									isMail = true;
								}
					%>
					<span id="myDivE<%=strNotId%>" style="float: right"> <img
						width="20px" src="images1/<%=strEImage%>"
						title="<%=strEEnable%>"
						onclick="updateEmailStatus('myDivE<%=strNotId%>',<%=strNotId%>,<%=isMail%>,'isEmailNewReimbursementRequest');" />
					</span>
				</h3>
				<div class="box-tools pull-right">
					<button class="btn btn-box-tool" data-widget="collapse">
						<i class="fa fa-plus"></i>
					</button>
					<button class="btn btn-box-tool" data-widget="remove">
						<i class="fa fa-times"></i>
					</button>
				</div>
			</div>
			<!-- /.box-header -->
			<div class="box-body"
				style="padding: 5px; overflow-y: auto; display: none;">
				<div class="content1" style="margin: 0px 0px 10px 0px">
					<table border="0" cellspacing="2" cellpadding="2" width="100%">
						<tr>
							<td valign="top" class="txtlabel alignRight">Email
								Notification:</td>
							<td><s:checkbox name="isEmailNewManagerReimbursementRequest"
									id="isEmailNewManagerReimbursementRequest" /><span class="hint">Please
									select the email notification.<span class="hint-pointer">&nbsp;</span>
							</span></td>
						</tr>
						<%-- <tr>
							<td valign="top" class="txtlabel alignRight">Enable/Disable Text Notification:</td>
							<td><s:checkbox name="isTextNewManagerReimbursementRequest"/><span class="hint">Please confirm the password by entering it once again.<span class="hint-pointer">&nbsp;</span></span></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Text Message</td>
							<td><s:textfield cssStyle="width:526px" name="strTextNewManagerReimbursementRequest"/><span class="hint">Please confirm the password by entering it once again.<span class="hint-pointer">&nbsp;</span></span></td>
						</tr> --%>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Subject:</td>
							<td><s:textfield cssStyle="width:526px"
									name="strSubjectNewManagerReimbursementRequest" /><span
								class="hint">Please enter the email Subject.<span
									class="hint-pointer">&nbsp;</span></span></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Body:</td>
							<td><div style="float: left; margin-right: 20px;">
									<s:textarea name="strBodyNewManagerReimbursementRequest"
										cols="63" rows="10" />
									<span class="hint">Please enter the email body.<span
										class="hint-pointer">&nbsp;</span></span>
								</div>
								<div style="float: left;">
									<!-- [LOGIN_LINK] <br/> -->
									[EMPCODE] <br /> [MGRNAME] <br /> [EMPFNAME] <br /> [EMPLNAME]
									<br /> [RMB_FROM] <br /> [RMB_TO] <br /> [RMB_PURPOSE] <br />
									[RMB_AMOUNT] <br /> [RMB_TYPE] <br /> [RMB_DATE] <br />
									[RMB_CURRENCY] <br /> [LEGAL_ENTITY_NAME] <br />
								</div></td>
						</tr>
					</table>
				</div>
			</div>
			<!-- /.box-body -->
		</div>


		<div class="box box-primary collapsed-box"
			style="border-top-color: #EEEEEE; margin-top: 10px;">

			<div class="box-header with-border">
				<h3 class="box-title" style="font-size: 14px; padding-right: 10px;">
					Reimbursement Approval Alert
					<%
					strNotId = "" + IConstants.N_EMPLOYEE_REIMBURSEMENT_APPROVAL;
							isMail = (Boolean) request.getAttribute("isEmailNewReimbursementApproval");
							strEImage = isMail ? "mail_enbl.png" : "mail_disbl.png";
							strEEnable = isMail ? "Disable Mail Notification" : "Enable Mail Notification";

							if (isMail) {
								isMail = false;
							} else {
								isMail = true;
							}
				%>
					<span id="myDivE<%=strNotId%>" style="float: right"> <img
						width="20px" src="images1/<%=strEImage%>"
						title="<%=strEEnable%>"
						onclick="updateEmailStatus('myDivE<%=strNotId%>',<%=strNotId%>,<%=isMail%>,'isEmailNewReimbursementApproval');" />
					</span>
				</h3>
				<div class="box-tools pull-right">
					<button class="btn btn-box-tool" data-widget="collapse">
						<i class="fa fa-plus"></i>
					</button>
					<button class="btn btn-box-tool" data-widget="remove">
						<i class="fa fa-times"></i>
					</button>
				</div>
			</div>
			<!-- /.box-header -->
			<div class="box-body"
				style="padding: 5px; overflow-y: auto; display: none;">
				<div class="content1" style="margin: 0px 0px 10px 0px">
					<table class="table table_no_border" cellspacing="2"
						cellpadding="2" width="100%">
						<tr>
							<td valign="top" class="txtlabel alignRight">Email
								Notification:</td>
							<td><s:checkbox name="isEmailNewReimbursementApproval"
									id="isEmailNewReimbursementApproval" /><span class="hint">Please
									select the email notification.<span class="hint-pointer">&nbsp;</span>
							</span></td>
						</tr>
						<%-- <tr>
							<td valign="top" class="txtlabel alignRight">Enable/Disable Text Notification:</td>
							<td><s:checkbox name="isTextNewReimbursementApproval"/><span class="hint">Please confirm the password by entering it once again.<span class="hint-pointer">&nbsp;</span></span></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Text Message</td>
							<td><s:textfield cssStyle="width:480px" name="strTextNewReimbursementApproval"/><span class="hint">Please confirm the password by entering it once again.<span class="hint-pointer">&nbsp;</span></span></td>
						</tr> --%>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Subject:</td>
							<td><s:textfield cssStyle="width:480px"
									name="strSubjectNewReimbursementApproval" /><span class="hint">Please
									enter the email Subject.<span class="hint-pointer">&nbsp;</span>
							</span></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Body:</td>
							<td><div style="float: left; margin-right: 20px;">
									<s:textarea name="strBodyNewReimbursementApproval" cols="63"
										rows="10" />
									<span class="hint">Please enter the email body.<span
										class="hint-pointer">&nbsp;</span></span>
								</div>
								<div style="float: left;">
									[LOGIN_LINK] <br /> [EMPCODE] <br /> [EMPFNAME] <br />
									[EMPLNAME] <br /> [RMB_FROM] <br /> [RMB_TO] <br />
									[RMB_PURPOSE] <br /> [RMB_AMOUNT] <br /> [RMB_TYPE] <br />
									[RMB_DATE] <br /> [RMB_CURRENCY] <br /> [LEGAL_ENTITY_NAME] <br />
								</div></td>
						</tr>

					</table>
				</div>
			</div>
			<!-- /.box-body -->
		</div>


		<!-- -------------------------- Close Compensation --------------------------- -->

		<!-- -------------------------- Time --------------------------- -->
		<!-- <div style="width: 100%; text-align: center; font-size: 14px; font-weight: bold; margin: 7px;" class="">Time</div> -->

		<%-- <div class="box box-primary collapsed-box" style="border-top-color: #EEEEEE; margin-top: 10px;">
                
                <div class="box-header with-border">
                    <h3 class="box-title" style="font-size: 14px;padding-right: 10px;">
                    	Employee New Roster Notification 
					<%
							strNotId = ""+IConstants.N_NEW_ROSTER; 
							isMail = (Boolean) request.getAttribute("isEmailNewRoster");
							strEImage = isMail ? "mail_enbl.png" : "mail_disbl.png";
							strEEnable = isMail ? "Disable Mail Notification" : "Enable Mail Notification" ;
							
							if(isMail){
								isMail = false;
							} else{
								isMail = true;
							}
						%>
						<span id="myDivE<%=strNotId%>" style="float:right">
							<img width="20px" src="images1/<%=strEImage %>" title="<%=strEEnable %>" onclick="updateEmailStatus('myDivE<%=strNotId%>',<%=strNotId%>,<%=isMail%>,'isEmailNewRoster');"/>
						</span>
                    </h3>
                    <div class="box-tools pull-right">
                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
                    </div>
                </div>
                <!-- /.box-header -->
                <div class="box-body" style="padding: 5px; overflow-y: auto; display:none;">
                    <div class="content1" style="margin:0px 0px 10px 0px">
					<table class="table table_no_border" cellspacing="2" cellpadding="2" width="100%">
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Notification:</td>
							<td><s:checkbox name="isEmailNewRoster" id="isEmailNewRoster"/><span class="hint">Please select the email notification.<span class="hint-pointer">&nbsp;</span></span></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Subject:</td>
							<td><s:textfield cssStyle="width:480px" name="strSubjectNewRoster"/><span class="hint">Please enter the email Subject.<span class="hint-pointer">&nbsp;</span></span></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Body:</td>
							<td><div style="float: left; margin-right: 20px;"><s:textarea name="strBodyNewRoster" cols="63" rows="10" /><span class="hint">Please enter the email body.<span class="hint-pointer">&nbsp;</span></span></div>
								<div style="float: left;">
									[LOGIN_LINK] <br/> 	
									[EMPCODE] <br/>
									[EMPFNAME] <br/>
									[EMPLNAME] <br/>
									[LEGAL_ENTITY_NAME] <br/>
								</div>
								 
							</td>
						</tr>
						
					</table>
				</div>
                </div>
                <!-- /.box-body -->
            </div> --%>


		<%-- <div class="box box-primary collapsed-box" style="border-top-color: #EEEEEE; margin-top: 10px;">
                
                <div class="box-header with-border">
                    <h3 class="box-title" style="font-size: 14px;padding-right: 10px;">
                    	Roster Changed Notification 
						<%
							strNotId = ""+IConstants.N_CHANGE_ROSTER; 
							isMail = (Boolean) request.getAttribute("isEmailRosterChanged");
							strEImage = isMail ? "mail_enbl.png" : "mail_disbl.png";
							strEEnable = isMail ? "Disable Mail Notification" : "Enable Mail Notification" ;
							
							if(isMail){
								isMail = false;
							} else{
								isMail = true;
							}
						%>
						<span id="myDivE<%=strNotId%>" style="float:right">
							<img width="20px" src="images1/<%=strEImage %>" title="<%=strEEnable %>" onclick="updateEmailStatus('myDivE<%=strNotId%>',<%=strNotId%>,<%=isMail%>,'isEmailRosterChanged');"/>
						</span>
                    </h3>
                    <div class="box-tools pull-right">
                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
                    </div>
                </div>
                <!-- /.box-header -->
                <div class="box-body" style="padding: 5px; overflow-y: auto; display:none;">
                    <div class="content1" style="margin:0px 0px 10px 0px">
						<table class="table table_no_border" cellspacing="2" cellpadding="2" width="100%">
							<tr>
								<td valign="top" class="txtlabel alignRight">Email Notification:</td>
								<td><s:checkbox name="isEmailRosterChanged" id="isEmailRosterChanged"/><span class="hint">Please select the email notification.<span class="hint-pointer">&nbsp;</span></span></td>
							</tr>
							<tr>
								<td valign="top" class="txtlabel alignRight">Email Subject:</td>
								<td><s:textfield cssStyle="width:480px" name="strSubjectRosterChanged"/><span class="hint">Please enter the email Subject.<span class="hint-pointer">&nbsp;</span></span></td>
							</tr>
							<tr>
								<td valign="top" class="txtlabel alignRight">Email Body:</td>
								<td><div style="float: left; margin-right: 20px;"><s:textarea name="strBodyRosterChanged" cols="63" rows="10" /><span class="hint">Please enter the email body.<span class="hint-pointer">&nbsp;</span></span></div>
									<div style="float: left;">
										[LOGIN_LINK] <br/> 	
										[EMPCODE] <br/>
										[EMPFNAME] <br/>
										[EMPLNAME] <br/>
										[LEGAL_ENTITY_NAME] <br/>
									</div>
									 
								</td>
							</tr>
							
						</table>
					</div>
                </div>
                <!-- /.box-body -->
            </div> --%>


		<%-- <div class="box box-primary collapsed-box" style="border-top-color: #EEEEEE; margin-top: 10px;">
                
                <div class="box-header with-border">
                    <h3 class="box-title" style="font-size: 14px;padding-right: 10px;">
                    	New Mail Notification 
					<%
							strNotId = ""+IConstants.N_NEW_MAIL; 
							isMail = (Boolean) request.getAttribute("isEmailNewMail");
							strEImage = isMail ? "mail_enbl.png" : "mail_disbl.png";
							strEEnable = isMail ? "Disable Mail Notification" : "Enable Mail Notification" ;
							
							if(isMail){
								isMail = false;
							} else{
								isMail = true;
							}
						%>
						<span id="myDivE<%=strNotId%>" style="float:right">
							<img width="20px" src="images1/<%=strEImage %>" title="<%=strEEnable %>" onclick="updateEmailStatus('myDivE<%=strNotId%>',<%=strNotId%>,<%=isMail%>,'isEmailNewMail');"/>
						</span>
                    </h3>
                    <div class="box-tools pull-right">
                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
                    </div>
                </div>
                <!-- /.box-header -->
                <div class="box-body" style="padding: 5px; overflow-y: auto; display:none;">
                    <div class="content1" style="margin:0px 0px 10px 0px">
					<table class="table table_no_border" cellspacing="2" cellpadding="2" width="100%">
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Notification:</td>
							<td><s:checkbox name="isEmailNewMail" id="isEmailNewMail"/><span class="hint">Please select the email notification.<span class="hint-pointer">&nbsp;</span></span></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Subject:</td>
							<td><s:textfield cssStyle="width:480px" name="strSubjectNewMail"/><span class="hint">Please enter the email Subject.<span class="hint-pointer">&nbsp;</span></span></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Body:</td>
							<td><div style="float: left; margin-right: 20px;"><s:textarea name="strBodyNewMail" cols="63" rows="10" /><span class="hint">Please enter the email body.<span class="hint-pointer">&nbsp;</span></span></div>
								<div style="float: left;">
									[EMPCODE] <br/>
									[EMPFNAME] <br/>
									[EMPLNAME] <br/>
									[MAIL_FROM] <br/>
									[MAIL_SUBJECT] <br/>
									[MAIL_BODY] <br/>
									[LEGAL_ENTITY_NAME] <br/>
								</div>
								 
							</td>
						</tr>
						
					</table>
				</div>
                </div>
                <!-- /.box-body -->
            </div> --%>

		<!-- -------------------------- Close Time --------------------------- -->


		<!-- -------------------------- HUB --------------------------- -->
		<div
			style="width: 100%; text-align: center; font-size: 14px; font-weight: bold; margin: 7px;"
			class="">Hub</div>

		<div class="box box-primary collapsed-box"
			style="border-top-color: #EEEEEE; margin-top: 10px;">

			<div class="box-header with-border">
				<h3 class="box-title" style="font-size: 14px; padding-right: 10px;">
					New Announcement
					<%
					strNotId = "" + IConstants.N_NEW_NOTICE;
							isMail = (Boolean) request.getAttribute("isEmailNewAnnouncement");
							strEImage = isMail ? "mail_enbl.png" : "mail_disbl.png";
							strEEnable = isMail ? "Disable Mail Notification" : "Enable Mail Notification";
							if (isMail) {
								isMail = false;
							} else {
								isMail = true;
							}
				%>
					<span id="myDivE<%=strNotId%>" style="float: right"> <img
						width="20px" src="images1/<%=strEImage%>"
						title="<%=strEEnable%>"
						onclick="updateEmailStatus('myDivE<%=strNotId%>',<%=strNotId%>,<%=isMail%>,'isEmailNewAnnouncement');" />
					</span>
				</h3>
				<div class="box-tools pull-right">
					<button class="btn btn-box-tool" data-widget="collapse">
						<i class="fa fa-plus"></i>
					</button>
					<button class="btn btn-box-tool" data-widget="remove">
						<i class="fa fa-times"></i>
					</button>
				</div>
			</div>
			<!-- /.box-header -->
			<div class="box-body"
				style="padding: 5px; overflow-y: auto; display: none;">
				<div class="content1" style="margin: 0px 0px 10px 0px">
					<table border="0" cellspacing="2" cellpadding="2" width="100%">
						<tr>
							<td valign="top" class="txtlabel alignRight">Email
								Notification:</td>
							<td><s:checkbox name="isEmailNewAnnouncement"
									id="isEmailNewAnnouncement" /><span class="hint">Please
									select the email notification.<span class="hint-pointer">&nbsp;</span>
							</span></td>
						</tr>
						<%-- <tr>
							<td valign="top" class="txtlabel alignRight">Enable/Disable Text Notification:</td>
							<td><s:checkbox name="isTextNewAnnouncement"/><span class="hint">Please confirm the password by entering it once again.<span class="hint-pointer">&nbsp;</span></span></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Text Message</td>
							<td><s:textfield cssStyle="width:526px" name="strTextNewAnnouncement"/><span class="hint">Please confirm the password by entering it once again.<span class="hint-pointer">&nbsp;</span></span></td>
						</tr> --%>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Subject:</td>
							<td><s:textfield cssStyle="width:526px"
									name="strSubjectNewAnnouncement" /><span class="hint">Please
									enter the email Subject.<span class="hint-pointer">&nbsp;</span>
							</span></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Body:</td>
							<td><div style="float: left; margin-right: 20px;">
									<s:textarea name="strBodyNewAnnouncement" cols="63" rows="10" />
									<span class="hint">Please enter the email body.<span
										class="hint-pointer">&nbsp;</span></span>
								</div>
								<div style="float: left;">
									<!-- [LOGIN_LINK] <br/> -->
									[AN_HEADING] <br /> [AN_BODY] <br /> [LEGAL_ENTITY_NAME] <br />
								</div></td>
						</tr>
					</table>
				</div>
			</div>
			<!-- /.box-body -->
		</div>
		
	<!-- ===parvez date: 15-02-2023=== -->	
		<div class="box box-primary collapsed-box"
			style="border-top-color: #EEEEEE; margin-top: 10px;">

			<div class="box-header with-border">
				<h3 class="box-title" style="font-size: 14px; padding-right: 10px;">
					New Circular Published
					<%
					strNotId = "" + IConstants.N_ORG_CIRCULAR_PUBLISH;
							isMail = (Boolean) request.getAttribute("isEmailOnCircularCreation");
							strEImage = isMail ? "mail_enbl.png" : "mail_disbl.png";
							strEEnable = isMail ? "Disable Mail Notification" : "Enable Mail Notification";
							if (isMail) {
								isMail = false;
							} else {
								isMail = true;
							}
				%>
					<span id="myDivE<%=strNotId%>" style="float: right"> <img
						width="20px" src="images1/<%=strEImage%>"
						title="<%=strEEnable%>"
						onclick="updateEmailStatus('myDivE<%=strNotId%>',<%=strNotId%>,<%=isMail%>,'isEmailOnCircularCreation');" />
					</span>
				</h3>
				<div class="box-tools pull-right">
					<button class="btn btn-box-tool" data-widget="collapse">
						<i class="fa fa-plus"></i>
					</button>
					<button class="btn btn-box-tool" data-widget="remove">
						<i class="fa fa-times"></i>
					</button>
				</div>
			</div>
			<!-- /.box-header -->
			<div class="box-body"
				style="padding: 5px; overflow-y: auto; display: none;">
				<div class="content1" style="margin: 0px 0px 10px 0px">
					<table border="0" cellspacing="2" cellpadding="2" width="100%">
						<tr>
							<td valign="top" class="txtlabel alignRight">Email
								Notification:</td>
							<td><s:checkbox name="isEmailOnCircularCreation"
									id="isEmailOnCircularCreation" /><span class="hint">Please
									select the email notification.<span class="hint-pointer">&nbsp;</span>
							</span></td>
						</tr>
						<%-- <tr>
							<td valign="top" class="txtlabel alignRight">Enable/Disable Text Notification:</td>
							<td><s:checkbox name="isTextOnCircularCreation"/><span class="hint">Please confirm the password by entering it once again.<span class="hint-pointer">&nbsp;</span></span></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Text Message</td>
							<td><s:textfield cssStyle="width:526px" name="strTextOnCircularCreation"/><span class="hint">Please confirm the password by entering it once again.<span class="hint-pointer">&nbsp;</span></span></td>
						</tr> --%>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Subject:</td>
							<td><s:textfield cssStyle="width:526px"
									name="strSubjectOnCircularCreation" /><span class="hint">Please
									enter the email Subject.<span class="hint-pointer">&nbsp;</span>
							</span></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Body:</td>
							<td><div style="float: left; margin-right: 20px;">
									<s:textarea name="strBodyOnCircularCreation" cols="63" rows="10" />
									<span class="hint">Please enter the email body.<span
										class="hint-pointer">&nbsp;</span></span>
								</div>
								<div style="float: left;">
									[ADDED BY] <br /> 
									[PUBLISH_DATE] <br />
									[LEGAL_ENTITY_NAME] <br />
								</div></td>
						</tr>
					</table>
				</div>
			</div>
			<!-- /.box-body -->
		</div>
	<!-- ===end parvez date: 15-02-2023=== -->	

		<!-- -------------------------- Close HUB --------------------------- -->


		<%
			if (arrEnabledModules != null
							&& ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_ONBOARDING + "") >= 0) {
		%>
		<!-- -------------------------- Recruitment --------------------------- -->
		<div
			style="width: 100%; text-align: center; font-size: 14px; font-weight: bold; margin: 7px;"
			class="">Recruitment</div>

		<div class="box box-primary collapsed-box"
			style="border-top-color: #EEEEEE; margin-top: 10px;">
			<div class="box-header with-border">
				<h3 class="box-title" style="font-size: 14px; padding-right: 10px;">
					Recruitment Request
					<%
					strNotId = "" + IConstants.N_RECRUITMENT_REQUEST;
								isMail = (Boolean) request.getAttribute("isEmailRecruitmentRequest");
								strEImage = isMail ? "mail_enbl.png" : "mail_disbl.png";
								strEEnable = isMail ? "Disable Mail Notification" : "Enable Mail Notification";

								if (isMail) {
									isMail = false;
								} else {
									isMail = true;
								}
				%>
					<span id="myDivE<%=strNotId%>" style="float: right"> <img
						width="20px" src="images1/<%=strEImage%>"
						title="<%=strEEnable%>"
						onclick="updateEmailStatus('myDivE<%=strNotId%>',<%=strNotId%>,<%=isMail%>,'isEmailRecruitmentRequest');" />
					</span>
				</h3>
				<div class="box-tools pull-right">
					<button class="btn btn-box-tool" data-widget="collapse">
						<i class="fa fa-plus"></i>
					</button>
					<button class="btn btn-box-tool" data-widget="remove">
						<i class="fa fa-times"></i>
					</button>
				</div>
			</div>
			<!-- /.box-header -->
			<div class="box-body"
				style="padding: 5px; overflow-y: auto; display: none;">

				<div class="content1" style="margin: 0px 0px 10px 0px">
					<table class="table table_no_border" cellspacing="2"
						cellpadding="2" width="100%">
						<tr>
							<td valign="top" class="txtlabel alignRight">Email
								Notification:</td>
							<td><s:checkbox name="isEmailRecruitmentRequest"
									id="isEmailRecruitmentRequest" /></td>
						</tr>
						<%-- <tr>
							<td valign="top" class="txtlabel alignRight">Enable/Disable Text Notification:</td>
							<td><s:checkbox name="isTextRecruitmentRequest"/></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Text Message</td>
							<td><s:textfield cssStyle="width:480px" name="strTextRecruitmentRequest"/></td>
						</tr> --%>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Subject:</td>
							<td><s:textfield cssStyle="width:480px"
									name="strSubjectRecruitmentRequest" /></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Body:</td>
							<td><div style="float: left; margin-right: 20px;">
									<s:textarea name="strBodyRecruitmentRequest" cols="63"
										rows="10" />
								</div>
								<div style="float: left;">
									[EMPFNAME] <br /> [EMPLNAME] <br /> [RECRUITMENT_DESIG] <br />
									[LEGAL_ENTITY_NAME] <br />
								</div></td>
						</tr>
					</table>
				</div>
			</div>
			<!-- /.box-body -->
		</div>


		<div class="box box-primary collapsed-box"
			style="border-top-color: #EEEEEE; margin-top: 10px;">
			<div class="box-header with-border">
				<h3 class="box-title" style="font-size: 14px; padding-right: 10px;">
					Mail for Reference
					<%
					strNotId = "" + IConstants.N_RECRUITMENT_MAIL_TO_EMP;
								isMail = (Boolean) request.getAttribute("isEmailNewRecruitmentToEmp");
								strEImage = isMail ? "mail_enbl.png" : "mail_disbl.png";
								strEEnable = isMail ? "Disable Mail Notification" : "Enable Mail Notification";

								if (isMail) {
									isMail = false;
								} else {
									isMail = true;
								}
				%>
					<span id="myDivE<%=strNotId%>" style="float: right"> <img
						width="20px" src="images1/<%=strEImage%>"
						title="<%=strEEnable%>"
						onclick="updateEmailStatus('myDivE<%=strNotId%>',<%=strNotId%>,<%=isMail%>,'isEmailNewRecruitmentToEmp');" />
					</span>
				</h3>
				<div class="box-tools pull-right">
					<button class="btn btn-box-tool" data-widget="collapse">
						<i class="fa fa-plus"></i>
					</button>
					<button class="btn btn-box-tool" data-widget="remove">
						<i class="fa fa-times"></i>
					</button>
				</div>
			</div>
			<!-- /.box-header -->
			<div class="box-body"
				style="padding: 5px; overflow-y: auto; display: none;">
				<div class="content1" style="margin: 0px 0px 10px 0px">
					<table class="table table_no_border" cellspacing="2"
						cellpadding="2" width="100%">
						<tr>
							<td valign="top" class="txtlabel alignRight">Email
								Notification:</td>
							<td><s:checkbox name="isEmailNewRecruitmentToEmp"
									id="isEmailNewRecruitmentToEmp" /></td>
						</tr>
						<%-- <tr>
							<td valign="top" class="txtlabel alignRight">Enable/Disable Text Notification:</td>
							<td><s:checkbox name="isTextNewRecruitmentToEmp"/></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Text Message</td>
							<td><s:textfield cssStyle="width:480px" name="strTextNewRecruitmentToEmp"/></td>
						</tr> --%>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Subject:</td>
							<td><s:textfield cssStyle="width:480px"
									name="strSubjectNewRecruitmentToEmp" /></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Body:</td>
							<td><div style="float: left; margin-right: 20px;">
									<s:textarea name="strBodyNewRecruitmentToEmp" cols="63"
										rows="10" />
								</div>
								<div style="float: left;">
									[EMPFNAME] <br /> [EMPLNAME] <br /> [RECRUITMENT_DESIG] <br />
									[LEGAL_ENTITY_NAME] <br />
								</div></td>
						</tr>
					</table>
				</div>
			</div>
			<!-- /.box-body -->
		</div>


		<div class="box box-primary collapsed-box"
			style="border-top-color: #EEEEEE; margin-top: 10px;">

			<div class="box-header with-border">
				<h3 class="box-title" style="font-size: 14px; padding-right: 10px;">
					Recruitment Approval
					<%
					strNotId = "" + IConstants.N_RECRUITMENT_APPROVAL;
								isMail = (Boolean) request.getAttribute("isEmailRecruitmentApproval");
								strEImage = isMail ? "mail_enbl.png" : "mail_disbl.png";
								strEEnable = isMail ? "Disable Mail Notification" : "Enable Mail Notification";

								if (isMail) {
									isMail = false;
								} else {
									isMail = true;
								}
				%>
					<span id="myDivE<%=strNotId%>" style="float: right"> <img
						width="20px" src="images1/<%=strEImage%>"
						title="<%=strEEnable%>"
						onclick="updateEmailStatus('myDivE<%=strNotId%>',<%=strNotId%>,<%=isMail%>,'isEmailRecruitmentApproval');" />
					</span>
				</h3>
				<div class="box-tools pull-right">
					<button class="btn btn-box-tool" data-widget="collapse">
						<i class="fa fa-plus"></i>
					</button>
					<button class="btn btn-box-tool" data-widget="remove">
						<i class="fa fa-times"></i>
					</button>
				</div>
			</div>
			<!-- /.box-header -->
			<div class="box-body"
				style="padding: 5px; overflow-y: auto; display: none;">
				<div class="content1" style="margin: 0px 0px 10px 0px">
					<table class="table table_no_border" cellspacing="2"
						cellpadding="2" width="100%">
						<tr>
							<td valign="top" class="txtlabel alignRight">Email
								Notification:</td>
							<td><s:checkbox name="isEmailRecruitmentApproval"
									id="isEmailRecruitmentApproval" /></td>
						</tr>
						<%-- <tr>
							<td valign="top" class="txtlabel alignRight">Enable/Disable Text Notification:</td>
							<td><s:checkbox name="isTextRecruitmentApproval"/></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Text Message</td>
							<td><s:textfield cssStyle="width:480px" name="strTextRecruitmentApproval"/></td>
						</tr> --%>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Subject:</td>
							<td><s:textfield cssStyle="width:480px"
									name="strSubjectRecruitmentApproval" /></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Body:</td>
							<td><div style="float: left; margin-right: 20px;">
									<s:textarea name="strBodyRecruitmentApproval" cols="63"
										rows="10" />
								</div>
								<div style="float: left;">
									[EMPFNAME] <br /> [EMPLNAME] <br /> [RECRUITMENT_DESIG] <br />
									[LEGAL_ENTITY_NAME] <br />
								</div></td>
						</tr>
					</table>
				</div>
			</div>
			<!-- /.box-body -->
		</div>


		<div class="box box-primary collapsed-box"
			style="border-top-color: #EEEEEE; margin-top: 10px;">

			<div class="box-header with-border">
				<h3 class="box-title" style="font-size: 14px; padding-right: 10px;">
					Applicant Shotlisted alert
					<%
					strNotId = "" + IConstants.N_APPLICATION_SHORTLIST;
								isMail = (Boolean) request.getAttribute("isEmailApplicationShortlist");
								strEImage = isMail ? "mail_enbl.png" : "mail_disbl.png";
								strEEnable = isMail ? "Disable Mail Notification" : "Enable Mail Notification";

								if (isMail) {
									isMail = false;
								} else {
									isMail = true;
								}
				%>
					<span id="myDivE<%=strNotId%>" style="float: right"> <img
						width="20px" src="images1/<%=strEImage%>"
						title="<%=strEEnable%>"
						onclick="updateEmailStatus('myDivE<%=strNotId%>',<%=strNotId%>,<%=isMail%>,'isEmailApplicationShortlist');" />
					</span>
				</h3>
				<div class="box-tools pull-right">
					<button class="btn btn-box-tool" data-widget="collapse">
						<i class="fa fa-plus"></i>
					</button>
					<button class="btn btn-box-tool" data-widget="remove">
						<i class="fa fa-times"></i>
					</button>
				</div>
			</div>
			<!-- /.box-header -->
			<div class="box-body"
				style="padding: 5px; overflow-y: auto; display: none;">
				<div class="content1" style="margin: 0px 0px 10px 0px">
					<table class="table table_no_border" cellspacing="2"
						cellpadding="2" width="100%">
						<tr>
							<td valign="top" class="txtlabel alignRight">Email
								Notification:</td>
							<td><s:checkbox name="isEmailApplicationShortlist"
									id="isEmailApplicationShortlist" /></td>
						</tr>
						<%-- <tr>
							<td valign="top" class="txtlabel alignRight">Enable/Disable Text Notification:</td>
							<td><s:checkbox name="isTextApplicationShortlist"/></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Text Message</td>
							<td><s:textfield cssStyle="width:480px" name="strTextApplicationShortlist"/></td>
						</tr> --%>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Subject:</td>
							<td><s:textfield cssStyle="width:480px"
									name="strSubjectApplicationShortlist" /></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Body:</td>
							<td><div style="float: left; margin-right: 20px;">
									<s:textarea name="strBodyApplicationShortlist" cols="63"
										rows="10" />
								</div>
								<div style="float: left;">
									[CANDIFNAME] <br /> 
									[CANDILNAME] <br /> 
									[LEGAL_ENTITY_NAME] <br />
									<!-- Start Dattatray Date:10-08-21  -->
									[JOB_TITLE]<br /> 
									[RECRUITER_NAME]<br />
									<!-- End Dattatray Date:10-08-21  -->
									<!-- [RECRUITMENT_DESIG] <br/> -->
								</div></td>
						</tr>
					</table>
				</div>
			</div>
			<!-- /.box-body -->
		</div>


		<div class="box box-primary collapsed-box"
			style="border-top-color: #EEEEEE; margin-top: 10px;">

			<div class="box-header with-border">
				<h3 class="box-title" style="font-size: 14px; padding-right: 10px;">
					Send Candidate to Specify Date
					<%
					strNotId = "" + IConstants.N_CANDI_SPECIFY_OTHER_DATE;
								isMail = (Boolean) request.getAttribute("isEmailCandiSpecifyOtherDate");
								strEImage = isMail ? "mail_enbl.png" : "mail_disbl.png";
								strEEnable = isMail ? "Disable Mail Notification" : "Enable Mail Notification";

								if (isMail) {
									isMail = false;
								} else {
									isMail = true;
								}
				%>
					<span id="myDivE<%=strNotId%>" style="float: right"> <img
						width="20px" src="images1/<%=strEImage%>"
						title="<%=strEEnable%>"
						onclick="updateEmailStatus('myDivE<%=strNotId%>',<%=strNotId%>,<%=isMail%>,'isEmailCandiSpecifyOtherDate');" />
					</span>
				</h3>
				<div class="box-tools pull-right">
					<button class="btn btn-box-tool" data-widget="collapse">
						<i class="fa fa-plus"></i>
					</button>
					<button class="btn btn-box-tool" data-widget="remove">
						<i class="fa fa-times"></i>
					</button>
				</div>
			</div>
			<!-- /.box-header -->
			<div class="box-body"
				style="padding: 5px; overflow-y: auto; display: none;">
				<div class="content1" style="margin: 0px 0px 10px 0px">
					<table class="table table_no_border" cellspacing="2"
						cellpadding="2" width="100%">
						<tr>
							<td valign="top" class="txtlabel alignRight">Email
								Notification:</td>
							<td><s:checkbox name="isEmailCandiSpecifyOtherDate"
									id="isEmailCandiSpecifyOtherDate" /></td>
						</tr>
						<%-- <tr>
							<td valign="top" class="txtlabel alignRight">Enable/Disable Text Notification:</td>
							<td><s:checkbox name="isTextCandiSpecifyOtherDate"/></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Text Message</td>
							<td><s:textfield cssStyle="width:480px" name="strTextCandiSpecifyOtherDate"/></td>
						</tr> --%>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Subject:</td>
							<td><s:textfield cssStyle="width:480px"
									name="strSubjectCandiSpecifyOtherDate" /></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Body:</td>
							<td><div style="float: left; margin-right: 20px;">
									<s:textarea name="strBodyCandiSpecifyOtherDate" cols="63"
										rows="10" />
								</div>
								<div style="float: left;">
									[CANDIFNAME] <br /> [CANDILNAME] <br />
									[ADD_CANDIDATE_SPECIFY_OTHER_DATE] <br /> [LEGAL_ENTITY_NAME] <br />
								</div></td>
						</tr>
					</table>
				</div>
			</div>
			<!-- /.box-body -->
		</div>


		<div class="box box-primary collapsed-box"
			style="border-top-color: #EEEEEE; margin-top: 10px;">

			<div class="box-header with-border">
				<h3 class="box-title" style="font-size: 14px; padding-right: 10px;">
					Candidate Offer
					<%
					strNotId = "" + IConstants.N_CANDI_JOINING_OFFER_CTC;
								isMail = (Boolean) request.getAttribute("isEmailCandiJoiningOfferCTC");
								strEImage = isMail ? "mail_enbl.png" : "mail_disbl.png";
								strEEnable = isMail ? "Disable Mail Notification" : "Enable Mail Notification";

								if (isMail) {
									isMail = false;
								} else {
									isMail = true;
								}
				%>
					<span id="myDivE<%=strNotId%>" style="float: right"> <img
						width="20px" src="images1/<%=strEImage%>"
						title="<%=strEEnable%>"
						onclick="updateEmailStatus('myDivE<%=strNotId%>',<%=strNotId%>,<%=isMail%>,'isEmailCandiJoiningOfferCTC');" />
					</span>
				</h3>
				<div class="box-tools pull-right">
					<button class="btn btn-box-tool" data-widget="collapse">
						<i class="fa fa-plus"></i>
					</button>
					<button class="btn btn-box-tool" data-widget="remove">
						<i class="fa fa-times"></i>
					</button>
				</div>
			</div>
			<!-- /.box-header -->
			<div class="box-body"
				style="padding: 5px; overflow-y: auto; display: none;">
				<div class="content1" style="margin: 0px 0px 10px 0px">
					<table class="table table_no_border" cellspacing="2"
						cellpadding="2" width="100%">
						<tr>
							<td valign="top" class="txtlabel alignRight">Email
								Notification:</td>
							<td><s:checkbox name="isEmailCandiJoiningOfferCTC"
									id="isEmailCandiJoiningOfferCTC" /></td>
						</tr>
						<%-- <tr>
							<td valign="top" class="txtlabel alignRight">Enable/Disable Text Notification:</td>
							<td><s:checkbox name="isTextCandiJoiningOfferCTC"/></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Text Message</td>
							<td><s:textfield cssStyle="width:480px" name="strTextCandiJoiningOfferCTC"/></td>
						</tr> --%>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Subject:</td>
							<td><s:textfield cssStyle="width:480px"
									name="strSubjectCandiJoiningOfferCTC" /></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Body:</td>
							<td><div style="float: left; margin-right: 20px;">
									<s:textarea name="strBodyCandiJoiningOfferCTC" cols="63"
										rows="10" />
								</div>
								<div style="float: left;">
									[CANDIFNAME]<br /> [CANDILNAME] <br /> [CANDI_CTC] <br />
									[RECRUITMENT_DESIG] <br /> [CANDI_JOINING_DATE] <br />
									[LEGAL_ENTITY_NAME] <br />
									<!-- [CANDI_SALARY_BASIC]		[CANDI_SALARY_DEDUCT_PROFTAX] <br/>
									[CANDI_SALARY_HRA]			[CANDI_SALARY_DEDUCT_TDS] <br/> 
									[CANDI_SALARY_CONVALLOW]	[CANDI_SALARY_DEDUCT_PFEMPCONT]<br/>
									[CANDI_SALARY_OVERTIME]		[CANDI_SALARY_DEDUCT_PFEMPRCONT]<br/>
									[CANDI_SALARY_GRATUITY]		[CANDI_SALARY_DEDUCT_ESIEMPR]<br/>
									[CANDI_SALARY_BONUS]		[CANDI_SALARY_DEDUCT_ESIEMP] <br/>
									[CANDI_SALARY_MOBILEEXPENSES] [CANDI_SALARY_DEDUCT_LOAN]<br/>
									[CANDI_SALARY_MEDICALALLOW]	[CANDI_SALARY_SPECIALALLOW]<br/>
									[CANDI_SALARY_ARREAR_AND_OTHERALLOW][CANDI_TOTAL_GROSS_SALARY]<br/>
									[CANDI_TOTAL_DEDUCTION]		[CANDI_OFFER_ACCEPT_LINK]<br/> -->
								</div></td>
						</tr>
					</table>
				</div>
			</div>
			<!-- /.box-body -->
		</div>


		<div class="box box-primary collapsed-box"
			style="border-top-color: #EEEEEE; margin-top: 10px;">

			<div class="box-header with-border">
				<h3 class="box-title" style="font-size: 14px; padding-right: 10px;">
					Candidate Interview Date
					<%
					strNotId = "" + IConstants.N_INTERVIEW_DATE_MAIL_FOR_CANDI;
								isMail = (Boolean) request.getAttribute("isEmailCandiInterviewDate");
								strEImage = isMail ? "mail_enbl.png" : "mail_disbl.png";
								strEEnable = isMail ? "Disable Mail Notification" : "Enable Mail Notification";

								if (isMail) {
									isMail = false;
								} else {
									isMail = true;
								}
				%>
					<span id="myDivE<%=strNotId%>" style="float: right"> <img
						width="20px" src="images1/<%=strEImage%>"
						title="<%=strEEnable%>"
						onclick="updateEmailStatus('myDivE<%=strNotId%>',<%=strNotId%>,<%=isMail%>,'isEmailCandiInterviewDate');" />
					</span>
				</h3>
				<div class="box-tools pull-right">
					<button class="btn btn-box-tool" data-widget="collapse">
						<i class="fa fa-plus"></i>
					</button>
					<button class="btn btn-box-tool" data-widget="remove">
						<i class="fa fa-times"></i>
					</button>
				</div>
			</div>
			<!-- /.box-header -->
			<div class="box-body"
				style="padding: 5px; overflow-y: auto; display: none;">
				<div class="content1" style="margin: 0px 0px 10px 0px">
					<table class="table table_no_border" cellspacing="2"
						cellpadding="2" width="100%">
						<tr>
							<td valign="top" class="txtlabel alignRight">Email
								Notification:</td>
							<td><s:checkbox name="isEmailCandiInterviewDate"
									id="isEmailCandiInterviewDate" /></td>
						</tr>
						<%-- <tr>
							<td valign="top" class="txtlabel alignRight">Enable/Disable Text Notification:</td>
							<td><s:checkbox name="isTextCandiInterviewDate"/></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Text Message</td>
							<td><s:textfield cssStyle="width:480px" name="strTextCandiInterviewDate"/></td>
						</tr> --%>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Subject:</td>
							<td><s:textfield cssStyle="width:480px"
									name="strSubjectCandiInterviewDate" /></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Body:</td>
							<td><div style="float: left; margin-right: 20px;">
									<s:textarea name="strBodyCandiInterviewDate" cols="63"
										rows="10" />
								</div>
								<div style="float: left;">
									[CANDIFNAME] <br /> [CANDILNAME] <br /> [CANDI_INTRVIEW_DATE] <br />
									[LEGAL_ENTITY_NAME] <br />
									<!-- Start Dattatray Date:10-08-21  -->
									[JOB_POSITION] <br /> [ROUND_NUMBER] <br /> [RECRUITER_NAME] <br />
									[RECRUITER_SIGNATURE]
									<!-- End Dattatray Date:10-08-21  -->
								</div></td>
						</tr>
					</table>
				</div>
			</div>
			<!-- /.box-body -->
		</div>


		<div class="box box-primary collapsed-box"
			style="border-top-color: #EEEEEE; margin-top: 10px;">

			<div class="box-header with-border">
				<h3 class="box-title" style="font-size: 14px; padding-right: 10px;">
					Panelist Interview Date
					<%
					strNotId = "" + IConstants.N_INTERVIEW_DATE_MAIL_FOR_ROUNDEMP;
								isMail = (Boolean) request.getAttribute("isEmailPanelistInterviewDate");
								strEImage = isMail ? "mail_enbl.png" : "mail_disbl.png";
								strEEnable = isMail ? "Disable Mail Notification" : "Enable Mail Notification";

								if (isMail) {
									isMail = false;
								} else {
									isMail = true;
								}
				%>
					<span id="myDivE<%=strNotId%>" style="float: right"> <img
						width="20px" src="images1/<%=strEImage%>"
						title="<%=strEEnable%>"
						onclick="updateEmailStatus('myDivE<%=strNotId%>',<%=strNotId%>,<%=isMail%>,'isEmailPanelistInterviewDate');" />
					</span>
				</h3>
				<div class="box-tools pull-right">
					<button class="btn btn-box-tool" data-widget="collapse">
						<i class="fa fa-plus"></i>
					</button>
					<button class="btn btn-box-tool" data-widget="remove">
						<i class="fa fa-times"></i>
					</button>
				</div>
			</div>
			<!-- /.box-header -->
			<div class="box-body"
				style="padding: 5px; overflow-y: auto; display: none;">
				<div class="content1" style="margin: 0px 0px 10px 0px">
					<table class="table table_no_border" cellspacing="2"
						cellpadding="2" width="100%">
						<tr>
							<td valign="top" class="txtlabel alignRight">Email
								Notification:</td>
							<td><s:checkbox name="isEmailPanelistInterviewDate"
									id="isEmailPanelistInterviewDate" /></td>
						</tr>
						<%-- <tr>
							<td valign="top" class="txtlabel alignRight">Enable/Disable Text Notification:</td>
							<td><s:checkbox name="isTextPanelistInterviewDate"/></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Text Message</td>
							<td><s:textfield cssStyle="width:480px" name="strTextPanelistInterviewDate"/></td>
						</tr> --%>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Subject:</td>
							<td><s:textfield cssStyle="width:480px"
									name="strSubjectPanelistInterviewDate" /></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Body:</td>
							<td><div style="float: left; margin-right: 20px;">
									<s:textarea name="strBodyPanelistInterviewDate" cols="63"
										rows="10" />
								</div>
								<div style="float: left;">
									[EMPFNAME] <br /> [EMPLNAME] <br /> [CANDI_INTRVIEW_DATE] <br />
									[LEGAL_ENTITY_NAME] <br />
								</div></td>
						</tr>
					</table>
				</div>
			</div>
			<!-- /.box-body -->
		</div>


		<div class="box box-primary collapsed-box"
			style="border-top-color: #EEEEEE; margin-top: 10px;">

			<div class="box-header with-border">
				<h3 class="box-title" style="font-size: 14px; padding-right: 10px;">
					Candidate Onboarding
					<%
					strNotId = "" + IConstants.N_CANDI_ONBOARDING_CTC;
								isMail = (Boolean) request.getAttribute("isEmailCandiOnboarding");
								strEImage = isMail ? "mail_enbl.png" : "mail_disbl.png";
								strEEnable = isMail ? "Disable Mail Notification" : "Enable Mail Notification";

								if (isMail) {
									isMail = false;
								} else {
									isMail = true;
								}
				%>
					<span id="myDivE<%=strNotId%>" style="float: right"> <img
						width="20px" src="images1/<%=strEImage%>"
						title="<%=strEEnable%>"
						onclick="updateEmailStatus('myDivE<%=strNotId%>',<%=strNotId%>,<%=isMail%>,'isEmailCandiOnboarding');" />
					</span>
				</h3>
				<div class="box-tools pull-right">
					<button class="btn btn-box-tool" data-widget="collapse">
						<i class="fa fa-plus"></i>
					</button>
					<button class="btn btn-box-tool" data-widget="remove">
						<i class="fa fa-times"></i>
					</button>
				</div>
			</div>
			<!-- /.box-header -->
			<div class="box-body"
				style="padding: 5px; overflow-y: auto; display: none;">
				<div class="content1" style="margin: 0px 0px 10px 0px">
					<table class="table table_no_border" cellspacing="2"
						cellpadding="2" width="100%">
						<tr>
							<td valign="top" class="txtlabel alignRight">Email
								Notification:</td>
							<td><s:checkbox name="isEmailCandiOnboarding"
									id="isEmailCandiOnboarding" /></td>
						</tr>
						<%-- <tr>
							<td valign="top" class="txtlabel alignRight">Enable/Disable Text Notification:</td>
							<td><s:checkbox name="isTextCandiOnboarding"/></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Text Message</td>
							<td><s:textfield cssStyle="width:480px" name="strTextCandiOnboarding"/></td>
						</tr> --%>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Subject:</td>
							<td><s:textfield cssStyle="width:480px"
									name="strSubjectCandiOnboarding" /></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Body:</td>
							<td><div style="float: left; margin-right: 20px;">
									<s:textarea name="strBodyCandiOnboarding" cols="63" rows="10" />
								</div>
								<div style="float: left;">
									[CANDIFNAME] <br /> [CANDILNAME] <br /> [CANDI_ONBOARDING_LINK]
									<br /> [LEGAL_ENTITY_NAME] <br /> [JOB_TITLE]<br />
									[CANDI_JOINING_DATE] <br />
									<!-- Created by Dattatray Date : 10-08-21  -->
								</div></td>
						</tr>
					</table>
				</div>
			</div>
			<!-- /.box-body -->
		</div>


		<div class="box box-primary collapsed-box"
			style="border-top-color: #EEEEEE; margin-top: 10px;">

			<div class="box-header with-border">
				<h3 class="box-title" style="font-size: 14px; padding-right: 10px;">
					Candidate Employee Login Detail
					<%
					strNotId = "" + IConstants.N_EMP_LOGIN_DETAILS;
								isMail = (Boolean) request.getAttribute("isEmailCandiEmpLoginDetail");
								strEImage = isMail ? "mail_enbl.png" : "mail_disbl.png";
								strEEnable = isMail ? "Disable Mail Notification" : "Enable Mail Notification";

								if (isMail) {
									isMail = false;
								} else {
									isMail = true;
								}
				%>
					<span id="myDivE<%=strNotId%>" style="float: right"> <img
						width="20px" src="images1/<%=strEImage%>"
						title="<%=strEEnable%>"
						onclick="updateEmailStatus('myDivE<%=strNotId%>',<%=strNotId%>,<%=isMail%>,'isEmailCandiEmpLoginDetail');" />
					</span>
				</h3>
				<div class="box-tools pull-right">
					<button class="btn btn-box-tool" data-widget="collapse">
						<i class="fa fa-plus"></i>
					</button>
					<button class="btn btn-box-tool" data-widget="remove">
						<i class="fa fa-times"></i>
					</button>
				</div>
			</div>
			<!-- /.box-header -->
			<div class="box-body"
				style="padding: 5px; overflow-y: auto; display: none;">
				<div class="content1" style="margin: 0px 0px 10px 0px">
					<table class="table table_no_border" cellspacing="2"
						cellpadding="2" width="100%">
						<tr>
							<td valign="top" class="txtlabel alignRight">Email
								Notification:</td>
							<td><s:checkbox name="isEmailCandiEmpLoginDetail"
									id="isEmailCandiEmpLoginDetail" /></td>
						</tr>
						<%-- <tr>
							<td valign="top" class="txtlabel alignRight">Enable/Disable Text Notification:</td>
							<td><s:checkbox name="isTextCandiEmpLoginDetail"/></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Text Message</td>
							<td><s:textfield cssStyle="width:480px" name="strTextCandiEmpLoginDetail"/></td>
						</tr> --%>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Subject:</td>
							<td><s:textfield cssStyle="width:480px"
									name="strSubjectCandiEmpLoginDetail" /></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Body:</td>
							<td><div style="float: left; margin-right: 20px;">
									<s:textarea name="strBodyCandiEmpLoginDetail" cols="63"
										rows="10" />
								</div>
								<div style="float: left;">
									[EMPFNAME] <br /> [EMPLNAME] <br /> [CANDI_USERNAME] <br />
									[CANDI_PASSWORD] <br /> [LEGAL_ENTITY_NAME] <br />
								</div></td>
						</tr>
					</table>
				</div>
			</div>
			<!-- /.box-body -->
		</div>


		<div class="box box-primary collapsed-box"
			style="border-top-color: #EEEEEE; margin-top: 10px;">
			<div class="box-header with-border">
				<h3 class="box-title" style="font-size: 14px; padding-right: 10px;">
					Candidate Information Form
					<%
					strNotId = "" + IConstants.N_NEW_CADIDATE_ADD;
								isMail = (Boolean) request.getAttribute("isEmailFillCandiInfoByCandi");
								strEImage = isMail ? "mail_enbl.png" : "mail_disbl.png";
								strEEnable = isMail ? "Disable Mail Notification" : "Enable Mail Notification";

								if (isMail) {
									isMail = false;
								} else {
									isMail = true;
								}
				%>
					<span id="myDivE<%=strNotId%>" style="float: right"> <img
						width="20px" src="images1/<%=strEImage%>"
						title="<%=strEEnable%>"
						onclick="updateEmailStatus('myDivE<%=strNotId%>',<%=strNotId%>,<%=isMail%>,'isEmailFillCandiInfoByCandi');" />
					</span>
				</h3>
				<div class="box-tools pull-right">
					<button class="btn btn-box-tool" data-widget="collapse">
						<i class="fa fa-plus"></i>
					</button>
					<button class="btn btn-box-tool" data-widget="remove">
						<i class="fa fa-times"></i>
					</button>
				</div>
			</div>
			<!-- /.box-header -->
			<div class="box-body"
				style="padding: 5px; overflow-y: auto; display: none;">
				<div class="content1" style="margin: 0px 0px 10px 0px">
					<table class="table table_no_border" cellspacing="2"
						cellpadding="2" width="100%">
						<tr>
							<td valign="top" class="txtlabel alignRight">Email
								Notification:</td>
							<td><s:checkbox name="isEmailFillCandiInfoByCandi"
									id="isEmailFillCandiInfoByCandi" /></td>
						</tr>
						<%-- <tr>
							<td valign="top" class="txtlabel alignRight">Enable/Disable Text Notification:</td>
							<td><s:checkbox name="isTextFillCandiInfoByCandi"/></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Text Message</td>
							<td><s:textfield cssStyle="width:480px" name="strTextFillCandiInfoByCandi"/></td>
						</tr> --%>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Subject:</td>
							<td><s:textfield cssStyle="width:480px"
									name="strSubjectFillCandiInfoByCandi" /></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Body:</td>
							<td><div style="float: left; margin-right: 20px;">
									<s:textarea name="strBodyFillCandiInfoByCandi" cols="63"
										rows="10" />
								</div>
								<div style="float: left;">
									[CANDIFNAME] <br /> [CANDILNAME] <br /> [CANDI_ADD_LINK] <br />
									[LEGAL_ENTITY_NAME] <br />
								</div></td>
						</tr>
					</table>
				</div>
			</div>
			<!-- /.box-body -->
		</div>


		<div class="box box-primary collapsed-box"
			style="border-top-color: #EEEEEE; margin-top: 10px;">
			<div class="box-header with-border">
				<h3 class="box-title" style="font-size: 14px; padding-right: 10px;">
					Candidate Backgroud Verification
					<%
					strNotId = "" + IConstants.N_NEW_CADIDATE_BACKGROUND_VERIFICATION;
								isMail = (Boolean) request.getAttribute("isEmailCandiBackgroudVerification");
								strEImage = isMail ? "mail_enbl.png" : "mail_disbl.png";
								strEEnable = isMail ? "Disable Mail Notification" : "Enable Mail Notification";

								if (isMail) {
									isMail = false;
								} else {
									isMail = true;
								}
				%>
					<span id="myDivE<%=strNotId%>" style="float: right"> <img
						width="20px" src="images1/<%=strEImage%>"
						title="<%=strEEnable%>"
						onclick="updateEmailStatus('myDivE<%=strNotId%>',<%=strNotId%>,<%=isMail%>,'isEmailCandiBackgroudVerification');" />
					</span>
				</h3>
				<div class="box-tools pull-right">
					<button class="btn btn-box-tool" data-widget="collapse">
						<i class="fa fa-plus"></i>
					</button>
					<button class="btn btn-box-tool" data-widget="remove">
						<i class="fa fa-times"></i>
					</button>
				</div>
			</div>
			<!-- /.box-header -->
			<div class="box-body"
				style="padding: 5px; overflow-y: auto; display: none;">
				<div class="content1" style="margin: 0px 0px 10px 0px">
					<table class="table table_no_border" cellspacing="2"
						cellpadding="2" width="100%">
						<tr>
							<td valign="top" class="txtlabel alignRight">Email
								Notification:</td>
							<td><s:checkbox name="isEmailCandiBackgroudVerification"
									id="isEmailCandiBackgroudVerification" /></td>
						</tr>
						<%-- <tr>
							<td valign="top" class="txtlabel alignRight">Enable/Disable Text Notification:</td>
							<td><s:checkbox name="isTextCandiBackgroudVerification"/></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Text Message</td>
							<td><s:textfield cssStyle="width:480px" name="strTextCandiBackgroudVerification"/></td>
						</tr> --%>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Subject:</td>
							<td><s:textfield cssStyle="width:480px"
									name="strSubjectCandiBackgroudVerification" /></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Body:</td>
							<td><div style="float: left; margin-right: 20px;">
									<s:textarea name="strBodyCandiBackgroudVerification" cols="63"
										rows="10" />
								</div>
								<div style="float: left;">
									[CANDIFNAME] <br /> [CANDILNAME] <br />
									[CANDI_BACKGROUND_VERIFICATION_LINK] <br /> [LEGAL_ENTITY_NAME]
									<br />
								</div></td>
						</tr>
					</table>
				</div>
			</div>
			<!-- /.box-body -->
		</div>


		<div class="box box-primary collapsed-box"
			style="border-top-color: #EEEEEE; margin-top: 10px;">

			<div class="box-header with-border">
				<h3 class="box-title" style="font-size: 14px; padding-right: 10px;">
					Recruitment Request Update
					<%
					strNotId = "" + IConstants.N_RECRUITMENT_REQUEST_EDIT;
								isMail = (Boolean) request.getAttribute("isEmailRecruitmentRequestUpdate");
								strEImage = isMail ? "mail_enbl.png" : "mail_disbl.png";
								strEEnable = isMail ? "Disable Mail Notification" : "Enable Mail Notification";

								if (isMail) {
									isMail = false;
								} else {
									isMail = true;
								}
				%>
					<span id="myDivE<%=strNotId%>" style="float: right"> <img
						width="20px" src="images1/<%=strEImage%>"
						title="<%=strEEnable%>"
						onclick="updateEmailStatus('myDivE<%=strNotId%>',<%=strNotId%>,<%=isMail%>,'isEmailRecruitmentRequestUpdate');" />
					</span>
				</h3>
				<div class="box-tools pull-right">
					<button class="btn btn-box-tool" data-widget="collapse">
						<i class="fa fa-plus"></i>
					</button>
					<button class="btn btn-box-tool" data-widget="remove">
						<i class="fa fa-times"></i>
					</button>
				</div>
			</div>
			<!-- /.box-header -->
			<div class="box-body"
				style="padding: 5px; overflow-y: auto; display: none;">
				<div class="content1" style="margin: 0px 0px 10px 0px">
					<table class="table table_no_border" cellspacing="2"
						cellpadding="2" width="100%">
						<tr>
							<td valign="top" class="txtlabel alignRight">Email
								Notification:</td>
							<td><s:checkbox name="isEmailRecruitmentRequestUpdate"
									id="isEmailRecruitmentRequestUpdate" /></td>
						</tr>
						<%-- <tr>
							<td valign="top" class="txtlabel alignRight">Enable/Disable Text Notification:</td>
							<td><s:checkbox name="isTextRecruitmentRequestUpdate"/></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Text Message</td>
							<td><s:textfield cssStyle="width:480px" name="strTextRecruitmentRequestUpdate"/></td>
						</tr> --%>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Subject:</td>
							<td><s:textfield cssStyle="width:480px"
									name="strSubjectRecruitmentRequestUpdate" /></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Body:</td>
							<td><div style="float: left; margin-right: 20px;">
									<s:textarea name="strBodyRecruitmentRequestUpdate" cols="63"
										rows="10" />
								</div>
								<div style="float: left;">
									[EMPFNAME] <br /> [EMPLNAME] <br /> [RECRUITMENT_DESIG] <br />
									[LEGAL_ENTITY_NAME] <br />
								</div></td>
						</tr>
					</table>
				</div>
			</div>
			<!-- /.box-body -->
		</div>


		<div class="box box-primary collapsed-box"
			style="border-top-color: #EEEEEE; margin-top: 10px;">

			<div class="box-header with-border">
				<h3 class="box-title" style="font-size: 14px; padding-right: 10px;">
					Candidate Offer Accept/Reject
					<%
					strNotId = "" + IConstants.N_CANDI_OFFER_ACCEPT_REJECT;
								isMail = (Boolean) request.getAttribute("isEmailCandiOfferAcceptReject");
								strEImage = isMail ? "mail_enbl.png" : "mail_disbl.png";
								strEEnable = isMail ? "Disable Mail Notification" : "Enable Mail Notification";

								if (isMail) {
									isMail = false;
								} else {
									isMail = true;
								}
				%>
					<span id="myDivE<%=strNotId%>" style="float: right"> <img
						width="20px" src="images1/<%=strEImage%>"
						title="<%=strEEnable%>"
						onclick="updateEmailStatus('myDivE<%=strNotId%>',<%=strNotId%>,<%=isMail%>,'isEmailCandiOfferAcceptReject');" />
					</span>
				</h3>
				<div class="box-tools pull-right">
					<button class="btn btn-box-tool" data-widget="collapse">
						<i class="fa fa-plus"></i>
					</button>
					<button class="btn btn-box-tool" data-widget="remove">
						<i class="fa fa-times"></i>
					</button>
				</div>
			</div>
			<!-- /.box-header -->
			<div class="box-body"
				style="padding: 5px; overflow-y: auto; display: none;">
				<div class="content1" style="margin: 0px 0px 10px 0px">
					<table class="table table_no_border" cellspacing="2"
						cellpadding="2" width="100%">
						<tr>
							<td valign="top" class="txtlabel alignRight">Email
								Notification:</td>
							<td><s:checkbox name="isEmailCandiOfferAcceptReject"
									id="isEmailCandiOfferAcceptReject" /></td>
						</tr>
						<%-- <tr>
							<td valign="top" class="txtlabel alignRight">Enable/Disable Text Notification:</td>
							<td><s:checkbox name="isTextCandiOfferAcceptReject"/></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Text Message</td>
							<td><s:textfield cssStyle="width:480px" name="strTextCandiOfferAcceptReject"/></td>
						</tr> --%>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Subject:</td>
							<td><s:textfield cssStyle="width:480px"
									name="strSubjectCandiOfferAcceptReject" /></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Body:</td>
							<td><div style="float: left; margin-right: 20px;">
									<s:textarea name="strBodyCandiOfferAcceptReject" cols="63"
										rows="10" />
								</div>
								<div style="float: left;">
									[EMPFNAME] <br /> [EMPLNAME] <br /> [CANDIFNAME] <br />
									[CANDILNAME] <br /> [RECRUITMENT_DESIG] <br />
									[LEGAL_ENTITY_NAME] <br />
								</div></td>
						</tr>
					</table>
				</div>
			</div>
			<!-- /.box-body -->
		</div>


		<div class="box box-primary collapsed-box"
			style="border-top-color: #EEEEEE; margin-top: 10px;">

			<div class="box-header with-border">
				<h3 class="box-title" style="font-size: 14px; padding-right: 10px;">
					New Employee Onboarding HR
					<%
					strNotId = "" + IConstants.N_EMP_ONBOARDING_TO_HR;
								isMail = (Boolean) request.getAttribute("isEmailNewEmpOnboardingToHr");
								strEImage = isMail ? "mail_enbl.png" : "mail_disbl.png";
								strEEnable = isMail ? "Disable Mail Notification" : "Enable Mail Notification";

								if (isMail) {
									isMail = false;
								} else {
									isMail = true;
								}
				%>
					<span id="myDivE<%=strNotId%>" style="float: right"> <img
						width="20px" src="images1/<%=strEImage%>"
						title="<%=strEEnable%>"
						onclick="updateEmailStatus('myDivE<%=strNotId%>',<%=strNotId%>,<%=isMail%>,'isEmailNewEmpOnboardingToHr');" />
					</span>
				</h3>
				<div class="box-tools pull-right">
					<button class="btn btn-box-tool" data-widget="collapse">
						<i class="fa fa-plus"></i>
					</button>
					<button class="btn btn-box-tool" data-widget="remove">
						<i class="fa fa-times"></i>
					</button>
				</div>
			</div>
			<!-- /.box-header -->
			<div class="box-body"
				style="padding: 5px; overflow-y: auto; display: none;">
				<div class="content1" style="margin: 0px 0px 10px 0px">
					<table class="table table_no_border" cellspacing="2"
						cellpadding="2" width="100%">
						<tr>
							<td valign="top" class="txtlabel alignRight">Email
								Notification:</td>
							<td><s:checkbox name="isEmailNewEmpOnboardingToHr"
									id="isEmailNewEmpOnboardingToHr" /></td>
						</tr>
						<%-- <tr>
							<td valign="top" class="txtlabel alignRight">Enable/Disable Text Notification:</td>
							<td><s:checkbox name="isTextNewEmpOnboardingToHr"/></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Text Message</td>
							<td><s:textfield cssStyle="width:480px" name="strTextNewEmpOnboardingToHr"/></td>
						</tr> --%>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Subject:</td>
							<td><s:textfield cssStyle="width:480px"
									name="strSubjectNewEmpOnboardingToHr" /></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Body:</td>
							<td><div style="float: left; margin-right: 20px;">
									<s:textarea name="strBodyNewEmpOnboardingToHr" cols="63"
										rows="10" />
								</div>
								<div style="float: left;">
									[EMPFNAME] <br /> [EMPLNAME] <br /> [CANDIFNAME] <br />
									[CANDILNAME] <br /> [RECRUITMENT_DESIG] <br />
									[LEGAL_ENTITY_NAME] <br />
								</div></td>
						</tr>
					</table>
				</div>
			</div>
			<!-- /.box-body -->
		</div>


		<div class="box box-primary collapsed-box"
			style="border-top-color: #EEEEEE; margin-top: 10px;">

			<div class="box-header with-border">
				<h3 class="box-title" style="font-size: 14px; padding-right: 10px;">
					Recruitment Request Denied
					<%
					strNotId = "" + IConstants.N_RECRUITMENT_DENY;
								isMail = (Boolean) request.getAttribute("isEmailRecruitmentRequestDeny");
								strEImage = isMail ? "mail_enbl.png" : "mail_disbl.png";
								strEEnable = isMail ? "Disable Mail Notification" : "Enable Mail Notification";

								if (isMail) {
									isMail = false;
								} else {
									isMail = true;
								}
				%>
					<span id="myDivE<%=strNotId%>" style="float: right"> <img
						width="20px" src="images1/<%=strEImage%>"
						title="<%=strEEnable%>"
						onclick="updateEmailStatus('myDivE<%=strNotId%>',<%=strNotId%>,<%=isMail%>,'isEmailRecruitmentRequestDeny');" />
					</span>
				</h3>
				<div class="box-tools pull-right">
					<button class="btn btn-box-tool" data-widget="collapse">
						<i class="fa fa-plus"></i>
					</button>
					<button class="btn btn-box-tool" data-widget="remove">
						<i class="fa fa-times"></i>
					</button>
				</div>
			</div>
			<!-- /.box-header -->
			<div class="box-body"
				style="padding: 5px; overflow-y: auto; display: none;">
				<div class="content1" style="margin: 0px 0px 10px 0px">
					<table class="table table_no_border" cellspacing="2"
						cellpadding="2" width="100%">
						<tr>
							<td valign="top" class="txtlabel alignRight">Email
								Notification:</td>
							<td><s:checkbox name="isEmailRecruitmentRequestDeny"
									id="isEmailRecruitmentRequestDeny" /></td>
						</tr>
						<%-- <tr>
							<td valign="top" class="txtlabel alignRight">Enable/Disable Text Notification:</td>
							<td><s:checkbox name="isTextRecruitmentRequestDeny"/></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Text Message</td>
							<td><s:textfield cssStyle="width:480px" name="strTextRecruitmentRequestDeny"/></td>
						</tr> --%>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Subject:</td>
							<td><s:textfield cssStyle="width:480px"
									name="strSubjectRecruitmentRequestDeny" /></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Body:</td>
							<td><div style="float: left; margin-right: 20px;">
									<s:textarea name="strBodyRecruitmentRequestDeny" cols="63"
										rows="10" />
								</div>
								<div style="float: left;">
									[EMPFNAME] <br /> [EMPLNAME] <br /> [RECRUITMENT_DESIG] <br />
									[LEGAL_ENTITY_NAME] <br />
								</div></td>
						</tr>
					</table>
				</div>
			</div>
			<!-- /.box-body -->
		</div>


		<div class="box box-primary collapsed-box"
			style="border-top-color: #EEEEEE; margin-top: 10px;">

			<div class="box-header with-border">
				<h3 class="box-title" style="font-size: 14px; padding-right: 10px;">
					Job Profile Approval
					<%
					strNotId = "" + IConstants.N_JOB_PROFILE_APPROVAL;
								isMail = (Boolean) request.getAttribute("isEmailJobProfileApproval");
								strEImage = isMail ? "mail_enbl.png" : "mail_disbl.png";
								strEEnable = isMail ? "Disable Mail Notification" : "Enable Mail Notification";

								if (isMail) {
									isMail = false;
								} else {
									isMail = true;
								}
				%>
					<span id="myDivE<%=strNotId%>" style="float: right"> <img
						width="20px" src="images1/<%=strEImage%>"
						title="<%=strEEnable%>"
						onclick="updateEmailStatus('myDivE<%=strNotId%>',<%=strNotId%>,<%=isMail%>,'isEmailJobProfileApproval');" />
					</span>
				</h3>
				<div class="box-tools pull-right">
					<button class="btn btn-box-tool" data-widget="collapse">
						<i class="fa fa-plus"></i>
					</button>
					<button class="btn btn-box-tool" data-widget="remove">
						<i class="fa fa-times"></i>
					</button>
				</div>
			</div>
			<!-- /.box-header -->
			<div class="box-body"
				style="padding: 5px; overflow-y: auto; display: none;">
				<div class="content1" style="margin: 0px 0px 10px 0px">
					<table class="table table_no_border" cellspacing="2"
						cellpadding="2" width="100%">
						<tr>
							<td valign="top" class="txtlabel alignRight">Email
								Notification:</td>
							<td><s:checkbox name="isEmailJobProfileApproval"
									id="isEmailJobProfileApproval" /></td>
						</tr>
						<%-- <tr>
							<td valign="top" class="txtlabel alignRight">Enable/Disable Text Notification:</td>
							<td><s:checkbox name="isTextJobProfileApproval"/></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Text Message</td>
							<td><s:textfield cssStyle="width:480px" name="strTextJobProfileApproval"/></td>
						</tr> --%>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Subject:</td>
							<td><s:textfield cssStyle="width:480px"
									name="strSubjectJobProfileApproval" /></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Body:</td>
							<td><div style="float: left; margin-right: 20px;">
									<s:textarea name="strBodyJobProfileApproval" cols="63"
										rows="10" />
								</div>
								<div style="float: left;">
									[EMPFNAME] <br /> [EMPLNAME] <br /> [RECRUITMENT_DESIG] <br />
									[LEGAL_ENTITY_NAME] <br />
								</div></td>
						</tr>
					</table>
				</div>
			</div>
			<!-- /.box-body -->
		</div>


		<div class="box box-primary collapsed-box"
			style="border-top-color: #EEEEEE; margin-top: 10px;">

			<div class="box-header with-border">
				<h3 class="box-title" style="font-size: 14px; padding-right: 10px;">
					Job Profile Denied
					<%
					strNotId = "" + IConstants.N_JOB_PROFILE_DENY;
								isMail = (Boolean) request.getAttribute("isEmailJobProfileDeny");
								strEImage = isMail ? "mail_enbl.png" : "mail_disbl.png";
								strEEnable = isMail ? "Disable Mail Notification" : "Enable Mail Notification";

								if (isMail) {
									isMail = false;
								} else {
									isMail = true;
								}
				%>
					<span id="myDivE<%=strNotId%>" style="float: right"> <img
						width="20px" src="images1/<%=strEImage%>"
						title="<%=strEEnable%>"
						onclick="updateEmailStatus('myDivE<%=strNotId%>',<%=strNotId%>,<%=isMail%>,'isEmailJobProfileDeny');" />
					</span>
				</h3>
				<div class="box-tools pull-right">
					<button class="btn btn-box-tool" data-widget="collapse">
						<i class="fa fa-plus"></i>
					</button>
					<button class="btn btn-box-tool" data-widget="remove">
						<i class="fa fa-times"></i>
					</button>
				</div>
			</div>
			<!-- /.box-header -->
			<div class="box-body"
				style="padding: 5px; overflow-y: auto; display: none;">
				<div class="content1" style="margin: 0px 0px 10px 0px">
					<table class="table table_no_border" cellspacing="2"
						cellpadding="2" width="100%">
						<tr>
							<td valign="top" class="txtlabel alignRight">Email
								Notification:</td>
							<td><s:checkbox name="isEmailJobProfileDeny"
									id="isEmailJobProfileDeny" /></td>
						</tr>
						<%-- <tr>
							<td valign="top" class="txtlabel alignRight">Enable/Disable Text Notification:</td>
							<td><s:checkbox name="isTextJobProfileDeny"/></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Text Message</td>
							<td><s:textfield cssStyle="width:480px" name="strTextJobProfileDeny"/></td>
						</tr> --%>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Subject:</td>
							<td><s:textfield cssStyle="width:480px"
									name="strSubjectJobProfileDeny" /></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Body:</td>
							<td><div style="float: left; margin-right: 20px;">
									<s:textarea name="strBodyJobProfileDeny" cols="63" rows="10" />
								</div>
								<div style="float: left;">
									[EMPFNAME] <br /> [EMPLNAME] <br /> [RECRUITMENT_DESIG] <br />
									[LEGAL_ENTITY_NAME] <br />
								</div></td>
						</tr>
					</table>
				</div>

			</div>
			<!-- /.box-body -->
		</div>

		<div class="box box-primary collapsed-box"
			style="border-top-color: #EEEEEE; margin-top: 10px;">

			<div class="box-header with-border">
				<h3 class="box-title" style="font-size: 14px; padding-right: 10px;">
					Job Profile Updated
					<%
					strNotId = "" + IConstants.N_JOB_PROFILE_UPDATE;
								isMail = (Boolean) request.getAttribute("isEmailJobProfileUpdate");
								strEImage = isMail ? "mail_enbl.png" : "mail_disbl.png";
								strEEnable = isMail ? "Disable Mail Notification" : "Enable Mail Notification";

								if (isMail) {
									isMail = false;
								} else {
									isMail = true;
								}
				%>
					<span id="myDivE<%=strNotId%>" style="float: right"> <img
						width="20px" src="images1/<%=strEImage%>"
						title="<%=strEEnable%>"
						onclick="updateEmailStatus('myDivE<%=strNotId%>',<%=strNotId%>,<%=isMail%>,'isEmailJobProfileUpdate');" />
					</span>
				</h3>
				<div class="box-tools pull-right">
					<button class="btn btn-box-tool" data-widget="collapse">
						<i class="fa fa-plus"></i>
					</button>
					<button class="btn btn-box-tool" data-widget="remove">
						<i class="fa fa-times"></i>
					</button>
				</div>
			</div>
			<!-- /.box-header -->
			<div class="box-body"
				style="padding: 5px; overflow-y: auto; display: none;">
				<div class="content1" style="margin: 0px 0px 10px 0px">
					<table class="table table_no_border" cellspacing="2"
						cellpadding="2" width="100%">
						<tr>
							<td valign="top" class="txtlabel alignRight">Email
								Notification:</td>
							<td><s:checkbox name="isEmailJobProfileUpdate"
									id="isEmailJobProfileUpdate" /></td>
						</tr>
						<%-- <tr>
							<td valign="top" class="txtlabel alignRight">Enable/Disable Text Notification:</td>
							<td><s:checkbox name="isTextJobProfileUpdate"/></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Text Message</td>
							<td><s:textfield cssStyle="width:480px" name="strTextJobProfileUpdate"/></td>
						</tr> --%>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Subject:</td>
							<td><s:textfield cssStyle="width:480px"
									name="strSubjectJobProfileUpdate" /></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Body:</td>
							<td><div style="float: left; margin-right: 20px;">
									<s:textarea name="strBodyJobProfileUpdate" cols="63" rows="10" />
								</div>
								<div style="float: left;">
									[EMPFNAME] <br /> [EMPLNAME] <br /> [RECRUITMENT_DESIG] <br />
									[LEGAL_ENTITY_NAME] <br />
								</div></td>
						</tr>
					</table>
				</div>
			</div>
			<!-- /.box-body -->
		</div>


		<div class="box box-primary collapsed-box"
			style="border-top-color: #EEEEEE; margin-top: 10px;">

			<div class="box-header with-border">
				<h3 class="box-title" style="font-size: 14px; padding-right: 10px;">
					Send New Job To Consultant
					<%
					strNotId = "" + IConstants.N_HIRING_LINK_FOR_CONSULTANT;
								isMail = (Boolean) request.getAttribute("isEmailNewJobToConsultant");
								strEImage = isMail ? "mail_enbl.png" : "mail_disbl.png";
								strEEnable = isMail ? "Disable Mail Notification" : "Enable Mail Notification";

								if (isMail) {
									isMail = false;
								} else {
									isMail = true;
								}
				%>
					<span id="myDivE<%=strNotId%>" style="float: right"> <img
						width="20px" src="images1/<%=strEImage%>"
						title="<%=strEEnable%>"
						onclick="updateEmailStatus('myDivE<%=strNotId%>',<%=strNotId%>,<%=isMail%>,'isEmailNewJobToConsultant');" />
					</span>
				</h3>
				<div class="box-tools pull-right">
					<button class="btn btn-box-tool" data-widget="collapse">
						<i class="fa fa-plus"></i>
					</button>
					<button class="btn btn-box-tool" data-widget="remove">
						<i class="fa fa-times"></i>
					</button>
				</div>
			</div>
			<!-- /.box-header -->
			<div class="box-body"
				style="padding: 5px; overflow-y: auto; display: none;">
				<div class="content1" style="margin: 0px 0px 10px 0px">
					<table class="table table_no_border" cellspacing="2"
						cellpadding="2" width="100%">
						<tr>
							<td valign="top" class="txtlabel alignRight">Email
								Notification:</td>
							<td><s:checkbox name="isEmailNewJobToConsultant"
									id="isEmailNewJobToConsultant" /></td>
						</tr>
						<%-- <tr>
							<td valign="top" class="txtlabel alignRight">Enable/Disable Text Notification:</td>
							<td><s:checkbox name="isTextNewJobToConsultant"/></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Text Message</td>
							<td><s:textfield cssStyle="width:480px" name="strTextNewJobToConsultant"/></td>
						</tr> --%>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Subject:</td>
							<td><s:textfield cssStyle="width:480px"
									name="strSubjectNewJobToConsultant" /></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Body:</td>
							<td><div style="float: left; margin-right: 20px;">
									<s:textarea name="strBodyNewJobToConsultant" cols="63"
										rows="10" />
								</div>
								<div style="float: left;">
									[EMPFNAME] <br /> [EMPLNAME] <br /> [RECRUITMENT_DESIG] <br />
									[LEGAL_ENTITY_NAME] <br />
								</div></td>
						</tr>
					</table>
				</div>
			</div>
			<!-- /.box-body -->
		</div>

		<div class="box box-primary collapsed-box"
			style="border-top-color: #EEEEEE; margin-top: 10px;">

			<div class="box-header with-border">
				<h3 class="box-title" style="font-size: 14px; padding-right: 10px;">
					Application(Candidate) Shortlist From Consultant
					<%
					strNotId = "" + IConstants.N_CANDI_SHORTLIST_FROM_CONSULTANT;
								isMail = (Boolean) request.getAttribute("isEmailCandiShortlistFromConsultant");
								strEImage = isMail ? "mail_enbl.png" : "mail_disbl.png";
								strEEnable = isMail ? "Disable Mail Notification" : "Enable Mail Notification";

								if (isMail) {
									isMail = false;
								} else {
									isMail = true;
								}
				%>
					<span id="myDivE<%=strNotId%>" style="float: right"> <img
						width="20px" src="images1/<%=strEImage%>"
						title="<%=strEEnable%>"
						onclick="updateEmailStatus('myDivE<%=strNotId%>',<%=strNotId%>,<%=isMail%>,'isEmailCandiShortlistFromConsultant');" />
					</span>
				</h3>
				<div class="box-tools pull-right">
					<button class="btn btn-box-tool" data-widget="collapse">
						<i class="fa fa-plus"></i>
					</button>
					<button class="btn btn-box-tool" data-widget="remove">
						<i class="fa fa-times"></i>
					</button>
				</div>
			</div>
			<!-- /.box-header -->
			<div class="box-body"
				style="padding: 5px; overflow-y: auto; display: none;">
				<div class="content1" style="margin: 0px 0px 10px 0px">
					<table class="table table_no_border" cellspacing="2"
						cellpadding="2" width="100%">
						<tr>
							<td valign="top" class="txtlabel alignRight">Email
								Notification:</td>
							<td><s:checkbox name="isEmailCandiShortlistFromConsultant"
									id="isEmailCandiShortlistFromConsultant" /></td>
						</tr>
						<%-- <tr>
							<td valign="top" class="txtlabel alignRight">Enable/Disable Text Notification:</td>
							<td><s:checkbox name="isTextCandiShortlistFromConsultant"/></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Text Message</td>
							<td><s:textfield cssStyle="width:480px" name="strTextCandiShortlistFromConsultant"/></td>
						</tr> --%>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Subject:</td>
							<td><s:textfield cssStyle="width:480px"
									name="strSubjectCandiShortlistFromConsultant" /></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Body:</td>
							<td><div style="float: left; margin-right: 20px;">
									<s:textarea name="strBodyCandiShortlistFromConsultant"
										cols="63" rows="10" />
								</div>
								<div style="float: left;">
									[EMPFNAME] <br /> [EMPLNAME] <br /> [CANDIFNAME] <br />
									[CANDILNAME] <br /> [LEGAL_ENTITY_NAME] <br />
								</div></td>
						</tr>
					</table>
				</div>
			</div>
			<!-- /.box-body -->
		</div>


		<div class="box box-primary collapsed-box"
			style="border-top-color: #EEEEEE; margin-top: 10px;">

			<div class="box-header with-border">
				<h3 class="box-title" style="font-size: 14px; padding-right: 10px;">
					Candidate Finalization
					<%
					strNotId = "" + IConstants.N_CANDI_FINALIZATION_FROM_EMP;
								isMail = (Boolean) request.getAttribute("isEmailCandiFinalize");
								strEImage = isMail ? "mail_enbl.png" : "mail_disbl.png";
								strEEnable = isMail ? "Disable Mail Notification" : "Enable Mail Notification";

								if (isMail) {
									isMail = false;
								} else {
									isMail = true;
								}
				%>
					<span id="myDivE<%=strNotId%>" style="float: right"> <img
						width="20px" src="images1/<%=strEImage%>"
						title="<%=strEEnable%>"
						onclick="updateEmailStatus('myDivE<%=strNotId%>',<%=strNotId%>,<%=isMail%>,'isEmailCandiFinalize');" />
					</span>
				</h3>
				<div class="box-tools pull-right">
					<button class="btn btn-box-tool" data-widget="collapse">
						<i class="fa fa-plus"></i>
					</button>
					<button class="btn btn-box-tool" data-widget="remove">
						<i class="fa fa-times"></i>
					</button>
				</div>
			</div>
			<!-- /.box-header -->
			<div class="box-body"
				style="padding: 5px; overflow-y: auto; display: none;">
				<div class="content1" style="margin: 0px 0px 10px 0px">
					<table class="table table_no_border" cellspacing="2"
						cellpadding="2" width="100%">
						<tr>
							<td valign="top" class="txtlabel alignRight">Email
								Notification:</td>
							<td><s:checkbox name="isEmailCandiFinalize"
									id="isEmailCandiFinalize" /></td>
						</tr>
						<%-- <tr>
							<td valign="top" class="txtlabel alignRight">Enable/Disable Text Notification:</td>
							<td><s:checkbox name="isTextCandiFinalize"/></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Text Message</td>
							<td><s:textfield cssStyle="width:480px" name="strTextCandiFinalize"/></td>
						</tr> --%>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Subject:</td>
							<td><s:textfield cssStyle="width:480px"
									name="strSubjectCandiFinalize" /></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Body:</td>
							<td><div style="float: left; margin-right: 20px;">
									<s:textarea name="strBodyCandiFinalize" cols="63" rows="10" />
								</div>
								<div style="float: left;">
									[EMPFNAME] <br /> [EMPLNAME] <br /> [CANDIFNAME] <br />
									[CANDILNAME] <br /> [LEGAL_ENTITY_NAME] <br />
								</div></td>
						</tr>
					</table>
				</div>
			</div>
			<!-- /.box-body -->
		</div>


		<div class="box box-primary collapsed-box"
			style="border-top-color: #EEEEEE; margin-top: 10px;">

			<div class="box-header with-border">
				<h3 class="box-title" style="font-size: 14px; padding-right: 10px;">
					Candidate Assessment Form
					<%
					strNotId = "" + IConstants.N_CANDI_TAKE_ASSESSMENT;
								isMail = (Boolean) request.getAttribute("isEmailCandiAssessmentForm");
								strEImage = isMail ? "mail_enbl.png" : "mail_disbl.png";
								strEEnable = isMail ? "Disable Mail Notification" : "Enable Mail Notification";
								if (isMail) {
									isMail = false;
								} else {
									isMail = true;
								}
				%>
					<span id="myDivE<%=strNotId%>" style="float: right"> <img
						width="20px" src="images1/<%=strEImage%>"
						title="<%=strEEnable%>"
						onclick="updateEmailStatus('myDivE<%=strNotId%>',<%=strNotId%>,<%=isMail%>,'isEmailCandiAssessmentForm');" />
					</span>
				</h3>
				<div class="box-tools pull-right">
					<button class="btn btn-box-tool" data-widget="collapse">
						<i class="fa fa-plus"></i>
					</button>
					<button class="btn btn-box-tool" data-widget="remove">
						<i class="fa fa-times"></i>
					</button>
				</div>
			</div>
			<!-- /.box-header -->
			<div class="box-body"
				style="padding: 5px; overflow-y: auto; display: none;">
				<div class="content1" style="margin: 0px 0px 10px 0px">
					<table border="0" cellspacing="2" cellpadding="2" width="100%">
						<tr>
							<td valign="top" class="txtlabel alignRight">Email
								Notification:</td>
							<td><s:checkbox name="isEmailCandiAssessmentForm"
									id="isEmailCandiAssessmentForm" /></td>
						</tr>
						<%-- <tr>
						<td valign="top" class="txtlabel alignRight">Enable/Disable Text Notification:</td>
						<td><s:checkbox name="isTextCandiAssessmentForm"/></td>
					</tr>
					<tr>
						<td valign="top" class="txtlabel alignRight">Text Message</td>
						<td><s:textfield cssStyle="width:526px" name="strTextCandiAssessmentForm"/></td>
					</tr> --%>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Subject:</td>
							<td><s:textfield cssStyle="width:526px"
									name="strSubjectCandiAssessmentForm" /></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Body:</td>
							<td><div style="float: left; margin-right: 20px;">
									<s:textarea name="strBodyCandiAssessmentForm" cols="63"
										rows="10" />
								</div>
								<div style="float: left;">
									[CANDIFNAME] <br /> [CANDILNAME] <br />
									[CANDI_TAKE_ASSESSMENT_LINK] <br /> [LEGAL_ENTITY_NAME] <br />
								</div></td>
						</tr>
					</table>
				</div>
			</div>
			<!-- /.box-body -->
		</div>
		<!-- Start Dattatray Date : 11-08-21 -->
		<div class="box box-primary collapsed-box"
			style="border-top-color: #EEEEEE; margin-top: 10px;">

			<div class="box-header with-border">
				<h3 class="box-title" style="font-size: 14px; padding-right: 10px;">
					Resume Submission
					<%
					strNotId = "" + IConstants.N_RESUME_SUBMISSION;
								isMail = (Boolean) request.getAttribute("isEmailResumeSubmission");
								//System.out.println("isMail() : "+isMail);
								strEImage = isMail ? "mail_enbl.png" : "mail_disbl.png";	
								strEEnable = isMail ? "Disable Mail Notification" : "Enable Mail Notification";
								if (isMail) {
									isMail = false;
								} else {
									isMail = true;
								}
				%>
					<span id="myDivE<%=strNotId%>" style="float: right"> <img
						width="20px" src="images1/<%=strEImage%>"
						title="<%=strEEnable%>"
						onclick="updateEmailStatus('myDivE<%=strNotId%>',<%=strNotId%>,<%=isMail%>,'isEmailResumeSubmission');" />
					</span>
				</h3>
				<div class="box-tools pull-right">
					<button class="btn btn-box-tool" data-widget="collapse">
						<i class="fa fa-plus"></i>
					</button>
					<button class="btn btn-box-tool" data-widget="remove">
						<i class="fa fa-times"></i>
					</button>
				</div>
			</div>
			<!-- /.box-header -->
			<div class="box-body"
				style="padding: 5px; overflow-y: auto; display: none;">
				<div class="content1" style="margin: 0px 0px 10px 0px">
					<table border="0" cellspacing="2" cellpadding="2" width="100%">
						<tr>
							<td valign="top" class="txtlabel alignRight">Email
								Notification:</td>
							<td><s:checkbox name="isEmailResumeSubmission"
									id="isEmailResumeSubmission" /></td>
						</tr>
						<%-- <tr>
						<td valign="top" class="txtlabel alignRight">Enable/Disable Text Notification:</td>
						<td><s:checkbox name="isTextCandiAssessmentForm"/></td>
					</tr>
					<tr>
						<td valign="top" class="txtlabel alignRight">Text Message</td>
						<td><s:textfield cssStyle="width:526px" name="strTextCandiAssessmentForm"/></td>
					</tr> --%>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Subject:</td>
							<td><s:textfield cssStyle="width:526px"
									name="strSubjectResumeSubmission" /></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Body:</td>
							<td><div style="float: left; margin-right: 20px;">
									<s:textarea name="strBodyResumeSubmission" cols="63"
										rows="10" />
								</div>
								<div style="float: left;">
									[CANDIFNAME] <br /> 
									[CANDILNAME] <br/>
									[JOB_TITLE] 
								</div></td>
						</tr>
					</table>
				</div>
			</div>
			<!-- /.box-body -->
		</div>
		

	<!-- ===start parvez date: 29-10-2021=== -->
		<div class="box box-primary collapsed-box"
			style="border-top-color: #EEEEEE; margin-top: 10px;">

			<div class="box-header with-border">
				<h3 class="box-title" style="font-size: 14px; padding-right: 10px;">
					Application Submission to Hiring Team
					<%
					strNotId = "" + IConstants.N_APPLICATION_SUBMISSION_TO_HIRING_TEAM;
								isMail = (Boolean) request.getAttribute("isEmailApplicationSubmissionToHT");
								//System.out.println("isMail() : "+isMail);
								strEImage = isMail ? "mail_enbl.png" : "mail_disbl.png";	
								strEEnable = isMail ? "Disable Mail Notification" : "Enable Mail Notification";
								if (isMail) {
									isMail = false;
								} else {
									isMail = true;
								}
				%>
					<span id="myDivE<%=strNotId%>" style="float: right"> <img
						width="20px" src="images1/<%=strEImage%>"
						title="<%=strEEnable%>"
						onclick="updateEmailStatus('myDivE<%=strNotId%>',<%=strNotId%>,<%=isMail%>,'isEmailApplicationSubmissionToHT');" />
					</span>
				</h3>
				<div class="box-tools pull-right">
					<button class="btn btn-box-tool" data-widget="collapse">
						<i class="fa fa-plus"></i>
					</button>
					<button class="btn btn-box-tool" data-widget="remove">
						<i class="fa fa-times"></i>
					</button>
				</div>
			</div>
			<!-- /.box-header -->
			<div class="box-body"
				style="padding: 5px; overflow-y: auto; display: none;">
				<div class="content1" style="margin: 0px 0px 10px 0px">
					<table border="0" cellspacing="2" cellpadding="2" width="100%">
						<tr>
							<td valign="top" class="txtlabel alignRight">Email
								Notification:</td>
							<td><s:checkbox name="isEmailApplicationSubmissionToHT"
									id="isEmailApplicationSubmissionToHT" /></td>
						</tr>
						<%-- <tr>
						<td valign="top" class="txtlabel alignRight">Enable/Disable Text Notification:</td>
						<td><s:checkbox name="isTextCandiAssessmentForm"/></td>
					</tr>
					<tr>
						<td valign="top" class="txtlabel alignRight">Text Message</td>
						<td><s:textfield cssStyle="width:526px" name="strTextCandiAssessmentForm"/></td>
					</tr> --%>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Subject:</td>
							<td><s:textfield cssStyle="width:526px"
									name="strSubjectApplicationSubmissionToHT" /></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Body:</td>
							<td><div style="float: left; margin-right: 20px;">
									<s:textarea name="strBodyApplicationSubmissionToHT" cols="63" rows="10" />
								</div>
								<div style="float: left;">
									[CANDIFNAME] <br /> 
									[CANDILNAME] <br/>
									[JOB_TITLE] <br/>
									[CANDI_TOTAL_EXPERIENCE] <br/>
									[CANDI_CTC] <br/>
									[CANDI_EXPECTED_CTC] <br/>
									[CANDI_NOTICE_PERIOD] <br/>
									[CANDI_Current_ORG] <br/>
									[CANDI_Current_LOCATION] <br/>
									[CANDI_PREFERRED_LOCATION] <br/>
									[CANDI_CONTACT_NO] <br/>
									[CANDI_EMAIL_ID] <br/>
									[CANDI_PRIMARY_SKILL] <br/>
									[CANDI_SECONDARY_SKILL] 
								</div></td>
						</tr>
					</table>
				</div>
			</div>
			<!-- /.box-body -->
		</div>
	<!-- ===end parvez date: 29-10-2021=== -->
		
		<div class="box box-primary collapsed-box"
			style="border-top-color: #EEEEEE; margin-top: 10px;">

			<div class="box-header with-border">
				<h3 class="box-title" style="font-size: 14px; padding-right: 10px;">
					Selected Round
					<%
					strNotId = "" + IConstants.N_SELECTED_ROUND;
								isMail = (Boolean) request.getAttribute("isEmailSelectedRound");
								//System.out.println("isMail() : "+isMail);
								strEImage = isMail ? "mail_enbl.png" : "mail_disbl.png";	
								strEEnable = isMail ? "Disable Mail Notification" : "Enable Mail Notification";
								if (isMail) {
									isMail = false;
								} else {
									isMail = true;
								}
				%>
					<span id="myDivE<%=strNotId%>" style="float: right"> <img
						width="20px" src="images1/<%=strEImage%>"
						title="<%=strEEnable%>"
						onclick="updateEmailStatus('myDivE<%=strNotId%>',<%=strNotId%>,<%=isMail%>,'isEmailSelectedRound');" />
					</span>
				</h3>
				<div class="box-tools pull-right">
					<button class="btn btn-box-tool" data-widget="collapse">
						<i class="fa fa-plus"></i>
					</button>
					<button class="btn btn-box-tool" data-widget="remove">
						<i class="fa fa-times"></i>
					</button>
				</div>
			</div>
			<!-- /.box-header -->
			<div class="box-body"
				style="padding: 5px; overflow-y: auto; display: none;">
				<div class="content1" style="margin: 0px 0px 10px 0px">
					<table border="0" cellspacing="2" cellpadding="2" width="100%">
						<tr>
							<td valign="top" class="txtlabel alignRight">Email
								Notification:</td>
							<td><s:checkbox name="isEmailSelectedRound"
									id="isEmailSelectedRound" /></td>
						</tr>
						<%-- <tr>
						<td valign="top" class="txtlabel alignRight">Enable/Disable Text Notification:</td>
						<td><s:checkbox name="isTextCandiAssessmentForm"/></td>
					</tr>
					<tr>
						<td valign="top" class="txtlabel alignRight">Text Message</td>
						<td><s:textfield cssStyle="width:526px" name="strTextCandiAssessmentForm"/></td>
					</tr> --%>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Subject:</td>
							<td><s:textfield cssStyle="width:526px"
									name="strSubjectSelectedRound" /></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Body:</td>
							<td><div style="float: left; margin-right: 20px;">
									<s:textarea name="strBodySelectedRound" cols="63"
										rows="10" />
								</div>
								<div style="float: left;">
									[CANDIFNAME] <br /> 
									[CANDILNAME] <br/>
									[JOB_TITLE]<br/>
									[PREVIOUS_ROUND_NAME]<br/>
									[NEXT_ROUND_NAME]<br/>
									[TIME_OF_INTERVIEW]<br/>
									[RECRUITER_NAME]<br/>
									[RECRUITER_SIGNATURE]
								</div></td>
						</tr>
					</table>
				</div>
			</div>
			<!-- /.box-body -->
		</div>
		
		<div class="box box-primary collapsed-box"
			style="border-top-color: #EEEEEE; margin-top: 10px;">

			<div class="box-header with-border">
				<h3 class="box-title" style="font-size: 14px; padding-right: 10px;">
					Rejected Round
					<%
					strNotId = "" + IConstants.N_REJECTED_ROUND;
								isMail = (Boolean) request.getAttribute("isEmailRejectedRound");
								//System.out.println("isMail() : "+isMail);
								strEImage = isMail ? "mail_enbl.png" : "mail_disbl.png";	
								strEEnable = isMail ? "Disable Mail Notification" : "Enable Mail Notification";
								if (isMail) {
									isMail = false;
								} else {
									isMail = true;
								}
				%>
					<span id="myDivE<%=strNotId%>" style="float: right"> <img
						width="20px" src="images1/<%=strEImage%>"
						title="<%=strEEnable%>"
						onclick="updateEmailStatus('myDivE<%=strNotId%>',<%=strNotId%>,<%=isMail%>,'isEmailRejectedRound');" />
					</span>
				</h3>
				<div class="box-tools pull-right">
					<button class="btn btn-box-tool" data-widget="collapse">
						<i class="fa fa-plus"></i>
					</button>
					<button class="btn btn-box-tool" data-widget="remove">
						<i class="fa fa-times"></i>
					</button>
				</div>
			</div>
			<!-- /.box-header -->
			<div class="box-body"
				style="padding: 5px; overflow-y: auto; display: none;">
				<div class="content1" style="margin: 0px 0px 10px 0px">
					<table border="0" cellspacing="2" cellpadding="2" width="100%">
						<tr>
							<td valign="top" class="txtlabel alignRight">Email
								Notification:</td>
							<td><s:checkbox name="isEmailRejectedRound"
									id="isEmailRejectedRound" /></td>
						</tr>
						<%-- <tr>
						<td valign="top" class="txtlabel alignRight">Enable/Disable Text Notification:</td>
						<td><s:checkbox name="isTextCandiAssessmentForm"/></td>
					</tr>
					<tr>
						<td valign="top" class="txtlabel alignRight">Text Message</td>
						<td><s:textfield cssStyle="width:526px" name="strTextCandiAssessmentForm"/></td>
					</tr> --%>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Subject:</td>
							<td><s:textfield cssStyle="width:526px"
									name="strSubjectRejectedRound" /></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Body:</td>
							<td><div style="float: left; margin-right: 20px;">
									<s:textarea name="strBodyRejectedRound" cols="63"
										rows="10" />
								</div>
								<div style="float: left;">
									[CANDIFNAME] <br /> 
									[CANDILNAME] <br/>
									[JOB_TITLE]<br/>
									[RECRUITER_NAME]<br/>
									[RECRUITER_SIGNATURE]
								</div></td>
						</tr>
					</table>
				</div>
			</div>
			<!-- /.box-body -->
		</div>
		<!-- Start Dattatray Date:12-08-21 -->
		<div class="box box-primary collapsed-box"
			style="border-top-color: #EEEEEE; margin-top: 10px;">

			<div class="box-header with-border">
				<h3 class="box-title" style="font-size: 14px; padding-right: 10px;">
					Fresher Job Submission
					<%
					strNotId = "" + IConstants.N_FRESHER_JOB_SUBMISSION;
								isMail = (Boolean) request.getAttribute("isEmailFresherResumeSubmission");
								//System.out.println("isMail() : "+isMail);
								strEImage = isMail ? "mail_enbl.png" : "mail_disbl.png";	
								strEEnable = isMail ? "Disable Mail Notification" : "Enable Mail Notification";
								if (isMail) {
									isMail = false;
								} else {
									isMail = true;
								}
				%>
					<span id="myDivE<%=strNotId%>" style="float: right"> <img
						width="20px" src="images1/<%=strEImage%>"
						title="<%=strEEnable%>"
						onclick="updateEmailStatus('myDivE<%=strNotId%>',<%=strNotId%>,<%=isMail%>,'isEmailFresherResumeSubmission');" />
					</span>
				</h3>
				<div class="box-tools pull-right">
					<button class="btn btn-box-tool" data-widget="collapse">
						<i class="fa fa-plus"></i>
					</button>
					<button class="btn btn-box-tool" data-widget="remove">
						<i class="fa fa-times"></i>
					</button>
				</div>
			</div>
			<!-- /.box-header -->
			<div class="box-body"
				style="padding: 5px; overflow-y: auto; display: none;">
				<div class="content1" style="margin: 0px 0px 10px 0px">
					<table border="0" cellspacing="2" cellpadding="2" width="100%">
						<tr>
							<td valign="top" class="txtlabel alignRight">Email
								Notification:</td>
							<td><s:checkbox name="isEmailFresherResumeSubmission"
									id="isEmailFresherResumeSubmission" /></td>
						</tr>
						<%-- <tr>
						<td valign="top" class="txtlabel alignRight">Enable/Disable Text Notification:</td>
						<td><s:checkbox name="isTextCandiAssessmentForm"/></td>
					</tr>
					<tr>
						<td valign="top" class="txtlabel alignRight">Text Message</td>
						<td><s:textfield cssStyle="width:526px" name="strTextCandiAssessmentForm"/></td>
					</tr> --%>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Subject:</td>
							<td><s:textfield cssStyle="width:526px"
									name="strSubjectFresherResumeSubmission" /></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Body:</td>
							<td><div style="float: left; margin-right: 20px;">
									<s:textarea name="strBodyFresherResumeSubmission" cols="63"
										rows="10" />
								</div>
								<div style="float: left;">
									[CANDIFNAME] <br /> 
									[CANDILNAME]
								</div></td>
						</tr>
					</table>
				</div>
			</div>
			<!-- /.box-body -->
		</div>
		<!-- Start Dattatray Date:12-08-21 -->
		<!-- End Dattatray Date : 11-08-21 -->
		<!-- -------------------------- Close Recruitment --------------------------- -->
		<%
			}
		%>

		<%
			if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules,
							IConstants.MODULE_CAREER_DEV_PLANNING + "") >= 0) {
		%>
		<!-- -------------------------- Learning --------------------------- -->
		<div
			style="width: 100%; text-align: center; font-size: 14px; font-weight: bold; margin: 7px;"
			class="">Learning</div>

		<div class="box box-primary collapsed-box"
			style="border-top-color: #EEEEEE; margin-top: 10px;">

			<div class="box-header with-border">
				<h3 class="box-title" style="font-size: 14px; padding-right: 10px;">
					New Learning Learners
					<%
					strNotId = "" + IConstants.N_NEW_LEARNING_PLAN_FOR_LEARNERS;
								isMail = (Boolean) request.getAttribute("isEmailLearningPlanForLearner");
								strEImage = isMail ? "mail_enbl.png" : "mail_disbl.png";
								strEEnable = isMail ? "Disable Mail Notification" : "Enable Mail Notification";

								if (isMail) {
									isMail = false;
								} else {
									isMail = true;
								}
				%>
					<span id="myDivE<%=strNotId%>" style="float: right"> <img
						width="20px" src="images1/<%=strEImage%>"
						title="<%=strEEnable%>"
						onclick="updateEmailStatus('myDivE<%=strNotId%>',<%=strNotId%>,<%=isMail%>,'isEmailLearningPlanForLearner');" />
					</span>
				</h3>
				<div class="box-tools pull-right">
					<button class="btn btn-box-tool" data-widget="collapse">
						<i class="fa fa-plus"></i>
					</button>
					<button class="btn btn-box-tool" data-widget="remove">
						<i class="fa fa-times"></i>
					</button>
				</div>
			</div>
			<!-- /.box-header -->
			<div class="box-body"
				style="padding: 5px; overflow-y: auto; display: none;">
				<div class="content1" style="margin: 0px 0px 10px 0px">
					<table class="table table_no_border" cellspacing="2"
						cellpadding="2" width="100%">
						<tr>
							<td valign="top" class="txtlabel alignRight">Email
								Notification:</td>
							<td><s:checkbox name="isEmailLearningPlanForLearner"
									id="isEmailLearningPlanForLearner" /></td>
						</tr>
						<%-- <tr>
							<td valign="top" class="txtlabel alignRight">Enable/Disable Text Notification:</td>
							<td><s:checkbox name="isTextLearningPlanForLearner"/></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Text Message</td>
							<td><s:textfield cssStyle="width:480px" name="strTextLearningPlanForLearner"/></td>
						</tr> --%>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Subject:</td>
							<td><s:textfield cssStyle="width:480px"
									name="strSubjectLearningPlanForLearner" /></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Body:</td>
							<td><div style="float: left; margin-right: 20px;">
									<s:textarea name="strBodyLearningPlanForLearner" cols="63"
										rows="10" />
								</div>
								<div style="float: left;">
									[EMPFNAME] <br /> [EMPLNAME] <br /> [LEARNING_PLAN_NAME] <br />
									[LEARNING_PLAN_STARTDATE] <br /> [LEARNING_PLAN_ENDDATE] <br />
									[LEGAL_ENTITY_NAME] <br />
								</div></td>
						</tr>
					</table>
				</div>
			</div>
			<!-- /.box-body -->
		</div>


		<div class="box box-primary collapsed-box"
			style="border-top-color: #EEEEEE; margin-top: 10px;">

			<div class="box-header with-border">
				<h3 class="box-title" style="font-size: 14px; padding-right: 10px;">
					Learning Gap Created
					<%
					strNotId = "" + IConstants.N_LEARNING_GAP_FOR_HR;
								isMail = (Boolean) request.getAttribute("isEmailLearningGapForHr");
								strEImage = isMail ? "mail_enbl.png" : "mail_disbl.png";
								strEEnable = isMail ? "Disable Mail Notification" : "Enable Mail Notification";
								if (isMail) {
									isMail = false;
								} else {
									isMail = true;
								}
				%>
					<span id="myDivE<%=strNotId%>" style="float: right"> <img
						width="20px" src="images1/<%=strEImage%>"
						title="<%=strEEnable%>"
						onclick="updateEmailStatus('myDivE<%=strNotId%>',<%=strNotId%>,<%=isMail%>,'isEmailLearningGapForHr');" />
					</span>
				</h3>
				<div class="box-tools pull-right">
					<button class="btn btn-box-tool" data-widget="collapse">
						<i class="fa fa-plus"></i>
					</button>
					<button class="btn btn-box-tool" data-widget="remove">
						<i class="fa fa-times"></i>
					</button>
				</div>
			</div>
			<!-- /.box-header -->
			<div class="box-body"
				style="padding: 5px; overflow-y: auto; display: none;">
				<div class="content1" style="margin: 0px 0px 10px 0px">
					<table border="0" cellspacing="2" cellpadding="2" width="100%">
						<tr>
							<td valign="top" class="txtlabel alignRight">Email
								Notification:</td>
							<td><s:checkbox name="isEmailLearningGapForHr"
									id="isEmailLearningGapForHr" /></td>
						</tr>
						<%-- <tr>
							<td valign="top" class="txtlabel alignRight">Enable/Disable Text Notification:</td>
							<td><s:checkbox name="isTextLearningGapForHr"/></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Text Message</td>
							<td><s:textfield cssStyle="width:526px" name="strTextLearningGapForHr"/></td>
						</tr> --%>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Subject:</td>
							<td><s:textfield cssStyle="width:526px"
									name="strSubjectLearningGapForHr" /></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Body:</td>
							<td><div style="float: left; margin-right: 20px;">
									<s:textarea name="strBodyLearningGapForHr" cols="63" rows="10" />
								</div>
								<div style="float: left;">
									[EMPFNAME] <br /> [EMPLNAME] <br /> [REVIEW_NAME] <br />
									[REVIEWEE_NAME] <br /> [ATTRIBUTE_NAME] <br />
									[LEGAL_ENTITY_NAME] <br />
								</div></td>
						</tr>
					</table>
				</div>
			</div>
			<!-- /.box-body -->
		</div>


		<div class="box box-primary collapsed-box"
			style="border-top-color: #EEEEEE; margin-top: 10px;">

			<div class="box-header with-border">
				<h3 class="box-title" style="font-size: 14px; padding-right: 10px;">
					New Learning Added
					<%
					strNotId = "" + IConstants.N_NEW_LEARNING_PLAN_FOR_HR;
								isMail = (Boolean) request.getAttribute("isEmailLearningPlanForHr");
								strEImage = isMail ? "mail_enbl.png" : "mail_disbl.png";
								strEEnable = isMail ? "Disable Mail Notification" : "Enable Mail Notification";

								if (isMail) {
									isMail = false;
								} else {
									isMail = true;
								}
				%>
					<span id="myDivE<%=strNotId%>" style="float: right"> <img
						width="20px" src="images1/<%=strEImage%>"
						title="<%=strEEnable%>"
						onclick="updateEmailStatus('myDivE<%=strNotId%>',<%=strNotId%>,<%=isMail%>,'isEmailLearningPlanForHr');" />
					</span>
				</h3>
				<div class="box-tools pull-right">
					<button class="btn btn-box-tool" data-widget="collapse">
						<i class="fa fa-plus"></i>
					</button>
					<button class="btn btn-box-tool" data-widget="remove">
						<i class="fa fa-times"></i>
					</button>
				</div>
			</div>
			<!-- /.box-header -->
			<div class="box-body"
				style="padding: 5px; overflow-y: auto; display: none;">
				<div class="content1" style="margin: 0px 0px 10px 0px">
					<table class="table table_no_border" cellspacing="2"
						cellpadding="2" width="100%">
						<tr>
							<td valign="top" class="txtlabel alignRight">Email
								Notification:</td>
							<td><s:checkbox name="isEmailLearningPlanForHr"
									id="isEmailLearningPlanForHr" /></td>
						</tr>
						<%-- <tr>
							<td valign="top" class="txtlabel alignRight">Enable/Disable Text Notification:</td>
							<td><s:checkbox name="isTextLearningPlanForHr"/></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Text Message</td>
							<td><s:textfield cssStyle="width:480px" name="strTextLearningPlanForHr"/></td>
						</tr> --%>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Subject:</td>
							<td><s:textfield cssStyle="width:480px"
									name="strSubjectLearningPlanForHr" /></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Body:</td>
							<td><div style="float: left; margin-right: 20px;">
									<s:textarea name="strBodyLearningPlanForHr" cols="63" rows="10" />
								</div>
								<div style="float: left;">
									[EMPFNAME] <br /> [EMPLNAME] <br /> [LEARNING_PLAN_NAME] <br />
									[LEARNING_PLAN_STARTDATE] <br /> [LEARNING_PLAN_ENDDATE] <br />
									[LEGAL_ENTITY_NAME] <br />
								</div></td>
						</tr>
					</table>
				</div>
			</div>
			<!-- /.box-body -->
		</div>

		<div class="box box-primary collapsed-box"
			style="border-top-color: #EEEEEE; margin-top: 10px;">

			<div class="box-header with-border">
				<h3 class="box-title" style="font-size: 14px; padding-right: 10px;">
					Learning Finalization (Training) to Employee
					<%
					strNotId = "" + IConstants.N_LEARNING_TRAINING_FINALIZATION_TO_EMP;
								isMail = (Boolean) request.getAttribute("isEmailLTrainingFinalizeForHr");
								strEImage = isMail ? "mail_enbl.png" : "mail_disbl.png";
								strEEnable = isMail ? "Disable Mail Notification" : "Enable Mail Notification";

								if (isMail) {
									isMail = false;
								} else {
									isMail = true;
								}
				%>
					<span id="myDivE<%=strNotId%>" style="float: right"> <img
						width="20px" src="images1/<%=strEImage%>"
						title="<%=strEEnable%>"
						onclick="updateEmailStatus('myDivE<%=strNotId%>',<%=strNotId%>,<%=isMail%>,'isEmailLTrainingFinalizeForHr');" />
					</span>
				</h3>
				<div class="box-tools pull-right">
					<button class="btn btn-box-tool" data-widget="collapse">
						<i class="fa fa-plus"></i>
					</button>
					<button class="btn btn-box-tool" data-widget="remove">
						<i class="fa fa-times"></i>
					</button>
				</div>
			</div>
			<!-- /.box-header -->
			<div class="box-body"
				style="padding: 5px; overflow-y: auto; display: none;">
				<div class="content1" style="margin: 0px 0px 10px 0px">
					<table class="table table_no_border" cellspacing="2"
						cellpadding="2" width="100%">
						<tr>
							<td valign="top" class="txtlabel alignRight">Email
								Notification:</td>
							<td><s:checkbox name="isEmailLTrainingFinalizeForHr"
									id="isEmailLTrainingFinalizeForHr" /></td>
						</tr>
						<%-- <tr>
							<td valign="top" class="txtlabel alignRight">Enable/Disable Text Notification:</td>
							<td><s:checkbox name="isTextLTrainingFinalizeForHr"/></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Text Message</td>
							<td><s:textfield cssStyle="width:480px" name="strTextLTrainingFinalizeForHr"/></td>
						</tr> --%>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Subject:</td>
							<td><s:textfield cssStyle="width:480px"
									name="strSubjectLTrainingFinalizeForHr" /></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Body:</td>
							<td><div style="float: left; margin-right: 20px;">
									<s:textarea name="strBodyLTrainingFinalizeForHr" cols="63"
										rows="10" />
								</div>
								<div style="float: left;">
									[EMPFNAME] <br /> [EMPLNAME] <br /> [LEARNING_PLAN_NAME] <br />
									[LEARNING_PLAN_STARTDATE] <br /> [LEARNING_PLAN_ENDDATE] <br />
									[LEGAL_ENTITY_NAME] <br />
								</div></td>
						</tr>
					</table>
				</div>
			</div>
			<!-- /.box-body -->
		</div>


		<div class="box box-primary collapsed-box"
			style="border-top-color: #EEEEEE; margin-top: 10px;">

			<div class="box-header with-border">
				<h3 class="box-title" style="font-size: 14px; padding-right: 10px;">
					Learning Finalization (Assessment) to Employee
					<%
					strNotId = "" + IConstants.N_LEARNING_ASSESS_FINALIZATION_TO_EMP;
								isMail = (Boolean) request.getAttribute("isEmailLAssessFinalizeForHr");
								strEImage = isMail ? "mail_enbl.png" : "mail_disbl.png";
								strEEnable = isMail ? "Disable Mail Notification" : "Enable Mail Notification";

								if (isMail) {
									isMail = false;
								} else {
									isMail = true;
								}
				%>
					<span id="myDivE<%=strNotId%>" style="float: right"> <img
						width="20px" src="images1/<%=strEImage%>"
						title="<%=strEEnable%>"
						onclick="updateEmailStatus('myDivE<%=strNotId%>',<%=strNotId%>,<%=isMail%>,'isEmailLAssessFinalizeForHr');" />
					</span>
				</h3>
				<div class="box-tools pull-right">
					<button class="btn btn-box-tool" data-widget="collapse">
						<i class="fa fa-plus"></i>
					</button>
					<button class="btn btn-box-tool" data-widget="remove">
						<i class="fa fa-times"></i>
					</button>
				</div>
			</div>
			<!-- /.box-header -->
			<div class="box-body"
				style="padding: 5px; overflow-y: auto; display: none;">
				<div class="content1" style="margin: 0px 0px 10px 0px">
					<table class="table table_no_border" cellspacing="2"
						cellpadding="2" width="100%">
						<tr>
							<td valign="top" class="txtlabel alignRight">Email
								Notification:</td>
							<td><s:checkbox name="isEmailLAssessFinalizeForHr"
									id="isEmailLAssessFinalizeForHr" /></td>
						</tr>
						<%-- <tr>
							<td valign="top" class="txtlabel alignRight">Enable/Disable Text Notification:</td>
							<td><s:checkbox name="isTextLAssessFinalizeForHr"/></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Text Message</td>
							<td><s:textfield cssStyle="width:480px" name="strTextLAssessFinalizeForHr"/></td>
						</tr> --%>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Subject:</td>
							<td><s:textfield cssStyle="width:480px"
									name="strSubjectLAssessFinalizeForHr" /></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Body:</td>
							<td><div style="float: left; margin-right: 20px;">
									<s:textarea name="strBodyLAssessFinalizeForHr" cols="63"
										rows="10" />
								</div>
								<div style="float: left;">
									[EMPFNAME] <br /> [EMPLNAME] <br /> [LEARNING_PLAN_NAME] <br />
									[LEARNING_PLAN_STARTDATE] <br /> [LEARNING_PLAN_ENDDATE] <br />
									[LEGAL_ENTITY_NAME] <br />
								</div></td>
						</tr>
					</table>
				</div>
			</div>
			<!-- /.box-body -->
		</div>

		<div class="box box-primary collapsed-box"
			style="border-top-color: #EEEEEE; margin-top: 10px;">

			<div class="box-header with-border">
				<h3 class="box-title" style="font-size: 14px; padding-right: 10px;">
					Learning Feedback by Trainer
					<%
					strNotId = "" + IConstants.N_L_TRAINING_FEEDBACK_FROM_TRAINER_TO_HR;
								isMail = (Boolean) request.getAttribute("isEmailLTrainingFeedbackTrainer");
								strEImage = isMail ? "mail_enbl.png" : "mail_disbl.png";
								strEEnable = isMail ? "Disable Mail Notification" : "Enable Mail Notification";

								if (isMail) {
									isMail = false;
								} else {
									isMail = true;
								}
				%>
					<span id="myDivE<%=strNotId%>" style="float: right"> <img
						width="20px" src="images1/<%=strEImage%>"
						title="<%=strEEnable%>"
						onclick="updateEmailStatus('myDivE<%=strNotId%>',<%=strNotId%>,<%=isMail%>,'isEmailLTrainingFeedbackTrainer');" />
					</span>
				</h3>
				<div class="box-tools pull-right">
					<button class="btn btn-box-tool" data-widget="collapse">
						<i class="fa fa-plus"></i>
					</button>
					<button class="btn btn-box-tool" data-widget="remove">
						<i class="fa fa-times"></i>
					</button>
				</div>
			</div>
			<!-- /.box-header -->
			<div class="box-body"
				style="padding: 5px; overflow-y: auto; display: none;">
				<div class="content1" style="margin: 0px 0px 10px 0px">
					<table class="table table_no_border" cellspacing="2"
						cellpadding="2" width="100%">
						<tr>
							<td valign="top" class="txtlabel alignRight">Email
								Notification:</td>
							<td><s:checkbox name="isEmailLTrainingFeedbackTrainer"
									id="isEmailLTrainingFeedbackTrainer" /></td>
						</tr>
						<%-- <tr>
							<td valign="top" class="txtlabel alignRight">Enable/Disable Text Notification:</td>
							<td><s:checkbox name="isTextLTrainingFeedbackTrainer"/></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Text Message</td>
							<td><s:textfield cssStyle="width:480px" name="strTextLTrainingFeedbackTrainer"/></td>
						</tr> --%>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Subject:</td>
							<td><s:textfield cssStyle="width:480px"
									name="strSubjectLTrainingFeedbackTrainer" /></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Body:</td>
							<td><div style="float: left; margin-right: 20px;">
									<s:textarea name="strBodyLTrainingFeedbackTrainer" cols="63"
										rows="10" />
								</div>
								<div style="float: left;">
									[EMPFNAME] <br /> [EMPLNAME] <br /> [LEARNING_PLAN_NAME] <br />
									[LEARNING_PLAN_STARTDATE] <br /> [LEARNING_PLAN_ENDDATE] <br />
									[LEGAL_ENTITY_NAME] <br />
								</div></td>
						</tr>
					</table>
				</div>
			</div>
			<!-- /.box-body -->
		</div>

		<div class="box box-primary collapsed-box"
			style="border-top-color: #EEEEEE; margin-top: 10px;">

			<div class="box-header with-border">
				<h3 class="box-title" style="font-size: 14px; padding-right: 10px;">
					Learning Feedback by Trainer to Learner
					<%
					strNotId = "" + IConstants.N_L_TRAINING_FEEDBACK_FROM_TRAINER_EMP;
								isMail = (Boolean) request.getAttribute("isEmailLTrainingFeedbackLearner");
								strEImage = isMail ? "mail_enbl.png" : "mail_disbl.png";
								strEEnable = isMail ? "Disable Mail Notification" : "Enable Mail Notification";

								if (isMail) {
									isMail = false;
								} else {
									isMail = true;
								}
				%>
					<span id="myDivE<%=strNotId%>" style="float: right"> <img
						width="20px" src="images1/<%=strEImage%>"
						title="<%=strEEnable%>"
						onclick="updateEmailStatus('myDivE<%=strNotId%>',<%=strNotId%>,<%=isMail%>,'isEmailLTrainingFeedbackLearner');" />
					</span>
				</h3>
				<div class="box-tools pull-right">
					<button class="btn btn-box-tool" data-widget="collapse">
						<i class="fa fa-plus"></i>
					</button>
					<button class="btn btn-box-tool" data-widget="remove">
						<i class="fa fa-times"></i>
					</button>
				</div>
			</div>
			<!-- /.box-header -->
			<div class="box-body"
				style="padding: 5px; overflow-y: auto; display: none;">
				<div class="content1" style="margin: 0px 0px 10px 0px">
					<table class="table table_no_border" cellspacing="2"
						cellpadding="2" width="100%">
						<tr>
							<td valign="top" class="txtlabel alignRight">Email
								Notification:</td>
							<td><s:checkbox name="isEmailLTrainingFeedbackLearner"
									id="isEmailLTrainingFeedbackLearner" /></td>
						</tr>
						<%-- <tr>
							<td valign="top" class="txtlabel alignRight">Enable/Disable Text Notification:</td>
							<td><s:checkbox name="isTextLTrainingFeedbackLearner"/></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Text Message</td>
							<td><s:textfield cssStyle="width:480px" name="strTextLTrainingFeedbackLearner"/></td>
						</tr> --%>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Subject:</td>
							<td><s:textfield cssStyle="width:480px"
									name="strSubjectLTrainingFeedbackLearner" /></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Body:</td>
							<td><div style="float: left; margin-right: 20px;">
									<s:textarea name="strBodyLTrainingFeedbackLearner" cols="63"
										rows="10" />
								</div>
								<div style="float: left;">
									[EMPFNAME] <br /> [EMPLNAME] <br /> [LEARNING_PLAN_NAME] <br />
									[LEARNING_PLAN_STARTDATE] <br /> [LEARNING_PLAN_ENDDATE] <br />
									[LEGAL_ENTITY_NAME] <br />
								</div></td>
						</tr>
					</table>
				</div>
			</div>
			<!-- /.box-body -->
		</div>


		<!-- -------------------------- Close Learning --------------------------- -->
		<%
			}
		%>

		<%
			if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules,
							IConstants.MODULE_PERFORMANCE_MANAGEMENT + "") >= 0) {
		%>
		<!-- -------------------------- Performance --------------------------- -->
		<div
			style="width: 100%; text-align: center; font-size: 14px; font-weight: bold; margin: 7px;"
			class="">Performance</div>

		<div class="box box-primary collapsed-box"
			style="border-top-color: #EEEEEE; margin-top: 10px;">

			<div class="box-header with-border">
				<h3 class="box-title" style="font-size: 14px; padding-right: 10px;">
					New Review Published
					<%
					strNotId = "" + IConstants.N_NEW_REVIW_PUBLISH;
								isMail = (Boolean) request.getAttribute("isEmailNewReviewPublish");
								strEImage = isMail ? "mail_enbl.png" : "mail_disbl.png";
								strEEnable = isMail ? "Disable Mail Notification" : "Enable Mail Notification";

								if (isMail) {
									isMail = false;
								} else {
									isMail = true;
								}
				%>
					<span id="myDivE<%=strNotId%>" style="float: right"> <img
						width="20px" src="images1/<%=strEImage%>"
						title="<%=strEEnable%>"
						onclick="updateEmailStatus('myDivE<%=strNotId%>',<%=strNotId%>,<%=isMail%>,'isEmailNewReviewPublish');" />
					</span>
				</h3>
				<div class="box-tools pull-right">
					<button class="btn btn-box-tool" data-widget="collapse">
						<i class="fa fa-plus"></i>
					</button>
					<button class="btn btn-box-tool" data-widget="remove">
						<i class="fa fa-times"></i>
					</button>
				</div>
			</div>
			<!-- /.box-header -->
			<div class="box-body"
				style="padding: 5px; overflow-y: auto; display: none;">
				<div class="content1" style="margin: 0px 0px 10px 0px">
					<table class="table table_no_border" cellspacing="2"
						cellpadding="2" width="100%">
						<tr>
							<td valign="top" class="txtlabel alignRight">Email
								Notification:</td>
							<td><s:checkbox name="isEmailNewReviewPublish"
									id="isEmailNewReviewPublish" /></td>
						</tr>
						<%-- <tr>
							<td valign="top" class="txtlabel alignRight">Enable/Disable Text Notification:</td>
							<td><s:checkbox name="isTextNewReviewPublish"/></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Text Message</td>
							<td><s:textfield cssStyle="width:480px" name="strTextNewReviewPublish"/></td>
						</tr> --%>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Subject:</td>
							<td><s:textfield cssStyle="width:480px"
									name="strSubjectNewReviewPublish" /></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Body:</td>
							<td><div style="float: left; margin-right: 20px;">
									<s:textarea name="strBodyNewReviewPublish" cols="63" rows="10" />
								</div>
								<div style="float: left;">
									[EMPFNAME] <br /> [EMPLNAME] <br /> [REVIEW_NAME] <br />
									[REVIEW_STARTDATE] <br /> [REVIEW_ENDDATE] <br />
									[REVIEWEE_NAME] <br /> [ROLE_TYPE] <br /> [LOGIN_LINK] <br />
									[LEGAL_ENTITY_NAME] <br />
								</div></td>
						</tr>
					</table>
				</div>
			</div>
			<!-- /.box-body -->
		</div>


		<%-- <div class="box box-primary collapsed-box" style="border-top-color: #EEEEEE; margin-top: 10px;">
                
                <div class="box-header with-border">
                    <h3 class="box-title" style="font-size: 14px;padding-right: 10px;">Employee Review Reviewed Notification 
				<%
						strNotId = ""+IConstants.N_EMP_REVIW_SUBMITED; 
						isMail = (Boolean) request.getAttribute("isEmailEmpReviewSubmit");
						strEImage = isMail ? "mail_enbl.png" : "mail_disbl.png";
						strEEnable = isMail ? "Disable Mail Notification" : "Enable Mail Notification" ;
						
						if(isMail){
							isMail = false;
						} else{
							isMail = true;
						}
					%>
					<span id="myDivE<%=strNotId%>" style="float:right">
						<img width="20px" src="images1/<%=strEImage %>" title="<%=strEEnable %>" onclick="updateEmailStatus('myDivE<%=strNotId%>',<%=strNotId%>,<%=isMail%>,'isEmailEmpReviewSubmit');"/>
					</span></h3>
                    <div class="box-tools pull-right">
                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
                    </div>
                </div>
                <!-- /.box-header -->
                <div class="box-body" style="padding: 5px; overflow-y: auto; display:none;">
                    <div class="content1" style="margin:0px 0px 10px 0px">
					<table class="table table_no_border" cellspacing="2" cellpadding="2" width="100%">
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Notification:</td>
							<td><s:checkbox name="isEmailEmpReviewSubmit" id="isEmailEmpReviewSubmit"/></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Subject:</td>
							<td><s:textfield cssStyle="width:480px" name="strSubjectEmpReviewSubmit"/></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Body:</td>
							<td><div style="float: left; margin-right: 20px;"><s:textarea name="strBodyEmpReviewSubmit" cols="63" rows="10" /></div>
								<div style="float: left;">
									[EMPFNAME] <br/>
									[EMPLNAME] <br/>
									[REVIEW_NAME] <br/>
									[REVIEWEE_NAME] <br/>
									[LEGAL_ENTITY_NAME] <br/>
								</div>
							</td>
						</tr>
					</table>
				</div>
                </div>
                <!-- /.box-body -->
            </div> --%>


		<%-- <div class="box box-primary collapsed-box" style="border-top-color: #EEEEEE; margin-top: 10px;">
                
                <div class="box-header with-border">
                    <h3 class="box-title" style="font-size: 14px;padding-right: 10px;">Review Finalization HR Notification 
				<%
						strNotId = ""+IConstants.N_REVIEW_FINALIZATION_FOR_HR; 
						isMail = (Boolean) request.getAttribute("isEmailReviewFinalizationForHr");
						strEImage = isMail ? "mail_enbl.png" : "mail_disbl.png";
						strEEnable = isMail ? "Disable Mail Notification" : "Enable Mail Notification" ;
						
						if(isMail){
							isMail = false;
						} else{
							isMail = true;
						}
					%>
					<span id="myDivE<%=strNotId%>" style="float:right">
						<img width="20px" src="images1/<%=strEImage %>" title="<%=strEEnable %>" onclick="updateEmailStatus('myDivE<%=strNotId%>',<%=strNotId%>,<%=isMail%>,'isEmailReviewFinalizationForHr');"/>
					</span></h3>
                    <div class="box-tools pull-right">
                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
                    </div>
                </div>
                <!-- /.box-header -->
                <div class="box-body" style="padding: 5px; overflow-y: auto; display:none;">
                    <div class="content1" style="margin:0px 0px 10px 0px">
					<table class="table table_no_border" cellspacing="2" cellpadding="2" width="100%">
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Notification:</td>
							<td><s:checkbox name="isEmailReviewFinalizationForHr" id="isEmailReviewFinalizationForHr"/></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Subject:</td>
							<td><s:textfield cssStyle="width:480px" name="strSubjectReviewFinalizationForHr"/></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Body:</td>
							<td><div style="float: left; margin-right: 20px;"><s:textarea name="strBodyReviewFinalizationForHr" cols="63" rows="10" /></div>
								<div style="float: left;">
									[EMPFNAME] <br/>
									[EMPLNAME] <br/>
									[REVIEW_NAME] <br/>
									[REVIEWEE_NAME] <br/>
									[LEGAL_ENTITY_NAME] <br/>
								</div>
							</td>
						</tr>
					</table>
				</div>
                </div>
                <!-- /.box-body -->
            </div> --%>


		<div class="box box-primary collapsed-box"
			style="border-top-color: #EEEEEE; margin-top: 10px;">

			<div class="box-header with-border">
				<h3 class="box-title" style="font-size: 14px; padding-right: 10px;">
					Review Finalization to Employee
					<%
					strNotId = "" + IConstants.N_REVIEW_FINALIZATION_FOR_EMP;
								isMail = (Boolean) request.getAttribute("isEmailReviewFinalizationForEmp");
								strEImage = isMail ? "mail_enbl.png" : "mail_disbl.png";
								strEEnable = isMail ? "Disable Mail Notification" : "Enable Mail Notification";

								if (isMail) {
									isMail = false;
								} else {
									isMail = true;
								}
				%>
					<span id="myDivE<%=strNotId%>" style="float: right"> <img
						width="20px" src="images1/<%=strEImage%>"
						title="<%=strEEnable%>"
						onclick="updateEmailStatus('myDivE<%=strNotId%>',<%=strNotId%>,<%=isMail%>,'isEmailReviewFinalizationForEmp');" />
					</span>
				</h3>
				<div class="box-tools pull-right">
					<button class="btn btn-box-tool" data-widget="collapse">
						<i class="fa fa-plus"></i>
					</button>
					<button class="btn btn-box-tool" data-widget="remove">
						<i class="fa fa-times"></i>
					</button>
				</div>
			</div>
			<!-- /.box-header -->
			<div class="box-body"
				style="padding: 5px; overflow-y: auto; display: none;">
				<div class="content1" style="margin: 0px 0px 10px 0px">
					<table class="table table_no_border" cellspacing="2"
						cellpadding="2" width="100%">
						<tr>
							<td valign="top" class="txtlabel alignRight">Email
								Notification:</td>
							<td><s:checkbox name="isEmailReviewFinalizationForEmp"
									id="isEmailReviewFinalizationForEmp" /></td>
						</tr>
						<%-- <tr>
							<td valign="top" class="txtlabel alignRight">Enable/Disable Text Notification:</td>
							<td><s:checkbox name="isTextReviewFinalizationForEmp"/></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Text Message</td>
							<td><s:textfield cssStyle="width:480px" name="strTextReviewFinalizationForEmp"/></td>
						</tr> --%>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Subject:</td>
							<td><s:textfield cssStyle="width:480px"
									name="strSubjectReviewFinalizationForEmp" /></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Body:</td>
							<td><div style="float: left; margin-right: 20px;">
									<s:textarea name="strBodyReviewFinalizationForEmp" cols="63"
										rows="10" />
								</div>
								<div style="float: left;">
									[EMPFNAME] <br /> [EMPLNAME] <br /> [REVIEW_NAME] <br />
									[FINALIZER_NAME] <br /> [LEGAL_ENTITY_NAME] <br />
								</div></td>
						</tr>
					</table>
				</div>
			</div>
			<!-- /.box-body -->
		</div>


		<div class="box box-primary collapsed-box"
			style="border-top-color: #EEEEEE; margin-top: 10px;">

			<div class="box-header with-border">
				<h3 class="box-title" style="font-size: 14px; padding-right: 10px;">
					Manager Goal Created
					<%
					strNotId = "" + IConstants.N_MANAGER_GOAL;
								isMail = (Boolean) request.getAttribute("isEmailManagerGoal");
								strEImage = isMail ? "mail_enbl.png" : "mail_disbl.png";
								strEEnable = isMail ? "Disable Mail Notification" : "Enable Mail Notification";

								if (isMail) {
									isMail = false;
								} else {
									isMail = true;
								}
				%>
					<span id="myDivE<%=strNotId%>" style="float: right"> <img
						width="20px" src="images1/<%=strEImage%>"
						title="<%=strEEnable%>"
						onclick="updateEmailStatus('myDivE<%=strNotId%>',<%=strNotId%>,<%=isMail%>,'isEmailManagerGoal');" />
					</span>
				</h3>
				<div class="box-tools pull-right">
					<button class="btn btn-box-tool" data-widget="collapse">
						<i class="fa fa-plus"></i>
					</button>
					<button class="btn btn-box-tool" data-widget="remove">
						<i class="fa fa-times"></i>
					</button>
				</div>
			</div>
			<!-- /.box-header -->
			<div class="box-body"
				style="padding: 5px; overflow-y: auto; display: none;">
				<div class="content1" style="margin: 0px 0px 10px 0px">
					<table class="table table_no_border" cellspacing="2"
						cellpadding="2" width="100%">
						<tr>
							<td valign="top" class="txtlabel alignRight">Email
								Notification:</td>
							<td><s:checkbox name="isEmailManagerGoal"
									id="isEmailManagerGoal" /></td>
						</tr>
						<%-- <tr>
							<td valign="top" class="txtlabel alignRight">Enable/Disable Text Notification:</td>
							<td><s:checkbox name="isTextManagerGoal"/></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Text Message</td>
							<td><s:textfield cssStyle="width:480px" name="strTextManagerGoal"/></td>
						</tr> --%>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Subject:</td>
							<td><s:textfield cssStyle="width:480px"
									name="strSubjectManagerGoal" /></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Body:</td>
							<td><div style="float: left; margin-right: 20px;">
									<s:textarea name="strBodyManagerGoal" cols="63" rows="10" />
								</div>
								<div style="float: left;">
									[EMPFNAME] <br /> [EMPLNAME] <br /> [GOAL_ASSIGNER_NAME] <br />
									[GOAL_NAME] <br /> [LEGAL_ENTITY_NAME] <br />
								</div></td>
						</tr>
					</table>
				</div>
			</div>
			<!-- /.box-body -->
		</div>


		<div class="box box-primary collapsed-box"
			style="border-top-color: #EEEEEE; margin-top: 10px;">

			<div class="box-header with-border">
				<h3 class="box-title" style="font-size: 14px; padding-right: 10px;">
					Employee Goal Created
					<%-- <%=uF.showData(hmNotificationStatus.get("NOT_"+IConstants.N_EXECUTIVE_GOAL), "") %> --%>
					<%
						strNotId = "" + IConstants.N_EXECUTIVE_GOAL;
									isMail = (Boolean) request.getAttribute("isEmailExecutiveGoal");
									strEImage = isMail ? "mail_enbl.png" : "mail_disbl.png";
									strEEnable = isMail ? "Disable Mail Notification" : "Enable Mail Notification";

									if (isMail) {
										isMail = false;
									} else {
										isMail = true;
									}
					%>
					<span id="myDivE<%=strNotId%>" style="float: right"> <img
						width="20px" src="images1/<%=strEImage%>"
						title="<%=strEEnable%>"
						onclick="updateEmailStatus('myDivE<%=strNotId%>',<%=strNotId%>,<%=isMail%>,'isEmailExecutiveGoal');" />
					</span>
				</h3>
				<div class="box-tools pull-right">
					<button class="btn btn-box-tool" data-widget="collapse">
						<i class="fa fa-plus"></i>
					</button>
					<button class="btn btn-box-tool" data-widget="remove">
						<i class="fa fa-times"></i>
					</button>
				</div>
			</div>
			<!-- /.box-header -->
			<div class="box-body"
				style="padding: 5px; overflow-y: auto; display: none;">
				<div class="content1" style="margin: 0px 0px 10px 0px">
					<table class="table table_no_border" cellspacing="2"
						cellpadding="2" width="100%">
						<tr>
							<td valign="top" class="txtlabel alignRight">Email
								Notification:</td>
							<td><s:checkbox name="isEmailExecutiveGoal"
									id="isEmailExecutiveGoal" /></td>
						</tr>
						<%-- <tr>
							<td valign="top" class="txtlabel alignRight">Enable/Disable Text Notification:</td>
							<td><s:checkbox name="isTextExecutiveGoal"/></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Text Message</td>
							<td><s:textfield cssStyle="width:480px" name="strTextExecutiveGoal"/></td>
						</tr> --%>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Subject:</td>
							<td><s:textfield cssStyle="width:480px"
									name="strSubjectExecutiveGoal" /></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Body:</td>
							<td><div style="float: left; margin-right: 20px;">
									<s:textarea name="strBodyExecutiveGoal" cols="63" rows="10" />
								</div>
								<div style="float: left;">
									[EMPFNAME] <br /> [EMPLNAME] <br /> [GOAL_ASSIGNER_NAME] <br />
									[GOAL_NAME] <br /> [LEGAL_ENTITY_NAME] <br />
								</div></td>
						</tr>
					</table>
				</div>
			</div>
			<!-- /.box-body -->
		</div>


		<div class="box box-primary collapsed-box"
			style="border-top-color: #EEEEEE; margin-top: 10px;">

			<div class="box-header with-border">
				<h3 class="box-title" style="font-size: 14px; padding-right: 10px;">
					Employee KRA Created
					<%-- <%=uF.showData(hmNotificationStatus.get("NOT_"+IConstants.N_EXECUTIVE_KRA), "") %> --%>
					<%
						strNotId = "" + IConstants.N_EXECUTIVE_KRA;
									isMail = (Boolean) request.getAttribute("isEmailExecutiveKRA");
									strEImage = isMail ? "mail_enbl.png" : "mail_disbl.png";
									strEEnable = isMail ? "Disable Mail Notification" : "Enable Mail Notification";

									if (isMail) {
										isMail = false;
									} else {
										isMail = true;
									}
					%>
					<span id="myDivE<%=strNotId%>" style="float: right"> <img
						width="20px" src="images1/<%=strEImage%>"
						title="<%=strEEnable%>"
						onclick="updateEmailStatus('myDivE<%=strNotId%>',<%=strNotId%>,<%=isMail%>,'isEmailExecutiveKRA');" />
					</span>
				</h3>
				<div class="box-tools pull-right">
					<button class="btn btn-box-tool" data-widget="collapse">
						<i class="fa fa-plus"></i>
					</button>
					<button class="btn btn-box-tool" data-widget="remove">
						<i class="fa fa-times"></i>
					</button>
				</div>
			</div>
			<!-- /.box-header -->
			<div class="box-body"
				style="padding: 5px; overflow-y: auto; display: none;">
				<div class="content1" style="margin: 0px 0px 10px 0px">
					<table class="table table_no_border" cellspacing="2"
						cellpadding="2" width="100%">
						<tr>
							<td valign="top" class="txtlabel alignRight">Email
								Notification:</td>
							<td><s:checkbox name="isEmailExecutiveKRA"
									id="isEmailExecutiveKRA" /></td>
						</tr>
						<%-- <tr>
							<td valign="top" class="txtlabel alignRight">Enable/Disable Text Notification:</td>
							<td><s:checkbox name="isTextExecutiveKRA"/></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Text Message</td>
							<td><s:textfield cssStyle="width:480px" name="strTextExecutiveKRA"/></td>
						</tr> --%>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Subject:</td>
							<td><s:textfield cssStyle="width:480px"
									name="strSubjectExecutiveKRA" /></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Body:</td>
							<td><div style="float: left; margin-right: 20px;">
									<s:textarea name="strBodyExecutiveKRA" cols="63" rows="10" />
								</div>
								<div style="float: left;">
									[EMPFNAME] <br /> [EMPLNAME] <br /> [GOAL_ASSIGNER_NAME] <br />
									[GOAL_NAME] <br /> [KRA_NAME] <br /> [LEGAL_ENTITY_NAME] <br />
								</div></td>
						</tr>
					</table>
				</div>
			</div>
			<!-- /.box-body -->
		</div>

<!-- Start Dattatray Date:12-08-21 Note : committed duplicated code -->
		<%-- <div class="box box-primary collapsed-box"
			style="border-top-color: #EEEEEE; margin-top: 10px;">

			<div class="box-header with-border">
				<h3 class="box-title" style="font-size: 14px; padding-right: 10px;">
					Employee Target Created
					<%
					strNotId = "" + IConstants.N_EXECUTIVE_TARGET;
								isMail = (Boolean) request.getAttribute("isEmailExecutiveTarget");
								strEImage = isMail ? "mail_enbl.png" : "mail_disbl.png";
								strEEnable = isMail ? "Disable Mail Notification" : "Enable Mail Notification";

								if (isMail) {
									isMail = false;
								} else {
									isMail = true;
								}
				%>
					<span id="myDivE<%=strNotId%>" style="float: right"> <img
						width="20px" src="images1/<%=strEImage%>"
						title="<%=strEEnable%>"
						onclick="updateEmailStatus('myDivE<%=strNotId%>',<%=strNotId%>,<%=isMail%>,'isEmailExecutiveTarget');" />
					</span>
				</h3>
				<div class="box-tools pull-right">
					<button class="btn btn-box-tool" data-widget="collapse">
						<i class="fa fa-plus"></i>
					</button>
					<button class="btn btn-box-tool" data-widget="remove">
						<i class="fa fa-times"></i>
					</button>
				</div>
			</div>
			<!-- /.box-header -->
			<div class="box-body"
				style="padding: 5px; overflow-y: auto; display: none;">
				<div class="content1" style="margin: 0px 0px 10px 0px">
					<table class="table table_no_border" cellspacing="2"
						cellpadding="2" width="100%">
						<tr>
							<td valign="top" class="txtlabel alignRight">Email
								Notification:</td>
							<td><s:checkbox name="isEmailExecutiveTarget"
									id="isEmailExecutiveTarget" /></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Enable/Disable Text Notification:</td>
							<td><s:checkbox name="isTextExecutiveTarget"/></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Text Message</td>
							<td><s:textfield cssStyle="width:480px" name="strTextExecutiveTarget"/></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Subject:</td>
							<td><s:textfield cssStyle="width:480px"
									name="strSubjectExecutiveTarget" /></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Body:</td>
							<td><div style="float: left; margin-right: 20px;">
									<s:textarea name="strBodyExecutiveTarget" cols="63" rows="10" />
								</div>
								<div style="float: left;">
									[EMPFNAME] <br /> [EMPLNAME] <br /> [GOAL_ASSIGNER_NAME] <br />
									[TARGET_NAME] <br /> [TARGET_VALUE] <br /> [LEGAL_ENTITY_NAME]
									<br />
								</div></td>
						</tr>
					</table>
				</div>
			</div>
			<!-- /.box-body -->
		</div> --%>
<!-- END Dattatray Date:12-08-21  -->

		<div class="box box-primary collapsed-box"
			style="border-top-color: #EEEEEE; margin-top: 10px;">

			<div class="box-header with-border">
				<h3 class="box-title" style="font-size: 14px; padding-right: 10px;">
					Employee Target Created
					<%
					strNotId = "" + IConstants.N_EXECUTIVE_TARGET;
								isMail = (Boolean) request.getAttribute("isEmailExecutiveTarget");
								strEImage = isMail ? "mail_enbl.png" : "mail_disbl.png";
								strEEnable = isMail ? "Disable Mail Notification" : "Enable Mail Notification";

								if (isMail) {
									isMail = false;
								} else {
									isMail = true;
								}
				%>
					<span id="myDivE<%=strNotId%>" style="float: right"> <img
						width="20px" src="images1/<%=strEImage%>"
						title="<%=strEEnable%>"
						onclick="updateEmailStatus('myDivE<%=strNotId%>',<%=strNotId%>,<%=isMail%>,'isEmailExecutiveTarget');" />
					</span>
				</h3>
				<div class="box-tools pull-right">
					<button class="btn btn-box-tool" data-widget="collapse">
						<i class="fa fa-plus"></i>
					</button>
					<button class="btn btn-box-tool" data-widget="remove">
						<i class="fa fa-times"></i>
					</button>
				</div>
			</div>
			<!-- /.box-header -->
			<div class="box-body"
				style="padding: 5px; overflow-y: auto; display: none;">
				<div class="content1" style="margin: 0px 0px 10px 0px">
					<table class="table table_no_border" cellspacing="2"
						cellpadding="2" width="100%">
						<tr>
							<td valign="top" class="txtlabel alignRight">Email
								Notification:</td>
							<td><s:checkbox name="isEmailExecutiveTarget"
									id="isEmailExecutiveTarget" /></td>
						</tr>
						<%-- <tr>
							<td valign="top" class="txtlabel alignRight">Enable/Disable Text Notification:</td>
							<td><s:checkbox name="isTextExecutiveTarget"/></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Text Message</td>
							<td><s:textfield cssStyle="width:480px" name="strTextExecutiveTarget"/></td>
						</tr> --%>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Subject:</td>
							<td><s:textfield cssStyle="width:480px" name="strSubjectExecutiveTarget" /></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Body:</td>
							<td><div style="float: left; margin-right: 20px;">
									<s:textarea name="strBodyExecutiveTarget" cols="63" rows="10" />
								</div>
								<div style="float: left;">
									[EMPFNAME] <br /> [EMPLNAME] <br /> [GOAL_ASSIGNER_NAME] <br />
									[TARGET_NAME] <br /> [TARGET_VALUE] <br /> [LEGAL_ENTITY_NAME]
									<br />
								</div></td>
						</tr>
					</table>
				</div>
			</div>
			<!-- /.box-body -->
		</div>


		<div class="box box-primary collapsed-box"
			style="border-top-color: #EEEEEE; margin-top: 10px;">

			<div class="box-header with-border">
				<h3 class="box-title" style="font-size: 14px; padding-right: 10px;">
					Review Reminder
					<%
					strNotId = "" + IConstants.N_PENDING_REVIEW_REMINDER;
								isMail = (Boolean) request.getAttribute("isEmailPendingReviewReminder");
								strEImage = isMail ? "mail_enbl.png" : "mail_disbl.png";
								strEEnable = isMail ? "Disable Mail Notification" : "Enable Mail Notification";

								if (isMail) {
									isMail = false;
								} else {
									isMail = true;
								}
				%>
					<span id="myDivE<%=strNotId%>" style="float: right"> <img
						width="20px" src="images1/<%=strEImage%>"
						title="<%=strEEnable%>"
						onclick="updateEmailStatus('myDivE<%=strNotId%>',<%=strNotId%>,<%=isMail%>,'isEmailPendingReviewReminder');" />
					</span>
				</h3>
				<div class="box-tools pull-right">
					<button class="btn btn-box-tool" data-widget="collapse">
						<i class="fa fa-plus"></i>
					</button>
					<button class="btn btn-box-tool" data-widget="remove">
						<i class="fa fa-times"></i>
					</button>
				</div>
			</div>
			<!-- /.box-header -->
			<div class="box-body"
				style="padding: 5px; overflow-y: auto; display: none;">
				<div class="content1" style="margin: 0px 0px 10px 0px">
					<table class="table table_no_border" cellspacing="2"
						cellpadding="2" width="100%">
						<tr>
							<td valign="top" class="txtlabel alignRight">Email
								Notification:</td>
							<td><s:checkbox name="isEmailPendingReviewReminder"
									id="isEmailPendingReviewReminder" /></td>
						</tr>
						<%-- <tr>
							<td valign="top" class="txtlabel alignRight">Enable/Disable Text Notification:</td>
							<td><s:checkbox name="isTextPendingReviewReminder"/></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Text Message</td>
							<td><s:textfield cssStyle="width:480px" name="strTextPendingReviewReminder"/></td>
						</tr> --%>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Subject:</td>
							<td><s:textfield cssStyle="width:480px"
									name="strSubjectPendingReviewReminder" /></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Body:</td>
							<td><div style="float: left; margin-right: 20px;">
									<s:textarea name="strBodyPendingReviewReminder" cols="63"
										rows="10" />
								</div>
								<div style="float: left;">
									[EMPFNAME] <br /> [EMPLNAME] <br /> [REVIEW_NAME] <br />
									[REVIEW_STARTDATE] <br /> [REVIEW_ENDDATE] <br />
									[REVIEWEE_NAME] <br /> [ROLE_TYPE] <br /> [LOGIN_LINK] <br />
									[LEGAL_ENTITY_NAME] <br />
								</div></td>
						</tr>
					</table>
				</div>
			</div>
			<!-- /.box-body -->
		</div>


		<!-- -------------------------- Close  Performance --------------------------- -->
		<%
			}
		%>

		<br />
		<br />

		<%
			}
				if (PRODUCTTYPE != null && PRODUCTTYPE.equals("3")) {
		%>

		<%
			if (arrEnabledModules != null
							&& ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_PROJECT_MANAGEMENT + "") >= 0) {
		%>
		<!-- ------------------------- Project Management ---------------------------- -->

		<!-- ------------------------- Customer ---------------------------- -->
		<div
			style="width: 100%; text-align: center; font-size: 14px; font-weight: bold; margin: 7px;"
			class="">Customer</div>
		<div class="box box-primary collapsed-box"
			style="border-top-color: #EEEEEE; margin-top: 10px;">

			<div class="box-header with-border">
				<h3 class="box-title" style="font-size: 14px; padding-right: 10px;">
					New Customer Added Notification
					<%-- <%=uF.showData(hmNotificationStatus.get("NOT_"+IConstants.N_NEW_CLIENT), "") %> --%>
					<%
						strNotId = "" + IConstants.N_NEW_CLIENT;
									isMail = (Boolean) request.getAttribute("isEmailNewCustAdded");
									strEImage = isMail ? "mail_enbl.png" : "mail_disbl.png";
									strEEnable = isMail ? "Disable Mail Notification" : "Enable Mail Notification";
									if (isMail) {
										isMail = false;
									} else {
										isMail = true;
									}
					%>
					<span id="myDivE<%=strNotId%>" style="float: right"> <img
						width="20px" src="images1/<%=strEImage%>"
						title="<%=strEEnable%>"
						onclick="updateEmailStatus('myDivE<%=strNotId%>',<%=strNotId%>,<%=isMail%>,'isEmailNewCustAdded');" />
					</span>
				</h3>
				<div class="box-tools pull-right">
					<button class="btn btn-box-tool" data-widget="collapse">
						<i class="fa fa-plus"></i>
					</button>
					<button class="btn btn-box-tool" data-widget="remove">
						<i class="fa fa-times"></i>
					</button>
				</div>
			</div>
			<!-- /.box-header -->
			<div class="box-body"
				style="padding: 5px; overflow-y: auto; display: none;">
				<div class="content1" style="margin: 0px 0px 10px 0px">
					<table class="table table_no_border" cellspacing="2"
						cellpadding="2" width="100%">
						<tr>
							<td valign="top" class="txtlabel alignRight">Email
								Notification:</td>
							<td><s:checkbox name="isEmailNewCustAdded"
									id="isEmailNewCustAdded" /></td>
						</tr>
						<%-- <tr>
							<td valign="top" class="txtlabel alignRight">Enable/Disable Text Notification:</td>
							<td><s:checkbox name="isTextNewCustAdded"/></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Text Message</td>
							<td><s:textfield cssStyle="width:480px" name="strTextNewCustAdded"/></td>
						</tr> --%>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Subject:</td>
							<td><s:textfield cssStyle="width:480px"
									name="strSubjectNewCustAdded" /></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Body:</td>
							<td><div style="float: left; margin-right: 20px;">
									<s:textarea name="strBodyNewCustAdded" cols="63" rows="10" />
								</div>
								<div style="float: left;">
									[CLIENT_NAME] <br /> [CUST_FNAME] <br /> [CUST_LNAME] <br />
									[EMPFNAME] <br /> [EMPLNAME] <br /> [LEGAL_ENTITY_NAME] <br />
								</div></td>
						</tr>
					</table>
				</div>
			</div>
			<!-- /.box-body -->
		</div>

		<div class="box box-primary collapsed-box"
			style="border-top-color: #EEEEEE; margin-top: 10px;">

			<div class="box-header with-border">
				<h3 class="box-title" style="font-size: 14px; padding-right: 10px;">
					New Customer Contact Added Notification
					<%-- <%=uF.showData(hmNotificationStatus.get("NOT_"+IConstants.N_NEW_CLIENT_CONTACT), "") %> --%>
					<%
						strNotId = "" + IConstants.N_NEW_CLIENT_CONTACT;
									isMail = (Boolean) request.getAttribute("isEmailNewCustContactAdded");
									strEImage = isMail ? "mail_enbl.png" : "mail_disbl.png";
									strEEnable = isMail ? "Disable Mail Notification" : "Enable Mail Notification";

									if (isMail) {
										isMail = false;
									} else {
										isMail = true;
									}
					%>
					<span id="myDivE<%=strNotId%>" style="float: right"> <img
						width="20px" src="images1/<%=strEImage%>"
						title="<%=strEEnable%>"
						onclick="updateEmailStatus('myDivE<%=strNotId%>',<%=strNotId%>,<%=isMail%>,'isEmailNewCustContactAdded');" />
					</span>
				</h3>
				<div class="box-tools pull-right">
					<button class="btn btn-box-tool" data-widget="collapse">
						<i class="fa fa-plus"></i>
					</button>
					<button class="btn btn-box-tool" data-widget="remove">
						<i class="fa fa-times"></i>
					</button>
				</div>
			</div>
			<!-- /.box-header -->
			<div class="box-body"
				style="padding: 5px; overflow-y: auto; display: none;">
				<div class="content1" style="margin: 0px 0px 10px 0px">
					<table class="table table_no_border" cellspacing="2"
						cellpadding="2" width="100%">
						<tr>
							<td valign="top" class="txtlabel alignRight">Email
								Notification:</td>
							<td><s:checkbox name="isEmailNewCustContactAdded"
									id="isEmailNewCustContactAdded" /></td>
						</tr>
						<%-- <tr>
							<td valign="top" class="txtlabel alignRight">Enable/Disable Text Notification:</td>
							<td><s:checkbox name="isTextNewCustContactAdded"/></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Text Message</td>
							<td><s:textfield cssStyle="width:480px" name="strTextNewCustContactAdded"/></td>
						</tr> --%>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Subject:</td>
							<td><s:textfield cssStyle="width:480px"
									name="strSubjectNewCustContactAdded" /></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Body:</td>
							<td><div style="float: left; margin-right: 20px;">
									<s:textarea name="strBodyNewCustContactAdded" cols="63"
										rows="10" />
								</div>
								<div style="float: left;">
									[CUST_FNAME] <br /> [CUST_LNAME] <br /> [LOGIN_LINK] <br />
									[CUSTOMER_REGISTER_LINK] <br /> [USERNAME] <br /> [PASSWORD] <br />
									[LEGAL_ENTITY_NAME] <br />
								</div></td>
						</tr>
					</table>
				</div>
			</div>
			<!-- /.box-body -->
		</div>

		<!-- ------------------------- Close Customer ---------------------------- -->


		<!-- ------------------------- Project, Tasks & Issues ---------------------------- -->
		<div
			style="width: 100%; text-align: center; font-size: 14px; font-weight: bold; margin: 7px;"
			class="">Project, Tasks & Issues</div>
		<div class="box box-primary collapsed-box"
			style="border-top-color: #EEEEEE; margin-top: 10px;">

			<div class="box-header with-border">
				<h3 class="box-title" style="font-size: 14px; padding-right: 10px;">
					New Project Created Notification
					<%-- <%=uF.showData(hmNotificationStatus.get("NOT_"+IConstants.N_CREATE_NEW_PROJECT), "") %> --%>
					<%
						strNotId = "" + IConstants.N_CREATE_NEW_PROJECT;
									isMail = (Boolean) request.getAttribute("isEmailNewProWelcomeToCust");
									strEImage = isMail ? "mail_enbl.png" : "mail_disbl.png";
									strEEnable = isMail ? "Disable Mail Notification" : "Enable Mail Notification";

									if (isMail) {
										isMail = false;
									} else {
										isMail = true;
									}
					%>
					<span id="myDivE<%=strNotId%>" style="float: right"> <img
						width="20px" src="images1/<%=strEImage%>"
						title="<%=strEEnable%>"
						onclick="updateEmailStatus('myDivE<%=strNotId%>',<%=strNotId%>,<%=isMail%>,'isEmailNewProWelcomeToCust');" />
					</span>
				</h3>
				<div class="box-tools pull-right">
					<button class="btn btn-box-tool" data-widget="collapse">
						<i class="fa fa-plus"></i>
					</button>
					<button class="btn btn-box-tool" data-widget="remove">
						<i class="fa fa-times"></i>
					</button>
				</div>
			</div>
			<!-- /.box-header -->
			<div class="box-body"
				style="padding: 5px; overflow-y: auto; display: none;">
				<div class="content1" style="margin: 0px 0px 10px 0px">
					<table class="table table_no_border" cellspacing="2"
						cellpadding="2" width="100%">
						<tr>
							<td valign="top" class="txtlabel alignRight">Email
								Notification:</td>
							<td><s:checkbox name="isEmailNewProWelcomeToCust"
									id="isEmailNewProWelcomeToCust" /></td>
						</tr>
						<%-- <tr>
							<td valign="top" class="txtlabel alignRight">Enable/Disable Text Notification:</td>
							<td><s:checkbox name="isTextNewProWelcomeToCust"/></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Text Message</td>
							<td><s:textfield cssStyle="width:480px" name="strTextNewProWelcomeToCust"/></td>
						</tr> --%>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Subject:</td>
							<td><s:textfield cssStyle="width:480px"
									name="strSubjectNewProWelcomeToCust" /></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Body:</td>
							<td><div style="float: left; margin-right: 20px;">
									<s:textarea name="strBodyNewProWelcomeToCust" cols="63"
										rows="10" />
								</div>
								<div style="float: left;">
									[CUST_FNAME] <br /> [CUST_LNAME] <br /> [PROJECT_NAME] <br />
									[PROJECT_DESCRIPTION] <br /> [PROJECT_OWNER] <br />
									[ORGANIZATION_NAME] <br /> [DONE_BY] <br /> [LEGAL_ENTITY_NAME]
									<br />
								</div></td>
						</tr>
					</table>
				</div>
			</div>
			<!-- /.box-body -->
		</div>

		<div class="box box-primary collapsed-box"
			style="border-top-color: #EEEEEE; margin-top: 10px;">

			<div class="box-header with-border">
				<h3 class="box-title" style="font-size: 14px; padding-right: 10px;">
					Project Updated Notification
					<%-- <%=uF.showData(hmNotificationStatus.get("NOT_"+IConstants.N_UPDATE_PROJECT), "") %> --%>
					<%
						strNotId = "" + IConstants.N_UPDATE_PROJECT;
									isMail = (Boolean) request.getAttribute("isEmailProUpdated");
									strEImage = isMail ? "mail_enbl.png" : "mail_disbl.png";
									strEEnable = isMail ? "Disable Mail Notification" : "Enable Mail Notification";

									if (isMail) {
										isMail = false;
									} else {
										isMail = true;
									}
					%>
					<span id="myDivE<%=strNotId%>" style="float: right"> <img
						width="20px" src="images1/<%=strEImage%>"
						title="<%=strEEnable%>"
						onclick="updateEmailStatus('myDivE<%=strNotId%>',<%=strNotId%>,<%=isMail%>,'isEmailProUpdated');" />
					</span>
				</h3>
				<div class="box-tools pull-right">
					<button class="btn btn-box-tool" data-widget="collapse">
						<i class="fa fa-plus"></i>
					</button>
					<button class="btn btn-box-tool" data-widget="remove">
						<i class="fa fa-times"></i>
					</button>
				</div>
			</div>
			<!-- /.box-header -->
			<div class="box-body"
				style="padding: 5px; overflow-y: auto; display: none;">
				<div class="content1" style="margin: 0px 0px 10px 0px">
					<table class="table table_no_border" cellspacing="2"
						cellpadding="2" width="100%">
						<tr>
							<td valign="top" class="txtlabel alignRight">Email
								Notification:</td>
							<td><s:checkbox name="isEmailProUpdated"
									id="isEmailProUpdated" /></td>
						</tr>
						<%-- <tr>
							<td valign="top" class="txtlabel alignRight">Enable/Disable Text Notification:</td>
							<td><s:checkbox name="isTextProUpdated"/></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Text Message</td>
							<td><s:textfield cssStyle="width:480px" name="strTextProUpdated"/></td>
						</tr> --%>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Subject:</td>
							<td><s:textfield cssStyle="width:480px"
									name="strSubjectProUpdated" /></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Body:</td>
							<td><div style="float: left; margin-right: 20px;">
									<s:textarea name="strBodyProUpdated" cols="63" rows="10" />
								</div>
								<div style="float: left;">
									[CUST_FNAME] <br /> [CUST_LNAME] <br /> [PROJECT_NAME] <br />
									[PROJECT_DESCRIPTION] <br /> [PROJECT_OWNER] <br />
									[ORGANIZATION_NAME] <br /> [DONE_BY] <br /> [LEGAL_ENTITY_NAME]
									<br />
								</div></td>
						</tr>
					</table>
				</div>
			</div>
			<!-- /.box-body -->
		</div>

		<div class="box box-primary collapsed-box"
			style="border-top-color: #EEEEEE; margin-top: 10px;">

			<div class="box-header with-border">
				<h3 class="box-title" style="font-size: 14px; padding-right: 10px;">
					New Task or Issue Assigned Notification
					<%-- <%=uF.showData(hmNotificationStatus.get("NOT_"+IConstants.N_NEW_TASK_ASSIGN), "") %> --%>
					<%
						strNotId = "" + IConstants.N_NEW_TASK_ASSIGN;
									isMail = (Boolean) request.getAttribute("isEmailNewTask");
									strEImage = isMail ? "mail_enbl.png" : "mail_disbl.png";
									strEEnable = isMail ? "Disable Mail Notification" : "Enable Mail Notification";

									if (isMail) {
										isMail = false;
									} else {
										isMail = true;
									}
					%>
					<span id="myDivE<%=strNotId%>" style="float: right"> <img
						width="20px" src="images1/<%=strEImage%>"
						title="<%=strEEnable%>"
						onclick="updateEmailStatus('myDivE<%=strNotId%>',<%=strNotId%>,<%=isMail%>,'isEmailNewTask');" />
					</span>
				</h3>
				<div class="box-tools pull-right">
					<button class="btn btn-box-tool" data-widget="collapse">
						<i class="fa fa-plus"></i>
					</button>
					<button class="btn btn-box-tool" data-widget="remove">
						<i class="fa fa-times"></i>
					</button>
				</div>
			</div>
			<!-- /.box-header -->
			<div class="box-body"
				style="padding: 5px; overflow-y: auto; display: none;">
				<div class="content1" style="margin: 0px 0px 10px 0px">
					<table class="table table_no_border" cellspacing="2"
						cellpadding="2" width="100%">
						<tr>
							<td valign="top" class="txtlabel alignRight">Email
								Notification:</td>
							<td><s:checkbox name="isEmailNewTask" id="isEmailNewTask" /></td>
						</tr>
						<%-- <tr>
							<td valign="top" class="txtlabel alignRight">Enable/Disable Text Notification:</td>
							<td><s:checkbox name="isTextNewTask"/></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Text Message</td>
							<td><s:textfield cssStyle="width:480px" name="strTextNewTask"/></td>
						</tr> --%>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Subject:</td>
							<td><s:textfield cssStyle="width:480px"
									name="strSubjectNewTask" /></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Body:</td>
							<td><div style="float: left; margin-right: 20px;">
									<s:textarea name="strBodyNewTask" cols="63" rows="10" />
								</div>
								<div style="float: left;">
									[RESOURCE_FNAME] <br /> [RESOURCE_LNAME] <br /> [TASK_NAME] <br />
									[PROJECT_NAME] <br /> [TEAM_LEADER] <br /> [PROJECT_OWNER] <br />
									[EMPFNAME] <br /> [EMPLNAME] <br /> [DONE_BY] <br />
									[LEGAL_ENTITY_NAME] <br />
								</div></td>
						</tr>
					</table>
				</div>
			</div>
			<!-- /.box-body -->
		</div>

		<div class="box box-primary collapsed-box"
			style="border-top-color: #EEEEEE; margin-top: 10px;">

			<div class="box-header with-border">
				<h3 class="box-title" style="font-size: 14px; padding-right: 10px;">
					New Document Shared Notification
					<%-- <%=uF.showData(hmNotificationStatus.get("NOT_"+IConstants.N_NEW_DOCUMENT_SHARED), "") %> --%>
					<%
						strNotId = "" + IConstants.N_NEW_DOCUMENT_SHARED;
									isMail = (Boolean) request.getAttribute("isEmailNewDocShared");
									strEImage = isMail ? "mail_enbl.png" : "mail_disbl.png";
									strEEnable = isMail ? "Disable Mail Notification" : "Enable Mail Notification";

									if (isMail) {
										isMail = false;
									} else {
										isMail = true;
									}
					%>
					<span id="myDivE<%=strNotId%>" style="float: right"> <img
						width="20px" src="images1/<%=strEImage%>"
						title="<%=strEEnable%>"
						onclick="updateEmailStatus('myDivE<%=strNotId%>',<%=strNotId%>,<%=isMail%>,'isEmailNewDocShared');" />
					</span>
				</h3>
				<div class="box-tools pull-right">
					<button class="btn btn-box-tool" data-widget="collapse">
						<i class="fa fa-plus"></i>
					</button>
					<button class="btn btn-box-tool" data-widget="remove">
						<i class="fa fa-times"></i>
					</button>
				</div>
			</div>
			<!-- /.box-header -->
			<div class="box-body"
				style="padding: 5px; overflow-y: auto; display: none;">
				<div class="content1" style="margin: 0px 0px 10px 0px">
					<table class="table table_no_border" cellspacing="2"
						cellpadding="2" width="100%">
						<tr>
							<td valign="top" class="txtlabel alignRight">Email
								Notification:</td>
							<td><s:checkbox name="isEmailNewDocShared"
									id="isEmailNewDocShared" /></td>
						</tr>
						<%-- <tr>
							<td valign="top" class="txtlabel alignRight">Enable/Disable Text Notification:</td>
							<td><s:checkbox name="isTextNewDocShared"/></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Text Message</td>
							<td><s:textfield cssStyle="width:480px" name="strTextNewDocShared"/></td>
						</tr> --%>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Subject:</td>
							<td><s:textfield cssStyle="width:480px"
									name="strSubjectNewDocShared" /></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Body:</td>
							<td><div style="float: left; margin-right: 20px;">
									<s:textarea name="strBodyNewDocShared" cols="63" rows="10" />
								</div>
								<div style="float: left;">
									[RESOURCE_FNAME] <br /> [RESOURCE_LNAME] <br /> [DOCUMENT_NAME]
									<br /> [EMPFNAME] <br /> [EMPLNAME] <br /> [DONE_BY] <br />
									[LEGAL_ENTITY_NAME] <br />
								</div></td>
						</tr>
					</table>
				</div>
			</div>
			<!-- /.box-body -->
		</div>

		<div class="box box-primary collapsed-box"
			style="border-top-color: #EEEEEE; margin-top: 10px;">

			<div class="box-header with-border">
				<h3 class="box-title" style="font-size: 14px; padding-right: 10px;">
					Task or Issue Completion Notification
					<%-- <%=uF.showData(hmNotificationStatus.get("NOT_"+IConstants.N_TASK_COMPLETED), "") %> --%>
					<%
						strNotId = "" + IConstants.N_TASK_COMPLETED;
									isMail = (Boolean) request.getAttribute("isEmailTaskCompleted");
									strEImage = isMail ? "mail_enbl.png" : "mail_disbl.png";
									strEEnable = isMail ? "Disable Mail Notification" : "Enable Mail Notification";

									if (isMail) {
										isMail = false;
									} else {
										isMail = true;
									}
					%>
					<span id="myDivE<%=strNotId%>" style="float: right"> <img
						width="20px" src="images1/<%=strEImage%>"
						title="<%=strEEnable%>"
						onclick="updateEmailStatus('myDivE<%=strNotId%>',<%=strNotId%>,<%=isMail%>,'isEmailTaskCompleted');" />
					</span>
				</h3>
				<div class="box-tools pull-right">
					<button class="btn btn-box-tool" data-widget="collapse">
						<i class="fa fa-plus"></i>
					</button>
					<button class="btn btn-box-tool" data-widget="remove">
						<i class="fa fa-times"></i>
					</button>
				</div>
			</div>
			<!-- /.box-header -->
			<div class="box-body"
				style="padding: 5px; overflow-y: auto; display: none;">
				<div class="content1" style="margin: 0px 0px 10px 0px">
					<table class="table table_no_border" cellspacing="2"
						cellpadding="2" width="100%">
						<tr>
							<td valign="top" class="txtlabel alignRight">Email
								Notification:</td>
							<td><s:checkbox name="isEmailTaskCompleted"
									id="isEmailTaskCompleted" /></td>
						</tr>
						<%-- <tr>
							<td valign="top" class="txtlabel alignRight">Enable/Disable Text Notification:</td>
							<td><s:checkbox name="isTextTaskCompleted"/></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Text Message</td>
							<td><s:textfield cssStyle="width:480px" name="strTextTaskCompleted"/></td>
						</tr> --%>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Subject:</td>
							<td><s:textfield cssStyle="width:480px"
									name="strSubjectTaskCompleted" /></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Body:</td>
							<td><div style="float: left; margin-right: 20px;">
									<s:textarea name="strBodyTaskCompleted" cols="63" rows="10" />
								</div>
								<div style="float: left;">
									[TEAM_LEADER] <br /> [RESOURCE_FNAME] <br /> [RESOURCE_LNAME] <br />
									[TASK_NAME] <br /> [PROJECT_NAME] <br /> [DONE_BY] <br />
									[LEGAL_ENTITY_NAME] <br />
								</div></td>
						</tr>
					</table>
				</div>
			</div>
			<!-- /.box-body -->
		</div>

		<div class="box box-primary collapsed-box"
			style="border-top-color: #EEEEEE; margin-top: 10px;">

			<div class="box-header with-border">
				<h3 class="box-title" style="font-size: 14px; padding-right: 10px;">
					Project Completion Notification
					<%-- <%=uF.showData(hmNotificationStatus.get("NOT_"+IConstants.N_PROJECT_COMPLETED), "") %> --%>
					<%
						strNotId = "" + IConstants.N_PROJECT_COMPLETED;
									isMail = (Boolean) request.getAttribute("isEmailProCompleted");
									strEImage = isMail ? "mail_enbl.png" : "mail_disbl.png";
									strEEnable = isMail ? "Disable Mail Notification" : "Enable Mail Notification";

									if (isMail) {
										isMail = false;
									} else {
										isMail = true;
									}
					%>
					<span id="myDivE<%=strNotId%>" style="float: right"> <img
						width="20px" src="images1/<%=strEImage%>"
						title="<%=strEEnable%>"
						onclick="updateEmailStatus('myDivE<%=strNotId%>',<%=strNotId%>,<%=isMail%>,'isEmailProCompleted');" />
					</span>
				</h3>
				<div class="box-tools pull-right">
					<button class="btn btn-box-tool" data-widget="collapse">
						<i class="fa fa-plus"></i>
					</button>
					<button class="btn btn-box-tool" data-widget="remove">
						<i class="fa fa-times"></i>
					</button>
				</div>
			</div>
			<!-- /.box-header -->
			<div class="box-body"
				style="padding: 5px; overflow-y: auto; display: none;">
				<div class="content1" style="margin: 0px 0px 10px 0px">
					<table class="table table_no_border" cellspacing="2"
						cellpadding="2" width="100%">
						<tr>
							<td valign="top" class="txtlabel alignRight">Email
								Notification:</td>
							<td><s:checkbox name="isEmailProCompleted"
									id="isEmailProCompleted" /></td>
						</tr>
						<%-- <tr>
							<td valign="top" class="txtlabel alignRight">Enable/Disable Text Notification:</td>
							<td><s:checkbox name="isTextProCompleted"/></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Text Message</td>
							<td><s:textfield cssStyle="width:480px" name="strTextProCompleted"/></td>
						</tr> --%>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Subject:</td>
							<td><s:textfield cssStyle="width:480px"
									name="strSubjectProCompleted" /></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Body:</td>
							<td><div style="float: left; margin-right: 20px;">
									<s:textarea name="strBodyProCompleted" cols="63" rows="10" />
								</div>
								<div style="float: left;">
									[EMPFNAME] <br /> [EMPLNAME] <br /> [PROJECT_NAME] <br />
									[DONE_BY] <br /> [LEGAL_ENTITY_NAME] <br />
								</div></td>
						</tr>
					</table>
				</div>
			</div>
			<!-- /.box-body -->
		</div>

		<div class="box box-primary collapsed-box"
			style="border-top-color: #EEEEEE; margin-top: 10px;">

			<div class="box-header with-border">
				<h3 class="box-title" style="font-size: 14px; padding-right: 10px;">
					Project Blocked Notification
					<%-- <%=uF.showData(hmNotificationStatus.get("NOT_"+IConstants.N_PROJECT_BLOCKED), "") %> --%>
					<%
						strNotId = "" + IConstants.N_PROJECT_BLOCKED;
									isMail = (Boolean) request.getAttribute("isEmailProBlocked");
									strEImage = isMail ? "mail_enbl.png" : "mail_disbl.png";
									strEEnable = isMail ? "Disable Mail Notification" : "Enable Mail Notification";

									if (isMail) {
										isMail = false;
									} else {
										isMail = true;
									}
					%>
					<span id="myDivE<%=strNotId%>" style="float: right"> <img
						width="20px" src="images1/<%=strEImage%>"
						title="<%=strEEnable%>"
						onclick="updateEmailStatus('myDivE<%=strNotId%>',<%=strNotId%>,<%=isMail%>,'isEmailProBlocked');" />
					</span>
				</h3>
				<div class="box-tools pull-right">
					<button class="btn btn-box-tool" data-widget="collapse">
						<i class="fa fa-plus"></i>
					</button>
					<button class="btn btn-box-tool" data-widget="remove">
						<i class="fa fa-times"></i>
					</button>
				</div>
			</div>
			<!-- /.box-header -->
			<div class="box-body"
				style="padding: 5px; overflow-y: auto; display: none;">
				<div class="content1" style="margin: 0px 0px 10px 0px">
					<table class="table table_no_border" cellspacing="2"
						cellpadding="2" width="100%">
						<tr>
							<td valign="top" class="txtlabel alignRight">Email
								Notification:</td>
							<td><s:checkbox name="isEmailProBlocked"
									id="isEmailProBlocked" /></td>
						</tr>
						<%-- <tr>
							<td valign="top" class="txtlabel alignRight">Enable/Disable Text Notification:</td>
							<td><s:checkbox name="isTextProBlocked"/></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Text Message</td>
							<td><s:textfield cssStyle="width:480px" name="strTextProBlocked"/></td>
						</tr> --%>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Subject:</td>
							<td><s:textfield cssStyle="width:480px"
									name="strSubjectProBlocked" /></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Body:</td>
							<td><div style="float: left; margin-right: 20px;">
									<s:textarea name="strBodyProBlocked" cols="63" rows="10" />
								</div>
								<div style="float: left;">
									[EMPFNAME] <br /> [EMPLNAME] <br /> [PROJECT_NAME] <br />
									[DONE_BY] <br /> [LEGAL_ENTITY_NAME] <br />
								</div></td>
						</tr>
					</table>
				</div>
			</div>
			<!-- /.box-body -->
		</div>

		<div class="box box-primary collapsed-box"
			style="border-top-color: #EEEEEE; margin-top: 10px;">

			<div class="box-header with-border">
				<h3 class="box-title" style="font-size: 14px; padding-right: 10px;">
					Milestone Completion Notification
					<%-- <%=uF.showData(hmNotificationStatus.get("NOT_"+IConstants.N_MILESTONE_COMPLETED), "") %> --%>
					<%
						strNotId = "" + IConstants.N_MILESTONE_COMPLETED;
									isMail = (Boolean) request.getAttribute("isEmailMilestoneCompleted");
									strEImage = isMail ? "mail_enbl.png" : "mail_disbl.png";
									strEEnable = isMail ? "Disable Mail Notification" : "Enable Mail Notification";

									if (isMail) {
										isMail = false;
									} else {
										isMail = true;
									}
					%>
					<span id="myDivE<%=strNotId%>" style="float: right"> <img
						width="20px" src="images1/<%=strEImage%>"
						title="<%=strEEnable%>"
						onclick="updateEmailStatus('myDivE<%=strNotId%>',<%=strNotId%>,<%=isMail%>,'isEmailMilestoneCompleted');" />
					</span>
				</h3>
				<div class="box-tools pull-right">
					<button class="btn btn-box-tool" data-widget="collapse">
						<i class="fa fa-plus"></i>
					</button>
					<button class="btn btn-box-tool" data-widget="remove">
						<i class="fa fa-times"></i>
					</button>
				</div>
			</div>
			<!-- /.box-header -->
			<div class="box-body"
				style="padding: 5px; overflow-y: auto; display: none;">
				<div class="content1" style="margin: 0px 0px 10px 0px">
					<table class="table table_no_border" cellspacing="2"
						cellpadding="2" width="100%">
						<tr>
							<td valign="top" class="txtlabel alignRight">Email
								Notification:</td>
							<td><s:checkbox name="isEmailMilestoneCompleted"
									id="isEmailMilestoneCompleted" /></td>
						</tr>
						<%-- <tr>
							<td valign="top" class="txtlabel alignRight">Enable/Disable Text Notification:</td>
							<td><s:checkbox name="isTextMilestoneCompleted"/></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Text Message</td>
							<td><s:textfield cssStyle="width:480px" name="strTextMilestoneCompleted"/></td>
						</tr> --%>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Subject:</td>
							<td><s:textfield cssStyle="width:480px"
									name="strSubjectMilestoneCompleted" /></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Body:</td>
							<td><div style="float: left; margin-right: 20px;">
									<s:textarea name="strBodyMilestoneCompleted" cols="63"
										rows="10" />
								</div>
								<div style="float: left;">
									[EMPFNAME] <br /> [EMPLNAME] <br /> [PROJECT_NAME] <br />
									[DONE_BY] <br /> [LEGAL_ENTITY_NAME] <br />
								</div></td>
						</tr>
					</table>
				</div>
			</div>
			<!-- /.box-body -->
		</div>

		<div class="box box-primary collapsed-box"
			style="border-top-color: #EEEEEE; margin-top: 10px;">

			<div class="box-header with-border">
				<h3 class="box-title" style="font-size: 14px; padding-right: 10px;">
					Project Re-opened Notification
					<%-- <%=uF.showData(hmNotificationStatus.get("NOT_"+IConstants.N_PROJECT_RE_OPENED), "") %> --%>
					<%
						strNotId = "" + IConstants.N_PROJECT_RE_OPENED;
									isMail = (Boolean) request.getAttribute("isEmailProReopened");
									strEImage = isMail ? "mail_enbl.png" : "mail_disbl.png";
									strEEnable = isMail ? "Disable Mail Notification" : "Enable Mail Notification";

									if (isMail) {
										isMail = false;
									} else {
										isMail = true;
									}
					%>
					<span id="myDivE<%=strNotId%>" style="float: right"> <img
						width="20px" src="images1/<%=strEImage%>"
						title="<%=strEEnable%>"
						onclick="updateEmailStatus('myDivE<%=strNotId%>',<%=strNotId%>,<%=isMail%>,'isEmailProReopened');" />
					</span>
				</h3>
				<div class="box-tools pull-right">
					<button class="btn btn-box-tool" data-widget="collapse">
						<i class="fa fa-plus"></i>
					</button>
					<button class="btn btn-box-tool" data-widget="remove">
						<i class="fa fa-times"></i>
					</button>
				</div>
			</div>
			<!-- /.box-header -->
			<div class="box-body"
				style="padding: 5px; overflow-y: auto; display: none;">
				<div class="content1" style="margin: 0px 0px 10px 0px">
					<table class="table table_no_border" cellspacing="2"
						cellpadding="2" width="100%">
						<tr>
							<td valign="top" class="txtlabel alignRight">Email
								Notification:</td>
							<td><s:checkbox name="isEmailProReopened"
									id="isEmailProReopened" /></td>
						</tr>
						<%-- <tr>
							<td valign="top" class="txtlabel alignRight">Enable/Disable Text Notification:</td>
							<td><s:checkbox name="isTextProReopened"/></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Text Message</td>
							<td><s:textfield cssStyle="width:480px" name="strTextProReopened"/></td>
						</tr> --%>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Subject:</td>
							<td><s:textfield cssStyle="width:480px"
									name="strSubjectProReopened" /></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Body:</td>
							<td><div style="float: left; margin-right: 20px;">
									<s:textarea name="strBodyProReopened" cols="63" rows="10" />
								</div>
								<div style="float: left;">
									[EMPFNAME] <br /> [EMPLNAME] <br /> [PROJECT_NAME] <br />
									[DONE_BY] <br /> [LEGAL_ENTITY_NAME] <br />
								</div></td>
						</tr>
					</table>
				</div>
			</div>
			<!-- /.box-body -->
		</div>



		<!-- ------------------------- Close Project, Tasks & Issues ---------------------------- -->


		<!-- ------------------------- TimeSheet ---------------------------- -->

		<div
			style="width: 100%; text-align: center; font-size: 14px; font-weight: bold; margin: 7px;"
			class="">Timesheet</div>
		<div class="box box-primary collapsed-box"
			style="border-top-color: #EEEEEE; margin-top: 10px;">

			<div class="box-header with-border">
				<h3 class="box-title" style="font-size: 14px; padding-right: 10px;">
					Timesheet Submitted Notification
					<%-- <%=uF.showData(hmNotificationStatus.get("NOT_"+IConstants.N_TIMESHEET_SUBMITED), "") %> --%>
					<%
						strNotId = "" + IConstants.N_TIMESHEET_SUBMITED;
									isMail = (Boolean) request.getAttribute("isEmailTimesheetSubmitted");
									strEImage = isMail ? "mail_enbl.png" : "mail_disbl.png";
									strEEnable = isMail ? "Disable Mail Notification" : "Enable Mail Notification";

									if (isMail) {
										isMail = false;
									} else {
										isMail = true;
									}
					%>
					<span id="myDivE<%=strNotId%>" style="float: right"> <img
						width="20px" src="images1/<%=strEImage%>"
						title="<%=strEEnable%>"
						onclick="updateEmailStatus('myDivE<%=strNotId%>',<%=strNotId%>,<%=isMail%>,'isEmailTimesheetSubmitted');" />
					</span>
				</h3>
				<div class="box-tools pull-right">
					<button class="btn btn-box-tool" data-widget="collapse">
						<i class="fa fa-plus"></i>
					</button>
					<button class="btn btn-box-tool" data-widget="remove">
						<i class="fa fa-times"></i>
					</button>
				</div>
			</div>
			<!-- /.box-header -->
			<div class="box-body"
				style="padding: 5px; overflow-y: auto; display: none;">
				<div class="content1" style="margin: 0px 0px 10px 0px">
					<table class="table table_no_border" cellspacing="2"
						cellpadding="2" width="100%">
						<tr>
							<td valign="top" class="txtlabel alignRight">Email
								Notification:</td>
							<td><s:checkbox name="isEmailTimesheetSubmitted"
									id="isEmailTimesheetSubmitted" /></td>
						</tr>
						<%-- <tr>
							<td valign="top" class="txtlabel alignRight">Enable/Disable Text Notification:</td>
							<td><s:checkbox name="isTextTimesheetSubmitted"/></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Text Message</td>
							<td><s:textfield cssStyle="width:480px" name="strTextTimesheetSubmitted"/></td>
						</tr> --%>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Subject:</td>
							<td><s:textfield cssStyle="width:480px"
									name="strSubjectTimesheetSubmitted" /></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Body:</td>
							<td><div style="float: left; margin-right: 20px;">
									<s:textarea name="strBodyTimesheetSubmitted" cols="63"
										rows="10" />
								</div>
								<div style="float: left;">
									[EMPFNAME] <br /> [EMPLNAME] <br /> [FROM_DATE] <br />
									[TO_DATE] <br /> [DONE_BY] <br /> [LEGAL_ENTITY_NAME] <br />
								</div></td>
						</tr>
					</table>
				</div>
			</div>
			<!-- /.box-body -->
		</div>

		<div class="box box-primary collapsed-box"
			style="border-top-color: #EEEEEE; margin-top: 10px;">

			<div class="box-header with-border">
				<h3 class="box-title" style="font-size: 14px; padding-right: 10px;">
					Timesheet Re-opened Notification
					<%-- <%=uF.showData(hmNotificationStatus.get("NOT_"+IConstants.N_TIMESHEET_RE_OPENED), "") %> --%>
					<%
						strNotId = "" + IConstants.N_TIMESHEET_RE_OPENED;
									isMail = (Boolean) request.getAttribute("isEmailTimesheetReopened");
									strEImage = isMail ? "mail_enbl.png" : "mail_disbl.png";
									strEEnable = isMail ? "Disable Mail Notification" : "Enable Mail Notification";

									if (isMail) {
										isMail = false;
									} else {
										isMail = true;
									}
					%>
					<span id="myDivE<%=strNotId%>" style="float: right"> <img
						width="20px" src="images1/<%=strEImage%>"
						title="<%=strEEnable%>"
						onclick="updateEmailStatus('myDivE<%=strNotId%>',<%=strNotId%>,<%=isMail%>,'isEmailTimesheetReopened');" />
					</span>
				</h3>
				<div class="box-tools pull-right">
					<button class="btn btn-box-tool" data-widget="collapse">
						<i class="fa fa-plus"></i>
					</button>
					<button class="btn btn-box-tool" data-widget="remove">
						<i class="fa fa-times"></i>
					</button>
				</div>
			</div>
			<!-- /.box-header -->
			<div class="box-body"
				style="padding: 5px; overflow-y: auto; display: none;">
				<div class="content1" style="margin: 0px 0px 10px 0px">
					<table class="table table_no_border" cellspacing="2"
						cellpadding="2" width="100%">
						<tr>
							<td valign="top" class="txtlabel alignRight">Email
								Notification:</td>
							<td><s:checkbox name="isEmailTimesheetReopened"
									id="isEmailTimesheetReopened" /></td>
						</tr>
						<%-- <tr>
							<td valign="top" class="txtlabel alignRight">Enable/Disable Text Notification:</td>
							<td><s:checkbox name="isTextTimesheetReopened"/></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Text Message</td>
							<td><s:textfield cssStyle="width:480px" name="strTextTimesheetReopened"/></td>
						</tr> --%>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Subject:</td>
							<td><s:textfield cssStyle="width:480px"
									name="strSubjectTimesheetReopened" /></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Body:</td>
							<td><div style="float: left; margin-right: 20px;">
									<s:textarea name="strBodyTimesheetReopened" cols="63" rows="10" />
								</div>
								<div style="float: left;">
									[RESOURCE_FNAME] <br /> [RESOURCE_LNAME] <br /> [FROM_DATE] <br />
									[TO_DATE] <br /> [DONE_BY] <br /> [LEGAL_ENTITY_NAME] <br />
								</div></td>
						</tr>
					</table>
				</div>
			</div>
			<!-- /.box-body -->
		</div>

		<div class="box box-primary collapsed-box"
			style="border-top-color: #EEEEEE; margin-top: 10px;">

			<div class="box-header with-border">
				<h3 class="box-title" style="font-size: 14px; padding-right: 10px;">
					Timesheet Approved Notification
					<%-- <%=uF.showData(hmNotificationStatus.get("NOT_"+IConstants.N_TIMESHEET_APPROVED), "") %> --%>
					<%
						strNotId = "" + IConstants.N_TIMESHEET_APPROVED;
									isMail = (Boolean) request.getAttribute("isEmailTimesheetApproved");
									strEImage = isMail ? "mail_enbl.png" : "mail_disbl.png";
									strEEnable = isMail ? "Disable Mail Notification" : "Enable Mail Notification";

									if (isMail) {
										isMail = false;
									} else {
										isMail = true;
									}
					%>
					<span id="myDivE<%=strNotId%>" style="float: right"> <img
						width="20px" src="images1/<%=strEImage%>"
						title="<%=strEEnable%>"
						onclick="updateEmailStatus('myDivE<%=strNotId%>',<%=strNotId%>,<%=isMail%>,'isEmailTimesheetApproved');" />
					</span>
				</h3>
				<div class="box-tools pull-right">
					<button class="btn btn-box-tool" data-widget="collapse">
						<i class="fa fa-plus"></i>
					</button>
					<button class="btn btn-box-tool" data-widget="remove">
						<i class="fa fa-times"></i>
					</button>
				</div>
			</div>
			<!-- /.box-header -->
			<div class="box-body"
				style="padding: 5px; overflow-y: auto; display: none;">
				<div class="content1" style="margin: 0px 0px 10px 0px">
					<table class="table table_no_border" cellspacing="2"
						cellpadding="2" width="100%">
						<tr>
							<td valign="top" class="txtlabel alignRight">Email
								Notification:</td>
							<td><s:checkbox name="isEmailTimesheetApproved"
									id="isEmailTimesheetApproved" /></td>
						</tr>
						<%-- <tr>
							<td valign="top" class="txtlabel alignRight">Enable/Disable Text Notification:</td>
							<td><s:checkbox name="isTextTimesheetApproved"/></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Text Message</td>
							<td><s:textfield cssStyle="width:480px" name="strTextTimesheetApproved"/></td>
						</tr> --%>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Subject:</td>
							<td><s:textfield cssStyle="width:480px"
									name="strSubjectTimesheetApproved" /></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Body:</td>
							<td><div style="float: left; margin-right: 20px;">
									<s:textarea name="strBodyTimesheetApproved" cols="63" rows="10" />
								</div>
								<div style="float: left;">
									[RESOURCE_FNAME] <br /> [RESOURCE_LNAME] <br /> [FROM_DATE] <br />
									[TO_DATE] <br /> [DONE_BY] <br /> [LEGAL_ENTITY_NAME] <br />
								</div></td>
						</tr>
					</table>
				</div>
			</div>
			<!-- /.box-body -->
		</div>

		<div class="box box-primary collapsed-box"
			style="border-top-color: #EEEEEE; margin-top: 10px;">

			<div class="box-header with-border">
				<h3 class="box-title" style="font-size: 14px; padding-right: 10px;">
					Timesheet submitted to Customer Notification
					<%-- <%=uF.showData(hmNotificationStatus.get("NOT_"+IConstants.N_TIMESHEET_SUBMITED_TO_CUST), "") %> --%>
					<%
						strNotId = "" + IConstants.N_TIMESHEET_SUBMITED_TO_CUST;
									isMail = (Boolean) request.getAttribute("isEmailTimesheetSubmitToCust");
									strEImage = isMail ? "mail_enbl.png" : "mail_disbl.png";
									strEEnable = isMail ? "Disable Mail Notification" : "Enable Mail Notification";

									if (isMail) {
										isMail = false;
									} else {
										isMail = true;
									}
					%>
					<span id="myDivE<%=strNotId%>" style="float: right"> <img
						width="20px" src="images1/<%=strEImage%>"
						title="<%=strEEnable%>"
						onclick="updateEmailStatus('myDivE<%=strNotId%>',<%=strNotId%>,<%=isMail%>,'isEmailTimesheetSubmitToCust');" />
					</span>
				</h3>
				<div class="box-tools pull-right">
					<button class="btn btn-box-tool" data-widget="collapse">
						<i class="fa fa-plus"></i>
					</button>
					<button class="btn btn-box-tool" data-widget="remove">
						<i class="fa fa-times"></i>
					</button>
				</div>
			</div>
			<!-- /.box-header -->
			<div class="box-body"
				style="padding: 5px; overflow-y: auto; display: none;">
				<div class="content1" style="margin: 0px 0px 10px 0px">
					<table class="table table_no_border" cellspacing="2"
						cellpadding="2" width="100%">
						<tr>
							<td valign="top" class="txtlabel alignRight">Email
								Notification:</td>
							<td><s:checkbox name="isEmailTimesheetSubmitToCust"
									id="isEmailTimesheetSubmitToCust" /></td>
						</tr>
						<%-- <tr>
							<td valign="top" class="txtlabel alignRight">Enable/Disable Text Notification:</td>
							<td><s:checkbox name="isTextTimesheetSubmitToCust"/></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Text Message</td>
							<td><s:textfield cssStyle="width:480px" name="strTextTimesheetSubmitToCust"/></td>
						</tr> --%>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Subject:</td>
							<td><s:textfield cssStyle="width:480px"
									name="strSubjectTimesheetSubmitToCust" /></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Body:</td>
							<td><div style="float: left; margin-right: 20px;">
									<s:textarea name="strBodyTimesheetSubmitToCust" cols="63"
										rows="10" />
								</div>
								<div style="float: left;">
									[CUST_FNAME] <br /> [CUST_LNAME] <br />
									[PROJECT_FREQUENCY_NAME] <br /> [FROM_DATE] <br /> [TO_DATE] <br />
									[DONE_BY] <br /> [LEGAL_ENTITY_NAME] <br />
								</div></td>
						</tr>
					</table>
				</div>
			</div>
			<!-- /.box-body -->
		</div>


		<!-- ------------------------- Close TimeSheet ---------------------------- -->


		<!-- ------------------------- Reviews ---------------------------- -->

		<!-- ------------------------- Close Reviews ---------------------------- -->


		<!-- ------------------------- Billing ---------------------------- -->

		<div
			style="width: 100%; text-align: center; font-size: 14px; font-weight: bold; margin: 7px;"
			class="">Billing</div>
		<div class="box box-primary collapsed-box"
			style="border-top-color: #EEEEEE; margin-top: 10px;">

			<div class="box-header with-border">
				<h3 class="box-title" style="font-size: 14px; padding-right: 10px;">
					Milestone Billing Notification
					<%-- <%=uF.showData(hmNotificationStatus.get("NOT_"+IConstants.N_MILESTONE_BILLING), "") %> --%>
					<%
						strNotId = "" + IConstants.N_MILESTONE_BILLING;
									isMail = (Boolean) request.getAttribute("isEmailMilestoneBilling");
									strEImage = isMail ? "mail_enbl.png" : "mail_disbl.png";
									strEEnable = isMail ? "Disable Mail Notification" : "Enable Mail Notification";

									if (isMail) {
										isMail = false;
									} else {
										isMail = true;
									}
					%>
					<span id="myDivE<%=strNotId%>" style="float: right"> <img
						width="20px" src="images1/<%=strEImage%>"
						title="<%=strEEnable%>"
						onclick="updateEmailStatus('myDivE<%=strNotId%>',<%=strNotId%>,<%=isMail%>,'isEmailMilestoneBilling');" />
					</span>
				</h3>
				<div class="box-tools pull-right">
					<button class="btn btn-box-tool" data-widget="collapse">
						<i class="fa fa-plus"></i>
					</button>
					<button class="btn btn-box-tool" data-widget="remove">
						<i class="fa fa-times"></i>
					</button>
				</div>
			</div>
			<!-- /.box-header -->
			<div class="box-body"
				style="padding: 5px; overflow-y: auto; display: none;">
				<div class="content1" style="margin: 0px 0px 10px 0px">
					<table class="table table_no_border" cellspacing="2"
						cellpadding="2" width="100%">
						<tr>
							<td valign="top" class="txtlabel alignRight">Email
								Notification:</td>
							<td><s:checkbox name="isEmailMilestoneBilling"
									id="isEmailMilestoneBilling" /></td>
						</tr>
						<%-- <tr>
							<td valign="top" class="txtlabel alignRight">Enable/Disable Text Notification:</td>
							<td><s:checkbox name="isTextMilestoneBilling"/></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Text Message</td>
							<td><s:textfield cssStyle="width:480px" name="strTextMilestoneBilling"/></td>
						</tr> --%>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Subject:</td>
							<td><s:textfield cssStyle="width:480px"
									name="strSubjectMilestoneBilling" /></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Body:</td>
							<td><div style="float: left; margin-right: 20px;">
									<s:textarea name="strBodyMilestoneBilling" cols="63" rows="10" />
								</div>
								<div style="float: left;">
									[EMPFNAME] <br /> [EMPLNAME] <br /> [FROM_DATE] <br />
									[TO_DATE] <br /> [DONE_BY] <br /> [LEGAL_ENTITY_NAME] <br />
								</div></td>
						</tr>
					</table>
				</div>
			</div>
			<!-- /.box-body -->
		</div>

		<div class="box box-primary collapsed-box"
			style="border-top-color: #EEEEEE; margin-top: 10px;">

			<div class="box-header with-border">
				<h3 class="box-title" style="font-size: 14px; padding-right: 10px;">
					Recurring Billing Notification
					<%-- <%=uF.showData(hmNotificationStatus.get("NOT_"+IConstants.N_RECURRING_BILLING), "") %> --%>
					<%
						strNotId = "" + IConstants.N_RECURRING_BILLING;
									isMail = (Boolean) request.getAttribute("isEmailRecurringBilling");
									strEImage = isMail ? "mail_enbl.png" : "mail_disbl.png";
									strEEnable = isMail ? "Disable Mail Notification" : "Enable Mail Notification";

									if (isMail) {
										isMail = false;
									} else {
										isMail = true;
									}
					%>
					<span id="myDivE<%=strNotId%>" style="float: right"> <img
						width="20px" src="images1/<%=strEImage%>"
						title="<%=strEEnable%>"
						onclick="updateEmailStatus('myDivE<%=strNotId%>',<%=strNotId%>,<%=isMail%>,'isEmailRecurringBilling');" />
					</span>
				</h3>
				<div class="box-tools pull-right">
					<button class="btn btn-box-tool" data-widget="collapse">
						<i class="fa fa-plus"></i>
					</button>
					<button class="btn btn-box-tool" data-widget="remove">
						<i class="fa fa-times"></i>
					</button>
				</div>
			</div>
			<!-- /.box-header -->
			<div class="box-body"
				style="padding: 5px; overflow-y: auto; display: none;">
				<div class="content1" style="margin: 0px 0px 10px 0px">
					<table class="table table_no_border" cellspacing="2"
						cellpadding="2" width="100%">
						<tr>
							<td valign="top" class="txtlabel alignRight">Email
								Notification:</td>
							<td><s:checkbox name="isEmailRecurringBilling"
									id="isEmailRecurringBilling" /></td>
						</tr>
						<%-- <tr>
							<td valign="top" class="txtlabel alignRight">Enable/Disable Text Notification:</td>
							<td><s:checkbox name="isTextRecurringBilling"/></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Text Message</td>
							<td><s:textfield cssStyle="width:480px" name="strTextRecurringBilling"/></td>
						</tr> --%>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Subject:</td>
							<td><s:textfield cssStyle="width:480px"
									name="strSubjectRecurringBilling" /></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Body:</td>
							<td><div style="float: left; margin-right: 20px;">
									<s:textarea name="strBodyRecurringBilling" cols="63" rows="10" />
								</div>
								<div style="float: left;">
									[EMPFNAME] <br /> [EMPLNAME] <br /> [FROM_DATE] <br />
									[TO_DATE] <br /> [DONE_BY] <br /> [LEGAL_ENTITY_NAME] <br />
								</div></td>
						</tr>
					</table>
				</div>
			</div>
			<!-- /.box-body -->
		</div>



		<!-- ------------------------- Close Billing ---------------------------- -->


		<!-- ------------------------- Payments ---------------------------- -->

		<div
			style="width: 100%; text-align: center; font-size: 14px; font-weight: bold; margin: 7px;"
			class="">Payments</div>

		<div class="box box-primary collapsed-box"
			style="border-top-color: #EEEEEE; margin-top: 10px;">

			<div class="box-header with-border">
				<h3 class="box-title" style="font-size: 14px; padding-right: 10px;">
					Payment Alert Notification
					<%-- <%=uF.showData(hmNotificationStatus.get("NOT_"+IConstants.N_PAYMENT_ALERT), "") %> --%>
					<%
						strNotId = "" + IConstants.N_PAYMENT_ALERT;
									isMail = (Boolean) request.getAttribute("isEmailPaymentAlert");
									strEImage = isMail ? "mail_enbl.png" : "mail_disbl.png";
									strEEnable = isMail ? "Disable Mail Notification" : "Enable Mail Notification";

									if (isMail) {
										isMail = false;
									} else {
										isMail = true;
									}
					%>
					<span id="myDivE<%=strNotId%>" style="float: right"> <img
						width="20px" src="images1/<%=strEImage%>"
						title="<%=strEEnable%>"
						onclick="updateEmailStatus('myDivE<%=strNotId%>',<%=strNotId%>,<%=isMail%>,'isEmailPaymentAlert');" />
					</span>
				</h3>
				<div class="box-tools pull-right">
					<button class="btn btn-box-tool" data-widget="collapse">
						<i class="fa fa-plus"></i>
					</button>
					<button class="btn btn-box-tool" data-widget="remove">
						<i class="fa fa-times"></i>
					</button>
				</div>
			</div>
			<!-- /.box-header -->
			<div class="box-body"
				style="padding: 5px; overflow-y: auto; display: none;">
				<div class="content1" style="margin: 0px 0px 10px 0px">
					<table class="table table_no_border" cellspacing="2"
						cellpadding="2" width="100%">
						<tr>
							<td valign="top" class="txtlabel alignRight">Email
								Notification:</td>
							<td><s:checkbox name="isEmailPaymentAlert"
									id="isEmailPaymentAlert" /></td>
						</tr>
						<%-- <tr>
							<td valign="top" class="txtlabel alignRight">Enable/Disable Text Notification:</td>
							<td><s:checkbox name="isTextPaymentAlert"/></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Text Message</td>
							<td><s:textfield cssStyle="width:480px" name="strTextPaymentAlert"/></td>
						</tr> --%>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Subject:</td>
							<td><s:textfield cssStyle="width:480px"
									name="strSubjectPaymentAlert" /></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Body:</td>
							<td><div style="float: left; margin-right: 20px;">
									<s:textarea name="strBodyPaymentAlert" cols="63" rows="10" />
								</div>
								<div style="float: left;">
									[CUST_FNAME] <br /> [CUST_LNAME] <br /> [INVOICE_NO] <br />
									[PROJECT_FREQUENCY_NAME] <br /> [FROM_DATE] <br /> [TO_DATE] <br />
									[DONE_BY] <br /> [LEGAL_ENTITY_NAME] <br />
								</div></td>
						</tr>
					</table>
				</div>
			</div>
			<!-- /.box-body -->
		</div>

		<div class="box box-primary collapsed-box"
			style="border-top-color: #EEEEEE; margin-top: 10px;">

			<div class="box-header with-border">
				<h3 class="box-title" style="font-size: 14px; padding-right: 10px;">
					Payment Reminder Notification
					<%-- <%=uF.showData(hmNotificationStatus.get("NOT_"+IConstants.N_PAYMENT_REMINDER), "") %> --%>
					<%
						strNotId = "" + IConstants.N_PAYMENT_REMINDER;
									isMail = (Boolean) request.getAttribute("isEmailPaymentReminder");
									strEImage = isMail ? "mail_enbl.png" : "mail_disbl.png";
									strEEnable = isMail ? "Disable Mail Notification" : "Enable Mail Notification";

									if (isMail) {
										isMail = false;
									} else {
										isMail = true;
									}
					%>
					<span id="myDivE<%=strNotId%>" style="float: right"> <img
						width="20px" src="images1/<%=strEImage%>"
						title="<%=strEEnable%>"
						onclick="updateEmailStatus('myDivE<%=strNotId%>',<%=strNotId%>,<%=isMail%>,'isEmailPaymentReminder');" />
					</span>
				</h3>
				<div class="box-tools pull-right">
					<button class="btn btn-box-tool" data-widget="collapse">
						<i class="fa fa-plus"></i>
					</button>
					<button class="btn btn-box-tool" data-widget="remove">
						<i class="fa fa-times"></i>
					</button>
				</div>
			</div>
			<!-- /.box-header -->
			<div class="box-body"
				style="padding: 5px; overflow-y: auto; display: none;">
				<div class="content1" style="margin: 0px 0px 10px 0px">
					<table class="table table_no_border" cellspacing="2"
						cellpadding="2" width="100%">
						<tr>
							<td valign="top" class="txtlabel alignRight">Email
								Notification:</td>
							<td><s:checkbox name="isEmailPaymentReminder"
									id="isEmailPaymentReminder" /></td>
						</tr>
						<%-- <tr>
							<td valign="top" class="txtlabel alignRight">Enable/Disable Text Notification:</td>
							<td><s:checkbox name="isTextPaymentReminder"/></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Text Message</td>
							<td><s:textfield cssStyle="width:480px" name="strTextPaymentReminder"/></td>
						</tr> --%>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Subject:</td>
							<td><s:textfield cssStyle="width:480px"
									name="strSubjectPaymentReminder" /></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Body:</td>
							<td><div style="float: left; margin-right: 20px;">
									<s:textarea name="strBodyPaymentReminder" cols="63" rows="10" />
								</div>
								<div style="float: left;">
									[EMPFNAME] <br /> [EMPLNAME] <br /> [FROM_DATE] <br />
									[TO_DATE] <br /> [DONE_BY] <br /> [LEGAL_ENTITY_NAME] <br />
								</div></td>
						</tr>
					</table>
				</div>
			</div>
			<!-- /.box-body -->
		</div>




		<!-- ------------------------- Close Payments ---------------------------- -->



		<!-- ------------------------- Close Project Management ---------------------------- -->
		<%
			}
		%>


		<%
			}
		%>

		<%-- 	
			<p class="past heading noti_title">Employee Requisition Approval</p>
			 <div class="content1" style="margin:0px 0px 10px 0px">
					<table class="table table_no_border" cellspacing="2" cellpadding="2" width="100%">
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Notification:</td>
							<td><s:checkbox name="isEmailNewRequisitionApproval"/><span class="hint">Please confirm the password by entering it once again.<span class="hint-pointer">&nbsp;</span></span></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Enable/Disable Text Notification:</td>
							<td><s:checkbox name="isTextNewRequisitionApproval"/><span class="hint">Please confirm the password by entering it once again.<span class="hint-pointer">&nbsp;</span></span></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Text Message</td>
							<td><s:textfield cssStyle="width:480px" name="strTextNewRequisitionApproval"/><span class="hint">Please confirm the password by entering it once again.<span class="hint-pointer">&nbsp;</span></span></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Subject:</td>
							<td><s:textfield cssStyle="width:480px" name="strSubjectNewRequisitionApproval"/><span class="hint">Please confirm the password by entering it once again.<span class="hint-pointer">&nbsp;</span></span></td>
						</tr>
						<tr>
							<td valign="top" class="txtlabel alignRight">Email Body:</td>
							<td><s:textarea name="strBodyNewRequisitionApproval" cols="63" rows="10" /><span class="hint">Please confirm the password by entering it once again.<span class="hint-pointer">&nbsp;</span></span>
							<div style="float: right; width: 40%;">  
									[LOGIN_LINK] <br/> 	
									[EMPCODE] <br/>
									[EMPFNAME] <br/>
									[EMPLNAME] <br/>
									[USERNAME] <br/>
									[PASSWORD] <br/>
								</div>
								 
							</td>
						</tr>
						
					</table>
				</div>
				 --%>
		<div style="width: 100%; text-align: center">
			<input type="submit" class="btn btn-primary"
				value="Save Notifications" />
		</div>
	</s:form>

</div>

<g:compress>
	<script>
			// Replace the <textarea id="editor1"> with an CKEditor instance.
			if(document.getElementById("editor1")) {
				CKEDITOR.replace( 'editor1', {
					
					on: {
						focus: onFocus,
						blur: onBlur,
	
						// Check for availability of corresponding plugins.
						pluginsLoaded: function( evt ) {
							var doc = CKEDITOR.document, ed = evt.editor;
							if ( !ed.getCommand( 'bold' ) )
								doc.getById( 'exec-bold' ).hide();
							if ( !ed.getCommand( 'link' ) )
								doc.getById( 'exec-link' ).hide();
						}
				
					}
				});
			}
			if(document.getElementById("editor2")) {
				CKEDITOR.replace( 'editor2', {
					on: {
						focus: onFocus,
						blur: onBlur,
	
						// Check for availability of corresponding plugins.
						pluginsLoaded: function( evt ) {
							var doc = CKEDITOR.document, ed = evt.editor;
							if ( !ed.getCommand( 'bold' ) )
								doc.getById( 'exec-bold' ).hide();
							if ( !ed.getCommand( 'link' ) )
								doc.getById( 'exec-link' ).hide();
						}
					}
				});
			}
			if(document.getElementById("editor3")) {
				CKEDITOR.replace( 'editor3', {
					on: {
						focus: onFocus,
						blur: onBlur,
	
						// Check for availability of corresponding plugins.
						pluginsLoaded: function( evt ) {
							var doc = CKEDITOR.document, ed = evt.editor;
							if ( !ed.getCommand( 'bold' ) )
								doc.getById( 'exec-bold' ).hide();
							if ( !ed.getCommand( 'link' ) )
								doc.getById( 'exec-link' ).hide();
						}
					}
				});
			}
			
			if(document.getElementById("editor4")) {
				CKEDITOR.replace( 'editor4', {
					on: {
						focus: onFocus,
						blur: onBlur,
	
						// Check for availability of corresponding plugins.
						pluginsLoaded: function( evt ) {
							var doc = CKEDITOR.document, ed = evt.editor;
							if ( !ed.getCommand( 'bold' ) )
								doc.getById( 'exec-bold' ).hide();
							if ( !ed.getCommand( 'link' ) )
								doc.getById( 'exec-link' ).hide();
						}
					}
				});
			}
			
			
</script>
</g:compress>