<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib uri="http://granule.com/tags" prefix="g" %>
<%@page import="java.util.*,com.konnect.jpms.util.*"%>

<script src="scripts/ckeditor_small/ckeditor.js"></script>


<g:compress>

	<link href="scripts/ckeditor_small/samples/sample.css" rel="stylesheet">
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
	if ( editor.mode == 'wysiwyg' ) {
		// Insert HTML code.
		// http://docs.ckeditor.com/#!/api/CKEDITOR.editor-method-insertHtml
		editor.insertHtml( value );
	} else
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

	</script>
	
<script type="text/javascript">

CKEDITOR.config.width='200px';
	
	jQuery(document).ready(function() {
		// binds form submission and fields to the validation engine
		jQuery("#frmInvoiceFormat").validationEngine();
	});

	//addLoadEvent(prepareInputsForHints);
	function deleteInvoiceFormat() {
		
		if(confirm("Are you sure, you want to delete this invoice format?")) {
			//alert("invoiceFormatId ==>> " + document.getElementById("invoiceFormatId").value);
			var invoiceFormatId = document.getElementById("invoiceFormatId").value;
			/* document.getElementById("operation").value = 'D';
			document.getElementById("frmInvoiceFormat").submit(); */
			//document.frmInvoiceFormat.submit();
			window.location="InvoiceFormat.action?operation=D&invoiceFormatId=" +invoiceFormatId;
		}
	}
	
	/* function updateInvoiceFormat() {
		document.getElementById("operation").value = 'U';
		document.getElementById("frmInvoiceFormat").submit();
	} */
	
</script>

</g:compress>
<script type="text/javascript">
function previewInvoice(){
	var invoiceFormatId = document.getElementById("invoiceFormatId").value;
	var url='InvoiceFormat.action?type=pdf&invoiceFormat='+invoiceFormatId;
	
	window.location = url;
}
</script>

<jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="Invoice Setting" name="title"/>
</jsp:include>

