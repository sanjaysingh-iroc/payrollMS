
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="java.util.List"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<%@ taglib uri="http://granule.com/tags" prefix="g" %>
<%-- <script type="text/javascript" src="scripts/charts/jquery.min.js"></script> --%>
<script src="scripts/ckeditor/ckeditor.js"></script> 

<%String pageFrom = (String)request.getAttribute("pageFrom"); %>
<g:compress>
<script>
/* $(document).ready(function(){
	$("#submitButton").click(function(){
		$(".validateRequired").prop('required',true); 
	});
}) ; */
//jQuery(document).ready(function(){
	 $("input[type='submit']").click(function(){
		$("#addManualFrm").find('.validateRequired').filter(':hidden').prop('required',false);
		$("#addManualFrm").find('.validateRequired').filter(':visible').prop('required',true);
		for ( instance in CKEDITOR.instances ) {
			CKEDITOR.instances[instance].updateElement();
		} 
    });
	
	
//}); 
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

function showData(type) {
	//alert("type==>"+type);
	if(type == 'U') {
		if(document.getElementById("manualDocStatus")) {
			document.getElementById("manualDocStatus").value = "1";
		}
		document.getElementById("paste_manualLink").style.display = "table-row";
		document.getElementById("paste_manual").style.display = "none";
		document.getElementById("upload_manualLink").style.display = "none";
		
		document.getElementById("upload_manual").style.display = "table-row";
		document.getElementById("upload_note").style.display = "table-row";
		document.getElementById("strPublish").value = "Upload & Publish";
		
	} else if(type == 'P') {
		if(document.getElementById("manualDocStatus")) {
			document.getElementById("manualDocStatus").value = "0";
		}
		document.getElementById("upload_manualLink").style.display = "table-row";
		document.getElementById("upload_manual").style.display = "none";
		document.getElementById("upload_note").style.display = "none";
		document.getElementById("paste_manual").style.display = "table-row";
		document.getElementById("paste_manualLink").style.display = "none";
		document.getElementById("strPublish").value = "Publish";
	}
}

<%-- <%if(pageFrom==null || pageFrom.equals("") || pageFrom.equalsIgnoreCase("null")) { %>
	var submitActor = null;
	var submitButtons = $('form').find('input[type=submit]').filter(':visible');
	$("form").bind('submit',function(event) {
			  event.preventDefault();
			  if (null === submitActor) {
	           // If no actor is explicitly clicked, the browser will
	           // automatically choose the first in source-order
	           // so we do the same here
	           submitActor = submitButtons[0];
	       }
			  
		  	for (instance in CKEDITOR.instances) {
                CKEDITOR.instances[instance].updateElement();
            }
			  var form_data = $("#addManualFrm").serialize();
			  form_data.append("strCompanyManual", $("#doc").attr('path'));
		   	  var strPublish=$('input[name = strPublish ]').val();
		   	  var strSaveDraft=$('input[name = strSaveDraft ]').val();
		   	  var strSubmit=$('input[name = strSubmit ]').val();
		   	  var strPriview=$('input[name = strPriview ]').val();
		   	
		   	  var submit = submitActor.name;
		      console.log("form_data==>"+form_data);
	         
		      if(strPublish != "" && strPublish === "Upload & Publish") {
	        	  strPublish = "Upload And Publish";
	          }
	      
		      if(submit != null && submit == "strPublish") {
	     	  	form_data = form_data +"&strPublish="+strPublish;
		      } else if(submit != null && submit == "strSaveDraft"){
		     	  form_data = form_data +"&strSaveDraft="+strSaveDraft;
		      } else if(submit != null && submit == "strSubmit"){
		     	  form_data = form_data +"&strSubmit="+strSubmit;
		      }else if(submit != null && submit == "strPriview"){
		     	  form_data = form_data +"&strPriview="+strPriview;
		      }
	       
	      // console.log("form_data==>"+form_data);
	       $("#actionResult").html('<div id ="the_div"><div id="ajaxLoadImage"></div></div>');
		   	
		      $.ajax({
		     		url: "AddCompanyManual.action",
		     		type: 'POST',
		     		data: form_data,
		     	    success: function(result){
		     			$("#actionResult").html(result);
		     			
		     	    }
		       });
		     
	});
	submitButtons.click(function(event) {
	    submitActor = this;
	});
<% }%> --%>
</script>


