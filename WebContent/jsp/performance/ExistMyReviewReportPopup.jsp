<%@page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>


<script>

// The instanceReady event is fired, when an instance of CKEditor has finished
// its initialization.  
CKEDITOR.on( 'instanceReady', function( ev ) {
	// Show the editor name and description in the browser status bar.
	document.getElementById( 'eMessage' ).innerHTML = 'Instance <code>' + ev.editor.name + '<\/code> loaded.';

	// Show this sample buttons.
	document.getElementById( 'eButtons' ).style.display = 'block';
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
	document.getElementById( 'eMessage' ).innerHTML = '<b>' + this.name + ' is focused </b>';
}

function onBlur() {
	document.getElementById( 'eMessage' ).innerHTML = this.name + ' lost focus';
}

$(function() {
	$('#lt').DataTable();
}); 		
			
</script>

<%-- <jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="Exist Appraisal Systems" name="title"/>
</jsp:include> --%>
<% String strSessionUserType=(String) session.getAttribute(IConstants.USERTYPE);%>

    <div id="printDiv" class="leftbox reportWidth">  
    <s:property value="message"/>    
   
    	<table class="table table-striped table-bordered" id="lt">
    	<% UtilityFunctions uF=new UtilityFunctions(); %>
				<thead>
					<tr> 
						<th style="text-align: left;">Review Name</th>
						<th style="text-align: left;">Review Type</th>
						<th style="text-align: left;">Orientation Type</th>
						<th style="text-align: left;">Frequency Type</th>
						<th style="text-align: left;">Added By</th>
						<th style="text-align: left;">Entry Date</th>
						<th style="text-align: left;">Choose</th>
						
					</tr>
				</thead>
				
				<tbody>
				<% java.util.List<List<String>> couterlist = (java.util.List<List<String>>)request.getAttribute("outerList"); 
				
				for(int i=0;couterlist!=null && i<couterlist.size();i++){
					List<String> innerList=couterlist.get(i);%>
					
					<tr id = "<%= innerList.get(0) %>">
						<td><%= innerList.get(1) %></td>
						<td><%= innerList.get(3) %></td>
						<td><%= innerList.get(2) %>&deg;</td>
						<td><%= innerList.get(4) %></td>
						<%-- <td><%= innerList.get(5) %></td>
						<td><%= innerList.get(6) %></td>	
						<td><%= innerList.get(7) %></td> --%>		
						<td><%= innerList.get(8) %></td>
						<td><%= innerList.get(9) %></td>		
						<td>
						
							<%-- <a href="javascript:void(0);" onclick="if(confirm('Are you sure, You want to create Appraisal from this template ?')) window.location='CreateAppraisalFromTemplate.action?existID=<%=innerList.get(0) %>';">Choose</a> --%>
							<a href="javascript:void(0);" onclick="openEditAppraisal('<%=innerList.get(0) %>','editexistapp','<%=innerList.get(12)%>');">Choose</a>
						</td>
							
					</tr>
				<%}	%>
				
				</tbody>
			</table> 
    </div>