<div class="leftbox reportWidth">

	<% 
		UtilityFunctions uF = new UtilityFunctions();
		String invoiceFormatId = (String)request.getAttribute("invoiceFormatId");
	
	%>
	<s:form name="frmInvoiceFormat_1" id="frmInvoiceFormat_1" action="InvoiceFormat" cssClass="formcss" theme="simple">

			<div style="float: left; width: 100%; margin-top: 10px; padding-bottom: 7px; margin-bottom: 5px; border-bottom: 1px solid #CCCCCC;">
				Select Template: 
				<s:select theme="simple" name="invoiceFormat" id="invoiceFormat" listKey="invoiceFormatId" listValue="invoiceFormatName" headerKey="" 
					headerValue="Select Invoice Template" onchange="document.frmInvoiceFormat_1.submit();" list="invoiceFormatList" key=""/>
			</div>
	</s:form>
			
	<s:form name="frmInvoiceFormat" id="frmInvoiceFormat" action="InvoiceFormat" method="post" cssClass="formcss" theme="simple">

		<s:hidden name="type"/>
		<s:hidden name="fromPage"/>
		<s:hidden name="operation" id="operation"/>
		<s:hidden name="invoiceFormatId" id="invoiceFormatId"/>
		
			<div style="float: left; margin-right: 10px;" id="tblDiv">
				<table class="formcss" style="border: 1px solid #CCCCCC;">
					<tr>
						<td colspan="3">Invoice Template Name: <s:textfield name="invoiceFormatTitle" id="invoiceFormatTitle" cssStyle="width: 220px; margin-bottom: 7px;"> </s:textfield>
							<%if(uF.parseToInt(invoiceFormatId) > 0) { %>
							&nbsp;&nbsp;<a onclick="previewInvoice();" href="javascript:void(0)">Preview</a>
							<%} %>
						</td>
					</tr>
					<tr>
						<td><s:textarea name="section1" id="editor1" rows="3" cols="20"></s:textarea> </td>
						<td><s:textarea name="section2" id="editor2" rows="3" cols="20"></s:textarea> </td>
						<td align="right"><s:textarea name="section3" id="editor3" rows="3" cols="20"></s:textarea> </td>
					</tr>

					<tr>
						<td><s:textarea name="section4" id="editor4" rows="3" cols="20"></s:textarea> </td>
						<td><s:textarea name="section5" id="editor5" rows="3" cols="20"></s:textarea> </td>
						<td align="right"><s:textarea name="section6" id="editor6" rows="3" cols="20"></s:textarea> </td>
					</tr>
					
					<tr>
						<td colspan="3" style="float: left; height: 250px;">&nbsp;</td>
					</tr>

					<tr>
						<td colspan="3"><div style="float: left;"> <s:textarea name="section7" id="editor7" rows="3" cols="30"></s:textarea></div> <!-- </td>
						<td>&nbsp;</td>
						<td > --><div style="float: right;"><s:textarea name="section8" id="editor8" rows="3" cols="30"></s:textarea></div> </td>
					</tr>

					<tr>
						<td colspan="3"><div style="float: left;"><s:textarea name="section9" id="editor9" rows="3" cols="30"></s:textarea></div> <!-- </td>
						<td>&nbsp;</td>
						<td > --> <div style="float: right;"><s:textarea name="section10" id="editor10" rows="3" cols="30"></s:textarea></div> </td>
					</tr>
					
					<tr>
						<td colspan="3" align="center"><s:textarea name="section11" id="editor11" rows="3" cols="50"></s:textarea> </td>
					</tr>
					
					
					<tr>
					<td colspan="3" align="center">
					<%if(uF.parseToInt(invoiceFormatId) > 0) { %>
					<%-- <s:hidden name="operation" id="operation" value="U"/> --%>
						<s:submit value="Update" cssClass="input_button" name="submit"></s:submit>
						<!-- <input type="button" value="Update" class="input_button" onclick="updateInvoiceFormat();"> -->
						<input type="button" value="Delete" class="cancel_button" onclick="deleteInvoiceFormat();">
					<% } else { %>
						<s:submit value="Save" cssClass="input_button" name="submit"></s:submit>
					<% } %>	
					</td>
					<td></td>
				</tr>
			</table>
			</div>
		
		<!-- <div class="clr"></div> -->
		
			<div style="float: left; width: 35%; font-size: 10px;">
				Organization Logo [ORG_LOGO]
				<br/>
				Legal Entity Logo [LEGAL_ENTITY_LOGO]
				<br/>
				Organization Name [ORG_NAME]
				<br/>
				Organization Name [ORG_SUB_TITLE]
				<br/>
				Organization Address [ORG_ADDRESS]
				<br/>
				Organization Contact No. [ORG_CONTACT_NO]
				<br/>
				Organization Fax No. [ORG_FAX_NO]
				<br/>
				Organization e-mail ID [ORG_EMAIL_ID]
				<br/>
				Legal Entity Name [LEGAL_ENTITY_NAME]
				<br/>
				Legal Entity Name [LEGAL_ENTITY_SUB_TITLE]
				<br/>
				Legal Entity Address [LEGAL_ENTITY_ADDRESS]
				<br/>
				Legal Entity Contact No. [LEGAL_ENTITY_CONTACT_NO]
				<br/>
				Legal Entity Fax No. [LEGAL_ENTITY_FAX_NO]
				<br/>
				Legal Entity e-mail ID [LEGAL_ENTITY_EMAIL_ID]
				<br/>
				Legal Entity Registration No. [LEGAL_ENTITY_REG_NO]
				<br/>
				Legal Entity PAN No. [LEGAL_ENTITY_PAN_NO]
				<br/>
				Legal Entity TAN No. [LEGAL_ENTITY_TAN_NO]
				<!-- <br/>
				Legal Entity ECC No. [LEGAL_ENTITY_ECC_NO] -->
				<br/>
				Work Location [WORK_LOCATION]
				<br/>
				Work Location Address [WORK_LOCATION_ADDRESS]
				<br/>
				Work Location Contact No. [WORK_LOCATION_CONTACT_NO]
				<br/>
				Work Location Fax No. [WORK_LOCATION_FAX_NO]
				<br/>
				Work Location e-mail ID [WORK_LOCATION_EMAIL_ID]
				<br/>
				Work Location ECC No. 1 [WORK_LOCATION_ECC_NO1]
				<br/>
				Work Location ECC No. 2 [WORK_LOCATION_ECC_NO2]
				<br/>
				Work Locations- in Legal Entity  [WORK_LOCATIONS_IN_LEGAL_ENTITY]
				<br/>
				Project Owner Name [PROJECT_OWNER_NAME]
				<br/>
				Project Owner Designation [PROJECT_OWNER_DESIG]
				<br/>
				Project Owner Contact No. [PROJECT_OWNER_CONTACT_NO]
				<br/>
				Project Owner e-mail ID [PROJECT_OWNER_EMAIL_ID]
				<br/>
				HR [HR]
				<br/>
				HR Contact No. [HR_CONTACT_NO]
				<br/>
				HR e-mail ID [HR_EMAIL_ID]
				<br/>
				Customer Name [CUSTOMER_NAME]
				<br/>
				Customer Address [CUSTOMER_ADDRESS]
				<br/>
				Customer SPOC [CUSTOMER_SPOC]
				<br/>
				Customer Contact No. [CUSTOMER_CONTACT_NO]
				<br/>
				Customer Fax No. [CUSTOMER_FAX_NO]
				<br/>
				Customer e-mail ID [CUSTOMER_EMAIL_ID]
				<br/>
				Bank Name [BANK_NAME]
				<br/>
				Branch Name [BRANCH_NAME]
				<br/>
				Branch Address [BRANCH_ADDRESS]
				<br/>
				Branch Contact No. [BRANCH_CONTACT_NO]
				<br/>
				Branch Fax No. [BRANCH_FAX_NO]
				<br/>
				Branch e-mail ID [BRANCH_EMAIL_ID]
				<br/>
				Branch A/c no. [BRANCH_AC_NO]
				<br/>
				Branch IFSC Code [BRANCH_IFSC_CODE]
				<br/>
				Branch Swift Code [BRANCH_SWIFT_CODE]
				<br/>
				Currency (Project) [PROJECT_CURRENCY]
				<br/>
				Invoice No. (Auto) [INVOICE_NO]
				<br/>
				Invoice Generation Date (Auto) [INVOICE_GENERATION_DATE]
				<br/>
			</div>
			
		
	</s:form>