</g:compress>


<%
	
	String orgId = (String)request.getAttribute("strOrg");
	String str_manualId = (String)request.getParameter("manualId");
	String strOrgIds = (String)request.getAttribute("f_org");
	String userscreen = (String)request.getAttribute("userscreen");
	String navigationId = (String)request.getAttribute("navigationId");
	String toPage = (String)request.getAttribute("toPage");
	
	String strEdit = (String)request.getParameter("E");
	String strDoc = (String)request.getAttribute("strDoc");
	String manualBody = (String)request.getAttribute("manualBody");
  
%>

 <%if(pageFrom==null || pageFrom.trim().equals("") || !pageFrom.trim().equalsIgnoreCase("MyHub")) { %>
	 <div class="col-md-12">
<% } %>
		<div style = "width:100%;float:left;">
			<s:form id="addManualFrm" theme="simple" action="AddCompanyManual" method="post" enctype="multipart/form-data">
				<s:hidden name="pageFrom" id="pageFrom"></s:hidden> 
				<s:hidden name="orgId" id="orgId"></s:hidden>
				<s:hidden name="userscreen" />
				<s:hidden name="navigationId" />
				<s:hidden name="toPage" />
				
				<table class="table table_no_border">
				    <tr>
						<td>Organisation:<sup>*</sup></td>
						<td><s:select list="orgList" name="strOrg" listKey="orgId" listValue="orgName" /></td>
					</tr>
			
					<tr>
						<td>Title:<sup>*</sup></td>
						<td><s:hidden id = "strManualId" name="strManualId" /><s:textfield cssClass="validateRequired" cssStyle="width:80%" name="strTitle" /></td>
					</tr>
			
					<%if((strEdit != null && !strEdit.equals("")) || (str_manualId != null && !str_manualId.equals(""))) { %>
						<input type="hidden" name="manualDocStatus" id ="manualDocStatus" value="<%=(String)request.getAttribute("manualDocStatus") %>" />
						<% if(strDoc != null && !strDoc.equals("")) { %>
							<tr id = "paste_manualLink">
								<td></td>
								<td><a href="#" onclick="showData('P');event.preventDefault();" >Copy & Paste Manual</a><td>
							</tr>
						
							<tr id="upload_manual">
								<td valign="top">Upload Manual:<sup>*</sup></td>
								
								<td>
									<span style="margin:-10px 10px 0px 0px;"><%=strDoc%></span>
									 
									<s:file id="strCompanyManual" name="strCompanyManual"  size="5"  cssStyle="font-size: 12px; height: 27px; vertical-align: top;"/>
								</td>
								
							</tr>
						
							<tr id="upload_note">
							  <td></td>
							  <td style="font-size:10px;font-style:italic;">Upload doc,pdf,txt files only.</td>
							</tr>
						
							<tr id = "upload_manualLink" style="display:none;">
								<td></td>
								<td><a href="#" onclick="showData('U');event.preventDefault();" >Upload Manual</a><td>
							</tr>
						
							<tr id="paste_manual"  style="display:none;" >
								<td valign="top">Body:<sup>*</sup></td>
								<td><s:textarea cssClass="validateRequired" cssStyle="width:100%" name="strBody" rows="20" cols="100" id="editor1"/></td>
							</tr> 
						<% } else if(manualBody != null && !manualBody.equals("")) { %>
					 		<tr id = "upload_manualLink">
								<td></td>
								<td><a href="#" onclick="showData('U');event.preventDefault();" >Upload Manual</a><td>
							</tr>
							
							<tr id="paste_manual">
								<td valign="top">Body:<sup>*</sup></td>
								<td><s:textarea cssClass="validateRequired" cssStyle="width:100%" name="strBody" rows="20" cols="100" id="editor1"/></td>
							</tr> 
							
							<tr id = "paste_manualLink" style="display:none;">
								<td></td>
								<td><a href="#" onclick="showData('P');event.preventDefault();" >Copy & Paste Manual</a><td>
							</tr>
							
							<tr id="upload_manual" style="display:none;">
								<td valign="top">Upload Manual:<sup>*</sup></td>
								<td>
									<s:file id="strCompanyManual" name="strCompanyManual"  cssClass="validateRequired" size="5" cssStyle="font-size: 12px; height: 27px;margin-top:10px; vertical-align: top;"/>
								</td>
								
							</tr>
							<tr id="upload_note" style="display:none;">
							  <td></td>
							  <td style="font-size:10px;font-style:italic;">Upload doc,pdf,txt files only.</td>
							</tr>
					 <% } else { %>	
					 		<tr id = "upload_manualLink">
								<td></td>
								<td><a href="#" onclick="showData('U');event.preventDefault();" >Upload Manual</a><td>
							</tr>
							<tr id = "paste_manualLink" style ="display:none;">
								<td></td>
								<td><a href="#" onclick="showData('P');event.preventDefault();" >Copy & Paste Manual</a><td>
							</tr>
						
							 <tr id="paste_manual">
								<td valign="top">Body:<sup>*</sup></td>
								<td><s:textarea cssClass="validateRequired" cssStyle="width:100%" name="strBody" rows="20" cols="100" id="editor1"/></td>
							</tr> 
							<tr id="upload_manual" style="display:none;">
								<td valign="top">Upload Manual:<sup>*</sup></td>
								<td>
									<s:file id="strCompanyManual" name="strCompanyManual"  cssClass="validateRequired" size="5" cssStyle="font-size: 12px; height: 27px;margin-top:10px; vertical-align: top;"/>
								</td>
							</tr>
							
							<tr id="upload_note" style="display:none;">
							  <td></td>
							  <td style="font-size:10px;font-style:italic;">Upload doc,pdf,txt files only.</td>
							</tr>
				    <% } %>  
				<% } else { %>
						<tr id = "upload_manualLink">
							<td></td>
							<td><a href="#" onclick="showData('U');event.preventDefault();" >Upload Manual</a><td>
						</tr>
						<tr id = "paste_manualLink" style ="display:none;">
							<td></td>
							<td><a href="#" onclick="showData('P');event.preventDefault();" >Copy & Paste Manual</a><td>
						</tr>
						
						 <tr id="paste_manual">
							<td valign="top">Body:<sup>*</sup></td>
							<td><s:textarea cssClass="validateRequired" cssStyle="width:100%" name="strBody" rows="20" cols="100" id="editor1"/></td>
						</tr> 
						<tr id="upload_manual" style="display:none;">
							<td valign="top">Upload Manual:<sup>*</sup></td>
							<td>
								<s:file id="strCompanyManual" name="strCompanyManual"  cssClass="validateRequired" size="5" cssStyle="font-size: 12px; height: 27px;margin-top:10px; vertical-align: top;"/>
							</td>
						</tr>
						<tr id="upload_note" style="display:none;">
						  <td></td>
						  <td style="font-size:10px;font-style:italic;">Upload doc,pdf,txt files only.</td>
						</tr>
				<% } %>
				<tr>
					<td></td>
					<td colspan="2">
					<%if((strEdit != null && !strEdit.equals("")) || (str_manualId != null && !str_manualId.equals(""))) {
							if(strDoc != null && !strDoc.equals("")) {				
							%>
								<s:submit name="strPublish" id="strPublish" cssClass="btn btn-primary"  value="Upload & Publish" />
					 		<% } else if(manualBody != null && !manualBody.equals("")) { %>
					 			 <s:submit name="strPublish" id="strPublish" cssClass="btn btn-primary"  value="Publish" />
					 		<% } %>
					 <% } else { %>
							<s:submit name="strPublish" id="strPublish" cssClass="btn btn-primary"  value="Publish" />
					<% } %>
					<s:submit name="strSaveDraft" cssClass="btn btn-primary"  value="Save as Draft" />
					<% if(pageFrom==null || !pageFrom.trim().equalsIgnoreCase("MyHub")) {%>
						<%-- <a href="CompanyManual.action?strOrg=<%=(String)request.getAttribute("orgId") %>">
							<input type="button" class="btn btn-danger" value="Cancel"> 
					    </a> --%>
					<%}else{ %>
						<a href="Hub.action?type=M"><input type="button" class="btn btn-danger" value="Cancel"> </a>
					<%} %>
					</td>
				</tr>
			  </table>				
			</s:form>
		</div>
		<%if(pageFrom==null || !pageFrom.trim().equalsIgnoreCase("MyHub")) { %>
   </div>
<% } %>
 
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
			
			</script>
</g:compress>