</div>



<g:compress>
<script>
			
			// Replace the <textarea id="editor1"> with an CKEditor instance.
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
			
			// Replace the <textarea id="editor2"> with an CKEditor instance.
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
			
			// Replace the <textarea id="editor3"> with an CKEditor instance.
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
			
			// Replace the <textarea id="editor4"> with an CKEditor instance.
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
			
			// Replace the <textarea id="editor5"> with an CKEditor instance.
			CKEDITOR.replace( 'editor5', {
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
			
			// Replace the <textarea id="editor6"> with an CKEditor instance.
			CKEDITOR.replace( 'editor6', {
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
			
			// Replace the <textarea id="editor7"> with an CKEditor instance.
			CKEDITOR.replace( 'editor7', {
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
				},
				width: 300
			});
			
			// Replace the <textarea id="editor8"> with an CKEditor instance.
			CKEDITOR.replace( 'editor8', {
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
				},
				width: 300
			});
			
			// Replace the <textarea id="editor9"> with an CKEditor instance.
			CKEDITOR.replace( 'editor9', {
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
				},
				width: 300
			});
			
			// Replace the <textarea id="editor10"> with an CKEditor instance.
			CKEDITOR.replace( 'editor10', {
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
				},
				width: 300
			});
			
			// Replace the <textarea id="editor11"> with an CKEditor instance.
			CKEDITOR.replace( 'editor11', {
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
				},
				width: 612
			});
			
			</script>
</g:compress> 